package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;

public class ProximitySheet extends FrameLayout {
    private int backgroundPaddingLeft;
    private TextView buttonTextView;
    private ViewGroup containerView;
    /* access modifiers changed from: private */
    public AnimatorSet currentAnimation = null;
    /* access modifiers changed from: private */
    public AnimatorSet currentSheetAnimation;
    /* access modifiers changed from: private */
    public int currentSheetAnimationType;
    private TLRPC$User currentUser;
    private LinearLayout customView;
    private boolean dismissed;
    private TextView infoTextView;
    /* access modifiers changed from: private */
    public NumberPicker kmPicker;
    /* access modifiers changed from: private */
    public NumberPicker mPicker;
    private boolean maybeStartTracking = false;
    private Runnable onDismissCallback;
    private onRadiusPickerChange onRadiusChange;
    private Interpolator openInterpolator;
    private boolean radiusSet;
    private Rect rect = new Rect();
    private boolean startedTracking = false;
    private int startedTrackingPointerId = -1;
    private int startedTrackingX;
    private int startedTrackingY;
    /* access modifiers changed from: private */
    public int totalWidth;
    private int touchSlop;
    private boolean useFastDismiss;
    /* access modifiers changed from: private */
    public boolean useHardwareLayer;
    private boolean useImperialSystem;
    private VelocityTracker velocityTracker = null;

