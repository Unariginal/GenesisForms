
package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.CycledFormSetting;
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

public class FormCycleItem extends ConsumablePolymerItem implements PokemonSelectingItem {
    private final CycledFormSetting cycledFormSetting;

    public FormCycleItem(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, boolean consumable, CycledFormSetting cycledFormSetting) {
        super(settings, polymerItem, modelData, itemID, lore, consumable);
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
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains(itemID)) return TypedActionResult.fail(itemStack);

        if (cycledFormSetting.featureValues().size() == 1) {
            if (cycledFormSetting.featureValues().getFirst().equalsIgnoreCase("true") || cycledFormSetting.featureValues().getFirst().equalsIgnoreCase("false"))
                new FlagSpeciesFeature(cycledFormSetting.featureName(), Boolean.getBoolean(cycledFormSetting.featureValues().getFirst())).apply(pokemon);
            else
                new StringSpeciesFeature(cycledFormSetting.featureName(), cycledFormSetting.featureValues().getFirst()).apply(pokemon);
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
                if (newFeatureValue.equalsIgnoreCase("null"))
                    pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(cycledFormSetting.featureName()));
                else if (newFeatureValue.equalsIgnoreCase("true") || newFeatureValue.equalsIgnoreCase("false"))
                    new FlagSpeciesFeature(cycledFormSetting.featureName(), Boolean.getBoolean(newFeatureValue)).apply(pokemon);
                else
                    new StringSpeciesFeature(cycledFormSetting.featureName(), newFeatureValue).apply(pokemon);
            }
        }

        pokemon.updateAspects();
        pokemon.updateForm();

        if (consumable) itemStack.decrementUnlessCreative(1, serverPlayerEntity);

        return TypedActionResult.success(itemStack);
    }
}
