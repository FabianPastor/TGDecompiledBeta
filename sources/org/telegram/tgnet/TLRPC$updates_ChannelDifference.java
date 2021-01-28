package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$updates_ChannelDifference extends TLObject {
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public TLRPC$Dialog dialog;
    public int flags;
    public boolean isFinal;
    public ArrayList<TLRPC$Message> messages = new ArrayList<>();
    public ArrayList<TLRPC$Message> new_messages = new ArrayList<>();
    public ArrayList<TLRPC$Update> other_updates = new ArrayList<>();
    public int pts;
    public int timeout;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$updates_ChannelDifference TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference;
        if (i == -NUM) {
            tLRPC$updates_ChannelDifference = new TLRPC$TL_updates_channelDifferenceTooLong();
        } else if (i != NUM) {
            tLRPC$updates_ChannelDifference = i != NUM ? null : new TLRPC$TL_updates_channelDifferenceEmpty();
        } else {
            tLRPC$updates_ChannelDifference = new TLRPC$TL_updates_channelDifference();
        }
        if (tLRPC$updates_ChannelDifference != null || !z) {
            if (tLRPC$updates_ChannelDifference != null) {
                tLRPC$updates_ChannelDifference.readParams(abstractSerializedData, z);
            }
            return tLRPC$updates_ChannelDifference;
        }
        throw new RuntimeException(String.format("can't parse magic %x in updates_ChannelDifference", new Object[]{Integer.valueOf(i)}));
    }
}
