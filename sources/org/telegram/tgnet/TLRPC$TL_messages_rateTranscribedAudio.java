package org.telegram.tgnet;

public class TLRPC$TL_messages_rateTranscribedAudio extends TLObject {
    public static int constructor = NUM;
    public boolean good;
    public int msg_id;
    public TLRPC$InputPeer peer;
    public long transcription_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
        abstractSerializedData.writeInt64(this.transcription_id);
        abstractSerializedData.writeBool(this.good);
    }
}
