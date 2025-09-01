package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.events.battles.instruction.TerastallizationEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentEvent;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
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

    public static Unit revertTera(PokemonSentEvent.Post event) {
        Pokemon pokemon = event.getPokemon();
        if (pokemon.getSpecies().getName().equalsIgnoreCase("Terapagos")) {
            new StringSpeciesFeature("tera_form", "terastal").apply(pokemon);
        }
        if (pokemon.getSpecies().getName().equalsIgnoreCase("Ogerpon")) {
            new FlagSpeciesFeature("embody_aspect", false).apply(pokemon);
        }
        return Unit.INSTANCE;
    }

    public static Unit setProperTeraTypes(PokemonGainedEvent event) {
        if (event.getPokemon().getSpecies().getName().equalsIgnoreCase("Terapagos") && gf.getConfig().fixTerapagosTeraType) {
            event.getPokemon().setTeraType(TeraTypes.getSTELLAR());
        }
        if (event.getPokemon().getSpecies().getName().equalsIgnoreCase("Ogerpon") && gf.getConfig().fixOgerponTeraType) {
            gf.logInfo("[Genesis] Ogerpon Aspects " + event.getPokemon().getAspects());
            if (event.getPokemon().getAspects().contains("cornerstone-mask")) {
                event.getPokemon().setTeraType(TeraTypes.getROCK());
            } else if (event.getPokemon().getAspects().contains("hearthflame-mask")) {
                event.getPokemon().setTeraType(TeraTypes.getFIRE());
            } else if (event.getPokemon().getAspects().contains("wellspring-mask")) {
                event.getPokemon().setTeraType(TeraTypes.getWATER());
            } else {
                event.getPokemon().setTeraType(TeraTypes.getGRASS());
            }
        }
        return Unit.INSTANCE;
    }
}