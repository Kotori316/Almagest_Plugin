package com.kotori316.almagest_plugin.jei;

import java.util.stream.Collectors;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noki.almagest.recipe.StarRecipe;
import noki.almagest.registry.ModBlocks;

@JEIPlugin
public class AlmagestJeiPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(StarRecipe.class, recipe -> new StarRecipeWrapper(registry.getJeiHelpers(), recipe), StarRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BOOKREST), StarRecipeCategory.UID);
        registry.addRecipes(
            ForgeRegistries.RECIPES.getValuesCollection().stream()
                .filter(StarRecipe.class::isInstance)
                .map(StarRecipe.class::cast)
//                .filter(starRecipe -> !starRecipe.isSpecial())
                .collect(Collectors.toList()),
            StarRecipeCategory.UID
        );
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new StarRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

}
