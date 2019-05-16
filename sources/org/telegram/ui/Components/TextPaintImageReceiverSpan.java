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
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC.Document;

public class TextPaintImageReceiverSpan extends ReplacementSpan {
    private boolean alignTop;
    private int height;
    private ImageReceiver imageReceiver;
    private int width;

    public TextPaintImageReceiverSpan(View view, Document document, Object obj, int i, int i2, boolean z, boolean z2) {
        Document document2 = document;
        String format = String.format(Locale.US, "%d_%d_i", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        this.width = i;
        this.height = i2;
        this.imageReceiver = new ImageReceiver(view);
        this.imageReceiver.setInvalidateAll(true);
        if (z2) {
            this.imageReceiver.setDelegate(-$$Lambda$TextPaintImageReceiverSpan$Cb0mzcqNIfBx1iovVDp8PZkH5ug.INSTANCE);
        }
        String str = format;
        this.imageReceiver.setImage(ImageLocation.getForDocument(document), str, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90), document2), format, -1, null, obj, 1);
        this.alignTop = z;
    }

    static /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2) {
        if (imageReceiver.canInvertBitmap()) {
            imageReceiver.setColorFilter(new ColorMatrixColorFilter(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, -1.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
        if (fontMetricsInt != null) {
            int dp;
            if (this.alignTop) {
                dp = (fontMetricsInt.descent - fontMetricsInt.ascent) - AndroidUtilities.dp(4.0f);
                int i3 = this.height - dp;
                fontMetricsInt.descent = i3;
                fontMetricsInt.bottom = i3;
                dp = 0 - dp;
                fontMetricsInt.ascent = dp;
                fontMetricsInt.top = dp;
            } else {
                dp = ((-this.height) / 2) - AndroidUtilities.dp(4.0f);
                fontMetricsInt.ascent = dp;
                fontMetricsInt.top = dp;
                dp = this.height;
                dp = (dp - (dp / 2)) - AndroidUtilities.dp(4.0f);
                fontMetricsInt.descent = dp;
                fontMetricsInt.bottom = dp;
            }
        }
        return this.width;
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        canvas.save();
        if (this.alignTop) {
            this.imageReceiver.setImageCoords((int) f, i3 - 1, this.width, this.height);
        } else {
            i5 = (i5 - AndroidUtilities.dp(4.0f)) - i3;
            ImageReceiver imageReceiver = this.imageReceiver;
            i = (int) f;
            i2 = this.height;
            imageReceiver.setImageCoords(i, i3 + ((i5 - i2) / 2), this.width, i2);
        }
        this.imageReceiver.draw(canvas);
        canvas.restore();
    }
}
