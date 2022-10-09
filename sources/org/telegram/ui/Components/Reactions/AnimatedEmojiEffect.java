package org.telegram.ui.Components.Reactions;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.RLottieDrawable;
/* loaded from: classes3.dex */
public class AnimatedEmojiEffect {
    public AnimatedEmojiDrawable animatedEmojiDrawable;
    int currentAccount;
    ImageReceiver effectImageReceiver;
    long lastGenerateTime;
    boolean longAnimation;
    View parentView;
    boolean showGeneric;
    Rect bounds = new Rect();
    ArrayList<Particle> particles = new ArrayList<>();
    boolean firsDraw = true;
    int animationIndex = -1;
    long startTime = System.currentTimeMillis();

    private AnimatedEmojiEffect(AnimatedEmojiDrawable animatedEmojiDrawable, int i, boolean z, boolean z2) {
        this.animatedEmojiDrawable = animatedEmojiDrawable;
        this.longAnimation = z;
        this.currentAccount = i;
        this.showGeneric = z2;
        if (z || !z2) {
            return;
        }
        this.effectImageReceiver = new ImageReceiver();
    }

    public static AnimatedEmojiEffect createFrom(AnimatedEmojiDrawable animatedEmojiDrawable, boolean z, boolean z2) {
        return new AnimatedEmojiEffect(animatedEmojiDrawable, UserConfig.selectedAccount, z, z2);
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        this.bounds.set(i, i2, i3, i4);
        ImageReceiver imageReceiver = this.effectImageReceiver;
        if (imageReceiver != null) {
            imageReceiver.setImageCoords(this.bounds);
        }
    }

    public void draw(Canvas canvas) {
        if (!this.longAnimation) {
            if (this.firsDraw) {
                for (int i = 0; i < 7; i++) {
                    Particle particle = new Particle();
                    particle.generate();
                    this.particles.add(particle);
                }
            }
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if (this.particles.size() < 12) {
                long j = this.startTime;
                if (currentTimeMillis - j < 1500 && currentTimeMillis - j > 200 && currentTimeMillis - this.lastGenerateTime > 50 && Utilities.fastRandom.nextInt() % 6 == 0) {
                    Particle particle2 = new Particle();
                    particle2.generate();
                    this.particles.add(particle2);
                    this.lastGenerateTime = currentTimeMillis;
                }
            }
        }
        ImageReceiver imageReceiver = this.effectImageReceiver;
        if (imageReceiver != null && this.showGeneric) {
            imageReceiver.draw(canvas);
        }
        int i2 = 0;
        while (i2 < this.particles.size()) {
            this.particles.get(i2).draw(canvas);
            if (this.particles.get(i2).progress >= 1.0f) {
                this.particles.remove(i2);
                i2--;
            }
            i2++;
        }
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
        this.firsDraw = false;
    }

    public boolean done() {
        return System.currentTimeMillis() - this.startTime > 2500;
    }

