package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.battles.ShowdownInterpreter;
import com.llamalad7.mixinextras.sugar.Local;
import me.unariginal.genesisforms.GenesisForms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShowdownInterpreter.class, remap = false)
public class BattleMessageDebugMixin {
    @Inject(method = "interpretMessage", at = @At("HEAD"))
    private void readMessage(CallbackInfo ci, @Local String message) {
        GenesisForms.logInfo(message);
    }
}
