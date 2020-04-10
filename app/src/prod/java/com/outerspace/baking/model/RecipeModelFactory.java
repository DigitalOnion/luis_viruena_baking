package com.outerspace.baking.model;

public final class RecipeModelFactory {
    private static RecipeModelImpl instance;

    public static IRecipeModel getInstance() {
        if(instance == null) {
            instance = new RecipeModelImpl();
        }
        return  instance;
    }
}
