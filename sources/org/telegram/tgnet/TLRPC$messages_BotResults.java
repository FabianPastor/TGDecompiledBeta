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
        TLRPC$messages_BotResults tLRPC$messages_BotResults;
        if (i != -NUM) {
            tLRPC$messages_BotResults = i != -NUM ? null : new TLRPC$TL_messages_botResults_layer71();
        } else {
            tLRPC$messages_BotResults = new TLRPC$TL_messages_botResults();
        }
        if (tLRPC$messages_BotResults != null || !z) {
            if (tLRPC$messages_BotResults != null) {
                tLRPC$messages_BotResults.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_BotResults;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_BotResults", new Object[]{Integer.valueOf(i)}));
    }
}
