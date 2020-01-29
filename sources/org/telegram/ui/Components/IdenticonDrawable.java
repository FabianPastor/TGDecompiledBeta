package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC;

public class IdenticonDrawable extends Drawable {
    private int[] colors = {-1, -2758925, -13805707, -13657655};
    private byte[] data;
    private Paint paint = new Paint();

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    private int getBits(int i) {
        return (this.data[i / 8] >> (i % 8)) & 3;
    }

    public void setEncryptedChat(TLRPC.EncryptedChat encryptedChat) {
        this.data = encryptedChat.key_hash;
        if (this.data == null) {
            byte[] calcAuthKeyHash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
            this.data = calcAuthKeyHash;
            encryptedChat.key_hash = calcAuthKeyHash;
        }
        invalidateSelf();
    }

    public void setColors(int[] iArr) {
        if (this.colors.length == 4) {
            this.colors = iArr;
            invalidateSelf();
            return;
        }
        throw new IllegalArgumentException("colors must have length of 4");
    }

    public void draw(Canvas canvas) {
        byte[] bArr = this.data;
        if (bArr != null) {
            if (bArr.length == 16) {
                float floor = (float) Math.floor((double) (((float) Math.min(getBounds().width(), getBounds().height())) / 8.0f));
                float f = 8.0f * floor;
                float max = Math.max(0.0f, (((float) getBounds().width()) - f) / 2.0f);
                float max2 = Math.max(0.0f, (((float) getBounds().height()) - f) / 2.0f);
                int i = 0;
                int i2 = 0;
                while (i < 8) {
                    int i3 = i2;
                    for (int i4 = 0; i4 < 8; i4++) {
                        int bits = getBits(i3);
                        i3 += 2;
                        this.paint.setColor(this.colors[Math.abs(bits) % 4]);
                        float f2 = max + (((float) i4) * floor);
                        float f3 = ((float) i) * floor;
                        canvas.drawRect(f2, f3 + max2, f2 + floor, f3 + floor + max2, this.paint);
                    }
                    i++;
                    i2 = i3;
                }
                return;
            }
            float floor2 = (float) Math.floor((double) (((float) Math.min(getBounds().width(), getBounds().height())) / 12.0f));
            float f4 = 12.0f * floor2;
            float max3 = Math.max(0.0f, (((float) getBounds().width()) - f4) / 2.0f);
            float max4 = Math.max(0.0f, (((float) getBounds().height()) - f4) / 2.0f);
            int i5 = 0;
            int i6 = 0;
            while (i5 < 12) {
                int i7 = i6;
                for (int i8 = 0; i8 < 12; i8++) {
                    this.paint.setColor(this.colors[Math.abs(getBits(i7)) % 4]);
                    float f5 = max3 + (((float) i8) * floor2);
                    float f6 = ((float) i5) * floor2;
                    canvas.drawRect(f5, f6 + max4, f5 + floor2, f6 + floor2 + max4, this.paint);
                    i7 += 2;
                }
                i5++;
                i6 = i7;
            }
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(32.0f);
    }
}
