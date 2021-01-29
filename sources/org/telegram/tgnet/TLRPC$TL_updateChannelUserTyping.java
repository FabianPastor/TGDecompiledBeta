package org.telegram.tgnet;

public class TLRPC$TL_updateChannelUserTyping extends TLRPC$Update {
    public static int constructor = -13975905;
    public TLRPC$SendMessageAction action;
    public int channel_id;
    public int flags;
    public int top_msg_id;
    public int user_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.channel_id = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.top_msg_id = abstractSerializedData.readInt32(z);
        }
        this.user_id = abstractSerializedData.readInt32(z);
        this.action = TLRPC$SendMessageAction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.channel_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.top_msg_id);
        }
        abstractSerializedData.writeInt32(this.user_id);
        this.action.serializeToStream(abstractSerializedData);
    }
}
