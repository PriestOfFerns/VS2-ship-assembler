package io.github.priestoffern.vs_ship_assembler.util

import de.m_marvin.unimat.impl.Quaterniond
import de.m_marvin.univec.impl.Vec3d
import io.github.priestoffern.vs_ship_assembler.util.PhysicUtility.isSolidContraptionBlock
import io.github.priestoffern.vs_ship_assembler.util.PhysicUtility.teleportContraption
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld


object PhysicHandlerCapability {

    fun createContraptionAt(level: Level, contraptionPosition: ContraptionPosition, scale: Double): BlockPos? {
        assert(level is ServerLevel) { "Can't manage contraptions on client side!" }

        // Get parent ship (if existing)
        val parentContraption: Ship? =
            (level as ServerLevel).getShipManagingPos(contraptionPosition.GetPosition().writeTo(Vector3d()))

        // Apply parent ship translation if available
        if (parentContraption != null) {
            contraptionPosition.toWorldPosition(parentContraption.transform)
        }

        // Create new contraption
        val dimensionId: String = level.dimensionId
        val newContraption: Ship = (level as ServerLevel).server.shipObjectWorld
            .createNewShipAtBlock(contraptionPosition.positionJOMLi, false, scale, dimensionId)

        // Stone for safety reasons
        val pos2: BlockPos = PhysicUtility.toContraptionBlockPos(
            newContraption.transform,
            MathUtility.toBlockPos(contraptionPosition.GetPosition())
        )
        level!!.setBlock(pos2, Blocks.STONE.defaultBlockState(), 3)

        // Teleport ship to final destination
        (level as ServerLevel).server.shipObjectWorld
            .teleportShip(newContraption as ServerShip, contraptionPosition.toTeleport())
        return pos2
    }

    fun assembleToContraption(level: Level, blocks: List<BlockPos>, removeOriginal: Boolean, scale: Double): Boolean {
        assert(level is ServerLevel) { "Can't manage contraptions on client side!" }
        val sLevel: ServerLevel = level as ServerLevel
        if (blocks.isEmpty()) {
            return false
        }

        var structureCornerMin: BlockPos = blocks[0]
        var structureCornerMax: BlockPos = blocks[0]
        var hasSolids = false

        // Calculate bounds of the area containing all blocks adn check for solids and invalid blocks
        for (itPos in blocks) {
            if (isSolidContraptionBlock(level.getBlockState(itPos))) {
                structureCornerMin = MathUtility.getMinCorner(structureCornerMin, itPos)
                structureCornerMax = MathUtility.getMaxCorner(structureCornerMax, itPos)
                hasSolids = true
            }
        }
        if (!hasSolids) return false

        // Create new contraption at center of bounds
        val contraptionWorldPos: Vec3d = MathUtility.getMiddle(structureCornerMin, structureCornerMax)
        val contraptionPosition = ContraptionPosition(Quaterniond(Vec3d(0.0, 1.0, 1.0), 0.0), contraptionWorldPos, null)
        val contraptionBlockPos: BlockPos = createContraptionAt(level, contraptionPosition, scale)!!
        val contraption: Ship = sLevel.getShipManagingPos(contraptionBlockPos)!!

        // Copy blocks and check if the center block got replaced (is default a stone block)
        var centerBlockReplaced = false
        for (itPos in blocks) {
            val relative: BlockPos = itPos!!.subtract(MathUtility.toBlockPos(contraptionWorldPos))
            val shipPos: BlockPos = contraptionBlockPos.offset(relative)
            GameUtility.copyBlock(level, itPos, shipPos)
            if (relative.equals(BlockPos.ZERO)) centerBlockReplaced = true
        }

        // If center block got not replaced, remove the stone block
        if (!centerBlockReplaced) {
            level.setBlock(contraptionBlockPos, Blocks.AIR.defaultBlockState(), 3)
        }

        // Remove original blocks
        if (removeOriginal) {
            for (itPos in blocks) {
                if (itPos != null) {
                    GameUtility.removeBlock(level, itPos)
                }
            }
        }

        // Trigger updates on both contraptions
        for (itPos in blocks) {
            val relative: BlockPos = itPos!!.subtract(MathUtility.toBlockPos(contraptionWorldPos))
            val shipPos: BlockPos = contraptionBlockPos.offset(relative)
            GameUtility.triggerUpdate(level, itPos)
            GameUtility.triggerUpdate(level, shipPos)
        }

        // Set the final position gain, since the contraption moves slightly if blocks are added
        if (contraption != null) {
            teleportContraption(level as ServerLevel, contraption as ServerShip, contraptionPosition, true)
        }
        return true
    }

}