package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.items.BasePolymerBlockItem;
import me.unariginal.genesisforms.polymer.KeyItemsGroup;
import me.unariginal.genesisforms.utils.PokemonUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains(itemID)) return TypedActionResult.fail(itemStack);
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
            if (formSetting.defaultValue.equalsIgnoreCase("true") || formSetting.defaultValue.equalsIgnoreCase("false")) {
                new FlagSpeciesFeature(formSetting.featureName, Boolean.getBoolean(formSetting.defaultValue)).apply(pokemon);
            } else {
                new StringSpeciesFeature(formSetting.featureName, formSetting.defaultValue).apply(pokemon);
            }
        } else {
            // This is rotom light bulb (default) form
            if (pokemon.getFeatures().stream().anyMatch(feature -> feature.getName().equalsIgnoreCase(formSetting.featureName))) {
                alreadyInForm = false;
                pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(formSetting.featureName));

                PokemonUtils.fixRotomMoves(pokemon);
            }
        }

        pokemon.updateAspects();
        pokemon.updateForm();

        if (!alreadyInForm) {
            NbtCompound data = pokemon.getPersistentData();
            ItemStack returnItem = ItemStack.EMPTY;
            if (data.contains("possession_item")) {
                String possessionItem = data.getString("possession_item");
                if (KeyItemsGroup.possessionItems.containsKey(possessionItem)) {
                    returnItem = KeyItemsGroup.possessionItems.get(possessionItem).getDefaultStack();
                }
            }

            data.putString("possession_item", itemID);
            itemStack.decrementUnlessCreative(1, serverPlayerEntity);
            serverPlayerEntity.getInventory().offerOrDrop(returnItem);
            pokemon.setPersistentData$common(data);
        }

        return TypedActionResult.success(itemStack);
    }
}
