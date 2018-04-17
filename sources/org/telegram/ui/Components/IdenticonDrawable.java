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

    private int getBits(int bitOffset) {
        return (this.data[bitOffset / 8] >> (bitOffset % 8)) & 3;
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

    public void setColors(int[] value) {
        if (this.colors.length != 4) {
            throw new IllegalArgumentException("colors must have length of 4");
        }
        this.colors = value;
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        if (this.data != null) {
            float rectSize;
            float xOffset;
            float yOffset;
            int bitPointer;
            int iy;
            int bitPointer2;
            if (r0.data.length == 16) {
                rectSize = (float) Math.floor((double) (((float) Math.min(getBounds().width(), getBounds().height())) / 8.0f));
                xOffset = Math.max(0.0f, (((float) getBounds().width()) - (rectSize * 8.0f)) / 2.0f);
                yOffset = Math.max(0.0f, (((float) getBounds().height()) - (8.0f * rectSize)) / 2.0f);
                bitPointer = 0;
                iy = 0;
                while (iy < 8) {
                    bitPointer2 = bitPointer;
                    for (bitPointer = 0; bitPointer < 8; bitPointer++) {
                        int byteValue = getBits(bitPointer2);
                        bitPointer2 += 2;
                        r0.paint.setColor(r0.colors[Math.abs(byteValue) % 4]);
                        canvas.drawRect(xOffset + (((float) bitPointer) * rectSize), (((float) iy) * rectSize) + yOffset, ((((float) bitPointer) * rectSize) + xOffset) + rectSize, ((((float) iy) * rectSize) + rectSize) + yOffset, r0.paint);
                    }
                    iy++;
                    bitPointer = bitPointer2;
                }
            } else {
                rectSize = (float) Math.floor((double) (((float) Math.min(getBounds().width(), getBounds().height())) / 12.0f));
                xOffset = Math.max(0.0f, (((float) getBounds().width()) - (rectSize * 12.0f)) / 2.0f);
                yOffset = Math.max(0.0f, (((float) getBounds().height()) - (12.0f * rectSize)) / 2.0f);
                bitPointer = 0;
                iy = 0;
                while (iy < 12) {
                    bitPointer2 = bitPointer;
                    for (bitPointer = 0; bitPointer < 12; bitPointer++) {
                        r0.paint.setColor(r0.colors[Math.abs(getBits(bitPointer2)) % 4]);
                        canvas.drawRect(xOffset + (((float) bitPointer) * rectSize), (((float) iy) * rectSize) + yOffset, ((((float) bitPointer) * rectSize) + xOffset) + rectSize, ((((float) iy) * rectSize) + rectSize) + yOffset, r0.paint);
                        bitPointer2 += 2;
                    }
                    iy++;
                    bitPointer = bitPointer2;
                }
            }
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(32.0f);
    }
}
