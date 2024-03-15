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
    var toRender = mutableListOf<RenderingData>()

    fun addRender(renderData: RenderingData): RenderingData {
        renderData.Id = CurrentId;
        CurrentId++

        toRender.add(renderData);
        return renderData;
    }

    fun removeRender(renderData: RenderingData) {
        toRender.remove(renderData);


    }

    fun removeRenderOfType(renderData: RenderingData) {
        val clone = toRender.toMutableList(); // This is an awful way to do this, but I can't think of a different way
        clone.removeIf { it.type==renderData.type }
        toRender=clone;
    }
}


interface RenderingData {
    var Id: Long;
    var type: String;
    fun renderData(poseStack: PoseStack, camera: Camera)
}
