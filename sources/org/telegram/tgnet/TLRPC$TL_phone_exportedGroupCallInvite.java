package org.telegram.tgnet;

public class TLRPC$TL_phone_exportedGroupCallInvite extends TLObject {
    public static int constructor = NUM;
    public String link;

    public static TLRPC$TL_phone_exportedGroupCallInvite TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_phone_exportedGroupCallInvite tLRPC$TL_phone_exportedGroupCallInvite = new TLRPC$TL_phone_exportedGroupCallInvite();
            tLRPC$TL_phone_exportedGroupCallInvite.readParams(abstractSerializedData, z);
            return tLRPC$TL_phone_exportedGroupCallInvite;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_phone_exportedGroupCallInvite", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.link = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.link);
    }
}
