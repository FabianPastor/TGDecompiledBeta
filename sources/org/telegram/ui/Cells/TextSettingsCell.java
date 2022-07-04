package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

public class TextSettingsCell extends FrameLayout {
    private boolean canDisable;
    private int changeProgressStartDelay;
    private boolean drawLoading;
    private float drawLoadingProgress;
    private ImageView imageView;
    private boolean incrementLoadingProgress;
    private float loadingProgress;
    private int loadingSize;
    private boolean measureDelay;
    private boolean needDivider;
    private int padding;
    Paint paint;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private BackupImageView valueBackupImageView;
    private ImageView valueImageView;
    private AnimatedTextView valueTextView;

    public TextSettingsCell(Context context) {
        this(context, 21);
    }

    public TextSettingsCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, 21, resourcesProvider2);
    }

    public TextSettingsCell(Context context, int padding2) {
        this(context, padding2, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TextSettingsCell(Context context, int padding2, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        int i = padding2;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.resourcesProvider = resourcesProvider3;
        this.padding = i;
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i2 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider3));
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) i, 0.0f, (float) i, 0.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(context2, true, true, !LocaleController.isRTL);
        this.valueTextView = animatedTextView;
        animatedTextView.setAnimationProperties(0.55f, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.valueTextView.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText", resourcesProvider3));
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, (float) i, 0.0f, (float) i, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.valueImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.valueImageView.setVisibility(4);
        this.valueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        addView(this.valueImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : i2) | 16, (float) i, 0.0f, (float) i, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        int width2 = availableWidth / 2;
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(width2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        }
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
        }
        BackupImageView backupImageView = this.valueBackupImageView;
        if (backupImageView != null) {
            backupImageView.measure(View.MeasureSpec.makeMeasureSpec(backupImageView.getLayoutParams().height, NUM), View.MeasureSpec.makeMeasureSpec(this.valueBackupImageView.getLayoutParams().width, NUM));
        }
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(width2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            width = (availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f);
        } else {
            width = availableWidth;
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.measureDelay && getParent() != null) {
            this.changeProgressStartDelay = (int) ((((float) getTop()) / ((float) ((View) getParent()).getMeasuredHeight())) * 150.0f);
        }
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setCanDisable(boolean value) {
        this.canDisable = value;
    }

    public AnimatedTextView getValueTextView() {
        return this.valueTextView;
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextValueColor(int color) {
        this.valueTextView.setTextColor(color);
    }

    public void setText(CharSequence text, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(4);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(CharSequence text, CharSequence value, boolean divider) {
        setTextAndValue(text, value, false, divider);
    }

    public void setTextAndValue(CharSequence text, CharSequence value, boolean animated, boolean divider) {
        this.textView.setText(text);
        this.valueImageView.setVisibility(4);
        if (value != null) {
            this.valueTextView.setText(value, animated);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    public void setTextAndIcon(CharSequence text, int resId, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        if (resId != 0) {
            this.valueImageView.setVisibility(0);
            this.valueImageView.setImageResource(resId);
        } else {
            this.valueImageView.setVisibility(4);
        }
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setIcon(int resId) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.textView.getLayoutParams();
        if (resId == 0) {
            this.imageView.setVisibility(8);
            params.leftMargin = 0;
            return;
        }
        this.imageView.setImageResource(resId);
        this.imageView.setVisibility(0);
        params.leftMargin = AndroidUtilities.dp(71.0f);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        setEnabled(value);
        float f = 1.0f;
        if (animators != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            if (this.valueTextView.getVisibility() == 0) {
                AnimatedTextView animatedTextView = this.valueTextView;
                float[] fArr2 = new float[1];
                fArr2[0] = value ? 1.0f : 0.5f;
                animators.add(ObjectAnimator.ofFloat(animatedTextView, "alpha", fArr2));
            }
            if (this.valueImageView.getVisibility() == 0) {
                ImageView imageView2 = this.valueImageView;
                float[] fArr3 = new float[1];
                if (!value) {
                    f = 0.5f;
                }
                fArr3[0] = f;
                animators.add(ObjectAnimator.ofFloat(imageView2, "alpha", fArr3));
                return;
            }
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.setAlpha(value ? 1.0f : 0.5f);
        }
        if (this.valueImageView.getVisibility() == 0) {
            ImageView imageView3 = this.valueImageView;
            if (!value) {
                f = 0.5f;
            }
            imageView3.setAlpha(f);
        }
    }

    public void setEnabled(boolean value) {
        super.setEnabled(value);
        float f = 0.5f;
        this.textView.setAlpha((value || !this.canDisable) ? 1.0f : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.setAlpha((value || !this.canDisable) ? 1.0f : 0.5f);
        }
        if (this.valueImageView.getVisibility() == 0) {
            ImageView imageView2 = this.valueImageView;
            if (value || !this.canDisable) {
                f = 1.0f;
            }
            imageView2.setAlpha(f);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        int i = 0;
        if (this.drawLoading || this.drawLoadingProgress != 0.0f) {
            if (this.paint == null) {
                Paint paint2 = new Paint(1);
                this.paint = paint2;
                paint2.setColor(Theme.getColor("dialogSearchBackground", this.resourcesProvider));
            }
            if (this.incrementLoadingProgress) {
                float f = this.loadingProgress + 0.016f;
                this.loadingProgress = f;
                if (f > 1.0f) {
                    this.loadingProgress = 1.0f;
                    this.incrementLoadingProgress = false;
                }
            } else {
                float f2 = this.loadingProgress - 0.016f;
                this.loadingProgress = f2;
                if (f2 < 0.0f) {
                    this.loadingProgress = 0.0f;
                    this.incrementLoadingProgress = true;
                }
            }
            int i2 = this.changeProgressStartDelay;
            if (i2 > 0) {
                this.changeProgressStartDelay = i2 - 15;
            } else {
                boolean z = this.drawLoading;
                if (z) {
                    float f3 = this.drawLoadingProgress;
                    if (f3 != 1.0f) {
                        float f4 = f3 + 0.10666667f;
                        this.drawLoadingProgress = f4;
                        if (f4 > 1.0f) {
                            this.drawLoadingProgress = 1.0f;
                        }
                    }
                }
                if (!z) {
                    float f5 = this.drawLoadingProgress;
                    if (f5 != 0.0f) {
                        float f6 = f5 - 0.10666667f;
                        this.drawLoadingProgress = f6;
                        if (f6 < 0.0f) {
                            this.drawLoadingProgress = 0.0f;
                        }
                    }
                }
            }
            this.paint.setAlpha((int) (255.0f * ((this.loadingProgress * 0.4f) + 0.6f) * this.drawLoadingProgress));
            int cy = getMeasuredHeight() >> 1;
            AndroidUtilities.rectTmp.set((float) ((getMeasuredWidth() - AndroidUtilities.dp((float) this.padding)) - AndroidUtilities.dp((float) this.loadingSize)), (float) (cy - AndroidUtilities.dp(3.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) this.padding)), (float) (AndroidUtilities.dp(3.0f) + cy));
            if (LocaleController.isRTL) {
                AndroidUtilities.rectTmp.left = ((float) getMeasuredWidth()) - AndroidUtilities.rectTmp.left;
                AndroidUtilities.rectTmp.right = ((float) getMeasuredWidth()) - AndroidUtilities.rectTmp.right;
            }
            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
            invalidate();
        }
        this.valueTextView.setAlpha(1.0f - this.drawLoadingProgress);
        super.dispatchDraw(canvas);
        if (this.needDivider) {
            float dp = LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f);
            float measuredHeight = (float) (getMeasuredHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                i = AndroidUtilities.dp(20.0f);
            }
            canvas.drawLine(dp, measuredHeight, (float) (measuredWidth - i), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        String str;
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sb = new StringBuilder();
        sb.append(this.textView.getText());
        AnimatedTextView animatedTextView = this.valueTextView;
        if (animatedTextView == null || animatedTextView.getVisibility() != 0) {
            str = "";
        } else {
            str = "\n" + this.valueTextView.getText();
        }
        sb.append(str);
        info.setText(sb.toString());
        info.setEnabled(isEnabled());
    }

    public void setDrawLoading(boolean drawLoading2, int size, boolean animated) {
        this.drawLoading = drawLoading2;
        this.loadingSize = size;
        if (!animated) {
            this.drawLoadingProgress = drawLoading2 ? 1.0f : 0.0f;
        } else {
            this.measureDelay = true;
        }
        invalidate();
    }

    public BackupImageView getValueBackupImageView() {
        if (this.valueBackupImageView == null) {
            BackupImageView backupImageView = new BackupImageView(getContext());
            this.valueBackupImageView = backupImageView;
            int i = (LocaleController.isRTL ? 3 : 5) | 16;
            int i2 = this.padding;
            addView(backupImageView, LayoutHelper.createFrame(24, 24.0f, i, (float) i2, 0.0f, (float) i2, 0.0f));
        }
        return this.valueBackupImageView;
    }
}
