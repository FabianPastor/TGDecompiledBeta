package org.telegram.tgnet;

public class TLRPC$TL_phoneCallProtocol extends TLRPC$PhoneCallProtocol {
    public static int constructor = -58224696;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        this.udp_p2p = (readInt32 & 1) != 0;
        this.udp_reflector = (this.flags & 2) != 0;
        this.min_layer = abstractSerializedData.readInt32(z);
        this.max_layer = abstractSerializedData.readInt32(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt323; i++) {
                this.library_versions.add(abstractSerializedData.readString(z));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
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
        abstractSerializedData.writeInt32(NUM);
        int size = this.library_versions.size();
        abstractSerializedData.writeInt32(size);
        for (int i3 = 0; i3 < size; i3++) {
            abstractSerializedData.writeString(this.library_versions.get(i3));
        }
    }
}
