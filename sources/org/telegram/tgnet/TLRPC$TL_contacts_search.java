package org.telegram.tgnet;

public class TLRPC$TL_contacts_search extends TLObject {
    public static int constructor = NUM;
    public int limit;
    public String q;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_contacts_found.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.q);
        abstractSerializedData.writeInt32(this.limit);
    }
}
