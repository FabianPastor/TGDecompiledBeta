package org.telegram.tgnet;

public class TLRPC$TL_messageActionWebViewDataSentMe extends TLRPC$MessageAction {
    public static int constructor = NUM;
    public String data;
    public String text;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.text = abstractSerializedData.readString(z);
        this.data = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.text);
        abstractSerializedData.writeString(this.data);
    }
}
