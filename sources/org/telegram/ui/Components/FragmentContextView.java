package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    /* access modifiers changed from: private */
    public final int account;
    private FragmentContextView additionalContextView;
    /* access modifiers changed from: private */
    public int animationIndex;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private View applyingView;
    /* access modifiers changed from: private */
    public AvatarsImageView avatars;
    /* access modifiers changed from: private */
    public boolean checkCallAfterAnimation;
    /* access modifiers changed from: private */
    public boolean checkImportAfterAnimation;
    /* access modifiers changed from: private */
    public Runnable checkLocationRunnable;
    /* access modifiers changed from: private */
    public boolean checkPlayerAfterAnimation;
    private ImageView closeButton;
    float collapseProgress;
    boolean collapseTransition;
    private int currentProgress;
    /* access modifiers changed from: private */
    public int currentStyle;
    /* access modifiers changed from: private */
    public FragmentContextViewDelegate delegate;
    boolean drawOverlay;
    float extraHeight;
    private boolean firstLocationsLoaded;
    /* access modifiers changed from: private */
    public BaseFragment fragment;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout;
    /* access modifiers changed from: private */
    public Paint gradientPaint;
    /* access modifiers changed from: private */
    public TextPaint gradientTextPaint;
    /* access modifiers changed from: private */
    public int gradientWidth;
    private RLottieImageView importingImageView;
    private boolean isLocation;
    private boolean isMusic;
    /* access modifiers changed from: private */
    public boolean isMuted;
    private TextView joinButton;
    private int lastLocationSharingCount;
    private MessageObject lastMessageObject;
    private String lastString;
    /* access modifiers changed from: private */
    public LinearGradient linearGradient;
    /* access modifiers changed from: private */
    public Matrix matrix;
    float micAmplitude;
    /* access modifiers changed from: private */
    public RLottieImageView muteButton;
    /* access modifiers changed from: private */
    public RLottieDrawable muteDrawable;
    private ImageView playButton;
    private PlayPauseDrawable playPauseDrawable;
    private ActionBarMenuItem playbackSpeedButton;
    /* access modifiers changed from: private */
    public RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public boolean scheduleRunnableScheduled;
    private View selector;
    private View shadow;
    float speakerAmplitude;
    private ActionBarMenuSubItem[] speedItems;
    private AudioPlayerAlert.ClippingTextViewSwitcher subtitleTextView;
    private boolean supportsCalls;
    /* access modifiers changed from: private */
    public StaticLayout timeLayout;
    private AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private float topPadding;
    /* access modifiers changed from: private */
    public final Runnable updateScheduleTimeRunnable;
    /* access modifiers changed from: private */
    public boolean visible;
    boolean wasDraw;

    public interface FragmentContextViewDelegate {
        void onAnimation(boolean z, boolean z2);
    }

    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    /* access modifiers changed from: protected */
    public void playbackSpeedChanged(float f) {
    }

    public void onAudioSettingsChanged() {
        boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        if (this.isMuted != z) {
            this.isMuted = z;
            this.muteDrawable.setCustomEndFrame(z ? 15 : 29);
            RLottieDrawable rLottieDrawable = this.muteDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            this.muteButton.invalidate();
            Theme.getFragmentContextViewWavesDrawable().updateState(this.visible);
        }
        if (this.isMuted) {
            this.micAmplitude = 0.0f;
            Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
        }
    }

    public FragmentContextView(Context context, BaseFragment baseFragment, boolean z) {
        this(context, baseFragment, (View) null, z, (Theme.ResourcesProvider) null);
    }

    public FragmentContextView(Context context, BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        this(context, baseFragment, (View) null, z, resourcesProvider2);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FragmentContextView(Context context, BaseFragment baseFragment, View view, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        float f;
        int i;
        final Context context2 = context;
        BaseFragment baseFragment2 = baseFragment;
        View view2 = view;
        boolean z2 = z;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.speedItems = new ActionBarMenuSubItem[4];
        this.currentProgress = -1;
        this.currentStyle = -1;
        this.supportsCalls = true;
        this.rect = new RectF();
        this.updateScheduleTimeRunnable = new Runnable() {
            public void run() {
                String str;
                if (FragmentContextView.this.gradientTextPaint == null || !(FragmentContextView.this.fragment instanceof ChatActivity)) {
                    boolean unused = FragmentContextView.this.scheduleRunnableScheduled = false;
                    return;
                }
                ChatObject.Call groupCall = ((ChatActivity) FragmentContextView.this.fragment).getGroupCall();
                if (groupCall == null || !groupCall.isScheduled()) {
                    StaticLayout unused2 = FragmentContextView.this.timeLayout = null;
                    boolean unused3 = FragmentContextView.this.scheduleRunnableScheduled = false;
                    return;
                }
                int currentTime = FragmentContextView.this.fragment.getConnectionsManager().getCurrentTime();
                int i = groupCall.call.schedule_date;
                int i2 = i - currentTime;
                if (i2 >= 86400) {
                    str = LocaleController.formatPluralString("Days", Math.round(((float) i2) / 86400.0f));
                } else {
                    str = AndroidUtilities.formatFullDuration(i - currentTime);
                }
                String str2 = str;
                StaticLayout unused4 = FragmentContextView.this.timeLayout = new StaticLayout(str2, FragmentContextView.this.gradientTextPaint, (int) Math.ceil((double) FragmentContextView.this.gradientTextPaint.measureText(str2)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                AndroidUtilities.runOnUIThread(FragmentContextView.this.updateScheduleTimeRunnable, 1000);
                FragmentContextView.this.frameLayout.invalidate();
            }
        };
        this.account = UserConfig.selectedAccount;
        this.lastLocationSharingCount = -1;
        this.checkLocationRunnable = new Runnable() {
            public void run() {
                FragmentContextView.this.checkLocationString();
                AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000);
            }
        };
        this.animationIndex = -1;
        this.resourcesProvider = resourcesProvider3;
        this.fragment = baseFragment2;
        SizeNotifierFrameLayout sizeNotifierFrameLayout = baseFragment.getFragmentView() instanceof SizeNotifierFrameLayout ? (SizeNotifierFrameLayout) this.fragment.getFragmentView() : null;
        this.applyingView = view2;
        this.visible = true;
        this.isLocation = z2;
        if (view2 == null) {
            ((ViewGroup) this.fragment.getFragmentView()).setClipToPadding(false);
        }
        setTag(1);
        AnonymousClass3 r1 = new BlurredFrameLayout(context2, sizeNotifierFrameLayout) {
            public void invalidate() {
                super.invalidate();
                if (FragmentContextView.this.avatars != null && FragmentContextView.this.avatars.getVisibility() == 0) {
                    FragmentContextView.this.avatars.invalidate();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float f;
                super.onDraw(canvas);
                if (FragmentContextView.this.currentStyle == 4 && FragmentContextView.this.timeLayout != null) {
                    int ceil = ((int) Math.ceil((double) FragmentContextView.this.timeLayout.getLineWidth(0))) + AndroidUtilities.dp(24.0f);
                    if (ceil != FragmentContextView.this.gradientWidth) {
                        LinearGradient unused = FragmentContextView.this.linearGradient = new LinearGradient(0.0f, 0.0f, 1.7f * ((float) ceil), 0.0f, new int[]{-10187532, -7575089, -2860679, -2860679}, new float[]{0.0f, 0.294f, 0.588f, 1.0f}, Shader.TileMode.CLAMP);
                        FragmentContextView.this.gradientPaint.setShader(FragmentContextView.this.linearGradient);
                        int unused2 = FragmentContextView.this.gradientWidth = ceil;
                    }
                    ChatObject.Call groupCall = ((ChatActivity) FragmentContextView.this.fragment).getGroupCall();
                    if (FragmentContextView.this.fragment == null || groupCall == null || !groupCall.isScheduled()) {
                        f = 0.0f;
                    } else {
                        long currentTimeMillis = (((long) groupCall.call.schedule_date) * 1000) - FragmentContextView.this.fragment.getConnectionsManager().getCurrentTimeMillis();
                        f = 1.0f;
                        if (currentTimeMillis >= 0) {
                            f = currentTimeMillis < 5000 ? 1.0f - (((float) currentTimeMillis) / 5000.0f) : 0.0f;
                        }
                        if (currentTimeMillis < 6000) {
                            invalidate();
                        }
                    }
                    FragmentContextView.this.matrix.reset();
                    FragmentContextView.this.matrix.postTranslate(((float) (-FragmentContextView.this.gradientWidth)) * 0.7f * f, 0.0f);
                    FragmentContextView.this.linearGradient.setLocalMatrix(FragmentContextView.this.matrix);
                    int dp = AndroidUtilities.dp(12.0f);
                    FragmentContextView.this.rect.set(0.0f, 0.0f, (float) ceil, (float) AndroidUtilities.dp(24.0f));
                    canvas.save();
                    canvas.translate((float) ((getMeasuredWidth() - ceil) - AndroidUtilities.dp(10.0f)), (float) dp);
                    canvas.drawRoundRect(FragmentContextView.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), FragmentContextView.this.gradientPaint);
                    canvas.translate((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(4.0f));
                    FragmentContextView.this.timeLayout.draw(canvas);
                    canvas.restore();
                }
            }
        };
        this.frameLayout = r1;
        addView(r1, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view3 = new View(context2);
        this.selector = view3;
        this.frameLayout.addView(view3, LayoutHelper.createFrame(-1, -1.0f));
        View view4 = new View(context2);
        this.shadow = view4;
        view4.setBackgroundResource(NUM);
        addView(this.shadow, LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.playButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("inappPlayerPlayPause"), PorterDuff.Mode.MULTIPLY));
        ImageView imageView2 = this.playButton;
        PlayPauseDrawable playPauseDrawable2 = new PlayPauseDrawable(14);
        this.playPauseDrawable = playPauseDrawable2;
        imageView2.setImageDrawable(playPauseDrawable2);
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21) {
            this.playButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("inappPlayerPlayPause") & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda3(this));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.importingImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.importingImageView.setAutoRepeat(true);
        this.importingImageView.setAnimation(NUM, 30, 30);
        this.importingImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(22.0f), getThemedColor("inappPlayerPlayPause")));
        addView(this.importingImageView, LayoutHelper.createFrame(22, 22.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        AnonymousClass4 r12 = new AudioPlayerAlert.ClippingTextViewSwitcher(context2) {
            /* access modifiers changed from: protected */
            public TextView createTextView() {
                TextView textView = new TextView(context2);
                textView.setMaxLines(1);
                textView.setLines(1);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextSize(1, 15.0f);
                textView.setGravity(19);
                if (FragmentContextView.this.currentStyle == 0 || FragmentContextView.this.currentStyle == 2) {
                    textView.setGravity(19);
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextSize(1, 15.0f);
                } else if (FragmentContextView.this.currentStyle == 4) {
                    textView.setGravity(51);
                    textView.setTextColor(FragmentContextView.this.getThemedColor("inappPlayerPerformer"));
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView.setTextSize(1, 15.0f);
                } else if (FragmentContextView.this.currentStyle == 1 || FragmentContextView.this.currentStyle == 3) {
                    textView.setGravity(19);
                    textView.setTextColor(FragmentContextView.this.getThemedColor("returnToCallText"));
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView.setTextSize(1, 14.0f);
                }
                return textView;
            }
        };
        this.titleTextView = r12;
        addView(r12, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        AnonymousClass5 r13 = new AudioPlayerAlert.ClippingTextViewSwitcher(context2) {
            /* access modifiers changed from: protected */
            public TextView createTextView() {
                TextView textView = new TextView(context2);
                textView.setMaxLines(1);
                textView.setLines(1);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setGravity(3);
                textView.setTextSize(1, 13.0f);
                textView.setTextColor(FragmentContextView.this.getThemedColor("inappPlayerClose"));
                return textView;
            }
        };
        this.subtitleTextView = r13;
        addView(r13, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 10.0f, 36.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.joinButton = textView;
        textView.setText(LocaleController.getString("VoipChatJoin", NUM));
        this.joinButton.setTextColor(getThemedColor("featuredStickers_buttonText"));
        this.joinButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("featuredStickers_addButton"), getThemedColor("featuredStickers_addButtonPressed")));
        this.joinButton.setTextSize(1, 14.0f);
        this.joinButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.joinButton.setGravity(17);
        this.joinButton.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        addView(this.joinButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 10.0f, 14.0f, 0.0f));
        this.joinButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda2(this));
        if (!z2) {
            i = 36;
            f = 14.0f;
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, getThemedColor("dialogTextBlack"), resourcesProvider2);
            this.playbackSpeedButton = actionBarMenuItem;
            actionBarMenuItem.setLongClickEnabled(false);
            this.playbackSpeedButton.setShowSubmenuByMove(false);
            this.playbackSpeedButton.setContentDescription(LocaleController.getString("AccDescrPlayerSpeed", NUM));
            this.playbackSpeedButton.setDelegate(new FragmentContextView$$ExternalSyntheticLambda10(this));
            this.speedItems[0] = this.playbackSpeedButton.addSubItem(1, NUM, LocaleController.getString("SpeedSlow", NUM));
            this.speedItems[1] = this.playbackSpeedButton.addSubItem(2, NUM, LocaleController.getString("SpeedNormal", NUM));
            this.speedItems[2] = this.playbackSpeedButton.addSubItem(3, NUM, LocaleController.getString("SpeedFast", NUM));
            this.speedItems[3] = this.playbackSpeedButton.addSubItem(4, NUM, LocaleController.getString("SpeedVeryFast", NUM));
            if (AndroidUtilities.density >= 3.0f) {
                this.playbackSpeedButton.setPadding(0, 1, 0, 0);
            }
            this.playbackSpeedButton.setAdditionalXOffset(AndroidUtilities.dp(8.0f));
            addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
            this.playbackSpeedButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda4(this));
            this.playbackSpeedButton.setOnLongClickListener(new FragmentContextView$$ExternalSyntheticLambda8(this));
            updatePlaybackButton();
        } else {
            i = 36;
            f = 14.0f;
        }
        AvatarsImageView avatarsImageView = new AvatarsImageView(context2, false);
        this.avatars = avatarsImageView;
        avatarsImageView.setDelegate(new FragmentContextView$$ExternalSyntheticLambda9(this));
        this.avatars.setVisibility(8);
        addView(this.avatars, LayoutHelper.createFrame(108, i, 51));
        this.muteDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), true, (int[]) null);
        AnonymousClass6 r14 = new RLottieImageView(context2) {
            private final Runnable pressRunnable = new FragmentContextView$6$$ExternalSyntheticLambda0(this);
            boolean pressed;
            boolean scheduled;
            private final Runnable toggleMicRunnable = new FragmentContextView$6$$ExternalSyntheticLambda1(this);

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$$0() {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setMicMute(false, true, false);
                    if (FragmentContextView.this.muteDrawable.setCustomEndFrame(FragmentContextView.this.isMuted ? 15 : 29)) {
                        if (FragmentContextView.this.isMuted) {
                            FragmentContextView.this.muteDrawable.setCurrentFrame(0);
                        } else {
                            FragmentContextView.this.muteDrawable.setCurrentFrame(14);
                        }
                    }
                    FragmentContextView.this.muteButton.playAnimation();
                    Theme.getFragmentContextViewWavesDrawable().updateState(true);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$$1() {
                if (this.scheduled && VoIPService.getSharedInstance() != null) {
                    this.scheduled = false;
                    this.pressed = true;
                    boolean unused = FragmentContextView.this.isMuted = false;
                    AndroidUtilities.runOnUIThread(this.toggleMicRunnable, 90);
                    FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
                }
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (FragmentContextView.this.currentStyle != 3 && FragmentContextView.this.currentStyle != 1) {
                    return super.onTouchEvent(motionEvent);
                }
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                if (sharedInstance == null) {
                    AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                    AndroidUtilities.cancelRunOnUIThread(this.toggleMicRunnable);
                    this.scheduled = false;
                    this.pressed = false;
                    return true;
                }
                if (motionEvent.getAction() == 0 && sharedInstance.isMicMute()) {
                    AndroidUtilities.runOnUIThread(this.pressRunnable, 300);
                    this.scheduled = true;
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    AndroidUtilities.cancelRunOnUIThread(this.toggleMicRunnable);
                    if (this.scheduled) {
                        AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                        this.scheduled = false;
                    } else if (this.pressed) {
                        boolean unused = FragmentContextView.this.isMuted = true;
                        if (FragmentContextView.this.muteDrawable.setCustomEndFrame(15)) {
                            if (FragmentContextView.this.isMuted) {
                                FragmentContextView.this.muteDrawable.setCurrentFrame(0);
                            } else {
                                FragmentContextView.this.muteDrawable.setCurrentFrame(14);
                            }
                        }
                        FragmentContextView.this.muteButton.playAnimation();
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().setMicMute(true, true, false);
                            FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
                        }
                        this.pressed = false;
                        Theme.getFragmentContextViewWavesDrawable().updateState(true);
                        MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                        super.onTouchEvent(obtain);
                        obtain.recycle();
                        return true;
                    }
                }
                return super.onTouchEvent(motionEvent);
            }

            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                String str;
                int i;
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setClassName(Button.class.getName());
                if (FragmentContextView.this.isMuted) {
                    i = NUM;
                    str = "VoipUnmute";
                } else {
                    i = NUM;
                    str = "VoipMute";
                }
                accessibilityNodeInfo.setText(LocaleController.getString(str, i));
            }
        };
        this.muteButton = r14;
        r14.setColorFilter(new PorterDuffColorFilter(getThemedColor("returnToCallText"), PorterDuff.Mode.MULTIPLY));
        if (i2 >= 21) {
            this.muteButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(f)));
        }
        this.muteButton.setAnimation(this.muteDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setVisibility(8);
        addView(this.muteButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.muteButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda5(this));
        ImageView imageView3 = new ImageView(context2);
        this.closeButton = imageView3;
        imageView3.setImageResource(NUM);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("inappPlayerClose"), PorterDuff.Mode.MULTIPLY));
        if (i2 >= 21) {
            this.closeButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(f)));
        }
        this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.closeButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda6(this, resourcesProvider3));
        setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda7(this, resourcesProvider3, baseFragment2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        if (this.currentStyle != 0) {
            return;
        }
        if (MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else {
            MediaController.getInstance().lambda$startAudioAgain$7(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        callOnClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i) {
        float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        if (i == 1) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 0.5f);
        } else if (i == 2) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else if (i == 3) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.5f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.8f);
        }
        float playbackSpeed2 = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        if (playbackSpeed != playbackSpeed2) {
            playbackSpeedChanged(playbackSpeed2);
        }
        updatePlaybackButton();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        float f = 1.0f;
        if (Math.abs(MediaController.getInstance().getPlaybackSpeed(this.isMusic) - 1.0f) > 0.001f) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else {
            MediaController instance = MediaController.getInstance();
            boolean z = this.isMusic;
            float fastPlaybackSpeed = MediaController.getInstance().getFastPlaybackSpeed(this.isMusic);
            instance.setPlaybackSpeed(z, fastPlaybackSpeed);
            f = fastPlaybackSpeed;
        }
        playbackSpeedChanged(f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$4(View view) {
        this.playbackSpeedButton.toggleSubMenu();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5() {
        updateAvatars(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.groupCall != null) {
                AccountInstance.getInstance(sharedInstance.getAccount());
                ChatObject.Call call = sharedInstance.groupCall;
                TLRPC$Chat chat = sharedInstance.getChat();
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call.participants.get(sharedInstance.getSelfId());
                if (tLRPC$TL_groupCallParticipant != null && !tLRPC$TL_groupCallParticipant.can_self_unmute && tLRPC$TL_groupCallParticipant.muted && !ChatObject.canManageCalls(chat)) {
                    return;
                }
            }
            boolean z = !sharedInstance.isMicMute();
            this.isMuted = z;
            sharedInstance.setMicMute(z, false, true);
            if (this.muteDrawable.setCustomEndFrame(this.isMuted ? 15 : 29)) {
                if (this.isMuted) {
                    this.muteDrawable.setCurrentFrame(0);
                } else {
                    this.muteDrawable.setCurrentFrame(14);
                }
            }
            this.muteButton.playAnimation();
            Theme.getFragmentContextViewWavesDrawable().updateState(true);
            this.muteButton.performHapticFeedback(3, 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(Theme.ResourcesProvider resourcesProvider2, View view) {
        if (this.currentStyle == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.fragment.getParentActivity(), resourcesProvider2);
            builder.setTitle(LocaleController.getString("StopLiveLocationAlertToTitle", NUM));
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof DialogsActivity) {
                builder.setMessage(LocaleController.getString("StopLiveLocationAlertAllText", NUM));
            } else {
                ChatActivity chatActivity = (ChatActivity) baseFragment;
                TLRPC$Chat currentChat = chatActivity.getCurrentChat();
                TLRPC$User currentUser = chatActivity.getCurrentUser();
                if (currentChat != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToGroupText", NUM, currentChat.title)));
                } else if (currentUser != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToUserText", NUM, UserObject.getFirstName(currentUser))));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSure", NUM));
                }
            }
            builder.setPositiveButton(LocaleController.getString("Stop", NUM), new FragmentContextView$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            builder.show();
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(getThemedColor("dialogTextRed2"));
                return;
            }
            return;
        }
        MediaController.getInstance().cleanupPlayer(true, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            for (int i2 = 0; i2 < 3; i2++) {
                LocationController.getInstance(i2).removeAllLocationSharings();
            }
            return;
        }
        LocationController.getInstance(baseFragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) this.fragment).getDialogId());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10(Theme.ResourcesProvider resourcesProvider2, BaseFragment baseFragment, View view) {
        ChatActivity chatActivity;
        ChatObject.Call groupCall;
        long j;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        int i = this.currentStyle;
        long j2 = 0;
        if (i == 0) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment != null && playingMessageObject != null) {
                if (!playingMessageObject.isMusic()) {
                    BaseFragment baseFragment2 = this.fragment;
                    if (baseFragment2 instanceof ChatActivity) {
                        j2 = ((ChatActivity) baseFragment2).getDialogId();
                    }
                    if (playingMessageObject.getDialogId() == j2) {
                        ((ChatActivity) this.fragment).scrollToMessageId(playingMessageObject.getId(), 0, false, 0, true, 0);
                        return;
                    }
                    long dialogId = playingMessageObject.getDialogId();
                    Bundle bundle = new Bundle();
                    if (DialogObject.isEncryptedDialog(dialogId)) {
                        bundle.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
                    } else if (DialogObject.isUserDialog(dialogId)) {
                        bundle.putLong("user_id", dialogId);
                    } else {
                        bundle.putLong("chat_id", -dialogId);
                    }
                    bundle.putInt("message_id", playingMessageObject.getId());
                    this.fragment.presentFragment(new ChatActivity(bundle), this.fragment instanceof ChatActivity);
                } else if (getContext() instanceof LaunchActivity) {
                    this.fragment.showDialog(new AudioPlayerAlert(getContext(), resourcesProvider3));
                }
            }
        } else {
            boolean z = true;
            if (i == 1) {
                getContext().startActivity(new Intent(getContext(), LaunchActivity.class).setAction("voip"));
            } else if (i == 2) {
                int i2 = UserConfig.selectedAccount;
                BaseFragment baseFragment3 = this.fragment;
                if (baseFragment3 instanceof ChatActivity) {
                    j = ((ChatActivity) baseFragment3).getDialogId();
                    i2 = this.fragment.getCurrentAccount();
                } else {
                    if (LocationController.getLocationsCount() == 1) {
                        int i3 = 0;
                        while (true) {
                            if (i3 >= 3) {
                                break;
                            } else if (!LocationController.getInstance(i3).sharingLocationsUI.isEmpty()) {
                                LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(i3).sharingLocationsUI.get(0);
                                j = sharingLocationInfo.did;
                                i2 = sharingLocationInfo.messageObject.currentAccount;
                                break;
                            } else {
                                i3++;
                            }
                        }
                    }
                    j = 0;
                }
                if (j != 0) {
                    openSharingLocation(LocationController.getInstance(i2).getSharingLocationInfo(j));
                } else {
                    this.fragment.showDialog(new SharingLocationsAlert(getContext(), new FragmentContextView$$ExternalSyntheticLambda11(this), resourcesProvider3));
                }
            } else if (i == 3) {
                if (VoIPService.getSharedInstance() != null && (getContext() instanceof LaunchActivity)) {
                    GroupCallActivity.create((LaunchActivity) getContext(), AccountInstance.getInstance(VoIPService.getSharedInstance().getAccount()), (TLRPC$Chat) null, (TLRPC$InputPeer) null, false, (String) null);
                }
            } else if (i == 4) {
                if (this.fragment.getParentActivity() != null && (groupCall = chatActivity.getGroupCall()) != null) {
                    TLRPC$Chat chat = (chatActivity = (ChatActivity) this.fragment).getMessagesController().getChat(Long.valueOf(groupCall.chatId));
                    TLRPC$GroupCall tLRPC$GroupCall = groupCall.call;
                    if (tLRPC$GroupCall == null || tLRPC$GroupCall.rtmp_stream) {
                        z = false;
                    }
                    Boolean valueOf = Boolean.valueOf(z);
                    Activity parentActivity = this.fragment.getParentActivity();
                    BaseFragment baseFragment4 = this.fragment;
                    VoIPHelper.startCall(chat, (TLRPC$InputPeer) null, (String) null, false, valueOf, parentActivity, baseFragment4, baseFragment4.getAccountInstance());
                }
            } else if (i == 5 && baseFragment.getSendMessagesHelper().getImportingHistory(((ChatActivity) baseFragment).getDialogId()) != null) {
                ImportingAlert importingAlert = new ImportingAlert(getContext(), (String) null, (ChatActivity) this.fragment, resourcesProvider3);
                importingAlert.setOnHideListener(new FragmentContextView$$ExternalSyntheticLambda1(this));
                this.fragment.showDialog(importingAlert);
                checkImport(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(DialogInterface dialogInterface) {
        checkImport(false);
    }

    public void setSupportsCalls(boolean z) {
        this.supportsCalls = z;
    }

    public void setDelegate(FragmentContextViewDelegate fragmentContextViewDelegate) {
        this.delegate = fragmentContextViewDelegate;
    }

    private void updatePlaybackButton() {
        if (this.playbackSpeedButton != null) {
            float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
            float fastPlaybackSpeed = MediaController.getInstance().getFastPlaybackSpeed(this.isMusic);
            if (Math.abs(fastPlaybackSpeed - 1.8f) < 0.001f) {
                this.playbackSpeedButton.setIcon(NUM);
            } else if (Math.abs(fastPlaybackSpeed - 1.5f) < 0.001f) {
                this.playbackSpeedButton.setIcon(NUM);
            } else {
                this.playbackSpeedButton.setIcon(NUM);
            }
            updateColors();
            for (int i = 0; i < this.speedItems.length; i++) {
                if ((i != 0 || Math.abs(playbackSpeed - 0.5f) >= 0.001f) && ((i != 1 || Math.abs(playbackSpeed - 1.0f) >= 0.001f) && ((i != 2 || Math.abs(playbackSpeed - 1.5f) >= 0.001f) && (i != 3 || Math.abs(playbackSpeed - 1.8f) >= 0.001f)))) {
                    this.speedItems[i].setColors(getThemedColor("actionBarDefaultSubmenuItem"), getThemedColor("actionBarDefaultSubmenuItemIcon"));
                } else {
                    this.speedItems[i].setColors(getThemedColor("inappPlayerPlayPause"), getThemedColor("inappPlayerPlayPause"));
                }
            }
        }
    }

    public void updateColors() {
        if (this.playbackSpeedButton != null) {
            String str = Math.abs(MediaController.getInstance().getPlaybackSpeed(this.isMusic) - 1.0f) > 0.001f ? "inappPlayerPlayPause" : "inappPlayerClose";
            this.playbackSpeedButton.setIconColor(getThemedColor(str));
            if (Build.VERSION.SDK_INT >= 21) {
                this.playbackSpeedButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str) & NUM, 1, AndroidUtilities.dp(14.0f)));
            }
        }
    }

    public void setAdditionalContextView(FragmentContextView fragmentContextView) {
        this.additionalContextView = fragmentContextView;
    }

    /* access modifiers changed from: private */
    public void openSharingLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
        if (sharingLocationInfo != null && (this.fragment.getParentActivity() instanceof LaunchActivity)) {
            LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
            launchActivity.switchToAccount(sharingLocationInfo.messageObject.currentAccount, true);
            LocationActivity locationActivity = new LocationActivity(2);
            locationActivity.setMessageObject(sharingLocationInfo.messageObject);
            locationActivity.setDelegate(new FragmentContextView$$ExternalSyntheticLambda12(sharingLocationInfo, sharingLocationInfo.messageObject.getDialogId()));
            launchActivity.lambda$runLinkRequest$47(locationActivity);
        }
    }

    @Keep
    public float getTopPadding() {
        return this.topPadding;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x008c, code lost:
        if (isPlayingVoice() == false) goto L_0x00a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x009d, code lost:
        if (r0.getId() != 0) goto L_0x00a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0010, code lost:
        if (org.telegram.messenger.LocationController.getLocationsCount() != 0) goto L_0x00a1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkVisibility() {
        /*
            r5 = this;
            boolean r0 = r5.isLocation
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x002a
            org.telegram.ui.ActionBar.BaseFragment r0 = r5.fragment
            boolean r3 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x0014
            int r0 = org.telegram.messenger.LocationController.getLocationsCount()
            if (r0 == 0) goto L_0x00a0
            goto L_0x00a1
        L_0x0014:
            int r0 = r0.getCurrentAccount()
            org.telegram.messenger.LocationController r0 = org.telegram.messenger.LocationController.getInstance(r0)
            org.telegram.ui.ActionBar.BaseFragment r2 = r5.fragment
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            long r2 = r2.getDialogId()
            boolean r2 = r0.isSharingLocation(r2)
            goto L_0x00a1
        L_0x002a:
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r0 == 0) goto L_0x0047
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r0 = r0.isHangingUp()
            if (r0 != 0) goto L_0x0047
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r0 = r0.getCallState()
            r3 = 15
            if (r0 == r3) goto L_0x0047
            goto L_0x00a1
        L_0x0047:
            org.telegram.ui.ActionBar.BaseFragment r0 = r5.fragment
            boolean r3 = r0 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x0066
            org.telegram.messenger.SendMessagesHelper r0 = r0.getSendMessagesHelper()
            org.telegram.ui.ActionBar.BaseFragment r3 = r5.fragment
            org.telegram.ui.ChatActivity r3 = (org.telegram.ui.ChatActivity) r3
            long r3 = r3.getDialogId()
            org.telegram.messenger.SendMessagesHelper$ImportingHistory r0 = r0.getImportingHistory(r3)
            if (r0 == 0) goto L_0x0066
            boolean r0 = r5.isPlayingVoice()
            if (r0 != 0) goto L_0x0066
            goto L_0x00a1
        L_0x0066:
            org.telegram.ui.ActionBar.BaseFragment r0 = r5.fragment
            boolean r3 = r0 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x008f
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.messenger.ChatObject$Call r0 = r0.getGroupCall()
            if (r0 == 0) goto L_0x008f
            org.telegram.ui.ActionBar.BaseFragment r0 = r5.fragment
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.messenger.ChatObject$Call r0 = r0.getGroupCall()
            boolean r0 = r0.shouldShowPanel()
            if (r0 == 0) goto L_0x008f
            boolean r0 = org.telegram.ui.Components.GroupCallPip.isShowing()
            if (r0 != 0) goto L_0x008f
            boolean r0 = r5.isPlayingVoice()
            if (r0 != 0) goto L_0x008f
            goto L_0x00a1
        L_0x008f:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r0 = r0.getPlayingMessageObject()
            if (r0 == 0) goto L_0x00a0
            int r0 = r0.getId()
            if (r0 == 0) goto L_0x00a0
            goto L_0x00a1
        L_0x00a0:
            r2 = 0
        L_0x00a1:
            if (r2 == 0) goto L_0x00a4
            goto L_0x00a6
        L_0x00a4:
            r1 = 8
        L_0x00a6:
            r5.setVisibility(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkVisibility():void");
    }

    @Keep
    public void setTopPadding(float f) {
        this.topPadding = f;
        if (this.fragment != null && getParent() != null) {
            View view = this.applyingView;
            if (view == null) {
                view = this.fragment.getFragmentView();
            }
            FragmentContextView fragmentContextView = this.additionalContextView;
            int dp = (fragmentContextView == null || fragmentContextView.getVisibility() != 0 || this.additionalContextView.getParent() == null) ? 0 : AndroidUtilities.dp((float) this.additionalContextView.getStyleHeight());
            if (view != null && getParent() != null) {
                view.setPadding(0, ((int) (getVisibility() == 0 ? this.topPadding : 0.0f)) + dp, 0, 0);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:60:0x01c9, code lost:
        if (r1.getGroupCall().call.rtmp_stream != false) goto L_0x01cd;
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0274  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0276  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0294  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0297  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02cf  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0337  */
    /* JADX WARNING: Removed duplicated region for block: B:158:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0243  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0245  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0261  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0265  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateStyle(int r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            int r2 = r0.currentStyle
            if (r2 != r1) goto L_0x0009
            return
        L_0x0009:
            r3 = 3
            r4 = 1
            if (r2 == r3) goto L_0x000f
            if (r2 != r4) goto L_0x0023
        L_0x000f:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable r2 = org.telegram.ui.ActionBar.Theme.getFragmentContextViewWavesDrawable()
            r2.removeParent(r0)
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x0023
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r2.unregisterStateListener(r0)
        L_0x0023:
            r0.currentStyle = r1
            android.widget.FrameLayout r2 = r0.frameLayout
            r5 = 4
            r6 = 0
            if (r1 == r5) goto L_0x002d
            r7 = 1
            goto L_0x002e
        L_0x002d:
            r7 = 0
        L_0x002e:
            r2.setWillNotDraw(r7)
            r2 = 0
            if (r1 == r5) goto L_0x0036
            r0.timeLayout = r2
        L_0x0036:
            org.telegram.ui.Components.AvatarsImageView r7 = r0.avatars
            r8 = 51
            if (r7 == 0) goto L_0x0050
            int r9 = r0.currentStyle
            r7.setStyle(r9)
            org.telegram.ui.Components.AvatarsImageView r7 = r0.avatars
            r9 = 108(0x6c, float:1.51E-43)
            int r10 = r19.getStyleHeight()
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r8)
            r7.setLayoutParams(r9)
        L_0x0050:
            android.widget.FrameLayout r7 = r0.frameLayout
            r9 = -1
            int r10 = r19.getStyleHeight()
            float r10 = (float) r10
            r11 = 51
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r7.setLayoutParams(r9)
            android.view.View r7 = r0.shadow
            r9 = -1
            r10 = 1073741824(0x40000000, float:2.0)
            int r13 = r19.getStyleHeight()
            float r13 = (float) r13
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r7.setLayoutParams(r9)
            float r7 = r0.topPadding
            r9 = 0
            int r10 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r10 <= 0) goto L_0x009b
            int r10 = r19.getStyleHeight()
            float r10 = (float) r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp2(r10)
            float r10 = (float) r10
            int r7 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r7 == 0) goto L_0x009b
            r19.updatePaddings()
            int r7 = r19.getStyleHeight()
            float r7 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp2(r7)
            float r7 = (float) r7
            r0.setTopPadding(r7)
        L_0x009b:
            r7 = 5
            r10 = 2131623967(0x7f0e001f, float:1.88751E38)
            java.lang.String r11 = "AccDescrClosePlayer"
            r12 = 1097859072(0x41700000, float:15.0)
            r13 = 19
            java.lang.String r14 = "inappPlayerTitle"
            r15 = 2
            java.lang.String r9 = "inappPlayerBackground"
            r2 = 8
            if (r1 != r7) goto L_0x0140
            android.view.View r1 = r0.selector
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r1.setBackground(r3)
            android.widget.FrameLayout r1 = r0.frameLayout
            int r3 = r0.getThemedColor(r9)
            r1.setBackgroundColor(r3)
            android.widget.FrameLayout r1 = r0.frameLayout
            r1.setTag(r9)
            r1 = 0
        L_0x00c6:
            if (r1 >= r15) goto L_0x00ed
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r3 = r0.titleTextView
            if (r1 != 0) goto L_0x00d1
            android.widget.TextView r3 = r3.getTextView()
            goto L_0x00d5
        L_0x00d1:
            android.widget.TextView r3 = r3.getNextTextView()
        L_0x00d5:
            if (r3 != 0) goto L_0x00d8
            goto L_0x00ea
        L_0x00d8:
            r3.setGravity(r13)
            int r5 = r0.getThemedColor(r14)
            r3.setTextColor(r5)
            android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r5)
            r3.setTextSize(r4, r12)
        L_0x00ea:
            int r1 = r1 + 1
            goto L_0x00c6
        L_0x00ed:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r1.setTag(r14)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            android.widget.TextView r1 = r0.joinButton
            r1.setVisibility(r2)
            android.widget.ImageView r1 = r0.closeButton
            r1.setVisibility(r2)
            android.widget.ImageView r1 = r0.playButton
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.setVisibility(r2)
            org.telegram.ui.Components.AvatarsImageView r1 = r0.avatars
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r1.setVisibility(r6)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r1.playAnimation()
            android.widget.ImageView r1 = r0.closeButton
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r1.setContentDescription(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            if (r1 == 0) goto L_0x012a
            r1.setVisibility(r2)
        L_0x012a:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r2 = -1
            r3 = 1108344832(0x42100000, float:36.0)
            r4 = 51
            r5 = 1108082688(0x420CLASSNAME, float:35.0)
            r6 = 0
            r7 = 1108344832(0x42100000, float:36.0)
            r8 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
            r1.setLayoutParams(r2)
            goto L_0x040f
        L_0x0140:
            if (r1 == 0) goto L_0x033c
            if (r1 != r15) goto L_0x0146
            goto L_0x033c
        L_0x0146:
            java.lang.String r7 = "fonts/rmedium.ttf"
            if (r1 != r5) goto L_0x020f
            android.view.View r1 = r0.selector
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r1.setBackground(r3)
            android.widget.FrameLayout r1 = r0.frameLayout
            int r3 = r0.getThemedColor(r9)
            r1.setBackgroundColor(r3)
            android.widget.FrameLayout r1 = r0.frameLayout
            r1.setTag(r9)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.setVisibility(r2)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.subtitleTextView
            r1.setVisibility(r6)
            r1 = 0
        L_0x016c:
            java.lang.String r3 = "inappPlayerPerformer"
            if (r1 >= r15) goto L_0x0197
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r5 = r0.titleTextView
            if (r1 != 0) goto L_0x0179
            android.widget.TextView r5 = r5.getTextView()
            goto L_0x017d
        L_0x0179:
            android.widget.TextView r5 = r5.getNextTextView()
        L_0x017d:
            if (r5 != 0) goto L_0x0180
            goto L_0x0194
        L_0x0180:
            r5.setGravity(r8)
            int r3 = r0.getThemedColor(r3)
            r5.setTextColor(r3)
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r5.setTypeface(r3)
            r5.setTextSize(r4, r12)
        L_0x0194:
            int r1 = r1 + 1
            goto L_0x016c
        L_0x0197:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r1.setTag(r3)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r1.setPadding(r6, r6, r6, r6)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r1.stopAnimation()
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.fragment
            boolean r3 = r1 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x01cc
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            org.telegram.messenger.ChatObject$Call r3 = r1.getGroupCall()
            if (r3 == 0) goto L_0x01cc
            org.telegram.messenger.ChatObject$Call r3 = r1.getGroupCall()
            org.telegram.tgnet.TLRPC$GroupCall r3 = r3.call
            if (r3 == 0) goto L_0x01cc
            org.telegram.messenger.ChatObject$Call r1 = r1.getGroupCall()
            org.telegram.tgnet.TLRPC$GroupCall r1 = r1.call
            boolean r1 = r1.rtmp_stream
            if (r1 == 0) goto L_0x01cc
            goto L_0x01cd
        L_0x01cc:
            r4 = 0
        L_0x01cd:
            org.telegram.ui.Components.AvatarsImageView r1 = r0.avatars
            if (r4 != 0) goto L_0x01d3
            r3 = 0
            goto L_0x01d5
        L_0x01d3:
            r3 = 8
        L_0x01d5:
            r1.setVisibility(r3)
            org.telegram.ui.Components.AvatarsImageView r1 = r0.avatars
            int r1 = r1.getVisibility()
            if (r1 == r2) goto L_0x01e4
            r0.updateAvatars(r6)
            goto L_0x01fc
        L_0x01e4:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r3 = 1108344832(0x42100000, float:36.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = -r4
            float r4 = (float) r4
            r1.setTranslationX(r4)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.subtitleTextView
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            float r3 = (float) r3
            r1.setTranslationX(r3)
        L_0x01fc:
            android.widget.ImageView r1 = r0.closeButton
            r1.setVisibility(r2)
            android.widget.ImageView r1 = r0.playButton
            r1.setVisibility(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            if (r1 == 0) goto L_0x040f
            r1.setVisibility(r2)
            goto L_0x040f
        L_0x020f:
            if (r1 == r4) goto L_0x0213
            if (r1 != r3) goto L_0x040f
        L_0x0213:
            android.view.View r5 = r0.selector
            r8 = 0
            r5.setBackground(r8)
            r19.updateCallTitle()
            org.telegram.ui.ActionBar.BaseFragment r5 = r0.fragment
            boolean r8 = r5 instanceof org.telegram.ui.ChatActivity
            if (r8 == 0) goto L_0x023e
            org.telegram.ui.ChatActivity r5 = (org.telegram.ui.ChatActivity) r5
            org.telegram.messenger.ChatObject$Call r8 = r5.getGroupCall()
            if (r8 == 0) goto L_0x023e
            org.telegram.messenger.ChatObject$Call r8 = r5.getGroupCall()
            org.telegram.tgnet.TLRPC$GroupCall r8 = r8.call
            if (r8 == 0) goto L_0x023e
            org.telegram.messenger.ChatObject$Call r5 = r5.getGroupCall()
            org.telegram.tgnet.TLRPC$GroupCall r5 = r5.call
            boolean r5 = r5.rtmp_stream
            if (r5 == 0) goto L_0x023e
            r5 = 1
            goto L_0x023f
        L_0x023e:
            r5 = 0
        L_0x023f:
            org.telegram.ui.Components.AvatarsImageView r8 = r0.avatars
            if (r5 != 0) goto L_0x0245
            r9 = 0
            goto L_0x0247
        L_0x0245:
            r9 = 8
        L_0x0247:
            r8.setVisibility(r9)
            if (r1 != r3) goto L_0x0259
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0259
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r1.registerStateListener(r0)
        L_0x0259:
            org.telegram.ui.Components.AvatarsImageView r1 = r0.avatars
            int r1 = r1.getVisibility()
            if (r1 == r2) goto L_0x0265
            r0.updateAvatars(r6)
            goto L_0x0270
        L_0x0265:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r3 = 0
            r1.setTranslationX(r3)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.subtitleTextView
            r1.setTranslationX(r3)
        L_0x0270:
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            if (r5 != 0) goto L_0x0276
            r3 = 0
            goto L_0x0278
        L_0x0276:
            r3 = 8
        L_0x0278:
            r1.setVisibility(r3)
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x028d
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r1 = r1.isMicMute()
            if (r1 == 0) goto L_0x028d
            r1 = 1
            goto L_0x028e
        L_0x028d:
            r1 = 0
        L_0x028e:
            r0.isMuted = r1
            org.telegram.ui.Components.RLottieDrawable r3 = r0.muteDrawable
            if (r1 == 0) goto L_0x0297
            r1 = 15
            goto L_0x0299
        L_0x0297:
            r1 = 29
        L_0x0299:
            r3.setCustomEndFrame(r1)
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            int r3 = r1.getCustomEndFrame()
            int r3 = r3 - r4
            r1.setCurrentFrame(r3, r6, r4)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.invalidate()
            android.widget.FrameLayout r1 = r0.frameLayout
            r3 = 0
            r1.setBackground(r3)
            android.widget.FrameLayout r1 = r0.frameLayout
            r1.setBackgroundColor(r6)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r1.stopAnimation()
            org.telegram.ui.Components.FragmentContextViewWavesDrawable r1 = org.telegram.ui.ActionBar.Theme.getFragmentContextViewWavesDrawable()
            r1.addParent(r0)
            r19.invalidate()
            r1 = 0
        L_0x02cb:
            java.lang.String r3 = "returnToCallText"
            if (r1 >= r15) goto L_0x02f8
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r5 = r0.titleTextView
            if (r1 != 0) goto L_0x02d8
            android.widget.TextView r5 = r5.getTextView()
            goto L_0x02dc
        L_0x02d8:
            android.widget.TextView r5 = r5.getNextTextView()
        L_0x02dc:
            if (r5 != 0) goto L_0x02df
            goto L_0x02f5
        L_0x02df:
            r5.setGravity(r13)
            int r3 = r0.getThemedColor(r3)
            r5.setTextColor(r3)
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r5.setTypeface(r3)
            r3 = 1096810496(0x41600000, float:14.0)
            r5.setTextSize(r4, r3)
        L_0x02f5:
            int r1 = r1 + 1
            goto L_0x02cb
        L_0x02f8:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r1.setTag(r3)
            android.widget.ImageView r1 = r0.closeButton
            r1.setVisibility(r2)
            android.widget.ImageView r1 = r0.playButton
            r1.setVisibility(r2)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.subtitleTextView
            r1.setVisibility(r2)
            android.widget.TextView r1 = r0.joinButton
            r1.setVisibility(r2)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r7 = -2
            r8 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r9 = 17
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 1073741824(0x40000000, float:2.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r1.setLayoutParams(r3)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r3 = 1121976320(0x42e00000, float:112.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setPadding(r4, r6, r3, r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            if (r1 == 0) goto L_0x040f
            r1.setVisibility(r2)
            goto L_0x040f
        L_0x033c:
            android.view.View r3 = r0.selector
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r3.setBackground(r5)
            android.widget.FrameLayout r3 = r0.frameLayout
            int r5 = r0.getThemedColor(r9)
            r3.setBackgroundColor(r5)
            android.widget.FrameLayout r3 = r0.frameLayout
            r3.setTag(r9)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r3 = r0.subtitleTextView
            r3.setVisibility(r2)
            android.widget.TextView r3 = r0.joinButton
            r3.setVisibility(r2)
            android.widget.ImageView r3 = r0.closeButton
            r3.setVisibility(r6)
            android.widget.ImageView r3 = r0.playButton
            r3.setVisibility(r6)
            org.telegram.ui.Components.RLottieImageView r3 = r0.muteButton
            r3.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r3 = r0.importingImageView
            r3.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r3 = r0.importingImageView
            r3.stopAnimation()
            org.telegram.ui.Components.AvatarsImageView r3 = r0.avatars
            r3.setVisibility(r2)
            r2 = 0
        L_0x037c:
            if (r2 >= r15) goto L_0x03a3
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r3 = r0.titleTextView
            if (r2 != 0) goto L_0x0387
            android.widget.TextView r3 = r3.getTextView()
            goto L_0x038b
        L_0x0387:
            android.widget.TextView r3 = r3.getNextTextView()
        L_0x038b:
            if (r3 != 0) goto L_0x038e
            goto L_0x03a0
        L_0x038e:
            r3.setGravity(r13)
            int r5 = r0.getThemedColor(r14)
            r3.setTextColor(r5)
            android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r5)
            r3.setTextSize(r4, r12)
        L_0x03a0:
            int r2 = r2 + 1
            goto L_0x037c
        L_0x03a3:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r2 = r0.titleTextView
            r2.setTag(r14)
            if (r1 != 0) goto L_0x03df
            android.widget.ImageView r1 = r0.playButton
            r12 = 36
            r13 = 1108344832(0x42100000, float:36.0)
            r14 = 51
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r1.setLayoutParams(r2)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r12 = -1
            r15 = 1108082688(0x420CLASSNAME, float:35.0)
            r17 = 1108344832(0x42100000, float:36.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r1.setLayoutParams(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            if (r1 == 0) goto L_0x03d5
            r1.setVisibility(r6)
        L_0x03d5:
            android.widget.ImageView r1 = r0.closeButton
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r1.setContentDescription(r2)
            goto L_0x040f
        L_0x03df:
            android.widget.ImageView r1 = r0.playButton
            r2 = 36
            r3 = 1108344832(0x42100000, float:36.0)
            r4 = 51
            r5 = 1090519040(0x41000000, float:8.0)
            r6 = 0
            r7 = 0
            r8 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
            r1.setLayoutParams(r2)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r0.titleTextView
            r2 = -1
            r5 = 1112276992(0x424CLASSNAME, float:51.0)
            r7 = 1108344832(0x42100000, float:36.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
            r1.setLayoutParams(r2)
            android.widget.ImageView r1 = r0.closeButton
            r2 = 2131624056(0x7f0e0078, float:1.887528E38)
            java.lang.String r3 = "AccDescrStopLiveLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
        L_0x040f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.updateStyle(int):void");
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        if (this.scheduleRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateScheduleTimeRunnable);
            this.scheduleRunnableScheduled = false;
        }
        this.visible = false;
        NotificationCenter.getInstance(this.account).onAnimationFinish(this.animationIndex);
        this.topPadding = 0.0f;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsCacheChanged);
        } else {
            for (int i = 0; i < 3; i++) {
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.groupCallTypingsUpdated);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.groupCallVisibilityChanged);
        }
        int i2 = this.currentStyle;
        if (i2 == 3 || i2 == 1) {
            Theme.getFragmentContextViewWavesDrawable().removeParent(this);
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        this.wasDraw = false;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        int i = 15;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
            FragmentContextView fragmentContextView = this.additionalContextView;
            if (fragmentContextView != null) {
                fragmentContextView.checkVisibility();
            }
            checkLiveLocation(true);
        } else {
            for (int i2 = 0; i2 < 3; i2++) {
                NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.groupCallTypingsUpdated);
                NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.historyImportProgressChanged);
            }
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.groupCallVisibilityChanged);
            FragmentContextView fragmentContextView2 = this.additionalContextView;
            if (fragmentContextView2 != null) {
                fragmentContextView2.checkVisibility();
            }
            if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isHangingUp() || VoIPService.getSharedInstance().getCallState() == 15 || GroupCallPip.isShowing()) {
                BaseFragment baseFragment = this.fragment;
                if (!(baseFragment instanceof ChatActivity) || baseFragment.getSendMessagesHelper().getImportingHistory(((ChatActivity) this.fragment).getDialogId()) == null || isPlayingVoice()) {
                    BaseFragment baseFragment2 = this.fragment;
                    if (!(baseFragment2 instanceof ChatActivity) || ((ChatActivity) baseFragment2).getGroupCall() == null || !((ChatActivity) this.fragment).getGroupCall().shouldShowPanel() || GroupCallPip.isShowing() || isPlayingVoice()) {
                        checkCall(true);
                        checkPlayer(true);
                        updatePlaybackButton();
                    } else {
                        checkCall(true);
                    }
                } else {
                    checkImport(true);
                }
            } else {
                checkCall(true);
            }
        }
        int i3 = this.currentStyle;
        if (i3 == 3 || i3 == 1) {
            Theme.getFragmentContextViewWavesDrawable().addParent(this);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (this.isMuted != z) {
                this.isMuted = z;
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                if (!z) {
                    i = 29;
                }
                rLottieDrawable.setCustomEndFrame(i);
                RLottieDrawable rLottieDrawable2 = this.muteDrawable;
                rLottieDrawable2.setCurrentFrame(rLottieDrawable2.getCustomEndFrame() - 1, false, true);
                this.muteButton.invalidate();
            }
        } else if (i3 == 4 && !this.scheduleRunnableScheduled) {
            this.scheduleRunnableScheduled = true;
            this.updateScheduleTimeRunnable.run();
        }
        if (this.visible && this.topPadding == 0.0f) {
            updatePaddings();
            setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
        }
        this.speakerAmplitude = 0.0f;
        this.micAmplitude = 0.0f;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, AndroidUtilities.dp2((float) (getStyleHeight() + 2)));
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        VoIPService sharedInstance;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        if (i == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
        } else if (i == NotificationCenter.liveLocationsCacheChanged) {
            if (this.fragment instanceof ChatActivity) {
                if (((ChatActivity) this.fragment).getDialogId() == objArr[0].longValue()) {
                    checkLocationString();
                }
            }
        } else if (i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.didEndCall) {
            int i3 = this.currentStyle;
            if (i3 == 1 || i3 == 3 || i3 == 4) {
                checkCall(false);
            }
            checkPlayer(false);
        } else {
            int i4 = NotificationCenter.didStartedCall;
            if (i == i4 || i == NotificationCenter.groupCallUpdated || i == NotificationCenter.groupCallVisibilityChanged) {
                checkCall(false);
                if (this.currentStyle == 3 && (sharedInstance = VoIPService.getSharedInstance()) != null && sharedInstance.groupCall != null) {
                    if (i == i4) {
                        sharedInstance.registerStateListener(this);
                    }
                    int callState = sharedInstance.getCallState();
                    if (callState != 1 && callState != 2 && callState != 6 && callState != 5 && (tLRPC$TL_groupCallParticipant = sharedInstance.groupCall.participants.get(sharedInstance.getSelfId())) != null && !tLRPC$TL_groupCallParticipant.can_self_unmute && tLRPC$TL_groupCallParticipant.muted && !ChatObject.canManageCalls(sharedInstance.getChat())) {
                        sharedInstance.setMicMute(true, false, false);
                        long uptimeMillis = SystemClock.uptimeMillis();
                        this.muteButton.dispatchTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0));
                    }
                }
            } else if (i == NotificationCenter.groupCallTypingsUpdated) {
                if (this.visible && this.currentStyle == 4) {
                    ChatObject.Call groupCall = ((ChatActivity) this.fragment).getGroupCall();
                    if (groupCall != null) {
                        if (groupCall.isScheduled()) {
                            this.subtitleTextView.setText(LocaleController.formatStartsTime((long) groupCall.call.schedule_date, 4), false);
                        } else {
                            TLRPC$GroupCall tLRPC$GroupCall = groupCall.call;
                            int i5 = tLRPC$GroupCall.participants_count;
                            if (i5 == 0) {
                                this.subtitleTextView.setText(LocaleController.getString(tLRPC$GroupCall.rtmp_stream ? NUM : NUM), false);
                            } else {
                                this.subtitleTextView.setText(LocaleController.formatPluralString(tLRPC$GroupCall.rtmp_stream ? "ViewersWatching" : "Participants", i5), false);
                            }
                        }
                    }
                    updateAvatars(true);
                }
            } else if (i == NotificationCenter.historyImportProgressChanged) {
                int i6 = this.currentStyle;
                if (i6 == 1 || i6 == 3 || i6 == 4) {
                    checkCall(false);
                }
                checkImport(false);
            } else if (i == NotificationCenter.messagePlayingSpeedChanged) {
                updatePlaybackButton();
            } else if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
                if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
                    this.micAmplitude = 0.0f;
                } else {
                    this.micAmplitude = Math.min(8500.0f, objArr[0].floatValue() * 4000.0f) / 8500.0f;
                }
                if (VoIPService.getSharedInstance() != null) {
                    Theme.getFragmentContextViewWavesDrawable().setAmplitude(Math.max(this.speakerAmplitude, this.micAmplitude));
                }
            } else if (i == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
                this.speakerAmplitude = Math.max(0.0f, Math.min((objArr[0].floatValue() * 15.0f) / 80.0f, 1.0f));
                if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
                    this.micAmplitude = 0.0f;
                }
                if (VoIPService.getSharedInstance() != null) {
                    Theme.getFragmentContextViewWavesDrawable().setAmplitude(Math.max(this.speakerAmplitude, this.micAmplitude));
                }
                this.avatars.invalidate();
            }
        }
    }

    public int getStyleHeight() {
        return this.currentStyle == 4 ? 48 : 36;
    }

    public boolean isCallTypeVisible() {
        int i = this.currentStyle;
        return (i == 1 || i == 3) && this.visible;
    }

    private void checkLiveLocation(boolean z) {
        boolean z2;
        String str;
        String str2;
        View fragmentView = this.fragment.getFragmentView();
        if (!z && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            z = true;
        }
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            z2 = LocationController.getLocationsCount() != 0;
        } else {
            z2 = LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        }
        if (!z2) {
            this.lastLocationSharingCount = -1;
            AndroidUtilities.cancelRunOnUIThread(this.checkLocationRunnable);
            if (this.visible) {
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                            FragmentContextView.this.setVisibility(8);
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
                return;
            }
            return;
        }
        updateStyle(2);
        this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), 1));
        if (z && this.topPadding == 0.0f) {
            setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
        }
        if (!this.visible) {
            if (!z) {
                AnimatorSet animatorSet4 = this.animatorSet;
                if (animatorSet4 != null) {
                    animatorSet4.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet5 = new AnimatorSet();
                this.animatorSet = animatorSet5;
                animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2((float) getStyleHeight())})});
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
            }
            this.visible = true;
            setVisibility(0);
        }
        if (this.fragment instanceof DialogsActivity) {
            String string = LocaleController.getString("LiveLocationContext", NUM);
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < 3; i++) {
                arrayList.addAll(LocationController.getInstance(i).sharingLocationsUI);
            }
            if (arrayList.size() == 1) {
                LocationController.SharingLocationInfo sharingLocationInfo = (LocationController.SharingLocationInfo) arrayList.get(0);
                long dialogId = sharingLocationInfo.messageObject.getDialogId();
                if (DialogObject.isUserDialog(dialogId)) {
                    str2 = UserObject.getFirstName(MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getUser(Long.valueOf(dialogId)));
                    str = LocaleController.getString("AttachLiveLocationIsSharing", NUM);
                } else {
                    TLRPC$Chat chat = MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getChat(Long.valueOf(-dialogId));
                    str2 = chat != null ? chat.title : "";
                    str = LocaleController.getString("AttachLiveLocationIsSharingChat", NUM);
                }
            } else {
                str2 = LocaleController.formatPluralString("Chats", arrayList.size());
                str = LocaleController.getString("AttachLiveLocationIsSharingChats", NUM);
            }
            String format = String.format(str, new Object[]{string, str2});
            int indexOf = format.indexOf(string);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(format);
            int i2 = 0;
            while (i2 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                TextView textView = i2 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                if (textView != null) {
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                }
                i2++;
            }
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, getThemedColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
            this.titleTextView.setText(spannableStringBuilder, false);
            return;
        }
        this.checkLocationRunnable.run();
        checkLocationString();
    }

    /* access modifiers changed from: private */
    public void checkLocationString() {
        int i;
        String str;
        BaseFragment baseFragment = this.fragment;
        if ((baseFragment instanceof ChatActivity) && this.titleTextView != null) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            long dialogId = chatActivity.getDialogId();
            int currentAccount = chatActivity.getCurrentAccount();
            ArrayList arrayList = LocationController.getInstance(currentAccount).locationsCache.get(dialogId);
            if (!this.firstLocationsLoaded) {
                LocationController.getInstance(currentAccount).loadLiveLocations(dialogId);
                this.firstLocationsLoaded = true;
            }
            TLRPC$User tLRPC$User = null;
            if (arrayList != null) {
                long clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                int currentTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                i = 0;
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i2);
                    TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                    if (tLRPC$MessageMedia != null && tLRPC$Message.date + tLRPC$MessageMedia.period > currentTime) {
                        long fromChatId = MessageObject.getFromChatId(tLRPC$Message);
                        if (tLRPC$User == null && fromChatId != clientUserId) {
                            tLRPC$User = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(fromChatId));
                        }
                        i++;
                    }
                }
            } else {
                i = 0;
            }
            if (this.lastLocationSharingCount != i) {
                this.lastLocationSharingCount = i;
                String string = LocaleController.getString("LiveLocationContext", NUM);
                if (i == 0) {
                    str = string;
                } else {
                    int i3 = i - 1;
                    if (LocationController.getInstance(currentAccount).isSharingLocation(dialogId)) {
                        if (i3 == 0) {
                            str = String.format("%1$s - %2$s", new Object[]{string, LocaleController.getString("ChatYourSelfName", NUM)});
                        } else if (i3 != 1 || tLRPC$User == null) {
                            str = String.format("%1$s - %2$s %3$s", new Object[]{string, LocaleController.getString("ChatYourSelfName", NUM), LocaleController.formatPluralString("AndOther", i3)});
                        } else {
                            str = String.format("%1$s - %2$s", new Object[]{string, LocaleController.formatString("SharingYouAndOtherName", NUM, UserObject.getFirstName(tLRPC$User))});
                        }
                    } else if (i3 != 0) {
                        str = String.format("%1$s - %2$s %3$s", new Object[]{string, UserObject.getFirstName(tLRPC$User), LocaleController.formatPluralString("AndOther", i3)});
                    } else {
                        str = String.format("%1$s - %2$s", new Object[]{string, UserObject.getFirstName(tLRPC$User)});
                    }
                }
                if (!str.equals(this.lastString)) {
                    this.lastString = str;
                    int indexOf = str.indexOf(string);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                    int i4 = 0;
                    while (i4 < 2) {
                        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                        TextView textView = i4 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                        if (textView != null) {
                            textView.setEllipsize(TextUtils.TruncateAt.END);
                        }
                        i4++;
                    }
                    if (indexOf >= 0) {
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, getThemedColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
                    }
                    this.titleTextView.setText(spannableStringBuilder, false);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkPlayer(boolean z) {
        SpannableStringBuilder spannableStringBuilder;
        boolean z2 = true;
        if (this.visible) {
            int i = this.currentStyle;
            if (i != 1 && i != 3) {
                if ((i == 4 || i == 5) && !isPlayingVoice()) {
                    return;
                }
            } else {
                return;
            }
        }
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        View fragmentView = this.fragment.getFragmentView();
        if (!z && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            z = true;
        }
        boolean z3 = this.visible;
        if (playingMessageObject == null || playingMessageObject.getId() == 0 || playingMessageObject.isVideo()) {
            this.lastMessageObject = null;
            boolean z4 = this.supportsCalls && VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().isHangingUp() && VoIPService.getSharedInstance().getCallState() != 15 && !GroupCallPip.isShowing();
            if (!isPlayingVoice() && !z4 && (this.fragment instanceof ChatActivity) && !GroupCallPip.isShowing()) {
                ChatObject.Call groupCall = ((ChatActivity) this.fragment).getGroupCall();
                z4 = groupCall != null && groupCall.shouldShowPanel();
            }
            if (z4) {
                checkCall(false);
            } else if (this.visible) {
                ActionBarMenuItem actionBarMenuItem = this.playbackSpeedButton;
                if (actionBarMenuItem != null && actionBarMenuItem.isSubMenuShowing()) {
                    this.playbackSpeedButton.toggleSubMenu();
                }
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                this.animationIndex = NotificationCenter.getInstance(this.account).setAnimationInProgress(this.animationIndex, (int[]) null);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(200);
                FragmentContextViewDelegate fragmentContextViewDelegate = this.delegate;
                if (fragmentContextViewDelegate != null) {
                    fragmentContextViewDelegate.onAnimation(true, false);
                }
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                            FragmentContextView.this.setVisibility(8);
                            if (FragmentContextView.this.delegate != null) {
                                FragmentContextView.this.delegate.onAnimation(false, false);
                            }
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                            if (FragmentContextView.this.checkCallAfterAnimation) {
                                FragmentContextView.this.checkCall(false);
                            } else if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                FragmentContextView.this.checkPlayer(false);
                            } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                FragmentContextView.this.checkImport(false);
                            }
                            boolean unused2 = FragmentContextView.this.checkCallAfterAnimation = false;
                            boolean unused3 = FragmentContextView.this.checkPlayerAfterAnimation = false;
                            boolean unused4 = FragmentContextView.this.checkImportAfterAnimation = false;
                        }
                    }
                });
                this.animatorSet.start();
            } else {
                setVisibility(8);
            }
        } else {
            int i2 = this.currentStyle;
            if (i2 == 0 || this.animatorSet == null || z) {
                updateStyle(0);
                if (z && this.topPadding == 0.0f) {
                    updatePaddings();
                    setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
                    FragmentContextViewDelegate fragmentContextViewDelegate2 = this.delegate;
                    if (fragmentContextViewDelegate2 != null) {
                        fragmentContextViewDelegate2.onAnimation(true, true);
                        this.delegate.onAnimation(false, true);
                    }
                }
                if (!this.visible) {
                    if (!z) {
                        AnimatorSet animatorSet4 = this.animatorSet;
                        if (animatorSet4 != null) {
                            animatorSet4.cancel();
                            this.animatorSet = null;
                        }
                        this.animationIndex = NotificationCenter.getInstance(this.account).setAnimationInProgress(this.animationIndex, (int[]) null);
                        this.animatorSet = new AnimatorSet();
                        FragmentContextView fragmentContextView = this.additionalContextView;
                        if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) getStyleHeight());
                        } else {
                            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) (getStyleHeight() + this.additionalContextView.getStyleHeight()));
                        }
                        FragmentContextViewDelegate fragmentContextViewDelegate3 = this.delegate;
                        if (fragmentContextViewDelegate3 != null) {
                            fragmentContextViewDelegate3.onAnimation(true, true);
                        }
                        this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2((float) getStyleHeight())})});
                        this.animatorSet.setDuration(200);
                        this.animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                                if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                                    if (FragmentContextView.this.delegate != null) {
                                        FragmentContextView.this.delegate.onAnimation(false, true);
                                    }
                                    AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                                    if (FragmentContextView.this.checkCallAfterAnimation) {
                                        FragmentContextView.this.checkCall(false);
                                    } else if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                        FragmentContextView.this.checkPlayer(false);
                                    } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                        FragmentContextView.this.checkImport(false);
                                    }
                                    boolean unused2 = FragmentContextView.this.checkCallAfterAnimation = false;
                                    boolean unused3 = FragmentContextView.this.checkPlayerAfterAnimation = false;
                                    boolean unused4 = FragmentContextView.this.checkImportAfterAnimation = false;
                                }
                            }
                        });
                        this.animatorSet.start();
                    }
                    this.visible = true;
                    setVisibility(0);
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    this.playPauseDrawable.setPause(false, !z);
                    this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
                } else {
                    this.playPauseDrawable.setPause(true, !z);
                    this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
                }
                if (this.lastMessageObject != playingMessageObject || i2 != 0) {
                    this.lastMessageObject = playingMessageObject;
                    if (playingMessageObject.isVoice() || this.lastMessageObject.isRoundVideo()) {
                        this.isMusic = false;
                        ActionBarMenuItem actionBarMenuItem2 = this.playbackSpeedButton;
                        if (actionBarMenuItem2 != null) {
                            actionBarMenuItem2.setAlpha(1.0f);
                            this.playbackSpeedButton.setEnabled(true);
                        }
                        this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                        spannableStringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{playingMessageObject.getMusicAuthor(), playingMessageObject.getMusicTitle()}));
                        int i3 = 0;
                        while (i3 < 2) {
                            AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                            TextView textView = i3 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                            if (textView != null) {
                                textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                            }
                            i3++;
                        }
                        updatePlaybackButton();
                    } else {
                        this.isMusic = true;
                        if (this.playbackSpeedButton == null) {
                            this.titleTextView.setPadding(0, 0, 0, 0);
                        } else if (playingMessageObject.getDuration() >= 600) {
                            this.playbackSpeedButton.setAlpha(1.0f);
                            this.playbackSpeedButton.setEnabled(true);
                            this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                            updatePlaybackButton();
                        } else {
                            this.playbackSpeedButton.setAlpha(0.0f);
                            this.playbackSpeedButton.setEnabled(false);
                            this.titleTextView.setPadding(0, 0, 0, 0);
                        }
                        spannableStringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{playingMessageObject.getMusicAuthor(), playingMessageObject.getMusicTitle()}));
                        int i4 = 0;
                        while (i4 < 2) {
                            AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = this.titleTextView;
                            TextView textView2 = i4 == 0 ? clippingTextViewSwitcher2.getTextView() : clippingTextViewSwitcher2.getNextTextView();
                            if (textView2 != null) {
                                textView2.setEllipsize(TextUtils.TruncateAt.END);
                            }
                            i4++;
                        }
                    }
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, getThemedColor("inappPlayerPerformer")), 0, playingMessageObject.getMusicAuthor().length(), 18);
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher3 = this.titleTextView;
                    if (z || !z3 || !this.isMusic) {
                        z2 = false;
                    }
                    clippingTextViewSwitcher3.setText(spannableStringBuilder, z2);
                    return;
                }
                return;
            }
            this.checkPlayerAfterAnimation = true;
        }
    }

    public void checkImport(boolean z) {
        int i;
        BaseFragment baseFragment = this.fragment;
        if (!(baseFragment instanceof ChatActivity)) {
            return;
        }
        if (!this.visible || !((i = this.currentStyle) == 1 || i == 3)) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            SendMessagesHelper.ImportingHistory importingHistory = chatActivity.getSendMessagesHelper().getImportingHistory(chatActivity.getDialogId());
            View fragmentView = this.fragment.getFragmentView();
            if (!z && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
                z = true;
            }
            Dialog visibleDialog = chatActivity.getVisibleDialog();
            if ((isPlayingVoice() || chatActivity.shouldShowImport() || ((visibleDialog instanceof ImportingAlert) && !((ImportingAlert) visibleDialog).isDismissed())) && importingHistory != null) {
                importingHistory = null;
            }
            if (importingHistory == null) {
                if (!this.visible || ((!z || this.currentStyle != -1) && this.currentStyle != 5)) {
                    int i2 = this.currentStyle;
                    if (i2 == -1 || i2 == 5) {
                        this.visible = false;
                        setVisibility(8);
                        return;
                    }
                    return;
                }
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                final int i3 = this.account;
                this.animationIndex = NotificationCenter.getInstance(i3).setAnimationInProgress(this.animationIndex, (int[]) null);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(220);
                this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        NotificationCenter.getInstance(i3).onAnimationFinish(FragmentContextView.this.animationIndex);
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                            FragmentContextView.this.setVisibility(8);
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                            if (FragmentContextView.this.checkCallAfterAnimation) {
                                FragmentContextView.this.checkCall(false);
                            } else if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                FragmentContextView.this.checkPlayer(false);
                            } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                FragmentContextView.this.checkImport(false);
                            }
                            boolean unused2 = FragmentContextView.this.checkCallAfterAnimation = false;
                            boolean unused3 = FragmentContextView.this.checkPlayerAfterAnimation = false;
                            boolean unused4 = FragmentContextView.this.checkImportAfterAnimation = false;
                        }
                    }
                });
                this.animatorSet.start();
            } else if (this.currentStyle == 5 || this.animatorSet == null || z) {
                updateStyle(5);
                if (z && this.topPadding == 0.0f) {
                    updatePaddings();
                    setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
                    FragmentContextViewDelegate fragmentContextViewDelegate = this.delegate;
                    if (fragmentContextViewDelegate != null) {
                        fragmentContextViewDelegate.onAnimation(true, true);
                        this.delegate.onAnimation(false, true);
                    }
                }
                if (!this.visible) {
                    if (!z) {
                        AnimatorSet animatorSet4 = this.animatorSet;
                        if (animatorSet4 != null) {
                            animatorSet4.cancel();
                            this.animatorSet = null;
                        }
                        this.animationIndex = NotificationCenter.getInstance(this.account).setAnimationInProgress(this.animationIndex, (int[]) null);
                        this.animatorSet = new AnimatorSet();
                        FragmentContextView fragmentContextView = this.additionalContextView;
                        if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) getStyleHeight());
                        } else {
                            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) (getStyleHeight() + this.additionalContextView.getStyleHeight()));
                        }
                        FragmentContextViewDelegate fragmentContextViewDelegate2 = this.delegate;
                        if (fragmentContextViewDelegate2 != null) {
                            fragmentContextViewDelegate2.onAnimation(true, true);
                        }
                        this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2((float) getStyleHeight())})});
                        this.animatorSet.setDuration(200);
                        this.animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                                if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                                    if (FragmentContextView.this.delegate != null) {
                                        FragmentContextView.this.delegate.onAnimation(false, true);
                                    }
                                    AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                                    if (FragmentContextView.this.checkCallAfterAnimation) {
                                        FragmentContextView.this.checkCall(false);
                                    } else if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                        FragmentContextView.this.checkPlayer(false);
                                    } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                        FragmentContextView.this.checkImport(false);
                                    }
                                    boolean unused2 = FragmentContextView.this.checkCallAfterAnimation = false;
                                    boolean unused3 = FragmentContextView.this.checkPlayerAfterAnimation = false;
                                    boolean unused4 = FragmentContextView.this.checkImportAfterAnimation = false;
                                }
                            }
                        });
                        this.animatorSet.start();
                    }
                    this.visible = true;
                    setVisibility(0);
                }
                int i4 = this.currentProgress;
                int i5 = importingHistory.uploadProgress;
                if (i4 != i5) {
                    this.currentProgress = i5;
                    this.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ImportUploading", NUM, Integer.valueOf(i5))), false);
                }
            } else {
                this.checkImportAfterAnimation = true;
            }
        }
    }

    private boolean isPlayingVoice() {
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        return playingMessageObject != null && playingMessageObject.isVoice();
    }

    /* JADX WARNING: Removed duplicated region for block: B:51:0x0096  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0144  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkCall(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r2 = r0.visible
            if (r2 == 0) goto L_0x0018
            int r2 = r0.currentStyle
            r3 = 5
            if (r2 != r3) goto L_0x0018
            if (r1 == 0) goto L_0x0017
            boolean r2 = r1.isHangingUp()
            if (r2 == 0) goto L_0x0018
        L_0x0017:
            return
        L_0x0018:
            org.telegram.ui.ActionBar.BaseFragment r2 = r0.fragment
            android.view.View r2 = r2.getFragmentView()
            r3 = 1
            if (r17 != 0) goto L_0x0037
            if (r2 == 0) goto L_0x0037
            android.view.ViewParent r4 = r2.getParent()
            if (r4 == 0) goto L_0x0035
            android.view.ViewParent r2 = r2.getParent()
            android.view.View r2 = (android.view.View) r2
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x0037
        L_0x0035:
            r2 = 1
            goto L_0x0039
        L_0x0037:
            r2 = r17
        L_0x0039:
            boolean r4 = org.telegram.ui.Components.GroupCallPip.isShowing()
            r5 = 0
            if (r4 == 0) goto L_0x0043
            r4 = 0
        L_0x0041:
            r6 = 0
            goto L_0x0089
        L_0x0043:
            boolean r4 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r4 != 0) goto L_0x0055
            boolean r4 = r0.supportsCalls
            if (r4 == 0) goto L_0x0055
            if (r1 == 0) goto L_0x0055
            boolean r4 = r1.isHangingUp()
            if (r4 != 0) goto L_0x0055
            r4 = 1
            goto L_0x0056
        L_0x0055:
            r4 = 0
        L_0x0056:
            if (r1 == 0) goto L_0x0063
            org.telegram.messenger.ChatObject$Call r6 = r1.groupCall
            if (r6 == 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$GroupCall r6 = r6.call
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_groupCallDiscarded
            if (r6 == 0) goto L_0x0063
            r4 = 0
        L_0x0063:
            boolean r6 = r16.isPlayingVoice()
            if (r6 != 0) goto L_0x0041
            boolean r6 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r6 != 0) goto L_0x0041
            boolean r6 = r0.supportsCalls
            if (r6 == 0) goto L_0x0041
            if (r4 != 0) goto L_0x0041
            org.telegram.ui.ActionBar.BaseFragment r6 = r0.fragment
            boolean r7 = r6 instanceof org.telegram.ui.ChatActivity
            if (r7 == 0) goto L_0x0041
            org.telegram.ui.ChatActivity r6 = (org.telegram.ui.ChatActivity) r6
            org.telegram.messenger.ChatObject$Call r6 = r6.getGroupCall()
            if (r6 == 0) goto L_0x0041
            boolean r6 = r6.shouldShowPanel()
            if (r6 == 0) goto L_0x0041
            r4 = 1
            r6 = 1
        L_0x0089:
            r7 = 220(0xdc, double:1.087E-321)
            r9 = 0
            java.lang.String r10 = "topPadding"
            r11 = 8
            r12 = -1
            r13 = 3
            r14 = 0
            r15 = 4
            if (r4 != 0) goto L_0x0144
            boolean r1 = r0.visible
            if (r1 == 0) goto L_0x0102
            if (r2 == 0) goto L_0x00a0
            int r4 = r0.currentStyle
            if (r4 == r12) goto L_0x00a8
        L_0x00a0:
            int r4 = r0.currentStyle
            if (r4 == r15) goto L_0x00a8
            if (r4 == r13) goto L_0x00a8
            if (r4 != r3) goto L_0x0102
        L_0x00a8:
            r0.visible = r5
            if (r2 == 0) goto L_0x00b9
            int r1 = r16.getVisibility()
            if (r1 == r11) goto L_0x00b5
            r0.setVisibility(r11)
        L_0x00b5:
            r0.setTopPadding(r9)
            goto L_0x0113
        L_0x00b9:
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x00c2
            r1.cancel()
            r0.animatorSet = r14
        L_0x00c2:
            int r1 = r0.account
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r6 = r0.animationIndex
            int r4 = r4.setAnimationInProgress(r6, r14)
            r0.animationIndex = r4
            android.animation.AnimatorSet r4 = new android.animation.AnimatorSet
            r4.<init>()
            r0.animatorSet = r4
            android.animation.Animator[] r6 = new android.animation.Animator[r3]
            float[] r3 = new float[r3]
            r3[r5] = r9
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r0, r10, r3)
            r6[r5] = r3
            r4.playTogether(r6)
            android.animation.AnimatorSet r3 = r0.animatorSet
            r3.setDuration(r7)
            android.animation.AnimatorSet r3 = r0.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r3.setInterpolator(r4)
            android.animation.AnimatorSet r3 = r0.animatorSet
            org.telegram.ui.Components.FragmentContextView$13 r4 = new org.telegram.ui.Components.FragmentContextView$13
            r4.<init>(r1)
            r3.addListener(r4)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
            goto L_0x0113
        L_0x0102:
            if (r1 == 0) goto L_0x0113
            int r1 = r0.currentStyle
            if (r1 == r12) goto L_0x010e
            if (r1 == r15) goto L_0x010e
            if (r1 == r13) goto L_0x010e
            if (r1 != r3) goto L_0x0113
        L_0x010e:
            r0.visible = r5
            r0.setVisibility(r11)
        L_0x0113:
            if (r2 == 0) goto L_0x0396
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.fragment
            boolean r2 = r1 instanceof org.telegram.ui.ChatActivity
            if (r2 == 0) goto L_0x0396
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            boolean r1 = r1.openedWithLivestream()
            if (r1 == 0) goto L_0x0396
            boolean r1 = org.telegram.ui.Components.GroupCallPip.isShowing()
            if (r1 != 0) goto L_0x0396
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.fragment
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r1)
            r2 = 2131558474(0x7f0d004a, float:1.8742265E38)
            r3 = 2131626079(0x7f0e085f, float:1.8879384E38)
            java.lang.String r4 = "InviteExpired"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.Bulletin r1 = r1.createSimpleBulletin(r2, r3)
            r1.show()
            goto L_0x0396
        L_0x0144:
            if (r6 == 0) goto L_0x0148
            r4 = 4
            goto L_0x014f
        L_0x0148:
            org.telegram.messenger.ChatObject$Call r4 = r1.groupCall
            if (r4 == 0) goto L_0x014e
            r4 = 3
            goto L_0x014f
        L_0x014e:
            r4 = 1
        L_0x014f:
            int r13 = r0.currentStyle
            if (r4 == r13) goto L_0x015c
            android.animation.AnimatorSet r11 = r0.animatorSet
            if (r11 == 0) goto L_0x015c
            if (r2 != 0) goto L_0x015c
            r0.checkCallAfterAnimation = r3
            return
        L_0x015c:
            if (r4 == r13) goto L_0x01ad
            boolean r4 = r0.visible
            if (r4 == 0) goto L_0x01ad
            if (r2 != 0) goto L_0x01ad
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x016d
            r1.cancel()
            r0.animatorSet = r14
        L_0x016d:
            int r1 = r0.account
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r4 = r0.animationIndex
            int r2 = r2.setAnimationInProgress(r4, r14)
            r0.animationIndex = r2
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.animatorSet = r2
            android.animation.Animator[] r4 = new android.animation.Animator[r3]
            float[] r3 = new float[r3]
            r3[r5] = r9
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r0, r10, r3)
            r4[r5] = r3
            r2.playTogether(r4)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.setDuration(r7)
            android.animation.AnimatorSet r2 = r0.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            android.animation.AnimatorSet r2 = r0.animatorSet
            org.telegram.ui.Components.FragmentContextView$14 r3 = new org.telegram.ui.Components.FragmentContextView$14
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
            return
        L_0x01ad:
            if (r6 == 0) goto L_0x02cc
            if (r13 != r15) goto L_0x01b7
            boolean r1 = r0.visible
            if (r1 == 0) goto L_0x01b7
            r1 = 1
            goto L_0x01b8
        L_0x01b7:
            r1 = 0
        L_0x01b8:
            r0.updateStyle(r15)
            org.telegram.ui.ActionBar.BaseFragment r4 = r0.fragment
            org.telegram.ui.ChatActivity r4 = (org.telegram.ui.ChatActivity) r4
            org.telegram.messenger.ChatObject$Call r4 = r4.getGroupCall()
            org.telegram.ui.ActionBar.BaseFragment r6 = r0.fragment
            org.telegram.ui.ChatActivity r6 = (org.telegram.ui.ChatActivity) r6
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getCurrentChat()
            boolean r9 = r4.isScheduled()
            if (r9 == 0) goto L_0x025f
            android.graphics.Paint r9 = r0.gradientPaint
            if (r9 != 0) goto L_0x0207
            android.text.TextPaint r9 = new android.text.TextPaint
            r9.<init>(r3)
            r0.gradientTextPaint = r9
            r9.setColor(r12)
            android.text.TextPaint r9 = r0.gradientTextPaint
            r11 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r9.setTextSize(r11)
            android.text.TextPaint r9 = r0.gradientTextPaint
            java.lang.String r11 = "fonts/rmedium.ttf"
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
            r9.setTypeface(r11)
            android.graphics.Paint r9 = new android.graphics.Paint
            r9.<init>(r3)
            r0.gradientPaint = r9
            r9.setColor(r12)
            android.graphics.Matrix r9 = new android.graphics.Matrix
            r9.<init>()
            r0.matrix = r9
        L_0x0207:
            android.widget.TextView r9 = r0.joinButton
            r11 = 8
            r9.setVisibility(r11)
            org.telegram.tgnet.TLRPC$GroupCall r9 = r4.call
            java.lang.String r9 = r9.title
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 != 0) goto L_0x0222
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r6 = r0.titleTextView
            org.telegram.tgnet.TLRPC$GroupCall r9 = r4.call
            java.lang.String r9 = r9.title
            r6.setText(r9, r5)
            goto L_0x0245
        L_0x0222:
            boolean r6 = org.telegram.messenger.ChatObject.isChannelOrGiga(r6)
            if (r6 == 0) goto L_0x0237
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r6 = r0.titleTextView
            r9 = 2131628647(0x7f0e1267, float:1.8884593E38)
            java.lang.String r11 = "VoipChannelScheduledVoiceChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r6.setText(r9, r5)
            goto L_0x0245
        L_0x0237:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r6 = r0.titleTextView
            r9 = 2131628756(0x7f0e12d4, float:1.8884814E38)
            java.lang.String r11 = "VoipGroupScheduledVoiceChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r6.setText(r9, r5)
        L_0x0245:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r6 = r0.subtitleTextView
            org.telegram.tgnet.TLRPC$GroupCall r4 = r4.call
            int r4 = r4.schedule_date
            long r11 = (long) r4
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatStartsTime(r11, r15)
            r6.setText(r4, r5)
            boolean r4 = r0.scheduleRunnableScheduled
            if (r4 != 0) goto L_0x02bb
            r0.scheduleRunnableScheduled = r3
            java.lang.Runnable r4 = r0.updateScheduleTimeRunnable
            r4.run()
            goto L_0x02bb
        L_0x025f:
            r0.timeLayout = r14
            android.widget.TextView r9 = r0.joinButton
            r9.setVisibility(r5)
            boolean r6 = org.telegram.messenger.ChatObject.isChannelOrGiga(r6)
            if (r6 == 0) goto L_0x027b
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r6 = r0.titleTextView
            r9 = 2131628660(0x7f0e1274, float:1.8884619E38)
            java.lang.String r11 = "VoipChannelVoiceChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r6.setText(r9, r5)
            goto L_0x0289
        L_0x027b:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r6 = r0.titleTextView
            r9 = 2131628788(0x7f0e12f4, float:1.8884879E38)
            java.lang.String r11 = "VoipGroupVoiceChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r6.setText(r9, r5)
        L_0x0289:
            org.telegram.tgnet.TLRPC$GroupCall r4 = r4.call
            int r6 = r4.participants_count
            if (r6 != 0) goto L_0x02a4
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r6 = r0.subtitleTextView
            boolean r4 = r4.rtmp_stream
            if (r4 == 0) goto L_0x0299
            r4 = 2131628589(0x7f0e122d, float:1.8884475E38)
            goto L_0x029c
        L_0x0299:
            r4 = 2131626383(0x7f0e098f, float:1.888E38)
        L_0x029c:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString((int) r4)
            r6.setText(r4, r5)
            goto L_0x02b6
        L_0x02a4:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r9 = r0.subtitleTextView
            boolean r4 = r4.rtmp_stream
            if (r4 == 0) goto L_0x02ad
            java.lang.String r4 = "ViewersWatching"
            goto L_0x02af
        L_0x02ad:
            java.lang.String r4 = "Participants"
        L_0x02af:
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6)
            r9.setText(r4, r5)
        L_0x02b6:
            android.widget.FrameLayout r4 = r0.frameLayout
            r4.invalidate()
        L_0x02bb:
            org.telegram.ui.Components.AvatarsImageView r4 = r0.avatars
            org.telegram.ui.Components.AvatarsDarawable r4 = r4.avatarsDarawable
            boolean r4 = r4.wasDraw
            if (r4 == 0) goto L_0x02c7
            if (r1 == 0) goto L_0x02c7
            r1 = 1
            goto L_0x02c8
        L_0x02c7:
            r1 = 0
        L_0x02c8:
            r0.updateAvatars(r1)
            goto L_0x02ea
        L_0x02cc:
            if (r1 == 0) goto L_0x02df
            org.telegram.messenger.ChatObject$Call r1 = r1.groupCall
            if (r1 == 0) goto L_0x02df
            r1 = 3
            if (r13 != r1) goto L_0x02d7
            r4 = 1
            goto L_0x02d8
        L_0x02d7:
            r4 = 0
        L_0x02d8:
            r0.updateAvatars(r4)
            r0.updateStyle(r1)
            goto L_0x02ea
        L_0x02df:
            if (r13 != r3) goto L_0x02e3
            r1 = 1
            goto L_0x02e4
        L_0x02e3:
            r1 = 0
        L_0x02e4:
            r0.updateAvatars(r1)
            r0.updateStyle(r3)
        L_0x02ea:
            boolean r1 = r0.visible
            if (r1 != 0) goto L_0x0396
            if (r2 != 0) goto L_0x0381
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x02f9
            r1.cancel()
            r0.animatorSet = r14
        L_0x02f9:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r0.animatorSet = r1
            org.telegram.ui.Components.FragmentContextView r1 = r0.additionalContextView
            if (r1 == 0) goto L_0x0324
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0324
            android.view.ViewGroup$LayoutParams r1 = r16.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = r16.getStyleHeight()
            org.telegram.ui.Components.FragmentContextView r4 = r0.additionalContextView
            int r4 = r4.getStyleHeight()
            int r2 = r2 + r4
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            r1.topMargin = r2
            goto L_0x0336
        L_0x0324:
            android.view.ViewGroup$LayoutParams r1 = r16.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = r16.getStyleHeight()
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            r1.topMargin = r2
        L_0x0336:
            int r1 = r0.account
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r4 = r0.animationIndex
            int[] r6 = new int[r3]
            int r9 = org.telegram.messenger.NotificationCenter.messagesDidLoad
            r6[r5] = r9
            int r2 = r2.setAnimationInProgress(r4, r6)
            r0.animationIndex = r2
            android.animation.AnimatorSet r2 = r0.animatorSet
            android.animation.Animator[] r4 = new android.animation.Animator[r3]
            float[] r6 = new float[r3]
            int r9 = r16.getStyleHeight()
            float r9 = (float) r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp2(r9)
            float r9 = (float) r9
            r6[r5] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r0, r10, r6)
            r4[r5] = r6
            r2.playTogether(r4)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.setDuration(r7)
            android.animation.AnimatorSet r2 = r0.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r4)
            android.animation.AnimatorSet r2 = r0.animatorSet
            org.telegram.ui.Components.FragmentContextView$15 r4 = new org.telegram.ui.Components.FragmentContextView$15
            r4.<init>(r1)
            r2.addListener(r4)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
            goto L_0x0391
        L_0x0381:
            r16.updatePaddings()
            int r1 = r16.getStyleHeight()
            float r1 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp2(r1)
            float r1 = (float) r1
            r0.setTopPadding(r1)
        L_0x0391:
            r0.visible = r3
            r0.setVisibility(r5)
        L_0x0396:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkCall(boolean):void");
    }

    private void updateAvatars(boolean z) {
        int i;
        ChatObject.Call call;
        TLRPC$User tLRPC$User;
        int i2;
        ValueAnimator valueAnimator;
        if (!z && (valueAnimator = this.avatars.avatarsDarawable.transitionProgressAnimator) != null) {
            valueAnimator.cancel();
            this.avatars.avatarsDarawable.transitionProgressAnimator = null;
        }
        AvatarsImageView avatarsImageView = this.avatars;
        if (avatarsImageView.avatarsDarawable.transitionProgressAnimator == null) {
            if (this.currentStyle == 4) {
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    ChatActivity chatActivity = (ChatActivity) baseFragment;
                    call = chatActivity.getGroupCall();
                    i2 = chatActivity.getCurrentAccount();
                } else {
                    i2 = this.account;
                    call = null;
                }
                i = i2;
                tLRPC$User = null;
            } else if (VoIPService.getSharedInstance() != null) {
                call = VoIPService.getSharedInstance().groupCall;
                tLRPC$User = this.fragment instanceof ChatActivity ? null : VoIPService.getSharedInstance().getUser();
                i = VoIPService.getSharedInstance().getAccount();
            } else {
                call = null;
                i = this.account;
                tLRPC$User = null;
            }
            int i3 = 0;
            if (call != null) {
                int size = call.sortedParticipants.size();
                for (int i4 = 0; i4 < 3; i4++) {
                    if (i4 < size) {
                        this.avatars.setObject(i4, i, call.sortedParticipants.get(i4));
                    } else {
                        this.avatars.setObject(i4, i, (TLObject) null);
                    }
                }
            } else if (tLRPC$User != null) {
                this.avatars.setObject(0, i, tLRPC$User);
                for (int i5 = 1; i5 < 3; i5++) {
                    this.avatars.setObject(i5, i, (TLObject) null);
                }
            } else {
                for (int i6 = 0; i6 < 3; i6++) {
                    this.avatars.setObject(i6, i, (TLObject) null);
                }
            }
            this.avatars.commitTransition(z);
            if (this.currentStyle == 4 && call != null) {
                if (!call.call.rtmp_stream) {
                    i3 = Math.min(3, call.sortedParticipants.size());
                }
                int i7 = 10;
                if (i3 != 0) {
                    i7 = 10 + ((i3 - 1) * 24) + 10 + 32;
                }
                if (z) {
                    int i8 = ((FrameLayout.LayoutParams) this.titleTextView.getLayoutParams()).leftMargin;
                    float f = (float) i7;
                    if (AndroidUtilities.dp(f) != i8) {
                        float translationX = (this.titleTextView.getTranslationX() + ((float) i8)) - ((float) AndroidUtilities.dp(f));
                        this.titleTextView.setTranslationX(translationX);
                        this.subtitleTextView.setTranslationX(translationX);
                        ViewPropertyAnimator duration = this.titleTextView.animate().translationX(0.0f).setDuration(220);
                        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                        duration.setInterpolator(cubicBezierInterpolator);
                        this.subtitleTextView.animate().translationX(0.0f).setDuration(220).setInterpolator(cubicBezierInterpolator);
                    }
                } else {
                    this.titleTextView.animate().cancel();
                    this.subtitleTextView.animate().cancel();
                    this.titleTextView.setTranslationX(0.0f);
                    this.subtitleTextView.setTranslationX(0.0f);
                }
                float f2 = (float) i7;
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, f2, 5.0f, call.isScheduled() ? 90.0f : 36.0f, 0.0f));
                this.subtitleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, f2, 25.0f, call.isScheduled() ? 90.0f : 36.0f, 0.0f));
                return;
            }
            return;
        }
        avatarsImageView.updateAfterTransitionEnd();
    }

    public void setCollapseTransition(boolean z, float f, float f2) {
        this.collapseTransition = z;
        this.extraHeight = f;
        this.collapseProgress = f2;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (!this.drawOverlay || getVisibility() == 0) {
            boolean z = false;
            int i = this.currentStyle;
            if ((i == 3 || i == 1) && this.drawOverlay) {
                if (GroupCallActivity.groupCallInstance == null) {
                    int state = Theme.getFragmentContextViewWavesDrawable().getState();
                }
                Theme.getFragmentContextViewWavesDrawable().updateState(this.wasDraw);
                float dp = this.topPadding / ((float) AndroidUtilities.dp((float) getStyleHeight()));
                if (this.collapseTransition) {
                    Theme.getFragmentContextViewWavesDrawable().draw(0.0f, (((float) AndroidUtilities.dp((float) getStyleHeight())) - this.topPadding) + this.extraHeight, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)), canvas, (FragmentContextView) null, Math.min(dp, 1.0f - this.collapseProgress));
                } else {
                    Theme.getFragmentContextViewWavesDrawable().draw(0.0f, ((float) AndroidUtilities.dp((float) getStyleHeight())) - this.topPadding, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)), canvas, this, dp);
                }
                float dp2 = ((float) AndroidUtilities.dp((float) getStyleHeight())) - this.topPadding;
                if (this.collapseTransition) {
                    dp2 += this.extraHeight;
                }
                if (dp2 <= ((float) getMeasuredHeight())) {
                    canvas.save();
                    canvas.clipRect(0.0f, dp2, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    invalidate();
                    z = true;
                } else {
                    return;
                }
            } else {
                Canvas canvas2 = canvas;
            }
            super.dispatchDraw(canvas);
            if (z) {
                canvas.restore();
            }
            this.wasDraw = true;
        }
    }

    public void setDrawOverlay(boolean z) {
        this.drawOverlay = z;
    }

    public void invalidate() {
        super.invalidate();
        int i = this.currentStyle;
        if ((i == 3 || i == 1) && getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean isCallStyle() {
        int i = this.currentStyle;
        return i == 3 || i == 1;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        updatePaddings();
        setTopPadding(this.topPadding);
        if (i == 8) {
            this.wasDraw = false;
        }
    }

    private void updatePaddings() {
        int i = 0;
        if (getVisibility() == 0) {
            i = 0 - AndroidUtilities.dp((float) getStyleHeight());
        }
        FragmentContextView fragmentContextView = this.additionalContextView;
        if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = i;
            return;
        }
        int dp = i - AndroidUtilities.dp((float) this.additionalContextView.getStyleHeight());
        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = dp;
        ((FrameLayout.LayoutParams) this.additionalContextView.getLayoutParams()).topMargin = dp;
    }

    public void onStateChanged(int i) {
        updateCallTitle();
    }

    private void updateCallTitle() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            int i = this.currentStyle;
            if (i == 1 || i == 3) {
                int callState = sharedInstance.getCallState();
                if (!sharedInstance.isSwitchingStream() && (callState == 1 || callState == 2 || callState == 6 || callState == 5)) {
                    this.titleTextView.setText(LocaleController.getString("VoipGroupConnecting", NUM), false);
                } else if (sharedInstance.getChat() != null) {
                    if (!TextUtils.isEmpty(sharedInstance.groupCall.call.title)) {
                        this.titleTextView.setText(sharedInstance.groupCall.call.title, false);
                        return;
                    }
                    BaseFragment baseFragment = this.fragment;
                    if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).getCurrentChat() == null || ((ChatActivity) this.fragment).getCurrentChat().id != sharedInstance.getChat().id) {
                        this.titleTextView.setText(sharedInstance.getChat().title, false);
                    } else if (ChatObject.isChannelOrGiga(((ChatActivity) this.fragment).getCurrentChat())) {
                        this.titleTextView.setText(LocaleController.getString("VoipChannelViewVoiceChat", NUM), false);
                    } else {
                        this.titleTextView.setText(LocaleController.getString("VoipGroupViewVoiceChat", NUM), false);
                    }
                } else if (sharedInstance.getUser() != null) {
                    TLRPC$User user = sharedInstance.getUser();
                    BaseFragment baseFragment2 = this.fragment;
                    if (!(baseFragment2 instanceof ChatActivity) || ((ChatActivity) baseFragment2).getCurrentUser() == null || ((ChatActivity) this.fragment).getCurrentUser().id != user.id) {
                        this.titleTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                    } else {
                        this.titleTextView.setText(LocaleController.getString("ReturnToCall", NUM));
                    }
                }
            }
        }
    }

    private int getTitleTextColor() {
        int i = this.currentStyle;
        if (i == 4) {
            return getThemedColor("inappPlayerPerformer");
        }
        if (i == 1 || i == 3) {
            return getThemedColor("returnToCallText");
        }
        return getThemedColor("inappPlayerTitle");
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
