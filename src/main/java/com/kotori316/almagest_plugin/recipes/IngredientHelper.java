package com.kotori316.almagest_plugin.recipes;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import noki.almagest.recipe.StarRecipe;

import static com.kotori316.almagest_plugin.Almagest_Plugin.NON_EMPTY;

public class IngredientHelper {
    static final String Almaget_modid = "almagest";

    public static List<Ingredient> ingredientList(StarRecipe recipe) {
        if (recipe instanceof BookRestRecipe) {
            return ((BookRestRecipe) recipe).ingredients();
        }
        if (recipe instanceof ItemHoneyRecipe) {
            return ((ItemHoneyRecipe) recipe).ingredients();
        }
        return recipe.getStack().stream().map(ItemStack::copy).filter(NON_EMPTY).map(Ingredient::fromStacks).collect(Collectors.toList());
    }

    public static void initRecipes() {
        BookRestRecipe.init();
        ItemHoneyRecipe.init();
    }
}
