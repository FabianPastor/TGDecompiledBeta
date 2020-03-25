package org.telegram.tgnet;

public class TLRPC$TL_peerNotifySettings_layer77 extends TLRPC$TL_peerNotifySettings {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.show_previews = (readInt32 & 1) != 0;
        if ((this.flags & 2) == 0) {
            z2 = false;
        }
        this.silent = z2;
        this.mute_until = abstractSerializedData.readInt32(z);
        this.sound = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.show_previews ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.silent ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt32(this.mute_until);
        abstractSerializedData.writeString(this.sound);
    }
}
