package lykrast.prodigytech.common.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lykrast.prodigytech.common.init.ModItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class Infusion {
    public static final Map<Integer, String> INFUSIONS_BY_ID = new Int2ObjectOpenHashMap<>();
    public static final Map<String, Infusion> INFUSIONS = new HashMap<>();
    private static final Map<String, List<Infusion>> MACHINES = new HashMap<>();

    public static class InfusionItem {
        @Nullable final ItemStack item;
        @Nullable final String oreDict;
        final int output;
        final int oreDictId;

        public InfusionItem(@Nonnull ItemStack item, int output) {
            if (output < 1) {
                throw new IllegalArgumentException("Infusion item output must be positive");
            }
            this.item = item;
            this.output = output;
            oreDictId = -1;
            oreDict = null;
        }

        public InfusionItem(@Nonnull String oreDict, int output) {
            if (output < 1) {
                throw new IllegalArgumentException("Infusion item output must be positive");
            }
            this.oreDict = oreDict;
            this.output = output;
            oreDictId = OreDictionary.getOreID(oreDict);
            item = null;
        }

        public boolean matches(@Nonnull ItemStack stack) {
            if (oreDict != null) {
                for (int oreDictId : OreDictionary.getOreIDs(stack)) {
                    if (oreDictId == this.oreDictId) {
                        return true;
                    }
                }
                return false;
            } else if (item != null) {
                return item.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(item, stack);
            } else {
                throw new IllegalStateException("InfusionItem must have an oredict or an item");
            }
        }
    }

    public static class InfusionCost {
        public final String infusion;
        public final int amount;

        public InfusionCost(@Nonnull String infusion, int output) {
            this.infusion = infusion;
            this.amount = output;
        }

        public boolean equals(Object o) {
            if (!(o instanceof InfusionCost)) {
                return false;
            }

            InfusionCost other = (InfusionCost) o;
            return infusion.equals(other.infusion) && amount == other.amount;
        }

        public int getInfusionId() {
            return INFUSIONS.get(infusion).id;
        }

        public List<ItemStack> getRepresentatives() {
            Infusion infusionObj = INFUSIONS.get(infusion);
            List<ItemStack> outputs = new ArrayList<>();
            for (InfusionItem item : infusionObj.items) {
                int count = (amount + item.output - 1) / item.output;
                if (item.item != null) {
                    ItemStack output = item.item.copy();
                    output.setCount(count);
                    outputs.add(output);
                } else {
                    List<ItemStack> oredictStacks = OreDictionary.getOres(item.oreDict);
                    if (oredictStacks.isEmpty()) {
                        throw new IllegalStateException("Infusion " + infusion + " registered a representative oredict " + item.oreDict + ", which has no items");
                    }
                    ItemStack oredictOutput = oredictStacks.get(0).copy();
                    oredictOutput.setCount(count);
                    outputs.add(oredictOutput);
                }
            }
            return outputs;
        }

        public ItemStack getRepresentative() {
            List<ItemStack> representatives = getRepresentatives();
            if (representatives.isEmpty()) {
                throw new IllegalStateException("Infusion " + infusion + " registered no representative items");
            }
            return representatives.get(0);
        }

        @SideOnly(Side.CLIENT)
        public String localize() {
            if (infusion == null) {
                I18n.format("container.prodigytech.infusion.empty");
            }
            Infusion infObject = INFUSIONS.get(infusion);
            return infObject.localizeCount(amount);
        }
    }

    public static class InfusionState {
        @Nullable String infusion;
        int count;
        int capacity;

        public InfusionState(int capacity) {
            this.capacity = capacity;
        }

        public void setInfusion(@Nullable String infusion) {
            if (infusion != null && !INFUSIONS.containsKey(infusion)) {
                throw new IllegalArgumentException("Attempt to set state to illegal infusion " + infusion);
            }
            this.infusion = infusion;
        }

        public void setInfusion(int infusionId) {
            this.infusion = INFUSIONS_BY_ID.get(infusionId);
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getInfusionId() {
            return infusion == null ? 0 : INFUSIONS.get(infusion).id;
        }

        public int getCount() {
            return count;
        }

        public boolean subtract(@Nullable InfusionCost infusion, boolean simulate) {
            if (infusion == null) {
                return true;
            }
            if (!infusion.infusion.equals(this.infusion) || this.count < count) {
                return false;
            }
            if (!simulate) {
                this.count -= infusion.amount;
                if (this.count == 0) {
                    this.infusion = null;
                }
            }
            return true;
        }

        public boolean add(@Nullable InfusionCost infusion, boolean simulate) {
            if (infusion == null || !(this.infusion == null || infusion.infusion.equals(this.infusion)) || capacity < count + infusion.amount) {
                return false;
            }
            if (!simulate) {
                this.infusion = infusion.infusion;
                count += infusion.amount;
            }
            return true;
        }

        public boolean validInput(@Nullable InfusionCost infusion) {
            return infusion != null && (this.infusion == null || infusion.infusion.equals(this.infusion));
        }

        @SideOnly(Side.CLIENT)
        public String localize() {
            if (infusion == null) {
                return I18n.format("container.prodigytech.infusion.empty");
            }
            Infusion infObject = INFUSIONS.get(infusion);
            if (infObject == null) {
                throw new IllegalStateException("Infusion " + infusion + " is in a machine, but it's not a valid infusion");
            }
            return infObject.localizeCount(count);
        }
    }

    public static class InfusionNameDescriptor {
        final String name;
        final int multiplier;

        public InfusionNameDescriptor(String name, int multiplier) {
            this.name = name;
            this.multiplier = multiplier;
            if (multiplier < 2) {
                throw new IllegalArgumentException("Infusion name multipliers must be positive");
            }
        }
    }

    public final String name;
    public final int id;

    public final List<InfusionItem> items = new ArrayList<>();
    public final List<String> machines = new ArrayList<>();
    private String unlocalizedName;
    private final List<InfusionNameDescriptor> naming = new ArrayList<>();

    public Infusion(String name, int id, String unlocalizedName) {
        if (id == 0) {
            throw new IllegalArgumentException("InfusionId 0 is used for no infusion, choose a different number");
        }
        this.name = name;
        this.id = id;
        this.unlocalizedName = unlocalizedName;
    }

    public int getOutputFor(@Nonnull ItemStack stack) {
        for (InfusionItem item : items) {
            if (item.matches(stack)) {
                return item.output;
            }
        }

        return 0;
    }

    public static void registerInfusion(@Nonnull Infusion infusion) {
        INFUSIONS.put(infusion.name, infusion);
        INFUSIONS_BY_ID.put(infusion.id, infusion.name);
        for (String machine : infusion.machines) {
            MACHINES.computeIfAbsent(machine, x -> new ArrayList<>()).add(infusion);
        }
    }

    public void addNaming(InfusionNameDescriptor naming) {
        this.naming.add(naming);
    }

    public void clear() {
        this.items.clear();
        this.naming.clear();
        this.machines.clear();
    }

    public void setBaseName(String name) {
        this.unlocalizedName = name;
    }

    public static void init() {
        Infusion gold = new Infusion("gold", 1, "container.prodigytech.solderer.gold.nuggets");
        gold.naming.add(new InfusionNameDescriptor("container.prodigytech.solderer.gold.ingots", 9));
        gold.naming.add(new InfusionNameDescriptor("container.prodigytech.solderer.gold.blocks", 9));
        gold.machines.add("solderer");
        gold.items.add(new InfusionItem("dustGold", 9));
        gold.items.add(new InfusionItem("dustTinyGold", 1));
        registerInfusion(gold);

        Infusion primordium = new Infusion("primordium", 2, "container.prodigytech.atomic_reshaper.primordium.amount");
        primordium.machines.add("atomic_reshaper");
        primordium.items.add(new InfusionItem(new ItemStack(ModItems.primordium), 100));
        registerInfusion(primordium);
    }

    @SideOnly(Side.CLIENT)
    public String localizeCount(int count) {
        if (count == 0) {
            return I18n.format("container.prodigytech.infusion.empty");
        }

        StringBuilder builder = new StringBuilder();
        List<String> strings = new ArrayList<>();
        String currentName = unlocalizedName;
        for (InfusionNameDescriptor name : naming) {
            int currentCount = count % name.multiplier;
            if (currentCount > 0) {
                strings.add(I18n.format(currentName, currentCount));
            }
            count /= name.multiplier;
            currentName = name.name;
        }
        if (count > 0) {
            strings.add(I18n.format(currentName, count));
        }
        for (int i = strings.size() - 1; i >= 0; i--) {
            builder.append(strings.get(i));
            if (i > 0) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static InfusionCost getInfusionOutput(@Nonnull String machine, @Nonnull ItemStack input) {
        if (!MACHINES.containsKey(machine) || input.isEmpty()) {
            return null;
        }
        for (Infusion infusion : MACHINES.get(machine)) {
            int output = infusion.getOutputFor(input);
            if (output > 0) {
                return new InfusionCost(infusion.name, output);
            }
        }
        return null;
    }

}
