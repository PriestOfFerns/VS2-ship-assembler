package io.github.priestoffern.vs_ship_assembler.util


import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.ticks.ScheduledTick


object GameUtility {



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
        level.setBlockAndUpdate(pos,level.getBlockState(pos)) //markAndNotifyBlock(pos, chunk, level.getBlockState(pos), level.getBlockState(pos), 3, 512)
    }

    fun triggerClientSync(level: Level, pos: BlockPos?) {
        val state = level.getBlockState(pos)
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL)
    }



}