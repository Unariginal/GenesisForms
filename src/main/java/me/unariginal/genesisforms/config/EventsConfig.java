package me.unariginal.genesisforms.config;

import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import kotlin.Unit;
import me.unariginal.genesisforms.handlers.GlowHandler;
import me.unariginal.genesisforms.handlers.ScaleHandler;
import me.unariginal.genesisforms.utils.ConfigUtils;
import me.unariginal.genesisforms.utils.ParticleUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class EventsConfig {
    public static GimmickEvents gimmickEvents;

    public static class GimmickEvents {
        @SerializedName(value = "mega_evolution", alternate = "megaEvolution")
        public EventData megaEvolution;
        public EventData terastallization;
        @SerializedName(value = "z_power", alternate = "zPower")
        public EventData zPower;
        public EventData dynamax;

        public GimmickEvents(EventData megaEvolution, EventData terastallization, EventData zPower, EventData dynamax) {
            this.megaEvolution = megaEvolution;
            this.terastallization = terastallization;
            this.zPower = zPower;
            this.dynamax = dynamax;
        }
    }

    public static class EventData {
        public Map<String, AnimationData> animations;
        public Map<String, String> glow;
        public Map<String, FeatureData> features;
        public Map<String, ScaleData> scale;

        public EventData(Map<String, AnimationData> animations, Map<String, String> glow, Map<String, FeatureData> features, Map<String, ScaleData> scale) {
            this.animations = animations;
            this.glow = glow;
            this.features = features;
            this.scale = scale;
        }

        public void runEvent(String id, Pokemon pokemon, PokemonEntity pokemonEntity) {
            EventsConfig.AnimationData animationData = getAnimation(id);
            EventsConfig.FeatureData featureData = getFeature(id);
            EventsConfig.ScaleData scaleData = getScale(id);
            float delay = 0;

            if (pokemonEntity != null) {
                if (animationData != null) {
                    ParticleUtils.spawnParticle(animationData.identifier, pokemonEntity.getPos().add(0, pokemonEntity.getBoundingBox().getLengthY() / 2, 0), pokemonEntity.getWorld().getRegistryKey());
                    delay = animationData.formDelaySeconds;
                }
            }

            if (featureData != null) {
                if (pokemonEntity != null) {
                    pokemonEntity.after(delay, () -> {
                        if (featureData.featureValue.equalsIgnoreCase("true") || featureData.featureValue.equalsIgnoreCase("false")) {
                            new FlagSpeciesFeature(featureData.featureName, Boolean.getBoolean(featureData.featureValue)).apply(pokemon);
                        } else {
                            new StringSpeciesFeature(featureData.featureName, featureData.featureValue).apply(pokemon);
                        }
                        return Unit.INSTANCE;
                    });
                } else {
                    if (featureData.featureValue.equalsIgnoreCase("true") || featureData.featureValue.equalsIgnoreCase("false")) {
                        new FlagSpeciesFeature(featureData.featureName, Boolean.getBoolean(featureData.featureValue)).apply(pokemon);
                    } else {
                        new StringSpeciesFeature(featureData.featureName, featureData.featureValue).apply(pokemon);
                    }
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

            String glowColor = null;
            if (glow.containsKey(id)) {
                glowColor = glow.get(id);
            } else if (glow.containsKey("global")) {
                glowColor = glow.get("global");
            }

            if (glowColor != null) {
                pokemon.getPersistentData().putString("glow_id", id);
                pokemon.getPersistentData().putString("glow_color", glowColor);
                if (pokemonEntity != null) {
                    GlowHandler.applyGlowing(id, glowColor, pokemon, pokemonEntity);
                }
            }
        }

        public void revertEvent(String id, Pokemon pokemon, PokemonEntity pokemonEntity) {
            EventsConfig.FeatureData featureData = getFeature(id);
            EventsConfig.ScaleData scaleData = getScale(id);

            if (featureData != null) {
                pokemon.getFeatures().removeIf(speciesFeature -> speciesFeature.getName().equalsIgnoreCase(featureData.featureName));
                if (!(featureData.defaultValue == null || featureData.defaultValue.equalsIgnoreCase("null"))) {
                    if (featureData.defaultValue.equalsIgnoreCase("true") || featureData.defaultValue.equalsIgnoreCase("false")) {
                        new FlagSpeciesFeature(featureData.featureName, Boolean.getBoolean(featureData.defaultValue)).apply(pokemon);
                    } else {
                        new StringSpeciesFeature(featureData.featureName, featureData.defaultValue).apply(pokemon);
                    }
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

            GlowHandler.removeGlowing(id, pokemon, pokemonEntity);
        }

        public AnimationData getAnimation(String animationName) {
            if (animations.containsKey(animationName)) {
                return animations.get(animationName);
            } else {
                return animations.get("global");
            }
        }

        public String getGlow(String glowName) {
            if (glow.containsKey(glowName)) {
                return glow.get(glowName);
            } else {
                return glow.get("global");
            }
        }

        public FeatureData getFeature(String featureName) {
            if (features.containsKey(featureName)) {
                return features.get(featureName);
            } else {
                return features.get("global");
            }
        }

        public ScaleData getScale(String scaleName) {
            if (scale.containsKey(scaleName)) {
                return scale.get(scaleName);
            } else {
                return scale.get("global");
            }
        }
    }

    public static class AnimationData {
        public String identifier;
        @SerializedName(value = "form_delay_seconds", alternate = "formDelaySeconds")
        public float formDelaySeconds;

        public AnimationData(String identifier, float formDelaySeconds) {
            this.identifier = identifier;
            this.formDelaySeconds = formDelaySeconds;
        }
    }

    public static class FeatureData {
        @SerializedName(value = "feature_name", alternate = "featureName")
        public String featureName;
        @SerializedName(value = "feature_value", alternate = "featureValue")
        public String featureValue;
        @SerializedName(value = "default_value", alternate = "defaultValue")
        public String defaultValue;

        public FeatureData(String featureName, String featureValue, String defaultValue) {
            this.featureName = featureName;
            this.featureValue = featureValue;
            this.defaultValue = defaultValue;
        }
    }

    public static class ScaleData {
        public float scale;
        @SerializedName(value = "scaling_ticks", alternate = "scalingTicks")
        public long scalingTicks;

        public ScaleData(float scale, long scalingTicks) {
            this.scale = scale;
            this.scalingTicks = scalingTicks;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File eventsConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/events.json").toFile();
        if (!eventsConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(eventsConfigFile, "/genesis_configs/events.json");
        }
        if (eventsConfigFile.exists()) json = JsonParser.parseReader(new FileReader(eventsConfigFile)).toString();

        Type eventsDataType = new TypeToken<GimmickEvents>() {}.getType();
        gimmickEvents = gson.fromJson(json, eventsDataType);
    }
}
