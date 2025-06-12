package me.unariginal.genesisforms.events;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Adapted from Mega Showdown @ yajatkaul
 */
public interface DynamaxEventEnd {
    Event<DynamaxEventEnd> EVENT = EventFactory.createArrayBacked(DynamaxEventEnd.class,
            listeners -> (battle, pokemon) -> {
                for (DynamaxEventEnd listener : listeners) {
                    listener.onDynamaxEnd(battle, pokemon);
                }
            }
    );

    void onDynamaxEnd(PokemonBattle battle, BattlePokemon pokemon);
}
