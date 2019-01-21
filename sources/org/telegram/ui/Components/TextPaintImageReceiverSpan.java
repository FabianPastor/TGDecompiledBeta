package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.text.style.ReplacementSpan;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC.Document;

public class TextPaintImageReceiverSpan extends ReplacementSpan {
    private boolean alignTop;
    private int height;
    private ImageReceiver imageReceiver;
    private int width;

    public TextPaintImageReceiverSpan(View parentView, Document document, Object parentObject, int w, int h, boolean top, boolean invert) {
        String filter = String.format(Locale.US, "%d_%d_i", new Object[]{Integer.valueOf(w), Integer.valueOf(h)});
        this.width = w;
        this.height = h;
        this.imageReceiver = new ImageReceiver(parentView);
        this.imageReceiver.setInvalidateAll(true);
        if (invert) {
            this.imageReceiver.setDelegate(TextPaintImageReceiverSpan$$Lambda$0.$instance);
        }
        this.imageReceiver.setImage(document, filter, FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), filter, -1, null, parentObject, 1);
        this.alignTop = top;
    }

    static final /* synthetic */ void lambda$new$0$TextPaintImageReceiverSpan(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        if (imageReceiver.canInvertBitmap()) {
            imageReceiver.setColorFilter(new ColorMatrixColorFilter(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, -1.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        if (fm != null) {
            int i;
            if (this.alignTop) {
                int h = (fm.descent - fm.ascent) - AndroidUtilities.dp(4.0f);
                i = this.height - h;
                fm.descent = i;
                fm.bottom = i;
                i = 0 - h;
                fm.ascent = i;
                fm.top = i;
            } else {
                i = ((-this.height) / 2) - AndroidUtilities.dp(4.0f);
                fm.ascent = i;
                fm.top = i;
                i = (this.height - (this.height / 2)) - AndroidUtilities.dp(4.0f);
                fm.descent = i;
                fm.bottom = i;
            }
        }
        return this.width;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        canvas.save();
        if (this.alignTop) {
            this.imageReceiver.setImageCoords((int) x, top - 1, this.width, this.height);
        } else {
            this.imageReceiver.setImageCoords((int) x, ((((bottom - AndroidUtilities.dp(4.0f)) - top) - this.height) / 2) + top, this.width, this.height);
        }
        this.imageReceiver.draw(canvas);
        canvas.restore();
    }
}
