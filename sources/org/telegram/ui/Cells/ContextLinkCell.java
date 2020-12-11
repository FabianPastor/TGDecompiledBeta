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
import org.telegram.tgnet.TLRPC$Peer;
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
import org.telegram.tgnet.TLRPC$User;
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
    private TLRPC$User inlineBot;
    private TLRPC$BotInlineResult inlineResult;
    private boolean isForceGif;
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
    /* JADX WARNING: type inference failed for: r6v10 */
    /* JADX WARNING: type inference failed for: r6v11 */
    /* JADX WARNING: type inference failed for: r6v12 */
    /* JADX WARNING: type inference failed for: r6v13 */
    /* JADX WARNING: type inference failed for: r6v14 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x01d9, code lost:
        if (r0 == r7.currentPhotoObject) goto L_0x01db;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0284  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02a0  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02a5  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02a8  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02ae  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02de  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02e8  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0314  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x036f  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x037d  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x037f  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0387  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x05f9  */
    /* JADX WARNING: Removed duplicated region for block: B:227:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0078 A[SYNTHETIC, Splitter:B:22:0x0078] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00d6 A[SYNTHETIC, Splitter:B:29:0x00d6] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x012d A[SYNTHETIC, Splitter:B:43:0x012d] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01c1  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01e3  */
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
            if (r0 != 0) goto L_0x016d
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            if (r0 == 0) goto L_0x016d
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
            if (r0 == 0) goto L_0x0123
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x011a }
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()     // Catch:{ Exception -> 0x011a }
            r9 = 1095761920(0x41500000, float:13.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x011a }
            java.lang.CharSequence r8 = org.telegram.messenger.Emoji.replaceEmoji(r0, r8, r9, r1)     // Catch:{ Exception -> 0x011a }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x011a }
            r0 = 0
            r17 = 3
            r10 = r6
            r11 = r6
            r2 = 10
            r12 = r0
            r5 = 32
            r13 = r17
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0118 }
            r7.descriptionLayout = r0     // Catch:{ Exception -> 0x0118 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x0118 }
            if (r0 <= 0) goto L_0x0127
            int r0 = r7.descriptionY     // Catch:{ Exception -> 0x0118 }
            android.text.StaticLayout r8 = r7.descriptionLayout     // Catch:{ Exception -> 0x0118 }
            int r9 = r8.getLineCount()     // Catch:{ Exception -> 0x0118 }
            int r9 = r9 - r14
            int r8 = r8.getLineBottom(r9)     // Catch:{ Exception -> 0x0118 }
            int r0 = r0 + r8
            r8 = 1065353216(0x3var_, float:1.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ Exception -> 0x0118 }
            int r0 = r0 + r8
            r7.linkY = r0     // Catch:{ Exception -> 0x0118 }
            goto L_0x0127
        L_0x0118:
            r0 = move-exception
            goto L_0x011f
        L_0x011a:
            r0 = move-exception
            r2 = 10
            r5 = 32
        L_0x011f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0127
        L_0x0123:
            r2 = 10
            r5 = 32
        L_0x0127:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            java.lang.String r0 = r0.url
            if (r0 == 0) goto L_0x016d
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0166 }
            float r0 = r8.measureText(r0)     // Catch:{ Exception -> 0x0166 }
            double r8 = (double) r0     // Catch:{ Exception -> 0x0166 }
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x0166 }
            int r0 = (int) r8     // Catch:{ Exception -> 0x0166 }
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r7.inlineResult     // Catch:{ Exception -> 0x0166 }
            java.lang.String r8 = r8.url     // Catch:{ Exception -> 0x0166 }
            java.lang.String r2 = r8.replace(r2, r5)     // Catch:{ Exception -> 0x0166 }
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0166 }
            int r0 = java.lang.Math.min(r0, r6)     // Catch:{ Exception -> 0x0166 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x0166 }
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0166 }
            java.lang.CharSequence r9 = android.text.TextUtils.ellipsize(r2, r5, r0, r8)     // Catch:{ Exception -> 0x0166 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0166 }
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0166 }
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0166 }
            r13 = 1065353216(0x3var_, float:1.0)
            r2 = 0
            r5 = 0
            r8 = r0
            r11 = r6
            r6 = 1
            r14 = r2
            r2 = r15
            r15 = r5
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x0164 }
            r7.linkLayout = r0     // Catch:{ Exception -> 0x0164 }
            goto L_0x016f
        L_0x0164:
            r0 = move-exception
            goto L_0x0169
        L_0x0166:
            r0 = move-exception
            r2 = r15
            r6 = 1
        L_0x0169:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x016f
        L_0x016d:
            r2 = r15
            r6 = 1
        L_0x016f:
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            r5 = 3
            r8 = 5
            r9 = 80
            if (r0 == 0) goto L_0x01c1
            boolean r2 = r7.isForceGif
            r10 = 90
            if (r2 != 0) goto L_0x01b6
            boolean r0 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r0)
            if (r0 == 0) goto L_0x0184
            goto L_0x01b6
        L_0x0184:
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            boolean r0 = org.telegram.messenger.MessageObject.isStickerDocument(r0)
            if (r0 != 0) goto L_0x01a6
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            boolean r0 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r0, r6)
            if (r0 == 0) goto L_0x0195
            goto L_0x01a6
        L_0x0195:
            int r0 = r7.documentAttachType
            if (r0 == r8) goto L_0x01db
            if (r0 == r5) goto L_0x01db
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r10)
            r7.currentPhotoObject = r0
            goto L_0x01db
        L_0x01a6:
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r10)
            r7.currentPhotoObject = r0
            java.lang.String r0 = "webp"
            r26 = r0
            r0 = 0
            goto L_0x01de
        L_0x01b6:
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r10)
            r7.currentPhotoObject = r0
            goto L_0x01db
        L_0x01c1:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            if (r0 == 0) goto L_0x01db
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x01db
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r0, r6)
            r7.currentPhotoObject = r0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r9)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            if (r0 != r2) goto L_0x01dc
        L_0x01db:
            r0 = 0
        L_0x01dc:
            r26 = 0
        L_0x01de:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            r10 = 2
            if (r2 == 0) goto L_0x02a8
            org.telegram.tgnet.TLRPC$WebDocument r11 = r2.content
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r11 == 0) goto L_0x0234
            java.lang.String r2 = r2.type
            if (r2 == 0) goto L_0x0234
            java.lang.String r11 = "gif"
            boolean r2 = r2.startsWith(r11)
            if (r2 == 0) goto L_0x0217
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            boolean r11 = r2 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r11 == 0) goto L_0x020e
            java.lang.String r2 = r2.mime_type
            java.lang.String r11 = "video/mp4"
            boolean r2 = r11.equals(r2)
            if (r2 == 0) goto L_0x020e
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
            goto L_0x0214
        L_0x020e:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
        L_0x0214:
            r7.documentAttachType = r10
            goto L_0x0235
        L_0x0217:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            java.lang.String r2 = r2.type
            java.lang.String r11 = "photo"
            boolean r2 = r2.equals(r11)
            if (r2 == 0) goto L_0x0234
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r11 = r2.thumb
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r12 == 0) goto L_0x022f
            r2 = r11
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
            goto L_0x0235
        L_0x022f:
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
            goto L_0x0235
        L_0x0234:
            r2 = 0
        L_0x0235:
            if (r2 != 0) goto L_0x0242
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r11 = r11.thumb
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r12 == 0) goto L_0x0242
            r2 = r11
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC$TL_webDocument) r2
        L_0x0242:
            if (r2 != 0) goto L_0x029b
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r7.currentPhotoObject
            if (r11 != 0) goto L_0x029b
            if (r0 != 0) goto L_0x029b
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r11 = r11.send_message
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue
            if (r12 != 0) goto L_0x0256
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo
            if (r12 == 0) goto L_0x029b
        L_0x0256:
            org.telegram.tgnet.TLRPC$GeoPoint r11 = r11.geo
            double r12 = r11.lat
            double r14 = r11._long
            int r11 = r7.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r11 = r11.mapProvider
            if (r11 != r10) goto L_0x0284
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
            goto L_0x029e
        L_0x0284:
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
            goto L_0x029c
        L_0x029b:
            r11 = 0
        L_0x029c:
            r18 = 0
        L_0x029e:
            if (r2 == 0) goto L_0x02a5
            org.telegram.messenger.WebFile r2 = org.telegram.messenger.WebFile.createWithWebDocument(r2)
            goto L_0x02aa
        L_0x02a5:
            r2 = r18
            goto L_0x02aa
        L_0x02a8:
            r2 = 0
            r11 = 0
        L_0x02aa:
            org.telegram.tgnet.TLRPC$Document r12 = r7.documentAttach
            if (r12 == 0) goto L_0x02d4
            r12 = 0
        L_0x02af:
            org.telegram.tgnet.TLRPC$Document r13 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r13.attributes
            int r13 = r13.size()
            if (r12 >= r13) goto L_0x02d4
            org.telegram.tgnet.TLRPC$Document r13 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r13.attributes
            java.lang.Object r13 = r13.get(r12)
            org.telegram.tgnet.TLRPC$DocumentAttribute r13 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r13
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            if (r14 != 0) goto L_0x02cf
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            if (r14 == 0) goto L_0x02cc
            goto L_0x02cf
        L_0x02cc:
            int r12 = r12 + 1
            goto L_0x02af
        L_0x02cf:
            int r12 = r13.w
            int r13 = r13.h
            goto L_0x02d6
        L_0x02d4:
            r12 = 0
            r13 = 0
        L_0x02d6:
            if (r12 == 0) goto L_0x02da
            if (r13 != 0) goto L_0x02f9
        L_0x02da:
            org.telegram.tgnet.TLRPC$PhotoSize r14 = r7.currentPhotoObject
            if (r14 == 0) goto L_0x02e8
            if (r0 == 0) goto L_0x02e3
            r12 = -1
            r0.size = r12
        L_0x02e3:
            int r12 = r14.w
            int r13 = r14.h
            goto L_0x02f9
        L_0x02e8:
            org.telegram.tgnet.TLRPC$BotInlineResult r14 = r7.inlineResult
            if (r14 == 0) goto L_0x02f9
            int[] r12 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r14)
            r13 = r12[r1]
            r12 = r12[r6]
            r37 = r13
            r13 = r12
            r12 = r37
        L_0x02f9:
            r14 = 1117782016(0x42a00000, float:80.0)
            if (r12 == 0) goto L_0x02ff
            if (r13 != 0) goto L_0x0304
        L_0x02ff:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r13 = r12
        L_0x0304:
            org.telegram.tgnet.TLRPC$Document r15 = r7.documentAttach
            if (r15 != 0) goto L_0x0310
            org.telegram.tgnet.TLRPC$PhotoSize r15 = r7.currentPhotoObject
            if (r15 != 0) goto L_0x0310
            if (r2 != 0) goto L_0x0310
            if (r11 == 0) goto L_0x04e7
        L_0x0310:
            boolean r15 = r7.mediaWebpage
            if (r15 == 0) goto L_0x036f
            float r12 = (float) r12
            float r13 = (float) r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            float r13 = r13 / r14
            float r12 = r12 / r13
            int r12 = (int) r12
            int r13 = r7.documentAttachType
            if (r13 != r10) goto L_0x0340
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
            goto L_0x0376
        L_0x0340:
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
            goto L_0x0376
        L_0x036f:
            java.lang.String r9 = "52_52_b"
            java.lang.String r12 = "52_52"
            r31 = r9
            r9 = r12
        L_0x0376:
            org.telegram.messenger.ImageReceiver r12 = r7.linkImageView
            int r13 = r7.documentAttachType
            r14 = 6
            if (r13 != r14) goto L_0x037f
            r14 = 1
            goto L_0x0380
        L_0x037f:
            r14 = 0
        L_0x0380:
            r12.setAspectFit(r14)
            int r12 = r7.documentAttachType
            if (r12 != r10) goto L_0x0427
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            if (r0 == 0) goto L_0x03e3
            org.telegram.tgnet.TLRPC$VideoSize r0 = org.telegram.messenger.MessageObject.getDocumentVideoThumb(r0)
            if (r0 == 0) goto L_0x03b4
            org.telegram.messenger.ImageReceiver r2 = r7.linkImageView
            org.telegram.tgnet.TLRPC$Document r11 = r7.documentAttach
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$VideoSize) r0, (org.telegram.tgnet.TLRPC$Document) r11)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r11 = r7.documentAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r0, (org.telegram.tgnet.TLRPC$Document) r11)
            r25 = -1
            java.lang.Object r0 = r7.parentObject
            r28 = 1
            java.lang.String r22 = "100_100"
            r20 = r2
            r24 = r9
            r27 = r0
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x04e5
        L_0x03b4:
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForDocument(r0)
            boolean r2 = r7.isForceGif
            if (r2 == 0) goto L_0x03c0
            r0.imageType = r10
        L_0x03c0:
            org.telegram.messenger.ImageReceiver r2 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r12 = r7.documentAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r11, (org.telegram.tgnet.TLRPC$Document) r12)
            org.telegram.tgnet.TLRPC$Document r11 = r7.documentAttach
            int r11 = r11.size
            java.lang.Object r12 = r7.parentObject
            r28 = 0
            java.lang.String r22 = "100_100"
            r20 = r2
            r21 = r0
            r24 = r9
            r25 = r11
            r27 = r12
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x04e5
        L_0x03e3:
            if (r2 == 0) goto L_0x0406
            org.telegram.messenger.ImageReceiver r0 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForWebFile(r2)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r11 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Photo) r11)
            r25 = -1
            java.lang.Object r2 = r7.parentObject
            r28 = 1
            java.lang.String r22 = "100_100"
            r20 = r0
            r24 = r9
            r27 = r2
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x04e5
        L_0x0406:
            org.telegram.messenger.ImageReceiver r0 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForPath(r11)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r11 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Photo) r11)
            r25 = -1
            java.lang.Object r2 = r7.parentObject
            r28 = 1
            java.lang.String r22 = "100_100"
            r20 = r0
            r24 = r9
            r27 = r2
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x04e5
        L_0x0427:
            org.telegram.tgnet.TLRPC$PhotoSize r12 = r7.currentPhotoObject
            if (r12 == 0) goto L_0x04a8
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            boolean r2 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r2)
            if (r2 == 0) goto L_0x045a
            org.telegram.messenger.ImageReceiver r0 = r7.linkImageView
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            org.telegram.messenger.ImageLocation r28 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r9 = r7.documentAttach
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r9)
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
            goto L_0x04e5
        L_0x045a:
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            if (r2 == 0) goto L_0x0482
            org.telegram.messenger.ImageReceiver r11 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r12 = r7.currentPhotoObject
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r12, (org.telegram.tgnet.TLRPC$Document) r2)
            org.telegram.tgnet.TLRPC$Photo r2 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r0, (org.telegram.tgnet.TLRPC$Photo) r2)
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
            goto L_0x04e5
        L_0x0482:
            org.telegram.messenger.ImageReceiver r2 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r12 = r7.photoAttach
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r11, (org.telegram.tgnet.TLRPC$Photo) r12)
            org.telegram.tgnet.TLRPC$Photo r11 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r0, (org.telegram.tgnet.TLRPC$Photo) r11)
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
            goto L_0x04e5
        L_0x04a8:
            if (r2 == 0) goto L_0x04c8
            org.telegram.messenger.ImageReceiver r11 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForWebFile(r2)
            org.telegram.tgnet.TLRPC$Photo r2 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r0, (org.telegram.tgnet.TLRPC$Photo) r2)
            r25 = -1
            java.lang.Object r0 = r7.parentObject
            r28 = 1
            r20 = r11
            r22 = r9
            r24 = r31
            r27 = r0
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
            goto L_0x04e5
        L_0x04c8:
            org.telegram.messenger.ImageReceiver r2 = r7.linkImageView
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForPath(r11)
            org.telegram.tgnet.TLRPC$Photo r11 = r7.photoAttach
            org.telegram.messenger.ImageLocation r23 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r0, (org.telegram.tgnet.TLRPC$Photo) r11)
            r25 = -1
            java.lang.Object r0 = r7.parentObject
            r28 = 1
            r20 = r2
            r22 = r9
            r24 = r31
            r27 = r0
            r20.setImage(r21, r22, r23, r24, r25, r26, r27, r28)
        L_0x04e5:
            r7.drawLinkImageView = r6
        L_0x04e7:
            boolean r0 = r7.mediaWebpage
            r2 = 1094713344(0x41400000, float:12.0)
            r9 = 1103101952(0x41CLASSNAME, float:24.0)
            if (r0 == 0) goto L_0x052c
            int r0 = android.view.View.MeasureSpec.getSize(r40)
            if (r0 != 0) goto L_0x04f9
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x04f9:
            r7.setMeasuredDimension(r4, r0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r4 - r1
            int r1 = r1 / r10
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r3 = r0 - r3
            int r3 = r3 / r10
            org.telegram.ui.Components.RadialProgress2 r5 = r7.radialProgress
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = r6 + r1
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 + r3
            r5.setProgressRect(r1, r3, r6, r8)
            org.telegram.ui.Components.RadialProgress2 r1 = r7.radialProgress
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setCircleRadius(r2)
            org.telegram.messenger.ImageReceiver r1 = r7.linkImageView
            float r2 = (float) r4
            float r0 = (float) r0
            r3 = 0
            r1.setImageCoords(r3, r3, r2, r0)
            goto L_0x05f5
        L_0x052c:
            android.text.StaticLayout r0 = r7.titleLayout
            if (r0 == 0) goto L_0x0542
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x0542
            android.text.StaticLayout r0 = r7.titleLayout
            int r3 = r0.getLineCount()
            int r3 = r3 - r6
            int r0 = r0.getLineBottom(r3)
            int r1 = r1 + r0
        L_0x0542:
            android.text.StaticLayout r0 = r7.descriptionLayout
            if (r0 == 0) goto L_0x0558
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x0558
            android.text.StaticLayout r0 = r7.descriptionLayout
            int r3 = r0.getLineCount()
            int r3 = r3 - r6
            int r0 = r0.getLineBottom(r3)
            int r1 = r1 + r0
        L_0x0558:
            android.text.StaticLayout r0 = r7.linkLayout
            if (r0 == 0) goto L_0x056e
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x056e
            android.text.StaticLayout r0 = r7.linkLayout
            int r3 = r0.getLineCount()
            int r3 = r3 - r6
            int r0 = r0.getLineBottom(r3)
            int r1 = r1 + r0
        L_0x056e:
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
            if (r1 == 0) goto L_0x05a8
            int r1 = android.view.View.MeasureSpec.getSize(r39)
            r3 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r4
            int r1 = r1 - r0
            goto L_0x05ae
        L_0x05a8:
            r3 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x05ae:
            org.telegram.ui.Components.LetterDrawable r4 = r7.letterDrawable
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r10 = r1 + r0
            r11 = 1114636288(0x42700000, float:60.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r4.setBounds(r1, r6, r10, r11)
            org.telegram.messenger.ImageReceiver r4 = r7.linkImageView
            float r6 = (float) r1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r0 = (float) r0
            r4.setImageCoords(r6, r3, r0, r0)
            int r0 = r7.documentAttachType
            if (r0 == r5) goto L_0x05d1
            if (r0 != r8) goto L_0x05f5
        L_0x05d1:
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
        L_0x05f5:
            org.telegram.ui.Components.CheckBox2 r2 = r7.checkBox
            if (r2 == 0) goto L_0x0604
            r4 = 0
            r6 = 0
            r1 = r38
            r3 = r39
            r5 = r40
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
        L_0x0604:
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
            tLRPC$TL_message.peer_id = new TLRPC$TL_peerUser();
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$TL_message.from_id = tLRPC$TL_peerUser;
            TLRPC$Peer tLRPC$Peer = tLRPC$TL_message.peer_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            tLRPC$TL_peerUser.user_id = clientUserId;
            tLRPC$Peer.user_id = clientUserId;
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
                TLRPC$BotInlineResult tLRPC$BotInlineResult2 = this.inlineResult;
                String str4 = tLRPC$BotInlineResult2.title;
                if (str4 == null) {
                    str4 = str2;
                }
                tLRPC$TL_documentAttributeAudio.title = str4;
                String str5 = tLRPC$BotInlineResult2.description;
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
            this.currentMessageObject = new MessageObject(this.currentAccount, tLRPC$TL_message, false, true);
        }
    }

    public void setLink(TLRPC$BotInlineResult tLRPC$BotInlineResult, TLRPC$User tLRPC$User, boolean z, boolean z2, boolean z3, boolean z4) {
        this.needDivider = z2;
        this.needShadow = z3;
        this.inlineBot = tLRPC$User;
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
        this.isForceGif = z4;
        setAttachType();
        if (z4) {
            this.documentAttachType = 2;
        }
        requestLayout();
        updateButtonState(false, false);
    }

    public TLRPC$User getInlineBot() {
        return this.inlineBot;
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
        this.isForceGif = true;
        setAttachType();
        this.documentAttachType = 2;
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
                if (MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
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
                            int imageX = (int) (this.linkImageView.getImageX() + ((float) ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / 2)));
                            int imageY = (int) (this.linkImageView.getImageY() + ((float) ((AndroidUtilities.dp(52.0f) - intrinsicHeight) / 2)));
                            canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + ((float) AndroidUtilities.dp(52.0f)), this.linkImageView.getImageY() + ((float) AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                            Theme.chat_inlineResultLocation.setBounds(imageX, imageY, intrinsicWidth + imageX, intrinsicHeight + imageY);
                            Theme.chat_inlineResultLocation.draw(canvas);
                        }
                    } else {
                        int intrinsicWidth2 = Theme.chat_inlineResultAudio.getIntrinsicWidth();
                        int intrinsicHeight2 = Theme.chat_inlineResultAudio.getIntrinsicHeight();
                        int imageX2 = (int) (this.linkImageView.getImageX() + ((float) ((AndroidUtilities.dp(52.0f) - intrinsicWidth2) / 2)));
                        int imageY2 = (int) (this.linkImageView.getImageY() + ((float) ((AndroidUtilities.dp(52.0f) - intrinsicHeight2) / 2)));
                        canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + ((float) AndroidUtilities.dp(52.0f)), this.linkImageView.getImageY() + ((float) AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultAudio.setBounds(imageX2, imageY2, intrinsicWidth2 + imageX2, intrinsicHeight2 + imageY2);
                        Theme.chat_inlineResultAudio.draw(canvas);
                    }
                } else {
                    int intrinsicWidth3 = Theme.chat_inlineResultFile.getIntrinsicWidth();
                    int intrinsicHeight3 = Theme.chat_inlineResultFile.getIntrinsicHeight();
                    int imageX3 = (int) (this.linkImageView.getImageX() + ((float) ((AndroidUtilities.dp(52.0f) - intrinsicWidth3) / 2)));
                    int imageY3 = (int) (this.linkImageView.getImageY() + ((float) ((AndroidUtilities.dp(52.0f) - intrinsicHeight3) / 2)));
                    canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + ((float) AndroidUtilities.dp(52.0f)), this.linkImageView.getImageY() + ((float) AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
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
                    int imageX4 = (int) (this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - ((float) intrinsicWidth4)) / 2.0f));
                    int imageY4 = (int) (this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - ((float) intrinsicHeight4)) / 2.0f));
                    canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + this.linkImageView.getImageWidth(), this.linkImageView.getImageY() + this.linkImageView.getImageHeight(), LetterDrawable.paint);
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
            boolean z = this.scaled;
            if ((z && this.scale != 0.8f) || (!z && this.scale != 1.0f)) {
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

    /* JADX WARNING: Removed duplicated region for block: B:50:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x015d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateButtonState(boolean r11, boolean r12) {
        /*
            r10 = this;
            int r0 = r10.documentAttachType
            r1 = 1
            r2 = 2
            java.lang.String r3 = "."
            r4 = 3
            r5 = 5
            r6 = 0
            r7 = 4
            if (r0 == r5) goto L_0x00fc
            if (r0 != r4) goto L_0x0010
            goto L_0x00fc
        L_0x0010:
            boolean r0 = r10.mediaWebpage
            if (r0 == 0) goto L_0x014c
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.inlineResult
            if (r0 == 0) goto L_0x00d9
            org.telegram.tgnet.TLRPC$Document r8 = r0.document
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r9 == 0) goto L_0x002c
            java.lang.String r0 = org.telegram.messenger.FileLoader.getAttachFileName(r8)
            org.telegram.tgnet.TLRPC$BotInlineResult r3 = r10.inlineResult
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3)
            goto L_0x00ea
        L_0x002c:
            org.telegram.tgnet.TLRPC$Photo r8 = r0.photo
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r9 == 0) goto L_0x004a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r8.sizes
            int r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3, r1)
            r10.currentPhotoObject = r0
            java.lang.String r0 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r10.currentPhotoObject
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3)
            goto L_0x00ea
        L_0x004a:
            org.telegram.tgnet.TLRPC$WebDocument r8 = r0.content
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r8 == 0) goto L_0x009d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r10.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r8 = r8.content
            java.lang.String r8 = r8.url
            java.lang.String r8 = org.telegram.messenger.Utilities.MD5(r8)
            r0.append(r8)
            r0.append(r3)
            org.telegram.tgnet.TLRPC$BotInlineResult r3 = r10.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r3 = r3.content
            java.lang.String r8 = r3.url
            java.lang.String r3 = r3.mime_type
            java.lang.String r3 = org.telegram.messenger.FileLoader.getMimeTypePart(r3)
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r8, r3)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.io.File r3 = new java.io.File
            java.io.File r8 = org.telegram.messenger.FileLoader.getDirectory(r7)
            r3.<init>(r8, r0)
            int r8 = r10.documentAttachType
            if (r8 != r2) goto L_0x00ea
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r10.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r8 = r8.thumb
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r9 == 0) goto L_0x00ea
            java.lang.String r8 = r8.mime_type
            java.lang.String r9 = "video/mp4"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x00ea
            r0 = r6
            goto L_0x00ea
        L_0x009d:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.thumb
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r0 == 0) goto L_0x00e8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r10.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r8 = r8.thumb
            java.lang.String r8 = r8.url
            java.lang.String r8 = org.telegram.messenger.Utilities.MD5(r8)
            r0.append(r8)
            r0.append(r3)
            org.telegram.tgnet.TLRPC$BotInlineResult r3 = r10.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r3 = r3.thumb
            java.lang.String r8 = r3.url
            java.lang.String r3 = r3.mime_type
            java.lang.String r3 = org.telegram.messenger.FileLoader.getMimeTypePart(r3)
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r8, r3)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.io.File r3 = new java.io.File
            java.io.File r8 = org.telegram.messenger.FileLoader.getDirectory(r7)
            r3.<init>(r8, r0)
            goto L_0x00ea
        L_0x00d9:
            org.telegram.tgnet.TLRPC$Document r0 = r10.documentAttach
            if (r0 == 0) goto L_0x00e8
            java.lang.String r0 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$Document r3 = r10.documentAttach
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3)
            goto L_0x00ea
        L_0x00e8:
            r0 = r6
            r3 = r0
        L_0x00ea:
            org.telegram.tgnet.TLRPC$Document r8 = r10.documentAttach
            if (r8 == 0) goto L_0x00f9
            int r9 = r10.documentAttachType
            if (r9 != r2) goto L_0x00f9
            org.telegram.tgnet.TLRPC$VideoSize r8 = org.telegram.messenger.MessageObject.getDocumentVideoThumb(r8)
            if (r8 == 0) goto L_0x00f9
            goto L_0x00fa
        L_0x00f9:
            r6 = r0
        L_0x00fa:
            r0 = r3
            goto L_0x014d
        L_0x00fc:
            org.telegram.tgnet.TLRPC$Document r0 = r10.documentAttach
            if (r0 == 0) goto L_0x010b
            java.lang.String r6 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$Document r0 = r10.documentAttach
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x014d
        L_0x010b:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r0 == 0) goto L_0x014c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$BotInlineResult r6 = r10.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r6 = r6.content
            java.lang.String r6 = r6.url
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r6)
            r0.append(r6)
            r0.append(r3)
            org.telegram.tgnet.TLRPC$BotInlineResult r3 = r10.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r3 = r3.content
            java.lang.String r3 = r3.url
            int r6 = r10.documentAttachType
            if (r6 != r5) goto L_0x0135
            java.lang.String r6 = "mp3"
            goto L_0x0137
        L_0x0135:
            java.lang.String r6 = "ogg"
        L_0x0137:
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r3, r6)
            r0.append(r3)
            java.lang.String r6 = r0.toString()
            java.io.File r0 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r7)
            r0.<init>(r3, r6)
            goto L_0x014d
        L_0x014c:
            r0 = r6
        L_0x014d:
            boolean r3 = android.text.TextUtils.isEmpty(r6)
            r8 = -1
            r9 = 0
            if (r3 == 0) goto L_0x015d
            r10.buttonState = r8
            org.telegram.ui.Components.RadialProgress2 r12 = r10.radialProgress
            r12.setIcon(r7, r11, r9)
            return
        L_0x015d:
            boolean r0 = r0.exists()
            if (r0 != 0) goto L_0x01c3
            int r0 = r10.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.addLoadingFileObserver(r6, r10)
            int r0 = r10.documentAttachType
            r3 = 0
            if (r0 == r5) goto L_0x018b
            if (r0 != r4) goto L_0x0174
            goto L_0x018b
        L_0x0174:
            r10.buttonState = r1
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r0 = r0.getFileProgress(r6)
            if (r0 == 0) goto L_0x0184
            float r3 = r0.floatValue()
        L_0x0184:
            org.telegram.ui.Components.RadialProgress2 r0 = r10.radialProgress
            r0.setProgress(r3, r9)
            goto L_0x01fb
        L_0x018b:
            org.telegram.tgnet.TLRPC$Document r0 = r10.documentAttach
            if (r0 == 0) goto L_0x019a
            int r0 = r10.currentAccount
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)
            boolean r0 = r0.isLoadingFile(r6)
            goto L_0x01a2
        L_0x019a:
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            boolean r0 = r0.isLoadingHttpFile(r6)
        L_0x01a2:
            if (r0 != 0) goto L_0x01a7
            r10.buttonState = r2
            goto L_0x01fb
        L_0x01a7:
            r10.buttonState = r7
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r0 = r0.getFileProgress(r6)
            if (r0 == 0) goto L_0x01bd
            org.telegram.ui.Components.RadialProgress2 r1 = r10.radialProgress
            float r0 = r0.floatValue()
            r1.setProgress(r0, r12)
            goto L_0x01fb
        L_0x01bd:
            org.telegram.ui.Components.RadialProgress2 r0 = r10.radialProgress
            r0.setProgress(r3, r12)
            goto L_0x01fb
        L_0x01c3:
            int r0 = r10.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.removeLoadingFileObserver(r10)
            int r0 = r10.documentAttachType
            if (r0 == r5) goto L_0x01d6
            if (r0 != r4) goto L_0x01d3
            goto L_0x01d6
        L_0x01d3:
            r10.buttonState = r8
            goto L_0x01fb
        L_0x01d6:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r2 = r10.currentMessageObject
            boolean r0 = r0.isPlayingMessage(r2)
            if (r0 == 0) goto L_0x01f2
            if (r0 == 0) goto L_0x01ef
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            boolean r0 = r0.isMessagePaused()
            if (r0 == 0) goto L_0x01ef
            goto L_0x01f2
        L_0x01ef:
            r10.buttonState = r1
            goto L_0x01f4
        L_0x01f2:
            r10.buttonState = r9
        L_0x01f4:
            org.telegram.ui.Components.RadialProgress2 r0 = r10.radialProgress
            r1 = 1065353216(0x3var_, float:1.0)
            r0.setProgress(r1, r12)
        L_0x01fb:
            org.telegram.ui.Components.RadialProgress2 r0 = r10.radialProgress
            int r1 = r10.getIconForCurrentState()
            r0.setIcon(r1, r11, r12)
            r10.invalidate()
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
                break;
            case 6:
                sb.append(LocaleController.getString("AttachSticker", NUM));
                break;
            case 7:
                sb.append(LocaleController.getString("AttachPhoto", NUM));
                break;
            case 8:
                sb.append(LocaleController.getString("AttachLocation", NUM));
                break;
        }
        StaticLayout staticLayout = this.titleLayout;
        boolean z = staticLayout != null && !TextUtils.isEmpty(staticLayout.getText());
        StaticLayout staticLayout2 = this.descriptionLayout;
        boolean z2 = staticLayout2 != null && !TextUtils.isEmpty(staticLayout2.getText());
        if (this.documentAttachType != 5 || !z || !z2) {
            if (z) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(this.titleLayout.getText());
            }
            if (z2) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(this.descriptionLayout.getText());
            }
        } else {
            sb.append(", ");
            sb.append(LocaleController.formatString("AccDescrMusicInfo", NUM, this.descriptionLayout.getText(), this.titleLayout.getText()));
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
