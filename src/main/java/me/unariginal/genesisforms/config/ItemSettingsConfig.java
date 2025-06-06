package me.unariginal.genesisforms.config;

import com.google.gson.*;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.items.keyitems.KeyFormItems;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemSettingsConfig {
    public Map<String, List<String>> item_lore = new HashMap<>();
    public Map<String, KeyFormItems.FormInformation> custom_key_form_items = new HashMap<>();
    public record CustomHeldItem(String species, String feature_name, String default_feature_value, String feature_value) {}
    public Map<String, CustomHeldItem> custom_held_items = new HashMap<>();
    public record FuelPokemon(String species, String featureName, String featureValue) {}
    public record Fusion(String corePokemon, List<FuelPokemon> fuelPokemon) {}
    public Map<String, List<Fusion>> fusionList = new HashMap<>(
            Map.of(
                    "dna_splicers",
                    List.of(
                            new Fusion(
                                    "kyurem",
                                    List.of(
                                            new FuelPokemon("reshiram", "absofusion", "white"),
                                            new FuelPokemon("zekrom", "absofusion", "black")
                                    )
                            )
                    ),
                    "n_solarizer",
                    List.of(
                            new Fusion(
                                    "necrozma",
                                    List.of(
                                            new FuelPokemon("solgaleo", "prism_fusion", "dusk")
                                    )
                            )
                    ),
                    "n_lunarizer",
                    List.of(
                            new Fusion(
                                    "necrozma",
                                    List.of(
                                            new FuelPokemon("lunala", "prism_fusion", "dawn")
                                    )
                            )
                    ),
                    "reins_of_unity",
                    List.of(
                            new Fusion(
                                    "calyrex",
                                    List.of(
                                            new FuelPokemon("glastrier", "king_steed", "ice"),
                                            new FuelPokemon("spectrier", "king_steed", "shadow")
                                    )
                            )
                    )
            )
    );

    public ItemSettingsConfig() {
        fillItemLore();
        try {
            loadConfig();
        } catch (IOException e) {
            GenesisForms.INSTANCE.logError("[Genesis] Failed to load item settings config file. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

        File itemSettingsFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/item_settings.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (itemSettingsFile.exists()) {
            root = JsonParser.parseReader(new FileReader(itemSettingsFile)).getAsJsonObject();
        }

        JsonObject custom_items = new JsonObject();
        if (root.has("custom_items")) {
            custom_items = root.getAsJsonObject("custom_items");
        }

        JsonObject key_form_items = new JsonObject();
        if (custom_items.has("key_form_change_items")) {
            key_form_items = custom_items.getAsJsonObject("key_form_change_items");
            for (String key : key_form_items.keySet()) {
                JsonObject item = key_form_items.getAsJsonObject(key);
                if (!item.has("species")
                        || !item.has("feature_name")
                        || !item.has("base_feature_value")) {
                    continue;
                }
                JsonArray species_array = item.getAsJsonArray("species");
                List<String> species = new ArrayList<>();
                for (JsonElement spec : species_array) {
                    species.add(spec.getAsString());
                }
                String feature_name = item.get("feature_name").getAsString();
                String base_feature_value = item.get("base_feature_value").getAsString();
                String alternate_feature_value = null;
                if (item.has("alternate_feature_value")) {
                    alternate_feature_value = item.get("alternate_feature_value").getAsString();
                    if (alternate_feature_value.isEmpty()) {
                        alternate_feature_value = null;
                    }
                }

                custom_key_form_items.put(key, new KeyFormItems.FormInformation(species, feature_name, base_feature_value, alternate_feature_value));
            }
        } else {
            custom_items.add("key_form_change_items", key_form_items);
        }

        JsonObject held_items = new JsonObject();
        if (custom_items.has("held_items")) {
            held_items = custom_items.getAsJsonObject("held_items");
            for (String key : held_items.keySet()) {
                JsonObject item = held_items.getAsJsonObject(key);
                if (!item.has("species")
                        || !item.has("feature_name")
                        || !item.has("default_feature_value")
                        || !item.has("feature_value")) {
                    continue;
                }
                String species = item.get("species").getAsString();
                String default_feature_value = item.get("default_feature_value").getAsString();
                String feature_value = item.get("feature_value").getAsString();
                String feature_name = item.get("feature_name").getAsString();
                custom_held_items.put(key, new CustomHeldItem(species, feature_name, default_feature_value, feature_value));
            }
        } else {
            custom_items.add("held_items", held_items);
        }

        JsonObject fusion_items = new JsonObject();
        if (custom_items.has("fusion_items")) {
            fusion_items = custom_items.getAsJsonObject("fusion_items");
            fusionList.clear();
            for (String key : fusion_items.keySet()) {
                JsonObject item = fusion_items.getAsJsonObject(key);
                if (!item.has("fusions")) {
                    continue;
                }
                List<Fusion> fusionsList = new ArrayList<>();
                JsonArray fusions = item.getAsJsonArray("fusions");
                for (JsonElement fusion : fusions) {
                    JsonObject fusion_object = fusion.getAsJsonObject();
                    if (!fusion_object.has("core_pokemon")
                            || !fusion_object.has("fuel_pokemon")) {
                        continue;
                    }
                    String core_pokemon = fusion_object.get("core_pokemon").getAsString();
                    JsonArray fuel_pokemon = fusion_object.getAsJsonArray("fuel_pokemon");
                    List<FuelPokemon> fuelPokemonList = new ArrayList<>();
                    for (JsonElement fuel_pokemon_object : fuel_pokemon) {
                        JsonObject fuel_object = fuel_pokemon_object.getAsJsonObject();
                        if (!fuel_object.has("species")
                                || !fuel_object.has("result_feature_name")
                                || !fuel_object.has("result_feature_value")) {
                            continue;
                        }
                        String species = fuel_object.get("species").getAsString();
                        String result_feature_name = fuel_object.get("result_feature_name").getAsString();
                        String result_feature_value = fuel_object.get("result_feature_value").getAsString();
                        fuelPokemonList.add(new FuelPokemon(species, result_feature_name, result_feature_value));
                    }
                    if (fuelPokemonList.isEmpty()) {
                        continue;
                    }
                    fusionsList.add(new Fusion(core_pokemon, fuelPokemonList));
                }
                if (fusionsList.isEmpty()) {
                    continue;
                }
                fusionList.put(key, fusionsList);
            }
        } else {
            for (String key : fusionList.keySet()) {
                JsonObject fusionItem = new JsonObject();
                JsonArray fusions = new JsonArray();
                for (Fusion fusion : fusionList.get(key)) {
                    JsonObject fusion_object = new JsonObject();
                    fusion_object.addProperty("core_pokemon", fusion.corePokemon());
                    JsonArray fuel_pokemon = new JsonArray();
                    for (FuelPokemon fuelPokemon : fusion.fuelPokemon()) {
                        JsonObject fuel_object = new JsonObject();
                        fuel_object.addProperty("species", fuelPokemon.species());
                        fuel_object.addProperty("result_feature_name", fuelPokemon.featureName());
                        fuel_object.addProperty("result_feature_value", fuelPokemon.featureValue());
                        fuel_pokemon.add(fuel_object);
                    }
                    fusion_object.add("fuel_pokemon", fuel_pokemon);
                    fusions.add(fusion_object);
                }
                fusionItem.add("fusions", fusions);
                fusion_items.add(key, fusionItem);
            }
            custom_items.add("fusion_items", fusion_items);
        }

        newRoot.add("custom_items", custom_items);

        JsonObject item_lore = new JsonObject();
        if (root.has("item_lore")) {
            item_lore = root.getAsJsonObject("item_lore");
            for (String key : item_lore.keySet()) {
                JsonArray jsonLore = item_lore.getAsJsonArray(key);
                List<String> lore = new ArrayList<>();
                for (JsonElement line : jsonLore) {
                    lore.add(line.getAsString());
                }
                this.item_lore.put(key, lore);
            }
        }

        for (String key : this.item_lore.keySet()) {
            if (!item_lore.has(key)) {
                List<String> lore = this.item_lore.get(key);
                JsonArray jsonLore = new JsonArray();
                for (String line : lore) {
                    jsonLore.add(line);
                }
                item_lore.add(key, jsonLore);
            }
        }
        newRoot.add("item_lore", item_lore);

        itemSettingsFile.delete();
        itemSettingsFile.createNewFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter(itemSettingsFile);
        gson.toJson(newRoot, writer);
        writer.close();
    }

    public void fillItemLore() {
        item_lore.put("key_stone", List.of("A stone filled with an unexplained power.", "It makes Pokémon that battle with a Mega Stone Mega Evolve."));
        item_lore.put("mega_bracelet", List.of("This bracelet contains an unknown power that somehow enables a Pokémon carrying a Mega Stone to Mega Evolve in battle."));
        item_lore.put("mega_charm", List.of("This charm contains an unknown power that somehow enables a Pokémon carrying a Mega Stone to Mega Evolve in battle."));
        item_lore.put("mega_cuff", List.of("This cuff contains an unknown power that somehow enables a Pokémon carrying a Mega Stone to Mega Evolve in battle."));
        item_lore.put("mega_ring", List.of("This ring contains an unknown power that somehow enables a Pokémon carrying a Mega Stone to Mega Evolve in battle."));

        item_lore.put("z_ring", List.of("A mysterious ring that enables Pokémon to use Z-Power.", "It requires both the willpower and the physical power of the Trainer wearing it."));
        item_lore.put("z_power_ring", List.of("A mysterious ring that enables Pokémon to use Z-Power.", "It requires both the willpower and the physical power of the Trainer wearing it."));

        item_lore.put("tera_orb", List.of("An orb that holds within it the power to crystallize.", "When it is charged with energy, it can be used to cause Pokémon to Terastallize."));

        item_lore.put("dynamax_band", List.of("A Wishing Star has been affixed to it.", "It lets out a light that allows Pokémon to Dynamax when at a Power Spot."));

        item_lore.put("adamant_crystal", List.of("An item to be held by Dialga.", "This large, glowing gem wells with power and allows the Pokémon to change form."));
        item_lore.put("griseous_core", List.of("An item to be held by Giratina.", "This large, glowing gem wells with power and allows the Pokémon to change form."));
        item_lore.put("lustrous_globe", List.of("An item to be held by Palkia.", "This large, glowing orb wells with power and allows the Pokémon to change form."));

        item_lore.put("gracidea_flower", List.of("A flower sometimes bundled into a bouquet to be given as an expression of gratitude on special occasions, such as birthdays and anniversaries."));
        item_lore.put("meteorite", List.of("A rock that fell to earth from space. It’s slightly warm to the touch.", "It allows a certain species of Pokémon to change forms."));
        item_lore.put("reveal_glass", List.of("A mysterious looking glass that reveals the truth.", "It can return a Pokémon to its original shape."));

        item_lore.put("n_lunarizer", List.of("A machine to fuse Necrozma, which needs light, and Lunala."));
        item_lore.put("n_solarizer", List.of("A machine to fuse Necrozma, which needs light, and Solgaleo."));
        item_lore.put("dna_splicers", List.of("A splicer that fuses Kyurem and a certain Pokémon.", "They are said to have originally been one."));
        item_lore.put("reins_of_unity", List.of("Reins that people presented to the king.", "They enhance Calyrex’s power over bountiful harvests and unite Calyrex with its beloved steed."));

        item_lore.put("prison_bottle", List.of("A bottle believed to have been used to seal away the power of a certain Pokémon long, long ago."));

        item_lore.put("pink_nectar", List.of("Flower nectar obtained from a pink flower.", "It changes the form of a certain species of Pokémon."));
        item_lore.put("purple_nectar", List.of("Flower nectar obtained from a purple flower.", "It changes the form of a certain species of Pokémon."));
        item_lore.put("yellow_nectar", List.of("Flower nectar obtained from a yellow flower.", "It changes the form of a certain species of Pokémon."));
        item_lore.put("red_nectar", List.of("Flower nectar obtained from a red flower.", "It changes the form of a certain species of Pokémon."));

        item_lore.put("zygarde_cube", List.of("An item in which Zygarde Cores and Cells are gathered.", "You can also use it to change Zygarde's forms."));

        item_lore.put("rusted_sword", List.of("It is said that a hero used this sword to halt a terrible disaster in ancient times.", "But it's grown rusty and worn."));
        item_lore.put("rusted_shield", List.of("It is said that a hero used this shield to halt a terrible disaster in ancient times.", "But it's grown rusty and worn."));

        item_lore.put("insect_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of bugs and boosts the power of the holder’s Bug-type moves."));
        item_lore.put("dread_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of darkness and boosts the power of the holder’s Dark-type moves."));
        item_lore.put("draco_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of dragons and boosts the power of the holder’s Dragon-type moves."));
        item_lore.put("zap_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of electricity and boosts the power of the holder’s Electric-type moves."));
        item_lore.put("pixie_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of fairies and boosts the power of the holder’s Fairy-type moves."));
        item_lore.put("fist_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of the fighting spirit and boosts the power of the holder’s Fighting-type moves."));
        item_lore.put("flame_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of fire and boosts the power of the holder’s Fire-type moves."));
        item_lore.put("sky_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of flight and boosts the power of the holder’s Flying-type moves."));
        item_lore.put("spooky_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of ghosts and boosts the power of the holder’s Ghost-type moves."));
        item_lore.put("meadow_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of plants and boosts the power of the holder’s Grass-type moves."));
        item_lore.put("earth_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of the earth and boosts the power of the holder’s Ground-type moves."));
        item_lore.put("icicle_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of ice and boosts the power of the holder’s Ice-type moves."));
        item_lore.put("toxic_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of poison and boosts the power of the holder’s Poison-type moves."));
        item_lore.put("mind_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of psychic energy and boosts the power of the holder’s Psychic-type moves."));
        item_lore.put("stone_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of rock and boosts the power of the holder’s Rock-type moves."));
        item_lore.put("iron_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of steel and boosts the power of the holder’s Steel-type moves."));
        item_lore.put("splash_plate", List.of("An item to be held by a Pokémon.", "This stone tablet is imbued with the essence of water and boosts the power of the holder’s Water-type moves."));
        item_lore.put("legend_plate", List.of("A stone tablet imbued with the essence of all creation.", "When used on a certain Pokémon, it allows that Pokémon to gain the power of every type there is."));
        item_lore.put("blank_plate", List.of("A stone tablet imbued with the essence of normalcy.", "When used on a certain Pokémon, it allows that Pokémon to gain the power of the Normal type."));

        item_lore.put("bug_memory", List.of("A memory disc that contains Bug-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("dark_memory", List.of("A memory disc that contains Dark-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("dragon_memory", List.of("A memory disc that contains Dragon-type data.", "It changes the type of the holder if held by a certain species of Pokémon"));
        item_lore.put("electric_memory", List.of("A memory disc that contains Electric-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("fairy_memory", List.of("A memory disc that contains Fairy-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("fighting_memory", List.of("A memory disc that contains Fighting-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("fire_memory", List.of("A memory disc that contains Fire-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("flying_memory", List.of("A memory disc that contains Flying-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("ghost_memory", List.of("A memory disc that contains Ghost-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("grass_memory", List.of("A memory disc that contains Grass-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("ground_memory", List.of("A memory disc that contains Ground-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("ice_memory", List.of("A memory disc that contains Ice-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("poison_memory", List.of("A memory disc that contains Poison-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("psychic_memory", List.of("A memory disc that contains Psychic-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("rock_memory", List.of("A memory disc that contains Rock-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("steel_memory", List.of("A memory disc that contains Steel-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("water_memory", List.of("A memory disc that contains Water-type data.", "It changes the type of the holder if held by a certain species of Pokémon."));

        item_lore.put("teal_mask", List.of("A teal mask patterned after the face of an ogre.", "Ogerpon dropped it while fleeing up the stairs after visiting the Festival of Masks."));
        item_lore.put("wellspring_mask", List.of("An item to be held by Ogerpon.", "This carved wooden mask is adorned with crystals and allows Ogerpon to wield the Water type during battle."));
        item_lore.put("hearthflame_mask", List.of("An item to be held by Ogerpon.", "This carved wooden mask is adorned with crystals and allows Ogerpon to wield the Fire type during battle."));
        item_lore.put("cornerstone_mask", List.of("An item to be held by Ogerpon.", "This carved wooden mask is adorned with crystals and allows Ogerpon to wield the Rock type during battle."));

        item_lore.put("red_orb", List.of("A shiny red orb that is said to have a deep connection to a legend of the Hoenn region."));
        item_lore.put("blue_orb", List.of("A shiny blue orb that is said to have a deep connection to a legend of the Hoenn region."));

        item_lore.put("shock_drive", List.of("A cassette to be held by Genesect.", "It changes Genesect's Techno Blast move so it becomes Electric type."));
        item_lore.put("burn_drive", List.of("A cassette to be held by Genesect.", "It changes Genesect's Techno Blast move so it becomes Fire type."));
        item_lore.put("chill_drive", List.of("A cassette to be held by Genesect.", "It changes Genesect's Techno Blast move so it becomes Ice type."));
        item_lore.put("douse_drive", List.of("A cassette to be held by Genesect.", "It changes Genesect's Techno Blast move so it becomes Water type."));

        item_lore.put("sparkling_stone", List.of("A stone entrusted to you by a Pokémon that has been venerated as a guardian deity in the Alola region.", "There is said to be some secret in how it sparkles."));
        item_lore.put("wishing_star", List.of("A stone found in the Galar region with a mysterious power.", "It's said that your dreams come true if you find one."));
        item_lore.put("dynamax_candy", List.of("A candy that is packed with energy.", "If consumed, it raises the Dynamax Level of a Pokémon by one.", "A higher level means higher HP when Dynamaxed."));
        item_lore.put("max_honey", List.of("Honey that Dynamax Vespiquen produces. Adding this honey to Max Soup makes the taste very smooth.", "It also has the same effect as a Max Revive."));
        item_lore.put("max_mushrooms", List.of("Mushrooms that have the power of changing Dynamax forms.", "They boost all stats of a Pokémon during battle."));
    }
}
