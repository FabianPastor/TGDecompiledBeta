package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import net.hockeyapp.android.objects.CrashManagerUserInput;
import net.hockeyapp.android.objects.CrashMetaData;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.Util;

public class CrashManager
{
  private static final FilenameFilter STACK_TRACES_FILTER = new FilenameFilter()
  {
    public boolean accept(File paramAnonymousFile, String paramAnonymousString)
    {
      return paramAnonymousString.endsWith(".stacktrace");
    }
  };
  private static boolean didCrashInLastSession;
  private static String identifier = null;
  private static long initializeTimestamp;
  static CountDownLatch latch;
  static int stackTracesCount;
  private static String urlString = null;
  static WeakReference<Context> weakContext;
  
  static
  {
    didCrashInLastSession = false;
    stackTracesCount = 0;
    latch = new CountDownLatch(1);
  }
  
  /* Error */
  private static String contentsOfFile(WeakReference<Context> paramWeakReference, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnull +37 -> 38
    //   4: aload_0
    //   5: invokevirtual 103	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
    //   8: checkcast 105	android/content/Context
    //   11: astore_2
    //   12: aload_2
    //   13: ifnull +196 -> 209
    //   16: aload_2
    //   17: aload_1
    //   18: invokevirtual 109	android/content/Context:getFileStreamPath	(Ljava/lang/String;)Ljava/io/File;
    //   21: astore_0
    //   22: aload_0
    //   23: ifnull +10 -> 33
    //   26: aload_0
    //   27: invokevirtual 115	java/io/File:exists	()Z
    //   30: ifne +13 -> 43
    //   33: ldc 117
    //   35: astore_0
    //   36: aload_0
    //   37: areturn
    //   38: aconst_null
    //   39: astore_2
    //   40: goto -28 -> 12
    //   43: new 119	java/lang/StringBuilder
    //   46: dup
    //   47: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   50: astore_3
    //   51: aconst_null
    //   52: astore 4
    //   54: aconst_null
    //   55: astore 5
    //   57: aload 4
    //   59: astore_0
    //   60: new 122	java/io/BufferedReader
    //   63: astore 6
    //   65: aload 4
    //   67: astore_0
    //   68: new 124	java/io/InputStreamReader
    //   71: astore 7
    //   73: aload 4
    //   75: astore_0
    //   76: aload 7
    //   78: aload_2
    //   79: aload_1
    //   80: invokevirtual 128	android/content/Context:openFileInput	(Ljava/lang/String;)Ljava/io/FileInputStream;
    //   83: invokespecial 131	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   86: aload 4
    //   88: astore_0
    //   89: aload 6
    //   91: aload 7
    //   93: invokespecial 134	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   96: aload 6
    //   98: invokevirtual 138	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   101: astore_0
    //   102: aload_0
    //   103: ifnull +78 -> 181
    //   106: aload_3
    //   107: aload_0
    //   108: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: pop
    //   112: aload_3
    //   113: ldc -112
    //   115: invokestatic 150	java/lang/System:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   118: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: pop
    //   122: goto -26 -> 96
    //   125: astore_2
    //   126: aload 6
    //   128: astore_0
    //   129: new 119	java/lang/StringBuilder
    //   132: astore 5
    //   134: aload 6
    //   136: astore_0
    //   137: aload 5
    //   139: invokespecial 120	java/lang/StringBuilder:<init>	()V
    //   142: aload 6
    //   144: astore_0
    //   145: aload 5
    //   147: ldc -104
    //   149: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: aload_1
    //   153: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   156: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   159: aload_2
    //   160: invokestatic 161	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   163: aload 6
    //   165: ifnull +8 -> 173
    //   168: aload 6
    //   170: invokevirtual 164	java/io/BufferedReader:close	()V
    //   173: aload_3
    //   174: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   177: astore_0
    //   178: goto -142 -> 36
    //   181: aload 6
    //   183: ifnull +55 -> 238
    //   186: aload 6
    //   188: invokevirtual 164	java/io/BufferedReader:close	()V
    //   191: goto -18 -> 173
    //   194: astore_0
    //   195: goto -22 -> 173
    //   198: astore_1
    //   199: aload_0
    //   200: ifnull +7 -> 207
    //   203: aload_0
    //   204: invokevirtual 164	java/io/BufferedReader:close	()V
    //   207: aload_1
    //   208: athrow
    //   209: ldc 117
    //   211: astore_0
    //   212: goto -176 -> 36
    //   215: astore_0
    //   216: goto -43 -> 173
    //   219: astore_0
    //   220: goto -13 -> 207
    //   223: astore_1
    //   224: aload 6
    //   226: astore_0
    //   227: goto -28 -> 199
    //   230: astore_2
    //   231: aload 5
    //   233: astore 6
    //   235: goto -109 -> 126
    //   238: goto -65 -> 173
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	241	0	paramWeakReference	WeakReference<Context>
    //   0	241	1	paramString	String
    //   11	68	2	localContext	Context
    //   125	35	2	localIOException1	java.io.IOException
    //   230	1	2	localIOException2	java.io.IOException
    //   50	124	3	localStringBuilder1	StringBuilder
    //   52	35	4	localObject1	Object
    //   55	177	5	localStringBuilder2	StringBuilder
    //   63	171	6	localObject2	Object
    //   71	21	7	localInputStreamReader	java.io.InputStreamReader
    // Exception table:
    //   from	to	target	type
    //   96	102	125	java/io/IOException
    //   106	122	125	java/io/IOException
    //   186	191	194	java/io/IOException
    //   60	65	198	finally
    //   68	73	198	finally
    //   76	86	198	finally
    //   89	96	198	finally
    //   129	134	198	finally
    //   137	142	198	finally
    //   145	163	198	finally
    //   168	173	215	java/io/IOException
    //   203	207	219	java/io/IOException
    //   96	102	223	finally
    //   106	122	223	finally
    //   60	65	230	java/io/IOException
    //   68	73	230	java/io/IOException
    //   76	86	230	java/io/IOException
    //   89	96	230	java/io/IOException
  }
  
