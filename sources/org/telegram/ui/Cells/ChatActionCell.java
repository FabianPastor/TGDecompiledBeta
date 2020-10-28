package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.PhotoViewer;

public class ChatActionCell extends BaseCell implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private AvatarDrawable avatarDrawable;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private ImageLocation currentVideoLocation;
    private int customDate;
    private CharSequence customText;
    private ChatActionCellDelegate delegate;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver;
    private float lastTouchX;
    private float lastTouchY;
    private String overrideBackground;
    private int overrideColor;
    private ColorFilter overrideColorFilter;
    private String overrideText;
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

            public static void $default$didPressReplyMessage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, int i) {
            }

            public static void $default$needOpenUserProfile(ChatActionCellDelegate chatActionCellDelegate, int i) {
            }
        }

        void didClickImage(ChatActionCell chatActionCell);

        void didLongPress(ChatActionCell chatActionCell, float f, float f2);

        void didPressReplyMessage(ChatActionCell chatActionCell, int i);

        void needOpenUserProfile(int i);
    }

    public void onFailedDownload(String str, boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    public void onProgressDownload(String str, long j, long j2) {
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public ChatActionCell(Context context) {
        super(context);
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
        this.avatarDrawable = new AvatarDrawable();
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
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
                updateTextInternal(z2);
            }
        }
    }

    private void updateTextInternal(boolean z) {
        if (getMeasuredWidth() != 0) {
            createLayout(this.customText, getMeasuredWidth());
            invalidate();
        }
        if (this.wasLayout) {
            buildLayout();
        } else if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChatActionCell.this.requestLayout();
                }
            });
        } else {
            requestLayout();
        }
    }

    public void setCustomText(String str) {
        this.customText = str;
        if (str != null) {
            updateTextInternal(false);
        }
    }

    public void setOverrideColor(String str, String str2) {
        this.overrideBackground = str;
        this.overrideText = str2;
    }

    public void setMessageObject(MessageObject messageObject) {
        TLRPC$PhotoSize tLRPC$PhotoSize;
        StaticLayout staticLayout;
        MessageObject messageObject2 = messageObject;
        if (this.currentMessageObject != messageObject2 || (((staticLayout = this.textLayout) != null && !TextUtils.equals(staticLayout.getText(), messageObject2.messageText)) || (!this.hasReplyMessage && messageObject2.replyMessageObject != null))) {
            this.currentMessageObject = messageObject2;
            this.hasReplyMessage = messageObject2.replyMessageObject != null;
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.previousWidth = 0;
            TLRPC$VideoSize tLRPC$VideoSize = null;
            if (this.currentMessageObject.type == 11) {
                this.avatarDrawable.setInfo((int) messageObject.getDialogId(), (String) null, (String) null);
                MessageObject messageObject3 = this.currentMessageObject;
                if (messageObject3.messageOwner.action instanceof TLRPC$TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, messageObject3, 0);
                } else {
                    int size = messageObject3.photoThumbs.size();
                    int i = 0;
                    while (true) {
                        if (i >= size) {
                            tLRPC$PhotoSize = null;
                            break;
                        }
                        tLRPC$PhotoSize = this.currentMessageObject.photoThumbs.get(i);
                        if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
                            break;
                        }
                        i++;
                    }
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, 640);
                    if (closestPhotoSizeWithSize != null) {
                        TLRPC$Photo tLRPC$Photo = messageObject2.messageOwner.action.photo;
                        if (!tLRPC$Photo.video_sizes.isEmpty() && SharedConfig.autoplayGifs) {
                            TLRPC$VideoSize tLRPC$VideoSize2 = tLRPC$Photo.video_sizes.get(0);
                            if (messageObject2.mediaExists || DownloadController.getInstance(this.currentAccount).canDownloadMedia(4, tLRPC$VideoSize2.size)) {
                                tLRPC$VideoSize = tLRPC$VideoSize2;
                            } else {
                                this.currentVideoLocation = ImageLocation.getForPhoto(tLRPC$VideoSize2, tLRPC$Photo);
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(tLRPC$VideoSize2), this.currentMessageObject, this);
                            }
                        }
                        if (tLRPC$VideoSize != null) {
                            this.imageReceiver.setImage(ImageLocation.getForPhoto(tLRPC$VideoSize, tLRPC$Photo), "g", ImageLocation.getForObject(tLRPC$PhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
                        } else {
                            this.imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize, this.currentMessageObject.photoThumbsObject), "150_150", ImageLocation.getForObject(tLRPC$PhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
                        }
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
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        this.imageReceiver.onDetachedFromWindow();
        this.wasLayout = false;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
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
            int r1 = r1.getReplyMsgId()
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
        StaticLayout staticLayout = new StaticLayout(charSequence, Theme.chat_actionTextPaint, dp, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        this.textLayout = staticLayout;
        int i2 = 0;
        this.textHeight = 0;
        this.textWidth = 0;
        try {
            int lineCount = staticLayout.getLineCount();
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
        setMeasuredDimension(max, i3 + ((messageObject == null || messageObject.type != 11) ? 0 : AndroidUtilities.roundMessageSize + AndroidUtilities.dp(10.0f)) + AndroidUtilities.dp(14.0f));
    }

    private void buildLayout() {
        CharSequence charSequence;
        TLRPC$MessageMedia tLRPC$MessageMedia;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (tLRPC$Message == null || (tLRPC$MessageMedia = tLRPC$Message.media) == null || tLRPC$MessageMedia.ttl_seconds == 0) {
                charSequence = messageObject.messageText;
            } else if (tLRPC$MessageMedia.photo instanceof TLRPC$TL_photoEmpty) {
                charSequence = LocaleController.getString("AttachPhotoExpired", NUM);
            } else if (tLRPC$MessageMedia.document instanceof TLRPC$TL_documentEmpty) {
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
            int i = AndroidUtilities.roundMessageSize;
            this.imageReceiver.setImageCoords((float) ((this.previousWidth - AndroidUtilities.roundMessageSize) / 2), (float) (this.textHeight + AndroidUtilities.dp(19.0f)), (float) i, (float) i);
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
        int i;
        char c;
        int i2;
        int i3;
        char c2;
        int i4;
        char c3;
        int i5;
        int i6;
        int i7;
        int i8;
        boolean z;
        int i9;
        int i10;
        int i11;
        char c4;
        int i12;
        int i13;
        int i14;
        char c5;
        int i15;
        int i16;
        int i17;
        int i18;
        int i19;
        int i20;
        char c6;
        int i21;
        int i22;
        int i23;
        int i24;
        ChatActionCell chatActionCell = this;
        Canvas canvas2 = canvas;
        MessageObject messageObject = chatActionCell.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            chatActionCell.imageReceiver.draw(canvas2);
        }
        if (chatActionCell.textLayout != null) {
            String str = chatActionCell.overrideBackground;
            if (str != null) {
                int color = Theme.getColor(str);
                if (color != chatActionCell.overrideColor) {
                    chatActionCell.overrideColor = color;
                    chatActionCell.overrideColorFilter = new PorterDuffColorFilter(chatActionCell.overrideColor, PorterDuff.Mode.MULTIPLY);
                }
                for (int i25 = 0; i25 < 4; i25++) {
                    Theme.chat_cornerOuter[i25].setColorFilter(chatActionCell.overrideColorFilter);
                    Theme.chat_cornerInner[i25].setColorFilter(chatActionCell.overrideColorFilter);
                }
                Theme.chat_actionBackgroundPaint.setColor(chatActionCell.overrideColor);
                Theme.chat_actionTextPaint.setColor(Theme.getColor(chatActionCell.overrideText));
            }
            int lineCount = chatActionCell.textLayout.getLineCount();
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
                int i32 = i31;
                int i33 = dp4;
                int i34 = i32;
                if (z2) {
                    i34 += AndroidUtilities.dp(3.0f);
                }
                int i35 = i34;
                if (z2 || (i24 = i29 + 1) >= lineCount) {
                    c = 0;
                    i = 0;
                } else {
                    int findMaxWidthAroundLine2 = chatActionCell.findMaxWidthAroundLine(i24) + i26;
                    int i36 = i26 * 2;
                    if (findMaxWidthAroundLine2 + i36 < i30) {
                        i = findMaxWidthAroundLine2;
                        c = 1;
                        z2 = true;
                    } else if (i30 + i36 < findMaxWidthAroundLine2) {
                        i = findMaxWidthAroundLine2;
                        c = 2;
                    } else {
                        i = findMaxWidthAroundLine2;
                        c = 3;
                    }
                }
                if (z3 || i29 <= 0) {
                    i2 = i33;
                    c2 = 0;
                    i3 = 0;
                } else {
                    i3 = chatActionCell.findMaxWidthAroundLine(i29 - 1) + i26;
                    int i37 = i26 * 2;
                    i2 = i33;
                    if (i3 + i37 < i30) {
                        c2 = 1;
                        z3 = true;
                    } else {
                        c2 = i30 + i37 < i3 ? (char) 2 : 3;
                    }
                }
                if (c != 0) {
                    char c7 = c2;
                    if (c == 1) {
                        int dp5 = AndroidUtilities.dp(3.0f);
                        i7 = lineBottom;
                        char c8 = c;
                        char c9 = c7;
                        i12 = i2;
                        i10 = i3;
                        int measuredWidth2 = (getMeasuredWidth() - i) / 2;
                        i9 = i30;
                        i6 = dp;
                        i11 = measuredWidth;
                        i5 = i28;
                        i8 = i29;
                        if (isLineBottom(i, i30, i29 + 1, lineCount, i26)) {
                            int i38 = i12 + i35;
                            float f = (float) i38;
                            canvas.drawRect((float) (i11 + dp2), f, (float) (measuredWidth2 - i26), (float) (AndroidUtilities.dp(3.0f) + i38), Theme.chat_actionBackgroundPaint);
                            canvas.drawRect((float) (measuredWidth2 + i + i26), f, (float) ((i11 + i9) - dp2), (float) (i38 + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        } else {
                            int i39 = i12 + i35;
                            float f2 = (float) i39;
                            canvas.drawRect((float) (i11 + dp2), f2, (float) measuredWidth2, (float) (AndroidUtilities.dp(3.0f) + i39), Theme.chat_actionBackgroundPaint);
                            canvas.drawRect((float) (measuredWidth2 + i), f2, (float) ((i11 + i9) - dp2), (float) (i39 + AndroidUtilities.dp(3.0f)), Theme.chat_actionBackgroundPaint);
                        }
                        c3 = c8;
                        i4 = dp5;
                        c4 = c9;
                        z = z2;
                        canvas2 = canvas;
                    } else {
                        i5 = i28;
                        i8 = i29;
                        i7 = lineBottom;
                        i6 = dp;
                        char CLASSNAME = c7;
                        i12 = i2;
                        char CLASSNAME = c;
                        i9 = i30;
                        i11 = measuredWidth;
                        i10 = i3;
                        if (CLASSNAME == 2) {
                            int dp6 = AndroidUtilities.dp(3.0f);
                            int dp7 = (i12 + i35) - AndroidUtilities.dp(11.0f);
                            int i40 = i11 - dp3;
                            if (!(CLASSNAME == 2 || CLASSNAME == 3)) {
                                i40 -= i26;
                            }
                            int i41 = i40;
                            if (z3 || z2) {
                                int i42 = i41 + dp3;
                                c6 = CLASSNAME;
                                i21 = i41;
                                canvas.drawRect((float) i42, (float) (AndroidUtilities.dp(3.0f) + dp7), (float) (i42 + i6), (float) (dp7 + i6), Theme.chat_actionBackgroundPaint);
                            } else {
                                c6 = CLASSNAME;
                                i21 = i41;
                            }
                            int i43 = dp7 + dp3;
                            Theme.chat_cornerInner[2].setBounds(i21, dp7, i21 + dp3, i43);
                            canvas2 = canvas;
                            char CLASSNAME = c6;
                            Theme.chat_cornerInner[2].draw(canvas2);
                            int i44 = i11 + i9;
                            if (!(CLASSNAME == 2 || CLASSNAME == 3)) {
                                i44 += i26;
                            }
                            if (z3 || z2) {
                                i4 = dp6;
                                i23 = i44;
                                z = z2;
                                i22 = i43;
                                c3 = CLASSNAME;
                                c4 = CLASSNAME;
                                canvas.drawRect((float) (i44 - i6), (float) (AndroidUtilities.dp(3.0f) + dp7), (float) i44, (float) (dp7 + i6), Theme.chat_actionBackgroundPaint);
                            } else {
                                i4 = dp6;
                                c3 = CLASSNAME;
                                z = z2;
                                i23 = i44;
                                i22 = i43;
                                c4 = CLASSNAME;
                            }
                            Theme.chat_cornerInner[3].setBounds(i23, dp7, i23 + dp3, i22);
                            Theme.chat_cornerInner[3].draw(canvas2);
                        } else {
                            c3 = CLASSNAME;
                            z = z2;
                            c4 = CLASSNAME;
                            canvas2 = canvas;
                            i4 = AndroidUtilities.dp(6.0f);
                        }
                    }
                } else {
                    c3 = c;
                    i5 = i28;
                    i8 = i29;
                    i7 = lineBottom;
                    i10 = i3;
                    i6 = dp;
                    z = z2;
                    i12 = i2;
                    c4 = c2;
                    i9 = i30;
                    i11 = measuredWidth;
                    i4 = 0;
                }
                if (c4 == 0) {
                    i13 = lineCount;
                    c5 = c3;
                    i15 = i12;
                    i14 = i35;
                } else if (c4 == 1) {
                    int measuredWidth3 = (getMeasuredWidth() - i10) / 2;
                    int dp8 = i12 - AndroidUtilities.dp(3.0f);
                    i14 = i35 + AndroidUtilities.dp(3.0f);
                    if (isLineTop(i10, i9, i8 - 1, lineCount, i26)) {
                        float f3 = (float) dp8;
                        canvas.drawRect((float) (i11 + dp2), f3, (float) (measuredWidth3 - i26), (float) (AndroidUtilities.dp(3.0f) + dp8), Theme.chat_actionBackgroundPaint);
                        canvas.drawRect((float) (measuredWidth3 + i10 + i26), f3, (float) ((i11 + i9) - dp2), (float) (AndroidUtilities.dp(3.0f) + dp8), Theme.chat_actionBackgroundPaint);
                    } else {
                        float f4 = (float) dp8;
                        canvas.drawRect((float) (i11 + dp2), f4, (float) measuredWidth3, (float) (AndroidUtilities.dp(3.0f) + dp8), Theme.chat_actionBackgroundPaint);
                        canvas.drawRect((float) (measuredWidth3 + i10), f4, (float) ((i11 + i9) - dp2), (float) (AndroidUtilities.dp(3.0f) + dp8), Theme.chat_actionBackgroundPaint);
                    }
                    i13 = lineCount;
                    i15 = dp8;
                    c5 = c3;
                } else if (c4 == 2) {
                    int dp9 = i12 - AndroidUtilities.dp(3.0f);
                    int dp10 = i35 + AndroidUtilities.dp(3.0f);
                    int i45 = i11 - dp3;
                    c5 = c3;
                    if (!(c5 == 2 || c5 == 3)) {
                        i45 -= i26;
                    }
                    int i46 = i45;
                    if (z3 || z) {
                        int i47 = i46 + dp3;
                        i13 = lineCount;
                        i16 = i46;
                        canvas.drawRect((float) i47, (float) (AndroidUtilities.dp(3.0f) + dp9), (float) (i47 + i6), (float) (AndroidUtilities.dp(11.0f) + dp9), Theme.chat_actionBackgroundPaint);
                    } else {
                        i13 = lineCount;
                        i16 = i46;
                    }
                    int i48 = i5;
                    int i49 = i48 + dp3;
                    Theme.chat_cornerInner[0].setBounds(i16, i48, i16 + dp3, i49);
                    Theme.chat_cornerInner[0].draw(canvas2);
                    int i50 = i11 + i9;
                    if (!(c5 == 2 || c5 == 3)) {
                        i50 += i26;
                    }
                    int i51 = i50;
                    if (z3 || z) {
                        float dp11 = (float) (AndroidUtilities.dp(3.0f) + dp9);
                        float dp12 = (float) (AndroidUtilities.dp(11.0f) + dp9);
                        i17 = dp9;
                        i20 = i49;
                        int i52 = i48;
                        float f5 = dp12;
                        i18 = dp10;
                        i19 = i52;
                        canvas.drawRect((float) (i51 - i6), dp11, (float) i51, f5, Theme.chat_actionBackgroundPaint);
                    } else {
                        i17 = dp9;
                        i18 = dp10;
                        i20 = i49;
                        i19 = i48;
                    }
                    Theme.chat_cornerInner[1].setBounds(i51, i19, i51 + dp3, i20);
                    Theme.chat_cornerInner[1].draw(canvas2);
                    i14 = i18;
                    i15 = i17;
                } else {
                    i13 = lineCount;
                    c5 = c3;
                    i14 = i35 + AndroidUtilities.dp(6.0f);
                    i15 = i12 - AndroidUtilities.dp(6.0f);
                }
                if (z3 || z) {
                    canvas.drawRect((float) (i11 + dp2), (float) i12, (float) ((i11 + i9) - dp2), (float) (i12 + i35), Theme.chat_actionBackgroundPaint);
                } else {
                    canvas.drawRect((float) i11, (float) i12, (float) (i11 + i9), (float) (i12 + i35), Theme.chat_actionBackgroundPaint);
                }
                int i53 = i11 - i26;
                int i54 = (i11 + i9) - dp2;
                if (z3 && !z && c5 != 2) {
                    int i55 = i15 + i14 + i4;
                    float f6 = (float) (i15 + i6);
                    canvas.drawRect((float) i53, f6, (float) (i53 + i6), (float) (i55 - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) i54, f6, (float) (i54 + i6), (float) (i55 - AndroidUtilities.dp(6.0f)), Theme.chat_actionBackgroundPaint);
                } else if (z && !z3 && c4 != 2) {
                    int i56 = i15 + i6;
                    float f7 = (float) (((i15 + i14) + i4) - i6);
                    canvas.drawRect((float) i53, (float) (i56 - AndroidUtilities.dp(5.0f)), (float) (i53 + i6), f7, Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) i54, (float) (i56 - AndroidUtilities.dp(5.0f)), (float) (i54 + i6), f7, Theme.chat_actionBackgroundPaint);
                } else if (z3 || z) {
                    float f8 = (float) (i15 + i6);
                    float f9 = (float) (((i15 + i14) + i4) - i6);
                    canvas.drawRect((float) i53, f8, (float) (i53 + i6), f9, Theme.chat_actionBackgroundPaint);
                    canvas.drawRect((float) i54, f8, (float) (i54 + i6), f9, Theme.chat_actionBackgroundPaint);
                }
                if (z3) {
                    int i57 = i15 + i6;
                    Theme.chat_cornerOuter[0].setBounds(i53, i15, i53 + i6, i57);
                    Theme.chat_cornerOuter[0].draw(canvas2);
                    Theme.chat_cornerOuter[1].setBounds(i54, i15, i54 + i6, i57);
                    Theme.chat_cornerOuter[1].draw(canvas2);
                }
                if (z) {
                    int i58 = ((i15 + i14) + i4) - i6;
                    int i59 = i58 + i6;
                    Theme.chat_cornerOuter[2].setBounds(i54, i58, i54 + i6, i59);
                    Theme.chat_cornerOuter[2].draw(canvas2);
                    Theme.chat_cornerOuter[3].setBounds(i53, i58, i53 + i6, i59);
                    Theme.chat_cornerOuter[3].draw(canvas2);
                }
                dp4 = i15 + i14;
                i28 = dp4 + i4;
                i29 = i8 + 1;
                chatActionCell = this;
                i27 = i7;
                dp = i6;
                lineCount = i13;
            }
            canvas.save();
            canvas2.translate((float) this.textXLeft, (float) this.textY);
            this.textLayout.draw(canvas2);
            canvas.restore();
            if (this.overrideColorFilter != null) {
                for (int i60 = 0; i60 < 4; i60++) {
                    Theme.chat_cornerOuter[i60].setColorFilter(Theme.colorFilter);
                    Theme.chat_cornerInner[i60].setColorFilter(Theme.colorFilter);
                }
                Theme.chat_actionBackgroundPaint.setColor(Theme.currentColor);
                Theme.chat_actionTextPaint.setColor(Theme.getColor("chat_serviceText"));
            }
        }
    }

    public void onSuccessDownload(String str) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            TLRPC$PhotoSize tLRPC$PhotoSize = null;
            int i = 0;
            int size = messageObject.photoThumbs.size();
            while (true) {
                if (i >= size) {
                    break;
                }
                TLRPC$PhotoSize tLRPC$PhotoSize2 = this.currentMessageObject.photoThumbs.get(i);
                if (tLRPC$PhotoSize2 instanceof TLRPC$TL_photoStrippedSize) {
                    tLRPC$PhotoSize = tLRPC$PhotoSize2;
                    break;
                }
                i++;
            }
            this.imageReceiver.setImage(this.currentVideoLocation, "g", ImageLocation.getForObject(tLRPC$PhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (!TextUtils.isEmpty(this.customText) || this.currentMessageObject != null) {
            accessibilityNodeInfo.setText(!TextUtils.isEmpty(this.customText) ? this.customText : this.currentMessageObject.messageText);
            accessibilityNodeInfo.setEnabled(true);
        }
    }
}
