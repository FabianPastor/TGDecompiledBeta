package org.telegram.ui.Cells;

import android.content.Context;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.PhotoViewer;

public class ChatActionCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private int customDate;
    private CharSequence customText;
    private ChatActionCellDelegate delegate;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver = new ImageReceiver(this);
    private float lastTouchX;
    private float lastTouchY;
    private URLSpan pressedLink;
    private int previousWidth;
    private int textHeight;
    private StaticLayout textLayout;
    private int textWidth;
    private int textX;
    private int textXLeft;
    private int textY;
    private boolean wasLayout;

    public interface ChatActionCellDelegate {

        public final /* synthetic */ class -CC {
            public static void $default$didClickImage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell) {
            }

            public static void $default$didLongPress(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, float f, float f2) {
            }

            public static void $default$didPressBotButton(ChatActionCellDelegate chatActionCellDelegate, MessageObject messageObject, KeyboardButton keyboardButton) {
            }

            public static void $default$didPressReplyMessage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, int i) {
            }

            public static void $default$needOpenUserProfile(ChatActionCellDelegate chatActionCellDelegate, int i) {
            }
        }

        void didClickImage(ChatActionCell chatActionCell);

        void didLongPress(ChatActionCell chatActionCell, float f, float f2);

        void didPressBotButton(MessageObject messageObject, KeyboardButton keyboardButton);

        void didPressReplyMessage(ChatActionCell chatActionCell, int i);

        void needOpenUserProfile(int i);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    public ChatActionCell(Context context) {
        super(context);
        this.imageReceiver.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setDelegate(ChatActionCellDelegate chatActionCellDelegate) {
        this.delegate = chatActionCellDelegate;
    }

    public void setCustomDate(int i, boolean z) {
        if (this.customDate != i) {
            CharSequence formatDateChat;
            if (!z) {
                formatDateChat = LocaleController.formatDateChat((long) i);
            } else if (i == NUM) {
                formatDateChat = LocaleController.getString("MessageScheduledUntilOnline", NUM);
            } else {
                formatDateChat = LocaleController.formatString("MessageScheduledOn", NUM, LocaleController.formatDateChat((long) i));
            }
            CharSequence charSequence = this.customText;
            if (charSequence == null || !TextUtils.equals(formatDateChat, charSequence)) {
                this.customDate = i;
                this.customText = formatDateChat;
                if (getMeasuredWidth() != 0) {
                    createLayout(this.customText, getMeasuredWidth());
                    invalidate();
                }
                if (this.wasLayout) {
                    buildLayout();
                } else {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$W3vgGEA8PP4iykiyHwC5GJEFtAc(this));
                }
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        if (this.currentMessageObject != messageObject || (!this.hasReplyMessage && messageObject.replyMessageObject != null)) {
            this.currentMessageObject = messageObject;
            this.hasReplyMessage = messageObject.replyMessageObject != null;
            this.previousWidth = 0;
            if (this.currentMessageObject.type == 11) {
                int i;
                Peer peer = messageObject.messageOwner.to_id;
                if (peer != null) {
                    i = peer.chat_id;
                    if (i == 0) {
                        i = peer.channel_id;
                        if (i == 0) {
                            int i2 = peer.user_id;
                            i = i2 == UserConfig.getInstance(this.currentAccount).getClientUserId() ? messageObject.messageOwner.from_id : i2;
                        }
                    }
                } else {
                    i = 0;
                }
                this.avatarDrawable.setInfo(i, null, null);
                MessageObject messageObject2 = this.currentMessageObject;
                if (messageObject2.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage(null, null, this.avatarDrawable, null, messageObject2, 0);
                } else {
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.dp(64.0f));
                    if (closestPhotoSizeWithSize != null) {
                        this.imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize, this.currentMessageObject.photoThumbsObject), "50_50", this.avatarDrawable, null, this.currentMessageObject, 0);
                    } else {
                        this.imageReceiver.setImageBitmap(this.avatarDrawable);
                    }
                }
                this.imageReceiver.setVisible(PhotoViewer.isShowingImage(this.currentMessageObject) ^ 1, false);
            } else {
                this.imageReceiver.setImageBitmap(null);
            }
            requestLayout();
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public ImageReceiver getPhotoImage() {
        return this.imageReceiver;
    }

    /* Access modifiers changed, original: protected */
    public void onLongPress() {
        ChatActionCellDelegate chatActionCellDelegate = this.delegate;
        if (chatActionCellDelegate != null) {
            chatActionCellDelegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013b  */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
        r9 = this;
        r0 = r9.currentMessageObject;
        if (r0 != 0) goto L_0x0009;
    L_0x0004:
        r10 = super.onTouchEvent(r10);
        return r10;
    L_0x0009:
        r0 = r10.getX();
        r9.lastTouchX = r0;
        r1 = r10.getY();
        r9.lastTouchY = r1;
        r2 = r10.getAction();
        r3 = 1;
        r4 = 0;
        if (r2 != 0) goto L_0x003c;
    L_0x001d:
        r2 = r9.delegate;
        if (r2 == 0) goto L_0x0077;
    L_0x0021:
        r2 = r9.currentMessageObject;
        r2 = r2.type;
        r5 = 11;
        if (r2 != r5) goto L_0x0035;
    L_0x0029:
        r2 = r9.imageReceiver;
        r2 = r2.isInsideImage(r0, r1);
        if (r2 == 0) goto L_0x0035;
    L_0x0031:
        r9.imagePressed = r3;
        r2 = 1;
        goto L_0x0036;
    L_0x0035:
        r2 = 0;
    L_0x0036:
        if (r2 == 0) goto L_0x0078;
    L_0x0038:
        r9.startCheckLongPress();
        goto L_0x0078;
    L_0x003c:
        r2 = r10.getAction();
        r5 = 2;
        if (r2 == r5) goto L_0x0046;
    L_0x0043:
        r9.cancelCheckLongPress();
    L_0x0046:
        r2 = r9.imagePressed;
        if (r2 == 0) goto L_0x0077;
    L_0x004a:
        r2 = r10.getAction();
        if (r2 != r3) goto L_0x005d;
    L_0x0050:
        r9.imagePressed = r4;
        r2 = r9.delegate;
        if (r2 == 0) goto L_0x0077;
    L_0x0056:
        r2.didClickImage(r9);
        r9.playSoundEffect(r4);
        goto L_0x0077;
    L_0x005d:
        r2 = r10.getAction();
        r6 = 3;
        if (r2 != r6) goto L_0x0067;
    L_0x0064:
        r9.imagePressed = r4;
        goto L_0x0077;
    L_0x0067:
        r2 = r10.getAction();
        if (r2 != r5) goto L_0x0077;
    L_0x006d:
        r2 = r9.imageReceiver;
        r2 = r2.isInsideImage(r0, r1);
        if (r2 != 0) goto L_0x0077;
    L_0x0075:
        r9.imagePressed = r4;
    L_0x0077:
        r2 = 0;
    L_0x0078:
        if (r2 != 0) goto L_0x0139;
    L_0x007a:
        r5 = r10.getAction();
        if (r5 == 0) goto L_0x008a;
    L_0x0080:
        r5 = r9.pressedLink;
        if (r5 == 0) goto L_0x0139;
    L_0x0084:
        r5 = r10.getAction();
        if (r5 != r3) goto L_0x0139;
    L_0x008a:
        r5 = r9.textX;
        r6 = (float) r5;
        r7 = 0;
        r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));
        if (r6 < 0) goto L_0x0137;
    L_0x0092:
        r6 = r9.textY;
        r8 = (float) r6;
        r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1));
        if (r8 < 0) goto L_0x0137;
    L_0x0099:
        r8 = r9.textWidth;
        r5 = r5 + r8;
        r5 = (float) r5;
        r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
        if (r5 > 0) goto L_0x0137;
    L_0x00a1:
        r5 = r9.textHeight;
        r5 = r5 + r6;
        r5 = (float) r5;
        r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
        if (r5 > 0) goto L_0x0137;
    L_0x00a9:
        r5 = (float) r6;
        r1 = r1 - r5;
        r5 = r9.textXLeft;
        r5 = (float) r5;
        r0 = r0 - r5;
        r5 = r9.textLayout;
        r1 = (int) r1;
        r1 = r5.getLineForVertical(r1);
        r5 = r9.textLayout;
        r5 = r5.getOffsetForHorizontal(r1, r0);
        r6 = r9.textLayout;
        r6 = r6.getLineLeft(r1);
        r8 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
        if (r8 > 0) goto L_0x0134;
    L_0x00c6:
        r8 = r9.textLayout;
        r1 = r8.getLineWidth(r1);
        r6 = r6 + r1;
        r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x0134;
    L_0x00d1:
        r0 = r9.currentMessageObject;
        r0 = r0.messageText;
        r1 = r0 instanceof android.text.Spannable;
        if (r1 == 0) goto L_0x0134;
    L_0x00d9:
        r0 = (android.text.Spannable) r0;
        r1 = android.text.style.URLSpan.class;
        r0 = r0.getSpans(r5, r5, r1);
        r0 = (android.text.style.URLSpan[]) r0;
        r1 = r0.length;
        if (r1 == 0) goto L_0x012f;
    L_0x00e6:
        r1 = r10.getAction();
        if (r1 != 0) goto L_0x00f1;
    L_0x00ec:
        r0 = r0[r4];
        r9.pressedLink = r0;
        goto L_0x0132;
    L_0x00f1:
        r1 = r0[r4];
        r5 = r9.pressedLink;
        if (r1 != r5) goto L_0x0131;
    L_0x00f7:
        r1 = r9.delegate;
        if (r1 == 0) goto L_0x0132;
    L_0x00fb:
        r0 = r0[r4];
        r0 = r0.getURL();
        r1 = "game";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0115;
    L_0x0109:
        r0 = r9.delegate;
        r1 = r9.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.reply_to_msg_id;
        r0.didPressReplyMessage(r9, r1);
        goto L_0x0132;
    L_0x0115:
        r1 = "http";
        r1 = r0.startsWith(r1);
        if (r1 == 0) goto L_0x0125;
    L_0x011d:
        r1 = r9.getContext();
        org.telegram.messenger.browser.Browser.openUrl(r1, r0);
        goto L_0x0132;
    L_0x0125:
        r1 = r9.delegate;
        r0 = java.lang.Integer.parseInt(r0);
        r1.needOpenUserProfile(r0);
        goto L_0x0132;
    L_0x012f:
        r9.pressedLink = r7;
    L_0x0131:
        r3 = r2;
    L_0x0132:
        r2 = r3;
        goto L_0x0139;
    L_0x0134:
        r9.pressedLink = r7;
        goto L_0x0139;
    L_0x0137:
        r9.pressedLink = r7;
    L_0x0139:
        if (r2 != 0) goto L_0x013f;
    L_0x013b:
        r2 = super.onTouchEvent(r10);
    L_0x013f:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void createLayout(CharSequence charSequence, int i) {
        int dp = i - AndroidUtilities.dp(30.0f);
        this.textLayout = new StaticLayout(charSequence, Theme.chat_actionTextPaint, dp, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        int i2 = 0;
        this.textHeight = 0;
        this.textWidth = 0;
        try {
            int lineCount = this.textLayout.getLineCount();
            while (i2 < lineCount) {
                try {
                    float lineWidth = this.textLayout.getLineWidth(i2);
                    float f = (float) dp;
                    if (lineWidth > f) {
                        lineWidth = f;
                    }
                    this.textHeight = (int) Math.max((double) this.textHeight, Math.ceil((double) this.textLayout.getLineBottom(i2)));
                    this.textWidth = (int) Math.max((double) this.textWidth, Math.ceil((double) lineWidth));
                    i2++;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        this.textX = (i - this.textWidth) / 2;
        this.textY = AndroidUtilities.dp(7.0f);
        this.textXLeft = (i - this.textLayout.getWidth()) / 2;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        if (this.currentMessageObject == null && this.customText == null) {
            setMeasuredDimension(MeasureSpec.getSize(i), this.textHeight + AndroidUtilities.dp(14.0f));
            return;
        }
        i = Math.max(AndroidUtilities.dp(30.0f), MeasureSpec.getSize(i));
        if (this.previousWidth != i) {
            this.wasLayout = true;
            this.previousWidth = i;
            buildLayout();
        }
        i2 = this.textHeight;
        MessageObject messageObject = this.currentMessageObject;
        int i3 = (messageObject == null || messageObject.type != 11) ? 0 : 70;
        setMeasuredDimension(i, i2 + AndroidUtilities.dp((float) (14 + i3)));
    }

    private void buildLayout() {
        CharSequence string;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            Message message = messageObject.messageOwner;
            if (message != null) {
                MessageMedia messageMedia = message.media;
                if (!(messageMedia == null || messageMedia.ttl_seconds == 0)) {
                    if (messageMedia.photo instanceof TL_photoEmpty) {
                        string = LocaleController.getString("AttachPhotoExpired", NUM);
                    } else if (messageMedia.document instanceof TL_documentEmpty) {
                        string = LocaleController.getString("AttachVideoExpired", NUM);
                    } else {
                        string = messageObject.messageText;
                    }
                }
            }
            string = this.currentMessageObject.messageText;
        } else {
            string = this.customText;
        }
        createLayout(string, this.previousWidth);
        messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            this.imageReceiver.setImageCoords((this.previousWidth - AndroidUtilities.dp(64.0f)) / 2, this.textHeight + AndroidUtilities.dp(15.0f), AndroidUtilities.dp(64.0f), AndroidUtilities.dp(64.0f));
        }
    }

    public int getCustomDate() {
        return this.customDate;
    }

    private int findMaxWidthAroundLine(int i) {
        int ceil = (int) Math.ceil((double) this.textLayout.getLineWidth(i));
        int lineCount = this.textLayout.getLineCount();
        for (int i2 = i + 1; i2 < lineCount; i2++) {
            int ceil2 = (int) Math.ceil((double) this.textLayout.getLineWidth(i2));
            if (Math.abs(ceil2 - ceil) >= AndroidUtilities.dp(10.0f)) {
                break;
            }
            ceil = Math.max(ceil2, ceil);
        }
        for (i--; i >= 0; i--) {
            lineCount = (int) Math.ceil((double) this.textLayout.getLineWidth(i));
            if (Math.abs(lineCount - ceil) >= AndroidUtilities.dp(10.0f)) {
                break;
            }
            ceil = Math.max(lineCount, ceil);
        }
        return ceil;
    }

    private boolean isLineTop(int i, int i2, int i3, int i4, int i5) {
        if (i3 != 0) {
            return i3 >= 0 && i3 < i4 && findMaxWidthAroundLine(i3 - 1) + (i5 * 3) < i;
        } else {
            return true;
        }
    }

    private boolean isLineBottom(int i, int i2, int i3, int i4, int i5) {
        i4--;
        if (i3 != i4) {
            return i3 >= 0 && i3 <= i4 && findMaxWidthAroundLine(i3 + 1) + (i5 * 3) < i;
        } else {
            return true;
        }
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0239  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x03af  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x024d  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x04ae  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x04da A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x04b1  */
    public void onDraw(android.graphics.Canvas r34) {
        /*
        r33 = this;
        r6 = r33;
        r7 = r34;
        r0 = r6.currentMessageObject;
        if (r0 == 0) goto L_0x0013;
    L_0x0008:
        r0 = r0.type;
        r1 = 11;
        if (r0 != r1) goto L_0x0013;
    L_0x000e:
        r0 = r6.imageReceiver;
        r0.draw(r7);
    L_0x0013:
        r0 = r6.textLayout;
        if (r0 == 0) goto L_0x0506;
    L_0x0017:
        r8 = r0.getLineCount();
        r9 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r11 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r13 = r10 - r12;
        r0 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1 = r0;
        r0 = 0;
        r4 = 0;
        r5 = 0;
    L_0x0039:
        if (r5 >= r8) goto L_0x04ef;
    L_0x003b:
        r2 = r6.findMaxWidthAroundLine(r5);
        r3 = r33.getMeasuredWidth();
        r3 = r3 - r2;
        r3 = r3 - r13;
        r15 = 2;
        r3 = r3 / r15;
        r2 = r2 + r13;
        r11 = r6.textLayout;
        r11 = r11.getLineBottom(r5);
        r0 = r11 - r0;
        r9 = r8 + -1;
        if (r5 != r9) goto L_0x0056;
    L_0x0054:
        r9 = 1;
        goto L_0x0057;
    L_0x0056:
        r9 = 0;
    L_0x0057:
        if (r5 != 0) goto L_0x005c;
    L_0x0059:
        r19 = 1;
        goto L_0x005e;
    L_0x005c:
        r19 = 0;
    L_0x005e:
        r20 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        if (r19 == 0) goto L_0x006e;
    L_0x0062:
        r21 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r1 = r1 - r21;
        r21 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r21;
    L_0x006e:
        if (r9 == 0) goto L_0x0076;
    L_0x0070:
        r21 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r21;
    L_0x0076:
        r21 = r0;
        if (r9 != 0) goto L_0x009c;
    L_0x007a:
        r0 = r5 + 1;
        if (r0 >= r8) goto L_0x009c;
    L_0x007e:
        r0 = r6.findMaxWidthAroundLine(r0);
        r0 = r0 + r13;
        r22 = r13 * 2;
        r15 = r0 + r22;
        if (r15 >= r2) goto L_0x008e;
    L_0x0089:
        r15 = r0;
        r9 = 1;
        r22 = 1;
        goto L_0x00a0;
    L_0x008e:
        r15 = r2 + r22;
        if (r15 >= r0) goto L_0x0097;
    L_0x0092:
        r15 = r0;
        r22 = r9;
        r9 = 2;
        goto L_0x00a0;
    L_0x0097:
        r15 = r0;
        r22 = r9;
        r9 = 3;
        goto L_0x00a0;
    L_0x009c:
        r22 = r9;
        r9 = 0;
        r15 = 0;
    L_0x00a0:
        if (r19 != 0) goto L_0x00c5;
    L_0x00a2:
        if (r5 <= 0) goto L_0x00c5;
    L_0x00a4:
        r0 = r5 + -1;
        r0 = r6.findMaxWidthAroundLine(r0);
        r0 = r0 + r13;
        r23 = r13 * 2;
        r24 = r1;
        r1 = r0 + r23;
        if (r1 >= r2) goto L_0x00b9;
    L_0x00b3:
        r23 = r0;
        r1 = 1;
        r19 = 1;
        goto L_0x00ca;
    L_0x00b9:
        r1 = r2 + r23;
        if (r1 >= r0) goto L_0x00c1;
    L_0x00bd:
        r23 = r0;
        r1 = 2;
        goto L_0x00ca;
    L_0x00c1:
        r23 = r0;
        r1 = 3;
        goto L_0x00ca;
    L_0x00c5:
        r24 = r1;
        r1 = 0;
        r23 = 0;
    L_0x00ca:
        if (r9 == 0) goto L_0x0239;
    L_0x00cc:
        r0 = 1;
        if (r9 != r0) goto L_0x0176;
    L_0x00cf:
        r0 = r33.getMeasuredWidth();
        r0 = r0 - r15;
        r18 = 2;
        r0 = r0 / 2;
        r25 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r26 = r5 + 1;
        r27 = r11;
        r6 = 3;
        r11 = r0;
        r0 = r33;
        r7 = r1;
        r6 = r24;
        r1 = r15;
        r24 = r2;
        r28 = r10;
        r10 = r3;
        r3 = r26;
        r29 = r4;
        r4 = r8;
        r26 = r5;
        r5 = r13;
        r0 = r0.isLineBottom(r1, r2, r3, r4, r5);
        if (r0 == 0) goto L_0x0135;
    L_0x00fb:
        r3 = r10 + r12;
        r1 = (float) r3;
        r5 = r6 + r21;
        r4 = (float) r5;
        r0 = r11 - r13;
        r3 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r5;
        r2 = (float) r0;
        r30 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r31 = r2;
        r2 = r4;
        r32 = r4;
        r4 = r31;
        r31 = r5;
        r5 = r30;
        r0.drawRect(r1, r2, r3, r4, r5);
        r0 = r11 + r15;
        r0 = r0 + r13;
        r1 = (float) r0;
        r3 = r10 + r24;
        r3 = r3 - r12;
        r3 = (float) r3;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r5 = r31 + r0;
        r4 = (float) r5;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r2 = r32;
        r0.drawRect(r1, r2, r3, r4, r5);
        goto L_0x016b;
    L_0x0135:
        r3 = r10 + r12;
        r1 = (float) r3;
        r5 = r6 + r21;
        r4 = (float) r5;
        r3 = (float) r11;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r5;
        r2 = (float) r0;
        r30 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r31 = r2;
        r2 = r4;
        r32 = r4;
        r4 = r31;
        r31 = r5;
        r5 = r30;
        r0.drawRect(r1, r2, r3, r4, r5);
        r0 = r11 + r15;
        r1 = (float) r0;
        r3 = r10 + r24;
        r3 = r3 - r12;
        r3 = (float) r3;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r5 = r31 + r0;
        r4 = (float) r5;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r2 = r32;
        r0.drawRect(r1, r2, r3, r4, r5);
    L_0x016b:
        r31 = r10;
        r15 = r25;
        r10 = r7;
        r25 = r9;
        r7 = r34;
        goto L_0x024b;
    L_0x0176:
        r7 = r1;
        r29 = r4;
        r26 = r5;
        r28 = r10;
        r27 = r11;
        r6 = r24;
        r0 = 2;
        r24 = r2;
        r10 = r3;
        if (r9 != r0) goto L_0x022b;
    L_0x0187:
        r15 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r1 = r6 + r21;
        r2 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r11 = r1 - r3;
        r3 = r10 - r14;
        if (r7 == r0) goto L_0x019d;
    L_0x0199:
        r0 = 3;
        if (r7 == r0) goto L_0x019d;
    L_0x019c:
        r3 = r3 - r13;
    L_0x019d:
        r5 = r3;
        if (r19 != 0) goto L_0x01a7;
    L_0x01a0:
        if (r22 == 0) goto L_0x01a3;
    L_0x01a2:
        goto L_0x01a7;
    L_0x01a3:
        r30 = r7;
        r7 = r5;
        goto L_0x01c2;
    L_0x01a7:
        r0 = r5 + r14;
        r1 = (float) r0;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r2 = r2 + r11;
        r2 = (float) r2;
        r0 = r0 + r28;
        r3 = (float) r0;
        r0 = r11 + r28;
        r4 = (float) r0;
        r25 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r30 = r7;
        r7 = r5;
        r5 = r25;
        r0.drawRect(r1, r2, r3, r4, r5);
    L_0x01c2:
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner;
        r1 = 2;
        r0 = r0[r1];
        r5 = r7 + r14;
        r4 = r11 + r14;
        r0.setBounds(r7, r11, r5, r4);
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner;
        r0 = r0[r1];
        r7 = r34;
        r5 = r30;
        r0.draw(r7);
        r3 = r10 + r24;
        if (r5 == r1) goto L_0x01e1;
    L_0x01dd:
        r0 = 3;
        if (r5 == r0) goto L_0x01e1;
    L_0x01e0:
        r3 = r3 + r13;
    L_0x01e1:
        if (r19 != 0) goto L_0x01f0;
    L_0x01e3:
        if (r22 == 0) goto L_0x01e6;
    L_0x01e5:
        goto L_0x01f0;
    L_0x01e6:
        r25 = r9;
        r31 = r10;
        r32 = r15;
        r15 = r3;
        r9 = r4;
        r10 = r5;
        goto L_0x0217;
    L_0x01f0:
        r0 = r3 - r28;
        r1 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r11;
        r2 = (float) r0;
        r0 = (float) r3;
        r25 = r0;
        r0 = r11 + r28;
        r0 = (float) r0;
        r30 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r31 = r0;
        r0 = r34;
        r32 = r15;
        r15 = r3;
        r3 = r25;
        r25 = r9;
        r9 = r4;
        r4 = r31;
        r31 = r10;
        r10 = r5;
        r5 = r30;
        r0.drawRect(r1, r2, r3, r4, r5);
    L_0x0217:
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner;
        r1 = 3;
        r0 = r0[r1];
        r3 = r15 + r14;
        r0.setBounds(r15, r11, r3, r9);
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner;
        r0 = r0[r1];
        r0.draw(r7);
        r15 = r32;
        goto L_0x024b;
    L_0x022b:
        r25 = r9;
        r31 = r10;
        r0 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r10 = r7;
        r7 = r34;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r0);
        goto L_0x024b;
    L_0x0239:
        r31 = r3;
        r29 = r4;
        r26 = r5;
        r25 = r9;
        r28 = r10;
        r27 = r11;
        r6 = r24;
        r10 = r1;
        r24 = r2;
        r15 = 0;
    L_0x024b:
        if (r10 == 0) goto L_0x03af;
    L_0x024d:
        r0 = 1;
        if (r10 != r0) goto L_0x02de;
    L_0x0250:
        r0 = r33.getMeasuredWidth();
        r0 = r0 - r23;
        r1 = 2;
        r9 = r0 / 2;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r11 = r6 - r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r29 = r21 + r0;
        r3 = r26 + -1;
        r0 = r33;
        r1 = r23;
        r2 = r24;
        r4 = r8;
        r5 = r13;
        r0 = r0.isLineTop(r1, r2, r3, r4, r5);
        if (r0 == 0) goto L_0x02a6;
    L_0x0275:
        r3 = r31 + r12;
        r1 = (float) r3;
        r5 = (float) r11;
        r0 = r9 - r13;
        r3 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r11;
        r4 = (float) r0;
        r30 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r2 = r5;
        r32 = r5;
        r5 = r30;
        r0.drawRect(r1, r2, r3, r4, r5);
        r9 = r9 + r23;
        r9 = r9 + r13;
        r1 = (float) r9;
        r3 = r31 + r24;
        r3 = r3 - r12;
        r3 = (float) r3;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r11;
        r4 = (float) r0;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r2 = r32;
        r0.drawRect(r1, r2, r3, r4, r5);
        goto L_0x02d3;
    L_0x02a6:
        r3 = r31 + r12;
        r1 = (float) r3;
        r5 = (float) r11;
        r3 = (float) r9;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r11;
        r4 = (float) r0;
        r30 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r2 = r5;
        r32 = r5;
        r5 = r30;
        r0.drawRect(r1, r2, r3, r4, r5);
        r9 = r9 + r23;
        r1 = (float) r9;
        r3 = r31 + r24;
        r3 = r3 - r12;
        r3 = (float) r3;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r11;
        r4 = (float) r0;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r2 = r32;
        r0.drawRect(r1, r2, r3, r4, r5);
    L_0x02d3:
        r30 = r8;
        r23 = r10;
        r8 = r11;
        r11 = r25;
        r17 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        goto L_0x03ba;
    L_0x02de:
        r0 = 2;
        if (r10 != r0) goto L_0x0395;
    L_0x02e1:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r9 = r6 - r1;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r11 = r21 + r1;
        r3 = r31 - r14;
        r5 = r25;
        if (r5 == r0) goto L_0x02f7;
    L_0x02f3:
        r0 = 3;
        if (r5 == r0) goto L_0x02f7;
    L_0x02f6:
        r3 = r3 - r13;
    L_0x02f7:
        r4 = r3;
        if (r19 != 0) goto L_0x0304;
    L_0x02fa:
        if (r22 == 0) goto L_0x02fd;
    L_0x02fc:
        goto L_0x0304;
    L_0x02fd:
        r30 = r8;
        r25 = r11;
        r8 = r4;
        r11 = r5;
        goto L_0x032c;
    L_0x0304:
        r0 = r4 + r14;
        r1 = (float) r0;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r2 = r2 + r9;
        r2 = (float) r2;
        r0 = r0 + r28;
        r3 = (float) r0;
        r0 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r23 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = r9 + r23;
        r0 = (float) r0;
        r23 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r25 = r0;
        r0 = r34;
        r30 = r8;
        r8 = r4;
        r4 = r25;
        r25 = r11;
        r11 = r5;
        r5 = r23;
        r0.drawRect(r1, r2, r3, r4, r5);
    L_0x032c:
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner;
        r1 = 0;
        r0 = r0[r1];
        r4 = r8 + r14;
        r5 = r29;
        r3 = r5 + r14;
        r0.setBounds(r8, r5, r4, r3);
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner;
        r0 = r0[r1];
        r0.draw(r7);
        r0 = r31 + r24;
        r1 = 2;
        if (r11 == r1) goto L_0x034a;
    L_0x0346:
        r1 = 3;
        if (r11 == r1) goto L_0x034a;
    L_0x0349:
        r0 = r0 + r13;
    L_0x034a:
        r8 = r0;
        if (r19 != 0) goto L_0x0359;
    L_0x034d:
        if (r22 == 0) goto L_0x0350;
    L_0x034f:
        goto L_0x0359;
    L_0x0350:
        r29 = r9;
        r23 = r10;
        r17 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r9 = r3;
        r10 = r5;
        goto L_0x037f;
    L_0x0359:
        r0 = r8 - r28;
        r1 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r9;
        r2 = (float) r0;
        r4 = (float) r8;
        r17 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 + r9;
        r0 = (float) r0;
        r20 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r23 = r0;
        r0 = r34;
        r29 = r9;
        r9 = r3;
        r3 = r4;
        r4 = r23;
        r23 = r10;
        r10 = r5;
        r5 = r20;
        r0.drawRect(r1, r2, r3, r4, r5);
    L_0x037f:
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner;
        r1 = 1;
        r0 = r0[r1];
        r2 = r8 + r14;
        r0.setBounds(r8, r10, r2, r9);
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner;
        r0 = r0[r1];
        r0.draw(r7);
        r8 = r29;
        r29 = r25;
        goto L_0x03ba;
    L_0x0395:
        r30 = r8;
        r23 = r10;
        r11 = r25;
        r0 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r17 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1 = r6 - r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = r21 + r2;
        r29 = r0;
        r8 = r1;
        goto L_0x03ba;
    L_0x03af:
        r30 = r8;
        r23 = r10;
        r11 = r25;
        r17 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r8 = r6;
        r29 = r21;
    L_0x03ba:
        if (r19 != 0) goto L_0x03d1;
    L_0x03bc:
        if (r22 == 0) goto L_0x03bf;
    L_0x03be:
        goto L_0x03d1;
    L_0x03bf:
        r9 = r31;
        r1 = (float) r9;
        r2 = (float) r6;
        r3 = r9 + r24;
        r3 = (float) r3;
        r0 = r6 + r21;
        r4 = (float) r0;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r0.drawRect(r1, r2, r3, r4, r5);
        goto L_0x03e5;
    L_0x03d1:
        r9 = r31;
        r3 = r9 + r12;
        r1 = (float) r3;
        r2 = (float) r6;
        r3 = r9 + r24;
        r3 = r3 - r12;
        r3 = (float) r3;
        r0 = r6 + r21;
        r4 = (float) r0;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r0.drawRect(r1, r2, r3, r4, r5);
    L_0x03e5:
        r6 = r9 - r13;
        r3 = r9 + r24;
        r9 = r3 - r12;
        if (r19 == 0) goto L_0x0422;
    L_0x03ed:
        if (r22 != 0) goto L_0x0422;
    L_0x03ef:
        r0 = 2;
        if (r11 == r0) goto L_0x0422;
    L_0x03f2:
        r1 = (float) r6;
        r10 = r8 + r28;
        r10 = (float) r10;
        r0 = r6 + r28;
        r3 = (float) r0;
        r0 = r8 + r29;
        r11 = r0 + r15;
        r0 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = r11 - r2;
        r4 = (float) r0;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r2 = r10;
        r0.drawRect(r1, r2, r3, r4, r5);
        r1 = (float) r9;
        r0 = r9 + r28;
        r3 = (float) r0;
        r16 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r11 = r11 - r0;
        r4 = (float) r11;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r0.drawRect(r1, r2, r3, r4, r5);
        goto L_0x0487;
    L_0x0422:
        r16 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r22 == 0) goto L_0x0462;
    L_0x0426:
        if (r19 != 0) goto L_0x0462;
    L_0x0428:
        r0 = r23;
        r1 = 2;
        if (r0 == r1) goto L_0x0462;
    L_0x042d:
        r1 = (float) r6;
        r10 = r8 + r28;
        r11 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r0 = r10 - r0;
        r2 = (float) r0;
        r0 = r6 + r28;
        r3 = (float) r0;
        r0 = r8 + r29;
        r0 = r0 + r15;
        r0 = r0 - r28;
        r5 = (float) r0;
        r20 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r4 = r5;
        r21 = r5;
        r5 = r20;
        r0.drawRect(r1, r2, r3, r4, r5);
        r1 = (float) r9;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10 = r10 - r0;
        r2 = (float) r10;
        r10 = r9 + r28;
        r3 = (float) r10;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r4 = r21;
        r0.drawRect(r1, r2, r3, r4, r5);
        goto L_0x0487;
    L_0x0462:
        if (r19 != 0) goto L_0x0466;
    L_0x0464:
        if (r22 == 0) goto L_0x0487;
    L_0x0466:
        r1 = (float) r6;
        r10 = r8 + r28;
        r10 = (float) r10;
        r0 = r6 + r28;
        r3 = (float) r0;
        r0 = r8 + r29;
        r0 = r0 + r15;
        r0 = r0 - r28;
        r11 = (float) r0;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r2 = r10;
        r4 = r11;
        r0.drawRect(r1, r2, r3, r4, r5);
        r1 = (float) r9;
        r0 = r9 + r28;
        r3 = (float) r0;
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r0 = r34;
        r0.drawRect(r1, r2, r3, r4, r5);
    L_0x0487:
        if (r19 == 0) goto L_0x04ae;
    L_0x0489:
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter;
        r1 = 0;
        r0 = r0[r1];
        r10 = r6 + r28;
        r2 = r8 + r28;
        r0.setBounds(r6, r8, r10, r2);
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter;
        r0 = r0[r1];
        r0.draw(r7);
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter;
        r3 = 1;
        r0 = r0[r3];
        r10 = r9 + r28;
        r0.setBounds(r9, r8, r10, r2);
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter;
        r0 = r0[r3];
        r0.draw(r7);
        goto L_0x04af;
    L_0x04ae:
        r1 = 0;
    L_0x04af:
        if (r22 == 0) goto L_0x04da;
    L_0x04b1:
        r0 = r8 + r29;
        r0 = r0 + r15;
        r0 = r0 - r28;
        r2 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter;
        r3 = 2;
        r2 = r2[r3];
        r10 = r9 + r28;
        r4 = r0 + r28;
        r2.setBounds(r9, r0, r10, r4);
        r2 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter;
        r2 = r2[r3];
        r2.draw(r7);
        r2 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter;
        r3 = 3;
        r2 = r2[r3];
        r10 = r6 + r28;
        r2.setBounds(r6, r0, r10, r4);
        r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter;
        r0 = r0[r3];
        r0.draw(r7);
    L_0x04da:
        r0 = r8 + r29;
        r4 = r0 + r15;
        r5 = r26 + 1;
        r1 = r0;
        r0 = r27;
        r10 = r28;
        r8 = r30;
        r9 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r11 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = r33;
        goto L_0x0039;
    L_0x04ef:
        r34.save();
        r0 = r33;
        r1 = r0.textXLeft;
        r1 = (float) r1;
        r2 = r0.textY;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.textLayout;
        r1.draw(r7);
        r34.restore();
        goto L_0x0507;
    L_0x0506:
        r0 = r6;
    L_0x0507:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onDraw(android.graphics.Canvas):void");
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (!TextUtils.isEmpty(this.customText) || this.currentMessageObject != null) {
            accessibilityNodeInfo.setText(!TextUtils.isEmpty(this.customText) ? this.customText : this.currentMessageObject.messageText);
            accessibilityNodeInfo.setEnabled(true);
        }
    }
}
