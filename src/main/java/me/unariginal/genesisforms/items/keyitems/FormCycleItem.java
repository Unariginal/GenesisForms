
package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import kotlin.Unit;
import me.unariginal.genesisforms.data.CycledFormSetting;
import me.unariginal.genesisforms.data.event.ParticleEvent;
import me.unariginal.genesisforms.items.ConsumablePolymerItem;
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

import static me.unariginal.genesisforms.config.ConfigManager.CONFIG;
import static me.unariginal.genesisforms.config.ConfigManager.EVENTS;
import static me.unariginal.genesisforms.utils.PokemonUtils.applyFeature;

public class FormCycleItem extends ConsumablePolymerItem implements PokemonSelectingItem {
    private final CycledFormSetting cycledFormSetting;

    public FormCycleItem(Settings settings, Item polymerItem, PolymerModelData modelData, String itemId, List<String> lore, boolean consumable, CycledFormSetting cycledFormSetting) {
        super(settings, polymerItem, modelData, itemId, lore, consumable);
        this.cycledFormSetting = cycledFormSetting;
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public boolean canUseOnPokemon(@NotNull ItemStack stack, @NotNull Pokemon pokemon) {
        for (String species : cycledFormSetting.species()) {
            if (pokemon.getSpecies().getName().equalsIgnoreCase(species)) {
                return true;
            }
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
        if (CONFIG.generalSettings.disabledItems.contains(itemId)) return TypedActionResult.fail(itemStack);
        PokemonEntity pokemonEntity = pokemon.getEntity();

        if (cycledFormSetting.featureValues().size() == 1) {
            String featureValue = cycledFormSetting.featureValues().getFirst();
            float delay = 0;
            if (EVENTS.formChanges != null && EVENTS.formChanges.keyItems != null) {
                String eventId = pokemon.getSpecies().getName().toLowerCase() + "_" + featureValue;
                EVENTS.formChanges.keyItems.runEvent(eventId, pokemon, pokemonEntity);
                ParticleEvent particleEvent = EVENTS.formChanges.keyItems.getAnimation(eventId);
                if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
            }

            if (pokemonEntity != null) {
                pokemonEntity.after(delay, () -> {
                    applyFeature(cycledFormSetting.featureName(), featureValue, pokemon);
                    pokemon.updateAspects();
                    pokemon.updateForm();
                    return Unit.INSTANCE;
                });
            } else {
                applyFeature(cycledFormSetting.featureName(), featureValue, pokemon);
                pokemon.updateAspects();
                pokemon.updateForm();
            }
        } else {
            SpeciesFeature currentFeature = pokemon.getFeature(cycledFormSetting.featureName());
            String currentFeatureValue = "null";
            if (currentFeature != null) {
                if (currentFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                    currentFeatureValue = stringSpeciesFeature.getValue();
                } else if (currentFeature instanceof FlagSpeciesFeature flagSpeciesFeature) {
                    currentFeatureValue = String.valueOf(flagSpeciesFeature.getEnabled());
                }
            }

            if (cycledFormSetting.featureValues().contains(currentFeatureValue)) {
                int index = cycledFormSetting.featureValues().indexOf(currentFeatureValue) + 1;
                if (index >= cycledFormSetting.featureValues().size()) index = 0;
                String newFeatureValue = cycledFormSetting.featureValues().get(index);

                float delay = 0;
                if (EVENTS.formChanges != null && EVENTS.formChanges.keyItems != null) {
                    String eventId = pokemon.getSpecies().getName().toLowerCase() + "_" + newFeatureValue;
                    EVENTS.formChanges.keyItems.runEvent(eventId, pokemon, pokemonEntity);
                    ParticleEvent particleEvent = EVENTS.formChanges.keyItems.getAnimation(eventId);
                    if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
                }

                if (pokemonEntity != null) {
                    pokemonEntity.after(delay, () -> {
                        if (newFeatureValue.equalsIgnoreCase("null")) {
                            pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(cycledFormSetting.featureName()));
                        } else {
                            applyFeature(cycledFormSetting.featureName(), newFeatureValue, pokemon);
                        }
                        pokemon.updateAspects();
                        pokemon.updateForm();
                        return Unit.INSTANCE;
                    });
                } else {
                    if (newFeatureValue.equalsIgnoreCase("null")) {
                        pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(cycledFormSetting.featureName()));
                    } else {
                        applyFeature(cycledFormSetting.featureName(), newFeatureValue, pokemon);
                    }
                    pokemon.updateAspects();
                    pokemon.updateForm();
                }
            }
        }

        if (consumable) itemStack.decrementUnlessCreative(1, serverPlayerEntity);

        return TypedActionResult.success(itemStack);
    }
}
