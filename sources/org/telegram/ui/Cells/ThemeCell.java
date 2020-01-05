package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeCell extends FrameLayout {
    private static byte[] bytes = new byte[1024];
    private ImageView checkImage;
    private ThemeInfo currentThemeInfo;
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
        this.paintStroke.setStyle(Style.STROKE);
        this.paintStroke.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setEllipsize(TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 105.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 105.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
        this.checkImage.setImageResource(NUM);
        ImageView imageView;
        if (this.isNightTheme) {
            imageView = this.checkImage;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(imageView, LayoutHelper.createFrame(19, 14.0f, i | 16, 21.0f, 0.0f, 21.0f, 0.0f));
            return;
        }
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : 5) | 16, 59.0f, 0.0f, 59.0f, 0.0f));
        this.optionsButton = new ImageView(context);
        this.optionsButton.setFocusable(false);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
        this.optionsButton.setImageResource(NUM);
        this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), Mode.MULTIPLY));
        this.optionsButton.setScaleType(ScaleType.CENTER);
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        imageView = this.optionsButton;
        if (LocaleController.isRTL) {
            i = 3;
        }
        addView(imageView, LayoutHelper.createFrame(48, 48, i | 48));
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + this.needDivider, NUM));
    }

    public void setOnOptionsClick(OnClickListener onClickListener) {
        this.optionsButton.setOnClickListener(onClickListener);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public ThemeInfo getCurrentThemeInfo() {
        return this.currentThemeInfo;
    }

    /* JADX WARNING: Removed duplicated region for block: B:79:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:93:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0102 A:{SYNTHETIC, Splitter:B:66:0x0102} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:93:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x00aa */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0102 A:{SYNTHETIC, Splitter:B:66:0x0102} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:93:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x013b  */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    public void setTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r17, boolean r18) {
        /*
        r16 = this;
        r1 = r16;
        r2 = r17;
        r1.currentThemeInfo = r2;
        r0 = r17.getName();
        r3 = ".attheme";
        r3 = r0.endsWith(r3);
        r4 = 0;
        if (r3 == 0) goto L_0x001d;
    L_0x0013:
        r3 = 46;
        r3 = r0.lastIndexOf(r3);
        r0 = r0.substring(r4, r3);
    L_0x001d:
        r3 = r1.textView;
        r3.setText(r0);
        r0 = r18;
        r1.needDivider = r0;
        r16.updateCurrentThemeCheck();
        r0 = org.telegram.ui.ActionBar.Theme.isThemeDefault(r17);
        r3 = "actionBarDefault";
        r5 = 1;
        if (r0 != 0) goto L_0x0117;
    L_0x0032:
        r0 = r2.assetName;
        if (r0 == 0) goto L_0x0038;
    L_0x0036:
        goto L_0x0117;
    L_0x0038:
        r0 = r2.pathToFile;
        if (r0 == 0) goto L_0x0114;
    L_0x003c:
        r6 = 0;
        r7 = new java.io.File;	 Catch:{ all -> 0x00f9 }
        r7.<init>(r0);	 Catch:{ all -> 0x00f9 }
        r8 = new java.io.FileInputStream;	 Catch:{ all -> 0x00f9 }
        r8.<init>(r7);	 Catch:{ all -> 0x00f9 }
        r0 = 0;
        r6 = 0;
        r7 = 0;
    L_0x004a:
        r9 = bytes;	 Catch:{ all -> 0x00f6 }
        r9 = r8.read(r9);	 Catch:{ all -> 0x00f6 }
        r10 = -1;
        if (r9 == r10) goto L_0x00eb;
    L_0x0053:
        r13 = r0;
        r11 = r6;
        r6 = 0;
        r12 = 0;
    L_0x0057:
        if (r6 >= r9) goto L_0x00d2;
    L_0x0059:
        r14 = bytes;	 Catch:{ all -> 0x00f6 }
        r14 = r14[r6];	 Catch:{ all -> 0x00f6 }
        r15 = 10;
        if (r14 != r15) goto L_0x00cb;
    L_0x0061:
        r11 = r11 + 1;
        r14 = r6 - r12;
        r14 = r14 + r5;
        r15 = new java.lang.String;	 Catch:{ all -> 0x00f6 }
        r5 = bytes;	 Catch:{ all -> 0x00f6 }
        r4 = r14 + -1;
        r10 = "UTF-8";
        r15.<init>(r5, r12, r4, r10);	 Catch:{ all -> 0x00f6 }
        r4 = "WPS";
        r4 = r15.startsWith(r4);	 Catch:{ all -> 0x00f6 }
        if (r4 == 0) goto L_0x007c;
    L_0x0079:
        r6 = r11;
        r10 = 0;
        goto L_0x00d4;
    L_0x007c:
        r4 = 61;
        r4 = r15.indexOf(r4);	 Catch:{ all -> 0x00f6 }
        r5 = -1;
        if (r4 == r5) goto L_0x00c7;
    L_0x0085:
        r10 = 0;
        r5 = r15.substring(r10, r4);	 Catch:{ all -> 0x00f6 }
        r5 = r5.equals(r3);	 Catch:{ all -> 0x00f6 }
        if (r5 == 0) goto L_0x00c7;
    L_0x0090:
        r4 = r4 + 1;
        r4 = r15.substring(r4);	 Catch:{ all -> 0x00f6 }
        r5 = r4.length();	 Catch:{ all -> 0x00f6 }
        if (r5 <= 0) goto L_0x00b3;
    L_0x009c:
        r10 = 0;
        r5 = r4.charAt(r10);	 Catch:{ all -> 0x00f6 }
        r6 = 35;
        if (r5 != r6) goto L_0x00b4;
    L_0x00a5:
        r4 = android.graphics.Color.parseColor(r4);	 Catch:{ Exception -> 0x00aa }
        goto L_0x00bc;
    L_0x00aa:
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ all -> 0x00f6 }
        r4 = r4.intValue();	 Catch:{ all -> 0x00f6 }
        goto L_0x00bc;
    L_0x00b3:
        r10 = 0;
    L_0x00b4:
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ all -> 0x00f6 }
        r4 = r4.intValue();	 Catch:{ all -> 0x00f6 }
    L_0x00bc:
        r5 = r1.paint;	 Catch:{ all -> 0x00c4 }
        r5.setColor(r4);	 Catch:{ all -> 0x00c4 }
        r6 = r11;
        r7 = 1;
        goto L_0x00d4;
    L_0x00c4:
        r0 = move-exception;
        r4 = 1;
        goto L_0x00fd;
    L_0x00c7:
        r10 = 0;
        r12 = r12 + r14;
        r13 = r13 + r14;
        goto L_0x00cc;
    L_0x00cb:
        r10 = 0;
    L_0x00cc:
        r6 = r6 + 1;
        r4 = 0;
        r5 = 1;
        r10 = -1;
        goto L_0x0057;
    L_0x00d2:
        r10 = 0;
        r6 = r11;
    L_0x00d4:
        if (r0 == r13) goto L_0x00eb;
    L_0x00d6:
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r6 < r0) goto L_0x00db;
    L_0x00da:
        goto L_0x00eb;
    L_0x00db:
        r0 = r8.getChannel();	 Catch:{ all -> 0x00f6 }
        r4 = (long) r13;	 Catch:{ all -> 0x00f6 }
        r0.position(r4);	 Catch:{ all -> 0x00f6 }
        if (r7 == 0) goto L_0x00e6;
    L_0x00e5:
        goto L_0x00eb;
    L_0x00e6:
        r0 = r13;
        r4 = 0;
        r5 = 1;
        goto L_0x004a;
    L_0x00eb:
        r4 = r7;
        r8.close();	 Catch:{ Exception -> 0x00f0 }
        goto L_0x0125;
    L_0x00f0:
        r0 = move-exception;
        r5 = r0;
        org.telegram.messenger.FileLog.e(r5);
        goto L_0x0125;
    L_0x00f6:
        r0 = move-exception;
        r4 = r7;
        goto L_0x00fd;
    L_0x00f9:
        r0 = move-exception;
        r10 = 0;
        r8 = r6;
        r4 = 0;
    L_0x00fd:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0106 }
        if (r8 == 0) goto L_0x0125;
    L_0x0102:
        r8.close();	 Catch:{ Exception -> 0x00f0 }
        goto L_0x0125;
    L_0x0106:
        r0 = move-exception;
        r2 = r0;
        if (r8 == 0) goto L_0x0113;
    L_0x010a:
        r8.close();	 Catch:{ Exception -> 0x010e }
        goto L_0x0113;
    L_0x010e:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0113:
        throw r2;
    L_0x0114:
        r10 = 0;
        r4 = 0;
        goto L_0x0125;
    L_0x0117:
        r0 = r1.paint;
        r4 = r2.accentColor;
        r5 = r2.previewBackgroundColor;
        r4 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r2, r4, r5);
        r0.setColor(r4);
        r4 = 1;
    L_0x0125:
        if (r4 != 0) goto L_0x0130;
    L_0x0127:
        r0 = r1.paint;
        r3 = org.telegram.ui.ActionBar.Theme.getDefaultColor(r3);
        r0.setColor(r3);
    L_0x0130:
        r0 = r1.paintStroke;
        r3 = r2.accentColor;
        r0.setColor(r3);
        r0 = r2.accentColor;
        if (r0 == 0) goto L_0x0142;
    L_0x013b:
        r0 = r1.paintStroke;
        r2 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r0.setAlpha(r2);
    L_0x0142:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemeCell.setTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

    public void updateCurrentThemeCheck() {
        ThemeInfo currentNightTheme;
        if (this.isNightTheme) {
            currentNightTheme = Theme.getCurrentNightTheme();
        } else {
            currentNightTheme = Theme.getCurrentTheme();
        }
        int i = this.currentThemeInfo == currentNightTheme ? 0 : 4;
        if (this.checkImage.getVisibility() != i) {
            this.checkImage.setVisibility(i);
        }
    }

    /* Access modifiers changed, original: protected */
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
