package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionChangeTitle extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = -NUM;
    public String new_value;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_value = abstractSerializedData.readString(z);
        this.new_value = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.prev_value);
        abstractSerializedData.writeString(this.new_value);
    }
}
