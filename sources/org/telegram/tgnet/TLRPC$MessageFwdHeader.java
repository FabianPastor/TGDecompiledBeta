package org.telegram.tgnet;

public abstract class TLRPC$MessageFwdHeader extends TLObject {
    public int channel_id;
    public int channel_post;
    public int date;
    public int flags;
    public int from_id;
    public String from_name;
    public String post_author;
    public String psa_type;
    public int saved_from_msg_id;
    public TLRPC$Peer saved_from_peer;

    public static TLRPC$MessageFwdHeader TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_messageFwdHeader tLRPC$TL_messageFwdHeader;
        switch (i) {
            case -947462709:
                tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader_layer68();
                break;
            case -332168592:
                tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader_layer112();
                break;
            case -85986132:
                tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader_layer72();
                break;
            case 893020267:
                tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader();
                break;
            case 1436466797:
                tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader_layer96();
                break;
            default:
                tLRPC$TL_messageFwdHeader = null;
                break;
        }
        if (tLRPC$TL_messageFwdHeader != null || !z) {
            if (tLRPC$TL_messageFwdHeader != null) {
                tLRPC$TL_messageFwdHeader.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messageFwdHeader;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageFwdHeader", new Object[]{Integer.valueOf(i)}));
    }
}
