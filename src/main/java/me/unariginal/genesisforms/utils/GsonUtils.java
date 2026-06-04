package me.unariginal.genesisforms.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.unariginal.genesisforms.data.event.ParticleEvent;
import me.unariginal.genesisforms.data.event.SnowstormEntityParticleEvent;
import me.unariginal.genesisforms.data.event.SnowstormParticleEvent;
import me.unariginal.genesisforms.data.event.VanillaParticleEvent;

public class GsonUtils {
    public static Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapterFactory(
                    RuntimeTypeAdapterFactory
                            .of(ParticleEvent.class, "type")
                            .registerSubtype(VanillaParticleEvent.class, "vanilla")
                            .registerSubtype(SnowstormParticleEvent.class, "snowstorm")
                            .registerSubtype(SnowstormEntityParticleEvent.class, "snowstorm_entity")
            )
            .create();
}
