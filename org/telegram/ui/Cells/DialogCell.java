package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;

public class DialogCell extends BaseCell {
    private static Paint backPaint;
    private static Drawable botDrawable;
    private static Drawable broadcastDrawable;
    private static Drawable checkDrawable;
    private static Drawable clockDrawable;
    private static Drawable countDrawable;
    private static Drawable countDrawableGrey;
    private static TextPaint countPaint;
    private static Drawable errorDrawable;
    private static Drawable groupDrawable;
    private static Drawable halfCheckDrawable;
    private static Paint linePaint;
    private static Drawable lockDrawable;
    private static TextPaint messagePaint;
    private static TextPaint messagePrintingPaint;
    private static Drawable muteDrawable;
    private static TextPaint nameEncryptedPaint;
    private static TextPaint namePaint;
    private static Drawable pinnedDrawable;
    private static Paint pinnedPaint;
    private static TextPaint timePaint;
    private static Drawable verifiedDrawable;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private int avatarTop = AndroidUtilities.dp(10.0f);
    private Chat chat = null;
    private int checkDrawLeft;
    private int checkDrawTop = AndroidUtilities.dp(18.0f);
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop = AndroidUtilities.dp(39.0f);
    private int countWidth;
    private long currentDialogId;
    private int currentEditDate;
    private boolean dialogMuted;
    private int dialogsType;
    private DraftMessage draftMessage;
    private boolean drawCheck1;
    private boolean drawCheck2;
    private boolean drawClock;
    private boolean drawCount;
    private boolean drawError;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawPin;
    private boolean drawVerified;
    private EncryptedChat encryptedChat = null;
    private int errorLeft;
    private int errorTop = AndroidUtilities.dp(39.0f);
    private int halfCheckDrawLeft;
    private int index;
    private boolean isDialogCell;
    private boolean isSelected;
    private int lastMessageDate;
    private CharSequence lastPrintString = null;
    private int lastSendState;
    private boolean lastUnreadState;
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
    private StaticLayout timeLayout;
    private int timeLeft;
    private int timeTop = AndroidUtilities.dp(17.0f);
    private int unreadCount;
    public boolean useSeparator = false;
    private User user = null;

