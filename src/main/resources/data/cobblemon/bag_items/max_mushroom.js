{
    use(battle, pokemon, itemId, data) {
        console.log(data);
        var boosts = {};
        boosts["atk"] = 1;
        boosts["def"] = 1;
        boosts["spa"] = 1;
        boosts["spd"] = 1;
        boosts["spe"] = 1;
        battle.boost(boosts, pokemon, null, {effectType: 'BagItem', name: itemId});
    }
}