package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.FormeChangeEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.MegaEvolutionEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.TerastallizationEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.ZMoveUsedEvent;
import com.cobblemon.mod.common.api.events.pokemon.HeldItemEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentEvent;
import com.cobblemon.mod.common.api.events.pokemon.TradeEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.battles.dispatch.UntilDispatch;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import dev.emi.trinkets.api.TrinketsApi;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.BattleFormChangeConfig;
import me.unariginal.genesisforms.config.MegaEvolutionConfig;
import me.unariginal.genesisforms.data.event.ParticleEvent;
import me.unariginal.genesisforms.items.helditems.HeldFormItem;
import me.unariginal.genesisforms.items.helditems.Megastone;
import me.unariginal.genesisforms.items.helditems.ZCrystal;
import me.unariginal.genesisforms.items.keyitems.accessories.DynamaxAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.MegaAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.TeraAccessory;
import me.unariginal.genesisforms.items.keyitems.accessories.ZAccessory;
import me.unariginal.genesisforms.utils.GlowUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.unariginal.genesisforms.config.ConfigManager.*;
import static me.unariginal.genesisforms.utils.PokemonUtils.applyFeature;
import static me.unariginal.genesisforms.utils.PokemonUtils.swapPokemonMove;

public class CobblemonEventHandler {
    public static boolean trinketsLoaded = false;
    // Written from the main thread (server tick, PokemonEntity.after callbacks) but polled from
    // the battle thread (UntilDispatch predicates), so this needs real cross-thread visibility.
    public static Map<UUID, Float> activeFormAnimations = new ConcurrentHashMap<>();

    public static void battleStartEvent(BattleStartedEvent.Pre event) {
        PokemonBattle battle = event.getBattle();
        revertPartyForms(battle);

        for (ServerPlayerEntity player : battle.getPlayers()) {
            List<ItemStack> inventory = getValidKeyItemSlots(player);

            AtomicBoolean hasMega = new AtomicBoolean(false);
            AtomicBoolean hasTera = new AtomicBoolean(false);
            AtomicBoolean hasZ = new AtomicBoolean(false);
            AtomicBoolean hasDmax = new AtomicBoolean(false);
            for (ItemStack itemStack : inventory) {
                if (itemStack.getItem() instanceof MegaAccessory && CONFIG.megaSettings.enableMegaEvolution) {
                    hasMega.set(true);
                } else if (itemStack.getItem() instanceof TeraAccessory && CONFIG.teraSettings.enableTera) {
                    if (itemStack.getDamage() != itemStack.getMaxDamage()) {
                        hasTera.set(true);
                    }
                } else if (itemStack.getItem() instanceof ZAccessory && CONFIG.zPowerSettings.enableZCrystals) {
                    hasZ.set(true);
                } else if (itemStack.getItem() instanceof DynamaxAccessory && CONFIG.dynamaxSettings.enableDynamax) {
                    hasDmax.set(true);
                }
            }

            if (trinketsLoaded) {
                TrinketsApi.getTrinketComponent(player).ifPresent(trinketComponent -> {
                    if (trinketComponent.isEquipped(item -> item.getItem() instanceof MegaAccessory) && CONFIG.megaSettings.enableMegaEvolution) {
                        hasMega.set(true);
                    }

                    if (trinketComponent.isEquipped(item -> item.getItem() instanceof TeraAccessory) && CONFIG.teraSettings.enableTera) {
                        hasTera.set(true);
                    }

                    if (trinketComponent.isEquipped(item -> item.getItem() instanceof ZAccessory) && CONFIG.zPowerSettings.enableZCrystals) {
                        hasZ.set(true);
                    }

                    if (trinketComponent.isEquipped(item -> item.getItem() instanceof DynamaxAccessory) && CONFIG.dynamaxSettings.enableDynamax) {
                        hasDmax.set(true);
                    }
                });
            }

            GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
            playerData.getKeyItems().removeIf(identifier ->
                    identifier.equals(MiscUtilsKt.cobblemonResource("key_stone")) ||
                            identifier.equals(MiscUtilsKt.cobblemonResource("tera_orb")) ||
                            identifier.equals(MiscUtilsKt.cobblemonResource("z_ring")) ||
                            identifier.equals(MiscUtilsKt.cobblemonResource("dynamax_band")));

            if (hasMega.get()) playerData.getKeyItems().add(MiscUtilsKt.cobblemonResource("key_stone"));
            if (hasTera.get()) playerData.getKeyItems().add(MiscUtilsKt.cobblemonResource("tera_orb"));
            if (hasZ.get()) playerData.getKeyItems().add(MiscUtilsKt.cobblemonResource("z_ring"));
            if (hasDmax.get() && !hasTera.get()) playerData.getKeyItems().add(MiscUtilsKt.cobblemonResource("dynamax_band"));
        }

    }

