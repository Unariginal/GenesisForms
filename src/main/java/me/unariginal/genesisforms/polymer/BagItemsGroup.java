package me.unariginal.genesisforms.polymer;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.blocks.MaxMushroomsBlock;
import me.unariginal.genesisforms.config.items.bagitems.MaxItemsConfig;
import me.unariginal.genesisforms.items.bagitems.DynamaxCandy;
import me.unariginal.genesisforms.items.bagitems.MaxHoney;
import me.unariginal.genesisforms.items.bagitems.MaxMushrooms;
import me.unariginal.genesisforms.items.bagitems.MaxSoup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.*;

public class BagItemsGroup {
    private static final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.UNCOMMON).fireproof();
    private static final Item baseVanillaItem = Items.FLINT;

    public static MaxMushroomsBlock MAX_MUSHROOMS_BLOCK;

    public static LinkedHashMap<String, Item> bagItems = new LinkedHashMap<>();

    public static void registerItemGroup() {
        if (MaxItemsConfig.maxItemMap.containsKey("dynamax_candy")) {
            MaxItemsConfig.MaxItemData maxItemData = MaxItemsConfig.maxItemMap.get("dynamax_candy");
            bagItems.put("dynamax_candy", Registry.register(Registries.ITEM, GenesisForms.id("dynamax_candy"), new DynamaxCandy(itemSettings, baseVanillaItem, PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id("item/dynamax_candy")), maxItemData.lore, maxItemData.consumable)));
        }

        if (MaxItemsConfig.maxItemMap.containsKey("max_honey")) {
            MaxItemsConfig.MaxItemData maxItemData = MaxItemsConfig.maxItemMap.get("max_honey");
            bagItems.put("max_honey", Registry.register(Registries.ITEM, GenesisForms.id("max_honey"), new MaxHoney(itemSettings, baseVanillaItem, PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id( "item/max_honey")), maxItemData.lore, maxItemData.consumable)));
        }

        if (MaxItemsConfig.maxItemMap.containsKey("max_mushrooms")) {
            MaxItemsConfig.MaxItemData maxItemData = MaxItemsConfig.maxItemMap.get("max_mushrooms");
            MAX_MUSHROOMS_BLOCK = Registry.register(Registries.BLOCK, GenesisForms.id("max_mushrooms"), new MaxMushroomsBlock(AbstractBlock.Settings.copy(Blocks.RED_MUSHROOM).nonOpaque().noCollision().mapColor(MapColor.DULL_PINK).breakInstantly().sounds(BlockSoundGroup.CHERRY_LEAVES).postProcess(Blocks::always).pistonBehavior(PistonBehavior.DESTROY).luminance(value -> 4), BlockModelType.BIOME_PLANT_BLOCK, "block/max_mushrooms"));
            bagItems.put("max_mushrooms", Registry.register(Registries.ITEM, GenesisForms.id("max_mushrooms"), new MaxMushrooms(MAX_MUSHROOMS_BLOCK, itemSettings, PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id( "item/max_mushrooms")), maxItemData.lore, maxItemData.consumable)));
        }

        if (MaxItemsConfig.maxItemMap.containsKey("max_soup")) {
            MaxItemsConfig.MaxItemData maxItemData = MaxItemsConfig.maxItemMap.get("max_soup");
            bagItems.put("max_soup", Registry.register(Registries.ITEM, GenesisForms.id("max_soup"), new MaxSoup(itemSettings, baseVanillaItem, PolymerResourcePackUtils.requestModel(baseVanillaItem, GenesisForms.id( "item/max_soup")), maxItemData.lore, maxItemData.consumable)));
        }

        final ItemGroup BAG_ITEMS = FabricItemGroup.builder()
                .icon(bagItems.firstEntry().getValue()::getDefaultStack)
                .displayName(Text.literal("Bag Items"))
                .entries((displayContext, entries) -> {
                    for (Item item : bagItems.values()) {
                        entries.add(item);
                    }
                })
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "bag_items"), BAG_ITEMS);
    }
}
