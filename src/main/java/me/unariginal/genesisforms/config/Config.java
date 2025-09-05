package me.unariginal.genesisforms.config;

import com.google.gson.*;
import me.unariginal.genesisforms.GenesisForms;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Adjust to new config layout
public class Config {
    public boolean debug = false;

    public List<String> disabledItems = new ArrayList<>();
    public boolean enableMegaEvolution = true;
    public boolean enableTera = true;
    public boolean enableZCrystals = true;
    public boolean enableDynamax = true;
    public boolean enableGigantamax = true;
    public boolean enableFusions = true;
    public boolean allowMegaOutsideBattles = true;
    public boolean fixOgerponTeraType = true;
    public boolean fixTerapagosTeraType = true;
    public int teraShardsRequired = 50;
    public boolean consumeTeraShards = true;

    public boolean useHotbarInventory = true;
    public boolean useMainInventory = true;
    public boolean useMainHandInventory = true;
    public boolean useOffHandInventory = true;
    public boolean useArmorInventory = false;
    public List<Integer> specificSlots = new ArrayList<>();


    public Config() {
        try {
            loadConfig();
        } catch (IOException e) {
            GenesisForms.INSTANCE.logError("[Genesis] Failed to load config file. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();
        if (!rootFolder.exists())
            rootFolder.mkdirs();

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/config.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (configFile.exists())
            root = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();

        if (root.has("debug"))
            debug = root.get("debug").getAsBoolean();
        newRoot.addProperty("debug", debug);

        JsonObject generalSettings = new JsonObject();
        if (root.has("general_settings"))
            generalSettings = root.get("general_settings").getAsJsonObject();

        JsonObject keyItemSlots = new JsonObject();

        if (root.has("key_item_slots"))
            keyItemSlots = root.get("key_item_slots").getAsJsonObject();

        if (keyItemSlots.has("hotbar"))
            useHotbarInventory = keyItemSlots.get("hotbar").getAsBoolean();
        keyItemSlots.addProperty("hotbar", useHotbarInventory);

        if (keyItemSlots.has("main"))
            useMainInventory = keyItemSlots.get("main").getAsBoolean();
        keyItemSlots.addProperty("main", useMainInventory);

        if (keyItemSlots.has("mainhand"))
            useMainHandInventory = keyItemSlots.get("mainhand").getAsBoolean();
        keyItemSlots.addProperty("mainhand", useMainHandInventory);

        if (keyItemSlots.has("offhand"))
            useOffHandInventory = keyItemSlots.get("offhand").getAsBoolean();
        keyItemSlots.addProperty("offhand", useOffHandInventory);

        if (keyItemSlots.has("armor"))
            useArmorInventory = keyItemSlots.get("armor").getAsBoolean();
        keyItemSlots.addProperty("armor", useArmorInventory);

        if (keyItemSlots.has("specific")) {
            JsonArray specificSlots = keyItemSlots.get("specific").getAsJsonArray();
            for (JsonElement element : specificSlots) {
                this.specificSlots.add(element.getAsInt());
            }
        }
        JsonArray specificSlots = new JsonArray();
        for (int slot : this.specificSlots) {
            specificSlots.add(slot);
        }
        keyItemSlots.add("specific", specificSlots);

        generalSettings.add("key_item_slots", keyItemSlots);

        JsonArray disabledItems = new JsonArray();
        if (generalSettings.has("disabled_items")) {
            this.disabledItems.clear();
            generalSettings.get("disabled_items").getAsJsonArray().forEach(element -> disabledItems.add(element.getAsString()));
        }
        for (String item : this.disabledItems) {
            disabledItems.add(item);
        }
        generalSettings.add("disabled_items", disabledItems);

        JsonObject customBattleForms = new JsonObject();
        if (generalSettings.has("custom_battle_forms"))
            customBattleForms = generalSettings.get("custom_battle_forms").getAsJsonObject();
        for (String battleFormKey : customBattleForms.keySet()) {
            JsonObject battleFormObject = customBattleForms.get(battleFormKey).getAsJsonObject();
            if (!(battleFormObject.has("species") && battleFormObject.has("default_form") && battleFormObject.has("forms"))) continue;
            String species = battleFormObject.get("species").getAsString();
            JsonObject defaultFormObject = battleFormObject.get("default_form").getAsJsonObject();
            JsonObject formsObject = defaultFormObject.get("forms").getAsJsonObject();

            if (!(defaultFormObject.has("feature_name") && defaultFormObject.has("feature_value"))) continue;
            String defaultFeatureName = defaultFormObject.get("feature_name").getAsString();
            String defaultFeatureValue = defaultFormObject.get("feature_value").getAsString();

            Map<String, BattleFormChangeConfig.BattleForm> battleFormMap = new HashMap<>();
            for (String formName : formsObject.keySet()) {
                JsonObject formObject = formsObject.get(formName).getAsJsonObject();
                if (!(formObject.has("feature_name") && formObject.has("feature_value"))) continue;
                String featureName = formObject.get("feature_name").getAsString();
                String featureValue = formObject.get("feature_value").getAsString();
                battleFormMap.put(formName, new BattleFormChangeConfig.BattleForm(featureName, featureValue));
            }

            BattleFormChangeConfig.battleForms.put(battleFormKey, new BattleFormChangeConfig.BattleFormInformation(species, new BattleFormChangeConfig.BattleForm(defaultFeatureName, defaultFeatureValue), battleFormMap));
        }
        generalSettings.add("custom_battle_forms", customBattleForms);

        newRoot.add("general_settings", generalSettings);

        JsonObject megaSettings = new JsonObject();
        if (root.has("mega_settings"))
            megaSettings = root.get("mega_settings").getAsJsonObject();

        if (megaSettings.has("enable_mega_evolution"))
            enableMegaEvolution = megaSettings.get("enable_mega_evolution").getAsBoolean();
        megaSettings.addProperty("enable_mega_evolution", enableMegaEvolution);

        if (megaSettings.has("allow_mega_outside_battles"))
            allowMegaOutsideBattles = megaSettings.get("allow_mega_outside_battles").getAsBoolean();
        megaSettings.addProperty("allow_mega_outside_battles", allowMegaOutsideBattles);

        newRoot.add("mega_settings", megaSettings);

        JsonObject zPowerSettings = new JsonObject();
        if (root.has("z_power_settings"))
            zPowerSettings = root.get("z_power_settings").getAsJsonObject();

        if (zPowerSettings.has("enable_z_crystals"))
            enableZCrystals = zPowerSettings.get("enable_z_crystals").getAsBoolean();
        zPowerSettings.addProperty("enable_z_crystals", enableZCrystals);

        newRoot.add("z_power_settings", zPowerSettings);

        JsonObject teraSettings = new JsonObject();
        if (root.has("tera_settings"))
            teraSettings = root.get("tera_settings").getAsJsonObject();

        if (teraSettings.has("enable_tera"))
            enableTera = teraSettings.get("enable_tera").getAsBoolean();
        teraSettings.addProperty("enable_tera", enableTera);

        if (teraSettings.has("tera_shards_required"))
            teraShardsRequired = teraSettings.get("tera_shards_required").getAsInt();
        if (teraShardsRequired > 64) teraShardsRequired = 64;
        if (teraShardsRequired < 1) teraShardsRequired = 1;
        teraSettings.addProperty("tera_shards_required", teraShardsRequired);

        if (teraSettings.has("fix_ogerpon_tera_type"))
            fixOgerponTeraType = teraSettings.get("fix_ogerpon_tera_type").getAsBoolean();
        teraSettings.addProperty("fix_ogerpon_tera_type", fixOgerponTeraType);

        if (teraSettings.has("fix_terapagos_tera_type"))
            fixTerapagosTeraType = teraSettings.get("fix_terapagos_tera_type").getAsBoolean();
        teraSettings.addProperty("fix_terapagos_tera_type", fixTerapagosTeraType);

        newRoot.add("tera_settings", teraSettings);

        JsonObject fusionSettings = new JsonObject();
        if (root.has("fusion_settings"))
            fusionSettings = root.get("fusion_settings").getAsJsonObject();

        if (fusionSettings.has("enable_fusions"))
            enableFusions = fusionSettings.get("enable_fusions").getAsBoolean();

        fusionSettings.addProperty("enable_fusions", enableFusions);

        newRoot.add("fusion_settings", fusionSettings);

        JsonObject dynamaxSettings = new JsonObject();
        if (root.has("dynamax_settings"))
            dynamaxSettings = root.get("dynamax_settings").getAsJsonObject();

        if (dynamaxSettings.has("enable_dynamax"))
            enableDynamax = dynamaxSettings.get("enable_dynamax").getAsBoolean();

        dynamaxSettings.addProperty("enable_dynamax", enableDynamax);

        if (dynamaxSettings.has("enable_gigantamax"))
            enableGigantamax = dynamaxSettings.get("enable_gigantamax").getAsBoolean();

        dynamaxSettings.addProperty("enable_gigantamax", enableGigantamax);

        newRoot.add("dynamax_settings", dynamaxSettings);

        configFile.delete();
        configFile.createNewFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter(configFile);
        gson.toJson(newRoot, writer);
        writer.close();
    }
}