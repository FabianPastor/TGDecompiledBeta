package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
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
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 101.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 101.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        this.checkImage.setImageResource(C0446R.drawable.sticker_added);
        if (this.isNightTheme) {
            context = this.checkImage;
            if (!LocaleController.isRTL) {
                i = 5;
            }
            addView(context, LayoutHelper.createFrame(19, 14.0f, i | 16, 17.0f, 0.0f, 17.0f, 0.0f));
            return;
        }
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : 5) | 16, 55.0f, 0.0f, 55.0f, 0.0f));
        this.optionsButton = new ImageView(context);
        this.optionsButton.setFocusable(false);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
        this.optionsButton.setImageResource(true);
        this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), Mode.MULTIPLY));
        this.optionsButton.setScaleType(ScaleType.CENTER);
        context = this.optionsButton;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(context, LayoutHelper.createFrame(48, 48, i | 48));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + this.needDivider, NUM));
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

    public void setTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r18, boolean r19) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r17 = this;
        r1 = r17;
        r2 = r18;
        r1.currentThemeInfo = r2;
        r3 = r18.getName();
        r4 = ".attheme";
        r4 = r3.endsWith(r4);
        r5 = 0;
        if (r4 == 0) goto L_0x001d;
    L_0x0013:
        r4 = 46;
        r4 = r3.lastIndexOf(r4);
        r3 = r3.substring(r5, r4);
    L_0x001d:
        r4 = r1.textView;
        r4.setText(r3);
        r3 = r19;
        r1.needDivider = r3;
        r17.updateCurrentThemeCheck();
        r3 = r2.pathToFile;
        if (r3 != 0) goto L_0x0031;
    L_0x002d:
        r3 = r2.assetName;
        if (r3 == 0) goto L_0x0117;
    L_0x0031:
        r3 = 0;
        r4 = 1;
        r6 = r2.assetName;	 Catch:{ Throwable -> 0x010c }
        if (r6 == 0) goto L_0x0041;
    L_0x0037:
        r2 = r2.assetName;	 Catch:{ Throwable -> 0x003e }
        r2 = org.telegram.ui.ActionBar.Theme.getAssetFile(r2);	 Catch:{ Throwable -> 0x003e }
        goto L_0x0049;
    L_0x003e:
        r0 = move-exception;
        goto L_0x010e;
    L_0x0041:
        r6 = new java.io.File;	 Catch:{ Throwable -> 0x010c }
        r2 = r2.pathToFile;	 Catch:{ Throwable -> 0x010c }
        r6.<init>(r2);	 Catch:{ Throwable -> 0x010c }
        r2 = r6;	 Catch:{ Throwable -> 0x010c }
    L_0x0049:
        r6 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x010c }
        r6.<init>(r2);	 Catch:{ Throwable -> 0x010c }
        r2 = r5;
        r3 = r2;
        r7 = r3;
    L_0x0051:
        r8 = bytes;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r8 = r6.read(r8);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r9 = -1;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        if (r8 == r9) goto L_0x00f4;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x005a:
        r12 = r2;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r10 = r3;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r3 = r5;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r11 = r3;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x005e:
        if (r3 >= r8) goto L_0x00db;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x0060:
        r13 = bytes;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r13 = r13[r3];	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r14 = 10;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        if (r13 != r14) goto L_0x00d5;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x0068:
        r10 = r10 + 1;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r13 = r3 - r11;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r13 = r13 + r4;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r14 = new java.lang.String;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r15 = bytes;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r4 = r13 + -1;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r5 = "UTF-8";	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r14.<init>(r15, r11, r4, r5);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r4 = "WPS";	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r4 = r14.startsWith(r4);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        if (r4 == 0) goto L_0x0083;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x0080:
        r3 = r10;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r4 = 0;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        goto L_0x00dd;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x0083:
        r4 = 61;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r4 = r14.indexOf(r4);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        if (r4 == r9) goto L_0x00d1;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x008b:
        r5 = 0;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r15 = r14.substring(r5, r4);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r5 = "actionBarDefault";	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r5 = r15.equals(r5);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        if (r5 == 0) goto L_0x00d1;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x0098:
        r4 = r4 + 1;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r3 = r14.substring(r4);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r4 = r3.length();	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        if (r4 <= 0) goto L_0x00bb;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x00a4:
        r4 = 0;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r5 = r3.charAt(r4);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r8 = 35;
        if (r5 != r8) goto L_0x00bc;
    L_0x00ad:
        r5 = android.graphics.Color.parseColor(r3);	 Catch:{ Exception -> 0x00b2 }
        goto L_0x00c4;
    L_0x00b2:
        r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r5 = r3.intValue();	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        goto L_0x00c4;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x00bb:
        r4 = 0;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x00bc:
        r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r5 = r3.intValue();	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
    L_0x00c4:
        r3 = r1.paint;	 Catch:{ Throwable -> 0x00cc, all -> 0x0100 }
        r3.setColor(r5);	 Catch:{ Throwable -> 0x00cc, all -> 0x0100 }
        r3 = r10;
        r7 = 1;
        goto L_0x00dd;
    L_0x00cc:
        r0 = move-exception;
        r2 = r0;
        r3 = r6;
        r5 = 1;
        goto L_0x010f;
    L_0x00d1:
        r4 = 0;
        r11 = r11 + r13;
        r12 = r12 + r13;
        goto L_0x00d6;
    L_0x00d5:
        r4 = r5;
    L_0x00d6:
        r3 = r3 + 1;
        r5 = r4;
        r4 = 1;
        goto L_0x005e;
    L_0x00db:
        r4 = r5;
        r3 = r10;
    L_0x00dd:
        if (r2 == r12) goto L_0x00f4;
    L_0x00df:
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r3 < r2) goto L_0x00e4;
    L_0x00e3:
        goto L_0x00f4;
    L_0x00e4:
        r2 = r6.getChannel();	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r8 = (long) r12;	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        r2.position(r8);	 Catch:{ Throwable -> 0x0103, all -> 0x0100 }
        if (r7 == 0) goto L_0x00ef;
    L_0x00ee:
        goto L_0x00f4;
    L_0x00ef:
        r5 = r4;
        r2 = r12;
        r4 = 1;
        goto L_0x0051;
    L_0x00f4:
        r5 = r7;
        if (r6 == 0) goto L_0x0117;
    L_0x00f7:
        r6.close();	 Catch:{ Exception -> 0x00fb }
        goto L_0x0117;
    L_0x00fb:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
        goto L_0x0117;
    L_0x0100:
        r0 = move-exception;
        r2 = r0;
        goto L_0x0125;
    L_0x0103:
        r0 = move-exception;
        r2 = r0;
        r3 = r6;
        r5 = r7;
        goto L_0x010f;
    L_0x0108:
        r0 = move-exception;
        r2 = r0;
        r6 = r3;
        goto L_0x0125;
    L_0x010c:
        r0 = move-exception;
        r4 = r5;
    L_0x010e:
        r2 = r0;
    L_0x010f:
        org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ all -> 0x0108 }
        if (r3 == 0) goto L_0x0117;
    L_0x0114:
        r3.close();	 Catch:{ Exception -> 0x00fb }
    L_0x0117:
        if (r5 != 0) goto L_0x0124;
    L_0x0119:
        r2 = r1.paint;
        r3 = "actionBarDefault";
        r3 = org.telegram.ui.ActionBar.Theme.getDefaultColor(r3);
        r2.setColor(r3);
    L_0x0124:
        return;
    L_0x0125:
        if (r6 == 0) goto L_0x012f;
    L_0x0127:
        r6.close();	 Catch:{ Exception -> 0x012b }
        goto L_0x012f;
    L_0x012b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x012f:
        throw r2;
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

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
        int dp = AndroidUtilities.dp(27.0f);
        if (LocaleController.isRTL) {
            dp = getWidth() - dp;
        }
        canvas.drawCircle((float) dp, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(11.0f), this.paint);
    }
}
