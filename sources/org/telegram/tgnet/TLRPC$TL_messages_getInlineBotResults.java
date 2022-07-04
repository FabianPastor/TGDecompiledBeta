package org.telegram.tgnet;

public class TLRPC$TL_messages_getInlineBotResults extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputUser bot;
    public int flags;
    public TLRPC$InputGeoPoint geo_point;
    public String offset;
    public TLRPC$InputPeer peer;
    public String query;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_BotResults.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.bot.serializeToStream(abstractSerializedData);
        this.peer.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            this.geo_point.serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.query);
        abstractSerializedData.writeString(this.offset);
    }
}
