package net.supcm.enhancedenchanting.client.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.block.entity.WordMachineTile;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;

import java.util.ArrayList;
import java.util.List;

public class WordMachineTileRenderer extends TileEntityRenderer<WordMachineTile> {
    public static List<String> T2_LIST = new ArrayList<>();
    public WordMachineTileRenderer(TileEntityRendererDispatcher manager) { super(manager); }
    @Override public void render(WordMachineTile te, float partialTicks, MatrixStack ms,
                       IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = te.handler.getStackInSlot(0);
        ItemStack stack1 = te.handler.getStackInSlot(1);
        float time = te.getLevel().getGameTime() + partialTicks;
        float[] angles = new float[9];
        float anglePer = 360F / 9;
        float totalAngle = 0F;
        for (short i = 0; i < angles.length; i++) {
            angles[i] = totalAngle += anglePer;
        }
        if(te.enchLevel != -1 && (!stack.isEmpty() || !stack1.isEmpty())) {
            ms.pushPose();
            ms.translate(0.5, 1.65, 0.5);
            ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            ms.scale(-0.025f, -0.025f, 0.025f);
            FontRenderer fontrenderer = Minecraft.getInstance().getEntityRenderDispatcher().getFont();
            String text = String.valueOf(te.enchLevel);
            float width = (float)(-fontrenderer.width(text) / 2);
            Matrix4f text_matrix = ms.last().pose();
            ms.popPose();
            fontrenderer.drawInBatch(text, width, 0f,
                    0x67ff67, false, text_matrix, buffer, false,
                    (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.33F) * 255.0F) << 24,
                    combinedLight);
        }
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 1.36+0.03*MathHelper.cos(0.05f*time), 0.5F);
            ms.mulPose(Vector3f.XN.rotationDegrees(90f));
            ms.mulPose(Vector3f.XN.rotationDegrees(3.75f*MathHelper.sin(time/12.5f)));
            ms.mulPose(Vector3f.ZN.rotationDegrees(-(te.getLevel().getGameTime()/1.125f)));
            float s = 0.55F;
            ms.scale(s, s, s);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                    combinedOverlay, ms, buffer);
            ms.popPose();
        }
        if(!stack1.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 1.23+0.02*MathHelper.sin(0.03f*time), 0.5F);
            ms.mulPose(Vector3f.XN.rotationDegrees(90f));
            ms.mulPose(Vector3f.YN.rotationDegrees(3.75f*MathHelper.cos((time/12.5f))));
            ms.mulPose(Vector3f.ZN.rotationDegrees(time/2.825f));
            float s = 0.55F;
            ms.scale(s, s, s);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack1, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                            combinedOverlay, ms, buffer);
            ms.popPose();
        }
        if(stack1.isEmpty() && !stack.isEmpty()){
            for (short i = 0; i < 9; i++) {
                ms.pushPose();
                ms.translate(0.5F, .5F, 0.5F);
                ms.mulPose(Vector3f.YP.rotationDegrees(-(angles[i] + (float)time)+50));
                ms.translate(0.825F, 0F, 0.25F);
                ms.mulPose(Vector3f.YP.rotationDegrees(90F));
                ItemStack render = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(
                        EnhancedEnchanting.MODID, EnchantmentsList.SYMBOLS_LIST.get(i))));
                float s = 0.9f;
                ms.scale(s, s, s);
                if(i % 2 == 0)
                    ms.mulPose(Vector3f.ZP.rotationDegrees(3.75f*MathHelper.cos(time/12.5f)));
                else
                    ms.mulPose(Vector3f.XP.rotationDegrees(2.15f*MathHelper.sin(time/4.5f)));
                if (T2_LIST.contains(stack.getItem().getRegistryName().getPath() + "_" +
                        render.getItem().getRegistryName().getPath()))
                    ms.translate(0, 1.05 + 0.075 * Math.cos(time / 8.2), 0);
                Minecraft.getInstance().getItemRenderer().renderStatic(render,
                        ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, ms, buffer);
                ms.popPose();
            }
        }
    }


}
