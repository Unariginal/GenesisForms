
package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.items.MiscItemsConfig;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.items.BasePolymerItem;
import me.unariginal.genesisforms.utils.NbtUtils;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
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

public class ZygardeCube extends BasePolymerItem implements PokemonSelectingItem {
    public ZygardeCube(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore) {
        super(settings, polymerItem, modelData, itemID, lore);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("zygarde_cube")) return TypedActionResult.pass(user.getStackInHand(hand));

        if (user.isSneaking()) {
            if (NbtUtils.getNbt(user.getStackInHand(hand), GenesisForms.MOD_ID).contains(DataKeys.NBT_CUBE_MODE)) {
                if (NbtUtils.getNbt(user.getStackInHand(hand), GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("form")) {
                    if (MiscItemsConfig.miscItemData.zygardeCube.enableAbilitySwap) {
                        NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "ability");
                        user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false).build());
                        user.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("cube_mode_feedback")).replaceAll("%cube_mode%", "Ability")), true);
                    }
                } else if (NbtUtils.getNbt(user.getStackInHand(hand), GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("ability")) {
                    if (MiscItemsConfig.miscItemData.zygardeCube.enableFormSwap) {
                        NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "form");
                        user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).build());
                        user.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("cube_mode_feedback")).replaceAll("%cube_mode%", "Form")), true);
                    }
                }
            } else {
                if (MiscItemsConfig.miscItemData.zygardeCube.enableFormSwap) {
                    NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "form");
                    user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).build());
                    user.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("cube_mode_feedback")).replaceAll("%cube_mode%", "Form")), true);
                } else {
                    NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "ability");
                    user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false).build());
                    user.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("cube_mode_feedback")).replaceAll("%cube_mode%", "Ability")), true);
                }
            }
        } else {
            if (user instanceof ServerPlayerEntity player) {
                return this.use(player, player.getStackInHand(hand));
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public void swapAbility(Pokemon pokemon) {
        if (MiscItemsConfig.miscItemData.zygardeCube.enableAbilitySwap) {
            AbilityTemplate powerconstruct = Abilities.INSTANCE.get("powerconstruct");
            AbilityTemplate aurabreak = Abilities.INSTANCE.get("aurabreak");
            if (powerconstruct != null && aurabreak != null) {
                if (pokemon.getAbility().getTemplate().getName().equalsIgnoreCase(aurabreak.getName())) {
                    GenesisForms.INSTANCE.logInfo("[Genesis] Switched Zygarde's ability to Power Construct.");
                    pokemon.setAbility$common(powerconstruct.create(false, Priority.LOW));
                } else {
                    GenesisForms.INSTANCE.logInfo("[Genesis] Switched Zygarde's ability to Aura Break.");
                    pokemon.setAbility$common(aurabreak.create(false, Priority.LOW));
                }
            }
        }
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public boolean canUseOnPokemon(@NotNull ItemStack stack, @NotNull Pokemon pokemon) {
        return pokemon.getSpecies().getName().equalsIgnoreCase("zygarde");
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!this.canUseOnPokemon(itemStack, pokemon)) return TypedActionResult.fail(itemStack);
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("zygarde_cube")) return TypedActionResult.fail(itemStack);
        if (serverPlayerEntity.isSneaking()) return TypedActionResult.fail(itemStack);

        if (NbtUtils.getNbt(itemStack, GenesisForms.MOD_ID).contains(DataKeys.NBT_CUBE_MODE)) {
            if (NbtUtils.getNbt(itemStack, GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("form")) {
                if (MiscItemsConfig.miscItemData.zygardeCube.enableFormSwap) {
                    GenesisForms.INSTANCE.logInfo("[Genesis] Aspects: " + pokemon.getAspects());

                    SpeciesFeature currentFeature = pokemon.getFeature(MiscItemsConfig.miscItemData.zygardeCube.featureName);
                    String currentFeatureValue = "null";
                    if (currentFeature != null) {
                        if (currentFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                            currentFeatureValue = stringSpeciesFeature.getValue();
                        } else if (currentFeature instanceof FlagSpeciesFeature flagSpeciesFeature) {
                            currentFeatureValue = String.valueOf(flagSpeciesFeature.getEnabled());
                        }
                    }

                    Ability ability = pokemon.getAbility();

                    if (MiscItemsConfig.miscItemData.zygardeCube.featureValues.contains(currentFeatureValue)) {
                        int index = MiscItemsConfig.miscItemData.zygardeCube.featureValues.indexOf(currentFeatureValue) + 1;
                        if (index >= MiscItemsConfig.miscItemData.zygardeCube.featureValues.size()) index = 0;

                        String newFeatureValue = MiscItemsConfig.miscItemData.zygardeCube.featureValues.get(index);
                        if (newFeatureValue.equalsIgnoreCase("null"))
                            pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(MiscItemsConfig.miscItemData.zygardeCube.featureName));
                        else if (newFeatureValue.equalsIgnoreCase("true") || newFeatureValue.equalsIgnoreCase("false"))
                            new FlagSpeciesFeature(MiscItemsConfig.miscItemData.zygardeCube.featureName, Boolean.getBoolean(newFeatureValue)).apply(pokemon);
                        else
                            new StringSpeciesFeature(MiscItemsConfig.miscItemData.zygardeCube.featureName, newFeatureValue).apply(pokemon);
                    }

                    pokemon.updateAspects();
                    pokemon.updateForm();
                    pokemon.updateAbility(ability);
                }
            } else if (NbtUtils.getNbt(itemStack, GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("ability")) {
                swapAbility(pokemon);
            }
        } else {
            NbtUtils.setNbtString(itemStack, GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "ability");
            itemStack.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false).build());

            swapAbility(pokemon);
        }

        return TypedActionResult.success(itemStack);
    }
}
