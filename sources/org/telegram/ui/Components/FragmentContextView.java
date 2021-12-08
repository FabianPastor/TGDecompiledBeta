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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FragmentContextView(Context context, BaseFragment parentFragment, View paddingView, boolean location, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        float f;
        final Context context2 = context;
        BaseFragment baseFragment = parentFragment;
        View view = paddingView;
        boolean z = location;
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
                ChatObject.Call call = ((ChatActivity) FragmentContextView.this.fragment).getGroupCall();
                if (call == null || !call.isScheduled()) {
                    StaticLayout unused2 = FragmentContextView.this.timeLayout = null;
                    boolean unused3 = FragmentContextView.this.scheduleRunnableScheduled = false;
                    return;
                }
                int currentTime = FragmentContextView.this.fragment.getConnectionsManager().getCurrentTime();
                int diff = call.call.schedule_date - currentTime;
                if (diff >= 86400) {
                    str = LocaleController.formatPluralString("Days", Math.round(((float) diff) / 86400.0f));
                } else {
                    str = AndroidUtilities.formatFullDuration(call.call.schedule_date - currentTime);
                }
                StaticLayout unused4 = FragmentContextView.this.timeLayout = new StaticLayout(str, FragmentContextView.this.gradientTextPaint, (int) Math.ceil((double) FragmentContextView.this.gradientTextPaint.measureText(str)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
        this.fragment = baseFragment;
        this.applyingView = view;
        this.visible = true;
        this.isLocation = z;
        if (view == null) {
            ((ViewGroup) parentFragment.getFragmentView()).setClipToPadding(false);
        }
        setTag(1);
        AnonymousClass3 r2 = new FrameLayout(context2) {
            public void invalidate() {
                super.invalidate();
                if (FragmentContextView.this.avatars != null && FragmentContextView.this.avatars.getVisibility() == 0) {
                    FragmentContextView.this.avatars.invalidate();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (FragmentContextView.this.currentStyle == 4 && FragmentContextView.this.timeLayout != null) {
                    int width = ((int) Math.ceil((double) FragmentContextView.this.timeLayout.getLineWidth(0))) + AndroidUtilities.dp(24.0f);
                    if (width != FragmentContextView.this.gradientWidth) {
                        LinearGradient unused = FragmentContextView.this.linearGradient = new LinearGradient(0.0f, 0.0f, 1.7f * ((float) width), 0.0f, new int[]{-10187532, -7575089, -2860679, -2860679}, new float[]{0.0f, 0.294f, 0.588f, 1.0f}, Shader.TileMode.CLAMP);
                        FragmentContextView.this.gradientPaint.setShader(FragmentContextView.this.linearGradient);
                        int unused2 = FragmentContextView.this.gradientWidth = width;
                    }
                    ChatObject.Call call = ((ChatActivity) FragmentContextView.this.fragment).getGroupCall();
                    float moveProgress = 0.0f;
                    if (!(FragmentContextView.this.fragment == null || call == null || !call.isScheduled())) {
                        long diff = (((long) call.call.schedule_date) * 1000) - FragmentContextView.this.fragment.getConnectionsManager().getCurrentTimeMillis();
                        if (diff < 0) {
                            moveProgress = 1.0f;
                        } else if (diff < 5000) {
                            moveProgress = 1.0f - (((float) diff) / 5000.0f);
                        }
                        if (diff < 6000) {
                            invalidate();
                        }
                    }
                    FragmentContextView.this.matrix.reset();
                    FragmentContextView.this.matrix.postTranslate(((float) (-FragmentContextView.this.gradientWidth)) * 0.7f * moveProgress, 0.0f);
                    FragmentContextView.this.linearGradient.setLocalMatrix(FragmentContextView.this.matrix);
                    int y = AndroidUtilities.dp(12.0f);
                    FragmentContextView.this.rect.set(0.0f, 0.0f, (float) width, (float) AndroidUtilities.dp(24.0f));
                    canvas.save();
                    canvas.translate((float) ((getMeasuredWidth() - width) - AndroidUtilities.dp(10.0f)), (float) y);
                    canvas.drawRoundRect(FragmentContextView.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), FragmentContextView.this.gradientPaint);
                    canvas.translate((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(4.0f));
                    FragmentContextView.this.timeLayout.draw(canvas);
                    canvas.restore();
                }
            }
        };
        this.frameLayout = r2;
        addView(r2, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view2 = new View(context2);
        this.selector = view2;
        this.frameLayout.addView(view2, LayoutHelper.createFrame(-1, -1.0f));
        View view3 = new View(context2);
        this.shadow = view3;
        view3.setBackgroundResource(NUM);
        addView(this.shadow, LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.playButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("inappPlayerPlayPause"), PorterDuff.Mode.MULTIPLY));
        ImageView imageView2 = this.playButton;
        PlayPauseDrawable playPauseDrawable2 = new PlayPauseDrawable(14);
        this.playPauseDrawable = playPauseDrawable2;
        imageView2.setImageDrawable(playPauseDrawable2);
        if (Build.VERSION.SDK_INT >= 21) {
            this.playButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("inappPlayerPlayPause") & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda5(this));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.importingImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.importingImageView.setAutoRepeat(true);
        this.importingImageView.setAnimation(NUM, 30, 30);
        this.importingImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(22.0f), getThemedColor("inappPlayerPlayPause")));
        addView(this.importingImageView, LayoutHelper.createFrame(22, 22.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        AnonymousClass4 r1 = new AudioPlayerAlert.ClippingTextViewSwitcher(context2) {
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
        this.titleTextView = r1;
        addView(r1, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        AnonymousClass5 r12 = new AudioPlayerAlert.ClippingTextViewSwitcher(context2) {
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
        this.subtitleTextView = r12;
        addView(r12, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 10.0f, 36.0f, 0.0f));
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
        this.joinButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda6(this));
        if (!z) {
            f = 14.0f;
            ActionBarMenuItem actionBarMenuItem = r1;
            ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, getThemedColor("dialogTextBlack"), resourcesProvider2);
            this.playbackSpeedButton = actionBarMenuItem;
            actionBarMenuItem.setLongClickEnabled(false);
            this.playbackSpeedButton.setShowSubmenuByMove(false);
            this.playbackSpeedButton.setContentDescription(LocaleController.getString("AccDescrPlayerSpeed", NUM));
            this.playbackSpeedButton.setDelegate(new FragmentContextView$$ExternalSyntheticLambda1(this));
            this.speedItems[0] = this.playbackSpeedButton.addSubItem(1, NUM, (CharSequence) LocaleController.getString("SpeedSlow", NUM));
            this.speedItems[1] = this.playbackSpeedButton.addSubItem(2, NUM, (CharSequence) LocaleController.getString("SpeedNormal", NUM));
            this.speedItems[2] = this.playbackSpeedButton.addSubItem(3, NUM, (CharSequence) LocaleController.getString("SpeedFast", NUM));
            this.speedItems[3] = this.playbackSpeedButton.addSubItem(4, NUM, (CharSequence) LocaleController.getString("SpeedVeryFast", NUM));
            if (AndroidUtilities.density >= 3.0f) {
                this.playbackSpeedButton.setPadding(0, 1, 0, 0);
            }
            this.playbackSpeedButton.setAdditionalXOffset(AndroidUtilities.dp(8.0f));
            addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
            this.playbackSpeedButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda7(this));
            this.playbackSpeedButton.setOnLongClickListener(new FragmentContextView$$ExternalSyntheticLambda11(this));
            updatePlaybackButton();
        } else {
            f = 14.0f;
        }
        AvatarsImageView avatarsImageView = new AvatarsImageView(context2, false);
        this.avatars = avatarsImageView;
        avatarsImageView.setDelegate(new FragmentContextView$$ExternalSyntheticLambda12(this));
        this.avatars.setVisibility(8);
        addView(this.avatars, LayoutHelper.createFrame(108, 36, 51));
        this.muteDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), true, (int[]) null);
        AnonymousClass6 r13 = new RLottieImageView(context2) {
            private final Runnable pressRunnable = new FragmentContextView$6$$ExternalSyntheticLambda1(this);
            boolean pressed;
            boolean scheduled;
            private final Runnable toggleMicRunnable = new FragmentContextView$6$$ExternalSyntheticLambda0(this);

            /* renamed from: lambda$$0$org-telegram-ui-Components-FragmentContextView$6  reason: not valid java name */
            public /* synthetic */ void m2311lambda$$0$orgtelegramuiComponentsFragmentContextView$6() {
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

            /* renamed from: lambda$$1$org-telegram-ui-Components-FragmentContextView$6  reason: not valid java name */
            public /* synthetic */ void m2312lambda$$1$orgtelegramuiComponentsFragmentContextView$6() {
                if (this.scheduled && VoIPService.getSharedInstance() != null) {
                    this.scheduled = false;
                    this.pressed = true;
                    boolean unused = FragmentContextView.this.isMuted = false;
                    AndroidUtilities.runOnUIThread(this.toggleMicRunnable, 90);
                    FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
                }
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (FragmentContextView.this.currentStyle != 3 && FragmentContextView.this.currentStyle != 1) {
                    return super.onTouchEvent(event);
                }
                VoIPService service = VoIPService.getSharedInstance();
                if (service == null) {
                    AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                    AndroidUtilities.cancelRunOnUIThread(this.toggleMicRunnable);
                    this.scheduled = false;
                    this.pressed = false;
                    return true;
                }
                if (event.getAction() == 0 && service.isMicMute()) {
                    AndroidUtilities.runOnUIThread(this.pressRunnable, 300);
                    this.scheduled = true;
                } else if (event.getAction() == 1 || event.getAction() == 3) {
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
                        MotionEvent cancel = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                        super.onTouchEvent(cancel);
                        cancel.recycle();
                        return true;
                    }
                }
                return super.onTouchEvent(event);
            }

            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                String str;
                int i;
                super.onInitializeAccessibilityNodeInfo(info);
                info.setClassName(Button.class.getName());
                if (FragmentContextView.this.isMuted) {
                    i = NUM;
                    str = "VoipUnmute";
                } else {
                    i = NUM;
                    str = "VoipMute";
                }
                info.setText(LocaleController.getString(str, i));
            }
        };
        this.muteButton = r13;
        r13.setColorFilter(new PorterDuffColorFilter(getThemedColor("returnToCallText"), PorterDuff.Mode.MULTIPLY));
        if (Build.VERSION.SDK_INT >= 21) {
            this.muteButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(f)));
        }
        this.muteButton.setAnimation(this.muteDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setVisibility(8);
        addView(this.muteButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.muteButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda8(this));
        ImageView imageView3 = new ImageView(context2);
        this.closeButton = imageView3;
        imageView3.setImageResource(NUM);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("inappPlayerClose"), PorterDuff.Mode.MULTIPLY));
        if (Build.VERSION.SDK_INT >= 21) {
            this.closeButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(f)));
        }
        this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.closeButton.setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda9(this, resourcesProvider3));
        setOnClickListener(new FragmentContextView$$ExternalSyntheticLambda10(this, resourcesProvider3, baseFragment));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m2300lambda$new$0$orgtelegramuiComponentsFragmentContextView(View v) {
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
    public /* synthetic */ void m2301lambda$new$1$orgtelegramuiComponentsFragmentContextView(View v) {
        callOnClick();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m2303lambda$new$2$orgtelegramuiComponentsFragmentContextView(int id) {
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
    public /* synthetic */ void m2304lambda$new$3$orgtelegramuiComponentsFragmentContextView(View v) {
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
    public /* synthetic */ boolean m2305lambda$new$4$orgtelegramuiComponentsFragmentContextView(View view) {
        this.playbackSpeedButton.toggleSubMenu();
        return true;
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m2306lambda$new$5$orgtelegramuiComponentsFragmentContextView() {
        updateAvatars(true);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m2307lambda$new$6$orgtelegramuiComponentsFragmentContextView(View v) {
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
    public /* synthetic */ void m2309lambda$new$8$orgtelegramuiComponentsFragmentContextView(Theme.ResourcesProvider resourcesProvider2, View v) {
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
    public /* synthetic */ void m2308lambda$new$7$orgtelegramuiComponentsFragmentContextView(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            for (int a = 0; a < 3; a++) {
                LocationController.getInstance(a).removeAllLocationSharings();
            }
            return;
        }
        LocationController.getInstance(baseFragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) this.fragment).getDialogId());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:55:0x015d, code lost:
        r0 = (org.telegram.ui.ChatActivity) r11.fragment;
     */
    /* renamed from: lambda$new$10$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m2302lambda$new$10$orgtelegramuiComponentsFragmentContextView(org.telegram.ui.ActionBar.Theme.ResourcesProvider r12, org.telegram.ui.ActionBar.BaseFragment r13, android.view.View r14) {
        /*
            r11 = this;
            int r0 = r11.currentStyle
            if (r0 != 0) goto L_0x009b
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r0 = r0.getPlayingMessageObject()
            org.telegram.ui.ActionBar.BaseFragment r1 = r11.fragment
            if (r1 == 0) goto L_0x0099
            if (r0 == 0) goto L_0x0099
            boolean r1 = r0.isMusic()
            if (r1 == 0) goto L_0x002f
            android.content.Context r1 = r11.getContext()
            boolean r1 = r1 instanceof org.telegram.ui.LaunchActivity
            if (r1 == 0) goto L_0x0099
            org.telegram.ui.ActionBar.BaseFragment r1 = r11.fragment
            org.telegram.ui.Components.AudioPlayerAlert r2 = new org.telegram.ui.Components.AudioPlayerAlert
            android.content.Context r3 = r11.getContext()
            r2.<init>(r3, r12)
            r1.showDialog(r2)
            goto L_0x0099
        L_0x002f:
            r1 = 0
            org.telegram.ui.ActionBar.BaseFragment r3 = r11.fragment
            boolean r4 = r3 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x003d
            org.telegram.ui.ChatActivity r3 = (org.telegram.ui.ChatActivity) r3
            long r1 = r3.getDialogId()
        L_0x003d:
            long r3 = r0.getDialogId()
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 != 0) goto L_0x0057
            org.telegram.ui.ActionBar.BaseFragment r3 = r11.fragment
            r4 = r3
            org.telegram.ui.ChatActivity r4 = (org.telegram.ui.ChatActivity) r4
            int r5 = r0.getId()
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 1
            r10 = 0
            r4.scrollToMessageId(r5, r6, r7, r8, r9, r10)
            goto L_0x0099
        L_0x0057:
            long r1 = r0.getDialogId()
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
            if (r4 == 0) goto L_0x0070
            int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r1)
            java.lang.String r5 = "enc_id"
            r3.putInt(r5, r4)
            goto L_0x0082
        L_0x0070:
            boolean r4 = org.telegram.messenger.DialogObject.isUserDialog(r1)
            if (r4 == 0) goto L_0x007c
            java.lang.String r4 = "user_id"
            r3.putLong(r4, r1)
            goto L_0x0082
        L_0x007c:
            long r4 = -r1
            java.lang.String r6 = "chat_id"
            r3.putLong(r6, r4)
        L_0x0082:
            int r4 = r0.getId()
            java.lang.String r5 = "message_id"
            r3.putInt(r5, r4)
            org.telegram.ui.ActionBar.BaseFragment r4 = r11.fragment
            org.telegram.ui.ChatActivity r5 = new org.telegram.ui.ChatActivity
            r5.<init>(r3)
            org.telegram.ui.ActionBar.BaseFragment r6 = r11.fragment
            boolean r6 = r6 instanceof org.telegram.ui.ChatActivity
            r4.presentFragment(r5, r6)
        L_0x0099:
            goto L_0x01be
        L_0x009b:
            r1 = 1
            if (r0 != r1) goto L_0x00b8
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = r11.getContext()
            java.lang.Class<org.telegram.ui.LaunchActivity> r2 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r1, r2)
            java.lang.String r1 = "voip"
            android.content.Intent r0 = r0.setAction(r1)
            android.content.Context r1 = r11.getContext()
            r1.startActivity(r0)
            goto L_0x01be
        L_0x00b8:
            r2 = 2
            r3 = 0
            r4 = 3
            if (r0 != r2) goto L_0x0126
            r5 = 0
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.ui.ActionBar.BaseFragment r2 = r11.fragment
            boolean r7 = r2 instanceof org.telegram.ui.ChatActivity
            if (r7 == 0) goto L_0x00d4
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            long r5 = r2.getDialogId()
            org.telegram.ui.ActionBar.BaseFragment r1 = r11.fragment
            int r0 = r1.getCurrentAccount()
            goto L_0x00ff
        L_0x00d4:
            int r2 = org.telegram.messenger.LocationController.getLocationsCount()
            if (r2 != r1) goto L_0x00ff
            r1 = 0
        L_0x00db:
            if (r1 >= r4) goto L_0x00ff
            org.telegram.messenger.LocationController r2 = org.telegram.messenger.LocationController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.LocationController$SharingLocationInfo> r2 = r2.sharingLocationsUI
            boolean r7 = r2.isEmpty()
            if (r7 != 0) goto L_0x00fc
            org.telegram.messenger.LocationController r4 = org.telegram.messenger.LocationController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.LocationController$SharingLocationInfo> r4 = r4.sharingLocationsUI
            java.lang.Object r3 = r4.get(r3)
            org.telegram.messenger.LocationController$SharingLocationInfo r3 = (org.telegram.messenger.LocationController.SharingLocationInfo) r3
            long r5 = r3.did
            org.telegram.messenger.MessageObject r4 = r3.messageObject
            int r0 = r4.currentAccount
            goto L_0x00ff
        L_0x00fc:
            int r1 = r1 + 1
            goto L_0x00db
        L_0x00ff:
            r1 = 0
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0111
            org.telegram.messenger.LocationController r1 = org.telegram.messenger.LocationController.getInstance(r0)
            org.telegram.messenger.LocationController$SharingLocationInfo r1 = r1.getSharingLocationInfo(r5)
            r11.openSharingLocation(r1)
            goto L_0x0124
        L_0x0111:
            org.telegram.ui.ActionBar.BaseFragment r1 = r11.fragment
            org.telegram.ui.Components.SharingLocationsAlert r2 = new org.telegram.ui.Components.SharingLocationsAlert
            android.content.Context r3 = r11.getContext()
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda2
            r4.<init>(r11)
            r2.<init>(r3, r4, r12)
            r1.showDialog(r2)
        L_0x0124:
            goto L_0x01be
        L_0x0126:
            if (r0 != r4) goto L_0x0151
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r0 == 0) goto L_0x01be
            android.content.Context r0 = r11.getContext()
            boolean r0 = r0 instanceof org.telegram.ui.LaunchActivity
            if (r0 == 0) goto L_0x01be
            android.content.Context r0 = r11.getContext()
            r1 = r0
            org.telegram.ui.LaunchActivity r1 = (org.telegram.ui.LaunchActivity) r1
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r0 = r0.getAccount()
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            goto L_0x01be
        L_0x0151:
            r1 = 4
            if (r0 != r1) goto L_0x0189
            org.telegram.ui.ActionBar.BaseFragment r0 = r11.fragment
            android.app.Activity r0 = r0.getParentActivity()
            if (r0 != 0) goto L_0x015d
            return
        L_0x015d:
            org.telegram.ui.ActionBar.BaseFragment r0 = r11.fragment
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.messenger.ChatObject$Call r1 = r0.getGroupCall()
            if (r1 != 0) goto L_0x0168
            return
        L_0x0168:
            org.telegram.messenger.MessagesController r2 = r0.getMessagesController()
            long r3 = r1.chatId
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r4 = r2.getChat(r3)
            r5 = 0
            r6 = 0
            r7 = 0
            org.telegram.ui.ActionBar.BaseFragment r2 = r11.fragment
            android.app.Activity r8 = r2.getParentActivity()
            org.telegram.ui.ActionBar.BaseFragment r9 = r11.fragment
            org.telegram.messenger.AccountInstance r10 = r9.getAccountInstance()
            org.telegram.ui.Components.voip.VoIPHelper.startCall(r4, r5, r6, r7, r8, r9, r10)
            goto L_0x01bd
        L_0x0189:
            r1 = 5
            if (r0 != r1) goto L_0x01bd
            org.telegram.messenger.SendMessagesHelper r0 = r13.getSendMessagesHelper()
            r1 = r13
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            long r1 = r1.getDialogId()
            org.telegram.messenger.SendMessagesHelper$ImportingHistory r0 = r0.getImportingHistory(r1)
            if (r0 != 0) goto L_0x019e
            return
        L_0x019e:
            org.telegram.ui.Components.ImportingAlert r1 = new org.telegram.ui.Components.ImportingAlert
            android.content.Context r2 = r11.getContext()
            r4 = 0
            org.telegram.ui.ActionBar.BaseFragment r5 = r11.fragment
            org.telegram.ui.ChatActivity r5 = (org.telegram.ui.ChatActivity) r5
            r1.<init>(r2, r4, r5, r12)
            org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda4
            r2.<init>(r11)
            r1.setOnHideListener(r2)
            org.telegram.ui.ActionBar.BaseFragment r2 = r11.fragment
            r2.showDialog(r1)
            r11.checkImport(r3)
            goto L_0x01be
        L_0x01bd:
        L_0x01be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.m2302lambda$new$10$orgtelegramuiComponentsFragmentContextView(org.telegram.ui.ActionBar.Theme$ResourcesProvider, org.telegram.ui.ActionBar.BaseFragment, android.view.View):void");
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-FragmentContextView  reason: not valid java name */
    public /* synthetic */ void m2310lambda$new$9$orgtelegramuiComponentsFragmentContextView(DialogInterface dialog) {
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
            locationActivity.setDelegate(new FragmentContextView$$ExternalSyntheticLambda3(info, info.messageObject.getDialogId()));
            launchActivity.m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(locationActivity);
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
                }
            } else {
                show = true;
            }
        } else {
            show = true;
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
                this.avatars.setVisibility(0);
                updateAvatars(false);
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                ActionBarMenuItem actionBarMenuItem3 = this.playbackSpeedButton;
                if (actionBarMenuItem3 != null) {
                    actionBarMenuItem3.setVisibility(8);
                }
            } else if (i == 1 || i == 3) {
                this.selector.setBackground((Drawable) null);
                updateCallTitle();
                this.avatars.setVisibility(0);
                if (i == 3 && VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().registerStateListener(this);
                }
                updateAvatars(false);
                this.muteButton.setVisibility(0);
                boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
                this.isMuted = z;
                this.muteDrawable.setCustomEndFrame(z ? 15 : 29);
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
                this.muteButton.invalidate();
                this.frameLayout.setBackground((Drawable) null);
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
                        this.subtitleTextView.setText(LocaleController.getString("MembersTalkingNobody", NUM), false);
                    } else {
                        this.subtitleTextView.setText(LocaleController.formatPluralString("Participants", call.call.participants_count), false);
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
        ChatActivity chatActivity;
        BaseFragment baseFragment = this.fragment;
        if ((baseFragment instanceof ChatActivity) && this.titleTextView != null) {
            ChatActivity chatActivity2 = (ChatActivity) baseFragment;
            long dialogId = chatActivity2.getDialogId();
            int currentAccount = chatActivity2.getCurrentAccount();
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
                        chatActivity = chatActivity2;
                    } else if (message.date + message.media.period > date) {
                        long fromId = MessageObject.getFromChatId(message);
                        if (notYouUser != null || fromId == currentUserId) {
                            chatActivity = chatActivity2;
                        } else {
                            chatActivity = chatActivity2;
                            notYouUser = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(fromId));
                        }
                        locationSharingCount++;
                    } else {
                        chatActivity = chatActivity2;
                    }
                    a++;
                    chatActivity2 = chatActivity;
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
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            SendMessagesHelper.ImportingHistory importingHistory = chatActivity.getSendMessagesHelper().getImportingHistory(chatActivity.getDialogId());
            View fragmentView = this.fragment.getFragmentView();
            if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
                create = true;
            }
            Dialog dialog = chatActivity.getVisibleDialog();
            if ((isPlayingVoice() || chatActivity.shouldShowImport() || ((dialog instanceof ImportingAlert) && !((ImportingAlert) dialog).isDismissed())) && importingHistory != null) {
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
                int i3 = this.currentStyle;
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
                        boolean z3 = groupActive;
                        return;
                    }
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
                    boolean z4 = groupActive;
                } else if (!z2 || !((i = this.currentStyle) == -1 || i == 4 || i == 3 || i == 1)) {
                    boolean z5 = groupActive;
                } else {
                    this.visible = false;
                    setVisibility(8);
                    boolean z6 = groupActive;
                }
            } else {
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
                            boolean z7 = groupActive;
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
                            if (ChatObject.isChannelOrGiga(chat)) {
                                this.titleTextView.setText(LocaleController.getString("VoipChannelVoiceChat", NUM), false);
                            } else {
                                this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM), false);
                            }
                            if (call2.call.participants_count == 0) {
                                this.subtitleTextView.setText(LocaleController.getString("MembersTalkingNobody", NUM), false);
                            } else {
                                this.subtitleTextView.setText(LocaleController.formatPluralString("Participants", call2.call.participants_count), false);
                            }
                            this.frameLayout.invalidate();
                        }
                        updateAvatars(this.avatars.wasDraw && updateAnimated);
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
                                }
                            });
                            this.animatorSet.start();
                        } else {
                            updatePaddings();
                            setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
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
    }

    private void updateAvatars(boolean animated) {
        TLRPC.User userCall;
        ChatObject.Call call;
        int currentAccount;
        boolean z = animated;
        if (!z && this.avatars.transitionProgressAnimator != null) {
            this.avatars.transitionProgressAnimator.cancel();
            this.avatars.transitionProgressAnimator = null;
        }
        if (this.avatars.transitionProgressAnimator == null) {
            if (this.currentStyle == 4) {
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    ChatActivity chatActivity = (ChatActivity) baseFragment;
                    call = chatActivity.getGroupCall();
                    currentAccount = chatActivity.getCurrentAccount();
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
                int N2 = Math.min(3, call.sortedParticipants.size());
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
                    } else if (ChatObject.isChannelOrGiga(((ChatActivity) this.fragment).getCurrentChat())) {
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
