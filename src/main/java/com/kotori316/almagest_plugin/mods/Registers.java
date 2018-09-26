package com.kotori316.almagest_plugin.mods;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import com.kotori316.almagest_plugin.Almagest_Plugin;

import static jp.t2v.lab.syntax.MapStreamSyntax.entry;

public class Registers {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)-(\\d+)");

    public static void addAttributes() {
        ModContainer container = FMLCommonHandler.instance().findContainerFor(Almagest_Plugin.getInstance());
        if (container.getSource().isDirectory()) {
            Path attributePath = container.getSource().toPath().resolve("assets/" + Almagest_Plugin.modID + "/attributes");
            searchJson(attributePath);
        } else if (container.getSource().getName().endsWith(".jar")) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(container.getSource().toPath(), null)) {
                Path attributePath = fileSystem.getPath("assets/" + Almagest_Plugin.modID + "/attributes");
                searchJson(attributePath);
            } catch (IOException e) {
                Almagest_Plugin.LOGGER.error(Registers.class.getSimpleName(), e);
            }
        }
        //Code base attributes
        Stream.of(IC2.getInstance()).filter(IModRegister::isValid).forEach(IModRegister::apply);
    }

    private static void searchJson(Path attributePath) {
        //File base attribute
        try {
            JsonContext ctx = new JsonContext(Almagest_Plugin.modID);
            Files.walk(attributePath).forEach(path -> {
                if (Files.isDirectory(path))
                    return;
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
                    if (json.has("conditions") && !CraftingHelper.processConditions(JsonUtils.getJsonArray(json, "conditions"), ctx)) {
                        return;
                    }
                    ResourceLocation name = new ResourceLocation(JsonUtils.getString(json, "name"));
                    Item item = ForgeRegistries.ITEMS.getValue(name);
                    if (item != null) {
                        JsonObject attributes = JsonUtils.getJsonObject(json, "attributes");
                        attributes.entrySet().forEach(entry((s, jsonElement) -> {
                            if (StringUtils.isNumeric(s)) {
                                int meta = Integer.valueOf(s);
                                IModRegister.register(name.getResourceDomain(), new ItemStack(item, 1, meta),
                                    IModRegister.Attributes.pauseJson(jsonElement.getAsJsonObject()));
                            } else {
                                Matcher matcher = RANGE_PATTERN.matcher(s);
                                if (matcher.matches()) {
                                    int meta1 = Integer.valueOf(matcher.group(1));
                                    int meta2 = Integer.valueOf(matcher.group(2));
                                    for (int i = meta1; i <= meta2; i++) {
                                        IModRegister.register(name.getResourceDomain(), new ItemStack(item, 1, i),
                                            IModRegister.Attributes.pauseJson(jsonElement.getAsJsonObject()));
                                    }
                                }
                            }
                        }));
                    }
                } catch (IOException | IllegalArgumentException e) {
                    Almagest_Plugin.LOGGER.warn(path.toString(), e);
                }
            });
        } catch (IOException e) {
            Almagest_Plugin.LOGGER.error(Registers.class.getSimpleName(), e);
        }
    }
}
