package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.events.pokemon.HeldItemEvent;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.items.helditems.HeldItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.minecraft.item.ItemStack;

public class HeldItemHandler {
    public static Unit held_item_change(HeldItemEvent.Post post) {
        if (post.getReturned() == post.getReceived() || post.getPokemon().getOwnerPlayer() == null) {
            return Unit.INSTANCE;
        }

        mega_event(post);

        change_forms(post.getPokemon(), post.getReceived());

        return Unit.INSTANCE;
    }

    public static void mega_event(HeldItemEvent.Post post) {
        Pokemon pokemon = post.getPokemon();
        if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"))) {
            MegaEvolutionHandler.devolveMega(pokemon, true);
        }
    }

    public static void change_forms(Pokemon pokemon, ItemStack received) {
        if (NbtUtils.getNbt(received, GenesisForms.MOD_ID).contains(DataKeys.NBT_HELD_ITEM)) {
            String heldItemId = NbtUtils.getNbt(received, GenesisForms.MOD_ID).getString(DataKeys.NBT_HELD_ITEM);
            if (GenesisForms.INSTANCE.getConfig().disabledItems.contains(heldItemId)) return;
            Species species = HeldItems.getInstance().getHeldItemSpecies(heldItemId);
            if (species == null) return;

            if (species.getName().equalsIgnoreCase(pokemon.getSpecies().getName())) {
                switch (heldItemId) {
                    case "rusted_sword", "rusted_shield" -> new StringSpeciesFeature("behemoth_warrior", "crowned").apply(pokemon);

                    case "insect_plate" -> new StringSpeciesFeature("multitype", "bug").apply(pokemon);
                    case "dread_plate" -> new StringSpeciesFeature("multitype", "dark").apply(pokemon);
                    case "draco_plate" -> new StringSpeciesFeature("multitype", "dragon").apply(pokemon);
                    case "zap_plate" -> new StringSpeciesFeature("multitype", "electric").apply(pokemon);
                    case "pixie_plate" -> new StringSpeciesFeature("multitype", "fairy").apply(pokemon);
                    case "fist_plate" -> new StringSpeciesFeature("multitype", "fighting").apply(pokemon);
                    case "flame_plate" -> new StringSpeciesFeature("multitype", "fire").apply(pokemon);
                    case "sky_plate" -> new StringSpeciesFeature("multitype", "flying").apply(pokemon);
                    case "spooky_plate" -> new StringSpeciesFeature("multitype", "ghost").apply(pokemon);
                    case "meadow_plate" -> new StringSpeciesFeature("multitype", "grass").apply(pokemon);
                    case "earth_plate" -> new StringSpeciesFeature("multitype", "ground").apply(pokemon);
                    case "icicle_plate" -> new StringSpeciesFeature("multitype", "ice").apply(pokemon);
                    case "toxic_plate" -> new StringSpeciesFeature("multitype", "poison").apply(pokemon);
                    case "mind_plate" -> new StringSpeciesFeature("multitype", "psychic").apply(pokemon);
                    case "stone_plate" -> new StringSpeciesFeature("multitype", "rock").apply(pokemon);
                    case "iron_plate" -> new StringSpeciesFeature("multitype", "steel").apply(pokemon);
                    case "splash_plate" -> new StringSpeciesFeature("multitype", "water").apply(pokemon);

                    case "bug_memory" -> new StringSpeciesFeature("rks_memory", "bug").apply(pokemon);
                    case "dark_memory" -> new StringSpeciesFeature("rks_memory", "dark").apply(pokemon);
                    case "dragon_memory" -> new StringSpeciesFeature("rks_memory", "dragon").apply(pokemon);
                    case "electric_memory" -> new StringSpeciesFeature("rks_memory", "electric").apply(pokemon);
                    case "fairy_memory" -> new StringSpeciesFeature("rks_memory", "fairy").apply(pokemon);
                    case "fighting_memory" -> new StringSpeciesFeature("rks_memory", "fighting").apply(pokemon);
                    case "fire_memory" -> new StringSpeciesFeature("rks_memory", "fire").apply(pokemon);
                    case "flying_memory" -> new StringSpeciesFeature("rks_memory", "flying").apply(pokemon);
                    case "ghost_memory" -> new StringSpeciesFeature("rks_memory", "ghost").apply(pokemon);
                    case "grass_memory" -> new StringSpeciesFeature("rks_memory", "grass").apply(pokemon);
                    case "ground_memory" -> new StringSpeciesFeature("rks_memory", "ground").apply(pokemon);
                    case "ice_memory" -> new StringSpeciesFeature("rks_memory", "ice").apply(pokemon);
                    case "poison_memory" -> new StringSpeciesFeature("rks_memory", "poison").apply(pokemon);
                    case "psychic_memory" -> new StringSpeciesFeature("rks_memory", "psychic").apply(pokemon);
                    case "rock_memory" -> new StringSpeciesFeature("rks_memory", "rock").apply(pokemon);
                    case "steel_memory" -> new StringSpeciesFeature("rks_memory", "steel").apply(pokemon);
                    case "water_memory" -> new StringSpeciesFeature("rks_memory", "water").apply(pokemon);

                    case "teal_mask" -> {
                        if (GenesisForms.INSTANCE.getConfig().fixOgerponTeraType)
                            pokemon.setTeraType(TeraTypes.getGRASS());
                        new StringSpeciesFeature("ogre_mask", "teal").apply(pokemon);
                    }
                    case "hearthflame_mask" -> {
                        if (GenesisForms.INSTANCE.getConfig().fixOgerponTeraType)
                            pokemon.setTeraType(TeraTypes.getFIRE());
                        new StringSpeciesFeature("ogre_mask", "hearthflame").apply(pokemon);
                    }
                    case "wellspring_mask" -> {
                        if (GenesisForms.INSTANCE.getConfig().fixOgerponTeraType)
                            pokemon.setTeraType(TeraTypes.getWATER());
                        new StringSpeciesFeature("ogre_mask", "wellspring").apply(pokemon);
                    }
                    case "cornerstone_mask" -> {
                        if (GenesisForms.INSTANCE.getConfig().fixOgerponTeraType)
                            pokemon.setTeraType(TeraTypes.getROCK());
                        new StringSpeciesFeature("ogre_mask", "cornerstone").apply(pokemon);
                    }

                    case "red_orb", "blue_orb" -> new StringSpeciesFeature("reversion_state", "primal").apply(pokemon);

                    case "shock_drive" -> new StringSpeciesFeature("techno_drive", "electric").apply(pokemon);
                    case "burn_drive" -> new StringSpeciesFeature("techno_drive", "fire").apply(pokemon);
                    case "chill_drive" -> new StringSpeciesFeature("techno_drive", "ice").apply(pokemon);
                    case "douse_drive" -> new StringSpeciesFeature("techno_drive", "water").apply(pokemon);
                }
            }
        } else if (NbtUtils.getNbt(received, GenesisForms.MOD_ID).contains(DataKeys.NBT_Z_CRYSTAL)) {
            if (pokemon.getSpecies().getName().equalsIgnoreCase("arceus")) {
                String zCrystalId = NbtUtils.getNbt(received, GenesisForms.MOD_ID).getString(DataKeys.NBT_Z_CRYSTAL);
                switch (zCrystalId) {
                    case "buginium-z" -> new StringSpeciesFeature("multitype", "bug").apply(pokemon);
                    case "darkinium-z" -> new StringSpeciesFeature("multitype", "dark").apply(pokemon);
                    case "dragonium-z" -> new StringSpeciesFeature("multitype", "dragon").apply(pokemon);
                    case "electrium-z" -> new StringSpeciesFeature("multitype", "electric").apply(pokemon);
                    case "fairium-z" -> new StringSpeciesFeature("multitype", "fairy").apply(pokemon);
                    case "fightinium-z" -> new StringSpeciesFeature("multitype", "fighting").apply(pokemon);
                    case "firium-z" -> new StringSpeciesFeature("multitype", "fire").apply(pokemon);
                    case "flyinium-z" -> new StringSpeciesFeature("multitype", "flying").apply(pokemon);
                    case "ghostium-z" -> new StringSpeciesFeature("multitype", "ghost").apply(pokemon);
                    case "grassium-z" -> new StringSpeciesFeature("multitype", "grass").apply(pokemon);
                    case "groundium-z" -> new StringSpeciesFeature("multitype", "ground").apply(pokemon);
                    case "icium-z" -> new StringSpeciesFeature("multitype", "ice").apply(pokemon);
                    case "normalium-z" -> new StringSpeciesFeature("multitype", "poison").apply(pokemon);
                    case "poisonium-z" -> new StringSpeciesFeature("multitype", "psychic").apply(pokemon);
                    case "rockium-z" -> new StringSpeciesFeature("multitype", "rock").apply(pokemon);
                    case "steelium-z" -> new StringSpeciesFeature("multitype", "steel").apply(pokemon);
                    case "waterium-z" -> new StringSpeciesFeature("multitype", "water").apply(pokemon);
                    default -> new StringSpeciesFeature("multitype", "normal").apply(pokemon);
                }
            }
        } else {
            switch (pokemon.getSpecies().getName()) {
                case "Zacian", "Zamazenta" -> new StringSpeciesFeature("behemoth_warrior", "hero").apply(pokemon);
                case "Arceus" -> new StringSpeciesFeature("multitype", "normal").apply(pokemon);
                case "Silvally" -> new StringSpeciesFeature("rks_memory", "normal").apply(pokemon);
                case "Ogerpon" -> {
                    if (GenesisForms.INSTANCE.getConfig().fixOgerponTeraType)
                        pokemon.setTeraType(TeraTypes.getGRASS());
                    new StringSpeciesFeature("ogre_mask", "teal").apply(pokemon);
                }
                case "Kyogre", "Groudon" -> new StringSpeciesFeature("reversion_state", "standard").apply(pokemon);
                case "Genesect" -> new StringSpeciesFeature("techno_drive", "normal").apply(pokemon);
            }
        }
    }
}
