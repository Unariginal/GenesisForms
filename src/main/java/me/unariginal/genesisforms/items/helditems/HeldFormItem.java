package me.unariginal.genesisforms.items.helditems;

import me.unariginal.genesisforms.data.FormSetting;

import java.util.List;

public record HeldFormItem(String itemID, String showdownID, FormSetting formSetting, List<String> itemLore) {}
