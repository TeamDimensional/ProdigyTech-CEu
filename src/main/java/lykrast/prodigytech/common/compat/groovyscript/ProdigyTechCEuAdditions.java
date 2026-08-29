package lykrast.prodigytech.common.compat.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.prodigytech.AtomicReshaper;
import com.cleanroommc.groovyscript.compat.mods.prodigytech.ProdigyTech;
import com.cleanroommc.groovyscript.compat.mods.prodigytech.Solderer;

public class ProdigyTechCEuAdditions extends ProdigyTech {

    public final Solderer solderer = new SoldererOverride();
    public final AtomicReshaper atomic_reshaper = new AtomicReshaperOverride();
    public final InfusionManager infusions = new InfusionManager();

    @Override
    public void initialize(GroovyContainer<?> owner) {
        super.initialize(owner);
        addProperty(solderer);
        addProperty(atomic_reshaper);
    }
}
