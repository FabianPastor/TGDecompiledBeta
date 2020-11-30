package org.telegram.tgnet;

public class TLRPC$TL_phone_toggleGroupCallSettings extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_inputGroupCall call;
    public int flags;
    public boolean join_muted;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.call.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeBool(this.join_muted);
        }
    }
}
