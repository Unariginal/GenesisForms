package me.unariginal.genesisforms.polymer;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.blocks.MaxMushroomsBlock;
import me.unariginal.genesisforms.items.bagitems.DynamaxCandy;
import me.unariginal.genesisforms.items.bagitems.MaxHoney;
import me.unariginal.genesisforms.items.bagitems.MaxMushroomsItem;
import me.unariginal.genesisforms.items.bagitems.MaxSoup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.HashMap;
import java.util.Map;

public class BagItems {
    private static final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.UNCOMMON).fireproof();
    private static final Item baseVanillaItem = Items.FLINT;
    public static PolymerModelData
            dynamaxCandyModelData,
            maxHoneyModelData,
            maxSoupModelData;

    public static DynamaxCandy DYNAMAX_CANDY;
    public static MaxHoney MAX_HONEY;
    public static MaxSoup MAX_SOUP;

    public static MaxMushroomsBlock MAX_MUSHROOMS_BLOCK;
    public static MaxMushroomsItem MAX_MUSHROOMS_ITEM;

    public static Map<String, ItemStack> bagItemStacks = new HashMap<>();

    public static void requestModel() {
        dynamaxCandyModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/dynamax_candy"));
        maxHoneyModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/max_honey"));
        maxSoupModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/max_soup"));
    }

    public static void registerItems() {
        DYNAMAX_CANDY = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "dynamax_candy"), new DynamaxCandy(itemSettings, baseVanillaItem));
        MAX_HONEY = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "max_honey"), new MaxHoney(itemSettings, baseVanillaItem));
        MAX_MUSHROOMS_BLOCK = Registry.register(Registries.BLOCK, Identifier.of(GenesisForms.MOD_ID, "max_mushrooms"), new MaxMushroomsBlock(AbstractBlock.Settings.copy(Blocks.BROWN_MUSHROOM), BlockModelType.BIOME_PLANT_BLOCK, "block/max_mushrooms"));
        MAX_MUSHROOMS_ITEM = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "max_mushrooms"), new MaxMushroomsItem(itemSettings, MAX_MUSHROOMS_BLOCK, "item/max_mushrooms"));
        MAX_SOUP = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "max_soup"), new MaxSoup(itemSettings, baseVanillaItem));
    }

    public static void registerItemGroup() {
        final ItemGroup BAG_ITEMS = FabricItemGroup.builder()
                .icon(DYNAMAX_CANDY::getDefaultStack)
                .displayName(Text.literal("Bag Items"))
                .entries((displayContext, entries) -> {
                    entries.add(DYNAMAX_CANDY);
                    entries.add(MAX_MUSHROOMS_ITEM);
                    entries.add(MAX_HONEY);
                    entries.add(MAX_SOUP);
                })
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "bag_items"), BAG_ITEMS);
        bagItemStacks.put("dynamax_candy", DYNAMAX_CANDY.getDefaultStack());
        bagItemStacks.put("max_mushrooms", MAX_MUSHROOMS_ITEM.getDefaultStack());
        bagItemStacks.put("max_honey", MAX_HONEY.getDefaultStack());
        bagItemStacks.put("max_soup", MAX_SOUP.getDefaultStack());
    }
}
