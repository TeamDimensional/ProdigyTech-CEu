package lykrast.prodigytech.common.compat.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.prodigytech.AtomicReshaper;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import lykrast.prodigytech.common.recipe.AtomicReshaperManager;
import lykrast.prodigytech.common.recipe.Infusion;
import lykrast.prodigytech.common.recipe.Infusion.InfusionCost;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;

@RegistryDescription(
        admonition =
                @Admonition(
                        value = "groovyscript.wiki.prodigytech_ceu.must_use_builder",
                        type = Admonition.Type.WARNING))
public class AtomicReshaperOverride extends AtomicReshaper {

    @RecipeBuilderDescription(
            example = {
                @Example(
                        ".pattern(item('minecraft:clay')).input(item('minecraft:gold_ingot')).output(item('minecraft:diamond')).gold(5).time(100)"),
                @Example(".pattern(item('minecraft:coal_block')).output(item('minecraft:nether_star')).gold(75)"),
            })
    public RecipeBuilder builder() {
        return new RecipeBuilder();
    }

    @Override
    public AtomicReshaper.RecipeBuilder recipeBuilder() {
        throw new NotImplementedException(
                "recipeBuilder() is currently not supported for Atomic Reshaper, use builder() instead");
    }

    @Override
    public List<String> getAliases() {
        return Alias.generateOfClass(AtomicReshaper.class);
    }

    @Override
    public String getName() {
        return this.getAliases().get(0).toLowerCase(Locale.ENGLISH);
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AtomicReshaperManager.AtomicReshaperRecipe> {

        @Property(comp = @Comp(gte = 1), defaultValue = "Config.atomicReshaperProcessTime")
        private int time = Config.atomicReshaperProcessTime;

        @Property(comp = @Comp(gte = 1))
        private int primordium;

        private final List<Integer> outputWeights = new ArrayList<>();

        @Property(comp = @Comp(unique = "groovyscript.wiki.prodigytech_ceu.atomic_reshaper_infusion"))
        private String infusion = "primordium";

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder primordium(int primordium) {
            this.primordium = primordium;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"primordium", "infusion"})
        public RecipeBuilder infusion(String name, int primordium) {
            this.infusion = name;
            this.primordium = primordium;
            return this;
        }

        @Override
        public RecipeBuilder output(ItemStack output) {
            output(output, 1);
            return this;
        }

        @Override
        public RecipeBuilder output(ItemStack... outputs) {
            for (ItemStack output : outputs) {
                output(output, 1);
            }
            return this;
        }

        @Override
        public RecipeBuilder output(Collection<ItemStack> outputs) {
            for (ItemStack output : outputs) {
                output(output, 1);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output, int weight) {
            this.output.add(output);
            outputWeights.add(weight);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding ProdigyTech Atomic Reshaper Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // The recipe correctly requires an increased amount of input items, but only consumes 1
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, Integer.MAX_VALUE);
            validateFluids(msg);

            // I think this check is not possible to fail at all but still, adding it for consistency
            msg.add(output.size() != outputWeights.size(), "Outputs and output weights must be the same size!");

            msg.add(
                    outputWeights.stream().anyMatch(x -> x <= 0),
                    "all weighted outputs must be greater than 0, yet they were {}",
                    outputWeights);
            msg.add(primordium <= 0, "primordium must be greater than or equal to 1, yet it was {}", primordium);
            // 100 is hardcoded in the source
            int capacity = Config.atomicReshaperCapacity;
            msg.add(
                    primordium > capacity,
                    "primordium must be less than or equal to the Reshaper's capacity {}, yet it was {}",
                    capacity,
                    primordium);
            msg.add(time <= 0, "time must be greater than 0, got {}", time);
            Infusion data = Infusion.INFUSIONS.get(infusion);
            msg.add(data == null, "infusion {} is not registered", infusion);
            msg.add(
                    data != null && !data.machines.contains("atomic_reshaper"),
                    "infusion {} cannot be used with Atomic Reshaper");
        }

        private Object[] getRecipeOutput() {
            List<Object> target = new ArrayList<>();
            for (int i = 0; i < output.size(); i++) {
                target.add(output.get(i));
                target.add(outputWeights.get(i));
            }
            return target.toArray();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AtomicReshaperManager.AtomicReshaperRecipe register() {
            if (!validate()) return null;
            AtomicReshaperManager.AtomicReshaperRecipe recipe = null;
            IIngredient inputItem = input.get(0);
            if (inputItem instanceof OreDictIngredient) {
                String oredict = ((OreDictIngredient) inputItem).getOreDict();
                recipe = new AtomicReshaperManager.AtomicReshaperRecipe(
                        oredict, time, new InfusionCost(infusion, primordium), getRecipeOutput());
                ModSupport.PRODIGY_TECH.get().atomicReshaper.add(recipe);
            } else {
                for (ItemStack it : inputItem.getMatchingStacks()) {
                    recipe = new AtomicReshaperManager.AtomicReshaperRecipe(
                            it, time, new InfusionCost(infusion, primordium), getRecipeOutput());
                    ModSupport.PRODIGY_TECH.get().atomicReshaper.add(recipe);
                }
            }

            return recipe;
        }
    }
}
