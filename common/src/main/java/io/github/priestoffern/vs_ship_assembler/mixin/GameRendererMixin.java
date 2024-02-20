package io.github.priestoffern.vs_ship_assembler.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.priestoffern.vs_ship_assembler.rendering.Renderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.priestoffern.vs_ship_assembler.rendering.RendererKt.renderData;


@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Camera mainCamera;
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0 ))
    void vsshipassembler_postWorldRender(float partialTicks, long finishTimeNano, PoseStack matrixStack, CallbackInfo ci) {
        minecraft.getProfiler().push("vsshipassembler_rendering_phase");
        renderData(matrixStack, mainCamera);
        minecraft.getProfiler().pop();
    }
}