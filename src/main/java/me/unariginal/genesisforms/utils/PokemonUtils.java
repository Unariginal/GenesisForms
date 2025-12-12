package me.unariginal.genesisforms.utils;

import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.pokemon.*;
import com.cobblemon.mod.common.util.DataKeys;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PokemonUtils {
    public static boolean playerOwnsPokemon(PlayerEntity player, Pokemon pokemon) {
        ServerPlayerEntity ownerPlayer = pokemon.getOwnerPlayer();
        if (ownerPlayer == null) return false;
        return ownerPlayer.getUuid().equals(player.getUuid());
    }

    public static NbtCompound saveToNBT(PokemonProperties properties) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT, properties.getOriginalString());
        if (properties.getLevel() != null) {
            nbt.putInt(DataKeys.POKEMON_LEVEL, properties.getLevel());
        }
        if (properties.getShiny() != null) {
            nbt.putBoolean(DataKeys.POKEMON_SHINY, properties.getShiny());
        }
        if (properties.getGender() != null) {
            nbt.putString(DataKeys.POKEMON_GENDER, properties.getGender().name());
        }
        if (properties.getSpecies() != null) {
            nbt.putString(DataKeys.POKEMON_SPECIES_TEXT, properties.getSpecies());
        }
        if (properties.getForm() != null) {
            nbt.putString(DataKeys.POKEMON_FORM_ID, properties.getForm());
        }
        if (properties.getFriendship() != null) {
            nbt.putInt(DataKeys.POKEMON_FRIENDSHIP, properties.getFriendship());
        }
        if (properties.getPokeball() != null) {
            nbt.putString(DataKeys.POKEMON_CAUGHT_BALL, properties.getPokeball());
        }
        if (properties.getNature() != null) {
            nbt.putString(DataKeys.POKEMON_NATURE, properties.getNature());
        }
        if (properties.getAbility() != null) {
            nbt.putString(DataKeys.POKEMON_ABILITY, properties.getAbility());
        }
        if (properties.getStatus() != null) {
            nbt.putString(DataKeys.POKEMON_STATUS_NAME, properties.getStatus());
        }
        if (properties.getIvs() != null) {
            nbt.put(DataKeys.POKEMON_IVS, IVs.getCODEC().encodeStart(NbtOps.INSTANCE, properties.getIvs()).result().get());
        }
        if (properties.getEvs() != null) {
            nbt.put(DataKeys.POKEMON_EVS, EVs.getCODEC().encodeStart(NbtOps.INSTANCE, properties.getEvs()).result().get());
        }
        if (properties.getType() != null) {
            nbt.putString(DataKeys.ELEMENTAL_TYPE, properties.getType());
        }
        if (properties.getTeraType() != null) {
            nbt.putString(DataKeys.POKEMON_TERA_TYPE, properties.getTeraType());
        }
        if (properties.getDmaxLevel() != null) {
            nbt.putInt(DataKeys.POKEMON_DMAX_LEVEL, properties.getDmaxLevel());
        }
        if (properties.getGmaxFactor() != null) {
            nbt.putBoolean(DataKeys.POKEMON_GMAX_FACTOR, properties.getGmaxFactor());
        }
        if (properties.getTradeable() != null) {
            nbt.putBoolean(DataKeys.POKEMON_TRADEABLE, properties.getTradeable());
        }
        if (properties.getOriginalTrainerType() != null) {
            nbt.putInt(DataKeys.POKEMON_ORIGINAL_TRAINER_TYPE, properties.getOriginalTrainerType().ordinal());
        }
        if (properties.getOriginalTrainer() != null) {
            nbt.putString(DataKeys.POKEMON_ORIGINAL_TRAINER, properties.getOriginalTrainer());
        }
        if (properties.getMoves() != null) {
            String moves = "";
            for (String move : properties.getMoves()) {
                moves = moves.concat(move + ",");
            }
            nbt.putString(DataKeys.POKEMON_PROPERTIES_MOVES, moves);
        }
        if (properties.getHeldItem() != null) {
            nbt.putString(DataKeys.POKEMON_PROPERTIES_HELDITEM, properties.getHeldItem());
        }
        NbtList custom = new NbtList();
        for (CustomPokemonProperty pokemonProperty : properties.getCustomProperties()) {
            custom.add(NbtString.of(pokemonProperty.asString()));
        }
        nbt.put(DataKeys.POKEMON_PROPERTIES_CUSTOM, custom);
        return nbt;
    }

    public static PokemonProperties loadFromNBT(NbtCompound nbt) {
        PokemonProperties properties = new PokemonProperties();
        properties.setOriginalString(nbt.getString(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT));
        if (nbt.contains(DataKeys.POKEMON_LEVEL)) {
            properties.setLevel(nbt.getInt(DataKeys.POKEMON_LEVEL));
        }
        if (nbt.contains(DataKeys.POKEMON_SHINY)) {
            properties.setShiny(nbt.getBoolean(DataKeys.POKEMON_SHINY));
        }
        if (nbt.contains(DataKeys.POKEMON_GENDER)) {
            properties.setGender(Gender.valueOf(nbt.getString(DataKeys.POKEMON_GENDER)));
        }
        if (nbt.contains(DataKeys.POKEMON_SPECIES_TEXT)) {
            properties.setSpecies(nbt.getString(DataKeys.POKEMON_SPECIES_TEXT));
        }
        if (nbt.contains(DataKeys.POKEMON_FORM_ID)) {
            properties.setForm(nbt.getString(DataKeys.POKEMON_FORM_ID));
        }
        if (nbt.contains(DataKeys.POKEMON_FRIENDSHIP)) {
            properties.setFriendship(nbt.getInt(DataKeys.POKEMON_FRIENDSHIP));
        }
        if (nbt.contains(DataKeys.POKEMON_CAUGHT_BALL)) {
            properties.setPokeball(nbt.getString(DataKeys.POKEMON_CAUGHT_BALL));
        }
        if (nbt.contains(DataKeys.POKEMON_NATURE)) {
            properties.setNature(nbt.getString(DataKeys.POKEMON_NATURE));
        }
        if (nbt.contains(DataKeys.POKEMON_ABILITY)) {
            properties.setAbility(nbt.getString(DataKeys.POKEMON_ABILITY));
        }
        if (nbt.contains(DataKeys.POKEMON_STATUS_NAME)) {
            properties.setStatus(nbt.getString(DataKeys.POKEMON_STATUS_NAME));
        }
        if (nbt.contains(DataKeys.POKEMON_IVS)) {
            properties.setIvs(IVs.getCODEC().decode(NbtOps.INSTANCE, nbt.getCompound(DataKeys.POKEMON_IVS)).result().get().getFirst());
        }
        if (nbt.contains(DataKeys.POKEMON_EVS)) {
            properties.setEvs(EVs.getCODEC().decode(NbtOps.INSTANCE, nbt.getCompound(DataKeys.POKEMON_EVS)).result().get().getFirst());
        }
        if (nbt.contains(DataKeys.ELEMENTAL_TYPE)) {
            properties.setType(nbt.getString(DataKeys.ELEMENTAL_TYPE));
        }
        if (nbt.contains(DataKeys.POKEMON_TERA_TYPE)) {
            properties.setTeraType(nbt.getString(DataKeys.POKEMON_TERA_TYPE));
        }
        if (nbt.contains(DataKeys.POKEMON_DMAX_LEVEL)) {
            properties.setDmaxLevel(nbt.getInt(DataKeys.POKEMON_DMAX_LEVEL));
        }
        if (nbt.contains(DataKeys.POKEMON_GMAX_FACTOR)) {
            properties.setGmaxFactor(nbt.getBoolean(DataKeys.POKEMON_GMAX_FACTOR));
        }
        if (nbt.contains(DataKeys.POKEMON_TRADEABLE)) {
            properties.setTradeable(nbt.getBoolean(DataKeys.POKEMON_TRADEABLE));
        }
        if (nbt.contains(DataKeys.POKEMON_ORIGINAL_TRAINER_TYPE)) {
            properties.setOriginalTrainerType(OriginalTrainerType.valueOf(nbt.getString(DataKeys.POKEMON_ORIGINAL_TRAINER_TYPE)));
        }
        if (nbt.contains(DataKeys.POKEMON_ORIGINAL_TRAINER)) {
            properties.setOriginalTrainer(nbt.getString(DataKeys.POKEMON_ORIGINAL_TRAINER));
        }
        if (nbt.contains(DataKeys.POKEMON_PROPERTIES_MOVES)) {
            properties.setMoves(Arrays.stream(nbt.getString(DataKeys.POKEMON_PROPERTIES_MOVES).split(",")).toList());
        }
        if (nbt.contains(DataKeys.POKEMON_PROPERTIES_HELDITEM)) {
            properties.setHeldItem(nbt.getString(DataKeys.POKEMON_PROPERTIES_HELDITEM));
        }
        if (nbt.contains(DataKeys.POKEMON_PROPERTIES_CUSTOM)) {
            NbtList custom = nbt.getList(DataKeys.POKEMON_PROPERTIES_CUSTOM, NbtCompound.STRING_TYPE);
            for (NbtElement element : custom) {
                properties.getCustomProperties().addAll(PokemonProperties.Companion.parse(element.asString()).getCustomProperties());
            }
        }
        properties.updateAspects();
        return properties;
    }

    public static void fixRotomMoves(Pokemon pokemon) {
        List<Move> newMoves = new ArrayList<>();
        for (Move move : pokemon.getMoveSet().getMoves()) {
            if (!(move.getTemplate().getName().equalsIgnoreCase("overheat") ||
                    move.getTemplate().getName().equalsIgnoreCase("hydropump") ||
                    move.getTemplate().getName().equalsIgnoreCase("blizzard") ||
                    move.getTemplate().getName().equalsIgnoreCase("airslash") ||
                    move.getTemplate().getName().equalsIgnoreCase("leafstorm"))) {
                newMoves.add(move);
            }
        }
        for (int i = 0; i < newMoves.size(); i++) {
            pokemon.getMoveSet().setMove(i, newMoves.get(i));
        }

        List<BenchedMove> newBenchedMoves = new ArrayList<>();
        for (BenchedMove move : pokemon.getBenchedMoves()) {
            if (!(move.getMoveTemplate().getName().equalsIgnoreCase("overheat") ||
                    move.getMoveTemplate().getName().equalsIgnoreCase("hydropump") ||
                    move.getMoveTemplate().getName().equalsIgnoreCase("blizzard") ||
                    move.getMoveTemplate().getName().equalsIgnoreCase("airslash") ||
                    move.getMoveTemplate().getName().equalsIgnoreCase("leafstorm"))) {
                newBenchedMoves.add(move);
            }
        }
        pokemon.getBenchedMoves().clear();
        pokemon.getBenchedMoves().addAll(newBenchedMoves);
    }

    public static void swapPokemonMove(Pokemon pokemon, String oldMove, String newMove) {
        MoveTemplate replacement = Moves.getByName(newMove);
        if (replacement != null) {
            List<Move> newMoves = new ArrayList<>();
            int slot = 0;
            int containsMoveSlot = -1;
            for (Move move : pokemon.getMoveSet().getMoves()) {
                if (!move.getTemplate().getName().equalsIgnoreCase(oldMove)) {
                    newMoves.add(move);
                } else {
                    containsMoveSlot = slot;
                }
                slot++;
            }
            for (int i = 0; i < newMoves.size(); i++) {
                pokemon.getMoveSet().setMove(i, newMoves.get(i));
            }
            if (containsMoveSlot != -1)
                pokemon.getMoveSet().setMove(containsMoveSlot, replacement.create(replacement.getMaxPp()));

            List<BenchedMove> newBenchedMoves = new ArrayList<>();
            boolean containsMoveBenched = false;
            for (BenchedMove move : pokemon.getBenchedMoves()) {
                if (!move.getMoveTemplate().getName().equalsIgnoreCase(oldMove)) {
                    newBenchedMoves.add(move);
                } else {
                    containsMoveBenched = true;
                }
            }
            pokemon.getBenchedMoves().clear();
            pokemon.getBenchedMoves().addAll(newBenchedMoves);
            if (containsMoveBenched) {
                pokemon.getBenchedMoves().add(new BenchedMove(replacement, 0));
            }
        }
    }
}
