package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.view.View.MeasureSpec;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.GroupCreateCheckBox;

public class DialogCell extends BaseCell {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private int avatarTop = AndroidUtilities.dp(10.0f);
    private Chat chat = null;
    private GroupCreateCheckBox checkBox;
    private int checkDrawLeft;
    private int checkDrawTop = AndroidUtilities.dp(18.0f);
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop = AndroidUtilities.dp(39.0f);
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
    private long currentDialogId;
    private int currentEditDate;
    private CustomDialog customDialog;
    private boolean dialogMuted;
    private int dialogsType;
    private DraftMessage draftMessage;
    private boolean drawCheck1;
    private boolean drawCheck2;
    private boolean drawClock;
    private boolean drawCount;
    private boolean drawError;
    private boolean drawMention;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawPin;
    private boolean drawPinBackground;
    private boolean drawVerified;
    private EncryptedChat encryptedChat = null;
    private int errorLeft;
    private int errorTop = AndroidUtilities.dp(39.0f);
    private int halfCheckDrawLeft;
    private int index;
    private boolean isDialogCell;
    private boolean isSelected;
    private int lastMessageDate;
    private CharSequence lastMessageString;
    private CharSequence lastPrintString = null;
    private int lastSendState;
    private boolean lastUnreadState;
    private int mentionCount;
    private int mentionLeft;
    private int mentionWidth;
    private MessageObject message;
    private StaticLayout messageLayout;
    private int messageLeft;
    private int messageTop = AndroidUtilities.dp(40.0f);
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameMuteLeft;
    private int pinLeft;
    private int pinTop = AndroidUtilities.dp(39.0f);
    private RectF rect = new RectF();
    private StaticLayout timeLayout;
    private int timeLeft;
    private int timeTop = AndroidUtilities.dp(17.0f);
    private int unreadCount;
    public boolean useSeparator = false;
    private User user = null;

    public static class CustomDialog {
        public int date;
        public int id;
        public boolean isMedia;
        public String message;
        public boolean muted;
        public String name;
        public boolean pinned;
        public boolean sent;
        public int type;
        public int unread_count;
        public boolean verified;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public DialogCell(Context context, boolean z) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
        if (z) {
            this.checkBox = new GroupCreateCheckBox(context);
            this.checkBox.setVisibility(0);
            addView(this.checkBox);
        }
    }

    public void setDialog(TL_dialog tL_dialog, int i, int i2) {
        this.currentDialogId = tL_dialog.id;
        this.isDialogCell = true;
        this.index = i;
        this.dialogsType = i2;
        update(null);
    }

    public void setDialog(CustomDialog customDialog) {
        this.customDialog = customDialog;
        update(null);
    }

    public void setDialog(long j, MessageObject messageObject, int i) {
        this.currentDialogId = j;
        this.message = messageObject;
        this.isDialogCell = false;
        this.lastMessageDate = i;
        this.currentEditDate = messageObject != null ? messageObject.messageOwner.edit_date : 0;
        this.unreadCount = 0;
        this.mentionCount = 0;
        boolean z = messageObject != null && messageObject.isUnread();
        this.lastUnreadState = z;
        if (this.message != null) {
            this.lastSendState = this.message.messageOwner.send_state;
        }
        update(0);
    }

