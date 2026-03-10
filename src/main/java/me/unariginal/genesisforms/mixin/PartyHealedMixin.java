package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.CobblemonEventHandler;
import me.unariginal.genesisforms.items.keyitems.accessories.TeraAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = PartyStore.class, remap = false)
public abstract class PartyHealedMixin {
    @Inject(method = "heal", at = @At("TAIL"))
    public void rechargeTeraOrbs(CallbackInfo ci) {
        PartyStore partyStore = (PartyStore) (Object) this;
        if (partyStore instanceof PlayerPartyStore playerPartyStore) {
            ServerPlayerEntity player = GenesisForms.INSTANCE.getServer().getPlayerManager().getPlayer(playerPartyStore.getPlayerUUID());
            if (player != null) {
                for (ItemStack itemStack : CobblemonEventHandler.getValidKeyItemSlots(player)) {
                    if (itemStack.getItem() instanceof TeraAccessory) {
                        if (itemStack.isDamaged()) {
                            itemStack.setDamage(0);
                        }
                    }
                }
            }
        }
    }
}
