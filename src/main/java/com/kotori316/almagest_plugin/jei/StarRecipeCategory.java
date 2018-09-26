package com.kotori316.almagest_plugin.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

import com.kotori316.almagest_plugin.Almagest_Plugin;

/*
    Gui : 0, 0 -> 163, 79
    FileName : textures/gui/jei.png
    In : 11, 13
    Out : 135, 49
    Attribute String : 82, 32
 */
public class StarRecipeCategory implements IRecipeCategory<StarRecipeWrapper> {
    static final int onebox = 18;
    static final int in_x = 11;
    static final int in_y = 13;
    private static final int out_x = 135;
    private static final int out_y = 49;
    private static final int WIDTH = 163;
    private static final int HEIGHT = 79;

    public static final String UID = Almagest_Plugin.modID + ":jeirecipe";
    private static final ResourceLocation LOCATION = new ResourceLocation(Almagest_Plugin.modID, "textures/gui/jei.png");
    private final IGuiHelper helper;

    public StarRecipeCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return "The Almagest";
    }

    @Override
    public String getModName() {
        return "The Almagest";
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(LOCATION, 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, StarRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup gui = recipeLayout.getItemStacks();
        for (int i = 0; i < recipeWrapper.items.size(); i++) {
            int xPos;
            int yPos;
            if (i < 3) {
                xPos = in_x + onebox * i;
                yPos = in_y;
            } else if (i < 6) {
                xPos = in_x + onebox * (i - 3);
                yPos = in_y + onebox;
            } else {
                xPos = in_x + onebox * (i - 6);
                yPos = in_y + onebox * 2;
            }
            gui.init(i, true, xPos, yPos);
        }
        gui.init(recipeWrapper.items.size(), false, out_x, out_y);
        gui.set(ingredients);
    }
}
