package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionExportedInviteEdit extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = -NUM;
    public TLRPC$TL_chatInviteExported new_invite;
    public TLRPC$TL_chatInviteExported prev_invite;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_invite = TLRPC$ExportedChatInvite.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.new_invite = TLRPC$ExportedChatInvite.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.prev_invite.serializeToStream(abstractSerializedData);
        this.new_invite.serializeToStream(abstractSerializedData);
    }
}
