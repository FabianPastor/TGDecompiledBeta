package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
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

    public void setCustomText(CharSequence charSequence) {
        this.customText = charSequence;
        if (charSequence != null) {
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
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02db  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x043f  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x044a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0476 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x04af A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x050a  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x052f  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0532  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x055c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00d3 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f2  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f8  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x013c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x016c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r37) {
        /*
            r36 = this;
            r6 = r36
            r7 = r37
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject
            if (r0 == 0) goto L_0x0013
            int r0 = r0.type
            r1 = 11
            if (r0 != r1) goto L_0x0013
            org.telegram.messenger.ImageReceiver r0 = r6.imageReceiver
            r0.draw(r7)
        L_0x0013:
            android.text.StaticLayout r0 = r6.textLayout
            if (r0 != 0) goto L_0x0018
            return
        L_0x0018:
            java.lang.String r0 = r6.overrideBackground
            r8 = 4
            if (r0 == 0) goto L_0x005c
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            int r1 = r6.overrideColor
            if (r0 == r1) goto L_0x0032
            r6.overrideColor = r0
            android.graphics.PorterDuffColorFilter r0 = new android.graphics.PorterDuffColorFilter
            int r1 = r6.overrideColor
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r0.<init>(r1, r2)
            r6.overrideColorFilter = r0
        L_0x0032:
            r0 = 0
        L_0x0033:
            if (r0 >= r8) goto L_0x004a
            android.graphics.drawable.Drawable[] r1 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r1 = r1[r0]
            android.graphics.ColorFilter r2 = r6.overrideColorFilter
            r1.setColorFilter(r2)
            android.graphics.drawable.Drawable[] r1 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r1 = r1[r0]
            android.graphics.ColorFilter r2 = r6.overrideColorFilter
            r1.setColorFilter(r2)
            int r0 = r0 + 1
            goto L_0x0033
        L_0x004a:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            int r1 = r6.overrideColor
            r0.setColor(r1)
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_actionTextPaint
            java.lang.String r1 = r6.overrideText
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
        L_0x005c:
            android.text.StaticLayout r0 = r6.textLayout
            int r10 = r0.getLineCount()
            r11 = 1093664768(0x41300000, float:11.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r15 = r12 - r14
            r0 = 1090519040(0x41000000, float:8.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
        L_0x0080:
            if (r5 >= r10) goto L_0x0572
            int r3 = r6.findMaxWidthAroundLine(r5)
            int r8 = r10 + -1
            if (r5 >= r8) goto L_0x0091
            int r9 = r5 + 1
            int r9 = r6.findMaxWidthAroundLine(r9)
            goto L_0x0092
        L_0x0091:
            r9 = 0
        L_0x0092:
            r17 = 1097859072(0x41700000, float:15.0)
            r13 = 2
            if (r1 == 0) goto L_0x00ae
            int r11 = r3 - r1
            if (r11 <= 0) goto L_0x00ae
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r20 = r3
            int r3 = r19 * 2
            if (r11 >= r3) goto L_0x00b0
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 * 2
            int r3 = r3 + r1
            r1 = r3
            goto L_0x00b3
        L_0x00ae:
            r20 = r3
        L_0x00b0:
            r3 = r20
            r1 = 0
        L_0x00b3:
            if (r9 == 0) goto L_0x00cc
            int r11 = r3 - r9
            if (r11 <= 0) goto L_0x00cc
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r20 = r3
            int r3 = r19 * 2
            if (r11 >= r3) goto L_0x00ce
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 * 2
            int r3 = r3 + r9
            r9 = r3
            goto L_0x00d1
        L_0x00cc:
            r20 = r3
        L_0x00ce:
            r3 = r20
            r9 = 0
        L_0x00d1:
            if (r1 == 0) goto L_0x00db
            if (r9 == 0) goto L_0x00db
            int r1 = java.lang.Math.max(r1, r9)
            r9 = r1
            goto L_0x00dc
        L_0x00db:
            r9 = r3
        L_0x00dc:
            int r1 = r36.getMeasuredWidth()
            int r1 = r1 - r9
            int r1 = r1 - r15
            int r11 = r1 / 2
            int r3 = r9 + r15
            android.text.StaticLayout r1 = r6.textLayout
            int r17 = r1.getLineBottom(r5)
            int r1 = r17 - r2
            if (r5 != r8) goto L_0x00f2
            r8 = 1
            goto L_0x00f3
        L_0x00f2:
            r8 = 0
        L_0x00f3:
            if (r5 != 0) goto L_0x00f8
            r19 = 1
            goto L_0x00fa
        L_0x00f8:
            r19 = 0
        L_0x00fa:
            r20 = 1077936128(0x40400000, float:3.0)
            if (r19 == 0) goto L_0x010a
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r21
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r1 = r1 + r21
        L_0x010a:
            r35 = r1
            r1 = r0
            r0 = r35
            if (r8 == 0) goto L_0x0117
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r21
        L_0x0117:
            r21 = r0
            if (r8 != 0) goto L_0x0138
            int r0 = r5 + 1
            if (r0 >= r10) goto L_0x0138
            int r0 = r6.findMaxWidthAroundLine(r0)
            int r0 = r0 + r15
            int r23 = r15 * 2
            int r13 = r0 + r23
            if (r13 >= r3) goto L_0x012e
            r13 = r0
            r0 = 1
            r8 = 1
            goto L_0x013a
        L_0x012e:
            int r13 = r3 + r23
            if (r13 >= r0) goto L_0x0135
            r13 = r0
            r0 = 2
            goto L_0x013a
        L_0x0135:
            r13 = r0
            r0 = 3
            goto L_0x013a
        L_0x0138:
            r0 = 0
            r13 = 0
        L_0x013a:
            if (r19 != 0) goto L_0x0163
            if (r5 <= 0) goto L_0x0163
            int r2 = r5 + -1
            int r2 = r6.findMaxWidthAroundLine(r2)
            int r2 = r2 + r15
            int r25 = r15 * 2
            r26 = r1
            int r1 = r2 + r25
            if (r1 >= r3) goto L_0x0153
            r19 = r2
            r2 = 1
            r25 = 1
            goto L_0x016a
        L_0x0153:
            int r1 = r3 + r25
            if (r1 >= r2) goto L_0x015d
            r25 = r19
            r19 = r2
            r2 = 2
            goto L_0x016a
        L_0x015d:
            r25 = r19
            r19 = r2
            r2 = 3
            goto L_0x016a
        L_0x0163:
            r26 = r1
            r25 = r19
            r2 = 0
            r19 = 0
        L_0x016a:
            if (r0 == 0) goto L_0x02db
            r1 = 1
            if (r0 != r1) goto L_0x0215
            int r23 = r36.getMeasuredWidth()
            int r23 = r23 - r13
            r27 = r9
            r24 = 2
            int r9 = r23 / 2
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r28 = r5 + 1
            r6 = r0
            r7 = 3
            r0 = r36
            r7 = r26
            r26 = 1
            r1 = r13
            r29 = r12
            r12 = r2
            r2 = r3
            r26 = r3
            r3 = r28
            r30 = r4
            r4 = r10
            r28 = r5
            r5 = r15
            boolean r0 = r0.isLineBottom(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x01d7
            int r0 = r11 + r14
            float r1 = (float) r0
            int r5 = r7 + r21
            float r4 = (float) r5
            int r0 = r9 - r15
            float r3 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r5
            float r2 = (float) r0
            android.graphics.Paint r31 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r32 = r2
            r2 = r4
            r33 = r4
            r4 = r32
            r32 = r5
            r5 = r31
            r0.drawRect(r1, r2, r3, r4, r5)
            int r9 = r9 + r13
            int r9 = r9 + r15
            float r1 = (float) r9
            int r3 = r11 + r26
            int r3 = r3 - r14
            float r3 = (float) r3
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r32 + r0
            float r4 = (float) r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r2 = r33
            r0.drawRect(r1, r2, r3, r4, r5)
            goto L_0x020c
        L_0x01d7:
            int r0 = r11 + r14
            float r1 = (float) r0
            int r5 = r7 + r21
            float r4 = (float) r5
            float r3 = (float) r9
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r5
            float r2 = (float) r0
            android.graphics.Paint r31 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r32 = r2
            r2 = r4
            r33 = r4
            r4 = r32
            r32 = r5
            r5 = r31
            r0.drawRect(r1, r2, r3, r4, r5)
            int r9 = r9 + r13
            float r1 = (float) r9
            int r3 = r11 + r26
            int r3 = r3 - r14
            float r3 = (float) r3
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r32 + r0
            float r4 = (float) r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r2 = r33
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x020c:
            r9 = r37
            r32 = r6
            r33 = r8
            r6 = 3
            goto L_0x02f0
        L_0x0215:
            r6 = r0
            r30 = r4
            r28 = r5
            r27 = r9
            r29 = r12
            r7 = r26
            r0 = 2
            r12 = r2
            r26 = r3
            if (r6 != r0) goto L_0x02cb
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r1 = r7 + r21
            r2 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r13 = r1 - r3
            int r1 = r11 - r16
            r5 = 3
            if (r12 == r0) goto L_0x023c
            if (r12 == r5) goto L_0x023c
            int r1 = r1 - r15
        L_0x023c:
            r4 = r1
            if (r25 != 0) goto L_0x0248
            if (r8 == 0) goto L_0x0242
            goto L_0x0248
        L_0x0242:
            r31 = r9
            r23 = 3
            r9 = r4
            goto L_0x0269
        L_0x0248:
            int r0 = r4 + r16
            float r1 = (float) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = r2 + r13
            float r2 = (float) r2
            int r0 = r0 + r29
            float r3 = (float) r0
            int r0 = r13 + r29
            float r0 = (float) r0
            android.graphics.Paint r22 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r23 = r0
            r0 = r37
            r31 = r9
            r9 = r4
            r4 = r23
            r23 = 3
            r5 = r22
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x0269:
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r1 = 2
            r0 = r0[r1]
            int r4 = r9 + r16
            int r5 = r13 + r16
            r0.setBounds(r9, r13, r4, r5)
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r0 = r0[r1]
            r9 = r37
            r4 = 3
            r0.draw(r9)
            int r3 = r11 + r26
            if (r12 == r1) goto L_0x0286
            if (r12 == r4) goto L_0x0286
            int r3 = r3 + r15
        L_0x0286:
            if (r25 != 0) goto L_0x0294
            if (r8 == 0) goto L_0x028b
            goto L_0x0294
        L_0x028b:
            r34 = r5
            r32 = r6
            r33 = r8
            r6 = 3
            r8 = r3
            goto L_0x02b6
        L_0x0294:
            int r0 = r3 - r29
            float r1 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r13
            float r2 = (float) r0
            float r0 = (float) r3
            int r4 = r13 + r29
            float r4 = (float) r4
            android.graphics.Paint r23 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r32 = r0
            r0 = r37
            r33 = r8
            r8 = r3
            r3 = r32
            r32 = r6
            r6 = 3
            r34 = r5
            r5 = r23
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x02b6:
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r0 = r0[r6]
            int r3 = r8 + r16
            r1 = r34
            r0.setBounds(r8, r13, r3, r1)
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r0 = r0[r6]
            r0.draw(r9)
            r23 = r31
            goto L_0x02f0
        L_0x02cb:
            r9 = r37
            r32 = r6
            r33 = r8
            r0 = 1086324736(0x40CLASSNAME, float:6.0)
            r6 = 3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r23 = r1
            goto L_0x02f0
        L_0x02db:
            r32 = r0
            r30 = r4
            r28 = r5
            r33 = r8
            r27 = r9
            r29 = r12
            r6 = 3
            r12 = r2
            r9 = r7
            r7 = r26
            r26 = r3
            r23 = 0
        L_0x02f0:
            if (r12 == 0) goto L_0x043f
            r8 = 1
            if (r12 != r8) goto L_0x0383
            int r0 = r36.getMeasuredWidth()
            int r0 = r0 - r19
            r1 = 2
            int r13 = r0 / 2
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r7 - r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r22 = r21 + r0
            int r3 = r28 + -1
            r0 = r36
            r1 = r19
            r2 = r26
            r4 = r10
            r8 = r5
            r5 = r15
            boolean r0 = r0.isLineTop(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x034d
            int r0 = r11 + r14
            float r1 = (float) r0
            float r5 = (float) r8
            int r0 = r13 - r15
            float r3 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r8
            float r4 = (float) r0
            android.graphics.Paint r30 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r2 = r5
            r31 = r5
            r5 = r30
            r0.drawRect(r1, r2, r3, r4, r5)
            int r13 = r13 + r19
            int r13 = r13 + r15
            float r1 = (float) r13
            int r3 = r11 + r26
            int r3 = r3 - r14
            float r3 = (float) r3
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r8 + r0
            float r4 = (float) r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r2 = r31
            r0.drawRect(r1, r2, r3, r4, r5)
            goto L_0x037b
        L_0x034d:
            int r0 = r11 + r14
            float r1 = (float) r0
            float r5 = (float) r8
            float r3 = (float) r13
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r8
            float r4 = (float) r0
            android.graphics.Paint r30 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r2 = r5
            r31 = r5
            r5 = r30
            r0.drawRect(r1, r2, r3, r4, r5)
            int r13 = r13 + r19
            float r1 = (float) r13
            int r3 = r11 + r26
            int r3 = r3 - r14
            float r3 = (float) r3
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r8 + r0
            float r4 = (float) r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r2 = r31
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x037b:
            r20 = r10
            r13 = r32
            r18 = 1093664768(0x41300000, float:11.0)
            goto L_0x0448
        L_0x0383:
            r0 = 2
            if (r12 != r0) goto L_0x0429
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r8 = r7 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r22 = r21 + r1
            int r1 = r11 - r16
            r13 = r32
            if (r13 == r0) goto L_0x039b
            if (r13 == r6) goto L_0x039b
            int r1 = r1 - r15
        L_0x039b:
            r5 = r1
            if (r25 != 0) goto L_0x03a3
            if (r33 == 0) goto L_0x03a1
            goto L_0x03a3
        L_0x03a1:
            r6 = r5
            goto L_0x03c1
        L_0x03a3:
            int r0 = r5 + r16
            float r1 = (float) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = r2 + r8
            float r2 = (float) r2
            int r0 = r0 + r29
            float r3 = (float) r0
            r0 = 1093664768(0x41300000, float:11.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r4 = r4 + r8
            float r4 = (float) r4
            android.graphics.Paint r19 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r6 = r5
            r5 = r19
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x03c1:
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r1 = 0
            r0 = r0[r1]
            int r5 = r6 + r16
            r4 = r30
            int r3 = r4 + r16
            r0.setBounds(r6, r4, r5, r3)
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r0 = r0[r1]
            r0.draw(r9)
            int r0 = r11 + r26
            r1 = 2
            if (r13 == r1) goto L_0x03df
            r1 = 3
            if (r13 == r1) goto L_0x03df
            int r0 = r0 + r15
        L_0x03df:
            r6 = r0
            if (r25 != 0) goto L_0x03ee
            if (r33 == 0) goto L_0x03e5
            goto L_0x03ee
        L_0x03e5:
            r30 = r8
            r20 = r10
            r18 = 1093664768(0x41300000, float:11.0)
            r8 = r3
            r10 = r4
            goto L_0x0415
        L_0x03ee:
            int r0 = r6 - r29
            float r1 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r8
            float r2 = (float) r0
            float r5 = (float) r6
            r18 = 1093664768(0x41300000, float:11.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 + r8
            float r0 = (float) r0
            android.graphics.Paint r19 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r20 = r0
            r0 = r37
            r30 = r8
            r8 = r3
            r3 = r5
            r5 = r4
            r4 = r20
            r20 = r10
            r10 = r5
            r5 = r19
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x0415:
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r1 = 1
            r0 = r0[r1]
            int r2 = r6 + r16
            r0.setBounds(r6, r10, r2, r8)
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r0 = r0[r1]
            r0.draw(r9)
            r8 = r30
            goto L_0x0448
        L_0x0429:
            r20 = r10
            r13 = r32
            r0 = 1086324736(0x40CLASSNAME, float:6.0)
            r18 = 1093664768(0x41300000, float:11.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r1 = r7 - r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r22 = r21 + r2
            r8 = r1
            goto L_0x0448
        L_0x043f:
            r20 = r10
            r13 = r32
            r18 = 1093664768(0x41300000, float:11.0)
            r8 = r7
            r22 = r21
        L_0x0448:
            if (r25 != 0) goto L_0x045d
            if (r33 == 0) goto L_0x044d
            goto L_0x045d
        L_0x044d:
            float r1 = (float) r11
            float r2 = (float) r7
            int r3 = r11 + r26
            float r3 = (float) r3
            int r0 = r7 + r21
            float r4 = (float) r0
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r0.drawRect(r1, r2, r3, r4, r5)
            goto L_0x046f
        L_0x045d:
            int r0 = r11 + r14
            float r1 = (float) r0
            float r2 = (float) r7
            int r3 = r11 + r26
            int r3 = r3 - r14
            float r3 = (float) r3
            int r0 = r7 + r21
            float r4 = (float) r0
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x046f:
            int r6 = r11 - r15
            int r11 = r11 + r26
            int r11 = r11 - r14
            if (r25 == 0) goto L_0x04ab
            if (r33 != 0) goto L_0x04ab
            r0 = 2
            if (r13 == r0) goto L_0x04ab
            float r1 = (float) r6
            int r12 = r8 + r29
            float r7 = (float) r12
            int r12 = r6 + r29
            float r3 = (float) r12
            int r0 = r8 + r22
            int r10 = r0 + r23
            r0 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = r10 - r2
            float r4 = (float) r0
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r2 = r7
            r0.drawRect(r1, r2, r3, r4, r5)
            float r1 = (float) r11
            int r12 = r11 + r29
            float r3 = (float) r12
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r10 = r10 - r0
            float r4 = (float) r10
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r0.drawRect(r1, r2, r3, r4, r5)
            goto L_0x0508
        L_0x04ab:
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r33 == 0) goto L_0x04e4
            if (r25 != 0) goto L_0x04e4
            r0 = 2
            if (r12 == r0) goto L_0x04e4
            float r1 = (float) r6
            int r12 = r8 + r29
            r7 = 1084227584(0x40a00000, float:5.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r12 - r0
            float r2 = (float) r0
            int r0 = r6 + r29
            float r3 = (float) r0
            int r0 = r8 + r22
            int r0 = r0 + r23
            int r0 = r0 - r29
            float r10 = (float) r0
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r4 = r10
            r0.drawRect(r1, r2, r3, r4, r5)
            float r1 = (float) r11
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r12 = r12 - r0
            float r2 = (float) r12
            int r12 = r11 + r29
            float r3 = (float) r12
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r0.drawRect(r1, r2, r3, r4, r5)
            goto L_0x0508
        L_0x04e4:
            if (r25 != 0) goto L_0x04e8
            if (r33 == 0) goto L_0x0508
        L_0x04e8:
            float r1 = (float) r6
            int r12 = r8 + r29
            float r7 = (float) r12
            int r12 = r6 + r29
            float r3 = (float) r12
            int r0 = r8 + r22
            int r0 = r0 + r23
            int r0 = r0 - r29
            float r10 = (float) r0
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0 = r37
            r2 = r7
            r4 = r10
            r0.drawRect(r1, r2, r3, r4, r5)
            float r1 = (float) r11
            int r12 = r11 + r29
            float r3 = (float) r12
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x0508:
            if (r25 == 0) goto L_0x052f
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r1 = 0
            r0 = r0[r1]
            int r12 = r6 + r29
            int r2 = r8 + r29
            r0.setBounds(r6, r8, r12, r2)
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r0 = r0[r1]
            r0.draw(r9)
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r3 = 1
            r0 = r0[r3]
            int r12 = r11 + r29
            r0.setBounds(r11, r8, r12, r2)
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r0 = r0[r3]
            r0.draw(r9)
            goto L_0x0530
        L_0x052f:
            r1 = 0
        L_0x0530:
            if (r33 == 0) goto L_0x055c
            int r0 = r8 + r22
            int r0 = r0 + r23
            int r0 = r0 - r29
            android.graphics.drawable.Drawable[] r2 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r3 = 2
            r2 = r2[r3]
            int r12 = r11 + r29
            int r4 = r0 + r29
            r2.setBounds(r11, r0, r12, r4)
            android.graphics.drawable.Drawable[] r2 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r2 = r2[r3]
            r2.draw(r9)
            android.graphics.drawable.Drawable[] r2 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r3 = 3
            r2 = r2[r3]
            int r12 = r6 + r29
            r2.setBounds(r6, r0, r12, r4)
            android.graphics.drawable.Drawable[] r0 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r0 = r0[r3]
            r0.draw(r9)
        L_0x055c:
            int r0 = r8 + r22
            int r4 = r0 + r23
            int r5 = r28 + 1
            r6 = r36
            r7 = r9
            r2 = r17
            r10 = r20
            r1 = r27
            r12 = r29
            r8 = 4
            r11 = 1093664768(0x41300000, float:11.0)
            goto L_0x0080
        L_0x0572:
            r9 = r7
            r1 = 0
            r37.save()
            r0 = r36
            int r2 = r0.textXLeft
            float r2 = (float) r2
            int r3 = r0.textY
            float r3 = (float) r3
            r9.translate(r2, r3)
            android.text.StaticLayout r2 = r0.textLayout
            r2.draw(r9)
            r37.restore()
            android.graphics.ColorFilter r2 = r0.overrideColorFilter
            if (r2 == 0) goto L_0x05b9
            r1 = 4
            r9 = 0
        L_0x0590:
            if (r9 >= r1) goto L_0x05a7
            android.graphics.drawable.Drawable[] r2 = org.telegram.ui.ActionBar.Theme.chat_cornerOuter
            r2 = r2[r9]
            android.graphics.PorterDuffColorFilter r3 = org.telegram.ui.ActionBar.Theme.colorFilter
            r2.setColorFilter(r3)
            android.graphics.drawable.Drawable[] r2 = org.telegram.ui.ActionBar.Theme.chat_cornerInner
            r2 = r2[r9]
            android.graphics.PorterDuffColorFilter r3 = org.telegram.ui.ActionBar.Theme.colorFilter
            r2.setColorFilter(r3)
            int r9 = r9 + 1
            goto L_0x0590
        L_0x05a7:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint
            int r2 = org.telegram.ui.ActionBar.Theme.currentColor
            r1.setColor(r2)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_actionTextPaint
            java.lang.String r2 = "chat_serviceText"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
        L_0x05b9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onDraw(android.graphics.Canvas):void");
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
