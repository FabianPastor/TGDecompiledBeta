package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_importedContact extends TLObject {
    public static int constructor = -NUM;
    public long client_id;
    public long user_id;

    public static TLRPC$TL_importedContact TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_importedContact", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_importedContact tLRPC$TL_importedContact = new TLRPC$TL_importedContact();
        tLRPC$TL_importedContact.readParams(abstractSerializedData, z);
        return tLRPC$TL_importedContact;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.client_id = abstractSerializedData.readInt64(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeInt64(this.client_id);
    }
}
