package lykrast.prodigytech.common.recipe;

import java.util.ArrayList;
import java.util.List;
import lykrast.prodigytech.common.init.ModItems;
import lykrast.prodigytech.common.recipe.Infusion.InfusionCost;
import lykrast.prodigytech.common.recipe.Infusion.InfusionState;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SoldererManager {
    public static final List<SoldererRecipe> RECIPES = new ArrayList<>();
    private static int idGoldDust, idGoldTinyDust;

    @Deprecated
    public static SoldererRecipe addRecipe(ItemStack pattern, ItemStack additive, ItemStack output, int gold) {
        return addRecipe(new SoldererRecipe(pattern, additive, output, gold));
    }

    @Deprecated
    public static SoldererRecipe addRecipe(
            ItemStack pattern, ItemStack additive, ItemStack output, int gold, int time) {
        return addRecipe(new SoldererRecipe(pattern, additive, output, gold, time));
    }

    public static SoldererRecipe addRecipe(ItemStack pattern, ItemStack additive, ItemStack output, InfusionCost gold) {
        return addRecipe(new SoldererRecipe(pattern, additive, output, gold));
    }

    public static SoldererRecipe addRecipe(
            ItemStack pattern, ItemStack additive, ItemStack output, InfusionCost gold, int time) {
        return addRecipe(new SoldererRecipe(pattern, additive, output, gold, time));
    }

    public static SoldererRecipe addRecipe(SoldererRecipe recipe) {
        RECIPES.add(recipe);
        return recipe;
    }

    public static void removeAll() {
        RECIPES.clear();
    }

    @Deprecated
    public static SoldererRecipe findRecipe(ItemStack pattern, ItemStack additive, int gold) {
        for (SoldererRecipe recipe : RECIPES) if (recipe.isValidInput(pattern, additive, gold)) return recipe;

        return null;
    }

    @Deprecated
    public static SoldererRecipe removeRecipe(ItemStack pattern, ItemStack additive, int gold) {
        SoldererRecipe recipe = findRecipe(pattern, additive, gold);
        if (recipe != null) RECIPES.remove(recipe);

        return recipe;
    }

    public static SoldererRecipe findRecipe(ItemStack pattern, ItemStack additive, InfusionState gold) {
        for (SoldererRecipe recipe : RECIPES) if (recipe.isValidInput(pattern, additive, gold)) return recipe;

        return null;
    }

    public static SoldererRecipe findRecipe(ItemStack pattern, ItemStack additive, InfusionCost gold) {
        for (SoldererRecipe recipe : RECIPES) if (recipe.matches(pattern, additive, gold)) return recipe;

        return null;
    }

    public static SoldererRecipe removeRecipe(ItemStack pattern, ItemStack additive, InfusionCost gold) {
        SoldererRecipe recipe = findRecipe(pattern, additive, gold);
        if (recipe != null) RECIPES.remove(recipe);

        return recipe;
    }

    public static boolean isValidPattern(ItemStack pattern) {
        for (SoldererRecipe recipe : RECIPES) if (recipe.isValidPattern(pattern)) return true;

        return false;
    }

    public static boolean isValidAdditive(ItemStack additive) {
        for (SoldererRecipe recipe : RECIPES)
            if (recipe.requiresAdditive() && recipe.isValidAdditive(additive)) return true;

        return false;
    }

    @Deprecated
    public static int getGoldAmount(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int[] oreIDs = OreDictionary.getOreIDs(stack);
        for (int i : oreIDs) {
            if (i == idGoldDust) return 9;
            else if (i == idGoldTinyDust) return 1;
        }
        return 0;
    }

    public static boolean isPlate(ItemStack stack) {
        return stack.getItem() == ModItems.circuitPlate;
    }

    public static void init() {
        idGoldDust = OreDictionary.getOreID("dustGold");
        idGoldTinyDust = OreDictionary.getOreID("dustTinyGold");

        addRecipe(
                new ItemStack(ModItems.patternCircuitCrude),
                ItemStack.EMPTY,
                new ItemStack(ModItems.circuitCrude),
                new InfusionCost("gold", 3));
        addRecipe(
                new ItemStack(ModItems.patternCircuitRefined),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(ModItems.circuitRefined),
                new InfusionCost("gold", 6),
                (int) (Config.soldererProcessTime * 1.5));
        addRecipe(
                new ItemStack(ModItems.patternCircuitPerfected),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ModItems.circuitPerfected),
                new InfusionCost("gold", 9),
                Config.soldererProcessTime * 2);
    }

    public static class SoldererRecipe {
        private final ItemStack pattern;
        private final ItemStack additive;
        private final ItemStack output;
        private final int time;
        private final InfusionCost gold;

        public SoldererRecipe(ItemStack pattern, ItemStack additive, ItemStack output, InfusionCost gold) {
            this(pattern, additive, output, gold, Config.soldererProcessTime);
        }

        public SoldererRecipe(ItemStack pattern, ItemStack additive, ItemStack output, InfusionCost gold, int time) {
            this.pattern = pattern;
            pattern.setCount(1);
            this.additive = additive;
            this.output = output;
            this.gold = gold;
            this.time = time;
        }

        @Deprecated
        public SoldererRecipe(ItemStack pattern, ItemStack additive, ItemStack output, int gold) {
            this(pattern, additive, output, new InfusionCost("gold", gold));
        }

        @Deprecated
        public SoldererRecipe(ItemStack pattern, ItemStack additive, ItemStack output, int gold, int time) {
            this(pattern, additive, output, new InfusionCost("gold", gold), time);
        }

        public ItemStack getPattern() {
            return pattern.copy();
        }

        public ItemStack getAdditive() {
            return additive.copy();
        }

        public ItemStack getOutput() {
            return output.copy();
        }

        @Deprecated
        public int getGoldAmount() {
            return gold.amount;
        }

        public InfusionCost getInfusion() {
            return gold;
        }

        public int getTimeTicks() {
            return time;
        }

        public int getTimeProcessing() {
            return time * 10;
        }

        @Deprecated
        public boolean isValidInput(ItemStack pattern, ItemStack additive, int gold) {
            if (!isValidPattern(pattern)) return false;
            if (!isValidAdditive(additive)) return false;

            return isEnoughGold(gold);
        }

        public boolean isValidInput(ItemStack pattern, ItemStack additive, InfusionState state) {
            if (!isValidPattern(pattern)) return false;
            if (!isValidAdditive(additive)) return false;

            return state.subtract(gold, true);
        }

        public boolean matches(ItemStack pattern, ItemStack additive, InfusionCost cost) {
            if (!isValidPattern(pattern)) return false;
            if (!isValidAdditive(additive)) return false;

            return cost.equals(gold);
        }

        public boolean isValidPattern(ItemStack pattern) {
            return pattern.isItemEqual(this.pattern);
        }

        public boolean requiresAdditive() {
            return !additive.isEmpty();
        }

        public boolean isValidAdditive(ItemStack additive) {
            if (!requiresAdditive()) return true;
            return (additive.isItemEqual(this.additive) && additive.getCount() >= this.additive.getCount());
        }

        @Deprecated
        public boolean isEnoughGold(int gold) {
            return gold >= this.gold.amount;
        }
    }
}
