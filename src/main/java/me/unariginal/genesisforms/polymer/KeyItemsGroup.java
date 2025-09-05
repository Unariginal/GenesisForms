package me.unariginal.genesisforms.polymer;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.blocks.PossessionBlock;
import me.unariginal.genesisforms.config.items.accessories.AccessoriesConfig;
import me.unariginal.genesisforms.config.items.keyitems.FusionItemsConfig;
import me.unariginal.genesisforms.config.items.keyitems.KeyFormItemsConfig;
import me.unariginal.genesisforms.config.items.keyitems.PossessionItemsConfig;
import me.unariginal.genesisforms.data.CycledFormSetting;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.items.BasePolymerItem;
import me.unariginal.genesisforms.items.keyitems.*;
import me.unariginal.genesisforms.items.keyitems.accessories.DynamaxAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.MegaAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.TeraAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.ZAccessory;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.*;

public class KeyItemsGroup {
    private static final Item.Settings itemSettings = new Item.Settings().maxCount(1).rarity(Rarity.EPIC);
    private static final Item baseVanillaItem = Items.DIAMOND;

    public static LinkedHashMap<String, ItemStack> allKeyItems = new LinkedHashMap<>();

    public static LinkedHashMap<String, MegaAccessory> megaAccessories = new LinkedHashMap<>();
    public static LinkedHashMap<String, TeraAccessory> teraAccessories = new LinkedHashMap<>();
    public static LinkedHashMap<String, ZAccessory> zAccessories = new LinkedHashMap<>();
    public static LinkedHashMap<String, DynamaxAccessory> dynamaxAccessories = new LinkedHashMap<>();

    public static LinkedHashMap<String, FormCycleItem> formCycleItems = new LinkedHashMap<>();

    public static LinkedHashMap<String, FusionItem> fusionItems = new LinkedHashMap<>();

    public static LinkedHashMap<String, PossessionBlock> possessionBlocks = new LinkedHashMap<>();
    public static LinkedHashMap<String, PossessionItem> possessionItems = new LinkedHashMap<>();

    // TODO: Register the misc items through the config
    public static BasePolymerItem SPARKLING_STONE = registerItem("sparkling_stone", baseVanillaItem, List.of(
            "<gray>A stone entrusted to you by a Pokémon that has been venerated as a guardian deity in the Alola region.",
            "<gray>There is said to be some secret in how it sparkles."
    ));
    public static BasePolymerItem WISHING_STAR = registerItem("wishing_star", baseVanillaItem, List.of(
            "<gray>A stone found in the Galar region with a mysterious power.",
            "<gray>It's said that your dreams come true if you find one."
    ));

    public static ZygardeCube ZYGARDE_CUBE = Registry.register(
            Registries.ITEM,
            GenesisForms.id("zygarde_cube"),
            new ZygardeCube(
                    itemSettings,
                    baseVanillaItem,
                    PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/zygarde_cube")),
                    "zygarde_cube",
                    List.of(
                            "<gray>An item in which Zygarde Cores and Cells are gathered.",
                            "<gray>You can also use it to change Zygarde's forms."
                    )
            )
    );

