package me.unariginal.genesisforms.polymer;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.items.keyitems.*;
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

public class KeyItems {
    private static final Item.Settings itemSettings = new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof();
    private static final Item baseVanillaItem = Items.DIAMOND;
    public static PolymerModelData
            meteoriteModelData,
            keyStoneModelData,
            megaBraceletModelData,
            megaCharmModelData,
            megaCuffModelData,
            megaRingModelData,
            dynamaxBandModelData,
            zRingModelData,
            zPowerRingModelData,
            sparklingStoneModelData,
            wishingStarModelData,
            teraOrbModelData,
            zygardeCubeModelData;

    public static final KeyStone KEY_STONE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "key_stone"), new KeyStone(itemSettings, baseVanillaItem));
    public static final MegaBracelet MEGA_BRACELET = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "mega_bracelet"), new MegaBracelet(itemSettings, baseVanillaItem));
    public static final MegaCharm MEGA_CHARM = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "mega_charm"), new MegaCharm(itemSettings, baseVanillaItem));
    public static final MegaCuff MEGA_CUFF = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "mega_cuff"), new MegaCuff(itemSettings, baseVanillaItem));
    public static final MegaRing MEGA_RING = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "mega_ring"), new MegaRing(itemSettings, baseVanillaItem));
    public static final ZRing Z_RING = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "z_ring"), new ZRing(itemSettings, baseVanillaItem));
    public static final ZPowerRing Z_POWER_RING = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "z_power_ring"), new ZPowerRing(itemSettings, baseVanillaItem));
    public static final TeraOrb TERA_ORB = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "tera_orb"), new TeraOrb(itemSettings, baseVanillaItem));
    public static final DynamaxBand DYNAMAX_BAND = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "dynamax_band"), new DynamaxBand(itemSettings, baseVanillaItem));
    public static final Meteorite METEORITE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "meteorite"), new Meteorite(itemSettings, baseVanillaItem));
    public static final SparklingStone SPARKLING_STONE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "sparkling_stone"), new SparklingStone(itemSettings, baseVanillaItem));
    public static final WishingStar WISHING_STAR = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "wishing_star"), new WishingStar(itemSettings, baseVanillaItem));
    public static final ZygardeCube ZYGARDE_CUBE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "zygarde_cube"), new ZygardeCube(itemSettings, baseVanillaItem));

    public static Map<String, ItemStack> keyItemStacks = new HashMap<>();

    public static void requestModel() {
        keyStoneModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/key_stone"));
        megaBraceletModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/mega_bracelet"));
        megaCharmModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/mega_charm"));
        megaCuffModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/mega_cuff"));
        megaRingModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/mega_ring"));
        zRingModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/z_ring"));
        zPowerRingModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/z_power_ring"));
        teraOrbModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/tera_orb"));
        dynamaxBandModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/dynamax_band"));
        meteoriteModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/meteorite"));
        sparklingStoneModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/sparkling_stone"));
        wishingStarModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/wishing_star"));
        zygardeCubeModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/zygarde_cube"));
    }

    public static final ItemGroup KEY_ITEMS = FabricItemGroup.builder()
            .icon(KEY_STONE::getDefaultStack)
            .displayName(Text.literal("Key Items"))
            .entries((displayContext, entries) -> {
                entries.add(KEY_STONE);
                entries.add(MEGA_BRACELET);
                entries.add(MEGA_CHARM);
                entries.add(MEGA_CUFF);
                entries.add(MEGA_RING);
                entries.add(Z_RING);
                entries.add(Z_POWER_RING);
                entries.add(TERA_ORB);
                entries.add(DYNAMAX_BAND);
                entries.add(SPARKLING_STONE);
                entries.add(WISHING_STAR);
                entries.add(METEORITE);
                entries.add(ZYGARDE_CUBE);

                for (String key : KeyFormItems.getInstance().keyItemPolymerItems.keySet()) {
                    entries.add(KeyFormItems.getInstance().keyItemPolymerItems.get(key));
                }

                for (String key : FusionItems.getInstance().getAllFusionItemIds()) {
                    entries.add(FusionItems.getInstance().fusionItemPolymerItems.get(key));
                }
            })
            .build();

    public static void registerItemGroup() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "key_items"), KEY_ITEMS);
        keyItemStacks.put("key_stone", KEY_STONE.getDefaultStack());
        keyItemStacks.put("mega_bracelet", MEGA_BRACELET.getDefaultStack());
        keyItemStacks.put("mega_charm", MEGA_CHARM.getDefaultStack());
        keyItemStacks.put("mega_cuff", MEGA_CUFF.getDefaultStack());
        keyItemStacks.put("mega_ring", MEGA_RING.getDefaultStack());
        keyItemStacks.put("z_ring", Z_RING.getDefaultStack());
        keyItemStacks.put("z_power_ring", Z_POWER_RING.getDefaultStack());
        keyItemStacks.put("tera_orb", TERA_ORB.getDefaultStack());
        keyItemStacks.put("dynamax_band", DYNAMAX_BAND.getDefaultStack());
        keyItemStacks.put("sparkling_stone", SPARKLING_STONE.getDefaultStack());
        keyItemStacks.put("wishing_star", WISHING_STAR.getDefaultStack());
        keyItemStacks.put("meteorite", METEORITE.getDefaultStack());
        keyItemStacks.put("zygarde_cube", ZYGARDE_CUBE.getDefaultStack());

        for (String key : KeyFormItems.getInstance().getAllKeyItemIds()) {
            keyItemStacks.put(key, KeyFormItems.getInstance().getKeyItem(key));
        }

        for (String key : FusionItems.getInstance().getAllFusionItemIds()) {
            keyItemStacks.put(key, FusionItems.getInstance().getFusionItem(key));
        }
    }
}
