package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.battles.instruction.FormeChangeEvent;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import net.minecraft.server.network.ServerPlayerEntity;

public class FormHandler {
    private final static GenesisForms gf = GenesisForms.INSTANCE;

    public static void revert_forms(Pokemon pokemon, boolean fromBattle) {
        boolean isMega = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
        boolean isGmax = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("gmax"));

        ServerPlayerEntity player = pokemon.getOwnerPlayer();
        if (isMega) {
            if (player != null) {
                gf.getPlayersWithMega().remove(player.getUuid());
                gf.getMegaEvolvedThisBattle().remove(player.getUuid());
            }
            MegaEvolutionHandler.devolveMega(pokemon, fromBattle);
        }

        if (isGmax) {
            new StringSpeciesFeature("dynamax_form", "none").apply(pokemon);
        }

        if (pokemon.getAspects().contains("ultra-fusion")) {
            new StringSpeciesFeature("prism_fusion", pokemon.getPersistentData().getString("prism_fusion")).apply(pokemon);
            pokemon.getPersistentData().remove("prism_fusion");
        }

        switch (pokemon.getSpecies().getName()) {
            case "Aegislash" -> new StringSpeciesFeature("stance_forme", "shield").apply(pokemon);
            case "Castform" -> new StringSpeciesFeature("forecast_form", "normal").apply(pokemon);
            case "Cherrim" -> new StringSpeciesFeature("blossom_form", "overcast").apply(pokemon);
            case "Cramorant" -> new StringSpeciesFeature("missile_form", "none").apply(pokemon);
            case "Darmanitan" -> new StringSpeciesFeature("blazing_mode", "standard").apply(pokemon);
            case "Eiscue" -> new StringSpeciesFeature("penguin_head", "ice_face").apply(pokemon);
            case "Greninja" -> {
                if (pokemon.getAspects().contains("ash")) {
                    new StringSpeciesFeature("battle_bond", "bond").apply(pokemon);
                }
            }
            case "Meloetta" -> new StringSpeciesFeature("song_forme", "aria").apply(pokemon);
            case "Mimikyu" -> new StringSpeciesFeature("disguise_form", "disguised").apply(pokemon);
            case "Morpeko" -> new StringSpeciesFeature("hunger_mode", "full_belly").apply(pokemon);
            case "Palafin" -> new StringSpeciesFeature("dolphin_form", "zero").apply(pokemon);
            case "Terapagos" -> new StringSpeciesFeature("tera_form", "normal").apply(pokemon);
            case "Ogerpon" -> new FlagSpeciesFeature("embody_aspect", false).apply(pokemon);
            case "Wishiwashi" -> new StringSpeciesFeature("schooling_form", "solo").apply(pokemon);
            case "Xerneas" -> new StringSpeciesFeature("life_mode", "neutral").apply(pokemon);
            case "Zygarde" -> {
                if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("complete"))) {
                    new FlagSpeciesFeature("power_construct", false).apply(pokemon);
                    new StringSpeciesFeature("percent_cells", "50").apply(pokemon);
                }
            }
        }
        pokemon.updateAspects();
    }

    public static Unit form_changes(FormeChangeEvent event) {
        String formName = event.getFormeName();
        if (formName.equals("x") || formName.equals("y") || formName.equals("mega")) {
            return Unit.INSTANCE;
        }

        Pokemon pokemon = event.getPokemon().getEffectedPokemon();
        PokemonBattle battle = event.getBattle();

        gf.logInfo("Pokemon: " +  pokemon.getSpecies().getName() + " has changed forme to: " + formName);

        switch (pokemon.getSpecies().getName()) {
            case "Aegislash" -> {
                if (formName.equals("blade")) {
                    new StringSpeciesFeature("stance_forme", "blade").apply(pokemon);
                } else {
                    new StringSpeciesFeature("stance_forme", "shield").apply(pokemon);
                }
            }
            case "Arceus" -> new StringSpeciesFeature("multitype", formName).apply(pokemon);
            case "Minior" -> {
                if (formName.equals("meteor")) {
                    new StringSpeciesFeature("meteor_shield", "meteor").apply(pokemon);
                } else {
                    new StringSpeciesFeature("meteor_shield", "core").apply(pokemon);
                }
            }
            case "Castform" -> {
                switch (formName) {
                    case "sunny" -> new StringSpeciesFeature("forecast_form", "sunny").apply(pokemon);
                    case "rainy" -> new StringSpeciesFeature("forecast_form", "rainy").apply(pokemon);
                    case "snowy" -> new StringSpeciesFeature("forecast_form", "snowy").apply(pokemon);
                    default -> new StringSpeciesFeature("forecast_form", "normal").apply(pokemon);
                }
            }
            case "Wishiwashi" -> {
                if (formName.equals("school")) {
                    new StringSpeciesFeature("schooling_form", "school").apply(pokemon);
                } else {
                    new StringSpeciesFeature("schooling_form", "solo").apply(pokemon);
                }
            }
            case "Mimikyu" -> {
                if (formName.equals("busted")) {
                    new StringSpeciesFeature("disguise_form", "busted").apply(pokemon);
                } else {
                    new StringSpeciesFeature("disguise_form", "disguised").apply(pokemon);
                }
            }
            case "Greninja" -> {
                if (formName.equals("ash")) {
                    new StringSpeciesFeature("battle_bond", "ash").apply(pokemon);
                }
            }
            case "Cherrim" -> {
                if (formName.equals("sunshine")) {
                    new StringSpeciesFeature("blossom_form", "sunshine").apply(pokemon);
                } else {
                    new StringSpeciesFeature("blossom_form", "overcast").apply(pokemon);
                }
            }
            case "Palafin" -> {
                if (formName.equals("hero")) {
                    new StringSpeciesFeature("dolphin_form", "hero").apply(pokemon);
                } else {
                    new StringSpeciesFeature("dolphin_form", "zero").apply(pokemon);
                }
            }
            case "Morpeko" -> {
                if (formName.equals("hangry")) {
                    new StringSpeciesFeature("hunger_mode", "hangry").apply(pokemon);
                } else {
                    new StringSpeciesFeature("hunger_mode", "full_belly").apply(pokemon);
                }
            }
            case "Eiscue" -> {
                if (formName.equals("noice")) {
                    new StringSpeciesFeature("penguin_head", "noice_face").apply(pokemon);
                } else {
                    new StringSpeciesFeature("penguin_head", "ice_face").apply(pokemon);
                }
            }
            case "Cramorant" -> {
                switch (formName) {
                    case "gulping" -> new StringSpeciesFeature("missile_form", "gulping").apply(pokemon);
                    case "gorging" -> new StringSpeciesFeature("missile_form", "gorging").apply(pokemon);
                    default -> new StringSpeciesFeature("missile_form", "none").apply(pokemon);
                }
            }
            case "Darmanitan" -> {
                if (formName.equals("zen")) {
                    new StringSpeciesFeature("blazing_mode", "zen").apply(pokemon);
                } else {
                    new StringSpeciesFeature("blazing_mode", "standard").apply(pokemon);
                }
            }
            case "Xerneas" -> {
                if (formName.equals("active")) {
                    new StringSpeciesFeature("life_mode", "active").apply(pokemon);
                } else {
                    new StringSpeciesFeature("life_mode", "neutral").apply(pokemon);
                }
            }
            case "Terapagos" -> {
                if (formName.equals("terastal")) {
                    new StringSpeciesFeature("tera_form", "terastal").apply(pokemon);
                } else {
                    new StringSpeciesFeature("tera_form", "normal").apply(pokemon);
                }
            }
            case "Meloetta" -> {
                if (formName.equals("pirouette")) {
                    new StringSpeciesFeature("song_forme", "pirouette").apply(pokemon);
                } else {
                    new StringSpeciesFeature("song_forme", "aria").apply(pokemon);
                }
            }
            case "Zygarde" -> {
                if (formName.equals("complete")) {
                    new FlagSpeciesFeature("power_construct", true).apply(pokemon);
                    new StringSpeciesFeature("percent_cells", "complete").apply(pokemon);
                }
            }
        }

        PacketHandler.update_packets(battle, event.getPokemon(), false);

        return Unit.INSTANCE;
    }
}
