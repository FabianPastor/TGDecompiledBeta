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
import org.telegram.ui.VoIPFragment;
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
    public final int currentAccount;
    /* access modifiers changed from: private */
    public int currentState;
    TLRPC$User currentUser;
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
    Runnable hideUIRunnable = new Runnable() {
        public final void run() {
            VoIPFragment.this.lambda$new$2$VoIPFragment();
        }
    };
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
    ValueAnimator.AnimatorUpdateListener navigationBarAnimationListener = new ValueAnimator.AnimatorUpdateListener() {
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFragment.this.lambda$new$1$VoIPFragment(valueAnimator);
        }
    };
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
    ValueAnimator.AnimatorUpdateListener statusbarAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFragment.this.lambda$new$0$VoIPFragment(valueAnimator);
        }
    };
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

    public void onScreenOnChange(boolean z) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$VoIPFragment(ValueAnimator valueAnimator) {
        this.uiVisibilityAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$VoIPFragment(ValueAnimator valueAnimator) {
        this.fillNaviagtionBarValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$VoIPFragment() {
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
                    r9.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                        public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                            return VoIPFragment.lambda$show$3(VoIPFragment.this, view, windowInsets);
                        }
                    });
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

    static /* synthetic */ WindowInsets lambda$show$3(VoIPFragment voIPFragment, View view, WindowInsets windowInsets) {
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
        this.currentUser = MessagesController.getInstance(i).getUser(Integer.valueOf(UserConfig.getInstance(i).getClientUserId()));
        this.callingUser = VoIPService.getSharedInstance().getUser();
        VoIPService.getSharedInstance().registerStateListener(this);
        this.isOutgoing = VoIPService.getSharedInstance().isOutgoing();
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
                    VoIPFragment voIPFragment = VoIPFragment.this;
                    float f2 = voIPFragment.touchSlop;
                    if (f < f2 * f2 && currentTimeMillis - this.pressedTime < 300 && currentTimeMillis - voIPFragment.lastContentTapTime > 300) {
                        voIPFragment.lastContentTapTime = System.currentTimeMillis();
                        if (VoIPFragment.this.emojiExpanded) {
                            VoIPFragment.this.expandEmoji(false);
                        } else if (VoIPFragment.this.canHideUI) {
                            VoIPFragment voIPFragment2 = VoIPFragment.this;
                            voIPFragment2.showUi(!voIPFragment2.uiVisible);
                            VoIPFragment voIPFragment3 = VoIPFragment.this;
                            int unused = voIPFragment3.previousState = voIPFragment3.currentState;
                            VoIPFragment.this.updateViewState();
                        }
                    }
                    this.check = false;
                }
                return this.check;
            }
        };
        boolean z = false;
        r8.setClipToPadding(false);
        r8.setClipChildren(false);
        updateSystemBarColors();
        this.fragmentView = r8;
        r8.setFitsSystemWindows(true);
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
        TextureViewRenderer textureViewRenderer = voIPTextureView.renderer;
        RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
        textureViewRenderer.setScalingType(scalingType);
        this.callingUserTextureView.renderer.setEnableHardwareScaler(true);
        this.callingUserTextureView.scaleType = VoIPTextureView.SCALE_TYPE_NONE;
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
        this.callingUserPhotoView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                VoIPFragment.this.lambda$createView$4$VoIPFragment(imageReceiver, z, z2, z3);
            }

            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        });
        this.callingUserPhotoView.setImage(ImageLocation.getForUserOrChat(this.callingUser, 0), (String) null, (Drawable) backgroundGradientDrawable, (Object) this.callingUser);
        VoIPFloatingLayout voIPFloatingLayout = new VoIPFloatingLayout(context2);
        this.currentUserCameraFloatingLayout = voIPFloatingLayout;
        voIPFloatingLayout.setDelegate(new VoIPFloatingLayout.VoIPFloatingLayoutDelegate() {
            public final void onChange(float f, boolean z) {
                VoIPFragment.this.lambda$createView$5$VoIPFragment(f, z);
            }
        });
        this.currentUserCameraFloatingLayout.setRelativePosition(1.0f, 1.0f);
        VoIPTextureView voIPTextureView2 = new VoIPTextureView(context2, true, false);
        this.currentUserTextureView = voIPTextureView2;
        voIPTextureView2.renderer.setIsCamera(true);
        this.currentUserTextureView.renderer.setUseCameraRotation(true);
        this.currentUserCameraFloatingLayout.setOnTapListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$6$VoIPFragment(view);
            }
        });
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
        this.callingUserMiniFloatingLayout.setOnTapListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$7$VoIPFragment(view);
            }
        });
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
        this.emojiLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$8$VoIPFragment(view);
            }
        });
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

            public void onDicline() {
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
        AnonymousClass8 r13 = new ImageView(context2) {
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
        this.speakerPhoneIcon.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$9$VoIPFragment(view);
            }
        });
        this.backIcon.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$10$VoIPFragment(view);
            }
        });
        if (this.windowView.isLockOnScreen()) {
            this.backIcon.setVisibility(8);
        }
        VoIPNotificationsLayout voIPNotificationsLayout = new VoIPNotificationsLayout(context2);
        this.notificationsLayout = voIPNotificationsLayout;
        voIPNotificationsLayout.setGravity(80);
        this.notificationsLayout.setOnViewsUpdated(new Runnable() {
            public final void run() {
                VoIPFragment.this.lambda$createView$11$VoIPFragment();
            }
        });
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
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$VoIPFragment(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (bitmapSafe != null) {
            this.overlayBackground.setBackground(bitmapSafe);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$VoIPFragment(float f, boolean z) {
        this.currentUserTextureView.setScreenshareMiniProgress(f, z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$VoIPFragment(View view) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ void lambda$createView$7$VoIPFragment(View view) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$VoIPFragment(View view) {
        if (System.currentTimeMillis() - this.lastContentTapTime >= 500) {
            this.lastContentTapTime = System.currentTimeMillis();
            if (this.emojiLoaded) {
                expandEmoji(!this.emojiExpanded);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$9 */
    public /* synthetic */ void lambda$createView$9$VoIPFragment(View view) {
        if (this.speakerPhoneIcon.getTag() != null && VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$10 */
    public /* synthetic */ void lambda$createView$10$VoIPFragment(View view) {
        if (!this.lockOnScreen) {
            onBackPressed();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$11 */
    public /* synthetic */ void lambda$createView$11$VoIPFragment() {
        this.previousState = this.currentState;
        updateViewState();
    }

    private void initRenderers() {
        this.currentUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            public void onFrameResolutionChanged(int i, int i2, int i3) {
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onFirstFrameRendered$0 */
            public /* synthetic */ void lambda$onFirstFrameRendered$0$VoIPFragment$9() {
                VoIPFragment.this.updateViewState();
            }

            public void onFirstFrameRendered() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        VoIPFragment.AnonymousClass9.this.lambda$onFirstFrameRendered$0$VoIPFragment$9();
                    }
                });
            }
        });
        this.callingUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            public void onFrameResolutionChanged(int i, int i2, int i3) {
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onFirstFrameRendered$0 */
            public /* synthetic */ void lambda$onFirstFrameRendered$0$VoIPFragment$10() {
                VoIPFragment.this.updateViewState();
            }

            public void onFirstFrameRendered() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        VoIPFragment.AnonymousClass10.this.lambda$onFirstFrameRendered$0$VoIPFragment$10();
                    }
                });
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
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                VoIPFragment.AnonymousClass11.this.lambda$onAnimationEnd$0$VoIPFragment$11();
                            }
                        }, 200);
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onAnimationEnd$0 */
                    public /* synthetic */ void lambda$onAnimationEnd$0$VoIPFragment$11() {
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
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VoIPFragment.this.lambda$startTransitionFromPiP$13$VoIPFragment();
            }
        }, 32);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startTransitionFromPiP$13 */
    public /* synthetic */ void lambda$startTransitionFromPiP$13$VoIPFragment() {
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
        AndroidUtilities.runOnUIThread(new Runnable(createPiPTransition) {
            public final /* synthetic */ Animator f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPFragment.this.lambda$startTransitionFromPiP$12$VoIPFragment(this.f$1);
            }
        }, 32);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startTransitionFromPiP$12 */
    public /* synthetic */ void lambda$startTransitionFromPiP$12$VoIPFragment(Animator animator) {
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
        $$Lambda$VoIPFragment$_7xuaUvar_gmlkT8ydLHk_4xXJmo r22 = r0;
        $$Lambda$VoIPFragment$_7xuaUvar_gmlkT8ydLHk_4xXJmo r0 = new ValueAnimator.AnimatorUpdateListener(this, z2, scaleX, f3, x, f2, y, f, dp, dp2, 1.0f, f4, 1.0f, var_, 0.0f, measuredWidth, 0.0f, measuredHeight) {
            public final /* synthetic */ VoIPFragment f$0;
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ float f$10;
            public final /* synthetic */ float f$11;
            public final /* synthetic */ float f$12;
            public final /* synthetic */ float f$13;
            public final /* synthetic */ float f$14;
            public final /* synthetic */ float f$15;
            public final /* synthetic */ float f$16;
            public final /* synthetic */ float f$17;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ float f$3;
            public final /* synthetic */ float f$4;
            public final /* synthetic */ float f$5;
            public final /* synthetic */ float f$6;
            public final /* synthetic */ float f$7;
            public final /* synthetic */ float f$8;
            public final /* synthetic */ float f$9;

            {
                this.f$0 = r3;
                this.f$1 = r4;
                this.f$2 = r5;
                this.f$3 = r6;
                this.f$4 = r7;
                this.f$5 = r8;
                this.f$6 = r9;
                this.f$7 = r10;
                this.f$8 = r11;
                this.f$9 = r12;
                this.f$10 = r13;
                this.f$11 = r14;
                this.f$12 = r15;
                this.f$13 = r16;
                this.f$14 = r17;
                this.f$15 = r18;
                this.f$16 = r19;
                this.f$17 = r20;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ValueAnimator valueAnimator2 = valueAnimator;
                VoIPFragment voIPFragment = this.f$0;
                VoIPFragment voIPFragment2 = voIPFragment;
                voIPFragment2.lambda$createPiPTransition$14$VoIPFragment(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, valueAnimator2);
            }
        };
        ValueAnimator valueAnimator2 = valueAnimator;
        valueAnimator2.addUpdateListener(r22);
        return valueAnimator2;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createPiPTransition$14 */
    public /* synthetic */ void lambda$createPiPTransition$14$VoIPFragment(boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float var_, float var_, float var_, float var_, float var_, float var_, float var_, ValueAnimator valueAnimator) {
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
        this.callingUserTextureView.setRoundCorners(((floatValue * ((float) AndroidUtilities.dp(4.0f))) * 1.0f) / var_);
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
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0082, code lost:
        r11 = true;
     */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02d8  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0303  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0316  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0318  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0324  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0326  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0342  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0344  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0355  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0357  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0394  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x03a3  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x043d  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0449  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0466  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0479  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x047b  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x048f  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0491  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0497  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x05a5  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x05cc  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x05e0  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x05f0  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x064e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0294  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateViewState() {
        /*
            r20 = this;
            r0 = r20
            boolean r1 = r0.isFinished
            if (r1 != 0) goto L_0x0699
            boolean r1 = r0.switchingToPip
            if (r1 != 0) goto L_0x0699
            org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r1 = r0.previewDialog
            if (r1 == 0) goto L_0x0010
            goto L_0x0699
        L_0x0010:
            r1 = 0
            r0.lockOnScreen = r1
            int r2 = r0.previousState
            r3 = -1
            r4 = 1
            if (r2 == r3) goto L_0x001b
            r2 = 1
            goto L_0x001c
        L_0x001b:
            r2 = 0
        L_0x001c:
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r5 = 2
            if (r3 == 0) goto L_0x0048
            int r6 = r3.getRemoteVideoState()
            if (r6 != r5) goto L_0x002b
            r6 = 1
            goto L_0x002c
        L_0x002b:
            r6 = 0
        L_0x002c:
            r0.callingUserIsVideo = r6
            int r6 = r3.getVideoState(r1)
            if (r6 == r5) goto L_0x003d
            int r6 = r3.getVideoState(r1)
            if (r6 != r4) goto L_0x003b
            goto L_0x003d
        L_0x003b:
            r6 = 0
            goto L_0x003e
        L_0x003d:
            r6 = 1
        L_0x003e:
            r0.currentUserIsVideo = r6
            if (r6 == 0) goto L_0x0048
            boolean r6 = r0.isVideoCall
            if (r6 != 0) goto L_0x0048
            r0.isVideoCall = r4
        L_0x0048:
            if (r2 == 0) goto L_0x0054
            org.telegram.ui.Components.voip.VoIPFloatingLayout r6 = r0.currentUserCameraFloatingLayout
            r6.saveRelatedPosition()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r6 = r0.callingUserMiniFloatingLayout
            r6.saveRelatedPosition()
        L_0x0054:
            int r6 = r0.currentState
            r7 = 5
            r8 = 3
            r9 = 0
            if (r6 == r4) goto L_0x0279
            if (r6 == r5) goto L_0x0279
            if (r6 == r8) goto L_0x0267
            r10 = 4
            if (r6 == r10) goto L_0x0128
            if (r6 == r7) goto L_0x0267
            switch(r6) {
                case 11: goto L_0x0117;
                case 12: goto L_0x0107;
                case 13: goto L_0x00f7;
                case 14: goto L_0x00e7;
                case 15: goto L_0x0095;
                case 16: goto L_0x0085;
                case 17: goto L_0x0069;
                default: goto L_0x0067;
            }
        L_0x0067:
            goto L_0x0287
        L_0x0069:
            org.telegram.ui.Components.voip.VoIPStatusTextView r6 = r0.statusTextView
            r10 = 2131628104(0x7f0e1048, float:1.8883491E38)
            java.lang.String r11 = "VoipBusy"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.setText(r10, r1, r2)
            org.telegram.ui.Components.voip.AcceptDeclineView r6 = r0.acceptDeclineView
            r6.setRetryMod(r4)
            r0.currentUserIsVideo = r1
            r0.callingUserIsVideo = r1
            r6 = 0
        L_0x0081:
            r10 = 0
        L_0x0082:
            r11 = 1
            goto L_0x028a
        L_0x0085:
            org.telegram.ui.Components.voip.VoIPStatusTextView r6 = r0.statusTextView
            r10 = 2131628279(0x7f0e10f7, float:1.8883846E38)
            java.lang.String r11 = "VoipRinging"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.setText(r10, r4, r2)
            goto L_0x0287
        L_0x0095:
            r0.lockOnScreen = r4
            r6 = 1103101952(0x41CLASSNAME, float:24.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Components.voip.AcceptDeclineView r10 = r0.acceptDeclineView
            r10.setRetryMod(r1)
            if (r3 == 0) goto L_0x00d3
            org.telegram.tgnet.TLRPC$PhoneCall r10 = r3.privateCall
            boolean r10 = r10.video
            if (r10 == 0) goto L_0x00d3
            boolean r10 = r0.currentUserIsVideo
            if (r10 == 0) goto L_0x00b6
            org.telegram.tgnet.TLRPC$User r10 = r0.callingUser
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r10.photo
            if (r10 == 0) goto L_0x00b6
            r10 = 1
            goto L_0x00b7
        L_0x00b6:
            r10 = 0
        L_0x00b7:
            org.telegram.ui.Components.voip.VoIPStatusTextView r11 = r0.statusTextView
            r12 = 2131628239(0x7f0e10cf, float:1.8883765E38)
            java.lang.String r13 = "VoipInVideoCallBranding"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r11.setText(r12, r4, r2)
            org.telegram.ui.Components.voip.AcceptDeclineView r11 = r0.acceptDeclineView
            r12 = 1114636288(0x42700000, float:60.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = -r12
            float r12 = (float) r12
            r11.setTranslationY(r12)
            goto L_0x0082
        L_0x00d3:
            org.telegram.ui.Components.voip.VoIPStatusTextView r10 = r0.statusTextView
            r11 = 2131628237(0x7f0e10cd, float:1.888376E38)
            java.lang.String r12 = "VoipInCallBranding"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setText(r11, r4, r2)
            org.telegram.ui.Components.voip.AcceptDeclineView r10 = r0.acceptDeclineView
            r10.setTranslationY(r9)
            goto L_0x0081
        L_0x00e7:
            org.telegram.ui.Components.voip.VoIPStatusTextView r6 = r0.statusTextView
            r10 = 2131628278(0x7f0e10f6, float:1.8883844E38)
            java.lang.String r11 = "VoipRequesting"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.setText(r10, r4, r2)
            goto L_0x0287
        L_0x00f7:
            org.telegram.ui.Components.voip.VoIPStatusTextView r6 = r0.statusTextView
            r10 = 2131628305(0x7f0e1111, float:1.8883899E38)
            java.lang.String r11 = "VoipWaiting"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.setText(r10, r4, r2)
            goto L_0x0287
        L_0x0107:
            org.telegram.ui.Components.voip.VoIPStatusTextView r6 = r0.statusTextView
            r10 = 2131628130(0x7f0e1062, float:1.8883544E38)
            java.lang.String r11 = "VoipExchangingKeys"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.setText(r10, r4, r2)
            goto L_0x0287
        L_0x0117:
            org.telegram.ui.Components.voip.VoIPTextureView r6 = r0.currentUserTextureView
            r6.saveCameraLastBitmap()
            org.telegram.ui.-$$Lambda$VoIPFragment$wlGE1E0MEZMutJu6GTKRaLq1FDs r6 = new org.telegram.ui.-$$Lambda$VoIPFragment$wlGE1E0MEZMutJu6GTKRaLq1FDs
            r6.<init>()
            r10 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r10)
            goto L_0x0287
        L_0x0128:
            org.telegram.ui.Components.voip.VoIPStatusTextView r6 = r0.statusTextView
            java.lang.String r10 = "VoipFailed"
            r11 = 2131628131(0x7f0e1063, float:1.8883546E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r10, r11)
            r6.setText(r12, r1, r2)
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            java.lang.String r12 = "ERROR_UNKNOWN"
            if (r6 == 0) goto L_0x0143
            java.lang.String r6 = r6.getLastError()
            goto L_0x0144
        L_0x0143:
            r6 = r12
        L_0x0144:
            boolean r12 = android.text.TextUtils.equals(r6, r12)
            r13 = 1000(0x3e8, double:4.94E-321)
            if (r12 != 0) goto L_0x025e
            java.lang.String r12 = "ERROR_INCOMPATIBLE"
            boolean r12 = android.text.TextUtils.equals(r6, r12)
            if (r12 == 0) goto L_0x0174
            org.telegram.tgnet.TLRPC$User r6 = r0.callingUser
            java.lang.String r10 = r6.first_name
            java.lang.String r6 = r6.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r10, r6)
            r10 = 2131628269(0x7f0e10ed, float:1.8883826E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r11[r1] = r6
            java.lang.String r6 = "VoipPeerIncompatible"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r10, r11)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            r0.showErrorDialog(r6)
            goto L_0x0287
        L_0x0174:
            java.lang.String r12 = "ERROR_PEER_OUTDATED"
            boolean r12 = android.text.TextUtils.equals(r6, r12)
            if (r12 == 0) goto L_0x01fd
            boolean r6 = r0.isVideoCall
            if (r6 == 0) goto L_0x01e1
            org.telegram.tgnet.TLRPC$User r6 = r0.callingUser
            java.lang.String r6 = org.telegram.messenger.UserObject.getFirstName(r6)
            r12 = 2131628271(0x7f0e10ef, float:1.888383E38)
            java.lang.Object[] r13 = new java.lang.Object[r4]
            r13[r1] = r6
            java.lang.String r6 = "VoipPeerVideoOutdated"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r12, r13)
            boolean[] r12 = new boolean[r4]
            org.telegram.ui.ActionBar.DarkAlertDialog$Builder r13 = new org.telegram.ui.ActionBar.DarkAlertDialog$Builder
            android.app.Activity r14 = r0.activity
            r13.<init>(r14)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)
            org.telegram.ui.ActionBar.AlertDialog$Builder r10 = r13.setTitle(r10)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = r10.setMessage(r6)
            r10 = 2131624658(0x7f0e02d2, float:1.8876502E38)
            java.lang.String r11 = "Cancel"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            org.telegram.ui.-$$Lambda$VoIPFragment$ojGeWuJU6g5BPAwVIIO5-YSMeUk r11 = new org.telegram.ui.-$$Lambda$VoIPFragment$ojGeWuJU6g5BPAwVIIO5-YSMeUk
            r11.<init>()
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = r6.setNegativeButton(r10, r11)
            r10 = 2131628272(0x7f0e10f0, float:1.8883832E38)
            java.lang.String r11 = "VoipPeerVideoOutdatedMakeVoice"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            org.telegram.ui.-$$Lambda$VoIPFragment$Y8iJLGoAb_XpjQLKsNKCAckpw-I r11 = new org.telegram.ui.-$$Lambda$VoIPFragment$Y8iJLGoAb_XpjQLKsNKCAckpw-I
            r11.<init>(r12)
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = r6.setPositiveButton(r10, r11)
            org.telegram.ui.ActionBar.AlertDialog r6 = r6.show()
            r6.setCanceledOnTouchOutside(r4)
            org.telegram.ui.-$$Lambda$VoIPFragment$nqzifKWhd4yBRmKR5HwfGZ4-CY8 r10 = new org.telegram.ui.-$$Lambda$VoIPFragment$nqzifKWhd4yBRmKR5HwfGZ4-CY8
            r10.<init>(r12)
            r6.setOnDismissListener(r10)
            goto L_0x0287
        L_0x01e1:
            org.telegram.tgnet.TLRPC$User r6 = r0.callingUser
            java.lang.String r6 = org.telegram.messenger.UserObject.getFirstName(r6)
            r10 = 2131628270(0x7f0e10ee, float:1.8883828E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r11[r1] = r6
            java.lang.String r6 = "VoipPeerOutdated"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r10, r11)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            r0.showErrorDialog(r6)
            goto L_0x0287
        L_0x01fd:
            java.lang.String r10 = "ERROR_PRIVACY"
            boolean r10 = android.text.TextUtils.equals(r6, r10)
            if (r10 == 0) goto L_0x0224
            org.telegram.tgnet.TLRPC$User r6 = r0.callingUser
            java.lang.String r10 = r6.first_name
            java.lang.String r6 = r6.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r10, r6)
            r10 = 2131624637(0x7f0e02bd, float:1.887646E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r11[r1] = r6
            java.lang.String r6 = "CallNotAvailable"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r10, r11)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            r0.showErrorDialog(r6)
            goto L_0x0287
        L_0x0224:
            java.lang.String r10 = "ERROR_AUDIO_IO"
            boolean r10 = android.text.TextUtils.equals(r6, r10)
            if (r10 == 0) goto L_0x0232
            java.lang.String r6 = "Error initializing audio hardware"
            r0.showErrorDialog(r6)
            goto L_0x0287
        L_0x0232:
            java.lang.String r10 = "ERROR_LOCALIZED"
            boolean r10 = android.text.TextUtils.equals(r6, r10)
            if (r10 == 0) goto L_0x0240
            org.telegram.ui.Components.voip.VoIPWindowView r6 = r0.windowView
            r6.finish()
            goto L_0x0287
        L_0x0240:
            java.lang.String r10 = "ERROR_CONNECTION_SERVICE"
            boolean r6 = android.text.TextUtils.equals(r6, r10)
            if (r6 == 0) goto L_0x0255
            r6 = 2131628129(0x7f0e1061, float:1.8883542E38)
            java.lang.String r10 = "VoipErrorUnknown"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            r0.showErrorDialog(r6)
            goto L_0x0287
        L_0x0255:
            org.telegram.ui.-$$Lambda$VoIPFragment$UueVJKqKgBWmV7xZu74My-hTS4k r6 = new org.telegram.ui.-$$Lambda$VoIPFragment$UueVJKqKgBWmV7xZu74My-hTS4k
            r6.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r13)
            goto L_0x0287
        L_0x025e:
            org.telegram.ui.-$$Lambda$VoIPFragment$51K6w2K4s88DQNBm4RmaJQqW0XU r6 = new org.telegram.ui.-$$Lambda$VoIPFragment$51K6w2K4s88DQNBm4RmaJQqW0XU
            r6.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r13)
            goto L_0x0287
        L_0x0267:
            r0.updateKeyView(r2)
            int r6 = r0.currentState
            if (r6 != r7) goto L_0x0274
            r6 = 0
            r10 = 0
            r11 = 0
            r12 = 1
            r13 = 1
            goto L_0x028c
        L_0x0274:
            r6 = 0
            r10 = 0
            r11 = 0
            r12 = 1
            goto L_0x028b
        L_0x0279:
            org.telegram.ui.Components.voip.VoIPStatusTextView r6 = r0.statusTextView
            r10 = 2131628121(0x7f0e1059, float:1.8883526E38)
            java.lang.String r11 = "VoipConnecting"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r6.setText(r10, r4, r2)
        L_0x0287:
            r6 = 0
            r10 = 0
            r11 = 0
        L_0x028a:
            r12 = 0
        L_0x028b:
            r13 = 0
        L_0x028c:
            boolean r14 = r0.callingUserIsVideo
            r7 = 250(0xfa, double:1.235E-321)
            r15 = 1065353216(0x3var_, float:1.0)
            if (r14 == 0) goto L_0x02cf
            boolean r14 = r0.switchingToPip
            if (r14 != 0) goto L_0x029d
            org.telegram.ui.Components.BackupImageView r14 = r0.callingUserPhotoView
            r14.setAlpha(r15)
        L_0x029d:
            if (r2 == 0) goto L_0x02b1
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r14 = r14.animate()
            android.view.ViewPropertyAnimator r14 = r14.alpha(r15)
            android.view.ViewPropertyAnimator r14 = r14.setDuration(r7)
            r14.start()
            goto L_0x02bf
        L_0x02b1:
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r14 = r14.animate()
            r14.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            r14.setAlpha(r15)
        L_0x02bf:
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            org.webrtc.TextureViewRenderer r14 = r14.renderer
            boolean r14 = r14.isFirstFrameRendered()
            if (r14 != 0) goto L_0x02cf
            boolean r14 = r0.enterFromPiP
            if (r14 != 0) goto L_0x02cf
            r0.callingUserIsVideo = r1
        L_0x02cf:
            boolean r14 = r0.currentUserIsVideo
            if (r14 != 0) goto L_0x0303
            boolean r14 = r0.callingUserIsVideo
            if (r14 == 0) goto L_0x02d8
            goto L_0x0303
        L_0x02d8:
            r0.fillNavigationBar(r1, r2)
            org.telegram.ui.Components.BackupImageView r14 = r0.callingUserPhotoView
            r14.setVisibility(r1)
            if (r2 == 0) goto L_0x02f4
            org.telegram.ui.Components.voip.VoIPTextureView r14 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r14 = r14.animate()
            android.view.ViewPropertyAnimator r14 = r14.alpha(r9)
            android.view.ViewPropertyAnimator r7 = r14.setDuration(r7)
            r7.start()
            goto L_0x0306
        L_0x02f4:
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r7 = r7.animate()
            r7.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r0.callingUserTextureView
            r7.setAlpha(r9)
            goto L_0x0306
        L_0x0303:
            r0.fillNavigationBar(r4, r2)
        L_0x0306:
            boolean r7 = r0.currentUserIsVideo
            if (r7 == 0) goto L_0x030e
            boolean r8 = r0.callingUserIsVideo
            if (r8 != 0) goto L_0x0310
        L_0x030e:
            r0.cameraForceExpanded = r1
        L_0x0310:
            if (r7 == 0) goto L_0x0318
            boolean r7 = r0.cameraForceExpanded
            if (r7 == 0) goto L_0x0318
            r7 = 1
            goto L_0x0319
        L_0x0318:
            r7 = 0
        L_0x0319:
            r0.showCallingUserAvatarMini(r10, r2)
            org.telegram.ui.Components.BackupImageView r8 = r0.callingUserPhotoViewMini
            java.lang.Object r8 = r8.getTag()
            if (r8 != 0) goto L_0x0326
            r8 = 0
            goto L_0x0333
        L_0x0326:
            r8 = 1124532224(0x43070000, float:135.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r10 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r8 + r10
        L_0x0333:
            int r6 = r6 + r8
            r0.showAcceptDeclineView(r11, r2)
            org.telegram.ui.Components.voip.VoIPWindowView r8 = r0.windowView
            boolean r10 = r0.lockOnScreen
            if (r10 != 0) goto L_0x0344
            boolean r10 = r0.deviceIsLocked
            if (r10 == 0) goto L_0x0342
            goto L_0x0344
        L_0x0342:
            r10 = 0
            goto L_0x0345
        L_0x0344:
            r10 = 1
        L_0x0345:
            r8.setLockOnScreen(r10)
            int r8 = r0.currentState
            r10 = 3
            if (r8 != r10) goto L_0x0357
            boolean r8 = r0.currentUserIsVideo
            if (r8 != 0) goto L_0x0355
            boolean r8 = r0.callingUserIsVideo
            if (r8 == 0) goto L_0x0357
        L_0x0355:
            r8 = 1
            goto L_0x0358
        L_0x0357:
            r8 = 0
        L_0x0358:
            r0.canHideUI = r8
            if (r8 != 0) goto L_0x0363
            boolean r8 = r0.uiVisible
            if (r8 != 0) goto L_0x0363
            r0.showUi(r4)
        L_0x0363:
            boolean r8 = r0.uiVisible
            if (r8 == 0) goto L_0x0381
            boolean r8 = r0.canHideUI
            if (r8 == 0) goto L_0x0381
            boolean r8 = r0.hideUiRunnableWaiting
            if (r8 != 0) goto L_0x0381
            if (r3 == 0) goto L_0x0381
            boolean r8 = r3.isMicMute()
            if (r8 != 0) goto L_0x0381
            java.lang.Runnable r8 = r0.hideUIRunnable
            r10 = 3000(0xbb8, double:1.482E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r10)
            r0.hideUiRunnableWaiting = r4
            goto L_0x0390
        L_0x0381:
            if (r3 == 0) goto L_0x0390
            boolean r8 = r3.isMicMute()
            if (r8 == 0) goto L_0x0390
            java.lang.Runnable r8 = r0.hideUIRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r8)
            r0.hideUiRunnableWaiting = r1
        L_0x0390:
            boolean r8 = r0.uiVisible
            if (r8 != 0) goto L_0x039b
            r8 = 1112014848(0x42480000, float:50.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 - r8
        L_0x039b:
            r8 = 1117782016(0x42a00000, float:80.0)
            r10 = 1098907648(0x41800000, float:16.0)
            r4 = 150(0x96, double:7.4E-322)
            if (r2 == 0) goto L_0x0402
            boolean r11 = r0.lockOnScreen
            if (r11 != 0) goto L_0x03ba
            boolean r11 = r0.uiVisible
            if (r11 != 0) goto L_0x03ac
            goto L_0x03ba
        L_0x03ac:
            android.widget.ImageView r11 = r0.backIcon
            android.view.ViewPropertyAnimator r11 = r11.animate()
            android.view.ViewPropertyAnimator r11 = r11.alpha(r15)
            r11.start()
            goto L_0x03d9
        L_0x03ba:
            android.widget.ImageView r11 = r0.backIcon
            int r11 = r11.getVisibility()
            if (r11 == 0) goto L_0x03cc
            android.widget.ImageView r11 = r0.backIcon
            r11.setVisibility(r1)
            android.widget.ImageView r11 = r0.backIcon
            r11.setAlpha(r9)
        L_0x03cc:
            android.widget.ImageView r11 = r0.backIcon
            android.view.ViewPropertyAnimator r11 = r11.animate()
            android.view.ViewPropertyAnimator r11 = r11.alpha(r9)
            r11.start()
        L_0x03d9:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r11 = r0.notificationsLayout
            android.view.ViewPropertyAnimator r11 = r11.animate()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            boolean r14 = r0.uiVisible
            if (r14 == 0) goto L_0x03ed
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x03ee
        L_0x03ed:
            r8 = 0
        L_0x03ee:
            int r10 = r10 - r8
            float r8 = (float) r10
            android.view.ViewPropertyAnimator r8 = r11.translationY(r8)
            android.view.ViewPropertyAnimator r8 = r8.setDuration(r4)
            org.telegram.ui.Components.CubicBezierInterpolator r10 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r8 = r8.setInterpolator(r10)
            r8.start()
            goto L_0x042e
        L_0x0402:
            boolean r11 = r0.lockOnScreen
            if (r11 != 0) goto L_0x040b
            android.widget.ImageView r11 = r0.backIcon
            r11.setVisibility(r1)
        L_0x040b:
            android.widget.ImageView r11 = r0.backIcon
            boolean r14 = r0.lockOnScreen
            if (r14 == 0) goto L_0x0413
            r14 = 0
            goto L_0x0415
        L_0x0413:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x0415:
            r11.setAlpha(r14)
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r11 = r0.notificationsLayout
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            boolean r14 = r0.uiVisible
            if (r14 == 0) goto L_0x0428
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x0429
        L_0x0428:
            r8 = 0
        L_0x0429:
            int r10 = r10 - r8
            float r8 = (float) r10
            r11.setTranslationY(r8)
        L_0x042e:
            int r8 = r0.currentState
            r10 = 10
            r11 = 11
            if (r8 == r10) goto L_0x043b
            if (r8 == r11) goto L_0x043b
            r0.updateButtons(r2)
        L_0x043b:
            if (r12 == 0) goto L_0x0442
            org.telegram.ui.Components.voip.VoIPStatusTextView r8 = r0.statusTextView
            r8.showTimer(r2)
        L_0x0442:
            org.telegram.ui.Components.voip.VoIPStatusTextView r8 = r0.statusTextView
            r8.showReconnect(r13, r2)
            if (r2 == 0) goto L_0x0466
            int r8 = r0.statusLayoutAnimateToOffset
            if (r6 == r8) goto L_0x046c
            android.widget.LinearLayout r8 = r0.statusLayout
            android.view.ViewPropertyAnimator r8 = r8.animate()
            float r10 = (float) r6
            android.view.ViewPropertyAnimator r8 = r8.translationY(r10)
            android.view.ViewPropertyAnimator r8 = r8.setDuration(r4)
            org.telegram.ui.Components.CubicBezierInterpolator r10 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r8 = r8.setInterpolator(r10)
            r8.start()
            goto L_0x046c
        L_0x0466:
            android.widget.LinearLayout r8 = r0.statusLayout
            float r10 = (float) r6
            r8.setTranslationY(r10)
        L_0x046c:
            r0.statusLayoutAnimateToOffset = r6
            org.telegram.ui.Components.voip.VoIPOverlayBackground r6 = r0.overlayBackground
            boolean r8 = r0.currentUserIsVideo
            if (r8 != 0) goto L_0x047b
            boolean r8 = r0.callingUserIsVideo
            if (r8 == 0) goto L_0x0479
            goto L_0x047b
        L_0x0479:
            r8 = 0
            goto L_0x047c
        L_0x047b:
            r8 = 1
        L_0x047c:
            r6.setShowBlackout(r8, r2)
            int r6 = r0.currentState
            if (r6 == r11) goto L_0x0491
            r8 = 17
            if (r6 == r8) goto L_0x0491
            boolean r6 = r0.currentUserIsVideo
            if (r6 != 0) goto L_0x048f
            boolean r6 = r0.callingUserIsVideo
            if (r6 == 0) goto L_0x0491
        L_0x048f:
            r11 = 1
            goto L_0x0492
        L_0x0491:
            r11 = 0
        L_0x0492:
            r0.canSwitchToPip = r11
            r6 = 0
            if (r3 == 0) goto L_0x05aa
            boolean r8 = r0.currentUserIsVideo
            if (r8 == 0) goto L_0x04a0
            org.telegram.messenger.voip.VoIPService$SharedUIParams r8 = r3.sharedUIParams
            r10 = 1
            r8.tapToVideoTooltipWasShowed = r10
        L_0x04a0:
            org.telegram.ui.Components.voip.VoIPTextureView r8 = r0.currentUserTextureView
            boolean r10 = r3.isScreencast()
            r8.setIsScreencast(r10)
            org.telegram.ui.Components.voip.VoIPTextureView r8 = r0.currentUserTextureView
            org.webrtc.TextureViewRenderer r8 = r8.renderer
            boolean r10 = r3.isFrontFaceCamera()
            r8.setMirror(r10)
            boolean r8 = r0.currentUserIsVideo
            if (r8 == 0) goto L_0x04c3
            boolean r8 = r3.isScreencast()
            if (r8 != 0) goto L_0x04c3
            org.telegram.ui.Components.voip.VoIPTextureView r8 = r0.currentUserTextureView
            org.webrtc.TextureViewRenderer r8 = r8.renderer
            goto L_0x04c4
        L_0x04c3:
            r8 = r6
        L_0x04c4:
            if (r7 == 0) goto L_0x04c9
            org.webrtc.TextureViewRenderer r10 = r0.callingUserMiniTextureRenderer
            goto L_0x04cd
        L_0x04c9:
            org.telegram.ui.Components.voip.VoIPTextureView r10 = r0.callingUserTextureView
            org.webrtc.TextureViewRenderer r10 = r10.renderer
        L_0x04cd:
            r3.setSinks(r8, r10)
            if (r2 == 0) goto L_0x04d7
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r8 = r0.notificationsLayout
            r8.beforeLayoutChanges()
        L_0x04d7:
            boolean r8 = r0.currentUserIsVideo
            java.lang.String r10 = "VoipUserMicrophoneIsOff"
            r12 = 2131165327(0x7var_f, float:1.7944868E38)
            java.lang.String r13 = "video"
            java.lang.String r14 = "muted"
            if (r8 != 0) goto L_0x04e8
            boolean r8 = r0.callingUserIsVideo
            if (r8 == 0) goto L_0x0546
        L_0x04e8:
            int r8 = r0.currentState
            r11 = 3
            if (r8 == r11) goto L_0x04f0
            r11 = 5
            if (r8 != r11) goto L_0x0546
        L_0x04f0:
            long r16 = r3.getCallDuration()
            r18 = 500(0x1f4, double:2.47E-321)
            int r8 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r8 <= 0) goto L_0x0546
            int r8 = r3.getRemoteAudioState()
            if (r8 != 0) goto L_0x0518
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r8 = r0.notificationsLayout
            r11 = 1
            java.lang.Object[] r4 = new java.lang.Object[r11]
            r5 = 2131628295(0x7f0e1107, float:1.8883879E38)
            org.telegram.tgnet.TLRPC$User r11 = r0.callingUser
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)
            r4[r1] = r11
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r10, r5, r4)
            r8.addNotification(r12, r4, r14, r2)
            goto L_0x051d
        L_0x0518:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r4.removeNotification(r14)
        L_0x051d:
            int r4 = r3.getRemoteVideoState()
            if (r4 != 0) goto L_0x0540
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r5 = 2131165318(0x7var_, float:1.794485E38)
            r8 = 2131628294(0x7f0e1106, float:1.8883877E38)
            r10 = 1
            java.lang.Object[] r12 = new java.lang.Object[r10]
            org.telegram.tgnet.TLRPC$User r10 = r0.callingUser
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r10)
            r12[r1] = r10
            java.lang.String r10 = "VoipUserCameraIsOff"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r10, r8, r12)
            r4.addNotification(r5, r8, r13, r2)
            goto L_0x056f
        L_0x0540:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r4.removeNotification(r13)
            goto L_0x056f
        L_0x0546:
            int r4 = r3.getRemoteAudioState()
            if (r4 != 0) goto L_0x0565
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r5 = 2131628295(0x7f0e1107, float:1.8883879E38)
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]
            r8 = r11
            org.telegram.tgnet.TLRPC$User r11 = r0.callingUser
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)
            r8[r1] = r11
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r10, r5, r8)
            r4.addNotification(r12, r5, r14, r2)
            goto L_0x056a
        L_0x0565:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r4.removeNotification(r14)
        L_0x056a:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            r4.removeNotification(r13)
        L_0x056f:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r4 = r0.notificationsLayout
            int r4 = r4.getChildCount()
            if (r4 != 0) goto L_0x0596
            boolean r4 = r0.callingUserIsVideo
            if (r4 == 0) goto L_0x0596
            org.telegram.tgnet.TLRPC$PhoneCall r4 = r3.privateCall
            if (r4 == 0) goto L_0x0596
            boolean r4 = r4.video
            if (r4 != 0) goto L_0x0596
            org.telegram.messenger.voip.VoIPService$SharedUIParams r3 = r3.sharedUIParams
            boolean r4 = r3.tapToVideoTooltipWasShowed
            if (r4 != 0) goto L_0x0596
            r4 = 1
            r3.tapToVideoTooltipWasShowed = r4
            org.telegram.ui.Components.HintView r3 = r0.tapToVideoTooltip
            org.telegram.ui.Components.voip.VoIPToggleButton[] r5 = r0.bottomButtons
            r5 = r5[r4]
            r3.showForView(r5, r4)
            goto L_0x05a3
        L_0x0596:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r3 = r0.notificationsLayout
            int r3 = r3.getChildCount()
            if (r3 == 0) goto L_0x05a3
            org.telegram.ui.Components.HintView r3 = r0.tapToVideoTooltip
            r3.hide()
        L_0x05a3:
            if (r2 == 0) goto L_0x05aa
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r3 = r0.notificationsLayout
            r3.animateLayoutChanges()
        L_0x05aa:
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
            if (r3 == 0) goto L_0x05e0
            boolean r3 = r0.callingUserIsVideo
            if (r3 == 0) goto L_0x05db
            boolean r3 = r0.cameraForceExpanded
            if (r3 == 0) goto L_0x05d5
            goto L_0x05db
        L_0x05d5:
            r3 = 2
            r0.showFloatingLayout(r3, r2)
            r3 = 1
            goto L_0x05e4
        L_0x05db:
            r3 = 1
            r0.showFloatingLayout(r3, r2)
            goto L_0x05e4
        L_0x05e0:
            r3 = 1
            r0.showFloatingLayout(r1, r2)
        L_0x05e4:
            r2 = 1056964608(0x3var_, float:0.5)
            if (r7 == 0) goto L_0x064e
            org.telegram.ui.Components.voip.VoIPFloatingLayout r4 = r0.callingUserMiniFloatingLayout
            java.lang.Object r4 = r4.getTag()
            if (r4 != 0) goto L_0x064e
            org.telegram.ui.Components.voip.VoIPFloatingLayout r4 = r0.callingUserMiniFloatingLayout
            r4.setIsActive(r3)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            int r3 = r3.getVisibility()
            if (r3 == 0) goto L_0x0611
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            r3.setVisibility(r1)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.setAlpha(r9)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.setScaleX(r2)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.setScaleY(r2)
        L_0x0611:
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r6)
            r1.cancel()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r15)
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r15)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r15)
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
            goto L_0x068c
        L_0x064e:
            if (r7 != 0) goto L_0x068c
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x068c
            org.telegram.ui.Components.voip.VoIPFloatingLayout r3 = r0.callingUserMiniFloatingLayout
            r3.setIsActive(r1)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r9)
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r2)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r2)
            org.telegram.ui.VoIPFragment$15 r2 = new org.telegram.ui.VoIPFragment$15
            r2.<init>()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r2)
            r2 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r1 = r1.setInterpolator(r2)
            r1.start()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.setTag(r6)
        L_0x068c:
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.currentUserCameraFloatingLayout
            r1.restoreRelativePosition()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.restoreRelativePosition()
            r20.updateSpeakerPhoneIcon()
        L_0x0699:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPFragment.updateViewState():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateViewState$15 */
    public /* synthetic */ void lambda$updateViewState$15$VoIPFragment() {
        this.windowView.finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateViewState$16 */
    public /* synthetic */ void lambda$updateViewState$16$VoIPFragment(DialogInterface dialogInterface, int i) {
        this.windowView.finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateViewState$17 */
    public /* synthetic */ void lambda$updateViewState$17$VoIPFragment(boolean[] zArr, DialogInterface dialogInterface, int i) {
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
    /* renamed from: lambda$updateViewState$18 */
    public /* synthetic */ void lambda$updateViewState$18$VoIPFragment(boolean[] zArr, DialogInterface dialogInterface) {
        if (!zArr[0]) {
            this.windowView.finish();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateViewState$19 */
    public /* synthetic */ void lambda$updateViewState$19$VoIPFragment() {
        this.windowView.finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateViewState$20 */
    public /* synthetic */ void lambda$updateViewState$20$VoIPFragment() {
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
                    this.currentUserCameraFloatingLayout.setVisibility(0);
                    this.currentUserCameraFloatingLayout.setAlpha(0.0f);
                    this.currentUserCameraFloatingLayout.setScaleX(0.7f);
                    this.currentUserCameraFloatingLayout.setScaleY(0.7f);
                }
                Animator animator2 = this.cameraShowingAnimator;
                if (animator2 != null) {
                    animator2.removeAllListeners();
                    this.cameraShowingAnimator.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                VoIPFloatingLayout voIPFloatingLayout = this.currentUserCameraFloatingLayout;
                Property property = View.ALPHA;
                float[] fArr = {voIPFloatingLayout.getAlpha(), 1.0f};
                VoIPFloatingLayout voIPFloatingLayout2 = this.currentUserCameraFloatingLayout;
                Property property2 = View.SCALE_X;
                float[] fArr2 = {voIPFloatingLayout2.getScaleX(), 1.0f};
                VoIPFloatingLayout voIPFloatingLayout3 = this.currentUserCameraFloatingLayout;
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(voIPFloatingLayout, property, fArr), ObjectAnimator.ofFloat(voIPFloatingLayout2, property2, fArr2), ObjectAnimator.ofFloat(voIPFloatingLayout3, View.SCALE_Y, new float[]{voIPFloatingLayout3.getScaleY(), 1.0f})});
                this.cameraShowingAnimator = animatorSet;
                animatorSet.setDuration(150).start();
            }
            if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) {
                VoIPFloatingLayout voIPFloatingLayout4 = this.currentUserCameraFloatingLayout;
                if (voIPFloatingLayout4.relativePositionToSetX < 0.0f) {
                    voIPFloatingLayout4.setRelativePosition(1.0f, 1.0f);
                }
            }
            VoIPFloatingLayout voIPFloatingLayout5 = this.currentUserCameraFloatingLayout;
            if (i != 2) {
                z2 = false;
            }
            voIPFloatingLayout5.setFloatingMode(z2, z3);
        } else if (!z) {
            this.currentUserCameraFloatingLayout.setVisibility(8);
        } else if (!(this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0)) {
            Animator animator3 = this.cameraShowingAnimator;
            if (animator3 != null) {
                animator3.removeAllListeners();
                this.cameraShowingAnimator.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            VoIPFloatingLayout voIPFloatingLayout6 = this.currentUserCameraFloatingLayout;
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(voIPFloatingLayout6, View.ALPHA, new float[]{voIPFloatingLayout6.getAlpha(), 0.0f})});
            if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 2) {
                VoIPFloatingLayout voIPFloatingLayout7 = this.currentUserCameraFloatingLayout;
                Property property3 = View.SCALE_X;
                float[] fArr3 = {voIPFloatingLayout7.getScaleX(), 0.7f};
                VoIPFloatingLayout voIPFloatingLayout8 = this.currentUserCameraFloatingLayout;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(voIPFloatingLayout7, property3, fArr3), ObjectAnimator.ofFloat(voIPFloatingLayout8, View.SCALE_Y, new float[]{voIPFloatingLayout8.getScaleX(), 0.7f})});
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
                FileLog.e((Throwable) e);
            }
            if (bArr != null) {
                String[] emojifyForCall = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(bArr, 0, bArr.length));
                for (int i = 0; i < 4; i++) {
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
                Transition duration = new Visibility() {
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
                this.bottomButtons[3].setOnClickListener($$Lambda$VoIPFragment$Y1ZYm8k9QQOFw1jxWKeCvriAFKg.INSTANCE);
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

    static /* synthetic */ void lambda$updateButtons$21(View view) {
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
        voIPToggleButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$setMicrohoneAction$22$VoIPFragment(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setMicrohoneAction$22 */
    public /* synthetic */ void lambda$setMicrohoneAction$22$VoIPFragment(View view) {
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
            voIPToggleButton.setOnClickListener(new View.OnClickListener(voIPService) {
                public final /* synthetic */ VoIPService f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    VoIPFragment.this.lambda$setVideoAction$24$VoIPFragment(this.f$1, view);
                }
            });
            voIPToggleButton.setEnabled(true);
            return;
        }
        voIPToggleButton.setData(NUM, ColorUtils.setAlphaComponent(-1, 127), ColorUtils.setAlphaComponent(-1, 30), "Video", false, z);
        voIPToggleButton.setOnClickListener((View.OnClickListener) null);
        voIPToggleButton.setEnabled(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setVideoAction$24 */
    public /* synthetic */ void lambda$setVideoAction$24$VoIPFragment(VoIPService voIPService, View view) {
        TLRPC$PhoneCall tLRPC$PhoneCall;
        int i = Build.VERSION.SDK_INT;
        if (i >= 23 && this.activity.checkSelfPermission("android.permission.CAMERA") != 0) {
            this.activity.requestPermissions(new String[]{"android.permission.CAMERA"}, 102);
        } else if (i >= 21 || (tLRPC$PhoneCall = voIPService.privateCall) == null || tLRPC$PhoneCall.video || this.callingUserIsVideo || voIPService.sharedUIParams.cameraAlertWasShowed) {
            toggleCameraInput();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.activity);
            builder.setMessage(LocaleController.getString("VoipSwitchToVideoCall", NUM));
            builder.setPositiveButton(LocaleController.getString("VoipSwitch", NUM), new DialogInterface.OnClickListener(voIPService) {
                public final /* synthetic */ VoIPService f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPFragment.this.lambda$setVideoAction$23$VoIPFragment(this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.create().show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setVideoAction$23 */
    public /* synthetic */ void lambda$setVideoAction$23$VoIPFragment(VoIPService voIPService, DialogInterface dialogInterface, int i) {
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
        voIPToggleButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$setSpeakerPhoneAction$25$VoIPFragment(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setSpeakerPhoneAction$25 */
    public /* synthetic */ void lambda$setSpeakerPhoneAction$25$VoIPFragment(View view) {
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
        voIPToggleButton.setOnClickListener(new View.OnClickListener(voIPService) {
            public final /* synthetic */ VoIPService f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                VoIPFragment.this.lambda$setFrontalCameraAction$26$VoIPFragment(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setFrontalCameraAction$26 */
    public /* synthetic */ void lambda$setFrontalCameraAction$26$VoIPFragment(VoIPService voIPService, View view) {
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
                AnonymousClass20 r0 = new PrivateVideoPreviewDialog(this.fragmentView.getContext()) {
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
                VoIPHelper.permissionDenied(this.activity, new Runnable() {
                    public final void run() {
                        VoIPFragment.this.lambda$onRequestPermissionsResultInternal$27$VoIPFragment();
                    }
                }, i);
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
    /* renamed from: lambda$onRequestPermissionsResultInternal$27 */
    public /* synthetic */ void lambda$onRequestPermissionsResultInternal$27$VoIPFragment() {
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
            show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    VoIPFragment.this.lambda$showErrorDialog$28$VoIPFragment(dialogInterface);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showErrorDialog$28 */
    public /* synthetic */ void lambda$showErrorDialog$28$VoIPFragment(DialogInterface dialogInterface) {
        this.windowView.finish();
    }

    @SuppressLint({"InlinedApi"})
    private void requestInlinePermissions() {
        if (Build.VERSION.SDK_INT >= 21) {
            AlertsCreator.createDrawOverlayPermissionDialog(this.activity, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPFragment.this.lambda$requestInlinePermissions$29$VoIPFragment(dialogInterface, i);
                }
            }).show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestInlinePermissions$29 */
    public /* synthetic */ void lambda$requestInlinePermissions$29$VoIPFragment(DialogInterface dialogInterface, int i) {
        VoIPWindowView voIPWindowView = this.windowView;
        if (voIPWindowView != null) {
            voIPWindowView.finish();
        }
    }
}
