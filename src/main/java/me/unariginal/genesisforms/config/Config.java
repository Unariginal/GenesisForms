package me.unariginal.genesisforms.config;

import com.google.gson.*;
import me.unariginal.genesisforms.GenesisForms;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public boolean debug = false;

    public List<String> disabledItems = new ArrayList<>();
    public record ItemConversion(String input, String output) {}
    public List<ItemConversion> itemConversions = new ArrayList<>();

    public boolean enableMegaEvolution = true;
    public boolean allowMegaOutsideBattles = true;
    public String megaFeatureName = "mega_evolution";
    public String megaFeatureValue = "mega";
    public String megaXFeatureName = "mega_evolution";
    public String megaXFeatureValue = "mega_x";
    public String megaYFeatureName = "mega_evolution";
    public String megaYFeatureValue = "mega_y";
    public record CustomMega(String baseSpecies, String megastoneID, String megaForm) {}
    public List<CustomMega> customMegaList = new ArrayList<>();

    public boolean enableZCrystals = true;
    public boolean enableUltraBurst = false;

    public boolean enableTera = true;
    public boolean fixOgerponTeraType = true;
    public boolean fixTerapagosTeraType = true;

    public boolean enableDynamax = false;
    public boolean enableGigantamax = false;
    public boolean useGen9Battles = false;

    public boolean enableFusions = true;
    public record FuelPokemon(String species, String featureName, String featureValue) {}
    public record Fusion(String fusionItem, String corePokemon, List<FuelPokemon> fuelPokemon) {}
    public List<Fusion> fusionList = new ArrayList<>(
            List.of(
                    new Fusion("dna_splicers",
                            "kyurem",
                            List.of(
                                    new FuelPokemon("reshiram", "absofusion", "white"),
                                    new FuelPokemon("zekrom", "absofusion", "black")
                            )
                    ),
                    new Fusion("n_solarizer",
                            "necrozma",
                            List.of(
                                    new FuelPokemon("solgaleo", "prism_fusion", "dusk")
                            )
                    ),
                    new Fusion("n_lunarizer",
                            "necrozma",
                            List.of(
                                    new FuelPokemon("lunala", "prism_fusion", "dawn")
                            )
                    ),
                    new Fusion("reins_of_unity",
                            "calyrex",
                            List.of(
                                    new FuelPokemon("glastrier", "king_steed", "ice"),
                                    new FuelPokemon("spectrier", "king_steed", "shadow")
                            )
                    )
            )
    );

    public Config() {
        try {
            loadConfig();
        } catch (IOException e) {
            GenesisForms.INSTANCE.logError("[Genesis] Failed to load config file. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/config.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        }

        if (root.get("debug") != null) {
            debug = root.get("debug").getAsBoolean();
        }
        newRoot.addProperty("debug", debug);

        JsonObject generalSettings = new JsonObject();
        if (root.get("general_settings") != null) {
            generalSettings = root.get("general_settings").getAsJsonObject();
        }

        JsonArray disabledItems = new JsonArray();
        if (generalSettings.get("disabled_items") != null) {
            this.disabledItems.clear();
            generalSettings.get("disabled_items").getAsJsonArray().forEach(element -> disabledItems.add(element.getAsString()));
        }
        for (String item : this.disabledItems) {
            disabledItems.add(item);
        }
        generalSettings.add("disabled_items", disabledItems);

        JsonArray itemConversions = new JsonArray();
        if (generalSettings.get("item_conversions") != null) {
            this.itemConversions.clear();
            for (JsonElement element : generalSettings.get("item_conversions").getAsJsonArray()) {
                JsonObject itemConversion = element.getAsJsonObject();
                if (itemConversion.get("input") != null && itemConversion.get("output") != null) {
                    this.itemConversions.add(new ItemConversion(itemConversion.get("input").getAsString(), itemConversion.get("output").getAsString()));
                }
            }
        }
        for (ItemConversion itemConversion : this.itemConversions) {
            JsonObject itemConversionJson = new JsonObject();
            itemConversionJson.addProperty("input", itemConversion.input);
            itemConversionJson.addProperty("output", itemConversion.output);
            itemConversions.add(itemConversionJson);
        }
        generalSettings.add("item_conversions", itemConversions);
        newRoot.add("general_settings", generalSettings);

        JsonObject megaSettings = new JsonObject();
        if (root.get("mega_settings") != null) {
            megaSettings = root.get("mega_settings").getAsJsonObject();
        }

        if (megaSettings.get("enable_mega_evolution") != null) {
            enableMegaEvolution = megaSettings.get("enable_mega_evolution").getAsBoolean();
        }
        megaSettings.addProperty("enable_mega_evolution", enableMegaEvolution);

        if (megaSettings.get("allow_mega_outside_battles") != null) {
            allowMegaOutsideBattles = megaSettings.get("allow_mega_outside_battles").getAsBoolean();
        }
        megaSettings.addProperty("allow_mega_outside_battles", allowMegaOutsideBattles);

        if (megaSettings.get("mega_feature_name") != null) {
            megaFeatureName = megaSettings.get("mega_feature_name").getAsString();
        }
        megaSettings.addProperty("mega_feature_name", megaFeatureName);

        if (megaSettings.get("mega_feature_value") != null) {
            megaFeatureValue = megaSettings.get("mega_feature_value").getAsString();
        }
        megaSettings.addProperty("mega_feature_value", megaFeatureValue);

        if (megaSettings.get("mega_x_feature_name") != null) {
            megaXFeatureName = megaSettings.get("mega_x_feature_name").getAsString();
        }
        megaSettings.addProperty("mega_x_feature_name", megaXFeatureName);

        if (megaSettings.get("mega_x_feature_value") != null) {
            megaXFeatureValue = megaSettings.get("mega_x_feature_value").getAsString();
        }
        megaSettings.addProperty("mega_x_feature_value", megaXFeatureValue);

        if (megaSettings.get("mega_y_feature_name") != null) {
            megaYFeatureName = megaSettings.get("mega_y_feature_name").getAsString();
        }
        megaSettings.addProperty("mega_y_feature_name", megaYFeatureName);

        if (megaSettings.get("mega_y_feature_value") != null) {
            megaYFeatureValue = megaSettings.get("mega_y_feature_value").getAsString();
        }
        megaSettings.addProperty("mega_y_feature_value", megaYFeatureValue);

        JsonArray customMegaList = new JsonArray();
        if (megaSettings.get("custom_megas") != null) {
            this.customMegaList.clear();
            for (JsonElement element : megaSettings.get("custom_megas").getAsJsonArray()) {
                JsonObject customMega = element.getAsJsonObject();
                if (customMega.get("base_species") != null && customMega.get("megastone_id") != null && customMega.get("mega_form") != null) {
                    this.customMegaList.add(new CustomMega(customMega.get("base_species").getAsString(), customMega.get("megastone_id").getAsString(), customMega.get("mega_form").getAsString()));
                }
            }
        }
        for (CustomMega customMega : this.customMegaList) {
            JsonObject customMegaObject = new JsonObject();
            customMegaObject.addProperty("base_species", customMega.baseSpecies);
            customMegaObject.addProperty("megastone_id", customMega.megastoneID);
            customMegaObject.addProperty("mega_form", customMega.megaForm);
            customMegaList.add(customMegaObject);
        }
        megaSettings.add("custom_megas", customMegaList);
        newRoot.add("mega_settings", megaSettings);

        JsonObject zPowerSettings = new JsonObject();
        if (root.get("z_power_settings") != null) {
            zPowerSettings = root.get("z_power_settings").getAsJsonObject();
        }
        if (zPowerSettings.get("enable_z_crystals") != null) {
            enableZCrystals = zPowerSettings.get("enable_z_crystals").getAsBoolean();
        }
        zPowerSettings.addProperty("enable_z_crystals", enableZCrystals);
        if (zPowerSettings.get("enable_ultra_burst") != null) {
            enableUltraBurst = zPowerSettings.get("enable_ultra_burst").getAsBoolean();
        }
        zPowerSettings.addProperty("enable_ultra_burst", enableUltraBurst);
        newRoot.add("z_power_settings", zPowerSettings);

        JsonObject teraSettings = new JsonObject();
        if (root.get("tera_settings") != null) {
            teraSettings = root.get("tera_settings").getAsJsonObject();
        }
        if (teraSettings.get("enable_tera") != null) {
            enableTera = teraSettings.get("enable_tera").getAsBoolean();
        }
        teraSettings.addProperty("enable_tera", enableTera);
        if (teraSettings.get("fix_ogerpon_tera_type") != null) {
            fixOgerponTeraType = teraSettings.get("fix_ogerpon_tera_type").getAsBoolean();
        }
        teraSettings.addProperty("fix_ogerpon_tera_type", fixOgerponTeraType);
        if (teraSettings.get("fix_terapagos_tera_type") != null) {
            fixTerapagosTeraType = teraSettings.get("fix_terapagos_tera_type").getAsBoolean();
        }
        teraSettings.addProperty("fix_terapagos_tera_type", fixTerapagosTeraType);
        newRoot.add("tera_settings", teraSettings);

        JsonObject fusionSettings = new JsonObject();
        if (root.get("fusion_settings") != null) {
            fusionSettings = root.get("fusion_settings").getAsJsonObject();
        }
        if (fusionSettings.get("enable_fusions") != null) {
            enableFusions = fusionSettings.get("enable_fusions").getAsBoolean();
        }
        fusionSettings.addProperty("enable_fusions", enableFusions);
        JsonArray fusionList = new JsonArray();
        if (fusionSettings.get("fusions") != null) {
            this.fusionList.clear();
            for (JsonElement element : fusionSettings.get("fusions").getAsJsonArray()) {
                JsonObject fusion = element.getAsJsonObject();
                if (fusion.get("fusion_item") != null && fusion.get("core_pokemon") != null && fusion.get("fuel_pokemon") != null) {
                    JsonArray fuelPokemonList = fusion.get("fuel_pokemon").getAsJsonArray();
                    if (fuelPokemonList.isEmpty()) continue;
                    List<FuelPokemon> fuelPokemon = new ArrayList<>();
                    for (JsonElement fuelElement : fuelPokemonList) {
                        JsonObject fuel = fuelElement.getAsJsonObject();
                        if (fuel.get("species") != null && fuel.get("result_feature_name") != null && fuel.get("result_feature_value") != null) {
                            fuelPokemon.add(new FuelPokemon(fuel.get("species").getAsString(), fuel.get("result_feature_name").getAsString(), fuel.get("result_feature_value").getAsString()));
                        } else {
                            GenesisForms.LOGGER.error("[Genesis] Invalid fuel_pokemon element in fusions, skipping");
                        }
                    }
                    this.fusionList.add(new Fusion(fusion.get("fusion_item").getAsString(), fusion.get("core_pokemon").getAsString(), fuelPokemon));
                }
            }
        }
        for (Fusion fusion : this.fusionList) {
            JsonObject customFusionObject = new JsonObject();
            customFusionObject.addProperty("fusion_item", fusion.fusionItem);
            customFusionObject.addProperty("core_pokemon", fusion.corePokemon);
            JsonArray fuelPokemonList = new JsonArray();
            for (FuelPokemon fuelPokemon : fusion.fuelPokemon) {
                JsonObject fuelPokemonObject = new JsonObject();
                fuelPokemonObject.addProperty("species", fuelPokemon.species);
                fuelPokemonObject.addProperty("result_feature_name", fuelPokemon.featureName);
                fuelPokemonObject.addProperty("result_feature_value", fuelPokemon.featureValue);
                fuelPokemonList.add(fuelPokemonObject);
            }
            customFusionObject.add("fuel_pokemon", fuelPokemonList);
            fusionList.add(customFusionObject);
        }
        fusionSettings.add("fusions", fusionList);
        newRoot.add("fusion_settings", fusionSettings);

        JsonObject dynamaxSettings = new JsonObject();
        if (root.get("dynamax_settings") != null) {
            dynamaxSettings = root.get("dynamax_settings").getAsJsonObject();
        }
        if (dynamaxSettings.get("enable_dynamax") != null) {
            enableDynamax = dynamaxSettings.get("enable_dynamax").getAsBoolean();
        }
        dynamaxSettings.addProperty("enable_dynamax", enableDynamax);
        if (dynamaxSettings.get("enable_gigantamax") != null) {
            enableGigantamax = dynamaxSettings.get("enable_gigantamax").getAsBoolean();
        }
        dynamaxSettings.addProperty("enable_gigantamax", enableGigantamax);
        if (dynamaxSettings.get("use_gen_9_battles") != null) {
            useGen9Battles = dynamaxSettings.get("use_gen_9_battles").getAsBoolean();
        }
        dynamaxSettings.addProperty("use_gen_9_battles", useGen9Battles);
        newRoot.add("dynamax_settings", dynamaxSettings);

        configFile.delete();
        configFile.createNewFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter(configFile);
        gson.toJson(newRoot, writer);
        writer.close();
    }
}
