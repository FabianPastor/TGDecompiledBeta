package org.telegram.tgnet;

public class TLRPC$TL_messageViews extends TLObject {
    public static int constructor = NUM;
    public int flags;
    public int forwards;
    public TLRPC$TL_messageReplies replies;
    public int views;

    public static TLRPC$TL_messageViews TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messageViews tLRPC$TL_messageViews = new TLRPC$TL_messageViews();
            tLRPC$TL_messageViews.readParams(abstractSerializedData, z);
            return tLRPC$TL_messageViews;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messageViews", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.views = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 2) != 0) {
            this.forwards = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 4) != 0) {
            this.replies = TLRPC$TL_messageReplies.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.views);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.forwards);
        }
        if ((this.flags & 4) != 0) {
            this.replies.serializeToStream(abstractSerializedData);
        }
    }
}
