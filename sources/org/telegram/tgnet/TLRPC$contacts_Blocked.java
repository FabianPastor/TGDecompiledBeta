package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$contacts_Blocked extends TLObject {
    public ArrayList<TLRPC$TL_peerBlocked> blocked = new ArrayList<>();
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public int count;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$contacts_Blocked TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$contacts_Blocked tLRPC$contacts_Blocked;
        if (i != -NUM) {
            tLRPC$contacts_Blocked = i != NUM ? null : new TLRPC$TL_contacts_blocked();
        } else {
            tLRPC$contacts_Blocked = new TLRPC$TL_contacts_blockedSlice();
        }
        if (tLRPC$contacts_Blocked != null || !z) {
            if (tLRPC$contacts_Blocked != null) {
                tLRPC$contacts_Blocked.readParams(abstractSerializedData, z);
            }
            return tLRPC$contacts_Blocked;
        }
        throw new RuntimeException(String.format("can't parse magic %x in contacts_Blocked", new Object[]{Integer.valueOf(i)}));
    }
}
