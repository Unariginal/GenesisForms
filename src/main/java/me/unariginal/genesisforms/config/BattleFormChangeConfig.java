package me.unariginal.genesisforms.config;

import java.util.HashMap;
import java.util.Map;

public class BattleFormChangeConfig {
    public String species;
    public BattleForm defaultForm;
    public Map<String, BattleForm> forms = new HashMap<>();

    public static class BattleForm {
        public String featureName;
        public String featureValue;
    }
}
