package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;

public class LocationCell extends FrameLayout {
    private static FlickerLoadingView globalGradientView;
    private TextView addressTextView;
    private ShapeDrawable circleDrawable;
    private float enterAlpha = 0.0f;
    private ValueAnimator enterAnimator;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean wrapContent;

    public LocationCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        this.wrapContent = z;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), -1);
        this.circleDrawable = createCircleDrawable;
        backupImageView.setBackground(createCircleDrawable);
        this.imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        BackupImageView backupImageView2 = this.imageView;
        boolean z2 = LocaleController.isRTL;
        int i = 5;
        addView(backupImageView2, LayoutHelper.createFrame(42, 42.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 15.0f, 11.0f, z2 ? 15.0f : 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextSize(1, 16.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView2 = this.nameTextView;
        boolean z3 = LocaleController.isRTL;
        int i2 = 16;
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, (z3 ? 5 : 3) | 48, (float) (z3 ? 16 : 73), 10.0f, (float) (z3 ? 73 : 16), 0.0f));
        TextView textView3 = new TextView(context);
        this.addressTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.addressTextView.setMaxLines(1);
        this.addressTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.addressTextView.setSingleLine(true);
        this.addressTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        this.addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView4 = this.addressTextView;
        boolean z4 = LocaleController.isRTL;
        addView(textView4, LayoutHelper.createFrame(-2, -2.0f, (!z4 ? 3 : i) | 48, (float) (z4 ? 16 : 73), 35.0f, (float) (z4 ? 73 : i2), 0.0f));
        this.imageView.setAlpha(this.enterAlpha);
        this.nameTextView.setAlpha(this.enterAlpha);
        this.addressTextView.setAlpha(this.enterAlpha);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.wrapContent) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        }
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void setLocation(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, String str, int i, boolean z) {
        setLocation(tLRPC$TL_messageMediaVenue, str, (String) null, i, z);
    }

    public static int getColorForIndex(int i) {
        int i2 = i % 7;
        if (i2 == 0) {
            return -1351584;
        }
        if (i2 == 1) {
            return -868277;
        }
        if (i2 == 2) {
            return -12214795;
        }
        if (i2 == 3) {
            return -13187226;
        }
        if (i2 != 4) {
            return i2 != 5 ? -1285237 : -12338729;
        }
        return -7900675;
    }

    public void setLocation(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, String str, String str2, int i, boolean z) {
        this.needDivider = z;
        this.circleDrawable.getPaint().setColor(getColorForIndex(i));
        if (tLRPC$TL_messageMediaVenue != null) {
            this.nameTextView.setText(tLRPC$TL_messageMediaVenue.title);
        }
        if (str2 != null) {
            this.addressTextView.setText(str2);
        } else if (tLRPC$TL_messageMediaVenue != null) {
            this.addressTextView.setText(tLRPC$TL_messageMediaVenue.address);
        }
        if (str != null) {
            this.imageView.setImage(str, (String) null, (Drawable) null);
        }
        setWillNotDraw(false);
        setClickable(tLRPC$TL_messageMediaVenue == null);
        ValueAnimator valueAnimator = this.enterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        boolean z2 = tLRPC$TL_messageMediaVenue == null;
        float f = this.enterAlpha;
        float f2 = z2 ? 0.0f : 1.0f;
        long abs = (long) (Math.abs(f - f2) * 150.0f);
        this.enterAnimator = ValueAnimator.ofFloat(new float[]{f, f2});
        this.enterAnimator.addUpdateListener(new LocationCell$$ExternalSyntheticLambda0(this, SystemClock.elapsedRealtime(), abs, f, f2));
        ValueAnimator valueAnimator2 = this.enterAnimator;
        if (z2) {
            abs = Long.MAX_VALUE;
        }
        valueAnimator2.setDuration(abs);
        this.enterAnimator.start();
        this.imageView.setAlpha(f);
        this.nameTextView.setAlpha(f);
        this.addressTextView.setAlpha(f);
        invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setLocation$0(long j, long j2, float f, float f2, ValueAnimator valueAnimator) {
        float f3 = 1.0f;
        float min = Math.min(Math.max(((float) (SystemClock.elapsedRealtime() - j)) / ((float) j2), 0.0f), 1.0f);
        if (j2 > 0) {
            f3 = min;
        }
        float lerp = AndroidUtilities.lerp(f, f2, f3);
        this.enterAlpha = lerp;
        this.imageView.setAlpha(lerp);
        this.nameTextView.setAlpha(this.enterAlpha);
        this.addressTextView.setAlpha(this.enterAlpha);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (globalGradientView == null) {
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext());
            globalGradientView = flickerLoadingView;
            flickerLoadingView.setIsSingleCell(true);
        }
        globalGradientView.setParentSize(getMeasuredWidth(), getMeasuredHeight(), (float) ((-(getParent() instanceof ViewGroup ? ((ViewGroup) getParent()).indexOfChild(this) : 0)) * AndroidUtilities.dp(56.0f)));
        globalGradientView.setViewType(4);
        globalGradientView.updateColors();
        globalGradientView.updateGradient();
        canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) ((1.0f - this.enterAlpha) * 255.0f), 31);
        canvas.translate((float) AndroidUtilities.dp(2.0f), (float) ((getMeasuredHeight() - AndroidUtilities.dp(56.0f)) / 2));
        globalGradientView.draw(canvas);
        canvas.restore();
        super.onDraw(canvas);
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (LocaleController.isRTL ? getWidth() - AndroidUtilities.dp(72.0f) : getWidth()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
