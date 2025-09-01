package me.unariginal.genesisforms.items.helditems;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.utils.TextUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HeldItems {
    private static final HeldItems INSTANCE = new HeldItems();
    public static HeldItems getInstance() {
        return INSTANCE;
    }

    public final LinkedHashMap<String, HeldFormItem> heldFormItems = new LinkedHashMap<>();
    public final LinkedHashMap<String, HeldBattleItem> heldBattleItems = new LinkedHashMap<>();

    public void register() {
        loadBattleHeldItems();
        loadFormHeldItems();

        fillPolymerModelData();
        fillPolymerItems();
        registerItemGroup();
    }

    public ItemStack getHeldItem(String id) {
        if (!heldItemPolymerItems.containsKey(id)) return ItemStack.EMPTY;
        return heldItemPolymerItems.get(id).getDefaultStack();
    }

    public Set<String> getAllHeldItemIds() {
        return heldItemPolymerItems.keySet();
    }

    public List<String> getLore(String id) {
        if (GenesisForms.INSTANCE.getItemSettings().item_lore.containsKey(id))
            return GenesisForms.INSTANCE.getItemSettings().item_lore.get(id);
//        if (heldFormItems.containsKey(id)) return heldFormItems.get(id).itemLore();
//        if (heldBattleItems.containsKey(id)) return heldBattleItems.get(id).itemLore();
        return List.of();
    }

    public List<Species> getHeldItemSpecies(String id) {
        List<Species> species = new ArrayList<>();
        if (heldFormItems.containsKey(id)) {
            for (String speciesName : heldFormItems.get(id).formSetting().species()) {
                Identifier speciesIdentifier;
                if (speciesName.contains(":")) speciesIdentifier = Identifier.of(speciesName);
                else speciesIdentifier = MiscUtilsKt.cobblemonResource(speciesName);

                Species pokemonSpecies = PokemonSpecies.INSTANCE.getByIdentifier(speciesIdentifier);
                if (pokemonSpecies != null) species.add(PokemonSpecies.INSTANCE.getByName(speciesName));
            }
        }
        return species;
    }

    public void loadFormHeldItems() {
        heldFormItems.put("rusted_sword", new HeldFormItem(
                "rusted_sword",
                "rustedsword",
                new FormSetting(
                        List.of(
                                "zacian"
                        ),
                        "behemoth_warrior",
                        "hero",
                        "crowned"
                ),
                List.of()
        ));

        heldFormItems.put("rusted_shield", new HeldFormItem(
                "rusted_shield",
                "rustedshield",
                new FormSetting(
                        List.of(
                                "zamazenta"
                        ),
                        "behemoth_warrior",
                        "hero",
                        "crowned"
                ),
                List.of()
        ));

        heldFormItems.put("red_orb", new HeldFormItem(
                "red_orb",
                "redorb",
                new FormSetting(
                        List.of(
                                "groudon"
                        ),
                        "reversion_state",
                        "standard",
                        "primal"
                ),
                List.of()
        ));

        heldFormItems.put("blue_orb", new HeldFormItem(
                "blue_orb",
                "blueorb",
                new FormSetting(
                        List.of(
                                "kyogre"
                        ),
                        "reversion_state",
                        "standard",
                        "primal"
                ),
                List.of()
        ));

        heldFormItems.put("shock_drive", new HeldFormItem(
                "shock_drive",
                "shockdrive",
                new FormSetting(
                        List.of(
                                "genesect"
                        ),
                        "techno_drive",
                        "electric",
                        "normal"
                ),
                List.of()
        ));

        heldFormItems.put("burn_drive", new HeldFormItem(
                "burn_drive",
                "burndrive",
                new FormSetting(
                        List.of(
                                "genesect"
                        ),
                        "techno_drive",
                        "fire",
                        "normal"
                ),
                List.of()
        ));

        heldFormItems.put("chill_drive", new HeldFormItem(
                "chill_drive",
                "chilldrive",
                new FormSetting(
                        List.of(
                                "genesect"
                        ),
                        "techno_drive",
                        "ice",
                        "normal"
                ),
                List.of()
        ));

        heldFormItems.put("douse_drive", new HeldFormItem(
                "douse_drive",
                "dousedrive",
                new FormSetting(
                        List.of(
                                "genesect"
                        ),
                        "techno_drive",
                        "water",
                        "normal"
                ),
                List.of()
        ));

        heldFormItems.put("teal_mask", new HeldFormItem(
                "teal_mask",
                "tealmask",
                new FormSetting(
                        List.of(
                                "ogerpon"
                        ),
                        "ogre_mask",
                        "teal",
                        "teal"
                ),
                List.of()
        ));

        heldFormItems.put("hearthflame_mask", new HeldFormItem(
                "hearthflame_mask",
                "hearthflamemask",
                new FormSetting(
                        List.of(
                                "ogerpon"
                        ),
                        "ogre_mask",
                        "teal",
                        "hearthflame"
                ),
                List.of()
        ));

        heldFormItems.put("wellspring_mask", new HeldFormItem(
                "wellspring_mask",
                "wellspringmask",
                new FormSetting(
                        List.of(
                                "ogerpon"
                        ),
                        "ogre_mask",
                        "teal",
                        "wellspring"
                ),
                List.of()
        ));

        heldFormItems.put("cornerstone_mask", new HeldFormItem(
                "cornerstone_mask",
                "cornerstonemask",
                new FormSetting(
                        List.of(
                                "ogerpon"
                        ),
                        "ogre_mask",
                        "teal",
                        "cornerstone"
                ),
                List.of()
        ));

        heldFormItems.put("bug_memory", new HeldFormItem(
                "bug_memory",
                "bugmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "bug"
                ),
                List.of()
        ));

        heldFormItems.put("dark_memory", new HeldFormItem(
                "dark_memory",
                "darkmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "dark"
                ),
                List.of()
        ));

        heldFormItems.put("dragon_memory", new HeldFormItem(
                "dragon_memory",
                "dragonmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "dragon"
                ),
                List.of()
        ));

        heldFormItems.put("electric_memory", new HeldFormItem(
                "electric_memory",
                "electricmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "electric"
                ),
                List.of()
        ));

        heldFormItems.put("fairy_memory", new HeldFormItem(
                "fairy_memory",
                "fairymemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "fairy"
                ),
                List.of()
        ));

        heldFormItems.put("fighting_memory", new HeldFormItem(
                "fighting_memory",
                "fightingmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "fighting"
                ),
                List.of()
        ));

        heldFormItems.put("fire_memory", new HeldFormItem(
                "fire_memory",
                "firememory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "fire"
                ),
                List.of()
        ));

        heldFormItems.put("flying_memory", new HeldFormItem(
                "flying_memory",
                "flyingmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "flying"
                ),
                List.of()
        ));

        heldFormItems.put("ghost_memory", new HeldFormItem(
                "ghost_memory",
                "ghostmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "ghost"
                ),
                List.of()
        ));

        heldFormItems.put("grass_memory", new HeldFormItem(
                "grass_memory",
                "grassmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "grass"
                ),
                List.of()
        ));

        heldFormItems.put("ground_memory", new HeldFormItem(
                "ground_memory",
                "groundmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "ground"
                ),
                List.of()
        ));

        heldFormItems.put("ice_memory", new HeldFormItem(
                "ice_memory",
                "icememory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "ice"
                ),
                List.of()
        ));

        heldFormItems.put("poison_memory", new HeldFormItem(
                "poison_memory",
                "poisonmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "poison"
                ),
                List.of()
        ));

        heldFormItems.put("psychic_memory", new HeldFormItem(
                "psychic_memory",
                "psychicmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "psychic"
                ),
                List.of()
        ));

        heldFormItems.put("rock_memory", new HeldFormItem(
                "rock_memory",
                "rockmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "rock"
                ),
                List.of()
        ));

        heldFormItems.put("steel_memory", new HeldFormItem(
                "steel_memory",
                "bugmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "steel"
                ),
                List.of()
        ));

        heldFormItems.put("water_memory", new HeldFormItem(
                "water_memory",
                "bugmemory",
                new FormSetting(
                        List.of(
                                "silvally"
                        ),
                        "rks_memory",
                        "normal",
                        "water"
                ),
                List.of()
        ));

        heldFormItems.put("insect_plate", new HeldFormItem(
                "insect_plate",
                "insectplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "bug"
                ),
                List.of()
        ));

        heldFormItems.put("dread_plate", new HeldFormItem(
                "dread_plate",
                "dreadplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "dark"
                ),
                List.of()
        ));

        heldFormItems.put("draco_plate", new HeldFormItem(
                "draco_plate",
                "dracoplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "dragon"
                ),
                List.of()
        ));

        heldFormItems.put("zap_plate", new HeldFormItem(
                "zap_plate",
                "zapplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "electric"
                ),
                List.of()
        ));

        heldFormItems.put("pixie_plate", new HeldFormItem(
                "pixie_plate",
                "pixieplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "fairy"
                ),
                List.of()
        ));

        heldFormItems.put("fist_plate", new HeldFormItem(
                "fist_plate",
                "fistplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "fighting"
                ),
                List.of()
        ));

        heldFormItems.put("flame_plate", new HeldFormItem(
                "flame_plate",
                "flameplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "fire"
                ),
                List.of()
        ));

        heldFormItems.put("sky_plate", new HeldFormItem(
                "sky_plate",
                "skyplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "flying"
                ),
                List.of()
        ));

        heldFormItems.put("spooky_plate", new HeldFormItem(
                "spooky_plate",
                "spookyplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "ghost"
                ),
                List.of()
        ));

        heldFormItems.put("meadow_plate", new HeldFormItem(
                "meadow_plate",
                "meadowplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "grass"
                ),
                List.of()
        ));

        heldFormItems.put("earth_plate", new HeldFormItem(
                "earth_plate",
                "earthplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "ground"
                ),
                List.of()
        ));

        heldFormItems.put("icicle_plate", new HeldFormItem(
                "icicle_plate",
                "icicleplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "ice"
                ),
                List.of()
        ));

        heldFormItems.put("toxic_plate", new HeldFormItem(
                "toxic_plate",
                "toxicplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "poison"
                ),
                List.of()
        ));

        heldFormItems.put("mind_plate", new HeldFormItem(
                "mind_plate",
                "mindplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "psychic"
                ),
                List.of()
        ));

        heldFormItems.put("stone_plate", new HeldFormItem(
                "stone_plate",
                "stoneplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "rock"
                ),
                List.of()
        ));

        heldFormItems.put("iron_plate", new HeldFormItem(
                "iron_plate",
                "ironplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "steel"
                ),
                List.of()
        ));

        heldFormItems.put("splash_plate", new HeldFormItem(
                "splash_plate",
                "splashplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "water"
                ),
                List.of()
        ));

        heldFormItems.put("legend_plate", new HeldFormItem(
                "legend_plate",
                "legendplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "normal"
                ),
                List.of()
        ));

        heldFormItems.put("blank_plate", new HeldFormItem(
                "blank_plate",
                "blankplate",
                new FormSetting(
                        List.of(
                                "arceus"
                        ),
                        "multitype",
                        "normal",
                        "normal"
                ),
                List.of()
        ));
    }

    public void loadBattleHeldItems() {
        heldBattleItems.put("adamant_orb", new HeldBattleItem(
                "adamant_orb",
                "adamantorb",
                List.of(
                        "<gray>A brightly shining gem to be held by Dialga.",
                        "<gray>It boosts the power of Dialga’s Dragon- and Steel-type moves."
                ))
        );
        heldBattleItems.put("adrenaline_orb", new HeldBattleItem(
                "adrenaline_orb",
                "adrenalineorb",
                List.of(
                        "<gray>An item to be held by a Pokémon.",
                        "<gray>This orb boosts the Speed stat if the holder is intimidated."
                ))
        );
        heldBattleItems.put("berserk_gene", new HeldBattleItem(
                "berserk_gene",
                "berserkgene",
                List.of("<gray>Boosts Attack but causes confusion."))
        );
        heldBattleItems.put("booster_energy", new HeldBattleItem(
                "booster_energy",
                "boosterenergy",
                List.of(
                        "<gray>An item to be held by Pokémon with certain Abilities.",
                        "<gray>The energy that fills this capsule boosts the strength of the Pokémon."
                ))
        );
        heldBattleItems.put("griseous_orb", new HeldBattleItem(
                "griseous_orb",
                "griseousorb",
                List.of(
                        "<gray>A shining gem to be held by Giratina.",
                        "<gray>It boosts the power of Giratina's Dragon- and Ghost-type moves."
                ))
        );
        heldBattleItems.put("lucky_punch", new HeldBattleItem(
                "lucky_punch",
                "luckypunch",
                List.of(
                        "<gray>An item to be held by Chansey.",
                        "<gray>This lucky boxing glove boosts the critical-hit ratio of Chansey's moves."
                ))
        );
        heldBattleItems.put("lustrous_orb", new HeldBattleItem(
                "lustrous_orb",
                "lustrousorb",
                List.of(
                        "<gray>A beautifully shining gem to be held by Palkia.",
                        "<gray>It boosts the power of Palkia’s Dragon- and Water-type moves."
                ))
        );
        heldBattleItems.put("macho_brace", new HeldBattleItem(
                "macho_brace",
                "machobrace",
                List.of(
                        "<gray>An item to be held by a Pokémon.",
                        "<gray>Holding this stiff, heavy brace reduces a Pokémon's Speed in battle but allows its stats to grow more quickly."
                ))
        );
        heldBattleItems.put("soul_dew", new HeldBattleItem(
                "soul_dew",
                "souldew",
                List.of(
                        "<gray>A wondrous orb to be held by Latios or Latias.",
                        "<gray>It boosts the power of their Psychic- and Dragon-type moves."
                ))
        );
    }

    public LinkedHashMap<String, HeldItemPolymerItem> heldItemPolymerItems = new LinkedHashMap<>();
    public LinkedHashMap<String, PolymerModelData> heldItemPolymerModelData = new LinkedHashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC).fireproof();
    private final Item baseVanillaItem = Items.EMERALD;

    public void fillPolymerItems() {
        for (String key : heldFormItems.keySet()) {
            heldItemPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new HeldItemPolymerItem(itemSettings.component(DataComponents.HELD_ITEM, key), baseVanillaItem, key)));
        }

        for (String key : heldBattleItems.keySet()) {
            heldItemPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new HeldItemPolymerItem(itemSettings.component(DataComponents.HELD_ITEM, key), baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : heldFormItems.keySet()) {
            heldItemPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }

        for (String key : heldBattleItems.keySet()) {
            heldItemPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
    }

    public void registerItemGroup() {
        ItemGroup HELD_ITEMS = FabricItemGroup.builder()
                .icon(heldItemPolymerItems.values().stream().toList().getFirst()::getDefaultStack)
                .displayName(Text.literal("Held Items"))
                .entries((displayContext, entries) -> {
                    for (String key : heldItemPolymerItems.keySet()) {
                        entries.add(heldItemPolymerItems.get(key));
                    }
                }).build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "held_items"), HELD_ITEMS);

        for (String key : heldFormItems.keySet()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(heldItemPolymerItems.get(key), heldFormItems.get(key).showdownID());
        }

        for (String key : heldBattleItems.keySet()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(heldItemPolymerItems.get(key), heldBattleItems.get(key).showdownID());
        }
    }

    public static class HeldItemPolymerItem extends SimplePolymerItem {
        private final PolymerModelData modelData;
        private final String id;

        public HeldItemPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
            this.modelData = HeldItems.getInstance().heldItemPolymerModelData.get(id);
        }

        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
            return this.modelData.value();
        }

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get(id)) {
                tooltip.add(TextUtils.deserialize(line));
            }
        }
    }
}
