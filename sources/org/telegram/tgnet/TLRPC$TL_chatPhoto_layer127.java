package org.telegram.tgnet;

import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;

public class TLRPC$TL_chatPhoto_layer127 extends TLRPC$TL_chatPhoto {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.has_video = z2;
        this.photo_small = TLRPC$FileLocation.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.photo_big = TLRPC$FileLocation.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 2) != 0) {
            this.stripped_thumb = abstractSerializedData.readByteArray(z);
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    this.strippedBitmap = new BitmapDrawable(ImageLoader.getStrippedPhotoBitmap(this.stripped_thumb, "b"));
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
        this.dc_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.has_video ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.photo_small.serializeToStream(abstractSerializedData);
        this.photo_big.serializeToStream(abstractSerializedData);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeByteArray(this.stripped_thumb);
        }
        abstractSerializedData.writeInt32(this.dc_id);
    }
}
