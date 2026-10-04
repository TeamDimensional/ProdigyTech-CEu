package lykrast.prodigytech.common.capability;

import javax.annotation.Nonnull;
import lykrast.prodigytech.common.util.HeatingProfile;

public abstract class HotAirProfileAeroheater extends HotAirAeroheater {

    protected HeatingProfile heating;
    protected HeatingProfile cooling;
    protected int defaultCoolingSpeed = 5;

    public HotAirProfileAeroheater() {
        super(30);
    }

    public HotAirProfileAeroheater(HeatingProfile heating, HeatingProfile cooling) {
        super(heating.getMaxTemperature());
        this.heating = heating;
        this.cooling = cooling;
    }

    protected void setHeatingProfile(@Nonnull HeatingProfile heating, @Nonnull HeatingProfile cooling) {
        this.maxTemperature = heating.getMaxTemperature();
        this.heating = heating;
        this.cooling = cooling;
    }

    protected void stopHeating() {
        this.maxTemperature = 30;
        this.heating = null;
        // Persist the old cooling profile to make sure it's currently applied
    }

    public void tick() {
        if (heating != null) {
            raiseTemperature();
        } else {
            lowerTemperature();
        }
    }

    @Override
    protected void resetRaiseClock() {
        if (this.heating != null) temperatureClock = this.heating.getSpeedAtTemperature(temperature);
    }

    @Override
    protected void resetLowerClock() {
        if (this.cooling != null && this.cooling.getMaxTemperature() >= temperature)
            temperatureClock = this.cooling.getSpeedAtTemperature(temperature);
        else temperatureClock = defaultCoolingSpeed;
    }
}
