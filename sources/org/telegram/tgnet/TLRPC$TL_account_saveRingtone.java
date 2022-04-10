package org.telegram.tgnet;

public class TLRPC$TL_account_saveRingtone extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputDocument id;
    public boolean unsave;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$account_SavedRingtone.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeBool(this.unsave);
    }
}
