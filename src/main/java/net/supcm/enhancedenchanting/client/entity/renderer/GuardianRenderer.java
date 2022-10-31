package net.supcm.enhancedenchanting.client.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;
import net.supcm.enhancedenchanting.common.entity.GuardianEntity;

public class GuardianRenderer extends MobRenderer<GuardianEntity, GuardianRenderer.GuardianModel> {
    protected static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation(EnhancedEnchanting.MODID,
            "textures/entity/guardian.png");
    protected static final ResourceLocation GUARDIAN_LIGHTNING_TEXTURE = new ResourceLocation(EnhancedEnchanting.MODID,
            "textures/entity/lightning_guardian.png");
    protected static final ResourceLocation GUARDIAN_WIND_TEXTURE = new ResourceLocation(EnhancedEnchanting.MODID,
            "textures/entity/wind_guardian.png");
    public GuardianRenderer(EntityRendererManager manager) {
        super(manager, new GuardianModel(), 1.5f);
        addLayer(new RingLayerRender<>(this, manager, false));
        addLayer(new RingLayerRender<>(this, manager, true));
    }
    @Override public ResourceLocation getTextureLocation(GuardianEntity entity) {
        if(entity.getState() == 1) return GUARDIAN_LIGHTNING_TEXTURE;
        else if(entity.getState() == 2) return GUARDIAN_WIND_TEXTURE;
        else return GUARDIAN_TEXTURE;
    }
    @Override public void render(GuardianEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack ms, IRenderTypeBuffer buffer, int p_225623_6_) {
        super.render(entity, p_225623_2_, p_225623_3_, ms, buffer, p_225623_6_);
        if(entity.tickCount % 20 == 0){
            if (entity.getState() == 1)
                entity.level.addParticle(ParticleTypes.ANGRY_VILLAGER,
                        entity.getX() + entity.getRandom().nextFloat(), entity.getY() + 0.45, entity.getZ() + entity.getRandom().nextFloat(),
                        entity.getRandom().nextFloat(), 0.5D, entity.getRandom().nextFloat());
            else if (entity.getState() == 2)
                entity.level.addParticle(ParticleTypes.SWEEP_ATTACK,
                        entity.getX() + entity.getRandom().nextFloat(), entity.getY() + 0.45, entity.getZ() + entity.getRandom().nextFloat(),
                        entity.getRandom().nextFloat(), 0.15D, entity.getRandom().nextFloat());
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static class RingLayerRender<T extends GuardianEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
        float[] angles = new float[9];
        float anglePer = 360F / 9;
        float totalAngle = 0F;
        EntityRendererManager manager;
        boolean reverse;
        public RingLayerRender(IEntityRenderer<T, M> renderer, EntityRendererManager manager, boolean reverse) {
            super(renderer);
            for (short i = 0; i < angles.length; i++) {
                angles[i] = totalAngle += anglePer;
            }
            this.manager = manager;
            this.reverse = reverse;
        }
        @Override public void render(MatrixStack ms, IRenderTypeBuffer buffer,
                           int combinedOverlay, T entity, float p_225628_5_, float p_225628_6_,
                                     float p_225628_7_, float partialTicks, float p_225628_9_, float p_225628_10_) {
            float time = (entity.level.getGameTime() + partialTicks) * (entity.getState() == 2 ? 12 : 1);
            int light = manager.getPackedLightCoords(entity, 1.6f);
            ms.pushPose();
            ms.translate(0, 0.5f, 0);
            ms.mulPose(Vector3f.ZP.rotationDegrees((reverse ? -1 : 1) * (float)(18.5*Math.cos(time/35.5f))));
            for (short i = 0; i < 9; i++) {
                ms.pushPose();
                if(reverse) ms.mulPose(Vector3f.YN.rotationDegrees(angles[i] + time));
                else ms.mulPose(Vector3f.YP.rotationDegrees(angles[i] + time));
                ms.translate(1.325F + (reverse ? 0.3 : 0), 0.25, 0.25F);
                ms.mulPose(Vector3f.YP.rotationDegrees(90F));
                ms.mulPose(Vector3f.ZP.rotationDegrees(180F));
                float s = 0.9f;
                ms.scale(s, s, s);
                ItemStack render = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(
                        EnhancedEnchanting.MODID, EnchantmentsList.SYMBOLS_LIST.get(i))));
                Minecraft.getInstance().getItemRenderer().renderStatic(render,
                        ItemCameraTransforms.TransformType.GROUND, light, combinedOverlay, ms, buffer);
                ms.popPose();
            }
            ms.popPose();
        }
    }
    // Made with Blockbench 4.3.1
    // Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
    // Paste this class into your mod and generate all required imports
    public static class GuardianModel extends EntityModel<GuardianEntity> {
        private final ModelRenderer head;
        public GuardianModel() {
            texWidth = 128;
            texHeight = 128;
            head = new ModelRenderer(this);
            head.setPos(0.0F, 24.0F, 0.0F);
            head.texOffs(0, 0).addBox(-10.0F, -20.0F, -10.0F, 20.0F, 20.0F, 20.0F, 0.0F, false);
        }
        @Override public void setupAnim(GuardianEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch){
            //previously the render function, render code was moved to a method below
        }
        @Override public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight,
                                             int packedOverlay, float red, float green, float blue, float alpha){
            head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
