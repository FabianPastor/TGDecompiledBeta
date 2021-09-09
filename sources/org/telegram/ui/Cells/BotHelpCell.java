package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.TypefaceSpan;

public class BotHelpCell extends View {
    private boolean animating;
    private BotHelpCellDelegate delegate;
    private int height;
    private String oldText;
    private ClickableSpan pressedLink;
    private final Theme.ResourcesProvider resourcesProvider;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    private int width;

    public interface BotHelpCellDelegate {
        void didPressUrl(String str);
    }

    public BotHelpCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
    }

    public void setDelegate(BotHelpCellDelegate botHelpCellDelegate) {
        this.delegate = botHelpCellDelegate;
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setText(boolean z, String str) {
        int i;
        if (str == null || str.length() == 0) {
            setVisibility(8);
        } else if (!str.equals(this.oldText)) {
            this.oldText = AndroidUtilities.getSafeString(str);
            setVisibility(0);
            if (AndroidUtilities.isTablet()) {
                i = AndroidUtilities.getMinTabletSide();
            } else {
                Point point = AndroidUtilities.displaySize;
                i = Math.min(point.x, point.y);
            }
            int i2 = (int) (((float) i) * 0.7f);
            String[] split = str.split("\n");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            String string = LocaleController.getString("BotInfoTitle", NUM);
            if (z) {
                spannableStringBuilder.append(string);
                spannableStringBuilder.append("\n\n");
            }
            for (int i3 = 0; i3 < split.length; i3++) {
                spannableStringBuilder.append(split[i3].trim());
                if (i3 != split.length - 1) {
                    spannableStringBuilder.append("\n");
                }
            }
            MessageObject.addLinks(false, spannableStringBuilder);
            if (z) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, string.length(), 33);
            }
            Emoji.replaceEmoji(spannableStringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            try {
                StaticLayout staticLayout = new StaticLayout(spannableStringBuilder, Theme.chat_msgTextPaint, i2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.textLayout = staticLayout;
                this.width = 0;
                this.height = staticLayout.getHeight() + AndroidUtilities.dp(22.0f);
                int lineCount = this.textLayout.getLineCount();
                for (int i4 = 0; i4 < lineCount; i4++) {
                    this.width = (int) Math.ceil((double) Math.max((float) this.width, this.textLayout.getLineWidth(i4) + this.textLayout.getLineLeft(i4)));
                }
                if (this.width > i2) {
                    this.width = i2;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.width += AndroidUtilities.dp(22.0f);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x0105 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:64:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            float r0 = r8.getX()
            float r1 = r8.getY()
            android.text.StaticLayout r2 = r7.textLayout
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x00fc
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
            if (r0 != r1) goto L_0x00fc
            r7.resetPressedLink()
            goto L_0x00fc
        L_0x002b:
            int r2 = r8.getAction()
            if (r2 != 0) goto L_0x00af
            r7.resetPressedLink()
            int r2 = r7.textX     // Catch:{ Exception -> 0x00a5 }
            float r2 = (float) r2     // Catch:{ Exception -> 0x00a5 }
            float r0 = r0 - r2
            int r0 = (int) r0     // Catch:{ Exception -> 0x00a5 }
            int r2 = r7.textY     // Catch:{ Exception -> 0x00a5 }
            float r2 = (float) r2     // Catch:{ Exception -> 0x00a5 }
            float r1 = r1 - r2
            int r1 = (int) r1     // Catch:{ Exception -> 0x00a5 }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x00a5 }
            int r1 = r2.getLineForVertical(r1)     // Catch:{ Exception -> 0x00a5 }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x00a5 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x00a5 }
            int r2 = r2.getOffsetForHorizontal(r1, r0)     // Catch:{ Exception -> 0x00a5 }
            android.text.StaticLayout r5 = r7.textLayout     // Catch:{ Exception -> 0x00a5 }
            float r5 = r5.getLineLeft(r1)     // Catch:{ Exception -> 0x00a5 }
            int r6 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r6 > 0) goto L_0x00a1
            android.text.StaticLayout r6 = r7.textLayout     // Catch:{ Exception -> 0x00a5 }
            float r1 = r6.getLineWidth(r1)     // Catch:{ Exception -> 0x00a5 }
            float r5 = r5 + r1
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x00a1
            android.text.StaticLayout r0 = r7.textLayout     // Catch:{ Exception -> 0x00a5 }
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x00a5 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x00a5 }
            java.lang.Class<android.text.style.ClickableSpan> r1 = android.text.style.ClickableSpan.class
            java.lang.Object[] r1 = r0.getSpans(r2, r2, r1)     // Catch:{ Exception -> 0x00a5 }
            android.text.style.ClickableSpan[] r1 = (android.text.style.ClickableSpan[]) r1     // Catch:{ Exception -> 0x00a5 }
            int r2 = r1.length     // Catch:{ Exception -> 0x00a5 }
            if (r2 == 0) goto L_0x009d
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00a5 }
            r1 = r1[r3]     // Catch:{ Exception -> 0x00a5 }
            r7.pressedLink = r1     // Catch:{ Exception -> 0x00a5 }
            int r1 = r0.getSpanStart(r1)     // Catch:{ Exception -> 0x0095 }
            org.telegram.ui.Components.LinkPath r2 = r7.urlPath     // Catch:{ Exception -> 0x0095 }
            android.text.StaticLayout r5 = r7.textLayout     // Catch:{ Exception -> 0x0095 }
            r6 = 0
            r2.setCurrentLayout(r5, r1, r6)     // Catch:{ Exception -> 0x0095 }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x0095 }
            android.text.style.ClickableSpan r5 = r7.pressedLink     // Catch:{ Exception -> 0x0095 }
            int r0 = r0.getSpanEnd(r5)     // Catch:{ Exception -> 0x0095 }
            org.telegram.ui.Components.LinkPath r5 = r7.urlPath     // Catch:{ Exception -> 0x0095 }
            r2.getSelectionPath(r1, r0, r5)     // Catch:{ Exception -> 0x0095 }
            goto L_0x00fa
        L_0x0095:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x009a }
            goto L_0x00fa
        L_0x009a:
            r0 = move-exception
            r1 = 1
            goto L_0x00a7
        L_0x009d:
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00a5 }
            goto L_0x00fc
        L_0x00a1:
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00a5 }
            goto L_0x00fc
        L_0x00a5:
            r0 = move-exception
            r1 = 0
        L_0x00a7:
            r7.resetPressedLink()
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r1
            goto L_0x00fd
        L_0x00af:
            android.text.style.ClickableSpan r0 = r7.pressedLink
            if (r0 == 0) goto L_0x00fc
            boolean r1 = r0 instanceof org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x00f3 }
            if (r1 == 0) goto L_0x00dd
            org.telegram.ui.Components.URLSpanNoUnderline r0 = (org.telegram.ui.Components.URLSpanNoUnderline) r0     // Catch:{ Exception -> 0x00f3 }
            java.lang.String r0 = r0.getURL()     // Catch:{ Exception -> 0x00f3 }
            java.lang.String r1 = "@"
            boolean r1 = r0.startsWith(r1)     // Catch:{ Exception -> 0x00f3 }
            if (r1 != 0) goto L_0x00d5
            java.lang.String r1 = "#"
            boolean r1 = r0.startsWith(r1)     // Catch:{ Exception -> 0x00f3 }
            if (r1 != 0) goto L_0x00d5
            java.lang.String r1 = "/"
            boolean r1 = r0.startsWith(r1)     // Catch:{ Exception -> 0x00f3 }
            if (r1 == 0) goto L_0x00f7
        L_0x00d5:
            org.telegram.ui.Cells.BotHelpCell$BotHelpCellDelegate r1 = r7.delegate     // Catch:{ Exception -> 0x00f3 }
            if (r1 == 0) goto L_0x00f7
            r1.didPressUrl(r0)     // Catch:{ Exception -> 0x00f3 }
            goto L_0x00f7
        L_0x00dd:
            boolean r1 = r0 instanceof android.text.style.URLSpan     // Catch:{ Exception -> 0x00f3 }
            if (r1 == 0) goto L_0x00ef
            org.telegram.ui.Cells.BotHelpCell$BotHelpCellDelegate r1 = r7.delegate     // Catch:{ Exception -> 0x00f3 }
            if (r1 == 0) goto L_0x00f7
            android.text.style.URLSpan r0 = (android.text.style.URLSpan) r0     // Catch:{ Exception -> 0x00f3 }
            java.lang.String r0 = r0.getURL()     // Catch:{ Exception -> 0x00f3 }
            r1.didPressUrl(r0)     // Catch:{ Exception -> 0x00f3 }
            goto L_0x00f7
        L_0x00ef:
            r0.onClick(r7)     // Catch:{ Exception -> 0x00f3 }
            goto L_0x00f7
        L_0x00f3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00f7:
            r7.resetPressedLink()
        L_0x00fa:
            r0 = 1
            goto L_0x00fd
        L_0x00fc:
            r0 = 0
        L_0x00fd:
            if (r0 != 0) goto L_0x0105
            boolean r8 = super.onTouchEvent(r8)
            if (r8 == 0) goto L_0x0106
        L_0x0105:
            r3 = 1
        L_0x0106:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.BotHelpCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), this.height + AndroidUtilities.dp(8.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int width2 = (getWidth() - this.width) / 2;
        int dp = AndroidUtilities.dp(2.0f);
        Drawable shadowDrawable = Theme.chat_msgInMediaDrawable.getShadowDrawable();
        if (shadowDrawable != null) {
            shadowDrawable.setBounds(width2, dp, this.width + width2, this.height + dp);
            shadowDrawable.draw(canvas);
        }
        Point point = AndroidUtilities.displaySize;
        int i = point.x;
        int i2 = point.y;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            i = view.getMeasuredWidth();
            i2 = view.getMeasuredHeight();
        }
        int i3 = i2;
        Theme.MessageDrawable messageDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
        Theme.MessageDrawable messageDrawable2 = messageDrawable;
        messageDrawable2.setTop((int) getY(), i, i3, false, false);
        messageDrawable.setBounds(width2, dp, this.width + width2, this.height + dp);
        messageDrawable.draw(canvas);
        Theme.chat_msgTextPaint.setColor(getThemedColor("chat_messageTextIn"));
        Theme.chat_msgTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
        canvas.save();
        int dp2 = AndroidUtilities.dp(11.0f) + width2;
        this.textX = dp2;
        int dp3 = AndroidUtilities.dp(11.0f) + dp;
        this.textY = dp3;
        canvas.translate((float) dp2, (float) dp3);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.chat_urlPaint);
        }
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            staticLayout.draw(canvas);
        }
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setText(this.textLayout.getText());
    }

    public boolean animating() {
        return this.animating;
    }

    public void setAnimating(boolean z) {
        this.animating = z;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Drawable drawable = resourcesProvider2 != null ? resourcesProvider2.getDrawable(str) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(str);
    }
}
