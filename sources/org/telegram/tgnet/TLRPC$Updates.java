package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$Updates extends TLObject {
    public long chat_id;
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public int date;
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public int flags;
    public long from_id;
    public TLRPC$MessageFwdHeader fwd_from;
    public int id;
    public TLRPC$MessageMedia media;
    public boolean media_unread;
    public boolean mentioned;
    public String message;
    public boolean out;
    public int pts;
    public int pts_count;
    public TLRPC$TL_messageReplyHeader reply_to;
    public int seq;
    public int seq_start;
    public boolean silent;
    public int ttl_period;
    public TLRPC$Update update;
    public ArrayList<TLRPC$Update> updates = new ArrayList<>();
    public long user_id;
    public ArrayList<TLRPC$User> users = new ArrayList<>();
    public long via_bot_id;

    public static TLRPC$Updates TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Updates tLRPC$Updates;
        switch (i) {
            case -1877614335:
                tLRPC$Updates = new TLRPC$TL_updateShortSentMessage();
                break;
            case -484987010:
                tLRPC$Updates = new TLRPC$TL_updatesTooLong();
                break;
            case 826001400:
                tLRPC$Updates = new TLRPC$TL_updateShortMessage();
                break;
            case 1299050149:
                tLRPC$Updates = new TLRPC$TL_updateShortChatMessage();
                break;
            case 1918567619:
                tLRPC$Updates = new TLRPC$TL_updatesCombined();
                break;
            case 1957577280:
                tLRPC$Updates = new TLRPC$TL_updates();
                break;
            case 2027216577:
                tLRPC$Updates = new TLRPC$TL_updateShort();
                break;
            default:
                tLRPC$Updates = null;
                break;
        }
        if (tLRPC$Updates != null || !z) {
            if (tLRPC$Updates != null) {
                tLRPC$Updates.readParams(abstractSerializedData, z);
            }
            return tLRPC$Updates;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Updates", new Object[]{Integer.valueOf(i)}));
    }
}
