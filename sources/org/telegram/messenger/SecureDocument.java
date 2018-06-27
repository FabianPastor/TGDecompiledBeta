package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_inputFile;
import org.telegram.tgnet.TLRPC.TL_secureFile;

public class SecureDocument extends TLObject {
    public byte[] fileHash;
    public byte[] fileSecret;
    public TL_inputFile inputFile;
    public String path;
    public SecureDocumentKey secureDocumentKey;
    public TL_secureFile secureFile;

    public SecureDocument(SecureDocumentKey key, TL_secureFile file, String p, byte[] fh, byte[] secret) {
        this.secureDocumentKey = key;
        this.secureFile = file;
        this.path = p;
        this.fileHash = fh;
        this.fileSecret = secret;
    }
}