    public DialogCell(Context context) {
        super(context);
        if (namePaint == null) {
            namePaint = new TextPaint(1);
            namePaint.setColor(-14606047);
            namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            nameEncryptedPaint = new TextPaint(1);
            nameEncryptedPaint.setColor(-16734706);
            nameEncryptedPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            messagePaint = new TextPaint(1);
            messagePaint.setColor(Theme.DIALOGS_MESSAGE_TEXT_COLOR);
            messagePaint.linkColor = Theme.DIALOGS_MESSAGE_TEXT_COLOR;
            linePaint = new Paint();
            linePaint.setColor(Theme.GROUP_CREATE_DIVIDER_COLOR);
            backPaint = new Paint();
            backPaint.setColor(251658240);
            pinnedPaint = new Paint();
            pinnedPaint.setColor(134217728);
            messagePrintingPaint = new TextPaint(1);
            messagePrintingPaint.setColor(-11697229);
            timePaint = new TextPaint(1);
            timePaint.setColor(-6710887);
            countPaint = new TextPaint(1);
            countPaint.setColor(-1);
            countPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            lockDrawable = getResources().getDrawable(R.drawable.list_secret);
            checkDrawable = getResources().getDrawable(R.drawable.dialogs_check);
            halfCheckDrawable = getResources().getDrawable(R.drawable.dialogs_halfcheck);
            clockDrawable = getResources().getDrawable(R.drawable.msg_clock);
            errorDrawable = getResources().getDrawable(R.drawable.dialogs_warning);
            countDrawable = getResources().getDrawable(R.drawable.dialogs_badge);
            countDrawableGrey = getResources().getDrawable(R.drawable.dialogs_badge2);
            groupDrawable = getResources().getDrawable(R.drawable.list_group);
            broadcastDrawable = getResources().getDrawable(R.drawable.list_broadcast);
            muteDrawable = getResources().getDrawable(R.drawable.mute_grey);
            verifiedDrawable = getResources().getDrawable(R.drawable.check_list);
            botDrawable = getResources().getDrawable(R.drawable.bot_list);
            pinnedDrawable = getResources().getDrawable(R.drawable.pin_round);
        }
        namePaint.setTextSize((float) AndroidUtilities.dp(17.0f));
        nameEncryptedPaint.setTextSize((float) AndroidUtilities.dp(17.0f));
        messagePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        messagePrintingPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        timePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        countPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        setBackgroundResource(R.drawable.list_selector);
        this.avatarImage = new ImageReceiver(this);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setDialog(TL_dialog dialog, int i, int type) {
        this.currentDialogId = dialog.id;
        this.isDialogCell = true;
        this.index = i;
        this.dialogsType = type;
        update(0);
    }

    public void setDialog(long dialog_id, MessageObject messageObject, int date) {
        boolean z;
        this.currentDialogId = dialog_id;
        this.message = messageObject;
        this.isDialogCell = false;
        this.lastMessageDate = date;
        this.currentEditDate = messageObject != null ? messageObject.messageOwner.edit_date : 0;
        this.unreadCount = 0;
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

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.useSeparator ? 1 : 0) + AndroidUtilities.dp(72.0f));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.currentDialogId == 0) {
            super.onLayout(changed, left, top, right, bottom);
        } else if (changed) {
            buildLayout();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
            getBackground().setHotspot(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void buildLayout() {
        String mess;
        TextPaint currentNamePaint;
        int nameWidth;
        int w;
        int avatarLeft;
        int messageWidth;
        String nameString = "";
        String timeString = "";
        String countString = null;
        CharSequence messageString = "";
        CharSequence printingString = null;
        if (this.isDialogCell) {
            printingString = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentDialogId));
        }
        TextPaint currentNamePaint2 = namePaint;
        TextPaint currentMessagePaint = messagePaint;
        boolean checkMessage = true;
        this.drawNameGroup = false;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameBot = false;
        this.drawVerified = false;
        if (this.encryptedChat != null) {
            this.drawNameLock = true;
            this.nameLockTop = AndroidUtilities.dp(16.5f);
            if (LocaleController.isRTL) {
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - lockDrawable.getIntrinsicWidth();
                this.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + lockDrawable.getIntrinsicWidth();
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
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (this.drawNameGroup ? groupDrawable.getIntrinsicWidth() : broadcastDrawable.getIntrinsicWidth());
                this.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                int intrinsicWidth;
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                int dp = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4));
                if (this.drawNameGroup) {
                    intrinsicWidth = groupDrawable.getIntrinsicWidth();
                } else {
                    intrinsicWidth = broadcastDrawable.getIntrinsicWidth();
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
                        this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - botDrawable.getIntrinsicWidth();
                        this.nameLeft = AndroidUtilities.dp(14.0f);
                    } else {
                        this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + botDrawable.getIntrinsicWidth();
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
            this.draftMessage = DraftQuery.getDraft(this.currentDialogId);
            if ((this.draftMessage != null && ((TextUtils.isEmpty(this.draftMessage.message) && this.draftMessage.reply_to_msg_id == 0) || (lastDate > this.draftMessage.date && this.unreadCount != 0))) || (!(!ChatObject.isChannel(this.chat) || this.chat.megagroup || this.chat.creator || this.chat.editor) || (this.chat != null && (this.chat.left || this.chat.kicked)))) {
                this.draftMessage = null;
            }
        } else {
            this.draftMessage = null;
        }
        if (printingString != null) {
            messageString = printingString;
            this.lastPrintString = printingString;
            currentMessagePaint = messagePrintingPaint;
        } else {
            this.lastPrintString = null;
            SpannableStringBuilder stringBuilder;
            if (this.draftMessage != null) {
                checkMessage = false;
                String draftString;
                if (TextUtils.isEmpty(this.draftMessage.message)) {
                    draftString = LocaleController.getString("Draft", R.string.Draft);
                    stringBuilder = SpannableStringBuilder.valueOf(draftString);
                    stringBuilder.setSpan(new ForegroundColorSpan(Theme.DIALOGS_DRAFT_TEXT_COLOR), 0, draftString.length(), 33);
                    Object messageString2 = stringBuilder;
                } else {
                    mess = this.draftMessage.message;
                    if (mess.length() > 150) {
                        mess = mess.substring(0, 150);
                    }
                    stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{LocaleController.getString("Draft", R.string.Draft), mess.replace('\n', ' ')}));
                    stringBuilder.setSpan(new ForegroundColorSpan(Theme.DIALOGS_DRAFT_TEXT_COLOR), 0, draftString.length() + 1, 33);
                    messageString = Emoji.replaceEmoji(stringBuilder, messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                }
            } else if (this.message != null) {
                User fromUser = null;
                Chat fromChat = null;
                if (this.message.isFromUser()) {
                    fromUser = MessagesController.getInstance().getUser(Integer.valueOf(this.message.messageOwner.from_id));
                } else {
                    fromChat = MessagesController.getInstance().getChat(Integer.valueOf(this.message.messageOwner.to_id.channel_id));
                }
                if (this.message.messageOwner instanceof TL_messageService) {
                    messageString = this.message.messageText;
                    currentMessagePaint = messagePrintingPaint;
                } else if (this.chat != null && this.chat.id > 0 && fromChat == null) {
                    String name;
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
                        stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{name, mess.replace('\n', ' ')}));
                    } else if (this.message.messageOwner.media != null && !this.message.isMediaEmpty()) {
                        currentMessagePaint = messagePrintingPaint;
                        if (this.message.messageOwner.media instanceof TL_messageMediaGame) {
                            stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: 🎮 %s", new Object[]{name, this.message.messageOwner.media.game.title}));
                        } else if (this.message.type == 14) {
                            stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: 🎧 %s - %s", new Object[]{name, this.message.getMusicAuthor(), this.message.getMusicTitle()}));
                        } else {
                            stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{name, this.message.messageText}));
                        }
                        stringBuilder.setSpan(new ForegroundColorSpan(-11697229), name.length() + 2, stringBuilder.length(), 33);
                    } else if (this.message.messageOwner.message != null) {
                        mess = this.message.messageOwner.message;
                        if (mess.length() > 150) {
                            mess = mess.substring(0, 150);
                        }
                        stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{name, mess.replace('\n', ' ')}));
                    } else {
                        stringBuilder = SpannableStringBuilder.valueOf("");
                    }
                    if (stringBuilder.length() > 0) {
                        stringBuilder.setSpan(new ForegroundColorSpan(-11697229), 0, name.length() + 1, 33);
                    }
                    messageString = Emoji.replaceEmoji(stringBuilder, messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
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
                        currentMessagePaint = messagePrintingPaint;
                    }
                }
            } else if (this.encryptedChat != null) {
                currentMessagePaint = messagePrintingPaint;
                if (this.encryptedChat instanceof TL_encryptedChatRequested) {
                    messageString = LocaleController.getString("EncryptionProcessing", R.string.EncryptionProcessing);
                } else if (this.encryptedChat instanceof TL_encryptedChatWaiting) {
                    messageString = (this.user == null || this.user.first_name == null) ? LocaleController.formatString("AwaitingEncryption", R.string.AwaitingEncryption, "") : LocaleController.formatString("AwaitingEncryption", R.string.AwaitingEncryption, this.user.first_name);
                } else if (this.encryptedChat instanceof TL_encryptedChatDiscarded) {
                    messageString = LocaleController.getString("EncryptionRejected", R.string.EncryptionRejected);
                } else if (this.encryptedChat instanceof TL_encryptedChat) {
                    messageString = this.encryptedChat.admin_id == UserConfig.getClientUserId() ? (this.user == null || this.user.first_name == null) ? LocaleController.formatString("EncryptedChatStartedOutgoing", R.string.EncryptedChatStartedOutgoing, "") : LocaleController.formatString("EncryptedChatStartedOutgoing", R.string.EncryptedChatStartedOutgoing, this.user.first_name) : LocaleController.getString("EncryptedChatStartedIncoming", R.string.EncryptedChatStartedIncoming);
                }
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
            this.drawError = false;
        } else {
            if (this.unreadCount != 0) {
                this.drawCount = true;
                countString = String.format("%d", new Object[]{Integer.valueOf(this.unreadCount)});
            } else {
                this.drawCount = false;
            }
            if (!this.message.isOut() || this.draftMessage != null) {
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
            } else if (this.message.isSent()) {
                boolean z = !this.message.isUnread() || (ChatObject.isChannel(this.chat) && !this.chat.megagroup);
                this.drawCheck1 = z;
                this.drawCheck2 = true;
                this.drawClock = false;
                this.drawError = false;
            }
        }
        int timeWidth = (int) Math.ceil((double) timePaint.measureText(timeString));
        this.timeLayout = new StaticLayout(timeString, timePaint, timeWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        if (LocaleController.isRTL) {
            this.timeLeft = AndroidUtilities.dp(15.0f);
        } else {
            this.timeLeft = (getMeasuredWidth() - AndroidUtilities.dp(15.0f)) - timeWidth;
        }
        if (this.chat != null) {
            nameString = this.chat.title;
            currentNamePaint = currentNamePaint2;
        } else {
            if (this.user != null) {
                if (this.user.id == UserConfig.getClientUserId()) {
                    nameString = LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName);
                } else if (this.user.id / 1000 == 777 || this.user.id / 1000 == 333 || ContactsController.getInstance().contactsDict.get(this.user.id) != null) {
                    nameString = UserObject.getUserName(this.user);
                } else if (ContactsController.getInstance().contactsDict.size() == 0 && (!ContactsController.getInstance().contactsLoaded || ContactsController.getInstance().isLoadingContacts())) {
                    nameString = UserObject.getUserName(this.user);
                } else if (this.user.phone == null || this.user.phone.length() == 0) {
                    nameString = UserObject.getUserName(this.user);
                } else {
                    nameString = PhoneFormat.getInstance().format("+" + this.user.phone);
                }
                if (this.encryptedChat != null) {
                    currentNamePaint = nameEncryptedPaint;
                }
            }
            currentNamePaint = currentNamePaint2;
        }
        if (nameString.length() == 0) {
            nameString = LocaleController.getString("HiddenName", R.string.HiddenName);
        }
        if (LocaleController.isRTL) {
            nameWidth = ((getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - timeWidth;
            this.nameLeft += timeWidth;
        } else {
            nameWidth = ((getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp(14.0f)) - timeWidth;
        }
        if (this.drawNameLock) {
            nameWidth -= AndroidUtilities.dp(4.0f) + lockDrawable.getIntrinsicWidth();
        } else if (this.drawNameGroup) {
            nameWidth -= AndroidUtilities.dp(4.0f) + groupDrawable.getIntrinsicWidth();
        } else if (this.drawNameBroadcast) {
            nameWidth -= AndroidUtilities.dp(4.0f) + broadcastDrawable.getIntrinsicWidth();
        } else if (this.drawNameBot) {
            nameWidth -= AndroidUtilities.dp(4.0f) + botDrawable.getIntrinsicWidth();
        }
        if (this.drawClock) {
            w = clockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            nameWidth -= w;
            if (LocaleController.isRTL) {
                this.checkDrawLeft = (this.timeLeft + timeWidth) + AndroidUtilities.dp(5.0f);
                this.nameLeft += w;
            } else {
                this.checkDrawLeft = this.timeLeft - w;
            }
        } else if (this.drawCheck2) {
            w = checkDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            nameWidth -= w;
            if (this.drawCheck1) {
                nameWidth -= halfCheckDrawable.getIntrinsicWidth() - AndroidUtilities.dp(8.0f);
                if (LocaleController.isRTL) {
                    this.checkDrawLeft = (this.timeLeft + timeWidth) + AndroidUtilities.dp(5.0f);
                    this.halfCheckDrawLeft = this.checkDrawLeft + AndroidUtilities.dp(5.5f);
                    this.nameLeft += (halfCheckDrawable.getIntrinsicWidth() + w) - AndroidUtilities.dp(8.0f);
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
            w = AndroidUtilities.dp(6.0f) + muteDrawable.getIntrinsicWidth();
            nameWidth -= w;
            if (LocaleController.isRTL) {
                this.nameLeft += w;
            }
        } else if (this.drawVerified) {
            w = AndroidUtilities.dp(6.0f) + verifiedDrawable.getIntrinsicWidth();
            nameWidth -= w;
            if (LocaleController.isRTL) {
                this.nameLeft += w;
            }
        }
        nameWidth = Math.max(AndroidUtilities.dp(12.0f), nameWidth);
        try {
            this.nameLayout = new StaticLayout(TextUtils.ellipsize(nameString.replace('\n', ' '), currentNamePaint, (float) (nameWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentNamePaint, nameWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
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
            w = errorDrawable.getIntrinsicWidth() + AndroidUtilities.dp(8.0f);
            messageWidth = messageWidth2 - w;
            if (LocaleController.isRTL) {
                this.errorLeft = AndroidUtilities.dp(11.0f);
                this.messageLeft += w;
            } else {
                this.errorLeft = (getMeasuredWidth() - errorDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(11.0f);
            }
        } else if (countString != null) {
            this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) countPaint.measureText(countString)));
            this.countLayout = new StaticLayout(countString, countPaint, this.countWidth, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
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
            if (this.drawPin) {
                w = pinnedDrawable.getIntrinsicWidth() + AndroidUtilities.dp(8.0f);
                messageWidth = messageWidth2 - w;
                if (LocaleController.isRTL) {
                    this.pinLeft = AndroidUtilities.dp(14.0f);
                    this.messageLeft += w;
                } else {
                    this.pinLeft = (getMeasuredWidth() - pinnedDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(14.0f);
                }
            } else {
                messageWidth = messageWidth2;
            }
            this.drawCount = false;
        }
        if (checkMessage) {
            if (messageString == null) {
                messageString = "";
            }
            mess = messageString.toString();
            if (mess.length() > 150) {
                mess = mess.substring(0, 150);
            }
            messageString = Emoji.replaceEmoji(mess.replace('\n', ' '), messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
        }
        messageWidth = Math.max(AndroidUtilities.dp(12.0f), messageWidth);
        try {
            this.messageLayout = new StaticLayout(TextUtils.ellipsize(messageString, currentMessagePaint, (float) (messageWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentMessagePaint, messageWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
        }
        float left;
        double widthpx;
        if (LocaleController.isRTL) {
            if (this.nameLayout != null && this.nameLayout.getLineCount() > 0) {
                left = this.nameLayout.getLineLeft(0);
                widthpx = Math.ceil((double) this.nameLayout.getLineWidth(0));
                if (this.dialogMuted && !this.drawVerified) {
                    this.nameMuteLeft = (int) (((((double) this.nameLeft) + (((double) nameWidth) - widthpx)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) muteDrawable.getIntrinsicWidth()));
                } else if (this.drawVerified) {
                    this.nameMuteLeft = (int) (((((double) this.nameLeft) + (((double) nameWidth) - widthpx)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) verifiedDrawable.getIntrinsicWidth()));
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

    public void setDialogSelected(boolean value) {
        if (this.isSelected != value) {
            invalidate();
        }
        this.isSelected = value;
    }

    private ArrayList<TL_dialog> getDialogsArray() {
        if (this.dialogsType == 0) {
            return MessagesController.getInstance().dialogs;
        }
        if (this.dialogsType == 1) {
            return MessagesController.getInstance().dialogsServerOnly;
        }
        if (this.dialogsType == 2) {
            return MessagesController.getInstance().dialogsGroupsOnly;
        }
        return null;
    }

    public void checkCurrentDialogIndex() {
        if (this.index < getDialogsArray().size()) {
            TL_dialog dialog = (TL_dialog) getDialogsArray().get(this.index);
            DraftMessage newDraftMessage = DraftQuery.getDraft(this.currentDialogId);
            MessageObject newMessageObject = (MessageObject) MessagesController.getInstance().dialogMessage.get(Long.valueOf(dialog.id));
            if (this.currentDialogId != dialog.id || ((this.message != null && this.message.getId() != dialog.top_message) || ((newMessageObject != null && newMessageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != dialog.unread_count || this.message != newMessageObject || ((this.message == null && newMessageObject != null) || newDraftMessage != this.draftMessage || this.drawPin != dialog.pinned)))) {
                this.currentDialogId = dialog.id;
                update(0);
            }
        }
    }

    public void update(int mask) {
        TL_dialog dialog;
        boolean z;
        if (this.isDialogCell) {
            dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
            if (dialog != null && mask == 0) {
                int i;
                this.message = (MessageObject) MessagesController.getInstance().dialogMessage.get(Long.valueOf(dialog.id));
                if (this.message == null || !this.message.isUnread()) {
                    z = false;
                } else {
                    z = true;
                }
                this.lastUnreadState = z;
                this.unreadCount = dialog.unread_count;
                if (this.message != null) {
                    i = this.message.messageOwner.edit_date;
                } else {
                    i = 0;
                }
                this.currentEditDate = i;
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
                CharSequence printString = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentDialogId));
                if ((this.lastPrintString != null && printString == null) || ((this.lastPrintString == null && printString != null) || !(this.lastPrintString == null || printString == null || this.lastPrintString.equals(printString)))) {
                    continueUpdate = true;
                }
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
                    dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
                    if (!(dialog == null || this.unreadCount == dialog.unread_count)) {
                        this.unreadCount = dialog.unread_count;
                        continueUpdate = true;
                    }
                }
            }
            if (!(continueUpdate || (mask & 4096) == 0 || this.message == null || this.lastSendState == this.message.messageOwner.send_state)) {
                this.lastSendState = this.message.messageOwner.send_state;
                continueUpdate = true;
            }
            if (!continueUpdate) {
                return;
            }
        }
        if (this.isDialogCell && MessagesController.getInstance().isDialogMuted(this.currentDialogId)) {
            z = true;
        } else {
            z = false;
        }
        this.dialogMuted = z;
        this.user = null;
        this.chat = null;
        this.encryptedChat = null;
        int lower_id = (int) this.currentDialogId;
        int high_id = (int) (this.currentDialogId >> 32);
        if (lower_id == 0) {
            this.encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id));
            if (this.encryptedChat != null) {
                this.user = MessagesController.getInstance().getUser(Integer.valueOf(this.encryptedChat.user_id));
            }
        } else if (high_id == 1) {
            this.chat = MessagesController.getInstance().getChat(Integer.valueOf(lower_id));
        } else if (lower_id < 0) {
            this.chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
            if (!(this.isDialogCell || this.chat == null || this.chat.migrated_to == null)) {
                Chat chat2 = MessagesController.getInstance().getChat(Integer.valueOf(this.chat.migrated_to.channel_id));
                if (chat2 != null) {
                    this.chat = chat2;
                }
            }
        } else {
            this.user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
        }
        TLObject photo = null;
        if (this.user != null) {
            if (this.user.photo != null) {
                photo = this.user.photo.photo_small;
            }
            this.avatarDrawable.setInfo(this.user);
        } else if (this.chat != null) {
            if (this.chat.photo != null) {
                photo = this.chat.photo.photo_small;
            }
            this.avatarDrawable.setInfo(this.chat);
        }
        this.avatarImage.setImage(photo, "50_50", this.avatarDrawable, null, false);
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentDialogId != 0) {
            if (this.isSelected) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), backPaint);
            }
            if (this.drawPin) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), pinnedPaint);
            }
            if (this.drawNameLock) {
                setDrawableBounds(lockDrawable, this.nameLockLeft, this.nameLockTop);
                lockDrawable.draw(canvas);
            } else if (this.drawNameGroup) {
                setDrawableBounds(groupDrawable, this.nameLockLeft, this.nameLockTop);
                groupDrawable.draw(canvas);
            } else if (this.drawNameBroadcast) {
                setDrawableBounds(broadcastDrawable, this.nameLockLeft, this.nameLockTop);
                broadcastDrawable.draw(canvas);
            } else if (this.drawNameBot) {
                setDrawableBounds(botDrawable, this.nameLockLeft, this.nameLockTop);
                botDrawable.draw(canvas);
            }
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, (float) AndroidUtilities.dp(13.0f));
                this.nameLayout.draw(canvas);
                canvas.restore();
            }
            canvas.save();
            canvas.translate((float) this.timeLeft, (float) this.timeTop);
            this.timeLayout.draw(canvas);
            canvas.restore();
            if (this.messageLayout != null) {
                canvas.save();
                canvas.translate((float) this.messageLeft, (float) this.messageTop);
                try {
                    this.messageLayout.draw(canvas);
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                canvas.restore();
            }
            if (this.drawClock) {
                setDrawableBounds(clockDrawable, this.checkDrawLeft, this.checkDrawTop);
                clockDrawable.draw(canvas);
            } else if (this.drawCheck2) {
                if (this.drawCheck1) {
                    setDrawableBounds(halfCheckDrawable, this.halfCheckDrawLeft, this.checkDrawTop);
                    halfCheckDrawable.draw(canvas);
                    setDrawableBounds(checkDrawable, this.checkDrawLeft, this.checkDrawTop);
                    checkDrawable.draw(canvas);
                } else {
                    setDrawableBounds(checkDrawable, this.checkDrawLeft, this.checkDrawTop);
                    checkDrawable.draw(canvas);
                }
            }
            if (this.dialogMuted && !this.drawVerified) {
                setDrawableBounds(muteDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
                muteDrawable.draw(canvas);
            } else if (this.drawVerified) {
                setDrawableBounds(verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
                verifiedDrawable.draw(canvas);
            }
            if (this.drawError) {
                setDrawableBounds(errorDrawable, this.errorLeft, this.errorTop);
                errorDrawable.draw(canvas);
            } else if (this.drawCount) {
                if (this.dialogMuted) {
                    setDrawableBounds(countDrawableGrey, this.countLeft - AndroidUtilities.dp(5.5f), this.countTop, this.countWidth + AndroidUtilities.dp(11.0f), AndroidUtilities.dp(23.0f));
                    countDrawableGrey.draw(canvas);
                } else {
                    setDrawableBounds(countDrawable, this.countLeft - AndroidUtilities.dp(5.5f), this.countTop, this.countWidth + AndroidUtilities.dp(11.0f), AndroidUtilities.dp(23.0f));
                    countDrawable.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                if (this.countLayout != null) {
                    this.countLayout.draw(canvas);
                }
                canvas.restore();
            } else if (this.drawPin) {
                setDrawableBounds(pinnedDrawable, this.pinLeft, this.pinTop);
                pinnedDrawable.draw(canvas);
            }
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), linePaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), linePaint);
                }
            }
            this.avatarImage.draw(canvas);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
