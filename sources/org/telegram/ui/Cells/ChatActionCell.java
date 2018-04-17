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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC.KeyboardButton;
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

    public ChatActionCell(Context context) {
        super(context);
        this.imageReceiver.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setDelegate(ChatActionCellDelegate delegate) {
        this.delegate = delegate;
    }

    public void setCustomDate(int date) {
        if (this.customDate != date) {
            CharSequence newText = LocaleController.formatDateChat((long) date);
            if (this.customText == null || !TextUtils.equals(newText, this.customText)) {
                this.previousWidth = 0;
                this.customDate = date;
                this.customText = newText;
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
                int id = 0;
                if (messageObject.messageOwner.to_id != null) {
                    if (messageObject.messageOwner.to_id.chat_id != 0) {
                        id = messageObject.messageOwner.to_id.chat_id;
                    } else if (messageObject.messageOwner.to_id.channel_id != 0) {
                        id = messageObject.messageOwner.to_id.channel_id;
                    } else {
                        id = messageObject.messageOwner.to_id.user_id;
                        if (id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            id = messageObject.messageOwner.from_id;
                        }
                    }
                }
                this.avatarDrawable.setInfo(id, null, null, false);
                if (this.currentMessageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage(this.currentMessageObject.messageOwner.action.newUserPhoto.photo_small, "50_50", this.avatarDrawable, null, 0);
                } else {
                    PhotoSize photo = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.dp(64.0f));
                    if (photo != null) {
                        this.imageReceiver.setImage(photo.location, "50_50", this.avatarDrawable, null, 0);
                    } else {
                        this.imageReceiver.setImageBitmap(this.avatarDrawable);
                    }
                }
                this.imageReceiver.setVisible(true ^ PhotoViewer.isShowingImage(this.currentMessageObject), false);
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

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(event);
        }
        float x = event.getX();
        float y = event.getY();
        boolean result = false;
        if (event.getAction() != 0) {
            if (event.getAction() != 2) {
                cancelCheckLongPress();
            }
            if (this.imagePressed) {
                if (event.getAction() == 1) {
                    this.imagePressed = false;
                    if (this.delegate != null) {
                        this.delegate.didClickedImage(this);
                        playSoundEffect(0);
                    }
                } else if (event.getAction() == 3) {
                    this.imagePressed = false;
                } else if (event.getAction() == 2 && !this.imageReceiver.isInsideImage(x, y)) {
                    this.imagePressed = false;
                }
            }
        } else if (this.delegate != null) {
            if (this.currentMessageObject.type == 11 && this.imageReceiver.isInsideImage(x, y)) {
                this.imagePressed = true;
                result = true;
            }
            if (result) {
                startCheckLongPress();
            }
        }
        if (!result && (event.getAction() == 0 || (this.pressedLink != null && event.getAction() == 1))) {
            if (x < ((float) this.textX) || y < ((float) this.textY) || x > ((float) (this.textX + this.textWidth)) || y > ((float) (this.textY + this.textHeight))) {
                this.pressedLink = null;
            } else {
                x -= (float) this.textXLeft;
                int line = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                int off = this.textLayout.getOffsetForHorizontal(line, x);
                float left = this.textLayout.getLineLeft(line);
                if (left > x || this.textLayout.getLineWidth(line) + left < x || !(this.currentMessageObject.messageText instanceof Spannable)) {
                    this.pressedLink = null;
                } else {
                    URLSpan[] link = (URLSpan[]) this.currentMessageObject.messageText.getSpans(off, off, URLSpan.class);
                    if (link.length == 0) {
                        this.pressedLink = null;
                    } else if (event.getAction() == 0) {
                        this.pressedLink = link[0];
                        result = true;
                    } else if (link[0] == this.pressedLink) {
                        if (this.delegate != null) {
                            String url = link[0].getURL();
                            if (url.startsWith("game")) {
                                this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
                            } else if (url.startsWith("http")) {
                                Browser.openUrl(getContext(), url);
                            } else {
                                this.delegate.needOpenUserProfile(Integer.parseInt(url));
                            }
                        }
                        result = true;
                    }
                }
            }
        }
        if (!result) {
            result = super.onTouchEvent(event);
        }
        return result;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createLayout(CharSequence text, int width) {
        int maxWidth = width - AndroidUtilities.dp(30.0f);
        this.textLayout = new StaticLayout(text, Theme.chat_actionTextPaint, maxWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        int a = 0;
        this.textHeight = 0;
        this.textWidth = 0;
        int linesCount = this.textLayout.getLineCount();
        while (a < linesCount) {
            try {
                float lineWidth = this.textLayout.getLineWidth(a);
                if (lineWidth > ((float) maxWidth)) {
                    lineWidth = (float) maxWidth;
                }
                this.textHeight = (int) Math.max((double) this.textHeight, Math.ceil((double) this.textLayout.getLineBottom(a)));
                this.textWidth = (int) Math.max((double) this.textWidth, Math.ceil((double) lineWidth));
                a++;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return;
            }
        }
        this.textX = (width - this.textWidth) / 2;
        this.textY = AndroidUtilities.dp(7.0f);
        this.textXLeft = (width - this.textLayout.getWidth()) / 2;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.currentMessageObject == null && this.customText == null) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), this.textHeight + AndroidUtilities.dp(14.0f));
            return;
        }
        int width = Math.max(AndroidUtilities.dp(30.0f), MeasureSpec.getSize(widthMeasureSpec));
        if (width != this.previousWidth) {
            CharSequence text;
            if (this.currentMessageObject == null) {
                text = this.customText;
            } else if (this.currentMessageObject.messageOwner == null || this.currentMessageObject.messageOwner.media == null || this.currentMessageObject.messageOwner.media.ttl_seconds == 0) {
                text = this.currentMessageObject.messageText;
            } else if (this.currentMessageObject.messageOwner.media.photo instanceof TL_photoEmpty) {
                text = LocaleController.getString("AttachPhotoExpired", R.string.AttachPhotoExpired);
            } else if (this.currentMessageObject.messageOwner.media.document instanceof TL_documentEmpty) {
                text = LocaleController.getString("AttachVideoExpired", R.string.AttachVideoExpired);
            } else {
                text = this.currentMessageObject.messageText;
            }
            this.previousWidth = width;
            createLayout(text, width);
            if (this.currentMessageObject != null && this.currentMessageObject.type == 11) {
                this.imageReceiver.setImageCoords((width - AndroidUtilities.dp(64.0f)) / 2, this.textHeight + AndroidUtilities.dp(15.0f), AndroidUtilities.dp(64.0f), AndroidUtilities.dp(64.0f));
            }
        }
        int i = this.textHeight;
        int i2 = (this.currentMessageObject == null || this.currentMessageObject.type != 11) ? 0 : 70;
        setMeasuredDimension(width, i + AndroidUtilities.dp((float) (14 + i2)));
    }

    public int getCustomDate() {
        return this.customDate;
    }

    private int findMaxWidthAroundLine(int line) {
        int a;
        int width = (int) Math.ceil((double) this.textLayout.getLineWidth(line));
        int count = this.textLayout.getLineCount();
        for (a = line + 1; a < count; a++) {
            int w = (int) Math.ceil((double) this.textLayout.getLineWidth(a));
            if (Math.abs(w - width) >= AndroidUtilities.dp(10.0f)) {
                break;
            }
            width = Math.max(w, width);
        }
        for (a = line - 1; a >= 0; a--) {
            w = (int) Math.ceil((double) this.textLayout.getLineWidth(a));
            if (Math.abs(w - width) >= AndroidUtilities.dp(10.0f)) {
                break;
            }
            width = Math.max(w, width);
        }
        return width;
    }

    private boolean isLineTop(int prevWidth, int currentWidth, int line, int count, int cornerRest) {
        if (line != 0) {
            if (line < 0 || line >= count || findMaxWidthAroundLine(line - 1) + (cornerRest * 3) >= prevWidth) {
                return false;
            }
        }
        return true;
    }

    private boolean isLineBottom(int nextWidth, int currentWidth, int line, int count, int cornerRest) {
        if (line != count - 1) {
            if (line < 0 || line > count - 1 || findMaxWidthAroundLine(line + 1) + (cornerRest * 3) >= nextWidth) {
                return false;
            }
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.currentMessageObject != null && r6.currentMessageObject.type == 11) {
            ChatActionCell chatActionCell;
            chatActionCell.imageReceiver.draw(canvas2);
        }
        if (chatActionCell.textLayout != null) {
            int count = chatActionCell.textLayout.getLineCount();
            int corner = AndroidUtilities.dp(11.0f);
            int cornerOffset = AndroidUtilities.dp(6.0f);
            int cornerRest = corner - cornerOffset;
            int cornerIn = AndroidUtilities.dp(8.0f);
            int previousLineBottom = 0;
            int y = AndroidUtilities.dp(NUM);
            int a = 0;
            while (true) {
                int a2 = a;
                int corner2;
                if (a2 < count) {
                    int nextLineWidth;
                    int width;
                    int yOld;
                    int drawInnerTop;
                    int nextX;
                    int count2;
                    int i;
                    int i2;
                    int i3;
                    a = chatActionCell.findMaxWidthAroundLine(a2);
                    int x = ((getMeasuredWidth() - a) - cornerRest) / 2;
                    a += cornerRest;
                    int lineBottom = chatActionCell.textLayout.getLineBottom(a2);
                    int height = lineBottom - previousLineBottom;
                    int additionalHeight = 0;
                    int previousLineBottom2 = lineBottom;
                    boolean z = a2 == count + -1;
                    boolean drawTopCorners = a2 == 0;
                    if (drawTopCorners) {
                        y -= AndroidUtilities.dp(3.0f);
                        height += AndroidUtilities.dp(3.0f);
                    }
                    int y2 = y;
                    if (z) {
                        height += AndroidUtilities.dp(3.0f);
                    }
                    y = y2;
                    int hOld = height;
                    boolean drawBottomCorners = false;
                    int drawInnerTop2 = 0;
                    int nextLineWidth2 = 0;
                    int prevLineWidth = 0;
                    if (!z && a2 + 1 < count) {
                        nextLineWidth = chatActionCell.findMaxWidthAroundLine(a2 + 1) + cornerRest;
                        if (nextLineWidth + (cornerRest * 2) < a) {
                            drawBottomCorners = true;
                            z = true;
                        } else if ((cornerRest * 2) + a < nextLineWidth) {
                            drawBottomCorners = true;
                        } else {
                            drawBottomCorners = true;
                        }
                        nextLineWidth2 = nextLineWidth;
                    }
                    boolean drawInnerBottom = drawBottomCorners;
                    drawBottomCorners = z;
                    if (!drawTopCorners && a2 > 0) {
                        previousLineBottom = chatActionCell.findMaxWidthAroundLine(a2 - 1) + cornerRest;
                        if ((cornerRest * 2) + previousLineBottom < a) {
                            drawInnerTop2 = 1;
                            drawTopCorners = true;
                        } else if ((cornerRest * 2) + a < previousLineBottom) {
                            drawInnerTop2 = 2;
                        } else {
                            drawInnerTop2 = 3;
                        }
                        prevLineWidth = previousLineBottom;
                    }
                    int drawInnerTop3 = drawInnerTop2;
                    int i4;
                    if (!drawInnerBottom) {
                        width = a;
                        yOld = y;
                        drawInnerTop = drawInnerTop3;
                        drawInnerTop2 = a2;
                        corner2 = corner;
                        i4 = lineBottom;
                        lineBottom = x;
                    } else if (drawInnerBottom) {
                        nextX = (getMeasuredWidth() - nextLineWidth2) / 2;
                        additionalHeight = AndroidUtilities.dp(3.0f);
                        width = a;
                        yOld = y;
                        lineBottom = x;
                        drawInnerTop = drawInnerTop3;
                        corner2 = corner;
                        corner = 2;
                        drawInnerTop2 = a2;
                        if (chatActionCell.isLineBottom(nextLineWidth2, width, a2 + 1, count, cornerRest)) {
                            canvas2.drawRect((float) (lineBottom + cornerOffset), (float) (y2 + height), (float) (nextX - cornerRest), (float) ((y2 + height) + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                            canvas2.drawRect((float) ((nextX + nextLineWidth2) + cornerRest), (float) (y2 + height), (float) ((lineBottom + width) - cornerOffset), (float) ((y2 + height) + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        } else {
                            canvas2.drawRect((float) (lineBottom + cornerOffset), (float) (y2 + height), (float) nextX, (float) ((y2 + height) + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                            canvas2.drawRect((float) (nextX + nextLineWidth2), (float) (y2 + height), (float) ((lineBottom + width) - cornerOffset), (float) ((y2 + height) + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        }
                    } else {
                        width = a;
                        yOld = y;
                        drawInnerTop = drawInnerTop3;
                        drawInnerTop2 = a2;
                        corner2 = corner;
                        i4 = lineBottom;
                        lineBottom = x;
                        if (drawInnerBottom) {
                            int dx;
                            additionalHeight = AndroidUtilities.dp(3.0f);
                            nextX = (y2 + height) - AndroidUtilities.dp(11.0f);
                            x = lineBottom - cornerIn;
                            if (!(drawInnerTop == 2 || drawInnerTop == 3)) {
                                x -= cornerRest;
                            }
                            a2 = x;
                            if (!drawTopCorners) {
                                if (!drawBottomCorners) {
                                    dx = a2;
                                    x = dx;
                                    Theme.chat_cornerInner[2].setBounds(x, nextX, x + cornerIn, nextX + cornerIn);
                                    Theme.chat_cornerInner[2].draw(canvas2);
                                    x = lineBottom + width;
                                    if (!(drawInnerTop == 2 || drawInnerTop == 3)) {
                                        x += cornerRest;
                                    }
                                    a2 = x;
                                    if (!drawTopCorners) {
                                        if (drawBottomCorners) {
                                            corner = a2;
                                            Theme.chat_cornerInner[3].setBounds(corner, nextX, corner + cornerIn, nextX + cornerIn);
                                            Theme.chat_cornerInner[3].draw(canvas2);
                                        }
                                    }
                                    corner = a2;
                                    canvas2.drawRect((float) (a2 - corner2), (float) (AndroidUtilities.dp(3.0f) + nextX), (float) a2, (float) (nextX + corner2), Theme.chat_actionBackgroundPaint);
                                    Theme.chat_cornerInner[3].setBounds(corner, nextX, corner + cornerIn, nextX + cornerIn);
                                    Theme.chat_cornerInner[3].draw(canvas2);
                                }
                            }
                            dx = a2;
                            canvas2.drawRect((float) (a2 + cornerIn), (float) (AndroidUtilities.dp(3.0f) + nextX), (float) ((a2 + cornerIn) + corner2), (float) (nextX + corner2), Theme.chat_actionBackgroundPaint);
                            x = dx;
                            Theme.chat_cornerInner[2].setBounds(x, nextX, x + cornerIn, nextX + cornerIn);
                            Theme.chat_cornerInner[2].draw(canvas2);
                            x = lineBottom + width;
                            x += cornerRest;
                            a2 = x;
                            if (drawTopCorners) {
                                if (drawBottomCorners) {
                                    corner = a2;
                                    Theme.chat_cornerInner[3].setBounds(corner, nextX, corner + cornerIn, nextX + cornerIn);
                                    Theme.chat_cornerInner[3].draw(canvas2);
                                }
                            }
                            corner = a2;
                            canvas2.drawRect((float) (a2 - corner2), (float) (AndroidUtilities.dp(3.0f) + nextX), (float) a2, (float) (nextX + corner2), Theme.chat_actionBackgroundPaint);
                            Theme.chat_cornerInner[3].setBounds(corner, nextX, corner + cornerIn, nextX + cornerIn);
                            Theme.chat_cornerInner[3].draw(canvas2);
                        } else {
                            additionalHeight = AndroidUtilities.dp(6.0f);
                        }
                    }
                    if (drawInnerTop == 0) {
                        corner = drawInnerTop;
                        count2 = count;
                        chatActionCell = this;
                        i = NUM;
                        count = y2;
                    } else if (drawInnerTop == 1) {
                        corner = drawInnerTop;
                        nextX = (getMeasuredWidth() - prevLineWidth) / 2;
                        height += AndroidUtilities.dp(3.0f);
                        drawInnerTop3 = count;
                        count2 = count;
                        count = y2 - AndroidUtilities.dp(3.0f);
                        if (isLineTop(prevLineWidth, width, drawInnerTop2 - 1, drawInnerTop3, cornerRest)) {
                            canvas2.drawRect((float) (lineBottom + cornerOffset), (float) count, (float) (nextX - cornerRest), (float) (count + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                            canvas2.drawRect((float) ((nextX + prevLineWidth) + cornerRest), (float) count, (float) ((lineBottom + width) - cornerOffset), (float) (count + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        } else {
                            canvas2.drawRect((float) (lineBottom + cornerOffset), (float) count, (float) nextX, (float) (count + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                            canvas2.drawRect((float) (nextX + prevLineWidth), (float) count, (float) ((lineBottom + width) - cornerOffset), (float) (count + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        }
                        i = NUM;
                    } else {
                        corner = drawInnerTop;
                        count2 = count;
                        chatActionCell = this;
                        if (corner == 2) {
                            y2 -= AndroidUtilities.dp(3.0f);
                            height += AndroidUtilities.dp(3.0f);
                            count = y2 + AndroidUtilities.dp(6.2f);
                            x = lineBottom - cornerIn;
                            if (!(drawInnerBottom || drawInnerBottom)) {
                                x -= cornerRest;
                            }
                            nextX = x;
                            if (drawTopCorners || drawBottomCorners) {
                                canvas2.drawRect((float) (nextX + cornerIn), (float) (y2 + AndroidUtilities.dp(3.0f)), (float) ((nextX + cornerIn) + corner2), (float) (y2 + AndroidUtilities.dp(11.0f)), Theme.chat_actionBackgroundPaint);
                            }
                            Theme.chat_cornerInner[0].setBounds(nextX, count, nextX + cornerIn, count + cornerIn);
                            Theme.chat_cornerInner[0].draw(canvas2);
                            x = lineBottom + width;
                            if (!(drawInnerBottom || drawInnerBottom)) {
                                x += cornerRest;
                            }
                            nextX = x;
                            if (!drawTopCorners) {
                                if (!drawBottomCorners) {
                                    i = NUM;
                                    Theme.chat_cornerInner[1].setBounds(nextX, count, nextX + cornerIn, count + cornerIn);
                                    Theme.chat_cornerInner[1].draw(canvas2);
                                }
                            }
                            i = NUM;
                            canvas2.drawRect((float) (nextX - corner2), (float) (y2 + AndroidUtilities.dp(3.0f)), (float) nextX, (float) (y2 + AndroidUtilities.dp(11.0f)), Theme.chat_actionBackgroundPaint);
                            Theme.chat_cornerInner[1].setBounds(nextX, count, nextX + cornerIn, count + cornerIn);
                            Theme.chat_cornerInner[1].draw(canvas2);
                        } else {
                            i = NUM;
                            y2 -= AndroidUtilities.dp(6.0f);
                            height += AndroidUtilities.dp(6.0f);
                        }
                        count = y2;
                    }
                    if (drawTopCorners) {
                        nextX = yOld;
                    } else if (drawBottomCorners) {
                        nextX = yOld;
                    } else {
                        nextX = yOld;
                        canvas2.drawRect((float) lineBottom, (float) nextX, (float) (lineBottom + width), (float) (nextX + hOld), Theme.chat_actionBackgroundPaint);
                        a2 = lineBottom - cornerRest;
                        drawInnerTop3 = (lineBottom + width) - cornerOffset;
                        if (drawTopCorners || drawBottomCorners || drawInnerBottom) {
                            i2 = nextX;
                            i3 = NUM;
                            nextLineWidth = drawInnerTop3;
                            nextX = a2;
                            if (drawBottomCorners || drawTopCorners || corner == 2) {
                                if (drawTopCorners || drawBottomCorners) {
                                    canvas2.drawRect((float) nextX, (float) (count + corner2), (float) (nextX + corner2), (float) (((count + height) + additionalHeight) - corner2), Theme.chat_actionBackgroundPaint);
                                    canvas2.drawRect((float) nextLineWidth, (float) (count + corner2), (float) (nextLineWidth + corner2), (float) (((count + height) + additionalHeight) - corner2), Theme.chat_actionBackgroundPaint);
                                }
                            } else {
                                float f = 5.0f;
                                canvas2.drawRect((float) nextX, (float) ((count + corner2) - AndroidUtilities.dp(5.0f)), (float) (nextX + corner2), (float) (((count + height) + additionalHeight) - corner2), Theme.chat_actionBackgroundPaint);
                                canvas2.drawRect((float) nextLineWidth, (float) ((count + corner2) - AndroidUtilities.dp(f)), (float) (nextLineWidth + corner2), (float) (((count + height) + additionalHeight) - corner2), Theme.chat_actionBackgroundPaint);
                            }
                        } else {
                            nextLineWidth = drawInnerTop3;
                            nextX = a2;
                            canvas2.drawRect((float) a2, (float) (count + corner2), (float) (a2 + corner2), (float) (((count + height) + additionalHeight) - AndroidUtilities.dp(NUM)), Theme.chat_actionBackgroundPaint);
                            i3 = NUM;
                            canvas2.drawRect((float) nextLineWidth, (float) (count + corner2), (float) (nextLineWidth + corner2), (float) (((count + height) + additionalHeight) - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                            int i5 = corner;
                        }
                        if (drawTopCorners) {
                            Theme.chat_cornerOuter[0].setBounds(nextX, count, nextX + corner2, count + corner2);
                            Theme.chat_cornerOuter[0].draw(canvas2);
                            Theme.chat_cornerOuter[1].setBounds(nextLineWidth, count, nextLineWidth + corner2, count + corner2);
                            Theme.chat_cornerOuter[1].draw(canvas2);
                        }
                        if (drawBottomCorners) {
                            a = ((count + height) + additionalHeight) - corner2;
                            Theme.chat_cornerOuter[2].setBounds(nextLineWidth, a, nextLineWidth + corner2, a + corner2);
                            Theme.chat_cornerOuter[2].draw(canvas2);
                            Theme.chat_cornerOuter[3].setBounds(nextX, a, nextX + corner2, a + corner2);
                            Theme.chat_cornerOuter[3].draw(canvas2);
                        }
                        y = count + height;
                        a = drawInnerTop2 + 1;
                        previousLineBottom = previousLineBottom2;
                        nextX = i3;
                        nextLineWidth = i;
                        corner = corner2;
                        count = count2;
                    }
                    canvas2.drawRect((float) (lineBottom + cornerOffset), (float) nextX, (float) ((lineBottom + width) - cornerOffset), (float) (nextX + hOld), Theme.chat_actionBackgroundPaint);
                    a2 = lineBottom - cornerRest;
                    drawInnerTop3 = (lineBottom + width) - cornerOffset;
                    if (drawTopCorners) {
                    }
                    i2 = nextX;
                    i3 = NUM;
                    nextLineWidth = drawInnerTop3;
                    nextX = a2;
                    if (drawBottomCorners) {
                    }
                    canvas2.drawRect((float) nextX, (float) (count + corner2), (float) (nextX + corner2), (float) (((count + height) + additionalHeight) - corner2), Theme.chat_actionBackgroundPaint);
                    canvas2.drawRect((float) nextLineWidth, (float) (count + corner2), (float) (nextLineWidth + corner2), (float) (((count + height) + additionalHeight) - corner2), Theme.chat_actionBackgroundPaint);
                    if (drawTopCorners) {
                        Theme.chat_cornerOuter[0].setBounds(nextX, count, nextX + corner2, count + corner2);
                        Theme.chat_cornerOuter[0].draw(canvas2);
                        Theme.chat_cornerOuter[1].setBounds(nextLineWidth, count, nextLineWidth + corner2, count + corner2);
                        Theme.chat_cornerOuter[1].draw(canvas2);
                    }
                    if (drawBottomCorners) {
                        a = ((count + height) + additionalHeight) - corner2;
                        Theme.chat_cornerOuter[2].setBounds(nextLineWidth, a, nextLineWidth + corner2, a + corner2);
                        Theme.chat_cornerOuter[2].draw(canvas2);
                        Theme.chat_cornerOuter[3].setBounds(nextX, a, nextX + corner2, a + corner2);
                        Theme.chat_cornerOuter[3].draw(canvas2);
                    }
                    y = count + height;
                    a = drawInnerTop2 + 1;
                    previousLineBottom = previousLineBottom2;
                    nextX = i3;
                    nextLineWidth = i;
                    corner = corner2;
                    count = count2;
                } else {
                    corner2 = corner;
                    canvas.save();
                    canvas2.translate((float) chatActionCell.textXLeft, (float) chatActionCell.textY);
                    chatActionCell.textLayout.draw(canvas2);
                    canvas.restore();
                    return;
                }
            }
        }
    }
}
