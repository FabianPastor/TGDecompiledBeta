package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;

public class StickerTabView extends FrameLayout {
    private static int indexPointer;
    public float dragOffset;
    ValueAnimator dragOffsetAnimator;
    boolean expanded;
    boolean hasSavedLeft;
    ImageView iconView;
    BackupImageView imageView;
    public final int index;
    public boolean inited;
    public boolean isChatSticker;
    float lastLeft;
    boolean roundImage;
    public SvgHelper.SvgDrawable svgThumb;
    TextView textView;
    public int type;
    View visibleView;

    public StickerTabView(Context context, int i) {
        super(context);
        this.type = i;
        int i2 = indexPointer;
        indexPointer = i2 + 1;
        this.index = i2;
        if (i == 2) {
            BackupImageView backupImageView = new BackupImageView(getContext());
            this.imageView = backupImageView;
            backupImageView.setLayerNum(1);
            this.imageView.setAspectFit(false);
            addView(this.imageView, LayoutHelper.createFrame(36, 36, 17));
            this.visibleView = this.imageView;
        } else if (i == 1) {
            ImageView imageView2 = new ImageView(context);
            this.iconView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(this.iconView, LayoutHelper.createFrame(24, 24, 17));
            this.visibleView = this.iconView;
        } else {
            BackupImageView backupImageView2 = new BackupImageView(getContext());
            this.imageView = backupImageView2;
            backupImageView2.setLayerNum(1);
            this.imageView.setAspectFit(true);
            addView(this.imageView, LayoutHelper.createFrame(30, 30, 17));
            this.visibleView = this.imageView;
        }
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setLines(1);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setTextSize(1, 11.0f);
        this.textView.setGravity(1);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 81, 8.0f, 0.0f, 8.0f, 10.0f));
        this.textView.setVisibility(8);
    }

    public void setExpanded(boolean z) {
        int i = this.type;
        if (i != 2) {
            this.expanded = z;
            float f = i == 1 ? 24.0f : 30.0f;
            float f2 = i == 1 ? 38.0f : 56.0f;
            this.visibleView.getLayoutParams().width = AndroidUtilities.dp(z ? f2 : f);
            ViewGroup.LayoutParams layoutParams = this.visibleView.getLayoutParams();
            if (z) {
                f = f2;
            }
            layoutParams.height = AndroidUtilities.dp(f);
            this.textView.setVisibility(z ? 0 : 8);
            if (this.type != 1 && this.roundImage) {
                this.imageView.setRoundRadius(AndroidUtilities.dp(((float) this.visibleView.getLayoutParams().width) / 2.0f));
            }
        }
    }

    public void updateExpandProgress(float f) {
        int i = this.type;
        if (i != 2) {
            if (this.expanded) {
                float f2 = i == 1 ? 24.0f : 30.0f;
                float f3 = i == 1 ? 38.0f : 56.0f;
                float f4 = 1.0f - f;
                this.visibleView.setTranslationY((((((float) AndroidUtilities.dp(48.0f - f2)) / 2.0f) - (((float) AndroidUtilities.dp(98.0f - f3)) / 2.0f)) * f4) - (((float) AndroidUtilities.dp(8.0f)) * f));
                this.visibleView.setTranslationX(((((float) AndroidUtilities.dp(52.0f - f2)) / 2.0f) - (((float) AndroidUtilities.dp(86.0f - f3)) / 2.0f)) * f4);
                this.textView.setAlpha(Math.max(0.0f, (f - 0.5f) / 0.5f));
                this.textView.setTranslationY(((float) (-AndroidUtilities.dp(40.0f))) * f4);
                this.textView.setTranslationX(((float) (-AndroidUtilities.dp(12.0f))) * f4);
                this.visibleView.setPivotX(0.0f);
                this.visibleView.setPivotY(0.0f);
                float f5 = ((f2 / f3) * f4) + f;
                this.visibleView.setScaleX(f5);
                this.visibleView.setScaleY(f5);
                return;
            }
            this.visibleView.setTranslationX(0.0f);
            this.visibleView.setTranslationY(0.0f);
            this.visibleView.setScaleX(1.0f);
            this.visibleView.setScaleY(1.0f);
        }
    }

    public void saveXPosition() {
        this.lastLeft = (float) getLeft();
        this.hasSavedLeft = true;
        invalidate();
    }

    public void animateIfPositionChanged(final ViewGroup viewGroup) {
        float f = this.lastLeft;
        if (((float) getLeft()) != f && this.hasSavedLeft) {
            this.dragOffset = f - ((float) getLeft());
            ValueAnimator valueAnimator = this.dragOffsetAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.dragOffsetAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.dragOffset, 0.0f});
            this.dragOffsetAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    StickerTabView.this.dragOffset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    StickerTabView.this.invalidate();
                    viewGroup.invalidate();
                }
            });
            this.dragOffsetAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    StickerTabView stickerTabView = StickerTabView.this;
                    stickerTabView.dragOffset = 0.0f;
                    stickerTabView.invalidate();
                    viewGroup.invalidate();
                }
            });
            this.dragOffsetAnimator.start();
        }
        this.hasSavedLeft = false;
    }

    public void setRoundImage() {
        this.roundImage = true;
    }
}