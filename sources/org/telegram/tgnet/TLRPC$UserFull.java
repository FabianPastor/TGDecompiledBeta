package org.telegram.tgnet;

public abstract class TLRPC$UserFull extends TLObject {
    public String about;
    public boolean blocked;
    public TLRPC$BotInfo bot_info;
    public boolean can_pin_message;
    public int common_chats_count;
    public int flags;
    public int folder_id;
    public boolean has_scheduled;
    public TLRPC$TL_contacts_link_layer101 link;
    public TLRPC$PeerNotifySettings notify_settings;
    public boolean phone_calls_available;
    public boolean phone_calls_private;
    public int pinned_msg_id;
    public TLRPC$Photo profile_photo;
    public TLRPC$TL_peerSettings settings;
    public TLRPC$User user;
    public boolean video_calls_available;

    public static TLRPC$UserFull TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$UserFull tLRPC$UserFull;
        if (i == -NUM) {
            tLRPC$UserFull = new TLRPC$TL_userFull_layer98();
        } else if (i != -NUM) {
            tLRPC$UserFull = i != NUM ? null : new TLRPC$TL_userFull_layer101();
        } else {
            tLRPC$UserFull = new TLRPC$TL_userFull();
        }
        if (tLRPC$UserFull != null || !z) {
            if (tLRPC$UserFull != null) {
                tLRPC$UserFull.readParams(abstractSerializedData, z);
            }
            return tLRPC$UserFull;
        }
        throw new RuntimeException(String.format("can't parse magic %x in UserFull", new Object[]{Integer.valueOf(i)}));
    }
}
