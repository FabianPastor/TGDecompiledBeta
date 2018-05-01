package org.telegram.messenger.exoplayer.upstream.cache;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.util.Util;

public final class CacheSpan
  implements Comparable<CacheSpan>
{
  private static final Pattern CACHE_FILE_PATTERN_V1 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v1\\.exo$");
  private static final Pattern CACHE_FILE_PATTERN_V2 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v2\\.exo$");
  private static final String SUFFIX = ".v2.exo";
  public final File file;
  public final boolean isCached;
  public final String key;
  public final long lastAccessTimestamp;
  public final long length;
  public final long position;
  
  CacheSpan(String paramString, long paramLong1, long paramLong2, boolean paramBoolean, long paramLong3, File paramFile)
  {
    this.key = paramString;
    this.position = paramLong1;
    this.length = paramLong2;
    this.isCached = paramBoolean;
    this.file = paramFile;
    this.lastAccessTimestamp = paramLong3;
  }
  
  public static CacheSpan createCacheEntry(File paramFile)
  {
    Matcher localMatcher = CACHE_FILE_PATTERN_V2.matcher(paramFile.getName());
    if (!localMatcher.matches()) {}
    String str;
    do
    {
      return null;
      str = Util.unescapeFileName(localMatcher.group(1));
    } while (str == null);
    return createCacheEntry(str, Long.parseLong(localMatcher.group(2)), Long.parseLong(localMatcher.group(3)), paramFile);
  }
  
  private static CacheSpan createCacheEntry(String paramString, long paramLong1, long paramLong2, File paramFile)
  {
    return new CacheSpan(paramString, paramLong1, paramFile.length(), true, paramLong2, paramFile);
  }
  
  public static CacheSpan createClosedHole(String paramString, long paramLong1, long paramLong2)
  {
    return new CacheSpan(paramString, paramLong1, paramLong2, false, -1L, null);
  }
  
  public static CacheSpan createLookup(String paramString, long paramLong)
  {
    return new CacheSpan(paramString, paramLong, -1L, false, -1L, null);
  }
  
  public static CacheSpan createOpenHole(String paramString, long paramLong)
  {
    return new CacheSpan(paramString, paramLong, -1L, false, -1L, null);
  }
  
  public static File getCacheFileName(File paramFile, String paramString, long paramLong1, long paramLong2)
  {
    return new File(paramFile, Util.escapeFileName(paramString) + "." + paramLong1 + "." + paramLong2 + ".v2.exo");
  }
  
  static File upgradeIfNeeded(File paramFile)
  {
    Object localObject = CACHE_FILE_PATTERN_V1.matcher(paramFile.getName());
    if (!((Matcher)localObject).matches()) {
      return paramFile;
    }
    String str = ((Matcher)localObject).group(1);
    localObject = getCacheFileName(paramFile.getParentFile(), str, Long.parseLong(((Matcher)localObject).group(2)), Long.parseLong(((Matcher)localObject).group(3)));
    paramFile.renameTo((File)localObject);
    return (File)localObject;
  }
  
  public int compareTo(CacheSpan paramCacheSpan)
  {
    if (!this.key.equals(paramCacheSpan.key)) {
      return this.key.compareTo(paramCacheSpan.key);
    }
    long l = this.position - paramCacheSpan.position;
    if (l == 0L) {
      return 0;
    }
    if (l < 0L) {
      return -1;
    }
    return 1;
  }
  
  public boolean isOpenEnded()
  {
    return this.length == -1L;
  }
  
  public CacheSpan touch()
  {
    long l = System.currentTimeMillis();
    File localFile = getCacheFileName(this.file.getParentFile(), this.key, this.position, l);
    this.file.renameTo(localFile);
    return createCacheEntry(this.key, this.position, l, localFile);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/cache/CacheSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */