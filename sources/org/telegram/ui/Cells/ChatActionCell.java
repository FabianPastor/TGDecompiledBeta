package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
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

        /* renamed from: org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didClickImage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell) {
            }

            public static void $default$didLongPress(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, float f, float f2) {
            }

            public static void $default$didPressBotButton(ChatActionCellDelegate chatActionCellDelegate, MessageObject messageObject, TLRPC.KeyboardButton keyboardButton) {
            }

            public static void $default$didPressReplyMessage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, int i) {
            }

            public static void $default$needOpenUserProfile(ChatActionCellDelegate chatActionCellDelegate, int i) {
            }
        }

        void didClickImage(ChatActionCell chatActionCell);

        void didLongPress(ChatActionCell chatActionCell, float f, float f2);

        void didPressBotButton(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton);

        void didPressReplyMessage(ChatActionCell chatActionCell, int i);

        void needOpenUserProfile(int i);
    }

    /* access modifiers changed from: protected */
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

    public void setCustomDate(int i, boolean z, boolean z2) {
        String str;
        if (this.customDate != i) {
            if (!z) {
                str = LocaleController.formatDateChat((long) i);
            } else if (i == NUM) {
                str = LocaleController.getString("MessageScheduledUntilOnline", NUM);
            } else {
                str = LocaleController.formatString("MessageScheduledOn", NUM, LocaleController.formatDateChat((long) i));
            }
            CharSequence charSequence = this.customText;
            if (charSequence == null || !TextUtils.equals(str, charSequence)) {
                this.customDate = i;
                this.customText = str;
                if (getMeasuredWidth() != 0) {
                    createLayout(this.customText, getMeasuredWidth());
                    invalidate();
                }
                if (this.wasLayout) {
                    buildLayout();
                } else if (z2) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            ChatActionCell.this.requestLayout();
                        }
                    });
                } else {
                    requestLayout();
                }
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        int i;
        if (this.currentMessageObject != messageObject || (!this.hasReplyMessage && messageObject.replyMessageObject != null)) {
            this.currentMessageObject = messageObject;
            this.hasReplyMessage = messageObject.replyMessageObject != null;
            this.previousWidth = 0;
            if (this.currentMessageObject.type == 11) {
                TLRPC.Peer peer = messageObject.messageOwner.to_id;
                if (peer != null) {
                    i = peer.chat_id;
                    if (i == 0 && (i = peer.channel_id) == 0) {
                        int i2 = peer.user_id;
                        i = i2 == UserConfig.getInstance(this.currentAccount).getClientUserId() ? messageObject.messageOwner.from_id : i2;
                    }
                } else {
                    i = 0;
                }
                this.avatarDrawable.setInfo(i, (String) null, (String) null);
                MessageObject messageObject2 = this.currentMessageObject;
                if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, messageObject2, 0);
                } else {
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.dp(64.0f));
                    if (closestPhotoSizeWithSize != null) {
                        this.imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize, this.currentMessageObject.photoThumbsObject), "50_50", this.avatarDrawable, (String) null, this.currentMessageObject, 0);
                    } else {
                        this.imageReceiver.setImageBitmap((Drawable) this.avatarDrawable);
                    }
                }
                this.imageReceiver.setVisible(!PhotoViewer.isShowingImage(this.currentMessageObject), false);
            } else {
                this.imageReceiver.setImageBitmap((Bitmap) null);
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

    /* access modifiers changed from: protected */
    public void onLongPress() {
        ChatActionCellDelegate chatActionCellDelegate = this.delegate;
        if (chatActionCellDelegate != null) {
            chatActionCellDelegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:80:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
            r9 = this;
            org.telegram.messenger.MessageObject r0 = r9.currentMessageObject
            if (r0 != 0) goto L_0x0009
            boolean r10 = super.onTouchEvent(r10)
            return r10
        L_0x0009:
            float r0 = r10.getX()
            r9.lastTouchX = r0
            float r1 = r10.getY()
            r9.lastTouchY = r1
            int r2 = r10.getAction()
            r3 = 1
            r4 = 0
            if (r2 != 0) goto L_0x003c
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r2 = r9.delegate
            if (r2 == 0) goto L_0x0077
            org.telegram.messenger.MessageObject r2 = r9.currentMessageObject
            int r2 = r2.type
            r5 = 11
            if (r2 != r5) goto L_0x0035
            org.telegram.messenger.ImageReceiver r2 = r9.imageReceiver
            boolean r2 = r2.isInsideImage(r0, r1)
            if (r2 == 0) goto L_0x0035
            r9.imagePressed = r3
            r2 = 1
            goto L_0x0036
        L_0x0035:
            r2 = 0
        L_0x0036:
            if (r2 == 0) goto L_0x0078
            r9.startCheckLongPress()
            goto L_0x0078
        L_0x003c:
            int r2 = r10.getAction()
            r5 = 2
            if (r2 == r5) goto L_0x0046
            r9.cancelCheckLongPress()
        L_0x0046:
            boolean r2 = r9.imagePressed
            if (r2 == 0) goto L_0x0077
            int r2 = r10.getAction()
            if (r2 != r3) goto L_0x005d
            r9.imagePressed = r4
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r2 = r9.delegate
            if (r2 == 0) goto L_0x0077
            r2.didClickImage(r9)
            r9.playSoundEffect(r4)
            goto L_0x0077
        L_0x005d:
            int r2 = r10.getAction()
            r6 = 3
            if (r2 != r6) goto L_0x0067
            r9.imagePressed = r4
            goto L_0x0077
        L_0x0067:
            int r2 = r10.getAction()
            if (r2 != r5) goto L_0x0077
            org.telegram.messenger.ImageReceiver r2 = r9.imageReceiver
            boolean r2 = r2.isInsideImage(r0, r1)
            if (r2 != 0) goto L_0x0077
            r9.imagePressed = r4
        L_0x0077:
            r2 = 0
        L_0x0078:
            if (r2 != 0) goto L_0x0139
            int r5 = r10.getAction()
            if (r5 == 0) goto L_0x008a
            android.text.style.URLSpan r5 = r9.pressedLink
            if (r5 == 0) goto L_0x0139
            int r5 = r10.getAction()
            if (r5 != r3) goto L_0x0139
        L_0x008a:
            int r5 = r9.textX
            float r6 = (float) r5
            r7 = 0
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 < 0) goto L_0x0137
            int r6 = r9.textY
            float r8 = (float) r6
            int r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x0137
            int r8 = r9.textWidth
            int r5 = r5 + r8
            float r5 = (float) r5
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 > 0) goto L_0x0137
            int r5 = r9.textHeight
            int r5 = r5 + r6
            float r5 = (float) r5
            int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r5 > 0) goto L_0x0137
            float r5 = (float) r6
            float r1 = r1 - r5
            int r5 = r9.textXLeft
            float r5 = (float) r5
            float r0 = r0 - r5
            android.text.StaticLayout r5 = r9.textLayout
            int r1 = (int) r1
            int r1 = r5.getLineForVertical(r1)
            android.text.StaticLayout r5 = r9.textLayout
            int r5 = r5.getOffsetForHorizontal(r1, r0)
            android.text.StaticLayout r6 = r9.textLayout
            float r6 = r6.getLineLeft(r1)
            int r8 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r8 > 0) goto L_0x0134
            android.text.StaticLayout r8 = r9.textLayout
            float r1 = r8.getLineWidth(r1)
            float r6 = r6 + r1
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x0134
            org.telegram.messenger.MessageObject r0 = r9.currentMessageObject
            java.lang.CharSequence r0 = r0.messageText
            boolean r1 = r0 instanceof android.text.Spannable
            if (r1 == 0) goto L_0x0134
            android.text.Spannable r0 = (android.text.Spannable) r0
            java.lang.Class<android.text.style.URLSpan> r1 = android.text.style.URLSpan.class
            java.lang.Object[] r0 = r0.getSpans(r5, r5, r1)
            android.text.style.URLSpan[] r0 = (android.text.style.URLSpan[]) r0
            int r1 = r0.length
            if (r1 == 0) goto L_0x012f
            int r1 = r10.getAction()
            if (r1 != 0) goto L_0x00f1
            r0 = r0[r4]
            r9.pressedLink = r0
            goto L_0x0132
        L_0x00f1:
            r1 = r0[r4]
            android.text.style.URLSpan r5 = r9.pressedLink
            if (r1 != r5) goto L_0x0131
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r1 = r9.delegate
            if (r1 == 0) goto L_0x0132
            r0 = r0[r4]
            java.lang.String r0 = r0.getURL()
            java.lang.String r1 = "game"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0115
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r0 = r9.delegate
            org.telegram.messenger.MessageObject r1 = r9.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.reply_to_msg_id
            r0.didPressReplyMessage(r9, r1)
            goto L_0x0132
        L_0x0115:
            java.lang.String r1 = "http"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0125
            android.content.Context r1 = r9.getContext()
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r1, (java.lang.String) r0)
            goto L_0x0132
        L_0x0125:
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r1 = r9.delegate
            int r0 = java.lang.Integer.parseInt(r0)
            r1.needOpenUserProfile(r0)
            goto L_0x0132
        L_0x012f:
            r9.pressedLink = r7
        L_0x0131:
            r3 = r2
        L_0x0132:
            r2 = r3
            goto L_0x0139
        L_0x0134:
            r9.pressedLink = r7
            goto L_0x0139
        L_0x0137:
            r9.pressedLink = r7
        L_0x0139:
            if (r2 != 0) goto L_0x013f
            boolean r2 = super.onTouchEvent(r10)
        L_0x013f:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void createLayout(CharSequence charSequence, int i) {
        int dp = i - AndroidUtilities.dp(30.0f);
        this.textLayout = new StaticLayout(charSequence, Theme.chat_actionTextPaint, dp, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
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
                    FileLog.e((Throwable) e);
                    return;
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        this.textX = (i - this.textWidth) / 2;
        this.textY = AndroidUtilities.dp(7.0f);
        this.textXLeft = (i - this.textLayout.getWidth()) / 2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.currentMessageObject == null && this.customText == null) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), this.textHeight + AndroidUtilities.dp(14.0f));
            return;
        }
        int max = Math.max(AndroidUtilities.dp(30.0f), View.MeasureSpec.getSize(i));
        if (this.previousWidth != max) {
            this.wasLayout = true;
            this.previousWidth = max;
            buildLayout();
        }
        int i3 = this.textHeight;
        MessageObject messageObject = this.currentMessageObject;
        setMeasuredDimension(max, i3 + AndroidUtilities.dp((float) (14 + ((messageObject == null || messageObject.type != 11) ? 0 : 70))));
    }

    private void buildLayout() {
        CharSequence charSequence;
        TLRPC.MessageMedia messageMedia;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            if (message == null || (messageMedia = message.media) == null || messageMedia.ttl_seconds == 0) {
                charSequence = this.currentMessageObject.messageText;
            } else if (messageMedia.photo instanceof TLRPC.TL_photoEmpty) {
                charSequence = LocaleController.getString("AttachPhotoExpired", NUM);
            } else if (messageMedia.document instanceof TLRPC.TL_documentEmpty) {
                charSequence = LocaleController.getString("AttachVideoExpired", NUM);
            } else {
                charSequence = messageObject.messageText;
            }
        } else {
            charSequence = this.customText;
        }
        createLayout(charSequence, this.previousWidth);
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && messageObject2.type == 11) {
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
        for (int i3 = i - 1; i3 >= 0; i3--) {
            int ceil3 = (int) Math.ceil((double) this.textLayout.getLineWidth(i3));
            if (Math.abs(ceil3 - ceil) >= AndroidUtilities.dp(10.0f)) {
                break;
            }
            ceil = Math.max(ceil3, ceil);
        }
        return ceil;
    }

    private boolean isLineTop(int i, int i2, int i3, int i4, int i5) {
        if (i3 != 0) {
            return i3 >= 0 && i3 < i4 && findMaxWidthAroundLine(i3 - 1) + (i5 * 3) < i;
        }
        return true;
    }

    private boolean isLineBottom(int i, int i2, int i3, int i4, int i5) {
        int i6 = i4 - 1;
        if (i3 != i6) {
            return i3 >= 0 && i3 <= i6 && findMaxWidthAroundLine(i3 + 1) + (i5 * 3) < i;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean z;
        int i;
        char c;
        int i2;
        int i3;
        char c2;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        char c3;
        int i9;
        int i10;
        char c4;
        int i11;
        int i12;
        int i13;
        char c5;
        char c6;
        int i14;
        int i15;
        int i16;
        int i17;
        int i18;
        int i19;
        int i20;
        char c7;
        int i21;
        int i22;
        int i23;
        int i24;
        int i25;
        ChatActionCell chatActionCell = this;
        Canvas canvas2 = canvas;
        MessageObject messageObject = chatActionCell.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            chatActionCell.imageReceiver.draw(canvas2);
        }
        StaticLayout staticLayout = chatActionCell.textLayout;
        if (staticLayout != null) {
            int lineCount = staticLayout.getLineCount();
            int dp = AndroidUtilities.dp(11.0f);
            int dp2 = AndroidUtilities.dp(6.0f);
            int i26 = dp - dp2;
            int dp3 = AndroidUtilities.dp(8.0f);
            int dp4 = AndroidUtilities.dp(7.0f);
            int i27 = 0;
            int i28 = 0;
            int i29 = 0;
            while (i29 < lineCount) {
                int findMaxWidthAroundLine = chatActionCell.findMaxWidthAroundLine(i29);
                int measuredWidth = ((getMeasuredWidth() - findMaxWidthAroundLine) - i26) / 2;
                int i30 = findMaxWidthAroundLine + i26;
                int lineBottom = chatActionCell.textLayout.getLineBottom(i29);
                int i31 = lineBottom - i27;
                boolean z2 = i29 == lineCount + -1;
                boolean z3 = i29 == 0;
                if (z3) {
                    dp4 -= AndroidUtilities.dp(3.0f);
                    i31 += AndroidUtilities.dp(3.0f);
                }
                if (z2) {
                    i31 += AndroidUtilities.dp(3.0f);
                }
                int i32 = i31;
                if (z2 || (i25 = i29 + 1) >= lineCount) {
                    z = z2;
                    c = 0;
                    i = 0;
                } else {
                    int findMaxWidthAroundLine2 = chatActionCell.findMaxWidthAroundLine(i25) + i26;
                    int i33 = i26 * 2;
                    if (findMaxWidthAroundLine2 + i33 < i30) {
                        i = findMaxWidthAroundLine2;
                        c = 1;
                        z = true;
                    } else if (i30 + i33 < findMaxWidthAroundLine2) {
                        i = findMaxWidthAroundLine2;
                        z = z2;
                        c = 2;
                    } else {
                        i = findMaxWidthAroundLine2;
                        z = z2;
                        c = 3;
                    }
                }
                if (z3 || i29 <= 0) {
                    i2 = dp4;
                    c2 = 0;
                    i3 = 0;
                } else {
                    int findMaxWidthAroundLine3 = chatActionCell.findMaxWidthAroundLine(i29 - 1) + i26;
                    int i34 = i26 * 2;
                    i2 = dp4;
                    if (findMaxWidthAroundLine3 + i34 < i30) {
                        i3 = findMaxWidthAroundLine3;
                        c2 = 1;
                        z3 = true;
                    } else if (i30 + i34 < findMaxWidthAroundLine3) {
                        i3 = findMaxWidthAroundLine3;
                        c2 = 2;
                    } else {
                        i3 = findMaxWidthAroundLine3;
                        c2 = 3;
                    }
                }
                if (c == 0) {
                    i4 = measuredWidth;
                    i5 = i28;
                    i8 = i29;
                    c3 = c;
                    i6 = dp;
                    i7 = lineBottom;
                    i11 = i2;
                    c4 = c2;
                    i9 = i30;
                    i10 = 0;
                } else if (c == 1) {
                    int dp5 = AndroidUtilities.dp(3.0f);
                    i7 = lineBottom;
                    int measuredWidth2 = (getMeasuredWidth() - i) / 2;
                    char c8 = c2;
                    i11 = i2;
                    i9 = i30;
                    i6 = dp;
                    int i35 = measuredWidth;
                    i5 = i28;
                    i8 = i29;
                    if (isLineBottom(i, i30, i29 + 1, lineCount, i26)) {
                        int i36 = i11 + i32;
                        float f = (float) i36;
                        canvas.drawRect((float) (i35 + dp2), f, (float) (measuredWidth2 - i26), (float) (AndroidUtilities.dp(3.0f) + i36), Theme.chat_actionBackgroundPaint);
                        canvas.drawRect((float) (measuredWidth2 + i + i26), f, (float) ((i35 + i9) - dp2), (float) (i36 + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                    } else {
                        int i37 = i11 + i32;
                        float f2 = (float) i37;
                        canvas.drawRect((float) (i35 + dp2), f2, (float) measuredWidth2, (float) (AndroidUtilities.dp(3.0f) + i37), Theme.chat_actionBackgroundPaint);
                        canvas.drawRect((float) (measuredWidth2 + i), f2, (float) ((i35 + i9) - dp2), (float) (i37 + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                    }
                    i4 = i35;
                    i10 = dp5;
                    c4 = c8;
                    c3 = c;
                    canvas2 = canvas;
                } else {
                    char c9 = c2;
                    i5 = i28;
                    i8 = i29;
                    i6 = dp;
                    i7 = lineBottom;
                    i11 = i2;
                    i9 = i30;
                    int i38 = measuredWidth;
                    if (c == 2) {
                        int dp6 = AndroidUtilities.dp(3.0f);
                        int dp7 = (i11 + i32) - AndroidUtilities.dp(11.0f);
                        int i39 = i38 - dp3;
                        if (!(c9 == 2 || c9 == 3)) {
                            i39 -= i26;
                        }
                        int i40 = i39;
                        if (z3 || z) {
                            int i41 = i40 + dp3;
                            c7 = c9;
                            i21 = i40;
                            canvas.drawRect((float) i41, (float) (AndroidUtilities.dp(3.0f) + dp7), (float) (i41 + i6), (float) (dp7 + i6), Theme.chat_actionBackgroundPaint);
                        } else {
                            c7 = c9;
                            i21 = i40;
                        }
                        int i42 = dp7 + dp3;
                        Theme.chat_cornerInner[2].setBounds(i21, dp7, i21 + dp3, i42);
                        canvas2 = canvas;
                        char CLASSNAME = c7;
                        Theme.chat_cornerInner[2].draw(canvas2);
                        int i43 = i38 + i9;
                        if (!(CLASSNAME == 2 || CLASSNAME == 3)) {
                            i43 += i26;
                        }
                        if (z3 || z) {
                            i22 = dp6;
                            i23 = i43;
                            c3 = c;
                            i24 = i42;
                            i4 = i38;
                            c4 = CLASSNAME;
                            canvas.drawRect((float) (i43 - i6), (float) (AndroidUtilities.dp(3.0f) + dp7), (float) i43, (float) (dp7 + i6), Theme.chat_actionBackgroundPaint);
                        } else {
                            c3 = c;
                            i4 = i38;
                            i22 = dp6;
                            i23 = i43;
                            i24 = i42;
                            c4 = CLASSNAME;
                        }
                        Theme.chat_cornerInner[3].setBounds(i23, dp7, i23 + dp3, i24);
                        Theme.chat_cornerInner[3].draw(canvas2);
                        i10 = i22;
                    } else {
                        c3 = c;
                        i4 = i38;
                        c4 = c9;
                        canvas2 = canvas;
                        i10 = AndroidUtilities.dp(6.0f);
                    }
                }
                if (c4 == 0) {
                    i12 = lineCount;
                    c5 = c4;
                    c6 = c3;
                    i14 = i11;
                    i13 = i32;
                } else if (c4 == 1) {
                    int measuredWidth3 = (getMeasuredWidth() - i3) / 2;
                    int dp8 = i11 - AndroidUtilities.dp(3.0f);
                    i13 = i32 + AndroidUtilities.dp(3.0f);
                    if (isLineTop(i3, i9, i8 - 1, lineCount, i26)) {
                        float f3 = (float) dp8;
                        canvas.drawRect((float) (i4 + dp2), f3, (float) (measuredWidth3 - i26), (float) (AndroidUtilities.dp(3.0f) + dp8), Theme.chat_actionBackgroundPaint);
                        canvas.drawRect((float) (measuredWidth3 + i3 + i26), f3, (float) ((i4 + i9) - dp2), (float) (AndroidUtilities.dp(3.0f) + dp8), Theme.chat_actionBackgroundPaint);
                    } else {
                        float f4 = (float) dp8;
                        canvas.drawRect((float) (i4 + dp2), f4, (float) measuredWidth3, (float) (AndroidUtilities.dp(3.0f) + dp8), Theme.chat_actionBackgroundPaint);
                        canvas.drawRect((float) (measuredWidth3 + i3), f4, (float) ((i4 + i9) - dp2), (float) (AndroidUtilities.dp(3.0f) + dp8), Theme.chat_actionBackgroundPaint);
                    }
                    i12 = lineCount;
                    c5 = c4;
                    i14 = dp8;
                    c6 = c3;
                } else if (c4 == 2) {
                    int dp9 = i11 - AndroidUtilities.dp(3.0f);
                    int dp10 = i32 + AndroidUtilities.dp(3.0f);
                    int i44 = i4 - dp3;
                    char CLASSNAME = c3;
                    if (!(CLASSNAME == 2 || CLASSNAME == 3)) {
                        i44 -= i26;
                    }
                    int i45 = i44;
                    if (z3 || z) {
                        int i46 = i45 + dp3;
                        i12 = lineCount;
                        i17 = i45;
                        i16 = dp10;
                        c6 = CLASSNAME;
                        canvas.drawRect((float) i46, (float) (AndroidUtilities.dp(3.0f) + dp9), (float) (i46 + i6), (float) (dp9 + AndroidUtilities.dp(11.0f)), Theme.chat_actionBackgroundPaint);
                    } else {
                        i12 = lineCount;
                        i16 = dp10;
                        i17 = i45;
                        c6 = CLASSNAME;
                    }
                    int i47 = i5;
                    int i48 = i47 + dp3;
                    Theme.chat_cornerInner[0].setBounds(i17, i47, i17 + dp3, i48);
                    Theme.chat_cornerInner[0].draw(canvas2);
                    int i49 = i4 + i9;
                    if (!(c6 == 2 || c6 == 3)) {
                        i49 += i26;
                    }
                    int i50 = i49;
                    if (z3 || z) {
                        float dp11 = (float) (AndroidUtilities.dp(3.0f) + dp9);
                        float dp12 = (float) (AndroidUtilities.dp(11.0f) + dp9);
                        i18 = dp9;
                        i20 = i48;
                        float f5 = dp12;
                        c5 = c4;
                        i19 = i47;
                        canvas.drawRect((float) (i50 - i6), dp11, (float) i50, f5, Theme.chat_actionBackgroundPaint);
                    } else {
                        i18 = dp9;
                        c5 = c4;
                        i20 = i48;
                        i19 = i47;
                    }
                    Theme.chat_cornerInner[1].setBounds(i50, i19, i50 + dp3, i20);
                    Theme.chat_cornerInner[1].draw(canvas2);
                    i14 = i18;
                    i13 = i16;
                } else {
                    i12 = lineCount;
                    c5 = c4;
                    c6 = c3;
                    i13 = i32 + AndroidUtilities.dp(6.0f);
                    i14 = i11 - AndroidUtilities.dp(6.0f);
                }
                if (z3 || z) {
                    i15 = i4;
                    canvas.drawRect((float) (i15 + dp2), (float) i11, (float) ((i15 + i9) - dp2), (float) (i11 + i32), Theme.chat_actionBackgroundPaint);
                } else {
                    i15 = i4;
                    canvas.drawRect((float) i15, (float) i11, (float) (i15 + i9), (float) (i11 + i32), Theme.chat_actionBackgroundPaint);
                }
                int i51 = i15 - i26;
                int i52 = (i15 + i9) - dp2;
                if (z3 && !z && c6 != 2) {
                    int i53 = i14 + i13 + i10;
                    float f6 = (float) (i14 + i6);
                    canvas.drawRect((float) i51, f6, (float) (i51 + i6), (float) (i53 - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) i52, f6, (float) (i52 + i6), (float) (i53 - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                } else if (z && !z3 && c5 != 2) {
                    int i54 = i14 + i6;
                    float f7 = (float) (((i14 + i13) + i10) - i6);
                    canvas.drawRect((float) i51, (float) (i54 - AndroidUtilities.dp(5.0f)), (float) (i51 + i6), f7, Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) i52, (float) (i54 - AndroidUtilities.dp(5.0f)), (float) (i52 + i6), f7, Theme.chat_actionBackgroundPaint);
                } else if (z3 || z) {
                    float f8 = (float) (i14 + i6);
                    float f9 = (float) (((i14 + i13) + i10) - i6);
                    canvas.drawRect((float) i51, f8, (float) (i51 + i6), f9, Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) i52, f8, (float) (i52 + i6), f9, Theme.chat_actionBackgroundPaint);
                }
                if (z3) {
                    int i55 = i14 + i6;
                    Theme.chat_cornerOuter[0].setBounds(i51, i14, i51 + i6, i55);
                    Theme.chat_cornerOuter[0].draw(canvas2);
                    Theme.chat_cornerOuter[1].setBounds(i52, i14, i52 + i6, i55);
                    Theme.chat_cornerOuter[1].draw(canvas2);
                }
                if (z) {
                    int i56 = ((i14 + i13) + i10) - i6;
                    int i57 = i56 + i6;
                    Theme.chat_cornerOuter[2].setBounds(i52, i56, i52 + i6, i57);
                    Theme.chat_cornerOuter[2].draw(canvas2);
                    Theme.chat_cornerOuter[3].setBounds(i51, i56, i51 + i6, i57);
                    Theme.chat_cornerOuter[3].draw(canvas2);
                }
                int i58 = i14 + i13;
                i28 = i58 + i10;
                i29 = i8 + 1;
                dp4 = i58;
                i27 = i7;
                dp = i6;
                lineCount = i12;
                chatActionCell = this;
            }
            canvas.save();
            canvas2.translate((float) this.textXLeft, (float) this.textY);
            this.textLayout.draw(canvas2);
            canvas.restore();
            return;
        }
        ChatActionCell chatActionCell2 = chatActionCell;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (!TextUtils.isEmpty(this.customText) || this.currentMessageObject != null) {
            accessibilityNodeInfo.setText(!TextUtils.isEmpty(this.customText) ? this.customText : this.currentMessageObject.messageText);
            accessibilityNodeInfo.setEnabled(true);
        }
    }
}
