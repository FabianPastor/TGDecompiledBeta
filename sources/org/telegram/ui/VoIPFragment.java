package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import java.io.ByteArrayOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$PhoneCall;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.DarkAlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.AcceptDeclineView;
import org.telegram.ui.Components.voip.PrivateVideoPreviewDialog;
import org.telegram.ui.Components.voip.VoIPButtonsLayout;
import org.telegram.ui.Components.voip.VoIPFloatingLayout;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Components.voip.VoIPNotificationsLayout;
import org.telegram.ui.Components.voip.VoIPOverlayBackground;
import org.telegram.ui.Components.voip.VoIPPiPView;
import org.telegram.ui.Components.voip.VoIPStatusTextView;
import org.telegram.ui.Components.voip.VoIPTextureView;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.Components.voip.VoIPWindowView;
import org.webrtc.EglBase;
import org.webrtc.GlRectDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;

public class VoIPFragment implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static VoIPFragment instance;
    /* access modifiers changed from: private */
    public AcceptDeclineView acceptDeclineView;
    private AccessibilityManager accessibilityManager;
    Activity activity;
    int animationIndex = -1;
    private ImageView backIcon;
    VoIPToggleButton[] bottomButtons = new VoIPToggleButton[4];
    View bottomShadow;
    private VoIPButtonsLayout buttonsLayout;
    TLRPC$User callingUser;
    boolean callingUserIsVideo;
    /* access modifiers changed from: private */
    public VoIPFloatingLayout callingUserMiniFloatingLayout;
    /* access modifiers changed from: private */
    public TextureViewRenderer callingUserMiniTextureRenderer;
    /* access modifiers changed from: private */
    public BackupImageView callingUserPhotoView;
    /* access modifiers changed from: private */
    public BackupImageView callingUserPhotoViewMini;
    /* access modifiers changed from: private */
    public VoIPTextureView callingUserTextureView;
    /* access modifiers changed from: private */
    public TextView callingUserTitle;
    boolean cameraForceExpanded;
    private Animator cameraShowingAnimator;
    /* access modifiers changed from: private */
    public boolean canHideUI;
    private boolean canSwitchToPip;
    /* access modifiers changed from: private */
    public boolean canZoomGesture;
    /* access modifiers changed from: private */
    public final int currentAccount;
    /* access modifiers changed from: private */
    public int currentState;
    /* access modifiers changed from: private */
    public VoIPFloatingLayout currentUserCameraFloatingLayout;
    /* access modifiers changed from: private */
    public boolean currentUserCameraIsFullscreen;
    boolean currentUserIsVideo;
    /* access modifiers changed from: private */
    public VoIPTextureView currentUserTextureView;
    private boolean deviceIsLocked;
    Emoji.EmojiDrawable[] emojiDrawables = new Emoji.EmojiDrawable[4];
    /* access modifiers changed from: private */
    public boolean emojiExpanded;
    LinearLayout emojiLayout;
    /* access modifiers changed from: private */
    public boolean emojiLoaded;
    TextView emojiRationalTextView;
    ImageView[] emojiViews = new ImageView[4];
    boolean enterFromPiP;
    private float enterTransitionProgress;
    boolean fillNaviagtionBar;
    float fillNaviagtionBarValue;
    /* access modifiers changed from: private */
    public ViewGroup fragmentView;
    Runnable hideUIRunnable = new VoIPFragment$$ExternalSyntheticLambda25(this);
    boolean hideUiRunnableWaiting;
    /* access modifiers changed from: private */
    public boolean isFinished;
    /* access modifiers changed from: private */
    public boolean isInPinchToZoomTouchMode;
    /* access modifiers changed from: private */
    public boolean isVideoCall;
    long lastContentTapTime;
    /* access modifiers changed from: private */
    public WindowInsets lastInsets;
    /* access modifiers changed from: private */
    public boolean lockOnScreen;
    ValueAnimator naviagtionBarAnimator;
    ValueAnimator.AnimatorUpdateListener navigationBarAnimationListener = new VoIPFragment$$ExternalSyntheticLambda0(this);
    VoIPNotificationsLayout notificationsLayout;
    /* access modifiers changed from: private */
    public VoIPOverlayBackground overlayBackground;
    Paint overlayBottomPaint = new Paint();
    Paint overlayPaint = new Paint();
    /* access modifiers changed from: private */
    public float pinchCenterX;
    /* access modifiers changed from: private */
    public float pinchCenterY;
    float pinchScale = 1.0f;
    /* access modifiers changed from: private */
    public float pinchStartCenterX;
    /* access modifiers changed from: private */
    public float pinchStartCenterY;
    /* access modifiers changed from: private */
    public float pinchStartDistance;
    /* access modifiers changed from: private */
    public float pinchTranslationX;
    /* access modifiers changed from: private */
    public float pinchTranslationY;
    /* access modifiers changed from: private */
    public int pointerId1;
    /* access modifiers changed from: private */
    public int pointerId2;
    /* access modifiers changed from: private */
    public PrivateVideoPreviewDialog previewDialog;
    /* access modifiers changed from: private */
    public int previousState;
    private boolean screenWasWakeup;
    private ImageView speakerPhoneIcon;
    LinearLayout statusLayout;
    private int statusLayoutAnimateToOffset;
    private VoIPStatusTextView statusTextView;
    ValueAnimator.AnimatorUpdateListener statusbarAnimatorListener = new VoIPFragment$$ExternalSyntheticLambda1(this);
    /* access modifiers changed from: private */
    public boolean switchingToPip;
    HintView tapToVideoTooltip;
    View topShadow;
    float touchSlop;
    float uiVisibilityAlpha = 1.0f;
    ValueAnimator uiVisibilityAnimator;
    /* access modifiers changed from: private */
    public boolean uiVisible = true;
    /* access modifiers changed from: private */
    public VoIPWindowView windowView;
    ValueAnimator zoomBackAnimator;
    /* access modifiers changed from: private */
    public boolean zoomStarted;

    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    public void onScreenOnChange(boolean z) {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
        this.uiVisibilityAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(ValueAnimator valueAnimator) {
        this.fillNaviagtionBarValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        this.hideUiRunnableWaiting = false;
        if (this.canHideUI && this.uiVisible && !this.emojiExpanded) {
            this.lastContentTapTime = System.currentTimeMillis();
            showUi(false);
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public static void show(Activity activity2, int i) {
        show(activity2, false, i);
    }

    public static void show(Activity activity2, boolean z, int i) {
        boolean z2;
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null && voIPFragment.windowView.getParent() == null) {
            VoIPFragment voIPFragment2 = instance;
            if (voIPFragment2 != null) {
                voIPFragment2.callingUserTextureView.renderer.release();
                instance.currentUserTextureView.renderer.release();
                instance.callingUserMiniTextureRenderer.release();
                instance.destroy();
            }
            instance = null;
        }
        if (instance == null && !activity2.isFinishing()) {
            boolean z3 = VoIPPiPView.getInstance() != null;
            if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().getUser() != null) {
                final VoIPFragment voIPFragment3 = new VoIPFragment(i);
                voIPFragment3.activity = activity2;
                instance = voIPFragment3;
                AnonymousClass1 r9 = new VoIPWindowView(activity2, !z3) {
                    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                        VoIPService sharedInstance;
                        if (voIPFragment3.isFinished || voIPFragment3.switchingToPip) {
                            return false;
                        }
                        int keyCode = keyEvent.getKeyCode();
                        if (keyCode == 4 && keyEvent.getAction() == 1 && !voIPFragment3.lockOnScreen) {
                            voIPFragment3.onBackPressed();
                            return true;
                        } else if ((keyCode != 25 && keyCode != 24) || voIPFragment3.currentState != 15 || (sharedInstance = VoIPService.getSharedInstance()) == null) {
                            return super.dispatchKeyEvent(keyEvent);
                        } else {
                            sharedInstance.stopRinging();
                            return true;
                        }
                    }
                };
                instance.deviceIsLocked = ((KeyguardManager) activity2.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
                PowerManager powerManager = (PowerManager) activity2.getSystemService("power");
                int i2 = Build.VERSION.SDK_INT;
                if (i2 >= 20) {
                    z2 = powerManager.isInteractive();
                } else {
                    z2 = powerManager.isScreenOn();
                }
                VoIPFragment voIPFragment4 = instance;
                voIPFragment4.screenWasWakeup = true ^ z2;
                r9.setLockOnScreen(voIPFragment4.deviceIsLocked);
                voIPFragment3.windowView = r9;
                if (i2 >= 20) {
                    r9.setOnApplyWindowInsetsListener(new VoIPFragment$$ExternalSyntheticLambda10(voIPFragment3));
                }
                WindowManager windowManager = (WindowManager) activity2.getSystemService("window");
                WindowManager.LayoutParams createWindowLayoutParams = r9.createWindowLayoutParams();
                if (z) {
                    if (i2 >= 26) {
                        createWindowLayoutParams.type = 2038;
                    } else {
                        createWindowLayoutParams.type = 2003;
                    }
                }
                windowManager.addView(r9, createWindowLayoutParams);
                r9.addView(voIPFragment3.createView(activity2));
                if (z3) {
                    voIPFragment3.enterTransitionProgress = 0.0f;
                    voIPFragment3.startTransitionFromPiP();
                    return;
                }
                voIPFragment3.enterTransitionProgress = 1.0f;
                voIPFragment3.updateSystemBarColors();
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ WindowInsets lambda$show$3(VoIPFragment voIPFragment, View view, WindowInsets windowInsets) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            voIPFragment.setInsets(windowInsets);
        }
        if (i >= 30) {
            return WindowInsets.CONSUMED;
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    /* access modifiers changed from: private */
    public void onBackPressed() {
        if (!this.isFinished && !this.switchingToPip) {
            PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
            if (privateVideoPreviewDialog != null) {
                privateVideoPreviewDialog.dismiss(false, false);
            } else if (this.callingUserIsVideo && this.currentUserIsVideo && this.cameraForceExpanded) {
                this.cameraForceExpanded = false;
                this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
                this.currentUserCameraIsFullscreen = false;
                this.previousState = this.currentState;
                updateViewState();
            } else if (this.emojiExpanded) {
                expandEmoji(false);
            } else if (this.emojiRationalTextView.getVisibility() == 8) {
                if (!this.canSwitchToPip || this.lockOnScreen) {
                    this.windowView.finish();
                } else if (AndroidUtilities.checkInlinePermissions(this.activity)) {
                    switchToPip();
                } else {
                    requestInlinePermissions();
                }
            }
        }
    }

    public static void clearInstance() {
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        if (instance != null) {
            if (VoIPService.getSharedInstance() != null) {
                int measuredHeight = instance.windowView.getMeasuredHeight();
                int i = Build.VERSION.SDK_INT;
                if (i >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                    measuredHeight -= windowInsets2.getSystemWindowInsetBottom();
                }
                VoIPFragment voIPFragment = instance;
                if (voIPFragment.canSwitchToPip) {
                    VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), measuredHeight, 0);
                    if (i >= 20 && (windowInsets = instance.lastInsets) != null) {
                        VoIPPiPView.topInset = windowInsets.getSystemWindowInsetTop();
                        VoIPPiPView.bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
                    }
                }
            }
            instance.callingUserTextureView.renderer.release();
            instance.currentUserTextureView.renderer.release();
            instance.callingUserMiniTextureRenderer.release();
            instance.destroy();
        }
        instance = null;
    }

    public static VoIPFragment getInstance() {
        return instance;
    }

    private void setInsets(WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        ((FrameLayout.LayoutParams) this.buttonsLayout.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.acceptDeclineView.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.backIcon.getLayoutParams()).topMargin = this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.speakerPhoneIcon.getLayoutParams()).topMargin = this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.topShadow.getLayoutParams()).topMargin = this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.statusLayout.getLayoutParams()).topMargin = AndroidUtilities.dp(68.0f) + this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.emojiLayout.getLayoutParams()).topMargin = AndroidUtilities.dp(17.0f) + this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.callingUserPhotoViewMini.getLayoutParams()).topMargin = AndroidUtilities.dp(68.0f) + this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.currentUserCameraFloatingLayout.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.callingUserMiniFloatingLayout.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.callingUserTextureView.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.notificationsLayout.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.bottomShadow.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        this.currentUserCameraFloatingLayout.setInsets(this.lastInsets);
        this.callingUserMiniFloatingLayout.setInsets(this.lastInsets);
        this.fragmentView.requestLayout();
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.setBottomPadding(this.lastInsets.getSystemWindowInsetBottom());
        }
    }

    public VoIPFragment(int i) {
        this.currentAccount = i;
        MessagesController.getInstance(i).getUser(Long.valueOf(UserConfig.getInstance(i).getClientUserId()));
        this.callingUser = VoIPService.getSharedInstance().getUser();
        VoIPService.getSharedInstance().registerStateListener(this);
        VoIPService.getSharedInstance().isOutgoing();
        this.previousState = -1;
        this.currentState = VoIPService.getSharedInstance().getCallState();
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
    }

    /* access modifiers changed from: private */
    public void destroy() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.unregisterStateListener(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
    }

    public void onStateChanged(int i) {
        int i2 = this.currentState;
        if (i2 != i) {
            this.previousState = i2;
            this.currentState = i;
            if (this.windowView != null) {
                updateViewState();
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.voipServiceCreated) {
            if (this.currentState == 17 && VoIPService.getSharedInstance() != null) {
                this.currentUserTextureView.renderer.release();
                this.callingUserTextureView.renderer.release();
                this.callingUserMiniTextureRenderer.release();
                initRenderers();
                VoIPService.getSharedInstance().registerStateListener(this);
            }
        } else if (i == NotificationCenter.emojiLoaded) {
            updateKeyView(true);
        } else if (i == NotificationCenter.closeInCallActivity) {
            this.windowView.finish();
        }
    }

    public void onSignalBarsCountChanged(int i) {
        VoIPStatusTextView voIPStatusTextView = this.statusTextView;
        if (voIPStatusTextView != null) {
            voIPStatusTextView.setSignalBarCount(i);
        }
    }

    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    public void onMediaStateUpdated(int i, int i2) {
        this.previousState = this.currentState;
        if (i2 == 2 && !this.isVideoCall) {
            this.isVideoCall = true;
        }
        updateViewState();
    }

    public void onCameraSwitch(boolean z) {
        this.previousState = this.currentState;
        updateViewState();
    }

    public void onVideoAvailableChange(boolean z) {
        this.previousState = this.currentState;
        if (z && !this.isVideoCall) {
            this.isVideoCall = true;
        }
        updateViewState();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        this.accessibilityManager = (AccessibilityManager) ContextCompat.getSystemService(context2, AccessibilityManager.class);
        AnonymousClass2 r8 = new FrameLayout(context2) {
            boolean check;
            long pressedTime;
            float pressedX;
            float pressedY;

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                int i = Build.VERSION.SDK_INT;
                if (i >= 20 && VoIPFragment.this.lastInsets != null) {
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) VoIPFragment.this.lastInsets.getSystemWindowInsetTop(), VoIPFragment.this.overlayPaint);
                }
                if (i >= 20 && VoIPFragment.this.lastInsets != null) {
                    canvas.drawRect(0.0f, (float) (getMeasuredHeight() - VoIPFragment.this.lastInsets.getSystemWindowInsetBottom()), (float) getMeasuredWidth(), (float) getMeasuredHeight(), VoIPFragment.this.overlayBottomPaint);
                }
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VoIPFragment.this.canZoomGesture || VoIPFragment.this.isInPinchToZoomTouchMode || VoIPFragment.this.zoomStarted || motionEvent.getActionMasked() == 0) {
                    if (motionEvent.getActionMasked() == 0) {
                        boolean unused = VoIPFragment.this.canZoomGesture = false;
                        boolean unused2 = VoIPFragment.this.isInPinchToZoomTouchMode = false;
                        boolean unused3 = VoIPFragment.this.zoomStarted = false;
                    }
                    VoIPTextureView access$1000 = VoIPFragment.this.getFullscreenTextureView();
                    if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
                        if (motionEvent.getActionMasked() == 0) {
                            RectF rectF = AndroidUtilities.rectTmp;
                            rectF.set(access$1000.getX(), access$1000.getY(), access$1000.getX() + ((float) access$1000.getMeasuredWidth()), access$1000.getY() + ((float) access$1000.getMeasuredHeight()));
                            rectF.inset(((((float) access$1000.getMeasuredHeight()) * access$1000.scaleTextureToFill) - ((float) access$1000.getMeasuredHeight())) / 2.0f, ((((float) access$1000.getMeasuredWidth()) * access$1000.scaleTextureToFill) - ((float) access$1000.getMeasuredWidth())) / 2.0f);
                            if (!GroupCallActivity.isLandscapeMode) {
                                rectF.top = Math.max(rectF.top, (float) ActionBar.getCurrentActionBarHeight());
                                rectF.bottom = Math.min(rectF.bottom, (float) (access$1000.getMeasuredHeight() - AndroidUtilities.dp(90.0f)));
                            } else {
                                rectF.top = Math.max(rectF.top, (float) ActionBar.getCurrentActionBarHeight());
                                rectF.right = Math.min(rectF.right, (float) (access$1000.getMeasuredWidth() - AndroidUtilities.dp(90.0f)));
                            }
                            boolean unused4 = VoIPFragment.this.canZoomGesture = rectF.contains(motionEvent.getX(), motionEvent.getY());
                            if (!VoIPFragment.this.canZoomGesture) {
                                VoIPFragment.this.finishZoom();
                            }
                        }
                        if (!VoIPFragment.this.isInPinchToZoomTouchMode && motionEvent.getPointerCount() == 2) {
                            float unused5 = VoIPFragment.this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                            VoIPFragment voIPFragment = VoIPFragment.this;
                            float unused6 = voIPFragment.pinchStartCenterX = voIPFragment.pinchCenterX = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
                            VoIPFragment voIPFragment2 = VoIPFragment.this;
                            float unused7 = voIPFragment2.pinchStartCenterY = voIPFragment2.pinchCenterY = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
                            VoIPFragment voIPFragment3 = VoIPFragment.this;
                            voIPFragment3.pinchScale = 1.0f;
                            int unused8 = voIPFragment3.pointerId1 = motionEvent.getPointerId(0);
                            int unused9 = VoIPFragment.this.pointerId2 = motionEvent.getPointerId(1);
                            boolean unused10 = VoIPFragment.this.isInPinchToZoomTouchMode = true;
                        }
                    } else if (motionEvent.getActionMasked() == 2 && VoIPFragment.this.isInPinchToZoomTouchMode) {
                        int i = -1;
                        int i2 = -1;
                        for (int i3 = 0; i3 < motionEvent.getPointerCount(); i3++) {
                            if (VoIPFragment.this.pointerId1 == motionEvent.getPointerId(i3)) {
                                i = i3;
                            }
                            if (VoIPFragment.this.pointerId2 == motionEvent.getPointerId(i3)) {
                                i2 = i3;
                            }
                        }
                        if (i == -1 || i2 == -1) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                            VoIPFragment.this.finishZoom();
                        }
                        VoIPFragment.this.pinchScale = ((float) Math.hypot((double) (motionEvent.getX(i2) - motionEvent.getX(i)), (double) (motionEvent.getY(i2) - motionEvent.getY(i)))) / VoIPFragment.this.pinchStartDistance;
                        VoIPFragment voIPFragment4 = VoIPFragment.this;
                        if (voIPFragment4.pinchScale > 1.005f && !voIPFragment4.zoomStarted) {
                            float unused11 = VoIPFragment.this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(i2) - motionEvent.getX(i)), (double) (motionEvent.getY(i2) - motionEvent.getY(i)));
                            VoIPFragment voIPFragment5 = VoIPFragment.this;
                            float unused12 = voIPFragment5.pinchStartCenterX = voIPFragment5.pinchCenterX = (motionEvent.getX(i) + motionEvent.getX(i2)) / 2.0f;
                            VoIPFragment voIPFragment6 = VoIPFragment.this;
                            float unused13 = voIPFragment6.pinchStartCenterY = voIPFragment6.pinchCenterY = (motionEvent.getY(i) + motionEvent.getY(i2)) / 2.0f;
                            VoIPFragment voIPFragment7 = VoIPFragment.this;
                            voIPFragment7.pinchScale = 1.0f;
                            float unused14 = voIPFragment7.pinchTranslationX = 0.0f;
                            float unused15 = VoIPFragment.this.pinchTranslationY = 0.0f;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            boolean unused16 = VoIPFragment.this.zoomStarted = true;
                            boolean unused17 = VoIPFragment.this.isInPinchToZoomTouchMode = true;
                        }
                        float access$1200 = VoIPFragment.this.pinchStartCenterX - ((motionEvent.getX(i) + motionEvent.getX(i2)) / 2.0f);
                        float access$1400 = VoIPFragment.this.pinchStartCenterY - ((motionEvent.getY(i) + motionEvent.getY(i2)) / 2.0f);
                        VoIPFragment voIPFragment8 = VoIPFragment.this;
                        float unused18 = voIPFragment8.pinchTranslationX = (-access$1200) / voIPFragment8.pinchScale;
                        VoIPFragment voIPFragment9 = VoIPFragment.this;
                        float unused19 = voIPFragment9.pinchTranslationY = (-access$1400) / voIPFragment9.pinchScale;
                        invalidate();
                    } else if (motionEvent.getActionMasked() == 1 || ((motionEvent.getActionMasked() == 6 && VoIPFragment.this.checkPointerIds(motionEvent)) || motionEvent.getActionMasked() == 3)) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        VoIPFragment.this.finishZoom();
                    }
                    VoIPFragment.this.fragmentView.invalidate();
                    int action = motionEvent.getAction();
                    if (action == 0) {
                        this.pressedX = motionEvent.getX();
                        this.pressedY = motionEvent.getY();
                        this.check = true;
                        this.pressedTime = System.currentTimeMillis();
                    } else if (action != 1) {
                        if (action == 3) {
                            this.check = false;
                        }
                    } else if (this.check) {
                        float x = motionEvent.getX() - this.pressedX;
                        float y = motionEvent.getY() - this.pressedY;
                        long currentTimeMillis = System.currentTimeMillis();
                        float f = (x * x) + (y * y);
                        VoIPFragment voIPFragment10 = VoIPFragment.this;
                        float f2 = voIPFragment10.touchSlop;
                        if (f < f2 * f2 && currentTimeMillis - this.pressedTime < 300 && currentTimeMillis - voIPFragment10.lastContentTapTime > 300) {
                            voIPFragment10.lastContentTapTime = System.currentTimeMillis();
                            if (VoIPFragment.this.emojiExpanded) {
                                VoIPFragment.this.expandEmoji(false);
                            } else if (VoIPFragment.this.canHideUI) {
                                VoIPFragment voIPFragment11 = VoIPFragment.this;
                                voIPFragment11.showUi(!voIPFragment11.uiVisible);
                                VoIPFragment voIPFragment12 = VoIPFragment.this;
                                int unused20 = voIPFragment12.previousState = voIPFragment12.currentState;
                                VoIPFragment.this.updateViewState();
                            }
                        }
                        this.check = false;
                    }
                    if (VoIPFragment.this.canZoomGesture || this.check) {
                        return true;
                    }
                    return false;
                }
                VoIPFragment.this.finishZoom();
                return false;
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view == VoIPFragment.this.callingUserPhotoView) {
                    VoIPFragment voIPFragment = VoIPFragment.this;
                    if (voIPFragment.currentUserIsVideo || voIPFragment.callingUserIsVideo) {
                        return false;
                    }
                }
                if ((view != VoIPFragment.this.callingUserPhotoView && view != VoIPFragment.this.callingUserTextureView && (view != VoIPFragment.this.currentUserCameraFloatingLayout || !VoIPFragment.this.currentUserCameraIsFullscreen)) || (!VoIPFragment.this.zoomStarted && VoIPFragment.this.zoomBackAnimator == null)) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                VoIPFragment voIPFragment2 = VoIPFragment.this;
                float f = voIPFragment2.pinchScale;
                canvas.scale(f, f, voIPFragment2.pinchCenterX, VoIPFragment.this.pinchCenterY);
                canvas.translate(VoIPFragment.this.pinchTranslationX, VoIPFragment.this.pinchTranslationY);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }
        };
        boolean z = false;
        r8.setClipToPadding(false);
        r8.setClipChildren(false);
        r8.setBackgroundColor(-16777216);
        updateSystemBarColors();
        this.fragmentView = r8;
        r8.setFitsSystemWindows(true);
        this.callingUserPhotoView = new BackupImageView(this, context2) {
            int blackoutColor = ColorUtils.setAlphaComponent(-16777216, 76);

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawColor(this.blackoutColor);
            }
        };
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, true, false, false);
        this.callingUserTextureView = voIPTextureView;
        TextureViewRenderer textureViewRenderer = voIPTextureView.renderer;
        RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
        textureViewRenderer.setScalingType(scalingType);
        this.callingUserTextureView.renderer.setEnableHardwareScaler(true);
        this.callingUserTextureView.renderer.setRotateTextureWithScreen(true);
        this.callingUserTextureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        r8.addView(this.callingUserPhotoView);
        r8.addView(this.callingUserTextureView);
        BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963});
        backgroundGradientDrawable.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT), new BackgroundGradientDrawable.ListenerAdapter() {
            public void onAllSizesReady() {
                VoIPFragment.this.callingUserPhotoView.invalidate();
            }
        });
        VoIPOverlayBackground voIPOverlayBackground = new VoIPOverlayBackground(context2);
        this.overlayBackground = voIPOverlayBackground;
        voIPOverlayBackground.setVisibility(8);
        this.callingUserPhotoView.getImageReceiver().setDelegate(new VoIPFragment$$ExternalSyntheticLambda29(this));
        this.callingUserPhotoView.setImage(ImageLocation.getForUserOrChat(this.callingUser, 0), (String) null, (Drawable) backgroundGradientDrawable, (Object) this.callingUser);
        VoIPFloatingLayout voIPFloatingLayout = new VoIPFloatingLayout(context2);
        this.currentUserCameraFloatingLayout = voIPFloatingLayout;
        voIPFloatingLayout.setDelegate(new VoIPFragment$$ExternalSyntheticLambda30(this));
        this.currentUserCameraFloatingLayout.setRelativePosition(1.0f, 1.0f);
        this.currentUserCameraIsFullscreen = true;
        VoIPTextureView voIPTextureView2 = new VoIPTextureView(context2, true, false);
        this.currentUserTextureView = voIPTextureView2;
        voIPTextureView2.renderer.setIsCamera(true);
        this.currentUserTextureView.renderer.setUseCameraRotation(true);
        this.currentUserCameraFloatingLayout.setOnTapListener(new VoIPFragment$$ExternalSyntheticLambda12(this));
        this.currentUserTextureView.renderer.setMirror(true);
        this.currentUserCameraFloatingLayout.addView(this.currentUserTextureView);
        VoIPFloatingLayout voIPFloatingLayout2 = new VoIPFloatingLayout(context2);
        this.callingUserMiniFloatingLayout = voIPFloatingLayout2;
        voIPFloatingLayout2.alwaysFloating = true;
        voIPFloatingLayout2.setFloatingMode(true, false);
        TextureViewRenderer textureViewRenderer2 = new TextureViewRenderer(context2);
        this.callingUserMiniTextureRenderer = textureViewRenderer2;
        textureViewRenderer2.setEnableHardwareScaler(true);
        this.callingUserMiniTextureRenderer.setIsCamera(false);
        this.callingUserMiniTextureRenderer.setFpsReduction(30.0f);
        this.callingUserMiniTextureRenderer.setScalingType(scalingType);
        View view = new View(context2);
        view.setBackgroundColor(-14999773);
        this.callingUserMiniFloatingLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f));
        this.callingUserMiniFloatingLayout.addView(this.callingUserMiniTextureRenderer, LayoutHelper.createFrame(-1, -2, 17));
        this.callingUserMiniFloatingLayout.setOnTapListener(new VoIPFragment$$ExternalSyntheticLambda16(this));
        this.callingUserMiniFloatingLayout.setVisibility(8);
        r8.addView(this.currentUserCameraFloatingLayout, LayoutHelper.createFrame(-2, -2.0f));
        r8.addView(this.callingUserMiniFloatingLayout);
        r8.addView(this.overlayBackground);
        View view2 = new View(context2);
        this.bottomShadow = view2;
        view2.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 127)}));
        r8.addView(this.bottomShadow, LayoutHelper.createFrame(-1, 140, 80));
        View view3 = new View(context2);
        this.topShadow = view3;
        view3.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ColorUtils.setAlphaComponent(-16777216, 102), 0}));
        r8.addView(this.topShadow, LayoutHelper.createFrame(-1, 140, 48));
        AnonymousClass5 r1 = new LinearLayout(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setVisibleToUser(VoIPFragment.this.emojiLoaded);
            }
        };
        this.emojiLayout = r1;
        r1.setOrientation(0);
        this.emojiLayout.setPadding(0, 0, 0, AndroidUtilities.dp(30.0f));
        this.emojiLayout.setClipToPadding(false);
        this.emojiLayout.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda14(this));
        TextView textView = new TextView(context2);
        this.emojiRationalTextView = textView;
        textView.setText(LocaleController.formatString("CallEmojiKeyTooltip", NUM, UserObject.getFirstName(this.callingUser)));
        this.emojiRationalTextView.setTextSize(1, 16.0f);
        this.emojiRationalTextView.setTextColor(-1);
        this.emojiRationalTextView.setGravity(17);
        this.emojiRationalTextView.setVisibility(8);
        int i = 0;
        while (i < 4) {
            this.emojiViews[i] = new ImageView(context2);
            this.emojiViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
            this.emojiLayout.addView(this.emojiViews[i], LayoutHelper.createLinear(22, 22, i == 0 ? 0.0f : 4.0f, 0.0f, 0.0f, 0.0f));
            i++;
        }
        AnonymousClass6 r12 = new LinearLayout(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                CharSequence text = VoIPFragment.this.callingUserTitle.getText();
                if (sharedInstance != null && !TextUtils.isEmpty(text)) {
                    StringBuilder sb = new StringBuilder(text);
                    sb.append(", ");
                    TLRPC$PhoneCall tLRPC$PhoneCall = sharedInstance.privateCall;
                    if (tLRPC$PhoneCall == null || !tLRPC$PhoneCall.video) {
                        sb.append(LocaleController.getString("VoipInCallBranding", NUM));
                    } else {
                        sb.append(LocaleController.getString("VoipInVideoCallBranding", NUM));
                    }
                    long callDuration = sharedInstance.getCallDuration();
                    if (callDuration > 0) {
                        sb.append(", ");
                        sb.append(LocaleController.formatDuration((int) (callDuration / 1000)));
                    }
                    accessibilityNodeInfo.setText(sb);
                }
            }
        };
        this.statusLayout = r12;
        r12.setOrientation(1);
        this.statusLayout.setFocusable(true);
        this.statusLayout.setFocusableInTouchMode(true);
        BackupImageView backupImageView = new BackupImageView(context2);
        this.callingUserPhotoViewMini = backupImageView;
        backupImageView.setImage(ImageLocation.getForUserOrChat(this.callingUser, 1), (String) null, (Drawable) Theme.createCircleDrawable(AndroidUtilities.dp(135.0f), -16777216), (Object) this.callingUser);
        this.callingUserPhotoViewMini.setRoundRadius(AndroidUtilities.dp(135.0f) / 2);
        this.callingUserPhotoViewMini.setVisibility(8);
        TextView textView2 = new TextView(context2);
        this.callingUserTitle = textView2;
        textView2.setTextSize(1, 24.0f);
        TextView textView3 = this.callingUserTitle;
        TLRPC$User tLRPC$User = this.callingUser;
        textView3.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
        this.callingUserTitle.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        this.callingUserTitle.setTextColor(-1);
        this.callingUserTitle.setGravity(1);
        this.callingUserTitle.setImportantForAccessibility(2);
        this.statusLayout.addView(this.callingUserTitle, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 6));
        VoIPStatusTextView voIPStatusTextView = new VoIPStatusTextView(context2);
        this.statusTextView = voIPStatusTextView;
        ViewCompat.setImportantForAccessibility(voIPStatusTextView, 4);
        this.statusLayout.addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 6));
        this.statusLayout.setClipChildren(false);
        this.statusLayout.setClipToPadding(false);
        this.statusLayout.setPadding(0, 0, 0, AndroidUtilities.dp(15.0f));
        r8.addView(this.callingUserPhotoViewMini, LayoutHelper.createFrame(135, 135.0f, 1, 0.0f, 68.0f, 0.0f, 0.0f));
        r8.addView(this.statusLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 68.0f, 0.0f, 0.0f));
        r8.addView(this.emojiLayout, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 17.0f, 0.0f, 0.0f));
        r8.addView(this.emojiRationalTextView, LayoutHelper.createFrame(-1, -2.0f, 17, 24.0f, 32.0f, 24.0f, 0.0f));
        this.buttonsLayout = new VoIPButtonsLayout(context2);
        for (int i2 = 0; i2 < 4; i2++) {
            this.bottomButtons[i2] = new VoIPToggleButton(context2);
            this.buttonsLayout.addView(this.bottomButtons[i2]);
        }
        AcceptDeclineView acceptDeclineView2 = new AcceptDeclineView(context2);
        this.acceptDeclineView = acceptDeclineView2;
        acceptDeclineView2.setListener(new AcceptDeclineView.Listener() {
            public void onAccept() {
                if (VoIPFragment.this.currentState == 17) {
                    Intent intent = new Intent(VoIPFragment.this.activity, VoIPService.class);
                    intent.putExtra("user_id", VoIPFragment.this.callingUser.id);
                    intent.putExtra("is_outgoing", true);
                    intent.putExtra("start_incall_activity", false);
                    intent.putExtra("video_call", VoIPFragment.this.isVideoCall);
                    intent.putExtra("can_video_call", VoIPFragment.this.isVideoCall);
                    intent.putExtra("account", VoIPFragment.this.currentAccount);
                    try {
                        VoIPFragment.this.activity.startService(intent);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                } else if (Build.VERSION.SDK_INT >= 23 && VoIPFragment.this.activity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                    VoIPFragment.this.activity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().acceptIncomingCall();
                    if (VoIPFragment.this.currentUserIsVideo) {
                        VoIPService.getSharedInstance().requestVideoCall(false);
                    }
                }
            }

            public void onDecline() {
                if (VoIPFragment.this.currentState == 17) {
                    VoIPFragment.this.windowView.finish();
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall();
                }
            }
        });
        this.acceptDeclineView.setScreenWasWakeup(this.screenWasWakeup);
        r8.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, -2, 80));
        r8.addView(this.acceptDeclineView, LayoutHelper.createFrame(-1, 186, 80));
        ImageView imageView = new ImageView(context2);
        this.backIcon = imageView;
        imageView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.backIcon.setImageResource(NUM);
        this.backIcon.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.backIcon.setContentDescription(LocaleController.getString("Back", NUM));
        r8.addView(this.backIcon, LayoutHelper.createFrame(56, 56, 51));
        AnonymousClass8 r13 = new ImageView(this, context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setClassName(ToggleButton.class.getName());
                accessibilityNodeInfo.setCheckable(true);
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                if (sharedInstance != null) {
                    accessibilityNodeInfo.setChecked(sharedInstance.isSpeakerphoneOn());
                }
            }
        };
        this.speakerPhoneIcon = r13;
        r13.setContentDescription(LocaleController.getString("VoipSpeaker", NUM));
        this.speakerPhoneIcon.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.speakerPhoneIcon.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
        r8.addView(this.speakerPhoneIcon, LayoutHelper.createFrame(56, 56, 53));
        this.speakerPhoneIcon.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda17(this));
        this.backIcon.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda11(this));
        if (this.windowView.isLockOnScreen()) {
            this.backIcon.setVisibility(8);
        }
        VoIPNotificationsLayout voIPNotificationsLayout = new VoIPNotificationsLayout(context2);
        this.notificationsLayout = voIPNotificationsLayout;
        voIPNotificationsLayout.setGravity(80);
        this.notificationsLayout.setOnViewsUpdated(new VoIPFragment$$ExternalSyntheticLambda27(this));
        r8.addView(this.notificationsLayout, LayoutHelper.createFrame(-1, 200.0f, 80, 16.0f, 0.0f, 16.0f, 0.0f));
        HintView hintView = new HintView(context2, 4);
        this.tapToVideoTooltip = hintView;
        hintView.setText(LocaleController.getString("TapToTurnCamera", NUM));
        r8.addView(this.tapToVideoTooltip, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 8.0f));
        this.tapToVideoTooltip.setBottomOffset(AndroidUtilities.dp(4.0f));
        this.tapToVideoTooltip.setVisibility(8);
        updateViewState();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (!this.isVideoCall) {
                TLRPC$PhoneCall tLRPC$PhoneCall = sharedInstance.privateCall;
                if (tLRPC$PhoneCall != null && tLRPC$PhoneCall.video) {
                    z = true;
                }
                this.isVideoCall = z;
            }
            initRenderers();
        }
        return r8;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (bitmapSafe != null) {
            this.overlayBackground.setBackground(bitmapSafe);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(float f, boolean z) {
        this.currentUserTextureView.setScreenshareMiniProgress(f, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(View view) {
        if (this.currentUserIsVideo && this.callingUserIsVideo && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.callingUserMiniFloatingLayout.setRelativePosition(this.currentUserCameraFloatingLayout);
            this.currentUserCameraIsFullscreen = true;
            this.cameraForceExpanded = true;
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(View view) {
        if (this.cameraForceExpanded && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
            this.currentUserCameraIsFullscreen = false;
            this.cameraForceExpanded = false;
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(View view) {
        if (System.currentTimeMillis() - this.lastContentTapTime >= 500) {
            this.lastContentTapTime = System.currentTimeMillis();
            if (this.emojiLoaded) {
                expandEmoji(!this.emojiExpanded);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(View view) {
        if (this.speakerPhoneIcon.getTag() != null && VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10(View view) {
        if (!this.lockOnScreen) {
            onBackPressed();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11() {
        this.previousState = this.currentState;
        updateViewState();
    }

    /* access modifiers changed from: private */
    public boolean checkPointerIds(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == motionEvent.getPointerId(0) && this.pointerId2 == motionEvent.getPointerId(1)) {
            return true;
        }
        if (this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public VoIPTextureView getFullscreenTextureView() {
        if (this.callingUserIsVideo) {
            return this.callingUserTextureView;
        }
        return this.currentUserTextureView;
    }

    /* access modifiers changed from: private */
    public void finishZoom() {
        if (this.zoomStarted) {
            this.zoomStarted = false;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            this.zoomBackAnimator = ofFloat;
            ofFloat.addUpdateListener(new VoIPFragment$$ExternalSyntheticLambda2(this, this.pinchScale, this.pinchTranslationX, this.pinchTranslationY));
            this.zoomBackAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPFragment voIPFragment = VoIPFragment.this;
                    voIPFragment.zoomBackAnimator = null;
                    voIPFragment.pinchScale = 1.0f;
                    float unused = voIPFragment.pinchTranslationX = 0.0f;
                    float unused2 = VoIPFragment.this.pinchTranslationY = 0.0f;
                    VoIPFragment.this.fragmentView.invalidate();
                }
            });
            this.zoomBackAnimator.setDuration(350);
            this.zoomBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.zoomBackAnimator.start();
        }
        this.canZoomGesture = false;
        this.isInPinchToZoomTouchMode = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$finishZoom$12(float f, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinchScale = (f * floatValue) + ((1.0f - floatValue) * 1.0f);
        this.pinchTranslationX = f2 * floatValue;
        this.pinchTranslationY = f3 * floatValue;
        this.fragmentView.invalidate();
    }

    private void initRenderers() {
        this.currentUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            public void onFrameResolutionChanged(int i, int i2, int i3) {
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onFirstFrameRendered$0() {
                VoIPFragment.this.updateViewState();
            }

            public void onFirstFrameRendered() {
                AndroidUtilities.runOnUIThread(new VoIPFragment$10$$ExternalSyntheticLambda0(this));
            }
        });
        this.callingUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            public void onFrameResolutionChanged(int i, int i2, int i3) {
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onFirstFrameRendered$0() {
                VoIPFragment.this.updateViewState();
            }

            public void onFirstFrameRendered() {
                AndroidUtilities.runOnUIThread(new VoIPFragment$11$$ExternalSyntheticLambda0(this));
            }
        }, EglBase.CONFIG_PLAIN, new GlRectDrawer());
        this.callingUserMiniTextureRenderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), (RendererCommon.RendererEvents) null);
    }

    public void switchToPip() {
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        if (!this.isFinished && AndroidUtilities.checkInlinePermissions(this.activity) && instance != null) {
            this.isFinished = true;
            if (VoIPService.getSharedInstance() != null) {
                int measuredHeight = instance.windowView.getMeasuredHeight();
                int i = Build.VERSION.SDK_INT;
                if (i >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                    measuredHeight -= windowInsets2.getSystemWindowInsetBottom();
                }
                VoIPFragment voIPFragment = instance;
                VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), measuredHeight, 1);
                if (i >= 20 && (windowInsets = instance.lastInsets) != null) {
                    VoIPPiPView.topInset = windowInsets.getSystemWindowInsetTop();
                    VoIPPiPView.bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
                }
            }
            if (VoIPPiPView.getInstance() != null) {
                ViewPropertyAnimator duration = this.speakerPhoneIcon.animate().alpha(0.0f).setDuration(150);
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                duration.setInterpolator(cubicBezierInterpolator).start();
                this.backIcon.animate().alpha(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
                this.emojiLayout.animate().alpha(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
                this.statusLayout.animate().alpha(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
                this.buttonsLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
                this.bottomShadow.animate().alpha(0.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
                this.topShadow.animate().alpha(0.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
                this.callingUserMiniFloatingLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
                this.notificationsLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
                VoIPPiPView.switchingToPip = true;
                this.switchingToPip = true;
                Animator createPiPTransition = createPiPTransition(false);
                this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
                createPiPTransition.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPPiPView.getInstance().windowView.setAlpha(1.0f);
                        AndroidUtilities.runOnUIThread(new VoIPFragment$12$$ExternalSyntheticLambda0(this), 200);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onAnimationEnd$0() {
                        NotificationCenter.getInstance(VoIPFragment.this.currentAccount).onAnimationFinish(VoIPFragment.this.animationIndex);
                        VoIPPiPView.getInstance().onTransitionEnd();
                        VoIPFragment.this.currentUserCameraFloatingLayout.setCornerRadius(-1.0f);
                        VoIPFragment.this.callingUserTextureView.renderer.release();
                        VoIPFragment.this.currentUserTextureView.renderer.release();
                        VoIPFragment.this.callingUserMiniTextureRenderer.release();
                        VoIPFragment.this.destroy();
                        VoIPFragment.this.windowView.finishImmediate();
                        VoIPPiPView.switchingToPip = false;
                        boolean unused = VoIPFragment.this.switchingToPip = false;
                        VoIPFragment unused2 = VoIPFragment.instance = null;
                    }
                });
                createPiPTransition.setDuration(350);
                createPiPTransition.setInterpolator(cubicBezierInterpolator);
                createPiPTransition.start();
            }
        }
    }

    public void startTransitionFromPiP() {
        this.enterFromPiP = true;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && sharedInstance.getVideoState(false) == 2) {
            this.callingUserTextureView.setStub(VoIPPiPView.getInstance().callingUserTextureView);
            this.currentUserTextureView.setStub(VoIPPiPView.getInstance().currentUserTextureView);
        }
        this.windowView.setAlpha(0.0f);
        updateViewState();
        this.switchingToPip = true;
        VoIPPiPView.switchingToPip = true;
        VoIPPiPView.prepareForTransition();
        this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
        AndroidUtilities.runOnUIThread(new VoIPFragment$$ExternalSyntheticLambda23(this), 32);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startTransitionFromPiP$14() {
        this.windowView.setAlpha(1.0f);
        Animator createPiPTransition = createPiPTransition(true);
        this.backIcon.setAlpha(0.0f);
        this.emojiLayout.setAlpha(0.0f);
        this.statusLayout.setAlpha(0.0f);
        this.buttonsLayout.setAlpha(0.0f);
        this.bottomShadow.setAlpha(0.0f);
        this.topShadow.setAlpha(0.0f);
        this.speakerPhoneIcon.setAlpha(0.0f);
        this.notificationsLayout.setAlpha(0.0f);
        this.callingUserPhotoView.setAlpha(0.0f);
        this.currentUserCameraFloatingLayout.switchingToPip = true;
        AndroidUtilities.runOnUIThread(new VoIPFragment$$ExternalSyntheticLambda28(this, createPiPTransition), 32);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startTransitionFromPiP$13(Animator animator) {
        VoIPPiPView.switchingToPip = false;
        VoIPPiPView.finish();
        ViewPropertyAnimator duration = this.speakerPhoneIcon.animate().setDuration(150);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        duration.setInterpolator(cubicBezierInterpolator).start();
        this.backIcon.animate().alpha(1.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
        this.emojiLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
        this.statusLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
        this.buttonsLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
        this.bottomShadow.animate().alpha(1.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
        this.topShadow.animate().alpha(1.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
        this.notificationsLayout.animate().alpha(1.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
        this.callingUserPhotoView.animate().alpha(1.0f).setDuration(350).setInterpolator(cubicBezierInterpolator).start();
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                NotificationCenter.getInstance(VoIPFragment.this.currentAccount).onAnimationFinish(VoIPFragment.this.animationIndex);
                VoIPFragment.this.currentUserCameraFloatingLayout.setCornerRadius(-1.0f);
                boolean unused = VoIPFragment.this.switchingToPip = false;
                VoIPFragment.this.currentUserCameraFloatingLayout.switchingToPip = false;
                VoIPFragment voIPFragment = VoIPFragment.this;
                int unused2 = voIPFragment.previousState = voIPFragment.currentState;
                VoIPFragment.this.updateViewState();
            }
        });
        animator.setDuration(350);
        animator.setInterpolator(cubicBezierInterpolator);
        animator.start();
    }

    public Animator createPiPTransition(boolean z) {
        float f;
        float f2;
        float f3;
        boolean z2;
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        this.currentUserCameraFloatingLayout.animate().cancel();
        float f9 = (float) (VoIPPiPView.getInstance().windowLayoutParams.x + VoIPPiPView.getInstance().xOffset);
        float var_ = (float) (VoIPPiPView.getInstance().windowLayoutParams.y + VoIPPiPView.getInstance().yOffset);
        float x = this.currentUserCameraFloatingLayout.getX();
        float y = this.currentUserCameraFloatingLayout.getY();
        float scaleX = this.currentUserCameraFloatingLayout.getScaleX();
        float var_ = VoIPPiPView.isExpanding() ? 0.4f : 0.25f;
        float measuredWidth = f9 - ((((float) this.callingUserTextureView.getMeasuredWidth()) - (((float) this.callingUserTextureView.getMeasuredWidth()) * var_)) / 2.0f);
        float measuredHeight = var_ - ((((float) this.callingUserTextureView.getMeasuredHeight()) - (((float) this.callingUserTextureView.getMeasuredHeight()) * var_)) / 2.0f);
        if (this.callingUserIsVideo) {
            int measuredWidth2 = this.currentUserCameraFloatingLayout.getMeasuredWidth();
            if (!this.currentUserIsVideo || measuredWidth2 == 0) {
                f8 = 1.0f;
                f7 = 1.0f;
                z2 = false;
                f6 = 0.0f;
            } else {
                f6 = (((float) this.windowView.getMeasuredWidth()) / ((float) measuredWidth2)) * var_ * 0.4f;
                f8 = (((f9 - ((((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) * f6)) / 2.0f)) + (((float) VoIPPiPView.getInstance().parentWidth) * var_)) - ((((float) VoIPPiPView.getInstance().parentWidth) * var_) * 0.4f)) - ((float) AndroidUtilities.dp(4.0f));
                f7 = (((var_ - ((((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) * f6)) / 2.0f)) + (((float) VoIPPiPView.getInstance().parentHeight) * var_)) - ((((float) VoIPPiPView.getInstance().parentHeight) * var_) * 0.4f)) - ((float) AndroidUtilities.dp(4.0f));
                z2 = true;
            }
            f2 = f8;
            f = f7;
            f3 = f6;
        } else {
            f2 = f9 - ((((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) * var_)) / 2.0f);
            f = var_ - ((((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) * var_)) / 2.0f);
            f3 = var_;
            z2 = true;
        }
        float dp = this.callingUserIsVideo ? (float) AndroidUtilities.dp(4.0f) : 0.0f;
        float dp2 = (((float) AndroidUtilities.dp(4.0f)) * 1.0f) / f3;
        if (this.callingUserIsVideo) {
            f4 = VoIPPiPView.isExpanding() ? 1.0f : 0.0f;
        } else {
            f4 = 1.0f;
        }
        if (z) {
            if (z2) {
                this.currentUserCameraFloatingLayout.setScaleX(f3);
                this.currentUserCameraFloatingLayout.setScaleY(f3);
                this.currentUserCameraFloatingLayout.setTranslationX(f2);
                this.currentUserCameraFloatingLayout.setTranslationY(f);
                this.currentUserCameraFloatingLayout.setCornerRadius(dp2);
                this.currentUserCameraFloatingLayout.setAlpha(f4);
            }
            this.callingUserTextureView.setScaleX(var_);
            this.callingUserTextureView.setScaleY(var_);
            this.callingUserTextureView.setTranslationX(measuredWidth);
            this.callingUserTextureView.setTranslationY(measuredHeight);
            this.callingUserTextureView.setRoundCorners((((float) AndroidUtilities.dp(6.0f)) * 1.0f) / var_);
            f5 = 0.0f;
            this.callingUserPhotoView.setAlpha(0.0f);
            this.callingUserPhotoView.setScaleX(var_);
            this.callingUserPhotoView.setScaleY(var_);
            this.callingUserPhotoView.setTranslationX(measuredWidth);
            this.callingUserPhotoView.setTranslationY(measuredHeight);
        } else {
            f5 = 0.0f;
        }
        float[] fArr = new float[2];
        fArr[0] = z ? 1.0f : 0.0f;
        fArr[1] = z ? 0.0f : 1.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        if (!z) {
            f5 = 1.0f;
        }
        this.enterTransitionProgress = f5;
        updateSystemBarColors();
        ValueAnimator valueAnimator = ofFloat;
        VoIPFragment$$ExternalSyntheticLambda3 voIPFragment$$ExternalSyntheticLambda3 = r0;
        VoIPFragment$$ExternalSyntheticLambda3 voIPFragment$$ExternalSyntheticLambda32 = new VoIPFragment$$ExternalSyntheticLambda3(this, z2, scaleX, f3, x, f2, y, f, dp, dp2, 1.0f, f4, 1.0f, var_, 0.0f, measuredWidth, 0.0f, measuredHeight);
        ValueAnimator valueAnimator2 = valueAnimator;
        valueAnimator2.addUpdateListener(voIPFragment$$ExternalSyntheticLambda3);
        return valueAnimator2;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPiPTransition$15(boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float var_, float var_, float var_, float var_, float var_, float var_, float var_, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float var_ = 1.0f - floatValue;
        this.enterTransitionProgress = var_;
        updateSystemBarColors();
        if (z) {
            float var_ = (f * var_) + (f2 * floatValue);
            this.currentUserCameraFloatingLayout.setScaleX(var_);
            this.currentUserCameraFloatingLayout.setScaleY(var_);
            this.currentUserCameraFloatingLayout.setTranslationX((f3 * var_) + (f4 * floatValue));
            this.currentUserCameraFloatingLayout.setTranslationY((f5 * var_) + (f6 * floatValue));
            this.currentUserCameraFloatingLayout.setCornerRadius((f7 * var_) + (f8 * floatValue));
            this.currentUserCameraFloatingLayout.setAlpha((f9 * var_) + (var_ * floatValue));
        }
        float var_ = (var_ * var_) + (var_ * floatValue);
        this.callingUserTextureView.setScaleX(var_);
        this.callingUserTextureView.setScaleY(var_);
        float var_ = (var_ * var_) + (var_ * floatValue);
        float var_ = (var_ * var_) + (var_ * floatValue);
        this.callingUserTextureView.setTranslationX(var_);
        this.callingUserTextureView.setTranslationY(var_);
        this.callingUserTextureView.setRoundCorners(((((float) AndroidUtilities.dp(4.0f)) * floatValue) * 1.0f) / var_);
        if (!this.currentUserCameraFloatingLayout.measuredAsFloatingMode) {
            this.currentUserTextureView.setScreenshareMiniProgress(floatValue, false);
        }
        this.callingUserPhotoView.setScaleX(var_);
        this.callingUserPhotoView.setScaleY(var_);
        this.callingUserPhotoView.setTranslationX(var_);
        this.callingUserPhotoView.setTranslationY(var_);
        this.callingUserPhotoView.setAlpha(var_);
    }

    /* access modifiers changed from: private */
    public void expandEmoji(boolean z) {
        if (this.emojiLoaded && this.emojiExpanded != z && this.uiVisible) {
            this.emojiExpanded = z;
            if (z) {
                AndroidUtilities.runOnUIThread(this.hideUIRunnable);
                this.hideUiRunnableWaiting = false;
                float measuredWidth = ((float) (this.windowView.getMeasuredWidth() - AndroidUtilities.dp(128.0f))) / ((float) this.emojiLayout.getMeasuredWidth());
                this.emojiLayout.animate().scaleX(measuredWidth).scaleY(measuredWidth).translationY((((float) this.windowView.getHeight()) / 2.0f) - ((float) this.emojiLayout.getBottom())).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(250).start();
                this.emojiRationalTextView.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (this.emojiRationalTextView.getVisibility() != 0) {
                    this.emojiRationalTextView.setVisibility(0);
                    this.emojiRationalTextView.setAlpha(0.0f);
                }
                this.emojiRationalTextView.animate().alpha(1.0f).setDuration(150).start();
                this.overlayBackground.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (this.overlayBackground.getVisibility() != 0) {
                    this.overlayBackground.setVisibility(0);
                    this.overlayBackground.setAlpha(0.0f);
                    this.overlayBackground.setShowBlackout(this.currentUserIsVideo || this.callingUserIsVideo, false);
                }
                this.overlayBackground.animate().alpha(1.0f).setDuration(150).start();
                return;
            }
            this.emojiLayout.animate().scaleX(1.0f).scaleY(1.0f).translationY(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(150).start();
            if (this.emojiRationalTextView.getVisibility() != 8) {
                this.emojiRationalTextView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPService sharedInstance = VoIPService.getSharedInstance();
                        if (VoIPFragment.this.canHideUI && !VoIPFragment.this.hideUiRunnableWaiting && sharedInstance != null && !sharedInstance.isMicMute()) {
                            AndroidUtilities.runOnUIThread(VoIPFragment.this.hideUIRunnable, 3000);
                            VoIPFragment.this.hideUiRunnableWaiting = true;
                        }
                        VoIPFragment.this.emojiRationalTextView.setVisibility(8);
                    }
                }).setDuration(150).start();
                this.overlayBackground.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPFragment.this.overlayBackground.setVisibility(8);
                    }
                }).setDuration(150).start();
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004b, code lost:
        r11 = true;
     */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x05a7  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0259 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x025a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateViewState() {
        /*
            r19 = this;
            r0 = r19
            boolean r1 = r0.isFinished
            if (r1 != 0) goto L_0x069c
            boolean r1 = r0.switchingToPip
            if (r1 == 0) goto L_0x000c
            goto L_0x069c
        L_0x000c:
            r1 = 0
            r0.lockOnScreen = r1
            int r2 = r0.previousState
            r3 = -1
            r4 = 1
            if (r2 == r3) goto L_0x0017
            r2 = 1
            goto L_0x0018
        L_0x0017:
            r2 = 0
        L_0x0018:
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r5 = r0.currentState
            r6 = 5
            r7 = 3
            r8 = 2
            r9 = 0
            if (r5 == r4) goto L_0x0242
            if (r5 == r8) goto L_0x0242
            if (r5 == r7) goto L_0x0230
            r10 = 4
            if (r5 == r10) goto L_0x00f1
            if (r5 == r6) goto L_0x0230
            switch(r5) {
                case 11: goto L_0x00e0;
                case 12: goto L_0x00d0;
                case 13: goto L_0x00c0;
                case 14: goto L_0x00b0;
                case 15: goto L_0x005e;
                case 16: goto L_0x004e;
                case 17: goto L_0x0032;
                default: goto L_0x0030;
            }
        L_0x0030:
            goto L_0x0250
        L_0x0032:
            org.telegram.ui.Components.voip.VoIPStatusTextView r5 = r0.statusTextView
            r10 = 2131628507(0x7f0e11db, float:1.8884309E38)
            java.lang.String r11 = "VoipBusy"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r5.setText(r10, r1, r2)
            org.telegram.ui.Components.voip.AcceptDeclineView r5 = r0.acceptDeclineView
            r5.setRetryMod(r4)
            r0.currentUserIsVideo = r1
            r0.callingUserIsVideo = r1
            r5 = 0
        L_0x004a:
            r10 = 0
        L_0x004b:
            r11 = 1
            goto L_0x0253
        L_0x004e:
            org.telegram.ui.Components.voip.VoIPStatusTextView r5 = r0.statusTextView
            r10 = 2131628725(0x7f0e12b5, float:1.888475E38)
            java.lang.String r11 = "VoipRinging"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r5.setText(r10, r4, r2)
            goto L_0x0250
        L_0x005e:
            r0.lockOnScreen = r4
            r5 = 1103101952(0x41CLASSNAME, float:24.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            org.telegram.ui.Components.voip.AcceptDeclineView r10 = r0.acceptDeclineView
            r10.setRetryMod(r1)
            if (r3 == 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$PhoneCall r10 = r3.privateCall
            boolean r10 = r10.video
            if (r10 == 0) goto L_0x009c
            boolean r10 = r0.currentUserIsVideo
            if (r10 == 0) goto L_0x007f
            org.telegram.tgnet.TLRPC$User r10 = r0.callingUser
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r10.photo
            if (r10 == 0) goto L_0x007f
            r10 = 1
            goto L_0x0080
        L_0x007f:
            r10 = 0
        L_0x0080:
            org.telegram.ui.Components.voip.VoIPStatusTextView r11 = r0.statusTextView
            r12 = 2131628678(0x7f0e1286, float:1.8884655E38)
            java.lang.String r13 = "VoipInVideoCallBranding"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r11.setText(r12, r4, r2)
            org.telegram.ui.Components.voip.AcceptDeclineView r11 = r0.acceptDeclineView
            r12 = 1114636288(0x42700000, float:60.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = -r12
            float r12 = (float) r12
            r11.setTranslationY(r12)
            goto L_0x004b
        L_0x009c:
            org.telegram.ui.Components.voip.VoIPStatusTextView r10 = r0.statusTextView
            r11 = 2131628676(0x7f0e1284, float:1.8884651E38)
            java.lang.String r12 = "VoipInCallBranding"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setText(r11, r4, r2)
            org.telegram.ui.Components.voip.AcceptDeclineView r10 = r0.acceptDeclineView
            r10.setTranslationY(r9)
            goto L_0x004a
        L_0x00b0:
            org.telegram.ui.Components.voip.VoIPStatusTextView r5 = r0.statusTextView
            r10 = 2131628724(0x7f0e12b4, float:1.8884749E38)
            java.lang.String r11 = "VoipRequesting"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r5.setText(r10, r4, r2)
            goto L_0x0250
        L_0x00c0:
            org.telegram.ui.Components.voip.VoIPStatusTextView r5 = r0.statusTextView
            r10 = 2131628751(0x7f0e12cf, float:1.8884804E38)
            java.lang.String r11 = "VoipWaiting"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r5.setText(r10, r4, r2)
            goto L_0x0250
        L_0x00d0:
            org.telegram.ui.Components.voip.VoIPStatusTextView r5 = r0.statusTextView
            r10 = 2131628566(0x7f0e1216, float:1.8884428E38)
            java.lang.String r11 = "VoipExchangingKeys"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r5.setText(r10, r4, r2)
            goto L_0x0250
        L_0x00e0:
            org.telegram.ui.Components.voip.VoIPTextureView r5 = r0.currentUserTextureView
            r5.saveCameraLastBitmap()
            org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda24 r5 = new org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda24
            r5.<init>(r0)
            r10 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5, r10)
            goto L_0x0250
        L_0x00f1:
            org.telegram.ui.Components.voip.VoIPStatusTextView r5 = r0.statusTextView
            java.lang.String r10 = "VoipFailed"
            r11 = 2131628567(0x7f0e1217, float:1.888443E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r10, r11)
            r5.setText(r12, r1, r2)
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            java.lang.String r12 = "ERROR_UNKNOWN"
            if (r5 == 0) goto L_0x010c
            java.lang.String r5 = r5.getLastError()
            goto L_0x010d
        L_0x010c:
            r5 = r12
        L_0x010d:
            boolean r12 = android.text.TextUtils.equals(r5, r12)
            r13 = 1000(0x3e8, double:4.94E-321)
            if (r12 != 0) goto L_0x0227
            java.lang.String r12 = "ERROR_INCOMPATIBLE"
            boolean r12 = android.text.TextUtils.equals(r5, r12)
            if (r12 == 0) goto L_0x013d
            org.telegram.tgnet.TLRPC$User r5 = r0.callingUser
            java.lang.String r10 = r5.first_name
            java.lang.String r5 = r5.last_name
            java.lang.String r5 = org.telegram.messenger.ContactsController.formatName(r10, r5)
            r10 = 2131628709(0x7f0e12a5, float:1.8884718E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r11[r1] = r5
            java.lang.String r5 = "VoipPeerIncompatible"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r10, r11)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
            r0.showErrorDialog(r5)
            goto L_0x0250
        L_0x013d:
            java.lang.String r12 = "ERROR_PEER_OUTDATED"
            boolean r12 = android.text.TextUtils.equals(r5, r12)
            if (r12 == 0) goto L_0x01c6
            boolean r5 = r0.isVideoCall
            if (r5 == 0) goto L_0x01aa
            org.telegram.tgnet.TLRPC$User r5 = r0.callingUser
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r5)
            r12 = 2131628711(0x7f0e12a7, float:1.8884722E38)
            java.lang.Object[] r13 = new java.lang.Object[r4]
            r13[r1] = r5
            java.lang.String r5 = "VoipPeerVideoOutdated"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r12, r13)
            boolean[] r12 = new boolean[r4]
            org.telegram.ui.ActionBar.DarkAlertDialog$Builder r13 = new org.telegram.ui.ActionBar.DarkAlertDialog$Builder
            android.app.Activity r14 = r0.activity
            r13.<init>(r14)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)
            org.telegram.ui.ActionBar.AlertDialog$Builder r10 = r13.setTitle(r10)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = r10.setMessage(r5)
            r10 = 2131624697(0x7f0e02f9, float:1.8876581E38)
            java.lang.String r11 = "Cancel"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda5 r11 = new org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda5
            r11.<init>(r0)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = r5.setNegativeButton(r10, r11)
            r10 = 2131628712(0x7f0e12a8, float:1.8884724E38)
            java.lang.String r11 = "VoipPeerVideoOutdatedMakeVoice"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda7 r11 = new org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda7
            r11.<init>(r0, r12)
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = r5.setPositiveButton(r10, r11)
            org.telegram.ui.ActionBar.AlertDialog r5 = r5.show()
            r5.setCanceledOnTouchOutside(r4)
            org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda9 r10 = new org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda9
            r10.<init>(r0, r12)
            r5.setOnDismissListener(r10)
            goto L_0x0250
        L_0x01aa:
            org.telegram.tgnet.TLRPC$User r5 = r0.callingUser
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r5)
            r10 = 2131628710(0x7f0e12a6, float:1.888472E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r11[r1] = r5
            java.lang.String r5 = "VoipPeerOutdated"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r10, r11)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
            r0.showErrorDialog(r5)
            goto L_0x0250
        L_0x01c6:
            java.lang.String r10 = "ERROR_PRIVACY"
            boolean r10 = android.text.TextUtils.equals(r5, r10)
            if (r10 == 0) goto L_0x01ed
            org.telegram.tgnet.TLRPC$User r5 = r0.callingUser
            java.lang.String r10 = r5.first_name
            java.lang.String r5 = r5.last_name
            java.lang.String r5 = org.telegram.messenger.ContactsController.formatName(r10, r5)
            r10 = 2131624676(0x7f0e02e4, float:1.8876538E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r11[r1] = r5
            java.lang.String r5 = "CallNotAvailable"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r10, r11)
            android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
            r0.showErrorDialog(r5)
            goto L_0x0250
        L_0x01ed:
            java.lang.String r10 = "ERROR_AUDIO_IO"
            boolean r10 = android.text.TextUtils.equals(r5, r10)
            if (r10 == 0) goto L_0x01fb
            java.lang.String r5 = "Error initializing audio hardware"
            r0.showErrorDialog(r5)
            goto L_0x0250
        L_0x01fb:
            java.lang.String r10 = "ERROR_LOCALIZED"
            boolean r10 = android.text.TextUtils.equals(r5, r10)
            if (r10 == 0) goto L_0x0209
            org.telegram.ui.Components.voip.VoIPWindowView r5 = r0.windowView
            r5.finish()
            goto L_0x0250
        L_0x0209:
            java.lang.String r10 = "ERROR_CONNECTION_SERVICE"
            boolean r5 = android.text.TextUtils.equals(r5, r10)
            if (r5 == 0) goto L_0x021e
            r5 = 2131628565(0x7f0e1215, float:1.8884426E38)
            java.lang.String r10 = "VoipErrorUnknown"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r5)
            r0.showErrorDialog(r5)
            goto L_0x0250
        L_0x021e:
            org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda21 r5 = new org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda21
            r5.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5, r13)
            goto L_0x0250
        L_0x0227:
            org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda26 r5 = new org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda26
            r5.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5, r13)
            goto L_0x0250
        L_0x0230:
            r0.updateKeyView(r2)
            int r5 = r0.currentState
            if (r5 != r6) goto L_0x023d
            r5 = 0
            r10 = 0
            r11 = 0
            r12 = 1
            r13 = 1
            goto L_0x0255
        L_0x023d:
            r5 = 0
            r10 = 0
            r11 = 0
            r12 = 1
            goto L_0x0254
        L_0x0242:
            org.telegram.ui.Components.voip.VoIPStatusTextView r5 = r0.statusTextView
            r10 = 2131628557(0x7f0e120d, float:1.888441E38)
            java.lang.String r11 = "VoipConnecting"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r5.setText(r10, r4, r2)
        L_0x0250:
            r5 = 0
            r10 = 0
            r11 = 0
        L_0x0253:
            r12 = 0
        L_0x0254:
            r13 = 0
        L_0x0255:
            org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r14 = r0.previewDialog
            if (r14 == 0) goto L_0x025a
            return
        L_0x025a:
            if (r3 == 0) goto L_0x0281
            int r14 = r3.getRemoteVideoState()
            if (r14 != r8) goto L_0x0264
            r14 = 1
            goto L_0x0265
        L_0x0264:
            r14 = 0
        L_0x0265:
            r0.callingUserIsVideo = r14
            int r14 = r3.getVideoState(r1)
            if (r14 == r8) goto L_0x0276
            int r14 = r3.getVideoState(r1)
            if (r14 != r4) goto L_0x0274
            goto L_0x0276
        L_0x0274:
            r14 = 0
            goto L_0x0277
        L_0x0276:
            r14 = 1
        L_0x0277:
            r0.currentUserIsVideo = r14
            if (r14 == 0) goto L_0x0281
            boolean r14 = r0.isVideoCall
            if (r14 != 0) goto L_0x0281
            r0.isVideoCall = r4
        L_0x0281:
            if (r2 == 0) goto L_0x028d
            org.telegram.ui.Components.voip.VoIPFloatingLayout r14 = r0.currentUserCameraFloatingLayout
            r14.saveRelativePosition()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r14 = r0.callingUserMiniFloatingLayout
            r14.saveRelativePosition()
        L_0x028d:
            boolean r14 = r0.callingUserIsVideo
            r6 = 250(0xfa, double:1.235E-321)
            r15 = 1065353216(0x3var_, float:1.0)
            if (r14 == 0) goto L_0x02d0
            boolean r14 = r0.switchingToPip
            if (r14 != 0) goto L_0x029e
            org.telegram.ui.Components.BackupImageView r14 = r0.callingUserPhotoView
            r14.setAlpha(r15)
        L_0x029e:
            if (r2 == 0) goto L_0x02b2
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r14 = r14.animate()
            android.view.ViewPropertyAnimator r14 = r14.alpha(r15)
            android.view.ViewPropertyAnimator r14 = r14.setDuration(r6)
            r14.start()
            goto L_0x02c0
        L_0x02b2:
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r14 = r14.animate()
            r14.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            r14.setAlpha(r15)
        L_0x02c0:
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            org.webrtc.TextureViewRenderer r14 = r14.renderer
            boolean r14 = r14.isFirstFrameRendered()
            if (r14 != 0) goto L_0x02d0
            boolean r14 = r0.enterFromPiP
            if (r14 != 0) goto L_0x02d0
            r0.callingUserIsVideo = r1
        L_0x02d0:
            boolean r14 = r0.currentUserIsVideo
            if (r14 != 0) goto L_0x0304
            boolean r14 = r0.callingUserIsVideo
            if (r14 == 0) goto L_0x02d9
            goto L_0x0304
        L_0x02d9:
            r0.fillNavigationBar(r1, r2)
            org.telegram.ui.Components.BackupImageView r14 = r0.callingUserPhotoView
            r14.setVisibility(r1)
            if (r2 == 0) goto L_0x02f5
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r14 = r14.animate()
            android.view.ViewPropertyAnimator r14 = r14.alpha(r9)
            android.view.ViewPropertyAnimator r6 = r14.setDuration(r6)
            r6.start()
            goto L_0x0307
        L_0x02f5:
            org.telegram.ui.Components.voip.VoIPTextureView r6 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r6 = r6.animate()
            r6.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r6 = r0.callingUserTextureView
            r6.setAlpha(r9)
            goto L_0x0307
        L_0x0304:
            r0.fillNavigationBar(r4, r2)
        L_0x0307:
            boolean r6 = r0.currentUserIsVideo
            if (r6 == 0) goto L_0x030f
            boolean r7 = r0.callingUserIsVideo
            if (r7 != 0) goto L_0x0311
        L_0x030f:
            r0.cameraForceExpanded = r1
        L_0x0311:
            if (r6 == 0) goto L_0x0319
            boolean r6 = r0.cameraForceExpanded
            if (r6 == 0) goto L_0x0319
            r6 = 1
            goto L_0x031a
        L_0x0319:
            r6 = 0
        L_0x031a:
            r0.showCallingUserAvatarMini(r10, r2)
            org.telegram.ui.Components.BackupImageView r7 = r0.callingUserPhotoViewMini
            java.lang.Object r7 = r7.getTag()
            if (r7 != 0) goto L_0x0327
            r7 = 0
            goto L_0x0334
        L_0x0327:
            r7 = 1124532224(0x43070000, float:135.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r10 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r7 = r7 + r10
        L_0x0334:
            int r5 = r5 + r7
            r0.showAcceptDeclineView(r11, r2)
            org.telegram.ui.Components.voip.VoIPWindowView r7 = r0.windowView
            boolean r10 = r0.lockOnScreen
            if (r10 != 0) goto L_0x0345
            boolean r10 = r0.deviceIsLocked
            if (r10 == 0) goto L_0x0343
            goto L_0x0345
        L_0x0343:
            r10 = 0
            goto L_0x0346
        L_0x0345:
            r10 = 1
        L_0x0346:
            r7.setLockOnScreen(r10)
            int r7 = r0.currentState
            r10 = 3
            if (r7 != r10) goto L_0x0358
            boolean r7 = r0.currentUserIsVideo
            if (r7 != 0) goto L_0x0356
            boolean r7 = r0.callingUserIsVideo
            if (r7 == 0) goto L_0x0358
        L_0x0356:
            r7 = 1
            goto L_0x0359
        L_0x0358:
            r7 = 0
        L_0x0359:
            r0.canHideUI = r7
            if (r7 != 0) goto L_0x0364
            boolean r7 = r0.uiVisible
            if (r7 != 0) goto L_0x0364
            r0.showUi(r4)
        L_0x0364:
            boolean r7 = r0.uiVisible
            if (r7 == 0) goto L_0x0382
            boolean r7 = r0.canHideUI
            if (r7 == 0) goto L_0x0382
            boolean r7 = r0.hideUiRunnableWaiting
            if (r7 != 0) goto L_0x0382
            if (r3 == 0) goto L_0x0382
            boolean r7 = r3.isMicMute()
            if (r7 != 0) goto L_0x0382
            java.lang.Runnable r7 = r0.hideUIRunnable
            r10 = 3000(0xbb8, double:1.482E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7, r10)
            r0.hideUiRunnableWaiting = r4
            goto L_0x0391
        L_0x0382:
            if (r3 == 0) goto L_0x0391
            boolean r7 = r3.isMicMute()
            if (r7 == 0) goto L_0x0391
            java.lang.Runnable r7 = r0.hideUIRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r7)
            r0.hideUiRunnableWaiting = r1
        L_0x0391:
            boolean r7 = r0.uiVisible
            if (r7 != 0) goto L_0x039c
            r7 = 1112014848(0x42480000, float:50.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
        L_0x039c:
            r7 = 1117782016(0x42a00000, float:80.0)
            r10 = 1098907648(0x41800000, float:16.0)
            r14 = r5
            r4 = 150(0x96, double:7.4E-322)
            if (r2 == 0) goto L_0x0404
            boolean r11 = r0.lockOnScreen
            if (r11 != 0) goto L_0x03bc
            boolean r11 = r0.uiVisible
            if (r11 != 0) goto L_0x03ae
            goto L_0x03bc
        L_0x03ae:
            android.widget.ImageView r11 = r0.backIcon
            android.view.ViewPropertyAnimator r11 = r11.animate()
            android.view.ViewPropertyAnimator r11 = r11.alpha(r15)
            r11.start()
            goto L_0x03db
        L_0x03bc:
            android.widget.ImageView r11 = r0.backIcon
            int r11 = r11.getVisibility()
            if (r11 == 0) goto L_0x03ce
            android.widget.ImageView r11 = r0.backIcon
            r11.setVisibility(r1)
            android.widget.ImageView r11 = r0.backIcon
            r11.setAlpha(r9)
        L_0x03ce:
            android.widget.ImageView r11 = r0.backIcon
            android.view.ViewPropertyAnimator r11 = r11.animate()
            android.view.ViewPropertyAnimator r11 = r11.alpha(r9)
            r11.start()
        L_0x03db:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r11 = r0.notificationsLayout
            android.view.ViewPropertyAnimator r11 = r11.animate()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            boolean r15 = r0.uiVisible
            if (r15 == 0) goto L_0x03ef
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x03f0
        L_0x03ef:
            r7 = 0
        L_0x03f0:
            int r10 = r10 - r7
            float r7 = (float) r10
            android.view.ViewPropertyAnimator r7 = r11.translationY(r7)
            android.view.ViewPropertyAnimator r7 = r7.setDuration(r4)
            org.telegram.ui.Components.CubicBezierInterpolator r10 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r7 = r7.setInterpolator(r10)
            r7.start()
            goto L_0x0430
        L_0x0404:
            boolean r11 = r0.lockOnScreen
            if (r11 != 0) goto L_0x040d
            android.widget.ImageView r11 = r0.backIcon
            r11.setVisibility(r1)
        L_0x040d:
            android.widget.ImageView r11 = r0.backIcon
            boolean r15 = r0.lockOnScreen
            if (r15 == 0) goto L_0x0415
            r15 = 0
            goto L_0x0417
        L_0x0415:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x0417:
            r11.setAlpha(r15)
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r11 = r0.notificationsLayout
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            boolean r15 = r0.uiVisible
            if (r15 == 0) goto L_0x042a
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x042b
        L_0x042a:
            r7 = 0
        L_0x042b:
            int r10 = r10 - r7
            float r7 = (float) r10
            r11.setTranslationY(r7)
        L_0x0430:
            int r7 = r0.currentState
            r10 = 10
            r11 = 11
            if (r7 == r10) goto L_0x043d
            if (r7 == r11) goto L_0x043d
            r0.updateButtons(r2)
        L_0x043d:
            if (r12 == 0) goto L_0x0444
            org.telegram.ui.Components.voip.VoIPStatusTextView r7 = r0.statusTextView
            r7.showTimer(r2)
        L_0x0444:
            org.telegram.ui.Components.voip.VoIPStatusTextView r7 = r0.statusTextView
            r7.showReconnect(r13, r2)
            if (r2 == 0) goto L_0x0468
            int r7 = r0.statusLayoutAnimateToOffset
            if (r14 == r7) goto L_0x046e
            android.widget.LinearLayout r7 = r0.statusLayout
            android.view.ViewPropertyAnimator r7 = r7.animate()
            float r10 = (float) r14
            android.view.ViewPropertyAnimator r7 = r7.translationY(r10)
            android.view.ViewPropertyAnimator r7 = r7.setDuration(r4)
            org.telegram.ui.Components.CubicBezierInterpolator r10 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r7 = r7.setInterpolator(r10)
            r7.start()
            goto L_0x046e
        L_0x0468:
            android.widget.LinearLayout r7 = r0.statusLayout
            float r10 = (float) r14
            r7.setTranslationY(r10)
        L_0x046e:
            r0.statusLayoutAnimateToOffset = r14
            org.telegram.ui.Components.voip.VoIPOverlayBackground r7 = r0.overlayBackground
            boolean r10 = r0.currentUserIsVideo
            if (r10 != 0) goto L_0x047d
            boolean r10 = r0.callingUserIsVideo
            if (r10 == 0) goto L_0x047b
            goto L_0x047d
        L_0x047b:
            r10 = 0
            goto L_0x047e
        L_0x047d:
            r10 = 1
        L_0x047e:
            r7.setShowBlackout(r10, r2)
            int r7 = r0.currentState
            if (r7 == r11) goto L_0x0493
            r10 = 17
            if (r7 == r10) goto L_0x0493
            boolean r7 = r0.currentUserIsVideo
            if (r7 != 0) goto L_0x0491
            boolean r7 = r0.callingUserIsVideo
            if (r7 == 0) goto L_0x0493
        L_0x0491:
            r11 = 1
            goto L_0x0494
        L_0x0493:
            r11 = 0
        L_0x0494:
            r0.canSwitchToPip = r11
            r7 = 0
            if (r3 == 0) goto L_0x05ac
            boolean r10 = r0.currentUserIsVideo
            if (r10 == 0) goto L_0x04a2
            org.telegram.messenger.voip.VoIPService$SharedUIParams r10 = r3.sharedUIParams
            r11 = 1
            r10.tapToVideoTooltipWasShowed = r11
        L_0x04a2:
            org.telegram.ui.Components.voip.VoIPTextureView r10 = r0.currentUserTextureView
            boolean r12 = r3.isScreencast()
            r10.setIsScreencast(r12)
            org.telegram.ui.Components.voip.VoIPTextureView r10 = r0.currentUserTextureView
            org.webrtc.TextureViewRenderer r10 = r10.renderer
            boolean r12 = r3.isFrontFaceCamera()
            r10.setMirror(r12)
            boolean r10 = r0.currentUserIsVideo
            if (r10 == 0) goto L_0x04c5
            boolean r10 = r3.isScreencast()
            if (r10 != 0) goto L_0x04c5
            org.telegram.ui.Components.voip.VoIPTextureView r10 = r0.currentUserTextureView
            org.webrtc.TextureViewRenderer r10 = r10.renderer
            goto L_0x04c6
        L_0x04c5:
            r10 = r7
        L_0x04c6:
            if (r6 == 0) goto L_0x04cb
            org.webrtc.TextureViewRenderer r12 = r0.callingUserMiniTextureRenderer
            goto L_0x04cf
        L_0x04cb:
            org.telegram.ui.Components.voip.VoIPTextureView r12 = r0.callingUserTextureView
            org.webrtc.TextureViewRenderer r12 = r12.renderer
        L_0x04cf:
            r3.setSinks(r10, r12)
            if (r2 == 0) goto L_0x04d9
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r10 = r0.notificationsLayout
            r10.beforeLayoutChanges()
        L_0x04d9:
            boolean r10 = r0.currentUserIsVideo
            java.lang.String r12 = "VoipUserMicrophoneIsOff"
            r13 = 2131165330(0x7var_, float:1.7944874E38)
            java.lang.String r14 = "video"
            java.lang.String r15 = "muted"
            if (r10 != 0) goto L_0x04ea
            boolean r10 = r0.callingUserIsVideo
            if (r10 == 0) goto L_0x0548
        L_0x04ea:
            int r10 = r0.currentState
            r11 = 3
            if (r10 == r11) goto L_0x04f2
            r11 = 5
            if (r10 != r11) goto L_0x0548
        L_0x04f2:
            long r10 = r3.getCallDuration()
            r16 = 500(0x1f4, double:2.47E-321)
            int r18 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r18 <= 0) goto L_0x0548
            int r10 = r3.getRemoteAudioState()
            if (r10 != 0) goto L_0x051a
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r10 = r0.notificationsLayout
            r11 = 1
            java.lang.Object[] r4 = new java.lang.Object[r11]
            r5 = 2131628741(0x7f0e12c5, float:1.8884783E38)
            org.telegram.tgnet.TLRPC$User r11 = r0.callingUser
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)
            r4[r1] = r11
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r12, r5, r4)
            r10.addNotification(r13, r4, r15, r2)
            goto L_0x051f
        L_0x051a:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r4.removeNotification(r15)
        L_0x051f:
            int r4 = r3.getRemoteVideoState()
            if (r4 != 0) goto L_0x0542
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r5 = 2131165321(0x7var_, float:1.7944856E38)
            r10 = 2131628740(0x7f0e12c4, float:1.8884781E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            org.telegram.tgnet.TLRPC$User r13 = r0.callingUser
            java.lang.String r13 = org.telegram.messenger.UserObject.getFirstName(r13)
            r12[r1] = r13
            java.lang.String r13 = "VoipUserCameraIsOff"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r13, r10, r12)
            r4.addNotification(r5, r10, r14, r2)
            goto L_0x0571
        L_0x0542:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r4.removeNotification(r14)
            goto L_0x0571
        L_0x0548:
            int r4 = r3.getRemoteAudioState()
            if (r4 != 0) goto L_0x0567
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r5 = 2131628741(0x7f0e12c5, float:1.8884783E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r10 = r11
            org.telegram.tgnet.TLRPC$User r11 = r0.callingUser
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)
            r10[r1] = r11
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r5, r10)
            r4.addNotification(r13, r5, r15, r2)
            goto L_0x056c
        L_0x0567:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r4.removeNotification(r15)
        L_0x056c:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r4.removeNotification(r14)
        L_0x0571:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            int r4 = r4.getChildCount()
            if (r4 != 0) goto L_0x0598
            boolean r4 = r0.callingUserIsVideo
            if (r4 == 0) goto L_0x0598
            org.telegram.tgnet.TLRPC$PhoneCall r4 = r3.privateCall
            if (r4 == 0) goto L_0x0598
            boolean r4 = r4.video
            if (r4 != 0) goto L_0x0598
            org.telegram.messenger.voip.VoIPService$SharedUIParams r3 = r3.sharedUIParams
            boolean r4 = r3.tapToVideoTooltipWasShowed
            if (r4 != 0) goto L_0x0598
            r4 = 1
            r3.tapToVideoTooltipWasShowed = r4
            org.telegram.ui.Components.HintView r3 = r0.tapToVideoTooltip
            org.telegram.ui.Components.voip.VoIPToggleButton[] r5 = r0.bottomButtons
            r5 = r5[r4]
            r3.showForView(r5, r4)
            goto L_0x05a5
        L_0x0598:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r3 = r0.notificationsLayout
            int r3 = r3.getChildCount()
            if (r3 == 0) goto L_0x05a5
            org.telegram.ui.Components.HintView r3 = r0.tapToVideoTooltip
            r3.hide()
        L_0x05a5:
            if (r2 == 0) goto L_0x05ac
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r3 = r0.notificationsLayout
            r3.animateLayoutChanges()
        L_0x05ac:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r3 = r0.notificationsLayout
            int r3 = r3.getChildsHight()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r4 = r0.callingUserMiniFloatingLayout
            r4.setBottomOffset(r3, r2)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r4 = r0.currentUserCameraFloatingLayout
            r4.setBottomOffset(r3, r2)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.currentUserCameraFloatingLayout
            boolean r4 = r0.uiVisible
            r3.setUiVisible(r4)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            boolean r4 = r0.uiVisible
            r3.setUiVisible(r4)
            boolean r3 = r0.currentUserIsVideo
            if (r3 == 0) goto L_0x05e1
            boolean r3 = r0.callingUserIsVideo
            if (r3 == 0) goto L_0x05dc
            boolean r3 = r0.cameraForceExpanded
            if (r3 == 0) goto L_0x05d7
            goto L_0x05dc
        L_0x05d7:
            r0.showFloatingLayout(r8, r2)
            r3 = 1
            goto L_0x05e5
        L_0x05dc:
            r3 = 1
            r0.showFloatingLayout(r3, r2)
            goto L_0x05e5
        L_0x05e1:
            r3 = 1
            r0.showFloatingLayout(r1, r2)
        L_0x05e5:
            r2 = 1056964608(0x3var_, float:0.5)
            if (r6 == 0) goto L_0x0651
            org.telegram.ui.Components.voip.VoIPFloatingLayout r4 = r0.callingUserMiniFloatingLayout
            java.lang.Object r4 = r4.getTag()
            if (r4 != 0) goto L_0x0651
            org.telegram.ui.Components.voip.VoIPFloatingLayout r4 = r0.callingUserMiniFloatingLayout
            r4.setIsActive(r3)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            int r3 = r3.getVisibility()
            if (r3 == 0) goto L_0x0612
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            r3.setVisibility(r1)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.setAlpha(r9)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.setScaleX(r2)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.setScaleY(r2)
        L_0x0612:
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r7)
            r1.cancel()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r2 = 1065353216(0x3var_, float:1.0)
            android.view.ViewPropertyAnimator r1 = r1.alpha(r2)
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r2)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r2)
            r2 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r1 = r1.setInterpolator(r4)
            android.view.ViewPropertyAnimator r1 = r1.setStartDelay(r2)
            r1.start()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r2 = 1
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1.setTag(r2)
            goto L_0x068f
        L_0x0651:
            if (r6 != 0) goto L_0x068f
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x068f
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            r3.setIsActive(r1)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r9)
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r2)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r2)
            org.telegram.ui.VoIPFragment$16 r2 = new org.telegram.ui.VoIPFragment$16
            r2.<init>()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r2)
            r2 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r1 = r1.setInterpolator(r2)
            r1.start()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.setTag(r7)
        L_0x068f:
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.currentUserCameraFloatingLayout
            r1.restoreRelativePosition()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.restoreRelativePosition()
            r19.updateSpeakerPhoneIcon()
        L_0x069c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPFragment.updateViewState():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateViewState$16() {
        this.windowView.finish();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateViewState$17(DialogInterface dialogInterface, int i) {
        this.windowView.finish();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateViewState$18(boolean[] zArr, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        this.currentState = 17;
        Intent intent = new Intent(this.activity, VoIPService.class);
        intent.putExtra("user_id", this.callingUser.id);
        intent.putExtra("is_outgoing", true);
        intent.putExtra("start_incall_activity", false);
        intent.putExtra("video_call", false);
        intent.putExtra("can_video_call", false);
        intent.putExtra("account", this.currentAccount);
        try {
            this.activity.startService(intent);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateViewState$19(boolean[] zArr, DialogInterface dialogInterface) {
        if (!zArr[0]) {
            this.windowView.finish();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateViewState$20() {
        this.windowView.finish();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateViewState$21() {
        this.windowView.finish();
    }

    private void fillNavigationBar(boolean z, boolean z2) {
        if (!this.switchingToPip) {
            float f = 0.0f;
            float f2 = 1.0f;
            if (!z2) {
                ValueAnimator valueAnimator = this.naviagtionBarAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                if (z) {
                    f = 1.0f;
                }
                this.fillNaviagtionBarValue = f;
                Paint paint = this.overlayBottomPaint;
                if (!z) {
                    f2 = 0.5f;
                }
                paint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (f2 * 255.0f)));
            } else if (z != this.fillNaviagtionBar) {
                ValueAnimator valueAnimator2 = this.naviagtionBarAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.fillNaviagtionBarValue;
                if (z) {
                    f = 1.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.naviagtionBarAnimator = ofFloat;
                ofFloat.addUpdateListener(this.navigationBarAnimationListener);
                this.naviagtionBarAnimator.setDuration(300);
                this.naviagtionBarAnimator.setInterpolator(new LinearInterpolator());
                this.naviagtionBarAnimator.start();
            }
            this.fillNaviagtionBar = z;
        }
    }

    /* access modifiers changed from: private */
    public void showUi(boolean z) {
        ValueAnimator valueAnimator = this.uiVisibilityAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int i = 0;
        if (!z && this.uiVisible) {
            ViewPropertyAnimator duration = this.speakerPhoneIcon.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(50.0f))).setDuration(150);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            this.backIcon.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(50.0f))).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
            this.emojiLayout.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(50.0f))).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
            this.statusLayout.animate().alpha(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
            this.buttonsLayout.animate().alpha(0.0f).translationY((float) AndroidUtilities.dp(50.0f)).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
            this.bottomShadow.animate().alpha(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
            this.topShadow.animate().alpha(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.uiVisibilityAlpha, 0.0f});
            this.uiVisibilityAnimator = ofFloat;
            ofFloat.addUpdateListener(this.statusbarAnimatorListener);
            this.uiVisibilityAnimator.setDuration(150).setInterpolator(cubicBezierInterpolator);
            this.uiVisibilityAnimator.start();
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.buttonsLayout.setEnabled(false);
        } else if (z && !this.uiVisible) {
            this.tapToVideoTooltip.hide();
            ViewPropertyAnimator duration2 = this.speakerPhoneIcon.animate().alpha(1.0f).translationY(0.0f).setDuration(150);
            CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.DEFAULT;
            duration2.setInterpolator(cubicBezierInterpolator2).start();
            this.backIcon.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator2).start();
            this.emojiLayout.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator2).start();
            this.statusLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(cubicBezierInterpolator2).start();
            this.buttonsLayout.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator2).start();
            this.bottomShadow.animate().alpha(1.0f).setDuration(150).setInterpolator(cubicBezierInterpolator2).start();
            this.topShadow.animate().alpha(1.0f).setDuration(150).setInterpolator(cubicBezierInterpolator2).start();
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.uiVisibilityAlpha, 1.0f});
            this.uiVisibilityAnimator = ofFloat2;
            ofFloat2.addUpdateListener(this.statusbarAnimatorListener);
            this.uiVisibilityAnimator.setDuration(150).setInterpolator(cubicBezierInterpolator2);
            this.uiVisibilityAnimator.start();
            this.buttonsLayout.setEnabled(true);
        }
        this.uiVisible = z;
        this.windowView.requestFullscreen(!z);
        ViewPropertyAnimator animate = this.notificationsLayout.animate();
        int i2 = -AndroidUtilities.dp(16.0f);
        if (this.uiVisible) {
            i = AndroidUtilities.dp(80.0f);
        }
        animate.translationY((float) (i2 - i)).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    private void showFloatingLayout(int i, boolean z) {
        Animator animator;
        if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) {
            this.currentUserCameraFloatingLayout.setUiVisible(this.uiVisible);
        }
        if (!z && (animator = this.cameraShowingAnimator) != null) {
            animator.removeAllListeners();
            this.cameraShowingAnimator.cancel();
        }
        boolean z2 = true;
        if (i != 0) {
            boolean z3 = (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0) ? false : z;
            if (!z) {
                this.currentUserCameraFloatingLayout.setVisibility(0);
            } else if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0) {
                if (this.currentUserCameraFloatingLayout.getVisibility() == 8) {
                    this.currentUserCameraFloatingLayout.setAlpha(0.0f);
                    this.currentUserCameraFloatingLayout.setScaleX(0.7f);
                    this.currentUserCameraFloatingLayout.setScaleY(0.7f);
                    this.currentUserCameraFloatingLayout.setVisibility(0);
                }
                Animator animator2 = this.cameraShowingAnimator;
                if (animator2 != null) {
                    animator2.removeAllListeners();
                    this.cameraShowingAnimator.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.SCALE_X, new float[]{0.7f, 1.0f}), ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.SCALE_Y, new float[]{0.7f, 1.0f})});
                this.cameraShowingAnimator = animatorSet;
                animatorSet.setDuration(150).start();
            }
            if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) {
                VoIPFloatingLayout voIPFloatingLayout = this.currentUserCameraFloatingLayout;
                if (voIPFloatingLayout.relativePositionToSetX < 0.0f) {
                    voIPFloatingLayout.setRelativePosition(1.0f, 1.0f);
                    this.currentUserCameraIsFullscreen = true;
                }
            }
            this.currentUserCameraFloatingLayout.setFloatingMode(i == 2, z3);
            if (i == 2) {
                z2 = false;
            }
            this.currentUserCameraIsFullscreen = z2;
        } else if (!z) {
            this.currentUserCameraFloatingLayout.setVisibility(8);
        } else if (!(this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0)) {
            Animator animator3 = this.cameraShowingAnimator;
            if (animator3 != null) {
                animator3.removeAllListeners();
                this.cameraShowingAnimator.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            VoIPFloatingLayout voIPFloatingLayout2 = this.currentUserCameraFloatingLayout;
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(voIPFloatingLayout2, View.ALPHA, new float[]{voIPFloatingLayout2.getAlpha(), 0.0f})});
            if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 2) {
                VoIPFloatingLayout voIPFloatingLayout3 = this.currentUserCameraFloatingLayout;
                Property property = View.SCALE_X;
                float[] fArr = {voIPFloatingLayout3.getScaleX(), 0.7f};
                VoIPFloatingLayout voIPFloatingLayout4 = this.currentUserCameraFloatingLayout;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(voIPFloatingLayout3, property, fArr), ObjectAnimator.ofFloat(voIPFloatingLayout4, View.SCALE_Y, new float[]{voIPFloatingLayout4.getScaleX(), 0.7f})});
            }
            this.cameraShowingAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPFragment.this.currentUserCameraFloatingLayout.setTranslationX(0.0f);
                    VoIPFragment.this.currentUserCameraFloatingLayout.setTranslationY(0.0f);
                    VoIPFragment.this.currentUserCameraFloatingLayout.setScaleY(1.0f);
                    VoIPFragment.this.currentUserCameraFloatingLayout.setScaleX(1.0f);
                    VoIPFragment.this.currentUserCameraFloatingLayout.setVisibility(8);
                }
            });
            this.cameraShowingAnimator.setDuration(250).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.cameraShowingAnimator.setStartDelay(50);
            this.cameraShowingAnimator.start();
        }
        this.currentUserCameraFloatingLayout.setTag(Integer.valueOf(i));
    }

    private void showCallingUserAvatarMini(boolean z, boolean z2) {
        int i = 0;
        int i2 = null;
        if (!z2) {
            this.callingUserPhotoViewMini.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.callingUserPhotoViewMini.setTranslationY(0.0f);
            this.callingUserPhotoViewMini.setAlpha(1.0f);
            BackupImageView backupImageView = this.callingUserPhotoViewMini;
            if (!z) {
                i = 8;
            }
            backupImageView.setVisibility(i);
        } else if (z && this.callingUserPhotoViewMini.getTag() == null) {
            this.callingUserPhotoViewMini.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.callingUserPhotoViewMini.setVisibility(0);
            this.callingUserPhotoViewMini.setAlpha(0.0f);
            this.callingUserPhotoViewMini.setTranslationY((float) (-AndroidUtilities.dp(135.0f)));
            this.callingUserPhotoViewMini.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        } else if (!z && this.callingUserPhotoViewMini.getTag() != null) {
            this.callingUserPhotoViewMini.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.callingUserPhotoViewMini.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(135.0f))).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPFragment.this.callingUserPhotoViewMini.setVisibility(8);
                }
            }).start();
        }
        BackupImageView backupImageView2 = this.callingUserPhotoViewMini;
        if (z) {
            i2 = 1;
        }
        backupImageView2.setTag(i2);
    }

    private void updateKeyView(boolean z) {
        VoIPService sharedInstance;
        if (!this.emojiLoaded && (sharedInstance = VoIPService.getSharedInstance()) != null) {
            byte[] bArr = null;
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(sharedInstance.getEncryptionKey());
                byteArrayOutputStream.write(sharedInstance.getGA());
                bArr = byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                FileLog.e((Throwable) e, false);
            }
            if (bArr != null) {
                String[] emojifyForCall = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(bArr, 0, bArr.length));
                for (int i = 0; i < 4; i++) {
                    Emoji.preloadEmoji(emojifyForCall[i]);
                    Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(emojifyForCall[i]);
                    if (emojiDrawable != null) {
                        emojiDrawable.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
                        emojiDrawable.preload();
                        this.emojiViews[i].setImageDrawable(emojiDrawable);
                        this.emojiViews[i].setContentDescription(emojifyForCall[i]);
                        this.emojiViews[i].setVisibility(8);
                    }
                    this.emojiDrawables[i] = emojiDrawable;
                }
                checkEmojiLoaded(z);
            }
        }
    }

    private void checkEmojiLoaded(boolean z) {
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            Emoji.EmojiDrawable[] emojiDrawableArr = this.emojiDrawables;
            if (emojiDrawableArr[i2] != null && emojiDrawableArr[i2].isLoaded()) {
                i++;
            }
        }
        if (i == 4) {
            this.emojiLoaded = true;
            for (int i3 = 0; i3 < 4; i3++) {
                if (this.emojiViews[i3].getVisibility() != 0) {
                    this.emojiViews[i3].setVisibility(0);
                    if (z) {
                        this.emojiViews[i3].setAlpha(0.0f);
                        this.emojiViews[i3].setTranslationY((float) AndroidUtilities.dp(30.0f));
                        this.emojiViews[i3].animate().alpha(1.0f).translationY(0.0f).setDuration(200).setStartDelay((long) (i3 * 20)).start();
                    }
                }
            }
        }
    }

    private void showAcceptDeclineView(boolean z, boolean z2) {
        int i = 0;
        int i2 = null;
        if (!z2) {
            AcceptDeclineView acceptDeclineView2 = this.acceptDeclineView;
            if (!z) {
                i = 8;
            }
            acceptDeclineView2.setVisibility(i);
        } else {
            if (z && this.acceptDeclineView.getTag() == null) {
                this.acceptDeclineView.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (this.acceptDeclineView.getVisibility() == 8) {
                    this.acceptDeclineView.setVisibility(0);
                    this.acceptDeclineView.setAlpha(0.0f);
                }
                this.acceptDeclineView.animate().alpha(1.0f);
            }
            if (!z && this.acceptDeclineView.getTag() != null) {
                this.acceptDeclineView.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.acceptDeclineView.animate().setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPFragment.this.acceptDeclineView.setVisibility(8);
                    }
                }).alpha(0.0f);
            }
        }
        this.acceptDeclineView.setEnabled(z);
        AcceptDeclineView acceptDeclineView3 = this.acceptDeclineView;
        if (z) {
            i2 = 1;
        }
        acceptDeclineView3.setTag(i2);
    }

    private void updateButtons(boolean z) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (z && Build.VERSION.SDK_INT >= 19) {
                TransitionSet transitionSet = new TransitionSet();
                Transition duration = new Visibility(this) {
                    public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(100.0f), 0.0f});
                        if (view instanceof VoIPToggleButton) {
                            view.setTranslationY((float) AndroidUtilities.dp(100.0f));
                            ofFloat.setStartDelay((long) ((VoIPToggleButton) view).animationDelay);
                        }
                        return ofFloat;
                    }

                    public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{view.getTranslationY(), (float) AndroidUtilities.dp(100.0f)});
                    }
                }.setDuration(150);
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                transitionSet.addTransition(duration.setInterpolator(cubicBezierInterpolator)).addTransition(new ChangeBounds().setDuration(150).setInterpolator(cubicBezierInterpolator));
                transitionSet.excludeChildren(VoIPToggleButton.class, true);
                TransitionManager.beginDelayedTransition(this.buttonsLayout, transitionSet);
            }
            int i = this.currentState;
            if (i == 15 || i == 17) {
                TLRPC$PhoneCall tLRPC$PhoneCall = sharedInstance.privateCall;
                if (tLRPC$PhoneCall == null || !tLRPC$PhoneCall.video || i != 15) {
                    this.bottomButtons[0].setVisibility(8);
                    this.bottomButtons[1].setVisibility(8);
                    this.bottomButtons[2].setVisibility(8);
                } else {
                    if (sharedInstance.isScreencast() || (!this.currentUserIsVideo && !this.callingUserIsVideo)) {
                        setSpeakerPhoneAction(this.bottomButtons[0], sharedInstance, z);
                        this.speakerPhoneIcon.animate().alpha(0.0f).start();
                    } else {
                        setFrontalCameraAction(this.bottomButtons[0], sharedInstance, z);
                        if (this.uiVisible) {
                            this.speakerPhoneIcon.animate().alpha(1.0f).start();
                        }
                    }
                    setVideoAction(this.bottomButtons[1], sharedInstance, z);
                    setMicrohoneAction(this.bottomButtons[2], sharedInstance, z);
                }
                this.bottomButtons[3].setVisibility(8);
            } else if (instance != null) {
                if (sharedInstance.isScreencast() || (!this.currentUserIsVideo && !this.callingUserIsVideo)) {
                    setSpeakerPhoneAction(this.bottomButtons[0], sharedInstance, z);
                    this.speakerPhoneIcon.setTag((Object) null);
                    this.speakerPhoneIcon.animate().alpha(0.0f).start();
                } else {
                    setFrontalCameraAction(this.bottomButtons[0], sharedInstance, z);
                    if (this.uiVisible) {
                        this.speakerPhoneIcon.setTag(1);
                        this.speakerPhoneIcon.animate().alpha(1.0f).start();
                    }
                }
                setVideoAction(this.bottomButtons[1], sharedInstance, z);
                setMicrohoneAction(this.bottomButtons[2], sharedInstance, z);
                this.bottomButtons[3].setData(NUM, -1, -1041108, LocaleController.getString("VoipEndCall", NUM), false, z);
                this.bottomButtons[3].setOnClickListener(VoIPFragment$$ExternalSyntheticLambda20.INSTANCE);
            } else {
                return;
            }
            int i2 = 0;
            for (int i3 = 0; i3 < 4; i3++) {
                if (this.bottomButtons[i3].getVisibility() == 0) {
                    this.bottomButtons[i3].animationDelay = i2;
                    i2 += 16;
                }
            }
            updateSpeakerPhoneIcon();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateButtons$22(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp();
        }
    }

    private void setMicrohoneAction(VoIPToggleButton voIPToggleButton, VoIPService voIPService, boolean z) {
        if (voIPService.isMicMute()) {
            voIPToggleButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipUnmute", NUM), true, z);
        } else {
            voIPToggleButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipMute", NUM), false, z);
        }
        this.currentUserCameraFloatingLayout.setMuted(voIPService.isMicMute(), z);
        voIPToggleButton.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda15(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setMicrohoneAction$23(View view) {
        String str;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            boolean z = !sharedInstance.isMicMute();
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (z) {
                    str = LocaleController.getString("AccDescrVoipMicOff", NUM);
                } else {
                    str = LocaleController.getString("AccDescrVoipMicOn", NUM);
                }
                view.announceForAccessibility(str);
            }
            sharedInstance.setMicMute(z, false, true);
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    private void setVideoAction(VoIPToggleButton voIPToggleButton, VoIPService voIPService, boolean z) {
        if ((this.currentUserIsVideo || this.callingUserIsVideo) ? true : voIPService.isVideoAvailable()) {
            if (this.currentUserIsVideo) {
                voIPToggleButton.setData(voIPService.isScreencast() ? NUM : NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipStopVideo", NUM), false, z);
            } else {
                voIPToggleButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipStartVideo", NUM), true, z);
            }
            voIPToggleButton.setCrossOffset(-AndroidUtilities.dpf2(3.5f));
            voIPToggleButton.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda19(this, voIPService));
            voIPToggleButton.setEnabled(true);
            return;
        }
        voIPToggleButton.setData(NUM, ColorUtils.setAlphaComponent(-1, 127), ColorUtils.setAlphaComponent(-1, 30), "Video", false, z);
        voIPToggleButton.setOnClickListener((View.OnClickListener) null);
        voIPToggleButton.setEnabled(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setVideoAction$25(VoIPService voIPService, View view) {
        TLRPC$PhoneCall tLRPC$PhoneCall;
        int i = Build.VERSION.SDK_INT;
        if (i >= 23 && this.activity.checkSelfPermission("android.permission.CAMERA") != 0) {
            this.activity.requestPermissions(new String[]{"android.permission.CAMERA"}, 102);
        } else if (i >= 21 || (tLRPC$PhoneCall = voIPService.privateCall) == null || tLRPC$PhoneCall.video || this.callingUserIsVideo || voIPService.sharedUIParams.cameraAlertWasShowed) {
            toggleCameraInput();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.activity);
            builder.setMessage(LocaleController.getString("VoipSwitchToVideoCall", NUM));
            builder.setPositiveButton(LocaleController.getString("VoipSwitch", NUM), new VoIPFragment$$ExternalSyntheticLambda6(this, voIPService));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.create().show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setVideoAction$24(VoIPService voIPService, DialogInterface dialogInterface, int i) {
        voIPService.sharedUIParams.cameraAlertWasShowed = true;
        toggleCameraInput();
    }

    private void updateSpeakerPhoneIcon() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.isBluetoothOn()) {
                this.speakerPhoneIcon.setImageResource(NUM);
            } else if (sharedInstance.isSpeakerphoneOn()) {
                this.speakerPhoneIcon.setImageResource(NUM);
            } else if (sharedInstance.isHeadsetPlugged()) {
                this.speakerPhoneIcon.setImageResource(NUM);
            } else {
                this.speakerPhoneIcon.setImageResource(NUM);
            }
        }
    }

    private void setSpeakerPhoneAction(VoIPToggleButton voIPToggleButton, VoIPService voIPService, boolean z) {
        VoIPToggleButton voIPToggleButton2 = voIPToggleButton;
        boolean z2 = z;
        if (voIPService.isBluetoothOn()) {
            voIPToggleButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipAudioRoutingBluetooth", NUM), false, z);
            voIPToggleButton.setChecked(false, z2);
        } else if (voIPService.isSpeakerphoneOn()) {
            voIPToggleButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipSpeaker", NUM), false, z);
            voIPToggleButton.setChecked(true, z2);
        } else {
            voIPToggleButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipSpeaker", NUM), false, z);
            voIPToggleButton.setChecked(false, z2);
        }
        voIPToggleButton.setCheckableForAccessibility(true);
        voIPToggleButton.setEnabled(true);
        voIPToggleButton.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda13(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setSpeakerPhoneAction$26(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
        }
    }

    private void setFrontalCameraAction(VoIPToggleButton voIPToggleButton, VoIPService voIPService, boolean z) {
        if (!this.currentUserIsVideo) {
            voIPToggleButton.setData(NUM, ColorUtils.setAlphaComponent(-1, 127), ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipFlip", NUM), false, z);
            voIPToggleButton.setOnClickListener((View.OnClickListener) null);
            voIPToggleButton.setEnabled(false);
            return;
        }
        voIPToggleButton.setEnabled(true);
        if (!voIPService.isFrontFaceCamera()) {
            voIPToggleButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipFlip", NUM), false, z);
        } else {
            voIPToggleButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipFlip", NUM), false, z);
        }
        voIPToggleButton.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda18(this, voIPService));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setFrontalCameraAction$27(VoIPService voIPService, View view) {
        String str;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (voIPService.isFrontFaceCamera()) {
                    str = LocaleController.getString("AccDescrVoipCamSwitchedToBack", NUM);
                } else {
                    str = LocaleController.getString("AccDescrVoipCamSwitchedToFront", NUM);
                }
                view.announceForAccessibility(str);
            }
            sharedInstance.switchCamera();
        }
    }

    public void onScreenCastStart() {
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.dismiss(true, true);
        }
    }

    private void toggleCameraInput() {
        String str;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (!this.currentUserIsVideo) {
                    str = LocaleController.getString("AccDescrVoipCamOn", NUM);
                } else {
                    str = LocaleController.getString("AccDescrVoipCamOff", NUM);
                }
                this.fragmentView.announceForAccessibility(str);
            }
            if (this.currentUserIsVideo) {
                this.currentUserTextureView.saveCameraLastBitmap();
                sharedInstance.setVideoState(false, 0);
                if (Build.VERSION.SDK_INT >= 21) {
                    sharedInstance.clearCamera();
                }
            } else if (Build.VERSION.SDK_INT < 21) {
                this.currentUserIsVideo = true;
                if (!sharedInstance.isSpeakerphoneOn()) {
                    VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
                }
                sharedInstance.requestVideoCall(false);
                sharedInstance.setVideoState(false, 2);
            } else if (this.previewDialog == null) {
                sharedInstance.createCaptureDevice(false);
                if (!sharedInstance.isFrontFaceCamera()) {
                    sharedInstance.switchCamera();
                }
                this.windowView.setLockOnScreen(true);
                AnonymousClass21 r0 = new PrivateVideoPreviewDialog(this.fragmentView.getContext(), false, true) {
                    public void onDismiss(boolean z, boolean z2) {
                        PrivateVideoPreviewDialog unused = VoIPFragment.this.previewDialog = null;
                        VoIPService sharedInstance = VoIPService.getSharedInstance();
                        VoIPFragment.this.windowView.setLockOnScreen(false);
                        if (z2) {
                            VoIPFragment.this.currentUserIsVideo = true;
                            if (sharedInstance != null && !z) {
                                sharedInstance.requestVideoCall(false);
                                sharedInstance.setVideoState(false, 2);
                            }
                        } else if (sharedInstance != null) {
                            sharedInstance.setVideoState(false, 0);
                        }
                        VoIPFragment voIPFragment = VoIPFragment.this;
                        int unused2 = voIPFragment.previousState = voIPFragment.currentState;
                        VoIPFragment.this.updateViewState();
                    }
                };
                this.previewDialog = r0;
                WindowInsets windowInsets = this.lastInsets;
                if (windowInsets != null) {
                    r0.setBottomPadding(windowInsets.getSystemWindowInsetBottom());
                }
                this.fragmentView.addView(this.previewDialog);
                return;
            } else {
                return;
            }
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public static void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onRequestPermissionsResultInternal(i, strArr, iArr);
        }
    }

    @TargetApi(23)
    private void onRequestPermissionsResultInternal(int i, String[] strArr, int[] iArr) {
        if (i == 101) {
            if (VoIPService.getSharedInstance() == null) {
                this.windowView.finish();
                return;
            } else if (iArr.length > 0 && iArr[0] == 0) {
                VoIPService.getSharedInstance().acceptIncomingCall();
            } else if (!this.activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
                VoIPService.getSharedInstance().declineIncomingCall();
                VoIPHelper.permissionDenied(this.activity, new VoIPFragment$$ExternalSyntheticLambda22(this), i);
                return;
            }
        }
        if (i != 102) {
            return;
        }
        if (VoIPService.getSharedInstance() == null) {
            this.windowView.finish();
        } else if (iArr.length > 0 && iArr[0] == 0) {
            toggleCameraInput();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultInternal$28() {
        this.windowView.finish();
    }

    private void updateSystemBarColors() {
        this.overlayPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (this.uiVisibilityAlpha * 102.0f * this.enterTransitionProgress)));
        this.overlayBottomPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (((this.fillNaviagtionBarValue * 0.5f) + 0.5f) * 255.0f * this.enterTransitionProgress)));
        ViewGroup viewGroup = this.fragmentView;
        if (viewGroup != null) {
            viewGroup.invalidate();
        }
    }

    public static void onPause() {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onPauseInternal();
        }
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.getInstance().onPause();
        }
    }

    public static void onResume() {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onResumeInternal();
        }
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.getInstance().onResume();
        }
    }

    public void onPauseInternal() {
        boolean z;
        VoIPService sharedInstance;
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        PowerManager powerManager = (PowerManager) this.activity.getSystemService("power");
        int i = Build.VERSION.SDK_INT;
        if (i >= 20) {
            z = powerManager.isInteractive();
        } else {
            z = powerManager.isScreenOn();
        }
        boolean checkInlinePermissions = AndroidUtilities.checkInlinePermissions(this.activity);
        if (this.canSwitchToPip && checkInlinePermissions) {
            int measuredHeight = instance.windowView.getMeasuredHeight();
            if (i >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                measuredHeight -= windowInsets2.getSystemWindowInsetBottom();
            }
            VoIPFragment voIPFragment = instance;
            VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), measuredHeight, 0);
            if (i >= 20 && (windowInsets = instance.lastInsets) != null) {
                VoIPPiPView.topInset = windowInsets.getSystemWindowInsetTop();
                VoIPPiPView.bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
            }
        }
        if (!this.currentUserIsVideo) {
            return;
        }
        if ((!checkInlinePermissions || !z) && (sharedInstance = VoIPService.getSharedInstance()) != null) {
            sharedInstance.setVideoState(false, 1);
        }
    }

    public void onResumeInternal() {
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.finish();
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.getVideoState(false) == 1) {
                sharedInstance.setVideoState(false, 2);
            }
            updateViewState();
        } else {
            this.windowView.finish();
        }
        this.deviceIsLocked = ((KeyguardManager) this.activity.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
    }

    private void showErrorDialog(CharSequence charSequence) {
        if (!this.activity.isFinishing()) {
            AlertDialog show = new DarkAlertDialog.Builder(this.activity).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(charSequence).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
            show.setCanceledOnTouchOutside(true);
            show.setOnDismissListener(new VoIPFragment$$ExternalSyntheticLambda8(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showErrorDialog$29(DialogInterface dialogInterface) {
        this.windowView.finish();
    }

    @SuppressLint({"InlinedApi"})
    private void requestInlinePermissions() {
        if (Build.VERSION.SDK_INT >= 21) {
            AlertsCreator.createDrawOverlayPermissionDialog(this.activity, new VoIPFragment$$ExternalSyntheticLambda4(this)).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestInlinePermissions$30(DialogInterface dialogInterface, int i) {
        VoIPWindowView voIPWindowView = this.windowView;
        if (voIPWindowView != null) {
            voIPWindowView.finish();
        }
    }
}
