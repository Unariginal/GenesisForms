package me.unariginal.genesisforms.data.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class ParticleEvent {
    public float formChangeDelaySeconds;
    public String particleResource;
    public double xOffset;
    public double yOffset;
    public double zOffset;

    public void spawnParticle(PokemonEntity pokemonEntity) {}
}
