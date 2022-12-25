package net.supcm.enhancedenchanting.client.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.block.entity.WordForgeTile;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

public class WordForgeTileRenderer extends TileEntityRenderer<WordForgeTile> {
    public static List<String> T2_LIST = new ArrayList<>();
    public static List<String> T3_LIST = new ArrayList<>();
    public WordForgeTileRenderer(TileEntityRendererDispatcher manager) { super(manager); }
    @Override public void render(WordForgeTile te, float partialTicks, MatrixStack ms,
                                 IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay ) {
        ItemStack stack = te.handler.getStackInSlot(0);
        ItemStack stack1 = te.handler.getStackInSlot(1);
        ItemStack stack2 = te.handler.getStackInSlot(2);
        {
            float s = 0.55F;
            renderGlyphs(te, ms, buffer, combinedLight, combinedOverlay);
            if (te.enchLevel != -1 && (!stack.isEmpty() || !stack1.isEmpty() || !stack2.isEmpty())) {
                ms.pushPose();
                ms.translate(0.5, 1.85, 0.5);
                ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
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
            if (!stack.isEmpty()) {
                ms.pushPose();
                ms.translate(0.5F, 1.46 + 0.03 * MathHelper.cos(0.05f * te.getLevel().getGameTime()), 0.5F);
                ms.mulPose(Vector3f.XN.rotationDegrees(90f));
                ms.mulPose(Vector3f.XN.rotationDegrees(3.75f * MathHelper.sin(te.getLevel().getGameTime() / 12.5f)));
                ms.mulPose(Vector3f.ZN.rotationDegrees(-(te.getLevel().getGameTime() / 0.325f)));
                ms.scale(s, s, s);
                Minecraft.getInstance().getItemRenderer().
                        renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                                combinedOverlay, ms, buffer);
                ms.popPose();
            }
            if (!stack1.isEmpty()) {
                ms.pushPose();
                ms.translate(0.5F, 1.32 + 0.02 * MathHelper.sin(0.03f * te.getLevel().getGameTime()), 0.5F);
                ms.mulPose(Vector3f.XN.rotationDegrees(90f));
                ms.mulPose(Vector3f.YN.rotationDegrees(3.75f * MathHelper.cos((te.getLevel().getGameTime() / 12.5f))));
                ms.mulPose(Vector3f.ZN.rotationDegrees(te.getLevel().getGameTime() / 0.625f));
                ms.scale(s, s, s);
                Minecraft.getInstance().getItemRenderer().
                        renderStatic(stack1, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                                combinedOverlay, ms, buffer);
                ms.popPose();
            }
            if (!stack2.isEmpty()) {
                ms.pushPose();
                ms.translate(0.5F, 1.23 + 0.02 * MathHelper.sin(0.03f * te.getLevel().getGameTime()), 0.5F);
                ms.mulPose(Vector3f.XN.rotationDegrees(90f));
                ms.mulPose(Vector3f.YN.rotationDegrees(-1.75f * MathHelper.sin((te.getLevel().getGameTime() / 8.5f))));
                ms.mulPose(Vector3f.ZN.rotationDegrees(te.getLevel().getGameTime() / 1.125f));
                ms.scale(s, s, s);
                Minecraft.getInstance().getItemRenderer().
                        renderStatic(stack2, ItemCameraTransforms.TransformType.FIXED, combinedLight,
                                combinedOverlay, ms, buffer);
                ms.popPose();
            }
        }
        double time = te.getLevel().getGameTime() + partialTicks;
        {
            float[] angles = new float[9];
            float anglePer = 360F / 9;
            float totalAngle = 0F;
            for (short i = 0; i < angles.length; i++) {
                angles[i] = totalAngle += anglePer;
            }

            if (stack1.isEmpty() && !stack.isEmpty()) {
                for (short i = 0; i < 9; i++) {
                    ms.pushPose();
                    ms.translate(0.5F, .35F, 0.5F);
                    ms.mulPose(Vector3f.YP.rotationDegrees(-(angles[i] + (float) time) + 50));
                    ms.translate(0.925F, 0F, 0.25F);
                    ms.mulPose(Vector3f.YP.rotationDegrees(90F));
                    ItemStack render = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(
                            EnhancedEnchanting.MODID, EnchantmentsList.SYMBOLS_LIST.get(i))));
                    float s = 0.9f;
                    ms.scale(s, s, s);
                    if (i % 2 == 0)
                        ms.mulPose(Vector3f.ZP.rotationDegrees(3.75f * MathHelper.cos((te.getLevel().getGameTime() / 12.5f))));
                    else
                        ms.mulPose(Vector3f.ZP.rotationDegrees(-2.15f * MathHelper.sin((te.getLevel().getGameTime() / 4.5f))));
                    if (!stack.isEmpty()) {
                        if (T2_LIST.contains(stack.getItem().getRegistryName().getPath() + "_" +
                                render.getItem().getRegistryName().getPath()))
                            ms.translate(0, 0.45 + 0.075 * Math.cos(time / 8.2), 0);
                    }
                    Minecraft.getInstance().getItemRenderer().renderStatic(render,
                            ItemCameraTransforms.TransformType.GROUND,
                            combinedLight, combinedOverlay, ms, buffer);
                    ms.popPose();
                }
            }
        }
        if(!stack.isEmpty() && stack2.isEmpty()) {
            short c = (short)T3_LIST.stream().filter(s1 -> s1.startsWith(
                    stack.getItem().getRegistryName().getPath()
            )).toArray().length;
            if(c > 0){
                float[] angles = new float[c];
                float anglePer = 360F / c;
                float totalAngle = 0F;
                for (short j = 0; j < angles.length; j++) {
                    angles[j] = totalAngle += anglePer;
                }
                for (short i = 0; i < c; i++) {
                    ms.pushPose();
                    ms.translate(0.5F, .35F, 0.5F);
                    ms.mulPose(Vector3f.YP.rotationDegrees(-(angles[i] + (float) time) + 50));
                    ms.translate(0.925F + (c * 0.025), 0F, 0.25F);
                    ms.mulPose(Vector3f.YP.rotationDegrees(90F));
                    String glyph2 = ((String)T3_LIST.stream().filter(string -> string.startsWith(
                            stack.getItem().getRegistryName().getPath()
                    )).toArray()[i]).split("_")[1];
                    String glyph3 = ((String)T3_LIST.stream().filter(string -> string.startsWith(
                            stack.getItem().getRegistryName().getPath()
                    )).toArray()[i]).split("_")[2];
                    ItemStack render = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(EnhancedEnchanting.MODID, glyph2)));
                    ItemStack render1 = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(EnhancedEnchanting.MODID, glyph3)));
                    float s = 0.9f;
                    ms.scale(s, s, s);
                    if (i % 2 == 0)
                        ms.mulPose(Vector3f.ZP.rotationDegrees(3.75f * MathHelper.cos((te.getLevel().getGameTime() / 12.5f))));
                    else
                        ms.mulPose(Vector3f.ZP.rotationDegrees(-2.15f * MathHelper.sin((te.getLevel().getGameTime() / 4.5f))));
                    ms.translate(0, 1.15 + 0.075 * Math.cos(time / 8.2), 0);
                    ms.pushPose();
                    ms.translate(0, 0.25, 0.15);
                    Minecraft.getInstance().getItemRenderer().renderStatic(render1,
                            ItemCameraTransforms.TransformType.GROUND,
                            combinedLight, combinedOverlay, ms, buffer);
                    ms.popPose();
                    Minecraft.getInstance().getItemRenderer().renderStatic(render,
                            ItemCameraTransforms.TransformType.GROUND,
                            combinedLight, combinedOverlay, ms, buffer);
                    ms.popPose();
                }
            }
        }
    }
    void renderGlyphs(WordForgeTile te, MatrixStack ms, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ms.pushPose();
        ms.translate(0.5, 1.75, 0.5);
        ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        ms.translate(0.08 * te.handler.getSlots(), 0.15, 0);
        ms.scale(0.25f, 0.25f, 0.25f);
        for (int i = 0; i < te.handler.getSlots(); i++) {
            if(!te.handler.getStackInSlot(i).isEmpty())
                Minecraft.getInstance().getItemRenderer().renderStatic(te.handler.getStackInSlot(i),
                        ItemCameraTransforms.TransformType.FIXED, 15728880, combinedOverlay, ms, buffer);
                //renderGlyph(te, ms, i);
            ms.translate(-1, 0, 0);
        }
        ms.popPose();
    }
    private void renderGlyph(WordForgeTile te, MatrixStack ms, int slot) {
        ms.pushPose();
        RenderSystem.disableLighting();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
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
    private ResourceLocation getGlyphTexture(WordForgeTile tile, int slot) {
        return new ResourceLocation(EnhancedEnchanting.MODID,
                "textures/item/" + tile.handler.getStackInSlot(slot).getItem().getRegistryName().getPath()
                        + ".png");
    }
}