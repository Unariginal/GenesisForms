package me.unariginal.genesisforms.polymer;

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
            adamantCrystalModelData,
            lustrousGlobeModelData,
            griseousCoreModelData,
            gracideaFlowerModelData,
            revealGlassModelData,
            prisonBottleModelData,
            meteoriteModelData,
            yellowNectarModelData,
            redNectarModelData,
            pinkNectarModelData,
            purpleNectarModelData,
            dnaSplicersModelData,
            nSolarizerModelData,
            nLunarizerModelData,
            reinsOfUnityModelData,
            keyStoneModelData,
            dynamaxBandModelData,
            zRingModelData,
            wishingStarModelData,
            teraOrbModelData,
            zygardeCubeModelData;

    public static final KeyStone KEY_STONE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "key_stone"), new KeyStone(itemSettings, baseVanillaItem));
    public static final ZRing Z_RING = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "z_ring"), new ZRing(itemSettings, baseVanillaItem));
    public static final TeraOrb TERA_ORB = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "tera_orb"), new TeraOrb(itemSettings, baseVanillaItem));
    public static final DynamaxBand DYNAMAX_BAND = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "dynamax_band"), new DynamaxBand(itemSettings, baseVanillaItem));
    public static final AdamantCrystal ADAMANT_CRYSTAL = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "adamant_crystal"), new AdamantCrystal(itemSettings, baseVanillaItem));
    public static final LustrousGlobe LUSTROUS_GLOBE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "lustrous_globe"), new LustrousGlobe(itemSettings, baseVanillaItem));
    public static final GriseousCore GRISEOUS_CORE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "griseous_core"), new GriseousCore(itemSettings, baseVanillaItem));
    public static final GracideaFlower GRACIDEA_FLOWER = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "gracidea_flower"), new GracideaFlower(itemSettings, baseVanillaItem));
    public static final RevealGlass REVEAL_GLASS = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "reveal_glass"), new RevealGlass(itemSettings, baseVanillaItem));
    public static final PrisonBottle PRISON_BOTTLE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "prison_bottle"), new PrisonBottle(itemSettings, baseVanillaItem));
    public static final Meteorite METEORITE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "meteorite"), new Meteorite(itemSettings, baseVanillaItem));
    public static final YellowNectar YELLOW_NECTAR = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "yellow_nectar"), new YellowNectar(itemSettings, baseVanillaItem));
    public static final RedNectar RED_NECTAR = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "red_nectar"), new RedNectar(itemSettings, baseVanillaItem));
    public static final PinkNectar PINK_NECTAR = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "pink_nectar"), new PinkNectar(itemSettings, baseVanillaItem));
    public static final PurpleNectar PURPLE_NECTAR = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "purple_nectar"), new PurpleNectar(itemSettings, baseVanillaItem));
    public static final DNASplicers DNA_SPLICERS = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "dna_splicers"), new DNASplicers(itemSettings, baseVanillaItem));
    public static final NSolarizer N_SOLARIZER = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "n_solarizer"), new NSolarizer(itemSettings, baseVanillaItem));
    public static final NLunarizer N_LUNARIZER = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "n_lunarizer"), new NLunarizer(itemSettings, baseVanillaItem));
    public static final ReinsOfUnity REINS_OF_UNITY = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "reins_of_unity"), new ReinsOfUnity(itemSettings, baseVanillaItem));
    public static final WishingStar WISHING_STAR = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "wishing_star"), new WishingStar(itemSettings, baseVanillaItem));
    public static final ZygardeCube ZYGARDE_CUBE = Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, "zygarde_cube"), new ZygardeCube(itemSettings, baseVanillaItem));

    public static Map<String, ItemStack> keyItemStacks = new HashMap<>();

    public static void requestModel() {
        keyStoneModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/key_stone"));
        zRingModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/z_ring"));
        teraOrbModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/tera_orb"));
        dynamaxBandModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/dynamax_band"));
        adamantCrystalModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/adamant_crystal"));
        lustrousGlobeModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/lustrous_globe"));
        griseousCoreModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/griseous_core"));
        gracideaFlowerModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/gracidea_flower"));
        revealGlassModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/reveal_glass"));
        prisonBottleModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/prison_bottle"));
        meteoriteModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/meteorite"));
        yellowNectarModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/yellow_nectar"));
        redNectarModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/red_nectar"));
        pinkNectarModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/pink_nectar"));
        purpleNectarModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/purple_nectar"));
        dnaSplicersModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/dna_splicers"));
        nSolarizerModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/n_solarizer"));
        nLunarizerModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/n_lunarizer"));
        reinsOfUnityModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/reins_of_unity"));
        wishingStarModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/wishing_star"));
        zygardeCubeModelData = PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/zygarde_cube"));
    }

    public static final ItemGroup KEY_ITEMS = FabricItemGroup.builder()
            .icon(KEY_STONE::getDefaultStack)
            .displayName(Text.literal("Key Items"))
            .entries((displayContext, entries) -> {
                entries.add(KEY_STONE);
                entries.add(Z_RING);
                entries.add(TERA_ORB);
                entries.add(DYNAMAX_BAND);
                entries.add(WISHING_STAR);
                entries.add(ADAMANT_CRYSTAL);
                entries.add(LUSTROUS_GLOBE);
                entries.add(GRISEOUS_CORE);
                entries.add(GRACIDEA_FLOWER);
                entries.add(REVEAL_GLASS);
                entries.add(PRISON_BOTTLE);
                entries.add(METEORITE);
                entries.add(YELLOW_NECTAR);
                entries.add(RED_NECTAR);
                entries.add(PINK_NECTAR);
                entries.add(PURPLE_NECTAR);
                entries.add(DNA_SPLICERS);
                entries.add(N_SOLARIZER);
                entries.add(N_LUNARIZER);
                entries.add(REINS_OF_UNITY);
                entries.add(ZYGARDE_CUBE);
            })
            .build();

    public static void registerItemGroup() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(GenesisForms.MOD_ID, "key_items"), KEY_ITEMS);
        keyItemStacks.put("key_stone", KEY_STONE.getDefaultStack());
        keyItemStacks.put("z_ring", Z_RING.getDefaultStack());
        keyItemStacks.put("tera_orb", TERA_ORB.getDefaultStack());
        keyItemStacks.put("dynamax_band", DYNAMAX_BAND.getDefaultStack());
        keyItemStacks.put("wishing_star", WISHING_STAR.getDefaultStack());
        keyItemStacks.put("adamant_crystal", ADAMANT_CRYSTAL.getDefaultStack());
        keyItemStacks.put("lustrous_globe", LUSTROUS_GLOBE.getDefaultStack());
        keyItemStacks.put("griseous_core", GRISEOUS_CORE.getDefaultStack());
        keyItemStacks.put("gracidea_flower", GRACIDEA_FLOWER.getDefaultStack());
        keyItemStacks.put("reveal_glass", REVEAL_GLASS.getDefaultStack());
        keyItemStacks.put("prison_bottle", PRISON_BOTTLE.getDefaultStack());
        keyItemStacks.put("meteorite", METEORITE.getDefaultStack());
        keyItemStacks.put("yellow_nectar", YELLOW_NECTAR.getDefaultStack());
        keyItemStacks.put("red_nectar", RED_NECTAR.getDefaultStack());
        keyItemStacks.put("pink_nectar", PINK_NECTAR.getDefaultStack());
        keyItemStacks.put("purple_nectar", PURPLE_NECTAR.getDefaultStack());
        keyItemStacks.put("dna_splicers", DNA_SPLICERS.getDefaultStack());
        keyItemStacks.put("n_solarizer", N_SOLARIZER.getDefaultStack());
        keyItemStacks.put("n_lunarizer", N_LUNARIZER.getDefaultStack());
        keyItemStacks.put("reins_of_unity", REINS_OF_UNITY.getDefaultStack());
        keyItemStacks.put("zygarde_cube", ZYGARDE_CUBE.getDefaultStack());
    }
}
