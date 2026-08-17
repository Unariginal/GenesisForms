package me.unariginal.genesisforms.config.items.helditems;

import me.unariginal.genesisforms.data.FormSetting;

import java.util.LinkedHashMap;
import java.util.List;

public class ZCrystalsConfig {
    public LinkedHashMap<String, TypedZCrystalData> typed = new LinkedHashMap<>();
    public LinkedHashMap<String, SpeciesZCrystalData> species = new LinkedHashMap<>();

    public static class TypedZCrystalData {
        public String showdownId;
        public String type;
        public List<FormSetting> formChanges;
        public List<String> lore;
    }

    public static class SpeciesZCrystalData {
        public String showdownId;
        public List<String> lore;
    }
}
