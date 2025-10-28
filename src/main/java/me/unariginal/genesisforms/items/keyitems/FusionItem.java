
package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.items.keyitems.FusionItemsConfig;
import me.unariginal.genesisforms.items.ConsumablePolymerItem;
import me.unariginal.genesisforms.utils.PokemonUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains(itemID) || !GenesisForms.INSTANCE.getConfig().enableFusions) return TypedActionResult.fail(itemStack);

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
            PokemonProperties properties = PokemonUtils.loadFromNBT(pokemon.getPersistentData());
            properties.setSpecies(fusedFuelPokemonData.species);
            FusionItemsConfig.FuelPokemonData finalFusedFuelPokemonData = fusedFuelPokemonData;
            pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(finalFusedFuelPokemonData.resultFeatureName));
            pokemon.updateAspects();
            pokemon.updateForm();

            Pokemon returnedPokemon = properties.create();
            IntSpeciesFeature dynamaxLevelFeature = returnedPokemon.getFeature("dynamax_level");
            assert dynamaxLevelFeature != null;
            dynamaxLevelFeature.setValue(returnedPokemon.getDmaxLevel());
            returnedPokemon.markFeatureDirty(dynamaxLevelFeature);
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
                            if (fuelPokemon.resultFeatureValue.equalsIgnoreCase("true") || fuelPokemon.resultFeatureValue.equalsIgnoreCase("false"))
                                new FlagSpeciesFeature(fuelPokemon.resultFeatureName, Boolean.getBoolean(fuelPokemon.resultFeatureValue)).apply(pokemon);
                            else
                                new StringSpeciesFeature(fuelPokemon.resultFeatureName, fuelPokemon.resultFeatureValue).apply(pokemon);
                            pokemon.setPersistentData$common(PokemonUtils.saveToNBT(partyPokemon.createPokemonProperties(PokemonPropertyExtractor.ALL)));
                            partyStore.remove(partyPokemon);
                            pokemon.updateAspects();
                            pokemon.updateForm();

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
