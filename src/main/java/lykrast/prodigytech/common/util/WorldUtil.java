package lykrast.prodigytech.common.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class WorldUtil {

    // Borrowed from Immersive Engineering
    // https://github.com/BluSunrize/ImmersiveEngineering/blob/1.13pre/src/main/java/blusunrize/immersiveengineering/common/util/Utils.java
    public static FluidStack drainFluidBlock(World world, BlockPos pos, boolean doDrain) {
        Block b = world.getBlockState(pos).getBlock();
        Fluid f = FluidRegistry.lookupFluidForBlock(b);

        if (f != null) {
            if (b instanceof IFluidBlock) {
                if (((IFluidBlock) b).canDrain(world, pos)) return ((IFluidBlock) b).drain(world, pos, doDrain);
                else return null;
            } else {
                if (b.getMetaFromState(world.getBlockState(pos)) == 0) {
                    if (doDrain) world.setBlockToAir(pos);
                    return new FluidStack(f, 1000);
                }
                return null;
            }
        }
        return null;
    }
}
