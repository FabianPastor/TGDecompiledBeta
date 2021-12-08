package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$messages_SavedGifs extends TLObject {
    public ArrayList<TLRPC$Document> gifs = new ArrayList<>();
    public long hash;

    public static TLRPC$messages_SavedGifs TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_SavedGifs tLRPC$messages_SavedGifs;
        if (i != -NUM) {
            tLRPC$messages_SavedGifs = i != -NUM ? null : new TLRPC$TL_messages_savedGifsNotModified();
        } else {
            tLRPC$messages_SavedGifs = new TLRPC$TL_messages_savedGifs();
        }
        if (tLRPC$messages_SavedGifs != null || !z) {
            if (tLRPC$messages_SavedGifs != null) {
                tLRPC$messages_SavedGifs.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_SavedGifs;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_SavedGifs", new Object[]{Integer.valueOf(i)}));
    }
}
