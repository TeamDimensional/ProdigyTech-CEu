package lykrast.prodigytech.common.compat.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.GroovyLog.Msg;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import javax.annotation.Nullable;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager.MagmaticAeroheaterRecipe;
import lykrast.prodigytech.common.util.HeatingProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

@RegistryDescription
public class MagmaticAeroheater extends VirtualizedRegistry<MagmaticAeroheaterRecipe> {

    @RecipeBuilderDescription(
            example = {
                @Example(".fluid(fluid('water')).heating(200).register()"),
                @Example(
                        ".fluid(fluid('lava')).heating(300).cooling(300, 1).outputBlock(blockstate('minecraft:obsidian')).consumptionChance(0.1f).residualHeat(100).register()")
            })
    public RecipeBuilder builder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = {@Example("fluid('lava')")})
    public boolean remove(FluidStack inputStack) {
        Fluid input = inputStack.getFluid();
        MagmaticAeroheaterRecipe recipe = MagmaticAeroheaterManager.getRecipe(input);
        if (recipe != null) {
            addBackup(recipe);
            MagmaticAeroheaterManager.unregisterRecipe(input);
            return true;
        } else {
            GroovyLog.msg("Unable to remove Magmatic Aeroheater recipe")
                    .add("No recipe for fluid " + input)
                    .post();
            return false;
        }
    }

    @Override
    public void onReload() {
        this.removeScripted().forEach(x -> MagmaticAeroheaterManager.unregisterRecipe(x.inputFluid));
        this.restoreFromBackup().forEach(MagmaticAeroheaterManager::registerRecipe);
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    public class RecipeBuilder extends AbstractRecipeBuilder<MagmaticAeroheaterRecipe> {

        private HeatingProfile heatingProfile, coolingProfile;

        @Property(comp = @Comp(eq = 1))
        private HeatingProfile.Builder heating = new HeatingProfile.Builder();

        @Property(comp = @Comp(lte = 1))
        private HeatingProfile.Builder cooling = new HeatingProfile.Builder();

        @Property(comp = @Comp(gt = 0, unique = "groovyscript.wiki.prodigytech_ceu.magmatic_recipe_nonpermanent"))
        private float consumptionChance = 0.0f;

        @Property(comp = @Comp(gte = 0, unique = "groovyscript.wiki.prodigytech_ceu.magmatic_recipe_nonpermanent"))
        private int residualHeat = 0;

        @Property
        private IBlockState outputBlock = null;

        @RecipeBuilderMethodDescription
        public RecipeBuilder heating(int temperature, int speed) {
            heating.point(temperature, speed);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder heating(int temperature) {
            heating.point(temperature, MagmaticAeroheaterManager.DEFAULT_HEATING);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder cooling(int temperature, int speed) {
            cooling.point(temperature, speed);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder cooling(int temperature) {
            cooling.point(temperature, MagmaticAeroheaterManager.DEFAULT_COOLING);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder consumptionChance(float consumptionChance) {
            this.consumptionChance = consumptionChance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder residualHeat(int residualHeat) {
            this.residualHeat = residualHeat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder outputBlock(IBlockState block) {
            this.outputBlock = block;
            return this;
        }

        @Override
        public @Nullable MagmaticAeroheaterRecipe register() {
            if (!validate()) return null;

            MagmaticAeroheaterRecipe recipe;
            if (outputBlock != null) {
                recipe = new MagmaticAeroheaterRecipe(
                        fluidInput.get(0).getFluid(),
                        outputBlock,
                        heatingProfile,
                        coolingProfile,
                        consumptionChance,
                        residualHeat);
            } else {
                recipe = new MagmaticAeroheaterRecipe(fluidInput.get(0).getFluid(), heatingProfile, coolingProfile);
            }

            MagmaticAeroheaterManager.registerRecipe(recipe);
            addScripted(recipe);
            return recipe;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Prodigy Tech Magmatic Aeroheater recipe";
        }

        @Override
        public void validate(Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(heating.size() == 0, "Must have a heating profile");
            if (outputBlock != null) {
                msg.add(
                        consumptionChance <= 0.0f,
                        "Must have a positive consumption chance for recipes with an output");
                msg.add(residualHeat < 0, "Residual heat must not be negative");
            }

            if (heating.size() > 0) {
                heatingProfile = heating.build();
                if (cooling.size() == 0) {
                    cooling.point(heatingProfile.getMaxTemperature(), MagmaticAeroheaterManager.DEFAULT_COOLING);
                }
                coolingProfile = cooling.build();
                msg.add(
                        heatingProfile.getMaxTemperature() != coolingProfile.getMaxTemperature(),
                        "Heating and cooling must have the same max temperature");
            }
        }
    }
}
