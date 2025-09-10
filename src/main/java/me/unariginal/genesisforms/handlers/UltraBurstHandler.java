package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.dispatch.UntilDispatch;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.genesisforms.events.UltraBurstEvent;

public class UltraBurstHandler {
    public static void register () {
        UltraBurstEvent.ULTRA_BURST.register(UltraBurstHandler::onUltra);
    }

    private static void onUltra(PokemonBattle battle, BattlePokemon battlePokemon) {
        Pokemon pokemon = battlePokemon.getEffectedPokemon();

        if (pokemon.getSpecies().getName().equals("Necrozma")) {
            battle.dispatchToFront(() -> {
                if (pokemon.getAspects().contains("dusk-fusion") || pokemon.getAspects().contains("dawn-fusion")) {
                    if (pokemon.getFeature("prism_fusion") instanceof StringSpeciesFeature feature) {
                        pokemon.getPersistentData().putString("prism_fusion", feature.getValue());
                        new StringSpeciesFeature("prism_fusion", "ultra").apply(pokemon);
                    }
                }
                return new UntilDispatch(() -> true);
            });
        }
    }
}