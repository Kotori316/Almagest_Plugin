package com.kotori316.almagest_plugin.debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jp.t2v.lab.syntax.MapStreamSyntax;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noki.almagest.ability.StarAbility;
import noki.almagest.ability.StarAbilityCreator;
import noki.almagest.attribute.AttributeHelper;

import com.kotori316.almagest_plugin.Almagest_Plugin;

import static jp.t2v.lab.syntax.MapStreamSyntax.byKey;
import static jp.t2v.lab.syntax.MapStreamSyntax.byValue;
import static jp.t2v.lab.syntax.MapStreamSyntax.toAny;

@SuppressWarnings("unchecked")
public class AttributeDump {

    /**
     * Called After postInit.
     */
    public static void outputAttributes(String modid) {
        List<String> list = ForgeRegistries.ITEMS.getEntries().stream()
            .filter(byKey(name -> name.getResourceDomain().equals(modid)))
            .map(Map.Entry::getValue)
            .flatMap(item -> {
                NonNullList<ItemStack> stacks = NonNullList.create();
                if (item.getCreativeTab() == null)
                    for (CreativeTabs tabs : CreativeTabs.CREATIVE_TAB_ARRAY) {
                        item.getSubItems(tabs, stacks);
                    }
                else {
                    item.getSubItems(item.getCreativeTab(), stacks);
                }
                return stacks.stream().filter(Almagest_Plugin.NON_EMPTY);
            }).map(MapStreamSyntax.toEntry(Function.identity(), AttributeHelper::getVanillaInfoSet))
            .filter(byValue(Objects::nonNull))
            .map(toAny((itemSet, infoSet) -> {
                String name = itemSet.toString();
                String value = infoSet.toString();
                return name + value;
            })).collect(Collectors.toList());

        if (!list.isEmpty())
            try {
                Path path = Paths.get(Almagest_Plugin.modID, modid + ".txt");
                if (Files.notExists(path))
                    Files.createFile(path);
                Files.write(path, list);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void outputAbilities() {
        HashMap<Integer, StarAbility> map = StarAbilityCreator.starAbilities;
        List<String> list = map.entrySet().stream().map(toAny((i, starAbility) ->
            String.format("id : %d, name : %s, max : %d, Level0 : %s", i, starAbility.getClass().getSimpleName(), starAbility.getMaxLevel(),
                new TextComponentTranslation(starAbility.getName(0)).getFormattedText()))).collect(Collectors.toList());
        try {
            Path path = Paths.get(Almagest_Plugin.modID, "Abilities" + ".txt");
            if (Files.notExists(path)) {
                if (Files.notExists(Paths.get(Almagest_Plugin.modID)))
                    Files.createDirectory(Paths.get(Almagest_Plugin.modID));
                Files.createFile(path);
            }
            Files.write(path, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
