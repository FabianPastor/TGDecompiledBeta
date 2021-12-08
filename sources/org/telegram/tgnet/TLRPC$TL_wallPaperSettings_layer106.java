package org.telegram.tgnet;

public class TLRPC$TL_wallPaperSettings_layer106 extends TLRPC$TL_wallPaperSettings {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.blur = (readInt32 & 2) != 0;
        if ((readInt32 & 4) != 0) {
            z2 = true;
        }
        this.motion = z2;
        if ((readInt32 & 1) != 0) {
            this.background_color = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 8) != 0) {
            this.intensity = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.blur ? this.flags | 2 : this.flags & -3;
        this.flags = i;
        int i2 = this.motion ? i | 4 : i & -5;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.background_color);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.intensity);
        }
    }
}
