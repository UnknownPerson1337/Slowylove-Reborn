/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.item;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
//import net.ccbluex.liquidbounce.features.module.modules.combat.TpAura;
import net.ccbluex.liquidbounce.features.module.modules.render.Animations;
import net.ccbluex.liquidbounce.features.module.modules.render.AntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.ItemShow;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ItemRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinItemRenderer {

    float delay = 0.0F;
    MSTimer rotateTimer = new MSTimer();

    @Shadow
    private ItemStack itemStackMainHand;
    @Shadow
    private ItemStack itemStackOffHand;


    @Shadow
    private float equippedProgressMainHand;
    @Shadow
    private float prevEquippedProgressMainHand;
    @Shadow
    private float equippedProgressOffHand;
    @Shadow
    private float prevEquippedProgressOffHand;

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    protected abstract void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_);

    @Shadow
    protected abstract void transformFirstPerson(EnumHandSide hand, float swingProgress);

    @Shadow
    protected abstract void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack);

    @Shadow
    protected abstract void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_);

    @Shadow
    protected abstract void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide hand, float p_187465_3_, ItemStack stack);

    @Shadow
    protected abstract void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_);

    @Shadow
    public abstract void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded);

    /**
     * @author CCBlueX (superblaubeere27)
     */
    @Overwrite
    public void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_) {
        boolean flag = hand == EnumHand.MAIN_HAND;
        EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        GlStateManager.pushMatrix();

        if (stack.isEmpty()) {
            if (flag && !player.isInvisible()) {
                this.renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
            }
        } else if (stack.getItem() instanceof ItemMap) {
            if (flag && this.itemStackOffHand.isEmpty()) {
                this.renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
            } else {
                this.renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, stack);
            }
        } else {
            final KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
//            final TpAura tpAura = (TpAura) LiquidBounce.moduleManager.getModule(TpAura.class);

            final Animations oldhiting = (Animations) LiquidBounce.moduleManager.getModule(Animations.class);

            boolean flag1 = enumhandside == EnumHandSide.RIGHT;

            if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand) {
                int j = flag1 ? 1 : -1;

                EnumAction enumaction = killAura.getBlockingStatus() ? EnumAction.BLOCK : stack.getItemUseAction();

                switch (enumaction) {
                    case NONE:
                    case BLOCK:
                        transformSideFirstPersonBlock(enumhandside, p_187457_7_, p_187457_5_);

                        //this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;
                    case EAT:
                    case DRINK:
                        this.transformEatFirstPerson(p_187457_2_, enumhandside, stack);
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState())
                            rotateItemAnim();
                        break;
                    case BOW:
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        GlStateManager.translate((float) j * -0.2785682F, 0.18344387F, 0.15731531F);
                        GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate((float) j * 35.3F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate((float) j * -9.785F, 0.0F, 0.0F, 1.0F);
                        float f5 = (float) stack.getMaxItemUseDuration() - ((float) this.mc.player.getItemInUseCount() - p_187457_2_ + 1.0F);
                        float f6 = f5 / 20.0F;
                        f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;

                        if (f6 > 1.0F) {
                            f6 = 1.0F;
                        }

                        if (f6 > 0.1F) {
                            float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                            float f3 = f6 - 0.1F;
                            float f4 = f7 * f3;
                            GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                        }

                        GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                        GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                        GlStateManager.rotate((float) j * 45.0F, 0.0F, -1.0F, 0.0F);

                        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState())
                            rotateItemAnim();
                }
            } else {
                if (mc.player.getHeldItemMainhand().getItem() != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword
                        && ((killAura.getTarget() != null && killAura.getBlockingStatus())
                        || mc.gameSettings.keyBindUseItem.pressed) && oldhiting.getState()) {
                    GlStateManager.translate(Animations.xValue.get(), Animations.yValue.get(), Animations.zValue.get());

                    float SP =  (Animations.SPValue.get() ? p_187457_7_ : 0);
                    if (Animations.Sword.get().equals("1.7")) {
                        transformSideFirstPersonBlock(enumhandside, SP, p_187457_5_);

                        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState())
                            rotateItemAnim();
                    }
                    if (Animations.Sword.get().equals("Old")) {
                        transformSideFirstPersonBlock(enumhandside, -0.1F + SP, p_187457_5_);

                        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState())
                            rotateItemAnim();
                    }
                    if (Animations.Sword.get().equals("Push")) {
                        Push(enumhandside, SP, p_187457_5_);

                        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState())
                            rotateItemAnim();
                    }
                    if (Animations.Sword.get().equals("WindMill")) {
                        WindMill(enumhandside, -0.2F + SP, p_187457_5_);

                        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState())
                            rotateItemAnim();
                    }
                    if (Animations.Sword.get().equals("Smooth")) {
                        SmoothBlock(enumhandside, SP, p_187457_5_);

                        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState())
                            rotateItemAnim();
                    }
                    GlStateManager.scale(Animations.scaleValue.get(), Animations.scaleValue.get(), Animations.scaleValue.get());
                } else {
                    if (Animations.heldValue.get()) {
                        GlStateManager.translate(Animations.xhValue.get(), Animations.yhValue.get(), Animations.zhValue.get());
                    }
                    float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * (float) Math.PI);
                    float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * ((float) Math.PI * 2F));
                    float f2 = -0.2F * MathHelper.sin(p_187457_5_ * (float) Math.PI);
                    int i = flag1 ? 1 : -1;
                    GlStateManager.translate((float) i * f, f1, f2);
                    this.transformSideFirstPerson(enumhandside, p_187457_7_);
                    this.transformFirstPerson(enumhandside, p_187457_5_);
                    if (Animations.heldValue.get()) {
                        GlStateManager.scale(Animations.scalehValue.get(), Animations.scalehValue.get(), Animations.scalehValue.get());
                    }

                    if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState())
                        rotateItemAnim();
                }
                /*float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * (float) Math.PI);
                float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * ((float) Math.PI * 2F));
                float f2 = -0.2F * MathHelper.sin(p_187457_5_ * (float) Math.PI);
                int i = flag1 ? 1 : -1;
                GlStateManager.translate((float) i * f, f1, f2);
                this.transformSideFirstPerson(enumhandside, p_187457_7_);
                this.transformFirstPerson(enumhandside, p_187457_5_);*/
            }

            this.renderItemSide(player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
        }

        GlStateManager.popMatrix();
    }

    //防砍动画们
    private static void WindMill(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(side * 0.56, -0.52 + equippedProg * -0.6, -0.72);
        GlStateManager.translate(side * -0.1414214, 0.08, 0.1414214);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        double f = Math.sin(swingProgress * swingProgress * Math.PI);
        double f1 = Math.sin(Math.sqrt(swingProgress) * Math.PI);
        GlStateManager.rotate((float) (f * -20.0F), 0, 1, 0);
        GlStateManager.rotate((float) (f1 * -20.0F), 0, 0, 1);
        GlStateManager.rotate((float) (f1 * -50.0F), 1, 0, 0);

    }
    private static void Push(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(side * 0.56, -0.52 + equippedProg * -0.6, -0.72);
        GlStateManager.translate(side * -0.1414214, 0.08, 0.1414214);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        double f = Math.sin(swingProgress * swingProgress * Math.PI);
        double f1 = Math.sin(Math.sqrt(swingProgress) * Math.PI);
        GlStateManager.rotate((float) (f *  -10.0F), 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate((float) (f1 * -10.0F), 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate((float) (f1 * -10.0F), 1.0F, 1.0F, 1.0F);
    }

    private static void transformSideFirstPersonBlock(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(side * 0.56, -0.52 + equippedProg * -0.6, -0.72);
        GlStateManager.translate(side * -0.1414214, 0.08, 0.1414214);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        double f = Math.sin(swingProgress * swingProgress * Math.PI);
        double f1 = Math.sin(Math.sqrt(swingProgress) * Math.PI);
        GlStateManager.rotate((float) (f * -20.0F), 0, 1, 0);
        GlStateManager.rotate((float) (f1 * -20.0F), 0, 0, 1);
        GlStateManager.rotate((float) (f1 * -80.0F), 1, 0, 0);
    }

    private static void SmoothBlock(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(side * 0.56, -0.52 + equippedProg * -0.6, -0.72);
        GlStateManager.translate(side * -0.1414214, 0.08, 0.1414214);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        double f = Math.sin(swingProgress * swingProgress * Math.PI);
        double f1 = Math.sin(Math.sqrt(swingProgress) * Math.PI);
        GlStateManager.rotate((float) (f * -20.0F), 0, 1, 0);
        GlStateManager.rotate((float) (f1 * -20.0F), 0, 0, 1);
        GlStateManager.rotate((float) (f1 * -30.0F), 1, 0, 0);
    }

    /**
     * @author CCBlueX
     */


    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void renderFireInFirstPerson(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);

        if (antiBlind.getState() && antiBlind.getFireEffect().get()) callbackInfo.cancel();
    }

    private void rotateItemAnim() {

        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState()&& ItemShow.transformFirstPersonRotate.get().equalsIgnoreCase("Rotate1")) {
            GlStateManager.rotate(this.delay, 0.0F, 1.0F, 0.0F);
        }
        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState()&&ItemShow.transformFirstPersonRotate.get().equalsIgnoreCase("Rotate2")) {
            GlStateManager.rotate(this.delay, 1.0F, 1.0F, 0.0F);
        }
        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState()&&ItemShow.transformFirstPersonRotate.get().equalsIgnoreCase("Rotate3")) {
            GlStateManager.rotate(this.delay,360, -360, -200);
        }
        if (LiquidBounce.moduleManager.getModule(ItemShow.class).getState()&&ItemShow.transformFirstPersonRotate.get().equalsIgnoreCase("Custom")) {
            GlStateManager.rotate(this.delay, ItemShow.customRotate1.get(),ItemShow.customRotate2.get(),ItemShow.customRotate3.get());
        }


        if (this.rotateTimer.hasTimePassed(1)) {
            ++this.delay;
            this.delay = this.delay + ItemShow.SpeedRotate.get();
            this.rotateTimer.reset();
        }
        if (this.delay > 360.0F) {
            this.delay = 0.0F;
        }
    }


    @Overwrite
    public void updateEquippedItem()
    {
        final Animations oldhiting = (Animations) LiquidBounce.moduleManager.getModule(Animations.class);

        this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
        this.prevEquippedProgressOffHand = this.equippedProgressOffHand;
        EntityPlayerSP entityplayersp = this.mc.player;
        ItemStack itemstack = entityplayersp.getHeldItemMainhand();
        ItemStack itemstack1 = entityplayersp.getHeldItemOffhand();
        if (entityplayersp.isRowingBoat()) {
            this.equippedProgressMainHand = MathHelper.clamp(this.equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
            this.equippedProgressOffHand = MathHelper.clamp(this.equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
        } else {
            float f = entityplayersp.getCooledAttackStrength(1.0F);
            boolean requipM = ForgeHooksClient.shouldCauseReequipAnimation(this.itemStackMainHand, itemstack, entityplayersp.inventory.currentItem);
            boolean requipO = ForgeHooksClient.shouldCauseReequipAnimation(this.itemStackOffHand, itemstack1, -1);
            if (!requipM && !Objects.equals(this.itemStackMainHand, itemstack)) {
                this.itemStackMainHand = itemstack;
            }

            if (!requipM && !Objects.equals(this.itemStackOffHand, itemstack1)) {
                this.itemStackOffHand = itemstack1;
            }

            float number = Animations.oldSPValue.get() ? 1f : f * f * f;
            this.equippedProgressMainHand += MathHelper.clamp((!requipM ? number : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
            this.equippedProgressOffHand += MathHelper.clamp((float)(!requipO ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
        }

        if (this.equippedProgressMainHand < 0.1F) {
            this.itemStackMainHand = itemstack;
        }

        if (this.equippedProgressOffHand < 0.1F) {
            this.itemStackOffHand = itemstack1;
        }
    }
}