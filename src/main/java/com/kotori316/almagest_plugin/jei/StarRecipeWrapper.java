package com.kotori316.almagest_plugin.jei;

import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import noki.almagest.recipe.StarRecipe;

import com.kotori316.almagest_plugin.recipes.IngredientHelper;

import static com.kotori316.almagest_plugin.jei.StarRecipeCategory.in_x;
import static com.kotori316.almagest_plugin.jei.StarRecipeCategory.in_y;
import static com.kotori316.almagest_plugin.jei.StarRecipeCategory.onebox;
import static jp.t2v.lab.syntax.MapStreamSyntax.keys;
import static jp.t2v.lab.syntax.MapStreamSyntax.toAny;

public class StarRecipeWrapper implements IRecipeWrapper {

    private final IJeiHelpers helpers;
    private final StarRecipe recipe;
    public final int maxCount;
    public final List<NameLevel> attributes;
    public final List<Ingredient> items;

    public StarRecipeWrapper(IJeiHelpers helpers, StarRecipe recipe) {
        this.helpers = helpers;
        this.recipe = recipe;
        maxCount = recipe.getMaxStack();
        attributes = recipe.getAttribute().entrySet().stream().map(keys(Enum::name)).map(toAny(NameLevel::new)).collect(Collectors.toList());
        items = IngredientHelper.ingredientList(recipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
        ingredients.setInputLists(ItemStack.class, helpers.getStackHelper().expandRecipeItemStackInputs(items));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        for (int i = 0; i < attributes.size(); i++) {
            StarRecipeWrapper.NameLevel level = attributes.get(i);
            minecraft.fontRenderer.drawString(level.localized() + " : " + level.level, in_x + onebox * 3 + 1, in_y + i * 10, 0x404040);
        }
        int max = maxCount - items.size();
        if (max > 0)
            minecraft.fontRenderer.drawString("Attributes : " + max, in_x + onebox * 3 + 1, in_y + onebox * 3 - 10, 0x404040);
    }

    public static class NameLevel {
        public final String name;
        public final int level;

        public NameLevel(String name, Integer level) {
            this.name = name;
            this.level = level;
        }

        @Override
        public String toString() {
            return name + " @" + level;
        }

        public String localized() {
            return I18n.format(name);
        }
    }
}
