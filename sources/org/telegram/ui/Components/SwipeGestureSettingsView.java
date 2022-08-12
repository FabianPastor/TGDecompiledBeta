package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public class SwipeGestureSettingsView extends FrameLayout {
    String[] backgroundKeys;
    float colorProgress;
    String currentColorKey;
    int currentIconIndex;
    int currentIconValue;
    Paint filledPaint = new Paint(1);
    int fromColor;
    boolean hasTabs;
    RLottieImageView[] iconViews;
    RLottieDrawable[] icons;
    Paint linePaint = new Paint(1);
    Paint outlinePaint = new Paint(1);
    private NumberPicker picker;
    Paint pickerDividersPaint = new Paint(1);
    float progressToSwipeFolders;
    RectF rect = new RectF();
    String[] strings;
    Runnable swapIconRunnable;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SwipeGestureSettingsView(Context context, int i) {
        super(context);
        Context context2 = context;
        String[] strArr = new String[6];
        this.strings = strArr;
        this.backgroundKeys = new String[6];
        this.icons = new RLottieDrawable[6];
        this.iconViews = new RLottieImageView[2];
        float f = 1.0f;
        this.colorProgress = 1.0f;
        strArr[0] = LocaleController.getString("SwipeSettingsPin", R.string.SwipeSettingsPin);
        this.strings[1] = LocaleController.getString("SwipeSettingsRead", R.string.SwipeSettingsRead);
        this.strings[2] = LocaleController.getString("SwipeSettingsArchive", R.string.SwipeSettingsArchive);
        this.strings[3] = LocaleController.getString("SwipeSettingsMute", R.string.SwipeSettingsMute);
        this.strings[4] = LocaleController.getString("SwipeSettingsDelete", R.string.SwipeSettingsDelete);
        this.strings[5] = LocaleController.getString("SwipeSettingsFolders", R.string.SwipeSettingsFolders);
        String[] strArr2 = this.backgroundKeys;
        strArr2[0] = "chats_archiveBackground";
        strArr2[1] = "chats_archiveBackground";
        strArr2[2] = "chats_archiveBackground";
        strArr2[3] = "chats_archiveBackground";
        strArr2[4] = "dialogSwipeRemove";
        strArr2[5] = "chats_archivePinBackground";
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(5.0f));
        this.pickerDividersPaint.setStyle(Paint.Style.STROKE);
        this.pickerDividersPaint.setStrokeCap(Paint.Cap.ROUND);
        this.pickerDividersPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        AnonymousClass1 r4 = new NumberPicker(context2, 13) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float dp = (float) AndroidUtilities.dp(31.0f);
                SwipeGestureSettingsView.this.pickerDividersPaint.setColor(Theme.getColor("radioBackgroundChecked"));
                canvas.drawLine((float) AndroidUtilities.dp(2.0f), dp, (float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f)), dp, SwipeGestureSettingsView.this.pickerDividersPaint);
                float measuredHeight = (float) (getMeasuredHeight() - AndroidUtilities.dp(31.0f));
                canvas.drawLine((float) AndroidUtilities.dp(2.0f), measuredHeight, (float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f)), measuredHeight, SwipeGestureSettingsView.this.pickerDividersPaint);
            }
        };
        this.picker = r4;
        r4.setMinValue(0);
        this.picker.setDrawDividers(false);
        boolean z = !MessagesController.getInstance(i).dialogFilters.isEmpty();
        this.hasTabs = z;
        this.picker.setMaxValue(z ? this.strings.length - 1 : this.strings.length - 2);
        this.picker.setFormatter(new SwipeGestureSettingsView$$ExternalSyntheticLambda1(this));
        this.picker.setOnValueChangedListener(new SwipeGestureSettingsView$$ExternalSyntheticLambda2(this));
        this.picker.setImportantForAccessibility(2);
        this.picker.setValue(SharedConfig.getChatSwipeAction(i));
        addView(this.picker, LayoutHelper.createFrame(132, -1.0f, 5, 21.0f, 0.0f, 21.0f, 0.0f));
        setWillNotDraw(false);
        this.currentIconIndex = 0;
        for (int i2 = 0; i2 < 2; i2++) {
            this.iconViews[i2] = new RLottieImageView(context2);
            addView(this.iconViews[i2], LayoutHelper.createFrame(28, 28.0f, 21, 0.0f, 0.0f, 184.0f, 0.0f));
        }
        RLottieDrawable icon = getIcon(this.picker.getValue());
        if (icon != null) {
            this.iconViews[0].setImageDrawable(icon);
            icon.setCurrentFrame(icon.getFramesCount() - 1);
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[0], true, 0.5f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[1], false, 0.5f, false);
        this.progressToSwipeFolders = this.picker.getValue() != 5 ? 0.0f : f;
        this.currentIconValue = this.picker.getValue();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ String lambda$new$0(int i) {
        return this.strings[i];
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(NumberPicker numberPicker, int i, int i2) {
        swapIcons();
        SharedConfig.updateChatListSwipeSetting(i2);
        invalidate();
        numberPicker.performHapticFeedback(3, 2);
    }

    private void swapIcons() {
        int value;
        if (this.swapIconRunnable == null && this.currentIconValue != (value = this.picker.getValue())) {
            this.currentIconValue = value;
            int i = (this.currentIconIndex + 1) % 2;
            RLottieDrawable icon = getIcon(value);
            if (icon != null) {
                if (this.iconViews[i].getVisibility() != 0) {
                    icon.setCurrentFrame(0, false);
                }
                this.iconViews[i].setAnimation(icon);
                this.iconViews[i].playAnimation();
            } else {
                this.iconViews[i].clearAnimationDrawable();
            }
            AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[this.currentIconIndex], false, 0.5f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[i], true, 0.5f, true);
            this.currentIconIndex = i;
            SwipeGestureSettingsView$$ExternalSyntheticLambda0 swipeGestureSettingsView$$ExternalSyntheticLambda0 = new SwipeGestureSettingsView$$ExternalSyntheticLambda0(this);
            this.swapIconRunnable = swipeGestureSettingsView$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(swipeGestureSettingsView$$ExternalSyntheticLambda0, 150);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$swapIcons$2() {
        this.swapIconRunnable = null;
        swapIcons();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(102.0f), NUM));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0113  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r16) {
        /*
            r15 = this;
            r0 = r15
            r7 = r16
            super.onDraw(r16)
            org.telegram.ui.Components.NumberPicker r1 = r0.picker
            int r1 = r1.getValue()
            r2 = 1
            r3 = 0
            r4 = 5
            if (r1 != r4) goto L_0x0013
            r1 = 1
            goto L_0x0014
        L_0x0013:
            r1 = 0
        L_0x0014:
            r4 = 1029338126(0x3d5a740e, float:0.NUM)
            r8 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            if (r1 == 0) goto L_0x003e
            float r6 = r0.progressToSwipeFolders
            int r9 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r9 == 0) goto L_0x003e
            float r6 = r6 + r4
            r0.progressToSwipeFolders = r6
            int r1 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x002c
            r0.progressToSwipeFolders = r5
            goto L_0x0061
        L_0x002c:
            org.telegram.ui.Components.RLottieImageView[] r1 = r0.iconViews
            r1 = r1[r3]
            r1.invalidate()
            org.telegram.ui.Components.RLottieImageView[] r1 = r0.iconViews
            r1 = r1[r2]
            r1.invalidate()
            r15.invalidate()
            goto L_0x0061
        L_0x003e:
            if (r1 != 0) goto L_0x0061
            float r1 = r0.progressToSwipeFolders
            int r6 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r6 == 0) goto L_0x0061
            float r1 = r1 - r4
            r0.progressToSwipeFolders = r1
            int r1 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r1 >= 0) goto L_0x0050
            r0.progressToSwipeFolders = r8
            goto L_0x0061
        L_0x0050:
            org.telegram.ui.Components.RLottieImageView[] r1 = r0.iconViews
            r1 = r1[r3]
            r1.invalidate()
            org.telegram.ui.Components.RLottieImageView[] r1 = r0.iconViews
            r1 = r1[r2]
            r1.invalidate()
            r15.invalidate()
        L_0x0061:
            android.graphics.Paint r1 = r0.outlinePaint
            java.lang.String r2 = "switchTrack"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r3)
            android.graphics.Paint r1 = r0.linePaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r3)
            int r1 = r15.getMeasuredWidth()
            r3 = 1124335616(0x43040000, float:132.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1101529088(0x41a80000, float:21.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r6
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 + r6
            int r1 = r1 - r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r15.getMeasuredHeight()
            r6 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            int r4 = r4 / 2
            android.graphics.RectF r6 = r0.rect
            float r3 = (float) r3
            float r9 = (float) r4
            float r10 = (float) r1
            int r11 = r15.getMeasuredHeight()
            int r11 = r11 - r4
            float r11 = (float) r11
            r6.set(r3, r9, r10, r11)
            java.lang.String r6 = r0.currentColorKey
            r10 = 1063675494(0x3var_, float:0.9)
            java.lang.String r11 = "windowBackgroundWhite"
            if (r6 != 0) goto L_0x00d5
            java.lang.String[] r6 = r0.backgroundKeys
            org.telegram.ui.Components.NumberPicker r12 = r0.picker
            int r12 = r12.getValue()
            r6 = r6[r12]
            r0.currentColorKey = r6
            r0.colorProgress = r5
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            java.lang.String r12 = r0.currentColorKey
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r12, r10)
            r0.fromColor = r6
            goto L_0x010d
        L_0x00d5:
            java.lang.String[] r6 = r0.backgroundKeys
            org.telegram.ui.Components.NumberPicker r12 = r0.picker
            int r12 = r12.getValue()
            r6 = r6[r12]
            java.lang.String r12 = r0.currentColorKey
            boolean r6 = r6.equals(r12)
            if (r6 != 0) goto L_0x010d
            int r6 = r0.fromColor
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            java.lang.String r13 = r0.currentColorKey
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            int r12 = androidx.core.graphics.ColorUtils.blendARGB(r12, r13, r10)
            float r13 = r0.colorProgress
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r12, r13)
            r0.fromColor = r6
            r0.colorProgress = r8
            java.lang.String[] r6 = r0.backgroundKeys
            org.telegram.ui.Components.NumberPicker r12 = r0.picker
            int r12 = r12.getValue()
            r6 = r6[r12]
            r0.currentColorKey = r6
        L_0x010d:
            float r6 = r0.colorProgress
            int r12 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r12 == 0) goto L_0x0123
            r12 = 1042536202(0x3e23d70a, float:0.16)
            float r6 = r6 + r12
            r0.colorProgress = r6
            int r6 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r6 <= 0) goto L_0x0120
            r0.colorProgress = r5
            goto L_0x0123
        L_0x0120:
            r15.invalidate()
        L_0x0123:
            int r6 = r0.fromColor
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            java.lang.String r13 = r0.currentColorKey
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            int r10 = androidx.core.graphics.ColorUtils.blendARGB(r12, r13, r10)
            float r12 = r0.colorProgress
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r10, r12)
            android.graphics.Paint r10 = r0.filledPaint
            r10.setColor(r6)
            android.graphics.RectF r6 = r0.rect
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r12 = (float) r12
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r13 = (float) r13
            android.graphics.Paint r14 = r0.filledPaint
            r7.drawRoundRect(r6, r12, r13, r14)
            android.graphics.Paint r6 = r0.filledPaint
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r6.setColor(r11)
            android.graphics.Paint r6 = r0.filledPaint
            r11 = 255(0xff, float:3.57E-43)
            r6.setAlpha(r11)
            android.graphics.RectF r6 = r0.rect
            r11 = 1114112000(0x42680000, float:58.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = r1 - r11
            float r1 = (float) r1
            int r11 = r15.getMeasuredHeight()
            int r11 = r11 - r4
            float r4 = (float) r11
            r6.set(r3, r9, r1, r4)
            android.graphics.RectF r1 = r0.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = -r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = -r4
            float r4 = (float) r4
            r1.inset(r3, r4)
            android.graphics.RectF r1 = r0.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r4 = (float) r4
            android.graphics.Paint r5 = r0.filledPaint
            r7.drawRoundRect(r1, r3, r4, r5)
            android.graphics.Paint r1 = r0.outlinePaint
            r3 = 31
            r1.setAlpha(r3)
            android.graphics.RectF r1 = r0.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r4 = (float) r4
            android.graphics.Paint r5 = r0.outlinePaint
            r7.drawRoundRect(r1, r3, r4, r5)
            r16.save()
            android.graphics.RectF r1 = r0.rect
            r7.clipRect(r1)
            android.graphics.Paint r1 = r0.filledPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            android.graphics.Paint r1 = r0.filledPaint
            r2 = 60
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            float r2 = r1.left
            float r2 = r2 + r8
            float r1 = r1.centerY()
            r3 = 1097859072(0x41700000, float:15.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.filledPaint
            r7.drawCircle(r2, r1, r3, r4)
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.centerY()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r2 = (float) r2
            float r5 = r1 - r2
            android.graphics.Paint r1 = r0.linePaint
            r2 = 57
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.left
            r9 = 1102577664(0x41b80000, float:23.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r2 = (float) r2
            float r1 = r1 + r2
            float r2 = r1 + r8
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.right
            r3 = 1116209152(0x42880000, float:68.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r1 - r3
            android.graphics.Paint r6 = r0.linePaint
            r1 = r16
            r3 = r5
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.centerY()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r2 = (float) r2
            float r5 = r1 + r2
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.left
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r2 = (float) r2
            float r1 = r1 + r2
            float r2 = r1 + r8
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.right
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r3 = (float) r3
            float r4 = r1 - r3
            android.graphics.Paint r6 = r0.linePaint
            r1 = r16
            r3 = r5
            r1.drawLine(r2, r3, r4, r5, r6)
            r16.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SwipeGestureSettingsView.onDraw(android.graphics.Canvas):void");
    }

    public RLottieDrawable getIcon(int i) {
        int i2;
        RLottieDrawable[] rLottieDrawableArr = this.icons;
        if (rLottieDrawableArr[i] == null) {
            if (i == 1) {
                i2 = R.raw.swipe_read;
            } else if (i == 2) {
                i2 = R.raw.chats_archive;
            } else if (i == 3) {
                i2 = R.raw.swipe_mute;
            } else if (i == 4) {
                i2 = R.raw.swipe_delete;
            } else if (i != 5) {
                i2 = R.raw.swipe_pin;
            } else {
                i2 = R.raw.swipe_disabled;
            }
            int i3 = i2;
            rLottieDrawableArr[i] = new RLottieDrawable(i3, "" + i3, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
            updateIconColor(i);
        }
        return this.icons[i];
    }

    public void updateIconColor(int i) {
        if (this.icons[i] != null) {
            int blendARGB = ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhite"), Theme.getColor("chats_archiveBackground"), 0.9f);
            int color = Theme.getColor("chats_archiveIcon");
            if (i == 2) {
                this.icons[i].setLayerColor("Arrow.**", blendARGB);
                this.icons[i].setLayerColor("Box2.**", color);
                this.icons[i].setLayerColor("Box1.**", color);
                return;
            }
            this.icons[i].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void updateColors() {
        for (int i = 0; i < this.icons.length; i++) {
            updateIconColor(i);
        }
    }

    public void setBackgroundColor(int i) {
        super.setBackgroundColor(i);
        updateColors();
        this.picker.setTextColor(Theme.getColor("dialogTextBlack"));
        this.picker.invalidate();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(true);
        accessibilityNodeInfo.setContentDescription(this.strings[this.picker.getValue()]);
        if (Build.VERSION.SDK_INT >= 21) {
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, (CharSequence) null));
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (accessibilityEvent.getEventType() == 1) {
            int value = this.picker.getValue() + 1;
            if (value > this.picker.getMaxValue() || value < 0) {
                value = 0;
            }
            setContentDescription(this.strings[value]);
            this.picker.changeValueByOne(true);
        }
    }
}
