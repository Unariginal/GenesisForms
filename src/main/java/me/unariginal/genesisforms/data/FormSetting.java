package me.unariginal.genesisforms.data;

import java.util.List;

public record FormSetting(List<String> species, String featureName, String defaultValue, String alternateValue) {}
