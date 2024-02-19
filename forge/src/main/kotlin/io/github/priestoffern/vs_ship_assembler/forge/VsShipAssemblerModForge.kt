package io.github.priestoffern.vs_ship_assembler.forge

import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerMod
import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerMod.init
import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerMod.initClient
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(VsShipAssemblerMod.MOD_ID)
class VsShipAssemblerModForge {
    init {
        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }

        EventBuses.registerModEventBus(VsShipAssemblerMod.MOD_ID, MOD_BUS)

        init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
        initClient()
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
