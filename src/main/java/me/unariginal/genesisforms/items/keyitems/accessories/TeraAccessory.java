package me.unariginal.genesisforms.items.keyitems.accessories;

import com.cobblemon.mod.common.api.toast.Toast;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.items.BasePolymerItem;
import me.unariginal.genesisforms.utils.PokemonUtils;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.List;

import static me.unariginal.genesisforms.config.ConfigManager.CONFIG;
import static me.unariginal.genesisforms.config.ConfigManager.MESSAGES;

public class TeraAccessory extends BasePolymerItem {
    public boolean requiresCharge = true;

    public TeraAccessory(Settings settings, Item polymerItem, PolymerModelData modelData, String itemId, List<String> lore) {
        super(settings, polymerItem, modelData, itemId, lore);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (CONFIG.generalSettings.disabledItems.contains(itemId) || !CONFIG.teraSettings.enableTera) super.useOnEntity(stack, user, entity, hand);
        if (entity instanceof PokemonEntity pokemonEntity) {
            if (PokemonUtils.playerOwnsPokemon(user, pokemonEntity.getPokemon())) {
                ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();

                Toast toast = new Toast(
                        TextUtils.deserialize(TextUtils.parse(MESSAGES.teraToastSettings.toastTitle, pokemonEntity.getPokemon())),
                        TextUtils.deserialize(TextUtils.parse(MESSAGES.teraToastSettings.toastDescription, pokemonEntity.getPokemon())),
                        MESSAGES.teraToastSettings.useShardIcon ?
                                Registries.ITEM.get(Identifier.of("genesisforms:" + pokemonEntity.getPokemon().getTeraType().showdownId() + "_tera_shard")).getDefaultStack()
                                : ItemStack.EMPTY,
                        Identifier.ofVanilla("toast/advancement"),
                        1F,
                        -1675545
                );
                toast.addListeners(player);
                toast.expireAfter(MESSAGES.teraToastSettings.displaySeconds);
            }
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}
