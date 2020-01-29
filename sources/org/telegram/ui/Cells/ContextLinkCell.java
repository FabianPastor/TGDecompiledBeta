package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.PhotoViewer;

public class ContextLinkCell extends View implements DownloadController.FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private boolean canPreviewGif;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private TLRPC.PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(27.0f);
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private TLRPC.BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView = new ImageReceiver(this);
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private Object parentObject;
    private TLRPC.Photo photoAttach;
    private RadialProgress2 radialProgress;
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(7.0f);

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public ContextLinkCell(Context context) {
        super(context);
        this.linkImageView.setLayerNum(1);
        this.linkImageView.setUseSharedAnimationQueue(true);
        this.letterDrawable = new LetterDrawable();
        this.radialProgress = new RadialProgress2(this);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setFocusable(true);
    }

    /* JADX WARNING: type inference failed for: r7v2, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r11v35, types: [org.telegram.tgnet.TLRPC$WebDocument] */
    /* JADX WARNING: type inference failed for: r11v38, types: [org.telegram.tgnet.TLRPC$WebDocument] */
    /* JADX WARNING: type inference failed for: r7v5 */
    /* JADX WARNING: type inference failed for: r7v6 */
    /* JADX WARNING: type inference failed for: r7v7 */
    /* JADX WARNING: type inference failed for: r7v8 */
    /* JADX WARNING: type inference failed for: r7v9 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01d3, code lost:
        if (r3 == r1.currentPhotoObject) goto L_0x01d5;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x024b  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0269  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028c  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0293  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02cf  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02fc  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0357  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0365  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036f  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03dc  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a2  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04de  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0070 A[SYNTHETIC, Splitter:B:22:0x0070] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00d0 A[SYNTHETIC, Splitter:B:29:0x00d0] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x012b A[SYNTHETIC, Splitter:B:43:0x012b] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0175  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01bb  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0218  */
    /* JADX WARNING: Unknown variable types count: 2 */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r38, int r39) {
        /*
            r37 = this;
            r1 = r37
            r2 = 0
            r1.drawLinkImageView = r2
            r3 = 0
            r1.descriptionLayout = r3
            r1.titleLayout = r3
            r1.linkLayout = r3
            r1.currentPhotoObject = r3
            r0 = 1104674816(0x41d80000, float:27.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.linkY = r0
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            if (r0 != 0) goto L_0x002c
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            if (r0 != 0) goto L_0x002c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setMeasuredDimension(r0, r2)
            return
        L_0x002c:
            int r5 = android.view.View.MeasureSpec.getSize(r38)
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = r5 - r0
            r6 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r7 = r0 - r7
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            if (r0 == 0) goto L_0x004e
            java.util.ArrayList r8 = new java.util.ArrayList
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            r8.<init>(r0)
        L_0x004c:
            r15 = r8
            goto L_0x005f
        L_0x004e:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            if (r0 == 0) goto L_0x005e
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x005e
            java.util.ArrayList r8 = new java.util.ArrayList
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            r8.<init>(r0)
            goto L_0x004c
        L_0x005e:
            r15 = r3
        L_0x005f:
            boolean r0 = r1.mediaWebpage
            r14 = 1
            if (r0 != 0) goto L_0x016b
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            if (r0 == 0) goto L_0x016b
            java.lang.String r0 = r0.title
            r13 = 32
            r12 = 10
            if (r0 == 0) goto L_0x00ca
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00bd }
            float r0 = r8.measureText(r0)     // Catch:{ Exception -> 0x00bd }
            double r8 = (double) r0     // Catch:{ Exception -> 0x00bd }
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x00bd }
            int r0 = (int) r8     // Catch:{ Exception -> 0x00bd }
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r1.inlineResult     // Catch:{ Exception -> 0x00bd }
            java.lang.String r8 = r8.title     // Catch:{ Exception -> 0x00bd }
            java.lang.String r8 = r8.replace(r12, r13)     // Catch:{ Exception -> 0x00bd }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00bd }
            android.graphics.Paint$FontMetricsInt r9 = r9.getFontMetricsInt()     // Catch:{ Exception -> 0x00bd }
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ Exception -> 0x00bd }
            java.lang.CharSequence r8 = org.telegram.messenger.Emoji.replaceEmoji(r8, r9, r10, r2)     // Catch:{ Exception -> 0x00bd }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00bd }
            int r0 = java.lang.Math.min(r0, r7)     // Catch:{ Exception -> 0x00bd }
            float r0 = (float) r0     // Catch:{ Exception -> 0x00bd }
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x00bd }
            java.lang.CharSequence r17 = android.text.TextUtils.ellipsize(r8, r9, r0, r10)     // Catch:{ Exception -> 0x00bd }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x00bd }
            android.text.TextPaint r18 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00bd }
            r8 = 1082130432(0x40800000, float:4.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ Exception -> 0x00bd }
            int r19 = r7 + r8
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x00bd }
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r0
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)     // Catch:{ Exception -> 0x00bd }
            r1.titleLayout = r0     // Catch:{ Exception -> 0x00bd }
            goto L_0x00c1
        L_0x00bd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c1:
            org.telegram.ui.Components.LetterDrawable r0 = r1.letterDrawable
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r1.inlineResult
            java.lang.String r8 = r8.title
            r0.setTitle(r8)
        L_0x00ca:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x0121
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0118 }
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()     // Catch:{ Exception -> 0x0118 }
            r9 = 1095761920(0x41500000, float:13.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x0118 }
            java.lang.CharSequence r8 = org.telegram.messenger.Emoji.replaceEmoji(r0, r8, r9, r2)     // Catch:{ Exception -> 0x0118 }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0118 }
            r0 = 0
            r16 = 3
            r10 = r7
            r11 = r7
            r3 = 10
            r12 = r0
            r6 = 32
            r13 = r16
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0116 }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x0116 }
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x0116 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x0116 }
            if (r0 <= 0) goto L_0x0125
            int r0 = r1.descriptionY     // Catch:{ Exception -> 0x0116 }
            android.text.StaticLayout r8 = r1.descriptionLayout     // Catch:{ Exception -> 0x0116 }
            android.text.StaticLayout r9 = r1.descriptionLayout     // Catch:{ Exception -> 0x0116 }
            int r9 = r9.getLineCount()     // Catch:{ Exception -> 0x0116 }
            int r9 = r9 - r14
            int r8 = r8.getLineBottom(r9)     // Catch:{ Exception -> 0x0116 }
            int r0 = r0 + r8
            r8 = 1065353216(0x3var_, float:1.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ Exception -> 0x0116 }
            int r0 = r0 + r8
            r1.linkY = r0     // Catch:{ Exception -> 0x0116 }
            goto L_0x0125
        L_0x0116:
            r0 = move-exception
            goto L_0x011d
        L_0x0118:
            r0 = move-exception
            r3 = 10
            r6 = 32
        L_0x011d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0125
        L_0x0121:
            r3 = 10
            r6 = 32
        L_0x0125:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            java.lang.String r0 = r0.url
            if (r0 == 0) goto L_0x016b
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0164 }
            float r0 = r8.measureText(r0)     // Catch:{ Exception -> 0x0164 }
            double r8 = (double) r0     // Catch:{ Exception -> 0x0164 }
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x0164 }
            int r0 = (int) r8     // Catch:{ Exception -> 0x0164 }
            org.telegram.tgnet.TLRPC$BotInlineResult r8 = r1.inlineResult     // Catch:{ Exception -> 0x0164 }
            java.lang.String r8 = r8.url     // Catch:{ Exception -> 0x0164 }
            java.lang.String r3 = r8.replace(r3, r6)     // Catch:{ Exception -> 0x0164 }
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0164 }
            int r0 = java.lang.Math.min(r0, r7)     // Catch:{ Exception -> 0x0164 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x0164 }
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0164 }
            java.lang.CharSequence r9 = android.text.TextUtils.ellipsize(r3, r6, r0, r8)     // Catch:{ Exception -> 0x0164 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0164 }
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0164 }
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0164 }
            r13 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r6 = 0
            r8 = r0
            r11 = r7
            r7 = 1
            r14 = r3
            r3 = r15
            r15 = r6
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x0162 }
            r1.linkLayout = r0     // Catch:{ Exception -> 0x0162 }
            goto L_0x016d
        L_0x0162:
            r0 = move-exception
            goto L_0x0167
        L_0x0164:
            r0 = move-exception
            r3 = r15
            r7 = 1
        L_0x0167:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x016d
        L_0x016b:
            r3 = r15
            r7 = 1
        L_0x016d:
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            r6 = 3
            r8 = 5
            r9 = 80
            if (r0 == 0) goto L_0x01bb
            boolean r0 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r0)
            r3 = 90
            if (r0 == 0) goto L_0x0188
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3)
            r1.currentPhotoObject = r0
            goto L_0x01d5
        L_0x0188:
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            boolean r0 = org.telegram.messenger.MessageObject.isStickerDocument(r0)
            if (r0 != 0) goto L_0x01aa
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            boolean r0 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r0, r7)
            if (r0 == 0) goto L_0x0199
            goto L_0x01aa
        L_0x0199:
            int r0 = r1.documentAttachType
            if (r0 == r8) goto L_0x01d5
            if (r0 == r6) goto L_0x01d5
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3)
            r1.currentPhotoObject = r0
            goto L_0x01d5
        L_0x01aa:
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3)
            r1.currentPhotoObject = r0
            java.lang.String r3 = "webp"
            r25 = r3
            r3 = 0
            goto L_0x01d8
        L_0x01bb:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            if (r0 == 0) goto L_0x01d5
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x01d5
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r0, r7)
            r1.currentPhotoObject = r0
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r9)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r3 != r0) goto L_0x01d6
        L_0x01d5:
            r3 = 0
        L_0x01d6:
            r25 = 0
        L_0x01d8:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            r10 = 2
            if (r0 == 0) goto L_0x028c
            org.telegram.tgnet.TLRPC$WebDocument r11 = r0.content
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r11 == 0) goto L_0x0215
            java.lang.String r0 = r0.type
            if (r0 == 0) goto L_0x0215
            java.lang.String r11 = "gif"
            boolean r0 = r0.startsWith(r11)
            if (r0 == 0) goto L_0x01f8
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            org.telegram.tgnet.TLRPC$TL_webDocument r0 = (org.telegram.tgnet.TLRPC.TL_webDocument) r0
            r1.documentAttachType = r10
            goto L_0x0216
        L_0x01f8:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            java.lang.String r0 = r0.type
            java.lang.String r11 = "photo"
            boolean r0 = r0.equals(r11)
            if (r0 == 0) goto L_0x0215
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r11 = r0.thumb
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r12 == 0) goto L_0x0210
            r0 = r11
            org.telegram.tgnet.TLRPC$TL_webDocument r0 = (org.telegram.tgnet.TLRPC.TL_webDocument) r0
            goto L_0x0216
        L_0x0210:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            org.telegram.tgnet.TLRPC$TL_webDocument r0 = (org.telegram.tgnet.TLRPC.TL_webDocument) r0
            goto L_0x0216
        L_0x0215:
            r0 = 0
        L_0x0216:
            if (r0 != 0) goto L_0x0223
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r1.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r11 = r11.thumb
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r12 == 0) goto L_0x0223
            r0 = r11
            org.telegram.tgnet.TLRPC$TL_webDocument r0 = (org.telegram.tgnet.TLRPC.TL_webDocument) r0
        L_0x0223:
            if (r0 != 0) goto L_0x0280
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r1.currentPhotoObject
            if (r11 != 0) goto L_0x0280
            if (r3 != 0) goto L_0x0280
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r1.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r11 = r11.send_message
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue
            if (r12 != 0) goto L_0x0237
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo
            if (r11 == 0) goto L_0x0280
        L_0x0237:
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r1.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r11 = r11.send_message
            org.telegram.tgnet.TLRPC$GeoPoint r11 = r11.geo
            double r12 = r11.lat
            double r14 = r11._long
            int r11 = r1.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r11 = r11.mapProvider
            if (r11 != r10) goto L_0x0269
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r1.inlineResult
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
            r17 = r11
            r11 = 0
            goto L_0x0283
        L_0x0269:
            int r11 = r1.currentAccount
            r31 = 72
            r32 = 72
            r33 = 1
            r34 = 15
            r35 = -1
            r26 = r11
            r27 = r12
            r29 = r14
            java.lang.String r11 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r26, r27, r29, r31, r32, r33, r34, r35)
            goto L_0x0281
        L_0x0280:
            r11 = 0
        L_0x0281:
            r17 = 0
        L_0x0283:
            if (r0 == 0) goto L_0x028f
            org.telegram.messenger.WebFile r0 = org.telegram.messenger.WebFile.createWithWebDocument(r0)
            r17 = r0
            goto L_0x028f
        L_0x028c:
            r11 = 0
            r17 = 0
        L_0x028f:
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            if (r0 == 0) goto L_0x02b9
            r0 = 0
        L_0x0294:
            org.telegram.tgnet.TLRPC$Document r12 = r1.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r12.attributes
            int r12 = r12.size()
            if (r0 >= r12) goto L_0x02b9
            org.telegram.tgnet.TLRPC$Document r12 = r1.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r12.attributes
            java.lang.Object r12 = r12.get(r0)
            org.telegram.tgnet.TLRPC$DocumentAttribute r12 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r12
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r13 != 0) goto L_0x02b4
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo
            if (r13 == 0) goto L_0x02b1
            goto L_0x02b4
        L_0x02b1:
            int r0 = r0 + 1
            goto L_0x0294
        L_0x02b4:
            int r0 = r12.w
            int r12 = r12.h
            goto L_0x02bb
        L_0x02b9:
            r0 = 0
            r12 = 0
        L_0x02bb:
            if (r0 == 0) goto L_0x02bf
            if (r12 != 0) goto L_0x02dc
        L_0x02bf:
            org.telegram.tgnet.TLRPC$PhotoSize r13 = r1.currentPhotoObject
            if (r13 == 0) goto L_0x02cf
            if (r3 == 0) goto L_0x02c8
            r0 = -1
            r3.size = r0
        L_0x02c8:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r12 = r0.w
            int r0 = r0.h
            goto L_0x02e1
        L_0x02cf:
            org.telegram.tgnet.TLRPC$BotInlineResult r13 = r1.inlineResult
            if (r13 == 0) goto L_0x02dc
            int[] r0 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r13)
            r12 = r0[r2]
            r0 = r0[r7]
            goto L_0x02e1
        L_0x02dc:
            r36 = r12
            r12 = r0
            r0 = r36
        L_0x02e1:
            r13 = 1117782016(0x42a00000, float:80.0)
            if (r12 == 0) goto L_0x02e7
            if (r0 != 0) goto L_0x02ec
        L_0x02e7:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r0 = r12
        L_0x02ec:
            org.telegram.tgnet.TLRPC$Document r14 = r1.documentAttach
            if (r14 != 0) goto L_0x02f8
            org.telegram.tgnet.TLRPC$PhotoSize r14 = r1.currentPhotoObject
            if (r14 != 0) goto L_0x02f8
            if (r17 != 0) goto L_0x02f8
            if (r11 == 0) goto L_0x049c
        L_0x02f8:
            boolean r14 = r1.mediaWebpage
            if (r14 == 0) goto L_0x0357
            float r12 = (float) r12
            float r0 = (float) r0
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r0 = r0 / r13
            float r12 = r12 / r0
            int r0 = (int) r12
            int r12 = r1.documentAttachType
            if (r12 != r10) goto L_0x0328
            java.util.Locale r12 = java.util.Locale.US
            java.lang.Object[] r13 = new java.lang.Object[r10]
            float r0 = (float) r0
            float r14 = org.telegram.messenger.AndroidUtilities.density
            float r0 = r0 / r14
            int r0 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r13[r2] = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
            r13[r7] = r0
            java.lang.String r0 = "%d_%d_b"
            java.lang.String r0 = java.lang.String.format(r12, r0, r13)
            r30 = r0
            goto L_0x035e
        L_0x0328:
            java.util.Locale r12 = java.util.Locale.US
            java.lang.Object[] r13 = new java.lang.Object[r10]
            float r0 = (float) r0
            float r14 = org.telegram.messenger.AndroidUtilities.density
            float r0 = r0 / r14
            int r0 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r13[r2] = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
            r13[r7] = r0
            java.lang.String r0 = "%d_%d"
            java.lang.String r0 = java.lang.String.format(r12, r0, r13)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r0)
            java.lang.String r12 = "_b"
            r9.append(r12)
            java.lang.String r9 = r9.toString()
            r30 = r9
            goto L_0x035e
        L_0x0357:
            java.lang.String r0 = "52_52_b"
            java.lang.String r9 = "52_52"
            r30 = r0
            r0 = r9
        L_0x035e:
            org.telegram.messenger.ImageReceiver r9 = r1.linkImageView
            int r12 = r1.documentAttachType
            r13 = 6
            if (r12 != r13) goto L_0x0367
            r12 = 1
            goto L_0x0368
        L_0x0367:
            r12 = 0
        L_0x0368:
            r9.setAspectFit(r12)
            int r9 = r1.documentAttachType
            if (r9 != r10) goto L_0x03dc
            org.telegram.tgnet.TLRPC$Document r3 = r1.documentAttach
            if (r3 == 0) goto L_0x0398
            org.telegram.messenger.ImageReceiver r9 = r1.linkImageView
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            r21 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r11 = r1.documentAttach
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForDocument(r3, r11)
            org.telegram.tgnet.TLRPC$Document r3 = r1.documentAttach
            int r3 = r3.size
            java.lang.Object r11 = r1.parentObject
            r27 = 0
            r19 = r9
            r23 = r0
            r24 = r3
            r26 = r11
            r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27)
            goto L_0x049a
        L_0x0398:
            if (r17 == 0) goto L_0x03bb
            org.telegram.messenger.ImageReceiver r3 = r1.linkImageView
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForWebFile(r17)
            r21 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r11 = r1.photoAttach
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForPhoto(r9, r11)
            r24 = -1
            java.lang.Object r9 = r1.parentObject
            r27 = 1
            r19 = r3
            r23 = r0
            r26 = r9
            r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27)
            goto L_0x049a
        L_0x03bb:
            org.telegram.messenger.ImageReceiver r3 = r1.linkImageView
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForPath(r11)
            r21 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r11 = r1.photoAttach
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForPhoto(r9, r11)
            r24 = -1
            java.lang.Object r9 = r1.parentObject
            r27 = 1
            r19 = r3
            r23 = r0
            r26 = r9
            r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27)
            goto L_0x049a
        L_0x03dc:
            org.telegram.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            if (r9 == 0) goto L_0x045d
            org.telegram.tgnet.TLRPC$Document r9 = r1.documentAttach
            boolean r9 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r9)
            if (r9 == 0) goto L_0x040f
            org.telegram.messenger.ImageReceiver r0 = r1.linkImageView
            org.telegram.tgnet.TLRPC$Document r3 = r1.documentAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r9 = r1.documentAttach
            org.telegram.messenger.ImageLocation r29 = org.telegram.messenger.ImageLocation.getForDocument(r3, r9)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            int r3 = r3.size
            r32 = 0
            java.lang.Object r9 = r1.parentObject
            r34 = 0
            java.lang.String r28 = "80_80"
            r26 = r0
            r31 = r3
            r33 = r9
            r26.setImage(r27, r28, r29, r30, r31, r32, r33, r34)
            goto L_0x049a
        L_0x040f:
            org.telegram.tgnet.TLRPC$Document r9 = r1.documentAttach
            if (r9 == 0) goto L_0x0437
            org.telegram.messenger.ImageReceiver r11 = r1.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r12 = r1.currentPhotoObject
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForDocument(r12, r9)
            org.telegram.tgnet.TLRPC$Photo r9 = r1.photoAttach
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r9)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            int r3 = r3.size
            java.lang.Object r9 = r1.parentObject
            r27 = 0
            r19 = r11
            r21 = r0
            r23 = r30
            r24 = r3
            r26 = r9
            r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27)
            goto L_0x049a
        L_0x0437:
            org.telegram.messenger.ImageReceiver r9 = r1.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r12 = r1.photoAttach
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForPhoto(r11, r12)
            org.telegram.tgnet.TLRPC$Photo r11 = r1.photoAttach
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r11)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            int r3 = r3.size
            java.lang.Object r11 = r1.parentObject
            r27 = 0
            r19 = r9
            r21 = r0
            r23 = r30
            r24 = r3
            r26 = r11
            r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27)
            goto L_0x049a
        L_0x045d:
            if (r17 == 0) goto L_0x047d
            org.telegram.messenger.ImageReceiver r9 = r1.linkImageView
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForWebFile(r17)
            org.telegram.tgnet.TLRPC$Photo r11 = r1.photoAttach
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r11)
            r24 = -1
            java.lang.Object r3 = r1.parentObject
            r27 = 1
            r19 = r9
            r21 = r0
            r23 = r30
            r26 = r3
            r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27)
            goto L_0x049a
        L_0x047d:
            org.telegram.messenger.ImageReceiver r9 = r1.linkImageView
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForPath(r11)
            org.telegram.tgnet.TLRPC$Photo r11 = r1.photoAttach
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r11)
            r24 = -1
            java.lang.Object r3 = r1.parentObject
            r27 = 1
            r19 = r9
            r21 = r0
            r23 = r30
            r26 = r3
            r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27)
        L_0x049a:
            r1.drawLinkImageView = r7
        L_0x049c:
            boolean r0 = r1.mediaWebpage
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            if (r0 == 0) goto L_0x04de
            int r0 = android.view.View.MeasureSpec.getSize(r39)
            if (r0 != 0) goto L_0x04ac
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
        L_0x04ac:
            r1.setMeasuredDimension(r5, r0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r5 - r4
            int r4 = r4 / r10
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = r0 - r6
            int r6 = r6 / r10
            org.telegram.ui.Components.RadialProgress2 r7 = r1.radialProgress
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r8 = r8 + r4
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r6
            r7.setProgressRect(r4, r6, r8, r3)
            org.telegram.ui.Components.RadialProgress2 r3 = r1.radialProgress
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setCircleRadius(r4)
            org.telegram.messenger.ImageReceiver r3 = r1.linkImageView
            r3.setImageCoords(r2, r2, r5, r0)
            goto L_0x05aa
        L_0x04de:
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x04f4
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x04f4
            android.text.StaticLayout r0 = r1.titleLayout
            int r4 = r0.getLineCount()
            int r4 = r4 - r7
            int r0 = r0.getLineBottom(r4)
            int r2 = r2 + r0
        L_0x04f4:
            android.text.StaticLayout r0 = r1.descriptionLayout
            if (r0 == 0) goto L_0x050a
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x050a
            android.text.StaticLayout r0 = r1.descriptionLayout
            int r4 = r0.getLineCount()
            int r4 = r4 - r7
            int r0 = r0.getLineBottom(r4)
            int r2 = r2 + r0
        L_0x050a:
            android.text.StaticLayout r0 = r1.linkLayout
            if (r0 == 0) goto L_0x0520
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0520
            android.text.StaticLayout r0 = r1.linkLayout
            int r4 = r0.getLineCount()
            int r4 = r4 - r7
            int r0 = r0.getLineBottom(r4)
            int r2 = r2 + r0
        L_0x0520:
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r0, r2)
            int r2 = android.view.View.MeasureSpec.getSize(r38)
            r4 = 1116209152(0x42880000, float:68.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r5
            int r0 = java.lang.Math.max(r4, r0)
            boolean r4 = r1.needDivider
            int r0 = r0 + r4
            r1.setMeasuredDimension(r2, r0)
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x055c
            int r2 = android.view.View.MeasureSpec.getSize(r38)
            r4 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 - r5
            int r2 = r2 - r0
            goto L_0x0562
        L_0x055c:
            r4 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
        L_0x0562:
            org.telegram.ui.Components.LetterDrawable r5 = r1.letterDrawable
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r9 = r2 + r0
            r10 = 1114636288(0x42700000, float:60.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r5.setBounds(r2, r7, r9, r10)
            org.telegram.messenger.ImageReceiver r5 = r1.linkImageView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5.setImageCoords(r2, r4, r0, r0)
            int r0 = r1.documentAttachType
            if (r0 == r6) goto L_0x0582
            if (r0 != r8) goto L_0x05aa
        L_0x0582:
            org.telegram.ui.Components.RadialProgress2 r0 = r1.radialProgress
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0.setCircleRadius(r3)
            org.telegram.ui.Components.RadialProgress2 r0 = r1.radialProgress
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r2
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 + r5
            r5 = 1113587712(0x42600000, float:56.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.setProgressRect(r3, r4, r2, r5)
        L_0x05aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onMeasure(int, int):void");
    }

    private void setAttachType() {
        String str;
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        TLRPC.Document document = this.documentAttach;
        if (document == null) {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (botInlineResult != null) {
                if (botInlineResult.photo != null) {
                    this.documentAttachType = 7;
                } else if (botInlineResult.type.equals("audio")) {
                    this.documentAttachType = 5;
                } else if (this.inlineResult.type.equals("voice")) {
                    this.documentAttachType = 3;
                }
            }
        } else if (MessageObject.isGifDocument(document)) {
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
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.out = true;
            tL_message.id = -Utilities.random.nextInt();
            tL_message.to_id = new TLRPC.TL_peerUser();
            TLRPC.Peer peer = tL_message.to_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            tL_message.from_id = clientUserId;
            peer.user_id = clientUserId;
            tL_message.date = (int) (System.currentTimeMillis() / 1000);
            String str2 = "";
            tL_message.message = str2;
            tL_message.media = new TLRPC.TL_messageMediaDocument();
            TLRPC.MessageMedia messageMedia = tL_message.media;
            messageMedia.flags |= 3;
            messageMedia.document = new TLRPC.TL_document();
            TLRPC.MessageMedia messageMedia2 = tL_message.media;
            messageMedia2.document.file_reference = new byte[0];
            tL_message.flags |= 768;
            TLRPC.Document document2 = this.documentAttach;
            if (document2 != null) {
                messageMedia2.document = document2;
                tL_message.attachPath = str2;
            } else {
                String str3 = "mp3";
                String httpUrlExtension = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? str3 : "ogg");
                TLRPC.Document document3 = tL_message.media.document;
                document3.id = 0;
                document3.access_hash = 0;
                document3.date = tL_message.date;
                document3.mime_type = "audio/" + httpUrlExtension;
                TLRPC.Document document4 = tL_message.media.document;
                document4.size = 0;
                document4.dc_id = 0;
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio = new TLRPC.TL_documentAttributeAudio();
                tL_documentAttributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                String str4 = this.inlineResult.title;
                if (str4 == null) {
                    str4 = str2;
                }
                tL_documentAttributeAudio.title = str4;
                String str5 = this.inlineResult.description;
                if (str5 != null) {
                    str2 = str5;
                }
                tL_documentAttributeAudio.performer = str2;
                tL_documentAttributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    tL_documentAttributeAudio.voice = true;
                }
                tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                TLRPC.TL_documentAttributeFilename tL_documentAttributeFilename = new TLRPC.TL_documentAttributeFilename();
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
                tL_documentAttributeFilename.file_name = sb.toString();
                tL_message.media.document.attributes.add(tL_documentAttributeFilename);
                File directory = FileLoader.getDirectory(4);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(Utilities.MD5(this.inlineResult.content.url));
                sb2.append(".");
                String str7 = this.inlineResult.content.url;
                if (this.documentAttachType != 5) {
                    str3 = "ogg";
                }
                sb2.append(ImageLoader.getHttpUrlExtension(str7, str3));
                tL_message.attachPath = new File(directory, sb2.toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, tL_message, false);
        }
    }

    public void setLink(TLRPC.BotInlineResult botInlineResult, boolean z, boolean z2, boolean z3) {
        this.needDivider = z2;
        this.needShadow = z3;
        this.inlineResult = botInlineResult;
        this.parentObject = botInlineResult;
        TLRPC.BotInlineResult botInlineResult2 = this.inlineResult;
        if (botInlineResult2 != null) {
            this.documentAttach = botInlineResult2.document;
            this.photoAttach = botInlineResult2.photo;
        } else {
            this.documentAttach = null;
            this.photoAttach = null;
        }
        this.mediaWebpage = z;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
    }

    public void setGif(TLRPC.Document document, boolean z) {
        this.needDivider = z;
        this.needShadow = false;
        this.inlineResult = null;
        this.parentObject = "gif" + document;
        this.documentAttach = document;
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

    public TLRPC.Document getDocument() {
        return this.documentAttach;
    }

    public TLRPC.BotInlineResult getBotInlineResult() {
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

    public boolean isCanPreviewGif() {
        return this.canPreviewGif;
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

    /* JADX WARNING: Removed duplicated region for block: B:53:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:57:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            boolean r0 = r8.mediaWebpage
            if (r0 != 0) goto L_0x00e0
            org.telegram.ui.Cells.ContextLinkCell$ContextLinkCellDelegate r0 = r8.delegate
            if (r0 == 0) goto L_0x00e0
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r8.inlineResult
            if (r0 != 0) goto L_0x000e
            goto L_0x00e0
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
            goto L_0x00d9
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
            goto L_0x00d9
        L_0x0083:
            org.telegram.ui.Components.LetterDrawable r2 = r8.letterDrawable
            android.graphics.Rect r2 = r2.getBounds()
            boolean r0 = r2.contains(r0, r1)
            int r1 = r9.getAction()
            if (r1 != 0) goto L_0x00a2
            if (r0 == 0) goto L_0x0081
            r8.buttonPressed = r5
            org.telegram.ui.Components.RadialProgress2 r0 = r8.radialProgress
            boolean r1 = r8.buttonPressed
            r0.setPressed(r1, r6)
            r8.invalidate()
            goto L_0x00d9
        L_0x00a2:
            boolean r1 = r8.buttonPressed
            if (r1 == 0) goto L_0x0081
            int r1 = r9.getAction()
            if (r1 != r5) goto L_0x00b8
            r8.buttonPressed = r6
            r8.playSoundEffect(r6)
            r8.didPressedButton()
            r8.invalidate()
            goto L_0x00d1
        L_0x00b8:
            int r1 = r9.getAction()
            if (r1 != r4) goto L_0x00c4
            r8.buttonPressed = r6
            r8.invalidate()
            goto L_0x00d1
        L_0x00c4:
            int r1 = r9.getAction()
            if (r1 != r3) goto L_0x00d1
            if (r0 != 0) goto L_0x00d1
            r8.buttonPressed = r6
            r8.invalidate()
        L_0x00d1:
            org.telegram.ui.Components.RadialProgress2 r0 = r8.radialProgress
            boolean r1 = r8.buttonPressed
            r0.setPressed(r1, r6)
            goto L_0x0081
        L_0x00d9:
            if (r5 != 0) goto L_0x00df
            boolean r5 = super.onTouchEvent(r9)
        L_0x00df:
            return r5
        L_0x00e0:
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
                if (MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i2 == 2) {
                this.radialProgress.setProgress(0.0f, false);
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.inlineResult, 1, 0);
                } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).loadFile(WebFile.createWithWebDocument(this.inlineResult.content), 1, 1);
                }
                this.buttonState = 4;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            } else if (i2 == 4) {
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
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
                TLRPC.BotInlineResult botInlineResult = this.inlineResult;
                if (botInlineResult == null || !botInlineResult.type.equals("file")) {
                    TLRPC.BotInlineResult botInlineResult2 = this.inlineResult;
                    if (botInlineResult2 == null || (!botInlineResult2.type.equals("audio") && !this.inlineResult.type.equals("voice"))) {
                        TLRPC.BotInlineResult botInlineResult3 = this.inlineResult;
                        if (botInlineResult3 == null || (!botInlineResult3.type.equals("venue") && !this.inlineResult.type.equals("geo"))) {
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
            TLRPC.BotInlineResult botInlineResult4 = this.inlineResult;
            if (botInlineResult4 != null) {
                TLRPC.BotInlineMessage botInlineMessage = botInlineResult4.send_message;
                if ((botInlineMessage instanceof TLRPC.TL_botInlineMessageMediaGeo) || (botInlineMessage instanceof TLRPC.TL_botInlineMessageMediaVenue)) {
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
            TLRPC.BotInlineResult botInlineResult5 = this.inlineResult;
            if (botInlineResult5 != null) {
                this.linkImageView.setVisible(!PhotoViewer.isShowingImage(botInlineResult5), false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || (!this.scaled && this.scale != 1.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                this.lastUpdateTime = currentTimeMillis;
                if (this.scaled) {
                    float f2 = this.scale;
                    if (f2 != 0.8f) {
                        this.scale = f2 - (((float) j) / 400.0f);
                        if (this.scale < 0.8f) {
                            this.scale = 0.8f;
                        }
                        invalidate();
                    }
                }
                this.scale += ((float) j) / 400.0f;
                if (this.scale > 1.0f) {
                    this.scale = 1.0f;
                }
                invalidate();
            }
            float f3 = this.scale;
            canvas.scale(f3, f3, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
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

    /* JADX WARNING: Removed duplicated region for block: B:35:0x0120 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0121  */
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
            if (r0 == r6) goto L_0x00c9
            if (r0 != r4) goto L_0x000f
            goto L_0x00c9
        L_0x000f:
            boolean r0 = r9.mediaWebpage
            if (r0 == 0) goto L_0x0119
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r9.inlineResult
            if (r0 == 0) goto L_0x00ba
            org.telegram.tgnet.TLRPC$Document r7 = r0.document
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_document
            if (r8 == 0) goto L_0x002b
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r7)
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r9.inlineResult
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x011a
        L_0x002b:
            org.telegram.tgnet.TLRPC$Photo r7 = r0.photo
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photo
            if (r8 == 0) goto L_0x004b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r7.sizes
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r1, r2)
            r9.currentPhotoObject = r0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r9.currentPhotoObject
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r9.currentPhotoObject
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x011a
        L_0x004b:
            org.telegram.tgnet.TLRPC$WebDocument r7 = r0.content
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            java.lang.String r8 = "jpg"
            if (r7 == 0) goto L_0x0084
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
            goto L_0x011a
        L_0x0084:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.thumb
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r0 == 0) goto L_0x0119
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
            goto L_0x011a
        L_0x00ba:
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            if (r0 == 0) goto L_0x0119
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x011a
        L_0x00c9:
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            if (r0 == 0) goto L_0x00d8
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            goto L_0x011a
        L_0x00d8:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r9.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r0 == 0) goto L_0x0119
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
            if (r3 != r6) goto L_0x0102
            java.lang.String r3 = "mp3"
            goto L_0x0104
        L_0x0102:
            java.lang.String r3 = "ogg"
        L_0x0104:
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r3)
            r0.append(r1)
            java.lang.String r1 = r0.toString()
            java.io.File r0 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r5)
            r0.<init>(r3, r1)
            goto L_0x011a
        L_0x0119:
            r0 = r1
        L_0x011a:
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 == 0) goto L_0x0121
            return
        L_0x0121:
            boolean r0 = r0.exists()
            r3 = 0
            if (r0 != 0) goto L_0x01a6
            int r0 = r9.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.addLoadingFileObserver(r1, r9)
            int r0 = r9.documentAttachType
            r7 = 0
            if (r0 == r6) goto L_0x0158
            if (r0 != r4) goto L_0x0139
            goto L_0x0158
        L_0x0139:
            r9.buttonState = r2
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r0 = r0.getFileProgress(r1)
            if (r0 == 0) goto L_0x0149
            float r7 = r0.floatValue()
        L_0x0149:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            r0.setProgress(r7, r3)
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            int r1 = r9.getIconForCurrentState()
            r0.setIcon(r1, r10, r11)
            goto L_0x01a2
        L_0x0158:
            org.telegram.tgnet.TLRPC$Document r0 = r9.documentAttach
            if (r0 == 0) goto L_0x0167
            int r0 = r9.currentAccount
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)
            boolean r0 = r0.isLoadingFile(r1)
            goto L_0x016f
        L_0x0167:
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            boolean r0 = r0.isLoadingHttpFile(r1)
        L_0x016f:
            if (r0 != 0) goto L_0x017e
            r0 = 2
            r9.buttonState = r0
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            int r1 = r9.getIconForCurrentState()
            r0.setIcon(r1, r10, r11)
            goto L_0x01a2
        L_0x017e:
            r9.buttonState = r5
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r0 = r0.getFileProgress(r1)
            if (r0 == 0) goto L_0x0194
            org.telegram.ui.Components.RadialProgress2 r1 = r9.radialProgress
            float r0 = r0.floatValue()
            r1.setProgress(r0, r11)
            goto L_0x0199
        L_0x0194:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            r0.setProgress(r7, r11)
        L_0x0199:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            int r1 = r9.getIconForCurrentState()
            r0.setIcon(r1, r10, r11)
        L_0x01a2:
            r9.invalidate()
            goto L_0x01eb
        L_0x01a6:
            int r0 = r9.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.removeLoadingFileObserver(r9)
            int r0 = r9.documentAttachType
            if (r0 == r6) goto L_0x01ba
            if (r0 != r4) goto L_0x01b6
            goto L_0x01ba
        L_0x01b6:
            r0 = -1
            r9.buttonState = r0
            goto L_0x01df
        L_0x01ba:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r1 = r9.currentMessageObject
            boolean r0 = r0.isPlayingMessage(r1)
            if (r0 == 0) goto L_0x01d6
            if (r0 == 0) goto L_0x01d3
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            boolean r0 = r0.isMessagePaused()
            if (r0 == 0) goto L_0x01d3
            goto L_0x01d6
        L_0x01d3:
            r9.buttonState = r2
            goto L_0x01d8
        L_0x01d6:
            r9.buttonState = r3
        L_0x01d8:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            r1 = 1065353216(0x3var_, float:1.0)
            r0.setProgress(r1, r11)
        L_0x01df:
            org.telegram.ui.Components.RadialProgress2 r0 = r9.radialProgress
            int r1 = r9.getIconForCurrentState()
            r0.setIcon(r1, r10, r11)
            r9.invalidate()
        L_0x01eb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.updateButtonState(boolean, boolean):void");
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public TLRPC.BotInlineResult getResult() {
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
    }
}
