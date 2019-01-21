package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
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

    public ThemeCell(Context context, boolean nightTheme) {
        int i;
        int i2 = 3;
        super(context);
        setWillNotDraw(false);
        this.isNightTheme = nightTheme;
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, LocaleController.isRTL ? 105.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 105.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
        this.checkImage.setImageResource(R.drawable.sticker_added);
        if (this.isNightTheme) {
            view = this.checkImage;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(view, LayoutHelper.createFrame(19, 14.0f, i2 | 16, 21.0f, 0.0f, 21.0f, 0.0f));
            return;
        }
        view = this.checkImage;
        if (LocaleController.isRTL) {
            i = 3;
        } else {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(19, 14.0f, i | 16, 59.0f, 0.0f, 59.0f, 0.0f));
        this.optionsButton = new ImageView(context);
        this.optionsButton.setFocusable(false);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
        this.optionsButton.setImageResource(R.drawable.ic_ab_other);
        this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), Mode.MULTIPLY));
        this.optionsButton.setScaleType(ScaleType.CENTER);
        View view2 = this.optionsButton;
        if (!LocaleController.isRTL) {
            i2 = 5;
        }
        addView(view2, LayoutHelper.createFrame(48, 48, i2 | 48));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(50.0f), NUM));
    }

    public void setOnOptionsClick(OnClickListener listener) {
        this.optionsButton.setOnClickListener(listener);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public ThemeInfo getCurrentThemeInfo() {
        return this.currentThemeInfo;
    }

    /* JADX WARNING: Removed duplicated region for block: B:92:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ce  */
    public void setTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r27, boolean r28) {
        /*
        r26 = this;
        r0 = r27;
        r1 = r26;
        r1.currentThemeInfo = r0;
        r21 = r27.getName();
        r23 = ".attheme";
        r0 = r21;
        r1 = r23;
        r23 = r0.endsWith(r1);
        if (r23 == 0) goto L_0x002d;
    L_0x0017:
        r23 = 0;
        r24 = 46;
        r0 = r21;
        r1 = r24;
        r24 = r0.lastIndexOf(r1);
        r0 = r21;
        r1 = r23;
        r2 = r24;
        r21 = r0.substring(r1, r2);
    L_0x002d:
        r0 = r26;
        r0 = r0.textView;
        r23 = r0;
        r0 = r23;
        r1 = r21;
        r0.setText(r1);
        r0 = r28;
        r1 = r26;
        r1.needDivider = r0;
        r26.updateCurrentThemeCheck();
        r8 = 0;
        r0 = r27;
        r0 = r0.pathToFile;
        r23 = r0;
        if (r23 != 0) goto L_0x0054;
    L_0x004c:
        r0 = r27;
        r0 = r0.assetName;
        r23 = r0;
        if (r23 == 0) goto L_0x00cc;
    L_0x0054:
        r19 = 0;
        r5 = 0;
        r0 = r27;
        r0 = r0.assetName;	 Catch:{ Throwable -> 0x0191 }
        r23 = r0;
        if (r23 == 0) goto L_0x00df;
    L_0x005f:
        r0 = r27;
        r0 = r0.assetName;	 Catch:{ Throwable -> 0x0191 }
        r23 = r0;
        r7 = org.telegram.ui.ActionBar.Theme.getAssetFile(r23);	 Catch:{ Throwable -> 0x0191 }
    L_0x0069:
        r20 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0191 }
        r0 = r20;
        r0.<init>(r7);	 Catch:{ Throwable -> 0x0191 }
        r14 = 0;
    L_0x0071:
        r23 = bytes;	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r0 = r20;
        r1 = r23;
        r17 = r0.read(r1);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r23 = -1;
        r0 = r17;
        r1 = r23;
        if (r0 == r1) goto L_0x00c7;
    L_0x0083:
        r16 = r5;
        r18 = 0;
        r4 = 0;
    L_0x0088:
        r0 = r17;
        if (r4 >= r0) goto L_0x00bd;
    L_0x008c:
        r23 = bytes;	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r23 = r23[r4];	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r24 = 10;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x0169;
    L_0x0098:
        r14 = r14 + 1;
        r23 = r4 - r18;
        r12 = r23 + 1;
        r13 = new java.lang.String;	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r23 = bytes;	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r24 = r12 + -1;
        r25 = "UTF-8";
        r0 = r23;
        r1 = r18;
        r2 = r24;
        r3 = r25;
        r13.<init>(r0, r1, r2, r3);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r23 = "WPS";
        r0 = r23;
        r23 = r13.startsWith(r0);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        if (r23 == 0) goto L_0x00ee;
    L_0x00bd:
        r0 = r16;
        if (r0 == r5) goto L_0x00c7;
    L_0x00c1:
        r23 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r0 = r23;
        if (r14 < r0) goto L_0x016d;
    L_0x00c7:
        if (r20 == 0) goto L_0x00cc;
    L_0x00c9:
        r20.close();	 Catch:{ Exception -> 0x017b }
    L_0x00cc:
        if (r8 != 0) goto L_0x00de;
    L_0x00ce:
        r0 = r26;
        r0 = r0.paint;
        r23 = r0;
        r24 = "actionBarDefault";
        r24 = org.telegram.ui.ActionBar.Theme.getDefaultColor(r24);
        r23.setColor(r24);
    L_0x00de:
        return;
    L_0x00df:
        r7 = new java.io.File;	 Catch:{ Throwable -> 0x0191 }
        r0 = r27;
        r0 = r0.pathToFile;	 Catch:{ Throwable -> 0x0191 }
        r23 = r0;
        r0 = r23;
        r7.<init>(r0);	 Catch:{ Throwable -> 0x0191 }
        goto L_0x0069;
    L_0x00ee:
        r23 = 61;
        r0 = r23;
        r9 = r13.indexOf(r0);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r23 = -1;
        r0 = r23;
        if (r9 == r0) goto L_0x0166;
    L_0x00fc:
        r23 = 0;
        r0 = r23;
        r11 = r13.substring(r0, r9);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r23 = "actionBarDefault";
        r0 = r23;
        r23 = r11.equals(r0);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        if (r23 == 0) goto L_0x0166;
    L_0x010f:
        r23 = r9 + 1;
        r0 = r23;
        r15 = r13.substring(r0);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r23 = r15.length();	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        if (r23 <= 0) goto L_0x015d;
    L_0x011d:
        r23 = 0;
        r0 = r23;
        r23 = r15.charAt(r0);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r24 = 35;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x015d;
    L_0x012d:
        r22 = android.graphics.Color.parseColor(r15);	 Catch:{ Exception -> 0x0153 }
    L_0x0131:
        r8 = 1;
        r0 = r26;
        r0 = r0.paint;	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r23 = r0;
        r0 = r23;
        r1 = r22;
        r0.setColor(r1);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        goto L_0x00bd;
    L_0x0141:
        r6 = move-exception;
        r19 = r20;
    L_0x0144:
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ all -> 0x0181 }
        if (r19 == 0) goto L_0x00cc;
    L_0x0149:
        r19.close();	 Catch:{ Exception -> 0x014d }
        goto L_0x00cc;
    L_0x014d:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
        goto L_0x00cc;
    L_0x0153:
        r10 = move-exception;
        r23 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r22 = r23.intValue();	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        goto L_0x0131;
    L_0x015d:
        r23 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r22 = r23.intValue();	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        goto L_0x0131;
    L_0x0166:
        r18 = r18 + r12;
        r5 = r5 + r12;
    L_0x0169:
        r4 = r4 + 1;
        goto L_0x0088;
    L_0x016d:
        r23 = r20.getChannel();	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r0 = (long) r5;	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        r24 = r0;
        r23.position(r24);	 Catch:{ Throwable -> 0x0141, all -> 0x018d }
        if (r8 == 0) goto L_0x0071;
    L_0x0179:
        goto L_0x00c7;
    L_0x017b:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
        goto L_0x00cc;
    L_0x0181:
        r23 = move-exception;
    L_0x0182:
        if (r19 == 0) goto L_0x0187;
    L_0x0184:
        r19.close();	 Catch:{ Exception -> 0x0188 }
    L_0x0187:
        throw r23;
    L_0x0188:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
        goto L_0x0187;
    L_0x018d:
        r23 = move-exception;
        r19 = r20;
        goto L_0x0182;
    L_0x0191:
        r6 = move-exception;
        goto L_0x0144;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemeCell.setTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

    public void updateCurrentThemeCheck() {
        ThemeInfo currentTheme;
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

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        int x = AndroidUtilities.dp(31.0f);
        if (LocaleController.isRTL) {
            x = getWidth() - x;
        }
        canvas.drawCircle((float) x, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(11.0f), this.paint);
    }
}
