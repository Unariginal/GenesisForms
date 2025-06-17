package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.toast.Toast;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeraOrb extends SimplePolymerItem {
    PolymerModelData modelData;

    public TeraOrb(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = KeyItems.teraOrbModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("tera_orb")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("tera_orb") || !GenesisForms.INSTANCE.getConfig().enableTera) return ActionResult.PASS;
        if (entity instanceof PokemonEntity pokemonEntity) {
            ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
            if (player != null) {
                if (player.getUuid().equals(user.getUuid())) {
                    Toast toast = new Toast(
                            TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().teraToastTitle, pokemonEntity.getPokemon())),
                            TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().teraToastDescription, pokemonEntity.getPokemon())),
                            GenesisForms.INSTANCE.getMessagesConfig().teraToastUseShardIcon ?
                                    Registries.ITEM.get(Identifier.of("genesisforms:" + pokemonEntity.getPokemon().getTeraType().showdownId() + "_tera_shard")).getDefaultStack()
                                    : ItemStack.EMPTY,
                            Identifier.ofVanilla("toast/advancement"),
                            1F,
                            -1675545
                    );
                    toast.addListeners(player);
                    toast.expireAfter(GenesisForms.INSTANCE.getMessagesConfig().teraToastDisplaySeconds);
                }
            }
        }
        return ActionResult.PASS;
    }
}
