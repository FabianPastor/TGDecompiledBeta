package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.StaticLayout.Builder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;

public class AboutLinkCell extends FrameLayout {
    private String oldText;
    private ClickableSpan pressedLink;
    private SpannableStringBuilder stringBuilder;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    private TextView valueTextView;

    /* Access modifiers changed, original: protected */
    public void didPressUrl(String str) {
    }

    public AboutLinkCell(Context context) {
        super(context);
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        int i = 5;
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView = this.valueTextView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i | 80, 23.0f, 0.0f, 23.0f, 10.0f));
        setWillNotDraw(false);
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setText(String str, boolean z) {
        setTextAndValue(str, null, z);
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        if (!TextUtils.isEmpty(str)) {
            if (str != null) {
                String str3 = this.oldText;
                if (str3 != null && str.equals(str3)) {
                    return;
                }
            }
            this.oldText = str;
            this.stringBuilder = new SpannableStringBuilder(this.oldText);
            if (z) {
                MessageObject.addLinks(false, this.stringBuilder, false);
            }
            Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (TextUtils.isEmpty(str2)) {
                this.valueTextView.setVisibility(8);
            } else {
                this.valueTextView.setText(str2);
                this.valueTextView.setVisibility(0);
            }
            requestLayout();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:57:0x00fe  */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
        r7 = this;
        r0 = r8.getX();
        r1 = r8.getY();
        r2 = r7.textLayout;
        r3 = 0;
        r4 = 1;
        if (r2 == 0) goto L_0x00fb;
    L_0x000e:
        r2 = r8.getAction();
        if (r2 == 0) goto L_0x002b;
    L_0x0014:
        r2 = r7.pressedLink;
        if (r2 == 0) goto L_0x001f;
    L_0x0018:
        r2 = r8.getAction();
        if (r2 != r4) goto L_0x001f;
    L_0x001e:
        goto L_0x002b;
    L_0x001f:
        r0 = r8.getAction();
        r1 = 3;
        if (r0 != r1) goto L_0x00fb;
    L_0x0026:
        r7.resetPressedLink();
        goto L_0x00fb;
    L_0x002b:
        r2 = r8.getAction();
        if (r2 != 0) goto L_0x00b0;
    L_0x0031:
        r7.resetPressedLink();
        r2 = r7.textX;	 Catch:{ Exception -> 0x00a6 }
        r2 = (float) r2;	 Catch:{ Exception -> 0x00a6 }
        r0 = r0 - r2;
        r0 = (int) r0;	 Catch:{ Exception -> 0x00a6 }
        r2 = r7.textY;	 Catch:{ Exception -> 0x00a6 }
        r2 = (float) r2;	 Catch:{ Exception -> 0x00a6 }
        r1 = r1 - r2;
        r1 = (int) r1;	 Catch:{ Exception -> 0x00a6 }
        r2 = r7.textLayout;	 Catch:{ Exception -> 0x00a6 }
        r1 = r2.getLineForVertical(r1);	 Catch:{ Exception -> 0x00a6 }
        r2 = r7.textLayout;	 Catch:{ Exception -> 0x00a6 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x00a6 }
        r2 = r2.getOffsetForHorizontal(r1, r0);	 Catch:{ Exception -> 0x00a6 }
        r5 = r7.textLayout;	 Catch:{ Exception -> 0x00a6 }
        r5 = r5.getLineLeft(r1);	 Catch:{ Exception -> 0x00a6 }
        r6 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1));
        if (r6 > 0) goto L_0x00a2;
    L_0x0055:
        r6 = r7.textLayout;	 Catch:{ Exception -> 0x00a6 }
        r1 = r6.getLineWidth(r1);	 Catch:{ Exception -> 0x00a6 }
        r5 = r5 + r1;
        r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x00a2;
    L_0x0060:
        r0 = r7.textLayout;	 Catch:{ Exception -> 0x00a6 }
        r0 = r0.getText();	 Catch:{ Exception -> 0x00a6 }
        r0 = (android.text.Spannable) r0;	 Catch:{ Exception -> 0x00a6 }
        r1 = android.text.style.ClickableSpan.class;
        r1 = r0.getSpans(r2, r2, r1);	 Catch:{ Exception -> 0x00a6 }
        r1 = (android.text.style.ClickableSpan[]) r1;	 Catch:{ Exception -> 0x00a6 }
        r2 = r1.length;	 Catch:{ Exception -> 0x00a6 }
        if (r2 == 0) goto L_0x009e;
    L_0x0073:
        r7.resetPressedLink();	 Catch:{ Exception -> 0x00a6 }
        r1 = r1[r3];	 Catch:{ Exception -> 0x00a6 }
        r7.pressedLink = r1;	 Catch:{ Exception -> 0x00a6 }
        r1 = r7.pressedLink;	 Catch:{ Exception -> 0x0096 }
        r1 = r0.getSpanStart(r1);	 Catch:{ Exception -> 0x0096 }
        r2 = r7.urlPath;	 Catch:{ Exception -> 0x0096 }
        r5 = r7.textLayout;	 Catch:{ Exception -> 0x0096 }
        r6 = 0;
        r2.setCurrentLayout(r5, r1, r6);	 Catch:{ Exception -> 0x0096 }
        r2 = r7.textLayout;	 Catch:{ Exception -> 0x0096 }
        r5 = r7.pressedLink;	 Catch:{ Exception -> 0x0096 }
        r0 = r0.getSpanEnd(r5);	 Catch:{ Exception -> 0x0096 }
        r5 = r7.urlPath;	 Catch:{ Exception -> 0x0096 }
        r2.getSelectionPath(r1, r0, r5);	 Catch:{ Exception -> 0x0096 }
        goto L_0x00f9;
    L_0x0096:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x009b }
        goto L_0x00f9;
    L_0x009b:
        r0 = move-exception;
        r1 = 1;
        goto L_0x00a8;
    L_0x009e:
        r7.resetPressedLink();	 Catch:{ Exception -> 0x00a6 }
        goto L_0x00fb;
    L_0x00a2:
        r7.resetPressedLink();	 Catch:{ Exception -> 0x00a6 }
        goto L_0x00fb;
    L_0x00a6:
        r0 = move-exception;
        r1 = 0;
    L_0x00a8:
        r7.resetPressedLink();
        org.telegram.messenger.FileLog.e(r0);
        r0 = r1;
        goto L_0x00fc;
    L_0x00b0:
        r0 = r7.pressedLink;
        if (r0 == 0) goto L_0x00fb;
    L_0x00b4:
        r1 = r0 instanceof org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x00f2 }
        if (r1 == 0) goto L_0x00da;
    L_0x00b8:
        r0 = (org.telegram.ui.Components.URLSpanNoUnderline) r0;	 Catch:{ Exception -> 0x00f2 }
        r0 = r0.getURL();	 Catch:{ Exception -> 0x00f2 }
        r1 = "@";
        r1 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00f2 }
        if (r1 != 0) goto L_0x00d6;
    L_0x00c6:
        r1 = "#";
        r1 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00f2 }
        if (r1 != 0) goto L_0x00d6;
    L_0x00ce:
        r1 = "/";
        r1 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00f2 }
        if (r1 == 0) goto L_0x00f6;
    L_0x00d6:
        r7.didPressUrl(r0);	 Catch:{ Exception -> 0x00f2 }
        goto L_0x00f6;
    L_0x00da:
        r1 = r0 instanceof android.text.style.URLSpan;	 Catch:{ Exception -> 0x00f2 }
        if (r1 == 0) goto L_0x00ee;
    L_0x00de:
        r0 = r7.getContext();	 Catch:{ Exception -> 0x00f2 }
        r1 = r7.pressedLink;	 Catch:{ Exception -> 0x00f2 }
        r1 = (android.text.style.URLSpan) r1;	 Catch:{ Exception -> 0x00f2 }
        r1 = r1.getURL();	 Catch:{ Exception -> 0x00f2 }
        org.telegram.messenger.browser.Browser.openUrl(r0, r1);	 Catch:{ Exception -> 0x00f2 }
        goto L_0x00f6;
    L_0x00ee:
        r0.onClick(r7);	 Catch:{ Exception -> 0x00f2 }
        goto L_0x00f6;
    L_0x00f2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00f6:
        r7.resetPressedLink();
    L_0x00f9:
        r0 = 1;
        goto L_0x00fc;
    L_0x00fb:
        r0 = 0;
    L_0x00fc:
        if (r0 != 0) goto L_0x0104;
    L_0x00fe:
        r8 = super.onTouchEvent(r8);
        if (r8 == 0) goto L_0x0105;
    L_0x0104:
        r3 = 1;
    L_0x0105:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.AboutLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* Access modifiers changed, original: protected */
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int i, int i2) {
        if (this.stringBuilder != null) {
            int size = MeasureSpec.getSize(i) - AndroidUtilities.dp(46.0f);
            if (VERSION.SDK_INT >= 24) {
                SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
                this.textLayout = Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), Theme.profile_aboutTextPaint, size).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(LocaleController.isRTL ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_LEFT).build();
            } else {
                this.textLayout = new StaticLayout(this.stringBuilder, Theme.profile_aboutTextPaint, size, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
        StaticLayout staticLayout = this.textLayout;
        i2 = (staticLayout != null ? staticLayout.getHeight() : AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(16.0f);
        if (this.valueTextView.getVisibility() == 0) {
            i2 += AndroidUtilities.dp(23.0f);
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        int dp = AndroidUtilities.dp(23.0f);
        this.textX = dp;
        float f = (float) dp;
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas.translate(f, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
        }
        try {
            if (this.textLayout != null) {
                this.textLayout.draw(canvas);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        canvas.restore();
    }
}
