package jakojaannos.hcisland.init;

import jakojaannos.hcisland.ModInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class ModLootTables {
    public static final ResourceLocation STARTING_GEAR = register("starter_chest");
    public static final ResourceLocation BONUS_GEAR = register("additional_chest");


    protected static ResourceLocation register(String name) {
        ResourceLocation resourceLocation = new ResourceLocation(ModInfo.MODID, name);
        LootTableList.register(resourceLocation);
        return resourceLocation;
    }
}
