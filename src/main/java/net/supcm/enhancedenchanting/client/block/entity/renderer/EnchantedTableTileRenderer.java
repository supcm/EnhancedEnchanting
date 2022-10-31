package net.supcm.enhancedenchanting.client.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.supcm.enhancedenchanting.common.block.entity.EnchantedTableTile;

public class EnchantedTableTileRenderer extends TileEntityRenderer<EnchantedTableTile> {
    float s = 0.55F;
    Minecraft mc = Minecraft.getInstance();

    public EnchantedTableTileRenderer(TileEntityRendererDispatcher manager) {
        super(manager);
    }
    @Override
    public void render(EnchantedTableTile te, float partialTicks, MatrixStack ms,
                       IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay ) {
        ItemStack stack = te.handler.getStackInSlot(0);
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 0.75+0.085*MathHelper.cos(0.05f*te.getLevel().getGameTime()), 0.5F);
            ms.mulPose(Vector3f.XN.rotationDegrees(90f));
            ms.mulPose(Vector3f.XN.rotationDegrees(3.75f*MathHelper.sin((float)(te.getLevel().getGameTime()/12.5f))));
            ms.mulPose(Vector3f.ZN.rotationDegrees(te.getLevel().getGameTime()/1.8525f));
            ms.scale(s, s, s);
            mc.getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                    combinedOverlay, ms, buffer);
            ms.popPose();
            if(te.enchLevel != -1){
                ms.pushPose();
                ms.translate(0.5, 1.5, 0.5);
                ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
                ms.scale(-0.025f, -0.025f, 0.025f);
                FontRenderer fontrenderer = Minecraft.getInstance().getEntityRenderDispatcher().getFont();
                String text = String.valueOf(te.enchLevel);
                float width = (float) (-fontrenderer.width(text) / 2);
                Matrix4f text_matrix = ms.last().pose();

                ms.popPose();
                fontrenderer.drawInBatch(text, width, 0f,
                        0x67ff67, false, text_matrix, buffer, false,
                        (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.33F) * 255.0F) << 24,
                        combinedLight);
            }
        }
    }
}
