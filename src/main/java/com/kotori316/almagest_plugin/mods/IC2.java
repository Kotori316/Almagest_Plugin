package com.kotori316.almagest_plugin.mods;

import java.util.stream.IntStream;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static noki.almagest.attribute.EStarAttribute.METAL;

public class IC2 implements IModRegister {
    private static final IC2 ourInstance = new IC2();
    public static final String IC2_ID = "ic2";

    public static IC2 getInstance() {
        return ourInstance;
    }

    private IC2() {
    }

    @Override
    public boolean isValid() {
        return Loader.isModLoaded(IC2_ID);
    }

    @Override
    public void apply() {
        Item ingot = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ic2:ingot"));
        IModRegister.register(IC2_ID, new ItemStack(ingot, 1, 0), (new Attributes()).set(METAL, 60));
        IntStream.rangeClosed(1, 6).forEach(i -> IModRegister.register(IC2_ID, new ItemStack(ingot, 1, i), (new Attributes()).set(METAL, 40)));
    }
}
