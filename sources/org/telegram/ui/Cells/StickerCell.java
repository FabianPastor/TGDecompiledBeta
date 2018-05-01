package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerCell extends FrameLayout {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private BackupImageView imageView;
    private long lastUpdateTime;
    private float scale;
    private boolean scaled;
    private Document sticker;
    private long time = 0;

    public StickerCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        addView(this.imageView, LayoutHelper.createFrame(66, 66.0f, 1, 0.0f, 5.0f, 0.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((AndroidUtilities.dp(NUM) + getPaddingLeft()) + getPaddingRight(), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f), NUM));
    }

    public void setPressed(boolean z) {
        if (this.imageView.getImageReceiver().getPressed() != z) {
            this.imageView.getImageReceiver().setPressed(z);
            this.imageView.invalidate();
        }
        super.setPressed(z);
    }

    public void setSticker(Document document, int i) {
        if (!(document == null || document.thumb == null)) {
            this.imageView.setImage(document.thumb.location, null, "webp", null);
        }
        this.sticker = document;
        if (i == -1) {
            setBackgroundResource(C0446R.drawable.stickers_back_left);
            setPadding(AndroidUtilities.dp(7.0f), 0, 0, 0);
        } else if (i == 0) {
            setBackgroundResource(C0446R.drawable.stickers_back_center);
            setPadding(0, 0, 0, 0);
        } else if (i == 1) {
            setBackgroundResource(C0446R.drawable.stickers_back_right);
            setPadding(0, 0, AndroidUtilities.dp(7.0f), 0);
        } else if (i == 2) {
            setBackgroundResource(C0446R.drawable.stickers_back_all);
            setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        }
        document = getBackground();
        if (document != null) {
            document.setAlpha(230);
            document.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_stickersHintPanel), Mode.MULTIPLY));
        }
    }

    public Document getSticker() {
        return this.sticker;
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        canvas = super.drawChild(canvas, view, j);
        if (view == this.imageView && (!(this.scaled == null || this.scale == NUM) || (this.scaled == null && this.scale != NUM))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.scaled == null || this.scale == NUM) {
                this.scale += ((float) j2) / 400.0f;
                if (this.scale > NUM) {
                    this.scale = 1.0f;
                }
            } else {
                this.scale -= ((float) j2) / NUM;
                if (this.scale < NUM) {
                    this.scale = 0.8f;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return canvas;
    }
}
