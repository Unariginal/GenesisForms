
package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import kotlin.Unit;
import me.unariginal.genesisforms.config.items.keyitems.FusionItemsConfig;
import me.unariginal.genesisforms.data.event.ParticleEvent;
import me.unariginal.genesisforms.items.ConsumablePolymerItem;
import me.unariginal.genesisforms.utils.PokemonUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.unariginal.genesisforms.config.ConfigManager.CONFIG;
import static me.unariginal.genesisforms.config.ConfigManager.EVENTS;
import static me.unariginal.genesisforms.utils.PokemonUtils.applyFeature;

public class FusionItem extends ConsumablePolymerItem implements PokemonSelectingItem {
    private final List<FusionItemsConfig.FusionData> fusions;

    public FusionItem(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, boolean consumable, List<FusionItemsConfig.FusionData> fusions) {
        super(settings, polymerItem, modelData, itemID, lore, consumable);
        this.fusions = fusions;
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public boolean canUseOnPokemon(@NotNull ItemStack stack, @NotNull Pokemon pokemon) {
        for (FusionItemsConfig.FusionData fusionData : fusions) {
            if (pokemon.getSpecies().getName().equalsIgnoreCase(fusionData.corePokemon)) return true;
        }
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            return this.use(player, player.getStackInHand(hand));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!this.canUseOnPokemon(itemStack, pokemon)) return TypedActionResult.fail(itemStack);
        if (CONFIG.generalSettings.disabledItems.contains(itemId) || !CONFIG.fusionSettings.enableFusions) return TypedActionResult.fail(itemStack);
        PokemonEntity pokemonEntity = pokemon.getEntity();

        PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(serverPlayerEntity);

        boolean isFused = false;
        FusionItemsConfig.FuelPokemonData fusedFuelPokemonData = null;
        for (FusionItemsConfig.FusionData fusionData : fusions) {
            if (pokemon.getSpecies().getName().equalsIgnoreCase(fusionData.corePokemon)) {
                for (FusionItemsConfig.FuelPokemonData fuelPokemonData : fusionData.fuelPokemon) {
                    if (pokemon.getFeatures().stream().anyMatch(speciesFeature -> {
                        if (speciesFeature.getName().equalsIgnoreCase(fuelPokemonData.resultFeatureName)) {
                            if (speciesFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                                return stringSpeciesFeature.getValue().equalsIgnoreCase(fuelPokemonData.resultFeatureValue);
                            }
                        }
                        return false;
                    })) {
                        isFused = true;
                        fusedFuelPokemonData = fuelPokemonData;
                        break;
                    }
                }
            }
        }

        if (isFused) {
            PokemonProperties properties;
            boolean oldData = false;
            if (pokemon.getPersistentData().contains("fusion_data") && pokemon.getPersistentData().getCompound("fusion_data").contains("fuel_properties")) {
                NbtCompound fusionData = pokemon.getPersistentData().getCompound("fusion_data");
                NbtCompound fuelProperties = fusionData.getCompound("fuel_properties");
                properties = PokemonUtils.loadFromNBT(fuelProperties);
            } else {
                oldData = true;
                properties = PokemonUtils.loadFromNBT(pokemon.getPersistentData());
            }

            properties.setSpecies(fusedFuelPokemonData.species);
            FusionItemsConfig.FuelPokemonData finalFusedFuelPokemonData = fusedFuelPokemonData;
            pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(finalFusedFuelPokemonData.resultFeatureName));
            pokemon.updateAspects();
            pokemon.updateForm();

            Pokemon returnedPokemon = properties.create();

            if (!oldData && pokemon.getPersistentData().getCompound("fusion_data").contains("fuel_features")) {
                NbtCompound fusionData = pokemon.getPersistentData().getCompound("fusion_data");
                NbtCompound fuelFeatures = fusionData.getCompound("fuel_features");
                for (String featureName : fuelFeatures.getKeys()) {
                    NbtElement featureValue = fuelFeatures.get(featureName);

                    if (featureValue instanceof NbtString value) {
                        // String Species Feature
                        new StringSpeciesFeature(featureName, value.asString()).apply(returnedPokemon);
                    } else if (featureValue instanceof NbtByte value) {
                        // Flag Species Feature
                        new FlagSpeciesFeature(featureName, value.byteValue() == (byte) 1).apply(returnedPokemon);
                    } else if (featureValue instanceof NbtInt value) {
                        // Int Species Feature
                        new IntSpeciesFeature(featureName, value.intValue()).apply(returnedPokemon);
                    }
                }
            }

            if (!oldData) {
                pokemon.getPersistentData().remove("fusion_data");
            }

            if (oldData) {
                IntSpeciesFeature dynamaxLevelFeature = returnedPokemon.getFeature("dynamax_level");
                if (dynamaxLevelFeature != null) {
                    dynamaxLevelFeature.setValue(returnedPokemon.getDmaxLevel());
                    returnedPokemon.markFeatureDirty(dynamaxLevelFeature);
                }
            }
            partyStore.add(returnedPokemon);

            if (consumable) itemStack.decrementUnlessCreative(1, serverPlayerEntity);
        } else {
            FusionItemsConfig.FusionData fusion = null;
            for (FusionItemsConfig.FusionData fusionData : fusions) {
                if (pokemon.getSpecies().getName().equalsIgnoreCase(fusionData.corePokemon)) {
                    fusion = fusionData;
                }
            }

            if (fusion == null) return TypedActionResult.fail(itemStack);

            partyLoop:
            for (Pokemon partyPokemon : partyStore) {
                if (partyPokemon != null) {
                    for (FusionItemsConfig.FuelPokemonData fuelPokemon : fusion.fuelPokemon) {
                        if (partyPokemon.getSpecies().getName().equalsIgnoreCase(fuelPokemon.species)) {
                            NbtCompound fusionData = new NbtCompound();
                            fusionData.put("fuel_properties", PokemonUtils.saveToNBT(partyPokemon.createPokemonProperties(PokemonPropertyExtractor.ALL)));

                            NbtCompound speciesFeatures = new NbtCompound();
                            for (SpeciesFeature speciesFeature : partyPokemon.getFeatures()) {
                                speciesFeatures = speciesFeature.saveToNBT(speciesFeatures);
                            }
                            fusionData.put("fuel_features", speciesFeatures);
                            pokemon.getPersistentData().put("fusion_data", fusionData);

                            partyStore.remove(partyPokemon);

                            float delay = 0;
                            if (EVENTS.formChanges != null && EVENTS.formChanges.fusions != null) {
                                String eventId = fusion.corePokemon + "_" + fuelPokemon.species;
                                EVENTS.formChanges.fusions.runEvent(eventId, pokemon, pokemonEntity);
                                ParticleEvent particleEvent = EVENTS.formChanges.fusions.getAnimation(eventId);
                                if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
                            }

                            if (pokemonEntity != null) {
                                pokemonEntity.after(delay, () -> {
                                    applyFeature(fuelPokemon.resultFeatureName, fuelPokemon.resultFeatureValue, pokemon);
                                    pokemon.updateAspects();
                                    pokemon.updateForm();
                                    return Unit.INSTANCE;
                                });
                            } else {
                                applyFeature(fuelPokemon.resultFeatureName, fuelPokemon.resultFeatureValue, pokemon);
                                pokemon.updateAspects();
                                pokemon.updateForm();
                            }

                            if (consumable) itemStack.decrementUnlessCreative(1, serverPlayerEntity);

                            break partyLoop;
                        }
                    }
                }
            }
        }

        return TypedActionResult.success(itemStack);
    }
}
