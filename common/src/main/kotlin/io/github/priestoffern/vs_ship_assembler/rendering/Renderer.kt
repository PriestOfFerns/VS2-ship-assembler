package io.github.priestoffern.vs_ship_assembler.rendering

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera


fun renderData(poseStack: PoseStack, camera: Camera) {

    val currentlyRendering = Renderer.toRender.toMutableList() // Have to do this to prevent ConcurrentModificationException
    for (data in currentlyRendering) {
        if (data!=null) data.renderData(poseStack, camera)

    }

}

object Renderer {
    var CurrentId:Long = 0;
    val toRender = mutableListOf<RenderingData>()

    fun addRender(renderData: RenderingData): RenderingData {
        renderData.Id = CurrentId;
        CurrentId++

        toRender.add(renderData);
        return renderData;
    }

    fun removeRender(renderData: RenderingData) {
        toRender.remove(renderData);

    }
}


interface RenderingData {
    var Id: Long;
    fun renderData(poseStack: PoseStack, camera: Camera)
}
