package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.interpreter.instructions.EndInstruction;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import me.unariginal.genesisforms.events.DynamaxEventEnd;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EndInstruction.class, remap = false)
public class EndInstructionMixin {
    @Shadow
    @Final
    private BattleMessage message;

    @Inject(method = "invoke", at = @At("HEAD"), remap = false)
    private void injectBeforeInvoke(PokemonBattle battle, CallbackInfo ci) {
        // |-end|p1a: <uuid>|Dynamax
        boolean dynamax = message.getRawMessage().contains("Dynamax");

        if (dynamax) {
            BattlePokemon pokemon = message.battlePokemon(0, battle);
            DynamaxEventEnd.EVENT.invoker().onDynamaxEnd(battle, pokemon);
        }
    }
}