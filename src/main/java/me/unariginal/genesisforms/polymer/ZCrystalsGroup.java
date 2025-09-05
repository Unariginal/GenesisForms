package me.unariginal.genesisforms.polymer;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.items.helditems.ZCrystalsConfig;
import me.unariginal.genesisforms.items.helditems.ZCrystal;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZCrystalsGroup {
    private static final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC);
    private static final Item baseVanillaItem = Items.EMERALD;

    public static LinkedHashMap<String, ZCrystal> zCrystals = new LinkedHashMap<>();

    public static void registerItemGroup() {
        for (Map.Entry<String, ZCrystalsConfig.TypedZCrystalData> typedZCrystalDataEntry : ZCrystalsConfig.zCrystalData.typedZCrystalMap.entrySet()) {
            zCrystals.put(typedZCrystalDataEntry.getKey(), registerTypedZCrystalItem(typedZCrystalDataEntry.getKey(), typedZCrystalDataEntry.getValue()));
        }

        for (Map.Entry<String, ZCrystalsConfig.SpeciesZCrystalData> speciesZCrystalDataEntry : ZCrystalsConfig.zCrystalData.speciesZCrystalMap.entrySet()) {
            zCrystals.put(speciesZCrystalDataEntry.getKey(), registerSpeciesZCrystalItem(speciesZCrystalDataEntry.getKey(), speciesZCrystalDataEntry.getValue()));
        }

        final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
                .icon(zCrystals.firstEntry().getValue()::getDefaultStack)
                .displayName(Text.literal("Z Crystals"))
                .entries((displayContext, entries) -> {
                    for (Map.Entry<String, ZCrystal> zCrystalEntry : zCrystals.entrySet()) {
                        entries.add(zCrystalEntry.getValue());
                    }
                })
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(GenesisForms.id("z_crystals"), ITEM_GROUP);

        for (Map.Entry<String, ZCrystal> zCrystalEntry : zCrystals.entrySet()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(zCrystalEntry.getValue(), zCrystalEntry.getValue().getShowdownID());
        }
    }

    public static ZCrystal registerTypedZCrystalItem(String itemID, ZCrystalsConfig.TypedZCrystalData zCrystalData) {
        return Registry.register(Registries.ITEM, GenesisForms.id(itemID),
                new ZCrystal(
                        itemSettings,
                        baseVanillaItem,
                        PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/" + itemID)),
                        itemID,
                        zCrystalData.lore,
                        zCrystalData.formChanges,
                        zCrystalData.showdownID
                )
        );
    }

    public static ZCrystal registerSpeciesZCrystalItem(String itemID, ZCrystalsConfig.SpeciesZCrystalData zCrystalData) {
        return Registry.register(Registries.ITEM, GenesisForms.id(itemID),
                new ZCrystal(
                        itemSettings,
                        baseVanillaItem,
                        PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/" + itemID)),
                        itemID,
                        zCrystalData.lore,
                        List.of(),
                        zCrystalData.showdownID
                )
        );
    }
}