    public static List<ItemStack> getValidKeyItemSlots(ServerPlayerEntity player) {
        List<ItemStack> inventory = new ArrayList<>();

        for (ItemStack itemStack : player.getInventory().main) {
            if ((CONFIG.generalSettings.keyItemSlots.main &&
                    (CONFIG.generalSettings.keyItemSlots.hotbar ||
                            !PlayerInventory.isValidHotbarIndex(player.getInventory().indexOf(itemStack)))) ||
                    CONFIG.generalSettings.keyItemSlots.hotbar &&
                            PlayerInventory.isValidHotbarIndex(player.getInventory().indexOf(itemStack))) {
                inventory.add(itemStack);
            }
        }
        if (CONFIG.generalSettings.keyItemSlots.mainhand && !inventory.contains(player.getMainHandStack())) {
            inventory.add(player.getMainHandStack());
        }
        if (CONFIG.generalSettings.keyItemSlots.offhand && !inventory.contains(player.getOffHandStack())) {
            inventory.add(player.getOffHandStack());
        }
        if (CONFIG.generalSettings.keyItemSlots.armor) {
            for (ItemStack itemStack : player.getArmorItems()) {
                if (!inventory.contains(itemStack)) {
                    inventory.add(itemStack);
                }
            }
        }
        for (int slot : CONFIG.generalSettings.keyItemSlots.specific) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (!inventory.contains(stack)) {
                inventory.add(stack);
            }
        }

