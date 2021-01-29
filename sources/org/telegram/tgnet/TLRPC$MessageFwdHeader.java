package org.telegram.tgnet;

public abstract class TLRPC$MessageFwdHeader extends TLObject {
    public int channel_post;
    public int date;
    public int flags;
    public TLRPC$Peer from_id;
    public String from_name;
    public String post_author;
    public String psa_type;
    public int saved_from_msg_id;
    public TLRPC$Peer saved_from_peer;

    public static TLRPC$MessageFwdHeader TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader;
        switch (i) {
            case -947462709:
                tLRPC$MessageFwdHeader = new TLRPC$TL_messageFwdHeader_layer68();
                break;
            case -332168592:
                tLRPC$MessageFwdHeader = new TLRPC$TL_messageFwdHeader_layer112();
                break;
            case -85986132:
                tLRPC$MessageFwdHeader = new TLRPC$TL_messageFwdHeader_layer72();
                break;
            case 893020267:
                tLRPC$MessageFwdHeader = new TLRPC$TL_messageFwdHeader_layer118();
                break;
            case 1436466797:
                tLRPC$MessageFwdHeader = new TLRPC$TL_messageFwdHeader_layer96();
                break;
            case 1601666510:
                tLRPC$MessageFwdHeader = new TLRPC$TL_messageFwdHeader();
                break;
            default:
                tLRPC$MessageFwdHeader = null;
                break;
        }
        if (tLRPC$MessageFwdHeader != null || !z) {
            if (tLRPC$MessageFwdHeader != null) {
                tLRPC$MessageFwdHeader.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessageFwdHeader;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageFwdHeader", new Object[]{Integer.valueOf(i)}));
    }
}
