package org.telegram.messenger.exoplayer2.upstream.cache;

import android.util.SparseArray;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.AtomicFile;
import org.telegram.messenger.exoplayer2.util.ReusableBufferedOutputStream;
import org.telegram.messenger.exoplayer2.util.Util;

class CachedContentIndex {
    public static final String FILE_NAME = "cached_content_index.exi";
    private static final int FLAG_ENCRYPTED_INDEX = 1;
    private static final String TAG = "CachedContentIndex";
    private static final int VERSION = 1;
    private final AtomicFile atomicFile;
    private ReusableBufferedOutputStream bufferedOutputStream;
    private boolean changed;
    private final Cipher cipher;
    private final boolean encrypt;
    private final SparseArray<String> idToKey;
    private final HashMap<String, CachedContent> keyToContent;
    private final SecretKeySpec secretKeySpec;

    public CachedContentIndex(File file) {
        this(file, null);
    }

    public CachedContentIndex(File file, byte[] bArr) {
        this(file, bArr, bArr != null);
    }

    public CachedContentIndex(File file, byte[] bArr, boolean z) {
        this.encrypt = z;
        boolean z2 = true;
        if (bArr != null) {
            if (!bArr.length) {
                z2 = false;
            }
            Assertions.checkArgument(z2);
            try {
                this.cipher = getCipher();
                this.secretKeySpec = new SecretKeySpec(bArr, "AES");
            } catch (File file2) {
                throw new IllegalStateException(file2);
            }
        }
        Assertions.checkState(z ^ 1);
        this.cipher = null;
        this.secretKeySpec = null;
        this.keyToContent = new HashMap();
        this.idToKey = new SparseArray();
        this.atomicFile = new AtomicFile(new File(file2, FILE_NAME));
    }

    public void load() {
        Assertions.checkState(this.changed ^ 1);
        if (!readFile()) {
            this.atomicFile.delete();
            this.keyToContent.clear();
            this.idToKey.clear();
        }
    }

    public void store() throws CacheException {
        if (this.changed) {
            writeFile();
            this.changed = false;
        }
    }

    public CachedContent getOrAdd(String str) {
        CachedContent cachedContent = (CachedContent) this.keyToContent.get(str);
        return cachedContent == null ? addNew(str, -1) : cachedContent;
    }

    public CachedContent get(String str) {
        return (CachedContent) this.keyToContent.get(str);
    }

    public Collection<CachedContent> getAll() {
        return this.keyToContent.values();
    }

    public int assignIdForKey(String str) {
        return getOrAdd(str).id;
    }

    public String getKeyForId(int i) {
        return (String) this.idToKey.get(i);
    }

    public void maybeRemove(String str) {
        CachedContent cachedContent = (CachedContent) this.keyToContent.get(str);
        if (cachedContent != null && cachedContent.isEmpty() && !cachedContent.isLocked()) {
            this.keyToContent.remove(str);
            this.idToKey.remove(cachedContent.id);
            this.changed = true;
        }
    }

    public void removeEmpty() {
        String[] strArr = new String[this.keyToContent.size()];
        this.keyToContent.keySet().toArray(strArr);
        for (String maybeRemove : strArr) {
            maybeRemove(maybeRemove);
        }
    }

    public Set<String> getKeys() {
        return this.keyToContent.keySet();
    }

    public void setContentLength(String str, long j) {
        CachedContent cachedContent = get(str);
        if (cachedContent == null) {
            addNew(str, j);
        } else if (cachedContent.getLength() != j) {
            cachedContent.setLength(j);
            this.changed = true;
        }
    }

    public long getContentLength(String str) {
        str = get(str);
        if (str == null) {
            return -1;
        }
        return str.getLength();
    }

