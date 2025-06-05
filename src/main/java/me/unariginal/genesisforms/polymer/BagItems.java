package me.unariginal.genesisforms.polymer;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.blocks.MaxMushroomsBlock;
import me.unariginal.genesisforms.items.bagitems.DynamaxCandy;
import me.unariginal.genesisforms.items.bagitems.MaxHoney;
import me.unariginal.genesisforms.items.bagitems.MaxMushroomsItem;
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
            maxHoneyModelData;

    public static final DynamaxCandy DYNAMAX_CANDY = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "dynamax_candy"), new DynamaxCandy(itemSettings, baseVanillaItem));
    public static final MaxHoney MAX_HONEY = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "max_honey"), new MaxHoney(itemSettings, baseVanillaItem));

    public static final MaxMushroomsBlock MAX_MUSHROOMS_BLOCK = Registry.register(Registries.BLOCK, Identifier.of(GenesisForms.MOD_ID, "max_mushrooms"), new MaxMushroomsBlock(AbstractBlock.Settings.copy(Blocks.BROWN_MUSHROOM_BLOCK), BlockModelType.PLANT_BLOCK, "block/max_mushrooms"));
    public static final MaxMushroomsItem MAX_MUSHROOMS_ITEM = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "max_mushrooms"), new MaxMushroomsItem(itemSettings, MAX_MUSHROOMS_BLOCK, "block/max_mushrooms"));

    public static Map<String, ItemStack> bagItemStacks = new HashMap<>();

    public static void requestModel() {
        dynamaxCandyModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/dynamax_candy"));
        maxHoneyModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/max_honey"));
    }

    public static final ItemGroup BAG_ITEMS = FabricItemGroup.builder()
            .icon(DYNAMAX_CANDY::getDefaultStack)
            .displayName(Text.literal("Bag Items"))
            .entries((displayContext, entries) -> {
                entries.add(DYNAMAX_CANDY);
                entries.add(MAX_MUSHROOMS_ITEM);
                entries.add(MAX_HONEY);
            })
            .build();

    public static void registerItemGroup() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "bag_items"), BAG_ITEMS);
        bagItemStacks.put("dynamax_candy", DYNAMAX_CANDY.getDefaultStack());
        bagItemStacks.put("max_mushrooms", MAX_MUSHROOMS_ITEM.getDefaultStack());
        bagItemStacks.put("max_honey", MAX_HONEY.getDefaultStack());
    }
}
