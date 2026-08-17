package me.unariginal.genesisforms.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.items.MiscItemsConfig;
import me.unariginal.genesisforms.config.items.accessories.AccessoriesConfig;
import me.unariginal.genesisforms.config.items.bagitems.MaxItemsConfig;
import me.unariginal.genesisforms.config.items.bagitems.TeraShardsConfig;
import me.unariginal.genesisforms.config.items.helditems.HeldBattleItemsConfig;
import me.unariginal.genesisforms.config.items.helditems.HeldFormItemsConfig;
import me.unariginal.genesisforms.config.items.helditems.ZCrystalsConfig;
import me.unariginal.genesisforms.config.items.keyitems.FusionItemsConfig;
import me.unariginal.genesisforms.config.items.keyitems.KeyFormItemsConfig;
import me.unariginal.genesisforms.config.items.keyitems.PossessionItemsConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.unariginal.genesisforms.utils.GsonUtils.gson;
import static me.unariginal.genesisforms.GenesisForms.LOGGER;

public class ConfigManager {
    public static File configDir;

    public static Config CONFIG;
    public static MessagesConfig MESSAGES;
    public static EventsConfig EVENTS;
    public static MiscItemsConfig MISC_ITEMS;
    public static ZCrystalsConfig Z_CRYSTALS;

    public static LinkedHashMap<String, BattleFormChangeConfig> BATTLE_FORMS;
    public static LinkedHashMap<String, MegaEvolutionConfig> MEGA_EVOLUTIONS;
    public static LinkedHashMap<String, FusionItemsConfig> FUSION_ITEMS;
    public static LinkedHashMap<String, KeyFormItemsConfig> KEY_FORM_ITEMS;
    public static LinkedHashMap<String, PossessionItemsConfig> POSSESSION_ITEMS;
    public static LinkedHashMap<String, HeldFormItemsConfig> HELD_FORM_ITEMS;
    public static LinkedHashMap<String, HeldBattleItemsConfig> HELD_BATTLE_ITEMS;
    public static LinkedHashMap<String, TeraShardsConfig> TERA_SHARDS;
    public static LinkedHashMap<String, MaxItemsConfig> MAX_ITEMS;
    public static LinkedHashMap<String, AccessoriesConfig> DYNAMAX_ACCESSORIES;
    public static LinkedHashMap<String, AccessoriesConfig> MEGA_ACCESSORIES;
    public static LinkedHashMap<String, AccessoriesConfig> TERA_ACCESSORIES;
    public static LinkedHashMap<String, AccessoriesConfig> Z_ACCESSORIES;

    public static List<String> itemlessMegas = new ArrayList<>();

    public static void load() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();

        generateDefaultFiles();
        fillMissingWithDefaults("config.json", null, false);
        fillMissingWithDefaults("messages.json", null, false);
        fillMissingWithDefaults("events.json", null, false);
        fillMissingWithDefaults("battle_forms.json", null, true);
        fillMissingWithDefaults("mega_evolutions.json", null, true);
        fillMissingWithDefaults("items/misc_items.json", null, false);
        fillMissingWithDefaults("items/key_items/fusion_items.json", null, true);
        fillMissingWithDefaults("items/key_items/key_form_items.json", null, true);
        fillMissingWithDefaults("items/key_items/possession_items.json", null, true);
        fillMissingWithDefaults("items/held_items/held_battle_items.json", null, true);
        fillMissingWithDefaults("items/held_items/held_form_items.json", null, true);
        fillMissingWithDefaults("items/held_items/z_crystals.json", null, false, Set.of("typed", "species"));
        fillMissingWithDefaults("items/bag_items/max_items.json", null, true);
        fillMissingWithDefaults("items/bag_items/tera_shards.json", null, true);
        fillMissingWithDefaults("items/accessories/dynamax_accessories.json", null, true);
        fillMissingWithDefaults("items/accessories/mega_accessories.json", null, true);
        fillMissingWithDefaults("items/accessories/tera_accessories.json", null, true);
        fillMissingWithDefaults("items/accessories/z_accessories.json", null, true);

        CONFIG = loadFile("config.json", Config.class);
        MESSAGES = loadFile("messages.json", MessagesConfig.class);
        EVENTS = loadFile("events.json", EventsConfig.class);

        BATTLE_FORMS = loadMapFile("battle_forms.json", BattleFormChangeConfig.class);
        MEGA_EVOLUTIONS = loadMapFile("mega_evolutions.json", MegaEvolutionConfig.class);
        for (Map.Entry<String, MegaEvolutionConfig> megaEvolutionConfig : MEGA_EVOLUTIONS.entrySet()) {
            if (!megaEvolutionConfig.getValue().hasItem) itemlessMegas.add(megaEvolutionConfig.getKey());
        }

