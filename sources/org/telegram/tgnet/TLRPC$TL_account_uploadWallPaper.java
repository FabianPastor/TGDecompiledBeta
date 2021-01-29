package org.telegram.tgnet;

public class TLRPC$TL_account_uploadWallPaper extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputFile file;
    public String mime_type;
    public TLRPC$TL_wallPaperSettings settings;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$WallPaper.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.file.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.mime_type);
        this.settings.serializeToStream(abstractSerializedData);
    }
}
