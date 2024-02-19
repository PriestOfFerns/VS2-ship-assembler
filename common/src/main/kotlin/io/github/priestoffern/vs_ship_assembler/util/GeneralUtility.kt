package io.github.priestoffern.vs_ship_assembler.util

import de.m_marvin.unimat.impl.Quaterniond
import de.m_marvin.unimat.impl.Quaternionf
import de.m_marvin.univec.impl.Vec3d
import de.m_marvin.univec.impl.Vec3f
import de.m_marvin.univec.impl.Vec3i
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.ticks.ScheduledTick


object GeneralUtility {

    fun setBlock(level: Level, pos: BlockPos, state: BlockState?) {
        val chunk = level.getChunk(pos) as LevelChunk
        val section = chunk.getSection(chunk.getSectionIndex(pos.y))
        val oldState = level.getBlockState(pos)
        section.setBlockState(pos.x and 15, pos.y and 15, pos.z and 15, state)
        PhysicUtility.triggerBlockChange(level, pos, oldState, state)
    }

    fun removeBlock(level: Level, pos: BlockPos) {
        level.removeBlockEntity(pos)
        setBlock(level, pos, Blocks.AIR.defaultBlockState())
    }

    fun copyBlock(level: Level, from: BlockPos?, to: BlockPos) {
        val state = level.getBlockState(from)
        val blockentity = level.getBlockEntity(from)
        setBlock(level, to, state)

        // Transfer pending schedule-ticks
        if (level.blockTicks.hasScheduledTick(from, state.block)) {
            level.blockTicks.schedule(ScheduledTick<Block?>(state.block, to, 0, 0))
        }

        // Transfer block-entity data
        if (state.hasBlockEntity() && blockentity != null) {
            val data: CompoundTag = blockentity.saveWithId()
            level.setBlockEntity(blockentity)
            val newBlockentity = level.getBlockEntity(to)
            newBlockentity?.load(data)
        }
    }

    fun triggerUpdate(level: Level, pos: BlockPos?) {
        val chunk = level.getChunkAt(pos)
        level.sendBlockUpdated (pos, level.getBlockState(pos), level.getBlockState(pos), 3) //markAndNotifyBlock(pos, chunk, level.getBlockState(pos), level.getBlockState(pos), 3, 512)
        level.updateNeighborsAt(pos,level.getBlockState(pos).block)
    }
    fun toBlockPos(x: Double, y: Double, z: Double): BlockPos {
        return BlockPos(Mth.floor(x), Math.floor(y).toInt(), Math.floor(z).toInt())
    }

    fun toBlockPos(vec: Vec3d): BlockPos {
        return toBlockPos(vec.x, vec.y, vec.z)
    }

    fun getVecDirection(v: Vec3i): Direction {
        var axis = Direction.Axis.X
        if (v.y() != 0) axis = Direction.Axis.Y
        if (v.z() != 0) axis = Direction.Axis.Z
        val direction = if (v.x + v.y + v.z > 0) Direction.AxisDirection.POSITIVE else Direction.AxisDirection.NEGATIVE
        return Direction.fromAxisAndDirection(axis, direction)
    }

    fun getMinCorner(pos1: BlockPos, pos2: BlockPos): BlockPos {
        return BlockPos(
            Math.min(pos1.x, pos2.x),
            Math.min(pos1.y, pos2.y),
            Math.min(pos1.z, pos2.z)
        )
    }

    fun getMaxCorner(pos1: BlockPos, pos2: BlockPos): BlockPos {
        return BlockPos(
            Math.max(pos1.x, pos2.x),
            Math.max(pos1.y, pos2.y),
            Math.max(pos1.z, pos2.z)
        )
    }




    fun getMiddle(pos1: BlockPos, pos2: BlockPos): Vec3d {
        val middleX = Math.min(pos1.x, pos2.x).toDouble() + (Math.max(pos1.x, pos2.x) - Math.min(
            pos1.x,
            pos2.x
        ) + 1).toDouble() / 2.0
        val middleY = Math.min(pos1.y, pos2.y).toDouble() + (Math.max(pos1.y, pos2.y) - Math.min(
            pos1.y,
            pos2.y
        ) + 1).toDouble() / 2.0
        val middleZ = Math.min(pos1.z, pos2.z).toDouble() + (Math.max(pos1.z, pos2.z) - Math.min(
            pos1.z,
            pos2.z
        ) + 1).toDouble() / 2.0
        return Vec3d(middleX, middleY, middleZ)
    }

    fun getMiddle(pos1: Vec3d, pos2: Vec3d): Vec3d {
        val middleX = Math.min(pos1.getX(), pos2.getX()) + (Math.max(pos1.getX(), pos2.getX()) - Math.min(
            pos1.getX(),
            pos2.getX()
        )) / 2.0
        val middleY = Math.min(pos1.getY(), pos2.getY()) + (Math.max(pos1.getY(), pos2.getY()) - Math.min(
            pos1.getY(),
            pos2.getY()
        )) / 2.0
        val middleZ = Math.min(pos1.getZ(), pos2.getZ()) + (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(
            pos1.getZ(),
            pos2.getZ()
        )) / 2.0
        return Vec3d(middleX, middleY, middleZ)
    }









    fun rotatePoint(point: Vec3d, axis: Vec3d?, angle: Float, degrees: Boolean): Vec3d {
        var angle = angle
        if (degrees) angle = Math.toRadians(angle.toDouble()).toFloat()
        val quat = Quaterniond(axis, angle.toDouble())
        return point.transform(quat)
    }

    fun rotatePoint(point: Vec3f, axis: Vec3f?, angle: Float, degrees: Boolean): Vec3f {
        var angle = angle
        if (degrees) angle = Math.toRadians(angle.toDouble()).toFloat()
        val quat = Quaternionf(axis, angle)
        return point.transform(quat)
    }

    fun rotatePoint(point: Vec3i?, axis: Vec3f?, angle: Float, degrees: Boolean): Vec3i {
        var angle = angle
        if (degrees) angle = Math.toRadians(angle.toDouble()).toFloat()
        val quat = Quaternionf(axis, angle)
        val transform = Vec3f(point).transform(quat)
        return Vec3i(Math.round(transform.x), Math.round(transform.y), Math.round(transform.z))
    }




}