  private static void deleteRedundantStackTraces(WeakReference<Context> paramWeakReference)
  {
    Object localObject;
    if (paramWeakReference != null)
    {
      localObject = (Context)paramWeakReference.get();
      if (localObject != null)
      {
        localObject = ((Context)localObject).getFilesDir();
        if ((localObject != null) && (((File)localObject).exists())) {
          break label38;
        }
      }
    }
    for (;;)
    {
      return;
      localObject = null;
      break;
      label38:
      localObject = ((File)localObject).listFiles(STACK_TRACES_FILTER);
      if (localObject.length > 100)
      {
        HockeyLog.debug("Delete " + (localObject.length - 100) + " redundant stacktrace(s).");
        Arrays.sort((Object[])localObject, new Comparator()
        {
          public int compare(File paramAnonymousFile1, File paramAnonymousFile2)
          {
            return Long.valueOf(paramAnonymousFile1.lastModified()).compareTo(Long.valueOf(paramAnonymousFile2.lastModified()));
          }
        });
        for (int i = 0; i < localObject.length - 100; i++) {
          deleteStackTrace(paramWeakReference, localObject[i].getName());
        }
      }
    }
  }
  
  private static void deleteRetryCounter(WeakReference<Context> paramWeakReference, String paramString)
  {
    if (paramWeakReference != null) {}
    for (paramWeakReference = (Context)paramWeakReference.get();; paramWeakReference = null)
    {
      if (paramWeakReference != null)
      {
        paramWeakReference = paramWeakReference.getSharedPreferences("HockeySDK", 0).edit();
        paramWeakReference.remove("RETRY_COUNT: " + paramString);
        paramWeakReference.apply();
      }
      return;
    }
  }
  
  private static void deleteStackTrace(WeakReference<Context> paramWeakReference, String paramString)
  {
    if (paramWeakReference != null) {}
    for (paramWeakReference = (Context)paramWeakReference.get();; paramWeakReference = null)
    {
      if (paramWeakReference != null)
      {
        paramWeakReference.deleteFile(paramString);
        paramWeakReference.deleteFile(paramString.replace(".stacktrace", ".user"));
        paramWeakReference.deleteFile(paramString.replace(".stacktrace", ".contact"));
        paramWeakReference.deleteFile(paramString.replace(".stacktrace", ".description"));
        stackTracesCount -= 1;
      }
      return;
    }
  }
  
