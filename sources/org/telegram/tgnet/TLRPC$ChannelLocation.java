package org.telegram.tgnet;

public abstract class TLRPC$ChannelLocation extends TLObject {
    public static TLRPC$ChannelLocation TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChannelLocation tLRPC$ChannelLocation;
        if (i != -NUM) {
            tLRPC$ChannelLocation = i != NUM ? null : new TLRPC$TL_channelLocation();
        } else {
            tLRPC$ChannelLocation = new TLRPC$TL_channelLocationEmpty();
        }
        if (tLRPC$ChannelLocation != null || !z) {
            if (tLRPC$ChannelLocation != null) {
                tLRPC$ChannelLocation.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChannelLocation;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChannelLocation", new Object[]{Integer.valueOf(i)}));
    }
}
