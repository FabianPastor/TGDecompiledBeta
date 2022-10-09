package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_searchResultPosition extends TLObject {
    public static int constructor = NUM;
    public int date;
    public int msg_id;
    public int offset;

    public static TLRPC$TL_searchResultPosition TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_searchResultPosition", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_searchResultPosition tLRPC$TL_searchResultPosition = new TLRPC$TL_searchResultPosition();
        tLRPC$TL_searchResultPosition.readParams(abstractSerializedData, z);
        return tLRPC$TL_searchResultPosition;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.msg_id = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        this.offset = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.msg_id);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.offset);
    }
}
