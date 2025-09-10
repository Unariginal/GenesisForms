package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.CobblemonEventHandler;
import me.unariginal.genesisforms.items.keyitems.accessories.TeraAccessory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = HealingMachineBlockEntity.class, remap = false)
public class PartyHealedMixin {
    @Inject(method = "completeHealing", at = @At("TAIL"))
    public void rechargeTeraOrbs(CallbackInfo ci, @Local UUID currentUser) {
        ServerPlayerEntity player = GenesisForms.INSTANCE.getServer().getPlayerManager().getPlayer(currentUser);
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
