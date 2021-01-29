package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEvent extends TLObject {
    public static int constructor = NUM;
    public TLRPC$ChannelAdminLogEventAction action;
    public int date;
    public long id;
    public int user_id;

    public static TLRPC$TL_channelAdminLogEvent TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_channelAdminLogEvent tLRPC$TL_channelAdminLogEvent = new TLRPC$TL_channelAdminLogEvent();
            tLRPC$TL_channelAdminLogEvent.readParams(abstractSerializedData, z);
            return tLRPC$TL_channelAdminLogEvent;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_channelAdminLogEvent", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt64(z);
        this.date = abstractSerializedData.readInt32(z);
        this.user_id = abstractSerializedData.readInt32(z);
        this.action = TLRPC$ChannelAdminLogEventAction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.user_id);
        this.action.serializeToStream(abstractSerializedData);
    }
}
