package me.unariginal.genesisforms.polymer;

import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.items.bagitems.TeraShardsConfig;
import me.unariginal.genesisforms.items.bagitems.TeraShard;
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

public class TeraShardsGroup {
    private static final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC);
    private static final Item baseVanillaItem = Items.EMERALD;

    public static LinkedHashMap<String, TeraShard> teraShards = new LinkedHashMap<>();

    public static void registerItemGroup() {
        for (Map.Entry<String, TeraShardsConfig.TeraShardData> teraShardDataEntry : TeraShardsConfig.teraShardMap.entrySet()) {
            teraShards.put(teraShardDataEntry.getKey(), registerTeraShardItem(teraShardDataEntry.getKey(), teraShardDataEntry.getValue()));
        }

        final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
                .icon(teraShards.firstEntry().getValue()::getDefaultStack)
                .displayName(Text.literal("Tera Shards"))
                .entries((displayContext, entries) -> {
                    for (Map.Entry<String, TeraShard> teraShardEntry : teraShards.entrySet()) {
                        entries.add(teraShardEntry.getValue());
                    }
                })
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(GenesisForms.id("tera_shards"), ITEM_GROUP);
    }

    public static TeraShard registerTeraShardItem(String itemID, TeraShardsConfig.TeraShardData teraShardData) {
        TeraType teraType = TeraTypes.get(teraShardData.teraType.toLowerCase());
        if (teraType == null) teraType = TeraTypes.getNORMAL();

        return Registry.register(Registries.ITEM, GenesisForms.id(itemID),
                new TeraShard(
                        itemSettings,
                        baseVanillaItem,
                        PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/" + itemID)),
                        itemID,
                        teraShardData.lore,
                        teraType
                )
        );
    }
}
