package me.unariginal.genesisforms.config;

import com.google.gson.*;
import me.unariginal.genesisforms.GenesisForms;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// TODO: Adjust to new config layout
public class Config {
    public boolean debug = false;

    public List<String> disabledItems = new ArrayList<>();
    public boolean enableMegaEvolution = true;
    public boolean useTradeableProperty = false;
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
    public boolean requireOrbRecharge = true;

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

        File oldAnimations = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/animations.json").toFile();
        if (oldAnimations.exists()) {
            oldAnimations.renameTo(FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/old_animations.json").toFile());
        }

        File oldItemSettings = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/item_settings.json").toFile();
        if (oldItemSettings.exists()) {
            oldItemSettings.renameTo(FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/old_item_settings.json").toFile());
        }

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/config.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (configFile.exists())
            root = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();

        if (root.has("debug"))
            debug = root.get("debug").getAsBoolean();
        newRoot.addProperty("debug", debug);

        JsonObject generalSettings = new JsonObject();
        JsonObject newGeneralSettings = new JsonObject();
        if (root.has("general_settings"))
            generalSettings = root.get("general_settings").getAsJsonObject();

        JsonObject keyItemSlots = new JsonObject();
        JsonObject newKeyItemSlots = new JsonObject();
        if (root.has("key_item_slots"))
            keyItemSlots = root.get("key_item_slots").getAsJsonObject();

        if (keyItemSlots.has("hotbar"))
            useHotbarInventory = keyItemSlots.get("hotbar").getAsBoolean();
        newKeyItemSlots.addProperty("hotbar", useHotbarInventory);

        if (keyItemSlots.has("main"))
            useMainInventory = keyItemSlots.get("main").getAsBoolean();
        newKeyItemSlots.addProperty("main", useMainInventory);

        if (keyItemSlots.has("mainhand"))
            useMainHandInventory = keyItemSlots.get("mainhand").getAsBoolean();
        newKeyItemSlots.addProperty("mainhand", useMainHandInventory);

        if (keyItemSlots.has("offhand"))
            useOffHandInventory = keyItemSlots.get("offhand").getAsBoolean();
        newKeyItemSlots.addProperty("offhand", useOffHandInventory);

        if (keyItemSlots.has("armor"))
            useArmorInventory = keyItemSlots.get("armor").getAsBoolean();
        newKeyItemSlots.addProperty("armor", useArmorInventory);

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
        newKeyItemSlots.add("specific", specificSlots);

        newGeneralSettings.add("key_item_slots", newKeyItemSlots);

        JsonArray disabledItems = new JsonArray();
        if (generalSettings.has("disabled_items")) {
            this.disabledItems.clear();
            generalSettings.get("disabled_items").getAsJsonArray().forEach(element -> disabledItems.add(element.getAsString()));
        }
        for (String item : this.disabledItems) {
            disabledItems.add(item);
        }
        newGeneralSettings.add("disabled_items", disabledItems);

        newRoot.add("general_settings", newGeneralSettings);

        JsonObject megaSettings = new JsonObject();
        JsonObject newMegaSettings = new JsonObject();
        if (root.has("mega_settings"))
            megaSettings = root.get("mega_settings").getAsJsonObject();

        if (megaSettings.has("enable_mega_evolution"))
            enableMegaEvolution = megaSettings.get("enable_mega_evolution").getAsBoolean();
        newMegaSettings.addProperty("enable_mega_evolution", enableMegaEvolution);

        if (megaSettings.has("allow_mega_outside_battles"))
            allowMegaOutsideBattles = megaSettings.get("allow_mega_outside_battles").getAsBoolean();
        newMegaSettings.addProperty("allow_mega_outside_battles", allowMegaOutsideBattles);

        if (megaSettings.has("use_tradeable_property"))
            useTradeableProperty = megaSettings.get("use_tradeable_property").getAsBoolean();
        megaSettings.addProperty("use_tradeable_property", useTradeableProperty);

        newRoot.add("mega_settings", megaSettings);

        JsonObject zPowerSettings = new JsonObject();
        JsonObject newZPowerSettings = new JsonObject();
        if (root.has("z_power_settings"))
            zPowerSettings = root.get("z_power_settings").getAsJsonObject();

        if (zPowerSettings.has("enable_z_crystals"))
            enableZCrystals = zPowerSettings.get("enable_z_crystals").getAsBoolean();
        newZPowerSettings.addProperty("enable_z_crystals", enableZCrystals);

        newRoot.add("z_power_settings", newZPowerSettings);

        JsonObject teraSettings = new JsonObject();
        JsonObject newTeraSettings = new JsonObject();
        if (root.has("tera_settings"))
            teraSettings = root.get("tera_settings").getAsJsonObject();

        if (teraSettings.has("enable_tera"))
            enableTera = teraSettings.get("enable_tera").getAsBoolean();
        newTeraSettings.addProperty("enable_tera", enableTera);

        if (teraSettings.has("tera_shards_required"))
            teraShardsRequired = teraSettings.get("tera_shards_required").getAsInt();
        if (teraShardsRequired > 64) teraShardsRequired = 64;
        if (teraShardsRequired < 1) teraShardsRequired = 1;
        newTeraSettings.addProperty("tera_shards_required", teraShardsRequired);

        if (teraSettings.has("consume_tera_shards"))
            consumeTeraShards = teraSettings.get("consume_tera_shards").getAsBoolean();
        newTeraSettings.addProperty("consume_tera_shards", consumeTeraShards);

        if (teraSettings.has("require_orb_recharge"))
            requireOrbRecharge = teraSettings.get("require_orb_recharge").getAsBoolean();
        newTeraSettings.addProperty("require_orb_recharge", requireOrbRecharge);

        if (teraSettings.has("fix_ogerpon_tera_type"))
            fixOgerponTeraType = teraSettings.get("fix_ogerpon_tera_type").getAsBoolean();
        newTeraSettings.addProperty("fix_ogerpon_tera_type", fixOgerponTeraType);

        if (teraSettings.has("fix_terapagos_tera_type"))
            fixTerapagosTeraType = teraSettings.get("fix_terapagos_tera_type").getAsBoolean();
        newTeraSettings.addProperty("fix_terapagos_tera_type", fixTerapagosTeraType);

        newRoot.add("tera_settings", newTeraSettings);

        JsonObject fusionSettings = new JsonObject();
        JsonObject newFusionSettings = new JsonObject();
        if (root.has("fusion_settings"))
            fusionSettings = root.get("fusion_settings").getAsJsonObject();

        if (fusionSettings.has("enable_fusions"))
            enableFusions = fusionSettings.get("enable_fusions").getAsBoolean();

        newFusionSettings.addProperty("enable_fusions", enableFusions);

        newRoot.add("fusion_settings", newFusionSettings);

        JsonObject dynamaxSettings = new JsonObject();
        JsonObject newDynamaxSettings = new JsonObject();
        if (root.has("dynamax_settings"))
            dynamaxSettings = root.get("dynamax_settings").getAsJsonObject();

        if (dynamaxSettings.has("enable_dynamax"))
            enableDynamax = dynamaxSettings.get("enable_dynamax").getAsBoolean();

        newDynamaxSettings.addProperty("enable_dynamax", enableDynamax);

        if (dynamaxSettings.has("enable_gigantamax"))
            enableGigantamax = dynamaxSettings.get("enable_gigantamax").getAsBoolean();

        newDynamaxSettings.addProperty("enable_gigantamax", enableGigantamax);

        newRoot.add("dynamax_settings", newDynamaxSettings);

        configFile.delete();
        configFile.createNewFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Writer writer = new FileWriter(configFile);
        gson.toJson(newRoot, writer);
        writer.close();
    }
}