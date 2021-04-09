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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.PrivacyUsersActivity;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int alwaysShareRow;
    /* access modifiers changed from: private */
    public ArrayList<Integer> currentMinus;
    /* access modifiers changed from: private */
    public ArrayList<Integer> currentPlus;
    /* access modifiers changed from: private */
    public int currentSubType;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int detailRow;
    private View doneButton;
    /* access modifiers changed from: private */
    public int everybodyRow;
    private ArrayList<Integer> initialMinus;
    private ArrayList<Integer> initialPlus;
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
            setWillNotDraw(false);
            setClipToPadding(false);
            this.shadowDrawable = Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow");
            setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            TLRPC$User user = MessagesController.getInstance(privacyControlActivity.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(privacyControlActivity.currentAccount).getClientUserId()));
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
                public /* synthetic */ boolean canPerformActions() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                }

                public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
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

                public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$TL_reactionCount tLRPC$TL_reactionCount) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tLRPC$TL_reactionCount);
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

                public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                }

                public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                }

                public /* synthetic */ String getAdminRank(int i) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, i);
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

                public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                }

                public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                }

                public /* synthetic */ void needReloadPolls() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
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
            if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
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
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
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
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PrivacyControlActivity.this.lambda$createView$2$PrivacyControlActivity(view, i);
            }
        });
        setMessageText();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$PrivacyControlActivity(View view, int i) {
        ArrayList<Integer> arrayList;
        int i2 = this.nobodyRow;
        int i3 = 0;
        boolean z = true;
        if (i == i2 || i == this.everybodyRow || i == this.myContactsRow) {
            int i4 = this.currentType;
            if (i == i2) {
                i3 = 1;
            } else if (i != this.everybodyRow) {
                i3 = i == this.myContactsRow ? 2 : i4;
            }
            if (i3 != i4) {
                this.currentType = i3;
                updateDoneButton();
                updateRows(true);
                return;
            }
            return;
        }
        int i5 = this.phoneContactsRow;
        if (i == i5 || i == this.phoneEverybodyRow) {
            int i6 = this.currentSubType;
            if (i != this.phoneEverybodyRow) {
                i3 = i == i5 ? 1 : i6;
            }
            if (i3 != i6) {
                this.currentSubType = i3;
                updateDoneButton();
                updateRows(true);
                return;
            }
            return;
        }
        int i7 = this.neverShareRow;
        if (i == i7 || i == this.alwaysShareRow) {
            if (i == i7) {
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
                groupCreateActivity.setDelegate((GroupCreateActivity.GroupCreateActivityDelegate) new GroupCreateActivity.GroupCreateActivityDelegate(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void didSelectUsers(ArrayList arrayList) {
                        PrivacyControlActivity.this.lambda$null$0$PrivacyControlActivity(this.f$1, arrayList);
                    }
                });
                presentFragment(groupCreateActivity);
                return;
            }
            boolean z2 = this.rulesType != 0;
            if (i != this.alwaysShareRow) {
                z = false;
            }
            PrivacyUsersActivity privacyUsersActivity = new PrivacyUsersActivity(0, arrayList, z2, z);
            privacyUsersActivity.setDelegate(new PrivacyUsersActivity.PrivacyActivityDelegate(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void didUpdateUserList(ArrayList arrayList, boolean z) {
                    PrivacyControlActivity.this.lambda$null$1$PrivacyControlActivity(this.f$1, arrayList, z);
                }
            });
            presentFragment(privacyUsersActivity);
        } else if (i == this.p2pRow) {
            presentFragment(new PrivacyControlActivity(3));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$PrivacyControlActivity(int i, ArrayList arrayList) {
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
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$PrivacyControlActivity(int i, ArrayList arrayList, boolean z) {
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
        } else if (i == NotificationCenter.emojiDidLoad) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_setPrivacy2, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(tLObject, tLRPC$TL_error);
                    }
                }, 2);
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
        } else {
            tLRPC$TL_account_setPrivacy.key = new TLRPC$TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TLRPC$TL_inputPrivacyValueAllowUsers tLRPC$TL_inputPrivacyValueAllowUsers = new TLRPC$TL_inputPrivacyValueAllowUsers();
            TLRPC$TL_inputPrivacyValueAllowChatParticipants tLRPC$TL_inputPrivacyValueAllowChatParticipants = new TLRPC$TL_inputPrivacyValueAllowChatParticipants();
            for (int i2 = 0; i2 < this.currentPlus.size(); i2++) {
                int intValue = this.currentPlus.get(i2).intValue();
                if (intValue > 0) {
                    TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(intValue));
                    if (!(user == null || (inputUser2 = MessagesController.getInstance(this.currentAccount).getInputUser(user)) == null)) {
                        tLRPC$TL_inputPrivacyValueAllowUsers.users.add(inputUser2);
                    }
                } else {
                    tLRPC$TL_inputPrivacyValueAllowChatParticipants.chats.add(Integer.valueOf(-intValue));
                }
            }
            tLRPC$TL_account_setPrivacy.rules.add(tLRPC$TL_inputPrivacyValueAllowUsers);
            tLRPC$TL_account_setPrivacy.rules.add(tLRPC$TL_inputPrivacyValueAllowChatParticipants);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TLRPC$TL_inputPrivacyValueDisallowUsers tLRPC$TL_inputPrivacyValueDisallowUsers = new TLRPC$TL_inputPrivacyValueDisallowUsers();
            TLRPC$TL_inputPrivacyValueDisallowChatParticipants tLRPC$TL_inputPrivacyValueDisallowChatParticipants = new TLRPC$TL_inputPrivacyValueDisallowChatParticipants();
            for (int i3 = 0; i3 < this.currentMinus.size(); i3++) {
                int intValue2 = this.currentMinus.get(i3).intValue();
                if (intValue2 > 0) {
                    TLRPC$User user2 = getMessagesController().getUser(Integer.valueOf(intValue2));
                    if (!(user2 == null || (inputUser = getMessagesController().getInputUser(user2)) == null)) {
                        tLRPC$TL_inputPrivacyValueDisallowUsers.users.add(inputUser);
                    }
                } else {
                    tLRPC$TL_inputPrivacyValueDisallowChatParticipants.chats.add(Integer.valueOf(-intValue2));
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
            alertDialog.setCanCacnel(false);
            alertDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_setPrivacy, new RequestDelegate(alertDialog) {
            public final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$6$PrivacyControlActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyCurrentPrivacySettings$4 */
    public /* synthetic */ void lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PrivacyControlActivity.this.lambda$null$3$PrivacyControlActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$PrivacyControlActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(((TLRPC$TL_account_privacyRules) tLObject).rules, 7);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyCurrentPrivacySettings$6 */
    public /* synthetic */ void lambda$applyCurrentPrivacySettings$6$PrivacyControlActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLRPC$TL_error, tLObject) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PrivacyControlActivity.this.lambda$null$5$PrivacyControlActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$PrivacyControlActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
                        this.currentPlus.add(Integer.valueOf(-tLRPC$TL_privacyValueAllowChatParticipants.chats.get(i2).intValue()));
                    }
                } else if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueDisallowChatParticipants) {
                    TLRPC$TL_privacyValueDisallowChatParticipants tLRPC$TL_privacyValueDisallowChatParticipants = (TLRPC$TL_privacyValueDisallowChatParticipants) tLRPC$PrivacyRule;
                    int size2 = tLRPC$TL_privacyValueDisallowChatParticipants.chats.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        this.currentMinus.add(Integer.valueOf(-tLRPC$TL_privacyValueDisallowChatParticipants.chats.get(i3).intValue()));
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
        int i;
        int i2 = this.alwaysShareRow;
        int i3 = this.neverShareRow;
        int i4 = this.phoneDetailRow;
        int i5 = this.detailRow;
        int i6 = this.currentType;
        boolean z2 = i6 == 1 && this.currentSubType == 1;
        this.rowCount = 0;
        int i7 = this.rulesType;
        if (i7 == 5) {
            this.rowCount = 0 + 1;
            this.messageRow = 0;
        } else {
            this.messageRow = -1;
        }
        int i8 = this.rowCount;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.sectionRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.everybodyRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.myContactsRow = i10;
        if (i7 == 0 || i7 == 2 || i7 == 3 || i7 == 5 || i7 == 6) {
            this.rowCount = i11 + 1;
            this.nobodyRow = i11;
        } else {
            this.nobodyRow = -1;
        }
        if (i7 == 6 && i6 == 1) {
            int i12 = this.rowCount;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.phoneDetailRow = i12;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.phoneSectionRow = i13;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.phoneEverybodyRow = i14;
            this.rowCount = i15 + 1;
            this.phoneContactsRow = i15;
        } else {
            this.phoneDetailRow = -1;
            this.phoneSectionRow = -1;
            this.phoneEverybodyRow = -1;
            this.phoneContactsRow = -1;
        }
        int i16 = this.rowCount;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.detailRow = i16;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.shareSectionRow = i17;
        if (i6 == 1 || i6 == 2) {
            this.rowCount = i18 + 1;
            this.alwaysShareRow = i18;
        } else {
            this.alwaysShareRow = -1;
        }
        if (i6 == 0 || i6 == 2) {
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.neverShareRow = i19;
        } else {
            this.neverShareRow = -1;
        }
        int i20 = this.rowCount;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.shareDetailRow = i20;
        if (i7 == 2) {
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.p2pSectionRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.p2pRow = i22;
            this.rowCount = i23 + 1;
            this.p2pDetailRow = i23;
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
            for (int i24 = 0; i24 < childCount; i24++) {
                View childAt = this.listView.getChildAt(i24);
                if ((childAt instanceof RadioCell) && (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) != null) {
                    int adapterPosition = findContainingViewHolder.getAdapterPosition();
                    RadioCell radioCell = (RadioCell) childAt;
                    int i25 = this.everybodyRow;
                    if (adapterPosition == i25 || adapterPosition == this.myContactsRow || adapterPosition == this.nobodyRow) {
                        radioCell.setChecked(this.currentType == (adapterPosition == i25 ? 0 : adapterPosition == this.myContactsRow ? 2 : 1), true);
                    } else {
                        if (adapterPosition == this.phoneContactsRow) {
                            i = 1;
                        } else {
                            int i26 = this.phoneEverybodyRow;
                            i = 0;
                        }
                        radioCell.setChecked(this.currentSubType == i, true);
                    }
                }
            }
            if (this.prevSubtypeContacts != z2) {
                this.listAdapter.notifyItemChanged(i5);
            }
            int i27 = this.alwaysShareRow;
            if ((i27 != -1 || i2 == -1 || this.neverShareRow == -1 || i3 != -1) && (i27 == -1 || i2 != -1 || this.neverShareRow != -1 || i3 == -1)) {
                if (i27 == -1 && i2 != -1) {
                    this.listAdapter.notifyItemRemoved(i2);
                } else if (i27 != -1 && i2 == -1) {
                    this.listAdapter.notifyItemInserted(i27);
                }
                int i28 = this.neverShareRow;
                if (i28 == -1 && i3 != -1) {
                    this.listAdapter.notifyItemRemoved(i3);
                    int i29 = this.phoneDetailRow;
                    if (i29 == -1 && i4 != -1) {
                        this.listAdapter.notifyItemRangeRemoved(i4, 4);
                    } else if (i29 != -1 && i4 == -1) {
                        this.listAdapter.notifyItemRangeInserted(i29, 4);
                    }
                } else if (i28 != -1 && i3 == -1) {
                    int i30 = this.phoneDetailRow;
                    if (i30 == -1 && i4 != -1) {
                        this.listAdapter.notifyItemRangeRemoved(i4, 4);
                    } else if (i30 != -1 && i4 == -1) {
                        this.listAdapter.notifyItemRangeInserted(i30, 4);
                    }
                    this.listAdapter.notifyItemInserted(this.neverShareRow);
                }
            } else {
                ListAdapter listAdapter3 = this.listAdapter;
                if (i27 != -1) {
                    i2 = i3;
                }
                listAdapter3.notifyItemChanged(i2);
                int i31 = this.phoneDetailRow;
                if (i31 == -1 && i4 != -1) {
                    this.listAdapter.notifyItemRangeRemoved(i4, 4);
                } else if (i31 != -1 && i4 == -1) {
                    this.listAdapter.notifyItemRangeInserted(i31, 4);
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
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(globalMainSettings) {
                        public final /* synthetic */ SharedPreferences f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PrivacyControlActivity.this.lambda$processDone$7$PrivacyControlActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processDone$7 */
    public /* synthetic */ void lambda$processDone$7$PrivacyControlActivity(SharedPreferences sharedPreferences, DialogInterface dialogInterface, int i) {
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
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacyControlActivity.this.lambda$checkDiscard$8$PrivacyControlActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacyControlActivity.this.lambda$checkDiscard$9$PrivacyControlActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$8 */
    public /* synthetic */ void lambda$checkDiscard$8$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$9 */
    public /* synthetic */ void lambda$checkDiscard$9$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
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
            return adapterPosition == PrivacyControlActivity.this.nobodyRow || adapterPosition == PrivacyControlActivity.this.everybodyRow || adapterPosition == PrivacyControlActivity.this.myContactsRow || adapterPosition == PrivacyControlActivity.this.neverShareRow || adapterPosition == PrivacyControlActivity.this.alwaysShareRow || (adapterPosition == PrivacyControlActivity.this.p2pRow && !ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3));
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

        private int getUsersCount(ArrayList<Integer> arrayList) {
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                int intValue = arrayList.get(i2).intValue();
                if (intValue > 0) {
                    i++;
                } else {
                    TLRPC$Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Integer.valueOf(-intValue));
                    if (chat != null) {
                        i += chat.participants_count;
                    }
                }
            }
            return i;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v14, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v2 */
        /* JADX WARNING: type inference failed for: r3v4 */
        /* JADX WARNING: type inference failed for: r3v5, types: [int] */
        /* JADX WARNING: type inference failed for: r3v6 */
        /* JADX WARNING: type inference failed for: r3v7 */
        /* JADX WARNING: type inference failed for: r3v9 */
        /* JADX WARNING: type inference failed for: r3v11 */
        /* JADX WARNING: type inference failed for: r3v13 */
        /* JADX WARNING: type inference failed for: r3v15 */
        /* JADX WARNING: type inference failed for: r3v17 */
        /* JADX WARNING: type inference failed for: r3v19 */
        /* JADX WARNING: Code restructure failed: missing block: B:150:0x0379, code lost:
            if (org.telegram.ui.PrivacyControlActivity.access$1900(r10.this$0) == 2) goto L_0x02db;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:152:0x0383, code lost:
            if (r12 == org.telegram.ui.PrivacyControlActivity.access$2700(r10.this$0)) goto L_0x0385;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:155:0x038a  */
        /* JADX WARNING: Removed duplicated region for block: B:211:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
                r10 = this;
                int r0 = r11.getItemViewType()
                r1 = -1
                r2 = 3
                r3 = 0
                r4 = 1
                if (r0 == 0) goto L_0x03ac
                r5 = 4
                r6 = 5
                r7 = 6
                r8 = 2
                if (r0 == r4) goto L_0x021a
                if (r0 == r8) goto L_0x013a
                if (r0 == r2) goto L_0x0016
                goto L_0x0495
            L_0x0016:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.RadioCell r11 = (org.telegram.ui.Cells.RadioCell) r11
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.everybodyRow
                r5 = 2131625902(0x7f0e07ae, float:1.8879025E38)
                java.lang.String r6 = "LastSeenContacts"
                r7 = 2131625908(0x7f0e07b4, float:1.8879037E38)
                java.lang.String r9 = "LastSeenEverybody"
                if (r12 == r0) goto L_0x0072
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.myContactsRow
                if (r12 == r0) goto L_0x0072
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.nobodyRow
                if (r12 != r0) goto L_0x003d
                goto L_0x0072
            L_0x003d:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneContactsRow
                if (r12 != r0) goto L_0x0058
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r6, r5)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != r4) goto L_0x0052
                goto L_0x0053
            L_0x0052:
                r4 = 0
            L_0x0053:
                r11.setText(r12, r4, r3)
                goto L_0x0495
            L_0x0058:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneEverybodyRow
                if (r12 != r0) goto L_0x0495
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r9, r7)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != 0) goto L_0x006d
                r3 = 1
            L_0x006d:
                r11.setText(r12, r3, r4)
                goto L_0x0495
            L_0x0072:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.everybodyRow
                if (r12 != r0) goto L_0x00ab
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x0099
                r12 = 2131626534(0x7f0e0a26, float:1.8880307E38)
                java.lang.String r0 = "P2PEverybody"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != 0) goto L_0x0094
                r3 = 1
            L_0x0094:
                r11.setText(r12, r3, r4)
                goto L_0x0495
            L_0x0099:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r9, r7)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != 0) goto L_0x00a6
                r3 = 1
            L_0x00a6:
                r11.setText(r12, r3, r4)
                goto L_0x0495
            L_0x00ab:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.myContactsRow
                if (r12 != r0) goto L_0x00fa
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x00dd
                r12 = 2131626529(0x7f0e0a21, float:1.8880297E38)
                java.lang.String r0 = "P2PContacts"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r8) goto L_0x00ce
                r0 = 1
                goto L_0x00cf
            L_0x00ce:
                r0 = 0
            L_0x00cf:
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.nobodyRow
                if (r2 == r1) goto L_0x00d8
                r3 = 1
            L_0x00d8:
                r11.setText(r12, r0, r3)
                goto L_0x0495
            L_0x00dd:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r6, r5)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r8) goto L_0x00eb
                r0 = 1
                goto L_0x00ec
            L_0x00eb:
                r0 = 0
            L_0x00ec:
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.nobodyRow
                if (r2 == r1) goto L_0x00f5
                r3 = 1
            L_0x00f5:
                r11.setText(r12, r0, r3)
                goto L_0x0495
            L_0x00fa:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.nobodyRow
                if (r12 != r0) goto L_0x0495
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x0122
                r12 = 2131626536(0x7f0e0a28, float:1.888031E38)
                java.lang.String r0 = "P2PNobody"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r4) goto L_0x011c
                goto L_0x011d
            L_0x011c:
                r4 = 0
            L_0x011d:
                r11.setText(r12, r4, r3)
                goto L_0x0495
            L_0x0122:
                r12 = 2131625911(0x7f0e07b7, float:1.8879043E38)
                java.lang.String r0 = "LastSeenNobody"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r4) goto L_0x0134
                goto L_0x0135
            L_0x0134:
                r4 = 0
            L_0x0135:
                r11.setText(r12, r4, r3)
                goto L_0x0495
            L_0x013a:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.HeaderCell r11 = (org.telegram.ui.Cells.HeaderCell) r11
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.sectionRow
                if (r12 != r0) goto L_0x01d8
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r7) goto L_0x015c
                r12 = 2131626995(0x7f0e0bf3, float:1.8881242E38)
                java.lang.String r0 = "PrivacyPhoneTitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x015c:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r6) goto L_0x0172
                r12 = 2131626982(0x7f0e0be6, float:1.8881216E38)
                java.lang.String r0 = "PrivacyForwardsTitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x0172:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r5) goto L_0x0188
                r12 = 2131627003(0x7f0e0bfb, float:1.8881258E38)
                java.lang.String r0 = "PrivacyProfilePhotoTitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x0188:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x019e
                r12 = 2131626533(0x7f0e0a25, float:1.8880305E38)
                java.lang.String r0 = "P2PEnabledWith"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x019e:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r8) goto L_0x01b4
                r12 = 2131628172(0x7f0e108c, float:1.888363E38)
                java.lang.String r0 = "WhoCanCallMe"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x01b4:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r4) goto L_0x01ca
                r12 = 2131628167(0x7f0e1087, float:1.888362E38)
                java.lang.String r0 = "WhoCanAddMe"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x01ca:
                r12 = 2131625913(0x7f0e07b9, float:1.8879047E38)
                java.lang.String r0 = "LastSeenTitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x01d8:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.shareSectionRow
                if (r12 != r0) goto L_0x01ee
                r12 = 2131624199(0x7f0e0107, float:1.887557E38)
                java.lang.String r0 = "AddExceptions"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x01ee:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pSectionRow
                if (r12 != r0) goto L_0x0204
                r12 = 2131626986(0x7f0e0bea, float:1.8881224E38)
                java.lang.String r0 = "PrivacyP2PHeader"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x0204:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneSectionRow
                if (r12 != r0) goto L_0x0495
                r12 = 2131626996(0x7f0e0bf4, float:1.8881244E38)
                java.lang.String r0 = "PrivacyPhoneTitle2"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0495
            L_0x021a:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r11 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r11
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.detailRow
                r1 = 2131165449(0x7var_, float:1.7945115E38)
                r9 = 2131165450(0x7var_a, float:1.7945117E38)
                if (r12 != r0) goto L_0x02e0
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r7) goto L_0x0266
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r12.currentType
                if (r0 != r4) goto L_0x0245
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != r4) goto L_0x0245
                r3 = 1
            L_0x0245:
                boolean unused = r12.prevSubtypeContacts = r3
                if (r3 == 0) goto L_0x0258
                r12 = 2131626994(0x7f0e0bf2, float:1.888124E38)
                java.lang.String r0 = "PrivacyPhoneInfo3"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x02db
            L_0x0258:
                r12 = 2131626992(0x7f0e0bf0, float:1.8881236E38)
                java.lang.String r0 = "PrivacyPhoneInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x02db
            L_0x0266:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r6) goto L_0x027b
                r12 = 2131626978(0x7f0e0be2, float:1.8881207E38)
                java.lang.String r0 = "PrivacyForwardsInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x02db
            L_0x027b:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r5) goto L_0x0290
                r12 = 2131627001(0x7f0e0bf9, float:1.8881254E38)
                java.lang.String r0 = "PrivacyProfilePhotoInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x02db
            L_0x0290:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x02a5
                r12 = 2131626969(0x7f0e0bd9, float:1.888119E38)
                java.lang.String r0 = "PrivacyCallsP2PHelp"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x02db
            L_0x02a5:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r8) goto L_0x02ba
                r12 = 2131628173(0x7f0e108d, float:1.8883631E38)
                java.lang.String r0 = "WhoCanCallMeInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x02db
            L_0x02ba:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r4) goto L_0x02cf
                r12 = 2131628168(0x7f0e1088, float:1.8883621E38)
                java.lang.String r0 = "WhoCanAddMeInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x02db
            L_0x02cf:
                r12 = 2131625030(0x7f0e0446, float:1.8877256E38)
                java.lang.String r0 = "CustomHelp"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
            L_0x02db:
                r3 = 2131165449(0x7var_, float:1.7945115E38)
                goto L_0x0388
            L_0x02e0:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.shareDetailRow
                if (r12 != r0) goto L_0x037d
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r7) goto L_0x02fe
                r12 = 2131626993(0x7f0e0bf1, float:1.8881238E38)
                java.lang.String r0 = "PrivacyPhoneInfo2"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0373
            L_0x02fe:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r6) goto L_0x0313
                r12 = 2131626979(0x7f0e0be3, float:1.888121E38)
                java.lang.String r0 = "PrivacyForwardsInfo2"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0373
            L_0x0313:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r5) goto L_0x0328
                r12 = 2131627002(0x7f0e0bfa, float:1.8881256E38)
                java.lang.String r0 = "PrivacyProfilePhotoInfo2"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0373
            L_0x0328:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x033d
                r12 = 2131625032(0x7f0e0448, float:1.887726E38)
                java.lang.String r0 = "CustomP2PInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0373
            L_0x033d:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r8) goto L_0x0352
                r12 = 2131625029(0x7f0e0445, float:1.8877254E38)
                java.lang.String r0 = "CustomCallInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0373
            L_0x0352:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r4) goto L_0x0367
                r12 = 2131625033(0x7f0e0449, float:1.8877263E38)
                java.lang.String r0 = "CustomShareInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0373
            L_0x0367:
                r12 = 2131625034(0x7f0e044a, float:1.8877265E38)
                java.lang.String r0 = "CustomShareSettingsHelp"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
            L_0x0373:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r8) goto L_0x0385
                goto L_0x02db
            L_0x037d:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pDetailRow
                if (r12 != r0) goto L_0x0388
            L_0x0385:
                r3 = 2131165450(0x7var_a, float:1.7945117E38)
            L_0x0388:
                if (r3 == 0) goto L_0x0495
                android.content.Context r12 = r10.mContext
                java.lang.String r0 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r3, (java.lang.String) r0)
                org.telegram.ui.Components.CombinedDrawable r0 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable
                java.lang.String r2 = "windowBackgroundGray"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r1.<init>(r2)
                r0.<init>(r1, r12)
                r0.setFullsize(r4)
                r11.setBackgroundDrawable(r0)
                goto L_0x0495
            L_0x03ac:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextSettingsCell r11 = (org.telegram.ui.Cells.TextSettingsCell) r11
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.alwaysShareRow
                java.lang.String r5 = "Users"
                r6 = 2131625279(0x7f0e053f, float:1.8877761E38)
                java.lang.String r7 = "EmpryUsersPlaceholder"
                if (r12 != r0) goto L_0x0414
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r12 = r12.currentPlus
                int r12 = r12.size()
                if (r12 == 0) goto L_0x03da
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r12 = r12.currentPlus
                int r12 = r10.getUsersCount(r12)
                java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12)
                goto L_0x03de
            L_0x03da:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r7, r6)
            L_0x03de:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.rulesType
                if (r0 == 0) goto L_0x03fd
                r0 = 2131624248(0x7f0e0138, float:1.887567E38)
                java.lang.String r2 = "AlwaysAllow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.neverShareRow
                if (r2 == r1) goto L_0x03f8
                r3 = 1
            L_0x03f8:
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x0495
            L_0x03fd:
                r0 = 2131624249(0x7f0e0139, float:1.8875672E38)
                java.lang.String r2 = "AlwaysShareWith"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.neverShareRow
                if (r2 == r1) goto L_0x040f
                r3 = 1
            L_0x040f:
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x0495
            L_0x0414:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.neverShareRow
                if (r12 != r0) goto L_0x045d
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r12 = r12.currentMinus
                int r12 = r12.size()
                if (r12 == 0) goto L_0x0437
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r12 = r12.currentMinus
                int r12 = r10.getUsersCount(r12)
                java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12)
                goto L_0x043b
            L_0x0437:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r7, r6)
            L_0x043b:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.rulesType
                if (r0 == 0) goto L_0x0450
                r0 = 2131626197(0x7f0e08d5, float:1.8879623E38)
                java.lang.String r1 = "NeverAllow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x0495
            L_0x0450:
                r0 = 2131626198(0x7f0e08d6, float:1.8879625E38)
                java.lang.String r1 = "NeverShareWith"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x0495
            L_0x045d:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pRow
                if (r12 != r0) goto L_0x0495
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.currentAccount
                org.telegram.messenger.ContactsController r12 = org.telegram.messenger.ContactsController.getInstance(r12)
                boolean r12 = r12.getLoadingPrivicyInfo(r2)
                if (r12 == 0) goto L_0x047f
                r12 = 2131625967(0x7f0e07ef, float:1.8879157E38)
                java.lang.String r0 = "Loading"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                goto L_0x0489
            L_0x047f:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                org.telegram.messenger.AccountInstance r12 = r12.getAccountInstance()
                java.lang.String r12 = org.telegram.ui.PrivacySettingsActivity.formatRulesString(r12, r2)
            L_0x0489:
                r0 = 2131626985(0x7f0e0be9, float:1.8881222E38)
                java.lang.String r1 = "PrivacyP2P2"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r11.setTextAndValue(r0, r12, r3)
            L_0x0495:
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
