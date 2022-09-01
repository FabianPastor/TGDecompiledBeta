package org.telegram.tgnet;

public class TLRPC$TL_reactionCount extends TLRPC$ReactionCount {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.chosen = z2;
        if (z2) {
            this.chosen_order = abstractSerializedData.readInt32(z);
        }
        this.reaction = TLRPC$Reaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.count = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.chosen_order);
        }
        this.reaction.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.count);
    }
}
