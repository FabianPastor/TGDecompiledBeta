package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_popularContact extends TLObject {
    public static int constructor = NUM;
    public long client_id;
    public int importers;

    public static TLRPC$TL_popularContact TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_popularContact", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_popularContact tLRPC$TL_popularContact = new TLRPC$TL_popularContact();
        tLRPC$TL_popularContact.readParams(abstractSerializedData, z);
        return tLRPC$TL_popularContact;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.client_id = abstractSerializedData.readInt64(z);
        this.importers = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.client_id);
        abstractSerializedData.writeInt32(this.importers);
    }
}
