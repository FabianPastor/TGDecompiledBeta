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
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
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
    private Chat chat;
    private GroupCreateCheckBox checkBox;
    private int checkDrawLeft;
    private int checkDrawTop = AndroidUtilities.dp(18.0f);
    private boolean clearingDialog;
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
    private EncryptedChat encryptedChat;
    private int errorLeft;
    private int errorTop = AndroidUtilities.dp(39.0f);
    public boolean fullSeparator;
    private int halfCheckDrawLeft;
    private int index;
    private boolean isDialogCell;
    private boolean isSelected;
    private int lastMessageDate;
    private CharSequence lastMessageString;
    private CharSequence lastPrintString;
    private int lastSendState;
    private boolean lastUnreadState;
    private boolean markUnread;
    private int mentionCount;
    private int mentionLeft;
    private int mentionWidth;
    private MessageObject message;
    private int messageId;
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
    public boolean useSeparator;
    private User user;

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

    public DialogCell(Context context, boolean needCheck) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
        if (needCheck) {
            this.checkBox = new GroupCreateCheckBox(context);
            this.checkBox.setVisibility(0);
            addView(this.checkBox);
        }
    }

    public void setDialog(TL_dialog dialog, int i, int type) {
        this.currentDialogId = dialog.id;
        this.isDialogCell = true;
        this.index = i;
        this.dialogsType = type;
        this.messageId = 0;
        update(0);
    }

    public void setDialog(CustomDialog dialog) {
        this.customDialog = dialog;
        this.messageId = 0;
        update(0);
    }

    public void setDialog(long dialog_id, MessageObject messageObject, int date) {
        int id;
        boolean z;
        this.currentDialogId = dialog_id;
        this.message = messageObject;
        this.isDialogCell = false;
        this.lastMessageDate = date;
        this.currentEditDate = messageObject != null ? messageObject.messageOwner.edit_date : 0;
        this.unreadCount = 0;
        this.markUnread = false;
        if (messageObject != null) {
            id = messageObject.getId();
        } else {
            id = 0;
        }
        this.messageId = id;
        this.mentionCount = 0;
        if (messageObject == null || !messageObject.isUnread()) {
            z = false;
        } else {
            z = true;
        }
        this.lastUnreadState = z;
        if (this.message != null) {
            this.lastSendState = this.message.messageOwner.send_state;
        }
        update(0);
    }

    public long getDialogId() {
        return this.currentDialogId;
    }

    public int getMessageId() {
        return this.messageId;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.checkBox != null) {
            this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.useSeparator ? 1 : 0) + AndroidUtilities.dp(72.0f));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.currentDialogId != 0 || this.customDialog != null) {
            if (this.checkBox != null) {
                int x = LocaleController.isRTL ? (right - left) - AndroidUtilities.dp(42.0f) : AndroidUtilities.dp(42.0f);
                int y = AndroidUtilities.dp(43.0f);
                this.checkBox.layout(x, y, this.checkBox.getMeasuredWidth() + x, this.checkBox.getMeasuredHeight() + y);
            }
            if (changed) {
                try {
                    buildLayout();
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public boolean isUnread() {
        return (this.unreadCount != 0 || this.markUnread) && !this.dialogMuted;
    }

    public void buildLayout() {
        String messageFormat;
        String mess;
        int timeWidth;
        int nameWidth;
        int w;
        int avatarLeft;
        int messageWidth;
        String nameString = "";
        String timeString = "";
        String countString = null;
        String mentionString = null;
        CharSequence messageString = "";
        CharSequence printingString = null;
        if (this.isDialogCell) {
            printingString = (CharSequence) MessagesController.getInstance(this.currentAccount).printingStrings.get(this.currentDialogId);
        }
        TextPaint currentNamePaint = Theme.dialogs_namePaint;
        TextPaint currentMessagePaint = Theme.dialogs_messagePaint;
        boolean checkMessage = true;
        this.drawNameGroup = false;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameBot = false;
        this.drawVerified = false;
        this.drawPinBackground = false;
        boolean showChecks = !UserObject.isUserSelf(this.user);
        boolean drawTime = true;
        if (VERSION.SDK_INT >= 18) {
            messageFormat = "%s: ⁨%s⁩";
        } else {
            messageFormat = "%s: %s";
        }
        this.lastMessageString = this.message != null ? this.message.messageText : null;
        String name;
        SpannableStringBuilder stringBuilder;
        Object messageString2;
        if (this.customDialog != null) {
            if (this.customDialog.type == 2) {
                this.drawNameLock = true;
                this.nameLockTop = AndroidUtilities.dp(16.5f);
                if (LocaleController.isRTL) {
                    this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - Theme.dialogs_lockDrawable.getIntrinsicWidth();
                    this.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
                }
            } else {
                this.drawVerified = this.customDialog.verified;
                if (this.customDialog.type == 1) {
                    this.drawNameGroup = true;
                    this.nameLockTop = AndroidUtilities.dp(17.5f);
                    if (LocaleController.isRTL) {
                        this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (this.drawNameGroup ? Theme.dialogs_groupDrawable.getIntrinsicWidth() : Theme.dialogs_broadcastDrawable.getIntrinsicWidth());
                        this.nameLeft = AndroidUtilities.dp(14.0f);
                    } else {
                        this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        this.nameLeft = (this.drawNameGroup ? Theme.dialogs_groupDrawable.getIntrinsicWidth() : Theme.dialogs_broadcastDrawable.getIntrinsicWidth()) + AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4));
                    }
                } else if (LocaleController.isRTL) {
                    this.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                }
            }
            if (this.customDialog.type == 1) {
                name = LocaleController.getString("FromYou", R.string.FromYou);
                checkMessage = false;
                if (this.customDialog.isMedia) {
                    currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                    stringBuilder = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{name, this.message.messageText}));
                    stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("chats_attachMessage")), name.length() + 2, stringBuilder.length(), 33);
                } else {
                    mess = this.customDialog.message;
                    if (mess.length() > 150) {
                        mess = mess.substring(0, 150);
                    }
                    stringBuilder = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{name, mess.replace(10, ' ')}));
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("chats_nameMessage")), 0, name.length() + 1, 33);
                }
                messageString = Emoji.replaceEmoji(stringBuilder, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            } else {
                messageString2 = this.customDialog.message;
                if (this.customDialog.isMedia) {
                    currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                }
            }
            timeString = LocaleController.stringForMessageListDate((long) this.customDialog.date);
            if (this.customDialog.unread_count != 0) {
                this.drawCount = true;
                countString = String.format("%d", new Object[]{Integer.valueOf(this.customDialog.unread_count)});
            } else {
                this.drawCount = false;
            }
            if (this.customDialog.sent) {
                this.drawCheck1 = true;
                this.drawCheck2 = true;
                this.drawClock = false;
                this.drawError = false;
            } else {
                this.drawCheck1 = false;
                this.drawCheck2 = false;
                this.drawClock = false;
                this.drawError = false;
            }
            nameString = this.customDialog.name;
            if (this.customDialog.type == 2) {
                currentNamePaint = Theme.dialogs_nameEncryptedPaint;
            }
        } else {
            if (this.encryptedChat != null) {
                this.drawNameLock = true;
                this.nameLockTop = AndroidUtilities.dp(16.5f);
                if (LocaleController.isRTL) {
                    this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - Theme.dialogs_lockDrawable.getIntrinsicWidth();
                    this.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
                }
            } else if (this.chat != null) {
                if (this.chat.id < 0 || (ChatObject.isChannel(this.chat) && !this.chat.megagroup)) {
                    this.drawNameBroadcast = true;
                    this.nameLockTop = AndroidUtilities.dp(16.5f);
                } else {
                    this.drawNameGroup = true;
                    this.nameLockTop = AndroidUtilities.dp(17.5f);
                }
                this.drawVerified = this.chat.verified;
                if (LocaleController.isRTL) {
                    this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (this.drawNameGroup ? Theme.dialogs_groupDrawable.getIntrinsicWidth() : Theme.dialogs_broadcastDrawable.getIntrinsicWidth());
                    this.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    int intrinsicWidth;
                    this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    int dp = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4));
                    if (this.drawNameGroup) {
                        intrinsicWidth = Theme.dialogs_groupDrawable.getIntrinsicWidth();
                    } else {
                        intrinsicWidth = Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
                    }
                    this.nameLeft = intrinsicWidth + dp;
                }
            } else {
                if (LocaleController.isRTL) {
                    this.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                }
                if (this.user != null) {
                    if (this.user.bot) {
                        this.drawNameBot = true;
                        this.nameLockTop = AndroidUtilities.dp(16.5f);
                        if (LocaleController.isRTL) {
                            this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - Theme.dialogs_botDrawable.getIntrinsicWidth();
                            this.nameLeft = AndroidUtilities.dp(14.0f);
                        } else {
                            this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                            this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_botDrawable.getIntrinsicWidth();
                        }
                    }
                    this.drawVerified = this.user.verified;
                }
            }
            int lastDate = this.lastMessageDate;
            if (this.lastMessageDate == 0 && this.message != null) {
                lastDate = this.message.messageOwner.date;
            }
            if (this.isDialogCell) {
                this.draftMessage = DataQuery.getInstance(this.currentAccount).getDraft(this.currentDialogId);
                if ((this.draftMessage != null && ((TextUtils.isEmpty(this.draftMessage.message) && this.draftMessage.reply_to_msg_id == 0) || (lastDate > this.draftMessage.date && this.unreadCount != 0))) || ((ChatObject.isChannel(this.chat) && !this.chat.megagroup && !this.chat.creator && (this.chat.admin_rights == null || !this.chat.admin_rights.post_messages)) || (this.chat != null && (this.chat.left || this.chat.kicked)))) {
                    this.draftMessage = null;
                }
            } else {
                this.draftMessage = null;
            }
            if (printingString != null) {
                messageString = printingString;
                this.lastPrintString = printingString;
                currentMessagePaint = Theme.dialogs_messagePrintingPaint;
            } else {
                this.lastPrintString = null;
                if (this.draftMessage != null) {
                    checkMessage = false;
                    String draftString;
                    if (TextUtils.isEmpty(this.draftMessage.message)) {
                        draftString = LocaleController.getString("Draft", R.string.Draft);
                        stringBuilder = SpannableStringBuilder.valueOf(draftString);
                        stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("chats_draft")), 0, draftString.length(), 33);
                        messageString2 = stringBuilder;
                    } else {
                        mess = this.draftMessage.message;
                        if (mess.length() > 150) {
                            mess = mess.substring(0, 150);
                        }
                        stringBuilder = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{LocaleController.getString("Draft", R.string.Draft), mess.replace(10, ' ')}));
                        stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("chats_draft")), 0, draftString.length() + 1, 33);
                        messageString = Emoji.replaceEmoji(stringBuilder, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    }
                } else if (this.clearingDialog) {
                    currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                    messageString = LocaleController.getString("HistoryCleared", R.string.HistoryCleared);
                } else if (this.message != null) {
                    User fromUser = null;
                    Chat fromChat = null;
                    if (this.message.isFromUser()) {
                        fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.message.messageOwner.from_id));
                    } else {
                        fromChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.message.messageOwner.to_id.channel_id));
                    }
                    if (this.dialogsType == 3 && UserObject.isUserSelf(this.user)) {
                        messageString = LocaleController.getString("SavedMessagesInfo", R.string.SavedMessagesInfo);
                        showChecks = false;
                        drawTime = false;
                    } else if (this.message.messageOwner instanceof TL_messageService) {
                        if (ChatObject.isChannel(this.chat) && ((this.message.messageOwner.action instanceof TL_messageActionHistoryClear) || (this.message.messageOwner.action instanceof TL_messageActionChannelMigrateFrom))) {
                            messageString = "";
                            showChecks = false;
                        } else {
                            messageString = this.message.messageText;
                        }
                        currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                    } else if (this.chat != null && this.chat.id > 0 && fromChat == null) {
                        if (this.message.isOutOwner()) {
                            name = LocaleController.getString("FromYou", R.string.FromYou);
                        } else if (fromUser != null) {
                            name = UserObject.getFirstName(fromUser).replace("\n", "");
                        } else if (fromChat != null) {
                            name = fromChat.title.replace("\n", "");
                        } else {
                            name = "DELETED";
                        }
                        checkMessage = false;
                        if (this.message.caption != null) {
                            mess = this.message.caption.toString();
                            if (mess.length() > 150) {
                                mess = mess.substring(0, 150);
                            }
                            stringBuilder = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{name, mess.replace(10, ' ')}));
                        } else if (this.message.messageOwner.media != null && !this.message.isMediaEmpty()) {
                            currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                            if (this.message.messageOwner.media instanceof TL_messageMediaGame) {
                                if (VERSION.SDK_INT >= 18) {
                                    stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: 🎮 ⁨%s⁩", new Object[]{name, this.message.messageOwner.media.game.title}));
                                } else {
                                    stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: 🎮 %s", new Object[]{name, this.message.messageOwner.media.game.title}));
                                }
                            } else if (this.message.type != 14) {
                                stringBuilder = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{name, this.message.messageText}));
                            } else if (VERSION.SDK_INT >= 18) {
                                stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: 🎧 ⁨%s - %s⁩", new Object[]{name, this.message.getMusicAuthor(), this.message.getMusicTitle()}));
                            } else {
                                stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: 🎧 %s - %s", new Object[]{name, this.message.getMusicAuthor(), this.message.getMusicTitle()}));
                            }
                            stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("chats_attachMessage")), name.length() + 2, stringBuilder.length(), 33);
                        } else if (this.message.messageOwner.message != null) {
                            mess = this.message.messageOwner.message;
                            if (mess.length() > 150) {
                                mess = mess.substring(0, 150);
                            }
                            stringBuilder = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{name, mess.replace(10, ' ')}));
                        } else {
                            stringBuilder = SpannableStringBuilder.valueOf("");
                        }
                        if (stringBuilder.length() > 0) {
                            stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("chats_nameMessage")), 0, name.length() + 1, 33);
                        }
                        messageString = Emoji.replaceEmoji(stringBuilder, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    } else if ((this.message.messageOwner.media instanceof TL_messageMediaPhoto) && (this.message.messageOwner.media.photo instanceof TL_photoEmpty) && this.message.messageOwner.media.ttl_seconds != 0) {
                        messageString = LocaleController.getString("AttachPhotoExpired", R.string.AttachPhotoExpired);
                    } else if ((this.message.messageOwner.media instanceof TL_messageMediaDocument) && (this.message.messageOwner.media.document instanceof TL_documentEmpty) && this.message.messageOwner.media.ttl_seconds != 0) {
                        messageString = LocaleController.getString("AttachVideoExpired", R.string.AttachVideoExpired);
                    } else if (this.message.caption != null) {
                        messageString = this.message.caption;
                    } else {
                        if (this.message.messageOwner.media instanceof TL_messageMediaGame) {
                            messageString = "🎮 " + this.message.messageOwner.media.game.title;
                        } else if (this.message.type == 14) {
                            messageString = String.format("🎧 %s - %s", new Object[]{this.message.getMusicAuthor(), this.message.getMusicTitle()});
                        } else {
                            messageString = this.message.messageText;
                        }
                        if (!(this.message.messageOwner.media == null || this.message.isMediaEmpty())) {
                            currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                        }
                    }
                } else if (this.encryptedChat != null) {
                    currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                    if (this.encryptedChat instanceof TL_encryptedChatRequested) {
                        messageString = LocaleController.getString("EncryptionProcessing", R.string.EncryptionProcessing);
                    } else if (this.encryptedChat instanceof TL_encryptedChatWaiting) {
                        if (this.user == null || this.user.first_name == null) {
                            messageString = LocaleController.formatString("AwaitingEncryption", R.string.AwaitingEncryption, "");
                        } else {
                            messageString = LocaleController.formatString("AwaitingEncryption", R.string.AwaitingEncryption, this.user.first_name);
                        }
                    } else if (this.encryptedChat instanceof TL_encryptedChatDiscarded) {
                        messageString = LocaleController.getString("EncryptionRejected", R.string.EncryptionRejected);
                    } else if (this.encryptedChat instanceof TL_encryptedChat) {
                        if (this.encryptedChat.admin_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            messageString = LocaleController.getString("EncryptedChatStartedIncoming", R.string.EncryptedChatStartedIncoming);
                        } else if (this.user == null || this.user.first_name == null) {
                            messageString = LocaleController.formatString("EncryptedChatStartedOutgoing", R.string.EncryptedChatStartedOutgoing, "");
                        } else {
                            messageString = LocaleController.formatString("EncryptedChatStartedOutgoing", R.string.EncryptedChatStartedOutgoing, this.user.first_name);
                        }
                    }
                } else {
                    messageString = "";
                }
            }
            if (this.draftMessage != null) {
                timeString = LocaleController.stringForMessageListDate((long) this.draftMessage.date);
            } else if (this.lastMessageDate != 0) {
                timeString = LocaleController.stringForMessageListDate((long) this.lastMessageDate);
            } else if (this.message != null) {
                timeString = LocaleController.stringForMessageListDate((long) this.message.messageOwner.date);
            }
            if (this.message == null) {
                this.drawCheck1 = false;
                this.drawCheck2 = false;
                this.drawClock = false;
                this.drawCount = false;
                this.drawMention = false;
                this.drawError = false;
            } else {
                if (this.clearingDialog) {
                    this.drawCount = false;
                    showChecks = false;
                } else if (this.unreadCount != 0 && (this.unreadCount != 1 || this.unreadCount != this.mentionCount || this.message == null || !this.message.messageOwner.mentioned)) {
                    this.drawCount = true;
                    countString = String.format("%d", new Object[]{Integer.valueOf(this.unreadCount)});
                } else if (this.markUnread) {
                    this.drawCount = true;
                    countString = "";
                } else {
                    this.drawCount = false;
                }
                if (this.mentionCount != 0) {
                    this.drawMention = true;
                    mentionString = "@";
                } else {
                    this.drawMention = false;
                }
                if (!this.message.isOut() || this.draftMessage != null || !showChecks) {
                    this.drawCheck1 = false;
                    this.drawCheck2 = false;
                    this.drawClock = false;
                    this.drawError = false;
                } else if (this.message.isSending()) {
                    this.drawCheck1 = false;
                    this.drawCheck2 = false;
                    this.drawClock = true;
                    this.drawError = false;
                } else if (this.message.isSendError()) {
                    this.drawCheck1 = false;
                    this.drawCheck2 = false;
                    this.drawClock = false;
                    this.drawError = true;
                    this.drawCount = false;
                    this.drawMention = false;
                } else if (this.message.isSent()) {
                    boolean z = !this.message.isUnread() || (ChatObject.isChannel(this.chat) && !this.chat.megagroup);
                    this.drawCheck1 = z;
                    this.drawCheck2 = true;
                    this.drawClock = false;
                    this.drawError = false;
                }
            }
            if (this.dialogsType == 0 && MessagesController.getInstance(this.currentAccount).isProxyDialog(this.currentDialogId)) {
                this.drawPinBackground = true;
                timeString = LocaleController.getString("UseProxySponsor", R.string.UseProxySponsor);
            }
            if (this.chat != null) {
                nameString = this.chat.title;
            } else if (this.user != null) {
                if (UserObject.isUserSelf(this.user)) {
                    if (this.dialogsType == 3) {
                        this.drawPinBackground = true;
                    }
                    nameString = LocaleController.getString("SavedMessages", R.string.SavedMessages);
                } else if (this.user.id / 1000 == 777 || this.user.id / 1000 == 333 || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user.id)) != null) {
                    nameString = UserObject.getUserName(this.user);
                } else if (ContactsController.getInstance(this.currentAccount).contactsDict.size() == 0 && (!ContactsController.getInstance(this.currentAccount).contactsLoaded || ContactsController.getInstance(this.currentAccount).isLoadingContacts())) {
                    nameString = UserObject.getUserName(this.user);
                } else if (this.user.phone == null || this.user.phone.length() == 0) {
                    nameString = UserObject.getUserName(this.user);
                } else {
                    nameString = PhoneFormat.getInstance().format("+" + this.user.phone);
                }
                if (this.encryptedChat != null) {
                    currentNamePaint = Theme.dialogs_nameEncryptedPaint;
                }
            }
            if (nameString.length() == 0) {
                nameString = LocaleController.getString("HiddenName", R.string.HiddenName);
            }
        }
        if (drawTime) {
            timeWidth = (int) Math.ceil((double) Theme.dialogs_timePaint.measureText(timeString));
            this.timeLayout = new StaticLayout(timeString, Theme.dialogs_timePaint, timeWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (LocaleController.isRTL) {
                this.timeLeft = AndroidUtilities.dp(15.0f);
            } else {
                this.timeLeft = (getMeasuredWidth() - AndroidUtilities.dp(15.0f)) - timeWidth;
            }
        } else {
            timeWidth = 0;
            this.timeLayout = null;
            this.timeLeft = 0;
        }
        if (LocaleController.isRTL) {
            nameWidth = ((getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - timeWidth;
            this.nameLeft += timeWidth;
        } else {
            nameWidth = ((getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp(14.0f)) - timeWidth;
        }
        if (this.drawNameLock) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        } else if (this.drawNameGroup) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
        } else if (this.drawNameBroadcast) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
        } else if (this.drawNameBot) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
        }
        if (this.drawClock) {
            w = Theme.dialogs_clockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            nameWidth -= w;
            if (LocaleController.isRTL) {
                this.checkDrawLeft = (this.timeLeft + timeWidth) + AndroidUtilities.dp(5.0f);
                this.nameLeft += w;
            } else {
                this.checkDrawLeft = this.timeLeft - w;
            }
        } else if (this.drawCheck2) {
            w = Theme.dialogs_checkDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            nameWidth -= w;
            if (this.drawCheck1) {
                nameWidth -= Theme.dialogs_halfCheckDrawable.getIntrinsicWidth() - AndroidUtilities.dp(8.0f);
                if (LocaleController.isRTL) {
                    this.checkDrawLeft = (this.timeLeft + timeWidth) + AndroidUtilities.dp(5.0f);
                    this.halfCheckDrawLeft = this.checkDrawLeft + AndroidUtilities.dp(5.5f);
                    this.nameLeft += (Theme.dialogs_halfCheckDrawable.getIntrinsicWidth() + w) - AndroidUtilities.dp(8.0f);
                } else {
                    this.halfCheckDrawLeft = this.timeLeft - w;
                    this.checkDrawLeft = this.halfCheckDrawLeft - AndroidUtilities.dp(5.5f);
                }
            } else if (LocaleController.isRTL) {
                this.checkDrawLeft = (this.timeLeft + timeWidth) + AndroidUtilities.dp(5.0f);
                this.nameLeft += w;
            } else {
                this.checkDrawLeft = this.timeLeft - w;
            }
        }
        if (this.dialogMuted && !this.drawVerified) {
            w = AndroidUtilities.dp(6.0f) + Theme.dialogs_muteDrawable.getIntrinsicWidth();
            nameWidth -= w;
            if (LocaleController.isRTL) {
                this.nameLeft += w;
            }
        } else if (this.drawVerified) {
            w = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
            nameWidth -= w;
            if (LocaleController.isRTL) {
                this.nameLeft += w;
            }
        }
        nameWidth = Math.max(AndroidUtilities.dp(12.0f), nameWidth);
        try {
            this.nameLayout = new StaticLayout(TextUtils.ellipsize(nameString.replace(10, ' '), currentNamePaint, (float) (nameWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentNamePaint, nameWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        int messageWidth2 = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
        if (LocaleController.isRTL) {
            this.messageLeft = AndroidUtilities.dp(16.0f);
            avatarLeft = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        } else {
            this.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            avatarLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
        }
        this.avatarImage.setImageCoords(avatarLeft, this.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        if (this.drawError) {
            w = AndroidUtilities.dp(31.0f);
            messageWidth = messageWidth2 - w;
            if (LocaleController.isRTL) {
                this.errorLeft = AndroidUtilities.dp(11.0f);
                this.messageLeft += w;
            } else {
                this.errorLeft = getMeasuredWidth() - AndroidUtilities.dp(34.0f);
            }
        } else if (countString == null && mentionString == null) {
            if (this.drawPin) {
                w = Theme.dialogs_pinnedDrawable.getIntrinsicWidth() + AndroidUtilities.dp(8.0f);
                messageWidth = messageWidth2 - w;
                if (LocaleController.isRTL) {
                    this.pinLeft = AndroidUtilities.dp(14.0f);
                    this.messageLeft += w;
                } else {
                    this.pinLeft = (getMeasuredWidth() - Theme.dialogs_pinnedDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(14.0f);
                }
            } else {
                messageWidth = messageWidth2;
            }
            this.drawCount = false;
            this.drawMention = false;
        } else {
            if (countString != null) {
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(countString)));
                this.countLayout = new StaticLayout(countString, Theme.dialogs_countTextPaint, this.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                w = this.countWidth + AndroidUtilities.dp(18.0f);
                messageWidth = messageWidth2 - w;
                if (LocaleController.isRTL) {
                    this.countLeft = AndroidUtilities.dp(19.0f);
                    this.messageLeft += w;
                } else {
                    this.countLeft = (getMeasuredWidth() - this.countWidth) - AndroidUtilities.dp(19.0f);
                }
                this.drawCount = true;
            } else {
                this.countWidth = 0;
                messageWidth = messageWidth2;
            }
            if (mentionString != null) {
                this.mentionWidth = AndroidUtilities.dp(12.0f);
                w = this.mentionWidth + AndroidUtilities.dp(18.0f);
                messageWidth -= w;
                if (LocaleController.isRTL) {
                    this.mentionLeft = (this.countWidth != 0 ? this.countWidth + AndroidUtilities.dp(18.0f) : 0) + AndroidUtilities.dp(19.0f);
                    this.messageLeft += w;
                } else {
                    this.mentionLeft = ((getMeasuredWidth() - this.mentionWidth) - AndroidUtilities.dp(19.0f)) - (this.countWidth != 0 ? this.countWidth + AndroidUtilities.dp(18.0f) : 0);
                }
                this.drawMention = true;
            }
        }
        if (checkMessage) {
            if (messageString == null) {
                messageString = "";
            }
            mess = messageString.toString();
            if (mess.length() > 150) {
                mess = mess.substring(0, 150);
            }
            messageString = Emoji.replaceEmoji(mess.replace(10, ' '), Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
        }
        messageWidth = Math.max(AndroidUtilities.dp(12.0f), messageWidth);
        try {
            this.messageLayout = new StaticLayout(TextUtils.ellipsize(messageString, currentMessagePaint, (float) (messageWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentMessagePaint, messageWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        float left;
        double widthpx;
        if (LocaleController.isRTL) {
            if (this.nameLayout != null && this.nameLayout.getLineCount() > 0) {
                left = this.nameLayout.getLineLeft(0);
                widthpx = Math.ceil((double) this.nameLayout.getLineWidth(0));
                if (this.dialogMuted && !this.drawVerified) {
                    this.nameMuteLeft = (int) (((((double) this.nameLeft) + (((double) nameWidth) - widthpx)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_muteDrawable.getIntrinsicWidth()));
                } else if (this.drawVerified) {
                    this.nameMuteLeft = (int) (((((double) this.nameLeft) + (((double) nameWidth) - widthpx)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                }
                if (left == 0.0f && widthpx < ((double) nameWidth)) {
                    this.nameLeft = (int) (((double) this.nameLeft) + (((double) nameWidth) - widthpx));
                }
            }
            if (this.messageLayout != null && this.messageLayout.getLineCount() > 0 && this.messageLayout.getLineLeft(0) == 0.0f) {
                widthpx = Math.ceil((double) this.messageLayout.getLineWidth(0));
                if (widthpx < ((double) messageWidth)) {
                    this.messageLeft = (int) (((double) this.messageLeft) + (((double) messageWidth) - widthpx));
                    return;
                }
                return;
            }
            return;
        }
        if (this.nameLayout != null && this.nameLayout.getLineCount() > 0) {
            left = this.nameLayout.getLineRight(0);
            if (left == ((float) nameWidth)) {
                widthpx = Math.ceil((double) this.nameLayout.getLineWidth(0));
                if (widthpx < ((double) nameWidth)) {
                    this.nameLeft = (int) (((double) this.nameLeft) - (((double) nameWidth) - widthpx));
                }
            }
            if (this.dialogMuted || this.drawVerified) {
                this.nameMuteLeft = (int) ((((float) this.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
            }
        }
        if (this.messageLayout != null && this.messageLayout.getLineCount() > 0 && this.messageLayout.getLineRight(0) == ((float) messageWidth)) {
            widthpx = Math.ceil((double) this.messageLayout.getLineWidth(0));
            if (widthpx < ((double) messageWidth)) {
                this.messageLeft = (int) (((double) this.messageLeft) - (((double) messageWidth) - widthpx));
            }
        }
    }

    public boolean isPointInsideAvatar(float x, float y) {
        if (LocaleController.isRTL) {
            if (x < ((float) (getMeasuredWidth() - AndroidUtilities.dp(60.0f))) || x >= ((float) getMeasuredWidth())) {
                return false;
            }
            return true;
        } else if (x < 0.0f || x >= ((float) AndroidUtilities.dp(60.0f))) {
            return false;
        } else {
            return true;
        }
    }

    public void setDialogSelected(boolean value) {
        if (this.isSelected != value) {
            invalidate();
        }
        this.isSelected = value;
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
        if (this.dialogsType == 3) {
            return MessagesController.getInstance(this.currentAccount).dialogsForward;
        }
        return null;
    }

    public void checkCurrentDialogIndex() {
        if (this.index < getDialogsArray().size()) {
            ArrayList<TL_dialog> dialogsArray = getDialogsArray();
            TL_dialog dialog = (TL_dialog) dialogsArray.get(this.index);
            TL_dialog nextDialog = this.index + 1 < dialogsArray.size() ? (TL_dialog) dialogsArray.get(this.index + 1) : null;
            DraftMessage newDraftMessage = DataQuery.getInstance(this.currentAccount).getDraft(this.currentDialogId);
            MessageObject newMessageObject = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
            if (this.currentDialogId != dialog.id || ((this.message != null && this.message.getId() != dialog.top_message) || ((newMessageObject != null && newMessageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != dialog.unread_count || this.mentionCount != dialog.unread_mentions_count || this.markUnread != dialog.unread_mark || this.message != newMessageObject || ((this.message == null && newMessageObject != null) || newDraftMessage != this.draftMessage || this.drawPin != dialog.pinned)))) {
                boolean z;
                this.currentDialogId = dialog.id;
                if (!dialog.pinned || nextDialog == null || nextDialog.pinned) {
                    z = false;
                } else {
                    z = true;
                }
                this.fullSeparator = z;
                update(0);
            }
        }
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox != null) {
            this.checkBox.setChecked(checked, animated);
        }
    }

    public void update(int mask) {
        if (this.customDialog != null) {
            this.lastMessageDate = this.customDialog.date;
            this.lastUnreadState = this.customDialog.unread_count != 0;
            this.unreadCount = this.customDialog.unread_count;
            this.drawPin = this.customDialog.pinned;
            this.dialogMuted = this.customDialog.muted;
            this.avatarDrawable.setInfo(this.customDialog.id, this.customDialog.name, null, false);
            this.avatarImage.setImage(null, "50_50", this.avatarDrawable, null, 0);
        } else {
            TL_dialog dialog;
            boolean z;
            if (this.isDialogCell) {
                dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                if (dialog != null && mask == 0) {
                    this.clearingDialog = MessagesController.getInstance(this.currentAccount).isClearingDialog(dialog.id);
                    this.message = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                    z = this.message != null && this.message.isUnread();
                    this.lastUnreadState = z;
                    this.unreadCount = dialog.unread_count;
                    this.markUnread = dialog.unread_mark;
                    this.mentionCount = dialog.unread_mentions_count;
                    this.currentEditDate = this.message != null ? this.message.messageOwner.edit_date : 0;
                    this.lastMessageDate = dialog.last_message_date;
                    this.drawPin = dialog.pinned;
                    if (this.message != null) {
                        this.lastSendState = this.message.messageOwner.send_state;
                    }
                }
            } else {
                this.drawPin = false;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if (this.isDialogCell && (mask & 64) != 0) {
                    CharSequence printString = (CharSequence) MessagesController.getInstance(this.currentAccount).printingStrings.get(this.currentDialogId);
                    if ((this.lastPrintString != null && printString == null) || ((this.lastPrintString == null && printString != null) || !(this.lastPrintString == null || printString == null || this.lastPrintString.equals(printString)))) {
                        continueUpdate = true;
                    }
                }
                if (!(continueUpdate || (32768 & mask) == 0 || this.message == null || this.message.messageText == this.lastMessageString)) {
                    continueUpdate = true;
                }
                if (!(continueUpdate || (mask & 2) == 0 || this.chat != null)) {
                    continueUpdate = true;
                }
                if (!(continueUpdate || (mask & 1) == 0 || this.chat != null)) {
                    continueUpdate = true;
                }
                if (!(continueUpdate || (mask & 8) == 0 || this.user != null)) {
                    continueUpdate = true;
                }
                if (!(continueUpdate || (mask & 16) == 0 || this.user != null)) {
                    continueUpdate = true;
                }
                if (!(continueUpdate || (mask & 256) == 0)) {
                    if (this.message != null && this.lastUnreadState != this.message.isUnread()) {
                        this.lastUnreadState = this.message.isUnread();
                        continueUpdate = true;
                    } else if (this.isDialogCell) {
                        dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                        if (!(dialog == null || (this.unreadCount == dialog.unread_count && this.markUnread == dialog.unread_mark && this.mentionCount == dialog.unread_mentions_count))) {
                            this.unreadCount = dialog.unread_count;
                            this.mentionCount = dialog.unread_mentions_count;
                            this.markUnread = dialog.unread_mark;
                            continueUpdate = true;
                        }
                    }
                }
                if (!(continueUpdate || (mask & 4096) == 0 || this.message == null || this.lastSendState == this.message.messageOwner.send_state)) {
                    this.lastSendState = this.message.messageOwner.send_state;
                    continueUpdate = true;
                }
                if (!continueUpdate) {
                    invalidate();
                    return;
                }
            }
            z = this.isDialogCell && MessagesController.getInstance(this.currentAccount).isDialogMuted(this.currentDialogId);
            this.dialogMuted = z;
            this.user = null;
            this.chat = null;
            this.encryptedChat = null;
            int lower_id = (int) this.currentDialogId;
            int high_id = (int) (this.currentDialogId >> 32);
            if (lower_id == 0) {
                this.encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (this.encryptedChat != null) {
                    this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.encryptedChat.user_id));
                }
            } else if (high_id == 1) {
                this.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(lower_id));
            } else if (lower_id < 0) {
                this.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                if (!(this.isDialogCell || this.chat == null || this.chat.migrated_to == null)) {
                    Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat.migrated_to.channel_id));
                    if (chat2 != null) {
                        this.chat = chat2;
                    }
                }
            } else {
                this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            }
            FileLocation photo = null;
            Object parentObject = null;
            if (this.user != null) {
                this.avatarDrawable.setInfo(this.user);
                if (UserObject.isUserSelf(this.user)) {
                    this.avatarDrawable.setSavedMessages(1);
                } else if (this.user.photo != null) {
                    photo = this.user.photo.photo_small;
                }
                parentObject = this.user;
            } else if (this.chat != null) {
                if (this.chat.photo != null) {
                    photo = this.chat.photo.photo_small;
                }
                this.avatarDrawable.setInfo(this.chat);
                parentObject = this.chat;
            }
            this.avatarImage.setImage(photo, "50_50", this.avatarDrawable, null, parentObject, 0);
        }
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
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
                    FileLog.e(e);
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
                canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.dialogs_errorPaint);
                BaseCell.setDrawableBounds(Theme.dialogs_errorDrawable, this.errorLeft + AndroidUtilities.dp(5.5f), this.errorTop + AndroidUtilities.dp(5.0f));
                Theme.dialogs_errorDrawable.draw(canvas);
            } else if (this.drawCount || this.drawMention) {
                int x;
                if (this.drawCount) {
                    x = this.countLeft - AndroidUtilities.dp(5.5f);
                    this.rect.set((float) x, (float) this.countTop, (float) ((this.countWidth + x) + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                    canvas.drawRoundRect(this.rect, 11.5f * AndroidUtilities.density, 11.5f * AndroidUtilities.density, this.dialogMuted ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                    canvas.save();
                    canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                    if (this.countLayout != null) {
                        this.countLayout.draw(canvas);
                    }
                    canvas.restore();
                }
                if (this.drawMention) {
                    x = this.mentionLeft - AndroidUtilities.dp(5.5f);
                    this.rect.set((float) x, (float) this.countTop, (float) ((this.mentionWidth + x) + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.dialogs_countPaint);
                    BaseCell.setDrawableBounds(Theme.dialogs_mentionDrawable, this.mentionLeft - AndroidUtilities.dp(2.0f), this.countTop + AndroidUtilities.dp(3.2f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                    Theme.dialogs_mentionDrawable.draw(canvas);
                }
            } else if (this.drawPin) {
                BaseCell.setDrawableBounds(Theme.dialogs_pinnedDrawable, this.pinLeft, this.pinTop);
                Theme.dialogs_pinnedDrawable.draw(canvas);
            }
            if (this.useSeparator) {
                int left;
                if (this.fullSeparator) {
                    left = 0;
                } else {
                    left = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                }
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - left), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                } else {
                    canvas.drawLine((float) left, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            }
            this.avatarImage.draw(canvas);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
