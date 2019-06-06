package jakojaannos.hcisland;

import jakojaannos.hcisland.init.ModLootTables;
import jakojaannos.hcisland.world.WorldTypeHCIsland;
import jakojaannos.hcisland.world.gen.adapter.BiomeSettingsAdapter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.RegistryBuilder;


@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES, acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class HardcoreIsland {
    @Instance
    public static HardcoreIsland instance;

    @SidedProxy(clientSide = "jakojaannos.hcisland.client.ClientProxy", serverSide = "jakojaannos.hcisland.CommonProxy")
    public static CommonProxy proxy;

    public HardcoreIsland() {
        new ModLootTables();
    }

    public static final WorldType WORLD_TYPE = new WorldTypeHCIsland();

    @EventHandler
    public void onInit(FMLPreInitializationEvent event) {
        ConfigManager.sync(ModInfo.MODID, Config.Type.INSTANCE);
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.onInit(event);
    }

    @SubscribeEvent
    public static void onNewRegistries(RegistryEvent.NewRegistry event) {
        new RegistryBuilder<BiomeSettingsAdapter>().setName(new ResourceLocation(ModInfo.MODID, "biome_adapters"))
                                                   .setType(BiomeSettingsAdapter.class)
                                                   .create();
    }
}
