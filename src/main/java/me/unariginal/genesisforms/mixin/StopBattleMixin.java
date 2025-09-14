package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.genesisforms.handlers.CobblemonEventHandler;
import me.unariginal.genesisforms.items.helditems.HeldBattleItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(value = PokemonBattle.class, remap = false)
public abstract class StopBattleMixin {
    @Final
    @Shadow
    public abstract Iterable<BattleActor> getActors();
    
    @Inject(method = "stop", at = @At("HEAD"))
    private void resetFormsOnStop(CallbackInfo ci) {
        for (BattleActor actor : this.getActors()) {
            for (BattlePokemon pokemon : actor.getPokemonList()) {
                CobblemonEventHandler.revertForm(pokemon.getEffectedPokemon(), false);
                CobblemonEventHandler.revertForm(pokemon.getOriginalPokemon(), false);
            }
        }
    }

    // 1.6.1 Macho Brace Impl
    @Inject(method = "end", at = @At("HEAD"))
    private void calculateMachoBrace(CallbackInfo ci) {
        for (BattleActor actor : this.getActors()) {
            List<BattlePokemon> faintedPokemons = actor.getPokemonList().stream().filter(pkmn -> pkmn.getHealth() <= 0).toList();
            for (BattleActor opponent : actor.getSide().getOppositeSide().getActors()) {
                List<BattlePokemon> opponentNonFaintedPokemons = opponent.getPokemonList().stream().filter(pkmn -> pkmn.getHealth() > 0).toList();
                for (BattlePokemon faintedPokemon : faintedPokemons) {
                    for (BattlePokemon opponentPokemon : opponentNonFaintedPokemons) {
                        Pokemon pokemon = opponentPokemon.getEffectedPokemon();
                        Item helditem = pokemon.heldItem().getItem();
                        if (helditem instanceof HeldBattleItem heldBattleItem) {
                            if (heldBattleItem.getShowdownID().equalsIgnoreCase("machobrace")) {
                                Cobblemon.INSTANCE.getEvYieldCalculator().calculate(opponentPokemon, faintedPokemon).forEach((stat, amount) -> {
                                    pokemon.getEvs().add(stat, amount);
                                });
                            }
                        }
                    }
                }
            }
        }
    }
}