    public void setView(View view) {
        boolean z;
        TLRPC$TL_availableReaction tLRPC$TL_availableReaction;
        TLRPC$Document tLRPC$Document;
        this.animatedEmojiDrawable.addView(view);
        this.parentView = view;
        ImageReceiver imageReceiver = this.effectImageReceiver;
        if (imageReceiver == null || !this.showGeneric) {
            return;
        }
        imageReceiver.onAttachedToWindow();
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
        String findAnimatedEmojiEmoticon = MessageObject.findAnimatedEmojiEmoticon(this.animatedEmojiDrawable.getDocument(), null);
        if (findAnimatedEmojiEmoticon == null || (tLRPC$TL_availableReaction = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(findAnimatedEmojiEmoticon)) == null || (tLRPC$Document = tLRPC$TL_availableReaction.around_animation) == null) {
            z = false;
        } else {
            this.effectImageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), ReactionsEffectOverlay.getFilterForAroundAnimation(), null, null, tLRPC$TL_availableReaction.around_animation, 0);
            z = true;
        }
        if (!z) {
            String str = UserConfig.getInstance(this.currentAccount).genericAnimationsStickerPack;
            if (str != null && (tLRPC$TL_messages_stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(str)) == null) {
                tLRPC$TL_messages_stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName(str);
            }
            if (tLRPC$TL_messages_stickerSet != null) {
                if (this.animationIndex < 0) {
                    this.animationIndex = Math.abs(Utilities.fastRandom.nextInt() % tLRPC$TL_messages_stickerSet.documents.size());
                }
                this.effectImageReceiver.setImage(ImageLocation.getForDocument(tLRPC$TL_messages_stickerSet.documents.get(this.animationIndex)), "60_60", null, null, tLRPC$TL_messages_stickerSet.documents.get(this.animationIndex), 0);
                z = true;
            }
        }
        if (z) {
            if (this.effectImageReceiver.getLottieAnimation() != null) {
                this.effectImageReceiver.getLottieAnimation().setCurrentFrame(0, false, true);
            }
            this.effectImageReceiver.setAutoRepeat(0);
            return;
        }
        int i = R.raw.custom_emoji_reaction;
        this.effectImageReceiver.setImageBitmap(new RLottieDrawable(i, "" + i, AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f), false, null));
    }

    public void removeView(View view) {
        this.animatedEmojiDrawable.removeView(view);
        ImageReceiver imageReceiver = this.effectImageReceiver;
        if (imageReceiver != null) {
            imageReceiver.onDetachedFromWindow();
            this.effectImageReceiver.clearImage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class Particle {
        long duration;
        float fromSize;
        float fromX;
        float fromY;
        boolean mirror;
        float progress;
        float randomRotation;
        float toSize;
        float toX;
        float toY1;
        float toY2;

        private Particle() {
        }

        public void generate() {
            float f = 0.0f;
            this.progress = 0.0f;
            float randX = randX();
            float randY = randY();
            for (int i = 0; i < 20; i++) {
                float randX2 = randX();
                float randY2 = randY();
                float f2 = 2.14748365E9f;
                for (int i2 = 0; i2 < AnimatedEmojiEffect.this.particles.size(); i2++) {
                    float f3 = AnimatedEmojiEffect.this.particles.get(i2).toX - randX2;
                    float f4 = AnimatedEmojiEffect.this.particles.get(i2).toY1 - randY2;
                    float f5 = (f3 * f3) + (f4 * f4);
                    if (f5 < f2) {
                        f2 = f5;
                    }
                }
                if (f2 > f) {
                    randX = randX2;
                    randY = randY2;
                    f = f2;
                }
            }
            AnimatedEmojiEffect animatedEmojiEffect = AnimatedEmojiEffect.this;
            float f6 = animatedEmojiEffect.longAnimation ? 0.8f : 0.5f;
            this.toX = randX;
            if (randX > animatedEmojiEffect.bounds.width() * f6) {
                this.fromX = AnimatedEmojiEffect.this.bounds.width() * f6;
            } else {
                float width = AnimatedEmojiEffect.this.bounds.width() * f6;
                this.fromX = width;
                if (this.toX > width) {
                    this.toX = width - 0.1f;
                }
            }
            this.fromY = (AnimatedEmojiEffect.this.bounds.height() * 0.45f) + (AnimatedEmojiEffect.this.bounds.height() * 0.1f * (Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f));
            AnimatedEmojiEffect animatedEmojiEffect2 = AnimatedEmojiEffect.this;
            if (animatedEmojiEffect2.longAnimation) {
                float width2 = (animatedEmojiEffect2.bounds.width() * 0.05f) + (AnimatedEmojiEffect.this.bounds.width() * 0.1f * (Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f));
                this.fromSize = width2;
                this.toSize = width2 * (((Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f) * 1.5f) + 1.5f);
                this.toY1 = (this.fromSize / 2.0f) + (AnimatedEmojiEffect.this.bounds.height() * 0.1f * (Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f));
                this.toY2 = AnimatedEmojiEffect.this.bounds.height() + this.fromSize;
                this.duration = Math.abs(Utilities.fastRandom.nextInt() % 600) + 1000;
            } else {
                float width3 = (animatedEmojiEffect2.bounds.width() * 0.05f) + (AnimatedEmojiEffect.this.bounds.width() * 0.1f * (Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f));
                this.fromSize = width3;
                this.toSize = width3 * (((Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f) * 0.5f) + 1.5f);
                this.toY1 = randY;
                this.toY2 = randY + AnimatedEmojiEffect.this.bounds.height();
                this.duration = 1800L;
            }
            this.mirror = Utilities.fastRandom.nextBoolean();
            this.randomRotation = ((Utilities.fastRandom.nextInt() % 100) / 100.0f) * 20.0f;
        }

        private float randY() {
            return AnimatedEmojiEffect.this.bounds.height() * 0.5f * (Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f);
        }

        private float randX() {
            AnimatedEmojiEffect animatedEmojiEffect = AnimatedEmojiEffect.this;
            if (animatedEmojiEffect.longAnimation) {
                return (animatedEmojiEffect.bounds.width() * (-0.25f)) + (AnimatedEmojiEffect.this.bounds.width() * 1.5f * (Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f));
            }
            return animatedEmojiEffect.bounds.width() * (Math.abs(Utilities.fastRandom.nextInt() % 100) / 100.0f);
        }

        /* JADX WARN: Removed duplicated region for block: B:14:0x008c  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void draw(android.graphics.Canvas r12) {
            /*
                r11 = this;
                float r0 = r11.progress
                long r1 = r11.duration
                float r1 = (float) r1
                r2 = 1098907648(0x41800000, float:16.0)
                float r1 = r2 / r1
                float r0 = r0 + r1
                r11.progress = r0
                r1 = 1065353216(0x3var_, float:1.0)
                r3 = 0
                float r0 = org.telegram.messenger.Utilities.clamp(r0, r1, r3)
                r11.progress = r0
                org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                float r0 = r4.getInterpolation(r0)
                float r5 = r11.fromX
                float r6 = r11.toX
                float r5 = org.telegram.messenger.AndroidUtilities.lerp(r5, r6, r0)
                org.telegram.ui.Components.Reactions.AnimatedEmojiEffect r6 = org.telegram.ui.Components.Reactions.AnimatedEmojiEffect.this
                boolean r6 = r6.longAnimation
                r6 = 1050253722(0x3e99999a, float:0.3)
                r7 = 1060320051(0x3var_, float:0.7)
                float r8 = r11.progress
                int r9 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r9 >= 0) goto L41
                float r7 = r11.fromY
                float r9 = r11.toY1
                float r8 = r8 / r6
                float r4 = r4.getInterpolation(r8)
                float r4 = org.telegram.messenger.AndroidUtilities.lerp(r7, r9, r4)
                goto L51
            L41:
                float r4 = r11.toY1
                float r9 = r11.toY2
                org.telegram.ui.Components.CubicBezierInterpolator r10 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
                float r8 = r8 - r6
                float r8 = r8 / r7
                float r6 = r10.getInterpolation(r8)
                float r4 = org.telegram.messenger.AndroidUtilities.lerp(r4, r9, r6)
            L51:
                float r6 = r11.fromSize
                float r7 = r11.toSize
                float r0 = org.telegram.messenger.AndroidUtilities.lerp(r6, r7, r0)
                org.telegram.ui.Components.Reactions.AnimatedEmojiEffect r6 = org.telegram.ui.Components.Reactions.AnimatedEmojiEffect.this
                boolean r7 = r6.longAnimation
                if (r7 != 0) goto L7e
                android.graphics.Rect r6 = r6.bounds
                int r6 = r6.height()
                float r6 = (float) r6
                r7 = 1061997773(0x3f4ccccd, float:0.8)
                float r6 = r6 * r7
                int r7 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r7 <= 0) goto L7e
                float r6 = r4 - r6
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                float r6 = r6 / r2
                float r2 = org.telegram.messenger.Utilities.clamp(r6, r1, r3)
                float r2 = r1 - r2
                goto L80
            L7e:
                r2 = 1065353216(0x3var_, float:1.0)
            L80:
                r6 = 1073741824(0x40000000, float:2.0)
                float r0 = r0 / r6
                float r0 = r0 * r2
                r12.save()
                boolean r6 = r11.mirror
                if (r6 == 0) goto L91
                r6 = -1082130432(0xffffffffbvar_, float:-1.0)
                r12.scale(r6, r1, r5, r4)
            L91:
                float r6 = r11.randomRotation
                r12.rotate(r6, r5, r4)
                org.telegram.ui.Components.Reactions.AnimatedEmojiEffect r6 = org.telegram.ui.Components.Reactions.AnimatedEmojiEffect.this
                org.telegram.ui.Components.AnimatedEmojiDrawable r6 = r6.animatedEmojiDrawable
                r7 = 1132396544(0x437var_, float:255.0)
                float r2 = r2 * r7
                float r7 = r11.progress
                r8 = 1045220557(0x3e4ccccd, float:0.2)
                float r7 = r7 / r8
                float r1 = org.telegram.messenger.Utilities.clamp(r7, r1, r3)
                float r2 = r2 * r1
                int r1 = (int) r2
                r6.setAlpha(r1)
                org.telegram.ui.Components.Reactions.AnimatedEmojiEffect r1 = org.telegram.ui.Components.Reactions.AnimatedEmojiEffect.this
                org.telegram.ui.Components.AnimatedEmojiDrawable r1 = r1.animatedEmojiDrawable
                float r2 = r5 - r0
                int r2 = (int) r2
                float r3 = r4 - r0
                int r3 = (int) r3
                float r5 = r5 + r0
                int r5 = (int) r5
                float r4 = r4 + r0
                int r0 = (int) r4
                r1.setBounds(r2, r3, r5, r0)
                org.telegram.ui.Components.Reactions.AnimatedEmojiEffect r0 = org.telegram.ui.Components.Reactions.AnimatedEmojiEffect.this
                org.telegram.ui.Components.AnimatedEmojiDrawable r0 = r0.animatedEmojiDrawable
                r0.draw(r12)
                org.telegram.ui.Components.Reactions.AnimatedEmojiEffect r0 = org.telegram.ui.Components.Reactions.AnimatedEmojiEffect.this
                org.telegram.ui.Components.AnimatedEmojiDrawable r0 = r0.animatedEmojiDrawable
                r1 = 255(0xff, float:3.57E-43)
                r0.setAlpha(r1)
                r12.restore()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.AnimatedEmojiEffect.Particle.draw(android.graphics.Canvas):void");
        }
    }
}
