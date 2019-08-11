package roito.afterthedrizzle.api.recipe;

import roito.silveroakoutpost.recipe.ISingleInRecipe;
import roito.silveroakoutpost.recipe.SingleInRecipeManager;

import java.util.ArrayList;
import java.util.Collection;

public class BasketIndoorsManager extends SingleInRecipeManager
{
    private static final ArrayList<ISingleInRecipe> RECIPES = new ArrayList<>();

    @Override
    public Collection<ISingleInRecipe> getRecipes()
    {
        return RECIPES;
    }
}