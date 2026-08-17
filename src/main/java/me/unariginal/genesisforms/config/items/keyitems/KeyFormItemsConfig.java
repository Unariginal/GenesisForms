package me.unariginal.genesisforms.config.items.keyitems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KeyFormItemsConfig {
    public boolean consumable = false;
    public int maxStackSize = 1;
    public List<String> species = new ArrayList<>();
    public String featureName;
    public LinkedList<String> featureValues = new LinkedList<>();
    public List<String> lore = new ArrayList<>();
}
