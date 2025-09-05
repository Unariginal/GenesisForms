package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.events.battles.instruction.ZMoveUsedEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.AnimationConfig;
import me.unariginal.genesisforms.items.helditems.ZCrystal;
import me.unariginal.genesisforms.utils.ParticleUtils;
import net.minecraft.item.Item;

public class ZPowerHandler {
    public static Unit playAnimation(ZMoveUsedEvent event) {
        Pokemon pokemon = event.getPokemon().getEffectedPokemon();
        PokemonEntity pokemonEntity = event.getPokemon().getEntity();
        if (pokemonEntity == null) return Unit.INSTANCE;

        Item helditem = pokemon.heldItem().getItem();
        if (helditem instanceof ZCrystal zCrystal) {
            String crystalId = zCrystal.getItemID();
            for (AnimationConfig.ZPowerAnimation zPowerAnimation : GenesisForms.INSTANCE.getAnimationConfig().zPowerAnimations) {
                if (zPowerAnimation.crystal().equalsIgnoreCase(crystalId)) {
                    if (zPowerAnimation.enabled()) {
                        ParticleUtils.spawnParticle(zPowerAnimation.identifier(), pokemonEntity.getPos(), pokemonEntity.getWorld().getRegistryKey());
                    }
                    break;
                }
            }
        }

        return Unit.INSTANCE;
    }
}