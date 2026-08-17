package me.unariginal.genesisforms.items.keyitems.accessories;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.items.BasePolymerItem;
import me.unariginal.genesisforms.utils.PokemonUtils;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.List;

import static me.unariginal.genesisforms.config.ConfigManager.CONFIG;
import static me.unariginal.genesisforms.config.ConfigManager.MESSAGES;

public class DynamaxAccessory extends BasePolymerItem {
    public DynamaxAccessory(Settings settings, Item polymerItem, PolymerModelData modelData, String itemId, List<String> lore) {
        super(settings, polymerItem, modelData, itemId, lore);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (CONFIG.generalSettings.disabledItems.contains(itemId) || !CONFIG.dynamaxSettings.enableDynamax || !CONFIG.dynamaxSettings.enableGigantamax) return super.useOnEntity(stack, user, entity, hand);

        if (entity instanceof PokemonEntity pokemonEntity) {
            if (PokemonUtils.playerOwnsPokemon(user, pokemonEntity.getPokemon())) {
                if (pokemonEntity.getPokemon().getGmaxFactor()) {
                    user.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.hasGmaxFactor, pokemonEntity.getPokemon())), true);
                } else {
                    user.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.doesNotHaveGmaxFactor, pokemonEntity.getPokemon())), true);
                }
            }
        }

        return super.useOnEntity(stack, user, entity, hand);
    }
}
