package org.telegram.tgnet;

public class TLRPC$TL_chatReactionsNone extends TLRPC$ChatReactions {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
