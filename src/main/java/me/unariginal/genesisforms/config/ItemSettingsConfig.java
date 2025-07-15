package me.unariginal.genesisforms.config;

import com.cobblemon.mod.common.api.types.tera.TeraTypes;
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
    public boolean consumeTeraShards = true;
    public List<String> consumableBagItems = new ArrayList<>(List.of("dynamax_candy", "max_honey", "max_mushrooms", "max_soup"));
    public List<String> consumableKeyItems = new ArrayList<>(List.of("pink_nectar", "purple_nectar", "red_nectar", "yellow_nectar"));
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

        JsonObject consumable_items = new JsonObject();
        if (root.has("consumable_items")) {
            consumable_items = root.getAsJsonObject("consumable_items");
        }
        if (consumable_items.has("consume_tera_shards")) {
            consumeTeraShards = consumable_items.get("consume_tera_shards").getAsBoolean();
        }
        consumable_items.addProperty("consume_tera_shards", consumeTeraShards);

        if (consumable_items.has("bag_items")) {
            consumableBagItems.clear();
            JsonArray bag_items = consumable_items.get("bag_items").getAsJsonArray();
            for (JsonElement bag_item : bag_items) {
                String item = bag_item.getAsString();
                consumableBagItems.add(item);
            }
        }
        JsonArray bag_items = new JsonArray();
        for (String item : consumableBagItems) {
            bag_items.add(item);
        }
        consumable_items.add("bag_items", bag_items);

        if (consumable_items.has("key_items")) {
            consumableKeyItems.clear();
            JsonArray key_items = consumable_items.get("key_items").getAsJsonArray();
            for (JsonElement key_item : key_items) {
                String item = key_item.getAsString();
                consumableKeyItems.add(item);
            }
        }
        JsonArray key_items = new JsonArray();
        for (String item : consumableKeyItems) {
            key_items.add(item);
        }
        consumable_items.add("key_items", key_items);
        newRoot.add("consumable_items", consumable_items);

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
                if (item.has("alternative_feature_value")) {
                    alternate_feature_value = item.get("alternative_feature_value").getAsString();
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
        item_lore.put("key_stone", List.of("<gray>A stone filled with an unexplained power.", "<gray>It makes Pokémon that battle with a Mega Stone Mega Evolve."));
        item_lore.put("mega_bracelet", List.of("<gray>This bracelet contains an unknown power that somehow enables a Pokémon carrying a Mega Stone to Mega Evolve in battle."));
        item_lore.put("mega_charm", List.of("<gray>This charm contains an unknown power that somehow enables a Pokémon carrying a Mega Stone to Mega Evolve in battle."));
        item_lore.put("mega_cuff", List.of("<gray>This cuff contains an unknown power that somehow enables a Pokémon carrying a Mega Stone to Mega Evolve in battle."));
        item_lore.put("mega_ring", List.of("<gray>This ring contains an unknown power that somehow enables a Pokémon carrying a Mega Stone to Mega Evolve in battle."));

        item_lore.put("z_ring", List.of("<gray>A mysterious ring that enables Pokémon to use Z-Power.", "<gray>It requires both the willpower and the physical power of the Trainer wearing it."));
        item_lore.put("z_power_ring", List.of("<gray>A mysterious ring that enables Pokémon to use Z-Power.", "<gray>It requires both the willpower and the physical power of the Trainer wearing it."));

        item_lore.put("tera_orb", List.of("<gray>An orb that holds within it the power to crystallize.", "<gray>When it is charged with energy, it can be used to cause Pokémon to Terastallize."));

        item_lore.put("dynamax_band", List.of("<gray>A Wishing Star has been affixed to it.", "<gray>It lets out a light that allows Pokémon to Dynamax when at a Power Spot."));

        item_lore.put("adamant_crystal", List.of("<gray>An item to be held by Dialga.", "<gray>This large, glowing gem wells with power and allows the Pokémon to change form."));
        item_lore.put("griseous_core", List.of("<gray>An item to be held by Giratina.", "<gray>This large, glowing gem wells with power and allows the Pokémon to change form."));
        item_lore.put("lustrous_globe", List.of("<gray>An item to be held by Palkia.", "<gray>This large, glowing orb wells with power and allows the Pokémon to change form."));

        item_lore.put("gracidea_flower", List.of("<gray>A flower sometimes bundled into a bouquet to be given as an expression of gratitude on special occasions, such as birthdays and anniversaries."));
        item_lore.put("meteorite", List.of("<gray>A rock that fell to earth from space. It’s slightly warm to the touch.", "<gray>It allows a certain species of Pokémon to change forms."));
        item_lore.put("reveal_glass", List.of("<gray>A mysterious looking glass that reveals the truth.", "<gray>It can return a Pokémon to its original shape."));

        item_lore.put("n_lunarizer", List.of("<gray>A machine to fuse Necrozma, which needs light, and Lunala."));
        item_lore.put("n_solarizer", List.of("<gray>A machine to fuse Necrozma, which needs light, and Solgaleo."));
        item_lore.put("dna_splicers", List.of("<gray>A splicer that fuses Kyurem and a certain Pokémon.", "<gray>They are said to have originally been one."));
        item_lore.put("reins_of_unity", List.of("<gray>Reins that people presented to the king.", "<gray>They enhance Calyrex’s power over bountiful harvests and unite Calyrex with its beloved steed."));

        item_lore.put("prison_bottle", List.of("<gray>A bottle believed to have been used to seal away the power of a certain Pokémon long, long ago."));

        item_lore.put("pink_nectar", List.of("<gray>Flower nectar obtained from a pink flower.", "<gray>It changes the form of a certain species of Pokémon."));
        item_lore.put("purple_nectar", List.of("<gray>Flower nectar obtained from a purple flower.", "<gray>It changes the form of a certain species of Pokémon."));
        item_lore.put("yellow_nectar", List.of("<gray>Flower nectar obtained from a yellow flower.", "<gray>It changes the form of a certain species of Pokémon."));
        item_lore.put("red_nectar", List.of("<gray>Flower nectar obtained from a red flower.", "<gray>It changes the form of a certain species of Pokémon."));

        item_lore.put("zygarde_cube", List.of("<gray>An item in which Zygarde Cores and Cells are gathered.", "<gray>You can also use it to change Zygarde's forms."));

        item_lore.put("rusted_sword", List.of("<gray>It is said that a hero used this sword to halt a terrible disaster in ancient times.", "<gray>But it's grown rusty and worn."));
        item_lore.put("rusted_shield", List.of("<gray>It is said that a hero used this shield to halt a terrible disaster in ancient times.", "<gray>But it's grown rusty and worn."));

        item_lore.put("insect_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of bugs and boosts the power of the holder’s Bug-type moves."));
        item_lore.put("dread_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of darkness and boosts the power of the holder’s Dark-type moves."));
        item_lore.put("draco_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of dragons and boosts the power of the holder’s Dragon-type moves."));
        item_lore.put("zap_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of electricity and boosts the power of the holder’s Electric-type moves."));
        item_lore.put("pixie_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of fairies and boosts the power of the holder’s Fairy-type moves."));
        item_lore.put("fist_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of the fighting spirit and boosts the power of the holder’s Fighting-type moves."));
        item_lore.put("flame_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of fire and boosts the power of the holder’s Fire-type moves."));
        item_lore.put("sky_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of flight and boosts the power of the holder’s Flying-type moves."));
        item_lore.put("spooky_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of ghosts and boosts the power of the holder’s Ghost-type moves."));
        item_lore.put("meadow_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of plants and boosts the power of the holder’s Grass-type moves."));
        item_lore.put("earth_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of the earth and boosts the power of the holder’s Ground-type moves."));
        item_lore.put("icicle_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of ice and boosts the power of the holder’s Ice-type moves."));
        item_lore.put("toxic_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of poison and boosts the power of the holder’s Poison-type moves."));
        item_lore.put("mind_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of psychic energy and boosts the power of the holder’s Psychic-type moves."));
        item_lore.put("stone_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of rock and boosts the power of the holder’s Rock-type moves."));
        item_lore.put("iron_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of steel and boosts the power of the holder’s Steel-type moves."));
        item_lore.put("splash_plate", List.of("<gray>An item to be held by a Pokémon.", "<gray>This stone tablet is imbued with the essence of water and boosts the power of the holder’s Water-type moves."));
        item_lore.put("legend_plate", List.of("<gray>A stone tablet imbued with the essence of all creation.", "<gray>When used on a certain Pokémon, it allows that Pokémon to gain the power of every type there is."));
        item_lore.put("blank_plate", List.of("<gray>A stone tablet imbued with the essence of normalcy.", "<gray>When used on a certain Pokémon, it allows that Pokémon to gain the power of the Normal type."));

        item_lore.put("bug_memory", List.of("<gray>A memory disc that contains Bug-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("dark_memory", List.of("<gray>A memory disc that contains Dark-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("dragon_memory", List.of("<gray>A memory disc that contains Dragon-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon"));
        item_lore.put("electric_memory", List.of("<gray>A memory disc that contains Electric-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("fairy_memory", List.of("<gray>A memory disc that contains Fairy-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("fighting_memory", List.of("<gray>A memory disc that contains Fighting-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("fire_memory", List.of("<gray>A memory disc that contains Fire-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("flying_memory", List.of("<gray>A memory disc that contains Flying-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("ghost_memory", List.of("<gray>A memory disc that contains Ghost-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("grass_memory", List.of("<gray>A memory disc that contains Grass-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("ground_memory", List.of("<gray>A memory disc that contains Ground-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("ice_memory", List.of("<gray>A memory disc that contains Ice-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("poison_memory", List.of("<gray>A memory disc that contains Poison-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("psychic_memory", List.of("<gray>A memory disc that contains Psychic-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("rock_memory", List.of("<gray>A memory disc that contains Rock-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("steel_memory", List.of("<gray>A memory disc that contains Steel-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));
        item_lore.put("water_memory", List.of("<gray>A memory disc that contains Water-type data.", "<gray>It changes the type of the holder if held by a certain species of Pokémon."));

        item_lore.put("teal_mask", List.of("<gray>A teal mask patterned after the face of an ogre.", "<gray>Ogerpon dropped it while fleeing up the stairs after visiting the Festival of Masks."));
        item_lore.put("wellspring_mask", List.of("<gray>An item to be held by Ogerpon.", "<gray>This carved wooden mask is adorned with crystals and allows Ogerpon to wield the Water type during battle."));
        item_lore.put("hearthflame_mask", List.of("<gray>An item to be held by Ogerpon.", "<gray>This carved wooden mask is adorned with crystals and allows Ogerpon to wield the Fire type during battle."));
        item_lore.put("cornerstone_mask", List.of("<gray>An item to be held by Ogerpon.", "<gray>This carved wooden mask is adorned with crystals and allows Ogerpon to wield the Rock type during battle."));

        item_lore.put("red_orb", List.of("<gray>A shiny red orb that is said to have a deep connection to a legend of the Hoenn region."));
        item_lore.put("blue_orb", List.of("<gray>A shiny blue orb that is said to have a deep connection to a legend of the Hoenn region."));

        item_lore.put("shock_drive", List.of("<gray>A cassette to be held by Genesect.", "<gray>It changes Genesect's Techno Blast move so it becomes Electric type."));
        item_lore.put("burn_drive", List.of("<gray>A cassette to be held by Genesect.", "<gray>It changes Genesect's Techno Blast move so it becomes Fire type."));
        item_lore.put("chill_drive", List.of("<gray>A cassette to be held by Genesect.", "<gray>It changes Genesect's Techno Blast move so it becomes Ice type."));
        item_lore.put("douse_drive", List.of("<gray>A cassette to be held by Genesect.", "<gray>It changes Genesect's Techno Blast move so it becomes Water type."));

        item_lore.put("sparkling_stone", List.of("<gray>A stone entrusted to you by a Pokémon that has been venerated as a guardian deity in the Alola region.", "<gray>There is said to be some secret in how it sparkles."));
        item_lore.put("wishing_star", List.of("<gray>A stone found in the Galar region with a mysterious power.", "<gray>It's said that your dreams come true if you find one."));
        item_lore.put("dynamax_candy", List.of("<gray>A candy that is packed with energy.", "<gray>If consumed, it raises the Dynamax Level of a Pokémon by one.", "<gray>A higher level means higher HP when Dynamaxed."));
        item_lore.put("max_honey", List.of("<gray>Honey that Dynamax Vespiquen produces. Adding this honey to Max Soup makes the taste very smooth.", "<gray>It also has the same effect as a Max Revive."));
        item_lore.put("max_mushrooms", List.of("<gray>Mushrooms that have the power of changing Dynamax forms.", "<gray>They boost all stats of a Pokémon during battle."));
        item_lore.put("max_soup", List.of("<gray>The soup can be fed to any Pokémon that has a Gigantamax form, which either adds or removes the Gigantamax Factor."));

        item_lore.put("venusaurite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Venusaur hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("charizardite-x", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Charizard hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("charizardite-y", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Charizard hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("blastoisinite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Blastoise hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("alakazite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Alakazam hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("gengarite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Gengar hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("kangaskhanite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Kangaskhan hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("pinsirite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Pinsir hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("gyaradosite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Gyarados hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("aerodactylite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Aerodactyl hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("mewtwonite-x", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Mewtwo hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("mewtwonite-y", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Mewtwo hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("ampharosite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Ampharos hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("scizorite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Scizor hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("heracronite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Hercross hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("houndoominite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Houndoom hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("tyranitarite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Tyranitar hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("blazikenite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Blaziken hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("gardevoirite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Gardevoir hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("mawilite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Mawile hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("aggronite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Aggron hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("medichamite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Medicham hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("manectite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Manectite hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("banettite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Banette hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("absolite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Absol hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("latiasite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Latias hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("latiosite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Latios hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("garchompite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Garchomp hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("lucarionite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Lucario hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("abomasite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Abomasnow hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("beedrillite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Beedrill hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("pidgeotite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Pidgeot hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("slowbronite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Slowbro hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("steelixite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Steelix hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("sceptilite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Sceptile hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("swampertite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Swampert hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("sablenite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Sableye hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("sharpedonite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Sharpedo hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("cameruptite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Camerupt hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("altarianite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Altaria hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("glalitite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Glalie hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("salamencite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Salamence hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("metagrossite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Metagross hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("lopunnite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Lopunny hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("galladite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Gallade hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("audinite", List.of("<gray>One of a variety of mysterious Mega Stones.", "<gray>Have Audino hold it, and this stone will enable it to Mega Evolve during battle."));
        item_lore.put("diancite", List.of("<gray><gray>One of a variety of mysterious Mega Stones.", "<gray>Have Diancie hold it, and this stone will enable it to Mega Evolve during battle."));

        item_lore.put("buginium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Bug-type moves to Z-Moves."));
        item_lore.put("darkinium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Dark-type moves to Z-Moves."));
        item_lore.put("dragonium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Dragon-type moves to Z-Moves."));
        item_lore.put("electrium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Electric-type moves to Z-Moves."));
        item_lore.put("fairium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Fairy-type moves to Z-Moves."));
        item_lore.put("fightinium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Fighting-type moves to Z-Moves."));
        item_lore.put("firium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Fire-type moves to Z-Moves."));
        item_lore.put("flyinium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Flying-type moves to Z-Moves."));
        item_lore.put("ghostium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Ghost-type moves to Z-Moves."));
        item_lore.put("grassium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Grass-type moves to Z-Moves."));
        item_lore.put("groundium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Ground-type moves to Z-Moves."));
        item_lore.put("icium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Ice-type moves to Z-Moves."));
        item_lore.put("normalium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Normal-type moves to Z-Moves."));
        item_lore.put("poisonium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Poison-type moves to Z-Moves."));
        item_lore.put("psychium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Psychic-type moves to Z-Moves."));
        item_lore.put("rockium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Rock-type moves to Z-Moves."));
        item_lore.put("steelium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Steel-type moves to Z-Moves."));
        item_lore.put("waterium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Water-type moves to Z-Moves."));

        item_lore.put("aloraichium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Alolan Raichu's Thunderbolt to a Z-Move."));
        item_lore.put("decidium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Decidueye's Spirit Shackle to a Z-Move."));
        item_lore.put("eevium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Eevee's Last Resort to a Z-Move."));
        item_lore.put("incinium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Incineroar's Darkest Lariat to a Z-Move."));
        item_lore.put("kommonium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Kommo-o's Clanging Scales to a Z-Move."));
        item_lore.put("lunalium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Lunala's Moongeist Beam to a Z-Move."));
        item_lore.put("lycanium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Lycanroc's Stone Edge to a Z-Move."));
        item_lore.put("marshadium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Marshadow's Spectral Thief to a Z-Move."));
        item_lore.put("mewnium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Mew's Psychic to a Z-Move."));
        item_lore.put("mimikium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Mimikyu's Play Rough to a Z-Move."));
        item_lore.put("pikanium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Pikachu's Volt Tackle to a Z-Move."));
        item_lore.put("pikashunium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Thunderbolt to a Z-Move when used by a Pikachu wearing a cap."));
        item_lore.put("primarium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Primarina's Sparkling Aria to a Z-Move."));
        item_lore.put("snorlium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Snorlax's Giga Impact to a Z-Move."));
        item_lore.put("solganium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Solgaleo's Sunsteel Strike to a Z-Move."));
        item_lore.put("tapunium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades the tapu's Nature's Madness to a Z-Move."));
        item_lore.put("ultranecrozium-z", List.of("<gray>This is a crystallized form of Z-Power.", "<gray>It upgrades Necrozma's Photon Geyser to a Z-Move."));

        item_lore.put("normal_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("fire_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("water_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("electric_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("grass_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("ice_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("fighting_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("poison_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("ground_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("flying_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("psychic_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("bug_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("rock_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("ghost_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("dragon_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("dark_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("steel_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("fairy_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));
        item_lore.put("stellar_tera_shard", List.of("<gray>On rare occasions, these shards form when a Tera Pokémon falls in battle and its Tera Jewel shatters."));

        item_lore.put("soul_dew", List.of("<gray>A wondrous orb to be held by Latios or Latias.", "<gray>It boosts the power of their Psychic- and Dragon-type moves."));
        item_lore.put("lucky_punch", List.of("<gray>An item to be held by Chansey.", "<gray>This lucky boxing glove boosts the critical-hit ratio of Chansey's moves."));
        item_lore.put("berserk_gene", List.of("<gray>Boosts Attack but causes confusion."));
        item_lore.put("adamant_orb", List.of("<gray>A brightly shining gem to be held by Dialga.", "<gray>It boosts the power of Dialga’s Dragon- and Steel-type moves."));
        item_lore.put("lustrous_orb", List.of("<gray>A beautifully shining gem to be held by Palkia.", "<gray>It boosts the power of Palkia’s Dragon- and Water-type moves."));
        item_lore.put("griseous_orb", List.of("<gray>A shining gem to be held by Giratina.", "<gray>It boosts the power of Giratina's Dragon- and Ghost-type moves."));
        item_lore.put("adrenaline_orb", List.of("<gray>An item to be held by a Pokémon.", "<gray>This orb boosts the Speed stat if the holder is intimidated."));
        item_lore.put("booster_energy", List.of("<gray>An item to be held by Pokémon with certain Abilities.", "<gray>The energy that fills this capsule boosts the strength of the Pokémon."));

        item_lore.put("rotom_microwave_oven", List.of("<gray>This item can be possessed by a certain pokemon!"));
        item_lore.put("rotom_lawn_mower", List.of("<gray>This item can be possessed by a certain pokemon!"));
        item_lore.put("rotom_washing_machine", List.of("<gray>This item can be possessed by a certain pokemon!"));
        item_lore.put("rotom_fan", List.of("<gray>This item can be possessed by a certain pokemon!"));
        item_lore.put("rotom_light_bulb", List.of("<gray>This item can be possessed by a certain pokemon!"));
        item_lore.put("rotom_refrigerator", List.of("<gray>This item can be possessed by a certain pokemon!"));
    }
}