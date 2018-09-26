package com.kotori316.almagest_plugin.mods;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import noki.almagest.event.post.AttributeLevelEvent;
import noki.almagest.event.post.MemoryEvent;

import com.kotori316.almagest_plugin.Almagest_Plugin;

public class Ores {

    public static void init() {
        Trees.class.getName();
    }

    @Mod.EventBusSubscriber(modid = Almagest_Plugin.modID)
    public static class Trees {
        private static final Pattern FLOWER_PATTERN = Pattern.compile("flower[A-Z][a-zA-Z]+");
        private static final Pattern PLANT_PATTERN = Pattern.compile("plant[A-Z][a-zA-Z]+");

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void event(AttributeLevelEvent event) {
            if (event.getLevel() > 0 || event.getStack().isEmpty()) return;
            int[] oreIDs = OreDictionary.getOreIDs(event.getStack());
            if (oreIDs.length == 0) return;
            Set<String> names = Arrays.stream(oreIDs).mapToObj(OreDictionary::getOreName).collect(Collectors.toSet());
            switch (event.getAttribute()) {
                case PLANT:
                    if (names.contains("treeLeaves") || names.contains("stickWood"))
                        event.setLevel(20);
                    else if (names.contains("treeSapling") || names.stream().map(PLANT_PATTERN::matcher).anyMatch(Matcher::matches))
                        event.setLevel(10);
                    else if (names.stream().map(FLOWER_PATTERN::matcher).anyMatch(Matcher::matches))
                        event.setLevel(10);
                    break;
                case WOOD:
                    if (names.contains("logWood"))
                        event.setLevel(40);
                    else if (names.contains("plankWood") || names.contains("stairWood"))
                        event.setLevel(20);
                    else if (names.contains("slabWood") || names.contains("fenceWood") || names.contains("fenceGateWood"))
                        event.setLevel(10);
                    break;
                case DECORATIVE:
                    if (names.contains("fenceGateWood"))
                        event.setLevel(30);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void event(MemoryEvent event) {
            if (event.getMemory() > 0 || event.getStack().isEmpty()) return;
            int[] oreIDs = OreDictionary.getOreIDs(event.getStack());
            if (oreIDs.length == 0) return;
            Set<String> names = Arrays.stream(oreIDs).mapToObj(OreDictionary::getOreName).collect(Collectors.toSet());
            if (names.contains("treeSapling") || names.contains("logWood"))
                event.setMemory(75);
            else if (names.contains("treeLeaves"))
                event.setMemory(50);
            else if (names.stream().map(FLOWER_PATTERN::matcher).anyMatch(Matcher::matches))
                event.setMemory(65);
            else if(names.stream().map(PLANT_PATTERN::matcher).anyMatch(Matcher::matches))
                event.setMemory(60);
        }
    }
}
