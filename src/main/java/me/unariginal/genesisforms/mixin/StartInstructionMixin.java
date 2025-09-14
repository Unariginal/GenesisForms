package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.interpreter.instructions.StartInstruction;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import me.unariginal.genesisforms.events.DynamaxStartEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StartInstruction.class, remap = false)
public class StartInstructionMixin {
    @Shadow
    @Final
    private BattleMessage message;

    @Inject(method = "invoke", at = @At("HEAD"), remap = false)
    private void injectBeforeInvoke(PokemonBattle battle, CallbackInfo ci) {
        // |-start|p1a: <uuid>|Dynamax|
        boolean dynamax = message.getRawMessage().contains("Dynamax");
        boolean gmax = message.getRawMessage().contains("Gmax");

        if (dynamax) {
            BattlePokemon pokemon =  message.battlePokemon(0, battle);
            DynamaxStartEvent.EVENT.invoker().onDynamaxStart(battle, pokemon, gmax);
        }
    }
}