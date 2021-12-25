package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_setChatAvailableReactions extends TLObject {
    public static int constructor = NUM;
    public ArrayList<String> available_reactions = new ArrayList<>();
    public TLRPC$InputPeer peer;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.available_reactions.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeString(this.available_reactions.get(i));
        }
    }
}
