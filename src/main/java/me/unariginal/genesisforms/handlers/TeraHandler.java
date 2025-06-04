package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.events.battles.instruction.TerastallizationEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPostEvent;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
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
            switch (teraType.getId().getPath()) {
                case "normal" -> {}
                case "fighting" -> {}
                case "flying" -> {}
                case "poison" -> {}
                case "ground" -> {}
                case "rock" -> {}
                case "bug" -> {}
                case "ghost" -> {}
                case "steel" -> {}
                case "fire" -> {
//                    ParticleUtils.spawnParticle("cobblemon:tera_fire_small", pokemonEntity.getPos().add(0.5, pokemonEntity.getBoundingBox().getLengthY(), 0.5), pokemonEntity.getWorld().getRegistryKey());
//                    ParticleUtils.spawnParticle("cobblemon:tera_fire_small", pokemonEntity.getPos().add(-0.5, pokemonEntity.getBoundingBox().getLengthY(), 0.5), pokemonEntity.getWorld().getRegistryKey());
//                    ParticleUtils.spawnParticle("cobblemon:tera_fire_small", pokemonEntity.getPos().add(0.5, pokemonEntity.getBoundingBox().getLengthY(), -0.5), pokemonEntity.getWorld().getRegistryKey());
//                    ParticleUtils.spawnParticle("cobblemon:tera_fire_small", pokemonEntity.getPos().add(-0.5, pokemonEntity.getBoundingBox().getLengthY(), -0.5), pokemonEntity.getWorld().getRegistryKey());
                }
                case "water" -> {}
                case "grass" -> {}
                case "electric" -> {}
                case "psychic" -> {}
                case "ice" -> {}
                case "dragon" -> {}
                case "dark" -> {}
                case "fairy" -> {}
                case "stellar" -> {}
            }
        }
        return Unit.INSTANCE;
    }

    public static Unit revertTera(PokemonSentPostEvent event) {
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
