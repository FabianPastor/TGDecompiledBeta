package android.support.v4.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

public class FileProvider
  extends ContentProvider
{
  private static final String[] COLUMNS = { "_display_name", "_size" };
  private static final File DEVICE_ROOT = new File("/");
  private static HashMap<String, PathStrategy> sCache = new HashMap();
  private PathStrategy mStrategy;
  
  private static File buildPath(File paramFile, String... paramVarArgs)
  {
    int i = paramVarArgs.length;
    int j = 0;
    if (j < i)
    {
      String str = paramVarArgs[j];
      if (str == null) {
        break label39;
      }
      paramFile = new File(paramFile, str);
    }
    label39:
    for (;;)
    {
      j++;
      break;
      return paramFile;
    }
  }
  
  private static Object[] copyOf(Object[] paramArrayOfObject, int paramInt)
  {
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, paramInt);
    return arrayOfObject;
  }
  
  private static String[] copyOf(String[] paramArrayOfString, int paramInt)
  {
    String[] arrayOfString = new String[paramInt];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramInt);
    return arrayOfString;
  }
  
  private static PathStrategy getPathStrategy(Context paramContext, String paramString)
  {
    PathStrategy localPathStrategy2;
    synchronized (sCache)
    {
      PathStrategy localPathStrategy1 = (PathStrategy)sCache.get(paramString);
      localPathStrategy2 = localPathStrategy1;
      if (localPathStrategy1 != null) {}
    }
    try
    {
      localPathStrategy2 = parsePathStrategy(paramContext, paramString);
      sCache.put(paramString, localPathStrategy2);
      return localPathStrategy2;
    }
    catch (IOException paramContext)
    {
      paramString = new java/lang/IllegalArgumentException;
      paramString.<init>("Failed to parse android.support.FILE_PROVIDER_PATHS meta-data", paramContext);
      throw paramString;
      paramContext = finally;
      throw paramContext;
    }
    catch (XmlPullParserException paramContext)
    {
      paramString = new java/lang/IllegalArgumentException;
      paramString.<init>("Failed to parse android.support.FILE_PROVIDER_PATHS meta-data", paramContext);
      throw paramString;
    }
  }
  
  public static Uri getUriForFile(Context paramContext, String paramString, File paramFile)
  {
    return getPathStrategy(paramContext, paramString).getUriForFile(paramFile);
  }
  
  private static int modeToMode(String paramString)
  {
    int i;
    if ("r".equals(paramString)) {
      i = 268435456;
    }
    for (;;)
    {
      return i;
      if (("w".equals(paramString)) || ("wt".equals(paramString)))
      {
        i = 738197504;
      }
      else if ("wa".equals(paramString))
      {
        i = 704643072;
      }
      else if ("rw".equals(paramString))
      {
        i = 939524096;
      }
      else
      {
        if (!"rwt".equals(paramString)) {
          break;
        }
        i = NUM;
      }
    }
    throw new IllegalArgumentException("Invalid mode: " + paramString);
  }
  
  private static PathStrategy parsePathStrategy(Context paramContext, String paramString)
    throws IOException, XmlPullParserException
  {
    SimplePathStrategy localSimplePathStrategy = new SimplePathStrategy(paramString);
    XmlResourceParser localXmlResourceParser = paramContext.getPackageManager().resolveContentProvider(paramString, 128).loadXmlMetaData(paramContext.getPackageManager(), "android.support.FILE_PROVIDER_PATHS");
    if (localXmlResourceParser == null) {
      throw new IllegalArgumentException("Missing android.support.FILE_PROVIDER_PATHS meta-data");
    }
    label302:
    for (;;)
    {
      int i = localXmlResourceParser.next();
      if (i == 1) {
        break;
      }
      if (i == 2)
      {
        Object localObject1 = localXmlResourceParser.getName();
        String str1 = localXmlResourceParser.getAttributeValue(null, "name");
        String str2 = localXmlResourceParser.getAttributeValue(null, "path");
        Object localObject2 = null;
        if ("root-path".equals(localObject1)) {
          paramString = DEVICE_ROOT;
        }
        for (;;)
        {
          if (paramString == null) {
            break label302;
          }
          localSimplePathStrategy.addRoot(str1, buildPath(paramString, new String[] { str2 }));
          break;
          if ("files-path".equals(localObject1))
          {
            paramString = paramContext.getFilesDir();
          }
          else if ("cache-path".equals(localObject1))
          {
            paramString = paramContext.getCacheDir();
          }
          else if ("external-path".equals(localObject1))
          {
            paramString = Environment.getExternalStorageDirectory();
          }
          else if ("external-files-path".equals(localObject1))
          {
            localObject1 = ContextCompat.getExternalFilesDirs(paramContext, null);
            paramString = (String)localObject2;
            if (localObject1.length > 0) {
              paramString = localObject1[0];
            }
          }
          else if ("external-cache-path".equals(localObject1))
          {
            localObject1 = ContextCompat.getExternalCacheDirs(paramContext);
            paramString = (String)localObject2;
            if (localObject1.length > 0) {
              paramString = localObject1[0];
            }
          }
          else
          {
            paramString = (String)localObject2;
            if (Build.VERSION.SDK_INT >= 21)
            {
              paramString = (String)localObject2;
              if ("external-media-path".equals(localObject1))
              {
                localObject1 = paramContext.getExternalMediaDirs();
                paramString = (String)localObject2;
                if (localObject1.length > 0) {
                  paramString = localObject1[0];
                }
              }
            }
          }
        }
      }
    }
    return localSimplePathStrategy;
  }
  
  public void attachInfo(Context paramContext, ProviderInfo paramProviderInfo)
  {
    super.attachInfo(paramContext, paramProviderInfo);
    if (paramProviderInfo.exported) {
      throw new SecurityException("Provider must not be exported");
    }
    if (!paramProviderInfo.grantUriPermissions) {
      throw new SecurityException("Provider must grant uri permissions");
    }
    this.mStrategy = getPathStrategy(paramContext, paramProviderInfo.authority);
  }
  
  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    if (this.mStrategy.getFileForUri(paramUri).delete()) {}
    for (int i = 1;; i = 0) {
      return i;
    }
  }
  
  public String getType(Uri paramUri)
  {
    paramUri = this.mStrategy.getFileForUri(paramUri);
    int i = paramUri.getName().lastIndexOf('.');
    if (i >= 0)
    {
      paramUri = paramUri.getName().substring(i + 1);
      paramUri = MimeTypeMap.getSingleton().getMimeTypeFromExtension(paramUri);
      if (paramUri == null) {}
    }
    for (;;)
    {
      return paramUri;
      paramUri = "application/octet-stream";
    }
  }
  
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    throw new UnsupportedOperationException("No external inserts");
  }
  
  public boolean onCreate()
  {
    return true;
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    return ParcelFileDescriptor.open(this.mStrategy.getFileForUri(paramUri), modeToMode(paramString));
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    paramString1 = this.mStrategy.getFileForUri(paramUri);
    paramUri = paramArrayOfString1;
    if (paramArrayOfString1 == null) {
      paramUri = COLUMNS;
    }
    paramArrayOfString2 = new String[paramUri.length];
    paramArrayOfString1 = new Object[paramUri.length];
    int i = paramUri.length;
    int j = 0;
    int k = 0;
    int m;
    if (j < i)
    {
      paramString2 = paramUri[j];
      if ("_display_name".equals(paramString2))
      {
        paramArrayOfString2[k] = "_display_name";
        m = k + 1;
        paramArrayOfString1[k] = paramString1.getName();
        k = m;
      }
    }
    for (;;)
    {
      j++;
      break;
      if ("_size".equals(paramString2))
      {
        paramArrayOfString2[k] = "_size";
        m = k + 1;
        paramArrayOfString1[k] = Long.valueOf(paramString1.length());
        k = m;
        continue;
        paramString1 = copyOf(paramArrayOfString2, k);
        paramUri = copyOf(paramArrayOfString1, k);
        paramArrayOfString1 = new MatrixCursor(paramString1, 1);
        paramArrayOfString1.addRow(paramUri);
        return paramArrayOfString1;
      }
    }
  }
  
  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException("No external updates");
  }
  
  static abstract interface PathStrategy
  {
    public abstract File getFileForUri(Uri paramUri);
    
    public abstract Uri getUriForFile(File paramFile);
  }
  
  static class SimplePathStrategy
    implements FileProvider.PathStrategy
  {
    private final String mAuthority;
    private final HashMap<String, File> mRoots = new HashMap();
    
    SimplePathStrategy(String paramString)
    {
      this.mAuthority = paramString;
    }
    
    void addRoot(String paramString, File paramFile)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException("Name must not be empty");
      }
      try
      {
        File localFile = paramFile.getCanonicalFile();
        this.mRoots.put(paramString, localFile);
        return;
      }
      catch (IOException paramString)
      {
        throw new IllegalArgumentException("Failed to resolve canonical path for " + paramFile, paramString);
      }
    }
    
    public File getFileForUri(Uri paramUri)
    {
      Object localObject1 = paramUri.getEncodedPath();
      int i = ((String)localObject1).indexOf('/', 1);
      Object localObject2 = Uri.decode(((String)localObject1).substring(1, i));
      localObject1 = Uri.decode(((String)localObject1).substring(i + 1));
      localObject2 = (File)this.mRoots.get(localObject2);
      if (localObject2 == null) {
        throw new IllegalArgumentException("Unable to find configured root for " + paramUri);
      }
      paramUri = new File((File)localObject2, (String)localObject1);
      try
      {
        localObject1 = paramUri.getCanonicalFile();
        if (!((File)localObject1).getPath().startsWith(((File)localObject2).getPath())) {
          throw new SecurityException("Resolved path jumped beyond configured root");
        }
      }
      catch (IOException localIOException)
      {
        throw new IllegalArgumentException("Failed to resolve canonical path for " + paramUri);
      }
      return (File)localObject1;
    }
    
    public Uri getUriForFile(File paramFile)
    {
      String str1;
      try
      {
        str1 = paramFile.getCanonicalPath();
        paramFile = null;
        Iterator localIterator = this.mRoots.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          String str3 = ((File)localEntry.getValue()).getPath();
          if ((str1.startsWith(str3)) && ((paramFile == null) || (str3.length() > ((File)paramFile.getValue()).getPath().length()))) {
            paramFile = localEntry;
          }
        }
        if (paramFile != null) {
          break label157;
        }
      }
      catch (IOException localIOException)
      {
        throw new IllegalArgumentException("Failed to resolve canonical path for " + paramFile);
      }
      throw new IllegalArgumentException("Failed to find configured root that contains " + str1);
      label157:
      String str2 = ((File)paramFile.getValue()).getPath();
      if (str2.endsWith("/")) {}
      for (str2 = str1.substring(str2.length());; str2 = str1.substring(str2.length() + 1))
      {
        paramFile = Uri.encode((String)paramFile.getKey()) + '/' + Uri.encode(str2, "/");
        return new Uri.Builder().scheme("content").authority(this.mAuthority).encodedPath(paramFile).build();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/content/FileProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */