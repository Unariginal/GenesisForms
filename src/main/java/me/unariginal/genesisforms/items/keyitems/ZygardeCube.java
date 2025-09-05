package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
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
                    NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "ability");
                    user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false).build());
                    user.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("cube_mode_feedback")).replaceAll("%cube_mode%", "Ability")), true);
                } else if (NbtUtils.getNbt(user.getStackInHand(hand), GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("ability")) {
                    NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "form");
                    user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).build());
                    user.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("cube_mode_feedback")).replaceAll("%cube_mode%", "Form")), true);
                }
            } else {
                NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "form");
                user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).build());
                user.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("cube_mode_feedback")).replaceAll("%cube_mode%", "Form")), true);
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
                GenesisForms.INSTANCE.logInfo("[Genesis] Aspects: " + pokemon.getAspects());

                Ability ability = pokemon.getAbility();
                if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("10"))) {
                    new StringSpeciesFeature("percent_cells", "50").apply(pokemon);
                    pokemon.setAbility$common(ability);
                } else {
                    new StringSpeciesFeature("percent_cells", "10").apply(pokemon);
                    pokemon.setAbility$common(ability);
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