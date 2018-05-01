package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class FeaturedStickerSetInfoCell extends FrameLayout {
    private TextView addButton;
    private Drawable addDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
    private int angle;
    private Paint botProgressPaint = new Paint(1);
    private int currentAccount = UserConfig.selectedAccount;
    private Drawable delDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_delButton), Theme.getColor(Theme.key_featuredStickers_delButtonPressed));
    private boolean drawProgress;
    Drawable drawable = new C08801();
    private boolean hasOnClick;
    private TextView infoTextView;
    private boolean isInstalled;
    private long lastUpdateTime;
    private TextView nameTextView;
    private float progressAlpha;
    private RectF rect = new RectF();
    private StickerSetCovered set;

    /* renamed from: org.telegram.ui.Cells.FeaturedStickerSetInfoCell$1 */
    class C08801 extends Drawable {
        Paint paint = new Paint(1);

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        C08801() {
        }

        public void draw(Canvas canvas) {
            this.paint.setColor(Theme.getColor(Theme.key_featuredStickers_unread));
            canvas.drawCircle((float) AndroidUtilities.dp(8.0f), 0.0f, (float) AndroidUtilities.dp(4.0f), this.paint);
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(12.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(26.0f);
        }
    }

    public FeaturedStickerSetInfoCell(Context context, int i) {
        super(context);
        this.botProgressPaint.setColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
        this.botProgressPaint.setStrokeCap(Cap.ROUND);
        this.botProgressPaint.setStyle(Style.STROKE);
        this.botProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelTrendingTitle));
        this.nameTextView.setTextSize(1, 17.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        int i2 = (float) i;
        addView(this.nameTextView, LayoutHelper.createFrame(-2, -1.0f, 51, i2, 8.0f, 100.0f, 0.0f));
        this.infoTextView = new TextView(context);
        this.infoTextView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelTrendingDescription));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(TruncateAt.END);
        this.infoTextView.setSingleLine(true);
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -1.0f, 51, i2, 30.0f, 100.0f, 0.0f));
        this.addButton = new TextView(context) {
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (FeaturedStickerSetInfoCell.this.drawProgress || !(FeaturedStickerSetInfoCell.this.drawProgress || FeaturedStickerSetInfoCell.this.progressAlpha == 0.0f)) {
                    FeaturedStickerSetInfoCell.this.botProgressPaint.setAlpha(Math.min(255, (int) (FeaturedStickerSetInfoCell.this.progressAlpha * 255.0f)));
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                    FeaturedStickerSetInfoCell.this.rect.set((float) measuredWidth, (float) AndroidUtilities.dp(3.0f), (float) (measuredWidth + AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(11.0f));
                    canvas.drawArc(FeaturedStickerSetInfoCell.this.rect, (float) FeaturedStickerSetInfoCell.this.angle, 220.0f, false, FeaturedStickerSetInfoCell.this.botProgressPaint);
                    invalidate(((int) FeaturedStickerSetInfoCell.this.rect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.bottom) + AndroidUtilities.dp(2.0f));
                    long currentTimeMillis = System.currentTimeMillis();
                    if (Math.abs(FeaturedStickerSetInfoCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                        long access$500 = currentTimeMillis - FeaturedStickerSetInfoCell.this.lastUpdateTime;
                        FeaturedStickerSetInfoCell.this.angle = (int) (((float) FeaturedStickerSetInfoCell.this.angle) + (((float) (360 * access$500)) / 2000.0f));
                        FeaturedStickerSetInfoCell.this.angle = FeaturedStickerSetInfoCell.this.angle - (360 * (FeaturedStickerSetInfoCell.this.angle / 360));
                        if (FeaturedStickerSetInfoCell.this.drawProgress != null) {
                            if (FeaturedStickerSetInfoCell.this.progressAlpha < NUM) {
                                FeaturedStickerSetInfoCell.this.progressAlpha = FeaturedStickerSetInfoCell.this.progressAlpha + (((float) access$500) / 200.0f);
                                if (FeaturedStickerSetInfoCell.this.progressAlpha > NUM) {
                                    FeaturedStickerSetInfoCell.this.progressAlpha = 1.0f;
                                }
                            }
                        } else if (FeaturedStickerSetInfoCell.this.progressAlpha > null) {
                            FeaturedStickerSetInfoCell.this.progressAlpha = FeaturedStickerSetInfoCell.this.progressAlpha - (((float) access$500) / 200.0f);
                            if (FeaturedStickerSetInfoCell.this.progressAlpha < null) {
                                FeaturedStickerSetInfoCell.this.progressAlpha = 0.0f;
                            }
                        }
                    }
                    FeaturedStickerSetInfoCell.this.lastUpdateTime = currentTimeMillis;
                    invalidate();
                }
            }
        };
        this.addButton.setGravity(17);
        this.addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.addButton.setTextSize(1, NUM);
        this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
    }

    public void setAddOnClickListener(OnClickListener onClickListener) {
        this.hasOnClick = true;
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setStickerSet(StickerSetCovered stickerSetCovered, boolean z) {
        setStickerSet(stickerSetCovered, z, 0, 0);
    }

    public void setStickerSet(org.telegram.tgnet.TLRPC.StickerSetCovered r4, boolean r5, int r6, int r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r3 = this;
        r0 = java.lang.System.currentTimeMillis();
        r3.lastUpdateTime = r0;
        if (r7 == 0) goto L_0x0028;
    L_0x0008:
        r0 = new android.text.SpannableStringBuilder;
        r1 = r4.set;
        r1 = r1.title;
        r0.<init>(r1);
        r1 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x0022 }
        r2 = "windowBackgroundWhiteBlueText4";	 Catch:{ Exception -> 0x0022 }
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);	 Catch:{ Exception -> 0x0022 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0022 }
        r7 = r7 + r6;	 Catch:{ Exception -> 0x0022 }
        r2 = 33;	 Catch:{ Exception -> 0x0022 }
        r0.setSpan(r1, r6, r7, r2);	 Catch:{ Exception -> 0x0022 }
    L_0x0022:
        r6 = r3.nameTextView;
        r6.setText(r0);
        goto L_0x0031;
    L_0x0028:
        r6 = r3.nameTextView;
        r7 = r4.set;
        r7 = r7.title;
        r6.setText(r7);
    L_0x0031:
        r6 = r3.infoTextView;
        r7 = "Stickers";
        r0 = r4.set;
        r0 = r0.count;
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r0);
        r6.setText(r7);
        r6 = 0;
        if (r5 == 0) goto L_0x004b;
    L_0x0043:
        r5 = r3.nameTextView;
        r7 = r3.drawable;
        r5.setCompoundDrawablesWithIntrinsicBounds(r6, r6, r7, r6);
        goto L_0x0050;
    L_0x004b:
        r5 = r3.nameTextView;
        r5.setCompoundDrawablesWithIntrinsicBounds(r6, r6, r6, r6);
    L_0x0050:
        r5 = r3.hasOnClick;
        if (r5 == 0) goto L_0x00af;
    L_0x0054:
        r5 = r3.addButton;
        r6 = 0;
        r5.setVisibility(r6);
        r5 = r3.currentAccount;
        r5 = org.telegram.messenger.DataQuery.getInstance(r5);
        r7 = r4.set;
        r0 = r7.id;
        r5 = r5.isStickerPackInstalled(r0);
        r3.isInstalled = r5;
        if (r5 == 0) goto L_0x0086;
    L_0x006c:
        r5 = r3.addButton;
        r7 = r3.delDrawable;
        r5.setBackgroundDrawable(r7);
        r5 = r3.addButton;
        r7 = "StickersRemove";
        r0 = NUM; // 0x7f0c061b float:1.8612362E38 double:1.0530981707E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r0);
        r7 = r7.toUpperCase();
        r5.setText(r7);
        goto L_0x009f;
    L_0x0086:
        r5 = r3.addButton;
        r7 = r3.addDrawable;
        r5.setBackgroundDrawable(r7);
        r5 = r3.addButton;
        r7 = "Add";
        r0 = NUM; // 0x7f0c0038 float:1.8609306E38 double:1.053097426E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r0);
        r7 = r7.toUpperCase();
        r5.setText(r7);
    L_0x009f:
        r5 = r3.addButton;
        r7 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r5.setPadding(r0, r6, r7, r6);
        goto L_0x00b6;
    L_0x00af:
        r5 = r3.addButton;
        r6 = 8;
        r5.setVisibility(r6);
    L_0x00b6:
        r3.set = r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.FeaturedStickerSetInfoCell.setStickerSet(org.telegram.tgnet.TLRPC$StickerSetCovered, boolean, int, int):void");
    }

    public void setUrl(java.lang.CharSequence r5, int r6) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r4 = this;
        if (r5 == 0) goto L_0x002f;
    L_0x0002:
        r0 = new android.text.SpannableStringBuilder;
        r0.<init>(r5);
        r1 = new org.telegram.ui.Components.ColorSpanUnderline;	 Catch:{ Exception -> 0x002a }
        r2 = "windowBackgroundWhiteBlueText4";	 Catch:{ Exception -> 0x002a }
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);	 Catch:{ Exception -> 0x002a }
        r1.<init>(r2);	 Catch:{ Exception -> 0x002a }
        r2 = 0;	 Catch:{ Exception -> 0x002a }
        r3 = 33;	 Catch:{ Exception -> 0x002a }
        r0.setSpan(r1, r2, r6, r3);	 Catch:{ Exception -> 0x002a }
        r1 = new org.telegram.ui.Components.ColorSpanUnderline;	 Catch:{ Exception -> 0x002a }
        r2 = "chat_emojiPanelTrendingDescription";	 Catch:{ Exception -> 0x002a }
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);	 Catch:{ Exception -> 0x002a }
        r1.<init>(r2);	 Catch:{ Exception -> 0x002a }
        r5 = r5.length();	 Catch:{ Exception -> 0x002a }
        r0.setSpan(r1, r6, r5, r3);	 Catch:{ Exception -> 0x002a }
    L_0x002a:
        r5 = r4.infoTextView;
        r5.setText(r0);
    L_0x002f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.FeaturedStickerSetInfoCell.setUrl(java.lang.CharSequence, int):void");
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    public void setDrawProgress(boolean z) {
        this.drawProgress = z;
        this.lastUpdateTime = System.currentTimeMillis();
        this.addButton.invalidate();
    }

    public StickerSetCovered getStickerSet() {
        return this.set;
    }
}
