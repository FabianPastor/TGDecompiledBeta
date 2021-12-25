package org.telegram.tgnet;

public class TLRPC$TL_messages_availableReactionsNotModified extends TLRPC$messages_AvailableReactions {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
