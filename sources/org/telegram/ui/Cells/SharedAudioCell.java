package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress2;

public class SharedAudioCell extends FrameLayout implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_GLOBAL_SEARCH = 1;
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
    private StaticLayout titleLayout;
    private int titleY;
    private int viewType;

    public SharedAudioCell(Context context) {
        this(context, 0, (Theme.ResourcesProvider) null);
    }

    public SharedAudioCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, 0, resourcesProvider2);
    }

    public SharedAudioCell(Context context, int viewType2, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.titleY = AndroidUtilities.dp(9.0f);
        this.descriptionY = AndroidUtilities.dp(29.0f);
        this.captionY = AndroidUtilities.dp(29.0f);
        this.currentAccount = UserConfig.selectedAccount;
        this.enterAlpha = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        this.viewType = viewType2;
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
        int i = 3;
        this.checkBox.setDrawBackgroundAsArc(3);
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : i) | 48, LocaleController.isRTL ? 0.0f : 38.1f, 32.1f, LocaleController.isRTL ? 6.0f : 0.0f, 0.0f));
        if (viewType2 == 1) {
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
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01f5  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x020a  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0215  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x024e  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0260  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r25, int r26) {
        /*
            r24 = this;
            r7 = r24
            r0 = 0
            r7.descriptionLayout = r0
            r7.titleLayout = r0
            r7.captionLayout = r0
            int r8 = android.view.View.MeasureSpec.getSize(r25)
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = r8 - r0
            r1 = 1105199104(0x41e00000, float:28.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r9 = r0 - r1
            r0 = 0
            int r1 = r7.viewType
            r2 = 1101004800(0x41a00000, float:20.0)
            r3 = 1090519040(0x41000000, float:8.0)
            r4 = 1
            if (r1 != r4) goto L_0x0064
            org.telegram.messenger.MessageObject r1 = r7.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.date
            long r5 = (long) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.stringForMessageListDate(r5)
            android.text.TextPaint r5 = r7.description2TextPaint
            float r5 = r5.measureText(r1)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            android.text.TextPaint r11 = r7.description2TextPaint
            r14 = 0
            r15 = 1
            r10 = r1
            r12 = r5
            r13 = r5
            android.text.StaticLayout r6 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r10, r11, r12, r13, r14, r15)
            r7.dateLayout = r6
            int r6 = r9 - r5
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = r6 - r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = r6 + r10
            r7.dateLayoutX = r6
            r6 = 1094713344(0x41400000, float:12.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r5 + r6
            r10 = r0
            goto L_0x0065
        L_0x0064:
            r10 = r0
        L_0x0065:
            r1 = 10
            r5 = 32
            r6 = 1082130432(0x40800000, float:4.0)
            int r0 = r7.viewType     // Catch:{ Exception -> 0x00c2 }
            if (r0 != r4) goto L_0x0086
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x00c2 }
            boolean r0 = r0.isVoice()     // Catch:{ Exception -> 0x00c2 }
            if (r0 != 0) goto L_0x007f
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x00c2 }
            boolean r0 = r0.isRoundVideo()     // Catch:{ Exception -> 0x00c2 }
            if (r0 == 0) goto L_0x0086
        L_0x007f:
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x00c2 }
            java.lang.CharSequence r0 = org.telegram.ui.FilteredSearchView.createFromInfoString(r0)     // Catch:{ Exception -> 0x00c2 }
            goto L_0x0090
        L_0x0086:
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x00c2 }
            java.lang.String r0 = r0.getMusicTitle()     // Catch:{ Exception -> 0x00c2 }
            java.lang.String r0 = r0.replace(r1, r5)     // Catch:{ Exception -> 0x00c2 }
        L_0x0090:
            org.telegram.messenger.MessageObject r11 = r7.currentMessageObject     // Catch:{ Exception -> 0x00c2 }
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords     // Catch:{ Exception -> 0x00c2 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r7.resourcesProvider     // Catch:{ Exception -> 0x00c2 }
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r11, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)     // Catch:{ Exception -> 0x00c2 }
            if (r11 == 0) goto L_0x009d
            r0 = r11
        L_0x009d:
            android.text.TextPaint r12 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00c2 }
            int r13 = r9 - r10
            float r13 = (float) r13     // Catch:{ Exception -> 0x00c2 }
            android.text.TextUtils$TruncateAt r14 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x00c2 }
            java.lang.CharSequence r16 = android.text.TextUtils.ellipsize(r0, r12, r13, r14)     // Catch:{ Exception -> 0x00c2 }
            android.text.StaticLayout r12 = new android.text.StaticLayout     // Catch:{ Exception -> 0x00c2 }
            android.text.TextPaint r17 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00c2 }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x00c2 }
            int r13 = r13 + r9
            int r18 = r13 - r10
            android.text.Layout$Alignment r19 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x00c2 }
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r15 = r12
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)     // Catch:{ Exception -> 0x00c2 }
            r7.titleLayout = r12     // Catch:{ Exception -> 0x00c2 }
            goto L_0x00c6
        L_0x00c2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c6:
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject
            boolean r0 = r0.hasHighlightedWords()
            r11 = 0
            if (r0 == 0) goto L_0x0139
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            java.lang.String r12 = "\n"
            java.lang.String r13 = " "
            java.lang.String r0 = r0.replace(r12, r13)
            java.lang.String r12 = " +"
            java.lang.String r0 = r0.replaceAll(r12, r13)
            java.lang.String r0 = r0.trim()
            android.text.TextPaint r12 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r12 = r12.getFontMetricsInt()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r12, r2, r11)
            org.telegram.messenger.MessageObject r2 = r7.currentMessageObject
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r7.resourcesProvider
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)
            if (r2 == 0) goto L_0x0139
            org.telegram.messenger.MessageObject r12 = r7.currentMessageObject
            java.util.ArrayList<java.lang.String> r12 = r12.highlightedWords
            java.lang.Object r12 = r12.get(r11)
            java.lang.String r12 = (java.lang.String) r12
            android.text.TextPaint r13 = r7.captionTextPaint
            r14 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r12 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r12, r9, r13, r14)
            android.text.TextPaint r13 = r7.captionTextPaint
            float r14 = (float) r9
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r2 = android.text.TextUtils.ellipsize(r12, r13, r14, r15)
            android.text.StaticLayout r12 = new android.text.StaticLayout
            android.text.TextPaint r13 = r7.captionTextPaint
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r19 = r9 + r14
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r12
            r17 = r2
            r18 = r13
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r7.captionLayout = r12
        L_0x0139:
            int r0 = r7.viewType     // Catch:{ Exception -> 0x01e0 }
            if (r0 != r4) goto L_0x017f
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x01e0 }
            boolean r0 = r0.isVoice()     // Catch:{ Exception -> 0x01e0 }
            if (r0 != 0) goto L_0x014d
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x01e0 }
            boolean r0 = r0.isRoundVideo()     // Catch:{ Exception -> 0x01e0 }
            if (r0 == 0) goto L_0x017f
        L_0x014d:
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x01e0 }
            int r0 = r0.getDuration()     // Catch:{ Exception -> 0x01e0 }
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.formatDuration(r0, r11)     // Catch:{ Exception -> 0x01e0 }
            int r1 = r7.viewType     // Catch:{ Exception -> 0x01e0 }
            if (r1 != r4) goto L_0x015e
            android.text.TextPaint r1 = r7.description2TextPaint     // Catch:{ Exception -> 0x01e0 }
            goto L_0x0160
        L_0x015e:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x01e0 }
        L_0x0160:
            float r2 = (float) r9     // Catch:{ Exception -> 0x01e0 }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x01e0 }
            java.lang.CharSequence r13 = android.text.TextUtils.ellipsize(r0, r1, r2, r4)     // Catch:{ Exception -> 0x01e0 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x01e0 }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x01e0 }
            int r15 = r9 + r2
            android.text.Layout$Alignment r16 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x01e0 }
            r17 = 1065353216(0x3var_, float:1.0)
            r18 = 0
            r19 = 0
            r12 = r0
            r14 = r1
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x01e0 }
            r7.descriptionLayout = r0     // Catch:{ Exception -> 0x01e0 }
            goto L_0x01df
        L_0x017f:
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject     // Catch:{ Exception -> 0x01e0 }
            java.lang.String r0 = r0.getMusicAuthor()     // Catch:{ Exception -> 0x01e0 }
            java.lang.String r0 = r0.replace(r1, r5)     // Catch:{ Exception -> 0x01e0 }
            org.telegram.messenger.MessageObject r1 = r7.currentMessageObject     // Catch:{ Exception -> 0x01e0 }
            java.util.ArrayList<java.lang.String> r1 = r1.highlightedWords     // Catch:{ Exception -> 0x01e0 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r7.resourcesProvider     // Catch:{ Exception -> 0x01e0 }
            java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)     // Catch:{ Exception -> 0x01e0 }
            if (r1 == 0) goto L_0x0196
            r0 = r1
        L_0x0196:
            int r2 = r7.viewType     // Catch:{ Exception -> 0x01e0 }
            if (r2 != r4) goto L_0x01b8
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x01e0 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x01e0 }
            android.text.SpannableStringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x01e0 }
            android.text.SpannableStringBuilder r12 = r7.dotSpan     // Catch:{ Exception -> 0x01e0 }
            android.text.SpannableStringBuilder r2 = r2.append(r12)     // Catch:{ Exception -> 0x01e0 }
            android.text.SpannableStringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x01e0 }
            org.telegram.messenger.MessageObject r5 = r7.currentMessageObject     // Catch:{ Exception -> 0x01e0 }
            java.lang.CharSequence r5 = org.telegram.ui.FilteredSearchView.createFromInfoString(r5)     // Catch:{ Exception -> 0x01e0 }
            android.text.SpannableStringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x01e0 }
            r0 = r2
        L_0x01b8:
            int r2 = r7.viewType     // Catch:{ Exception -> 0x01e0 }
            if (r2 != r4) goto L_0x01bf
            android.text.TextPaint r2 = r7.description2TextPaint     // Catch:{ Exception -> 0x01e0 }
            goto L_0x01c1
        L_0x01bf:
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x01e0 }
        L_0x01c1:
            float r4 = (float) r9     // Catch:{ Exception -> 0x01e0 }
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x01e0 }
            java.lang.CharSequence r13 = android.text.TextUtils.ellipsize(r0, r2, r4, r5)     // Catch:{ Exception -> 0x01e0 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x01e0 }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x01e0 }
            int r15 = r9 + r4
            android.text.Layout$Alignment r16 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x01e0 }
            r17 = 1065353216(0x3var_, float:1.0)
            r18 = 0
            r19 = 0
            r12 = r0
            r14 = r2
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x01e0 }
            r7.descriptionLayout = r0     // Catch:{ Exception -> 0x01e0 }
        L_0x01df:
            goto L_0x01e4
        L_0x01e0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01e4:
            int r0 = android.view.View.MeasureSpec.getSize(r25)
            r1 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            android.text.StaticLayout r2 = r7.captionLayout
            r12 = 1099956224(0x41900000, float:18.0)
            if (r2 != 0) goto L_0x01f5
            goto L_0x01f9
        L_0x01f5:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r12)
        L_0x01f9:
            int r1 = r1 + r11
            boolean r2 = r7.needDivider
            int r1 = r1 + r2
            r7.setMeasuredDimension(r0, r1)
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0215
            int r1 = android.view.View.MeasureSpec.getSize(r25)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r2
            int r1 = r1 - r0
            goto L_0x0219
        L_0x0215:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x0219:
            r11 = r1
            org.telegram.ui.Components.RadialProgress2 r1 = r7.radialProgress
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 + r11
            r7.buttonX = r2
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r7.buttonY = r3
            r4 = 1111490560(0x42400000, float:48.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r11
            r5 = 1112014848(0x42480000, float:50.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.setProgressRect(r2, r3, r4, r5)
            org.telegram.ui.Components.CheckBox2 r2 = r7.checkBox
            r4 = 0
            r6 = 0
            r1 = r24
            r3 = r25
            r5 = r26
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
            android.text.StaticLayout r1 = r7.captionLayout
            r2 = 1105723392(0x41e80000, float:29.0)
            if (r1 == 0) goto L_0x0260
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r7.captionY = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r1 = r1 + r2
            r7.descriptionY = r1
            goto L_0x0266
        L_0x0260:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r7.descriptionY = r1
        L_0x0266:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedAudioCell.onMeasure(int, int):void");
    }

    public void setMessageObject(MessageObject messageObject, boolean divider) {
        this.needDivider = divider;
        this.currentMessageObject = messageObject;
        TLRPC.Document document = messageObject.getDocument();
        TLRPC.PhotoSize thumb = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 360) : null;
        if ((thumb instanceof TLRPC.TL_photoSize) || (thumb instanceof TLRPC.TL_photoSizeProgressive)) {
            this.radialProgress.setImageOverlay(thumb, document, messageObject);
        } else {
            String artworkUrl = messageObject.getArtworkUrl(true);
            if (!TextUtils.isEmpty(artworkUrl)) {
                this.radialProgress.setImageOverlay(artworkUrl);
            } else {
                this.radialProgress.setImageOverlay((TLRPC.PhotoSize) null, (TLRPC.Document) null, (Object) null);
            }
        }
        updateButtonState(false, false);
        requestLayout();
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    public void setCheckForButtonPress(boolean value) {
        this.checkForButtonPress = value;
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

    private boolean checkAudioMotionEvent(MotionEvent event) {
        boolean z;
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        int side = AndroidUtilities.dp(36.0f);
        boolean area = false;
        if (this.miniButtonState >= 0) {
            int offset = AndroidUtilities.dp(27.0f);
            int i = this.buttonX;
            if (x >= i + offset && x <= i + offset + side) {
                int i2 = this.buttonY;
                if (y >= i2 + offset && y <= i2 + offset + side) {
                    z = true;
                    area = z;
                }
            }
            z = false;
            area = z;
        }
        if (event.getAction() == 0) {
            if (area) {
                this.miniButtonPressed = true;
                this.radialProgress.setPressed(true, true);
                invalidate();
                result = true;
            } else if (this.checkForButtonPress && this.radialProgress.getProgressRect().contains((float) x, (float) y)) {
                this.buttonPressed = true;
                this.radialProgress.setPressed(true, false);
                invalidate();
                result = true;
            }
        } else if (event.getAction() == 1) {
            if (this.miniButtonPressed) {
                this.miniButtonPressed = false;
                playSoundEffect(0);
                didPressedMiniButton(true);
                invalidate();
            } else if (this.buttonPressed) {
                this.buttonPressed = false;
                playSoundEffect(0);
                didPressedButton();
                invalidate();
            }
        } else if (event.getAction() == 3) {
            this.miniButtonPressed = false;
            this.buttonPressed = false;
            invalidate();
        } else if (event.getAction() == 2 && !area && this.miniButtonPressed) {
            this.miniButtonPressed = false;
            invalidate();
        }
        this.radialProgress.setPressed(this.miniButtonPressed, true);
        return result;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(event);
        }
        boolean result = checkAudioMotionEvent(event);
        if (event.getAction() != 3) {
            return result;
        }
        this.miniButtonPressed = false;
        this.buttonPressed = false;
        this.radialProgress.setPressed(false, false);
        this.radialProgress.setPressed(this.miniButtonPressed, true);
        return false;
    }

    private void didPressedMiniButton(boolean animated) {
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
            if (MediaController.getInstance().m104lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.currentMessageObject)) {
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
        if (i == 0) {
            return 2;
        }
        return 3;
    }

    private int getIconForCurrentState() {
        int i = this.buttonState;
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        if (i == 4) {
            return 3;
        }
        return 0;
    }

    public void updateButtonState(boolean ifSame, boolean animated) {
        String fileName = this.currentMessageObject.getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            boolean fileExists = this.currentMessageObject.attachPathExists || this.currentMessageObject.mediaExists;
            if (!SharedConfig.streamMedia || !this.currentMessageObject.isMusic() || ((int) this.currentMessageObject.getDialogId()) == 0) {
                this.hasMiniProgress = 0;
                this.miniButtonState = -1;
            } else {
                this.hasMiniProgress = fileExists ? 1 : 2;
                fileExists = true;
            }
            if (this.hasMiniProgress != 0) {
                this.radialProgress.setMiniProgressBackgroundColor(getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outLoader" : "chat_inLoader"));
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                if (this.hasMiniProgress == 1) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    this.miniButtonState = -1;
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), ifSame, animated);
                    return;
                }
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.miniButtonState = 0;
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), ifSame, animated);
                    return;
                }
                this.miniButtonState = 1;
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), ifSame, animated);
                Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress != null) {
                    this.radialProgress.setProgress(progress.floatValue(), animated);
                } else {
                    this.radialProgress.setProgress(0.0f, animated);
                }
            } else if (fileExists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                boolean playing2 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing2 || (playing2 && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, animated);
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                invalidate();
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                } else {
                    this.buttonState = 4;
                    Float progress2 = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress2 != null) {
                        this.radialProgress.setProgress(progress2.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                }
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                invalidate();
            }
        }
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadSize) / ((float) totalSize)), true);
        if (this.hasMiniProgress != 0) {
            if (this.miniButtonState != 1) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 4) {
            updateButtonState(false, true);
        }
    }

    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    /* access modifiers changed from: protected */
    public boolean needPlayMessage(MessageObject messageObject) {
        return false;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(true);
        if (this.currentMessageObject.isMusic()) {
            info.setText(LocaleController.formatString("AccDescrMusicInfo", NUM, this.currentMessageObject.getMusicAuthor(), this.currentMessageObject.getMusicTitle()));
        } else if (!(this.titleLayout == null || this.descriptionLayout == null)) {
            info.setText(this.titleLayout.getText() + ", " + this.descriptionLayout.getText());
        }
        if (this.checkBox.isChecked()) {
            info.setCheckable(true);
            info.setChecked(true);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        updateButtonState(false, true);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setGlobalGradientView(FlickerLoadingView globalGradientView2) {
        this.globalGradientView = globalGradientView2;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.enterAlpha == 1.0f || this.globalGradientView == null) {
            drawInternal(canvas);
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
        canvas.restore();
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

    public void setEnterAnimationAlpha(float alpha) {
        if (this.enterAlpha != alpha) {
            this.enterAlpha = alpha;
            invalidate();
        }
    }
}
