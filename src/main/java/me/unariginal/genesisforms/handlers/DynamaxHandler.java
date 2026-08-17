package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.net.messages.client.battle.BattleTransformPokemonPacket;
import me.unariginal.genesisforms.events.DynamaxStartEvent;
import me.unariginal.genesisforms.events.DynamaxEventEnd;

import java.util.*;

import static me.unariginal.genesisforms.config.ConfigManager.CONFIG;
import static me.unariginal.genesisforms.config.ConfigManager.EVENTS;

public class DynamaxHandler {
    public static void register() {
        DynamaxStartEvent.EVENT.register(((battle, pokemon, gmax) -> {
            // Handle gmax form change
            if (gmax && CONFIG.dynamaxSettings.enableGigantamax) {
                new StringSpeciesFeature("dynamax_form", "gmax").apply(pokemon.getEffectedPokemon());
                for (ActiveBattlePokemon activeBattlePokemon : battle.getActivePokemon()) {
                    if (activeBattlePokemon.getBattlePokemon() != null &&
//                            activeBattlePokemon.getBattlePokemon().getEffectedPokemon().getOwnerPlayer() == pokemon.getEffectedPokemon().getOwnerPlayer() &&
                            activeBattlePokemon.getBattlePokemon() == pokemon) {
                        battle.sendSidedUpdate(
                                activeBattlePokemon.getActor(),
                                new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, true),
                                new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, false),
                                false
                        );
                    }
                }
            }

            if (EVENTS.dynamax != null)
                EVENTS.dynamax.runEvent("global", pokemon.getEffectedPokemon(), pokemon.getEntity());
        }));

        DynamaxEventEnd.EVENT.register((battle, pokemon) -> {
            new StringSpeciesFeature("dynamax_form", "none").apply(pokemon.getEffectedPokemon());
            for (ActiveBattlePokemon activeBattlePokemon : battle.getActivePokemon()) {
                if (activeBattlePokemon.getBattlePokemon() != null &&
//                        activeBattlePokemon.getBattlePokemon().getEffectedPokemon().getOwnerPlayer() == pokemon.getEffectedPokemon().getOwnerPlayer() &&
                        activeBattlePokemon.getBattlePokemon() == pokemon) {
                    battle.sendSidedUpdate(
                            activeBattlePokemon.getActor(),
                            new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, true),
                            new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, false),
                            false
                    );
                }
            }

            if (EVENTS.dynamax != null)
                EVENTS.dynamax.revertEvent("global", pokemon.getEffectedPokemon(), pokemon.getEntity());
        });
    }
}