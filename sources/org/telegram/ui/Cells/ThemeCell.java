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

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:87:0x0121 in {2, 6, 11, 12, 24, 36, 38, 39, 40, 43, 45, 46, 47, 48, 52, 56, 57, 61, 63, 65, 67, 69, 71, 76, 78, 79, 83, 85, 86} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
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
        r3 = 46;
        r3 = r2.lastIndexOf(r3);
        r2 = r2.substring(r4, r3);
        r3 = r1.textView;
        r3.setText(r2);
        r2 = r18;
        r1.needDivider = r2;
        r16.updateCurrentThemeCheck();
        r2 = r0.pathToFile;
        r3 = "actionBarDefault";
        if (r2 != 0) goto L_0x0033;
        r2 = r0.assetName;
        if (r2 == 0) goto L_0x0109;
        r2 = 0;
        r5 = 1;
        r6 = r0.assetName;	 Catch:{ Throwable -> 0x0100 }
        if (r6 == 0) goto L_0x0040;	 Catch:{ Throwable -> 0x0100 }
        r0 = r0.assetName;	 Catch:{ Throwable -> 0x0100 }
        r0 = org.telegram.ui.ActionBar.Theme.getAssetFile(r0);	 Catch:{ Throwable -> 0x0100 }
        goto L_0x0048;	 Catch:{ Throwable -> 0x0100 }
        r6 = new java.io.File;	 Catch:{ Throwable -> 0x0100 }
        r0 = r0.pathToFile;	 Catch:{ Throwable -> 0x0100 }
        r6.<init>(r0);	 Catch:{ Throwable -> 0x0100 }
        r0 = r6;	 Catch:{ Throwable -> 0x0100 }
        r6 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0100 }
        r6.<init>(r0);	 Catch:{ Throwable -> 0x0100 }
        r0 = 0;
        r2 = 0;
        r7 = 0;
        r8 = bytes;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r8 = r6.read(r8);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r9 = -1;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r8 == r9) goto L_0x00eb;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r12 = r0;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r10 = r2;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r2 = 0;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r11 = 0;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r2 >= r8) goto L_0x00d4;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r13 = bytes;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r13 = r13[r2];	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r14 = 10;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r13 != r14) goto L_0x00d0;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r10 = r10 + 1;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r13 = r2 - r11;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r13 = r13 + r5;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r14 = new java.lang.String;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r15 = bytes;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r5 = r13 + -1;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = "UTF-8";	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r14.<init>(r15, r11, r5, r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = "WPS";	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = r14.startsWith(r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r4 == 0) goto L_0x0082;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r2 = r10;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = 0;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        goto L_0x00d5;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = 61;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = r14.indexOf(r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r4 == r9) goto L_0x00cd;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r5 = 0;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r15 = r14.substring(r5, r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r5 = r15.equals(r3);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r5 == 0) goto L_0x00cd;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = r4 + 1;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r2 = r14.substring(r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = r2.length();	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r4 <= 0) goto L_0x00b8;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = 0;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r5 = r2.charAt(r4);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r8 = 35;
        if (r5 != r8) goto L_0x00b9;
        r2 = android.graphics.Color.parseColor(r2);	 Catch:{ Exception -> 0x00af }
        goto L_0x00c1;
    L_0x00af:
        r2 = org.telegram.messenger.Utilities.parseInt(r2);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r2 = r2.intValue();	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        goto L_0x00c1;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r4 = 0;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r2 = org.telegram.messenger.Utilities.parseInt(r2);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r2 = r2.intValue();	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r5 = r1.paint;	 Catch:{ Throwable -> 0x00c9, all -> 0x00f6 }
        r5.setColor(r2);	 Catch:{ Throwable -> 0x00c9, all -> 0x00f6 }
        r2 = r10;
        r7 = 1;
        goto L_0x00d5;
        r0 = move-exception;
        r2 = r6;
        r4 = 1;
        goto L_0x0101;
        r4 = 0;
        r11 = r11 + r13;
        r12 = r12 + r13;
        r2 = r2 + 1;
        r5 = 1;
        goto L_0x005d;
        r2 = r10;
        if (r0 == r12) goto L_0x00eb;
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r2 < r0) goto L_0x00dc;
        goto L_0x00eb;
        r0 = r6.getChannel();	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r8 = (long) r12;	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        r0.position(r8);	 Catch:{ Throwable -> 0x00f8, all -> 0x00f6 }
        if (r7 == 0) goto L_0x00e7;
        goto L_0x00eb;
        r0 = r12;
        r5 = 1;
        goto L_0x0050;
        r4 = r7;
        r6.close();	 Catch:{ Exception -> 0x00f0 }
        goto L_0x0109;
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
        goto L_0x0109;
        r0 = move-exception;
        goto L_0x00fe;
        r0 = move-exception;
        r2 = r6;
        r4 = r7;
        goto L_0x0101;
        r0 = move-exception;
        r6 = r2;
        r2 = r0;
        goto L_0x0115;
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x00fc }
        if (r2 == 0) goto L_0x0109;
        r2.close();	 Catch:{ Exception -> 0x00f0 }
        if (r4 != 0) goto L_0x0114;
        r0 = r1.paint;
        r2 = org.telegram.ui.ActionBar.Theme.getDefaultColor(r3);
        r0.setColor(r2);
        return;
        if (r6 == 0) goto L_0x0120;
        r6.close();	 Catch:{ Exception -> 0x011b }
        goto L_0x0120;
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemeCell.setTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

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
