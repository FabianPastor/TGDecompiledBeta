package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_uploadImportedMedia extends TLObject {
    public static int constructor = NUM;
    public String file_name;
    public long import_id;
    public TLRPC$InputMedia media;
    public TLRPC$InputPeer peer;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$MessageMedia.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.import_id);
        abstractSerializedData.writeString(this.file_name);
        this.media.serializeToStream(abstractSerializedData);
    }
}
