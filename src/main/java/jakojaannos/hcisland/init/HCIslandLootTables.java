package jakojaannos.hcisland.init;

import jakojaannos.api.mod.LootTablesBase;
import net.minecraft.util.ResourceLocation;

public class HCIslandLootTables extends LootTablesBase {
    public static final ResourceLocation STARTING_GEAR = register("hcisland", "starter_chest");
    public static final ResourceLocation BONUS_GEAR = register("hcisland", "additional_chest");
}
