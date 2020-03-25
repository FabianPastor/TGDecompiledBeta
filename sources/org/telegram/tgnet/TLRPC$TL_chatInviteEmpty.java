package org.telegram.tgnet;

public class TLRPC$TL_chatInviteEmpty extends TLRPC$ExportedChatInvite {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
