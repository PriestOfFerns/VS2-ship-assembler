package io.github.priestoffern.vs_ship_assembler.util

import de.m_marvin.unimat.impl.Quaterniond
import de.m_marvin.unimat.impl.Quaternionf
import de.m_marvin.univec.impl.Vec2f
import de.m_marvin.univec.impl.Vec3d
import de.m_marvin.univec.impl.Vec3f
import de.m_marvin.univec.impl.Vec3i
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import java.util.*
import java.util.stream.Stream


object MathUtility {
    const val ANGULAR_VELOCITY_TO_ROTATIONS_PER_SECOND = 180.0 / Math.PI / 360.0
    const val ROTATIONS_PER_SECOND_TO_ANGULAR_VELOCITY = 360 / (180.0 / Math.PI)
    fun getPosRelativeFacing(pos1: BlockPos?, pos2: Block?): Direction {
        return getVecDirection(Vec3i.fromVec(pos2).sub(Vec3i.fromVec(pos1)))
    }

    fun toBlockPos(x: Double, y: Double, z: Double): BlockPos {
        return BlockPos(Mth.floor(x), Math.floor(y).toInt(), Math.floor(z).toInt())
    }

    fun toBlockPos(vec: Vec3f): BlockPos {
        return toBlockPos(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())
    }

    fun toBlockPos(vec: Vec3i): BlockPos {
        return BlockPos(vec.x, vec.y, vec.z)
    }

    fun toBlockPos(vec: Vec3d): BlockPos {
        return toBlockPos(vec.x, vec.y, vec.z)
    }

    fun clamp(v: Int, min: Int, max: Int): Int {
        if (v < min) return min
        return if (v > max) max else v
    }

    fun clamp(v: Float, min: Float, max: Float): Float {
        if (v < min) return min
        return if (v > max) max else v
    }

    fun clamp(v: Double, min: Double, max: Double): Double {
        if (v < min) return min
        return if (v > max) max else v
    }

    fun clampToDegree(angle: Float): Float {
        return angle % 360
    }

    fun getDirectionsAround(nodeAxis: Direction.Axis): Array<Direction?> {
        return Stream.of(*Direction.values()).filter { d: Direction -> d.axis !== nodeAxis }
            .toArray { i: Int ->
                arrayOfNulls(
                    i
                )
            }
    }

    fun getDirectionVec(d: Direction): Vec3i {
        return Vec3i(d.stepX, d.stepY, d.stepZ)
    }

    fun getVecDirection(v: Vec3i): Direction {
        var axis = Direction.Axis.X
        if (v.y() != 0) axis = Direction.Axis.Y
        if (v.z() != 0) axis = Direction.Axis.Z
        val direction = if (v.x + v.y + v.z > 0) Direction.AxisDirection.POSITIVE else Direction.AxisDirection.NEGATIVE
        return Direction.fromAxisAndDirection(axis, direction)
    }

