package me.unariginal.genesisforms.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public boolean debug = false;
    public GeneralSettings generalSettings = new GeneralSettings();
    public MegaSettings megaSettings = new MegaSettings();
    public ZPowerSettings zPowerSettings = new ZPowerSettings();
    public TeraSettings teraSettings = new TeraSettings();
    public FusionSettings fusionSettings = new FusionSettings();
    public DynamaxSettings dynamaxSettings = new DynamaxSettings();

    public static class GeneralSettings {
        public KeyItemSlots keyItemSlots = new KeyItemSlots();
        public List<String> disabledItems = new ArrayList<>();

        public static class KeyItemSlots {
            public boolean hotbar = true;
            public boolean main = true;
            public boolean mainhand = true;
            public boolean offhand = true;
            public boolean armor = false;
            public List<Integer> specific = new ArrayList<>();
        }
    }

    public static class MegaSettings {
        public boolean enableMegaEvolution = true;
        public boolean allowMegaOutsideBattles = true;
        public boolean useTradeableProperty = false;
    }

    public static class ZPowerSettings {
        public boolean enableZCrystals = true;
    }

    public static class TeraSettings {
        public boolean enableTera = true;
        public int teraShardsRequired = 50;
        public boolean consumeTeraShards = true;
        public boolean requireOrbRecharge = true;
        public boolean fixOgerponTeraType = true;
        public boolean fixTerapagosTeraType = true;
    }

    public static class FusionSettings {
        public boolean enableFusions = true;
    }

    public static class DynamaxSettings {
        public boolean enableDynamax = true;
        public boolean enableGigantamax = true;
    }
}