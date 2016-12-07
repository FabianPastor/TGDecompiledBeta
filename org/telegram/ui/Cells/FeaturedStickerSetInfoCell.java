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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class FeaturedStickerSetInfoCell extends FrameLayout {
    private static Paint botProgressPaint;
    private TextView addButton;
    private int angle;
    private boolean drawProgress;
    Drawable drawable = new Drawable() {
        Paint paint = new Paint(1);

        public void draw(Canvas canvas) {
            this.paint.setColor(-11688214);
            canvas.drawCircle((float) AndroidUtilities.dp(8.0f), 0.0f, (float) AndroidUtilities.dp(4.0f), this.paint);
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return 0;
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(12.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(26.0f);
        }
    };
    private boolean hasOnClick;
    private TextView infoTextView;
    private boolean isInstalled;
    private long lastUpdateTime;
    private TextView nameTextView;
    private float progressAlpha;
    private RectF rect = new RectF();
    private StickerSetCovered set;

    public FeaturedStickerSetInfoCell(Context context, int left) {
        super(context);
        if (botProgressPaint == null) {
            botProgressPaint = new Paint(1);
            botProgressPaint.setColor(-1);
            botProgressPaint.setStrokeCap(Cap.ROUND);
            botProgressPaint.setStyle(Style.STROKE);
        }
        botProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.nameTextView.setTextSize(1, 17.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        addView(this.nameTextView, LayoutHelper.createFrame(-2, -1.0f, 51, (float) left, 8.0f, 100.0f, 0.0f));
        this.infoTextView = new TextView(context);
        this.infoTextView.setTextColor(-7697782);
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(TruncateAt.END);
        this.infoTextView.setSingleLine(true);
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -1.0f, 51, (float) left, BitmapDescriptorFactory.HUE_ORANGE, 100.0f, 0.0f));
        this.addButton = new TextView(context) {
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (FeaturedStickerSetInfoCell.this.drawProgress || !(FeaturedStickerSetInfoCell.this.drawProgress || FeaturedStickerSetInfoCell.this.progressAlpha == 0.0f)) {
                    FeaturedStickerSetInfoCell.botProgressPaint.setAlpha(Math.min(255, (int) (FeaturedStickerSetInfoCell.this.progressAlpha * 255.0f)));
                    int x = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                    FeaturedStickerSetInfoCell.this.rect.set((float) x, (float) AndroidUtilities.dp(3.0f), (float) (AndroidUtilities.dp(8.0f) + x), (float) AndroidUtilities.dp(11.0f));
                    canvas.drawArc(FeaturedStickerSetInfoCell.this.rect, (float) FeaturedStickerSetInfoCell.this.angle, 220.0f, false, FeaturedStickerSetInfoCell.botProgressPaint);
                    invalidate(((int) FeaturedStickerSetInfoCell.this.rect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.bottom) + AndroidUtilities.dp(2.0f));
                    long newTime = System.currentTimeMillis();
                    if (Math.abs(FeaturedStickerSetInfoCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                        long delta = newTime - FeaturedStickerSetInfoCell.this.lastUpdateTime;
                        FeaturedStickerSetInfoCell.this.angle = (int) (((float) FeaturedStickerSetInfoCell.this.angle) + (((float) (360 * delta)) / 2000.0f));
                        FeaturedStickerSetInfoCell.this.angle = FeaturedStickerSetInfoCell.this.angle - ((FeaturedStickerSetInfoCell.this.angle / 360) * 360);
                        if (FeaturedStickerSetInfoCell.this.drawProgress) {
                            if (FeaturedStickerSetInfoCell.this.progressAlpha < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                FeaturedStickerSetInfoCell.this.progressAlpha = FeaturedStickerSetInfoCell.this.progressAlpha + (((float) delta) / 200.0f);
                                if (FeaturedStickerSetInfoCell.this.progressAlpha > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                    FeaturedStickerSetInfoCell.this.progressAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                                }
                            }
                        } else if (FeaturedStickerSetInfoCell.this.progressAlpha > 0.0f) {
                            FeaturedStickerSetInfoCell.this.progressAlpha = FeaturedStickerSetInfoCell.this.progressAlpha - (((float) delta) / 200.0f);
                            if (FeaturedStickerSetInfoCell.this.progressAlpha < 0.0f) {
                                FeaturedStickerSetInfoCell.this.progressAlpha = 0.0f;
                            }
                        }
                    }
                    FeaturedStickerSetInfoCell.this.lastUpdateTime = newTime;
                    invalidate();
                }
            }
        };
        this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        this.addButton.setGravity(17);
        this.addButton.setTextColor(-1);
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW), NUM));
    }

    public void setAddOnClickListener(OnClickListener onClickListener) {
        this.hasOnClick = true;
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setStickerSet(StickerSetCovered stickerSet, boolean unread) {
        this.lastUpdateTime = System.currentTimeMillis();
        this.nameTextView.setText(stickerSet.set.title);
        this.infoTextView.setText(LocaleController.formatPluralString("Stickers", stickerSet.set.count));
        if (unread) {
            this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, this.drawable, null);
        } else {
            this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        if (this.hasOnClick) {
            this.addButton.setVisibility(0);
            boolean isStickerPackInstalled = StickersQuery.isStickerPackInstalled(stickerSet.set.id);
            this.isInstalled = isStickerPackInstalled;
            if (isStickerPackInstalled) {
                this.addButton.setBackgroundResource(R.drawable.del_states);
                this.addButton.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove).toUpperCase());
            } else {
                this.addButton.setBackgroundResource(R.drawable.add_states);
                this.addButton.setText(LocaleController.getString("Add", R.string.Add).toUpperCase());
            }
        } else {
            this.addButton.setVisibility(8);
        }
        this.set = stickerSet;
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    public void setDrawProgress(boolean value) {
        this.drawProgress = value;
        this.lastUpdateTime = System.currentTimeMillis();
        this.addButton.invalidate();
    }

    public StickerSetCovered getStickerSet() {
        return this.set;
    }
}
