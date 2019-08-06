package jakojaannos.hcisland.world.gen;

import jakojaannos.hcisland.world.WorldTypeHCIsland;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorHell;

import javax.annotation.Nullable;

@Log4j2
public class ChunkGeneratorHCIslandHell extends ChunkGeneratorHell {
    public ChunkGeneratorHCIslandHell(World worldIn) {
        super(worldIn, worldIn.getWorldInfo().isMapFeaturesEnabled(), worldIn.getSeed());
    }
/*
    @Override
    public void prepareHeights(int chunkX, int chunkZ, ChunkPrimer primer) {
        int hellSeaLevel = this.world.getSeaLevel() / 2 + 1;
        // 63 / 2 (=> 31.5) => 31
        // 31 + 1 => 32
        val bufferSizeXorZ = 5;
        int bufferSizeY = 17;
        this.buffer = this.getHeights(this.buffer,
                                      chunkX * 4,
                                      0,
                                      chunkZ * 4,
                                      bufferSizeXorZ,
                                      bufferSizeY,
                                      bufferSizeXorZ);

        // Chunks are generated in four horizontal 4x4 steps. This is probably to reduce number of height-map
        // queries? Or horizontal scaling trick? The height-map can be generated with lower res?
        for (int stepX = 0; stepX < 4; ++stepX) {
            for (int stepZ = 0; stepZ < 4; ++stepZ) {
                // Chunk is generated in 16 sections, although section height is 8 blocks instead of 16!
                for (int section = 0; section < 16; ++section) {
                    // Buffer is indexed as columns
                    int i00 = ((stepX) * bufferSizeXorZ + stepZ) * bufferSizeY;
                    int i01 = ((stepX) * bufferSizeXorZ + stepZ + 1) * bufferSizeY;
                    int i10 = ((stepX + 1) * bufferSizeXorZ + stepZ) * bufferSizeY;
                    int i11 = ((stepX + 1) * bufferSizeXorZ + stepZ + 1) * bufferSizeY;
                    double val00 = this.buffer[i00 + section];
                    double val01 = this.buffer[i01 + section];
                    double val10 = this.buffer[i10 + section];
                    double val11 = this.buffer[i11 + section];
                    val gradientStepY = 1.0D / 8.0D;
                    val gradientStepXorZ = 1.0D / 4.0D;
                    double valOneAbove00 = (this.buffer[i00 + section + 1] - val00) * gradientStepY;
                    double valOneAbove01 = (this.buffer[i01 + section + 1] - val01) * gradientStepY;
                    double valOneAbove10 = (this.buffer[i10 + section + 1] - val10) * gradientStepY;
                    double valOneAbove11 = (this.buffer[i11 + section + 1] - val11) * gradientStepY;

                    for (int iy = 0; iy < 8; ++iy) {
                        double yLocalVar00 = val00;
                        double yLocalVar01 = val01;
                        final double scaledDeltaXatZ0 = (val10 - val00) * gradientStepXorZ;
                        final double scaledDeltaXatZ1 = (val11 - val01) * gradientStepXorZ;

                        for (int ix = 0; ix < 4; ++ix) {
                            double densityAtCurrentPos = yLocalVar00;
                            double d16 = (yLocalVar01 - yLocalVar00) * gradientStepXorZ;

                            for (int iz = 0; iz < 4; ++iz) {
                                IBlockState iblockstate = null;

                                val blockY = iy + section * 8;
                                if (blockY < hellSeaLevel) {
                                    iblockstate = LAVA;
                                }

                                if (densityAtCurrentPos > 0.0) {
                                    iblockstate = NETHERRACK;
                                }

                                int blockX = ix + stepX * 4;
                                int blockZ = iz + stepZ * 4;
                                // May set state to null, which is a flag for surface building or sth.
                                primer.setBlockState(blockX, blockY, blockZ, iblockstate);
                                densityAtCurrentPos += d16;
                            }

                            // Make the values fade towards the value of the next horizontal step
                            yLocalVar00 += scaledDeltaXatZ0;
                            yLocalVar01 += scaledDeltaXatZ1;
                        }

                        // Make the values fade towards the value of the next vertical section
                        val00 += valOneAbove00;
                        val01 += valOneAbove01;
                        val10 += valOneAbove10;
                        val11 += valOneAbove11;
                    }
                }
            }
        }
    }
*/
    public double[] getHeights(
            @Nullable double[] buffer,
            int xOffset,
            int yOffset,
            int zOffset,
            int bufferSizeX,
            int bufferSizeY,
            int bufferSizeZ
    ) {
        if (buffer == null) {
            buffer = new double[bufferSizeX * bufferSizeY * bufferSizeZ];
        }

        net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField event = new net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField(this, buffer, xOffset, yOffset, zOffset, bufferSizeX, bufferSizeY, bufferSizeZ);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
            return event.getNoisefield();

        this.noiseData4 = this.scaleNoise.generateNoiseOctaves(this.noiseData4, xOffset, yOffset, zOffset, bufferSizeX, 1, bufferSizeZ, 1.0D, 0.0D, 1.0D);
        this.dr = this.depthNoise.generateNoiseOctaves(this.dr, xOffset, yOffset, zOffset, bufferSizeX, 1, bufferSizeZ, 100.0D, 0.0D, 100.0D);
        this.pnr = this.perlinNoise1.generateNoiseOctaves(this.pnr, xOffset, yOffset, zOffset, bufferSizeX, bufferSizeY, bufferSizeZ, 8.555150000000001D, 34.2206D, 8.555150000000001D);
        this.ar = this.lperlinNoise1.generateNoiseOctaves(this.ar, xOffset, yOffset, zOffset, bufferSizeX, bufferSizeY, bufferSizeZ, 684.412D, 2053.236D, 684.412D);
        this.br = this.lperlinNoise2.generateNoiseOctaves(this.br, xOffset, yOffset, zOffset, bufferSizeX, bufferSizeY, bufferSizeZ, 684.412D, 2053.236D, 684.412D);

        // Calculate density multipliers for y-level. This helps to create the layer-like structure of the nether
        double[] yLevelDensityModifiers = new double[bufferSizeY];
        for (int y = 0; y < bufferSizeY; ++y) {
            yLevelDensityModifiers[y] = Math.cos((double) y * Math.PI * 6.0D / (double) bufferSizeY) * 2.0D;
            double d2 = (double) y;

            // Invert at the middle
            if (y > bufferSizeY / 2) {
                d2 = (double) (bufferSizeY - 1 - y);
            }

            // For low values, adjust the value to achieve more distinct flat surfaces above the layers?
            if (d2 < 4.0D) {
                d2 = 4.0D - d2;
                yLevelDensityModifiers[y] -= d2 * d2 * d2 * 10.0D;
            }

            val densityYOffset = 30.0;
            yLevelDensityModifiers[y] *= MathHelper.clampedLerp(1.0, 0.0, (y - densityYOffset) / (bufferSizeY - densityYOffset - 1));
        }

        int i = 0;
        for (int x = 0; x < bufferSizeX; ++x) {
            for (int z = 0; z < bufferSizeZ; ++z) {
                double realX = (xOffset) / 4.0 + (double) x / bufferSizeX;
                double realZ = (zOffset) / 4.0 + (double) z / bufferSizeZ;
                double distanceFromOriginSq = (realX * realX) + (realZ * realZ);
                val settings = ((WorldTypeHCIsland) world.getWorldType()).getSettings();
                double fadeStart = settings.getTotalRadialZoneRadius();
                double fadeStartSq = fadeStart * fadeStart;

                for (int y = 0; y < bufferSizeY; ++y) {
                    double fadeEnd = settings.getTotalRadialZoneRadius() + 8 + (Math.max(y - 4, 0) * 2);
                    double fadeEndSq = fadeEnd * fadeEnd;

                    double yDensityModifier = yLevelDensityModifiers[y];
                    double d5 = this.ar[i] / 512.0D;
                    double d6 = this.br[i] / 512.0D;
                    double d7 = (this.pnr[i] / 10.0D + 1.0D) / 2.0D;
                    double density;

                    if (d7 < 0.0D) {
                        density = d5;
                    } else if (d7 > 1.0D) {
                        density = d6;
                    } else {
                        density = d5 + (d6 - d5) * d7;
                    }

                    density = density - yDensityModifier;

                    if (y > bufferSizeY - 4) {
                        double d9 = (double) ((float) (y - (bufferSizeY - 4)) / 3.0F);
                        density = density * (1.0D - d9) + -10.0D * d9;
                    }

                    if (distanceFromOriginSq > fadeStartSq && distanceFromOriginSq < fadeEndSq) {
                        double fadeT = (distanceFromOriginSq - (fadeStart * fadeStart)) / (fadeEndSq - fadeStartSq);
                        //yDensityModifier *= fadeT * ((double) y / (bufferSizeY - 1)) * 1000;
                        //yDensityModifier = 9999999;
                        //log.info("fadeT: {}", fadeT);

                        density = MathHelper.clampedLerp(-100.0, density, fadeT);
                    }

                    buffer[i] = density;
                    ++i;
                }
            }
        }

        return buffer;
    }

