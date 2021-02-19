package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
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
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ClearHistoryAlert;
import org.telegram.ui.Components.SharedMediaLayout;

public class ChatAvatarContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentConnectionState;
    private boolean[] isOnline = new boolean[1];
    private CharSequence lastSubtitle;
    private String lastSubtitleColorKey;
    private int leftPadding = AndroidUtilities.dp(8.0f);
    private boolean occupyStatusBar = true;
    private int onlineCount = -1;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    private boolean secretChatTimer;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    /* access modifiers changed from: private */
    public SimpleTextView subtitleTextView;
    /* access modifiers changed from: private */
    public ImageView timeItem;
    private TimerDrawable timerDrawable;
    /* access modifiers changed from: private */
    public AnimatorSet titleAnimation;
    private SimpleTextView titleTextView;

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean z) {
        super(context);
        this.parentFragment = chatActivity;
        final boolean z2 = chatActivity != null && chatActivity.getChatMode() == 0 && !UserObject.isReplyUser(this.parentFragment.getCurrentUser());
        this.avatarImageView = new BackupImageView(this, context) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (!z2 || !getImageReceiver().hasNotThumb()) {
                    accessibilityNodeInfo.setVisibleToUser(false);
                    return;
                }
                accessibilityNodeInfo.setText(LocaleController.getString("AccDescrProfilePicture", NUM));
                if (Build.VERSION.SDK_INT >= 21) {
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("Open", NUM)));
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
        if (z2) {
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
            this.secretChatTimer = z;
            this.timeItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatAvatarContainer.this.lambda$new$1$ChatAvatarContainer(view);
                }
            });
            if (this.secretChatTimer) {
                this.timeItem.setContentDescription(LocaleController.getString("SetTimer", NUM));
            } else {
                this.timeItem.setContentDescription(LocaleController.getString("AccAutoDeleteTimer", NUM));
            }
        }
        ChatActivity chatActivity2 = this.parentFragment;
        if (chatActivity2 != null && chatActivity2.getChatMode() == 0) {
            if (!this.parentFragment.isThreadChat() && !UserObject.isReplyUser(this.parentFragment.getCurrentUser())) {
                setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChatAvatarContainer.this.lambda$new$2$ChatAvatarContainer(view);
                    }
                });
            }
            TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
            this.statusDrawables[0] = new TypingDotsDrawable(false);
            this.statusDrawables[1] = new RecordStatusDrawable(false);
            this.statusDrawables[2] = new SendingFileDrawable(false);
            this.statusDrawables[3] = new PlayingGameDrawable(false);
            this.statusDrawables[4] = new RoundStatusDrawable(false);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ChatAvatarContainer(View view) {
        openProfile(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ChatAvatarContainer(View view) {
        if (this.secretChatTimer) {
            this.parentFragment.showDialog(AlertsCreator.createTTLAlert(getContext(), this.parentFragment.getCurrentEncryptedChat()).create());
        } else {
            openSetTimer();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$ChatAvatarContainer(View view) {
        openProfile(false);
    }

    public boolean openSetTimer() {
        if (this.parentFragment.getParentActivity() == null) {
            return false;
        }
        TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
        if (currentChat == null || ChatObject.canUserDoAdminAction(currentChat, 13)) {
            ClearHistoryAlert clearHistoryAlert = new ClearHistoryAlert(this.parentFragment.getParentActivity(), this.parentFragment.getCurrentUser(), this.parentFragment.getCurrentChat(), false);
            clearHistoryAlert.setDelegate(new ClearHistoryAlert.ClearHistoryAlertDelegate() {
                public /* synthetic */ void onClearHistory(boolean z) {
                    ClearHistoryAlert.ClearHistoryAlertDelegate.CC.$default$onClearHistory(this, z);
                }

                public void onAutoDeleteHistory(int i, int i2) {
                    ChatAvatarContainer.this.parentFragment.getMessagesController().setDialogHistoryTTL(ChatAvatarContainer.this.parentFragment.getDialogId(), i);
                    TLRPC$ChatFull currentChatInfo = ChatAvatarContainer.this.parentFragment.getCurrentChatInfo();
                    TLRPC$UserFull currentUserInfo = ChatAvatarContainer.this.parentFragment.getCurrentUserInfo();
                    if (currentUserInfo != null || currentChatInfo != null) {
                        ChatAvatarContainer.this.parentFragment.getUndoView().showWithAction(ChatAvatarContainer.this.parentFragment.getDialogId(), i2, ChatAvatarContainer.this.parentFragment.getCurrentUser(), Integer.valueOf(currentUserInfo != null ? currentUserInfo.ttl_period : currentChatInfo.ttl_period), (Runnable) null, (Runnable) null);
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
            org.telegram.ui.Components.BackupImageView r3 = r7.avatarImageView
            org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            java.lang.String r4 = r3.getImageKey()
            org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.getInstance()
            if (r4 == 0) goto L_0x004d
            boolean r6 = r5.isInMemCache(r4, r0)
            if (r6 != 0) goto L_0x004d
            android.graphics.drawable.Drawable r3 = r3.getDrawable()
            boolean r6 = r3 instanceof android.graphics.drawable.BitmapDrawable
            if (r6 == 0) goto L_0x004d
            android.graphics.drawable.BitmapDrawable r3 = (android.graphics.drawable.BitmapDrawable) r3
            r5.putImageToCache(r3, r4)
        L_0x004d:
            r3 = 2
            r4 = 1
            if (r1 == 0) goto L_0x00ca
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            boolean r5 = org.telegram.messenger.UserObject.isUserSelf(r1)
            java.lang.String r6 = "dialog_id"
            if (r5 == 0) goto L_0x008e
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
            goto L_0x00f4
        L_0x008e:
            int r0 = r1.id
            java.lang.String r1 = "user_id"
            r2.putInt(r1, r0)
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            boolean r0 = r0.hasReportSpam()
            java.lang.String r1 = "reportSpam"
            r2.putBoolean(r1, r0)
            android.widget.ImageView r0 = r7.timeItem
            if (r0 == 0) goto L_0x00ad
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            long r0 = r0.getDialogId()
            r2.putLong(r6, r0)
        L_0x00ad:
            org.telegram.ui.ProfileActivity r0 = new org.telegram.ui.ProfileActivity
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r1 = r7.sharedMediaPreloader
            r0.<init>(r2, r1)
            org.telegram.ui.ChatActivity r1 = r7.parentFragment
            org.telegram.tgnet.TLRPC$UserFull r1 = r1.getCurrentUserInfo()
            r0.setUserInfo(r1)
            if (r8 == 0) goto L_0x00c0
            goto L_0x00c1
        L_0x00c0:
            r3 = 1
        L_0x00c1:
            r0.setPlayProfileAnimation(r3)
            org.telegram.ui.ChatActivity r8 = r7.parentFragment
            r8.presentFragment(r0)
            goto L_0x00f4
        L_0x00ca:
            if (r2 == 0) goto L_0x00f4
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
            if (r8 == 0) goto L_0x00eb
            goto L_0x00ec
        L_0x00eb:
            r3 = 1
        L_0x00ec:
            r1.setPlayProfileAnimation(r3)
            org.telegram.ui.ChatActivity r8 = r7.parentFragment
            r8.presentFragment(r1)
        L_0x00f4:
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
        int dp = size - AndroidUtilities.dp((float) ((this.avatarImageView.getVisibility() == 0 ? 54 : 0) + 16));
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
        int i5 = 0;
        int currentActionBarHeight = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0f)) / 2) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
        BackupImageView backupImageView = this.avatarImageView;
        int i6 = this.leftPadding;
        backupImageView.layout(i6, currentActionBarHeight, AndroidUtilities.dp(42.0f) + i6, AndroidUtilities.dp(42.0f) + currentActionBarHeight);
        int i7 = this.leftPadding;
        if (this.avatarImageView.getVisibility() == 0) {
            i5 = AndroidUtilities.dp(54.0f);
        }
        int i8 = i7 + i5;
        if (this.subtitleTextView.getVisibility() != 8) {
            this.titleTextView.layout(i8, AndroidUtilities.dp(1.3f) + currentActionBarHeight, this.titleTextView.getMeasuredWidth() + i8, this.titleTextView.getTextHeight() + currentActionBarHeight + AndroidUtilities.dp(1.3f));
        } else {
            this.titleTextView.layout(i8, AndroidUtilities.dp(11.0f) + currentActionBarHeight, this.titleTextView.getMeasuredWidth() + i8, this.titleTextView.getTextHeight() + currentActionBarHeight + AndroidUtilities.dp(11.0f));
        }
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.layout(this.leftPadding + AndroidUtilities.dp(16.0f), AndroidUtilities.dp(15.0f) + currentActionBarHeight, this.leftPadding + AndroidUtilities.dp(50.0f), AndroidUtilities.dp(49.0f) + currentActionBarHeight);
        }
        this.subtitleTextView.layout(i8, AndroidUtilities.dp(24.0f) + currentActionBarHeight, this.subtitleTextView.getMeasuredWidth() + i8, currentActionBarHeight + this.subtitleTextView.getTextHeight() + AndroidUtilities.dp(24.0f));
    }

    public void setLeftPadding(int i) {
        this.leftPadding = i;
    }

    public void showTimeItem(boolean z) {
        ImageView imageView = this.timeItem;
        if (imageView != null && imageView.getTag() == null) {
            this.timeItem.clearAnimation();
            this.timeItem.setVisibility(0);
            this.timeItem.setTag(1);
            if (z) {
                this.timeItem.animate().setDuration(180).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setListener((Animator.AnimatorListener) null).start();
                return;
            }
            this.timeItem.setAlpha(1.0f);
            this.timeItem.setScaleY(1.0f);
            this.timeItem.setScaleX(1.0f);
        }
    }

    public void hideTimeItem(boolean z) {
        ImageView imageView = this.timeItem;
        if (imageView != null && imageView.getTag() != null) {
            this.timeItem.clearAnimation();
            this.timeItem.setTag((Object) null);
            if (z) {
                this.timeItem.animate().setDuration(180).alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatAvatarContainer.this.timeItem.setVisibility(8);
                        super.onAnimationEnd(animator);
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

    public void setTime(int i) {
        TimerDrawable timerDrawable2 = this.timerDrawable;
        if (timerDrawable2 != null) {
            if (i != 0 || this.secretChatTimer) {
                timerDrawable2.setTime(i);
            }
        }
    }

    public void setTitleIcons(Drawable drawable, Drawable drawable2) {
        this.titleTextView.setLeftDrawable(drawable);
        if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
            this.titleTextView.setRightDrawable(drawable2);
        }
    }

    public void setTitle(CharSequence charSequence) {
        setTitle(charSequence, false, false);
    }

    public void setTitle(CharSequence charSequence, boolean z, boolean z2) {
        this.titleTextView.setText(charSequence);
        if (z || z2) {
            if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
                ScamDrawable scamDrawable = new ScamDrawable(11, z ^ true ? 1 : 0);
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
                Integer printingStringType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.parentFragment.getDialogId(), this.parentFragment.getThreadId());
                this.subtitleTextView.setLeftDrawable((Drawable) this.statusDrawables[printingStringType.intValue()]);
                while (i < this.statusDrawables.length) {
                    if (i == printingStringType.intValue()) {
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
        updateSubtitle(false);
    }

    public void updateSubtitle(boolean z) {
        String string;
        TLRPC$ChatParticipants tLRPC$ChatParticipants;
        int i;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC$User currentUser = chatActivity.getCurrentUser();
            if (!UserObject.isUserSelf(currentUser) && !UserObject.isReplyUser(currentUser) && this.parentFragment.getChatMode() == 0) {
                TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
                boolean z2 = false;
                CharSequence printingString = MessagesController.getInstance(this.currentAccount).getPrintingString(this.parentFragment.getDialogId(), this.parentFragment.getThreadId(), false);
                String str = "";
                if (printingString != null) {
                    printingString = TextUtils.replace(printingString, new String[]{"..."}, new String[]{str});
                }
                if (printingString != null && printingString.length() != 0 && (!ChatObject.isChannel(currentChat) || currentChat.megagroup)) {
                    if (this.parentFragment.isThreadChat() && this.titleTextView.getTag() != null) {
                        this.titleTextView.setTag((Object) null);
                        this.subtitleTextView.setVisibility(0);
                        AnimatorSet animatorSet = this.titleAnimation;
                        if (animatorSet != null) {
                            animatorSet.cancel();
                            this.titleAnimation = null;
                        }
                        if (z) {
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            this.titleAnimation = animatorSet2;
                            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.titleTextView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.subtitleTextView, View.ALPHA, new float[]{1.0f})});
                            this.titleAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
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
                    setTypingAnimation(true);
                    str = printingString;
                    z2 = true;
                } else if (!this.parentFragment.isThreadChat()) {
                    setTypingAnimation(false);
                    if (currentChat != null) {
                        TLRPC$ChatFull currentChatInfo = this.parentFragment.getCurrentChatInfo();
                        if (ChatObject.isChannel(currentChat)) {
                            if (currentChatInfo == null || (i = currentChatInfo.participants_count) == 0) {
                                if (currentChat.megagroup) {
                                    if (currentChatInfo == null) {
                                        string = LocaleController.getString("Loading", NUM).toLowerCase();
                                    } else if (currentChat.has_geo) {
                                        string = LocaleController.getString("MegaLocation", NUM).toLowerCase();
                                    } else if (!TextUtils.isEmpty(currentChat.username)) {
                                        string = LocaleController.getString("MegaPublic", NUM).toLowerCase();
                                    } else {
                                        string = LocaleController.getString("MegaPrivate", NUM).toLowerCase();
                                    }
                                } else if ((currentChat.flags & 64) != 0) {
                                    string = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                                } else {
                                    string = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                                }
                            } else if (!currentChat.megagroup) {
                                int[] iArr = new int[1];
                                String formatShortNumber = LocaleController.formatShortNumber(i, iArr);
                                if (currentChat.megagroup) {
                                    string = LocaleController.formatPluralString("Members", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), formatShortNumber);
                                } else {
                                    string = LocaleController.formatPluralString("Subscribers", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), formatShortNumber);
                                }
                            } else if (this.onlineCount > 1) {
                                string = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("OnlineCount", Math.min(this.onlineCount, currentChatInfo.participants_count))});
                            } else {
                                string = LocaleController.formatPluralString("Members", i);
                            }
                        } else if (ChatObject.isKickedFromChat(currentChat)) {
                            string = LocaleController.getString("YouWereKicked", NUM);
                        } else if (ChatObject.isLeftFromChat(currentChat)) {
                            string = LocaleController.getString("YouLeft", NUM);
                        } else {
                            int i2 = currentChat.participants_count;
                            if (!(currentChatInfo == null || (tLRPC$ChatParticipants = currentChatInfo.participants) == null)) {
                                i2 = tLRPC$ChatParticipants.participants.size();
                            }
                            if (this.onlineCount <= 1 || i2 == 0) {
                                string = LocaleController.formatPluralString("Members", i2);
                            } else {
                                string = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", i2), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                            }
                        }
                    } else if (currentUser != null) {
                        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(currentUser.id));
                        if (user != null) {
                            currentUser = user;
                        }
                        if (!UserObject.isReplyUser(currentUser)) {
                            if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                string = LocaleController.getString("ChatYourSelf", NUM);
                            } else {
                                int i3 = currentUser.id;
                                if (i3 == 333000 || i3 == 777000 || i3 == 42777) {
                                    string = LocaleController.getString("ServiceNotifications", NUM);
                                } else if (MessagesController.isSupportUser(currentUser)) {
                                    string = LocaleController.getString("SupportStatus", NUM);
                                } else if (currentUser.bot) {
                                    string = LocaleController.getString("Bot", NUM);
                                } else {
                                    boolean[] zArr = this.isOnline;
                                    zArr[0] = false;
                                    str = LocaleController.formatUserStatus(this.currentAccount, currentUser, zArr);
                                    z2 = this.isOnline[0];
                                }
                            }
                        }
                    }
                    str = string;
                } else if (this.titleTextView.getTag() == null) {
                    this.titleTextView.setTag(1);
                    AnimatorSet animatorSet3 = this.titleAnimation;
                    if (animatorSet3 != null) {
                        animatorSet3.cancel();
                        this.titleAnimation = null;
                    }
                    if (z) {
                        AnimatorSet animatorSet4 = new AnimatorSet();
                        this.titleAnimation = animatorSet4;
                        animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.titleTextView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(9.7f)}), ObjectAnimator.ofFloat(this.subtitleTextView, View.ALPHA, new float[]{0.0f})});
                        this.titleAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationCancel(Animator animator) {
                                AnimatorSet unused = ChatAvatarContainer.this.titleAnimation = null;
                            }

                            public void onAnimationEnd(Animator animator) {
                                if (ChatAvatarContainer.this.titleAnimation == animator) {
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
                this.lastSubtitleColorKey = z2 ? "chat_status" : "actionBarDefaultSubtitle";
                if (this.lastSubtitle == null) {
                    this.subtitleTextView.setText(str);
                    this.subtitleTextView.setTextColor(Theme.getColor(this.lastSubtitleColorKey));
                    this.subtitleTextView.setTag(this.lastSubtitleColorKey);
                    return;
                }
                this.lastSubtitle = str;
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
        setUserAvatar(tLRPC$User, false);
    }

    public void setUserAvatar(TLRPC$User tLRPC$User, boolean z) {
        this.avatarDrawable.setInfo(tLRPC$User);
        if (UserObject.isReplyUser(tLRPC$User)) {
            this.avatarDrawable.setAvatarType(12);
            this.avatarDrawable.setSmallSize(true);
            BackupImageView backupImageView = this.avatarImageView;
            if (backupImageView != null) {
                backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) tLRPC$User);
            }
        } else if (!UserObject.isUserSelf(tLRPC$User) || z) {
            this.avatarDrawable.setSmallSize(false);
            BackupImageView backupImageView2 = this.avatarImageView;
            if (backupImageView2 != null) {
                backupImageView2.setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$User);
            }
        } else {
            this.avatarDrawable.setAvatarType(1);
            this.avatarDrawable.setSmallSize(true);
            BackupImageView backupImageView3 = this.avatarImageView;
            if (backupImageView3 != null) {
                backupImageView3.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) tLRPC$User);
            }
        }
    }

    public void checkAndUpdateAvatar() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC$User currentUser = chatActivity.getCurrentUser();
            TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
            if (currentUser != null) {
                this.avatarDrawable.setInfo(currentUser);
                if (UserObject.isReplyUser(currentUser)) {
                    this.avatarDrawable.setSmallSize(true);
                    this.avatarDrawable.setAvatarType(12);
                    BackupImageView backupImageView = this.avatarImageView;
                    if (backupImageView != null) {
                        backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) currentUser);
                    }
                } else if (UserObject.isUserSelf(currentUser)) {
                    this.avatarDrawable.setSmallSize(true);
                    this.avatarDrawable.setAvatarType(1);
                    BackupImageView backupImageView2 = this.avatarImageView;
                    if (backupImageView2 != null) {
                        backupImageView2.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) currentUser);
                    }
                } else {
                    this.avatarDrawable.setSmallSize(false);
                    BackupImageView backupImageView3 = this.avatarImageView;
                    if (backupImageView3 != null) {
                        backupImageView3.setImage(ImageLocation.getForUser(currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) currentUser);
                    }
                }
            } else if (currentChat != null) {
                this.avatarDrawable.setInfo(currentChat);
                BackupImageView backupImageView4 = this.avatarImageView;
                if (backupImageView4 != null) {
                    backupImageView4.setImage(ImageLocation.getForChat(currentChat, false), "50_50", (Drawable) this.avatarDrawable, (Object) currentChat);
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (accessibilityNodeInfo.isClickable() && Build.VERSION.SDK_INT >= 21) {
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("OpenProfile", NUM)));
        }
    }

    public SharedMediaLayout.SharedMediaPreloader getSharedMediaPreloader() {
        return this.sharedMediaPreloader;
    }
}
