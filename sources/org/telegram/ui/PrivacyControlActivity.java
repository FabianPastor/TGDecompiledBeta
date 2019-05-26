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
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneP2P;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowContacts;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
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

                public /* synthetic */ void didPressUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
                    -CC.$default$didPressUrl(this, messageObject, characterStyle, z);
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

                public /* synthetic */ boolean isChatAdminCell(int i) {
                    return -CC.$default$isChatAdminCell(this, i);
                }

                public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                    -CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                }

                public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                    return -CC.$default$needPlayMessage(this, messageObject);
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
            return adapterPosition == PrivacyControlActivity.this.nobodyRow || adapterPosition == PrivacyControlActivity.this.everybodyRow || adapterPosition == PrivacyControlActivity.this.myContactsRow || adapterPosition == PrivacyControlActivity.this.neverShareRow || adapterPosition == PrivacyControlActivity.this.alwaysShareRow;
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

        /* JADX WARNING: Removed duplicated region for block: B:61:0x0106  */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x00fb  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
            r8 = this;
            r0 = r9.getItemViewType();
            r1 = -1;
            r2 = 0;
            r3 = 1;
            if (r0 == 0) goto L_0x02d4;
        L_0x0009:
            r4 = 4;
            r5 = 5;
            r6 = 3;
            r7 = 2;
            if (r0 == r3) goto L_0x01b7;
        L_0x000f:
            if (r0 == r7) goto L_0x0119;
        L_0x0011:
            if (r0 == r6) goto L_0x0015;
        L_0x0013:
            goto L_0x0382;
        L_0x0015:
            r9 = r9.itemView;
            r9 = (org.telegram.ui.Cells.RadioCell) r9;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.everybodyRow;
            if (r10 != r0) goto L_0x005b;
        L_0x0021:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r6) goto L_0x0042;
        L_0x0029:
            r10 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
            r0 = "P2PEverybody";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != 0) goto L_0x003c;
        L_0x003a:
            r0 = 1;
            goto L_0x003d;
        L_0x003c:
            r0 = 0;
        L_0x003d:
            r9.setText(r10, r0, r3);
            goto L_0x00f2;
        L_0x0042:
            r10 = NUM; // 0x7f0d050f float:1.8744741E38 double:1.0531304174E-314;
            r0 = "LastSeenEverybody";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != 0) goto L_0x0055;
        L_0x0053:
            r0 = 1;
            goto L_0x0056;
        L_0x0055:
            r0 = 0;
        L_0x0056:
            r9.setText(r10, r0, r3);
            goto L_0x00f2;
        L_0x005b:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.myContactsRow;
            if (r10 != r0) goto L_0x00b1;
        L_0x0063:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r6) goto L_0x008e;
        L_0x006b:
            r10 = NUM; // 0x7f0d069b float:1.8745544E38 double:1.053130613E-314;
            r0 = "P2PContacts";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r7) goto L_0x007e;
        L_0x007c:
            r0 = 1;
            goto L_0x007f;
        L_0x007e:
            r0 = 0;
        L_0x007f:
            r4 = org.telegram.ui.PrivacyControlActivity.this;
            r4 = r4.nobodyRow;
            if (r4 == r1) goto L_0x0089;
        L_0x0087:
            r1 = 1;
            goto L_0x008a;
        L_0x0089:
            r1 = 0;
        L_0x008a:
            r9.setText(r10, r0, r1);
            goto L_0x00f3;
        L_0x008e:
            r10 = NUM; // 0x7f0d0509 float:1.874473E38 double:1.0531304144E-314;
            r0 = "LastSeenContacts";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r7) goto L_0x00a1;
        L_0x009f:
            r0 = 1;
            goto L_0x00a2;
        L_0x00a1:
            r0 = 0;
        L_0x00a2:
            r4 = org.telegram.ui.PrivacyControlActivity.this;
            r4 = r4.nobodyRow;
            if (r4 == r1) goto L_0x00ac;
        L_0x00aa:
            r1 = 1;
            goto L_0x00ad;
        L_0x00ac:
            r1 = 0;
        L_0x00ad:
            r9.setText(r10, r0, r1);
            goto L_0x00f3;
        L_0x00b1:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.nobodyRow;
            if (r10 != r0) goto L_0x00f2;
        L_0x00b9:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r6) goto L_0x00d9;
        L_0x00c1:
            r10 = NUM; // 0x7f0d06a2 float:1.8745559E38 double:1.0531306165E-314;
            r0 = "P2PNobody";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r3) goto L_0x00d4;
        L_0x00d2:
            r0 = 1;
            goto L_0x00d5;
        L_0x00d4:
            r0 = 0;
        L_0x00d5:
            r9.setText(r10, r0, r2);
            goto L_0x00f0;
        L_0x00d9:
            r10 = NUM; // 0x7f0d0512 float:1.8744747E38 double:1.053130419E-314;
            r0 = "LastSeenNobody";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.lastCheckedType;
            if (r0 != r3) goto L_0x00ec;
        L_0x00ea:
            r0 = 1;
            goto L_0x00ed;
        L_0x00ec:
            r0 = 0;
        L_0x00ed:
            r9.setText(r10, r0, r2);
        L_0x00f0:
            r7 = 1;
            goto L_0x00f3;
        L_0x00f2:
            r7 = 0;
        L_0x00f3:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.lastCheckedType;
            if (r10 != r7) goto L_0x0106;
        L_0x00fb:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.enableAnimation;
            r9.setChecked(r2, r10);
            goto L_0x0382;
        L_0x0106:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.currentType;
            if (r10 != r7) goto L_0x0382;
        L_0x010e:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.enableAnimation;
            r9.setChecked(r3, r10);
            goto L_0x0382;
        L_0x0119:
            r9 = r9.itemView;
            r9 = (org.telegram.ui.Cells.HeaderCell) r9;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.sectionRow;
            if (r10 != r0) goto L_0x01a1;
        L_0x0125:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r5) goto L_0x013b;
        L_0x012d:
            r10 = NUM; // 0x7f0d07fe float:1.8746265E38 double:1.0531307884E-314;
            r0 = "PrivacyForwardsTitle";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x0382;
        L_0x013b:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r4) goto L_0x0151;
        L_0x0143:
            r10 = NUM; // 0x7f0d0809 float:1.8746287E38 double:1.053130794E-314;
            r0 = "PrivacyProfilePhotoTitle";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x0382;
        L_0x0151:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r6) goto L_0x0167;
        L_0x0159:
            r10 = NUM; // 0x7f0d069f float:1.8745553E38 double:1.053130615E-314;
            r0 = "P2PEnabledWith";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x0382;
        L_0x0167:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r7) goto L_0x017d;
        L_0x016f:
            r10 = NUM; // 0x7f0d0a64 float:1.874751E38 double:1.053131092E-314;
            r0 = "WhoCanCallMe";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x0382;
        L_0x017d:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r3) goto L_0x0193;
        L_0x0185:
            r10 = NUM; // 0x7f0d0a5f float:1.87475E38 double:1.0531310893E-314;
            r0 = "WhoCanAddMe";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x0382;
        L_0x0193:
            r10 = NUM; // 0x7f0d0514 float:1.8744751E38 double:1.05313042E-314;
            r0 = "LastSeenTitle";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x0382;
        L_0x01a1:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.shareSectionRow;
            if (r10 != r0) goto L_0x0382;
        L_0x01a9:
            r10 = NUM; // 0x7f0d00aa float:1.874246E38 double:1.0531298615E-314;
            r0 = "AddExceptions";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x0382;
        L_0x01b7:
            r9 = r9.itemView;
            r9 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r9;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.detailRow;
            r1 = "windowBackgroundGrayShadow";
            if (r10 != r0) goto L_0x0249;
        L_0x01c6:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r5) goto L_0x01db;
        L_0x01ce:
            r10 = NUM; // 0x7f0d07fa float:1.8746256E38 double:1.0531307864E-314;
            r0 = "PrivacyForwardsInfo";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x023b;
        L_0x01db:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r4) goto L_0x01f0;
        L_0x01e3:
            r10 = NUM; // 0x7f0d0807 float:1.8746283E38 double:1.053130793E-314;
            r0 = "PrivacyProfilePhotoInfo";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x023b;
        L_0x01f0:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r6) goto L_0x0205;
        L_0x01f8:
            r10 = NUM; // 0x7f0d07f2 float:1.874624E38 double:1.0531307825E-314;
            r0 = "PrivacyCallsP2PHelp";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x023b;
        L_0x0205:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r7) goto L_0x021a;
        L_0x020d:
            r10 = NUM; // 0x7f0d0a65 float:1.8747512E38 double:1.0531310923E-314;
            r0 = "WhoCanCallMeInfo";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x023b;
        L_0x021a:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r3) goto L_0x022f;
        L_0x0222:
            r10 = NUM; // 0x7f0d0a60 float:1.8747502E38 double:1.05313109E-314;
            r0 = "WhoCanAddMeInfo";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x023b;
        L_0x022f:
            r10 = NUM; // 0x7f0d0304 float:1.874368E38 double:1.053130159E-314;
            r0 = "CustomHelp";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
        L_0x023b:
            r10 = r8.mContext;
            r0 = NUM; // 0x7var_e4 float:1.794504E38 double:1.0529356157E-314;
            r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r10, r0, r1);
            r9.setBackgroundDrawable(r10);
            goto L_0x0382;
        L_0x0249:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.shareDetailRow;
            if (r10 != r0) goto L_0x0382;
        L_0x0251:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r5) goto L_0x0266;
        L_0x0259:
            r10 = NUM; // 0x7f0d07fb float:1.8746258E38 double:1.053130787E-314;
            r0 = "PrivacyForwardsInfo2";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x02c6;
        L_0x0266:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r4) goto L_0x027b;
        L_0x026e:
            r10 = NUM; // 0x7f0d0808 float:1.8746285E38 double:1.0531307933E-314;
            r0 = "PrivacyProfilePhotoInfo2";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x02c6;
        L_0x027b:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r6) goto L_0x0290;
        L_0x0283:
            r10 = NUM; // 0x7f0d0306 float:1.8743685E38 double:1.05313016E-314;
            r0 = "CustomP2PInfo";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x02c6;
        L_0x0290:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r7) goto L_0x02a5;
        L_0x0298:
            r10 = NUM; // 0x7f0d0303 float:1.8743679E38 double:1.0531301585E-314;
            r0 = "CustomCallInfo";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x02c6;
        L_0x02a5:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.rulesType;
            if (r10 != r3) goto L_0x02ba;
        L_0x02ad:
            r10 = NUM; // 0x7f0d0307 float:1.8743687E38 double:1.0531301604E-314;
            r0 = "CustomShareInfo";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
            goto L_0x02c6;
        L_0x02ba:
            r10 = NUM; // 0x7f0d0308 float:1.8743689E38 double:1.053130161E-314;
            r0 = "CustomShareSettingsHelp";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
        L_0x02c6:
            r10 = r8.mContext;
            r0 = NUM; // 0x7var_e5 float:1.7945042E38 double:1.052935616E-314;
            r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r10, r0, r1);
            r9.setBackgroundDrawable(r10);
            goto L_0x0382;
        L_0x02d4:
            r9 = r9.itemView;
            r9 = (org.telegram.ui.Cells.TextSettingsCell) r9;
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.alwaysShareRow;
            r4 = "Users";
            r5 = NUM; // 0x7f0d038c float:1.8743956E38 double:1.053130226E-314;
            r6 = "EmpryUsersPlaceholder";
            if (r10 != r0) goto L_0x033a;
        L_0x02e7:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.currentPlus;
            r10 = r10.size();
            if (r10 == 0) goto L_0x0302;
        L_0x02f3:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.currentPlus;
            r10 = r10.size();
            r10 = org.telegram.messenger.LocaleController.formatPluralString(r4, r10);
            goto L_0x0306;
        L_0x0302:
            r10 = org.telegram.messenger.LocaleController.getString(r6, r5);
        L_0x0306:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.rulesType;
            if (r0 == 0) goto L_0x0324;
        L_0x030e:
            r0 = NUM; // 0x7f0d00cd float:1.874253E38 double:1.053129879E-314;
            r4 = "AlwaysAllow";
            r0 = org.telegram.messenger.LocaleController.getString(r4, r0);
            r4 = org.telegram.ui.PrivacyControlActivity.this;
            r4 = r4.neverShareRow;
            if (r4 == r1) goto L_0x0320;
        L_0x031f:
            r2 = 1;
        L_0x0320:
            r9.setTextAndValue(r0, r10, r2);
            goto L_0x0382;
        L_0x0324:
            r0 = NUM; // 0x7f0d00cf float:1.8742535E38 double:1.05312988E-314;
            r4 = "AlwaysShareWith";
            r0 = org.telegram.messenger.LocaleController.getString(r4, r0);
            r4 = org.telegram.ui.PrivacyControlActivity.this;
            r4 = r4.neverShareRow;
            if (r4 == r1) goto L_0x0336;
        L_0x0335:
            r2 = 1;
        L_0x0336:
            r9.setTextAndValue(r0, r10, r2);
            goto L_0x0382;
        L_0x033a:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.neverShareRow;
            if (r10 != r0) goto L_0x0382;
        L_0x0342:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.currentMinus;
            r10 = r10.size();
            if (r10 == 0) goto L_0x035d;
        L_0x034e:
            r10 = org.telegram.ui.PrivacyControlActivity.this;
            r10 = r10.currentMinus;
            r10 = r10.size();
            r10 = org.telegram.messenger.LocaleController.formatPluralString(r4, r10);
            goto L_0x0361;
        L_0x035d:
            r10 = org.telegram.messenger.LocaleController.getString(r6, r5);
        L_0x0361:
            r0 = org.telegram.ui.PrivacyControlActivity.this;
            r0 = r0.rulesType;
            if (r0 == 0) goto L_0x0376;
        L_0x0369:
            r0 = NUM; // 0x7f0d05ac float:1.874506E38 double:1.053130495E-314;
            r1 = "NeverAllow";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r9.setTextAndValue(r0, r10, r2);
            goto L_0x0382;
        L_0x0376:
            r0 = NUM; // 0x7f0d05ae float:1.8745064E38 double:1.053130496E-314;
            r1 = "NeverShareWith";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r9.setTextAndValue(r0, r10, r2);
        L_0x0382:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity$ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == PrivacyControlActivity.this.alwaysShareRow || i == PrivacyControlActivity.this.neverShareRow) {
                return 0;
            }
            if (i == PrivacyControlActivity.this.shareDetailRow || i == PrivacyControlActivity.this.detailRow) {
                return 1;
            }
            if (i == PrivacyControlActivity.this.sectionRow || i == PrivacyControlActivity.this.shareSectionRow) {
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
        if (i == 5) {
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
                privacyUsersActivity.setDelegate(new -$$Lambda$PrivacyControlActivity$CiNgBGPNaiZgZfd3WNhmwRR25KM(this, i));
                presentFragment(privacyUsersActivity);
            }
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
        User user;
        InputUser inputUser;
        TL_account_setPrivacy tL_account_setPrivacy = new TL_account_setPrivacy();
        int i2 = this.rulesType;
        if (i2 == 5) {
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
            for (i = 0; i < this.currentPlus.size(); i++) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) this.currentPlus.get(i));
                if (user != null) {
                    inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    if (inputUser != null) {
                        tL_inputPrivacyValueAllowUsers.users.add(inputUser);
                    }
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueAllowUsers);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TL_inputPrivacyValueDisallowUsers tL_inputPrivacyValueDisallowUsers = new TL_inputPrivacyValueDisallowUsers();
            for (i = 0; i < this.currentMinus.size(); i++) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) this.currentMinus.get(i));
                if (user != null) {
                    inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    if (inputUser != null) {
                        tL_inputPrivacyValueDisallowUsers.users.add(inputUser);
                    }
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueDisallowUsers);
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
                if (privacyRule instanceof TL_privacyValueAllowUsers) {
                    this.currentPlus.addAll(privacyRule.users);
                } else if (privacyRule instanceof TL_privacyValueDisallowUsers) {
                    this.currentMinus.addAll(privacyRule.users);
                } else {
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
        if (i == 0 || i == 2 || i == 3 || i == 5) {
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
