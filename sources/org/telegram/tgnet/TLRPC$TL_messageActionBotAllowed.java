package org.telegram.tgnet;

public class TLRPC$TL_messageActionBotAllowed extends TLRPC$MessageAction {
    public static int constructor = -NUM;
    public String domain;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.domain = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.domain);
    }
}
