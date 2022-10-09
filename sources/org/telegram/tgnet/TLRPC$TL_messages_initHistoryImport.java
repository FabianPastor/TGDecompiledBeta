package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_initHistoryImport extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputFile file;
    public int media_count;
    public TLRPC$InputPeer peer;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_historyImport.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        this.file.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.media_count);
    }
}
