package me.unariginal.genesisforms.config;

import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BattleFormChanges {
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

    public BattleFormChanges() throws IOException {
//        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();
//        if (!rootFolder.exists()) rootFolder.mkdirs();
//
//        File configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/battle_forms.json").toFile();
//        String json = "{}";
//        if (!configFile.exists()) {
//
//        }
//        if (configFile.exists()) json = JsonParser.parseReader(new FileReader(configFile)).toString();
//
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .disableHtmlEscaping()
//                .create();
//
//        Type mapType = new TypeToken<Map<String, BattleFormInformation>>() {}.getType();
//        battleForms = gson.fromJson(json, mapType);

        // TODO: No hardcoding after 1.7 is ready
        battleForms.put("aegislash_stance",
                new BattleFormInformation(
                        "aegislash",
                        new BattleForm("stance_forme", "shield"),
                        Map.of("blade", new BattleForm("stance_forme", "blade"))
                )
        );
        battleForms.put("minior_shield",
                new BattleFormInformation(
                        "minior",
                        new BattleForm("meteor_shield", "core"),
                        Map.of("meteor", new BattleForm("meteor_shield", "meteor"))
                )
        );
        battleForms.put("castform_forecast",
                new BattleFormInformation(
                        "castform",
                        new BattleForm("forecast_form", "normal"),
                        Map.of("sunny", new BattleForm("forecast_form", "sunny"),
                                "rainy", new BattleForm("forecast_form", "rainy"),
                                "snowy", new BattleForm("forecast_form", "snowy"))
                )
        );
        battleForms.put("wishiwashi_schooling",
                new BattleFormInformation(
                        "wishiwashi",
                        new BattleForm("schooling_form", "solo"),
                        Map.of("school", new BattleForm("schooling_form", "school"))
                )
        );
        battleForms.put("mimikyu_disguise",
                new BattleFormInformation(
                        "mimikyu",
                        new BattleForm("disguise_form", "disguised"),
                        Map.of("busted", new BattleForm("disguise_form", "busted"))
                )
        );
        battleForms.put("greninja_ash",
                new BattleFormInformation(
                        "greninja",
                        new BattleForm("battle_bond", "bond"),
                        Map.of("ash", new BattleForm("battle_bond", "ash"))
                )
        );
        battleForms.put("cherrim_blossom",
                new BattleFormInformation(
                        "cherrim",
                        new BattleForm("blossom_form", "overcast"),
                        Map.of("sunshine", new BattleForm("blossom_form", "sunshine"))
                )
        );
        battleForms.put("morpeko_hunger",
                new BattleFormInformation(
                        "morpeko",
                        new BattleForm("hunger_mode", "full_belly"),
                        Map.of("hangry", new BattleForm("hunger_mode", "c"))
                )
        );
        battleForms.put("palafin_dolphin",
                new BattleFormInformation(
                        "palafin",
                        new BattleForm("dolphin_form", "zero"),
                        Map.of("hero", new BattleForm("dolphin_form", "hero"))
                )
        );
        battleForms.put("eiscue_head",
                new BattleFormInformation(
                        "eiscue",
                        new BattleForm("penguin_head", "ice_face"),
                        Map.of("noice", new BattleForm("penguin_head", "noice"))
                )
        );
        battleForms.put("cramorant_missile",
                new BattleFormInformation(
                        "cramorant",
                        new BattleForm("missile_form", "none"),
                        Map.of("gulping", new BattleForm("missile_form", "gulping"),
                                "gorging", new BattleForm("missile_form", "gorging"))
                )
        );
        battleForms.put("darmanitan_blazing",
                new BattleFormInformation(
                        "darmanitan",
                        new BattleForm("blazing_mode", "standard"),
                        Map.of("zen", new BattleForm("blazing_mode", "zen"))
                )
        );
        battleForms.put("xerneas_life",
                new BattleFormInformation(
                        "xerneas",
                        new BattleForm("life_mode", "neutral"),
                        Map.of("active", new BattleForm("life_mode", "active"))
                )
        );
        battleForms.put("terapagos_tera",
                new BattleFormInformation(
                        "terapagos",
                        new BattleForm("tera_form", "normal"),
                        Map.of("terastal", new BattleForm("tera_form", "terastal"))
                )
        );
        battleForms.put("meloetta_song",
                new BattleFormInformation(
                        "meloetta",
                        new BattleForm("song_forme", "aria"),
                        Map.of("pirouette", new BattleForm("song_forme", "pirouette"))
                )
        );
    }
}
