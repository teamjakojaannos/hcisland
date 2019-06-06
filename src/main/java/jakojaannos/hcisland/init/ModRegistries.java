package jakojaannos.hcisland.init;

import jakojaannos.hcisland.world.gen.adapter.BiomeSettingsAdapter;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

@Mod.EventBusSubscriber
public class ModRegistries {
    public static IForgeRegistry<BiomeSettingsAdapter> BIOME_ADAPTERS = RegistryManager.ACTIVE.getRegistry(BiomeSettingsAdapter.class);
}
