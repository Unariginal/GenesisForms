package me.unariginal.genesisforms.utils;

import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormParticlePacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleUtils {
    public static void spawnParticle(String identifier, Vec3d pos, RegistryKey<World> worldKey) {
        new SpawnSnowstormParticlePacket(Identifier.of(identifier), pos).sendToPlayersAround(pos.getX(), pos.getY(), pos.getZ(), 64.0, worldKey, (p) -> false);
    }
}
