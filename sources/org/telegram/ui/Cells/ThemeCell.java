package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeCell extends FrameLayout {
    private static byte[] bytes = new byte[1024];
    private ImageView checkImage;
    private Theme.ThemeInfo currentThemeInfo;
    private boolean isNightTheme;
    private boolean needDivider;
    private ImageView optionsButton;
    private Paint paint = new Paint(1);
    private Paint paintStroke = new Paint(1);
    private TextView textView;

    public ThemeCell(Context context, boolean z) {
        super(context);
        setWillNotDraw(false);
        this.isNightTheme = z;
        this.paintStroke.setStyle(Paint.Style.STROKE);
        this.paintStroke.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 105.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 105.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
        this.checkImage.setImageResource(NUM);
        if (!this.isNightTheme) {
            addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : 5) | 16, 59.0f, 0.0f, 59.0f, 0.0f));
            this.optionsButton = new ImageView(context);
            this.optionsButton.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsButton.setImageResource(NUM);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            addView(this.optionsButton, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : i) | 48));
            return;
        }
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setOnOptionsClick(View.OnClickListener onClickListener) {
        this.optionsButton.setOnClickListener(onClickListener);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public Theme.ThemeInfo getCurrentThemeInfo() {
        return this.currentThemeInfo;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(2:35|36) */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x009f, code lost:
        r3 = r15.substring(r4 + 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a9, code lost:
        if (r3.length() <= 0) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b2, code lost:
        if (r3.charAt(0) != '#') goto L_0x00c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r3 = android.graphics.Color.parseColor(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        r3 = org.telegram.messenger.Utilities.parseInt(r3).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00c3, code lost:
        r3 = org.telegram.messenger.Utilities.parseInt(r3).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0105, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0106, code lost:
        r4 = r9;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x00b9 */
    /* JADX WARNING: Removed duplicated region for block: B:100:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00f5 A[LOOP:0: B:14:0x0059->B:54:0x00f5, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0111 A[SYNTHETIC, Splitter:B:67:0x0111] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0134  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x00fa A[EDGE_INSN: B:92:0x00fa->B:55:0x00fa ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r17, boolean r18) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            r1.currentThemeInfo = r0
            java.lang.String r2 = r17.getName()
            java.lang.String r3 = ".attheme"
            boolean r3 = r2.endsWith(r3)
            r4 = 0
            if (r3 == 0) goto L_0x001d
            r3 = 46
            int r3 = r2.lastIndexOf(r3)
            java.lang.String r2 = r2.substring(r4, r3)
        L_0x001d:
            android.widget.TextView r3 = r1.textView
            r3.setText(r2)
            r2 = r18
            r1.needDivider = r2
            r16.updateCurrentThemeCheck()
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r0.getAccent(r4)
            java.lang.String r3 = r0.assetName
            java.lang.String r5 = "actionBarDefault"
            r7 = 1
            if (r3 == 0) goto L_0x0048
            android.graphics.Paint r3 = r1.paint
            if (r2 == 0) goto L_0x003a
            int r4 = r2.accentColor
        L_0x003a:
            int r8 = r17.getPreviewBackgroundColor()
            int r0 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r0, r4, r8)
            r3.setColor(r0)
            r4 = 1
            goto L_0x0125
        L_0x0048:
            java.lang.String r0 = r0.pathToFile
            if (r0 == 0) goto L_0x0123
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x0108 }
            r3.<init>(r0)     // Catch:{ all -> 0x0108 }
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ all -> 0x0108 }
            r8.<init>(r3)     // Catch:{ all -> 0x0108 }
            r0 = 0
            r3 = 0
            r9 = 0
        L_0x0059:
            byte[] r10 = bytes     // Catch:{ all -> 0x0105 }
            int r10 = r8.read(r10)     // Catch:{ all -> 0x0105 }
            r11 = -1
            if (r10 == r11) goto L_0x00fa
            r14 = r0
            r12 = r3
            r3 = 0
            r13 = 0
        L_0x0066:
            if (r3 >= r10) goto L_0x00e1
            byte[] r15 = bytes     // Catch:{ all -> 0x0105 }
            byte r15 = r15[r3]     // Catch:{ all -> 0x0105 }
            r6 = 10
            if (r15 != r6) goto L_0x00da
            int r12 = r12 + 1
            int r6 = r3 - r13
            int r6 = r6 + r7
            java.lang.String r15 = new java.lang.String     // Catch:{ all -> 0x0105 }
            byte[] r7 = bytes     // Catch:{ all -> 0x0105 }
            int r4 = r6 + -1
            java.lang.String r11 = "UTF-8"
            r15.<init>(r7, r13, r4, r11)     // Catch:{ all -> 0x0105 }
            java.lang.String r4 = "WPS"
            boolean r4 = r15.startsWith(r4)     // Catch:{ all -> 0x0105 }
            if (r4 == 0) goto L_0x008b
            r3 = r12
            r11 = 0
            goto L_0x00e3
        L_0x008b:
            r4 = 61
            int r4 = r15.indexOf(r4)     // Catch:{ all -> 0x0105 }
            r7 = -1
            if (r4 == r7) goto L_0x00d6
            r11 = 0
            java.lang.String r7 = r15.substring(r11, r4)     // Catch:{ all -> 0x0105 }
            boolean r7 = r7.equals(r5)     // Catch:{ all -> 0x0105 }
            if (r7 == 0) goto L_0x00d6
            int r4 = r4 + 1
            java.lang.String r3 = r15.substring(r4)     // Catch:{ all -> 0x0105 }
            int r4 = r3.length()     // Catch:{ all -> 0x0105 }
            if (r4 <= 0) goto L_0x00c2
            r11 = 0
            char r4 = r3.charAt(r11)     // Catch:{ all -> 0x0105 }
            r6 = 35
            if (r4 != r6) goto L_0x00c3
            int r3 = android.graphics.Color.parseColor(r3)     // Catch:{ Exception -> 0x00b9 }
            goto L_0x00cb
        L_0x00b9:
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x0105 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x0105 }
            goto L_0x00cb
        L_0x00c2:
            r11 = 0
        L_0x00c3:
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x0105 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x0105 }
        L_0x00cb:
            android.graphics.Paint r4 = r1.paint     // Catch:{ all -> 0x00d3 }
            r4.setColor(r3)     // Catch:{ all -> 0x00d3 }
            r3 = r12
            r9 = 1
            goto L_0x00e3
        L_0x00d3:
            r0 = move-exception
            r4 = 1
            goto L_0x010c
        L_0x00d6:
            r11 = 0
            int r13 = r13 + r6
            int r14 = r14 + r6
            goto L_0x00db
        L_0x00da:
            r11 = 0
        L_0x00db:
            int r3 = r3 + 1
            r4 = 0
            r7 = 1
            r11 = -1
            goto L_0x0066
        L_0x00e1:
            r11 = 0
            r3 = r12
        L_0x00e3:
            if (r0 == r14) goto L_0x00fa
            r0 = 500(0x1f4, float:7.0E-43)
            if (r3 < r0) goto L_0x00ea
            goto L_0x00fa
        L_0x00ea:
            java.nio.channels.FileChannel r0 = r8.getChannel()     // Catch:{ all -> 0x0105 }
            long r6 = (long) r14     // Catch:{ all -> 0x0105 }
            r0.position(r6)     // Catch:{ all -> 0x0105 }
            if (r9 == 0) goto L_0x00f5
            goto L_0x00fa
        L_0x00f5:
            r0 = r14
            r4 = 0
            r7 = 1
            goto L_0x0059
        L_0x00fa:
            r4 = r9
            r8.close()     // Catch:{ Exception -> 0x00ff }
            goto L_0x0125
        L_0x00ff:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            goto L_0x0125
        L_0x0105:
            r0 = move-exception
            r4 = r9
            goto L_0x010c
        L_0x0108:
            r0 = move-exception
            r11 = 0
            r4 = 0
            r8 = 0
        L_0x010c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0115 }
            if (r8 == 0) goto L_0x0125
            r8.close()     // Catch:{ Exception -> 0x00ff }
            goto L_0x0125
        L_0x0115:
            r0 = move-exception
            r2 = r0
            if (r8 == 0) goto L_0x0122
            r8.close()     // Catch:{ Exception -> 0x011d }
            goto L_0x0122
        L_0x011d:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0122:
            throw r2
        L_0x0123:
            r11 = 0
            r4 = 0
        L_0x0125:
            if (r4 != 0) goto L_0x0130
            android.graphics.Paint r0 = r1.paint
            int r3 = org.telegram.ui.ActionBar.Theme.getDefaultColor(r5)
            r0.setColor(r3)
        L_0x0130:
            android.graphics.Paint r0 = r1.paintStroke
            if (r2 == 0) goto L_0x013b
            int r3 = r2.accentColor
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)
            goto L_0x013c
        L_0x013b:
            r6 = 0
        L_0x013c:
            int r3 = r6.intValue()
            r0.setColor(r3)
            if (r2 == 0) goto L_0x0150
            int r0 = r2.accentColor
            if (r0 == 0) goto L_0x0150
            android.graphics.Paint r0 = r1.paintStroke
            r2 = 180(0xb4, float:2.52E-43)
            r0.setAlpha(r2)
        L_0x0150:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemeCell.setTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

    public void updateCurrentThemeCheck() {
        Theme.ThemeInfo themeInfo;
        if (this.isNightTheme) {
            themeInfo = Theme.getCurrentNightTheme();
        } else {
            themeInfo = Theme.getCurrentTheme();
        }
        int i = this.currentThemeInfo == themeInfo ? 0 : 4;
        if (this.checkImage.getVisibility() != i) {
            this.checkImage.setVisibility(i);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        int dp = AndroidUtilities.dp(31.0f);
        if (LocaleController.isRTL) {
            dp = getWidth() - dp;
        }
        float f = (float) dp;
        canvas.drawCircle(f, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(11.0f), this.paint);
        canvas.drawCircle(f, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(10.0f), this.paintStroke);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        setSelected(this.checkImage.getVisibility() == 0);
    }
}
