package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.transition.ChangeBounds;
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
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.util.Log;
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
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.VideoCameraCapturer;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$PhoneCall;
import org.telegram.tgnet.TLRPC$TL_encryptedChat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.AcceptDeclineView;
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

public class VoIPFragment implements VoIPBaseService.StateListener, NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static VoIPFragment instance;
    /* access modifiers changed from: private */
    public AcceptDeclineView acceptDeclineView;
    Activity activity;
    int animationIndex = -1;
    private ImageView backIcon;
    VoIPToggleButton[] bottomButtons = new VoIPToggleButton[4];
    View bottomShadow;
    private VoIPButtonsLayout buttonsLayout;
    TLRPC$User callingUser;
    boolean callingUserIsVideo = false;
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
    boolean cameraForceExpanded;
    private Animator cameraShowingAnimator;
    /* access modifiers changed from: private */
    public boolean canHideUI;
    private boolean canSwitchToPip;
    /* access modifiers changed from: private */
    public int currentState;
    /* access modifiers changed from: private */
    public VoIPFloatingLayout currentUserCameraFloatingLayout;
    boolean currentUserIsVideo = false;
    /* access modifiers changed from: private */
    public VoIPTextureView currentUserTextureView;
    Emoji.EmojiDrawable[] emojiDrawables = new Emoji.EmojiDrawable[4];
    /* access modifiers changed from: private */
    public boolean emojiExpanded;
    LinearLayout emojiLayout;
    private boolean emojiLoaded;
    TextView emojiRationalTextView;
    ImageView[] emojiViews = new ImageView[4];
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
    long lastContentTapTime;
    /* access modifiers changed from: private */
    public WindowInsets lastInsets;
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
    public int previousState;
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

    public /* synthetic */ void lambda$new$0$VoIPFragment(ValueAnimator valueAnimator) {
        this.uiVisibilityAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    public /* synthetic */ void lambda$new$1$VoIPFragment(ValueAnimator valueAnimator) {
        this.fillNaviagtionBarValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    public /* synthetic */ void lambda$new$2$VoIPFragment() {
        this.hideUiRunnableWaiting = false;
        if (this.canHideUI && this.uiVisible) {
            this.lastContentTapTime = System.currentTimeMillis();
            showUi(false);
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public static void show(Activity activity2) {
        show(activity2, false);
    }

    public static void show(Activity activity2, boolean z) {
        if (instance == null) {
            boolean z2 = VoIPPiPView.getInstance() != null;
            if (VoIPService.getSharedInstance() != null) {
                final VoIPFragment voIPFragment = new VoIPFragment();
                voIPFragment.activity = activity2;
                instance = voIPFragment;
                AnonymousClass1 r2 = new VoIPWindowView(activity2, !z2) {
                    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                        if (voIPFragment.isFinished || voIPFragment.switchingToPip) {
                            return false;
                        }
                        if (keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1 || this.lockOnScreen) {
                            return super.dispatchKeyEvent(keyEvent);
                        }
                        voIPFragment.onBackPressed();
                        return true;
                    }
                };
                voIPFragment.windowView = r2;
                if (Build.VERSION.SDK_INT >= 20) {
                    r2.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                        public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                            return VoIPFragment.lambda$show$3(VoIPFragment.this, view, windowInsets);
                        }
                    });
                }
                WindowManager windowManager = (WindowManager) activity2.getSystemService("window");
                WindowManager.LayoutParams createWindowLayoutParams = r2.createWindowLayoutParams();
                if (z) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        createWindowLayoutParams.type = 2038;
                    } else {
                        createWindowLayoutParams.type = 2003;
                    }
                }
                windowManager.addView(r2, createWindowLayoutParams);
                r2.addView(voIPFragment.createView(activity2));
                if (z2) {
                    voIPFragment.startTransitionFromPiP();
                    return;
                }
                voIPFragment.enterTransitionProgress = 1.0f;
                voIPFragment.updateSystemBarColors();
            }
        }
    }

    static /* synthetic */ WindowInsets lambda$show$3(VoIPFragment voIPFragment, View view, WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= 21) {
            voIPFragment.setInsets(windowInsets);
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    /* access modifiers changed from: private */
    public void onBackPressed() {
        if (!this.isFinished && !this.switchingToPip) {
            if (this.callingUserIsVideo && this.currentUserIsVideo && this.cameraForceExpanded) {
                this.cameraForceExpanded = false;
                this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
                this.previousState = this.currentState;
                updateViewState();
            } else if (this.emojiExpanded) {
                expandEmoji(false);
            } else if (this.emojiRationalTextView.getVisibility() == 8) {
                if (!this.canSwitchToPip) {
                    this.windowView.finish();
                } else if (checkInlinePermissions()) {
                    switchToPip();
                } else {
                    requestInlinePermissions();
                }
            }
        }
    }

    public static void clearInstance() {
        WindowInsets windowInsets;
        if (VoIPService.getSharedInstance() != null) {
            int measuredHeight = instance.windowView.getMeasuredHeight();
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                measuredHeight -= windowInsets.getSystemWindowInsetBottom();
            }
            VoIPFragment voIPFragment = instance;
            if (voIPFragment.canSwitchToPip && voIPFragment.checkInlinePermissions()) {
                VoIPFragment voIPFragment2 = instance;
                VoIPPiPView.show(voIPFragment2.activity, voIPFragment2.windowView.getMeasuredWidth(), measuredHeight, 0);
                if (Build.VERSION.SDK_INT >= 20 && instance.lastInsets != null) {
                    VoIPPiPView.getInstance().topInset = instance.lastInsets.getSystemWindowInsetTop();
                    VoIPPiPView.getInstance().bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
                }
            }
        }
        VoIPFragment voIPFragment3 = instance;
        if (voIPFragment3 != null) {
            voIPFragment3.callingUserTextureView.renderer.release();
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
    }

    public VoIPFragment() {
        MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()));
        this.callingUser = VoIPService.getSharedInstance().getUser();
        VoIPService.getSharedInstance().registerStateListener(this);
        VoIPService.getSharedInstance().isOutgoing();
        this.previousState = -1;
        this.currentState = VoIPService.getSharedInstance().getCallState();
        NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
    }

    /* access modifiers changed from: private */
    public void destroy() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.unregisterStateListener(this);
        }
        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
    }

    public void onStateChanged(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPFragment.this.lambda$onStateChanged$4$VoIPFragment(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onStateChanged$4$VoIPFragment(int i) {
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
        if (i == NotificationCenter.voipServiceCreated && this.currentState == 17 && VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().registerStateListener(this);
        }
        if (i == NotificationCenter.emojiDidLoad) {
            checkEmojiLoaded(true);
        }
        if (i == NotificationCenter.closeInCallActivity) {
            this.windowView.finish();
        }
    }

    public /* synthetic */ void lambda$onSignalBarsCountChanged$5$VoIPFragment(int i) {
        this.statusTextView.setSignalBarCount(i);
    }

    public void onSignalBarsCountChanged(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPFragment.this.lambda$onSignalBarsCountChanged$5$VoIPFragment(this.f$1);
            }
        });
    }

    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    public void onMediaStateUpdated(int i, int i2) {
        this.previousState = this.currentState;
        updateViewState();
    }

    public void onCameraSwitch(boolean z) {
        this.previousState = this.currentState;
        updateViewState();
    }

    public void onVideoAvailableChange(boolean z) {
        this.previousState = this.currentState;
        updateViewState();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        AnonymousClass2 r2 = new FrameLayout(context2) {
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
                    Log.d("kek", "nav bar " + VoIPFragment.this.fillNaviagtionBarValue);
                    canvas.drawRect(0.0f, (float) (getMeasuredHeight() - VoIPFragment.this.lastInsets.getSystemWindowInsetBottom()), (float) getMeasuredWidth(), (float) getMeasuredHeight(), VoIPFragment.this.overlayBottomPaint);
                }
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!isEnabled()) {
                    return false;
                }
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
        updateSystemBarColors();
        this.fragmentView = r2;
        r2.setFitsSystemWindows(true);
        this.callingUserPhotoView = new BackupImageView(this, context2) {
            int blackoutColor = ColorUtils.setAlphaComponent(-16777216, 76);

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawColor(this.blackoutColor);
            }
        };
        VoIPTextureView voIPTextureView = new VoIPTextureView(context2, false);
        this.callingUserTextureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.callingUserTextureView.renderer.setEnableHardwareScaler(true);
        r2.addView(this.callingUserPhotoView);
        r2.addView(this.callingUserTextureView);
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
                VoIPFragment.this.lambda$createView$6$VoIPFragment(imageReceiver, z, z2, z3);
            }

            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        });
        this.callingUserPhotoView.setImage(ImageLocation.getForUser(this.callingUser, true), (String) null, (Drawable) backgroundGradientDrawable, (Object) this.callingUser);
        VoIPFloatingLayout voIPFloatingLayout = new VoIPFloatingLayout(context2);
        this.currentUserCameraFloatingLayout = voIPFloatingLayout;
        voIPFloatingLayout.setRelativePosition(1.0f, 1.0f);
        this.currentUserTextureView = new VoIPTextureView(context2, true);
        this.currentUserCameraFloatingLayout.setOnTapListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$7$VoIPFragment(view);
            }
        });
        this.currentUserTextureView.renderer.setMirror(true);
        this.currentUserCameraFloatingLayout.addView(this.currentUserTextureView);
        VoIPFloatingLayout voIPFloatingLayout2 = new VoIPFloatingLayout(context2);
        this.callingUserMiniFloatingLayout = voIPFloatingLayout2;
        voIPFloatingLayout2.alwaysFloating = true;
        voIPFloatingLayout2.setFloatingMode(true, false);
        TextureViewRenderer textureViewRenderer = new TextureViewRenderer(context2);
        this.callingUserMiniTextureRenderer = textureViewRenderer;
        textureViewRenderer.setEnableHardwareScaler(true);
        this.callingUserMiniFloatingLayout.addView(this.callingUserMiniTextureRenderer);
        this.callingUserMiniFloatingLayout.setOnTapListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$8$VoIPFragment(view);
            }
        });
        this.callingUserMiniFloatingLayout.setVisibility(8);
        r2.addView(this.currentUserCameraFloatingLayout, LayoutHelper.createFrame(-2, -2.0f));
        r2.addView(this.callingUserMiniFloatingLayout);
        r2.addView(this.overlayBackground);
        View view = new View(context2);
        this.bottomShadow = view;
        view.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 127)}));
        r2.addView(this.bottomShadow, LayoutHelper.createFrame(-1, 140, 80));
        View view2 = new View(context2);
        this.topShadow = view2;
        view2.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ColorUtils.setAlphaComponent(-16777216, 102), 0}));
        r2.addView(this.topShadow, LayoutHelper.createFrame(-1, 140, 48));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emojiLayout = linearLayout;
        linearLayout.setOrientation(0);
        this.emojiLayout.setPadding(0, 0, 0, AndroidUtilities.dp(30.0f));
        this.emojiLayout.setClipToPadding(false);
        this.emojiLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$9$VoIPFragment(view);
            }
        });
        TextView textView = new TextView(context2);
        this.emojiRationalTextView = textView;
        textView.setText(LocaleController.formatString("CallEmojiKeyTooltip", NUM, this.callingUser.first_name));
        this.emojiRationalTextView.setTextSize(16.0f);
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
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.statusLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        BackupImageView backupImageView = new BackupImageView(context2);
        this.callingUserPhotoViewMini = backupImageView;
        backupImageView.setImage(ImageLocation.getForUser(this.callingUser, false), (String) null, (Drawable) Theme.createCircleDrawable(AndroidUtilities.dp(135.0f), -16777216), (Object) this.callingUser);
        this.callingUserPhotoViewMini.setRoundRadius(AndroidUtilities.dp(135.0f) / 2);
        this.callingUserPhotoViewMini.setVisibility(8);
        TextView textView2 = new TextView(context2);
        textView2.setTextSize(24.0f);
        TLRPC$User tLRPC$User = this.callingUser;
        textView2.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
        textView2.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView2.setTextColor(-1);
        textView2.setGravity(1);
        this.statusLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 6));
        VoIPStatusTextView voIPStatusTextView = new VoIPStatusTextView(context2);
        this.statusTextView = voIPStatusTextView;
        this.statusLayout.addView(voIPStatusTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 6));
        this.statusLayout.setClipChildren(false);
        this.statusLayout.setClipToPadding(false);
        this.statusLayout.setPadding(0, 0, 0, AndroidUtilities.dp(15.0f));
        r2.addView(this.callingUserPhotoViewMini, LayoutHelper.createFrame(135, 135.0f, 1, 0.0f, 68.0f, 0.0f, 0.0f));
        r2.addView(this.statusLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 68.0f, 0.0f, 0.0f));
        r2.addView(this.emojiLayout, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 17.0f, 0.0f, 0.0f));
        r2.addView(this.emojiRationalTextView, LayoutHelper.createFrame(-1, -2.0f, 17, 24.0f, 32.0f, 24.0f, 0.0f));
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
                    intent.putExtra("account", UserConfig.selectedAccount);
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
                        VoIPService.getSharedInstance().requestVideoCall();
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
        r2.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, 80, 80));
        r2.addView(this.acceptDeclineView, LayoutHelper.createFrame(-1, 186, 80));
        ImageView imageView = new ImageView(context2);
        this.backIcon = imageView;
        imageView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.backIcon.setImageResource(NUM);
        this.backIcon.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        r2.addView(this.backIcon, LayoutHelper.createFrame(56, 56, 51));
        ImageView imageView2 = new ImageView(context2);
        this.speakerPhoneIcon = imageView2;
        imageView2.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.speakerPhoneIcon.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        r2.addView(this.speakerPhoneIcon, LayoutHelper.createFrame(56, 56, 53));
        this.speakerPhoneIcon.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$10$VoIPFragment(view);
            }
        });
        this.backIcon.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$11$VoIPFragment(view);
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
                VoIPFragment.this.lambda$createView$12$VoIPFragment();
            }
        });
        r2.addView(this.notificationsLayout, LayoutHelper.createFrame(-1, 200.0f, 80, 16.0f, 0.0f, 16.0f, 0.0f));
        updateViewState();
        if (VoIPService.getSharedInstance() != null) {
            if (VideoCameraCapturer.eglBase == null) {
                VideoCameraCapturer.eglBase = EglBase.CC.create((EglBase.Context) null, EglBase.CONFIG_PLAIN);
            }
            this.currentUserTextureView.renderer.init(VideoCameraCapturer.eglBase.getEglBaseContext(), new RendererCommon.RendererEvents() {
                public void onFrameResolutionChanged(int i, int i2, int i3) {
                }

                public /* synthetic */ void lambda$onFirstFrameRendered$0$VoIPFragment$6() {
                    VoIPFragment.this.updateViewState();
                }

                public void onFirstFrameRendered() {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            VoIPFragment.AnonymousClass6.this.lambda$onFirstFrameRendered$0$VoIPFragment$6();
                        }
                    });
                }
            });
            this.callingUserTextureView.renderer.init(VideoCameraCapturer.eglBase.getEglBaseContext(), new RendererCommon.RendererEvents() {
                public void onFrameResolutionChanged(int i, int i2, int i3) {
                }

                public /* synthetic */ void lambda$onFirstFrameRendered$0$VoIPFragment$7() {
                    VoIPFragment.this.updateViewState();
                }

                public void onFirstFrameRendered() {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            VoIPFragment.AnonymousClass7.this.lambda$onFirstFrameRendered$0$VoIPFragment$7();
                        }
                    });
                }
            }, EglBase.CONFIG_PLAIN, new GlRectDrawer());
            this.callingUserMiniTextureRenderer.init(VideoCameraCapturer.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
        }
        HintView hintView = new HintView(context2, 4);
        this.tapToVideoTooltip = hintView;
        hintView.setText(LocaleController.getString("TapToTurnCamera", NUM));
        r2.addView(this.tapToVideoTooltip, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 8.0f));
        this.tapToVideoTooltip.setBottomOffset(AndroidUtilities.dp(4.0f));
        this.tapToVideoTooltip.setVisibility(8);
        return r2;
    }

    public /* synthetic */ void lambda$createView$6$VoIPFragment(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (bitmapSafe != null) {
            this.overlayBackground.setBackground(bitmapSafe);
        }
    }

    public /* synthetic */ void lambda$createView$7$VoIPFragment(View view) {
        if (this.currentUserIsVideo && this.callingUserIsVideo && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.callingUserMiniFloatingLayout.setRelativePosition(this.currentUserCameraFloatingLayout);
            this.cameraForceExpanded = true;
            this.currentState = this.previousState;
            updateViewState();
        }
    }

    public /* synthetic */ void lambda$createView$8$VoIPFragment(View view) {
        if (this.cameraForceExpanded && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
            this.cameraForceExpanded = false;
            this.currentState = this.previousState;
            updateViewState();
        }
    }

    public /* synthetic */ void lambda$createView$9$VoIPFragment(View view) {
        if (System.currentTimeMillis() - this.lastContentTapTime >= 500) {
            this.lastContentTapTime = System.currentTimeMillis();
            if (this.emojiLoaded) {
                expandEmoji(!this.emojiExpanded);
            }
        }
    }

    public /* synthetic */ void lambda$createView$10$VoIPFragment(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity);
        }
    }

    public /* synthetic */ void lambda$createView$11$VoIPFragment(View view) {
        if (!this.windowView.isLockOnScreen()) {
            onBackPressed();
        }
    }

    public /* synthetic */ void lambda$createView$12$VoIPFragment() {
        this.previousState = this.currentState;
        updateViewState();
    }

    public void switchToPip() {
        WindowInsets windowInsets;
        if (!this.isFinished && checkInlinePermissions()) {
            this.isFinished = true;
            if (VoIPService.getSharedInstance() != null) {
                int measuredHeight = instance.windowView.getMeasuredHeight();
                if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                    measuredHeight -= windowInsets.getSystemWindowInsetBottom();
                }
                VoIPFragment voIPFragment = instance;
                VoIPPiPView.show(voIPFragment.activity, voIPFragment.windowView.getMeasuredWidth(), measuredHeight, 1);
                if (Build.VERSION.SDK_INT >= 20 && instance.lastInsets != null) {
                    VoIPPiPView.getInstance().topInset = instance.lastInsets.getSystemWindowInsetTop();
                    VoIPPiPView.getInstance().bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
                }
            }
            this.speakerPhoneIcon.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.backIcon.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.emojiLayout.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.statusLayout.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.buttonsLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.bottomShadow.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.topShadow.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.callingUserMiniFloatingLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.notificationsLayout.animate().alpha(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            VoIPPiPView.getInstance().switchingToPip = true;
            this.switchingToPip = true;
            Animator createPiPTransition = createPiPTransition(false);
            this.animationIndex = NotificationCenter.getInstance(UserConfig.selectedAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
            createPiPTransition.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPPiPView.getInstance().windowView.setAlpha(1.0f);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            VoIPFragment.AnonymousClass8.this.lambda$onAnimationEnd$0$VoIPFragment$8();
                        }
                    }, 200);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$VoIPFragment$8() {
                    NotificationCenter.getInstance(UserConfig.selectedAccount).onAnimationFinish(VoIPFragment.this.animationIndex);
                    VoIPPiPView.getInstance().onTransitionEnd();
                    VoIPFragment.this.currentUserCameraFloatingLayout.setCornerRadius(-1.0f);
                    VoIPFragment.this.callingUserTextureView.renderer.release();
                    VoIPFragment.this.currentUserTextureView.renderer.release();
                    VoIPFragment.this.callingUserMiniTextureRenderer.release();
                    VoIPFragment.this.destroy();
                    VoIPFragment.this.windowView.finishImmediate();
                    VoIPPiPView.getInstance().switchingToPip = false;
                    boolean unused = VoIPFragment.this.switchingToPip = false;
                    VoIPFragment unused2 = VoIPFragment.instance = null;
                }
            });
            createPiPTransition.setDuration(350);
            createPiPTransition.setInterpolator(CubicBezierInterpolator.DEFAULT);
            createPiPTransition.start();
        }
    }

    public void startTransitionFromPiP() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && sharedInstance.getVideoState() == 2) {
            this.callingUserTextureView.renderer.setFirstFrameRendered();
        }
        this.windowView.setAlpha(0.0f);
        updateViewState();
        this.switchingToPip = true;
        VoIPPiPView.getInstance().switchingToPip = true;
        this.animationIndex = NotificationCenter.getInstance(UserConfig.selectedAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VoIPFragment.this.lambda$startTransitionFromPiP$14$VoIPFragment();
            }
        }, 64);
    }

    public /* synthetic */ void lambda$startTransitionFromPiP$14$VoIPFragment() {
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
        AndroidUtilities.runOnUIThread(new Runnable(createPiPTransition) {
            public final /* synthetic */ Animator f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPFragment.this.lambda$null$13$VoIPFragment(this.f$1);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$null$13$VoIPFragment(Animator animator) {
        VoIPPiPView.getInstance().switchingToPip = false;
        VoIPPiPView.getInstance().finish();
        this.speakerPhoneIcon.animate().setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.backIcon.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.emojiLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.statusLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.buttonsLayout.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.bottomShadow.animate().alpha(1.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.topShadow.animate().alpha(1.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.notificationsLayout.animate().alpha(1.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                NotificationCenter.getInstance(UserConfig.selectedAccount).onAnimationFinish(VoIPFragment.this.animationIndex);
                VoIPFragment.this.currentUserCameraFloatingLayout.setCornerRadius(-1.0f);
                boolean unused = VoIPFragment.this.switchingToPip = false;
                VoIPFragment voIPFragment = VoIPFragment.this;
                int unused2 = voIPFragment.previousState = voIPFragment.currentState;
                VoIPFragment.this.updateViewState();
            }
        });
        animator.setDuration(350);
        animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
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
        this.currentUserCameraFloatingLayout.animate().cancel();
        float f7 = (float) (VoIPPiPView.getInstance().windowLayoutParams.x + VoIPPiPView.getInstance().xOffset);
        float f8 = (float) (VoIPPiPView.getInstance().windowLayoutParams.y + VoIPPiPView.getInstance().yOffset);
        float x = this.currentUserCameraFloatingLayout.getX();
        float y = this.currentUserCameraFloatingLayout.getY();
        float scaleX = this.currentUserCameraFloatingLayout.getScaleX();
        float f9 = VoIPPiPView.getInstance().expanded ? 0.4f : 0.25f;
        float measuredWidth = f7 - ((((float) this.callingUserTextureView.getMeasuredWidth()) - (((float) this.callingUserTextureView.getMeasuredWidth()) * f9)) / 2.0f);
        float measuredHeight = f8 - ((((float) this.callingUserTextureView.getMeasuredHeight()) - (((float) this.callingUserTextureView.getMeasuredHeight()) * f9)) / 2.0f);
        float var_ = 0.0f;
        if (this.callingUserIsVideo) {
            int measuredWidth2 = this.currentUserCameraFloatingLayout.getMeasuredWidth();
            if (!this.currentUserIsVideo || measuredWidth2 == 0) {
                f6 = 1.0f;
                f5 = 1.0f;
                z2 = false;
                f4 = 0.0f;
            } else {
                f4 = (((float) this.windowView.getMeasuredWidth()) / ((float) measuredWidth2)) * f9 * 0.4f;
                f6 = (((f7 - ((((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) * f4)) / 2.0f)) + (((float) VoIPPiPView.getInstance().parentWidth) * f9)) - ((((float) VoIPPiPView.getInstance().parentWidth) * f9) * 0.4f)) - ((float) AndroidUtilities.dp(4.0f));
                f5 = (((f8 - ((((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) * f4)) / 2.0f)) + (((float) VoIPPiPView.getInstance().parentHeight) * f9)) - ((((float) VoIPPiPView.getInstance().parentHeight) * f9) * 0.4f)) - ((float) AndroidUtilities.dp(4.0f));
                z2 = true;
            }
            f2 = f6;
            f = f5;
            f3 = f4;
        } else {
            f2 = f7 - ((((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredWidth()) * f9)) / 2.0f);
            f = f8 - ((((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) - (((float) this.currentUserCameraFloatingLayout.getMeasuredHeight()) * f9)) / 2.0f);
            f3 = f9;
            z2 = true;
        }
        float dp = this.callingUserIsVideo ? (float) AndroidUtilities.dp(4.0f) : 0.0f;
        float dp2 = (((float) AndroidUtilities.dp(4.0f)) * 1.0f) / f3;
        if (z) {
            if (z2) {
                this.currentUserCameraFloatingLayout.setScaleX(f3);
                this.currentUserCameraFloatingLayout.setScaleY(f3);
                this.currentUserCameraFloatingLayout.setTranslationX(f2);
                this.currentUserCameraFloatingLayout.setTranslationY(f);
                this.currentUserCameraFloatingLayout.setCornerRadius(dp2);
            }
            this.callingUserTextureView.setScaleX(f9);
            this.callingUserTextureView.setScaleY(f9);
            this.callingUserTextureView.setTranslationX(measuredWidth);
            this.callingUserTextureView.setTranslationY(measuredHeight);
            this.callingUserTextureView.setRoundCorners((((float) AndroidUtilities.dp(6.0f)) * 1.0f) / f9);
            this.callingUserPhotoView.setAlpha(0.0f);
            this.callingUserPhotoView.setScaleX(f9);
            this.callingUserPhotoView.setScaleY(f9);
            this.callingUserPhotoView.setTranslationX(measuredWidth);
            this.callingUserPhotoView.setTranslationY(measuredHeight);
        }
        float[] fArr = new float[2];
        fArr[0] = z ? 1.0f : 0.0f;
        fArr[1] = z ? 0.0f : 1.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        if (!z) {
            var_ = 1.0f;
        }
        this.enterTransitionProgress = var_;
        updateSystemBarColors();
        $$Lambda$VoIPFragment$eiLc8Rrdnt3qjFkrjvfxSBnqk r21 = r0;
        $$Lambda$VoIPFragment$eiLc8Rrdnt3qjFkrjvfxSBnqk r0 = new ValueAnimator.AnimatorUpdateListener(this, z2, scaleX, f3, x, f2, y, f, dp, dp2, 1.0f, f9, 0.0f, measuredWidth, 0.0f, measuredHeight) {
            public final /* synthetic */ VoIPFragment f$0;
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ float f$10;
            public final /* synthetic */ float f$11;
            public final /* synthetic */ float f$12;
            public final /* synthetic */ float f$13;
            public final /* synthetic */ float f$14;
            public final /* synthetic */ float f$15;
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
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ValueAnimator valueAnimator2 = valueAnimator;
                VoIPFragment voIPFragment = this.f$0;
                VoIPFragment voIPFragment2 = voIPFragment;
                voIPFragment2.lambda$createPiPTransition$15$VoIPFragment(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, valueAnimator2);
            }
        };
        ValueAnimator valueAnimator = ofFloat;
        valueAnimator.addUpdateListener(r21);
        return valueAnimator;
    }

    public /* synthetic */ void lambda$createPiPTransition$15$VoIPFragment(boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float var_, float var_, float var_, float var_, float var_, ValueAnimator valueAnimator) {
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
        }
        float var_ = (f9 * var_) + (var_ * floatValue);
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
        if (this.emojiLoaded && this.emojiExpanded != z) {
            this.emojiExpanded = z;
            if (z) {
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
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0228  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0235  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0282  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02aa  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02e1  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02ff  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03e4  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0405  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0463 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01b1  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01c6  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01d6  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01f9  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01fb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateViewState() {
        /*
            r17 = this;
            r0 = r17
            boolean r1 = r0.isFinished
            if (r1 == 0) goto L_0x0007
            return
        L_0x0007:
            int r1 = r0.currentState
            r2 = 4
            if (r1 != r2) goto L_0x0010
            r1 = 17
            r0.currentState = r1
        L_0x0010:
            int r1 = r0.previousState
            r2 = -1
            r3 = 0
            r4 = 1
            if (r1 == r2) goto L_0x0019
            r1 = 1
            goto L_0x001a
        L_0x0019:
            r1 = 0
        L_0x001a:
            r2 = 1098907648(0x41800000, float:16.0)
            org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r6 = 2
            if (r5 == 0) goto L_0x003c
            int r7 = r5.getCurrentVideoState()
            if (r7 != r6) goto L_0x002e
            r7 = 1
            goto L_0x002f
        L_0x002e:
            r7 = 0
        L_0x002f:
            r0.callingUserIsVideo = r7
            int r7 = r5.getVideoState()
            if (r7 != r6) goto L_0x0039
            r7 = 1
            goto L_0x003a
        L_0x0039:
            r7 = 0
        L_0x003a:
            r0.currentUserIsVideo = r7
        L_0x003c:
            if (r1 == 0) goto L_0x0048
            org.telegram.ui.Components.voip.VoIPFloatingLayout r7 = r0.currentUserCameraFloatingLayout
            r7.saveRelatedPosition()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r7 = r0.callingUserMiniFloatingLayout
            r7.saveRelatedPosition()
        L_0x0048:
            int r7 = r0.currentState
            r8 = 5
            r9 = 3
            r10 = 0
            if (r7 == r4) goto L_0x011a
            if (r7 == r6) goto L_0x011a
            if (r7 == r9) goto L_0x010b
            if (r7 == r8) goto L_0x010b
            switch(r7) {
                case 11: goto L_0x00fb;
                case 12: goto L_0x00ec;
                case 13: goto L_0x00dd;
                case 14: goto L_0x00ce;
                case 15: goto L_0x0086;
                case 16: goto L_0x0076;
                case 17: goto L_0x005a;
                default: goto L_0x0058;
            }
        L_0x0058:
            goto L_0x0128
        L_0x005a:
            org.telegram.ui.Components.voip.VoIPStatusTextView r7 = r0.statusTextView
            r11 = 2131627406(0x7f0e0d8e, float:1.8882076E38)
            java.lang.String r12 = "VoipBusy"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r7.setText(r11, r3, r1)
            org.telegram.ui.Components.voip.AcceptDeclineView r7 = r0.acceptDeclineView
            r7.setRetryMod(r4)
            r0.currentUserIsVideo = r3
            r0.callingUserIsVideo = r3
            r7 = 0
            r11 = 0
            r12 = 1
            goto L_0x012b
        L_0x0076:
            org.telegram.ui.Components.voip.VoIPStatusTextView r7 = r0.statusTextView
            r11 = 2131627441(0x7f0e0db1, float:1.8882147E38)
            java.lang.String r12 = "VoipRinging"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r7.setText(r11, r4, r1)
            goto L_0x0128
        L_0x0086:
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.voip.AcceptDeclineView r11 = r0.acceptDeclineView
            r11.setRetryMod(r3)
            if (r5 == 0) goto L_0x00b7
            org.telegram.tgnet.TLRPC$PhoneCall r11 = r5.call
            boolean r11 = r11.video
            if (r11 == 0) goto L_0x00b7
            boolean r11 = r0.currentUserIsVideo
            org.telegram.ui.Components.voip.VoIPStatusTextView r12 = r0.statusTextView
            r13 = 2131627419(0x7f0e0d9b, float:1.8882102E38)
            java.lang.String r14 = "VoipInVideoCallBranding"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r12.setText(r13, r4, r1)
            org.telegram.ui.Components.voip.AcceptDeclineView r12 = r0.acceptDeclineView
            r13 = 1114636288(0x42700000, float:60.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = -r13
            float r13 = (float) r13
            r12.setTranslationY(r13)
            goto L_0x00cb
        L_0x00b7:
            org.telegram.ui.Components.voip.VoIPStatusTextView r11 = r0.statusTextView
            r12 = 2131627417(0x7f0e0d99, float:1.8882098E38)
            java.lang.String r13 = "VoipInCallBranding"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r11.setText(r12, r4, r1)
            org.telegram.ui.Components.voip.AcceptDeclineView r11 = r0.acceptDeclineView
            r11.setTranslationY(r10)
            r11 = 0
        L_0x00cb:
            r12 = 1
            r13 = 1
            goto L_0x012c
        L_0x00ce:
            org.telegram.ui.Components.voip.VoIPStatusTextView r7 = r0.statusTextView
            r11 = 2131627440(0x7f0e0db0, float:1.8882144E38)
            java.lang.String r12 = "VoipRequesting"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r7.setText(r11, r4, r1)
            goto L_0x0128
        L_0x00dd:
            org.telegram.ui.Components.voip.VoIPStatusTextView r7 = r0.statusTextView
            r11 = 2131627452(0x7f0e0dbc, float:1.8882169E38)
            java.lang.String r12 = "VoipWaiting"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r7.setText(r11, r4, r1)
            goto L_0x0128
        L_0x00ec:
            org.telegram.ui.Components.voip.VoIPStatusTextView r7 = r0.statusTextView
            r11 = 2131627412(0x7f0e0d94, float:1.8882088E38)
            java.lang.String r12 = "VoipExchangingKeys"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r7.setText(r11, r4, r1)
            goto L_0x0128
        L_0x00fb:
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r0.currentUserTextureView
            r7.saveCameraLastBitmap()
            org.telegram.ui.-$$Lambda$VoIPFragment$MvW0sQCy-TSLYUodkxbKK4c0B_w r7 = new org.telegram.ui.-$$Lambda$VoIPFragment$MvW0sQCy-TSLYUodkxbKK4c0B_w
            r7.<init>()
            r11 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7, r11)
            goto L_0x0128
        L_0x010b:
            int r7 = r0.previousState
            if (r7 == r9) goto L_0x0114
            if (r7 == r8) goto L_0x0114
            r0.updateKeyView(r1)
        L_0x0114:
            r7 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 1
            goto L_0x012d
        L_0x011a:
            org.telegram.ui.Components.voip.VoIPStatusTextView r7 = r0.statusTextView
            r11 = 2131627408(0x7f0e0d90, float:1.888208E38)
            java.lang.String r12 = "VoipConnecting"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r7.setText(r11, r4, r1)
        L_0x0128:
            r7 = 0
            r11 = 0
            r12 = 0
        L_0x012b:
            r13 = 0
        L_0x012c:
            r14 = 0
        L_0x012d:
            boolean r15 = r0.callingUserIsVideo
            r8 = 250(0xfa, double:1.235E-321)
            r6 = 1065353216(0x3var_, float:1.0)
            if (r15 == 0) goto L_0x017d
            org.telegram.ui.Components.BackupImageView r15 = r0.callingUserPhotoView
            r15.setAlpha(r6)
            if (r1 == 0) goto L_0x014e
            org.telegram.ui.Components.voip.VoIPTextureView r15 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r15 = r15.animate()
            android.view.ViewPropertyAnimator r15 = r15.alpha(r6)
            android.view.ViewPropertyAnimator r15 = r15.setDuration(r8)
            r15.start()
            goto L_0x015c
        L_0x014e:
            org.telegram.ui.Components.voip.VoIPTextureView r15 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r15 = r15.animate()
            r15.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r15 = r0.callingUserTextureView
            r15.setAlpha(r6)
        L_0x015c:
            org.telegram.ui.Components.BackupImageView r15 = r0.callingUserPhotoView
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.callingUserTextureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            boolean r2 = r2.isFirstFrameRendered()
            if (r2 == 0) goto L_0x016d
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            goto L_0x016e
        L_0x016d:
            r2 = 0
        L_0x016e:
            r15.setTag(r2)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.callingUserTextureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            boolean r2 = r2.isFirstFrameRendered()
            if (r2 != 0) goto L_0x017d
            r0.callingUserIsVideo = r3
        L_0x017d:
            boolean r2 = r0.currentUserIsVideo
            if (r2 != 0) goto L_0x01b1
            boolean r2 = r0.callingUserIsVideo
            if (r2 == 0) goto L_0x0186
            goto L_0x01b1
        L_0x0186:
            r0.fillNavigationBar(r3, r1)
            org.telegram.ui.Components.BackupImageView r2 = r0.callingUserPhotoView
            r2.setVisibility(r3)
            if (r1 == 0) goto L_0x01a2
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r2 = r2.animate()
            android.view.ViewPropertyAnimator r2 = r2.alpha(r10)
            android.view.ViewPropertyAnimator r2 = r2.setDuration(r8)
            r2.start()
            goto L_0x01b4
        L_0x01a2:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.callingUserTextureView
            android.view.ViewPropertyAnimator r2 = r2.animate()
            r2.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.callingUserTextureView
            r2.setAlpha(r10)
            goto L_0x01b4
        L_0x01b1:
            r0.fillNavigationBar(r4, r1)
        L_0x01b4:
            boolean r2 = r0.currentUserIsVideo
            if (r2 == 0) goto L_0x01bc
            boolean r2 = r0.callingUserIsVideo
            if (r2 != 0) goto L_0x01be
        L_0x01bc:
            r0.cameraForceExpanded = r3
        L_0x01be:
            boolean r2 = r0.currentUserIsVideo
            if (r2 == 0) goto L_0x01c8
            boolean r2 = r0.cameraForceExpanded
            if (r2 == 0) goto L_0x01c8
            r2 = 1
            goto L_0x01c9
        L_0x01c8:
            r2 = 0
        L_0x01c9:
            r0.showCallingUserAvatarMini(r11, r1)
            org.telegram.ui.Components.BackupImageView r8 = r0.callingUserPhotoViewMini
            java.lang.Object r8 = r8.getTag()
            if (r8 != 0) goto L_0x01d6
            r8 = 0
            goto L_0x01e3
        L_0x01d6:
            r8 = 1124532224(0x43070000, float:135.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 1094713344(0x41400000, float:12.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 + r9
        L_0x01e3:
            int r7 = r7 + r8
            r0.showAcceptDeclineView(r12, r1)
            org.telegram.ui.Components.voip.VoIPWindowView r8 = r0.windowView
            r8.setLockOnScreen(r13)
            int r8 = r0.currentState
            r9 = 3
            if (r8 != r9) goto L_0x01fb
            boolean r8 = r0.currentUserIsVideo
            if (r8 != 0) goto L_0x01f9
            boolean r8 = r0.callingUserIsVideo
            if (r8 == 0) goto L_0x01fb
        L_0x01f9:
            r8 = 1
            goto L_0x01fc
        L_0x01fb:
            r8 = 0
        L_0x01fc:
            r0.canHideUI = r8
            if (r8 != 0) goto L_0x0207
            boolean r8 = r0.uiVisible
            if (r8 != 0) goto L_0x0207
            r0.showUi(r4)
        L_0x0207:
            boolean r8 = r0.uiVisible
            if (r8 == 0) goto L_0x0224
            boolean r8 = r0.canHideUI
            if (r8 == 0) goto L_0x0224
            boolean r8 = r0.hideUiRunnableWaiting
            if (r8 != 0) goto L_0x0224
            if (r5 == 0) goto L_0x0224
            boolean r8 = r5.isMicMute()
            if (r8 != 0) goto L_0x0224
            java.lang.Runnable r8 = r0.hideUIRunnable
            r11 = 3000(0xbb8, double:1.482E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r11)
            r0.hideUiRunnableWaiting = r4
        L_0x0224:
            boolean r8 = r0.uiVisible
            if (r8 != 0) goto L_0x022f
            r8 = 1112014848(0x42480000, float:50.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 - r8
        L_0x022f:
            r8 = 1117782016(0x42a00000, float:80.0)
            r11 = 150(0x96, double:7.4E-322)
            if (r1 == 0) goto L_0x0282
            if (r13 != 0) goto L_0x024a
            boolean r9 = r0.uiVisible
            if (r9 != 0) goto L_0x023c
            goto L_0x024a
        L_0x023c:
            android.widget.ImageView r9 = r0.backIcon
            android.view.ViewPropertyAnimator r9 = r9.animate()
            android.view.ViewPropertyAnimator r9 = r9.alpha(r6)
            r9.start()
            goto L_0x0257
        L_0x024a:
            android.widget.ImageView r9 = r0.backIcon
            android.view.ViewPropertyAnimator r9 = r9.animate()
            android.view.ViewPropertyAnimator r9 = r9.alpha(r10)
            r9.start()
        L_0x0257:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r9 = r0.notificationsLayout
            android.view.ViewPropertyAnimator r9 = r9.animate()
            r13 = 1098907648(0x41800000, float:16.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = -r13
            boolean r15 = r0.uiVisible
            if (r15 == 0) goto L_0x026d
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x026e
        L_0x026d:
            r8 = 0
        L_0x026e:
            int r13 = r13 - r8
            float r8 = (float) r13
            android.view.ViewPropertyAnimator r8 = r9.translationY(r8)
            android.view.ViewPropertyAnimator r8 = r8.setDuration(r11)
            org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r8 = r8.setInterpolator(r9)
            r8.start()
            goto L_0x02a5
        L_0x0282:
            android.widget.ImageView r9 = r0.backIcon
            if (r13 == 0) goto L_0x0288
            r13 = 0
            goto L_0x028a
        L_0x0288:
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x028a:
            r9.setAlpha(r13)
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r9 = r0.notificationsLayout
            r13 = 1098907648(0x41800000, float:16.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = -r13
            boolean r15 = r0.uiVisible
            if (r15 == 0) goto L_0x029f
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x02a0
        L_0x029f:
            r8 = 0
        L_0x02a0:
            int r13 = r13 - r8
            float r8 = (float) r13
            r9.setTranslationY(r8)
        L_0x02a5:
            r0.updateButtons(r1)
            if (r14 == 0) goto L_0x02af
            org.telegram.ui.Components.voip.VoIPStatusTextView r8 = r0.statusTextView
            r8.showTimer(r1)
        L_0x02af:
            if (r1 == 0) goto L_0x02ce
            int r8 = r0.statusLayoutAnimateToOffset
            if (r7 == r8) goto L_0x02d4
            android.widget.LinearLayout r8 = r0.statusLayout
            android.view.ViewPropertyAnimator r8 = r8.animate()
            float r9 = (float) r7
            android.view.ViewPropertyAnimator r8 = r8.translationY(r9)
            android.view.ViewPropertyAnimator r8 = r8.setDuration(r11)
            org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r8 = r8.setInterpolator(r9)
            r8.start()
            goto L_0x02d4
        L_0x02ce:
            android.widget.LinearLayout r8 = r0.statusLayout
            float r9 = (float) r7
            r8.setTranslationY(r9)
        L_0x02d4:
            r0.statusLayoutAnimateToOffset = r7
            org.telegram.ui.Components.voip.VoIPOverlayBackground r7 = r0.overlayBackground
            boolean r8 = r0.currentUserIsVideo
            if (r8 != 0) goto L_0x02e3
            boolean r8 = r0.callingUserIsVideo
            if (r8 == 0) goto L_0x02e1
            goto L_0x02e3
        L_0x02e1:
            r8 = 0
            goto L_0x02e4
        L_0x02e3:
            r8 = 1
        L_0x02e4:
            r7.setShowBlackout(r8, r1)
            int r7 = r0.currentState
            r8 = 3
            if (r7 == r8) goto L_0x02ef
            r8 = 5
            if (r7 != r8) goto L_0x02f8
        L_0x02ef:
            boolean r7 = r0.currentUserIsVideo
            if (r7 != 0) goto L_0x02fa
            boolean r7 = r0.callingUserIsVideo
            if (r7 == 0) goto L_0x02f8
            goto L_0x02fa
        L_0x02f8:
            r7 = 0
            goto L_0x02fb
        L_0x02fa:
            r7 = 1
        L_0x02fb:
            r0.canSwitchToPip = r7
            if (r5 == 0) goto L_0x03c2
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r0.currentUserTextureView
            org.webrtc.TextureViewRenderer r7 = r7.renderer
            boolean r8 = r5.isFrontFaceCamera()
            r7.setMirror(r8)
            boolean r7 = r0.currentUserIsVideo
            if (r7 == 0) goto L_0x0313
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r0.currentUserTextureView
            org.webrtc.TextureViewRenderer r7 = r7.renderer
            goto L_0x0314
        L_0x0313:
            r7 = 0
        L_0x0314:
            if (r2 == 0) goto L_0x0319
            org.webrtc.TextureViewRenderer r8 = r0.callingUserMiniTextureRenderer
            goto L_0x031d
        L_0x0319:
            org.telegram.ui.Components.voip.VoIPTextureView r8 = r0.callingUserTextureView
            org.webrtc.TextureViewRenderer r8 = r8.renderer
        L_0x031d:
            r5.setSinks(r7, r8)
            boolean r7 = r0.callingUserIsVideo
            if (r7 == 0) goto L_0x033d
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r5.call
            if (r7 == 0) goto L_0x033d
            boolean r7 = r7.video
            if (r7 != 0) goto L_0x033d
            org.telegram.messenger.voip.VoIPBaseService$SharedUIParams r7 = r5.sharedUIParams
            boolean r8 = r7.tapToVideoTooltipWasShowed
            if (r8 != 0) goto L_0x033d
            r7.tapToVideoTooltipWasShowed = r4
            org.telegram.ui.Components.HintView r7 = r0.tapToVideoTooltip
            org.telegram.ui.Components.voip.VoIPToggleButton[] r8 = r0.bottomButtons
            r8 = r8[r4]
            r7.showForView(r8, r4)
        L_0x033d:
            if (r1 == 0) goto L_0x0344
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r7 = r0.notificationsLayout
            r7.beforeLayoutChanges()
        L_0x0344:
            boolean r7 = r0.currentUserIsVideo
            java.lang.String r8 = "video"
            java.lang.String r9 = "muted"
            if (r7 != 0) goto L_0x0350
            boolean r7 = r0.callingUserIsVideo
            if (r7 == 0) goto L_0x03b1
        L_0x0350:
            int r7 = r0.currentState
            r13 = 3
            if (r7 == r13) goto L_0x0358
            r13 = 5
            if (r7 != r13) goto L_0x03b1
        L_0x0358:
            long r13 = r5.getCallDuration()
            r15 = 500(0x1f4, double:2.47E-321)
            int r7 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r7 <= 0) goto L_0x03b1
            int r7 = r5.getCurrentAudioState()
            if (r7 != 0) goto L_0x0384
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r7 = r0.notificationsLayout
            r13 = 2131165321(0x7var_, float:1.7944856E38)
            r14 = 2131627451(0x7f0e0dbb, float:1.8882167E38)
            java.lang.Object[] r15 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$User r11 = r0.callingUser
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)
            r15[r3] = r11
            java.lang.String r11 = "VoipUserMicrophoneIsOff"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r14, r15)
            r7.addNotification(r13, r11, r9, r1)
            goto L_0x0389
        L_0x0384:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r7 = r0.notificationsLayout
            r7.removeNotification(r9)
        L_0x0389:
            int r5 = r5.getCurrentVideoState()
            if (r5 != 0) goto L_0x03ab
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r5 = r0.notificationsLayout
            r7 = 2131165312(0x7var_, float:1.7944838E38)
            r9 = 2131627450(0x7f0e0dba, float:1.8882165E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$User r12 = r0.callingUser
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r12)
            r11[r3] = r12
            java.lang.String r12 = "VoipUserCameraIsOff"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r12, r9, r11)
            r5.addNotification(r7, r9, r8, r1)
            goto L_0x03bb
        L_0x03ab:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r5 = r0.notificationsLayout
            r5.removeNotification(r8)
            goto L_0x03bb
        L_0x03b1:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r5 = r0.notificationsLayout
            r5.removeNotification(r9)
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r5 = r0.notificationsLayout
            r5.removeNotification(r8)
        L_0x03bb:
            if (r1 == 0) goto L_0x03c2
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r5 = r0.notificationsLayout
            r5.animateLayoutChanges()
        L_0x03c2:
            org.telegram.ui.Components.voip.VoIPNotificationsLayout r5 = r0.notificationsLayout
            int r5 = r5.getChildsHight()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r7 = r0.callingUserMiniFloatingLayout
            r7.setBottomOffset(r5, r1)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r7 = r0.currentUserCameraFloatingLayout
            r7.setBottomOffset(r5, r1)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r5 = r0.currentUserCameraFloatingLayout
            boolean r7 = r0.uiVisible
            r5.setUiVisible(r7)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r5 = r0.callingUserMiniFloatingLayout
            boolean r7 = r0.uiVisible
            r5.setUiVisible(r7)
            boolean r5 = r0.currentUserIsVideo
            if (r5 == 0) goto L_0x03f6
            boolean r5 = r0.callingUserIsVideo
            if (r5 == 0) goto L_0x03f2
            boolean r5 = r0.cameraForceExpanded
            if (r5 == 0) goto L_0x03ed
            goto L_0x03f2
        L_0x03ed:
            r5 = 2
            r0.showFloatingLayout(r5, r1)
            goto L_0x03f9
        L_0x03f2:
            r0.showFloatingLayout(r4, r1)
            goto L_0x03f9
        L_0x03f6:
            r0.showFloatingLayout(r3, r1)
        L_0x03f9:
            r1 = 1056964608(0x3var_, float:0.5)
            if (r2 == 0) goto L_0x0463
            org.telegram.ui.Components.voip.VoIPFloatingLayout r5 = r0.callingUserMiniFloatingLayout
            java.lang.Object r5 = r5.getTag()
            if (r5 != 0) goto L_0x0463
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            r2.setIsActive(r4)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x0426
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            r2.setVisibility(r3)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            r2.setAlpha(r10)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            r2.setScaleX(r1)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            r2.setScaleY(r1)
        L_0x0426:
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r2 = 0
            android.view.ViewPropertyAnimator r1 = r1.setListener(r2)
            r1.cancel()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r6)
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r6)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r6)
            r2 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r1 = r1.setInterpolator(r5)
            android.view.ViewPropertyAnimator r1 = r1.setStartDelay(r2)
            r1.start()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r1.setTag(r2)
            goto L_0x04a2
        L_0x0463:
            if (r2 != 0) goto L_0x04a2
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            java.lang.Object r2 = r2.getTag()
            if (r2 == 0) goto L_0x04a2
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            r2.setIsActive(r3)
            org.telegram.ui.Components.voip.VoIPFloatingLayout r2 = r0.callingUserMiniFloatingLayout
            android.view.ViewPropertyAnimator r2 = r2.animate()
            android.view.ViewPropertyAnimator r2 = r2.alpha(r10)
            android.view.ViewPropertyAnimator r2 = r2.scaleX(r1)
            android.view.ViewPropertyAnimator r1 = r2.scaleY(r1)
            org.telegram.ui.VoIPFragment$12 r2 = new org.telegram.ui.VoIPFragment$12
            r2.<init>()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r2)
            r2 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r1 = r1.setInterpolator(r2)
            r1.start()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r2 = 0
            r1.setTag(r2)
        L_0x04a2:
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.currentUserCameraFloatingLayout
            r1.restoreRelativePosition()
            org.telegram.ui.Components.voip.VoIPFloatingLayout r1 = r0.callingUserMiniFloatingLayout
            r1.restoreRelativePosition()
            r17.updateSpeakerPhoneIcon()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPFragment.updateViewState():void");
    }

    public /* synthetic */ void lambda$updateViewState$16$VoIPFragment() {
        this.windowView.finish();
    }

    private void fillNavigationBar(boolean z, boolean z2) {
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

    /* access modifiers changed from: private */
    public void showUi(boolean z) {
        ValueAnimator valueAnimator = this.uiVisibilityAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int i = 0;
        if (!z && this.uiVisible) {
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
        } else if (z && !this.uiVisible) {
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
        TLRPC$TL_encryptedChat tLRPC$TL_encryptedChat = new TLRPC$TL_encryptedChat();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(sharedInstance.getEncryptionKey());
                byteArrayOutputStream.write(sharedInstance.getGA());
                tLRPC$TL_encryptedChat.auth_key = byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            byte[] bArr = tLRPC$TL_encryptedChat.auth_key;
            String[] emojifyForCall = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(bArr, 0, bArr.length));
            for (int i = 0; i < 4; i++) {
                Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(emojifyForCall[i]);
                if (emojiDrawable != null) {
                    emojiDrawable.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
                    emojiDrawable.preload();
                    this.emojiViews[i].setImageDrawable(emojiDrawable);
                    this.emojiViews[i].setVisibility(8);
                }
                this.emojiDrawables[i] = emojiDrawable;
            }
            checkEmojiLoaded(z);
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
                transitionSet.addTransition(new Visibility(this) {
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
                }.setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT)).addTransition(new ChangeBounds().setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT));
                transitionSet.excludeChildren(VoIPToggleButton.class, true);
                TransitionManager.beginDelayedTransition(this.buttonsLayout, transitionSet);
            }
            int i = this.currentState;
            if (i == 15 || i == 17) {
                TLRPC$PhoneCall tLRPC$PhoneCall = sharedInstance.call;
                if (tLRPC$PhoneCall == null || !tLRPC$PhoneCall.video || this.currentState != 15) {
                    this.bottomButtons[0].setVisibility(8);
                    this.bottomButtons[1].setVisibility(8);
                    this.bottomButtons[2].setVisibility(8);
                } else {
                    if (this.currentUserIsVideo) {
                        setFrontalCameraAction(this.bottomButtons[0], sharedInstance, z);
                        if (this.uiVisible) {
                            this.speakerPhoneIcon.animate().alpha(1.0f).start();
                        }
                    } else {
                        setSpeakerPhoneAction(this.bottomButtons[0], sharedInstance, z);
                        this.speakerPhoneIcon.animate().alpha(0.0f).start();
                    }
                    setVideoAction(this.bottomButtons[1], sharedInstance, z);
                    setMicrohoneAction(this.bottomButtons[2], sharedInstance, z);
                }
                this.bottomButtons[3].setVisibility(8);
            } else if (instance != null) {
                if (this.currentUserIsVideo) {
                    setFrontalCameraAction(this.bottomButtons[0], sharedInstance, z);
                    if (this.uiVisible) {
                        this.speakerPhoneIcon.animate().alpha(1.0f).start();
                    }
                } else {
                    setSpeakerPhoneAction(this.bottomButtons[0], sharedInstance, z);
                    this.speakerPhoneIcon.animate().alpha(0.0f).start();
                }
                setVideoAction(this.bottomButtons[1], sharedInstance, z);
                setMicrohoneAction(this.bottomButtons[2], sharedInstance, z);
                this.bottomButtons[3].setData(NUM, -1, -1041108, LocaleController.getString("VoipEndCall", NUM), false, z);
                this.bottomButtons[3].setOnClickListener($$Lambda$VoIPFragment$2Mj4qPmFKJAGv2cAUi0sJ2kqQK4.INSTANCE);
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

    static /* synthetic */ void lambda$updateButtons$17(View view) {
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
                VoIPFragment.this.lambda$setMicrohoneAction$18$VoIPFragment(view);
            }
        });
    }

    public /* synthetic */ void lambda$setMicrohoneAction$18$VoIPFragment(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().setMicMute(!VoIPService.getSharedInstance().isMicMute());
            updateButtons(true);
        }
    }

    private void setVideoAction(VoIPToggleButton voIPToggleButton, VoIPService voIPService, boolean z) {
        if ((this.currentUserIsVideo || this.callingUserIsVideo) ? true : voIPService.isVideoAvailable()) {
            if (this.currentUserIsVideo) {
                voIPToggleButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipStopVideo", NUM), false, z);
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
                    VoIPFragment.this.lambda$setVideoAction$20$VoIPFragment(this.f$1, view);
                }
            });
            voIPToggleButton.setEnabled(true);
            return;
        }
        voIPToggleButton.setData(NUM, ColorUtils.setAlphaComponent(-1, 127), ColorUtils.setAlphaComponent(-1, 30), "Video", false, z);
        voIPToggleButton.setOnClickListener((View.OnClickListener) null);
        voIPToggleButton.setEnabled(false);
    }

    public /* synthetic */ void lambda$setVideoAction$20$VoIPFragment(VoIPService voIPService, View view) {
        if (Build.VERSION.SDK_INT < 23 || this.activity.checkSelfPermission("android.permission.CAMERA") == 0) {
            TLRPC$PhoneCall tLRPC$PhoneCall = voIPService.call;
            if (tLRPC$PhoneCall == null || tLRPC$PhoneCall.video || this.callingUserIsVideo || voIPService.sharedUIParams.cameraAlertWasShowed) {
                toggleCameraInput();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setMessage(LocaleController.getString("VoipSwitchToVideoCall", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(voIPService) {
                public final /* synthetic */ VoIPService f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPFragment.this.lambda$null$19$VoIPFragment(this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.create().show();
            return;
        }
        this.activity.requestPermissions(new String[]{"android.permission.CAMERA"}, 102);
    }

    public /* synthetic */ void lambda$null$19$VoIPFragment(VoIPService voIPService, DialogInterface dialogInterface, int i) {
        voIPService.sharedUIParams.cameraAlertWasShowed = true;
        toggleCameraInput();
    }

    private void updateSpeakerPhoneIcon() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (!sharedInstance.isSpeakerphoneOn()) {
                this.speakerPhoneIcon.setImageResource(NUM);
            } else if (sharedInstance.isBluetoothOn()) {
                this.speakerPhoneIcon.setImageResource(NUM);
            } else {
                this.speakerPhoneIcon.setImageResource(NUM);
            }
        }
    }

    private void setSpeakerPhoneAction(VoIPToggleButton voIPToggleButton, VoIPService voIPService, boolean z) {
        if (voIPService.isSpeakerphoneOn()) {
            voIPToggleButton.setData(voIPService.isBluetoothOn() ? NUM : NUM, -16777216, -1, LocaleController.getString("VoipSpeaker", NUM), false, z);
        } else {
            voIPToggleButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipSpeaker", NUM), false, z);
        }
        voIPToggleButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPFragment.this.lambda$setSpeakerPhoneAction$21$VoIPFragment(view);
            }
        });
    }

    public /* synthetic */ void lambda$setSpeakerPhoneAction$21$VoIPFragment(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity);
        }
    }

    private void setFrontalCameraAction(VoIPToggleButton voIPToggleButton, VoIPService voIPService, boolean z) {
        if (!voIPService.isFrontFaceCamera()) {
            voIPToggleButton.setData(NUM, -16777216, -1, LocaleController.getString("VoipFlip", NUM), false, z);
        } else {
            voIPToggleButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipFlip", NUM), false, z);
        }
        voIPToggleButton.setOnClickListener($$Lambda$VoIPFragment$x_1RZL381ODjIVlMTP1JKnckpsc.INSTANCE);
    }

    static /* synthetic */ void lambda$setFrontalCameraAction$22(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().switchCamera();
        }
    }

    private void toggleCameraInput() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (!this.currentUserIsVideo) {
                this.currentUserIsVideo = true;
                if (!sharedInstance.isSpeakerphoneOn()) {
                    VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity);
                }
                sharedInstance.requestVideoCall();
                sharedInstance.setVideoState(2);
            } else {
                this.currentUserTextureView.saveCameraLastBitmap();
                sharedInstance.setVideoState(0);
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
                        VoIPFragment.this.lambda$onRequestPermissionsResultInternal$23$VoIPFragment();
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

    public /* synthetic */ void lambda$onRequestPermissionsResultInternal$23$VoIPFragment() {
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

    public void onPause() {
        WindowInsets windowInsets;
        if (this.canSwitchToPip && checkInlinePermissions() && VoIPService.getSharedInstance() != null && VoIPPiPView.getInstance() == null) {
            int measuredHeight = instance.windowView.getMeasuredHeight();
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                measuredHeight -= windowInsets.getSystemWindowInsetBottom();
            }
            VoIPFragment voIPFragment = instance;
            VoIPPiPView.show(voIPFragment.activity, voIPFragment.windowView.getMeasuredWidth(), measuredHeight, 0);
            if (Build.VERSION.SDK_INT >= 20 && instance.lastInsets != null) {
                VoIPPiPView.getInstance().topInset = instance.lastInsets.getSystemWindowInsetTop();
                VoIPPiPView.getInstance().bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
            }
        }
    }

    public void onResume() {
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.getInstance().finish();
        }
        if (VoIPService.getSharedInstance() != null) {
            updateViewState();
        } else {
            this.windowView.finish();
        }
    }

    private boolean checkInlinePermissions() {
        return Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.activity);
    }

    @SuppressLint({"InlinedApi"})
    private void requestInlinePermissions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM));
        builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                VoIPFragment.this.lambda$requestInlinePermissions$24$VoIPFragment(dialogInterface, i);
            }
        });
        builder.show();
    }

    public /* synthetic */ void lambda$requestInlinePermissions$24$VoIPFragment(DialogInterface dialogInterface, int i) {
        Activity activity2 = this.activity;
        if (activity2 != null) {
            try {
                activity2.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + this.activity.getPackageName())));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }
}
