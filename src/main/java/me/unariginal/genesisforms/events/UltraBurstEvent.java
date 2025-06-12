package me.unariginal.genesisforms.events;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import me.unariginal.genesisforms.GenesisForms;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

/**
 * Adapted from GMG/Mega Showdown @ yajatkaul
 */
public interface UltraBurstEvent {
    Event<UltraBurst> ULTRA_BURST = EventFactory.createWithPhases(
            UltraBurst.class,
            listeners -> (battle, pokemon) -> {
                for (UltraBurst listener : listeners) {
                    listener.onUltraBurst(battle, pokemon);
                }
            },
            Identifier.of(GenesisForms.MOD_ID, "earliest"), Identifier.of(GenesisForms.MOD_ID, "pre_default"), Event.DEFAULT_PHASE, Identifier.of(GenesisForms.MOD_ID, "post_default"), Identifier.of(GenesisForms.MOD_ID, "latest"));

    @FunctionalInterface
    interface UltraBurst {
        void onUltraBurst(PokemonBattle battle, BattlePokemon pokemon);
    }
}
