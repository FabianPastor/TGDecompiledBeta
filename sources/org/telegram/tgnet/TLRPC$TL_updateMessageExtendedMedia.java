package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updateMessageExtendedMedia extends TLRPC$Update {
    public static int constructor = NUM;
    public TLRPC$MessageExtendedMedia extended_media;
    public int msg_id;
    public TLRPC$Peer peer;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.msg_id = abstractSerializedData.readInt32(z);
        this.extended_media = TLRPC$MessageExtendedMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
        this.extended_media.serializeToStream(abstractSerializedData);
    }
}
