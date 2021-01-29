package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionStartGroupCall extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public TLRPC$TL_inputGroupCall call;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.call = TLRPC$TL_inputGroupCall.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.call.serializeToStream(abstractSerializedData);
    }
}