        MISC_ITEMS = loadFile("items/misc_items.json", MiscItemsConfig.class);
        Z_CRYSTALS = loadFile("items/held_items/z_crystals.json", ZCrystalsConfig.class);

        FUSION_ITEMS = loadMapFile("items/key_items/fusion_items.json", FusionItemsConfig.class);
        KEY_FORM_ITEMS = loadMapFile("items/key_items/key_form_items.json", KeyFormItemsConfig.class);
        POSSESSION_ITEMS = loadMapFile("items/key_items/possession_items.json", PossessionItemsConfig.class);

        HELD_BATTLE_ITEMS = loadMapFile("items/held_items/held_battle_items.json", HeldBattleItemsConfig.class);
        HELD_FORM_ITEMS = loadMapFile("items/held_items/held_form_items.json", HeldFormItemsConfig.class);
        MAX_ITEMS = loadMapFile("items/bag_items/max_items.json", MaxItemsConfig.class);
        TERA_SHARDS = loadMapFile("items/bag_items/tera_shards.json", TeraShardsConfig.class);
        DYNAMAX_ACCESSORIES = loadMapFile("items/accessories/dynamax_accessories.json", AccessoriesConfig.class);
        MEGA_ACCESSORIES = loadMapFile("items/accessories/mega_accessories.json", AccessoriesConfig.class);
        TERA_ACCESSORIES = loadMapFile("items/accessories/tera_accessories.json", AccessoriesConfig.class);
        Z_ACCESSORIES = loadMapFile("items/accessories/z_accessories.json", AccessoriesConfig.class);
    }

    public static void generateDefaultFiles() {
        generateDefaultFile("config.json");
        generateDefaultFile("messages.json");
        generateDefaultFile("events.json");
        generateDefaultFile("battle_forms.json");
        generateDefaultFile("mega_evolutions.json");
        generateDefaultFile("items/misc_items.json");
        generateDefaultFile("items/key_items/fusion_items.json");
        generateDefaultFile("items/key_items/key_form_items.json");
        generateDefaultFile("items/key_items/possession_items.json");
        generateDefaultFile("items/held_items/held_battle_items.json");
        generateDefaultFile("items/held_items/held_form_items.json");
        generateDefaultFile("items/held_items/z_crystals.json");
        generateDefaultFile("items/bag_items/max_items.json");
        generateDefaultFile("items/bag_items/tera_shards.json");
        generateDefaultFile("items/accessories/dynamax_accessories.json");
        generateDefaultFile("items/accessories/mega_accessories.json");
        generateDefaultFile("items/accessories/tera_accessories.json");
        generateDefaultFile("items/accessories/z_accessories.json");
    }

    public static <T> T loadFile(String fileName, Class<T> clazz) {
        File file = new File(configDir, fileName);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                String jsonString = JsonParser.parseReader(reader).toString();
                return gson.fromJson(jsonString, clazz);
            } catch (IOException | JsonParseException | IllegalStateException e) {
                LOGGER.error("[GenesisForms] Failed to parse config file {}, falling back to defaults.", fileName, e);
                return gson.fromJson(getDefaultJsonString(fileName), clazz);
            }
        }
        LOGGER.error("[GenesisForms] Error loading config file {}. File does not exist!", fileName);
        return gson.fromJson(getDefaultJsonString(fileName), clazz);
    }

    public static <T> LinkedHashMap<String, T> loadMapFile(String fileName, Class<T> clazz) {
        File file = new File(configDir, fileName);
        Type mapType = TypeToken.getParameterized(LinkedHashMap.class, String.class, clazz).getType();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                return gson.fromJson(reader, mapType);
            } catch (IOException | JsonParseException | IllegalStateException e) {
                LOGGER.error("[GenesisForms] Failed to parse config file {}, falling back to defaults.", fileName, e);
                return gson.fromJson(getDefaultJsonString(fileName), mapType);
            }
        }
        LOGGER.error("[GenesisForms] Error loading config file {}. File does not exist!", fileName);
        return gson.fromJson(getDefaultJsonString(fileName), mapType);
    }

    public static void generateDefaultFile(String fileName) {
        File file = new File(configDir, fileName);
        if (file.exists()) return;
        try {
            Files.createDirectories(file.getParentFile().toPath());
            Files.createFile(file.toPath());
            writeFile(file, getDefaultJsonString(fileName));
        } catch (IOException e) {
            LOGGER.error("[GenesisForms] Failed to create directories for file {}", file.getName(), e);
        }
    }

    public static void fillMissingWithDefaults(String fileName, String defaultFileName, boolean isMapFile) {
        fillMissingWithDefaults(fileName, defaultFileName, isMapFile, Set.of());
    }

    public static void fillMissingWithDefaults(String fileName, String defaultFileName, boolean isMapFile, Set<String> nestedMapKeys) {
        File file = new File(configDir, fileName);
        if (defaultFileName == null) defaultFileName = fileName;

        JsonObject defaultJson;
        try (InputStream in = GenesisForms.class.getResourceAsStream("/genesis_configs/" + defaultFileName)) {
            if (in == null) {
                LOGGER.error("[GenesisForms] Missing bundled default resource for {}, skipping default-fill.", defaultFileName);
                return;
            }
            defaultJson = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
        } catch (IOException | JsonParseException | IllegalStateException e) {
            LOGGER.error("[GenesisForms] Bundled default resource for {} is invalid, skipping default-fill.", defaultFileName, e);
            return;
        }

        JsonObject targetJson;
        try (FileReader reader = new FileReader(file)) {
            targetJson = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException | JsonParseException | IllegalStateException e) {
            LOGGER.error("[GenesisForms] {} contains invalid JSON, resetting it to defaults.", fileName, e);
            writeFile(file, gson.toJson(defaultJson));
            return;
        }

        mergeJsonObjects(targetJson, defaultJson, isMapFile, nestedMapKeys);
        writeFile(file, gson.toJson(targetJson));
    }

    private static void mergeJsonObjects(JsonObject target, JsonObject defaults, boolean isMapFile, Set<String> nestedMapKeys) {
        if (!isMapFile) {
            for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) {
                String key = entry.getKey();
                JsonElement defaultValue = entry.getValue();

                // Keys explicitly marked as nested maps (e.g. z_crystals.json's "typed"/"species")
                // are merged per-entry rather than as a single settings object.
                if (nestedMapKeys.contains(key) && target.has(key) && target.get(key).isJsonObject() && defaultValue.isJsonObject()) {
                    mergeMaps(target.getAsJsonObject(key), defaultValue.getAsJsonObject());
                    continue;
                }

                if (!target.has(key)) {
                    target.add(key, defaultValue.deepCopy());
                } else {
                    JsonElement targetValue = target.get(key);
                    if (!targetValue.isJsonArray() && targetValue.isJsonObject() && defaultValue.isJsonObject()) {
                        mergeJsonObjects(targetValue.getAsJsonObject(), defaultValue.getAsJsonObject(), false, nestedMapKeys);
                    }
                }
            }
        } else {
            mergeMaps(target, defaults);
        }
    }

    /**
     * Backfills missing fields on each of the user's existing map entries (e.g. one mega evolution,
     * one fusion item, ...) using only the fields that every entry in the shipped defaults agrees on.
     * Fields that vary between default entries are item-specific (species, feature values, ids, lore)
     * and are deliberately never used to fill in a different entry.
     */
    private static void mergeMaps(JsonObject target, JsonObject defaults) {
        JsonObject consensusTemplate = computeConsensusTemplate(defaults);
        for (Map.Entry<String, JsonElement> mapEntry : target.entrySet()) {
            if (mapEntry.getValue().isJsonObject()) {
                mergeJsonObjects(mapEntry.getValue().getAsJsonObject(), consensusTemplate, false, Set.of());
            }
        }
    }

    private static JsonObject computeConsensusTemplate(JsonObject defaultsMap) {
        JsonObject consensus = null;
        for (Map.Entry<String, JsonElement> mapEntry : defaultsMap.entrySet()) {
            if (!mapEntry.getValue().isJsonObject()) continue;
            JsonObject entryObject = mapEntry.getValue().getAsJsonObject();
            if (consensus == null) {
                consensus = entryObject.deepCopy();
            } else {
                intersectInPlace(consensus, entryObject);
            }
        }
        return consensus != null ? consensus : new JsonObject();
    }

    /** Drops any key from {@code consensus} that {@code other} doesn't also have with an equal value. */
    private static void intersectInPlace(JsonObject consensus, JsonObject other) {
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : consensus.entrySet()) {
            String key = entry.getKey();
            JsonElement consensusValue = entry.getValue();
            JsonElement otherValue = other.get(key);

            if (otherValue == null) {
                keysToRemove.add(key);
            } else if (consensusValue.isJsonObject() && otherValue.isJsonObject()) {
                intersectInPlace(consensusValue.getAsJsonObject(), otherValue.getAsJsonObject());
            } else if (!consensusValue.equals(otherValue)) {
                keysToRemove.add(key);
            }
        }
        keysToRemove.forEach(consensus::remove);
    }

    public static String getDefaultJsonString(String fileName) {
        InputStream in = GenesisForms.class.getResourceAsStream("/genesis_configs/" + fileName);
        if (in != null) {
            return gson.toJson(JsonParser.parseReader(new InputStreamReader(in)));
        }
        return "{}";
    }

    public static void writeFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            LOGGER.error("[GenesisForms] Failed to write to file {}", file.getName(), e);
        }
    }
}
