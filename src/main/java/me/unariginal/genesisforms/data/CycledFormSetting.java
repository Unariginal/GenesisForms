package me.unariginal.genesisforms.data;

import java.util.LinkedList;
import java.util.List;

public record CycledFormSetting(List<String> species, String featureName, LinkedList<String> featureValues) {
}
