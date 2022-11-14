package org.telegram.ui.Components.Forum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.StaticLayoutEx;
/* loaded from: classes3.dex */
public class MessageTopicButton {
    private Context context;
    private MessageObject lastMessageObject;
    private Theme.ResourcesProvider resourcesProvider;
    private int topicArrowColor;
    private Drawable topicArrowDrawable;
    private int topicBackgroundColor;
    private AnimatedColor topicBackgroundColorAnimated;
    private float[] topicHSV;
    private int topicHeight;
    private RectF topicHitRect;
    private Drawable topicIconDrawable;
    private Rect topicIconDrawableBounds;
    private boolean topicIconWaiting;
    private int topicNameColor;
    private AnimatedColor topicNameColorAnimated;
    private StaticLayout topicNameLayout;
    private Paint topicPaint;
    private Path topicPath;
    private boolean topicPressed;
    private Drawable topicSelectorDrawable;
    private int topicWidth;
    private static final float[] lightHueRanges = {0.0f, 43.0f, 56.0f, 86.0f, 169.0f, 183.0f, 249.0f, 289.0f, 360.0f};
    private static final float[] lightSatValues = {0.6f, 1.0f, 0.95f, 0.98f, 0.8f, 0.88f, 0.51f, 0.55f, 0.6f};
    private static final float[] lightValValues = {0.79f, 0.77f, 0.6f, 0.62f, 0.6f, 0.61f, 0.8f, 0.7f, 0.79f};
    private static final float[] darkHueRanges = {0.0f, 43.0f, 56.0f, 63.0f, 86.0f, 122.0f, 147.0f, 195.0f, 205.0f, 249.0f, 270.0f, 312.0f, 388.0f, 360.0f};
    private static final float[] darkSatValues = {0.64f, 0.89f, 0.84f, 0.87f, 0.74f, 0.66f, 0.81f, 0.81f, 0.71f, 0.51f, 0.61f, 0.55f, 0.62f, 0.64f};
    private static final float[] darkValValues = {0.92f, 0.9f, 0.82f, 0.82f, 0.84f, 0.84f, 0.82f, 0.88f, 0.96f, 0.1f, 0.93f, 0.88f, 0.96f, 0.92f};
    private static final int[] idleState = new int[0];
    private static final int[] pressedState = {16842910, 16842919};

    protected void onClick() {
        throw null;
    }

    public MessageTopicButton(Context context, Theme.ResourcesProvider resourcesProvider) {
        this.context = context;
        this.resourcesProvider = resourcesProvider;
    }

