package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$channels_ChannelParticipants extends TLObject {
    public TLRPC$channels_ChannelParticipants() {
        new ArrayList();
        new ArrayList();
    }

    public static TLRPC$channels_ChannelParticipants TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$channels_ChannelParticipants tLRPC$TL_channels_channelParticipants;
        if (i == -NUM) {
            tLRPC$TL_channels_channelParticipants = new TLRPC$TL_channels_channelParticipants();
        } else {
            tLRPC$TL_channels_channelParticipants = i != -NUM ? null : new TLRPC$channels_ChannelParticipants() { // from class: org.telegram.tgnet.TLRPC$TL_channels_channelParticipantsNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$TL_channels_channelParticipants != null || !z) {
            if (tLRPC$TL_channels_channelParticipants != null) {
                tLRPC$TL_channels_channelParticipants.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_channels_channelParticipants;
        }
        throw new RuntimeException(String.format("can't parse magic %x in channels_ChannelParticipants", Integer.valueOf(i)));
    }
}
