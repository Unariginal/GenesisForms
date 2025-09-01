package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.battles.instruction.FormeChangeEvent;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.BattleFormChanges;
import net.minecraft.server.network.ServerPlayerEntity;

public class FormHandler {
    private final static GenesisForms gf = GenesisForms.INSTANCE;

    public static void revertForms(Pokemon pokemon, boolean fromBattle) {
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

        if (isGmax) new StringSpeciesFeature("dynamax_form", "none").apply(pokemon);

        if (pokemon.getAspects().contains("ultra-fusion")) {
            new StringSpeciesFeature("prism_fusion", pokemon.getPersistentData().getString("prism_fusion")).apply(pokemon);
            pokemon.getPersistentData().remove("prism_fusion");
        }

        for (BattleFormChanges.BattleFormInformation battleFormInformation : BattleFormChanges.battleForms.values()) {
            if (battleFormInformation.species.equalsIgnoreCase(pokemon.getSpecies().getName())) {
                if (!pokemon.getSpecies().getName().equals("Greninja") || pokemon.getAspects().contains("ash")) {
                    if (battleFormInformation.defaultForm.featureValue.equalsIgnoreCase("true") || battleFormInformation.defaultForm.featureValue.equalsIgnoreCase("false"))
                        new FlagSpeciesFeature(battleFormInformation.defaultForm.featureName, Boolean.getBoolean(battleFormInformation.defaultForm.featureValue)).apply(pokemon);
                    else
                        new StringSpeciesFeature(battleFormInformation.defaultForm.featureName, battleFormInformation.defaultForm.featureValue).apply(pokemon);
                }
            }
        }

        if (pokemon.getSpecies().getName().equals("Ogerpon")) new FlagSpeciesFeature("embody_aspect", false).apply(pokemon);

        pokemon.updateAspects();
    }

    public static Unit formChanges(FormeChangeEvent event) {
        String formName = event.getFormeName();
        if (formName.equals("x") || formName.equals("y") || formName.equals("mega")) return Unit.INSTANCE;

        Pokemon pokemon = event.getPokemon().getEffectedPokemon();
        PokemonBattle battle = event.getBattle();

        gf.logInfo("Pokemon \"" + pokemon.getSpecies().showdownId() + "\" has changed forme to: " + formName);

        if (pokemon.getSpecies().showdownId().equalsIgnoreCase("zygarde") && formName.equalsIgnoreCase("complete")) {
            if (pokemon.getAspects().contains("10-percent")) pokemon.getPersistentData().putString("percent_cells", "10");
            else pokemon.getPersistentData().putString("percent_cells", "50");
            new StringSpeciesFeature("percent_cells", "complete").apply(pokemon);
        }

        for (BattleFormChanges.BattleFormInformation battleFormInformation : BattleFormChanges.battleForms.values()) {
            if (battleFormInformation.species.equalsIgnoreCase(pokemon.getSpecies().showdownId())) {
                if (battleFormInformation.forms.containsKey(formName)) {
                    if (battleFormInformation.forms.get(formName).featureValue.equalsIgnoreCase("true") || battleFormInformation.forms.get(formName).featureValue.equalsIgnoreCase("false"))
                        new FlagSpeciesFeature(battleFormInformation.forms.get(formName).featureName, Boolean.getBoolean(battleFormInformation.forms.get(formName).featureValue)).apply(pokemon);
                    else
                        new StringSpeciesFeature(battleFormInformation.forms.get(formName).featureName, battleFormInformation.forms.get(formName).featureValue).apply(pokemon);
                    break;
                }
            }
        }

        PacketHandler.updatePackets(battle, event.getPokemon(), false);

        return Unit.INSTANCE;
    }
}