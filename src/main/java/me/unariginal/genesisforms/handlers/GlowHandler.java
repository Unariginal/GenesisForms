package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.genesisforms.GenesisForms;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

public class GlowHandler {
    public static void applyGlowing(String id, String color, Pokemon pokemon, PokemonEntity pokemonEntity) {
        if (pokemonEntity == null) return;
        ServerScoreboard scoreboard = GenesisForms.INSTANCE.getServer().getScoreboard();

        String teamName = "glow_" + id + "_" + pokemon.getUuid().toString();
        Team team = scoreboard.getTeam(teamName);
        if (team == null) team = scoreboard.addTeam(teamName);

        Formatting teamColor = Formatting.byName(color);
        if (teamColor == null) teamColor = Formatting.WHITE;
        team.setColor(teamColor);

        scoreboard.addScoreHolderToTeam(pokemonEntity.getNameForScoreboard(), team);

        pokemonEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 999999, 1, false, false));
    }

    public static void removeGlowing(String id, Pokemon pokemon, PokemonEntity pokemonEntity) {
        ServerScoreboard scoreboard = GenesisForms.INSTANCE.getServer().getScoreboard();

        String teamName = "glow_" + id + "_" + pokemon.getUuid().toString();
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            scoreboard.removeTeam(team);
        }

        if (pokemonEntity != null)
            pokemonEntity.removeStatusEffect(StatusEffects.GLOWING);
    }
}
