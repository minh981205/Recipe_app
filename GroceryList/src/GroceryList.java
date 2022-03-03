
/*
Comp 3350 Software Engineering
Group 8
February 28, 2022
GroceryList.java

Private member:
groceryList - Linked list of the ingredients.

Public method:
GroceryList() - Constructor to initialize the linked list.
addIngredient() - Add the given string ingredient to the list.
removeIngredient() - Remove the given index of the ingredient from the list.
getIngredient() - Get the ingredient from a given index.
numIngredients() - Number of ingredients in the grocery list.
*/

import java.util.LinkedList;

public class GroceryList
{
    // Private member
    private final LinkedList<String> groceryList;

    // Public method

    /* GroceryList()
    */
    public GroceryList()
    {
        groceryList = new LinkedList<String>();
    }

    /* addIngredient()
    Parameter:
    newIngredient - The given ingredient string to append to the grocery list.
    */
    public void addIngredient(String newIngredient)
    {
        if (newIngredient != null)
        {
            groceryList.add(newIngredient);
        }
    }

    /* removeIngredient()
    Parameter:
    index - Remove the ingredient at given index. Do nothing if element at given
        index does not exist.
    */
    public void removeIngredient(int index)
    {
        // Pre-condition
        assert(index >= 0 && index < this.groceryList.size());

        // Remove the ingredient at given index
        if(index < groceryList.size() && index >= 0)
        {
            groceryList.remove(index);
        }
    }

    /* getIngredient()
    Parameter:
    index - The index of the ingredient in the grocery list.

    Return:
    String ingredient at given index in the grocery list.
    Null if the given index is invalid.
    */
    public String getIngredient(int index)
    {
        // Pre-condition
        assert(index >= 0 && index < this.groceryList.size());

        // Get the ingredient string from the grocery list
        String returnIngredient = null;
        if (index < groceryList.size() && index >= 0)
        {
            returnIngredient = groceryList.get(index);
        }

        // Return
        return returnIngredient;
    }

    public int numIngredients()
    {
        return this.groceryList.size();
    }

}
