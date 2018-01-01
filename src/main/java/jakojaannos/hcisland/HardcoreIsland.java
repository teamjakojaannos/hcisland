package jakojaannos.hcisland;

import jakojaannos.api.mod.CommandsBase;
import jakojaannos.api.mod.ItemsBase;
import jakojaannos.api.mod.ModMainBase;
import jakojaannos.hcisland.event.handler.WorldGenEventHandler;
import jakojaannos.hcisland.init.HCIslandBiomes;
import jakojaannos.hcisland.init.HCIslandBlocks;
import jakojaannos.hcisland.init.HCIslandLootTables;
import jakojaannos.hcisland.world.WorldTypeHCIsland;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
public class HardcoreIsland extends ModMainBase<HCIslandBlocks, ItemsBase, HCIslandBiomes, CommandsBase, HCIslandLootTables> {

    @SidedProxy(clientSide = "jakojaannos.hcisland.client.ClientProxy", serverSide = "jakojaannos.hcisland.CommonProxy")
    public static CommonProxy proxy;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Misc Content
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final WorldType WORLD_TYPE = new WorldTypeHCIsland();


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// FML Events
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    @Override
    @Mod.EventHandler
    public void onInit(FMLPreInitializationEvent event) {
        ConfigManager.sync(ModInfo.MODID, Config.Type.INSTANCE);

        proxy.onInit(event);

        MinecraftForge.TERRAIN_GEN_BUS.register(WorldGenEventHandler.class);
        super.onInit(event);
    }

    @Override
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    @Mod.EventHandler
    public void onInit(FMLPostInitializationEvent event) {

    }
}
