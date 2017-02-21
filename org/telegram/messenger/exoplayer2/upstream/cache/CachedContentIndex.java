package org.telegram.messenger.exoplayer2.upstream.cache;

import android.util.SparseArray;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.AtomicFile;
import org.telegram.messenger.exoplayer2.util.ReusableBufferedOutputStream;
import org.telegram.messenger.exoplayer2.util.Util;

final class CachedContentIndex {
    public static final String FILE_NAME = "cached_content_index.exi";
    private static final int FLAG_ENCRYPTED_INDEX = 1;
    private static final int VERSION = 1;
    private final AtomicFile atomicFile;
    private ReusableBufferedOutputStream bufferedOutputStream;
    private boolean changed;
    private final Cipher cipher;
    private final SparseArray<String> idToKey;
    private final HashMap<String, CachedContent> keyToContent;
    private final SecretKeySpec secretKeySpec;

    public CachedContentIndex(File cacheDir) {
        this(cacheDir, null);
    }

    public CachedContentIndex(File cacheDir, byte[] secretKey) {
        GeneralSecurityException e;
        if (secretKey != null) {
            try {
                this.cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                this.secretKeySpec = new SecretKeySpec(secretKey, "AES");
            } catch (NoSuchAlgorithmException e2) {
                e = e2;
                throw new IllegalStateException(e);
            } catch (NoSuchPaddingException e3) {
                e = e3;
                throw new IllegalStateException(e);
            }
        }
        this.cipher = null;
        this.secretKeySpec = null;
        this.keyToContent = new HashMap();
        this.idToKey = new SparseArray();
        this.atomicFile = new AtomicFile(new File(cacheDir, FILE_NAME));
    }

