package org.telegram.tgnet;

public class TLRPC$TL_textPlain extends TLRPC$RichText {
    public static int constructor = NUM;
    public String text;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.text = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.text);
    }
}
