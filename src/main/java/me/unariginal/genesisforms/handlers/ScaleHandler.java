package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ScaleHandler {
    public static final Map<PokemonEntity, ScalingData> scalingAnimations = new HashMap<>();

    public static class ScalingData {
        final float startScale;
        final float targetScale;
        final long duration;
        int currentTick;

        public ScalingData(float startScale, float targetScale, long duration) {
            this.startScale = startScale;
            this.targetScale = targetScale;
            this.duration = duration;
            this.currentTick = 0;
        }
    }

    public static void updateScales() {
        Iterator<Map.Entry<PokemonEntity, ScalingData>> iterator = scalingAnimations.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<PokemonEntity, ScalingData> entry = iterator.next();

            PokemonEntity entity = entry.getKey();
            ScalingData data = entry.getValue();

            data.currentTick++;

            if (entity != null && !entity.isRemoved()) {
                EntityAttributeInstance scaleAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                if (scaleAttribute != null) {
                    float progress = Math.min(1.0f, (float) data.currentTick / data.duration);
                    float newScale = data.startScale + (data.targetScale - data.startScale) * progress;

                    scaleAttribute.setBaseValue(newScale);
                }

                if (data.currentTick >= data.duration) {
                    iterator.remove();
                }
            } else {
                iterator.remove();
            }
        }
    }
}
