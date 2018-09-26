package com.kotori316.almagest_plugin.recipes;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.ForgeRegistry;
import noki.almagest.recipe.StarRecipe;
import noki.almagest.registry.ModItems;

public class ItemHoneyRecipe extends StarRecipe {

    public ItemHoneyRecipe(Item honey) {
        super(new ItemStack(honey));
        setSpecial(true);
        setMaxStack(4);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int flowerCount = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack selectedStack = inv.getStackInSlot(i);
            if (selectedStack.isEmpty()) {
                continue;
            }
            if (this.isFlower(selectedStack)) {
                flowerCount++;
            } else {
                return false;
            }
        }

        return flowerCount == 4;
    }

    private boolean isFlower(ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        return block == Blocks.RED_FLOWER || block == Blocks.YELLOW_FLOWER;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public List<Ingredient> ingredients() {
        class FlowerIngredient extends Ingredient {
            FlowerIngredient() {
                super(new ItemStack(Blocks.RED_FLOWER, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(Blocks.YELLOW_FLOWER, 1, OreDictionary.WILDCARD_VALUE));
            }
        }
        FlowerIngredient ingredient = new FlowerIngredient();
        return Arrays.asList(ingredient, ingredient, ingredient, ingredient);
    }

    public static void init() {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(IngredientHelper.Almaget_modid, ModItems.HONEY_name));
        if (item != null) {
            ((ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES).remove(new ResourceLocation(IngredientHelper.Almaget_modid, ModItems.HONEY_name));
            ForgeRegistries.RECIPES.register(new ItemHoneyRecipe(item));
        }
    }
}
