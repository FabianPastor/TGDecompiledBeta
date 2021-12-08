package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class SecureDocument extends TLObject {
    public byte[] fileHash;
    public byte[] fileSecret;
    public TLRPC.TL_inputFile inputFile;
    public String path;
    public SecureDocumentKey secureDocumentKey;
    public TLRPC.TL_secureFile secureFile;
    public int type;

    public SecureDocument(SecureDocumentKey key, TLRPC.TL_secureFile file, String p, byte[] fh, byte[] secret) {
        this.secureDocumentKey = key;
        this.secureFile = file;
        this.path = p;
        this.fileHash = fh;
        this.fileSecret = secret;
    }
}
