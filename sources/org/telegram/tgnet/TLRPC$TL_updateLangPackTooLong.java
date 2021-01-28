package org.telegram.tgnet;

public class TLRPC$TL_updateLangPackTooLong extends TLRPC$Update {
    public static int constructor = NUM;
    public String lang_code;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.lang_code = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.lang_code);
    }
}
