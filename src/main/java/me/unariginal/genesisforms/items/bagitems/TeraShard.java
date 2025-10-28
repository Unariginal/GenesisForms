
package me.unariginal.genesisforms.items.bagitems;

import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.items.BasePolymerItem;
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

public class TeraShard extends BasePolymerItem implements PokemonSelectingItem {
    private final TeraType teraType;

    public TeraShard(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, TeraType teraType) {
        super(settings, polymerItem, modelData, itemID, lore);
        this.teraType = teraType;
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
        if (pokemon.getSpecies().getName().equalsIgnoreCase("ogerpon") || pokemon.getSpecies().getName().equalsIgnoreCase("terapagos")) return false;

        if (pokemon.getTeraType().getId().equals(teraType.getId())) return false;

        return stack.getCount() >= GenesisForms.INSTANCE.getConfig().teraShardsRequired;
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!this.canUseOnPokemon(itemStack, pokemon)) return TypedActionResult.fail(itemStack);
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains(itemID) || !GenesisForms.INSTANCE.getConfig().enableTera) return TypedActionResult.fail(itemStack);

        pokemon.setTeraType(teraType);

        if (GenesisForms.INSTANCE.getConfig().consumeTeraShards) itemStack.decrementUnlessCreative(GenesisForms.INSTANCE.getConfig().teraShardsRequired, serverPlayerEntity);

        serverPlayerEntity.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("tera_type_changed"), pokemon)), true);

        return TypedActionResult.success(itemStack);
    }
}
