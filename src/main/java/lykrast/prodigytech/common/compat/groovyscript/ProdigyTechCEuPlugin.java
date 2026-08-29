package lykrast.prodigytech.common.compat.groovyscript;

import java.util.ArrayList;
import java.util.Collection;

import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lykrast.prodigytech.core.ProdigyTech;

public class ProdigyTechCEuPlugin implements GroovyPlugin {

    public static ProdigyTechCEuAdditions get() {
        return new ProdigyTechCEuAdditions();
    }

    @Override
    public String getContainerName() {
        return ProdigyTech.NAME;
    }

    @Override
    public String getModId() {
        return ProdigyTech.MODID;
    }

    @Override
    public GroovyPropertyContainer createGroovyPropertyContainer() {
        return get();
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> arg0) {
    }

    @Override
    public @NotNull Priority getOverridePriority() {
        return Priority.OVERRIDE;
    }

    @Override
    public Collection<String> getAliases() {
        Collection<String> info = new ArrayList<>();
        info.add(ProdigyTech.MODID);
        info.add("prodigytech_ceu");
        return info;
    }

}