    public void load() {
        Assertions.checkState(!this.changed);
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

    public CachedContent add(String key) {
        CachedContent cachedContent = (CachedContent) this.keyToContent.get(key);
        if (cachedContent == null) {
            return addNew(key, -1);
        }
        return cachedContent;
    }

    public CachedContent get(String key) {
        return (CachedContent) this.keyToContent.get(key);
    }

    public Collection<CachedContent> getAll() {
        return this.keyToContent.values();
    }

    public int assignIdForKey(String key) {
        return add(key).id;
    }

    public String getKeyForId(int id) {
        return (String) this.idToKey.get(id);
    }

    public void removeEmpty(String key) {
        CachedContent cachedContent = (CachedContent) this.keyToContent.remove(key);
        if (cachedContent != null) {
            Assertions.checkState(cachedContent.isEmpty());
            this.idToKey.remove(cachedContent.id);
            this.changed = true;
        }
    }

    public void removeEmpty() {
        LinkedList<String> cachedContentToBeRemoved = new LinkedList();
        for (CachedContent cachedContent : this.keyToContent.values()) {
            if (cachedContent.isEmpty()) {
                cachedContentToBeRemoved.add(cachedContent.key);
            }
        }
        Iterator it = cachedContentToBeRemoved.iterator();
        while (it.hasNext()) {
            removeEmpty((String) it.next());
        }
    }

    public Set<String> getKeys() {
        return this.keyToContent.keySet();
    }

    public void setContentLength(String key, long length) {
        CachedContent cachedContent = get(key);
        if (cachedContent == null) {
            addNew(key, length);
        } else if (cachedContent.getLength() != length) {
            cachedContent.setLength(length);
            this.changed = true;
        }
    }

    public long getContentLength(String key) {
        CachedContent cachedContent = get(key);
        return cachedContent == null ? -1 : cachedContent.getLength();
    }

    private boolean readFile() {
        GeneralSecurityException e;
        Throwable th;
        Closeable closeable = null;
        try {
            InputStream inputStream = new BufferedInputStream(this.atomicFile.openRead());
            Closeable input = new DataInputStream(inputStream);
            try {
                if (input.readInt() != 1) {
                    if (input != null) {
                        Util.closeQuietly(input);
                    }
                    closeable = input;
                    return false;
                }
                if ((input.readInt() & 1) == 0) {
                    closeable = input;
                } else if (this.cipher == null) {
                    if (input != null) {
                        Util.closeQuietly(input);
                    }
                    closeable = input;
                    return false;
                } else {
                    byte[] initializationVector = new byte[16];
                    input.read(initializationVector);
                    try {
                        this.cipher.init(2, this.secretKeySpec, new IvParameterSpec(initializationVector));
                        closeable = new DataInputStream(new CipherInputStream(inputStream, this.cipher));
                    } catch (GeneralSecurityException e2) {
                        e = e2;
                        throw new IllegalStateException(e);
                    } catch (GeneralSecurityException e22) {
                        e = e22;
                        throw new IllegalStateException(e);
                    }
                }
                int count = closeable.readInt();
                int hashCode = 0;
                for (int i = 0; i < count; i++) {
                    CachedContent cachedContent = new CachedContent(closeable);
                    addNew(cachedContent);
                    hashCode += cachedContent.headerHashCode();
                }
                if (closeable.readInt() == hashCode) {
                    if (closeable != null) {
                        Util.closeQuietly(closeable);
                    }
                    return true;
                } else if (closeable == null) {
                    return false;
                } else {
                    Util.closeQuietly(closeable);
                    return false;
                }
            } catch (IOException e3) {
                closeable = input;
                if (closeable != null) {
                    return false;
                }
                Util.closeQuietly(closeable);
                return false;
            } catch (Throwable th2) {
                th = th2;
                closeable = input;
                if (closeable != null) {
                    Util.closeQuietly(closeable);
                }
                throw th;
            }
        } catch (IOException e4) {
            if (closeable != null) {
                return false;
            }
            Util.closeQuietly(closeable);
            return false;
        } catch (Throwable th3) {
            th = th3;
            if (closeable != null) {
                Util.closeQuietly(closeable);
            }
            throw th;
        }
    }

    private void writeFile() throws CacheException {
        GeneralSecurityException e;
        Object output;
        IOException e2;
        Throwable th;
        int flags = 1;
        Closeable closeable = null;
        try {
            OutputStream outputStream = this.atomicFile.startWrite();
            if (this.bufferedOutputStream == null) {
                this.bufferedOutputStream = new ReusableBufferedOutputStream(outputStream);
            } else {
                this.bufferedOutputStream.reset(outputStream);
            }
            DataOutputStream output2 = new DataOutputStream(this.bufferedOutputStream);
            try {
                output2.writeInt(1);
                if (this.cipher == null) {
                    flags = 0;
                }
                output2.writeInt(flags);
                if (this.cipher != null) {
                    byte[] initializationVector = new byte[16];
                    new Random().nextBytes(initializationVector);
                    output2.write(initializationVector);
                    try {
                        this.cipher.init(1, this.secretKeySpec, new IvParameterSpec(initializationVector));
                        output2.flush();
                        closeable = new DataOutputStream(new CipherOutputStream(this.bufferedOutputStream, this.cipher));
                    } catch (GeneralSecurityException e3) {
                        e = e3;
                        throw new IllegalStateException(e);
                    } catch (GeneralSecurityException e32) {
                        e = e32;
                        throw new IllegalStateException(e);
                    }
                }
                output = output2;
                closeable.writeInt(this.keyToContent.size());
                int hashCode = 0;
                for (CachedContent cachedContent : this.keyToContent.values()) {
                    cachedContent.writeToStream(closeable);
                    hashCode += cachedContent.headerHashCode();
                }
                closeable.writeInt(hashCode);
                this.atomicFile.endWrite(closeable);
                Util.closeQuietly(closeable);
            } catch (IOException e4) {
                e2 = e4;
                output = output2;
                try {
                    throw new CacheException(e2);
                } catch (Throwable th2) {
                    th = th2;
                    Util.closeQuietly(closeable);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                output = output2;
                Util.closeQuietly(closeable);
                throw th;
            }
        } catch (IOException e5) {
            e2 = e5;
            throw new CacheException(e2);
        }
    }

    void addNew(CachedContent cachedContent) {
        this.keyToContent.put(cachedContent.key, cachedContent);
        this.idToKey.put(cachedContent.id, cachedContent.key);
        this.changed = true;
    }

    private CachedContent addNew(String key, long length) {
        CachedContent cachedContent = new CachedContent(getNewId(this.idToKey), key, length);
        addNew(cachedContent);
        return cachedContent;
    }

    public static int getNewId(SparseArray<String> idToKey) {
        int size = idToKey.size();
        int id = size == 0 ? 0 : idToKey.keyAt(size - 1) + 1;
        if (id < 0) {
            id = 0;
            while (id < size && id == idToKey.keyAt(id)) {
                id++;
            }
        }
        return id;
    }
}
