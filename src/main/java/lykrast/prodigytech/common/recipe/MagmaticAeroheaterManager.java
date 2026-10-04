package lykrast.prodigytech.common.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lykrast.prodigytech.common.util.HeatingProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class MagmaticAeroheaterManager {
    private static final Map<Fluid, MagmaticAeroheaterRecipe> RECIPES = new HashMap<>();

    public static final int DEFAULT_HEATING = 4;
    public static final int DEFAULT_COOLING = 8;

    public static @Nullable MagmaticAeroheaterRecipe getRecipe(Fluid inputFluid) {
        return RECIPES.get(inputFluid);
    }

    public static @Nullable Collection<MagmaticAeroheaterRecipe> getAllRecipes() {
        return RECIPES.values();
    }

    public static void registerRecipe(MagmaticAeroheaterRecipe recipe) {
        if (getRecipe(recipe.inputFluid) != null) {
            throw new IllegalArgumentException(
                    "Duplicate Magmatic Aeroheater recipe, using fluid " + recipe.inputFluid);
        }
        RECIPES.put(recipe.inputFluid, recipe);
    }

    public static void unregisterRecipe(Fluid inputFluid) {
        RECIPES.remove(inputFluid);
    }

    public static void init() {
        registerRecipe(new MagmaticAeroheaterRecipe(
                FluidRegistry.LAVA,
                new HeatingProfile.Builder().point(80, DEFAULT_HEATING).build(),
                new HeatingProfile.Builder().point(80, DEFAULT_COOLING).build()));
    }

    public static class MagmaticAeroheaterRecipe {
        public final Fluid inputFluid;
        public final @Nullable IBlockState outputBlock;
        public final float consumptionChance;
        public final int residualHeatDuration;
        public final HeatingProfile heatingProfile, coolingProfile;

        public MagmaticAeroheaterRecipe(
                @Nonnull Fluid inputFluid,
                @Nonnull IBlockState outputBlock,
                HeatingProfile heating,
                HeatingProfile cooling,
                float consumptionChance,
                int residualHeatDuration) {
            if (consumptionChance <= 0.0F) {
                throw new IllegalArgumentException("Consumption chance must be positive for transforming recipes");
            }
            this.inputFluid = inputFluid;
            this.outputBlock = outputBlock;
            this.heatingProfile = heating;
            this.coolingProfile = cooling;
            this.consumptionChance = consumptionChance;
            this.residualHeatDuration = residualHeatDuration;
        }

        public MagmaticAeroheaterRecipe(Fluid inputFluid, HeatingProfile heating, HeatingProfile cooling) {
            this.inputFluid = inputFluid;
            this.outputBlock = null;
            this.heatingProfile = heating;
            this.coolingProfile = cooling;
            this.consumptionChance = 0.0F;
            this.residualHeatDuration = 0;
        }

        public boolean isPermanent() {
            return outputBlock == null;
        }
    }
}
