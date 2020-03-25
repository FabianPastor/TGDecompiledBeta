package org.telegram.tgnet;

public class TLRPC$TL_updateGroupCall extends TLRPC$Update {
    public static int constructor = -NUM;
    public TLRPC$GroupCall call;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.call = TLRPC$GroupCall.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.call.serializeToStream(abstractSerializedData);
    }
}
