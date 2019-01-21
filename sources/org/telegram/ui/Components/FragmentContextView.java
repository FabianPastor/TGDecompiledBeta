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
import android.support.annotation.Keep;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
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
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
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

    public FragmentContextView(Context context, BaseFragment parentFragment, boolean location) {
        super(context);
        this.fragment = parentFragment;
        this.visible = true;
        this.isLocation = location;
        ((ViewGroup) this.fragment.getFragmentView()).setClipToPadding(false);
        setTag(Integer.valueOf(1));
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setWillNotDraw(false);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow);
        addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), Mode.MULTIPLY));
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new FragmentContextView$$Lambda$0(this));
        this.titleTextView = new TextView(context);
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(19);
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        if (!location) {
            this.playbackSpeedButton = new ImageView(context);
            this.playbackSpeedButton.setScaleType(ScaleType.CENTER);
            this.playbackSpeedButton.setImageResource(R.drawable.voice2x);
            if (AndroidUtilities.density >= 3.0f) {
                this.playbackSpeedButton.setPadding(0, 1, 0, 0);
            }
            addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
            this.playbackSpeedButton.setOnClickListener(new FragmentContextView$$Lambda$1(this));
            updatePlaybackButton();
        }
        this.closeButton = new ImageView(context);
        this.closeButton.setImageResource(R.drawable.miniplayer_close);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), Mode.MULTIPLY));
        this.closeButton.setScaleType(ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36, 53));
        this.closeButton.setOnClickListener(new FragmentContextView$$Lambda$2(this));
        setOnClickListener(new FragmentContextView$$Lambda$3(this));
    }

    final /* synthetic */ void lambda$new$0$FragmentContextView(View v) {
        if (this.currentStyle != 0) {
            return;
        }
        if (MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else {
            MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    final /* synthetic */ void lambda$new$1$FragmentContextView(View v) {
        if (MediaController.getInstance().getPlaybackSpeed() > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(1.8f);
        }
        updatePlaybackButton();
    }

    final /* synthetic */ void lambda$new$3$FragmentContextView(View v) {
        if (this.currentStyle == 2) {
            Builder builder = new Builder(this.fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (this.fragment instanceof DialogsActivity) {
                builder.setMessage(LocaleController.getString("StopLiveLocationAlertAll", R.string.StopLiveLocationAlertAll));
            } else {
                ChatActivity activity = this.fragment;
                Chat chat = activity.getCurrentChat();
                User user = activity.getCurrentUser();
                if (chat != null) {
                    builder.setMessage(LocaleController.formatString("StopLiveLocationAlertToGroup", R.string.StopLiveLocationAlertToGroup, chat.title));
                } else if (user != null) {
                    builder.setMessage(LocaleController.formatString("StopLiveLocationAlertToUser", R.string.StopLiveLocationAlertToUser, UserObject.getFirstName(user)));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                }
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new FragmentContextView$$Lambda$6(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.show();
            return;
        }
        MediaController.getInstance().cleanupPlayer(true, true);
    }

    final /* synthetic */ void lambda$null$2$FragmentContextView(DialogInterface dialogInterface, int i) {
        if (this.fragment instanceof DialogsActivity) {
            for (int a = 0; a < 3; a++) {
                LocationController.getInstance(a).removeAllLocationSharings();
            }
            return;
        }
        LocationController.getInstance(this.fragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) this.fragment).getDialogId());
    }

    final /* synthetic */ void lambda$new$4$FragmentContextView(View v) {
        if (this.currentStyle == 0) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment != null && messageObject != null) {
                if (messageObject.isMusic()) {
                    this.fragment.showDialog(new AudioPlayerAlert(getContext()));
                    return;
                }
                long dialog_id = 0;
                if (this.fragment instanceof ChatActivity) {
                    dialog_id = ((ChatActivity) this.fragment).getDialogId();
                }
                if (messageObject.getDialogId() == dialog_id) {
                    ((ChatActivity) this.fragment).scrollToMessageId(messageObject.getId(), 0, false, 0, true);
                    return;
                }
                dialog_id = messageObject.getDialogId();
                Bundle args = new Bundle();
                int lower_part = (int) dialog_id;
                int high_id = (int) (dialog_id >> 32);
                if (lower_part == 0) {
                    args.putInt("enc_id", high_id);
                } else if (high_id == 1) {
                    args.putInt("chat_id", lower_part);
                } else if (lower_part > 0) {
                    args.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    args.putInt("chat_id", -lower_part);
                }
                args.putInt("message_id", messageObject.getId());
                this.fragment.presentFragment(new ChatActivity(args), this.fragment instanceof ChatActivity);
            }
        } else if (this.currentStyle == 1) {
            Intent intent = new Intent(getContext(), VoIPActivity.class);
            intent.addFlags(NUM);
            getContext().startActivity(intent);
        } else if (this.currentStyle == 2) {
            long did = 0;
            int account = UserConfig.selectedAccount;
            if (this.fragment instanceof ChatActivity) {
                did = ((ChatActivity) this.fragment).getDialogId();
                account = this.fragment.getCurrentAccount();
            } else if (LocationController.getLocationsCount() == 1) {
                for (int a = 0; a < 3; a++) {
                    if (!LocationController.getInstance(a).sharingLocationsUI.isEmpty()) {
                        SharingLocationInfo info = (SharingLocationInfo) LocationController.getInstance(a).sharingLocationsUI.get(0);
                        did = info.did;
                        account = info.messageObject.currentAccount;
                        break;
                    }
                }
            } else {
                did = 0;
            }
            if (did != 0) {
                openSharingLocation(LocationController.getInstance(account).getSharingLocationInfo(did));
                return;
            }
            this.fragment.showDialog(new SharingLocationsAlert(getContext(), new FragmentContextView$$Lambda$5(this)));
        }
    }

    private void updatePlaybackButton() {
        if (MediaController.getInstance().getPlaybackSpeed() > 1.0f) {
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), Mode.MULTIPLY));
        } else {
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), Mode.MULTIPLY));
        }
    }

    public void setAdditionalContextView(FragmentContextView contextView) {
        this.additionalContextView = contextView;
    }

    private void openSharingLocation(SharingLocationInfo info) {
        if (info != null && this.fragment.getParentActivity() != null) {
            LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
            launchActivity.switchToAccount(info.messageObject.currentAccount, true);
            LocationActivity locationActivity = new LocationActivity(2);
            locationActivity.setMessageObject(info.messageObject);
            locationActivity.setDelegate(new FragmentContextView$$Lambda$4(info, info.messageObject.getDialogId()));
            launchActivity.presentFragment(locationActivity);
        }
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    private void checkVisibility() {
        int i = 0;
        boolean show = false;
        if (this.isLocation) {
            show = this.fragment instanceof DialogsActivity ? LocationController.getLocationsCount() != 0 : LocationController.getInstance(this.fragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        } else if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (!(messageObject == null || messageObject.getId() == 0)) {
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

    @Keep
    public void setTopPadding(float value) {
        this.topPadding = value;
        if (this.fragment != null && getParent() != null) {
            View view = this.fragment.getFragmentView();
            ActionBar actionBar = this.fragment.getActionBar();
            int additionalPadding = 0;
            if (!(this.additionalContextView == null || this.additionalContextView.getVisibility() != 0 || this.additionalContextView.getParent() == null)) {
                additionalPadding = AndroidUtilities.dp(36.0f);
            }
            if (!(view == null || getParent() == null)) {
                view.setPadding(0, ((int) this.topPadding) + additionalPadding, 0, 0);
            }
            if (this.isLocation && this.additionalContextView != null) {
                ((LayoutParams) this.additionalContextView.getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0f)) - ((int) this.topPadding);
            }
        }
    }

    private void updateStyle(int style) {
        if (this.currentStyle != style) {
            this.currentStyle = style;
            if (style == 0 || style == 2) {
                this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.titleTextView.setTextColor(Theme.getColor("inappPlayerTitle"));
                this.titleTextView.setTag("inappPlayerTitle");
                this.closeButton.setVisibility(0);
                this.playButton.setVisibility(0);
                this.titleTextView.setTypeface(Typeface.DEFAULT);
                this.titleTextView.setTextSize(1, 15.0f);
                if (style == 0) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                    if (this.playbackSpeedButton != null) {
                        this.playbackSpeedButton.setVisibility(0);
                    }
                } else if (style == 2) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
                }
            } else if (style == 1) {
                this.titleTextView.setText(LocaleController.getString("ReturnToCall", R.string.ReturnToCall));
                this.frameLayout.setBackgroundColor(Theme.getColor("returnToCallBackground"));
                this.frameLayout.setTag("returnToCallBackground");
                this.titleTextView.setTextColor(Theme.getColor("returnToCallText"));
                this.titleTextView.setTag("returnToCallText");
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 14.0f);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
                this.titleTextView.setPadding(0, 0, 0, 0);
                if (this.playbackSpeedButton != null) {
                    this.playbackSpeedButton.setVisibility(8);
                }
            }
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.topPadding = 0.0f;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsCacheChanged);
            return;
        }
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndedCall);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
            if (this.additionalContextView != null) {
                this.additionalContextView.checkVisibility();
            }
            checkLiveLocation(true);
            return;
        }
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidStart);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndedCall);
        if (this.additionalContextView != null) {
            this.additionalContextView.checkVisibility();
        }
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            checkPlayer(true);
            updatePlaybackButton();
            return;
        }
        checkCall(true);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, AndroidUtilities.dp2(39.0f));
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
        } else if (id == NotificationCenter.liveLocationsCacheChanged) {
            if (this.fragment instanceof ChatActivity) {
                if (((ChatActivity) this.fragment).getDialogId() == ((Long) args[0]).longValue()) {
                    checkLocationString();
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.didEndedCall) {
            checkPlayer(false);
        } else if (id == NotificationCenter.didStartedCall) {
            checkCall(false);
        } else {
            checkPlayer(false);
        }
    }

    private void checkLiveLocation(boolean create) {
        View fragmentView = this.fragment.getFragmentView();
        if (!(create || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            create = true;
        }
        boolean show = this.fragment instanceof DialogsActivity ? LocationController.getLocationsCount() != 0 : LocationController.getInstance(this.fragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (show) {
            updateStyle(2);
            this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), true));
            if (create && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!create) {
                    if (this.animatorSet != null) {
                        this.animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    animatorSet = this.animatorSet;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)});
                    animatorSet.playTogether(animatorArr);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
                String param;
                String liveLocation = LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
                ArrayList<SharingLocationInfo> infos = new ArrayList();
                for (int a = 0; a < 3; a++) {
                    infos.addAll(LocationController.getInstance(a).sharingLocationsUI);
                }
                if (infos.size() == 1) {
                    SharingLocationInfo info = (SharingLocationInfo) infos.get(0);
                    int lower_id = (int) info.messageObject.getDialogId();
                    if (lower_id > 0) {
                        param = UserObject.getFirstName(MessagesController.getInstance(info.messageObject.currentAccount).getUser(Integer.valueOf(lower_id)));
                    } else {
                        Chat chat = MessagesController.getInstance(info.messageObject.currentAccount).getChat(Integer.valueOf(-lower_id));
                        if (chat != null) {
                            param = chat.title;
                        } else {
                            param = "";
                        }
                    }
                } else {
                    param = LocaleController.formatPluralString("Chats", infos.size());
                }
                String fullString = String.format(LocaleController.getString("AttachLiveLocationIsSharing", R.string.AttachLiveLocationIsSharing), new Object[]{liveLocation, param});
                int start = fullString.indexOf(liveLocation);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullString);
                this.titleTextView.setEllipsize(TruncateAt.END);
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), start, liveLocation.length() + start, 18);
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
            if (create) {
                if (getVisibility() != 8) {
                    setVisibility(8);
                }
                setTopPadding(0.0f);
                return;
            }
            if (this.animatorSet != null) {
                this.animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            animatorSet = this.animatorSet;
            animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.animatorSet.setDuration(200);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                        FragmentContextView.this.setVisibility(8);
                        FragmentContextView.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }

    private void checkLocationString() {
        if ((this.fragment instanceof ChatActivity) && this.titleTextView != null) {
            ChatActivity chatActivity = this.fragment;
            long dialogId = chatActivity.getDialogId();
            int currentAccount = chatActivity.getCurrentAccount();
            ArrayList<Message> messages = (ArrayList) LocationController.getInstance(currentAccount).locationsCache.get(dialogId);
            if (!this.firstLocationsLoaded) {
                LocationController.getInstance(currentAccount).loadLiveLocations(dialogId);
                this.firstLocationsLoaded = true;
            }
            int locationSharingCount = 0;
            User notYouUser = null;
            if (messages != null) {
                int currentUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                int date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                for (int a = 0; a < messages.size(); a++) {
                    Message message = (Message) messages.get(a);
                    if (message.media != null && message.date + message.media.period > date) {
                        if (notYouUser == null && message.from_id != currentUserId) {
                            notYouUser = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(message.from_id));
                        }
                        locationSharingCount++;
                    }
                }
            }
            if (this.lastLocationSharingCount != locationSharingCount) {
                String fullString;
                this.lastLocationSharingCount = locationSharingCount;
                String liveLocation = LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
                if (locationSharingCount == 0) {
                    fullString = liveLocation;
                } else {
                    int otherSharingCount = locationSharingCount - 1;
                    if (LocationController.getInstance(currentAccount).isSharingLocation(dialogId)) {
                        if (otherSharingCount == 0) {
                            fullString = String.format("%1$s - %2$s", new Object[]{liveLocation, LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName)});
                        } else if (otherSharingCount != 1 || notYouUser == null) {
                            fullString = String.format("%1$s - %2$s %3$s", new Object[]{liveLocation, LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName), LocaleController.formatPluralString("AndOther", otherSharingCount)});
                        } else {
                            Object[] objArr = new Object[2];
                            objArr[0] = liveLocation;
                            objArr[1] = LocaleController.formatString("SharingYouAndOtherName", R.string.SharingYouAndOtherName, UserObject.getFirstName(notYouUser));
                            fullString = String.format("%1$s - %2$s", objArr);
                        }
                    } else if (otherSharingCount != 0) {
                        fullString = String.format("%1$s - %2$s %3$s", new Object[]{liveLocation, UserObject.getFirstName(notYouUser), LocaleController.formatPluralString("AndOther", otherSharingCount)});
                    } else {
                        fullString = String.format("%1$s - %2$s", new Object[]{liveLocation, UserObject.getFirstName(notYouUser)});
                    }
                }
                if (this.lastString == null || !fullString.equals(this.lastString)) {
                    this.lastString = fullString;
                    int start = fullString.indexOf(liveLocation);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullString);
                    this.titleTextView.setEllipsize(TruncateAt.END);
                    if (start >= 0) {
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), start, liveLocation.length() + start, 18);
                    }
                    this.titleTextView.setText(spannableStringBuilder);
                }
            }
        }
    }

    private void checkPlayer(boolean create) {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        View fragmentView = this.fragment.getFragmentView();
        if (!(create || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            create = true;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (messageObject == null || messageObject.getId() == 0) {
            this.lastMessageObject = null;
            boolean callAvailable = (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) ? false : true;
            if (callAvailable) {
                checkCall(false);
                return;
            } else if (this.visible) {
                this.visible = false;
                if (create) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                if (this.animatorSet != null) {
                    this.animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.animatorSet = new AnimatorSet();
                animatorSet = this.animatorSet;
                animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            FragmentContextView.this.setVisibility(8);
                            FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
                return;
            } else {
                return;
            }
        }
        int prevStyle = this.currentStyle;
        updateStyle(0);
        if (create && this.topPadding == 0.0f) {
            setTopPadding((float) AndroidUtilities.dp2(36.0f));
            if (this.additionalContextView == null || this.additionalContextView.getVisibility() != 0) {
                ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
            } else {
                ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
            }
            this.yPosition = 0.0f;
        }
        if (!this.visible) {
            if (!create) {
                if (this.animatorSet != null) {
                    this.animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.animatorSet = new AnimatorSet();
                if (this.additionalContextView == null || this.additionalContextView.getVisibility() != 0) {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                } else {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                }
                animatorSet = this.animatorSet;
                animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)});
                animatorSet.playTogether(animatorArr);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
            this.playButton.setImageResource(R.drawable.miniplayer_play);
        } else {
            this.playButton.setImageResource(R.drawable.miniplayer_pause);
        }
        if (this.lastMessageObject != messageObject || prevStyle != 0) {
            SpannableStringBuilder stringBuilder;
            this.lastMessageObject = messageObject;
            if (this.lastMessageObject.isVoice() || this.lastMessageObject.isRoundVideo()) {
                if (this.playbackSpeedButton != null) {
                    this.playbackSpeedButton.setAlpha(1.0f);
                    this.playbackSpeedButton.setEnabled(true);
                }
                this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                stringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
            } else {
                if (this.playbackSpeedButton != null) {
                    this.playbackSpeedButton.setAlpha(0.0f);
                    this.playbackSpeedButton.setEnabled(false);
                }
                this.titleTextView.setPadding(0, 0, 0, 0);
                stringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                this.titleTextView.setEllipsize(TruncateAt.END);
            }
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), 0, messageObject.getMusicAuthor().length(), 18);
            this.titleTextView.setText(stringBuilder);
        }
    }

    private void checkCall(boolean create) {
        boolean callAvailable;
        View fragmentView = this.fragment.getFragmentView();
        if (!(create || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            create = true;
        }
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            callAvailable = false;
        } else {
            callAvailable = true;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (callAvailable) {
            updateStyle(1);
            if (create && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                if (this.additionalContextView == null || this.additionalContextView.getVisibility() != 0) {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                } else {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                }
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!create) {
                    if (this.animatorSet != null) {
                        this.animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    if (this.additionalContextView == null || this.additionalContextView.getVisibility() != 0) {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                    } else {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                    }
                    animatorSet = this.animatorSet;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)});
                    animatorSet.playTogether(animatorArr);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
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
            if (create) {
                if (getVisibility() != 8) {
                    setVisibility(8);
                }
                setTopPadding(0.0f);
                return;
            }
            if (this.animatorSet != null) {
                this.animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            animatorSet = this.animatorSet;
            animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.animatorSet.setDuration(200);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                        FragmentContextView.this.setVisibility(8);
                        FragmentContextView.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }
}
