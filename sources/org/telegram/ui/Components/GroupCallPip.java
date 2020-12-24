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
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.GroupCallActivity;

public class GroupCallPip implements NotificationCenter.NotificationCenterDelegate {
    private static boolean forceRemoved = true;
    /* access modifiers changed from: private */
    public static GroupCallPip instance;
    FrameLayout alertContainer;
    boolean animateToPinnedToCenter = false;
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
    boolean moving;
    ValueAnimator pinAnimator;
    float pinnedProgress = 0.0f;
    GroupCallPipAlertView pipAlertView;
    float[] point = new float[2];
    float prepareToRemoveProgress = 0.0f;
    boolean pressedState;
    View removeTooltipView;
    boolean removed;
    boolean showAlert;
    AnimatorSet showRemoveAnimator;
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener updateXlistener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallPip groupCallPip = GroupCallPip.this;
            groupCallPip.windowLayoutParams.x = (int) floatValue;
            groupCallPip.updateAvatarsPosition();
            if (GroupCallPip.this.windowView.getParent() != null) {
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
    int windowLeft;
    WindowManager windowManager;
    float windowOffsetLeft;
    float windowOffsetTop;
    FrameLayout windowRemoveTooltipOverlayView;
    FrameLayout windowRemoveTooltipView;
    int windowTop;
    FrameLayout windowView;
    float windowX;
    float windowY;
    float xRelative = -1.0f;
    float yRelative = -1.0f;

    public GroupCallPip(Context context, int i) {
        this.currentAccount = i;
        final float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        AnonymousClass3 r0 = new FrameLayout(context) {
            Runnable micRunnable = $$Lambda$GroupCallPip$3$Tfg_uc9uCyCLASSNAMEHbGaVoTLHS8RO0.INSTANCE;
            AnimatorSet moveToBoundsAnimator;
            boolean pressed;
            Runnable pressedRunnable = new Runnable() {
                public void run() {
                    if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute()) {
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = VoIPService.getSharedInstance().groupCall.participants.get(UserConfig.getInstance(GroupCallPip.this.currentAccount).getClientUserId());
                        if (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || ChatObject.canManageCalls(VoIPService.getSharedInstance().getChat())) {
                            AndroidUtilities.runOnUIThread(AnonymousClass3.this.micRunnable, 90);
                            AnonymousClass3.this.performHapticFeedback(3, 2);
                            AnonymousClass3.this.pressed = true;
                        }
                    }
                }
            };
            float startX;
            float startY;

            static /* synthetic */ void lambda$$0() {
                if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute()) {
                    VoIPService.getSharedInstance().setMicMute(false, true, false);
                }
            }

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
                        GroupCallPip.this.xRelative = sharedPreferences.getFloat("relativeX", 1.0f);
                        GroupCallPip.this.yRelative = sharedPreferences.getFloat("relativeY", 0.4f);
                    }
                    if (GroupCallPip.instance != null) {
                        GroupCallPip access$100 = GroupCallPip.instance;
                        GroupCallPip groupCallPip2 = GroupCallPip.this;
                        access$100.setPosition(groupCallPip2.xRelative, groupCallPip2.yRelative);
                    }
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:8:0x0022, code lost:
                if (r4 != 3) goto L_0x02ff;
             */
            /* JADX WARNING: Removed duplicated region for block: B:19:0x0064  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouchEvent(android.view.MotionEvent r13) {
                /*
                    r12 = this;
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.instance
                    r1 = 0
                    if (r0 != 0) goto L_0x0008
                    return r1
                L_0x0008:
                    float r0 = r13.getRawX()
                    float r2 = r13.getRawY()
                    android.view.ViewParent r3 = r12.getParent()
                    int r4 = r13.getAction()
                    r5 = 1
                    if (r4 == 0) goto L_0x02c1
                    r6 = 3
                    r7 = 0
                    r8 = 2
                    if (r4 == r5) goto L_0x011b
                    if (r4 == r8) goto L_0x0026
                    if (r4 == r6) goto L_0x011b
                    goto L_0x02ff
                L_0x0026:
                    float r13 = r12.startX
                    float r13 = r0 - r13
                    float r4 = r12.startY
                    float r4 = r2 - r4
                    org.telegram.ui.Components.GroupCallPip r6 = org.telegram.ui.Components.GroupCallPip.this
                    boolean r6 = r6.moving
                    if (r6 != 0) goto L_0x005d
                    float r6 = r13 * r13
                    float r8 = r4 * r4
                    float r6 = r6 + r8
                    float r8 = r13
                    float r8 = r8 * r8
                    int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                    if (r6 <= 0) goto L_0x005d
                    if (r3 == 0) goto L_0x0046
                    r3.requestDisallowInterceptTouchEvent(r5)
                L_0x0046:
                    java.lang.Runnable r13 = r12.pressedRunnable
                    org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13)
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    r13.moving = r5
                    r13.showRemoveTooltip(r5)
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    r13.showAlert(r1)
                    r12.startX = r0
                    r12.startY = r2
                    r4 = 0
                    goto L_0x005e
                L_0x005d:
                    r7 = r13
                L_0x005e:
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    boolean r3 = r13.moving
                    if (r3 == 0) goto L_0x02ff
                    float r3 = r13.windowX
                    float r3 = r3 + r7
                    r13.windowX = r3
                    float r3 = r13.windowY
                    float r3 = r3 + r4
                    r13.windowY = r3
                    r12.startX = r0
                    r12.startY = r2
                    r13.updateButtonPosition()
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    float r13 = r13.windowX
                    int r0 = r12.getMeasuredWidth()
                    float r0 = (float) r0
                    r2 = 1073741824(0x40000000, float:2.0)
                    float r0 = r0 / r2
                    float r13 = r13 + r0
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    float r0 = r0.windowY
                    int r3 = r12.getMeasuredHeight()
                    float r3 = (float) r3
                    float r3 = r3 / r2
                    float r0 = r0 + r3
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    int r4 = r3.windowLeft
                    float r4 = (float) r4
                    float r6 = r3.windowOffsetLeft
                    float r4 = r4 - r6
                    android.widget.FrameLayout r3 = r3.windowRemoveTooltipView
                    int r3 = r3.getMeasuredWidth()
                    float r3 = (float) r3
                    float r3 = r3 / r2
                    float r4 = r4 + r3
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    int r6 = r3.windowTop
                    float r6 = (float) r6
                    float r7 = r3.windowOffsetTop
                    float r6 = r6 - r7
                    android.widget.FrameLayout r3 = r3.windowRemoveTooltipView
                    int r3 = r3.getMeasuredHeight()
                    float r3 = (float) r3
                    float r3 = r3 / r2
                    float r6 = r6 + r3
                    float r2 = r13 - r4
                    float r3 = r2 * r2
                    float r7 = r0 - r6
                    float r8 = r7 * r7
                    float r3 = r3 + r8
                    r8 = 1117782016(0x42a00000, float:80.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r9 = r9 * r8
                    float r8 = (float) r9
                    int r8 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                    if (r8 >= 0) goto L_0x010e
                    float r2 = r2 / r7
                    double r7 = (double) r2
                    double r7 = java.lang.Math.atan(r7)
                    double r7 = java.lang.Math.toDegrees(r7)
                    int r2 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
                    if (r2 <= 0) goto L_0x00db
                    int r2 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                    if (r2 < 0) goto L_0x00e3
                L_0x00db:
                    int r13 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
                    if (r13 >= 0) goto L_0x00e9
                    int r13 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                    if (r13 >= 0) goto L_0x00e9
                L_0x00e3:
                    r9 = 4643457506423603200(0x4070eNUM, double:270.0)
                    goto L_0x00ee
                L_0x00e9:
                    r9 = 4636033603912859648(0xNUM, double:90.0)
                L_0x00ee:
                    double r9 = r9 - r7
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    org.telegram.ui.Components.GroupCallPipButton r13 = r13.button
                    r13.setRemoveAngle(r9)
                    r13 = 1112014848(0x42480000, float:50.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
                    int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                    int r0 = r0 * r13
                    float r13 = (float) r0
                    int r13 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
                    if (r13 >= 0) goto L_0x010c
                    r13 = 1
                    r1 = 1
                    goto L_0x010f
                L_0x010c:
                    r13 = 1
                    goto L_0x010f
                L_0x010e:
                    r13 = 0
                L_0x010f:
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    r0.pinnedToCenter(r1)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    r0.prepareToRemove(r13)
                    goto L_0x02ff
                L_0x011b:
                    java.lang.Runnable r0 = r12.micRunnable
                    org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
                    java.lang.Runnable r0 = r12.pressedRunnable
                    org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    boolean r2 = r0.animateToPrepareRemove
                    if (r2 == 0) goto L_0x0144
                    boolean r13 = r12.pressed
                    if (r13 == 0) goto L_0x013c
                    org.telegram.messenger.voip.VoIPService r13 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                    if (r13 == 0) goto L_0x013c
                    org.telegram.messenger.voip.VoIPService r13 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                    r13.setMicMute(r5, r1, r1)
                L_0x013c:
                    r12.pressed = r1
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    r13.remove()
                    return r1
                L_0x0144:
                    r0.pressedState = r1
                    r0.checkButtonAlpha()
                    boolean r0 = r12.pressed
                    if (r0 == 0) goto L_0x0160
                    org.telegram.messenger.voip.VoIPService r13 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                    if (r13 == 0) goto L_0x015d
                    org.telegram.messenger.voip.VoIPService r13 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                    r13.setMicMute(r5, r1, r1)
                    r12.performHapticFeedback(r6, r8)
                L_0x015d:
                    r12.pressed = r1
                    goto L_0x0170
                L_0x0160:
                    int r13 = r13.getAction()
                    if (r13 != r5) goto L_0x0170
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    boolean r13 = r13.moving
                    if (r13 != 0) goto L_0x0170
                    r12.onTap()
                    return r1
                L_0x0170:
                    if (r3 == 0) goto L_0x02b9
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    boolean r13 = r13.moving
                    if (r13 == 0) goto L_0x02b9
                    r3.requestDisallowInterceptTouchEvent(r1)
                    android.graphics.Point r13 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r0 = r13.x
                    int r13 = r13.y
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                    int r2 = r2.x
                    float r2 = (float) r2
                    int r3 = r12.getMeasuredWidth()
                    float r3 = (float) r3
                    float r3 = r3 + r2
                    org.telegram.ui.Components.GroupCallPip r4 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r4 = r4.windowLayoutParams
                    int r4 = r4.y
                    float r4 = (float) r4
                    int r6 = r12.getMeasuredHeight()
                    float r6 = (float) r6
                    float r6 = r6 + r4
                    android.animation.AnimatorSet r9 = new android.animation.AnimatorSet
                    r9.<init>()
                    r12.moveToBoundsAnimator = r9
                    r9 = 1108344832(0x42100000, float:36.0)
                    int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r10 = -r10
                    float r10 = (float) r10
                    int r11 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
                    if (r11 >= 0) goto L_0x01d3
                    float[] r0 = new float[r8]
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                    int r2 = r2.x
                    float r2 = (float) r2
                    r0[r1] = r2
                    r0[r5] = r10
                    android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                    org.telegram.ui.Components.GroupCallPip r2 = org.telegram.ui.Components.GroupCallPip.this
                    android.animation.ValueAnimator$AnimatorUpdateListener r2 = r2.updateXlistener
                    r0.addUpdateListener(r2)
                    android.animation.AnimatorSet r2 = r12.moveToBoundsAnimator
                    android.animation.Animator[] r3 = new android.animation.Animator[r5]
                    r3[r1] = r0
                    r2.playTogether(r3)
                    r2 = r10
                    goto L_0x0204
                L_0x01d3:
                    float r11 = (float) r0
                    float r11 = r11 - r10
                    int r3 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
                    if (r3 <= 0) goto L_0x0204
                    float[] r2 = new float[r8]
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r3 = r3.windowLayoutParams
                    int r3 = r3.x
                    float r3 = (float) r3
                    r2[r1] = r3
                    int r3 = r12.getMeasuredWidth()
                    int r0 = r0 - r3
                    float r0 = (float) r0
                    float r0 = r0 - r10
                    r2[r5] = r0
                    android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    android.animation.ValueAnimator$AnimatorUpdateListener r3 = r3.updateXlistener
                    r2.addUpdateListener(r3)
                    android.animation.AnimatorSet r3 = r12.moveToBoundsAnimator
                    android.animation.Animator[] r10 = new android.animation.Animator[r5]
                    r10[r1] = r2
                    r3.playTogether(r10)
                    r2 = r0
                L_0x0204:
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r13 = r13 + r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r0 = r0 - r3
                    float r0 = (float) r0
                    int r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
                    if (r0 >= 0) goto L_0x0241
                    float[] r13 = new float[r8]
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r0 = r0.windowLayoutParams
                    int r0 = r0.y
                    float r0 = (float) r0
                    r13[r1] = r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r0 = r0 - r3
                    float r4 = (float) r0
                    r13[r5] = r4
                    android.animation.ValueAnimator r13 = android.animation.ValueAnimator.ofFloat(r13)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    android.animation.ValueAnimator$AnimatorUpdateListener r0 = r0.updateYlistener
                    r13.addUpdateListener(r0)
                    android.animation.AnimatorSet r0 = r12.moveToBoundsAnimator
                    android.animation.Animator[] r3 = new android.animation.Animator[r5]
                    r3[r1] = r13
                    r0.playTogether(r3)
                    goto L_0x026f
                L_0x0241:
                    float r0 = (float) r13
                    int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
                    if (r0 <= 0) goto L_0x026f
                    float[] r0 = new float[r8]
                    org.telegram.ui.Components.GroupCallPip r3 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r3 = r3.windowLayoutParams
                    int r3 = r3.y
                    float r3 = (float) r3
                    r0[r1] = r3
                    int r3 = r12.getMeasuredHeight()
                    int r13 = r13 - r3
                    float r4 = (float) r13
                    r0[r5] = r4
                    android.animation.ValueAnimator r13 = android.animation.ValueAnimator.ofFloat(r0)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    android.animation.ValueAnimator$AnimatorUpdateListener r0 = r0.updateYlistener
                    r13.addUpdateListener(r0)
                    android.animation.AnimatorSet r0 = r12.moveToBoundsAnimator
                    android.animation.Animator[] r3 = new android.animation.Animator[r5]
                    r3[r1] = r13
                    r0.playTogether(r3)
                L_0x026f:
                    android.animation.AnimatorSet r13 = r12.moveToBoundsAnimator
                    r8 = 150(0x96, double:7.4E-322)
                    android.animation.AnimatorSet r13 = r13.setDuration(r8)
                    org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                    r13.setInterpolator(r0)
                    android.animation.AnimatorSet r13 = r12.moveToBoundsAnimator
                    r13.start()
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    float r0 = r13.xRelative
                    int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
                    if (r0 < 0) goto L_0x02b9
                    float[] r0 = r13.point
                    r13.getRelativePosition(r2, r4, r0)
                    android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
                    java.lang.String r0 = "groupcallpipconfig"
                    android.content.SharedPreferences r13 = r13.getSharedPreferences(r0, r1)
                    android.content.SharedPreferences$Editor r13 = r13.edit()
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    float[] r2 = r0.point
                    r2 = r2[r1]
                    r0.xRelative = r2
                    java.lang.String r0 = "relativeX"
                    android.content.SharedPreferences$Editor r13 = r13.putFloat(r0, r2)
                    org.telegram.ui.Components.GroupCallPip r0 = org.telegram.ui.Components.GroupCallPip.this
                    float[] r2 = r0.point
                    r2 = r2[r5]
                    r0.yRelative = r2
                    java.lang.String r0 = "relativeY"
                    android.content.SharedPreferences$Editor r13 = r13.putFloat(r0, r2)
                    r13.apply()
                L_0x02b9:
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    r13.moving = r1
                    r13.showRemoveTooltip(r1)
                    goto L_0x02ff
                L_0x02c1:
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    int[] r13 = r13.location
                    r12.getLocationOnScreen(r13)
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    int[] r3 = r13.location
                    r1 = r3[r1]
                    android.view.WindowManager$LayoutParams r4 = r13.windowLayoutParams
                    int r6 = r4.x
                    int r1 = r1 - r6
                    float r1 = (float) r1
                    r13.windowOffsetLeft = r1
                    r1 = r3[r5]
                    int r3 = r4.y
                    int r1 = r1 - r3
                    float r1 = (float) r1
                    r13.windowOffsetTop = r1
                    r12.startX = r0
                    r12.startY = r2
                    java.lang.System.currentTimeMillis()
                    java.lang.Runnable r13 = r12.pressedRunnable
                    r0 = 300(0x12c, double:1.48E-321)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r13, r0)
                    org.telegram.ui.Components.GroupCallPip r13 = org.telegram.ui.Components.GroupCallPip.this
                    android.view.WindowManager$LayoutParams r0 = r13.windowLayoutParams
                    int r1 = r0.x
                    float r1 = (float) r1
                    r13.windowX = r1
                    int r0 = r0.y
                    float r0 = (float) r0
                    r13.windowY = r0
                    r13.pressedState = r5
                    r13.checkButtonAlpha()
                L_0x02ff:
                    return r5
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPip.AnonymousClass3.onTouchEvent(android.view.MotionEvent):boolean");
            }

            private void onTap() {
                if (VoIPService.getSharedInstance() != null) {
                    GroupCallPip groupCallPip = GroupCallPip.this;
                    groupCallPip.showAlert(!groupCallPip.showAlert);
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
        this.windowRemoveTooltipView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                GroupCallPip groupCallPip = GroupCallPip.this;
                groupCallPip.windowRemoveTooltipView.getLocationOnScreen(groupCallPip.location);
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                int[] iArr = groupCallPip2.location;
                groupCallPip2.windowLeft = iArr[0];
                groupCallPip2.windowTop = iArr[1] - AndroidUtilities.dp(25.0f);
            }

            public void setVisibility(int i) {
                super.setVisibility(i);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setVisibility(i);
            }
        };
        AnonymousClass5 r13 = new View(context) {
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
                        canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, (((float) getMeasuredHeight()) / 2.0f) - ((float) AndroidUtilities.dp(25.0f)), ((float) AndroidUtilities.dp(35.0f)) + (((float) AndroidUtilities.dp(5.0f)) * GroupCallPip.this.prepareToRemoveProgress), this.paint);
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
                canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, (((float) getMeasuredHeight()) / 2.0f) - ((float) AndroidUtilities.dp(25.0f)), ((float) AndroidUtilities.dp(35.0f)) + (((float) AndroidUtilities.dp(5.0f)) * GroupCallPip.this.prepareToRemoveProgress), this.paint);
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
        };
        this.removeTooltipView = r13;
        this.windowRemoveTooltipView.addView(r13);
        this.windowRemoveTooltipOverlayView = new FrameLayout(context);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f), true, (int[]) null);
        this.deleteIcon = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        rLottieImageView.setAnimation(this.deleteIcon);
        rLottieImageView.setColorFilter(-1);
        this.windowRemoveTooltipOverlayView.addView(rLottieImageView, LayoutHelper.createFrame(40, 40.0f, 17, 0.0f, 0.0f, 0.0f, 25.0f));
        AnonymousClass6 r132 = new FrameLayout(context) {
            int lastSize = -1;

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                Point point = AndroidUtilities.displaySize;
                int i5 = point.x + point.y;
                int i6 = this.lastSize;
                if (i6 > 0 && i6 != i5) {
                    setVisibility(8);
                    GroupCallPip groupCallPip = GroupCallPip.this;
                    groupCallPip.showAlert = false;
                    groupCallPip.checkButtonAlpha();
                }
                this.lastSize = i5;
            }

            public void setVisibility(int i) {
                super.setVisibility(i);
                if (i == 8) {
                    this.lastSize = -1;
                }
            }
        };
        this.alertContainer = r132;
        r132.setOnClickListener(new View.OnClickListener() {
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

    public static boolean isShowing() {
        if (instance != null) {
            return true;
        }
        if (!checkInlinePermissions()) {
            return false;
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (!((sharedInstance == null || sharedInstance.groupCall == null || sharedInstance.isHangingUp()) ? false : true) || forceRemoved || (!ApplicationLoader.mainInterfaceStopped && GroupCallActivity.groupCallUiVisible)) {
            return false;
        }
        return true;
    }

    public static boolean onBackPressed() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip == null || !groupCallPip.showAlert) {
            return false;
        }
        groupCallPip.showAlert(false);
        return true;
    }

    /* access modifiers changed from: private */
    public void showAlert(boolean z) {
        if (z != this.showAlert) {
            this.showAlert = z;
            this.alertContainer.animate().setListener((Animator.AnimatorListener) null).cancel();
            if (this.showAlert) {
                if (this.alertContainer.getVisibility() != 0) {
                    this.alertContainer.setVisibility(0);
                    this.alertContainer.setAlpha(0.0f);
                    this.pipAlertView.setScaleX(0.7f);
                    this.pipAlertView.setScaleY(0.7f);
                }
                this.alertContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        GroupCallPip.this.alertContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallPip groupCallPip = GroupCallPip.this;
                        groupCallPip.alertContainer.getLocationOnScreen(groupCallPip.location);
                        GroupCallPip groupCallPip2 = GroupCallPip.this;
                        float measuredWidth = ((float) groupCallPip2.windowLayoutParams.x) + groupCallPip2.windowOffsetLeft + (((float) groupCallPip2.button.getMeasuredWidth()) / 2.0f);
                        GroupCallPip groupCallPip3 = GroupCallPip.this;
                        float f = measuredWidth - ((float) groupCallPip3.location[0]);
                        float measuredWidth2 = ((((float) groupCallPip3.windowLayoutParams.y) + groupCallPip3.windowOffsetTop) + (((float) groupCallPip3.button.getMeasuredWidth()) / 2.0f)) - ((float) GroupCallPip.this.location[1]);
                        boolean z = measuredWidth2 - ((float) AndroidUtilities.dp(61.0f)) > 0.0f && ((float) AndroidUtilities.dp(61.0f)) + measuredWidth2 < ((float) GroupCallPip.this.alertContainer.getMeasuredHeight());
                        if (((float) AndroidUtilities.dp(61.0f)) + f + ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()) < ((float) (GroupCallPip.this.alertContainer.getMeasuredWidth() - AndroidUtilities.dp(16.0f))) && z) {
                            GroupCallPip.this.pipAlertView.setTranslationX(((float) AndroidUtilities.dp(61.0f)) + f);
                            float dp = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredHeight());
                            float max = Math.max(dp, Math.min(measuredWidth2 / ((float) GroupCallPip.this.alertContainer.getMeasuredHeight()), 1.0f - dp));
                            GroupCallPipAlertView groupCallPipAlertView = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView.setTranslationY((float) ((int) (measuredWidth2 - (((float) groupCallPipAlertView.getMeasuredHeight()) * max))));
                            GroupCallPip.this.pipAlertView.setPosition(0, f, measuredWidth2);
                        } else if ((f - ((float) AndroidUtilities.dp(61.0f))) - ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()) > ((float) AndroidUtilities.dp(16.0f)) && z) {
                            float dp2 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredHeight());
                            float max2 = Math.max(dp2, Math.min(measuredWidth2 / ((float) GroupCallPip.this.alertContainer.getMeasuredHeight()), 1.0f - dp2));
                            GroupCallPip.this.pipAlertView.setTranslationX((float) ((int) ((f - ((float) AndroidUtilities.dp(61.0f))) - ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()))));
                            GroupCallPipAlertView groupCallPipAlertView2 = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView2.setTranslationY((float) ((int) (measuredWidth2 - (((float) groupCallPipAlertView2.getMeasuredHeight()) * max2))));
                            GroupCallPip.this.pipAlertView.setPosition(1, f, measuredWidth2);
                        } else if (measuredWidth2 > ((float) GroupCallPip.this.alertContainer.getMeasuredHeight()) * 0.3f) {
                            float dp3 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth());
                            float max3 = Math.max(dp3, Math.min(f / ((float) GroupCallPip.this.alertContainer.getMeasuredWidth()), 1.0f - dp3));
                            GroupCallPipAlertView groupCallPipAlertView3 = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView3.setTranslationX((float) ((int) (f - (((float) groupCallPipAlertView3.getMeasuredWidth()) * max3))));
                            GroupCallPipAlertView groupCallPipAlertView4 = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView4.setTranslationY((float) ((int) ((measuredWidth2 - ((float) groupCallPipAlertView4.getMeasuredHeight())) - ((float) AndroidUtilities.dp(61.0f)))));
                            GroupCallPip.this.pipAlertView.setPosition(3, f, measuredWidth2);
                        } else {
                            float dp4 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth());
                            float max4 = Math.max(dp4, Math.min(f / ((float) GroupCallPip.this.alertContainer.getMeasuredWidth()), 1.0f - dp4));
                            GroupCallPipAlertView groupCallPipAlertView5 = GroupCallPip.this.pipAlertView;
                            groupCallPipAlertView5.setTranslationX((float) ((int) (f - (((float) groupCallPipAlertView5.getMeasuredWidth()) * max4))));
                            GroupCallPip.this.pipAlertView.setTranslationY((float) ((int) (((float) AndroidUtilities.dp(61.0f)) + measuredWidth2)));
                            GroupCallPip.this.pipAlertView.setPosition(2, f, measuredWidth2);
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

    public static GroupCallPip getInstance() {
        return instance;
    }

    /* access modifiers changed from: private */
    public void remove() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip != null) {
            this.removed = true;
            forceRemoved = true;
            this.button.removed = true;
            groupCallPip.showAlert(false);
            float measuredWidth = ((float) this.windowLayoutParams.x) + (((float) this.windowView.getMeasuredWidth()) / 2.0f);
            float measuredHeight = ((float) this.windowLayoutParams.y) + (((float) this.windowView.getMeasuredHeight()) / 2.0f);
            float measuredWidth2 = ((((float) this.windowLeft) - this.windowOffsetLeft) + (((float) this.windowRemoveTooltipView.getMeasuredWidth()) / 2.0f)) - measuredWidth;
            float measuredHeight2 = ((((float) this.windowTop) - this.windowOffsetTop) + (((float) this.windowRemoveTooltipView.getMeasuredHeight()) / 2.0f)) - measuredHeight;
            GroupCallPip groupCallPip2 = instance;
            WindowManager windowManager2 = groupCallPip2.windowManager;
            FrameLayout frameLayout = groupCallPip2.windowView;
            FrameLayout frameLayout2 = groupCallPip2.windowRemoveTooltipView;
            FrameLayout frameLayout3 = groupCallPip2.windowRemoveTooltipOverlayView;
            FrameLayout frameLayout4 = groupCallPip2.alertContainer;
            onDestroy();
            instance = null;
            AnimatorSet animatorSet = new AnimatorSet();
            long j = 0;
            if (this.deleteIcon.getCurrentFrame() < 33) {
                j = (long) (((1.0f - (((float) this.deleteIcon.getCurrentFrame()) / 33.0f)) * ((float) this.deleteIcon.getDuration())) / 2.0f);
            }
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
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(frameLayout, View.SCALE_X, new float[]{frameLayout.getScaleX(), 0.1f}).setDuration(180)});
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(frameLayout, View.SCALE_Y, new float[]{frameLayout.getScaleY(), 0.1f}).setDuration(180)});
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(frameLayout, View.ALPHA, new float[]{1.0f, 0.0f});
            float f = (float) 350;
            ofFloat3.setStartDelay((long) (0.7f * f));
            ofFloat3.setDuration((long) (f * 0.3f));
            animatorSet.playTogether(new Animator[]{ofFloat3});
            AndroidUtilities.runOnUIThread($$Lambda$GroupCallPip$xgZsLWasbbNg91cOicWH8iHQb88.INSTANCE, 370);
            long j2 = 350 + j + 180;
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, new float[]{1.0f, 1.05f});
            ofFloat4.setDuration(j2);
            CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.EASE_BOTH;
            ofFloat4.setInterpolator(cubicBezierInterpolator2);
            animatorSet.playTogether(new Animator[]{ofFloat4});
            ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, new float[]{1.0f, 1.05f});
            ofFloat5.setDuration(j2);
            ofFloat5.setInterpolator(cubicBezierInterpolator2);
            animatorSet.playTogether(new Animator[]{ofFloat5});
            ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, new float[]{1.0f, 0.3f});
            ofFloat6.setStartDelay(j2);
            ofFloat6.setDuration(350);
            CubicBezierInterpolator cubicBezierInterpolator3 = CubicBezierInterpolator.EASE_OUT_QUINT;
            ofFloat6.setInterpolator(cubicBezierInterpolator3);
            animatorSet.playTogether(new Animator[]{ofFloat6});
            ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, new float[]{1.0f, 0.3f});
            ofFloat7.setStartDelay(j2);
            ofFloat7.setDuration(350);
            ofFloat7.setInterpolator(cubicBezierInterpolator3);
            animatorSet.playTogether(new Animator[]{ofFloat7});
            ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(this.removeTooltipView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(60.0f)});
            ofFloat8.setStartDelay(j2);
            ofFloat8.setDuration(350);
            ofFloat8.setInterpolator(cubicBezierInterpolator3);
            animatorSet.playTogether(new Animator[]{ofFloat8});
            ObjectAnimator ofFloat9 = ObjectAnimator.ofFloat(this.removeTooltipView, View.ALPHA, new float[]{1.0f, 0.0f});
            ofFloat9.setStartDelay(j2);
            ofFloat9.setDuration(350);
            ofFloat9.setInterpolator(cubicBezierInterpolator3);
            animatorSet.playTogether(new Animator[]{ofFloat9});
            final FrameLayout frameLayout5 = frameLayout;
            final FrameLayout frameLayout6 = frameLayout2;
            final WindowManager windowManager3 = windowManager2;
            final FrameLayout frameLayout7 = frameLayout3;
            final FrameLayout frameLayout8 = frameLayout4;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    NotificationCenter.getInstance(GroupCallPip.this.currentAccount).doOnIdle(new Runnable(frameLayout5, frameLayout6, windowManager3, frameLayout7, frameLayout8) {
                        public final /* synthetic */ View f$0;
                        public final /* synthetic */ View f$1;
                        public final /* synthetic */ WindowManager f$2;
                        public final /* synthetic */ View f$3;
                        public final /* synthetic */ View f$4;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            GroupCallPip.AnonymousClass9.lambda$onAnimationEnd$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                }

                static /* synthetic */ void lambda$onAnimationEnd$0(View view, View view2, WindowManager windowManager, View view3, View view4) {
                    view.setVisibility(8);
                    view2.setVisibility(8);
                    windowManager.removeView(view);
                    windowManager.removeView(view2);
                    windowManager.removeView(view3);
                    windowManager.removeView(view4);
                }
            });
            animatorSet.start();
            this.deleteIcon.setCustomEndFrame(66);
            this.iconView.stopAnimation();
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
                while (i < 2) {
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
                this.avatarsImageView.setObject(2, this.currentAccount, (TLObject) null);
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
            WindowManager windowManager2 = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            instance.windowManager = windowManager2;
            WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams(context);
            createWindowLayoutParams.width = -1;
            createWindowLayoutParams.height = -1;
            createWindowLayoutParams.dimAmount = 0.25f;
            createWindowLayoutParams.flags = 522;
            windowManager2.addView(instance.alertContainer, createWindowLayoutParams);
            instance.alertContainer.setVisibility(8);
            WindowManager.LayoutParams createWindowLayoutParams2 = createWindowLayoutParams(context);
            createWindowLayoutParams2.gravity = 81;
            createWindowLayoutParams2.width = AndroidUtilities.dp(100.0f);
            createWindowLayoutParams2.height = AndroidUtilities.dp(150.0f);
            windowManager2.addView(instance.windowRemoveTooltipView, createWindowLayoutParams2);
            WindowManager.LayoutParams createWindowLayoutParams3 = createWindowLayoutParams(context);
            GroupCallPip groupCallPip = instance;
            groupCallPip.windowLayoutParams = createWindowLayoutParams3;
            windowManager2.addView(groupCallPip.windowView, createWindowLayoutParams3);
            WindowManager.LayoutParams createWindowLayoutParams4 = createWindowLayoutParams(context);
            createWindowLayoutParams4.gravity = 81;
            createWindowLayoutParams4.width = AndroidUtilities.dp(100.0f);
            createWindowLayoutParams4.height = AndroidUtilities.dp(150.0f);
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.groupCallVisibilityChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
    }

    /* access modifiers changed from: private */
    public void setPosition(float f, float f2) {
        float f3 = (float) (-AndroidUtilities.dp(36.0f));
        float f4 = ((float) AndroidUtilities.displaySize.x) - (2.0f * f3);
        this.windowLayoutParams.x = (int) (f3 + ((f4 - ((float) AndroidUtilities.dp(105.0f))) * f));
        this.windowLayoutParams.y = (int) (((float) (AndroidUtilities.displaySize.y - AndroidUtilities.dp(105.0f))) * f2);
        updateAvatarsPosition();
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
            final FrameLayout frameLayout2 = groupCallPip2.windowRemoveTooltipView;
            final FrameLayout frameLayout3 = groupCallPip2.windowRemoveTooltipOverlayView;
            final FrameLayout frameLayout4 = groupCallPip2.alertContainer;
            frameLayout.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (frameLayout.getParent() != null) {
                        frameLayout.setVisibility(8);
                        frameLayout2.setVisibility(8);
                        frameLayout3.setVisibility(8);
                        windowManager2.removeView(frameLayout);
                        windowManager2.removeView(frameLayout2);
                        windowManager2.removeView(frameLayout3);
                        windowManager2.removeView(frameLayout4);
                    }
                }
            }).start();
            instance.onDestroy();
            instance = null;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        }
    }

    private static WindowManager.LayoutParams createWindowLayoutParams(Context context) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = AndroidUtilities.dp(105.0f);
        layoutParams.width = AndroidUtilities.dp(105.0f);
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        if (!AndroidUtilities.checkInlinePermissions(context)) {
            layoutParams.type = 99;
        } else if (Build.VERSION.SDK_INT >= 26) {
            layoutParams.type = 2038;
        } else {
            layoutParams.type = 2003;
        }
        layoutParams.flags = 520;
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
                    this.removeTooltipView.setAlpha(0.0f);
                    this.removeTooltipView.setScaleX(0.5f);
                    this.removeTooltipView.setScaleY(0.5f);
                    this.deleteIcon.setCurrentFrame(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.showRemoveAnimator = animatorSet2;
                View view = this.removeTooltipView;
                Property property = View.ALPHA;
                float[] fArr = {view.getAlpha(), 1.0f};
                View view2 = this.removeTooltipView;
                Property property2 = View.SCALE_X;
                float[] fArr2 = {view2.getScaleX(), 1.0f};
                View view3 = this.removeTooltipView;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, property, fArr), ObjectAnimator.ofFloat(view2, property2, fArr2), ObjectAnimator.ofFloat(view3, View.SCALE_Y, new float[]{view3.getScaleY(), 1.0f})});
                this.showRemoveAnimator.setDuration(150).start();
                return;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.showRemoveAnimator = animatorSet3;
            View view4 = this.removeTooltipView;
            Property property3 = View.ALPHA;
            float[] fArr3 = {view4.getAlpha(), 0.0f};
            View view5 = this.removeTooltipView;
            Property property4 = View.SCALE_X;
            float[] fArr4 = {view5.getScaleX(), 0.5f};
            View view6 = this.removeTooltipView;
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
            this.removeTooltipView.invalidate();
            if (!this.removed) {
                this.deleteIcon.setCustomEndFrame(z ? 33 : 0);
                this.iconView.playAnimation();
            }
            if (z) {
                this.button.performHapticFeedback(3, 2);
            }
        }
        this.button.prepareToRemove(z);
    }

    /* access modifiers changed from: package-private */
    public void pinnedToCenter(final boolean z) {
        if (!this.removed && this.animateToPinnedToCenter != z) {
            this.animateToPinnedToCenter = z;
            ValueAnimator valueAnimator = this.pinAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.pinAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.pinnedProgress;
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.pinAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GroupCallPip.this.lambda$pinnedToCenter$3$GroupCallPip(valueAnimator);
                }
            });
            this.pinAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    GroupCallPip groupCallPip = GroupCallPip.this;
                    if (!groupCallPip.removed) {
                        groupCallPip.pinnedProgress = z ? 1.0f : 0.0f;
                        groupCallPip.button.setPinnedProgress(GroupCallPip.this.pinnedProgress);
                        GroupCallPip groupCallPip2 = GroupCallPip.this;
                        groupCallPip2.windowView.setScaleX(1.0f - (groupCallPip2.pinnedProgress * 0.6f));
                        GroupCallPip groupCallPip3 = GroupCallPip.this;
                        groupCallPip3.windowView.setScaleY(1.0f - (groupCallPip3.pinnedProgress * 0.6f));
                        GroupCallPip groupCallPip4 = GroupCallPip.this;
                        if (groupCallPip4.moving) {
                            groupCallPip4.updateButtonPosition();
                        }
                    }
                }
            });
            this.pinAnimator.setDuration(250);
            this.pinAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.pinAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$pinnedToCenter$3 */
    public /* synthetic */ void lambda$pinnedToCenter$3$GroupCallPip(ValueAnimator valueAnimator) {
        if (!this.removed) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.pinnedProgress = floatValue;
            this.button.setPinnedProgress(floatValue);
            this.windowView.setScaleX(1.0f - (this.pinnedProgress * 0.6f));
            this.windowView.setScaleY(1.0f - (this.pinnedProgress * 0.6f));
            if (this.moving) {
                updateButtonPosition();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateButtonPosition() {
        float measuredWidth = ((((float) this.windowLeft) - this.windowOffsetLeft) + (((float) this.windowRemoveTooltipView.getMeasuredWidth()) / 2.0f)) - (((float) this.windowView.getMeasuredWidth()) / 2.0f);
        float measuredHeight = (((((float) this.windowTop) - this.windowOffsetTop) + (((float) this.windowRemoveTooltipView.getMeasuredHeight()) / 2.0f)) - (((float) this.windowView.getMeasuredHeight()) / 2.0f)) - ((float) AndroidUtilities.dp(25.0f));
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        float f = this.windowX;
        float f2 = this.pinnedProgress;
        layoutParams.x = (int) ((f * (1.0f - f2)) + (measuredWidth * f2));
        layoutParams.y = (int) ((this.windowY * (1.0f - f2)) + (measuredHeight * f2));
        updateAvatarsPosition();
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    /* access modifiers changed from: private */
    public void updateAvatarsPosition() {
        int i = AndroidUtilities.displaySize.x;
        float min = Math.min((float) Math.max(this.windowLayoutParams.x, -AndroidUtilities.dp(36.0f)), (float) ((i - this.windowView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f)));
        if (min < 0.0f) {
            this.avatarsImageView.setTranslationX(Math.abs(min) / 3.0f);
        } else if (min > ((float) (i - this.windowView.getMeasuredWidth()))) {
            this.avatarsImageView.setTranslationX((-Math.abs(min - ((float) (i - this.windowView.getMeasuredWidth())))) / 3.0f);
        } else {
            this.avatarsImageView.setTranslationX(0.0f);
        }
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
        float f3 = (float) (-AndroidUtilities.dp(36.0f));
        fArr[0] = (f - f3) / ((((float) point2.x) - (f3 * 2.0f)) - ((float) AndroidUtilities.dp(105.0f)));
        fArr[1] = f2 / (((float) point2.y) - ((float) AndroidUtilities.dp(105.0f)));
        fArr[0] = Math.min(1.0f, Math.max(0.0f, fArr[0]));
        fArr[1] = Math.min(1.0f, Math.max(0.0f, fArr[1]));
    }

    public static void updateVisibility(Context context) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z = false;
        boolean z2 = (sharedInstance == null || sharedInstance.groupCall == null || sharedInstance.isHangingUp()) ? false : true;
        if (AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext) && z2 && !forceRemoved && (ApplicationLoader.mainInterfaceStopped || !GroupCallActivity.groupCallUiVisible)) {
            z = true;
        }
        if (z) {
            show(context, sharedInstance.getAccount());
            instance.showAvatars(true);
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

    public static boolean checkInlinePermissions() {
        return Build.VERSION.SDK_INT < 23 || ApplicationLoader.canDrawOverlays;
    }
}
