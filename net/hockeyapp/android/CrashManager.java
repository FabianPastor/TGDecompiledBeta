package net.hockeyapp.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.objects.CrashDetails;
import net.hockeyapp.android.objects.CrashManagerUserInput;
import net.hockeyapp.android.objects.CrashMetaData;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.Util;

public class CrashManager
{
  private static final String ALWAYS_SEND_KEY = "always_send_crash_reports";
  private static final int STACK_TRACES_FOUND_CONFIRMED = 2;
  private static final int STACK_TRACES_FOUND_NEW = 1;
  private static final int STACK_TRACES_FOUND_NONE = 0;
  private static boolean didCrashInLastSession = false;
  private static String identifier = null;
  private static long initializeTimestamp;
  private static boolean submitting;
  private static String urlString = null;
  
  static
  {
    submitting = false;
  }
  
  /* Error */
  private static String contentsOfFile(WeakReference<Context> paramWeakReference, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnull +148 -> 149
    //   4: aload_0
    //   5: invokevirtual 64	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
    //   8: checkcast 66	android/content/Context
    //   11: astore 5
    //   13: aload 5
    //   15: ifnull +134 -> 149
    //   18: new 68	java/lang/StringBuilder
    //   21: dup
    //   22: invokespecial 69	java/lang/StringBuilder:<init>	()V
    //   25: astore 4
    //   27: aconst_null
    //   28: astore_3
    //   29: aconst_null
    //   30: astore_0
    //   31: aconst_null
    //   32: astore_2
    //   33: new 71	java/io/BufferedReader
    //   36: dup
    //   37: new 73	java/io/InputStreamReader
    //   40: dup
    //   41: aload 5
    //   43: aload_1
    //   44: invokevirtual 77	android/content/Context:openFileInput	(Ljava/lang/String;)Ljava/io/FileInputStream;
    //   47: invokespecial 80	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   50: invokespecial 83	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   53: astore_1
    //   54: aload_1
    //   55: invokevirtual 87	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   58: astore_0
    //   59: aload_0
    //   60: ifnull +39 -> 99
    //   63: aload 4
    //   65: aload_0
    //   66: invokevirtual 91	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: pop
    //   70: aload 4
    //   72: ldc 93
    //   74: invokestatic 99	java/lang/System:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   77: invokevirtual 91	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: pop
    //   81: goto -27 -> 54
    //   84: astore_0
    //   85: aload_1
    //   86: ifnull +7 -> 93
    //   89: aload_1
    //   90: invokevirtual 102	java/io/BufferedReader:close	()V
    //   93: aload 4
    //   95: invokevirtual 105	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: areturn
    //   99: aload_1
    //   100: ifnull +77 -> 177
    //   103: aload_1
    //   104: invokevirtual 102	java/io/BufferedReader:close	()V
    //   107: goto -14 -> 93
    //   110: astore_0
    //   111: goto -18 -> 93
    //   114: astore_2
    //   115: aload_3
    //   116: astore_1
    //   117: aload_1
    //   118: astore_0
    //   119: aload_2
    //   120: invokevirtual 108	java/io/IOException:printStackTrace	()V
    //   123: aload_1
    //   124: ifnull -31 -> 93
    //   127: aload_1
    //   128: invokevirtual 102	java/io/BufferedReader:close	()V
    //   131: goto -38 -> 93
    //   134: astore_0
    //   135: goto -42 -> 93
    //   138: astore_1
    //   139: aload_0
    //   140: ifnull +7 -> 147
    //   143: aload_0
    //   144: invokevirtual 102	java/io/BufferedReader:close	()V
    //   147: aload_1
    //   148: athrow
    //   149: aconst_null
    //   150: areturn
    //   151: astore_0
    //   152: goto -59 -> 93
    //   155: astore_0
    //   156: goto -9 -> 147
    //   159: astore_2
    //   160: aload_1
    //   161: astore_0
    //   162: aload_2
    //   163: astore_1
    //   164: goto -25 -> 139
    //   167: astore_2
    //   168: goto -51 -> 117
    //   171: astore_0
    //   172: aload_2
    //   173: astore_1
    //   174: goto -89 -> 85
    //   177: goto -84 -> 93
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	180	0	paramWeakReference	WeakReference<Context>
    //   0	180	1	paramString	String
    //   32	1	2	localObject1	Object
    //   114	6	2	localIOException1	IOException
    //   159	4	2	localObject2	Object
    //   167	6	2	localIOException2	IOException
    //   28	88	3	localObject3	Object
    //   25	69	4	localStringBuilder	StringBuilder
    //   11	31	5	localContext	Context
    // Exception table:
    //   from	to	target	type
    //   54	59	84	java/io/FileNotFoundException
    //   63	81	84	java/io/FileNotFoundException
    //   103	107	110	java/io/IOException
    //   33	54	114	java/io/IOException
    //   127	131	134	java/io/IOException
    //   33	54	138	finally
    //   119	123	138	finally
    //   89	93	151	java/io/IOException
    //   143	147	155	java/io/IOException
    //   54	59	159	finally
    //   63	81	159	finally
    //   54	59	167	java/io/IOException
    //   63	81	167	java/io/IOException
    //   33	54	171	java/io/FileNotFoundException
  }
  
