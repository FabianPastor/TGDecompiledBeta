package org.telegram.tgnet;

public class TLRPC$TL_langpack_getLanguage extends TLObject {
    public static int constructor = NUM;
    public String lang_code;
    public String lang_pack;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_langPackLanguage.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.lang_pack);
        abstractSerializedData.writeString(this.lang_code);
    }
}
