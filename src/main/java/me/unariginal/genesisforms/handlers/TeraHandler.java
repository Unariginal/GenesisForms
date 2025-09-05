package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.events.battles.instruction.TerastallizationEvent;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.AnimationConfig;
import me.unariginal.genesisforms.utils.ParticleUtils;

public class TeraHandler {
    private final static GenesisForms gf = GenesisForms.INSTANCE;

    public static Unit teraEvent(TerastallizationEvent event) {
        BattlePokemon battlePokemon = event.getPokemon();
        Pokemon pokemon = battlePokemon.getEffectedPokemon();
        PokemonEntity pokemonEntity = battlePokemon.getEntity();

        if (pokemon.getSpecies().getName().equalsIgnoreCase("Terapagos")) {
            new StringSpeciesFeature("tera_form", "stellar").apply(pokemon);
        }
        if (pokemon.getSpecies().getName().equalsIgnoreCase("Ogerpon")) {
            new FlagSpeciesFeature("embody_aspect", true).apply(pokemon);
        }

        if (pokemonEntity != null) {
            TeraType teraType = event.getTeraType();
            gf.getTeraPokemonEntities().put(pokemonEntity.getUuid(), teraType.getId().getPath());
            for (AnimationConfig.TeraAnimation teraAnimation : gf.getAnimationConfig().teraAnimations) {
                if (teraAnimation.type().equalsIgnoreCase(teraType.getId().getPath())) {
                    if (teraAnimation.enabled()) {
                        ParticleUtils.spawnParticle(teraAnimation.identifier(), pokemonEntity.getPos(), pokemonEntity.getWorld().getRegistryKey());
                    }
                    break;
                }
            }
        }
        return Unit.INSTANCE;
    }
}