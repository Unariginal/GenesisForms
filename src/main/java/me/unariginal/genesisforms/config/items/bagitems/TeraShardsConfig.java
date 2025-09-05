package me.unariginal.genesisforms.config.items.bagitems;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

public class TeraShardsConfig {
    public static LinkedHashMap<String, TeraShardData> teraShardMap = new LinkedHashMap<>();

    public static class TeraShardData {
        @SerializedName(value = "tera_type")
        public String teraType;
        @SerializedName(value = "max_stack_size")
        public int maxStackSize;

        public List<String> lore;

        public TeraShardData(String teraType, int maxStackSize, List<String> lore) {
            this.teraType = teraType;
            this.maxStackSize = maxStackSize;
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File teraShardConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/bag_items/tera_shards.json").toFile();
        if (!teraShardConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/bag_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(teraShardConfigFile, "/genesis_configs/items/bag_items/tera_shards.json");
        }
        if (teraShardConfigFile.exists()) json = JsonParser.parseReader(new FileReader(teraShardConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, TeraShardData>>() {}.getType();
        teraShardMap = gson.fromJson(json, mapType);
    }
}
