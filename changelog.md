# Genesis Forms v1.1

## Additions:
- New Held Items:
  - Adamant Orb
  - Adrenaline Orb
  - Berserk Gene
  - Booster Energy
  - Griseous Orb
  - Lucky Punch
  - Lustrous Orb
  - Macho Brace
  - Soul Dew
- New Key Items
  - Rotom Catalog
  - Rotom Lawn Mower Block
  - Rotom Fan Block
  - Rotom Microwave Oven Block
  - Rotom Refrigerator Block
  - Rotom Washing Machine Block
  - Rotom Light Bulb Block
- All items are now defined in various config files, so you can now add, remove, and change item functionality
- Battle forms are now defined in `battle_forms.json`
- Gimmick "Events" are now fully customizable in `events.json`. This includes particle animations, feature changes, glow color, and scaling
- `config.json` now contains a `tera_shards_required` option
- `config.json` now contains a `require_orb_recharge` option. Tera orbs will be recharged when you heal pokemon at a healing machine.

## Removed:
- `animations.json` is no longer used, use `events.json` instead
- `item_settings.json` is no longer used, all lore and items settings are defined in the various item files