    public interface onRadiusPickerChange {
        boolean run(boolean z, int i);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ProximitySheet(Context context, TLRPC$User tLRPC$User, onRadiusPickerChange onradiuspickerchange, onRadiusPickerChange onradiuspickerchange2, Runnable runnable) {
        super(context);
        Context context2 = context;
        new Paint();
        this.useHardwareLayer = true;
        this.openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        setWillNotDraw(false);
        this.onDismissCallback = runnable;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect rect2 = new Rect();
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        mutate.getPadding(rect2);
        this.backgroundPaddingLeft = rect2.left;
        AnonymousClass1 r7 = new FrameLayout(this, getContext()) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.containerView = r7;
        r7.setBackgroundDrawable(mutate);
        this.containerView.setPadding(this.backgroundPaddingLeft, (AndroidUtilities.dp(8.0f) + rect2.top) - 1, this.backgroundPaddingLeft, 0);
        this.containerView.setVisibility(4);
        addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        this.useImperialSystem = LocaleController.getUseImperialSystemType();
        this.currentUser = tLRPC$User;
        this.onRadiusChange = onradiuspickerchange;
        NumberPicker numberPicker = new NumberPicker(context2);
        this.kmPicker = numberPicker;
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        this.kmPicker.setItemCount(5);
        NumberPicker numberPicker2 = new NumberPicker(context2);
        this.mPicker = numberPicker2;
        numberPicker2.setItemCount(5);
        this.mPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        AnonymousClass2 r5 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                ProximitySheet.this.kmPicker.setItemCount(i3);
                ProximitySheet.this.mPicker.setItemCount(i3);
                ProximitySheet.this.kmPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                ProximitySheet.this.mPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                this.ignoreLayout = false;
                int unused = ProximitySheet.this.totalWidth = View.MeasureSpec.getSize(i);
                if (ProximitySheet.this.totalWidth != 0) {
                    ProximitySheet.this.updateText(false, false);
                }
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.customView = r5;
        r5.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.customView.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        textView.setText(LocaleController.getString("LocationNotifiation", NUM));
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(ProximitySheet$$ExternalSyntheticLambda1.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        this.customView.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        System.currentTimeMillis();
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.infoTextView = new TextView(context2);
        this.buttonTextView = new TextView(this, context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(this.kmPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        this.kmPicker.setFormatter(new ProximitySheet$$ExternalSyntheticLambda2(this));
        this.kmPicker.setMinValue(0);
        this.kmPicker.setMaxValue(10);
        this.kmPicker.setWrapSelectorWheel(false);
        this.kmPicker.setTextOffset(AndroidUtilities.dp(20.0f));
        ProximitySheet$$ExternalSyntheticLambda4 proximitySheet$$ExternalSyntheticLambda4 = new ProximitySheet$$ExternalSyntheticLambda4(this);
        this.kmPicker.setOnValueChangedListener(proximitySheet$$ExternalSyntheticLambda4);
        this.mPicker.setMinValue(0);
        this.mPicker.setMaxValue(10);
        this.mPicker.setWrapSelectorWheel(false);
        this.mPicker.setTextOffset(-AndroidUtilities.dp(20.0f));
        linearLayout.addView(this.mPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        this.mPicker.setFormatter(new ProximitySheet$$ExternalSyntheticLambda3(this));
        this.mPicker.setOnValueChangedListener(proximitySheet$$ExternalSyntheticLambda4);
        this.kmPicker.setValue(0);
        this.mPicker.setValue(6);
        this.customView.addView(frameLayout2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setMaxLines(2);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        frameLayout2.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f));
        this.buttonTextView.setOnClickListener(new ProximitySheet$$ExternalSyntheticLambda0(this, onradiuspickerchange2));
        this.infoTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.infoTextView.setGravity(17);
        this.infoTextView.setTextColor(Theme.getColor("dialogTextGray2"));
        this.infoTextView.setTextSize(1, 14.0f);
        this.infoTextView.setAlpha(0.0f);
        this.infoTextView.setScaleX(0.5f);
        this.infoTextView.setScaleY(0.5f);
        frameLayout2.addView(this.infoTextView, LayoutHelper.createFrame(-1, 48.0f));
        this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2, 51));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ String lambda$new$1(int i) {
        if (this.useImperialSystem) {
            return LocaleController.formatString("MilesShort", NUM, Integer.valueOf(i));
        }
        return LocaleController.formatString("KMetersShort", NUM, Integer.valueOf(i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(NumberPicker numberPicker, int i, int i2) {
        try {
            performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        updateText(true, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ String lambda$new$3(int i) {
        if (this.useImperialSystem) {
            if (i == 1) {
                return LocaleController.formatString("FootsShort", NUM, 250);
            }
            if (i > 1) {
                i--;
            }
            return String.format(Locale.US, ".%d", new Object[]{Integer.valueOf(i)});
        } else if (i == 1) {
            return LocaleController.formatString("MetersShort", NUM, 50);
        } else {
            if (i > 1) {
                i--;
            }
            return LocaleController.formatString("MetersShort", NUM, Integer.valueOf(i * 100));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(onRadiusPickerChange onradiuspickerchange, View view) {
        if (this.buttonTextView.getTag() == null && onradiuspickerchange.run(true, (int) Math.max(1.0f, getValue()))) {
            dismiss();
        }
    }

    public View getCustomView() {
        return this.customView;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0027, code lost:
        r1 = (float) (r1 * 100);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x001a, code lost:
        if (r1 > 1) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0023, code lost:
        if (r1 > 1) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0025, code lost:
        r1 = r1 - 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public float getValue() {
        /*
            r4 = this;
            org.telegram.ui.Components.NumberPicker r0 = r4.kmPicker
            int r0 = r0.getValue()
            int r0 = r0 * 1000
            float r0 = (float) r0
            org.telegram.ui.Components.NumberPicker r1 = r4.mPicker
            int r1 = r1.getValue()
            boolean r2 = r4.useImperialSystem
            r3 = 1
            if (r2 == 0) goto L_0x001d
            if (r1 != r3) goto L_0x001a
            r1 = 1111319904(0x423d6560, float:47.349)
            goto L_0x0021
        L_0x001a:
            if (r1 <= r3) goto L_0x0027
            goto L_0x0025
        L_0x001d:
            if (r1 != r3) goto L_0x0023
            r1 = 1112014848(0x42480000, float:50.0)
        L_0x0021:
            float r0 = r0 + r1
            goto L_0x002b
        L_0x0023:
            if (r1 <= r3) goto L_0x0027
        L_0x0025:
            int r1 = r1 + -1
        L_0x0027:
            int r1 = r1 * 100
            float r1 = (float) r1
            goto L_0x0021
        L_0x002b:
            if (r2 == 0) goto L_0x0032
            r1 = 1070464730(0x3fcdfeda, float:1.60934)
            float r0 = r0 * r1
        L_0x0032:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProximitySheet.getValue():float");
    }

    public boolean getRadiusSet() {
        return this.radiusSet;
    }

    public void setRadiusSet() {
        this.radiusSet = true;
    }

    public void updateText(boolean z, boolean z2) {
        float value = getValue();
        String formatDistance = LocaleController.formatDistance(value, 2, Boolean.valueOf(this.useImperialSystem));
        if (this.onRadiusChange.run(z, (int) value) || this.currentUser == null) {
            if (this.currentUser == null) {
                this.buttonTextView.setText(LocaleController.formatString("LocationNotifiationButtonGroup", NUM, formatDistance));
            } else {
                CharSequence ellipsize = TextUtils.ellipsize(UserObject.getFirstName(this.currentUser), this.buttonTextView.getPaint(), (float) Math.max(AndroidUtilities.dp(10.0f), (int) ((((float) (this.totalWidth - AndroidUtilities.dp(94.0f))) * 1.5f) - ((float) ((int) Math.ceil((double) this.buttonTextView.getPaint().measureText(LocaleController.getString("LocationNotifiationButtonUser", NUM))))))), TextUtils.TruncateAt.END);
                this.buttonTextView.setText(LocaleController.formatString("LocationNotifiationButtonUser", NUM, ellipsize, formatDistance));
            }
            if (this.buttonTextView.getTag() != null) {
                this.buttonTextView.setTag((Object) null);
                this.buttonTextView.animate().setDuration(180).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).start();
                this.infoTextView.animate().setDuration(180).alpha(0.0f).scaleX(0.5f).scaleY(0.5f).start();
                return;
            }
            return;
        }
        this.infoTextView.setText(LocaleController.formatString("LocationNotifiationCloser", NUM, formatDistance));
        if (this.buttonTextView.getTag() == null) {
            this.buttonTextView.setTag(1);
            this.buttonTextView.animate().setDuration(180).alpha(0.0f).scaleX(0.5f).scaleY(0.5f).start();
            this.infoTextView.animate().setDuration(180).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).start();
        }
    }

    private void checkDismiss(float f, float f2) {
        float translationY = this.containerView.getTranslationY();
        if (!((translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (f2 < 3500.0f || Math.abs(f2) < Math.abs(f))) || (f2 < 0.0f && Math.abs(f2) >= 3500.0f))) {
            this.useFastDismiss = true;
            dismiss();
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentAnimation = animatorSet;
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f})});
        this.currentAnimation.setDuration((long) ((int) ((Math.max(0.0f, translationY) / AndroidUtilities.getPixelsInCM(0.8f, false)) * 150.0f)));
        this.currentAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.currentAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ProximitySheet.this.currentAnimation != null && ProximitySheet.this.currentAnimation.equals(animator)) {
                    AnimatorSet unused = ProximitySheet.this.currentAnimation = null;
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentAnimation.start();
    }

    private void cancelCurrentAnimation() {
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean processTouchEvent(MotionEvent motionEvent, boolean z) {
        if (this.dismissed) {
            return false;
        }
        if (motionEvent == null || (!(motionEvent.getAction() == 0 || motionEvent.getAction() == 2) || this.startedTracking || this.maybeStartTracking || motionEvent.getPointerCount() != 1)) {
            float f = 0.0f;
            if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float abs = (float) Math.abs((int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                float y = (float) (((int) motionEvent.getY()) - this.startedTrackingY);
                this.velocityTracker.addMovement(motionEvent);
                if (this.maybeStartTracking && !this.startedTracking && y > 0.0f && y / 3.0f > Math.abs(abs) && Math.abs(y) >= ((float) this.touchSlop)) {
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float translationY = this.containerView.getTranslationY() + y;
                    if (translationY >= 0.0f) {
                        f = translationY;
                    }
                    this.containerView.setTranslationY(f);
                    this.startedTrackingY = (int) motionEvent.getY();
                }
            } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                float translationY2 = this.containerView.getTranslationY();
                if (this.startedTracking || translationY2 != 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                    this.startedTracking = false;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                }
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.velocityTracker = null;
                }
                this.startedTrackingPointerId = -1;
            }
        } else {
            this.startedTrackingX = (int) motionEvent.getX();
            int y2 = (int) motionEvent.getY();
            this.startedTrackingY = y2;
            if (y2 < this.containerView.getTop() || this.startedTrackingX < this.containerView.getLeft() || this.startedTrackingX > this.containerView.getRight()) {
                requestDisallowInterceptTouchEvent(true);
                dismiss();
                return true;
            }
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            this.maybeStartTracking = true;
            cancelCurrentAnimation();
            VelocityTracker velocityTracker3 = this.velocityTracker;
            if (velocityTracker3 != null) {
                velocityTracker3.clear();
            }
        }
        if ((z || !this.maybeStartTracking) && !this.startedTracking) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.dismissed || processTouchEvent(motionEvent, false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        setMeasuredDimension(size, size2);
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec((this.backgroundPaddingLeft * 2) + size, NUM), View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (!(childAt.getVisibility() == 8 || childAt == this.containerView)) {
                measureChildWithMargins(childAt, View.MeasureSpec.makeMeasureSpec(size, NUM), 0, View.MeasureSpec.makeMeasureSpec(size2, NUM), 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0081  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayout(boolean r8, int r9, int r10, int r11, int r12) {
        /*
            r7 = this;
            int r12 = r12 - r10
            android.view.ViewGroup r8 = r7.containerView
            int r8 = r8.getMeasuredHeight()
            int r8 = r12 - r8
            int r9 = r11 - r9
            android.view.ViewGroup r10 = r7.containerView
            int r10 = r10.getMeasuredWidth()
            int r10 = r9 - r10
            int r10 = r10 / 2
            android.view.ViewGroup r0 = r7.containerView
            int r1 = r0.getMeasuredWidth()
            int r1 = r1 + r10
            android.view.ViewGroup r2 = r7.containerView
            int r2 = r2.getMeasuredHeight()
            int r2 = r2 + r8
            r0.layout(r10, r8, r1, r2)
            int r8 = r7.getChildCount()
            r10 = 0
        L_0x002b:
            if (r10 >= r8) goto L_0x0094
            android.view.View r0 = r7.getChildAt(r10)
            int r1 = r0.getVisibility()
            r2 = 8
            if (r1 == r2) goto L_0x0091
            android.view.ViewGroup r1 = r7.containerView
            if (r0 != r1) goto L_0x003e
            goto L_0x0091
        L_0x003e:
            android.view.ViewGroup$LayoutParams r1 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = r0.getMeasuredWidth()
            int r3 = r0.getMeasuredHeight()
            int r4 = r1.gravity
            r5 = -1
            if (r4 != r5) goto L_0x0053
            r4 = 51
        L_0x0053:
            r5 = r4 & 7
            r4 = r4 & 112(0x70, float:1.57E-43)
            r5 = r5 & 7
            r6 = 1
            if (r5 == r6) goto L_0x0067
            r6 = 5
            if (r5 == r6) goto L_0x0062
            int r5 = r1.leftMargin
            goto L_0x0071
        L_0x0062:
            int r5 = r11 - r2
            int r6 = r1.rightMargin
            goto L_0x0070
        L_0x0067:
            int r5 = r9 - r2
            int r5 = r5 / 2
            int r6 = r1.leftMargin
            int r5 = r5 + r6
            int r6 = r1.rightMargin
        L_0x0070:
            int r5 = r5 - r6
        L_0x0071:
            r6 = 16
            if (r4 == r6) goto L_0x0081
            r6 = 80
            if (r4 == r6) goto L_0x007c
            int r1 = r1.topMargin
            goto L_0x008c
        L_0x007c:
            int r4 = r12 - r3
            int r1 = r1.bottomMargin
            goto L_0x008a
        L_0x0081:
            int r4 = r12 - r3
            int r4 = r4 / 2
            int r6 = r1.topMargin
            int r4 = r4 + r6
            int r1 = r1.bottomMargin
        L_0x008a:
            int r1 = r4 - r1
        L_0x008c:
            int r2 = r2 + r5
            int r3 = r3 + r1
            r0.layout(r5, r1, r2, r3)
        L_0x0091:
            int r10 = r10 + 1
            goto L_0x002b
        L_0x0094:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProximitySheet.onLayout(boolean, int, int, int, int):void");
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.dismissed || processTouchEvent(motionEvent, true);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent((MotionEvent) null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void show() {
        this.dismissed = false;
        cancelSheetAnimation();
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (this.backgroundPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        startOpenAnimation();
        updateText(true, false);
    }

    private void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
        }
    }

    private void startOpenAnimation() {
        if (!this.dismissed) {
            this.containerView.setVisibility(0);
            if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
                setLayerType(2, (Paint) null);
            }
            ViewGroup viewGroup = this.containerView;
            viewGroup.setTranslationY((float) viewGroup.getMeasuredHeight());
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentSheetAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f})});
            this.currentSheetAnimation.setDuration(400);
            this.currentSheetAnimation.setStartDelay(20);
            this.currentSheetAnimation.setInterpolator(this.openInterpolator);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animator)) {
                        AnimatorSet unused = ProximitySheet.this.currentSheetAnimation = null;
                        int unused2 = ProximitySheet.this.currentSheetAnimationType = 0;
                        if (ProximitySheet.this.useHardwareLayer) {
                            ProximitySheet.this.setLayerType(0, (Paint) null);
                        }
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                public void onAnimationCancel(Animator animator) {
                    if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animator)) {
                        AnimatorSet unused = ProximitySheet.this.currentSheetAnimation = null;
                        int unused2 = ProximitySheet.this.currentSheetAnimationType = 0;
                    }
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentSheetAnimation.start();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.dismissed) {
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentSheetAnimation = animatorSet;
            ViewGroup viewGroup = this.containerView;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewGroup, View.TRANSLATION_Y, new float[]{(float) (viewGroup.getMeasuredHeight() + AndroidUtilities.dp(10.0f))})});
            if (this.useFastDismiss) {
                float measuredHeight = (float) this.containerView.getMeasuredHeight();
                this.currentSheetAnimation.setDuration((long) Math.max(60, (int) (((measuredHeight - this.containerView.getTranslationY()) * 250.0f) / measuredHeight)));
                this.useFastDismiss = false;
            } else {
                this.currentSheetAnimation.setDuration(250);
            }
            this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animator)) {
                        AnimatorSet unused = ProximitySheet.this.currentSheetAnimation = null;
                        int unused2 = ProximitySheet.this.currentSheetAnimationType = 0;
                        AndroidUtilities.runOnUIThread(new ProximitySheet$6$$ExternalSyntheticLambda0(this));
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0() {
                    try {
                        ProximitySheet.this.dismissInternal();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animator)) {
                        AnimatorSet unused = ProximitySheet.this.currentSheetAnimation = null;
                        int unused2 = ProximitySheet.this.currentSheetAnimationType = 0;
                    }
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentSheetAnimation.start();
        }
    }

    /* access modifiers changed from: private */
    public void dismissInternal() {
        if (getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
        this.onDismissCallback.run();
    }
}
