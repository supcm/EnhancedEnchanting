package net.supcm.enhancedenchanting.client.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.supcm.enhancedenchanting.common.block.entity.ThoughtLoomTile;

public class ThoughtLoomTileRenderer extends TileEntityRenderer<ThoughtLoomTile> {
    public ThoughtLoomTileRenderer(TileEntityRendererDispatcher manager) { super(manager); }
    @Override public void render(ThoughtLoomTile te, float partialTicks, MatrixStack ms,
                                 IRenderTypeBuffer buffer, int light, int overlay) {
        ItemStack stack = te.handler.getStackInSlot(0);
        ItemStack stack1 = te.handler.getStackInSlot(1);
        ItemStack stack2 = te.handler.getStackInSlot(2);
        ItemStack stack3 = te.handler.getStackInSlot(3);
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5, 1.25 + 0.015 * MathHelper.cos(0.15f * te.getLevel().getGameTime()), 0.5);
            ms.mulPose(Vector3f.YP.rotation(te.getLevel().getGameTime() / 24.8525f));
            ms.scale(0.85f, 0.85f, 0.85f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, light,
                            overlay, ms, buffer);
            ms.popPose();
        }
        if(!stack1.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, .5F, 0.5F);
            ms.mulPose(Vector3f.YP.rotationDegrees((float)-(360 + 0.85*te.getLevel().getGameTime())));
            ms.translate(0.95F, 0F, 0.25F);
            ms.mulPose(Vector3f.YP.rotation(90f));
            ms.scale(0.65f, 0.65f, 0.65f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack1, ItemCameraTransforms.TransformType.GROUND, light,
                            overlay, ms, buffer);
            ms.popPose();
        }
        if(!stack2.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, .65F, 0.5F);
            ms.mulPose(Vector3f.YP.rotationDegrees((float)-(360 + 0.65*te.getLevel().getGameTime())));
            ms.translate(1.225F, 0F, 0.25F);
            ms.mulPose(Vector3f.YP.rotation(90f));
            ms.scale(0.65f, 0.65f, 0.65f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack2, ItemCameraTransforms.TransformType.GROUND, light,
                            overlay, ms, buffer);
            ms.popPose();
        }
        if(!stack3.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, .85F, 0.5F);
            ms.mulPose(Vector3f.YP.rotationDegrees((float)-(360 + 0.45*te.getLevel().getGameTime())));
            ms.translate(1.425F, 0F, 0.25F);
            ms.mulPose(Vector3f.YP.rotation(90f));
            ms.scale(0.65f, 0.65f, 0.65f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack3, ItemCameraTransforms.TransformType.GROUND, light,
                            overlay, ms, buffer);
            ms.popPose();
        }
    }
}
