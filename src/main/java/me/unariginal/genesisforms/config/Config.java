package me.unariginal.genesisforms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.unariginal.genesisforms.GenesisForms;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class Config {
    public boolean debug = false;
    public boolean enableMegaEvolution = true;
    public boolean allowMegaOutsideBattles = true;
    public boolean enableZCrystals = true;
    public boolean enableTera = true;
    public boolean fixOgerponTeraType = true;
    public boolean fixTerapagosTeraType = true;
    public boolean enableUltraBurst = false;
    public boolean enableDynamax = false;
    public boolean enableGigantamax = false;
    public float dynamaxScale = 4.0F;
    public boolean enableFusions = true;

    public Config() {
        try {
            loadConfig();
        } catch (IOException e) {
            GenesisForms.INSTANCE.logError("[Genesis] Failed to load config files. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/config.json").toFile();
        if (configFile.createNewFile()) {
            JsonObject root = new JsonObject();
            root.addProperty("debug", debug);
            root.addProperty("enable_mega_evolution", enableMegaEvolution);
            root.addProperty("allow_mega_outside_battles", allowMegaOutsideBattles);
            root.addProperty("enable_z_crystals", enableZCrystals);
            root.addProperty("enable_tera", enableTera);
            root.addProperty("fix_ogerpon_tera_type", fixOgerponTeraType);
            root.addProperty("fix_terapagos_tera_type", fixTerapagosTeraType);
            root.addProperty("enable_ultra_burst", enableUltraBurst);
            root.addProperty("enable_dynamax", enableDynamax);
            root.addProperty("enable_gigantamax", enableGigantamax);
            root.addProperty("dynamax_scale", dynamaxScale);
            root.addProperty("enable_fusions", enableFusions);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = new FileWriter(configFile);
            gson.toJson(root, writer);
            writer.close();
        } else {
            JsonObject root = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
            debug = root.get("debug").getAsBoolean();
            enableMegaEvolution = root.get("enable_mega_evolution").getAsBoolean();
            allowMegaOutsideBattles = root.get("allow_mega_outside_battles").getAsBoolean();
            enableZCrystals = root.get("enable_z_crystals").getAsBoolean();
            enableTera = root.get("enable_tera").getAsBoolean();
            fixOgerponTeraType = root.get("fix_ogerpon_tera_type").getAsBoolean();
            fixTerapagosTeraType = root.get("fix_terapagos_tera_type").getAsBoolean();
            enableUltraBurst = root.get("enable_ultra_burst").getAsBoolean();
            enableDynamax = root.get("enable_dynamax").getAsBoolean();
            enableGigantamax = root.get("enable_gigantamax").getAsBoolean();
            dynamaxScale = root.get("dynamax_scale").getAsFloat();
            enableFusions = root.get("enable_fusions").getAsBoolean();
        }
    }
}
