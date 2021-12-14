package org.telegram.tgnet;

public class TLRPC$TL_encryptedChatWaiting_layer131 extends TLRPC$TL_encryptedChatWaiting {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt32(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.date = abstractSerializedData.readInt32(z);
        this.admin_id = (long) abstractSerializedData.readInt32(z);
        this.participant_id = (long) abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32((int) this.admin_id);
        abstractSerializedData.writeInt32((int) this.participant_id);
    }
}
