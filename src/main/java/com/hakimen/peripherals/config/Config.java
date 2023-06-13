package com.hakimen.peripherals.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public final static ForgeConfigSpec.Builder commonConfigBuilder = new ForgeConfigSpec.Builder();
    public final static ForgeConfigSpec commonConfigSpec;
    public final static ForgeConfigSpec.DoubleValue mobDataCaptureChance;
    public final static ForgeConfigSpec.IntValue conversionRate;
    public final static ForgeConfigSpec.IntValue extractRate;

    public final static ForgeConfigSpec.IntValue maxMagnetRange;
    public final static ForgeConfigSpec.BooleanValue magnetConsumesFuel;

    static {
        commonConfigBuilder.push("Common Configs for More Peripherals");
        mobDataCaptureChance = commonConfigBuilder.comment("Chance to get a mobs data when hit by a Spawner Card")
                .defineInRange("mobDataCaptureChance",0.9f,0f,1f);

        conversionRate = commonConfigBuilder.comment("Conversion Rate for the Induction charger (1 RF = x Turtle Fuel")
                .defineInRange("conversionRate",1,0,15);

        extractRate = commonConfigBuilder.comment("Extraction Rate for each tick")
                .defineInRange("extractRate",4,1,100);

        maxMagnetRange = commonConfigBuilder.comment("Max Range for Magnetic Turtles")
                .defineInRange("maxRangeMagnet",8,1,32);

        magnetConsumesFuel = commonConfigBuilder.comment("Does Magnetic Turtle uses fuel")
                        .define("magnetConsumesFuel", true);
        commonConfigBuilder.pop();
        commonConfigSpec = commonConfigBuilder.build();
    }

}
