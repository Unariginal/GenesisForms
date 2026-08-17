package me.unariginal.genesisforms.config;

public class MessagesConfig {
    public String prefix = "<dark_gray>[<#35DEC6>Genesis<dark_gray>]";
    public TeraToastSettings teraToastSettings = new TeraToastSettings();
    public MessageSettings messages = new MessageSettings();

    public static class TeraToastSettings {
        public String toastTitle = "Tera Type: %pokemon.tera_type%";
        public String toastDescription = "";
        public boolean useShardIcon = true;
        public float displaySeconds = 3.0f;
    }

    public static class MessageSettings {
        public String reloadCommand = "%prefix% <green>Reloaded!";
        public String resetDataCommand = "%prefix% <green>Reset %player%'s Internal Form Data!";
        public String giveCommandReceived = "%prefix% <green>Received %item%!";
        public String giveCommandFeedback = "%prefix% <green>Gave %item% to %player%!";
        public String cubeModeFeedback = "<gray>Cube Mode: <green>%cube_mode%";
        public String gmaxFactorApplied = "<green>%pokemon% can now Gigantamax!";
        public String gmaxFactorRemoved = "<red>%pokemon% can no longer Gigantamax!";
        public String hasGmaxFactor = "<green>%pokemon% can Gigantamax!";
        public String doesNotHaveGmaxFactor = "<red>%pokemon% can not Gigantamax!";
        public String teraTypeChanged = "<green>Set %pokemon%'s tera type to %pokemon.tera_type%!";
        public String dynamaxLevelChanged = "<green>%pokemon%'s dynamax level is now %pokemon.dmax_level%!";
    }
}
