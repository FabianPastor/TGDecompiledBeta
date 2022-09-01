package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress2;

public class SharedAudioCell extends FrameLayout implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private StaticLayout captionLayout;
    private TextPaint captionTextPaint;
    private int captionY;
    private CheckBox2 checkBox;
    private boolean checkForButtonPress;
    private int currentAccount;
    private MessageObject currentMessageObject;
    private StaticLayout dateLayout;
    private int dateLayoutX;
    private TextPaint description2TextPaint;
    private StaticLayout descriptionLayout;
    private int descriptionY;
    private SpannableStringBuilder dotSpan;
    float enterAlpha;
    FlickerLoadingView globalGradientView;
    private int hasMiniProgress;
    private boolean miniButtonPressed;
    private int miniButtonState;
    private boolean needDivider;
    private RadialProgress2 radialProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    boolean showReorderIcon;
    float showReorderIconProgress;
    private StaticLayout titleLayout;
    private int titleY;
    private int viewType;

    /* access modifiers changed from: protected */
    public boolean needPlayMessage(MessageObject messageObject) {
        return false;
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public SharedAudioCell(Context context) {
        this(context, 0, (Theme.ResourcesProvider) null);
    }

    public SharedAudioCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, 0, resourcesProvider2);
    }

    public SharedAudioCell(Context context, int i, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.titleY = AndroidUtilities.dp(9.0f);
        this.descriptionY = AndroidUtilities.dp(29.0f);
        this.captionY = AndroidUtilities.dp(29.0f);
        this.currentAccount = UserConfig.selectedAccount;
        this.enterAlpha = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        this.viewType = i;
        setFocusable(true);
        setImportantForAccessibility(1);
        RadialProgress2 radialProgress2 = new RadialProgress2(this, resourcesProvider2);
        this.radialProgress = radialProgress2;
        radialProgress2.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setWillNotDraw(false);
        CheckBox2 checkBox2 = new CheckBox2(context, 22, resourcesProvider2);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        int i2 = 3;
        this.checkBox.setDrawBackgroundAsArc(3);
        CheckBox2 checkBox22 = this.checkBox;
        boolean z = LocaleController.isRTL;
        addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (z ? 5 : i2) | 48, z ? 0.0f : 38.1f, 32.1f, z ? 6.0f : 0.0f, 0.0f));
        if (i == 1) {
            TextPaint textPaint = new TextPaint(1);
            this.description2TextPaint = textPaint;
            textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(".");
            this.dotSpan = spannableStringBuilder;
            spannableStringBuilder.setSpan(new DotDividerSpan(), 0, 1, 0);
        }
        TextPaint textPaint2 = new TextPaint(1);
        this.captionTextPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01ea  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01ff  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x020a  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0254  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r23, int r24) {
        /*
            r22 = this;
            r7 = r22
            r0 = 0
            r7.descriptionLayout = r0
            r7.titleLayout = r0
            r7.captionLayout = r0
            int r0 = android.view.View.MeasureSpec.getSize(r23)
            int r1 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r1 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            r1 = 1105199104(0x41e00000, float:28.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0 - r1
            int r0 = r7.viewType
            r2 = 1101004800(0x41a00000, float:20.0)
            r3 = 1090519040(0x41000000, float:8.0)
            r4 = 0
            r5 = 1
            if (r0 != r5) goto L_0x0060
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            int r0 = r0.date
            long r8 = (long) r0
            java.lang.String r10 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            android.text.TextPaint r0 = r7.description2TextPaint
            float r0 = r0.measureText(r10)
            double r8 = (double) r0
            double r8 = java.lang.Math.ceil(r8)
            int r0 = (int) r8
            android.text.TextPaint r11 = r7.description2TextPaint
            r14 = 0
            r15 = 1
            r12 = r0
            r13 = r0
            android.text.StaticLayout r6 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r10, r11, r12, r13, r14, r15)
            r7.dateLayout = r6
            int r6 = r1 - r0
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = r6 - r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = r6 + r8
            r7.dateLayoutX = r6
            r6 = 1094713344(0x41400000, float:12.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 + r6
            goto L_0x0061
        L_0x0060:
            r0 = 0
        L_0x0061:
            r6 = 10
            r8 = 32
            r9 = 1082130432(0x40800000, float:4.0)
            int r10 = r7.viewType     // Catch:{ Exception -> 0x00be }
            if (r10 != r5) goto L_0x0082
            org.telegram.messenger.MessageObject r10 = r7.currentMessageObject     // Catch:{ Exception -> 0x00be }
            boolean r10 = r10.isVoice()     // Catch:{ Exception -> 0x00be }
            if (r10 != 0) goto L_0x007b
            org.telegram.messenger.MessageObject r10 = r7.currentMessageObject     // Catch:{ Exception -> 0x00be }
            boolean r10 = r10.isRoundVideo()     // Catch:{ Exception -> 0x00be }
            if (r10 == 0) goto L_0x0082
        L_0x007b:
            org.telegram.messenger.MessageObject r10 = r7.currentMessageObject     // Catch:{ Exception -> 0x00be }
            java.lang.CharSequence r10 = org.telegram.ui.FilteredSearchView.createFromInfoString(r10)     // Catch:{ Exception -> 0x00be }
            goto L_0x008c
        L_0x0082:
            org.telegram.messenger.MessageObject r10 = r7.currentMessageObject     // Catch:{ Exception -> 0x00be }
            java.lang.String r10 = r10.getMusicTitle()     // Catch:{ Exception -> 0x00be }
            java.lang.String r10 = r10.replace(r6, r8)     // Catch:{ Exception -> 0x00be }
        L_0x008c:
            org.telegram.messenger.MessageObject r11 = r7.currentMessageObject     // Catch:{ Exception -> 0x00be }
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords     // Catch:{ Exception -> 0x00be }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r7.resourcesProvider     // Catch:{ Exception -> 0x00be }
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r10, (java.util.ArrayList<java.lang.String>) r11, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)     // Catch:{ Exception -> 0x00be }
            if (r11 == 0) goto L_0x0099
            r10 = r11
        L_0x0099:
            android.text.TextPaint r11 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00be }
            int r12 = r1 - r0
            float r12 = (float) r12     // Catch:{ Exception -> 0x00be }
            android.text.TextUtils$TruncateAt r13 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x00be }
            java.lang.CharSequence r15 = android.text.TextUtils.ellipsize(r10, r11, r12, r13)     // Catch:{ Exception -> 0x00be }
            android.text.StaticLayout r10 = new android.text.StaticLayout     // Catch:{ Exception -> 0x00be }
            android.text.TextPaint r16 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00be }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x00be }
            int r11 = r11 + r1
            int r17 = r11 - r0
            android.text.Layout$Alignment r18 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x00be }
            r19 = 1065353216(0x3var_, float:1.0)
            r20 = 0
            r21 = 0
            r14 = r10
            r14.<init>(r15, r16, r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x00be }
            r7.titleLayout = r10     // Catch:{ Exception -> 0x00be }
            goto L_0x00c2
        L_0x00be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c2:
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x012f
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            java.lang.String r10 = "\n"
            java.lang.String r11 = " "
            java.lang.String r0 = r0.replace(r10, r11)
            java.lang.String r10 = " +"
            java.lang.String r0 = r0.replaceAll(r10, r11)
            java.lang.String r0 = r0.trim()
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r10, r2, r4)
            org.telegram.messenger.MessageObject r2 = r7.currentMessageObject
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            if (r0 == 0) goto L_0x012f
            org.telegram.messenger.MessageObject r2 = r7.currentMessageObject
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            java.lang.Object r2 = r2.get(r4)
            java.lang.String r2 = (java.lang.String) r2
            android.text.TextPaint r10 = r7.captionTextPaint
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r2, r1, r10, r11)
            android.text.TextPaint r2 = r7.captionTextPaint
            float r10 = (float) r1
            android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r13 = android.text.TextUtils.ellipsize(r0, r2, r10, r11)
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r14 = r7.captionTextPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r15 = r1 + r2
            android.text.Layout$Alignment r16 = android.text.Layout.Alignment.ALIGN_NORMAL
            r17 = 1065353216(0x3var_, float:1.0)
            r18 = 0
            r19 = 0
            r12 = r0
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)
            r7.captionLayout = r0
        L_0x012f:
            int r0 = r7.viewType     // Catch:{ Exception -> 0x01d5 }
            if (r0 != r5) goto L_0x0175
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x01d5 }
            boolean r0 = r0.isVoice()     // Catch:{ Exception -> 0x01d5 }
            if (r0 != 0) goto L_0x0143
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x01d5 }
            boolean r0 = r0.isRoundVideo()     // Catch:{ Exception -> 0x01d5 }
            if (r0 == 0) goto L_0x0175
        L_0x0143:
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x01d5 }
            int r0 = r0.getDuration()     // Catch:{ Exception -> 0x01d5 }
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.formatDuration(r0, r4)     // Catch:{ Exception -> 0x01d5 }
            int r2 = r7.viewType     // Catch:{ Exception -> 0x01d5 }
            if (r2 != r5) goto L_0x0154
            android.text.TextPaint r2 = r7.description2TextPaint     // Catch:{ Exception -> 0x01d5 }
            goto L_0x0156
        L_0x0154:
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x01d5 }
        L_0x0156:
            r12 = r2
            float r2 = (float) r1     // Catch:{ Exception -> 0x01d5 }
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x01d5 }
            java.lang.CharSequence r11 = android.text.TextUtils.ellipsize(r0, r12, r2, r5)     // Catch:{ Exception -> 0x01d5 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x01d5 }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x01d5 }
            int r13 = r1 + r2
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x01d5 }
            r15 = 1065353216(0x3var_, float:1.0)
            r16 = 0
            r17 = 0
            r10 = r0
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x01d5 }
            r7.descriptionLayout = r0     // Catch:{ Exception -> 0x01d5 }
            goto L_0x01d9
        L_0x0175:
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x01d5 }
            java.lang.String r0 = r0.getMusicAuthor()     // Catch:{ Exception -> 0x01d5 }
            java.lang.String r0 = r0.replace(r6, r8)     // Catch:{ Exception -> 0x01d5 }
            org.telegram.messenger.MessageObject r2 = r7.currentMessageObject     // Catch:{ Exception -> 0x01d5 }
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords     // Catch:{ Exception -> 0x01d5 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider     // Catch:{ Exception -> 0x01d5 }
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)     // Catch:{ Exception -> 0x01d5 }
            if (r2 == 0) goto L_0x018c
            r0 = r2
        L_0x018c:
            int r2 = r7.viewType     // Catch:{ Exception -> 0x01d5 }
            if (r2 != r5) goto L_0x01ad
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x01d5 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x01d5 }
            android.text.SpannableStringBuilder r0 = r2.append(r8)     // Catch:{ Exception -> 0x01d5 }
            android.text.SpannableStringBuilder r2 = r7.dotSpan     // Catch:{ Exception -> 0x01d5 }
            android.text.SpannableStringBuilder r0 = r0.append(r2)     // Catch:{ Exception -> 0x01d5 }
            android.text.SpannableStringBuilder r0 = r0.append(r8)     // Catch:{ Exception -> 0x01d5 }
            org.telegram.messenger.MessageObject r2 = r7.currentMessageObject     // Catch:{ Exception -> 0x01d5 }
            java.lang.CharSequence r2 = org.telegram.ui.FilteredSearchView.createFromInfoString(r2)     // Catch:{ Exception -> 0x01d5 }
            android.text.SpannableStringBuilder r0 = r0.append(r2)     // Catch:{ Exception -> 0x01d5 }
        L_0x01ad:
            int r2 = r7.viewType     // Catch:{ Exception -> 0x01d5 }
            if (r2 != r5) goto L_0x01b4
            android.text.TextPaint r2 = r7.description2TextPaint     // Catch:{ Exception -> 0x01d5 }
            goto L_0x01b6
        L_0x01b4:
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x01d5 }
        L_0x01b6:
            r12 = r2
            float r2 = (float) r1     // Catch:{ Exception -> 0x01d5 }
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x01d5 }
            java.lang.CharSequence r11 = android.text.TextUtils.ellipsize(r0, r12, r2, r5)     // Catch:{ Exception -> 0x01d5 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x01d5 }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x01d5 }
            int r13 = r1 + r2
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x01d5 }
            r15 = 1065353216(0x3var_, float:1.0)
            r16 = 0
            r17 = 0
            r10 = r0
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x01d5 }
            r7.descriptionLayout = r0     // Catch:{ Exception -> 0x01d5 }
            goto L_0x01d9
        L_0x01d5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01d9:
            int r0 = android.view.View.MeasureSpec.getSize(r23)
            r1 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            android.text.StaticLayout r2 = r7.captionLayout
            r8 = 1099956224(0x41900000, float:18.0)
            if (r2 != 0) goto L_0x01ea
            goto L_0x01ee
        L_0x01ea:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
        L_0x01ee:
            int r1 = r1 + r4
            boolean r2 = r7.needDivider
            int r1 = r1 + r2
            r7.setMeasuredDimension(r0, r1)
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x020a
            int r1 = android.view.View.MeasureSpec.getSize(r23)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r2
            int r1 = r1 - r0
            goto L_0x020e
        L_0x020a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x020e:
            org.telegram.ui.Components.RadialProgress2 r0 = r7.radialProgress
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r2 = r2 + r1
            r7.buttonX = r2
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r7.buttonY = r3
            r4 = 1111490560(0x42400000, float:48.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 + r4
            r4 = 1112014848(0x42480000, float:50.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setProgressRect(r2, r3, r1, r4)
            org.telegram.ui.Components.CheckBox2 r2 = r7.checkBox
            r4 = 0
            r6 = 0
            r1 = r22
            r3 = r23
            r5 = r24
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
            android.text.StaticLayout r0 = r7.captionLayout
            r1 = 1105723392(0x41e80000, float:29.0)
            if (r0 == 0) goto L_0x0254
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r7.captionY = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 + r1
            r7.descriptionY = r0
            goto L_0x025a
        L_0x0254:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r7.descriptionY = r0
        L_0x025a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedAudioCell.onMeasure(int, int):void");
    }

    public void setMessageObject(MessageObject messageObject, boolean z) {
        this.needDivider = z;
        this.currentMessageObject = messageObject;
        TLRPC$Document document = messageObject.getDocument();
        TLRPC$PhotoSize closestPhotoSizeWithSize = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 360) : null;
        if ((closestPhotoSizeWithSize instanceof TLRPC$TL_photoSize) || (closestPhotoSizeWithSize instanceof TLRPC$TL_photoSizeProgressive)) {
            this.radialProgress.setImageOverlay(closestPhotoSizeWithSize, document, messageObject);
        } else {
            String artworkUrl = messageObject.getArtworkUrl(true);
            if (!TextUtils.isEmpty(artworkUrl)) {
                this.radialProgress.setImageOverlay(artworkUrl);
            } else {
                this.radialProgress.setImageOverlay((TLRPC$PhotoSize) null, (TLRPC$Document) null, (Object) null);
            }
        }
        updateButtonState(false, false);
        requestLayout();
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    public void setCheckForButtonPress(boolean z) {
        this.checkForButtonPress = z;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.radialProgress.onAttachedToWindow();
        updateButtonState(false, false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        this.radialProgress.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
    }

    public MessageObject getMessage() {
        return this.currentMessageObject;
    }

    public void initStreamingIcons() {
        this.radialProgress.initMiniIcons();
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0064  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkAudioMotionEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            float r0 = r9.getX()
            int r0 = (int) r0
            float r1 = r9.getY()
            int r1 = (int) r1
            r2 = 1108344832(0x42100000, float:36.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r8.miniButtonState
            r4 = 1
            r5 = 0
            if (r3 < 0) goto L_0x0032
            r3 = 1104674816(0x41d80000, float:27.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = r8.buttonX
            int r7 = r6 + r3
            if (r0 < r7) goto L_0x0032
            int r6 = r6 + r3
            int r6 = r6 + r2
            if (r0 > r6) goto L_0x0032
            int r6 = r8.buttonY
            int r7 = r6 + r3
            if (r1 < r7) goto L_0x0032
            int r6 = r6 + r3
            int r6 = r6 + r2
            if (r1 > r6) goto L_0x0032
            r2 = 1
            goto L_0x0033
        L_0x0032:
            r2 = 0
        L_0x0033:
            int r3 = r9.getAction()
            if (r3 != 0) goto L_0x0064
            if (r2 == 0) goto L_0x0047
            r8.miniButtonPressed = r4
            org.telegram.ui.Components.RadialProgress2 r9 = r8.radialProgress
            r9.setPressed(r4, r4)
            r8.invalidate()
        L_0x0045:
            r5 = 1
            goto L_0x00ab
        L_0x0047:
            boolean r9 = r8.checkForButtonPress
            if (r9 == 0) goto L_0x00ab
            org.telegram.ui.Components.RadialProgress2 r9 = r8.radialProgress
            android.graphics.RectF r9 = r9.getProgressRect()
            float r0 = (float) r0
            float r1 = (float) r1
            boolean r9 = r9.contains(r0, r1)
            if (r9 == 0) goto L_0x00ab
            r8.buttonPressed = r4
            org.telegram.ui.Components.RadialProgress2 r9 = r8.radialProgress
            r9.setPressed(r4, r5)
            r8.invalidate()
            goto L_0x0045
        L_0x0064:
            int r0 = r9.getAction()
            if (r0 != r4) goto L_0x008a
            boolean r9 = r8.miniButtonPressed
            if (r9 == 0) goto L_0x007a
            r8.miniButtonPressed = r5
            r8.playSoundEffect(r5)
            r8.didPressedMiniButton(r4)
            r8.invalidate()
            goto L_0x00ab
        L_0x007a:
            boolean r9 = r8.buttonPressed
            if (r9 == 0) goto L_0x00ab
            r8.buttonPressed = r5
            r8.playSoundEffect(r5)
            r8.didPressedButton()
            r8.invalidate()
            goto L_0x00ab
        L_0x008a:
            int r0 = r9.getAction()
            r1 = 3
            if (r0 != r1) goto L_0x0099
            r8.miniButtonPressed = r5
            r8.buttonPressed = r5
            r8.invalidate()
            goto L_0x00ab
        L_0x0099:
            int r9 = r9.getAction()
            r0 = 2
            if (r9 != r0) goto L_0x00ab
            if (r2 != 0) goto L_0x00ab
            boolean r9 = r8.miniButtonPressed
            if (r9 == 0) goto L_0x00ab
            r8.miniButtonPressed = r5
            r8.invalidate()
        L_0x00ab:
            org.telegram.ui.Components.RadialProgress2 r9 = r8.radialProgress
            boolean r0 = r8.miniButtonPressed
            r9.setPressed(r0, r4)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedAudioCell.checkAudioMotionEvent(android.view.MotionEvent):boolean");
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(motionEvent);
        }
        boolean checkAudioMotionEvent = checkAudioMotionEvent(motionEvent);
        if (motionEvent.getAction() != 3) {
            return checkAudioMotionEvent;
        }
        this.miniButtonPressed = false;
        this.buttonPressed = false;
        this.radialProgress.setPressed(false, false);
        this.radialProgress.setPressed(this.miniButtonPressed, true);
        return false;
    }

    private void didPressedMiniButton(boolean z) {
        int i = this.miniButtonState;
        if (i == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 1) {
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        }
    }

    public void didPressedButton() {
        int i = this.buttonState;
        if (i == 0) {
            if (this.miniButtonState == 0) {
                this.currentMessageObject.putInDownloadsStore = true;
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            }
            if (needPlayMessage(this.currentMessageObject)) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
                }
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        } else if (i == 1) {
            if (MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        } else if (i == 2) {
            this.radialProgress.setProgress(0.0f, false);
            this.currentMessageObject.putInDownloadsStore = true;
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 4;
            this.radialProgress.setIcon(getIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 4) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.buttonState = 2;
            this.radialProgress.setIcon(getIconForCurrentState(), false, true);
            invalidate();
        }
    }

    private int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        return i == 0 ? 2 : 3;
    }

    private int getIconForCurrentState() {
        int i = this.buttonState;
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        return i == 4 ? 3 : 0;
    }

    public void updateButtonState(boolean z, boolean z2) {
        String fileName = this.currentMessageObject.getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            MessageObject messageObject = this.currentMessageObject;
            boolean z3 = messageObject.attachPathExists || messageObject.mediaExists;
            if (!SharedConfig.streamMedia || !messageObject.isMusic() || ((int) this.currentMessageObject.getDialogId()) == 0) {
                this.hasMiniProgress = 0;
                this.miniButtonState = -1;
            } else {
                this.hasMiniProgress = z3 ? 1 : 2;
                z3 = true;
            }
            if (this.hasMiniProgress != 0) {
                this.radialProgress.setMiniProgressBackgroundColor(getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outLoader" : "chat_inLoader"));
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!isPlayingMessage || (isPlayingMessage && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
                if (this.hasMiniProgress == 1) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    this.miniButtonState = -1;
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), z, z2);
                    return;
                }
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.miniButtonState = 0;
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), z, z2);
                    return;
                }
                this.miniButtonState = 1;
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), z, z2);
                Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                if (fileProgress != null) {
                    this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                } else {
                    this.radialProgress.setProgress(0.0f, z2);
                }
            } else if (z3) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                boolean isPlayingMessage2 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!isPlayingMessage2 || (isPlayingMessage2 && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, z2);
                this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
                invalidate();
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, z2);
                } else {
                    this.buttonState = 4;
                    Float fileProgress2 = ImageLoader.getInstance().getFileProgress(fileName);
                    if (fileProgress2 != null) {
                        this.radialProgress.setProgress(fileProgress2.floatValue(), z2);
                    } else {
                        this.radialProgress.setProgress(0.0f, z2);
                    }
                }
                this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
                invalidate();
            }
        }
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
        if (this.hasMiniProgress != 0) {
            if (this.miniButtonState != 1) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 4) {
            updateButtonState(false, true);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(true);
        if (this.currentMessageObject.isMusic()) {
            accessibilityNodeInfo.setText(LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, this.currentMessageObject.getMusicAuthor(), this.currentMessageObject.getMusicTitle()));
        } else if (!(this.titleLayout == null || this.descriptionLayout == null)) {
            accessibilityNodeInfo.setText(this.titleLayout.getText() + ", " + this.descriptionLayout.getText());
        }
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        updateButtonState(false, true);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    public void setGlobalGradientView(FlickerLoadingView flickerLoadingView) {
        this.globalGradientView = flickerLoadingView;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.enterAlpha == 1.0f || this.globalGradientView == null) {
            drawInternal(canvas);
            drawReorder(canvas);
            super.dispatchDraw(canvas);
            return;
        }
        canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), (int) ((1.0f - this.enterAlpha) * 255.0f), 31);
        this.globalGradientView.setViewType(4);
        this.globalGradientView.updateColors();
        this.globalGradientView.updateGradient();
        this.globalGradientView.draw(canvas);
        canvas.restore();
        canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), (int) (this.enterAlpha * 255.0f), 31);
        drawInternal(canvas);
        super.dispatchDraw(canvas);
        drawReorder(canvas);
        canvas.restore();
    }

    private void drawReorder(Canvas canvas) {
        boolean z = this.showReorderIcon;
        if (z || this.showReorderIconProgress != 0.0f) {
            if (z) {
                float f = this.showReorderIconProgress;
                if (f != 1.0f) {
                    this.showReorderIconProgress = f + 0.10666667f;
                    invalidate();
                    this.showReorderIconProgress = Utilities.clamp(this.showReorderIconProgress, 1.0f, 0.0f);
                    int measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp(12.0f)) - Theme.dialogs_reorderDrawable.getIntrinsicWidth();
                    int measuredHeight = (getMeasuredHeight() - Theme.dialogs_reorderDrawable.getIntrinsicHeight()) >> 1;
                    canvas.save();
                    float f2 = this.showReorderIconProgress;
                    canvas.scale(f2, f2, ((float) measuredWidth) + (((float) Theme.dialogs_reorderDrawable.getIntrinsicWidth()) / 2.0f), ((float) measuredHeight) + (((float) Theme.dialogs_reorderDrawable.getIntrinsicHeight()) / 2.0f));
                    Drawable drawable = Theme.dialogs_reorderDrawable;
                    drawable.setBounds(measuredWidth, measuredHeight, drawable.getIntrinsicWidth() + measuredWidth, Theme.dialogs_reorderDrawable.getIntrinsicHeight() + measuredHeight);
                    Theme.dialogs_reorderDrawable.draw(canvas);
                    canvas.restore();
                }
            }
            if (!z) {
                float f3 = this.showReorderIconProgress;
                if (f3 != 0.0f) {
                    this.showReorderIconProgress = f3 - 0.10666667f;
                    invalidate();
                }
            }
            this.showReorderIconProgress = Utilities.clamp(this.showReorderIconProgress, 1.0f, 0.0f);
            int measuredWidth2 = (getMeasuredWidth() - AndroidUtilities.dp(12.0f)) - Theme.dialogs_reorderDrawable.getIntrinsicWidth();
            int measuredHeight2 = (getMeasuredHeight() - Theme.dialogs_reorderDrawable.getIntrinsicHeight()) >> 1;
            canvas.save();
            float var_ = this.showReorderIconProgress;
            canvas.scale(var_, var_, ((float) measuredWidth2) + (((float) Theme.dialogs_reorderDrawable.getIntrinsicWidth()) / 2.0f), ((float) measuredHeight2) + (((float) Theme.dialogs_reorderDrawable.getIntrinsicHeight()) / 2.0f));
            Drawable drawable2 = Theme.dialogs_reorderDrawable;
            drawable2.setBounds(measuredWidth2, measuredHeight2, drawable2.getIntrinsicWidth() + measuredWidth2, Theme.dialogs_reorderDrawable.getIntrinsicHeight() + measuredHeight2);
            Theme.dialogs_reorderDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void drawInternal(Canvas canvas) {
        StaticLayout staticLayout;
        if (this.viewType == 1) {
            this.description2TextPaint.setColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        }
        int i = 0;
        float f = 8.0f;
        if (this.dateLayout != null) {
            canvas.save();
            canvas.translate((float) (AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? 0 : this.dateLayoutX)), (float) this.titleY);
            this.dateLayout.draw(canvas);
            canvas.restore();
        }
        if (this.titleLayout != null) {
            canvas.save();
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
            if (LocaleController.isRTL && (staticLayout = this.dateLayout) != null) {
                i = staticLayout.getWidth() + AndroidUtilities.dp(4.0f);
            }
            canvas.translate((float) (dp + i), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.captionLayout != null) {
            this.captionTextPaint.setColor(getThemedColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.captionY);
            this.captionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(getThemedColor("windowBackgroundWhiteGrayText2"));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        this.radialProgress.setProgressColor(getThemedColor(this.buttonPressed ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress"));
        this.radialProgress.draw(canvas);
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void setEnterAnimationAlpha(float f) {
        if (this.enterAlpha != f) {
            this.enterAlpha = f;
            invalidate();
        }
    }

    public void showReorderIcon(boolean z, boolean z2) {
        if (this.showReorderIcon != z) {
            this.showReorderIcon = z;
            if (!z2) {
                this.showReorderIconProgress = z ? 1.0f : 0.0f;
            }
            invalidate();
        }
    }
}
