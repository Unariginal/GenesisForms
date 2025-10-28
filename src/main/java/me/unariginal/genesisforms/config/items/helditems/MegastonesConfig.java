package me.unariginal.genesisforms.config.items.helditems;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.unariginal.genesisforms.config.items.MiscItemsConfig;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MegastonesConfig {
    public static LinkedHashMap<String, MegastoneData> megastoneMap = new LinkedHashMap<>();

    public static class MegastoneData {
        @SerializedName(value = "showdown_id", alternate = {"showdownId", "showdownID", "showdownid"})
        public String showdownID;
        public String species;

        @SerializedName(value = "feature_name", alternate = "featureName")
        public String featureName;
        @SerializedName(value = "feature_value", alternate = "featureValue")
        public String featureValue;

        public List<String> lore;

        public MegastoneData(String showdownID, String species, String featureName, String featureValue, List<String> lore) {
            this.showdownID = showdownID;
            this.species = species;
            this.featureName = featureName;
            this.featureValue = featureValue;
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create();

        String json = "{}";

        File megastonesConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/megastones.json").toFile();
        if (!megastonesConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(megastonesConfigFile, "/genesis_configs/items/held_items/megastones.json");
        }
        if (megastonesConfigFile.exists()) json = JsonParser.parseReader(new FileReader(megastonesConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, MegastoneData>>() {}.getType();
        megastoneMap = gson.fromJson(json, mapType);
        LinkedHashMap<String, MegastoneData> zaMegas = getNewMegastones();
        zaMegas.forEach((s, megastoneData) -> {
            megastoneMap.putIfAbsent(s, megastoneData);
        });
        save();
    }

    public static void save() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create();

        FileWriter writer = new FileWriter("config/GenesisForms/items/held_items/megastones.json");
        Type mapType = new TypeToken<LinkedHashMap<String, MegastoneData>>() {}.getType();
        gson.toJson(megastoneMap, mapType, writer);
        writer.close();
    }

    public static List<MegastoneData> getMegastoneBySpecies(String species) {
        List<MegastoneData> megastoneData = new ArrayList<>();
        for (MegastoneData data : megastoneMap.values()) {
            if (data.species.equalsIgnoreCase(species)) megastoneData.add(data);
        }
        return megastoneData;
    }

    public static LinkedHashMap<String, MegastoneData> getNewMegastones() {
        LinkedHashMap<String, MegastoneData> zaMegas = new LinkedHashMap<>();
        zaMegas.put("barbaracite", new MegastoneData(
                "barbaracite",
                "barbaracle",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Barbaracle hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("chandelurite", new MegastoneData(
                "chandelurite",
                "chandelure",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Chandelure hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("chesnaughtite", new MegastoneData(
                "chesnaughtite",
                "chesnaught",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Chesnaught hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("clefablite", new MegastoneData(
                "clefablite",
                "clefable",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Clefable hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("delphoxite", new MegastoneData(
                "delphoxite",
                "delphox",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Delphox hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("dragalgite", new MegastoneData(
                "dragalgite",
                "dragalge",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Dragalge hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("dragoninite", new MegastoneData(
                "dragoninite",
                "dragonite",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Dragonite hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("drampanite", new MegastoneData(
                "drampanite",
                "drampa",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Drampa hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("eelektrossite", new MegastoneData(
                "eelektrossite",
                "eelektross",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Eelektross hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("emboarite", new MegastoneData(
                "emboarite",
                "emboar",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Emboar hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("excadrite", new MegastoneData(
                "excadrite",
                "excadrill",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Excadrill hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("falinksite", new MegastoneData(
                "falinksite",
                "falinks",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Falinks hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("feraligite", new MegastoneData(
                "feraligite",
                "feraligatr",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Feraligatr hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("floettite", new MegastoneData(
                "floettite",
                "floette",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Floette hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("froslassite", new MegastoneData(
                "froslassite",
                "froslass",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Froslass hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("greninjite", new MegastoneData(
                "greninjite",
                "greninja",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Greninja hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        // Why am I doing this..
        zaMegas.put("hawluchanite", new MegastoneData(
                "hawluchanite",
                "hawlucha",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Hawlucha hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("malamarite", new MegastoneData(
                "malamarite",
                "malamar",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Malamar hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("meganiumite", new MegastoneData(
                "meganiumite",
                "meganium",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Meganium hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("pyroarite", new MegastoneData(
                "pyroarite",
                "pyroar",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Pyroar hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("raichunite-x", new MegastoneData(
                "raichunitex",
                "raichu",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Raichu hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("raichunite-y", new MegastoneData(
                "raichunitey",
                "raichu",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Raichu hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("scolipite", new MegastoneData(
                "scolipite",
                "scolipede",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Scolipede hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("scraftinite", new MegastoneData(
                "scraftinite",
                "scrafty",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Scrafty hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("skarmorite", new MegastoneData(
                "skarmorite",
                "skarmory",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Skarmory hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("starminite", new MegastoneData(
                "starminite",
                "starmie",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Starmie hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("victreebelite", new MegastoneData(
                "victreebelite",
                "victreebel",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Victreebel hold it, and this stone will enable it to Mega Evolve during battle.")
        ));
        zaMegas.put("zygardite", new MegastoneData(
                "zygardite",
                "zygarde",
                "mega_evolution",
                "mega",
                List.of("<gray>One of a variety of mysterious Mega Stones.",
                        "<gray>Have Zygarde hold it, and this stone will enable it to Mega Evolve during battle.")
        ));

        return zaMegas;
    }
}
