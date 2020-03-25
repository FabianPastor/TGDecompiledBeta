package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionChangePhoto extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.new_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.prev_photo.serializeToStream(abstractSerializedData);
        this.new_photo.serializeToStream(abstractSerializedData);
    }
}
