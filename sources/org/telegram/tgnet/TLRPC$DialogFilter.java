package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$DialogFilter extends TLObject {
    public boolean bots;
    public boolean broadcasts;
    public boolean contacts;
    public String emoticon;
    public boolean exclude_archived;
    public boolean exclude_muted;
    public ArrayList<TLRPC$InputPeer> exclude_peers = new ArrayList<>();
    public boolean exclude_read;
    public int flags;
    public boolean groups;
    public int id;
    public ArrayList<TLRPC$InputPeer> include_peers = new ArrayList<>();
    public boolean non_contacts;
    public ArrayList<TLRPC$InputPeer> pinned_peers = new ArrayList<>();
    public String title;

    public static TLRPC$DialogFilter TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$DialogFilter tLRPC$DialogFilter;
        if (i != NUM) {
            tLRPC$DialogFilter = i != NUM ? null : new TLRPC$TL_dialogFilter();
        } else {
            tLRPC$DialogFilter = new TLRPC$TL_dialogFilterDefault();
        }
        if (tLRPC$DialogFilter != null || !z) {
            if (tLRPC$DialogFilter != null) {
                tLRPC$DialogFilter.readParams(abstractSerializedData, z);
            }
            return tLRPC$DialogFilter;
        }
        throw new RuntimeException(String.format("can't parse magic %x in DialogFilter", new Object[]{Integer.valueOf(i)}));
    }
}
