package org.telegram.tgnet;

public class TLRPC$TL_fileHash extends TLObject {
    public static int constructor = -NUM;
    public byte[] hash;
    public int limit;
    public long offset;

    public static TLRPC$TL_fileHash TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_fileHash tLRPC$TL_fileHash = new TLRPC$TL_fileHash();
            tLRPC$TL_fileHash.readParams(abstractSerializedData, z);
            return tLRPC$TL_fileHash;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_fileHash", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.offset = abstractSerializedData.readInt64(z);
        this.limit = abstractSerializedData.readInt32(z);
        this.hash = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.offset);
        abstractSerializedData.writeInt32(this.limit);
        abstractSerializedData.writeByteArray(this.hash);
    }
}
