package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.instruction.MegaEvolutionEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPostEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.items.helditems.megastones.MegaStoneHeldItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import me.unariginal.genesisforms.utils.ParticleUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class MegaEvolutionHandler {
    private final static GenesisForms gf = GenesisForms.INSTANCE;

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

        Pokemon pokemon = pokemonEntity.getPokemon();
        Species species = MegaStoneHeldItems.getInstance().getMegaStoneSpecies(MegaStoneHeldItems.getInstance().showdownId(pokemon));

        if (pokemonEntity.isBattling() && !fromBattle) {
            gf.logInfo("Battling but not from battle! Not invoking mega evolution.");
            return;
        }

        if (pokemon.getSpecies().getName().equalsIgnoreCase("Rayquaza")) {
            gf.logInfo("This is rayquaza! Checking for dragonascent move.");
            for (Move move : pokemon.getMoveSet().getMoves()) {
                if (move.getName().equalsIgnoreCase("dragonascent")) {
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, "mega", fromBattle);
                }
            }
            return;
        }

        if (species == null) {
            gf.logInfo("No mega stone found! Not invoking mega evolution.");
            return;
        }

        gf.logInfo("Species Name: " + species.getName());

        if (species.getName().equalsIgnoreCase(pokemon.getSpecies().getName())) {
            NbtCompound nbt = NbtUtils.getNbt(pokemon.heldItem(), GenesisForms.MOD_ID);
            if (nbt.isEmpty() || !nbt.contains(DataKeys.NBT_MEGASTONE)) return;
            if (species.getName().equalsIgnoreCase("Charizard")) {
                String nbtString = nbt.getString(DataKeys.NBT_MEGASTONE);
                gf.logInfo("Charizard mega stone nbt: " + nbtString);
                if (nbtString.isEmpty()) return;
                if (nbtString.equalsIgnoreCase("charizardite-x")){
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, "mega_x", fromBattle);
                } else if (nbtString.equalsIgnoreCase("charizardite-y")){
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, "mega_y", fromBattle);
                }
            }
            else if (species.getName().equalsIgnoreCase("Mewtwo")) {
                String nbtString = nbt.getString(DataKeys.NBT_MEGASTONE);
                gf.logInfo("Mewtwo mega stone nbt: " + nbtString);
                if (nbtString.isEmpty()) return;
                if (nbtString.equalsIgnoreCase("mewtwonite-x")){
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, "mega_x", fromBattle);
                } else if (nbtString.equalsIgnoreCase("mewtwonite-y")){
                    changeMegaFormWithAnimation(pokemonEntity, pokemon, player, "mega_y", fromBattle);
                }
            } else {
                changeMegaFormWithAnimation(pokemonEntity, pokemon, player, "mega", fromBattle);
            }
        }
    }

    private static void changeMegaFormWithAnimation(PokemonEntity pokemonEntity, Pokemon pokemon, ServerPlayerEntity player, String form, boolean fromBattle) {
        gf.getPlayersWithMega().put(player.getUuid(), pokemon);
        if (fromBattle) {
            gf.getMegaEvolvedThisBattle().add(player.getUuid());
        }
        ParticleUtils.spawnParticle("cobblemon:mega_evolution", pokemonEntity.getPos().add(0, pokemonEntity.getBoundingBox().getLengthY() / 2, 0), pokemonEntity.getWorld().getRegistryKey());
        pokemonEntity.after(4.9F, () -> {
            new StringSpeciesFeature("mega_evolution", form).apply(pokemon);
            pokemon.setTradeable(false);
            return Unit.INSTANCE;
        });
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

                new StringSpeciesFeature("mega_evolution", "none").apply(pokemon);
                // TODO: Make sure this doesn't break the nuzlocke mod
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