    @Override
    public void buildSurfaces(int p_185937_1_, int p_185937_2_, ChunkPrimer primer) {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, p_185937_1_, p_185937_2_, primer, this.world))
            return;
        int i = this.world.getSeaLevel() + 1;
        this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, p_185937_1_ * 16, p_185937_2_ * 16, 0, 16, 16, 1, 0.03125D, 0.03125D, 1.0D);
        this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, p_185937_1_ * 16, 109, p_185937_2_ * 16, 16, 1, 16, 0.03125D, 1.0D, 0.03125D);
        this.depthBuffer = this.netherrackExculsivityNoiseGen.generateNoiseOctaves(this.depthBuffer, p_185937_1_ * 16, p_185937_2_ * 16, 0, 16, 16, 1, 0.0625D, 0.0625D, 0.0625D);

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                boolean flag = this.slowsandNoise[j + k * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                boolean flag1 = this.gravelNoise[j + k * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                int l = (int) (this.depthBuffer[j + k * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int i1 = -1;
                IBlockState iblockstate = NETHERRACK;
                IBlockState iblockstate1 = NETHERRACK;

                for (int j1 = 127; j1 >= 0; --j1) {
                    if (j1 > this.rand.nextInt(5)) {
                        IBlockState iblockstate2 = primer.getBlockState(k, j1, j);

                        if (iblockstate2.getBlock() != null && iblockstate2.getMaterial() != Material.AIR) {
                            if (iblockstate2.getBlock() == Blocks.NETHERRACK) {
                                if (i1 == -1) {
                                    if (l <= 0) {
                                        iblockstate = AIR;
                                        iblockstate1 = NETHERRACK;
                                    } else if (j1 >= i - 4 && j1 <= i + 1) {
                                        iblockstate = NETHERRACK;
                                        iblockstate1 = NETHERRACK;

                                        if (flag1) {
                                            iblockstate = GRAVEL;
                                            iblockstate1 = NETHERRACK;
                                        }

                                        if (flag) {
                                            iblockstate = SOUL_SAND;
                                            iblockstate1 = SOUL_SAND;
                                        }
                                    }

                                    if (j1 < i && (iblockstate == null || iblockstate.getMaterial() == Material.AIR)) {
                                        iblockstate = LAVA;
                                    }

                                    i1 = l;

                                    if (j1 >= i - 1) {
                                        primer.setBlockState(k, j1, j, iblockstate);
                                    } else {
                                        primer.setBlockState(k, j1, j, iblockstate1);
                                    }
                                } else if (i1 > 0) {
                                    --i1;
                                    primer.setBlockState(k, j1, j, iblockstate1);
                                }
                            }
                        } else {
                            i1 = -1;
                        }
                    } else {
                        primer.setBlockState(k, j1, j, BEDROCK);
                    }
                }
            }
        }
    }
}
