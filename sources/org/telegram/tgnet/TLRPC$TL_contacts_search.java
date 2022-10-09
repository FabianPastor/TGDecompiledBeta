package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_contacts_search extends TLObject {
    public static int constructor = NUM;
    public int limit;
    public String q;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_contacts_found.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.q);
        abstractSerializedData.writeInt32(this.limit);
    }
}
