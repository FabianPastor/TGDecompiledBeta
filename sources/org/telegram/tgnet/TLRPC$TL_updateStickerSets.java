package org.telegram.tgnet;

public class TLRPC$TL_updateStickerSets extends TLRPC$Update {
    public static int constructor = NUM;
    public boolean emojis;
    public int flags;
    public boolean masks;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.masks = (readInt32 & 1) != 0;
        if ((readInt32 & 2) != 0) {
            z2 = true;
        }
        this.emojis = z2;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.masks ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.emojis ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
    }
}
