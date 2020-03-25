package org.telegram.tgnet;

public class TLRPC$TL_phoneCallProtocol_layer110 extends TLRPC$TL_phoneCallProtocol {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.udp_p2p = (readInt32 & 1) != 0;
        if ((this.flags & 2) == 0) {
            z2 = false;
        }
        this.udp_reflector = z2;
        this.min_layer = abstractSerializedData.readInt32(z);
        this.max_layer = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.udp_p2p ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.udp_reflector ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt32(this.min_layer);
        abstractSerializedData.writeInt32(this.max_layer);
    }
}
