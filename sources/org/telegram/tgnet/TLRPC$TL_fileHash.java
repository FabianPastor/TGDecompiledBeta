package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_fileHash extends TLObject {
    public static int constructor = -NUM;
    public byte[] hash;
    public int limit;
    public long offset;

    public static TLRPC$TL_fileHash TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_fileHash", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_fileHash tLRPC$TL_fileHash = new TLRPC$TL_fileHash();
        tLRPC$TL_fileHash.readParams(abstractSerializedData, z);
        return tLRPC$TL_fileHash;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.offset = abstractSerializedData.readInt64(z);
        this.limit = abstractSerializedData.readInt32(z);
        this.hash = abstractSerializedData.readByteArray(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.offset);
        abstractSerializedData.writeInt32(this.limit);
        abstractSerializedData.writeByteArray(this.hash);
    }
}
