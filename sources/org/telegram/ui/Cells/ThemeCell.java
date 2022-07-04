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
    private Paint paintStroke;
    private TextView textView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ThemeCell(Context context, boolean nightTheme) {
        super(context);
        Context context2 = context;
        setWillNotDraw(false);
        this.isNightTheme = nightTheme;
        Paint paint2 = new Paint(1);
        this.paintStroke = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.paintStroke.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 105.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 105.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.checkImage = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
        this.checkImage.setImageResource(NUM);
        if (!this.isNightTheme) {
            addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : 5) | 16, 59.0f, 0.0f, 59.0f, 0.0f));
            ImageView imageView2 = new ImageView(context2);
            this.optionsButton = imageView2;
            imageView2.setFocusable(false);
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setOnOptionsClick(View.OnClickListener listener) {
        this.optionsButton.setOnClickListener(listener);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public Theme.ThemeInfo getCurrentThemeInfo() {
        return this.currentThemeInfo;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b6, code lost:
        r17 = r2.substring(r5 + 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00c2, code lost:
        if (r17.length() <= 0) goto L_0x00e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c4, code lost:
        r20 = r2;
        r16 = r5;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00d1, code lost:
        if (r2.charAt(0) != '#') goto L_0x00eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r0 = android.graphics.Color.parseColor(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00e5, code lost:
        r20 = r2;
        r16 = r5;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00eb, code lost:
        r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2).intValue();
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x012c A[LOOP:0: B:15:0x0061->B:61:0x012c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0152 A[SYNTHETIC, Splitter:B:73:0x0152] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0178  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x017f  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x018d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r23, boolean r24) {
        /*
            r22 = this;
            r1 = r22
            r2 = r23
            r1.currentThemeInfo = r2
            java.lang.String r0 = r23.getName()
            java.lang.String r3 = ".attheme"
            boolean r3 = r0.endsWith(r3)
            r4 = 0
            if (r3 == 0) goto L_0x001f
            r3 = 46
            int r3 = r0.lastIndexOf(r3)
            java.lang.String r0 = r0.substring(r4, r3)
            r3 = r0
            goto L_0x0020
        L_0x001f:
            r3 = r0
        L_0x0020:
            android.widget.TextView r0 = r1.textView
            r0.setText(r3)
            r5 = r24
            r1.needDivider = r5
            r22.updateCurrentThemeCheck()
            r6 = 0
            org.telegram.ui.ActionBar.Theme$ThemeAccent r7 = r2.getAccent(r4)
            java.lang.String r0 = r2.assetName
            java.lang.String r8 = "actionBarDefault"
            if (r0 == 0) goto L_0x004d
            android.graphics.Paint r0 = r1.paint
            if (r7 == 0) goto L_0x003d
            int r4 = r7.accentColor
        L_0x003d:
            int r9 = r23.getPreviewBackgroundColor()
            int r4 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r2, r4, r9)
            r0.setColor(r4)
            r6 = 1
            r18 = r3
            goto L_0x0169
        L_0x004d:
            java.lang.String r0 = r2.pathToFile
            if (r0 == 0) goto L_0x0167
            r9 = 0
            r0 = 0
            java.io.File r10 = new java.io.File     // Catch:{ all -> 0x014a }
            java.lang.String r11 = r2.pathToFile     // Catch:{ all -> 0x014a }
            r10.<init>(r11)     // Catch:{ all -> 0x014a }
            java.io.FileInputStream r11 = new java.io.FileInputStream     // Catch:{ all -> 0x014a }
            r11.<init>(r10)     // Catch:{ all -> 0x014a }
            r9 = r11
            r11 = 0
        L_0x0061:
            byte[] r12 = bytes     // Catch:{ all -> 0x014a }
            int r12 = r9.read(r12)     // Catch:{ all -> 0x014a }
            r13 = r12
            r14 = -1
            if (r12 == r14) goto L_0x013c
            r12 = r0
            r15 = 0
            r16 = 0
            r4 = r16
            r21 = r11
            r11 = r0
            r0 = r21
        L_0x0076:
            if (r4 >= r13) goto L_0x0118
            byte[] r14 = bytes     // Catch:{ all -> 0x014a }
            byte r2 = r14[r4]     // Catch:{ all -> 0x014a }
            r18 = r3
            r3 = 10
            if (r2 != r3) goto L_0x010d
            int r2 = r0 + 1
            int r0 = r4 - r15
            int r3 = r0 + 1
            java.lang.String r0 = new java.lang.String     // Catch:{ all -> 0x013a }
            r19 = r2
            int r2 = r3 + -1
            java.lang.String r5 = "UTF-8"
            r0.<init>(r14, r15, r2, r5)     // Catch:{ all -> 0x013a }
            r2 = r0
            java.lang.String r0 = "WPS"
            boolean r0 = r2.startsWith(r0)     // Catch:{ all -> 0x013a }
            if (r0 == 0) goto L_0x00a0
            r0 = r19
            goto L_0x011a
        L_0x00a0:
            r0 = 61
            int r0 = r2.indexOf(r0)     // Catch:{ all -> 0x013a }
            r5 = r0
            r14 = -1
            if (r0 == r14) goto L_0x0105
            r14 = 0
            java.lang.String r0 = r2.substring(r14, r5)     // Catch:{ all -> 0x013a }
            r14 = r0
            boolean r0 = r14.equals(r8)     // Catch:{ all -> 0x013a }
            if (r0 == 0) goto L_0x0100
            int r0 = r5 + 1
            java.lang.String r0 = r2.substring(r0)     // Catch:{ all -> 0x013a }
            r17 = r0
            int r0 = r17.length()     // Catch:{ all -> 0x013a }
            if (r0 <= 0) goto L_0x00e5
            r20 = r2
            r16 = r5
            r2 = r17
            r5 = 0
            char r0 = r2.charAt(r5)     // Catch:{ all -> 0x013a }
            r5 = 35
            if (r0 != r5) goto L_0x00eb
            int r0 = android.graphics.Color.parseColor(r2)     // Catch:{ Exception -> 0x00d8 }
        L_0x00d7:
            goto L_0x00f3
        L_0x00d8:
            r0 = move-exception
            r5 = r0
            r0 = r5
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)     // Catch:{ all -> 0x013a }
            int r5 = r5.intValue()     // Catch:{ all -> 0x013a }
            r0 = r5
            goto L_0x00d7
        L_0x00e5:
            r20 = r2
            r16 = r5
            r2 = r17
        L_0x00eb:
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)     // Catch:{ all -> 0x013a }
            int r0 = r0.intValue()     // Catch:{ all -> 0x013a }
        L_0x00f3:
            r5 = 1
            android.graphics.Paint r6 = r1.paint     // Catch:{ all -> 0x00fd }
            r6.setColor(r0)     // Catch:{ all -> 0x00fd }
            r6 = r5
            r0 = r19
            goto L_0x011a
        L_0x00fd:
            r0 = move-exception
            r6 = r5
            goto L_0x014d
        L_0x0100:
            r20 = r2
            r16 = r5
            goto L_0x0109
        L_0x0105:
            r20 = r2
            r16 = r5
        L_0x0109:
            int r15 = r15 + r3
            int r11 = r11 + r3
            r0 = r19
        L_0x010d:
            int r4 = r4 + 1
            r2 = r23
            r5 = r24
            r3 = r18
            r14 = -1
            goto L_0x0076
        L_0x0118:
            r18 = r3
        L_0x011a:
            if (r12 == r11) goto L_0x013e
            r2 = 500(0x1f4, float:7.0E-43)
            if (r0 < r2) goto L_0x0121
            goto L_0x013e
        L_0x0121:
            java.nio.channels.FileChannel r2 = r9.getChannel()     // Catch:{ all -> 0x013a }
            long r3 = (long) r11     // Catch:{ all -> 0x013a }
            r2.position(r3)     // Catch:{ all -> 0x013a }
            if (r6 == 0) goto L_0x012c
            goto L_0x013e
        L_0x012c:
            r2 = r23
            r5 = r24
            r3 = r18
            r4 = 0
            r21 = r11
            r11 = r0
            r0 = r21
            goto L_0x0061
        L_0x013a:
            r0 = move-exception
            goto L_0x014d
        L_0x013c:
            r18 = r3
        L_0x013e:
            r9.close()     // Catch:{ Exception -> 0x0143 }
        L_0x0142:
            goto L_0x0169
        L_0x0143:
            r0 = move-exception
            r2 = r0
            r0 = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0169
        L_0x014a:
            r0 = move-exception
            r18 = r3
        L_0x014d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0156 }
            if (r9 == 0) goto L_0x0142
            r9.close()     // Catch:{ Exception -> 0x0143 }
            goto L_0x0142
        L_0x0156:
            r0 = move-exception
            r2 = r0
            if (r9 == 0) goto L_0x0165
            r9.close()     // Catch:{ Exception -> 0x015e }
            goto L_0x0165
        L_0x015e:
            r0 = move-exception
            r3 = r0
            r0 = r3
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0166
        L_0x0165:
        L_0x0166:
            throw r2
        L_0x0167:
            r18 = r3
        L_0x0169:
            if (r6 != 0) goto L_0x0174
            android.graphics.Paint r0 = r1.paint
            int r2 = org.telegram.ui.ActionBar.Theme.getDefaultColor(r8)
            r0.setColor(r2)
        L_0x0174:
            android.graphics.Paint r0 = r1.paintStroke
            if (r7 == 0) goto L_0x017f
            int r2 = r7.accentColor
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            goto L_0x0180
        L_0x017f:
            r2 = 0
        L_0x0180:
            int r2 = r2.intValue()
            r0.setColor(r2)
            if (r7 == 0) goto L_0x0194
            int r0 = r7.accentColor
            if (r0 == 0) goto L_0x0194
            android.graphics.Paint r0 = r1.paintStroke
            r2 = 180(0xb4, float:2.52E-43)
            r0.setAlpha(r2)
        L_0x0194:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemeCell.setTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

    public void updateCurrentThemeCheck() {
        Theme.ThemeInfo currentTheme;
        if (this.isNightTheme) {
            currentTheme = Theme.getCurrentNightTheme();
        } else {
            currentTheme = Theme.getCurrentTheme();
        }
        int newVisibility = this.currentThemeInfo == currentTheme ? 0 : 4;
        if (this.checkImage.getVisibility() != newVisibility) {
            this.checkImage.setVisibility(newVisibility);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        int x = AndroidUtilities.dp(31.0f);
        if (LocaleController.isRTL) {
            x = getWidth() - x;
        }
        canvas.drawCircle((float) x, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(11.0f), this.paint);
        canvas.drawCircle((float) x, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(10.0f), this.paintStroke);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        setSelected(this.checkImage.getVisibility() == 0);
    }
}
