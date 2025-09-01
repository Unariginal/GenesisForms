package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.events.pokemon.HeldItemEvent;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.items.helditems.HeldFormItem;
import me.unariginal.genesisforms.items.helditems.HeldItems;
import me.unariginal.genesisforms.items.helditems.zcrystals.TypedZCrystalItem;
import me.unariginal.genesisforms.items.helditems.zcrystals.ZCrystalItems;
import net.minecraft.item.ItemStack;

import java.util.List;

public class HeldItemHandler {
    public static Unit heldItemChange(HeldItemEvent.Post post) {
        if (post.getReturned() == post.getReceived() || post.getPokemon().getOwnerPlayer() == null) return Unit.INSTANCE;

        Pokemon pokemon = post.getPokemon();
        if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"))) MegaEvolutionHandler.devolveMega(pokemon, true);

        ItemStack received = post.getReceived();
        ItemStack returned = post.getReturned();

        String heldItemID = "";

        if (returned.getComponents().contains(DataComponents.Z_CRYSTAL)) {
            heldItemID = returned.getComponents().get(DataComponents.Z_CRYSTAL);
            if (heldItemID == null || heldItemID.isEmpty()) return Unit.INSTANCE;

            if (ZCrystalItems.getInstance().typedZCrystalItems.containsKey(heldItemID)) {
                TypedZCrystalItem typedZCrystalItem = ZCrystalItems.getInstance().typedZCrystalItems.get(heldItemID);

                for (FormSetting formSetting : typedZCrystalItem.formChanges()) {
                    for (String species : formSetting.species()) {
                        if (pokemon.getSpecies().getName().equalsIgnoreCase(species)) {
                            if (formSetting.defaultValue().equalsIgnoreCase("true") || formSetting.defaultValue().equalsIgnoreCase("false")) {
                                new FlagSpeciesFeature(formSetting.featureName(), Boolean.getBoolean(formSetting.defaultValue())).apply(pokemon);
                            } else {
                                new StringSpeciesFeature(formSetting.featureName(), formSetting.defaultValue()).apply(pokemon);
                            }
                        }
                    }
                }
            }
        } else if (returned.getComponents().contains(DataComponents.HELD_ITEM)) {
            heldItemID = returned.getComponents().get(DataComponents.HELD_ITEM);
            if (heldItemID == null || heldItemID.isEmpty()) return Unit.INSTANCE;

            if (HeldItems.getInstance().heldFormItems.containsKey(heldItemID)) {
                revertHeldItemForm(pokemon, HeldItems.getInstance().heldFormItems.get(heldItemID).formSetting());
            }
        }

        if (received.getComponents().contains(DataComponents.HELD_ITEM)) {
            heldItemID = received.getComponents().get(DataComponents.HELD_ITEM);
            if (heldItemID == null || heldItemID.isEmpty()) return Unit.INSTANCE;

            if (GenesisForms.INSTANCE.getConfig().disabledItems.contains(heldItemID)) return Unit.INSTANCE;

            List<Species> speciesList = HeldItems.getInstance().getHeldItemSpecies(heldItemID);
            if (speciesList.isEmpty()) return Unit.INSTANCE;

            if (speciesList.contains(pokemon.getSpecies())) {
                if (HeldItems.getInstance().heldFormItems.containsKey(heldItemID)) {
                    HeldFormItem heldFormItem = HeldItems.getInstance().heldFormItems.get(heldItemID);
                    if (heldFormItem.formSetting().alternateValue().equalsIgnoreCase("true") || heldFormItem.formSetting().alternateValue().equalsIgnoreCase("false")) {
                        new FlagSpeciesFeature(heldFormItem.formSetting().featureName(), Boolean.getBoolean(heldFormItem.formSetting().alternateValue())).apply(pokemon);
                    } else {
                        new StringSpeciesFeature(heldFormItem.formSetting().featureName(), heldFormItem.formSetting().alternateValue()).apply(pokemon);
                    }
                }
            } else if (returned.getComponents().contains(DataComponents.HELD_ITEM)) {
                heldItemID = received.getComponents().get(DataComponents.HELD_ITEM);
                if (heldItemID == null || heldItemID.isEmpty()) return Unit.INSTANCE;

                if (HeldItems.getInstance().heldFormItems.containsKey(heldItemID)) {
                    revertHeldItemForm(pokemon, HeldItems.getInstance().heldFormItems.get(heldItemID).formSetting());
                }
            }
        } else if (received.getComponents().contains(DataComponents.Z_CRYSTAL)) {
            heldItemID = received.getComponents().get(DataComponents.Z_CRYSTAL);
            if (heldItemID == null || heldItemID.isEmpty()) return Unit.INSTANCE;

            if (ZCrystalItems.getInstance().typedZCrystalItems.containsKey(heldItemID)) {
                TypedZCrystalItem typedZCrystalItem = ZCrystalItems.getInstance().typedZCrystalItems.get(heldItemID);

                for (FormSetting formSetting : typedZCrystalItem.formChanges()) {
                    for (String species : formSetting.species()) {
                        if (pokemon.getSpecies().getName().equalsIgnoreCase(species)) {
                            if (formSetting.alternateValue().equalsIgnoreCase("true") || formSetting.alternateValue().equalsIgnoreCase("false")) {
                                new FlagSpeciesFeature(formSetting.featureName(), Boolean.getBoolean(formSetting.alternateValue())).apply(pokemon);
                            } else {
                                new StringSpeciesFeature(formSetting.featureName(), formSetting.alternateValue()).apply(pokemon);
                            }
                        }
                    }
                }
            }
        }

        if (GenesisForms.INSTANCE.getConfig().fixOgerponTeraType) {
            if (pokemon.getSpecies().getName().equalsIgnoreCase("ogerpon")) {
                fixOgerponTeraType(pokemon, heldItemID);
            }
        }

        return Unit.INSTANCE;
    }

    public static void fixOgerponTeraType(Pokemon pokemon, String heldItemID) {
        // TODO: Allow custom ogerpon item tera type changing
        switch (heldItemID) {
            case "hearthflame_mask" -> pokemon.setTeraType(TeraTypes.getFIRE());
            case "wellspring_mask" -> pokemon.setTeraType(TeraTypes.getWATER());
            case "cornerstone_mask" -> pokemon.setTeraType(TeraTypes.getROCK());
            default -> pokemon.setTeraType(TeraTypes.getGRASS());
        }
    }

    public static void revertHeldItemForm(Pokemon pokemon, FormSetting formSetting) {
        if (formSetting.species().contains(pokemon.getSpecies().getName().toLowerCase())) {
            if (formSetting.defaultValue().equalsIgnoreCase("true") || formSetting.defaultValue().equalsIgnoreCase("false")) {
                new FlagSpeciesFeature(formSetting.featureName(), Boolean.getBoolean(formSetting.defaultValue())).apply(pokemon);
            } else {
                new StringSpeciesFeature(formSetting.featureName(), formSetting.defaultValue()).apply(pokemon);
            }
        }
    }
}