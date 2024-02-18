package io.github.priestoffern.vs_ship_assembler.fabric;

import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class VsShipAssemblerModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();

        VsShipAssemblerMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            VsShipAssemblerMod.initClient();
        }
    }
}
