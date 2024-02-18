package io.github.priestoffern.vs_ship_assembler

import dev.architectury.registry.CreativeTabRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier

object VsShipAssemblerItems {
    val ITEMS = DeferredRegister.create(VsShipAssemblerMod.MOD_ID, Registry.ITEM_REGISTRY)
    val TAB: CreativeModeTab = CreativeTabRegistry.create(
        ResourceLocation(
            VsShipAssemblerMod.MOD_ID,
            "vs_ship_assembler_tab"
        )
    ) {ItemStack(VsShipAssemblerItems.SHIP_ASSEMBLER.get())}


    var SHIP_ASSEMBLER: RegistrySupplier<Item> = ITEMS.register("status_goggles") { ShipAssemblerItem(Item.Properties().tab(
        TAB).stacksTo(1)) }
    fun register() {
        ITEMS.register()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}