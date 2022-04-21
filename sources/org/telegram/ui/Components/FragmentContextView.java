package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    public static final int STYLE_ACTIVE_GROUP_CALL = 3;
    public static final int STYLE_AUDIO_PLAYER = 0;
    public static final int STYLE_CONNECTING_GROUP_CALL = 1;
    public static final int STYLE_IMPORTING_MESSAGES = 5;
    public static final int STYLE_INACTIVE_GROUP_CALL = 4;
    public static final int STYLE_LIVE_LOCATION = 2;
    public static final int STYLE_NOT_SET = -1;
    private static final int menu_speed_fast = 3;
    private static final int menu_speed_normal = 2;
    private static final int menu_speed_slow = 1;
    private static final int menu_speed_veryfast = 4;
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
    private ChatActivity chatActivity;
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
    /* access modifiers changed from: private */
    public CellFlickerDrawable joinButtonFlicker;
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

    @Retention(RetentionPolicy.SOURCE)
    public @interface Style {
    }

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

    public void onAudioSettingsChanged() {
        boolean newMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        if (this.isMuted != newMuted) {
            this.isMuted = newMuted;
            this.muteDrawable.setCustomEndFrame(newMuted ? 15 : 29);
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

    public boolean drawOverlayed() {
        return this.currentStyle == 3;
    }

    public FragmentContextView(Context context, BaseFragment parentFragment, boolean location) {
        this(context, parentFragment, (View) null, location, (Theme.ResourcesProvider) null);
    }

    public FragmentContextView(Context context, BaseFragment parentFragment, boolean location, Theme.ResourcesProvider resourcesProvider2) {
        this(context, parentFragment, (View) null, location, resourcesProvider2);
    }

    /* JADX WARNING: type inference failed for: r3v23, types: [android.view.View] */
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FragmentContextView(android.content.Context r28, org.telegram.ui.ActionBar.BaseFragment r29, android.view.View r30, boolean r31, org.telegram.ui.ActionBar.Theme.ResourcesProvider r32) {
        /*
            r27 = this;
            r0 = r27
            r7 = r28
            r8 = r29
            r9 = r30
            r10 = r31
            r11 = r32
            r27.<init>(r28)
            r12 = 4
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem[r12]
            r0.speedItems = r1
            r1 = -1
            r0.currentProgress = r1
            r0.currentStyle = r1
            r13 = 1
            r0.supportsCalls = r13
            android.graphics.RectF r2 = new android.graphics.RectF
            r2.<init>()
            r0.rect = r2
            org.telegram.ui.Components.FragmentContextView$1 r2 = new org.telegram.ui.Components.FragmentContextView$1
            r2.<init>()
            r0.updateScheduleTimeRunnable = r2
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            r0.account = r2
            r0.lastLocationSharingCount = r1
            org.telegram.ui.Components.FragmentContextView$2 r2 = new org.telegram.ui.Components.FragmentContextView$2
            r2.<init>()
            r0.checkLocationRunnable = r2
            r0.animationIndex = r1
            r0.resourcesProvider = r11
            r0.fragment = r8
            r2 = 0
            android.view.View r3 = r29.getFragmentView()
            boolean r3 = r3 instanceof org.telegram.ui.Components.SizeNotifierFrameLayout
            if (r3 == 0) goto L_0x0051
            org.telegram.ui.ActionBar.BaseFragment r3 = r0.fragment
            android.view.View r3 = r3.getFragmentView()
            r2 = r3
            org.telegram.ui.Components.SizeNotifierFrameLayout r2 = (org.telegram.ui.Components.SizeNotifierFrameLayout) r2
            r14 = r2
            goto L_0x0052
        L_0x0051:
            r14 = r2
        L_0x0052:
            r0.applyingView = r9
            r0.visible = r13
            r0.isLocation = r10
            r15 = 0
            if (r9 != 0) goto L_0x0066
            org.telegram.ui.ActionBar.BaseFragment r2 = r0.fragment
            android.view.View r2 = r2.getFragmentView()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            r2.setClipToPadding(r15)
        L_0x0066:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r13)
            r0.setTag(r2)
            org.telegram.ui.Components.FragmentContextView$3 r2 = new org.telegram.ui.Components.FragmentContextView$3
            r2.<init>(r7, r14)
            r0.frameLayout = r2
            r16 = -1
            r17 = 1108344832(0x42100000, float:36.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r2, r3)
            android.view.View r2 = new android.view.View
            r2.<init>(r7)
            r0.selector = r2
            android.widget.FrameLayout r3 = r0.frameLayout
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r4)
            r3.addView(r2, r1)
            android.view.View r1 = new android.view.View
            r1.<init>(r7)
            r0.shadow = r1
            r2 = 2131165297(0x7var_, float:1.7944807E38)
            r1.setBackgroundResource(r2)
            android.view.View r1 = r0.shadow
            r17 = 1073741824(0x40000000, float:2.0)
            r20 = 1108344832(0x42100000, float:36.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r1, r2)
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r7)
            r0.playButton = r1
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r2)
            android.widget.ImageView r1 = r0.playButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "inappPlayerPlayPause"
            int r4 = r0.getThemedColor(r3)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r4, r5)
            r1.setColorFilter(r2)
            android.widget.ImageView r1 = r0.playButton
            org.telegram.ui.Components.PlayPauseDrawable r2 = new org.telegram.ui.Components.PlayPauseDrawable
            r4 = 14
            r2.<init>(r4)
            r0.playPauseDrawable = r2
            r1.setImageDrawable(r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            r16 = 436207615(0x19ffffff, float:2.6469778E-23)
            r6 = 21
            r5 = 1096810496(0x41600000, float:14.0)
            if (r1 < r6) goto L_0x00ff
            android.widget.ImageView r1 = r0.playButton
            int r2 = r0.getThemedColor(r3)
            r2 = r2 & r16
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r13, r4)
            r1.setBackground(r2)
        L_0x00ff:
            android.widget.ImageView r1 = r0.playButton
            r4 = 36
            r2 = 51
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r2)
            r0.addView(r1, r6)
            android.widget.ImageView r1 = r0.playButton
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda6 r6 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda6
            r6.<init>(r0)
            r1.setOnClickListener(r6)
            org.telegram.ui.Components.RLottieImageView r1 = new org.telegram.ui.Components.RLottieImageView
            r1.<init>(r7)
            r0.importingImageView = r1
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r6)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r1.setAutoRepeat(r13)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r6 = 2131558472(0x7f0d0048, float:1.874226E38)
            r2 = 30
            r1.setAnimation(r6, r2, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r2 = 1102053376(0x41b00000, float:22.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r0.getThemedColor(r3)
            android.graphics.drawable.ShapeDrawable r2 = org.telegram.ui.ActionBar.Theme.createCircleDrawable(r2, r3)
            r1.setBackground(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.importingImageView
            r19 = 22
            r20 = 1102053376(0x41b00000, float:22.0)
            r21 = 51
            r22 = 1088421888(0x40e00000, float:7.0)
            r23 = 1088421888(0x40e00000, float:7.0)
            r24 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            org.telegram.ui.Components.FragmentContextView$4 r1 = new org.telegram.ui.Components.FragmentContextView$4
            r1.<init>(r7, r7)
            r0.titleTextView = r1
            r19 = -1
            r20 = 1108344832(0x42100000, float:36.0)
            r22 = 1108082688(0x420CLASSNAME, float:35.0)
            r23 = 0
            r24 = 1108344832(0x42100000, float:36.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            org.telegram.ui.Components.FragmentContextView$5 r1 = new org.telegram.ui.Components.FragmentContextView$5
            r1.<init>(r7, r7)
            r0.subtitleTextView = r1
            r23 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            org.telegram.ui.Components.voip.CellFlickerDrawable r1 = new org.telegram.ui.Components.voip.CellFlickerDrawable
            r1.<init>()
            r0.joinButtonFlicker = r1
            r2 = 1073741824(0x40000000, float:2.0)
            r1.setProgress(r2)
            org.telegram.ui.Components.voip.CellFlickerDrawable r1 = r0.joinButtonFlicker
            r1.repeatEnabled = r15
            org.telegram.ui.Components.FragmentContextView$6 r1 = new org.telegram.ui.Components.FragmentContextView$6
            r1.<init>(r7)
            r0.joinButton = r1
            r2 = 2131628770(0x7f0e12e2, float:1.8884842E38)
            java.lang.String r3 = "VoipChatJoin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.joinButton
            java.lang.String r2 = "featuredStickers_buttonText"
            int r2 = r0.getThemedColor(r2)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r0.joinButton
            r19 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            java.lang.String r3 = "featuredStickers_addButton"
            int r3 = r0.getThemedColor(r3)
            java.lang.String r6 = "featuredStickers_addButtonPressed"
            int r6 = r0.getThemedColor(r6)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r2, r3, r6)
            r1.setBackground(r2)
            android.widget.TextView r1 = r0.joinButton
            r1.setTextSize(r13, r5)
            android.widget.TextView r1 = r0.joinButton
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.joinButton
            r2 = 17
            r1.setGravity(r2)
            android.widget.TextView r1 = r0.joinButton
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.setPadding(r2, r15, r3, r15)
            android.widget.TextView r1 = r0.joinButton
            r20 = -2
            r21 = 1105199104(0x41e00000, float:28.0)
            r22 = 53
            r23 = 0
            r24 = 1092616192(0x41200000, float:10.0)
            r25 = 1096810496(0x41600000, float:14.0)
            r26 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r1, r2)
            android.widget.TextView r1 = r0.joinButton
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda7 r2 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda7
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            if (r10 != 0) goto L_0x02f1
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r3 = 0
            r20 = 0
            java.lang.String r1 = "dialogTextBlack"
            int r21 = r0.getThemedColor(r1)
            r1 = r6
            r2 = r28
            r4 = r20
            r18 = 1096810496(0x41600000, float:14.0)
            r5 = r21
            r12 = r6
            r6 = r32
            r1.<init>((android.content.Context) r2, (org.telegram.ui.ActionBar.ActionBarMenu) r3, (int) r4, (int) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r0.playbackSpeedButton = r12
            r12.setLongClickEnabled(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r1.setShowSubmenuByMove(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r2 = 2131624013(0x7f0e004d, float:1.8875194E38)
            java.lang.String r3 = "AccDescrPlayerSpeed"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda2
            r2.<init>(r0)
            r1.setDelegate(r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165904(0x7var_d0, float:1.7946038E38)
            r4 = 2131628185(0x7f0e1099, float:1.8883656E38)
            java.lang.String r5 = "SpeedSlow"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem((int) r13, (int) r3, (java.lang.CharSequence) r4)
            r1[r15] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165905(0x7var_d1, float:1.794604E38)
            r4 = 2131628184(0x7f0e1098, float:1.8883654E38)
            java.lang.String r5 = "SpeedNormal"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem((int) r5, (int) r3, (java.lang.CharSequence) r4)
            r1[r13] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165906(0x7var_d2, float:1.7946042E38)
            r4 = 2131628183(0x7f0e1097, float:1.8883651E38)
            java.lang.String r6 = "SpeedFast"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r6 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem((int) r6, (int) r3, (java.lang.CharSequence) r4)
            r1[r5] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165907(0x7var_d3, float:1.7946044E38)
            r4 = 2131628186(0x7f0e109a, float:1.8883658E38)
            java.lang.String r5 = "SpeedVeryFast"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 4
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem((int) r5, (int) r3, (java.lang.CharSequence) r4)
            r1[r6] = r2
            float r1 = org.telegram.messenger.AndroidUtilities.density
            r2 = 1077936128(0x40400000, float:3.0)
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x02b7
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r1.setPadding(r15, r13, r15, r15)
        L_0x02b7:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setAdditionalXOffset(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r20 = 36
            r21 = 1108344832(0x42100000, float:36.0)
            r22 = 53
            r23 = 0
            r24 = 0
            r25 = 1108344832(0x42100000, float:36.0)
            r26 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda8
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda12 r2 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda12
            r2.<init>(r0)
            r1.setOnLongClickListener(r2)
            r27.updatePlaybackButton()
            goto L_0x02f3
        L_0x02f1:
            r18 = 1096810496(0x41600000, float:14.0)
        L_0x02f3:
            org.telegram.ui.Components.AvatarsImageView r1 = new org.telegram.ui.Components.AvatarsImageView
            r1.<init>(r7, r15)
            r0.avatars = r1
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda13
            r2.<init>(r0)
            r1.setDelegate(r2)
            org.telegram.ui.Components.AvatarsImageView r1 = r0.avatars
            r2 = 8
            r1.setVisibility(r2)
            org.telegram.ui.Components.AvatarsImageView r1 = r0.avatars
            r3 = 108(0x6c, float:1.51E-43)
            r4 = 51
            r5 = 36
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r5, (int) r4)
            r0.addView(r1, r3)
            org.telegram.ui.Components.RLottieDrawable r1 = new org.telegram.ui.Components.RLottieDrawable
            r21 = 2131558557(0x7f0d009d, float:1.8742433E38)
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r3 = 1101004800(0x41a00000, float:20.0)
            int r24 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r25 = 1
            r26 = 0
            java.lang.String r22 = "NUM"
            r20 = r1
            r20.<init>(r21, r22, r23, r24, r25, r26)
            r0.muteDrawable = r1
            org.telegram.ui.Components.FragmentContextView$7 r1 = new org.telegram.ui.Components.FragmentContextView$7
            r1.<init>(r7)
            r0.muteButton = r1
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "returnToCallText"
            int r4 = r0.getThemedColor(r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r5)
            r1.setColorFilter(r3)
            int r1 = android.os.Build.VERSION.SDK_INT
            java.lang.String r3 = "inappPlayerClose"
            r4 = 21
            if (r1 < r4) goto L_0x0366
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            int r5 = r0.getThemedColor(r3)
            r5 = r5 & r16
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r13, r6)
            r1.setBackground(r5)
        L_0x0366:
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            r1.setAnimation(r5)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r5)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r19 = 36
            r20 = 1108344832(0x42100000, float:36.0)
            r21 = 53
            r22 = 0
            r23 = 0
            r24 = 1073741824(0x40000000, float:2.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda9
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r7)
            r0.closeButton = r1
            r2 = 2131165741(0x7var_d, float:1.7945708E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r0.closeButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r5 = r0.getThemedColor(r3)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r5, r6)
            r1.setColorFilter(r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r4) goto L_0x03ce
            android.widget.ImageView r1 = r0.closeButton
            int r2 = r0.getThemedColor(r3)
            r2 = r2 & r16
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r13, r3)
            r1.setBackground(r2)
        L_0x03ce:
            android.widget.ImageView r1 = r0.closeButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r2)
            android.widget.ImageView r1 = r0.closeButton
            r15 = 36
            r16 = 1108344832(0x42100000, float:36.0)
            r17 = 53
            r18 = 0
            r19 = 0
            r20 = 1073741824(0x40000000, float:2.0)
            r21 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r0.addView(r1, r2)
            android.widget.ImageView r1 = r0.closeButton
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda10 r2 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda10
            r2.<init>(r0, r11)
            r1.setOnClickListener(r2)
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda11 r1 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda11
            r1.<init>(r0, r11, r8)
            r0.setOnClickListener(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, android.view.View, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4020lambda$new$0$orgtelegramuiComponentsFragmentContextView(View v) {
        if (this.currentStyle != 0) {
            return;
        }
        if (MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else {
            MediaController.getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4021lambda$new$1$orgtelegramuiComponentsFragmentContextView(View v) {
        callOnClick();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4023lambda$new$2$orgtelegramuiComponentsFragmentContextView(int id) {
        float oldSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        if (id == 1) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 0.5f);
        } else if (id == 2) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else if (id == 3) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.5f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.8f);
        }
        float newSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        if (oldSpeed != newSpeed) {
            playbackSpeedChanged(newSpeed);
        }
        updatePlaybackButton();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4024lambda$new$3$orgtelegramuiComponentsFragmentContextView(View v) {
        float newSpeed;
        if (Math.abs(MediaController.getInstance().getPlaybackSpeed(this.isMusic) - 1.0f) > 0.001f) {
            newSpeed = 1.0f;
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else {
            MediaController instance = MediaController.getInstance();
            boolean z = this.isMusic;
            float fastPlaybackSpeed = MediaController.getInstance().getFastPlaybackSpeed(this.isMusic);
            newSpeed = fastPlaybackSpeed;
            instance.setPlaybackSpeed(z, fastPlaybackSpeed);
        }
        playbackSpeedChanged(newSpeed);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ boolean m4025lambda$new$4$orgtelegramuiComponentsFragmentContextView(View view) {
        this.playbackSpeedButton.toggleSubMenu();
        return true;
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4026lambda$new$5$orgtelegramuiComponentsFragmentContextView() {
        updateAvatars(true);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4027lambda$new$6$orgtelegramuiComponentsFragmentContextView(View v) {
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null) {
            if (voIPService.groupCall != null) {
                AccountInstance instance = AccountInstance.getInstance(voIPService.getAccount());
                ChatObject.Call call = voIPService.groupCall;
                TLRPC.Chat chat = voIPService.getChat();
                TLRPC.TL_groupCallParticipant participant = call.participants.get(voIPService.getSelfId());
                if (participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(chat)) {
                    return;
                }
            }
            boolean z = !voIPService.isMicMute();
            this.isMuted = z;
            voIPService.setMicMute(z, false, true);
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

    /* renamed from: lambda$new$8$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4029lambda$new$8$orgtelegramuiComponentsFragmentContextView(Theme.ResourcesProvider resourcesProvider2, View v) {
        if (this.currentStyle == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.fragment.getParentActivity(), resourcesProvider2);
            builder.setTitle(LocaleController.getString("StopLiveLocationAlertToTitle", NUM));
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof DialogsActivity) {
                builder.setMessage(LocaleController.getString("StopLiveLocationAlertAllText", NUM));
            } else {
                ChatActivity activity = (ChatActivity) baseFragment;
                TLRPC.Chat chat = activity.getCurrentChat();
                TLRPC.User user = activity.getCurrentUser();
                if (chat != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToGroupText", NUM, chat.title)));
                } else if (user != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToUserText", NUM, UserObject.getFirstName(user))));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSure", NUM));
                }
            }
            builder.setPositiveButton(LocaleController.getString("Stop", NUM), new FragmentContextView$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog alertDialog = builder.create();
            builder.show();
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(getThemedColor("dialogTextRed2"));
                return;
            }
            return;
        }
        MediaController.getInstance().cleanupPlayer(true, true);
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4028lambda$new$7$orgtelegramuiComponentsFragmentContextView(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            for (int a = 0; a < 3; a++) {
                LocationController.getInstance(a).removeAllLocationSharings();
            }
            return;
        }
        LocationController.getInstance(baseFragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) this.fragment).getDialogId());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0162, code lost:
        r2 = (org.telegram.ui.ChatActivity) r0.fragment;
     */
    /* renamed from: lambda$new$10$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m4022lambda$new$10$orgtelegramuiComponentsFragmentContextView(org.telegram.ui.ActionBar.Theme.ResourcesProvider r17, org.telegram.ui.ActionBar.BaseFragment r18, android.view.View r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            int r2 = r0.currentStyle
            if (r2 != 0) goto L_0x009f
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r2 = r2.getPlayingMessageObject()
            org.telegram.ui.ActionBar.BaseFragment r3 = r0.fragment
            if (r3 == 0) goto L_0x009d
            if (r2 == 0) goto L_0x009d
            boolean r3 = r2.isMusic()
            if (r3 == 0) goto L_0x0033
            android.content.Context r3 = r16.getContext()
            boolean r3 = r3 instanceof org.telegram.ui.LaunchActivity
            if (r3 == 0) goto L_0x009d
            org.telegram.ui.ActionBar.BaseFragment r3 = r0.fragment
            org.telegram.ui.Components.AudioPlayerAlert r4 = new org.telegram.ui.Components.AudioPlayerAlert
            android.content.Context r5 = r16.getContext()
            r4.<init>(r5, r1)
            r3.showDialog(r4)
            goto L_0x009d
        L_0x0033:
            r3 = 0
            org.telegram.ui.ActionBar.BaseFragment r5 = r0.fragment
            boolean r6 = r5 instanceof org.telegram.ui.ChatActivity
            if (r6 == 0) goto L_0x0041
            org.telegram.ui.ChatActivity r5 = (org.telegram.ui.ChatActivity) r5
            long r3 = r5.getDialogId()
        L_0x0041:
            long r5 = r2.getDialogId()
            int r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x005b
            org.telegram.ui.ActionBar.BaseFragment r5 = r0.fragment
            r6 = r5
            org.telegram.ui.ChatActivity r6 = (org.telegram.ui.ChatActivity) r6
            int r7 = r2.getId()
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 1
            r12 = 0
            r6.scrollToMessageId(r7, r8, r9, r10, r11, r12)
            goto L_0x009d
        L_0x005b:
            long r3 = r2.getDialogId()
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            boolean r6 = org.telegram.messenger.DialogObject.isEncryptedDialog(r3)
            if (r6 == 0) goto L_0x0074
            int r6 = org.telegram.messenger.DialogObject.getEncryptedChatId(r3)
            java.lang.String r7 = "enc_id"
            r5.putInt(r7, r6)
            goto L_0x0086
        L_0x0074:
            boolean r6 = org.telegram.messenger.DialogObject.isUserDialog(r3)
            if (r6 == 0) goto L_0x0080
            java.lang.String r6 = "user_id"
            r5.putLong(r6, r3)
            goto L_0x0086
        L_0x0080:
            long r6 = -r3
            java.lang.String r8 = "chat_id"
            r5.putLong(r8, r6)
        L_0x0086:
            int r6 = r2.getId()
            java.lang.String r7 = "message_id"
            r5.putInt(r7, r6)
            org.telegram.ui.ActionBar.BaseFragment r6 = r0.fragment
            org.telegram.ui.ChatActivity r7 = new org.telegram.ui.ChatActivity
            r7.<init>(r5)
            org.telegram.ui.ActionBar.BaseFragment r8 = r0.fragment
            boolean r8 = r8 instanceof org.telegram.ui.ChatActivity
            r6.presentFragment(r7, r8)
        L_0x009d:
            goto L_0x01d4
        L_0x009f:
            r3 = 1
            if (r2 != r3) goto L_0x00bc
            android.content.Intent r2 = new android.content.Intent
            android.content.Context r3 = r16.getContext()
            java.lang.Class<org.telegram.ui.LaunchActivity> r4 = org.telegram.ui.LaunchActivity.class
            r2.<init>(r3, r4)
            java.lang.String r3 = "voip"
            android.content.Intent r2 = r2.setAction(r3)
            android.content.Context r3 = r16.getContext()
            r3.startActivity(r2)
            goto L_0x01d4
        L_0x00bc:
            r4 = 2
            r5 = 3
            r6 = 0
            if (r2 != r4) goto L_0x012a
            r7 = 0
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.ui.ActionBar.BaseFragment r4 = r0.fragment
            boolean r9 = r4 instanceof org.telegram.ui.ChatActivity
            if (r9 == 0) goto L_0x00d8
            org.telegram.ui.ChatActivity r4 = (org.telegram.ui.ChatActivity) r4
            long r7 = r4.getDialogId()
            org.telegram.ui.ActionBar.BaseFragment r3 = r0.fragment
            int r2 = r3.getCurrentAccount()
            goto L_0x0103
        L_0x00d8:
            int r4 = org.telegram.messenger.LocationController.getLocationsCount()
            if (r4 != r3) goto L_0x0103
            r3 = 0
        L_0x00df:
            if (r3 >= r5) goto L_0x0103
            org.telegram.messenger.LocationController r4 = org.telegram.messenger.LocationController.getInstance(r3)
            java.util.ArrayList<org.telegram.messenger.LocationController$SharingLocationInfo> r4 = r4.sharingLocationsUI
            boolean r9 = r4.isEmpty()
            if (r9 != 0) goto L_0x0100
            org.telegram.messenger.LocationController r5 = org.telegram.messenger.LocationController.getInstance(r3)
            java.util.ArrayList<org.telegram.messenger.LocationController$SharingLocationInfo> r5 = r5.sharingLocationsUI
            java.lang.Object r5 = r5.get(r6)
            org.telegram.messenger.LocationController$SharingLocationInfo r5 = (org.telegram.messenger.LocationController.SharingLocationInfo) r5
            long r7 = r5.did
            org.telegram.messenger.MessageObject r6 = r5.messageObject
            int r2 = r6.currentAccount
            goto L_0x0103
        L_0x0100:
            int r3 = r3 + 1
            goto L_0x00df
        L_0x0103:
            r3 = 0
            int r5 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0115
            org.telegram.messenger.LocationController r3 = org.telegram.messenger.LocationController.getInstance(r2)
            org.telegram.messenger.LocationController$SharingLocationInfo r3 = r3.getSharingLocationInfo(r7)
            r0.openSharingLocation(r3)
            goto L_0x0128
        L_0x0115:
            org.telegram.ui.ActionBar.BaseFragment r3 = r0.fragment
            org.telegram.ui.Components.SharingLocationsAlert r4 = new org.telegram.ui.Components.SharingLocationsAlert
            android.content.Context r5 = r16.getContext()
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda3
            r6.<init>(r0)
            r4.<init>(r5, r6, r1)
            r3.showDialog(r4)
        L_0x0128:
            goto L_0x01d4
        L_0x012a:
            if (r2 != r5) goto L_0x0156
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x01d4
            android.content.Context r2 = r16.getContext()
            boolean r2 = r2 instanceof org.telegram.ui.LaunchActivity
            if (r2 == 0) goto L_0x01d4
            android.content.Context r2 = r16.getContext()
            r3 = r2
            org.telegram.ui.LaunchActivity r3 = (org.telegram.ui.LaunchActivity) r3
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r2 = r2.getAccount()
            org.telegram.messenger.AccountInstance r4 = org.telegram.messenger.AccountInstance.getInstance(r2)
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            org.telegram.ui.GroupCallActivity.create(r3, r4, r5, r6, r7, r8)
            goto L_0x01d4
        L_0x0156:
            r4 = 4
            if (r2 != r4) goto L_0x019e
            org.telegram.ui.ActionBar.BaseFragment r2 = r0.fragment
            android.app.Activity r2 = r2.getParentActivity()
            if (r2 != 0) goto L_0x0162
            return
        L_0x0162:
            org.telegram.ui.ActionBar.BaseFragment r2 = r0.fragment
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.messenger.ChatObject$Call r4 = r2.getGroupCall()
            if (r4 != 0) goto L_0x016d
            return
        L_0x016d:
            org.telegram.messenger.MessagesController r5 = r2.getMessagesController()
            long r7 = r4.chatId
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r8 = r5.getChat(r7)
            r9 = 0
            r10 = 0
            r11 = 0
            org.telegram.tgnet.TLRPC$GroupCall r5 = r4.call
            if (r5 == 0) goto L_0x0189
            org.telegram.tgnet.TLRPC$GroupCall r5 = r4.call
            boolean r5 = r5.rtmp_stream
            if (r5 != 0) goto L_0x0189
            goto L_0x018a
        L_0x0189:
            r3 = 0
        L_0x018a:
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r3)
            org.telegram.ui.ActionBar.BaseFragment r3 = r0.fragment
            android.app.Activity r13 = r3.getParentActivity()
            org.telegram.ui.ActionBar.BaseFragment r14 = r0.fragment
            org.telegram.messenger.AccountInstance r15 = r14.getAccountInstance()
            org.telegram.ui.Components.voip.VoIPHelper.startCall(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x01d3
        L_0x019e:
            r3 = 5
            if (r2 != r3) goto L_0x01d3
            org.telegram.messenger.SendMessagesHelper r2 = r18.getSendMessagesHelper()
            r3 = r18
            org.telegram.ui.ChatActivity r3 = (org.telegram.ui.ChatActivity) r3
            long r3 = r3.getDialogId()
            org.telegram.messenger.SendMessagesHelper$ImportingHistory r2 = r2.getImportingHistory(r3)
            if (r2 != 0) goto L_0x01b4
            return
        L_0x01b4:
            org.telegram.ui.Components.ImportingAlert r3 = new org.telegram.ui.Components.ImportingAlert
            android.content.Context r4 = r16.getContext()
            r5 = 0
            org.telegram.ui.ActionBar.BaseFragment r7 = r0.fragment
            org.telegram.ui.ChatActivity r7 = (org.telegram.ui.ChatActivity) r7
            r3.<init>(r4, r5, r7, r1)
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda5
            r4.<init>(r0)
            r3.setOnHideListener(r4)
            org.telegram.ui.ActionBar.BaseFragment r4 = r0.fragment
            r4.showDialog(r3)
            r0.checkImport(r6)
            goto L_0x01d4
        L_0x01d3:
        L_0x01d4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.m4022lambda$new$10$orgtelegramuiComponentsFragmentContextView(org.telegram.ui.ActionBar.Theme$ResourcesProvider, org.telegram.ui.ActionBar.BaseFragment, android.view.View):void");
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4030lambda$new$9$orgtelegramuiComponentsFragmentContextView(DialogInterface dialog) {
        checkImport(false);
    }

    public void setSupportsCalls(boolean value) {
        this.supportsCalls = value;
    }

    public void setDelegate(FragmentContextViewDelegate fragmentContextViewDelegate) {
        this.delegate = fragmentContextViewDelegate;
    }

    private void updatePlaybackButton() {
        if (this.playbackSpeedButton != null) {
            float currentPlaybackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
            float speed = MediaController.getInstance().getFastPlaybackSpeed(this.isMusic);
            if (Math.abs(speed - 1.8f) < 0.001f) {
                this.playbackSpeedButton.setIcon(NUM);
            } else if (Math.abs(speed - 1.5f) < 0.001f) {
                this.playbackSpeedButton.setIcon(NUM);
            } else {
                this.playbackSpeedButton.setIcon(NUM);
            }
            updateColors();
            for (int a = 0; a < this.speedItems.length; a++) {
                if ((a != 0 || Math.abs(currentPlaybackSpeed - 0.5f) >= 0.001f) && ((a != 1 || Math.abs(currentPlaybackSpeed - 1.0f) >= 0.001f) && ((a != 2 || Math.abs(currentPlaybackSpeed - 1.5f) >= 0.001f) && (a != 3 || Math.abs(currentPlaybackSpeed - 1.8f) >= 0.001f)))) {
                    this.speedItems[a].setColors(getThemedColor("actionBarDefaultSubmenuItem"), getThemedColor("actionBarDefaultSubmenuItemIcon"));
                } else {
                    this.speedItems[a].setColors(getThemedColor("inappPlayerPlayPause"), getThemedColor("inappPlayerPlayPause"));
                }
            }
        }
    }

    public void updateColors() {
        String key;
        if (this.playbackSpeedButton != null) {
            if (Math.abs(MediaController.getInstance().getPlaybackSpeed(this.isMusic) - 1.0f) > 0.001f) {
                key = "inappPlayerPlayPause";
            } else {
                key = "inappPlayerClose";
            }
            this.playbackSpeedButton.setIconColor(getThemedColor(key));
            if (Build.VERSION.SDK_INT >= 21) {
                this.playbackSpeedButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(key) & NUM, 1, AndroidUtilities.dp(14.0f)));
            }
        }
    }

    public void setAdditionalContextView(FragmentContextView contextView) {
        this.additionalContextView = contextView;
    }

    /* access modifiers changed from: private */
    public void openSharingLocation(LocationController.SharingLocationInfo info) {
        if (info != null && (this.fragment.getParentActivity() instanceof LaunchActivity)) {
            LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
            launchActivity.switchToAccount(info.messageObject.currentAccount, true);
            LocationActivity locationActivity = new LocationActivity(2);
            locationActivity.setMessageObject(info.messageObject);
            locationActivity.setDelegate(new FragmentContextView$$ExternalSyntheticLambda4(info, info.messageObject.getDialogId()));
            launchActivity.m2367lambda$runLinkRequest$54$orgtelegramuiLaunchActivity(locationActivity);
        }
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    private void checkVisibility() {
        boolean show = false;
        int i = 0;
        if (this.isLocation) {
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof DialogsActivity) {
                show = LocationController.getLocationsCount() != 0;
            } else {
                show = LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
            }
        } else if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isHangingUp() || VoIPService.getSharedInstance().getCallState() == 15) {
            BaseFragment baseFragment2 = this.fragment;
            if (!(baseFragment2 instanceof ChatActivity) || baseFragment2.getSendMessagesHelper().getImportingHistory(((ChatActivity) this.fragment).getDialogId()) == null || isPlayingVoice()) {
                BaseFragment baseFragment3 = this.fragment;
                if (!(baseFragment3 instanceof ChatActivity) || ((ChatActivity) baseFragment3).getGroupCall() == null || !((ChatActivity) this.fragment).getGroupCall().shouldShowPanel() || GroupCallPip.isShowing() || isPlayingVoice()) {
                    MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (!(messageObject == null || messageObject.getId() == 0)) {
                        show = true;
                    }
                } else {
                    show = true;
                    startJoinFlickerAnimation();
                }
            } else {
                show = true;
            }
        } else {
            show = true;
            startJoinFlickerAnimation();
        }
        if (!show) {
            i = 8;
        }
        setVisibility(i);
    }

    public void setTopPadding(float value) {
        this.topPadding = value;
        if (this.fragment != null && getParent() != null) {
            View view = this.applyingView;
            if (view == null) {
                view = this.fragment.getFragmentView();
            }
            int additionalPadding = 0;
            FragmentContextView fragmentContextView = this.additionalContextView;
            if (!(fragmentContextView == null || fragmentContextView.getVisibility() != 0 || this.additionalContextView.getParent() == null)) {
                additionalPadding = AndroidUtilities.dp((float) this.additionalContextView.getStyleHeight());
            }
            if (view != null && getParent() != null) {
                view.setPadding(0, ((int) (getVisibility() == 0 ? this.topPadding : 0.0f)) + additionalPadding, 0, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void playbackSpeedChanged(float value) {
    }

    private void updateStyle(int style) {
        int i = style;
        int i2 = this.currentStyle;
        if (i2 != i) {
            boolean z = true;
            if (i2 == 3 || i2 == 1) {
                Theme.getFragmentContextViewWavesDrawable().removeParent(this);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().unregisterStateListener(this);
                }
            }
            this.currentStyle = i;
            this.frameLayout.setWillNotDraw(i != 4);
            if (i != 4) {
                this.timeLayout = null;
            }
            AvatarsImageView avatarsImageView = this.avatars;
            if (avatarsImageView != null) {
                avatarsImageView.setStyle(this.currentStyle);
                this.avatars.setLayoutParams(LayoutHelper.createFrame(108, getStyleHeight(), 51));
            }
            this.frameLayout.setLayoutParams(LayoutHelper.createFrame(-1, (float) getStyleHeight(), 51, 0.0f, 0.0f, 0.0f, 0.0f));
            this.shadow.setLayoutParams(LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, (float) getStyleHeight(), 0.0f, 0.0f));
            float f = this.topPadding;
            if (f > 0.0f && f != ((float) AndroidUtilities.dp2((float) getStyleHeight()))) {
                updatePaddings();
                setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
            }
            if (i == 5) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(getThemedColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                int i3 = 0;
                while (i3 < 2) {
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                    TextView textView = i3 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                    if (textView != null) {
                        textView.setGravity(19);
                        textView.setTextColor(getThemedColor("inappPlayerTitle"));
                        textView.setTypeface(Typeface.DEFAULT);
                        textView.setTextSize(1, 15.0f);
                    }
                    i3++;
                }
                this.titleTextView.setTag("inappPlayerTitle");
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.muteButton.setVisibility(8);
                this.avatars.setVisibility(8);
                this.importingImageView.setVisibility(0);
                this.importingImageView.playAnimation();
                this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", NUM));
                ActionBarMenuItem actionBarMenuItem = this.playbackSpeedButton;
                if (actionBarMenuItem != null) {
                    actionBarMenuItem.setVisibility(8);
                }
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
            } else if (i == 0 || i == 2) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(getThemedColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.closeButton.setVisibility(0);
                this.playButton.setVisibility(0);
                this.muteButton.setVisibility(8);
                this.importingImageView.setVisibility(8);
                this.importingImageView.stopAnimation();
                this.avatars.setVisibility(8);
                int i4 = 0;
                while (i4 < 2) {
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = this.titleTextView;
                    TextView textView2 = i4 == 0 ? clippingTextViewSwitcher2.getTextView() : clippingTextViewSwitcher2.getNextTextView();
                    if (textView2 != null) {
                        textView2.setGravity(19);
                        textView2.setTextColor(getThemedColor("inappPlayerTitle"));
                        textView2.setTypeface(Typeface.DEFAULT);
                        textView2.setTextSize(1, 15.0f);
                    }
                    i4++;
                }
                this.titleTextView.setTag("inappPlayerTitle");
                if (i == 0) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                    ActionBarMenuItem actionBarMenuItem2 = this.playbackSpeedButton;
                    if (actionBarMenuItem2 != null) {
                        actionBarMenuItem2.setVisibility(0);
                    }
                    this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", NUM));
                    return;
                }
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
                this.closeButton.setContentDescription(LocaleController.getString("AccDescrStopLiveLocation", NUM));
            } else if (i == 4) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(getThemedColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.muteButton.setVisibility(8);
                this.subtitleTextView.setVisibility(0);
                int i5 = 0;
                while (i5 < 2) {
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher3 = this.titleTextView;
                    TextView textView3 = i5 == 0 ? clippingTextViewSwitcher3.getTextView() : clippingTextViewSwitcher3.getNextTextView();
                    if (textView3 != null) {
                        textView3.setGravity(51);
                        textView3.setTextColor(getThemedColor("inappPlayerPerformer"));
                        textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView3.setTextSize(1, 15.0f);
                    }
                    i5++;
                }
                this.titleTextView.setTag("inappPlayerPerformer");
                this.titleTextView.setPadding(0, 0, 0, 0);
                this.importingImageView.setVisibility(8);
                this.importingImageView.stopAnimation();
                boolean isRtmpStream = false;
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    ChatActivity chatActivity2 = (ChatActivity) baseFragment;
                    if (chatActivity2.getGroupCall() == null || chatActivity2.getGroupCall().call == null || !chatActivity2.getGroupCall().call.rtmp_stream) {
                        z = false;
                    }
                    isRtmpStream = z;
                }
                this.avatars.setVisibility(!isRtmpStream ? 0 : 8);
                if (this.avatars.getVisibility() != 8) {
                    updateAvatars(false);
                } else {
                    this.titleTextView.setTranslationX((float) (-AndroidUtilities.dp(36.0f)));
                    this.subtitleTextView.setTranslationX((float) (-AndroidUtilities.dp(36.0f)));
                }
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                ActionBarMenuItem actionBarMenuItem3 = this.playbackSpeedButton;
                if (actionBarMenuItem3 != null) {
                    actionBarMenuItem3.setVisibility(8);
                }
            } else if (i == 1 || i == 3) {
                this.selector.setBackground((Drawable) null);
                updateCallTitle();
                boolean isRtmpStream2 = VoIPService.hasRtmpStream();
                this.avatars.setVisibility(!isRtmpStream2 ? 0 : 8);
                if (i == 3 && VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().registerStateListener(this);
                }
                if (this.avatars.getVisibility() != 8) {
                    updateAvatars(false);
                } else {
                    this.titleTextView.setTranslationX(0.0f);
                    this.subtitleTextView.setTranslationX(0.0f);
                }
                this.muteButton.setVisibility(!isRtmpStream2 ? 0 : 8);
                boolean z2 = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
                this.isMuted = z2;
                this.muteDrawable.setCustomEndFrame(z2 ? 15 : 29);
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
                this.muteButton.invalidate();
                this.frameLayout.setBackground((Drawable) null);
                this.frameLayout.setBackgroundColor(0);
                this.importingImageView.setVisibility(8);
                this.importingImageView.stopAnimation();
                Theme.getFragmentContextViewWavesDrawable().addParent(this);
                invalidate();
                int i6 = 0;
                while (i6 < 2) {
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher4 = this.titleTextView;
                    TextView textView4 = i6 == 0 ? clippingTextViewSwitcher4.getTextView() : clippingTextViewSwitcher4.getNextTextView();
                    if (textView4 != null) {
                        textView4.setGravity(19);
                        textView4.setTextColor(getThemedColor("returnToCallText"));
                        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView4.setTextSize(1, 14.0f);
                    }
                    i6++;
                }
                this.titleTextView.setTag("returnToCallText");
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
                this.titleTextView.setPadding(AndroidUtilities.dp(112.0f), 0, AndroidUtilities.dp(112.0f), 0);
                ActionBarMenuItem actionBarMenuItem4 = this.playbackSpeedButton;
                if (actionBarMenuItem4 != null) {
                    actionBarMenuItem4.setVisibility(8);
                }
            }
        }
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
            for (int a = 0; a < 3; a++) {
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.groupCallTypingsUpdated);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.groupCallVisibilityChanged);
        }
        int i = this.currentStyle;
        if (i == 3 || i == 1) {
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
            for (int a = 0; a < 3; a++) {
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.groupCallTypingsUpdated);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.historyImportProgressChanged);
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
        int i2 = this.currentStyle;
        if (i2 == 3 || i2 == 1) {
            Theme.getFragmentContextViewWavesDrawable().addParent(this);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            boolean newMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (this.isMuted != newMuted) {
                this.isMuted = newMuted;
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                if (!newMuted) {
                    i = 29;
                }
                rLottieDrawable.setCustomEndFrame(i);
                RLottieDrawable rLottieDrawable2 = this.muteDrawable;
                rLottieDrawable2.setCurrentFrame(rLottieDrawable2.getCustomEndFrame() - 1, false, true);
                this.muteButton.invalidate();
            }
        } else if (i2 == 4 && !this.scheduleRunnableScheduled) {
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, AndroidUtilities.dp2((float) (getStyleHeight() + 2)));
    }

    public void didReceivedNotification(int id, int account2, Object... args) {
        VoIPService sharedInstance;
        TLRPC.TL_groupCallParticipant participant;
        int i = id;
        if (i == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
        } else if (i == NotificationCenter.liveLocationsCacheChanged) {
            if (this.fragment instanceof ChatActivity) {
                if (((ChatActivity) this.fragment).getDialogId() == args[0].longValue()) {
                    checkLocationString();
                }
            }
        } else if (i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.didEndCall) {
            int i2 = this.currentStyle;
            if (i2 == 1 || i2 == 3 || i2 == 4) {
                checkCall(false);
            }
            checkPlayer(false);
        } else if (i == NotificationCenter.didStartedCall || i == NotificationCenter.groupCallUpdated || i == NotificationCenter.groupCallVisibilityChanged) {
            checkCall(false);
            if (this.currentStyle == 3 && (sharedInstance = VoIPService.getSharedInstance()) != null && sharedInstance.groupCall != null) {
                if (i == NotificationCenter.didStartedCall) {
                    sharedInstance.registerStateListener(this);
                }
                int currentCallState = sharedInstance.getCallState();
                if (currentCallState != 1 && currentCallState != 2 && currentCallState != 6 && currentCallState != 5 && (participant = sharedInstance.groupCall.participants.get(sharedInstance.getSelfId())) != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(sharedInstance.getChat())) {
                    sharedInstance.setMicMute(true, false, false);
                    long now = SystemClock.uptimeMillis();
                    this.muteButton.dispatchTouchEvent(MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0));
                }
            }
        } else if (i == NotificationCenter.groupCallTypingsUpdated) {
            if (this.visible && this.currentStyle == 4) {
                ChatObject.Call call = ((ChatActivity) this.fragment).getGroupCall();
                if (call != null) {
                    if (call.isScheduled()) {
                        this.subtitleTextView.setText(LocaleController.formatStartsTime((long) call.call.schedule_date, 4), false);
                    } else if (call.call.participants_count == 0) {
                        this.subtitleTextView.setText(LocaleController.getString(call.call.rtmp_stream ? NUM : NUM), false);
                    } else {
                        this.subtitleTextView.setText(LocaleController.formatPluralString(call.call.rtmp_stream ? "ViewersWatching" : "Participants", call.call.participants_count), false);
                    }
                }
                updateAvatars(true);
            }
        } else if (i == NotificationCenter.historyImportProgressChanged) {
            int i3 = this.currentStyle;
            if (i3 == 1 || i3 == 3 || i3 == 4) {
                checkCall(false);
            }
            checkImport(false);
        } else if (i == NotificationCenter.messagePlayingSpeedChanged) {
            updatePlaybackButton();
        } else if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
            if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
                this.micAmplitude = 0.0f;
            } else {
                this.micAmplitude = Math.min(8500.0f, args[0].floatValue() * 4000.0f) / 8500.0f;
            }
            if (VoIPService.getSharedInstance() != null) {
                Theme.getFragmentContextViewWavesDrawable().setAmplitude(Math.max(this.speakerAmplitude, this.micAmplitude));
            }
        } else if (i == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
            this.speakerAmplitude = Math.max(0.0f, Math.min((args[0].floatValue() * 15.0f) / 80.0f, 1.0f));
            if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
                this.micAmplitude = 0.0f;
            }
            if (VoIPService.getSharedInstance() != null) {
                Theme.getFragmentContextViewWavesDrawable().setAmplitude(Math.max(this.speakerAmplitude, this.micAmplitude));
            }
            this.avatars.invalidate();
        }
    }

    public int getStyleHeight() {
        return this.currentStyle == 4 ? 48 : 36;
    }

    public boolean isCallTypeVisible() {
        int i = this.currentStyle;
        return (i == 1 || i == 3) && this.visible;
    }

    private void checkLiveLocation(boolean create) {
        boolean show;
        String param;
        String str;
        View fragmentView = this.fragment.getFragmentView();
        if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            create = true;
        }
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            show = LocationController.getLocationsCount() != 0;
        } else {
            show = LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        }
        if (!show) {
            this.lastLocationSharingCount = -1;
            AndroidUtilities.cancelRunOnUIThread(this.checkLocationRunnable);
            if (this.visible) {
                this.visible = false;
                if (create) {
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
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
        if (create && this.topPadding == 0.0f) {
            setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
        }
        if (!this.visible) {
            if (!create) {
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
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
            String liveLocation = LocaleController.getString("LiveLocationContext", NUM);
            ArrayList<LocationController.SharingLocationInfo> infos = new ArrayList<>();
            for (int a = 0; a < 3; a++) {
                infos.addAll(LocationController.getInstance(a).sharingLocationsUI);
            }
            if (infos.size() == 1) {
                LocationController.SharingLocationInfo info = infos.get(0);
                long dialogId = info.messageObject.getDialogId();
                if (DialogObject.isUserDialog(dialogId)) {
                    param = UserObject.getFirstName(MessagesController.getInstance(info.messageObject.currentAccount).getUser(Long.valueOf(dialogId)));
                    str = LocaleController.getString("AttachLiveLocationIsSharing", NUM);
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(info.messageObject.currentAccount).getChat(Long.valueOf(-dialogId));
                    if (chat != null) {
                        param = chat.title;
                    } else {
                        param = "";
                    }
                    str = LocaleController.getString("AttachLiveLocationIsSharingChat", NUM);
                }
            } else {
                param = LocaleController.formatPluralString("Chats", infos.size());
                str = LocaleController.getString("AttachLiveLocationIsSharingChats", NUM);
            }
            String fullString = String.format(str, new Object[]{liveLocation, param});
            int start = fullString.indexOf(liveLocation);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(fullString);
            int i = 0;
            while (i < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                TextView textView = i == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                if (textView != null) {
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                }
                i++;
            }
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, getThemedColor("inappPlayerPerformer")), start, liveLocation.length() + start, 18);
            this.titleTextView.setText(stringBuilder, false);
            return;
        }
        this.checkLocationRunnable.run();
        checkLocationString();
    }

    /* access modifiers changed from: private */
    public void checkLocationString() {
        String fullString;
        ChatActivity chatActivity2;
        BaseFragment baseFragment = this.fragment;
        if ((baseFragment instanceof ChatActivity) && this.titleTextView != null) {
            ChatActivity chatActivity3 = (ChatActivity) baseFragment;
            long dialogId = chatActivity3.getDialogId();
            int currentAccount = chatActivity3.getCurrentAccount();
            ArrayList<TLRPC.Message> messages = LocationController.getInstance(currentAccount).locationsCache.get(dialogId);
            if (!this.firstLocationsLoaded) {
                LocationController.getInstance(currentAccount).loadLiveLocations(dialogId);
                this.firstLocationsLoaded = true;
            }
            int locationSharingCount = 0;
            TLRPC.User notYouUser = null;
            if (messages != null) {
                long currentUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                int date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                int a = 0;
                while (a < messages.size()) {
                    TLRPC.Message message = messages.get(a);
                    if (message.media == null) {
                        chatActivity2 = chatActivity3;
                    } else if (message.date + message.media.period > date) {
                        long fromId = MessageObject.getFromChatId(message);
                        if (notYouUser != null || fromId == currentUserId) {
                            chatActivity2 = chatActivity3;
                        } else {
                            chatActivity2 = chatActivity3;
                            notYouUser = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(fromId));
                        }
                        locationSharingCount++;
                    } else {
                        chatActivity2 = chatActivity3;
                    }
                    a++;
                    chatActivity3 = chatActivity2;
                }
            }
            if (this.lastLocationSharingCount != locationSharingCount) {
                this.lastLocationSharingCount = locationSharingCount;
                String liveLocation = LocaleController.getString("LiveLocationContext", NUM);
                if (locationSharingCount == 0) {
                    fullString = liveLocation;
                } else {
                    int otherSharingCount = locationSharingCount - 1;
                    if (LocationController.getInstance(currentAccount).isSharingLocation(dialogId)) {
                        if (otherSharingCount == 0) {
                            fullString = String.format("%1$s - %2$s", new Object[]{liveLocation, LocaleController.getString("ChatYourSelfName", NUM)});
                        } else if (otherSharingCount != 1 || notYouUser == null) {
                            fullString = String.format("%1$s - %2$s %3$s", new Object[]{liveLocation, LocaleController.getString("ChatYourSelfName", NUM), LocaleController.formatPluralString("AndOther", otherSharingCount)});
                        } else {
                            fullString = String.format("%1$s - %2$s", new Object[]{liveLocation, LocaleController.formatString("SharingYouAndOtherName", NUM, UserObject.getFirstName(notYouUser))});
                        }
                    } else if (otherSharingCount != 0) {
                        fullString = String.format("%1$s - %2$s %3$s", new Object[]{liveLocation, UserObject.getFirstName(notYouUser), LocaleController.formatPluralString("AndOther", otherSharingCount)});
                    } else {
                        fullString = String.format("%1$s - %2$s", new Object[]{liveLocation, UserObject.getFirstName(notYouUser)});
                    }
                }
                if (!fullString.equals(this.lastString)) {
                    this.lastString = fullString;
                    int start = fullString.indexOf(liveLocation);
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(fullString);
                    int i = 0;
                    while (i < 2) {
                        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                        TextView textView = i == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                        if (textView != null) {
                            textView.setEllipsize(TextUtils.TruncateAt.END);
                        }
                        i++;
                    }
                    if (start >= 0) {
                        stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, getThemedColor("inappPlayerPerformer")), start, liveLocation.length() + start, 18);
                    }
                    this.titleTextView.setText(stringBuilder, false);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkPlayer(boolean create) {
        SpannableStringBuilder stringBuilder;
        boolean z = true;
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
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        View fragmentView = this.fragment.getFragmentView();
        if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            create = true;
        }
        boolean wasVisible = this.visible;
        if (messageObject == null || messageObject.getId() == 0 || messageObject.isVideo()) {
            this.lastMessageObject = null;
            boolean callAvailable = this.supportsCalls && VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().isHangingUp() && VoIPService.getSharedInstance().getCallState() != 15 && !GroupCallPip.isShowing();
            if (!isPlayingVoice() && !callAvailable && (this.fragment instanceof ChatActivity) && !GroupCallPip.isShowing()) {
                ChatObject.Call call = ((ChatActivity) this.fragment).getGroupCall();
                callAvailable = call != null && call.shouldShowPanel();
            }
            if (callAvailable) {
                checkCall(false);
            } else if (this.visible) {
                ActionBarMenuItem actionBarMenuItem = this.playbackSpeedButton;
                if (actionBarMenuItem != null && actionBarMenuItem.isSubMenuShowing()) {
                    this.playbackSpeedButton.toggleSubMenu();
                }
                this.visible = false;
                if (create) {
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
                    public void onAnimationEnd(Animator animation) {
                        NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
        } else if (this.currentStyle == 0 || this.animatorSet == null || create) {
            int prevStyle = this.currentStyle;
            updateStyle(0);
            if (create && this.topPadding == 0.0f) {
                updatePaddings();
                setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
                FragmentContextViewDelegate fragmentContextViewDelegate2 = this.delegate;
                if (fragmentContextViewDelegate2 != null) {
                    fragmentContextViewDelegate2.onAnimation(true, true);
                    this.delegate.onAnimation(false, true);
                }
            }
            if (!this.visible) {
                if (!create) {
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
                        public void onAnimationEnd(Animator animation) {
                            NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
                this.playPauseDrawable.setPause(false, !create);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                this.playPauseDrawable.setPause(true, !create);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            if (this.lastMessageObject != messageObject || prevStyle != 0) {
                this.lastMessageObject = messageObject;
                if (messageObject.isVoice() || this.lastMessageObject.isRoundVideo()) {
                    this.isMusic = false;
                    ActionBarMenuItem actionBarMenuItem2 = this.playbackSpeedButton;
                    if (actionBarMenuItem2 != null) {
                        actionBarMenuItem2.setAlpha(1.0f);
                        this.playbackSpeedButton.setEnabled(true);
                    }
                    this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                    stringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                    int i2 = 0;
                    while (i2 < 2) {
                        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                        TextView textView = i2 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                        if (textView != null) {
                            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                        }
                        i2++;
                    }
                    updatePlaybackButton();
                } else {
                    this.isMusic = true;
                    if (this.playbackSpeedButton == null) {
                        this.titleTextView.setPadding(0, 0, 0, 0);
                    } else if (messageObject.getDuration() >= 600) {
                        this.playbackSpeedButton.setAlpha(1.0f);
                        this.playbackSpeedButton.setEnabled(true);
                        this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                        updatePlaybackButton();
                    } else {
                        this.playbackSpeedButton.setAlpha(0.0f);
                        this.playbackSpeedButton.setEnabled(false);
                        this.titleTextView.setPadding(0, 0, 0, 0);
                    }
                    stringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                    int i3 = 0;
                    while (i3 < 2) {
                        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = this.titleTextView;
                        TextView textView2 = i3 == 0 ? clippingTextViewSwitcher2.getTextView() : clippingTextViewSwitcher2.getNextTextView();
                        if (textView2 != null) {
                            textView2.setEllipsize(TextUtils.TruncateAt.END);
                        }
                        i3++;
                    }
                }
                stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, getThemedColor("inappPlayerPerformer")), 0, messageObject.getMusicAuthor().length(), 18);
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher3 = this.titleTextView;
                if (create || !wasVisible || !this.isMusic) {
                    z = false;
                }
                clippingTextViewSwitcher3.setText(stringBuilder, z);
            }
        } else {
            this.checkPlayerAfterAnimation = true;
        }
    }

    public void checkImport(boolean create) {
        int i;
        BaseFragment baseFragment = this.fragment;
        if (!(baseFragment instanceof ChatActivity)) {
            return;
        }
        if (!this.visible || !((i = this.currentStyle) == 1 || i == 3)) {
            ChatActivity chatActivity2 = (ChatActivity) baseFragment;
            SendMessagesHelper.ImportingHistory importingHistory = chatActivity2.getSendMessagesHelper().getImportingHistory(chatActivity2.getDialogId());
            View fragmentView = this.fragment.getFragmentView();
            if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
                create = true;
            }
            Dialog dialog = chatActivity2.getVisibleDialog();
            if ((isPlayingVoice() || chatActivity2.shouldShowImport() || ((dialog instanceof ImportingAlert) && !((ImportingAlert) dialog).isDismissed())) && importingHistory != null) {
                importingHistory = null;
            }
            if (importingHistory == null) {
                if (!this.visible || ((!create || this.currentStyle != -1) && this.currentStyle != 5)) {
                    int i2 = this.currentStyle;
                    if (i2 == -1 || i2 == 5) {
                        this.visible = false;
                        setVisibility(8);
                        return;
                    }
                    return;
                }
                this.visible = false;
                if (create) {
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
                final int currentAccount = this.account;
                this.animationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(220);
                this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        NotificationCenter.getInstance(currentAccount).onAnimationFinish(FragmentContextView.this.animationIndex);
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
            } else if (this.currentStyle == 5 || this.animatorSet == null || create) {
                updateStyle(5);
                if (create && this.topPadding == 0.0f) {
                    updatePaddings();
                    setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
                    FragmentContextViewDelegate fragmentContextViewDelegate = this.delegate;
                    if (fragmentContextViewDelegate != null) {
                        fragmentContextViewDelegate.onAnimation(true, true);
                        this.delegate.onAnimation(false, true);
                    }
                }
                if (!this.visible) {
                    if (!create) {
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
                            public void onAnimationEnd(Animator animation) {
                                NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                                if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
                if (this.currentProgress != importingHistory.uploadProgress) {
                    this.currentProgress = importingHistory.uploadProgress;
                    this.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ImportUploading", NUM, Integer.valueOf(importingHistory.uploadProgress))), false);
                }
            } else {
                this.checkImportAfterAnimation = true;
            }
        }
    }

    private boolean isPlayingVoice() {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        return messageObject != null && messageObject.isVoice();
    }

    public void checkCall(boolean create) {
        boolean create2;
        boolean groupActive;
        boolean callAvailable;
        int newStyle;
        boolean z;
        int[] iArr;
        int i;
        int[] iArr2;
        int i2;
        ChatObject.Call call;
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (!this.visible || this.currentStyle != 5 || (voIPService != null && !voIPService.isHangingUp())) {
            View fragmentView = this.fragment.getFragmentView();
            if (create || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0)) {
                create2 = create;
            } else {
                create2 = true;
            }
            if (GroupCallPip.isShowing()) {
                callAvailable = false;
                groupActive = false;
            } else {
                callAvailable = !GroupCallActivity.groupCallUiVisible && this.supportsCalls && voIPService != null && !voIPService.isHangingUp();
                if (!(voIPService == null || voIPService.groupCall == null || !(voIPService.groupCall.call instanceof TLRPC.TL_groupCallDiscarded))) {
                    callAvailable = false;
                }
                groupActive = false;
                if (!isPlayingVoice() && !GroupCallActivity.groupCallUiVisible && this.supportsCalls && !callAvailable) {
                    BaseFragment baseFragment = this.fragment;
                    if ((baseFragment instanceof ChatActivity) && (call = ((ChatActivity) baseFragment).getGroupCall()) != null && call.shouldShowPanel()) {
                        callAvailable = true;
                        groupActive = true;
                    }
                }
            }
            if (!callAvailable) {
                boolean z2 = this.visible;
                if (z2 && ((create2 && this.currentStyle == -1) || (i2 = this.currentStyle) == 4 || i2 == 3 || i2 == 1)) {
                    this.visible = false;
                    if (create2) {
                        if (getVisibility() != 8) {
                            setVisibility(8);
                        }
                        setTopPadding(0.0f);
                    } else {
                        AnimatorSet animatorSet2 = this.animatorSet;
                        if (animatorSet2 != null) {
                            animatorSet2.cancel();
                            iArr2 = null;
                            this.animatorSet = null;
                        } else {
                            iArr2 = null;
                        }
                        final int currentAccount = this.account;
                        this.animationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(this.animationIndex, iArr2);
                        AnimatorSet animatorSet3 = new AnimatorSet();
                        this.animatorSet = animatorSet3;
                        animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                        this.animatorSet.setDuration(220);
                        this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                NotificationCenter.getInstance(currentAccount).onAnimationFinish(FragmentContextView.this.animationIndex);
                                if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
                    }
                } else if (z2 && ((i = this.currentStyle) == -1 || i == 4 || i == 3 || i == 1)) {
                    this.visible = false;
                    setVisibility(8);
                }
                if (create2) {
                    BaseFragment baseFragment2 = this.fragment;
                    if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).openedWithLivestream() && !GroupCallPip.isShowing()) {
                        BulletinFactory.of(this.fragment).createSimpleBulletin(NUM, LocaleController.getString("InviteExpired", NUM)).show();
                        boolean z3 = groupActive;
                        return;
                    }
                }
                boolean z4 = groupActive;
                return;
            }
            if (groupActive) {
                newStyle = 4;
            } else if (voIPService.groupCall != null) {
                newStyle = 3;
            } else {
                newStyle = 1;
            }
            int i3 = this.currentStyle;
            if (newStyle != i3 && this.animatorSet != null && !create2) {
                this.checkCallAfterAnimation = true;
            } else if (newStyle == i3 || !this.visible || create2) {
                if (groupActive) {
                    boolean updateAnimated = i3 == 4 && this.visible;
                    updateStyle(4);
                    ChatObject.Call call2 = ((ChatActivity) this.fragment).getGroupCall();
                    TLRPC.Chat chat = ((ChatActivity) this.fragment).getCurrentChat();
                    if (call2.isScheduled()) {
                        if (this.gradientPaint == null) {
                            TextPaint textPaint = new TextPaint(1);
                            this.gradientTextPaint = textPaint;
                            textPaint.setColor(-1);
                            this.gradientTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
                            this.gradientTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                            Paint paint = new Paint(1);
                            this.gradientPaint = paint;
                            paint.setColor(-1);
                            this.matrix = new Matrix();
                        }
                        this.joinButton.setVisibility(8);
                        if (!TextUtils.isEmpty(call2.call.title)) {
                            z = false;
                            this.titleTextView.setText(call2.call.title, false);
                        } else {
                            z = false;
                            if (ChatObject.isChannelOrGiga(chat)) {
                                this.titleTextView.setText(LocaleController.getString("VoipChannelScheduledVoiceChat", NUM), false);
                            } else {
                                this.titleTextView.setText(LocaleController.getString("VoipGroupScheduledVoiceChat", NUM), false);
                            }
                        }
                        boolean z5 = groupActive;
                        int i4 = newStyle;
                        this.subtitleTextView.setText(LocaleController.formatStartsTime((long) call2.call.schedule_date, 4), z);
                        if (!this.scheduleRunnableScheduled) {
                            this.scheduleRunnableScheduled = true;
                            this.updateScheduleTimeRunnable.run();
                        }
                    } else {
                        int i5 = newStyle;
                        this.timeLayout = null;
                        this.joinButton.setVisibility(0);
                        if (call2.call.rtmp_stream) {
                            this.titleTextView.setText(LocaleController.getString(NUM), false);
                        } else if (ChatObject.isChannelOrGiga(chat)) {
                            this.titleTextView.setText(LocaleController.getString("VoipChannelVoiceChat", NUM), false);
                        } else {
                            this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM), false);
                        }
                        if (call2.call.participants_count == 0) {
                            this.subtitleTextView.setText(LocaleController.getString(call2.call.rtmp_stream ? NUM : NUM), false);
                        } else {
                            this.subtitleTextView.setText(LocaleController.formatPluralString(call2.call.rtmp_stream ? "ViewersWatching" : "Participants", call2.call.participants_count), false);
                        }
                        this.frameLayout.invalidate();
                    }
                    updateAvatars(this.avatars.avatarsDarawable.wasDraw && updateAnimated);
                } else {
                    int i6 = newStyle;
                    if (voIPService == null || voIPService.groupCall == null) {
                        updateAvatars(this.currentStyle == 1);
                        updateStyle(1);
                    } else {
                        updateAvatars(this.currentStyle == 3);
                        updateStyle(3);
                    }
                }
                if (!this.visible) {
                    if (!create2) {
                        AnimatorSet animatorSet4 = this.animatorSet;
                        if (animatorSet4 != null) {
                            animatorSet4.cancel();
                            this.animatorSet = null;
                        }
                        this.animatorSet = new AnimatorSet();
                        FragmentContextView fragmentContextView = this.additionalContextView;
                        if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) getStyleHeight());
                        } else {
                            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) (getStyleHeight() + this.additionalContextView.getStyleHeight()));
                        }
                        final int currentAccount2 = this.account;
                        this.animationIndex = NotificationCenter.getInstance(currentAccount2).setAnimationInProgress(this.animationIndex, new int[]{NotificationCenter.messagesDidLoad});
                        this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2((float) getStyleHeight())})});
                        this.animatorSet.setDuration(220);
                        this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                NotificationCenter.getInstance(currentAccount2).onAnimationFinish(FragmentContextView.this.animationIndex);
                                if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                    AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                                }
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
                                FragmentContextView.this.startJoinFlickerAnimation();
                            }
                        });
                        this.animatorSet.start();
                    } else {
                        updatePaddings();
                        setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
                        startJoinFlickerAnimation();
                    }
                    this.visible = true;
                    setVisibility(0);
                }
            } else {
                AnimatorSet animatorSet5 = this.animatorSet;
                if (animatorSet5 != null) {
                    animatorSet5.cancel();
                    iArr = null;
                    this.animatorSet = null;
                } else {
                    iArr = null;
                }
                final int currentAccount3 = this.account;
                this.animationIndex = NotificationCenter.getInstance(currentAccount3).setAnimationInProgress(this.animationIndex, iArr);
                AnimatorSet animatorSet6 = new AnimatorSet();
                this.animatorSet = animatorSet6;
                animatorSet6.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(220);
                this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        NotificationCenter.getInstance(currentAccount3).onAnimationFinish(FragmentContextView.this.animationIndex);
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            boolean unused = FragmentContextView.this.visible = false;
                            AnimatorSet unused2 = FragmentContextView.this.animatorSet = null;
                            FragmentContextView.this.checkCall(false);
                        }
                    }
                });
                this.animatorSet.start();
            }
        }
    }

    /* access modifiers changed from: private */
    public void startJoinFlickerAnimation() {
        if (this.joinButtonFlicker.getProgress() > 1.0f) {
            AndroidUtilities.runOnUIThread(new FragmentContextView$$ExternalSyntheticLambda1(this), 150);
        }
    }

    /* renamed from: lambda$startJoinFlickerAnimation$12$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m4031x92aab17d() {
        this.joinButtonFlicker.setProgress(0.0f);
        this.joinButton.invalidate();
    }

    private void updateAvatars(boolean animated) {
        TLRPC.User userCall;
        ChatObject.Call call;
        int currentAccount;
        boolean z = animated;
        if (!z && this.avatars.avatarsDarawable.transitionProgressAnimator != null) {
            this.avatars.avatarsDarawable.transitionProgressAnimator.cancel();
            this.avatars.avatarsDarawable.transitionProgressAnimator = null;
        }
        if (this.avatars.avatarsDarawable.transitionProgressAnimator == null) {
            if (this.currentStyle == 4) {
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    ChatActivity chatActivity2 = (ChatActivity) baseFragment;
                    call = chatActivity2.getGroupCall();
                    currentAccount = chatActivity2.getCurrentAccount();
                } else {
                    call = null;
                    currentAccount = this.account;
                }
                userCall = null;
            } else if (VoIPService.getSharedInstance() != null) {
                call = VoIPService.getSharedInstance().groupCall;
                userCall = this.fragment instanceof ChatActivity ? null : VoIPService.getSharedInstance().getUser();
                currentAccount = VoIPService.getSharedInstance().getAccount();
            } else {
                call = null;
                userCall = null;
                currentAccount = this.account;
            }
            int i = 0;
            if (call != null) {
                int N = call.sortedParticipants.size();
                for (int a = 0; a < 3; a++) {
                    if (a < N) {
                        this.avatars.setObject(a, currentAccount, call.sortedParticipants.get(a));
                    } else {
                        this.avatars.setObject(a, currentAccount, (TLObject) null);
                    }
                }
            } else if (userCall != null) {
                this.avatars.setObject(0, currentAccount, userCall);
                for (int a2 = 1; a2 < 3; a2++) {
                    this.avatars.setObject(a2, currentAccount, (TLObject) null);
                }
            } else {
                for (int a3 = 0; a3 < 3; a3++) {
                    this.avatars.setObject(a3, currentAccount, (TLObject) null);
                }
            }
            this.avatars.commitTransition(z);
            if (this.currentStyle == 4 && call != null) {
                if (!call.call.rtmp_stream) {
                    i = Math.min(3, call.sortedParticipants.size());
                }
                int N2 = i;
                int x = 10;
                if (N2 != 0) {
                    x = 10 + ((N2 - 1) * 24) + 10 + 32;
                }
                if (z) {
                    int leftMargin = ((FrameLayout.LayoutParams) this.titleTextView.getLayoutParams()).leftMargin;
                    if (AndroidUtilities.dp((float) x) != leftMargin) {
                        float dx = (this.titleTextView.getTranslationX() + ((float) leftMargin)) - ((float) AndroidUtilities.dp((float) x));
                        this.titleTextView.setTranslationX(dx);
                        this.subtitleTextView.setTranslationX(dx);
                        this.titleTextView.animate().translationX(0.0f).setDuration(220).setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.subtitleTextView.animate().translationX(0.0f).setDuration(220).setInterpolator(CubicBezierInterpolator.DEFAULT);
                    }
                } else {
                    this.titleTextView.animate().cancel();
                    this.subtitleTextView.animate().cancel();
                    this.titleTextView.setTranslationX(0.0f);
                    this.subtitleTextView.setTranslationX(0.0f);
                }
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, (float) x, 5.0f, call.isScheduled() ? 90.0f : 36.0f, 0.0f));
                this.subtitleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, (float) x, 25.0f, call.isScheduled() ? 90.0f : 36.0f, 0.0f));
                return;
            }
            return;
        }
        this.avatars.updateAfterTransitionEnd();
    }

    public void setCollapseTransition(boolean show, float extraHeight2, float progress) {
        this.collapseTransition = show;
        this.extraHeight = extraHeight2;
        this.collapseProgress = progress;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (!this.drawOverlay || getVisibility() == 0) {
            boolean clipped = false;
            int i = this.currentStyle;
            if ((i == 3 || i == 1) && this.drawOverlay) {
                if (GroupCallActivity.groupCallInstance == null && Theme.getFragmentContextViewWavesDrawable().getState() == 3) {
                }
                Theme.getFragmentContextViewWavesDrawable().updateState(this.wasDraw);
                float progress = this.topPadding / ((float) AndroidUtilities.dp((float) getStyleHeight()));
                if (this.collapseTransition) {
                    Canvas canvas2 = canvas;
                    Theme.getFragmentContextViewWavesDrawable().draw(0.0f, this.extraHeight + (((float) AndroidUtilities.dp((float) getStyleHeight())) - this.topPadding), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)), canvas2, (FragmentContextView) null, Math.min(progress, 1.0f - this.collapseProgress));
                } else {
                    Theme.getFragmentContextViewWavesDrawable().draw(0.0f, ((float) AndroidUtilities.dp((float) getStyleHeight())) - this.topPadding, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)), canvas, this, progress);
                }
                float clipTop = ((float) AndroidUtilities.dp((float) getStyleHeight())) - this.topPadding;
                if (this.collapseTransition) {
                    clipTop += this.extraHeight;
                }
                if (clipTop <= ((float) getMeasuredHeight())) {
                    clipped = true;
                    canvas.save();
                    canvas.clipRect(0.0f, clipTop, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    invalidate();
                } else {
                    return;
                }
            }
            super.dispatchDraw(canvas);
            if (clipped) {
                canvas.restore();
            }
            this.wasDraw = true;
        }
    }

    public void setDrawOverlay(boolean drawOverlay2) {
        this.drawOverlay = drawOverlay2;
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

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        updatePaddings();
        setTopPadding(this.topPadding);
        if (visibility == 8) {
            this.wasDraw = false;
        }
    }

    private void updatePaddings() {
        int margin = 0;
        if (getVisibility() == 0) {
            margin = 0 - AndroidUtilities.dp((float) getStyleHeight());
        }
        FragmentContextView fragmentContextView = this.additionalContextView;
        if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = margin;
            return;
        }
        int margin2 = margin - AndroidUtilities.dp((float) this.additionalContextView.getStyleHeight());
        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = margin2;
        ((FrameLayout.LayoutParams) this.additionalContextView.getLayoutParams()).topMargin = margin2;
    }

    public void onStateChanged(int state) {
        updateCallTitle();
    }

    private void updateCallTitle() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            int i = this.currentStyle;
            if (i == 1 || i == 3) {
                int currentCallState = service.getCallState();
                if (!service.isSwitchingStream() && (currentCallState == 1 || currentCallState == 2 || currentCallState == 6 || currentCallState == 5)) {
                    this.titleTextView.setText(LocaleController.getString("VoipGroupConnecting", NUM), false);
                } else if (service.getChat() != null) {
                    if (!TextUtils.isEmpty(service.groupCall.call.title)) {
                        this.titleTextView.setText(service.groupCall.call.title, false);
                        return;
                    }
                    BaseFragment baseFragment = this.fragment;
                    if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).getCurrentChat() == null || ((ChatActivity) this.fragment).getCurrentChat().id != service.getChat().id) {
                        this.titleTextView.setText(service.getChat().title, false);
                        return;
                    }
                    TLRPC.Chat chat = ((ChatActivity) this.fragment).getCurrentChat();
                    if (VoIPService.hasRtmpStream()) {
                        this.titleTextView.setText(LocaleController.getString(NUM), false);
                    } else if (ChatObject.isChannelOrGiga(chat)) {
                        this.titleTextView.setText(LocaleController.getString("VoipChannelViewVoiceChat", NUM), false);
                    } else {
                        this.titleTextView.setText(LocaleController.getString("VoipGroupViewVoiceChat", NUM), false);
                    }
                } else if (service.getUser() != null) {
                    TLRPC.User user = service.getUser();
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
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
