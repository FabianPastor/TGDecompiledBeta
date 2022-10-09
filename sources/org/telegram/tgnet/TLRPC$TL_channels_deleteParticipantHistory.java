package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_channels_deleteParticipantHistory extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputChannel channel;
    public TLRPC$InputPeer participant;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_affectedHistory.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        this.participant.serializeToStream(abstractSerializedData);
    }
}
