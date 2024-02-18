package io.github.priestoffern.vs_ship_assembler

object VsShipAssemblerMod {
    const val MOD_ID = "vs_ship_assembler"

    @JvmStatic
    fun init() {


        VsShipAssemblerItems.register()
    }

    @JvmStatic
    fun initClient() {
    }
}
