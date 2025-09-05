package me.unariginal.genesisforms.config.items.accessories;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

public class AccessoriesConfig {
    public static LinkedHashMap<String, AccessoryData> megaAccessories = new LinkedHashMap<>();
    public static LinkedHashMap<String, AccessoryData> teraAccessories = new LinkedHashMap<>();
    public static LinkedHashMap<String, AccessoryData> zAccessories = new LinkedHashMap<>();
    public static LinkedHashMap<String, AccessoryData> dmaxAccessories = new LinkedHashMap<>();

    public static class AccessoryData {
        public List<String> lore;

        public AccessoryData(List<String> lore) {
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        Type mapType = new TypeToken<LinkedHashMap<String, AccessoryData>>() {}.getType();

        String json = "{}";

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/accessories/mega_accessories.json").toFile();
        if (!configFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/accessories/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(configFile, "/genesis_configs/items/accessories/mega_accessories.json");
        }
        if (configFile.exists()) json = JsonParser.parseReader(new FileReader(configFile)).toString();

        megaAccessories = gson.fromJson(json, mapType);

        json = "{}";

        configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/accessories/tera_accessories.json").toFile();
        if (!configFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/accessories/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(configFile, "/genesis_configs/items/accessories/tera_accessories.json");
        }
        if (configFile.exists()) json = JsonParser.parseReader(new FileReader(configFile)).toString();

        teraAccessories = gson.fromJson(json, mapType);

        json = "{}";

        configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/accessories/z_accessories.json").toFile();
        if (!configFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/accessories/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(configFile, "/genesis_configs/items/accessories/z_accessories.json");
        }
        if (configFile.exists()) json = JsonParser.parseReader(new FileReader(configFile)).toString();

        zAccessories = gson.fromJson(json, mapType);

        json = "{}";

        configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/accessories/dynamax_accessories.json").toFile();
        if (!configFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/accessories/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(configFile, "/genesis_configs/items/accessories/dynamax_accessories.json");
        }
        if (configFile.exists()) json = JsonParser.parseReader(new FileReader(configFile)).toString();

        dmaxAccessories = gson.fromJson(json, mapType);
    }
}
