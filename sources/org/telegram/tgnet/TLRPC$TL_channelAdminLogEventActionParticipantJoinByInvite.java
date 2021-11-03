package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionParticipantJoinByInvite extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public TLRPC$TL_chatInviteExported invite;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.invite = TLRPC$ExportedChatInvite.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.invite.serializeToStream(abstractSerializedData);
    }
}
