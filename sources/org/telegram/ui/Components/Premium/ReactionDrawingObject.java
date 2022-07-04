package org.telegram.ui.Components.Premium;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.Premium.CarouselView;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;

public class ReactionDrawingObject extends CarouselView.DrawingObject {
    ImageReceiver actionReceiver = new ImageReceiver();
    ImageReceiver effectImageReceiver = new ImageReceiver();
    ImageReceiver imageReceiver = new ImageReceiver();
    long lastSelectedTime;
    private View parentView;
    private int position;
    TLRPC.TL_availableReaction reaction;
    Rect rect = new Rect();
    private boolean selected;
    private float selectedProgress;

    public ReactionDrawingObject(int i) {
        this.position = i;
    }

    public void onAttachToWindow(View parentView2, int i) {
        View view = parentView2;
        this.parentView = view;
        if (i == 0) {
            this.imageReceiver.setParentView(view);
            this.imageReceiver.onAttachedToWindow();
            this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(this.reaction.activate_animation, "windowBackgroundGray", 0.5f);
            this.actionReceiver.setParentView(view);
            this.actionReceiver.onAttachedToWindow();
            this.actionReceiver.setLayerNum(Integer.MAX_VALUE);
            this.actionReceiver.setAllowStartLottieAnimation(false);
            this.actionReceiver.setImage(ImageLocation.getForDocument(this.reaction.activate_animation), "50_50_nolimit", (ImageLocation) null, (String) null, svgThumb, 0, "tgs", this.reaction, 0);
            this.actionReceiver.setAutoRepeat(0);
            if (this.actionReceiver.getLottieAnimation() != null) {
                this.actionReceiver.getLottieAnimation().setCurrentFrame(0, false);
                this.actionReceiver.getLottieAnimation().stop();
                return;
            }
            return;
        }
        this.effectImageReceiver.setParentView(view);
        this.effectImageReceiver.onAttachedToWindow();
        this.effectImageReceiver.setLayerNum(Integer.MAX_VALUE);
        this.effectImageReceiver.setAllowStartLottieAnimation(false);
        int size = ReactionsEffectOverlay.sizeForBigReaction();
        ImageReceiver imageReceiver2 = this.effectImageReceiver;
        ImageLocation forDocument = ImageLocation.getForDocument(this.reaction.around_animation);
        imageReceiver2.setImage(forDocument, size + "_" + size, (ImageLocation) null, (String) null, (Drawable) null, 0, "tgs", this.reaction, 0);
        this.effectImageReceiver.setAutoRepeat(0);
        if (this.effectImageReceiver.getLottieAnimation() != null) {
            this.effectImageReceiver.getLottieAnimation().setCurrentFrame(0, false);
            this.effectImageReceiver.getLottieAnimation().stop();
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

    public void draw(Canvas canvas, float cX, float cY, float globalScale) {
        int imageSize = (int) (((float) AndroidUtilities.dp(120.0f)) * globalScale);
        int effectSize = (int) (((float) AndroidUtilities.dp(350.0f)) * globalScale);
        this.rect.set((int) (cX - (((float) imageSize) / 2.0f)), (int) (cY - (((float) imageSize) / 2.0f)), (int) ((((float) imageSize) / 2.0f) + cX), (int) ((((float) imageSize) / 2.0f) + cY));
        this.imageReceiver.setImageCoords(cX - (((float) imageSize) / 2.0f), cY - (((float) imageSize) / 2.0f), (float) imageSize, (float) imageSize);
        this.actionReceiver.setImageCoords(cX - (((float) imageSize) / 2.0f), cY - (((float) imageSize) / 2.0f), (float) imageSize, (float) imageSize);
        if (this.actionReceiver.getLottieAnimation() != null && this.actionReceiver.getLottieAnimation().hasBitmap()) {
            this.actionReceiver.draw(canvas);
            if ((this.actionReceiver.getLottieAnimation() == null || !this.actionReceiver.getLottieAnimation().isLastFrame()) && this.selected && this.actionReceiver.getLottieAnimation() != null && !this.actionReceiver.getLottieAnimation().isRunning()) {
                this.actionReceiver.getLottieAnimation().start();
            }
        }
        if (this.selected || this.selectedProgress != 0.0f) {
            this.effectImageReceiver.setImageCoords(cX - (((float) effectSize) / 2.0f), cY - (((float) effectSize) / 2.0f), (float) effectSize, (float) effectSize);
            this.effectImageReceiver.setAlpha(this.selectedProgress);
            float f = this.selectedProgress;
            if (f != 1.0f) {
                float s = (f * 0.3f) + 0.7f;
                canvas.save();
                canvas.scale(s, s, cX, cY);
                this.effectImageReceiver.draw(canvas);
                canvas.restore();
            } else {
                this.effectImageReceiver.draw(canvas);
            }
            if (this.selected && this.effectImageReceiver.getLottieAnimation() != null && this.effectImageReceiver.getLottieAnimation().isLastFrame()) {
                this.carouselView.autoplayToNext();
            }
            if (this.selected && this.effectImageReceiver.getLottieAnimation() != null && !this.effectImageReceiver.getLottieAnimation().isRunning() && !this.effectImageReceiver.getLottieAnimation().isLastFrame()) {
                this.effectImageReceiver.getLottieAnimation().start();
            }
            if (this.selected && this.effectImageReceiver.getLottieAnimation() != null && !this.effectImageReceiver.getLottieAnimation().isRunning() && this.effectImageReceiver.getLottieAnimation().isLastFrame()) {
                this.selected = false;
            }
            boolean z = this.selected;
            if (z) {
                float f2 = this.selectedProgress;
                if (f2 != 1.0f) {
                    float f3 = f2 + 0.08f;
                    this.selectedProgress = f3;
                    if (f3 > 1.0f) {
                        this.selectedProgress = 1.0f;
                        return;
                    }
                    return;
                }
            }
            if (!z) {
                float f4 = this.selectedProgress - 0.08f;
                this.selectedProgress = f4;
                if (f4 < 0.0f) {
                    this.selectedProgress = 0.0f;
                }
            }
        }
    }

    public boolean checkTap(float x, float y) {
        if (!this.rect.contains((int) x, (int) y)) {
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
            this.lastSelectedTime = System.currentTimeMillis();
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

    public void set(TLRPC.TL_availableReaction reaction2) {
        this.reaction = reaction2;
    }
}
