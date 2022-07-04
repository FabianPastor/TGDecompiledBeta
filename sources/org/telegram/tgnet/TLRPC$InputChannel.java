package org.telegram.tgnet;

public abstract class TLRPC$InputChannel extends TLObject {
    public long access_hash;
    public long channel_id;

    public static TLRPC$InputChannel TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputChannel tLRPC$InputChannel;
        switch (i) {
            case -1343524562:
                tLRPC$InputChannel = new TLRPC$TL_inputChannel_layer131();
                break;
            case -292807034:
                tLRPC$InputChannel = new TLRPC$TL_inputChannelEmpty();
                break;
            case -212145112:
                tLRPC$InputChannel = new TLRPC$TL_inputChannel();
                break;
            case 707290417:
                tLRPC$InputChannel = new TLRPC$TL_inputChannelFromMessage_layer131();
                break;
            case 1536380829:
                tLRPC$InputChannel = new TLRPC$TL_inputChannelFromMessage();
                break;
            default:
                tLRPC$InputChannel = null;
                break;
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
