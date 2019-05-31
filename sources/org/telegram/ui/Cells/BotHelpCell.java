package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.MeasureSpec;
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
    private BotHelpCellDelegate delegate;
    private int height;
    private String oldText;
    private ClickableSpan pressedLink;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    private int width;

    public interface BotHelpCellDelegate {
        void didPressUrl(String str);
    }

    public BotHelpCell(Context context) {
        super(context);
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

    public void setText(String str) {
        if (str == null || str.length() == 0) {
            setVisibility(8);
        } else if (str == null || !str.equals(this.oldText)) {
            int minTabletSide;
            this.oldText = str;
            int i = 0;
            setVisibility(0);
            if (AndroidUtilities.isTablet()) {
                minTabletSide = AndroidUtilities.getMinTabletSide();
            } else {
                Point point = AndroidUtilities.displaySize;
                minTabletSide = Math.min(point.x, point.y);
            }
            minTabletSide = (int) (((float) minTabletSide) * 0.7f);
            String str2 = "\n";
            String[] split = str.split(str2);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            String string = LocaleController.getString("BotInfoTitle", NUM);
            spannableStringBuilder.append(string);
            spannableStringBuilder.append("\n\n");
            for (int i2 = 0; i2 < split.length; i2++) {
                spannableStringBuilder.append(split[i2].trim());
                if (i2 != split.length - 1) {
                    spannableStringBuilder.append(str2);
                }
            }
            MessageObject.addLinks(false, spannableStringBuilder);
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, string.length(), 33);
            Emoji.replaceEmoji(spannableStringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            try {
                this.textLayout = new StaticLayout(spannableStringBuilder, Theme.chat_msgTextPaint, minTabletSide, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.width = 0;
                this.height = this.textLayout.getHeight() + AndroidUtilities.dp(22.0f);
                int lineCount = this.textLayout.getLineCount();
                while (i < lineCount) {
                    this.width = (int) Math.ceil((double) Math.max((float) this.width, this.textLayout.getLineWidth(i) + this.textLayout.getLineLeft(i)));
                    i++;
                }
                if (this.width > minTabletSide) {
                    this.width = minTabletSide;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.width += AndroidUtilities.dp(22.0f);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x0105  */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
        r7 = this;
        r0 = r8.getX();
        r1 = r8.getY();
        r2 = r7.textLayout;
        r3 = 0;
        r4 = 1;
        if (r2 == 0) goto L_0x0102;
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
        if (r0 != r1) goto L_0x0102;
    L_0x0026:
        r7.resetPressedLink();
        goto L_0x0102;
    L_0x002b:
        r2 = r8.getAction();
        if (r2 != 0) goto L_0x00b1;
    L_0x0031:
        r7.resetPressedLink();
        r2 = r7.textX;	 Catch:{ Exception -> 0x00a7 }
        r2 = (float) r2;	 Catch:{ Exception -> 0x00a7 }
        r0 = r0 - r2;
        r0 = (int) r0;	 Catch:{ Exception -> 0x00a7 }
        r2 = r7.textY;	 Catch:{ Exception -> 0x00a7 }
        r2 = (float) r2;	 Catch:{ Exception -> 0x00a7 }
        r1 = r1 - r2;
        r1 = (int) r1;	 Catch:{ Exception -> 0x00a7 }
        r2 = r7.textLayout;	 Catch:{ Exception -> 0x00a7 }
        r1 = r2.getLineForVertical(r1);	 Catch:{ Exception -> 0x00a7 }
        r2 = r7.textLayout;	 Catch:{ Exception -> 0x00a7 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x00a7 }
        r2 = r2.getOffsetForHorizontal(r1, r0);	 Catch:{ Exception -> 0x00a7 }
        r5 = r7.textLayout;	 Catch:{ Exception -> 0x00a7 }
        r5 = r5.getLineLeft(r1);	 Catch:{ Exception -> 0x00a7 }
        r6 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1));
        if (r6 > 0) goto L_0x00a3;
    L_0x0055:
        r6 = r7.textLayout;	 Catch:{ Exception -> 0x00a7 }
        r1 = r6.getLineWidth(r1);	 Catch:{ Exception -> 0x00a7 }
        r5 = r5 + r1;
        r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x00a3;
    L_0x0060:
        r0 = r7.textLayout;	 Catch:{ Exception -> 0x00a7 }
        r0 = r0.getText();	 Catch:{ Exception -> 0x00a7 }
        r0 = (android.text.Spannable) r0;	 Catch:{ Exception -> 0x00a7 }
        r1 = android.text.style.ClickableSpan.class;
        r1 = r0.getSpans(r2, r2, r1);	 Catch:{ Exception -> 0x00a7 }
        r1 = (android.text.style.ClickableSpan[]) r1;	 Catch:{ Exception -> 0x00a7 }
        r2 = r1.length;	 Catch:{ Exception -> 0x00a7 }
        if (r2 == 0) goto L_0x009f;
    L_0x0073:
        r7.resetPressedLink();	 Catch:{ Exception -> 0x00a7 }
        r1 = r1[r3];	 Catch:{ Exception -> 0x00a7 }
        r7.pressedLink = r1;	 Catch:{ Exception -> 0x00a7 }
        r1 = r7.pressedLink;	 Catch:{ Exception -> 0x0097 }
        r1 = r0.getSpanStart(r1);	 Catch:{ Exception -> 0x0097 }
        r2 = r7.urlPath;	 Catch:{ Exception -> 0x0097 }
        r5 = r7.textLayout;	 Catch:{ Exception -> 0x0097 }
        r6 = 0;
        r2.setCurrentLayout(r5, r1, r6);	 Catch:{ Exception -> 0x0097 }
        r2 = r7.textLayout;	 Catch:{ Exception -> 0x0097 }
        r5 = r7.pressedLink;	 Catch:{ Exception -> 0x0097 }
        r0 = r0.getSpanEnd(r5);	 Catch:{ Exception -> 0x0097 }
        r5 = r7.urlPath;	 Catch:{ Exception -> 0x0097 }
        r2.getSelectionPath(r1, r0, r5);	 Catch:{ Exception -> 0x0097 }
        goto L_0x0100;
    L_0x0097:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x009c }
        goto L_0x0100;
    L_0x009c:
        r0 = move-exception;
        r1 = 1;
        goto L_0x00a9;
    L_0x009f:
        r7.resetPressedLink();	 Catch:{ Exception -> 0x00a7 }
        goto L_0x0102;
    L_0x00a3:
        r7.resetPressedLink();	 Catch:{ Exception -> 0x00a7 }
        goto L_0x0102;
    L_0x00a7:
        r0 = move-exception;
        r1 = 0;
    L_0x00a9:
        r7.resetPressedLink();
        org.telegram.messenger.FileLog.e(r0);
        r0 = r1;
        goto L_0x0103;
    L_0x00b1:
        r0 = r7.pressedLink;
        if (r0 == 0) goto L_0x0102;
    L_0x00b5:
        r1 = r0 instanceof org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x00f9 }
        if (r1 == 0) goto L_0x00e1;
    L_0x00b9:
        r0 = (org.telegram.ui.Components.URLSpanNoUnderline) r0;	 Catch:{ Exception -> 0x00f9 }
        r0 = r0.getURL();	 Catch:{ Exception -> 0x00f9 }
        r1 = "@";
        r1 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00f9 }
        if (r1 != 0) goto L_0x00d7;
    L_0x00c7:
        r1 = "#";
        r1 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00f9 }
        if (r1 != 0) goto L_0x00d7;
    L_0x00cf:
        r1 = "/";
        r1 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00f9 }
        if (r1 == 0) goto L_0x00fd;
    L_0x00d7:
        r1 = r7.delegate;	 Catch:{ Exception -> 0x00f9 }
        if (r1 == 0) goto L_0x00fd;
    L_0x00db:
        r1 = r7.delegate;	 Catch:{ Exception -> 0x00f9 }
        r1.didPressUrl(r0);	 Catch:{ Exception -> 0x00f9 }
        goto L_0x00fd;
    L_0x00e1:
        r1 = r0 instanceof android.text.style.URLSpan;	 Catch:{ Exception -> 0x00f9 }
        if (r1 == 0) goto L_0x00f5;
    L_0x00e5:
        r0 = r7.getContext();	 Catch:{ Exception -> 0x00f9 }
        r1 = r7.pressedLink;	 Catch:{ Exception -> 0x00f9 }
        r1 = (android.text.style.URLSpan) r1;	 Catch:{ Exception -> 0x00f9 }
        r1 = r1.getURL();	 Catch:{ Exception -> 0x00f9 }
        org.telegram.messenger.browser.Browser.openUrl(r0, r1);	 Catch:{ Exception -> 0x00f9 }
        goto L_0x00fd;
    L_0x00f5:
        r0.onClick(r7);	 Catch:{ Exception -> 0x00f9 }
        goto L_0x00fd;
    L_0x00f9:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00fd:
        r7.resetPressedLink();
    L_0x0100:
        r0 = 1;
        goto L_0x0103;
    L_0x0102:
        r0 = 0;
    L_0x0103:
        if (r0 != 0) goto L_0x010b;
    L_0x0105:
        r8 = super.onTouchEvent(r8);
        if (r8 == 0) goto L_0x010c;
    L_0x010b:
        r3 = 1;
    L_0x010c:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.BotHelpCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), this.height + AndroidUtilities.dp(8.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        int width = (getWidth() - this.width) / 2;
        int dp = AndroidUtilities.dp(4.0f);
        Theme.chat_msgInMediaShadowDrawable.setBounds(width, dp, this.width + width, this.height + dp);
        Theme.chat_msgInMediaShadowDrawable.draw(canvas);
        Theme.chat_msgInMediaDrawable.setBounds(width, dp, this.width + width, this.height + dp);
        Theme.chat_msgInMediaDrawable.draw(canvas);
        Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
        Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
        canvas.save();
        int dp2 = AndroidUtilities.dp(11.0f) + width;
        this.textX = dp2;
        float f = (float) dp2;
        int dp3 = AndroidUtilities.dp(11.0f) + dp;
        this.textY = dp3;
        canvas.translate(f, (float) dp3);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.chat_urlPaint);
        }
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            staticLayout.draw(canvas);
        }
        canvas.restore();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setText(this.textLayout.getText());
    }
}
