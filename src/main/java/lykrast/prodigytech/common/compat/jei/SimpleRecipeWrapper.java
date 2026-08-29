package lykrast.prodigytech.common.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lykrast.prodigytech.common.recipe.SimpleRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleRecipeWrapper implements IRecipeWrapper {
	//Used for 1 input -> 1 output recipes, like most Simple Recipes and a few others like the Fuel Processor
	private ItemStack out;
	private List<List<ItemStack>> in;
	private int time;
	private final IDrawableAnimated arrow;
	
	public SimpleRecipeWrapper(ItemStack input, ItemStack output, int ticks, IGuiHelper guiHelper) {
		in = Collections.singletonList(Collections.singletonList(input));
		out = output;
		time = ticks;
		arrow = guiHelper.createAnimatedDrawable(ProdigyTechJEI.getDefaultProcessArrow(guiHelper), ticks, IDrawableAnimated.StartDirection.LEFT, false);
	}
	
	public SimpleRecipeWrapper(SimpleRecipe recipe, IGuiHelper guiHelper) {
		List<ItemStack> inputs = new ArrayList<>();
		
		if (recipe.isOreRecipe())
		{
			List<ItemStack> items = OreDictionary.getOres(recipe.getOreInput(), false);
			inputs.addAll(items);
		}
		else inputs.add(recipe.getInput());
		
		in = Collections.singletonList(inputs);
		out = recipe.getOutput();
		time = recipe.getTimeTicks();
		
		arrow = guiHelper.createAnimatedDrawable(ProdigyTechJEI.getDefaultProcessArrow(guiHelper), recipe.getTimeTicks(), IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, in);
		ingredients.setOutput(VanillaTypes.ITEM, out);
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		arrow.draw(minecraft, 24, 5);
	}
	
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (mouseX >= 24 && mouseY >= 5 && mouseX <= 24 + arrow.getWidth() && mouseY <= 5 + arrow.getHeight()) {
			List<String> list = new ArrayList<>();
			list.add(I18n.format("container.prodigytech.jei.base_time", time));
			return list;
		} else return Collections.emptyList();
	}

}
