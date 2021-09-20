package org.telegram.tgnet;

public class TLRPC$TL_encryptedChatRequested_layer131 extends TLRPC$TL_encryptedChatRequested {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.folder_id = abstractSerializedData.readInt32(z);
        }
        this.id = abstractSerializedData.readInt32(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.date = abstractSerializedData.readInt32(z);
        this.admin_id = (long) abstractSerializedData.readInt32(z);
        this.participant_id = (long) abstractSerializedData.readInt32(z);
        this.g_a = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.folder_id);
        }
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32((int) this.admin_id);
        abstractSerializedData.writeInt32((int) this.participant_id);
        abstractSerializedData.writeByteArray(this.g_a);
    }
}
