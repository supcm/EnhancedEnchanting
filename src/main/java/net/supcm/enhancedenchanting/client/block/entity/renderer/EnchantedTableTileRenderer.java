package net.supcm.enhancedenchanting.client.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.block.entity.EnchantedTableTile;
import org.lwjgl.opengl.GL11;

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
            if(te.enchLevel != -1) {
                ms.clear();
                ms.pushPose();
                ms.translate(0.5, 1.5, 0.5);
                ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
                ms.scale(-0.025f, -0.025f, 0.025f);
                FontRenderer fontrenderer = Minecraft.getInstance().getEntityRenderDispatcher().getFont();
                String text = String.valueOf(te.enchLevel);
                float width = (float) (-fontrenderer.width(text) / 2);
                Matrix4f text_matrix = ms.last().pose();
                fontrenderer.drawInBatch(text, width, 0f,
                        0x67ff67, false, text_matrix, buffer, false,
                        (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.33F) * 255.0F) << 24,
                        combinedLight);
                ms.popPose();
            }
            renderGlyphs(te, ms, buffer, combinedLight, combinedOverlay);
        }
    }
    void renderGlyphs(EnchantedTableTile te, MatrixStack ms, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ms.pushPose();
        ms.translate(0.5, 1.55, 0.5);
        ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        ms.translate(0, 0.15, 0);
        ms.scale(0.25f, 0.25f, 0.25f);
        for (int i = 0; i < te.handler.getSlots(); i++) {
            if(!te.handler.getStackInSlot(i).isEmpty())
                Minecraft.getInstance().getItemRenderer().renderStatic(te.handler.getStackInSlot(i),
                        ItemCameraTransforms.TransformType.FIXED, 15728880, OverlayTexture.NO_OVERLAY, ms, buffer);
                //renderGlyph(te, ms, i);
            ms.translate(-1, 0, 0);
        }
        ms.popPose();
    }
    private void renderGlyph(EnchantedTableTile te, MatrixStack ms, int slot) {
        ms.pushPose();
        RenderSystem.disableLighting();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f,
                MathHelper.abs(MathHelper.sin(te.getLevel().getGameTime()/16f)));
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ms.mulPose(Vector3f.YN.rotationDegrees(180f));
        ms.mulPose(Vector3f.XN.rotationDegrees(90f));
        Matrix4f mm = ms.last().pose();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Minecraft.getInstance().getTextureManager().bind(getGlyphTexture(te, slot));
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(mm,0.5f, 0, 0.5f).uv(0, 0).endVertex();
        buffer.vertex(mm,-0.5f, 0, 0.5f).uv(1, 0).endVertex();
        buffer.vertex(mm,-0.5f, 0, -0.5f).uv(1, 1).endVertex();
        buffer.vertex(mm,0.5f, 0, -0.5f).uv(0, 1).endVertex();
        tessellator.end();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableLighting();
        ms.popPose();
    }
    private ResourceLocation getGlyphTexture(EnchantedTableTile tile, int slot) {
        return new ResourceLocation(EnhancedEnchanting.MODID,
                "textures/item/" + tile.handler.getStackInSlot(slot).getItem().getRegistryName().getPath()
                        + ".png");
    }
}
