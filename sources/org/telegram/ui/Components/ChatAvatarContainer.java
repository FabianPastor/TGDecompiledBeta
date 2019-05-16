package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
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

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean z) {
        super(context);
        this.parentFragment = chatActivity;
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        addView(this.avatarImageView);
        this.titleTextView = new SimpleTextView(context);
        this.titleTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
        this.titleTextView.setTextSize(18);
        this.titleTextView.setGravity(3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
        addView(this.titleTextView);
        this.subtitleTextView = new SimpleTextView(context);
        this.subtitleTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
        this.subtitleTextView.setTextSize(14);
        this.subtitleTextView.setGravity(3);
        addView(this.subtitleTextView);
        if (z) {
            this.timeItem = new ImageView(context);
            this.timeItem.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
            this.timeItem.setScaleType(ScaleType.CENTER);
            ImageView imageView = this.timeItem;
            TimerDrawable timerDrawable = new TimerDrawable(context);
            this.timerDrawable = timerDrawable;
            imageView.setImageDrawable(timerDrawable);
            addView(this.timeItem);
            this.timeItem.setOnClickListener(new -$$Lambda$ChatAvatarContainer$AUPtAkLLMEGZaufiE0P7VnJ7Wsk(this));
            this.timeItem.setContentDescription(LocaleController.getString("SetTimer", NUM));
        }
        if (this.parentFragment != null) {
            setOnClickListener(new -$$Lambda$ChatAvatarContainer$F8krEgvfCBOEDgLZx9CeIOwCevs(this));
            Chat currentChat = this.parentFragment.getCurrentChat();
            this.statusDrawables[0] = new TypingDotsDrawable();
            this.statusDrawables[1] = new RecordStatusDrawable();
            this.statusDrawables[2] = new SendingFileDrawable();
            this.statusDrawables[3] = new PlayingGameDrawable();
            this.statusDrawables[4] = new RoundStatusDrawable();
            int i = 0;
            while (true) {
                StatusDrawable[] statusDrawableArr = this.statusDrawables;
                if (i < statusDrawableArr.length) {
                    statusDrawableArr[i].setIsChat(currentChat != null);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public /* synthetic */ void lambda$new$0$ChatAvatarContainer(View view) {
        this.parentFragment.showDialog(AlertsCreator.createTTLAlert(getContext(), this.parentFragment.getCurrentEncryptedChat()).create());
    }

    public /* synthetic */ void lambda$new$1$ChatAvatarContainer(View view) {
        User currentUser = this.parentFragment.getCurrentUser();
        Chat currentChat = this.parentFragment.getCurrentChat();
        if (currentUser != null) {
            Bundle bundle = new Bundle();
            String str = "dialog_id";
            if (UserObject.isUserSelf(currentUser)) {
                bundle.putLong(str, this.parentFragment.getDialogId());
                MediaActivity mediaActivity = new MediaActivity(bundle, new int[]{-1, -1, -1, -1, -1});
                mediaActivity.setChatInfo(this.parentFragment.getCurrentChatInfo());
                this.parentFragment.presentFragment(mediaActivity);
                return;
            }
            bundle.putInt("user_id", currentUser.id);
            if (this.timeItem != null) {
                bundle.putLong(str, this.parentFragment.getDialogId());
            }
            ProfileActivity profileActivity = new ProfileActivity(bundle);
            profileActivity.setUserInfo(this.parentFragment.getCurrentUserInfo());
            profileActivity.setPlayProfileAnimation(true);
            this.parentFragment.presentFragment(profileActivity);
        } else if (currentChat != null) {
            Bundle bundle2 = new Bundle();
            bundle2.putInt("chat_id", currentChat.id);
            ProfileActivity profileActivity2 = new ProfileActivity(bundle2);
            profileActivity2.setChatInfo(this.parentFragment.getCurrentChatInfo());
            profileActivity2.setPlayProfileAnimation(true);
            this.parentFragment.presentFragment(profileActivity2);
        }
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
    }

    public void setTitleColors(int i, int i2) {
        this.titleTextView.setTextColor(i);
        this.subtitleTextView.setTextColor(i);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        int dp = i - AndroidUtilities.dp(70.0f);
        this.avatarImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
        this.titleTextView.measure(MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
        this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), NUM));
        }
        setMeasuredDimension(i, MeasureSpec.getSize(i2));
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0f)) / 2;
        i2 = (VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight;
        currentActionBarHeight += i2;
        this.avatarImageView.layout(AndroidUtilities.dp(8.0f), currentActionBarHeight, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(42.0f) + currentActionBarHeight);
        if (this.subtitleTextView.getVisibility() == 0) {
            this.titleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(1.3f) + currentActionBarHeight, AndroidUtilities.dp(62.0f) + this.titleTextView.getMeasuredWidth(), (this.titleTextView.getTextHeight() + currentActionBarHeight) + AndroidUtilities.dp(1.3f));
        } else {
            this.titleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(11.0f) + currentActionBarHeight, AndroidUtilities.dp(62.0f) + this.titleTextView.getMeasuredWidth(), (this.titleTextView.getTextHeight() + currentActionBarHeight) + AndroidUtilities.dp(11.0f));
        }
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.layout(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(15.0f) + currentActionBarHeight, AndroidUtilities.dp(58.0f), AndroidUtilities.dp(49.0f) + currentActionBarHeight);
        }
        this.subtitleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(24.0f) + currentActionBarHeight, AndroidUtilities.dp(62.0f) + this.subtitleTextView.getMeasuredWidth(), (currentActionBarHeight + this.subtitleTextView.getTextHeight()) + AndroidUtilities.dp(24.0f));
    }

    public void showTimeItem() {
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.setVisibility(0);
        }
    }

    public void hideTimeItem() {
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.setVisibility(8);
        }
    }

    public void setTime(int i) {
        TimerDrawable timerDrawable = this.timerDrawable;
        if (timerDrawable != null) {
            timerDrawable.setTime(i);
        }
    }

    public void setTitleIcons(Drawable drawable, Drawable drawable2) {
        this.titleTextView.setLeftDrawable(drawable);
        if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
            this.titleTextView.setRightDrawable(drawable2);
        }
    }

    public void setTitle(CharSequence charSequence) {
        setTitle(charSequence, false);
    }

    public void setTitle(CharSequence charSequence, boolean z) {
        this.titleTextView.setText(charSequence);
        if (!z) {
            this.titleTextView.setRightDrawable(null);
        } else if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
            Drawable scamDrawable = new ScamDrawable(11);
            scamDrawable.setColor(Theme.getColor("actionBarDefaultSubtitle"));
            this.titleTextView.setRightDrawable(scamDrawable);
        }
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
        int i = 0;
        if (z) {
            try {
                Integer num = (Integer) MessagesController.getInstance(this.currentAccount).printingStringsTypes.get(this.parentFragment.getDialogId());
                this.subtitleTextView.setLeftDrawable(this.statusDrawables[num.intValue()]);
                while (i < this.statusDrawables.length) {
                    if (i == num.intValue()) {
                        this.statusDrawables[i].start();
                    } else {
                        this.statusDrawables[i].stop();
                    }
                    i++;
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.subtitleTextView.setLeftDrawable(null);
        while (true) {
            StatusDrawable[] statusDrawableArr = this.statusDrawables;
            if (i < statusDrawableArr.length) {
                statusDrawableArr[i].stop();
                i++;
            } else {
                return;
            }
        }
    }

    public void updateSubtitle() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            User currentUser = chatActivity.getCurrentUser();
            if (UserObject.isUserSelf(currentUser)) {
                if (this.subtitleTextView.getVisibility() != 8) {
                    this.subtitleTextView.setVisibility(8);
                }
                return;
            }
            Chat currentChat = this.parentFragment.getCurrentChat();
            CharSequence charSequence = (CharSequence) MessagesController.getInstance(this.currentAccount).printingStrings.get(this.parentFragment.getDialogId());
            CharSequence charSequence2 = "";
            if (charSequence != null) {
                charSequence = TextUtils.replace(charSequence, new String[]{"..."}, new String[]{charSequence2});
            }
            if (charSequence == null || charSequence.length() == 0 || (ChatObject.isChannel(currentChat) && !currentChat.megagroup)) {
                String format;
                setTypingAnimation(false);
                int i;
                if (currentChat != null) {
                    ChatFull currentChatInfo = this.parentFragment.getCurrentChatInfo();
                    String str = "OnlineCount";
                    String str2 = "%s, %s";
                    String str3 = "Members";
                    if (ChatObject.isChannel(currentChat)) {
                        if (currentChatInfo != null) {
                            int i2 = currentChatInfo.participants_count;
                            if (i2 != 0) {
                                if (currentChat.megagroup) {
                                    format = this.onlineCount > 1 ? String.format(str2, new Object[]{LocaleController.formatPluralString(str3, i2), LocaleController.formatPluralString(str, Math.min(this.onlineCount, currentChatInfo.participants_count))}) : LocaleController.formatPluralString(str3, i2);
                                } else {
                                    int[] iArr = new int[1];
                                    String formatShortNumber = LocaleController.formatShortNumber(i2, iArr);
                                    str = "%d";
                                    format = currentChat.megagroup ? LocaleController.formatPluralString(str3, iArr[0]).replace(String.format(str, new Object[]{Integer.valueOf(iArr[0])}), formatShortNumber) : LocaleController.formatPluralString("Subscribers", iArr[0]).replace(String.format(str, new Object[]{Integer.valueOf(iArr[0])}), formatShortNumber);
                                }
                            }
                        }
                        format = currentChat.megagroup ? LocaleController.getString("Loading", NUM).toLowerCase() : (currentChat.flags & 64) != 0 ? LocaleController.getString("ChannelPublic", NUM).toLowerCase() : LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                    } else if (ChatObject.isKickedFromChat(currentChat)) {
                        format = LocaleController.getString("YouWereKicked", NUM);
                    } else if (ChatObject.isLeftFromChat(currentChat)) {
                        format = LocaleController.getString("YouLeft", NUM);
                    } else {
                        i = currentChat.participants_count;
                        if (currentChatInfo != null) {
                            ChatParticipants chatParticipants = currentChatInfo.participants;
                            if (chatParticipants != null) {
                                i = chatParticipants.participants.size();
                            }
                        }
                        format = (this.onlineCount <= 1 || i == 0) ? LocaleController.formatPluralString(str3, i) : String.format(str2, new Object[]{LocaleController.formatPluralString(str3, i), LocaleController.formatPluralString(str, this.onlineCount)});
                    }
                } else if (currentUser != null) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(currentUser.id));
                    if (user != null) {
                        currentUser = user;
                    }
                    if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        format = LocaleController.getString("ChatYourSelf", NUM);
                    } else {
                        i = currentUser.id;
                        format = (i == 333000 || i == 777000 || i == 42777) ? LocaleController.getString("ServiceNotifications", NUM) : MessagesController.isSupportUser(currentUser) ? LocaleController.getString("SupportStatus", NUM) : currentUser.bot ? LocaleController.getString("Bot", NUM) : LocaleController.formatUserStatus(this.currentAccount, currentUser);
                    }
                }
                charSequence2 = format;
            } else {
                setTypingAnimation(true);
                charSequence2 = charSequence;
            }
            if (this.lastSubtitle == null) {
                this.subtitleTextView.setText(charSequence2);
            } else {
                this.lastSubtitle = charSequence2;
            }
        }
    }

    public void setChatAvatar(Chat chat) {
        this.avatarDrawable.setInfo(chat);
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", this.avatarDrawable, (Object) chat);
        }
    }

    public void setUserAvatar(User user) {
        this.avatarDrawable.setInfo(user);
        BackupImageView backupImageView;
        if (UserObject.isUserSelf(user)) {
            this.avatarDrawable.setAvatarType(2);
            backupImageView = this.avatarImageView;
            if (backupImageView != null) {
                backupImageView.setImage(null, null, this.avatarDrawable, (Object) user);
                return;
            }
            return;
        }
        backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImage(ImageLocation.getForUser(user, false), "50_50", this.avatarDrawable, (Object) user);
        }
    }

    public void checkAndUpdateAvatar() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            Object currentUser = chatActivity.getCurrentUser();
            Object currentChat = this.parentFragment.getCurrentChat();
            String str = "50_50";
            if (currentUser != null) {
                this.avatarDrawable.setInfo((User) currentUser);
                BackupImageView backupImageView;
                if (UserObject.isUserSelf(currentUser)) {
                    this.avatarDrawable.setAvatarType(2);
                    backupImageView = this.avatarImageView;
                    if (backupImageView != null) {
                        backupImageView.setImage(null, null, this.avatarDrawable, currentUser);
                    }
                } else {
                    backupImageView = this.avatarImageView;
                    if (backupImageView != null) {
                        backupImageView.setImage(ImageLocation.getForUser(currentUser, false), str, this.avatarDrawable, currentUser);
                    }
                }
            } else if (currentChat != null) {
                this.avatarDrawable.setInfo((Chat) currentChat);
                BackupImageView backupImageView2 = this.avatarImageView;
                if (backupImageView2 != null) {
                    backupImageView2.setImage(ImageLocation.getForChat(currentChat, false), str, this.avatarDrawable, currentChat);
                }
            }
        }
    }

    public void updateOnlineCount() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            int i = 0;
            this.onlineCount = 0;
            ChatFull currentChatInfo = chatActivity.getCurrentChatInfo();
            if (currentChatInfo != null) {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if (!(currentChatInfo instanceof TL_chatFull)) {
                    boolean z = currentChatInfo instanceof TL_channelFull;
                    if (!z || currentChatInfo.participants_count > 200 || currentChatInfo.participants == null) {
                        if (z && currentChatInfo.participants_count > 200) {
                            this.onlineCount = currentChatInfo.online_count;
                        }
                    }
                }
                while (i < currentChatInfo.participants.participants.size()) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) currentChatInfo.participants.participants.get(i)).user_id));
                    if (user != null) {
                        UserStatus userStatus = user.status;
                        if (userStatus != null && ((userStatus.expires > currentTime || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) && user.status.expires > 10000)) {
                            this.onlineCount++;
                        }
                    }
                    i++;
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            updateCurrentConnectionState();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didUpdateConnectionState) {
            i = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            if (this.currentConnectionState != i) {
                this.currentConnectionState = i;
                updateCurrentConnectionState();
            }
        }
    }

    private void updateCurrentConnectionState() {
        int i = this.currentConnectionState;
        CharSequence string = i == 2 ? LocaleController.getString("WaitingForNetwork", NUM) : i == 1 ? LocaleController.getString("Connecting", NUM) : i == 5 ? LocaleController.getString("Updating", NUM) : i == 4 ? LocaleController.getString("ConnectingToProxy", NUM) : null;
        if (string == null) {
            string = this.lastSubtitle;
            if (string != null) {
                this.subtitleTextView.setText(string);
                this.lastSubtitle = null;
                return;
            }
            return;
        }
        this.lastSubtitle = this.subtitleTextView.getText();
        this.subtitleTextView.setText(string);
    }
}
