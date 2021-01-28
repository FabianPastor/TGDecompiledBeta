package org.telegram.tgnet;

public class TLRPC$TL_keyboardButtonRequestGeoLocation extends TLRPC$KeyboardButton {
    public static int constructor = -59151553;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.text = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.text);
    }
}
