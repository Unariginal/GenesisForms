package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.FormeChangeEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.MegaEvolutionEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.TerastallizationEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.ZMoveUsedEvent;
import com.cobblemon.mod.common.api.events.pokemon.HeldItemEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPostEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.BattleFormChangeConfig;
import me.unariginal.genesisforms.config.EventsConfig;
import me.unariginal.genesisforms.items.helditems.HeldFormItem;
import me.unariginal.genesisforms.items.helditems.Megastone;
import me.unariginal.genesisforms.items.helditems.ZCrystal;
import me.unariginal.genesisforms.items.keyitems.accessories.DynamaxAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.MegaAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.TeraAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.ZAccessory;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class CobblemonEventHandler {
    public static Unit battleStartEvent(BattleStartedPreEvent event) {
        PokemonBattle battle = event.getBattle();
        for (ServerPlayerEntity player : battle.getPlayers()) {
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : party) {
                if (pokemon == null) continue;
                revertForm(pokemon, false);
            }

            List<ItemStack> inventory = getValidKeyItemSlots(player);

            boolean hasMega = false;
            boolean hasTera = false;
            boolean hasZ = false;
            boolean hasDmax = false;
            for (ItemStack itemStack : inventory) {
                if (itemStack.getItem() instanceof MegaAccessory && GenesisForms.INSTANCE.getConfig().enableMegaEvolution) {
                    hasMega = true;
                } else if (itemStack.getItem() instanceof TeraAccessory && GenesisForms.INSTANCE.getConfig().enableTera) {
                    if (itemStack.getDamage() != itemStack.getMaxDamage()) {
                        hasTera = true;
                    }
                } else if (itemStack.getItem() instanceof ZAccessory && GenesisForms.INSTANCE.getConfig().enableZCrystals) {
                    hasZ = true;
                } else if (itemStack.getItem() instanceof DynamaxAccessory && GenesisForms.INSTANCE.getConfig().enableDynamax) {
                    hasDmax = true;
                }
            }

            GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
            playerData.getKeyItems().removeIf(identifier ->
                    identifier.equals(MiscUtilsKt.cobblemonResource("key_stone")) ||
                            identifier.equals(MiscUtilsKt.cobblemonResource("tera_orb")) ||
                            identifier.equals(MiscUtilsKt.cobblemonResource("z_ring")) ||
                            identifier.equals(MiscUtilsKt.cobblemonResource("dynamax_band")));

            if (hasMega) playerData.getKeyItems().add(MiscUtilsKt.cobblemonResource("key_stone"));
            if (hasTera) playerData.getKeyItems().add(MiscUtilsKt.cobblemonResource("tera_orb"));
            if (hasZ) playerData.getKeyItems().add(MiscUtilsKt.cobblemonResource("z_ring"));
            if (hasDmax && !hasTera) playerData.getKeyItems().add(MiscUtilsKt.cobblemonResource("dynamax_band"));
        }

        return Unit.INSTANCE;
    }

    public static List<ItemStack> getValidKeyItemSlots(ServerPlayerEntity player) {
        List<ItemStack> inventory = new ArrayList<>();

        for (ItemStack itemStack : player.getInventory().main) {
            if ((GenesisForms.INSTANCE.getConfig().useMainInventory &&
                    (GenesisForms.INSTANCE.getConfig().useHotbarInventory ||
                            !PlayerInventory.isValidHotbarIndex(player.getInventory().indexOf(itemStack)))) ||
                    GenesisForms.INSTANCE.getConfig().useHotbarInventory &&
                            PlayerInventory.isValidHotbarIndex(player.getInventory().indexOf(itemStack))) {
                inventory.add(itemStack);
            }
        }
        if (GenesisForms.INSTANCE.getConfig().useMainHandInventory && !inventory.contains(player.getMainHandStack())) {
            inventory.add(player.getMainHandStack());
        }
        if (GenesisForms.INSTANCE.getConfig().useOffHandInventory && !inventory.contains(player.getOffHandStack())) {
            inventory.add(player.getOffHandStack());
        }
        if (GenesisForms.INSTANCE.getConfig().useArmorInventory) {
            for (ItemStack itemStack : player.getArmorItems()) {
                if (!inventory.contains(itemStack)) {
                    inventory.add(itemStack);
                }
            }
        }
        for (int slot : GenesisForms.INSTANCE.getConfig().specificSlots) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (!inventory.contains(stack)) {
                inventory.add(stack);
            }
        }

        return inventory;
    }

    public static Unit battleEndEvent(BattleVictoryEvent event) {
        PokemonBattle battle = event.getBattle();
        for (ServerPlayerEntity player : battle.getPlayers()) {
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : party) {
                if (pokemon == null) continue;
                revertForm(pokemon, false);
            }
        }
        return Unit.INSTANCE;
    }

    public static Unit battleFledEvent(BattleFledEvent event) {
        PokemonBattle battle = event.getBattle();
        for (ServerPlayerEntity player : battle.getPlayers()) {
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : party) {
                if (pokemon == null) continue;
                revertForm(pokemon, false);
            }
        }
        return Unit.INSTANCE;
    }

    public static Unit battleFaintEvent(BattleFaintedEvent event) {
        Pokemon pokemon = event.getKilled().getEffectedPokemon();
        if (pokemon.isPlayerOwned()) {
            revertForm(pokemon, true);
        }
        return Unit.INSTANCE;
    }

    public static Unit formChangeEvent(FormeChangeEvent event) {
        String formName = event.getFormeName();
        BattlePokemon battlePokemon = event.getPokemon();
        Pokemon pokemon = battlePokemon.getEffectedPokemon();
        PokemonBattle battle = event.getBattle();

        if (pokemon.getSpecies().getName().equalsIgnoreCase("zygarde") && formName.equalsIgnoreCase("complete")) {
            if (pokemon.getAspects().contains("10-percent")) pokemon.getPersistentData().putString("percent_cells", "10");
            else pokemon.getPersistentData().putString("percent_cells", "50");
            new StringSpeciesFeature("percent_cells", "complete").apply(pokemon);
        }

        for (BattleFormChangeConfig.BattleFormInformation battleFormInformation : BattleFormChangeConfig.battleForms.values()) {
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

        PacketHandler.updatePackets(battle, battlePokemon, false);

        return Unit.INSTANCE;
    }

    public static void revertForm(Pokemon pokemon, boolean fromBattle) {
        // Reverting megas
        Item heldItem = pokemon.heldItem().getItem();
        if (heldItem instanceof Megastone megastone) {
            if (revertMega(pokemon, megastone.getMegastoneData().featureName)) {
                EventsConfig.gimmickEvents.megaEvolution.revertEvent(megastone.getItemID(), pokemon, pokemon.getEntity());
            }
        } else {
            if (revertMega(pokemon, "mega_evolution")) {
                EventsConfig.gimmickEvents.megaEvolution.revertEvent("rayquaza", pokemon, pokemon.getEntity());
            }

            if (heldItem instanceof ZCrystal zcrystal) {
                EventsConfig.gimmickEvents.zPower.revertEvent(zcrystal.getItemID(), pokemon, pokemon.getEntity());
            }
        }

        // Reverting ultra fusion & dmax
        if (pokemon.getFeatures().removeIf(speciesFeature -> speciesFeature.getName().equalsIgnoreCase("dynamax_form"))) {
            PokemonEntity pokemonEntity = pokemon.getEntity();
            if (pokemonEntity != null) {
                EntityAttributeInstance scaleAttribute = pokemonEntity.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                if (scaleAttribute != null) {
                    scaleAttribute.setBaseValue(1.0f);
                }
            }
        }

        boolean ultra = pokemon.getFeatures().stream().anyMatch(speciesFeature -> {
            if (speciesFeature.getName().equalsIgnoreCase("prism_fusion")) {
                if (speciesFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                    return stringSpeciesFeature.getValue().equalsIgnoreCase("ultra");
                }
            }
            return false;
        });

        if (ultra) {
            new StringSpeciesFeature("prism_fusion", pokemon.getPersistentData().getString("prism_fusion")).apply(pokemon);
            pokemon.getPersistentData().remove("prism_fusion");
        }

        // Revert zygarde complete form
        boolean wasComplete = false;
        if (pokemon.getSpecies().getName().equalsIgnoreCase("zygarde")) {
            if (pokemon.getFeatures().stream().anyMatch(speciesFeature -> {
                if (speciesFeature.getName().equalsIgnoreCase("percent_cells") && speciesFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                    return stringSpeciesFeature.getValue().equalsIgnoreCase("complete");
                }
                return false;
            })) {
                if (pokemon.getPersistentData().contains("percent_cells")) {
                    new StringSpeciesFeature("percent_cells", pokemon.getPersistentData().getString("percent_cells")).apply(pokemon);
                } else {
                    new StringSpeciesFeature("percent_cells", "50").apply(pokemon);
                }
                wasComplete = true;
            }
        }

        // Reverting battle forms. fromBattle is currently only true for fainting pokemon
        if (!fromBattle) {
            for (BattleFormChangeConfig.BattleFormInformation battleFormInformation : BattleFormChangeConfig.battleForms.values()) {
                if (battleFormInformation.species.equalsIgnoreCase(pokemon.getSpecies().getName())) {
                    if (!pokemon.getSpecies().getName().equals("Greninja") || pokemon.getAspects().contains("ash")) {
                        if (battleFormInformation.defaultForm.featureValue.equalsIgnoreCase("true") || battleFormInformation.defaultForm.featureValue.equalsIgnoreCase("false"))
                            new FlagSpeciesFeature(battleFormInformation.defaultForm.featureName, Boolean.getBoolean(battleFormInformation.defaultForm.featureValue)).apply(pokemon);
                        else
                            new StringSpeciesFeature(battleFormInformation.defaultForm.featureName, battleFormInformation.defaultForm.featureValue).apply(pokemon);
                    }
                }
            }

            if (pokemon.getSpecies().getName().equals("Ogerpon"))
                new FlagSpeciesFeature("embody_aspect", false).apply(pokemon);
        }

        // Reverting event features
        if (pokemon.getPersistentData().contains("tera_type")) {
            String teraType = pokemon.getPersistentData().getString("tera_type");
            pokemon.getPersistentData().remove("tera_type");

            EventsConfig.gimmickEvents.terastallization.revertEvent(teraType, pokemon, pokemon.getEntity());
        }

        pokemon.updateAspects();
        pokemon.updateForm();

        if (wasComplete) {
            AbilityTemplate powerconstruct = Abilities.INSTANCE.get("powerconstruct");
            pokemon.setAbility$common(powerconstruct.create(false, Priority.LOW));
        }
    }

    public static Unit pokemonSentEvent(PokemonSentPostEvent event) {
        if (event.getPokemon().getPersistentData().contains("glow_id") && event.getPokemon().getPersistentData().contains("glow_color")) {
            String glowID = event.getPokemon().getPersistentData().getString("glow_id");
            String glowColor = event.getPokemon().getPersistentData().getString("glow_color");
            GlowHandler.applyGlowing(glowID, glowColor, event.getPokemon(), event.getPokemonEntity());
        }
        return Unit.INSTANCE;
    }

    public static Unit heldItemChange(HeldItemEvent.Post event) {
        ItemStack received = event.getReceived();
        ItemStack returned = event.getReturned();
        Pokemon pokemon = event.getPokemon();

        if (received == returned) return Unit.INSTANCE;

        // Revert all forms of the pokemon relating to held items (mega, held form items, z crystals)
        revertFormByItem(pokemon, returned.getItem());

        // Change forms
        if (received.getItem() instanceof ZCrystal zCrystal) {
            GenesisForms.INSTANCE.logError("[Genesis] This is a Z Crystal!");
            zCrystal.getFormChanges().forEach(formChange -> {
                boolean speciesMatch = false;
                for (String species : formChange.species) {
                    if (pokemon.getSpecies().getName().equalsIgnoreCase(species)) {
                        speciesMatch = true;
                        break;
                    }
                }
                if (speciesMatch) {
                    GenesisForms.INSTANCE.logError("[Genesis] Species match! Name: " + formChange.featureName + " Value: " + formChange.alternateValue);
                    if (formChange.alternateValue.equalsIgnoreCase("true") || formChange.alternateValue.equalsIgnoreCase("false")) {
                        new FlagSpeciesFeature(formChange.featureName, Boolean.getBoolean(formChange.alternateValue)).apply(pokemon);
                    } else {
                        new StringSpeciesFeature(formChange.featureName, formChange.alternateValue).apply(pokemon);
                    }
                    pokemon.updateAspects();
                    pokemon.updateForm();
                }
            });
        }

        if (received.getItem() instanceof HeldFormItem heldFormItem) {
            GenesisForms.INSTANCE.logError("[Genesis] This is a Held Form Item!");
            if (heldFormItem.getSpeciesList().contains(pokemon.getSpecies())) {
                GenesisForms.INSTANCE.logError("[Genesis] Species match! Name: " + heldFormItem.getFormData().featureName + " Value: " + heldFormItem.getFormData().alternateValue);
                if (heldFormItem.getFormData().alternateValue.equalsIgnoreCase("true") || heldFormItem.getFormData().alternateValue.equalsIgnoreCase("false")) {
                    new FlagSpeciesFeature(heldFormItem.getFormData().featureName, Boolean.getBoolean(heldFormItem.getFormData().alternateValue)).apply(pokemon);
                } else {
                    new StringSpeciesFeature(heldFormItem.getFormData().featureName, heldFormItem.getFormData().alternateValue).apply(pokemon);
                }

                fixOgerponTeraType(pokemon, heldFormItem.getShowdownID());

                pokemon.updateAspects();
                pokemon.updateForm();
            }
        }

        return Unit.INSTANCE;
    }

    public static void fixOgerponTeraType(Pokemon pokemon, String showdownID) {
        // TODO: Allow custom ogerpon item tera type changing
        if (GenesisForms.INSTANCE.getConfig().fixOgerponTeraType) {
            if (pokemon.getSpecies().getName().equalsIgnoreCase("ogerpon")) {
                switch (showdownID) {
                    case "hearthflamemask" -> pokemon.setTeraType(TeraTypes.getFIRE());
                    case "wellspringmask" -> pokemon.setTeraType(TeraTypes.getWATER());
                    case "cornerstonemask" -> pokemon.setTeraType(TeraTypes.getROCK());
                    default -> pokemon.setTeraType(TeraTypes.getGRASS());
                }
            }
        }
    }

    public static void revertFormByItem(Pokemon pokemon, Item item) {
        if (item instanceof Megastone megastone) {
            if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"))) {
                revertMega(pokemon, megastone.getMegastoneData().featureName);
            }
        }

        if (item instanceof ZCrystal zcrystal) {
            zcrystal.getFormChanges().forEach(formChange -> {
                boolean speciesMatch = false;
                for (String species : formChange.species) {
                    if (pokemon.getSpecies().getName().equalsIgnoreCase(species)) {
                        speciesMatch = true;
                        break;
                    }
                }
                if (speciesMatch) {
                    pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(formChange.featureName));
                    if (formChange.defaultValue.equalsIgnoreCase("true") || formChange.defaultValue.equalsIgnoreCase("false")) {
                        new FlagSpeciesFeature(formChange.featureName, Boolean.getBoolean(formChange.defaultValue)).apply(pokemon);
                    } else {
                        new StringSpeciesFeature(formChange.featureName, formChange.defaultValue).apply(pokemon);
                    }
                    pokemon.updateAspects();
                    pokemon.updateForm();
                }
            });
        }

        if (item instanceof HeldFormItem heldFormItem) {
            if (heldFormItem.getSpeciesList().contains(pokemon.getSpecies())) {
                pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(heldFormItem.getFormData().featureName));
                if (heldFormItem.getFormData().defaultValue.equalsIgnoreCase("true") || heldFormItem.getFormData().defaultValue.equalsIgnoreCase("false")) {
                    new FlagSpeciesFeature(heldFormItem.getFormData().featureName, Boolean.getBoolean(heldFormItem.getFormData().defaultValue)).apply(pokemon);
                } else {
                    new StringSpeciesFeature(heldFormItem.getFormData().featureName, heldFormItem.getFormData().defaultValue).apply(pokemon);
                }
                pokemon.updateAspects();
                pokemon.updateForm();
            }
        }
    }

    public static Unit megaEvolveEvent(MegaEvolutionEvent event) {
        PokemonBattle battle = event.getBattle();
        BattlePokemon battlePokemon = event.getPokemon();
        Pokemon pokemon = battlePokemon.getEffectedPokemon();

        megaEvolveLogic(pokemon);

        PacketHandler.updatePackets(battle, battlePokemon, true);
        return Unit.INSTANCE;
    }

    public static void megaEvolveLogic(Pokemon pokemon) {
        Item heldItem = pokemon.heldItem().getItem();
        boolean canMegaEvolve = false;
        String featureName;
        String featureValue;
        String eventID = "global";

        if (heldItem instanceof Megastone megastone) {
            if (megastone.getSpecies().equals(pokemon.getSpecies())) {
                canMegaEvolve = true;
                featureName = megastone.getMegastoneData().featureName;
                featureValue = megastone.getMegastoneData().featureValue;
                eventID = megastone.getItemID();

            } else {
                featureName = "mega_evolution";
                featureValue = "mega";
            }
        } else {
            featureName = "mega_evolution";
            featureValue = "mega";
            if (pokemon.getSpecies().getName().equalsIgnoreCase("rayquaza")) {
                if (pokemon.getMoveSet().getMoves().stream().anyMatch(move -> move.getTemplate().getName().equalsIgnoreCase("dragonascent"))) {
                    canMegaEvolve = true;
                    eventID = "rayquaza";
                }
            }
        }

        EventsConfig.AnimationData megaAnimation = EventsConfig.gimmickEvents.megaEvolution.getAnimation(eventID);

        if (canMegaEvolve) {
            EventsConfig.gimmickEvents.megaEvolution.runEvent(eventID, pokemon, pokemon.getEntity());
            if (pokemon.getEntity() != null && megaAnimation != null) {
                pokemon.getEntity().after(megaAnimation.formDelaySeconds, () -> {
                    if (pokemon.getOwnerUUID() == null || GenesisForms.INSTANCE.getPlayersWithMega().containsKey(pokemon.getOwnerUUID())) return Unit.INSTANCE;

                    if (featureValue.equalsIgnoreCase("true") || featureValue.equalsIgnoreCase("false")) {
                        new FlagSpeciesFeature(featureName, Boolean.getBoolean(featureValue)).apply(pokemon);
                    } else {
                        new StringSpeciesFeature(featureName, featureValue).apply(pokemon);
                    }

                    if (pokemon.isPlayerOwned() && pokemon.getOwnerUUID() != null) {
                        GenesisForms.INSTANCE.getPlayersWithMega().put(pokemon.getOwnerUUID(), pokemon.getUuid());
                    }

                    return Unit.INSTANCE;
                });
            } else {
                if (pokemon.getOwnerUUID() == null || GenesisForms.INSTANCE.getPlayersWithMega().containsKey(pokemon.getOwnerUUID())) return;

                if (featureValue.equalsIgnoreCase("true") || featureValue.equalsIgnoreCase("false")) {
                    new FlagSpeciesFeature(featureName, Boolean.getBoolean(featureValue)).apply(pokemon);
                } else {
                    new StringSpeciesFeature(featureName, featureValue).apply(pokemon);
                }

                if (pokemon.isPlayerOwned() && pokemon.getOwnerUUID() != null) {
                    GenesisForms.INSTANCE.getPlayersWithMega().put(pokemon.getOwnerUUID(), pokemon.getUuid());
                }
            }
            if (pokemon.getTradeable() || GenesisForms.INSTANCE.getConfig().alwaysModifyTradeableProperty) {
                pokemon.getPersistentData().putBoolean("genesis_untradeable", true);
                pokemon.setTradeable(false);
            }
        }
    }

    public static boolean revertMega(Pokemon pokemon, String featureName) {
        boolean wasMega = pokemon.getFeatures().removeIf(features -> features.getName().equalsIgnoreCase(featureName));

        // This causes an issue if the tradeable property is disabled while in mega form.. But 1.7 fixes this with the magical trading.pre event
        if (wasMega && (pokemon.getPersistentData().contains("genesis_untradeable") || GenesisForms.INSTANCE.getConfig().alwaysModifyTradeableProperty)) {
            pokemon.setTradeable(true);
            pokemon.getPersistentData().remove("genesis_untradeable");
        }

        if (wasMega && pokemon.getOwnerUUID() != null) GenesisForms.INSTANCE.getPlayersWithMega().remove(pokemon.getOwnerUUID());

        pokemon.updateAspects();
        pokemon.updateForm();

        return wasMega;
    }

    public static Unit pokemonReleasedEvent(ReleasePokemonEvent.Post event) {
        ServerPlayerEntity player = event.getPlayer();
        Pokemon pokemon = event.getPokemon();
        if (GenesisForms.INSTANCE.getPlayersWithMega().containsKey(player.getUuid())) {
            if (pokemon.getUuid().equals(GenesisForms.INSTANCE.getPlayersWithMega().get(player.getUuid()))) {
                GenesisForms.INSTANCE.getPlayersWithMega().remove(player.getUuid());
            }
        }
        return Unit.INSTANCE;
    }

    public static Unit pokemonGainedEvent(PokemonGainedEvent event) {
        if (event.getPokemon().getSpecies().getName().equalsIgnoreCase("Terapagos") && GenesisForms.INSTANCE.getConfig().fixTerapagosTeraType) {
            event.getPokemon().setTeraType(TeraTypes.getSTELLAR());
        }

        if (event.getPokemon().getSpecies().getName().equalsIgnoreCase("Ogerpon") && GenesisForms.INSTANCE.getConfig().fixOgerponTeraType) {
            for (SpeciesFeature speciesFeature : event.getPokemon().getFeatures()) {
                if (speciesFeature.getName().equalsIgnoreCase("ogre_mask") && speciesFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                    switch (stringSpeciesFeature.getValue()) {
                        case "cornerstone" -> event.getPokemon().setTeraType(TeraTypes.getROCK());
                        case "hearthflame" -> event.getPokemon().setTeraType(TeraTypes.getFIRE());
                        case "wellspring" -> event.getPokemon().setTeraType(TeraTypes.getWATER());
                        default -> event.getPokemon().setTeraType(TeraTypes.getGRASS());
                    }
                    break;
                }
            }
        }
        return Unit.INSTANCE;
    }

    public static Unit terastallizationEvent(TerastallizationEvent event) {
        BattlePokemon battlePokemon = event.getPokemon();
        Pokemon pokemon = battlePokemon.getEffectedPokemon();
        PokemonEntity pokemonEntity = battlePokemon.getEntity();
        TeraType teraType = event.getTeraType();

        pokemon.getPersistentData().putString("tera_type", teraType.showdownId());

        if (pokemon.getSpecies().getName().equalsIgnoreCase("Terapagos")) {
            new StringSpeciesFeature("tera_form", "stellar").apply(pokemon);
        }
        if (pokemon.getSpecies().getName().equalsIgnoreCase("Ogerpon")) {
            new FlagSpeciesFeature("embody_aspect", true).apply(pokemon);
        }

        if (pokemonEntity != null) {
            EventsConfig.gimmickEvents.terastallization.runEvent(teraType.showdownId(), pokemon, pokemonEntity);
        }

        if (pokemon.isPlayerOwned() && pokemon.getOwnerPlayer() != null) {
            for (ItemStack stack : getValidKeyItemSlots(pokemon.getOwnerPlayer())) {
                if (stack.getItem() instanceof TeraAccessory teraAccessory) {
                    if (teraAccessory.requiresCharge && GenesisForms.INSTANCE.getConfig().requireOrbRecharge) {
                        stack.setDamage(stack.getMaxDamage());
                    }
                }
            }
        }

        return Unit.INSTANCE;
    }

    public static Unit zPowerEvent(ZMoveUsedEvent event) {
        Pokemon pokemon = event.getPokemon().getEffectedPokemon();
        PokemonEntity pokemonEntity = event.getPokemon().getEntity();
        Item heldItem = pokemon.heldItem().getItem();

        if (heldItem instanceof ZCrystal zCrystal) {
            EventsConfig.gimmickEvents.zPower.runEvent(zCrystal.getItemID(), pokemon, pokemonEntity);
        }

        return Unit.INSTANCE;
    }
}
