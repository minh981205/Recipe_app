package persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import objects.Recipe;
import persistence.RecipePersistence;

public class RecipePersistenceHSQLDB implements RecipePersistence {

    private final String dbPath;

    public RecipePersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    private Recipe createRecipe(ResultSet rs, ArrayList<String> ingredientsList, ArrayList<String> categoryList) throws SQLException {
        final int recipeID = rs.getInt("recipeID");
        final String recipeName = rs.getString("name");
        final String recipeNationality = rs.getString("nationality");
        final int prepTime = rs.getInt("preptime");
        final int cookTime = rs.getInt("cooktime");
        final String cookingSkillLevel = rs.getString("cookingskilllevel");
        final String description = rs.getString("description");
        final String instruction = rs.getString("instruction");
        final String link = rs.getString("link");

        return new Recipe(recipeID, recipeName, recipeNationality, ingredientsList, prepTime, cookTime, cookingSkillLevel, description, instruction, link, categoryList);
    }

    private ArrayList<String> getListForRecipeFromDB(Connection c, int recipeID, String statement, String target) throws SQLException {
        PreparedStatement theStatement = c.prepareStatement(statement);
        theStatement.setInt(1, recipeID);

        ResultSet theRS = theStatement.executeQuery();
        ArrayList<String> theList = new ArrayList();
        while(theRS.next()){
            String thing = theRS.getString(target);
            theList.add(thing);
        }
        theRS.close();
        theStatement.close();
        return theList;
    }

    @Override
    public List<Recipe> getRecipes() {
        final List<Recipe> recipes = new ArrayList<>();
        try (final Connection c = connection()) {
            final Statement st = c.createStatement();
            final ResultSet rs = st.executeQuery("SELECT * FROM RECIPE");
            while (rs.next()) {
                int recipeID = rs.getInt("RECIPEID");
                ArrayList<String> ingredientsList = getListForRecipeFromDB(c, recipeID, "SELECT * FROM INGREDIENTS WHERE RECIPEID=?", "ingredient");
                ArrayList<String> categoryList = getListForRecipeFromDB(c, recipeID, "SELECT * FROM CATEGORIES WHERE RECIPEID=?", "category");
                final Recipe recipe = createRecipe(rs, ingredientsList, categoryList);
                recipes.add(recipe);
            }
            rs.close();
            st.close();

            return recipes;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    private void insertSmallTable(Connection c, String tableName, int recipeID, ArrayList<String> theList) throws SQLException {
        for (int i = 0; i < theList.size(); i++) {
            PreparedStatement st2 = c.prepareStatement("INSERT INTO ? VALUES(?, ?)");
            st2.setString(1, tableName);
            st2.setInt(2, recipeID);
            st2.setString(3, theList.get(i));
            st2.executeUpdate();
            st2.close();
        }
    }

    private void updateSmallTable(Connection c, String tableName, int recipeID, ArrayList<String> theList) throws SQLException {
        final PreparedStatement st = c.prepareStatement("DELETE FROM ? WHERE recipeID = ?");
        st.setString(1, tableName);
        st.setInt(2, recipeID);
        st.executeUpdate();
        st.close();

        insertSmallTable(c, tableName, recipeID, theList);
    }

    @Override
    public Recipe insertRecipe(Recipe currentRecipe) {
        try (final Connection c = connection()) {
            int recipeID = currentRecipe.getRecipeID();
            PreparedStatement st = c.prepareStatement("INSERT INTO RECIPE VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            st.setInt(1, recipeID);
            st.setString(2, currentRecipe.getName());
            st.setString(3, currentRecipe.getNationality());
            st.setInt(4, currentRecipe.getPrepTime());
            st.setInt(5, currentRecipe.getCookTime());
            st.setString(6, currentRecipe.getCookingSkillLevel());
            st.setString(7, currentRecipe.getDescription());
            st.setString(8, currentRecipe.getInstructions());
            st.setString(9, currentRecipe.getLink());
            st.executeUpdate();
            st.close();

            ArrayList<String> ingredientList = currentRecipe.getIngredientList();
            insertSmallTable(c, "INGREDIENTS", recipeID, ingredientList);

            ArrayList<String> categoryList = currentRecipe.getCategoryList();
            insertSmallTable(c, "CATEGORIES", recipeID, categoryList);

            return currentRecipe;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Recipe updateRecipe(Recipe currentRecipe) {
        try (final Connection c = connection()) {
            int recipeID = currentRecipe.getRecipeID();
            final PreparedStatement st = c.prepareStatement("UPDATE RECIPE SET name=?, nationality=?, prepTime=?, cookTime=?, cookingSkillLevel=?, description=?, instruction=?, link=?, WHERE recipeID=?");
            st.setString(1, currentRecipe.getName());
            st.setString(2, currentRecipe.getNationality());
            st.setInt(3, currentRecipe.getPrepTime());
            st.setInt(4, currentRecipe.getCookTime());
            st.setString(5, currentRecipe.getCookingSkillLevel());
            st.setString(6, currentRecipe.getDescription());
            st.setString(7, currentRecipe.getInstructions());
            st.setString(8, currentRecipe.getLink());
            st.executeUpdate();
            st.close();

            ArrayList<String> ingredientList = currentRecipe.getIngredientList();
            updateSmallTable(c, "INGREDIENTS", recipeID, ingredientList);

            ArrayList<String> categoryList = currentRecipe.getCategoryList();
            updateSmallTable(c, "CATEGORIES", recipeID, categoryList);

            return currentRecipe;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void deleteRecipe(Recipe currentRecipe) {
        try (final Connection c = connection()) {
            final PreparedStatement sc = c.prepareStatement("DELETE FROM RECIPE WHERE recipeID = ?");
            sc.setInt(1, currentRecipe.getRecipeID());
            sc.executeUpdate();
            final PreparedStatement st = c.prepareStatement("DELETE FROM INGREDIENTS WHERE recipeID = ?");
            st.setInt(1, currentRecipe.getRecipeID());
            st.executeUpdate();
            final PreparedStatement st2 = c.prepareStatement("DELETE FROM CATEGORIES WHERE recipeID = ?");
            st2.setInt(1, currentRecipe.getRecipeID());
            st2.executeUpdate();
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }
}
