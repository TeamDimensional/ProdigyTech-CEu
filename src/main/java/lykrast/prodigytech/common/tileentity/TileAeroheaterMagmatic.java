package lykrast.prodigytech.common.tileentity;

import lykrast.prodigytech.common.block.BlockAeroheaterMagmatic;
import lykrast.prodigytech.common.capability.CapabilityHotAir;
import lykrast.prodigytech.common.capability.HotAirProfileAeroheater;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager.MagmaticAeroheaterRecipe;
import lykrast.prodigytech.common.util.WorldUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TileAeroheaterMagmatic extends TileEntity implements ITickable, IProcessing {
    private HotAir hotAir;
    private MagmaticAeroheaterRecipe currentRecipe, lastRecipe;
    private boolean checkNextTick = true;
    private int residualHeat = 0;

    public TileAeroheaterMagmatic() {
        hotAir = new HotAir();
        currentRecipe = lastRecipe = null;
    }

    private boolean recipeNeedsUpdate(boolean shouldTryToConsumeFluid) {
        BlockPos belowPos = pos.down();
        FluidStack stackBelow = WorldUtil.drainFluidBlock(world, belowPos, false);
        Fluid fluid = stackBelow == null ? null : stackBelow.getFluid();
        MagmaticAeroheaterRecipe newRecipe = MagmaticAeroheaterManager.getRecipe(fluid);

        if (newRecipe != null) {
            residualHeat = 0;
            currentRecipe = newRecipe;
            hotAir.setRecipe(newRecipe);
        } else if (residualHeat > 0) {
            residualHeat--;
            return false;
        } else {
            hotAir.setRecipe(null);
            currentRecipe = null;
            return true;
        }

        if (currentRecipe != null) {
            if (!shouldTryToConsumeFluid
                    || currentRecipe.consumptionChance <= 0.0F
                    || world.rand.nextFloat() > currentRecipe.consumptionChance) {
                return false;
            }

            residualHeat = currentRecipe.residualHeatDuration;
            lastRecipe = currentRecipe;
            currentRecipe = null;
            if (!world.isRemote) {
                world.setBlockState(belowPos, lastRecipe.outputBlock);
            }
            return true;
        }
        return false;
    }

    public void checkActive() {
        if (recipeNeedsUpdate(false)) {
            markDirty();
        }
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        boolean isActive = world.getBlockState(pos).getValue(BlockAeroheaterMagmatic.ACTIVE);
        boolean activityChanged = false;
        boolean recipeChanged = false;

        if (this.world.getWorldTime() % 20 == 0 || checkNextTick) {
            recipeChanged = recipeNeedsUpdate(true);
            checkNextTick = false;
        }
        if (residualHeat > 0) {
            residualHeat--;
            if (residualHeat == 0) {
                currentRecipe = null;
                activityChanged = true;
            }
        }

        if (isActive != (currentRecipe != null)) {
            activityChanged = true;
            BlockAeroheaterMagmatic.setState(currentRecipe != null, this.world, this.pos);
        }
        hotAir.tick();

        if (activityChanged || recipeChanged) {
            this.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        hotAir.deserializeNBT(compound.getCompoundTag("HotAir"));
        residualHeat = compound.getInteger("ResidualHeat");
        checkNextTick = true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("HotAir", hotAir.serializeNBT());
        compound.setInteger("ResidualHeat", residualHeat);

        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        // Only refresh (invalidate/destroy the TE) if the actual block type changes.
        // Return false if it's just a metadata/state property change for the same block.
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityHotAir.HOT_AIR && (facing == EnumFacing.UP || facing == null)) return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityHotAir.HOT_AIR && (facing == EnumFacing.UP || facing == null)) return (T) hotAir;
        return super.getCapability(capability, facing);
    }

    public int getComparatorOutput() {
        if (residualHeat > 0) {
            return 2;
        } else if (currentRecipe != null) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean isProcessing() {
        return residualHeat > 0;
    }

    @Override
    public int getProgressLeft() {
        return residualHeat;
    }

    @Override
    public int getMaxProgress() {
        return lastRecipe == null ? 1 : lastRecipe.residualHeatDuration;
    }

    @Override
    public boolean invertDisplay() {
        return true;
    }

    private static class HotAir extends HotAirProfileAeroheater {
        HotAir() {
            super();
            defaultCoolingSpeed = MagmaticAeroheaterManager.DEFAULT_COOLING;
        }

        void setRecipe(MagmaticAeroheaterRecipe recipe) {
            if (recipe == null) {
                stopHeating();
            } else {
                setHeatingProfile(recipe.heatingProfile, recipe.coolingProfile);
            }
        }
    }
}
