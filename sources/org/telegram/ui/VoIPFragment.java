package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
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
import org.telegram.tgnet.TLRPC;
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
    private static final int STATE_FLOATING = 2;
    private static final int STATE_FULLSCREEN = 1;
    private static final int STATE_GONE = 0;
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
    TLRPC.User callingUser;
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
    public final int currentAccount;
    /* access modifiers changed from: private */
    public int currentState;
    TLRPC.User currentUser;
    /* access modifiers changed from: private */
    public VoIPFloatingLayout currentUserCameraFloatingLayout;
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
    private ViewGroup fragmentView;
    Runnable hideUIRunnable = new VoIPFragment$$ExternalSyntheticLambda13(this);
    boolean hideUiRunnableWaiting;
    /* access modifiers changed from: private */
    public boolean isFinished;
    boolean isOutgoing;
    /* access modifiers changed from: private */
    public boolean isVideoCall;
    long lastContentTapTime;
    /* access modifiers changed from: private */
    public WindowInsets lastInsets;
    /* access modifiers changed from: private */
    public boolean lockOnScreen;
    ValueAnimator naviagtionBarAnimator;
    ValueAnimator.AnimatorUpdateListener navigationBarAnimationListener = new VoIPFragment$$ExternalSyntheticLambda11(this);
    VoIPNotificationsLayout notificationsLayout;
    /* access modifiers changed from: private */
    public VoIPOverlayBackground overlayBackground;
    Paint overlayBottomPaint = new Paint();
    Paint overlayPaint = new Paint();
    /* access modifiers changed from: private */
    public PrivateVideoPreviewDialog previewDialog;
    /* access modifiers changed from: private */
    public int previousState;
    private boolean screenWasWakeup;
    private ImageView speakerPhoneIcon;
    LinearLayout statusLayout;
    private int statusLayoutAnimateToOffset;
    private VoIPStatusTextView statusTextView;
    ValueAnimator.AnimatorUpdateListener statusbarAnimatorListener = new VoIPFragment$$ExternalSyntheticLambda0(this);
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

    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4052lambda$new$0$orgtelegramuiVoIPFragment(ValueAnimator valueAnimator) {
        this.uiVisibilityAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4053lambda$new$1$orgtelegramuiVoIPFragment(ValueAnimator valueAnimator) {
        this.fillNaviagtionBarValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4054lambda$new$2$orgtelegramuiVoIPFragment() {
        this.hideUiRunnableWaiting = false;
        if (this.canHideUI && this.uiVisible && !this.emojiExpanded) {
            this.lastContentTapTime = System.currentTimeMillis();
            showUi(false);
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public static void show(Activity activity2, int account) {
        show(activity2, false, account);
    }

    public static void show(Activity activity2, boolean overlay, int account) {
        boolean screenOn;
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
            boolean z = false;
            boolean transitionFromPip = VoIPPiPView.getInstance() != null;
            if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().getUser() != null) {
                final VoIPFragment fragment = new VoIPFragment(account);
                fragment.activity = activity2;
                instance = fragment;
                if (!transitionFromPip) {
                    z = true;
                }
                VoIPWindowView windowView2 = new VoIPWindowView(activity2, z) {
                    public boolean dispatchKeyEvent(KeyEvent event) {
                        VoIPService service;
                        if (fragment.isFinished || fragment.switchingToPip) {
                            return false;
                        }
                        int keyCode = event.getKeyCode();
                        if (keyCode == 4 && event.getAction() == 1 && !fragment.lockOnScreen) {
                            fragment.onBackPressed();
                            return true;
                        } else if ((keyCode != 25 && keyCode != 24) || fragment.currentState != 15 || (service = VoIPService.getSharedInstance()) == null) {
                            return super.dispatchKeyEvent(event);
                        } else {
                            service.stopRinging();
                            return true;
                        }
                    }
                };
                instance.deviceIsLocked = ((KeyguardManager) activity2.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
                PowerManager pm = (PowerManager) activity2.getSystemService("power");
                if (Build.VERSION.SDK_INT >= 20) {
                    screenOn = pm.isInteractive();
                } else {
                    screenOn = pm.isScreenOn();
                }
                VoIPFragment voIPFragment3 = instance;
                voIPFragment3.screenWasWakeup = !screenOn;
                windowView2.setLockOnScreen(voIPFragment3.deviceIsLocked);
                fragment.windowView = windowView2;
                if (Build.VERSION.SDK_INT >= 20) {
                    windowView2.setOnApplyWindowInsetsListener(new VoIPFragment$$ExternalSyntheticLambda29(fragment));
                }
                WindowManager wm = (WindowManager) activity2.getSystemService("window");
                WindowManager.LayoutParams layoutParams = windowView2.createWindowLayoutParams();
                if (overlay) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        layoutParams.type = 2038;
                    } else {
                        layoutParams.type = 2003;
                    }
                }
                wm.addView(windowView2, layoutParams);
                windowView2.addView(fragment.createView(activity2));
                if (transitionFromPip) {
                    fragment.enterTransitionProgress = 0.0f;
                    fragment.startTransitionFromPiP();
                    return;
                }
                fragment.enterTransitionProgress = 1.0f;
                fragment.updateSystemBarColors();
            }
        }
    }

    static /* synthetic */ WindowInsets lambda$show$3(VoIPFragment fragment, View view, WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= 21) {
            fragment.setInsets(windowInsets);
        }
        if (Build.VERSION.SDK_INT >= 30) {
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
                int h = instance.windowView.getMeasuredHeight();
                if (Build.VERSION.SDK_INT >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                    h -= windowInsets2.getSystemWindowInsetBottom();
                }
                VoIPFragment voIPFragment = instance;
                if (voIPFragment.canSwitchToPip) {
                    VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), h, 0);
                    if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
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

    public VoIPFragment(int account) {
        this.currentAccount = account;
        this.currentUser = MessagesController.getInstance(account).getUser(Long.valueOf(UserConfig.getInstance(account).getClientUserId()));
        this.callingUser = VoIPService.getSharedInstance().getUser();
        VoIPService.getSharedInstance().registerStateListener(this);
        this.isOutgoing = VoIPService.getSharedInstance().isOutgoing();
        this.previousState = -1;
        this.currentState = VoIPService.getSharedInstance().getCallState();
        NotificationCenter.getInstance(account).addObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
    }

    /* access modifiers changed from: private */
    public void destroy() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.unregisterStateListener(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
    }

    public void onStateChanged(int state) {
        int i = this.currentState;
        if (i != state) {
            this.previousState = i;
            this.currentState = state;
            if (this.windowView != null) {
                updateViewState();
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.voipServiceCreated) {
            if (this.currentState == 17 && VoIPService.getSharedInstance() != null) {
                this.currentUserTextureView.renderer.release();
                this.callingUserTextureView.renderer.release();
                this.callingUserMiniTextureRenderer.release();
                initRenderers();
                VoIPService.getSharedInstance().registerStateListener(this);
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            updateKeyView(true);
        } else if (id == NotificationCenter.closeInCallActivity) {
            this.windowView.finish();
        }
    }

    public void onSignalBarsCountChanged(int count) {
        VoIPStatusTextView voIPStatusTextView = this.statusTextView;
        if (voIPStatusTextView != null) {
            voIPStatusTextView.setSignalBarCount(count);
        }
    }

    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    public void onMediaStateUpdated(int audioState, int videoState) {
        this.previousState = this.currentState;
        if (videoState == 2 && !this.isVideoCall) {
            this.isVideoCall = true;
        }
        updateViewState();
    }

    public void onCameraSwitch(boolean isFrontFace) {
        this.previousState = this.currentState;
        updateViewState();
    }

    public void onVideoAvailableChange(boolean isAvailable) {
        this.previousState = this.currentState;
        if (isAvailable && !this.isVideoCall) {
            this.isVideoCall = true;
        }
        updateViewState();
    }

    public void onScreenOnChange(boolean screenOn) {
    }

    public View createView(Context context) {
        Context context2 = context;
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        this.accessibilityManager = (AccessibilityManager) ContextCompat.getSystemService(context2, AccessibilityManager.class);
        FrameLayout frameLayout = new FrameLayout(context2) {
            boolean check;
            long pressedTime;
            float pressedX;
            float pressedY;

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (Build.VERSION.SDK_INT >= 20 && VoIPFragment.this.lastInsets != null) {
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) VoIPFragment.this.lastInsets.getSystemWindowInsetTop(), VoIPFragment.this.overlayPaint);
                }
                if (Build.VERSION.SDK_INT >= 20 && VoIPFragment.this.lastInsets != null) {
                    canvas.drawRect(0.0f, (float) (getMeasuredHeight() - VoIPFragment.this.lastInsets.getSystemWindowInsetBottom()), (float) getMeasuredWidth(), (float) getMeasuredHeight(), VoIPFragment.this.overlayBottomPaint);
                }
            }

            public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        this.pressedX = event.getX();
                        this.pressedY = event.getY();
                        this.check = true;
                        this.pressedTime = System.currentTimeMillis();
                        break;
                    case 1:
                        if (this.check) {
                            float dx = event.getX() - this.pressedX;
                            float dy = event.getY() - this.pressedY;
                            long currentTime = System.currentTimeMillis();
                            if ((dx * dx) + (dy * dy) < VoIPFragment.this.touchSlop * VoIPFragment.this.touchSlop && currentTime - this.pressedTime < 300 && currentTime - VoIPFragment.this.lastContentTapTime > 300) {
                                VoIPFragment.this.lastContentTapTime = System.currentTimeMillis();
                                if (VoIPFragment.this.emojiExpanded) {
                                    VoIPFragment.this.expandEmoji(false);
                                } else if (VoIPFragment.this.canHideUI) {
                                    VoIPFragment voIPFragment = VoIPFragment.this;
                                    voIPFragment.showUi(true ^ voIPFragment.uiVisible);
                                    VoIPFragment voIPFragment2 = VoIPFragment.this;
                                    int unused = voIPFragment2.previousState = voIPFragment2.currentState;
                                    VoIPFragment.this.updateViewState();
                                }
                            }
                            this.check = false;
                            break;
                        }
                        break;
                    case 3:
                        this.check = false;
                        break;
                }
                return this.check;
            }
        };
        boolean z = false;
        frameLayout.setClipToPadding(false);
        frameLayout.setClipChildren(false);
        updateSystemBarColors();
        this.fragmentView = frameLayout;
        frameLayout.setFitsSystemWindows(true);
        this.callingUserPhotoView = new BackupImageView(context2) {
            int blackoutColor = ColorUtils.setAlphaComponent(-16777216, 76);

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawColor(this.blackoutColor);
            }
        };
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, true, false, false);
        this.callingUserTextureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.callingUserTextureView.renderer.setEnableHardwareScaler(true);
        this.callingUserTextureView.scaleType = VoIPTextureView.SCALE_TYPE_NONE;
        frameLayout.addView(this.callingUserPhotoView);
        frameLayout.addView(this.callingUserTextureView);
        BackgroundGradientDrawable gradientDrawable = new BackgroundGradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963});
        gradientDrawable.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT), new BackgroundGradientDrawable.ListenerAdapter() {
            public void onAllSizesReady() {
                VoIPFragment.this.callingUserPhotoView.invalidate();
            }
        });
        VoIPOverlayBackground voIPOverlayBackground = new VoIPOverlayBackground(context2);
        this.overlayBackground = voIPOverlayBackground;
        voIPOverlayBackground.setVisibility(8);
        this.callingUserPhotoView.getImageReceiver().setDelegate(new VoIPFragment$$ExternalSyntheticLambda20(this));
        this.callingUserPhotoView.setImage(ImageLocation.getForUserOrChat(this.callingUser, 0), (String) null, (Drawable) gradientDrawable, (Object) this.callingUser);
        VoIPFloatingLayout voIPFloatingLayout = new VoIPFloatingLayout(context2);
        this.currentUserCameraFloatingLayout = voIPFloatingLayout;
        voIPFloatingLayout.setDelegate(new VoIPFragment$$ExternalSyntheticLambda21(this));
        this.currentUserCameraFloatingLayout.setRelativePosition(1.0f, 1.0f);
        VoIPTextureView voIPTextureView2 = new VoIPTextureView(context2, true, false);
        this.currentUserTextureView = voIPTextureView2;
        voIPTextureView2.renderer.setIsCamera(true);
        this.currentUserTextureView.renderer.setUseCameraRotation(true);
        this.currentUserCameraFloatingLayout.setOnTapListener(new VoIPFragment$$ExternalSyntheticLambda2(this));
        this.currentUserTextureView.renderer.setMirror(true);
        this.currentUserCameraFloatingLayout.addView(this.currentUserTextureView);
        VoIPFloatingLayout voIPFloatingLayout2 = new VoIPFloatingLayout(context2);
        this.callingUserMiniFloatingLayout = voIPFloatingLayout2;
        voIPFloatingLayout2.alwaysFloating = true;
        this.callingUserMiniFloatingLayout.setFloatingMode(true, false);
        TextureViewRenderer textureViewRenderer = new TextureViewRenderer(context2);
        this.callingUserMiniTextureRenderer = textureViewRenderer;
        textureViewRenderer.setEnableHardwareScaler(true);
        this.callingUserMiniTextureRenderer.setIsCamera(false);
        this.callingUserMiniTextureRenderer.setFpsReduction(30.0f);
        this.callingUserMiniTextureRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        View backgroundView = new View(context2);
        backgroundView.setBackgroundColor(-14999773);
        this.callingUserMiniFloatingLayout.addView(backgroundView, LayoutHelper.createFrame(-1, -1.0f));
        this.callingUserMiniFloatingLayout.addView(this.callingUserMiniTextureRenderer, LayoutHelper.createFrame(-1, -2, 17));
        this.callingUserMiniFloatingLayout.setOnTapListener(new VoIPFragment$$ExternalSyntheticLambda3(this));
        this.callingUserMiniFloatingLayout.setVisibility(8);
        frameLayout.addView(this.currentUserCameraFloatingLayout, LayoutHelper.createFrame(-2, -2.0f));
        frameLayout.addView(this.callingUserMiniFloatingLayout);
        frameLayout.addView(this.overlayBackground);
        View view = new View(context2);
        this.bottomShadow = view;
        view.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 127)}));
        frameLayout.addView(this.bottomShadow, LayoutHelper.createFrame(-1, 140, 80));
        View view2 = new View(context2);
        this.topShadow = view2;
        view2.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ColorUtils.setAlphaComponent(-16777216, 102), 0}));
        frameLayout.addView(this.topShadow, LayoutHelper.createFrame(-1, 140, 48));
        AnonymousClass5 r3 = new LinearLayout(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setVisibleToUser(VoIPFragment.this.emojiLoaded);
            }
        };
        this.emojiLayout = r3;
        r3.setOrientation(0);
        this.emojiLayout.setPadding(0, 0, 0, AndroidUtilities.dp(30.0f));
        this.emojiLayout.setClipToPadding(false);
        this.emojiLayout.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda4(this));
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
        AnonymousClass6 r32 = new LinearLayout(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                VoIPService service = VoIPService.getSharedInstance();
                CharSequence callingUserTitleText = VoIPFragment.this.callingUserTitle.getText();
                if (service != null && !TextUtils.isEmpty(callingUserTitleText)) {
                    StringBuilder builder = new StringBuilder(callingUserTitleText);
                    builder.append(", ");
                    if (service.privateCall == null || !service.privateCall.video) {
                        builder.append(LocaleController.getString("VoipInCallBranding", NUM));
                    } else {
                        builder.append(LocaleController.getString("VoipInVideoCallBranding", NUM));
                    }
                    long callDuration = service.getCallDuration();
                    if (callDuration > 0) {
                        builder.append(", ");
                        builder.append(LocaleController.formatDuration((int) (callDuration / 1000)));
                    }
                    info.setText(builder);
                }
            }
        };
        this.statusLayout = r32;
        r32.setOrientation(1);
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
        this.callingUserTitle.setText(ContactsController.formatName(this.callingUser.first_name, this.callingUser.last_name));
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
        frameLayout.addView(this.callingUserPhotoViewMini, LayoutHelper.createFrame(135, 135.0f, 1, 0.0f, 68.0f, 0.0f, 0.0f));
        frameLayout.addView(this.statusLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 68.0f, 0.0f, 0.0f));
        frameLayout.addView(this.emojiLayout, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 17.0f, 0.0f, 0.0f));
        frameLayout.addView(this.emojiRationalTextView, LayoutHelper.createFrame(-1, -2.0f, 17, 24.0f, 32.0f, 24.0f, 0.0f));
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
                    } catch (Throwable e) {
                        FileLog.e(e);
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

            public void onDicline() {
                if (VoIPFragment.this.currentState == 17) {
                    VoIPFragment.this.windowView.finish();
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall();
                }
            }
        });
        this.acceptDeclineView.setScreenWasWakeup(this.screenWasWakeup);
        frameLayout.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, -2, 80));
        frameLayout.addView(this.acceptDeclineView, LayoutHelper.createFrame(-1, 186, 80));
        ImageView imageView = new ImageView(context2);
        this.backIcon = imageView;
        imageView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.backIcon.setImageResource(NUM);
        this.backIcon.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.backIcon.setContentDescription(LocaleController.getString("Back", NUM));
        frameLayout.addView(this.backIcon, LayoutHelper.createFrame(56, 56, 51));
        AnonymousClass8 r33 = new ImageView(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setClassName(ToggleButton.class.getName());
                info.setCheckable(true);
                VoIPService service = VoIPService.getSharedInstance();
                if (service != null) {
                    info.setChecked(service.isSpeakerphoneOn());
                }
            }
        };
        this.speakerPhoneIcon = r33;
        r33.setContentDescription(LocaleController.getString("VoipSpeaker", NUM));
        this.speakerPhoneIcon.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.speakerPhoneIcon.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
        frameLayout.addView(this.speakerPhoneIcon, LayoutHelper.createFrame(56, 56, 53));
        this.speakerPhoneIcon.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda5(this));
        this.backIcon.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda1(this));
        if (this.windowView.isLockOnScreen()) {
            this.backIcon.setVisibility(8);
        }
        VoIPNotificationsLayout voIPNotificationsLayout = new VoIPNotificationsLayout(context2);
        this.notificationsLayout = voIPNotificationsLayout;
        voIPNotificationsLayout.setGravity(80);
        this.notificationsLayout.setOnViewsUpdated(new VoIPFragment$$ExternalSyntheticLambda12(this));
        frameLayout.addView(this.notificationsLayout, LayoutHelper.createFrame(-1, 200.0f, 80, 16.0f, 0.0f, 16.0f, 0.0f));
        HintView hintView = new HintView(context2, 4);
        this.tapToVideoTooltip = hintView;
        hintView.setText(LocaleController.getString("TapToTurnCamera", NUM));
        frameLayout.addView(this.tapToVideoTooltip, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 8.0f));
        this.tapToVideoTooltip.setBottomOffset(AndroidUtilities.dp(4.0f));
        this.tapToVideoTooltip.setVisibility(8);
        updateViewState();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (!this.isVideoCall) {
                if (service.privateCall != null && service.privateCall.video) {
                    z = true;
                }
                this.isVideoCall = z;
            }
            initRenderers();
        }
        return frameLayout;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4046lambda$createView$4$orgtelegramuiVoIPFragment(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        ImageReceiver.BitmapHolder bmp = imageReceiver.getBitmapSafe();
        if (bmp != null) {
            this.overlayBackground.setBackground(bmp);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4047lambda$createView$5$orgtelegramuiVoIPFragment(float progress, boolean value) {
        this.currentUserTextureView.setScreenshareMiniProgress(progress, value);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4048lambda$createView$6$orgtelegramuiVoIPFragment(View view) {
        if (this.currentUserIsVideo && this.callingUserIsVideo && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.callingUserMiniFloatingLayout.setRelativePosition(this.currentUserCameraFloatingLayout);
            this.cameraForceExpanded = true;
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4049lambda$createView$7$orgtelegramuiVoIPFragment(View view) {
        if (this.cameraForceExpanded && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
            this.cameraForceExpanded = false;
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4050lambda$createView$8$orgtelegramuiVoIPFragment(View view) {
        if (System.currentTimeMillis() - this.lastContentTapTime >= 500) {
            this.lastContentTapTime = System.currentTimeMillis();
            if (this.emojiLoaded) {
                expandEmoji(!this.emojiExpanded);
            }
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4051lambda$createView$9$orgtelegramuiVoIPFragment(View view) {
        if (this.speakerPhoneIcon.getTag() != null && VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4044lambda$createView$10$orgtelegramuiVoIPFragment(View view) {
        if (!this.lockOnScreen) {
            onBackPressed();
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4045lambda$createView$11$orgtelegramuiVoIPFragment() {
        this.previousState = this.currentState;
        updateViewState();
    }

    private void initRenderers() {
        this.currentUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            /* renamed from: lambda$onFirstFrameRendered$0$org-telegram-ui-VoIPFragment$9  reason: not valid java name */
            public /* synthetic */ void m4073lambda$onFirstFrameRendered$0$orgtelegramuiVoIPFragment$9() {
                VoIPFragment.this.updateViewState();
            }

            public void onFirstFrameRendered() {
                AndroidUtilities.runOnUIThread(new VoIPFragment$9$$ExternalSyntheticLambda0(this));
            }

            public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
            }
        });
        this.callingUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            /* renamed from: lambda$onFirstFrameRendered$0$org-telegram-ui-VoIPFragment$10  reason: not valid java name */
            public /* synthetic */ void m4071lambda$onFirstFrameRendered$0$orgtelegramuiVoIPFragment$10() {
                VoIPFragment.this.updateViewState();
            }

            public void onFirstFrameRendered() {
                AndroidUtilities.runOnUIThread(new VoIPFragment$10$$ExternalSyntheticLambda0(this));
            }

            public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
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
                int h = instance.windowView.getMeasuredHeight();
                if (Build.VERSION.SDK_INT >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                    h -= windowInsets2.getSystemWindowInsetBottom();
                }
                VoIPFragment voIPFragment = instance;
                VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), h, 1);
                if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                    VoIPPiPView.topInset = windowInsets.getSystemWindowInsetTop();
                    VoIPPiPView.bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
                }
            }
            if (VoIPPiPView.getInstance() != null) {
                this.speakerPhoneIcon.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.backIcon.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.emojiLayout.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.statusLayout.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.buttonsLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.bottomShadow.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.topShadow.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.callingUserMiniFloatingLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.notificationsLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                VoIPPiPView.switchingToPip = true;
                this.switchingToPip = true;
                Animator animator = createPiPTransition(false);
                this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPPiPView.getInstance().windowView.setAlpha(1.0f);
                        AndroidUtilities.runOnUIThread(new VoIPFragment$11$$ExternalSyntheticLambda0(this), 200);
                    }

                    /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-VoIPFragment$11  reason: not valid java name */
                    public /* synthetic */ void m4072lambda$onAnimationEnd$0$orgtelegramuiVoIPFragment$11() {
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
                animator.setDuration(350);
                animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animator.start();
            }
        }
    }

    public void startTransitionFromPiP() {
        this.enterFromPiP = true;
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.getVideoState(false) == 2) {
            this.callingUserTextureView.setStub(VoIPPiPView.getInstance().callingUserTextureView);
            this.currentUserTextureView.setStub(VoIPPiPView.getInstance().currentUserTextureView);
        }
        this.windowView.setAlpha(0.0f);
        updateViewState();
        this.switchingToPip = true;
        VoIPPiPView.switchingToPip = true;
        VoIPPiPView.prepareForTransition();
        this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
        AndroidUtilities.runOnUIThread(new VoIPFragment$$ExternalSyntheticLambda15(this), 32);
    }

    /* renamed from: lambda$startTransitionFromPiP$13$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4064lambda$startTransitionFromPiP$13$orgtelegramuiVoIPFragment() {
        this.windowView.setAlpha(1.0f);
        Animator animator = createPiPTransition(true);
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
        AndroidUtilities.runOnUIThread(new VoIPFragment$$ExternalSyntheticLambda19(this, animator), 32);
    }

    /* renamed from: lambda$startTransitionFromPiP$12$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4063lambda$startTransitionFromPiP$12$orgtelegramuiVoIPFragment(Animator animator) {
        VoIPPiPView.switchingToPip = false;
        VoIPPiPView.finish();
        this.speakerPhoneIcon.animate().setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.backIcon.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.emojiLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.statusLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.buttonsLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.bottomShadow.animate().alpha(1.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.topShadow.animate().alpha(1.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.notificationsLayout.animate().alpha(1.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.callingUserPhotoView.animate().alpha(1.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
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
        animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animator.start();
    }

    public Animator createPiPTransition(boolean enter) {
        boolean animateCamera;
        float cameraToY;
        float cameraToX;
        float cameraToScale;
        float fromCameraAlpha;
        float cameraToX2;
        float cameraToScale2;
        float cameraToY2;
        this.currentUserCameraFloatingLayout.animate().cancel();
        float toX = (float) (VoIPPiPView.getInstance().windowLayoutParams.x + VoIPPiPView.getInstance().xOffset);
        float toY = (float) (VoIPPiPView.getInstance().windowLayoutParams.y + VoIPPiPView.getInstance().yOffset);
        float cameraFromX = this.currentUserCameraFloatingLayout.getX();
        float cameraFromY = this.currentUserCameraFloatingLayout.getY();
        float cameraFromScale = this.currentUserCameraFloatingLayout.getScaleX();
        boolean animateCamera2 = true;
        float pipScale = VoIPPiPView.isExpanding() ? 0.4f : 0.25f;
        float callingUserToScale = pipScale;
        float callingUserToX = toX - ((((float) this.callingUserTextureView.getMeasuredWidth()) - (((float) this.callingUserTextureView.getMeasuredWidth()) * callingUserToScale)) / 2.0f);
        float callingUserToY = toY - ((((float) this.callingUserTextureView.getMeasuredHeight()) - (((float) this.callingUserTextureView.getMeasuredHeight()) * callingUserToScale)) / 2.0f);
        if (this.callingUserIsVideo) {
            int currentW = this.currentUserCameraFloatingLayout.getMeasuredWidth();
            if (!this.currentUserIsVideo || currentW == 0) {
                cameraToScale2 = 0.0f;
                cameraToX2 = 1.0f;
                cameraToY2 = 1.0f;
                animateCamera2 = false;
            } else {
                cameraToScale2 = (((float) this.windowView.getMeasuredWidth()) / ((float) currentW)) * pipScale * 0.4f;
                cameraToX2 = (((toX - ((((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) * cameraToScale2)) / 2.0f)) + (((float) VoIPPiPView.getInstance().parentWidth) * pipScale)) - ((((float) VoIPPiPView.getInstance().parentWidth) * pipScale) * 0.4f)) - ((float) AndroidUtilities.dp(4.0f));
                cameraToY2 = (((toY - ((((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) * cameraToScale2)) / 2.0f)) + (((float) VoIPPiPView.getInstance().parentHeight) * pipScale)) - ((((float) VoIPPiPView.getInstance().parentHeight) * pipScale) * 0.4f)) - ((float) AndroidUtilities.dp(4.0f));
            }
            animateCamera = animateCamera2;
            cameraToY = cameraToY2;
            float f = cameraToX2;
            cameraToX = cameraToScale2;
            cameraToScale = f;
        } else {
            float cameraToScale3 = pipScale;
            float cameraToX3 = toX - ((((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) * cameraToScale3)) / 2.0f);
            animateCamera = true;
            cameraToY = toY - ((((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) * cameraToScale3)) / 2.0f);
            float f2 = cameraToX3;
            cameraToX = cameraToScale3;
            cameraToScale = f2;
        }
        float f3 = 0.0f;
        float cameraCornerRadiusFrom = this.callingUserIsVideo ? (float) AndroidUtilities.dp(4.0f) : 0.0f;
        float cameraCornerRadiusTo = (((float) AndroidUtilities.dp(4.0f)) * 1.0f) / cameraToX;
        if (this.callingUserIsVideo) {
            fromCameraAlpha = VoIPPiPView.isExpanding() ? 1.0f : 0.0f;
        } else {
            fromCameraAlpha = 1.0f;
        }
        if (enter) {
            if (animateCamera) {
                this.currentUserCameraFloatingLayout.setScaleX(cameraToX);
                this.currentUserCameraFloatingLayout.setScaleY(cameraToX);
                this.currentUserCameraFloatingLayout.setTranslationX(cameraToScale);
                this.currentUserCameraFloatingLayout.setTranslationY(cameraToY);
                this.currentUserCameraFloatingLayout.setCornerRadius(cameraCornerRadiusTo);
                this.currentUserCameraFloatingLayout.setAlpha(fromCameraAlpha);
            }
            this.callingUserTextureView.setScaleX(callingUserToScale);
            this.callingUserTextureView.setScaleY(callingUserToScale);
            this.callingUserTextureView.setTranslationX(callingUserToX);
            this.callingUserTextureView.setTranslationY(callingUserToY);
            this.callingUserTextureView.setRoundCorners((((float) AndroidUtilities.dp(6.0f)) * 1.0f) / callingUserToScale);
            this.callingUserPhotoView.setAlpha(0.0f);
            this.callingUserPhotoView.setScaleX(callingUserToScale);
            this.callingUserPhotoView.setScaleY(callingUserToScale);
            this.callingUserPhotoView.setTranslationX(callingUserToX);
            this.callingUserPhotoView.setTranslationY(callingUserToY);
        }
        float[] fArr = new float[2];
        fArr[0] = enter ? 1.0f : 0.0f;
        fArr[1] = enter ? 0.0f : 1.0f;
        ValueAnimator animator = ValueAnimator.ofFloat(fArr);
        if (!enter) {
            f3 = 1.0f;
        }
        this.enterTransitionProgress = f3;
        updateSystemBarColors();
        ValueAnimator animator2 = animator;
        VoIPFragment$$ExternalSyntheticLambda22 voIPFragment$$ExternalSyntheticLambda22 = r0;
        float f4 = fromCameraAlpha;
        float f5 = cameraToX;
        float f6 = toY;
        float f7 = toX;
        VoIPFragment$$ExternalSyntheticLambda22 voIPFragment$$ExternalSyntheticLambda222 = new VoIPFragment$$ExternalSyntheticLambda22(this, animateCamera, cameraFromScale, cameraToX, cameraFromX, cameraToScale, cameraFromY, cameraToY, cameraCornerRadiusFrom, cameraCornerRadiusTo, 1.0f, fromCameraAlpha, 1.0f, callingUserToScale, 0.0f, callingUserToX, 0.0f, callingUserToY);
        ValueAnimator animator3 = animator2;
        animator3.addUpdateListener(voIPFragment$$ExternalSyntheticLambda22);
        return animator3;
    }

    /* renamed from: lambda$createPiPTransition$14$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4043lambda$createPiPTransition$14$orgtelegramuiVoIPFragment(boolean finalAnimateCamera, float cameraFromScale, float cameraToScale, float cameraFromX, float cameraToX, float cameraFromY, float cameraToY, float cameraCornerRadiusFrom, float cameraCornerRadiusTo, float toCameraAlpha, float finalFromCameraAlpha, float callingUserFromScale, float callingUserToScale, float callingUserFromX, float callingUserToX, float callingUserFromY, float callingUserToY, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.enterTransitionProgress = 1.0f - v;
        updateSystemBarColors();
        if (finalAnimateCamera) {
            float cameraScale = ((1.0f - v) * cameraFromScale) + (cameraToScale * v);
            this.currentUserCameraFloatingLayout.setScaleX(cameraScale);
            this.currentUserCameraFloatingLayout.setScaleY(cameraScale);
            this.currentUserCameraFloatingLayout.setTranslationX(((1.0f - v) * cameraFromX) + (cameraToX * v));
            this.currentUserCameraFloatingLayout.setTranslationY(((1.0f - v) * cameraFromY) + (cameraToY * v));
            this.currentUserCameraFloatingLayout.setCornerRadius(((1.0f - v) * cameraCornerRadiusFrom) + (cameraCornerRadiusTo * v));
            this.currentUserCameraFloatingLayout.setAlpha(((1.0f - v) * toCameraAlpha) + (finalFromCameraAlpha * v));
        }
        float callingUserScale = ((1.0f - v) * callingUserFromScale) + (callingUserToScale * v);
        this.callingUserTextureView.setScaleX(callingUserScale);
        this.callingUserTextureView.setScaleY(callingUserScale);
        float tx = ((1.0f - v) * callingUserFromX) + (callingUserToX * v);
        float ty = ((1.0f - v) * callingUserFromY) + (callingUserToY * v);
        this.callingUserTextureView.setTranslationX(tx);
        this.callingUserTextureView.setTranslationY(ty);
        this.callingUserTextureView.setRoundCorners(((((float) AndroidUtilities.dp(4.0f)) * v) * 1.0f) / callingUserScale);
        if (!this.currentUserCameraFloatingLayout.measuredAsFloatingMode) {
            this.currentUserTextureView.setScreenshareMiniProgress(v, false);
        }
        this.callingUserPhotoView.setScaleX(callingUserScale);
        this.callingUserPhotoView.setScaleY(callingUserScale);
        this.callingUserPhotoView.setTranslationX(tx);
        this.callingUserPhotoView.setTranslationY(ty);
        this.callingUserPhotoView.setAlpha(1.0f - v);
    }

    /* access modifiers changed from: private */
    public void expandEmoji(boolean expanded) {
        if (this.emojiLoaded && this.emojiExpanded != expanded && this.uiVisible) {
            this.emojiExpanded = expanded;
            if (expanded) {
                AndroidUtilities.runOnUIThread(this.hideUIRunnable);
                this.hideUiRunnableWaiting = false;
                float scale = ((float) (this.windowView.getMeasuredWidth() - AndroidUtilities.dp(128.0f))) / ((float) this.emojiLayout.getMeasuredWidth());
                this.emojiLayout.animate().scaleX(scale).scaleY(scale).translationY((((float) this.windowView.getHeight()) / 2.0f) - ((float) this.emojiLayout.getBottom())).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(250).start();
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
                    public void onAnimationEnd(Animator animation) {
                        VoIPService service = VoIPService.getSharedInstance();
                        if (VoIPFragment.this.canHideUI && !VoIPFragment.this.hideUiRunnableWaiting && service != null && !service.isMicMute()) {
                            AndroidUtilities.runOnUIThread(VoIPFragment.this.hideUIRunnable, 3000);
                            VoIPFragment.this.hideUiRunnableWaiting = true;
                        }
                        VoIPFragment.this.emojiRationalTextView.setVisibility(8);
                    }
                }).setDuration(150).start();
                this.overlayBackground.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPFragment.this.overlayBackground.setVisibility(8);
                    }
                }).setDuration(150).start();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateViewState() {
        boolean animated;
        int i;
        float f;
        if (!this.isFinished && !this.switchingToPip) {
            this.lockOnScreen = false;
            boolean animated2 = this.previousState != -1;
            boolean showAcceptDeclineView = false;
            boolean showTimer = false;
            boolean showReconnecting = false;
            boolean showCallingAvatarMini = false;
            int statusLayoutOffset = 0;
            VoIPService service = VoIPService.getSharedInstance();
            switch (this.currentState) {
                case 1:
                case 2:
                    animated = animated2;
                    this.statusTextView.setText(LocaleController.getString("VoipConnecting", NUM), true, animated);
                    break;
                case 3:
                case 5:
                    animated = animated2;
                    updateKeyView(animated);
                    showTimer = true;
                    if (this.currentState == 5) {
                        showReconnecting = true;
                        break;
                    }
                    break;
                case 4:
                    this.statusTextView.setText(LocaleController.getString("VoipFailed", NUM), false, animated2);
                    VoIPService voipService = VoIPService.getSharedInstance();
                    String lastError = voipService != null ? voipService.getLastError() : "ERROR_UNKNOWN";
                    boolean animated3 = animated2;
                    if (!TextUtils.equals(lastError, "ERROR_UNKNOWN")) {
                        if (!TextUtils.equals(lastError, "ERROR_INCOMPATIBLE")) {
                            if (!TextUtils.equals(lastError, "ERROR_PEER_OUTDATED")) {
                                if (!TextUtils.equals(lastError, "ERROR_PRIVACY")) {
                                    if (!TextUtils.equals(lastError, "ERROR_AUDIO_IO")) {
                                        if (!TextUtils.equals(lastError, "ERROR_LOCALIZED")) {
                                            if (!TextUtils.equals(lastError, "ERROR_CONNECTION_SERVICE")) {
                                                AndroidUtilities.runOnUIThread(new VoIPFragment$$ExternalSyntheticLambda17(this), 1000);
                                                animated = animated3;
                                                break;
                                            } else {
                                                showErrorDialog(LocaleController.getString("VoipErrorUnknown", NUM));
                                                animated = animated3;
                                                break;
                                            }
                                        } else {
                                            this.windowView.finish();
                                            animated = animated3;
                                            break;
                                        }
                                    } else {
                                        showErrorDialog("Error initializing audio hardware");
                                        animated = animated3;
                                        break;
                                    }
                                } else {
                                    showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, ContactsController.formatName(this.callingUser.first_name, this.callingUser.last_name))));
                                    animated = animated3;
                                    break;
                                }
                            } else if (!this.isVideoCall) {
                                showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", NUM, UserObject.getFirstName(this.callingUser))));
                                animated = animated3;
                                break;
                            } else {
                                boolean[] callAgain = new boolean[1];
                                AlertDialog dlg = new DarkAlertDialog.Builder(this.activity).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerVideoOutdated", NUM, UserObject.getFirstName(this.callingUser)))).setNegativeButton(LocaleController.getString("Cancel", NUM), new VoIPFragment$$ExternalSyntheticLambda24(this)).setPositiveButton(LocaleController.getString("VoipPeerVideoOutdatedMakeVoice", NUM), new VoIPFragment$$ExternalSyntheticLambda26(this, callAgain)).show();
                                dlg.setCanceledOnTouchOutside(true);
                                dlg.setOnDismissListener(new VoIPFragment$$ExternalSyntheticLambda28(this, callAgain));
                                animated = animated3;
                                break;
                            }
                        } else {
                            showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", NUM, ContactsController.formatName(this.callingUser.first_name, this.callingUser.last_name))));
                            animated = animated3;
                            break;
                        }
                    } else {
                        AndroidUtilities.runOnUIThread(new VoIPFragment$$ExternalSyntheticLambda18(this), 1000);
                        animated = animated3;
                        break;
                    }
                case 10:
                    animated = animated2;
                    break;
                case 11:
                    this.currentUserTextureView.saveCameraLastBitmap();
                    AndroidUtilities.runOnUIThread(new VoIPFragment$$ExternalSyntheticLambda16(this), 200);
                    animated = animated2;
                    break;
                case 12:
                    this.statusTextView.setText(LocaleController.getString("VoipExchangingKeys", NUM), true, animated2);
                    animated = animated2;
                    break;
                case 13:
                    this.statusTextView.setText(LocaleController.getString("VoipWaiting", NUM), true, animated2);
                    animated = animated2;
                    break;
                case 14:
                    this.statusTextView.setText(LocaleController.getString("VoipRequesting", NUM), true, animated2);
                    animated = animated2;
                    break;
                case 15:
                    showAcceptDeclineView = true;
                    this.lockOnScreen = true;
                    statusLayoutOffset = AndroidUtilities.dp(24.0f);
                    this.acceptDeclineView.setRetryMod(false);
                    if (service != null && service.privateCall.video) {
                        if (!this.currentUserIsVideo || this.callingUser.photo == null) {
                            showCallingAvatarMini = false;
                        } else {
                            showCallingAvatarMini = true;
                        }
                        this.statusTextView.setText(LocaleController.getString("VoipInVideoCallBranding", NUM), true, animated2);
                        this.acceptDeclineView.setTranslationY((float) (-AndroidUtilities.dp(60.0f)));
                        animated = animated2;
                        break;
                    } else {
                        this.statusTextView.setText(LocaleController.getString("VoipInCallBranding", NUM), true, animated2);
                        this.acceptDeclineView.setTranslationY(0.0f);
                        animated = animated2;
                        break;
                    }
                    break;
                case 16:
                    this.statusTextView.setText(LocaleController.getString("VoipRinging", NUM), true, animated2);
                    animated = animated2;
                    break;
                case 17:
                    showAcceptDeclineView = true;
                    this.statusTextView.setText(LocaleController.getString("VoipBusy", NUM), false, animated2);
                    this.acceptDeclineView.setRetryMod(true);
                    this.currentUserIsVideo = false;
                    this.callingUserIsVideo = false;
                    animated = animated2;
                    break;
                default:
                    animated = animated2;
                    break;
            }
            if (this.previewDialog == null) {
                if (service != null) {
                    this.callingUserIsVideo = service.getRemoteVideoState() == 2;
                    boolean z = service.getVideoState(false) == 2 || service.getVideoState(false) == 1;
                    this.currentUserIsVideo = z;
                    if (z && !this.isVideoCall) {
                        this.isVideoCall = true;
                    }
                }
                if (animated) {
                    this.currentUserCameraFloatingLayout.saveRelatedPosition();
                    this.callingUserMiniFloatingLayout.saveRelatedPosition();
                }
                if (this.callingUserIsVideo) {
                    if (!this.switchingToPip) {
                        this.callingUserPhotoView.setAlpha(1.0f);
                    }
                    if (animated) {
                        this.callingUserTextureView.animate().alpha(1.0f).setDuration(250).start();
                    } else {
                        this.callingUserTextureView.animate().cancel();
                        this.callingUserTextureView.setAlpha(1.0f);
                    }
                    if (!this.callingUserTextureView.renderer.isFirstFrameRendered() && !this.enterFromPiP) {
                        this.callingUserIsVideo = false;
                    }
                }
                if (this.currentUserIsVideo || this.callingUserIsVideo) {
                    fillNavigationBar(true, animated);
                } else {
                    fillNavigationBar(false, animated);
                    this.callingUserPhotoView.setVisibility(0);
                    if (animated) {
                        this.callingUserTextureView.animate().alpha(0.0f).setDuration(250).start();
                    } else {
                        this.callingUserTextureView.animate().cancel();
                        this.callingUserTextureView.setAlpha(0.0f);
                    }
                }
                boolean z2 = this.currentUserIsVideo;
                if (!z2 || !this.callingUserIsVideo) {
                    this.cameraForceExpanded = false;
                }
                boolean showCallingUserVideoMini = z2 && this.cameraForceExpanded;
                showCallingUserAvatarMini(showCallingAvatarMini, animated);
                int statusLayoutOffset2 = statusLayoutOffset + (this.callingUserPhotoViewMini.getTag() == null ? 0 : AndroidUtilities.dp(135.0f) + AndroidUtilities.dp(12.0f));
                showAcceptDeclineView(showAcceptDeclineView, animated);
                this.windowView.setLockOnScreen(this.lockOnScreen || this.deviceIsLocked);
                boolean z3 = this.currentState == 3 && (this.currentUserIsVideo || this.callingUserIsVideo);
                this.canHideUI = z3;
                if (!z3 && !this.uiVisible) {
                    showUi(true);
                }
                if (this.uiVisible && this.canHideUI && !this.hideUiRunnableWaiting && service != null && !service.isMicMute()) {
                    AndroidUtilities.runOnUIThread(this.hideUIRunnable, 3000);
                    this.hideUiRunnableWaiting = true;
                } else if (service != null && service.isMicMute()) {
                    AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
                    this.hideUiRunnableWaiting = false;
                }
                if (!this.uiVisible) {
                    statusLayoutOffset2 -= AndroidUtilities.dp(50.0f);
                }
                if (animated) {
                    if (this.lockOnScreen || !this.uiVisible) {
                        if (this.backIcon.getVisibility() != 0) {
                            this.backIcon.setVisibility(0);
                            f = 0.0f;
                            this.backIcon.setAlpha(0.0f);
                        } else {
                            f = 0.0f;
                        }
                        this.backIcon.animate().alpha(f).start();
                    } else {
                        this.backIcon.animate().alpha(1.0f).start();
                    }
                    this.notificationsLayout.animate().translationY((float) ((-AndroidUtilities.dp(16.0f)) - (this.uiVisible ? AndroidUtilities.dp(80.0f) : 0))).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                } else {
                    if (!this.lockOnScreen) {
                        this.backIcon.setVisibility(0);
                    }
                    this.backIcon.setAlpha(this.lockOnScreen ? 0.0f : 1.0f);
                    this.notificationsLayout.setTranslationY((float) ((-AndroidUtilities.dp(16.0f)) - (this.uiVisible ? AndroidUtilities.dp(80.0f) : 0)));
                }
                int i2 = this.currentState;
                if (!(i2 == 10 || i2 == 11)) {
                    updateButtons(animated);
                }
                if (showTimer) {
                    this.statusTextView.showTimer(animated);
                }
                this.statusTextView.showReconnect(showReconnecting, animated);
                if (!animated) {
                    this.statusLayout.setTranslationY((float) statusLayoutOffset2);
                } else if (statusLayoutOffset2 != this.statusLayoutAnimateToOffset) {
                    this.statusLayout.animate().translationY((float) statusLayoutOffset2).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
                this.statusLayoutAnimateToOffset = statusLayoutOffset2;
                this.overlayBackground.setShowBlackout(this.currentUserIsVideo || this.callingUserIsVideo, animated);
                int i3 = this.currentState;
                this.canSwitchToPip = (i3 == 11 || i3 == 17 || (!this.currentUserIsVideo && !this.callingUserIsVideo)) ? false : true;
                if (service != null) {
                    if (this.currentUserIsVideo) {
                        service.sharedUIParams.tapToVideoTooltipWasShowed = true;
                    }
                    this.currentUserTextureView.setIsScreencast(service.isScreencast());
                    this.currentUserTextureView.renderer.setMirror(service.isFrontFaceCamera());
                    service.setSinks((!this.currentUserIsVideo || service.isScreencast()) ? null : this.currentUserTextureView.renderer, showCallingUserVideoMini ? this.callingUserMiniTextureRenderer : this.callingUserTextureView.renderer);
                    if (animated) {
                        this.notificationsLayout.beforeLayoutChanges();
                    }
                    if ((this.currentUserIsVideo || this.callingUserIsVideo) && (((i = this.currentState) == 3 || i == 5) && service.getCallDuration() > 500)) {
                        if (service.getRemoteAudioState() == 0) {
                            this.notificationsLayout.addNotification(NUM, LocaleController.formatString("VoipUserMicrophoneIsOff", NUM, UserObject.getFirstName(this.callingUser)), "muted", animated);
                        } else {
                            this.notificationsLayout.removeNotification("muted");
                        }
                        if (service.getRemoteVideoState() == 0) {
                            this.notificationsLayout.addNotification(NUM, LocaleController.formatString("VoipUserCameraIsOff", NUM, UserObject.getFirstName(this.callingUser)), "video", animated);
                        } else {
                            this.notificationsLayout.removeNotification("video");
                        }
                    } else {
                        if (service.getRemoteAudioState() == 0) {
                            this.notificationsLayout.addNotification(NUM, LocaleController.formatString("VoipUserMicrophoneIsOff", NUM, UserObject.getFirstName(this.callingUser)), "muted", animated);
                        } else {
                            this.notificationsLayout.removeNotification("muted");
                        }
                        this.notificationsLayout.removeNotification("video");
                    }
                    if (this.notificationsLayout.getChildCount() == 0 && this.callingUserIsVideo && service.privateCall != null && !service.privateCall.video && !service.sharedUIParams.tapToVideoTooltipWasShowed) {
                        service.sharedUIParams.tapToVideoTooltipWasShowed = true;
                        this.tapToVideoTooltip.showForView(this.bottomButtons[1], true);
                    } else if (this.notificationsLayout.getChildCount() != 0) {
                        this.tapToVideoTooltip.hide();
                    }
                    if (animated) {
                        this.notificationsLayout.animateLayoutChanges();
                    }
                }
                int floatingViewsOffset = this.notificationsLayout.getChildsHight();
                this.callingUserMiniFloatingLayout.setBottomOffset(floatingViewsOffset, animated);
                this.currentUserCameraFloatingLayout.setBottomOffset(floatingViewsOffset, animated);
                this.currentUserCameraFloatingLayout.setUiVisible(this.uiVisible);
                this.callingUserMiniFloatingLayout.setUiVisible(this.uiVisible);
                if (!this.currentUserIsVideo) {
                    showFloatingLayout(0, animated);
                } else if (!this.callingUserIsVideo || this.cameraForceExpanded) {
                    showFloatingLayout(1, animated);
                } else {
                    showFloatingLayout(2, animated);
                }
                if (showCallingUserVideoMini && this.callingUserMiniFloatingLayout.getTag() == null) {
                    this.callingUserMiniFloatingLayout.setIsActive(true);
                    if (this.callingUserMiniFloatingLayout.getVisibility() != 0) {
                        this.callingUserMiniFloatingLayout.setVisibility(0);
                        this.callingUserMiniFloatingLayout.setAlpha(0.0f);
                        this.callingUserMiniFloatingLayout.setScaleX(0.5f);
                        this.callingUserMiniFloatingLayout.setScaleY(0.5f);
                    }
                    this.callingUserMiniFloatingLayout.animate().setListener((Animator.AnimatorListener) null).cancel();
                    this.callingUserMiniFloatingLayout.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).setStartDelay(150).start();
                    this.callingUserMiniFloatingLayout.setTag(1);
                } else if (!showCallingUserVideoMini && this.callingUserMiniFloatingLayout.getTag() != null) {
                    this.callingUserMiniFloatingLayout.setIsActive(false);
                    this.callingUserMiniFloatingLayout.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (VoIPFragment.this.callingUserMiniFloatingLayout.getTag() == null) {
                                VoIPFragment.this.callingUserMiniFloatingLayout.setVisibility(8);
                            }
                        }
                    }).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    this.callingUserMiniFloatingLayout.setTag((Object) null);
                }
                this.currentUserCameraFloatingLayout.restoreRelativePosition();
                this.callingUserMiniFloatingLayout.restoreRelativePosition();
                updateSpeakerPhoneIcon();
            }
        }
    }

    /* renamed from: lambda$updateViewState$15$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4065lambda$updateViewState$15$orgtelegramuiVoIPFragment() {
        this.windowView.finish();
    }

    /* renamed from: lambda$updateViewState$16$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4066lambda$updateViewState$16$orgtelegramuiVoIPFragment(DialogInterface dialogInterface, int i) {
        this.windowView.finish();
    }

    /* renamed from: lambda$updateViewState$17$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4067lambda$updateViewState$17$orgtelegramuiVoIPFragment(boolean[] callAgain, DialogInterface dialogInterface, int i) {
        callAgain[0] = true;
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
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$updateViewState$18$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4068lambda$updateViewState$18$orgtelegramuiVoIPFragment(boolean[] callAgain, DialogInterface dialog) {
        if (!callAgain[0]) {
            this.windowView.finish();
        }
    }

    /* renamed from: lambda$updateViewState$19$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4069lambda$updateViewState$19$orgtelegramuiVoIPFragment() {
        this.windowView.finish();
    }

    /* renamed from: lambda$updateViewState$20$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4070lambda$updateViewState$20$orgtelegramuiVoIPFragment() {
        this.windowView.finish();
    }

    private void fillNavigationBar(boolean fill, boolean animated) {
        if (!this.switchingToPip) {
            float f = 0.0f;
            float f2 = 1.0f;
            if (!animated) {
                ValueAnimator valueAnimator = this.naviagtionBarAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                if (fill) {
                    f = 1.0f;
                }
                this.fillNaviagtionBarValue = f;
                Paint paint = this.overlayBottomPaint;
                if (!fill) {
                    f2 = 0.5f;
                }
                paint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (f2 * 255.0f)));
            } else if (fill != this.fillNaviagtionBar) {
                ValueAnimator valueAnimator2 = this.naviagtionBarAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.fillNaviagtionBarValue;
                if (fill) {
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
            this.fillNaviagtionBar = fill;
        }
    }

    /* access modifiers changed from: private */
    public void showUi(boolean show) {
        ValueAnimator valueAnimator = this.uiVisibilityAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int i = 0;
        if (!show && this.uiVisible) {
            this.speakerPhoneIcon.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(50.0f))).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.backIcon.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(50.0f))).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.emojiLayout.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(50.0f))).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.statusLayout.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.buttonsLayout.animate().alpha(0.0f).translationY((float) AndroidUtilities.dp(50.0f)).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.bottomShadow.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.topShadow.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.uiVisibilityAlpha, 0.0f});
            this.uiVisibilityAnimator = ofFloat;
            ofFloat.addUpdateListener(this.statusbarAnimatorListener);
            this.uiVisibilityAnimator.setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.uiVisibilityAnimator.start();
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.buttonsLayout.setEnabled(false);
        } else if (show && !this.uiVisible) {
            this.tapToVideoTooltip.hide();
            this.speakerPhoneIcon.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.backIcon.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.emojiLayout.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.statusLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.buttonsLayout.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.bottomShadow.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.topShadow.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.uiVisibilityAlpha, 1.0f});
            this.uiVisibilityAnimator = ofFloat2;
            ofFloat2.addUpdateListener(this.statusbarAnimatorListener);
            this.uiVisibilityAnimator.setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.uiVisibilityAnimator.start();
            this.buttonsLayout.setEnabled(true);
        }
        this.uiVisible = show;
        this.windowView.requestFullscreen(!show);
        ViewPropertyAnimator animate = this.notificationsLayout.animate();
        int i2 = -AndroidUtilities.dp(16.0f);
        if (this.uiVisible) {
            i = AndroidUtilities.dp(80.0f);
        }
        animate.translationY((float) (i2 - i)).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    private void showFloatingLayout(int state, boolean animated) {
        Animator animator;
        if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) {
            this.currentUserCameraFloatingLayout.setUiVisible(this.uiVisible);
        }
        if (!animated && (animator = this.cameraShowingAnimator) != null) {
            animator.removeAllListeners();
            this.cameraShowingAnimator.cancel();
        }
        boolean z = true;
        if (state != 0) {
            boolean swtichToFloatAnimated = animated;
            if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0) {
                swtichToFloatAnimated = false;
            }
            if (!animated) {
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
            if ((this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) && this.currentUserCameraFloatingLayout.relativePositionToSetX < 0.0f) {
                this.currentUserCameraFloatingLayout.setRelativePosition(1.0f, 1.0f);
            }
            VoIPFloatingLayout voIPFloatingLayout = this.currentUserCameraFloatingLayout;
            if (state != 2) {
                z = false;
            }
            voIPFloatingLayout.setFloatingMode(z, swtichToFloatAnimated);
        } else if (!animated) {
            this.currentUserCameraFloatingLayout.setVisibility(8);
        } else if (!(this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0)) {
            Animator animator3 = this.cameraShowingAnimator;
            if (animator3 != null) {
                animator3.removeAllListeners();
                this.cameraShowingAnimator.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.ALPHA, new float[]{this.currentUserCameraFloatingLayout.getAlpha(), 0.0f})});
            if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 2) {
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.SCALE_X, new float[]{this.currentUserCameraFloatingLayout.getScaleX(), 0.7f}), ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.SCALE_Y, new float[]{this.currentUserCameraFloatingLayout.getScaleX(), 0.7f})});
            }
            this.cameraShowingAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
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
        this.currentUserCameraFloatingLayout.setTag(Integer.valueOf(state));
    }

    private void showCallingUserAvatarMini(boolean show, boolean animated) {
        int i = 0;
        int i2 = null;
        if (!animated) {
            this.callingUserPhotoViewMini.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.callingUserPhotoViewMini.setTranslationY(0.0f);
            this.callingUserPhotoViewMini.setAlpha(1.0f);
            BackupImageView backupImageView = this.callingUserPhotoViewMini;
            if (!show) {
                i = 8;
            }
            backupImageView.setVisibility(i);
        } else if (show && this.callingUserPhotoViewMini.getTag() == null) {
            this.callingUserPhotoViewMini.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.callingUserPhotoViewMini.setVisibility(0);
            this.callingUserPhotoViewMini.setAlpha(0.0f);
            this.callingUserPhotoViewMini.setTranslationY((float) (-AndroidUtilities.dp(135.0f)));
            this.callingUserPhotoViewMini.animate().alpha(1.0f).translationY(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        } else if (!show && this.callingUserPhotoViewMini.getTag() != null) {
            this.callingUserPhotoViewMini.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.callingUserPhotoViewMini.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(135.0f))).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    VoIPFragment.this.callingUserPhotoViewMini.setVisibility(8);
                }
            }).start();
        }
        BackupImageView backupImageView2 = this.callingUserPhotoViewMini;
        if (show) {
            i2 = 1;
        }
        backupImageView2.setTag(i2);
    }

    private void updateKeyView(boolean animated) {
        VoIPService service;
        if (!this.emojiLoaded && (service = VoIPService.getSharedInstance()) != null) {
            byte[] auth_key = null;
            try {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                buf.write(service.getEncryptionKey());
                buf.write(service.getGA());
                auth_key = buf.toByteArray();
            } catch (Exception checkedExceptionsAreBad) {
                FileLog.e((Throwable) checkedExceptionsAreBad);
            }
            if (auth_key != null) {
                String[] emoji = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(auth_key, 0, auth_key.length));
                for (int i = 0; i < 4; i++) {
                    Emoji.preloadEmoji(emoji[i]);
                    Emoji.EmojiDrawable drawable = Emoji.getEmojiDrawable(emoji[i]);
                    if (drawable != null) {
                        drawable.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
                        drawable.preload();
                        this.emojiViews[i].setImageDrawable(drawable);
                        this.emojiViews[i].setContentDescription(emoji[i]);
                        this.emojiViews[i].setVisibility(8);
                    }
                    this.emojiDrawables[i] = drawable;
                }
                checkEmojiLoaded(animated);
            }
        }
    }

    private void checkEmojiLoaded(boolean animated) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            Emoji.EmojiDrawable[] emojiDrawableArr = this.emojiDrawables;
            if (emojiDrawableArr[i] != null && emojiDrawableArr[i].isLoaded()) {
                count++;
            }
        }
        if (count == 4) {
            this.emojiLoaded = true;
            for (int i2 = 0; i2 < 4; i2++) {
                if (this.emojiViews[i2].getVisibility() != 0) {
                    this.emojiViews[i2].setVisibility(0);
                    if (animated) {
                        this.emojiViews[i2].setAlpha(0.0f);
                        this.emojiViews[i2].setTranslationY((float) AndroidUtilities.dp(30.0f));
                        this.emojiViews[i2].animate().alpha(1.0f).translationY(0.0f).setDuration(200).setStartDelay((long) (i2 * 20)).start();
                    }
                }
            }
        }
    }

    private void showAcceptDeclineView(boolean show, boolean animated) {
        int i = 0;
        int i2 = null;
        if (!animated) {
            AcceptDeclineView acceptDeclineView2 = this.acceptDeclineView;
            if (!show) {
                i = 8;
            }
            acceptDeclineView2.setVisibility(i);
        } else {
            if (show && this.acceptDeclineView.getTag() == null) {
                this.acceptDeclineView.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (this.acceptDeclineView.getVisibility() == 8) {
                    this.acceptDeclineView.setVisibility(0);
                    this.acceptDeclineView.setAlpha(0.0f);
                }
                this.acceptDeclineView.animate().alpha(1.0f);
            }
            if (!show && this.acceptDeclineView.getTag() != null) {
                this.acceptDeclineView.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.acceptDeclineView.animate().setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPFragment.this.acceptDeclineView.setVisibility(8);
                    }
                }).alpha(0.0f);
            }
        }
        this.acceptDeclineView.setEnabled(show);
        AcceptDeclineView acceptDeclineView3 = this.acceptDeclineView;
        if (show) {
            i2 = 1;
        }
        acceptDeclineView3.setTag(i2);
    }

    private void updateButtons(boolean animated) {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (animated && Build.VERSION.SDK_INT >= 19) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new Visibility() {
                    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(100.0f), 0.0f});
                        if (view instanceof VoIPToggleButton) {
                            view.setTranslationY((float) AndroidUtilities.dp(100.0f));
                            animator.setStartDelay((long) ((VoIPToggleButton) view).animationDelay);
                        }
                        return animator;
                    }

                    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{view.getTranslationY(), (float) AndroidUtilities.dp(100.0f)});
                    }
                }.setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT)).addTransition(new ChangeBounds().setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT));
                transitionSet.excludeChildren(VoIPToggleButton.class, true);
                TransitionManager.beginDelayedTransition(this.buttonsLayout, transitionSet);
            }
            int i = this.currentState;
            if (i == 15 || i == 17) {
                if (service.privateCall == null || !service.privateCall.video || this.currentState != 15) {
                    this.bottomButtons[0].setVisibility(8);
                    this.bottomButtons[1].setVisibility(8);
                    this.bottomButtons[2].setVisibility(8);
                } else {
                    if (service.isScreencast() || (!this.currentUserIsVideo && !this.callingUserIsVideo)) {
                        setSpeakerPhoneAction(this.bottomButtons[0], service, animated);
                        this.speakerPhoneIcon.animate().alpha(0.0f).start();
                    } else {
                        setFrontalCameraAction(this.bottomButtons[0], service, animated);
                        if (this.uiVisible) {
                            this.speakerPhoneIcon.animate().alpha(1.0f).start();
                        }
                    }
                    setVideoAction(this.bottomButtons[1], service, animated);
                    setMicrohoneAction(this.bottomButtons[2], service, animated);
                }
                this.bottomButtons[3].setVisibility(8);
            } else if (instance != null) {
                if (service.isScreencast() || (!this.currentUserIsVideo && !this.callingUserIsVideo)) {
                    setSpeakerPhoneAction(this.bottomButtons[0], service, animated);
                    this.speakerPhoneIcon.setTag((Object) null);
                    this.speakerPhoneIcon.animate().alpha(0.0f).start();
                } else {
                    setFrontalCameraAction(this.bottomButtons[0], service, animated);
                    if (this.uiVisible) {
                        this.speakerPhoneIcon.setTag(1);
                        this.speakerPhoneIcon.animate().alpha(1.0f).start();
                    }
                }
                setVideoAction(this.bottomButtons[1], service, animated);
                setMicrohoneAction(this.bottomButtons[2], service, animated);
                this.bottomButtons[3].setData(NUM, -1, -1041108, LocaleController.getString("VoipEndCall", NUM), false, animated);
                this.bottomButtons[3].setOnClickListener(VoIPFragment$$ExternalSyntheticLambda10.INSTANCE);
            } else {
                return;
            }
            int animationDelay = 0;
            for (int i2 = 0; i2 < 4; i2++) {
                if (this.bottomButtons[i2].getVisibility() == 0) {
                    this.bottomButtons[i2].animationDelay = animationDelay;
                    animationDelay += 16;
                }
            }
            updateSpeakerPhoneIcon();
        }
    }

    static /* synthetic */ void lambda$updateButtons$21(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp();
        }
    }

    private void setMicrohoneAction(VoIPToggleButton bottomButton, VoIPService service, boolean animated) {
        if (service.isMicMute()) {
            bottomButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipUnmute", NUM), true, animated);
        } else {
            bottomButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipMute", NUM), false, animated);
        }
        this.currentUserCameraFloatingLayout.setMuted(service.isMicMute(), animated);
        bottomButton.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda6(this));
    }

    /* renamed from: lambda$setMicrohoneAction$22$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4058lambda$setMicrohoneAction$22$orgtelegramuiVoIPFragment(View view) {
        String text;
        VoIPService serviceInstance = VoIPService.getSharedInstance();
        if (serviceInstance != null) {
            boolean micMute = !serviceInstance.isMicMute();
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (micMute) {
                    text = LocaleController.getString("AccDescrVoipMicOff", NUM);
                } else {
                    text = LocaleController.getString("AccDescrVoipMicOn", NUM);
                }
                view.announceForAccessibility(text);
            }
            serviceInstance.setMicMute(micMute, false, true);
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    private void setVideoAction(VoIPToggleButton bottomButton, VoIPService service, boolean animated) {
        boolean isVideoAvailable;
        if (this.currentUserIsVideo || this.callingUserIsVideo) {
            isVideoAvailable = true;
        } else {
            isVideoAvailable = service.isVideoAvailable();
        }
        if (isVideoAvailable) {
            if (this.currentUserIsVideo) {
                bottomButton.setData(service.isScreencast() ? NUM : NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipStopVideo", NUM), false, animated);
            } else {
                bottomButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipStartVideo", NUM), true, animated);
            }
            bottomButton.setCrossOffset(-AndroidUtilities.dpf2(3.5f));
            bottomButton.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda9(this, service));
            bottomButton.setEnabled(true);
            return;
        }
        bottomButton.setData(NUM, ColorUtils.setAlphaComponent(-1, 127), ColorUtils.setAlphaComponent(-1, 30), "Video", false, animated);
        bottomButton.setOnClickListener((View.OnClickListener) null);
        bottomButton.setEnabled(false);
    }

    /* renamed from: lambda$setVideoAction$24$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4061lambda$setVideoAction$24$orgtelegramuiVoIPFragment(VoIPService service, View view) {
        if (Build.VERSION.SDK_INT >= 23 && this.activity.checkSelfPermission("android.permission.CAMERA") != 0) {
            this.activity.requestPermissions(new String[]{"android.permission.CAMERA"}, 102);
        } else if (Build.VERSION.SDK_INT >= 21 || service.privateCall == null || service.privateCall.video || this.callingUserIsVideo || service.sharedUIParams.cameraAlertWasShowed) {
            toggleCameraInput();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.activity);
            builder.setMessage(LocaleController.getString("VoipSwitchToVideoCall", NUM));
            builder.setPositiveButton(LocaleController.getString("VoipSwitch", NUM), new VoIPFragment$$ExternalSyntheticLambda25(this, service));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.create().show();
        }
    }

    /* renamed from: lambda$setVideoAction$23$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4060lambda$setVideoAction$23$orgtelegramuiVoIPFragment(VoIPService service, DialogInterface dialogInterface, int i) {
        service.sharedUIParams.cameraAlertWasShowed = true;
        toggleCameraInput();
    }

    private void updateSpeakerPhoneIcon() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (service.isBluetoothOn()) {
                this.speakerPhoneIcon.setImageResource(NUM);
            } else if (service.isSpeakerphoneOn()) {
                this.speakerPhoneIcon.setImageResource(NUM);
            } else if (service.isHeadsetPlugged()) {
                this.speakerPhoneIcon.setImageResource(NUM);
            } else {
                this.speakerPhoneIcon.setImageResource(NUM);
            }
        }
    }

    private void setSpeakerPhoneAction(VoIPToggleButton bottomButton, VoIPService service, boolean animated) {
        VoIPToggleButton voIPToggleButton = bottomButton;
        boolean z = animated;
        if (service.isBluetoothOn()) {
            bottomButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipAudioRoutingBluetooth", NUM), false, animated);
            bottomButton.setChecked(false, z);
        } else if (service.isSpeakerphoneOn()) {
            bottomButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipSpeaker", NUM), false, animated);
            bottomButton.setChecked(true, z);
        } else {
            bottomButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipSpeaker", NUM), false, animated);
            bottomButton.setChecked(false, z);
        }
        bottomButton.setCheckableForAccessibility(true);
        bottomButton.setEnabled(true);
        bottomButton.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda7(this));
    }

    /* renamed from: lambda$setSpeakerPhoneAction$25$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4059lambda$setSpeakerPhoneAction$25$orgtelegramuiVoIPFragment(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
        }
    }

    private void setFrontalCameraAction(VoIPToggleButton bottomButton, VoIPService service, boolean animated) {
        if (!this.currentUserIsVideo) {
            bottomButton.setData(NUM, ColorUtils.setAlphaComponent(-1, 127), ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipFlip", NUM), false, animated);
            bottomButton.setOnClickListener((View.OnClickListener) null);
            bottomButton.setEnabled(false);
            return;
        }
        bottomButton.setEnabled(true);
        if (!service.isFrontFaceCamera()) {
            bottomButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipFlip", NUM), false, animated);
        } else {
            bottomButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipFlip", NUM), false, animated);
        }
        bottomButton.setOnClickListener(new VoIPFragment$$ExternalSyntheticLambda8(this, service));
    }

    /* renamed from: lambda$setFrontalCameraAction$26$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4057lambda$setFrontalCameraAction$26$orgtelegramuiVoIPFragment(VoIPService service, View view) {
        String text;
        VoIPService serviceInstance = VoIPService.getSharedInstance();
        if (serviceInstance != null) {
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (service.isFrontFaceCamera()) {
                    text = LocaleController.getString("AccDescrVoipCamSwitchedToBack", NUM);
                } else {
                    text = LocaleController.getString("AccDescrVoipCamSwitchedToFront", NUM);
                }
                view.announceForAccessibility(text);
            }
            serviceInstance.switchCamera();
        }
    }

    public void onScreenCastStart() {
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.dismiss(true, true);
        }
    }

    private void toggleCameraInput() {
        String text;
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (!this.currentUserIsVideo) {
                    text = LocaleController.getString("AccDescrVoipCamOn", NUM);
                } else {
                    text = LocaleController.getString("AccDescrVoipCamOff", NUM);
                }
                this.fragmentView.announceForAccessibility(text);
            }
            if (this.currentUserIsVideo) {
                this.currentUserTextureView.saveCameraLastBitmap();
                service.setVideoState(false, 0);
                if (Build.VERSION.SDK_INT >= 21) {
                    service.clearCamera();
                }
            } else if (Build.VERSION.SDK_INT < 21) {
                this.currentUserIsVideo = true;
                if (!service.isSpeakerphoneOn()) {
                    VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
                }
                service.requestVideoCall(false);
                service.setVideoState(false, 2);
            } else if (this.previewDialog == null) {
                service.createCaptureDevice(false);
                if (!service.isFrontFaceCamera()) {
                    service.switchCamera();
                }
                this.windowView.setLockOnScreen(true);
                AnonymousClass20 r1 = new PrivateVideoPreviewDialog(this.fragmentView.getContext(), false, true) {
                    public void onDismiss(boolean screencast, boolean apply) {
                        PrivateVideoPreviewDialog unused = VoIPFragment.this.previewDialog = null;
                        VoIPService service = VoIPService.getSharedInstance();
                        VoIPFragment.this.windowView.setLockOnScreen(false);
                        if (apply) {
                            VoIPFragment.this.currentUserIsVideo = true;
                            if (service != null && !screencast) {
                                service.requestVideoCall(false);
                                service.setVideoState(false, 2);
                            }
                        } else if (service != null) {
                            service.setVideoState(false, 0);
                        }
                        VoIPFragment voIPFragment = VoIPFragment.this;
                        int unused2 = voIPFragment.previousState = voIPFragment.currentState;
                        VoIPFragment.this.updateViewState();
                    }
                };
                this.previewDialog = r1;
                WindowInsets windowInsets = this.lastInsets;
                if (windowInsets != null) {
                    r1.setBottomPadding(windowInsets.getSystemWindowInsetBottom());
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

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onRequestPermissionsResultInternal(requestCode, permissions, grantResults);
        }
    }

    private void onRequestPermissionsResultInternal(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (VoIPService.getSharedInstance() == null) {
                this.windowView.finish();
                return;
            } else if (grantResults.length > 0 && grantResults[0] == 0) {
                VoIPService.getSharedInstance().acceptIncomingCall();
            } else if (!this.activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
                VoIPService.getSharedInstance().declineIncomingCall();
                VoIPHelper.permissionDenied(this.activity, new VoIPFragment$$ExternalSyntheticLambda14(this), requestCode);
                return;
            }
        }
        if (requestCode != 102) {
            return;
        }
        if (VoIPService.getSharedInstance() == null) {
            this.windowView.finish();
        } else if (grantResults.length > 0 && grantResults[0] == 0) {
            toggleCameraInput();
        }
    }

    /* renamed from: lambda$onRequestPermissionsResultInternal$27$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4055xa48eddec() {
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
        boolean screenOn;
        VoIPService service;
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        PowerManager pm = (PowerManager) this.activity.getSystemService("power");
        if (Build.VERSION.SDK_INT >= 20) {
            screenOn = pm.isInteractive();
        } else {
            screenOn = pm.isScreenOn();
        }
        boolean hasPermissionsToPip = AndroidUtilities.checkInlinePermissions(this.activity);
        if (this.canSwitchToPip && hasPermissionsToPip) {
            int h = instance.windowView.getMeasuredHeight();
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                h -= windowInsets2.getSystemWindowInsetBottom();
            }
            VoIPFragment voIPFragment = instance;
            VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), h, 0);
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                VoIPPiPView.topInset = windowInsets.getSystemWindowInsetTop();
                VoIPPiPView.bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
            }
        }
        if (!this.currentUserIsVideo) {
            return;
        }
        if ((!hasPermissionsToPip || !screenOn) && (service = VoIPService.getSharedInstance()) != null) {
            service.setVideoState(false, 1);
        }
    }

    public void onResumeInternal() {
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.finish();
        }
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (service.getVideoState(false) == 1) {
                service.setVideoState(false, 2);
            }
            updateViewState();
        } else {
            this.windowView.finish();
        }
        this.deviceIsLocked = ((KeyguardManager) this.activity.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
    }

    private void showErrorDialog(CharSequence message) {
        if (!this.activity.isFinishing()) {
            AlertDialog dlg = new DarkAlertDialog.Builder(this.activity).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(message).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
            dlg.setCanceledOnTouchOutside(true);
            dlg.setOnDismissListener(new VoIPFragment$$ExternalSyntheticLambda27(this));
        }
    }

    /* renamed from: lambda$showErrorDialog$28$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4062lambda$showErrorDialog$28$orgtelegramuiVoIPFragment(DialogInterface dialog) {
        this.windowView.finish();
    }

    private void requestInlinePermissions() {
        if (Build.VERSION.SDK_INT >= 21) {
            AlertsCreator.createDrawOverlayPermissionDialog(this.activity, new VoIPFragment$$ExternalSyntheticLambda23(this)).show();
        }
    }

    /* renamed from: lambda$requestInlinePermissions$29$org-telegram-ui-VoIPFragment  reason: not valid java name */
    public /* synthetic */ void m4056lambda$requestInlinePermissions$29$orgtelegramuiVoIPFragment(DialogInterface dialogInterface, int i) {
        VoIPWindowView voIPWindowView = this.windowView;
        if (voIPWindowView != null) {
            voIPWindowView.finish();
        }
    }
}
