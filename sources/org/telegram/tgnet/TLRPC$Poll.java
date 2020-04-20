package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$Poll extends TLObject {
    public ArrayList<TLRPC$TL_pollAnswer> answers = new ArrayList<>();
    public int close_date;
    public int close_period;
    public boolean closed;
    public int flags;
    public long id;
    public boolean multiple_choice;
    public boolean public_voters;
    public String question;
    public boolean quiz;

    public static TLRPC$Poll TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_poll tLRPC$TL_poll;
        if (i == -NUM) {
            tLRPC$TL_poll = new TLRPC$TL_poll();
        } else if (i != -NUM) {
            tLRPC$TL_poll = i != -NUM ? null : new TLRPC$TL_poll_layer111();
        } else {
            tLRPC$TL_poll = new TLRPC$TL_poll_toDelete();
        }
        if (tLRPC$TL_poll != null || !z) {
            if (tLRPC$TL_poll != null) {
                tLRPC$TL_poll.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_poll;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Poll", new Object[]{Integer.valueOf(i)}));
    }
}
