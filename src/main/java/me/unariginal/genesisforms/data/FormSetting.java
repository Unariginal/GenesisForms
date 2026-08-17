package me.unariginal.genesisforms.data;

import java.util.List;

public class FormSetting {
    public List<String> species;
    public String featureName;
    public String defaultValue;
    public String alternateValue;

    public FormSetting(List<String> species, String featureName, String defaultValue, String alternateValue) {
        this.species = species;
        this.featureName = featureName;
        this.defaultValue = defaultValue;
        this.alternateValue = alternateValue;
    }
}
