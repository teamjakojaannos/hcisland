package jakojaannos.hcisland.util.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.adapter.BiomeSettingsAdapter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates type adapters for maps containing biome settings. Map key is assumed to be a biome ID and is used to
 * query {@link ModRegistries#BIOME_ADAPTERS registry} for a {@link BiomeSettingsAdapter} in order to know which
 * {@link BiomeSettings} subclass to use.
 *
 * @see BiomeSettings
 * @see BiomeSettingsAdapter
 * @see ModRegistries#BIOME_ADAPTERS
 */
@Log4j2
public class BiomeSettingsMapTypeAdapterFactory implements TypeAdapterFactory {
    private static final TypeToken<Map<ResourceLocation, BiomeSettings>> MAP_TYPE_TOKEN = new TypeToken<Map<ResourceLocation, BiomeSettings>>() {};

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (!MAP_TYPE_TOKEN.equals(typeToken)) {
            return null;
        }

        return (TypeAdapter<T>) newBiomeSettingsMapAdapter(gson);
    }

    private TypeAdapter<Map<ResourceLocation, BiomeSettings>> newBiomeSettingsMapAdapter(Gson gson) {
        return new TypeAdapter<Map<ResourceLocation, BiomeSettings>>() {
            @Override
            public void write(JsonWriter out, Map<ResourceLocation, BiomeSettings> value) throws IOException {
                out.beginObject();

                for (val entry : value.entrySet()) {
                    val id = entry.getKey();
                    val adapter = ModRegistries.BIOME_ADAPTERS.getValue(id);
                    if (adapter == null) {
                        log.warn("Could not find biome settings adapter for biome with ID=\"{}\"", id);
                        continue;
                    }
                    TypeAdapter valueAdapter = gson.getAdapter(adapter.getSettingsType());

                    out.name(entry.getKey().toString());
                    valueAdapter.write(out, entry.getValue());
                }

                out.endObject();
            }

            @Nullable
            @Override
            public Map<ResourceLocation, BiomeSettings> read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    return null;
                }

                in.beginObject();
                val result = new HashMap<ResourceLocation, BiomeSettings>();
                while (in.hasNext()) {
                    val id = new ResourceLocation(in.nextName());
                    val adapter = ModRegistries.BIOME_ADAPTERS.getValue(id);
                    if (adapter == null) {
                        log.warn("Could not find biome settings adapter for biome with ID=\"{}\"", id);
                        continue;
                    }
                    TypeAdapter valueAdapter = gson.getAdapter(adapter.getSettingsType());

                    BiomeSettings biomeSettings = (BiomeSettings) valueAdapter.read(in);
                    result.put(id, biomeSettings);
                }
                in.endObject();

                return result;
            }
        };
    }
}
