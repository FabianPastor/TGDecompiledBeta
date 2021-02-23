package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public int new_value;
    public int prev_value;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_value = abstractSerializedData.readInt32(z);
        this.new_value = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.prev_value);
        abstractSerializedData.writeInt32(this.new_value);
    }
}