    fun getVecDirection(v: Vec3d): Direction {
        var v = v
        v = v.normalize()
        val v2 = v.abs()
        return if (v2.x > v2.y && v2.x > v2.z) {
            if (v.x > 0) Direction.EAST else Direction.WEST
        } else if (v2.y > v2.x && v2.y > v2.z) {
            if (v.y > 0) Direction.UP else Direction.DOWN
        } else if (v2.z > v2.y && v2.z > v2.y) {
            if (v.z > 0) Direction.SOUTH else Direction.NORTH
        } else {
            Direction.NORTH
        }
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

    fun getMinCorner(pos1: Vec3d, pos2: Vec3d): Vec3d {
        return Vec3d(
            Math.min(pos1.getX(), pos2.getX()),
            Math.min(pos1.getY(), pos2.getY()),
            Math.min(pos1.getZ(), pos2.getZ())
        )
    }

    fun getMaxCorner(pos1: Vec3d, pos2: Vec3d): Vec3d {
        return Vec3d(
            Math.max(pos1.getX(), pos2.getX()),
            Math.max(pos1.getY(), pos2.getY()),
            Math.max(pos1.getZ(), pos2.getZ())
        )
    }

    fun getMiddleBlock(pos1: BlockPos, pos2: BlockPos): BlockPos {
        val middleX = Math.min(pos1.x, pos2.x) + (Math.max(pos1.x, pos2.x) - Math.min(pos1.x, pos2.x)) / 2
        val middleY = Math.min(pos1.y, pos2.y) + (Math.max(pos1.y, pos2.y) - Math.min(pos1.y, pos2.y)) / 2
        val middleZ = Math.min(pos1.z, pos2.z) + (Math.max(pos1.z, pos2.z) - Math.min(pos1.z, pos2.z)) / 2
        return BlockPos(middleX, middleY, middleZ)
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

    fun getMiddle(pos1: Vec3i, pos2: Vec3i): Vec3i {
        val middleX = (Math.min(pos1.getX(), pos2.getX()) + (Math.max(pos1.getX(), pos2.getX()) - Math.min(
            pos1.getX(),
            pos2.getX()
        )) / 2.0).toInt()
        val middleY = (Math.min(pos1.getY(), pos2.getY()) + (Math.max(pos1.getY(), pos2.getY()) - Math.min(
            pos1.getY(),
            pos2.getY()
        )) / 2.0).toInt()
        val middleZ = (Math.min(pos1.getZ(), pos2.getZ()) + (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(
            pos1.getZ(),
            pos2.getZ()
        )) / 2.0).toInt()
        return Vec3i(middleX, middleY, middleZ)
    }

    fun directionHoriziontalAngleDegrees(direction: Direction?): Double {
        return when (direction) {
            Direction.NORTH -> 0.0
            Direction.SOUTH -> 180.0
            Direction.EAST -> -90.0
            Direction.WEST -> 90.0
            Direction.UP -> 90.0
            Direction.DOWN -> -90.0
            else -> 0.0
        }
    }

    fun directionVector(direction: Direction): Vec3i {
        val vector = Vec3i()
        when (direction.axis) {
            Direction.Axis.X -> vector.setX(1)
            Direction.Axis.Y -> vector.setY(1)
            Direction.Axis.Z -> vector.setZ(1)
        }
        return if (direction.axisDirection == Direction.AxisDirection.POSITIVE) vector else vector.mul(-1)
    }

    fun directionRelativeAngleDegrees(direction: Direction, origin: Direction?): Double {
        return directionVector(direction).angle(directionVector(direction))
    }

    fun rotatePoint(point: Vec3d, angle: Float, degrees: Boolean, axis: Direction.Axis?): Vec3d {
        var rotationAxis: Vec3d? = null
        when (axis) {
            Direction.Axis.X -> rotationAxis = Vec3d(1.0, 0.0, 0.0)
            Direction.Axis.Y -> rotationAxis = Vec3d(0.0, 1.0, 0.0)
            Direction.Axis.Z -> rotationAxis = Vec3d(0.0, 0.0, 1.0)
            else -> {}
        }
        return rotatePoint(point, rotationAxis, angle, degrees)
    }

    fun rotatePoint(point: Vec3f, angle: Float, degrees: Boolean, axis: Direction.Axis?): Vec3f {
        var rotationAxis: Vec3f? = null
        when (axis) {
            Direction.Axis.X -> rotationAxis = Vec3f(1f, 0f, 0f)
            Direction.Axis.Y -> rotationAxis = Vec3f(0f, 1f, 0f)
            Direction.Axis.Z -> rotationAxis = Vec3f(0f, 0f, 1f)
            else -> {}
        }
        return rotatePoint(point, rotationAxis, angle, degrees)
    }

    fun rotatePoint(point: Vec3i?, angle: Float, degrees: Boolean, axis: Direction.Axis?): Vec3i {
        var rotationAxis: Vec3f? = null
        when (axis) {
            Direction.Axis.X -> rotationAxis = Vec3f(1f, 0f, 0f)
            Direction.Axis.Y -> rotationAxis = Vec3f(0f, 1f, 0f)
            Direction.Axis.Z -> rotationAxis = Vec3f(0f, 0f, 1f)
            else -> {}
        }
        return rotatePoint(point, rotationAxis, angle, degrees)
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

    fun isInChunk(chunk: ChunkPos, block: BlockPos): Boolean {
        return chunk.minBlockX <= block.x && chunk.maxBlockX >= block.x && chunk.minBlockZ <= block.z && chunk.maxBlockZ >= block.z
    }

    fun getChunksOnLine(from: Vec2f, to: Vec2f): Set<ChunkPos> {
        val lineVec = to.copy().sub(from)
        val chunkOff = from.copy().module(16f)
        chunkOff.x = if (lineVec.x() < 0) -(16 - chunkOff.x()) else chunkOff.x()
        chunkOff.y = if (lineVec.y() < 0) -(16 - chunkOff.y()) else chunkOff.y()
        val worldOff = from.copy().sub(chunkOff)
        val lineRlativeTarget = to.copy().sub(worldOff)
        val insecsX = Math.floor((Math.abs(lineRlativeTarget.x()) / 16).toDouble()).toInt()
        val insecsZ = Math.floor((Math.abs(lineRlativeTarget.y()) / 16).toDouble()).toInt()
        val chunks: MutableSet<ChunkPos> = HashSet()
        chunks.add(ChunkPos(toBlockPos(from.x.toDouble(), 0.0, from.y.toDouble())))
        for (insecX in 1..insecsX) {
            var chunkX = (worldOff.x + insecX * if (lineVec.x() < 0) -16 else 16).toInt()
            if (lineVec.x() < 0) chunkX -= 1
            val chunkZ = (Math.abs(chunkX - from.x()) / Math.abs(lineVec.x()) * lineVec.y() + from.y()).toInt()
            chunks.add(ChunkPos(BlockPos(chunkX, 0, chunkZ)))
        }
        for (insecZ in 1..insecsZ) {
            var chunkZ = (worldOff.y + insecZ * if (lineVec.y() < 0) -16 else 16).toInt()
            if (lineVec.y() < 0) chunkZ -= 1
            val chunkX = (Math.abs(chunkZ - from.y()) / Math.abs(lineVec.y()) * lineVec.x() + from.x()).toInt()
            chunks.add(ChunkPos(BlockPos(chunkX, 0, chunkZ)))
        }
        return chunks
    }

    fun lineInfinityIntersection(lineA1: Vec3d, lineA2: Vec3d, lineB1: Vec3d, lineB2: Vec3d): Array<Vec3d> {
        val p43 = Vec3d(lineB2.x - lineB1.x, lineB2.y - lineB1.y, lineB2.z - lineB1.z)
        val p21 = Vec3d(lineA2.x - lineA1.x, lineA2.y - lineA1.y, lineA2.z - lineA1.z)
        val p13 = Vec3d(lineA1.x - lineB1.x, lineA1.y - lineB1.y, lineA1.z - lineB1.z)
        val d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z
        val d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z
        val d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z
        val d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z
        val denom = d2121 * d4343 - d4321 * d4321
        val d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z
        val numer = d1343 * d4321 - d1321 * d4343
        val mua = numer / denom
        val mub = (d1343 + d4321 * mua) / d4343
        val cl1 = Vec3(lineA1.x + mua * p21.x, lineA1.y + mua * p21.y, lineA1.z + mua * p21.z)
        val cl2 = Vec3(lineB1.x + mub * p43.x, lineB1.y + mub * p43.y, lineB1.z + mub * p43.z)
        return arrayOf(Vec3d.fromVec(cl1), Vec3d.fromVec(cl2))
    }

    fun isOnLine(point: Vec3d?, line1: Vec3d, line2: Vec3d, t: Double): Boolean {
        return line1.copy().sub(point).length() + line2.copy().sub(point).length() <= line1.copy().sub(line2)
            .length() + t
    }

    fun getHitPoint(lineA1: Vec3d, lineA2: Vec3d, lineB1: Vec3d, lineB2: Vec3d, tolerance: Double): Optional<Vec3d> {
        val shortesLine = lineInfinityIntersection(lineA1, lineA2, lineB1, lineB2)
        if (isOnLine(shortesLine[0], lineA1, lineA2, 0.1) && isOnLine(shortesLine[1], lineB1, lineB2, 0.1)) {
            if (shortesLine[0].copy().sub(shortesLine[1]).length() <= tolerance) return Optional.of(
                shortesLine[0]
            )
        }
        return Optional.empty()
    }

    fun doLinesCross(lineA1: Vec3d, lineA2: Vec3d, lineB1: Vec3d, lineB2: Vec3d, tolerance: Double): Boolean {
        val shortesLine = lineInfinityIntersection(lineA1, lineA2, lineB1, lineB2)
        return if (isOnLine(shortesLine[0], lineA1, lineA2, 0.1) && isOnLine(shortesLine[1], lineB1, lineB2, 0.1)) {
            shortesLine[0].copy().sub(shortesLine[1]).length() <= tolerance
        } else false
    }

    fun getPlayerPOVHitResult(
        pLevel: Level,
        pPlayer: Player,
        pFluidMode: ClipContext.Fluid?,
        reachDistance: Double
    ): BlockHitResult {
        val f = pPlayer.xRot
        val f1 = pPlayer.yRot
        val vec3 = pPlayer.eyePosition
        val f2 = Mth.cos(-f1 * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
        val f3 = Mth.sin(-f1 * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
        val f4 = -Mth.cos(-f * (Math.PI.toFloat() / 180f))
        val f5 = Mth.sin(-f * (Math.PI.toFloat() / 180f))
        val f6 = f3 * f4
        val f7 = f2 * f4
        val vec31 = vec3.add(
            f6.toDouble() * reachDistance,
            f5.toDouble() * reachDistance,
            f7.toDouble() * reachDistance
        )
        return pLevel.clip(ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, pFluidMode, pPlayer))
    }

    private val ENTITY_PREDICATE_CLICKEABLE = EntitySelector.NO_SPECTATORS.and { obj: Entity -> obj.isPickable }

}