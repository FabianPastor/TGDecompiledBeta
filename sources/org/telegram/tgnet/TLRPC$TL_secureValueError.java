package org.telegram.tgnet;

public class TLRPC$TL_secureValueError extends TLRPC$SecureValueError {
    public static int constructor = -NUM;
    public byte[] hash;
    public String text;
    public TLRPC$SecureValueType type;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.type = TLRPC$SecureValueType.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.hash = abstractSerializedData.readByteArray(z);
        this.text = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.type.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeByteArray(this.hash);
        abstractSerializedData.writeString(this.text);
    }
}
