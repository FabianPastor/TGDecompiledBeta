package org.telegram.tgnet;

public abstract class TLRPC$InputChannel extends TLObject {
    public long access_hash;
    public int channel_id;

    public static TLRPC$InputChannel TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputChannel tLRPC$InputChannel;
        if (i == -NUM) {
            tLRPC$InputChannel = new TLRPC$TL_inputChannel();
        } else if (i != -NUM) {
            tLRPC$InputChannel = i != NUM ? null : new TLRPC$TL_inputChannelFromMessage();
        } else {
            tLRPC$InputChannel = new TLRPC$TL_inputChannelEmpty();
        }
        if (tLRPC$InputChannel != null || !z) {
            if (tLRPC$InputChannel != null) {
                tLRPC$InputChannel.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputChannel;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputChannel", new Object[]{Integer.valueOf(i)}));
    }
}