  private static void deleteRetryCounter(WeakReference<Context> paramWeakReference, String paramString, int paramInt)
  {
    if (paramWeakReference != null)
    {
      paramWeakReference = (Context)paramWeakReference.get();
      if (paramWeakReference != null)
      {
        paramWeakReference = paramWeakReference.getSharedPreferences("HockeySDK", 0).edit();
        paramWeakReference.remove("RETRY_COUNT: " + paramString);
        paramWeakReference.apply();
      }
    }
  }
  
  private static void deleteStackTrace(WeakReference<Context> paramWeakReference, String paramString)
  {
    if (paramWeakReference != null)
    {
      paramWeakReference = (Context)paramWeakReference.get();
      if (paramWeakReference != null)
      {
        paramWeakReference.deleteFile(paramString);
        paramWeakReference.deleteFile(paramString.replace(".stacktrace", ".user"));
        paramWeakReference.deleteFile(paramString.replace(".stacktrace", ".contact"));
        paramWeakReference.deleteFile(paramString.replace(".stacktrace", ".description"));
      }
    }
  }
  
  public static void deleteStackTraces(WeakReference<Context> paramWeakReference)
  {
    String[] arrayOfString = searchForStackTraces();
    if ((arrayOfString != null) && (arrayOfString.length > 0))
    {
      HockeyLog.debug("Found " + arrayOfString.length + " stacktrace(s).");
      int i = 0;
      while (i < arrayOfString.length)
      {
        if (paramWeakReference != null) {}
        try
        {
          HockeyLog.debug("Delete stacktrace " + arrayOfString[i] + ".");
          deleteStackTrace(paramWeakReference, arrayOfString[i]);
          Context localContext = (Context)paramWeakReference.get();
          if (localContext != null) {
            localContext.deleteFile(arrayOfString[i]);
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localException.printStackTrace();
          }
        }
        i += 1;
      }
    }
  }
  
  public static boolean didCrashInLastSession()
  {
    return didCrashInLastSession;
  }
  
