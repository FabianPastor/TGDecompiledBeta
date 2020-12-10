package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.SystemClock;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.ui.GroupCallActivity;

public class GroupCallPip implements NotificationCenter.NotificationCenterDelegate {
    private static boolean forceRemoved = true;
    /* access modifiers changed from: private */
    public static GroupCallPip instance;
    FrameLayout alertContainer;
    boolean animateToPrepareRemove;
    boolean animateToShowRemoveTooltip;
    AvatarsImageView avatarsImageView;
    /* access modifiers changed from: private */
    public final GroupCallPipButton button;
    boolean buttonInAlpha;
    int currentAccount;
    RLottieDrawable deleteIcon;
    private final RLottieImageView iconView;
    int lastScreenX;
    int lastScreenY;
    int[] location = new int[2];
    GroupCallPipAlertView pipAlertView;
    float[] point = new float[2];
    float prepareToRemoveProgress = 0.0f;
    boolean pressedState;
    boolean showAlert;
    AnimatorSet showRemoveAnimator;
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener updateXlistener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallPip groupCallPip = GroupCallPip.this;
            groupCallPip.windowLayoutParams.x = (int) floatValue;
            if (groupCallPip.windowView.getParent() != null) {
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                groupCallPip2.windowManager.updateViewLayout(groupCallPip2.windowView, groupCallPip2.windowLayoutParams);
            }
        }
    };
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener updateYlistener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallPip groupCallPip = GroupCallPip.this;
            groupCallPip.windowLayoutParams.y = (int) floatValue;
            if (groupCallPip.windowView.getParent() != null) {
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                groupCallPip2.windowManager.updateViewLayout(groupCallPip2.windowView, groupCallPip2.windowLayoutParams);
            }
        }
    };
    WindowManager.LayoutParams windowLayoutParams;
    WindowManager windowManager;
    FrameLayout windowRemoveTooltipOverlayView;
    View windowRemoveTooltipView;
    FrameLayout windowView;
    float xRelative = -1.0f;
    float yRelative = -1.0f;

    public GroupCallPip(Context context, int i) {
        this.currentAccount = i;
        final float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        AnonymousClass3 r0 = new FrameLayout(context) {
            AnimatorSet moveToBoundsAnimator;
            boolean moving;
            boolean pressed;
            Runnable pressedRunnable = new Runnable() {
                public void run() {
                    if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute()) {
                        VoIPService.getSharedInstance().setMicMute(false, true);
                        AnonymousClass3.this.performHapticFeedback(3, 2);
                        AnonymousClass3.this.pressed = true;
                    }
                }
            };
            float startX;
            float startY;
            float windowX;
            float windowY;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x;
                GroupCallPip groupCallPip = GroupCallPip.this;
                if (i3 != groupCallPip.lastScreenX || groupCallPip.lastScreenY != point.y) {
                    groupCallPip.lastScreenX = i3;
                    groupCallPip.lastScreenY = point.y;
                    if (groupCallPip.xRelative < 0.0f) {
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("groupcallpipconfig", 0);
                        GroupCallPip.this.xRelative = sharedPreferences.getFloat("relativeX", 0.5f);
                        GroupCallPip.this.yRelative = sharedPreferences.getFloat("relativeY", 0.5f);
                    }
                    GroupCallPip access$000 = GroupCallPip.instance;
                    GroupCallPip groupCallPip2 = GroupCallPip.this;
                    access$000.setPosition(groupCallPip2.xRelative, groupCallPip2.yRelative);
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:8:0x0021, code lost:
                if (r4 != 3) goto L_0x02ab;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouchEvent(android.view.MotionEvent r11) {
                /*
                    r10 = this;
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.instance
                    r1 = 0
                    if (r0 != 0) goto L_0x0008
                    return r1
                L_0x0008:
                    float r0 = r11.getRawX()
                    float r2 = r11.getRawY()
                    android.view.ViewParent r3 = r10.getParent()
                    int r4 = r11.getAction()
                    r5 = 1
                    if (r4 == 0) goto L_0x0287
                    r6 = 3
                    r7 = 2
                    if (r4 == r5) goto L_0x0118
                    if (r4 == r7) goto L_0x0025
                    if (r4 == r6) goto L_0x0118
                    goto L_0x02ab
                L_0x0025:
                    float r11 = r10.startX
                    float r11 = r0 - r11
                    float r4 = r10.startY
                    float r4 = r2 - r4
                    boolean r6 = r10.moving
                    r7 = 0
                    if (r6 != 0) goto L_0x005b
                    float r6 = r11 * r11
                    float r8 = r4 * r4
                    float r6 = r6 + r8
                    float r8 = r14
                    float r8 = r8 * r8
                    int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                    if (r6 <= 0) goto L_0x005b
                    if (r3 == 0) goto L_0x0044
                    r3.requestDisallowInterceptTouchEvent(r5)
                L_0x0044:
                    java.lang.Runnable r11 = r10.pressedRunnable
                    org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r11)
                    r10.moving = r5
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    r11.showRemoveTooltip(r5)
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    r11.showAlert(r1)
                    r10.startX = r0
                    r10.startY = r2
                    r11 = 0
                    r4 = 0
                L_0x005b:
                    boolean r3 = r10.moving
                    if (r3 == 0) goto L_0x02ab
                    float r3 = r10.windowX
                    float r3 = r3 + r11
                    r10.windowX = r3
                    float r11 = r10.windowY
                    float r11 = r11 + r4
                    r10.windowY = r11
                    r10.startX = r0
                    r10.startY = r2
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r0 = r0.windowLayoutParams
                    int r2 = (int) r3
                    r0.x = r2
                    int r11 = (int) r11
                    r0.y = r11
                    int r11 = r10.getMeasuredWidth()
                    float r11 = (float) r11
                    r0 = 1073741824(0x40000000, float:2.0)
                    float r11 = r11 / r0
                    float r3 = r3 + r11
                    float r11 = r10.windowY
                    int r2 = r10.getMeasuredHeight()
                    float r2 = (float) r2
                    float r2 = r2 / r0
                    float r11 = r11 + r2
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.View r4 = r2.windowRemoveTooltipView
                    int[] r2 = r2.location
                    r4.getLocationOnScreen(r2)
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    int[] r4 = r2.location
                    r4 = r4[r1]
                    float r4 = (float) r4
                    android.view.View r2 = r2.windowRemoveTooltipView
                    int r2 = r2.getMeasuredWidth()
                    float r2 = (float) r2
                    float r2 = r2 / r0
                    float r4 = r4 + r2
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    int[] r6 = r2.location
                    r6 = r6[r5]
                    float r6 = (float) r6
                    android.view.View r2 = r2.windowRemoveTooltipView
                    int r2 = r2.getMeasuredHeight()
                    float r2 = (float) r2
                    float r2 = r2 / r0
                    float r6 = r6 + r2
                    float r0 = r3 - r4
                    float r2 = r0 * r0
                    float r7 = r11 - r6
                    float r8 = r7 * r7
                    float r2 = r2 + r8
                    r8 = 1117782016(0x42a00000, float:80.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r9 = r9 * r8
                    float r8 = (float) r9
                    int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                    if (r2 >= 0) goto L_0x00cd
                    r1 = 1
                L_0x00cd:
                    float r0 = r0 / r7
                    double r7 = (double) r0
                    double r7 = java.lang.Math.atan(r7)
                    double r7 = java.lang.Math.toDegrees(r7)
                    int r0 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                    if (r0 <= 0) goto L_0x00df
                    int r0 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
                    if (r0 < 0) goto L_0x00e7
                L_0x00df:
                    int r0 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                    if (r0 >= 0) goto L_0x00ed
                    int r11 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
                    if (r11 >= 0) goto L_0x00ed
                L_0x00e7:
                    r2 = 4643457506423603200(0x4070eNUM, double:270.0)
                    goto L_0x00f2
                L_0x00ed:
                    r2 = 4636033603912859648(0xNUM, double:90.0)
                L_0x00f2:
                    double r2 = r2 - r7
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    org.telegram.ui.Components.GroupCallPipButton r11 = r11.button
                    r11.setRemoveAngle(r2)
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    r11.prepareToRemove(r1)
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    android.widget.FrameLayout r11 = r11.windowView
                    android.view.ViewParent r11 = r11.getParent()
                    if (r11 == 0) goto L_0x02ab
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager r0 = r11.windowManager
                    android.widget.FrameLayout r1 = r11.windowView
                    android.view.WindowManager$LayoutParams r11 = r11.windowLayoutParams
                    r0.updateViewLayout(r1, r11)
                    goto L_0x02ab
                L_0x0118:
                    java.lang.Runnable r0 = r10.pressedRunnable
                    org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    boolean r2 = r0.animateToPrepareRemove
                    if (r2 == 0) goto L_0x0129
                    r10.pressed = r1
                    r0.remove()
                    return r1
                L_0x0129:
                    r0.pressedState = r1
                    r0.checkButtonAlpha()
                    boolean r0 = r10.pressed
                    if (r0 == 0) goto L_0x0145
                    org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                    if (r11 == 0) goto L_0x0142
                    org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                    r11.setMicMute(r5, r1)
                    r10.performHapticFeedback(r6, r7)
                L_0x0142:
                    r10.pressed = r1
                    goto L_0x0153
                L_0x0145:
                    int r11 = r11.getAction()
                    if (r11 != r5) goto L_0x0153
                    boolean r11 = r10.moving
                    if (r11 != 0) goto L_0x0153
                    r10.onTap()
                    return r1
                L_0x0153:
                    if (r3 == 0) goto L_0x027f
                    r3.requestDisallowInterceptTouchEvent(r1)
                    android.graphics.Point r11 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r0 = r11.x
                    int r11 = r11.y
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                    int r2 = r2.x
                    float r2 = (float) r2
                    int r3 = r10.getMeasuredWidth()
                    float r3 = (float) r3
                    float r3 = r3 + r2
                    org.telegram.ui.Components.GroupCallPip r4 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r4 = r4.windowLayoutParams
                    int r4 = r4.y
                    float r4 = (float) r4
                    int r6 = r10.getMeasuredHeight()
                    float r6 = (float) r6
                    float r6 = r6 + r4
                    android.animation.AnimatorSet r8 = new android.animation.AnimatorSet
                    r8.<init>()
                    r10.moveToBoundsAnimator = r8
                    r8 = 1108344832(0x42100000, float:36.0)
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r8 = -r8
                    float r8 = (float) r8
                    int r9 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                    if (r9 >= 0) goto L_0x01b0
                    float[] r0 = new float[r7]
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                    int r2 = r2.x
                    float r2 = (float) r2
                    r0[r1] = r2
                    r0[r5] = r8
                    android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    android.animation.ValueAnimator$AnimatorUpdateListener r2 = r2.updateXlistener
                    r0.addUpdateListener(r2)
                    android.animation.AnimatorSet r2 = r10.moveToBoundsAnimator
                    android.animation.Animator[] r3 = new android.animation.Animator[r5]
                    r3[r1] = r0
                    r2.playTogether(r3)
                    r2 = r8
                    goto L_0x01e1
                L_0x01b0:
                    float r9 = (float) r0
                    float r9 = r9 - r8
                    int r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                    if (r3 <= 0) goto L_0x01e1
                    float[] r2 = new float[r7]
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r3 = r3.windowLayoutParams
                    int r3 = r3.x
                    float r3 = (float) r3
                    r2[r1] = r3
                    int r3 = r10.getMeasuredWidth()
                    int r0 = r0 - r3
                    float r0 = (float) r0
                    float r0 = r0 - r8
                    r2[r5] = r0
                    android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    android.animation.ValueAnimator$AnimatorUpdateListener r3 = r3.updateXlistener
                    r2.addUpdateListener(r3)
                    android.animation.AnimatorSet r3 = r10.moveToBoundsAnimator
                    android.animation.Animator[] r8 = new android.animation.Animator[r5]
                    r8[r1] = r2
                    r3.playTogether(r8)
                    r2 = r0
                L_0x01e1:
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r3 = (float) r0
                    int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
                    if (r3 >= 0) goto L_0x020d
                    float[] r11 = new float[r7]
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r3 = r3.windowLayoutParams
                    int r3 = r3.y
                    float r3 = (float) r3
                    r11[r1] = r3
                    float r4 = (float) r0
                    r11[r5] = r4
                    android.animation.ValueAnimator r11 = android.animation.ValueAnimator.ofFloat(r11)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    android.animation.ValueAnimator$AnimatorUpdateListener r0 = r0.updateYlistener
                    r11.addUpdateListener(r0)
                    android.animation.AnimatorSet r0 = r10.moveToBoundsAnimator
                    android.animation.Animator[] r3 = new android.animation.Animator[r5]
                    r3[r1] = r11
                    r0.playTogether(r3)
                    goto L_0x023b
                L_0x020d:
                    float r0 = (float) r11
                    int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
                    if (r0 <= 0) goto L_0x023b
                    float[] r0 = new float[r7]
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r3 = r3.windowLayoutParams
                    int r3 = r3.y
                    float r3 = (float) r3
                    r0[r1] = r3
                    int r3 = r10.getMeasuredHeight()
                    int r11 = r11 - r3
                    float r4 = (float) r11
                    r0[r5] = r4
                    android.animation.ValueAnimator r11 = android.animation.ValueAnimator.ofFloat(r0)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    android.animation.ValueAnimator$AnimatorUpdateListener r0 = r0.updateYlistener
                    r11.addUpdateListener(r0)
                    android.animation.AnimatorSet r0 = r10.moveToBoundsAnimator
                    android.animation.Animator[] r3 = new android.animation.Animator[r5]
                    r3[r1] = r11
                    r0.playTogether(r3)
                L_0x023b:
                    android.animation.AnimatorSet r11 = r10.moveToBoundsAnimator
                    r6 = 150(0x96, double:7.4E-322)
                    android.animation.AnimatorSet r11 = r11.setDuration(r6)
                    org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                    r11.setInterpolator(r0)
                    android.animation.AnimatorSet r11 = r10.moveToBoundsAnimator
                    r11.start()
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    float[] r0 = r11.point
                    r11.getRelativePosition(r2, r4, r0)
                    android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
                    java.lang.String r0 = "groupcallpipconfig"
                    android.content.SharedPreferences r11 = r11.getSharedPreferences(r0, r1)
                    android.content.SharedPreferences$Editor r11 = r11.edit()
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    float[] r2 = r0.point
                    r2 = r2[r1]
                    r0.xRelative = r2
                    java.lang.String r0 = "relativeX"
                    android.content.SharedPreferences$Editor r11 = r11.putFloat(r0, r2)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    float[] r2 = r0.point
                    r2 = r2[r5]
                    r0.yRelative = r2
                    java.lang.String r0 = "relativeY"
                    android.content.SharedPreferences$Editor r11 = r11.putFloat(r0, r2)
                    r11.apply()
                L_0x027f:
                    r10.moving = r1
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    r11.showRemoveTooltip(r1)
                    goto L_0x02ab
                L_0x0287:
                    r10.startX = r0
                    r10.startY = r2
                    java.lang.System.currentTimeMillis()
                    java.lang.Runnable r11 = r10.pressedRunnable
                    int r0 = android.view.ViewConfiguration.getTapTimeout()
                    long r0 = (long) r0
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r11, r0)
                    org.telegram.ui.Components.GroupCallPip r11 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r0 = r11.windowLayoutParams
                    int r1 = r0.x
                    float r1 = (float) r1
                    r10.windowX = r1
                    int r0 = r0.y
                    float r0 = (float) r0
                    r10.windowY = r0
                    r11.pressedState = r5
                    r11.checkButtonAlpha()
                L_0x02ab:
                    return r5
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPip.AnonymousClass3.onTouchEvent(android.view.MotionEvent):boolean");
            }

            private void onTap() {
                if (VoIPService.getSharedInstance() != null) {
                    GroupCallPip.this.showAlert(true);
                }
            }
        };
        this.windowView = r0;
        r0.setAlpha(0.7f);
        GroupCallPipButton groupCallPipButton = new GroupCallPipButton(context, this.currentAccount, false);
        this.button = groupCallPipButton;
        this.windowView.addView(groupCallPipButton, LayoutHelper.createFrame(-1, -1, 17));
        AvatarsImageView avatarsImageView2 = new AvatarsImageView(context);
        this.avatarsImageView = avatarsImageView2;
        avatarsImageView2.setStyle(5);
        this.avatarsImageView.setCentered(true);
        this.avatarsImageView.setVisibility(8);
        this.avatarsImageView.setDelegate(new Runnable() {
            public final void run() {
                GroupCallPip.this.lambda$new$0$GroupCallPip();
            }
        });
        updateAvatars(false);
        this.windowView.addView(this.avatarsImageView, LayoutHelper.createFrame(108, 36, 49));
        this.windowRemoveTooltipView = new View(context) {
            Paint paint = new Paint(1);

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                GroupCallPip groupCallPip = GroupCallPip.this;
                boolean z = groupCallPip.animateToPrepareRemove;
                if (z) {
                    float f = groupCallPip.prepareToRemoveProgress;
                    if (f != 1.0f) {
                        float f2 = f + 0.064f;
                        groupCallPip.prepareToRemoveProgress = f2;
                        if (f2 > 1.0f) {
                            groupCallPip.prepareToRemoveProgress = 1.0f;
                        }
                        invalidate();
                        this.paint.setColor(ColorUtils.blendARGB(NUM, NUM, GroupCallPip.this.prepareToRemoveProgress));
                        canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) AndroidUtilities.dp(35.0f)) + (((float) AndroidUtilities.dp(5.0f)) * GroupCallPip.this.prepareToRemoveProgress), this.paint);
                    }
                }
                if (!z) {
                    float f3 = groupCallPip.prepareToRemoveProgress;
                    if (f3 != 0.0f) {
                        float f4 = f3 - 0.064f;
                        groupCallPip.prepareToRemoveProgress = f4;
                        if (f4 < 0.0f) {
                            groupCallPip.prepareToRemoveProgress = 0.0f;
                        }
                        invalidate();
                    }
                }
                this.paint.setColor(ColorUtils.blendARGB(NUM, NUM, GroupCallPip.this.prepareToRemoveProgress));
                canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) AndroidUtilities.dp(35.0f)) + (((float) AndroidUtilities.dp(5.0f)) * GroupCallPip.this.prepareToRemoveProgress), this.paint);
            }

            public void setAlpha(float f) {
                super.setAlpha(f);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setAlpha(f);
            }

            public void setScaleX(float f) {
                super.setScaleX(f);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setScaleX(f);
            }

            public void setScaleY(float f) {
                super.setScaleY(f);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setScaleY(f);
            }

            public void setTranslationY(float f) {
                super.setTranslationY(f);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setTranslationY(f);
            }

            public void setVisibility(int i) {
                super.setVisibility(i);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setVisibility(i);
            }
        };
        this.windowRemoveTooltipOverlayView = new FrameLayout(context);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f), true, (int[]) null);
        this.deleteIcon = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        rLottieImageView.setAnimation(this.deleteIcon);
        rLottieImageView.setColorFilter(-1);
        this.windowRemoveTooltipOverlayView.addView(rLottieImageView, LayoutHelper.createFrame(40, 40, 17));
        AnonymousClass5 r14 = new FrameLayout(context) {
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 4 && keyEvent.getAction() == 1) {
                    GroupCallPip.this.showAlert(false);
                }
                return super.dispatchKeyEvent(keyEvent);
            }
        };
        this.alertContainer = r14;
        r14.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallPip.this.lambda$new$1$GroupCallPip(view);
            }
        });
        this.alertContainer.setClipChildren(false);
        FrameLayout frameLayout = this.alertContainer;
        GroupCallPipAlertView groupCallPipAlertView = new GroupCallPipAlertView(context, this.currentAccount);
        this.pipAlertView = groupCallPipAlertView;
        frameLayout.addView(groupCallPipAlertView, LayoutHelper.createFrame(-2, -2.0f));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupCallPip() {
        updateAvatars(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GroupCallPip(View view) {
        showAlert(false);
    }

    /* access modifiers changed from: private */
    public void showAlert(boolean z) {
        if (z != this.showAlert) {
            this.showAlert = z;
            this.alertContainer.animate().setListener((Animator.AnimatorListener) null).cancel();
            if (this.showAlert) {
                this.alertContainer.getLocationOnScreen(this.location);
                final float measuredWidth = (((float) this.windowLayoutParams.x) + (((float) this.button.getMeasuredWidth()) / 2.0f)) - ((float) this.location[0]);
                final float measuredWidth2 = (((float) this.windowLayoutParams.y) + (((float) this.button.getMeasuredWidth()) / 2.0f)) - ((float) this.location[1]);
                if (this.alertContainer.getVisibility() != 0) {
                    this.alertContainer.setVisibility(0);
                    this.alertContainer.setAlpha(0.0f);
                    this.pipAlertView.setScaleX(0.7f);
                    this.pipAlertView.setScaleY(0.7f);
                }
                this.alertContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        GroupCallPip.this.alertContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (measuredWidth + ((float) AndroidUtilities.dp(61.0f)) + ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()) < ((float) (GroupCallPip.this.alertContainer.getMeasuredWidth() - AndroidUtilities.dp(16.0f)))) {
                            GroupCallPip.this.pipAlertView.setTranslationX(measuredWidth + ((float) AndroidUtilities.dp(61.0f)));
                            float measuredHeight = measuredWidth2 / ((float) GroupCallPip.this.alertContainer.getMeasuredHeight());
                            float dp = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredHeight());
                            float max = Math.max(dp, Math.min(measuredHeight, 1.0f - dp));
                            GroupCallPipAlertView groupCallPipAlertView = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView.setTranslationY((float) ((int) (measuredWidth2 - (((float) groupCallPipAlertView.getMeasuredHeight()) * max))));
                            GroupCallPip.this.pipAlertView.setPosition(0, measuredWidth, measuredWidth2);
                        } else if ((measuredWidth - ((float) AndroidUtilities.dp(61.0f))) - ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()) > ((float) AndroidUtilities.dp(16.0f))) {
                            float measuredHeight2 = measuredWidth2 / ((float) GroupCallPip.this.alertContainer.getMeasuredHeight());
                            float dp2 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredHeight());
                            float max2 = Math.max(dp2, Math.min(measuredHeight2, 1.0f - dp2));
                            GroupCallPip.this.pipAlertView.setTranslationX((float) ((int) ((measuredWidth - ((float) AndroidUtilities.dp(61.0f))) - ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()))));
                            GroupCallPipAlertView groupCallPipAlertView2 = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView2.setTranslationY((float) ((int) (measuredWidth2 - (((float) groupCallPipAlertView2.getMeasuredHeight()) * max2))));
                            GroupCallPip.this.pipAlertView.setPosition(1, measuredWidth, measuredWidth2);
                        } else if (measuredWidth2 > ((float) GroupCallPip.this.alertContainer.getMeasuredHeight()) * 0.3f) {
                            float measuredWidth = measuredWidth / ((float) GroupCallPip.this.alertContainer.getMeasuredWidth());
                            float dp3 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth());
                            float max3 = Math.max(dp3, Math.min(measuredWidth, 1.0f - dp3));
                            GroupCallPipAlertView groupCallPipAlertView3 = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView3.setTranslationX((float) ((int) (measuredWidth - (((float) groupCallPipAlertView3.getMeasuredWidth()) * max3))));
                            GroupCallPipAlertView groupCallPipAlertView4 = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView4.setTranslationY((float) ((int) ((measuredWidth2 - ((float) groupCallPipAlertView4.getMeasuredHeight())) - ((float) AndroidUtilities.dp(61.0f)))));
                            GroupCallPip.this.pipAlertView.setPosition(3, measuredWidth, measuredWidth2);
                        } else {
                            float measuredWidth2 = measuredWidth / ((float) GroupCallPip.this.alertContainer.getMeasuredWidth());
                            float dp4 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth());
                            float max4 = Math.max(dp4, Math.min(measuredWidth2, 1.0f - dp4));
                            GroupCallPipAlertView groupCallPipAlertView5 = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView5.setTranslationX((float) ((int) (measuredWidth - (((float) groupCallPipAlertView5.getMeasuredWidth()) * max4))));
                            GroupCallPip.this.pipAlertView.setTranslationY((float) ((int) (measuredWidth2 + ((float) AndroidUtilities.dp(61.0f)))));
                            GroupCallPip.this.pipAlertView.setPosition(2, measuredWidth, measuredWidth2);
                        }
                        return false;
                    }
                });
                this.alertContainer.animate().alpha(1.0f).setDuration(150).start();
                this.pipAlertView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
            } else {
                this.pipAlertView.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).start();
                this.alertContainer.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        GroupCallPip.this.alertContainer.setVisibility(8);
                    }
                }).start();
            }
        }
        checkButtonAlpha();
    }

    /* access modifiers changed from: private */
    public void checkButtonAlpha() {
        boolean z = this.pressedState || this.showAlert;
        if (this.buttonInAlpha != z) {
            this.buttonInAlpha = z;
            if (z) {
                this.windowView.animate().alpha(1.0f).start();
            } else {
                this.windowView.animate().alpha(0.7f).start();
            }
            this.button.setPressedState(z);
        }
    }

    /* access modifiers changed from: private */
    public void remove() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip != null) {
            forceRemoved = true;
            groupCallPip.showAlert(false);
            float measuredWidth = ((float) this.windowLayoutParams.x) + (((float) this.windowView.getMeasuredWidth()) / 2.0f);
            float measuredHeight = ((float) this.windowLayoutParams.y) + (((float) this.windowView.getMeasuredHeight()) / 2.0f);
            this.windowRemoveTooltipView.getLocationOnScreen(this.location);
            float measuredWidth2 = (((float) this.location[0]) + (((float) this.windowRemoveTooltipView.getMeasuredWidth()) / 2.0f)) - measuredWidth;
            float measuredHeight2 = (((float) this.location[1]) + (((float) this.windowRemoveTooltipView.getMeasuredHeight()) / 2.0f)) - measuredHeight;
            GroupCallPip groupCallPip2 = instance;
            final WindowManager windowManager2 = groupCallPip2.windowManager;
            FrameLayout frameLayout = groupCallPip2.windowView;
            View view = groupCallPip2.windowRemoveTooltipView;
            FrameLayout frameLayout2 = groupCallPip2.windowRemoveTooltipOverlayView;
            FrameLayout frameLayout3 = groupCallPip2.alertContainer;
            onDestroy();
            instance = null;
            AnimatorSet animatorSet = new AnimatorSet();
            int i = this.windowLayoutParams.x;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{(float) i, ((float) i) + measuredWidth2});
            ofFloat.addUpdateListener(this.updateXlistener);
            ValueAnimator duration = ofFloat.setDuration(250);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator);
            animatorSet.playTogether(new Animator[]{ofFloat});
            int i2 = this.windowLayoutParams.y;
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{(float) i2, (((float) i2) + measuredHeight2) - ((float) AndroidUtilities.dp(30.0f)), ((float) this.windowLayoutParams.y) + measuredHeight2});
            ofFloat2.addUpdateListener(this.updateYlistener);
            ofFloat2.setDuration(250).setInterpolator(cubicBezierInterpolator);
            animatorSet.playTogether(new Animator[]{ofFloat2});
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(frameLayout, View.SCALE_X, new float[]{1.0f, 0.1f}).setDuration(180)});
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(frameLayout, View.SCALE_Y, new float[]{1.0f, 0.1f}).setDuration(180)});
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(frameLayout, View.ALPHA, new float[]{1.0f, 0.0f});
            float f = (float) 350;
            ofFloat3.setStartDelay((long) (0.7f * f));
            ofFloat3.setDuration((long) (f * 0.3f));
            animatorSet.playTogether(new Animator[]{ofFloat3});
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.0f, 1.05f});
            ofFloat4.setDuration(530);
            CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.EASE_BOTH;
            ofFloat4.setInterpolator(cubicBezierInterpolator2);
            animatorSet.playTogether(new Animator[]{ofFloat4});
            ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.0f, 1.05f});
            ofFloat5.setDuration(530);
            ofFloat5.setInterpolator(cubicBezierInterpolator2);
            animatorSet.playTogether(new Animator[]{ofFloat5});
            ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.0f, 0.3f});
            ofFloat6.setStartDelay(530);
            ofFloat6.setDuration(350);
            CubicBezierInterpolator cubicBezierInterpolator3 = CubicBezierInterpolator.EASE_OUT_QUINT;
            ofFloat6.setInterpolator(cubicBezierInterpolator3);
            animatorSet.playTogether(new Animator[]{ofFloat6});
            ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.0f, 0.3f});
            ofFloat7.setStartDelay(530);
            ofFloat7.setDuration(350);
            ofFloat7.setInterpolator(cubicBezierInterpolator3);
            animatorSet.playTogether(new Animator[]{ofFloat7});
            ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(60.0f)});
            ofFloat8.setStartDelay(530);
            ofFloat8.setDuration(350);
            ofFloat8.setInterpolator(cubicBezierInterpolator3);
            animatorSet.playTogether(new Animator[]{ofFloat8});
            ObjectAnimator ofFloat9 = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f});
            ofFloat9.setStartDelay(530);
            ofFloat9.setDuration(350);
            ofFloat9.setInterpolator(cubicBezierInterpolator3);
            animatorSet.playTogether(new Animator[]{ofFloat9});
            final FrameLayout frameLayout4 = frameLayout;
            final View view2 = view;
            final FrameLayout frameLayout5 = frameLayout2;
            final FrameLayout frameLayout6 = frameLayout3;
            animatorSet.addListener(new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(Animator animator) {
                    frameLayout4.setVisibility(8);
                    view2.setVisibility(8);
                    windowManager2.removeView(frameLayout4);
                    windowManager2.removeView(view2);
                    windowManager2.removeView(frameLayout5);
                    windowManager2.removeView(frameLayout6);
                }
            });
            animatorSet.start();
            this.deleteIcon.setCustomEndFrame(66);
            this.iconView.playAnimation();
        }
    }

    private void updateAvatars(boolean z) {
        AvatarsImageView avatarsImageView2 = this.avatarsImageView;
        if (avatarsImageView2.transitionProgressAnimator == null) {
            ChatObject.Call call = VoIPService.getSharedInstance() != null ? VoIPService.getSharedInstance().groupCall : null;
            int i = 0;
            if (call != null) {
                int size = call.sortedParticipants.size();
                int i2 = 0;
                while (i < 3) {
                    if (i2 < size) {
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call.sortedParticipants.get(i2);
                        if (tLRPC$TL_groupCallParticipant.user_id != UserConfig.getInstance(this.currentAccount).clientUserId && SystemClock.uptimeMillis() - tLRPC$TL_groupCallParticipant.lastSpeakTime <= 500) {
                            this.avatarsImageView.setObject(i, this.currentAccount, tLRPC$TL_groupCallParticipant);
                        }
                        i2++;
                    } else {
                        this.avatarsImageView.setObject(i, this.currentAccount, (TLObject) null);
                    }
                    i++;
                    i2++;
                }
                this.avatarsImageView.commitTransition(z);
                return;
            }
            while (i < 3) {
                this.avatarsImageView.setObject(i, this.currentAccount, (TLObject) null);
                i++;
            }
            this.avatarsImageView.commitTransition(z);
            return;
        }
        avatarsImageView2.updateAfterTransitionEnd();
    }

    public static void show(Context context, int i) {
        if (instance == null) {
            instance = new GroupCallPip(context, i);
            WindowManager windowManager2 = (WindowManager) context.getSystemService("window");
            instance.windowManager = windowManager2;
            WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams(context);
            createWindowLayoutParams.width = -1;
            createWindowLayoutParams.height = -1;
            createWindowLayoutParams.dimAmount = 0.25f;
            createWindowLayoutParams.flags = 258;
            windowManager2.addView(instance.alertContainer, createWindowLayoutParams);
            instance.alertContainer.setVisibility(8);
            WindowManager.LayoutParams createWindowLayoutParams2 = createWindowLayoutParams(context);
            createWindowLayoutParams2.gravity = 80;
            createWindowLayoutParams2.width = -1;
            createWindowLayoutParams2.height = AndroidUtilities.dp(200.0f);
            windowManager2.addView(instance.windowRemoveTooltipView, createWindowLayoutParams2);
            WindowManager.LayoutParams createWindowLayoutParams3 = createWindowLayoutParams(context);
            GroupCallPip groupCallPip = instance;
            groupCallPip.windowLayoutParams = createWindowLayoutParams3;
            windowManager2.addView(groupCallPip.windowView, createWindowLayoutParams3);
            WindowManager.LayoutParams createWindowLayoutParams4 = createWindowLayoutParams(context);
            createWindowLayoutParams4.gravity = 80;
            createWindowLayoutParams4.width = -1;
            createWindowLayoutParams4.height = AndroidUtilities.dp(200.0f);
            windowManager2.addView(instance.windowRemoveTooltipOverlayView, createWindowLayoutParams4);
            instance.windowRemoveTooltipView.setVisibility(8);
            instance.windowView.setScaleX(0.5f);
            instance.windowView.setScaleY(0.5f);
            instance.windowView.setAlpha(0.0f);
            instance.windowView.animate().alpha(0.7f).scaleY(1.0f).scaleX(1.0f).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
            NotificationCenter.getInstance(instance.currentAccount).addObserver(instance, NotificationCenter.groupCallUpdated);
            NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.didEndCall);
        }
    }

    private void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
        NotificationCenter.getGlobalInstance().removeObserver(instance, NotificationCenter.groupCallVisibilityChanged);
        NotificationCenter.getGlobalInstance().removeObserver(instance, NotificationCenter.didEndCall);
    }

    /* access modifiers changed from: private */
    public void setPosition(float f, float f2) {
        float f3 = (float) (-AndroidUtilities.dp(36.0f));
        float f4 = ((float) AndroidUtilities.displaySize.x) - (2.0f * f3);
        this.windowLayoutParams.x = (int) (f3 + ((f4 - ((float) AndroidUtilities.dp(117.0f))) * f));
        this.windowLayoutParams.y = (int) (((float) (AndroidUtilities.displaySize.y - AndroidUtilities.dp(117.0f))) * f2);
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public static void finish() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip != null) {
            groupCallPip.showAlert(false);
            GroupCallPip groupCallPip2 = instance;
            final WindowManager windowManager2 = groupCallPip2.windowManager;
            final FrameLayout frameLayout = groupCallPip2.windowView;
            final View view = groupCallPip2.windowRemoveTooltipView;
            final FrameLayout frameLayout2 = groupCallPip2.windowRemoveTooltipOverlayView;
            final FrameLayout frameLayout3 = groupCallPip2.alertContainer;
            frameLayout.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (frameLayout.getParent() != null) {
                        frameLayout.setVisibility(8);
                        view.setVisibility(8);
                        frameLayout2.setVisibility(8);
                        windowManager2.removeView(frameLayout);
                        windowManager2.removeView(view);
                        windowManager2.removeView(frameLayout2);
                        windowManager2.removeView(frameLayout3);
                    }
                }
            }).start();
            instance.onDestroy();
            instance = null;
        }
    }

    private static WindowManager.LayoutParams createWindowLayoutParams(Context context) {
        int i = Build.VERSION.SDK_INT;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = AndroidUtilities.dp(117.0f);
        layoutParams.width = AndroidUtilities.dp(117.0f);
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        if (!AndroidUtilities.checkInlinePermissions(context)) {
            layoutParams.type = 99;
        } else if (i >= 26) {
            layoutParams.type = 2038;
        } else {
            layoutParams.type = 2003;
        }
        layoutParams.flags = 776;
        if (i >= 21) {
            layoutParams.flags = 776 | Integer.MIN_VALUE;
        }
        return layoutParams;
    }

    /* access modifiers changed from: package-private */
    public void showRemoveTooltip(boolean z) {
        if (this.animateToShowRemoveTooltip != z) {
            this.animateToShowRemoveTooltip = z;
            AnimatorSet animatorSet = this.showRemoveAnimator;
            if (animatorSet != null) {
                animatorSet.removeAllListeners();
                this.showRemoveAnimator.cancel();
            }
            if (z) {
                if (this.windowRemoveTooltipView.getVisibility() != 0) {
                    this.windowRemoveTooltipView.setVisibility(0);
                    this.windowRemoveTooltipView.setAlpha(0.0f);
                    this.windowRemoveTooltipView.setScaleX(0.5f);
                    this.windowRemoveTooltipView.setScaleY(0.5f);
                    this.deleteIcon.setCurrentFrame(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.showRemoveAnimator = animatorSet2;
                View view = this.windowRemoveTooltipView;
                Property property = View.ALPHA;
                float[] fArr = {view.getAlpha(), 1.0f};
                View view2 = this.windowRemoveTooltipView;
                Property property2 = View.SCALE_X;
                float[] fArr2 = {view2.getScaleX(), 1.0f};
                View view3 = this.windowRemoveTooltipView;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, property, fArr), ObjectAnimator.ofFloat(view2, property2, fArr2), ObjectAnimator.ofFloat(view3, View.SCALE_Y, new float[]{view3.getScaleY(), 1.0f})});
                this.showRemoveAnimator.setDuration(150).start();
                return;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.showRemoveAnimator = animatorSet3;
            View view4 = this.windowRemoveTooltipView;
            Property property3 = View.ALPHA;
            float[] fArr3 = {view4.getAlpha(), 0.0f};
            View view5 = this.windowRemoveTooltipView;
            Property property4 = View.SCALE_X;
            float[] fArr4 = {view5.getScaleX(), 0.5f};
            View view6 = this.windowRemoveTooltipView;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(view4, property3, fArr3), ObjectAnimator.ofFloat(view5, property4, fArr4), ObjectAnimator.ofFloat(view6, View.SCALE_Y, new float[]{view6.getScaleY(), 0.5f})});
            this.showRemoveAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    GroupCallPip.this.windowRemoveTooltipView.setVisibility(8);
                    GroupCallPip groupCallPip = GroupCallPip.this;
                    groupCallPip.animateToPrepareRemove = false;
                    groupCallPip.prepareToRemoveProgress = 0.0f;
                }
            });
            this.showRemoveAnimator.setDuration(150);
            this.showRemoveAnimator.start();
        }
    }

    /* access modifiers changed from: package-private */
    public void prepareToRemove(boolean z) {
        if (this.animateToPrepareRemove != z) {
            this.animateToPrepareRemove = z;
            this.windowRemoveTooltipView.invalidate();
            this.deleteIcon.setCustomEndFrame(z ? 33 : 0);
            this.iconView.playAnimation();
        }
        this.button.prepareToRemove(z);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.groupCallUpdated || i == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
            updateAvatars(true);
        } else if (i == NotificationCenter.didEndCall) {
            updateVisibility(ApplicationLoader.applicationContext);
        }
    }

    /* access modifiers changed from: private */
    public void getRelativePosition(float f, float f2, float[] fArr) {
        Point point2 = AndroidUtilities.displaySize;
        fArr[0] = f / (((float) point2.x) - ((float) AndroidUtilities.dp(117.0f)));
        fArr[1] = f2 / (((float) point2.y) - ((float) AndroidUtilities.dp(117.0f)));
        fArr[0] = Math.min(1.0f, Math.max(0.0f, fArr[0]));
        fArr[1] = Math.min(1.0f, Math.max(0.0f, fArr[1]));
    }

    public static void updateVisibility(Context context) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z = true;
        if (!((sharedInstance == null || sharedInstance.groupCall == null) ? false : true) || forceRemoved || ((!ApplicationLoader.mainInterfaceStopped || !AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) && GroupCallActivity.groupCallUiVisible)) {
            z = false;
        }
        if (z) {
            show(context, sharedInstance.getAccount());
            instance.showAvatars(ApplicationLoader.mainInterfaceStopped);
            return;
        }
        finish();
    }

    private void showAvatars(boolean z) {
        if (z != (this.avatarsImageView.getTag() != null)) {
            int i = null;
            this.avatarsImageView.animate().setListener((Animator.AnimatorListener) null).cancel();
            if (z) {
                if (this.avatarsImageView.getVisibility() != 0) {
                    this.avatarsImageView.setVisibility(0);
                    this.avatarsImageView.setAlpha(0.0f);
                    this.avatarsImageView.setScaleX(0.5f);
                    this.avatarsImageView.setScaleY(0.5f);
                }
                this.avatarsImageView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
            } else {
                this.avatarsImageView.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        GroupCallPip.this.avatarsImageView.setVisibility(8);
                    }
                }).start();
            }
            AvatarsImageView avatarsImageView2 = this.avatarsImageView;
            if (z) {
                i = 1;
            }
            avatarsImageView2.setTag(i);
        }
    }

    public static void clearForce() {
        forceRemoved = false;
    }
}
