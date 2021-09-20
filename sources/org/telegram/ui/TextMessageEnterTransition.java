package org.telegram.ui;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MessageEnterTransitionContainer;

public class TextMessageEnterTransition implements MessageEnterTransitionContainer.Transition {
    /* access modifiers changed from: private */
    public int animationIndex = -1;
    private ValueAnimator animator;
    Paint bitmapPaint = new Paint(1);
    boolean changeColor;
    private ChatActivity chatActivity;
    MessageEnterTransitionContainer container;
    boolean crossfade;
    Bitmap crossfadeTextBitmap;
    float crossfadeTextOffset;
    /* access modifiers changed from: private */
    public final int currentAccount;
    MessageObject currentMessageObject;
    boolean drawBitmaps = false;
    private float drawableFromBottom;
    float drawableFromTop;
    ChatActivityEnterView enterView;
    int fromColor;
    Drawable fromMessageDrawable;
    private float fromStartX;
    private float fromStartY;
    private Matrix gradientMatrix;
    private Paint gradientPaint;
    private LinearGradient gradientShader;
    boolean hasReply;
    boolean initBitmaps = false;
    StaticLayout layout;
    RecyclerListView listView;
    private int messageId;
    ChatMessageCell messageView;
    float progress;
    int replayFromColor;
    int replayObjectFromColor;
    float replyFromStartX;
    float replyFromStartY;
    float replyMessageDx;
    float replyNameDx;
    private final Theme.ResourcesProvider resourcesProvider;
    StaticLayout rtlLayout;
    private float scaleFrom;
    private float scaleY;
    Bitmap textLayoutBitmap;
    Bitmap textLayoutBitmapRtl;
    MessageObject.TextLayoutBlock textLayoutBlock;
    float textX;
    float textY;
    int toColor;
    float toXOffset;
    float toXOffsetRtl;

