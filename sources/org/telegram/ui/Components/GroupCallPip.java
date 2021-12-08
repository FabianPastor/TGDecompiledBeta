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
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
            GroupCallPip.this.windowLayoutParams.x = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallPip.this.updateAvatarsPosition();
            if (GroupCallPip.this.windowView.getParent() != null) {
                GroupCallPip.this.windowManager.updateViewLayout(GroupCallPip.this.windowView, GroupCallPip.this.windowLayoutParams);
            }
        }
    };
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener updateYlistener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            GroupCallPip.this.windowLayoutParams.y = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (GroupCallPip.this.windowView.getParent() != null) {
                GroupCallPip.this.windowManager.updateViewLayout(GroupCallPip.this.windowView, GroupCallPip.this.windowLayoutParams);
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

    public GroupCallPip(Context context, int account) {
        this.currentAccount = account;
        final float touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        AnonymousClass3 r2 = new FrameLayout(context) {
            Runnable micRunnable = GroupCallPip$3$$ExternalSyntheticLambda0.INSTANCE;
            AnimatorSet moveToBoundsAnimator;
            boolean pressed;
            Runnable pressedRunnable = new Runnable() {
                public void run() {
                    VoIPService voIPService = VoIPService.getSharedInstance();
                    if (voIPService != null && voIPService.isMicMute()) {
                        TLRPC.TL_groupCallParticipant participant = voIPService.groupCall.participants.get(voIPService.getSelfId());
                        if (participant == null || participant.can_self_unmute || !participant.muted || ChatObject.canManageCalls(voIPService.getChat())) {
                            AndroidUtilities.runOnUIThread(AnonymousClass3.this.micRunnable, 90);
                            AnonymousClass3.this.performHapticFeedback(3, 2);
                            AnonymousClass3.this.pressed = true;
                        }
                    }
                }
            };
            long startTime;
            float startX;
            float startY;

            static /* synthetic */ void lambda$$0() {
                if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute()) {
                    VoIPService.getSharedInstance().setMicMute(false, true, false);
                }
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (AndroidUtilities.displaySize.x != GroupCallPip.this.lastScreenX || GroupCallPip.this.lastScreenY != AndroidUtilities.displaySize.y) {
                    GroupCallPip.this.lastScreenX = AndroidUtilities.displaySize.x;
                    GroupCallPip.this.lastScreenY = AndroidUtilities.displaySize.y;
                    if (GroupCallPip.this.xRelative < 0.0f) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("groupcallpipconfig", 0);
                        GroupCallPip.this.xRelative = preferences.getFloat("relativeX", 1.0f);
                        GroupCallPip.this.yRelative = preferences.getFloat("relativeY", 0.4f);
                    }
                    if (GroupCallPip.instance != null) {
                        GroupCallPip.instance.setPosition(GroupCallPip.this.xRelative, GroupCallPip.this.yRelative);
                    }
                }
            }

            public boolean onTouchEvent(MotionEvent event) {
                double angle;
                if (GroupCallPip.instance == null) {
                    return false;
                }
                float x = event.getRawX();
                float y = event.getRawY();
                ViewParent parent = getParent();
                switch (event.getAction()) {
                    case 0:
                        getLocationOnScreen(GroupCallPip.this.location);
                        GroupCallPip groupCallPip = GroupCallPip.this;
                        groupCallPip.windowOffsetLeft = (float) (groupCallPip.location[0] - GroupCallPip.this.windowLayoutParams.x);
                        GroupCallPip groupCallPip2 = GroupCallPip.this;
                        groupCallPip2.windowOffsetTop = (float) (groupCallPip2.location[1] - GroupCallPip.this.windowLayoutParams.y);
                        this.startX = x;
                        this.startY = y;
                        this.startTime = System.currentTimeMillis();
                        AndroidUtilities.runOnUIThread(this.pressedRunnable, 300);
                        GroupCallPip groupCallPip3 = GroupCallPip.this;
                        groupCallPip3.windowX = (float) groupCallPip3.windowLayoutParams.x;
                        GroupCallPip groupCallPip4 = GroupCallPip.this;
                        groupCallPip4.windowY = (float) groupCallPip4.windowLayoutParams.y;
                        GroupCallPip.this.pressedState = true;
                        GroupCallPip.this.checkButtonAlpha();
                        return true;
                    case 1:
                    case 3:
                        AndroidUtilities.cancelRunOnUIThread(this.micRunnable);
                        AndroidUtilities.cancelRunOnUIThread(this.pressedRunnable);
                        if (GroupCallPip.this.animateToPrepareRemove) {
                            if (this.pressed && VoIPService.getSharedInstance() != null) {
                                VoIPService.getSharedInstance().setMicMute(true, false, false);
                            }
                            this.pressed = false;
                            GroupCallPip.this.remove();
                            return false;
                        }
                        GroupCallPip.this.pressedState = false;
                        GroupCallPip.this.checkButtonAlpha();
                        if (this.pressed) {
                            if (VoIPService.getSharedInstance() != null) {
                                VoIPService.getSharedInstance().setMicMute(true, false, false);
                                performHapticFeedback(3, 2);
                            }
                            this.pressed = false;
                        } else if (event.getAction() == 1 && !GroupCallPip.this.moving) {
                            onTap();
                            return false;
                        }
                        if (parent == null || !GroupCallPip.this.moving) {
                        } else {
                            parent.requestDisallowInterceptTouchEvent(false);
                            int parentWidth = AndroidUtilities.displaySize.x;
                            int parentHeight = AndroidUtilities.displaySize.y;
                            float left = (float) GroupCallPip.this.windowLayoutParams.x;
                            float right = ((float) getMeasuredWidth()) + left;
                            float top = (float) GroupCallPip.this.windowLayoutParams.y;
                            float bottom = ((float) getMeasuredHeight()) + top;
                            this.moveToBoundsAnimator = new AnimatorSet();
                            float finallyX = left;
                            float finallyY = top;
                            float paddingHorizontal = (float) (-AndroidUtilities.dp(36.0f));
                            if (left < paddingHorizontal) {
                                finallyX = paddingHorizontal;
                                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{(float) GroupCallPip.this.windowLayoutParams.x, paddingHorizontal});
                                animator.addUpdateListener(GroupCallPip.this.updateXlistener);
                                ViewParent viewParent = parent;
                                this.moveToBoundsAnimator.playTogether(new Animator[]{animator});
                            } else {
                                if (right > ((float) parentWidth) - paddingHorizontal) {
                                    float measuredWidth = ((float) (parentWidth - getMeasuredWidth())) - paddingHorizontal;
                                    finallyX = measuredWidth;
                                    ValueAnimator animator2 = ValueAnimator.ofFloat(new float[]{(float) GroupCallPip.this.windowLayoutParams.x, measuredWidth});
                                    animator2.addUpdateListener(GroupCallPip.this.updateXlistener);
                                    this.moveToBoundsAnimator.playTogether(new Animator[]{animator2});
                                }
                            }
                            int maxBottom = AndroidUtilities.dp(36.0f) + parentHeight;
                            if (top < ((float) (AndroidUtilities.statusBarHeight - AndroidUtilities.dp(36.0f)))) {
                                float dp = (float) (AndroidUtilities.statusBarHeight - AndroidUtilities.dp(36.0f));
                                finallyY = dp;
                                ValueAnimator animator3 = ValueAnimator.ofFloat(new float[]{(float) GroupCallPip.this.windowLayoutParams.y, dp});
                                animator3.addUpdateListener(GroupCallPip.this.updateYlistener);
                                int i = parentWidth;
                                this.moveToBoundsAnimator.playTogether(new Animator[]{animator3});
                            } else {
                                if (bottom > ((float) maxBottom)) {
                                    float measuredHeight = (float) (maxBottom - getMeasuredHeight());
                                    finallyY = measuredHeight;
                                    ValueAnimator animator4 = ValueAnimator.ofFloat(new float[]{(float) GroupCallPip.this.windowLayoutParams.y, measuredHeight});
                                    animator4.addUpdateListener(GroupCallPip.this.updateYlistener);
                                    this.moveToBoundsAnimator.playTogether(new Animator[]{animator4});
                                }
                            }
                            this.moveToBoundsAnimator.setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT);
                            this.moveToBoundsAnimator.start();
                            if (GroupCallPip.this.xRelative >= 0.0f) {
                                GroupCallPip groupCallPip5 = GroupCallPip.this;
                                groupCallPip5.getRelativePosition(finallyX, finallyY, groupCallPip5.point);
                                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("groupcallpipconfig", 0);
                                SharedPreferences.Editor edit = preferences.edit();
                                GroupCallPip groupCallPip6 = GroupCallPip.this;
                                SharedPreferences sharedPreferences = preferences;
                                float f = groupCallPip6.point[0];
                                groupCallPip6.xRelative = f;
                                SharedPreferences.Editor putFloat = edit.putFloat("relativeX", f);
                                GroupCallPip groupCallPip7 = GroupCallPip.this;
                                float f2 = groupCallPip7.point[1];
                                groupCallPip7.yRelative = f2;
                                putFloat.putFloat("relativeY", f2).apply();
                            }
                        }
                        GroupCallPip.this.moving = false;
                        GroupCallPip.this.showRemoveTooltip(false);
                        return true;
                    case 2:
                        float dx = x - this.startX;
                        float dy = y - this.startY;
                        if (!GroupCallPip.this.moving) {
                            float f3 = (dx * dx) + (dy * dy);
                            float f4 = touchSlop;
                            if (f3 > f4 * f4) {
                                if (parent != null) {
                                    parent.requestDisallowInterceptTouchEvent(true);
                                }
                                AndroidUtilities.cancelRunOnUIThread(this.pressedRunnable);
                                GroupCallPip.this.moving = true;
                                GroupCallPip.this.showRemoveTooltip(true);
                                GroupCallPip.this.showAlert(false);
                                this.startX = x;
                                this.startY = y;
                                dx = 0.0f;
                                dy = 0.0f;
                            }
                        }
                        if (GroupCallPip.this.moving) {
                            GroupCallPip.this.windowX += dx;
                            GroupCallPip.this.windowY += dy;
                            this.startX = x;
                            this.startY = y;
                            GroupCallPip.this.updateButtonPosition();
                            float cx = GroupCallPip.this.windowX + (((float) getMeasuredWidth()) / 2.0f);
                            float cy = GroupCallPip.this.windowY + (((float) getMeasuredHeight()) / 2.0f);
                            float cxRemove = (((float) GroupCallPip.this.windowLeft) - GroupCallPip.this.windowOffsetLeft) + (((float) GroupCallPip.this.windowRemoveTooltipView.getMeasuredWidth()) / 2.0f);
                            float cyRemove = (((float) GroupCallPip.this.windowTop) - GroupCallPip.this.windowOffsetTop) + (((float) GroupCallPip.this.windowRemoveTooltipView.getMeasuredHeight()) / 2.0f);
                            float distanceToRemove = ((cx - cxRemove) * (cx - cxRemove)) + ((cy - cyRemove) * (cy - cyRemove));
                            boolean prepareToRemove = false;
                            boolean pinnedToCenter = false;
                            if (distanceToRemove < ((float) (AndroidUtilities.dp(80.0f) * AndroidUtilities.dp(80.0f)))) {
                                prepareToRemove = true;
                                double angle2 = Math.toDegrees(Math.atan((double) ((cx - cxRemove) / (cy - cyRemove))));
                                if ((cx <= cxRemove || cy >= cyRemove) && (cx >= cxRemove || cy >= cyRemove)) {
                                    angle = 90.0d - angle2;
                                } else {
                                    angle = 270.0d - angle2;
                                }
                                GroupCallPip.this.button.setRemoveAngle(angle);
                                if (distanceToRemove < ((float) (AndroidUtilities.dp(50.0f) * AndroidUtilities.dp(50.0f)))) {
                                    pinnedToCenter = true;
                                }
                            }
                            GroupCallPip.this.pinnedToCenter(pinnedToCenter);
                            GroupCallPip.this.prepareToRemove(prepareToRemove);
                            ViewParent viewParent2 = parent;
                            return true;
                        }
                        ViewParent viewParent3 = parent;
                        return true;
                    default:
                        ViewParent viewParent4 = parent;
                        return true;
                }
            }

            private void onTap() {
                if (VoIPService.getSharedInstance() != null) {
                    GroupCallPip groupCallPip = GroupCallPip.this;
                    groupCallPip.showAlert(!groupCallPip.showAlert);
                }
            }
        };
        this.windowView = r2;
        r2.setAlpha(0.7f);
        GroupCallPipButton groupCallPipButton = new GroupCallPipButton(context, this.currentAccount, false);
        this.button = groupCallPipButton;
        this.windowView.addView(groupCallPipButton, LayoutHelper.createFrame(-1, -1, 17));
        AvatarsImageView avatarsImageView2 = new AvatarsImageView(context, true);
        this.avatarsImageView = avatarsImageView2;
        avatarsImageView2.setStyle(5);
        this.avatarsImageView.setCentered(true);
        this.avatarsImageView.setVisibility(8);
        this.avatarsImageView.setDelegate(new GroupCallPip$$ExternalSyntheticLambda2(this));
        updateAvatars(false);
        this.windowView.addView(this.avatarsImageView, LayoutHelper.createFrame(108, 36, 49));
        this.windowRemoveTooltipView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                GroupCallPip.this.windowRemoveTooltipView.getLocationOnScreen(GroupCallPip.this.location);
                GroupCallPip groupCallPip = GroupCallPip.this;
                groupCallPip.windowLeft = groupCallPip.location[0];
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                groupCallPip2.windowTop = groupCallPip2.location[1] - AndroidUtilities.dp(25.0f);
            }

            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setVisibility(visibility);
            }
        };
        AnonymousClass5 r22 = new View(context) {
            Paint paint = new Paint(1);

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (GroupCallPip.this.animateToPrepareRemove && GroupCallPip.this.prepareToRemoveProgress != 1.0f) {
                    GroupCallPip.this.prepareToRemoveProgress += 0.064f;
                    if (GroupCallPip.this.prepareToRemoveProgress > 1.0f) {
                        GroupCallPip.this.prepareToRemoveProgress = 1.0f;
                    }
                    invalidate();
                } else if (!GroupCallPip.this.animateToPrepareRemove && GroupCallPip.this.prepareToRemoveProgress != 0.0f) {
                    GroupCallPip.this.prepareToRemoveProgress -= 0.064f;
                    if (GroupCallPip.this.prepareToRemoveProgress < 0.0f) {
                        GroupCallPip.this.prepareToRemoveProgress = 0.0f;
                    }
                    invalidate();
                }
                this.paint.setColor(ColorUtils.blendARGB(NUM, NUM, GroupCallPip.this.prepareToRemoveProgress));
                canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, (((float) getMeasuredHeight()) / 2.0f) - ((float) AndroidUtilities.dp(25.0f)), ((float) AndroidUtilities.dp(35.0f)) + (((float) AndroidUtilities.dp(5.0f)) * GroupCallPip.this.prepareToRemoveProgress), this.paint);
            }

            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setAlpha(alpha);
            }

            public void setScaleX(float scaleX) {
                super.setScaleX(scaleX);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setScaleX(scaleX);
            }

            public void setScaleY(float scaleY) {
                super.setScaleY(scaleY);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setScaleY(scaleY);
            }

            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setTranslationY(translationY);
            }
        };
        this.removeTooltipView = r22;
        this.windowRemoveTooltipView.addView(r22);
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
        AnonymousClass6 r23 = new FrameLayout(context) {
            int lastSize = -1;

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int size = AndroidUtilities.displaySize.x + AndroidUtilities.displaySize.y;
                int i = this.lastSize;
                if (i > 0 && i != size) {
                    setVisibility(8);
                    GroupCallPip.this.showAlert = false;
                    GroupCallPip.this.checkButtonAlpha();
                }
                this.lastSize = size;
            }

            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                if (visibility == 8) {
                    this.lastSize = -1;
                }
            }
        };
        this.alertContainer = r23;
        r23.setOnClickListener(new GroupCallPip$$ExternalSyntheticLambda1(this));
        this.alertContainer.setClipChildren(false);
        FrameLayout frameLayout = this.alertContainer;
        GroupCallPipAlertView groupCallPipAlertView = new GroupCallPipAlertView(context, this.currentAccount);
        this.pipAlertView = groupCallPipAlertView;
        frameLayout.addView(groupCallPipAlertView, LayoutHelper.createFrame(-2, -2.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GroupCallPip  reason: not valid java name */
    public /* synthetic */ void m2317lambda$new$0$orgtelegramuiComponentsGroupCallPip() {
        updateAvatars(true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-GroupCallPip  reason: not valid java name */
    public /* synthetic */ void m2318lambda$new$1$orgtelegramuiComponentsGroupCallPip(View view) {
        showAlert(false);
    }

    public static boolean isShowing() {
        if (instance != null) {
            return true;
        }
        if (!checkInlinePermissions()) {
            return false;
        }
        VoIPService service = VoIPService.getSharedInstance();
        boolean groupCall = false;
        if (!(service == null || service.groupCall == null || service.isHangingUp())) {
            groupCall = true;
        }
        if (!groupCall || forceRemoved || (!ApplicationLoader.mainInterfaceStopped && GroupCallActivity.groupCallUiVisible)) {
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
    public void showAlert(boolean b) {
        if (b != this.showAlert) {
            this.showAlert = b;
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
                        GroupCallPip.this.alertContainer.getLocationOnScreen(GroupCallPip.this.location);
                        float cx = ((((float) GroupCallPip.this.windowLayoutParams.x) + GroupCallPip.this.windowOffsetLeft) + (((float) GroupCallPip.this.button.getMeasuredWidth()) / 2.0f)) - ((float) GroupCallPip.this.location[0]);
                        float cy = ((((float) GroupCallPip.this.windowLayoutParams.y) + GroupCallPip.this.windowOffsetTop) + (((float) GroupCallPip.this.button.getMeasuredWidth()) / 2.0f)) - ((float) GroupCallPip.this.location[1]);
                        boolean canHorizontal = cy - ((float) AndroidUtilities.dp(61.0f)) > 0.0f && ((float) AndroidUtilities.dp(61.0f)) + cy < ((float) GroupCallPip.this.alertContainer.getMeasuredHeight());
                        if (((float) AndroidUtilities.dp(61.0f)) + cx + ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()) < ((float) (GroupCallPip.this.alertContainer.getMeasuredWidth() - AndroidUtilities.dp(16.0f))) && canHorizontal) {
                            GroupCallPip.this.pipAlertView.setTranslationX(((float) AndroidUtilities.dp(61.0f)) + cx);
                            float maxOffset = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredHeight());
                            GroupCallPip.this.pipAlertView.setTranslationY((float) ((int) (cy - (((float) GroupCallPip.this.pipAlertView.getMeasuredHeight()) * Math.max(maxOffset, Math.min(cy / ((float) GroupCallPip.this.alertContainer.getMeasuredHeight()), 1.0f - maxOffset))))));
                            GroupCallPip.this.pipAlertView.setPosition(0, cx, cy);
                        } else if ((cx - ((float) AndroidUtilities.dp(61.0f))) - ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()) > ((float) AndroidUtilities.dp(16.0f)) && canHorizontal) {
                            float maxOffset2 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredHeight());
                            float yOffset = Math.max(maxOffset2, Math.min(cy / ((float) GroupCallPip.this.alertContainer.getMeasuredHeight()), 1.0f - maxOffset2));
                            GroupCallPip.this.pipAlertView.setTranslationX((float) ((int) ((cx - ((float) AndroidUtilities.dp(61.0f))) - ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()))));
                            GroupCallPip.this.pipAlertView.setTranslationY((float) ((int) (cy - (((float) GroupCallPip.this.pipAlertView.getMeasuredHeight()) * yOffset))));
                            GroupCallPip.this.pipAlertView.setPosition(1, cx, cy);
                        } else if (cy > ((float) GroupCallPip.this.alertContainer.getMeasuredHeight()) * 0.3f) {
                            float maxOffset3 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth());
                            GroupCallPip.this.pipAlertView.setTranslationX((float) ((int) (cx - (((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()) * Math.max(maxOffset3, Math.min(cx / ((float) GroupCallPip.this.alertContainer.getMeasuredWidth()), 1.0f - maxOffset3))))));
                            GroupCallPip.this.pipAlertView.setTranslationY((float) ((int) ((cy - ((float) GroupCallPip.this.pipAlertView.getMeasuredHeight())) - ((float) AndroidUtilities.dp(61.0f)))));
                            GroupCallPip.this.pipAlertView.setPosition(3, cx, cy);
                        } else {
                            float maxOffset4 = ((float) AndroidUtilities.dp(40.0f)) / ((float) GroupCallPip.this.pipAlertView.getMeasuredWidth());
                            GroupCallPip.this.pipAlertView.setTranslationX((float) ((int) (cx - (((float) GroupCallPip.this.pipAlertView.getMeasuredWidth()) * Math.max(maxOffset4, Math.min(cx / ((float) GroupCallPip.this.alertContainer.getMeasuredWidth()), 1.0f - maxOffset4))))));
                            GroupCallPip.this.pipAlertView.setTranslationY((float) ((int) (((float) AndroidUtilities.dp(61.0f)) + cy)));
                            GroupCallPip.this.pipAlertView.setPosition(2, cx, cy);
                        }
                        return false;
                    }
                });
                this.alertContainer.animate().alpha(1.0f).setDuration(150).start();
                this.pipAlertView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
            } else {
                this.pipAlertView.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).start();
                this.alertContainer.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        GroupCallPip.this.alertContainer.setVisibility(8);
                    }
                }).start();
            }
        }
        checkButtonAlpha();
    }

    /* access modifiers changed from: private */
    public void checkButtonAlpha() {
        boolean alpha = this.pressedState || this.showAlert;
        if (this.buttonInAlpha != alpha) {
            this.buttonInAlpha = alpha;
            if (alpha) {
                this.windowView.animate().alpha(1.0f).start();
            } else {
                this.windowView.animate().alpha(0.7f).start();
            }
            this.button.setPressedState(alpha);
        }
    }

    public static GroupCallPip getInstance() {
        return instance;
    }

    /* access modifiers changed from: private */
    public void remove() {
        View alert;
        if (instance != null) {
            this.removed = true;
            forceRemoved = true;
            this.button.removed = true;
            instance.showAlert(false);
            float cx = ((float) this.windowLayoutParams.x) + (((float) this.windowView.getMeasuredWidth()) / 2.0f);
            float cy = ((float) this.windowLayoutParams.y) + (((float) this.windowView.getMeasuredHeight()) / 2.0f);
            float cxRemove = (((float) this.windowLeft) - this.windowOffsetLeft) + (((float) this.windowRemoveTooltipView.getMeasuredWidth()) / 2.0f);
            float cyRemove = (((float) this.windowTop) - this.windowOffsetTop) + (((float) this.windowRemoveTooltipView.getMeasuredHeight()) / 2.0f);
            float dx = cxRemove - cx;
            float dy = cyRemove - cy;
            GroupCallPip groupCallPip = instance;
            WindowManager windowManager2 = groupCallPip.windowManager;
            View windowView2 = groupCallPip.windowView;
            FrameLayout frameLayout = groupCallPip.windowRemoveTooltipView;
            View windowRemoveTooltipOverlayView2 = groupCallPip.windowRemoveTooltipOverlayView;
            View alert2 = groupCallPip.alertContainer;
            onDestroy();
            instance = null;
            AnimatorSet animatorSet = new AnimatorSet();
            long additionalDuration = 0;
            if (this.deleteIcon.getCurrentFrame() < 33) {
                alert = alert2;
                additionalDuration = (long) (((1.0f - (((float) this.deleteIcon.getCurrentFrame()) / 33.0f)) * ((float) this.deleteIcon.getDuration())) / 2.0f);
            } else {
                alert = alert2;
            }
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{(float) this.windowLayoutParams.x, ((float) this.windowLayoutParams.x) + dx});
            animator.addUpdateListener(this.updateXlistener);
            View windowRemoveTooltipOverlayView3 = windowRemoveTooltipOverlayView2;
            animator.setDuration(250).setInterpolator(CubicBezierInterpolator.DEFAULT);
            AnimatorSet animatorSet2 = animatorSet;
            animatorSet2.playTogether(new Animator[]{animator});
            ValueAnimator valueAnimator = animator;
            ValueAnimator animator2 = ValueAnimator.ofFloat(new float[]{(float) this.windowLayoutParams.y, (((float) this.windowLayoutParams.y) + dy) - ((float) AndroidUtilities.dp(30.0f)), ((float) this.windowLayoutParams.y) + dy});
            animator2.addUpdateListener(this.updateYlistener);
            animator2.setDuration(250).setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet2.playTogether(new Animator[]{animator2});
            ValueAnimator animator3 = animator2;
            float f = cx;
            float f2 = cy;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(windowView2, View.SCALE_X, new float[]{windowView2.getScaleX(), 0.1f});
            float cxRemove2 = cxRemove;
            animatorSet2.playTogether(new Animator[]{ofFloat.setDuration(180)});
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(windowView2, View.SCALE_Y, new float[]{windowView2.getScaleY(), 0.1f});
            float cyRemove2 = cyRemove;
            animatorSet2.playTogether(new Animator[]{ofFloat2.setDuration(180)});
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(windowView2, View.ALPHA, new float[]{1.0f, 0.0f});
            alphaAnimator.setStartDelay((long) (((float) 350) * 0.7f));
            alphaAnimator.setDuration((long) (((float) 350) * 0.3f));
            animatorSet2.playTogether(new Animator[]{alphaAnimator});
            AndroidUtilities.runOnUIThread(GroupCallPip$$ExternalSyntheticLambda3.INSTANCE, 20 + 350);
            float f3 = cxRemove2;
            float f4 = cyRemove2;
            long moveDuration = 350 + additionalDuration + 180;
            ObjectAnimator o = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, new float[]{1.0f, 1.05f});
            o.setDuration(moveDuration);
            o.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            animatorSet2.playTogether(new Animator[]{o});
            ObjectAnimator o2 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, new float[]{1.0f, 1.05f});
            o2.setDuration(moveDuration);
            o2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            animatorSet2.playTogether(new Animator[]{o2});
            ObjectAnimator o3 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, new float[]{1.0f, 0.3f});
            o3.setStartDelay(moveDuration);
            o3.setDuration(350);
            o3.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animatorSet2.playTogether(new Animator[]{o3});
            ObjectAnimator o4 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, new float[]{1.0f, 0.3f});
            o4.setStartDelay(moveDuration);
            o4.setDuration(350);
            o4.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animatorSet2.playTogether(new Animator[]{o4});
            ObjectAnimator objectAnimator = o4;
            ObjectAnimator o5 = ObjectAnimator.ofFloat(this.removeTooltipView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(60.0f)});
            o5.setStartDelay(moveDuration);
            o5.setDuration(350);
            o5.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animatorSet2.playTogether(new Animator[]{o5});
            ObjectAnimator o6 = ObjectAnimator.ofFloat(this.removeTooltipView, View.ALPHA, new float[]{1.0f, 0.0f});
            o6.setStartDelay(moveDuration);
            o6.setDuration(350);
            o6.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animatorSet2.playTogether(new Animator[]{o6});
            final View view = windowView2;
            long j = moveDuration;
            View alert3 = alert;
            AnonymousClass9 r8 = r0;
            final FrameLayout frameLayout2 = frameLayout;
            ObjectAnimator objectAnimator2 = o6;
            ValueAnimator valueAnimator2 = animator3;
            final WindowManager windowManager3 = windowManager2;
            AnimatorSet animatorSet3 = animatorSet2;
            final View view2 = windowRemoveTooltipOverlayView3;
            FrameLayout frameLayout3 = frameLayout;
            final View windowRemoveTooltipView2 = alert3;
            AnonymousClass9 r0 = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(GroupCallPip.this.currentAccount).doOnIdle(new GroupCallPip$9$$ExternalSyntheticLambda0(view, frameLayout2, windowManager3, view2, windowRemoveTooltipView2));
                }

                static /* synthetic */ void lambda$onAnimationEnd$0(View windowView, View windowRemoveTooltipView, WindowManager windowManager, View windowRemoveTooltipOverlayView, View alert) {
                    windowView.setVisibility(8);
                    windowRemoveTooltipView.setVisibility(8);
                    windowManager.removeView(windowView);
                    windowManager.removeView(windowRemoveTooltipView);
                    windowManager.removeView(windowRemoveTooltipOverlayView);
                    windowManager.removeView(alert);
                }
            };
            animatorSet3.addListener(r8);
            animatorSet3.start();
            this.deleteIcon.setCustomEndFrame(66);
            this.iconView.stopAnimation();
            this.iconView.playAnimation();
        }
    }

    private void updateAvatars(boolean animated) {
        ChatObject.Call call;
        if (this.avatarsImageView.transitionProgressAnimator == null) {
            VoIPService voIPService = VoIPService.getSharedInstance();
            if (voIPService != null) {
                call = voIPService.groupCall;
            } else {
                call = null;
            }
            if (call != null) {
                long selfId = voIPService.getSelfId();
                int a = 0;
                int N = call.sortedParticipants.size();
                int k = 0;
                while (k < 2) {
                    if (a < N) {
                        TLRPC.TL_groupCallParticipant participant = call.sortedParticipants.get(a);
                        if (MessageObject.getPeerId(participant.peer) != selfId && SystemClock.uptimeMillis() - participant.lastSpeakTime <= 500) {
                            this.avatarsImageView.setObject(k, this.currentAccount, participant);
                            k++;
                        }
                    } else {
                        this.avatarsImageView.setObject(k, this.currentAccount, (TLObject) null);
                        k++;
                    }
                    a++;
                }
                this.avatarsImageView.setObject(2, this.currentAccount, (TLObject) null);
                this.avatarsImageView.commitTransition(animated);
                return;
            }
            for (int a2 = 0; a2 < 3; a2++) {
                this.avatarsImageView.setObject(a2, this.currentAccount, (TLObject) null);
            }
            this.avatarsImageView.commitTransition(animated);
            return;
        }
        this.avatarsImageView.updateAfterTransitionEnd();
    }

    public static void show(Context context, int account) {
        if (instance == null) {
            instance = new GroupCallPip(context, account);
            WindowManager wm = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            instance.windowManager = wm;
            WindowManager.LayoutParams windowLayoutParams2 = createWindowLayoutParams(context);
            windowLayoutParams2.width = -1;
            windowLayoutParams2.height = -1;
            windowLayoutParams2.dimAmount = 0.25f;
            windowLayoutParams2.flags = 522;
            wm.addView(instance.alertContainer, windowLayoutParams2);
            instance.alertContainer.setVisibility(8);
            WindowManager.LayoutParams windowLayoutParams3 = createWindowLayoutParams(context);
            windowLayoutParams3.gravity = 81;
            windowLayoutParams3.width = AndroidUtilities.dp(100.0f);
            windowLayoutParams3.height = AndroidUtilities.dp(150.0f);
            wm.addView(instance.windowRemoveTooltipView, windowLayoutParams3);
            WindowManager.LayoutParams windowLayoutParams4 = createWindowLayoutParams(context);
            GroupCallPip groupCallPip = instance;
            groupCallPip.windowLayoutParams = windowLayoutParams4;
            wm.addView(groupCallPip.windowView, windowLayoutParams4);
            WindowManager.LayoutParams windowLayoutParams5 = createWindowLayoutParams(context);
            windowLayoutParams5.gravity = 81;
            windowLayoutParams5.width = AndroidUtilities.dp(100.0f);
            windowLayoutParams5.height = AndroidUtilities.dp(150.0f);
            wm.addView(instance.windowRemoveTooltipOverlayView, windowLayoutParams5);
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
    public void setPosition(float xRelative2, float yRelative2) {
        float paddingHorizontal = (float) (-AndroidUtilities.dp(36.0f));
        float w = ((float) AndroidUtilities.displaySize.x) - (2.0f * paddingHorizontal);
        this.windowLayoutParams.x = (int) (((w - ((float) AndroidUtilities.dp(105.0f))) * xRelative2) + paddingHorizontal);
        this.windowLayoutParams.y = (int) (((float) (AndroidUtilities.displaySize.y - AndroidUtilities.dp(105.0f))) * yRelative2);
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
            WindowManager windowManager2 = groupCallPip2.windowManager;
            View windowView2 = groupCallPip2.windowView;
            View windowRemoveTooltipView2 = groupCallPip2.windowRemoveTooltipView;
            View windowRemoveTooltipOverlayView2 = groupCallPip2.windowRemoveTooltipOverlayView;
            final View view = windowView2;
            final View view2 = windowRemoveTooltipView2;
            final View view3 = windowRemoveTooltipOverlayView2;
            final WindowManager windowManager3 = windowManager2;
            final View view4 = groupCallPip2.alertContainer;
            groupCallPip2.windowView.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (view.getParent() != null) {
                        view.setVisibility(8);
                        view2.setVisibility(8);
                        view3.setVisibility(8);
                        windowManager3.removeView(view);
                        windowManager3.removeView(view2);
                        windowManager3.removeView(view3);
                        windowManager3.removeView(view4);
                    }
                }
            }).start();
            instance.onDestroy();
            instance = null;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        }
    }

    private static WindowManager.LayoutParams createWindowLayoutParams(Context context) {
        WindowManager.LayoutParams windowLayoutParams2 = new WindowManager.LayoutParams();
        windowLayoutParams2.height = AndroidUtilities.dp(105.0f);
        windowLayoutParams2.width = AndroidUtilities.dp(105.0f);
        windowLayoutParams2.gravity = 51;
        windowLayoutParams2.format = -3;
        if (!AndroidUtilities.checkInlinePermissions(context)) {
            windowLayoutParams2.type = 99;
        } else if (Build.VERSION.SDK_INT >= 26) {
            windowLayoutParams2.type = 2038;
        } else {
            windowLayoutParams2.type = 2003;
        }
        windowLayoutParams2.flags = 520;
        return windowLayoutParams2;
    }

    /* access modifiers changed from: package-private */
    public void showRemoveTooltip(boolean show) {
        if (this.animateToShowRemoveTooltip != show) {
            this.animateToShowRemoveTooltip = show;
            AnimatorSet animatorSet = this.showRemoveAnimator;
            if (animatorSet != null) {
                animatorSet.removeAllListeners();
                this.showRemoveAnimator.cancel();
            }
            if (show) {
                if (this.windowRemoveTooltipView.getVisibility() != 0) {
                    this.windowRemoveTooltipView.setVisibility(0);
                    this.removeTooltipView.setAlpha(0.0f);
                    this.removeTooltipView.setScaleX(0.5f);
                    this.removeTooltipView.setScaleY(0.5f);
                    this.deleteIcon.setCurrentFrame(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.showRemoveAnimator = animatorSet2;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.removeTooltipView, View.ALPHA, new float[]{this.removeTooltipView.getAlpha(), 1.0f}), ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, new float[]{this.removeTooltipView.getScaleX(), 1.0f}), ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, new float[]{this.removeTooltipView.getScaleY(), 1.0f})});
                this.showRemoveAnimator.setDuration(150).start();
                return;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.showRemoveAnimator = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.removeTooltipView, View.ALPHA, new float[]{this.removeTooltipView.getAlpha(), 0.0f}), ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, new float[]{this.removeTooltipView.getScaleX(), 0.5f}), ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, new float[]{this.removeTooltipView.getScaleY(), 0.5f})});
            this.showRemoveAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    GroupCallPip.this.windowRemoveTooltipView.setVisibility(8);
                    GroupCallPip.this.animateToPrepareRemove = false;
                    GroupCallPip.this.prepareToRemoveProgress = 0.0f;
                }
            });
            this.showRemoveAnimator.setDuration(150);
            this.showRemoveAnimator.start();
        }
    }

    /* access modifiers changed from: package-private */
    public void prepareToRemove(boolean prepare) {
        if (this.animateToPrepareRemove != prepare) {
            this.animateToPrepareRemove = prepare;
            this.removeTooltipView.invalidate();
            if (!this.removed) {
                this.deleteIcon.setCustomEndFrame(prepare ? 33 : 0);
                this.iconView.playAnimation();
            }
            if (prepare) {
                this.button.performHapticFeedback(3, 2);
            }
        }
        this.button.prepareToRemove(prepare);
    }

    /* access modifiers changed from: package-private */
    public void pinnedToCenter(final boolean pinned) {
        if (!this.removed && this.animateToPinnedToCenter != pinned) {
            this.animateToPinnedToCenter = pinned;
            ValueAnimator valueAnimator = this.pinAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.pinAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.pinnedProgress;
            fArr[1] = pinned ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.pinAnimator = ofFloat;
            ofFloat.addUpdateListener(new GroupCallPip$$ExternalSyntheticLambda0(this));
            this.pinAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (!GroupCallPip.this.removed) {
                        GroupCallPip.this.pinnedProgress = pinned ? 1.0f : 0.0f;
                        GroupCallPip.this.button.setPinnedProgress(GroupCallPip.this.pinnedProgress);
                        GroupCallPip.this.windowView.setScaleX(1.0f - (GroupCallPip.this.pinnedProgress * 0.6f));
                        GroupCallPip.this.windowView.setScaleY(1.0f - (GroupCallPip.this.pinnedProgress * 0.6f));
                        if (GroupCallPip.this.moving) {
                            GroupCallPip.this.updateButtonPosition();
                        }
                    }
                }
            });
            this.pinAnimator.setDuration(250);
            this.pinAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.pinAnimator.start();
        }
    }

    /* renamed from: lambda$pinnedToCenter$3$org-telegram-ui-Components-GroupCallPip  reason: not valid java name */
    public /* synthetic */ void m2319lambda$pinnedToCenter$3$orgtelegramuiComponentsGroupCallPip(ValueAnimator valueAnimator) {
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
        float cxRemove = ((((float) this.windowLeft) - this.windowOffsetLeft) + (((float) this.windowRemoveTooltipView.getMeasuredWidth()) / 2.0f)) - (((float) this.windowView.getMeasuredWidth()) / 2.0f);
        float cyRemove = (((((float) this.windowTop) - this.windowOffsetTop) + (((float) this.windowRemoveTooltipView.getMeasuredHeight()) / 2.0f)) - (((float) this.windowView.getMeasuredHeight()) / 2.0f)) - ((float) AndroidUtilities.dp(25.0f));
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        float f = this.windowX;
        float f2 = this.pinnedProgress;
        layoutParams.x = (int) ((f * (1.0f - f2)) + (f2 * cxRemove));
        WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
        float f3 = this.windowY;
        float f4 = this.pinnedProgress;
        layoutParams2.y = (int) ((f3 * (1.0f - f4)) + (f4 * cyRemove));
        updateAvatarsPosition();
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    /* access modifiers changed from: private */
    public void updateAvatarsPosition() {
        int parentWidth = AndroidUtilities.displaySize.x;
        float x = Math.min((float) Math.max(this.windowLayoutParams.x, -AndroidUtilities.dp(36.0f)), (float) ((parentWidth - this.windowView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f)));
        if (x < 0.0f) {
            this.avatarsImageView.setTranslationX(Math.abs(x) / 3.0f);
        } else if (x > ((float) (parentWidth - this.windowView.getMeasuredWidth()))) {
            this.avatarsImageView.setTranslationX((-Math.abs(x - ((float) (parentWidth - this.windowView.getMeasuredWidth())))) / 3.0f);
        } else {
            this.avatarsImageView.setTranslationX(0.0f);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.groupCallUpdated || id == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
            updateAvatars(true);
        } else if (id == NotificationCenter.didEndCall) {
            updateVisibility(ApplicationLoader.applicationContext);
        }
    }

    /* access modifiers changed from: private */
    public void getRelativePosition(float x, float y, float[] point2) {
        float paddingHorizontal = (float) (-AndroidUtilities.dp(36.0f));
        point2[0] = (x - paddingHorizontal) / ((((float) AndroidUtilities.displaySize.x) - (2.0f * paddingHorizontal)) - ((float) AndroidUtilities.dp(105.0f)));
        point2[1] = y / (((float) AndroidUtilities.displaySize.y) - ((float) AndroidUtilities.dp(105.0f)));
        point2[0] = Math.min(1.0f, Math.max(0.0f, point2[0]));
        point2[1] = Math.min(1.0f, Math.max(0.0f, point2[1]));
    }

    public static void updateVisibility(Context context) {
        boolean visible;
        VoIPService service = VoIPService.getSharedInstance();
        boolean groupCall = false;
        if (!(service == null || service.groupCall == null || service.isHangingUp())) {
            groupCall = true;
        }
        if (!AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
            visible = false;
        } else {
            visible = groupCall && !forceRemoved && (ApplicationLoader.mainInterfaceStopped || !GroupCallActivity.groupCallUiVisible);
        }
        if (visible) {
            show(context, service.getAccount());
            instance.showAvatars(true);
            return;
        }
        finish();
    }

    private void showAvatars(boolean show) {
        if (show != (this.avatarsImageView.getTag() != null)) {
            int i = null;
            this.avatarsImageView.animate().setListener((Animator.AnimatorListener) null).cancel();
            if (show) {
                if (this.avatarsImageView.getVisibility() != 0) {
                    this.avatarsImageView.setVisibility(0);
                    this.avatarsImageView.setAlpha(0.0f);
                    this.avatarsImageView.setScaleX(0.5f);
                    this.avatarsImageView.setScaleY(0.5f);
                }
                this.avatarsImageView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
            } else {
                this.avatarsImageView.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        GroupCallPip.this.avatarsImageView.setVisibility(8);
                    }
                }).start();
            }
            AvatarsImageView avatarsImageView2 = this.avatarsImageView;
            if (show) {
                i = 1;
            }
            avatarsImageView2.setTag(i);
        }
    }

    public static void clearForce() {
        forceRemoved = false;
    }

    public static boolean checkInlinePermissions() {
        if (Build.VERSION.SDK_INT < 23 || ApplicationLoader.canDrawOverlays) {
            return true;
        }
        return false;
    }
}
