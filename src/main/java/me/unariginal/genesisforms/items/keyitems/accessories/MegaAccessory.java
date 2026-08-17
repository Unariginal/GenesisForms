package me.unariginal.genesisforms.items.keyitems.accessories;

import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.MegaEvolutionConfig;
import me.unariginal.genesisforms.handlers.CobblemonEventHandler;
import me.unariginal.genesisforms.items.BasePolymerItem;
import me.unariginal.genesisforms.items.helditems.Megastone;
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

import static me.unariginal.genesisforms.config.ConfigManager.*;

public class MegaAccessory extends BasePolymerItem implements PokemonSelectingItem {
    public MegaAccessory(Settings settings, Item polymerItem, PolymerModelData modelData, String itemId, List<String> lore) {
        super(settings, polymerItem, modelData, itemId, lore);
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            return this.use(player, player.getStackInHand(hand), false);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!canUseOnPokemon(itemStack, pokemon)) return TypedActionResult.fail(itemStack);
        if (CONFIG.generalSettings.disabledItems.contains(itemId) ||
                !(CONFIG.megaSettings.enableMegaEvolution && CONFIG.megaSettings.allowMegaOutsideBattles)
        ) return TypedActionResult.fail(itemStack);

        boolean isMega = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
        if (!isMega) {
            if (!GenesisForms.INSTANCE.playersWithMega.containsKey(serverPlayerEntity.getUuid())) {
                CobblemonEventHandler.megaEvolveLogic(pokemon, false, null, null);
            }
        } else {
            Item helditem = pokemon.heldItem().getItem();
            if (helditem instanceof Megastone megastone) {
                CobblemonEventHandler.revertMega(pokemon, megastone.getMegastoneData().featureName);
            } else {
                CobblemonEventHandler.revertMega(pokemon, "mega_evolution");
            }
        }

        pokemon.updateAspects();
        pokemon.updateForm();

        return TypedActionResult.success(itemStack);
    }

    @Override
    public boolean canUseOnPokemon(@NotNull ItemStack stack, @NotNull Pokemon pokemon) {
        if (pokemon.getEntity() != null && pokemon.getEntity().isBattling()) return false;

        Item helditem = pokemon.heldItem().getItem();
        if (helditem instanceof Megastone megastone) {
            MegaEvolutionConfig megaEvolutionData = megastone.getMegastoneData();
            return megaEvolutionData.canMegaEvolve(pokemon);
        } else {
            for (String itemlessMega : itemlessMegas) {
                MegaEvolutionConfig megaEvolutionData = MEGA_EVOLUTIONS.get(itemlessMega);
                if (megaEvolutionData != null && megaEvolutionData.canMegaEvolve(pokemon)) {
                    return true;
                }
            }
        }
        return false;
    }
}
