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
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MessageEnterTransitionContainer;

public class TextMessageEnterTransition implements MessageEnterTransitionContainer.Transition {
    /* access modifiers changed from: private */
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack;
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

    /* JADX WARNING: Removed duplicated region for block: B:104:0x044a A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x04d7  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x04d9  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x04de  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0655  */
    /* JADX WARNING: Removed duplicated region for block: B:160:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0150  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x018b  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01c9  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x01e7  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0297  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x02a9  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0318  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0335  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0363  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0380  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x03cb  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0405  */
    @android.annotation.SuppressLint({"WrongConstant"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TextMessageEnterTransition(org.telegram.ui.Cells.ChatMessageCell r25, org.telegram.ui.ChatActivity r26, org.telegram.ui.Components.RecyclerListView r27, org.telegram.ui.MessageEnterTransitionContainer r28, org.telegram.ui.ActionBar.Theme.ResourcesProvider r29) {
        /*
            r24 = this;
            r6 = r24
            r7 = r25
            r2 = r28
            r24.<init>()
            android.graphics.Paint r0 = new android.graphics.Paint
            r8 = 1
            r0.<init>(r8)
            r6.bitmapPaint = r0
            r0 = 0
            r6.initBitmaps = r0
            r6.drawBitmaps = r0
            r1 = -1
            r6.animationIndex = r1
            r1 = r29
            r6.resourcesProvider = r1
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r6.currentAccount = r1
            org.telegram.messenger.MessageObject r1 = r25.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            if (r1 == 0) goto L_0x0661
            org.telegram.messenger.MessageObject r1 = r25.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            int r1 = r1.size()
            if (r1 > r8) goto L_0x0661
            org.telegram.messenger.MessageObject r1 = r25.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0661
            org.telegram.messenger.MessageObject r1 = r25.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            java.lang.Object r1 = r1.get(r0)
            org.telegram.messenger.MessageObject$TextLayoutBlock r1 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r1
            android.text.StaticLayout r1 = r1.textLayout
            int r1 = r1.getLineCount()
            r3 = 10
            if (r1 <= r3) goto L_0x0059
            goto L_0x0661
        L_0x0059:
            r6.messageView = r7
            r1 = r27
            r6.listView = r1
            r6.container = r2
            r5 = r26
            r6.chatActivity = r5
            org.telegram.ui.Components.ChatActivityEnterView r3 = r26.getChatActivityEnterView()
            r6.enterView = r3
            org.telegram.ui.Components.ChatActivityEnterView r4 = r26.getChatActivityEnterView()
            if (r4 == 0) goto L_0x0661
            org.telegram.ui.Components.EditTextCaption r3 = r4.getEditField()
            if (r3 == 0) goto L_0x0661
            org.telegram.ui.Components.EditTextCaption r3 = r4.getEditField()
            android.text.Layout r3 = r3.getLayout()
            if (r3 != 0) goto L_0x0083
            goto L_0x0661
        L_0x0083:
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r3 = r4.getRecordCicle()
            float r3 = r3.drawingCircleRadius
            android.graphics.Paint r3 = r6.bitmapPaint
            r3.setFilterBitmap(r8)
            org.telegram.messenger.MessageObject r3 = r25.getMessageObject()
            r6.currentMessageObject = r3
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r3 = r25.getTransitionParams()
            boolean r3 = r3.wasDraw
            if (r3 != 0) goto L_0x00a4
            android.graphics.Canvas r3 = new android.graphics.Canvas
            r3.<init>()
            r7.draw(r3)
        L_0x00a4:
            r7.setEnterTransitionInProgress(r8)
            org.telegram.ui.Components.EditTextCaption r3 = r4.getEditField()
            android.text.Layout r3 = r3.getLayout()
            java.lang.CharSequence r3 = r3.getText()
            org.telegram.messenger.MessageObject r9 = r25.getMessageObject()
            java.lang.CharSequence r9 = r9.messageText
            r6.crossfade = r0
            org.telegram.ui.Components.EditTextCaption r10 = r4.getEditField()
            android.text.Layout r10 = r10.getLayout()
            int r10 = r10.getHeight()
            android.text.TextPaint r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r12 = 1101004800(0x41a00000, float:20.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            org.telegram.messenger.MessageObject r13 = r25.getMessageObject()
            int r13 = r13.getEmojiOnlyCount()
            r14 = 1082130432(0x40800000, float:4.0)
            r15 = 2
            if (r13 == 0) goto L_0x0114
            org.telegram.messenger.MessageObject r11 = r25.getMessageObject()
            int r11 = r11.getEmojiOnlyCount()
            switch(r11) {
                case 0: goto L_0x0103;
                case 1: goto L_0x0103;
                case 2: goto L_0x0103;
                case 3: goto L_0x00fe;
                case 4: goto L_0x00f9;
                case 5: goto L_0x00f3;
                case 6: goto L_0x00ed;
                default: goto L_0x00e7;
            }
        L_0x00e7:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintEmoji
            r13 = 5
            r11 = r11[r13]
            goto L_0x0107
        L_0x00ed:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintEmoji
            r13 = 4
            r11 = r11[r13]
            goto L_0x0107
        L_0x00f3:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintEmoji
            r13 = 3
            r11 = r11[r13]
            goto L_0x0107
        L_0x00f9:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintEmoji
            r11 = r11[r15]
            goto L_0x0107
        L_0x00fe:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintEmoji
            r11 = r11[r8]
            goto L_0x0107
        L_0x0103:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintEmoji
            r11 = r11[r0]
        L_0x0107:
            if (r11 == 0) goto L_0x0114
            float r12 = r11.getTextSize()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r13 = (float) r13
            float r12 = r12 + r13
            int r12 = (int) r12
        L_0x0114:
            boolean r13 = r9 instanceof android.text.Spannable
            if (r13 == 0) goto L_0x0134
            r13 = r9
            android.text.Spannable r13 = (android.text.Spannable) r13
            int r14 = r9.length()
            java.lang.Class<java.lang.Object> r15 = java.lang.Object.class
            java.lang.Object[] r13 = r13.getSpans(r0, r14, r15)
            r14 = 0
        L_0x0126:
            int r15 = r13.length
            if (r14 >= r15) goto L_0x0134
            r15 = r13[r14]
            boolean r15 = r15 instanceof org.telegram.messenger.Emoji.EmojiSpan
            if (r15 != 0) goto L_0x0131
            r13 = 1
            goto L_0x0135
        L_0x0131:
            int r14 = r14 + 1
            goto L_0x0126
        L_0x0134:
            r13 = 0
        L_0x0135:
            int r14 = r3.length()
            int r15 = r9.length()
            if (r14 != r15) goto L_0x0144
            if (r13 == 0) goto L_0x0142
            goto L_0x0144
        L_0x0142:
            r8 = 0
            goto L_0x0197
        L_0x0144:
            r6.crossfade = r8
            int[] r13 = new int[r8]
            java.lang.CharSequence r14 = org.telegram.messenger.AndroidUtilities.trim(r3, r13)
            r15 = r13[r0]
            if (r15 <= 0) goto L_0x018b
            org.telegram.ui.Components.EditTextCaption r10 = r4.getEditField()
            android.text.Layout r10 = r10.getLayout()
            org.telegram.ui.Components.EditTextCaption r15 = r4.getEditField()
            android.text.Layout r15 = r15.getLayout()
            r8 = r13[r0]
            int r8 = r15.getLineForOffset(r8)
            int r8 = r10.getLineTop(r8)
            org.telegram.ui.Components.EditTextCaption r10 = r4.getEditField()
            android.text.Layout r10 = r10.getLayout()
            org.telegram.ui.Components.EditTextCaption r15 = r4.getEditField()
            android.text.Layout r15 = r15.getLayout()
            r13 = r13[r0]
            int r14 = r14.length()
            int r13 = r13 + r14
            int r13 = r15.getLineForOffset(r13)
            int r10 = r10.getLineBottom(r13)
            int r10 = r10 - r8
            goto L_0x018c
        L_0x018b:
            r8 = 0
        L_0x018c:
            org.telegram.ui.Components.AnimatedEmojiSpan.cloneSpans(r9)
            android.graphics.Paint$FontMetricsInt r9 = r11.getFontMetricsInt()
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r3, r9, r12, r0)
        L_0x0197:
            org.telegram.ui.Components.EditTextCaption r3 = r4.getEditField()
            float r3 = r3.getTextSize()
            float r12 = r11.getTextSize()
            float r3 = r3 / r12
            r6.scaleFrom = r3
            org.telegram.ui.Components.EditTextCaption r3 = r4.getEditField()
            android.text.Layout r3 = r3.getLayout()
            int r3 = r3.getLineCount()
            org.telegram.ui.Components.EditTextCaption r12 = r4.getEditField()
            android.text.Layout r12 = r12.getLayout()
            int r12 = r12.getWidth()
            float r12 = (float) r12
            float r13 = r6.scaleFrom
            float r12 = r12 / r13
            int r12 = (int) r12
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 24
            if (r13 < r14) goto L_0x01e7
            int r13 = r9.length()
            android.text.StaticLayout$Builder r13 = android.text.StaticLayout.Builder.obtain(r9, r0, r13, r11, r12)
            r15 = 1
            android.text.StaticLayout$Builder r13 = r13.setBreakStrategy(r15)
            android.text.StaticLayout$Builder r13 = r13.setHyphenationFrequency(r0)
            android.text.Layout$Alignment r15 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r13 = r13.setAlignment(r15)
            android.text.StaticLayout r13 = r13.build()
            r6.layout = r13
            goto L_0x01fe
        L_0x01e7:
            android.text.StaticLayout r13 = new android.text.StaticLayout
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r13
            r17 = r9
            r18 = r11
            r19 = r12
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r6.layout = r13
        L_0x01fe:
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r13 = r6.animatedEmojiStack
            r15 = 1
            android.text.Layout[] r14 = new android.text.Layout[r15]
            android.text.StaticLayout r15 = r6.layout
            r14[r0] = r15
            r15 = 0
            r0 = 2
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r13 = org.telegram.ui.Components.AnimatedEmojiSpan.update((int) r0, (android.view.View) r15, (org.telegram.ui.Components.AnimatedEmojiSpan.EmojiGroupedSpans) r13, (android.text.Layout[]) r14)
            r6.animatedEmojiStack = r13
            float r0 = r4.getY()
            org.telegram.ui.Components.EditTextCaption r13 = r4.getEditField()
            float r13 = r13.getY()
            float r0 = r0 + r13
            org.telegram.ui.Components.EditTextCaption r13 = r4.getEditField()
            android.view.ViewParent r13 = r13.getParent()
            android.view.View r13 = (android.view.View) r13
            float r13 = r13.getY()
            float r0 = r0 + r13
            org.telegram.ui.Components.EditTextCaption r13 = r4.getEditField()
            android.view.ViewParent r13 = r13.getParent()
            android.view.ViewParent r13 = r13.getParent()
            android.view.View r13 = (android.view.View) r13
            float r13 = r13.getY()
            float r0 = r0 + r13
            float r13 = r4.getX()
            org.telegram.ui.Components.EditTextCaption r14 = r4.getEditField()
            float r14 = r14.getX()
            float r13 = r13 + r14
            org.telegram.ui.Components.EditTextCaption r14 = r4.getEditField()
            android.view.ViewParent r14 = r14.getParent()
            android.view.View r14 = (android.view.View) r14
            float r14 = r14.getX()
            float r13 = r13 + r14
            org.telegram.ui.Components.EditTextCaption r14 = r4.getEditField()
            android.view.ViewParent r14 = r14.getParent()
            android.view.ViewParent r14 = r14.getParent()
            android.view.View r14 = (android.view.View) r14
            float r14 = r14.getX()
            float r13 = r13 + r14
            r6.fromStartX = r13
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r13 = r13 + r0
            org.telegram.ui.Components.EditTextCaption r14 = r4.getEditField()
            int r14 = r14.getScrollY()
            float r14 = (float) r14
            float r13 = r13 - r14
            float r8 = (float) r8
            float r13 = r13 + r8
            r6.fromStartY = r13
            r8 = 0
            r6.toXOffset = r8
            r13 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r14 = 0
            r15 = 2139095039(0x7f7fffff, float:3.4028235E38)
        L_0x028f:
            android.text.StaticLayout r8 = r6.layout
            int r8 = r8.getLineCount()
            if (r14 >= r8) goto L_0x02a5
            android.text.StaticLayout r8 = r6.layout
            float r8 = r8.getLineLeft(r14)
            int r17 = (r8 > r15 ? 1 : (r8 == r15 ? 0 : -1))
            if (r17 >= 0) goto L_0x02a2
            r15 = r8
        L_0x02a2:
            int r14 = r14 + 1
            goto L_0x028f
        L_0x02a5:
            int r8 = (r15 > r13 ? 1 : (r15 == r13 ? 0 : -1))
            if (r8 == 0) goto L_0x02ab
            r6.toXOffset = r15
        L_0x02ab:
            float r8 = (float) r10
            android.text.StaticLayout r10 = r6.layout
            int r10 = r10.getHeight()
            float r10 = (float) r10
            float r14 = r6.scaleFrom
            float r10 = r10 * r14
            float r8 = r8 / r10
            r6.scaleY = r8
            r8 = 1082130432(0x40800000, float:4.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            float r8 = r8 + r0
            r6.drawableFromTop = r8
            org.telegram.ui.Components.ChatActivityEnterView r8 = r6.enterView
            boolean r8 = r8.isTopViewVisible()
            r10 = 1094713344(0x41400000, float:12.0)
            if (r8 == 0) goto L_0x02d8
            float r8 = r6.drawableFromTop
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r14 = (float) r14
            float r8 = r8 - r14
            r6.drawableFromTop = r8
        L_0x02d8:
            org.telegram.ui.Components.EditTextCaption r8 = r4.getEditField()
            int r8 = r8.getMeasuredHeight()
            float r8 = (float) r8
            float r0 = r0 + r8
            r6.drawableFromBottom = r0
            org.telegram.messenger.MessageObject r0 = r25.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r0.textLayoutBlocks
            r8 = 0
            java.lang.Object r0 = r0.get(r8)
            org.telegram.messenger.MessageObject$TextLayoutBlock r0 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r0
            r6.textLayoutBlock = r0
            android.text.StaticLayout r0 = r0.textLayout
            java.lang.String r8 = "chat_messageTextOut"
            int r14 = r6.getThemedColor(r8)
            double r14 = androidx.core.graphics.ColorUtils.calculateLuminance(r14)
            java.lang.String r13 = "chat_messagePanelText"
            int r17 = r6.getThemedColor(r13)
            double r17 = androidx.core.graphics.ColorUtils.calculateLuminance(r17)
            double r14 = r14 - r17
            double r14 = java.lang.Math.abs(r14)
            r17 = 4596373779801702400(0x3fCLASSNAMEa0000000, double:0.NUM)
            int r19 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1))
            if (r19 <= 0) goto L_0x031d
            r14 = 1
            r6.crossfade = r14
            r6.changeColor = r14
        L_0x031d:
            int r13 = r6.getThemedColor(r13)
            r6.fromColor = r13
            int r8 = r6.getThemedColor(r8)
            r6.toColor = r8
            int r8 = r0.getLineCount()
            android.text.StaticLayout r13 = r6.layout
            int r13 = r13.getLineCount()
            if (r8 != r13) goto L_0x0363
            int r3 = r0.getLineCount()
            r8 = 0
            r13 = 0
            r14 = 0
        L_0x033c:
            if (r8 >= r3) goto L_0x0361
            android.text.StaticLayout r15 = r6.layout
            boolean r15 = r6.isRtlLine(r15, r8)
            if (r15 == 0) goto L_0x0349
            int r14 = r14 + 1
            goto L_0x034b
        L_0x0349:
            int r13 = r13 + 1
        L_0x034b:
            int r15 = r0.getLineEnd(r8)
            android.text.StaticLayout r10 = r6.layout
            int r10 = r10.getLineEnd(r8)
            if (r15 == r10) goto L_0x035b
            r10 = 1
            r6.crossfade = r10
            goto L_0x0368
        L_0x035b:
            r10 = 1
            int r8 = r8 + 1
            r10 = 1094713344(0x41400000, float:12.0)
            goto L_0x033c
        L_0x0361:
            r10 = 1
            goto L_0x0368
        L_0x0363:
            r10 = 1
            r6.crossfade = r10
            r13 = 0
            r14 = 0
        L_0x0368:
            boolean r0 = r6.crossfade
            if (r0 != 0) goto L_0x0429
            if (r14 <= 0) goto L_0x0429
            if (r13 <= 0) goto L_0x0429
            android.text.SpannableString r0 = new android.text.SpannableString
            r0.<init>(r9)
            android.text.SpannableString r8 = new android.text.SpannableString
            r8.<init>(r9)
            r9 = 0
            r13 = 2139095039(0x7f7fffff, float:3.4028235E38)
        L_0x037e:
            if (r9 >= r3) goto L_0x03c4
            android.text.StaticLayout r10 = r6.layout
            boolean r10 = r6.isRtlLine(r10, r9)
            if (r10 == 0) goto L_0x03aa
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            android.text.StaticLayout r14 = r6.layout
            int r14 = r14.getLineStart(r9)
            android.text.StaticLayout r15 = r6.layout
            int r15 = r15.getLineEnd(r9)
            r1 = 0
            r0.setSpan(r10, r14, r15, r1)
            android.text.StaticLayout r1 = r6.layout
            float r1 = r1.getLineLeft(r9)
            int r10 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r10 >= 0) goto L_0x03a8
            r13 = r1
        L_0x03a8:
            r15 = 0
            goto L_0x03bf
        L_0x03aa:
            org.telegram.ui.Components.EmptyStubSpan r1 = new org.telegram.ui.Components.EmptyStubSpan
            r1.<init>()
            android.text.StaticLayout r10 = r6.layout
            int r10 = r10.getLineStart(r9)
            android.text.StaticLayout r14 = r6.layout
            int r14 = r14.getLineEnd(r9)
            r15 = 0
            r8.setSpan(r1, r10, r14, r15)
        L_0x03bf:
            int r9 = r9 + 1
            r1 = r27
            goto L_0x037e
        L_0x03c4:
            r15 = 0
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 24
            if (r1 < r3) goto L_0x0405
            int r1 = r0.length()
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r15, r1, r11, r12)
            r1 = 1
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r1)
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r15)
            android.text.Layout$Alignment r3 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r3)
            android.text.StaticLayout r0 = r0.build()
            r6.layout = r0
            int r0 = r8.length()
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r8, r15, r0, r11, r12)
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r1)
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r15)
            android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r1)
            android.text.StaticLayout r0 = r0.build()
            r6.rtlLayout = r0
            goto L_0x0429
        L_0x0405:
            android.text.StaticLayout r1 = new android.text.StaticLayout
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r1
            r17 = r0
            r18 = r11
            r19 = r12
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r6.layout = r1
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL
            r16 = r0
            r17 = r8
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r6.rtlLayout = r0
        L_0x0429:
            android.text.StaticLayout r0 = r6.layout
            int r0 = r0.getWidth()
            org.telegram.messenger.MessageObject r1 = r25.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject$TextLayoutBlock r1 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r1
            android.text.StaticLayout r1 = r1.textLayout
            int r1 = r1.getWidth()
            int r0 = r0 - r1
            float r0 = (float) r0
            r6.toXOffsetRtl = r0
            boolean r0 = r6.drawBitmaps     // Catch:{ Exception -> 0x04c6 }
            if (r0 == 0) goto L_0x04c9
            android.text.StaticLayout r0 = r6.layout     // Catch:{ Exception -> 0x04c6 }
            int r0 = r0.getWidth()     // Catch:{ Exception -> 0x04c6 }
            android.text.StaticLayout r1 = r6.layout     // Catch:{ Exception -> 0x04c6 }
            int r1 = r1.getHeight()     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x04c6 }
            r6.textLayoutBitmap = r0     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap r1 = r6.textLayoutBitmap     // Catch:{ Exception -> 0x04c6 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x04c6 }
            android.text.StaticLayout r1 = r6.layout     // Catch:{ Exception -> 0x04c6 }
            r1.draw(r0)     // Catch:{ Exception -> 0x04c6 }
            android.text.StaticLayout r0 = r6.rtlLayout     // Catch:{ Exception -> 0x04c6 }
            if (r0 == 0) goto L_0x048c
            int r0 = r0.getWidth()     // Catch:{ Exception -> 0x04c6 }
            android.text.StaticLayout r1 = r6.rtlLayout     // Catch:{ Exception -> 0x04c6 }
            int r1 = r1.getHeight()     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x04c6 }
            r6.textLayoutBitmapRtl = r0     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap r1 = r6.textLayoutBitmapRtl     // Catch:{ Exception -> 0x04c6 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x04c6 }
            android.text.StaticLayout r1 = r6.rtlLayout     // Catch:{ Exception -> 0x04c6 }
            r1.draw(r0)     // Catch:{ Exception -> 0x04c6 }
        L_0x048c:
            boolean r0 = r6.crossfade     // Catch:{ Exception -> 0x04c6 }
            if (r0 == 0) goto L_0x04c9
            int r0 = r25.getMeasuredHeight()     // Catch:{ Exception -> 0x04c6 }
            int r1 = r27.getMeasuredHeight()     // Catch:{ Exception -> 0x04c6 }
            if (r0 >= r1) goto L_0x04ae
            r0 = 0
            r6.crossfadeTextOffset = r0     // Catch:{ Exception -> 0x04c6 }
            int r0 = r25.getMeasuredWidth()     // Catch:{ Exception -> 0x04c6 }
            int r1 = r25.getMeasuredHeight()     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x04c6 }
            r6.crossfadeTextBitmap = r0     // Catch:{ Exception -> 0x04c6 }
            goto L_0x04c9
        L_0x04ae:
            int r0 = r25.getTop()     // Catch:{ Exception -> 0x04c6 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x04c6 }
            r6.crossfadeTextOffset = r0     // Catch:{ Exception -> 0x04c6 }
            int r0 = r25.getMeasuredWidth()     // Catch:{ Exception -> 0x04c6 }
            int r1 = r27.getMeasuredHeight()     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x04c6 }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x04c6 }
            r6.crossfadeTextBitmap = r0     // Catch:{ Exception -> 0x04c6 }
            goto L_0x04c9
        L_0x04c6:
            r0 = 0
            r6.drawBitmaps = r0
        L_0x04c9:
            org.telegram.messenger.MessageObject r0 = r25.getMessageObject()
            int r0 = r0.getReplyMsgId()
            if (r0 == 0) goto L_0x04d9
            android.text.StaticLayout r0 = r7.replyNameLayout
            if (r0 == 0) goto L_0x04d9
            r0 = 1
            goto L_0x04da
        L_0x04d9:
            r0 = 0
        L_0x04da:
            r6.hasReply = r0
            if (r0 == 0) goto L_0x0560
            org.telegram.ui.ActionBar.SimpleTextView r0 = r26.getReplyNameTextView()
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
            org.telegram.ui.ActionBar.SimpleTextView r0 = r26.getReplyObjectTextView()
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
            org.telegram.ui.ActionBar.SimpleTextView r0 = r26.getReplyNameTextView()
            int r0 = r0.getTextColor()
            r6.replayFromColor = r0
            org.telegram.ui.ActionBar.SimpleTextView r0 = r26.getReplyObjectTextView()
            int r0 = r0.getTextColor()
            r6.replayObjectFromColor = r0
            float r0 = r6.drawableFromTop
            r1 = 1110966272(0x42380000, float:46.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r0 = r0 - r1
            r6.drawableFromTop = r0
        L_0x0560:
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
            org.telegram.messenger.MessageObject r0 = r25.getMessageObject()
            int r0 = r0.stableId
            r6.messageId = r0
            org.telegram.ui.Components.EditTextCaption r0 = r4.getEditField()
            r1 = 0
            r0.setAlpha(r1)
            r0 = 1
            r4.setTextTransitionIsRunning(r0)
            android.text.StaticLayout r3 = r7.replyNameLayout
            if (r3 == 0) goto L_0x05d2
            java.lang.CharSequence r3 = r3.getText()
            int r3 = r3.length()
            if (r3 <= r0) goto L_0x05d2
            android.text.StaticLayout r0 = r7.replyNameLayout
            r3 = 0
            float r0 = r0.getPrimaryHorizontal(r3)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x05d2
            android.text.StaticLayout r0 = r7.replyNameLayout
            int r0 = r0.getWidth()
            float r0 = (float) r0
            android.text.StaticLayout r1 = r7.replyNameLayout
            float r1 = r1.getLineWidth(r3)
            float r0 = r0 - r1
            r6.replyNameDx = r0
        L_0x05d2:
            android.text.StaticLayout r0 = r7.replyTextLayout
            if (r0 == 0) goto L_0x05fd
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            r1 = 1
            if (r0 < r1) goto L_0x05fd
            android.text.StaticLayout r0 = r7.replyTextLayout
            r1 = 0
            float r0 = r0.getPrimaryHorizontal(r1)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x05fd
            android.text.StaticLayout r0 = r7.replyTextLayout
            int r0 = r0.getWidth()
            float r0 = (float) r0
            android.text.StaticLayout r3 = r7.replyTextLayout
            float r1 = r3.getLineWidth(r1)
            float r0 = r0 - r1
            r6.replyMessageDx = r0
        L_0x05fd:
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
            r1 = r24
            r2 = r28
            r3 = r25
            r5 = r26
            r0.<init>(r2, r3, r4, r5)
            r8.addListener(r9)
            int r0 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
            r1 = 2
            if (r0 != r1) goto L_0x0661
            r0 = 1
            org.telegram.ui.ActionBar.Theme$MessageDrawable r0 = r7.getCurrentBackgroundDrawable(r0)
            if (r0 == 0) goto L_0x0661
            java.lang.String r1 = "chat_messagePanelBackground"
            int r1 = r6.getThemedColor(r1)
            android.graphics.drawable.Drawable r0 = r0.getTransitionDrawable(r1)
            r6.fromMessageDrawable = r0
        L_0x0661:
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

    /* JADX WARNING: Removed duplicated region for block: B:71:0x0373  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0380  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x03b2  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0418  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x045d  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0473  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x04df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r39) {
        /*
            r38 = this;
            r0 = r38
            r10 = r39
            boolean r1 = r0.drawBitmaps
            r8 = 1
            r11 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            if (r1 == 0) goto L_0x004a
            boolean r1 = r0.initBitmaps
            if (r1 != 0) goto L_0x004a
            android.graphics.Bitmap r1 = r0.crossfadeTextBitmap
            if (r1 == 0) goto L_0x004a
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r1 = r1.getTransitionParams()
            boolean r1 = r1.wasDraw
            if (r1 == 0) goto L_0x004a
            r0.initBitmaps = r8
            android.graphics.Canvas r1 = new android.graphics.Canvas
            android.graphics.Bitmap r2 = r0.crossfadeTextBitmap
            r1.<init>(r2)
            float r2 = r0.crossfadeTextOffset
            r1.translate(r12, r2)
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r2 = r2.animatedEmojiStack
            if (r2 == 0) goto L_0x0035
            r2.clearPositions()
        L_0x0035:
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            org.telegram.messenger.MessageObject r3 = r2.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r4 = r3.textLayoutBlocks
            r5 = 1
            r6 = 1065353216(0x3var_, float:1.0)
            r7 = 1
            r3 = r1
            r2.drawMessageText(r3, r4, r5, r6, r7)
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            r2.drawAnimatedEmojis(r1, r11)
        L_0x004a:
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
            float r13 = r2 - r3
            float r2 = r0.fromStartY
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r14 = r2 - r3
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
            if (r2 == r3) goto L_0x0092
            return
        L_0x0092:
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            float r2 = r2.getX()
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            float r3 = r3.getX()
            float r2 = r2 + r3
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getX()
            float r9 = r2 - r3
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
            float r15 = r2 + r3
            android.view.animation.Interpolator r2 = androidx.recyclerview.widget.ChatListItemAnimator.DEFAULT_INTERPOLATOR
            float r3 = r0.progress
            float r7 = r2.getInterpolation(r3)
            float r2 = r0.progress
            r16 = 1053609165(0x3ecccccd, float:0.4)
            int r3 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r3 <= 0) goto L_0x00d8
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x00db
        L_0x00d8:
            float r3 = r2 / r16
            r6 = r3
        L_0x00db:
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r3.getInterpolation(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r17 = r3.getInterpolation(r2)
            float r2 = r0.textX
            float r18 = r9 + r2
            float r2 = r0.textY
            float r19 = r15 + r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            int r2 = r2.getMeasuredHeight()
            float r2 = (float) r2
            float r20 = r11 - r17
            float r2 = r2 * r20
            float r1 = r1 * r17
            float r2 = r2 + r1
            int r5 = (int) r2
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBottom()
            r21 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r1 = r1 - r2
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            int r2 = r2.getMeasuredHeight()
            if (r1 <= r2) goto L_0x0115
            r1 = 1
            goto L_0x0116
        L_0x0115:
            r1 = 0
        L_0x0116:
            if (r1 == 0) goto L_0x0138
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getMeasuredHeight()
            float r1 = (float) r1
            float r1 = r1 + r15
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r1 = r1 - r2
            float r2 = (float) r5
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x0138
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredHeight()
            if (r1 <= 0) goto L_0x0138
            r22 = 1
            goto L_0x013a
        L_0x0138:
            r22 = 0
        L_0x013a:
            if (r22 == 0) goto L_0x016a
            r2 = 0
            float r3 = java.lang.Math.max(r12, r15)
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredWidth()
            float r1 = (float) r1
            org.telegram.ui.MessageEnterTransitionContainer r4 = r0.container
            int r4 = r4.getMeasuredHeight()
            float r4 = (float) r4
            r24 = 255(0xff, float:3.57E-43)
            r25 = 31
            r26 = r1
            r1 = r39
            r23 = r4
            r4 = r26
            r27 = r5
            r5 = r23
            r28 = r6
            r6 = r24
            r8 = r7
            r7 = r25
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x016f
        L_0x016a:
            r27 = r5
            r28 = r6
            r8 = r7
        L_0x016f:
            r39.save()
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
            r10.clipRect(r12, r1, r2, r3)
            r39.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableLeft()
            float r1 = (float) r1
            float r1 = r1 + r9
            float r2 = r0.toXOffset
            float r2 = r18 - r2
            float r2 = r13 - r2
            float r2 = r2 * r20
            float r7 = r1 + r2
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableTop()
            float r1 = (float) r1
            float r1 = r1 + r15
            float r2 = r0.drawableFromTop
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r2 = r2 - r3
            float r24 = r11 - r8
            float r2 = r2 * r24
            float r3 = r1 * r8
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
            float r1 = r1 * r8
            float r5 = r3 + r1
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableRight()
            float r1 = (float) r1
            float r1 = r1 + r9
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r2 = r2 * r20
            float r1 = r1 + r2
            int r4 = (int) r1
            r1 = 0
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.isAnimatedEmojiStickers()
            if (r2 != 0) goto L_0x020c
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r2 = 1
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r1.getCurrentBackgroundDrawable(r2)
        L_0x020c:
            r25 = 1132396544(0x437var_, float:255.0)
            if (r1 == 0) goto L_0x0275
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            int r3 = r3.getTop()
            org.telegram.ui.Components.RecyclerListView r12 = r0.listView
            int r12 = r12.getTop()
            int r3 = r3 - r12
            r2.setBackgroundTopY((int) r3)
            android.graphics.drawable.Drawable r2 = r1.getShadowDrawable()
            r12 = r28
            int r3 = (r12 > r11 ? 1 : (r12 == r11 ? 0 : -1))
            if (r3 == 0) goto L_0x0240
            android.graphics.drawable.Drawable r3 = r0.fromMessageDrawable
            if (r3 == 0) goto L_0x0240
            int r11 = (int) r7
            r29 = r13
            int r13 = (int) r6
            r30 = r8
            int r8 = (int) r5
            r3.setBounds(r11, r13, r4, r8)
            android.graphics.drawable.Drawable r3 = r0.fromMessageDrawable
            r3.draw(r10)
            goto L_0x0244
        L_0x0240:
            r30 = r8
            r29 = r13
        L_0x0244:
            r3 = 255(0xff, float:3.57E-43)
            if (r2 == 0) goto L_0x025a
            float r8 = r17 * r25
            int r8 = (int) r8
            r2.setAlpha(r8)
            int r8 = (int) r7
            int r11 = (int) r6
            int r13 = (int) r5
            r2.setBounds(r8, r11, r4, r13)
            r2.draw(r10)
            r2.setAlpha(r3)
        L_0x025a:
            float r2 = r12 * r25
            int r2 = (int) r2
            r1.setAlpha(r2)
            int r2 = (int) r7
            int r8 = (int) r6
            int r11 = (int) r5
            r1.setBounds(r2, r8, r4, r11)
            r2 = 1
            r1.setDrawFullBubble(r2)
            r1.draw(r10)
            r11 = 0
            r1.setDrawFullBubble(r11)
            r1.setAlpha(r3)
            goto L_0x027c
        L_0x0275:
            r30 = r8
            r29 = r13
            r12 = r28
            r11 = 0
        L_0x027c:
            r39.restore()
            r39.save()
            r8 = 1092616192(0x41200000, float:10.0)
            if (r1 == 0) goto L_0x02c9
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = r1.isOutOwner()
            if (r1 == 0) goto L_0x02ac
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 + r7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r2 = r2 + r6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r4 - r3
            float r3 = (float) r3
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r13 = (float) r13
            float r13 = r5 - r13
            r10.clipRect(r1, r2, r3, r13)
            goto L_0x02c9
        L_0x02ac:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 + r7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r2 = r2 + r6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r3 = r4 - r3
            float r3 = (float) r3
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r13 = (float) r13
            float r13 = r5 - r13
            r10.clipRect(r1, r2, r3, r13)
        L_0x02c9:
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getLeft()
            float r1 = (float) r1
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            float r2 = r2.getX()
            float r1 = r1 + r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            float r2 = r2.getX()
            float r1 = r1 - r2
            float r2 = r14 - r19
            float r2 = r2 * r24
            float r13 = r15 + r2
            r10.translate(r1, r13)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawTime(r10, r12, r11)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawNamesLayout(r10, r12)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawCommentButton(r10, r12)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawCaptionLayout(r10, r11, r12)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawLinkPreview(r10, r12)
            r39.restore()
            boolean r1 = r0.hasReply
            if (r1 == 0) goto L_0x04e6
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
            int r11 = r3.replyStartX
            float r11 = (float) r11
            float r9 = r9 + r11
            int r3 = r3.replyStartY
            float r3 = (float) r3
            float r15 = r15 + r3
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.hasValidReplyMessageObject()
            if (r3 == 0) goto L_0x0365
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.messenger.MessageObject r3 = r3.replyMessageObject
            int r11 = r3.type
            if (r11 == 0) goto L_0x034e
            java.lang.CharSequence r3 = r3.caption
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0365
        L_0x034e:
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.messenger.MessageObject r3 = r3.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r11 != 0) goto L_0x0365
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r3 != 0) goto L_0x0365
            java.lang.String r3 = "chat_outReplyMessageText"
            int r3 = r0.getThemedColor(r3)
            goto L_0x036b
        L_0x0365:
            java.lang.String r3 = "chat_outReplyMediaMessageText"
            int r3 = r0.getThemedColor(r3)
        L_0x036b:
            org.telegram.messenger.MessageObject r11 = r0.currentMessageObject
            boolean r11 = r11.isOutOwner()
            if (r11 == 0) goto L_0x0380
            java.lang.String r11 = "chat_outReplyNameText"
            int r11 = r0.getThemedColor(r11)
            java.lang.String r8 = "chat_outReplyLine"
            int r8 = r0.getThemedColor(r8)
            goto L_0x038c
        L_0x0380:
            java.lang.String r8 = "chat_inReplyNameText"
            int r11 = r0.getThemedColor(r8)
            java.lang.String r8 = "chat_inReplyLine"
            int r8 = r0.getThemedColor(r8)
        L_0x038c:
            r31 = r4
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            r32 = r5
            int r5 = r0.replayObjectFromColor
            r33 = r7
            r7 = r30
            int r3 = androidx.core.graphics.ColorUtils.blendARGB(r5, r3, r7)
            r4.setColor(r3)
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint
            int r4 = r0.replayFromColor
            int r4 = androidx.core.graphics.ColorUtils.blendARGB(r4, r11, r7)
            r3.setColor(r4)
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            boolean r3 = r3.needReplyImage
            r11 = 1110441984(0x42300000, float:44.0)
            if (r3 == 0) goto L_0x03b8
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r3 = (float) r3
            float r1 = r1 - r3
        L_0x03b8:
            r30 = r1
            float r34 = r30 * r20
            float r1 = r9 * r17
            float r5 = r34 + r1
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r7
            float r2 = r2 + r1
            float r2 = r2 * r24
            float r15 = r15 * r7
            float r15 = r15 + r2
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint
            int r2 = android.graphics.Color.alpha(r8)
            float r2 = (float) r2
            float r2 = r2 * r17
            int r2 = (int) r2
            int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r8, r2)
            r1.setColor(r2)
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r4 = r5 + r1
            r8 = 1108082688(0x420CLASSNAME, float:35.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r1 = (float) r1
            float r35 = r15 + r1
            android.graphics.Paint r36 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint
            r1 = r39
            r2 = r5
            r3 = r15
            r11 = r5
            r5 = r35
            r35 = r6
            r6 = r36
            r1.drawRect(r2, r3, r4, r5, r6)
            r39.save()
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r17
            r2 = 0
            r10.translate(r1, r2)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            boolean r1 = r1.needReplyImage
            if (r1 == 0) goto L_0x0444
            r39.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.ImageReceiver r1 = r1.replyImageReceiver
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r3 = (float) r3
            r1.setImageCoords(r11, r15, r2, r3)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.ImageReceiver r1 = r1.replyImageReceiver
            r1.draw(r10)
            r10.translate(r11, r15)
            r39.restore()
            r1 = 1110441984(0x42300000, float:44.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r2 = 0
            r10.translate(r1, r2)
        L_0x0444:
            float r1 = r0.replyMessageDx
            float r2 = r9 - r1
            float r3 = r0.replyNameDx
            float r9 = r9 - r3
            float r30 = r30 - r1
            float r30 = r30 * r20
            float r2 = r2 * r17
            float r1 = r30 + r2
            float r9 = r9 * r17
            float r2 = r34 + r9
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            android.text.StaticLayout r3 = r3.replyNameLayout
            if (r3 == 0) goto L_0x046d
            r39.save()
            r10.translate(r2, r15)
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            android.text.StaticLayout r2 = r2.replyNameLayout
            r2.draw(r10)
            r39.restore()
        L_0x046d:
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            android.text.StaticLayout r2 = r2.replyTextLayout
            if (r2 == 0) goto L_0x04df
            r39.save()
            r2 = 1100480512(0x41980000, float:19.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r15 = r15 + r2
            r10.translate(r1, r15)
            r39.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r1 = r1.replySpoilers
            org.telegram.ui.Components.spoilers.SpoilerEffect.clipOutCanvas(r10, r1)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            android.text.StaticLayout r2 = r1.replyTextLayout
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r3 = r1.animatedEmojiReplyStack
            r4 = 0
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r5 = r1.replySpoilers
            r6 = 0
            r8 = 0
            r9 = 0
            r11 = 1065353216(0x3var_, float:1.0)
            r1 = r39
            r15 = r7
            r23 = r33
            r7 = r8
            r8 = r9
            r9 = r11
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            android.text.StaticLayout r1 = r1.replyTextLayout
            r1.draw(r10)
            r39.restore()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r1 = r1.replySpoilers
            java.util.Iterator r1 = r1.iterator()
        L_0x04b6:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x04db
            java.lang.Object r2 = r1.next()
            org.telegram.ui.Components.spoilers.SpoilerEffect r2 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r2
            boolean r3 = r2.shouldInvalidateColor()
            if (r3 == 0) goto L_0x04d7
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            android.text.StaticLayout r3 = r3.replyTextLayout
            android.text.TextPaint r3 = r3.getPaint()
            int r3 = r3.getColor()
            r2.setColor(r3)
        L_0x04d7:
            r2.draw(r10)
            goto L_0x04b6
        L_0x04db:
            r39.restore()
            goto L_0x04e2
        L_0x04df:
            r15 = r7
            r23 = r33
        L_0x04e2:
            r39.restore()
            goto L_0x04f0
        L_0x04e6:
            r31 = r4
            r32 = r5
            r35 = r6
            r23 = r7
            r15 = r30
        L_0x04f0:
            r39.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.MessageObject r1 = r1.getMessageObject()
            if (r1 == 0) goto L_0x0507
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.MessageObject r1 = r1.getMessageObject()
            int r1 = r1.type
            r2 = 19
            if (r1 == r2) goto L_0x0526
        L_0x0507:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r7 = r23 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r6 = r35 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r4 = r31 - r1
            float r1 = (float) r4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r5 = r32 - r2
            r10.clipRect(r7, r6, r1, r5)
        L_0x0526:
            float r1 = r0.scaleFrom
            float r1 = r1 * r20
            float r11 = r17 + r1
            boolean r1 = r0.drawBitmaps
            if (r1 == 0) goto L_0x0537
            float r1 = r0.scaleY
            float r1 = r1 * r20
            float r1 = r17 + r1
            goto L_0x0539
        L_0x0537:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0539:
            r39.save()
            float r21 = r29 * r20
            float r2 = r0.toXOffset
            float r2 = r18 - r2
            float r2 = r2 * r17
            float r2 = r21 + r2
            float r14 = r14 * r24
            org.telegram.messenger.MessageObject$TextLayoutBlock r3 = r0.textLayoutBlock
            float r3 = r3.textYOffset
            float r3 = r19 + r3
            float r3 = r3 * r15
            float r3 = r3 + r14
            r10.translate(r2, r3)
            float r9 = r11 * r1
            r1 = 0
            r10.scale(r11, r9, r1, r1)
            boolean r1 = r0.drawBitmaps
            if (r1 == 0) goto L_0x057b
            boolean r1 = r0.crossfade
            if (r1 == 0) goto L_0x056e
            android.graphics.Paint r1 = r0.bitmapPaint
            r2 = 1065353216(0x3var_, float:1.0)
            float r3 = r2 - r12
            float r3 = r3 * r25
            int r2 = (int) r3
            r1.setAlpha(r2)
        L_0x056e:
            android.graphics.Bitmap r1 = r0.textLayoutBitmap
            android.graphics.Paint r2 = r0.bitmapPaint
            r3 = 0
            r10.drawBitmap(r1, r3, r3, r2)
            r24 = r13
            r13 = r9
            goto L_0x0637
        L_0x057b:
            boolean r1 = r0.crossfade
            if (r1 == 0) goto L_0x05e8
            boolean r2 = r0.changeColor
            if (r2 == 0) goto L_0x05e8
            android.text.StaticLayout r1 = r0.layout
            android.text.TextPaint r1 = r1.getPaint()
            int r8 = r1.getColor()
            android.text.StaticLayout r1 = r0.layout
            android.text.TextPaint r1 = r1.getPaint()
            int r2 = r0.fromColor
            int r3 = r0.toColor
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r3, r12)
            r1.setColor(r2)
            r2 = 0
            r3 = 0
            android.text.StaticLayout r1 = r0.layout
            int r1 = r1.getWidth()
            float r4 = (float) r1
            android.text.StaticLayout r1 = r0.layout
            int r1 = r1.getHeight()
            float r5 = (float) r1
            r1 = 1065353216(0x3var_, float:1.0)
            float r23 = r1 - r12
            float r1 = r23 * r25
            int r6 = (int) r1
            r7 = 31
            r1 = r39
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            android.text.StaticLayout r1 = r0.layout
            r1.draw(r10)
            android.text.StaticLayout r2 = r0.layout
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r3 = r0.animatedEmojiStack
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r24 = 0
            r1 = r39
            r37 = r8
            r8 = r24
            r24 = r13
            r13 = r9
            r9 = r23
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            android.text.StaticLayout r1 = r0.layout
            android.text.TextPaint r1 = r1.getPaint()
            r2 = r37
            r1.setColor(r2)
            r39.restore()
            goto L_0x0637
        L_0x05e8:
            r24 = r13
            r13 = r9
            if (r1 == 0) goto L_0x0622
            r2 = 0
            r3 = 0
            android.text.StaticLayout r1 = r0.layout
            int r1 = r1.getWidth()
            float r4 = (float) r1
            android.text.StaticLayout r1 = r0.layout
            int r1 = r1.getHeight()
            float r5 = (float) r1
            r1 = 1065353216(0x3var_, float:1.0)
            float r9 = r1 - r12
            float r1 = r9 * r25
            int r6 = (int) r1
            r7 = 31
            r1 = r39
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            android.text.StaticLayout r1 = r0.layout
            r1.draw(r10)
            android.text.StaticLayout r2 = r0.layout
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r3 = r0.animatedEmojiStack
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r39
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            r39.restore()
            goto L_0x0637
        L_0x0622:
            android.text.StaticLayout r1 = r0.layout
            r1.draw(r10)
            android.text.StaticLayout r2 = r0.layout
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r3 = r0.animatedEmojiStack
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 1065353216(0x3var_, float:1.0)
            r1 = r39
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r1, r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x0637:
            r39.restore()
            android.text.StaticLayout r1 = r0.rtlLayout
            if (r1 == 0) goto L_0x06ed
            r39.save()
            float r1 = r0.toXOffsetRtl
            float r1 = r18 - r1
            float r1 = r1 * r17
            float r1 = r21 + r1
            org.telegram.messenger.MessageObject$TextLayoutBlock r2 = r0.textLayoutBlock
            float r2 = r2.textYOffset
            float r19 = r19 + r2
            float r19 = r19 * r15
            float r14 = r14 + r19
            r10.translate(r1, r14)
            r1 = 0
            r10.scale(r11, r13, r1, r1)
            boolean r1 = r0.drawBitmaps
            if (r1 == 0) goto L_0x0677
            boolean r1 = r0.crossfade
            if (r1 == 0) goto L_0x066e
            android.graphics.Paint r1 = r0.bitmapPaint
            r2 = 1065353216(0x3var_, float:1.0)
            float r3 = r2 - r12
            float r3 = r3 * r25
            int r2 = (int) r3
            r1.setAlpha(r2)
        L_0x066e:
            android.graphics.Bitmap r1 = r0.textLayoutBitmapRtl
            android.graphics.Paint r2 = r0.bitmapPaint
            r3 = 0
            r10.drawBitmap(r1, r3, r3, r2)
            goto L_0x06ea
        L_0x0677:
            boolean r1 = r0.crossfade
            if (r1 == 0) goto L_0x06b9
            boolean r2 = r0.changeColor
            if (r2 == 0) goto L_0x06b9
            android.text.StaticLayout r1 = r0.rtlLayout
            android.text.TextPaint r1 = r1.getPaint()
            int r1 = r1.getColor()
            int r2 = android.graphics.Color.alpha(r1)
            android.text.StaticLayout r3 = r0.rtlLayout
            android.text.TextPaint r3 = r3.getPaint()
            int r4 = r0.fromColor
            int r5 = r0.toColor
            int r4 = androidx.core.graphics.ColorUtils.blendARGB(r4, r5, r12)
            float r2 = (float) r2
            r5 = 1065353216(0x3var_, float:1.0)
            float r6 = r5 - r12
            float r2 = r2 * r6
            int r2 = (int) r2
            int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r4, r2)
            r3.setColor(r2)
            android.text.StaticLayout r2 = r0.rtlLayout
            r2.draw(r10)
            android.text.StaticLayout r2 = r0.rtlLayout
            android.text.TextPaint r2 = r2.getPaint()
            r2.setColor(r1)
            goto L_0x06ea
        L_0x06b9:
            if (r1 == 0) goto L_0x06e5
            android.text.StaticLayout r1 = r0.rtlLayout
            android.text.TextPaint r1 = r1.getPaint()
            int r1 = r1.getAlpha()
            android.text.StaticLayout r2 = r0.rtlLayout
            android.text.TextPaint r2 = r2.getPaint()
            float r3 = (float) r1
            r4 = 1065353216(0x3var_, float:1.0)
            float r5 = r4 - r12
            float r3 = r3 * r5
            int r3 = (int) r3
            r2.setAlpha(r3)
            android.text.StaticLayout r2 = r0.rtlLayout
            r2.draw(r10)
            android.text.StaticLayout r2 = r0.rtlLayout
            android.text.TextPaint r2 = r2.getPaint()
            r2.setAlpha(r1)
            goto L_0x06ea
        L_0x06e5:
            android.text.StaticLayout r1 = r0.rtlLayout
            r1.draw(r10)
        L_0x06ea:
            r39.restore()
        L_0x06ed:
            boolean r1 = r0.crossfade
            if (r1 == 0) goto L_0x0771
            r39.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getLeft()
            float r1 = (float) r1
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            float r2 = r2.getX()
            float r1 = r1 + r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            float r2 = r2.getX()
            float r1 = r1 - r2
            float r2 = r29 - r18
            float r2 = r2 * r20
            float r1 = r1 + r2
            r15 = r24
            r10.translate(r1, r15)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getTextX()
            float r1 = (float) r1
            org.telegram.ui.Cells.ChatMessageCell r2 = r0.messageView
            int r2 = r2.getTextY()
            float r2 = (float) r2
            r10.scale(r11, r13, r1, r2)
            float r1 = r0.crossfadeTextOffset
            float r1 = -r1
            r2 = 0
            r10.translate(r2, r1)
            android.graphics.Bitmap r1 = r0.crossfadeTextBitmap
            if (r1 == 0) goto L_0x073f
            android.graphics.Paint r1 = r0.bitmapPaint
            float r6 = r12 * r25
            int r3 = (int) r6
            r1.setAlpha(r3)
            android.graphics.Bitmap r1 = r0.crossfadeTextBitmap
            android.graphics.Paint r3 = r0.bitmapPaint
            r10.drawBitmap(r1, r2, r2, r3)
            goto L_0x076e
        L_0x073f:
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
            r2 = r39
            r5 = r12
            r1.drawMessageText(r2, r3, r4, r5, r6)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawAnimatedEmojis(r10, r12)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            int r1 = r1.getColor()
            if (r1 == r7) goto L_0x076e
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r1.setColor(r7)
        L_0x076e:
            r39.restore()
        L_0x0771:
            r39.restore()
            if (r22 == 0) goto L_0x079f
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
            r1 = r39
            r1.drawRect(r2, r3, r4, r5, r6)
            r39.restore()
        L_0x079f:
            float r1 = r0.progress
            int r2 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x07aa
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x07ae
        L_0x07aa:
            float r2 = r1 / r16
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x07ae:
            int r3 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x07b8
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            r4 = 0
            r3.setTextTransitionIsRunning(r4)
        L_0x07b8:
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0863
            int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x0863
            r39.save()
            org.telegram.ui.Components.ChatActivityEnterView r1 = r0.enterView
            float r1 = r1.getX()
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            float r3 = r3.getX()
            float r1 = r1 + r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getX()
            float r1 = r1 + r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getX()
            float r1 = r1 + r3
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getX()
            float r1 = r1 - r3
            r3 = 1112539136(0x42500000, float:52.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r2
            float r1 = r1 + r3
            org.telegram.ui.Components.ChatActivityEnterView r2 = r0.enterView
            float r2 = r2.getY()
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            float r3 = r3.getY()
            float r2 = r2 + r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r2 = r2 + r3
            org.telegram.ui.Components.ChatActivityEnterView r3 = r0.enterView
            android.view.View r3 = r3.getSendButton()
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r2 = r2 + r3
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r2 = r2 - r3
            r10.translate(r1, r2)
            org.telegram.ui.Components.ChatActivityEnterView r1 = r0.enterView
            android.view.View r1 = r1.getSendButton()
            r1.draw(r10)
            r39.restore()
            r39.restore()
        L_0x0863:
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
