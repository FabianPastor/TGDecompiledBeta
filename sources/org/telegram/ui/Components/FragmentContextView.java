package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.annotation.Keep;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.VoIPActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenterDelegate {
    private FragmentContextView additionalContextView;
    private AnimatorSet animatorSet;
    private Runnable checkLocationRunnable = new Runnable() {
        public void run() {
            FragmentContextView.this.checkLocationString();
            AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000);
        }
    };
    private ImageView closeButton;
    private int currentStyle = -1;
    private boolean firstLocationsLoaded;
    private BaseFragment fragment;
    private FrameLayout frameLayout;
    private boolean isLocation;
    private boolean isMusic;
    private int lastLocationSharingCount = -1;
    private MessageObject lastMessageObject;
    private String lastString;
    private boolean loadingSharingCount;
    private ImageView playButton;
    private ImageView playbackSpeedButton;
    private TextView titleTextView;
    private float topPadding;
    private boolean visible;
    private float yPosition;

    public FragmentContextView(Context context, BaseFragment baseFragment, boolean z) {
        super(context);
        this.fragment = baseFragment;
        this.visible = true;
        this.isLocation = z;
        ((ViewGroup) this.fragment.getFragmentView()).setClipToPadding(false);
        setTag(Integer.valueOf(1));
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setWillNotDraw(false);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context);
        view.setBackgroundResource(NUM);
        addView(view, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), Mode.MULTIPLY));
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new -$$Lambda$FragmentContextView$xaUr_8Yxtvar_bWuD8Iv6_gj2wjI(this));
        this.titleTextView = new TextView(context);
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(19);
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        if (!z) {
            this.playbackSpeedButton = new ImageView(context);
            this.playbackSpeedButton.setScaleType(ScaleType.CENTER);
            this.playbackSpeedButton.setImageResource(NUM);
            this.playbackSpeedButton.setContentDescription(LocaleController.getString("AccDescrPlayerSpeed", NUM));
            if (AndroidUtilities.density >= 3.0f) {
                this.playbackSpeedButton.setPadding(0, 1, 0, 0);
            }
            addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
            this.playbackSpeedButton.setOnClickListener(new -$$Lambda$FragmentContextView$mn4uoFbwvEVtbgf8Ptja5mtZJQM(this));
            updatePlaybackButton();
        }
        this.closeButton = new ImageView(context);
        this.closeButton.setImageResource(NUM);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), Mode.MULTIPLY));
        this.closeButton.setScaleType(ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36, 53));
        this.closeButton.setOnClickListener(new -$$Lambda$FragmentContextView$A00dqLRerQA-JpKS29_7k-ZhsDA(this));
        setOnClickListener(new -$$Lambda$FragmentContextView$oHS8Qv6e4NDG6yqH2reqe1Dmqu0(this));
    }

    public /* synthetic */ void lambda$new$0$FragmentContextView(View view) {
        if (this.currentStyle != 0) {
            return;
        }
        if (MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else {
            MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    public /* synthetic */ void lambda$new$1$FragmentContextView(View view) {
        if (MediaController.getInstance().getPlaybackSpeed(this.isMusic) > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.8f);
        }
    }

    public /* synthetic */ void lambda$new$3$FragmentContextView(View view) {
        if (this.currentStyle == 2) {
            Builder builder = new Builder(this.fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("StopLiveLocationAlertToTitle", NUM));
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof DialogsActivity) {
                builder.setMessage(LocaleController.getString("StopLiveLocationAlertAllText", NUM));
            } else {
                ChatActivity chatActivity = (ChatActivity) baseFragment;
                Chat currentChat = chatActivity.getCurrentChat();
                User currentUser = chatActivity.getCurrentUser();
                if (currentChat != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToGroupText", NUM, currentChat.title)));
                } else if (currentUser != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToUserText", NUM, UserObject.getFirstName(currentUser))));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSure", NUM));
                }
            }
            builder.setPositiveButton(LocaleController.getString("Stop", NUM), new -$$Lambda$FragmentContextView$n_uFawyX6mh1KEybpEHLF-yq7cs(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
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

    public /* synthetic */ void lambda$null$2$FragmentContextView(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            for (int i2 = 0; i2 < 3; i2++) {
                LocationController.getInstance(i2).removeAllLocationSharings();
            }
            return;
        }
        LocationController.getInstance(baseFragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) this.fragment).getDialogId());
    }

    public /* synthetic */ void lambda$new$4$FragmentContextView(View view) {
        int i = this.currentStyle;
        long j = 0;
        int i2;
        if (i == 0) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment != null && playingMessageObject != null) {
                if (playingMessageObject.isMusic()) {
                    this.fragment.showDialog(new AudioPlayerAlert(getContext()));
                    return;
                }
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    j = ((ChatActivity) baseFragment).getDialogId();
                }
                if (playingMessageObject.getDialogId() == j) {
                    ((ChatActivity) this.fragment).scrollToMessageId(playingMessageObject.getId(), 0, false, 0, true);
                    return;
                }
                j = playingMessageObject.getDialogId();
                Bundle bundle = new Bundle();
                i2 = (int) j;
                int i3 = (int) (j >> 32);
                if (i2 == 0) {
                    bundle.putInt("enc_id", i3);
                } else if (i2 > 0) {
                    bundle.putInt("user_id", i2);
                } else if (i2 < 0) {
                    bundle.putInt("chat_id", -i2);
                }
                bundle.putInt("message_id", playingMessageObject.getId());
                this.fragment.presentFragment(new ChatActivity(bundle), this.fragment instanceof ChatActivity);
            }
        } else if (i == 1) {
            Intent intent = new Intent(getContext(), VoIPActivity.class);
            intent.addFlags(NUM);
            getContext().startActivity(intent);
        } else if (i == 2) {
            long dialogId;
            i = UserConfig.selectedAccount;
            BaseFragment baseFragment2 = this.fragment;
            if (baseFragment2 instanceof ChatActivity) {
                dialogId = ((ChatActivity) baseFragment2).getDialogId();
                i = this.fragment.getCurrentAccount();
            } else {
                if (LocationController.getLocationsCount() == 1) {
                    for (i2 = 0; i2 < 3; i2++) {
                        if (!LocationController.getInstance(i2).sharingLocationsUI.isEmpty()) {
                            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) LocationController.getInstance(i2).sharingLocationsUI.get(0);
                            dialogId = sharingLocationInfo.did;
                            i = sharingLocationInfo.messageObject.currentAccount;
                            break;
                        }
                    }
                }
                dialogId = 0;
            }
            if (dialogId != 0) {
                openSharingLocation(LocationController.getInstance(i).getSharingLocationInfo(dialogId));
            } else {
                this.fragment.showDialog(new SharingLocationsAlert(getContext(), new -$$Lambda$FragmentContextView$Z72HHKSvAYjXtWgaFaDIC5DAHI8(this)));
            }
        }
    }

    private void updatePlaybackButton() {
        if (this.playbackSpeedButton != null) {
            if (MediaController.getInstance().getPlaybackSpeed(this.isMusic) > 1.0f) {
                this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), Mode.MULTIPLY));
            } else {
                this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), Mode.MULTIPLY));
            }
        }
    }

    public void setAdditionalContextView(FragmentContextView fragmentContextView) {
        this.additionalContextView = fragmentContextView;
    }

    private void openSharingLocation(SharingLocationInfo sharingLocationInfo) {
        if (sharingLocationInfo != null && this.fragment.getParentActivity() != null) {
            LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
            launchActivity.switchToAccount(sharingLocationInfo.messageObject.currentAccount, true);
            LocationActivity locationActivity = new LocationActivity(2);
            locationActivity.setMessageObject(sharingLocationInfo.messageObject);
            locationActivity.setDelegate(new -$$Lambda$FragmentContextView$z_fnb_TazpUpwkTqfofStIvH9G8(sharingLocationInfo, sharingLocationInfo.messageObject.getDialogId()));
            launchActivity.lambda$runLinkRequest$28$LaunchActivity(locationActivity);
        }
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0050  */
    /* JADX WARNING: Missing block: B:5:0x0010, code skipped:
            if (org.telegram.messenger.LocationController.getLocationsCount() != 0) goto L_0x004d;
     */
    /* JADX WARNING: Missing block: B:14:0x0049, code skipped:
            if (r0.getId() != 0) goto L_0x004d;
     */
    private void checkVisibility() {
        /*
        r5 = this;
        r0 = r5.isLocation;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x0028;
    L_0x0006:
        r0 = r5.fragment;
        r3 = r0 instanceof org.telegram.ui.DialogsActivity;
        if (r3 == 0) goto L_0x0013;
    L_0x000c:
        r0 = org.telegram.messenger.LocationController.getLocationsCount();
        if (r0 == 0) goto L_0x004c;
    L_0x0012:
        goto L_0x004d;
    L_0x0013:
        r0 = r0.getCurrentAccount();
        r0 = org.telegram.messenger.LocationController.getInstance(r0);
        r1 = r5.fragment;
        r1 = (org.telegram.ui.ChatActivity) r1;
        r3 = r1.getDialogId();
        r1 = r0.isSharingLocation(r3);
        goto L_0x004d;
    L_0x0028:
        r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance();
        if (r0 == 0) goto L_0x003b;
    L_0x002e:
        r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance();
        r0 = r0.getCallState();
        r3 = 15;
        if (r0 == r3) goto L_0x003b;
    L_0x003a:
        goto L_0x004d;
    L_0x003b:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r0 = r0.getPlayingMessageObject();
        if (r0 == 0) goto L_0x004c;
    L_0x0045:
        r0 = r0.getId();
        if (r0 == 0) goto L_0x004c;
    L_0x004b:
        goto L_0x004d;
    L_0x004c:
        r1 = 0;
    L_0x004d:
        if (r1 == 0) goto L_0x0050;
    L_0x004f:
        goto L_0x0052;
    L_0x0050:
        r2 = 8;
    L_0x0052:
        r5.setVisibility(r2);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkVisibility():void");
    }

    @Keep
    public void setTopPadding(float f) {
        this.topPadding = f;
        if (this.fragment != null && getParent() != null) {
            View fragmentView = this.fragment.getFragmentView();
            this.fragment.getActionBar();
            FragmentContextView fragmentContextView = this.additionalContextView;
            int dp = (fragmentContextView == null || fragmentContextView.getVisibility() != 0 || this.additionalContextView.getParent() == null) ? 0 : AndroidUtilities.dp(36.0f);
            if (!(fragmentView == null || getParent() == null)) {
                fragmentView.setPadding(0, ((int) this.topPadding) + dp, 0, 0);
            }
            if (this.isLocation) {
                FragmentContextView fragmentContextView2 = this.additionalContextView;
                if (fragmentContextView2 != null) {
                    ((LayoutParams) fragmentContextView2.getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0f)) - ((int) this.topPadding);
                }
            }
        }
    }

    private void updateStyle(int i) {
        if (this.currentStyle != i) {
            this.currentStyle = i;
            ImageView imageView;
            if (i == 0 || i == 2) {
                String str = "inappPlayerBackground";
                this.frameLayout.setBackgroundColor(Theme.getColor(str));
                this.frameLayout.setTag(str);
                str = "inappPlayerTitle";
                this.titleTextView.setTextColor(Theme.getColor(str));
                this.titleTextView.setTag(str);
                this.closeButton.setVisibility(0);
                this.playButton.setVisibility(0);
                this.titleTextView.setTypeface(Typeface.DEFAULT);
                this.titleTextView.setTextSize(1, 15.0f);
                if (i == 0) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                    imageView = this.playbackSpeedButton;
                    if (imageView != null) {
                        imageView.setVisibility(0);
                    }
                    this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", NUM));
                } else if (i == 2) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
                    this.closeButton.setContentDescription(LocaleController.getString("AccDescrStopLiveLocation", NUM));
                }
            } else if (i == 1) {
                this.titleTextView.setText(LocaleController.getString("ReturnToCall", NUM));
                String str2 = "returnToCallBackground";
                this.frameLayout.setBackgroundColor(Theme.getColor(str2));
                this.frameLayout.setTag(str2);
                str2 = "returnToCallText";
                this.titleTextView.setTextColor(Theme.getColor(str2));
                this.titleTextView.setTag(str2);
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 14.0f);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
                this.titleTextView.setPadding(0, 0, 0, 0);
                imageView = this.playbackSpeedButton;
                if (imageView != null) {
                    imageView.setVisibility(8);
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.topPadding = 0.0f;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsCacheChanged);
            return;
        }
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messagePlayingSpeedChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndedCall);
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FragmentContextView fragmentContextView;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
            fragmentContextView = this.additionalContextView;
            if (fragmentContextView != null) {
                fragmentContextView.checkVisibility();
            }
            checkLiveLocation(true);
            return;
        }
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidStart);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messagePlayingSpeedChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndedCall);
        fragmentContextView = this.additionalContextView;
        if (fragmentContextView != null) {
            fragmentContextView.checkVisibility();
        }
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            checkPlayer(true);
            updatePlaybackButton();
            return;
        }
        checkCall(true);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, AndroidUtilities.dp2(39.0f));
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
        } else if (i == NotificationCenter.liveLocationsCacheChanged) {
            if (this.fragment instanceof ChatActivity) {
                if (((ChatActivity) this.fragment).getDialogId() == ((Long) objArr[0]).longValue()) {
                    checkLocationString();
                }
            }
        } else if (i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.didEndedCall) {
            checkPlayer(false);
        } else if (i == NotificationCenter.didStartedCall) {
            checkCall(false);
        } else if (i == NotificationCenter.messagePlayingSpeedChanged) {
            updatePlaybackButton();
        } else {
            checkPlayer(false);
        }
    }

    private void checkLiveLocation(boolean z) {
        View fragmentView = this.fragment.getFragmentView();
        if (!(z || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            z = true;
        }
        BaseFragment baseFragment = this.fragment;
        boolean isSharingLocation = baseFragment instanceof DialogsActivity ? LocationController.getLocationsCount() != 0 : LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        String str = "topPadding";
        AnimatorSet animatorSet;
        if (isSharingLocation) {
            updateStyle(2);
            this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), 1));
            if (z && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!z) {
                    animatorSet = this.animatorSet;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    animatorSet = this.animatorSet;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, str, new float[]{(float) AndroidUtilities.dp2(36.0f)});
                    animatorSet.playTogether(animatorArr);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                                FragmentContextView.this.animatorSet = null;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
            if (this.fragment instanceof DialogsActivity) {
                String firstName;
                String string = LocaleController.getString("AttachLiveLocation", NUM);
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < 3; i++) {
                    arrayList.addAll(LocationController.getInstance(i).sharingLocationsUI);
                }
                if (arrayList.size() == 1) {
                    SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) arrayList.get(0);
                    int dialogId = (int) sharingLocationInfo.messageObject.getDialogId();
                    if (dialogId > 0) {
                        firstName = UserObject.getFirstName(MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getUser(Integer.valueOf(dialogId)));
                    } else {
                        Chat chat = MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getChat(Integer.valueOf(-dialogId));
                        firstName = chat != null ? chat.title : "";
                    }
                } else {
                    firstName = LocaleController.formatPluralString("Chats", arrayList.size());
                }
                String format = String.format(LocaleController.getString("AttachLiveLocationIsSharing", NUM), new Object[]{string, firstName});
                int indexOf = format.indexOf(string);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(format);
                this.titleTextView.setEllipsize(TruncateAt.END);
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
                this.titleTextView.setText(spannableStringBuilder);
                return;
            }
            this.checkLocationRunnable.run();
            checkLocationString();
            return;
        }
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
            animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            animatorSet = this.animatorSet;
            Animator[] animatorArr2 = new Animator[1];
            animatorArr2[0] = ObjectAnimator.ofFloat(this, str, new float[]{0.0f});
            animatorSet.playTogether(animatorArr2);
            this.animatorSet.setDuration(200);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                        FragmentContextView.this.setVisibility(8);
                        FragmentContextView.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }

    private void checkLocationString() {
        BaseFragment baseFragment = this.fragment;
        if ((baseFragment instanceof ChatActivity) && this.titleTextView != null) {
            int i;
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            long dialogId = chatActivity.getDialogId();
            int currentAccount = chatActivity.getCurrentAccount();
            ArrayList arrayList = (ArrayList) LocationController.getInstance(currentAccount).locationsCache.get(dialogId);
            if (!this.firstLocationsLoaded) {
                LocationController.getInstance(currentAccount).loadLiveLocations(dialogId);
                this.firstLocationsLoaded = true;
            }
            User user = null;
            if (arrayList != null) {
                int clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                int currentTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                User user2 = null;
                i = 0;
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    Message message = (Message) arrayList.get(i2);
                    MessageMedia messageMedia = message.media;
                    if (messageMedia != null && message.date + messageMedia.period > currentTime) {
                        if (user2 == null && message.from_id != clientUserId) {
                            user2 = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(message.from_id));
                        }
                        i++;
                    }
                }
                user = user2;
            } else {
                i = 0;
            }
            if (this.lastLocationSharingCount != i) {
                String str;
                this.lastLocationSharingCount = i;
                String string = LocaleController.getString("AttachLiveLocation", NUM);
                if (i == 0) {
                    str = string;
                } else {
                    i--;
                    boolean isSharingLocation = LocationController.getInstance(currentAccount).isSharingLocation(dialogId);
                    String str2 = "AndOther";
                    String str3 = "%1$s - %2$s %3$s";
                    String str4 = "%1$s - %2$s";
                    if (isSharingLocation) {
                        String str5 = "ChatYourSelfName";
                        if (i == 0) {
                            str = String.format(str4, new Object[]{string, LocaleController.getString(str5, NUM)});
                        } else if (i != 1 || user == null) {
                            str = String.format(str3, new Object[]{string, LocaleController.getString(str5, NUM), LocaleController.formatPluralString(str2, i)});
                        } else {
                            Object[] objArr = new Object[2];
                            objArr[0] = string;
                            objArr[1] = LocaleController.formatString("SharingYouAndOtherName", NUM, UserObject.getFirstName(user));
                            str = String.format(str4, objArr);
                        }
                    } else if (i != 0) {
                        str = String.format(str3, new Object[]{string, UserObject.getFirstName(user), LocaleController.formatPluralString(str2, i)});
                    } else {
                        str = String.format(str4, new Object[]{string, UserObject.getFirstName(user)});
                    }
                }
                if (!str.equals(this.lastString)) {
                    this.lastString = str;
                    int indexOf = str.indexOf(string);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                    this.titleTextView.setEllipsize(TruncateAt.END);
                    if (indexOf >= 0) {
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
                    }
                    this.titleTextView.setText(spannableStringBuilder);
                }
            }
        }
    }

    private void checkPlayer(boolean z) {
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        View fragmentView = this.fragment.getFragmentView();
        if (!(z || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            z = true;
        }
        String str = "topPadding";
        AnimatorSet animatorSet;
        if (playingMessageObject == null || playingMessageObject.getId() == 0 || playingMessageObject.isVideo()) {
            this.lastMessageObject = null;
            Object obj = (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) ? null : 1;
            if (obj != null) {
                checkCall(false);
            } else if (this.visible) {
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                } else {
                    animatorSet = this.animatorSet;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    animatorSet = this.animatorSet;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, str, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                                FragmentContextView.this.setVisibility(8);
                                FragmentContextView.this.animatorSet = null;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
            }
        } else {
            int i = this.currentStyle;
            updateStyle(0);
            if (z && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                FragmentContextView fragmentContextView = this.additionalContextView;
                if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                } else {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                }
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!z) {
                    animatorSet = this.animatorSet;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    FragmentContextView fragmentContextView2 = this.additionalContextView;
                    if (fragmentContextView2 == null || fragmentContextView2.getVisibility() != 0) {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                    } else {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                    }
                    animatorSet = this.animatorSet;
                    Animator[] animatorArr2 = new Animator[1];
                    animatorArr2[0] = ObjectAnimator.ofFloat(this, str, new float[]{(float) AndroidUtilities.dp2(36.0f)});
                    animatorSet.playTogether(animatorArr2);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                                FragmentContextView.this.animatorSet = null;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
            if (MediaController.getInstance().isMessagePaused()) {
                this.playButton.setImageResource(NUM);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                this.playButton.setImageResource(NUM);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            if (!(this.lastMessageObject == playingMessageObject && i == 0)) {
                SpannableStringBuilder spannableStringBuilder;
                this.lastMessageObject = playingMessageObject;
                if (this.lastMessageObject.isVoice() || this.lastMessageObject.isRoundVideo()) {
                    this.isMusic = false;
                    ImageView imageView = this.playbackSpeedButton;
                    if (imageView != null) {
                        imageView.setAlpha(1.0f);
                        this.playbackSpeedButton.setEnabled(true);
                    }
                    this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                    spannableStringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{playingMessageObject.getMusicAuthor(), playingMessageObject.getMusicTitle()}));
                    this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
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
                    this.titleTextView.setEllipsize(TruncateAt.END);
                }
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), 0, playingMessageObject.getMusicAuthor().length(), 18);
                this.titleTextView.setText(spannableStringBuilder);
            }
        }
    }

    private void checkCall(boolean z) {
        View fragmentView = this.fragment.getFragmentView();
        if (!(z || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            z = true;
        }
        Object obj = (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) ? null : 1;
        String str = "topPadding";
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (obj != null) {
            updateStyle(1);
            if (z && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                FragmentContextView fragmentContextView = this.additionalContextView;
                if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                } else {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                }
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!z) {
                    animatorSet = this.animatorSet;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    FragmentContextView fragmentContextView2 = this.additionalContextView;
                    if (fragmentContextView2 == null || fragmentContextView2.getVisibility() != 0) {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                    } else {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                    }
                    animatorSet = this.animatorSet;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, str, new float[]{(float) AndroidUtilities.dp2(36.0f)});
                    animatorSet.playTogether(animatorArr);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                                FragmentContextView.this.animatorSet = null;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
        } else if (this.visible) {
            this.visible = false;
            if (z) {
                if (getVisibility() != 8) {
                    setVisibility(8);
                }
                setTopPadding(0.0f);
                return;
            }
            animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            animatorSet = this.animatorSet;
            animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, str, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.animatorSet.setDuration(200);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                        FragmentContextView.this.setVisibility(8);
                        FragmentContextView.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }
}
