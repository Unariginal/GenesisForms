package me.unariginal.genesisforms.data.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class ParticleEvent {
    public int formChangeDelaySeconds;
    public String particleResource;
    public double xOffset;
    public double yOffset;
    public double zOffset;

    public ParticleEvent(int formChangeDelaySeconds, String particleResource, double xOffset, double yOffset, double zOffset) {
        this.formChangeDelaySeconds = formChangeDelaySeconds;
        this.particleResource = particleResource;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public void spawnParticle(PokemonEntity pokemonEntity) {}
}
