package jakojaannos.hcisland.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.val;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockStateTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!IBlockState.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return (TypeAdapter<T>) new BlockStateTypeAdapter();
    }

    private static class BlockStateTypeAdapter extends TypeAdapter<IBlockState> {
        @Override
        public void write(JsonWriter out, IBlockState blockState) throws IOException {
            out.beginObject();
            out.name("block").value(blockState.getBlock().getRegistryName().toString());
            out.name("properties").beginObject();
            for (val entry : blockState.getProperties().entrySet()) {
                IProperty property = entry.getKey();
                val value = entry.getValue();

                out.name(property.getName()).value(property.getName(value));
            }
            out.endObject();
            out.endObject();
        }

        @Override
        public IBlockState read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            in.beginObject();
            String blockRegistryName = "";
            Map<String, String> propertyStrings = new HashMap<>();
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "block":
                        blockRegistryName = in.nextString();
                        break;
                    case "properties":
                        in.beginObject();
                        while (in.hasNext()) {
                            val key = in.nextName();
                            val value = in.nextString();
                            propertyStrings.put(key, value);
                        }
                        in.endObject();
                        break;
                }
            }
            in.endObject();

            val block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockRegistryName));
            if (block == null) {
                throw new JsonParseException("Invalid block registry name: " + blockRegistryName);
            }

            val fullState = block.getBlockState();
            IBlockState finalState = block.getDefaultState();
            for (IProperty property : fullState.getProperties()) {
                val name = property.getName();
                if (propertyStrings.containsKey(name)) {
                    Optional<? extends Comparable> value = property.parseValue(propertyStrings.get(name)).toJavaUtil();
                    if (value.isPresent()) {
                        finalState = finalState.withProperty(property, value.get());
                    }
                }
            }

            return finalState;
        }
    }
}