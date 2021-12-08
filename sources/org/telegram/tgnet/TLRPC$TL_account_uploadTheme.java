package org.telegram.tgnet;

public class TLRPC$TL_account_uploadTheme extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputFile file;
    public String file_name;
    public int flags;
    public String mime_type;
    public TLRPC$InputFile thumb;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Document.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.file.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            this.thumb.serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.file_name);
        abstractSerializedData.writeString(this.mime_type);
    }
}