  public static void execute(Context paramContext, CrashManagerListener paramCrashManagerListener)
  {
    boolean bool2 = true;
    Boolean localBoolean2;
    WeakReference localWeakReference;
    int i;
    if ((paramCrashManagerListener != null) && (paramCrashManagerListener.ignoreDefaultHandler()))
    {
      bool1 = true;
      localBoolean2 = Boolean.valueOf(bool1);
      localWeakReference = new WeakReference(paramContext);
      i = hasStackTraces(localWeakReference);
      if (i != 1) {
        break label161;
      }
      didCrashInLastSession = true;
      if ((paramContext instanceof Activity)) {
        break label144;
      }
    }
    label144:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      paramContext = PreferenceManager.getDefaultSharedPreferences(paramContext);
      Boolean localBoolean1 = Boolean.valueOf(Boolean.valueOf(bool1).booleanValue() | paramContext.getBoolean("always_send_crash_reports", false));
      paramContext = localBoolean1;
      if (paramCrashManagerListener != null)
      {
        paramContext = Boolean.valueOf(Boolean.valueOf(localBoolean1.booleanValue() | paramCrashManagerListener.shouldAutoUploadCrashes()).booleanValue() | paramCrashManagerListener.onCrashesFound());
        paramCrashManagerListener.onNewCrashesFound();
      }
      if (paramContext.booleanValue()) {
        break label149;
      }
      showDialog(localWeakReference, paramCrashManagerListener, localBoolean2.booleanValue());
      return;
      bool1 = false;
      break;
    }
    label149:
    sendCrashes(localWeakReference, paramCrashManagerListener, localBoolean2.booleanValue());
    return;
    label161:
    if (i == 2)
    {
      if (paramCrashManagerListener != null) {
        paramCrashManagerListener.onConfirmedCrashesFound();
      }
      sendCrashes(localWeakReference, paramCrashManagerListener, localBoolean2.booleanValue());
      return;
    }
    registerHandler(localWeakReference, paramCrashManagerListener, localBoolean2.booleanValue());
  }
  
  private static String getAlertTitle(Context paramContext)
  {
    String str = Util.getAppName(paramContext);
    return String.format(paramContext.getString(R.string.hockeyapp_crash_dialog_title), new Object[] { str });
  }
  
  private static List<String> getConfirmedFilenames(WeakReference<Context> paramWeakReference)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramWeakReference != null)
    {
      paramWeakReference = (Context)paramWeakReference.get();
      localObject1 = localObject2;
      if (paramWeakReference != null) {
        localObject1 = Arrays.asList(paramWeakReference.getSharedPreferences("HockeySDK", 0).getString("ConfirmedFilenames", "").split("\\|"));
      }
    }
    return (List<String>)localObject1;
  }
  
  public static long getInitializeTimestamp()
  {
    return initializeTimestamp;
  }
  
  public static CrashDetails getLastCrashDetails()
  {
    if ((Constants.FILES_PATH == null) || (!didCrashInLastSession())) {}
    Object localObject;
    do
    {
      return null;
      File[] arrayOfFile = new File(Constants.FILES_PATH + "/").listFiles(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.endsWith(".stacktrace");
        }
      });
      long l1 = 0L;
      localObject = null;
      int j = arrayOfFile.length;
      int i = 0;
      while (i < j)
      {
        File localFile = arrayOfFile[i];
        long l2 = l1;
        if (localFile.lastModified() > l1)
        {
          l2 = localFile.lastModified();
          localObject = localFile;
        }
        i += 1;
        l1 = l2;
      }
    } while ((localObject == null) || (!((File)localObject).exists()));
    try
    {
      localObject = CrashDetails.fromFile((File)localObject);
      return (CrashDetails)localObject;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  private static String getURLString()
  {
    return urlString + "api/2/apps/" + identifier + "/crashes/";
  }
  
  public static boolean handleUserInput(CrashManagerUserInput paramCrashManagerUserInput, CrashMetaData paramCrashMetaData, CrashManagerListener paramCrashManagerListener, WeakReference<Context> paramWeakReference, boolean paramBoolean)
  {
    switch (paramCrashManagerUserInput)
    {
    default: 
      return false;
    case ???: 
      if (paramCrashManagerListener != null) {
        paramCrashManagerListener.onUserDeniedCrashes();
      }
      deleteStackTraces(paramWeakReference);
      registerHandler(paramWeakReference, paramCrashManagerListener, paramBoolean);
      return true;
    case ???: 
      paramCrashManagerUserInput = null;
      if (paramWeakReference != null) {
        paramCrashManagerUserInput = (Context)paramWeakReference.get();
      }
      if (paramCrashManagerUserInput == null) {
        return false;
      }
      PreferenceManager.getDefaultSharedPreferences(paramCrashManagerUserInput).edit().putBoolean("always_send_crash_reports", true).apply();
      sendCrashes(paramWeakReference, paramCrashManagerListener, paramBoolean, paramCrashMetaData);
      return true;
    }
    sendCrashes(paramWeakReference, paramCrashManagerListener, paramBoolean, paramCrashMetaData);
    return true;
  }
  
  public static int hasStackTraces(WeakReference<Context> paramWeakReference)
  {
    String[] arrayOfString = searchForStackTraces();
    Object localObject = null;
    int j = 0;
    int i = j;
    if (arrayOfString != null)
    {
      i = j;
      if (arrayOfString.length <= 0) {}
    }
    try
    {
      paramWeakReference = getConfirmedFilenames(paramWeakReference);
      if (paramWeakReference != null)
      {
        int k = 2;
        int m = arrayOfString.length;
        j = 0;
        for (;;)
        {
          i = k;
          if (j < m)
          {
            if (!paramWeakReference.contains(arrayOfString[j])) {
              i = 1;
            }
          }
          else {
            return i;
          }
          j += 1;
        }
      }
      return 1;
    }
    catch (Exception paramWeakReference)
    {
      for (;;)
      {
        paramWeakReference = (WeakReference<Context>)localObject;
      }
    }
  }
  
  public static void initialize(Context paramContext, String paramString1, String paramString2, CrashManagerListener paramCrashManagerListener)
  {
    initialize(paramContext, paramString1, paramString2, paramCrashManagerListener, true);
  }
  
  private static void initialize(Context paramContext, String paramString1, String paramString2, CrashManagerListener paramCrashManagerListener, boolean paramBoolean)
  {
    boolean bool = false;
    if (paramContext != null)
    {
      if (initializeTimestamp == 0L) {
        initializeTimestamp = System.currentTimeMillis();
      }
      urlString = paramString1;
      identifier = Util.sanitizeAppIdentifier(paramString2);
      didCrashInLastSession = false;
      Constants.loadFromContext(paramContext);
      if (identifier == null) {
        identifier = Constants.APP_PACKAGE;
      }
      if (paramBoolean)
      {
        paramBoolean = bool;
        if (paramCrashManagerListener != null)
        {
          paramBoolean = bool;
          if (paramCrashManagerListener.ignoreDefaultHandler()) {
            paramBoolean = true;
          }
        }
        registerHandler(new WeakReference(paramContext), paramCrashManagerListener, Boolean.valueOf(paramBoolean).booleanValue());
      }
    }
  }
  
  public static void initialize(Context paramContext, String paramString, CrashManagerListener paramCrashManagerListener)
  {
    initialize(paramContext, "https://sdk.hockeyapp.net/", paramString, paramCrashManagerListener, true);
  }
  
  private static String joinArray(String[] paramArrayOfString, String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      localStringBuffer.append(paramArrayOfString[i]);
      if (i < paramArrayOfString.length - 1) {
        localStringBuffer.append(paramString);
      }
      i += 1;
    }
    return localStringBuffer.toString();
  }
  
  public static void register(Context paramContext)
  {
    String str = Util.getAppIdentifier(paramContext);
    if (TextUtils.isEmpty(str)) {
      throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
    }
    register(paramContext, str);
  }
  
  public static void register(Context paramContext, String paramString)
  {
    register(paramContext, "https://sdk.hockeyapp.net/", paramString, null);
  }
  
  public static void register(Context paramContext, String paramString1, String paramString2, CrashManagerListener paramCrashManagerListener)
  {
    initialize(paramContext, paramString1, paramString2, paramCrashManagerListener, false);
    execute(paramContext, paramCrashManagerListener);
  }
  
  public static void register(Context paramContext, String paramString, CrashManagerListener paramCrashManagerListener)
  {
    register(paramContext, "https://sdk.hockeyapp.net/", paramString, paramCrashManagerListener);
  }
  
  private static void registerHandler(WeakReference<Context> paramWeakReference, CrashManagerListener paramCrashManagerListener, boolean paramBoolean)
  {
    if ((!TextUtils.isEmpty(Constants.APP_VERSION)) && (!TextUtils.isEmpty(Constants.APP_PACKAGE)))
    {
      paramWeakReference = Thread.getDefaultUncaughtExceptionHandler();
      if (paramWeakReference != null) {
        HockeyLog.debug("Current handler class = " + paramWeakReference.getClass().getName());
      }
      if ((paramWeakReference instanceof ExceptionHandler))
      {
        ((ExceptionHandler)paramWeakReference).setListener(paramCrashManagerListener);
        return;
      }
      Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(paramWeakReference, paramCrashManagerListener, paramBoolean));
      return;
    }
    HockeyLog.debug("Exception handler not set because version or package is null.");
  }
  
  public static void resetAlwaysSend(WeakReference<Context> paramWeakReference)
  {
    if (paramWeakReference != null)
    {
      paramWeakReference = (Context)paramWeakReference.get();
      if (paramWeakReference != null) {
        PreferenceManager.getDefaultSharedPreferences(paramWeakReference).edit().remove("always_send_crash_reports").apply();
      }
    }
  }
  
  private static void saveConfirmedStackTraces(WeakReference<Context> paramWeakReference)
  {
    Object localObject;
    if (paramWeakReference != null)
    {
      localObject = (Context)paramWeakReference.get();
      if (localObject == null) {}
    }
    try
    {
      paramWeakReference = searchForStackTraces();
      localObject = ((Context)localObject).getSharedPreferences("HockeySDK", 0).edit();
      ((SharedPreferences.Editor)localObject).putString("ConfirmedFilenames", joinArray(paramWeakReference, "|"));
      ((SharedPreferences.Editor)localObject).apply();
      return;
    }
    catch (Exception paramWeakReference) {}
  }
  
  private static String[] searchForStackTraces()
  {
    if (Constants.FILES_PATH != null)
    {
      HockeyLog.debug("Looking for exceptions in: " + Constants.FILES_PATH);
      File localFile = new File(Constants.FILES_PATH + "/");
      if ((!localFile.mkdir()) && (!localFile.exists())) {
        return new String[0];
      }
      localFile.list(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.endsWith(".stacktrace");
        }
      });
    }
    HockeyLog.debug("Can't search for exception as file path is null.");
    return null;
  }
  
  private static void sendCrashes(WeakReference<Context> paramWeakReference, CrashManagerListener paramCrashManagerListener, boolean paramBoolean)
  {
    sendCrashes(paramWeakReference, paramCrashManagerListener, paramBoolean, null);
  }
  
  private static void sendCrashes(WeakReference<Context> paramWeakReference, final CrashManagerListener paramCrashManagerListener, boolean paramBoolean, final CrashMetaData paramCrashMetaData)
  {
    saveConfirmedStackTraces(paramWeakReference);
    registerHandler(paramWeakReference, paramCrashManagerListener, paramBoolean);
    Context localContext = (Context)paramWeakReference.get();
    if ((localContext != null) && (!Util.isConnectedToNetwork(localContext))) {}
    while (submitting) {
      return;
    }
    submitting = true;
    new Thread()
    {
      public void run()
      {
        CrashManager.submitStackTraces(this.val$weakContext, paramCrashManagerListener, paramCrashMetaData);
        CrashManager.access$002(false);
      }
    }.start();
  }
  
  private static void showDialog(final WeakReference<Context> paramWeakReference, CrashManagerListener paramCrashManagerListener, final boolean paramBoolean)
  {
    Context localContext = null;
    if (paramWeakReference != null) {
      localContext = (Context)paramWeakReference.get();
    }
    if (localContext == null) {}
    while ((paramCrashManagerListener != null) && (paramCrashManagerListener.onHandleAlertView())) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(localContext);
    localBuilder.setTitle(getAlertTitle(localContext));
    localBuilder.setMessage(R.string.hockeyapp_crash_dialog_message);
    localBuilder.setNegativeButton(R.string.hockeyapp_crash_dialog_negative_button, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputDontSend, null, this.val$listener, paramWeakReference, paramBoolean);
      }
    });
    localBuilder.setNeutralButton(R.string.hockeyapp_crash_dialog_neutral_button, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputAlwaysSend, null, this.val$listener, paramWeakReference, paramBoolean);
      }
    });
    localBuilder.setPositiveButton(R.string.hockeyapp_crash_dialog_positive_button, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputSend, null, this.val$listener, paramWeakReference, paramBoolean);
      }
    });
    localBuilder.create().show();
  }
  
  public static void submitStackTraces(WeakReference<Context> paramWeakReference, CrashManagerListener paramCrashManagerListener)
  {
    submitStackTraces(paramWeakReference, paramCrashManagerListener, null);
  }
  
  public static void submitStackTraces(WeakReference<Context> paramWeakReference, CrashManagerListener paramCrashManagerListener, CrashMetaData paramCrashMetaData)
  {
    String[] arrayOfString = searchForStackTraces();
    Object localObject1 = Boolean.valueOf(false);
    int i;
    Object localObject8;
    Object localObject9;
    Object localObject5;
    String str3;
    Object localObject3;
    Object localObject2;
    if ((arrayOfString != null) && (arrayOfString.length > 0))
    {
      HockeyLog.debug("Found " + arrayOfString.length + " stacktrace(s).");
      i = 0;
      if (i < arrayOfString.length)
      {
        localObject8 = null;
        localObject9 = null;
        localObject5 = null;
        str3 = arrayOfString[i];
        localObject3 = localObject8;
        localObject2 = localObject9;
      }
    }
    for (;;)
    {
      try
      {
        String str2 = contentsOfFile(paramWeakReference, str3);
        localObject4 = localObject1;
        localObject3 = localObject8;
        localObject2 = localObject9;
        if (str2.length() > 0)
        {
          localObject3 = localObject8;
          localObject2 = localObject9;
          HockeyLog.debug("Transmitting crash data: \n" + str2);
          localObject3 = localObject8;
          localObject2 = localObject9;
          localObject4 = contentsOfFile(paramWeakReference, str3.replace(".stacktrace", ".user"));
          localObject3 = localObject8;
          localObject2 = localObject9;
          localObject5 = contentsOfFile(paramWeakReference, str3.replace(".stacktrace", ".contact"));
          Object localObject7 = localObject5;
          Object localObject6 = localObject4;
          if (paramCrashMetaData != null)
          {
            localObject3 = localObject8;
            localObject2 = localObject9;
            localObject6 = paramCrashMetaData.getUserID();
            localObject3 = localObject8;
            localObject2 = localObject9;
            if (!TextUtils.isEmpty((CharSequence)localObject6)) {
              localObject4 = localObject6;
            }
            localObject3 = localObject8;
            localObject2 = localObject9;
            str1 = paramCrashMetaData.getUserEmail();
            localObject7 = localObject5;
            localObject6 = localObject4;
            localObject3 = localObject8;
            localObject2 = localObject9;
            if (!TextUtils.isEmpty(str1))
            {
              localObject7 = str1;
              localObject6 = localObject4;
            }
          }
          localObject3 = localObject8;
          localObject2 = localObject9;
          str1 = contentsOfFile(paramWeakReference, str3.replace(".stacktrace", ".description"));
          if (paramCrashMetaData == null) {
            continue;
          }
          localObject3 = localObject8;
          localObject2 = localObject9;
          localObject5 = paramCrashMetaData.getUserDescription();
          localObject4 = localObject5;
          localObject3 = localObject8;
          localObject2 = localObject9;
          if (!TextUtils.isEmpty(str1))
          {
            localObject3 = localObject8;
            localObject2 = localObject9;
            if (TextUtils.isEmpty((CharSequence)localObject5)) {
              continue;
            }
            localObject3 = localObject8;
            localObject2 = localObject9;
            localObject4 = String.format("%s\n\nLog:\n%s", new Object[] { localObject5, str1 });
          }
          localObject3 = localObject8;
          localObject2 = localObject9;
          localObject5 = new HashMap();
          localObject3 = localObject8;
          localObject2 = localObject9;
          ((Map)localObject5).put("raw", str2);
          localObject3 = localObject8;
          localObject2 = localObject9;
          ((Map)localObject5).put("userID", localObject6);
          localObject3 = localObject8;
          localObject2 = localObject9;
          ((Map)localObject5).put("contact", localObject7);
          localObject3 = localObject8;
          localObject2 = localObject9;
          ((Map)localObject5).put("description", localObject4);
          localObject3 = localObject8;
          localObject2 = localObject9;
          ((Map)localObject5).put("sdk", "HockeySDK");
          localObject3 = localObject8;
          localObject2 = localObject9;
          ((Map)localObject5).put("sdk_version", "4.0.1");
          localObject3 = localObject8;
          localObject2 = localObject9;
          localObject4 = new HttpURLConnectionBuilder(getURLString()).setRequestMethod("POST").writeFormFields((Map)localObject5).build();
          localObject3 = localObject4;
          localObject2 = localObject4;
          int j = ((HttpURLConnection)localObject4).getResponseCode();
          if (j == 202) {
            break label961;
          }
          if (j != 201) {
            continue;
          }
          break label961;
          localObject1 = Boolean.valueOf(bool);
          localObject5 = localObject4;
          localObject4 = localObject1;
        }
        if (localObject5 != null) {
          ((HttpURLConnection)localObject5).disconnect();
        }
        if (!((Boolean)localObject4).booleanValue()) {
          continue;
        }
        HockeyLog.debug("Transmission succeeded");
        deleteStackTrace(paramWeakReference, arrayOfString[i]);
        localObject2 = localObject4;
        if (paramCrashManagerListener != null)
        {
          paramCrashManagerListener.onCrashesSent();
          deleteRetryCounter(paramWeakReference, arrayOfString[i], paramCrashManagerListener.getMaxRetryAttempts());
          localObject2 = localObject4;
        }
      }
      catch (Exception localException)
      {
        Object localObject4;
        String str1;
        localObject2 = localObject3;
        localException.printStackTrace();
        if (localObject3 == null) {
          continue;
        }
        ((HttpURLConnection)localObject3).disconnect();
        if (!((Boolean)localObject1).booleanValue()) {
          continue;
        }
        HockeyLog.debug("Transmission succeeded");
        deleteStackTrace(paramWeakReference, arrayOfString[i]);
        localObject2 = localObject1;
        if (paramCrashManagerListener == null) {
          continue;
        }
        paramCrashManagerListener.onCrashesSent();
        deleteRetryCounter(paramWeakReference, arrayOfString[i], paramCrashManagerListener.getMaxRetryAttempts());
        localObject2 = localObject1;
        continue;
        HockeyLog.debug("Transmission failed, will retry on next register() call");
        localObject2 = localObject1;
        if (paramCrashManagerListener == null) {
          continue;
        }
        paramCrashManagerListener.onCrashesNotSent();
        updateRetryCounter(paramWeakReference, arrayOfString[i], paramCrashManagerListener.getMaxRetryAttempts());
        localObject2 = localObject1;
        continue;
      }
      finally
      {
        if (localObject2 == null) {
          continue;
        }
        ((HttpURLConnection)localObject2).disconnect();
        if (!((Boolean)localObject1).booleanValue()) {
          continue;
        }
        HockeyLog.debug("Transmission succeeded");
        deleteStackTrace(paramWeakReference, arrayOfString[i]);
        if (paramCrashManagerListener == null) {
          continue;
        }
        paramCrashManagerListener.onCrashesSent();
        deleteRetryCounter(paramWeakReference, arrayOfString[i], paramCrashManagerListener.getMaxRetryAttempts());
        throw paramCrashMetaData;
        HockeyLog.debug("Transmission failed, will retry on next register() call");
        if (paramCrashManagerListener == null) {
          continue;
        }
        paramCrashManagerListener.onCrashesNotSent();
        updateRetryCounter(paramWeakReference, arrayOfString[i], paramCrashManagerListener.getMaxRetryAttempts());
        continue;
      }
      i += 1;
      localObject1 = localObject2;
      break;
      localObject5 = "";
      continue;
      localObject3 = localObject8;
      localObject2 = localObject9;
      localObject4 = String.format("Log:\n%s", new Object[] { str1 });
      continue;
      boolean bool = false;
      continue;
      HockeyLog.debug("Transmission failed, will retry on next register() call");
      localObject2 = localObject4;
      if (paramCrashManagerListener != null)
      {
        paramCrashManagerListener.onCrashesNotSent();
        updateRetryCounter(paramWeakReference, arrayOfString[i], paramCrashManagerListener.getMaxRetryAttempts());
        localObject2 = localObject4;
        continue;
        return;
        label961:
        bool = true;
      }
    }
  }
  
  private static void updateRetryCounter(WeakReference<Context> paramWeakReference, String paramString, int paramInt)
  {
    if (paramInt == -1) {}
    do
    {
      do
      {
        return;
      } while (paramWeakReference == null);
      localObject = (Context)paramWeakReference.get();
    } while (localObject == null);
    Object localObject = ((Context)localObject).getSharedPreferences("HockeySDK", 0);
    SharedPreferences.Editor localEditor = ((SharedPreferences)localObject).edit();
    int i = ((SharedPreferences)localObject).getInt("RETRY_COUNT: " + paramString, 0);
    if (i >= paramInt)
    {
      deleteStackTrace(paramWeakReference, paramString);
      deleteRetryCounter(paramWeakReference, paramString, paramInt);
      return;
    }
    localEditor.putInt("RETRY_COUNT: " + paramString, i + 1);
    localEditor.apply();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/CrashManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */