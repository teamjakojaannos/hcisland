package jakojaannos.hcisland.world;

import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorld;
import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.util.world.gen.GeneratorSettingsHelper;
import jakojaannos.hcisland.world.biome.BiomeProviderHCIsland;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Log4j2
public class WorldTypeHCIsland extends WorldType {
    private static final String NAME = "hcisland";

    @Getter private HCIslandChunkGeneratorSettings settings;

    public WorldTypeHCIsland() {
        super(NAME);
        enableInfoNotice();
    }

    @Override
    public boolean isCustomizable() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onCustomizeButton(
            net.minecraft.client.Minecraft mc,
            net.minecraft.client.gui.GuiCreateWorld guiCreateWorld
    ) {
        mc.displayGuiScreen(new GuiCustomizeHCWorld(guiCreateWorld,
                                                    guiCreateWorld.chunkProviderSettingsJson,
                                                    s -> guiCreateWorld.chunkProviderSettingsJson = GeneratorSettingsHelper.toJson(s)));
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        // APPLY ONLY FOR DIMENSION 0 AS SOME PEOPLE DO NOT OVERRIDE BIOME PROVIDERS FOR THEIR CUSTOM DIMENSIONS,
        // RESULTING IN SITUATIONS WHERE THIS METHOD IS CALLED WITH EMPTY GENERATOR OPTIONS, CLEARING SETTINGS FOR
        // OVERWORLD
        if (world.provider.getDimension() != 0) {
            return super.getBiomeProvider(world);
        }

        this.settings = GeneratorSettingsHelper.fromJson(world.getWorldInfo().getGeneratorOptions());

        // HACK: Apply only on server as applying on client world in SP results in settings being cleared as client
        //       world does not have generator settings
        if (!world.isRemote) {
            log.info("Applying per-world biome settings via biome adapters...");
            ModRegistries.BIOME_ADAPTERS.getValuesCollection()
                                        .forEach(a -> {
                                            val name = a.getBiome().getRegistryName();
                                            log.debug("-> Modifying biome: {}", name);
                                            a.applyBiomeSettings(settings.getSettingsFor(name));
                                        });
        }

        return new BiomeProviderHCIsland(world.getWorldInfo(), settings);
    }
}