    public int set(ChatMessageCell chatMessageCell, MessageObject messageObject, TLRPC$TL_forumTopic tLRPC$TL_forumTopic, int i) {
        int i2;
        int i3;
        float f;
        int i4;
        boolean z;
        Context context;
        this.lastMessageObject = messageObject;
        if (chatMessageCell == null || messageObject == null) {
            return 0;
        }
        int dp = AndroidUtilities.dp(7.0f) + ((int) Theme.chat_topicTextPaint.getTextSize());
        float dp2 = AndroidUtilities.dp(10.0f) + dp;
        float textSize = Theme.chat_topicTextPaint.getTextSize() - AndroidUtilities.dp(8.0f);
        float dp3 = AndroidUtilities.dp(5.0f) + Theme.chat_topicTextPaint.getTextSize();
        float f2 = dp2 + dp3;
        int i5 = (int) (i - f2);
        String str = tLRPC$TL_forumTopic.title;
        if (str == null) {
            str = "";
        }
        String str2 = str;
        this.topicNameLayout = StaticLayoutEx.createStaticLayout(str2, 0, str2.length(), Theme.chat_topicTextPaint, i5, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, i5, 2, false);
        int dp4 = AndroidUtilities.dp(8.5f);
        int dp5 = AndroidUtilities.dp(24.0f);
        StaticLayout staticLayout = this.topicNameLayout;
        this.topicHeight = dp4 + Math.min(dp5, staticLayout == null ? 0 : staticLayout.getHeight());
        StaticLayout staticLayout2 = this.topicNameLayout;
        int lineCount = staticLayout2 == null ? 0 : staticLayout2.getLineCount();
        Path path = this.topicPath;
        if (path == null) {
            this.topicPath = new Path();
        } else {
            path.rewind();
        }
        if (this.topicPaint == null) {
            this.topicPaint = new Paint(1);
        }
        long j = tLRPC$TL_forumTopic.icon_emoji_id;
        if (j != 0) {
            Drawable drawable = this.topicIconDrawable;
            if (!(drawable instanceof AnimatedEmojiDrawable) || j != ((AnimatedEmojiDrawable) drawable).getDocumentId()) {
                Drawable drawable2 = this.topicIconDrawable;
                if (drawable2 instanceof AnimatedEmojiDrawable) {
                    ((AnimatedEmojiDrawable) drawable2).removeView(new MessageTopicButton$$ExternalSyntheticLambda0(chatMessageCell));
                    this.topicIconDrawable = null;
                }
                AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(messageObject.currentAccount, 0, tLRPC$TL_forumTopic.icon_emoji_id);
                this.topicIconDrawable = make;
                make.addView(new MessageTopicButton$$ExternalSyntheticLambda0(chatMessageCell));
            }
            this.topicIconWaiting = false;
            Drawable drawable3 = this.topicIconDrawable;
            i2 = drawable3 instanceof AnimatedEmojiDrawable ? AnimatedEmojiDrawable.getDominantColor((AnimatedEmojiDrawable) drawable3) : 0;
            if (i2 == 0) {
                this.topicIconWaiting = true;
                i2 = getThemedColor(messageObject.isOutOwner() ? "chat_outReactionButtonText" : "chat_inReactionButtonText");
            }
        } else {
            i2 = tLRPC$TL_forumTopic.icon_color;
            this.topicIconDrawable = ForumUtilities.createSmallTopicDrawable(str2, i2);
        }
        setupColors(i2);
        if (this.topicIconWaiting) {
            if (this.topicNameColorAnimated == null) {
                this.topicNameColorAnimated = new AnimatedColor(chatMessageCell);
            }
            if (this.topicBackgroundColorAnimated == null) {
                this.topicBackgroundColorAnimated = new AnimatedColor(chatMessageCell);
            }
        }
        if (this.topicArrowDrawable == null && (context = this.context) != null) {
            this.topicArrowDrawable = context.getResources().getDrawable(R.drawable.msg_mini_topicarrow).mutate();
        }
        Drawable drawable4 = this.topicArrowDrawable;
        int alphaComponent = ColorUtils.setAlphaComponent(this.topicNameColor, 140);
        this.topicArrowColor = alphaComponent;
        drawable4.setColorFilter(new PorterDuffColorFilter(alphaComponent, PorterDuff.Mode.MULTIPLY));
        float dp6 = AndroidUtilities.dp(11.0f) + ((int) Theme.chat_topicTextPaint.getTextSize());
        int max = Math.max(1, ((int) Theme.chat_topicTextPaint.getTextSize()) + AndroidUtilities.dp(0.0f));
        if (lineCount == 2) {
            this.topicHeight = AndroidUtilities.dp(15.0f) + (((int) Theme.chat_topicTextPaint.getTextSize()) * 2);
            float lineWidth = this.topicNameLayout.getLineWidth(0) - this.topicNameLayout.getLineLeft(0);
            float lineWidth2 = this.topicNameLayout.getLineWidth(1) - this.topicNameLayout.getLineLeft(1);
            float max2 = Math.max(lineWidth, lineWidth2);
            float dp7 = (AndroidUtilities.dp(11.0f) + ((int) Theme.chat_topicTextPaint.getTextSize())) / 1.5f;
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, dp6, dp6);
            i3 = dp;
            this.topicPath.arcTo(rectF, 180.0f, 90.0f);
            float f3 = dp3 - textSize;
            if (Math.abs(lineWidth - lineWidth2) <= f3) {
                lineWidth = Math.max(lineWidth, lineWidth2 + f3);
                lineWidth2 = Math.max(lineWidth2, lineWidth - f3);
                z = true;
            } else {
                z = false;
            }
            float f4 = dp2 + textSize + lineWidth;
            rectF.set(f4 - dp7, 0.0f, f4, dp7);
            this.topicPath.arcTo(rectF, 270.0f, 90.0f);
            float dp8 = AndroidUtilities.dp(11.0f) + Theme.chat_topicTextPaint.getTextSize();
            float min = Math.min(dp7, Math.abs((lineWidth - AndroidUtilities.dp(13.0f)) - lineWidth2));
            if (!z) {
                if (lineWidth - f3 > lineWidth2) {
                    rectF.set(f4 - min, dp8 - min, f4, dp8);
                    this.topicPath.arcTo(rectF, 0.0f, 90.0f);
                    float f5 = f2 + lineWidth2;
                    rectF.set(f5, dp8, f5 + min, min + dp8);
                    this.topicPath.arcTo(rectF, 270.0f, -90.0f);
                } else {
                    float f6 = this.topicHeight - dp8;
                    rectF.set(f4, f6 - min, f4 + min, f6);
                    this.topicPath.arcTo(rectF, 180.0f, -90.0f);
                    float f7 = f2 + lineWidth2;
                    rectF.set(f7 - min, f6, f7, min + f6);
                    this.topicPath.arcTo(rectF, 270.0f, 90.0f);
                }
            }
            float f8 = max / 2;
            this.topicArrowDrawable.setBounds((int) (((AndroidUtilities.dp(-4.0f) + f2) + lineWidth2) - max), (int) (((((this.topicHeight - AndroidUtilities.dp(11.0f)) - Theme.chat_topicTextPaint.getTextSize()) + this.topicHeight) / 2.0f) - f8), (int) (AndroidUtilities.dp(-4.0f) + f2 + lineWidth2), (int) (((((this.topicHeight - AndroidUtilities.dp(11.0f)) - Theme.chat_topicTextPaint.getTextSize()) + this.topicHeight) / 2.0f) + f8));
            float f9 = lineWidth2 + f2;
            int i6 = this.topicHeight;
            rectF.set(f9 - dp7, i6 - dp7, f9, i6);
            this.topicPath.arcTo(rectF, 0.0f, 90.0f);
            int i7 = this.topicHeight;
            rectF.set(0.0f, i7 - dp6, dp6, i7);
            this.topicPath.arcTo(rectF, 90.0f, 90.0f);
            this.topicPath.close();
            f = max2;
        } else {
            i3 = dp;
            if (lineCount == 1) {
                this.topicHeight = AndroidUtilities.dp(11.0f) + ((int) Theme.chat_topicTextPaint.getTextSize());
                float lineWidth3 = this.topicNameLayout.getLineWidth(0) - this.topicNameLayout.getLineLeft(0);
                RectF rectF2 = AndroidUtilities.rectTmp;
                rectF2.set(0.0f, 0.0f, f2 + lineWidth3, this.topicHeight);
                float var_ = max / 2;
                this.topicArrowDrawable.setBounds((int) (((AndroidUtilities.dp(-4.0f) + f2) + lineWidth3) - max), (int) ((this.topicHeight / 2.0f) - var_), (int) (AndroidUtilities.dp(-4.0f) + f2 + lineWidth3), (int) ((this.topicHeight / 2.0f) + var_));
                this.topicPath.addRoundRect(rectF2, dp6, dp6, Path.Direction.CW);
                f = lineWidth3;
            } else {
                f = 0.0f;
            }
        }
        this.topicWidth = (int) ((f2 - AndroidUtilities.dp(1.0f)) + f);
        if (messageObject.isAnyKindOfSticker() || messageObject.type == 5) {
            i4 = 0;
        } else {
            int dp9 = AndroidUtilities.dp(6.0f) + this.topicHeight + 0;
            i4 = messageObject.type != 0 ? dp9 + AndroidUtilities.dp(9.0f) : dp9;
        }
        Drawable drawable5 = this.topicSelectorDrawable;
        if (drawable5 == null) {
            Drawable createSelectorDrawable = Theme.createSelectorDrawable(this.topicBackgroundColor, 2);
            this.topicSelectorDrawable = createSelectorDrawable;
            createSelectorDrawable.setCallback(chatMessageCell);
        } else {
            Theme.setSelectorDrawableColor(drawable5, this.topicBackgroundColor, true);
        }
        this.topicPaint.setColor(this.topicBackgroundColor);
        if (this.topicIconDrawable != null) {
            if (this.topicIconDrawableBounds == null) {
                this.topicIconDrawableBounds = new Rect();
            }
            this.topicIconDrawableBounds.set(AndroidUtilities.dp(5.0f), AndroidUtilities.dp((lineCount == 2 ? 3 : 0) + 2), AndroidUtilities.dp(5.0f) + i3, AndroidUtilities.dp((lineCount == 2 ? 3 : 0) + 2) + i3);
            this.topicIconDrawable.setBounds(this.topicIconDrawableBounds);
        }
        return i4;
    }

    public void onAttached(ChatMessageCell chatMessageCell) {
        Drawable drawable = this.topicIconDrawable;
        if (!(drawable instanceof AnimatedEmojiDrawable) || chatMessageCell == null) {
            return;
        }
        ((AnimatedEmojiDrawable) drawable).addView(new MessageTopicButton$$ExternalSyntheticLambda0(chatMessageCell));
    }

    public void onDetached(ChatMessageCell chatMessageCell) {
        Drawable drawable = this.topicIconDrawable;
        if (!(drawable instanceof AnimatedEmojiDrawable) || chatMessageCell == null) {
            return;
        }
        ((AnimatedEmojiDrawable) drawable).removeView(new MessageTopicButton$$ExternalSyntheticLambda0(chatMessageCell));
    }

    private void setupColors(int i) {
        MessageObject messageObject = this.lastMessageObject;
        if (messageObject != null && messageObject.shouldDrawWithoutBackground()) {
            this.topicNameColor = getThemedColor("chat_stickerReplyNameText");
            return;
        }
        MessageObject messageObject2 = this.lastMessageObject;
        if (messageObject2 != null && messageObject2.isOutOwner()) {
            this.topicNameColor = getThemedColor("chat_outReactionButtonText");
            this.topicBackgroundColor = ColorUtils.setAlphaComponent(getThemedColor("chat_outReactionButtonBackground"), 38);
            return;
        }
        if (this.topicHSV == null) {
            this.topicHSV = new float[3];
        }
        Color.colorToHSV(i, this.topicHSV);
        float[] fArr = this.topicHSV;
        float f = fArr[0];
        if (fArr[1] <= 0.02f) {
            this.topicNameColor = getThemedColor("chat_inReactionButtonText");
            this.topicBackgroundColor = ColorUtils.setAlphaComponent(getThemedColor("chat_inReactionButtonBackground"), 38);
            return;
        }
        Color.colorToHSV(getThemedColor("chat_inReactionButtonText"), this.topicHSV);
        this.topicHSV[0] = f;
        float[] fArr2 = Theme.isCurrentThemeDark() ? darkHueRanges : lightHueRanges;
        float[] fArr3 = Theme.isCurrentThemeDark() ? darkSatValues : lightSatValues;
        float[] fArr4 = Theme.isCurrentThemeDark() ? darkValValues : lightValValues;
        int i2 = 1;
        while (true) {
            if (i2 >= fArr2.length) {
                break;
            } else if (f <= fArr2[i2]) {
                int i3 = i2 - 1;
                float f2 = (f - fArr2[i3]) / (fArr2[i2] - fArr2[i3]);
                this.topicHSV[1] = AndroidUtilities.lerp(fArr3[i3], fArr3[i2], f2);
                this.topicHSV[2] = AndroidUtilities.lerp(fArr4[i3], fArr4[i2], f2);
                break;
            } else {
                i2++;
            }
        }
        this.topicNameColor = Color.HSVToColor(Color.alpha(getThemedColor("chat_inReactionButtonText")), this.topicHSV);
        this.topicBackgroundColor = Color.HSVToColor(38, this.topicHSV);
    }

    public boolean checkTouchEvent(MotionEvent motionEvent) {
        Drawable drawable;
        RectF rectF = this.topicHitRect;
        if (rectF == null) {
            this.topicPressed = false;
            return false;
        }
        boolean contains = rectF.contains(motionEvent.getX(), motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            if (contains) {
                Drawable drawable2 = this.topicSelectorDrawable;
                if (drawable2 != null) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        drawable2.setHotspot(motionEvent.getX() - this.topicHitRect.left, motionEvent.getY() - this.topicHitRect.top);
                    }
                    this.topicSelectorDrawable.setState(pressedState);
                }
                this.topicPressed = true;
            } else {
                this.topicPressed = false;
            }
            return this.topicPressed;
        } else if (motionEvent.getAction() == 2) {
            boolean z = this.topicPressed;
            if (z != contains) {
                if (z && (drawable = this.topicSelectorDrawable) != null) {
                    drawable.setState(idleState);
                }
                this.topicPressed = contains;
            }
            return this.topicPressed;
        } else {
            if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.topicPressed) {
                this.topicPressed = false;
                Drawable drawable3 = this.topicSelectorDrawable;
                if (drawable3 != null) {
                    drawable3.setState(idleState);
                }
                if (motionEvent.getAction() == 1) {
                    onClick();
                    return true;
                }
            }
            return false;
        }
    }

    public int width() {
        return this.topicWidth;
    }

    public int height() {
        return this.topicHeight;
    }

    public void draw(Canvas canvas, float f, float f2, float f3) {
        Paint paint;
        int i;
        int dominantColor;
        if (this.topicIconWaiting) {
            Drawable drawable = this.topicIconDrawable;
            if ((drawable instanceof AnimatedEmojiDrawable) && (dominantColor = AnimatedEmojiDrawable.getDominantColor((AnimatedEmojiDrawable) drawable)) != 0) {
                this.topicIconWaiting = false;
                setupColors(dominantColor);
            }
        }
        canvas.save();
        MessageObject messageObject = this.lastMessageObject;
        if (messageObject != null && messageObject.shouldDrawWithoutBackground()) {
            this.topicPath.offset(f, f2);
            int i2 = -1;
            if (f3 < 1.0f) {
                i = getThemedPaint("paintChatActionBackground").getAlpha();
                getThemedPaint("paintChatActionBackground").setAlpha((int) (i * f3));
            } else {
                i = -1;
            }
            canvas.drawPath(this.topicPath, getThemedPaint("paintChatActionBackground"));
            if (hasGradientService()) {
                if (f3 < 1.0f) {
                    int alpha = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                    Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (alpha * f3));
                    i2 = alpha;
                }
                canvas.drawPath(this.topicPath, Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            if (i >= 0) {
                getThemedPaint("paintChatActionBackground").setAlpha(i);
            }
            if (i2 >= 0) {
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(i2);
            }
            this.topicPath.offset(-f, -f2);
            canvas.translate(f, f2);
        } else {
            canvas.translate(f, f2);
            if (this.topicPath != null && (paint = this.topicPaint) != null) {
                AnimatedColor animatedColor = this.topicBackgroundColorAnimated;
                if (animatedColor != null) {
                    paint.setColor(animatedColor.set(this.topicBackgroundColor));
                } else {
                    paint.setColor(this.topicBackgroundColor);
                }
                int alpha2 = this.topicPaint.getAlpha();
                this.topicPaint.setAlpha((int) (alpha2 * f3));
                canvas.drawPath(this.topicPath, this.topicPaint);
                this.topicPaint.setAlpha(alpha2);
            }
        }
        if (this.topicHitRect == null) {
            this.topicHitRect = new RectF();
        }
        this.topicHitRect.set(f, f2, this.topicWidth + f, this.topicHeight + f2);
        if (this.topicSelectorDrawable != null) {
            canvas.save();
            canvas.clipPath(this.topicPath);
            Rect rect = AndroidUtilities.rectTmp2;
            rect.set(0, 0, this.topicWidth, this.topicHeight);
            this.topicSelectorDrawable.setBounds(rect);
            this.topicSelectorDrawable.draw(canvas);
            canvas.restore();
        }
        int i3 = this.topicNameColor;
        if (this.topicNameLayout != null) {
            canvas.save();
            canvas.translate(AndroidUtilities.dp(17.0f) + Theme.chat_topicTextPaint.getTextSize(), AndroidUtilities.dp(4.5f));
            AnimatedColor animatedColor2 = this.topicNameColorAnimated;
            if (animatedColor2 != null) {
                TextPaint textPaint = Theme.chat_topicTextPaint;
                i3 = animatedColor2.set(this.topicNameColor);
                textPaint.setColor(i3);
            } else {
                TextPaint textPaint2 = Theme.chat_topicTextPaint;
                int i4 = this.topicNameColor;
                textPaint2.setColor(i4);
                i3 = i4;
            }
            TextPaint textPaint3 = Theme.chat_topicTextPaint;
            textPaint3.setAlpha((int) (textPaint3.getAlpha() * f3));
            this.topicNameLayout.draw(canvas);
            canvas.restore();
        }
        if (this.topicArrowDrawable != null) {
            int alphaComponent = ColorUtils.setAlphaComponent(i3, 140);
            if (this.topicArrowColor != alphaComponent) {
                Drawable drawable2 = this.topicArrowDrawable;
                this.topicArrowColor = alphaComponent;
                drawable2.setColorFilter(new PorterDuffColorFilter(alphaComponent, PorterDuff.Mode.MULTIPLY));
            }
            this.topicArrowDrawable.draw(canvas);
        }
        canvas.restore();
    }

    public void drawOutbounds(Canvas canvas, float f) {
        if (this.topicHitRect != null) {
            canvas.save();
            RectF rectF = this.topicHitRect;
            canvas.translate(rectF.left, rectF.top);
            this.topicIconDrawable.setAlpha((int) (f * 255.0f));
            this.topicIconDrawable.setBounds(this.topicIconDrawableBounds);
            this.topicIconDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public void resetClick() {
        Drawable drawable = this.topicSelectorDrawable;
        if (drawable != null) {
            drawable.setState(idleState);
        }
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }
}
