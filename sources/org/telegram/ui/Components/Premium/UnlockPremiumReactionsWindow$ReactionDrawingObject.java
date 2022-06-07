package org.telegram.ui.Components.Premium;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.Components.Premium.CarouselView;

public class UnlockPremiumReactionsWindow$ReactionDrawingObject extends CarouselView.DrawingObject {
    ImageReceiver actionReceiver = new ImageReceiver();
    ImageReceiver effectImageReceiver = new ImageReceiver();
    ImageReceiver imageReceiver = new ImageReceiver();
    private View parentView;
    TLRPC$TL_availableReaction reaction;
    Rect rect = new Rect();
    private boolean selected;
    private float selectedProgress;

    public UnlockPremiumReactionsWindow$ReactionDrawingObject(int i) {
    }

    public void onAttachToWindow(View view, int i) {
        this.parentView = view;
        if (i == 0) {
            this.imageReceiver.setParentView(view);
            this.imageReceiver.onAttachedToWindow();
            this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
            this.imageReceiver.setImage(ImageLocation.getForDocument(this.reaction.appear_animation), "60_60_nolimit", (ImageLocation) null, (String) null, DocumentObject.getSvgThumb(this.reaction.activate_animation, "windowBackgroundGray", 0.5f), 0, "tgs", this.reaction, 0);
            this.imageReceiver.setAutoRepeat(0);
            if (this.imageReceiver.getLottieAnimation() != null) {
                this.imageReceiver.getLottieAnimation().setCurrentFrame(0, false);
            }
            this.imageReceiver.startAnimation();
            return;
        }
        this.effectImageReceiver.setParentView(view);
        this.effectImageReceiver.onAttachedToWindow();
        this.effectImageReceiver.setLayerNum(Integer.MAX_VALUE);
        this.actionReceiver.setParentView(view);
        this.actionReceiver.onAttachedToWindow();
        this.actionReceiver.setLayerNum(Integer.MAX_VALUE);
        this.effectImageReceiver.setAllowStartLottieAnimation(false);
        this.effectImageReceiver.setImage(ImageLocation.getForDocument(this.reaction.around_animation), "200_200", (ImageLocation) null, (String) null, (Drawable) null, 0, "tgs", this.reaction, 0);
        this.effectImageReceiver.setAutoRepeat(0);
        if (this.effectImageReceiver.getLottieAnimation() != null) {
            this.effectImageReceiver.getLottieAnimation().setCurrentFrame(0, false);
            this.effectImageReceiver.getLottieAnimation().stop();
        }
        this.actionReceiver.setAllowStartLottieAnimation(false);
        this.actionReceiver.setImage(ImageLocation.getForDocument(this.reaction.activate_animation), "60_60_nolimit", (ImageLocation) null, (String) null, (Drawable) null, 0, "tgs", this.reaction, 0);
        this.actionReceiver.setAutoRepeat(0);
        if (this.actionReceiver.getLottieAnimation() != null) {
            this.actionReceiver.getLottieAnimation().setCurrentFrame(0, false);
            this.actionReceiver.getLottieAnimation().stop();
        }
    }

    public void onDetachFromWindow() {
        this.imageReceiver.onDetachedFromWindow();
        this.imageReceiver.setParentView((View) null);
        this.effectImageReceiver.onDetachedFromWindow();
        this.effectImageReceiver.setParentView((View) null);
        this.actionReceiver.onDetachedFromWindow();
        this.actionReceiver.setParentView((View) null);
    }

    public void draw(Canvas canvas, float f, float f2, float f3) {
        int dp = (int) (((float) AndroidUtilities.dp(350.0f)) * f3);
        float dp2 = (float) ((int) (((float) AndroidUtilities.dp(120.0f)) * f3));
        float f4 = dp2 / 2.0f;
        float f5 = f - f4;
        float f6 = f2 - f4;
        this.rect.set((int) f5, (int) f6, (int) (f + f4), (int) (f4 + f2));
        this.imageReceiver.setImageCoords(f5, f6, dp2, dp2);
        this.actionReceiver.setImageCoords(f5, f6, dp2, dp2);
        if (this.selected || this.selectedProgress != 0.0f) {
            float f7 = (float) dp;
            float f8 = f7 / 2.0f;
            this.effectImageReceiver.setImageCoords(f - f8, f2 - f8, f7, f7);
            this.effectImageReceiver.setAlpha(this.selectedProgress);
            float f9 = this.selectedProgress;
            if (f9 != 1.0f) {
                float var_ = (f9 * 0.3f) + 0.7f;
                canvas.save();
                canvas.scale(var_, var_, f, f2);
                this.effectImageReceiver.draw(canvas);
                canvas.restore();
            } else {
                this.effectImageReceiver.draw(canvas);
            }
            if (this.selected && this.effectImageReceiver.getLottieAnimation() != null && !this.effectImageReceiver.getLottieAnimation().isRunning() && !this.effectImageReceiver.getLottieAnimation().isLastFrame()) {
                this.effectImageReceiver.getLottieAnimation().start();
            }
            if (this.selected && this.effectImageReceiver.getLottieAnimation() != null && !this.effectImageReceiver.getLottieAnimation().isRunning() && this.effectImageReceiver.getLottieAnimation().isLastFrame()) {
                this.selected = false;
            }
            boolean z = this.selected;
            if (z) {
                float var_ = this.selectedProgress;
                if (var_ != 1.0f) {
                    float var_ = var_ + 0.08f;
                    this.selectedProgress = var_;
                    if (var_ > 1.0f) {
                        this.selectedProgress = 1.0f;
                    }
                }
            }
            if (!z) {
                float var_ = this.selectedProgress - 0.08f;
                this.selectedProgress = var_;
                if (var_ < 0.0f) {
                    this.selectedProgress = 0.0f;
                }
            }
        }
        if (this.actionReceiver.getLottieAnimation() == null || !this.actionReceiver.getLottieAnimation().hasBitmap()) {
            this.imageReceiver.draw(canvas);
            return;
        }
        this.actionReceiver.draw(canvas);
        if (this.actionReceiver.getLottieAnimation() != null && this.actionReceiver.getLottieAnimation().isLastFrame()) {
            this.selected = false;
        } else if (this.selected && this.actionReceiver.getLottieAnimation() != null && !this.actionReceiver.getLottieAnimation().isRunning()) {
            this.actionReceiver.getLottieAnimation().start();
        }
    }

    public boolean checkTap(float f, float f2) {
        if (!this.rect.contains((int) f, (int) f2)) {
            return false;
        }
        select();
        return true;
    }

    public void select() {
        if (!this.selected) {
            this.selected = true;
            if (this.selectedProgress == 0.0f) {
                this.selectedProgress = 1.0f;
            }
            System.currentTimeMillis();
            if (this.effectImageReceiver.getLottieAnimation() != null) {
                this.effectImageReceiver.getLottieAnimation().setCurrentFrame(0, false);
                this.effectImageReceiver.getLottieAnimation().start();
            }
            if (this.actionReceiver.getLottieAnimation() != null) {
                this.actionReceiver.getLottieAnimation().setCurrentFrame(0, false);
                this.actionReceiver.getLottieAnimation().start();
            }
            this.parentView.invalidate();
        }
    }

    public void hideAnimation() {
        super.hideAnimation();
        this.selected = false;
    }

    public void set(TLRPC$TL_availableReaction tLRPC$TL_availableReaction) {
        this.reaction = tLRPC$TL_availableReaction;
    }
}
