package org.telegram.ui;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
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
    float fromRadius;
    private float fromStartX;
    private float fromStartY;
    private Matrix gradientMatrix;
    private Paint gradientPaint;
    private LinearGradient gradientShader;
    boolean hasReply;
    boolean initBitmaps = false;
    float lastMessageX;
    float lastMessageY;
    StaticLayout layout;
    RecyclerListView listView;
    private int messageId;
    ChatMessageCell messageView;
    float progress;
    int replayFromColor;
    int replayObjectFromColor;
    float replyFromObjectStartY;
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

    /* JADX WARNING: Removed duplicated region for block: B:106:0x04a5 A[Catch:{ Exception -> 0x0522 }] */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0534  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0536  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x053b  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x06d4  */
    /* JADX WARNING: Removed duplicated region for block: B:159:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0165  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x01a6  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01ef  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x02b4  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x02cb  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x02f3  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0343  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0365  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x039d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TextMessageEnterTransition(org.telegram.ui.Cells.ChatMessageCell r37, org.telegram.ui.ChatActivity r38, org.telegram.ui.Components.RecyclerListView r39, org.telegram.ui.MessageEnterTransitionContainer r40, org.telegram.ui.ActionBar.Theme.ResourcesProvider r41) {
        /*
            r36 = this;
            r7 = r36
            r8 = r37
            r9 = r40
            r36.<init>()
            android.graphics.Paint r0 = new android.graphics.Paint
            r10 = 1
            r0.<init>(r10)
            r7.bitmapPaint = r0
            r1 = 0
            r7.initBitmaps = r1
            r7.drawBitmaps = r1
            r0 = -1
            r7.animationIndex = r0
            r11 = r41
            r7.resourcesProvider = r11
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r7.currentAccount = r0
            org.telegram.messenger.MessageObject r0 = r37.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r0.textLayoutBlocks
            int r0 = r0.size()
            if (r0 > r10) goto L_0x06e2
            org.telegram.messenger.MessageObject r0 = r37.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r0.textLayoutBlocks
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x06e2
            org.telegram.messenger.MessageObject r0 = r37.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r0.textLayoutBlocks
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject$TextLayoutBlock r0 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r0
            android.text.StaticLayout r0 = r0.textLayout
            int r0 = r0.getLineCount()
            r2 = 10
            if (r0 <= r2) goto L_0x0053
            r13 = r38
            goto L_0x06e4
        L_0x0053:
            r7.messageView = r8
            r12 = r39
            r7.listView = r12
            r7.container = r9
            r13 = r38
            r7.chatActivity = r13
            org.telegram.ui.Components.ChatActivityEnterView r0 = r38.getChatActivityEnterView()
            r7.enterView = r0
            org.telegram.ui.Components.ChatActivityEnterView r14 = r38.getChatActivityEnterView()
            if (r14 == 0) goto L_0x06e1
            org.telegram.ui.Components.EditTextCaption r0 = r14.getEditField()
            if (r0 == 0) goto L_0x06e1
            org.telegram.ui.Components.EditTextCaption r0 = r14.getEditField()
            android.text.Layout r0 = r0.getLayout()
            if (r0 != 0) goto L_0x007d
            goto L_0x06e1
        L_0x007d:
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r14.getRecordCicle()
            float r0 = r0.drawingCircleRadius
            r7.fromRadius = r0
            android.graphics.Paint r0 = r7.bitmapPaint
            r0.setFilterBitmap(r10)
            org.telegram.messenger.MessageObject r0 = r37.getMessageObject()
            r7.currentMessageObject = r0
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r0 = r37.getTransitionParams()
            boolean r0 = r0.wasDraw
            if (r0 != 0) goto L_0x00a0
            android.graphics.Canvas r0 = new android.graphics.Canvas
            r0.<init>()
            r8.draw(r0)
        L_0x00a0:
            r8.setEnterTransitionInProgress(r10)
            org.telegram.ui.Components.EditTextCaption r0 = r14.getEditField()
            android.text.Layout r0 = r0.getLayout()
            java.lang.CharSequence r15 = r0.getText()
            org.telegram.messenger.MessageObject r0 = r37.getMessageObject()
            java.lang.CharSequence r0 = r0.messageText
            r7.crossfade = r1
            r2 = 0
            org.telegram.ui.Components.EditTextCaption r3 = r14.getEditField()
            android.text.Layout r3 = r3.getLayout()
            int r3 = r3.getHeight()
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r5 = 1101004800(0x41a00000, float:20.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            org.telegram.messenger.MessageObject r6 = r37.getMessageObject()
            int r6 = r6.getEmojiOnlyCount()
            r1 = 2
            if (r6 == 0) goto L_0x0114
            org.telegram.messenger.MessageObject r6 = r37.getMessageObject()
            int r6 = r6.getEmojiOnlyCount()
            if (r6 != r10) goto L_0x00eb
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintOneEmoji
            r6 = 1107296256(0x42000000, float:32.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r6 = r4
            goto L_0x0115
        L_0x00eb:
            org.telegram.messenger.MessageObject r6 = r37.getMessageObject()
            int r6 = r6.getEmojiOnlyCount()
            if (r6 != r1) goto L_0x00ff
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintTwoEmoji
            r6 = 1105199104(0x41e00000, float:28.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r6 = r4
            goto L_0x0115
        L_0x00ff:
            org.telegram.messenger.MessageObject r6 = r37.getMessageObject()
            int r6 = r6.getEmojiOnlyCount()
            r1 = 3
            if (r6 != r1) goto L_0x0114
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaintThreeEmoji
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6 = r4
            goto L_0x0115
        L_0x0114:
            r6 = r4
        L_0x0115:
            r1 = 0
            boolean r4 = r0 instanceof android.text.Spannable
            if (r4 == 0) goto L_0x013b
            r4 = r0
            android.text.Spannable r4 = (android.text.Spannable) r4
            int r10 = r0.length()
            r18 = r1
            java.lang.Class<java.lang.Object> r1 = java.lang.Object.class
            r19 = r2
            r2 = 0
            java.lang.Object[] r1 = r4.getSpans(r2, r10, r1)
            r2 = 0
        L_0x012d:
            int r10 = r1.length
            if (r2 >= r10) goto L_0x013f
            r10 = r1[r2]
            boolean r10 = r10 instanceof org.telegram.messenger.Emoji.EmojiSpan
            if (r10 != 0) goto L_0x0138
            r10 = 1
            goto L_0x0141
        L_0x0138:
            int r2 = r2 + 1
            goto L_0x012d
        L_0x013b:
            r18 = r1
            r19 = r2
        L_0x013f:
            r10 = r18
        L_0x0141:
            int r1 = r15.length()
            int r2 = r0.length()
            if (r1 != r2) goto L_0x0154
            if (r10 == 0) goto L_0x014e
            goto L_0x0154
        L_0x014e:
            r26 = r10
            r4 = r19
            r10 = r0
            goto L_0x01bb
        L_0x0154:
            r1 = 1
            r7.crossfade = r1
            java.lang.String r1 = r15.toString()
            java.lang.String r2 = r1.trim()
            int r4 = r1.indexOf(r2)
            if (r4 <= 0) goto L_0x01a6
            org.telegram.ui.Components.EditTextCaption r18 = r14.getEditField()
            r20 = r0
            android.text.Layout r0 = r18.getLayout()
            org.telegram.ui.Components.EditTextCaption r18 = r14.getEditField()
            r21 = r1
            android.text.Layout r1 = r18.getLayout()
            int r1 = r1.getLineForOffset(r4)
            int r0 = r0.getLineTop(r1)
            org.telegram.ui.Components.EditTextCaption r1 = r14.getEditField()
            android.text.Layout r1 = r1.getLayout()
            org.telegram.ui.Components.EditTextCaption r18 = r14.getEditField()
            r22 = r3
            android.text.Layout r3 = r18.getLayout()
            int r18 = r2.length()
            r26 = r10
            int r10 = r4 + r18
            int r3 = r3.getLineForOffset(r10)
            int r1 = r1.getLineBottom(r3)
            int r3 = r1 - r0
            goto L_0x01b0
        L_0x01a6:
            r20 = r0
            r21 = r1
            r22 = r3
            r26 = r10
            r0 = r19
        L_0x01b0:
            android.graphics.Paint$FontMetricsInt r1 = r6.getFontMetricsInt()
            r10 = 0
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r2, r1, r5, r10)
            r4 = r0
            r10 = r1
        L_0x01bb:
            org.telegram.ui.Components.EditTextCaption r0 = r14.getEditField()
            float r0 = r0.getTextSize()
            float r1 = r6.getTextSize()
            float r0 = r0 / r1
            r7.scaleFrom = r0
            org.telegram.ui.Components.EditTextCaption r0 = r14.getEditField()
            android.text.Layout r0 = r0.getLayout()
            int r0 = r0.getLineCount()
            org.telegram.ui.Components.EditTextCaption r1 = r14.getEditField()
            android.text.Layout r1 = r1.getLayout()
            int r1 = r1.getWidth()
            float r1 = (float) r1
            float r2 = r7.scaleFrom
            float r1 = r1 / r2
            int r2 = (int) r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r27 = r0
            r0 = 24
            if (r1 < r0) goto L_0x0211
            int r1 = r10.length()
            r28 = r5
            r5 = 0
            android.text.StaticLayout$Builder r1 = android.text.StaticLayout.Builder.obtain(r10, r5, r1, r6, r2)
            r5 = 1
            android.text.StaticLayout$Builder r1 = r1.setBreakStrategy(r5)
            r5 = 0
            android.text.StaticLayout$Builder r1 = r1.setHyphenationFrequency(r5)
            android.text.Layout$Alignment r5 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r1 = r1.setAlignment(r5)
            android.text.StaticLayout r1 = r1.build()
            r7.layout = r1
            goto L_0x022a
        L_0x0211:
            r28 = r5
            android.text.StaticLayout r1 = new android.text.StaticLayout
            android.text.Layout$Alignment r22 = android.text.Layout.Alignment.ALIGN_NORMAL
            r23 = 1065353216(0x3var_, float:1.0)
            r24 = 0
            r25 = 0
            r18 = r1
            r19 = r10
            r20 = r6
            r21 = r2
            r18.<init>(r19, r20, r21, r22, r23, r24, r25)
            r7.layout = r1
        L_0x022a:
            float r1 = r14.getY()
            org.telegram.ui.Components.EditTextCaption r5 = r14.getEditField()
            float r5 = r5.getY()
            float r1 = r1 + r5
            org.telegram.ui.Components.EditTextCaption r5 = r14.getEditField()
            android.view.ViewParent r5 = r5.getParent()
            android.view.View r5 = (android.view.View) r5
            float r5 = r5.getY()
            float r1 = r1 + r5
            org.telegram.ui.Components.EditTextCaption r5 = r14.getEditField()
            android.view.ViewParent r5 = r5.getParent()
            android.view.ViewParent r5 = r5.getParent()
            android.view.View r5 = (android.view.View) r5
            float r5 = r5.getY()
            float r29 = r1 + r5
            float r1 = r14.getX()
            org.telegram.ui.Components.EditTextCaption r5 = r14.getEditField()
            float r5 = r5.getX()
            float r1 = r1 + r5
            org.telegram.ui.Components.EditTextCaption r5 = r14.getEditField()
            android.view.ViewParent r5 = r5.getParent()
            android.view.View r5 = (android.view.View) r5
            float r5 = r5.getX()
            float r1 = r1 + r5
            org.telegram.ui.Components.EditTextCaption r5 = r14.getEditField()
            android.view.ViewParent r5 = r5.getParent()
            android.view.ViewParent r5 = r5.getParent()
            android.view.View r5 = (android.view.View) r5
            float r5 = r5.getX()
            float r1 = r1 + r5
            r7.fromStartX = r1
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r29 + r1
            org.telegram.ui.Components.EditTextCaption r5 = r14.getEditField()
            int r5 = r5.getScrollY()
            float r5 = (float) r5
            float r1 = r1 - r5
            float r5 = (float) r4
            float r1 = r1 + r5
            r7.fromStartY = r1
            r1 = 0
            r7.toXOffset = r1
            r5 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r18 = 0
            r1 = r18
        L_0x02ac:
            android.text.StaticLayout r0 = r7.layout
            int r0 = r0.getLineCount()
            if (r1 >= r0) goto L_0x02c4
            android.text.StaticLayout r0 = r7.layout
            float r0 = r0.getLineLeft(r1)
            int r19 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r19 >= 0) goto L_0x02bf
            r5 = r0
        L_0x02bf:
            int r1 = r1 + 1
            r0 = 24
            goto L_0x02ac
        L_0x02c4:
            r0 = 2139095039(0x7f7fffff, float:3.4028235E38)
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 == 0) goto L_0x02cd
            r7.toXOffset = r5
        L_0x02cd:
            float r0 = (float) r3
            android.text.StaticLayout r1 = r7.layout
            int r1 = r1.getHeight()
            float r1 = (float) r1
            r30 = r3
            float r3 = r7.scaleFrom
            float r1 = r1 * r3
            float r0 = r0 / r1
            r7.scaleY = r0
            r0 = 1082130432(0x40800000, float:4.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r0 = r29 + r0
            r7.drawableFromTop = r0
            org.telegram.ui.Components.ChatActivityEnterView r0 = r7.enterView
            boolean r0 = r0.isTopViewVisible()
            r1 = 1094713344(0x41400000, float:12.0)
            if (r0 == 0) goto L_0x02fd
            float r0 = r7.drawableFromTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r3 = (float) r3
            float r0 = r0 - r3
            r7.drawableFromTop = r0
        L_0x02fd:
            org.telegram.ui.Components.EditTextCaption r0 = r14.getEditField()
            int r0 = r0.getMeasuredHeight()
            float r0 = (float) r0
            float r0 = r29 + r0
            r7.drawableFromBottom = r0
            org.telegram.messenger.MessageObject r0 = r37.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r0.textLayoutBlocks
            r3 = 0
            java.lang.Object r0 = r0.get(r3)
            org.telegram.messenger.MessageObject$TextLayoutBlock r0 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r0
            r7.textLayoutBlock = r0
            android.text.StaticLayout r3 = r0.textLayout
            r0 = 0
            r19 = 0
            java.lang.String r1 = "chat_messageTextOut"
            int r20 = r7.getThemedColor(r1)
            double r20 = androidx.core.graphics.ColorUtils.calculateLuminance(r20)
            r22 = r0
            java.lang.String r0 = "chat_messagePanelText"
            int r23 = r7.getThemedColor(r0)
            double r23 = androidx.core.graphics.ColorUtils.calculateLuminance(r23)
            double r20 = r20 - r23
            double r20 = java.lang.Math.abs(r20)
            r23 = 4596373779801702400(0x3fCLASSNAMEa0000000, double:0.NUM)
            int r25 = (r20 > r23 ? 1 : (r20 == r23 ? 0 : -1))
            if (r25 <= 0) goto L_0x034b
            r31 = r4
            r4 = 1
            r7.crossfade = r4
            r7.changeColor = r4
            goto L_0x034d
        L_0x034b:
            r31 = r4
        L_0x034d:
            int r0 = r7.getThemedColor(r0)
            r7.fromColor = r0
            int r0 = r7.getThemedColor(r1)
            r7.toColor = r0
            int r0 = r3.getLineCount()
            android.text.StaticLayout r1 = r7.layout
            int r1 = r1.getLineCount()
            if (r0 != r1) goto L_0x039d
            int r0 = r3.getLineCount()
            r1 = 0
        L_0x036a:
            if (r1 >= r0) goto L_0x0392
            android.text.StaticLayout r4 = r7.layout
            boolean r4 = r7.isRtlLine(r4, r1)
            if (r4 == 0) goto L_0x0377
            int r19 = r19 + 1
            goto L_0x0379
        L_0x0377:
            int r22 = r22 + 1
        L_0x0379:
            int r4 = r3.getLineEnd(r1)
            r20 = r0
            android.text.StaticLayout r0 = r7.layout
            int r0 = r0.getLineEnd(r1)
            if (r4 == r0) goto L_0x038d
            r4 = 1
            r7.crossfade = r4
            r0 = r22
            goto L_0x0396
        L_0x038d:
            int r1 = r1 + 1
            r0 = r20
            goto L_0x036a
        L_0x0392:
            r20 = r0
            r0 = r22
        L_0x0396:
            r27 = r0
            r32 = r19
            r4 = r20
            goto L_0x03a6
        L_0x039d:
            r1 = 1
            r7.crossfade = r1
            r32 = r19
            r4 = r27
            r27 = r22
        L_0x03a6:
            r0 = 2139095039(0x7f7fffff, float:3.4028235E38)
            boolean r1 = r7.crossfade
            if (r1 != 0) goto L_0x047d
            if (r32 <= 0) goto L_0x047d
            if (r27 <= 0) goto L_0x047d
            android.text.SpannableString r1 = new android.text.SpannableString
            r1.<init>(r10)
            android.text.SpannableString r5 = new android.text.SpannableString
            r5.<init>(r10)
            r19 = 0
            r33 = r3
            r3 = r19
        L_0x03c1:
            if (r3 >= r4) goto L_0x0412
            r34 = r4
            android.text.StaticLayout r4 = r7.layout
            boolean r4 = r7.isRtlLine(r4, r3)
            if (r4 == 0) goto L_0x03f0
            org.telegram.ui.Components.EmptyStubSpan r4 = new org.telegram.ui.Components.EmptyStubSpan
            r4.<init>()
            r35 = r10
            android.text.StaticLayout r10 = r7.layout
            int r10 = r10.getLineStart(r3)
            android.text.StaticLayout r11 = r7.layout
            int r11 = r11.getLineEnd(r3)
            r12 = 0
            r1.setSpan(r4, r10, r11, r12)
            android.text.StaticLayout r4 = r7.layout
            float r4 = r4.getLineLeft(r3)
            int r10 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r10 >= 0) goto L_0x03ef
            r0 = r4
        L_0x03ef:
            goto L_0x0407
        L_0x03f0:
            r35 = r10
            org.telegram.ui.Components.EmptyStubSpan r4 = new org.telegram.ui.Components.EmptyStubSpan
            r4.<init>()
            android.text.StaticLayout r10 = r7.layout
            int r10 = r10.getLineStart(r3)
            android.text.StaticLayout r11 = r7.layout
            int r11 = r11.getLineEnd(r3)
            r12 = 0
            r5.setSpan(r4, r10, r11, r12)
        L_0x0407:
            int r3 = r3 + 1
            r12 = r39
            r11 = r41
            r4 = r34
            r10 = r35
            goto L_0x03c1
        L_0x0412:
            r34 = r4
            r35 = r10
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 24
            if (r3 < r4) goto L_0x0458
            int r3 = r1.length()
            r4 = 0
            android.text.StaticLayout$Builder r3 = android.text.StaticLayout.Builder.obtain(r1, r4, r3, r6, r2)
            r10 = 1
            android.text.StaticLayout$Builder r3 = r3.setBreakStrategy(r10)
            android.text.StaticLayout$Builder r3 = r3.setHyphenationFrequency(r4)
            android.text.Layout$Alignment r10 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r3 = r3.setAlignment(r10)
            android.text.StaticLayout r3 = r3.build()
            r7.layout = r3
            int r3 = r5.length()
            android.text.StaticLayout$Builder r3 = android.text.StaticLayout.Builder.obtain(r5, r4, r3, r6, r2)
            r10 = 1
            android.text.StaticLayout$Builder r3 = r3.setBreakStrategy(r10)
            android.text.StaticLayout$Builder r3 = r3.setHyphenationFrequency(r4)
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r3 = r3.setAlignment(r4)
            android.text.StaticLayout r3 = r3.build()
            r7.rtlLayout = r3
            goto L_0x0483
        L_0x0458:
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.Layout$Alignment r22 = android.text.Layout.Alignment.ALIGN_NORMAL
            r23 = 1065353216(0x3var_, float:1.0)
            r24 = 0
            r25 = 0
            r18 = r3
            r19 = r1
            r20 = r6
            r21 = r2
            r18.<init>(r19, r20, r21, r22, r23, r24, r25)
            r7.layout = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.Layout$Alignment r22 = android.text.Layout.Alignment.ALIGN_NORMAL
            r18 = r3
            r19 = r5
            r18.<init>(r19, r20, r21, r22, r23, r24, r25)
            r7.rtlLayout = r3
            goto L_0x0483
        L_0x047d:
            r33 = r3
            r34 = r4
            r35 = r10
        L_0x0483:
            r10 = r0
            android.text.StaticLayout r0 = r7.layout
            int r0 = r0.getWidth()
            org.telegram.messenger.MessageObject r1 = r37.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r1.textLayoutBlocks
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject$TextLayoutBlock r1 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r1
            android.text.StaticLayout r1 = r1.textLayout
            int r1 = r1.getWidth()
            int r0 = r0 - r1
            float r0 = (float) r0
            r7.toXOffsetRtl = r0
            boolean r0 = r7.drawBitmaps     // Catch:{ Exception -> 0x0522 }
            if (r0 == 0) goto L_0x0521
            android.text.StaticLayout r0 = r7.layout     // Catch:{ Exception -> 0x0522 }
            int r0 = r0.getWidth()     // Catch:{ Exception -> 0x0522 }
            android.text.StaticLayout r1 = r7.layout     // Catch:{ Exception -> 0x0522 }
            int r1 = r1.getHeight()     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r3)     // Catch:{ Exception -> 0x0522 }
            r7.textLayoutBitmap = r0     // Catch:{ Exception -> 0x0522 }
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap r1 = r7.textLayoutBitmap     // Catch:{ Exception -> 0x0522 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0522 }
            android.text.StaticLayout r1 = r7.layout     // Catch:{ Exception -> 0x0522 }
            r1.draw(r0)     // Catch:{ Exception -> 0x0522 }
            android.text.StaticLayout r1 = r7.rtlLayout     // Catch:{ Exception -> 0x0522 }
            if (r1 == 0) goto L_0x04e8
            int r1 = r1.getWidth()     // Catch:{ Exception -> 0x0522 }
            android.text.StaticLayout r3 = r7.rtlLayout     // Catch:{ Exception -> 0x0522 }
            int r3 = r3.getHeight()     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r3, r4)     // Catch:{ Exception -> 0x0522 }
            r7.textLayoutBitmapRtl = r1     // Catch:{ Exception -> 0x0522 }
            android.graphics.Canvas r1 = new android.graphics.Canvas     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap r3 = r7.textLayoutBitmapRtl     // Catch:{ Exception -> 0x0522 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0522 }
            r0 = r1
            android.text.StaticLayout r1 = r7.rtlLayout     // Catch:{ Exception -> 0x0522 }
            r1.draw(r0)     // Catch:{ Exception -> 0x0522 }
        L_0x04e8:
            boolean r1 = r7.crossfade     // Catch:{ Exception -> 0x0522 }
            if (r1 == 0) goto L_0x0521
            int r1 = r37.getMeasuredHeight()     // Catch:{ Exception -> 0x0522 }
            int r3 = r39.getMeasuredHeight()     // Catch:{ Exception -> 0x0522 }
            if (r1 >= r3) goto L_0x050a
            r1 = 0
            r7.crossfadeTextOffset = r1     // Catch:{ Exception -> 0x0522 }
            int r1 = r37.getMeasuredWidth()     // Catch:{ Exception -> 0x0522 }
            int r3 = r37.getMeasuredHeight()     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r3, r4)     // Catch:{ Exception -> 0x0522 }
            r7.crossfadeTextBitmap = r1     // Catch:{ Exception -> 0x0522 }
            goto L_0x0521
        L_0x050a:
            int r1 = r37.getTop()     // Catch:{ Exception -> 0x0522 }
            float r1 = (float) r1     // Catch:{ Exception -> 0x0522 }
            r7.crossfadeTextOffset = r1     // Catch:{ Exception -> 0x0522 }
            int r1 = r37.getMeasuredWidth()     // Catch:{ Exception -> 0x0522 }
            int r3 = r39.getMeasuredHeight()     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x0522 }
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r3, r4)     // Catch:{ Exception -> 0x0522 }
            r7.crossfadeTextBitmap = r1     // Catch:{ Exception -> 0x0522 }
        L_0x0521:
            goto L_0x0526
        L_0x0522:
            r0 = move-exception
            r1 = 0
            r7.drawBitmaps = r1
        L_0x0526:
            org.telegram.messenger.MessageObject r0 = r37.getMessageObject()
            int r0 = r0.getReplyMsgId()
            if (r0 == 0) goto L_0x0536
            android.text.StaticLayout r0 = r8.replyNameLayout
            if (r0 == 0) goto L_0x0536
            r0 = 1
            goto L_0x0537
        L_0x0536:
            r0 = 0
        L_0x0537:
            r7.hasReply = r0
            if (r0 == 0) goto L_0x05c4
            org.telegram.ui.ActionBar.SimpleTextView r0 = r38.getReplyNameTextView()
            float r1 = r0.getX()
            android.view.ViewParent r3 = r0.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getX()
            float r1 = r1 + r3
            r7.replyFromStartX = r1
            float r1 = r0.getY()
            android.view.ViewParent r3 = r0.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r1 = r1 + r3
            android.view.ViewParent r3 = r0.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r1 = r1 + r3
            r7.replyFromStartY = r1
            org.telegram.ui.ActionBar.SimpleTextView r0 = r38.getReplyObjectTextView()
            float r1 = r0.getY()
            android.view.ViewParent r3 = r0.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r1 = r1 + r3
            android.view.ViewParent r3 = r0.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewParent r3 = r3.getParent()
            android.view.View r3 = (android.view.View) r3
            float r3 = r3.getY()
            float r1 = r1 + r3
            r7.replyFromObjectStartY = r1
            org.telegram.ui.ActionBar.SimpleTextView r1 = r38.getReplyNameTextView()
            int r1 = r1.getTextColor()
            r7.replayFromColor = r1
            org.telegram.ui.ActionBar.SimpleTextView r1 = r38.getReplyObjectTextView()
            int r1 = r1.getTextColor()
            r7.replayObjectFromColor = r1
            float r1 = r7.drawableFromTop
            r3 = 1110966272(0x42380000, float:46.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r1 = r1 - r3
            r7.drawableFromTop = r1
        L_0x05c4:
            android.graphics.Matrix r0 = new android.graphics.Matrix
            r0.<init>()
            r7.gradientMatrix = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r1 = 1
            r0.<init>(r1)
            r7.gradientPaint = r0
            android.graphics.PorterDuffXfermode r1 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.DST_IN
            r1.<init>(r3)
            r0.setXfermode(r1)
            android.graphics.LinearGradient r0 = new android.graphics.LinearGradient
            r19 = 0
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.Shader$TileMode r25 = android.graphics.Shader.TileMode.CLAMP
            r18 = r0
            r20 = r1
            r18.<init>(r19, r20, r21, r22, r23, r24, r25)
            r7.gradientShader = r0
            android.graphics.Paint r1 = r7.gradientPaint
            r1.setShader(r0)
            org.telegram.messenger.MessageObject r0 = r37.getMessageObject()
            int r0 = r0.stableId
            r7.messageId = r0
            org.telegram.ui.Components.EditTextCaption r0 = r14.getEditField()
            r1 = 0
            r0.setAlpha(r1)
            r1 = 1
            r14.setTextTransitionIsRunning(r1)
            android.text.StaticLayout r0 = r8.replyNameLayout
            if (r0 == 0) goto L_0x0640
            android.text.StaticLayout r0 = r8.replyNameLayout
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r0 <= r1) goto L_0x0640
            android.text.StaticLayout r0 = r8.replyNameLayout
            r1 = 0
            float r0 = r0.getPrimaryHorizontal(r1)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x0640
            android.text.StaticLayout r0 = r8.replyNameLayout
            int r0 = r0.getWidth()
            float r0 = (float) r0
            android.text.StaticLayout r3 = r8.replyNameLayout
            float r3 = r3.getLineWidth(r1)
            float r0 = r0 - r3
            r7.replyNameDx = r0
        L_0x0640:
            android.text.StaticLayout r0 = r8.replyTextLayout
            if (r0 == 0) goto L_0x066d
            android.text.StaticLayout r0 = r8.replyTextLayout
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            r1 = 1
            if (r0 < r1) goto L_0x066d
            android.text.StaticLayout r0 = r8.replyTextLayout
            r1 = 0
            float r0 = r0.getPrimaryHorizontal(r1)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x066d
            android.text.StaticLayout r0 = r8.replyTextLayout
            int r0 = r0.getWidth()
            float r0 = (float) r0
            android.text.StaticLayout r3 = r8.replyTextLayout
            float r1 = r3.getLineWidth(r1)
            float r0 = r0 - r1
            r7.replyMessageDx = r0
        L_0x066d:
            r1 = 2
            float[] r0 = new float[r1]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r7.animator = r0
            org.telegram.ui.TextMessageEnterTransition$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.TextMessageEnterTransition$$ExternalSyntheticLambda0
            r3.<init>(r7, r14, r9)
            r0.addUpdateListener(r3)
            android.animation.ValueAnimator r0 = r7.animator
            android.view.animation.LinearInterpolator r3 = new android.view.animation.LinearInterpolator
            r3.<init>()
            r0.setInterpolator(r3)
            android.animation.ValueAnimator r0 = r7.animator
            r3 = 250(0xfa, double:1.235E-321)
            r0.setDuration(r3)
            r9.addTransition(r7)
            int r0 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r3 = r7.animationIndex
            r4 = 0
            int r0 = r0.setAnimationInProgress(r3, r4)
            r7.animationIndex = r0
            android.animation.ValueAnimator r0 = r7.animator
            org.telegram.ui.TextMessageEnterTransition$1 r11 = new org.telegram.ui.TextMessageEnterTransition$1
            r12 = 2
            r1 = r11
            r16 = r2
            r2 = r36
            r17 = r30
            r18 = r33
            r3 = r40
            r19 = r31
            r20 = r34
            r4 = r37
            r21 = r28
            r5 = r14
            r22 = r6
            r6 = r38
            r1.<init>(r3, r4, r5, r6)
            r0.addListener(r11)
            int r0 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
            if (r0 != r12) goto L_0x06e0
            r1 = 1
            org.telegram.ui.ActionBar.Theme$MessageDrawable r0 = r8.getCurrentBackgroundDrawable(r1)
            if (r0 == 0) goto L_0x06e0
            java.lang.String r1 = "chat_messagePanelBackground"
            int r1 = r7.getThemedColor(r1)
            android.graphics.drawable.Drawable r1 = r0.getTransitionDrawable(r1)
            r7.fromMessageDrawable = r1
        L_0x06e0:
            return
        L_0x06e1:
            return
        L_0x06e2:
            r13 = r38
        L_0x06e4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TextMessageEnterTransition.<init>(org.telegram.ui.Cells.ChatMessageCell, org.telegram.ui.ChatActivity, org.telegram.ui.Components.RecyclerListView, org.telegram.ui.MessageEnterTransitionContainer, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-TextMessageEnterTransition  reason: not valid java name */
    public /* synthetic */ void m4651lambda$new$0$orgtelegramuiTextMessageEnterTransition(ChatActivityEnterView chatActivityEnterView, MessageEnterTransitionContainer container2, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chatActivityEnterView.getEditField().setAlpha(this.progress);
        container2.invalidate();
    }

    public void start() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    private boolean isRtlLine(Layout layout2, int line) {
        return layout2.getLineRight(line) == ((float) layout2.getWidth()) && layout2.getLineLeft(line) != 0.0f;
    }

    public void onDraw(Canvas canvas) {
        float alphaProgress;
        int clipBottom;
        float progress2;
        float messageViewX;
        float fromX;
        boolean z;
        float alphaProgress2;
        float drawableTop;
        float drawableBottom;
        int drawableRight;
        float scale2;
        int replyMessageColor;
        int replyOwnerMessageColor;
        int replyOwnerMessageColor2;
        float fromReplayX;
        float replyY;
        float replyToMessageX;
        Drawable drawable;
        Canvas canvas2 = canvas;
        if (this.drawBitmaps && !this.initBitmaps && this.crossfadeTextBitmap != null && this.messageView.getTransitionParams().wasDraw) {
            this.initBitmaps = true;
            Canvas bitmapCanvas = new Canvas(this.crossfadeTextBitmap);
            bitmapCanvas.translate(0.0f, this.crossfadeTextOffset);
            ChatMessageCell chatMessageCell = this.messageView;
            chatMessageCell.drawMessageText(bitmapCanvas, chatMessageCell.getMessageObject().textLayoutBlocks, true, 1.0f, true);
        }
        float listViewBottom = (this.listView.getY() - this.container.getY()) + ((float) this.listView.getMeasuredHeight());
        float fromX2 = this.fromStartX - this.container.getX();
        float fromY = this.fromStartY - this.container.getY();
        this.textX = (float) this.messageView.getTextX();
        this.textY = (float) this.messageView.getTextY();
        if (this.messageView.getMessageObject().stableId == this.messageId) {
            float messageViewX2 = (this.messageView.getX() + this.listView.getX()) - this.container.getX();
            float messageViewY = (((float) (this.messageView.getTop() + this.listView.getTop())) - this.container.getY()) + this.enterView.getTopViewHeight();
            this.lastMessageX = messageViewX2;
            this.lastMessageY = messageViewY;
            float progress3 = ChatListItemAnimator.DEFAULT_INTERPOLATOR.getInterpolation(this.progress);
            float f = this.progress;
            float alphaProgress3 = f > 0.4f ? 1.0f : f / 0.4f;
            float p2 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.progress);
            float progressX = CubicBezierInterpolator.EASE_OUT.getInterpolation(p2);
            float toX = messageViewX2 + this.textX;
            float toY = messageViewY + this.textY;
            int clipBottom2 = (int) ((((float) this.container.getMeasuredHeight()) * (1.0f - progressX)) + (listViewBottom * progressX));
            boolean clipBottomWithAlpha = (this.messageView.getBottom() - AndroidUtilities.dp(4.0f) > this.listView.getMeasuredHeight()) && (((float) this.messageView.getMeasuredHeight()) + messageViewY) - ((float) AndroidUtilities.dp(8.0f)) > ((float) clipBottom2) && this.container.getMeasuredHeight() > 0;
            if (clipBottomWithAlpha) {
                clipBottom = clipBottom2;
                float f2 = p2;
                alphaProgress = alphaProgress3;
                progress2 = progress3;
                canvas.saveLayerAlpha(0.0f, Math.max(0.0f, messageViewY), (float) this.container.getMeasuredWidth(), (float) this.container.getMeasuredHeight(), 255, 31);
            } else {
                clipBottom = clipBottom2;
                float f3 = p2;
                alphaProgress = alphaProgress3;
                progress2 = progress3;
            }
            canvas.save();
            canvas2.clipRect(0.0f, ((this.listView.getY() + this.chatActivity.getChatListViewPadding()) - this.container.getY()) - ((float) AndroidUtilities.dp(3.0f)), (float) this.container.getMeasuredWidth(), (float) this.container.getMeasuredHeight());
            canvas.save();
            float drawableX = ((float) this.messageView.getBackgroundDrawableLeft()) + messageViewX2 + ((fromX2 - (toX - this.toXOffset)) * (1.0f - progressX));
            float drawableToTop = messageViewY + ((float) this.messageView.getBackgroundDrawableTop());
            float drawableTop2 = ((this.drawableFromTop - this.container.getY()) * (1.0f - progress2)) + (drawableToTop * progress2);
            float drawableH = (float) (this.messageView.getBackgroundDrawableBottom() - this.messageView.getBackgroundDrawableTop());
            float drawableBottom2 = ((this.drawableFromBottom - this.container.getY()) * (1.0f - progress2)) + ((drawableToTop + drawableH) * progress2);
            int drawableRight2 = (int) (((float) this.messageView.getBackgroundDrawableRight()) + messageViewX2 + (((float) AndroidUtilities.dp(4.0f)) * (1.0f - progressX)));
            Theme.MessageDrawable drawable2 = this.messageView.getCurrentBackgroundDrawable(true);
            if (drawable2 != null) {
                float f4 = drawableH;
                this.messageView.setBackgroundTopY(this.container.getTop() - this.listView.getTop());
                Drawable shadowDrawable = drawable2.getShadowDrawable();
                alphaProgress2 = alphaProgress;
                if (alphaProgress2 == 1.0f || (drawable = this.fromMessageDrawable) == null) {
                    fromX = fromX2;
                    messageViewX = messageViewX2;
                } else {
                    float f5 = listViewBottom;
                    fromX = fromX2;
                    messageViewX = messageViewX2;
                    drawable.setBounds((int) drawableX, (int) drawableTop2, drawableRight2, (int) drawableBottom2);
                    this.fromMessageDrawable.draw(canvas2);
                }
                if (shadowDrawable != null) {
                    shadowDrawable.setAlpha((int) (progressX * 255.0f));
                    shadowDrawable.setBounds((int) drawableX, (int) drawableTop2, drawableRight2, (int) drawableBottom2);
                    shadowDrawable.draw(canvas2);
                    shadowDrawable.setAlpha(255);
                }
                drawable2.setAlpha((int) (alphaProgress2 * 255.0f));
                drawable2.setBounds((int) drawableX, (int) drawableTop2, drawableRight2, (int) drawableBottom2);
                drawable2.setDrawFullBubble(true);
                drawable2.draw(canvas2);
                z = false;
                drawable2.setDrawFullBubble(false);
                drawable2.setAlpha(255);
            } else {
                fromX = fromX2;
                messageViewX = messageViewX2;
                alphaProgress2 = alphaProgress;
                float alphaProgress4 = listViewBottom;
                z = false;
            }
            canvas.restore();
            canvas.save();
            if (this.currentMessageObject.isOutOwner()) {
                canvas2.clipRect(((float) AndroidUtilities.dp(4.0f)) + drawableX, ((float) AndroidUtilities.dp(4.0f)) + drawableTop2, (float) (drawableRight2 - AndroidUtilities.dp(10.0f)), drawableBottom2 - ((float) AndroidUtilities.dp(4.0f)));
            } else {
                canvas2.clipRect(((float) AndroidUtilities.dp(4.0f)) + drawableX, ((float) AndroidUtilities.dp(4.0f)) + drawableTop2, (float) (drawableRight2 - AndroidUtilities.dp(4.0f)), drawableBottom2 - ((float) AndroidUtilities.dp(4.0f)));
            }
            canvas2.translate((((float) this.messageView.getLeft()) + this.listView.getX()) - this.container.getX(), ((fromY - toY) * (1.0f - progress2)) + messageViewY);
            this.messageView.drawTime(canvas2, alphaProgress2, z);
            this.messageView.drawNamesLayout(canvas2, alphaProgress2);
            this.messageView.drawCommentButton(canvas2, alphaProgress2);
            this.messageView.drawCaptionLayout(canvas2, z, alphaProgress2);
            this.messageView.drawLinkPreview(canvas2, alphaProgress2);
            canvas.restore();
            if (this.hasReply) {
                this.chatActivity.getReplyNameTextView().setAlpha(0.0f);
                this.chatActivity.getReplyObjectTextView().setAlpha(0.0f);
                float fromReplayX2 = this.replyFromStartX - this.container.getX();
                float fromReplayY = this.replyFromStartY - this.container.getY();
                float toReplayX = messageViewX + ((float) this.messageView.replyStartX);
                float toReplayY = messageViewY + ((float) this.messageView.replyStartY);
                if (!this.currentMessageObject.hasValidReplyMessageObject() || ((this.currentMessageObject.replyMessageObject.type != 0 && TextUtils.isEmpty(this.currentMessageObject.replyMessageObject.caption)) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))) {
                    replyMessageColor = getThemedColor("chat_outReplyMediaMessageText");
                } else {
                    replyMessageColor = getThemedColor("chat_outReplyMessageText");
                }
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.MessageDrawable messageDrawable = drawable2;
                    int themedColor = getThemedColor("chat_outReplyNameText");
                    replyOwnerMessageColor = getThemedColor("chat_outReplyLine");
                    replyOwnerMessageColor2 = themedColor;
                } else {
                    int themedColor2 = getThemedColor("chat_inReplyNameText");
                    replyOwnerMessageColor = getThemedColor("chat_inReplyLine");
                    replyOwnerMessageColor2 = themedColor2;
                }
                drawableRight = drawableRight2;
                drawableBottom = drawableBottom2;
                Theme.chat_replyTextPaint.setColor(ColorUtils.blendARGB(this.replayObjectFromColor, replyMessageColor, progress2));
                Theme.chat_replyNamePaint.setColor(ColorUtils.blendARGB(this.replayFromColor, replyOwnerMessageColor2, progress2));
                if (this.messageView.needReplyImage) {
                    fromReplayX = fromReplayX2 - ((float) AndroidUtilities.dp(44.0f));
                } else {
                    fromReplayX = fromReplayX2;
                }
                float replyX = ((1.0f - progressX) * fromReplayX) + (toReplayX * progressX);
                float replyY2 = (toReplayY * progress2) + (((((float) AndroidUtilities.dp(12.0f)) * progress2) + fromReplayY) * (1.0f - progress2));
                int i = replyOwnerMessageColor2;
                Theme.chat_replyLinePaint.setColor(ColorUtils.setAlphaComponent(replyOwnerMessageColor, (int) (((float) Color.alpha(replyOwnerMessageColor)) * progressX)));
                float replyY3 = replyY2;
                int i2 = replyOwnerMessageColor;
                float replyX2 = replyX;
                int i3 = replyMessageColor;
                drawableTop = drawableTop2;
                canvas.drawRect(replyX, replyY2, replyX + ((float) AndroidUtilities.dp(2.0f)), replyY2 + ((float) AndroidUtilities.dp(35.0f)), Theme.chat_replyLinePaint);
                canvas.save();
                canvas2.translate(((float) AndroidUtilities.dp(10.0f)) * progressX, 0.0f);
                if (this.messageView.needReplyImage) {
                    canvas.save();
                    replyY = replyY3;
                    this.messageView.replyImageReceiver.setImageCoords(replyX2, replyY, (float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(35.0f));
                    this.messageView.replyImageReceiver.draw(canvas2);
                    canvas2.translate(replyX2, replyY);
                    canvas.restore();
                    canvas2.translate((float) AndroidUtilities.dp(44.0f), 0.0f);
                } else {
                    replyY = replyY3;
                }
                float f6 = this.replyMessageDx;
                float replyToMessageX2 = toReplayX - f6;
                float replyMessageX = ((fromReplayX - f6) * (1.0f - progressX)) + (replyToMessageX2 * progressX);
                float replyNameX = ((1.0f - progressX) * fromReplayX) + ((toReplayX - this.replyNameDx) * progressX);
                if (this.messageView.replyNameLayout != null) {
                    canvas.save();
                    canvas2.translate(replyNameX, replyY);
                    this.messageView.replyNameLayout.draw(canvas2);
                    canvas.restore();
                }
                if (this.messageView.replyTextLayout != null) {
                    canvas.save();
                    canvas2.translate(replyMessageX, ((float) AndroidUtilities.dp(19.0f)) + replyY);
                    canvas.save();
                    SpoilerEffect.clipOutCanvas(canvas2, this.messageView.replySpoilers);
                    this.messageView.replyTextLayout.draw(canvas2);
                    canvas.restore();
                    for (SpoilerEffect eff : this.messageView.replySpoilers) {
                        float replyMessageX2 = replyMessageX;
                        if (eff.shouldInvalidateColor()) {
                            replyToMessageX = replyToMessageX2;
                            eff.setColor(this.messageView.replyTextLayout.getPaint().getColor());
                        } else {
                            replyToMessageX = replyToMessageX2;
                        }
                        eff.draw(canvas2);
                        replyToMessageX2 = replyToMessageX;
                        replyMessageX = replyMessageX2;
                    }
                    float f7 = replyToMessageX2;
                    canvas.restore();
                } else {
                    float f8 = replyToMessageX2;
                }
                canvas.restore();
            } else {
                drawableRight = drawableRight2;
                drawableBottom = drawableBottom2;
                drawableTop = drawableTop2;
            }
            canvas.save();
            canvas2.clipRect(((float) AndroidUtilities.dp(4.0f)) + drawableX, drawableTop + ((float) AndroidUtilities.dp(4.0f)), (float) (drawableRight - AndroidUtilities.dp(4.0f)), drawableBottom - ((float) AndroidUtilities.dp(4.0f)));
            float scale = progressX + (this.scaleFrom * (1.0f - progressX));
            if (this.drawBitmaps) {
                scale2 = progressX + (this.scaleY * (1.0f - progressX));
            } else {
                scale2 = 1.0f;
            }
            canvas.save();
            canvas2.translate(((1.0f - progressX) * fromX) + ((toX - this.toXOffset) * progressX), ((1.0f - progress2) * fromY) + ((toY + this.textLayoutBlock.textYOffset) * progress2));
            canvas2.scale(scale, scale * scale2, 0.0f, 0.0f);
            if (this.drawBitmaps) {
                if (this.crossfade) {
                    this.bitmapPaint.setAlpha((int) ((1.0f - alphaProgress2) * 255.0f));
                }
                canvas2.drawBitmap(this.textLayoutBitmap, 0.0f, 0.0f, this.bitmapPaint);
            } else {
                boolean z2 = this.crossfade;
                if (z2 && this.changeColor) {
                    int oldColor = this.layout.getPaint().getColor();
                    this.layout.getPaint().setColor(ColorUtils.setAlphaComponent(ColorUtils.blendARGB(this.fromColor, this.toColor, alphaProgress2), (int) (((float) Color.alpha(oldColor)) * (1.0f - alphaProgress2))));
                    this.layout.draw(canvas2);
                    this.layout.getPaint().setColor(oldColor);
                } else if (z2) {
                    int oldAlpha = Theme.chat_msgTextPaint.getAlpha();
                    Theme.chat_msgTextPaint.setAlpha((int) (((float) oldAlpha) * (1.0f - alphaProgress2)));
                    this.layout.draw(canvas2);
                    Theme.chat_msgTextPaint.setAlpha(oldAlpha);
                } else {
                    this.layout.draw(canvas2);
                }
            }
            canvas.restore();
            if (this.rtlLayout != null) {
                canvas.save();
                canvas2.translate(((1.0f - progressX) * fromX) + ((toX - this.toXOffsetRtl) * progressX), ((1.0f - progress2) * fromY) + ((toY + this.textLayoutBlock.textYOffset) * progress2));
                canvas2.scale(scale, scale * scale2, 0.0f, 0.0f);
                if (this.drawBitmaps) {
                    if (this.crossfade) {
                        this.bitmapPaint.setAlpha((int) ((1.0f - alphaProgress2) * 255.0f));
                    }
                    canvas2.drawBitmap(this.textLayoutBitmapRtl, 0.0f, 0.0f, this.bitmapPaint);
                } else {
                    boolean z3 = this.crossfade;
                    if (z3 && this.changeColor) {
                        int oldColor2 = this.rtlLayout.getPaint().getColor();
                        this.rtlLayout.getPaint().setColor(ColorUtils.setAlphaComponent(ColorUtils.blendARGB(this.fromColor, this.toColor, alphaProgress2), (int) (((float) Color.alpha(oldColor2)) * (1.0f - alphaProgress2))));
                        this.rtlLayout.draw(canvas2);
                        this.rtlLayout.getPaint().setColor(oldColor2);
                    } else if (z3) {
                        int oldAlpha2 = this.rtlLayout.getPaint().getAlpha();
                        this.rtlLayout.getPaint().setAlpha((int) (((float) oldAlpha2) * (1.0f - alphaProgress2)));
                        this.rtlLayout.draw(canvas2);
                        this.rtlLayout.getPaint().setAlpha(oldAlpha2);
                    } else {
                        this.rtlLayout.draw(canvas2);
                    }
                }
                canvas.restore();
            }
            if (this.crossfade) {
                canvas.save();
                canvas2.translate(((((float) this.messageView.getLeft()) + this.listView.getX()) - this.container.getX()) + ((fromX - toX) * (1.0f - progressX)), ((fromY - toY) * (1.0f - progress2)) + messageViewY);
                canvas2.scale(scale, scale * scale2, (float) this.messageView.getTextX(), (float) this.messageView.getTextY());
                canvas2.translate(0.0f, -this.crossfadeTextOffset);
                if (this.crossfadeTextBitmap != null) {
                    this.bitmapPaint.setAlpha((int) (alphaProgress2 * 255.0f));
                    canvas2.drawBitmap(this.crossfadeTextBitmap, 0.0f, 0.0f, this.bitmapPaint);
                } else {
                    int oldColor3 = Theme.chat_msgTextPaint.getColor();
                    Theme.chat_msgTextPaint.setColor(this.toColor);
                    ChatMessageCell chatMessageCell2 = this.messageView;
                    chatMessageCell2.drawMessageText(canvas, chatMessageCell2.getMessageObject().textLayoutBlocks, false, alphaProgress2, true);
                    if (Theme.chat_msgTextPaint.getColor() != oldColor3) {
                        Theme.chat_msgTextPaint.setColor(oldColor3);
                    }
                }
                canvas.restore();
            }
            canvas.restore();
            if (clipBottomWithAlpha) {
                int clipBottom3 = clipBottom;
                this.gradientMatrix.setTranslate(0.0f, (float) clipBottom3);
                this.gradientShader.setLocalMatrix(this.gradientMatrix);
                canvas.drawRect(0.0f, (float) clipBottom3, (float) this.container.getMeasuredWidth(), (float) this.container.getMeasuredHeight(), this.gradientPaint);
                canvas.restore();
            }
            float f9 = this.progress;
            float sendProgress = f9 > 0.4f ? 1.0f : f9 / 0.4f;
            if (sendProgress == 1.0f) {
                this.enterView.setTextTransitionIsRunning(false);
            }
            if (this.enterView.getSendButton().getVisibility() == 0 && sendProgress < 1.0f) {
                canvas.save();
                canvas2.translate(((((this.enterView.getX() + this.enterView.getSendButton().getX()) + ((View) this.enterView.getSendButton().getParent()).getX()) + ((View) this.enterView.getSendButton().getParent().getParent()).getX()) - this.container.getX()) + (((float) AndroidUtilities.dp(52.0f)) * sendProgress), (((this.enterView.getY() + this.enterView.getSendButton().getY()) + ((View) this.enterView.getSendButton().getParent()).getY()) + ((View) this.enterView.getSendButton().getParent().getParent()).getY()) - this.container.getY());
                this.enterView.getSendButton().draw(canvas2);
                canvas.restore();
                canvas.restore();
            }
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
