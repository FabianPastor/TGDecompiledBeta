package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updateMessagePoll extends TLRPC$Update {
    public static int constructor = -NUM;
    public int flags;
    public TLRPC$Poll poll;
    public long poll_id;
    public TLRPC$PollResults results;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.poll_id = abstractSerializedData.readInt64(z);
        if ((this.flags & 1) != 0) {
            this.poll = TLRPC$Poll.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.results = TLRPC$PollResults.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt64(this.poll_id);
        if ((this.flags & 1) != 0) {
            this.poll.serializeToStream(abstractSerializedData);
        }
        this.results.serializeToStream(abstractSerializedData);
    }
}
