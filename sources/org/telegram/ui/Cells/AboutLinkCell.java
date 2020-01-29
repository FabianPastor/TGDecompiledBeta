package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
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
import org.telegram.ui.Components.StaticLayoutEx;

public class AboutLinkCell extends FrameLayout {
    private String oldText;
    private ClickableSpan pressedLink;
    private SpannableStringBuilder stringBuilder;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    private TextView valueTextView;

    /* access modifiers changed from: protected */
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
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 80, 23.0f, 0.0f, 23.0f, 10.0f));
        setWillNotDraw(false);
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setText(String str, boolean z) {
        setTextAndValue(str, (String) null, z);
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        String str3;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (str == null || (str3 = this.oldText) == null || !str.equals(str3)) {
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

    /* JADX WARNING: Removed duplicated region for block: B:59:0x0104 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:60:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            float r0 = r8.getX()
            float r1 = r8.getY()
            android.text.StaticLayout r2 = r7.textLayout
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x00fb
            int r2 = r8.getAction()
            if (r2 == 0) goto L_0x002b
            android.text.style.ClickableSpan r2 = r7.pressedLink
            if (r2 == 0) goto L_0x001f
            int r2 = r8.getAction()
            if (r2 != r4) goto L_0x001f
            goto L_0x002b
        L_0x001f:
            int r0 = r8.getAction()
            r1 = 3
            if (r0 != r1) goto L_0x00fb
            r7.resetPressedLink()
            goto L_0x00fb
        L_0x002b:
            int r2 = r8.getAction()
            if (r2 != 0) goto L_0x00b0
            r7.resetPressedLink()
            int r2 = r7.textX     // Catch:{ Exception -> 0x00a6 }
            float r2 = (float) r2     // Catch:{ Exception -> 0x00a6 }
            float r0 = r0 - r2
            int r0 = (int) r0     // Catch:{ Exception -> 0x00a6 }
            int r2 = r7.textY     // Catch:{ Exception -> 0x00a6 }
            float r2 = (float) r2     // Catch:{ Exception -> 0x00a6 }
            float r1 = r1 - r2
            int r1 = (int) r1     // Catch:{ Exception -> 0x00a6 }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x00a6 }
            int r1 = r2.getLineForVertical(r1)     // Catch:{ Exception -> 0x00a6 }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x00a6 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x00a6 }
            int r2 = r2.getOffsetForHorizontal(r1, r0)     // Catch:{ Exception -> 0x00a6 }
            android.text.StaticLayout r5 = r7.textLayout     // Catch:{ Exception -> 0x00a6 }
            float r5 = r5.getLineLeft(r1)     // Catch:{ Exception -> 0x00a6 }
            int r6 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r6 > 0) goto L_0x00a2
            android.text.StaticLayout r6 = r7.textLayout     // Catch:{ Exception -> 0x00a6 }
            float r1 = r6.getLineWidth(r1)     // Catch:{ Exception -> 0x00a6 }
            float r5 = r5 + r1
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x00a2
            android.text.StaticLayout r0 = r7.textLayout     // Catch:{ Exception -> 0x00a6 }
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x00a6 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x00a6 }
            java.lang.Class<android.text.style.ClickableSpan> r1 = android.text.style.ClickableSpan.class
            java.lang.Object[] r1 = r0.getSpans(r2, r2, r1)     // Catch:{ Exception -> 0x00a6 }
            android.text.style.ClickableSpan[] r1 = (android.text.style.ClickableSpan[]) r1     // Catch:{ Exception -> 0x00a6 }
            int r2 = r1.length     // Catch:{ Exception -> 0x00a6 }
            if (r2 == 0) goto L_0x009e
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00a6 }
            r1 = r1[r3]     // Catch:{ Exception -> 0x00a6 }
            r7.pressedLink = r1     // Catch:{ Exception -> 0x00a6 }
            android.text.style.ClickableSpan r1 = r7.pressedLink     // Catch:{ Exception -> 0x0096 }
            int r1 = r0.getSpanStart(r1)     // Catch:{ Exception -> 0x0096 }
            org.telegram.ui.Components.LinkPath r2 = r7.urlPath     // Catch:{ Exception -> 0x0096 }
            android.text.StaticLayout r5 = r7.textLayout     // Catch:{ Exception -> 0x0096 }
            r6 = 0
            r2.setCurrentLayout(r5, r1, r6)     // Catch:{ Exception -> 0x0096 }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x0096 }
            android.text.style.ClickableSpan r5 = r7.pressedLink     // Catch:{ Exception -> 0x0096 }
            int r0 = r0.getSpanEnd(r5)     // Catch:{ Exception -> 0x0096 }
            org.telegram.ui.Components.LinkPath r5 = r7.urlPath     // Catch:{ Exception -> 0x0096 }
            r2.getSelectionPath(r1, r0, r5)     // Catch:{ Exception -> 0x0096 }
            goto L_0x00f9
        L_0x0096:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x009b }
            goto L_0x00f9
        L_0x009b:
            r0 = move-exception
            r1 = 1
            goto L_0x00a8
        L_0x009e:
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00a6 }
            goto L_0x00fb
        L_0x00a2:
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00a6 }
            goto L_0x00fb
        L_0x00a6:
            r0 = move-exception
            r1 = 0
        L_0x00a8:
            r7.resetPressedLink()
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r1
            goto L_0x00fc
        L_0x00b0:
            android.text.style.ClickableSpan r0 = r7.pressedLink
            if (r0 == 0) goto L_0x00fb
            boolean r1 = r0 instanceof org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x00f2 }
            if (r1 == 0) goto L_0x00da
            org.telegram.ui.Components.URLSpanNoUnderline r0 = (org.telegram.ui.Components.URLSpanNoUnderline) r0     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r0 = r0.getURL()     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r1 = "@"
            boolean r1 = r0.startsWith(r1)     // Catch:{ Exception -> 0x00f2 }
            if (r1 != 0) goto L_0x00d6
            java.lang.String r1 = "#"
            boolean r1 = r0.startsWith(r1)     // Catch:{ Exception -> 0x00f2 }
            if (r1 != 0) goto L_0x00d6
            java.lang.String r1 = "/"
            boolean r1 = r0.startsWith(r1)     // Catch:{ Exception -> 0x00f2 }
            if (r1 == 0) goto L_0x00f6
        L_0x00d6:
            r7.didPressUrl(r0)     // Catch:{ Exception -> 0x00f2 }
            goto L_0x00f6
        L_0x00da:
            boolean r1 = r0 instanceof android.text.style.URLSpan     // Catch:{ Exception -> 0x00f2 }
            if (r1 == 0) goto L_0x00ee
            android.content.Context r0 = r7.getContext()     // Catch:{ Exception -> 0x00f2 }
            android.text.style.ClickableSpan r1 = r7.pressedLink     // Catch:{ Exception -> 0x00f2 }
            android.text.style.URLSpan r1 = (android.text.style.URLSpan) r1     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r1 = r1.getURL()     // Catch:{ Exception -> 0x00f2 }
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r0, (java.lang.String) r1)     // Catch:{ Exception -> 0x00f2 }
            goto L_0x00f6
        L_0x00ee:
            r0.onClick(r7)     // Catch:{ Exception -> 0x00f2 }
            goto L_0x00f6
        L_0x00f2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00f6:
            r7.resetPressedLink()
        L_0x00f9:
            r0 = 1
            goto L_0x00fc
        L_0x00fb:
            r0 = 0
        L_0x00fc:
            if (r0 != 0) goto L_0x0104
            boolean r8 = super.onTouchEvent(r8)
            if (r8 == 0) goto L_0x0105
        L_0x0104:
            r3 = 1
        L_0x0105:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.AboutLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int i, int i2) {
        if (this.stringBuilder != null) {
            int size = View.MeasureSpec.getSize(i) - AndroidUtilities.dp(46.0f);
            if (Build.VERSION.SDK_INT >= 24) {
                SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
                this.textLayout = StaticLayout.Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), Theme.profile_aboutTextPaint, size).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(LocaleController.isRTL ? StaticLayoutEx.ALIGN_RIGHT() : StaticLayoutEx.ALIGN_LEFT()).build();
            } else {
                this.textLayout = new StaticLayout(this.stringBuilder, Theme.profile_aboutTextPaint, size, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
        StaticLayout staticLayout = this.textLayout;
        int height = (staticLayout != null ? staticLayout.getHeight() : AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(16.0f);
        if (this.valueTextView.getVisibility() == 0) {
            height += AndroidUtilities.dp(23.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        int dp = AndroidUtilities.dp(23.0f);
        this.textX = dp;
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas.translate((float) dp, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
        }
        try {
            if (this.textLayout != null) {
                this.textLayout.draw(canvas);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        canvas.restore();
    }
}
