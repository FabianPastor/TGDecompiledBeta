package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;

public class MessageContainsEmojiButton extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public boolean checkWidth;
    private int currentAccount;
    private AnimatedEmojiDrawable emojiDrawable;
    /* access modifiers changed from: private */
    public Rect emojiDrawableBounds = new Rect();
    private CharSequence endText;
    private TLRPC$InputStickerSet inputStickerSet;
    private int lastLineHeight;
    /* access modifiers changed from: private */
    public int lastLineMargin;
    /* access modifiers changed from: private */
    public int lastLineTop;
    private CharSequence lastMainTextText;
    private int lastMainTextWidth;
    private CharSequence lastSecondPartText;
    private int lastSecondPartTextWidth;
    private int lastWidth;
    private ValueAnimator loadAnimator;
    private float loadT;
    private Rect loadingBoundsFrom;
    private Rect loadingBoundsTo;
    private LoadingDrawable loadingDrawable;
    private boolean loadingDrawableBoundsSet;
    private CharSequence mainText;
    private StaticLayout mainTextLayout;
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    private CharSequence secondPartText;
    private StaticLayout secondPartTextLayout;
    private TextPaint textPaint;

    private class BoldAndAccent extends CharacterStyle {
        private BoldAndAccent() {
        }

        public void updateDrawState(TextPaint textPaint) {
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            int alpha = textPaint.getAlpha();
            textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlueText", MessageContainsEmojiButton.this.resourcesProvider));
            textPaint.setAlpha(alpha);
        }
    }

    public MessageContainsEmojiButton(int i, Context context, Theme.ResourcesProvider resourcesProvider2, ArrayList<TLRPC$InputStickerSet> arrayList, int i2) {
        super(context);
        String str;
        String str2;
        TLRPC$Document tLRPC$Document;
        TLRPC$TL_messages_stickerSet stickerSet;
        TLRPC$StickerSet tLRPC$StickerSet;
        ArrayList<TLRPC$Document> arrayList2;
        String str3;
        int i3 = 0;
        this.loadingDrawableBoundsSet = false;
        this.lastWidth = -1;
        this.checkWidth = true;
        this.loadT = 0.0f;
        this.currentAccount = i;
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor("listSelectorSDK21", resourcesProvider2), 0, 6));
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(Theme.getColor("actionBarDefaultSubmenuItem", resourcesProvider2));
        if (arrayList.size() > 1) {
            if (i2 == 0) {
                str3 = LocaleController.formatPluralString("MessageContainsEmojiPacks", arrayList.size(), new Object[0]);
            } else {
                str3 = LocaleController.formatPluralString("MessageContainsReactionsPacks", arrayList.size(), new Object[0]);
            }
            SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(str3);
            this.mainText = replaceTags;
            Spannable spannable = replaceTags;
            TypefaceSpan[] typefaceSpanArr = (TypefaceSpan[]) spannable.getSpans(0, replaceTags.length(), TypefaceSpan.class);
            while (typefaceSpanArr != null && i3 < typefaceSpanArr.length) {
                int spanStart = spannable.getSpanStart(typefaceSpanArr[i3]);
                int spanEnd = spannable.getSpanEnd(typefaceSpanArr[i3]);
                spannable.removeSpan(typefaceSpanArr[i3]);
                spannable.setSpan(new BoldAndAccent(), spanStart, spanEnd, 33);
                i3++;
            }
        } else if (arrayList.size() == 1) {
            if (i2 == 0) {
                str = LocaleController.getString("MessageContainsEmojiPack", R.string.MessageContainsEmojiPack);
            } else {
                str = LocaleController.getString("MessageContainsReactionsPack", R.string.MessageContainsReactionsPack);
            }
            String[] split = str.split("%s");
            if (split.length <= 1) {
                this.mainText = str;
                return;
            }
            TLRPC$InputStickerSet tLRPC$InputStickerSet = arrayList.get(0);
            this.inputStickerSet = tLRPC$InputStickerSet;
            if (tLRPC$InputStickerSet == null || (stickerSet = MediaDataController.getInstance(i).getStickerSet(this.inputStickerSet, false)) == null || (tLRPC$StickerSet = stickerSet.set) == null) {
                str2 = null;
                tLRPC$Document = null;
            } else {
                str2 = tLRPC$StickerSet.title;
                int i4 = 0;
                while (true) {
                    ArrayList<TLRPC$Document> arrayList3 = stickerSet.documents;
                    if (arrayList3 == null || i4 >= arrayList3.size()) {
                        tLRPC$Document = null;
                    } else if (stickerSet.documents.get(i4).id == stickerSet.set.thumb_document_id) {
                        tLRPC$Document = stickerSet.documents.get(i4);
                        break;
                    } else {
                        i4++;
                    }
                }
                if (tLRPC$Document == null && (arrayList2 = stickerSet.documents) != null && arrayList2.size() > 0) {
                    tLRPC$Document = stickerSet.documents.get(0);
                }
            }
            if (str2 == null || tLRPC$Document == null) {
                this.mainText = split[0];
                this.endText = split[1];
                LoadingDrawable loadingDrawable2 = new LoadingDrawable(resourcesProvider2);
                this.loadingDrawable = loadingDrawable2;
                loadingDrawable2.paint.setPathEffect(new CornerPathEffect((float) AndroidUtilities.dp(4.0f)));
                return;
            }
            SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document));
            spannableString.setSpan(new AnimatedEmojiSpan(tLRPC$Document, this.textPaint.getFontMetricsInt()) {
                public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                    int i6 = i5 + i3;
                    int i7 = this.measuredSize;
                    MessageContainsEmojiButton.this.emojiDrawableBounds.set((int) f, (i6 - i7) / 2, (int) (f + ((float) i7)), (i6 + i7) / 2);
                }
            }, 0, spannableString.length(), 33);
            AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(i, 0, tLRPC$Document);
            this.emojiDrawable = make;
            make.addView((View) this);
            SpannableString spannableString2 = new SpannableString(str2);
            spannableString2.setSpan(new BoldAndAccent(), 0, spannableString2.length(), 33);
            this.mainText = new SpannableStringBuilder().append(split[0]).append(spannableString).append(' ').append(spannableString2).append(split[1]);
            this.loadT = 1.0f;
            this.inputStickerSet = null;
        }
    }

    private int updateLayout(int i, boolean z) {
        float f;
        int i2 = i;
        CharSequence charSequence = this.mainText;
        int i3 = 0;
        if (!(charSequence == this.lastMainTextText && this.lastMainTextWidth == i2)) {
            if (charSequence != null) {
                CharSequence charSequence2 = this.mainText;
                StaticLayout staticLayout = new StaticLayout(charSequence2, 0, charSequence2.length(), this.textPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.mainTextLayout = staticLayout;
                if (this.loadingDrawable != null && this.loadingBoundsTo == null) {
                    int lineCount = staticLayout.getLineCount() - 1;
                    this.lastLineMargin = ((int) this.mainTextLayout.getPrimaryHorizontal(this.mainText.length())) + AndroidUtilities.dp(2.0f);
                    this.lastLineTop = this.mainTextLayout.getLineTop(lineCount);
                    int lineBottom = (int) ((float) this.mainTextLayout.getLineBottom(lineCount));
                    this.lastLineHeight = lineBottom - this.lastLineTop;
                    float min = (float) Math.min(AndroidUtilities.dp(100.0f), this.mainTextLayout.getWidth() - this.lastLineMargin);
                    if (this.loadingBoundsFrom == null) {
                        this.loadingBoundsFrom = new Rect();
                    }
                    this.loadingBoundsFrom.set(this.lastLineMargin, this.lastLineTop + AndroidUtilities.dp(1.25f), (int) (((float) this.lastLineMargin) + min), lineBottom + AndroidUtilities.dp(1.25f));
                    this.loadingDrawable.setBounds(this.loadingBoundsFrom);
                    this.loadingDrawableBoundsSet = true;
                }
            } else {
                this.mainTextLayout = null;
                this.loadingDrawableBoundsSet = false;
            }
            this.lastMainTextText = this.mainText;
            this.lastMainTextWidth = i2;
        }
        CharSequence charSequence3 = this.secondPartText;
        if (!(charSequence3 == this.lastSecondPartText && this.lastSecondPartTextWidth == i2)) {
            if (charSequence3 != null) {
                CharSequence charSequence4 = this.secondPartText;
                this.secondPartTextLayout = new StaticLayout(charSequence4, 0, charSequence4.length(), this.textPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } else {
                this.secondPartTextLayout = null;
            }
            this.lastSecondPartText = this.secondPartText;
            this.lastSecondPartTextWidth = i2;
        }
        StaticLayout staticLayout2 = this.mainTextLayout;
        if (staticLayout2 != null) {
            i3 = staticLayout2.getHeight();
        }
        StaticLayout staticLayout3 = this.secondPartTextLayout;
        if (staticLayout3 != null) {
            f = ((float) (staticLayout3.getHeight() - this.lastLineHeight)) * (z ? 1.0f : this.loadT);
        } else {
            f = 0.0f;
        }
        return i3 + ((int) f);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        int size = View.MeasureSpec.getSize(i);
        if (this.checkWidth && (i3 = this.lastWidth) > 0) {
            size = Math.min(size, i3);
        }
        this.lastWidth = size;
        int paddingLeft = (size - getPaddingLeft()) - getPaddingRight();
        if (paddingLeft < 0) {
            paddingLeft = 0;
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(updateLayout(paddingLeft, false) + getPaddingTop() + getPaddingBottom(), NUM));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Rect rect;
        super.onDraw(canvas);
        if (this.mainTextLayout != null) {
            canvas.save();
            canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
            this.textPaint.setAlpha(255);
            this.mainTextLayout.draw(canvas);
            LoadingDrawable loadingDrawable2 = this.loadingDrawable;
            if (loadingDrawable2 != null && this.loadingDrawableBoundsSet) {
                loadingDrawable2.setAlpha((int) ((1.0f - this.loadT) * 255.0f));
                Rect rect2 = this.loadingBoundsFrom;
                if (!(rect2 == null || (rect = this.loadingBoundsTo) == null)) {
                    float f = this.loadT;
                    Rect rect3 = AndroidUtilities.rectTmp2;
                    AndroidUtilities.lerp(rect2, rect, f, rect3);
                    this.loadingDrawable.setBounds(rect3);
                }
                this.loadingDrawable.draw(canvas);
                invalidate();
            }
            if (this.secondPartTextLayout != null) {
                canvas.save();
                canvas.translate(0.0f, (float) this.lastLineTop);
                this.textPaint.setAlpha((int) (this.loadT * 255.0f));
                this.secondPartTextLayout.draw(canvas);
                canvas.restore();
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.setAlpha((int) (this.loadT * 255.0f));
                this.emojiDrawable.setBounds(this.emojiDrawableBounds);
                this.emojiDrawable.draw(canvas);
            }
            canvas.restore();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$TL_messages_stickerSet stickerSet;
        String str;
        TLRPC$Document tLRPC$Document;
        ArrayList<TLRPC$Document> arrayList;
        if (i == NotificationCenter.groupStickersDidLoad && this.inputStickerSet != null && (stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSet, false)) != null) {
            TLRPC$StickerSet tLRPC$StickerSet = stickerSet.set;
            if (tLRPC$StickerSet != null) {
                str = tLRPC$StickerSet.title;
                int i3 = 0;
                while (true) {
                    ArrayList<TLRPC$Document> arrayList2 = stickerSet.documents;
                    if (arrayList2 == null || i3 >= arrayList2.size()) {
                        tLRPC$Document = null;
                    } else if (stickerSet.documents.get(i3).id == stickerSet.set.thumb_document_id) {
                        tLRPC$Document = stickerSet.documents.get(i3);
                        break;
                    } else {
                        i3++;
                    }
                }
                if (tLRPC$Document == null && (arrayList = stickerSet.documents) != null && arrayList.size() > 0) {
                    tLRPC$Document = stickerSet.documents.get(0);
                }
            } else {
                str = null;
                tLRPC$Document = null;
            }
            if (str != null && tLRPC$Document != null) {
                AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(this.currentAccount, 0, tLRPC$Document);
                this.emojiDrawable = make;
                make.addView((View) this);
                invalidate();
                SpannableString spannableString = new SpannableString(" ");
                spannableString.setSpan(new ReplacementSpan() {
                    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                    }

                    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                        return MessageContainsEmojiButton.this.lastLineMargin;
                    }
                }, 0, 1, 33);
                SpannableString spannableString2 = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document));
                spannableString2.setSpan(new AnimatedEmojiSpan(tLRPC$Document, this.textPaint.getFontMetricsInt()) {
                    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                        int access$400 = MessageContainsEmojiButton.this.lastLineTop;
                        int i6 = i5 + i3;
                        int i7 = this.measuredSize;
                        MessageContainsEmojiButton.this.emojiDrawableBounds.set((int) f, access$400 + ((i6 - i7) / 2), (int) (f + ((float) i7)), MessageContainsEmojiButton.this.lastLineTop + ((i6 + this.measuredSize) / 2));
                    }
                }, 0, spannableString2.length(), 33);
                SpannableString spannableString3 = new SpannableString(str);
                spannableString3.setSpan(new BoldAndAccent(), 0, spannableString3.length(), 33);
                this.secondPartText = new SpannableStringBuilder().append(spannableString).append(spannableString2).append(' ').append(spannableString3).append(this.endText);
                int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
                int updateLayout = updateLayout((this.lastWidth - getPaddingLeft()) - getPaddingRight(), true);
                if (!(this.loadingBoundsFrom == null || this.secondPartTextLayout == null)) {
                    if (this.loadingBoundsTo == null) {
                        this.loadingBoundsTo = new Rect();
                    }
                    StaticLayout staticLayout = this.secondPartTextLayout;
                    float primaryHorizontal = staticLayout.getPrimaryHorizontal(staticLayout.getLineEnd(0));
                    Rect rect = this.loadingBoundsTo;
                    Rect rect2 = this.loadingBoundsFrom;
                    rect.set(rect2.left, rect2.top, (int) primaryHorizontal, rect2.bottom);
                }
                this.inputStickerSet = null;
                ValueAnimator valueAnimator = this.loadAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                boolean z = Math.abs(measuredHeight - updateLayout) > AndroidUtilities.dp(3.0f);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.loadT, 1.0f});
                this.loadAnimator = ofFloat;
                ofFloat.addUpdateListener(new MessageContainsEmojiButton$$ExternalSyntheticLambda0(this, z));
                this.loadAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.loadAnimator.setStartDelay(150);
                this.loadAnimator.setDuration(400);
                this.loadAnimator.start();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$0(boolean z, ValueAnimator valueAnimator) {
        this.loadT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (z) {
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.removeView((View) this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.addView((View) this);
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
    }
}
