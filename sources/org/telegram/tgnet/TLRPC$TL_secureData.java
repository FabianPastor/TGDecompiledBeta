package org.telegram.tgnet;

public class TLRPC$TL_secureData extends TLObject {
    public static int constructor = -NUM;
    public byte[] data;
    public byte[] data_hash;
    public byte[] secret;

    public static TLRPC$TL_secureData TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_secureData tLRPC$TL_secureData = new TLRPC$TL_secureData();
            tLRPC$TL_secureData.readParams(abstractSerializedData, z);
            return tLRPC$TL_secureData;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_secureData", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.data = abstractSerializedData.readByteArray(z);
        this.data_hash = abstractSerializedData.readByteArray(z);
        this.secret = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.data);
        abstractSerializedData.writeByteArray(this.data_hash);
        abstractSerializedData.writeByteArray(this.secret);
    }
}
