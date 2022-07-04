package org.telegram.tgnet;

public class TLRPC$TL_messageMediaPoll extends TLRPC$MessageMedia {
    public static int constructor = NUM;
    public TLRPC$Poll poll;
    public TLRPC$PollResults results;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.poll = TLRPC$Poll.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.results = TLRPC$PollResults.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.poll.serializeToStream(abstractSerializedData);
        this.results.serializeToStream(abstractSerializedData);
    }
}
