package net.supcm.enhancedenchanting.client.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.block.entity.ReassessmentPillarTile;
import net.supcm.enhancedenchanting.common.block.entity.ReassessmentTableTile;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ReassessmentTableTileRenderer extends TileEntityRenderer<ReassessmentTableTile> {
    public static int[] conceptions = new int[6];
    private final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(EnhancedEnchanting.MODID,
            "textures/block/reassessment_circle.png");
    public ReassessmentTableTileRenderer(TileEntityRendererDispatcher manager) { super(manager); }
    @Override public void render(ReassessmentTableTile te, float partialTicks, MatrixStack ms,
                       IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay ) {
        ms.pushPose();
        ms.translate(0.5, 1, 0.5);
        ms.mulPose(Vector3f.XN.rotationDegrees(90f));
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Blocks.GLASS_PANE),
                ItemCameraTransforms.TransformType.FIXED, combinedLight,
                combinedOverlay, ms, buffer);
        ms.popPose();
        ItemStack stack = te.handler.getStackInSlot(0);
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 0.95 + 0.015 * MathHelper.cos(0.05f * te.getLevel().getGameTime()), 0.5F);
            ms.mulPose(Vector3f.XN.rotationDegrees(90f));
            ms.mulPose(Vector3f.XN.rotationDegrees(3.75f * MathHelper.sin(te.getLevel().getGameTime() / 11.5f)));
            ms.mulPose(Vector3f.ZN.rotationDegrees(te.getLevel().getGameTime() / 1.8525f));
            ms.scale(0.55f, 0.55f, 0.55f);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                    combinedOverlay, ms, buffer);
            ms.popPose();
            if(Arrays.stream(conceptions).sum() != 0){
                ms.pushPose();
                ms.translate(0.5, 0.35, 0.5);
                ms.mulPose(Vector3f.XN.rotationDegrees(180f));
                ms.scale(2.1f, 2.1f, 2.1f);
                renderEffect(ms, te, 1, 1, 1);
                ms.popPose();
            }
        }
    }
    public void renderEffect(MatrixStack ms, ReassessmentTableTile te,
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
    @Override public boolean shouldRenderOffScreen(ReassessmentTableTile tile) { return true; }
}
