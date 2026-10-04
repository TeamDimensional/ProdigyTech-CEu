package lykrast.prodigytech.common.util;

import crafttweaker.annotations.ZenRegister;
import java.util.ArrayList;
import java.util.List;
import lykrast.prodigytech.core.ProdigyTech;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenConstructor;
import stanhebben.zenscript.annotations.ZenMethod;

public class HeatingProfile {
    public static class HeatPoint {
        public final int maxTemperature;
        public final int speed;

        public HeatPoint(int maxTemperature, int speed) {
            this.maxTemperature = maxTemperature;
            this.speed = speed;
        }
    }

    @ZenClass("mods.prodigytech.heating_profile")
    @ZenRegister
    public static class Builder {
        private final List<HeatPoint> points = new ArrayList<>();

        @ZenConstructor
        public Builder() {}

        @ZenMethod
        public Builder point(int temperature, int speed) {
            this.points.add(new HeatPoint(temperature, speed));
            return this;
        }

        public int size() {
            return points.size();
        }

        @ZenMethod
        public HeatingProfile build() {
            return new HeatingProfile(points.toArray(new HeatPoint[0]));
        }
    }

    private final HeatPoint[] points;

    private HeatingProfile(HeatPoint[] points) {
        int lastTemperature = 30;
        if (points.length == 0) {
            throw new IllegalArgumentException("Heating profile must not be empty");
        }
        for (HeatPoint point : points) {
            if (point.maxTemperature <= lastTemperature) {
                throw new IllegalArgumentException("Heating profile must be monotonous, got " + point.maxTemperature
                        + " after " + lastTemperature);
            }
            lastTemperature = point.maxTemperature;
        }
        this.points = points;
    }

    public int getSpeedAtTemperature(int temperature) {
        for (HeatPoint p : points) {
            if (p.maxTemperature >= temperature) {
                return p.speed;
            }
        }
        ProdigyTech.logger.warn(
                "Attempted to get heating speed at temperature {} where the maximum heat is {}",
                temperature,
                points[points.length - 1].maxTemperature);
        return points[points.length - 1].speed;
    }

    public int getMaxTemperature() {
        return points[points.length - 1].maxTemperature;
    }
}
