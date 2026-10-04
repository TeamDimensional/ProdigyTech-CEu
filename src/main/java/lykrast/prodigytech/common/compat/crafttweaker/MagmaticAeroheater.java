package lykrast.prodigytech.common.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.liquid.ILiquidStack;
import javax.annotation.Nullable;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager.MagmaticAeroheaterRecipe;
import lykrast.prodigytech.common.util.HeatingProfile;
import net.minecraftforge.fluids.Fluid;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.prodigytech.magmatic_aeroheater")
@ZenRegister
public class MagmaticAeroheater {

    // Add
    @ZenMethod
    public static void add(ILiquidStack input, int maxTemperature) {
        add(input, maxTemperature, null);
    }

    @ZenMethod
    public static void add(ILiquidStack input, int maxTemperature, HeatingProfile cooling) {
        add(
                input,
                new HeatingProfile.Builder()
                        .point(maxTemperature, MagmaticAeroheaterManager.DEFAULT_HEATING)
                        .build(),
                cooling);
    }

    @ZenMethod
    public static void add(ILiquidStack input, HeatingProfile heating) {
        add(input, heating, null);
    }

    @ZenMethod
    public static void add(ILiquidStack input, HeatingProfile heating, @Nullable HeatingProfile cooling) {
        if (input == null) throw new IllegalArgumentException("Input fluid cannot be null");
        if (heating == null) throw new IllegalArgumentException("Heating profile cannot be null");
        if (cooling == null)
            cooling = new HeatingProfile.Builder()
                    .point(heating.getMaxTemperature(), MagmaticAeroheaterManager.DEFAULT_COOLING)
                    .build();
        if (cooling.getMaxTemperature() != heating.getMaxTemperature())
            throw new IllegalArgumentException("Heating and cooling profiles must have the same max temperature");
        CraftTweakerAPI.apply(
                new Add(CraftTweakerHelper.toFluidStack(input).getFluid(), heating, cooling, 0.0f, 0, null));
    }

    @ZenMethod
    public static void add(
            ILiquidStack input, int maxTemperature, IBlockState block, float consumptionChance, int residualHeat) {
        add(input, maxTemperature, null, block, consumptionChance, residualHeat);
    }

    @ZenMethod
    public static void add(
            ILiquidStack input,
            int maxTemperature,
            HeatingProfile cooling,
            IBlockState block,
            float consumptionChance,
            int residualHeat) {
        add(
                input,
                new HeatingProfile.Builder()
                        .point(maxTemperature, MagmaticAeroheaterManager.DEFAULT_HEATING)
                        .build(),
                cooling,
                block,
                consumptionChance,
                residualHeat);
    }

    @ZenMethod
    public static void add(
            ILiquidStack input, HeatingProfile heating, IBlockState block, float consumptionChance, int residualHeat) {
        add(input, heating, null, block, consumptionChance, residualHeat);
    }

    @ZenMethod
    public static void add(
            ILiquidStack input,
            HeatingProfile heating,
            @Nullable HeatingProfile cooling,
            IBlockState block,
            float consumptionChance,
            int residualHeat) {
        if (input == null) throw new IllegalArgumentException("Input fluid cannot be null");
        if (heating == null) throw new IllegalArgumentException("Heating profile cannot be null");
        if (block == null)
            throw new IllegalArgumentException(
                    "Output block cannot be null, if you meant to add a permanent recipe use the shorter form of add()");
        if (consumptionChance <= 0.0f)
            throw new IllegalArgumentException(
                    "Consumption chance must be positive, if you meant to add a permanent recipe use the shorter form of add()");
        if (residualHeat < 0) throw new IllegalArgumentException("Residual heat duration must not be negative");
        if (cooling == null)
            cooling = new HeatingProfile.Builder()
                    .point(heating.getMaxTemperature(), MagmaticAeroheaterManager.DEFAULT_COOLING)
                    .build();
        if (cooling.getMaxTemperature() != heating.getMaxTemperature())
            throw new IllegalArgumentException("Heating and cooling profiles must have the same max temperature");
        CraftTweakerAPI.apply(new Add(
                CraftTweakerHelper.toFluidStack(input).getFluid(),
                heating,
                cooling,
                consumptionChance,
                residualHeat,
                block));
    }

    private static class Add implements IAction {
        private final Fluid fluid;
        private final HeatingProfile heating, cooling;
        private final float consumptionChance;
        private final int residualHeatDuration;
        private final IBlockState output;

        Add(
                Fluid fluid,
                HeatingProfile heating,
                HeatingProfile cooling,
                float consumptionChance,
                int residualHeatDuration,
                IBlockState output) {
            this.fluid = fluid;
            this.heating = heating;
            this.cooling = cooling;
            this.consumptionChance = consumptionChance;
            this.residualHeatDuration = residualHeatDuration;
            this.output = output;
        }

        @Override
        public void apply() {
            if (output == null) {
                MagmaticAeroheaterManager.registerRecipe(new MagmaticAeroheaterRecipe(fluid, heating, cooling));
            } else {
                MagmaticAeroheaterManager.registerRecipe(new MagmaticAeroheaterRecipe(
                        fluid,
                        CraftTweakerHelper.toBlockState(output),
                        heating,
                        cooling,
                        consumptionChance,
                        residualHeatDuration));
            }
        }

        @Override
        public String describe() {
            return "Adding Magmatic Aeroheater fluid " + fluid.getName();
        }
    }

    // Remove
    @ZenMethod
    public static void remove(ILiquidStack input) {
        if (input == null) throw new IllegalArgumentException("Input fluid cannot be null");
        CraftTweakerAPI.apply(new Remove(CraftTweakerHelper.toFluidStack(input).getFluid()));
    }

    private static class Remove implements IAction {
        private final Fluid fluid;

        Remove(Fluid fluid) {
            this.fluid = fluid;
        }

        @Override
        public void apply() {
            MagmaticAeroheaterManager.unregisterRecipe(fluid);
        }

        @Override
        public String describe() {
            return "Removing Magmatic Aeroheater fluid " + fluid.getName();
        }
    }
}
