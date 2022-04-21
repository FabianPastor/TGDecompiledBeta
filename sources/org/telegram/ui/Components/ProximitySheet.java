package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.NumberPicker;

public class ProximitySheet extends FrameLayout {
    private int backgroundPaddingLeft;
    private int backgroundPaddingTop;
    private Paint backgroundPaint = new Paint();
    private TextView buttonTextView;
    private ViewGroup containerView;
    /* access modifiers changed from: private */
    public AnimatorSet currentAnimation = null;
    /* access modifiers changed from: private */
    public AnimatorSet currentSheetAnimation;
    /* access modifiers changed from: private */
    public int currentSheetAnimationType;
    private TLRPC.User currentUser;
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
    private Interpolator openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
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
    public boolean useHardwareLayer = true;
    private boolean useImperialSystem;
    private VelocityTracker velocityTracker = null;

    public interface onRadiusPickerChange {
        boolean run(boolean z, int i);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ProximitySheet(Context context, TLRPC.User user, onRadiusPickerChange onRadius, onRadiusPickerChange onFinish, Runnable onDismiss) {
        super(context);
        Context context2 = context;
        setWillNotDraw(false);
        this.onDismissCallback = onDismiss;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect padding = new Rect();
        Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        shadowDrawable.getPadding(padding);
        this.backgroundPaddingLeft = padding.left;
        AnonymousClass1 r9 = new FrameLayout(getContext()) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.containerView = r9;
        r9.setBackgroundDrawable(shadowDrawable);
        this.containerView.setPadding(this.backgroundPaddingLeft, (AndroidUtilities.dp(8.0f) + padding.top) - 1, this.backgroundPaddingLeft, 0);
        this.containerView.setVisibility(4);
        addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        this.useImperialSystem = LocaleController.getUseImperialSystemType();
        this.currentUser = user;
        this.onRadiusChange = onRadius;
        NumberPicker numberPicker = new NumberPicker(context2);
        this.kmPicker = numberPicker;
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        this.kmPicker.setItemCount(5);
        NumberPicker numberPicker2 = new NumberPicker(context2);
        this.mPicker = numberPicker2;
        numberPicker2.setItemCount(5);
        this.mPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        AnonymousClass2 r12 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                ProximitySheet.this.kmPicker.setItemCount(count);
                ProximitySheet.this.mPicker.setItemCount(count);
                ProximitySheet.this.kmPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                ProximitySheet.this.mPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                this.ignoreLayout = false;
                int unused = ProximitySheet.this.totalWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                if (0 != ProximitySheet.this.totalWidth) {
                    ProximitySheet.this.updateText(false, false);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.customView = r12;
        r12.setOrientation(1);
        FrameLayout titleLayout = new FrameLayout(context2);
        this.customView.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context2);
        titleView.setText(LocaleController.getString("LocationNotifiation", NUM));
        titleView.setTextColor(Theme.getColor("dialogTextBlack"));
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(ProximitySheet$$ExternalSyntheticLambda1.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        this.customView.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTimeMillis = System.currentTimeMillis();
        FrameLayout buttonContainer = new FrameLayout(context2);
        this.infoTextView = new TextView(context2);
        this.buttonTextView = new TextView(context2) {
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
        NumberPicker.OnValueChangeListener onValueChangeListener = new ProximitySheet$$ExternalSyntheticLambda4(this);
        this.kmPicker.setOnValueChangedListener(onValueChangeListener);
        this.mPicker.setMinValue(0);
        this.mPicker.setMaxValue(10);
        this.mPicker.setWrapSelectorWheel(false);
        this.mPicker.setTextOffset(-AndroidUtilities.dp(20.0f));
        linearLayout.addView(this.mPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        this.mPicker.setFormatter(new ProximitySheet$$ExternalSyntheticLambda3(this));
        this.mPicker.setOnValueChangedListener(onValueChangeListener);
        this.kmPicker.setValue(0);
        this.mPicker.setValue(6);
        this.customView.addView(buttonContainer, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setMaxLines(2);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        buttonContainer.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f));
        this.buttonTextView.setOnClickListener(new ProximitySheet$$ExternalSyntheticLambda0(this, onFinish));
        this.infoTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.infoTextView.setGravity(17);
        this.infoTextView.setTextColor(Theme.getColor("dialogTextGray2"));
        this.infoTextView.setTextSize(1, 14.0f);
        this.infoTextView.setAlpha(0.0f);
        this.infoTextView.setScaleX(0.5f);
        this.infoTextView.setScaleY(0.5f);
        buttonContainer.addView(this.infoTextView, LayoutHelper.createFrame(-1, 48.0f));
        this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2, 51));
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ProximitySheet  reason: not valid java name */
    public /* synthetic */ String m4249lambda$new$1$orgtelegramuiComponentsProximitySheet(int value) {
        if (this.useImperialSystem) {
            return LocaleController.formatString("MilesShort", NUM, Integer.valueOf(value));
        }
        return LocaleController.formatString("KMetersShort", NUM, Integer.valueOf(value));
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ProximitySheet  reason: not valid java name */
    public /* synthetic */ void m4250lambda$new$2$orgtelegramuiComponentsProximitySheet(NumberPicker picker, int oldVal, int newVal) {
        try {
            performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        updateText(true, true);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ProximitySheet  reason: not valid java name */
    public /* synthetic */ String m4251lambda$new$3$orgtelegramuiComponentsProximitySheet(int value) {
        if (this.useImperialSystem) {
            if (value == 1) {
                return LocaleController.formatString("FootsShort", NUM, 250);
            }
            if (value > 1) {
                value--;
            }
            return String.format(Locale.US, ".%d", new Object[]{Integer.valueOf(value)});
        } else if (value == 1) {
            return LocaleController.formatString("MetersShort", NUM, 50);
        } else {
            if (value > 1) {
                value--;
            }
            return LocaleController.formatString("MetersShort", NUM, Integer.valueOf(value * 100));
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ProximitySheet  reason: not valid java name */
    public /* synthetic */ void m4252lambda$new$4$orgtelegramuiComponentsProximitySheet(onRadiusPickerChange onFinish, View v) {
        if (this.buttonTextView.getTag() == null && onFinish.run(true, (int) Math.max(1.0f, getValue()))) {
            dismiss();
        }
    }

    public View getCustomView() {
        return this.customView;
    }

    public float getValue() {
        float value;
        float value2 = (float) (this.kmPicker.getValue() * 1000);
        int second = this.mPicker.getValue();
        boolean z = this.useImperialSystem;
        if (z) {
            if (second == 1) {
                value = value2 + 47.349f;
            } else {
                if (second > 1) {
                    second--;
                }
                value = value2 + ((float) (second * 100));
            }
        } else if (second == 1) {
            value = value2 + 50.0f;
        } else {
            if (second > 1) {
                second--;
            }
            value = value2 + ((float) (second * 100));
        }
        if (z) {
            return value * 1.60934f;
        }
        return value;
    }

    public boolean getRadiusSet() {
        return this.radiusSet;
    }

    public void setRadiusSet() {
        this.radiusSet = true;
    }

    public void updateText(boolean move, boolean animated) {
        float value = getValue();
        String distance = LocaleController.formatDistance(value, 2, Boolean.valueOf(this.useImperialSystem));
        if (this.onRadiusChange.run(move, (int) value) || this.currentUser == null) {
            if (this.currentUser == null) {
                this.buttonTextView.setText(LocaleController.formatString("LocationNotifiationButtonGroup", NUM, distance));
            } else {
                String format = LocaleController.getString("LocationNotifiationButtonUser", NUM);
                CharSequence name = TextUtils.ellipsize(UserObject.getFirstName(this.currentUser), this.buttonTextView.getPaint(), (float) Math.max(AndroidUtilities.dp(10.0f), (int) ((((float) (this.totalWidth - AndroidUtilities.dp(94.0f))) * 1.5f) - ((float) ((int) Math.ceil((double) this.buttonTextView.getPaint().measureText(format)))))), TextUtils.TruncateAt.END);
                this.buttonTextView.setText(LocaleController.formatString("LocationNotifiationButtonUser", NUM, name, distance));
            }
            if (this.buttonTextView.getTag() != null) {
                this.buttonTextView.setTag((Object) null);
                this.buttonTextView.animate().setDuration(180).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).start();
                this.infoTextView.animate().setDuration(180).alpha(0.0f).scaleX(0.5f).scaleY(0.5f).start();
                return;
            }
            return;
        }
        this.infoTextView.setText(LocaleController.formatString("LocationNotifiationCloser", NUM, distance));
        if (this.buttonTextView.getTag() == null) {
            this.buttonTextView.setTag(1);
            this.buttonTextView.animate().setDuration(180).alpha(0.0f).scaleX(0.5f).scaleY(0.5f).start();
            this.infoTextView.animate().setDuration(180).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).start();
        }
    }

    private void checkDismiss(float velX, float velY) {
        float translationY = this.containerView.getTranslationY();
        if (!((translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (velY < 3500.0f || Math.abs(velY) < Math.abs(velX))) || (velY < 0.0f && Math.abs(velY) >= 3500.0f))) {
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
            public void onAnimationEnd(Animator animation) {
                if (ProximitySheet.this.currentAnimation != null && ProximitySheet.this.currentAnimation.equals(animation)) {
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
    public boolean processTouchEvent(MotionEvent ev, boolean intercept) {
        if (this.dismissed) {
            return false;
        }
        if (ev != null && ((ev.getAction() == 0 || ev.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking && ev.getPointerCount() == 1)) {
            this.startedTrackingX = (int) ev.getX();
            int y = (int) ev.getY();
            this.startedTrackingY = y;
            if (y < this.containerView.getTop() || this.startedTrackingX < this.containerView.getLeft() || this.startedTrackingX > this.containerView.getRight()) {
                requestDisallowInterceptTouchEvent(true);
                dismiss();
                return true;
            }
            this.startedTrackingPointerId = ev.getPointerId(0);
            this.maybeStartTracking = true;
            cancelCurrentAnimation();
            VelocityTracker velocityTracker2 = this.velocityTracker;
            if (velocityTracker2 != null) {
                velocityTracker2.clear();
            }
        } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            float dx = (float) Math.abs((int) (ev.getX() - ((float) this.startedTrackingX)));
            float dy = (float) (((int) ev.getY()) - this.startedTrackingY);
            this.velocityTracker.addMovement(ev);
            if (this.maybeStartTracking && !this.startedTracking && dy > 0.0f && dy / 3.0f > Math.abs(dx) && Math.abs(dy) >= ((float) this.touchSlop)) {
                this.startedTrackingY = (int) ev.getY();
                this.maybeStartTracking = false;
                this.startedTracking = true;
                requestDisallowInterceptTouchEvent(true);
            } else if (this.startedTracking) {
                float translationY = this.containerView.getTranslationY() + dy;
                if (translationY < 0.0f) {
                    translationY = 0.0f;
                }
                this.containerView.setTranslationY(translationY);
                this.startedTrackingY = (int) ev.getY();
            }
        } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
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
            VelocityTracker velocityTracker3 = this.velocityTracker;
            if (velocityTracker3 != null) {
                velocityTracker3.recycle();
                this.velocityTracker = null;
            }
            this.startedTrackingPointerId = -1;
        }
        if ((intercept || !this.maybeStartTracking) && !this.startedTracking) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return this.dismissed || processTouchEvent(ev, false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        setMeasuredDimension(width, height);
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec((this.backgroundPaddingLeft * 2) + width, NUM), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (!(child.getVisibility() == 8 || child == this.containerView)) {
                measureChildWithMargins(child, View.MeasureSpec.makeMeasureSpec(width, NUM), 0, View.MeasureSpec.makeMeasureSpec(height, NUM), 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft;
        int childTop;
        int t = (bottom - top) - this.containerView.getMeasuredHeight();
        int l = ((right - left) - this.containerView.getMeasuredWidth()) / 2;
        ViewGroup viewGroup = this.containerView;
        viewGroup.layout(l, t, viewGroup.getMeasuredWidth() + l, this.containerView.getMeasuredHeight() + t);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (!(child.getVisibility() == 8 || child == this.containerView)) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = 51;
                }
                int verticalGravity = gravity & 112;
                switch (gravity & 7 & 7) {
                    case 1:
                        childLeft = ((((right - left) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                        break;
                    case 5:
                        childLeft = (right - width) - lp.rightMargin;
                        break;
                    default:
                        childLeft = lp.leftMargin;
                        break;
                }
                switch (verticalGravity) {
                    case 16:
                        childTop = ((((bottom - top) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        break;
                    case 80:
                        childTop = ((bottom - top) - height) - lp.bottomMargin;
                        break;
                    default:
                        childTop = lp.topMargin;
                        break;
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.dismissed || processTouchEvent(event, true);
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent((MotionEvent) null);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean hasOverlappingRendering() {
        return false;
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
            this.currentSheetAnimationType = 0;
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
            this.currentSheetAnimationType = 1;
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentSheetAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f})});
            this.currentSheetAnimation.setDuration(400);
            this.currentSheetAnimation.setStartDelay(20);
            this.currentSheetAnimation.setInterpolator(this.openInterpolator);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animation)) {
                        AnimatorSet unused = ProximitySheet.this.currentSheetAnimation = null;
                        int unused2 = ProximitySheet.this.currentSheetAnimationType = 0;
                        if (ProximitySheet.this.useHardwareLayer) {
                            ProximitySheet.this.setLayerType(0, (Paint) null);
                        }
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                public void onAnimationCancel(Animator animation) {
                    if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animation)) {
                        AnimatorSet unused = ProximitySheet.this.currentSheetAnimation = null;
                        int unused2 = ProximitySheet.this.currentSheetAnimationType = 0;
                    }
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentSheetAnimation.start();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.dismissed) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            this.currentSheetAnimationType = 2;
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentSheetAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))})});
            if (this.useFastDismiss) {
                int height = this.containerView.getMeasuredHeight();
                this.currentSheetAnimation.setDuration((long) Math.max(60, (int) (((((float) height) - this.containerView.getTranslationY()) * 250.0f) / ((float) height))));
                this.useFastDismiss = false;
            } else {
                this.currentSheetAnimation.setDuration(250);
            }
            this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animation)) {
                        AnimatorSet unused = ProximitySheet.this.currentSheetAnimation = null;
                        int unused2 = ProximitySheet.this.currentSheetAnimationType = 0;
                        AndroidUtilities.runOnUIThread(new ProximitySheet$6$$ExternalSyntheticLambda0(this));
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-ProximitySheet$6  reason: not valid java name */
                public /* synthetic */ void m4253xb0bca8e() {
                    try {
                        ProximitySheet.this.dismissInternal();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animation)) {
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
