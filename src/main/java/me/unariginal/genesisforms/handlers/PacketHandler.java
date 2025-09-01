package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.net.messages.client.battle.BattleTransformPokemonPacket;
import com.cobblemon.mod.common.net.messages.client.battle.BattleUpdateTeamPokemonPacket;
import com.cobblemon.mod.common.net.messages.client.pokemon.update.AbilityUpdatePacket;
import com.cobblemon.mod.common.pokemon.Pokemon;

public class PacketHandler {
    public static void updatePackets(PokemonBattle battle, BattlePokemon battlePokemon, boolean abilities) {
        Pokemon pokemon = battlePokemon.getEffectedPokemon();

        if (abilities) {
            battle.sendUpdate(new AbilityUpdatePacket(battlePokemon::getEffectedPokemon, pokemon.getAbility().getTemplate()));
            battle.sendUpdate(new BattleUpdateTeamPokemonPacket(pokemon));
        }

        for (ActiveBattlePokemon activeBattlePokemon : battle.getActivePokemon()) {
            if (
                    activeBattlePokemon.getBattlePokemon() != null
                    && activeBattlePokemon.getBattlePokemon().getEffectedPokemon().getOwnerPlayer() == battlePokemon.getEffectedPokemon().getOwnerPlayer()
                    && activeBattlePokemon.getBattlePokemon() == battlePokemon
            ) {
                battle.sendSidedUpdate(
                        activeBattlePokemon.getActor(),
                        new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), battlePokemon, true),
                        new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), battlePokemon, false),
                        false
                );
            }
        }
    }
}