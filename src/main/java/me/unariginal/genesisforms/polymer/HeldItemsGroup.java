package me.unariginal.genesisforms.polymer;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.items.helditems.HeldBattleItemsConfig;
import me.unariginal.genesisforms.config.items.helditems.HeldFormItemsConfig;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.items.helditems.HeldBattleItem;
import me.unariginal.genesisforms.items.helditems.HeldFormItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;

public class HeldItemsGroup {
    private static final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC);
    private static final Item baseVanillaItem = Items.EMERALD;

    public static LinkedHashMap<String, ItemStack> allHeldItems = new LinkedHashMap<>();

    public static LinkedHashMap<String, HeldFormItem> heldFormItems = new LinkedHashMap<>();
    public static LinkedHashMap<String, HeldBattleItem> heldBattleItems = new LinkedHashMap<>();

    public static void registerItemGroup() {
        for (Map.Entry<String, HeldFormItemsConfig.HeldFormItemData> heldFormItemDataEntry : HeldFormItemsConfig.heldFormItemMap.entrySet()) {
            heldFormItems.put(heldFormItemDataEntry.getKey(), registerHeldFormItem(heldFormItemDataEntry.getKey(), heldFormItemDataEntry.getValue()));
            allHeldItems.put(heldFormItemDataEntry.getKey(), heldFormItems.get(heldFormItemDataEntry.getKey()).getDefaultStack());
        }

        for (Map.Entry<String, HeldBattleItemsConfig.HeldBattleItemData> heldbattleItemDataEntry : HeldBattleItemsConfig.heldBattleItemMap.entrySet()) {
            heldBattleItems.put(heldbattleItemDataEntry.getKey(), registerHeldBattleItem(heldbattleItemDataEntry.getKey(), heldbattleItemDataEntry.getValue()));
            allHeldItems.put(heldbattleItemDataEntry.getKey(), heldBattleItems.get(heldbattleItemDataEntry.getKey()).getDefaultStack());
        }

        final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
                .icon(heldFormItems.firstEntry().getValue()::getDefaultStack)
                .displayName(Text.literal("Held Items"))
                .entries((displayContext, entries) -> {
                    for (Map.Entry<String, HeldFormItem> heldFormItemEntry : heldFormItems.entrySet()) {
                        entries.add(heldFormItemEntry.getValue());
                    }

                    for (Map.Entry<String, HeldBattleItem> heldBattleItemEntry : heldBattleItems.entrySet()) {
                        entries.add(heldBattleItemEntry.getValue());
                    }
                })
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(GenesisForms.id("held_items"), ITEM_GROUP);

        for (Map.Entry<String, HeldFormItem> heldFormItemEntry : heldFormItems.entrySet()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(heldFormItemEntry.getValue(), heldFormItemEntry.getValue().getShowdownID());
        }

        for (Map.Entry<String, HeldBattleItem> heldBattleItemEntry : heldBattleItems.entrySet()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(heldBattleItemEntry.getValue(), heldBattleItemEntry.getValue().getShowdownID());
        }
    }

    public static HeldFormItem registerHeldFormItem(String itemID, HeldFormItemsConfig.HeldFormItemData heldFormItemData) {
        return Registry.register(Registries.ITEM, GenesisForms.id(itemID),
                new HeldFormItem(
                        itemSettings,
                        baseVanillaItem,
                        PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/" + itemID)),
                        itemID,
                        heldFormItemData.lore,
                        heldFormItemData.showdownID,
                        new FormSetting(
                                heldFormItemData.species,
                                heldFormItemData.featureName,
                                heldFormItemData.defaultValue,
                                heldFormItemData.alternateValue
                        )
                )
        );
    }

    public static HeldBattleItem registerHeldBattleItem(String itemID, HeldBattleItemsConfig.HeldBattleItemData heldBattleItemData) {
        return Registry.register(Registries.ITEM, GenesisForms.id(itemID),
                new HeldBattleItem(
                        itemSettings,
                        baseVanillaItem,
                        PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/" + itemID)),
                        itemID,
                        heldBattleItemData.lore,
                        heldBattleItemData.showdownID
                )
        );
    }
}
