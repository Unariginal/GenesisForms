package me.unariginal.genesisforms.events;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface UltraBurstEvent {
    Event<UltraBurstEvent> ULTRA_BURST = EventFactory.createArrayBacked(UltraBurstEvent.class, listeners -> (battle, pokemon) -> {
        for (UltraBurstEvent listener : listeners) {
            listener.onUltraBurst(battle, pokemon);
        }
    });

    void onUltraBurst(PokemonBattle battle, BattlePokemon pokemon);
}