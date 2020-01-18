package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

public class BottomSheet extends Dialog {
    private boolean allowCustomAnimation = true;
    private boolean allowDrawContent = true;
    private boolean allowNestedScroll = true;
    private boolean applyBottomPadding = true;
    private boolean applyTopPadding = true;
    protected ColorDrawable backDrawable = new ColorDrawable(-16777216);
    protected int backgroundPaddingLeft;
    protected int backgroundPaddingTop;
    private boolean bigTitle;
    private boolean canDismissWithSwipe = true;
    protected ContainerView container;
    protected ViewGroup containerView = r4[0];
    protected int currentAccount = UserConfig.selectedAccount;
    protected AnimatorSet currentSheetAnimation;
    protected int currentSheetAnimationType;
    private View customView;
    private BottomSheetDelegateInterface delegate;
    private boolean dimBehind = true;
    private boolean disableScroll;
    private Runnable dismissRunnable = new -$$Lambda$wKJSb77Iz9CSKJu9VMkyxGvOd-c(this);
    private boolean dismissed;
    private boolean focusable;
    protected boolean fullWidth;
    protected boolean isFullscreen;
    private int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews = new ArrayList();
    private CharSequence[] items;
    private WindowInsets lastInsets;
    private int layoutCount;
    protected View nestedScrollChild;
    private OnClickListener onClickListener;
    protected Interpolator openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    protected Drawable shadowDrawable;
    private boolean showWithoutAnimation;
    private Runnable startAnimationRunnable;
    private int tag;
    private CharSequence title;
    private TextView titleView;
    private int touchSlop;
    private boolean useFastDismiss;
    private boolean useHardwareLayer = true;

