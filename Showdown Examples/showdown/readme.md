# Overview of Showdown Changes
Read the comments at the top of each file for more information

---
### `../showdown/data/items.js`
- Adds all Legends ZA Megastone items at the top of the items list
- Required for the new Megas to function in battle

---
### `../showdown/data/pokedex.js`
- Adds all Legends ZA Mega Evolution Species under their respective base species definitions
- Uses the base species' hidden ability as the mega evolution's ability. As no abilities exist in Legends ZA, I want to leave this up to each server to decide
- Floette and Zygarde are set up to mega evolve off of their Eternal and Complete forms, respectively
- Required for the new Megas to function in battle

---
### `../showdown/data/moves.js`
- Adds Mega Zygarde's signature move Nihil Light
- This full implementation of this move is slightly buggy, so use it with caution
- The change:
    ```js
    nihillight: {
        num: 920,
        accuracy: 100,
        basePower: 200,
        category: "Special",
        isNonstandard: "Future",
        name: "Nihil Light",
        pp: 10,
        priority: 0,
        flags: { protect: 1, mirror: 1, metronome: 1 },
        ignoreEvasion: true,
        ignoreDefensive: true,
        ignoreImmunity: { 'Fairy': true },
        secondary: null,
        target: "allAdjacentFoes",
        type: "Dragon"
    }
    ```
- This is an optional addition

---
### `../showdown/sim/battle-actions.js`
- This file is modified by Genesis Forms to include the method that enables Floette and Zygarde to mega evolve properly
  - This patch can be found in the `canMegaEvo()` method at line 1731
- The additional modifications not included in Genesis Forms are in the `runMegaEvo()` method which swaps out Zygarde's Core Enforcer move with Nihil Light when mega evolved
  - This patch can be found at line 1761
  - It is also slightly buggy
- The change:
    ```js
    if (speciesid === 'Zygarde-Mega') {
        const coreEnforcer = pokemon.moveSlots.findIndex(x => x.id === 'coreenforcer');
            if (coreEnforcer >= 0) {
                const nihilLight = this.battle.dex.moves.get('nihillight');
                pokemon.moveSlots[coreEnforcer] = pokemon.baseMoveSlots[coreEnforcer] = {
                id: nihilLight.id,
                move: nihilLight.name,
                pp: pokemon.moveSlots[coreEnforcer].pp,
                disabled: false,
                used: false
            };
        }
    }```
- This is an optional addition