package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.*;
import kotlin.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = MoveActionResponse.class, remap = false)
public abstract class MoveActionResponseMixin {
    @Shadow
    private String moveName;
    @Shadow
    private String targetPnx;
    @Shadow
    private String gimmickID;

    /**
     * @author Cobblemon Team
     * @reason This is the 1.7 implementation but modified to account for Gimmick Multi-target moves, which are handled properly in 1.7
     */
    @Overwrite
    public boolean isValid(ActiveBattlePokemon activeBattlePokemon, ShowdownMoveset showdownMoveSet, boolean forceSwitch) {
        if (forceSwitch || showdownMoveSet == null) {
            return false;
        }

//        val move = showdownMoveSet.moves.find { it.id == moveName } ?: return false
//        val gimmickMove = move.gimmickMove
//        val validGimmickMove = gimmickMove != null && !gimmickMove.disabled
//        if (!validGimmickMove && !move.canBeUsed()) {
//            // No PP or disabled or something
//            return false
//        }
        InBattleMove move = showdownMoveSet.getMoves().stream().filter(inBattleMove -> inBattleMove.getId().equals(moveName)).findFirst().orElse(null);
        if (move == null) return false;
        InBattleGimmickMove gimmickMove = move.getGimmickMove();
        boolean validGimmickMove = gimmickMove != null && !gimmickMove.getDisabled();
        if (!validGimmickMove && !move.canBeUsed()) {
            // No PP or disabled or something
            return false;
        }

//        val availableTargets = (if (gimmickID != null && validGimmickMove) gimmickMove.target else move.target)
//            .targetList(activeBattlePokemon)?.takeIf { it.isNotEmpty() } ?: return true
        MoveTarget moveTarget = (gimmickID != null && gimmickMove != null) ? gimmickMove.getTarget() : move.getTarget();

        List<?> availableTargets = moveTarget.getTargetList().invoke(activeBattlePokemon);
        if (availableTargets == null || availableTargets.isEmpty()) return true;

        boolean isMultitargetGimmickInSingles =
                ("terastal".equals(gimmickID) || "dynamax".equals(gimmickID)) &&
                        (moveTarget == MoveTarget.allAdjacent ||
                                moveTarget == MoveTarget.allAdjacentFoes ||
                                moveTarget == MoveTarget.adjacentFoe) &&
                        activeBattlePokemon.getBattle().getFormat().getBattleType() == BattleTypes.INSTANCE.getSINGLES();

        if (this.targetPnx == null) {
            if (activeBattlePokemon.getBattle().getFormat().getBattleType() == BattleTypes.INSTANCE.getSINGLES() && moveTarget == MoveTarget.adjacentFoe) return true;
            return isMultitargetGimmickInSingles;
        }

        Pair<BattleActor, ActiveBattlePokemon> targetPokemon = activeBattlePokemon.getActor().getBattle().getActorAndActiveSlotFromPNX(this.targetPnx);
        if (!availableTargets.contains(targetPokemon.getSecond())) return false; // It's not a possible target.
        if (isMultitargetGimmickInSingles) {
            this.targetPnx = null;
            return true;
        }

        return true;
    }
}
