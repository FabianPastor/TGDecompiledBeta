package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionToggleGroupCallSetting extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public boolean join_muted;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.join_muted = abstractSerializedData.readBool(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeBool(this.join_muted);
    }
}
