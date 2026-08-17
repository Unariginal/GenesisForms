package me.unariginal.genesisforms.data.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormParticlePacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class SnowstormParticleEvent extends ParticleEvent {
    @Override
    public void spawnParticle(PokemonEntity pokemonEntity) {
        SpawnSnowstormParticlePacket snowstormParticlePacket = new SpawnSnowstormParticlePacket(
                Identifier.of(particleResource),
                new Vec3d(
                        pokemonEntity.getX() + xOffset,
                        pokemonEntity.getY() + yOffset,
                        pokemonEntity.getZ() + zOffset
                )
        );

        snowstormParticlePacket.sendToPlayersAround(
                pokemonEntity.getX() + xOffset,
                pokemonEntity.getY() + yOffset,
                pokemonEntity.getZ() + zOffset,
                64,
                pokemonEntity.getWorld().getRegistryKey(),
                player -> false
        );
    }
}
