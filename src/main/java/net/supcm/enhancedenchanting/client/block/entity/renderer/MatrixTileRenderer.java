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
import net.supcm.enhancedenchanting.common.block.entity.MatrixTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

public class MatrixTileRenderer extends TileEntityRenderer<MatrixTile>{
    public MatrixTileRenderer(TileEntityRendererDispatcher manager) { super(manager); }

    @Override
    public void render(MatrixTile tile, float partialTicks, MatrixStack ms,
                       IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if(!tile.handler.getStackInSlot(1).isEmpty()) {
            ms.pushPose();
            renderSymbol(tile, ms, 0.045f, 0.5f,true, combinedLight, combinedOverlay, buffer);
            renderSymbol(tile, ms, 0.5f, 0.045f,false, combinedLight, combinedOverlay, buffer);
            renderSymbol(tile, ms, 1-0.045f, 0.5f,true, combinedLight, combinedOverlay, buffer);
            renderSymbol(tile, ms, 0.5f, 1-0.045f,false, combinedLight, combinedOverlay, buffer);
            ms.popPose();
        }
        if(!tile.handler.getStackInSlot(0).isEmpty()) {
            ItemStack stack = tile.handler.getStackInSlot(0);
            ms.pushPose();
            ms.translate(0.5, 1.23+0.02*MathHelper.sin(0.03f*tile.getLevel().getGameTime()), 0.5);
            ms.mulPose(Vector3f.XN.rotationDegrees(90f));
            ms.mulPose(Vector3f.ZN.rotationDegrees(tile.getLevel().getGameTime()/(0.625123f)));
            ms.scale(0.45f, 0.45f, 0.45f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                            combinedOverlay, ms, buffer);
            ms.popPose();
            if(tile.doRenderCrystal) {
                ItemStack crystal = new ItemStack(ItemRegister.LAVA_CRYSTAL.get());
                ms.pushPose();
                ms.translate(0.5, 1.52-((tile.renderTick/3.125)* 0.01), 0.5);
                ms.mulPose(Vector3f.XN.rotationDegrees(90f));
                ms.mulPose(Vector3f.ZN.rotationDegrees(-tile.getLevel().getGameTime()/(0.625123f)));
                ms.scale(0.3f, 0.3f, 0.3f);
                Minecraft.getInstance().getItemRenderer().
                        renderStatic(crystal, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                                combinedOverlay, ms, buffer);
                ms.popPose();
            }
        }
    }
    public void renderSymbol(MatrixTile te, MatrixStack ms, float x, float z, boolean rotate,
                             int combinedLight, int combinedOverlay, IRenderTypeBuffer buffer) {
        ItemStack stack = te.handler.getStackInSlot(1);
        ms.pushPose();
        ms.translate(x, 0.375, z);
        if(rotate) ms.mulPose(Vector3f.YP.rotationDegrees(90));
        float s = 0.33f;
        ms.scale(s, s, s);
        Minecraft.getInstance().getItemRenderer().
                renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                        combinedOverlay, ms, buffer);
        ms.popPose();
    }
}
