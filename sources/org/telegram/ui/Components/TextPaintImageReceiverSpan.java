package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.text.style.ReplacementSpan;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;

public class TextPaintImageReceiverSpan extends ReplacementSpan {
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_BOTTOM = 0;
    private int height;
    private ImageReceiver imageReceiver;
    protected final int mVerticalAlignment = 1;
    private int width;

    public TextPaintImageReceiverSpan(View parentView, Document document, int w, int h) {
        FileLocation fileLocation;
        String filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(w), Integer.valueOf(h)});
        this.width = AndroidUtilities.dp((float) w);
        this.height = AndroidUtilities.dp((float) h);
        this.imageReceiver = new ImageReceiver(parentView);
        ImageReceiver imageReceiver = this.imageReceiver;
        if (document.thumb != null) {
            fileLocation = document.thumb.location;
        } else {
            fileLocation = null;
        }
        imageReceiver.setImage(document, filter, fileLocation, filter, -1, null, 1);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        if (fm != null) {
            fm.ascent = -this.height;
            fm.descent = 0;
            fm.top = fm.ascent;
            fm.bottom = 0;
        }
        return this.width;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        canvas.save();
        int transY = bottom - this.height;
        if (this.mVerticalAlignment == 1) {
            transY -= paint.getFontMetricsInt().descent;
        }
        this.imageReceiver.setImageCoords((int) x, transY, this.width, this.height);
        this.imageReceiver.draw(canvas);
        canvas.restore();
    }
}
