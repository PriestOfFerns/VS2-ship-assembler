package io.github.priestoffern.vs_ship_assembler.items

import de.m_marvin.univec.impl.Vec3d
import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerTags
import io.github.priestoffern.vs_ship_assembler.rendering.Renderer
import io.github.priestoffern.vs_ship_assembler.rendering.RenderingData
import io.github.priestoffern.vs_ship_assembler.rendering.SelectionZoneRenderer
import io.github.priestoffern.vs_ship_assembler.util.PhysicUtility
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TextComponent
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.phys.BlockHitResult
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft
import java.awt.Color
import java.lang.Math.*

class ShipAssemblerItem(properties: Properties): Item(properties) {

    var firstPosition: BlockPos? = null
    var secondPosition: BlockPos? = null
    var SelectionZone: RenderingData? = null

    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val clipResult = level.clip(
            ClipContext(
                (Vector3d(player.eyePosition.toJOML()).toMinecraft()),
                (player.eyePosition.toJOML()
                    .add(0.5, 0.5, 0.5)
                    .add(Vector3d(player.lookAngle.toJOML()).mul(10.0)) //distance
                        ).toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            )
        )
        //player.sendMessage(TextComponent(" ${clipResult.blockPos}"), Util.NIL_UUID)
        player.playSound(SoundType.AMETHYST_CLUSTER.placeSound, 1F, 1F)

        val pos = clipResult.blockPos


        makeSelection(level, player, interactionHand, pos)



        return super.use(level, player, interactionHand)
    }

    fun makeSelection(level: Level, player: Player, interactionHand: InteractionHand, pos: BlockPos){

        if (!level.isClientSide) {
            if (player.isShiftKeyDown and (level.getBlockState(pos).isAir)) {
                firstPosition = null
                secondPosition = null
                if (SelectionZone!=null) Renderer.removeRender(SelectionZone!!)
                SelectionZone = null;
                player.sendMessage(TextComponent("Selection reset"), Util.NIL_UUID)
            } else if (firstPosition == null) {
                if (level.getShipObjectManagingPos(pos) == null) {
                    firstPosition = pos
                    player.sendMessage(TextComponent("First pos selected"), Util.NIL_UUID)
                } else {
                    player.sendMessage(TextComponent("Selected position is on a ship!"), Util.NIL_UUID)
                }
            } else if (secondPosition == null) {
                if (level.getShipObjectManagingPos(pos) == null) {


                    if (SelectionZone!=null) Renderer.removeRender(SelectionZone!!)
                    SelectionZone = null;

                    secondPosition = pos
                    player.sendMessage(TextComponent("Second pos selected"), Util.NIL_UUID)
                    val SZ = SelectionZoneRenderer(Vec3d(firstPosition!!.x.toDouble(),
                        firstPosition!!.y.toDouble(), firstPosition!!.z.toDouble()),Vec3d(pos.x.toDouble(),pos.y.toDouble(),pos.z.toDouble()), Color.GREEN);
                    SelectionZone = Renderer.addRender(SZ)
                } else {
                    player.sendMessage(TextComponent("Selected position is on a ship!"), Util.NIL_UUID)
                }
            } else {
                val set : List<BlockPos> = buildList {
                    for (x in min(firstPosition!!.x, secondPosition!!.x)..max(firstPosition!!.x, secondPosition!!.x)) {
                        for (y in min(firstPosition!!.y, secondPosition!!.y)..max(firstPosition!!.y, secondPosition!!.y)) {
                            for (z in min(firstPosition!!.z, secondPosition!!.z)..max(firstPosition!!.z, secondPosition!!.z)) {

                                if (!level.getBlockState(BlockPos(x,y,z)).tags.anyMatch { it==VsShipAssemblerTags.FORBIDDEN_ASSEMBLE })
                                add(BlockPos(x,y,z))
                            }
                        }
                    }
                }

                if (set.size>0) {
                    PhysicUtility.assembleToContraption(level,set,true,1.0)
                    player.sendMessage(TextComponent("Assembled!"), Util.NIL_UUID)
                } else {
                    player.sendMessage(TextComponent("Failed to Assemble: Empty ship"), Util.NIL_UUID)
                }

                if (SelectionZone!=null) Renderer.removeRender(SelectionZone!!)
                SelectionZone = null;
                firstPosition = null
                secondPosition = null
            }
        }
    }


    @Override
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)

        if (isSelected && firstPosition!=null && secondPosition == null) {
            if (SelectionZone!=null) Renderer.removeRender(SelectionZone!!)
            SelectionZone = null;

            val res = raycast(level,entity as Player,ClipContext.Fluid.NONE)
            if (res!=null) {
                val SZ = SelectionZoneRenderer(Vec3d(firstPosition!!.x.toDouble(),
                    firstPosition!!.y.toDouble(), firstPosition!!.z.toDouble()),Vec3d(res.blockPos.x.toDouble(),res.blockPos.y.toDouble(),res.blockPos.z.toDouble()), Color.GREEN);
                SelectionZone = Renderer.addRender(SZ)
            }
        }
    }

    protected fun raycast(level: Level, player: Player, fluidMode: ClipContext.Fluid?): BlockHitResult? {
        val f = player.xRot
        val g = player.yRot
        val vec3 = player.eyePosition
        val h = Mth.cos(-g * 0.017453292f - 3.1415927f)
        val i = Mth.sin(-g * 0.017453292f - 3.1415927f)
        val j = -Mth.cos(-f * 0.017453292f)
        val k = Mth.sin(-f * 0.017453292f)
        val l = i * j
        val n = h * j
        val d = 5.0
        val vec32 = vec3.add(l.toDouble() * 5.0, k.toDouble() * 5.0, n.toDouble() * 5.0)
        return level.clip(ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, fluidMode, player))
    }
}