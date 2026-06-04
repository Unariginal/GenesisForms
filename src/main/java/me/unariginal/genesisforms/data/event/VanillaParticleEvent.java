package me.unariginal.genesisforms.data.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.unariginal.genesisforms.GenesisForms;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

public class VanillaParticleEvent extends ParticleEvent {
    public int count;
    public double deltaX;
    public double deltaY;
    public double deltaZ;
    public double speed;

    public VanillaParticleEvent(
            int formChangeDelaySeconds,
            String particleResource,
            double xOffset,
            double yOffset,
            double zOffset,
            int count,
            double deltaX,
            double deltaY,
            double deltaZ,
            double speed
    ) {
        super(formChangeDelaySeconds, particleResource, xOffset, yOffset, zOffset);
        this.count = count;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
        this.speed = speed;
    }

    @Override
    public void spawnParticle(PokemonEntity pokemonEntity) {
        Optional<ParticleType<?>> particleType = Registries.PARTICLE_TYPE.getOrEmpty(Identifier.of(particleResource));
        if (particleType.isPresent() && particleType.get() instanceof SimpleParticleType simpleParticleType) {
            RegistryKey<World> worldRegistryKey = pokemonEntity.getWorld().getRegistryKey();
            ServerWorld world = GenesisForms.INSTANCE.getServer().getWorld(worldRegistryKey);
            world.spawnParticles(
                    simpleParticleType,
                    pokemonEntity.getX() + xOffset,
                    pokemonEntity.getY() + yOffset,
                    pokemonEntity.getZ() + zOffset,
                    count,
                    deltaX,
                    deltaY,
                    deltaZ,
                    speed
            );
        }
    }
}
