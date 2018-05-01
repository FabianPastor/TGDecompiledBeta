package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.MediaActivity;
import org.telegram.ui.ProfileActivity;

public class ChatAvatarContainer extends FrameLayout implements NotificationCenterDelegate {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentConnectionState;
    private CharSequence lastSubtitle;
    private boolean occupyStatusBar = true;
    private int onlineCount = -1;
    private ChatActivity parentFragment;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private SimpleTextView subtitleTextView;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private SimpleTextView titleTextView;

    /* renamed from: org.telegram.ui.Components.ChatAvatarContainer$1 */
    class C11141 implements OnClickListener {
        C11141() {
        }

        public void onClick(View view) {
            ChatAvatarContainer.this.parentFragment.showDialog(AlertsCreator.createTTLAlert(ChatAvatarContainer.this.getContext(), ChatAvatarContainer.this.parentFragment.getCurrentEncryptedChat()).create());
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAvatarContainer$2 */
    class C11152 implements OnClickListener {
        C11152() {
        }

        public void onClick(View view) {
            view = ChatAvatarContainer.this.parentFragment.getCurrentUser();
            Chat currentChat = ChatAvatarContainer.this.parentFragment.getCurrentChat();
            if (view != null) {
                Bundle bundle = new Bundle();
                if (UserObject.isUserSelf(view)) {
                    bundle.putLong("dialog_id", ChatAvatarContainer.this.parentFragment.getDialogId());
                    view = new MediaActivity(bundle);
                    view.setChatInfo(ChatAvatarContainer.this.parentFragment.getCurrentChatInfo());
                    ChatAvatarContainer.this.parentFragment.presentFragment(view);
                    return;
                }
                bundle.putInt("user_id", view.id);
                if (ChatAvatarContainer.this.timeItem != null) {
                    bundle.putLong("dialog_id", ChatAvatarContainer.this.parentFragment.getDialogId());
                }
                view = new ProfileActivity(bundle);
                view.setPlayProfileAnimation(true);
                ChatAvatarContainer.this.parentFragment.presentFragment(view);
            } else if (currentChat != null) {
                view = new Bundle();
                view.putInt("chat_id", currentChat.id);
                BaseFragment profileActivity = new ProfileActivity(view);
                profileActivity.setChatInfo(ChatAvatarContainer.this.parentFragment.getCurrentChatInfo());
                profileActivity.setPlayProfileAnimation(true);
                ChatAvatarContainer.this.parentFragment.presentFragment(profileActivity);
            }
        }
    }

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean z) {
        super(context);
        this.parentFragment = chatActivity;
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        addView(this.avatarImageView);
        this.titleTextView = new SimpleTextView(context);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.titleTextView.setTextSize(18);
        this.titleTextView.setGravity(3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
        addView(this.titleTextView);
        this.subtitleTextView = new SimpleTextView(context);
        this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.subtitleTextView.setTextSize(14);
        this.subtitleTextView.setGravity(3);
        addView(this.subtitleTextView);
        if (z) {
            this.timeItem = new ImageView(context);
            this.timeItem.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
            this.timeItem.setScaleType(ScaleType.CENTER);
            chatActivity = this.timeItem;
            z = new TimerDrawable(context);
            this.timerDrawable = z;
            chatActivity.setImageDrawable(z);
            addView(this.timeItem);
            this.timeItem.setOnClickListener(new C11141());
        }
        if (this.parentFragment != null) {
            setOnClickListener(new C11152());
            context = this.parentFragment.getCurrentChat();
            this.statusDrawables[0] = new TypingDotsDrawable();
            this.statusDrawables[1] = new RecordStatusDrawable();
            this.statusDrawables[true] = new SendingFileDrawable();
            this.statusDrawables[3] = new PlayingGameDrawable();
            this.statusDrawables[true] = new RoundStatusDrawable();
            for (boolean z2 : this.statusDrawables) {
                z2.setIsChat(context != null);
            }
        }
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
    }

    public void setTitleColors(int i, int i2) {
        this.titleTextView.setTextColor(i);
        this.subtitleTextView.setTextColor(i);
    }

    protected void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        int dp = i - AndroidUtilities.dp(70.0f);
        this.avatarImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
        this.titleTextView.measure(MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
        this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
        if (this.timeItem != null) {
            this.timeItem.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), NUM));
        }
        setMeasuredDimension(i, MeasureSpec.getSize(i2));
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        z = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0f)) / 2;
        i2 = (VERSION.SDK_INT < 21 || this.occupyStatusBar == 0) ? 0 : AndroidUtilities.statusBarHeight;
        z += i2;
        this.avatarImageView.layout(AndroidUtilities.dp(NUM), z, AndroidUtilities.dp(NUM), AndroidUtilities.dp(42.0f) + z);
        if (this.subtitleTextView.getVisibility() == 0) {
            this.titleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(1.3f) + z, AndroidUtilities.dp(62.0f) + this.titleTextView.getMeasuredWidth(), (this.titleTextView.getTextHeight() + z) + AndroidUtilities.dp(1.3f));
        } else {
            this.titleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(11.0f) + z, AndroidUtilities.dp(62.0f) + this.titleTextView.getMeasuredWidth(), (this.titleTextView.getTextHeight() + z) + AndroidUtilities.dp(11.0f));
        }
        if (this.timeItem != 0) {
            this.timeItem.layout(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(15.0f) + z, AndroidUtilities.dp(58.0f), AndroidUtilities.dp(49.0f) + z);
        }
        this.subtitleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(24.0f) + z, AndroidUtilities.dp(62.0f) + this.subtitleTextView.getMeasuredWidth(), (z + this.subtitleTextView.getTextHeight()) + AndroidUtilities.dp(24.0f));
    }

    public void showTimeItem() {
        if (this.timeItem != null) {
            this.timeItem.setVisibility(0);
        }
    }

    public void hideTimeItem() {
        if (this.timeItem != null) {
            this.timeItem.setVisibility(8);
        }
    }

    public void setTime(int i) {
        if (this.timerDrawable != null) {
            this.timerDrawable.setTime(i);
        }
    }

    public void setTitleIcons(int i, int i2) {
        this.titleTextView.setLeftDrawable(i);
        this.titleTextView.setRightDrawable(i2);
    }

    public void setTitleIcons(Drawable drawable, Drawable drawable2) {
        this.titleTextView.setLeftDrawable(drawable);
        this.titleTextView.setRightDrawable(drawable2);
    }

    public void setTitle(CharSequence charSequence) {
        this.titleTextView.setText(charSequence);
    }

    public void setSubtitle(CharSequence charSequence) {
        if (this.lastSubtitle == null) {
            this.subtitleTextView.setText(charSequence);
        } else {
            this.lastSubtitle = charSequence;
        }
    }

    public ImageView getTimeItem() {
        return this.timeItem;
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView;
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    private void setTypingAnimation(boolean z) {
        boolean z2 = false;
        if (z) {
            try {
                Integer num = (Integer) MessagesController.getInstance(this.currentAccount).printingStringsTypes.get(this.parentFragment.getDialogId());
                this.subtitleTextView.setLeftDrawable(this.statusDrawables[num.intValue()]);
                int i;
                while (i < this.statusDrawables.length) {
                    if (i == num.intValue()) {
                        this.statusDrawables[i].start();
                    } else {
                        this.statusDrawables[i].stop();
                    }
                    i++;
                }
                return;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return;
            }
        }
        this.subtitleTextView.setLeftDrawable(null);
        while (z2 < this.statusDrawables.length) {
            this.statusDrawables[z2].stop();
            z2++;
        }
    }

    public void updateSubtitle() {
        if (this.parentFragment != null) {
            User currentUser = this.parentFragment.getCurrentUser();
            if (UserObject.isUserSelf(currentUser)) {
                if (this.subtitleTextView.getVisibility() != 8) {
                    this.subtitleTextView.setVisibility(8);
                }
                return;
            }
            String toLowerCase;
            Chat currentChat = this.parentFragment.getCurrentChat();
            CharSequence charSequence = (CharSequence) MessagesController.getInstance(this.currentAccount).printingStrings.get(this.parentFragment.getDialogId());
            if (charSequence != null) {
                charSequence = TextUtils.replace(charSequence, new String[]{"..."}, new String[]{TtmlNode.ANONYMOUS_REGION_ID});
            }
            if (!(charSequence == null || charSequence.length() == 0)) {
                if (!ChatObject.isChannel(currentChat) || currentChat.megagroup) {
                    setTypingAnimation(true);
                    if (this.lastSubtitle == null) {
                        this.subtitleTextView.setText(charSequence);
                    } else {
                        this.lastSubtitle = charSequence;
                    }
                }
            }
            setTypingAnimation(false);
            if (currentChat != null) {
                ChatFull currentChatInfo = this.parentFragment.getCurrentChatInfo();
                if (ChatObject.isChannel(currentChat)) {
                    if (currentChatInfo == null || currentChatInfo.participants_count == 0) {
                        if (currentChat.megagroup) {
                            toLowerCase = LocaleController.getString("Loading", C0446R.string.Loading).toLowerCase();
                        } else if ((currentChat.flags & 64) != 0) {
                            toLowerCase = LocaleController.getString("ChannelPublic", C0446R.string.ChannelPublic).toLowerCase();
                        } else {
                            toLowerCase = LocaleController.getString("ChannelPrivate", C0446R.string.ChannelPrivate).toLowerCase();
                        }
                    } else if (!currentChat.megagroup || currentChatInfo.participants_count > Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                        int[] iArr = new int[1];
                        CharSequence formatShortNumber = LocaleController.formatShortNumber(currentChatInfo.participants_count, iArr);
                        if (currentChat.megagroup) {
                            toLowerCase = LocaleController.formatPluralString("Members", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), formatShortNumber);
                        } else {
                            toLowerCase = LocaleController.formatPluralString("Subscribers", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), formatShortNumber);
                        }
                    } else if (this.onlineCount <= 1 || currentChatInfo.participants_count == 0) {
                        toLowerCase = LocaleController.formatPluralString("Members", currentChatInfo.participants_count);
                    } else {
                        toLowerCase = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", currentChatInfo.participants_count), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                    }
                } else if (ChatObject.isKickedFromChat(currentChat)) {
                    toLowerCase = LocaleController.getString("YouWereKicked", C0446R.string.YouWereKicked);
                } else if (ChatObject.isLeftFromChat(currentChat)) {
                    toLowerCase = LocaleController.getString("YouLeft", C0446R.string.YouLeft);
                } else {
                    int i = currentChat.participants_count;
                    if (!(currentChatInfo == null || currentChatInfo.participants == null)) {
                        i = currentChatInfo.participants.participants.size();
                    }
                    if (this.onlineCount <= 1 || i == 0) {
                        toLowerCase = LocaleController.formatPluralString("Members", i);
                    } else {
                        toLowerCase = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                    }
                }
            } else if (currentUser != null) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(currentUser.id));
                if (user != null) {
                    currentUser = user;
                }
                if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    toLowerCase = LocaleController.getString("ChatYourSelf", C0446R.string.ChatYourSelf);
                } else {
                    if (currentUser.id != 333000) {
                        if (currentUser.id != 777000) {
                            if (currentUser.bot) {
                                toLowerCase = LocaleController.getString("Bot", C0446R.string.Bot);
                            } else {
                                toLowerCase = LocaleController.formatUserStatus(this.currentAccount, currentUser);
                            }
                        }
                    }
                    toLowerCase = LocaleController.getString("ServiceNotifications", C0446R.string.ServiceNotifications);
                }
            } else {
                charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                if (this.lastSubtitle == null) {
                    this.lastSubtitle = charSequence;
                } else {
                    this.subtitleTextView.setText(charSequence);
                }
            }
            charSequence = toLowerCase;
            if (this.lastSubtitle == null) {
                this.subtitleTextView.setText(charSequence);
            } else {
                this.lastSubtitle = charSequence;
            }
        }
    }

    public void setChatAvatar(Chat chat) {
        TLObject tLObject = chat.photo != null ? chat.photo.photo_small : null;
        this.avatarDrawable.setInfo(chat);
        if (this.avatarImageView != null) {
            this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
        }
    }

    public void setUserAvatar(User user) {
        TLObject tLObject;
        this.avatarDrawable.setInfo(user);
        if (UserObject.isUserSelf(user)) {
            this.avatarDrawable.setSavedMessages(2);
        } else if (user.photo != null) {
            tLObject = user.photo.photo_small;
            if (this.avatarImageView != null) {
                this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
            }
        }
        tLObject = null;
        if (this.avatarImageView != null) {
            this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
        }
    }

    public void checkAndUpdateAvatar() {
        if (this.parentFragment != null) {
            TLObject tLObject = null;
            User currentUser = this.parentFragment.getCurrentUser();
            Chat currentChat = this.parentFragment.getCurrentChat();
            if (currentUser != null) {
                this.avatarDrawable.setInfo(currentUser);
                if (UserObject.isUserSelf(currentUser)) {
                    this.avatarDrawable.setSavedMessages(2);
                } else if (currentUser.photo != null) {
                    tLObject = currentUser.photo.photo_small;
                }
            } else if (currentChat != null) {
                if (currentChat.photo != null) {
                    tLObject = currentChat.photo.photo_small;
                }
                this.avatarDrawable.setInfo(currentChat);
            }
            if (this.avatarImageView != null) {
                this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
            }
        }
    }

    public void updateOnlineCount() {
        if (this.parentFragment != null) {
            int i = 0;
            this.onlineCount = 0;
            ChatFull currentChatInfo = this.parentFragment.getCurrentChatInfo();
            if (currentChatInfo != null) {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if ((currentChatInfo instanceof TL_chatFull) || ((currentChatInfo instanceof TL_channelFull) && currentChatInfo.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && currentChatInfo.participants != null)) {
                    while (i < currentChatInfo.participants.participants.size()) {
                        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) currentChatInfo.participants.participants.get(i)).user_id));
                        if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                            this.onlineCount++;
                        }
                        i++;
                    }
                }
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatedConnectionState);
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            updateCurrentConnectionState();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedConnectionState);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didUpdatedConnectionState) {
            i = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            if (this.currentConnectionState != i) {
                this.currentConnectionState = i;
                updateCurrentConnectionState();
            }
        }
    }

    private void updateCurrentConnectionState() {
        CharSequence string = this.currentConnectionState == 2 ? LocaleController.getString("WaitingForNetwork", C0446R.string.WaitingForNetwork) : this.currentConnectionState == 1 ? LocaleController.getString("Connecting", C0446R.string.Connecting) : this.currentConnectionState == 5 ? LocaleController.getString("Updating", C0446R.string.Updating) : this.currentConnectionState == 4 ? LocaleController.getString("ConnectingToProxy", C0446R.string.ConnectingToProxy) : null;
        if (string != null) {
            this.lastSubtitle = this.subtitleTextView.getText();
            this.subtitleTextView.setText(string);
        } else if (this.lastSubtitle != null) {
            this.subtitleTextView.setText(this.lastSubtitle);
            this.lastSubtitle = null;
        }
    }
}
