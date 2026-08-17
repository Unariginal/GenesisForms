
package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import kotlin.Unit;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.data.event.ParticleEvent;
import me.unariginal.genesisforms.items.BasePolymerBlockItem;
import me.unariginal.genesisforms.polymer.KeyItemsGroup;
import me.unariginal.genesisforms.utils.PokemonUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
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

public class PossessionItem extends BasePolymerBlockItem implements PokemonSelectingItem {
    private final FormSetting formSetting;

    public PossessionItem(Block block, Settings settings, PolymerModelData modelData, String itemID, List<String> lore, FormSetting formSetting) {
        super(block, settings, modelData, itemID, lore);
        this.formSetting = formSetting;
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public boolean canUseOnPokemon(@NotNull ItemStack stack, @NotNull Pokemon pokemon) {
        for (String species : formSetting.species) {
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
        if (CONFIG.generalSettings.disabledItems.contains(itemID)) return TypedActionResult.fail(itemStack);
        PokemonEntity pokemonEntity = pokemon.getEntity();
        boolean alreadyInForm = true;
        if (pokemon.getFeatures().stream().noneMatch(speciesFeature -> {
            if (speciesFeature.getName().equalsIgnoreCase(formSetting.featureName)) {
                if (speciesFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                    return stringSpeciesFeature.getValue().equalsIgnoreCase(formSetting.defaultValue);
                } else if (speciesFeature instanceof FlagSpeciesFeature flagSpeciesFeature) {
                    return Boolean.toString(flagSpeciesFeature.getEnabled()).equalsIgnoreCase(formSetting.defaultValue);
                }
            }
            return false;
        })) {
            alreadyInForm = false;

            float delay = 0;
            if (EVENTS.formChanges != null && EVENTS.formChanges.possessions != null) {
                String eventId = pokemon.getSpecies().getName().toLowerCase() + "_" + formSetting.defaultValue;
                EVENTS.formChanges.possessions.runEvent(eventId, pokemon, pokemonEntity);
                ParticleEvent particleEvent = EVENTS.formChanges.possessions.getAnimation(eventId);
                if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
            }

            if (pokemonEntity != null) {
                pokemonEntity.after(delay, () -> {
                    applyFeature(formSetting.featureName, formSetting.defaultValue, pokemon);
                    PokemonUtils.fixRotomMoves(pokemon);
                    pokemon.updateAspects();
                    pokemon.updateForm();
                    return Unit.INSTANCE;
                });
            } else {
                applyFeature(formSetting.featureName, formSetting.defaultValue, pokemon);
                PokemonUtils.fixRotomMoves(pokemon);
                pokemon.updateAspects();
                pokemon.updateForm();
            }
        }
//        } else {
//            // This is rotom light bulb (default) form
//            if (pokemon.getFeatures().stream().anyMatch(feature -> feature.getName().equalsIgnoreCase(formSetting.featureName))) {
//                alreadyInForm = false;
//
//                float delay = 0;
//                if (EVENTS.formChanges != null && EVENTS.formChanges.possessions != null) {
//                    String eventId = pokemon.getSpecies().getName().toLowerCase() + "_" + formSetting.defaultValue;
//                    EVENTS.formChanges.possessions.runEvent(eventId, pokemon, pokemonEntity);
//                    ParticleEvent particleEvent = EVENTS.formChanges.possessions.getAnimation(eventId);
//                    if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
//                }
//
//                if (pokemonEntity != null) {
//                    pokemonEntity.after(delay, () -> {
//                        pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(formSetting.featureName));
//                        PokemonUtils.fixRotomMoves(pokemon);
//                        pokemon.updateAspects();
//                        pokemon.updateForm();
//                        return Unit.INSTANCE;
//                    });
//                } else {
//                    pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(formSetting.featureName));
//                    PokemonUtils.fixRotomMoves(pokemon);
//                    pokemon.updateAspects();
//                    pokemon.updateForm();
//                }
//            }
//        }

        if (!alreadyInForm) {
            ItemStack returnItem = ItemStack.EMPTY;
            if (pokemon.getPersistentData().contains("possession_item")) {
                String possessionItem = pokemon.getPersistentData().getString("possession_item");
                if (KeyItemsGroup.possessionItems.containsKey(possessionItem)) {
                    returnItem = KeyItemsGroup.possessionItems.get(possessionItem).getDefaultStack();
                }
            }

            pokemon.getPersistentData().putString("possession_item", itemID);
            itemStack.decrementUnlessCreative(1, serverPlayerEntity);
            serverPlayerEntity.getInventory().offerOrDrop(returnItem);
        }

        return TypedActionResult.success(itemStack);
    }
}