  public static void deleteStackTraces(WeakReference<Context> paramWeakReference)
  {
    String[] arrayOfString = searchForStackTraces(paramWeakReference);
    if ((arrayOfString != null) && (arrayOfString.length > 0))
    {
      HockeyLog.debug("Found " + arrayOfString.length + " stacktrace(s).");
      int i = arrayOfString.length;
      int j = 0;
      for (;;)
      {
        if (j < i)
        {
          String str = arrayOfString[j];
          if (paramWeakReference != null) {}
          try
          {
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            HockeyLog.debug("Delete stacktrace " + str + ".");
            deleteStackTrace(paramWeakReference, str);
            j++;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              HockeyLog.error("Failed to delete stacktrace", localException);
            }
          }
        }
      }
    }
  }
  
  @SuppressLint({"StaticFieldLeak"})
  public static void execute(Context paramContext, final CrashManagerListener paramCrashManagerListener)
  {
    AsyncTaskUtils.execute(new AsyncTask()
    {
      private boolean autoSend = false;
      
      protected Integer doInBackground(Void... paramAnonymousVarArgs)
      {
        boolean bool = true;
        paramAnonymousVarArgs = (Context)this.val$weakContext.get();
        if (paramAnonymousVarArgs != null)
        {
          paramAnonymousVarArgs = PreferenceManager.getDefaultSharedPreferences(paramAnonymousVarArgs);
          this.autoSend |= paramAnonymousVarArgs.getBoolean("always_send_crash_reports", false);
        }
        int i = CrashManager.hasStackTraces(this.val$weakContext);
        if (i == 1) {}
        for (;;)
        {
          CrashManager.access$002(bool);
          CrashManager.latch.countDown();
          return Integer.valueOf(i);
          bool = false;
        }
      }
      
      protected void onPostExecute(Integer paramAnonymousInteger)
      {
        boolean bool1 = this.autoSend;
        boolean bool2;
        if ((paramCrashManagerListener != null) && (paramCrashManagerListener.ignoreDefaultHandler()))
        {
          bool2 = true;
          if (paramAnonymousInteger.intValue() != 1) {
            break label99;
          }
          boolean bool3 = bool1;
          if (paramCrashManagerListener != null)
          {
            bool3 = bool1 | paramCrashManagerListener.shouldAutoUploadCrashes();
            paramCrashManagerListener.onNewCrashesFound();
          }
          if ((bool3) || (!CrashManager.showDialog(this.val$weakContext, paramCrashManagerListener, bool2))) {
            CrashManager.sendCrashes(this.val$weakContext, paramCrashManagerListener, bool2, null);
          }
        }
        for (;;)
        {
          return;
          bool2 = false;
          break;
          label99:
          if (paramAnonymousInteger.intValue() == 2)
          {
            if (paramCrashManagerListener != null) {
              paramCrashManagerListener.onConfirmedCrashesFound();
            }
            CrashManager.sendCrashes(this.val$weakContext, paramCrashManagerListener, bool2, null);
          }
          else if (paramAnonymousInteger.intValue() == 0)
          {
            if (paramCrashManagerListener != null) {
              paramCrashManagerListener.onNoCrashesFound();
            }
            CrashManager.registerHandler(paramCrashManagerListener, bool2);
          }
        }
      }
    });
  }
  
  private static String getAlertTitle(Context paramContext)
  {
    return paramContext.getString(R.string.hockeyapp_crash_dialog_title, new Object[] { Util.getAppName(paramContext) });
  }
  
  public static long getInitializeTimestamp()
  {
    return initializeTimestamp;
  }
  
  private static String getURLString()
  {
    return urlString + "api/2/apps/" + identifier + "/crashes/";
  }
  
  @SuppressLint({"StaticFieldLeak"})
  public static boolean handleUserInput(CrashManagerUserInput paramCrashManagerUserInput, CrashMetaData paramCrashMetaData, CrashManagerListener paramCrashManagerListener, WeakReference<Context> paramWeakReference, boolean paramBoolean)
  {
    switch (paramCrashManagerUserInput)
    {
    default: 
      paramBoolean = false;
    }
    for (;;)
    {
      return paramBoolean;
      if (paramCrashManagerListener != null) {
        paramCrashManagerListener.onUserDeniedCrashes();
      }
      registerHandler(paramCrashManagerListener, paramBoolean);
      AsyncTaskUtils.execute(new AsyncTask()
      {
        protected Object doInBackground(Void... paramAnonymousVarArgs)
        {
          CrashManager.deleteStackTraces(this.val$weakContext);
          return null;
        }
      });
      paramBoolean = true;
      continue;
      if (paramWeakReference != null) {}
      for (paramCrashManagerUserInput = (Context)paramWeakReference.get();; paramCrashManagerUserInput = null)
      {
        if (paramCrashManagerUserInput != null) {
          break label100;
        }
        paramBoolean = false;
        break;
      }
      label100:
      PreferenceManager.getDefaultSharedPreferences(paramCrashManagerUserInput).edit().putBoolean("always_send_crash_reports", true).apply();
      sendCrashes(paramWeakReference, paramCrashManagerListener, paramBoolean, paramCrashMetaData);
      paramBoolean = true;
      continue;
      sendCrashes(paramWeakReference, paramCrashManagerListener, paramBoolean, paramCrashMetaData);
      paramBoolean = true;
    }
  }
  
  public static int hasStackTraces(WeakReference<Context> paramWeakReference)
  {
    String[] arrayOfString = searchForStackTraces(paramWeakReference);
    localObject1 = null;
    int i = 0;
    int j = i;
    if (arrayOfString != null)
    {
      j = i;
      if ((arrayOfString.length > 0) && (paramWeakReference == null)) {
        break label114;
      }
    }
    try
    {
      paramWeakReference = (Context)paramWeakReference.get();
      localObject2 = localObject1;
      if (paramWeakReference != null) {
        localObject2 = Arrays.asList(paramWeakReference.getSharedPreferences("HockeySDK", 0).getString("ConfirmedFilenames", "").split("\\|"));
      }
    }
    catch (Exception paramWeakReference)
    {
      for (;;)
      {
        int k;
        int m;
        label85:
        label114:
        label119:
        Object localObject2 = localObject1;
      }
    }
    if (localObject2 != null)
    {
      k = 2;
      m = arrayOfString.length;
      i = 0;
      j = k;
      if (i < m) {
        if (((List)localObject2).contains(arrayOfString[i])) {
          break label119;
        }
      }
    }
    for (j = 1;; j = 1)
    {
      return j;
      paramWeakReference = null;
      break;
      i++;
      break label85;
    }
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
      weakContext = new WeakReference(paramContext);
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
        registerHandler(paramCrashManagerListener, paramBoolean);
      }
    }
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
  
  private static void registerHandler(CrashManagerListener paramCrashManagerListener, boolean paramBoolean)
  {
    Thread.UncaughtExceptionHandler localUncaughtExceptionHandler;
    if ((!TextUtils.isEmpty(Constants.APP_VERSION)) && (!TextUtils.isEmpty(Constants.APP_PACKAGE)))
    {
      localUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
      if (localUncaughtExceptionHandler != null) {
        HockeyLog.debug("Current handler class = " + localUncaughtExceptionHandler.getClass().getName());
      }
      if ((localUncaughtExceptionHandler instanceof ExceptionHandler)) {
        ((ExceptionHandler)localUncaughtExceptionHandler).setListener(paramCrashManagerListener);
      }
    }
    for (;;)
    {
      return;
      Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(localUncaughtExceptionHandler, paramCrashManagerListener, paramBoolean));
      continue;
      HockeyLog.debug("Exception handler not set because version or package is null.");
    }
  }
  
  private static void saveConfirmedStackTraces(WeakReference<Context> paramWeakReference, String[] paramArrayOfString)
  {
    if (paramWeakReference != null) {
      paramWeakReference = (Context)paramWeakReference.get();
    }
    for (;;)
    {
      if (paramWeakReference != null) {}
      try
      {
        paramWeakReference = paramWeakReference.getSharedPreferences("HockeySDK", 0).edit();
        paramWeakReference.putString("ConfirmedFilenames", TextUtils.join(",", paramArrayOfString));
        paramWeakReference.apply();
        return;
        paramWeakReference = null;
      }
      catch (Exception paramWeakReference)
      {
        for (;;) {}
      }
    }
  }
  
  static String[] searchForStackTraces(WeakReference<Context> paramWeakReference)
  {
    Object localObject = null;
    Context localContext;
    if (paramWeakReference != null)
    {
      localContext = (Context)paramWeakReference.get();
      paramWeakReference = (WeakReference<Context>)localObject;
      if (localContext != null)
      {
        paramWeakReference = localContext.getFilesDir();
        if (paramWeakReference == null) {
          break label108;
        }
        HockeyLog.debug("Looking for exceptions in: " + paramWeakReference.getAbsolutePath());
        if ((paramWeakReference.exists()) || (paramWeakReference.mkdir())) {
          break label81;
        }
        paramWeakReference = new String[0];
      }
    }
    for (;;)
    {
      return paramWeakReference;
      localContext = null;
      break;
      label81:
      paramWeakReference = paramWeakReference.list(STACK_TRACES_FILTER);
      if (paramWeakReference != null) {}
      for (int i = paramWeakReference.length;; i = 0)
      {
        stackTracesCount = i;
        break;
      }
      label108:
      HockeyLog.debug("Can't search for exception as file path is null.");
      paramWeakReference = (WeakReference<Context>)localObject;
    }
  }
  
  @SuppressLint({"StaticFieldLeak"})
  private static void sendCrashes(WeakReference<Context> paramWeakReference, final CrashManagerListener paramCrashManagerListener, final boolean paramBoolean, final CrashMetaData paramCrashMetaData)
  {
    registerHandler(paramCrashManagerListener, paramBoolean);
    Context localContext;
    if (paramWeakReference != null)
    {
      localContext = (Context)paramWeakReference.get();
      if ((localContext == null) || (!Util.isConnectedToNetwork(localContext))) {
        break label66;
      }
    }
    label66:
    for (paramBoolean = true;; paramBoolean = false)
    {
      if ((!paramBoolean) && (paramCrashManagerListener != null)) {
        paramCrashManagerListener.onCrashesNotSent();
      }
      AsyncTaskUtils.execute(new AsyncTask()
      {
        protected Object doInBackground(Void... paramAnonymousVarArgs)
        {
          Object localObject = CrashManager.searchForStackTraces(this.val$weakContext);
          if ((localObject != null) && (localObject.length > 0))
          {
            HockeyLog.debug("Found " + localObject.length + " stacktrace(s).");
            paramAnonymousVarArgs = (Void[])localObject;
            if (localObject.length <= 100) {
              break label77;
            }
            CrashManager.deleteRedundantStackTraces(this.val$weakContext);
            localObject = CrashManager.searchForStackTraces(this.val$weakContext);
            paramAnonymousVarArgs = (Void[])localObject;
            if (localObject != null) {
              break label77;
            }
          }
          for (;;)
          {
            return null;
            label77:
            CrashManager.saveConfirmedStackTraces(this.val$weakContext, paramAnonymousVarArgs);
            if (paramBoolean)
            {
              int i = paramAnonymousVarArgs.length;
              for (int j = 0; j < i; j++)
              {
                localObject = paramAnonymousVarArgs[j];
                CrashManager.submitStackTrace(this.val$weakContext, (String)localObject, paramCrashManagerListener, paramCrashMetaData);
              }
            }
          }
        }
      });
      return;
      localContext = null;
      break;
    }
  }
  
  private static boolean showDialog(final WeakReference<Context> paramWeakReference, CrashManagerListener paramCrashManagerListener, final boolean paramBoolean)
  {
    if ((paramCrashManagerListener != null) && (paramCrashManagerListener.onHandleAlertView())) {
      paramBoolean = true;
    }
    for (;;)
    {
      return paramBoolean;
      if (paramWeakReference != null) {}
      for (Context localContext = (Context)paramWeakReference.get();; localContext = null)
      {
        if ((localContext != null) && ((localContext instanceof Activity))) {
          break label48;
        }
        paramBoolean = false;
        break;
      }
      label48:
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
      paramBoolean = true;
    }
  }
  
  private static void submitStackTrace(WeakReference<Context> paramWeakReference, String paramString, CrashManagerListener paramCrashManagerListener, CrashMetaData paramCrashMetaData)
  {
    Boolean localBoolean = Boolean.valueOf(false);
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = localObject1;
    Object localObject5 = localObject2;
    for (;;)
    {
      try
      {
        String str1 = contentsOfFile(paramWeakReference, paramString);
        localObject6 = localBoolean;
        localObject4 = localObject1;
        localObject5 = localObject2;
        if (str1.length() > 0)
        {
          localObject4 = localObject1;
          localObject5 = localObject2;
          localObject6 = new java/lang/StringBuilder;
          localObject4 = localObject1;
          localObject5 = localObject2;
          ((StringBuilder)localObject6).<init>();
          localObject4 = localObject1;
          localObject5 = localObject2;
          HockeyLog.debug("Transmitting crash data: \n" + str1);
          localObject4 = localObject1;
          localObject5 = localObject2;
          localObject6 = contentsOfFile(paramWeakReference, paramString.replace(".stacktrace", ".user"));
          localObject4 = localObject1;
          localObject5 = localObject2;
          str2 = contentsOfFile(paramWeakReference, paramString.replace(".stacktrace", ".contact"));
          Object localObject7 = str2;
          localObject3 = localObject6;
          if (paramCrashMetaData != null)
          {
            localObject4 = localObject1;
            localObject5 = localObject2;
            localObject3 = paramCrashMetaData.getUserID();
            localObject4 = localObject1;
            localObject5 = localObject2;
            if (!TextUtils.isEmpty((CharSequence)localObject3)) {
              localObject6 = localObject3;
            }
            localObject4 = localObject1;
            localObject5 = localObject2;
            String str3 = paramCrashMetaData.getUserEmail();
            localObject7 = str2;
            localObject3 = localObject6;
            localObject4 = localObject1;
            localObject5 = localObject2;
            if (!TextUtils.isEmpty(str3))
            {
              localObject7 = str3;
              localObject3 = localObject6;
            }
          }
          localObject4 = localObject1;
          localObject5 = localObject2;
          str2 = contentsOfFile(paramWeakReference, paramString.replace(".stacktrace", ".description"));
          if (paramCrashMetaData != null)
          {
            localObject4 = localObject1;
            localObject5 = localObject2;
            localObject6 = paramCrashMetaData.getUserDescription();
            paramCrashMetaData = (CrashMetaData)localObject6;
            localObject4 = localObject1;
            localObject5 = localObject2;
            if (!TextUtils.isEmpty(str2))
            {
              localObject4 = localObject1;
              localObject5 = localObject2;
              if (TextUtils.isEmpty((CharSequence)localObject6)) {
                continue;
              }
              localObject4 = localObject1;
              localObject5 = localObject2;
              paramCrashMetaData = String.format("%s\n\nLog:\n%s", new Object[] { localObject6, str2 });
            }
            localObject4 = localObject1;
            localObject5 = localObject2;
            localObject6 = new java/util/HashMap;
            localObject4 = localObject1;
            localObject5 = localObject2;
            ((HashMap)localObject6).<init>();
            localObject4 = localObject1;
            localObject5 = localObject2;
            ((Map)localObject6).put("raw", str1);
            localObject4 = localObject1;
            localObject5 = localObject2;
            ((Map)localObject6).put("userID", localObject3);
            localObject4 = localObject1;
            localObject5 = localObject2;
            ((Map)localObject6).put("contact", localObject7);
            localObject4 = localObject1;
            localObject5 = localObject2;
            ((Map)localObject6).put("description", paramCrashMetaData);
            localObject4 = localObject1;
            localObject5 = localObject2;
            ((Map)localObject6).put("sdk", "HockeySDK");
            localObject4 = localObject1;
            localObject5 = localObject2;
            ((Map)localObject6).put("sdk_version", "5.0.4");
            localObject4 = localObject1;
            localObject5 = localObject2;
            paramCrashMetaData = new net/hockeyapp/android/utils/HttpURLConnectionBuilder;
            localObject4 = localObject1;
            localObject5 = localObject2;
            paramCrashMetaData.<init>(getURLString());
            localObject4 = localObject1;
            localObject5 = localObject2;
            paramCrashMetaData = paramCrashMetaData.setRequestMethod("POST").writeFormFields((Map)localObject6).build();
            localObject4 = paramCrashMetaData;
            localObject5 = paramCrashMetaData;
            int i = paramCrashMetaData.getResponseCode();
            if ((i != 202) && (i != 201)) {
              continue;
            }
            bool = true;
            localObject6 = Boolean.valueOf(bool);
            localObject3 = paramCrashMetaData;
          }
        }
        else
        {
          if (localObject3 != null) {
            ((HttpURLConnection)localObject3).disconnect();
          }
          if (!((Boolean)localObject6).booleanValue()) {
            continue;
          }
          HockeyLog.debug("Transmission succeeded");
          deleteStackTrace(paramWeakReference, paramString);
          if (paramCrashManagerListener != null)
          {
            paramCrashManagerListener.onCrashesSent();
            deleteRetryCounter(paramWeakReference, paramString);
          }
          return;
        }
      }
      catch (Exception paramCrashMetaData)
      {
        Object localObject6;
        String str2;
        boolean bool;
        localObject5 = localObject4;
        HockeyLog.error("Failed to transmit crash data", paramCrashMetaData);
        if (localObject4 == null) {
          continue;
        }
        ((HttpURLConnection)localObject4).disconnect();
        if (!localBoolean.booleanValue()) {
          continue;
        }
        HockeyLog.debug("Transmission succeeded");
        deleteStackTrace(paramWeakReference, paramString);
        if (paramCrashManagerListener == null) {
          continue;
        }
        paramCrashManagerListener.onCrashesSent();
        deleteRetryCounter(paramWeakReference, paramString);
        continue;
        HockeyLog.debug("Transmission failed, will retry on next register() call");
        if (paramCrashManagerListener == null) {
          continue;
        }
        paramCrashManagerListener.onCrashesNotSent();
        updateRetryCounter(paramWeakReference, paramString, paramCrashManagerListener.getMaxRetryAttempts());
        continue;
      }
      finally
      {
        if (localObject5 == null) {
          continue;
        }
        ((HttpURLConnection)localObject5).disconnect();
        if (!localBoolean.booleanValue()) {
          break label824;
        }
      }
      localObject6 = "";
      continue;
      localObject4 = localObject1;
      localObject5 = localObject2;
      paramCrashMetaData = String.format("Log:\n%s", new Object[] { str2 });
      continue;
      bool = false;
      continue;
      HockeyLog.debug("Transmission failed, will retry on next register() call");
      if (paramCrashManagerListener != null)
      {
        paramCrashManagerListener.onCrashesNotSent();
        updateRetryCounter(paramWeakReference, paramString, paramCrashManagerListener.getMaxRetryAttempts());
      }
    }
    HockeyLog.debug("Transmission succeeded");
    deleteStackTrace(paramWeakReference, paramString);
    if (paramCrashManagerListener != null)
    {
      paramCrashManagerListener.onCrashesSent();
      deleteRetryCounter(paramWeakReference, paramString);
    }
    for (;;)
    {
      throw paramCrashMetaData;
      label824:
      HockeyLog.debug("Transmission failed, will retry on next register() call");
      if (paramCrashManagerListener != null)
      {
        paramCrashManagerListener.onCrashesNotSent();
        updateRetryCounter(paramWeakReference, paramString, paramCrashManagerListener.getMaxRetryAttempts());
      }
    }
  }
  
  private static void updateRetryCounter(WeakReference<Context> paramWeakReference, String paramString, int paramInt)
  {
    if (paramInt == -1) {}
    for (;;)
    {
      return;
      if (paramWeakReference != null) {}
      int i;
      for (Object localObject = (Context)paramWeakReference.get();; localObject = null)
      {
        if (localObject == null) {
          break label90;
        }
        SharedPreferences localSharedPreferences = ((Context)localObject).getSharedPreferences("HockeySDK", 0);
        localObject = localSharedPreferences.edit();
        i = localSharedPreferences.getInt("RETRY_COUNT: " + paramString, 0);
        if (i < paramInt) {
          break label92;
        }
        deleteStackTrace(paramWeakReference, paramString);
        deleteRetryCounter(paramWeakReference, paramString);
        break;
      }
      label90:
      continue;
      label92:
      ((SharedPreferences.Editor)localObject).putInt("RETRY_COUNT: " + paramString, i + 1);
      ((SharedPreferences.Editor)localObject).apply();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/CrashManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */