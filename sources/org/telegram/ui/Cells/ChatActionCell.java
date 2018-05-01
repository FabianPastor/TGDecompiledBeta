package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC.KeyboardButton;
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
    private boolean imagePressed = false;
    private ImageReceiver imageReceiver = new ImageReceiver(this);
    private URLSpan pressedLink;
    private int previousWidth = 0;
    private int textHeight = 0;
    private StaticLayout textLayout;
    private int textWidth = 0;
    private int textX = 0;
    private int textXLeft = 0;
    private int textY = 0;

    /* renamed from: org.telegram.ui.Cells.ChatActionCell$1 */
    class C08711 implements Runnable {
        C08711() {
        }

        public void run() {
            ChatActionCell.this.requestLayout();
        }
    }

    public interface ChatActionCellDelegate {
        void didClickedImage(ChatActionCell chatActionCell);

        void didLongPressed(ChatActionCell chatActionCell);

        void didPressedBotButton(MessageObject messageObject, KeyboardButton keyboardButton);

        void didPressedReplyMessage(ChatActionCell chatActionCell, int i);

        void needOpenUserProfile(int i);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    public ChatActionCell(Context context) {
        super(context);
        this.imageReceiver.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setDelegate(ChatActionCellDelegate chatActionCellDelegate) {
        this.delegate = chatActionCellDelegate;
    }

    public void setCustomDate(int i) {
        if (this.customDate != i) {
            CharSequence formatDateChat = LocaleController.formatDateChat((long) i);
            if (this.customText == null || !TextUtils.equals(formatDateChat, this.customText)) {
                this.previousWidth = 0;
                this.customDate = i;
                this.customText = formatDateChat;
                if (getMeasuredWidth() != 0) {
                    createLayout(this.customText, getMeasuredWidth());
                    invalidate();
                }
                AndroidUtilities.runOnUIThread(new C08711());
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        if (this.currentMessageObject != messageObject || (!this.hasReplyMessage && messageObject.replyMessageObject != null)) {
            this.currentMessageObject = messageObject;
            this.hasReplyMessage = messageObject.replyMessageObject != null;
            this.previousWidth = 0;
            if (this.currentMessageObject.type == 11) {
                if (messageObject.messageOwner.to_id == null) {
                    messageObject = null;
                } else if (messageObject.messageOwner.to_id.chat_id != 0) {
                    messageObject = messageObject.messageOwner.to_id.chat_id;
                } else if (messageObject.messageOwner.to_id.channel_id != 0) {
                    messageObject = messageObject.messageOwner.to_id.channel_id;
                } else {
                    int i = messageObject.messageOwner.to_id.user_id;
                    messageObject = i == UserConfig.getInstance(this.currentAccount).getClientUserId() ? messageObject.messageOwner.from_id : i;
                }
                this.avatarDrawable.setInfo(messageObject, null, null, false);
                if ((this.currentMessageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) != null) {
                    this.imageReceiver.setImage(this.currentMessageObject.messageOwner.action.newUserPhoto.photo_small, "50_50", this.avatarDrawable, null, 0);
                } else {
                    messageObject = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.dp(64.0f));
                    if (messageObject != null) {
                        this.imageReceiver.setImage(messageObject.location, "50_50", this.avatarDrawable, null, 0);
                    } else {
                        this.imageReceiver.setImageBitmap(this.avatarDrawable);
                    }
                }
                this.imageReceiver.setVisible(PhotoViewer.isShowingImage(this.currentMessageObject) ^ true, false);
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

    protected void onLongPress() {
        if (this.delegate != null) {
            this.delegate.didLongPressed(this);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(motionEvent);
        }
        boolean z;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        boolean z2 = true;
        if (motionEvent.getAction() != 0) {
            if (motionEvent.getAction() != 2) {
                cancelCheckLongPress();
            }
            if (this.imagePressed) {
                if (motionEvent.getAction() == 1) {
                    this.imagePressed = false;
                    if (this.delegate != null) {
                        this.delegate.didClickedImage(this);
                        playSoundEffect(0);
                    }
                } else if (motionEvent.getAction() == 3) {
                    this.imagePressed = false;
                } else if (motionEvent.getAction() == 2 && !this.imageReceiver.isInsideImage(x, y)) {
                    this.imagePressed = false;
                }
            }
        } else if (this.delegate != null) {
            if (this.currentMessageObject.type == 11 && this.imageReceiver.isInsideImage(x, y)) {
                this.imagePressed = true;
                z = true;
            } else {
                z = false;
            }
            if (z) {
                startCheckLongPress();
            }
            if (!z && (motionEvent.getAction() == 0 || (this.pressedLink != null && motionEvent.getAction() == 1))) {
                if (x >= ((float) this.textX) || y < ((float) this.textY) || x > ((float) (this.textX + this.textWidth)) || y > ((float) (this.textY + this.textHeight))) {
                    this.pressedLink = null;
                } else {
                    x -= (float) this.textXLeft;
                    int lineForVertical = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                    int offsetForHorizontal = this.textLayout.getOffsetForHorizontal(lineForVertical, x);
                    float lineLeft = this.textLayout.getLineLeft(lineForVertical);
                    if (lineLeft > x || lineLeft + this.textLayout.getLineWidth(lineForVertical) < x || !(this.currentMessageObject.messageText instanceof Spannable)) {
                        this.pressedLink = null;
                    } else {
                        URLSpan[] uRLSpanArr = (URLSpan[]) ((Spannable) this.currentMessageObject.messageText).getSpans(offsetForHorizontal, offsetForHorizontal, URLSpan.class);
                        if (uRLSpanArr.length != 0) {
                            if (motionEvent.getAction() == 0) {
                                this.pressedLink = uRLSpanArr[0];
                            } else if (uRLSpanArr[0] == this.pressedLink) {
                                if (this.delegate != null) {
                                    String url = uRLSpanArr[0].getURL();
                                    if (url.startsWith("game")) {
                                        this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
                                    } else if (url.startsWith("http")) {
                                        Browser.openUrl(getContext(), url);
                                    } else {
                                        this.delegate.needOpenUserProfile(Integer.parseInt(url));
                                    }
                                }
                            }
                            z = z2;
                        } else {
                            this.pressedLink = null;
                        }
                        z2 = z;
                        z = z2;
                    }
                }
            }
            if (!z) {
                z = super.onTouchEvent(motionEvent);
            }
            return z;
        }
        z = false;
        if (x >= ((float) this.textX)) {
        }
        this.pressedLink = null;
        if (z) {
            z = super.onTouchEvent(motionEvent);
        }
        return z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createLayout(CharSequence charSequence, int i) {
        int dp = i - AndroidUtilities.dp(30.0f);
        this.textLayout = new StaticLayout(charSequence, Theme.chat_actionTextPaint, dp, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        charSequence = null;
        this.textHeight = 0;
        this.textWidth = 0;
        int lineCount = this.textLayout.getLineCount();
        while (charSequence < lineCount) {
            try {
                float lineWidth = this.textLayout.getLineWidth(charSequence);
                float f = (float) dp;
                if (lineWidth > f) {
                    lineWidth = f;
                }
                this.textHeight = (int) Math.max((double) this.textHeight, Math.ceil((double) this.textLayout.getLineBottom(charSequence)));
                this.textWidth = (int) Math.max((double) this.textWidth, Math.ceil((double) lineWidth));
                charSequence++;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return;
            }
        }
        this.textX = (i - this.textWidth) / 2;
        this.textY = AndroidUtilities.dp(7.0f);
        this.textXLeft = (i - this.textLayout.getWidth()) / 2;
    }

    protected void onMeasure(int i, int i2) {
        if (this.currentMessageObject == 0 && this.customText == 0) {
            setMeasuredDimension(MeasureSpec.getSize(i), this.textHeight + AndroidUtilities.dp(14.0f));
            return;
        }
        i = Math.max(AndroidUtilities.dp(NUM), MeasureSpec.getSize(i));
        if (i != this.previousWidth) {
            if (this.currentMessageObject == 0) {
                i2 = this.customText;
            } else if (this.currentMessageObject.messageOwner == 0 || this.currentMessageObject.messageOwner.media == 0 || this.currentMessageObject.messageOwner.media.ttl_seconds == 0) {
                i2 = this.currentMessageObject.messageText;
            } else if ((this.currentMessageObject.messageOwner.media.photo instanceof TL_photoEmpty) != 0) {
                i2 = LocaleController.getString("AttachPhotoExpired", C0446R.string.AttachPhotoExpired);
            } else if ((this.currentMessageObject.messageOwner.media.document instanceof TL_documentEmpty) != 0) {
                i2 = LocaleController.getString("AttachVideoExpired", C0446R.string.AttachVideoExpired);
            } else {
                i2 = this.currentMessageObject.messageText;
            }
            this.previousWidth = i;
            createLayout(i2, i);
            if (this.currentMessageObject != 0 && this.currentMessageObject.type == 11) {
                this.imageReceiver.setImageCoords((i - AndroidUtilities.dp(64.0f)) / 2, this.textHeight + AndroidUtilities.dp(15.0f), AndroidUtilities.dp(64.0f), AndroidUtilities.dp(64.0f));
            }
        }
        i2 = this.textHeight;
        int i3 = (this.currentMessageObject == null || this.currentMessageObject.type != 11) ? 0 : 70;
        setMeasuredDimension(i, i2 + AndroidUtilities.dp((float) (14 + i3)));
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

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.currentMessageObject != null && r6.currentMessageObject.type == 11) {
            ChatActionCell chatActionCell;
            chatActionCell.imageReceiver.draw(canvas2);
        }
        if (chatActionCell.textLayout != null) {
            int lineCount = chatActionCell.textLayout.getLineCount();
            int dp = AndroidUtilities.dp(11.0f);
            int dp2 = AndroidUtilities.dp(6.0f);
            int i = dp - dp2;
            int dp3 = AndroidUtilities.dp(8.0f);
            int dp4 = AndroidUtilities.dp(7.0f);
            int i2 = 0;
            int i3 = 0;
            while (i3 < lineCount) {
                int i4;
                int i5;
                Object obj;
                int i6;
                int i7;
                int i8;
                int i9;
                int i10;
                int i11;
                int i12;
                int i13;
                int i14;
                int i15;
                int i16;
                int i17;
                float f;
                float f2;
                float f3;
                int i18;
                int i19;
                int i20;
                int i21;
                int i22;
                float f4;
                int i23;
                float f5;
                float f6;
                int i24;
                float f7;
                float f8;
                float f9;
                int findMaxWidthAroundLine = chatActionCell.findMaxWidthAroundLine(i3);
                int measuredWidth = ((getMeasuredWidth() - findMaxWidthAroundLine) - i) / 2;
                findMaxWidthAroundLine += i;
                int lineBottom = chatActionCell.textLayout.getLineBottom(i3);
                i2 = lineBottom - i2;
                Object obj2 = i3 == lineCount + -1 ? 1 : null;
                Object obj3 = i3 == 0 ? 1 : null;
                if (obj3 != null) {
                    dp4 -= AndroidUtilities.dp(3.0f);
                    i2 += AndroidUtilities.dp(3.0f);
                }
                if (obj2 != null) {
                    i2 += AndroidUtilities.dp(3.0f);
                }
                int i25 = i2;
                if (obj2 == null) {
                    i2 = i3 + 1;
                    if (i2 < lineCount) {
                        i2 = chatActionCell.findMaxWidthAroundLine(i2) + i;
                        i4 = i * 2;
                        if (i2 + i4 < findMaxWidthAroundLine) {
                            i4 = i2;
                            i5 = 1;
                            obj = 1;
                        } else if (findMaxWidthAroundLine + i4 < i2) {
                            i4 = i2;
                            obj = obj2;
                            i5 = 2;
                        } else {
                            i4 = i2;
                            obj = obj2;
                            i5 = 3;
                        }
                        if (obj3 == null || i3 <= 0) {
                            i6 = 0;
                            i7 = 0;
                        } else {
                            i2 = chatActionCell.findMaxWidthAroundLine(i3 - 1) + i;
                            i8 = i * 2;
                            if (i2 + i8 < findMaxWidthAroundLine) {
                                i7 = i2;
                                i6 = 1;
                                obj3 = 1;
                            } else if (i8 + findMaxWidthAroundLine < i2) {
                                i7 = i2;
                                i6 = 2;
                            } else {
                                i7 = i2;
                                i6 = 3;
                            }
                        }
                        if (i5 != 0) {
                            i9 = findMaxWidthAroundLine;
                            i10 = measuredWidth;
                            i11 = i3;
                            i12 = dp;
                            i13 = i5;
                            i14 = lineBottom;
                            dp = dp4;
                            i5 = i6;
                            i15 = 0;
                        } else if (i5 != 1) {
                            i2 = (getMeasuredWidth() - i4) / 2;
                            i15 = AndroidUtilities.dp(3.0f);
                            i12 = dp;
                            i14 = lineBottom;
                            lineBottom = i2;
                            dp = dp4;
                            i9 = findMaxWidthAroundLine;
                            i16 = measuredWidth;
                            i17 = i6;
                            f = 3.0f;
                            i11 = i3;
                            if (chatActionCell.isLineBottom(i4, findMaxWidthAroundLine, i3 + 1, lineCount, i)) {
                                i3 = dp + i25;
                                f2 = (float) i3;
                                f3 = f2;
                                i18 = i3;
                                canvas2.drawRect((float) (i16 + dp2), f2, (float) lineBottom, (float) (AndroidUtilities.dp(f) + i3), Theme.chat_actionBackgroundPaint);
                                canvas2.drawRect((float) (lineBottom + i4), f3, (float) ((i16 + i9) - dp2), (float) (i18 + AndroidUtilities.dp(f)), Theme.chat_actionBackgroundPaint);
                            } else {
                                i3 = dp + i25;
                                f2 = (float) i3;
                                f3 = f2;
                                i18 = i3;
                                canvas2.drawRect((float) (i16 + dp2), f2, (float) (lineBottom - i), (float) (AndroidUtilities.dp(f) + i3), Theme.chat_actionBackgroundPaint);
                                canvas2.drawRect((float) ((lineBottom + i4) + i), f3, (float) ((i16 + i9) - dp2), (float) (i18 + AndroidUtilities.dp(f)), Theme.chat_actionBackgroundPaint);
                            }
                            i10 = i16;
                            i13 = i5;
                            i5 = i17;
                        } else {
                            i9 = findMaxWidthAroundLine;
                            i16 = measuredWidth;
                            i11 = i3;
                            i17 = i6;
                            i12 = dp;
                            i14 = lineBottom;
                            dp = dp4;
                            if (i5 != 2) {
                                i15 = AndroidUtilities.dp(3.0f);
                                i3 = (dp + i25) - AndroidUtilities.dp(11.0f);
                                measuredWidth = i16 - dp3;
                                i8 = i17;
                                if (!(i8 == 2 || i8 == 3)) {
                                    measuredWidth -= i;
                                }
                                if (obj3 == null) {
                                    if (obj != null) {
                                        i19 = measuredWidth;
                                        i20 = i8;
                                        i6 = i3;
                                        measuredWidth = i19;
                                        i3 = i6 + dp3;
                                        Theme.chat_cornerInner[2].setBounds(measuredWidth, i6, measuredWidth + dp3, i3);
                                        Theme.chat_cornerInner[2].draw(canvas2);
                                        measuredWidth = i16 + i9;
                                        i8 = i20;
                                        if (!(i8 == 2 || i8 == 3)) {
                                            measuredWidth += i;
                                        }
                                        if (obj3 == null) {
                                            if (obj == null) {
                                                lineBottom = i3;
                                                i10 = i16;
                                                i13 = i5;
                                                i16 = measuredWidth;
                                                i5 = i8;
                                                Theme.chat_cornerInner[3].setBounds(i16, i6, i16 + dp3, lineBottom);
                                                Theme.chat_cornerInner[3].draw(canvas2);
                                            }
                                        }
                                        i10 = i16;
                                        i16 = measuredWidth;
                                        i13 = i5;
                                        i5 = i8;
                                        lineBottom = i3;
                                        canvas2.drawRect((float) (measuredWidth - i12), (float) (i6 + AndroidUtilities.dp(3.0f)), (float) measuredWidth, (float) (i6 + i12), Theme.chat_actionBackgroundPaint);
                                        Theme.chat_cornerInner[3].setBounds(i16, i6, i16 + dp3, lineBottom);
                                        Theme.chat_cornerInner[3].draw(canvas2);
                                    }
                                }
                                i2 = measuredWidth + dp3;
                                i19 = measuredWidth;
                                i20 = i8;
                                i6 = i3;
                                canvas2.drawRect((float) i2, (float) (AndroidUtilities.dp(3.0f) + i3), (float) (i2 + i12), (float) (i3 + i12), Theme.chat_actionBackgroundPaint);
                                measuredWidth = i19;
                                i3 = i6 + dp3;
                                Theme.chat_cornerInner[2].setBounds(measuredWidth, i6, measuredWidth + dp3, i3);
                                Theme.chat_cornerInner[2].draw(canvas2);
                                measuredWidth = i16 + i9;
                                i8 = i20;
                                measuredWidth += i;
                                if (obj3 == null) {
                                    if (obj == null) {
                                        lineBottom = i3;
                                        i10 = i16;
                                        i13 = i5;
                                        i16 = measuredWidth;
                                        i5 = i8;
                                        Theme.chat_cornerInner[3].setBounds(i16, i6, i16 + dp3, lineBottom);
                                        Theme.chat_cornerInner[3].draw(canvas2);
                                    }
                                }
                                i10 = i16;
                                i16 = measuredWidth;
                                i13 = i5;
                                i5 = i8;
                                lineBottom = i3;
                                canvas2.drawRect((float) (measuredWidth - i12), (float) (i6 + AndroidUtilities.dp(3.0f)), (float) measuredWidth, (float) (i6 + i12), Theme.chat_actionBackgroundPaint);
                                Theme.chat_cornerInner[3].setBounds(i16, i6, i16 + dp3, lineBottom);
                                Theme.chat_cornerInner[3].draw(canvas2);
                            } else {
                                i10 = i16;
                                i13 = i5;
                                i5 = i17;
                                i15 = AndroidUtilities.dp(6.0f);
                            }
                        }
                        if (i5 != 0) {
                            i21 = lineCount;
                            i22 = i5;
                            i6 = i10;
                            i16 = i13;
                            f4 = 11.0f;
                            lineCount = dp;
                            i23 = i25;
                        } else if (i5 != 1) {
                            i6 = i10;
                            lineBottom = (getMeasuredWidth() - i7) / 2;
                            i4 = i25 + AndroidUtilities.dp(3.0f);
                            i8 = lineCount;
                            i21 = lineCount;
                            lineCount = dp - AndroidUtilities.dp(3.0f);
                            if (isLineTop(i7, i9, i11 - 1, i8, i)) {
                                f5 = (float) lineCount;
                                f6 = f5;
                                canvas2.drawRect((float) (i6 + dp2), f5, (float) lineBottom, (float) (lineCount + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                                canvas2.drawRect((float) (lineBottom + i7), f6, (float) ((i6 + i9) - dp2), (float) (lineCount + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                            } else {
                                f5 = (float) lineCount;
                                f6 = f5;
                                canvas2.drawRect((float) (i6 + dp2), f5, (float) (lineBottom - i), (float) (lineCount + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                                canvas2.drawRect((float) ((lineBottom + i7) + i), f6, (float) ((i6 + i9) - dp2), (float) (lineCount + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                            }
                            i22 = i5;
                            i23 = i4;
                            i16 = i13;
                            f4 = 11.0f;
                        } else {
                            i21 = lineCount;
                            i6 = i10;
                            chatActionCell = this;
                            if (i5 != 2) {
                                lineCount = dp - AndroidUtilities.dp(3.0f);
                                lineBottom = i25 + AndroidUtilities.dp(3.0f);
                                i3 = lineCount + AndroidUtilities.dp(6.2f);
                                measuredWidth = i6 - dp3;
                                i8 = i13;
                                if (!(i8 == 2 || i8 == 3)) {
                                    measuredWidth -= i;
                                }
                                if (obj3 == null) {
                                    if (obj != null) {
                                        i16 = i8;
                                        i22 = i5;
                                        i23 = lineBottom;
                                        lineBottom = measuredWidth;
                                        i5 = i3;
                                        i3 = i5 + dp3;
                                        Theme.chat_cornerInner[0].setBounds(lineBottom, i5, lineBottom + dp3, i3);
                                        Theme.chat_cornerInner[0].draw(canvas2);
                                        measuredWidth = i6 + i9;
                                        if (!(i16 == 2 || i16 == 3)) {
                                            measuredWidth += i;
                                        }
                                        lineBottom = measuredWidth;
                                        if (obj3 == null) {
                                            if (obj == null) {
                                                i24 = lineCount;
                                                f4 = 11.0f;
                                                lineCount = i3;
                                                Theme.chat_cornerInner[1].setBounds(lineBottom, i5, lineBottom + dp3, lineCount);
                                                Theme.chat_cornerInner[1].draw(canvas2);
                                                lineCount = i24;
                                            }
                                        }
                                        f4 = 11.0f;
                                        i24 = lineCount;
                                        lineCount = i3;
                                        canvas2.drawRect((float) (lineBottom - i12), (float) (AndroidUtilities.dp(3.0f) + lineCount), (float) lineBottom, (float) (AndroidUtilities.dp(11.0f) + lineCount), Theme.chat_actionBackgroundPaint);
                                        Theme.chat_cornerInner[1].setBounds(lineBottom, i5, lineBottom + dp3, lineCount);
                                        Theme.chat_cornerInner[1].draw(canvas2);
                                        lineCount = i24;
                                    }
                                }
                                i2 = measuredWidth + dp3;
                                i23 = lineBottom;
                                lineBottom = measuredWidth;
                                i16 = i8;
                                i22 = i5;
                                i5 = i3;
                                canvas2.drawRect((float) i2, (float) (lineCount + AndroidUtilities.dp(3.0f)), (float) (i2 + i12), (float) (lineCount + AndroidUtilities.dp(11.0f)), Theme.chat_actionBackgroundPaint);
                                i3 = i5 + dp3;
                                Theme.chat_cornerInner[0].setBounds(lineBottom, i5, lineBottom + dp3, i3);
                                Theme.chat_cornerInner[0].draw(canvas2);
                                measuredWidth = i6 + i9;
                                measuredWidth += i;
                                lineBottom = measuredWidth;
                                if (obj3 == null) {
                                    if (obj == null) {
                                        i24 = lineCount;
                                        f4 = 11.0f;
                                        lineCount = i3;
                                        Theme.chat_cornerInner[1].setBounds(lineBottom, i5, lineBottom + dp3, lineCount);
                                        Theme.chat_cornerInner[1].draw(canvas2);
                                        lineCount = i24;
                                    }
                                }
                                f4 = 11.0f;
                                i24 = lineCount;
                                lineCount = i3;
                                canvas2.drawRect((float) (lineBottom - i12), (float) (AndroidUtilities.dp(3.0f) + lineCount), (float) lineBottom, (float) (AndroidUtilities.dp(11.0f) + lineCount), Theme.chat_actionBackgroundPaint);
                                Theme.chat_cornerInner[1].setBounds(lineBottom, i5, lineBottom + dp3, lineCount);
                                Theme.chat_cornerInner[1].draw(canvas2);
                                lineCount = i24;
                            } else {
                                i22 = i5;
                                i16 = i13;
                                f4 = 11.0f;
                                i23 = i25 + AndroidUtilities.dp(6.0f);
                                lineCount = dp - AndroidUtilities.dp(6.0f);
                            }
                        }
                        if (obj3 == null) {
                            if (obj != null) {
                                canvas2.drawRect((float) i6, (float) dp, (float) (i6 + i9), (float) (dp + i25), Theme.chat_actionBackgroundPaint);
                                dp = i6 - i;
                                i6 = (i6 + i9) - dp2;
                                if (obj3 == null && obj == null && r6 != 2) {
                                    i5 = (lineCount + i23) + i15;
                                    f7 = (float) (lineCount + i12);
                                    canvas2.drawRect((float) dp, f7, (float) (dp + i12), (float) (i5 - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                                    f8 = 6.0f;
                                    canvas2.drawRect((float) i6, f7, (float) (i6 + i12), (float) (i5 - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                                } else {
                                    f8 = 6.0f;
                                    if (obj == null && obj3 == null && r40 != 2) {
                                        i16 = lineCount + i12;
                                        f5 = (float) (((lineCount + i23) + i15) - i12);
                                        float f10 = f5;
                                        canvas2.drawRect((float) dp, (float) (i16 - AndroidUtilities.dp(5.0f)), (float) (dp + i12), f5, Theme.chat_actionBackgroundPaint);
                                        canvas2.drawRect((float) i6, (float) (i16 - AndroidUtilities.dp(5.0f)), (float) (i6 + i12), f10, Theme.chat_actionBackgroundPaint);
                                    } else if (!(obj3 == null && obj == null)) {
                                        f7 = (float) (lineCount + i12);
                                        f2 = (float) (((lineCount + i23) + i15) - i12);
                                        canvas2.drawRect((float) dp, f7, (float) (dp + i12), f2, Theme.chat_actionBackgroundPaint);
                                        canvas2.drawRect((float) i6, f7, (float) (i6 + i12), f2, Theme.chat_actionBackgroundPaint);
                                    }
                                }
                                if (obj3 == null) {
                                    measuredWidth = lineCount + i12;
                                    Theme.chat_cornerOuter[0].setBounds(dp, lineCount, dp + i12, measuredWidth);
                                    Theme.chat_cornerOuter[0].draw(canvas2);
                                    Theme.chat_cornerOuter[1].setBounds(i6, lineCount, i6 + i12, measuredWidth);
                                    Theme.chat_cornerOuter[1].draw(canvas2);
                                }
                                if (obj == null) {
                                    i2 = ((lineCount + i23) + i15) - i12;
                                    i3 = i2 + i12;
                                    Theme.chat_cornerOuter[2].setBounds(i6, i2, i6 + i12, i3);
                                    Theme.chat_cornerOuter[2].draw(canvas2);
                                    Theme.chat_cornerOuter[3].setBounds(dp, i2, dp + i12, i3);
                                    Theme.chat_cornerOuter[3].draw(canvas2);
                                }
                                i3 = i11 + 1;
                                dp4 = lineCount + i23;
                                f9 = f8;
                                f = f4;
                                i2 = i14;
                                dp = i12;
                                lineCount = i21;
                                chatActionCell = this;
                            }
                        }
                        canvas2.drawRect((float) (i6 + dp2), (float) dp, (float) ((i6 + i9) - dp2), (float) (dp + i25), Theme.chat_actionBackgroundPaint);
                        dp = i6 - i;
                        i6 = (i6 + i9) - dp2;
                        if (obj3 == null) {
                        }
                        f8 = 6.0f;
                        if (obj == null) {
                        }
                        f7 = (float) (lineCount + i12);
                        f2 = (float) (((lineCount + i23) + i15) - i12);
                        canvas2.drawRect((float) dp, f7, (float) (dp + i12), f2, Theme.chat_actionBackgroundPaint);
                        canvas2.drawRect((float) i6, f7, (float) (i6 + i12), f2, Theme.chat_actionBackgroundPaint);
                        if (obj3 == null) {
                            measuredWidth = lineCount + i12;
                            Theme.chat_cornerOuter[0].setBounds(dp, lineCount, dp + i12, measuredWidth);
                            Theme.chat_cornerOuter[0].draw(canvas2);
                            Theme.chat_cornerOuter[1].setBounds(i6, lineCount, i6 + i12, measuredWidth);
                            Theme.chat_cornerOuter[1].draw(canvas2);
                        }
                        if (obj == null) {
                            i2 = ((lineCount + i23) + i15) - i12;
                            i3 = i2 + i12;
                            Theme.chat_cornerOuter[2].setBounds(i6, i2, i6 + i12, i3);
                            Theme.chat_cornerOuter[2].draw(canvas2);
                            Theme.chat_cornerOuter[3].setBounds(dp, i2, dp + i12, i3);
                            Theme.chat_cornerOuter[3].draw(canvas2);
                        }
                        i3 = i11 + 1;
                        dp4 = lineCount + i23;
                        f9 = f8;
                        f = f4;
                        i2 = i14;
                        dp = i12;
                        lineCount = i21;
                        chatActionCell = this;
                    }
                }
                obj = obj2;
                i5 = 0;
                i4 = 0;
                if (obj3 == null) {
                }
                i6 = 0;
                i7 = 0;
                if (i5 != 0) {
                    i9 = findMaxWidthAroundLine;
                    i10 = measuredWidth;
                    i11 = i3;
                    i12 = dp;
                    i13 = i5;
                    i14 = lineBottom;
                    dp = dp4;
                    i5 = i6;
                    i15 = 0;
                } else if (i5 != 1) {
                    i9 = findMaxWidthAroundLine;
                    i16 = measuredWidth;
                    i11 = i3;
                    i17 = i6;
                    i12 = dp;
                    i14 = lineBottom;
                    dp = dp4;
                    if (i5 != 2) {
                        i10 = i16;
                        i13 = i5;
                        i5 = i17;
                        i15 = AndroidUtilities.dp(6.0f);
                    } else {
                        i15 = AndroidUtilities.dp(3.0f);
                        i3 = (dp + i25) - AndroidUtilities.dp(11.0f);
                        measuredWidth = i16 - dp3;
                        i8 = i17;
                        measuredWidth -= i;
                        if (obj3 == null) {
                            if (obj != null) {
                                i19 = measuredWidth;
                                i20 = i8;
                                i6 = i3;
                                measuredWidth = i19;
                                i3 = i6 + dp3;
                                Theme.chat_cornerInner[2].setBounds(measuredWidth, i6, measuredWidth + dp3, i3);
                                Theme.chat_cornerInner[2].draw(canvas2);
                                measuredWidth = i16 + i9;
                                i8 = i20;
                                measuredWidth += i;
                                if (obj3 == null) {
                                    if (obj == null) {
                                        lineBottom = i3;
                                        i10 = i16;
                                        i13 = i5;
                                        i16 = measuredWidth;
                                        i5 = i8;
                                        Theme.chat_cornerInner[3].setBounds(i16, i6, i16 + dp3, lineBottom);
                                        Theme.chat_cornerInner[3].draw(canvas2);
                                    }
                                }
                                i10 = i16;
                                i16 = measuredWidth;
                                i13 = i5;
                                i5 = i8;
                                lineBottom = i3;
                                canvas2.drawRect((float) (measuredWidth - i12), (float) (i6 + AndroidUtilities.dp(3.0f)), (float) measuredWidth, (float) (i6 + i12), Theme.chat_actionBackgroundPaint);
                                Theme.chat_cornerInner[3].setBounds(i16, i6, i16 + dp3, lineBottom);
                                Theme.chat_cornerInner[3].draw(canvas2);
                            }
                        }
                        i2 = measuredWidth + dp3;
                        i19 = measuredWidth;
                        i20 = i8;
                        i6 = i3;
                        canvas2.drawRect((float) i2, (float) (AndroidUtilities.dp(3.0f) + i3), (float) (i2 + i12), (float) (i3 + i12), Theme.chat_actionBackgroundPaint);
                        measuredWidth = i19;
                        i3 = i6 + dp3;
                        Theme.chat_cornerInner[2].setBounds(measuredWidth, i6, measuredWidth + dp3, i3);
                        Theme.chat_cornerInner[2].draw(canvas2);
                        measuredWidth = i16 + i9;
                        i8 = i20;
                        measuredWidth += i;
                        if (obj3 == null) {
                            if (obj == null) {
                                lineBottom = i3;
                                i10 = i16;
                                i13 = i5;
                                i16 = measuredWidth;
                                i5 = i8;
                                Theme.chat_cornerInner[3].setBounds(i16, i6, i16 + dp3, lineBottom);
                                Theme.chat_cornerInner[3].draw(canvas2);
                            }
                        }
                        i10 = i16;
                        i16 = measuredWidth;
                        i13 = i5;
                        i5 = i8;
                        lineBottom = i3;
                        canvas2.drawRect((float) (measuredWidth - i12), (float) (i6 + AndroidUtilities.dp(3.0f)), (float) measuredWidth, (float) (i6 + i12), Theme.chat_actionBackgroundPaint);
                        Theme.chat_cornerInner[3].setBounds(i16, i6, i16 + dp3, lineBottom);
                        Theme.chat_cornerInner[3].draw(canvas2);
                    }
                } else {
                    i2 = (getMeasuredWidth() - i4) / 2;
                    i15 = AndroidUtilities.dp(3.0f);
                    i12 = dp;
                    i14 = lineBottom;
                    lineBottom = i2;
                    dp = dp4;
                    i9 = findMaxWidthAroundLine;
                    i16 = measuredWidth;
                    i17 = i6;
                    f = 3.0f;
                    i11 = i3;
                    if (chatActionCell.isLineBottom(i4, findMaxWidthAroundLine, i3 + 1, lineCount, i)) {
                        i3 = dp + i25;
                        f2 = (float) i3;
                        f3 = f2;
                        i18 = i3;
                        canvas2.drawRect((float) (i16 + dp2), f2, (float) lineBottom, (float) (AndroidUtilities.dp(f) + i3), Theme.chat_actionBackgroundPaint);
                        canvas2.drawRect((float) (lineBottom + i4), f3, (float) ((i16 + i9) - dp2), (float) (i18 + AndroidUtilities.dp(f)), Theme.chat_actionBackgroundPaint);
                    } else {
                        i3 = dp + i25;
                        f2 = (float) i3;
                        f3 = f2;
                        i18 = i3;
                        canvas2.drawRect((float) (i16 + dp2), f2, (float) (lineBottom - i), (float) (AndroidUtilities.dp(f) + i3), Theme.chat_actionBackgroundPaint);
                        canvas2.drawRect((float) ((lineBottom + i4) + i), f3, (float) ((i16 + i9) - dp2), (float) (i18 + AndroidUtilities.dp(f)), Theme.chat_actionBackgroundPaint);
                    }
                    i10 = i16;
                    i13 = i5;
                    i5 = i17;
                }
                if (i5 != 0) {
                    i21 = lineCount;
                    i22 = i5;
                    i6 = i10;
                    i16 = i13;
                    f4 = 11.0f;
                    lineCount = dp;
                    i23 = i25;
                } else if (i5 != 1) {
                    i21 = lineCount;
                    i6 = i10;
                    chatActionCell = this;
                    if (i5 != 2) {
                        i22 = i5;
                        i16 = i13;
                        f4 = 11.0f;
                        i23 = i25 + AndroidUtilities.dp(6.0f);
                        lineCount = dp - AndroidUtilities.dp(6.0f);
                    } else {
                        lineCount = dp - AndroidUtilities.dp(3.0f);
                        lineBottom = i25 + AndroidUtilities.dp(3.0f);
                        i3 = lineCount + AndroidUtilities.dp(6.2f);
                        measuredWidth = i6 - dp3;
                        i8 = i13;
                        measuredWidth -= i;
                        if (obj3 == null) {
                            if (obj != null) {
                                i16 = i8;
                                i22 = i5;
                                i23 = lineBottom;
                                lineBottom = measuredWidth;
                                i5 = i3;
                                i3 = i5 + dp3;
                                Theme.chat_cornerInner[0].setBounds(lineBottom, i5, lineBottom + dp3, i3);
                                Theme.chat_cornerInner[0].draw(canvas2);
                                measuredWidth = i6 + i9;
                                measuredWidth += i;
                                lineBottom = measuredWidth;
                                if (obj3 == null) {
                                    if (obj == null) {
                                        i24 = lineCount;
                                        f4 = 11.0f;
                                        lineCount = i3;
                                        Theme.chat_cornerInner[1].setBounds(lineBottom, i5, lineBottom + dp3, lineCount);
                                        Theme.chat_cornerInner[1].draw(canvas2);
                                        lineCount = i24;
                                    }
                                }
                                f4 = 11.0f;
                                i24 = lineCount;
                                lineCount = i3;
                                canvas2.drawRect((float) (lineBottom - i12), (float) (AndroidUtilities.dp(3.0f) + lineCount), (float) lineBottom, (float) (AndroidUtilities.dp(11.0f) + lineCount), Theme.chat_actionBackgroundPaint);
                                Theme.chat_cornerInner[1].setBounds(lineBottom, i5, lineBottom + dp3, lineCount);
                                Theme.chat_cornerInner[1].draw(canvas2);
                                lineCount = i24;
                            }
                        }
                        i2 = measuredWidth + dp3;
                        i23 = lineBottom;
                        lineBottom = measuredWidth;
                        i16 = i8;
                        i22 = i5;
                        i5 = i3;
                        canvas2.drawRect((float) i2, (float) (lineCount + AndroidUtilities.dp(3.0f)), (float) (i2 + i12), (float) (lineCount + AndroidUtilities.dp(11.0f)), Theme.chat_actionBackgroundPaint);
                        i3 = i5 + dp3;
                        Theme.chat_cornerInner[0].setBounds(lineBottom, i5, lineBottom + dp3, i3);
                        Theme.chat_cornerInner[0].draw(canvas2);
                        measuredWidth = i6 + i9;
                        measuredWidth += i;
                        lineBottom = measuredWidth;
                        if (obj3 == null) {
                            if (obj == null) {
                                i24 = lineCount;
                                f4 = 11.0f;
                                lineCount = i3;
                                Theme.chat_cornerInner[1].setBounds(lineBottom, i5, lineBottom + dp3, lineCount);
                                Theme.chat_cornerInner[1].draw(canvas2);
                                lineCount = i24;
                            }
                        }
                        f4 = 11.0f;
                        i24 = lineCount;
                        lineCount = i3;
                        canvas2.drawRect((float) (lineBottom - i12), (float) (AndroidUtilities.dp(3.0f) + lineCount), (float) lineBottom, (float) (AndroidUtilities.dp(11.0f) + lineCount), Theme.chat_actionBackgroundPaint);
                        Theme.chat_cornerInner[1].setBounds(lineBottom, i5, lineBottom + dp3, lineCount);
                        Theme.chat_cornerInner[1].draw(canvas2);
                        lineCount = i24;
                    }
                } else {
                    i6 = i10;
                    lineBottom = (getMeasuredWidth() - i7) / 2;
                    i4 = i25 + AndroidUtilities.dp(3.0f);
                    i8 = lineCount;
                    i21 = lineCount;
                    lineCount = dp - AndroidUtilities.dp(3.0f);
                    if (isLineTop(i7, i9, i11 - 1, i8, i)) {
                        f5 = (float) lineCount;
                        f6 = f5;
                        canvas2.drawRect((float) (i6 + dp2), f5, (float) lineBottom, (float) (lineCount + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        canvas2.drawRect((float) (lineBottom + i7), f6, (float) ((i6 + i9) - dp2), (float) (lineCount + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                    } else {
                        f5 = (float) lineCount;
                        f6 = f5;
                        canvas2.drawRect((float) (i6 + dp2), f5, (float) (lineBottom - i), (float) (lineCount + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        canvas2.drawRect((float) ((lineBottom + i7) + i), f6, (float) ((i6 + i9) - dp2), (float) (lineCount + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                    }
                    i22 = i5;
                    i23 = i4;
                    i16 = i13;
                    f4 = 11.0f;
                }
                if (obj3 == null) {
                    if (obj != null) {
                        canvas2.drawRect((float) i6, (float) dp, (float) (i6 + i9), (float) (dp + i25), Theme.chat_actionBackgroundPaint);
                        dp = i6 - i;
                        i6 = (i6 + i9) - dp2;
                        if (obj3 == null) {
                        }
                        f8 = 6.0f;
                        if (obj == null) {
                        }
                        f7 = (float) (lineCount + i12);
                        f2 = (float) (((lineCount + i23) + i15) - i12);
                        canvas2.drawRect((float) dp, f7, (float) (dp + i12), f2, Theme.chat_actionBackgroundPaint);
                        canvas2.drawRect((float) i6, f7, (float) (i6 + i12), f2, Theme.chat_actionBackgroundPaint);
                        if (obj3 == null) {
                            measuredWidth = lineCount + i12;
                            Theme.chat_cornerOuter[0].setBounds(dp, lineCount, dp + i12, measuredWidth);
                            Theme.chat_cornerOuter[0].draw(canvas2);
                            Theme.chat_cornerOuter[1].setBounds(i6, lineCount, i6 + i12, measuredWidth);
                            Theme.chat_cornerOuter[1].draw(canvas2);
                        }
                        if (obj == null) {
                            i2 = ((lineCount + i23) + i15) - i12;
                            i3 = i2 + i12;
                            Theme.chat_cornerOuter[2].setBounds(i6, i2, i6 + i12, i3);
                            Theme.chat_cornerOuter[2].draw(canvas2);
                            Theme.chat_cornerOuter[3].setBounds(dp, i2, dp + i12, i3);
                            Theme.chat_cornerOuter[3].draw(canvas2);
                        }
                        i3 = i11 + 1;
                        dp4 = lineCount + i23;
                        f9 = f8;
                        f = f4;
                        i2 = i14;
                        dp = i12;
                        lineCount = i21;
                        chatActionCell = this;
                    }
                }
                canvas2.drawRect((float) (i6 + dp2), (float) dp, (float) ((i6 + i9) - dp2), (float) (dp + i25), Theme.chat_actionBackgroundPaint);
                dp = i6 - i;
                i6 = (i6 + i9) - dp2;
                if (obj3 == null) {
                }
                f8 = 6.0f;
                if (obj == null) {
                }
                f7 = (float) (lineCount + i12);
                f2 = (float) (((lineCount + i23) + i15) - i12);
                canvas2.drawRect((float) dp, f7, (float) (dp + i12), f2, Theme.chat_actionBackgroundPaint);
                canvas2.drawRect((float) i6, f7, (float) (i6 + i12), f2, Theme.chat_actionBackgroundPaint);
                if (obj3 == null) {
                    measuredWidth = lineCount + i12;
                    Theme.chat_cornerOuter[0].setBounds(dp, lineCount, dp + i12, measuredWidth);
                    Theme.chat_cornerOuter[0].draw(canvas2);
                    Theme.chat_cornerOuter[1].setBounds(i6, lineCount, i6 + i12, measuredWidth);
                    Theme.chat_cornerOuter[1].draw(canvas2);
                }
                if (obj == null) {
                    i2 = ((lineCount + i23) + i15) - i12;
                    i3 = i2 + i12;
                    Theme.chat_cornerOuter[2].setBounds(i6, i2, i6 + i12, i3);
                    Theme.chat_cornerOuter[2].draw(canvas2);
                    Theme.chat_cornerOuter[3].setBounds(dp, i2, dp + i12, i3);
                    Theme.chat_cornerOuter[3].draw(canvas2);
                }
                i3 = i11 + 1;
                dp4 = lineCount + i23;
                f9 = f8;
                f = f4;
                i2 = i14;
                dp = i12;
                lineCount = i21;
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
}
