package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.List;

public class FadingTextViewLayout extends FrameLayout {
    private final ValueAnimator animator;
    /* access modifiers changed from: private */
    public TextView currentView;
    /* access modifiers changed from: private */
    public TextView foregroundView;
    /* access modifiers changed from: private */
    public TextView nextView;
    /* access modifiers changed from: private */
    public CharSequence text;

    public FadingTextViewLayout(Context context) {
        this(context, false);
    }

    public FadingTextViewLayout(Context context, boolean hasStaticChars) {
        super(context);
        for (int i = 0; i < (hasStaticChars) + true; i++) {
            TextView textView = new TextView(context);
            onTextViewCreated(textView);
            addView(textView);
            if (i == 0) {
                this.currentView = textView;
            } else {
                textView.setVisibility(8);
                if (i == 1) {
                    textView.setAlpha(0.0f);
                    this.nextView = textView;
                } else {
                    this.foregroundView = textView;
                }
            }
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.animator = ofFloat;
        ofFloat.setDuration(200);
        ofFloat.setInterpolator((TimeInterpolator) null);
        ofFloat.addUpdateListener(new FadingTextViewLayout$$ExternalSyntheticLambda0(this));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                FadingTextViewLayout.this.currentView.setLayerType(0, (Paint) null);
                FadingTextViewLayout.this.nextView.setLayerType(0, (Paint) null);
                FadingTextViewLayout.this.nextView.setVisibility(8);
                if (FadingTextViewLayout.this.foregroundView != null) {
                    FadingTextViewLayout.this.currentView.setText(FadingTextViewLayout.this.text);
                    FadingTextViewLayout.this.foregroundView.setVisibility(8);
                }
            }

            public void onAnimationStart(Animator animation) {
                FadingTextViewLayout.this.currentView.setLayerType(2, (Paint) null);
                FadingTextViewLayout.this.nextView.setLayerType(2, (Paint) null);
                if (ViewCompat.isAttachedToWindow(FadingTextViewLayout.this.currentView)) {
                    FadingTextViewLayout.this.currentView.buildLayer();
                }
                if (ViewCompat.isAttachedToWindow(FadingTextViewLayout.this.nextView)) {
                    FadingTextViewLayout.this.nextView.buildLayer();
                }
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-FadingTextViewLayout  reason: not valid java name */
    public /* synthetic */ void m2272lambda$new$0$orgtelegramuiComponentsFadingTextViewLayout(ValueAnimator a) {
        float fraction = a.getAnimatedFraction();
        this.currentView.setAlpha(CubicBezierInterpolator.DEFAULT.getInterpolation(fraction));
        this.nextView.setAlpha(CubicBezierInterpolator.DEFAULT.getInterpolation(1.0f - fraction));
    }

    public void setText(CharSequence text2) {
        setText(text2, true, true);
    }

    public void setText(CharSequence text2, boolean animated) {
        setText(text2, animated, true);
    }

    public void setText(CharSequence text2, boolean animated, boolean dontAnimateUnchangedStaticChars) {
        CharSequence currentText;
        CharSequence text3 = text2;
        if (!TextUtils.equals(text3, this.currentView.getText())) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.end();
            }
            this.text = text3;
            if (animated) {
                if (dontAnimateUnchangedStaticChars && this.foregroundView != null) {
                    int staticCharsCount = getStaticCharsCount();
                    if (staticCharsCount > 0) {
                        CharSequence currentText2 = this.currentView.getText();
                        int length = Math.min(staticCharsCount, Math.min(text2.length(), currentText2.length()));
                        List<Point> points = new ArrayList<>();
                        int startIndex = -1;
                        for (int i = 0; i < length; i++) {
                            if (text3.charAt(i) == currentText2.charAt(i)) {
                                if (startIndex >= 0) {
                                    points.add(new Point(startIndex, i));
                                    startIndex = -1;
                                }
                            } else if (startIndex == -1) {
                                startIndex = i;
                            }
                        }
                        if (startIndex != 0) {
                            if (startIndex > 0) {
                                points.add(new Point(startIndex, length));
                            } else {
                                points.add(new Point(length, 0));
                            }
                        }
                        if (!points.isEmpty()) {
                            SpannableString foregroundText = new SpannableString(text3.subSequence(0, length));
                            SpannableString currentSpannableText = new SpannableString(currentText2);
                            SpannableString spannableText = new SpannableString(text3);
                            int lastIndex = 0;
                            int i2 = 0;
                            int N = points.size();
                            while (i2 < N) {
                                Point point = points.get(i2);
                                int staticCharsCount2 = staticCharsCount;
                                if (point.y > point.x) {
                                    currentText = currentText2;
                                    foregroundText.setSpan(new ForegroundColorSpan(0), point.x, point.y, 17);
                                } else {
                                    currentText = currentText2;
                                }
                                if (point.x > lastIndex) {
                                    currentSpannableText.setSpan(new ForegroundColorSpan(0), lastIndex, point.x, 17);
                                    spannableText.setSpan(new ForegroundColorSpan(0), lastIndex, point.x, 17);
                                }
                                lastIndex = point.y;
                                i2++;
                                staticCharsCount = staticCharsCount2;
                                currentText2 = currentText;
                            }
                            CharSequence charSequence = currentText2;
                            this.foregroundView.setVisibility(0);
                            this.foregroundView.setText(foregroundText);
                            this.currentView.setText(currentSpannableText);
                            text3 = spannableText;
                        } else {
                            CharSequence charSequence2 = currentText2;
                        }
                    }
                }
                this.nextView.setVisibility(0);
                this.nextView.setText(text3);
                showNext();
                return;
            }
            this.currentView.setText(text3);
        }
    }

    public CharSequence getText() {
        return this.text;
    }

    public TextView getCurrentView() {
        return this.currentView;
    }

    public TextView getNextView() {
        return this.nextView;
    }

    private void showNext() {
        TextView prevView = this.currentView;
        this.currentView = this.nextView;
        this.nextView = prevView;
        this.animator.start();
    }

    /* access modifiers changed from: protected */
    public void onTextViewCreated(TextView textView) {
        textView.setSingleLine(true);
        textView.setMaxLines(1);
    }

    /* access modifiers changed from: protected */
    public int getStaticCharsCount() {
        return 0;
    }
}
