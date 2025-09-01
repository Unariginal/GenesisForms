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
    @Shadow private String moveName;
    @Shadow private String targetPnx;
    @Shadow private String gimmickID;

    /**
     * @author Cobblemon Contributors (idk.. IntelliJ really wants me to put this comment here)
     * @reason Modified to account for gimmicks without gimmick moves
     *
     * https://gitlab.com/cable-mc/cobblemon/-/blob/main/common/src/main/kotlin/com/cobblemon/mod/common/battles/ShowdownActionRequest.kt#L118
     */
    @Overwrite
    public boolean isValid(ActiveBattlePokemon activeBattlePokemon, ShowdownMoveset showdownMoveSet, boolean forceSwitch) {
        if (forceSwitch || showdownMoveSet == null) {
            return false;
        }

        InBattleMove move = showdownMoveSet.getMoves().stream()
                .filter(inBattleMove -> inBattleMove.getId().equals(moveName))
                .findFirst()
                .orElse(null);
        if (move == null) return false;

        InBattleGimmickMove gimmickMove = move.getGimmickMove();
        boolean validGimmickMove = gimmickMove != null && !gimmickMove.getDisabled();
        if (!validGimmickMove && !move.canBeUsed()) return false;

        List<Targetable> availableTargets;
        if (gimmickID != null &&
                validGimmickMove &&
                // Mega and Tera don't have associated gimmick moves!
                !gimmickID.equalsIgnoreCase("mega") &&
                !gimmickID.equalsIgnoreCase("terastal")
        ) availableTargets = gimmickMove.getTarget().getTargetList().invoke(activeBattlePokemon);
        else availableTargets = move.getTarget().getTargetList().invoke(activeBattlePokemon);
        if (availableTargets == null || availableTargets.isEmpty()) return true;

        if (targetPnx == null) return false;
        Pair<BattleActor,ActiveBattlePokemon> activeBattlePokemonPair = activeBattlePokemon.getActor().getBattle().getActorAndActiveSlotFromPNX(targetPnx);
        return availableTargets.contains(activeBattlePokemonPair.getSecond());
    }
}