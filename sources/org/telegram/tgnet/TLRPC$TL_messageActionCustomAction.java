package org.telegram.tgnet;

public class TLRPC$TL_messageActionCustomAction extends TLRPC$MessageAction {
    public static int constructor = -85549226;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.message = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.message);
    }
}
