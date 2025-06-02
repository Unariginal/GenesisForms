package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.battles.interpreter.instructions.StartInstruction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Credit to Mega Showdown for this Mixin logic!
 */
@Mixin(value = StartInstruction.class, remap = false)
public interface StartInstructionAccessor {
    @Accessor("message")
    BattleMessage getMessage();
}
