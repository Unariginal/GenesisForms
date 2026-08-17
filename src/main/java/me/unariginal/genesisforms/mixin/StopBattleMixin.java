package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.unariginal.genesisforms.handlers.CobblemonEventHandler.revertPartyForms;

@Mixin(value = PokemonBattle.class, remap = false)
public abstract class StopBattleMixin {
    @Inject(method = "stop", at = @At("HEAD"))
    private void resetFormsOnStop(CallbackInfo ci) {
        PokemonBattle battle = (PokemonBattle) (Object) this;
        revertPartyForms(battle);
    }
}
