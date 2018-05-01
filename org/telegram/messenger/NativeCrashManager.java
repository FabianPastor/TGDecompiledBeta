package org.telegram.messenger;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.utils.SimpleMultipartEntity;

public class NativeCrashManager
{
  public static String createLogFile()
  {
    Object localObject = new Date();
    try
    {
      String str = UUID.randomUUID().toString();
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(Constants.FILES_PATH + "/" + str + ".faketrace"));
      localBufferedWriter.write("Package: " + Constants.APP_PACKAGE + "\n");
      localBufferedWriter.write("Version Code: " + Constants.APP_VERSION + "\n");
      localBufferedWriter.write("Version Name: " + Constants.APP_VERSION_NAME + "\n");
      localBufferedWriter.write("Android: " + Constants.ANDROID_VERSION + "\n");
      localBufferedWriter.write("Manufacturer: " + Constants.PHONE_MANUFACTURER + "\n");
      localBufferedWriter.write("Model: " + Constants.PHONE_MODEL + "\n");
      localBufferedWriter.write("Date: " + localObject + "\n");
      localBufferedWriter.write("\n");
      localBufferedWriter.write("MinidumpContainer");
      localBufferedWriter.flush();
      localBufferedWriter.close();
      localObject = str + ".faketrace";
      return (String)localObject;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return null;
  }
  
  public static void handleDumpFiles(Activity paramActivity)
  {
    String[] arrayOfString = searchForDumpFiles();
    int j = arrayOfString.length;
    int i = 0;
    if (i < j)
    {
      String str2 = arrayOfString[i];
      String str3 = createLogFile();
      if (str3 != null) {
        if (!BuildVars.DEBUG_VERSION) {
          break label58;
        }
      }
      label58:
      for (String str1 = BuildVars.HOCKEY_APP_HASH_DEBUG;; str1 = BuildVars.HOCKEY_APP_HASH)
      {
        uploadDumpAndLog(paramActivity, str1, str2, str3);
        i += 1;
        break;
      }
    }
  }
  
  private static String[] searchForDumpFiles()
  {
    if (Constants.FILES_PATH != null)
    {
      File localFile = new File(Constants.FILES_PATH + "/");
      if ((!localFile.mkdir()) && (!localFile.exists())) {
        return new String[0];
      }
      localFile.list(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.endsWith(".dmp");
        }
      });
    }
    return new String[0];
  }
  
  public static void uploadDumpAndLog(final Activity paramActivity, final String paramString1, String paramString2, final String paramString3)
  {
    new Thread()
    {
      public void run()
      {
        try
        {
          SimpleMultipartEntity localSimpleMultipartEntity = new SimpleMultipartEntity();
          localSimpleMultipartEntity.writeFirstBoundaryIfNeeds();
          Object localObject2 = Uri.fromFile(new File(Constants.FILES_PATH, this.val$dumpFilename));
          Object localObject3 = paramActivity.getContentResolver().openInputStream((Uri)localObject2);
          localSimpleMultipartEntity.addPart("attachment0", ((Uri)localObject2).getLastPathSegment(), (InputStream)localObject3, false);
          localObject2 = Uri.fromFile(new File(Constants.FILES_PATH, paramString3));
          localObject3 = paramActivity.getContentResolver().openInputStream((Uri)localObject2);
          localSimpleMultipartEntity.addPart("log", ((Uri)localObject2).getLastPathSegment(), (InputStream)localObject3, true);
          localSimpleMultipartEntity.writeLastBoundaryIfNeeds();
          localObject2 = (HttpURLConnection)new URL("https://rink.hockeyapp.net/api/2/apps/" + paramString1 + "/crashes/upload").openConnection();
          ((HttpURLConnection)localObject2).setDoOutput(true);
          ((HttpURLConnection)localObject2).setRequestMethod("POST");
          ((HttpURLConnection)localObject2).setRequestProperty("Content-Type", localSimpleMultipartEntity.getContentType());
          ((HttpURLConnection)localObject2).setRequestProperty("Content-Length", String.valueOf(localSimpleMultipartEntity.getContentLength()));
          localObject3 = new BufferedOutputStream(((HttpURLConnection)localObject2).getOutputStream());
          ((BufferedOutputStream)localObject3).write(localSimpleMultipartEntity.getOutputStream().toByteArray());
          ((BufferedOutputStream)localObject3).flush();
          ((BufferedOutputStream)localObject3).close();
          ((HttpURLConnection)localObject2).connect();
          FileLog.e("response code = " + ((HttpURLConnection)localObject2).getResponseCode() + " message = " + ((HttpURLConnection)localObject2).getResponseMessage());
          return;
        }
        catch (IOException localIOException)
        {
          localIOException.printStackTrace();
          return;
        }
        finally
        {
          paramActivity.deleteFile(paramString3);
          paramActivity.deleteFile(this.val$dumpFilename);
        }
      }
    }.start();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/NativeCrashManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */