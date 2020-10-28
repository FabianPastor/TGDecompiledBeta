package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC$Document;

public class TextPaintImageReceiverSpan extends ReplacementSpan {
    private boolean alignTop;
    private int height;
    private ImageReceiver imageReceiver;
    private int width;

    public TextPaintImageReceiverSpan(View view, TLRPC$Document tLRPC$Document, Object obj, int i, int i2, boolean z, boolean z2) {
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        String format = String.format(Locale.US, "%d_%d_i", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        this.width = i;
        this.height = i2;
        ImageReceiver imageReceiver2 = new ImageReceiver(view);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setInvalidateAll(true);
        if (z2) {
            this.imageReceiver.setDelegate($$Lambda$TextPaintImageReceiverSpan$VOfUEMxfp5Biab0XljN117MV0.INSTANCE);
        }
        String str = format;
        this.imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), str, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90), tLRPC$Document2), format, -1, (String) null, obj, 1);
        this.alignTop = z;
    }

    static /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
        if (imageReceiver2.canInvertBitmap()) {
            imageReceiver2.setColorFilter(new ColorMatrixColorFilter(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, -1.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        if (fontMetricsInt != null) {
            if (this.alignTop) {
                int dp = (fontMetricsInt.descent - fontMetricsInt.ascent) - AndroidUtilities.dp(4.0f);
                int i3 = this.height - dp;
                fontMetricsInt.descent = i3;
                fontMetricsInt.bottom = i3;
                int i4 = 0 - dp;
                fontMetricsInt.ascent = i4;
                fontMetricsInt.top = i4;
            } else {
                int dp2 = ((-this.height) / 2) - AndroidUtilities.dp(4.0f);
                fontMetricsInt.ascent = dp2;
                fontMetricsInt.top = dp2;
                int i5 = this.height;
                int dp3 = (i5 - (i5 / 2)) - AndroidUtilities.dp(4.0f);
                fontMetricsInt.descent = dp3;
                fontMetricsInt.bottom = dp3;
            }
        }
        return this.width;
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        canvas.save();
        if (this.alignTop) {
            this.imageReceiver.setImageCoords((float) ((int) f), (float) (i3 - 1), (float) this.width, (float) this.height);
        } else {
            int i6 = this.height;
            this.imageReceiver.setImageCoords((float) ((int) f), (float) (i3 + ((((i5 - AndroidUtilities.dp(4.0f)) - i3) - i6) / 2)), (float) this.width, (float) i6);
        }
        this.imageReceiver.draw(canvas);
        canvas.restore();
    }
}
