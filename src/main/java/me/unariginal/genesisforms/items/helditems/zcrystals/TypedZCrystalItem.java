package me.unariginal.genesisforms.items.helditems.zcrystals;

import me.unariginal.genesisforms.data.FormSetting;

import java.util.List;

public record TypedZCrystalItem(String itemID, String showdownID, String type, List<FormSetting> formChanges, List<String> itemLore) {}
