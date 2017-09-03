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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        ChatActionCell.this.requestLayout();
                    }
                });
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        boolean z = true;
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
                        if (id == UserConfig.getClientUserId()) {
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
                ImageReceiver imageReceiver = this.imageReceiver;
                if (PhotoViewer.getInstance().isShowingImage(this.currentMessageObject)) {
                    z = false;
                }
                imageReceiver.setVisible(z, false);
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
                            } else {
                                this.delegate.needOpenUserProfile(Integer.parseInt(url));
                            }
                        }
                        result = true;
                    }
                }
            }
        }
        if (result) {
            return result;
        }
        return super.onTouchEvent(event);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createLayout(CharSequence text, int width) {
        int maxWidth = width - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        this.textLayout = new StaticLayout(text, Theme.chat_actionTextPaint, maxWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        this.textHeight = 0;
        this.textWidth = 0;
        int linesCount = this.textLayout.getLineCount();
        int a = 0;
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
                FileLog.e(e);
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
        int i;
        int width = Math.max(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), MeasureSpec.getSize(widthMeasureSpec));
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
        int i2 = this.textHeight;
        if (this.currentMessageObject == null || this.currentMessageObject.type != 11) {
            i = 0;
        } else {
            i = 70;
        }
        setMeasuredDimension(width, AndroidUtilities.dp((float) (i + 14)) + i2);
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
        return line == 0 || (line >= 0 && line < count && findMaxWidthAroundLine(line - 1) + (cornerRest * 3) < prevWidth);
    }

    private boolean isLineBottom(int nextWidth, int currentWidth, int line, int count, int cornerRest) {
        return line == count + -1 || (line >= 0 && line <= count - 1 && findMaxWidthAroundLine(line + 1) + (cornerRest * 3) < nextWidth);
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentMessageObject != null && this.currentMessageObject.type == 11) {
            this.imageReceiver.draw(canvas);
        }
        if (this.textLayout != null) {
            int count = this.textLayout.getLineCount();
            int corner = AndroidUtilities.dp(11.0f);
            int cornerOffset = AndroidUtilities.dp(6.0f);
            int cornerRest = corner - cornerOffset;
            int cornerIn = AndroidUtilities.dp(8.0f);
            int y = AndroidUtilities.dp(7.0f);
            int previousLineBottom = 0;
            int a = 0;
            while (a < count) {
                int dy;
                int dx;
                int width = findMaxWidthAroundLine(a);
                int x = ((getMeasuredWidth() - width) - cornerRest) / 2;
                width += cornerRest;
                int lineBottom = this.textLayout.getLineBottom(a);
                int height = lineBottom - previousLineBottom;
                int additionalHeight = 0;
                previousLineBottom = lineBottom;
                boolean drawBottomCorners = a == count + -1;
                boolean drawTopCorners = a == 0;
                if (drawTopCorners) {
                    y -= AndroidUtilities.dp(3.0f);
                    height += AndroidUtilities.dp(3.0f);
                }
                if (drawBottomCorners) {
                    height += AndroidUtilities.dp(3.0f);
                }
                int yOld = y;
                int hOld = height;
                int drawInnerBottom = 0;
                int drawInnerTop = 0;
                int nextLineWidth = 0;
                int prevLineWidth = 0;
                if (!drawBottomCorners && a + 1 < count) {
                    nextLineWidth = findMaxWidthAroundLine(a + 1) + cornerRest;
                    if ((cornerRest * 2) + nextLineWidth < width) {
                        drawInnerBottom = 1;
                        drawBottomCorners = true;
                    } else {
                        drawInnerBottom = (cornerRest * 2) + width < nextLineWidth ? 2 : 3;
                    }
                }
                if (!drawTopCorners && a > 0) {
                    prevLineWidth = findMaxWidthAroundLine(a - 1) + cornerRest;
                    if ((cornerRest * 2) + prevLineWidth < width) {
                        drawInnerTop = 1;
                        drawTopCorners = true;
                    } else {
                        drawInnerTop = (cornerRest * 2) + width < prevLineWidth ? 2 : 3;
                    }
                }
                if (drawInnerBottom != 0) {
                    if (drawInnerBottom == 1) {
                        int nextX = (getMeasuredWidth() - nextLineWidth) / 2;
                        additionalHeight = AndroidUtilities.dp(3.0f);
                        if (isLineBottom(nextLineWidth, width, a + 1, count, cornerRest)) {
                            canvas.drawRect((float) (x + cornerOffset), (float) (y + height), (float) (nextX - cornerRest), (float) ((y + height) + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                            canvas.drawRect((float) ((nextX + nextLineWidth) + cornerRest), (float) (y + height), (float) ((x + width) - cornerOffset), (float) ((y + height) + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        } else {
                            canvas.drawRect((float) (x + cornerOffset), (float) (y + height), (float) nextX, (float) ((y + height) + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                            canvas.drawRect((float) (nextX + nextLineWidth), (float) (y + height), (float) ((x + width) - cornerOffset), (float) ((y + height) + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        }
                    } else if (drawInnerBottom == 2) {
                        additionalHeight = AndroidUtilities.dp(3.0f);
                        dy = (y + height) - AndroidUtilities.dp(11.0f);
                        dx = x - cornerIn;
                        if (!(drawInnerTop == 2 || drawInnerTop == 3)) {
                            dx -= cornerRest;
                        }
                        if (drawTopCorners || drawBottomCorners) {
                            canvas.drawRect((float) (dx + cornerIn), (float) (AndroidUtilities.dp(3.0f) + dy), (float) ((dx + cornerIn) + corner), (float) (dy + corner), Theme.chat_actionBackgroundPaint);
                        }
                        Theme.chat_cornerInner[2].setBounds(dx, dy, dx + cornerIn, dy + cornerIn);
                        Theme.chat_cornerInner[2].draw(canvas);
                        dx = x + width;
                        if (!(drawInnerTop == 2 || drawInnerTop == 3)) {
                            dx += cornerRest;
                        }
                        if (drawTopCorners || drawBottomCorners) {
                            canvas.drawRect((float) (dx - corner), (float) (AndroidUtilities.dp(3.0f) + dy), (float) dx, (float) (dy + corner), Theme.chat_actionBackgroundPaint);
                        }
                        Theme.chat_cornerInner[3].setBounds(dx, dy, dx + cornerIn, dy + cornerIn);
                        Theme.chat_cornerInner[3].draw(canvas);
                    } else {
                        additionalHeight = AndroidUtilities.dp(6.0f);
                    }
                }
                if (drawInnerTop != 0) {
                    if (drawInnerTop == 1) {
                        int prevX = (getMeasuredWidth() - prevLineWidth) / 2;
                        y -= AndroidUtilities.dp(3.0f);
                        height += AndroidUtilities.dp(3.0f);
                        if (isLineTop(prevLineWidth, width, a - 1, count, cornerRest)) {
                            canvas.drawRect((float) (x + cornerOffset), (float) y, (float) (prevX - cornerRest), (float) (AndroidUtilities.dp(3.0f) + y), Theme.chat_actionBackgroundPaint);
                            canvas.drawRect((float) ((prevX + prevLineWidth) + cornerRest), (float) y, (float) ((x + width) - cornerOffset), (float) (AndroidUtilities.dp(3.0f) + y), Theme.chat_actionBackgroundPaint);
                        } else {
                            canvas.drawRect((float) (x + cornerOffset), (float) y, (float) prevX, (float) (AndroidUtilities.dp(3.0f) + y), Theme.chat_actionBackgroundPaint);
                            canvas.drawRect((float) (prevX + prevLineWidth), (float) y, (float) ((x + width) - cornerOffset), (float) (AndroidUtilities.dp(3.0f) + y), Theme.chat_actionBackgroundPaint);
                        }
                    } else if (drawInnerTop == 2) {
                        y -= AndroidUtilities.dp(3.0f);
                        height += AndroidUtilities.dp(3.0f);
                        dy = y + AndroidUtilities.dp(6.2f);
                        dx = x - cornerIn;
                        if (!(drawInnerBottom == 2 || drawInnerBottom == 3)) {
                            dx -= cornerRest;
                        }
                        if (drawTopCorners || drawBottomCorners) {
                            canvas.drawRect((float) (dx + cornerIn), (float) (AndroidUtilities.dp(3.0f) + y), (float) ((dx + cornerIn) + corner), (float) (AndroidUtilities.dp(11.0f) + y), Theme.chat_actionBackgroundPaint);
                        }
                        Theme.chat_cornerInner[0].setBounds(dx, dy, dx + cornerIn, dy + cornerIn);
                        Theme.chat_cornerInner[0].draw(canvas);
                        dx = x + width;
                        if (!(drawInnerBottom == 2 || drawInnerBottom == 3)) {
                            dx += cornerRest;
                        }
                        if (drawTopCorners || drawBottomCorners) {
                            canvas.drawRect((float) (dx - corner), (float) (AndroidUtilities.dp(3.0f) + y), (float) dx, (float) (AndroidUtilities.dp(11.0f) + y), Theme.chat_actionBackgroundPaint);
                        }
                        Theme.chat_cornerInner[1].setBounds(dx, dy, dx + cornerIn, dy + cornerIn);
                        Theme.chat_cornerInner[1].draw(canvas);
                    } else {
                        y -= AndroidUtilities.dp(6.0f);
                        height += AndroidUtilities.dp(6.0f);
                    }
                }
                if (drawTopCorners || drawBottomCorners) {
                    canvas.drawRect((float) (x + cornerOffset), (float) yOld, (float) ((x + width) - cornerOffset), (float) (yOld + hOld), Theme.chat_actionBackgroundPaint);
                } else {
                    canvas.drawRect((float) x, (float) yOld, (float) (x + width), (float) (yOld + hOld), Theme.chat_actionBackgroundPaint);
                }
                dx = x - cornerRest;
                int dx2 = (x + width) - cornerOffset;
                if (drawTopCorners && !drawBottomCorners && drawInnerBottom != 2) {
                    canvas.drawRect((float) dx, (float) (y + corner), (float) (dx + corner), (float) (((y + height) + additionalHeight) - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) dx2, (float) (y + corner), (float) (dx2 + corner), (float) (((y + height) + additionalHeight) - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                } else if (drawBottomCorners && !drawTopCorners && drawInnerTop != 2) {
                    canvas.drawRect((float) dx, (float) ((y + corner) - AndroidUtilities.dp(5.0f)), (float) (dx + corner), (float) (((y + height) + additionalHeight) - corner), Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) dx2, (float) ((y + corner) - AndroidUtilities.dp(5.0f)), (float) (dx2 + corner), (float) (((y + height) + additionalHeight) - corner), Theme.chat_actionBackgroundPaint);
                } else if (drawTopCorners || drawBottomCorners) {
                    canvas.drawRect((float) dx, (float) (y + corner), (float) (dx + corner), (float) (((y + height) + additionalHeight) - corner), Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) dx2, (float) (y + corner), (float) (dx2 + corner), (float) (((y + height) + additionalHeight) - corner), Theme.chat_actionBackgroundPaint);
                }
                if (drawTopCorners) {
                    Theme.chat_cornerOuter[0].setBounds(dx, y, dx + corner, y + corner);
                    Theme.chat_cornerOuter[0].draw(canvas);
                    Theme.chat_cornerOuter[1].setBounds(dx2, y, dx2 + corner, y + corner);
                    Theme.chat_cornerOuter[1].draw(canvas);
                }
                if (drawBottomCorners) {
                    dy = ((y + height) + additionalHeight) - corner;
                    Theme.chat_cornerOuter[2].setBounds(dx2, dy, dx2 + corner, dy + corner);
                    Theme.chat_cornerOuter[2].draw(canvas);
                    Theme.chat_cornerOuter[3].setBounds(dx, dy, dx + corner, dy + corner);
                    Theme.chat_cornerOuter[3].draw(canvas);
                }
                y += height;
                a++;
            }
            canvas.save();
            canvas.translate((float) this.textXLeft, (float) this.textY);
            this.textLayout.draw(canvas);
            canvas.restore();
        }
    }
}
