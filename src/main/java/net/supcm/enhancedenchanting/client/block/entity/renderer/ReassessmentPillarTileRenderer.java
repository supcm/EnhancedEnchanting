package net.supcm.enhancedenchanting.client.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.block.entity.ReassessmentPillarTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import org.lwjgl.opengl.GL11;

public class ReassessmentPillarTileRenderer extends TileEntityRenderer<ReassessmentPillarTile> {
    private final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(EnhancedEnchanting.MODID,
            "textures/block/reassessment_circle.png");
    final float s = 0.55F;
    public ReassessmentPillarTileRenderer(TileEntityRendererDispatcher mgr) { super(mgr); }
    @Override public void render(ReassessmentPillarTile te, float partialTicks, MatrixStack ms,
                       IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay ) {
        ItemStack stack = te.handler.getStackInSlot(0);
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 1.25 + 0.035 * MathHelper.cos(0.05f * te.getLevel().getGameTime()), 0.5F);
            ms.mulPose(Vector3f.YN.rotationDegrees(te.getLevel().getGameTime() / 0.8525f));
            ms.scale(s, s, s);
            ms.mulPose(Vector3f.XN.rotationDegrees(3.75f * MathHelper.sin(te.getLevel().getGameTime() / 12.5f)));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                    combinedOverlay, ms, buffer);
            ms.popPose();
            if(hasRightConception(stack, te.conceptions)){
                ms.pushPose();
                ms.translate(0.5, 1.05, 0.5);
                ms.mulPose(Vector3f.XN.rotationDegrees(180f));
                ms.scale(0.75f, 0.75f, 0.75f);
                renderEffect(ms, te,
                        ((ItemRegister.ConceptionItem) stack.getItem()).getColor()[0],
                        ((ItemRegister.ConceptionItem) stack.getItem()).getColor()[1],
                        ((ItemRegister.ConceptionItem) stack.getItem()).getColor()[2]);
                ms.popPose();
            }
        }
    }
    boolean hasRightConception(ItemStack stack, int[] conceptions) {
        boolean hasRightItemStack = false;
        if(conceptions != null){
            for (int i = 0; i < conceptions.length; i++) {
                if (conceptions[i] != 0) {
                    if (i == 0) {
                        if (stack.getItem() == ItemRegister.CONCEPTION_BEAUTY.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else if (i == 1) {
                        if (stack.getItem() == ItemRegister.CONCEPTION_CREATION.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else if (i == 2) {
                        if (stack.getItem() == ItemRegister.CONCEPTION_ART.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else if (i == 3) {
                        if (stack.getItem() == ItemRegister.CONCEPTION_TRUTH.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else if (i == 4) {
                        if (stack.getItem() == ItemRegister.CONCEPTION_SOUL.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else {
                        if (stack.getItem() == ItemRegister.CONCEPTION_LIES.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    }
                }
            }
        }
        return hasRightItemStack;
    }
    public void renderEffect(MatrixStack ms, ReassessmentPillarTile te,
                             float r, float g, float b) {
        ms.pushPose();
        RenderSystem.disableLighting();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        RenderSystem.color4f(r, g, b, 1);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ms.mulPose(Vector3f.YN.rotationDegrees(te.getLevel().getGameTime() / 0.8525f));
        Matrix4f mm = ms.last().pose();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Minecraft.getInstance().getTextureManager().bind(EFFECT_TEXTURE);
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
}
