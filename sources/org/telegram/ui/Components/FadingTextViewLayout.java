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

    /* access modifiers changed from: protected */
    public int getStaticCharsCount() {
        return 0;
    }

    public FadingTextViewLayout(Context context) {
        this(context, false);
    }

    public FadingTextViewLayout(Context context, boolean z) {
        super(context);
        for (int i = 0; i < (z ? 1 : 0) + true; i++) {
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
            public void onAnimationEnd(Animator animator) {
                FadingTextViewLayout.this.currentView.setLayerType(0, (Paint) null);
                FadingTextViewLayout.this.nextView.setLayerType(0, (Paint) null);
                FadingTextViewLayout.this.nextView.setVisibility(8);
                if (FadingTextViewLayout.this.foregroundView != null) {
                    FadingTextViewLayout.this.currentView.setText(FadingTextViewLayout.this.text);
                    FadingTextViewLayout.this.foregroundView.setVisibility(8);
                }
            }

            public void onAnimationStart(Animator animator) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        TextView textView = this.currentView;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        textView.setAlpha(cubicBezierInterpolator.getInterpolation(animatedFraction));
        this.nextView.setAlpha(cubicBezierInterpolator.getInterpolation(1.0f - animatedFraction));
    }

    public void setText(CharSequence charSequence) {
        setText(charSequence, true, true);
    }

    public void setText(CharSequence charSequence, boolean z) {
        setText(charSequence, z, true);
    }

    public void setText(CharSequence charSequence, boolean z, boolean z2) {
        int staticCharsCount;
        if (!TextUtils.equals(charSequence, this.currentView.getText())) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.end();
            }
            this.text = charSequence;
            if (z) {
                if (z2 && this.foregroundView != null && (staticCharsCount = getStaticCharsCount()) > 0) {
                    CharSequence text2 = this.currentView.getText();
                    int min = Math.min(staticCharsCount, Math.min(charSequence.length(), text2.length()));
                    ArrayList arrayList = new ArrayList();
                    int i = -1;
                    for (int i2 = 0; i2 < min; i2++) {
                        if (charSequence.charAt(i2) == text2.charAt(i2)) {
                            if (i >= 0) {
                                arrayList.add(new Point(i, i2));
                                i = -1;
                            }
                        } else if (i == -1) {
                            i = i2;
                        }
                    }
                    if (i != 0) {
                        if (i > 0) {
                            arrayList.add(new Point(i, min));
                        } else {
                            arrayList.add(new Point(min, 0));
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        SpannableString spannableString = new SpannableString(charSequence.subSequence(0, min));
                        SpannableString spannableString2 = new SpannableString(text2);
                        SpannableString spannableString3 = new SpannableString(charSequence);
                        int size = arrayList.size();
                        int i3 = 0;
                        for (int i4 = 0; i4 < size; i4++) {
                            Point point = (Point) arrayList.get(i4);
                            if (point.y > point.x) {
                                spannableString.setSpan(new ForegroundColorSpan(0), point.x, point.y, 17);
                            }
                            if (point.x > i3) {
                                spannableString2.setSpan(new ForegroundColorSpan(0), i3, point.x, 17);
                                spannableString3.setSpan(new ForegroundColorSpan(0), i3, point.x, 17);
                            }
                            i3 = point.y;
                        }
                        this.foregroundView.setVisibility(0);
                        this.foregroundView.setText(spannableString);
                        this.currentView.setText(spannableString2);
                        charSequence = spannableString3;
                    }
                }
                this.nextView.setVisibility(0);
                this.nextView.setText(charSequence);
                showNext();
                return;
            }
            this.currentView.setText(charSequence);
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
        TextView textView = this.currentView;
        this.currentView = this.nextView;
        this.nextView = textView;
        this.animator.start();
    }

    /* access modifiers changed from: protected */
    public void onTextViewCreated(TextView textView) {
        textView.setSingleLine(true);
        textView.setMaxLines(1);
    }
}
