package me.unariginal.genesisforms.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FormSetting {
    public List<String> species;
    @SerializedName(value = "feature_name", alternate = "featureName")
    public String featureName;
    @SerializedName(value = "default_value", alternate = "defaultValue")
    public String defaultValue;
    @SerializedName(value = "alternate_value", alternate = "alternateValue")
    public String alternateValue;

    public FormSetting(List<String> species, String featureName, String defaultValue, String alternateValue) {
        this.species = species;
        this.featureName = featureName;
        this.defaultValue = defaultValue;
        this.alternateValue = alternateValue;
    }

    @Override
    public String toString() {
        return "Species: " + species + "\nFeature: " + featureName + "\nDefault: " + defaultValue + "\nAlternate: " + alternateValue;
    }
}
