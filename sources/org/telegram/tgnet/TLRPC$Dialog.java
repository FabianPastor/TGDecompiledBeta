package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$Dialog extends TLObject {
    public TLRPC$DraftMessage draft;
    public int flags;
    public int folder_id;
    public long id;
    public int last_message_date;
    public TLRPC$PeerNotifySettings notify_settings;
    public TLRPC$Peer peer;
    public boolean pinned;
    public int pinnedNum;
    public int pts;
    public int read_inbox_max_id;
    public int read_outbox_max_id;
    public int top_message;
    public int unread_count;
    public boolean unread_mark;
    public int unread_mentions_count;
    public int unread_reactions_count;

    public static TLRPC$Dialog TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Dialog tLRPC$TL_dialog;
        if (i == -NUM) {
            tLRPC$TL_dialog = new TLRPC$TL_dialog();
        } else {
            tLRPC$TL_dialog = i != NUM ? null : new TLRPC$TL_dialogFolder();
        }
        if (tLRPC$TL_dialog != null || !z) {
            if (tLRPC$TL_dialog != null) {
                tLRPC$TL_dialog.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_dialog;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Dialog", Integer.valueOf(i)));
    }
}
