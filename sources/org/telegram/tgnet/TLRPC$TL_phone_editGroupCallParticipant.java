package org.telegram.tgnet;

public class TLRPC$TL_phone_editGroupCallParticipant extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$TL_inputGroupCall call;
    public int flags;
    public boolean muted;
    public TLRPC$InputPeer participant;
    public boolean raise_hand;
    public int volume;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.muted ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.call.serializeToStream(abstractSerializedData);
        this.participant.serializeToStream(abstractSerializedData);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.volume);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeBool(this.raise_hand);
        }
    }
}
