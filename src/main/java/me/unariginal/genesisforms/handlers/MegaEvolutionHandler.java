package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.instruction.MegaEvolutionEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPostEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.Config;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.items.helditems.megastones.MegaStoneHeldItems;
import me.unariginal.genesisforms.utils.ParticleUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class MegaEvolutionHandler {
    private final static GenesisForms gf = GenesisForms.INSTANCE;
    public static List<Pokemon> activeMegaAnimations = new ArrayList<>();

    public static Unit mega_event(MegaEvolutionEvent event) {
        PokemonBattle battle = event.getBattle();
        Pokemon pokemon = event.getPokemon().getEffectedPokemon();
        PokemonEntity pokemonEntity = pokemon.getEntity();
        if (pokemonEntity != null) {
            battle.dispatchWaitingToFront(3f, () -> Unit.INSTANCE);
            gf.logInfo("Entering Mega Evolve Method.");
            attemptEvolution(pokemonEntity, battle, event.getPokemon());
        }

        return Unit.INSTANCE;
    }

    public static Unit handleMegaRayquaza(PokemonSentPostEvent event) {
        Pokemon pokemon = event.getPokemon();
        ServerPlayerEntity player = pokemon.getOwnerPlayer();
        if (gf.getConfig().enableMegaEvolution) {
            if (player != null) {
                if (event.getPokemonEntity().isBattling()) {
                    PokemonBattle battle = Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayer(player);
                    if (battle != null) {
                        if (pokemon.getSpecies().getName().equalsIgnoreCase("Rayquaza")) {
                            for (Move move : pokemon.getMoveSet().getMoves()) {
                                if (move.getName().equalsIgnoreCase("dragonascent")) {
                                    gf.logInfo("Rayquaza has dragonascent!");
                                    for (ActiveBattlePokemon activeBattlePokemon : battle.getActivePokemon()) {
                                        BattlePokemon battlePokemon = activeBattlePokemon.getBattlePokemon();
                                        if (battlePokemon != null) {
                                            Pokemon activePokemon = activeBattlePokemon.getBattlePokemon().getOriginalPokemon();
                                            if (activePokemon.getUuid().equals(pokemon.getUuid())) {
                                                gf.logInfo("Posting Mega Event!");
                                                CobblemonEvents.MEGA_EVOLUTION.post(new MegaEvolutionEvent[]{new MegaEvolutionEvent(battle, battlePokemon)}, megaEvolutionEvent -> Unit.INSTANCE);
                                                return Unit.INSTANCE;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Unit.INSTANCE;
    }

    public static void attemptEvolution(PokemonEntity pokemonEntity, PokemonBattle battle, BattlePokemon battlePokemon) {
        ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();

        if (player != null) {
            if (!gf.getPlayersWithMega().containsKey(player.getUuid()) && !gf.getMegaEvolvedThisBattle().contains(player.getUuid())) {
                gf.logInfo("Attempting evolution");
                pokemonEntity.after(0.5f, () -> {
                    megaEvolve(pokemonEntity, player, true);
                    PacketHandler.update_packets(battle, battlePokemon, true);
                    return Unit.INSTANCE;
                });
            }
        }
    }

    public static void megaEvolve(PokemonEntity pokemonEntity, ServerPlayerEntity player, boolean fromBattle) {
        if (pokemonEntity.getPokemon().getOwnerPlayer() != player) {
            return;
        }

        if (!gf.getConfig().enableMegaEvolution) {
            return;
        }

        PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
        for (Pokemon pokemon : playerPartyStore) {
            if (pokemon != null) {
                boolean isMega = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
                if (isMega) {
                    gf.logInfo("Player has a mega already");
                    gf.getPlayersWithMega().put(player.getUuid(), pokemon);
                    return;
                }
            }
        }

        PCStore pcStore = Cobblemon.INSTANCE.getStorage().getPC(player);
        for (Pokemon pokemon : pcStore) {
            if (pokemon != null) {
                boolean isMega = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
                if (isMega) {
                    gf.logInfo("Player has a mega already");
                    gf.getPlayersWithMega().put(player.getUuid(), pokemon);
                    return;
                }
            }
        }

        Pokemon pokemon = pokemonEntity.getPokemon();

        if (pokemon.getSpecies().getName().equalsIgnoreCase("Rayquaza")) {
            gf.logInfo("This is rayquaza! Checking for dragonascent move.");
            for (Move move : pokemon.getMoveSet().getMoves()) {
                if (move.getName().equalsIgnoreCase("dragonascent")) {
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, gf.getConfig().megaFeatureValue, fromBattle);
                }
            }
            return;
        }

        ItemStack heldItem = pokemon.heldItem();
        if (!heldItem.getComponents().contains(DataComponents.MEGA_STONE)) {
            gf.logInfo("No mega stone component");
            return;
        }
        String stoneId = heldItem.getComponents().get(DataComponents.MEGA_STONE);
        if (stoneId == null) {
            gf.logInfo("Stone id null");
            return;
        }

        Species species = MegaStoneHeldItems.getInstance().getMegaStoneSpecies(stoneId);

        if (species == null) {
            gf.logInfo("No mega stone found! Not invoking mega evolution.");
            return;
        }

        gf.logInfo("Species Name: " + species.getName());

        if (!species.getName().equalsIgnoreCase(pokemon.getSpecies().getName())) {
            gf.logInfo("Mismatched species");
            return;
        }

        if (pokemonEntity.isBattling() && !fromBattle) {
            gf.logInfo("Battling but not from battle! Not invoking mega evolution.");
            return;
        }

        if (species.getName().equalsIgnoreCase(pokemon.getSpecies().getName())) {
            if (species.getName().equalsIgnoreCase("Charizard")) {
                if (stoneId.equalsIgnoreCase("charizardite-x")) {
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, gf.getConfig().megaXFeatureValue, fromBattle);
                } else if (stoneId.equalsIgnoreCase("charizardite-y")) {
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, gf.getConfig().megaYFeatureValue, fromBattle);
                }
            }
            else if (species.getName().equalsIgnoreCase("Mewtwo")) {
                if (stoneId.equalsIgnoreCase("mewtwonite-x")){
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, gf.getConfig().megaXFeatureValue, fromBattle);
                } else if (stoneId.equalsIgnoreCase("mewtwonite-y")) {
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, gf.getConfig().megaYFeatureValue, fromBattle);
                }
            } else {
                boolean isCustom = false;
                for (Config.CustomMega customMega : gf.getConfig().customMegaList) {
                    if (species.getName().equalsIgnoreCase(customMega.baseSpecies())) {
                        isCustom = true;
                        if (stoneId.equalsIgnoreCase(customMega.megastoneID())) {
                            changeMegaFormWithAnimation(pokemonEntity, pokemon, player, customMega.megaForm(), fromBattle);
                        }
                    }
                }
                if (!isCustom) {
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, gf.getConfig().megaFeatureValue, fromBattle);
                }
            }
        }
    }

    public static boolean canMegaEvolve(Pokemon pokemon) {
        if (pokemon.getSpecies().getName().equalsIgnoreCase("Rayquaza")) {
            gf.logInfo("This is rayquaza! Checking for dragonascent move.");
            for (Move move : pokemon.getMoveSet().getMoves()) {
                if (move.getName().equalsIgnoreCase("dragonascent")) {
                    return true;
                }
            }
        }

        ItemStack heldItem = pokemon.heldItem();
        if (!heldItem.getComponents().contains(DataComponents.MEGA_STONE)) {
            gf.logInfo("No mega stone component");
            return false;
        }
        String stoneId = heldItem.getComponents().get(DataComponents.MEGA_STONE);
        if (stoneId == null) {
            gf.logInfo("Stone id null");
            return false;
        }

        Species species = MegaStoneHeldItems.getInstance().getMegaStoneSpecies(stoneId);

        if (species == null) {
            gf.logInfo("No mega stone found! Not invoking mega evolution.");
            return false;
        }

        gf.logInfo("Species Name: " + species.getName());

        if (!species.getName().equalsIgnoreCase(pokemon.getSpecies().getName())) {
            gf.logInfo("Mismatched species");
            return false;
        }

        return true;
    }

    private static void changeMegaFormWithAnimation(PokemonEntity pokemonEntity, Pokemon pokemon, ServerPlayerEntity player, String form, boolean fromBattle) {
        if (gf.getAnimationConfig().megaEnabled) {
            if (!activeMegaAnimations.contains(pokemon)) {
                ParticleUtils.spawnParticle(gf.getAnimationConfig().megaIdentifier, pokemonEntity.getPos().add(0, pokemonEntity.getBoundingBox().getLengthY() / 2, 0), pokemonEntity.getWorld().getRegistryKey());
                activeMegaAnimations.add(pokemon);
                pokemonEntity.after(gf.getAnimationConfig().megaSeconds, () -> {
                    if (canMegaEvolve(pokemon)) {
                        gf.getPlayersWithMega().put(player.getUuid(), pokemon);
                        if (fromBattle) {
                            gf.getMegaEvolvedThisBattle().add(player.getUuid());
                        }

                        if (form.equalsIgnoreCase(gf.getConfig().megaXFeatureValue)) {
                            new StringSpeciesFeature(gf.getConfig().megaXFeatureName, form).apply(pokemon);
                        } else if (form.equalsIgnoreCase(gf.getConfig().megaYFeatureValue)) {
                            new StringSpeciesFeature(gf.getConfig().megaYFeatureName, form).apply(pokemon);
                        } else {
                            new StringSpeciesFeature(gf.getConfig().megaFeatureName, form).apply(pokemon);
                        }
                        pokemon.setTradeable(false);
                    }
                    activeMegaAnimations.remove(pokemon);
                    return Unit.INSTANCE;
                });
            }
        } else {
            pokemonEntity.after(0.2F, () -> {
                if (canMegaEvolve(pokemon)) {
                    gf.getPlayersWithMega().put(player.getUuid(), pokemon);
                    if (fromBattle) {
                        gf.getMegaEvolvedThisBattle().add(player.getUuid());
                    }

                    if (form.equalsIgnoreCase(gf.getConfig().megaXFeatureValue)) {
                        new StringSpeciesFeature(gf.getConfig().megaXFeatureName, form).apply(pokemon);
                    } else if (form.equalsIgnoreCase(gf.getConfig().megaYFeatureValue)) {
                        new StringSpeciesFeature(gf.getConfig().megaYFeatureName, form).apply(pokemon);
                    } else {
                        new StringSpeciesFeature(gf.getConfig().megaFeatureName, form).apply(pokemon);
                    }
                    pokemon.setTradeable(false);
                }
                return Unit.INSTANCE;
            });
        }
    }

    public static void devolveMega(Pokemon pokemon, boolean fromBattle) {
        ServerPlayerEntity player = pokemon.getOwnerPlayer();
        if (player != null) {
            PokemonEntity pokemonEntity = pokemon.getEntity();
            if (pokemonEntity != null) {
                if (pokemonEntity.isBattling() && !fromBattle) {
                    return;
                }

                gf.getPlayersWithMega().remove(player.getUuid());

                if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("x"))) {
                    new StringSpeciesFeature(gf.getConfig().megaXFeatureName, "none").apply(pokemon);
                } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("y"))) {
                    new StringSpeciesFeature(gf.getConfig().megaYFeatureName, "none").apply(pokemon);
                } else {
                    new StringSpeciesFeature(gf.getConfig().megaFeatureName, "none").apply(pokemon);
                }
                pokemon.setTradeable(true);
            }
        }
    }

    public static Unit pokemon_released(ReleasePokemonEvent.Post post) {
        ServerPlayerEntity player = post.getPlayer();
        if (gf.getPlayersWithMega().containsKey(player.getUuid())) {
            Pokemon pokemon = post.getPokemon();
            Pokemon mega = gf.getPlayersWithMega().get(player.getUuid());
            if (pokemon.getUuid().equals(mega.getUuid())) {
                gf.getPlayersWithMega().remove(player.getUuid());
            }
        }
        return Unit.INSTANCE;
    }
}