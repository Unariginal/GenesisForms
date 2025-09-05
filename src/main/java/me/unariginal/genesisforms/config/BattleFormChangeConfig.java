package me.unariginal.genesisforms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BattleFormChangeConfig {
    public static Map<String, BattleFormInformation> battleForms = new HashMap<>();

    public static class BattleFormInformation {
        public String species;

        @SerializedName(value = "default_form", alternate = "defaultForm")
        public BattleForm defaultForm;

        public Map<String, BattleForm> forms;

        public BattleFormInformation(String species, BattleForm defaultForm, Map<String, BattleForm> forms) {
            this.species = species;
            this.defaultForm = defaultForm;
            this.forms = forms;
        }
    }

    public static class BattleForm {
        @SerializedName(value = "feature_name", alternate = "featureName")
        public String featureName;

        @SerializedName(value = "feature_value", alternate = "featureValue")
        public String featureValue;

        public BattleForm(String featureName, String featureValue) {
            this.featureName = featureName;
            this.featureValue = featureValue;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();
        if (!rootFolder.exists()) rootFolder.mkdirs();

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/battle_forms.json").toFile();
        String json = "{}";
        if (!configFile.exists()) ConfigUtils.create(configFile, "/genesis_configs/battle_forms.json");
        if (configFile.exists()) json = JsonParser.parseReader(new FileReader(configFile)).toString();

        Type mapType = new TypeToken<Map<String, BattleFormInformation>>() {}.getType();
        battleForms = gson.fromJson(json, mapType);
    }
}
