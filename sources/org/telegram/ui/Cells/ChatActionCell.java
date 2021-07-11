package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
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
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
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
    private int backgroundHeight;
    private Path backgroundPath = new Path();
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private ImageLocation currentVideoLocation;
    private int customDate;
    private CharSequence customText;
    private ChatActionCellDelegate delegate;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver;
    private boolean invalidatePath = true;
    private float lastTouchX;
    private float lastTouchY;
    private ArrayList<Integer> lineHeights = new ArrayList<>();
    private ArrayList<Integer> lineWidths = new ArrayList<>();
    private String overrideBackground;
    private int overrideColor;
    private ColorFilter overrideColorFilter;
    private String overrideText;
    private URLSpan pressedLink;
    private int previousWidth;
    private RectF rect = new RectF();
    private int textHeight;
    private StaticLayout textLayout;
    private int textWidth;
    private int textX;
    private int textXLeft;
    private int textY;
    private float viewTop;
    private boolean visiblePartSet;
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

            public static void $default$needOpenInviteLink(ChatActionCellDelegate chatActionCellDelegate, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
            }

            public static void $default$needOpenUserProfile(ChatActionCellDelegate chatActionCellDelegate, int i) {
            }
        }

        void didClickImage(ChatActionCell chatActionCell);

        void didLongPress(ChatActionCell chatActionCell, float f, float f2);

        void didPressReplyMessage(ChatActionCell chatActionCell, int i);

        void needOpenInviteLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported);

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

    public void setVisiblePart(float f, int i) {
        this.visiblePartSet = true;
        this.backgroundHeight = i;
        this.viewTop = f;
        if (Theme.hasGradientService()) {
            invalidate();
        }
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
    /* JADX WARNING: Removed duplicated region for block: B:85:0x015b  */
    /* JADX WARNING: Removed duplicated region for block: B:87:? A[RETURN, SYNTHETIC] */
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
            if (r2 != 0) goto L_0x0159
            int r5 = r10.getAction()
            if (r5 == 0) goto L_0x008a
            android.text.style.URLSpan r5 = r9.pressedLink
            if (r5 == 0) goto L_0x0159
            int r5 = r10.getAction()
            if (r5 != r3) goto L_0x0159
        L_0x008a:
            int r5 = r9.textX
            float r6 = (float) r5
            r7 = 0
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 < 0) goto L_0x0157
            int r6 = r9.textY
            float r8 = (float) r6
            int r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x0157
            int r8 = r9.textWidth
            int r5 = r5 + r8
            float r5 = (float) r5
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 > 0) goto L_0x0157
            int r5 = r9.textHeight
            int r5 = r5 + r6
            float r5 = (float) r5
            int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r5 > 0) goto L_0x0157
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
            if (r8 > 0) goto L_0x0154
            android.text.StaticLayout r8 = r9.textLayout
            float r1 = r8.getLineWidth(r1)
            float r6 = r6 + r1
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x0154
            org.telegram.messenger.MessageObject r0 = r9.currentMessageObject
            java.lang.CharSequence r0 = r0.messageText
            boolean r1 = r0 instanceof android.text.Spannable
            if (r1 == 0) goto L_0x0154
            android.text.Spannable r0 = (android.text.Spannable) r0
            java.lang.Class<android.text.style.URLSpan> r1 = android.text.style.URLSpan.class
            java.lang.Object[] r0 = r0.getSpans(r5, r5, r1)
            android.text.style.URLSpan[] r0 = (android.text.style.URLSpan[]) r0
            int r1 = r0.length
            if (r1 == 0) goto L_0x014f
            int r1 = r10.getAction()
            if (r1 != 0) goto L_0x00f1
            r0 = r0[r4]
            r9.pressedLink = r0
            goto L_0x0152
        L_0x00f1:
            r1 = r0[r4]
            android.text.style.URLSpan r5 = r9.pressedLink
            if (r1 != r5) goto L_0x0151
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r1 = r9.delegate
            if (r1 == 0) goto L_0x0152
            r0 = r0[r4]
            java.lang.String r0 = r0.getURL()
            java.lang.String r1 = "invite"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0121
            android.text.style.URLSpan r1 = r9.pressedLink
            boolean r2 = r1 instanceof org.telegram.ui.Components.URLSpanNoUnderline
            if (r2 == 0) goto L_0x0121
            org.telegram.ui.Components.URLSpanNoUnderline r1 = (org.telegram.ui.Components.URLSpanNoUnderline) r1
            org.telegram.tgnet.TLObject r0 = r1.getObject()
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatInviteExported
            if (r1 == 0) goto L_0x0152
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r0 = (org.telegram.tgnet.TLRPC$TL_chatInviteExported) r0
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r1 = r9.delegate
            r1.needOpenInviteLink(r0)
            goto L_0x0152
        L_0x0121:
            java.lang.String r1 = "game"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0135
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r0 = r9.delegate
            org.telegram.messenger.MessageObject r1 = r9.currentMessageObject
            int r1 = r1.getReplyMsgId()
            r0.didPressReplyMessage(r9, r1)
            goto L_0x0152
        L_0x0135:
            java.lang.String r1 = "http"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0145
            android.content.Context r1 = r9.getContext()
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r1, (java.lang.String) r0)
            goto L_0x0152
        L_0x0145:
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r1 = r9.delegate
            int r0 = java.lang.Integer.parseInt(r0)
            r1.needOpenUserProfile(r0)
            goto L_0x0152
        L_0x014f:
            r9.pressedLink = r7
        L_0x0151:
            r3 = r2
        L_0x0152:
            r2 = r3
            goto L_0x0159
        L_0x0154:
            r9.pressedLink = r7
            goto L_0x0159
        L_0x0157:
            r9.pressedLink = r7
        L_0x0159:
            if (r2 != 0) goto L_0x015f
            boolean r2 = super.onTouchEvent(r10)
        L_0x015f:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void createLayout(CharSequence charSequence, int i) {
        int dp = i - AndroidUtilities.dp(30.0f);
        this.invalidatePath = true;
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        int i;
        int i2;
        int i3;
        int i4;
        float f2;
        float f3;
        int i5;
        Canvas canvas2 = canvas;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            this.imageReceiver.draw(canvas2);
        }
        if (this.textLayout != null) {
            String str = this.overrideBackground;
            if (str != null) {
                int color = Theme.getColor(str);
                if (color != this.overrideColor) {
                    this.overrideColor = color;
                    this.overrideColorFilter = new PorterDuffColorFilter(this.overrideColor, PorterDuff.Mode.MULTIPLY);
                }
                Theme.chat_actionBackgroundPaint.setColor(this.overrideColor);
                Theme.chat_actionTextPaint.setColor(Theme.getColor(this.overrideText));
            }
            if (this.invalidatePath) {
                this.invalidatePath = false;
                this.lineWidths.clear();
                int lineCount = this.textLayout.getLineCount();
                int dp = AndroidUtilities.dp(11.0f);
                int dp2 = AndroidUtilities.dp(8.0f);
                int i6 = 0;
                for (int i7 = 0; i7 < lineCount; i7++) {
                    int ceil = (int) Math.ceil((double) this.textLayout.getLineWidth(i7));
                    if (i7 == 0 || (i5 = i6 - ceil) <= 0 || i5 > dp + dp2) {
                        i6 = ceil;
                    }
                    this.lineWidths.add(Integer.valueOf(i6));
                }
                for (int i8 = lineCount - 2; i8 >= 0; i8--) {
                    int intValue = this.lineWidths.get(i8).intValue();
                    int i9 = i6 - intValue;
                    if (i9 <= 0 || i9 > dp + dp2) {
                        i6 = intValue;
                    }
                    this.lineWidths.set(i8, Integer.valueOf(i6));
                }
                int dp3 = AndroidUtilities.dp(4.0f);
                int measuredWidth = getMeasuredWidth() / 2;
                int dp4 = AndroidUtilities.dp(3.0f);
                int dp5 = AndroidUtilities.dp(6.0f);
                int i10 = dp - dp4;
                this.lineHeights.clear();
                this.backgroundPath.reset();
                float f4 = (float) measuredWidth;
                this.backgroundPath.moveTo(f4, (float) dp3);
                int i11 = 0;
                int i12 = 0;
                while (i11 < lineCount) {
                    int intValue2 = this.lineWidths.get(i11).intValue();
                    int lineBottom = this.textLayout.getLineBottom(i11);
                    int i13 = dp2;
                    int i14 = lineCount - 1;
                    int i15 = dp5;
                    int intValue3 = i11 < i14 ? this.lineWidths.get(i11 + 1).intValue() : 0;
                    int i16 = lineBottom - i12;
                    if (i11 == 0 || intValue2 > i6) {
                        f = 3.0f;
                        i16 += AndroidUtilities.dp(3.0f);
                    } else {
                        f = 3.0f;
                    }
                    if (i11 == i14 || intValue2 > intValue3) {
                        i16 += AndroidUtilities.dp(f);
                    }
                    float f5 = (((float) intValue2) / 2.0f) + f4;
                    int i17 = (i11 == i14 || intValue2 >= intValue3 || i11 == 0 || intValue2 >= i6) ? i13 : i15;
                    if (i11 == 0 || intValue2 > i6) {
                        f2 = f4;
                        i2 = lineCount;
                        i = i6;
                        i3 = measuredWidth;
                        i4 = lineBottom;
                        this.rect.set((f5 - ((float) dp4)) - ((float) dp), (float) dp3, ((float) i10) + f5, (float) ((dp * 2) + dp3));
                        this.backgroundPath.arcTo(this.rect, -90.0f, 90.0f);
                    } else {
                        f2 = f4;
                        if (intValue2 < i6) {
                            i4 = lineBottom;
                            float f6 = ((float) i10) + f5;
                            i3 = measuredWidth;
                            i2 = lineCount;
                            int i18 = i17 * 2;
                            i = i6;
                            this.rect.set(f6, (float) dp3, ((float) i18) + f6, (float) (i18 + dp3));
                            this.backgroundPath.arcTo(this.rect, -90.0f, -90.0f);
                        } else {
                            i2 = lineCount;
                            i = i6;
                            i3 = measuredWidth;
                            i4 = lineBottom;
                        }
                    }
                    dp3 += i16;
                    if (i11 == i14 || intValue2 >= intValue3) {
                        f3 = 3.0f;
                    } else {
                        f3 = 3.0f;
                        dp3 -= AndroidUtilities.dp(3.0f);
                        i16 -= AndroidUtilities.dp(3.0f);
                    }
                    if (i11 != 0 && intValue2 < i) {
                        dp3 -= AndroidUtilities.dp(f3);
                        i16 -= AndroidUtilities.dp(f3);
                    }
                    this.lineHeights.add(Integer.valueOf(i16));
                    if (i11 == i14 || intValue2 > intValue3) {
                        this.rect.set((f5 - ((float) dp4)) - ((float) dp), (float) (dp3 - (dp * 2)), f5 + ((float) i10), (float) dp3);
                        this.backgroundPath.arcTo(this.rect, 0.0f, 90.0f);
                    } else if (intValue2 < intValue3) {
                        float f7 = f5 + ((float) i10);
                        int i19 = i17 * 2;
                        this.rect.set(f7, (float) (dp3 - i19), ((float) i19) + f7, (float) dp3);
                        this.backgroundPath.arcTo(this.rect, 180.0f, -90.0f);
                    }
                    i11++;
                    Canvas canvas3 = canvas;
                    i6 = intValue2;
                    dp2 = i13;
                    dp5 = i15;
                    f4 = f2;
                    i12 = i4;
                    measuredWidth = i3;
                    lineCount = i2;
                }
                int i20 = dp2;
                int i21 = measuredWidth;
                int i22 = dp5;
                int i23 = lineCount - 1;
                int i24 = i23;
                while (i24 >= 0) {
                    int intValue4 = i24 != 0 ? this.lineWidths.get(i24 - 1).intValue() : 0;
                    int intValue5 = this.lineWidths.get(i24).intValue();
                    int intValue6 = i24 != i23 ? this.lineWidths.get(i24 + 1).intValue() : 0;
                    this.textLayout.getLineBottom(i24);
                    float f8 = (float) (i21 - (intValue5 / 2));
                    int i25 = (i24 == i23 || intValue5 >= intValue6 || i24 == 0 || intValue5 >= intValue4) ? i20 : i22;
                    if (i24 == i23 || intValue5 > intValue6) {
                        this.rect.set(f8 - ((float) i10), (float) (dp3 - (dp * 2)), ((float) dp4) + f8 + ((float) dp), (float) dp3);
                        this.backgroundPath.arcTo(this.rect, 90.0f, 90.0f);
                    } else if (intValue5 < intValue6) {
                        float f9 = f8 - ((float) i10);
                        int i26 = i25 * 2;
                        this.rect.set(f9 - ((float) i26), (float) (dp3 - i26), f9, (float) dp3);
                        this.backgroundPath.arcTo(this.rect, 90.0f, -90.0f);
                    }
                    dp3 -= this.lineHeights.get(i24).intValue();
                    if (i24 == 0 || intValue5 > intValue4) {
                        this.rect.set(f8 - ((float) i10), (float) dp3, f8 + ((float) dp4) + ((float) dp), (float) ((dp * 2) + dp3));
                        this.backgroundPath.arcTo(this.rect, 180.0f, 90.0f);
                    } else if (intValue5 < intValue4) {
                        float var_ = f8 - ((float) i10);
                        int i27 = i25 * 2;
                        this.rect.set(var_ - ((float) i27), (float) dp3, var_, (float) (i27 + dp3));
                        this.backgroundPath.arcTo(this.rect, 0.0f, -90.0f);
                    }
                    i24--;
                }
                this.backgroundPath.close();
            }
            if (!this.visiblePartSet) {
                this.backgroundHeight = ((ViewGroup) getParent()).getMeasuredHeight();
            }
            Theme.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, 0.0f, this.viewTop + ((float) AndroidUtilities.dp(4.0f)));
            Canvas canvas4 = canvas;
            canvas4.drawPath(this.backgroundPath, Theme.chat_actionBackgroundPaint);
            if (Theme.hasGradientService()) {
                canvas4.drawPath(this.backgroundPath, Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            canvas.save();
            canvas4.translate((float) this.textXLeft, (float) this.textY);
            this.textLayout.draw(canvas4);
            canvas.restore();
            if (this.overrideColorFilter != null) {
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
