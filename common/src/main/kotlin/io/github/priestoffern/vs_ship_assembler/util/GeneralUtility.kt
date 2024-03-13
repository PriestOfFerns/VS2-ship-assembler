package io.github.priestoffern.vs_ship_assembler.util

import de.m_marvin.univec.impl.Vec3d
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

private val AIR = Blocks.AIR.defaultBlockState()
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

    fun updateBlock(level: Level, fromPos: BlockPos, toPos: BlockPos, toState: BlockState) {

        // 75 = flag 1 (block update) & flag 2 (send to clients) + flag 8 (force rerenders)
        val flags = 11

        //updateNeighbourShapes recurses through nearby blocks, recursionLeft is the limit
        val recursionLeft = 511

        level.setBlocksDirty(fromPos, toState, AIR)
        level.sendBlockUpdated(fromPos, toState, AIR, flags)
        level.blockUpdated(fromPos, AIR.block)
        // This handles the update for neighboring blocks in worldspace
        AIR.updateIndirectNeighbourShapes(level, fromPos, flags, recursionLeft - 1)
        AIR.updateNeighbourShapes(level, fromPos, flags, recursionLeft)
        AIR.updateIndirectNeighbourShapes(level, fromPos, flags, recursionLeft)
        //This updates lighting for blocks in worldspace
        level.chunkSource.lightEngine.checkBlock(fromPos)

        level.setBlocksDirty(toPos, AIR, toState)
        level.sendBlockUpdated(toPos, AIR, toState, flags)
        level.blockUpdated(toPos, toState.block)
        if (!level.isClientSide && toState.hasAnalogOutputSignal()) {
            level.updateNeighbourForOutputSignal(toPos, toState.block)
        }
        //This updates lighting for blocks in shipspace
        level.chunkSource.lightEngine.checkBlock(toPos)
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
    




}