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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
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
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.SharingLocationsAlert;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPBaseService.StateListener {
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
    private BaseFragment fragment;
    private FrameLayout frameLayout;
    private RLottieImageView importingImageView;
    private boolean isLocation;
    private boolean isMusic;
    /* access modifiers changed from: private */
    public boolean isMuted;
    private TextView joinButton;
    private int lastLocationSharingCount;
    private MessageObject lastMessageObject;
    private String lastString;
    float micAmplitude;
    /* access modifiers changed from: private */
    public RLottieImageView muteButton;
    /* access modifiers changed from: private */
    public RLottieDrawable muteDrawable;
    private ImageView playButton;
    private PlayPauseDrawable playPauseDrawable;
    private ImageView playbackSpeedButton;
    private View selector;
    private View shadow;
    float speakerAmplitude;
    private AudioPlayerAlert.ClippingTextViewSwitcher subtitleTextView;
    private boolean supportsCalls;
    private AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private float topPadding;
    /* access modifiers changed from: private */
    public boolean visible;
    boolean wasDraw;

    public interface FragmentContextViewDelegate {
        void onAnimation(boolean z, boolean z2);
    }

    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPBaseService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPBaseService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    /* access modifiers changed from: protected */
    public void playbackSpeedChanged(boolean z) {
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
        this(context, baseFragment, (View) null, z);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FragmentContextView(Context context, BaseFragment baseFragment, View view, boolean z) {
        super(context);
        final Context context2 = context;
        BaseFragment baseFragment2 = baseFragment;
        View view2 = view;
        boolean z2 = z;
        this.currentProgress = -1;
        this.currentStyle = -1;
        this.supportsCalls = true;
        this.account = UserConfig.selectedAccount;
        this.lastLocationSharingCount = -1;
        this.checkLocationRunnable = new Runnable() {
            public void run() {
                FragmentContextView.this.checkLocationString();
                AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000);
            }
        };
        this.animationIndex = -1;
        this.fragment = baseFragment2;
        this.applyingView = view2;
        this.visible = true;
        this.isLocation = z2;
        if (view2 == null) {
            ((ViewGroup) baseFragment.getFragmentView()).setClipToPadding(false);
        }
        setTag(1);
        AnonymousClass2 r3 = new FrameLayout(context2) {
            public void invalidate() {
                super.invalidate();
                if (FragmentContextView.this.avatars != null && FragmentContextView.this.avatars.getVisibility() == 0) {
                    FragmentContextView.this.avatars.invalidate();
                }
            }
        };
        this.frameLayout = r3;
        addView(r3, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
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
        this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), PorterDuff.Mode.MULTIPLY));
        ImageView imageView2 = this.playButton;
        PlayPauseDrawable playPauseDrawable2 = new PlayPauseDrawable(14);
        this.playPauseDrawable = playPauseDrawable2;
        imageView2.setImageDrawable(playPauseDrawable2);
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            this.playButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("inappPlayerPlayPause") & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$0$FragmentContextView(view);
            }
        });
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.importingImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.importingImageView.setAutoRepeat(true);
        this.importingImageView.setAnimation(NUM, 30, 30);
        this.importingImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(22.0f), Theme.getColor("inappPlayerPlayPause")));
        addView(this.importingImageView, LayoutHelper.createFrame(22, 22.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        AnonymousClass3 r8 = new AudioPlayerAlert.ClippingTextViewSwitcher(context2) {
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
                    textView.setTextColor(Theme.getColor("inappPlayerTitle"));
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextSize(1, 15.0f);
                } else if (FragmentContextView.this.currentStyle == 4) {
                    textView.setGravity(51);
                    textView.setTextColor(Theme.getColor("inappPlayerPerformer"));
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView.setTextSize(1, 15.0f);
                } else if (FragmentContextView.this.currentStyle == 1 || FragmentContextView.this.currentStyle == 3) {
                    textView.setGravity(19);
                    textView.setTextColor(Theme.getColor("returnToCallText"));
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView.setTextSize(1, 14.0f);
                }
                return textView;
            }
        };
        this.titleTextView = r8;
        addView(r8, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        AnonymousClass4 r82 = new AudioPlayerAlert.ClippingTextViewSwitcher(this, context2) {
            /* access modifiers changed from: protected */
            public TextView createTextView() {
                TextView textView = new TextView(context2);
                textView.setMaxLines(1);
                textView.setLines(1);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setGravity(3);
                textView.setTextSize(1, 13.0f);
                textView.setTextColor(Theme.getColor("inappPlayerClose"));
                return textView;
            }
        };
        this.subtitleTextView = r82;
        addView(r82, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 10.0f, 36.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.joinButton = textView;
        textView.setText(LocaleController.getString("VoipChatJoin", NUM));
        this.joinButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.joinButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.joinButton.setTextSize(1, 14.0f);
        this.joinButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.joinButton.setGravity(17);
        this.joinButton.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        addView(this.joinButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 10.0f, 14.0f, 0.0f));
        this.joinButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$1$FragmentContextView(view);
            }
        });
        if (!z2) {
            ImageView imageView3 = new ImageView(context2);
            this.playbackSpeedButton = imageView3;
            imageView3.setScaleType(ImageView.ScaleType.CENTER);
            this.playbackSpeedButton.setImageResource(NUM);
            this.playbackSpeedButton.setContentDescription(LocaleController.getString("AccDescrPlayerSpeed", NUM));
            if (AndroidUtilities.density >= 3.0f) {
                this.playbackSpeedButton.setPadding(0, 1, 0, 0);
            }
            addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
            this.playbackSpeedButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    FragmentContextView.this.lambda$new$2$FragmentContextView(view);
                }
            });
            updatePlaybackButton();
        }
        AvatarsImageView avatarsImageView = new AvatarsImageView(context2);
        this.avatars = avatarsImageView;
        avatarsImageView.setDelegate(new Runnable() {
            public final void run() {
                FragmentContextView.this.lambda$new$3$FragmentContextView();
            }
        });
        this.avatars.setVisibility(8);
        addView(this.avatars, LayoutHelper.createFrame(108, 36, 51));
        this.muteDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), true, (int[]) null);
        AnonymousClass5 r4 = new RLottieImageView(context2) {
            private final Runnable pressRunnable = new Runnable() {
                public final void run() {
                    FragmentContextView.AnonymousClass5.this.lambda$$1$FragmentContextView$5();
                }
            };
            boolean pressed;
            boolean scheduled;
            private final Runnable toggleMicRunnable = new Runnable() {
                public final void run() {
                    FragmentContextView.AnonymousClass5.this.lambda$$0$FragmentContextView$5();
                }
            };

            /* access modifiers changed from: private */
            /* renamed from: lambda$$0 */
            public /* synthetic */ void lambda$$0$FragmentContextView$5() {
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
            /* renamed from: lambda$$1 */
            public /* synthetic */ void lambda$$1$FragmentContextView$5() {
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
        this.muteButton = r4;
        r4.setColorFilter(new PorterDuffColorFilter(Theme.getColor("returnToCallText"), PorterDuff.Mode.MULTIPLY));
        if (i >= 21) {
            this.muteButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        this.muteButton.setAnimation(this.muteDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setVisibility(8);
        addView(this.muteButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$4$FragmentContextView(view);
            }
        });
        ImageView imageView4 = new ImageView(context2);
        this.closeButton = imageView4;
        imageView4.setImageResource(NUM);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), PorterDuff.Mode.MULTIPLY));
        if (i >= 21) {
            this.closeButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$6$FragmentContextView(view);
            }
        });
        setOnClickListener(new View.OnClickListener(baseFragment2) {
            public final /* synthetic */ BaseFragment f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$8$FragmentContextView(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$FragmentContextView(View view) {
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
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$FragmentContextView(View view) {
        callOnClick();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$FragmentContextView(View view) {
        float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        if (playbackSpeed > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.8f);
        }
        playbackSpeedChanged(playbackSpeed <= 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$FragmentContextView() {
        updateAvatars(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$FragmentContextView(View view) {
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
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$FragmentContextView(View view) {
        if (this.currentStyle == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.fragment.getParentActivity());
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
            builder.setPositiveButton(LocaleController.getString("Stop", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FragmentContextView.this.lambda$null$5$FragmentContextView(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            builder.show();
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
                return;
            }
            return;
        }
        MediaController.getInstance().cleanupPlayer(true, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$FragmentContextView(DialogInterface dialogInterface, int i) {
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
    /* renamed from: lambda$new$8 */
    public /* synthetic */ void lambda$new$8$FragmentContextView(BaseFragment baseFragment, View view) {
        ChatActivity chatActivity;
        ChatObject.Call groupCall;
        long j;
        int i;
        int i2 = this.currentStyle;
        long j2 = 0;
        if (i2 == 0) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment != null && playingMessageObject != null) {
                if (playingMessageObject.isMusic()) {
                    this.fragment.showDialog(new AudioPlayerAlert(getContext()));
                    return;
                }
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
                int i3 = (int) dialogId;
                int i4 = (int) (dialogId >> 32);
                if (i3 == 0) {
                    bundle.putInt("enc_id", i4);
                } else if (i3 > 0) {
                    bundle.putInt("user_id", i3);
                } else {
                    bundle.putInt("chat_id", -i3);
                }
                bundle.putInt("message_id", playingMessageObject.getId());
                this.fragment.presentFragment(new ChatActivity(bundle), this.fragment instanceof ChatActivity);
            }
        } else if (i2 == 1) {
            getContext().startActivity(new Intent(getContext(), LaunchActivity.class).setAction("voip"));
        } else if (i2 == 2) {
            int i5 = UserConfig.selectedAccount;
            BaseFragment baseFragment3 = this.fragment;
            if (baseFragment3 instanceof ChatActivity) {
                j = ((ChatActivity) baseFragment3).getDialogId();
                i = this.fragment.getCurrentAccount();
            } else {
                if (LocationController.getLocationsCount() == 1) {
                    int i6 = 0;
                    while (true) {
                        if (i6 >= 3) {
                            break;
                        } else if (!LocationController.getInstance(i6).sharingLocationsUI.isEmpty()) {
                            LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(i6).sharingLocationsUI.get(0);
                            long j3 = sharingLocationInfo.did;
                            i = sharingLocationInfo.messageObject.currentAccount;
                            j = j3;
                            break;
                        } else {
                            i6++;
                        }
                    }
                }
                i = i5;
                j = 0;
            }
            if (j != 0) {
                openSharingLocation(LocationController.getInstance(i).getSharingLocationInfo(j));
            } else {
                this.fragment.showDialog(new SharingLocationsAlert(getContext(), new SharingLocationsAlert.SharingLocationsAlertDelegate() {
                    public final void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
                        FragmentContextView.this.openSharingLocation(sharingLocationInfo);
                    }
                }));
            }
        } else if (i2 == 3) {
            if (VoIPService.getSharedInstance() != null && (getContext() instanceof LaunchActivity)) {
                GroupCallActivity.create((LaunchActivity) getContext(), AccountInstance.getInstance(VoIPService.getSharedInstance().getAccount()));
            }
        } else if (i2 == 4) {
            if (this.fragment.getParentActivity() != null && (groupCall = chatActivity.getGroupCall()) != null) {
                TLRPC$Chat chat = (chatActivity = (ChatActivity) this.fragment).getMessagesController().getChat(Integer.valueOf(groupCall.chatId));
                Activity parentActivity = this.fragment.getParentActivity();
                BaseFragment baseFragment4 = this.fragment;
                VoIPHelper.startCall(chat, (TLRPC$InputPeer) null, (String) null, false, parentActivity, baseFragment4, baseFragment4.getAccountInstance());
            }
        } else if (i2 == 5 && baseFragment.getSendMessagesHelper().getImportingHistory(((ChatActivity) baseFragment).getDialogId()) != null) {
            ImportingAlert importingAlert = new ImportingAlert(getContext(), (ChatActivity) this.fragment);
            importingAlert.setOnHideListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    FragmentContextView.this.lambda$null$7$FragmentContextView(dialogInterface);
                }
            });
            this.fragment.showDialog(importingAlert);
            checkImport(false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$FragmentContextView(DialogInterface dialogInterface) {
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
            String str = MediaController.getInstance().getPlaybackSpeed(this.isMusic) > 1.0f ? "inappPlayerPlayPause" : "inappPlayerClose";
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
            if (Build.VERSION.SDK_INT >= 21) {
                this.playbackSpeedButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str) & NUM, 1, AndroidUtilities.dp(14.0f)));
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
            locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate(sharingLocationInfo.messageObject.getDialogId()) {
                public final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                    SendMessagesHelper.getInstance(LocationController.SharingLocationInfo.this.messageObject.currentAccount).sendMessage(tLRPC$MessageMedia, this.f$1, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
                }
            });
            launchActivity.lambda$runLinkRequest$41(locationActivity);
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
            org.telegram.tgnet.TLRPC$GroupCall r0 = r0.call
            int r0 = r0.participants_count
            if (r0 <= 0) goto L_0x008f
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

    private void updateStyle(int i) {
        int i2 = i;
        int i3 = this.currentStyle;
        if (i3 != i2) {
            if (i3 == 3 || i3 == 1) {
                Theme.getFragmentContextViewWavesDrawable().removeParent(this);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().unregisterStateListener(this);
                }
            }
            this.currentStyle = i2;
            AvatarsImageView avatarsImageView = this.avatars;
            if (avatarsImageView != null) {
                avatarsImageView.setStyle(i2);
                this.avatars.setLayoutParams(LayoutHelper.createFrame(108, getStyleHeight(), 51));
            }
            this.frameLayout.setLayoutParams(LayoutHelper.createFrame(-1, (float) getStyleHeight(), 51, 0.0f, 0.0f, 0.0f, 0.0f));
            this.shadow.setLayoutParams(LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, (float) getStyleHeight(), 0.0f, 0.0f));
            float f = this.topPadding;
            if (f > 0.0f && f != ((float) AndroidUtilities.dp2((float) getStyleHeight()))) {
                updatePaddings();
                setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
            }
            if (i2 == 5) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                int i4 = 0;
                while (i4 < 2) {
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                    TextView textView = i4 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                    if (textView != null) {
                        textView.setGravity(19);
                        textView.setTextColor(Theme.getColor("inappPlayerTitle"));
                        textView.setTypeface(Typeface.DEFAULT);
                        textView.setTextSize(1, 15.0f);
                    }
                    i4++;
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
                ImageView imageView = this.playbackSpeedButton;
                if (imageView != null) {
                    imageView.setVisibility(8);
                }
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
            } else if (i2 == 0 || i2 == 2) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.closeButton.setVisibility(0);
                this.playButton.setVisibility(0);
                this.muteButton.setVisibility(8);
                this.importingImageView.setVisibility(8);
                this.importingImageView.stopAnimation();
                this.avatars.setVisibility(8);
                int i5 = 0;
                while (i5 < 2) {
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = this.titleTextView;
                    TextView textView2 = i5 == 0 ? clippingTextViewSwitcher2.getTextView() : clippingTextViewSwitcher2.getNextTextView();
                    if (textView2 != null) {
                        textView2.setGravity(19);
                        textView2.setTextColor(Theme.getColor("inappPlayerTitle"));
                        textView2.setTypeface(Typeface.DEFAULT);
                        textView2.setTextSize(1, 15.0f);
                    }
                    i5++;
                }
                this.titleTextView.setTag("inappPlayerTitle");
                if (i2 == 0) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                    ImageView imageView2 = this.playbackSpeedButton;
                    if (imageView2 != null) {
                        imageView2.setVisibility(0);
                    }
                    this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", NUM));
                    return;
                }
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
                this.closeButton.setContentDescription(LocaleController.getString("AccDescrStopLiveLocation", NUM));
            } else if (i2 == 4) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.muteButton.setVisibility(8);
                this.subtitleTextView.setVisibility(0);
                this.joinButton.setVisibility(0);
                int i6 = 0;
                while (i6 < 2) {
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher3 = this.titleTextView;
                    TextView textView3 = i6 == 0 ? clippingTextViewSwitcher3.getTextView() : clippingTextViewSwitcher3.getNextTextView();
                    if (textView3 != null) {
                        textView3.setGravity(51);
                        textView3.setTextColor(Theme.getColor("inappPlayerPerformer"));
                        textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView3.setTextSize(1, 15.0f);
                    }
                    i6++;
                }
                this.titleTextView.setTag("inappPlayerPerformer");
                this.titleTextView.setPadding(0, 0, 0, 0);
                this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM), false);
                this.importingImageView.setVisibility(8);
                this.importingImageView.stopAnimation();
                this.avatars.setVisibility(0);
                updateAvatars(false);
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                ImageView imageView3 = this.playbackSpeedButton;
                if (imageView3 != null) {
                    imageView3.setVisibility(8);
                }
            } else if (i2 == 1 || i2 == 3) {
                this.selector.setBackground((Drawable) null);
                updateCallTitle();
                this.avatars.setVisibility(0);
                if (i2 == 3 && VoIPService.getSharedInstance() != null) {
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
                int i7 = 0;
                while (i7 < 2) {
                    AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher4 = this.titleTextView;
                    TextView textView4 = i7 == 0 ? clippingTextViewSwitcher4.getTextView() : clippingTextViewSwitcher4.getNextTextView();
                    if (textView4 != null) {
                        textView4.setGravity(19);
                        textView4.setTextColor(Theme.getColor("returnToCallText"));
                        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView4.setTextSize(1, 14.0f);
                    }
                    i7++;
                }
                this.titleTextView.setTag("returnToCallText");
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
                this.titleTextView.setPadding(AndroidUtilities.dp(112.0f), 0, AndroidUtilities.dp(112.0f), 0);
                ImageView imageView4 = this.playbackSpeedButton;
                if (imageView4 != null) {
                    imageView4.setVisibility(8);
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
                    if (!(baseFragment2 instanceof ChatActivity) || ((ChatActivity) baseFragment2).getGroupCall() == null || ((ChatActivity) this.fragment).getGroupCall().call.participants_count <= 0 || GroupCallPip.isShowing() || isPlayingVoice()) {
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
                        int i5 = groupCall.call.participants_count;
                        if (i5 == 0) {
                            this.subtitleTextView.setText(LocaleController.getString("MembersTalkingNobody", NUM), false);
                        } else {
                            this.subtitleTextView.setText(LocaleController.formatPluralString("Participants", i5), false);
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
                int dialogId = (int) sharingLocationInfo.messageObject.getDialogId();
                if (dialogId > 0) {
                    str2 = UserObject.getFirstName(MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getUser(Integer.valueOf(dialogId)));
                    str = LocaleController.getString("AttachLiveLocationIsSharing", NUM);
                } else {
                    TLRPC$Chat chat = MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getChat(Integer.valueOf(-dialogId));
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
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
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
                int clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                int currentTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                i = 0;
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i2);
                    TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                    if (tLRPC$MessageMedia != null && tLRPC$Message.date + tLRPC$MessageMedia.period > currentTime) {
                        int fromChatId = MessageObject.getFromChatId(tLRPC$Message);
                        if (tLRPC$User == null && fromChatId != clientUserId) {
                            tLRPC$User = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(fromChatId));
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
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
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
                z4 = groupCall != null && groupCall.call.participants_count > 0;
            }
            if (z4) {
                checkCall(false);
            } else if (this.visible) {
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
                        ImageView imageView = this.playbackSpeedButton;
                        if (imageView != null) {
                            imageView.setAlpha(1.0f);
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
                        } else if (playingMessageObject.getDuration() >= 1200) {
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
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), 0, playingMessageObject.getMusicAuthor().length(), 18);
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

    /* JADX WARNING: Removed duplicated region for block: B:50:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0112  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkCall(boolean r15) {
        /*
            r14 = this;
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r1 = r14.visible
            if (r1 == 0) goto L_0x0016
            int r1 = r14.currentStyle
            r2 = 5
            if (r1 != r2) goto L_0x0016
            if (r0 == 0) goto L_0x0015
            boolean r1 = r0.isHangingUp()
            if (r1 == 0) goto L_0x0016
        L_0x0015:
            return
        L_0x0016:
            org.telegram.ui.ActionBar.BaseFragment r1 = r14.fragment
            android.view.View r1 = r1.getFragmentView()
            r2 = 1
            if (r15 != 0) goto L_0x0034
            if (r1 == 0) goto L_0x0034
            android.view.ViewParent r3 = r1.getParent()
            if (r3 == 0) goto L_0x0033
            android.view.ViewParent r1 = r1.getParent()
            android.view.View r1 = (android.view.View) r1
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x0034
        L_0x0033:
            r15 = 1
        L_0x0034:
            boolean r1 = org.telegram.ui.Components.GroupCallPip.isShowing()
            r3 = 0
            if (r1 == 0) goto L_0x003e
            r1 = 0
        L_0x003c:
            r4 = 0
            goto L_0x0084
        L_0x003e:
            boolean r1 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r1 != 0) goto L_0x0050
            boolean r1 = r14.supportsCalls
            if (r1 == 0) goto L_0x0050
            if (r0 == 0) goto L_0x0050
            boolean r1 = r0.isHangingUp()
            if (r1 != 0) goto L_0x0050
            r1 = 1
            goto L_0x0051
        L_0x0050:
            r1 = 0
        L_0x0051:
            if (r0 == 0) goto L_0x005e
            org.telegram.messenger.ChatObject$Call r4 = r0.groupCall
            if (r4 == 0) goto L_0x005e
            org.telegram.tgnet.TLRPC$GroupCall r4 = r4.call
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_groupCallDiscarded
            if (r4 == 0) goto L_0x005e
            r1 = 0
        L_0x005e:
            boolean r4 = r14.isPlayingVoice()
            if (r4 != 0) goto L_0x003c
            boolean r4 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r4 != 0) goto L_0x003c
            boolean r4 = r14.supportsCalls
            if (r4 == 0) goto L_0x003c
            if (r1 != 0) goto L_0x003c
            org.telegram.ui.ActionBar.BaseFragment r4 = r14.fragment
            boolean r5 = r4 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x003c
            org.telegram.ui.ChatActivity r4 = (org.telegram.ui.ChatActivity) r4
            org.telegram.messenger.ChatObject$Call r4 = r4.getGroupCall()
            if (r4 == 0) goto L_0x003c
            org.telegram.tgnet.TLRPC$GroupCall r4 = r4.call
            int r4 = r4.participants_count
            if (r4 <= 0) goto L_0x003c
            r1 = 1
            r4 = 1
        L_0x0084:
            r5 = 220(0xdc, double:1.087E-321)
            r7 = 0
            java.lang.String r8 = "topPadding"
            r9 = 3
            r10 = 4
            r11 = 0
            if (r1 != 0) goto L_0x0112
            boolean r0 = r14.visible
            r1 = -1
            r4 = 8
            if (r0 == 0) goto L_0x00ff
            if (r15 == 0) goto L_0x009b
            int r12 = r14.currentStyle
            if (r12 == r1) goto L_0x00a3
        L_0x009b:
            int r12 = r14.currentStyle
            if (r12 == r10) goto L_0x00a3
            if (r12 == r9) goto L_0x00a3
            if (r12 != r2) goto L_0x00ff
        L_0x00a3:
            r14.visible = r3
            if (r15 == 0) goto L_0x00b5
            int r15 = r14.getVisibility()
            if (r15 == r4) goto L_0x00b0
            r14.setVisibility(r4)
        L_0x00b0:
            r14.setTopPadding(r7)
            goto L_0x0285
        L_0x00b5:
            android.animation.AnimatorSet r15 = r14.animatorSet
            if (r15 == 0) goto L_0x00be
            r15.cancel()
            r14.animatorSet = r11
        L_0x00be:
            int r15 = r14.account
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r15)
            int r1 = r14.animationIndex
            int r0 = r0.setAnimationInProgress(r1, r11)
            r14.animationIndex = r0
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r14.animatorSet = r0
            android.animation.Animator[] r1 = new android.animation.Animator[r2]
            float[] r2 = new float[r2]
            r2[r3] = r7
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r14, r8, r2)
            r1[r3] = r2
            r0.playTogether(r1)
            android.animation.AnimatorSet r0 = r14.animatorSet
            r0.setDuration(r5)
            android.animation.AnimatorSet r0 = r14.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setInterpolator(r1)
            android.animation.AnimatorSet r0 = r14.animatorSet
            org.telegram.ui.Components.FragmentContextView$12 r1 = new org.telegram.ui.Components.FragmentContextView$12
            r1.<init>(r15)
            r0.addListener(r1)
            android.animation.AnimatorSet r15 = r14.animatorSet
            r15.start()
            goto L_0x0285
        L_0x00ff:
            if (r0 == 0) goto L_0x0285
            int r15 = r14.currentStyle
            if (r15 == r1) goto L_0x010b
            if (r15 == r10) goto L_0x010b
            if (r15 == r9) goto L_0x010b
            if (r15 != r2) goto L_0x0285
        L_0x010b:
            r14.visible = r3
            r14.setVisibility(r4)
            goto L_0x0285
        L_0x0112:
            if (r4 == 0) goto L_0x0116
            r1 = 4
            goto L_0x011f
        L_0x0116:
            if (r0 == 0) goto L_0x011e
            org.telegram.messenger.ChatObject$Call r1 = r0.groupCall
            if (r1 == 0) goto L_0x011e
            r1 = 3
            goto L_0x011f
        L_0x011e:
            r1 = 1
        L_0x011f:
            int r12 = r14.currentStyle
            if (r1 == r12) goto L_0x012c
            android.animation.AnimatorSet r13 = r14.animatorSet
            if (r13 == 0) goto L_0x012c
            if (r15 != 0) goto L_0x012c
            r14.checkCallAfterAnimation = r2
            return
        L_0x012c:
            if (r1 == r12) goto L_0x017d
            boolean r1 = r14.visible
            if (r1 == 0) goto L_0x017d
            if (r15 != 0) goto L_0x017d
            android.animation.AnimatorSet r15 = r14.animatorSet
            if (r15 == 0) goto L_0x013d
            r15.cancel()
            r14.animatorSet = r11
        L_0x013d:
            int r15 = r14.account
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r15)
            int r1 = r14.animationIndex
            int r0 = r0.setAnimationInProgress(r1, r11)
            r14.animationIndex = r0
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r14.animatorSet = r0
            android.animation.Animator[] r1 = new android.animation.Animator[r2]
            float[] r2 = new float[r2]
            r2[r3] = r7
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r14, r8, r2)
            r1[r3] = r2
            r0.playTogether(r1)
            android.animation.AnimatorSet r0 = r14.animatorSet
            r0.setDuration(r5)
            android.animation.AnimatorSet r0 = r14.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setInterpolator(r1)
            android.animation.AnimatorSet r0 = r14.animatorSet
            org.telegram.ui.Components.FragmentContextView$13 r1 = new org.telegram.ui.Components.FragmentContextView$13
            r1.<init>(r15)
            r0.addListener(r1)
            android.animation.AnimatorSet r15 = r14.animatorSet
            r15.start()
            return
        L_0x017d:
            if (r4 == 0) goto L_0x01c2
            if (r12 != r10) goto L_0x0187
            boolean r0 = r14.visible
            if (r0 == 0) goto L_0x0187
            r0 = 1
            goto L_0x0188
        L_0x0187:
            r0 = 0
        L_0x0188:
            r14.updateStyle(r10)
            org.telegram.ui.ActionBar.BaseFragment r1 = r14.fragment
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            org.telegram.messenger.ChatObject$Call r1 = r1.getGroupCall()
            org.telegram.tgnet.TLRPC$GroupCall r1 = r1.call
            int r1 = r1.participants_count
            if (r1 != 0) goto L_0x01a8
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r14.subtitleTextView
            r4 = 2131626084(0x7f0e0864, float:1.8879394E38)
            java.lang.String r7 = "MembersTalkingNobody"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r1.setText(r4, r3)
            goto L_0x01b3
        L_0x01a8:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r4 = r14.subtitleTextView
            java.lang.String r7 = "Participants"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r7, r1)
            r4.setText(r1, r3)
        L_0x01b3:
            org.telegram.ui.Components.AvatarsImageView r1 = r14.avatars
            boolean r1 = r1.wasDraw
            if (r1 == 0) goto L_0x01bd
            if (r0 == 0) goto L_0x01bd
            r0 = 1
            goto L_0x01be
        L_0x01bd:
            r0 = 0
        L_0x01be:
            r14.updateAvatars(r0)
            goto L_0x01df
        L_0x01c2:
            if (r0 == 0) goto L_0x01d4
            org.telegram.messenger.ChatObject$Call r0 = r0.groupCall
            if (r0 == 0) goto L_0x01d4
            if (r12 != r9) goto L_0x01cc
            r0 = 1
            goto L_0x01cd
        L_0x01cc:
            r0 = 0
        L_0x01cd:
            r14.updateAvatars(r0)
            r14.updateStyle(r9)
            goto L_0x01df
        L_0x01d4:
            if (r12 != r2) goto L_0x01d8
            r0 = 1
            goto L_0x01d9
        L_0x01d8:
            r0 = 0
        L_0x01d9:
            r14.updateAvatars(r0)
            r14.updateStyle(r2)
        L_0x01df:
            boolean r0 = r14.visible
            if (r0 != 0) goto L_0x0285
            if (r15 != 0) goto L_0x0270
            android.animation.AnimatorSet r15 = r14.animatorSet
            if (r15 == 0) goto L_0x01ee
            r15.cancel()
            r14.animatorSet = r11
        L_0x01ee:
            android.animation.AnimatorSet r15 = new android.animation.AnimatorSet
            r15.<init>()
            r14.animatorSet = r15
            org.telegram.ui.Components.FragmentContextView r15 = r14.additionalContextView
            if (r15 == 0) goto L_0x0219
            int r15 = r15.getVisibility()
            if (r15 != 0) goto L_0x0219
            android.view.ViewGroup$LayoutParams r15 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r15 = (android.widget.FrameLayout.LayoutParams) r15
            int r0 = r14.getStyleHeight()
            org.telegram.ui.Components.FragmentContextView r1 = r14.additionalContextView
            int r1 = r1.getStyleHeight()
            int r0 = r0 + r1
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            r15.topMargin = r0
            goto L_0x022b
        L_0x0219:
            android.view.ViewGroup$LayoutParams r15 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r15 = (android.widget.FrameLayout.LayoutParams) r15
            int r0 = r14.getStyleHeight()
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            r15.topMargin = r0
        L_0x022b:
            int r15 = r14.account
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r15)
            int r1 = r14.animationIndex
            int r0 = r0.setAnimationInProgress(r1, r11)
            r14.animationIndex = r0
            android.animation.AnimatorSet r0 = r14.animatorSet
            android.animation.Animator[] r1 = new android.animation.Animator[r2]
            float[] r4 = new float[r2]
            int r7 = r14.getStyleHeight()
            float r7 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp2(r7)
            float r7 = (float) r7
            r4[r3] = r7
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r14, r8, r4)
            r1[r3] = r4
            r0.playTogether(r1)
            android.animation.AnimatorSet r0 = r14.animatorSet
            r0.setDuration(r5)
            android.animation.AnimatorSet r0 = r14.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setInterpolator(r1)
            android.animation.AnimatorSet r0 = r14.animatorSet
            org.telegram.ui.Components.FragmentContextView$14 r1 = new org.telegram.ui.Components.FragmentContextView$14
            r1.<init>(r15)
            r0.addListener(r1)
            android.animation.AnimatorSet r15 = r14.animatorSet
            r15.start()
            goto L_0x0280
        L_0x0270:
            r14.updatePaddings()
            int r15 = r14.getStyleHeight()
            float r15 = (float) r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp2(r15)
            float r15 = (float) r15
            r14.setTopPadding(r15)
        L_0x0280:
            r14.visible = r2
            r14.setVisibility(r3)
        L_0x0285:
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
        if (!z && (valueAnimator = this.avatars.transitionProgressAnimator) != null) {
            valueAnimator.cancel();
            this.avatars.transitionProgressAnimator = null;
        }
        AvatarsImageView avatarsImageView = this.avatars;
        if (avatarsImageView.transitionProgressAnimator == null) {
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
                while (i3 < 3) {
                    if (i3 < size) {
                        this.avatars.setObject(i3, i, call.sortedParticipants.get(i3));
                    } else {
                        this.avatars.setObject(i3, i, (TLObject) null);
                    }
                    i3++;
                }
            } else if (tLRPC$User != null) {
                this.avatars.setObject(0, i, tLRPC$User);
                for (int i4 = 1; i4 < 3; i4++) {
                    this.avatars.setObject(i4, i, (TLObject) null);
                }
            } else {
                while (i3 < 3) {
                    this.avatars.setObject(i3, i, (TLObject) null);
                    i3++;
                }
            }
            this.avatars.commitTransition(z);
            if (this.currentStyle == 4 && call != null) {
                int min = Math.min(3, call.sortedParticipants.size());
                int i5 = 10;
                if (min != 0) {
                    i5 = 10 + ((min - 1) * 24) + 10 + 32;
                }
                if (z) {
                    int i6 = ((FrameLayout.LayoutParams) this.titleTextView.getLayoutParams()).leftMargin;
                    float f = (float) i5;
                    if (AndroidUtilities.dp(f) != i6) {
                        float translationX = (this.titleTextView.getTranslationX() + ((float) i6)) - ((float) AndroidUtilities.dp(f));
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
                float f2 = (float) i5;
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, f2, 5.0f, 36.0f, 0.0f));
                this.subtitleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, f2, 25.0f, 36.0f, 0.0f));
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
}
