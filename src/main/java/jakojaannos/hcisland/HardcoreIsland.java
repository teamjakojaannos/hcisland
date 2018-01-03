package jakojaannos.hcisland;

import jakojaannos.hcisland.init.HCIslandBiomes;
import jakojaannos.hcisland.init.HCIslandLootTables;
import jakojaannos.hcisland.world.WorldTypeHCIsland;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
public class HardcoreIsland {

    @Instance
    public static HardcoreIsland instance;

    @SidedProxy(clientSide = "jakojaannos.hcisland.client.ClientProxy", serverSide = "jakojaannos.hcisland.CommonProxy")
    public static CommonProxy proxy;

    public HardcoreIsland() {
        this.biomes = new HCIslandBiomes();
        this.loot = new HCIslandLootTables();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Content
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final WorldType WORLD_TYPE = new WorldTypeHCIsland();

    private final HCIslandBiomes biomes;
    private final HCIslandLootTables loot;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// FML Events
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @EventHandler
    public void onInit(FMLPreInitializationEvent event) {
        ConfigManager.sync(ModInfo.MODID, Config.Type.INSTANCE);
        biomes.initBiomes();
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.onInit(event);
    }
}
