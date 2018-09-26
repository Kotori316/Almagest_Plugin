package com.kotori316.almagest_plugin;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kotori316.almagest_plugin.debug.AttributeDump;
import com.kotori316.almagest_plugin.mods.Ores;
import com.kotori316.almagest_plugin.mods.Registers;
import com.kotori316.almagest_plugin.recipes.IngredientHelper;

@Mod(name = Almagest_Plugin.MOD_NAME, modid = Almagest_Plugin.modID, version = "${version}")
public class Almagest_Plugin {

    public static final Almagest_Plugin instance;
    public static final String MOD_NAME = "Almagest_Plugin";
    public static final String modID = "almagest_plugin";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    static {
        instance = new Almagest_Plugin();
        MinecraftForge.EVENT_BUS.register(instance);
    }

    public static final Predicate<ItemStack> NON_EMPTY = ((Predicate<ItemStack>) ItemStack::isEmpty).negate();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Registers.addAttributes();
        IngredientHelper.initRecipes();
        Ores.init();
    }

    @Mod.EventHandler
    public void complete(FMLLoadCompleteEvent event) {
        if ((Boolean) Launch.blackboard.getOrDefault("fml.deobfuscatedEnvironment", Boolean.FALSE)) {
            AttributeDump.outputAbilities();
            Loader.instance().getActiveModList().stream().map(ModContainer::getModId).forEach(AttributeDump::outputAttributes);
        }
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {

    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {

    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {

    }

    @Mod.InstanceFactory
    public static Almagest_Plugin getInstance() {
        return instance;
    }
}
