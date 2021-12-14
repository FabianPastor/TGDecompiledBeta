package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionToggleInvites extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public boolean new_value;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.new_value = abstractSerializedData.readBool(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeBool(this.new_value);
    }
}
