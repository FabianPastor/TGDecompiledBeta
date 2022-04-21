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
import android.view.MotionEvent;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.PhotoViewer;

public class ContextLinkCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
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
    private TLRPC.PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY;
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private boolean hideLoadProgress;
    /* access modifiers changed from: private */
    public float imageScale;
    private TLRPC.User inlineBot;
    private TLRPC.BotInlineResult inlineResult;
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
    private TLRPC.Photo photoAttach;
    private RadialProgress2 radialProgress;
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY;

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public ContextLinkCell(Context context) {
        this(context, false);
    }

    public ContextLinkCell(Context context, boolean needsCheckBox) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.titleY = AndroidUtilities.dp(7.0f);
        this.descriptionY = AndroidUtilities.dp(27.0f);
        this.imageScale = 1.0f;
        this.IMAGE_SCALE = new AnimationProperties.FloatProperty<ContextLinkCell>("animationValue") {
            public void setValue(ContextLinkCell object, float value) {
                float unused = ContextLinkCell.this.imageScale = value;
                ContextLinkCell.this.invalidate();
            }

            public Float get(ContextLinkCell object) {
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
        if (needsCheckBox) {
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

    /* JADX WARNING: type inference failed for: r1v1, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r2v70, types: [org.telegram.tgnet.TLRPC$WebDocument] */
    /* JADX WARNING: type inference failed for: r2v72, types: [org.telegram.tgnet.TLRPC$WebDocument] */
    /* JADX WARNING: type inference failed for: r1v7 */
    /* JADX WARNING: type inference failed for: r1v14 */
    /* JADX WARNING: type inference failed for: r1v15 */
    /* JADX WARNING: type inference failed for: r1v16 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0302  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0332 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x033c  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x034f  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0362 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0381  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03eb  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x03f3  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x049a  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x05bb  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x05fc  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x06d1  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x06e1  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x014a A[SYNTHETIC, Splitter:B:42:0x014a] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0190  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01a3  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01eb  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0210  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r36, int r37) {
        /*
            r35 = this;
            r7 = r35
            r1 = 0
            r7.drawLinkImageView = r1
            r0 = 0
            r7.descriptionLayout = r0
            r7.titleLayout = r0
            r7.linkLayout = r0
            r7.currentPhotoObject = r0
            r0 = 1104674816(0x41d80000, float:27.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.linkY = r0
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            r2 = 1120403456(0x42CLASSNAME, float:100.0)
            if (r0 != 0) goto L_0x002c
            org.telegram.tgnet.TLRPC$Document r0 = r7.documentAttach
            if (r0 != 0) goto L_0x002c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r7.setMeasuredDimension(r0, r1)
            return
        L_0x002c:
            int r8 = android.view.View.MeasureSpec.getSize(r36)
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = r8 - r0
            r3 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = r0 - r4
            r4 = 0
            r0 = 0
            r5 = 0
            r17 = 0
            r18 = 0
            org.telegram.tgnet.TLRPC$Document r9 = r7.documentAttach
            if (r9 == 0) goto L_0x0058
            java.util.ArrayList r9 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$Document r10 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r10.thumbs
            r9.<init>(r10)
            r0 = r9
            r15 = r0
            goto L_0x006f
        L_0x0058:
            org.telegram.tgnet.TLRPC$BotInlineResult r9 = r7.inlineResult
            if (r9 == 0) goto L_0x006e
            org.telegram.tgnet.TLRPC$Photo r9 = r9.photo
            if (r9 == 0) goto L_0x006e
            java.util.ArrayList r9 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$BotInlineResult r10 = r7.inlineResult
            org.telegram.tgnet.TLRPC$Photo r10 = r10.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r10.sizes
            r9.<init>(r10)
            r0 = r9
            r15 = r0
            goto L_0x006f
        L_0x006e:
            r15 = r0
        L_0x006f:
            boolean r0 = r7.mediaWebpage
            r19 = 1082130432(0x40800000, float:4.0)
            r14 = 1065353216(0x3var_, float:1.0)
            r13 = 1
            if (r0 != 0) goto L_0x0194
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            if (r0 == 0) goto L_0x0194
            java.lang.String r0 = r0.title
            r12 = 32
            r11 = 10
            if (r0 == 0) goto L_0x00e0
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00d3 }
            org.telegram.tgnet.TLRPC$BotInlineResult r9 = r7.inlineResult     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r9 = r9.title     // Catch:{ Exception -> 0x00d3 }
            float r0 = r0.measureText(r9)     // Catch:{ Exception -> 0x00d3 }
            double r9 = (double) r0     // Catch:{ Exception -> 0x00d3 }
            double r9 = java.lang.Math.ceil(r9)     // Catch:{ Exception -> 0x00d3 }
            int r0 = (int) r9     // Catch:{ Exception -> 0x00d3 }
            org.telegram.tgnet.TLRPC$BotInlineResult r9 = r7.inlineResult     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r9 = r9.title     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r9 = r9.replace(r11, r12)     // Catch:{ Exception -> 0x00d3 }
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00d3 }
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()     // Catch:{ Exception -> 0x00d3 }
            r16 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x00d3 }
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r9, r10, r11, r1)     // Catch:{ Exception -> 0x00d3 }
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00d3 }
            int r11 = java.lang.Math.min(r0, r6)     // Catch:{ Exception -> 0x00d3 }
            float r11 = (float) r11     // Catch:{ Exception -> 0x00d3 }
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x00d3 }
            java.lang.CharSequence r21 = android.text.TextUtils.ellipsize(r9, r10, r11, r12)     // Catch:{ Exception -> 0x00d3 }
            android.text.StaticLayout r9 = new android.text.StaticLayout     // Catch:{ Exception -> 0x00d3 }
            android.text.TextPaint r22 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00d3 }
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x00d3 }
            int r23 = r6 + r10
            android.text.Layout$Alignment r24 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x00d3 }
            r25 = 1065353216(0x3var_, float:1.0)
            r26 = 0
            r27 = 0
            r20 = r9
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x00d3 }
            r7.titleLayout = r9     // Catch:{ Exception -> 0x00d3 }
            goto L_0x00d7
        L_0x00d3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00d7:
            org.telegram.ui.Components.LetterDrawable r0 = r7.letterDrawable
            org.telegram.tgnet.TLRPC$BotInlineResult r9 = r7.inlineResult
            java.lang.String r9 = r9.title
            r0.setTitle(r9)
        L_0x00e0:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x013d
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult     // Catch:{ Exception -> 0x0131 }
            java.lang.String r0 = r0.description     // Catch:{ Exception -> 0x0131 }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0131 }
            android.graphics.Paint$FontMetricsInt r9 = r9.getFontMetricsInt()     // Catch:{ Exception -> 0x0131 }
            r10 = 1095761920(0x41500000, float:13.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ Exception -> 0x0131 }
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r0, r9, r10, r1)     // Catch:{ Exception -> 0x0131 }
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0131 }
            r0 = 0
            r20 = 3
            r12 = 10
            r11 = r6
            r2 = 10
            r3 = 32
            r12 = r6
            r1 = 1
            r13 = r0
            r23 = 1065353216(0x3var_, float:1.0)
            r14 = r20
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x012f }
            r7.descriptionLayout = r0     // Catch:{ Exception -> 0x012f }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x012f }
            if (r0 <= 0) goto L_0x012e
            int r0 = r7.descriptionY     // Catch:{ Exception -> 0x012f }
            android.text.StaticLayout r9 = r7.descriptionLayout     // Catch:{ Exception -> 0x012f }
            int r10 = r9.getLineCount()     // Catch:{ Exception -> 0x012f }
            int r10 = r10 - r1
            int r9 = r9.getLineBottom(r10)     // Catch:{ Exception -> 0x012f }
            int r0 = r0 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r23)     // Catch:{ Exception -> 0x012f }
            int r0 = r0 + r9
            r7.linkY = r0     // Catch:{ Exception -> 0x012f }
        L_0x012e:
            goto L_0x0144
        L_0x012f:
            r0 = move-exception
            goto L_0x0139
        L_0x0131:
            r0 = move-exception
            r1 = 1
            r2 = 10
            r3 = 32
            r23 = 1065353216(0x3var_, float:1.0)
        L_0x0139:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0144
        L_0x013d:
            r1 = 1
            r2 = 10
            r3 = 32
            r23 = 1065353216(0x3var_, float:1.0)
        L_0x0144:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r7.inlineResult
            java.lang.String r0 = r0.url
            if (r0 == 0) goto L_0x0190
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0188 }
            org.telegram.tgnet.TLRPC$BotInlineResult r9 = r7.inlineResult     // Catch:{ Exception -> 0x0188 }
            java.lang.String r9 = r9.url     // Catch:{ Exception -> 0x0188 }
            float r0 = r0.measureText(r9)     // Catch:{ Exception -> 0x0188 }
            double r9 = (double) r0     // Catch:{ Exception -> 0x0188 }
            double r9 = java.lang.Math.ceil(r9)     // Catch:{ Exception -> 0x0188 }
            int r0 = (int) r9     // Catch:{ Exception -> 0x0188 }
            org.telegram.tgnet.TLRPC$BotInlineResult r9 = r7.inlineResult     // Catch:{ Exception -> 0x0188 }
            java.lang.String r9 = r9.url     // Catch:{ Exception -> 0x0188 }
            java.lang.String r2 = r9.replace(r2, r3)     // Catch:{ Exception -> 0x0188 }
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0188 }
            int r9 = java.lang.Math.min(r0, r6)     // Catch:{ Exception -> 0x0188 }
            float r9 = (float) r9     // Catch:{ Exception -> 0x0188 }
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0188 }
            java.lang.CharSequence r10 = android.text.TextUtils.ellipsize(r2, r3, r9, r10)     // Catch:{ Exception -> 0x0188 }
            android.text.StaticLayout r2 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0188 }
            android.text.TextPaint r11 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0188 }
            android.text.Layout$Alignment r13 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0188 }
            r14 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            r16 = 0
            r9 = r2
            r12 = r6
            r20 = r6
            r6 = r15
            r15 = r3
            r9.<init>(r10, r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x0186 }
            r7.linkLayout = r2     // Catch:{ Exception -> 0x0186 }
            goto L_0x019a
        L_0x0186:
            r0 = move-exception
            goto L_0x018c
        L_0x0188:
            r0 = move-exception
            r20 = r6
            r6 = r15
        L_0x018c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x019a
        L_0x0190:
            r20 = r6
            r6 = r15
            goto L_0x019a
        L_0x0194:
            r20 = r6
            r6 = r15
            r1 = 1
            r23 = 1065353216(0x3var_, float:1.0)
        L_0x019a:
            r0 = 0
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            r3 = 3
            r9 = 5
            r10 = 80
            if (r2 == 0) goto L_0x01eb
            boolean r11 = r7.isForceGif
            r12 = 90
            if (r11 != 0) goto L_0x01e0
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r2)
            if (r2 == 0) goto L_0x01b0
            goto L_0x01e0
        L_0x01b0:
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            boolean r2 = org.telegram.messenger.MessageObject.isStickerDocument(r2)
            if (r2 != 0) goto L_0x01d2
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            boolean r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, r1)
            if (r2 == 0) goto L_0x01c1
            goto L_0x01d2
        L_0x01c1:
            int r2 = r7.documentAttachType
            if (r2 == r9) goto L_0x020a
            if (r2 == r3) goto L_0x020a
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r12)
            r7.currentPhotoObject = r2
            goto L_0x020a
        L_0x01d2:
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r12)
            r7.currentPhotoObject = r2
            java.lang.String r0 = "webp"
            r11 = r4
            goto L_0x020b
        L_0x01e0:
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r12)
            r7.currentPhotoObject = r2
            goto L_0x020a
        L_0x01eb:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            if (r2 == 0) goto L_0x020a
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            if (r2 == 0) goto L_0x020a
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r2, r1)
            r7.currentPhotoObject = r2
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r10)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            if (r4 != r2) goto L_0x0208
            r4 = 0
            r11 = r4
            goto L_0x020b
        L_0x0208:
            r11 = r4
            goto L_0x020b
        L_0x020a:
            r11 = r4
        L_0x020b:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            r4 = 2
            if (r2 == 0) goto L_0x0302
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r2 == 0) goto L_0x0277
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            java.lang.String r2 = r2.type
            if (r2 == 0) goto L_0x0277
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            java.lang.String r2 = r2.type
            java.lang.String r12 = "gif"
            boolean r2 = r2.startsWith(r12)
            if (r2 == 0) goto L_0x0252
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r2 == 0) goto L_0x0247
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            java.lang.String r2 = r2.mime_type
            java.lang.String r12 = "video/mp4"
            boolean r2 = r12.equals(r2)
            if (r2 == 0) goto L_0x0247
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC.TL_webDocument) r2
            r17 = r2
            goto L_0x024f
        L_0x0247:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC.TL_webDocument) r2
            r17 = r2
        L_0x024f:
            r7.documentAttachType = r4
            goto L_0x0277
        L_0x0252:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            java.lang.String r2 = r2.type
            java.lang.String r12 = "photo"
            boolean r2 = r2.equals(r12)
            if (r2 == 0) goto L_0x0277
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r2 == 0) goto L_0x026f
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            r17 = r2
            org.telegram.tgnet.TLRPC$TL_webDocument r17 = (org.telegram.tgnet.TLRPC.TL_webDocument) r17
            goto L_0x0277
        L_0x026f:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            r17 = r2
            org.telegram.tgnet.TLRPC$TL_webDocument r17 = (org.telegram.tgnet.TLRPC.TL_webDocument) r17
        L_0x0277:
            if (r17 != 0) goto L_0x0289
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r2 == 0) goto L_0x0289
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            org.telegram.tgnet.TLRPC$TL_webDocument r2 = (org.telegram.tgnet.TLRPC.TL_webDocument) r2
            r17 = r2
        L_0x0289:
            if (r17 != 0) goto L_0x02f6
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            if (r2 != 0) goto L_0x02f6
            if (r11 != 0) goto L_0x02f6
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r2 = r2.send_message
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue
            if (r2 != 0) goto L_0x02a5
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r2 = r2.send_message
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo
            if (r2 == 0) goto L_0x02a2
            goto L_0x02a5
        L_0x02a2:
            r34 = r11
            goto L_0x02f8
        L_0x02a5:
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r2 = r2.send_message
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo
            double r12 = r2.lat
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r2 = r2.send_message
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo
            double r14 = r2._long
            int r2 = r7.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r2 = r2.mapProvider
            if (r2 != r4) goto L_0x02dd
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r7.inlineResult
            org.telegram.tgnet.TLRPC$BotInlineMessage r2 = r2.send_message
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo
            r9 = 15
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r34 = r11
            double r10 = (double) r3
            double r10 = java.lang.Math.ceil(r10)
            int r3 = (int) r10
            int r3 = java.lang.Math.min(r4, r3)
            r10 = 72
            org.telegram.messenger.WebFile r2 = org.telegram.messenger.WebFile.createWithGeoPoint(r2, r10, r10, r9, r3)
            r5 = r2
            goto L_0x02f8
        L_0x02dd:
            r34 = r11
            int r2 = r7.currentAccount
            r29 = 72
            r30 = 72
            r31 = 1
            r32 = 15
            r33 = -1
            r24 = r2
            r25 = r12
            r27 = r14
            java.lang.String r18 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r24, r25, r27, r29, r30, r31, r32, r33)
            goto L_0x02f8
        L_0x02f6:
            r34 = r11
        L_0x02f8:
            if (r17 == 0) goto L_0x0300
            org.telegram.messenger.WebFile r5 = org.telegram.messenger.WebFile.createWithWebDocument(r17)
            r9 = r5
            goto L_0x0305
        L_0x0300:
            r9 = r5
            goto L_0x0305
        L_0x0302:
            r34 = r11
            r9 = r5
        L_0x0305:
            r2 = 0
            r3 = 0
            org.telegram.tgnet.TLRPC$Document r5 = r7.documentAttach
            if (r5 == 0) goto L_0x0330
            r5 = 0
        L_0x030c:
            org.telegram.tgnet.TLRPC$Document r10 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r10 = r10.attributes
            int r10 = r10.size()
            if (r5 >= r10) goto L_0x0330
            org.telegram.tgnet.TLRPC$Document r10 = r7.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r10 = r10.attributes
            java.lang.Object r10 = r10.get(r5)
            org.telegram.tgnet.TLRPC$DocumentAttribute r10 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r10
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r11 != 0) goto L_0x032c
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo
            if (r11 == 0) goto L_0x0329
            goto L_0x032c
        L_0x0329:
            int r5 = r5 + 1
            goto L_0x030c
        L_0x032c:
            int r2 = r10.w
            int r3 = r10.h
        L_0x0330:
            if (r2 == 0) goto L_0x0338
            if (r3 != 0) goto L_0x0335
            goto L_0x0338
        L_0x0335:
            r10 = r34
            goto L_0x035e
        L_0x0338:
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r7.currentPhotoObject
            if (r5 == 0) goto L_0x034f
            if (r34 == 0) goto L_0x0344
            r5 = -1
            r10 = r34
            r10.size = r5
            goto L_0x0346
        L_0x0344:
            r10 = r34
        L_0x0346:
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r7.currentPhotoObject
            int r2 = r5.w
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r7.currentPhotoObject
            int r3 = r5.h
            goto L_0x035e
        L_0x034f:
            r10 = r34
            org.telegram.tgnet.TLRPC$BotInlineResult r5 = r7.inlineResult
            if (r5 == 0) goto L_0x035e
            int[] r5 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r5)
            r11 = 0
            r2 = r5[r11]
            r3 = r5[r1]
        L_0x035e:
            r5 = 1117782016(0x42a00000, float:80.0)
            if (r2 == 0) goto L_0x0368
            if (r3 != 0) goto L_0x0365
            goto L_0x0368
        L_0x0365:
            r11 = r2
            r12 = r3
            goto L_0x036f
        L_0x0368:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r3 = r11
            r2 = r11
            r12 = r3
        L_0x036f:
            org.telegram.tgnet.TLRPC$Document r2 = r7.documentAttach
            if (r2 != 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.currentPhotoObject
            if (r2 != 0) goto L_0x037b
            if (r9 != 0) goto L_0x037b
            if (r18 == 0) goto L_0x05b3
        L_0x037b:
            java.lang.String r2 = "52_52_b"
            boolean r3 = r7.mediaWebpage
            if (r3 == 0) goto L_0x03e1
            float r3 = (float) r11
            float r13 = (float) r12
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r13 = r13 / r5
            float r3 = r3 / r13
            int r3 = (int) r3
            int r5 = r7.documentAttachType
            if (r5 != r4) goto L_0x03b1
            java.util.Locale r5 = java.util.Locale.US
            java.lang.Object[] r13 = new java.lang.Object[r4]
            float r14 = (float) r3
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r14 = r14 / r15
            int r14 = (int) r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r15 = 0
            r13[r15] = r14
            r14 = 80
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r13[r1] = r14
            java.lang.String r14 = "%d_%d_b"
            java.lang.String r5 = java.lang.String.format(r5, r14, r13)
            r13 = r5
            r2 = r5
            r15 = 0
            goto L_0x03e4
        L_0x03b1:
            java.util.Locale r5 = java.util.Locale.US
            java.lang.Object[] r13 = new java.lang.Object[r4]
            float r14 = (float) r3
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r14 = r14 / r15
            int r14 = (int) r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r15 = 0
            r13[r15] = r14
            r14 = 80
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r13[r1] = r14
            java.lang.String r14 = "%d_%d"
            java.lang.String r13 = java.lang.String.format(r5, r14, r13)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r13)
            java.lang.String r14 = "_b"
            r5.append(r14)
            java.lang.String r2 = r5.toString()
            goto L_0x03e4
        L_0x03e1:
            r15 = 0
            java.lang.String r13 = "52_52"
        L_0x03e4:
            org.telegram.messenger.ImageReceiver r3 = r7.linkImageView
            int r5 = r7.documentAttachType
            r14 = 6
            if (r5 != r14) goto L_0x03ec
            r15 = 1
        L_0x03ec:
            r3.setAspectFit(r15)
            int r3 = r7.documentAttachType
            if (r3 != r4) goto L_0x049a
            org.telegram.tgnet.TLRPC$Document r3 = r7.documentAttach
            if (r3 == 0) goto L_0x0452
            org.telegram.tgnet.TLRPC$VideoSize r3 = org.telegram.messenger.MessageObject.getDocumentVideoThumb(r3)
            if (r3 == 0) goto L_0x0421
            org.telegram.messenger.ImageReceiver r5 = r7.linkImageView
            org.telegram.tgnet.TLRPC$Document r14 = r7.documentAttach
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.VideoSize) r3, (org.telegram.tgnet.TLRPC.Document) r14)
            org.telegram.tgnet.TLRPC$PhotoSize r14 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r15 = r7.documentAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r14, (org.telegram.tgnet.TLRPC.Document) r15)
            r29 = -1
            java.lang.Object r14 = r7.parentObject
            r32 = 1
            java.lang.String r26 = "100_100"
            r24 = r5
            r28 = r13
            r30 = r0
            r31 = r14
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x0450
        L_0x0421:
            org.telegram.tgnet.TLRPC$Document r5 = r7.documentAttach
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument(r5)
            boolean r14 = r7.isForceGif
            if (r14 == 0) goto L_0x042d
            r5.imageType = r4
        L_0x042d:
            org.telegram.messenger.ImageReceiver r14 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r15 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r4 = r7.documentAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r15, (org.telegram.tgnet.TLRPC.Document) r4)
            org.telegram.tgnet.TLRPC$Document r4 = r7.documentAttach
            int r4 = r4.size
            java.lang.Object r15 = r7.parentObject
            r32 = 0
            java.lang.String r26 = "100_100"
            r24 = r14
            r25 = r5
            r28 = r13
            r29 = r4
            r30 = r0
            r31 = r15
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
        L_0x0450:
            goto L_0x05b1
        L_0x0452:
            if (r9 == 0) goto L_0x0477
            org.telegram.messenger.ImageReceiver r3 = r7.linkImageView
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForWebFile(r9)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r5 = r7.photoAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r4, (org.telegram.tgnet.TLRPC.Photo) r5)
            r29 = -1
            java.lang.Object r4 = r7.parentObject
            r32 = 1
            java.lang.String r26 = "100_100"
            r24 = r3
            r28 = r13
            r30 = r0
            r31 = r4
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x05b1
        L_0x0477:
            org.telegram.messenger.ImageReceiver r3 = r7.linkImageView
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForPath(r18)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r5 = r7.photoAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r4, (org.telegram.tgnet.TLRPC.Photo) r5)
            r29 = -1
            java.lang.Object r4 = r7.parentObject
            r32 = 1
            java.lang.String r26 = "100_100"
            r24 = r3
            r28 = r13
            r30 = r0
            r31 = r4
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x05b1
        L_0x049a:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r7.currentPhotoObject
            if (r3 == 0) goto L_0x0570
            org.telegram.tgnet.TLRPC$Document r3 = r7.documentAttach
            java.lang.String r4 = "windowBackgroundGray"
            r5 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.SvgHelper$SvgDrawable r3 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC.Document) r3, (java.lang.String) r4, (float) r5)
            org.telegram.tgnet.TLRPC$Document r4 = r7.documentAttach
            boolean r4 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r4)
            if (r4 == 0) goto L_0x04fc
            if (r3 == 0) goto L_0x04d3
            org.telegram.messenger.ImageReceiver r4 = r7.linkImageView
            org.telegram.tgnet.TLRPC$Document r5 = r7.documentAttach
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForDocument(r5)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r7.currentPhotoObject
            int r5 = r5.size
            java.lang.Object r14 = r7.parentObject
            r31 = 0
            java.lang.String r26 = "80_80"
            r24 = r4
            r27 = r3
            r28 = r5
            r29 = r0
            r30 = r14
            r24.setImage((org.telegram.messenger.ImageLocation) r25, (java.lang.String) r26, (android.graphics.drawable.Drawable) r27, (int) r28, (java.lang.String) r29, (java.lang.Object) r30, (int) r31)
            goto L_0x056f
        L_0x04d3:
            org.telegram.messenger.ImageReceiver r4 = r7.linkImageView
            org.telegram.tgnet.TLRPC$Document r5 = r7.documentAttach
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForDocument(r5)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r14 = r7.documentAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r5, (org.telegram.tgnet.TLRPC.Document) r14)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r7.currentPhotoObject
            int r5 = r5.size
            java.lang.Object r14 = r7.parentObject
            r32 = 0
            java.lang.String r26 = "80_80"
            r24 = r4
            r28 = r2
            r29 = r5
            r30 = r0
            r31 = r14
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x056f
        L_0x04fc:
            org.telegram.tgnet.TLRPC$Document r4 = r7.documentAttach
            if (r4 == 0) goto L_0x0548
            if (r3 == 0) goto L_0x0522
            org.telegram.messenger.ImageReceiver r5 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r14 = r7.currentPhotoObject
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r14, (org.telegram.tgnet.TLRPC.Document) r4)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r7.currentPhotoObject
            int r4 = r4.size
            java.lang.Object r14 = r7.parentObject
            r31 = 0
            r24 = r5
            r26 = r13
            r27 = r3
            r28 = r4
            r29 = r0
            r30 = r14
            r24.setImage((org.telegram.messenger.ImageLocation) r25, (java.lang.String) r26, (android.graphics.drawable.Drawable) r27, (int) r28, (java.lang.String) r29, (java.lang.Object) r30, (int) r31)
            goto L_0x056f
        L_0x0522:
            org.telegram.messenger.ImageReceiver r5 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r14 = r7.currentPhotoObject
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r14, (org.telegram.tgnet.TLRPC.Document) r4)
            org.telegram.tgnet.TLRPC$Photo r4 = r7.photoAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r10, (org.telegram.tgnet.TLRPC.Photo) r4)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r7.currentPhotoObject
            int r4 = r4.size
            java.lang.Object r14 = r7.parentObject
            r32 = 0
            r24 = r5
            r26 = r13
            r28 = r2
            r29 = r4
            r30 = r0
            r31 = r14
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x056f
        L_0x0548:
            org.telegram.messenger.ImageReceiver r4 = r7.linkImageView
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r7.currentPhotoObject
            org.telegram.tgnet.TLRPC$Photo r14 = r7.photoAttach
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r5, (org.telegram.tgnet.TLRPC.Photo) r14)
            org.telegram.tgnet.TLRPC$Photo r5 = r7.photoAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r10, (org.telegram.tgnet.TLRPC.Photo) r5)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r7.currentPhotoObject
            int r5 = r5.size
            java.lang.Object r14 = r7.parentObject
            r32 = 0
            r24 = r4
            r26 = r13
            r28 = r2
            r29 = r5
            r30 = r0
            r31 = r14
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
        L_0x056f:
            goto L_0x05b1
        L_0x0570:
            if (r9 == 0) goto L_0x0592
            org.telegram.messenger.ImageReceiver r3 = r7.linkImageView
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForWebFile(r9)
            org.telegram.tgnet.TLRPC$Photo r4 = r7.photoAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r10, (org.telegram.tgnet.TLRPC.Photo) r4)
            r29 = -1
            java.lang.Object r4 = r7.parentObject
            r32 = 1
            r24 = r3
            r26 = r13
            r28 = r2
            r30 = r0
            r31 = r4
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x05b1
        L_0x0592:
            org.telegram.messenger.ImageReceiver r3 = r7.linkImageView
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForPath(r18)
            org.telegram.tgnet.TLRPC$Photo r4 = r7.photoAttach
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r10, (org.telegram.tgnet.TLRPC.Photo) r4)
            r29 = -1
            java.lang.Object r4 = r7.parentObject
            r32 = 1
            r24 = r3
            r26 = r13
            r28 = r2
            r30 = r0
            r31 = r4
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
        L_0x05b1:
            r7.drawLinkImageView = r1
        L_0x05b3:
            boolean r2 = r7.mediaWebpage
            r3 = 1094713344(0x41400000, float:12.0)
            r4 = 1103101952(0x41CLASSNAME, float:24.0)
            if (r2 == 0) goto L_0x05fc
            r1 = r8
            int r2 = android.view.View.MeasureSpec.getSize(r37)
            if (r2 != 0) goto L_0x05c8
            r5 = 1120403456(0x42CLASSNAME, float:100.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
        L_0x05c8:
            r7.setMeasuredDimension(r1, r2)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r1 - r5
            r13 = 2
            int r5 = r5 / r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r14 = r2 - r14
            int r14 = r14 / r13
            org.telegram.ui.Components.RadialProgress2 r13 = r7.radialProgress
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r15 = r15 + r5
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r14
            r13.setProgressRect(r5, r14, r15, r4)
            org.telegram.ui.Components.RadialProgress2 r4 = r7.radialProgress
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.setCircleRadius(r3)
            org.telegram.messenger.ImageReceiver r3 = r7.linkImageView
            float r4 = (float) r1
            float r13 = (float) r2
            r15 = 0
            r3.setImageCoords(r15, r15, r4, r13)
            goto L_0x06cd
        L_0x05fc:
            r2 = 0
            android.text.StaticLayout r5 = r7.titleLayout
            if (r5 == 0) goto L_0x0613
            int r5 = r5.getLineCount()
            if (r5 == 0) goto L_0x0613
            android.text.StaticLayout r5 = r7.titleLayout
            int r13 = r5.getLineCount()
            int r13 = r13 - r1
            int r5 = r5.getLineBottom(r13)
            int r2 = r2 + r5
        L_0x0613:
            android.text.StaticLayout r5 = r7.descriptionLayout
            if (r5 == 0) goto L_0x0629
            int r5 = r5.getLineCount()
            if (r5 == 0) goto L_0x0629
            android.text.StaticLayout r5 = r7.descriptionLayout
            int r13 = r5.getLineCount()
            int r13 = r13 - r1
            int r5 = r5.getLineBottom(r13)
            int r2 = r2 + r5
        L_0x0629:
            android.text.StaticLayout r5 = r7.linkLayout
            if (r5 == 0) goto L_0x063f
            int r5 = r5.getLineCount()
            if (r5 <= 0) goto L_0x063f
            android.text.StaticLayout r5 = r7.linkLayout
            int r13 = r5.getLineCount()
            int r13 = r13 - r1
            int r1 = r5.getLineBottom(r13)
            int r2 = r2 + r1
        L_0x063f:
            r1 = 1112539136(0x42500000, float:52.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = java.lang.Math.max(r5, r2)
            int r5 = android.view.View.MeasureSpec.getSize(r36)
            r13 = 1116209152(0x42880000, float:68.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r14 = 1098907648(0x41800000, float:16.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = r14 + r2
            int r13 = java.lang.Math.max(r13, r14)
            boolean r14 = r7.needDivider
            int r13 = r13 + r14
            r7.setMeasuredDimension(r5, r13)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0679
            int r5 = android.view.View.MeasureSpec.getSize(r36)
            r13 = 1090519040(0x41000000, float:8.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r5 = r5 - r14
            int r5 = r5 - r1
            goto L_0x067f
        L_0x0679:
            r13 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
        L_0x067f:
            org.telegram.ui.Components.LetterDrawable r14 = r7.letterDrawable
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r3 = r5 + r1
            r21 = 1114636288(0x42700000, float:60.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r14.setBounds(r5, r15, r3, r4)
            org.telegram.messenger.ImageReceiver r3 = r7.linkImageView
            float r4 = (float) r5
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = (float) r1
            float r15 = (float) r1
            r3.setImageCoords(r4, r13, r14, r15)
            int r3 = r7.documentAttachType
            r4 = 3
            if (r3 == r4) goto L_0x06a5
            r4 = 5
            if (r3 != r4) goto L_0x06cd
        L_0x06a5:
            org.telegram.ui.Components.RadialProgress2 r3 = r7.radialProgress
            r4 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setCircleRadius(r4)
            org.telegram.ui.Components.RadialProgress2 r3 = r7.radialProgress
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r4 = r4 + r5
            r13 = 1094713344(0x41400000, float:12.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r14 = 1111490560(0x42400000, float:48.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = r14 + r5
            r15 = 1113587712(0x42600000, float:56.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r3.setProgressRect(r4, r13, r14, r15)
        L_0x06cd:
            org.telegram.ui.Components.CheckBox2 r2 = r7.checkBox
            if (r2 == 0) goto L_0x06e1
            r4 = 0
            r13 = 0
            r1 = r35
            r3 = r36
            r5 = r37
            r15 = r6
            r14 = r20
            r6 = r13
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
            goto L_0x06e4
        L_0x06e1:
            r15 = r6
            r14 = r20
        L_0x06e4:
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
                } else if (this.inlineResult.type.equals("audio")) {
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
            TLRPC.TL_message message = new TLRPC.TL_message();
            message.out = true;
            message.id = -Utilities.random.nextInt();
            message.peer_id = new TLRPC.TL_peerUser();
            message.from_id = new TLRPC.TL_peerUser();
            TLRPC.Peer peer = message.peer_id;
            TLRPC.Peer peer2 = message.from_id;
            long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            peer2.user_id = clientUserId;
            peer.user_id = clientUserId;
            message.date = (int) (System.currentTimeMillis() / 1000);
            String str2 = "";
            message.message = str2;
            message.media = new TLRPC.TL_messageMediaDocument();
            message.media.flags |= 3;
            message.media.document = new TLRPC.TL_document();
            message.media.document.file_reference = new byte[0];
            message.flags |= 768;
            if (this.documentAttach != null) {
                message.media.document = this.documentAttach;
                message.attachPath = str2;
            } else {
                String str3 = "mp3";
                String ext = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? str3 : "ogg");
                message.media.document.id = 0;
                message.media.document.access_hash = 0;
                message.media.document.date = message.date;
                message.media.document.mime_type = "audio/" + ext;
                message.media.document.size = 0;
                message.media.document.dc_id = 0;
                TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
                attributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                attributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : str2;
                if (this.inlineResult.description != null) {
                    str2 = this.inlineResult.description;
                }
                attributeAudio.performer = str2;
                attributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    attributeAudio.voice = true;
                }
                message.media.document.attributes.add(attributeAudio);
                TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                StringBuilder sb = new StringBuilder();
                sb.append(Utilities.MD5(this.inlineResult.content.url));
                sb.append(".");
                String str4 = this.inlineResult.content.url;
                if (this.documentAttachType == 5) {
                    str = str3;
                } else {
                    str = "ogg";
                }
                sb.append(ImageLoader.getHttpUrlExtension(str4, str));
                fileName.file_name = sb.toString();
                message.media.document.attributes.add(fileName);
                File directory = FileLoader.getDirectory(4);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(Utilities.MD5(this.inlineResult.content.url));
                sb2.append(".");
                String str5 = this.inlineResult.content.url;
                if (this.documentAttachType != 5) {
                    str3 = "ogg";
                }
                sb2.append(ImageLoader.getHttpUrlExtension(str5, str3));
                message.attachPath = new File(directory, sb2.toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, message, false, true);
        }
    }

    public void setLink(TLRPC.BotInlineResult contextResult, TLRPC.User bot, boolean media, boolean divider, boolean shadow) {
        setLink(contextResult, bot, media, divider, shadow, false);
    }

    public void setLink(TLRPC.BotInlineResult contextResult, TLRPC.User bot, boolean media, boolean divider, boolean shadow, boolean forceGif) {
        this.needDivider = divider;
        this.needShadow = shadow;
        this.inlineBot = bot;
        this.inlineResult = contextResult;
        this.parentObject = contextResult;
        if (contextResult != null) {
            this.documentAttach = contextResult.document;
            this.photoAttach = this.inlineResult.photo;
        } else {
            this.documentAttach = null;
            this.photoAttach = null;
        }
        this.mediaWebpage = media;
        this.isForceGif = forceGif;
        setAttachType();
        if (forceGif) {
            this.documentAttachType = 2;
        }
        requestLayout();
        updateButtonState(false, false);
    }

    public TLRPC.User getInlineBot() {
        return this.inlineBot;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public void setGif(TLRPC.Document document, boolean divider) {
        setGif(document, "gif" + document, 0, divider);
    }

    public void setGif(TLRPC.Document document, Object parent, int date, boolean divider) {
        this.needDivider = divider;
        this.needShadow = false;
        this.currentDate = date;
        this.inlineResult = null;
        this.parentObject = parent;
        this.documentAttach = document;
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

    public TLRPC.Document getDocument() {
        return this.documentAttach;
    }

    public TLRPC.BotInlineResult getBotInlineResult() {
        return this.inlineResult;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setCanPreviewGif(boolean value) {
        this.canPreviewGif = value;
    }

    public boolean isCanPreviewGif() {
        return this.canPreviewGif;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.linkImageView.onDetachedFromWindow();
        this.radialProgress.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false, false);
        }
        this.radialProgress.onAttachedToWindow();
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        int dp = AndroidUtilities.dp(48.0f);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            boolean area = this.letterDrawable.getBounds().contains(x, y);
            if (event.getAction() == 0) {
                if (area) {
                    this.buttonPressed = true;
                    this.radialProgress.setPressed(true, false);
                    invalidate();
                    result = true;
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    didPressedButton();
                    invalidate();
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                    invalidate();
                } else if (event.getAction() == 2 && !area) {
                    this.buttonPressed = false;
                    invalidate();
                }
                this.radialProgress.setPressed(this.buttonPressed, false);
            }
        } else {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (!(botInlineResult == null || botInlineResult.content == null || TextUtils.isEmpty(this.inlineResult.content.url))) {
                if (event.getAction() == 0) {
                    if (this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = true;
                        result = true;
                    }
                } else if (this.buttonPressed) {
                    if (event.getAction() == 1) {
                        this.buttonPressed = false;
                        playSoundEffect(0);
                        this.delegate.didPressedImage(this);
                    } else if (event.getAction() == 3) {
                        this.buttonPressed = false;
                    } else if (event.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = false;
                    }
                }
            }
        }
        if (!result) {
            return super.onTouchEvent(event);
        }
        return result;
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
                if (MediaController.getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.currentMessageObject)) {
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
                TLRPC.BotInlineResult botInlineResult = this.inlineResult;
                if (botInlineResult == null || !botInlineResult.type.equals("file")) {
                    TLRPC.BotInlineResult botInlineResult2 = this.inlineResult;
                    if (botInlineResult2 == null || (!botInlineResult2.type.equals("audio") && !this.inlineResult.type.equals("voice"))) {
                        TLRPC.BotInlineResult botInlineResult3 = this.inlineResult;
                        if (botInlineResult3 == null || (!botInlineResult3.type.equals("venue") && !this.inlineResult.type.equals("geo"))) {
                            this.letterDrawable.draw(canvas);
                        } else {
                            int w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                            int h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                            int x = (int) (this.linkImageView.getImageX() + ((float) ((AndroidUtilities.dp(52.0f) - w) / 2)));
                            int y = (int) (this.linkImageView.getImageY() + ((float) ((AndroidUtilities.dp(52.0f) - h) / 2)));
                            canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + ((float) AndroidUtilities.dp(52.0f)), this.linkImageView.getImageY() + ((float) AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                            Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
                            Theme.chat_inlineResultLocation.draw(canvas);
                        }
                    } else {
                        int w2 = Theme.chat_inlineResultAudio.getIntrinsicWidth();
                        int h2 = Theme.chat_inlineResultAudio.getIntrinsicHeight();
                        int x2 = (int) (this.linkImageView.getImageX() + ((float) ((AndroidUtilities.dp(52.0f) - w2) / 2)));
                        int y2 = (int) (this.linkImageView.getImageY() + ((float) ((AndroidUtilities.dp(52.0f) - h2) / 2)));
                        canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + ((float) AndroidUtilities.dp(52.0f)), this.linkImageView.getImageY() + ((float) AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultAudio.setBounds(x2, y2, x2 + w2, y2 + h2);
                        Theme.chat_inlineResultAudio.draw(canvas);
                    }
                } else {
                    int w3 = Theme.chat_inlineResultFile.getIntrinsicWidth();
                    int h3 = Theme.chat_inlineResultFile.getIntrinsicHeight();
                    int x3 = (int) (this.linkImageView.getImageX() + ((float) ((AndroidUtilities.dp(52.0f) - w3) / 2)));
                    int y3 = (int) (this.linkImageView.getImageY() + ((float) ((AndroidUtilities.dp(52.0f) - h3) / 2)));
                    canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + ((float) AndroidUtilities.dp(52.0f)), this.linkImageView.getImageY() + ((float) AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                    Theme.chat_inlineResultFile.setBounds(x3, y3, x3 + w3, y3 + h3);
                    Theme.chat_inlineResultFile.draw(canvas);
                }
            }
        } else {
            TLRPC.BotInlineResult botInlineResult4 = this.inlineResult;
            if (botInlineResult4 != null && ((botInlineResult4.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))) {
                int w4 = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                int h4 = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                int x4 = (int) (this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - ((float) w4)) / 2.0f));
                int y4 = (int) (this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - ((float) h4)) / 2.0f));
                canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + this.linkImageView.getImageWidth(), this.linkImageView.getImageY() + this.linkImageView.getImageHeight(), LetterDrawable.paint);
                Theme.chat_inlineResultLocation.setBounds(x4, y4, x4 + w4, y4 + h4);
                Theme.chat_inlineResultLocation.draw(canvas);
            }
        }
        if (this.drawLinkImageView != 0) {
            TLRPC.BotInlineResult botInlineResult5 = this.inlineResult;
            if (botInlineResult5 != null) {
                this.linkImageView.setVisible(!PhotoViewer.isShowingImage(botInlineResult5), false);
            }
            canvas.save();
            boolean z = this.scaled;
            if ((z && this.scale != 0.8f) || (!z && this.scale != 1.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                if (this.scaled) {
                    float f2 = this.scale;
                    if (f2 != 0.8f) {
                        float f3 = f2 - (((float) dt) / 400.0f);
                        this.scale = f3;
                        if (f3 < 0.8f) {
                            this.scale = 0.8f;
                        }
                        invalidate();
                    }
                }
                float f4 = this.scale + (((float) dt) / 400.0f);
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

    public void updateButtonState(boolean ifSame, boolean animated) {
        boolean isLoading;
        String fileName = null;
        File cacheFile = null;
        int i = this.documentAttachType;
        if (i == 5 || i == 3) {
            TLRPC.Document document = this.documentAttach;
            if (document != null) {
                fileName = FileLoader.getAttachFileName(document);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                StringBuilder sb = new StringBuilder();
                sb.append(Utilities.MD5(this.inlineResult.content.url));
                sb.append(".");
                sb.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg"));
                fileName = sb.toString();
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
            }
        } else if (this.mediaWebpage) {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (botInlineResult == null) {
                TLRPC.Document document2 = this.documentAttach;
                if (document2 != null) {
                    fileName = FileLoader.getAttachFileName(document2);
                    cacheFile = FileLoader.getPathToAttach(this.documentAttach);
                }
            } else if (botInlineResult.document instanceof TLRPC.TL_document) {
                fileName = FileLoader.getAttachFileName(this.inlineResult.document);
                cacheFile = FileLoader.getPathToAttach(this.inlineResult.document);
            } else if (this.inlineResult.photo instanceof TLRPC.TL_photo) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                this.currentPhotoObject = closestPhotoSizeWithSize;
                fileName = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                cacheFile = FileLoader.getPathToAttach(this.currentPhotoObject);
            } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                fileName = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, FileLoader.getMimeTypePart(this.inlineResult.content.mime_type));
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
                if (this.documentAttachType == 2 && (this.inlineResult.thumb instanceof TLRPC.TL_webDocument) && "video/mp4".equals(this.inlineResult.thumb.mime_type)) {
                    fileName = null;
                }
            } else if (this.inlineResult.thumb instanceof TLRPC.TL_webDocument) {
                fileName = Utilities.MD5(this.inlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb.url, FileLoader.getMimeTypePart(this.inlineResult.thumb.mime_type));
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
            }
            TLRPC.Document document3 = this.documentAttach;
            if (!(document3 == null || this.documentAttachType != 2 || MessageObject.getDocumentVideoThumb(document3) == null)) {
                fileName = null;
            }
        }
        if (TextUtils.isEmpty(fileName)) {
            this.buttonState = -1;
            this.radialProgress.setIcon(4, ifSame, false);
            return;
        }
        if (!cacheFile.exists()) {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            int i2 = this.documentAttachType;
            float setProgress = 0.0f;
            if (i2 == 5 || i2 == 3) {
                if (this.documentAttach != null) {
                    isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                } else {
                    isLoading = ImageLoader.getInstance().isLoadingHttpFile(fileName);
                }
                if (!isLoading) {
                    this.buttonState = 2;
                } else {
                    this.buttonState = 4;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                }
            } else {
                this.buttonState = 1;
                Float progress2 = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress2 != null) {
                    setProgress = progress2.floatValue();
                }
                this.radialProgress.setProgress(setProgress, false);
            }
        } else {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            int i3 = this.documentAttachType;
            if (i3 == 5 || i3 == 3) {
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, animated);
            } else {
                this.buttonState = -1;
            }
        }
        this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
        invalidate();
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public TLRPC.BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, true);
        }
    }

    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sbuf = new StringBuilder();
        switch (this.documentAttachType) {
            case 1:
                sbuf.append(LocaleController.getString("AttachDocument", NUM));
                break;
            case 2:
                sbuf.append(LocaleController.getString("AttachGif", NUM));
                break;
            case 3:
                sbuf.append(LocaleController.getString("AttachAudio", NUM));
                break;
            case 4:
                sbuf.append(LocaleController.getString("AttachVideo", NUM));
                break;
            case 5:
                sbuf.append(LocaleController.getString("AttachMusic", NUM));
                break;
            case 6:
                sbuf.append(LocaleController.getString("AttachSticker", NUM));
                break;
            case 7:
                sbuf.append(LocaleController.getString("AttachPhoto", NUM));
                break;
            case 8:
                sbuf.append(LocaleController.getString("AttachLocation", NUM));
                break;
        }
        StaticLayout staticLayout = this.titleLayout;
        boolean hasTitle = staticLayout != null && !TextUtils.isEmpty(staticLayout.getText());
        StaticLayout staticLayout2 = this.descriptionLayout;
        boolean hasDescription = staticLayout2 != null && !TextUtils.isEmpty(staticLayout2.getText());
        if (this.documentAttachType != 5 || !hasTitle || !hasDescription) {
            if (hasTitle) {
                if (sbuf.length() > 0) {
                    sbuf.append(", ");
                }
                sbuf.append(this.titleLayout.getText());
            }
            if (hasDescription) {
                if (sbuf.length() > 0) {
                    sbuf.append(", ");
                }
                sbuf.append(this.descriptionLayout.getText());
            }
        } else {
            sbuf.append(", ");
            sbuf.append(LocaleController.formatString("AccDescrMusicInfo", NUM, this.descriptionLayout.getText(), this.titleLayout.getText()));
        }
        info.setText(sbuf);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            info.setCheckable(true);
            info.setChecked(true);
        }
    }

    public void setChecked(final boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            if (checkBox2.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                Property<ContextLinkCell, Float> property = this.IMAGE_SCALE;
                float[] fArr = new float[1];
                if (checked) {
                    f = 0.81f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ContextLinkCell.this.animator != null && ContextLinkCell.this.animator.equals(animation)) {
                            AnimatorSet unused = ContextLinkCell.this.animator = null;
                            if (!checked) {
                                ContextLinkCell.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ContextLinkCell.this.animator != null && ContextLinkCell.this.animator.equals(animation)) {
                            AnimatorSet unused = ContextLinkCell.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            if (checked) {
                f = 0.85f;
            }
            this.imageScale = f;
            invalidate();
        }
    }
}
