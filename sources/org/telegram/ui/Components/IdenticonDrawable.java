package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC.EncryptedChat;

public class IdenticonDrawable extends Drawable {
    private int[] colors = new int[]{-1, -2758925, -13805707, -13657655};
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

    public void setEncryptedChat(EncryptedChat encryptedChat) {
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
            float floor;
            float f;
            float max;
            int i;
            int i2;
            int i3;
            float f2;
            float f3;
            if (bArr.length == 16) {
                floor = (float) Math.floor((double) (((float) Math.min(getBounds().width(), getBounds().height())) / 8.0f));
                f = 8.0f * floor;
                max = Math.max(0.0f, (((float) getBounds().width()) - f) / 2.0f);
                f = Math.max(0.0f, (((float) getBounds().height()) - f) / 2.0f);
                i = 0;
                i2 = 0;
                while (i < 8) {
                    i3 = i2;
                    for (i2 = 0; i2 < 8; i2++) {
                        int bits = getBits(i3);
                        i3 += 2;
                        this.paint.setColor(this.colors[Math.abs(bits) % 4]);
                        f2 = max + (((float) i2) * floor);
                        f3 = ((float) i) * floor;
                        canvas.drawRect(f2, f3 + f, f2 + floor, (f3 + floor) + f, this.paint);
                    }
                    i++;
                    i2 = i3;
                }
            } else {
                floor = (float) Math.floor((double) (((float) Math.min(getBounds().width(), getBounds().height())) / 12.0f));
                f = 12.0f * floor;
                max = Math.max(0.0f, (((float) getBounds().width()) - f) / 2.0f);
                f = Math.max(0.0f, (((float) getBounds().height()) - f) / 2.0f);
                i = 0;
                i2 = 0;
                while (i < 12) {
                    i3 = i2;
                    for (i2 = 0; i2 < 12; i2++) {
                        this.paint.setColor(this.colors[Math.abs(getBits(i3)) % 4]);
                        f2 = max + (((float) i2) * floor);
                        f3 = ((float) i) * floor;
                        canvas.drawRect(f2, f3 + f, f2 + floor, (f3 + floor) + f, this.paint);
                        i3 += 2;
                    }
                    i++;
                    i2 = i3;
                }
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
