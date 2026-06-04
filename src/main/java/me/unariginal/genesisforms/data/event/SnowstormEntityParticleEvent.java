package me.unariginal.genesisforms.data.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormEntityParticlePacket;
import net.minecraft.util.Identifier;

import java.util.List;

public class SnowstormEntityParticleEvent extends ParticleEvent {
    public List<String> locators;

    public SnowstormEntityParticleEvent(int formChangeDelaySeconds, String particleResource, double xOffset, double yOffset, double zOffset, List<String> locators) {
        super(formChangeDelaySeconds, particleResource, xOffset, yOffset, zOffset);
        this.locators = locators;
    }

    @Override
    public void spawnParticle(PokemonEntity pokemonEntity) {
        SpawnSnowstormEntityParticlePacket snowstormEntityParticlePacket = new SpawnSnowstormEntityParticlePacket(
                Identifier.of(particleResource),
                pokemonEntity.getId(),
                locators,
                null,
                null
        );

        snowstormEntityParticlePacket.sendToPlayersAround(
                pokemonEntity.getX(),
                pokemonEntity.getY(),
                pokemonEntity.getZ(),
                64,
                pokemonEntity.getWorld().getRegistryKey(),
                player -> false
        );
    }
}
