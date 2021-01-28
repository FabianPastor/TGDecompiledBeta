package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$messages_Messages extends TLObject {
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public int count;
    public int flags;
    public boolean inexact;
    public ArrayList<TLRPC$Message> messages = new ArrayList<>();
    public int next_rate;
    public int offset_id_offset;
    public int pts;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$messages_Messages TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Messages tLRPC$messages_Messages;
        switch (i) {
            case -1938715001:
                tLRPC$messages_Messages = new TLRPC$TL_messages_messages();
                break;
            case 978610270:
                tLRPC$messages_Messages = new TLRPC$TL_messages_messagesSlice();
                break;
            case 1682413576:
                tLRPC$messages_Messages = new TLRPC$TL_messages_channelMessages();
                break;
            case 1951620897:
                tLRPC$messages_Messages = new TLRPC$TL_messages_messagesNotModified();
                break;
            default:
                tLRPC$messages_Messages = null;
                break;
        }
        if (tLRPC$messages_Messages != null || !z) {
            if (tLRPC$messages_Messages != null) {
                tLRPC$messages_Messages.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_Messages;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Messages", new Object[]{Integer.valueOf(i)}));
    }
}
