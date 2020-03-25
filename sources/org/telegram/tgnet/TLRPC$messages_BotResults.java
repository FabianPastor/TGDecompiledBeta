package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$messages_BotResults extends TLObject {
    public int cache_time;
    public int flags;
    public boolean gallery;
    public String next_offset;
    public long query_id;
    public ArrayList<TLRPC$BotInlineResult> results = new ArrayList<>();
    public TLRPC$TL_inlineBotSwitchPM switch_pm;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$messages_BotResults TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_messages_botResults tLRPC$TL_messages_botResults;
        if (i != -NUM) {
            tLRPC$TL_messages_botResults = i != -NUM ? null : new TLRPC$TL_messages_botResults_layer71();
        } else {
            tLRPC$TL_messages_botResults = new TLRPC$TL_messages_botResults();
        }
        if (tLRPC$TL_messages_botResults != null || !z) {
            if (tLRPC$TL_messages_botResults != null) {
                tLRPC$TL_messages_botResults.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messages_botResults;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_BotResults", new Object[]{Integer.valueOf(i)}));
    }
}
