package com.kotori316.almagest_plugin.mods;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import noki.almagest.ability.StarAbilityCreator;
import noki.almagest.ability.StarPropertyCreator;
import noki.almagest.attribute.AttributeHelper;
import noki.almagest.attribute.EStarAttribute;
import noki.almagest.event.post.AttributeLevelEvent;
import noki.almagest.event.post.MemoryEvent;
import org.apache.commons.lang3.StringUtils;

import com.kotori316.almagest_plugin.Almagest_Plugin;

import static jp.t2v.lab.syntax.MapStreamSyntax.entry;
import static jp.t2v.lab.syntax.MapStreamSyntax.entryToMap;
import static jp.t2v.lab.syntax.MapStreamSyntax.keys;
import static jp.t2v.lab.syntax.MapStreamSyntax.toEntry;

public interface IModRegister {

    boolean isValid();

    void apply();

    static void register(String modid, ItemStack stack, Attributes attributes) {
        if (!stack.isEmpty())
            Holder.register(modid, stack.getItem(), stack.getItemDamage(), attributes);
    }

    @SuppressWarnings("UnusedReturnValue")
    class Attributes {

        private AttributeHelper.InfoSet infoSet = new AttributeHelper.InfoSet();

        AttributeHelper.InfoSet toInfoSet() {
            return infoSet;
        }

        public Attributes set(EStarAttribute attribute, int level) {
            infoSet.set(attribute, level);
            return this;
        }

        public Attributes set(int abilityId, int level) {
            infoSet.set(abilityId, level);
            return this;
        }

        public Attributes set(int memory) {
            infoSet.set(memory);
            return this;
        }

        public Attributes set(StarPropertyCreator.ItemStarLine line) {
            infoSet.set(line);
            return this;
        }

        private static final Map<String, EStarAttribute> STAR_ATTRIBUTES =
            Arrays.stream(EStarAttribute.values())
                .map(toEntry(Enum::name, Function.identity()))
                .map(keys(String::toLowerCase))
                .collect(entryToMap());

        private static final Map<String, StarPropertyCreator.ItemStarLine> STAR_LINE =
            Arrays.stream(StarPropertyCreator.ItemStarLine.values())
                .map(toEntry(Enum::name, Function.identity()))
                .map(keys(String::toLowerCase))
                .collect(entryToMap());

        static Attributes pauseJson(JsonObject object) {
            Attributes attributes = new Attributes();
            object.entrySet().forEach(entry((s, element) -> {
                String low = s.toLowerCase();
                if (low.startsWith("_")) // To write some comment.
                    return;
                if (STAR_ATTRIBUTES.containsKey(low)) {
                    attributes.set(STAR_ATTRIBUTES.get(low), element.getAsInt());
                } else if ("memory".equals(low)) {
                    attributes.set(element.getAsInt());
                } else if (STAR_LINE.containsKey(low)) {
                    if (element.getAsBoolean()) {
                        attributes.set(STAR_LINE.get(low));
                    }
                } else if (StringUtils.isNumeric(low)) {
                    attributes.set(Integer.valueOf(low), element.getAsInt());
                } else {
                    throw new IllegalArgumentException("Wrong element : " + low + " for IModRegister.Attributes#pauseJson");
                }
            }));
            return attributes;
        }
    }

    @Mod.EventBusSubscriber(modid = Almagest_Plugin.modID)
    class Holder {
        private static final Map<String, Map<AttributeHelper.ItemSet, AttributeHelper.InfoSet>> MAP = new HashMap<>();

        static void register(String modid, Item item, int meta, Attributes attributes) {
            Map<AttributeHelper.ItemSet, AttributeHelper.InfoSet> map = MAP.computeIfAbsent(modid, s -> new HashMap<>());
            map.put(new AttributeHelper.ItemSet(item, meta), attributes.toInfoSet());
        }

        @SubscribeEvent
        static void event(AttributeLevelEvent event) {
            ItemStack stack = event.getStack();
            if (event.getLevel() > 0 || stack.isEmpty())
                return;
            String domain = stack.getItem().getRegistryName().getResourceDomain();
            event.setLevel(
                Optional.ofNullable(MAP.get(domain))
                    .map(m -> m.get(new AttributeHelper.ItemSet(stack.getItem(), stack.getItemDamage())))
                    .map(infoSet -> infoSet.getAttribute(event.getAttribute())).orElse(0));
        }

        @SubscribeEvent
        static void event(MemoryEvent event) {
            ItemStack stack = event.getStack();
            if (event.getMemory() > 0 || stack.isEmpty()) return;
            String domain = stack.getItem().getRegistryName().getResourceDomain();
            event.setMemory(
                Optional.ofNullable(MAP.get(domain))
                    .map(m -> m.get(new AttributeHelper.ItemSet(stack.getItem(), stack.getItemDamage())))
                    .map(AttributeHelper.InfoSet::getMemory).orElse(0)
            );
        }

        @SubscribeEvent
        static void dropEvent(BlockEvent.HarvestDropsEvent event) {
            for (int i = 0; i < event.getDrops().size(); i++) {
                ItemStack stack = event.getDrops().get(i);
                if (StarAbilityCreator.getAbility2(stack).isEmpty()) {
                    Map<AttributeHelper.ItemSet, AttributeHelper.InfoSet> map = MAP.get(stack.getItem().getRegistryName().getResourceDomain());
                    if (map != null) {
                        AttributeHelper.InfoSet set = map.get(new AttributeHelper.ItemSet(stack.getItem(), stack.getItemDamage()));
                        if (set != null) {
                            set.getAbilities().forEach((id, levels) -> levels.forEach(level -> StarAbilityCreator.addAbility2(stack, id, level)));
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        static void craftEvent(PlayerEvent.ItemCraftedEvent event) {
            ItemStack stack = event.crafting;
            if (StarAbilityCreator.getAbility2(stack).isEmpty()) {
                Map<AttributeHelper.ItemSet, AttributeHelper.InfoSet> map = MAP.get(stack.getItem().getRegistryName().getResourceDomain());
                if (map != null) {
                    AttributeHelper.InfoSet set = map.get(new AttributeHelper.ItemSet(stack.getItem(), stack.getItemDamage()));
                    set.getAbilities().forEach((id, levels) -> levels.forEach(level -> StarAbilityCreator.addAbility2(stack, id, level)));
                }
            }
        }
    }
}
