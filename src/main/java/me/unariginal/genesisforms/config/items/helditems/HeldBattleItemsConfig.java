package me.unariginal.genesisforms.config.items.helditems;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

public class HeldBattleItemsConfig {
    public static LinkedHashMap<String, HeldBattleItemData> heldBattleItemMap = new LinkedHashMap<>();

    public static class HeldBattleItemData {
        @SerializedName(value = "showdown_id", alternate = {"showdownId", "showdownID", "showdownid"})
        public String showdownID;
        public List<String> lore;

        public HeldBattleItemData(String showdownID, List<String> lore) {
            this.showdownID = showdownID;
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File heldBattleItemsConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/held_battle_items.json").toFile();
        if (!heldBattleItemsConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(heldBattleItemsConfigFile, "/genesis_configs/items/held_items/held_battle_items.json");
        }
        if (heldBattleItemsConfigFile.exists()) json = JsonParser.parseReader(new FileReader(heldBattleItemsConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, HeldBattleItemData>>() {}.getType();
        heldBattleItemMap = gson.fromJson(json, mapType);
    }
}
