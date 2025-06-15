package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.battle.BattleTransformPokemonPacket;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.events.DynamaxEvent;
import me.unariginal.genesisforms.events.DynamaxEventEnd;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

/**
 * Adapted from Mega Showdown @ yajatkaul
 */
public class DynamaxHandler {
    private static final GenesisForms gf = GenesisForms.INSTANCE;
    private static final Map<UUID, ScalingData> activeScalingAnimations = new HashMap<>();
    private static final WeakHashMap<UUID, PokemonEntity> scalingEntities = new WeakHashMap<>();
    private static MinecraftServer server;

    public static void register() {
        DynamaxEvent.EVENT.register(((battle, pokemon, gmax) -> {
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

            if (pokemon.getEntity() != null) {
                if (gf.getAnimationConfig().dynamaxEnabled) {
                    startScaling(pokemon.getEntity(), gf.getAnimationConfig().dynamaxScale);
                }
            }
        }));

        DynamaxEventEnd.EVENT.register((battle, pokemon) -> {
            new StringSpeciesFeature("dynamax_form", "none").apply(pokemon.getEffectedPokemon());
            for (ActiveBattlePokemon activeBattlePokemon : battle.getActivePokemon()) {
                if (activeBattlePokemon.getBattlePokemon() != null && activeBattlePokemon.getBattlePokemon().getEffectedPokemon().getOwnerPlayer() == pokemon.getEffectedPokemon().getOwnerPlayer() && activeBattlePokemon.getBattlePokemon() == pokemon) {
                    battle.sendSidedUpdate(activeBattlePokemon.getActor(),
                            new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, true),
                            new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), pokemon, false),
                            false);

                }
            }
            PokemonEntity pokemonEntity = pokemon.getEntity();

            if (pokemonEntity != null) {
                if (server == null && pokemonEntity.getWorld() instanceof ServerWorld serverWorld) {
                    server = serverWorld.getServer();
                }

                startScaling(pokemonEntity, 1.0f);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            DynamaxHandler.server = server;
            updateScales();
        });
    }

    public static void startScaling(PokemonEntity pokemonEntity, float targetScale) {
        UUID entityId = pokemonEntity.getUuid();
        EntityAttributeInstance scaleAttribute = pokemonEntity.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
        if (scaleAttribute != null) {
            float startScale = (float) scaleAttribute.getBaseValue();
            int duration = gf.getAnimationConfig().scalingTicks;
            ScalingData scalingData = new ScalingData(
                    pokemonEntity.getWorld().getRegistryKey().toString(),
                    entityId,
                    startScale,
                    targetScale,
                    duration,
                    0
            );
            scalingEntities.put(entityId, pokemonEntity);
            activeScalingAnimations.put(entityId, scalingData);
        }
    }

    private static void updateScales() {
        Iterator<Map.Entry<UUID, ScalingData>> iterator = activeScalingAnimations.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, ScalingData> entry = iterator.next();
            UUID entityId = entry.getKey();
            ScalingData data = entry.getValue();

            data.currentTick++;

            PokemonEntity entity = scalingEntities.get(entityId);

            if (entity == null || entity.isRemoved()) {
                for (ServerWorld world : server.getWorlds()) {
                    entity = (PokemonEntity) world.getEntity(entityId);
                    if (entity != null) {
                        scalingEntities.put(entityId, entity);
                        break;
                    }
                }
            }

            if (entity != null && !entity.isRemoved()) {
                EntityAttributeInstance scaleAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                if (scaleAttribute != null) {
                    float progress = Math.min(1.0f, (float) data.currentTick / data.durationTicks);
                    float newScale = data.startScale + (data.targetScale - data.startScale) * progress;

                    scaleAttribute.setBaseValue(newScale);
                }

                if (data.currentTick >= data.durationTicks) {
                    iterator.remove();
                    scalingEntities.remove(entityId);
                }
            } else {
                iterator.remove();
                scalingEntities.remove(entityId);
            }
        }
    }

    private static class ScalingData {
        final String worldId;
        final UUID entityId;
        final float startScale;
        final float targetScale;
        final int durationTicks;
        int currentTick;

        public ScalingData(String worldId, UUID entityId, float startScale, float targetScale, int durationTicks, int currentTick) {
            this.worldId = worldId;
            this.entityId = entityId;
            this.startScale = startScale;
            this.targetScale = targetScale;
            this.durationTicks = durationTicks;
            this.currentTick = currentTick;
        }
    }
}