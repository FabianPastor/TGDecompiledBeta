package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_contacts_link_layer101 extends TLObject {
    public static int constructor = NUM;
    public TLRPC$ContactLink_layer101 foreign_link;
    public TLRPC$ContactLink_layer101 my_link;
    public TLRPC$User user;

    public static TLRPC$TL_contacts_link_layer101 TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_contacts_link", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_contacts_link_layer101 tLRPC$TL_contacts_link_layer101 = new TLRPC$TL_contacts_link_layer101();
        tLRPC$TL_contacts_link_layer101.readParams(abstractSerializedData, z);
        return tLRPC$TL_contacts_link_layer101;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.my_link = TLRPC$ContactLink_layer101.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.foreign_link = TLRPC$ContactLink_layer101.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.user = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.my_link.serializeToStream(abstractSerializedData);
        this.foreign_link.serializeToStream(abstractSerializedData);
        this.user.serializeToStream(abstractSerializedData);
    }
}
