package io.github.priestoffern.vs_ship_assembler.rendering

import com.google.common.base.Supplier
import com.mojang.blaze3d.vertex.PoseStack
import dev.architectury.event.events.client.ClientTooltipEvent.Render

import net.minecraft.client.Camera
import net.minecraft.client.Minecraft

import org.valkyrienskies.mod.common.shipObjectWorld


fun renderData(poseStack: PoseStack, camera: Camera) {
    if (Renderer.rendering) return // Sometimes renderData() seems to go off before removeAll() is done, causing a crash. No clue why that happens though?
    Renderer.rendering = true;
    for (data in Renderer.toRender) {

        data.renderData(poseStack, camera)

    }
    Renderer.toRender.removeAll(Renderer.toRemove)
    Renderer.rendering = false;
}

object Renderer {
    var CurrentId:Long = 0;
    val toRender = mutableListOf<RenderingData>()
    val toRemove = mutableListOf<RenderingData>()

    var rendering = false;
    fun addRender(renderData: RenderingData): RenderingData {
        renderData.Id = CurrentId;
        CurrentId++

        toRender.add(renderData);
        return renderData;
    }

    fun removeRender(renderData: RenderingData) {
        toRemove.add(renderData);

    }
}


interface RenderingData {
    var Id: Long;
    fun renderData(poseStack: PoseStack, camera: Camera)
}
