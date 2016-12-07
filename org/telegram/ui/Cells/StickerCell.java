package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLRPC.Document;
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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((AndroidUtilities.dp(76.0f) + getPaddingLeft()) + getPaddingRight(), C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f), C.ENCODING_PCM_32BIT));
    }

    public void setPressed(boolean pressed) {
        if (this.imageView.getImageReceiver().getPressed() != pressed) {
            this.imageView.getImageReceiver().setPressed(pressed);
            this.imageView.invalidate();
        }
        super.setPressed(pressed);
    }

    public void setSticker(Document document, int side) {
        if (!(document == null || document.thumb == null)) {
            this.imageView.setImage(document.thumb.location, null, "webp", null);
        }
        this.sticker = document;
        if (side == -1) {
            setBackgroundResource(R.drawable.stickers_back_left);
            setPadding(AndroidUtilities.dp(7.0f), 0, 0, 0);
        } else if (side == 0) {
            setBackgroundResource(R.drawable.stickers_back_center);
            setPadding(0, 0, 0, 0);
        } else if (side == 1) {
            setBackgroundResource(R.drawable.stickers_back_right);
            setPadding(0, 0, AndroidUtilities.dp(7.0f), 0);
        } else if (side == 2) {
            setBackgroundResource(R.drawable.stickers_back_all);
            setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        }
        if (getBackground() != null) {
            getBackground().setAlpha(230);
        }
    }

    public Document getSticker() {
        return this.sticker;
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && ((this.scaled && this.scale != DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) || !(this.scaled || this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (!this.scaled || this.scale == DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) {
                this.scale += ((float) dt) / 400.0f;
                if (this.scale > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
            } else {
                this.scale -= ((float) dt) / 400.0f;
                if (this.scale < DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) {
                    this.scale = DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return result;
    }
}
