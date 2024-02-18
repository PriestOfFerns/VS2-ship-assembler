package io.github.priestoffern.vs_ship_assembler.util


import de.m_marvin.univec.impl.Vec3d
import de.m_marvin.univec.impl.Vec3i
import io.github.priestoffern.vs_ship_assembler.util.MathUtility.getDirectionVec
import io.github.priestoffern.vs_ship_assembler.util.MathUtility.getVecDirection
import io.github.priestoffern.vs_ship_assembler.util.MathUtility.toBlockPos
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import org.joml.Vector3d
import org.joml.Vector4d
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.constraints.VSConstraint
import org.valkyrienskies.mod.common.BlockStateInfo.onSetBlock
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import java.lang.reflect.InvocationTargetException
import java.util.function.BiFunction


object PhysicUtility {
    /* Naming and finding of contraptions */


    fun getContraptionOfBlock(level: Level?, shipBlockPos: BlockPos?): Ship? {
        if (shipBlockPos == null) return null
        return level.getShipManagingPos(shipBlockPos.x.toDouble(), shipBlockPos.y.toDouble(), shipBlockPos.z.toDouble())
    }


    fun toContraptionPos(contraption: ShipTransform, pos: Vec3d): Vec3d {
        val worldToShip = contraption.worldToShip
        if (worldToShip != null) {
            val transformPosition = worldToShip.transformPosition(pos.writeTo(Vector3d()))
            return Vec3d.fromVec(transformPosition)
        }
        return Vec3d(0.0, 0.0, 0.0)
    }

    fun toContraptionBlockPos(contraption: ShipTransform, pos: Vec3d): BlockPos {
        val position = toContraptionPos(contraption, pos)
        return toBlockPos(position)
    }

    fun toContraptionBlockPos(contraption: ShipTransform, pos: BlockPos?): BlockPos {
        return toContraptionBlockPos(contraption, Vec3d.fromVec(pos))
    }

    fun toWorldPos(contraption: ShipTransform, pos: Vec3d): Vec3d {
        val shipToWorld = contraption.shipToWorld
        if (shipToWorld != null) {
            val transformedPosition = shipToWorld.transformPosition(pos.writeTo(Vector3d()))
            return Vec3d.fromVec(transformedPosition)
        }
        return Vec3d(0.0, 0.0, 0.0)
    }

    fun toWorldPos(contraption: ShipTransform, pos: BlockPos?): Vec3d {
        return toWorldPos(contraption, Vec3d.fromVec(pos).addI(0.5, 0.5, 0.5))
    }



    fun teleportContraption(
        level: ServerLevel,
        contraption: ServerShip,
        position: ContraptionPosition,
        useGeometricCenter: Boolean
    ) {
        (level as ServerLevel).server.shipObjectWorld
            .teleportShip(contraption as ServerShip, position.toTeleport())
    }














    fun triggerBlockChange(level: Level?, pos: BlockPos?, prevState: BlockState?, newState: BlockState?) {
        onSetBlock(level!!, pos!!, prevState!!, newState!!)
    }

    fun isSolidContraptionBlock(state: BlockState?): Boolean {

        return true
    }



}