    public long getDialogId() {
        return this.currentDialogId;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    protected void onMeasure(int i, int i2) {
        if (this.checkBox != 0) {
            this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(NUM) + this.useSeparator);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.currentDialogId != 0 || this.customDialog != 0) {
            if (this.checkBox != 0) {
                i3 = LocaleController.isRTL != 0 ? (i3 - i) - AndroidUtilities.dp(42.0f) : AndroidUtilities.dp(42.0f);
                i = AndroidUtilities.dp(NUM);
                this.checkBox.layout(i3, i, this.checkBox.getMeasuredWidth() + i3, this.checkBox.getMeasuredHeight() + i);
            }
            if (z) {
                try {
                    buildLayout();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        String stringForMessageListDate;
        String format;
        String str;
        int i;
        int ceil;
        int measuredWidth;
        int measuredWidth2;
        String str2 = TtmlNode.ANONYMOUS_REGION_ID;
        String str3 = TtmlNode.ANONYMOUS_REGION_ID;
        CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID;
        CharSequence charSequence2 = this.isDialogCell ? (CharSequence) MessagesController.getInstance(r1.currentAccount).printingStrings.get(r1.currentDialogId) : null;
        TextPaint textPaint = Theme.dialogs_namePaint;
        TextPaint textPaint2 = Theme.dialogs_messagePaint;
        r1.drawNameGroup = false;
        r1.drawNameBroadcast = false;
        r1.drawNameLock = false;
        r1.drawNameBot = false;
        r1.drawVerified = false;
        r1.drawPinBackground = false;
        boolean z = true;
        int isUserSelf = UserObject.isUserSelf(r1.user) ^ 1;
        String str4 = VERSION.SDK_INT >= 18 ? "%s: \u2068%s\u2069" : "%s: %s";
        r1.lastMessageString = r1.message != null ? r1.message.messageText : null;
        if (r1.customDialog != null) {
            CharSequence replaceEmoji;
            boolean z2;
            if (r1.customDialog.type == 2) {
                r1.drawNameLock = true;
                r1.nameLockTop = AndroidUtilities.dp(16.5f);
                if (LocaleController.isRTL) {
                    r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - Theme.dialogs_lockDrawable.getIntrinsicWidth();
                    r1.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
                }
            } else {
                r1.drawVerified = r1.customDialog.verified;
                if (r1.customDialog.type == 1) {
                    r1.drawNameGroup = true;
                    r1.nameLockTop = AndroidUtilities.dp(17.5f);
                    if (LocaleController.isRTL) {
                        r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                        r1.nameLeft = AndroidUtilities.dp(14.0f);
                    } else {
                        r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                    }
                } else if (LocaleController.isRTL) {
                    r1.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    r1.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                }
            }
            if (r1.customDialog.type == 1) {
                str2 = LocaleController.getString("FromYou", C0446R.string.FromYou);
                if (r1.customDialog.isMedia) {
                    TextPaint textPaint3 = Theme.dialogs_messagePrintingPaint;
                    charSequence = SpannableStringBuilder.valueOf(String.format(str4, new Object[]{str2, r1.message.messageText}));
                    charSequence.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_attachMessage)), str2.length() + 2, charSequence.length(), 33);
                    textPaint2 = textPaint3;
                } else {
                    str3 = r1.customDialog.message;
                    if (str3.length() > 150) {
                        str3 = str3.substring(0, 150);
                    }
                    charSequence = SpannableStringBuilder.valueOf(String.format(str4, new Object[]{str2, str3.replace('\n', ' ')}));
                }
                if (charSequence.length() > 0) {
                    charSequence.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_nameMessage)), 0, str2.length() + 1, 33);
                }
                replaceEmoji = Emoji.replaceEmoji(charSequence, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                z2 = false;
            } else {
                replaceEmoji = r1.customDialog.message;
                if (r1.customDialog.isMedia) {
                    textPaint2 = Theme.dialogs_messagePrintingPaint;
                }
                z2 = true;
            }
            stringForMessageListDate = LocaleController.stringForMessageListDate((long) r1.customDialog.date);
            if (r1.customDialog.unread_count != 0) {
                r1.drawCount = true;
                format = String.format("%d", new Object[]{Integer.valueOf(r1.customDialog.unread_count)});
            } else {
                r1.drawCount = false;
                format = null;
            }
            if (r1.customDialog.sent) {
                r1.drawCheck1 = true;
                r1.drawCheck2 = true;
                r1.drawClock = false;
                r1.drawError = false;
            } else {
                r1.drawCheck1 = false;
                r1.drawCheck2 = false;
                r1.drawClock = false;
                r1.drawError = false;
            }
            str = r1.customDialog.name;
            if (r1.customDialog.type == 2) {
                textPaint = Theme.dialogs_nameEncryptedPaint;
            }
            i = z2;
            str3 = stringForMessageListDate;
            charSequence = replaceEmoji;
            str2 = str;
            str = null;
        } else {
            String str5;
            CharSequence charSequence3;
            String str6;
            boolean z3;
            Object[] objArr;
            if (r1.encryptedChat != null) {
                r1.drawNameLock = true;
                r1.nameLockTop = AndroidUtilities.dp(16.5f);
                if (LocaleController.isRTL) {
                    r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - Theme.dialogs_lockDrawable.getIntrinsicWidth();
                    r1.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
                }
            } else if (r1.chat != null) {
                if (r1.chat.id >= 0) {
                    if (!ChatObject.isChannel(r1.chat) || r1.chat.megagroup) {
                        r1.drawNameGroup = true;
                        r1.nameLockTop = AndroidUtilities.dp(17.5f);
                        r1.drawVerified = r1.chat.verified;
                        if (LocaleController.isRTL) {
                            r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                            r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                        } else {
                            r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                            r1.nameLeft = AndroidUtilities.dp(14.0f);
                        }
                    }
                }
                r1.drawNameBroadcast = true;
                r1.nameLockTop = AndroidUtilities.dp(16.5f);
                r1.drawVerified = r1.chat.verified;
                if (LocaleController.isRTL) {
                    if (r1.drawNameGroup) {
                    }
                    r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                    r1.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    if (r1.drawNameGroup) {
                    }
                    r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                }
            } else {
                if (LocaleController.isRTL) {
                    r1.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    r1.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                }
                if (r1.user != null) {
                    if (r1.user.bot) {
                        r1.drawNameBot = true;
                        r1.nameLockTop = AndroidUtilities.dp(16.5f);
                        if (LocaleController.isRTL) {
                            r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - Theme.dialogs_botDrawable.getIntrinsicWidth();
                            r1.nameLeft = AndroidUtilities.dp(14.0f);
                        } else {
                            r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                            r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_botDrawable.getIntrinsicWidth();
                        }
                    }
                    r1.drawVerified = r1.user.verified;
                }
            }
            int i2 = r1.lastMessageDate;
            if (r1.lastMessageDate == 0 && r1.message != null) {
                i2 = r1.message.messageOwner.date;
            }
            if (r1.isDialogCell) {
                str5 = str4;
                r1.draftMessage = DataQuery.getInstance(r1.currentAccount).getDraft(r1.currentDialogId);
                if (r1.draftMessage != null) {
                    if (TextUtils.isEmpty(r1.draftMessage.message)) {
                        if (r1.draftMessage.reply_to_msg_id != 0) {
                        }
                        charSequence3 = null;
                        r1.draftMessage = null;
                    }
                    if (i2 > r1.draftMessage.date) {
                    }
                }
                if (!ChatObject.isChannel(r1.chat) || r1.chat.megagroup || r1.chat.creator || (r1.chat.admin_rights != null && r1.chat.admin_rights.post_messages)) {
                    if (r1.chat != null) {
                        if (!r1.chat.left) {
                            if (r1.chat.kicked) {
                            }
                        }
                    }
                    charSequence3 = null;
                }
                charSequence3 = null;
                r1.draftMessage = null;
            } else {
                str5 = str4;
                charSequence3 = null;
                r1.draftMessage = null;
            }
            if (charSequence2 != null) {
                r1.lastPrintString = charSequence2;
                textPaint2 = Theme.dialogs_messagePrintingPaint;
                charSequence = charSequence2;
            } else {
                r1.lastPrintString = charSequence3;
                if (r1.draftMessage == null) {
                    str6 = str5;
                    if (r1.message != null) {
                        User user;
                        Chat chat;
                        if (r1.message.isFromUser()) {
                            user = MessagesController.getInstance(r1.currentAccount).getUser(Integer.valueOf(r1.message.messageOwner.from_id));
                            chat = null;
                        } else {
                            chat = MessagesController.getInstance(r1.currentAccount).getChat(Integer.valueOf(r1.message.messageOwner.to_id.channel_id));
                            user = null;
                        }
                        if (r1.dialogsType == 3 && UserObject.isUserSelf(r1.user)) {
                            charSequence = LocaleController.getString("SavedMessagesInfo", C0446R.string.SavedMessagesInfo);
                            isUserSelf = 0;
                            z = isUserSelf;
                            i = 1;
                            if (r1.draftMessage == null) {
                                str3 = LocaleController.stringForMessageListDate((long) r1.draftMessage.date);
                            } else if (r1.lastMessageDate == 0) {
                                str3 = LocaleController.stringForMessageListDate((long) r1.lastMessageDate);
                            } else if (r1.message != null) {
                                str3 = LocaleController.stringForMessageListDate((long) r1.message.messageOwner.date);
                            }
                            if (r1.message != null) {
                                if (r1.unreadCount != 0) {
                                    z3 = r1.unreadCount != 1 ? true : true;
                                    r1.drawCount = z3;
                                    objArr = new Object[z3];
                                    objArr[0] = Integer.valueOf(r1.unreadCount);
                                    str6 = String.format("%d", objArr);
                                    if (r1.mentionCount == 0) {
                                        r1.drawMention = z3;
                                        format = "@";
                                    } else {
                                        r1.drawMention = false;
                                        format = null;
                                    }
                                    if (r1.message.isOut()) {
                                    }
                                    r1.drawCheck1 = false;
                                    r1.drawCheck2 = false;
                                    r1.drawClock = false;
                                    r1.drawError = false;
                                    str = format;
                                    format = str6;
                                }
                                z3 = true;
                                r1.drawCount = false;
                                str6 = null;
                                if (r1.mentionCount == 0) {
                                    r1.drawMention = false;
                                    format = null;
                                } else {
                                    r1.drawMention = z3;
                                    format = "@";
                                }
                                if (r1.message.isOut()) {
                                }
                                r1.drawCheck1 = false;
                                r1.drawCheck2 = false;
                                r1.drawClock = false;
                                r1.drawError = false;
                                str = format;
                                format = str6;
                            } else {
                                r1.drawCheck1 = false;
                                r1.drawCheck2 = false;
                                r1.drawClock = false;
                                r1.drawCount = false;
                                r1.drawMention = false;
                                r1.drawError = false;
                                format = null;
                                str = null;
                            }
                            if (r1.chat == null) {
                                str2 = r1.chat.title;
                            } else if (r1.user != null) {
                                if (UserObject.isUserSelf(r1.user)) {
                                    if (r1.dialogsType == 3) {
                                        r1.drawPinBackground = true;
                                    }
                                    str2 = LocaleController.getString("SavedMessages", C0446R.string.SavedMessages);
                                } else {
                                    if (r1.user.id / 1000 != 777) {
                                    }
                                    str2 = UserObject.getUserName(r1.user);
                                }
                                if (r1.encryptedChat != null) {
                                    textPaint = Theme.dialogs_nameEncryptedPaint;
                                }
                            }
                            if (str2.length() == 0) {
                                str2 = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
                            }
                        } else if (r1.message.messageOwner instanceof TL_messageService) {
                            if (ChatObject.isChannel(r1.chat) && (r1.message.messageOwner.action instanceof TL_messageActionHistoryClear)) {
                                charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                                isUserSelf = 0;
                            } else {
                                charSequence = r1.message.messageText;
                            }
                            textPaint2 = Theme.dialogs_messagePrintingPaint;
                        } else if (r1.chat != null && r1.chat.id > 0 && chat == null) {
                            stringForMessageListDate = r1.message.isOutOwner() ? LocaleController.getString("FromYou", C0446R.string.FromYou) : user != null ? UserObject.getFirstName(user).replace("\n", TtmlNode.ANONYMOUS_REGION_ID) : chat != null ? chat.title.replace("\n", TtmlNode.ANONYMOUS_REGION_ID) : "DELETED";
                            if (r1.message.caption != null) {
                                format = r1.message.caption.toString();
                                if (format.length() > 150) {
                                    format = format.substring(0, 150);
                                }
                                charSequence2 = SpannableStringBuilder.valueOf(String.format(str6, new Object[]{stringForMessageListDate, format.replace('\n', ' ')}));
                            } else if (r1.message.messageOwner.media != null && !r1.message.isMediaEmpty()) {
                                SpannableStringBuilder valueOf;
                                TextPaint textPaint4 = Theme.dialogs_messagePrintingPaint;
                                if (r1.message.messageOwner.media instanceof TL_messageMediaGame) {
                                    if (VERSION.SDK_INT >= 18) {
                                        valueOf = SpannableStringBuilder.valueOf(String.format("%s: \ud83c\udfae \u2068%s\u2069", new Object[]{stringForMessageListDate, r1.message.messageOwner.media.game.title}));
                                    } else {
                                        valueOf = SpannableStringBuilder.valueOf(String.format("%s: \ud83c\udfae %s", new Object[]{stringForMessageListDate, r1.message.messageOwner.media.game.title}));
                                    }
                                } else if (r1.message.type != 14) {
                                    valueOf = SpannableStringBuilder.valueOf(String.format(str6, new Object[]{stringForMessageListDate, r1.message.messageText}));
                                } else if (VERSION.SDK_INT >= 18) {
                                    valueOf = SpannableStringBuilder.valueOf(String.format("%s: \ud83c\udfa7 \u2068%s - %s\u2069", new Object[]{stringForMessageListDate, r1.message.getMusicAuthor(), r1.message.getMusicTitle()}));
                                } else {
                                    valueOf = SpannableStringBuilder.valueOf(String.format("%s: \ud83c\udfa7 %s - %s", new Object[]{stringForMessageListDate, r1.message.getMusicAuthor(), r1.message.getMusicTitle()}));
                                }
                                valueOf.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_attachMessage)), stringForMessageListDate.length() + 2, valueOf.length(), 33);
                                SpannableStringBuilder spannableStringBuilder = valueOf;
                                textPaint2 = textPaint4;
                                charSequence2 = spannableStringBuilder;
                            } else if (r1.message.messageOwner.message != null) {
                                format = r1.message.messageOwner.message;
                                if (format.length() > 150) {
                                    format = format.substring(0, 150);
                                }
                                charSequence2 = SpannableStringBuilder.valueOf(String.format(str6, new Object[]{stringForMessageListDate, format.replace('\n', ' ')}));
                            } else {
                                charSequence2 = SpannableStringBuilder.valueOf(TtmlNode.ANONYMOUS_REGION_ID);
                            }
                            if (charSequence2.length() > 0) {
                                charSequence2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_nameMessage)), 0, stringForMessageListDate.length() + 1, 33);
                            }
                            charSequence = Emoji.replaceEmoji(charSequence2, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        } else if ((r1.message.messageOwner.media instanceof TL_messageMediaPhoto) && (r1.message.messageOwner.media.photo instanceof TL_photoEmpty) && r1.message.messageOwner.media.ttl_seconds != 0) {
                            charSequence = LocaleController.getString("AttachPhotoExpired", C0446R.string.AttachPhotoExpired);
                        } else if ((r1.message.messageOwner.media instanceof TL_messageMediaDocument) && (r1.message.messageOwner.media.document instanceof TL_documentEmpty) && r1.message.messageOwner.media.ttl_seconds != 0) {
                            charSequence = LocaleController.getString("AttachVideoExpired", C0446R.string.AttachVideoExpired);
                        } else if (r1.message.caption != null) {
                            charSequence = r1.message.caption;
                        } else {
                            if (r1.message.messageOwner.media instanceof TL_messageMediaGame) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("\ud83c\udfae ");
                                stringBuilder.append(r1.message.messageOwner.media.game.title);
                                charSequence = stringBuilder.toString();
                            } else if (r1.message.type == 14) {
                                charSequence = String.format("\ud83c\udfa7 %s - %s", new Object[]{r1.message.getMusicAuthor(), r1.message.getMusicTitle()});
                            } else {
                                charSequence = r1.message.messageText;
                            }
                            if (!(r1.message.messageOwner.media == null || r1.message.isMediaEmpty())) {
                                textPaint2 = Theme.dialogs_messagePrintingPaint;
                            }
                        }
                    } else if (r1.encryptedChat != null) {
                        textPaint2 = Theme.dialogs_messagePrintingPaint;
                        if (r1.encryptedChat instanceof TL_encryptedChatRequested) {
                            charSequence = LocaleController.getString("EncryptionProcessing", C0446R.string.EncryptionProcessing);
                        } else {
                            if (r1.encryptedChat instanceof TL_encryptedChatWaiting) {
                                if (r1.user == null || r1.user.first_name == null) {
                                    charSequence = LocaleController.formatString("AwaitingEncryption", C0446R.string.AwaitingEncryption, TtmlNode.ANONYMOUS_REGION_ID);
                                } else {
                                    i = 1;
                                    charSequence = LocaleController.formatString("AwaitingEncryption", C0446R.string.AwaitingEncryption, r1.user.first_name);
                                }
                            } else if (r1.encryptedChat instanceof TL_encryptedChatDiscarded) {
                                charSequence = LocaleController.getString("EncryptionRejected", C0446R.string.EncryptionRejected);
                            } else if (r1.encryptedChat instanceof TL_encryptedChat) {
                                if (r1.encryptedChat.admin_id != UserConfig.getInstance(r1.currentAccount).getClientUserId()) {
                                    charSequence = LocaleController.getString("EncryptedChatStartedIncoming", C0446R.string.EncryptedChatStartedIncoming);
                                } else if (r1.user == null || r1.user.first_name == null) {
                                    charSequence = LocaleController.formatString("EncryptedChatStartedOutgoing", C0446R.string.EncryptedChatStartedOutgoing, TtmlNode.ANONYMOUS_REGION_ID);
                                } else {
                                    i = 1;
                                    charSequence = LocaleController.formatString("EncryptedChatStartedOutgoing", C0446R.string.EncryptedChatStartedOutgoing, r1.user.first_name);
                                }
                            }
                            z = i;
                            if (r1.draftMessage == null) {
                                str3 = LocaleController.stringForMessageListDate((long) r1.draftMessage.date);
                            } else if (r1.lastMessageDate == 0) {
                                str3 = LocaleController.stringForMessageListDate((long) r1.lastMessageDate);
                            } else if (r1.message != null) {
                                str3 = LocaleController.stringForMessageListDate((long) r1.message.messageOwner.date);
                            }
                            if (r1.message != null) {
                                r1.drawCheck1 = false;
                                r1.drawCheck2 = false;
                                r1.drawClock = false;
                                r1.drawCount = false;
                                r1.drawMention = false;
                                r1.drawError = false;
                                format = null;
                                str = null;
                            } else {
                                if (r1.unreadCount != 0) {
                                    if (r1.unreadCount != 1) {
                                        if (!(r1.unreadCount == r1.mentionCount && r1.message != null && r1.message.messageOwner.mentioned)) {
                                        }
                                    }
                                    r1.drawCount = z3;
                                    objArr = new Object[z3];
                                    objArr[0] = Integer.valueOf(r1.unreadCount);
                                    str6 = String.format("%d", objArr);
                                    if (r1.mentionCount == 0) {
                                        r1.drawMention = z3;
                                        format = "@";
                                    } else {
                                        r1.drawMention = false;
                                        format = null;
                                    }
                                    if (r1.message.isOut() || r1.draftMessage != null || r10 == 0) {
                                        r1.drawCheck1 = false;
                                        r1.drawCheck2 = false;
                                        r1.drawClock = false;
                                        r1.drawError = false;
                                    } else if (r1.message.isSending()) {
                                        r1.drawCheck1 = false;
                                        r1.drawCheck2 = false;
                                        r1.drawClock = true;
                                        r1.drawError = false;
                                    } else if (r1.message.isSendError()) {
                                        r1.drawCheck1 = false;
                                        r1.drawCheck2 = false;
                                        r1.drawClock = false;
                                        r1.drawError = true;
                                        r1.drawCount = false;
                                        r1.drawMention = false;
                                    } else if (r1.message.isSent()) {
                                        boolean z4;
                                        if (r1.message.isUnread()) {
                                            if (!ChatObject.isChannel(r1.chat) || r1.chat.megagroup) {
                                                z4 = false;
                                                r1.drawCheck1 = z4;
                                                r1.drawCheck2 = true;
                                                r1.drawClock = false;
                                                r1.drawError = false;
                                            }
                                        }
                                        z4 = true;
                                        r1.drawCheck1 = z4;
                                        r1.drawCheck2 = true;
                                        r1.drawClock = false;
                                        r1.drawError = false;
                                    }
                                    str = format;
                                    format = str6;
                                }
                                z3 = true;
                                r1.drawCount = false;
                                str6 = null;
                                if (r1.mentionCount == 0) {
                                    r1.drawMention = false;
                                    format = null;
                                } else {
                                    r1.drawMention = z3;
                                    format = "@";
                                }
                                if (r1.message.isOut()) {
                                }
                                r1.drawCheck1 = false;
                                r1.drawCheck2 = false;
                                r1.drawClock = false;
                                r1.drawError = false;
                                str = format;
                                format = str6;
                            }
                            if (r1.chat == null) {
                                str2 = r1.chat.title;
                            } else if (r1.user != null) {
                                if (UserObject.isUserSelf(r1.user)) {
                                    if (r1.dialogsType == 3) {
                                        r1.drawPinBackground = true;
                                    }
                                    str2 = LocaleController.getString("SavedMessages", C0446R.string.SavedMessages);
                                } else if (r1.user.id / 1000 != 777 || r1.user.id / 1000 == 333 || ContactsController.getInstance(r1.currentAccount).contactsDict.get(Integer.valueOf(r1.user.id)) != null) {
                                    str2 = UserObject.getUserName(r1.user);
                                } else if (ContactsController.getInstance(r1.currentAccount).contactsDict.size() == 0 && (!ContactsController.getInstance(r1.currentAccount).contactsLoaded || ContactsController.getInstance(r1.currentAccount).isLoadingContacts())) {
                                    str2 = UserObject.getUserName(r1.user);
                                } else if (r1.user.phone == null || r1.user.phone.length() == 0) {
                                    str2 = UserObject.getUserName(r1.user);
                                } else {
                                    PhoneFormat instance = PhoneFormat.getInstance();
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("+");
                                    stringBuilder2.append(r1.user.phone);
                                    str2 = instance.format(stringBuilder2.toString());
                                }
                                if (r1.encryptedChat != null) {
                                    textPaint = Theme.dialogs_nameEncryptedPaint;
                                }
                            }
                            if (str2.length() == 0) {
                                str2 = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
                            }
                        }
                    }
                } else if (TextUtils.isEmpty(r1.draftMessage.message)) {
                    Object string = LocaleController.getString("Draft", C0446R.string.Draft);
                    SpannableStringBuilder valueOf2 = SpannableStringBuilder.valueOf(string);
                    valueOf2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_draft)), 0, string.length(), 33);
                    charSequence = valueOf2;
                } else {
                    stringForMessageListDate = r1.draftMessage.message;
                    if (stringForMessageListDate.length() > 150) {
                        stringForMessageListDate = stringForMessageListDate.substring(0, 150);
                    }
                    charSequence = SpannableStringBuilder.valueOf(String.format(str5, new Object[]{LocaleController.getString("Draft", C0446R.string.Draft), stringForMessageListDate.replace('\n', ' ')}));
                    charSequence.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_draft)), 0, format.length() + 1, 33);
                    charSequence = Emoji.replaceEmoji(charSequence, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                }
                boolean z5 = false;
                z = true;
                if (r1.draftMessage == null) {
                    str3 = LocaleController.stringForMessageListDate((long) r1.draftMessage.date);
                } else if (r1.lastMessageDate == 0) {
                    str3 = LocaleController.stringForMessageListDate((long) r1.lastMessageDate);
                } else if (r1.message != null) {
                    str3 = LocaleController.stringForMessageListDate((long) r1.message.messageOwner.date);
                }
                if (r1.message != null) {
                    r1.drawCheck1 = false;
                    r1.drawCheck2 = false;
                    r1.drawClock = false;
                    r1.drawCount = false;
                    r1.drawMention = false;
                    r1.drawError = false;
                    format = null;
                    str = null;
                } else {
                    if (r1.unreadCount != 0) {
                        if (r1.unreadCount != 1) {
                        }
                        r1.drawCount = z3;
                        objArr = new Object[z3];
                        objArr[0] = Integer.valueOf(r1.unreadCount);
                        str6 = String.format("%d", objArr);
                        if (r1.mentionCount == 0) {
                            r1.drawMention = z3;
                            format = "@";
                        } else {
                            r1.drawMention = false;
                            format = null;
                        }
                        if (r1.message.isOut()) {
                        }
                        r1.drawCheck1 = false;
                        r1.drawCheck2 = false;
                        r1.drawClock = false;
                        r1.drawError = false;
                        str = format;
                        format = str6;
                    }
                    z3 = true;
                    r1.drawCount = false;
                    str6 = null;
                    if (r1.mentionCount == 0) {
                        r1.drawMention = false;
                        format = null;
                    } else {
                        r1.drawMention = z3;
                        format = "@";
                    }
                    if (r1.message.isOut()) {
                    }
                    r1.drawCheck1 = false;
                    r1.drawCheck2 = false;
                    r1.drawClock = false;
                    r1.drawError = false;
                    str = format;
                    format = str6;
                }
                if (r1.chat == null) {
                    str2 = r1.chat.title;
                } else if (r1.user != null) {
                    if (UserObject.isUserSelf(r1.user)) {
                        if (r1.dialogsType == 3) {
                            r1.drawPinBackground = true;
                        }
                        str2 = LocaleController.getString("SavedMessages", C0446R.string.SavedMessages);
                    } else {
                        if (r1.user.id / 1000 != 777) {
                        }
                        str2 = UserObject.getUserName(r1.user);
                    }
                    if (r1.encryptedChat != null) {
                        textPaint = Theme.dialogs_nameEncryptedPaint;
                    }
                }
                if (str2.length() == 0) {
                    str2 = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
                }
            }
            i = 1;
            z = true;
            if (r1.draftMessage == null) {
                str3 = LocaleController.stringForMessageListDate((long) r1.draftMessage.date);
            } else if (r1.lastMessageDate == 0) {
                str3 = LocaleController.stringForMessageListDate((long) r1.lastMessageDate);
            } else if (r1.message != null) {
                str3 = LocaleController.stringForMessageListDate((long) r1.message.messageOwner.date);
            }
            if (r1.message != null) {
                if (r1.unreadCount != 0) {
                    if (r1.unreadCount != 1) {
                    }
                    r1.drawCount = z3;
                    objArr = new Object[z3];
                    objArr[0] = Integer.valueOf(r1.unreadCount);
                    str6 = String.format("%d", objArr);
                    if (r1.mentionCount == 0) {
                        r1.drawMention = z3;
                        format = "@";
                    } else {
                        r1.drawMention = false;
                        format = null;
                    }
                    if (r1.message.isOut()) {
                    }
                    r1.drawCheck1 = false;
                    r1.drawCheck2 = false;
                    r1.drawClock = false;
                    r1.drawError = false;
                    str = format;
                    format = str6;
                }
                z3 = true;
                r1.drawCount = false;
                str6 = null;
                if (r1.mentionCount == 0) {
                    r1.drawMention = false;
                    format = null;
                } else {
                    r1.drawMention = z3;
                    format = "@";
                }
                if (r1.message.isOut()) {
                }
                r1.drawCheck1 = false;
                r1.drawCheck2 = false;
                r1.drawClock = false;
                r1.drawError = false;
                str = format;
                format = str6;
            } else {
                r1.drawCheck1 = false;
                r1.drawCheck2 = false;
                r1.drawClock = false;
                r1.drawCount = false;
                r1.drawMention = false;
                r1.drawError = false;
                format = null;
                str = null;
            }
            if (r1.chat == null) {
                str2 = r1.chat.title;
            } else if (r1.user != null) {
                if (UserObject.isUserSelf(r1.user)) {
                    if (r1.user.id / 1000 != 777) {
                    }
                    str2 = UserObject.getUserName(r1.user);
                } else {
                    if (r1.dialogsType == 3) {
                        r1.drawPinBackground = true;
                    }
                    str2 = LocaleController.getString("SavedMessages", C0446R.string.SavedMessages);
                }
                if (r1.encryptedChat != null) {
                    textPaint = Theme.dialogs_nameEncryptedPaint;
                }
            }
            if (str2.length() == 0) {
                str2 = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
            }
        }
        if (z) {
            ceil = (int) Math.ceil((double) Theme.dialogs_timePaint.measureText(str3));
            r1.timeLayout = new StaticLayout(str3, Theme.dialogs_timePaint, ceil, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (LocaleController.isRTL) {
                r1.timeLeft = AndroidUtilities.dp(15.0f);
            } else {
                r1.timeLeft = (getMeasuredWidth() - AndroidUtilities.dp(15.0f)) - ceil;
            }
        } else {
            r1.timeLayout = null;
            r1.timeLeft = 0;
            ceil = 0;
        }
        if (LocaleController.isRTL) {
            measuredWidth = ((getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - ceil;
            r1.nameLeft += ceil;
        } else {
            measuredWidth = ((getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp(14.0f)) - ceil;
        }
        if (r1.drawNameLock) {
            measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        } else if (r1.drawNameGroup) {
            measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
        } else if (r1.drawNameBroadcast) {
            measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
        } else if (r1.drawNameBot) {
            measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
        }
        int intrinsicWidth;
        if (r1.drawClock) {
            intrinsicWidth = Theme.dialogs_clockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            measuredWidth -= intrinsicWidth;
            if (LocaleController.isRTL) {
                r1.checkDrawLeft = (r1.timeLeft + ceil) + AndroidUtilities.dp(5.0f);
                r1.nameLeft += intrinsicWidth;
            } else {
                r1.checkDrawLeft = r1.timeLeft - intrinsicWidth;
            }
        } else if (r1.drawCheck2) {
            intrinsicWidth = Theme.dialogs_checkDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            measuredWidth -= intrinsicWidth;
            if (r1.drawCheck1) {
                measuredWidth -= Theme.dialogs_halfCheckDrawable.getIntrinsicWidth() - AndroidUtilities.dp(8.0f);
                if (LocaleController.isRTL) {
                    r1.checkDrawLeft = (r1.timeLeft + ceil) + AndroidUtilities.dp(5.0f);
                    r1.halfCheckDrawLeft = r1.checkDrawLeft + AndroidUtilities.dp(5.5f);
                    r1.nameLeft += (intrinsicWidth + Theme.dialogs_halfCheckDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(8.0f);
                } else {
                    r1.halfCheckDrawLeft = r1.timeLeft - intrinsicWidth;
                    r1.checkDrawLeft = r1.halfCheckDrawLeft - AndroidUtilities.dp(5.5f);
                }
            } else if (LocaleController.isRTL) {
                r1.checkDrawLeft = (r1.timeLeft + ceil) + AndroidUtilities.dp(5.0f);
                r1.nameLeft += intrinsicWidth;
            } else {
                r1.checkDrawLeft = r1.timeLeft - intrinsicWidth;
            }
        }
        if (r1.dialogMuted && !r1.drawVerified) {
            ceil = AndroidUtilities.dp(6.0f) + Theme.dialogs_muteDrawable.getIntrinsicWidth();
            measuredWidth -= ceil;
            if (LocaleController.isRTL) {
                r1.nameLeft += ceil;
            }
        } else if (r1.drawVerified) {
            ceil = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
            measuredWidth -= ceil;
            if (LocaleController.isRTL) {
                r1.nameLeft += ceil;
            }
        }
        measuredWidth = Math.max(AndroidUtilities.dp(12.0f), measuredWidth);
        try {
            r1.nameLayout = new StaticLayout(TextUtils.ellipsize(str2.replace('\n', ' '), textPaint, (float) (measuredWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint, measuredWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        int measuredWidth3 = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
        if (LocaleController.isRTL) {
            r1.messageLeft = AndroidUtilities.dp(16.0f);
            measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        } else {
            r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            measuredWidth2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
        }
        r1.avatarImage.setImageCoords(measuredWidth2, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        int dp;
        if (r1.drawError) {
            dp = AndroidUtilities.dp(31.0f);
            measuredWidth3 -= dp;
            if (LocaleController.isRTL) {
                r1.errorLeft = AndroidUtilities.dp(11.0f);
                r1.messageLeft += dp;
            } else {
                r1.errorLeft = getMeasuredWidth() - AndroidUtilities.dp(34.0f);
            }
        } else {
            if (format == null) {
                if (str == null) {
                    if (r1.drawPin) {
                        dp = Theme.dialogs_pinnedDrawable.getIntrinsicWidth() + AndroidUtilities.dp(8.0f);
                        measuredWidth3 -= dp;
                        if (LocaleController.isRTL) {
                            r1.pinLeft = AndroidUtilities.dp(14.0f);
                            r1.messageLeft += dp;
                        } else {
                            r1.pinLeft = (getMeasuredWidth() - Theme.dialogs_pinnedDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(14.0f);
                        }
                    }
                    r1.drawCount = false;
                    r1.drawMention = false;
                }
            }
            if (format != null) {
                r1.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format)));
                r1.countLayout = new StaticLayout(format, Theme.dialogs_countTextPaint, r1.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                dp = r1.countWidth + AndroidUtilities.dp(18.0f);
                measuredWidth3 -= dp;
                if (LocaleController.isRTL) {
                    r1.countLeft = AndroidUtilities.dp(19.0f);
                    r1.messageLeft += dp;
                } else {
                    r1.countLeft = (getMeasuredWidth() - r1.countWidth) - AndroidUtilities.dp(19.0f);
                }
                r1.drawCount = true;
            } else {
                r1.countWidth = 0;
            }
            if (str != null) {
                r1.mentionWidth = AndroidUtilities.dp(12.0f);
                dp = r1.mentionWidth + AndroidUtilities.dp(18.0f);
                measuredWidth3 -= dp;
                if (LocaleController.isRTL) {
                    r1.mentionLeft = AndroidUtilities.dp(19.0f) + (r1.countWidth != 0 ? r1.countWidth + AndroidUtilities.dp(18.0f) : 0);
                    r1.messageLeft += dp;
                } else {
                    r1.mentionLeft = ((getMeasuredWidth() - r1.mentionWidth) - AndroidUtilities.dp(19.0f)) - (r1.countWidth != 0 ? r1.countWidth + AndroidUtilities.dp(18.0f) : 0);
                }
                r1.drawMention = true;
            }
        }
        if (i != 0) {
            if (charSequence == null) {
                charSequence = TtmlNode.ANONYMOUS_REGION_ID;
            }
            stringForMessageListDate = charSequence.toString();
            if (stringForMessageListDate.length() > 150) {
                stringForMessageListDate = stringForMessageListDate.substring(0, 150);
            }
            charSequence = Emoji.replaceEmoji(stringForMessageListDate.replace('\n', ' '), Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
        }
        measuredWidth3 = Math.max(AndroidUtilities.dp(12.0f), measuredWidth3);
        try {
            r1.messageLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint2, (float) (measuredWidth3 - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint2, measuredWidth3, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        float lineLeft;
        double ceil2;
        double d;
        if (LocaleController.isRTL) {
            if (r1.nameLayout != null && r1.nameLayout.getLineCount() > 0) {
                lineLeft = r1.nameLayout.getLineLeft(0);
                ceil2 = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                if (r1.dialogMuted && !r1.drawVerified) {
                    r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) measuredWidth) - ceil2)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_muteDrawable.getIntrinsicWidth()));
                } else if (r1.drawVerified) {
                    r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) measuredWidth) - ceil2)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                }
                if (lineLeft == 0.0f) {
                    d = (double) measuredWidth;
                    if (ceil2 < d) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) + (d - ceil2));
                    }
                }
            }
            if (r1.messageLayout != null && r1.messageLayout.getLineCount() > 0 && r1.messageLayout.getLineLeft(0) == 0.0f) {
                d = Math.ceil((double) r1.messageLayout.getLineWidth(0));
                ceil2 = (double) measuredWidth3;
                if (d < ceil2) {
                    r1.messageLeft = (int) (((double) r1.messageLeft) + (ceil2 - d));
                    return;
                }
                return;
            }
            return;
        }
        if (r1.nameLayout != null && r1.nameLayout.getLineCount() > 0) {
            lineLeft = r1.nameLayout.getLineRight(0);
            if (lineLeft == ((float) measuredWidth)) {
                ceil2 = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                double d2 = (double) measuredWidth;
                if (ceil2 < d2) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) - (d2 - ceil2));
                }
            }
            if (r1.dialogMuted || r1.drawVerified) {
                r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + lineLeft) + ((float) AndroidUtilities.dp(6.0f)));
            }
        }
        if (r1.messageLayout != null && r1.messageLayout.getLineCount() > 0 && r1.messageLayout.getLineRight(0) == ((float) measuredWidth3)) {
            d = Math.ceil((double) r1.messageLayout.getLineWidth(0));
            ceil2 = (double) measuredWidth3;
            if (d < ceil2) {
                r1.messageLeft = (int) (((double) r1.messageLeft) - (ceil2 - d));
            }
        }
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
    }

    private ArrayList<TL_dialog> getDialogsArray() {
        if (this.dialogsType == 0) {
            return MessagesController.getInstance(this.currentAccount).dialogs;
        }
        if (this.dialogsType == 1) {
            return MessagesController.getInstance(this.currentAccount).dialogsServerOnly;
        }
        if (this.dialogsType == 2) {
            return MessagesController.getInstance(this.currentAccount).dialogsGroupsOnly;
        }
        return this.dialogsType == 3 ? MessagesController.getInstance(this.currentAccount).dialogsForward : null;
    }

    public void checkCurrentDialogIndex() {
        if (this.index < getDialogsArray().size()) {
            TL_dialog tL_dialog = (TL_dialog) getDialogsArray().get(this.index);
            DraftMessage draft = DataQuery.getInstance(this.currentAccount).getDraft(this.currentDialogId);
            MessageObject messageObject = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessage.get(tL_dialog.id);
            if (this.currentDialogId != tL_dialog.id || ((this.message != null && this.message.getId() != tL_dialog.top_message) || ((messageObject != null && messageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != tL_dialog.unread_count || this.mentionCount != tL_dialog.unread_mentions_count || this.message != messageObject || ((this.message == null && messageObject != null) || draft != this.draftMessage || this.drawPin != tL_dialog.pinned)))) {
                this.currentDialogId = tL_dialog.id;
                update(0);
            }
        }
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox != null) {
            this.checkBox.setChecked(z, z2);
        }
    }

    public void update(int i) {
        TLObject tLObject = null;
        boolean z = false;
        boolean z2 = true;
        if (this.customDialog != null) {
            this.lastMessageDate = this.customDialog.date;
            if (this.customDialog.unread_count == 0) {
                z2 = false;
            }
            this.lastUnreadState = z2;
            this.unreadCount = this.customDialog.unread_count;
            this.drawPin = this.customDialog.pinned;
            this.dialogMuted = this.customDialog.muted;
            this.avatarDrawable.setInfo(this.customDialog.id, this.customDialog.name, null, false);
            this.avatarImage.setImage(null, "50_50", this.avatarDrawable, null, 0);
        } else {
            if (this.isDialogCell) {
                TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                if (tL_dialog != null && i == 0) {
                    this.message = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessage.get(tL_dialog.id);
                    boolean z3 = this.message != null && this.message.isUnread();
                    this.lastUnreadState = z3;
                    this.unreadCount = tL_dialog.unread_count;
                    this.mentionCount = tL_dialog.unread_mentions_count;
                    this.currentEditDate = this.message != null ? this.message.messageOwner.edit_date : 0;
                    this.lastMessageDate = tL_dialog.last_message_date;
                    this.drawPin = tL_dialog.pinned;
                    if (this.message != null) {
                        this.lastSendState = this.message.messageOwner.send_state;
                    }
                }
            } else {
                this.drawPin = false;
            }
            if (i != 0) {
                boolean z4;
                TL_dialog tL_dialog2;
                if (this.isDialogCell && (i & 64) != 0) {
                    CharSequence charSequence = (CharSequence) MessagesController.getInstance(this.currentAccount).printingStrings.get(this.currentDialogId);
                    if ((this.lastPrintString != null && charSequence == null) || ((this.lastPrintString == null && charSequence != null) || !(this.lastPrintString == null || charSequence == null || this.lastPrintString.equals(charSequence)))) {
                        z4 = true;
                        if (!(z4 || (32768 & i) == 0 || this.message == null || this.message.messageText == this.lastMessageString)) {
                            z4 = true;
                        }
                        if (!(z4 || (i & 2) == 0 || this.chat != null)) {
                            z4 = true;
                        }
                        if (!(z4 || (i & 1) == 0 || this.chat != null)) {
                            z4 = true;
                        }
                        if (!(z4 || (i & 8) == 0 || this.user != null)) {
                            z4 = true;
                        }
                        if (!(z4 || (i & 16) == 0 || this.user != null)) {
                            z4 = true;
                        }
                        if (!(z4 || (i & 256) == 0)) {
                            if (this.message == null && this.lastUnreadState != this.message.isUnread()) {
                                this.lastUnreadState = this.message.isUnread();
                            } else if (this.isDialogCell) {
                                tL_dialog2 = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                                if (!(tL_dialog2 == null || (this.unreadCount == tL_dialog2.unread_count && this.mentionCount == tL_dialog2.unread_mentions_count))) {
                                    this.unreadCount = tL_dialog2.unread_count;
                                    this.mentionCount = tL_dialog2.unread_mentions_count;
                                }
                            }
                            z4 = true;
                        }
                        if (!(z4 || (i & 4096) == 0 || this.message == 0 || this.lastSendState == this.message.messageOwner.send_state)) {
                            this.lastSendState = this.message.messageOwner.send_state;
                            z4 = true;
                        }
                        if (!z4) {
                            return;
                        }
                    }
                }
                z4 = false;
                z4 = true;
                z4 = true;
                z4 = true;
                z4 = true;
                z4 = true;
                if (this.message == null) {
                }
                if (this.isDialogCell) {
                    tL_dialog2 = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                    this.unreadCount = tL_dialog2.unread_count;
                    this.mentionCount = tL_dialog2.unread_mentions_count;
                    z4 = true;
                }
                this.lastSendState = this.message.messageOwner.send_state;
                z4 = true;
                if (z4) {
                    return;
                }
            }
            if (!(this.isDialogCell == 0 || MessagesController.getInstance(this.currentAccount).isDialogMuted(this.currentDialogId) == 0)) {
                z = true;
            }
            this.dialogMuted = z;
            this.user = null;
            this.chat = null;
            this.encryptedChat = null;
            i = (int) this.currentDialogId;
            int i2 = (int) (this.currentDialogId >> 32);
            if (i == 0) {
                this.encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i2));
                if (this.encryptedChat != 0) {
                    this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.encryptedChat.user_id));
                }
            } else if (i2 == 1) {
                this.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
            } else if (i < 0) {
                this.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
                if (!(this.isDialogCell != 0 || this.chat == 0 || this.chat.migrated_to == 0)) {
                    i = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat.migrated_to.channel_id));
                    if (i != 0) {
                        this.chat = i;
                    }
                }
            } else {
                this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            }
            if (this.user != 0) {
                this.avatarDrawable.setInfo(this.user);
                if (UserObject.isUserSelf(this.user) != 0) {
                    this.avatarDrawable.setSavedMessages(1);
                } else if (this.user.photo != 0) {
                    tLObject = this.user.photo.photo_small;
                }
            } else if (this.chat != 0) {
                if (this.chat.photo != 0) {
                    tLObject = this.chat.photo.photo_small;
                }
                this.avatarDrawable.setInfo(this.chat);
            }
            this.avatarImage.setImage(tLObject, "50_50", this.avatarDrawable, null, 0);
        }
        if (getMeasuredWidth() == 0) {
            if (getMeasuredHeight() == 0) {
                requestLayout();
                invalidate();
            }
        }
        buildLayout();
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentDialogId != 0 || this.customDialog != null) {
            if (this.isSelected) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
            }
            if (this.drawPin || this.drawPinBackground) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dialogs_pinnedPaint);
            }
            if (this.drawNameLock) {
                BaseCell.setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_lockDrawable.draw(canvas);
            } else if (this.drawNameGroup) {
                BaseCell.setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_groupDrawable.draw(canvas);
            } else if (this.drawNameBroadcast) {
                BaseCell.setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_broadcastDrawable.draw(canvas);
            } else if (this.drawNameBot) {
                BaseCell.setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_botDrawable.draw(canvas);
            }
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, (float) AndroidUtilities.dp(13.0f));
                this.nameLayout.draw(canvas);
                canvas.restore();
            }
            if (this.timeLayout != null) {
                canvas.save();
                canvas.translate((float) this.timeLeft, (float) this.timeTop);
                this.timeLayout.draw(canvas);
                canvas.restore();
            }
            if (this.messageLayout != null) {
                canvas.save();
                canvas.translate((float) this.messageLeft, (float) this.messageTop);
                try {
                    this.messageLayout.draw(canvas);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                canvas.restore();
            }
            if (this.drawClock) {
                BaseCell.setDrawableBounds(Theme.dialogs_clockDrawable, this.checkDrawLeft, this.checkDrawTop);
                Theme.dialogs_clockDrawable.draw(canvas);
            } else if (this.drawCheck2) {
                if (this.drawCheck1) {
                    BaseCell.setDrawableBounds(Theme.dialogs_halfCheckDrawable, this.halfCheckDrawLeft, this.checkDrawTop);
                    Theme.dialogs_halfCheckDrawable.draw(canvas);
                    BaseCell.setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft, this.checkDrawTop);
                    Theme.dialogs_checkDrawable.draw(canvas);
                } else {
                    BaseCell.setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft, this.checkDrawTop);
                    Theme.dialogs_checkDrawable.draw(canvas);
                }
            }
            if (this.dialogMuted && !this.drawVerified) {
                BaseCell.setDrawableBounds(Theme.dialogs_muteDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
                Theme.dialogs_muteDrawable.draw(canvas);
            } else if (this.drawVerified) {
                BaseCell.setDrawableBounds(Theme.dialogs_verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
                BaseCell.setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
                Theme.dialogs_verifiedDrawable.draw(canvas);
                Theme.dialogs_verifiedCheckDrawable.draw(canvas);
            }
            if (this.drawError) {
                this.rect.set((float) this.errorLeft, (float) this.errorTop, (float) (this.errorLeft + AndroidUtilities.dp(23.0f)), (float) (this.errorTop + AndroidUtilities.dp(23.0f)));
                canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, 11.5f * AndroidUtilities.density, Theme.dialogs_errorPaint);
                BaseCell.setDrawableBounds(Theme.dialogs_errorDrawable, this.errorLeft + AndroidUtilities.dp(5.5f), this.errorTop + AndroidUtilities.dp(5.0f));
                Theme.dialogs_errorDrawable.draw(canvas);
            } else {
                int dp;
                if (!this.drawCount) {
                    if (!this.drawMention) {
                        if (this.drawPin) {
                            BaseCell.setDrawableBounds(Theme.dialogs_pinnedDrawable, this.pinLeft, this.pinTop);
                            Theme.dialogs_pinnedDrawable.draw(canvas);
                        }
                    }
                }
                if (this.drawCount) {
                    dp = this.countLeft - AndroidUtilities.dp(5.5f);
                    this.rect.set((float) dp, (float) this.countTop, (float) ((dp + this.countWidth) + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, this.dialogMuted ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                    canvas.save();
                    canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                    if (this.countLayout != null) {
                        this.countLayout.draw(canvas);
                    }
                    canvas.restore();
                }
                if (this.drawMention) {
                    dp = this.mentionLeft - AndroidUtilities.dp(5.5f);
                    this.rect.set((float) dp, (float) this.countTop, (float) ((dp + this.mentionWidth) + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, 11.5f * AndroidUtilities.density, Theme.dialogs_countPaint);
                    BaseCell.setDrawableBounds(Theme.dialogs_mentionDrawable, this.mentionLeft - AndroidUtilities.dp(2.0f), this.countTop + AndroidUtilities.dp(3.2f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                    Theme.dialogs_mentionDrawable.draw(canvas);
                }
            }
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            }
            this.avatarImage.draw(canvas);
        }
    }
}
