package me.unariginal.genesisforms.config;

import com.google.gson.*;
import me.unariginal.genesisforms.GenesisForms;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemSettingsConfig {
    public Map<String, List<String>> item_lore = new HashMap<>();

    public ItemSettingsConfig() {
        item_lore.put("key_stone", List.of("A stone filled with an unexplained power.", "It makes Pokémon that battle with a Mega Stone Mega Evolve."));
        item_lore.put("z_ring", List.of("A mysterious ring that enables Pokémon to use Z-Power.", "It requires both the willpower and the physical power of the Trainer wearing it."));
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

        item_lore.put("wishing_star", List.of("A stone found in the Galar region with a mysterious power.", "It's said that your dreams come true if you find one."));
        item_lore.put("dynamax_candy", List.of("A candy that is packed with energy.", "If consumed, it raises the Dynamax Level of a Pokémon by one.", "A higher level means higher HP when Dynamaxed."));
        item_lore.put("max_honey", List.of("Honey that Dynamax Vespiquen produces. Adding this honey to Max Soup makes the taste very smooth.", "It also has the same effect as a Max Revive."));
        item_lore.put("max_mushrooms", List.of("Mushrooms that have the power of changing Dynamax forms.", "They boost all stats of a Pokémon during battle."));

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

        File itemSettingsFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/item_settings.json").toFile();
        if (itemSettingsFile.createNewFile()) {
            JsonObject root = new JsonObject();
            JsonObject item_lore = new JsonObject();
            for (String key : this.item_lore.keySet()) {
                List<String> lore = this.item_lore.get(key);
                JsonArray jsonLore = new JsonArray();
                for (String line : lore) {
                    jsonLore.add(line);
                }
                item_lore.add(key, jsonLore);
            }
            root.add("item_lore", item_lore);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = new FileWriter(itemSettingsFile);
            gson.toJson(root, writer);
            writer.close();
        } else {
            JsonObject root = JsonParser.parseReader(new FileReader(itemSettingsFile)).getAsJsonObject();
            JsonObject item_lore = root.getAsJsonObject("item_lore");
            for (String key : item_lore.keySet()) {
                JsonArray jsonLore = item_lore.getAsJsonArray(key);
                List<String> lore = new ArrayList<>();
                for (JsonElement line : jsonLore) {
                    lore.add(line.getAsString());
                }
                this.item_lore.put(key, lore);
            }
        }
    }
}
