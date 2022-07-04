package org.telegram.tgnet;

public class TLRPC$TL_decryptedMessageActionTyping extends TLRPC$DecryptedMessageAction {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.action = TLRPC$SendMessageAction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.action.serializeToStream(abstractSerializedData);
    }
}
