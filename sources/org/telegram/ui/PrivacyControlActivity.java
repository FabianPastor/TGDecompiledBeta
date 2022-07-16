package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputUser;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$PrivacyRule;
import org.telegram.tgnet.TLRPC$TL_account_privacyRules;
import org.telegram.tgnet.TLRPC$TL_account_setPrivacy;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyAddedByPhone;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyForwards;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyPhoneNumber;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyPhoneP2P;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyKeyVoiceMessages;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyValueAllowAll;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyValueAllowChatParticipants;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyValueAllowContacts;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyValueAllowUsers;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyValueDisallowAll;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyValueDisallowChatParticipants;
import org.telegram.tgnet.TLRPC$TL_inputPrivacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowChatParticipants;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowContacts;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC$TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC$TL_privacyValueDisallowChatParticipants;
import org.telegram.tgnet.TLRPC$TL_privacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateActivity;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int alwaysShareRow;
    /* access modifiers changed from: private */
    public ArrayList<Long> currentMinus;
    /* access modifiers changed from: private */
    public ArrayList<Long> currentPlus;
    /* access modifiers changed from: private */
    public int currentSubType;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int detailRow;
    private View doneButton;
    /* access modifiers changed from: private */
    public int everybodyRow;
    private ArrayList<Long> initialMinus;
    private ArrayList<Long> initialPlus;
    private int initialRulesSubType;
    private int initialRulesType;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public MessageCell messageCell;
    /* access modifiers changed from: private */
    public int messageRow;
    /* access modifiers changed from: private */
    public int myContactsRow;
    /* access modifiers changed from: private */
    public int neverShareRow;
    /* access modifiers changed from: private */
    public int nobodyRow;
    /* access modifiers changed from: private */
    public int p2pDetailRow;
    /* access modifiers changed from: private */
    public int p2pRow;
    /* access modifiers changed from: private */
    public int p2pSectionRow;
    /* access modifiers changed from: private */
    public int phoneContactsRow;
    /* access modifiers changed from: private */
    public int phoneDetailRow;
    /* access modifiers changed from: private */
    public int phoneEverybodyRow;
    /* access modifiers changed from: private */
    public int phoneSectionRow;
    /* access modifiers changed from: private */
    public boolean prevSubtypeContacts;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int rulesType;
    /* access modifiers changed from: private */
    public int sectionRow;
    /* access modifiers changed from: private */
    public int shareDetailRow;
    /* access modifiers changed from: private */
    public int shareSectionRow;

    private class MessageCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
        /* access modifiers changed from: private */
        public ChatMessageCell cell;
        /* access modifiers changed from: private */
        public HintView hintView;
        /* access modifiers changed from: private */
        public MessageObject messageObject;
        private Drawable shadowDrawable;

        /* access modifiers changed from: protected */
        public void dispatchSetPressed(boolean z) {
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public MessageCell(PrivacyControlActivity privacyControlActivity, Context context) {
            super(context);
            new PrivacyControlActivity$MessageCell$$ExternalSyntheticLambda0(this);
            setWillNotDraw(false);
            setClipToPadding(false);
            this.shadowDrawable = Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow");
            setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            TLRPC$User user = MessagesController.getInstance(privacyControlActivity.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(privacyControlActivity.currentAccount).getClientUserId()));
            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
            tLRPC$TL_message.message = LocaleController.getString("PrivacyForwardsMessageLine", NUM);
            tLRPC$TL_message.date = (((int) (System.currentTimeMillis() / 1000)) - 3600) + 60;
            tLRPC$TL_message.dialog_id = 1;
            tLRPC$TL_message.flags = 261;
            tLRPC$TL_message.from_id = new TLRPC$TL_peerUser();
            tLRPC$TL_message.id = 1;
            TLRPC$TL_messageFwdHeader tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader();
            tLRPC$TL_message.fwd_from = tLRPC$TL_messageFwdHeader;
            tLRPC$TL_messageFwdHeader.from_name = ContactsController.formatName(user.first_name, user.last_name);
            tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
            tLRPC$TL_message.out = false;
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$TL_message.peer_id = tLRPC$TL_peerUser;
            tLRPC$TL_peerUser.user_id = UserConfig.getInstance(privacyControlActivity.currentAccount).getClientUserId();
            MessageObject messageObject2 = new MessageObject(privacyControlActivity.currentAccount, tLRPC$TL_message, true, false);
            this.messageObject = messageObject2;
            messageObject2.eventId = 1;
            messageObject2.resetLayout();
            ChatMessageCell chatMessageCell = new ChatMessageCell(context);
            this.cell = chatMessageCell;
            chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate(this, privacyControlActivity) {
                public /* synthetic */ boolean canDrawOutboundsContent() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                }

                public /* synthetic */ boolean canPerformActions() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                }

                public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell, tLRPC$KeyboardButton);
                }

                public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, tLRPC$Chat, i, f, f2);
                }

                public /* synthetic */ boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell, tLRPC$User, f, f2);
                }

                public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, tLRPC$KeyboardButton);
                }

                public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell);
                }

                public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell, tLRPC$Chat, i, f, f2);
                }

                public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell);
                }

                public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
                }

                public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell, i);
                }

                public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell, i);
                }

                public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$TL_reactionCount tLRPC$TL_reactionCount, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tLRPC$TL_reactionCount, z);
                }

                public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                }

                public /* synthetic */ void didPressSideButton(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell);
                }

                public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell);
                }

                public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
                }

                public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell, tLRPC$User, f, f2);
                }

                public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell, str);
                }

                public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell, j);
                }

                public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                }

                public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                }

                public /* synthetic */ String getAdminRank(long j) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                }

                public /* synthetic */ PinchToZoomHelper getPinchToZoomHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                }

                public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                }

                public /* synthetic */ boolean hasSelectedMessages() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                }

                public /* synthetic */ void invalidateBlur() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                }

                public /* synthetic */ boolean isLandscape() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                }

                public /* synthetic */ boolean keyboardIsOpened() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                }

                public /* synthetic */ void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject, str, str2, str3, str4, i, i2);
                }

                public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                }

                public /* synthetic */ void needReloadPolls() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                }

                public /* synthetic */ void needShowPremiumFeatures(String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumFeatures(this, str);
                }

                public /* synthetic */ boolean onAccessibilityAction(int i, Bundle bundle) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i, bundle);
                }

                public /* synthetic */ void onDiceFinished() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                }

                public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                }

                public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell);
                }

                public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                }

                public /* synthetic */ void videoTimerReached() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                }
            });
            ChatMessageCell chatMessageCell2 = this.cell;
            chatMessageCell2.isChat = false;
            chatMessageCell2.setFullyDraw(true);
            this.cell.setMessageObject(this.messageObject, (MessageObject.GroupedMessages) null, false, false);
            addView(this.cell, LayoutHelper.createLinear(-1, -2));
            HintView hintView2 = new HintView(context, 1, true);
            this.hintView = hintView2;
            addView(hintView2, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.hintView.showForMessageCell(this.cell, false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
            if (!(cachedWallpaperNonBlocking == null || this.backgroundDrawable == cachedWallpaperNonBlocking)) {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
                this.backgroundDrawable = cachedWallpaperNonBlocking;
            }
            Drawable drawable = this.backgroundDrawable;
            if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                Drawable drawable2 = this.backgroundDrawable;
                if (drawable2 instanceof BackgroundGradientDrawable) {
                    this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable2).drawExactBoundsSize(canvas, this);
                } else {
                    drawable2.draw(canvas);
                }
            } else if (drawable instanceof BitmapDrawable) {
                if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                    canvas.save();
                    float f = 2.0f / AndroidUtilities.density;
                    canvas.scale(f, f);
                    this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                } else {
                    int measuredHeight = getMeasuredHeight();
                    float max = Math.max(((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth()), ((float) measuredHeight) / ((float) this.backgroundDrawable.getIntrinsicHeight()));
                    int ceil = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * max));
                    int ceil2 = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * max));
                    int measuredWidth = (getMeasuredWidth() - ceil) / 2;
                    int i = (measuredHeight - ceil2) / 2;
                    canvas.save();
                    canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                    this.backgroundDrawable.setBounds(measuredWidth, i, ceil + measuredWidth, ceil2 + i);
                }
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            } else {
                super.onDraw(canvas);
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
            if (disposable != null) {
                disposable.dispose();
                this.backgroundGradientDisposable = null;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.cell.invalidate();
        }
    }

    public PrivacyControlActivity(int i) {
        this(i, false);
    }

    public PrivacyControlActivity(int i, boolean z) {
        this.initialPlus = new ArrayList<>();
        this.initialMinus = new ArrayList<>();
        this.rulesType = i;
        if (z) {
            ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    public View createView(Context context) {
        if (this.rulesType == 5) {
            this.messageCell = new MessageCell(this, context);
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.rulesType;
        if (i == 6) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyPhone", NUM));
        } else if (i == 5) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyForwards", NUM));
        } else if (i == 4) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyProfilePhoto", NUM));
        } else if (i == 3) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyP2P", NUM));
        } else if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("Calls", NUM));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", NUM));
        } else if (i == 8) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyVoiceMessages", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (PrivacyControlActivity.this.checkDiscard()) {
                        PrivacyControlActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    PrivacyControlActivity.this.processDone();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), (CharSequence) LocaleController.getString("Done", NUM));
        boolean hasChanges = hasChanges();
        float f = 1.0f;
        this.doneButton.setAlpha(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleX(hasChanges ? 1.0f : 0.0f);
        View view = this.doneButton;
        if (!hasChanges) {
            f = 0.0f;
        }
        view.setScaleY(f);
        this.doneButton.setEnabled(hasChanges);
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PrivacyControlActivity$$ExternalSyntheticLambda7(this));
        setMessageText();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view, int i) {
        ArrayList<Long> arrayList;
        int i2 = this.nobodyRow;
        int i3 = 0;
        boolean z = true;
        if (i == i2 || i == this.everybodyRow || i == this.myContactsRow) {
            if (i == i2) {
                i3 = 1;
            } else if (i != this.everybodyRow) {
                i3 = 2;
            }
            if (i3 != this.currentType) {
                this.currentType = i3;
                updateDoneButton();
                updateRows(true);
            }
        } else if (i == this.phoneContactsRow || i == this.phoneEverybodyRow) {
            if (i != this.phoneEverybodyRow) {
                i3 = 1;
            }
            if (i3 != this.currentSubType) {
                this.currentSubType = i3;
                updateDoneButton();
                updateRows(true);
            }
        } else {
            int i4 = this.neverShareRow;
            if (i == i4 || i == this.alwaysShareRow) {
                if (i == i4) {
                    arrayList = this.currentMinus;
                } else {
                    arrayList = this.currentPlus;
                }
                if (arrayList.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(i == this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                    if (this.rulesType != 0) {
                        i3 = 1;
                    }
                    bundle.putInt("chatAddType", i3);
                    GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
                    groupCreateActivity.setDelegate((GroupCreateActivity.GroupCreateActivityDelegate) new PrivacyControlActivity$$ExternalSyntheticLambda8(this, i));
                    presentFragment(groupCreateActivity);
                    return;
                }
                boolean z2 = this.rulesType != 0;
                if (i != this.alwaysShareRow) {
                    z = false;
                }
                PrivacyUsersActivity privacyUsersActivity = new PrivacyUsersActivity(0, arrayList, z2, z);
                privacyUsersActivity.setDelegate(new PrivacyControlActivity$$ExternalSyntheticLambda9(this, i));
                presentFragment(privacyUsersActivity);
            } else if (i == this.p2pRow) {
                presentFragment(new PrivacyControlActivity(3));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(int i, ArrayList arrayList) {
        int i2 = 0;
        if (i == this.neverShareRow) {
            this.currentMinus = arrayList;
            while (i2 < this.currentMinus.size()) {
                this.currentPlus.remove(this.currentMinus.get(i2));
                i2++;
            }
        } else {
            this.currentPlus = arrayList;
            while (i2 < this.currentPlus.size()) {
                this.currentMinus.remove(this.currentPlus.get(i2));
                i2++;
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(int i, ArrayList arrayList, boolean z) {
        int i2 = 0;
        if (i == this.neverShareRow) {
            this.currentMinus = arrayList;
            if (z) {
                while (i2 < this.currentMinus.size()) {
                    this.currentPlus.remove(this.currentMinus.get(i2));
                    i2++;
                }
            }
        } else {
            this.currentPlus = arrayList;
            if (z) {
                while (i2 < this.currentPlus.size()) {
                    this.currentMinus.remove(this.currentPlus.get(i2));
                    i2++;
                }
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageCell messageCell2;
        if (i == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        } else if (i == NotificationCenter.emojiLoaded) {
            this.listView.invalidateViews();
        } else if (i == NotificationCenter.didSetNewWallpapper && (messageCell2 = this.messageCell) != null) {
            messageCell2.invalidate();
        }
    }

    private void updateDoneButton() {
        boolean hasChanges = hasChanges();
        this.doneButton.setEnabled(hasChanges);
        float f = 1.0f;
        ViewPropertyAnimator scaleX = this.doneButton.animate().alpha(hasChanges ? 1.0f : 0.0f).scaleX(hasChanges ? 1.0f : 0.0f);
        if (!hasChanges) {
            f = 0.0f;
        }
        scaleX.scaleY(f).setDuration(180).start();
    }

    private void applyCurrentPrivacySettings() {
        TLRPC$InputUser inputUser;
        TLRPC$InputUser inputUser2;
        TLRPC$TL_account_setPrivacy tLRPC$TL_account_setPrivacy = new TLRPC$TL_account_setPrivacy();
        int i = this.rulesType;
        if (i == 6) {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyPhoneNumber();
            if (this.currentType == 1) {
                TLRPC$TL_account_setPrivacy tLRPC$TL_account_setPrivacy2 = new TLRPC$TL_account_setPrivacy();
                tLRPC$TL_account_setPrivacy2.key = new TLRPC$TL_inputPrivacyKeyAddedByPhone();
                if (this.currentSubType == 0) {
                    tLRPC$TL_account_setPrivacy2.rules.add(new TLRPC$TL_inputPrivacyValueAllowAll());
                } else {
                    tLRPC$TL_account_setPrivacy2.rules.add(new TLRPC$TL_inputPrivacyValueAllowContacts());
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_setPrivacy2, new PrivacyControlActivity$$ExternalSyntheticLambda5(this), 2);
            }
        } else if (i == 5) {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyForwards();
        } else if (i == 4) {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyProfilePhoto();
        } else if (i == 3) {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyPhoneP2P();
        } else if (i == 2) {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyPhoneCall();
        } else if (i == 1) {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyChatInvite();
        } else if (i == 8) {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyVoiceMessages();
        } else {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TLRPC$TL_inputPrivacyValueAllowUsers tLRPC$TL_inputPrivacyValueAllowUsers = new TLRPC$TL_inputPrivacyValueAllowUsers();
            TLRPC$TL_inputPrivacyValueAllowChatParticipants tLRPC$TL_inputPrivacyValueAllowChatParticipants = new TLRPC$TL_inputPrivacyValueAllowChatParticipants();
            for (int i2 = 0; i2 < this.currentPlus.size(); i2++) {
                long longValue = this.currentPlus.get(i2).longValue();
                if (DialogObject.isUserDialog(longValue)) {
                    TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(longValue));
                    if (!(user == null || (inputUser2 = MessagesController.getInstance(this.currentAccount).getInputUser(user)) == null)) {
                        tLRPC$TL_inputPrivacyValueAllowUsers.users.add(inputUser2);
                    }
                } else {
                    tLRPC$TL_inputPrivacyValueAllowChatParticipants.chats.add(Long.valueOf(-longValue));
                }
            }
            tLRPC$TL_account_setPrivacy.rules.add(tLRPC$TL_inputPrivacyValueAllowUsers);
            tLRPC$TL_account_setPrivacy.rules.add(tLRPC$TL_inputPrivacyValueAllowChatParticipants);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TLRPC$TL_inputPrivacyValueDisallowUsers tLRPC$TL_inputPrivacyValueDisallowUsers = new TLRPC$TL_inputPrivacyValueDisallowUsers();
            TLRPC$TL_inputPrivacyValueDisallowChatParticipants tLRPC$TL_inputPrivacyValueDisallowChatParticipants = new TLRPC$TL_inputPrivacyValueDisallowChatParticipants();
            for (int i3 = 0; i3 < this.currentMinus.size(); i3++) {
                long longValue2 = this.currentMinus.get(i3).longValue();
                if (DialogObject.isUserDialog(longValue2)) {
                    TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(longValue2));
                    if (!(user2 == null || (inputUser = getMessagesController().getInputUser(user2)) == null)) {
                        tLRPC$TL_inputPrivacyValueDisallowUsers.users.add(inputUser);
                    }
                } else {
                    tLRPC$TL_inputPrivacyValueDisallowChatParticipants.chats.add(Long.valueOf(-longValue2));
                }
            }
            tLRPC$TL_account_setPrivacy.rules.add(tLRPC$TL_inputPrivacyValueDisallowUsers);
            tLRPC$TL_account_setPrivacy.rules.add(tLRPC$TL_inputPrivacyValueDisallowChatParticipants);
        }
        int i4 = this.currentType;
        if (i4 == 0) {
            tLRPC$TL_account_setPrivacy.rules.add(new TLRPC$TL_inputPrivacyValueAllowAll());
        } else if (i4 == 1) {
            tLRPC$TL_account_setPrivacy.rules.add(new TLRPC$TL_inputPrivacyValueDisallowAll());
        } else if (i4 == 2) {
            tLRPC$TL_account_setPrivacy.rules.add(new TLRPC$TL_inputPrivacyValueAllowContacts());
        }
        AlertDialog alertDialog = null;
        if (getParentActivity() != null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_setPrivacy, new PrivacyControlActivity$$ExternalSyntheticLambda6(this, alertDialog), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyCurrentPrivacySettings$4(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PrivacyControlActivity$$ExternalSyntheticLambda3(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyCurrentPrivacySettings$3(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(((TLRPC$TL_account_privacyRules) tLObject).rules, 7);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyCurrentPrivacySettings$6(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PrivacyControlActivity$$ExternalSyntheticLambda4(this, alertDialog, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyCurrentPrivacySettings$5(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_privacyRules tLRPC$TL_account_privacyRules = (TLRPC$TL_account_privacyRules) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_account_privacyRules.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_account_privacyRules.chats, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(tLRPC$TL_account_privacyRules.rules, this.rulesType);
            finishFragment();
            return;
        }
        showErrorAlert();
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList<>();
        this.currentMinus = new ArrayList<>();
        ArrayList<TLRPC$PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules == null || privacyRules.size() == 0) {
            this.currentType = 1;
        } else {
            char c = 65535;
            for (int i = 0; i < privacyRules.size(); i++) {
                TLRPC$PrivacyRule tLRPC$PrivacyRule = privacyRules.get(i);
                if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueAllowChatParticipants) {
                    TLRPC$TL_privacyValueAllowChatParticipants tLRPC$TL_privacyValueAllowChatParticipants = (TLRPC$TL_privacyValueAllowChatParticipants) tLRPC$PrivacyRule;
                    int size = tLRPC$TL_privacyValueAllowChatParticipants.chats.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        this.currentPlus.add(Long.valueOf(-tLRPC$TL_privacyValueAllowChatParticipants.chats.get(i2).longValue()));
                    }
                } else if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueDisallowChatParticipants) {
                    TLRPC$TL_privacyValueDisallowChatParticipants tLRPC$TL_privacyValueDisallowChatParticipants = (TLRPC$TL_privacyValueDisallowChatParticipants) tLRPC$PrivacyRule;
                    int size2 = tLRPC$TL_privacyValueDisallowChatParticipants.chats.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        this.currentMinus.add(Long.valueOf(-tLRPC$TL_privacyValueDisallowChatParticipants.chats.get(i3).longValue()));
                    }
                } else if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueAllowUsers) {
                    this.currentPlus.addAll(((TLRPC$TL_privacyValueAllowUsers) tLRPC$PrivacyRule).users);
                } else if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueDisallowUsers) {
                    this.currentMinus.addAll(((TLRPC$TL_privacyValueDisallowUsers) tLRPC$PrivacyRule).users);
                } else if (c == 65535) {
                    if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueAllowAll) {
                        c = 0;
                    } else {
                        c = tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueDisallowAll ? (char) 1 : 2;
                    }
                }
            }
            if (c == 0 || (c == 65535 && this.currentMinus.size() > 0)) {
                this.currentType = 0;
            } else if (c == 2 || (c == 65535 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
                this.currentType = 2;
            } else if (c == 1 || (c == 65535 && this.currentPlus.size() > 0)) {
                this.currentType = 1;
            }
            View view = this.doneButton;
            if (view != null) {
                view.setAlpha(0.0f);
                this.doneButton.setScaleX(0.0f);
                this.doneButton.setScaleY(0.0f);
                this.doneButton.setEnabled(false);
            }
        }
        this.initialPlus.clear();
        this.initialMinus.clear();
        this.initialRulesType = this.currentType;
        this.initialPlus.addAll(this.currentPlus);
        this.initialMinus.addAll(this.currentMinus);
        if (this.rulesType == 6) {
            ArrayList<TLRPC$PrivacyRule> privacyRules2 = ContactsController.getInstance(this.currentAccount).getPrivacyRules(7);
            if (privacyRules2 != null && privacyRules2.size() != 0) {
                int i4 = 0;
                while (true) {
                    if (i4 >= privacyRules2.size()) {
                        break;
                    }
                    TLRPC$PrivacyRule tLRPC$PrivacyRule2 = privacyRules2.get(i4);
                    if (tLRPC$PrivacyRule2 instanceof TLRPC$TL_privacyValueAllowAll) {
                        this.currentSubType = 0;
                        break;
                    } else if (tLRPC$PrivacyRule2 instanceof TLRPC$TL_privacyValueDisallowAll) {
                        this.currentSubType = 2;
                        break;
                    } else if (tLRPC$PrivacyRule2 instanceof TLRPC$TL_privacyValueAllowContacts) {
                        this.currentSubType = 1;
                        break;
                    } else {
                        i4++;
                    }
                }
            } else {
                this.currentSubType = 0;
            }
            this.initialRulesSubType = this.currentSubType;
        }
        updateRows(false);
    }

    private boolean hasChanges() {
        int i = this.initialRulesType;
        int i2 = this.currentType;
        if (i != i2) {
            return true;
        }
        if ((this.rulesType == 6 && i2 == 1 && this.initialRulesSubType != this.currentSubType) || this.initialMinus.size() != this.currentMinus.size() || this.initialPlus.size() != this.currentPlus.size()) {
            return true;
        }
        Collections.sort(this.initialPlus);
        Collections.sort(this.currentPlus);
        if (!this.initialPlus.equals(this.currentPlus)) {
            return true;
        }
        Collections.sort(this.initialMinus);
        Collections.sort(this.currentMinus);
        if (!this.initialMinus.equals(this.currentMinus)) {
            return true;
        }
        return false;
    }

    private void updateRows(boolean z) {
        RecyclerView.ViewHolder findContainingViewHolder;
        int i = this.alwaysShareRow;
        int i2 = this.neverShareRow;
        int i3 = this.phoneDetailRow;
        int i4 = this.detailRow;
        int i5 = this.currentType;
        boolean z2 = i5 == 1 && this.currentSubType == 1;
        this.rowCount = 0;
        int i6 = this.rulesType;
        if (i6 == 5) {
            this.rowCount = 0 + 1;
            this.messageRow = 0;
        } else {
            this.messageRow = -1;
        }
        int i7 = this.rowCount;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.sectionRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.everybodyRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.myContactsRow = i9;
        if (i6 == 0 || i6 == 2 || i6 == 3 || i6 == 5 || i6 == 6 || i6 == 8) {
            this.rowCount = i10 + 1;
            this.nobodyRow = i10;
        } else {
            this.nobodyRow = -1;
        }
        if (i6 == 6 && i5 == 1) {
            int i11 = this.rowCount;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.phoneDetailRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.phoneSectionRow = i12;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.phoneEverybodyRow = i13;
            this.rowCount = i14 + 1;
            this.phoneContactsRow = i14;
        } else {
            this.phoneDetailRow = -1;
            this.phoneSectionRow = -1;
            this.phoneEverybodyRow = -1;
            this.phoneContactsRow = -1;
        }
        int i15 = this.rowCount;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.detailRow = i15;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.shareSectionRow = i16;
        if (i5 == 1 || i5 == 2) {
            this.rowCount = i17 + 1;
            this.alwaysShareRow = i17;
        } else {
            this.alwaysShareRow = -1;
        }
        if (i5 == 0 || i5 == 2) {
            int i18 = this.rowCount;
            this.rowCount = i18 + 1;
            this.neverShareRow = i18;
        } else {
            this.neverShareRow = -1;
        }
        int i19 = this.rowCount;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.shareDetailRow = i19;
        if (i6 == 2) {
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.p2pSectionRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.p2pRow = i21;
            this.rowCount = i22 + 1;
            this.p2pDetailRow = i22;
        } else {
            this.p2pSectionRow = -1;
            this.p2pRow = -1;
            this.p2pDetailRow = -1;
        }
        setMessageText();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 == null) {
            return;
        }
        if (z) {
            int childCount = this.listView.getChildCount();
            for (int i23 = 0; i23 < childCount; i23++) {
                View childAt = this.listView.getChildAt(i23);
                if ((childAt instanceof RadioCell) && (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) != null) {
                    int adapterPosition = findContainingViewHolder.getAdapterPosition();
                    RadioCell radioCell = (RadioCell) childAt;
                    int i24 = this.everybodyRow;
                    if (adapterPosition == i24 || adapterPosition == this.myContactsRow || adapterPosition == this.nobodyRow) {
                        radioCell.setChecked(this.currentType == (adapterPosition == i24 ? 0 : adapterPosition == this.myContactsRow ? 2 : 1), true);
                    } else {
                        radioCell.setChecked(this.currentSubType == (adapterPosition == this.phoneContactsRow ? 1 : 0), true);
                    }
                }
            }
            if (this.prevSubtypeContacts != z2) {
                this.listAdapter.notifyItemChanged(i4);
            }
            int i25 = this.alwaysShareRow;
            if ((i25 != -1 || i == -1 || this.neverShareRow == -1 || i2 != -1) && (i25 == -1 || i != -1 || this.neverShareRow != -1 || i2 == -1)) {
                if (i25 == -1 && i != -1) {
                    this.listAdapter.notifyItemRemoved(i);
                } else if (i25 != -1 && i == -1) {
                    this.listAdapter.notifyItemInserted(i25);
                }
                int i26 = this.neverShareRow;
                if (i26 == -1 && i2 != -1) {
                    this.listAdapter.notifyItemRemoved(i2);
                    int i27 = this.phoneDetailRow;
                    if (i27 == -1 && i3 != -1) {
                        this.listAdapter.notifyItemRangeRemoved(i3, 4);
                    } else if (i27 != -1 && i3 == -1) {
                        this.listAdapter.notifyItemRangeInserted(i27, 4);
                    }
                } else if (i26 != -1 && i2 == -1) {
                    int i28 = this.phoneDetailRow;
                    if (i28 == -1 && i3 != -1) {
                        this.listAdapter.notifyItemRangeRemoved(i3, 4);
                    } else if (i28 != -1 && i3 == -1) {
                        this.listAdapter.notifyItemRangeInserted(i28, 4);
                    }
                    this.listAdapter.notifyItemInserted(this.neverShareRow);
                }
            } else {
                ListAdapter listAdapter3 = this.listAdapter;
                if (i25 != -1) {
                    i = i2;
                }
                listAdapter3.notifyItemChanged(i);
                int i29 = this.phoneDetailRow;
                if (i29 == -1 && i3 != -1) {
                    this.listAdapter.notifyItemRangeRemoved(i3, 4);
                } else if (i29 != -1 && i3 == -1) {
                    this.listAdapter.notifyItemRangeInserted(i29, 4);
                }
            }
        } else {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void setMessageText() {
        MessageCell messageCell2 = this.messageCell;
        if (messageCell2 != null) {
            messageCell2.messageObject.messageOwner.fwd_from.from_id = new TLRPC$TL_peerUser();
            int i = this.currentType;
            if (i == 0) {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsEverybody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 1;
            } else if (i == 1) {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsNobody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 0;
            } else {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsContacts", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 1;
            }
            this.messageCell.cell.forceResetMessageObject();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (getParentActivity() != null) {
            if (this.currentType != 0 && this.rulesType == 0) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (!globalMainSettings.getBoolean("privacyAlertShowed", false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (this.rulesType == 1) {
                        builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("CustomHelp", NUM));
                    }
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new PrivacyControlActivity$$ExternalSyntheticLambda2(this, globalMainSettings));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$7(SharedPreferences sharedPreferences, DialogInterface dialogInterface, int i) {
        applyCurrentPrivacySettings();
        sharedPreferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (this.doneButton.getAlpha() != 1.0f) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        builder.setMessage(LocaleController.getString("PrivacySettingsChangedAlert", NUM));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new PrivacyControlActivity$$ExternalSyntheticLambda1(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new PrivacyControlActivity$$ExternalSyntheticLambda0(this));
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$8(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$9(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PrivacyControlActivity.this.nobodyRow || adapterPosition == PrivacyControlActivity.this.everybodyRow || adapterPosition == PrivacyControlActivity.this.myContactsRow || adapterPosition == PrivacyControlActivity.this.neverShareRow || adapterPosition == PrivacyControlActivity.this.alwaysShareRow || (adapterPosition == PrivacyControlActivity.this.p2pRow && !ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivacyInfo(3));
        }

        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            if (i != 0) {
                if (i == 1) {
                    view = new TextInfoPrivacyCell(this.mContext);
                } else if (i == 2) {
                    view2 = new HeaderCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                } else if (i == 3) {
                    view2 = new RadioCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                } else if (i != 4) {
                    view = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                } else {
                    view = PrivacyControlActivity.this.messageCell;
                }
                return new RecyclerListView.Holder(view);
            }
            view2 = new TextSettingsCell(this.mContext);
            view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            view = view2;
            return new RecyclerListView.Holder(view);
        }

        private int getUsersCount(ArrayList<Long> arrayList) {
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                long longValue = arrayList.get(i2).longValue();
                if (longValue > 0) {
                    i++;
                } else {
                    TLRPC$Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Long.valueOf(-longValue));
                    if (chat != null) {
                        i += chat.participants_count;
                    }
                }
            }
            return i;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v19, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v21, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v2 */
        /* JADX WARNING: type inference failed for: r3v4 */
        /* JADX WARNING: type inference failed for: r3v5, types: [int] */
        /* JADX WARNING: type inference failed for: r3v6 */
        /* JADX WARNING: type inference failed for: r3v7 */
        /* JADX WARNING: type inference failed for: r3v14 */
        /* JADX WARNING: type inference failed for: r3v16 */
        /* JADX WARNING: type inference failed for: r3v18 */
        /* JADX WARNING: type inference failed for: r3v20 */
        /* JADX WARNING: type inference failed for: r3v22 */
        /* JADX WARNING: Code restructure failed: missing block: B:158:0x0406, code lost:
            if (org.telegram.ui.PrivacyControlActivity.access$1900(r11.this$0) == 2) goto L_0x0352;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:160:0x0410, code lost:
            if (r13 == org.telegram.ui.PrivacyControlActivity.access$2700(r11.this$0)) goto L_0x0412;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:163:0x0417  */
        /* JADX WARNING: Removed duplicated region for block: B:219:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r12, int r13) {
            /*
                r11 = this;
                int r0 = r12.getItemViewType()
                r1 = -1
                r2 = 3
                r3 = 0
                r4 = 1
                if (r0 == 0) goto L_0x0437
                r5 = 8
                r6 = 4
                r7 = 5
                r8 = 6
                r9 = 2
                if (r0 == r4) goto L_0x022a
                if (r0 == r9) goto L_0x0134
                if (r0 == r2) goto L_0x0018
                goto L_0x0524
            L_0x0018:
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.RadioCell r12 = (org.telegram.ui.Cells.RadioCell) r12
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.everybodyRow
                r5 = 2131626408(0x7f0e09a8, float:1.8880051E38)
                java.lang.String r6 = "LastSeenContacts"
                r7 = 2131626414(0x7f0e09ae, float:1.8880064E38)
                java.lang.String r8 = "LastSeenEverybody"
                if (r13 == r0) goto L_0x0074
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.myContactsRow
                if (r13 == r0) goto L_0x0074
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.nobodyRow
                if (r13 != r0) goto L_0x003f
                goto L_0x0074
            L_0x003f:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneContactsRow
                if (r13 != r0) goto L_0x005a
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r6, r5)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != r4) goto L_0x0054
                goto L_0x0055
            L_0x0054:
                r4 = 0
            L_0x0055:
                r12.setText(r13, r4, r3)
                goto L_0x0524
            L_0x005a:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneEverybodyRow
                if (r13 != r0) goto L_0x0524
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r8, r7)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != 0) goto L_0x006f
                r3 = 1
            L_0x006f:
                r12.setText(r13, r3, r4)
                goto L_0x0524
            L_0x0074:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.everybodyRow
                if (r13 != r0) goto L_0x00ad
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r2) goto L_0x009b
                r13 = 2131627172(0x7f0e0ca4, float:1.88816E38)
                java.lang.String r0 = "P2PEverybody"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != 0) goto L_0x0096
                r3 = 1
            L_0x0096:
                r12.setText(r13, r3, r4)
                goto L_0x0524
            L_0x009b:
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r8, r7)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != 0) goto L_0x00a8
                r3 = 1
            L_0x00a8:
                r12.setText(r13, r3, r4)
                goto L_0x0524
            L_0x00ad:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.myContactsRow
                if (r13 != r0) goto L_0x00fc
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r2) goto L_0x00df
                r13 = 2131627167(0x7f0e0c9f, float:1.888159E38)
                java.lang.String r0 = "P2PContacts"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r9) goto L_0x00d0
                r0 = 1
                goto L_0x00d1
            L_0x00d0:
                r0 = 0
            L_0x00d1:
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.nobodyRow
                if (r2 == r1) goto L_0x00da
                r3 = 1
            L_0x00da:
                r12.setText(r13, r0, r3)
                goto L_0x0524
            L_0x00df:
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r6, r5)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r9) goto L_0x00ed
                r0 = 1
                goto L_0x00ee
            L_0x00ed:
                r0 = 0
            L_0x00ee:
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.nobodyRow
                if (r2 == r1) goto L_0x00f7
                r3 = 1
            L_0x00f7:
                r12.setText(r13, r0, r3)
                goto L_0x0524
            L_0x00fc:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r2) goto L_0x011c
                r13 = 2131627174(0x7f0e0ca6, float:1.8881605E38)
                java.lang.String r0 = "P2PNobody"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r4) goto L_0x0116
                goto L_0x0117
            L_0x0116:
                r4 = 0
            L_0x0117:
                r12.setText(r13, r4, r3)
                goto L_0x0524
            L_0x011c:
                r13 = 2131626417(0x7f0e09b1, float:1.888007E38)
                java.lang.String r0 = "LastSeenNobody"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r4) goto L_0x012e
                goto L_0x012f
            L_0x012e:
                r4 = 0
            L_0x012f:
                r12.setText(r13, r4, r3)
                goto L_0x0524
            L_0x0134:
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.HeaderCell r12 = (org.telegram.ui.Cells.HeaderCell) r12
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.sectionRow
                if (r13 != r0) goto L_0x01e8
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r8) goto L_0x0156
                r13 = 2131627772(0x7f0e0efc, float:1.8882818E38)
                java.lang.String r0 = "PrivacyPhoneTitle"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x0156:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r7) goto L_0x016c
                r13 = 2131627755(0x7f0e0eeb, float:1.8882783E38)
                java.lang.String r0 = "PrivacyForwardsTitle"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x016c:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r6) goto L_0x0182
                r13 = 2131627780(0x7f0e0var_, float:1.8882834E38)
                java.lang.String r0 = "PrivacyProfilePhotoTitle"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x0182:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r2) goto L_0x0198
                r13 = 2131627171(0x7f0e0ca3, float:1.8881599E38)
                java.lang.String r0 = "P2PEnabledWith"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x0198:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r9) goto L_0x01ae
                r13 = 2131629301(0x7f0e14f5, float:1.888592E38)
                java.lang.String r0 = "WhoCanCallMe"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x01ae:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r4) goto L_0x01c4
                r13 = 2131629296(0x7f0e14f0, float:1.8885909E38)
                java.lang.String r0 = "WhoCanAddMe"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x01c4:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r5) goto L_0x01da
                r13 = 2131627787(0x7f0e0f0b, float:1.8882848E38)
                java.lang.String r0 = "PrivacyVoiceMessagesTitle"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x01da:
                r13 = 2131626419(0x7f0e09b3, float:1.8880074E38)
                java.lang.String r0 = "LastSeenTitle"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x01e8:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.shareSectionRow
                if (r13 != r0) goto L_0x01fe
                r13 = 2131624272(0x7f0e0150, float:1.887572E38)
                java.lang.String r0 = "AddExceptions"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x01fe:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pSectionRow
                if (r13 != r0) goto L_0x0214
                r13 = 2131627759(0x7f0e0eef, float:1.8882792E38)
                java.lang.String r0 = "PrivacyP2PHeader"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x0214:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneSectionRow
                if (r13 != r0) goto L_0x0524
                r13 = 2131627773(0x7f0e0efd, float:1.888282E38)
                java.lang.String r0 = "PrivacyPhoneTitle2"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0524
            L_0x022a:
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r12 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r12
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.detailRow
                r1 = 2131165435(0x7var_fb, float:1.7945087E38)
                r10 = 2131165436(0x7var_fc, float:1.794509E38)
                if (r13 != r0) goto L_0x0357
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r8) goto L_0x02c7
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r13.currentType
                if (r0 != r4) goto L_0x0256
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != r4) goto L_0x0256
                r0 = 1
                goto L_0x0257
            L_0x0256:
                r0 = 0
            L_0x0257:
                boolean r13 = r13.prevSubtypeContacts = r0
                if (r13 == 0) goto L_0x026b
                r13 = 2131627770(0x7f0e0efa, float:1.8882814E38)
                java.lang.String r0 = "PrivacyPhoneInfo3"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0352
            L_0x026b:
                android.text.SpannableStringBuilder r13 = new android.text.SpannableStringBuilder
                r13.<init>()
                java.util.Locale r0 = java.util.Locale.ENGLISH
                java.lang.Object[] r2 = new java.lang.Object[r4]
                org.telegram.ui.PrivacyControlActivity r5 = org.telegram.ui.PrivacyControlActivity.this
                org.telegram.messenger.UserConfig r5 = r5.getUserConfig()
                java.lang.String r5 = r5.getClientPhone()
                r2[r3] = r5
                java.lang.String r5 = "https://t.me/+%s"
                java.lang.String r0 = java.lang.String.format(r0, r5, r2)
                android.text.SpannableString r2 = new android.text.SpannableString
                r2.<init>(r0)
                org.telegram.ui.PrivacyControlActivity$ListAdapter$1 r5 = new org.telegram.ui.PrivacyControlActivity$ListAdapter$1
                r5.<init>(r0)
                int r0 = r0.length()
                r6 = 33
                r2.setSpan(r5, r3, r0, r6)
                r0 = 2131627768(0x7f0e0ef8, float:1.888281E38)
                java.lang.String r3 = "PrivacyPhoneInfo"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                android.text.SpannableStringBuilder r0 = r13.append(r0)
                java.lang.String r3 = "\n\n"
                android.text.SpannableStringBuilder r0 = r0.append(r3)
                r3 = 2131627771(0x7f0e0efb, float:1.8882816E38)
                java.lang.String r5 = "PrivacyPhoneInfo4"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                android.text.SpannableStringBuilder r0 = r0.append(r3)
                java.lang.String r3 = "\n"
                android.text.SpannableStringBuilder r0 = r0.append(r3)
                r0.append(r2)
                r12.setText(r13)
                goto L_0x0352
            L_0x02c7:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r7) goto L_0x02dd
                r13 = 2131627751(0x7f0e0ee7, float:1.8882775E38)
                java.lang.String r0 = "PrivacyForwardsInfo"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0352
            L_0x02dd:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r6) goto L_0x02f2
                r13 = 2131627778(0x7f0e0var_, float:1.888283E38)
                java.lang.String r0 = "PrivacyProfilePhotoInfo"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0352
            L_0x02f2:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r2) goto L_0x0307
                r13 = 2131627742(0x7f0e0ede, float:1.8882757E38)
                java.lang.String r0 = "PrivacyCallsP2PHelp"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0352
            L_0x0307:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r9) goto L_0x031c
                r13 = 2131629302(0x7f0e14f6, float:1.8885921E38)
                java.lang.String r0 = "WhoCanCallMeInfo"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0352
            L_0x031c:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r4) goto L_0x0331
                r13 = 2131629297(0x7f0e14f1, float:1.888591E38)
                java.lang.String r0 = "WhoCanAddMeInfo"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0352
            L_0x0331:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r5) goto L_0x0346
                r13 = 2131627785(0x7f0e0var_, float:1.8882844E38)
                java.lang.String r0 = "PrivacyVoiceMessagesInfo"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0352
            L_0x0346:
                r13 = 2131625309(0x7f0e055d, float:1.8877822E38)
                java.lang.String r0 = "CustomHelp"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
            L_0x0352:
                r3 = 2131165435(0x7var_fb, float:1.7945087E38)
                goto L_0x0415
            L_0x0357:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.shareDetailRow
                if (r13 != r0) goto L_0x040a
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r8) goto L_0x0375
                r13 = 2131627769(0x7f0e0ef9, float:1.8882812E38)
                java.lang.String r0 = "PrivacyPhoneInfo2"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0400
            L_0x0375:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r7) goto L_0x038b
                r13 = 2131627752(0x7f0e0ee8, float:1.8882777E38)
                java.lang.String r0 = "PrivacyForwardsInfo2"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0400
            L_0x038b:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r6) goto L_0x03a0
                r13 = 2131627779(0x7f0e0var_, float:1.8882832E38)
                java.lang.String r0 = "PrivacyProfilePhotoInfo2"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0400
            L_0x03a0:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r2) goto L_0x03b5
                r13 = 2131625311(0x7f0e055f, float:1.8877826E38)
                java.lang.String r0 = "CustomP2PInfo"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0400
            L_0x03b5:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r9) goto L_0x03ca
                r13 = 2131625308(0x7f0e055c, float:1.887782E38)
                java.lang.String r0 = "CustomCallInfo"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0400
            L_0x03ca:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r4) goto L_0x03df
                r13 = 2131625312(0x7f0e0560, float:1.8877828E38)
                java.lang.String r0 = "CustomShareInfo"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0400
            L_0x03df:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r5) goto L_0x03f4
                r13 = 2131627786(0x7f0e0f0a, float:1.8882846E38)
                java.lang.String r0 = "PrivacyVoiceMessagesInfo2"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x0400
            L_0x03f4:
                r13 = 2131625313(0x7f0e0561, float:1.887783E38)
                java.lang.String r0 = "CustomShareSettingsHelp"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
            L_0x0400:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.rulesType
                if (r13 != r9) goto L_0x0412
                goto L_0x0352
            L_0x040a:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pDetailRow
                if (r13 != r0) goto L_0x0415
            L_0x0412:
                r3 = 2131165436(0x7var_fc, float:1.794509E38)
            L_0x0415:
                if (r3 == 0) goto L_0x0524
                android.content.Context r13 = r11.mContext
                java.lang.String r0 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r13, (int) r3, (java.lang.String) r0)
                org.telegram.ui.Components.CombinedDrawable r0 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable
                java.lang.String r2 = "windowBackgroundGray"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r1.<init>(r2)
                r0.<init>(r1, r13)
                r0.setFullsize(r4)
                r12.setBackgroundDrawable(r0)
                goto L_0x0524
            L_0x0437:
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.TextSettingsCell r12 = (org.telegram.ui.Cells.TextSettingsCell) r12
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.alwaysShareRow
                java.lang.String r5 = "Users"
                r6 = 2131625645(0x7f0e06ad, float:1.8878504E38)
                java.lang.String r7 = "EmpryUsersPlaceholder"
                if (r13 != r0) goto L_0x04a1
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r13 = r13.currentPlus
                int r13 = r13.size()
                if (r13 == 0) goto L_0x0467
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r13 = r13.currentPlus
                int r13 = r11.getUsersCount(r13)
                java.lang.Object[] r0 = new java.lang.Object[r3]
                java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r5, r13, r0)
                goto L_0x046b
            L_0x0467:
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r7, r6)
            L_0x046b:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.rulesType
                if (r0 == 0) goto L_0x048a
                r0 = 2131624340(0x7f0e0194, float:1.8875857E38)
                java.lang.String r2 = "AlwaysAllow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.neverShareRow
                if (r2 == r1) goto L_0x0485
                r3 = 1
            L_0x0485:
                r12.setTextAndValue(r0, r13, r3)
                goto L_0x0524
            L_0x048a:
                r0 = 2131624341(0x7f0e0195, float:1.8875859E38)
                java.lang.String r2 = "AlwaysShareWith"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.neverShareRow
                if (r2 == r1) goto L_0x049c
                r3 = 1
            L_0x049c:
                r12.setTextAndValue(r0, r13, r3)
                goto L_0x0524
            L_0x04a1:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.neverShareRow
                if (r13 != r0) goto L_0x04ec
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r13 = r13.currentMinus
                int r13 = r13.size()
                if (r13 == 0) goto L_0x04c6
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r13 = r13.currentMinus
                int r13 = r11.getUsersCount(r13)
                java.lang.Object[] r0 = new java.lang.Object[r3]
                java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r5, r13, r0)
                goto L_0x04ca
            L_0x04c6:
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r7, r6)
            L_0x04ca:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.rulesType
                if (r0 == 0) goto L_0x04df
                r0 = 2131626810(0x7f0e0b3a, float:1.8880867E38)
                java.lang.String r1 = "NeverAllow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r12.setTextAndValue(r0, r13, r3)
                goto L_0x0524
            L_0x04df:
                r0 = 2131626811(0x7f0e0b3b, float:1.8880869E38)
                java.lang.String r1 = "NeverShareWith"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r12.setTextAndValue(r0, r13, r3)
                goto L_0x0524
            L_0x04ec:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pRow
                if (r13 != r0) goto L_0x0524
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                int r13 = r13.currentAccount
                org.telegram.messenger.ContactsController r13 = org.telegram.messenger.ContactsController.getInstance(r13)
                boolean r13 = r13.getLoadingPrivacyInfo(r2)
                if (r13 == 0) goto L_0x050e
                r13 = 2131626510(0x7f0e0a0e, float:1.8880258E38)
                java.lang.String r0 = "Loading"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                goto L_0x0518
            L_0x050e:
                org.telegram.ui.PrivacyControlActivity r13 = org.telegram.ui.PrivacyControlActivity.this
                org.telegram.messenger.AccountInstance r13 = r13.getAccountInstance()
                java.lang.String r13 = org.telegram.ui.PrivacySettingsActivity.formatRulesString(r13, r2)
            L_0x0518:
                r0 = 2131627758(0x7f0e0eee, float:1.888279E38)
                java.lang.String r1 = "PrivacyP2P2"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r12.setTextAndValue(r0, r13, r3)
            L_0x0524:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == PrivacyControlActivity.this.alwaysShareRow || i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.p2pRow) {
                return 0;
            }
            if (i == PrivacyControlActivity.this.shareDetailRow || i == PrivacyControlActivity.this.detailRow || i == PrivacyControlActivity.this.p2pDetailRow) {
                return 1;
            }
            if (i == PrivacyControlActivity.this.sectionRow || i == PrivacyControlActivity.this.shareSectionRow || i == PrivacyControlActivity.this.p2pSectionRow || i == PrivacyControlActivity.this.phoneSectionRow) {
                return 2;
            }
            if (i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.phoneEverybodyRow || i == PrivacyControlActivity.this.phoneContactsRow) {
                return 3;
            }
            if (i == PrivacyControlActivity.this.messageRow) {
                return 4;
            }
            if (i == PrivacyControlActivity.this.phoneDetailRow) {
                return 5;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient2"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient3"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        return arrayList;
    }
}
