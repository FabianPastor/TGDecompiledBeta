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
        update(0);
    }

    public void setDialog(CustomDialog dialog) {
        this.customDialog = dialog;
        update(0);
    }

    public void setDialog(long dialog_id, MessageObject messageObject, int date) {
        this.currentDialogId = dialog_id;
        this.message = messageObject;
        this.isDialogCell = false;
        this.lastMessageDate = date;
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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.checkBox != null) {
            this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(72.0f) + this.useSeparator);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.currentDialogId != 0 || this.customDialog != null) {
            if (this.checkBox != null) {
                int x = LocaleController.isRTL ? (right - left) - AndroidUtilities.dp(42.0f) : AndroidUtilities.dp(42.0f);
                int y = AndroidUtilities.dp(NUM);
                this.checkBox.layout(x, y, this.checkBox.getMeasuredWidth() + x, this.checkBox.getMeasuredHeight() + y);
            }
            if (changed) {
                try {
                    buildLayout();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }

    public void buildLayout() {
        String messageFormat;
        int i;
        boolean z;
        String format;
        String nameString;
        int i2;
        int i3;
        int timeWidth;
        int nameWidth;
        int messageWidth;
        boolean z2;
        boolean z3;
        float left;
        double widthpx;
        String nameString2 = TtmlNode.ANONYMOUS_REGION_ID;
        String timeString = TtmlNode.ANONYMOUS_REGION_ID;
        CharSequence messageString = TtmlNode.ANONYMOUS_REGION_ID;
        CharSequence printingString = null;
        if (this.isDialogCell) {
            printingString = (CharSequence) MessagesController.getInstance(r1.currentAccount).printingStrings.get(r1.currentDialogId);
        }
        TextPaint currentNamePaint = Theme.dialogs_namePaint;
        TextPaint currentMessagePaint = Theme.dialogs_messagePaint;
        boolean checkMessage = true;
        r1.drawNameGroup = false;
        r1.drawNameBroadcast = false;
        r1.drawNameLock = false;
        r1.drawNameBot = false;
        r1.drawVerified = false;
        r1.drawPinBackground = false;
        boolean showChecks = UserObject.isUserSelf(r1.user) ^ true;
        boolean drawTime = true;
        if (VERSION.SDK_INT >= 18) {
            messageFormat = "%s: \u2068%s\u2069";
        } else {
            messageFormat = "%s: %s";
        }
        r1.lastMessageString = r1.message != null ? r1.message.messageText : null;
        String str;
        String str2;
        String str3;
        Object[] objArr;
        boolean z4;
        if (r1.customDialog != null) {
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
                String name = LocaleController.getString("FromYou", R.string.FromYou);
                checkMessage = false;
                if (r1.customDialog.isMedia) {
                    currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                    nameString2 = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{name, r1.message.messageText}));
                    str = null;
                    str2 = null;
                    nameString2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_attachMessage)), name.length() + 2, nameString2.length(), 33);
                } else {
                    str3 = timeString;
                    str = null;
                    str2 = null;
                    nameString2 = r1.customDialog.message;
                    if (nameString2.length() > 150) {
                        i = 0;
                        nameString2 = nameString2.substring(0, 150);
                    } else {
                        i = 0;
                    }
                    nameString2 = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{name, nameString2.replace('\n', ' ')}));
                }
                if (nameString2.length() > 0) {
                    z = false;
                    nameString2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_nameMessage)), 0, name.length() + 1, 33);
                } else {
                    z = false;
                }
                nameString2 = Emoji.replaceEmoji(nameString2, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), z);
            } else {
                str3 = timeString;
                str = null;
                str2 = null;
                nameString2 = r1.customDialog.message;
                if (r1.customDialog.isMedia) {
                    currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                }
            }
            timeString = LocaleController.stringForMessageListDate((long) r1.customDialog.date);
            if (r1.customDialog.unread_count != 0) {
                r1.drawCount = true;
                objArr = new Object[1];
                z4 = false;
                objArr[0] = Integer.valueOf(r1.customDialog.unread_count);
                format = String.format("%d", objArr);
            } else {
                z4 = false;
                r1.drawCount = false;
                format = str;
            }
            if (r1.customDialog.sent) {
                r1.drawCheck1 = true;
                r1.drawCheck2 = true;
                r1.drawClock = z4;
                r1.drawError = z4;
            } else {
                r1.drawCheck1 = z4;
                r1.drawCheck2 = z4;
                r1.drawClock = z4;
                r1.drawError = z4;
            }
            nameString = r1.customDialog.name;
            if (r1.customDialog.type == 2) {
                currentNamePaint = Theme.dialogs_nameEncryptedPaint;
            }
            messageString = nameString2;
            nameString2 = str2;
        } else {
            CharSequence messageString2;
            CharSequence charSequence;
            String nameString3 = nameString2;
            str3 = timeString;
            str = null;
            str2 = null;
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
            int lastDate = r1.lastMessageDate;
            if (r1.lastMessageDate == 0 && r1.message != null) {
                lastDate = r1.message.messageOwner.date;
            }
            if (r1.isDialogCell) {
                r1.draftMessage = DataQuery.getInstance(r1.currentAccount).getDraft(r1.currentDialogId);
                if ((r1.draftMessage != null && ((TextUtils.isEmpty(r1.draftMessage.message) && r1.draftMessage.reply_to_msg_id == 0) || (lastDate > r1.draftMessage.date && r1.unreadCount != 0))) || ((ChatObject.isChannel(r1.chat) && !r1.chat.megagroup && !r1.chat.creator && (r1.chat.admin_rights == null || !r1.chat.admin_rights.post_messages)) || (r1.chat != null && (r1.chat.left || r1.chat.kicked)))) {
                    r1.draftMessage = null;
                }
            } else {
                r1.draftMessage = null;
            }
            if (printingString != null) {
                messageString = printingString;
                r1.lastPrintString = printingString;
                currentMessagePaint = Theme.dialogs_messagePrintingPaint;
            } else {
                r1.lastPrintString = null;
                SpannableStringBuilder stringBuilder;
                if (r1.draftMessage != null) {
                    checkMessage = false;
                    if (TextUtils.isEmpty(r1.draftMessage.message)) {
                        format = LocaleController.getString("Draft", R.string.Draft);
                        stringBuilder = SpannableStringBuilder.valueOf(format);
                        int i4 = lastDate;
                        stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_draft)), 0, format.length(), 33);
                        Object messageString3 = stringBuilder;
                    } else {
                        lastDate = r1.draftMessage.message;
                        if (lastDate.length() > 150) {
                            i = 0;
                            lastDate = lastDate.substring(0, 150);
                        } else {
                            i = 0;
                        }
                        stringBuilder = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{LocaleController.getString("Draft", R.string.Draft), lastDate.replace('\n', ' ')}));
                        String mess = lastDate;
                        stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_draft)), 0, format.length() + 1, 33);
                        messageString = Emoji.replaceEmoji(stringBuilder, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    }
                } else {
                    if (r1.message != null) {
                        User fromUser = null;
                        Chat fromChat = null;
                        if (r1.message.isFromUser()) {
                            fromUser = MessagesController.getInstance(r1.currentAccount).getUser(Integer.valueOf(r1.message.messageOwner.from_id));
                        } else {
                            fromChat = MessagesController.getInstance(r1.currentAccount).getChat(Integer.valueOf(r1.message.messageOwner.to_id.channel_id));
                        }
                        if (r1.dialogsType == 3 && UserObject.isUserSelf(r1.user)) {
                            messageString = LocaleController.getString("SavedMessagesInfo", R.string.SavedMessagesInfo);
                            showChecks = false;
                            drawTime = false;
                        } else if (r1.message.messageOwner instanceof TL_messageService) {
                            if (ChatObject.isChannel(r1.chat) && (r1.message.messageOwner.action instanceof TL_messageActionHistoryClear)) {
                                messageString = TtmlNode.ANONYMOUS_REGION_ID;
                                showChecks = false;
                            } else {
                                messageString = r1.message.messageText;
                            }
                            currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                        } else if (r1.chat == null || r1.chat.id <= 0 || fromChat != null) {
                            r29 = fromChat;
                            if ((r1.message.messageOwner.media instanceof TL_messageMediaPhoto) && (r1.message.messageOwner.media.photo instanceof TL_photoEmpty) && r1.message.messageOwner.media.ttl_seconds != 0) {
                                messageString = LocaleController.getString("AttachPhotoExpired", R.string.AttachPhotoExpired);
                            } else if ((r1.message.messageOwner.media instanceof TL_messageMediaDocument) && (r1.message.messageOwner.media.document instanceof TL_documentEmpty) && r1.message.messageOwner.media.ttl_seconds != 0) {
                                messageString = LocaleController.getString("AttachVideoExpired", R.string.AttachVideoExpired);
                            } else if (r1.message.caption != null) {
                                messageString = r1.message.caption;
                            } else {
                                if (r1.message.messageOwner.media instanceof TL_messageMediaGame) {
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("\ud83c\udfae ");
                                    stringBuilder2.append(r1.message.messageOwner.media.game.title);
                                    messageString2 = stringBuilder2.toString();
                                } else if (r1.message.type == 14) {
                                    messageString2 = String.format("\ud83c\udfa7 %s - %s", new Object[]{r1.message.getMusicAuthor(), r1.message.getMusicTitle()});
                                } else {
                                    messageString2 = r1.message.messageText;
                                }
                                messageString = messageString2;
                                if (r1.message.messageOwner.media != null && r1.message.isMediaEmpty() == null) {
                                    currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                                }
                            }
                        } else {
                            Object[] objArr2;
                            boolean z5;
                            if (r1.message.isOutOwner()) {
                                format = LocaleController.getString("FromYou", R.string.FromYou);
                            } else if (fromUser != null) {
                                format = UserObject.getFirstName(fromUser).replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
                            } else if (fromChat != null) {
                                format = fromChat.title.replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
                            } else {
                                format = "DELETED";
                                checkMessage = false;
                                if (r1.message.caption == null) {
                                    nameString = r1.message.caption.toString();
                                    if (nameString.length() <= 150) {
                                        i2 = 0;
                                        nameString = nameString.substring(0, 150);
                                    } else {
                                        i2 = 0;
                                    }
                                    objArr2 = new Object[2];
                                    objArr2[i2] = format;
                                    objArr2[1] = nameString.replace('\n', ' ');
                                    fromUser = SpannableStringBuilder.valueOf(String.format(messageFormat, objArr2));
                                } else {
                                    if (r1.message.messageOwner.media == null && r1.message.isMediaEmpty() == null) {
                                        TextPaint currentMessagePaint2;
                                        fromUser = Theme.dialogs_messagePrintingPaint;
                                        if (r1.message.messageOwner.media instanceof TL_messageMediaGame) {
                                            if (VERSION.SDK_INT >= 18) {
                                                stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: \ud83c\udfae \u2068%s\u2069", new Object[]{format, r1.message.messageOwner.media.game.title}));
                                            } else {
                                                stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: \ud83c\udfae %s", new Object[]{format, r1.message.messageOwner.media.game.title}));
                                            }
                                        } else if (r1.message.type != 14) {
                                            stringBuilder = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{format, r1.message.messageText}));
                                            currentMessagePaint2 = fromUser;
                                            stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_attachMessage)), format.length() + 2, stringBuilder.length(), 33);
                                            fromUser = stringBuilder;
                                            currentMessagePaint = currentMessagePaint2;
                                        } else if (VERSION.SDK_INT >= 18) {
                                            stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: \ud83c\udfa7 \u2068%s - %s\u2069", new Object[]{format, r1.message.getMusicAuthor(), r1.message.getMusicTitle()}));
                                        } else {
                                            stringBuilder = SpannableStringBuilder.valueOf(String.format("%s: \ud83c\udfa7 %s - %s", new Object[]{format, r1.message.getMusicAuthor(), r1.message.getMusicTitle()}));
                                        }
                                        currentMessagePaint2 = fromUser;
                                        stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_attachMessage)), format.length() + 2, stringBuilder.length(), 33);
                                        fromUser = stringBuilder;
                                        currentMessagePaint = currentMessagePaint2;
                                    } else if (r1.message.messageOwner.message == null) {
                                        fromUser = r1.message.messageOwner.message;
                                        if (fromUser.length() <= 150) {
                                            i3 = 0;
                                            fromUser = fromUser.substring(0, 150);
                                        } else {
                                            i3 = 0;
                                        }
                                        fromUser = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{format, fromUser.replace('\n', ' ')}));
                                    } else {
                                        fromUser = SpannableStringBuilder.valueOf(TtmlNode.ANONYMOUS_REGION_ID);
                                    }
                                }
                                if (fromUser.length() <= 0) {
                                    r29 = fromChat;
                                    z5 = false;
                                    fromUser.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_nameMessage)), 0, format.length() + 1, 33);
                                } else {
                                    z5 = false;
                                }
                                messageString = Emoji.replaceEmoji(fromUser, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), z5);
                            }
                            checkMessage = false;
                            if (r1.message.caption == null) {
                                if (r1.message.messageOwner.media == null) {
                                }
                                if (r1.message.messageOwner.message == null) {
                                    fromUser = SpannableStringBuilder.valueOf(TtmlNode.ANONYMOUS_REGION_ID);
                                } else {
                                    fromUser = r1.message.messageOwner.message;
                                    if (fromUser.length() <= 150) {
                                        i3 = 0;
                                    } else {
                                        i3 = 0;
                                        fromUser = fromUser.substring(0, 150);
                                    }
                                    fromUser = SpannableStringBuilder.valueOf(String.format(messageFormat, new Object[]{format, fromUser.replace('\n', ' ')}));
                                }
                            } else {
                                nameString = r1.message.caption.toString();
                                if (nameString.length() <= 150) {
                                    i2 = 0;
                                } else {
                                    i2 = 0;
                                    nameString = nameString.substring(0, 150);
                                }
                                objArr2 = new Object[2];
                                objArr2[i2] = format;
                                objArr2[1] = nameString.replace('\n', ' ');
                                fromUser = SpannableStringBuilder.valueOf(String.format(messageFormat, objArr2));
                            }
                            if (fromUser.length() <= 0) {
                                z5 = false;
                            } else {
                                r29 = fromChat;
                                z5 = false;
                                fromUser.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_nameMessage)), 0, format.length() + 1, 33);
                            }
                            messageString = Emoji.replaceEmoji(fromUser, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), z5);
                        }
                    } else if (r1.encryptedChat != null) {
                        currentMessagePaint = Theme.dialogs_messagePrintingPaint;
                        if (r1.encryptedChat instanceof TL_encryptedChatRequested) {
                            messageString = LocaleController.getString("EncryptionProcessing", R.string.EncryptionProcessing);
                        } else if (r1.encryptedChat instanceof TL_encryptedChatWaiting) {
                            messageString = (r1.user == null || r1.user.first_name == null) ? LocaleController.formatString("AwaitingEncryption", R.string.AwaitingEncryption, TtmlNode.ANONYMOUS_REGION_ID) : LocaleController.formatString("AwaitingEncryption", R.string.AwaitingEncryption, r1.user.first_name);
                        } else if (r1.encryptedChat instanceof TL_encryptedChatDiscarded) {
                            messageString = LocaleController.getString("EncryptionRejected", R.string.EncryptionRejected);
                        } else if (r1.encryptedChat instanceof TL_encryptedChat) {
                            messageString = r1.encryptedChat.admin_id == UserConfig.getInstance(r1.currentAccount).getClientUserId() ? (r1.user == null || r1.user.first_name == null) ? LocaleController.formatString("EncryptedChatStartedOutgoing", R.string.EncryptedChatStartedOutgoing, TtmlNode.ANONYMOUS_REGION_ID) : LocaleController.formatString("EncryptedChatStartedOutgoing", R.string.EncryptedChatStartedOutgoing, r1.user.first_name) : LocaleController.getString("EncryptedChatStartedIncoming", R.string.EncryptedChatStartedIncoming);
                        }
                    }
                }
            }
            messageString2 = messageString;
            if (r1.draftMessage != null) {
                timeString = LocaleController.stringForMessageListDate((long) r1.draftMessage.date);
            } else if (r1.lastMessageDate != 0) {
                timeString = LocaleController.stringForMessageListDate((long) r1.lastMessageDate);
            } else if (r1.message != null) {
                timeString = LocaleController.stringForMessageListDate((long) r1.message.messageOwner.date);
            } else {
                timeString = str3;
                if (r1.message != null) {
                    r1.drawCheck1 = false;
                    r1.drawCheck2 = false;
                    r1.drawClock = false;
                    r1.drawCount = false;
                    r1.drawMention = false;
                    r1.drawError = false;
                    format = str;
                } else {
                    if (r1.unreadCount != 0 || (r1.unreadCount == 1 && r1.unreadCount == r1.mentionCount && r1.message != null && r1.message.messageOwner.mentioned)) {
                        z4 = false;
                        r1.drawCount = false;
                        format = str;
                    } else {
                        r1.drawCount = true;
                        objArr = new Object[1];
                        z4 = false;
                        objArr[0] = Integer.valueOf(r1.unreadCount);
                        format = String.format("%d", objArr);
                    }
                    if (r1.mentionCount == 0) {
                        r1.drawMention = true;
                        str2 = "@";
                    } else {
                        r1.drawMention = z4;
                    }
                    if (r1.message.isOut() || r1.draftMessage != null || !showChecks) {
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
                        if (r1.message.isUnread()) {
                            if (!ChatObject.isChannel(r1.chat) || r1.chat.megagroup) {
                                z = false;
                                r1.drawCheck1 = z;
                                r1.drawCheck2 = true;
                                r1.drawClock = false;
                                r1.drawError = false;
                            }
                        }
                        z = true;
                        r1.drawCheck1 = z;
                        r1.drawCheck2 = true;
                        r1.drawClock = false;
                        r1.drawError = false;
                    }
                }
                nameString = str2;
                if (r1.chat != null) {
                    messageString = r1.chat.title;
                } else if (r1.user == null) {
                    if (UserObject.isUserSelf(r1.user)) {
                        if (r1.dialogsType == 3) {
                            r1.drawPinBackground = true;
                        }
                        messageString = LocaleController.getString("SavedMessages", R.string.SavedMessages);
                    } else if (r1.user.id / 1000 != 777 || r1.user.id / 1000 == 333 || ContactsController.getInstance(r1.currentAccount).contactsDict.get(Integer.valueOf(r1.user.id)) != null) {
                        messageString = UserObject.getUserName(r1.user);
                    } else if (ContactsController.getInstance(r1.currentAccount).contactsDict.size() == 0 && (!ContactsController.getInstance(r1.currentAccount).contactsLoaded || ContactsController.getInstance(r1.currentAccount).isLoadingContacts())) {
                        messageString = UserObject.getUserName(r1.user);
                    } else if (r1.user.phone == null || r1.user.phone.length() == 0) {
                        messageString = UserObject.getUserName(r1.user);
                    } else {
                        PhoneFormat instance = PhoneFormat.getInstance();
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("+");
                        stringBuilder3.append(r1.user.phone);
                        messageString = instance.format(stringBuilder3.toString());
                    }
                    if (r1.encryptedChat != null) {
                        currentNamePaint = Theme.dialogs_nameEncryptedPaint;
                    }
                } else {
                    messageString = nameString3;
                }
                if (messageString.length() == 0) {
                    messageString = LocaleController.getString("HiddenName", R.string.HiddenName);
                }
                charSequence = messageString;
                messageString = messageString2;
                nameString2 = nameString;
                nameString = charSequence;
            }
            if (r1.message != null) {
                if (r1.unreadCount != 0) {
                }
                z4 = false;
                r1.drawCount = false;
                format = str;
                if (r1.mentionCount == 0) {
                    r1.drawMention = z4;
                } else {
                    r1.drawMention = true;
                    str2 = "@";
                }
                if (r1.message.isOut()) {
                }
                r1.drawCheck1 = false;
                r1.drawCheck2 = false;
                r1.drawClock = false;
                r1.drawError = false;
            } else {
                r1.drawCheck1 = false;
                r1.drawCheck2 = false;
                r1.drawClock = false;
                r1.drawCount = false;
                r1.drawMention = false;
                r1.drawError = false;
                format = str;
            }
            nameString = str2;
            if (r1.chat != null) {
                messageString = r1.chat.title;
            } else if (r1.user == null) {
                messageString = nameString3;
            } else {
                if (UserObject.isUserSelf(r1.user)) {
                    if (r1.dialogsType == 3) {
                        r1.drawPinBackground = true;
                    }
                    messageString = LocaleController.getString("SavedMessages", R.string.SavedMessages);
                } else {
                    if (r1.user.id / 1000 != 777) {
                    }
                    messageString = UserObject.getUserName(r1.user);
                }
                if (r1.encryptedChat != null) {
                    currentNamePaint = Theme.dialogs_nameEncryptedPaint;
                }
            }
            if (messageString.length() == 0) {
                messageString = LocaleController.getString("HiddenName", R.string.HiddenName);
            }
            charSequence = messageString;
            messageString = messageString2;
            nameString2 = nameString;
            nameString = charSequence;
        }
        if (drawTime) {
            timeWidth = (int) Math.ceil((double) Theme.dialogs_timePaint.measureText(timeString));
            r1.timeLayout = new StaticLayout(timeString, Theme.dialogs_timePaint, timeWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (LocaleController.isRTL) {
                r1.timeLeft = AndroidUtilities.dp(15.0f);
            } else {
                r1.timeLeft = (getMeasuredWidth() - AndroidUtilities.dp(15.0f)) - timeWidth;
            }
        } else {
            boolean z6 = showChecks;
            timeWidth = 0;
            r1.timeLayout = null;
            r1.timeLeft = 0;
        }
        if (LocaleController.isRTL) {
            nameWidth = ((getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - timeWidth;
            r1.nameLeft += timeWidth;
        } else {
            nameWidth = ((getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp(14.0f)) - timeWidth;
        }
        if (r1.drawNameLock) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        } else if (r1.drawNameGroup) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
        } else if (r1.drawNameBroadcast) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
        } else if (r1.drawNameBot) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
        }
        if (r1.drawClock) {
            i2 = Theme.dialogs_clockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            nameWidth -= i2;
            if (LocaleController.isRTL) {
                r1.checkDrawLeft = (r1.timeLeft + timeWidth) + AndroidUtilities.dp(5.0f);
                r1.nameLeft += i2;
            } else {
                r1.checkDrawLeft = r1.timeLeft - i2;
                String str4 = timeString;
            }
        } else {
            if (r1.drawCheck2) {
                i = Theme.dialogs_checkDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
                nameWidth -= i;
                if (r1.drawCheck1) {
                    nameWidth -= Theme.dialogs_halfCheckDrawable.getIntrinsicWidth() - AndroidUtilities.dp(8.0f);
                    if (LocaleController.isRTL) {
                        r1.checkDrawLeft = (r1.timeLeft + timeWidth) + AndroidUtilities.dp(5.0f);
                        r1.halfCheckDrawLeft = r1.checkDrawLeft + AndroidUtilities.dp(5.5f);
                        CharSequence charSequence2 = printingString;
                        r1.nameLeft += (Theme.dialogs_halfCheckDrawable.getIntrinsicWidth() + i) - AndroidUtilities.dp(8.0f);
                    } else {
                        r1.halfCheckDrawLeft = r1.timeLeft - i;
                        r1.checkDrawLeft = r1.halfCheckDrawLeft - AndroidUtilities.dp(5.5f);
                    }
                } else {
                    if (LocaleController.isRTL) {
                        r1.checkDrawLeft = (r1.timeLeft + timeWidth) + AndroidUtilities.dp(5.0f);
                        r1.nameLeft += i;
                    } else {
                        r1.checkDrawLeft = r1.timeLeft - i;
                    }
                }
            }
            if (!r1.dialogMuted && !r1.drawVerified) {
                i = AndroidUtilities.dp(6.0f) + Theme.dialogs_muteDrawable.getIntrinsicWidth();
                nameWidth -= i;
                if (LocaleController.isRTL) {
                    r1.nameLeft += i;
                }
            } else if (r1.drawVerified) {
                i = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                nameWidth -= i;
                if (LocaleController.isRTL) {
                    r1.nameLeft += i;
                }
            }
            nameWidth = Math.max(AndroidUtilities.dp(12.0f), nameWidth);
            r1.nameLayout = new StaticLayout(TextUtils.ellipsize(nameString.replace('\n', ' '), currentNamePaint, (float) (nameWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentNamePaint, nameWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            messageWidth = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
            if (LocaleController.isRTL) {
                r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                i2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? NUM : NUM);
            } else {
                r1.messageLeft = AndroidUtilities.dp(16.0f);
                i2 = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
            }
            r1.avatarImage.setImageCoords(i2, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
            if (r1.drawError) {
                if (format == null) {
                    if (nameString2 != null) {
                        if (r1.drawPin) {
                            i = Theme.dialogs_pinnedDrawable.getIntrinsicWidth() + AndroidUtilities.dp(8.0f);
                            messageWidth -= i;
                            if (LocaleController.isRTL) {
                                r1.pinLeft = (getMeasuredWidth() - Theme.dialogs_pinnedDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(14.0f);
                            } else {
                                r1.pinLeft = AndroidUtilities.dp(14.0f);
                                r1.messageLeft += i;
                            }
                        }
                        r1.drawCount = false;
                        r1.drawMention = false;
                    }
                }
                if (format == null) {
                    int i5 = i2;
                    z2 = drawTime;
                    r1.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format)));
                    r1.countLayout = new StaticLayout(format, Theme.dialogs_countTextPaint, r1.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    i = r1.countWidth + AndroidUtilities.dp(18.0f);
                    messageWidth -= i;
                    if (LocaleController.isRTL) {
                        r1.countLeft = (getMeasuredWidth() - r1.countWidth) - AndroidUtilities.dp(19.0f);
                    } else {
                        r1.countLeft = AndroidUtilities.dp(19.0f);
                        r1.messageLeft += i;
                    }
                    r1.drawCount = true;
                } else {
                    z2 = drawTime;
                    r1.countWidth = 0;
                }
                if (nameString2 != null) {
                    r1.mentionWidth = AndroidUtilities.dp(12.0f);
                    i = r1.mentionWidth + AndroidUtilities.dp(18.0f);
                    messageWidth -= i;
                    if (LocaleController.isRTL) {
                        r1.mentionLeft = ((getMeasuredWidth() - r1.mentionWidth) - AndroidUtilities.dp(19.0f)) - (r1.countWidth == 0 ? r1.countWidth + AndroidUtilities.dp(18.0f) : 0);
                    } else {
                        r1.mentionLeft = AndroidUtilities.dp(19.0f) + (r1.countWidth == 0 ? r1.countWidth + AndroidUtilities.dp(18.0f) : 0);
                        r1.messageLeft += i;
                    }
                    r1.drawMention = true;
                }
                if (checkMessage) {
                    if (messageString == null) {
                        messageString = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    timeString = messageString.toString();
                    if (timeString.length() <= 150) {
                        z = false;
                        timeString = timeString.substring(0, 150);
                    } else {
                        z = false;
                    }
                    messageString = Emoji.replaceEmoji(timeString.replace('\n', ' '), Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), z);
                }
                i3 = Math.max(AndroidUtilities.dp(12.0f), messageWidth);
                r1.messageLayout = new StaticLayout(TextUtils.ellipsize(messageString, currentMessagePaint, (float) (i3 - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentMessagePaint, i3, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (LocaleController.isRTL) {
                    z3 = checkMessage;
                    if (r1.nameLayout != null && r1.nameLayout.getLineCount() > 0) {
                        left = r1.nameLayout.getLineRight(0);
                        if (left == ((float) nameWidth)) {
                            widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                            if (widthpx < ((double) nameWidth)) {
                                r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) nameWidth) - widthpx));
                            }
                        }
                        if (r1.dialogMuted || r1.drawVerified) {
                            r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
                        }
                    }
                    if (r1.messageLayout != null && r1.messageLayout.getLineCount() > 0 && r1.messageLayout.getLineRight(0) == ((float) i3)) {
                        widthpx = Math.ceil((double) r1.messageLayout.getLineWidth(0));
                        if (widthpx < ((double) i3)) {
                            r1.messageLeft = (int) (((double) r1.messageLeft) - (((double) i3) - widthpx));
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (r1.nameLayout != null || r1.nameLayout.getLineCount() <= 0) {
                    z3 = checkMessage;
                } else {
                    left = r1.nameLayout.getLineLeft(0);
                    widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    if (!r1.dialogMuted || r1.drawVerified) {
                        z3 = checkMessage;
                        if (r1.drawVerified) {
                            r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) nameWidth) - widthpx)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                        }
                    } else {
                        r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) nameWidth) - widthpx)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_muteDrawable.getIntrinsicWidth()));
                    }
                    if (left == 0.0f && widthpx < ((double) nameWidth)) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) + (((double) nameWidth) - widthpx));
                    }
                }
                if (r1.messageLayout != null && r1.messageLayout.getLineCount() > 0 && r1.messageLayout.getLineLeft(0) == 0.0f) {
                    widthpx = Math.ceil((double) r1.messageLayout.getLineWidth(0));
                    if (widthpx < ((double) i3)) {
                        r1.messageLeft = (int) (((double) r1.messageLeft) + (((double) i3) - widthpx));
                        return;
                    }
                    return;
                }
                return;
            }
            i = AndroidUtilities.dp(31.0f);
            messageWidth -= i;
            if (LocaleController.isRTL) {
                r1.errorLeft = getMeasuredWidth() - AndroidUtilities.dp(34.0f);
            } else {
                r1.errorLeft = AndroidUtilities.dp(11.0f);
                r1.messageLeft += i;
            }
            if (checkMessage) {
                if (messageString == null) {
                    messageString = TtmlNode.ANONYMOUS_REGION_ID;
                }
                timeString = messageString.toString();
                if (timeString.length() <= 150) {
                    z = false;
                } else {
                    z = false;
                    timeString = timeString.substring(0, 150);
                }
                messageString = Emoji.replaceEmoji(timeString.replace('\n', ' '), Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), z);
            }
            i3 = Math.max(AndroidUtilities.dp(12.0f), messageWidth);
            r1.messageLayout = new StaticLayout(TextUtils.ellipsize(messageString, currentMessagePaint, (float) (i3 - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentMessagePaint, i3, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (LocaleController.isRTL) {
                z3 = checkMessage;
                left = r1.nameLayout.getLineRight(0);
                if (left == ((float) nameWidth)) {
                    widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    if (widthpx < ((double) nameWidth)) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) nameWidth) - widthpx));
                    }
                }
                r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
                if (r1.messageLayout != null) {
                    return;
                }
                return;
            }
            if (r1.nameLayout != null) {
            }
            z3 = checkMessage;
            if (r1.messageLayout != null) {
            }
        }
        if (!r1.dialogMuted) {
        }
        if (r1.drawVerified) {
            i = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
            nameWidth -= i;
            if (LocaleController.isRTL) {
                r1.nameLeft += i;
            }
        }
        nameWidth = Math.max(AndroidUtilities.dp(12.0f), nameWidth);
        try {
            r1.nameLayout = new StaticLayout(TextUtils.ellipsize(nameString.replace('\n', ' '), currentNamePaint, (float) (nameWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentNamePaint, nameWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        messageWidth = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
        if (LocaleController.isRTL) {
            r1.messageLeft = AndroidUtilities.dp(16.0f);
            if (AndroidUtilities.isTablet()) {
            }
            i2 = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        } else {
            r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            if (AndroidUtilities.isTablet()) {
            }
            i2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? NUM : NUM);
        }
        r1.avatarImage.setImageCoords(i2, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        if (r1.drawError) {
            if (format == null) {
                if (nameString2 != null) {
                    if (r1.drawPin) {
                        i = Theme.dialogs_pinnedDrawable.getIntrinsicWidth() + AndroidUtilities.dp(8.0f);
                        messageWidth -= i;
                        if (LocaleController.isRTL) {
                            r1.pinLeft = AndroidUtilities.dp(14.0f);
                            r1.messageLeft += i;
                        } else {
                            r1.pinLeft = (getMeasuredWidth() - Theme.dialogs_pinnedDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(14.0f);
                        }
                    }
                    r1.drawCount = false;
                    r1.drawMention = false;
                }
            }
            if (format == null) {
                z2 = drawTime;
                r1.countWidth = 0;
            } else {
                int i52 = i2;
                z2 = drawTime;
                r1.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format)));
                r1.countLayout = new StaticLayout(format, Theme.dialogs_countTextPaint, r1.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                i = r1.countWidth + AndroidUtilities.dp(18.0f);
                messageWidth -= i;
                if (LocaleController.isRTL) {
                    r1.countLeft = AndroidUtilities.dp(19.0f);
                    r1.messageLeft += i;
                } else {
                    r1.countLeft = (getMeasuredWidth() - r1.countWidth) - AndroidUtilities.dp(19.0f);
                }
                r1.drawCount = true;
            }
            if (nameString2 != null) {
                r1.mentionWidth = AndroidUtilities.dp(12.0f);
                i = r1.mentionWidth + AndroidUtilities.dp(18.0f);
                messageWidth -= i;
                if (LocaleController.isRTL) {
                    if (r1.countWidth == 0) {
                    }
                    r1.mentionLeft = AndroidUtilities.dp(19.0f) + (r1.countWidth == 0 ? r1.countWidth + AndroidUtilities.dp(18.0f) : 0);
                    r1.messageLeft += i;
                } else {
                    if (r1.countWidth == 0) {
                    }
                    r1.mentionLeft = ((getMeasuredWidth() - r1.mentionWidth) - AndroidUtilities.dp(19.0f)) - (r1.countWidth == 0 ? r1.countWidth + AndroidUtilities.dp(18.0f) : 0);
                }
                r1.drawMention = true;
            }
            if (checkMessage) {
                if (messageString == null) {
                    messageString = TtmlNode.ANONYMOUS_REGION_ID;
                }
                timeString = messageString.toString();
                if (timeString.length() <= 150) {
                    z = false;
                    timeString = timeString.substring(0, 150);
                } else {
                    z = false;
                }
                messageString = Emoji.replaceEmoji(timeString.replace('\n', ' '), Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), z);
            }
            i3 = Math.max(AndroidUtilities.dp(12.0f), messageWidth);
            r1.messageLayout = new StaticLayout(TextUtils.ellipsize(messageString, currentMessagePaint, (float) (i3 - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentMessagePaint, i3, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (LocaleController.isRTL) {
                if (r1.nameLayout != null) {
                }
                z3 = checkMessage;
                if (r1.messageLayout != null) {
                }
            }
            z3 = checkMessage;
            left = r1.nameLayout.getLineRight(0);
            if (left == ((float) nameWidth)) {
                widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                if (widthpx < ((double) nameWidth)) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) nameWidth) - widthpx));
                }
            }
            r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
            if (r1.messageLayout != null) {
                return;
            }
            return;
        }
        i = AndroidUtilities.dp(31.0f);
        messageWidth -= i;
        if (LocaleController.isRTL) {
            r1.errorLeft = AndroidUtilities.dp(11.0f);
            r1.messageLeft += i;
        } else {
            r1.errorLeft = getMeasuredWidth() - AndroidUtilities.dp(34.0f);
        }
        if (checkMessage) {
            if (messageString == null) {
                messageString = TtmlNode.ANONYMOUS_REGION_ID;
            }
            timeString = messageString.toString();
            if (timeString.length() <= 150) {
                z = false;
            } else {
                z = false;
                timeString = timeString.substring(0, 150);
            }
            messageString = Emoji.replaceEmoji(timeString.replace('\n', ' '), Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), z);
        }
        i3 = Math.max(AndroidUtilities.dp(12.0f), messageWidth);
        try {
            r1.messageLayout = new StaticLayout(TextUtils.ellipsize(messageString, currentMessagePaint, (float) (i3 - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentMessagePaint, i3, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        if (LocaleController.isRTL) {
            z3 = checkMessage;
            left = r1.nameLayout.getLineRight(0);
            if (left == ((float) nameWidth)) {
                widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                if (widthpx < ((double) nameWidth)) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) nameWidth) - widthpx));
                }
            }
            r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
            if (r1.messageLayout != null) {
                return;
            }
            return;
        }
        if (r1.nameLayout != null) {
        }
        z3 = checkMessage;
        if (r1.messageLayout != null) {
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
            TL_dialog dialog = (TL_dialog) getDialogsArray().get(this.index);
            DraftMessage newDraftMessage = DataQuery.getInstance(this.currentAccount).getDraft(this.currentDialogId);
            MessageObject newMessageObject = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
            if (this.currentDialogId != dialog.id || ((this.message != null && this.message.getId() != dialog.top_message) || ((newMessageObject != null && newMessageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != dialog.unread_count || this.mentionCount != dialog.unread_mentions_count || this.message != newMessageObject || ((this.message == null && newMessageObject != null) || newDraftMessage != this.draftMessage || this.drawPin != dialog.pinned)))) {
                this.currentDialogId = dialog.id;
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
                TL_dialog dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                if (dialog != null && mask == 0) {
                    this.message = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                    boolean z3 = this.message != null && this.message.isUnread();
                    this.lastUnreadState = z3;
                    this.unreadCount = dialog.unread_count;
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
                        TL_dialog dialog2 = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                        if (!(dialog2 == null || (this.unreadCount == dialog2.unread_count && this.mentionCount == dialog2.unread_mentions_count))) {
                            this.unreadCount = dialog2.unread_count;
                            this.mentionCount = dialog2.unread_mentions_count;
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
            if (this.isDialogCell && MessagesController.getInstance(this.currentAccount).isDialogMuted(this.currentDialogId)) {
                z = true;
            }
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
            TLObject photo = null;
            if (this.user != null) {
                this.avatarDrawable.setInfo(this.user);
                if (UserObject.isUserSelf(this.user)) {
                    this.avatarDrawable.setSavedMessages(1);
                } else if (this.user.photo != null) {
                    photo = this.user.photo.photo_small;
                }
            } else if (this.chat != null) {
                if (this.chat.photo != null) {
                    photo = this.chat.photo.photo_small;
                }
                this.avatarDrawable.setInfo(this.chat);
            }
            this.avatarImage.setImage(photo, "50_50", this.avatarDrawable, null, 0);
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
                int x;
                if (!this.drawCount) {
                    if (!this.drawMention) {
                        if (this.drawPin) {
                            BaseCell.setDrawableBounds(Theme.dialogs_pinnedDrawable, this.pinLeft, this.pinTop);
                            Theme.dialogs_pinnedDrawable.draw(canvas);
                        }
                    }
                }
                if (this.drawCount) {
                    x = this.countLeft - AndroidUtilities.dp(5.5f);
                    this.rect.set((float) x, (float) this.countTop, (float) ((this.countWidth + x) + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, this.dialogMuted ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
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

    public boolean hasOverlappingRendering() {
        return false;
    }
}
