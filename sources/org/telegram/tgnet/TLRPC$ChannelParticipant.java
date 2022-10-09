package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$ChannelParticipant extends TLObject {
    public TLRPC$TL_chatAdminRights admin_rights;
    public TLRPC$TL_channelAdminRights_layer92 admin_rights_layer92;
    public TLRPC$TL_chatBannedRights banned_rights;
    public TLRPC$TL_channelBannedRights_layer92 banned_rights_layer92;
    public boolean can_edit;
    public int date;
    public int flags;
    public long inviter_id;
    public long kicked_by;
    public boolean left;
    public TLRPC$Peer peer;
    public long promoted_by;
    public String rank;
    public boolean self;
    public long user_id;
    public boolean via_invite;

    public static TLRPC$ChannelParticipant TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
        switch (i) {
            case -2138237532:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantCreator() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantCreator_layer118
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantCreator, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 1) != 0) {
                            this.rank = abstractSerializedData2.readString(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantCreator, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeString(this.rank);
                        }
                    }
                };
                break;
            case -1933187430:
                tLRPC$ChannelParticipant = new TLRPC$ChannelParticipant() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantKicked_layer67
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.kicked_by = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        abstractSerializedData2.writeInt32((int) this.kicked_by);
                        abstractSerializedData2.writeInt32(this.date);
                    }
                };
                break;
            case -1861910545:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantModerator_layer67
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.inviter_id = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        abstractSerializedData2.writeInt32((int) this.inviter_id);
                        abstractSerializedData2.writeInt32(this.date);
                    }
                };
                break;
            case -1743180447:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantEditor_layer67
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.inviter_id = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        abstractSerializedData2.writeInt32((int) this.inviter_id);
                        abstractSerializedData2.writeInt32(this.date);
                    }
                };
                break;
            case -1557620115:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantSelf() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantSelf_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantSelf, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.inviter_id = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantSelf, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        abstractSerializedData2.writeInt32((int) this.inviter_id);
                        abstractSerializedData2.writeInt32(this.date);
                    }
                };
                break;
            case -1473271656:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin_layer92
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        if ((readInt32 & 1) == 0) {
                            z3 = false;
                        }
                        this.can_edit = z3;
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.inviter_id = abstractSerializedData2.readInt32(z2);
                        this.promoted_by = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        TLRPC$TL_channelAdminRights_layer92 TLdeserialize = TLRPC$TL_channelAdminRights_layer92.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.admin_rights_layer92 = TLdeserialize;
                        this.admin_rights = TLRPC$Chat.mergeAdminRights(TLdeserialize);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.can_edit ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        abstractSerializedData2.writeInt32((int) this.inviter_id);
                        abstractSerializedData2.writeInt32((int) this.promoted_by);
                        abstractSerializedData2.writeInt32(this.date);
                        this.admin_rights_layer92.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case -1072953408:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipant();
                break;
            case -1010402965:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantLeft() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantLeft_layer125
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantLeft, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantLeft, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                    }
                };
                break;
            case -859915345:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.can_edit = (readInt32 & 1) != 0;
                        if ((readInt32 & 2) != 0) {
                            z3 = true;
                        }
                        this.self = z3;
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 2) != 0) {
                            this.inviter_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.promoted_by = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.rank = abstractSerializedData2.readString(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.can_edit ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.self ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        abstractSerializedData2.writeInt32(i3);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeInt32((int) this.inviter_id);
                        }
                        abstractSerializedData2.writeInt32((int) this.promoted_by);
                        abstractSerializedData2.writeInt32(this.date);
                        this.admin_rights.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            abstractSerializedData2.writeString(this.rank);
                        }
                    }
                };
                break;
            case -471670279:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantCreator() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantCreator_layer103
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantCreator, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantCreator, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                    }
                };
                break;
            case 367766557:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipant() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipant_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipant, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipant, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        abstractSerializedData2.writeInt32(this.date);
                    }
                };
                break;
            case 453242886:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantLeft();
                break;
            case 470789295:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantBanned() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantBanned_layer125
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantBanned, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        if ((readInt32 & 1) == 0) {
                            z3 = false;
                        }
                        this.left = z3;
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.kicked_by = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantBanned, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.left ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        abstractSerializedData2.writeInt32((int) this.kicked_by);
                        abstractSerializedData2.writeInt32(this.date);
                        this.banned_rights.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case 573315206:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantBanned() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantBanned_layer92
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantBanned, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        if ((readInt32 & 1) == 0) {
                            z3 = false;
                        }
                        this.left = z3;
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.kicked_by = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        TLRPC$TL_channelBannedRights_layer92 TLdeserialize = TLRPC$TL_channelBannedRights_layer92.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.banned_rights_layer92 = TLdeserialize;
                        this.banned_rights = TLRPC$Chat.mergeBannedRights(TLdeserialize);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantBanned, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.left ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        abstractSerializedData2.writeInt32((int) this.kicked_by);
                        abstractSerializedData2.writeInt32(this.date);
                        this.banned_rights_layer92.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case 682146919:
                tLRPC$ChannelParticipant = new TLRPC$ChannelParticipant() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantSelf_layer133
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt64(z2);
                        this.inviter_id = abstractSerializedData2.readInt64(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt64(this.peer.user_id);
                        abstractSerializedData2.writeInt64(this.inviter_id);
                        abstractSerializedData2.writeInt32(this.date);
                    }
                };
                break;
            case 803602899:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantCreator();
                break;
            case 885242707:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin();
                break;
            case 900251559:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantSelf();
                break;
            case 1149094475:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantCreator() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantCreator_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantCreator, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 1) != 0) {
                            this.rank = abstractSerializedData2.readString(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantCreator, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        this.admin_rights.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeString(this.rank);
                        }
                    }
                };
                break;
            case 1352785878:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantBanned() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantBanned_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantBanned, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        if ((readInt32 & 1) == 0) {
                            z3 = false;
                        }
                        this.left = z3;
                        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.kicked_by = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantBanned, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.left ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        this.peer.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32((int) this.kicked_by);
                        abstractSerializedData2.writeInt32(this.date);
                        this.banned_rights.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case 1571450403:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin_layer103
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.can_edit = (readInt32 & 1) != 0;
                        if ((readInt32 & 2) != 0) {
                            z3 = true;
                        }
                        this.self = z3;
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        this.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 2) != 0) {
                            this.inviter_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.promoted_by = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.can_edit ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.self ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        abstractSerializedData2.writeInt32(i3);
                        abstractSerializedData2.writeInt32((int) this.peer.user_id);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeInt32((int) this.inviter_id);
                        }
                        abstractSerializedData2.writeInt32((int) this.promoted_by);
                        abstractSerializedData2.writeInt32(this.date);
                        this.admin_rights.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case 1844969806:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantBanned();
                break;
            default:
                tLRPC$ChannelParticipant = null;
                break;
        }
        if (tLRPC$ChannelParticipant != null || !z) {
            if (tLRPC$ChannelParticipant != null) {
                tLRPC$ChannelParticipant.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChannelParticipant;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChannelParticipant", Integer.valueOf(i)));
    }
}
