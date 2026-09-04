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
import com.cleanroommc.groovyscript.compat.mods.prodigytech.Solderer;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import lykrast.prodigytech.common.init.ModItems;
import lykrast.prodigytech.common.recipe.Infusion;
import lykrast.prodigytech.common.recipe.Infusion.InfusionCost;
import lykrast.prodigytech.common.recipe.SoldererManager;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;

@RegistryDescription(
        admonition =
                @Admonition(
                        value = "groovyscript.wiki.prodigytech_ceu.must_use_builder",
                        type = Admonition.Type.WARNING))
public class SoldererOverride extends Solderer {

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
    public Solderer.RecipeBuilder recipeBuilder() {
        throw new NotImplementedException(
                "recipeBuilder() is currently not supported for Solderer, use builder() instead");
    }

    @Override
    public List<String> getAliases() {
        return Alias.generateOfClass(Solderer.class);
    }

    @Override
    public String getName() {
        return this.getAliases().get(0).toLowerCase(Locale.ENGLISH);
    }

    @Property(property = "input", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public class RecipeBuilder extends AbstractRecipeBuilder<SoldererManager.SoldererRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int time = Config.soldererProcessTime;

        @Property(comp = @Comp(gte = 1))
        private int gold;

        @Property(comp = @Comp(eq = 1))
        private IIngredient pattern;

        @Property(comp = @Comp(eq = 1), defaultValue = "groovyscript.wiki.prodigytech_ceu.solderer_circuit_plate")
        private IIngredient circuitBoard;

        @Property(comp = @Comp(unique = "groovyscript.wiki.prodigytech_ceu.solderer_infusion"))
        private String infusion = "gold";

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder gold(int gold) {
            this.gold = gold;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"gold", "infusion"})
        public RecipeBuilder infusion(String name, int gold) {
            this.infusion = name;
            this.gold = gold;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder pattern(IIngredient pattern) {
            this.pattern = pattern;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder circuitBoard(IIngredient circuitBoard) {
            this.circuitBoard = circuitBoard;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding ProdigyTech Solderer Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 1, 1);
            validateFluids(msg);
            msg.add(gold <= 0, "gold must be greater than or equal to 1, yet it was {}", gold);
            msg.add(IngredientHelper.isEmpty(pattern), "pattern cannot be empty");
            int capacity = Config.soldererCapacity;
            msg.add(
                    gold > capacity,
                    "gold must be less than or equal to the Solderer's capacity {}, yet it was {}",
                    capacity,
                    gold);
            msg.add(time <= 0, "time must be greater than 0, got {}", time);
            Infusion data = Infusion.INFUSIONS.get(infusion);
            msg.add(data == null, "infusion {} is not registered", infusion);
            msg.add(data != null && !data.machines.contains("solderer"), "infusion {} cannot be used with Solderer");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SoldererManager.SoldererRecipe register() {
            if (!validate()) return null;
            SoldererManager.SoldererRecipe recipe = null;
            for (ItemStack pat : pattern.getMatchingStacks()) {
                IIngredient circuitBoard = this.circuitBoard == null
                        ? new ItemsIngredient(new ItemStack(ModItems.circuitPlate))
                        : this.circuitBoard;
                for (ItemStack board : circuitBoard.getMatchingStacks()) {
                    if (input.isEmpty()) {
                        SoldererManager.SoldererRecipe theRecipe = new SoldererManager.SoldererRecipe(
                                pat, ItemStack.EMPTY, board, output.get(0), new InfusionCost(infusion, gold), time);
                        SoldererOverride.this.add(theRecipe);
                        if (recipe != null) recipe = theRecipe;
                    } else {
                        for (ItemStack additive : input.get(0).getMatchingStacks()) {
                            SoldererManager.SoldererRecipe theRecipe = new SoldererManager.SoldererRecipe(
                                    pat, additive, board, output.get(0), new InfusionCost(infusion, gold), time);
                            SoldererOverride.this.add(theRecipe);
                            if (recipe != null) recipe = theRecipe;
                        }
                    }
                }
            }

            return recipe;
        }
    }
}
