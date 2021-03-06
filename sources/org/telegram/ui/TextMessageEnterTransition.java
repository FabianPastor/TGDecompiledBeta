package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.LinearInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MessageEnterTransitionContainer;

public class TextMessageEnterTransition implements MessageEnterTransitionContainer.Transition {
    /* access modifiers changed from: private */
    public int animationIndex = -1;
    private ValueAnimator animator;
    Paint bitmapPaint = new Paint(1);
    private ChatActivity chatActivity;
    MessageEnterTransitionContainer container;
    boolean crossfade;
    Bitmap crossfadeTextBitmap;
    float crossfadeTextOffset;
    /* access modifiers changed from: private */
    public final int currentAccount = UserConfig.selectedAccount;
    MessageObject currentMessageObject;
    boolean drawBitmaps = false;
    private float drawableFromBottom;
    float drawableFromTop;
    ChatActivityEnterView enterView;
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
    StaticLayout rtlLayout;
    private float scaleFrom;
    private float scaleY;
    Bitmap textLayoutBitmap;
    Bitmap textLayoutBitmapRtl;
    MessageObject.TextLayoutBlock textLayoutBlock;
    float textX;
    float textY;
    float toXOffset;
    float toXOffsetRtl;

    @SuppressLint({"WrongConstant"})
    public TextMessageEnterTransition(ChatMessageCell chatMessageCell, ChatActivity chatActivity2, RecyclerListView recyclerListView, MessageEnterTransitionContainer messageEnterTransitionContainer) {
        int i;
        int i2;
        int i3;
        int i4;
        ChatMessageCell chatMessageCell2 = chatMessageCell;
        MessageEnterTransitionContainer messageEnterTransitionContainer2 = messageEnterTransitionContainer;
        if (chatMessageCell.getMessageObject().textLayoutBlocks.size() <= 1 && chatMessageCell.getMessageObject().textLayoutBlocks.get(0).textLayout.getLineCount() <= 10) {
            this.messageView = chatMessageCell2;
            this.listView = recyclerListView;
            this.container = messageEnterTransitionContainer2;
            this.chatActivity = chatActivity2;
            this.enterView = chatActivity2.getChatActivityEnterView();
            final ChatActivityEnterView chatActivityEnterView = chatActivity2.getChatActivityEnterView();
            this.fromRadius = chatActivityEnterView.getRecordCicle().drawingCircleRadius;
            this.bitmapPaint.setFilterBitmap(true);
            this.currentMessageObject = chatMessageCell.getMessageObject();
            if (!chatMessageCell.getTransitionParams().wasDraw) {
                chatMessageCell2.draw(new Canvas());
            }
            chatMessageCell2.setEnterTransitionInProgress(true);
            CharSequence text = chatActivityEnterView.getEditField().getLayout().getText();
            CharSequence charSequence = chatMessageCell.getMessageObject().messageText;
            this.crossfade = false;
            int height = chatActivityEnterView.getEditField().getLayout().getHeight();
            TextPaint textPaint = Theme.chat_msgTextPaint;
            int dp = AndroidUtilities.dp(20.0f);
            if (chatMessageCell.getMessageObject().getEmojiOnlyCount() != 0) {
                if (chatMessageCell.getMessageObject().getEmojiOnlyCount() == 1) {
                    textPaint = Theme.chat_msgTextPaintOneEmoji;
                    dp = AndroidUtilities.dp(32.0f);
                } else if (chatMessageCell.getMessageObject().getEmojiOnlyCount() == 2) {
                    textPaint = Theme.chat_msgTextPaintTwoEmoji;
                    dp = AndroidUtilities.dp(28.0f);
                } else if (chatMessageCell.getMessageObject().getEmojiOnlyCount() == 3) {
                    textPaint = Theme.chat_msgTextPaintThreeEmoji;
                    dp = AndroidUtilities.dp(24.0f);
                }
            }
            if (text.length() != charSequence.length()) {
                this.crossfade = true;
                String charSequence2 = text.toString();
                String trim = charSequence2.trim();
                int indexOf = charSequence2.indexOf(trim);
                if (indexOf > 0) {
                    i = chatActivityEnterView.getEditField().getLayout().getLineTop(chatActivityEnterView.getEditField().getLayout().getLineForOffset(indexOf));
                    i2 = chatActivityEnterView.getEditField().getLayout().getLineBottom(chatActivityEnterView.getEditField().getLayout().getLineForOffset(indexOf + trim.length())) - i;
                } else {
                    i2 = height;
                    i = 0;
                }
                charSequence = Emoji.replaceEmoji(trim, textPaint.getFontMetricsInt(), dp, false);
            } else {
                i2 = height;
                i = 0;
            }
            this.scaleFrom = chatActivityEnterView.getEditField().getTextSize() / textPaint.getTextSize();
            int lineCount = chatActivityEnterView.getEditField().getLayout().getLineCount();
            int width = (int) (((float) chatActivityEnterView.getEditField().getLayout().getWidth()) / this.scaleFrom);
            if (Build.VERSION.SDK_INT >= 24) {
                this.layout = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), textPaint, width).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).build();
            } else {
                this.layout = new StaticLayout(charSequence, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            float y = chatActivityEnterView.getY() + chatActivityEnterView.getEditField().getY() + ((View) chatActivityEnterView.getEditField().getParent()).getY() + ((View) chatActivityEnterView.getEditField().getParent().getParent()).getY();
            this.fromStartX = chatActivityEnterView.getX() + chatActivityEnterView.getEditField().getX() + ((View) chatActivityEnterView.getEditField().getParent()).getX() + ((View) chatActivityEnterView.getEditField().getParent().getParent()).getX();
            this.fromStartY = ((((float) AndroidUtilities.dp(10.0f)) + y) - ((float) chatActivityEnterView.getEditField().getScrollY())) + ((float) i);
            this.toXOffset = 0.0f;
            float f = Float.MAX_VALUE;
            for (int i5 = 0; i5 < this.layout.getLineCount(); i5++) {
                float lineLeft = this.layout.getLineLeft(i5);
                if (lineLeft < f) {
                    f = lineLeft;
                }
            }
            if (f != Float.MAX_VALUE) {
                this.toXOffset = f;
            }
            this.scaleY = ((float) i2) / (((float) this.layout.getHeight()) * this.scaleFrom);
            this.drawableFromTop = ((float) AndroidUtilities.dp(4.0f)) + y;
            if (this.enterView.isTopViewVisible()) {
                this.drawableFromTop -= (float) AndroidUtilities.dp(4.0f);
            }
            this.drawableFromBottom = y + ((float) chatActivityEnterView.getEditField().getMeasuredHeight());
            MessageObject.TextLayoutBlock textLayoutBlock2 = chatMessageCell.getMessageObject().textLayoutBlocks.get(0);
            this.textLayoutBlock = textLayoutBlock2;
            StaticLayout staticLayout = textLayoutBlock2.textLayout;
            if (staticLayout.getLineCount() == this.layout.getLineCount()) {
                lineCount = staticLayout.getLineCount();
                int i6 = 0;
                i4 = 0;
                i3 = 0;
                while (true) {
                    if (i6 >= lineCount) {
                        break;
                    }
                    if (isRtlLine(this.layout, i6)) {
                        i3++;
                    } else {
                        i4++;
                    }
                    if (staticLayout.getLineEnd(i6) != this.layout.getLineEnd(i6)) {
                        this.crossfade = true;
                        break;
                    }
                    i6++;
                }
            } else {
                this.crossfade = true;
                i4 = 0;
                i3 = 0;
            }
            if (!this.crossfade && i3 > 0 && i4 > 0) {
                SpannableString spannableString = new SpannableString(charSequence);
                SpannableString spannableString2 = new SpannableString(charSequence);
                int i7 = 0;
                float f2 = Float.MAX_VALUE;
                while (i7 < lineCount) {
                    if (isRtlLine(this.layout, i7)) {
                        spannableString.setSpan(new EmptyStubSpan(), this.layout.getLineStart(i7), this.layout.getLineEnd(i7), 0);
                        float lineLeft2 = this.layout.getLineLeft(i7);
                        f2 = lineLeft2 < f2 ? lineLeft2 : f2;
                    } else {
                        spannableString2.setSpan(new EmptyStubSpan(), this.layout.getLineStart(i7), this.layout.getLineEnd(i7), 0);
                    }
                    i7++;
                    RecyclerListView recyclerListView2 = recyclerListView;
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    this.layout = StaticLayout.Builder.obtain(spannableString, 0, spannableString.length(), textPaint, width).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).build();
                    this.rtlLayout = StaticLayout.Builder.obtain(spannableString2, 0, spannableString2.length(), textPaint, width).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).build();
                } else {
                    TextPaint textPaint2 = textPaint;
                    int i8 = width;
                    this.layout = new StaticLayout(spannableString, textPaint2, i8, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.rtlLayout = new StaticLayout(spannableString2, textPaint2, i8, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
            }
            this.toXOffsetRtl = (float) (this.layout.getWidth() - chatMessageCell.getMessageObject().textLayoutBlocks.get(0).textLayout.getWidth());
            try {
                if (this.drawBitmaps) {
                    this.textLayoutBitmap = Bitmap.createBitmap(this.layout.getWidth(), this.layout.getHeight(), Bitmap.Config.ARGB_8888);
                    this.layout.draw(new Canvas(this.textLayoutBitmap));
                    StaticLayout staticLayout2 = this.rtlLayout;
                    if (staticLayout2 != null) {
                        this.textLayoutBitmapRtl = Bitmap.createBitmap(staticLayout2.getWidth(), this.rtlLayout.getHeight(), Bitmap.Config.ARGB_8888);
                        this.rtlLayout.draw(new Canvas(this.textLayoutBitmapRtl));
                    }
                    if (this.crossfade) {
                        if (chatMessageCell.getMeasuredHeight() < recyclerListView.getMeasuredHeight()) {
                            this.crossfadeTextOffset = 0.0f;
                            this.crossfadeTextBitmap = Bitmap.createBitmap(chatMessageCell.getMeasuredWidth(), chatMessageCell.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                        } else {
                            this.crossfadeTextOffset = (float) chatMessageCell.getTop();
                            this.crossfadeTextBitmap = Bitmap.createBitmap(chatMessageCell.getMeasuredWidth(), recyclerListView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                        }
                    }
                }
            } catch (Exception unused) {
                this.drawBitmaps = false;
            }
            boolean z = (chatMessageCell.getMessageObject().getReplyMsgId() == 0 || chatMessageCell2.replyNameLayout == null) ? false : true;
            this.hasReply = z;
            if (z) {
                SimpleTextView replyNameTextView = chatActivity2.getReplyNameTextView();
                this.replyFromStartX = replyNameTextView.getX() + ((View) replyNameTextView.getParent()).getX();
                this.replyFromStartY = replyNameTextView.getY() + ((View) replyNameTextView.getParent().getParent()).getY() + ((View) replyNameTextView.getParent().getParent().getParent()).getY();
                SimpleTextView replyObjectTextView = chatActivity2.getReplyObjectTextView();
                this.replyFromObjectStartY = replyObjectTextView.getY() + ((View) replyObjectTextView.getParent().getParent()).getY() + ((View) replyObjectTextView.getParent().getParent().getParent()).getY();
                this.replayFromColor = chatActivity2.getReplyNameTextView().getTextColor();
                this.replayObjectFromColor = chatActivity2.getReplyObjectTextView().getTextColor();
                this.drawableFromTop -= (float) AndroidUtilities.dp(46.0f);
            }
            this.gradientMatrix = new Matrix();
            Paint paint = new Paint(1);
            this.gradientPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            LinearGradient linearGradient = new LinearGradient(0.0f, (float) AndroidUtilities.dp(12.0f), 0.0f, 0.0f, 0, -16777216, Shader.TileMode.CLAMP);
            this.gradientShader = linearGradient;
            this.gradientPaint.setShader(linearGradient);
            this.messageId = chatMessageCell.getMessageObject().stableId;
            chatActivityEnterView.getEditField().setAlpha(0.0f);
            chatActivityEnterView.setTextTransitionIsRunning(true);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(chatActivityEnterView, messageEnterTransitionContainer2) {
                public final /* synthetic */ ChatActivityEnterView f$1;
                public final /* synthetic */ MessageEnterTransitionContainer f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TextMessageEnterTransition.this.lambda$new$0$TextMessageEnterTransition(this.f$1, this.f$2, valueAnimator);
                }
            });
            this.animator.setInterpolator(new LinearInterpolator());
            this.animator.setDuration(250);
            messageEnterTransitionContainer2.addTransition(this);
            this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
            final MessageEnterTransitionContainer messageEnterTransitionContainer3 = messageEnterTransitionContainer;
            final ChatMessageCell chatMessageCell3 = chatMessageCell;
            final ChatActivity chatActivity3 = chatActivity2;
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    NotificationCenter.getInstance(TextMessageEnterTransition.this.currentAccount).onAnimationFinish(TextMessageEnterTransition.this.animationIndex);
                    messageEnterTransitionContainer3.removeTransition(TextMessageEnterTransition.this);
                    chatMessageCell3.setEnterTransitionInProgress(false);
                    chatActivityEnterView.setTextTransitionIsRunning(false);
                    chatActivityEnterView.getEditField().setAlpha(1.0f);
                    chatActivity3.getReplyNameTextView().setAlpha(1.0f);
                    chatActivity3.getReplyObjectTextView().setAlpha(1.0f);
                }
            });
            this.fromMessageDrawable = chatMessageCell2.getCurrentBackgroundDrawable(false).getTransitionDrawable(Theme.getColor("chat_messagePanelBackground"));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$TextMessageEnterTransition(ChatActivityEnterView chatActivityEnterView, MessageEnterTransitionContainer messageEnterTransitionContainer, ValueAnimator valueAnimator) {
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

    /* JADX WARNING: Removed duplicated region for block: B:61:0x0347  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0354  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0384  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x03dc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r35) {
        /*
            r34 = this;
            r0 = r34
            r8 = r35
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
            r0.lastMessageX = r13
            r0.lastMessageY = r14
            android.view.animation.Interpolator r2 = androidx.recyclerview.widget.ChatListItemAnimator.DEFAULT_INTERPOLATOR
            float r3 = r0.progress
            float r15 = r2.getInterpolation(r3)
            float r2 = r0.progress
            r3 = 1058642330(0x3var_a, float:0.6)
            r16 = 1065353216(0x3var_, float:1.0)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x00cd
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x00d0
        L_0x00cd:
            float r3 = r2 / r3
            r7 = r3
        L_0x00d0:
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r3.getInterpolation(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r17 = r3.getInterpolation(r2)
            float r2 = r0.textX
            float r18 = r13 + r2
            float r2 = r0.textY
            float r19 = r14 + r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            int r2 = r2.getMeasuredHeight()
            float r2 = (float) r2
            float r20 = r16 - r17
            float r2 = r2 * r20
            float r1 = r1 * r17
            float r2 = r2 + r1
            int r6 = (int) r2
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBottom()
            r21 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r1 = r1 - r2
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            int r2 = r2.getMeasuredHeight()
            r5 = 0
            if (r1 <= r2) goto L_0x010b
            r1 = 1
            goto L_0x010c
        L_0x010b:
            r1 = 0
        L_0x010c:
            if (r1 == 0) goto L_0x012e
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
            if (r1 <= 0) goto L_0x012e
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredHeight()
            if (r1 <= 0) goto L_0x012e
            r22 = 1
            goto L_0x0130
        L_0x012e:
            r22 = 0
        L_0x0130:
            if (r22 == 0) goto L_0x015b
            r2 = 0
            float r3 = java.lang.Math.max(r10, r14)
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredWidth()
            float r4 = (float) r1
            org.telegram.ui.MessageEnterTransitionContainer r1 = r0.container
            int r1 = r1.getMeasuredHeight()
            float r1 = (float) r1
            r23 = 255(0xff, float:3.57E-43)
            r24 = 31
            r25 = r1
            r1 = r35
            r5 = r25
            r27 = r6
            r6 = r23
            r28 = r7
            r7 = r24
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x015f
        L_0x015b:
            r27 = r6
            r28 = r7
        L_0x015f:
            r35.save()
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            int r1 = r1.getTop()
            float r1 = (float) r1
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
            r35.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableLeft()
            float r1 = (float) r1
            float r1 = r1 + r13
            float r2 = r0.toXOffset
            float r2 = r18 - r2
            float r2 = r11 - r2
            float r2 = r2 * r20
            float r7 = r1 + r2
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableTop()
            float r1 = (float) r1
            float r1 = r1 + r14
            float r2 = r0.drawableFromTop
            org.telegram.ui.MessageEnterTransitionContainer r3 = r0.container
            float r3 = r3.getY()
            float r2 = r2 - r3
            float r23 = r16 - r15
            float r2 = r2 * r23
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
            float r3 = r3 * r23
            float r1 = r1 + r2
            float r1 = r1 * r15
            float r24 = r3 + r1
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getBackgroundDrawableRight()
            float r1 = (float) r1
            float r1 = r1 + r13
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r2 = r2 * r20
            float r1 = r1 + r2
            int r5 = (int) r1
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r1.getCurrentBackgroundDrawable(r9)
            if (r1 == 0) goto L_0x024d
            r35.save()
            r8.translate(r7, r6)
            float r2 = r24 - r6
            int r2 = (int) r2
            float r3 = (float) r5
            float r3 = r3 - r7
            int r3 = (int) r3
            org.telegram.ui.Cells.ChatMessageCell r4 = r0.messageView
            r10 = 0
            r4.setBackgroundTopY(r10)
            android.graphics.drawable.Drawable r4 = r1.getShadowDrawable()
            r9 = r28
            int r28 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r28 == 0) goto L_0x021e
            r28 = r11
            android.graphics.drawable.Drawable r11 = r0.fromMessageDrawable
            r11.setBounds(r10, r10, r3, r2)
            android.graphics.drawable.Drawable r11 = r0.fromMessageDrawable
            r11.draw(r8)
            goto L_0x0220
        L_0x021e:
            r28 = r11
        L_0x0220:
            if (r4 == 0) goto L_0x0236
            r26 = 1132396544(0x437var_, float:255.0)
            float r11 = r17 * r26
            int r11 = (int) r11
            r4.setAlpha(r11)
            r4.setBounds(r10, r10, r3, r2)
            r4.draw(r8)
            r11 = 255(0xff, float:3.57E-43)
            r4.setAlpha(r11)
            goto L_0x023a
        L_0x0236:
            r11 = 255(0xff, float:3.57E-43)
            r26 = 1132396544(0x437var_, float:255.0)
        L_0x023a:
            float r4 = r9 * r26
            int r4 = (int) r4
            r1.setAlpha(r4)
            r1.setBounds(r10, r10, r3, r2)
            r1.draw(r8)
            r1.setAlpha(r11)
            r35.restore()
            goto L_0x0252
        L_0x024d:
            r9 = r28
            r10 = 0
            r28 = r11
        L_0x0252:
            r35.restore()
            r35.save()
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = r1.isOutOwner()
            r11 = 1092616192(0x41200000, float:10.0)
            if (r1 == 0) goto L_0x0280
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 + r7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r2 = r2 + r6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r3 = r5 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r4 = (float) r4
            float r4 = r24 - r4
            r8.clipRect(r1, r2, r3, r4)
            goto L_0x029d
        L_0x0280:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r1 = r1 + r7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r2 = r2 + r6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r3 = r5 - r3
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r4 = (float) r4
            float r4 = r24 - r4
            r8.clipRect(r1, r2, r3, r4)
        L_0x029d:
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            int r1 = r1.getLeft()
            float r1 = (float) r1
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            float r2 = r2.getX()
            float r1 = r1 + r2
            org.telegram.ui.MessageEnterTransitionContainer r2 = r0.container
            float r2 = r2.getX()
            float r1 = r1 - r2
            float r2 = r12 - r19
            float r2 = r2 * r23
            float r4 = r14 + r2
            r8.translate(r1, r4)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawTime(r8, r9, r10)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawNamesLayout(r8, r9)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawCommentButton(r8, r9)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawCaptionLayout(r8, r10, r9)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            r1.drawLinkPreview(r8, r9)
            r35.restore()
            boolean r1 = r0.hasReply
            if (r1 == 0) goto L_0x0432
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
            int r10 = r3.replyStartX
            float r10 = (float) r10
            float r13 = r13 + r10
            int r3 = r3.replyStartY
            float r3 = (float) r3
            float r14 = r14 + r3
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.hasValidReplyMessageObject()
            if (r3 == 0) goto L_0x0339
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.messenger.MessageObject r3 = r3.replyMessageObject
            int r10 = r3.type
            if (r10 == 0) goto L_0x0322
            java.lang.CharSequence r3 = r3.caption
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0339
        L_0x0322:
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.messenger.MessageObject r3 = r3.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r10 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r10 != 0) goto L_0x0339
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r3 != 0) goto L_0x0339
            java.lang.String r3 = "chat_outReplyMessageText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            goto L_0x033f
        L_0x0339:
            java.lang.String r3 = "chat_outReplyMediaMessageText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
        L_0x033f:
            org.telegram.messenger.MessageObject r10 = r0.currentMessageObject
            boolean r10 = r10.isOutOwner()
            if (r10 == 0) goto L_0x0354
            java.lang.String r10 = "chat_outReplyNameText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            java.lang.String r29 = "chat_outReplyLine"
            int r29 = org.telegram.ui.ActionBar.Theme.getColor(r29)
            goto L_0x0360
        L_0x0354:
            java.lang.String r10 = "chat_inReplyNameText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            java.lang.String r29 = "chat_inReplyLine"
            int r29 = org.telegram.ui.ActionBar.Theme.getColor(r29)
        L_0x0360:
            r30 = r4
            r11 = r29
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            r31 = r5
            int r5 = r0.replayObjectFromColor
            int r3 = androidx.core.graphics.ColorUtils.blendARGB(r5, r3, r15)
            r4.setColor(r3)
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint
            int r4 = r0.replayFromColor
            int r4 = androidx.core.graphics.ColorUtils.blendARGB(r4, r10, r15)
            r3.setColor(r4)
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            boolean r3 = r3.needReplyImage
            r10 = 1110441984(0x42300000, float:44.0)
            if (r3 == 0) goto L_0x038a
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r3 = (float) r3
            float r1 = r1 - r3
        L_0x038a:
            float r1 = r1 * r20
            float r13 = r13 * r17
            float r13 = r13 + r1
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r15
            float r2 = r2 + r1
            float r2 = r2 * r23
            float r14 = r14 * r15
            float r14 = r14 + r2
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint
            r1.setColor(r11)
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r4 = r13 + r1
            r11 = 1108082688(0x420CLASSNAME, float:35.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r1 = (float) r1
            float r5 = r14 + r1
            android.graphics.Paint r32 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint
            r1 = r35
            r2 = r13
            r3 = r14
            r33 = r30
            r30 = r31
            r31 = r6
            r6 = r32
            r1.drawRect(r2, r3, r4, r5, r6)
            r35.save()
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r17
            r2 = 0
            r8.translate(r1, r2)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            boolean r1 = r1.needReplyImage
            if (r1 == 0) goto L_0x0406
            r35.save()
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.ImageReceiver r1 = r1.replyImageReceiver
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r3 = (float) r3
            r1.setImageCoords(r13, r14, r2, r3)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.ImageReceiver r1 = r1.replyImageReceiver
            r1.draw(r8)
            r8.translate(r13, r14)
            r35.restore()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r1 = (float) r1
            r2 = 0
            r8.translate(r1, r2)
        L_0x0406:
            r35.save()
            r8.translate(r13, r14)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            android.text.StaticLayout r1 = r1.replyNameLayout
            r1.draw(r8)
            r35.restore()
            r35.save()
            r1 = 1100480512(0x41980000, float:19.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r14 = r14 + r1
            r8.translate(r13, r14)
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            android.text.StaticLayout r1 = r1.replyTextLayout
            r1.draw(r8)
            r35.restore()
            r35.restore()
            goto L_0x0438
        L_0x0432:
            r33 = r4
            r30 = r5
            r31 = r6
        L_0x0438:
            r35.save()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r7 = r7 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r6 = r31 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r5 = r30 - r1
            float r1 = (float) r5
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r2 = (float) r2
            float r2 = r24 - r2
            r8.clipRect(r7, r6, r1, r2)
            float r1 = r0.scaleFrom
            float r1 = r1 * r20
            float r1 = r17 + r1
            boolean r2 = r0.drawBitmaps
            if (r2 == 0) goto L_0x046a
            float r2 = r0.scaleY
            float r2 = r2 * r20
            float r2 = r17 + r2
            goto L_0x046c
        L_0x046a:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x046c:
            r35.save()
            float r11 = r28 * r20
            float r3 = r0.toXOffset
            float r3 = r18 - r3
            float r3 = r3 * r17
            float r3 = r3 + r11
            float r12 = r12 * r23
            org.telegram.messenger.MessageObject$TextLayoutBlock r4 = r0.textLayoutBlock
            float r4 = r4.textYOffset
            float r4 = r19 + r4
            float r4 = r4 * r15
            float r4 = r4 + r12
            r8.translate(r3, r4)
            float r2 = r2 * r1
            r3 = 0
            r8.scale(r1, r2, r3, r3)
            boolean r3 = r0.drawBitmaps
            if (r3 == 0) goto L_0x04a9
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x04a0
            android.graphics.Paint r3 = r0.bitmapPaint
            float r4 = r16 - r9
            r5 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r5
            int r4 = (int) r4
            r3.setAlpha(r4)
        L_0x04a0:
            android.graphics.Bitmap r3 = r0.textLayoutBitmap
            android.graphics.Paint r4 = r0.bitmapPaint
            r5 = 0
            r8.drawBitmap(r3, r5, r5, r4)
            goto L_0x04ce
        L_0x04a9:
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x04c9
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            int r3 = r3.getAlpha()
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            float r5 = (float) r3
            float r6 = r16 - r9
            float r5 = r5 * r6
            int r5 = (int) r5
            r4.setAlpha(r5)
            android.text.StaticLayout r4 = r0.layout
            r4.draw(r8)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r4.setAlpha(r3)
            goto L_0x04ce
        L_0x04c9:
            android.text.StaticLayout r3 = r0.layout
            r3.draw(r8)
        L_0x04ce:
            r35.restore()
            android.text.StaticLayout r3 = r0.rtlLayout
            if (r3 == 0) goto L_0x0535
            r35.save()
            float r3 = r0.toXOffsetRtl
            float r3 = r18 - r3
            float r3 = r3 * r17
            float r11 = r11 + r3
            org.telegram.messenger.MessageObject$TextLayoutBlock r3 = r0.textLayoutBlock
            float r3 = r3.textYOffset
            float r19 = r19 + r3
            float r19 = r19 * r15
            float r12 = r12 + r19
            r8.translate(r11, r12)
            r3 = 0
            r8.scale(r1, r2, r3, r3)
            boolean r3 = r0.drawBitmaps
            if (r3 == 0) goto L_0x050d
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x0504
            android.graphics.Paint r3 = r0.bitmapPaint
            float r4 = r16 - r9
            r5 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r5
            int r4 = (int) r4
            r3.setAlpha(r4)
        L_0x0504:
            android.graphics.Bitmap r3 = r0.textLayoutBitmapRtl
            android.graphics.Paint r4 = r0.bitmapPaint
            r5 = 0
            r8.drawBitmap(r3, r5, r5, r4)
            goto L_0x0532
        L_0x050d:
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x052d
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            int r3 = r3.getAlpha()
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            float r5 = (float) r3
            float r6 = r16 - r9
            float r5 = r5 * r6
            int r5 = (int) r5
            r4.setAlpha(r5)
            android.text.StaticLayout r4 = r0.rtlLayout
            r4.draw(r8)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r4.setAlpha(r3)
            goto L_0x0532
        L_0x052d:
            android.text.StaticLayout r3 = r0.rtlLayout
            r3.draw(r8)
        L_0x0532:
            r35.restore()
        L_0x0535:
            boolean r3 = r0.crossfade
            if (r3 == 0) goto L_0x059c
            r35.save()
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageView
            int r3 = r3.getLeft()
            float r3 = (float) r3
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            float r4 = r4.getX()
            float r3 = r3 + r4
            org.telegram.ui.MessageEnterTransitionContainer r4 = r0.container
            float r4 = r4.getX()
            float r3 = r3 - r4
            float r11 = r28 - r18
            float r11 = r11 * r20
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
            boolean r1 = r0.drawBitmaps
            if (r1 == 0) goto L_0x0589
            android.graphics.Paint r1 = r0.bitmapPaint
            r3 = 1132396544(0x437var_, float:255.0)
            float r7 = r9 * r3
            int r3 = (int) r7
            r1.setAlpha(r3)
            android.graphics.Bitmap r1 = r0.crossfadeTextBitmap
            android.graphics.Paint r3 = r0.bitmapPaint
            r8.drawBitmap(r1, r2, r2, r3)
            goto L_0x0599
        L_0x0589:
            org.telegram.ui.Cells.ChatMessageCell r1 = r0.messageView
            org.telegram.messenger.MessageObject r2 = r1.getMessageObject()
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r3 = r2.textLayoutBlocks
            r4 = 1
            r6 = 1
            r2 = r35
            r5 = r9
            r1.drawMessageText(r2, r3, r4, r5, r6)
        L_0x0599:
            r35.restore()
        L_0x059c:
            r35.restore()
            if (r22 == 0) goto L_0x05ca
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
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
            r35.restore()
        L_0x05ca:
            float r1 = r0.progress
            r2 = 1053609165(0x3ecccccd, float:0.4)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x05d6
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x05d7
        L_0x05d6:
            float r1 = r1 / r2
        L_0x05d7:
            int r2 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x05e1
            org.telegram.ui.Components.ChatActivityEnterView r2 = r0.enterView
            r3 = 0
            r2.setTextTransitionIsRunning(r3)
        L_0x05e1:
            org.telegram.ui.Components.ChatActivityEnterView r2 = r0.enterView
            android.view.View r2 = r2.getSendButton()
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x068c
            int r2 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r2 >= 0) goto L_0x068c
            r35.save()
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
            r35.restore()
            r35.restore()
        L_0x068c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TextMessageEnterTransition.onDraw(android.graphics.Canvas):void");
    }
}
