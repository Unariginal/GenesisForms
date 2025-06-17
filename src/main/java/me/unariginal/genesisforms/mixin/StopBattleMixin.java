package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.FormHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
