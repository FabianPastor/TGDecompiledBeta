package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class SimpleCacheSpan
  extends CacheSpan
{
  private static final Pattern CACHE_FILE_PATTERN_V1 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v1\\.exo$", 32);
  private static final Pattern CACHE_FILE_PATTERN_V2 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v2\\.exo$", 32);
  private static final Pattern CACHE_FILE_PATTERN_V3 = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\.v3\\.exo$", 32);
  private static final String SUFFIX = ".v3.exo";
  
  private SimpleCacheSpan(String paramString, long paramLong1, long paramLong2, long paramLong3, File paramFile)
  {
    super(paramString, paramLong1, paramLong2, paramLong3, paramFile);
  }
  
  public static SimpleCacheSpan createCacheEntry(File paramFile, CachedContentIndex paramCachedContentIndex)
  {
    Object localObject1 = null;
    String str = paramFile.getName();
    Object localObject2 = str;
    File localFile = paramFile;
    if (!str.endsWith(".v3.exo"))
    {
      localFile = upgradeFile(paramFile, paramCachedContentIndex);
      if (localFile == null) {
        paramFile = (File)localObject1;
      }
    }
    for (;;)
    {
      return paramFile;
      localObject2 = localFile.getName();
      localObject2 = CACHE_FILE_PATTERN_V3.matcher((CharSequence)localObject2);
      paramFile = (File)localObject1;
      if (((Matcher)localObject2).matches())
      {
        long l = localFile.length();
        paramCachedContentIndex = paramCachedContentIndex.getKeyForId(Integer.parseInt(((Matcher)localObject2).group(1)));
        paramFile = (File)localObject1;
        if (paramCachedContentIndex != null) {
          paramFile = new SimpleCacheSpan(paramCachedContentIndex, Long.parseLong(((Matcher)localObject2).group(2)), l, Long.parseLong(((Matcher)localObject2).group(3)), localFile);
        }
      }
    }
  }
  
  public static SimpleCacheSpan createClosedHole(String paramString, long paramLong1, long paramLong2)
  {
    return new SimpleCacheSpan(paramString, paramLong1, paramLong2, -9223372036854775807L, null);
  }
  
  public static SimpleCacheSpan createLookup(String paramString, long paramLong)
  {
    return new SimpleCacheSpan(paramString, paramLong, -1L, -9223372036854775807L, null);
  }
  
  public static SimpleCacheSpan createOpenHole(String paramString, long paramLong)
  {
    return new SimpleCacheSpan(paramString, paramLong, -1L, -9223372036854775807L, null);
  }
  
  public static File getCacheFile(File paramFile, int paramInt, long paramLong1, long paramLong2)
  {
    return new File(paramFile, paramInt + "." + paramLong1 + "." + paramLong2 + ".v3.exo");
  }
  
  private static File upgradeFile(File paramFile, CachedContentIndex paramCachedContentIndex)
  {
    Object localObject1 = paramFile.getName();
    Object localObject2 = CACHE_FILE_PATTERN_V2.matcher((CharSequence)localObject1);
    if (((Matcher)localObject2).matches())
    {
      String str = Util.unescapeFileName(((Matcher)localObject2).group(1));
      localObject1 = str;
      if (str != null) {
        break label68;
      }
      paramCachedContentIndex = null;
    }
    for (;;)
    {
      return paramCachedContentIndex;
      localObject2 = CACHE_FILE_PATTERN_V1.matcher((CharSequence)localObject1);
      if (!((Matcher)localObject2).matches())
      {
        paramCachedContentIndex = null;
      }
      else
      {
        localObject1 = ((Matcher)localObject2).group(1);
        label68:
        localObject2 = getCacheFile(paramFile.getParentFile(), paramCachedContentIndex.assignIdForKey((String)localObject1), Long.parseLong(((Matcher)localObject2).group(2)), Long.parseLong(((Matcher)localObject2).group(3)));
        paramCachedContentIndex = (CachedContentIndex)localObject2;
        if (!paramFile.renameTo((File)localObject2)) {
          paramCachedContentIndex = null;
        }
      }
    }
  }
  
  public SimpleCacheSpan copyWithUpdatedLastAccessTime(int paramInt)
  {
    Assertions.checkState(this.isCached);
    long l = System.currentTimeMillis();
    File localFile = getCacheFile(this.file.getParentFile(), paramInt, this.position, l);
    return new SimpleCacheSpan(this.key, this.position, this.length, l, localFile);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/SimpleCacheSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */