package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.*;
import kotlin.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = MoveActionResponse.class, remap = false)
public class MoveActionResponseMixin {
    @Shadow private String moveName;
    @Shadow private String targetPnx;
    @Shadow private String gimmickID;

    /**
     * @author Cobblemon Contributors
     * @reason Terastal target selection fix
     */
    @Overwrite
    public boolean isValid(ActiveBattlePokemon activeBattlePokemon, ShowdownMoveset showdownMoveSet, boolean forceSwitch) {
        if (forceSwitch || showdownMoveSet == null) {
            return false;
        }

        // val move = showdownMoveSet.moves.find { it.id == moveName } ?: return false
        InBattleMove move = showdownMoveSet.getMoves().stream()
                .filter(inBattleMove -> inBattleMove.getId().equals(moveName))
                .findFirst().orElse(null);
        if (move == null) return false;
        InBattleGimmickMove gimmickMove = move.getGimmickMove();
        boolean validGimmickMove = gimmickMove != null && !gimmickMove.getDisabled();
        if (!validGimmickMove && !move.canBeUsed()) {
            // No PP or disabled or something
            return false;
        }

        /*
        val availableTargets = (if (gimmickID != null && validGimmickMove) gimmickMove.target else move.target)
            .targetList(activeBattlePokemon)?.takeIf { it.isNotEmpty() } ?: return true
        */
        MoveTarget moveTarget;
        if (gimmickID != null && validGimmickMove &&
                // This is the only change, we need to grab the move targets of the move rather than the gimmick move if it's terastal, otherwise the move errors.
                !gimmickID.equalsIgnoreCase("terastal")) {
            moveTarget = gimmickMove.getTarget();
        } else moveTarget = move.getTarget();
        List<Targetable> availableTargets = moveTarget.getTargetList().invoke(activeBattlePokemon);
        if (availableTargets == null || availableTargets.isEmpty()) return true;

        String pnx = this.targetPnx;
        if (pnx == null) return false; // If the targets list is non-null then they need to have specified a target
        Pair<BattleActor, ActiveBattlePokemon> targetPair = activeBattlePokemon.getActor().getBattle().getActorAndActiveSlotFromPNX(pnx);
        ActiveBattlePokemon targetPokemon = targetPair.getSecond();
        if (!availableTargets.contains(targetPokemon)) {
            return false; // It's not a possible target.
        }

        return true;
    }
}
