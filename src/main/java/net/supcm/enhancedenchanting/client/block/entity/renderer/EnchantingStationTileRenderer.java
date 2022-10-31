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
import net.supcm.enhancedenchanting.common.block.entity.EnchantingStationTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

public class EnchantingStationTileRenderer extends TileEntityRenderer<EnchantingStationTile> {
    public EnchantingStationTileRenderer(TileEntityRendererDispatcher manager) { super(manager); }

    @Override
    public void render(EnchantingStationTile te, float partialTicks, MatrixStack ms,
                       IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        renderCrystal(te, ms, 0.25f, 0.25f, combinedLight, combinedOverlay, buffer);
        renderCrystal(te, ms, 1-0.25f, 0.25f, combinedLight, combinedOverlay, buffer);
        renderCrystal(te, ms, 0.25f, 1-0.25f, combinedLight, combinedOverlay, buffer);
        renderCrystal(te, ms, 1-0.25f, 1-0.25f, combinedLight, combinedOverlay, buffer);
        /*if(te.doCraft) {
            ItemStack stack = new ItemStack(ItemRegister.CRYSTAL.get());
            ms.pushPose();
            ms.translate(0.5, 1.55 + 0.015 * MathHelper.cos(0.15f * te.getLevel().getGameTime()), 0.5);
            ms.mulPose(Vector3f.YP.rotationDegrees(te.getLevel().getGameTime() / 0.18525f));
            float s = 0.88f;
            ms.scale(s, s, s);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight,
                            combinedOverlay, ms, buffer);
            ms.mulPose(Vector3f.YP.rotationDegrees(90f));
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight,
                            combinedOverlay, ms, buffer);
            ms.popPose();
        }*/
        ItemStack stack = te.handler.getStackInSlot(0);
        ItemStack stack1 = te.handler.getStackInSlot(1);
        if(!stack.isEmpty()){
            ms.pushPose();
            ms.translate(0.5F, 1.15 + 0.025 * MathHelper.cos(0.05f * te.getLevel().getGameTime()), 0.5F);
            ms.mulPose(Vector3f.XN.rotationDegrees(90f));
            ms.mulPose(Vector3f.XN.rotationDegrees(3.75f * MathHelper.cos(te.getLevel().getGameTime() / 8.5f)));
            ms.mulPose(Vector3f.ZN.rotationDegrees(te.getLevel().getGameTime() / -0.9525f));
            float s = 0.55f;
            ms.scale(s, s, s);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack,
                    ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, ms, buffer);
            ms.popPose();
        }
        if(!stack1.isEmpty() || te.tick != 0) {
            double time = te.getLevel().getGameTime() + partialTicks;
            float[] angles = new float[stack1.getCount()];
            float anglePer = 360F / stack1.getCount();
            float totalAngle = 0F;
            for (int i = 0; i < angles.length; i++) {
                angles[i] = totalAngle += anglePer;
            }
            for (int i = 0; i < stack1.getCount(); i++) {
                ms.pushPose();
                ms.translate(0.5F, 0.95F, 0.5F);
                if(te.doCraft) {
                    ms.mulPose(Vector3f.YP.rotationDegrees(-(angles[i] + (float)time)+20*(te.tick*0.12525f)));
                    ms.translate(1.45F-(te.tick*0.012525f), 0F, 0.25F);
                } else {
                    ms.mulPose(Vector3f.YP.rotationDegrees(-(angles[i] + (float) time) + 50));
                    ms.translate(1.45F, 0F, 0.25F);
                }
                ms.mulPose(Vector3f.YP.rotationDegrees(90F));;
                float s = 0.9f;
                ms.scale(s, s, s);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack1,
                        ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, ms, buffer);
                ms.popPose();
            }
        }
    }

    public void renderCrystal(EnchantingStationTile te, MatrixStack ms, float x, float z, int combinedLight, int combinedOverlay,
                              IRenderTypeBuffer buffer) {
        ItemStack stack = new ItemStack(ItemRegister.CRYSTAL.get());
        ms.pushPose();
        ms.translate(x, 0.25 + 0.015 * MathHelper.cos(0.05f * te.getLevel().getGameTime()), z);
        ms.mulPose(Vector3f.YP.rotationDegrees(te.getLevel().getGameTime() / 2.8525f));
        float s = 0.5f;
        ms.scale(s, s, s);
        Minecraft.getInstance().getItemRenderer().
                renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight,
                        combinedOverlay, ms, buffer);
        ms.popPose();
    }
}
