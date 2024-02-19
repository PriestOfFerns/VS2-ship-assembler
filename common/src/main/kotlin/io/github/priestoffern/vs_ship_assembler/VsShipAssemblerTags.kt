package io.github.priestoffern.vs_ship_assembler

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object VsShipAssemblerTags {

    val FORBIDDEN_ASSEMBLE: TagKey<Block> = TagKey.create(
        Registry.BLOCK_REGISTRY,
        ResourceLocation(VsShipAssemblerMod.MOD_ID, "forbidden_assemble")
    )


}