    private boolean readFile() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r8 = this;
        r0 = 0;
        r1 = 0;
        r2 = new java.io.BufferedInputStream;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r3 = r8.atomicFile;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r3 = r3.openRead();	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r2.<init>(r3);	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r3 = new java.io.DataInputStream;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r3.<init>(r2);	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r1 = r3.readInt();	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r4 = 1;
        if (r1 == r4) goto L_0x001f;
    L_0x0019:
        if (r3 == 0) goto L_0x001e;
    L_0x001b:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r3);
    L_0x001e:
        return r0;
    L_0x001f:
        r1 = r3.readInt();	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r1 = r1 & r4;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        if (r1 == 0) goto L_0x0058;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
    L_0x0026:
        r1 = r8.cipher;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        if (r1 != 0) goto L_0x0030;
    L_0x002a:
        if (r3 == 0) goto L_0x002f;
    L_0x002c:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r3);
    L_0x002f:
        return r0;
    L_0x0030:
        r1 = 16;
        r1 = new byte[r1];	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r3.readFully(r1);	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r5 = new javax.crypto.spec.IvParameterSpec;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r5.<init>(r1);	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r1 = r8.cipher;	 Catch:{ InvalidKeyException -> 0x0051, InvalidKeyException -> 0x0051 }
        r6 = 2;	 Catch:{ InvalidKeyException -> 0x0051, InvalidKeyException -> 0x0051 }
        r7 = r8.secretKeySpec;	 Catch:{ InvalidKeyException -> 0x0051, InvalidKeyException -> 0x0051 }
        r1.init(r6, r7, r5);	 Catch:{ InvalidKeyException -> 0x0051, InvalidKeyException -> 0x0051 }
        r1 = new java.io.DataInputStream;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r5 = new javax.crypto.CipherInputStream;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r6 = r8.cipher;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r5.<init>(r2, r6);	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r1.<init>(r5);	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        goto L_0x005f;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
    L_0x0051:
        r1 = move-exception;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r2 = new java.lang.IllegalStateException;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        r2.<init>(r1);	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        throw r2;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
    L_0x0058:
        r1 = r8.encrypt;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
        if (r1 == 0) goto L_0x005e;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
    L_0x005c:
        r8.changed = r4;	 Catch:{ FileNotFoundException -> 0x00a6, IOException -> 0x0089 }
    L_0x005e:
        r1 = r3;
    L_0x005f:
        r2 = r1.readInt();	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r3 = r0;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r5 = r3;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
    L_0x0065:
        if (r3 >= r2) goto L_0x0077;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
    L_0x0067:
        r6 = new org.telegram.messenger.exoplayer2.upstream.cache.CachedContent;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r6.<init>(r1);	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r8.add(r6);	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r6 = r6.headerHashCode();	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r5 = r5 + r6;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        r3 = r3 + 1;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        goto L_0x0065;	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
    L_0x0077:
        r2 = r1.readInt();	 Catch:{ FileNotFoundException -> 0x00a5, IOException -> 0x008e, all -> 0x008b }
        if (r2 == r5) goto L_0x0083;
    L_0x007d:
        if (r1 == 0) goto L_0x0082;
    L_0x007f:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r1);
    L_0x0082:
        return r0;
    L_0x0083:
        if (r1 == 0) goto L_0x0088;
    L_0x0085:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r1);
    L_0x0088:
        return r4;
    L_0x0089:
        r1 = move-exception;
        goto L_0x0091;
    L_0x008b:
        r0 = move-exception;
        r3 = r1;
        goto L_0x009f;
    L_0x008e:
        r2 = move-exception;
        r3 = r1;
        r1 = r2;
    L_0x0091:
        r2 = "CachedContentIndex";	 Catch:{ all -> 0x009e }
        r4 = "Error reading cache content index file.";	 Catch:{ all -> 0x009e }
        android.util.Log.e(r2, r4, r1);	 Catch:{ all -> 0x009e }
        if (r3 == 0) goto L_0x009d;
    L_0x009a:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r3);
    L_0x009d:
        return r0;
    L_0x009e:
        r0 = move-exception;
    L_0x009f:
        if (r3 == 0) goto L_0x00a4;
    L_0x00a1:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r3);
    L_0x00a4:
        throw r0;
    L_0x00a5:
        r3 = r1;
    L_0x00a6:
        if (r3 == 0) goto L_0x00ab;
    L_0x00a8:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r3);
    L_0x00ab:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex.readFile():boolean");
    }

    private void writeFile() throws CacheException {
        Throwable e;
        Throwable th;
        Closeable dataOutputStream;
        try {
            OutputStream startWrite = this.atomicFile.startWrite();
            if (this.bufferedOutputStream == null) {
                this.bufferedOutputStream = new ReusableBufferedOutputStream(startWrite);
            } else {
                this.bufferedOutputStream.reset(startWrite);
            }
            dataOutputStream = new DataOutputStream(this.bufferedOutputStream);
            try {
                dataOutputStream.writeInt(1);
                dataOutputStream.writeInt(this.encrypt);
                if (this.encrypt) {
                    byte[] bArr = new byte[16];
                    new Random().nextBytes(bArr);
                    dataOutputStream.write(bArr);
                    this.cipher.init(1, this.secretKeySpec, new IvParameterSpec(bArr));
                    dataOutputStream.flush();
                    startWrite = new DataOutputStream(new CipherOutputStream(this.bufferedOutputStream, this.cipher));
                }
                startWrite.writeInt(this.keyToContent.size());
                int i = 0;
                for (CachedContent cachedContent : this.keyToContent.values()) {
                    cachedContent.writeToStream(startWrite);
                    i += cachedContent.headerHashCode();
                }
                startWrite.writeInt(i);
                this.atomicFile.endWrite(startWrite);
                Util.closeQuietly(null);
            } catch (Throwable e2) {
                throw new IllegalStateException(e2);
            } catch (IOException e3) {
                e2 = e3;
                try {
                    throw new CacheException(e2);
                } catch (Throwable th2) {
                    e2 = th2;
                    Util.closeQuietly(dataOutputStream);
                    throw e2;
                }
            }
        } catch (Throwable e4) {
            th = e4;
            dataOutputStream = null;
            e2 = th;
            throw new CacheException(e2);
        } catch (Throwable e42) {
            th = e42;
            dataOutputStream = null;
            e2 = th;
            Util.closeQuietly(dataOutputStream);
            throw e2;
        }
    }

    private void add(CachedContent cachedContent) {
        this.keyToContent.put(cachedContent.key, cachedContent);
        this.idToKey.put(cachedContent.id, cachedContent.key);
    }

    void addNew(CachedContent cachedContent) {
        add(cachedContent);
        this.changed = true;
    }

    private CachedContent addNew(String str, long j) {
        CachedContent cachedContent = new CachedContent(getNewId(this.idToKey), str, j);
        addNew(cachedContent);
        return cachedContent;
    }

    private static javax.crypto.Cipher getCipher() throws javax.crypto.NoSuchPaddingException, java.security.NoSuchAlgorithmException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = org.telegram.messenger.exoplayer2.util.Util.SDK_INT;
        r1 = 18;
        if (r0 != r1) goto L_0x000f;
    L_0x0006:
        r0 = "AES/CBC/PKCS5PADDING";	 Catch:{ Throwable -> 0x000f }
        r1 = "BC";	 Catch:{ Throwable -> 0x000f }
        r0 = javax.crypto.Cipher.getInstance(r0, r1);	 Catch:{ Throwable -> 0x000f }
        return r0;
    L_0x000f:
        r0 = "AES/CBC/PKCS5PADDING";
        r0 = javax.crypto.Cipher.getInstance(r0);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex.getCipher():javax.crypto.Cipher");
    }

    public static int getNewId(SparseArray<String> sparseArray) {
        int i;
        int size = sparseArray.size();
        if (size == 0) {
            i = 0;
        } else {
            i = sparseArray.keyAt(size - 1) + 1;
        }
        if (i < 0) {
            i = 0;
            while (i < size) {
                if (i != sparseArray.keyAt(i)) {
                    break;
                }
                i++;
            }
        }
        return i;
    }
}
