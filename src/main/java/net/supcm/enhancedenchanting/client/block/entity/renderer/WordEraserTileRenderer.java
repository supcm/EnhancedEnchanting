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
import net.minecraft.util.text.TranslationTextComponent;
import net.supcm.enhancedenchanting.common.block.entity.WordEraserTile;

public class WordEraserTileRenderer extends TileEntityRenderer<WordEraserTile> {
    final float s = 0.55F;
    public WordEraserTileRenderer(TileEntityRendererDispatcher mgr) { super(mgr); }
    @Override public void render(WordEraserTile te, float partialTicks, MatrixStack ms,
                                 IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay ) {
        ItemStack stack = te.handler.getStackInSlot(0);
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 0.4 + 0.035 * MathHelper.cos(0.05f * te.getLevel().getGameTime()), 0.5F);
            ms.mulPose(Vector3f.YN.rotationDegrees(te.getLevel().getGameTime() / 0.8525f));
            ms.scale(s, s, s);
            ms.mulPose(Vector3f.XN.rotationDegrees(3.75f * MathHelper.sin(te.getLevel().getGameTime() / 12.5f)));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                    combinedOverlay, ms, buffer);
            ms.popPose();
            if(!te.currentEnchantmentName.equals("")) {
                ms.pushPose();
                ms.translate(0.5, 1.45, 0.5);
                ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                ms.scale(-0.025f, -0.025f, 0.025f);
                FontRenderer fontrenderer = Minecraft.getInstance().getEntityRenderDispatcher().getFont();
                TranslationTextComponent text = new TranslationTextComponent(te.currentEnchantmentName);
                float width = (float)(-fontrenderer.width(text.getString()) / 2);
                Matrix4f text_matrix = ms.last().pose();
                fontrenderer.drawInBatch(text, width, 0f,
                        0x67ff67, false, text_matrix, buffer, false,
                        (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.33F) * 255.0F) << 24,
                        combinedLight);
                ms.popPose();
            }
        }
    }
}