    public static class BottomSheetCell extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public BottomSheetCell(Context context, int i) {
            super(context);
            setBackground(null);
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), Mode.MULTIPLY));
            int i2 = 5;
            addView(this.imageView, LayoutHelper.createFrame(56, 48, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            String str = "dialogTextBlack";
            if (i == 0) {
                this.textView.setTextColor(Theme.getColor(str));
                this.textView.setTextSize(1, 16.0f);
                TextView textView = this.textView;
                if (!LocaleController.isRTL) {
                    i2 = 3;
                }
                addView(textView, LayoutHelper.createFrame(-2, -2, i2 | 16));
            } else if (i == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(Theme.getColor(str));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setTextAndIcon(CharSequence charSequence, int i) {
            setTextAndIcon(charSequence, i, false);
        }

        public void setTextAndIcon(CharSequence charSequence, int i, boolean z) {
            this.textView.setText(charSequence);
            float f = 21.0f;
            if (i != 0) {
                this.imageView.setImageResource(i);
                this.imageView.setVisibility(0);
                float f2 = 72.0f;
                if (z) {
                    TextView textView = this.textView;
                    int dp = AndroidUtilities.dp(LocaleController.isRTL ? 21.0f : 72.0f);
                    if (LocaleController.isRTL) {
                        f = 72.0f;
                    }
                    textView.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
                    this.imageView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(5.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(5.0f) : 5, 0);
                    return;
                }
                TextView textView2 = this.textView;
                int dp2 = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 72.0f);
                if (!LocaleController.isRTL) {
                    f2 = 16.0f;
                }
                textView2.setPadding(dp2, 0, AndroidUtilities.dp(f2), 0);
                this.imageView.setPadding(0, 0, 0, 0);
                return;
            }
            this.imageView.setVisibility(4);
            TextView textView3 = this.textView;
            int dp3 = AndroidUtilities.dp(z ? 21.0f : 16.0f);
            if (!z) {
                f = 16.0f;
            }
            textView3.setPadding(dp3, 0, AndroidUtilities.dp(f), 0);
        }
    }

    public interface BottomSheetDelegateInterface {
        boolean canDismiss();

        void onOpenAnimationEnd();

        void onOpenAnimationStart();
    }

    public static class Builder {
        private BottomSheet bottomSheet;

        public Builder(Context context) {
            this.bottomSheet = new BottomSheet(context, false);
        }

        public Builder(Context context, boolean z) {
            this.bottomSheet = new BottomSheet(context, z);
        }

        public Builder setItems(CharSequence[] charSequenceArr, OnClickListener onClickListener) {
            this.bottomSheet.items = charSequenceArr;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, int[] iArr, OnClickListener onClickListener) {
            this.bottomSheet.items = charSequenceArr;
            this.bottomSheet.itemIcons = iArr;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setCustomView(View view) {
            this.bottomSheet.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            return setTitle(charSequence, false);
        }

        public Builder setTitle(CharSequence charSequence, boolean z) {
            this.bottomSheet.title = charSequence;
            this.bottomSheet.bigTitle = z;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet setDimBehind(boolean z) {
            this.bottomSheet.dimBehind = z;
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setTag(int i) {
            this.bottomSheet.tag = i;
            return this;
        }

        public Builder setUseHardwareLayer(boolean z) {
            this.bottomSheet.useHardwareLayer = z;
            return this;
        }

        public Builder setDelegate(BottomSheetDelegate bottomSheetDelegate) {
            this.bottomSheet.setDelegate(bottomSheetDelegate);
            return this;
        }

        public Builder setApplyTopPadding(boolean z) {
            this.bottomSheet.applyTopPadding = z;
            return this;
        }

        public Builder setApplyBottomPadding(boolean z) {
            this.bottomSheet.applyBottomPadding = z;
            return this;
        }

        public Runnable getDismissRunnable() {
            return this.bottomSheet.dismissRunnable;
        }

        public BottomSheet setUseFullWidth(boolean z) {
            BottomSheet bottomSheet = this.bottomSheet;
            bottomSheet.fullWidth = z;
            return bottomSheet;
        }

        public BottomSheet setUseFullscreen(boolean z) {
            BottomSheet bottomSheet = this.bottomSheet;
            bottomSheet.isFullscreen = z;
            return bottomSheet;
        }
    }

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        public boolean canDismiss() {
            return true;
        }

        public void onOpenAnimationEnd() {
        }

        public void onOpenAnimationStart() {
        }
    }

    protected class ContainerView extends FrameLayout implements NestedScrollingParent {
        private AnimatorSet currentAnimation = null;
        private boolean maybeStartTracking = false;
        private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        private boolean startedTracking = false;
        private int startedTrackingPointerId = -1;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker = null;

        public boolean hasOverlappingRendering() {
            return false;
        }

        public boolean onNestedFling(View view, float f, float f2, boolean z) {
            return false;
        }

        public boolean onNestedPreFling(View view, float f, float f2) {
            return false;
        }

        public ContainerView(Context context) {
            super(context);
        }

        public boolean onStartNestedScroll(View view, View view2, int i) {
            view2 = BottomSheet.this.nestedScrollChild;
            return (view2 == null || view == view2) && !BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll && i == 2 && !BottomSheet.this.canDismissWithSwipe();
        }

        public void onNestedScrollAccepted(View view, View view2, int i) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
            }
        }

        public void onStopNestedScroll(View view) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                BottomSheet.this.containerView.getTranslationY();
                checkDismiss(0.0f, 0.0f);
            }
        }

        public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
                if (i4 != 0) {
                    float translationY = BottomSheet.this.containerView.getTranslationY() - ((float) i4);
                    if (translationY < 0.0f) {
                        translationY = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(translationY);
                }
            }
        }

        public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
                float translationY = BottomSheet.this.containerView.getTranslationY();
                if (translationY > 0.0f && i2 > 0) {
                    translationY -= (float) i2;
                    iArr[1] = i2;
                    if (translationY < 0.0f) {
                        translationY = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(translationY);
                }
            }
        }

        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        private void checkDismiss(float f, float f2) {
            float translationY = BottomSheet.this.containerView.getTranslationY();
            Object obj = ((translationY >= AndroidUtilities.getPixelsInCM(0.8f, false) || (f2 >= 3500.0f && Math.abs(f2) >= Math.abs(f))) && (f2 >= 0.0f || Math.abs(f2) < 3500.0f)) ? null : 1;
            if (obj == null) {
                boolean access$200 = BottomSheet.this.allowCustomAnimation;
                BottomSheet.this.allowCustomAnimation = false;
                BottomSheet.this.useFastDismiss = true;
                BottomSheet.this.dismiss();
                BottomSheet.this.allowCustomAnimation = access$200;
                return;
            }
            this.currentAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.currentAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.currentAnimation.setDuration((long) ((int) ((translationY / AndroidUtilities.getPixelsInCM(0.8f, false)) * 150.0f)));
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animator)) {
                        ContainerView.this.currentAnimation = null;
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(512));
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(512));
            this.currentAnimation.start();
        }

        private void cancelCurrentAnimation() {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
        }

        /* Access modifiers changed, original: 0000 */
        public boolean processTouchEvent(MotionEvent motionEvent, boolean z) {
            boolean z2 = false;
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(motionEvent)) {
                return true;
            }
            VelocityTracker velocityTracker;
            if (!BottomSheet.this.canDismissWithTouchOutside() || motionEvent == null || (!(motionEvent.getAction() == 0 || motionEvent.getAction() == 2) || this.startedTracking || this.maybeStartTracking || motionEvent.getPointerCount() != 1)) {
                float f = 0.0f;
                if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    float abs = (float) Math.abs((int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                    float y = (float) (((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (!BottomSheet.this.disableScroll && this.maybeStartTracking && !this.startedTracking && y > 0.0f && y / 3.0f > Math.abs(abs) && Math.abs(y) >= ((float) BottomSheet.this.touchSlop)) {
                        this.startedTrackingY = (int) motionEvent.getY();
                        this.maybeStartTracking = false;
                        this.startedTracking = true;
                        requestDisallowInterceptTouchEvent(true);
                    } else if (this.startedTracking) {
                        abs = BottomSheet.this.containerView.getTranslationY() + y;
                        if (abs >= 0.0f) {
                            f = abs;
                        }
                        BottomSheet.this.containerView.setTranslationY(f);
                        this.startedTrackingY = (int) motionEvent.getY();
                    }
                } else if (motionEvent == null || (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    float translationY = BottomSheet.this.containerView.getTranslationY();
                    if (this.startedTracking || translationY != 0.0f) {
                        checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                    }
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                    this.startedTrackingPointerId = -1;
                }
            } else {
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                if (this.startedTrackingY < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.dismiss();
                    return true;
                }
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            }
            if ((!z && this.maybeStartTracking) || this.startedTracking || !BottomSheet.this.canDismissWithSwipe()) {
                z2 = true;
            }
            return z2;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return processTouchEvent(motionEvent, false);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            int makeMeasureSpec;
            i = MeasureSpec.getSize(i);
            i2 = MeasureSpec.getSize(i2);
            if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                i2 -= BottomSheet.this.lastInsets.getSystemWindowInsetBottom();
            }
            setMeasuredDimension(i, i2);
            if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                i -= BottomSheet.this.lastInsets.getSystemWindowInsetRight() + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
            }
            Object obj = i < i2 ? 1 : null;
            BottomSheet bottomSheet = BottomSheet.this;
            ViewGroup viewGroup = bottomSheet.containerView;
            if (viewGroup != null) {
                if (bottomSheet.fullWidth) {
                    viewGroup.measure(MeasureSpec.makeMeasureSpec((bottomSheet.backgroundPaddingLeft * 2) + i, NUM), MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
                } else {
                    if (AndroidUtilities.isTablet()) {
                        Point point = AndroidUtilities.displaySize;
                        makeMeasureSpec = MeasureSpec.makeMeasureSpec(((int) (((float) Math.min(point.x, point.y)) * 0.8f)) + (BottomSheet.this.backgroundPaddingLeft * 2), NUM);
                    } else {
                        makeMeasureSpec = MeasureSpec.makeMeasureSpec(obj != null ? (BottomSheet.this.backgroundPaddingLeft * 2) + i : ((int) Math.max(((float) i) * 0.8f, (float) Math.min(AndroidUtilities.dp(480.0f), i))) + (BottomSheet.this.backgroundPaddingLeft * 2), NUM);
                    }
                    BottomSheet.this.containerView.measure(makeMeasureSpec, MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
                }
            }
            makeMeasureSpec = getChildCount();
            for (int i3 = 0; i3 < makeMeasureSpec; i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() != 8) {
                    bottomSheet = BottomSheet.this;
                    if (!(childAt == bottomSheet.containerView || bottomSheet.onCustomMeasure(childAt, i, i2))) {
                        measureChildWithMargins(childAt, MeasureSpec.makeMeasureSpec(i, NUM), 0, MeasureSpec.makeMeasureSpec(i2, NUM), 0);
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00f2  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00de  */
        public void onLayout(boolean r14, int r15, int r16, int r17, int r18) {
            /*
            r13 = this;
            r0 = r13;
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r1.layoutCount = r1.layoutCount - 1;
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r2 = r1.containerView;
            r7 = 21;
            if (r2 == 0) goto L_0x007a;
        L_0x000e:
            r1 = r1.lastInsets;
            if (r1 == 0) goto L_0x0030;
        L_0x0014:
            r1 = android.os.Build.VERSION.SDK_INT;
            if (r1 < r7) goto L_0x0030;
        L_0x0018:
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r1 = r1.lastInsets;
            r1 = r1.getSystemWindowInsetLeft();
            r1 = r1 + r15;
            r2 = org.telegram.ui.ActionBar.BottomSheet.this;
            r2 = r2.lastInsets;
            r2 = r2.getSystemWindowInsetRight();
            r2 = r17 - r2;
            goto L_0x0033;
        L_0x0030:
            r1 = r15;
            r2 = r17;
        L_0x0033:
            r3 = r18 - r16;
            r4 = org.telegram.ui.ActionBar.BottomSheet.this;
            r4 = r4.containerView;
            r4 = r4.getMeasuredHeight();
            r3 = r3 - r4;
            r4 = r2 - r1;
            r5 = org.telegram.ui.ActionBar.BottomSheet.this;
            r5 = r5.containerView;
            r5 = r5.getMeasuredWidth();
            r4 = r4 - r5;
            r4 = r4 / 2;
            r5 = org.telegram.ui.ActionBar.BottomSheet.this;
            r5 = r5.lastInsets;
            if (r5 == 0) goto L_0x0062;
        L_0x0053:
            r5 = android.os.Build.VERSION.SDK_INT;
            if (r5 < r7) goto L_0x0062;
        L_0x0057:
            r5 = org.telegram.ui.ActionBar.BottomSheet.this;
            r5 = r5.lastInsets;
            r5 = r5.getSystemWindowInsetLeft();
            r4 = r4 + r5;
        L_0x0062:
            r5 = org.telegram.ui.ActionBar.BottomSheet.this;
            r5 = r5.containerView;
            r6 = r5.getMeasuredWidth();
            r6 = r6 + r4;
            r8 = org.telegram.ui.ActionBar.BottomSheet.this;
            r8 = r8.containerView;
            r8 = r8.getMeasuredHeight();
            r8 = r8 + r3;
            r5.layout(r4, r3, r6, r8);
            r8 = r1;
            r9 = r2;
            goto L_0x007d;
        L_0x007a:
            r8 = r15;
            r9 = r17;
        L_0x007d:
            r10 = r13.getChildCount();
            r1 = 0;
            r11 = 0;
        L_0x0083:
            if (r11 >= r10) goto L_0x011e;
        L_0x0085:
            r12 = r13.getChildAt(r11);
            r1 = r12.getVisibility();
            r2 = 8;
            if (r1 == r2) goto L_0x011a;
        L_0x0091:
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r2 = r1.containerView;
            if (r12 != r2) goto L_0x0099;
        L_0x0097:
            goto L_0x011a;
        L_0x0099:
            r2 = r12;
            r3 = r8;
            r4 = r16;
            r5 = r9;
            r6 = r18;
            r1 = r1.onCustomLayout(r2, r3, r4, r5, r6);
            if (r1 != 0) goto L_0x011a;
        L_0x00a6:
            r1 = r12.getLayoutParams();
            r1 = (android.widget.FrameLayout.LayoutParams) r1;
            r2 = r12.getMeasuredWidth();
            r3 = r12.getMeasuredHeight();
            r4 = r1.gravity;
            r5 = -1;
            if (r4 != r5) goto L_0x00bb;
        L_0x00b9:
            r4 = 51;
        L_0x00bb:
            r5 = r4 & 7;
            r4 = r4 & 112;
            r5 = r5 & 7;
            r6 = 1;
            if (r5 == r6) goto L_0x00cf;
        L_0x00c4:
            r6 = 5;
            if (r5 == r6) goto L_0x00ca;
        L_0x00c7:
            r5 = r1.leftMargin;
            goto L_0x00da;
        L_0x00ca:
            r5 = r9 - r2;
            r6 = r1.rightMargin;
            goto L_0x00d9;
        L_0x00cf:
            r5 = r9 - r8;
            r5 = r5 - r2;
            r5 = r5 / 2;
            r6 = r1.leftMargin;
            r5 = r5 + r6;
            r6 = r1.rightMargin;
        L_0x00d9:
            r5 = r5 - r6;
        L_0x00da:
            r6 = 16;
            if (r4 == r6) goto L_0x00f2;
        L_0x00de:
            r6 = 48;
            if (r4 == r6) goto L_0x00ef;
        L_0x00e2:
            r6 = 80;
            if (r4 == r6) goto L_0x00e9;
        L_0x00e6:
            r1 = r1.topMargin;
            goto L_0x00fe;
        L_0x00e9:
            r4 = r18 - r16;
            r4 = r4 - r3;
            r1 = r1.bottomMargin;
            goto L_0x00fc;
        L_0x00ef:
            r1 = r1.topMargin;
            goto L_0x00fe;
        L_0x00f2:
            r4 = r18 - r16;
            r4 = r4 - r3;
            r4 = r4 / 2;
            r6 = r1.topMargin;
            r4 = r4 + r6;
            r1 = r1.bottomMargin;
        L_0x00fc:
            r1 = r4 - r1;
        L_0x00fe:
            r4 = org.telegram.ui.ActionBar.BottomSheet.this;
            r4 = r4.lastInsets;
            if (r4 == 0) goto L_0x0115;
        L_0x0106:
            r4 = android.os.Build.VERSION.SDK_INT;
            if (r4 < r7) goto L_0x0115;
        L_0x010a:
            r4 = org.telegram.ui.ActionBar.BottomSheet.this;
            r4 = r4.lastInsets;
            r4 = r4.getSystemWindowInsetLeft();
            r5 = r5 + r4;
        L_0x0115:
            r2 = r2 + r5;
            r3 = r3 + r1;
            r12.layout(r5, r1, r2, r3);
        L_0x011a:
            r11 = r11 + 1;
            goto L_0x0083;
        L_0x011e:
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r1 = r1.layoutCount;
            if (r1 != 0) goto L_0x0146;
        L_0x0126:
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r1 = r1.startAnimationRunnable;
            if (r1 == 0) goto L_0x0146;
        L_0x012e:
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r1 = r1.startAnimationRunnable;
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1);
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r1 = r1.startAnimationRunnable;
            r1.run();
            r1 = org.telegram.ui.ActionBar.BottomSheet.this;
            r2 = 0;
            r1.startAnimationRunnable = r2;
        L_0x0146:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet$ContainerView.onLayout(boolean, int, int, int, int):void");
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (BottomSheet.this.canDismissWithSwipe()) {
                return processTouchEvent(motionEvent, true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(z);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            BottomSheet.this.onContainerDraw(canvas);
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithTouchOutside() {
        return true;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return null;
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    public void onContainerDraw(Canvas canvas) {
    }

    /* Access modifiers changed, original: protected */
    public boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onContainerTranslationYChanged(float f) {
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomCloseAnimation() {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomMeasure(View view, int i, int i2) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomOpenAnimation() {
        return false;
    }

    public void setDisableScroll(boolean z) {
        this.disableScroll = z;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
        if (!this.allowNestedScroll) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, boolean z) {
        super(context, NUM);
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-NUM);
        }
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect rect = new Rect();
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), Mode.MULTIPLY));
        this.shadowDrawable.getPadding(rect);
        this.backgroundPaddingLeft = rect.left;
        this.backgroundPaddingTop = rect.top;
        this.container = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean z = true;
                try {
                    if (!(BottomSheet.this.allowDrawContent && super.drawChild(canvas, view, j))) {
                        z = false;
                    }
                    return z;
                } catch (Exception e) {
                    FileLog.e(e);
                    return true;
                }
            }
        };
        this.container.setBackgroundDrawable(this.backDrawable);
        this.focusable = z;
        if (VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new -$$Lambda$BottomSheet$IjDyKTRWpdCwBFc4MNcspRHUp7w(this));
            this.container.setSystemUiVisibility(1280);
        }
        this.backDrawable.setAlpha(0);
    }

    public /* synthetic */ WindowInsets lambda$new$0$BottomSheet(View view, WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        view.requestLayout();
        return windowInsets.consumeSystemWindowInsets();
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        Window window = getWindow();
        window.setWindowAnimations(NUM);
        setContentView(this.container, new LayoutParams(-1, -1));
        if (VERSION.SDK_INT >= 23 && Theme.getColor("actionBarDefault", null, true) == -1) {
            this.container.setSystemUiVisibility(this.container.getSystemUiVisibility() | 8192);
        }
        if (this.containerView == null) {
            this.containerView = new FrameLayout(getContext()) {
                public boolean hasOverlappingRendering() {
                    return false;
                }

                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    BottomSheet.this.onContainerTranslationYChanged(f);
                }
            };
            this.containerView.setBackgroundDrawable(this.shadowDrawable);
            this.containerView.setPadding(this.backgroundPaddingLeft, ((this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0) + this.backgroundPaddingTop) - 1, this.backgroundPaddingLeft, this.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        if (this.title != null) {
            this.titleView = new TextView(getContext());
            this.titleView.setLines(1);
            this.titleView.setSingleLine(true);
            this.titleView.setText(this.title);
            if (this.bigTitle) {
                this.titleView.setTextColor(Theme.getColor("dialogTextBlack"));
                this.titleView.setTextSize(1, 20.0f);
                this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            } else {
                this.titleView.setTextColor(Theme.getColor("dialogTextGray2"));
                this.titleView.setTextSize(1, 16.0f);
                this.titleView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            }
            this.titleView.setEllipsize(TruncateAt.MIDDLE);
            this.titleView.setGravity(16);
            this.containerView.addView(this.titleView, LayoutHelper.createFrame(-1, 48.0f));
            this.titleView.setOnTouchListener(-$$Lambda$BottomSheet$bysjO3P7kPXgYfq-9zd4-H2r0_8.INSTANCE);
            i = 48;
        } else {
            i = 0;
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, (float) i, 0.0f, 0.0f));
        } else if (this.items != null) {
            int i2 = i;
            i = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (i >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[i] != null) {
                    BottomSheetCell bottomSheetCell = new BottomSheetCell(getContext(), 0);
                    CharSequence charSequence = this.items[i];
                    int[] iArr = this.itemIcons;
                    bottomSheetCell.setTextAndIcon(charSequence, iArr != null ? iArr[i] : 0, this.bigTitle);
                    this.containerView.addView(bottomSheetCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) i2, 0.0f, 0.0f));
                    i2 += 48;
                    bottomSheetCell.setTag(Integer.valueOf(i));
                    bottomSheetCell.setOnClickListener(new -$$Lambda$BottomSheet$6IWrsZfWA7fvlM9-8brqhUJi-uM(this));
                    this.itemViews.add(bottomSheetCell);
                }
                i++;
            }
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        attributes.flags &= -3;
        if (this.focusable) {
            attributes.softInputMode = 16;
        } else {
            attributes.flags |= 131072;
        }
        if (this.isFullscreen) {
            if (VERSION.SDK_INT >= 21) {
                attributes.flags |= -NUM;
            }
            attributes.flags |= 1024;
            this.container.setSystemUiVisibility(1284);
        }
        attributes.height = -1;
        if (VERSION.SDK_INT >= 28) {
            attributes.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(attributes);
    }

    public /* synthetic */ void lambda$onCreate$2$BottomSheet(View view) {
        dismissWithButtonClick(((Integer) view.getTag()).intValue());
    }

    public boolean isFocusable() {
        return this.focusable;
    }

    public void setFocusable(boolean z) {
        if (this.focusable != z) {
            this.focusable = z;
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (this.focusable) {
                attributes.softInputMode = 16;
                attributes.flags &= -131073;
            } else {
                attributes.softInputMode = 48;
                attributes.flags |= 131072;
            }
            window.setAttributes(attributes);
        }
    }

    public void setShowWithoutAnimation(boolean z) {
        this.showWithoutAnimation = z;
    }

    public void setBackgroundColor(int i) {
        this.shadowDrawable.setColorFilter(i, Mode.MULTIPLY);
    }

    public void show() {
        super.show();
        if (this.focusable) {
            getWindow().setSoftInputMode(16);
        }
        int i = 0;
        this.dismissed = false;
        cancelSheetAnimation();
        this.containerView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (this.backgroundPaddingLeft * 2), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        if (this.showWithoutAnimation) {
            ColorDrawable colorDrawable = this.backDrawable;
            if (this.dimBehind) {
                i = 51;
            }
            colorDrawable.setAlpha(i);
            this.containerView.setTranslationY(0.0f);
            return;
        }
        this.backDrawable.setAlpha(0);
        if (VERSION.SDK_INT >= 18) {
            this.layoutCount = 2;
            ViewGroup viewGroup = this.containerView;
            viewGroup.setTranslationY((float) viewGroup.getMeasuredHeight());
            AnonymousClass3 anonymousClass3 = new Runnable() {
                public void run() {
                    if (BottomSheet.this.startAnimationRunnable == this && !BottomSheet.this.dismissed) {
                        BottomSheet.this.startAnimationRunnable = null;
                        BottomSheet.this.startOpenAnimation();
                    }
                }
            };
            this.startAnimationRunnable = anonymousClass3;
            AndroidUtilities.runOnUIThread(anonymousClass3, 150);
        } else {
            startOpenAnimation();
        }
    }

    public void setAllowDrawContent(boolean z) {
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            this.container.setBackgroundDrawable(this.allowDrawContent ? this.backDrawable : null);
            this.container.invalidate();
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return this.canDismissWithSwipe;
    }

    public void setCanDismissWithSwipe(boolean z) {
        this.canDismissWithSwipe = z;
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setTitle(CharSequence charSequence) {
        setTitle(charSequence, false);
    }

    public void setTitle(CharSequence charSequence, boolean z) {
        this.title = charSequence;
        this.bigTitle = z;
    }

    public void setApplyTopPadding(boolean z) {
        this.applyTopPadding = z;
    }

    public void setApplyBottomPadding(boolean z) {
        this.applyBottomPadding = z;
    }

    public TextView getTitleView() {
        return this.titleView;
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
            if (!onCustomOpenAnimation()) {
                if (VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
                    this.container.setLayerType(2, null);
                }
                ViewGroup viewGroup = this.containerView;
                viewGroup.setTranslationY((float) viewGroup.getMeasuredHeight());
                this.currentSheetAnimationType = 1;
                this.currentSheetAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.currentSheetAnimation;
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f});
                ColorDrawable colorDrawable = this.backDrawable;
                Property property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                int[] iArr = new int[1];
                iArr[0] = this.dimBehind ? 51 : 0;
                animatorArr[1] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
                animatorSet.playTogether(animatorArr);
                this.currentSheetAnimation.setDuration(400);
                this.currentSheetAnimation.setStartDelay(20);
                this.currentSheetAnimation.setInterpolator(this.openInterpolator);
                this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                            if (bottomSheet.delegate != null) {
                                BottomSheet.this.delegate.onOpenAnimationEnd();
                            }
                            if (BottomSheet.this.useHardwareLayer) {
                                BottomSheet.this.container.setLayerType(0, null);
                            }
                            bottomSheet = BottomSheet.this;
                            if (bottomSheet.isFullscreen) {
                                WindowManager.LayoutParams attributes = bottomSheet.getWindow().getAttributes();
                                attributes.flags &= -1025;
                                BottomSheet.this.getWindow().setAttributes(attributes);
                            }
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(512));
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                        }
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(512));
                this.currentSheetAnimation.start();
            }
        }
    }

    public void setDelegate(BottomSheetDelegateInterface bottomSheetDelegateInterface) {
        this.delegate = bottomSheetDelegateInterface;
    }

    public FrameLayout getContainer() {
        return this.container;
    }

    public ViewGroup getSheetContainer() {
        return this.containerView;
    }

    public int getTag() {
        return this.tag;
    }

    public void setDimBehind(boolean z) {
        this.dimBehind = z;
    }

    public void setItemText(int i, CharSequence charSequence) {
        if (i >= 0 && i < this.itemViews.size()) {
            ((BottomSheetCell) this.itemViews.get(i)).textView.setText(charSequence);
        }
    }

    public void setItemColor(int i, int i2, int i3) {
        if (i >= 0 && i < this.itemViews.size()) {
            BottomSheetCell bottomSheetCell = (BottomSheetCell) this.itemViews.get(i);
            bottomSheetCell.textView.setTextColor(i2);
            bottomSheetCell.imageView.setColorFilter(new PorterDuffColorFilter(i3, Mode.MULTIPLY));
        }
    }

    public void setItems(CharSequence[] charSequenceArr, int[] iArr, OnClickListener onClickListener) {
        this.items = charSequenceArr;
        this.itemIcons = iArr;
        this.onClickListener = onClickListener;
    }

    public void setTitleColor(int i) {
        TextView textView = this.titleView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void dismissWithButtonClick(final int i) {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            this.currentSheetAnimationType = 2;
            this.currentSheetAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.currentSheetAnimation;
            Animator[] animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
            animatorArr[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0});
            animatorSet.playTogether(animatorArr);
            this.currentSheetAnimation.setDuration(180);
            this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                    if (animatorSet != null && animatorSet.equals(animator)) {
                        BottomSheet bottomSheet = BottomSheet.this;
                        bottomSheet.currentSheetAnimation = null;
                        bottomSheet.currentSheetAnimationType = 0;
                        if (bottomSheet.onClickListener != null) {
                            BottomSheet.this.onClickListener.onClick(BottomSheet.this, i);
                        }
                        AndroidUtilities.runOnUIThread(new -$$Lambda$BottomSheet$5$CikgvDyZEWn0favL4ZqbmH9PuGE(this));
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(512));
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$BottomSheet$5() {
                    try {
                        super.dismiss();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                    if (animatorSet != null && animatorSet.equals(animator)) {
                        BottomSheet bottomSheet = BottomSheet.this;
                        bottomSheet.currentSheetAnimation = null;
                        bottomSheet.currentSheetAnimationType = 0;
                    }
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(512));
            this.currentSheetAnimation.start();
        }
    }

    public void dismiss() {
        BottomSheetDelegateInterface bottomSheetDelegateInterface = this.delegate;
        if ((bottomSheetDelegateInterface == null || bottomSheetDelegateInterface.canDismiss()) && !this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            if (!(this.allowCustomAnimation && onCustomCloseAnimation())) {
                this.currentSheetAnimationType = 2;
                this.currentSheetAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.currentSheetAnimation;
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
                animatorArr[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0});
                animatorSet.playTogether(animatorArr);
                if (this.useFastDismiss) {
                    float measuredHeight = (float) this.containerView.getMeasuredHeight();
                    this.currentSheetAnimation.setDuration((long) Math.max(60, (int) (((measuredHeight - this.containerView.getTranslationY()) * 180.0f) / measuredHeight)));
                    this.useFastDismiss = false;
                } else {
                    this.currentSheetAnimation.setDuration(180);
                }
                this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                            AndroidUtilities.runOnUIThread(new -$$Lambda$BottomSheet$6$VTgE-oeIT2bQ5t-sdXAKcokhgP8(this));
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(512));
                    }

                    public /* synthetic */ void lambda$onAnimationEnd$0$BottomSheet$6() {
                        try {
                            BottomSheet.this.dismissInternal();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                        }
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(512));
                this.currentSheetAnimation.start();
            }
        }
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* Access modifiers changed, original: protected */
    public int getLeftInset() {
        WindowInsets windowInsets = this.lastInsets;
        return (windowInsets == null || VERSION.SDK_INT < 21) ? 0 : windowInsets.getSystemWindowInsetLeft();
    }

    /* Access modifiers changed, original: protected */
    public int getRightInset() {
        WindowInsets windowInsets = this.lastInsets;
        return (windowInsets == null || VERSION.SDK_INT < 21) ? 0 : windowInsets.getSystemWindowInsetRight();
    }
}
