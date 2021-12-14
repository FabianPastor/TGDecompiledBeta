package org.telegram.tgnet;

public class TLRPC$TL_decryptedMessageActionSetMessageTTL extends TLRPC$DecryptedMessageAction {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.ttl_seconds = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.ttl_seconds);
    }
}
