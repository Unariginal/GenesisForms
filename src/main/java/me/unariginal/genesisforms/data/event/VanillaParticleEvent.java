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

    @Override
    public void spawnParticle(PokemonEntity pokemonEntity) {
        Optional<ParticleType<?>> particleType = Registries.PARTICLE_TYPE.getOrEmpty(Identifier.of(particleResource));
        if (particleType.isPresent() && particleType.get() instanceof SimpleParticleType simpleParticleType) {
            RegistryKey<World> worldRegistryKey = pokemonEntity.getWorld().getRegistryKey();
            ServerWorld world = GenesisForms.INSTANCE.server.getWorld(worldRegistryKey);
            if (world != null)
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
