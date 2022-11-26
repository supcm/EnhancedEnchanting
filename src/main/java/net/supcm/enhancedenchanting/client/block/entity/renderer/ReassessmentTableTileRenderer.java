package net.supcm.enhancedenchanting.client.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.block.entity.ReassessmentPillarTile;
import net.supcm.enhancedenchanting.common.block.entity.ReassessmentTableTile;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ReassessmentTableTileRenderer extends TileEntityRenderer<ReassessmentTableTile> {
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
        boolean isValid = te.isValid;
        if(isValid) {
            ItemStack stack = te.handler.getStackInSlot(0);
            if (!stack.isEmpty()) {
                ms.pushPose();
                ms.translate(0.5F, 0.95 + 0.015 * MathHelper.cos(0.05f * te.getLevel().getGameTime()), 0.5F);
                ms.mulPose(Vector3f.XN.rotationDegrees(90f));
                ms.mulPose(Vector3f.XN.rotationDegrees(3.75f * MathHelper.sin(te.getLevel().getGameTime() / 11.5f)));
                ms.mulPose(Vector3f.ZN.rotationDegrees(te.getLevel().getGameTime() / 1.8525f));
                ms.scale(0.55f, 0.55f, 0.55f);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                        combinedOverlay, ms, buffer);
                ms.popPose();
            }
        } else renderInvalid(ms, te, buffer);
    }
    public void renderInvalid(MatrixStack ms, ReassessmentTableTile tile, IRenderTypeBuffer buffer) {
        ms.pushPose();
        BlockPos pos = tile.getBlockPos();
        for(int i = -2; i < 3; i++) {
            for(int j = -2; j < 3; j++) {
                if((j == -2 || j == 2) && i == 0) {
                    if(tile.getLevel().getBlockState(pos.south(j))
                            != BlockRegister.REASSESSMENT_PILLAR.get().defaultBlockState()) {
                        ms.pushPose();
                        ms.translate(i, 0, j);
                        if (tile.getLevel().getBlockState(pos.south(j)).getMaterial() == Material.AIR)
                            renderInvalidPillar(ms, buffer);
                        else
                            renderInvalidBlock(ms, buffer);
                        ms.popPose();
                    }
                } else if((j == 1 || j == -1) && (i == -2 || i == 2)) {
                    if(tile.getLevel().getBlockState(pos.south(j).east(i))
                            != BlockRegister.REASSESSMENT_PILLAR.get().defaultBlockState()) {
                        ms.pushPose();
                        ms.translate(i, 0, j);
                        if (tile.getLevel().getBlockState(pos.south(j).east(i)).getMaterial() == Material.AIR)
                            renderInvalidPillar(ms, buffer);
                        else
                            renderInvalidBlock(ms, buffer);
                        ms.popPose();
                    }
                } else if(!(i == 0 && j == 0)){
                    if(tile.getLevel().getBlockState(pos.south(j).east(i)).getMaterial() != Material.AIR){
                        ms.pushPose();
                        ms.translate(i, 0, j);
                        renderInvalidBlock(ms, buffer);
                        ms.popPose();
                    }
                }
            }
        }
        ms.popPose();
    }
    private void renderInvalidPillar(MatrixStack ms, IRenderTypeBuffer buffers) {
        ms.pushPose();
        RenderSystem.disableLighting();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1, 1, 1, 0.5f);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegister.REASSESSMENT_PILLAR.get().defaultBlockState(),
                ms, buffers, 0xFF0000, OverlayTexture.NO_OVERLAY);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableLighting();
        ms.popPose();
    }
    private void renderInvalidBlock(MatrixStack ms, IRenderTypeBuffer buffers) {
        ms.pushPose();
        RenderSystem.lineWidth(10);
        IVertexBuilder builder = buffers.getBuffer(RenderType.LINES);
        WorldRenderer.renderLineBox(ms, builder,
                0, 0, 0, 1, 1, 1,
                1, 0, 0, 1, 1, 0 ,0);

        ms.popPose();
    }
    @Override public boolean shouldRenderOffScreen(ReassessmentTableTile tile) { return true; }
}
