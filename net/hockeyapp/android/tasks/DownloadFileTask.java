package net.hockeyapp.android.tasks;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import net.hockeyapp.android.R.string;
import net.hockeyapp.android.listeners.DownloadFileListener;

public class DownloadFileTask
  extends AsyncTask<Void, Integer, Long>
{
  protected static final int MAX_REDIRECTS = 6;
  protected Context mContext;
  private String mDownloadErrorMessage;
  protected String mFilePath;
  protected String mFilename;
  protected DownloadFileListener mNotifier;
  protected ProgressDialog mProgressDialog;
  protected String mUrlString;
  
  public DownloadFileTask(Context paramContext, String paramString, DownloadFileListener paramDownloadFileListener)
  {
    this.mContext = paramContext;
    this.mUrlString = paramString;
    this.mFilename = (UUID.randomUUID() + ".apk");
    this.mFilePath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
    this.mNotifier = paramDownloadFileListener;
    this.mDownloadErrorMessage = null;
  }
  
  public void attach(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  protected URLConnection createConnection(URL paramURL, int paramInt)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURL.openConnection();
    setConnectionProperties(localHttpURLConnection);
    int i = localHttpURLConnection.getResponseCode();
    if (((i != 301) && (i != 302) && (i != 303)) || (paramInt == 0)) {}
    URL localURL;
    do
    {
      return localHttpURLConnection;
      localURL = new URL(localHttpURLConnection.getHeaderField("Location"));
    } while (paramURL.getProtocol().equals(localURL.getProtocol()));
    localHttpURLConnection.disconnect();
    return createConnection(localURL, paramInt - 1);
  }
  
  public void detach()
  {
    this.mContext = null;
    this.mProgressDialog = null;
  }
  
  /* Error */
  protected Long doInBackground(Void... paramVarArgs)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 11
    //   3: aconst_null
    //   4: astore 10
    //   6: aconst_null
    //   7: astore_1
    //   8: aconst_null
    //   9: astore 8
    //   11: aconst_null
    //   12: astore 9
    //   14: aload 11
    //   16: astore 6
    //   18: aload_1
    //   19: astore 7
    //   21: aload_0
    //   22: new 84	java/net/URL
    //   25: dup
    //   26: aload_0
    //   27: invokevirtual 130	net/hockeyapp/android/tasks/DownloadFileTask:getURLString	()Ljava/lang/String;
    //   30: invokespecial 107	java/net/URL:<init>	(Ljava/lang/String;)V
    //   33: bipush 6
    //   35: invokevirtual 121	net/hockeyapp/android/tasks/DownloadFileTask:createConnection	(Ljava/net/URL;I)Ljava/net/URLConnection;
    //   38: astore 12
    //   40: aload 11
    //   42: astore 6
    //   44: aload_1
    //   45: astore 7
    //   47: aload 12
    //   49: invokevirtual 135	java/net/URLConnection:connect	()V
    //   52: aload 11
    //   54: astore 6
    //   56: aload_1
    //   57: astore 7
    //   59: aload 12
    //   61: invokevirtual 138	java/net/URLConnection:getContentLength	()I
    //   64: istore_3
    //   65: aload 11
    //   67: astore 6
    //   69: aload_1
    //   70: astore 7
    //   72: aload 12
    //   74: invokevirtual 141	java/net/URLConnection:getContentType	()Ljava/lang/String;
    //   77: astore 13
    //   79: aload 13
    //   81: ifnull +77 -> 158
    //   84: aload 11
    //   86: astore 6
    //   88: aload_1
    //   89: astore 7
    //   91: aload 13
    //   93: ldc -113
    //   95: invokevirtual 147	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   98: ifeq +60 -> 158
    //   101: aload 11
    //   103: astore 6
    //   105: aload_1
    //   106: astore 7
    //   108: aload_0
    //   109: ldc -107
    //   111: putfield 75	net/hockeyapp/android/tasks/DownloadFileTask:mDownloadErrorMessage	Ljava/lang/String;
    //   114: lconst_0
    //   115: invokestatic 155	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   118: astore_1
    //   119: iconst_0
    //   120: ifeq +11 -> 131
    //   123: new 157	java/lang/NullPointerException
    //   126: dup
    //   127: invokespecial 158	java/lang/NullPointerException:<init>	()V
    //   130: athrow
    //   131: aload_1
    //   132: astore 6
    //   134: iconst_0
    //   135: ifeq +11 -> 146
    //   138: new 157	java/lang/NullPointerException
    //   141: dup
    //   142: invokespecial 158	java/lang/NullPointerException:<init>	()V
    //   145: athrow
    //   146: aload 6
    //   148: areturn
    //   149: astore 6
    //   151: aload 6
    //   153: invokevirtual 161	java/io/IOException:printStackTrace	()V
    //   156: aload_1
    //   157: areturn
    //   158: aload 11
    //   160: astore 6
    //   162: aload_1
    //   163: astore 7
    //   165: new 64	java/io/File
    //   168: dup
    //   169: aload_0
    //   170: getfield 71	net/hockeyapp/android/tasks/DownloadFileTask:mFilePath	Ljava/lang/String;
    //   173: invokespecial 162	java/io/File:<init>	(Ljava/lang/String;)V
    //   176: astore 13
    //   178: aload 11
    //   180: astore 6
    //   182: aload_1
    //   183: astore 7
    //   185: aload 13
    //   187: invokevirtual 166	java/io/File:mkdirs	()Z
    //   190: ifne +112 -> 302
    //   193: aload 11
    //   195: astore 6
    //   197: aload_1
    //   198: astore 7
    //   200: aload 13
    //   202: invokevirtual 169	java/io/File:exists	()Z
    //   205: ifne +97 -> 302
    //   208: aload 11
    //   210: astore 6
    //   212: aload_1
    //   213: astore 7
    //   215: new 82	java/io/IOException
    //   218: dup
    //   219: new 34	java/lang/StringBuilder
    //   222: dup
    //   223: invokespecial 35	java/lang/StringBuilder:<init>	()V
    //   226: ldc -85
    //   228: invokevirtual 50	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   231: aload 13
    //   233: invokevirtual 67	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   236: invokevirtual 50	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: invokevirtual 54	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   242: invokespecial 172	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   245: athrow
    //   246: astore 8
    //   248: aload 10
    //   250: astore_1
    //   251: aload_1
    //   252: astore 6
    //   254: aload 9
    //   256: astore 7
    //   258: aload 8
    //   260: invokevirtual 161	java/io/IOException:printStackTrace	()V
    //   263: lconst_0
    //   264: invokestatic 155	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   267: astore 7
    //   269: aload 9
    //   271: ifnull +8 -> 279
    //   274: aload 9
    //   276: invokevirtual 177	java/io/OutputStream:close	()V
    //   279: aload 7
    //   281: astore 6
    //   283: aload_1
    //   284: ifnull -138 -> 146
    //   287: aload_1
    //   288: invokevirtual 180	java/io/InputStream:close	()V
    //   291: aload 7
    //   293: areturn
    //   294: astore_1
    //   295: aload_1
    //   296: invokevirtual 161	java/io/IOException:printStackTrace	()V
    //   299: aload 7
    //   301: areturn
    //   302: aload 11
    //   304: astore 6
    //   306: aload_1
    //   307: astore 7
    //   309: new 64	java/io/File
    //   312: dup
    //   313: aload 13
    //   315: aload_0
    //   316: getfield 56	net/hockeyapp/android/tasks/DownloadFileTask:mFilename	Ljava/lang/String;
    //   319: invokespecial 183	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   322: astore 13
    //   324: aload 11
    //   326: astore 6
    //   328: aload_1
    //   329: astore 7
    //   331: new 185	java/io/BufferedInputStream
    //   334: dup
    //   335: aload 12
    //   337: invokevirtual 189	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
    //   340: invokespecial 192	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   343: astore_1
    //   344: new 194	java/io/FileOutputStream
    //   347: dup
    //   348: aload 13
    //   350: invokespecial 197	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   353: astore 6
    //   355: sipush 1024
    //   358: newarray <illegal type>
    //   360: astore 7
    //   362: lconst_0
    //   363: lstore 4
    //   365: aload_1
    //   366: aload 7
    //   368: invokevirtual 201	java/io/InputStream:read	([B)I
    //   371: istore_2
    //   372: iload_2
    //   373: iconst_m1
    //   374: if_icmpeq +48 -> 422
    //   377: lload 4
    //   379: iload_2
    //   380: i2l
    //   381: ladd
    //   382: lstore 4
    //   384: aload_0
    //   385: iconst_1
    //   386: anewarray 203	java/lang/Integer
    //   389: dup
    //   390: iconst_0
    //   391: lload 4
    //   393: l2f
    //   394: ldc -52
    //   396: fmul
    //   397: iload_3
    //   398: i2f
    //   399: fdiv
    //   400: invokestatic 210	java/lang/Math:round	(F)I
    //   403: invokestatic 213	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   406: aastore
    //   407: invokevirtual 217	net/hockeyapp/android/tasks/DownloadFileTask:publishProgress	([Ljava/lang/Object;)V
    //   410: aload 6
    //   412: aload 7
    //   414: iconst_0
    //   415: iload_2
    //   416: invokevirtual 221	java/io/OutputStream:write	([BII)V
    //   419: goto -54 -> 365
    //   422: aload 6
    //   424: invokevirtual 224	java/io/OutputStream:flush	()V
    //   427: aload 6
    //   429: ifnull +8 -> 437
    //   432: aload 6
    //   434: invokevirtual 177	java/io/OutputStream:close	()V
    //   437: aload_1
    //   438: ifnull +7 -> 445
    //   441: aload_1
    //   442: invokevirtual 180	java/io/InputStream:close	()V
    //   445: lload 4
    //   447: invokestatic 155	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   450: areturn
    //   451: astore_1
    //   452: aload_1
    //   453: invokevirtual 161	java/io/IOException:printStackTrace	()V
    //   456: goto -11 -> 445
    //   459: astore_1
    //   460: aload 7
    //   462: ifnull +8 -> 470
    //   465: aload 7
    //   467: invokevirtual 177	java/io/OutputStream:close	()V
    //   470: aload 6
    //   472: ifnull +8 -> 480
    //   475: aload 6
    //   477: invokevirtual 180	java/io/InputStream:close	()V
    //   480: aload_1
    //   481: athrow
    //   482: astore 6
    //   484: aload 6
    //   486: invokevirtual 161	java/io/IOException:printStackTrace	()V
    //   489: goto -9 -> 480
    //   492: astore 9
    //   494: aload_1
    //   495: astore 6
    //   497: aload 8
    //   499: astore 7
    //   501: aload 9
    //   503: astore_1
    //   504: goto -44 -> 460
    //   507: astore 8
    //   509: aload 6
    //   511: astore 7
    //   513: aload_1
    //   514: astore 6
    //   516: aload 8
    //   518: astore_1
    //   519: goto -59 -> 460
    //   522: astore 8
    //   524: goto -273 -> 251
    //   527: astore 8
    //   529: aload 6
    //   531: astore 9
    //   533: goto -282 -> 251
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	536	0	this	DownloadFileTask
    //   0	536	1	paramVarArgs	Void[]
    //   371	45	2	i	int
    //   64	334	3	j	int
    //   363	83	4	l	long
    //   16	131	6	localObject1	Object
    //   149	3	6	localIOException1	IOException
    //   160	316	6	localObject2	Object
    //   482	3	6	localIOException2	IOException
    //   495	35	6	arrayOfVoid	Void[]
    //   19	493	7	localObject3	Object
    //   9	1	8	localObject4	Object
    //   246	252	8	localIOException3	IOException
    //   507	10	8	localObject5	Object
    //   522	1	8	localIOException4	IOException
    //   527	1	8	localIOException5	IOException
    //   12	263	9	localObject6	Object
    //   492	10	9	localObject7	Object
    //   531	1	9	localObject8	Object
    //   4	245	10	localObject9	Object
    //   1	324	11	localObject10	Object
    //   38	298	12	localURLConnection	URLConnection
    //   77	272	13	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   123	131	149	java/io/IOException
    //   138	146	149	java/io/IOException
    //   21	40	246	java/io/IOException
    //   47	52	246	java/io/IOException
    //   59	65	246	java/io/IOException
    //   72	79	246	java/io/IOException
    //   91	101	246	java/io/IOException
    //   108	114	246	java/io/IOException
    //   165	178	246	java/io/IOException
    //   185	193	246	java/io/IOException
    //   200	208	246	java/io/IOException
    //   215	246	246	java/io/IOException
    //   309	324	246	java/io/IOException
    //   331	344	246	java/io/IOException
    //   274	279	294	java/io/IOException
    //   287	291	294	java/io/IOException
    //   432	437	451	java/io/IOException
    //   441	445	451	java/io/IOException
    //   21	40	459	finally
    //   47	52	459	finally
    //   59	65	459	finally
    //   72	79	459	finally
    //   91	101	459	finally
    //   108	114	459	finally
    //   165	178	459	finally
    //   185	193	459	finally
    //   200	208	459	finally
    //   215	246	459	finally
    //   258	263	459	finally
    //   309	324	459	finally
    //   331	344	459	finally
    //   465	470	482	java/io/IOException
    //   475	480	482	java/io/IOException
    //   344	355	492	finally
    //   355	362	507	finally
    //   365	372	507	finally
    //   384	419	507	finally
    //   422	427	507	finally
    //   344	355	522	java/io/IOException
    //   355	362	527	java/io/IOException
    //   365	372	527	java/io/IOException
    //   384	419	527	java/io/IOException
    //   422	427	527	java/io/IOException
  }
  
  protected String getURLString()
  {
    return this.mUrlString + "&type=apk";
  }
  
  protected void onPostExecute(Long paramLong)
  {
    if (this.mProgressDialog != null) {}
    try
    {
      this.mProgressDialog.dismiss();
      if (paramLong.longValue() > 0L)
      {
        this.mNotifier.downloadSuccessful(this);
        paramLong = new Intent("android.intent.action.VIEW");
        paramLong.setDataAndType(Uri.fromFile(new File(this.mFilePath, this.mFilename)), "application/vnd.android.package-archive");
        paramLong.setFlags(268435456);
        this.mContext.startActivity(paramLong);
        return;
      }
    }
    catch (Exception localException)
    {
      try
      {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.mContext);
        localBuilder.setTitle(R.string.hockeyapp_download_failed_dialog_title);
        if (this.mDownloadErrorMessage == null) {}
        for (paramLong = this.mContext.getString(R.string.hockeyapp_download_failed_dialog_message);; paramLong = this.mDownloadErrorMessage)
        {
          localBuilder.setMessage(paramLong);
          localBuilder.setNegativeButton(R.string.hockeyapp_download_failed_dialog_negative_button, new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              DownloadFileTask.this.mNotifier.downloadFailed(DownloadFileTask.this, Boolean.valueOf(false));
            }
          });
          localBuilder.setPositiveButton(R.string.hockeyapp_download_failed_dialog_positive_button, new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              DownloadFileTask.this.mNotifier.downloadFailed(DownloadFileTask.this, Boolean.valueOf(true));
            }
          });
          localBuilder.create().show();
          return;
        }
        localException = localException;
      }
      catch (Exception paramLong) {}
    }
  }
  
  protected void onProgressUpdate(Integer... paramVarArgs)
  {
    try
    {
      if (this.mProgressDialog == null)
      {
        this.mProgressDialog = new ProgressDialog(this.mContext);
        this.mProgressDialog.setProgressStyle(1);
        this.mProgressDialog.setMessage("Loading...");
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.show();
      }
      this.mProgressDialog.setProgress(paramVarArgs[0].intValue());
      return;
    }
    catch (Exception paramVarArgs) {}
  }
  
  protected void setConnectionProperties(HttpURLConnection paramHttpURLConnection)
  {
    paramHttpURLConnection.addRequestProperty("User-Agent", "HockeySDK/Android");
    paramHttpURLConnection.setInstanceFollowRedirects(true);
    if (Build.VERSION.SDK_INT <= 9) {
      paramHttpURLConnection.setRequestProperty("connection", "close");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/DownloadFileTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */