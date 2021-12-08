package org.telegram.tgnet;

public class TLRPC$TL_decryptedMessageActionNotifyLayer extends TLRPC$DecryptedMessageAction {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.layer = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.layer);
    }
}
