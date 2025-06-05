package me.unariginal.genesisforms.config;

import com.google.gson.*;
import me.unariginal.genesisforms.GenesisForms;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnimationConfig {
    public boolean megaEnabled = true;
    public String megaIdentifier = "cobblemon:mega_evolution";
    public float megaSeconds = 4.9F;

    public boolean dynamaxEnabled = true;
    public float dynamaxScale = 4.0F;
    public int scalingTicks = 60;

    public record TeraAnimation(String type, boolean enabled, String identifier, float seconds) {}
    public List<TeraAnimation> teraAnimations = new ArrayList<>();

    public record ZPowerAnimation(String crystal, boolean enabled, String identifier, float seconds) {}
    public List<ZPowerAnimation> zPowerAnimations = new ArrayList<>();

    public AnimationConfig() {
        try {
            loadConfig();
        } catch (IOException e) {
            GenesisForms.INSTANCE.logError("[Genesis] Failed to load animation config file. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/animations.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        }

        JsonObject megaAnimation = new JsonObject();
        if (root.has("mega_animation")) {
            megaAnimation = root.get("mega_animation").getAsJsonObject();
        }
        if (megaAnimation.has("enabled")) {
            megaEnabled = megaAnimation.get("enabled").getAsBoolean();
        }
        megaAnimation.addProperty("enabled", megaEnabled);
        if (megaAnimation.has("identifier")) {
            megaIdentifier = megaAnimation.get("identifier").getAsString();
        }
        megaAnimation.addProperty("identifier", megaIdentifier);
        if (megaAnimation.has("seconds_before_form_change")) {
            megaSeconds = megaAnimation.get("seconds_before_form_change").getAsFloat();
        }
        megaAnimation.addProperty("seconds_before_form_change", megaSeconds);
        newRoot.add("mega_animation", megaAnimation);

        JsonObject dynamaxAnimation = new JsonObject();
        if (root.has("dynamax_animation")) {
            dynamaxAnimation = root.get("dynamax_animation").getAsJsonObject();
        }
        if (dynamaxAnimation.has("enabled")) {
            dynamaxEnabled = dynamaxAnimation.get("enabled").getAsBoolean();
        }
        dynamaxAnimation.addProperty("enabled", dynamaxEnabled);
        if (dynamaxAnimation.has("scale")) {
            dynamaxScale = dynamaxAnimation.get("scale").getAsFloat();
        }
        dynamaxAnimation.addProperty("scale", dynamaxScale);
        if (dynamaxAnimation.has("scaling_ticks")) {
            scalingTicks = dynamaxAnimation.get("scaling_ticks").getAsInt();
        }
        dynamaxAnimation.addProperty("scaling_ticks", scalingTicks);
        newRoot.add("dynamax_animation", dynamaxAnimation);

        JsonArray teraAnimations = new JsonArray();
        if (root.has("tera_animations")) {
            this.teraAnimations.clear();
            for (JsonElement jsonElement : root.get("tera_animations").getAsJsonArray()) {
                JsonObject teraAnimation = jsonElement.getAsJsonObject();
                String type = teraAnimation.get("type").getAsString();
                boolean enabled = teraAnimation.get("enabled").getAsBoolean();
                String identifier = teraAnimation.get("identifier").getAsString();
                float seconds = teraAnimation.get("seconds_before_form_change").getAsFloat();
                this.teraAnimations.add(new TeraAnimation(type, enabled, identifier, seconds));
            }
        }
        for (TeraAnimation teraAnimation : this.teraAnimations) {
            JsonObject teraAnimationObject = new JsonObject();
            teraAnimationObject.addProperty("type", teraAnimation.type);
            teraAnimationObject.addProperty("enabled", teraAnimation.enabled);
            teraAnimationObject.addProperty("identifier", teraAnimation.identifier);
            teraAnimationObject.addProperty("seconds_before_form_change", teraAnimation.seconds);
            teraAnimations.add(teraAnimationObject);
        }
        newRoot.add("tera_animations", teraAnimations);

        JsonArray zPowerAnimations = new JsonArray();
        if (root.has("z_power_animations")) {
            this.zPowerAnimations.clear();
            for (JsonElement jsonElement : root.get("z_power_animations").getAsJsonArray()) {
                JsonObject zPowerAnimation = jsonElement.getAsJsonObject();
                String crystal = zPowerAnimation.get("crystal").getAsString();
                boolean enabled = zPowerAnimation.get("enabled").getAsBoolean();
                String identifier = zPowerAnimation.get("identifier").getAsString();
                float seconds = zPowerAnimation.get("seconds_before_form_change").getAsFloat();
                this.zPowerAnimations.add(new ZPowerAnimation(crystal, enabled, identifier, seconds));
            }
        }
        for (ZPowerAnimation zPowerAnimation : this.zPowerAnimations) {
            JsonObject zPowerAnimationObject = new JsonObject();
            zPowerAnimationObject.addProperty("crystal", zPowerAnimation.crystal);
            zPowerAnimationObject.addProperty("enabled", zPowerAnimation.enabled);
            zPowerAnimationObject.addProperty("identifier", zPowerAnimation.identifier);
            zPowerAnimationObject.addProperty("seconds_before_form_change", zPowerAnimation.seconds);
            zPowerAnimations.add(zPowerAnimationObject);
        }
        newRoot.add("z_power_animations", zPowerAnimations);

        configFile.delete();
        configFile.createNewFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter(configFile);
        gson.toJson(newRoot, writer);
        writer.close();
    }
}
