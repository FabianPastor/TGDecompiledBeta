package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.ViewConfiguration;
import android.view.ViewGroup;
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
import org.telegram.tgnet.TLRPC$Chat;
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
    boolean checkCallAfterAnimation;
    /* access modifiers changed from: private */
    public Runnable checkLocationRunnable;
    private ImageView closeButton;
    float collapseProgress;
    boolean collapseTransition;
    /* access modifiers changed from: private */
    public int currentStyle;
    /* access modifiers changed from: private */
    public FragmentContextViewDelegate delegate;
    boolean drawOverlay;
    float extraHeight;
    private boolean firstLocationsLoaded;
    private BaseFragment fragment;
    private FrameLayout frameLayout;
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
    private TextView titleTextView;
    private float topPadding;
    /* access modifiers changed from: private */
    public boolean visible;

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

    public void onAudioSettingsChanged() {
        boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        if (this.isMuted != z) {
            this.isMuted = z;
            this.muteDrawable.setCustomEndFrame(z ? 0 : 14);
            RLottieDrawable rLottieDrawable = this.muteDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame(), false, true);
            this.muteButton.invalidate();
            Theme.getFragmentContextViewWavesDrawable().updateState();
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
        View view2 = view;
        boolean z2 = z;
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
        this.fragment = baseFragment;
        this.applyingView = view2;
        this.visible = true;
        this.isLocation = z2;
        if (view2 == null) {
            ((ViewGroup) baseFragment.getFragmentView()).setClipToPadding(false);
        }
        setTag(1);
        AnonymousClass2 r2 = new FrameLayout(context2) {
            public void invalidate() {
                super.invalidate();
                if (FragmentContextView.this.avatars != null && FragmentContextView.this.avatars.getVisibility() == 0) {
                    FragmentContextView.this.avatars.invalidate();
                }
            }
        };
        this.frameLayout = r2;
        addView(r2, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
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
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(19);
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        AnonymousClass3 r6 = new AudioPlayerAlert.ClippingTextViewSwitcher(this, context2) {
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
        this.subtitleTextView = r6;
        addView(r6, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 10.0f, 36.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.joinButton = textView2;
        textView2.setText(LocaleController.getString("VoipChatJoin", NUM));
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
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), true, (int[]) null);
        this.muteDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        AnonymousClass4 r3 = new RLottieImageView(context2) {
            private Runnable hapticRunnable = new Runnable() {
                public final void run() {
                    FragmentContextView.AnonymousClass4.this.lambda$$1$FragmentContextView$4();
                }
            };
            private Runnable pressRunnable = new Runnable() {
                public final void run() {
                    FragmentContextView.AnonymousClass4.this.lambda$$0$FragmentContextView$4();
                }
            };
            boolean pressed;
            boolean scheduled;

            /* access modifiers changed from: private */
            /* renamed from: lambda$$0 */
            public /* synthetic */ void lambda$$0$FragmentContextView$4() {
                if (this.scheduled && VoIPService.getSharedInstance() != null) {
                    int i = 0;
                    VoIPService.getSharedInstance().setMicMute(false, true, false);
                    this.scheduled = false;
                    this.pressed = true;
                    boolean unused = FragmentContextView.this.isMuted = false;
                    RLottieDrawable access$500 = FragmentContextView.this.muteDrawable;
                    if (!FragmentContextView.this.isMuted) {
                        i = 15;
                    }
                    access$500.setCustomEndFrame(i);
                    FragmentContextView.this.muteButton.playAnimation();
                    Theme.getFragmentContextViewWavesDrawable().updateState();
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$$1 */
            public /* synthetic */ void lambda$$1$FragmentContextView$4() {
                if (this.scheduled && VoIPService.getSharedInstance() != null) {
                    FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
                }
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (FragmentContextView.this.currentStyle != 3) {
                    return super.onTouchEvent(motionEvent);
                }
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                if (sharedInstance == null) {
                    AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                    AndroidUtilities.cancelRunOnUIThread(this.hapticRunnable);
                    this.scheduled = false;
                    this.pressed = false;
                    return true;
                }
                if (motionEvent.getAction() == 0 && sharedInstance.isMicMute()) {
                    AndroidUtilities.runOnUIThread(this.pressRunnable, (long) ViewConfiguration.getTapTimeout());
                    AndroidUtilities.runOnUIThread(this.hapticRunnable, (long) Math.max(0, ViewConfiguration.getTapTimeout() - 90));
                    this.scheduled = true;
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    AndroidUtilities.cancelRunOnUIThread(this.hapticRunnable);
                    if (this.scheduled) {
                        AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                        this.scheduled = false;
                    } else if (this.pressed) {
                        boolean unused = FragmentContextView.this.isMuted = true;
                        FragmentContextView.this.muteDrawable.setCustomEndFrame(FragmentContextView.this.isMuted ? 0 : 15);
                        FragmentContextView.this.muteButton.playAnimation();
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().setMicMute(true, true, false);
                            FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
                        }
                        this.pressed = false;
                        Theme.getFragmentContextViewWavesDrawable().updateState();
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
        this.muteButton = r3;
        r3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("returnToCallText"), PorterDuff.Mode.MULTIPLY));
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
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$7$FragmentContextView(view);
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
        if (MediaController.getInstance().getPlaybackSpeed(this.isMusic) > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.8f);
        }
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
            ChatObject.Call call = sharedInstance.groupCall;
            AccountInstance instance = AccountInstance.getInstance(sharedInstance.getAccount());
            TLRPC$Chat chat = sharedInstance.getChat();
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call.participants.get(instance.getUserConfig().getClientUserId());
            if (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || ChatObject.canManageCalls(chat)) {
                boolean z = !sharedInstance.isMicMute();
                this.isMuted = z;
                int i = 0;
                sharedInstance.setMicMute(z, false, true);
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                if (!this.isMuted) {
                    i = 15;
                }
                rLottieDrawable.setCustomEndFrame(i);
                this.muteButton.playAnimation();
                Theme.getFragmentContextViewWavesDrawable().updateState();
                this.muteButton.performHapticFeedback(3, 2);
            }
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
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$FragmentContextView(View view) {
        ChatActivity chatActivity;
        ChatObject.Call groupCall;
        long j;
        int i = this.currentStyle;
        long j2 = 0;
        if (i == 0) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment != null && playingMessageObject != null) {
                if (playingMessageObject.isMusic()) {
                    this.fragment.showDialog(new AudioPlayerAlert(getContext()));
                    return;
                }
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    j2 = ((ChatActivity) baseFragment).getDialogId();
                }
                if (playingMessageObject.getDialogId() == j2) {
                    ((ChatActivity) this.fragment).scrollToMessageId(playingMessageObject.getId(), 0, false, 0, true, 0);
                    return;
                }
                long dialogId = playingMessageObject.getDialogId();
                Bundle bundle = new Bundle();
                int i2 = (int) dialogId;
                int i3 = (int) (dialogId >> 32);
                if (i2 == 0) {
                    bundle.putInt("enc_id", i3);
                } else if (i2 > 0) {
                    bundle.putInt("user_id", i2);
                } else {
                    bundle.putInt("chat_id", -i2);
                }
                bundle.putInt("message_id", playingMessageObject.getId());
                this.fragment.presentFragment(new ChatActivity(bundle), this.fragment instanceof ChatActivity);
            }
        } else if (i == 1) {
            getContext().startActivity(new Intent(getContext(), LaunchActivity.class).setAction("voip"));
        } else if (i == 2) {
            int i4 = UserConfig.selectedAccount;
            BaseFragment baseFragment2 = this.fragment;
            if (baseFragment2 instanceof ChatActivity) {
                j = ((ChatActivity) baseFragment2).getDialogId();
                i4 = this.fragment.getCurrentAccount();
            } else {
                if (LocationController.getLocationsCount() == 1) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= 3) {
                            break;
                        } else if (!LocationController.getInstance(i5).sharingLocationsUI.isEmpty()) {
                            LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(i5).sharingLocationsUI.get(0);
                            j = sharingLocationInfo.did;
                            i4 = sharingLocationInfo.messageObject.currentAccount;
                            break;
                        } else {
                            i5++;
                        }
                    }
                }
                j = 0;
            }
            if (j != 0) {
                openSharingLocation(LocationController.getInstance(i4).getSharingLocationInfo(j));
            } else {
                this.fragment.showDialog(new SharingLocationsAlert(getContext(), new SharingLocationsAlert.SharingLocationsAlertDelegate() {
                    public final void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
                        FragmentContextView.this.openSharingLocation(sharingLocationInfo);
                    }
                }));
            }
        } else if (i == 3) {
            if (VoIPService.getSharedInstance() != null && (getContext() instanceof LaunchActivity)) {
                GroupCallActivity.create((LaunchActivity) getContext(), AccountInstance.getInstance(VoIPService.getSharedInstance().getAccount()));
            }
        } else if (i == 4 && this.fragment.getParentActivity() != null && (groupCall = chatActivity.getGroupCall()) != null) {
            VoIPHelper.startCall((chatActivity = (ChatActivity) this.fragment).getMessagesController().getChat(Integer.valueOf(groupCall.chatId)), 0, this.fragment.getParentActivity());
        }
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
        if (sharingLocationInfo != null && this.fragment.getParentActivity() != null) {
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
            launchActivity.lambda$runLinkRequest$38(locationActivity);
        }
    }

    @Keep
    public float getTopPadding() {
        return this.topPadding;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0051, code lost:
        if (((org.telegram.ui.ChatActivity) r0).getGroupCall() != null) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0062, code lost:
        if (r0.getId() != 0) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0010, code lost:
        if (org.telegram.messenger.LocationController.getLocationsCount() != 0) goto L_0x0066;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0069  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkVisibility() {
        /*
            r4 = this;
            boolean r0 = r4.isLocation
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0028
            org.telegram.ui.ActionBar.BaseFragment r0 = r4.fragment
            boolean r3 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x0013
            int r0 = org.telegram.messenger.LocationController.getLocationsCount()
            if (r0 == 0) goto L_0x0065
            goto L_0x0066
        L_0x0013:
            int r0 = r0.getCurrentAccount()
            org.telegram.messenger.LocationController r0 = org.telegram.messenger.LocationController.getInstance(r0)
            org.telegram.ui.ActionBar.BaseFragment r2 = r4.fragment
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            long r2 = r2.getDialogId()
            boolean r2 = r0.isSharingLocation(r2)
            goto L_0x0066
        L_0x0028:
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r0 == 0) goto L_0x0045
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r0 = r0.isHangingUp()
            if (r0 != 0) goto L_0x0045
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r0 = r0.getCallState()
            r3 = 15
            if (r0 == r3) goto L_0x0045
            goto L_0x0066
        L_0x0045:
            org.telegram.ui.ActionBar.BaseFragment r0 = r4.fragment
            boolean r3 = r0 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x0054
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.messenger.ChatObject$Call r0 = r0.getGroupCall()
            if (r0 == 0) goto L_0x0054
            goto L_0x0066
        L_0x0054:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r0 = r0.getPlayingMessageObject()
            if (r0 == 0) goto L_0x0065
            int r0 = r0.getId()
            if (r0 == 0) goto L_0x0065
            goto L_0x0066
        L_0x0065:
            r2 = 0
        L_0x0066:
            if (r2 == 0) goto L_0x0069
            goto L_0x006b
        L_0x0069:
            r1 = 8
        L_0x006b:
            r4.setVisibility(r1)
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
        int i2 = this.currentStyle;
        if (i2 != i) {
            if (i2 == 3) {
                Theme.getFragmentContextViewWavesDrawable().removeParent(this);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().unregisterStateListener(this);
                }
            }
            this.currentStyle = i;
            AvatarsImageView avatarsImageView = this.avatars;
            if (avatarsImageView != null) {
                avatarsImageView.setStyle(i);
                this.avatars.setLayoutParams(LayoutHelper.createFrame(108, getStyleHeight(), 51));
            }
            this.frameLayout.setLayoutParams(LayoutHelper.createFrame(-1, (float) getStyleHeight(), 51, 0.0f, 0.0f, 0.0f, 0.0f));
            this.shadow.setLayoutParams(LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, (float) getStyleHeight(), 0.0f, 0.0f));
            float f = this.topPadding;
            if (f > 0.0f && f != ((float) AndroidUtilities.dp2((float) getStyleHeight()))) {
                updatePaddings();
                setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
            }
            if (i == 0 || i == 2) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.titleTextView.setGravity(19);
                this.titleTextView.setTextColor(Theme.getColor("inappPlayerTitle"));
                this.titleTextView.setTag("inappPlayerTitle");
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.closeButton.setVisibility(0);
                this.playButton.setVisibility(0);
                this.muteButton.setVisibility(8);
                this.avatars.setVisibility(8);
                this.titleTextView.setTypeface(Typeface.DEFAULT);
                this.titleTextView.setTextSize(1, 15.0f);
                if (i == 0) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                    ImageView imageView = this.playbackSpeedButton;
                    if (imageView != null) {
                        imageView.setVisibility(0);
                    }
                    this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", NUM));
                    return;
                }
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
                this.closeButton.setContentDescription(LocaleController.getString("AccDescrStopLiveLocation", NUM));
            } else if (i == 4) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.muteButton.setVisibility(8);
                this.subtitleTextView.setVisibility(0);
                this.joinButton.setVisibility(0);
                this.titleTextView.setTextColor(Theme.getColor("inappPlayerTitle"));
                this.titleTextView.setTag("inappPlayerTitle");
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 15.0f);
                this.titleTextView.setPadding(0, 0, 0, 0);
                this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM));
                this.titleTextView.setGravity(51);
                this.avatars.setVisibility(0);
                updateAvatars(false);
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                ImageView imageView2 = this.playbackSpeedButton;
                if (imageView2 != null) {
                    imageView2.setVisibility(8);
                }
            } else if (i == 1 || i == 3) {
                this.selector.setBackground((Drawable) null);
                if (i == 3) {
                    updateGroupCallTitle();
                    this.muteButton.setVisibility(0);
                    this.avatars.setVisibility(0);
                    updateAvatars(false);
                    boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
                    this.isMuted = z;
                    this.muteDrawable.setCustomEndFrame(z ? 0 : 14);
                    RLottieDrawable rLottieDrawable = this.muteDrawable;
                    rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame(), false, true);
                    this.muteButton.invalidate();
                    this.frameLayout.setBackground((Drawable) null);
                    Theme.getFragmentContextViewWavesDrawable().addParent(this);
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().registerStateListener(this);
                    }
                    invalidate();
                } else {
                    this.frameLayout.setTag("returnToCallBackground");
                    this.titleTextView.setText(LocaleController.getString("ReturnToCall", NUM));
                    this.muteButton.setVisibility(8);
                    this.avatars.setVisibility(8);
                    this.frameLayout.setBackgroundColor(Theme.getColor("returnToCallBackground"));
                }
                this.titleTextView.setGravity(19);
                this.titleTextView.setTextColor(Theme.getColor("returnToCallText"));
                this.titleTextView.setTag("returnToCallText");
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 14.0f);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
                this.titleTextView.setPadding(AndroidUtilities.dp(112.0f), 0, AndroidUtilities.dp(112.0f), 0);
                ImageView imageView3 = this.playbackSpeedButton;
                if (imageView3 != null) {
                    imageView3.setVisibility(8);
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
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.groupCallVisibilityChanged);
        }
        if (this.currentStyle == 3) {
            Theme.getFragmentContextViewWavesDrawable().removeParent(this);
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
            FragmentContextView fragmentContextView = this.additionalContextView;
            if (fragmentContextView != null) {
                fragmentContextView.checkVisibility();
            }
            checkLiveLocation(true);
        } else {
            for (int i = 0; i < 3; i++) {
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.groupCallTypingsUpdated);
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
                if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).getGroupCall() == null || GroupCallPip.isShowing()) {
                    checkPlayer(true);
                    updatePlaybackButton();
                } else {
                    checkCall(true);
                }
            } else {
                checkCall(true);
            }
        }
        if (this.currentStyle == 3) {
            Theme.getFragmentContextViewWavesDrawable().addParent(this);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (this.isMuted != z) {
                this.isMuted = z;
                this.muteDrawable.setCustomEndFrame(z ? 0 : 14);
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame(), false, true);
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
            if (this.currentStyle == 3) {
                checkCall(false);
            }
            checkPlayer(false);
        } else {
            int i3 = NotificationCenter.didStartedCall;
            if (i == i3 || i == NotificationCenter.groupCallUpdated || i == NotificationCenter.groupCallVisibilityChanged) {
                checkCall(false);
                if (this.currentStyle == 3 && (sharedInstance = VoIPService.getSharedInstance()) != null && sharedInstance.groupCall != null) {
                    if (i == i3) {
                        sharedInstance.registerStateListener(this);
                    }
                    int callState = sharedInstance.getCallState();
                    if (callState != 1 && callState != 2 && callState != 6 && callState != 5 && (tLRPC$TL_groupCallParticipant = sharedInstance.groupCall.participants.get(AccountInstance.getInstance(sharedInstance.getAccount()).getUserConfig().getClientUserId())) != null && !tLRPC$TL_groupCallParticipant.can_self_unmute && tLRPC$TL_groupCallParticipant.muted && !ChatObject.canManageCalls(sharedInstance.getChat())) {
                        sharedInstance.setMicMute(true, false, false);
                        long uptimeMillis = SystemClock.uptimeMillis();
                        this.muteButton.dispatchTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0));
                    }
                }
            } else if (i == NotificationCenter.groupCallTypingsUpdated) {
                if (this.visible && this.currentStyle == 4) {
                    ChatObject.Call groupCall = ((ChatActivity) this.fragment).getGroupCall();
                    if (groupCall != null) {
                        int i4 = groupCall.call.participants_count;
                        if (i4 == 0) {
                            this.subtitleTextView.setText(LocaleController.getString("MembersTalkingNobody", NUM));
                        } else {
                            this.subtitleTextView.setText(LocaleController.formatPluralString("Participants", i4));
                        }
                    }
                    updateAvatars(true);
                }
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
            this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
            this.titleTextView.setText(spannableStringBuilder);
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
                    this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
                    if (indexOf >= 0) {
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
                    }
                    this.titleTextView.setText(spannableStringBuilder);
                }
            }
        }
    }

    private void checkPlayer(boolean z) {
        SpannableStringBuilder spannableStringBuilder;
        int i;
        if (!this.visible || !((i = this.currentStyle) == 3 || i == 4)) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            View fragmentView = this.fragment.getFragmentView();
            if (!z && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
                z = true;
            }
            if (playingMessageObject == null || playingMessageObject.getId() == 0 || playingMessageObject.isVideo()) {
                this.lastMessageObject = null;
                boolean z2 = this.supportsCalls && VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().isHangingUp() && VoIPService.getSharedInstance().getCallState() != 15 && !GroupCallPip.isShowing();
                if (!z2) {
                    BaseFragment baseFragment = this.fragment;
                    if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getGroupCall() != null && !GroupCallPip.isShowing()) {
                        z2 = true;
                    }
                }
                if (z2) {
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
                                FragmentContextView fragmentContextView = FragmentContextView.this;
                                if (fragmentContextView.checkCallAfterAnimation) {
                                    fragmentContextView.checkCall(false);
                                }
                            }
                        }
                    });
                    this.animatorSet.start();
                } else {
                    setVisibility(8);
                }
            } else {
                int i2 = this.currentStyle;
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
                                    FragmentContextView fragmentContextView = FragmentContextView.this;
                                    if (fragmentContextView.checkCallAfterAnimation) {
                                        fragmentContextView.checkCall(false);
                                    }
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
                        this.titleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
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
                        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
                    }
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), 0, playingMessageObject.getMusicAuthor().length(), 18);
                    this.titleTextView.setText(spannableStringBuilder);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00ea  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkCall(boolean r24) {
        /*
            r23 = this;
            r0 = r23
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.fragment
            android.view.View r1 = r1.getFragmentView()
            r2 = 1
            if (r24 != 0) goto L_0x0021
            if (r1 == 0) goto L_0x0021
            android.view.ViewParent r3 = r1.getParent()
            if (r3 == 0) goto L_0x001f
            android.view.ViewParent r1 = r1.getParent()
            android.view.View r1 = (android.view.View) r1
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x0021
        L_0x001f:
            r1 = 1
            goto L_0x0023
        L_0x0021:
            r1 = r24
        L_0x0023:
            boolean r3 = org.telegram.ui.Components.GroupCallPip.isShowing()
            r4 = 0
            if (r3 == 0) goto L_0x002d
            r3 = 0
        L_0x002b:
            r5 = 0
            goto L_0x0062
        L_0x002d:
            boolean r3 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r3 != 0) goto L_0x0047
            boolean r3 = r0.supportsCalls
            if (r3 == 0) goto L_0x0047
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x0047
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r3 = r3.isHangingUp()
            if (r3 != 0) goto L_0x0047
            r3 = 1
            goto L_0x0048
        L_0x0047:
            r3 = 0
        L_0x0048:
            boolean r5 = org.telegram.ui.GroupCallActivity.groupCallUiVisible
            if (r5 != 0) goto L_0x002b
            boolean r5 = r0.supportsCalls
            if (r5 == 0) goto L_0x002b
            if (r3 != 0) goto L_0x002b
            org.telegram.ui.ActionBar.BaseFragment r5 = r0.fragment
            boolean r6 = r5 instanceof org.telegram.ui.ChatActivity
            if (r6 == 0) goto L_0x002b
            org.telegram.ui.ChatActivity r5 = (org.telegram.ui.ChatActivity) r5
            org.telegram.messenger.ChatObject$Call r5 = r5.getGroupCall()
            if (r5 == 0) goto L_0x002b
            r3 = 1
            r5 = 1
        L_0x0062:
            java.lang.String r6 = "topPadding"
            r7 = 220(0xdc, double:1.087E-321)
            r9 = 4
            r10 = 3
            r11 = 0
            r12 = 0
            if (r3 != 0) goto L_0x00ea
            boolean r3 = r0.visible
            r5 = -1
            r13 = 8
            if (r3 == 0) goto L_0x00db
            if (r1 == 0) goto L_0x0079
            int r3 = r0.currentStyle
            if (r3 == r5) goto L_0x007f
        L_0x0079:
            int r3 = r0.currentStyle
            if (r3 == r9) goto L_0x007f
            if (r3 != r10) goto L_0x00db
        L_0x007f:
            r0.visible = r4
            if (r1 == 0) goto L_0x0091
            int r1 = r23.getVisibility()
            if (r1 == r13) goto L_0x008c
            r0.setVisibility(r13)
        L_0x008c:
            r0.setTopPadding(r11)
            goto L_0x031b
        L_0x0091:
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x009a
            r1.cancel()
            r0.animatorSet = r12
        L_0x009a:
            int r1 = r0.account
            org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r5 = r0.animationIndex
            int r3 = r3.setAnimationInProgress(r5, r12)
            r0.animationIndex = r3
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            r0.animatorSet = r3
            android.animation.Animator[] r5 = new android.animation.Animator[r2]
            float[] r2 = new float[r2]
            r2[r4] = r11
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r6, r2)
            r5[r4] = r2
            r3.playTogether(r5)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.setDuration(r7)
            android.animation.AnimatorSet r2 = r0.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            android.animation.AnimatorSet r2 = r0.animatorSet
            org.telegram.ui.Components.FragmentContextView$9 r3 = new org.telegram.ui.Components.FragmentContextView$9
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
            goto L_0x031b
        L_0x00db:
            int r1 = r0.currentStyle
            if (r1 == r5) goto L_0x00e3
            if (r1 == r9) goto L_0x00e3
            if (r1 != r10) goto L_0x031b
        L_0x00e3:
            r0.visible = r4
            r0.setVisibility(r13)
            goto L_0x031b
        L_0x00ea:
            if (r5 == 0) goto L_0x00ee
            r3 = 4
            goto L_0x00ff
        L_0x00ee:
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x00fe
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$Call r3 = r3.groupCall
            if (r3 == 0) goto L_0x00fe
            r3 = 3
            goto L_0x00ff
        L_0x00fe:
            r3 = 1
        L_0x00ff:
            int r13 = r0.currentStyle
            if (r3 == r13) goto L_0x010c
            android.animation.AnimatorSet r14 = r0.animatorSet
            if (r14 == 0) goto L_0x010c
            if (r1 != 0) goto L_0x010c
            r0.checkCallAfterAnimation = r2
            return
        L_0x010c:
            if (r3 == r13) goto L_0x0166
            boolean r3 = r0.visible
            if (r3 == 0) goto L_0x0166
            if (r1 != 0) goto L_0x0166
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x011d
            r1.cancel()
            r0.animatorSet = r12
        L_0x011d:
            int r1 = r0.account
            android.animation.AnimatorSet r3 = r0.animatorSet
            if (r3 == 0) goto L_0x0128
            r3.cancel()
            r0.animatorSet = r12
        L_0x0128:
            org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r5 = r0.animationIndex
            int r3 = r3.setAnimationInProgress(r5, r12)
            r0.animationIndex = r3
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            r0.animatorSet = r3
            android.animation.Animator[] r5 = new android.animation.Animator[r2]
            float[] r2 = new float[r2]
            r2[r4] = r11
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r6, r2)
            r5[r4] = r2
            r3.playTogether(r5)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.setDuration(r7)
            android.animation.AnimatorSet r2 = r0.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            android.animation.AnimatorSet r2 = r0.animatorSet
            org.telegram.ui.Components.FragmentContextView$10 r3 = new org.telegram.ui.Components.FragmentContextView$10
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
            return
        L_0x0166:
            if (r5 == 0) goto L_0x0256
            if (r13 != r9) goto L_0x0170
            boolean r3 = r0.visible
            if (r3 == 0) goto L_0x0170
            r3 = 1
            goto L_0x0171
        L_0x0170:
            r3 = 0
        L_0x0171:
            r0.updateStyle(r9)
            r0.updateAvatars(r3)
            org.telegram.ui.ActionBar.BaseFragment r5 = r0.fragment
            org.telegram.ui.ChatActivity r5 = (org.telegram.ui.ChatActivity) r5
            org.telegram.messenger.ChatObject$Call r5 = r5.getGroupCall()
            org.telegram.tgnet.TLRPC$GroupCall r9 = r5.call
            int r9 = r9.participants_count
            if (r9 != 0) goto L_0x0194
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r9 = r0.subtitleTextView
            r13 = 2131625907(0x7f0e07b3, float:1.8879035E38)
            java.lang.String r14 = "MembersTalkingNobody"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r9.setText(r13)
            goto L_0x019f
        L_0x0194:
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r13 = r0.subtitleTextView
            java.lang.String r14 = "Participants"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r14, r9)
            r13.setText(r9)
        L_0x019f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r5 = r5.sortedParticipants
            int r5 = r5.size()
            int r5 = java.lang.Math.min(r10, r5)
            r9 = 10
            if (r5 != 0) goto L_0x01ae
            goto L_0x01b5
        L_0x01ae:
            int r5 = r5 - r2
            int r5 = r5 * 24
            int r5 = r5 + r9
            int r5 = r5 + 32
            int r9 = r9 + r5
        L_0x01b5:
            org.telegram.ui.Components.AvatarsImageView r5 = r0.avatars
            boolean r5 = r5.wasDraw
            if (r5 == 0) goto L_0x020b
            if (r3 == 0) goto L_0x020b
            android.widget.TextView r3 = r0.titleTextView
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r3 = r3.leftMargin
            float r5 = (float) r9
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            if (r10 == r3) goto L_0x0227
            android.widget.TextView r10 = r0.titleTextView
            float r10 = r10.getTranslationX()
            float r3 = (float) r3
            float r10 = r10 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            float r10 = r10 - r3
            android.widget.TextView r3 = r0.titleTextView
            r3.setTranslationX(r10)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r3 = r0.subtitleTextView
            r3.setTranslationX(r10)
            android.widget.TextView r3 = r0.titleTextView
            android.view.ViewPropertyAnimator r3 = r3.animate()
            android.view.ViewPropertyAnimator r3 = r3.translationX(r11)
            android.view.ViewPropertyAnimator r3 = r3.setDuration(r7)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r3.setInterpolator(r5)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r3 = r0.subtitleTextView
            android.view.ViewPropertyAnimator r3 = r3.animate()
            android.view.ViewPropertyAnimator r3 = r3.translationX(r11)
            android.view.ViewPropertyAnimator r3 = r3.setDuration(r7)
            r3.setInterpolator(r5)
            goto L_0x0227
        L_0x020b:
            android.widget.TextView r3 = r0.titleTextView
            android.view.ViewPropertyAnimator r3 = r3.animate()
            r3.cancel()
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r3 = r0.subtitleTextView
            android.view.ViewPropertyAnimator r3 = r3.animate()
            r3.cancel()
            android.widget.TextView r3 = r0.titleTextView
            r3.setTranslationX(r11)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r3 = r0.subtitleTextView
            r3.setTranslationX(r11)
        L_0x0227:
            android.widget.TextView r3 = r0.titleTextView
            r13 = -1
            r14 = 1101004800(0x41a00000, float:20.0)
            r15 = 51
            float r5 = (float) r9
            r17 = 1084227584(0x40a00000, float:5.0)
            r18 = 1108344832(0x42100000, float:36.0)
            r19 = 0
            r16 = r5
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r3.setLayoutParams(r9)
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r3 = r0.subtitleTextView
            r16 = -1
            r17 = 1101004800(0x41a00000, float:20.0)
            r18 = 51
            r20 = 1103626240(0x41CLASSNAME, float:25.0)
            r21 = 1108344832(0x42100000, float:36.0)
            r22 = 0
            r19 = r5
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r3.setLayoutParams(r5)
            goto L_0x0275
        L_0x0256:
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x0272
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$Call r3 = r3.groupCall
            if (r3 == 0) goto L_0x0272
            int r3 = r0.currentStyle
            if (r3 != r10) goto L_0x026a
            r3 = 1
            goto L_0x026b
        L_0x026a:
            r3 = 0
        L_0x026b:
            r0.updateAvatars(r3)
            r0.updateStyle(r10)
            goto L_0x0275
        L_0x0272:
            r0.updateStyle(r2)
        L_0x0275:
            boolean r3 = r0.visible
            if (r3 != 0) goto L_0x031b
            if (r1 != 0) goto L_0x0306
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x0284
            r1.cancel()
            r0.animatorSet = r12
        L_0x0284:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r0.animatorSet = r1
            org.telegram.ui.Components.FragmentContextView r1 = r0.additionalContextView
            if (r1 == 0) goto L_0x02af
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x02af
            android.view.ViewGroup$LayoutParams r1 = r23.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r3 = r23.getStyleHeight()
            org.telegram.ui.Components.FragmentContextView r5 = r0.additionalContextView
            int r5 = r5.getStyleHeight()
            int r3 = r3 + r5
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            r1.topMargin = r3
            goto L_0x02c1
        L_0x02af:
            android.view.ViewGroup$LayoutParams r1 = r23.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r3 = r23.getStyleHeight()
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            r1.topMargin = r3
        L_0x02c1:
            int r1 = r0.account
            org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r5 = r0.animationIndex
            int r3 = r3.setAnimationInProgress(r5, r12)
            r0.animationIndex = r3
            android.animation.AnimatorSet r3 = r0.animatorSet
            android.animation.Animator[] r5 = new android.animation.Animator[r2]
            float[] r9 = new float[r2]
            int r10 = r23.getStyleHeight()
            float r10 = (float) r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp2(r10)
            float r10 = (float) r10
            r9[r4] = r10
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r0, r6, r9)
            r5[r4] = r6
            r3.playTogether(r5)
            android.animation.AnimatorSet r3 = r0.animatorSet
            r3.setDuration(r7)
            android.animation.AnimatorSet r3 = r0.animatorSet
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r3.setInterpolator(r5)
            android.animation.AnimatorSet r3 = r0.animatorSet
            org.telegram.ui.Components.FragmentContextView$11 r5 = new org.telegram.ui.Components.FragmentContextView$11
            r5.<init>(r1)
            r3.addListener(r5)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
            goto L_0x0316
        L_0x0306:
            r23.updatePaddings()
            int r1 = r23.getStyleHeight()
            float r1 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp2(r1)
            float r1 = (float) r1
            r0.setTopPadding(r1)
        L_0x0316:
            r0.visible = r2
            r0.setVisibility(r4)
        L_0x031b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkCall(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006d A[LOOP:1: B:26:0x006d->B:27:0x006f, LOOP_START, PHI: r4 
      PHI: (r4v1 int) = (r4v0 int), (r4v2 int) binds: [B:18:0x0045, B:27:0x006f] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAvatars(boolean r9) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x000b
            org.telegram.ui.Components.AvatarsImageView r0 = r8.avatars
            android.animation.ValueAnimator r0 = r0.transitionProgressAnimator
            if (r0 == 0) goto L_0x000b
            r0.cancel()
        L_0x000b:
            org.telegram.ui.Components.AvatarsImageView r0 = r8.avatars
            android.animation.ValueAnimator r1 = r0.transitionProgressAnimator
            if (r1 != 0) goto L_0x007d
            int r0 = r8.currentStyle
            r1 = 4
            r2 = 0
            if (r0 != r1) goto L_0x002b
            org.telegram.ui.ActionBar.BaseFragment r0 = r8.fragment
            boolean r1 = r0 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x0028
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.messenger.ChatObject$Call r1 = r0.getGroupCall()
            int r0 = r0.getCurrentAccount()
            goto L_0x0043
        L_0x0028:
            int r0 = r8.account
            goto L_0x0042
        L_0x002b:
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r0 == 0) goto L_0x0040
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$Call r1 = r0.groupCall
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r0 = r0.getAccount()
            goto L_0x0043
        L_0x0040:
            int r0 = r8.account
        L_0x0042:
            r1 = r2
        L_0x0043:
            r3 = 3
            r4 = 0
            if (r1 == 0) goto L_0x006d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r5 = r1.sortedParticipants
            int r5 = r5.size()
        L_0x004d:
            if (r4 >= r3) goto L_0x0067
            if (r4 >= r5) goto L_0x005f
            org.telegram.ui.Components.AvatarsImageView r6 = r8.avatars
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r7 = r1.sortedParticipants
            java.lang.Object r7 = r7.get(r4)
            org.telegram.tgnet.TLObject r7 = (org.telegram.tgnet.TLObject) r7
            r6.setObject(r4, r0, r7)
            goto L_0x0064
        L_0x005f:
            org.telegram.ui.Components.AvatarsImageView r6 = r8.avatars
            r6.setObject(r4, r0, r2)
        L_0x0064:
            int r4 = r4 + 1
            goto L_0x004d
        L_0x0067:
            org.telegram.ui.Components.AvatarsImageView r0 = r8.avatars
            r0.commitTransition(r9)
            goto L_0x0080
        L_0x006d:
            if (r4 >= r3) goto L_0x0077
            org.telegram.ui.Components.AvatarsImageView r1 = r8.avatars
            r1.setObject(r4, r0, r2)
            int r4 = r4 + 1
            goto L_0x006d
        L_0x0077:
            org.telegram.ui.Components.AvatarsImageView r0 = r8.avatars
            r0.commitTransition(r9)
            goto L_0x0080
        L_0x007d:
            r0.updateAfterTransitionEnd()
        L_0x0080:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.updateAvatars(boolean):void");
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
            if (this.currentStyle != 3 || !this.drawOverlay) {
                Canvas canvas2 = canvas;
            } else {
                Theme.getFragmentContextViewWavesDrawable().updateState();
                float dp = this.topPadding / ((float) AndroidUtilities.dp((float) getStyleHeight()));
                if (this.collapseTransition) {
                    Theme.getFragmentContextViewWavesDrawable().draw(0.0f, (((float) AndroidUtilities.dp((float) getStyleHeight())) - this.topPadding) + this.extraHeight, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)), canvas, (View) null, Math.min(dp, 1.0f - this.collapseProgress));
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
            }
            super.dispatchDraw(canvas);
            if (z) {
                canvas.restore();
            }
        }
    }

    public void setDrawOverlay(boolean z) {
        this.drawOverlay = z;
    }

    public void invalidate() {
        super.invalidate();
        if (this.currentStyle == 3 && getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public int getCurrentStyle() {
        return this.currentStyle;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        updatePaddings();
        setTopPadding(this.topPadding);
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
        updateGroupCallTitle();
    }

    private void updateGroupCallTitle() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && this.currentStyle == 3) {
            int callState = sharedInstance.getCallState();
            if (callState == 1 || callState == 2 || callState == 6 || callState == 5) {
                this.titleTextView.setText(LocaleController.getString("VoipGroupConnecting", NUM));
            } else if (sharedInstance.getChat() != null) {
                BaseFragment baseFragment = this.fragment;
                if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).getCurrentChat() == null || ((ChatActivity) this.fragment).getCurrentChat().id != sharedInstance.getChat().id) {
                    this.titleTextView.setText(sharedInstance.getChat().title);
                } else {
                    this.titleTextView.setText(LocaleController.getString("VoipGroupViewVoiceChat", NUM));
                }
            }
        }
    }
}
