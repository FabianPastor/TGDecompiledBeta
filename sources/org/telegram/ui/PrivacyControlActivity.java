package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Shader.TileMode;
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
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_account_privacyRules;
import org.telegram.tgnet.TLRPC.TL_account_setPrivacy;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyAddedByPhone;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyForwards;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneNumber;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneP2P;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowChatParticipants;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowContacts;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowChatParticipants;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowChatParticipants;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowContacts;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowChatParticipants;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.TL_reactionCount;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate.-CC;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable.Disposable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int PRIVACY_RULES_TYPE_ADDED_BY_PHONE = 7;
    public static final int PRIVACY_RULES_TYPE_CALLS = 2;
    public static final int PRIVACY_RULES_TYPE_FORWARDS = 5;
    public static final int PRIVACY_RULES_TYPE_INVITE = 1;
    public static final int PRIVACY_RULES_TYPE_LASTSEEN = 0;
    public static final int PRIVACY_RULES_TYPE_P2P = 3;
    public static final int PRIVACY_RULES_TYPE_PHONE = 6;
    public static final int PRIVACY_RULES_TYPE_PHOTO = 4;
    public static final int TYPE_CONTACTS = 2;
    public static final int TYPE_EVERYBODY = 0;
    public static final int TYPE_NOBODY = 1;
    private static final int done_button = 1;
    private int alwaysShareRow;
    private ArrayList<Integer> currentMinus;
    private ArrayList<Integer> currentPlus;
    private int currentSubType;
    private int currentType;
    private int detailRow;
    private View doneButton;
    private int everybodyRow;
    private ArrayList<Integer> initialMinus;
    private ArrayList<Integer> initialPlus;
    private int initialRulesSubType;
    private int initialRulesType;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private MessageCell messageCell;
    private int messageRow;
    private int myContactsRow;
    private int neverShareRow;
    private int nobodyRow;
    private int p2pDetailRow;
    private int p2pRow;
    private int p2pSectionRow;
    private int phoneContactsRow;
    private int phoneDetailRow;
    private int phoneEverybodyRow;
    private int phoneSectionRow;
    private boolean prevSubtypeContacts;
    private int rowCount;
    private int rulesType;
    private int sectionRow;
    private int shareDetailRow;
    private int shareSectionRow;

    private class MessageCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private Disposable backgroundGradientDisposable;
        private ChatMessageCell cell;
        private HintView hintView;
        private final Runnable invalidateRunnable = new -$$Lambda$DlCLASSNAMEmB3j0kiLz-yohx_0UnaSV0(this);
        private MessageObject messageObject;
        private Drawable shadowDrawable;

        /* Access modifiers changed, original: protected */
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

        public MessageCell(Context context) {
            super(context);
            setWillNotDraw(false);
            setClipToPadding(false);
            this.shadowDrawable = Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow");
            setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            User user = MessagesController.getInstance(PrivacyControlActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(PrivacyControlActivity.this.currentAccount).getClientUserId()));
            TL_message tL_message = new TL_message();
            tL_message.message = LocaleController.getString("PrivacyForwardsMessageLine", NUM);
            tL_message.date = currentTimeMillis + 60;
            tL_message.dialog_id = 1;
            tL_message.flags = 261;
            tL_message.from_id = 0;
            tL_message.id = 1;
            tL_message.fwd_from = new TL_messageFwdHeader();
            tL_message.fwd_from.from_name = ContactsController.formatName(user.first_name, user.last_name);
            tL_message.media = new TL_messageMediaEmpty();
            tL_message.out = false;
            tL_message.to_id = new TL_peerUser();
            tL_message.to_id.user_id = UserConfig.getInstance(PrivacyControlActivity.this.currentAccount).getClientUserId();
            this.messageObject = new MessageObject(PrivacyControlActivity.this.currentAccount, tL_message, true);
            MessageObject messageObject = this.messageObject;
            messageObject.eventId = 1;
            messageObject.resetLayout();
            this.cell = new ChatMessageCell(context);
            this.cell.setDelegate(new ChatMessageCellDelegate(PrivacyControlActivity.this) {
                public /* synthetic */ boolean canPerformActions() {
                    return -CC.$default$canPerformActions(this);
                }

                public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                    -CC.$default$didLongPress(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                    -CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                }

                public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                    -CC.$default$didPressCancelSendButton(this, chatMessageCell);
                }

                public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i, float f, float f2) {
                    -CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                }

                public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                    -CC.$default$didPressHiddenForward(this, chatMessageCell);
                }

                public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                    -CC.$default$didPressImage(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                    -CC.$default$didPressInstantButton(this, chatMessageCell, i);
                }

                public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                    -CC.$default$didPressOther(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TL_reactionCount tL_reactionCount) {
                    -CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount);
                }

                public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                    -CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                }

                public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                    -CC.$default$didPressShare(this, chatMessageCell);
                }

                public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                    -CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
                }

                public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, User user, float f, float f2) {
                    -CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                }

                public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                    -CC.$default$didPressViaBot(this, chatMessageCell, str);
                }

                public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList<TL_pollAnswer> arrayList, int i, int i2, int i3) {
                    -CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                }

                public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                    -CC.$default$didStartVideoStream(this, messageObject);
                }

                public /* synthetic */ String getAdminRank(int i) {
                    return -CC.$default$getAdminRank(this, i);
                }

                public /* synthetic */ ChatListTextSelectionHelper getTextSelectionHelper() {
                    return -CC.$default$getTextSelectionHelper(this);
                }

                public /* synthetic */ boolean hasSelectedMessages() {
                    return -CC.$default$hasSelectedMessages(this);
                }

                public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                    -CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                }

                public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                    return -CC.$default$needPlayMessage(this, messageObject);
                }

                public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                    -CC.$default$setShouldNotRepeatSticker(this, messageObject);
                }

                public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                    return -CC.$default$shouldRepeatSticker(this, messageObject);
                }

                public /* synthetic */ void videoTimerReached() {
                    -CC.$default$videoTimerReached(this);
                }
            });
            ChatMessageCell chatMessageCell = this.cell;
            chatMessageCell.isChat = false;
            chatMessageCell.setFullyDraw(true);
            this.cell.setMessageObject(this.messageObject, null, false, false);
            addView(this.cell, LayoutHelper.createLinear(-1, -2));
            this.hintView = new HintView(context, 1, true);
            addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.hintView.showForMessageCell(this.cell, false);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
            if (!(cachedWallpaperNonBlocking == null || this.backgroundDrawable == cachedWallpaperNonBlocking)) {
                Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
                this.backgroundDrawable = cachedWallpaperNonBlocking;
            }
            cachedWallpaperNonBlocking = this.backgroundDrawable;
            if ((cachedWallpaperNonBlocking instanceof ColorDrawable) || (cachedWallpaperNonBlocking instanceof GradientDrawable)) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                cachedWallpaperNonBlocking = this.backgroundDrawable;
                if (cachedWallpaperNonBlocking instanceof BackgroundGradientDrawable) {
                    this.backgroundGradientDisposable = ((BackgroundGradientDrawable) cachedWallpaperNonBlocking).drawExactBoundsSize(canvas, this);
                } else {
                    cachedWallpaperNonBlocking.draw(canvas);
                }
            } else if (!(cachedWallpaperNonBlocking instanceof BitmapDrawable)) {
                super.onDraw(canvas);
            } else if (((BitmapDrawable) cachedWallpaperNonBlocking).getTileModeX() == TileMode.REPEAT) {
                canvas.save();
                float f = 2.0f / AndroidUtilities.density;
                canvas.scale(f, f);
                this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            } else {
                int measuredHeight = getMeasuredHeight();
                float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                float intrinsicHeight = ((float) measuredHeight) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                if (measuredWidth < intrinsicHeight) {
                    measuredWidth = intrinsicHeight;
                }
                int ceil = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * measuredWidth));
                int ceil2 = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * measuredWidth));
                int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
                measuredHeight = (measuredHeight - ceil2) / 2;
                canvas.save();
                canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                this.backgroundDrawable.setBounds(measuredWidth2, measuredHeight, ceil + measuredWidth2, ceil2 + measuredHeight);
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
        }

        /* Access modifiers changed, original: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Disposable disposable = this.backgroundGradientDisposable;
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

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PrivacyControlActivity.this.nobodyRow || adapterPosition == PrivacyControlActivity.this.everybodyRow || adapterPosition == PrivacyControlActivity.this.myContactsRow || adapterPosition == PrivacyControlActivity.this.neverShareRow || adapterPosition == PrivacyControlActivity.this.alwaysShareRow || (adapterPosition == PrivacyControlActivity.this.p2pRow && !ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3));
        }

        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textInfoPrivacyCell;
            View headerCell;
            String str = "windowBackgroundWhite";
            if (i != 0) {
                if (i == 1) {
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                } else if (i == 2) {
                    headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                } else if (i == 3) {
                    headerCell = new RadioCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                } else if (i != 4) {
                    textInfoPrivacyCell = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    textInfoPrivacyCell.setBackgroundDrawable(combinedDrawable);
                } else {
                    textInfoPrivacyCell = PrivacyControlActivity.this.messageCell;
                }
                return new Holder(textInfoPrivacyCell);
            }
            headerCell = new TextSettingsCell(this.mContext);
            headerCell.setBackgroundColor(Theme.getColor(str));
            textInfoPrivacyCell = headerCell;
            return new Holder(textInfoPrivacyCell);
        }

        private int getUsersCount(ArrayList<Integer> arrayList) {
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                int intValue = ((Integer) arrayList.get(i2)).intValue();
                if (intValue > 0) {
                    i++;
                } else {
                    Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Integer.valueOf(-intValue));
                    if (chat != null) {
                        i += chat.participants_count;
                    }
                }
            }
            return i;
        }

        /* JADX WARNING: Missing block: B:149:0x0381, code skipped:
            if (org.telegram.ui.PrivacyControlActivity.access$1900(r10.this$0) == 2) goto L_0x0391;
     */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
            r10 = this;
            r0 = r11.getItemViewType();
            r1 = -1;
            r2 = 3;
            r3 = 0;
            r4 = 1;
            if (r0 == 0) goto L_0x03b5;
        L_0x000a:
            r5 = 4;
            r6 = 5;
            r7 = 6;
            r8 = 2;
            if (r0 == r4) goto L_0x021f;
        L_0x0010:
            if (r0 == r8) goto L_0x013f;
        L_0x0012:
            if (r0 == r2) goto L_0x0016;
        L_0x0014:
            goto L_0x049e;
        L_0x0016:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.RadioCell) r11;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.everybodyRow;
            r5 = NUM; // 0x7f0e05cb float:1.8878045E38 double:1.0531628893E-314;
            r6 = "LastSeenEverybody";
            if (r12 == r0) goto L_0x0072;
        L_0x0027:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.myContactsRow;
            if (r12 == r0) goto L_0x0072;
        L_0x002f:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.nobodyRow;
            if (r12 != r0) goto L_0x0038;
        L_0x0037:
            goto L_0x0072;
        L_0x0038:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.phoneContactsRow;
            if (r12 != r0) goto L_0x0058;
        L_0x0040:
            r12 = NUM; // 0x7f0e05c5 float:1.8878033E38 double:1.0531628864E-314;
            r0 = "LastSeenContacts";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentSubType;
            if (r0 != r4) goto L_0x0052;
        L_0x0051:
            goto L_0x0053;
        L_0x0052:
            r4 = 0;
        L_0x0053:
            r11.setText(r12, r4, r3);
            goto L_0x049e;
        L_0x0058:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.phoneEverybodyRow;
            if (r12 != r0) goto L_0x049e;
        L_0x0060:
            r12 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentSubType;
            if (r0 != 0) goto L_0x006d;
        L_0x006c:
            r3 = 1;
        L_0x006d:
            r11.setText(r12, r3, r4);
            goto L_0x049e;
        L_0x0072:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.everybodyRow;
            if (r12 != r0) goto L_0x00ab;
        L_0x007a:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x0099;
        L_0x0082:
            r12 = NUM; // 0x7f0e07a2 float:1.8879E38 double:1.053163122E-314;
            r0 = "P2PEverybody";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentType;
            if (r0 != 0) goto L_0x0094;
        L_0x0093:
            r3 = 1;
        L_0x0094:
            r11.setText(r12, r3, r4);
            goto L_0x049e;
        L_0x0099:
            r12 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentType;
            if (r0 != 0) goto L_0x00a6;
        L_0x00a5:
            r3 = 1;
        L_0x00a6:
            r11.setText(r12, r3, r4);
            goto L_0x049e;
        L_0x00ab:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.myContactsRow;
            if (r12 != r0) goto L_0x00ff;
        L_0x00b3:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x00dd;
        L_0x00bb:
            r12 = NUM; // 0x7f0e079d float:1.887899E38 double:1.0531631196E-314;
            r0 = "P2PContacts";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentType;
            if (r0 != r8) goto L_0x00ce;
        L_0x00cc:
            r0 = 1;
            goto L_0x00cf;
        L_0x00ce:
            r0 = 0;
        L_0x00cf:
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.nobodyRow;
            if (r2 == r1) goto L_0x00d8;
        L_0x00d7:
            r3 = 1;
        L_0x00d8:
            r11.setText(r12, r0, r3);
            goto L_0x049e;
        L_0x00dd:
            r12 = NUM; // 0x7f0e05c5 float:1.8878033E38 double:1.0531628864E-314;
            r0 = "LastSeenContacts";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentType;
            if (r0 != r8) goto L_0x00f0;
        L_0x00ee:
            r0 = 1;
            goto L_0x00f1;
        L_0x00f0:
            r0 = 0;
        L_0x00f1:
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.nobodyRow;
            if (r2 == r1) goto L_0x00fa;
        L_0x00f9:
            r3 = 1;
        L_0x00fa:
            r11.setText(r12, r0, r3);
            goto L_0x049e;
        L_0x00ff:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.nobodyRow;
            if (r12 != r0) goto L_0x049e;
        L_0x0107:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x0127;
        L_0x010f:
            r12 = NUM; // 0x7f0e07a4 float:1.8879005E38 double:1.053163123E-314;
            r0 = "P2PNobody";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentType;
            if (r0 != r4) goto L_0x0121;
        L_0x0120:
            goto L_0x0122;
        L_0x0121:
            r4 = 0;
        L_0x0122:
            r11.setText(r12, r4, r3);
            goto L_0x049e;
        L_0x0127:
            r12 = NUM; // 0x7f0e05ce float:1.8878052E38 double:1.053162891E-314;
            r0 = "LastSeenNobody";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentType;
            if (r0 != r4) goto L_0x0139;
        L_0x0138:
            goto L_0x013a;
        L_0x0139:
            r4 = 0;
        L_0x013a:
            r11.setText(r12, r4, r3);
            goto L_0x049e;
        L_0x013f:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.HeaderCell) r11;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.sectionRow;
            if (r12 != r0) goto L_0x01dd;
        L_0x014b:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r7) goto L_0x0161;
        L_0x0153:
            r12 = NUM; // 0x7f0e092e float:1.8879804E38 double:1.0531633177E-314;
            r0 = "PrivacyPhoneTitle";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x0161:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r6) goto L_0x0177;
        L_0x0169:
            r12 = NUM; // 0x7f0e0921 float:1.8879778E38 double:1.0531633113E-314;
            r0 = "PrivacyForwardsTitle";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x0177:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r5) goto L_0x018d;
        L_0x017f:
            r12 = NUM; // 0x7f0e0936 float:1.887982E38 double:1.0531633216E-314;
            r0 = "PrivacyProfilePhotoTitle";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x018d:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x01a3;
        L_0x0195:
            r12 = NUM; // 0x7f0e07a1 float:1.8878999E38 double:1.0531631215E-314;
            r0 = "P2PEnabledWith";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x01a3:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r8) goto L_0x01b9;
        L_0x01ab:
            r12 = NUM; // 0x7f0e0cc7 float:1.8881672E38 double:1.0531637727E-314;
            r0 = "WhoCanCallMe";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x01b9:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r4) goto L_0x01cf;
        L_0x01c1:
            r12 = NUM; // 0x7f0e0cc2 float:1.8881662E38 double:1.0531637702E-314;
            r0 = "WhoCanAddMe";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x01cf:
            r12 = NUM; // 0x7f0e05d0 float:1.8878056E38 double:1.053162892E-314;
            r0 = "LastSeenTitle";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x01dd:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.shareSectionRow;
            if (r12 != r0) goto L_0x01f3;
        L_0x01e5:
            r12 = NUM; // 0x7f0e00b3 float:1.88754E38 double:1.053162245E-314;
            r0 = "AddExceptions";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x01f3:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pSectionRow;
            if (r12 != r0) goto L_0x0209;
        L_0x01fb:
            r12 = NUM; // 0x7f0e0925 float:1.8879786E38 double:1.053163313E-314;
            r0 = "PrivacyP2PHeader";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x0209:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.phoneSectionRow;
            if (r12 != r0) goto L_0x049e;
        L_0x0211:
            r12 = NUM; // 0x7f0e092f float:1.8879806E38 double:1.053163318E-314;
            r0 = "PrivacyPhoneTitle2";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x049e;
        L_0x021f:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r11;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.detailRow;
            r1 = NUM; // 0x7var_df float:1.794503E38 double:1.052935613E-314;
            r9 = NUM; // 0x7var_e0 float:1.7945032E38 double:1.0529356137E-314;
            if (r12 != r0) goto L_0x02e8;
        L_0x0231:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r7) goto L_0x026c;
        L_0x0239:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r12.currentType;
            if (r0 != r4) goto L_0x024a;
        L_0x0241:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentSubType;
            if (r0 != r4) goto L_0x024a;
        L_0x0249:
            r3 = 1;
        L_0x024a:
            r12 = r12.prevSubtypeContacts = r3;
            if (r12 == 0) goto L_0x025e;
        L_0x0250:
            r12 = NUM; // 0x7f0e092d float:1.8879802E38 double:1.053163317E-314;
            r0 = "PrivacyPhoneInfo3";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0391;
        L_0x025e:
            r12 = NUM; // 0x7f0e092b float:1.8879798E38 double:1.053163316E-314;
            r0 = "PrivacyPhoneInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0391;
        L_0x026c:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r6) goto L_0x0282;
        L_0x0274:
            r12 = NUM; // 0x7f0e091d float:1.887977E38 double:1.0531633093E-314;
            r0 = "PrivacyForwardsInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0391;
        L_0x0282:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r5) goto L_0x0298;
        L_0x028a:
            r12 = NUM; // 0x7f0e0934 float:1.8879816E38 double:1.0531633206E-314;
            r0 = "PrivacyProfilePhotoInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0391;
        L_0x0298:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x02ae;
        L_0x02a0:
            r12 = NUM; // 0x7f0e0914 float:1.8879751E38 double:1.053163305E-314;
            r0 = "PrivacyCallsP2PHelp";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0391;
        L_0x02ae:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r8) goto L_0x02c4;
        L_0x02b6:
            r12 = NUM; // 0x7f0e0cc8 float:1.8881674E38 double:1.053163773E-314;
            r0 = "WhoCanCallMeInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0391;
        L_0x02c4:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r4) goto L_0x02da;
        L_0x02cc:
            r12 = NUM; // 0x7f0e0cc3 float:1.8881664E38 double:1.0531637707E-314;
            r0 = "WhoCanAddMeInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0391;
        L_0x02da:
            r12 = NUM; // 0x7f0e0363 float:1.8876796E38 double:1.053162585E-314;
            r0 = "CustomHelp";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0391;
        L_0x02e8:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.shareDetailRow;
            if (r12 != r0) goto L_0x0384;
        L_0x02f0:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r7) goto L_0x0306;
        L_0x02f8:
            r12 = NUM; // 0x7f0e092c float:1.88798E38 double:1.0531633167E-314;
            r0 = "PrivacyPhoneInfo2";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x037b;
        L_0x0306:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r6) goto L_0x031b;
        L_0x030e:
            r12 = NUM; // 0x7f0e091e float:1.8879771E38 double:1.05316331E-314;
            r0 = "PrivacyForwardsInfo2";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x037b;
        L_0x031b:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r5) goto L_0x0330;
        L_0x0323:
            r12 = NUM; // 0x7f0e0935 float:1.8879818E38 double:1.053163321E-314;
            r0 = "PrivacyProfilePhotoInfo2";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x037b;
        L_0x0330:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x0345;
        L_0x0338:
            r12 = NUM; // 0x7f0e0365 float:1.88768E38 double:1.053162586E-314;
            r0 = "CustomP2PInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x037b;
        L_0x0345:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r8) goto L_0x035a;
        L_0x034d:
            r12 = NUM; // 0x7f0e0362 float:1.8876794E38 double:1.0531625845E-314;
            r0 = "CustomCallInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x037b;
        L_0x035a:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r4) goto L_0x036f;
        L_0x0362:
            r12 = NUM; // 0x7f0e0366 float:1.8876802E38 double:1.0531625865E-314;
            r0 = "CustomShareInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x037b;
        L_0x036f:
            r12 = NUM; // 0x7f0e0367 float:1.8876804E38 double:1.053162587E-314;
            r0 = "CustomShareSettingsHelp";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
        L_0x037b:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r8) goto L_0x038c;
        L_0x0383:
            goto L_0x0391;
        L_0x0384:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pDetailRow;
            if (r12 != r0) goto L_0x0390;
        L_0x038c:
            r1 = NUM; // 0x7var_e0 float:1.7945032E38 double:1.0529356137E-314;
            goto L_0x0391;
        L_0x0390:
            r1 = 0;
        L_0x0391:
            if (r1 == 0) goto L_0x049e;
        L_0x0393:
            r12 = r10.mContext;
            r0 = "windowBackgroundGrayShadow";
            r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r12, r1, r0);
            r0 = new org.telegram.ui.Components.CombinedDrawable;
            r1 = new android.graphics.drawable.ColorDrawable;
            r2 = "windowBackgroundGray";
            r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
            r1.<init>(r2);
            r0.<init>(r1, r12);
            r0.setFullsize(r4);
            r11.setBackgroundDrawable(r0);
            goto L_0x049e;
        L_0x03b5:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.TextSettingsCell) r11;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.alwaysShareRow;
            r5 = "Users";
            r6 = NUM; // 0x7f0e0429 float:1.8877198E38 double:1.053162683E-314;
            r7 = "EmpryUsersPlaceholder";
            if (r12 != r0) goto L_0x041d;
        L_0x03c8:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentPlus;
            r12 = r12.size();
            if (r12 == 0) goto L_0x03e3;
        L_0x03d4:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentPlus;
            r12 = r10.getUsersCount(r12);
            r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12);
            goto L_0x03e7;
        L_0x03e3:
            r12 = org.telegram.messenger.LocaleController.getString(r7, r6);
        L_0x03e7:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.rulesType;
            if (r0 == 0) goto L_0x0406;
        L_0x03ef:
            r0 = NUM; // 0x7f0e00df float:1.887549E38 double:1.053162267E-314;
            r2 = "AlwaysAllow";
            r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.neverShareRow;
            if (r2 == r1) goto L_0x0401;
        L_0x0400:
            r3 = 1;
        L_0x0401:
            r11.setTextAndValue(r0, r12, r3);
            goto L_0x049e;
        L_0x0406:
            r0 = NUM; // 0x7f0e00e0 float:1.8875492E38 double:1.0531622673E-314;
            r2 = "AlwaysShareWith";
            r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.neverShareRow;
            if (r2 == r1) goto L_0x0418;
        L_0x0417:
            r3 = 1;
        L_0x0418:
            r11.setTextAndValue(r0, r12, r3);
            goto L_0x049e;
        L_0x041d:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.neverShareRow;
            if (r12 != r0) goto L_0x0466;
        L_0x0425:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentMinus;
            r12 = r12.size();
            if (r12 == 0) goto L_0x0440;
        L_0x0431:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentMinus;
            r12 = r10.getUsersCount(r12);
            r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12);
            goto L_0x0444;
        L_0x0440:
            r12 = org.telegram.messenger.LocaleController.getString(r7, r6);
        L_0x0444:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.rulesType;
            if (r0 == 0) goto L_0x0459;
        L_0x044c:
            r0 = NUM; // 0x7f0e068b float:1.8878435E38 double:1.053162984E-314;
            r1 = "NeverAllow";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r11.setTextAndValue(r0, r12, r3);
            goto L_0x049e;
        L_0x0459:
            r0 = NUM; // 0x7f0e068c float:1.8878437E38 double:1.0531629847E-314;
            r1 = "NeverShareWith";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r11.setTextAndValue(r0, r12, r3);
            goto L_0x049e;
        L_0x0466:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pRow;
            if (r12 != r0) goto L_0x049e;
        L_0x046e:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.ContactsController.getInstance(r12);
            r12 = r12.getLoadingPrivicyInfo(r2);
            if (r12 == 0) goto L_0x0488;
        L_0x047e:
            r12 = NUM; // 0x7f0e05ee float:1.8878116E38 double:1.0531629066E-314;
            r0 = "Loading";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            goto L_0x0492;
        L_0x0488:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.getAccountInstance();
            r12 = org.telegram.ui.PrivacySettingsActivity.formatRulesString(r12, r2);
        L_0x0492:
            r0 = NUM; // 0x7f0e0924 float:1.8879784E38 double:1.0531633127E-314;
            r1 = "PrivacyP2P2";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r11.setTextAndValue(r0, r12, r3);
        L_0x049e:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity$ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
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

    public PrivacyControlActivity(int i) {
        this(i, false);
    }

    public PrivacyControlActivity(int i, boolean z) {
        this.initialPlus = new ArrayList();
        this.initialMinus = new ArrayList();
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
            this.messageCell = new MessageCell(context);
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
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
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
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
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$PrivacyControlActivity$wxF_vl2Ux3ukEJDYev7VlVBgIRk(this));
        setMessageText();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$PrivacyControlActivity(View view, int i) {
        int i2 = 0;
        int i3;
        if (i == this.nobodyRow || i == this.everybodyRow || i == this.myContactsRow) {
            i3 = this.currentType;
            if (i == this.nobodyRow) {
                i2 = 1;
            } else if (i != this.everybodyRow) {
                i2 = i == this.myContactsRow ? 2 : i3;
            }
            if (i2 != this.currentType) {
                this.currentType = i2;
                updateDoneButton();
                updateRows(true);
            }
        } else if (i == this.phoneContactsRow || i == this.phoneEverybodyRow) {
            i3 = this.currentSubType;
            if (i == this.phoneEverybodyRow) {
                i3 = 0;
            } else if (i == this.phoneContactsRow) {
                i3 = 1;
            }
            if (i3 != this.currentSubType) {
                this.currentSubType = i3;
                updateDoneButton();
                updateRows(true);
            }
        } else if (i == this.neverShareRow || i == this.alwaysShareRow) {
            ArrayList arrayList;
            if (i == this.neverShareRow) {
                arrayList = this.currentMinus;
            } else {
                arrayList = this.currentPlus;
            }
            boolean z;
            if (arrayList.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(i == this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                if (this.rulesType != 0) {
                    z = true;
                }
                bundle.putBoolean("isGroup", z);
                GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
                groupCreateActivity.setDelegate(new -$$Lambda$PrivacyControlActivity$gyVCUgP7-dxVWuYDNVL7ikSQ-s4(this, i));
                presentFragment(groupCreateActivity);
            } else {
                boolean z2 = this.rulesType != 0;
                if (i == this.alwaysShareRow) {
                    z = true;
                }
                PrivacyUsersActivity privacyUsersActivity = new PrivacyUsersActivity(arrayList, z2, z);
                privacyUsersActivity.setDelegate(new -$$Lambda$PrivacyControlActivity$Optb0rMT99Nhw1X8nsWqhFLsnHs(this, i));
                presentFragment(privacyUsersActivity);
            }
        } else if (i == this.p2pRow) {
            presentFragment(new PrivacyControlActivity(3));
        }
    }

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
        if (i == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        } else if (i == NotificationCenter.emojiDidLoad) {
            this.listView.invalidateViews();
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            MessageCell messageCell = this.messageCell;
            if (messageCell != null) {
                messageCell.invalidate();
            }
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
        int i;
        int intValue;
        User user;
        InputUser inputUser;
        TL_account_setPrivacy tL_account_setPrivacy = new TL_account_setPrivacy();
        int i2 = this.rulesType;
        if (i2 == 6) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyPhoneNumber();
            if (this.currentType == 1) {
                TL_account_setPrivacy tL_account_setPrivacy2 = new TL_account_setPrivacy();
                tL_account_setPrivacy2.key = new TL_inputPrivacyKeyAddedByPhone();
                if (this.currentSubType == 0) {
                    tL_account_setPrivacy2.rules.add(new TL_inputPrivacyValueAllowAll());
                } else {
                    tL_account_setPrivacy2.rules.add(new TL_inputPrivacyValueAllowContacts());
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_setPrivacy2, new -$$Lambda$PrivacyControlActivity$GPNnel5EWY68dTbf_d3WaxLY8RA(this), 2);
            }
        } else if (i2 == 5) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyForwards();
        } else if (i2 == 4) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyProfilePhoto();
        } else if (i2 == 3) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyPhoneP2P();
        } else if (i2 == 2) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyPhoneCall();
        } else if (i2 == 1) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyChatInvite();
        } else {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TL_inputPrivacyValueAllowUsers tL_inputPrivacyValueAllowUsers = new TL_inputPrivacyValueAllowUsers();
            TL_inputPrivacyValueAllowChatParticipants tL_inputPrivacyValueAllowChatParticipants = new TL_inputPrivacyValueAllowChatParticipants();
            for (i = 0; i < this.currentPlus.size(); i++) {
                intValue = ((Integer) this.currentPlus.get(i)).intValue();
                if (intValue > 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(intValue));
                    if (user != null) {
                        inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                        if (inputUser != null) {
                            tL_inputPrivacyValueAllowUsers.users.add(inputUser);
                        }
                    }
                } else {
                    tL_inputPrivacyValueAllowChatParticipants.chats.add(Integer.valueOf(-intValue));
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueAllowUsers);
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueAllowChatParticipants);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TL_inputPrivacyValueDisallowUsers tL_inputPrivacyValueDisallowUsers = new TL_inputPrivacyValueDisallowUsers();
            TL_inputPrivacyValueDisallowChatParticipants tL_inputPrivacyValueDisallowChatParticipants = new TL_inputPrivacyValueDisallowChatParticipants();
            for (i = 0; i < this.currentMinus.size(); i++) {
                intValue = ((Integer) this.currentMinus.get(i)).intValue();
                if (intValue > 0) {
                    user = getMessagesController().getUser(Integer.valueOf(intValue));
                    if (user != null) {
                        inputUser = getMessagesController().getInputUser(user);
                        if (inputUser != null) {
                            tL_inputPrivacyValueDisallowUsers.users.add(inputUser);
                        }
                    }
                } else {
                    tL_inputPrivacyValueDisallowChatParticipants.chats.add(Integer.valueOf(-intValue));
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueDisallowUsers);
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueDisallowChatParticipants);
        }
        i2 = this.currentType;
        if (i2 == 0) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueAllowAll());
        } else if (i2 == 1) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueDisallowAll());
        } else if (i2 == 2) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueAllowContacts());
        }
        AlertDialog alertDialog = null;
        if (getParentActivity() != null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_setPrivacy, new -$$Lambda$PrivacyControlActivity$zhDvaoyb5PYTpfVPO3M1bp4zkcU(this, alertDialog), 2);
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PrivacyControlActivity$RBPpfQyoTjeR2R2GbQoHY4AyNB0(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$3$PrivacyControlActivity(TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(((TL_account_privacyRules) tLObject).rules, 7);
        }
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$6$PrivacyControlActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PrivacyControlActivity$xzfj0dLNMY5psGjgrUGejhzIeyc(this, alertDialog, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$5$PrivacyControlActivity(AlertDialog alertDialog, TL_error tL_error, TLObject tLObject) {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (tL_error == null) {
            TL_account_privacyRules tL_account_privacyRules = (TL_account_privacyRules) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_account_privacyRules.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_account_privacyRules.chats, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(tL_account_privacyRules.rules, this.rulesType);
            finishFragment();
            return;
        }
        showErrorAlert();
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList();
        this.currentMinus = new ArrayList();
        ArrayList privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules == null || privacyRules.size() == 0) {
            this.currentType = 1;
        } else {
            Object obj = -1;
            for (int i = 0; i < privacyRules.size(); i++) {
                PrivacyRule privacyRule = (PrivacyRule) privacyRules.get(i);
                int size;
                int i2;
                if (privacyRule instanceof TL_privacyValueAllowChatParticipants) {
                    TL_privacyValueAllowChatParticipants tL_privacyValueAllowChatParticipants = (TL_privacyValueAllowChatParticipants) privacyRule;
                    size = tL_privacyValueAllowChatParticipants.chats.size();
                    for (i2 = 0; i2 < size; i2++) {
                        this.currentPlus.add(Integer.valueOf(-((Integer) tL_privacyValueAllowChatParticipants.chats.get(i2)).intValue()));
                    }
                } else if (privacyRule instanceof TL_privacyValueDisallowChatParticipants) {
                    TL_privacyValueDisallowChatParticipants tL_privacyValueDisallowChatParticipants = (TL_privacyValueDisallowChatParticipants) privacyRule;
                    size = tL_privacyValueDisallowChatParticipants.chats.size();
                    for (i2 = 0; i2 < size; i2++) {
                        this.currentMinus.add(Integer.valueOf(-((Integer) tL_privacyValueDisallowChatParticipants.chats.get(i2)).intValue()));
                    }
                } else if (privacyRule instanceof TL_privacyValueAllowUsers) {
                    this.currentPlus.addAll(((TL_privacyValueAllowUsers) privacyRule).users);
                } else if (privacyRule instanceof TL_privacyValueDisallowUsers) {
                    this.currentMinus.addAll(((TL_privacyValueDisallowUsers) privacyRule).users);
                } else if (obj == -1) {
                    obj = privacyRule instanceof TL_privacyValueAllowAll ? null : privacyRule instanceof TL_privacyValueDisallowAll ? 1 : 2;
                }
            }
            if (obj == null || (obj == -1 && this.currentMinus.size() > 0)) {
                this.currentType = 0;
            } else if (obj == 2 || (obj == -1 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
                this.currentType = 2;
            } else if (obj == 1 || (obj == -1 && this.currentPlus.size() > 0)) {
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
            privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(7);
            if (privacyRules == null || privacyRules.size() == 0) {
                this.currentSubType = 0;
            } else {
                int i3 = 0;
                while (i3 < privacyRules.size()) {
                    PrivacyRule privacyRule2 = (PrivacyRule) privacyRules.get(i3);
                    if (privacyRule2 instanceof TL_privacyValueAllowAll) {
                        this.currentSubType = 0;
                        break;
                    } else if (privacyRule2 instanceof TL_privacyValueDisallowAll) {
                        this.currentSubType = 2;
                        break;
                    } else if (privacyRule2 instanceof TL_privacyValueAllowContacts) {
                        this.currentSubType = 1;
                        break;
                    } else {
                        i3++;
                    }
                }
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
        if (this.initialMinus.equals(this.currentMinus)) {
            return false;
        }
        return true;
    }

    private void updateRows(boolean z) {
        int i;
        int i2 = this.alwaysShareRow;
        int i3 = this.neverShareRow;
        int i4 = this.phoneDetailRow;
        int i5 = this.detailRow;
        boolean z2 = this.currentType == 1 && this.currentSubType == 1;
        this.rowCount = 0;
        if (this.rulesType == 5) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messageRow = i;
        } else {
            this.messageRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.everybodyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.myContactsRow = i;
        i = this.rulesType;
        if (i == 0 || i == 2 || i == 3 || i == 5 || i == 6) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nobodyRow = i;
        } else {
            this.nobodyRow = -1;
        }
        if (this.rulesType == 6 && this.currentType == 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.phoneDetailRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.phoneSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.phoneEverybodyRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.phoneContactsRow = i;
        } else {
            this.phoneDetailRow = -1;
            this.phoneSectionRow = -1;
            this.phoneEverybodyRow = -1;
            this.phoneContactsRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.detailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareSectionRow = i;
        i = this.currentType;
        if (i == 1 || i == 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.alwaysShareRow = i;
        } else {
            this.alwaysShareRow = -1;
        }
        i = this.currentType;
        if (i == 0 || i == 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.neverShareRow = i;
        } else {
            this.neverShareRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareDetailRow = i;
        if (this.rulesType == 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.p2pSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.p2pRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.p2pDetailRow = i;
        } else {
            this.p2pSectionRow = -1;
            this.p2pRow = -1;
            this.p2pDetailRow = -1;
        }
        setMessageText();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter == null) {
            return;
        }
        if (z) {
            int childCount = this.listView.getChildCount();
            for (i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof RadioCell) {
                    ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
                    if (findContainingViewHolder != null) {
                        int adapterPosition = findContainingViewHolder.getAdapterPosition();
                        RadioCell radioCell = (RadioCell) childAt;
                        if (adapterPosition == this.everybodyRow || adapterPosition == this.myContactsRow || adapterPosition == this.nobodyRow) {
                            adapterPosition = adapterPosition == this.everybodyRow ? 0 : adapterPosition == this.myContactsRow ? 2 : 1;
                            radioCell.setChecked(this.currentType == adapterPosition, true);
                        } else {
                            if (adapterPosition == this.phoneContactsRow) {
                                adapterPosition = 1;
                            } else {
                                int i6 = this.phoneEverybodyRow;
                                adapterPosition = 0;
                            }
                            radioCell.setChecked(this.currentSubType == adapterPosition, true);
                        }
                    }
                }
            }
            if (this.prevSubtypeContacts != z2) {
                this.listAdapter.notifyItemChanged(i5);
            }
            if ((this.alwaysShareRow != -1 || i2 == -1 || this.neverShareRow == -1 || i3 != -1) && (this.alwaysShareRow == -1 || i2 != -1 || this.neverShareRow != -1 || i3 == -1)) {
                if (this.alwaysShareRow != -1 || i2 == -1) {
                    childCount = this.alwaysShareRow;
                    if (childCount != -1 && i2 == -1) {
                        this.listAdapter.notifyItemInserted(childCount);
                    }
                } else {
                    this.listAdapter.notifyItemRemoved(i2);
                }
                if (this.neverShareRow == -1 && i3 != -1) {
                    this.listAdapter.notifyItemRemoved(i3);
                    if (this.phoneDetailRow != -1 || i4 == -1) {
                        childCount = this.phoneDetailRow;
                        if (childCount != -1 && i4 == -1) {
                            this.listAdapter.notifyItemRangeInserted(childCount, 4);
                            return;
                        }
                        return;
                    }
                    this.listAdapter.notifyItemRangeRemoved(i4, 4);
                    return;
                } else if (this.neverShareRow != -1 && i3 == -1) {
                    if (this.phoneDetailRow != -1 || i4 == -1) {
                        childCount = this.phoneDetailRow;
                        if (childCount != -1 && i4 == -1) {
                            this.listAdapter.notifyItemRangeInserted(childCount, 4);
                        }
                    } else {
                        this.listAdapter.notifyItemRangeRemoved(i4, 4);
                    }
                    this.listAdapter.notifyItemInserted(this.neverShareRow);
                    return;
                } else {
                    return;
                }
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (this.alwaysShareRow != -1) {
                i2 = i3;
            }
            listAdapter2.notifyItemChanged(i2);
            if (this.phoneDetailRow != -1 || i4 == -1) {
                childCount = this.phoneDetailRow;
                if (childCount != -1 && i4 == -1) {
                    this.listAdapter.notifyItemRangeInserted(childCount, 4);
                    return;
                }
                return;
            }
            this.listAdapter.notifyItemRangeRemoved(i4, 4);
            return;
        }
        listAdapter.notifyDataSetChanged();
    }

    private void setMessageText() {
        MessageCell messageCell = this.messageCell;
        if (messageCell != null) {
            int i = this.currentType;
            if (i == 0) {
                messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsEverybody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 1;
            } else if (i == 1) {
                messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsNobody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 0;
            } else {
                messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsContacts", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 1;
            }
            this.messageCell.cell.forceResetMessageObject();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    private void processDone() {
        if (getParentActivity() != null) {
            if (this.currentType != 0 && this.rulesType == 0) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (!globalMainSettings.getBoolean("privacyAlertShowed", false)) {
                    Builder builder = new Builder(getParentActivity());
                    if (this.rulesType == 1) {
                        builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("CustomHelp", NUM));
                    }
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PrivacyControlActivity$uPVQHgKX9OAvKXmrAPXt_jp2D6I(this, globalMainSettings));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    public /* synthetic */ void lambda$processDone$7$PrivacyControlActivity(SharedPreferences sharedPreferences, DialogInterface dialogInterface, int i) {
        applyCurrentPrivacySettings();
        sharedPreferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    private boolean checkDiscard() {
        if (this.doneButton.getAlpha() != 1.0f) {
            return true;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        builder.setMessage(LocaleController.getString("PrivacySettingsChangedAlert", NUM));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new -$$Lambda$PrivacyControlActivity$V3tjUXU6pWTbxJGp9IC4JxKfGxs(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new -$$Lambda$PrivacyControlActivity$XNXL3VVbRo1DwjeDgroe8bjsmfk(this));
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$8$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$9$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[46];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextSettingsCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[9] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        view = this.listView;
        View view2 = view;
        themeDescriptionArr[12] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view2 = this.listView;
        themeDescriptionArr[13] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGray");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[14] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view2 = this.listView;
        themeDescriptionArr[15] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKBOX;
        clsArr = new Class[]{RadioCell.class};
        strArr = new String[1];
        strArr[0] = "radioButton";
        themeDescriptionArr[18] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "radioBackground");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[19] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable.getShadowDrawable(), Theme.chat_msgInMediaDrawable.getShadowDrawable()}, null, "chat_inBubbleShadow");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable.getShadowDrawable(), Theme.chat_msgOutMediaDrawable.getShadowDrawable()}, null, "chat_outBubbleShadow");
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextIn");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextOut");
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck");
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyLine");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyLine");
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyNameText");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyNameText");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMessageText");
        themeDescriptionArr[39] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMessageText");
        themeDescriptionArr[40] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        themeDescriptionArr[41] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        themeDescriptionArr[42] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeText");
        themeDescriptionArr[43] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeText");
        themeDescriptionArr[44] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeSelectedText");
        themeDescriptionArr[45] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeSelectedText");
        return themeDescriptionArr;
    }
}
