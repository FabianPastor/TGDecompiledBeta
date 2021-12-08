package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ClearHistoryAlert;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.ProfileActivity;

public class ChatAvatarContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount;
    private int currentConnectionState;
    StatusDrawable currentTypingDrawable;
    private boolean[] isOnline;
    private CharSequence lastSubtitle;
    private String lastSubtitleColorKey;
    private int leftPadding;
    private boolean occupyStatusBar;
    private int onlineCount;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean secretChatTimer;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private StatusDrawable[] statusDrawables;
    /* access modifiers changed from: private */
    public SimpleTextView subtitleTextView;
    /* access modifiers changed from: private */
    public ImageView timeItem;
    private TimerDrawable timerDrawable;
    /* access modifiers changed from: private */
    public AnimatorSet titleAnimation;
    private SimpleTextView titleTextView;

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean needTime) {
        this(context, chatActivity, needTime, (Theme.ResourcesProvider) null);
    }

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean needTime, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.statusDrawables = new StatusDrawable[6];
        this.avatarDrawable = new AvatarDrawable();
        this.currentAccount = UserConfig.selectedAccount;
        this.occupyStatusBar = true;
        this.leftPadding = AndroidUtilities.dp(8.0f);
        this.isOnline = new boolean[1];
        this.onlineCount = -1;
        this.resourcesProvider = resourcesProvider2;
        this.parentFragment = chatActivity;
        final boolean avatarClickable = chatActivity != null && chatActivity.getChatMode() == 0 && !UserObject.isReplyUser(this.parentFragment.getCurrentUser());
        this.avatarImageView = new BackupImageView(context) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (!avatarClickable || !getImageReceiver().hasNotThumb()) {
                    info.setVisibleToUser(false);
                    return;
                }
                info.setText(LocaleController.getString("AccDescrProfilePicture", NUM));
                if (Build.VERSION.SDK_INT >= 21) {
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("Open", NUM)));
                }
            }
        };
        if (this.parentFragment != null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(chatActivity);
            if (this.parentFragment.isThreadChat() || this.parentFragment.getChatMode() == 2) {
                this.avatarImageView.setVisibility(8);
            }
        }
        this.avatarImageView.setContentDescription(LocaleController.getString("AccDescrProfilePicture", NUM));
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        addView(this.avatarImageView);
        if (avatarClickable) {
            this.avatarImageView.setOnClickListener(new ChatAvatarContainer$$ExternalSyntheticLambda0(this));
        }
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.titleTextView = simpleTextView;
        simpleTextView.setTextColor(getThemedColor("actionBarDefaultTitle"));
        this.titleTextView.setTextSize(18);
        this.titleTextView.setGravity(3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
        addView(this.titleTextView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.subtitleTextView = simpleTextView2;
        simpleTextView2.setTextColor(getThemedColor("actionBarDefaultSubtitle"));
        this.subtitleTextView.setTag("actionBarDefaultSubtitle");
        this.subtitleTextView.setTextSize(14);
        this.subtitleTextView.setGravity(3);
        addView(this.subtitleTextView);
        if (this.parentFragment != null) {
            ImageView imageView = new ImageView(context);
            this.timeItem = imageView;
            imageView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
            this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
            this.timeItem.setAlpha(0.0f);
            this.timeItem.setScaleY(0.0f);
            this.timeItem.setScaleX(0.0f);
            this.timeItem.setVisibility(8);
            ImageView imageView2 = this.timeItem;
            TimerDrawable timerDrawable2 = new TimerDrawable(context);
            this.timerDrawable = timerDrawable2;
            imageView2.setImageDrawable(timerDrawable2);
            addView(this.timeItem);
            this.secretChatTimer = needTime;
            this.timeItem.setOnClickListener(new ChatAvatarContainer$$ExternalSyntheticLambda2(this, resourcesProvider2));
            if (this.secretChatTimer) {
                this.timeItem.setContentDescription(LocaleController.getString("SetTimer", NUM));
            } else {
                this.timeItem.setContentDescription(LocaleController.getString("AccAutoDeleteTimer", NUM));
            }
        }
        ChatActivity chatActivity2 = this.parentFragment;
        if (chatActivity2 != null && chatActivity2.getChatMode() == 0) {
            if (!this.parentFragment.isThreadChat() && !UserObject.isReplyUser(this.parentFragment.getCurrentUser())) {
                setOnClickListener(new ChatAvatarContainer$$ExternalSyntheticLambda1(this));
            }
            TLRPC.Chat chat = this.parentFragment.getCurrentChat();
            this.statusDrawables[0] = new TypingDotsDrawable(true);
            this.statusDrawables[1] = new RecordStatusDrawable(true);
            this.statusDrawables[2] = new SendingFileDrawable(true);
            this.statusDrawables[3] = new PlayingGameDrawable(false, resourcesProvider2);
            this.statusDrawables[4] = new RoundStatusDrawable(true);
            this.statusDrawables[5] = new ChoosingStickerStatusDrawable(true);
            int a = 0;
            while (true) {
                StatusDrawable[] statusDrawableArr = this.statusDrawables;
                if (a < statusDrawableArr.length) {
                    statusDrawableArr[a].setIsChat(chat != null);
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAvatarContainer  reason: not valid java name */
    public /* synthetic */ void m2195lambda$new$0$orgtelegramuiComponentsChatAvatarContainer(View v) {
        openProfile(true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAvatarContainer  reason: not valid java name */
    public /* synthetic */ void m2196lambda$new$1$orgtelegramuiComponentsChatAvatarContainer(Theme.ResourcesProvider resourcesProvider2, View v) {
        if (this.secretChatTimer) {
            this.parentFragment.showDialog(AlertsCreator.createTTLAlert(getContext(), this.parentFragment.getCurrentEncryptedChat(), resourcesProvider2).create());
        } else {
            openSetTimer();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAvatarContainer  reason: not valid java name */
    public /* synthetic */ void m2197lambda$new$2$orgtelegramuiComponentsChatAvatarContainer(View v) {
        openProfile(false);
    }

    public boolean openSetTimer() {
        if (this.parentFragment.getParentActivity() == null) {
            return false;
        }
        TLRPC.Chat chat = this.parentFragment.getCurrentChat();
        if (chat == null || ChatObject.canUserDoAdminAction(chat, 13)) {
            ClearHistoryAlert clearHistoryAlert = new ClearHistoryAlert(this.parentFragment.getParentActivity(), this.parentFragment.getCurrentUser(), this.parentFragment.getCurrentChat(), false, (Theme.ResourcesProvider) null);
            clearHistoryAlert.setDelegate(new ClearHistoryAlert.ClearHistoryAlertDelegate() {
                public /* synthetic */ void onClearHistory(boolean z) {
                    ClearHistoryAlert.ClearHistoryAlertDelegate.CC.$default$onClearHistory(this, z);
                }

                public void onAutoDeleteHistory(int ttl, int action) {
                    ChatAvatarContainer.this.parentFragment.getMessagesController().setDialogHistoryTTL(ChatAvatarContainer.this.parentFragment.getDialogId(), ttl);
                    TLRPC.ChatFull chatInfo = ChatAvatarContainer.this.parentFragment.getCurrentChatInfo();
                    TLRPC.UserFull userInfo = ChatAvatarContainer.this.parentFragment.getCurrentUserInfo();
                    if (userInfo != null || chatInfo != null) {
                        ChatAvatarContainer.this.parentFragment.getUndoView().showWithAction(ChatAvatarContainer.this.parentFragment.getDialogId(), action, (Object) ChatAvatarContainer.this.parentFragment.getCurrentUser(), (Object) Integer.valueOf(userInfo != null ? userInfo.ttl_period : chatInfo.ttl_period), (Runnable) null, (Runnable) null);
                    }
                }
            });
            this.parentFragment.showDialog(clearHistoryAlert);
            return true;
        }
        if (this.timeItem.getTag() != null) {
            this.parentFragment.showTimerHint();
        }
        return false;
    }

    private void openProfile(boolean byAvatar) {
        if (byAvatar && (AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y || !this.avatarImageView.getImageReceiver().hasNotThumb())) {
            byAvatar = false;
        }
        TLRPC.User user = this.parentFragment.getCurrentUser();
        TLRPC.Chat chat = this.parentFragment.getCurrentChat();
        ImageReceiver imageReceiver = this.avatarImageView.getImageReceiver();
        String key = imageReceiver.getImageKey();
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (key != null && !imageLoader.isInMemCache(key, false)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                imageLoader.putImageToCache((BitmapDrawable) drawable, key, false);
            }
        }
        int i = 2;
        if (user != null) {
            Bundle args = new Bundle();
            if (UserObject.isUserSelf(user)) {
                args.putLong("dialog_id", this.parentFragment.getDialogId());
                int[] media = new int[8];
                System.arraycopy(this.sharedMediaPreloader.getLastMediaCount(), 0, media, 0, media.length);
                MediaActivity fragment = new MediaActivity(args, this.sharedMediaPreloader);
                fragment.setChatInfo(this.parentFragment.getCurrentChatInfo());
                this.parentFragment.presentFragment(fragment);
                return;
            }
            args.putLong("user_id", user.id);
            args.putBoolean("reportSpam", this.parentFragment.hasReportSpam());
            if (this.timeItem != null) {
                args.putLong("dialog_id", this.parentFragment.getDialogId());
            }
            args.putInt("actionBarColor", getThemedColor("actionBarDefault"));
            ProfileActivity fragment2 = new ProfileActivity(args, this.sharedMediaPreloader);
            fragment2.setUserInfo(this.parentFragment.getCurrentUserInfo());
            if (!byAvatar) {
                i = 1;
            }
            fragment2.setPlayProfileAnimation(i);
            this.parentFragment.presentFragment(fragment2);
        } else if (chat != null) {
            Bundle args2 = new Bundle();
            args2.putLong("chat_id", chat.id);
            ProfileActivity fragment3 = new ProfileActivity(args2, this.sharedMediaPreloader);
            fragment3.setChatInfo(this.parentFragment.getCurrentChatInfo());
            if (!byAvatar) {
                i = 1;
            }
            fragment3.setPlayProfileAnimation(i);
            this.parentFragment.presentFragment(fragment3);
        }
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
    }

    public void setTitleColors(int title, int subtitle) {
        this.titleTextView.setTextColor(title);
        this.subtitleTextView.setTextColor(subtitle);
        this.subtitleTextView.setTag(Integer.valueOf(subtitle));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - AndroidUtilities.dp((float) ((this.avatarImageView.getVisibility() == 0 ? 54 : 0) + 16));
        this.avatarImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
        this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
        this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), NUM));
        }
        setMeasuredDimension(width, View.MeasureSpec.getSize(heightMeasureSpec));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i = 0;
        int viewTop = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0f)) / 2) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
        BackupImageView backupImageView = this.avatarImageView;
        int i2 = this.leftPadding;
        backupImageView.layout(i2, viewTop, AndroidUtilities.dp(42.0f) + i2, AndroidUtilities.dp(42.0f) + viewTop);
        int i3 = this.leftPadding;
        if (this.avatarImageView.getVisibility() == 0) {
            i = AndroidUtilities.dp(54.0f);
        }
        int l = i3 + i;
        if (this.subtitleTextView.getVisibility() != 8) {
            this.titleTextView.layout(l, AndroidUtilities.dp(1.3f) + viewTop, this.titleTextView.getMeasuredWidth() + l, this.titleTextView.getTextHeight() + viewTop + AndroidUtilities.dp(1.3f));
        } else {
            this.titleTextView.layout(l, AndroidUtilities.dp(11.0f) + viewTop, this.titleTextView.getMeasuredWidth() + l, this.titleTextView.getTextHeight() + viewTop + AndroidUtilities.dp(11.0f));
        }
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.layout(this.leftPadding + AndroidUtilities.dp(16.0f), AndroidUtilities.dp(15.0f) + viewTop, this.leftPadding + AndroidUtilities.dp(50.0f), AndroidUtilities.dp(49.0f) + viewTop);
        }
        this.subtitleTextView.layout(l, AndroidUtilities.dp(24.0f) + viewTop, this.subtitleTextView.getMeasuredWidth() + l, this.subtitleTextView.getTextHeight() + viewTop + AndroidUtilities.dp(24.0f));
    }

    public void setLeftPadding(int value) {
        this.leftPadding = value;
    }

    public void showTimeItem(boolean animated) {
        ImageView imageView = this.timeItem;
        if (imageView != null && imageView.getTag() == null) {
            this.timeItem.clearAnimation();
            this.timeItem.setVisibility(0);
            this.timeItem.setTag(1);
            if (animated) {
                this.timeItem.animate().setDuration(180).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setListener((Animator.AnimatorListener) null).start();
                return;
            }
            this.timeItem.setAlpha(1.0f);
            this.timeItem.setScaleY(1.0f);
            this.timeItem.setScaleX(1.0f);
        }
    }

    public void hideTimeItem(boolean animated) {
        ImageView imageView = this.timeItem;
        if (imageView != null && imageView.getTag() != null) {
            this.timeItem.clearAnimation();
            this.timeItem.setTag((Object) null);
            if (animated) {
                this.timeItem.animate().setDuration(180).alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ChatAvatarContainer.this.timeItem.setVisibility(8);
                        super.onAnimationEnd(animation);
                    }
                }).start();
                return;
            }
            this.timeItem.setVisibility(8);
            this.timeItem.setAlpha(0.0f);
            this.timeItem.setScaleY(0.0f);
            this.timeItem.setScaleX(0.0f);
        }
    }

    public void setTime(int value) {
        TimerDrawable timerDrawable2 = this.timerDrawable;
        if (timerDrawable2 != null) {
            if (value != 0 || this.secretChatTimer) {
                timerDrawable2.setTime(value);
            }
        }
    }

    public void setTitleIcons(Drawable leftIcon, Drawable rightIcon) {
        this.titleTextView.setLeftDrawable(leftIcon);
        if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
            this.titleTextView.setRightDrawable(rightIcon);
        }
    }

    public void setTitle(CharSequence value) {
        setTitle(value, false, false);
    }

    public void setTitle(CharSequence value, boolean scam, boolean fake) {
        this.titleTextView.setText(value);
        if (scam || fake) {
            if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
                ScamDrawable drawable = new ScamDrawable(11, scam ^ true ? 1 : 0);
                drawable.setColor(getThemedColor("actionBarDefaultSubtitle"));
                this.titleTextView.setRightDrawable((Drawable) drawable);
            }
        } else if (this.titleTextView.getRightDrawable() instanceof ScamDrawable) {
            this.titleTextView.setRightDrawable((Drawable) null);
        }
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

    public void onDestroy() {
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
        if (sharedMediaPreloader2 != null) {
            sharedMediaPreloader2.onDestroy(this.parentFragment);
        }
    }

    private void setTypingAnimation(boolean start) {
        if (start) {
            try {
                int type = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.parentFragment.getDialogId(), this.parentFragment.getThreadId()).intValue();
                if (type == 5) {
                    this.subtitleTextView.replaceTextWithDrawable(this.statusDrawables[type], "**oo**");
                    this.statusDrawables[type].setColor(getThemedColor("chat_status"));
                    this.subtitleTextView.setLeftDrawable((Drawable) null);
                } else {
                    this.subtitleTextView.replaceTextWithDrawable((Drawable) null, (String) null);
                    this.statusDrawables[type].setColor(getThemedColor("chat_status"));
                    this.subtitleTextView.setLeftDrawable((Drawable) this.statusDrawables[type]);
                }
                this.currentTypingDrawable = this.statusDrawables[type];
                int a = 0;
                while (true) {
                    StatusDrawable[] statusDrawableArr = this.statusDrawables;
                    if (a < statusDrawableArr.length) {
                        if (a == type) {
                            statusDrawableArr[a].start();
                        } else {
                            statusDrawableArr[a].stop();
                        }
                        a++;
                    } else {
                        return;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            this.currentTypingDrawable = null;
            this.subtitleTextView.setLeftDrawable((Drawable) null);
            this.subtitleTextView.replaceTextWithDrawable((Drawable) null, (String) null);
            int a2 = 0;
            while (true) {
                StatusDrawable[] statusDrawableArr2 = this.statusDrawables;
                if (a2 < statusDrawableArr2.length) {
                    statusDrawableArr2[a2].stop();
                    a2++;
                } else {
                    return;
                }
            }
        }
    }

    public void updateSubtitle() {
        updateSubtitle(false);
    }

    public void updateSubtitle(boolean animated) {
        CharSequence newSubtitle;
        CharSequence newSubtitle2;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC.User user = chatActivity.getCurrentUser();
            if (!UserObject.isUserSelf(user) && !UserObject.isReplyUser(user) && this.parentFragment.getChatMode() == 0) {
                TLRPC.Chat chat = this.parentFragment.getCurrentChat();
                CharSequence printString = MessagesController.getInstance(this.currentAccount).getPrintingString(this.parentFragment.getDialogId(), this.parentFragment.getThreadId(), false);
                if (printString != null) {
                    printString = TextUtils.replace(printString, new String[]{"..."}, new String[]{""});
                }
                boolean useOnlineColor = false;
                if (printString != null && printString.length() != 0 && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                    if (this.parentFragment.isThreadChat() && this.titleTextView.getTag() != null) {
                        this.titleTextView.setTag((Object) null);
                        this.subtitleTextView.setVisibility(0);
                        AnimatorSet animatorSet = this.titleAnimation;
                        if (animatorSet != null) {
                            animatorSet.cancel();
                            this.titleAnimation = null;
                        }
                        if (animated) {
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            this.titleAnimation = animatorSet2;
                            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.titleTextView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.subtitleTextView, View.ALPHA, new float[]{1.0f})});
                            this.titleAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    AnimatorSet unused = ChatAvatarContainer.this.titleAnimation = null;
                                }
                            });
                            this.titleAnimation.setDuration(180);
                            this.titleAnimation.start();
                        } else {
                            this.titleTextView.setTranslationY(0.0f);
                            this.subtitleTextView.setAlpha(1.0f);
                        }
                    }
                    newSubtitle = printString;
                    if (MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.parentFragment.getDialogId(), this.parentFragment.getThreadId()).intValue() == 5) {
                        newSubtitle = Emoji.replaceEmoji(newSubtitle, this.subtitleTextView.getTextPaint().getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    }
                    useOnlineColor = true;
                    setTypingAnimation(true);
                } else if (!this.parentFragment.isThreadChat()) {
                    setTypingAnimation(false);
                    if (chat != null) {
                        TLRPC.ChatFull info = this.parentFragment.getCurrentChatInfo();
                        if (ChatObject.isChannel(chat)) {
                            if (info == null || info.participants_count == 0) {
                                if (chat.megagroup) {
                                    if (info == null) {
                                        newSubtitle = LocaleController.getString("Loading", NUM).toLowerCase();
                                    } else if (chat.has_geo) {
                                        newSubtitle = LocaleController.getString("MegaLocation", NUM).toLowerCase();
                                    } else if (!TextUtils.isEmpty(chat.username)) {
                                        newSubtitle = LocaleController.getString("MegaPublic", NUM).toLowerCase();
                                    } else {
                                        newSubtitle = LocaleController.getString("MegaPrivate", NUM).toLowerCase();
                                    }
                                } else if ((chat.flags & 64) != 0) {
                                    newSubtitle = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                                } else {
                                    newSubtitle = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                                }
                            } else if (!chat.megagroup) {
                                int[] result = new int[1];
                                String shortNumber = LocaleController.formatShortNumber(info.participants_count, result);
                                if (chat.megagroup) {
                                    newSubtitle2 = LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                                } else {
                                    newSubtitle2 = LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                                }
                                newSubtitle = newSubtitle2;
                            } else if (this.onlineCount > 1) {
                                newSubtitle = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", info.participants_count), LocaleController.formatPluralString("OnlineCount", Math.min(this.onlineCount, info.participants_count))});
                            } else {
                                newSubtitle = LocaleController.formatPluralString("Members", info.participants_count);
                            }
                        } else if (ChatObject.isKickedFromChat(chat)) {
                            newSubtitle = LocaleController.getString("YouWereKicked", NUM);
                        } else if (ChatObject.isLeftFromChat(chat)) {
                            newSubtitle = LocaleController.getString("YouLeft", NUM);
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
                        TLRPC.User newUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(user.id));
                        if (newUser != null) {
                            user = newUser;
                        }
                        if (UserObject.isReplyUser(user)) {
                            newSubtitle = "";
                        } else if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            newSubtitle = LocaleController.getString("ChatYourSelf", NUM);
                        } else if (user.id == 333000 || user.id == 777000 || user.id == 42777) {
                            newSubtitle = LocaleController.getString("ServiceNotifications", NUM);
                        } else if (MessagesController.isSupportUser(user)) {
                            newSubtitle = LocaleController.getString("SupportStatus", NUM);
                        } else if (user.bot) {
                            newSubtitle = LocaleController.getString("Bot", NUM);
                        } else {
                            boolean[] zArr = this.isOnline;
                            zArr[0] = false;
                            newSubtitle = LocaleController.formatUserStatus(this.currentAccount, user, zArr);
                            useOnlineColor = this.isOnline[0];
                        }
                    } else {
                        newSubtitle = "";
                    }
                } else if (this.titleTextView.getTag() == null) {
                    this.titleTextView.setTag(1);
                    AnimatorSet animatorSet3 = this.titleAnimation;
                    if (animatorSet3 != null) {
                        animatorSet3.cancel();
                        this.titleAnimation = null;
                    }
                    if (animated) {
                        AnimatorSet animatorSet4 = new AnimatorSet();
                        this.titleAnimation = animatorSet4;
                        animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.titleTextView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(9.7f)}), ObjectAnimator.ofFloat(this.subtitleTextView, View.ALPHA, new float[]{0.0f})});
                        this.titleAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationCancel(Animator animation) {
                                AnimatorSet unused = ChatAvatarContainer.this.titleAnimation = null;
                            }

                            public void onAnimationEnd(Animator animation) {
                                if (ChatAvatarContainer.this.titleAnimation == animation) {
                                    ChatAvatarContainer.this.subtitleTextView.setVisibility(4);
                                    AnimatorSet unused = ChatAvatarContainer.this.titleAnimation = null;
                                }
                            }
                        });
                        this.titleAnimation.setDuration(180);
                        this.titleAnimation.start();
                        return;
                    }
                    this.titleTextView.setTranslationY((float) AndroidUtilities.dp(9.7f));
                    this.subtitleTextView.setAlpha(0.0f);
                    this.subtitleTextView.setVisibility(4);
                    return;
                } else {
                    return;
                }
                this.lastSubtitleColorKey = useOnlineColor ? "chat_status" : "actionBarDefaultSubtitle";
                if (this.lastSubtitle == null) {
                    this.subtitleTextView.setText(newSubtitle);
                    this.subtitleTextView.setTextColor(getThemedColor(this.lastSubtitleColorKey));
                    this.subtitleTextView.setTag(this.lastSubtitleColorKey);
                    return;
                }
                this.lastSubtitle = newSubtitle;
            } else if (this.subtitleTextView.getVisibility() != 8) {
                this.subtitleTextView.setVisibility(8);
            }
        }
    }

    public void setChatAvatar(TLRPC.Chat chat) {
        this.avatarDrawable.setInfo(chat);
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setForUserOrChat(chat, this.avatarDrawable);
        }
    }

    public void setUserAvatar(TLRPC.User user) {
        setUserAvatar(user, false);
    }

    public void setUserAvatar(TLRPC.User user, boolean showSelf) {
        this.avatarDrawable.setInfo(user);
        if (UserObject.isReplyUser(user)) {
            this.avatarDrawable.setAvatarType(12);
            this.avatarDrawable.setSmallSize(true);
            BackupImageView backupImageView = this.avatarImageView;
            if (backupImageView != null) {
                backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) user);
            }
        } else if (!UserObject.isUserSelf(user) || showSelf) {
            this.avatarDrawable.setSmallSize(false);
            BackupImageView backupImageView2 = this.avatarImageView;
            if (backupImageView2 != null) {
                backupImageView2.setForUserOrChat(user, this.avatarDrawable);
            }
        } else {
            this.avatarDrawable.setAvatarType(1);
            this.avatarDrawable.setSmallSize(true);
            BackupImageView backupImageView3 = this.avatarImageView;
            if (backupImageView3 != null) {
                backupImageView3.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) user);
            }
        }
    }

    public void checkAndUpdateAvatar() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC.User user = chatActivity.getCurrentUser();
            TLRPC.Chat chat = this.parentFragment.getCurrentChat();
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                if (UserObject.isReplyUser(user)) {
                    this.avatarDrawable.setSmallSize(true);
                    this.avatarDrawable.setAvatarType(12);
                    BackupImageView backupImageView = this.avatarImageView;
                    if (backupImageView != null) {
                        backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) user);
                    }
                } else if (UserObject.isUserSelf(user)) {
                    this.avatarDrawable.setSmallSize(true);
                    this.avatarDrawable.setAvatarType(1);
                    BackupImageView backupImageView2 = this.avatarImageView;
                    if (backupImageView2 != null) {
                        backupImageView2.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) user);
                    }
                } else {
                    this.avatarDrawable.setSmallSize(false);
                    BackupImageView backupImageView3 = this.avatarImageView;
                    if (backupImageView3 != null) {
                        backupImageView3.setForUserOrChat(user, this.avatarDrawable);
                    }
                }
            } else if (chat != null) {
                this.avatarDrawable.setInfo(chat);
                BackupImageView backupImageView4 = this.avatarImageView;
                if (backupImageView4 != null) {
                    backupImageView4.setForUserOrChat(chat, this.avatarDrawable);
                }
            }
        }
    }

    public void updateOnlineCount() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            this.onlineCount = 0;
            TLRPC.ChatFull info = chatActivity.getCurrentChatInfo();
            if (info != null) {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if ((info instanceof TLRPC.TL_chatFull) || ((info instanceof TLRPC.TL_channelFull) && info.participants_count <= 200 && info.participants != null)) {
                    for (int a = 0; a < info.participants.participants.size(); a++) {
                        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(info.participants.participants.get(a).user_id));
                        if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                            this.onlineCount++;
                        }
                    }
                } else if ((info instanceof TLRPC.TL_channelFull) && info.participants_count > 200) {
                    this.onlineCount = info.online_count;
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

    public void didReceivedNotification(int id, int account, Object... args) {
        int state;
        if (id == NotificationCenter.didUpdateConnectionState && this.currentConnectionState != (state = ConnectionsManager.getInstance(this.currentAccount).getConnectionState())) {
            this.currentConnectionState = state;
            updateCurrentConnectionState();
        }
    }

    private void updateCurrentConnectionState() {
        String title = null;
        int i = this.currentConnectionState;
        if (i == 2) {
            title = LocaleController.getString("WaitingForNetwork", NUM);
        } else if (i == 1) {
            title = LocaleController.getString("Connecting", NUM);
        } else if (i == 5) {
            title = LocaleController.getString("Updating", NUM);
        } else if (i == 4) {
            title = LocaleController.getString("ConnectingToProxy", NUM);
        }
        if (title == null) {
            CharSequence charSequence = this.lastSubtitle;
            if (charSequence != null) {
                this.subtitleTextView.setText(charSequence);
                this.lastSubtitle = null;
                String str = this.lastSubtitleColorKey;
                if (str != null) {
                    this.subtitleTextView.setTextColor(getThemedColor(str));
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
        this.subtitleTextView.setText(title);
        this.subtitleTextView.setTextColor(getThemedColor("actionBarDefaultSubtitle"));
        this.subtitleTextView.setTag("actionBarDefaultSubtitle");
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (info.isClickable() && Build.VERSION.SDK_INT >= 21) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("OpenProfile", NUM)));
        }
    }

    public SharedMediaLayout.SharedMediaPreloader getSharedMediaPreloader() {
        return this.sharedMediaPreloader;
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void updateColors() {
        StatusDrawable statusDrawable = this.currentTypingDrawable;
        if (statusDrawable != null) {
            statusDrawable.setColor(getThemedColor("chat_status"));
        }
    }
}
