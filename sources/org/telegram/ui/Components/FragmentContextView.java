package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;
import org.telegram.ui.VoIPActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenterDelegate {
    private FragmentContextView additionalContextView;
    private AnimatorSet animatorSet;
    private Runnable checkLocationRunnable = new C11641();
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
    private TextView titleTextView;
    private float topPadding;
    private boolean visible;
    private float yPosition;

    /* renamed from: org.telegram.ui.Components.FragmentContextView$1 */
    class C11641 implements Runnable {
        C11641() {
        }

        public void run() {
            FragmentContextView.this.checkLocationString();
            AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000);
        }
    }

    /* renamed from: org.telegram.ui.Components.FragmentContextView$2 */
    class C11652 implements OnClickListener {
        C11652() {
        }

        public void onClick(View view) {
            if (FragmentContextView.this.currentStyle != null) {
                return;
            }
            if (MediaController.getInstance().isMessagePaused() != null) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.FragmentContextView$3 */
    class C11673 implements OnClickListener {

        /* renamed from: org.telegram.ui.Components.FragmentContextView$3$1 */
        class C11661 implements DialogInterface.OnClickListener {
            C11661() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if ((FragmentContextView.this.fragment instanceof DialogsActivity) != null) {
                    for (dialogInterface = null; dialogInterface < 3; dialogInterface++) {
                        LocationController.getInstance(dialogInterface).removeAllLocationSharings();
                    }
                    return;
                }
                LocationController.getInstance(FragmentContextView.this.fragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) FragmentContextView.this.fragment).getDialogId());
            }
        }

        C11673() {
        }

        public void onClick(View view) {
            if (FragmentContextView.this.currentStyle == 2) {
                view = new Builder(FragmentContextView.this.fragment.getParentActivity());
                view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                if (FragmentContextView.this.fragment instanceof DialogsActivity) {
                    view.setMessage(LocaleController.getString("StopLiveLocationAlertAll", C0446R.string.StopLiveLocationAlertAll));
                } else {
                    ChatActivity chatActivity = (ChatActivity) FragmentContextView.this.fragment;
                    Chat currentChat = chatActivity.getCurrentChat();
                    User currentUser = chatActivity.getCurrentUser();
                    if (currentChat != null) {
                        view.setMessage(LocaleController.formatString("StopLiveLocationAlertToGroup", C0446R.string.StopLiveLocationAlertToGroup, currentChat.title));
                    } else if (currentUser != null) {
                        view.setMessage(LocaleController.formatString("StopLiveLocationAlertToUser", C0446R.string.StopLiveLocationAlertToUser, UserObject.getFirstName(currentUser)));
                    } else {
                        view.setMessage(LocaleController.getString("AreYouSure", C0446R.string.AreYouSure));
                    }
                }
                view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C11661());
                view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                view.show();
                return;
            }
            MediaController.getInstance().cleanupPlayer(true, true);
        }
    }

    /* renamed from: org.telegram.ui.Components.FragmentContextView$4 */
    class C11684 implements OnClickListener {

        /* renamed from: org.telegram.ui.Components.FragmentContextView$4$1 */
        class C20611 implements SharingLocationsAlertDelegate {
            C20611() {
            }

            public void didSelectLocation(SharingLocationInfo sharingLocationInfo) {
                FragmentContextView.this.openSharingLocation(sharingLocationInfo);
            }
        }

        C11684() {
        }

        public void onClick(View view) {
            long j = 0;
            if (FragmentContextView.this.currentStyle == null) {
                view = MediaController.getInstance().getPlayingMessageObject();
                if (FragmentContextView.this.fragment != null && view != null) {
                    if (view.isMusic()) {
                        FragmentContextView.this.fragment.showDialog(new AudioPlayerAlert(FragmentContextView.this.getContext()));
                        return;
                    }
                    if (FragmentContextView.this.fragment instanceof ChatActivity) {
                        j = ((ChatActivity) FragmentContextView.this.fragment).getDialogId();
                    }
                    if (view.getDialogId() == j) {
                        ((ChatActivity) FragmentContextView.this.fragment).scrollToMessageId(view.getId(), 0, false, 0, true);
                        return;
                    }
                    j = view.getDialogId();
                    Bundle bundle = new Bundle();
                    int i = (int) j;
                    int i2 = (int) (j >> 32);
                    if (i == 0) {
                        bundle.putInt("enc_id", i2);
                    } else if (i2 == 1) {
                        bundle.putInt("chat_id", i);
                    } else if (i > 0) {
                        bundle.putInt("user_id", i);
                    } else if (i < 0) {
                        bundle.putInt("chat_id", -i);
                    }
                    bundle.putInt("message_id", view.getId());
                    FragmentContextView.this.fragment.presentFragment(new ChatActivity(bundle), FragmentContextView.this.fragment instanceof ChatActivity);
                }
            } else if (FragmentContextView.this.currentStyle == 1) {
                view = new Intent(FragmentContextView.this.getContext(), VoIPActivity.class);
                view.addFlags(805306368);
                FragmentContextView.this.getContext().startActivity(view);
            } else if (FragmentContextView.this.currentStyle == 2) {
                long dialogId;
                view = UserConfig.selectedAccount;
                if (FragmentContextView.this.fragment instanceof ChatActivity) {
                    dialogId = ((ChatActivity) FragmentContextView.this.fragment).getDialogId();
                    view = FragmentContextView.this.fragment.getCurrentAccount();
                } else {
                    if (LocationController.getLocationsCount() == 1) {
                        for (int i3 = 0; i3 < 3; i3++) {
                            if (!LocationController.getInstance(i3).sharingLocationsUI.isEmpty()) {
                                SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) LocationController.getInstance(i3).sharingLocationsUI.get(0);
                                dialogId = sharingLocationInfo.did;
                                view = sharingLocationInfo.messageObject.currentAccount;
                                break;
                            }
                        }
                    }
                    dialogId = 0;
                }
                if (dialogId != 0) {
                    FragmentContextView.this.openSharingLocation(LocationController.getInstance(view).getSharingLocationInfo(dialogId));
                } else {
                    FragmentContextView.this.fragment.showDialog(new SharingLocationsAlert(FragmentContextView.this.getContext(), new C20611()));
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.FragmentContextView$6 */
    class C11696 extends AnimatorListenerAdapter {
        C11696() {
        }

        public void onAnimationEnd(Animator animator) {
            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator) != null) {
                FragmentContextView.this.setVisibility(8);
                FragmentContextView.this.animatorSet = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.FragmentContextView$7 */
    class C11707 extends AnimatorListenerAdapter {
        C11707() {
        }

        public void onAnimationEnd(Animator animator) {
            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator) != null) {
                FragmentContextView.this.animatorSet = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.FragmentContextView$8 */
    class C11718 extends AnimatorListenerAdapter {
        C11718() {
        }

        public void onAnimationEnd(Animator animator) {
            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator) != null) {
                FragmentContextView.this.setVisibility(8);
                FragmentContextView.this.animatorSet = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.FragmentContextView$9 */
    class C11729 extends AnimatorListenerAdapter {
        C11729() {
        }

        public void onAnimationEnd(Animator animator) {
            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator) != null) {
                FragmentContextView.this.animatorSet = null;
            }
        }
    }

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
        z = new View(context);
        z.setBackgroundResource(C0446R.drawable.header_shadow);
        addView(z, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_inappPlayerPlayPause), Mode.MULTIPLY));
        addView(this.playButton, LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.playButton.setOnClickListener(new C11652());
        this.titleTextView = new TextView(context);
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(true);
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        this.closeButton = new ImageView(context);
        this.closeButton.setImageResource(C0446R.drawable.miniplayer_close);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_inappPlayerClose), Mode.MULTIPLY));
        this.closeButton.setScaleType(ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36, true));
        this.closeButton.setOnClickListener(new C11673());
        setOnClickListener(new C11684());
    }

    public void setAdditionalContextView(FragmentContextView fragmentContextView) {
        this.additionalContextView = fragmentContextView;
    }

    private void openSharingLocation(final SharingLocationInfo sharingLocationInfo) {
        if (sharingLocationInfo != null) {
            if (this.fragment.getParentActivity() != null) {
                LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
                launchActivity.switchToAccount(sharingLocationInfo.messageObject.currentAccount, true);
                BaseFragment locationActivity = new LocationActivity(2);
                locationActivity.setMessageObject(sharingLocationInfo.messageObject);
                final long dialogId = sharingLocationInfo.messageObject.getDialogId();
                locationActivity.setDelegate(new LocationActivityDelegate() {
                    public void didSelectLocation(MessageMedia messageMedia, int i) {
                        SendMessagesHelper.getInstance(sharingLocationInfo.messageObject.currentAccount).sendMessage(messageMedia, dialogId, null, null, null);
                    }
                });
                launchActivity.presentFragment(locationActivity);
            }
        }
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    private void checkVisibility() {
        boolean z = true;
        int i = 0;
        if (this.isLocation) {
            if (!(this.fragment instanceof DialogsActivity)) {
                z = LocationController.getInstance(this.fragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
            } else if (LocationController.getLocationsCount() != 0) {
            }
            if (!z) {
                i = 8;
            }
            setVisibility(i);
        } else if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (!(playingMessageObject == null || playingMessageObject.getId() == 0)) {
                if (!z) {
                    i = 8;
                }
                setVisibility(i);
            }
        } else {
            if (!z) {
                i = 8;
            }
            setVisibility(i);
        }
        z = false;
        if (!z) {
            i = 8;
        }
        setVisibility(i);
    }

    @Keep
    public void setTopPadding(float f) {
        this.topPadding = f;
        if (this.fragment != null) {
            f = this.fragment.getFragmentView();
            int dp = (this.additionalContextView == null || this.additionalContextView.getVisibility() != 0) ? 0 : AndroidUtilities.dp(36.0f);
            if (f != null) {
                f.setPadding(0, ((int) this.topPadding) + dp, 0, 0);
            }
            if (this.isLocation != null && this.additionalContextView != null) {
                ((LayoutParams) this.additionalContextView.getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0f)) - ((int) this.topPadding);
            }
        }
    }

    private void updateStyle(int i) {
        if (this.currentStyle != i) {
            this.currentStyle = i;
            if (i != 0) {
                if (i != 2) {
                    if (i == 1) {
                        this.titleTextView.setText(LocaleController.getString("ReturnToCall", C0446R.string.ReturnToCall));
                        this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_returnToCallBackground));
                        this.frameLayout.setTag(Theme.key_returnToCallBackground);
                        this.titleTextView.setTextColor(Theme.getColor(Theme.key_returnToCallText));
                        this.titleTextView.setTag(Theme.key_returnToCallText);
                        this.closeButton.setVisibility(8);
                        this.playButton.setVisibility(8);
                        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        this.titleTextView.setTextSize(1, 14.0f);
                        this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
                    }
                }
            }
            this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_inappPlayerBackground));
            this.frameLayout.setTag(Theme.key_inappPlayerBackground);
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_inappPlayerTitle));
            this.titleTextView.setTag(Theme.key_inappPlayerTitle);
            this.closeButton.setVisibility(0);
            this.playButton.setVisibility(0);
            this.titleTextView.setTypeface(Typeface.DEFAULT);
            this.titleTextView.setTextSize(1, 15.0f);
            this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
            if (i == 0) {
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
            } else if (i == 2) {
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
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
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
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
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidStarted);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndedCall);
        if (this.additionalContextView != null) {
            this.additionalContextView.checkVisibility();
        }
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            checkPlayer(true);
        } else {
            checkCall(true);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, AndroidUtilities.dp2(NUM));
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
        } else if (i != NotificationCenter.liveLocationsCacheChanged) {
            if (!(i == NotificationCenter.messagePlayingDidStarted || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset)) {
                if (i != NotificationCenter.didEndedCall) {
                    if (i == NotificationCenter.didStartedCall) {
                        checkCall(false);
                        return;
                    } else {
                        checkPlayer(false);
                        return;
                    }
                }
            }
            checkPlayer(false);
        } else if ((this.fragment instanceof ChatActivity) != 0) {
            if (((ChatActivity) this.fragment).getDialogId() == ((Long) objArr[0]).longValue()) {
                checkLocationString();
            }
        }
    }

    private void checkLiveLocation(boolean z) {
        View fragmentView = this.fragment.getFragmentView();
        if (!(z || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            z = true;
        }
        boolean isSharingLocation = this.fragment instanceof DialogsActivity ? LocationController.getLocationsCount() != 0 : LocationController.getInstance(this.fragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        if (isSharingLocation) {
            updateStyle(2);
            this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), true));
            if (z && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                setTranslationY(0.0f);
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!z) {
                    if (this.animatorSet) {
                        this.animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    z = this.animatorSet;
                    r0 = new Animator[2];
                    r0[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f)), 0.0f});
                    r0[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)});
                    z.playTogether(r0);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new C11707());
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
            if (this.fragment instanceof DialogsActivity) {
                int i;
                String firstName;
                z = LocaleController.getString("AttachLiveLocation", C0446R.string.AttachLiveLocation);
                ArrayList arrayList = new ArrayList();
                for (i = 0; i < 3; i++) {
                    arrayList.addAll(LocationController.getInstance(i).sharingLocationsUI);
                }
                if (arrayList.size() == 1) {
                    SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) arrayList.get(0);
                    i = (int) sharingLocationInfo.messageObject.getDialogId();
                    if (i > 0) {
                        firstName = UserObject.getFirstName(MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getUser(Integer.valueOf(i)));
                    } else {
                        Chat chat = MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getChat(Integer.valueOf(-i));
                        firstName = chat != null ? chat.title : TtmlNode.ANONYMOUS_REGION_ID;
                    }
                } else {
                    firstName = LocaleController.formatPluralString("Chats", arrayList.size());
                }
                CharSequence format = String.format(LocaleController.getString("AttachLiveLocationIsSharing", C0446R.string.AttachLiveLocationIsSharing), new Object[]{z, firstName});
                int indexOf = format.indexOf(z);
                CharSequence spannableStringBuilder = new SpannableStringBuilder(format);
                this.titleTextView.setEllipsize(TruncateAt.END);
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_inappPlayerPerformer)), indexOf, z.length() + indexOf, 18);
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
                if (!getVisibility()) {
                    setVisibility(8);
                }
                setTopPadding(0.0f);
                return;
            }
            if (this.animatorSet) {
                this.animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            z = this.animatorSet;
            r0 = new Animator[2];
            r0[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f))});
            r0[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
            z.playTogether(r0);
            this.animatorSet.setDuration(200);
            this.animatorSet.addListener(new C11696());
            this.animatorSet.start();
        }
    }

    private void checkLocationString() {
        if (this.fragment instanceof ChatActivity) {
            if (this.titleTextView != null) {
                int i;
                ChatActivity chatActivity = (ChatActivity) this.fragment;
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
                    int i2 = 0;
                    i = i2;
                    while (i2 < arrayList.size()) {
                        Message message = (Message) arrayList.get(i2);
                        if (message.media != null) {
                            if (message.date + message.media.period > currentTime) {
                                if (user2 == null && message.from_id != clientUserId) {
                                    user2 = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(message.from_id));
                                }
                                i++;
                            }
                        }
                        i2++;
                    }
                    user = user2;
                } else {
                    i = 0;
                }
                if (this.lastLocationSharingCount != i) {
                    CharSequence charSequence;
                    this.lastLocationSharingCount = i;
                    String string = LocaleController.getString("AttachLiveLocation", C0446R.string.AttachLiveLocation);
                    if (i == 0) {
                        charSequence = string;
                    } else {
                        i--;
                        if (LocationController.getInstance(currentAccount).isSharingLocation(dialogId)) {
                            if (i == 0) {
                                charSequence = String.format("%1$s - %2$s", new Object[]{string, LocaleController.getString("ChatYourSelfName", C0446R.string.ChatYourSelfName)});
                            } else if (i != 1 || user == null) {
                                charSequence = String.format("%1$s - %2$s %3$s", new Object[]{string, LocaleController.getString("ChatYourSelfName", C0446R.string.ChatYourSelfName), LocaleController.formatPluralString("AndOther", i)});
                            } else {
                                Object[] objArr = new Object[2];
                                objArr[0] = string;
                                objArr[1] = LocaleController.formatString("SharingYouAndOtherName", C0446R.string.SharingYouAndOtherName, UserObject.getFirstName(user));
                                charSequence = String.format("%1$s - %2$s", objArr);
                            }
                        } else if (i != 0) {
                            charSequence = String.format("%1$s - %2$s %3$s", new Object[]{string, UserObject.getFirstName(user), LocaleController.formatPluralString("AndOther", i)});
                        } else {
                            charSequence = String.format("%1$s - %2$s", new Object[]{string, UserObject.getFirstName(user)});
                        }
                    }
                    if (this.lastString == null || !charSequence.equals(this.lastString)) {
                        this.lastString = charSequence;
                        int indexOf = charSequence.indexOf(string);
                        CharSequence spannableStringBuilder = new SpannableStringBuilder(charSequence);
                        this.titleTextView.setEllipsize(TruncateAt.END);
                        if (indexOf >= 0) {
                            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_inappPlayerPerformer)), indexOf, string.length() + indexOf, 18);
                        }
                        this.titleTextView.setText(spannableStringBuilder);
                    }
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
        if (playingMessageObject != null) {
            if (playingMessageObject.getId() != 0) {
                int i = this.currentStyle;
                updateStyle(0);
                if (z && this.topPadding == 0.0f) {
                    setTopPadding((float) AndroidUtilities.dp2(36.0f));
                    if (this.additionalContextView == null || this.additionalContextView.getVisibility() != 0) {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                    } else {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                    }
                    setTranslationY(0.0f);
                    this.yPosition = 0.0f;
                }
                if (!this.visible) {
                    if (!z) {
                        if (this.animatorSet) {
                            this.animatorSet.cancel();
                            this.animatorSet = null;
                        }
                        this.animatorSet = new AnimatorSet();
                        if (!this.additionalContextView || this.additionalContextView.getVisibility()) {
                            ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                        } else {
                            ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                        }
                        z = this.animatorSet;
                        r1 = new Animator[2];
                        r1[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f)), 0.0f});
                        r1[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)});
                        z.playTogether(r1);
                        this.animatorSet.setDuration(200);
                        this.animatorSet.addListener(new C11729());
                        this.animatorSet.start();
                    }
                    this.visible = true;
                    setVisibility(0);
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    this.playButton.setImageResource(C0446R.drawable.miniplayer_play);
                } else {
                    this.playButton.setImageResource(C0446R.drawable.miniplayer_pause);
                }
                if (!(this.lastMessageObject == playingMessageObject && i == 0)) {
                    this.lastMessageObject = playingMessageObject;
                    if (!this.lastMessageObject.isVoice()) {
                        if (!this.lastMessageObject.isRoundVideo()) {
                            z = new SpannableStringBuilder(String.format("%s - %s", new Object[]{playingMessageObject.getMusicAuthor(), playingMessageObject.getMusicTitle()}));
                            this.titleTextView.setEllipsize(TruncateAt.END);
                            z.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_inappPlayerPerformer)), 0, playingMessageObject.getMusicAuthor().length(), 18);
                            this.titleTextView.setText(z);
                        }
                    }
                    z = new SpannableStringBuilder(String.format("%s %s", new Object[]{playingMessageObject.getMusicAuthor(), playingMessageObject.getMusicTitle()}));
                    this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
                    z.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_inappPlayerPerformer)), 0, playingMessageObject.getMusicAuthor().length(), 18);
                    this.titleTextView.setText(z);
                }
            }
        }
        this.lastMessageObject = null;
        boolean z2 = (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) ? false : true;
        if (z2) {
            checkCall(false);
            return;
        }
        if (this.visible) {
            this.visible = false;
            if (z) {
                if (!getVisibility()) {
                    setVisibility(8);
                }
                setTopPadding(0.0f);
            } else {
                if (this.animatorSet) {
                    this.animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.animatorSet = new AnimatorSet();
                z = this.animatorSet;
                r0 = new Animator[2];
                r0[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f))});
                r0[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
                z.playTogether(r0);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new C11718());
                this.animatorSet.start();
            }
        }
    }

    private void checkCall(boolean z) {
        View fragmentView = this.fragment.getFragmentView();
        if (!(z || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            z = true;
        }
        boolean z2 = (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) ? false : true;
        Animator[] animatorArr;
        if (z2) {
            updateStyle(1);
            if (z && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
                if (this.additionalContextView == null || this.additionalContextView.getVisibility() != 0) {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                } else {
                    ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                }
                setTranslationY(0.0f);
                this.yPosition = 0.0f;
            }
            if (!this.visible) {
                if (!z) {
                    if (this.animatorSet) {
                        this.animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    if (!this.additionalContextView || this.additionalContextView.getVisibility()) {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(36.0f);
                    } else {
                        ((LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(72.0f);
                    }
                    z = this.animatorSet;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f)), 0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)});
                    z.playTogether(animatorArr);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator) != null) {
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
                if (!getVisibility()) {
                    setVisibility(8);
                }
                setTopPadding(0.0f);
                return;
            }
            if (this.animatorSet) {
                this.animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            z = this.animatorSet;
            animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f))});
            animatorArr[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
            z.playTogether(animatorArr);
            this.animatorSet.setDuration(200);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator) != null) {
                        FragmentContextView.this.setVisibility(8);
                        FragmentContextView.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }

    @Keep
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.yPosition = f;
        invalidate();
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        int save = canvas.save();
        if (this.yPosition < 0.0f) {
            canvas.clipRect(0, (int) (-this.yPosition), view.getMeasuredWidth(), AndroidUtilities.dp2(39.0f));
        }
        view = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        return view;
    }
}
