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
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import org.telegram.ui.Cells.TextSettingsCell;
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
    private static final int done_button = 1;
    private int alwaysShareRow;
    private ArrayList<Integer> currentMinus;
    private ArrayList<Integer> currentPlus;
    private int currentSubType;
    private int currentType;
    private int detailRow;
    private View doneButton;
    private boolean enableAnimation;
    private int everybodyRow;
    private ArrayList<Integer> initialMinus;
    private ArrayList<Integer> initialPlus;
    private int initialRulesSubType;
    private int initialRulesType;
    private int lastCheckedSubType;
    private int lastCheckedType;
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
    private int rowCount;
    private int rulesType;
    private int sectionRow;
    private int shareDetailRow;
    private int shareSectionRow;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                return super.onTouchEvent(textView, spannable, motionEvent);
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    private class MessageCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private ChatMessageCell cell;
        private HintView hintView;
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

                public /* synthetic */ void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
                    -CC.$default$didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                }

                public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                    -CC.$default$didStartVideoStream(this, messageObject);
                }

                public /* synthetic */ String getAdminRank(int i) {
                    return -CC.$default$getAdminRank(this, i);
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
            if (cachedWallpaperNonBlocking != null) {
                this.backgroundDrawable = cachedWallpaperNonBlocking;
            }
            cachedWallpaperNonBlocking = this.backgroundDrawable;
            if ((cachedWallpaperNonBlocking instanceof ColorDrawable) || (cachedWallpaperNonBlocking instanceof GradientDrawable)) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.backgroundDrawable.draw(canvas);
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

        /* JADX WARNING: Removed duplicated region for block: B:88:0x0182  */
        /* JADX WARNING: Removed duplicated region for block: B:87:0x0177  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
            r9 = this;
            r0 = r10.getItemViewType();
            r1 = -1;
            r2 = 3;
            r3 = 0;
            r4 = 1;
            if (r0 == 0) goto L_0x0404;
        L_0x000a:
            r5 = 4;
            r6 = 5;
            r7 = 6;
            r8 = 2;
            if (r0 == r4) goto L_0x0275;
        L_0x0010:
            if (r0 == r8) goto L_0x0195;
        L_0x0012:
            if (r0 == r2) goto L_0x0016;
        L_0x0014:
            goto L_0x04ed;
        L_0x0016:
            r10 = r10.itemView;
            r10 = (org.telegram.ui.Cells.RadioCell) r10;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.everybodyRow;
            r5 = NUM; // 0x7f0d0570 float:1.8744938E38 double:1.0531304653E-314;
            r6 = "LastSeenEverybody";
            if (r11 == r0) goto L_0x009a;
        L_0x0027:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.myContactsRow;
            if (r11 == r0) goto L_0x009a;
        L_0x002f:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.nobodyRow;
            if (r11 != r0) goto L_0x0038;
        L_0x0037:
            goto L_0x009a;
        L_0x0038:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.phoneContactsRow;
            if (r11 != r0) goto L_0x0059;
        L_0x0040:
            r11 = NUM; // 0x7f0d056a float:1.8744926E38 double:1.0531304623E-314;
            r0 = "LastSeenContacts";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedSubType;
            if (r0 != r4) goto L_0x0053;
        L_0x0051:
            r0 = 1;
            goto L_0x0054;
        L_0x0053:
            r0 = 0;
        L_0x0054:
            r10.setText(r11, r0, r3);
            r11 = 1;
            goto L_0x0074;
        L_0x0059:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.phoneEverybodyRow;
            if (r11 != r0) goto L_0x0073;
        L_0x0061:
            r11 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedSubType;
            if (r0 != 0) goto L_0x006f;
        L_0x006d:
            r0 = 1;
            goto L_0x0070;
        L_0x006f:
            r0 = 0;
        L_0x0070:
            r10.setText(r11, r0, r4);
        L_0x0073:
            r11 = 0;
        L_0x0074:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedSubType;
            if (r0 != r11) goto L_0x0087;
        L_0x007c:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.enableAnimation;
            r10.setChecked(r3, r11);
            goto L_0x04ed;
        L_0x0087:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.currentSubType;
            if (r0 != r11) goto L_0x04ed;
        L_0x008f:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.enableAnimation;
            r10.setChecked(r4, r11);
            goto L_0x04ed;
        L_0x009a:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.everybodyRow;
            if (r11 != r0) goto L_0x00d7;
        L_0x00a2:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r2) goto L_0x00c3;
        L_0x00aa:
            r11 = NUM; // 0x7f0d072b float:1.8745837E38 double:1.053130684E-314;
            r0 = "P2PEverybody";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != 0) goto L_0x00bd;
        L_0x00bb:
            r0 = 1;
            goto L_0x00be;
        L_0x00bd:
            r0 = 0;
        L_0x00be:
            r10.setText(r11, r0, r4);
            goto L_0x016e;
        L_0x00c3:
            r11 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != 0) goto L_0x00d1;
        L_0x00cf:
            r0 = 1;
            goto L_0x00d2;
        L_0x00d1:
            r0 = 0;
        L_0x00d2:
            r10.setText(r11, r0, r4);
            goto L_0x016e;
        L_0x00d7:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.myContactsRow;
            if (r11 != r0) goto L_0x012d;
        L_0x00df:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r2) goto L_0x010a;
        L_0x00e7:
            r11 = NUM; // 0x7f0d0726 float:1.8745826E38 double:1.0531306817E-314;
            r0 = "P2PContacts";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r8) goto L_0x00fa;
        L_0x00f8:
            r0 = 1;
            goto L_0x00fb;
        L_0x00fa:
            r0 = 0;
        L_0x00fb:
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.nobodyRow;
            if (r2 == r1) goto L_0x0105;
        L_0x0103:
            r1 = 1;
            goto L_0x0106;
        L_0x0105:
            r1 = 0;
        L_0x0106:
            r10.setText(r11, r0, r1);
            goto L_0x016f;
        L_0x010a:
            r11 = NUM; // 0x7f0d056a float:1.8744926E38 double:1.0531304623E-314;
            r0 = "LastSeenContacts";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r8) goto L_0x011d;
        L_0x011b:
            r0 = 1;
            goto L_0x011e;
        L_0x011d:
            r0 = 0;
        L_0x011e:
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.nobodyRow;
            if (r2 == r1) goto L_0x0128;
        L_0x0126:
            r1 = 1;
            goto L_0x0129;
        L_0x0128:
            r1 = 0;
        L_0x0129:
            r10.setText(r11, r0, r1);
            goto L_0x016f;
        L_0x012d:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.nobodyRow;
            if (r11 != r0) goto L_0x016e;
        L_0x0135:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r2) goto L_0x0155;
        L_0x013d:
            r11 = NUM; // 0x7f0d072d float:1.874584E38 double:1.053130685E-314;
            r0 = "P2PNobody";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r4) goto L_0x0150;
        L_0x014e:
            r0 = 1;
            goto L_0x0151;
        L_0x0150:
            r0 = 0;
        L_0x0151:
            r10.setText(r11, r0, r3);
            goto L_0x016c;
        L_0x0155:
            r11 = NUM; // 0x7f0d0573 float:1.8744944E38 double:1.053130467E-314;
            r0 = "LastSeenNobody";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r4) goto L_0x0168;
        L_0x0166:
            r0 = 1;
            goto L_0x0169;
        L_0x0168:
            r0 = 0;
        L_0x0169:
            r10.setText(r11, r0, r3);
        L_0x016c:
            r8 = 1;
            goto L_0x016f;
        L_0x016e:
            r8 = 0;
        L_0x016f:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.lastCheckedType;
            if (r11 != r8) goto L_0x0182;
        L_0x0177:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.enableAnimation;
            r10.setChecked(r3, r11);
            goto L_0x04ed;
        L_0x0182:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.currentType;
            if (r11 != r8) goto L_0x04ed;
        L_0x018a:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.enableAnimation;
            r10.setChecked(r4, r11);
            goto L_0x04ed;
        L_0x0195:
            r10 = r10.itemView;
            r10 = (org.telegram.ui.Cells.HeaderCell) r10;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.sectionRow;
            if (r11 != r0) goto L_0x0233;
        L_0x01a1:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r7) goto L_0x01b7;
        L_0x01a9:
            r11 = NUM; // 0x7f0d08a7 float:1.8746607E38 double:1.053130872E-314;
            r0 = "PrivacyPhoneTitle";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x01b7:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r6) goto L_0x01cd;
        L_0x01bf:
            r11 = NUM; // 0x7f0d089c float:1.8746585E38 double:1.0531308665E-314;
            r0 = "PrivacyForwardsTitle";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x01cd:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r5) goto L_0x01e3;
        L_0x01d5:
            r11 = NUM; // 0x7f0d08af float:1.8746624E38 double:1.053130876E-314;
            r0 = "PrivacyProfilePhotoTitle";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x01e3:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r2) goto L_0x01f9;
        L_0x01eb:
            r11 = NUM; // 0x7f0d072a float:1.8745835E38 double:1.0531306837E-314;
            r0 = "P2PEnabledWith";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x01f9:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r8) goto L_0x020f;
        L_0x0201:
            r11 = NUM; // 0x7f0d0b74 float:1.8748062E38 double:1.053131226E-314;
            r0 = "WhoCanCallMe";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x020f:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r4) goto L_0x0225;
        L_0x0217:
            r11 = NUM; // 0x7f0d0b6f float:1.8748051E38 double:1.0531312237E-314;
            r0 = "WhoCanAddMe";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x0225:
            r11 = NUM; // 0x7f0d0575 float:1.8744948E38 double:1.053130468E-314;
            r0 = "LastSeenTitle";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x0233:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.shareSectionRow;
            if (r11 != r0) goto L_0x0249;
        L_0x023b:
            r11 = NUM; // 0x7f0d00ac float:1.8742464E38 double:1.0531298625E-314;
            r0 = "AddExceptions";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x0249:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pSectionRow;
            if (r11 != r0) goto L_0x025f;
        L_0x0251:
            r11 = NUM; // 0x7f0d08a0 float:1.8746593E38 double:1.0531308684E-314;
            r0 = "PrivacyP2PHeader";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x025f:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.phoneSectionRow;
            if (r11 != r0) goto L_0x04ed;
        L_0x0267:
            r11 = NUM; // 0x7f0d08a8 float:1.874661E38 double:1.0531308724E-314;
            r0 = "PrivacyPhoneTitle2";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x04ed;
        L_0x0275:
            r10 = r10.itemView;
            r10 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r10;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.detailRow;
            r1 = "windowBackgroundGrayShadow";
            if (r11 != r0) goto L_0x033a;
        L_0x0283:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r7) goto L_0x02b7;
        L_0x028b:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.currentType;
            if (r11 != r4) goto L_0x02a9;
        L_0x0293:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.currentSubType;
            if (r11 != r4) goto L_0x02a9;
        L_0x029b:
            r11 = NUM; // 0x7f0d08a6 float:1.8746605E38 double:1.0531308714E-314;
            r0 = "PrivacyPhoneInfo3";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x032c;
        L_0x02a9:
            r11 = NUM; // 0x7f0d08a4 float:1.8746601E38 double:1.0531308704E-314;
            r0 = "PrivacyPhoneInfo";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x032c;
        L_0x02b7:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r6) goto L_0x02cc;
        L_0x02bf:
            r11 = NUM; // 0x7f0d0898 float:1.8746577E38 double:1.0531308645E-314;
            r0 = "PrivacyForwardsInfo";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x032c;
        L_0x02cc:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r5) goto L_0x02e1;
        L_0x02d4:
            r11 = NUM; // 0x7f0d08ad float:1.874662E38 double:1.053130875E-314;
            r0 = "PrivacyProfilePhotoInfo";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x032c;
        L_0x02e1:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r2) goto L_0x02f6;
        L_0x02e9:
            r11 = NUM; // 0x7f0d088f float:1.8746559E38 double:1.05313086E-314;
            r0 = "PrivacyCallsP2PHelp";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x032c;
        L_0x02f6:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r8) goto L_0x030b;
        L_0x02fe:
            r11 = NUM; // 0x7f0d0b75 float:1.8748064E38 double:1.0531312266E-314;
            r0 = "WhoCanCallMeInfo";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x032c;
        L_0x030b:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r4) goto L_0x0320;
        L_0x0313:
            r11 = NUM; // 0x7f0d0b70 float:1.8748053E38 double:1.053131224E-314;
            r0 = "WhoCanAddMeInfo";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x032c;
        L_0x0320:
            r11 = NUM; // 0x7f0d0329 float:1.8743756E38 double:1.053130177E-314;
            r0 = "CustomHelp";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
        L_0x032c:
            r11 = r9.mContext;
            r0 = NUM; // 0x7var_ float:1.7944874E38 double:1.052935575E-314;
            r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r11, r0, r1);
            r10.setBackgroundDrawable(r11);
            goto L_0x04ed;
        L_0x033a:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.shareDetailRow;
            r3 = NUM; // 0x7var_ float:1.7944876E38 double:1.0529355757E-314;
            if (r11 != r0) goto L_0x03f1;
        L_0x0345:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r7) goto L_0x035b;
        L_0x034d:
            r11 = NUM; // 0x7f0d08a5 float:1.8746603E38 double:1.053130871E-314;
            r0 = "PrivacyPhoneInfo2";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x03d0;
        L_0x035b:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r6) goto L_0x0370;
        L_0x0363:
            r11 = NUM; // 0x7f0d0899 float:1.8746579E38 double:1.053130865E-314;
            r0 = "PrivacyForwardsInfo2";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x03d0;
        L_0x0370:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r5) goto L_0x0385;
        L_0x0378:
            r11 = NUM; // 0x7f0d08ae float:1.8746621E38 double:1.0531308754E-314;
            r0 = "PrivacyProfilePhotoInfo2";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x03d0;
        L_0x0385:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r2) goto L_0x039a;
        L_0x038d:
            r11 = NUM; // 0x7f0d032b float:1.874376E38 double:1.053130178E-314;
            r0 = "CustomP2PInfo";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x03d0;
        L_0x039a:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r8) goto L_0x03af;
        L_0x03a2:
            r11 = NUM; // 0x7f0d0328 float:1.8743754E38 double:1.0531301767E-314;
            r0 = "CustomCallInfo";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x03d0;
        L_0x03af:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r4) goto L_0x03c4;
        L_0x03b7:
            r11 = NUM; // 0x7f0d032c float:1.8743762E38 double:1.0531301787E-314;
            r0 = "CustomShareInfo";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
            goto L_0x03d0;
        L_0x03c4:
            r11 = NUM; // 0x7f0d032d float:1.8743764E38 double:1.053130179E-314;
            r0 = "CustomShareSettingsHelp";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            r10.setText(r11);
        L_0x03d0:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.rulesType;
            if (r11 != r8) goto L_0x03e6;
        L_0x03d8:
            r11 = r9.mContext;
            r0 = NUM; // 0x7var_ float:1.7944874E38 double:1.052935575E-314;
            r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r11, r0, r1);
            r10.setBackgroundDrawable(r11);
            goto L_0x04ed;
        L_0x03e6:
            r11 = r9.mContext;
            r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r11, r3, r1);
            r10.setBackgroundDrawable(r11);
            goto L_0x04ed;
        L_0x03f1:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pDetailRow;
            if (r11 != r0) goto L_0x04ed;
        L_0x03f9:
            r11 = r9.mContext;
            r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r11, r3, r1);
            r10.setBackgroundDrawable(r11);
            goto L_0x04ed;
        L_0x0404:
            r10 = r10.itemView;
            r10 = (org.telegram.ui.Cells.TextSettingsCell) r10;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.alwaysShareRow;
            r5 = "Users";
            r6 = NUM; // 0x7f0d03db float:1.8744117E38 double:1.053130265E-314;
            r7 = "EmpryUsersPlaceholder";
            if (r11 != r0) goto L_0x046c;
        L_0x0417:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.currentPlus;
            r11 = r11.size();
            if (r11 == 0) goto L_0x0432;
        L_0x0423:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.currentPlus;
            r11 = r9.getUsersCount(r11);
            r11 = org.telegram.messenger.LocaleController.formatPluralString(r5, r11);
            goto L_0x0436;
        L_0x0432:
            r11 = org.telegram.messenger.LocaleController.getString(r7, r6);
        L_0x0436:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.rulesType;
            if (r0 == 0) goto L_0x0455;
        L_0x043e:
            r0 = NUM; // 0x7f0d00d7 float:1.874255E38 double:1.053129884E-314;
            r2 = "AlwaysAllow";
            r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.neverShareRow;
            if (r2 == r1) goto L_0x0450;
        L_0x044f:
            r3 = 1;
        L_0x0450:
            r10.setTextAndValue(r0, r11, r3);
            goto L_0x04ed;
        L_0x0455:
            r0 = NUM; // 0x7f0d00d8 float:1.8742553E38 double:1.0531298843E-314;
            r2 = "AlwaysShareWith";
            r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.neverShareRow;
            if (r2 == r1) goto L_0x0467;
        L_0x0466:
            r3 = 1;
        L_0x0467:
            r10.setTextAndValue(r0, r11, r3);
            goto L_0x04ed;
        L_0x046c:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.neverShareRow;
            if (r11 != r0) goto L_0x04b5;
        L_0x0474:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.currentMinus;
            r11 = r11.size();
            if (r11 == 0) goto L_0x048f;
        L_0x0480:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.currentMinus;
            r11 = r9.getUsersCount(r11);
            r11 = org.telegram.messenger.LocaleController.formatPluralString(r5, r11);
            goto L_0x0493;
        L_0x048f:
            r11 = org.telegram.messenger.LocaleController.getString(r7, r6);
        L_0x0493:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.rulesType;
            if (r0 == 0) goto L_0x04a8;
        L_0x049b:
            r0 = NUM; // 0x7f0d0628 float:1.8745311E38 double:1.053130556E-314;
            r1 = "NeverAllow";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r10.setTextAndValue(r0, r11, r3);
            goto L_0x04ed;
        L_0x04a8:
            r0 = NUM; // 0x7f0d0629 float:1.8745313E38 double:1.0531305567E-314;
            r1 = "NeverShareWith";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r10.setTextAndValue(r0, r11, r3);
            goto L_0x04ed;
        L_0x04b5:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pRow;
            if (r11 != r0) goto L_0x04ed;
        L_0x04bd:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.currentAccount;
            r11 = org.telegram.messenger.ContactsController.getInstance(r11);
            r11 = r11.getLoadingPrivicyInfo(r2);
            if (r11 == 0) goto L_0x04d7;
        L_0x04cd:
            r11 = NUM; // 0x7f0d0590 float:1.8745003E38 double:1.053130481E-314;
            r0 = "Loading";
            r11 = org.telegram.messenger.LocaleController.getString(r0, r11);
            goto L_0x04e1;
        L_0x04d7:
            r11 = org.telegram.ui.PrivacyControlActivity.this;
            r11 = r11.getAccountInstance();
            r11 = org.telegram.ui.PrivacySettingsActivity.formatRulesString(r11, r2);
        L_0x04e1:
            r0 = NUM; // 0x7f0d089f float:1.8746591E38 double:1.053130868E-314;
            r1 = "PrivacyP2P2";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r10.setTextAndValue(r0, r11, r3);
        L_0x04ed:
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
        this.lastCheckedType = -1;
        this.lastCheckedSubType = -1;
        this.rulesType = i;
        if (z) {
            ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
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
        View view = this.doneButton;
        i = view != null ? view.getVisibility() : 8;
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.doneButton.setVisibility(i);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$PrivacyControlActivity$wxF_vl2Ux3ukEJDYev7VlVBgIRk(this));
        setMessageText();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$PrivacyControlActivity(View view, int i) {
        int i2 = 8;
        boolean z = false;
        int i3;
        if (i == this.nobodyRow || i == this.everybodyRow || i == this.myContactsRow) {
            i3 = this.currentType;
            if (i == this.nobodyRow) {
                i3 = 1;
            } else if (i == this.everybodyRow) {
                i3 = 0;
            } else if (i == this.myContactsRow) {
                i3 = 2;
            }
            i = this.currentType;
            if (i3 != i) {
                this.enableAnimation = true;
                this.lastCheckedType = i;
                this.currentType = i3;
                view = this.doneButton;
                if (hasChanges()) {
                    i2 = 0;
                }
                view.setVisibility(i2);
                updateRows();
            }
        } else if (i == this.phoneContactsRow || i == this.phoneEverybodyRow) {
            i3 = this.currentSubType;
            if (i == this.phoneEverybodyRow) {
                i3 = 0;
            } else if (i == this.phoneContactsRow) {
                i3 = 1;
            }
            i = this.currentSubType;
            if (i3 != i) {
                this.enableAnimation = true;
                this.lastCheckedSubType = i;
                this.currentSubType = i3;
                view = this.doneButton;
                if (hasChanges()) {
                    i2 = 0;
                }
                view.setVisibility(i2);
                updateRows();
            }
        } else if (i == this.neverShareRow || i == this.alwaysShareRow) {
            ArrayList arrayList;
            if (i == this.neverShareRow) {
                arrayList = this.currentMinus;
            } else {
                arrayList = this.currentPlus;
            }
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
            for (i = 0; i < this.currentMinus.size(); i++) {
                this.currentPlus.remove(this.currentMinus.get(i));
            }
        } else {
            this.currentPlus = arrayList;
            for (i = 0; i < this.currentPlus.size(); i++) {
                this.currentMinus.remove(this.currentPlus.get(i));
            }
        }
        this.lastCheckedType = -1;
        View view = this.doneButton;
        if (!hasChanges()) {
            i2 = 8;
        }
        view.setVisibility(i2);
        this.listAdapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$1$PrivacyControlActivity(int i, ArrayList arrayList, boolean z) {
        int i2 = 0;
        if (i == this.neverShareRow) {
            this.currentMinus = arrayList;
            if (z) {
                for (i = 0; i < this.currentMinus.size(); i++) {
                    this.currentPlus.remove(this.currentMinus.get(i));
                }
            }
        } else {
            this.currentPlus = arrayList;
            if (z) {
                for (i = 0; i < this.currentPlus.size(); i++) {
                    this.currentMinus.remove(this.currentPlus.get(i));
                }
            }
        }
        View view = this.doneButton;
        if (!hasChanges()) {
            i2 = 8;
        }
        view.setVisibility(i2);
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        } else if (i == NotificationCenter.emojiDidLoad) {
            this.listView.invalidateViews();
        }
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
                view.setVisibility(8);
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
        updateRows();
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

    private void updateRows() {
        int i;
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
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
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
        this.lastCheckedType = -1;
        this.lastCheckedSubType = -1;
        this.enableAnimation = false;
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
        if (this.doneButton.getVisibility() != 0) {
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[43];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
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
        view = this.listView;
        view2 = view;
        themeDescriptionArr[13] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKBOX;
        clsArr = new Class[]{RadioCell.class};
        strArr = new String[1];
        strArr[0] = "radioButton";
        themeDescriptionArr[16] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "radioBackground");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[17] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextIn");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextOut");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck");
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead");
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected");
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyLine");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyLine");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyNameText");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyNameText");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMessageText");
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMessageText");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        themeDescriptionArr[39] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeText");
        themeDescriptionArr[40] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeText");
        themeDescriptionArr[41] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeSelectedText");
        themeDescriptionArr[42] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeSelectedText");
        return themeDescriptionArr;
    }
}
