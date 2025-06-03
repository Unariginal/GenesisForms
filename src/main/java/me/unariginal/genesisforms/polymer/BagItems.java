package me.unariginal.genesisforms.polymer;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.items.bagitems.DynamaxCandy;
import me.unariginal.genesisforms.items.bagitems.MaxHoney;
import me.unariginal.genesisforms.items.bagitems.MaxMushrooms;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
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
            maxMushroomsModelData,
            maxHoneyModelData;

    public static final DynamaxCandy DYNAMAX_CANDY = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "dynamax_candy"), new DynamaxCandy(itemSettings, baseVanillaItem));
    public static final MaxMushrooms MAX_MUSHROOMS = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "max_mushrooms"), new MaxMushrooms(itemSettings, baseVanillaItem));
    public static final MaxHoney MAX_HONEY = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "max_honey"), new MaxHoney(itemSettings, baseVanillaItem));

    public static Map<String, ItemStack> bagItemStacks = new HashMap<>();

    public static void requestModel() {
        dynamaxCandyModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/dynamax_candy"));
        maxMushroomsModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/max_mushrooms"));
        maxHoneyModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/max_honey"));
    }

    public static final ItemGroup BAG_ITEMS = FabricItemGroup.builder()
            .icon(DYNAMAX_CANDY::getDefaultStack)
            .displayName(Text.literal("Bag Items"))
            .entries((displayContext, entries) -> {
                entries.add(DYNAMAX_CANDY);
                entries.add(MAX_MUSHROOMS);
                entries.add(MAX_HONEY);
            })
            .build();

    public static void registerItemGroup() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "bag_items"), BAG_ITEMS);
        bagItemStacks.put("dynamax_candy", DYNAMAX_CANDY.getDefaultStack());
        bagItemStacks.put("max_mushrooms", MAX_MUSHROOMS.getDefaultStack());
        bagItemStacks.put("max_honey", MAX_HONEY.getDefaultStack());
    }
}
