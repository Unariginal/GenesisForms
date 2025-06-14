package me.unariginal.genesisforms.items.bagitems;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.BagItems;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaxSoup extends SimplePolymerItem implements PokemonSelectingItem {
    PolymerModelData modelData;

    public MaxSoup(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = BagItems.maxSoupModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("max_soup")) {
            tooltip.add(TextUtils.deserialize(line));
        }
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
    public @NotNull TypedActionResult<ItemStack> use(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack) {
        return PokemonSelectingItem.DefaultImpls.use(this, serverPlayerEntity, itemStack);
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!this.canUseOnPokemon(pokemon)) return TypedActionResult.fail(itemStack);
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("max_soup") ||
            !GenesisForms.INSTANCE.getConfig().enableDynamax ||
            !GenesisForms.INSTANCE.getConfig().enableGigantamax) return TypedActionResult.fail(itemStack);
        pokemon.setGmaxFactor(!pokemon.getGmaxFactor());
        if (pokemon.getGmaxFactor()) {
            serverPlayerEntity.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("gmax_factor_applied"), pokemon)), true);
        } else {
            serverPlayerEntity.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("gmax_factor_removed"), pokemon)), true);
        }

        itemStack.decrementUnlessCreative(1, serverPlayerEntity);
        pokemon.updateAspects();
        return TypedActionResult.success(itemStack);
    }

    @Override
    public void applyToBattlePokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull BattlePokemon battlePokemon) {

    }

    @Override
    public boolean canUseOnPokemon(@NotNull Pokemon pokemon) {
        return pokemon.getSpecies().getForms().stream().anyMatch(formData -> formData.getLabels().contains("gmax"));
    }

    @Override
    public boolean canUseOnBattlePokemon(@NotNull BattlePokemon battlePokemon) {
        return false;
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactWithSpecificBattle(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull BattlePokemon battlePokemon) {
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactGeneral(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack) {
        return PokemonSelectingItem.DefaultImpls.interactGeneral(this, serverPlayerEntity, itemStack);
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactGeneralBattle(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull BattleActor battleActor) {
        return TypedActionResult.fail(itemStack);
    }
}
