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
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public class SwipeGestureSettingsView extends FrameLayout {
    public static final int SWIPE_GESTURE_ARCHIVE = 2;
    public static final int SWIPE_GESTURE_DELETE = 4;
    public static final int SWIPE_GESTURE_FOLDERS = 5;
    public static final int SWIPE_GESTURE_MUTE = 3;
    public static final int SWIPE_GESTURE_PIN = 0;
    public static final int SWIPE_GESTURE_READ = 1;
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
    public SwipeGestureSettingsView(Context context, int currentAccount) {
        super(context);
        Context context2 = context;
        String[] strArr = new String[6];
        this.strings = strArr;
        this.backgroundKeys = new String[6];
        this.icons = new RLottieDrawable[6];
        this.iconViews = new RLottieImageView[2];
        float f = 1.0f;
        this.colorProgress = 1.0f;
        strArr[0] = LocaleController.getString("SwipeSettingsPin", NUM);
        this.strings[1] = LocaleController.getString("SwipeSettingsRead", NUM);
        this.strings[2] = LocaleController.getString("SwipeSettingsArchive", NUM);
        this.strings[3] = LocaleController.getString("SwipeSettingsMute", NUM);
        this.strings[4] = LocaleController.getString("SwipeSettingsDelete", NUM);
        this.strings[5] = LocaleController.getString("SwipeSettingsFolders", NUM);
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
                float y = (float) AndroidUtilities.dp(31.0f);
                SwipeGestureSettingsView.this.pickerDividersPaint.setColor(Theme.getColor("radioBackgroundChecked"));
                canvas.drawLine((float) AndroidUtilities.dp(2.0f), y, (float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f)), y, SwipeGestureSettingsView.this.pickerDividersPaint);
                float y2 = (float) (getMeasuredHeight() - AndroidUtilities.dp(31.0f));
                canvas.drawLine((float) AndroidUtilities.dp(2.0f), y2, (float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f)), y2, SwipeGestureSettingsView.this.pickerDividersPaint);
            }
        };
        this.picker = r4;
        r4.setMinValue(0);
        this.picker.setDrawDividers(false);
        boolean z = !MessagesController.getInstance(currentAccount).dialogFilters.isEmpty();
        this.hasTabs = z;
        this.picker.setMaxValue(z ? this.strings.length - 1 : this.strings.length - 2);
        this.picker.setFormatter(new SwipeGestureSettingsView$$ExternalSyntheticLambda1(this));
        this.picker.setOnValueChangedListener(new SwipeGestureSettingsView$$ExternalSyntheticLambda2(this));
        this.picker.setImportantForAccessibility(2);
        this.picker.setValue(SharedConfig.getChatSwipeAction(currentAccount));
        addView(this.picker, LayoutHelper.createFrame(132, -1.0f, 5, 21.0f, 0.0f, 21.0f, 0.0f));
        setWillNotDraw(false);
        this.currentIconIndex = 0;
        for (int i = 0; i < 2; i++) {
            this.iconViews[i] = new RLottieImageView(context2);
            addView(this.iconViews[i], LayoutHelper.createFrame(28, 28.0f, 21, 0.0f, 0.0f, 184.0f, 0.0f));
        }
        RLottieDrawable currentIcon = getIcon(this.picker.getValue());
        if (currentIcon != null) {
            this.iconViews[0].setImageDrawable(currentIcon);
            currentIcon.setCurrentFrame(currentIcon.getFramesCount() - 1);
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[0], true, 0.5f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[1], false, 0.5f, false);
        this.progressToSwipeFolders = this.picker.getValue() != 5 ? 0.0f : f;
        this.currentIconValue = this.picker.getValue();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SwipeGestureSettingsView  reason: not valid java name */
    public /* synthetic */ String m1468lambda$new$0$orgtelegramuiComponentsSwipeGestureSettingsView(int value) {
        return this.strings[value];
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-SwipeGestureSettingsView  reason: not valid java name */
    public /* synthetic */ void m1469lambda$new$1$orgtelegramuiComponentsSwipeGestureSettingsView(NumberPicker picker2, int oldVal, int newVal) {
        swapIcons();
        SharedConfig.updateChatListSwipeSetting(newVal);
        invalidate();
        picker2.performHapticFeedback(3, 2);
    }

    private void swapIcons() {
        int newValue;
        if (this.swapIconRunnable == null && this.currentIconValue != (newValue = this.picker.getValue())) {
            this.currentIconValue = newValue;
            int nextIconIndex = (this.currentIconIndex + 1) % 2;
            RLottieDrawable drawable = getIcon(newValue);
            if (drawable != null) {
                if (this.iconViews[nextIconIndex].getVisibility() != 0) {
                    drawable.setCurrentFrame(0, false);
                }
                this.iconViews[nextIconIndex].setAnimation(drawable);
                this.iconViews[nextIconIndex].playAnimation();
            } else {
                this.iconViews[nextIconIndex].clearAnimationDrawable();
            }
            AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[this.currentIconIndex], false, 0.5f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[nextIconIndex], true, 0.5f, true);
            this.currentIconIndex = nextIconIndex;
            SwipeGestureSettingsView$$ExternalSyntheticLambda0 swipeGestureSettingsView$$ExternalSyntheticLambda0 = new SwipeGestureSettingsView$$ExternalSyntheticLambda0(this);
            this.swapIconRunnable = swipeGestureSettingsView$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(swipeGestureSettingsView$$ExternalSyntheticLambda0, 150);
        }
    }

    /* renamed from: lambda$swapIcons$2$org-telegram-ui-Components-SwipeGestureSettingsView  reason: not valid java name */
    public /* synthetic */ void m1470x6d65954c() {
        this.swapIconRunnable = null;
        swapIcons();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(102.0f), NUM));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0116  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r0 = r17
            r7 = r18
            super.onDraw(r18)
            org.telegram.ui.Components.NumberPicker r1 = r0.picker
            int r1 = r1.getValue()
            r2 = 1
            r3 = 0
            r4 = 5
            if (r1 != r4) goto L_0x0014
            r1 = 1
            goto L_0x0015
        L_0x0014:
            r1 = 0
        L_0x0015:
            r8 = r1
            r1 = 1029338126(0x3d5a740e, float:0.NUM)
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            if (r8 == 0) goto L_0x0040
            float r6 = r0.progressToSwipeFolders
            int r9 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r9 == 0) goto L_0x0040
            float r6 = r6 + r1
            r0.progressToSwipeFolders = r6
            int r1 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x002e
            r0.progressToSwipeFolders = r5
            goto L_0x0063
        L_0x002e:
            org.telegram.ui.Components.RLottieImageView[] r1 = r0.iconViews
            r1 = r1[r3]
            r1.invalidate()
            org.telegram.ui.Components.RLottieImageView[] r1 = r0.iconViews
            r1 = r1[r2]
            r1.invalidate()
            r17.invalidate()
            goto L_0x0063
        L_0x0040:
            if (r8 != 0) goto L_0x0063
            float r6 = r0.progressToSwipeFolders
            int r9 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r9 == 0) goto L_0x0063
            float r6 = r6 - r1
            r0.progressToSwipeFolders = r6
            int r1 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r1 >= 0) goto L_0x0052
            r0.progressToSwipeFolders = r4
            goto L_0x0063
        L_0x0052:
            org.telegram.ui.Components.RLottieImageView[] r1 = r0.iconViews
            r1 = r1[r3]
            r1.invalidate()
            org.telegram.ui.Components.RLottieImageView[] r1 = r0.iconViews
            r1 = r1[r2]
            r1.invalidate()
            r17.invalidate()
        L_0x0063:
            android.graphics.Paint r1 = r0.outlinePaint
            java.lang.String r2 = "switchTrack"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r3)
            android.graphics.Paint r1 = r0.linePaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r3)
            int r1 = r17.getMeasuredWidth()
            r3 = 1124335616(0x43040000, float:132.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r6 = 1101529088(0x41a80000, float:21.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 + r9
            r9 = 1098907648(0x41800000, float:16.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r3 = r3 + r9
            int r9 = r1 - r3
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r1 = r17.getMeasuredHeight()
            r3 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r3
            int r11 = r1 / 2
            android.graphics.RectF r1 = r0.rect
            float r3 = (float) r10
            float r6 = (float) r11
            float r12 = (float) r9
            int r13 = r17.getMeasuredHeight()
            int r13 = r13 - r11
            float r13 = (float) r13
            r1.set(r3, r6, r12, r13)
            java.lang.String r1 = r0.currentColorKey
            r3 = 1063675494(0x3var_, float:0.9)
            java.lang.String r6 = "windowBackgroundWhite"
            if (r1 != 0) goto L_0x00d8
            java.lang.String[] r1 = r0.backgroundKeys
            org.telegram.ui.Components.NumberPicker r4 = r0.picker
            int r4 = r4.getValue()
            r1 = r1[r4]
            r0.currentColorKey = r1
            r0.colorProgress = r5
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r4 = r0.currentColorKey
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r4, r3)
            r0.fromColor = r1
            goto L_0x0110
        L_0x00d8:
            java.lang.String[] r1 = r0.backgroundKeys
            org.telegram.ui.Components.NumberPicker r12 = r0.picker
            int r12 = r12.getValue()
            r1 = r1[r12]
            java.lang.String r12 = r0.currentColorKey
            boolean r1 = r1.equals(r12)
            if (r1 != 0) goto L_0x0110
            int r1 = r0.fromColor
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r13 = r0.currentColorKey
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            int r12 = androidx.core.graphics.ColorUtils.blendARGB(r12, r13, r3)
            float r13 = r0.colorProgress
            int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r12, r13)
            r0.fromColor = r1
            r0.colorProgress = r4
            java.lang.String[] r1 = r0.backgroundKeys
            org.telegram.ui.Components.NumberPicker r4 = r0.picker
            int r4 = r4.getValue()
            r1 = r1[r4]
            r0.currentColorKey = r1
        L_0x0110:
            float r1 = r0.colorProgress
            int r4 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r4 == 0) goto L_0x0126
            r4 = 1042536202(0x3e23d70a, float:0.16)
            float r1 = r1 + r4
            r0.colorProgress = r1
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0123
            r0.colorProgress = r5
            goto L_0x0126
        L_0x0123:
            r17.invalidate()
        L_0x0126:
            int r1 = r0.fromColor
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r12 = r0.currentColorKey
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            int r3 = androidx.core.graphics.ColorUtils.blendARGB(r4, r12, r3)
            float r4 = r0.colorProgress
            int r12 = androidx.core.graphics.ColorUtils.blendARGB(r1, r3, r4)
            android.graphics.Paint r1 = r0.filledPaint
            r1.setColor(r12)
            android.graphics.RectF r1 = r0.rect
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r4 = (float) r4
            android.graphics.Paint r14 = r0.filledPaint
            r7.drawRoundRect(r1, r3, r4, r14)
            android.graphics.Paint r1 = r0.filledPaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r1.setColor(r3)
            android.graphics.Paint r1 = r0.filledPaint
            r3 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r3)
            android.graphics.RectF r1 = r0.rect
            float r3 = (float) r10
            float r4 = (float) r11
            r6 = 1114112000(0x42680000, float:58.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r9 - r6
            float r6 = (float) r6
            int r14 = r17.getMeasuredHeight()
            int r14 = r14 - r11
            float r14 = (float) r14
            r1.set(r3, r4, r6, r14)
            android.graphics.RectF r1 = r0.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = -r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = -r4
            float r4 = (float) r4
            r1.inset(r3, r4)
            android.graphics.RectF r1 = r0.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r4 = (float) r4
            android.graphics.Paint r5 = r0.filledPaint
            r7.drawRoundRect(r1, r3, r4, r5)
            android.graphics.Paint r1 = r0.outlinePaint
            r3 = 31
            r1.setAlpha(r3)
            android.graphics.RectF r1 = r0.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r4 = (float) r4
            android.graphics.Paint r5 = r0.outlinePaint
            r7.drawRoundRect(r1, r3, r4, r5)
            r18.save()
            android.graphics.RectF r1 = r0.rect
            r7.clipRect(r1)
            r14 = 0
            android.graphics.Paint r1 = r0.filledPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            android.graphics.Paint r1 = r0.filledPaint
            r2 = 60
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.left
            float r1 = r1 + r14
            android.graphics.RectF r2 = r0.rect
            float r2 = r2.centerY()
            r3 = 1097859072(0x41700000, float:15.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.filledPaint
            r7.drawCircle(r1, r2, r3, r4)
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.centerY()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r2 = (float) r2
            float r15 = r1 - r2
            android.graphics.Paint r1 = r0.linePaint
            r2 = 57
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.left
            r16 = 1102577664(0x41b80000, float:23.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r2 = (float) r2
            float r1 = r1 + r2
            float r2 = r1 + r14
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.right
            r3 = 1116209152(0x42880000, float:68.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r1 - r3
            android.graphics.Paint r6 = r0.linePaint
            r1 = r18
            r3 = r15
            r5 = r15
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.centerY()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r2 = (float) r2
            float r13 = r1 + r2
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.left
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r2 = (float) r2
            float r1 = r1 + r2
            float r2 = r1 + r14
            android.graphics.RectF r1 = r0.rect
            float r1 = r1.right
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r4 = r1 - r3
            android.graphics.Paint r6 = r0.linePaint
            r1 = r18
            r3 = r13
            r5 = r13
            r1.drawLine(r2, r3, r4, r5, r6)
            r18.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SwipeGestureSettingsView.onDraw(android.graphics.Canvas):void");
    }

    public RLottieDrawable getIcon(int i) {
        int rawId;
        RLottieDrawable[] rLottieDrawableArr = this.icons;
        if (rLottieDrawableArr[i] == null) {
            switch (i) {
                case 1:
                    rawId = NUM;
                    break;
                case 2:
                    rawId = NUM;
                    break;
                case 3:
                    rawId = NUM;
                    break;
                case 4:
                    rawId = NUM;
                    break;
                case 5:
                    rawId = NUM;
                    break;
                default:
                    rawId = NUM;
                    break;
            }
            rLottieDrawableArr[i] = new RLottieDrawable(rawId, "" + rawId, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
            updateIconColor(i);
        }
        return this.icons[i];
    }

    public void updateIconColor(int i) {
        if (this.icons[i] != null) {
            int backgroundColor = ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhite"), Theme.getColor("chats_archiveBackground"), 0.9f);
            int iconColor = Theme.getColor("chats_archiveIcon");
            if (i == 2) {
                this.icons[i].setLayerColor("Arrow.**", backgroundColor);
                this.icons[i].setLayerColor("Box2.**", iconColor);
                this.icons[i].setLayerColor("Box1.**", iconColor);
                return;
            }
            this.icons[i].setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void updateColors() {
        for (int i = 0; i < this.icons.length; i++) {
            updateIconColor(i);
        }
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        updateColors();
        this.picker.setTextColor(Theme.getColor("dialogTextBlack"));
        this.picker.invalidate();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(true);
        info.setContentDescription(this.strings[this.picker.getValue()]);
        if (Build.VERSION.SDK_INT >= 21) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, (CharSequence) null));
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (event.getEventType() == 1) {
            int newValue = this.picker.getValue() + 1;
            if (newValue > this.picker.getMaxValue() || newValue < 0) {
                newValue = 0;
            }
            setContentDescription(this.strings[newValue]);
            this.picker.changeValueByOne(true);
        }
    }
}
