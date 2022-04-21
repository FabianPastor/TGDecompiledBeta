package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.PhotoViewer;

public class ChatActionCell extends BaseCell implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private int TAG;
    private AvatarDrawable avatarDrawable;
    private int backgroundHeight;
    private Path backgroundPath;
    private boolean canDrawInParent;
    private int currentAccount;
    private MessageObject currentMessageObject;
    private ImageLocation currentVideoLocation;
    private int customDate;
    private CharSequence customText;
    private ChatActionCellDelegate delegate;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver;
    private boolean invalidateColors;
    private boolean invalidatePath;
    private float lastTouchX;
    private float lastTouchY;
    private ArrayList<Integer> lineHeights;
    private ArrayList<Integer> lineWidths;
    private String overrideBackground;
    private Paint overrideBackgroundPaint;
    private int overrideColor;
    private String overrideText;
    private TextPaint overrideTextPaint;
    private URLSpan pressedLink;
    private int previousWidth;
    private RectF rect;
    public List<SpoilerEffect> spoilers;
    private Stack<SpoilerEffect> spoilersPool;
    private int textHeight;
    private StaticLayout textLayout;
    TextPaint textPaint;
    private int textWidth;
    private int textX;
    private int textXLeft;
    private int textY;
    private ThemeDelegate themeDelegate;
    private float viewTop;
    private boolean visiblePartSet;
    private boolean wasLayout;

    public interface ThemeDelegate extends Theme.ResourcesProvider {
        int getCurrentColor();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.startSpoilers) {
            setSpoilersSuppressed(false);
        } else if (id == NotificationCenter.stopSpoilers) {
            setSpoilersSuppressed(true);
        }
    }

    public void setSpoilersSuppressed(boolean s) {
        for (SpoilerEffect eff : this.spoilers) {
            eff.setSuppressUpdates(s);
        }
    }

    public interface ChatActionCellDelegate {
        void didClickImage(ChatActionCell chatActionCell);

        boolean didLongPress(ChatActionCell chatActionCell, float f, float f2);

        void didPressBotButton(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton);

        void didPressReplyMessage(ChatActionCell chatActionCell, int i);

        void needOpenInviteLink(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void needOpenUserProfile(long j);

        /* renamed from: org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didClickImage(ChatActionCellDelegate _this, ChatActionCell cell) {
            }

            public static boolean $default$didLongPress(ChatActionCellDelegate _this, ChatActionCell cell, float x, float y) {
                return false;
            }

            public static void $default$needOpenUserProfile(ChatActionCellDelegate _this, long uid) {
            }

            public static void $default$didPressBotButton(ChatActionCellDelegate _this, MessageObject messageObject, TLRPC.KeyboardButton button) {
            }

            public static void $default$didPressReplyMessage(ChatActionCellDelegate _this, ChatActionCell cell, int id) {
            }

            public static void $default$needOpenInviteLink(ChatActionCellDelegate _this, TLRPC.TL_chatInviteExported invite) {
            }
        }
    }

    public ChatActionCell(Context context) {
        this(context, false, (ThemeDelegate) null);
    }

    public ChatActionCell(Context context, boolean canDrawInParent2, ThemeDelegate themeDelegate2) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.spoilers = new ArrayList();
        this.spoilersPool = new Stack<>();
        this.lineWidths = new ArrayList<>();
        this.lineHeights = new ArrayList<>();
        this.backgroundPath = new Path();
        this.rect = new RectF();
        this.invalidatePath = true;
        this.invalidateColors = false;
        this.canDrawInParent = canDrawInParent2;
        this.themeDelegate = themeDelegate2;
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
        this.avatarDrawable = new AvatarDrawable();
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    }

    public void setDelegate(ChatActionCellDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setCustomDate(int date, boolean scheduled, boolean inLayout) {
        CharSequence newText;
        int i = this.customDate;
        if (i != date && i / 3600 != date / 3600) {
            if (!scheduled) {
                newText = LocaleController.formatDateChat((long) date);
            } else if (date == NUM) {
                newText = LocaleController.getString("MessageScheduledUntilOnline", NUM);
            } else {
                newText = LocaleController.formatString("MessageScheduledOn", NUM, LocaleController.formatDateChat((long) date));
            }
            this.customDate = date;
            CharSequence charSequence = this.customText;
            if (charSequence == null || !TextUtils.equals(newText, charSequence)) {
                this.customText = newText;
                updateTextInternal(inLayout);
            }
        }
    }

    private void updateTextInternal(boolean inLayout) {
        if (getMeasuredWidth() != 0) {
            createLayout(this.customText, getMeasuredWidth());
            invalidate();
        }
        if (this.wasLayout) {
            buildLayout();
        } else if (inLayout) {
            AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda0(this));
        } else {
            requestLayout();
        }
    }

    public void setCustomText(CharSequence text) {
        this.customText = text;
        if (text != null) {
            updateTextInternal(false);
        }
    }

    public void setOverrideColor(String background, String text) {
        this.overrideBackground = background;
        this.overrideText = text;
    }

    public void setMessageObject(MessageObject messageObject) {
        StaticLayout staticLayout;
        MessageObject messageObject2 = messageObject;
        if (this.currentMessageObject != messageObject2 || (((staticLayout = this.textLayout) != null && !TextUtils.equals(staticLayout.getText(), messageObject2.messageText)) || (!this.hasReplyMessage && messageObject2.replyMessageObject != null))) {
            this.currentMessageObject = messageObject2;
            this.hasReplyMessage = messageObject2.replyMessageObject != null;
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.previousWidth = 0;
            if (this.currentMessageObject.type == 11) {
                this.avatarDrawable.setInfo(messageObject.getDialogId(), (String) null, (String) null);
                if (this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, this.currentMessageObject, 0);
                } else {
                    TLRPC.PhotoSize strippedPhotoSize = null;
                    int a = 0;
                    int N = this.currentMessageObject.photoThumbs.size();
                    while (true) {
                        if (a >= N) {
                            break;
                        }
                        TLRPC.PhotoSize photoSize = this.currentMessageObject.photoThumbs.get(a);
                        if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                            strippedPhotoSize = photoSize;
                            break;
                        }
                        a++;
                    }
                    TLRPC.PhotoSize photoSize2 = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, 640);
                    if (photoSize2 != null) {
                        TLRPC.Photo photo = messageObject2.messageOwner.action.photo;
                        TLRPC.VideoSize videoSize = null;
                        if (!photo.video_sizes.isEmpty() && SharedConfig.autoplayGifs) {
                            videoSize = photo.video_sizes.get(0);
                            if (!messageObject2.mediaExists && !DownloadController.getInstance(this.currentAccount).canDownloadMedia(4, videoSize.size)) {
                                this.currentVideoLocation = ImageLocation.getForPhoto(videoSize, photo);
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(videoSize), this.currentMessageObject, this);
                                videoSize = null;
                            }
                        }
                        if (videoSize != null) {
                            this.imageReceiver.setImage(ImageLocation.getForPhoto(videoSize, photo), "g", ImageLocation.getForObject(strippedPhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
                        } else {
                            this.imageReceiver.setImage(ImageLocation.getForObject(photoSize2, this.currentMessageObject.photoThumbsObject), "150_150", ImageLocation.getForObject(strippedPhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
                        }
                    } else {
                        this.imageReceiver.setImageBitmap((Drawable) this.avatarDrawable);
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

    public void setVisiblePart(float visibleTop, int parentH) {
        this.visiblePartSet = true;
        this.backgroundHeight = parentH;
        this.viewTop = visibleTop;
    }

    /* access modifiers changed from: protected */
    public boolean onLongPress() {
        ChatActionCellDelegate chatActionCellDelegate = this.delegate;
        if (chatActionCellDelegate != null) {
            return chatActionCellDelegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
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

    public boolean onTouchEvent(MotionEvent event) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(event);
        }
        float x = event.getX();
        this.lastTouchX = x;
        float y = event.getY();
        this.lastTouchY = y;
        boolean result = false;
        if (event.getAction() != 0) {
            if (event.getAction() != 2) {
                cancelCheckLongPress();
            }
            if (this.imagePressed) {
                if (event.getAction() == 1) {
                    this.imagePressed = false;
                    ChatActionCellDelegate chatActionCellDelegate = this.delegate;
                    if (chatActionCellDelegate != null) {
                        chatActionCellDelegate.didClickImage(this);
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
            int i = this.textX;
            if (x >= ((float) i)) {
                int i2 = this.textY;
                if (y >= ((float) i2) && x <= ((float) (i + this.textWidth)) && y <= ((float) (this.textHeight + i2))) {
                    float x2 = x - ((float) this.textXLeft);
                    int line = this.textLayout.getLineForVertical((int) (y - ((float) i2)));
                    int off = this.textLayout.getOffsetForHorizontal(line, x2);
                    float left = this.textLayout.getLineLeft(line);
                    if (left > x2 || this.textLayout.getLineWidth(line) + left < x2 || !(this.currentMessageObject.messageText instanceof Spannable)) {
                        this.pressedLink = null;
                    } else {
                        URLSpan[] link = (URLSpan[]) ((Spannable) this.currentMessageObject.messageText).getSpans(off, off, URLSpan.class);
                        if (link.length == 0) {
                            this.pressedLink = null;
                        } else if (event.getAction() == 0) {
                            this.pressedLink = link[0];
                            result = true;
                        } else if (link[0] == this.pressedLink) {
                            if (this.delegate != null) {
                                String url = link[0].getURL();
                                if (url.startsWith("invite")) {
                                    URLSpan uRLSpan = this.pressedLink;
                                    if (uRLSpan instanceof URLSpanNoUnderline) {
                                        TLObject object = ((URLSpanNoUnderline) uRLSpan).getObject();
                                        if (object instanceof TLRPC.TL_chatInviteExported) {
                                            this.delegate.needOpenInviteLink((TLRPC.TL_chatInviteExported) object);
                                        }
                                    }
                                }
                                if (url.startsWith("game")) {
                                    this.delegate.didPressReplyMessage(this, this.currentMessageObject.getReplyMsgId());
                                } else if (url.startsWith("http")) {
                                    Browser.openUrl(getContext(), url);
                                } else {
                                    this.delegate.needOpenUserProfile(Long.parseLong(url));
                                }
                            }
                            result = true;
                        }
                    }
                }
            }
            this.pressedLink = null;
        }
        if (!result) {
            return super.onTouchEvent(event);
        }
        return result;
    }

    private void createLayout(CharSequence text, int width) {
        int maxWidth = width - AndroidUtilities.dp(30.0f);
        this.invalidatePath = true;
        this.textLayout = new StaticLayout(text, (TextPaint) getThemedPaint("paintChatActionText"), maxWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        this.spoilersPool.addAll(this.spoilers);
        this.spoilers.clear();
        if (text instanceof Spannable) {
            SpoilerEffect.addSpoilers(this, this.textLayout, (Spannable) text, this.spoilersPool, this.spoilers);
        }
        this.textHeight = 0;
        this.textWidth = 0;
        try {
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
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return;
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        this.textX = (width - this.textWidth) / 2;
        this.textY = AndroidUtilities.dp(7.0f);
        this.textXLeft = (width - this.textLayout.getWidth()) / 2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.currentMessageObject == null && this.customText == null) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), this.textHeight + AndroidUtilities.dp(14.0f));
            return;
        }
        int width = Math.max(AndroidUtilities.dp(30.0f), View.MeasureSpec.getSize(widthMeasureSpec));
        if (this.previousWidth != width) {
            this.wasLayout = true;
            this.previousWidth = width;
            buildLayout();
        }
        int i = this.textHeight;
        MessageObject messageObject = this.currentMessageObject;
        setMeasuredDimension(width, i + ((messageObject == null || messageObject.type != 11) ? 0 : AndroidUtilities.roundMessageSize + AndroidUtilities.dp(10.0f)) + AndroidUtilities.dp(14.0f));
    }

    private void buildLayout() {
        CharSequence text;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            text = this.customText;
        } else if (messageObject.messageOwner == null || this.currentMessageObject.messageOwner.media == null || this.currentMessageObject.messageOwner.media.ttl_seconds == 0) {
            text = this.currentMessageObject.messageText;
        } else if (this.currentMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photoEmpty) {
            text = LocaleController.getString("AttachPhotoExpired", NUM);
        } else if (this.currentMessageObject.messageOwner.media.document instanceof TLRPC.TL_documentEmpty) {
            text = LocaleController.getString("AttachVideoExpired", NUM);
        } else {
            text = this.currentMessageObject.messageText;
        }
        createLayout(text, this.previousWidth);
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && messageObject2.type == 11) {
            this.imageReceiver.setImageCoords((float) ((this.previousWidth - AndroidUtilities.roundMessageSize) / 2), (float) (this.textHeight + AndroidUtilities.dp(19.0f)), (float) AndroidUtilities.roundMessageSize, (float) AndroidUtilities.roundMessageSize);
        }
    }

    public int getCustomDate() {
        return this.customDate;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            this.imageReceiver.draw(canvas);
        }
        if (this.textLayout != null) {
            drawBackground(canvas, false);
            if (this.textPaint != null) {
                canvas.save();
                canvas.translate((float) this.textXLeft, (float) this.textY);
                if (this.textLayout.getPaint() != this.textPaint) {
                    buildLayout();
                }
                canvas.save();
                SpoilerEffect.clipOutCanvas(canvas, this.spoilers);
                this.textLayout.draw(canvas);
                canvas.restore();
                for (SpoilerEffect eff : this.spoilers) {
                    eff.setColor(this.textLayout.getPaint().getColor());
                    eff.draw(canvas);
                }
                canvas.restore();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:114:0x0345  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01f6  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x020a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x021b  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0252  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawBackground(android.graphics.Canvas r28, boolean r29) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            boolean r2 = r0.canDrawInParent
            if (r2 == 0) goto L_0x001a
            boolean r2 = r27.hasGradientService()
            if (r2 == 0) goto L_0x0011
            if (r29 != 0) goto L_0x0011
            return
        L_0x0011:
            boolean r2 = r27.hasGradientService()
            if (r2 != 0) goto L_0x001a
            if (r29 == 0) goto L_0x001a
            return
        L_0x001a:
            java.lang.String r2 = "paintChatActionBackground"
            android.graphics.Paint r2 = r0.getThemedPaint(r2)
            java.lang.String r3 = "paintChatActionText"
            android.graphics.Paint r3 = r0.getThemedPaint(r3)
            android.text.TextPaint r3 = (android.text.TextPaint) r3
            r0.textPaint = r3
            java.lang.String r3 = r0.overrideBackground
            if (r3 == 0) goto L_0x0077
            int r3 = r0.getThemedColor(r3)
            android.graphics.Paint r4 = r0.overrideBackgroundPaint
            if (r4 != 0) goto L_0x0071
            android.graphics.Paint r4 = new android.graphics.Paint
            r5 = 1
            r4.<init>(r5)
            r0.overrideBackgroundPaint = r4
            r4.setColor(r3)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r5)
            r0.overrideTextPaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = r0.overrideTextPaint
            r5 = 16
            int r6 = org.telegram.messenger.SharedConfig.fontSize
            int r5 = java.lang.Math.max(r5, r6)
            int r5 = r5 + -2
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r4.setTextSize(r5)
            android.text.TextPaint r4 = r0.overrideTextPaint
            java.lang.String r5 = r0.overrideText
            int r5 = r0.getThemedColor(r5)
            r4.setColor(r5)
        L_0x0071:
            android.graphics.Paint r2 = r0.overrideBackgroundPaint
            android.text.TextPaint r4 = r0.overrideTextPaint
            r0.textPaint = r4
        L_0x0077:
            boolean r3 = r0.invalidatePath
            r4 = 1082130432(0x40800000, float:4.0)
            if (r3 == 0) goto L_0x039e
            r3 = 0
            r0.invalidatePath = r3
            java.util.ArrayList<java.lang.Integer> r6 = r0.lineWidths
            r6.clear()
            android.text.StaticLayout r6 = r0.textLayout
            int r6 = r6.getLineCount()
            r7 = 1093664768(0x41300000, float:11.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 0
            r10 = 0
        L_0x0099:
            if (r10 >= r6) goto L_0x00bf
            android.text.StaticLayout r11 = r0.textLayout
            float r11 = r11.getLineWidth(r10)
            double r11 = (double) r11
            double r11 = java.lang.Math.ceil(r11)
            int r11 = (int) r11
            if (r10 == 0) goto L_0x00b2
            int r12 = r9 - r11
            if (r12 <= 0) goto L_0x00b2
            int r13 = r7 + r8
            if (r12 > r13) goto L_0x00b2
            r11 = r9
        L_0x00b2:
            java.util.ArrayList<java.lang.Integer> r12 = r0.lineWidths
            java.lang.Integer r13 = java.lang.Integer.valueOf(r11)
            r12.add(r13)
            r9 = r11
            int r10 = r10 + 1
            goto L_0x0099
        L_0x00bf:
            int r10 = r6 + -2
        L_0x00c1:
            if (r10 < 0) goto L_0x00e5
            java.util.ArrayList<java.lang.Integer> r11 = r0.lineWidths
            java.lang.Object r11 = r11.get(r10)
            java.lang.Integer r11 = (java.lang.Integer) r11
            int r11 = r11.intValue()
            int r12 = r9 - r11
            if (r12 <= 0) goto L_0x00d8
            int r13 = r7 + r8
            if (r12 > r13) goto L_0x00d8
            r11 = r9
        L_0x00d8:
            java.util.ArrayList<java.lang.Integer> r13 = r0.lineWidths
            java.lang.Integer r14 = java.lang.Integer.valueOf(r11)
            r13.set(r10, r14)
            r9 = r11
            int r10 = r10 + -1
            goto L_0x00c1
        L_0x00e5:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r11 = r27.getMeasuredWidth()
            int r11 = r11 / 2
            r12 = 0
            r13 = 1077936128(0x40400000, float:3.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r3 = r7 - r14
            java.util.ArrayList<java.lang.Integer> r4 = r0.lineHeights
            r4.clear()
            android.graphics.Path r4 = r0.backgroundPath
            r4.reset()
            android.graphics.Path r4 = r0.backgroundPath
            float r5 = (float) r11
            float r13 = (float) r10
            r4.moveTo(r5, r13)
            r4 = 0
        L_0x0110:
            if (r4 >= r6) goto L_0x0285
            java.util.ArrayList<java.lang.Integer> r5 = r0.lineWidths
            java.lang.Object r5 = r5.get(r4)
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            android.text.StaticLayout r13 = r0.textLayout
            int r13 = r13.getLineBottom(r4)
            int r1 = r6 + -1
            if (r4 >= r1) goto L_0x0139
            java.util.ArrayList<java.lang.Integer> r1 = r0.lineWidths
            r18 = r2
            int r2 = r4 + 1
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            goto L_0x013c
        L_0x0139:
            r18 = r2
            r1 = 0
        L_0x013c:
            int r2 = r13 - r12
            if (r4 == 0) goto L_0x0142
            if (r5 <= r9) goto L_0x014a
        L_0x0142:
            r16 = 1077936128(0x40400000, float:3.0)
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r2 = r2 + r19
        L_0x014a:
            r19 = r12
            int r12 = r6 + -1
            if (r4 == r12) goto L_0x0152
            if (r5 <= r1) goto L_0x015a
        L_0x0152:
            r12 = 1077936128(0x40400000, float:3.0)
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r2 = r2 + r20
        L_0x015a:
            r12 = r13
            r19 = r12
            float r12 = (float) r11
            r20 = r13
            float r13 = (float) r5
            r21 = 1073741824(0x40000000, float:2.0)
            float r13 = r13 / r21
            float r12 = r12 + r13
            int r13 = r6 + -1
            if (r4 == r13) goto L_0x0172
            if (r5 >= r1) goto L_0x0172
            if (r4 == 0) goto L_0x0172
            if (r5 >= r9) goto L_0x0172
            r13 = r15
            goto L_0x0173
        L_0x0172:
            r13 = r8
        L_0x0173:
            if (r4 == 0) goto L_0x01b5
            if (r5 <= r9) goto L_0x0182
            r25 = r1
            r21 = r8
            r24 = r9
            r23 = r11
            r22 = r15
            goto L_0x01bf
        L_0x0182:
            if (r5 >= r9) goto L_0x01aa
            r21 = r8
            android.graphics.RectF r8 = r0.rect
            r22 = r15
            float r15 = (float) r3
            float r15 = r15 + r12
            r23 = r11
            float r11 = (float) r10
            r24 = r9
            float r9 = (float) r3
            float r9 = r9 + r12
            r25 = r1
            int r1 = r13 * 2
            float r1 = (float) r1
            float r9 = r9 + r1
            int r1 = r13 * 2
            int r1 = r1 + r10
            float r1 = (float) r1
            r8.set(r15, r11, r9, r1)
            android.graphics.Path r1 = r0.backgroundPath
            android.graphics.RectF r8 = r0.rect
            r9 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r1.arcTo(r8, r9, r9)
            goto L_0x01db
        L_0x01aa:
            r25 = r1
            r21 = r8
            r24 = r9
            r23 = r11
            r22 = r15
            goto L_0x01db
        L_0x01b5:
            r25 = r1
            r21 = r8
            r24 = r9
            r23 = r11
            r22 = r15
        L_0x01bf:
            android.graphics.RectF r1 = r0.rect
            float r8 = (float) r14
            float r8 = r12 - r8
            float r9 = (float) r7
            float r8 = r8 - r9
            float r9 = (float) r10
            float r11 = (float) r3
            float r11 = r11 + r12
            int r15 = r7 * 2
            int r15 = r15 + r10
            float r15 = (float) r15
            r1.set(r8, r9, r11, r15)
            android.graphics.Path r1 = r0.backgroundPath
            android.graphics.RectF r8 = r0.rect
            r9 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r11 = 1119092736(0x42b40000, float:90.0)
            r1.arcTo(r8, r9, r11)
        L_0x01db:
            int r10 = r10 + r2
            r1 = r10
            int r8 = r6 + -1
            if (r4 == r8) goto L_0x01f2
            r8 = r25
            if (r5 >= r8) goto L_0x01f4
            r9 = 1077936128(0x40400000, float:3.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r10 = r10 - r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r2 = r2 - r11
            goto L_0x01f4
        L_0x01f2:
            r8 = r25
        L_0x01f4:
            if (r4 == 0) goto L_0x020a
            r9 = r24
            if (r5 >= r9) goto L_0x0207
            r11 = 1077936128(0x40400000, float:3.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r2 = r2 - r15
            goto L_0x020e
        L_0x0207:
            r11 = 1077936128(0x40400000, float:3.0)
            goto L_0x020e
        L_0x020a:
            r9 = r24
            r11 = 1077936128(0x40400000, float:3.0)
        L_0x020e:
            java.util.ArrayList<java.lang.Integer> r15 = r0.lineHeights
            java.lang.Integer r11 = java.lang.Integer.valueOf(r2)
            r15.add(r11)
            int r11 = r6 + -1
            if (r4 == r11) goto L_0x0252
            if (r5 <= r8) goto L_0x0224
            r24 = r1
            r25 = r2
            r26 = r8
            goto L_0x0258
        L_0x0224:
            if (r5 >= r8) goto L_0x024b
            android.graphics.RectF r11 = r0.rect
            float r15 = (float) r3
            float r15 = r15 + r12
            int r17 = r13 * 2
            r24 = r1
            int r1 = r10 - r17
            float r1 = (float) r1
            r25 = r2
            float r2 = (float) r3
            float r2 = r2 + r12
            r26 = r8
            int r8 = r13 * 2
            float r8 = (float) r8
            float r2 = r2 + r8
            float r8 = (float) r10
            r11.set(r15, r1, r2, r8)
            android.graphics.Path r1 = r0.backgroundPath
            android.graphics.RectF r2 = r0.rect
            r8 = 1127481344(0x43340000, float:180.0)
            r11 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r1.arcTo(r2, r8, r11)
            goto L_0x0274
        L_0x024b:
            r24 = r1
            r25 = r2
            r26 = r8
            goto L_0x0274
        L_0x0252:
            r24 = r1
            r25 = r2
            r26 = r8
        L_0x0258:
            android.graphics.RectF r1 = r0.rect
            float r2 = (float) r14
            float r2 = r12 - r2
            float r8 = (float) r7
            float r2 = r2 - r8
            int r8 = r7 * 2
            int r8 = r10 - r8
            float r8 = (float) r8
            float r11 = (float) r3
            float r11 = r11 + r12
            float r15 = (float) r10
            r1.set(r2, r8, r11, r15)
            android.graphics.Path r1 = r0.backgroundPath
            android.graphics.RectF r2 = r0.rect
            r8 = 0
            r11 = 1119092736(0x42b40000, float:90.0)
            r1.arcTo(r2, r8, r11)
        L_0x0274:
            r9 = r5
            int r4 = r4 + 1
            r1 = r28
            r2 = r18
            r12 = r19
            r8 = r21
            r15 = r22
            r11 = r23
            goto L_0x0110
        L_0x0285:
            r18 = r2
            r21 = r8
            r23 = r11
            r19 = r12
            r22 = r15
            int r1 = r6 + -1
        L_0x0291:
            if (r1 < 0) goto L_0x0398
            if (r1 == 0) goto L_0x02a4
            java.util.ArrayList<java.lang.Integer> r2 = r0.lineWidths
            int r4 = r1 + -1
            java.lang.Object r2 = r2.get(r4)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            goto L_0x02a5
        L_0x02a4:
            r2 = 0
        L_0x02a5:
            r9 = r2
            java.util.ArrayList<java.lang.Integer> r2 = r0.lineWidths
            java.lang.Object r2 = r2.get(r1)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r4 = r6 + -1
            if (r1 == r4) goto L_0x02c5
            java.util.ArrayList<java.lang.Integer> r4 = r0.lineWidths
            int r5 = r1 + 1
            java.lang.Object r4 = r4.get(r5)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            goto L_0x02c6
        L_0x02c5:
            r4 = 0
        L_0x02c6:
            android.text.StaticLayout r5 = r0.textLayout
            int r5 = r5.getLineBottom(r1)
            int r8 = r2 / 2
            int r11 = r23 - r8
            float r8 = (float) r11
            int r11 = r6 + -1
            if (r1 == r11) goto L_0x02de
            if (r2 >= r4) goto L_0x02de
            if (r1 == 0) goto L_0x02de
            if (r2 >= r9) goto L_0x02de
            r11 = r22
            goto L_0x02e0
        L_0x02de:
            r11 = r21
        L_0x02e0:
            int r12 = r6 + -1
            if (r1 == r12) goto L_0x0317
            if (r2 <= r4) goto L_0x02eb
            r16 = r4
            r20 = r5
            goto L_0x031b
        L_0x02eb:
            if (r2 >= r4) goto L_0x0312
            android.graphics.RectF r12 = r0.rect
            float r13 = (float) r3
            float r13 = r8 - r13
            int r15 = r11 * 2
            float r15 = (float) r15
            float r13 = r13 - r15
            int r15 = r11 * 2
            int r15 = r10 - r15
            float r15 = (float) r15
            r16 = r4
            float r4 = (float) r3
            float r4 = r8 - r4
            r20 = r5
            float r5 = (float) r10
            r12.set(r13, r15, r4, r5)
            android.graphics.Path r4 = r0.backgroundPath
            android.graphics.RectF r5 = r0.rect
            r12 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r13 = 1119092736(0x42b40000, float:90.0)
            r4.arcTo(r5, r13, r12)
            goto L_0x0336
        L_0x0312:
            r16 = r4
            r20 = r5
            goto L_0x0336
        L_0x0317:
            r16 = r4
            r20 = r5
        L_0x031b:
            android.graphics.RectF r4 = r0.rect
            float r5 = (float) r3
            float r5 = r8 - r5
            int r12 = r7 * 2
            int r12 = r10 - r12
            float r12 = (float) r12
            float r13 = (float) r14
            float r13 = r13 + r8
            float r15 = (float) r7
            float r13 = r13 + r15
            float r15 = (float) r10
            r4.set(r5, r12, r13, r15)
            android.graphics.Path r4 = r0.backgroundPath
            android.graphics.RectF r5 = r0.rect
            r12 = 1119092736(0x42b40000, float:90.0)
            r4.arcTo(r5, r12, r12)
        L_0x0336:
            java.util.ArrayList<java.lang.Integer> r4 = r0.lineHeights
            java.lang.Object r4 = r4.get(r1)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            int r10 = r10 - r4
            if (r1 == 0) goto L_0x0376
            if (r2 <= r9) goto L_0x034a
            r13 = -1028390912(0xffffffffc2b40000, float:-90.0)
            goto L_0x0378
        L_0x034a:
            if (r2 >= r9) goto L_0x036f
            android.graphics.RectF r4 = r0.rect
            float r5 = (float) r3
            float r5 = r8 - r5
            int r12 = r11 * 2
            float r12 = (float) r12
            float r5 = r5 - r12
            float r12 = (float) r10
            float r13 = (float) r3
            float r13 = r8 - r13
            int r15 = r11 * 2
            int r15 = r15 + r10
            float r15 = (float) r15
            r4.set(r5, r12, r13, r15)
            android.graphics.Path r4 = r0.backgroundPath
            android.graphics.RectF r5 = r0.rect
            r12 = 0
            r13 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r4.arcTo(r5, r12, r13)
            r12 = 1127481344(0x43340000, float:180.0)
            r13 = 1119092736(0x42b40000, float:90.0)
            goto L_0x0394
        L_0x036f:
            r13 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r12 = 1127481344(0x43340000, float:180.0)
            r13 = 1119092736(0x42b40000, float:90.0)
            goto L_0x0394
        L_0x0376:
            r13 = -1028390912(0xffffffffc2b40000, float:-90.0)
        L_0x0378:
            android.graphics.RectF r4 = r0.rect
            float r5 = (float) r3
            float r5 = r8 - r5
            float r12 = (float) r10
            float r15 = (float) r14
            float r15 = r15 + r8
            float r13 = (float) r7
            float r15 = r15 + r13
            int r13 = r7 * 2
            int r13 = r13 + r10
            float r13 = (float) r13
            r4.set(r5, r12, r15, r13)
            android.graphics.Path r4 = r0.backgroundPath
            android.graphics.RectF r5 = r0.rect
            r12 = 1127481344(0x43340000, float:180.0)
            r13 = 1119092736(0x42b40000, float:90.0)
            r4.arcTo(r5, r12, r13)
        L_0x0394:
            int r1 = r1 + -1
            goto L_0x0291
        L_0x0398:
            android.graphics.Path r1 = r0.backgroundPath
            r1.close()
            goto L_0x03a0
        L_0x039e:
            r18 = r2
        L_0x03a0:
            boolean r1 = r0.visiblePartSet
            if (r1 != 0) goto L_0x03b0
            android.view.ViewParent r1 = r27.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r2 = r1.getMeasuredHeight()
            r0.backgroundHeight = r2
        L_0x03b0:
            org.telegram.ui.Cells.ChatActionCell$ThemeDelegate r1 = r0.themeDelegate
            if (r1 == 0) goto L_0x03c9
            int r2 = r27.getMeasuredWidth()
            int r3 = r0.backgroundHeight
            float r4 = r0.viewTop
            r5 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 + r5
            r6 = 0
            r1.applyServiceShaderMatrix(r2, r3, r6, r4)
            goto L_0x03dd
        L_0x03c9:
            r5 = 1082130432(0x40800000, float:4.0)
            r6 = 0
            int r1 = r27.getMeasuredWidth()
            int r2 = r0.backgroundHeight
            float r3 = r0.viewTop
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            float r3 = r3 + r4
            org.telegram.ui.ActionBar.Theme.applyServiceShaderMatrix(r1, r2, r6, r3)
        L_0x03dd:
            r1 = -1
            r2 = -1
            if (r29 == 0) goto L_0x0410
            float r3 = r27.getAlpha()
            r4 = 1065353216(0x3var_, float:1.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x0410
            int r1 = r18.getAlpha()
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundGradientDarkenPaint
            int r2 = r3.getAlpha()
            float r3 = (float) r1
            float r4 = r27.getAlpha()
            float r3 = r3 * r4
            int r3 = (int) r3
            r4 = r18
            r4.setAlpha(r3)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundGradientDarkenPaint
            float r5 = (float) r2
            float r6 = r27.getAlpha()
            float r5 = r5 * r6
            int r5 = (int) r5
            r3.setAlpha(r5)
            goto L_0x0412
        L_0x0410:
            r4 = r18
        L_0x0412:
            android.graphics.Path r3 = r0.backgroundPath
            r5 = r28
            r5.drawPath(r3, r4)
            boolean r3 = r27.hasGradientService()
            if (r3 == 0) goto L_0x0426
            android.graphics.Path r3 = r0.backgroundPath
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundGradientDarkenPaint
            r5.drawPath(r3, r6)
        L_0x0426:
            if (r1 < 0) goto L_0x0430
            r4.setAlpha(r1)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundGradientDarkenPaint
            r3.setAlpha(r2)
        L_0x0430:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.drawBackground(android.graphics.Canvas, boolean):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.themeDelegate;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasGradientService() {
        /*
            r1 = this;
            android.graphics.Paint r0 = r1.overrideBackgroundPaint
            if (r0 != 0) goto L_0x0017
            org.telegram.ui.Cells.ChatActionCell$ThemeDelegate r0 = r1.themeDelegate
            if (r0 == 0) goto L_0x000f
            boolean r0 = r0.hasGradientService()
            if (r0 == 0) goto L_0x0017
            goto L_0x0015
        L_0x000f:
            boolean r0 = org.telegram.ui.ActionBar.Theme.hasGradientService()
            if (r0 == 0) goto L_0x0017
        L_0x0015:
            r0 = 1
            goto L_0x0018
        L_0x0017:
            r0 = 0
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.hasGradientService():boolean");
    }

    public void onFailedDownload(String fileName, boolean canceled) {
    }

    public void onSuccessDownload(String fileName) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            TLRPC.PhotoSize strippedPhotoSize = null;
            int a = 0;
            int N = this.currentMessageObject.photoThumbs.size();
            while (true) {
                if (a >= N) {
                    break;
                }
                TLRPC.PhotoSize photoSize = this.currentMessageObject.photoThumbs.get(a);
                if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                    strippedPhotoSize = photoSize;
                    break;
                }
                a++;
            }
            this.imageReceiver.setImage(this.currentVideoLocation, "g", ImageLocation.getForObject(strippedPhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        }
    }

    public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
    }

    public void onProgressUpload(String fileName, long downloadSize, long totalSize, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (!TextUtils.isEmpty(this.customText) || this.currentMessageObject != null) {
            info.setText(!TextUtils.isEmpty(this.customText) ? this.customText : this.currentMessageObject.messageText);
            info.setEnabled(true);
        }
    }

    public void setInvalidateColors(boolean invalidate) {
        if (this.invalidateColors != invalidate) {
            this.invalidateColors = invalidate;
            invalidate();
        }
    }

    private int getThemedColor(String key) {
        ThemeDelegate themeDelegate2 = this.themeDelegate;
        Integer color = themeDelegate2 != null ? themeDelegate2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        ThemeDelegate themeDelegate2 = this.themeDelegate;
        Paint paint = themeDelegate2 != null ? themeDelegate2.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }
}
