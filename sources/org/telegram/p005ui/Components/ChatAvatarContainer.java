package org.telegram.p005ui.Components;

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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ChatActivity;
import org.telegram.p005ui.MediaActivity;
import org.telegram.p005ui.ProfileActivity;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.Components.ChatAvatarContainer */
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

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean needTime) {
        super(context);
        this.parentFragment = chatActivity;
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.m9dp(21.0f));
        addView(this.avatarImageView);
        this.titleTextView = new SimpleTextView(context);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.titleTextView.setTextSize(18);
        this.titleTextView.setGravity(3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.m9dp(1.3f));
        addView(this.titleTextView);
        this.subtitleTextView = new SimpleTextView(context);
        this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.subtitleTextView.setTextSize(14);
        this.subtitleTextView.setGravity(3);
        addView(this.subtitleTextView);
        if (needTime) {
            this.timeItem = new ImageView(context);
            this.timeItem.setPadding(AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(5.0f), AndroidUtilities.m9dp(5.0f));
            this.timeItem.setScaleType(ScaleType.CENTER);
            ImageView imageView = this.timeItem;
            Drawable timerDrawable = new TimerDrawable(context);
            this.timerDrawable = timerDrawable;
            imageView.setImageDrawable(timerDrawable);
            addView(this.timeItem);
            this.timeItem.setOnClickListener(new ChatAvatarContainer$$Lambda$0(this));
        }
        if (this.parentFragment != null) {
            setOnClickListener(new ChatAvatarContainer$$Lambda$1(this));
            Chat chat = this.parentFragment.getCurrentChat();
            this.statusDrawables[0] = new TypingDotsDrawable();
            this.statusDrawables[1] = new RecordStatusDrawable();
            this.statusDrawables[2] = new SendingFileDrawable();
            this.statusDrawables[3] = new PlayingGameDrawable();
            this.statusDrawables[4] = new RoundStatusDrawable();
            for (StatusDrawable statusDrawable : this.statusDrawables) {
                boolean z;
                if (chat != null) {
                    z = true;
                } else {
                    z = false;
                }
                statusDrawable.setIsChat(z);
            }
        }
    }

    final /* synthetic */ void lambda$new$0$ChatAvatarContainer(View v) {
        this.parentFragment.showDialog(AlertsCreator.createTTLAlert(getContext(), this.parentFragment.getCurrentEncryptedChat()).create());
    }

    final /* synthetic */ void lambda$new$1$ChatAvatarContainer(View v) {
        User user = this.parentFragment.getCurrentUser();
        Chat chat = this.parentFragment.getCurrentChat();
        Bundle args;
        ProfileActivity fragment;
        if (user != null) {
            args = new Bundle();
            if (UserObject.isUserSelf(user)) {
                args.putLong("dialog_id", this.parentFragment.getDialogId());
                MediaActivity fragment2 = new MediaActivity(args, new int[]{-1, -1, -1, -1, -1});
                fragment2.setChatInfo(this.parentFragment.getCurrentChatInfo());
                this.parentFragment.presentFragment(fragment2);
                return;
            }
            args.putInt("user_id", user.f176id);
            if (this.timeItem != null) {
                args.putLong("dialog_id", this.parentFragment.getDialogId());
            }
            fragment = new ProfileActivity(args);
            fragment.setUserInfo(this.parentFragment.getCurrentUserInfo());
            fragment.setPlayProfileAnimation(true);
            this.parentFragment.presentFragment(fragment);
        } else if (chat != null) {
            args = new Bundle();
            args.putInt("chat_id", chat.f78id);
            fragment = new ProfileActivity(args);
            fragment.setChatInfo(this.parentFragment.getCurrentChatInfo());
            fragment.setPlayProfileAnimation(true);
            this.parentFragment.presentFragment(fragment);
        }
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
    }

    public void setTitleColors(int title, int subtitle) {
        this.titleTextView.setTextColor(title);
        this.subtitleTextView.setTextColor(title);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - AndroidUtilities.m9dp(70.0f);
        this.avatarImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(42.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(42.0f), NUM));
        this.titleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(24.0f), Integer.MIN_VALUE));
        this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(20.0f), Integer.MIN_VALUE));
        if (this.timeItem != null) {
            this.timeItem.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(34.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(34.0f), NUM));
        }
        setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int currentActionBarHeight = (CLASSNAMEActionBar.getCurrentActionBarHeight() - AndroidUtilities.m9dp(42.0f)) / 2;
        int i = (VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight;
        int viewTop = currentActionBarHeight + i;
        this.avatarImageView.layout(AndroidUtilities.m9dp(8.0f), viewTop, AndroidUtilities.m9dp(50.0f), AndroidUtilities.m9dp(42.0f) + viewTop);
        if (this.subtitleTextView.getVisibility() == 0) {
            this.titleTextView.layout(AndroidUtilities.m9dp(62.0f), AndroidUtilities.m9dp(1.3f) + viewTop, AndroidUtilities.m9dp(62.0f) + this.titleTextView.getMeasuredWidth(), (this.titleTextView.getTextHeight() + viewTop) + AndroidUtilities.m9dp(1.3f));
        } else {
            this.titleTextView.layout(AndroidUtilities.m9dp(62.0f), AndroidUtilities.m9dp(11.0f) + viewTop, AndroidUtilities.m9dp(62.0f) + this.titleTextView.getMeasuredWidth(), (this.titleTextView.getTextHeight() + viewTop) + AndroidUtilities.m9dp(11.0f));
        }
        if (this.timeItem != null) {
            this.timeItem.layout(AndroidUtilities.m9dp(24.0f), AndroidUtilities.m9dp(15.0f) + viewTop, AndroidUtilities.m9dp(58.0f), AndroidUtilities.m9dp(49.0f) + viewTop);
        }
        this.subtitleTextView.layout(AndroidUtilities.m9dp(62.0f), AndroidUtilities.m9dp(24.0f) + viewTop, AndroidUtilities.m9dp(62.0f) + this.subtitleTextView.getMeasuredWidth(), (this.subtitleTextView.getTextHeight() + viewTop) + AndroidUtilities.m9dp(24.0f));
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

    public void setTime(int value) {
        if (this.timerDrawable != null) {
            this.timerDrawable.setTime(value);
        }
    }

    public void setTitleIcons(int leftIcon, int rightIcon) {
        this.titleTextView.setLeftDrawable(leftIcon);
        this.titleTextView.setRightDrawable(rightIcon);
    }

    public void setTitleIcons(Drawable leftIcon, Drawable rightIcon) {
        this.titleTextView.setLeftDrawable(leftIcon);
        this.titleTextView.setRightDrawable(rightIcon);
    }

    public void setTitle(CharSequence value) {
        this.titleTextView.setText(value);
    }

    public void setSubtitle(CharSequence value) {
        if (this.lastSubtitle == null) {
            this.subtitleTextView.setText(value);
        } else {
            this.lastSubtitle = value;
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

    private void setTypingAnimation(boolean start) {
        int a;
        if (start) {
            try {
                Integer type = (Integer) MessagesController.getInstance(this.currentAccount).printingStringsTypes.get(this.parentFragment.getDialogId());
                this.subtitleTextView.setLeftDrawable(this.statusDrawables[type.intValue()]);
                for (a = 0; a < this.statusDrawables.length; a++) {
                    if (a == type.intValue()) {
                        this.statusDrawables[a].start();
                    } else {
                        this.statusDrawables[a].stop();
                    }
                }
                return;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        this.subtitleTextView.setLeftDrawable(null);
        for (StatusDrawable stop : this.statusDrawables) {
            stop.stop();
        }
    }

    public void updateSubtitle() {
        if (this.parentFragment != null) {
            User user = this.parentFragment.getCurrentUser();
            if (!UserObject.isUserSelf(user)) {
                CharSequence newSubtitle;
                Chat chat = this.parentFragment.getCurrentChat();
                CharSequence printString = (CharSequence) MessagesController.getInstance(this.currentAccount).printingStrings.get(this.parentFragment.getDialogId());
                if (printString != null) {
                    printString = TextUtils.replace(printString, new String[]{"..."}, new String[]{TtmlNode.ANONYMOUS_REGION_ID});
                }
                if (printString == null || printString.length() == 0 || (ChatObject.isChannel(chat) && !chat.megagroup)) {
                    setTypingAnimation(false);
                    if (chat != null) {
                        ChatFull info = this.parentFragment.getCurrentChatInfo();
                        if (ChatObject.isChannel(chat)) {
                            if (info == null || info.participants_count == 0) {
                                if (chat.megagroup) {
                                    newSubtitle = LocaleController.getString("Loading", R.string.Loading).toLowerCase();
                                } else if ((chat.flags & 64) != 0) {
                                    newSubtitle = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                                } else {
                                    newSubtitle = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                                }
                            } else if (!chat.megagroup || info.participants_count > Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                                int[] result = new int[1];
                                String shortNumber = LocaleController.formatShortNumber(info.participants_count, result);
                                if (chat.megagroup) {
                                    newSubtitle = LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                                } else {
                                    newSubtitle = LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                                }
                            } else if (this.onlineCount <= 1 || info.participants_count == 0) {
                                newSubtitle = LocaleController.formatPluralString("Members", info.participants_count);
                            } else {
                                newSubtitle = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", info.participants_count), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                            }
                        } else if (ChatObject.isKickedFromChat(chat)) {
                            newSubtitle = LocaleController.getString("YouWereKicked", R.string.YouWereKicked);
                        } else if (ChatObject.isLeftFromChat(chat)) {
                            newSubtitle = LocaleController.getString("YouLeft", R.string.YouLeft);
                        } else {
                            int count = chat.participants_count;
                            if (!(info == null || info.participants == null)) {
                                count = info.participants.participants.size();
                            }
                            if (this.onlineCount <= 1 || count == 0) {
                                newSubtitle = LocaleController.formatPluralString("Members", count);
                            } else {
                                newSubtitle = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", count), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                            }
                        }
                    } else if (user != null) {
                        String newStatus;
                        User newUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(user.f176id));
                        if (newUser != null) {
                            user = newUser;
                        }
                        if (user.f176id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            newStatus = LocaleController.getString("ChatYourSelf", R.string.ChatYourSelf);
                        } else if (user.f176id == 333000 || user.f176id == 777000) {
                            newStatus = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                        } else if (user.bot) {
                            newStatus = LocaleController.getString("Bot", R.string.Bot);
                        } else {
                            newStatus = LocaleController.formatUserStatus(this.currentAccount, user);
                        }
                        Object newSubtitle2 = newStatus;
                    } else {
                        newSubtitle2 = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                } else {
                    newSubtitle2 = printString;
                    setTypingAnimation(true);
                }
                if (this.lastSubtitle == null) {
                    this.subtitleTextView.setText(newSubtitle2);
                } else {
                    this.lastSubtitle = newSubtitle2;
                }
            } else if (this.subtitleTextView.getVisibility() != 8) {
                this.subtitleTextView.setVisibility(8);
            }
        }
    }

    public void setChatAvatar(Chat chat) {
        TLObject newPhoto = null;
        if (chat.photo != null) {
            newPhoto = chat.photo.photo_small;
        }
        this.avatarDrawable.setInfo(chat);
        if (this.avatarImageView != null) {
            this.avatarImageView.setImage(newPhoto, "50_50", this.avatarDrawable, (Object) chat);
        }
    }

    public void setUserAvatar(User user) {
        TLObject newPhoto = null;
        this.avatarDrawable.setInfo(user);
        if (UserObject.isUserSelf(user)) {
            this.avatarDrawable.setSavedMessages(2);
        } else if (user.photo != null) {
            newPhoto = user.photo.photo_small;
        }
        if (this.avatarImageView != null) {
            this.avatarImageView.setImage(newPhoto, "50_50", this.avatarDrawable, (Object) user);
        }
    }

    public void checkAndUpdateAvatar() {
        if (this.parentFragment != null) {
            TLObject newPhoto = null;
            Object parentObject = null;
            User user = this.parentFragment.getCurrentUser();
            Chat chat = this.parentFragment.getCurrentChat();
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                if (UserObject.isUserSelf(user)) {
                    this.avatarDrawable.setSavedMessages(2);
                } else if (user.photo != null) {
                    newPhoto = user.photo.photo_small;
                }
                parentObject = user;
            } else if (chat != null) {
                if (chat.photo != null) {
                    newPhoto = chat.photo.photo_small;
                }
                this.avatarDrawable.setInfo(chat);
                Chat parentObject2 = chat;
            }
            if (this.avatarImageView != null) {
                this.avatarImageView.setImage(newPhoto, "50_50", this.avatarDrawable, parentObject2);
            }
        }
    }

    public void updateOnlineCount() {
        if (this.parentFragment != null) {
            this.onlineCount = 0;
            ChatFull info = this.parentFragment.getCurrentChatInfo();
            if (info != null) {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if ((info instanceof TL_chatFull) || ((info instanceof TL_channelFull) && info.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && info.participants != null)) {
                    for (int a = 0; a < info.participants.participants.size(); a++) {
                        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) info.participants.participants.get(a)).user_id));
                        if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.f176id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                            this.onlineCount++;
                        }
                    }
                }
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            updateCurrentConnectionState();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didUpdateConnectionState) {
            int state = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                updateCurrentConnectionState();
            }
        }
    }

    private void updateCurrentConnectionState() {
        String title = null;
        if (this.currentConnectionState == 2) {
            title = LocaleController.getString("WaitingForNetwork", R.string.WaitingForNetwork);
        } else if (this.currentConnectionState == 1) {
            title = LocaleController.getString("Connecting", R.string.Connecting);
        } else if (this.currentConnectionState == 5) {
            title = LocaleController.getString("Updating", R.string.Updating);
        } else if (this.currentConnectionState == 4) {
            title = LocaleController.getString("ConnectingToProxy", R.string.ConnectingToProxy);
        }
        if (title != null) {
            this.lastSubtitle = this.subtitleTextView.getText();
            this.subtitleTextView.setText(title);
        } else if (this.lastSubtitle != null) {
            this.subtitleTextView.setText(this.lastSubtitle);
            this.lastSubtitle = null;
        }
    }
}
