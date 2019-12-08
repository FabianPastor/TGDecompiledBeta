package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowChatParticipants;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
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
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenterDelegate {
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
    private int currentType;
    private int detailRow;
    private View doneButton;
    private boolean enableAnimation;
    private int everybodyRow;
    private ArrayList<Integer> initialMinus;
    private ArrayList<Integer> initialPlus;
    private int initialRulesType;
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
            if (cachedWallpaperNonBlocking instanceof ColorDrawable) {
                cachedWallpaperNonBlocking.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
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
            View headerCell;
            String str = "windowBackgroundWhite";
            if (i != 0) {
                View textInfoPrivacyCell;
                if (i == 1) {
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                } else if (i == 2) {
                    headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                } else if (i != 3) {
                    textInfoPrivacyCell = PrivacyControlActivity.this.messageCell;
                } else {
                    headerCell = new RadioCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                }
                headerCell = textInfoPrivacyCell;
            } else {
                headerCell = new TextSettingsCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor(str));
            }
            return new Holder(headerCell);
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

        /* JADX WARNING: Removed duplicated region for block: B:61:0x0107  */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x00fc  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
            r10 = this;
            r0 = r11.getItemViewType();
            r1 = -1;
            r2 = 3;
            r3 = 0;
            r4 = 1;
            if (r0 == 0) goto L_0x0352;
        L_0x000a:
            r5 = 4;
            r6 = 5;
            r7 = 6;
            r8 = 2;
            if (r0 == r4) goto L_0x01e4;
        L_0x0010:
            if (r0 == r8) goto L_0x011a;
        L_0x0012:
            if (r0 == r2) goto L_0x0016;
        L_0x0014:
            goto L_0x043b;
        L_0x0016:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.RadioCell) r11;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.everybodyRow;
            if (r12 != r0) goto L_0x005c;
        L_0x0022:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x0043;
        L_0x002a:
            r12 = NUM; // 0x7f0d0718 float:1.8745798E38 double:1.053130675E-314;
            r0 = "P2PEverybody";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != 0) goto L_0x003d;
        L_0x003b:
            r0 = 1;
            goto L_0x003e;
        L_0x003d:
            r0 = 0;
        L_0x003e:
            r11.setText(r12, r0, r4);
            goto L_0x00f3;
        L_0x0043:
            r12 = NUM; // 0x7f0d056e float:1.8744934E38 double:1.0531304643E-314;
            r0 = "LastSeenEverybody";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != 0) goto L_0x0056;
        L_0x0054:
            r0 = 1;
            goto L_0x0057;
        L_0x0056:
            r0 = 0;
        L_0x0057:
            r11.setText(r12, r0, r4);
            goto L_0x00f3;
        L_0x005c:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.myContactsRow;
            if (r12 != r0) goto L_0x00b2;
        L_0x0064:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x008f;
        L_0x006c:
            r12 = NUM; // 0x7f0d0713 float:1.8745788E38 double:1.0531306723E-314;
            r0 = "P2PContacts";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r8) goto L_0x007f;
        L_0x007d:
            r0 = 1;
            goto L_0x0080;
        L_0x007f:
            r0 = 0;
        L_0x0080:
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.nobodyRow;
            if (r2 == r1) goto L_0x008a;
        L_0x0088:
            r1 = 1;
            goto L_0x008b;
        L_0x008a:
            r1 = 0;
        L_0x008b:
            r11.setText(r12, r0, r1);
            goto L_0x00f4;
        L_0x008f:
            r12 = NUM; // 0x7f0d0568 float:1.8744922E38 double:1.0531304613E-314;
            r0 = "LastSeenContacts";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r8) goto L_0x00a2;
        L_0x00a0:
            r0 = 1;
            goto L_0x00a3;
        L_0x00a2:
            r0 = 0;
        L_0x00a3:
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.nobodyRow;
            if (r2 == r1) goto L_0x00ad;
        L_0x00ab:
            r1 = 1;
            goto L_0x00ae;
        L_0x00ad:
            r1 = 0;
        L_0x00ae:
            r11.setText(r12, r0, r1);
            goto L_0x00f4;
        L_0x00b2:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.nobodyRow;
            if (r12 != r0) goto L_0x00f3;
        L_0x00ba:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x00da;
        L_0x00c2:
            r12 = NUM; // 0x7f0d071a float:1.8745802E38 double:1.053130676E-314;
            r0 = "P2PNobody";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r4) goto L_0x00d5;
        L_0x00d3:
            r0 = 1;
            goto L_0x00d6;
        L_0x00d5:
            r0 = 0;
        L_0x00d6:
            r11.setText(r12, r0, r3);
            goto L_0x00f1;
        L_0x00da:
            r12 = NUM; // 0x7f0d0571 float:1.874494E38 double:1.053130466E-314;
            r0 = "LastSeenNobody";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r4) goto L_0x00ed;
        L_0x00eb:
            r0 = 1;
            goto L_0x00ee;
        L_0x00ed:
            r0 = 0;
        L_0x00ee:
            r11.setText(r12, r0, r3);
        L_0x00f1:
            r8 = 1;
            goto L_0x00f4;
        L_0x00f3:
            r8 = 0;
        L_0x00f4:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.lastCheckedType;
            if (r12 != r8) goto L_0x0107;
        L_0x00fc:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.enableAnimation;
            r11.setChecked(r3, r12);
            goto L_0x043b;
        L_0x0107:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentType;
            if (r12 != r8) goto L_0x043b;
        L_0x010f:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.enableAnimation;
            r11.setChecked(r4, r12);
            goto L_0x043b;
        L_0x011a:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.HeaderCell) r11;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.sectionRow;
            if (r12 != r0) goto L_0x01b8;
        L_0x0126:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r7) goto L_0x013c;
        L_0x012e:
            r12 = NUM; // 0x7f0d0893 float:1.8746567E38 double:1.053130862E-314;
            r0 = "PrivacyPhoneTitle";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x013c:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r6) goto L_0x0152;
        L_0x0144:
            r12 = NUM; // 0x7f0d0889 float:1.8746546E38 double:1.053130857E-314;
            r0 = "PrivacyForwardsTitle";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x0152:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r5) goto L_0x0168;
        L_0x015a:
            r12 = NUM; // 0x7f0d089a float:1.874658E38 double:1.0531308655E-314;
            r0 = "PrivacyProfilePhotoTitle";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x0168:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x017e;
        L_0x0170:
            r12 = NUM; // 0x7f0d0717 float:1.8745796E38 double:1.0531306743E-314;
            r0 = "P2PEnabledWith";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x017e:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r8) goto L_0x0194;
        L_0x0186:
            r12 = NUM; // 0x7f0d0b21 float:1.8747893E38 double:1.053131185E-314;
            r0 = "WhoCanCallMe";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x0194:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r4) goto L_0x01aa;
        L_0x019c:
            r12 = NUM; // 0x7f0d0b1c float:1.8747883E38 double:1.0531311827E-314;
            r0 = "WhoCanAddMe";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x01aa:
            r12 = NUM; // 0x7f0d0573 float:1.8744944E38 double:1.053130467E-314;
            r0 = "LastSeenTitle";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x01b8:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.shareSectionRow;
            if (r12 != r0) goto L_0x01ce;
        L_0x01c0:
            r12 = NUM; // 0x7f0d00ac float:1.8742464E38 double:1.0531298625E-314;
            r0 = "AddExceptions";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x01ce:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pSectionRow;
            if (r12 != r0) goto L_0x043b;
        L_0x01d6:
            r12 = NUM; // 0x7f0d088d float:1.8746555E38 double:1.053130859E-314;
            r0 = "PrivacyP2PHeader";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x043b;
        L_0x01e4:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r11;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.detailRow;
            r1 = NUM; // 0x7var_ float:1.7944874E38 double:1.052935575E-314;
            r3 = "windowBackgroundGrayShadow";
            if (r12 != r0) goto L_0x028b;
        L_0x01f5:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r7) goto L_0x020b;
        L_0x01fd:
            r12 = NUM; // 0x7f0d0891 float:1.8746563E38 double:1.053130861E-314;
            r0 = "PrivacyPhoneInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0280;
        L_0x020b:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r6) goto L_0x0220;
        L_0x0213:
            r12 = NUM; // 0x7f0d0885 float:1.8746538E38 double:1.053130855E-314;
            r0 = "PrivacyForwardsInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0280;
        L_0x0220:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r5) goto L_0x0235;
        L_0x0228:
            r12 = NUM; // 0x7f0d0898 float:1.8746577E38 double:1.0531308645E-314;
            r0 = "PrivacyProfilePhotoInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0280;
        L_0x0235:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x024a;
        L_0x023d:
            r12 = NUM; // 0x7f0d087c float:1.874652E38 double:1.0531308507E-314;
            r0 = "PrivacyCallsP2PHelp";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0280;
        L_0x024a:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r8) goto L_0x025f;
        L_0x0252:
            r12 = NUM; // 0x7f0d0b22 float:1.8747895E38 double:1.0531311856E-314;
            r0 = "WhoCanCallMeInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0280;
        L_0x025f:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r4) goto L_0x0274;
        L_0x0267:
            r12 = NUM; // 0x7f0d0b1d float:1.8747885E38 double:1.053131183E-314;
            r0 = "WhoCanAddMeInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0280;
        L_0x0274:
            r12 = NUM; // 0x7f0d0328 float:1.8743754E38 double:1.0531301767E-314;
            r0 = "CustomHelp";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
        L_0x0280:
            r12 = r10.mContext;
            r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r12, r1, r3);
            r11.setBackgroundDrawable(r12);
            goto L_0x043b;
        L_0x028b:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.shareDetailRow;
            r9 = NUM; // 0x7var_ float:1.7944876E38 double:1.0529355757E-314;
            if (r12 != r0) goto L_0x033f;
        L_0x0296:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r7) goto L_0x02ac;
        L_0x029e:
            r12 = NUM; // 0x7f0d0892 float:1.8746565E38 double:1.0531308615E-314;
            r0 = "PrivacyPhoneInfo2";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0321;
        L_0x02ac:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r6) goto L_0x02c1;
        L_0x02b4:
            r12 = NUM; // 0x7f0d0886 float:1.874654E38 double:1.0531308556E-314;
            r0 = "PrivacyForwardsInfo2";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0321;
        L_0x02c1:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r5) goto L_0x02d6;
        L_0x02c9:
            r12 = NUM; // 0x7f0d0899 float:1.8746579E38 double:1.053130865E-314;
            r0 = "PrivacyProfilePhotoInfo2";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0321;
        L_0x02d6:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r2) goto L_0x02eb;
        L_0x02de:
            r12 = NUM; // 0x7f0d032a float:1.8743758E38 double:1.0531301777E-314;
            r0 = "CustomP2PInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0321;
        L_0x02eb:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r8) goto L_0x0300;
        L_0x02f3:
            r12 = NUM; // 0x7f0d0327 float:1.8743752E38 double:1.0531301763E-314;
            r0 = "CustomCallInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0321;
        L_0x0300:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r4) goto L_0x0315;
        L_0x0308:
            r12 = NUM; // 0x7f0d032b float:1.874376E38 double:1.053130178E-314;
            r0 = "CustomShareInfo";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
            goto L_0x0321;
        L_0x0315:
            r12 = NUM; // 0x7f0d032c float:1.8743762E38 double:1.0531301787E-314;
            r0 = "CustomShareSettingsHelp";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            r11.setText(r12);
        L_0x0321:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.rulesType;
            if (r12 != r8) goto L_0x0334;
        L_0x0329:
            r12 = r10.mContext;
            r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r12, r1, r3);
            r11.setBackgroundDrawable(r12);
            goto L_0x043b;
        L_0x0334:
            r12 = r10.mContext;
            r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r12, r9, r3);
            r11.setBackgroundDrawable(r12);
            goto L_0x043b;
        L_0x033f:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pDetailRow;
            if (r12 != r0) goto L_0x043b;
        L_0x0347:
            r12 = r10.mContext;
            r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r12, r9, r3);
            r11.setBackgroundDrawable(r12);
            goto L_0x043b;
        L_0x0352:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.TextSettingsCell) r11;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.alwaysShareRow;
            r5 = "Users";
            r6 = NUM; // 0x7f0d03da float:1.8744115E38 double:1.0531302647E-314;
            r7 = "EmpryUsersPlaceholder";
            if (r12 != r0) goto L_0x03ba;
        L_0x0365:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentPlus;
            r12 = r12.size();
            if (r12 == 0) goto L_0x0380;
        L_0x0371:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentPlus;
            r12 = r10.getUsersCount(r12);
            r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12);
            goto L_0x0384;
        L_0x0380:
            r12 = org.telegram.messenger.LocaleController.getString(r7, r6);
        L_0x0384:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.rulesType;
            if (r0 == 0) goto L_0x03a3;
        L_0x038c:
            r0 = NUM; // 0x7f0d00d9 float:1.8742555E38 double:1.053129885E-314;
            r2 = "AlwaysAllow";
            r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.neverShareRow;
            if (r2 == r1) goto L_0x039e;
        L_0x039d:
            r3 = 1;
        L_0x039e:
            r11.setTextAndValue(r0, r12, r3);
            goto L_0x043b;
        L_0x03a3:
            r0 = NUM; // 0x7f0d00da float:1.8742557E38 double:1.0531298853E-314;
            r2 = "AlwaysShareWith";
            r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
            r2 = org.telegram.ui.PrivacyControlActivity.this;
            r2 = r2.neverShareRow;
            if (r2 == r1) goto L_0x03b5;
        L_0x03b4:
            r3 = 1;
        L_0x03b5:
            r11.setTextAndValue(r0, r12, r3);
            goto L_0x043b;
        L_0x03ba:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.neverShareRow;
            if (r12 != r0) goto L_0x0403;
        L_0x03c2:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentMinus;
            r12 = r12.size();
            if (r12 == 0) goto L_0x03dd;
        L_0x03ce:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentMinus;
            r12 = r10.getUsersCount(r12);
            r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12);
            goto L_0x03e1;
        L_0x03dd:
            r12 = org.telegram.messenger.LocaleController.getString(r7, r6);
        L_0x03e1:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.rulesType;
            if (r0 == 0) goto L_0x03f6;
        L_0x03e9:
            r0 = NUM; // 0x7f0d061e float:1.874529E38 double:1.0531305513E-314;
            r1 = "NeverAllow";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r11.setTextAndValue(r0, r12, r3);
            goto L_0x043b;
        L_0x03f6:
            r0 = NUM; // 0x7f0d061f float:1.8745293E38 double:1.0531305517E-314;
            r1 = "NeverShareWith";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r11.setTextAndValue(r0, r12, r3);
            goto L_0x043b;
        L_0x0403:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.p2pRow;
            if (r12 != r0) goto L_0x043b;
        L_0x040b:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.ContactsController.getInstance(r12);
            r12 = r12.getLoadingPrivicyInfo(r2);
            if (r12 == 0) goto L_0x0425;
        L_0x041b:
            r12 = NUM; // 0x7f0d058e float:1.8744999E38 double:1.05313048E-314;
            r0 = "Loading";
            r12 = org.telegram.messenger.LocaleController.getString(r0, r12);
            goto L_0x042f;
        L_0x0425:
            r12 = org.telegram.ui.PrivacyControlActivity.this;
            r12 = r12.getAccountInstance();
            r12 = org.telegram.ui.PrivacySettingsActivity.formatRulesString(r12, r2);
        L_0x042f:
            r0 = NUM; // 0x7f0d088c float:1.8746553E38 double:1.0531308586E-314;
            r1 = "PrivacyP2P2";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r11.setTextAndValue(r0, r12, r3);
        L_0x043b:
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
            if (i == PrivacyControlActivity.this.sectionRow || i == PrivacyControlActivity.this.shareSectionRow || i == PrivacyControlActivity.this.p2pSectionRow) {
                return 2;
            }
            if (i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.nobodyRow) {
                return 3;
            }
            if (i == PrivacyControlActivity.this.messageRow) {
                return 4;
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
        int i2 = 0;
        if (i == this.nobodyRow || i == this.everybodyRow || i == this.myContactsRow) {
            int i3 = this.currentType;
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
                if (!hasChanges()) {
                    i2 = 8;
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_setPrivacy, new -$$Lambda$PrivacyControlActivity$8u1Pr-pdaGnQbppD7jDw_8yxFb4(this, alertDialog), 2);
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PrivacyControlActivity$wCT7rGu451A16C1KVTM9KhVzZrc(this, alertDialog, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$3$PrivacyControlActivity(AlertDialog alertDialog, TL_error tL_error, TLObject tLObject) {
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
        updateRows();
    }

    private boolean hasChanges() {
        if (this.initialRulesType != this.currentType || this.initialMinus.size() != this.currentMinus.size() || this.initialPlus.size() != this.currentPlus.size()) {
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
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PrivacyControlActivity$IKfTFdmuEI1xlx3GBeNMY8JX7Aw(this, globalMainSettings));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    public /* synthetic */ void lambda$processDone$5$PrivacyControlActivity(SharedPreferences sharedPreferences, DialogInterface dialogInterface, int i) {
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
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new -$$Lambda$PrivacyControlActivity$5im2Vf3cKm0s4wzjCS8m-xwDFXs(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new -$$Lambda$PrivacyControlActivity$jMiVlh9zo_Od8OHpA_uuLslJQzk(this));
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$6$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$7$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[40];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextSettingsCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKBOX;
        clsArr = new Class[]{RadioCell.class};
        strArr = new String[1];
        strArr[0] = "radioButton";
        themeDescriptionArr[15] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "radioBackground");
        view = this.listView;
        View view2 = view;
        themeDescriptionArr[16] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextIn");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextOut");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheck");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyLine");
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyLine");
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyNameText");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyNameText");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMessageText");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMessageText");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeText");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeText");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeSelectedText");
        themeDescriptionArr[39] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeSelectedText");
        return themeDescriptionArr;
    }
}
