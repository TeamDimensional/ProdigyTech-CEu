package lykrast.prodigytech.common.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lykrast.prodigytech.client.gui.GuiAtomicReshaper;
import lykrast.prodigytech.client.gui.GuiInfusion;
import lykrast.prodigytech.common.recipe.AtomicReshaperManager.AtomicReshaperRecipe;
import lykrast.prodigytech.common.recipe.Infusion;
import lykrast.prodigytech.common.recipe.Infusion.InfusionCost;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class AtomicReshaperWrapper implements IRecipeWrapper, ITooltipCallback<ItemStack> {
    protected static final String AMOUNT = "container.prodigytech.atomic_reshaper.primordium.amount";
    protected static final String CHANCE = "container.prodigytech.jei.ptreshaper.chance";
    protected static final String CHANCE_LOW = "container.prodigytech.jei.ptreshaper.chance.low";

    private List<ItemStack> in;
    private List<List<ItemStack>> out;
    private final IDrawableAnimated arrow;
    private final IDrawable primordiumGauge;
    private int totalWeight, time;
    private int[] weights;
    private InfusionCost cost;

    public AtomicReshaperWrapper(AtomicReshaperRecipe recipe, IGuiHelper guiHelper) {
        in = new ArrayList<>();
        if (recipe.isOreRecipe()) {
            List<ItemStack> items = OreDictionary.getOres(recipe.getOreInput(), false);
            in.addAll(items);
        } else in.add(recipe.getInput());

        List<ItemStack> outputs;
        if (!recipe.isSingleOutput()) outputs = recipe.getOutputList();
        else outputs = Collections.singletonList(recipe.getSingleOutput());
        out = Collections.singletonList(outputs);

        weights = recipe.getWeights();
        totalWeight = recipe.getTotalWeight();
        cost = recipe.getCost();

        arrow = guiHelper.createAnimatedDrawable(
                guiHelper.createDrawable(GuiAtomicReshaper.GUI, 176, 0, 48, 17),
                recipe.getTimeTicks(),
                IDrawableAnimated.StartDirection.LEFT,
                false);

        primordiumGauge =
                GuiInfusion.makeJEIDrawable(guiHelper, recipe.getCost().getInfusionId(), recipe.getCost().amount, 100);
        time = recipe.getTimeTicks();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();
        inputs.add(cost.getRepresentatives());
        inputs.add(in);

        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutputLists(VanillaTypes.ITEM, out);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        arrow.draw(minecraft, 60, 19);
        primordiumGauge.draw(minecraft, 30, 1 + (GuiInfusion.MAX_HEIGHT - primordiumGauge.getHeight()));
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX >= 30 && mouseX <= 34) {
            List<String> list = new ArrayList<>();
            list.add(cost.localize());
            return list;
        } else if (mouseX >= 60
                && mouseY >= 19
                && mouseX <= 60 + arrow.getWidth()
                && mouseY <= 19 + arrow.getHeight()) {
            List<String> list = new ArrayList<>();
            list.add(I18n.format("container.prodigytech.jei.base_time", time));
            return list;
        } else return Collections.emptyList();
    }

    @Override
    public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        if (slotIndex == 2 && weights.length > 1) {
            int chance = (weights[out.get(0).indexOf(ingredient)] * 100) / totalWeight;
            if (chance == 0) tooltip.add(I18n.format(CHANCE_LOW));
            else tooltip.add(I18n.format(CHANCE, chance));
        } else if (slotIndex == 0) {
            int count = Infusion.INFUSIONS.get(cost.infusion).getOutputFor(ingredient);
            tooltip.add(I18n.format(GuiInfusion.PROVIDES, count));
        }
    }
}
