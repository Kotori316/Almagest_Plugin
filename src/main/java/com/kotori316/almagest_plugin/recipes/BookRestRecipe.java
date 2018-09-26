package com.kotori316.almagest_plugin.recipes;

import java.util.Arrays;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.ForgeRegistry;
import noki.almagest.attribute.AttributeHelper;
import noki.almagest.attribute.EStarAttribute;
import noki.almagest.recipe.StarRecipe;
import noki.almagest.registry.ModBlocks;

import com.kotori316.almagest_plugin.Almagest_Plugin;

public final class BookRestRecipe extends StarRecipe {

    public static final String PLANK_ORENAME = "plankWood";

    public BookRestRecipe() {
        super(new ItemStack(ModBlocks.BOOKREST));
        setRegistryName(new ResourceLocation(Almagest_Plugin.modID, ModBlocks.BOOKREST_name));
        setSpecial(true);
        setAttribute(EStarAttribute.STAR, 10);
        setMaxStack(4);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int plankCount = 0;
        int attributeSum = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack selectedStack = inv.getStackInSlot(i);
            if (selectedStack.isEmpty()) {
                continue;
            }

            boolean plankFlag = false;
            boolean attributeFlag = false;

            if (this.isPlank(selectedStack)) {
                plankCount++;
                plankFlag = true;
            }
            int level = AttributeHelper.getAttrributeLevel(selectedStack, EStarAttribute.STAR);
            attributeSum += level;
            if (level != 0) {
                attributeFlag = true;
            }

            if (!plankFlag && !attributeFlag) {
                return false;
            }
        }

        return plankCount == 2 && attributeSum >= 10;
    }

    private boolean isPlank(ItemStack stack) {
        return Arrays.stream(OreDictionary.getOreIDs(stack)).mapToObj(OreDictionary::getOreName).anyMatch(PLANK_ORENAME::equals);
    }

    public List<Ingredient> ingredients() {
        return Arrays.asList(new OreIngredient(PLANK_ORENAME), new OreIngredient(PLANK_ORENAME));
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    /**
     * Called in post init.
     */
    public static void init() {
        ((ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES).remove(new ResourceLocation(IngredientHelper.Almaget_modid, ModBlocks.BOOKREST_name));
        ForgeRegistries.RECIPES.register(new BookRestRecipe());
    }
}
