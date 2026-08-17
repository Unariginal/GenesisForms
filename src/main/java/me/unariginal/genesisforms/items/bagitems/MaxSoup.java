
package me.unariginal.genesisforms.items.bagitems;

import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.items.ConsumablePolymerItem;
import me.unariginal.genesisforms.utils.TextUtils;
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
import static me.unariginal.genesisforms.config.ConfigManager.MESSAGES;

public class MaxSoup extends ConsumablePolymerItem implements PokemonSelectingItem {
    public MaxSoup(Settings settings, Item polymerItem, PolymerModelData modelData, List<String> lore, boolean consumable) {
        super(settings, polymerItem, modelData, "max_soup", lore, consumable);
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            return this.use(player, player.getStackInHand(hand));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public boolean canUseOnPokemon(@NotNull ItemStack stack, @NotNull Pokemon pokemon) {
        return pokemon.getSpecies().getForms().stream().anyMatch(formData -> formData.getLabels().contains("gmax"));
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!this.canUseOnPokemon(itemStack, pokemon)) return TypedActionResult.fail(itemStack);
        if (CONFIG.generalSettings.disabledItems.contains("max_soup") ||
                !CONFIG.dynamaxSettings.enableDynamax ||
                !CONFIG.dynamaxSettings.enableGigantamax) return TypedActionResult.fail(itemStack);
        pokemon.setGmaxFactor(!pokemon.getGmaxFactor());
        if (pokemon.getGmaxFactor()) {
            serverPlayerEntity.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.gmaxFactorApplied, pokemon)), true);
        } else {
            serverPlayerEntity.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.gmaxFactorRemoved, pokemon)), true);
        }
        if (consumable) itemStack.decrementUnlessCreative(1, serverPlayerEntity);
        pokemon.updateAspects();
        return TypedActionResult.success(itemStack);
    }
}
