package me.unariginal.genesisforms.polymer;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
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

import java.util.*;

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
            zygardeCubeModelData,
            rotomCatalogModelData;

    public static KeyStone KEY_STONE;
    public static MegaBracelet MEGA_BRACELET;
    public static MegaCharm MEGA_CHARM;
    public static MegaCuff MEGA_CUFF;
    public static MegaRing MEGA_RING;
    public static ZRing Z_RING;
    public static ZPowerRing Z_POWER_RING;
    public static TeraOrb TERA_ORB;
    public static DynamaxBand DYNAMAX_BAND;
    public static Meteorite METEORITE;
    public static SparklingStone SPARKLING_STONE;
    public static WishingStar WISHING_STAR;
    public static ZygardeCube ZYGARDE_CUBE;
    public static RotomCatalog ROTOM_CATALOG;

    public static LinkedHashMap<String, ItemStack> keyItemStacks = new LinkedHashMap<>();

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
        rotomCatalogModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/rotom_catalog"));
    }

    public static void registerItems() {
        KEY_STONE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "key_stone"), new KeyStone(itemSettings.component(DataComponents.KEY_ITEM, "key_stone"), baseVanillaItem));
        MEGA_BRACELET = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "mega_bracelet"), new MegaBracelet(itemSettings.component(DataComponents.KEY_ITEM, "mega_bracelet"), baseVanillaItem));
        MEGA_CHARM = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "mega_charm"), new MegaCharm(itemSettings.component(DataComponents.KEY_ITEM, "mega_charm"), baseVanillaItem));
        MEGA_CUFF = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "mega_cuff"), new MegaCuff(itemSettings.component(DataComponents.KEY_ITEM, "mega_cuff"), baseVanillaItem));
        MEGA_RING = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "mega_ring"), new MegaRing(itemSettings.component(DataComponents.KEY_ITEM, "mega_ring"), baseVanillaItem));
        Z_RING = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "z_ring"), new ZRing(itemSettings.component(DataComponents.KEY_ITEM, "z_ring"), baseVanillaItem));
        Z_POWER_RING = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "z_power_ring"), new ZPowerRing(itemSettings.component(DataComponents.KEY_ITEM, "z_power_ring"), baseVanillaItem));
        TERA_ORB = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "tera_orb"), new TeraOrb(itemSettings.component(DataComponents.KEY_ITEM, "tera_orb"), baseVanillaItem));
        DYNAMAX_BAND = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "dynamax_band"), new DynamaxBand(itemSettings.component(DataComponents.KEY_ITEM, "dynamax_band"), baseVanillaItem));
        METEORITE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "meteorite"), new Meteorite(itemSettings.component(DataComponents.KEY_ITEM, "meteorite"), baseVanillaItem));
        SPARKLING_STONE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "sparkling_stone"), new SparklingStone(itemSettings, baseVanillaItem));
        WISHING_STAR = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "wishing_star"), new WishingStar(itemSettings, baseVanillaItem));
        ZYGARDE_CUBE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "zygarde_cube"), new ZygardeCube(itemSettings.component(DataComponents.KEY_ITEM, "zygarde_cube"), baseVanillaItem));
        ROTOM_CATALOG = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "rotom_catalog"), new RotomCatalog(itemSettings.component(DataComponents.KEY_ITEM, "rotom_catalog"), baseVanillaItem));
    }

    public static void registerItemGroup() {
        final ItemGroup KEY_ITEMS = FabricItemGroup.builder()
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
                    entries.add(ROTOM_CATALOG);

                    for (String key : KeyFormItems.getInstance().keyItemPolymerItems.keySet()) {
                        entries.add(KeyFormItems.getInstance().keyItemPolymerItems.get(key));
                    }

                    for (String key : FusionItems.getInstance().getAllFusionItemIds()) {
                        entries.add(FusionItems.getInstance().fusionItemPolymerItems.get(key));
                    }

                    for (String key : PossessionBlockItems.getInstance().getAllPossessionItemIds()) {
                        entries.add(PossessionBlockItems.getInstance().possessionItemPolymerItems.get(key));
                    }
                })
                .build();

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
        keyItemStacks.put("rotom_catalog", ROTOM_CATALOG.getDefaultStack());

        for (String key : KeyFormItems.getInstance().getAllKeyItemIds()) {
            keyItemStacks.put(key, KeyFormItems.getInstance().getKeyItem(key));
        }

        for (String key : FusionItems.getInstance().getAllFusionItemIds()) {
            keyItemStacks.put(key, FusionItems.getInstance().getFusionItem(key));
        }

        for (String key : PossessionBlockItems.getInstance().getAllPossessionItemIds()) {
            keyItemStacks.put(key, PossessionBlockItems.getInstance().getPossessionItem(key));
        }
    }
}
