package me.unariginal.genesisforms.polymer;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.items.helditems.MegastonesConfig;
import me.unariginal.genesisforms.items.helditems.Megastone;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;

public class MegastonesGroup {
    private static final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC);
    private static final Item baseVanillaItem = Items.EMERALD;

    public static LinkedHashMap<String, Megastone> megastones = new LinkedHashMap<>();

    public static void registerItemGroup() {
        for (Map.Entry<String, MegastonesConfig.MegastoneData> megastoneData : MegastonesConfig.megastoneMap.entrySet()) {
            megastones.put(megastoneData.getKey(), registerMegastoneItem(megastoneData.getKey(), megastoneData.getValue()));
        }

        final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
                .icon(megastones.firstEntry().getValue()::getDefaultStack)
                .displayName(Text.literal("Mega Stones"))
                .entries((displayContext, entries) -> {
                    for (Map.Entry<String, Megastone> megastoneEntry : megastones.entrySet()) {
                        entries.add(megastoneEntry.getValue());
                    }
                })
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(GenesisForms.id("mega_stones"), ITEM_GROUP);

        for (Map.Entry<String, Megastone> megastoneEntry : megastones.entrySet()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(megastoneEntry.getValue(), megastoneEntry.getValue().getShowdownID());
        }
    }

    public static Megastone registerMegastoneItem(String itemID, MegastonesConfig.MegastoneData megastoneData) {
        return Registry.register(Registries.ITEM, GenesisForms.id(itemID),
                new Megastone(
                        itemSettings,
                        baseVanillaItem,
                        PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/" + itemID)),
                        itemID,
                        megastoneData.lore,
                        megastoneData
                )
        );
    }
}
