package io.github.priestoffern.vs_ship_assembler.rendering

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Matrix4f
import de.m_marvin.univec.impl.Vec3d
import net.minecraft.client.Camera
import net.minecraft.client.renderer.GameRenderer
import org.lwjgl.opengl.GL11
import java.awt.Color
class SelectionZoneRenderer() : RenderingData {

    var point0 = Vec3d();
    var point1 = Vec3d();
    var color: Color = Color(0)
    override var Id: Long = 0
    override var type: String = "Ship_Assembler_Selection_Zone"
    constructor(
                point0: Vec3d,
                point1: Vec3d,
                color: Color,
    ): this() {
        this.point0 = point0
        this.point1 = point1
        this.color = color

    }

    override fun renderData(poseStack: PoseStack, camera: Camera) {


        val tesselator = Tesselator.getInstance()
        val vBuffer = tesselator.builder

        RenderSystem.enableDepthTest()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)
        RenderSystem.depthMask(true)
        RenderSystem.setShader(GameRenderer::getPositionColorShader)

//        val light = LightTexture.pack(level!!.getBrightness(LightLayer.BLOCK, point1.toBlockPos()), level!!.getBrightness(LightLayer.SKY, point1.toBlockPos()))

        val light = Int.MAX_VALUE

        vBuffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP)

        poseStack.pushPose()

        val cameraPos = camera.position

        val Add1 = Vec3d(if (point0.x>=point1.x) 1.0 else 0.0, if (point0.y>=point1.y) 1.0 else 0.0, if (point0.z>=point1.z) 1.0 else 0.0)
        val Add2 = Vec3d(if (point1.x>point0.x) 1.0 else 0.0, if (point1.y>point0.y) 1.0 else 0.0, if (point1.z>point0.z) 1.0 else 0.0)

        val tpos1 = Vec3d(point0.x-cameraPos.x,point0.y-cameraPos.y,point0.z-cameraPos.z).add(Add1)
        val tpos2 = Vec3d(point1.x-cameraPos.x,point1.y-cameraPos.y,point1.z-cameraPos.z).add(Add2)

        val matrix = poseStack.last().pose()
        makeBox(
            vBuffer, matrix,
            color.red, color.green, color.blue, color.alpha, light,
            tpos1, tpos2
        )

        tesselator.end()

        poseStack.popPose()

        
    }

    fun tof(n: Double) = n.toFloat()
    fun makeBox(buf: VertexConsumer, matrix: Matrix4f,
                    r: Int, g: Int, b: Int, a: Int, lightmapUV: Int,
                    A: Vec3d, B:Vec3d
    ) {
        buf.vertex(matrix,  tof(A.x), tof(A.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(A.x), tof(A.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()

        buf.vertex(matrix,  tof(A.x), tof(A.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(A.x), tof(B.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()

        buf.vertex(matrix,  tof(A.x), tof(A.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(B.x), tof(A.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()


        buf.vertex(matrix,  tof(A.x), tof(A.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(B.x), tof(A.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()

        buf.vertex(matrix,  tof(A.x), tof(A.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(A.x), tof(B.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()

        buf.vertex(matrix,  tof(A.x), tof(B.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(B.x), tof(B.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()


        buf.vertex(matrix,  tof(A.x), tof(B.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(A.x), tof(B.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()

        buf.vertex(matrix,  tof(A.x), tof(B.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(B.x), tof(B.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()

        buf.vertex(matrix,  tof(B.x), tof(B.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(B.x), tof(B.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()


        buf.vertex(matrix,  tof(B.x), tof(A.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(B.x), tof(B.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()

        buf.vertex(matrix,  tof(B.x), tof(A.y), tof(A.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(B.x), tof(A.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()

        buf.vertex(matrix,  tof(B.x), tof(A.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
        buf.vertex(matrix,  tof(B.x), tof(B.y), tof(B.z)).color(r, g, b, a).uv2(lightmapUV).endVertex()
    }


}