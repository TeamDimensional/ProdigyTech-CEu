package lykrast.prodigytech.common.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import lykrast.prodigytech.common.recipe.Infusion;
import lykrast.prodigytech.common.recipe.Infusion.InfusionItem;
import lykrast.prodigytech.common.recipe.Infusion.InfusionNameDescriptor;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.prodigytech.infusion")
@ZenRegister
public class InfusionManager {

    Infusion infusion;

    private InfusionManager(Infusion infusion) {
        this.infusion = infusion;
    }
    
    @ZenMethod
    public static InfusionManager addInfusion(String name, int id, String unlocalizedName) {
        if (Infusion.INFUSIONS.containsKey(name)) {
            throw new IllegalArgumentException("Infusion " + name + " is already registered");
        }
        if (Infusion.INFUSIONS_BY_ID.containsKey(id)) {
            throw new IllegalArgumentException("Infusion ID " + id + " is already registered");
        }
        Infusion infusion = new Infusion(name, id, unlocalizedName);
        return new InfusionManager(infusion);
    }

    @ZenMethod
    public static InfusionManager getInfusion(String name) {
        Infusion infusion = Infusion.INFUSIONS.get(name);
        if (infusion == null) {
            throw new IllegalArgumentException("Infusion " + name + " is not registered");
        }
        return new InfusionManager(infusion);
    }

    @ZenMethod
    public InfusionManager addMachine(String machine) {
        infusion.machines.add(machine);
        return this;
    }

    @ZenMethod
    public InfusionManager addItem(IIngredient stack) {
        return addItem(stack, 1);
    }

    @ZenMethod
    public InfusionManager addItem(IIngredient stack, int amount) {
        if (stack instanceof IItemStack) {
            ItemStack mcStack = CraftTweakerMC.getItemStack((IItemStack) stack);
            infusion.items.add(new InfusionItem(mcStack, amount));
        } else if (stack instanceof IOreDictEntry) {
            String name = ((IOreDictEntry) stack).getName();
            infusion.items.add(new InfusionItem(name, amount));
        } else {
            throw new IllegalArgumentException("Argument not currently supported");
        }
        return this;
    }

    @ZenMethod
    public InfusionManager addUnlocalizedName(String name, int multiplier) {
        infusion.addNaming(new InfusionNameDescriptor(name, multiplier));
        return this;
    }

    @ZenMethod
    public void register() {
        Infusion.registerInfusion(infusion);
    }

    @ZenMethod
    public InfusionManager clear() {
        infusion.clear();
        return this;
    }

    @ZenMethod
    public InfusionManager setBaseName(String name) {
        infusion.setBaseName(name);
        return this;
    }

}
