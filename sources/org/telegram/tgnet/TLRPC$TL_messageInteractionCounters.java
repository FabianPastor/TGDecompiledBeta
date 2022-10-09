package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messageInteractionCounters extends TLObject {
    public static int constructor = -NUM;
    public int forwards;
    public int msg_id;
    public int views;

    public static TLRPC$TL_messageInteractionCounters TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_messageInteractionCounters", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_messageInteractionCounters tLRPC$TL_messageInteractionCounters = new TLRPC$TL_messageInteractionCounters();
        tLRPC$TL_messageInteractionCounters.readParams(abstractSerializedData, z);
        return tLRPC$TL_messageInteractionCounters;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.msg_id = abstractSerializedData.readInt32(z);
        this.views = abstractSerializedData.readInt32(z);
        this.forwards = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.msg_id);
        abstractSerializedData.writeInt32(this.views);
        abstractSerializedData.writeInt32(this.forwards);
    }
}
