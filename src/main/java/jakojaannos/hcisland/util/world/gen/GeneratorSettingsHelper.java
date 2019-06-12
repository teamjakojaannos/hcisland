package jakojaannos.hcisland.util.world.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.util.json.BiomeSettingsMapTypeAdapterFactory;
import jakojaannos.hcisland.util.json.BlockStateTypeAdapterFactory;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.util.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class GeneratorSettingsHelper {
    private static final Gson JSON_ADAPTER = new GsonBuilder().setLenient()
                                                              .registerTypeAdapterFactory(new BlockStateTypeAdapterFactory())
                                                              .registerTypeAdapterFactory(new BiomeSettingsMapTypeAdapterFactory())
                                                              .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
                                                              .create();

    public static HCIslandChunkGeneratorSettings createOverriddenDefaults() {
        return fromJson(getOverrideJson());
    }

    public static HCIslandChunkGeneratorSettings fromJson(String json) {
        if (json.trim().isEmpty()) {
            return new HCIslandChunkGeneratorSettings();
        }

        try {
            val instance = JSON_ADAPTER.fromJson(json, HCIslandChunkGeneratorSettings.class);
            if (instance == null) {
                log.error("Passed JSON did not contain valid generator settings, falling back to default settings.");
                return new HCIslandChunkGeneratorSettings();
            }

            return instance;
        } catch (JsonParseException e) {
            log.error("Reading generator settings from JSON failed, falling back to default settings.", e);
            return new HCIslandChunkGeneratorSettings();
        }
    }

    private static String getOverrideJson() {
        return HCIslandConfig.world.generatorSettingsDefaults.trim();
    }

    public static String toJson(HCIslandChunkGeneratorSettings settings) {
        return JSON_ADAPTER.toJson(settings, HCIslandChunkGeneratorSettings.class);
    }
}
