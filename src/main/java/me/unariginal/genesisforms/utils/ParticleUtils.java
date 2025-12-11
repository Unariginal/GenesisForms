package me.unariginal.genesisforms.utils;

import com.cobblemon.mod.common.net.messages.client.animation.PlayPosableAnimationPacket;
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormParticlePacket;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

public class ParticleUtils {
    public static void spawnParticle(String identifier, Vec3d pos, RegistryKey<World> worldKey) {
        new SpawnSnowstormParticlePacket(Identifier.of(identifier), pos).sendToPlayersAround(pos.getX(), pos.getY(), pos.getZ(), 64.0, worldKey, (p) -> false);
    }

    public static void megaEvolutionAnimation(Entity pokemon, String identifier) {
        PlayPosableAnimationPacket playPosableAnimationPacket = new PlayPosableAnimationPacket(pokemon.getId(), Set.of("q.bedrock_stateful('" + identifier + "', '" + identifier + "', 'endures_primary_animations');"), List.of());
        playPosableAnimationPacket.sendToPlayersAround(pokemon.getX(), pokemon.getY(), pokemon.getZ(), 128.0, pokemon.getWorld().getRegistryKey(), (p) -> false);
    }
}
