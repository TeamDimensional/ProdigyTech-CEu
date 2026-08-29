package lykrast.prodigytech.common.compat.jei;

import java.util.List;

import java.awt.Rectangle;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

import lykrast.prodigytech.common.util.Config;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.gui.ingredients.GuiIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class IncineratorCategory extends ProdigyCategory<IncineratorWrapper> {
	public static final String UID = "ptincinerator";
	private final IDrawableAnimated arrow;
	private List<ItemStack> allItems;
	private static GuiIngredient<ItemStack> allItemIngredient;

	protected static final String ANY_ITEM = "container.prodigytech.jei.ptincinerator.any_item";

	public IncineratorCategory(IGuiHelper guiHelper) {
		super(guiHelper, guiHelper.drawableBuilder(ProdigyTechJEI.GUI, 0, 36, 82, 26)
				.addPadding(0, (int)(Config.incineratorChance * 100) >= 100 ? 0 : 10 , 0, 0).build(), UID);
		
		this.arrow = guiHelper.createAnimatedDrawable(ProdigyTechJEI.getDefaultProcessArrow(guiHelper), Config.incineratorProcessTime, IDrawableAnimated.StartDirection.LEFT, false);

		allItems = new ArrayList<>();
		
		for (Item i : ForgeRegistries.ITEMS) {
			allItems.add(new ItemStack(i, 1, OreDictionary.WILDCARD_VALUE));
		}
	}
	
	@Override
	public void drawExtras(Minecraft minecraft)
	{
		arrow.draw(minecraft, 24, 5);
		allItemIngredient.draw(minecraft, 0, 4);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IncineratorWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(1, false, 60, 4);
		guiItemStacks.set(ingredients);

		allItemIngredient = new GuiIngredient<>(0, true, ProdigyTechJEI.stackRenderer, ProdigyTechJEI.stackHelper, new Rectangle(0, 0, 16, 16), 1, 1, 0);
		allItemIngredient.set(allItems, null);
	}

	public static void registerRecipes(IModRegistry registry)
	{
		registry.addRecipes(ImmutableList.of(new IncineratorWrapper()), UID);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		List<String> tooltips = new ArrayList<>();
		if (mouseX >= 0 && mouseX < 16 && mouseY >= 4 && mouseY < 20) {
			tooltips.add(I18n.format(ANY_ITEM));
		}
		if (mouseX >= 24 && mouseY >= 5 && mouseX <= 24 + arrow.getWidth() && mouseY <= 5 + arrow.getHeight()) {
			tooltips.add(I18n.format("container.prodigytech.jei.base_time", Config.incineratorProcessTime));
		}
		return tooltips;
	}

}
