package org.telegram.tgnet;

public class TLRPC$TL_contactStatus extends TLObject {
    public static int constructor = NUM;
    public TLRPC$UserStatus status;
    public long user_id;

    public static TLRPC$TL_contactStatus TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_contactStatus tLRPC$TL_contactStatus = new TLRPC$TL_contactStatus();
            tLRPC$TL_contactStatus.readParams(abstractSerializedData, z);
            return tLRPC$TL_contactStatus;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_contactStatus", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.status = TLRPC$UserStatus.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        this.status.serializeToStream(abstractSerializedData);
    }
}
