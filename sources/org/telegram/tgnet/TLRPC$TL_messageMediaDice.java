package org.telegram.tgnet;

public class TLRPC$TL_messageMediaDice extends TLRPC$MessageMedia {
    public static int constructor = NUM;
    public int value;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.value = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.value);
    }
}
