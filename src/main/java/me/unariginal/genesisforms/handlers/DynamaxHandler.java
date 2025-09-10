package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.battle.BattleTransformPokemonPacket;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.events.DynamaxStartEvent;
import me.unariginal.genesisforms.events.DynamaxEventEnd;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.*;

public class DynamaxHandler {
    private static final GenesisForms gf = GenesisForms.INSTANCE;
    private static final Map<PokemonEntity, ScalingData> scalingAnimations = new HashMap<>();

    private static void updateScales() {
        Iterator<Map.Entry<PokemonEntity, ScalingData>> iterator = scalingAnimations.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<PokemonEntity, ScalingData> entry = iterator.next();

            PokemonEntity entity = entry.getKey();
            ScalingData data = entry.getValue();

            data.currentTick++;

            if (entity != null && !entity.isRemoved()) {
                EntityAttributeInstance scaleAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                if (scaleAttribute != null) {
                    float progress = Math.min(1.0f, (float) data.currentTick / data.durationTicks);
                    float newScale = data.startScale + (data.targetScale - data.startScale) * progress;

                    scaleAttribute.setBaseValue(newScale);
                }

                if (data.currentTick >= data.durationTicks) {
                    iterator.remove();
                }
            } else {
                iterator.remove();
            }
        }
    }

    private static class ScalingData {
        final float startScale;
        final float targetScale;
        final int durationTicks;
        int currentTick;

        public ScalingData(float startScale, float targetScale, int durationTicks, int currentTick) {
            this.startScale = startScale;
            this.targetScale = targetScale;
            this.durationTicks = durationTicks;
            this.currentTick = currentTick;
        }
    }

    public static void register() {
        DynamaxStartEvent.EVENT.register(((battle, pokemon, gmax) -> {
            // Handle gmax form change
            if (gmax && GenesisForms.INSTANCE.getConfig().enableGigantamax) {
                new StringSpeciesFeature("dynamax_form", "gmax").apply(pokemon.getEffectedPokemon());
                for (ActiveBattlePokemon activeBattlePokemon : battle.getActivePokemon()) {
                    if (activeBattlePokemon.getBattlePokemon() != null &&
                            activeBattlePokemon.getBattlePokemon().getEffectedPokemon().getOwnerPlayer() == pokemon.getEffectedPokemon().getOwnerPlayer() &&
                            activeBattlePokemon.getBattlePokemon() == pokemon) {
                        battle.sendSidedUpdate(
                                activeBattlePokemon.getActor(),
                                new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, true),
                                new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, false),
                                false
                        );
                    }
                }
            }

            // Start scaling the entity to the target size
            if (pokemon.getEntity() != null) {
                if (gf.getAnimationConfig().dynamaxEnabled) {
                    EntityAttributeInstance scaleAttribute = pokemon.getEntity().getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                    if (scaleAttribute != null) {
                        float startScale = (float) scaleAttribute.getBaseValue();
                        int duration = gf.getAnimationConfig().scalingTicks;
                        ScalingData scalingData = new ScalingData(
                                startScale,
                                gf.getAnimationConfig().dynamaxScale,
                                duration,
                                0
                        );
                        scalingAnimations.put(pokemon.getEntity(), scalingData);
                    }
                }
            }
        }));

        DynamaxEventEnd.EVENT.register((battle, pokemon) -> {
            new StringSpeciesFeature("dynamax_form", "none").apply(pokemon.getEffectedPokemon());
            for (ActiveBattlePokemon activeBattlePokemon : battle.getActivePokemon()) {
                if (activeBattlePokemon.getBattlePokemon() != null && activeBattlePokemon.getBattlePokemon().getEffectedPokemon().getOwnerPlayer() == pokemon.getEffectedPokemon().getOwnerPlayer() && activeBattlePokemon.getBattlePokemon() == pokemon) {
                    battle.sendSidedUpdate(
                            activeBattlePokemon.getActor(),
                            new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, true),
                            new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, false),
                            false
                    );
                }
            }
            PokemonEntity pokemonEntity = pokemon.getEntity();

            if (pokemonEntity != null) {
                EntityAttributeInstance scaleAttribute = pokemon.getEntity().getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                if (scaleAttribute != null) {
                    float startScale = (float) scaleAttribute.getBaseValue();
                    int duration = gf.getAnimationConfig().scalingTicks;
                    ScalingData scalingData = new ScalingData(
                            startScale,
                            1.0f,
                            duration,
                            0
                    );
                    scalingAnimations.put(pokemon.getEntity(), scalingData);
                }
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> updateScales());
    }
}