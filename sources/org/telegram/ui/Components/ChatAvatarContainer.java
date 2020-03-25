package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipants;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.SharedMediaLayout;

public class ChatAvatarContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentConnectionState;
    private boolean[] isOnline = new boolean[1];
    private CharSequence lastSubtitle;
    private String lastSubtitleColorKey;
    private boolean occupyStatusBar = true;
    private int onlineCount = -1;
    private ChatActivity parentFragment;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private SimpleTextView subtitleTextView;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private SimpleTextView titleTextView;

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean z) {
        super(context);
        this.parentFragment = chatActivity;
        if (chatActivity != null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(chatActivity);
        }
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        addView(this.avatarImageView);
        ChatActivity chatActivity2 = this.parentFragment;
        if (chatActivity2 != null && !chatActivity2.isInScheduleMode()) {
            this.avatarImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatAvatarContainer.this.lambda$new$0$ChatAvatarContainer(view);
                }
            });
        }
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.titleTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
        this.titleTextView.setTextSize(18);
        this.titleTextView.setGravity(3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
        addView(this.titleTextView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.subtitleTextView = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
        this.subtitleTextView.setTag("actionBarDefaultSubtitle");
        this.subtitleTextView.setTextSize(14);
        this.subtitleTextView.setGravity(3);
        addView(this.subtitleTextView);
        if (z) {
            ImageView imageView = new ImageView(context);
            this.timeItem = imageView;
            imageView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
            this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView2 = this.timeItem;
            TimerDrawable timerDrawable2 = new TimerDrawable(context);
            this.timerDrawable = timerDrawable2;
            imageView2.setImageDrawable(timerDrawable2);
            addView(this.timeItem);
            this.timeItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatAvatarContainer.this.lambda$new$1$ChatAvatarContainer(view);
                }
            });
            this.timeItem.setContentDescription(LocaleController.getString("SetTimer", NUM));
        }
        ChatActivity chatActivity3 = this.parentFragment;
        if (chatActivity3 != null && !chatActivity3.isInScheduleMode()) {
            setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatAvatarContainer.this.lambda$new$2$ChatAvatarContainer(view);
                }
            });
            TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
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
        openProfile(true);
    }

    public /* synthetic */ void lambda$new$1$ChatAvatarContainer(View view) {
        this.parentFragment.showDialog(AlertsCreator.createTTLAlert(getContext(), this.parentFragment.getCurrentEncryptedChat()).create());
    }

    public /* synthetic */ void lambda$new$2$ChatAvatarContainer(View view) {
        openProfile(false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001b, code lost:
        if (r7.avatarImageView.getImageReceiver().hasNotThumb() != false) goto L_0x001e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void openProfile(boolean r8) {
        /*
            r7 = this;
            r0 = 0
            if (r8 == 0) goto L_0x001e
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 != 0) goto L_0x001d
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r1.x
            int r1 = r1.y
            if (r2 > r1) goto L_0x001d
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r1 = r1.hasNotThumb()
            if (r1 != 0) goto L_0x001e
        L_0x001d:
            r8 = 0
        L_0x001e:
            org.telegram.ui.ChatActivity r1 = r7.parentFragment
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            org.telegram.ui.ChatActivity r2 = r7.parentFragment
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getCurrentChat()
            r3 = 2
            r4 = 1
            if (r1 == 0) goto L_0x00a7
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            boolean r5 = org.telegram.messenger.UserObject.isUserSelf(r1)
            java.lang.String r6 = "dialog_id"
            if (r5 == 0) goto L_0x006b
            org.telegram.ui.ChatActivity r8 = r7.parentFragment
            long r3 = r8.getDialogId()
            r2.putLong(r6, r3)
            r8 = 6
            int[] r1 = new int[r8]
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r3 = r7.sharedMediaPreloader
            int[] r3 = r3.getLastMediaCount()
            java.lang.System.arraycopy(r3, r0, r1, r0, r8)
            org.telegram.ui.MediaActivity r8 = new org.telegram.ui.MediaActivity
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r0 = r7.sharedMediaPreloader
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r0 = r0.getSharedMediaData()
            r3 = -1
            r8.<init>(r2, r1, r0, r3)
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.getCurrentChatInfo()
            r8.setChatInfo(r0)
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            r0.presentFragment(r8)
            goto L_0x00d1
        L_0x006b:
            int r0 = r1.id
            java.lang.String r1 = "user_id"
            r2.putInt(r1, r0)
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            boolean r0 = r0.hasReportSpam()
            java.lang.String r1 = "reportSpam"
            r2.putBoolean(r1, r0)
            android.widget.ImageView r0 = r7.timeItem
            if (r0 == 0) goto L_0x008a
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            long r0 = r0.getDialogId()
            r2.putLong(r6, r0)
        L_0x008a:
            org.telegram.ui.ProfileActivity r0 = new org.telegram.ui.ProfileActivity
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r1 = r7.sharedMediaPreloader
            r0.<init>(r2, r1)
            org.telegram.ui.ChatActivity r1 = r7.parentFragment
            org.telegram.tgnet.TLRPC$UserFull r1 = r1.getCurrentUserInfo()
            r0.setUserInfo(r1)
            if (r8 == 0) goto L_0x009d
            goto L_0x009e
        L_0x009d:
            r3 = 1
        L_0x009e:
            r0.setPlayProfileAnimation(r3)
            org.telegram.ui.ChatActivity r8 = r7.parentFragment
            r8.presentFragment(r0)
            goto L_0x00d1
        L_0x00a7:
            if (r2 == 0) goto L_0x00d1
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r1 = r2.id
            java.lang.String r2 = "chat_id"
            r0.putInt(r2, r1)
            org.telegram.ui.ProfileActivity r1 = new org.telegram.ui.ProfileActivity
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r2 = r7.sharedMediaPreloader
            r1.<init>(r0, r2)
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.getCurrentChatInfo()
            r1.setChatInfo(r0)
            if (r8 == 0) goto L_0x00c8
            goto L_0x00c9
        L_0x00c8:
            r3 = 1
        L_0x00c9:
            r1.setPlayProfileAnimation(r3)
            org.telegram.ui.ChatActivity r8 = r7.parentFragment
            r8.presentFragment(r1)
        L_0x00d1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAvatarContainer.openProfile(boolean):void");
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
    }

    public void setTitleColors(int i, int i2) {
        this.titleTextView.setTextColor(i);
        this.subtitleTextView.setTextColor(i2);
        this.subtitleTextView.setTag(Integer.valueOf(i2));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = size - AndroidUtilities.dp(70.0f);
        this.avatarImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
        this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
        this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), NUM));
        }
        setMeasuredDimension(size, View.MeasureSpec.getSize(i2));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int currentActionBarHeight = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0f)) / 2) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
        this.avatarImageView.layout(AndroidUtilities.dp(8.0f), currentActionBarHeight, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(42.0f) + currentActionBarHeight);
        if (this.subtitleTextView.getVisibility() == 0) {
            this.titleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(1.3f) + currentActionBarHeight, AndroidUtilities.dp(62.0f) + this.titleTextView.getMeasuredWidth(), this.titleTextView.getTextHeight() + currentActionBarHeight + AndroidUtilities.dp(1.3f));
        } else {
            this.titleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(11.0f) + currentActionBarHeight, AndroidUtilities.dp(62.0f) + this.titleTextView.getMeasuredWidth(), this.titleTextView.getTextHeight() + currentActionBarHeight + AndroidUtilities.dp(11.0f));
        }
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.layout(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(15.0f) + currentActionBarHeight, AndroidUtilities.dp(58.0f), AndroidUtilities.dp(49.0f) + currentActionBarHeight);
        }
        this.subtitleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(24.0f) + currentActionBarHeight, AndroidUtilities.dp(62.0f) + this.subtitleTextView.getMeasuredWidth(), currentActionBarHeight + this.subtitleTextView.getTextHeight() + AndroidUtilities.dp(24.0f));
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
        TimerDrawable timerDrawable2 = this.timerDrawable;
        if (timerDrawable2 != null) {
            timerDrawable2.setTime(i);
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
        if (z) {
            if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
                ScamDrawable scamDrawable = new ScamDrawable(11);
                scamDrawable.setColor(Theme.getColor("actionBarDefaultSubtitle"));
                this.titleTextView.setRightDrawable((Drawable) scamDrawable);
            }
        } else if (this.titleTextView.getRightDrawable() instanceof ScamDrawable) {
            this.titleTextView.setRightDrawable((Drawable) null);
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

    public void onDestroy() {
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
        if (sharedMediaPreloader2 != null) {
            sharedMediaPreloader2.onDestroy(this.parentFragment);
        }
    }

    private void setTypingAnimation(boolean z) {
        int i = 0;
        if (z) {
            try {
                Integer num = MessagesController.getInstance(this.currentAccount).printingStringsTypes.get(this.parentFragment.getDialogId());
                this.subtitleTextView.setLeftDrawable((Drawable) this.statusDrawables[num.intValue()]);
                while (i < this.statusDrawables.length) {
                    if (i == num.intValue()) {
                        this.statusDrawables[i].start();
                    } else {
                        this.statusDrawables[i].stop();
                    }
                    i++;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            this.subtitleTextView.setLeftDrawable((Drawable) null);
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
    }

    public void updateSubtitle() {
        String str;
        TLRPC$ChatParticipants tLRPC$ChatParticipants;
        int i;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC$User currentUser = chatActivity.getCurrentUser();
            if (!UserObject.isUserSelf(currentUser) && !this.parentFragment.isInScheduleMode()) {
                TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
                CharSequence charSequence = MessagesController.getInstance(this.currentAccount).printingStrings.get(this.parentFragment.getDialogId());
                String str2 = "";
                boolean z = true;
                if (charSequence != null) {
                    charSequence = TextUtils.replace(charSequence, new String[]{"..."}, new String[]{str2});
                }
                if (charSequence == null || charSequence.length() == 0 || (ChatObject.isChannel(currentChat) && !currentChat.megagroup)) {
                    setTypingAnimation(false);
                    if (currentChat != null) {
                        TLRPC$ChatFull currentChatInfo = this.parentFragment.getCurrentChatInfo();
                        if (ChatObject.isChannel(currentChat)) {
                            if (currentChatInfo == null || (i = currentChatInfo.participants_count) == 0) {
                                if (currentChat.megagroup) {
                                    if (currentChatInfo == null) {
                                        str = LocaleController.getString("Loading", NUM).toLowerCase();
                                    } else if (currentChat.has_geo) {
                                        str = LocaleController.getString("MegaLocation", NUM).toLowerCase();
                                    } else if (!TextUtils.isEmpty(currentChat.username)) {
                                        str = LocaleController.getString("MegaPublic", NUM).toLowerCase();
                                    } else {
                                        str = LocaleController.getString("MegaPrivate", NUM).toLowerCase();
                                    }
                                } else if ((currentChat.flags & 64) != 0) {
                                    str = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                                } else {
                                    str = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                                }
                            } else if (!currentChat.megagroup) {
                                int[] iArr = new int[1];
                                String formatShortNumber = LocaleController.formatShortNumber(i, iArr);
                                if (currentChat.megagroup) {
                                    str = LocaleController.formatPluralString("Members", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), formatShortNumber);
                                } else {
                                    str = LocaleController.formatPluralString("Subscribers", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), formatShortNumber);
                                }
                            } else if (this.onlineCount > 1) {
                                str = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("OnlineCount", Math.min(this.onlineCount, currentChatInfo.participants_count))});
                            } else {
                                str = LocaleController.formatPluralString("Members", i);
                            }
                        } else if (ChatObject.isKickedFromChat(currentChat)) {
                            str = LocaleController.getString("YouWereKicked", NUM);
                        } else if (ChatObject.isLeftFromChat(currentChat)) {
                            str = LocaleController.getString("YouLeft", NUM);
                        } else {
                            int i2 = currentChat.participants_count;
                            if (!(currentChatInfo == null || (tLRPC$ChatParticipants = currentChatInfo.participants) == null)) {
                                i2 = tLRPC$ChatParticipants.participants.size();
                            }
                            if (this.onlineCount <= 1 || i2 == 0) {
                                str = LocaleController.formatPluralString("Members", i2);
                            } else {
                                str = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", i2), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                            }
                        }
                    } else {
                        if (currentUser != null) {
                            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(currentUser.id));
                            if (user != null) {
                                currentUser = user;
                            }
                            if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                str = LocaleController.getString("ChatYourSelf", NUM);
                            } else {
                                int i3 = currentUser.id;
                                if (i3 == 333000 || i3 == 777000 || i3 == 42777) {
                                    str = LocaleController.getString("ServiceNotifications", NUM);
                                } else if (MessagesController.isSupportUser(currentUser)) {
                                    str = LocaleController.getString("SupportStatus", NUM);
                                } else if (currentUser.bot) {
                                    str = LocaleController.getString("Bot", NUM);
                                } else {
                                    boolean[] zArr = this.isOnline;
                                    zArr[0] = false;
                                    str2 = LocaleController.formatUserStatus(this.currentAccount, currentUser, zArr);
                                    z = this.isOnline[0];
                                }
                            }
                        }
                        z = false;
                    }
                    str2 = str;
                    z = false;
                } else {
                    setTypingAnimation(true);
                    str2 = charSequence;
                }
                this.lastSubtitleColorKey = z ? "chat_status" : "actionBarDefaultSubtitle";
                if (this.lastSubtitle == null) {
                    this.subtitleTextView.setText(str2);
                    this.subtitleTextView.setTextColor(Theme.getColor(this.lastSubtitleColorKey));
                    this.subtitleTextView.setTag(this.lastSubtitleColorKey);
                    return;
                }
                this.lastSubtitle = str2;
            } else if (this.subtitleTextView.getVisibility() != 8) {
                this.subtitleTextView.setVisibility(8);
            }
        }
    }

    public void setChatAvatar(TLRPC$Chat tLRPC$Chat) {
        this.avatarDrawable.setInfo(tLRPC$Chat);
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImage(ImageLocation.getForChat(tLRPC$Chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$Chat);
        }
    }

    public void setUserAvatar(TLRPC$User tLRPC$User) {
        this.avatarDrawable.setInfo(tLRPC$User);
        if (UserObject.isUserSelf(tLRPC$User)) {
            this.avatarDrawable.setAvatarType(1);
            this.avatarDrawable.setSmallSize(true);
            BackupImageView backupImageView = this.avatarImageView;
            if (backupImageView != null) {
                backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) tLRPC$User);
                return;
            }
            return;
        }
        this.avatarDrawable.setSmallSize(false);
        BackupImageView backupImageView2 = this.avatarImageView;
        if (backupImageView2 != null) {
            backupImageView2.setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$User);
        }
    }

    public void checkAndUpdateAvatar() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC$User currentUser = chatActivity.getCurrentUser();
            TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
            if (currentUser != null) {
                this.avatarDrawable.setInfo(currentUser);
                if (UserObject.isUserSelf(currentUser)) {
                    this.avatarDrawable.setSmallSize(true);
                    this.avatarDrawable.setAvatarType(1);
                    BackupImageView backupImageView = this.avatarImageView;
                    if (backupImageView != null) {
                        backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) currentUser);
                        return;
                    }
                    return;
                }
                this.avatarDrawable.setSmallSize(false);
                BackupImageView backupImageView2 = this.avatarImageView;
                if (backupImageView2 != null) {
                    backupImageView2.setImage(ImageLocation.getForUser(currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) currentUser);
                }
            } else if (currentChat != null) {
                this.avatarDrawable.setInfo(currentChat);
                BackupImageView backupImageView3 = this.avatarImageView;
                if (backupImageView3 != null) {
                    backupImageView3.setImage(ImageLocation.getForChat(currentChat, false), "50_50", (Drawable) this.avatarDrawable, (Object) currentChat);
                }
            }
        }
    }

    public void updateOnlineCount() {
        TLRPC$UserStatus tLRPC$UserStatus;
        boolean z;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            this.onlineCount = 0;
            TLRPC$ChatFull currentChatInfo = chatActivity.getCurrentChatInfo();
            if (currentChatInfo != null) {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if ((currentChatInfo instanceof TLRPC$TL_chatFull) || (z && currentChatInfo.participants_count <= 200 && currentChatInfo.participants != null)) {
                    for (int i = 0; i < currentChatInfo.participants.participants.size(); i++) {
                        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(currentChatInfo.participants.participants.get(i).user_id));
                        if (!(user == null || (tLRPC$UserStatus = user.status) == null || ((tLRPC$UserStatus.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                            this.onlineCount++;
                        }
                    }
                } else if (((z = currentChatInfo instanceof TLRPC$TL_channelFull)) && currentChatInfo.participants_count > 200) {
                    this.onlineCount = currentChatInfo.online_count;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            updateCurrentConnectionState();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int connectionState;
        if (i == NotificationCenter.didUpdateConnectionState && this.currentConnectionState != (connectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState())) {
            this.currentConnectionState = connectionState;
            updateCurrentConnectionState();
        }
    }

    private void updateCurrentConnectionState() {
        String str;
        int i = this.currentConnectionState;
        if (i == 2) {
            str = LocaleController.getString("WaitingForNetwork", NUM);
        } else if (i == 1) {
            str = LocaleController.getString("Connecting", NUM);
        } else if (i == 5) {
            str = LocaleController.getString("Updating", NUM);
        } else {
            str = i == 4 ? LocaleController.getString("ConnectingToProxy", NUM) : null;
        }
        if (str == null) {
            CharSequence charSequence = this.lastSubtitle;
            if (charSequence != null) {
                this.subtitleTextView.setText(charSequence);
                this.lastSubtitle = null;
                String str2 = this.lastSubtitleColorKey;
                if (str2 != null) {
                    this.subtitleTextView.setTextColor(Theme.getColor(str2));
                    this.subtitleTextView.setTag(this.lastSubtitleColorKey);
                    return;
                }
                return;
            }
            return;
        }
        if (this.lastSubtitle == null) {
            this.lastSubtitle = this.subtitleTextView.getText();
        }
        this.subtitleTextView.setText(str);
        this.subtitleTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
        this.subtitleTextView.setTag("actionBarDefaultSubtitle");
    }
}