        return inventory;
    }

    public static void battleEndEvent(BattleVictoryEvent event) {
        revertPartyForms(event.getBattle());
    }

    public static void revertPartyForms(PokemonBattle battle) {
        for (BattleActor actor : battle.getActors()) {
            for (BattlePokemon pokemon : actor.getPokemonList()) {
                revertForm(pokemon.getEffectedPokemon(), false);
                revertForm(pokemon.getOriginalPokemon(), false);
            }
        }
    }

    public static void battleFledEvent(BattleFledEvent event) {
        revertPartyForms(event.getBattle());
    }

    public static void battleFaintEvent(BattleFaintedEvent event) {
        Pokemon pokemon = event.getKilled().getEffectedPokemon();
        if (pokemon.isPlayerOwned()) {
            revertForm(pokemon, true);
        }
    }

    public static void formChangeEvent(FormeChangeEvent event) {
        String formName = event.getFormeName();
        BattlePokemon battlePokemon = event.getPokemon();
        Pokemon pokemon = battlePokemon.getEffectedPokemon();
        PokemonBattle battle = event.getBattle();
        PokemonEntity pokemonEntity = pokemon.getEntity();

        if (pokemon.getSpecies().getName().equalsIgnoreCase("zygarde") && formName.equalsIgnoreCase("complete")) {
            if (pokemon.getAspects().contains("10-percent")) pokemon.getPersistentData().putString("percent_cells", "10");
            else pokemon.getPersistentData().putString("percent_cells", "50");
            battle.dispatchToFront(pokemonBattle -> {
                float delay = 0;
                if (EVENTS.formChanges != null && EVENTS.formChanges.battleForms != null) {
                    String eventId = "zygarde_percent_cells";
                    EVENTS.formChanges.battleForms.runEvent(eventId, pokemon, pokemonEntity);
                    ParticleEvent particleEvent = EVENTS.formChanges.battleForms.getAnimation(eventId);
                    if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
                }
                if (pokemonEntity != null) {
                    pokemonEntity.after(delay, () -> {
                        new StringSpeciesFeature("percent_cells", "complete").apply(pokemon);
                        return Unit.INSTANCE;
                    });
                } else new StringSpeciesFeature("percent_cells", "complete").apply(pokemon);
                return new UntilDispatch(() -> true);
            });
        }

        for (BattleFormChangeConfig battleFormInformation : BATTLE_FORMS.values()) {
            if (battleFormInformation.species.equalsIgnoreCase(pokemon.getSpecies().showdownId())) {
                if (battleFormInformation.forms.containsKey(formName)) {
                    battle.dispatchToFront(pokemonBattle -> {
                        float delay = 0;
                        if (EVENTS.formChanges != null && EVENTS.formChanges.battleForms != null) {
                            String eventId = battleFormInformation.species + "_" + formName;
                            EVENTS.formChanges.battleForms.runEvent(eventId, pokemon, pokemonEntity);
                            ParticleEvent particleEvent = EVENTS.formChanges.battleForms.getAnimation(eventId);
                            if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
                        }
                        if (pokemonEntity != null) {
                            pokemonEntity.after(delay, () -> {
                                applyFeature(battleFormInformation.forms.get(formName).featureName, battleFormInformation.forms.get(formName).featureValue, pokemon);
                                return Unit.INSTANCE;
                            });
                        }
                        applyFeature(battleFormInformation.forms.get(formName).featureName, battleFormInformation.forms.get(formName).featureValue, pokemon);
                        return new UntilDispatch(() -> true);
                    });
                    break;
                }
            }
        }

        PacketHandler.updatePackets(battle, battlePokemon, false);
    }

    public static void revertForm(Pokemon pokemon, boolean fromBattle) {
        // Reverting megas
        PokemonEntity pokemonEntity = pokemon.getEntity();
        Item heldItem = pokemon.heldItem().getItem();
        if (heldItem instanceof Megastone megastone) {
            if (revertMega(pokemon, megastone.getMegastoneData().featureName)) {
                if (EVENTS.megaEvolution != null)
                    EVENTS.megaEvolution.revertEvent(megastone.getItemId(), pokemon, pokemon.getEntity());
            }
        } else {
            for (String itemlessMega : itemlessMegas) {
                MegaEvolutionConfig megaEvolutionData = MEGA_EVOLUTIONS.get(itemlessMega);
                if (megaEvolutionData != null) {
                    if (megaEvolutionData.canMegaEvolve(pokemon)) {
                        if (revertMega(pokemon, megaEvolutionData.featureName)) {
                            if (EVENTS.megaEvolution != null)
                                EVENTS.megaEvolution.revertEvent(itemlessMega, pokemon, pokemon.getEntity());
                        }
                    }
                }
            }
        }

        // Reverting ultra fusion
        pokemon.getFeatures().removeIf(speciesFeature -> speciesFeature.getName().equalsIgnoreCase("dynamax_form"));
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
            for (BattleFormChangeConfig battleFormInformation : BATTLE_FORMS.values()) {
                if (battleFormInformation.species.equalsIgnoreCase(pokemon.getSpecies().getName())) {
                    if (!pokemon.getSpecies().getName().equals("Greninja") || pokemon.getAspects().contains("ash")) {
                        float delay = 0;
                        if (EVENTS.formChanges != null && EVENTS.formChanges.battleForms != null) {
                            String eventId = battleFormInformation.species + "_default_form";
                            EVENTS.formChanges.battleForms.runEvent(eventId, pokemon, pokemonEntity);
                            ParticleEvent particleEvent = EVENTS.formChanges.battleForms.getAnimation(eventId);
                            if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
                        }

                        if (pokemonEntity != null) {
                            pokemonEntity.after(delay, () -> {
                                applyFeature(battleFormInformation.defaultForm.featureName, battleFormInformation.defaultForm.featureValue, pokemon);
                                pokemon.updateAspects();
                                pokemon.updateForm();
                                return Unit.INSTANCE;
                            });
                        } else {
                            applyFeature(battleFormInformation.defaultForm.featureName, battleFormInformation.defaultForm.featureValue, pokemon);
                            pokemon.updateAspects();
                            pokemon.updateForm();
                        }
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

            if (EVENTS.terastallization != null)
                EVENTS.terastallization.revertEvent(teraType, pokemon, pokemon.getEntity());
        }

        pokemon.updateAspects();
        pokemon.updateForm();

        if (wasComplete) {
            AbilityTemplate powerconstruct = Abilities.get("powerconstruct");
            if (powerconstruct != null) pokemon.updateAbility(powerconstruct.create(false, Priority.LOW));
        }
    }

    public static void pokemonSentEvent(PokemonSentEvent.Post event) {
        if (event.getPokemon().getPersistentData().contains("glow_id") && event.getPokemon().getPersistentData().contains("glow_color")) {
            String glowId = event.getPokemon().getPersistentData().getString("glow_id");
            String glowColor = event.getPokemon().getPersistentData().getString("glow_color");
            GlowUtils.applyGlowing(glowId, glowColor, event.getPokemon(), event.getPokemonEntity());
        }
    }

    public static void heldItemChange(HeldItemEvent.Post event) {
        ItemStack received = event.getReceived();
        ItemStack returned = event.getReturned();
        Pokemon pokemon = event.getPokemon();
        PokemonEntity pokemonEntity = pokemon.getEntity();

        if (received == returned) return;

        // Revert all forms of the pokemon relating to held items (mega, held form items, z crystals)
        revertFormByItem(pokemon, returned.getItem());

        // Change forms
        if (received.getItem() instanceof ZCrystal zCrystal) {
            zCrystal.getFormChanges().forEach(formChange -> {
                boolean speciesMatch = false;
                for (String species : formChange.species) {
                    if (pokemon.getSpecies().getName().equalsIgnoreCase(species)) {
                        speciesMatch = true;
                        break;
                    }
                }
                if (speciesMatch) {
                    float delay = 0;
                    if (EVENTS.formChanges != null && EVENTS.formChanges.heldItems != null) {
                        String eventId = pokemon.getSpecies().getName().toLowerCase() + "_" + formChange.alternateValue;
                        EVENTS.formChanges.heldItems.runEvent(eventId, pokemon, pokemon.getEntity());
                        ParticleEvent particleEvent = EVENTS.formChanges.heldItems.getAnimation(eventId);
                        if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
                    }

                    if (pokemonEntity != null) {
                        activeFormAnimations.put(pokemon.getUuid(), delay * 20F);
                        pokemonEntity.after(delay, () -> {
                            if (pokemon.heldItem().getItem() == received.getItem()) {
                                applyFeature(formChange.featureName, formChange.alternateValue, pokemon);
                                pokemon.updateAspects();
                                pokemon.updateForm();
                            }
                            return Unit.INSTANCE;
                        });
                    }
                    applyFeature(formChange.featureName, formChange.alternateValue, pokemon);
                    pokemon.updateAspects();
                    pokemon.updateForm();
                }
            });
        }

        if (received.getItem() instanceof HeldFormItem heldFormItem) {
            if (heldFormItem.getSpeciesList().contains(pokemon.getSpecies())) {
                float delay = 0;
                if (EVENTS.formChanges != null && EVENTS.formChanges.heldItems != null) {
                    String eventId = pokemon.getSpecies().getName().toLowerCase() + "_" + heldFormItem.getFormData().alternateValue;
                    EVENTS.formChanges.heldItems.runEvent(eventId, pokemon, pokemon.getEntity());
                    ParticleEvent particleEvent = EVENTS.formChanges.heldItems.getAnimation(eventId);
                    if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
                }

                if (pokemonEntity != null) {
                    activeFormAnimations.put(pokemon.getUuid(), delay * 20F);
                    pokemonEntity.after(delay, () -> {
                        if (pokemon.heldItem().getItem() == received.getItem()) {
                            applyFeature(heldFormItem.getFormData().featureName, heldFormItem.getFormData().alternateValue, pokemon);
                            pokemon.updateAspects();
                            pokemon.updateForm();
                        }
                        return Unit.INSTANCE;
                    });
                } else {
                    applyFeature(heldFormItem.getFormData().featureName, heldFormItem.getFormData().alternateValue, pokemon);
                    pokemon.updateAspects();
                    pokemon.updateForm();
                }

                if (pokemon.getSpecies().getName().equalsIgnoreCase("zacian")) {
                    swapPokemonMove(pokemon, "ironhead", "behemothbash");
                } else if (pokemon.getSpecies().getName().equalsIgnoreCase("zamazenta")) {
                    swapPokemonMove(pokemon, "ironhead", "behemothblade");
                }
            }
        }

        fixOgerponTeraType(pokemon);
    }

    public static void fixOgerponTeraType(Pokemon pokemon) {
        // TODO: Allow custom ogerpon item tera type changing
        if (CONFIG.teraSettings.fixOgerponTeraType) {
            if (pokemon.getSpecies().getName().equalsIgnoreCase("ogerpon")) {
                if (pokemon.heldItem().getItem() instanceof HeldFormItem heldFormItem) {
                    String showdownID = heldFormItem.getShowdownId();
                    switch (showdownID) {
                        case "hearthflamemask" -> pokemon.setTeraType(TeraTypes.getFIRE());
                        case "wellspringmask" -> pokemon.setTeraType(TeraTypes.getWATER());
                        case "cornerstonemask" -> pokemon.setTeraType(TeraTypes.getROCK());
                        default -> pokemon.setTeraType(TeraTypes.getGRASS());
                    }
                } else {
                    pokemon.setTeraType(TeraTypes.getGRASS());
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
                    if (EVENTS.formChanges != null && EVENTS.formChanges.heldItems != null) {
                        String eventId = formChange.featureName + "_" + formChange.defaultValue;
                        EVENTS.formChanges.heldItems.revertEvent(eventId, pokemon, pokemon.getEntity());
                    }

                    pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(formChange.featureName));
                    applyFeature(formChange.featureName, formChange.defaultValue, pokemon);
                    pokemon.updateAspects();
                    pokemon.updateForm();
                }
            });
        }

        if (item instanceof HeldFormItem heldFormItem) {
            if (heldFormItem.getSpeciesList().contains(pokemon.getSpecies())) {
                if (EVENTS.formChanges != null && EVENTS.formChanges.heldItems != null) {
                    String eventId = heldFormItem.getFormData().featureName + "_" + heldFormItem.getFormData().defaultValue;
                    EVENTS.formChanges.heldItems.revertEvent(eventId, pokemon, pokemon.getEntity());
                }
                pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(heldFormItem.getFormData().featureName));
                applyFeature(heldFormItem.getFormData().featureName, heldFormItem.getFormData().defaultValue, pokemon);

                if (pokemon.getSpecies().getName().equalsIgnoreCase("zacian")) {
                    swapPokemonMove(pokemon, "behemothblade", "ironhead");
                } else if (pokemon.getSpecies().getName().equalsIgnoreCase("zamazenta")) {
                    swapPokemonMove(pokemon, "behemothbash", "ironhead");
                }

                pokemon.updateAspects();
                pokemon.updateForm();
            }
        }
    }

    public static void megaEvolveEvent(MegaEvolutionEvent event) {
        PokemonBattle battle = event.getBattle();
        BattlePokemon battlePokemon = event.getPokemon();
        Pokemon pokemon = battlePokemon.getEffectedPokemon();

        megaEvolveLogic(pokemon, true, battle, battlePokemon);
    }

    public static void megaEvolveLogic(Pokemon pokemon, boolean fromBattle, @Nullable PokemonBattle battle, @Nullable BattlePokemon battlePokemon) {
        Item heldItem = pokemon.heldItem().getItem();
        PokemonEntity pokemonEntity = pokemon.getEntity();
        boolean canMegaEvolve = false;
        String featureName = "mega_evolution";
        String featureValue = "mega";
        String eventId = "global";

        if (heldItem instanceof Megastone megastone) {
            if (megastone.getMegastoneData().canMegaEvolve(pokemon)) {
                canMegaEvolve = true;
                featureName = megastone.getMegastoneData().featureName;
                featureValue = megastone.getMegastoneData().featureValue;
                eventId = megastone.getItemId();
            }
        } else {
            for (String itemlessMega : itemlessMegas) {
                MegaEvolutionConfig megaEvolutionData = MEGA_EVOLUTIONS.get(itemlessMega);
                if (megaEvolutionData != null) {
                    if (megaEvolutionData.canMegaEvolve(pokemon)) {
                        canMegaEvolve = true;
                        featureName = megaEvolutionData.featureName;
                        featureValue = megaEvolutionData.featureValue;
                        eventId = itemlessMega;
                    }
                }
            }
        }

        if (canMegaEvolve) {
            if (!activeFormAnimations.containsKey(pokemon.getUuid())) {
                if (battle != null) {
                    String finalEventId = eventId;
                    String finalFeatureName = featureName;
                    String finalFeatureValue = featureValue;
                    battle.dispatchToFront(pokemonBattle -> {
                        megaEvolveWithAnimation(pokemon, pokemonEntity, fromBattle, finalEventId, finalFeatureName, finalFeatureValue, battle, battlePokemon);
                        return new UntilDispatch(() -> !activeFormAnimations.containsKey(pokemon.getUuid())).andThen(() -> {
                            if (battlePokemon != null) {
                                battlePokemon.sendUpdate();
                                PacketHandler.updatePackets(battle, battlePokemon, true);
                            }
                            return Unit.INSTANCE;
                        });
                    });
                }
                else
                    megaEvolveWithAnimation(pokemon, pokemonEntity, fromBattle, eventId, featureName, featureValue, null, null);
            }
        }
    }

    public static void megaEvolveWithAnimation(Pokemon pokemon, @Nullable PokemonEntity pokemonEntity, boolean fromBattle, String eventId, String featureName, String featureValue, @Nullable PokemonBattle battle, @Nullable BattlePokemon battlePokemon) {
        float delay = 0;
        if (EVENTS.megaEvolution != null) {
            EVENTS.megaEvolution.runEvent(eventId, pokemon, pokemonEntity);
            ParticleEvent particleEvent = EVENTS.megaEvolution.getAnimation(eventId);
            if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
        }

        if (pokemonEntity != null) {
            activeFormAnimations.put(pokemon.getUuid(), delay * 20F);
            pokemonEntity.after(delay, () -> {
                if ((pokemon.getOwnerUUID() == null || GenesisForms.INSTANCE.playersWithMega.containsKey(pokemon.getOwnerUUID())) && !fromBattle)
                    return Unit.INSTANCE;

                applyFeature(featureName, featureValue, pokemon);

                if (pokemon.isPlayerOwned() && pokemon.getOwnerUUID() != null) {
                    GenesisForms.INSTANCE.playersWithMega.put(pokemon.getOwnerUUID(), pokemon.getUuid());
                }

                activeFormAnimations.remove(pokemon.getUuid());
                return Unit.INSTANCE;
            });
        } else {
            if ((pokemon.getOwnerUUID() == null || GenesisForms.INSTANCE.playersWithMega.containsKey(pokemon.getOwnerUUID())) && !fromBattle)
                return;

            applyFeature(featureName, featureValue, pokemon);

            if (pokemon.isPlayerOwned() && pokemon.getOwnerUUID() != null) {
                GenesisForms.INSTANCE.playersWithMega.put(pokemon.getOwnerUUID(), pokemon.getUuid());
            }

            if (battlePokemon != null) {
                battlePokemon.sendUpdate();
                PacketHandler.updatePackets(battle, battlePokemon, true);
            }
        }
        if (CONFIG.megaSettings.useTradeableProperty) pokemon.setTradeable(false);
        pokemon.getPersistentData().putBoolean("genesis_untradeable", true);
    }

    public static boolean revertMega(Pokemon pokemon, String featureName) {
        boolean wasMega = pokemon.getFeatures().removeIf(features -> features.getName().equalsIgnoreCase(featureName));

        if (wasMega && !pokemon.getPersistentData().contains("genesis_untradeable") && !pokemon.getTradeable()) pokemon.setTradeable(true);
        if (wasMega && CONFIG.megaSettings.useTradeableProperty) pokemon.setTradeable(true);

        pokemon.getPersistentData().remove("genesis_untradeable");
        if (pokemon.getOwnerUUID() != null) GenesisForms.INSTANCE.playersWithMega.remove(pokemon.getOwnerUUID());

        pokemon.updateAspects();
        pokemon.updateForm();

        return wasMega;
    }

    public static void pokemonReleasedEvent(ReleasePokemonEvent.Post event) {
        ServerPlayerEntity player = event.getPlayer();
        Pokemon pokemon = event.getPokemon();
        if (GenesisForms.INSTANCE.playersWithMega.containsKey(player.getUuid())) {
            if (pokemon.getUuid().equals(GenesisForms.INSTANCE.playersWithMega.get(player.getUuid()))) {
                GenesisForms.INSTANCE.playersWithMega.remove(player.getUuid());
            }
        }
    }

    public static void tradeEvent(TradeEvent.Pre event) {
        if (event.getTradeParticipant1Pokemon().getPersistentData().contains("genesis_untradeable")) {
            event.cancel();
            return;
        }
        if (event.getTradeParticipant2Pokemon().getPersistentData().contains("genesis_untradeable")) event.cancel();
    }

    public static void pokemonGainedEvent(PokemonGainedEvent event) {
        if (event.getPokemon().getSpecies().getName().equalsIgnoreCase("Terapagos") && CONFIG.teraSettings.fixTerapagosTeraType) {
            event.getPokemon().setTeraType(TeraTypes.getSTELLAR());
        }

        if (event.getPokemon().getSpecies().getName().equalsIgnoreCase("Ogerpon") && CONFIG.teraSettings.fixOgerponTeraType) {
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
    }

    public static void terastallizationEvent(TerastallizationEvent event) {
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

        if (pokemonEntity != null && EVENTS.terastallization != null) {
            EVENTS.terastallization.runEvent(teraType.showdownId(), pokemon, pokemonEntity);
        }

        if (pokemon.isPlayerOwned() && pokemon.getOwnerPlayer() != null) {
            for (ItemStack stack : getValidKeyItemSlots(pokemon.getOwnerPlayer())) {
                if (stack.getItem() instanceof TeraAccessory teraAccessory) {
                    if (teraAccessory.requiresCharge && CONFIG.teraSettings.requireOrbRecharge) {
                        stack.setDamage(stack.getMaxDamage());
                    }
                }
            }
        }

    }

    public static void zPowerEvent(ZMoveUsedEvent event) {
        Pokemon pokemon = event.getPokemon().getEffectedPokemon();
        PokemonEntity pokemonEntity = event.getPokemon().getEntity();
        Item heldItem = pokemon.heldItem().getItem();

        if (heldItem instanceof ZCrystal zCrystal && EVENTS.zPower != null) {
            ParticleEvent particleEvent = EVENTS.zPower.getAnimation(zCrystal.getItemId());
            float delay = 0;
            if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
            event.getBattle().dispatchWaitingToFront(delay, () -> {
                if (EVENTS.zPower != null) EVENTS.zPower.runEvent(zCrystal.getItemId(), pokemon, pokemonEntity);
                return Unit.INSTANCE;
            });
        }
    }
}