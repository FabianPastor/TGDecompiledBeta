package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private TextView textView;

    public ThemeCell(Context context, boolean z) {
        super(context);
        setWillNotDraw(false);
        this.isNightTheme = z;
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

    /* JADX WARNING: Removed duplicated region for block: B:90:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0106 A:{SYNTHETIC, Splitter:B:70:0x0106} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:90:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00f6 A:{Splitter:B:15:0x0050, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:36:0x00af */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0106 A:{SYNTHETIC, Splitter:B:70:0x0106} */
    /* JADX WARNING: Removed duplicated region for block: B:90:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0117 A:{SYNTHETIC, Splitter:B:75:0x0117} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing block: B:43:0x00c9, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:44:0x00ca, code skipped:
            r2 = r6;
            r4 = 1;
     */
    /* JADX WARNING: Missing block: B:60:0x00f6, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:71:?, code skipped:
            r2.close();
     */
    /* JADX WARNING: Missing block: B:76:?, code skipped:
            r6.close();
     */
    /* JADX WARNING: Missing block: B:77:0x011b, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:78:0x011c, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    public void setTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r17, boolean r18) {
        /*
        r16 = this;
        r1 = r16;
        r0 = r17;
        r1.currentThemeInfo = r0;
        r2 = r17.getName();
        r3 = ".attheme";
        r3 = r2.endsWith(r3);
        r4 = 0;
        if (r3 == 0) goto L_0x001d;
    L_0x0013:
        r3 = 46;
        r3 = r2.lastIndexOf(r3);
        r2 = r2.substring(r4, r3);
    L_0x001d:
        r3 = r1.textView;
        r3.setText(r2);
        r2 = r18;
        r1.needDivider = r2;
        r16.updateCurrentThemeCheck();
        r2 = r0.pathToFile;
        r3 = "actionBarDefault";
        if (r2 != 0) goto L_0x0033;
    L_0x002f:
        r2 = r0.assetName;
        if (r2 == 0) goto L_0x0109;
    L_0x0033:
        r2 = 0;
        r5 = 1;
        r6 = r0.assetName;	 Catch:{ Throwable -> 0x0100 }
        if (r6 == 0) goto L_0x0040;
    L_0x0039:
        r0 = r0.assetName;	 Catch:{ Throwable -> 0x0100 }
        r0 = org.telegram.ui.ActionBar.Theme.getAssetFile(r0);	 Catch:{ Throwable -> 0x0100 }
        goto L_0x0048;
    L_0x0040:
        r6 = new java.io.File;	 Catch:{ Throwable -> 0x0100 }
        r0 = r0.pathToFile;	 Catch:{ Throwable -> 0x0100 }
        r6.<init>(r0);	 Catch:{ Throwable -> 0x0100 }
        r0 = r6;
    L_0x0048:
        r6 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0100 }
        r6.<init>(r0);	 Catch:{ Throwable -> 0x0100 }
        r0 = 0;
        r2 = 0;
        r7 = 0;
    L_0x0050:
        r8 = bytes;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r8 = r6.read(r8);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r9 = -1;
        if (r8 == r9) goto L_0x00eb;
    L_0x0059:
        r12 = r0;
        r10 = r2;
        r2 = 0;
        r11 = 0;
    L_0x005d:
        if (r2 >= r8) goto L_0x00d4;
    L_0x005f:
        r13 = bytes;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r13 = r13[r2];	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r14 = 10;
        if (r13 != r14) goto L_0x00d0;
    L_0x0067:
        r10 = r10 + 1;
        r13 = r2 - r11;
        r13 = r13 + r5;
        r14 = new java.lang.String;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r15 = bytes;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r5 = r13 + -1;
        r4 = "UTF-8";
        r14.<init>(r15, r11, r5, r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = "WPS";
        r4 = r14.startsWith(r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r4 == 0) goto L_0x0082;
    L_0x007f:
        r2 = r10;
        r4 = 0;
        goto L_0x00d5;
    L_0x0082:
        r4 = 61;
        r4 = r14.indexOf(r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r4 == r9) goto L_0x00cd;
    L_0x008a:
        r5 = 0;
        r15 = r14.substring(r5, r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r5 = r15.equals(r3);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r5 == 0) goto L_0x00cd;
    L_0x0095:
        r4 = r4 + 1;
        r2 = r14.substring(r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = r2.length();	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r4 <= 0) goto L_0x00b8;
    L_0x00a1:
        r4 = 0;
        r5 = r2.charAt(r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r8 = 35;
        if (r5 != r8) goto L_0x00b9;
    L_0x00aa:
        r2 = android.graphics.Color.parseColor(r2);	 Catch:{ Exception -> 0x00af }
        goto L_0x00c1;
    L_0x00af:
        r2 = org.telegram.messenger.Utilities.parseInt(r2);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r2 = r2.intValue();	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        goto L_0x00c1;
    L_0x00b8:
        r4 = 0;
    L_0x00b9:
        r2 = org.telegram.messenger.Utilities.parseInt(r2);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r2 = r2.intValue();	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
    L_0x00c1:
        r5 = r1.paint;	 Catch:{ Throwable -> 0x00c9, all -> 0x00f6 }
        r5.setColor(r2);	 Catch:{ Throwable -> 0x00c9, all -> 0x00f6 }
        r2 = r10;
        r7 = 1;
        goto L_0x00d5;
    L_0x00c9:
        r0 = move-exception;
        r2 = r6;
        r4 = 1;
        goto L_0x0101;
    L_0x00cd:
        r4 = 0;
        r11 = r11 + r13;
        r12 = r12 + r13;
    L_0x00d0:
        r2 = r2 + 1;
        r5 = 1;
        goto L_0x005d;
    L_0x00d4:
        r2 = r10;
    L_0x00d5:
        if (r0 == r12) goto L_0x00eb;
    L_0x00d7:
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r2 < r0) goto L_0x00dc;
    L_0x00db:
        goto L_0x00eb;
    L_0x00dc:
        r0 = r6.getChannel();	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r8 = (long) r12;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r0.position(r8);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r7 == 0) goto L_0x00e7;
    L_0x00e6:
        goto L_0x00eb;
    L_0x00e7:
        r0 = r12;
        r5 = 1;
        goto L_0x0050;
    L_0x00eb:
        r4 = r7;
        r6.close();	 Catch:{ Exception -> 0x00f0 }
        goto L_0x0109;
    L_0x00f0:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
        goto L_0x0109;
    L_0x00f6:
        r0 = move-exception;
        goto L_0x00fe;
    L_0x00f8:
        r0 = move-exception;
        r2 = r6;
        r4 = r7;
        goto L_0x0101;
    L_0x00fc:
        r0 = move-exception;
        r6 = r2;
    L_0x00fe:
        r2 = r0;
        goto L_0x0115;
    L_0x0100:
        r0 = move-exception;
    L_0x0101:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x00fc }
        if (r2 == 0) goto L_0x0109;
    L_0x0106:
        r2.close();	 Catch:{ Exception -> 0x00f0 }
    L_0x0109:
        if (r4 != 0) goto L_0x0114;
    L_0x010b:
        r0 = r1.paint;
        r2 = org.telegram.ui.ActionBar.Theme.getDefaultColor(r3);
        r0.setColor(r2);
    L_0x0114:
        return;
    L_0x0115:
        if (r6 == 0) goto L_0x0120;
    L_0x0117:
        r6.close();	 Catch:{ Exception -> 0x011b }
        goto L_0x0120;
    L_0x011b:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0120:
        goto L_0x0122;
    L_0x0121:
        throw r2;
    L_0x0122:
        goto L_0x0121;
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
        canvas.drawCircle((float) dp, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(11.0f), this.paint);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        setSelected(this.checkImage.getVisibility() == 0);
    }
}
