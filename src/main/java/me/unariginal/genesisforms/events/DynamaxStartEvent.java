package me.unariginal.genesisforms.events;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface DynamaxStartEvent {
    Event<DynamaxStartEvent> EVENT = EventFactory.createArrayBacked(DynamaxStartEvent.class, listeners -> (battle, pokemon, gmax) -> {
        for (DynamaxStartEvent listener : listeners) {
            listener.onDynamaxStart(battle, pokemon, gmax);
        }
    });

    void onDynamaxStart(PokemonBattle battle, BattlePokemon pokemon, Boolean gmax);
}