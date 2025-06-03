package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public class RevealGlass extends SimplePolymerItem {
    PolymerModelData modelData;

    public RevealGlass(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get("reveal_glass"));
        return super.getPolymerItem(itemStack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        this.modelData = KeyItems.revealGlassModelData;
        return this.modelData.value();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("reveal_glass")) return ActionResult.PASS;
        if (entity instanceof PokemonEntity pokemonEntity &&
                (pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("tornadus") ||
                        pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("thundurus") ||
                        pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("landorus") ||
                        pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("enamorus"))) {
            ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
            if (player != null) {
                if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                    if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("therian"))) {
                        new StringSpeciesFeature("mirror_forme", "incarnate").apply(pokemonEntity.getPokemon());
                    } else {
                        new StringSpeciesFeature("mirror_forme", "therian").apply(pokemonEntity.getPokemon());
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
