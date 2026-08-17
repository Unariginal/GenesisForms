package me.unariginal.genesisforms.config.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscItemsConfig {
    public ZygardeCubeData zygardeCube = new ZygardeCubeData();
    public Map<String, MiscItem> featureless = new HashMap<>();

    public static class ZygardeCubeData {
        public Boolean enableAbilitySwap = true;
        public Boolean enableFormSwap = true;
        public String featureName = "percent_cells";
        public List<String> featureValues = new ArrayList<>(List.of("10", "50"));
        public List<String> lore = new ArrayList<>();
    }

    public static class MiscItem {
        public Integer maxCount = 1;
        public List<String> lore = new ArrayList<>();
    }
}