    public static void registerItemGroup() {
        for (Map.Entry<String, AccessoriesConfig.AccessoryData> accessoryDataEntry : AccessoriesConfig.megaAccessories.entrySet()) {
            megaAccessories.put(accessoryDataEntry.getKey(), registerMegaAccessory(accessoryDataEntry.getKey(), baseVanillaItem, accessoryDataEntry.getValue().lore));
            allKeyItems.put(accessoryDataEntry.getKey(), megaAccessories.get(accessoryDataEntry.getKey()).getDefaultStack());
        }

        for (Map.Entry<String, AccessoriesConfig.AccessoryData> accessoryDataEntry : AccessoriesConfig.teraAccessories.entrySet()) {
            teraAccessories.put(accessoryDataEntry.getKey(), registerTeraAccessory(accessoryDataEntry.getKey(), baseVanillaItem, accessoryDataEntry.getValue().lore));
            allKeyItems.put(accessoryDataEntry.getKey(), teraAccessories.get(accessoryDataEntry.getKey()).getDefaultStack());
        }

        for (Map.Entry<String, AccessoriesConfig.AccessoryData> accessoryDataEntry : AccessoriesConfig.zAccessories.entrySet()) {
            zAccessories.put(accessoryDataEntry.getKey(), registerZAccessory(accessoryDataEntry.getKey(), baseVanillaItem, accessoryDataEntry.getValue().lore));
            allKeyItems.put(accessoryDataEntry.getKey(), zAccessories.get(accessoryDataEntry.getKey()).getDefaultStack());
        }

        for (Map.Entry<String, AccessoriesConfig.AccessoryData> accessoryDataEntry : AccessoriesConfig.dmaxAccessories.entrySet()) {
            dynamaxAccessories.put(accessoryDataEntry.getKey(), registerDynamaxAccessory(accessoryDataEntry.getKey(), baseVanillaItem, accessoryDataEntry.getValue().lore));
            allKeyItems.put(accessoryDataEntry.getKey(), dynamaxAccessories.get(accessoryDataEntry.getKey()).getDefaultStack());
        }

        for (Map.Entry<String, KeyFormItemsConfig.KeyItemData> keyItemDataEntry : KeyFormItemsConfig.keyFormItemMap.entrySet()) {
            formCycleItems.put(keyItemDataEntry.getKey(), registerFormCycleItem(keyItemDataEntry.getKey(), baseVanillaItem, keyItemDataEntry.getValue().lore, keyItemDataEntry.getValue().consumable, keyItemDataEntry.getValue().maxStackSize,
                    new CycledFormSetting(
                            keyItemDataEntry.getValue().species,
                            keyItemDataEntry.getValue().featureName,
                            keyItemDataEntry.getValue().featureValues
                    )
            ));
            allKeyItems.put(keyItemDataEntry.getKey(), formCycleItems.get(keyItemDataEntry.getKey()).getDefaultStack());
        }

        for (Map.Entry<String, FusionItemsConfig.FusionItemData> fusionDataEntry : FusionItemsConfig.fusionItemMap.entrySet()) {
            fusionItems.put(fusionDataEntry.getKey(), registerFusionItem(fusionDataEntry.getKey(), baseVanillaItem, fusionDataEntry.getValue().lore, fusionDataEntry.getValue().consumable, fusionDataEntry.getValue().fusions));
            allKeyItems.put(fusionDataEntry.getKey(), fusionItems.get(fusionDataEntry.getKey()).getDefaultStack());
        }

        for (Map.Entry<String, PossessionItemsConfig.PossessionItemData> possessionItemDataEntry : PossessionItemsConfig.possessionItems.entrySet()) {
            possessionBlocks.put(possessionItemDataEntry.getKey(), registerPossessionBlock(possessionItemDataEntry.getKey(), possessionItemDataEntry.getValue()));
            possessionItems.put(possessionItemDataEntry.getKey(), registerPossessionItem(possessionItemDataEntry.getKey(), Items.IRON_INGOT, possessionItemDataEntry.getValue().lore, possessionItemDataEntry.getValue()));
            allKeyItems.put(possessionItemDataEntry.getKey(), possessionItems.get(possessionItemDataEntry.getKey()).getDefaultStack());
        }

        final ItemGroup KEY_ITEMS = FabricItemGroup.builder()
                .icon(megaAccessories.firstEntry().getValue()::getDefaultStack)
                .displayName(Text.literal("Key Items"))
                .entries((displayContext, entries) -> {
                    for (MegaAccessory accessory : megaAccessories.values()) {
                        entries.add(accessory);
                    }

                    for (TeraAccessory accessory : teraAccessories.values()) {
                        entries.add(accessory);
                    }

                    for (ZAccessory accessory : zAccessories.values()) {
                        entries.add(accessory);
                    }

                    for (DynamaxAccessory accessory : dynamaxAccessories.values()) {
                        entries.add(accessory);
                    }

                    for (FormCycleItem formCycleItem : formCycleItems.values()) {
                        entries.add(formCycleItem);
                    }

                    for (FusionItem fusionItem : fusionItems.values()) {
                        entries.add(fusionItem);
                    }

                    for (PossessionItem possessionItem : possessionItems.values()) {
                        entries.add(possessionItem);
                    }

                    entries.add(SPARKLING_STONE);
                    entries.add(WISHING_STAR);
                    entries.add(ZYGARDE_CUBE);
                })
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "key_items"), KEY_ITEMS);
    }

    public static MegaAccessory registerMegaAccessory(String id, Item baseItem, List<String> lore) {
        return Registry.register(
                Registries.ITEM,
                GenesisForms.id(id),
                new MegaAccessory(
                        itemSettings,
                        baseItem,
                        PolymerResourcePackUtils.requestModel(baseItem, GenesisForms.id("item/" + id)),
                        id,
                        lore
                )
        );
    }

    public static ZAccessory registerZAccessory(String id, Item baseItem, List<String> lore) {
        return Registry.register(
                Registries.ITEM,
                GenesisForms.id(id),
                new ZAccessory(
                        itemSettings,
                        baseItem,
                        PolymerResourcePackUtils.requestModel(baseItem, GenesisForms.id("item/" + id)),
                        id,
                        lore
                )
        );
    }

    public static TeraAccessory registerTeraAccessory(String id, Item baseItem, List<String> lore) {
        return Registry.register(
                Registries.ITEM,
                GenesisForms.id(id),
                new TeraAccessory(
                        itemSettings,
                        baseItem,
                        PolymerResourcePackUtils.requestModel(baseItem, GenesisForms.id("item/" + id)),
                        id,
                        lore
                )
        );
    }

    public static DynamaxAccessory registerDynamaxAccessory(String id, Item baseItem, List<String> lore) {
        return Registry.register(
                Registries.ITEM,
                GenesisForms.id(id),
                new DynamaxAccessory(
                        itemSettings,
                        baseItem,
                        PolymerResourcePackUtils.requestModel(baseItem, GenesisForms.id("item/" + id)),
                        id,
                        lore
                )
        );
    }

    public static FormCycleItem registerFormCycleItem(String id, Item baseItem, List<String> lore, boolean consumable, int maxStackSize, CycledFormSetting cycledFormSetting) {
        return Registry.register(
                Registries.ITEM,
                GenesisForms.id(id),
                new FormCycleItem(
                        itemSettings.maxCount(maxStackSize),
                        baseItem,
                        PolymerResourcePackUtils.requestModel(baseItem, GenesisForms.id("item/" + id)),
                        id,
                        lore,
                        consumable,
                        cycledFormSetting
                )
        );
    }

    public static FusionItem registerFusionItem(String id, Item baseItem, List<String> lore, boolean consumable, List<FusionItemsConfig.FusionData> fusionData) {
        return Registry.register(
                Registries.ITEM,
                GenesisForms.id(id),
                new FusionItem(
                        itemSettings,
                        baseItem,
                        PolymerResourcePackUtils.requestModel(baseItem, GenesisForms.id("item/" + id)),
                        id,
                        lore,
                        consumable,
                        fusionData
                )
        );
    }

    public static PossessionBlock registerPossessionBlock(String id, PossessionItemsConfig.PossessionItemData possessionItemData) {
        return Registry.register(Registries.BLOCK, GenesisForms.id(id), new PossessionBlock(AbstractBlock.Settings.create().solid().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.0F, 0.0F).nonOpaque(), possessionItemData.placeable));
    }

    public static PossessionItem registerPossessionItem(String id, Item baseItem, List<String> lore, PossessionItemsConfig.PossessionItemData possessionItemData) {
        return Registry.register(
                Registries.ITEM,
                GenesisForms.id(id),
                new PossessionItem(
                        possessionBlocks.get(id),
                        new Item.Settings().rarity(Rarity.RARE).fireproof(),
                        PolymerResourcePackUtils.requestModel(baseItem, GenesisForms.id("block/" + id)),
                        id,
                        lore,
                        new FormSetting(possessionItemData.species, possessionItemData.featureName, possessionItemData.featureValue, null)
                )
        );
    }

    public static BasePolymerItem registerItem(String id, Item baseItem, List<String> lore) {
        return Registry.register(
                Registries.ITEM,
                GenesisForms.id(id),
                new BasePolymerItem(
                        itemSettings,
                        baseItem,
                        PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/" + id)),
                        id,
                        lore
                )
        );
    }
}
