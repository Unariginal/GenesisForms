package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.handlers.FormHandler;
import net.minecraft.component.ComponentMap;
import net.minecraft.server.network.ServerPlayerEntity;
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
                Pokemon effectedPokemon = pokemon.getEffectedPokemon();
                Pokemon originalPokemon = pokemon.getOriginalPokemon();
                
                FormHandler.revert_forms(effectedPokemon, true);
                FormHandler.revert_forms(originalPokemon, true);
                
                // Just in case cause megas are being wierd
                ServerPlayerEntity player = effectedPokemon.getOwnerPlayer();
                if (effectedPokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("x"))) {
                    new StringSpeciesFeature(GenesisForms.INSTANCE.getConfig().megaXFeatureName, "none").apply(effectedPokemon);
                    effectedPokemon.setTradeable(true);
                    if (player != null) {
                        GenesisForms.INSTANCE.getPlayersWithMega().remove(player.getUuid());
                        GenesisForms.INSTANCE.getMegaEvolvedThisBattle().remove(player.getUuid());
                    }
                } else if (effectedPokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("y"))) {
                    new StringSpeciesFeature(GenesisForms.INSTANCE.getConfig().megaYFeatureName, "none").apply(effectedPokemon);
                    effectedPokemon.setTradeable(true);
                    if (player != null) {
                        GenesisForms.INSTANCE.getPlayersWithMega().remove(player.getUuid());
                        GenesisForms.INSTANCE.getMegaEvolvedThisBattle().remove(player.getUuid());
                    }
                } else if (effectedPokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"))) {
                    new StringSpeciesFeature(GenesisForms.INSTANCE.getConfig().megaFeatureName, "none").apply(effectedPokemon);
                    effectedPokemon.setTradeable(true);
                    if (player != null) {
                        GenesisForms.INSTANCE.getPlayersWithMega().remove(player.getUuid());
                        GenesisForms.INSTANCE.getMegaEvolvedThisBattle().remove(player.getUuid());
                    }
                }


                player = originalPokemon.getOwnerPlayer();
                if (originalPokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("x"))) {
                    new StringSpeciesFeature(GenesisForms.INSTANCE.getConfig().megaXFeatureName, "none").apply(originalPokemon);
                    originalPokemon.setTradeable(true);
                    if (player != null) {
                        GenesisForms.INSTANCE.getPlayersWithMega().remove(player.getUuid());
                        GenesisForms.INSTANCE.getMegaEvolvedThisBattle().remove(player.getUuid());
                    }
                } else if (originalPokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("y"))) {
                    new StringSpeciesFeature(GenesisForms.INSTANCE.getConfig().megaYFeatureName, "none").apply(originalPokemon);
                    originalPokemon.setTradeable(true);
                    if (player != null) {
                        GenesisForms.INSTANCE.getPlayersWithMega().remove(player.getUuid());
                        GenesisForms.INSTANCE.getMegaEvolvedThisBattle().remove(player.getUuid());
                    }
                } else if (originalPokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"))) {
                    new StringSpeciesFeature(GenesisForms.INSTANCE.getConfig().megaFeatureName, "none").apply(originalPokemon);
                    originalPokemon.setTradeable(true);
                    if (player != null) {
                        GenesisForms.INSTANCE.getPlayersWithMega().remove(player.getUuid());
                        GenesisForms.INSTANCE.getMegaEvolvedThisBattle().remove(player.getUuid());
                    }
                }
            }
        }
    }

    // TODO: 1.7 has an EvGainedEvent, this is a reminder to myself to use it.
    @Inject(method = "end", at = @At("HEAD"))
    private void calculateMachoBrace(CallbackInfo ci) {
        for (BattleActor actor : this.getActors()) {
            List<BattlePokemon> faintedPokemons = actor.getPokemonList().stream().filter(pkmn -> pkmn.getHealth() <= 0).toList();
            for (BattleActor opponent : actor.getSide().getOppositeSide().getActors()) {
                List<BattlePokemon> opponentNonFaintedPokemons = opponent.getPokemonList().stream().filter(pkmn -> pkmn.getHealth() > 0).toList();
                for (BattlePokemon faintedPokemon : faintedPokemons) {
                    for (BattlePokemon opponentPokemon : opponentNonFaintedPokemons) {
                        Pokemon pokemon = opponentPokemon.getEffectedPokemon();
                        ComponentMap components = pokemon.heldItem().getComponents();
                        if (components.contains(DataComponents.HELD_ITEM)) {
                            String heldItemComponent = components.get(DataComponents.HELD_ITEM);
                            if (heldItemComponent != null && !heldItemComponent.isEmpty()) {
                                if (heldItemComponent.equalsIgnoreCase("macho_brace") || heldItemComponent.equalsIgnoreCase("machobrace")) {
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
}
