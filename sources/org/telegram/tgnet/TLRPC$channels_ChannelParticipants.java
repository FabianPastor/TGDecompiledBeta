package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$channels_ChannelParticipants extends TLObject {
    public int count;
    public ArrayList<TLRPC$ChannelParticipant> participants = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$channels_ChannelParticipants TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$channels_ChannelParticipants tLRPC$channels_ChannelParticipants;
        if (i != -NUM) {
            tLRPC$channels_ChannelParticipants = i != -NUM ? null : new TLRPC$TL_channels_channelParticipants();
        } else {
            tLRPC$channels_ChannelParticipants = new TLRPC$TL_channels_channelParticipantsNotModified();
        }
        if (tLRPC$channels_ChannelParticipants != null || !z) {
            if (tLRPC$channels_ChannelParticipants != null) {
                tLRPC$channels_ChannelParticipants.readParams(abstractSerializedData, z);
            }
            return tLRPC$channels_ChannelParticipants;
        }
        throw new RuntimeException(String.format("can't parse magic %x in channels_ChannelParticipants", new Object[]{Integer.valueOf(i)}));
    }
}
