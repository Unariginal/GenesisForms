package me.unariginal.genesisforms.config;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import me.unariginal.genesisforms.data.event.ParticleEvent;
import me.unariginal.genesisforms.utils.GlowUtils;
import me.unariginal.genesisforms.handlers.ScaleHandler;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static me.unariginal.genesisforms.utils.PokemonUtils.applyFeature;

public class EventsConfig {
    @Nullable
    public EventData megaEvolution;
    @Nullable
    public EventData terastallization;
    @Nullable
    public EventData zPower;
    @Nullable
    public EventData dynamax;
    @Nullable
    public FormChanges formChanges;

    public static class FormChanges {
        @Nullable
        public EventData keyItems;
        @Nullable
        public EventData heldItems;
        @Nullable
        public EventData fusions;
        @Nullable
        public EventData possessions;
        @Nullable
        public EventData battleForms;
    }

    public static class EventData {
        @Nullable
        public Map<String, ParticleEvent> animations;
        @Nullable
        public Map<String, String> glow;
        @Nullable
        public Map<String, FeatureData> features;
        @Nullable
        public Map<String, ScaleData> scale;

        public void runEvent(@NotNull String id, @NotNull Pokemon pokemon, @Nullable PokemonEntity pokemonEntity) {
            ParticleEvent animationData = getAnimation(id);
            EventsConfig.FeatureData featureData = getFeature(id);
            EventsConfig.ScaleData scaleData = getScale(id);
            float delay = 0;

            if (pokemonEntity != null) {
                if (animationData != null) {
                    animationData.spawnParticle(pokemonEntity);
                    delay = animationData.formChangeDelaySeconds;
                }
            }

            if (featureData != null) {
                if (pokemonEntity != null) {
                    pokemonEntity.after(delay, () -> {
                        applyFeature(featureData.featureName, featureData.featureValue, pokemon);
                        return Unit.INSTANCE;
                    });
                } else {
                    applyFeature(featureData.featureName, featureData.featureValue, pokemon);
                }
            }

            if (pokemonEntity != null) {
                if (scaleData != null) {
                    EntityAttributeInstance scaleAttribute = pokemonEntity.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                    if (scaleAttribute != null) {
                        ScaleHandler.scalingAnimations.put(pokemonEntity, new ScaleHandler.ScalingData(
                                (float) scaleAttribute.getBaseValue(),
                                scaleData.scale,
                                scaleData.scalingTicks
                        ));
                    }
                }
            }

            String glowColor = getGlow(id);

            if (glowColor != null) {
                pokemon.getPersistentData().putString("glow_id", id);
                pokemon.getPersistentData().putString("glow_color", glowColor);
                GlowUtils.applyGlowing(id, glowColor, pokemon, pokemonEntity);
            }
        }

        public void revertEvent(@NotNull String id, @NotNull Pokemon pokemon, @Nullable PokemonEntity pokemonEntity) {
            EventsConfig.FeatureData featureData = getFeature(id);
            EventsConfig.ScaleData scaleData = getScale(id);

            if (featureData != null) {
                pokemon.getFeatures().removeIf(speciesFeature -> speciesFeature.getName().equalsIgnoreCase(featureData.featureName));
                if (!(featureData.defaultValue == null || featureData.defaultValue.equalsIgnoreCase("null"))) {
                    applyFeature(featureData.featureName, featureData.defaultValue, pokemon);
                }
            }

            if (pokemonEntity != null && scaleData != null) {
                EntityAttributeInstance scaleAttribute = pokemonEntity.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                if (scaleAttribute != null) {
                    ScaleHandler.scalingAnimations.put(pokemonEntity, new ScaleHandler.ScalingData(
                            (float) scaleAttribute.getBaseValue(),
                            1.0F,
                            scaleData.scalingTicks
                    ));
                }
            }

            if (pokemon.getPersistentData().contains("glow_id")) {
                pokemon.getPersistentData().remove("glow_id");
            }

            if (pokemon.getPersistentData().contains("glow_color")) {
                pokemon.getPersistentData().remove("glow_color");
            }

            GlowUtils.removeGlowing(id, pokemon, pokemonEntity);
        }

        @Nullable
        public ParticleEvent getAnimation(String animationName) {
            if (animations == null) return null;
            if (animations.containsKey(animationName)) {
                return animations.get(animationName);
            } else {
                return animations.get("global");
            }
        }

        @Nullable
        public String getGlow(String glowName) {
            if (glow == null) return null;
            if (glow.containsKey(glowName)) {
                return glow.get(glowName);
            } else {
                return glow.get("global");
            }
        }

        @Nullable
        public FeatureData getFeature(String featureName) {
            if (features == null) return null;
            if (features.containsKey(featureName)) {
                return features.get(featureName);
            } else {
                return features.get("global");
            }
        }

        @Nullable
        public ScaleData getScale(String scaleName) {
            if (scale == null) return null;
            if (scale.containsKey(scaleName)) {
                return scale.get(scaleName);
            } else {
                return scale.get("global");
            }
        }
    }

    public static class FeatureData {
        public String featureName;
        public String featureValue;
        public String defaultValue;
    }

    public static class ScaleData {
        public float scale;
        public long scalingTicks;
    }
}
