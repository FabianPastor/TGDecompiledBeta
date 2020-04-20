package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Property;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC$BotInlineMessage;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_webDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.PhotoViewer;

public class ContextLinkCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    public final Property<ContextLinkCell, Float> IMAGE_SCALE;
    private int TAG;
    /* access modifiers changed from: private */
    public AnimatorSet animator;
    private Paint backgroundPaint;
    private boolean buttonPressed;
    private int buttonState;
    private boolean canPreviewGif;
    private CheckBox2 checkBox;
    private int currentAccount;
    private int currentDate;
    private MessageObject currentMessageObject;
    private TLRPC$PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY;
    private TLRPC$Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    /* access modifiers changed from: private */
    public float imageScale;
    private TLRPC$BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private Object parentObject;
    private TLRPC$Photo photoAttach;
    private RadialProgress2 radialProgress;
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY;

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    static {
        new AccelerateInterpolator(0.5f);
    }

    public ContextLinkCell(Context context) {
        this(context, false);
    }

    public ContextLinkCell(Context context, boolean z) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.titleY = AndroidUtilities.dp(7.0f);
        this.descriptionY = AndroidUtilities.dp(27.0f);
        this.imageScale = 1.0f;
        this.IMAGE_SCALE = new AnimationProperties.FloatProperty<ContextLinkCell>("animationValue") {
            public void setValue(ContextLinkCell contextLinkCell, float f) {
                float unused = ContextLinkCell.this.imageScale = f;
                ContextLinkCell.this.invalidate();
            }

            public Float get(ContextLinkCell contextLinkCell) {
                return Float.valueOf(ContextLinkCell.this.imageScale);
            }
        };
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setLayerNum(1);
        this.linkImageView.setUseSharedAnimationQueue(true);
        this.letterDrawable = new LetterDrawable();
        this.radialProgress = new RadialProgress2(this);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setFocusable(true);
        if (z) {
            Paint paint = new Paint();
            this.backgroundPaint = paint;
            paint.setColor(Theme.getColor("sharedMedia_photoPlaceholder"));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor((String) null, "sharedMedia_photoPlaceholder", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(1);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 1.0f, 1.0f, 0.0f));
        }
        setWillNotDraw(false);
    }

    /* JADX WARNING: type inference failed for: r6v2, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r11v37, types: [org.telegram.tgnet.TLRPC$WebDocument] */
    /* JADX WARNING: type inference failed for: r11v40, types: [org.telegram.tgnet.TLRPC$WebDocument] */
    /* JADX WARNING: type inference failed for: r6v8 */
    /* JADX WARNING: type inference failed for: r6v9 */
    /* JADX WARNING: type inference failed for: r6v10 */
    /* JADX WARNING: type inference failed for: r6v11 */
    /* JADX WARNING: type inference failed for: r6v12 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01d6, code lost:
        if (r0 == r7.currentPhotoObject) goto L_0x01d8;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x024e  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x026c  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0288  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0290  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0296  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02c6  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02d2  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02fe  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0369  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0371  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x04a6  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x04e0  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x05aa  */
    /* JADX WARNING: Removed duplicated region for block: B:214:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0078 A[SYNTHETIC, Splitter:B:22:0x0078] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00d6 A[SYNTHETIC, Splitter:B:29:0x00d6] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0125  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x012f A[SYNTHETIC, Splitter:B:43:0x012f] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0179  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01be  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x021b  */
    /* JADX WARNING: Unknown variable types count: 2 */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r39, int r40) {
        /*
            r38 = this;
            r7 = r38
            r1 = 0
            r7.drawLinkImageView = r1
            r2 = 0
            r7.descriptionLayout = r2
            r7.titleLayout = r2
            r7.linkLayout = r2
            r7.currentPhotoObject = r2
            r0 = 1104674816(0x41d80000, float:27.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.linkY = r0
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            if (r0 != 0) goto L_0x002c
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            if (r0 != 0) goto L_0x002c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r7.setMeasuredDimension(r0, r1)
            return
        L_0x002c:
            int r4 = android.view.View.MeasureSpec.getSize(r39)
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = r4 - r0
            r5 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r6 = r0 - r6
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            if (r0 == 0) goto L_0x0050
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$Document r8 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.thumbs
            r0.<init>(r8)
        L_0x004e:
            r15 = r0
            goto L_0x0065
        L_0x0050:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            if (r0 == 0) goto L_0x0064
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x0064
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r7.inlineResult
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            r0.<init>(r8)
            goto L_0x004e
        L_0x0064:
            r15 = r2
        L_0x0065:
            boolean r0 = r7.mediaWebpage
            r16 = 1082130432(0x40800000, float:4.0)
            r14 = 1
            if (r0 != 0) goto L_0x016f
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            if (r0 == 0) goto L_0x016f
            java.lang.String r0 = r0.title
            r13 = 32
            r12 = 10
            if (r0 == 0) goto L_0x00d0
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00c3 }
            float r0 = r8.measureText(r0)     // Catch:{ Exception -> 0x00c3 }
            double r8 = (double) r0     // Catch:{ Exception -> 0x00c3 }
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x00c3 }
            int r0 = (int) r8     // Catch:{ Exception -> 0x00c3 }
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r7.inlineResult     // Catch:{ Exception -> 0x00c3 }
            java.lang.String r8 = r8.title     // Catch:{ Exception -> 0x00c3 }
            java.lang.String r8 = r8.replace(r12, r13)     // Catch:{ Exception -> 0x00c3 }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00c3 }
            android.graphics.Paint$FontMetricsInt r9 = r9.getFontMetricsInt()     // Catch:{ Exception -> 0x00c3 }
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ Exception -> 0x00c3 }
            java.lang.CharSequence r8 = org.telegram.messenger.Emoji.replaceEmoji(r8, r9, r10, r1)     // Catch:{ Exception -> 0x00c3 }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00c3 }
            int r0 = java.lang.Math.min(r0, r6)     // Catch:{ Exception -> 0x00c3 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x00c3 }
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x00c3 }
            java.lang.CharSequence r18 = android.text.TextUtils.ellipsize(r8, r9, r0, r10)     // Catch:{ Exception -> 0x00c3 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x00c3 }
            android.text.TextPaint r19 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00c3 }
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x00c3 }
            int r20 = r6 + r8
            android.text.Layout$Alignment r21 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x00c3 }
            r22 = 1065353216(0x3var_, float:1.0)
            r23 = 0
            r24 = 0
            r17 = r0
            r17.<init>(r18, r19, r20, r21, r22, r23, r24)     // Catch:{ Exception -> 0x00c3 }
            r7.titleLayout = r0     // Catch:{ Exception -> 0x00c3 }
            goto L_0x00c7
        L_0x00c3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c7:
            org.telegram.ui.Components.LetterDrawable r0 = r7.letterDrawable
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r7.inlineResult
            java.lang.String r8 = r8.title
            r0.setTitle(r8)
        L_0x00d0:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x0125
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x011c }
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()     // Catch:{ Exception -> 0x011c }
            r9 = 1095761920(0x41500000, float:13.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x011c }
            java.lang.CharSequence r8 = org.telegram.messenger.Emoji.replaceEmoji(r0, r8, r9, r1)     // Catch:{ Exception -> 0x011c }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x011c }
            r0 = 0
            r17 = 3
            r10 = r6
            r11 = r6
            r2 = 10
            r12 = r0
            r5 = 32
            r13 = r17
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x011a }
            r7.descriptionLayout = r0     // Catch:{ Exception -> 0x011a }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x011a }
            if (r0 <= 0) goto L_0x0129
            int r0 = r7.descriptionY     // Catch:{ Exception -> 0x011a }
            android.text.StaticLayout r8 = r7.descriptionLayout     // Catch:{ Exception -> 0x011a }
            android.text.StaticLayout r9 = r7.descriptionLayout     // Catch:{ Exception -> 0x011a }
            int r9 = r9.getLineCount()     // Catch:{ Exception -> 0x011a }
            int r9 = r9 - r14
            int r8 = r8.getLineBottom(r9)     // Catch:{ Exception -> 0x011a }
            int r0 = r0 + r8
            r8 = 1065353216(0x3var_, float:1.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ Exception -> 0x011a }
            int r0 = r0 + r8
            r7.linkY = r0     // Catch:{ Exception -> 0x011a }
            goto L_0x0129
        L_0x011a:
            r0 = move-exception
            goto L_0x0121
        L_0x011c:
            r0 = move-exception
            r2 = 10
            r5 = 32
        L_0x0121:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0129
        L_0x0125:
            r2 = 10
            r5 = 32
        L_0x0129:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            java.lang.String r0 = r0.url
            if (r0 == 0) goto L_0x016f
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0168 }
            float r0 = r8.measureText(r0)     // Catch:{ Exception -> 0x0168 }
            double r8 = (double) r0     // Catch:{ Exception -> 0x0168 }
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x0168 }
            int r0 = (int) r8     // Catch:{ Exception -> 0x0168 }
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r7.inlineResult     // Catch:{ Exception -> 0x0168 }
            java.lang.String r8 = r8.url     // Catch:{ Exception -> 0x0168 }
            java.lang.String r2 = r8.replace(r2, r5)     // Catch:{ Exception -> 0x0168 }
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0168 }
            int r0 = java.lang.Math.min(r0, r6)     // Catch:{ Exception -> 0x0168 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x0168 }
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0168 }
            java.lang.CharSequence r9 = android.text.TextUtils.ellipsize(r2, r5, r0, r8)     // Catch:{ Exception -> 0x0168 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0168 }
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0168 }
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0168 }
            r13 = 1065353216(0x3var_, float:1.0)
            r2 = 0
            r5 = 0
            r8 = r0
            r11 = r6
            r6 = 1
            r14 = r2
            r2 = r15
            r15 = r5
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x0166 }
            r7.linkLayout = r0     // Catch:{ Exception -> 0x0166 }
            goto L_0x0171
        L_0x0166:
            r0 = move-exception
            goto L_0x016b
        L_0x0168:
            r0 = move-exception
            r2 = r15
            r6 = 1
        L_0x016b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0171
        L_0x016f:
            r2 = r15
            r6 = 1
        L_0x0171:
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            r5 = 3
            r8 = 5
            r9 = 80
            if (r0 == 0) goto L_0x01be
            boolean r0 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r0)
            r2 = 90
            if (r0 == 0) goto L_0x018c
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)
            r7.currentPhotoObject = r0
            goto L_0x01d8
        L_0x018c:
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            boolean r0 = org.telegram.messenger.MessageObject.isStickerDocument(r0)
            if (r0 != 0) goto L_0x01ae
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            boolean r0 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r0, r6)
            if (r0 == 0) goto L_0x019d
            goto L_0x01ae
        L_0x019d:
            int r0 = r7.documentAttachType
            if (r0 == r8) goto L_0x01d8
            if (r0 == r5) goto L_0x01d8
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)
            r7.currentPhotoObject = r0
            goto L_0x01d8
        L_0x01ae:
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)
            r7.currentPhotoObject = r0
            java.lang.String r0 = "webp"
            r26 = r0
            r0 = 0
            goto L_0x01db
        L_0x01be:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            if (r0 == 0) goto L_0x01d8
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x01d8
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r0, r6)
            r7.currentPhotoObject = r0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r9)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            if (r0 != r2) goto L_0x01d9
        L_0x01d8:
            r0 = 0
        L_0x01d9:
            r26 = 0
        L_0x01db:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            r10 = 2
            if (r2 == 0) goto L_0x0290
            org.telegram.tgnet.TLRPC$WebDocument r11 = r2.content
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r11 == 0) goto L_0x0218
            java.lang.String r2 = r2.type
            if (r2 == 0) goto L_0x0218
            java.lang.String r11 = "gif"
            boolean r2 = r2.startsWith(r11)
            if (r2 == 0) goto L_0x01fb
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
            r7.documentAttachType = r10
            goto L_0x0219
        L_0x01fb:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            java.lang.String r2 = r2.type
            java.lang.String r11 = "photo"
            boolean r2 = r2.equals(r11)
            if (r2 == 0) goto L_0x0218
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r11 = r2.thumb
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r12 == 0) goto L_0x0213
            r2 = r11
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
            goto L_0x0219
        L_0x0213:
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
            goto L_0x0219
        L_0x0218:
            r2 = 0
        L_0x0219:
            if (r2 != 0) goto L_0x0226
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r11 = r11.thumb
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r12 == 0) goto L_0x0226
            r2 = r11
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
        L_0x0226:
            if (r2 != 0) goto L_0x0283
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r7.currentPhotoObject
            if (r11 != 0) goto L_0x0283
            if (r0 != 0) goto L_0x0283
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r11 = r11.send_message
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue
            if (r12 != 0) goto L_0x023a
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo
            if (r11 == 0) goto L_0x0283
        L_0x023a:
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r11 = r11.send_message
            org.telegram.tgnet.TLRPC$GeoPoint r11 = r11.geo
            double r12 = r11.lat
            double r14 = r11._long
            int r11 = r7.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r11 = r11.mapProvider
            if (r11 != r10) goto L_0x026c
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r11 = r11.send_message
            org.telegram.tgnet.TLRPC$GeoPoint r11 = r11.geo
            r12 = 15
            float r13 = org.telegram.messenger.AndroidUtilities.density
            double r13 = (double) r13
            double r13 = java.lang.Math.ceil(r13)
            int r13 = (int) r13
            int r13 = java.lang.Math.min(r10, r13)
            r14 = 72
            org.telegram.messenger.WebFile r11 = org.telegram.messenger.WebFile.createWithGeoPoint(r11, r14, r14, r12, r13)
            r18 = r11
            r11 = 0
            goto L_0x0286
        L_0x026c:
            int r11 = r7.currentAccount
            r32 = 72
            r33 = 72
            r34 = 1
            r35 = 15
            r36 = -1
            r27 = r11
            r28 = r12
            r30 = r14
            java.lang.String r11 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r27, r28, r30, r32, r33, r34, r35, r36)
            goto L_0x0284
        L_0x0283:
            r11 = 0
        L_0x0284:
            r18 = 0
        L_0x0286:
            if (r2 == 0) goto L_0x028d
            org.telegram.messenger.WebFile r2 = org.telegram.messenger.WebFile.createWithWebDocument(r2)
            goto L_0x0292
        L_0x028d:
            r2 = r18
            goto L_0x0292
        L_0x0290:
            r2 = 0
            r11 = 0
        L_0x0292:
            org.telegram.tgnet.TLRPC$Document r12 = r7.documentAttach
            if (r12 == 0) goto L_0x02bc
            r12 = 0
        L_0x0297:
            org.telegram.tgnet.TLRPC$Document r13 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r13.attributes
            int r13 = r13.size()
            if (r12 >= r13) goto L_0x02bc
            org.telegram.tgnet.TLRPC$Document r13 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r13.attributes
            java.lang.Object r13 = r13.get(r12)
            org.telegram.tgnet.TLRPC$DocumentAttribute r13 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r13
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            if (r14 != 0) goto L_0x02b7
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            if (r14 == 0) goto L_0x02b4
            goto L_0x02b7
        L_0x02b4:
            int r12 = r12 + 1
            goto L_0x0297
        L_0x02b7:
            int r12 = r13.w
            int r13 = r13.h
            goto L_0x02be
        L_0x02bc:
            r12 = 0
            r13 = 0
        L_0x02be:
            if (r12 == 0) goto L_0x02c2
            if (r13 != 0) goto L_0x02e3
        L_0x02c2:
            org.telegram.tgnet.TLRPC$PhotoSize r14 = r7.currentPhotoObject
            if (r14 == 0) goto L_0x02d2
            if (r0 == 0) goto L_0x02cb
            r12 = -1
            r0.size = r12
        L_0x02cb:
            org.telegram.tgnet.TLRPC$PhotoSize r12 = r7.currentPhotoObject
            int r13 = r12.w
            int r12 = r12.h
            goto L_0x02de
        L_0x02d2:
            org.telegram.tgnet.TLRPC$BotInlineResult r14 = r7.inlineResult
            if (r14 == 0) goto L_0x02e3
            int[] r12 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r14)
            r13 = r12[r1]
            r12 = r12[r6]
        L_0x02de:
            r37 = r13
            r13 = r12
            r12 = r37
        L_0x02e3:
            r14 = 1117782016(0x42a00000, float:80.0)
            if (r12 == 0) goto L_0x02e9
            if (r13 != 0) goto L_0x02ee
        L_0x02e9:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r13 = r12
        L_0x02ee:
            org.telegram.tgnet.TLRPC$Document r15 = r7.documentAttach
            if (r15 != 0) goto L_0x02fa
            org.telegram.tgnet.TLRPC$PhotoSize r15 = r7.currentPhotoObject
            if (r15 != 0) goto L_0x02fa
            if (r2 != 0) goto L_0x02fa
            if (r11 == 0) goto L_0x049e
        L_0x02fa:
            boolean r15 = r7.mediaWebpage
            if (r15 == 0) goto L_0x0359
            float r12 = (float) r12
            float r13 = (float) r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            float r13 = r13 / r14
            float r12 = r12 / r13
            int r12 = (int) r12
            int r13 = r7.documentAttachType
            if (r13 != r10) goto L_0x032a
            java.util.Locale r13 = java.util.Locale.US
            java.lang.Object[] r14 = new java.lang.Object[r10]
            float r12 = (float) r12
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r12 = r12 / r15
            int r12 = (int) r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r14[r1] = r12
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r14[r6] = r9
            java.lang.String r9 = "%d_%d_b"
            java.lang.String r9 = java.lang.String.format(r13, r9, r14)
            r31 = r9
            goto L_0x0360
        L_0x032a:
            java.util.Locale r13 = java.util.Locale.US
            java.lang.Object[] r14 = new java.lang.Object[r10]
            float r12 = (float) r12
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r12 = r12 / r15
            int r12 = (int) r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r14[r1] = r12
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r14[r6] = r9
            java.lang.String r9 = "%d_%d"
            java.lang.String r9 = java.lang.String.format(r13, r9, r14)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r9)
            java.lang.String r13 = "_b"
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r31 = r12
            goto L_0x0360
        L_0x0359:
            java.lang.String r9 = "52_52_b"
            java.lang.String r12 = "52_52"
            r31 = r9
            r9 = r12
        L_0x0360:
            org.telegram.messenger.ImageReceiver r12 = r7.linkImageView
            int r13 = r7.documentAttachType
            r14 = 6
            if (r13 != r14) goto L_0x0369
            r14 = 1
            goto L_0x036a
        L_0x0369:
            r14 = 0
        L_0x036a:
            r12.setAspectFit(r14)
            int r12 = r7.documentAttachType
            if (r12 != r10) goto L_0x03de
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            if (r0 == 0) goto L_0x039a
            org.telegram.messenger.ImageReceiver r2 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForDocument(r0)
            r22 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r11 = r7.documentAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForDocument(r0, r11)
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            int r0 = r0.size
            java.lang.Object r11 = r7.parentObject
            r28 = 0
            r20 = r2
            r24 = r9
            r25 = r0
            r27 = r11
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x049c
        L_0x039a:
            if (r2 == 0) goto L_0x03bd
            org.telegram.messenger.ImageReceiver r0 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForWebFile(r2)
            r22 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r11 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r11)
            r25 = -1
            java.lang.Object r2 = r7.parentObject
            r28 = 1
            r20 = r0
            r24 = r9
            r27 = r2
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x049c
        L_0x03bd:
            org.telegram.messenger.ImageReceiver r0 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForPath(r11)
            r22 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r11 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r11)
            r25 = -1
            java.lang.Object r2 = r7.parentObject
            r28 = 1
            r20 = r0
            r24 = r9
            r27 = r2
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x049c
        L_0x03de:
            org.telegram.tgnet.TLRPC$PhotoSize r12 = r7.currentPhotoObject
            if (r12 == 0) goto L_0x045f
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            boolean r2 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r2)
            if (r2 == 0) goto L_0x0411
            org.telegram.messenger.ImageReceiver r0 = r7.linkImageView
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            org.telegram.messenger.ImageLocation r28 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r9 = r7.documentAttach
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForDocument(r2, r9)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            int r2 = r2.size
            r33 = 0
            java.lang.Object r9 = r7.parentObject
            r35 = 0
            java.lang.String r29 = "80_80"
            r27 = r0
            r32 = r2
            r34 = r9
            r27.setImage(r28, r29, r30, r31, r32, r33, r34, r35)
            goto L_0x049c
        L_0x0411:
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            if (r2 == 0) goto L_0x0439
            org.telegram.messenger.ImageReceiver r11 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r12 = r7.currentPhotoObject
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForDocument(r12, r2)
            org.telegram.tgnet.TLRPC$Photo r2 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto(r0, r2)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r7.currentPhotoObject
            int r0 = r0.size
            java.lang.Object r2 = r7.parentObject
            r28 = 0
            r20 = r11
            r22 = r9
            r24 = r31
            r25 = r0
            r27 = r2
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x049c
        L_0x0439:
            org.telegram.messenger.ImageReceiver r2 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r12 = r7.photoAttach
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForPhoto(r11, r12)
            org.telegram.tgnet.TLRPC$Photo r11 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto(r0, r11)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r7.currentPhotoObject
            int r0 = r0.size
            java.lang.Object r11 = r7.parentObject
            r28 = 0
            r20 = r2
            r22 = r9
            r24 = r31
            r25 = r0
            r27 = r11
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x049c
        L_0x045f:
            if (r2 == 0) goto L_0x047f
            org.telegram.messenger.ImageReceiver r11 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForWebFile(r2)
            org.telegram.tgnet.TLRPC$Photo r2 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto(r0, r2)
            r25 = -1
            java.lang.Object r0 = r7.parentObject
            r28 = 1
            r20 = r11
            r22 = r9
            r24 = r31
            r27 = r0
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x049c
        L_0x047f:
            org.telegram.messenger.ImageReceiver r2 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForPath(r11)
            org.telegram.tgnet.TLRPC$Photo r11 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto(r0, r11)
            r25 = -1
            java.lang.Object r0 = r7.parentObject
            r28 = 1
            r20 = r2
            r22 = r9
            r24 = r31
            r27 = r0
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
        L_0x049c:
            r7.drawLinkImageView = r6
        L_0x049e:
            boolean r0 = r7.mediaWebpage
            r2 = 1094713344(0x41400000, float:12.0)
            r9 = 1103101952(0x41CLASSNAME, float:24.0)
            if (r0 == 0) goto L_0x04e0
            int r0 = android.view.View.MeasureSpec.getSize(r40)
            if (r0 != 0) goto L_0x04b0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x04b0:
            r7.setMeasuredDimension(r4, r0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r3 = r4 - r3
            int r3 = r3 / r10
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r5 = r0 - r5
            int r5 = r5 / r10
            org.telegram.ui.Components.RadialProgress2 r6 = r7.radialProgress
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 + r3
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r5
            r6.setProgressRect(r3, r5, r8, r9)
            org.telegram.ui.Components.RadialProgress2 r3 = r7.radialProgress
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3.setCircleRadius(r2)
            org.telegram.messenger.ImageReceiver r2 = r7.linkImageView
            r2.setImageCoords(r1, r1, r4, r0)
            goto L_0x05a6
        L_0x04e0:
            android.text.StaticLayout r0 = r7.titleLayout
            if (r0 == 0) goto L_0x04f6
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x04f6
            android.text.StaticLayout r0 = r7.titleLayout
            int r3 = r0.getLineCount()
            int r3 = r3 - r6
            int r0 = r0.getLineBottom(r3)
            int r1 = r1 + r0
        L_0x04f6:
            android.text.StaticLayout r0 = r7.descriptionLayout
            if (r0 == 0) goto L_0x050c
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x050c
            android.text.StaticLayout r0 = r7.descriptionLayout
            int r3 = r0.getLineCount()
            int r3 = r3 - r6
            int r0 = r0.getLineBottom(r3)
            int r1 = r1 + r0
        L_0x050c:
            android.text.StaticLayout r0 = r7.linkLayout
            if (r0 == 0) goto L_0x0522
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0522
            android.text.StaticLayout r0 = r7.linkLayout
            int r3 = r0.getLineCount()
            int r3 = r3 - r6
            int r0 = r0.getLineBottom(r3)
            int r1 = r1 + r0
        L_0x0522:
            r0 = 1112539136(0x42500000, float:52.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r1 = java.lang.Math.max(r3, r1)
            int r3 = android.view.View.MeasureSpec.getSize(r39)
            r4 = 1116209152(0x42880000, float:68.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r1 = r1 + r6
            int r1 = java.lang.Math.max(r4, r1)
            boolean r4 = r7.needDivider
            int r1 = r1 + r4
            r7.setMeasuredDimension(r3, r1)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x055c
            int r1 = android.view.View.MeasureSpec.getSize(r39)
            r3 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r4
            int r1 = r1 - r0
            goto L_0x0562
        L_0x055c:
            r3 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x0562:
            org.telegram.ui.Components.LetterDrawable r4 = r7.letterDrawable
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r10 = r1 + r0
            r11 = 1114636288(0x42700000, float:60.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r4.setBounds(r1, r6, r10, r11)
            org.telegram.messenger.ImageReceiver r4 = r7.linkImageView
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.setImageCoords(r1, r3, r0, r0)
            int r0 = r7.documentAttachType
            if (r0 == r5) goto L_0x0582
            if (r0 != r8) goto L_0x05a6
        L_0x0582:
            org.telegram.ui.Components.RadialProgress2 r0 = r7.radialProgress
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r0.setCircleRadius(r3)
            org.telegram.ui.Components.RadialProgress2 r0 = r7.radialProgress
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = r3 + r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = 1111490560(0x42400000, float:48.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 + r4
            r4 = 1113587712(0x42600000, float:56.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setProgressRect(r3, r2, r1, r4)
        L_0x05a6:
            org.telegram.ui.Components.CheckBox2 r2 = r7.checkBox
            if (r2 == 0) goto L_0x05b5
            r4 = 0
            r6 = 0
            r1 = r38
            r3 = r39
            r5 = r40
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
        L_0x05b5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onMeasure(int, int):void");
    }

    private void setAttachType() {
        String str;
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        TLRPC$Document tLRPC$Document = this.documentAttach;
        if (tLRPC$Document == null) {
            TLRPC$BotInlineResult tLRPC$BotInlineResult = this.inlineResult;
            if (tLRPC$BotInlineResult != null) {
                if (tLRPC$BotInlineResult.photo != null) {
                    this.documentAttachType = 7;
                } else if (tLRPC$BotInlineResult.type.equals("audio")) {
                    this.documentAttachType = 5;
                } else if (this.inlineResult.type.equals("voice")) {
                    this.documentAttachType = 3;
                }
            }
        } else if (MessageObject.isGifDocument(tLRPC$Document)) {
            this.documentAttachType = 2;
        } else if (MessageObject.isStickerDocument(this.documentAttach) || MessageObject.isAnimatedStickerDocument(this.documentAttach, true)) {
            this.documentAttachType = 6;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
        } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
            this.documentAttachType = 3;
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
            tLRPC$TL_message.out = true;
            tLRPC$TL_message.id = -Utilities.random.nextInt();
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$TL_message.to_id = tLRPC$TL_peerUser;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            tLRPC$TL_message.from_id = clientUserId;
            tLRPC$TL_peerUser.user_id = clientUserId;
            tLRPC$TL_message.date = (int) (System.currentTimeMillis() / 1000);
            String str2 = "";
            tLRPC$TL_message.message = str2;
            TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
            tLRPC$TL_message.media = tLRPC$TL_messageMediaDocument;
            tLRPC$TL_messageMediaDocument.flags |= 3;
            tLRPC$TL_messageMediaDocument.document = new TLRPC$TL_document();
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$TL_message.media;
            tLRPC$MessageMedia.document.file_reference = new byte[0];
            tLRPC$TL_message.flags |= 768;
            TLRPC$Document tLRPC$Document2 = this.documentAttach;
            if (tLRPC$Document2 != null) {
                tLRPC$MessageMedia.document = tLRPC$Document2;
                tLRPC$TL_message.attachPath = str2;
            } else {
                String str3 = "mp3";
                String httpUrlExtension = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? str3 : "ogg");
                TLRPC$Document tLRPC$Document3 = tLRPC$TL_message.media.document;
                tLRPC$Document3.id = 0;
                tLRPC$Document3.access_hash = 0;
                tLRPC$Document3.date = tLRPC$TL_message.date;
                tLRPC$Document3.mime_type = "audio/" + httpUrlExtension;
                TLRPC$Document tLRPC$Document4 = tLRPC$TL_message.media.document;
                tLRPC$Document4.size = 0;
                tLRPC$Document4.dc_id = 0;
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                String str4 = this.inlineResult.title;
                if (str4 == null) {
                    str4 = str2;
                }
                tLRPC$TL_documentAttributeAudio.title = str4;
                String str5 = this.inlineResult.description;
                if (str5 != null) {
                    str2 = str5;
                }
                tLRPC$TL_documentAttributeAudio.performer = str2;
                tLRPC$TL_documentAttributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    tLRPC$TL_documentAttributeAudio.voice = true;
                }
                tLRPC$TL_message.media.document.attributes.add(tLRPC$TL_documentAttributeAudio);
                TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
                StringBuilder sb = new StringBuilder();
                sb.append(Utilities.MD5(this.inlineResult.content.url));
                sb.append(".");
                String str6 = this.inlineResult.content.url;
                if (this.documentAttachType == 5) {
                    str = str3;
                } else {
                    str = "ogg";
                }
                sb.append(ImageLoader.getHttpUrlExtension(str6, str));
                tLRPC$TL_documentAttributeFilename.file_name = sb.toString();
                tLRPC$TL_message.media.document.attributes.add(tLRPC$TL_documentAttributeFilename);
                File directory = FileLoader.getDirectory(4);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(Utilities.MD5(this.inlineResult.content.url));
                sb2.append(".");
                String str7 = this.inlineResult.content.url;
                if (this.documentAttachType != 5) {
                    str3 = "ogg";
                }
                sb2.append(ImageLoader.getHttpUrlExtension(str7, str3));
                tLRPC$TL_message.attachPath = new File(directory, sb2.toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, tLRPC$TL_message, false);
        }
    }

    public void setLink(TLRPC$BotInlineResult tLRPC$BotInlineResult, boolean z, boolean z2, boolean z3) {
        this.needDivider = z2;
        this.needShadow = z3;
        this.inlineResult = tLRPC$BotInlineResult;
        this.parentObject = tLRPC$BotInlineResult;
        if (tLRPC$BotInlineResult != null) {
            this.documentAttach = tLRPC$BotInlineResult.document;
            this.photoAttach = tLRPC$BotInlineResult.photo;
        } else {
            this.documentAttach = null;
            this.photoAttach = null;
        }
        this.mediaWebpage = z;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public void setGif(TLRPC$Document tLRPC$Document, boolean z) {
        setGif(tLRPC$Document, "gif" + tLRPC$Document, 0, z);
    }

    public void setGif(TLRPC$Document tLRPC$Document, Object obj, int i, boolean z) {
        this.needDivider = z;
        this.needShadow = false;
        this.currentDate = i;
        this.inlineResult = null;
        this.parentObject = obj;
        this.documentAttach = tLRPC$Document;
        this.photoAttach = null;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    public boolean isGif() {
        return this.documentAttachType == 2 && this.canPreviewGif;
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public int getDate() {
        return this.currentDate;
    }

    public TLRPC$Document getDocument() {
        return this.documentAttach;
    }

    public TLRPC$BotInlineResult getBotInlineResult() {
        return this.inlineResult;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setCanPreviewGif(boolean z) {
        this.canPreviewGif = z;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
        this.radialProgress.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false, false);
        }
        this.radialProgress.onAttachedToWindow();
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:57:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            boolean r0 = r8.mediaWebpage
            if (r0 != 0) goto L_0x00de
            org.telegram.ui.Cells.ContextLinkCell$ContextLinkCellDelegate r0 = r8.delegate
            if (r0 == 0) goto L_0x00de
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r8.inlineResult
            if (r0 != 0) goto L_0x000e
            goto L_0x00de
        L_0x000e:
            float r0 = r9.getX()
            int r0 = (int) r0
            float r1 = r9.getY()
            int r1 = (int) r1
            r2 = 1111490560(0x42400000, float:48.0)
            org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r8.documentAttachType
            r3 = 2
            r4 = 3
            r5 = 1
            r6 = 0
            if (r2 == r4) goto L_0x0083
            r7 = 5
            if (r2 != r7) goto L_0x0029
            goto L_0x0083
        L_0x0029:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r8.inlineResult
            if (r2 == 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            if (r2 == 0) goto L_0x0081
            java.lang.String r2 = r2.url
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0081
            int r2 = r9.getAction()
            if (r2 != 0) goto L_0x004f
            org.telegram.ui.Components.LetterDrawable r2 = r8.letterDrawable
            android.graphics.Rect r2 = r2.getBounds()
            boolean r0 = r2.contains(r0, r1)
            if (r0 == 0) goto L_0x0081
            r8.buttonPressed = r5
            goto L_0x00d7
        L_0x004f:
            boolean r2 = r8.buttonPressed
            if (r2 == 0) goto L_0x0081
            int r2 = r9.getAction()
            if (r2 != r5) goto L_0x0064
            r8.buttonPressed = r6
            r8.playSoundEffect(r6)
            org.telegram.ui.Cells.ContextLinkCell$ContextLinkCellDelegate r0 = r8.delegate
            r0.didPressedImage(r8)
            goto L_0x0081
        L_0x0064:
            int r2 = r9.getAction()
            if (r2 != r4) goto L_0x006d
            r8.buttonPressed = r6
            goto L_0x0081
        L_0x006d:
            int r2 = r9.getAction()
            if (r2 != r3) goto L_0x0081
            org.telegram.ui.Components.LetterDrawable r2 = r8.letterDrawable
            android.graphics.Rect r2 = r2.getBounds()
            boolean r0 = r2.contains(r0, r1)
            if (r0 != 0) goto L_0x0081
            r8.buttonPressed = r6
        L_0x0081:
            r5 = 0
            goto L_0x00d7
        L_0x0083:
            org.telegram.ui.Components.LetterDrawable r2 = r8.letterDrawable
            android.graphics.Rect r2 = r2.getBounds()
            boolean r0 = r2.contains(r0, r1)
            int r1 = r9.getAction()
            if (r1 != 0) goto L_0x00a0
            if (r0 == 0) goto L_0x0081
            r8.buttonPressed = r5
            org.telegram.ui.Components.RadialProgress2 r0 = r8.radialProgress
            r0.setPressed(r5, r6)
            r8.invalidate()
            goto L_0x00d7
        L_0x00a0:
            boolean r1 = r8.buttonPressed
            if (r1 == 0) goto L_0x0081
            int r1 = r9.getAction()
            if (r1 != r5) goto L_0x00b6
            r8.buttonPressed = r6
            r8.playSoundEffect(r6)
            r8.didPressedButton()
            r8.invalidate()
            goto L_0x00cf
        L_0x00b6:
            int r1 = r9.getAction()
            if (r1 != r4) goto L_0x00c2
            r8.buttonPressed = r6
            r8.invalidate()
            goto L_0x00cf
        L_0x00c2:
            int r1 = r9.getAction()
            if (r1 != r3) goto L_0x00cf
            if (r0 != 0) goto L_0x00cf
            r8.buttonPressed = r6
            r8.invalidate()
        L_0x00cf:
            org.telegram.ui.Components.RadialProgress2 r0 = r8.radialProgress
            boolean r1 = r8.buttonPressed
            r0.setPressed(r1, r6)
            goto L_0x0081
        L_0x00d7:
            if (r5 != 0) goto L_0x00dd
            boolean r5 = super.onTouchEvent(r9)
        L_0x00dd:
            return r5
        L_0x00de:
            boolean r9 = super.onTouchEvent(r9)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void didPressedButton() {
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            int i2 = this.buttonState;
            if (i2 == 0) {
                if (MediaController.getInstance().playMessage(this.currentMessageObject)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i2 == 1) {
                if (MediaController.getInstance().lambda$startAudioAgain$7$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i2 == 2) {
                this.radialProgress.setProgress(0.0f, false);
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.inlineResult, 1, 0);
                } else if (this.inlineResult.content instanceof TLRPC$TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).loadFile(WebFile.createWithWebDocument(this.inlineResult.content), 1, 1);
                }
                this.buttonState = 4;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            } else if (i2 == 4) {
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else if (this.inlineResult.content instanceof TLRPC$TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(WebFile.createWithWebDocument(this.inlineResult.content));
                }
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && (checkBox2.isChecked() || !this.linkImageView.hasBitmapImage() || this.linkImageView.getCurrentAlpha() != 1.0f || PhotoViewer.isShowingImage((MessageObject) this.parentObject))) {
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
        }
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        if (!this.mediaWebpage) {
            if (!this.drawLinkImageView || PhotoViewer.isShowingImage(this.inlineResult)) {
                this.letterDrawable.setAlpha(255);
            } else {
                this.letterDrawable.setAlpha((int) ((1.0f - this.linkImageView.getCurrentAlpha()) * 255.0f));
            }
            int i2 = this.documentAttachType;
            if (i2 == 3 || i2 == 5) {
                this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress"));
                this.radialProgress.draw(canvas);
            } else {
                TLRPC$BotInlineResult tLRPC$BotInlineResult = this.inlineResult;
                if (tLRPC$BotInlineResult == null || !tLRPC$BotInlineResult.type.equals("file")) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult2 = this.inlineResult;
                    if (tLRPC$BotInlineResult2 == null || (!tLRPC$BotInlineResult2.type.equals("audio") && !this.inlineResult.type.equals("voice"))) {
                        TLRPC$BotInlineResult tLRPC$BotInlineResult3 = this.inlineResult;
                        if (tLRPC$BotInlineResult3 == null || (!tLRPC$BotInlineResult3.type.equals("venue") && !this.inlineResult.type.equals("geo"))) {
                            this.letterDrawable.draw(canvas);
                        } else {
                            int intrinsicWidth = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                            int intrinsicHeight = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                            int imageX = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / 2);
                            int imageY = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight) / 2);
                            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                            Theme.chat_inlineResultLocation.setBounds(imageX, imageY, intrinsicWidth + imageX, intrinsicHeight + imageY);
                            Theme.chat_inlineResultLocation.draw(canvas);
                        }
                    } else {
                        int intrinsicWidth2 = Theme.chat_inlineResultAudio.getIntrinsicWidth();
                        int intrinsicHeight2 = Theme.chat_inlineResultAudio.getIntrinsicHeight();
                        int imageX2 = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth2) / 2);
                        int imageY2 = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight2) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultAudio.setBounds(imageX2, imageY2, intrinsicWidth2 + imageX2, intrinsicHeight2 + imageY2);
                        Theme.chat_inlineResultAudio.draw(canvas);
                    }
                } else {
                    int intrinsicWidth3 = Theme.chat_inlineResultFile.getIntrinsicWidth();
                    int intrinsicHeight3 = Theme.chat_inlineResultFile.getIntrinsicHeight();
                    int imageX3 = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth3) / 2);
                    int imageY3 = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight3) / 2);
                    canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                    Theme.chat_inlineResultFile.setBounds(imageX3, imageY3, intrinsicWidth3 + imageX3, intrinsicHeight3 + imageY3);
                    Theme.chat_inlineResultFile.draw(canvas);
                }
            }
        } else {
            TLRPC$BotInlineResult tLRPC$BotInlineResult4 = this.inlineResult;
            if (tLRPC$BotInlineResult4 != null) {
                TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult4.send_message;
                if ((tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaGeo) || (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaVenue)) {
                    int intrinsicWidth4 = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                    int intrinsicHeight4 = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                    int imageX4 = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - intrinsicWidth4) / 2);
                    int imageY4 = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - intrinsicHeight4) / 2);
                    canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
                    Theme.chat_inlineResultLocation.setBounds(imageX4, imageY4, intrinsicWidth4 + imageX4, intrinsicHeight4 + imageY4);
                    Theme.chat_inlineResultLocation.draw(canvas);
                }
            }
        }
        if (this.drawLinkImageView) {
            TLRPC$BotInlineResult tLRPC$BotInlineResult5 = this.inlineResult;
            if (tLRPC$BotInlineResult5 != null) {
                this.linkImageView.setVisible(!PhotoViewer.isShowingImage(tLRPC$BotInlineResult5), false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || (!this.scaled && this.scale != 1.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                this.lastUpdateTime = currentTimeMillis;
                if (this.scaled) {
                    float f2 = this.scale;
                    if (f2 != 0.8f) {
                        float f3 = f2 - (((float) j) / 400.0f);
                        this.scale = f3;
                        if (f3 < 0.8f) {
                            this.scale = 0.8f;
                        }
                        invalidate();
                    }
                }
                float f4 = this.scale + (((float) j) / 400.0f);
                this.scale = f4;
                if (f4 > 1.0f) {
                    this.scale = 1.0f;
                }
                invalidate();
            }
            float f5 = this.scale;
            float f6 = this.imageScale;
            canvas.scale(f5 * f6, f5 * f6, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage && ((i = this.documentAttachType) == 7 || i == 2)) {
            this.radialProgress.draw(canvas);
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        if (this.needShadow) {
            Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0f));
            Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(canvas);
        }
    }

    private int getIconForCurrentState() {
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
            int i2 = this.buttonState;
            if (i2 == 1) {
                return 1;
            }
            if (i2 == 2) {
                return 2;
            }
            if (i2 == 4) {
                return 3;
            }
            return 0;
        }
        this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
        if (this.buttonState == 1) {
            return 10;
        }
        return 4;
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x011e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x011f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateButtonState(boolean r10, boolean r11) {
        /*
            r9 = this;
            int r0 = r9.documentAttachType
            r1 = 0
            r2 = 1
            java.lang.String r3 = "."
            r4 = 3
            r5 = 4
            r6 = 5
            if (r0 == r6) goto L_0x00c7
            if (r0 != r4) goto L_0x000f
            goto L_0x00c7
        L_0x000f:
            boolean r0 = r9.mediaWebpage
            if (r0 == 0) goto L_0x0117
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r9.inlineResult
            if (r0 == 0) goto L_0x00b8
            org.telegram.tgnet.TLRPC$Document r7 = r0.document
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r8 == 0) goto L_0x002b
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r7)
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r9.inlineResult
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x0118
        L_0x002b:
            org.telegram.tgnet.TLRPC$Photo r7 = r0.photo
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r8 == 0) goto L_0x0049
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r7.sizes
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r1, r2)
            r9.currentPhotoObject = r0
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r9.currentPhotoObject
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x0118
        L_0x0049:
            org.telegram.tgnet.TLRPC$WebDocument r7 = r0.content
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            java.lang.String r8 = "jpg"
            if (r7 == 0) goto L_0x0082
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r9.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r1 = r1.content
            java.lang.String r1 = r1.url
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r1)
            r0.append(r1)
            r0.append(r3)
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r9.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r1 = r1.content
            java.lang.String r1 = r1.url
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r8)
            r0.append(r1)
            java.lang.String r1 = r0.toString()
            java.io.File r0 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r5)
            r0.<init>(r3, r1)
            goto L_0x0118
        L_0x0082:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.thumb
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r0 == 0) goto L_0x0117
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r9.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r1 = r1.thumb
            java.lang.String r1 = r1.url
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r1)
            r0.append(r1)
            r0.append(r3)
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r9.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r1 = r1.thumb
            java.lang.String r1 = r1.url
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r8)
            r0.append(r1)
            java.lang.String r1 = r0.toString()
            java.io.File r0 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r5)
            r0.<init>(r3, r1)
            goto L_0x0118
        L_0x00b8:
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            if (r0 == 0) goto L_0x0117
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x0118
        L_0x00c7:
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            if (r0 == 0) goto L_0x00d6
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x0118
        L_0x00d6:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r9.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r0 == 0) goto L_0x0117
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r9.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r1 = r1.content
            java.lang.String r1 = r1.url
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r1)
            r0.append(r1)
            r0.append(r3)
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r9.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r1 = r1.content
            java.lang.String r1 = r1.url
            int r3 = r9.documentAttachType
            if (r3 != r6) goto L_0x0100
            java.lang.String r3 = "mp3"
            goto L_0x0102
        L_0x0100:
            java.lang.String r3 = "ogg"
        L_0x0102:
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r3)
            r0.append(r1)
            java.lang.String r1 = r0.toString()
            java.io.File r0 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r5)
            r0.<init>(r3, r1)
            goto L_0x0118
        L_0x0117:
            r0 = r1
        L_0x0118:
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 == 0) goto L_0x011f
            return
        L_0x011f:
            boolean r0 = r0.exists()
            r3 = 0
            if (r0 != 0) goto L_0x01a4
            int r0 = r9.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.addLoadingFileObserver(r1, r9)
            int r0 = r9.documentAttachType
            r7 = 0
            if (r0 == r6) goto L_0x0156
            if (r0 != r4) goto L_0x0137
            goto L_0x0156
        L_0x0137:
            r9.buttonState = r2
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r0 = r0.getFileProgress(r1)
            if (r0 == 0) goto L_0x0147
            float r7 = r0.floatValue()
        L_0x0147:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            r0.setProgress(r7, r3)
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            int r1 = r9.getIconForCurrentState()
            r0.setIcon(r1, r10, r11)
            goto L_0x01a0
        L_0x0156:
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            if (r0 == 0) goto L_0x0165
            int r0 = r9.currentAccount
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)
            boolean r0 = r0.isLoadingFile(r1)
            goto L_0x016d
        L_0x0165:
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            boolean r0 = r0.isLoadingHttpFile(r1)
        L_0x016d:
            if (r0 != 0) goto L_0x017c
            r0 = 2
            r9.buttonState = r0
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            int r1 = r9.getIconForCurrentState()
            r0.setIcon(r1, r10, r11)
            goto L_0x01a0
        L_0x017c:
            r9.buttonState = r5
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r0 = r0.getFileProgress(r1)
            if (r0 == 0) goto L_0x0192
            org.telegram.ui.Components.RadialProgress2 r1 = r9.radialProgress
            float r0 = r0.floatValue()
            r1.setProgress(r0, r11)
            goto L_0x0197
        L_0x0192:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            r0.setProgress(r7, r11)
        L_0x0197:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            int r1 = r9.getIconForCurrentState()
            r0.setIcon(r1, r10, r11)
        L_0x01a0:
            r9.invalidate()
            goto L_0x01e9
        L_0x01a4:
            int r0 = r9.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.removeLoadingFileObserver(r9)
            int r0 = r9.documentAttachType
            if (r0 == r6) goto L_0x01b8
            if (r0 != r4) goto L_0x01b4
            goto L_0x01b8
        L_0x01b4:
            r0 = -1
            r9.buttonState = r0
            goto L_0x01dd
        L_0x01b8:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r1 = r9.currentMessageObject
            boolean r0 = r0.isPlayingMessage(r1)
            if (r0 == 0) goto L_0x01d4
            if (r0 == 0) goto L_0x01d1
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            boolean r0 = r0.isMessagePaused()
            if (r0 == 0) goto L_0x01d1
            goto L_0x01d4
        L_0x01d1:
            r9.buttonState = r2
            goto L_0x01d6
        L_0x01d4:
            r9.buttonState = r3
        L_0x01d6:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            r1 = 1065353216(0x3var_, float:1.0)
            r0.setProgress(r1, r11)
        L_0x01dd:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            int r1 = r9.getIconForCurrentState()
            r0.setIcon(r1, r10, r11)
            r9.invalidate()
        L_0x01e9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.updateButtonState(boolean, boolean):void");
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public TLRPC$BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String str, long j, long j2) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, true);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        StringBuilder sb = new StringBuilder();
        switch (this.documentAttachType) {
            case 1:
                sb.append(LocaleController.getString("AttachDocument", NUM));
                break;
            case 2:
                sb.append(LocaleController.getString("AttachGif", NUM));
                break;
            case 3:
                sb.append(LocaleController.getString("AttachAudio", NUM));
                break;
            case 4:
                sb.append(LocaleController.getString("AttachVideo", NUM));
                break;
            case 5:
                sb.append(LocaleController.getString("AttachMusic", NUM));
                if (!(this.descriptionLayout == null || this.titleLayout == null)) {
                    sb.append(", ");
                    sb.append(LocaleController.formatString("AccDescrMusicInfo", NUM, this.descriptionLayout.getText(), this.titleLayout.getText()));
                    break;
                }
            case 6:
                sb.append(LocaleController.getString("AttachSticker", NUM));
                break;
            case 7:
                sb.append(LocaleController.getString("AttachPhoto", NUM));
                break;
            case 8:
                sb.append(LocaleController.getString("AttachLocation", NUM));
                break;
            default:
                StaticLayout staticLayout = this.titleLayout;
                if (staticLayout != null && !TextUtils.isEmpty(staticLayout.getText())) {
                    sb.append(this.titleLayout.getText());
                }
                StaticLayout staticLayout2 = this.descriptionLayout;
                if (staticLayout2 != null && !TextUtils.isEmpty(staticLayout2.getText())) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(this.descriptionLayout.getText());
                    break;
                }
        }
        accessibilityNodeInfo.setText(sb);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }
    }

    public void setChecked(final boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            if (checkBox2.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
            float f = 1.0f;
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                Property<ContextLinkCell, Float> property = this.IMAGE_SCALE;
                float[] fArr = new float[1];
                if (z) {
                    f = 0.81f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ContextLinkCell.this.animator != null && ContextLinkCell.this.animator.equals(animator)) {
                            AnimatorSet unused = ContextLinkCell.this.animator = null;
                            if (!z) {
                                ContextLinkCell.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (ContextLinkCell.this.animator != null && ContextLinkCell.this.animator.equals(animator)) {
                            AnimatorSet unused = ContextLinkCell.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            if (z) {
                f = 0.85f;
            }
            this.imageScale = f;
            invalidate();
        }
    }
}