    /* JADX WARNING: Removed duplicated region for block: B:114:0x04af  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x04b1  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x062d  */
    /* JADX WARNING: Removed duplicated region for block: B:153:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x01b3  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0271  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x02a8  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x033d  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x035a  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x03a5  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x03df  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0422 A[Catch:{ Exception -> 0x049e }] */
    @android.annotation.SuppressLint({"WrongConstant"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TextMessageEnterTransition(org.telegram.ui.Cells.ChatMessageCell r24, org.telegram.ui.ChatActivity r25, org.telegram.ui.Components.RecyclerListView r26, org.telegram.ui.MessageEnterTransitionContainer r27, org.telegram.ui.ActionBar.Theme.ResourcesProvider r28) {
        /*
            r23 = this;
            r6 = r23
            r7 = r24
            r2 = r27
            r23.<init>()
            android.graphics.Paint r0 = new android.graphics.Paint
            r8 = 1
            r0.<init>(r8)
            r6.bitmapPaint = r0
            r0 = 0
            r6.initBitmaps = r0
            r6.drawBitmaps = r0
            r1 = -1
            r6.animationIndex = r1
            r1 = r28
            r6.resourcesProvider = r1
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r6.currentAccount = r1
            org.telegram.messenger.MessageObject r1 = r24.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            int r1 = r1.size()
            if (r1 > r8) goto L_0x0639
            org.telegram.messenger.MessageObject r1 = r24.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            java.lang.Object r1 = r1.get(r0)
            org.telegram.messenger.MessageObject$TextLayoutBlock r1 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r1
            android.text.StaticLayout r1 = r1.textLayout
            int r1 = r1.getLineCount()
            r3 = 10
            if (r1 <= r3) goto L_0x0045
            goto L_0x0639
        L_0x0045:
            r6.messageView = r7
            r1 = r26
            r6.listView = r1
            r6.container = r2
            r5 = r25
            r6.chatActivity = r5
            org.telegram.ui.Components.ChatActivityEnterView r3 = r25.getChatActivityEnterView()
            r6.enterView = r3
            org.telegram.ui.Components.ChatActivityEnterView r4 = r25.getChatActivityEnterView()
            if (r4 == 0) goto L_0x0639
            org.telegram.ui.Components.EditTextCaption r3 = r4.getEditField()
            if (r3 == 0) goto L_0x0639
            org.telegram.ui.Components.EditTextCaption r3 = r4.getEditField()
            android.text.Layout r3 = r3.getLayout()
            if (r3 != 0) goto L_0x006f
            goto L_0x0639
        L_0x006f:
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r3 = r4.getRecordCicle()
            float r3 = r3.drawingCircleRadius
            android.graphics.Paint r3 = r6.bitmapPaint
            r3.setFilterBitmap(r8)
            org.telegram.messenger.MessageObject r3 = r24.getMessageObject()
            r6.currentMessageObject = r3
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r3 = r24.getTransitionParams()
            boolean r3 = r3.wasDraw
            if (r3 != 0) goto L_0x0090
            android.graphics.Canvas r3 = new android.graphics.Canvas
            r3.<init>()
            r7.draw(r3)
        L_0x0090:
            r7.setEnterTransitionInProgress(r8)
            org.telegram.ui.Components.EditTextCaption r3 = r4.getEditField()
            android.text.Layout r3 = r3.getLayout()
            java.lang.CharSequence r3 = r3.getText()
            org.telegram.messenger.MessageObject r9 = r24.getMessageObject()
            java.lang.CharSequence r9 = r9.messageText
            r6.crossfade = r0
            org.telegram.ui.Components.EditTextCaption r10 = r4.getEditField()
            android.text.Layout r10 = r10.getLayout()
            int r10 = r10.getHeight()
            android.text.TextPaint r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r12 = 1101004800(0x41a00000, float:20.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            org.telegram.messenger.MessageObject r13 = r24.getMessageObject()
            int r13 = r13.getEmojiOnlyCount()
            r14 = 2
            if (r13 == 0) goto L_0x00ff
            org.telegram.messenger.MessageObject r13 = r24.getMessageObject()
            int r13 = r13.getEmojiOnlyCount()
            if (r13 != r8) goto L_0x00d9
            android.text.TextPaint r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintOneEmoji
            r12 = 1107296256(0x42000000, float:32.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            goto L_0x00ff
        L_0x00d9:
            org.telegram.messenger.MessageObject r13 = r24.getMessageObject()
            int r13 = r13.getEmojiOnlyCount()
            if (r13 != r14) goto L_0x00ec
            android.text.TextPaint r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintTwoEmoji
            r12 = 1105199104(0x41e00000, float:28.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            goto L_0x00ff
        L_0x00ec:
            org.telegram.messenger.MessageObject r13 = r24.getMessageObject()
            int r13 = r13.getEmojiOnlyCount()
            r15 = 3
            if (r13 != r15) goto L_0x00ff
            android.text.TextPaint r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintThreeEmoji
            r12 = 1103101952(0x41CLASSNAME, float:24.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
        L_0x00ff:
            boolean r13 = r9 instanceof android.text.Spannable
            if (r13 == 0) goto L_0x011f
            r13 = r9
            android.text.Spannable r13 = (android.text.Spannable) r13
            int r15 = r9.length()
            java.lang.Class<java.lang.Object> r14 = java.lang.Object.class
            java.lang.Object[] r13 = r13.getSpans(r0, r15, r14)
            r14 = 0
        L_0x0111:
            int r15 = r13.length
            if (r14 >= r15) goto L_0x011f
            r15 = r13[r14]
            boolean r15 = r15 instanceof org.telegram.messenger.Emoji.EmojiSpan
            if (r15 != 0) goto L_0x011c
            r13 = 1
            goto L_0x0120
        L_0x011c:
            int r14 = r14 + 1
            goto L_0x0111
        L_0x011f:
            r13 = 0
        L_0x0120:
            int r14 = r3.length()
            int r15 = r9.length()
            if (r14 != r15) goto L_0x0130
            if (r13 == 0) goto L_0x012d
            goto L_0x0130
        L_0x012d:
            r3 = r10
            r10 = 0
            goto L_0x0181
        L_0x0130:
            r6.crossfade = r8
            java.lang.String r3 = r3.toString()
            java.lang.String r9 = r3.trim()
            int r3 = r3.indexOf(r9)
            if (r3 <= 0) goto L_0x0177
            org.telegram.ui.Components.EditTextCaption r10 = r4.getEditField()
            android.text.Layout r10 = r10.getLayout()
            org.telegram.ui.Components.EditTextCaption r13 = r4.getEditField()
            android.text.Layout r13 = r13.getLayout()
            int r13 = r13.getLineForOffset(r3)
            int r10 = r10.getLineTop(r13)
            org.telegram.ui.Components.EditTextCaption r13 = r4.getEditField()
            android.text.Layout r13 = r13.getLayout()
            org.telegram.ui.Components.EditTextCaption r14 = r4.getEditField()
            android.text.Layout r14 = r14.getLayout()
            int r15 = r9.length()
            int r3 = r3 + r15
            int r3 = r14.getLineForOffset(r3)
            int r3 = r13.getLineBottom(r3)
            int r3 = r3 - r10
            goto L_0x0179
        L_0x0177:
            r3 = r10
            r10 = 0
        L_0x0179:
            android.graphics.Paint$FontMetricsInt r13 = r11.getFontMetricsInt()
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r9, r13, r12, r0)
        L_0x0181:
            org.telegram.ui.Components.EditTextCaption r12 = r4.getEditField()
            float r12 = r12.getTextSize()
            float r13 = r11.getTextSize()
            float r12 = r12 / r13
            r6.scaleFrom = r12
            org.telegram.ui.Components.EditTextCaption r12 = r4.getEditField()
            android.text.Layout r12 = r12.getLayout()
            int r12 = r12.getLineCount()
            org.telegram.ui.Components.EditTextCaption r13 = r4.getEditField()
            android.text.Layout r13 = r13.getLayout()
            int r13 = r13.getWidth()
            float r13 = (float) r13
            float r14 = r6.scaleFrom
            float r13 = r13 / r14
            int r13 = (int) r13
            int r14 = android.os.Build.VERSION.SDK_INT
            r15 = 24
            if (r14 < r15) goto L_0x01d0
            int r14 = r9.length()
            android.text.StaticLayout$Builder r14 = android.text.StaticLayout.Builder.obtain(r9, r0, r14, r11, r13)
            android.text.StaticLayout$Builder r14 = r14.setBreakStrategy(r8)
            android.text.StaticLayout$Builder r14 = r14.setHyphenationFrequency(r0)
            android.text.Layout$Alignment r15 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r14 = r14.setAlignment(r15)
            android.text.StaticLayout r14 = r14.build()
            r6.layout = r14
            goto L_0x01e6
        L_0x01d0:
            android.text.StaticLayout r14 = new android.text.StaticLayout
            android.text.Layout$Alignment r19 = android.text.Layout.Alignment.ALIGN_NORMAL
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r15 = r14
            r16 = r9
            r17 = r11
            r18 = r13
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)
            r6.layout = r14
        L_0x01e6:
            float r14 = r4.getY()
            org.telegram.ui.Components.EditTextCaption r15 = r4.getEditField()
            float r15 = r15.getY()
            float r14 = r14 + r15
            org.telegram.ui.Components.EditTextCaption r15 = r4.getEditField()
            android.view.ViewParent r15 = r15.getParent()
            android.view.View r15 = (android.view.View) r15
            float r15 = r15.getY()
            float r14 = r14 + r15
            org.telegram.ui.Components.EditTextCaption r15 = r4.getEditField()
            android.view.ViewParent r15 = r15.getParent()
            android.view.ViewParent r15 = r15.getParent()
            android.view.View r15 = (android.view.View) r15
            float r15 = r15.getY()
            float r14 = r14 + r15
            float r15 = r4.getX()
            org.telegram.ui.Components.EditTextCaption r16 = r4.getEditField()
            float r16 = r16.getX()
            float r15 = r15 + r16
            org.telegram.ui.Components.EditTextCaption r16 = r4.getEditField()
            android.view.ViewParent r16 = r16.getParent()
            android.view.View r16 = (android.view.View) r16
            float r16 = r16.getX()
            float r15 = r15 + r16
            org.telegram.ui.Components.EditTextCaption r16 = r4.getEditField()
            android.view.ViewParent r16 = r16.getParent()
            android.view.ViewParent r16 = r16.getParent()
            android.view.View r16 = (android.view.View) r16
            float r16 = r16.getX()
            float r15 = r15 + r16
            r6.fromStartX = r15
            r15 = 1092616192(0x41200000, float:10.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            float r15 = r15 + r14
            org.telegram.ui.Components.EditTextCaption r16 = r4.getEditField()
            int r8 = r16.getScrollY()
            float r8 = (float) r8
            float r15 = r15 - r8
            float r8 = (float) r10
            float r15 = r15 + r8
            r6.fromStartY = r15
            r8 = 0
            r6.toXOffset = r8
            r10 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r8 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r15 = 0
        L_0x0269:
            android.text.StaticLayout r0 = r6.layout
            int r0 = r0.getLineCount()
            if (r15 >= r0) goto L_0x027f
            android.text.StaticLayout r0 = r6.layout
            float r0 = r0.getLineLeft(r15)
            int r16 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r16 >= 0) goto L_0x027c
            r8 = r0
        L_0x027c:
            int r15 = r15 + 1
            goto L_0x0269
        L_0x027f:
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0285
            r6.toXOffset = r8
        L_0x0285:
            float r0 = (float) r3
            android.text.StaticLayout r3 = r6.layout
            int r3 = r3.getHeight()
            float r3 = (float) r3
            float r8 = r6.scaleFrom
            float r3 = r3 * r8
            float r0 = r0 / r3
            r6.scaleY = r0
            r0 = 1082130432(0x40800000, float:4.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r0 = r0 + r14
            r6.drawableFromTop = r0
            org.telegram.ui.Components.ChatActivityEnterView r0 = r6.enterView
            boolean r0 = r0.isTopViewVisible()
            r3 = 1094713344(0x41400000, float:12.0)
            if (r0 == 0) goto L_0x02b2
            float r0 = r6.drawableFromTop
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r8 = (float) r8
            float r0 = r0 - r8
            r6.drawableFromTop = r0
        L_0x02b2:
            org.telegram.ui.Components.EditTextCaption r0 = r4.getEditField()
            int r0 = r0.getMeasuredHeight()
            float r0 = (float) r0
            float r14 = r14 + r0
            r6.drawableFromBottom = r14
            org.telegram.messenger.MessageObject r0 = r24.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r0.textLayoutBlocks
            r8 = 0
            java.lang.Object r0 = r0.get(r8)
            org.telegram.messenger.MessageObject$TextLayoutBlock r0 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r0
            r6.textLayoutBlock = r0
            android.text.StaticLayout r0 = r0.textLayout
            java.lang.String r8 = "chat_messageTextOut"
            int r14 = r6.getThemedColor(r8)
            double r14 = androidx.core.graphics.ColorUtils.calculateLuminance(r14)
            java.lang.String r10 = "chat_messagePanelText"
            int r17 = r6.getThemedColor(r10)
            double r17 = androidx.core.graphics.ColorUtils.calculateLuminance(r17)
            double r14 = r14 - r17
            double r14 = java.lang.Math.abs(r14)
            r17 = 4596373779801702400(0x3fCLASSNAMEa0000000, double:0.NUM)
            int r19 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1))
            if (r19 <= 0) goto L_0x02f7
            r14 = 1
            r6.crossfade = r14
            r6.changeColor = r14
        L_0x02f7:
            int r10 = r6.getThemedColor(r10)
            r6.fromColor = r10
            int r8 = r6.getThemedColor(r8)
            r6.toColor = r8
            int r8 = r0.getLineCount()
            android.text.StaticLayout r10 = r6.layout
            int r10 = r10.getLineCount()
            if (r8 != r10) goto L_0x033d
            int r12 = r0.getLineCount()
            r8 = 0
            r10 = 0
            r14 = 0
        L_0x0316:
            if (r8 >= r12) goto L_0x033b
            android.text.StaticLayout r15 = r6.layout
            boolean r15 = r6.isRtlLine(r15, r8)
            if (r15 == 0) goto L_0x0323
            int r14 = r14 + 1
            goto L_0x0325
        L_0x0323:
            int r10 = r10 + 1
        L_0x0325:
            int r15 = r0.getLineEnd(r8)
            android.text.StaticLayout r3 = r6.layout
            int r3 = r3.getLineEnd(r8)
            if (r15 == r3) goto L_0x0335
            r3 = 1
            r6.crossfade = r3
            goto L_0x0342
        L_0x0335:
            r3 = 1
            int r8 = r8 + 1
            r3 = 1094713344(0x41400000, float:12.0)
            goto L_0x0316
        L_0x033b:
            r3 = 1
            goto L_0x0342
        L_0x033d:
            r3 = 1
            r6.crossfade = r3
            r10 = 0
            r14 = 0
        L_0x0342:
            boolean r0 = r6.crossfade
            if (r0 != 0) goto L_0x0401
            if (r14 <= 0) goto L_0x0401
            if (r10 <= 0) goto L_0x0401
            android.text.SpannableString r0 = new android.text.SpannableString
            r0.<init>(r9)
            android.text.SpannableString r3 = new android.text.SpannableString
            r3.<init>(r9)
            r8 = 0
            r10 = 2139095039(0x7f7fffff, float:3.4028235E38)
        L_0x0358:
            if (r8 >= r12) goto L_0x039e
            android.text.StaticLayout r9 = r6.layout
            boolean r9 = r6.isRtlLine(r9, r8)
            if (r9 == 0) goto L_0x0384
            org.telegram.ui.Components.EmptyStubSpan r9 = new org.telegram.ui.Components.EmptyStubSpan
            r9.<init>()
            android.text.StaticLayout r14 = r6.layout
            int r14 = r14.getLineStart(r8)
            android.text.StaticLayout r15 = r6.layout
            int r15 = r15.getLineEnd(r8)
            r1 = 0
            r0.setSpan(r9, r14, r15, r1)
            android.text.StaticLayout r1 = r6.layout
            float r1 = r1.getLineLeft(r8)
            int r9 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r9 >= 0) goto L_0x0382
            r10 = r1
        L_0x0382:
            r15 = 0
            goto L_0x0399
        L_0x0384:
            org.telegram.ui.Components.EmptyStubSpan r1 = new org.telegram.ui.Components.EmptyStubSpan
            r1.<init>()
            android.text.StaticLayout r9 = r6.layout
            int r9 = r9.getLineStart(r8)
            android.text.StaticLayout r14 = r6.layout
            int r14 = r14.getLineEnd(r8)
            r15 = 0
            r3.setSpan(r1, r9, r14, r15)
        L_0x0399:
            int r8 = r8 + 1
            r1 = r26
            goto L_0x0358
        L_0x039e:
            r15 = 0
            int r1 = android.os.Build.VERSION.SDK_INT
            r8 = 24
            if (r1 < r8) goto L_0x03df
            int r1 = r0.length()
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r15, r1, r11, r13)
            r1 = 1
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r1)
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r15)
            android.text.Layout$Alignment r8 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r8)
            android.text.StaticLayout r0 = r0.build()
            r6.layout = r0
            int r0 = r3.length()
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r3, r15, r0, r11, r13)
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r1)
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r15)
            android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r1)
            android.text.StaticLayout r0 = r0.build()
            r6.rtlLayout = r0
            goto L_0x0401
        L_0x03df:
            android.text.StaticLayout r1 = new android.text.StaticLayout
            android.text.Layout$Alignment r19 = android.text.Layout.Alignment.ALIGN_NORMAL
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r15 = r1
            r16 = r0
            r17 = r11
            r18 = r13
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)
            r6.layout = r1
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.Layout$Alignment r19 = android.text.Layout.Alignment.ALIGN_NORMAL
            r15 = r0
            r16 = r3
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)
            r6.rtlLayout = r0
        L_0x0401:
            android.text.StaticLayout r0 = r6.layout
            int r0 = r0.getWidth()
            org.telegram.messenger.MessageObject r1 = r24.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject$TextLayoutBlock r1 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r1
            android.text.StaticLayout r1 = r1.textLayout
            int r1 = r1.getWidth()
            int r0 = r0 - r1
            float r0 = (float) r0
            r6.toXOffsetRtl = r0
            boolean r0 = r6.drawBitmaps     // Catch:{ Exception -> 0x049e }
            if (r0 == 0) goto L_0x04a1
            android.text.StaticLayout r0 = r6.layout     // Catch:{ Exception -> 0x049e }
            int r0 = r0.getWidth()     // Catch:{ Exception -> 0x049e }
            android.text.StaticLayout r1 = r6.layout     // Catch:{ Exception -> 0x049e }
            int r1 = r1.getHeight()     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x049e }
            r6.textLayoutBitmap = r0     // Catch:{ Exception -> 0x049e }
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap r1 = r6.textLayoutBitmap     // Catch:{ Exception -> 0x049e }
            r0.<init>(r1)     // Catch:{ Exception -> 0x049e }
            android.text.StaticLayout r1 = r6.layout     // Catch:{ Exception -> 0x049e }
            r1.draw(r0)     // Catch:{ Exception -> 0x049e }
            android.text.StaticLayout r0 = r6.rtlLayout     // Catch:{ Exception -> 0x049e }
            if (r0 == 0) goto L_0x0464
            int r0 = r0.getWidth()     // Catch:{ Exception -> 0x049e }
            android.text.StaticLayout r1 = r6.rtlLayout     // Catch:{ Exception -> 0x049e }
            int r1 = r1.getHeight()     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x049e }
            r6.textLayoutBitmapRtl = r0     // Catch:{ Exception -> 0x049e }
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap r1 = r6.textLayoutBitmapRtl     // Catch:{ Exception -> 0x049e }
            r0.<init>(r1)     // Catch:{ Exception -> 0x049e }
            android.text.StaticLayout r1 = r6.rtlLayout     // Catch:{ Exception -> 0x049e }
            r1.draw(r0)     // Catch:{ Exception -> 0x049e }
        L_0x0464:
            boolean r0 = r6.crossfade     // Catch:{ Exception -> 0x049e }
            if (r0 == 0) goto L_0x04a1
            int r0 = r24.getMeasuredHeight()     // Catch:{ Exception -> 0x049e }
            int r1 = r26.getMeasuredHeight()     // Catch:{ Exception -> 0x049e }
            if (r0 >= r1) goto L_0x0486
            r0 = 0
            r6.crossfadeTextOffset = r0     // Catch:{ Exception -> 0x049e }
            int r0 = r24.getMeasuredWidth()     // Catch:{ Exception -> 0x049e }
            int r1 = r24.getMeasuredHeight()     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x049e }
            r6.crossfadeTextBitmap = r0     // Catch:{ Exception -> 0x049e }
            goto L_0x04a1
        L_0x0486:
            int r0 = r24.getTop()     // Catch:{ Exception -> 0x049e }
            float r0 = (float) r0     // Catch:{ Exception -> 0x049e }
            r6.crossfadeTextOffset = r0     // Catch:{ Exception -> 0x049e }
            int r0 = r24.getMeasuredWidth()     // Catch:{ Exception -> 0x049e }
            int r1 = r26.getMeasuredHeight()     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x049e }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x049e }
            r6.crossfadeTextBitmap = r0     // Catch:{ Exception -> 0x049e }
            goto L_0x04a1
        L_0x049e:
            r0 = 0
            r6.drawBitmaps = r0
        L_0x04a1:
            org.telegram.messenger.MessageObject r0 = r24.getMessageObject()
            int r0 = r0.getReplyMsgId()
            if (r0 == 0) goto L_0x04b1
            android.text.StaticLayout r0 = r7.replyNameLayout
            if (r0 == 0) goto L_0x04b1
            r0 = 1
            goto L_0x04b2
        L_0x04b1:
            r0 = 0
        L_0x04b2:
            r6.hasReply = r0
            if (r0 == 0) goto L_0x0538
            org.telegram.ui.ActionBar.SimpleTextView r0 = r25.getReplyNameTextView()
            float r1 = r0.getX()
            android.view.ViewParent r3 = r0.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getX()
            float r1 = r1 + r3
            r6.replyFromStartX = r1
            float r1 = r0.getY()
            android.view.ViewParent r3 = r0.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r1 = r1 + r3
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewParent r0 = r0.getParent()
            android.view.View r0 = (android.view.View) r0
            float r0 = r0.getY()
            float r1 = r1 + r0
            r6.replyFromStartY = r1
            org.telegram.ui.ActionBar.SimpleTextView r0 = r25.getReplyObjectTextView()
            r0.getY()
            android.view.ViewParent r1 = r0.getParent()
            android.view.ViewParent r1 = r1.getParent()
            android.view.View r1 = (android.view.View) r1
            r1.getY()
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewParent r0 = r0.getParent()
            android.view.View r0 = (android.view.View) r0
            r0.getY()
            org.telegram.ui.ActionBar.SimpleTextView r0 = r25.getReplyNameTextView()
            int r0 = r0.getTextColor()
            r6.replayFromColor = r0
            org.telegram.ui.ActionBar.SimpleTextView r0 = r25.getReplyObjectTextView()
            int r0 = r0.getTextColor()
            r6.replayObjectFromColor = r0
            float r0 = r6.drawableFromTop
            r1 = 1110966272(0x42380000, float:46.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r0 = r0 - r1
            r6.drawableFromTop = r0
        L_0x0538:
            android.graphics.Matrix r0 = new android.graphics.Matrix
            r0.<init>()
            r6.gradientMatrix = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r1 = 1
            r0.<init>(r1)
            r6.gradientPaint = r0
            android.graphics.PorterDuffXfermode r1 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.DST_IN
            r1.<init>(r3)
            r0.setXfermode(r1)
            android.graphics.LinearGradient r0 = new android.graphics.LinearGradient
            r9 = 0
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r10 = (float) r1
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.CLAMP
            r8 = r0
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)
            r6.gradientShader = r0
            android.graphics.Paint r1 = r6.gradientPaint
            r1.setShader(r0)
            org.telegram.messenger.MessageObject r0 = r24.getMessageObject()
            int r0 = r0.stableId
            r6.messageId = r0
            org.telegram.ui.Components.EditTextCaption r0 = r4.getEditField()
            r1 = 0
            r0.setAlpha(r1)
            r0 = 1
            r4.setTextTransitionIsRunning(r0)
            android.text.StaticLayout r3 = r7.replyNameLayout
            if (r3 == 0) goto L_0x05aa
            java.lang.CharSequence r3 = r3.getText()
            int r3 = r3.length()
            if (r3 <= r0) goto L_0x05aa
            android.text.StaticLayout r0 = r7.replyNameLayout
            r3 = 0
            float r0 = r0.getPrimaryHorizontal(r3)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x05aa
            android.text.StaticLayout r0 = r7.replyNameLayout
            int r0 = r0.getWidth()
            float r0 = (float) r0
            android.text.StaticLayout r1 = r7.replyNameLayout
            float r1 = r1.getLineWidth(r3)
            float r0 = r0 - r1
            r6.replyNameDx = r0
        L_0x05aa:
            android.text.StaticLayout r0 = r7.replyTextLayout
            if (r0 == 0) goto L_0x05d5
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            r1 = 1
            if (r0 < r1) goto L_0x05d5
            android.text.StaticLayout r0 = r7.replyTextLayout
            r1 = 0
            float r0 = r0.getPrimaryHorizontal(r1)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x05d5
            android.text.StaticLayout r0 = r7.replyTextLayout
            int r0 = r0.getWidth()
            float r0 = (float) r0
            android.text.StaticLayout r3 = r7.replyTextLayout
            float r1 = r3.getLineWidth(r1)
            float r0 = r0 - r1
            r6.replyMessageDx = r0
        L_0x05d5:
            r0 = 2
            float[] r1 = new float[r0]
            r1 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r1)
            r6.animator = r0
            org.telegram.ui.TextMessageEnterTransition$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.TextMessageEnterTransition$$ExternalSyntheticLambda0
            r1.<init>(r6, r4, r2)
            r0.addUpdateListener(r1)
            android.animation.ValueAnimator r0 = r6.animator
            android.view.animation.LinearInterpolator r1 = new android.view.animation.LinearInterpolator
            r1.<init>()
            r0.setInterpolator(r1)
            android.animation.ValueAnimator r0 = r6.animator
            r8 = 250(0xfa, double:1.235E-321)
            r0.setDuration(r8)
            r2.addTransition(r6)
            int r0 = r6.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = r6.animationIndex
            r3 = 0
            int r0 = r0.setAnimationInProgress(r1, r3)
            r6.animationIndex = r0
            android.animation.ValueAnimator r8 = r6.animator
            org.telegram.ui.TextMessageEnterTransition$1 r9 = new org.telegram.ui.TextMessageEnterTransition$1
            r0 = r9
            r1 = r23
            r2 = r27
            r3 = r24
            r5 = r25
            r0.<init>(r2, r3, r4, r5)
            r8.addListener(r9)
            int r0 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
            r1 = 2
            if (r0 != r1) goto L_0x0639
            r0 = 1
            org.telegram.ui.ActionBar.Theme$MessageDrawable r0 = r7.getCurrentBackgroundDrawable(r0)
            if (r0 == 0) goto L_0x0639
            java.lang.String r1 = "chat_messagePanelBackground"
            int r1 = r6.getThemedColor(r1)
            android.graphics.drawable.Drawable r0 = r0.getTransitionDrawable(r1)
            r6.fromMessageDrawable = r0
        L_0x0639:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TextMessageEnterTransition.<init>(org.telegram.ui.Cells.ChatMessageCell, org.telegram.ui.ChatActivity, org.telegram.ui.Components.RecyclerListView, org.telegram.ui.MessageEnterTransitionContainer, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ChatActivityEnterView chatActivityEnterView, MessageEnterTransitionContainer messageEnterTransitionContainer, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chatActivityEnterView.getEditField().setAlpha(this.progress);
        messageEnterTransitionContainer.invalidate();
    }

    public void start() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    private boolean isRtlLine(Layout layout2, int i) {
        return layout2.getLineRight(i) == ((float) layout2.getWidth()) && layout2.getLineLeft(i) != 0.0f;
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x0353  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0360  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0392  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x043d  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0453  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r38) {
        /*
            r37 = this;
            r0 = r37
            r8 = r38
            boolean r1 = r0.drawBitmaps
            r9 = 1
            r10 = 0
            if (r1 == 0) goto L_0x0039
            boolean r1 = r0.initBitmaps
            if (r1 != 0) goto L_0x0039
            android.graphics.Bitmap r1 = r0.crossfadeTextBitmap
            if (r1 == 0) goto L_0x0039
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r1 = r1.getTransitionParams()
            boolean r1 = r1.wasDraw
            if (r1 == 0) goto L_0x0039
            r0.initBitmaps = r9
            android.graphics.Canvas r3 = new android.graphics.Canvas
            android.graphics.Bitmap r1 = r0.crossfadeTextBitmap
            r3.<init>(r1)
            float r1 = r0.crossfadeTextOffset
            r3.translate(r10, r1)
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            org.telegram.messenger.MessageObject r1 = r2.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r4 = r1.textLayoutBlocks
            r5 = 1
            r6 = 1065353216(0x3var_, float:1.0)
            r7 = 1
            r2.drawMessageText(r3, r4, r5, r6, r7)
        L_0x0039:
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            float r1 = r1.getY()
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            float r2 = r2.getY()
            float r1 = r1 - r2
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            int r2 = r2.getMeasuredHeight()
            float r2 = (float) r2
            float r1 = r1 + r2
            float r2 = r0.fromStartX
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getX()
            float r11 = r2 - r3
            float r2 = r0.fromStartY
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r12 = r2 - r3
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            int r2 = r2.getTextX()
            float r2 = (float) r2
            r0.textX = r2
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            int r2 = r2.getTextY()
            float r2 = (float) r2
            r0.textY = r2
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            org.telegram.messenger.MessageObject r2 = r2.getMessageObject()
            int r2 = r2.stableId
            int r3 = r0.messageId
            if (r2 == r3) goto L_0x0081
            return
        L_0x0081:
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            float r2 = r2.getX()
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            float r3 = r3.getX()
            float r2 = r2 + r3
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getX()
            float r13 = r2 - r3
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            int r2 = r2.getTop()
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            int r3 = r3.getTop()
            int r2 = r2 + r3
            float r2 = (float) r2
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r2 = r2 - r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            float r3 = r3.getTopViewHeight()
            float r14 = r2 + r3
            android.view.animation.Interpolator r2 = androidx.recyclerview.widget.ChatListItemAnimator.DEFAULT_INTERPOLATOR
            float r3 = r0.progress
            float r15 = r2.getInterpolation(r3)
            float r2 = r0.progress
            r16 = 1053609165(0x3ecccccd, float:0.4)
            r17 = 1065353216(0x3var_, float:1.0)
            int r3 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r3 <= 0) goto L_0x00c9
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x00cc
        L_0x00c9:
            float r3 = r2 / r16
            r7 = r3
        L_0x00cc:
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r3.getInterpolation(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r18 = r3.getInterpolation(r2)
            float r2 = r0.textX
            float r19 = r13 + r2
            float r2 = r0.textY
            float r20 = r14 + r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            int r2 = r2.getMeasuredHeight()
            float r2 = (float) r2
            float r21 = r17 - r18
            float r2 = r2 * r21
            float r1 = r1 * r18
            float r2 = r2 + r1
            int r6 = (int) r2
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBottom()
            r22 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = r1 - r2
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            int r2 = r2.getMeasuredHeight()
            r5 = 0
            if (r1 <= r2) goto L_0x0107
            r1 = 1
            goto L_0x0108
        L_0x0107:
            r1 = 0
        L_0x0108:
            if (r1 == 0) goto L_0x012a
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getMeasuredHeight()
            float r1 = (float) r1
            float r1 = r1 + r14
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r1 = r1 - r2
            float r2 = (float) r6
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x012a
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredHeight()
            if (r1 <= 0) goto L_0x012a
            r23 = 1
            goto L_0x012c
        L_0x012a:
            r23 = 0
        L_0x012c:
            if (r23 == 0) goto L_0x0157
            r2 = 0
            float r3 = java.lang.Math.max(r10, r14)
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredWidth()
            float r4 = (float) r1
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredHeight()
            float r1 = (float) r1
            r24 = 255(0xff, float:3.57E-43)
            r25 = 31
            r26 = r1
            r1 = r38
            r5 = r26
            r27 = r6
            r6 = r24
            r28 = r7
            r7 = r25
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x015b
        L_0x0157:
            r27 = r6
            r28 = r7
        L_0x015b:
            r38.save()
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            float r1 = r1.getY()
            org.telegram.ui.ChatActivity r2 = r0.chatActivity
            float r2 = r2.getChatListViewPadding()
            float r1 = r1 + r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            float r2 = r2.getY()
            float r1 = r1 - r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r1 = r1 - r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            int r2 = r2.getMeasuredWidth()
            float r2 = (float) r2
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            int r3 = r3.getMeasuredHeight()
            float r3 = (float) r3
            r8.clipRect(r10, r1, r2, r3)
            r38.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableLeft()
            float r1 = (float) r1
            float r1 = r1 + r13
            float r2 = r0.toXOffset
            float r2 = r19 - r2
            float r2 = r11 - r2
            float r2 = r2 * r21
            float r7 = r1 + r2
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableTop()
            float r1 = (float) r1
            float r1 = r1 + r14
            float r2 = r0.drawableFromTop
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r2 = r2 - r3
            float r24 = r17 - r15
            float r2 = r2 * r24
            float r3 = r1 * r15
            float r6 = r2 + r3
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            int r2 = r2.getBackgroundDrawableBottom()
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            int r3 = r3.getBackgroundDrawableTop()
            int r2 = r2 - r3
            float r2 = (float) r2
            float r3 = r0.drawableFromBottom
            org.telegram.ui.MessageEnterTransitionContainer r4 = r0.container
            float r4 = r4.getY()
            float r3 = r3 - r4
            float r3 = r3 * r24
            float r1 = r1 + r2
            float r1 = r1 * r15
            float r5 = r3 + r1
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableRight()
            float r1 = (float) r1
            float r1 = r1 + r13
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r2 = (float) r2
            float r2 = r2 * r21
            float r1 = r1 + r2
            int r4 = (int) r1
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r1.getCurrentBackgroundDrawable(r9)
            r25 = 1132396544(0x437var_, float:255.0)
            if (r1 == 0) goto L_0x0257
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            int r3 = r3.getTop()
            org.telegram.ui.MessageEnterTransitionContainer r10 = r0.container
            int r10 = r10.getTop()
            int r3 = r3 - r10
            r2.setBackgroundTopY((int) r3)
            android.graphics.drawable.Drawable r2 = r1.getShadowDrawable()
            r10 = r28
            int r3 = (r10 > r17 ? 1 : (r10 == r17 ? 0 : -1))
            if (r3 == 0) goto L_0x0222
            android.graphics.drawable.Drawable r3 = r0.fromMessageDrawable
            if (r3 == 0) goto L_0x0222
            int r9 = (int) r7
            r29 = r11
            int r11 = (int) r6
            r30 = r15
            int r15 = (int) r5
            r3.setBounds(r9, r11, r4, r15)
            android.graphics.drawable.Drawable r3 = r0.fromMessageDrawable
            r3.draw(r8)
            goto L_0x0226
        L_0x0222:
            r29 = r11
            r30 = r15
        L_0x0226:
            r3 = 255(0xff, float:3.57E-43)
            if (r2 == 0) goto L_0x023c
            float r9 = r18 * r25
            int r9 = (int) r9
            r2.setAlpha(r9)
            int r9 = (int) r7
            int r11 = (int) r6
            int r15 = (int) r5
            r2.setBounds(r9, r11, r4, r15)
            r2.draw(r8)
            r2.setAlpha(r3)
        L_0x023c:
            float r2 = r10 * r25
            int r2 = (int) r2
            r1.setAlpha(r2)
            int r2 = (int) r7
            int r9 = (int) r6
            int r11 = (int) r5
            r1.setBounds(r2, r9, r4, r11)
            r2 = 1
            r1.setDrawFullBubble(r2)
            r1.draw(r8)
            r9 = 0
            r1.setDrawFullBubble(r9)
            r1.setAlpha(r3)
            goto L_0x025e
        L_0x0257:
            r29 = r11
            r30 = r15
            r10 = r28
            r9 = 0
        L_0x025e:
            r38.restore()
            r38.save()
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = r1.isOutOwner()
            r11 = 1092616192(0x41200000, float:10.0)
            if (r1 == 0) goto L_0x028c
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r1 = r1 + r7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r2 = (float) r2
            float r2 = r2 + r6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r3 = r4 - r3
            float r3 = (float) r3
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r15 = (float) r15
            float r15 = r5 - r15
            r8.clipRect(r1, r2, r3, r15)
            goto L_0x02a9
        L_0x028c:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r1 = r1 + r7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r2 = (float) r2
            float r2 = r2 + r6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r3 = r4 - r3
            float r3 = (float) r3
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r15 = (float) r15
            float r15 = r5 - r15
            r8.clipRect(r1, r2, r3, r15)
        L_0x02a9:
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getLeft()
            float r1 = (float) r1
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            float r2 = r2.getX()
            float r1 = r1 + r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            float r2 = r2.getX()
            float r1 = r1 - r2
            float r2 = r12 - r20
            float r2 = r2 * r24
            float r15 = r14 + r2
            r8.translate(r1, r15)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawTime(r8, r10, r9)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawNamesLayout(r8, r10)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawCommentButton(r8, r10)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawCaptionLayout(r8, r9, r10)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawLinkPreview(r8, r10)
            r38.restore()
            boolean r1 = r0.hasReply
            if (r1 == 0) goto L_0x046f
            org.telegram.ui.ChatActivity r1 = r0.chatActivity
            org.telegram.ui.ActionBar.SimpleTextView r1 = r1.getReplyNameTextView()
            r2 = 0
            r1.setAlpha(r2)
            org.telegram.ui.ChatActivity r1 = r0.chatActivity
            org.telegram.ui.ActionBar.SimpleTextView r1 = r1.getReplyObjectTextView()
            r1.setAlpha(r2)
            float r1 = r0.replyFromStartX
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            float r2 = r2.getX()
            float r1 = r1 - r2
            float r2 = r0.replyFromStartY
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r2 = r2 - r3
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            int r9 = r3.replyStartX
            float r9 = (float) r9
            float r13 = r13 + r9
            int r3 = r3.replyStartY
            float r3 = (float) r3
            float r14 = r14 + r3
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.hasValidReplyMessageObject()
            if (r3 == 0) goto L_0x0345
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.messenger.MessageObject r3 = r3.replyMessageObject
            int r9 = r3.type
            if (r9 == 0) goto L_0x032e
            java.lang.CharSequence r3 = r3.caption
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0345
        L_0x032e:
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.messenger.MessageObject r3 = r3.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r9 != 0) goto L_0x0345
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r3 != 0) goto L_0x0345
            java.lang.String r3 = "chat_outReplyMessageText"
            int r3 = r0.getThemedColor(r3)
            goto L_0x034b
        L_0x0345:
            java.lang.String r3 = "chat_outReplyMediaMessageText"
            int r3 = r0.getThemedColor(r3)
        L_0x034b:
            org.telegram.messenger.MessageObject r9 = r0.currentMessageObject
            boolean r9 = r9.isOutOwner()
            if (r9 == 0) goto L_0x0360
            java.lang.String r9 = "chat_outReplyNameText"
            int r9 = r0.getThemedColor(r9)
            java.lang.String r11 = "chat_outReplyLine"
            int r11 = r0.getThemedColor(r11)
            goto L_0x036c
        L_0x0360:
            java.lang.String r9 = "chat_inReplyNameText"
            int r9 = r0.getThemedColor(r9)
            java.lang.String r11 = "chat_inReplyLine"
            int r11 = r0.getThemedColor(r11)
        L_0x036c:
            r31 = r4
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            r32 = r5
            int r5 = r0.replayObjectFromColor
            r33 = r15
            r15 = r30
            int r3 = androidx.core.graphics.ColorUtils.blendARGB(r5, r3, r15)
            r4.setColor(r3)
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint
            int r4 = r0.replayFromColor
            int r4 = androidx.core.graphics.ColorUtils.blendARGB(r4, r9, r15)
            r3.setColor(r4)
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            boolean r3 = r3.needReplyImage
            r9 = 1110441984(0x42300000, float:44.0)
            if (r3 == 0) goto L_0x0398
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r3 = (float) r3
            float r1 = r1 - r3
        L_0x0398:
            r30 = r1
            float r34 = r30 * r21
            float r1 = r13 * r18
            float r5 = r34 + r1
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r15
            float r2 = r2 + r1
            float r2 = r2 * r24
            float r14 = r14 * r15
            float r14 = r14 + r2
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint
            int r2 = android.graphics.Color.alpha(r11)
            float r2 = (float) r2
            float r2 = r2 * r18
            int r2 = (int) r2
            int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r11, r2)
            r1.setColor(r2)
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r4 = r5 + r1
            r11 = 1108082688(0x420CLASSNAME, float:35.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r1 = (float) r1
            float r35 = r14 + r1
            android.graphics.Paint r36 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint
            r1 = r38
            r2 = r5
            r3 = r14
            r9 = r5
            r5 = r35
            r35 = r6
            r6 = r36
            r1.drawRect(r2, r3, r4, r5, r6)
            r38.save()
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r18
            r2 = 0
            r8.translate(r1, r2)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            boolean r1 = r1.needReplyImage
            if (r1 == 0) goto L_0x0424
            r38.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.ImageReceiver r1 = r1.replyImageReceiver
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r3 = (float) r3
            r1.setImageCoords(r9, r14, r2, r3)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.ImageReceiver r1 = r1.replyImageReceiver
            r1.draw(r8)
            r8.translate(r9, r14)
            r38.restore()
            r1 = 1110441984(0x42300000, float:44.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r2 = 0
            r8.translate(r1, r2)
        L_0x0424:
            float r1 = r0.replyMessageDx
            float r2 = r13 - r1
            float r3 = r0.replyNameDx
            float r13 = r13 - r3
            float r30 = r30 - r1
            float r30 = r30 * r21
            float r2 = r2 * r18
            float r1 = r30 + r2
            float r13 = r13 * r18
            float r2 = r34 + r13
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            android.text.StaticLayout r3 = r3.replyNameLayout
            if (r3 == 0) goto L_0x044d
            r38.save()
            r8.translate(r2, r14)
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            android.text.StaticLayout r2 = r2.replyNameLayout
            r2.draw(r8)
            r38.restore()
        L_0x044d:
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            android.text.StaticLayout r2 = r2.replyTextLayout
            if (r2 == 0) goto L_0x046b
            r38.save()
            r2 = 1100480512(0x41980000, float:19.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r14 = r14 + r2
            r8.translate(r1, r14)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            android.text.StaticLayout r1 = r1.replyTextLayout
            r1.draw(r8)
            r38.restore()
        L_0x046b:
            r38.restore()
            goto L_0x0479
        L_0x046f:
            r31 = r4
            r32 = r5
            r35 = r6
            r33 = r15
            r15 = r30
        L_0x0479:
            r38.save()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r7 = r7 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r6 = r35 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r4 = r31 - r1
            float r1 = (float) r4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r2 = (float) r2
            float r5 = r32 - r2
            r8.clipRect(r7, r6, r1, r5)
            float r1 = r0.scaleFrom
            float r1 = r1 * r21
            float r1 = r18 + r1
            boolean r2 = r0.drawBitmaps
            if (r2 == 0) goto L_0x04ab
            float r2 = r0.scaleY
            float r2 = r2 * r21
            float r2 = r18 + r2
            goto L_0x04ad
        L_0x04ab:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x04ad:
            r38.save()
            float r11 = r29 * r21
            float r3 = r0.toXOffset
            float r3 = r19 - r3
            float r3 = r3 * r18
            float r3 = r3 + r11
            float r12 = r12 * r24
            org.telegram.messenger.MessageObject$TextLayoutBlock r4 = r0.textLayoutBlock
            float r4 = r4.textYOffset
            float r4 = r20 + r4
            float r4 = r4 * r15
            float r4 = r4 + r12
            r8.translate(r3, r4)
            float r2 = r2 * r1
            r3 = 0
            r8.scale(r1, r2, r3, r3)
            boolean r3 = r0.drawBitmaps
            if (r3 == 0) goto L_0x04e8
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x04df
            android.graphics.Paint r3 = r0.bitmapPaint
            float r4 = r17 - r10
            float r4 = r4 * r25
            int r4 = (int) r4
            r3.setAlpha(r4)
        L_0x04df:
            android.graphics.Bitmap r3 = r0.textLayoutBitmap
            android.graphics.Paint r4 = r0.bitmapPaint
            r5 = 0
            r8.drawBitmap(r3, r5, r5, r4)
            goto L_0x054b
        L_0x04e8:
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x0528
            boolean r4 = r0.changeColor
            if (r4 == 0) goto L_0x0528
            android.text.StaticLayout r3 = r0.layout
            android.text.TextPaint r3 = r3.getPaint()
            int r3 = r3.getColor()
            int r4 = android.graphics.Color.alpha(r3)
            android.text.StaticLayout r5 = r0.layout
            android.text.TextPaint r5 = r5.getPaint()
            int r6 = r0.fromColor
            int r7 = r0.toColor
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r7, r10)
            float r4 = (float) r4
            float r7 = r17 - r10
            float r4 = r4 * r7
            int r4 = (int) r4
            int r4 = androidx.core.graphics.ColorUtils.setAlphaComponent(r6, r4)
            r5.setColor(r4)
            android.text.StaticLayout r4 = r0.layout
            r4.draw(r8)
            android.text.StaticLayout r4 = r0.layout
            android.text.TextPaint r4 = r4.getPaint()
            r4.setColor(r3)
            goto L_0x054b
        L_0x0528:
            if (r3 == 0) goto L_0x0546
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            int r3 = r3.getAlpha()
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            float r5 = (float) r3
            float r6 = r17 - r10
            float r5 = r5 * r6
            int r5 = (int) r5
            r4.setAlpha(r5)
            android.text.StaticLayout r4 = r0.layout
            r4.draw(r8)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r4.setAlpha(r3)
            goto L_0x054b
        L_0x0546:
            android.text.StaticLayout r3 = r0.layout
            r3.draw(r8)
        L_0x054b:
            r38.restore()
            android.text.StaticLayout r3 = r0.rtlLayout
            if (r3 == 0) goto L_0x05fa
            r38.save()
            float r3 = r0.toXOffsetRtl
            float r3 = r19 - r3
            float r3 = r3 * r18
            float r11 = r11 + r3
            org.telegram.messenger.MessageObject$TextLayoutBlock r3 = r0.textLayoutBlock
            float r3 = r3.textYOffset
            float r20 = r20 + r3
            float r20 = r20 * r15
            float r12 = r12 + r20
            r8.translate(r11, r12)
            r3 = 0
            r8.scale(r1, r2, r3, r3)
            boolean r3 = r0.drawBitmaps
            if (r3 == 0) goto L_0x0588
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x057f
            android.graphics.Paint r3 = r0.bitmapPaint
            float r4 = r17 - r10
            float r4 = r4 * r25
            int r4 = (int) r4
            r3.setAlpha(r4)
        L_0x057f:
            android.graphics.Bitmap r3 = r0.textLayoutBitmapRtl
            android.graphics.Paint r4 = r0.bitmapPaint
            r5 = 0
            r8.drawBitmap(r3, r5, r5, r4)
            goto L_0x05f7
        L_0x0588:
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x05c8
            boolean r4 = r0.changeColor
            if (r4 == 0) goto L_0x05c8
            android.text.StaticLayout r3 = r0.rtlLayout
            android.text.TextPaint r3 = r3.getPaint()
            int r3 = r3.getColor()
            int r4 = android.graphics.Color.alpha(r3)
            android.text.StaticLayout r5 = r0.rtlLayout
            android.text.TextPaint r5 = r5.getPaint()
            int r6 = r0.fromColor
            int r7 = r0.toColor
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r7, r10)
            float r4 = (float) r4
            float r7 = r17 - r10
            float r4 = r4 * r7
            int r4 = (int) r4
            int r4 = androidx.core.graphics.ColorUtils.setAlphaComponent(r6, r4)
            r5.setColor(r4)
            android.text.StaticLayout r4 = r0.rtlLayout
            r4.draw(r8)
            android.text.StaticLayout r4 = r0.rtlLayout
            android.text.TextPaint r4 = r4.getPaint()
            r4.setColor(r3)
            goto L_0x05f7
        L_0x05c8:
            if (r3 == 0) goto L_0x05f2
            android.text.StaticLayout r3 = r0.rtlLayout
            android.text.TextPaint r3 = r3.getPaint()
            int r3 = r3.getAlpha()
            android.text.StaticLayout r4 = r0.rtlLayout
            android.text.TextPaint r4 = r4.getPaint()
            float r5 = (float) r3
            float r6 = r17 - r10
            float r5 = r5 * r6
            int r5 = (int) r5
            r4.setAlpha(r5)
            android.text.StaticLayout r4 = r0.rtlLayout
            r4.draw(r8)
            android.text.StaticLayout r4 = r0.rtlLayout
            android.text.TextPaint r4 = r4.getPaint()
            r4.setAlpha(r3)
            goto L_0x05f7
        L_0x05f2:
            android.text.StaticLayout r3 = r0.rtlLayout
            r3.draw(r8)
        L_0x05f7:
            r38.restore()
        L_0x05fa:
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x0679
            r38.save()
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            int r3 = r3.getLeft()
            float r3 = (float) r3
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            float r4 = r4.getX()
            float r3 = r3 + r4
            org.telegram.ui.MessageEnterTransitionContainer r4 = r0.container
            float r4 = r4.getX()
            float r3 = r3 - r4
            float r11 = r29 - r19
            float r11 = r11 * r21
            float r3 = r3 + r11
            r14 = r33
            r8.translate(r3, r14)
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            int r3 = r3.getTextX()
            float r3 = (float) r3
            org.telegram.ui.Cells.ChatMessageCell r4 = r0.messageView
            int r4 = r4.getTextY()
            float r4 = (float) r4
            r8.scale(r1, r2, r3, r4)
            float r1 = r0.crossfadeTextOffset
            float r1 = -r1
            r2 = 0
            r8.translate(r2, r1)
            android.graphics.Bitmap r1 = r0.crossfadeTextBitmap
            if (r1 == 0) goto L_0x064c
            android.graphics.Paint r1 = r0.bitmapPaint
            float r7 = r10 * r25
            int r3 = (int) r7
            r1.setAlpha(r3)
            android.graphics.Bitmap r1 = r0.crossfadeTextBitmap
            android.graphics.Paint r3 = r0.bitmapPaint
            r8.drawBitmap(r1, r2, r2, r3)
            goto L_0x0676
        L_0x064c:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            int r7 = r1.getColor()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            int r2 = r0.toColor
            r1.setColor(r2)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.MessageObject r2 = r1.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r3 = r2.textLayoutBlocks
            r4 = 0
            r6 = 1
            r2 = r38
            r5 = r10
            r1.drawMessageText(r2, r3, r4, r5, r6)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            int r1 = r1.getColor()
            if (r1 == r7) goto L_0x0676
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r1.setColor(r7)
        L_0x0676:
            r38.restore()
        L_0x0679:
            r38.restore()
            if (r23 == 0) goto L_0x06a7
            android.graphics.Matrix r1 = r0.gradientMatrix
            r2 = r27
            float r3 = (float) r2
            r2 = 0
            r1.setTranslate(r2, r3)
            android.graphics.LinearGradient r1 = r0.gradientShader
            android.graphics.Matrix r2 = r0.gradientMatrix
            r1.setLocalMatrix(r2)
            r2 = 0
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredWidth()
            float r4 = (float) r1
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredHeight()
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.gradientPaint
            r1 = r38
            r1.drawRect(r2, r3, r4, r5, r6)
            r38.restore()
        L_0x06a7:
            float r1 = r0.progress
            int r2 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x06b0
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x06b2
        L_0x06b0:
            float r1 = r1 / r16
        L_0x06b2:
            int r2 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1))
            if (r2 != 0) goto L_0x06bc
            org.telegram.ui.Components.ChatActivityEnterView r2 = r0.enterView
            r3 = 0
            r2.setTextTransitionIsRunning(r3)
        L_0x06bc:
            org.telegram.ui.Components.ChatActivityEnterView r2 = r0.enterView
            android.view.View r2 = r2.getSendButton()
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x0767
            int r2 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1))
            if (r2 >= 0) goto L_0x0767
            r38.save()
            org.telegram.ui.Components.ChatActivityEnterView r2 = r0.enterView
            float r2 = r2.getX()
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            float r3 = r3.getX()
            float r2 = r2 + r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getX()
            float r2 = r2 + r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getX()
            float r2 = r2 + r3
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getX()
            float r2 = r2 - r3
            r3 = 1112539136(0x42500000, float:52.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r1
            float r2 = r2 + r3
            org.telegram.ui.Components.ChatActivityEnterView r1 = r0.enterView
            float r1 = r1.getY()
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            float r3 = r3.getY()
            float r1 = r1 + r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r1 = r1 + r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r1 = r1 + r3
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r1 = r1 - r3
            r8.translate(r2, r1)
            org.telegram.ui.Components.ChatActivityEnterView r1 = r0.enterView
            android.view.View r1 = r1.getSendButton()
            r1.draw(r8)
            r38.restore()
            r38.restore()
        L_0x0767:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TextMessageEnterTransition.onDraw(android.graphics.Canvas):void");
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
