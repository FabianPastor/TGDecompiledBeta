package net.hockeyapp.android.tasks;

import android.annotation.SuppressLint;
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
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy.Builder;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import net.hockeyapp.android.R.string;
import net.hockeyapp.android.listeners.DownloadFileListener;

@SuppressLint({"StaticFieldLeak"})
public class DownloadFileTask
  extends AsyncTask<Void, Integer, Long>
{
  protected Context mContext;
  protected File mDirectory;
  private String mDownloadErrorMessage;
  protected String mFilename;
  protected DownloadFileListener mNotifier;
  protected ProgressDialog mProgressDialog;
  protected String mUrlString;
  
  public DownloadFileTask(Context paramContext, String paramString, DownloadFileListener paramDownloadFileListener)
  {
    this.mContext = paramContext;
    this.mUrlString = paramString;
    this.mFilename = (UUID.randomUUID() + ".apk");
    this.mDirectory = new File(paramContext.getExternalFilesDir(null), "Download");
    this.mNotifier = paramDownloadFileListener;
    this.mDownloadErrorMessage = null;
  }
  
  protected URLConnection createConnection(URL paramURL, int paramInt)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURL.openConnection();
    setConnectionProperties(localHttpURLConnection);
    int i = localHttpURLConnection.getResponseCode();
    Object localObject;
    if ((i != 301) && (i != 302))
    {
      localObject = localHttpURLConnection;
      if (i != 303) {}
    }
    else
    {
      if (paramInt != 0) {
        break label56;
      }
      localObject = localHttpURLConnection;
    }
    for (;;)
    {
      return (URLConnection)localObject;
      label56:
      URL localURL = new URL(localHttpURLConnection.getHeaderField("Location"));
      localObject = localHttpURLConnection;
      if (!paramURL.getProtocol().equals(localURL.getProtocol()))
      {
        localHttpURLConnection.disconnect();
        localObject = createConnection(localURL, paramInt - 1);
      }
    }
  }
  
  /* Error */
  protected Long doInBackground(Void... paramVarArgs)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: aconst_null
    //   5: astore_1
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore 5
    //   12: aload_2
    //   13: astore 6
    //   15: aload_1
    //   16: astore 7
    //   18: new 83	java/net/URL
    //   21: astore 8
    //   23: aload_2
    //   24: astore 6
    //   26: aload_1
    //   27: astore 7
    //   29: aload 8
    //   31: aload_0
    //   32: invokevirtual 126	net/hockeyapp/android/tasks/DownloadFileTask:getURLString	()Ljava/lang/String;
    //   35: invokespecial 106	java/net/URL:<init>	(Ljava/lang/String;)V
    //   38: aload_2
    //   39: astore 6
    //   41: aload_1
    //   42: astore 7
    //   44: aload_0
    //   45: aload 8
    //   47: bipush 6
    //   49: invokevirtual 120	net/hockeyapp/android/tasks/DownloadFileTask:createConnection	(Ljava/net/URL;I)Ljava/net/URLConnection;
    //   52: astore 9
    //   54: aload_2
    //   55: astore 6
    //   57: aload_1
    //   58: astore 7
    //   60: aload 9
    //   62: invokevirtual 131	java/net/URLConnection:connect	()V
    //   65: aload_2
    //   66: astore 6
    //   68: aload_1
    //   69: astore 7
    //   71: aload 9
    //   73: invokevirtual 134	java/net/URLConnection:getContentLength	()I
    //   76: istore 10
    //   78: aload_2
    //   79: astore 6
    //   81: aload_1
    //   82: astore 7
    //   84: aload 9
    //   86: invokevirtual 137	java/net/URLConnection:getContentType	()Ljava/lang/String;
    //   89: astore 8
    //   91: aload 8
    //   93: ifnull +66 -> 159
    //   96: aload_2
    //   97: astore 6
    //   99: aload_1
    //   100: astore 7
    //   102: aload 8
    //   104: ldc -117
    //   106: invokevirtual 143	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   109: ifeq +50 -> 159
    //   112: aload_2
    //   113: astore 6
    //   115: aload_1
    //   116: astore 7
    //   118: aload_0
    //   119: ldc -111
    //   121: putfield 76	net/hockeyapp/android/tasks/DownloadFileTask:mDownloadErrorMessage	Ljava/lang/String;
    //   124: lconst_0
    //   125: invokestatic 151	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   128: astore_1
    //   129: iconst_0
    //   130: ifeq +11 -> 141
    //   133: new 153	java/lang/NullPointerException
    //   136: dup
    //   137: invokespecial 154	java/lang/NullPointerException:<init>	()V
    //   140: athrow
    //   141: aload_1
    //   142: astore 6
    //   144: iconst_0
    //   145: ifeq +11 -> 156
    //   148: new 153	java/lang/NullPointerException
    //   151: dup
    //   152: invokespecial 154	java/lang/NullPointerException:<init>	()V
    //   155: athrow
    //   156: aload 6
    //   158: areturn
    //   159: aload_2
    //   160: astore 6
    //   162: aload_1
    //   163: astore 7
    //   165: aload_0
    //   166: getfield 72	net/hockeyapp/android/tasks/DownloadFileTask:mDirectory	Ljava/io/File;
    //   169: invokevirtual 158	java/io/File:mkdirs	()Z
    //   172: ifne +187 -> 359
    //   175: aload_2
    //   176: astore 6
    //   178: aload_1
    //   179: astore 7
    //   181: aload_0
    //   182: getfield 72	net/hockeyapp/android/tasks/DownloadFileTask:mDirectory	Ljava/io/File;
    //   185: invokevirtual 161	java/io/File:exists	()Z
    //   188: ifne +171 -> 359
    //   191: aload_2
    //   192: astore 6
    //   194: aload_1
    //   195: astore 7
    //   197: new 81	java/io/IOException
    //   200: astore 4
    //   202: aload_2
    //   203: astore 6
    //   205: aload_1
    //   206: astore 7
    //   208: new 35	java/lang/StringBuilder
    //   211: astore 8
    //   213: aload_2
    //   214: astore 6
    //   216: aload_1
    //   217: astore 7
    //   219: aload 8
    //   221: invokespecial 36	java/lang/StringBuilder:<init>	()V
    //   224: aload_2
    //   225: astore 6
    //   227: aload_1
    //   228: astore 7
    //   230: aload 4
    //   232: aload 8
    //   234: ldc -93
    //   236: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: aload_0
    //   240: getfield 72	net/hockeyapp/android/tasks/DownloadFileTask:mDirectory	Ljava/io/File;
    //   243: invokevirtual 166	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   246: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   249: invokevirtual 55	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   252: invokespecial 167	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   255: aload_2
    //   256: astore 6
    //   258: aload_1
    //   259: astore 7
    //   261: aload 4
    //   263: athrow
    //   264: astore_2
    //   265: aload_3
    //   266: astore_1
    //   267: aload_1
    //   268: astore 6
    //   270: aload 5
    //   272: astore 7
    //   274: new 35	java/lang/StringBuilder
    //   277: astore_3
    //   278: aload_1
    //   279: astore 6
    //   281: aload 5
    //   283: astore 7
    //   285: aload_3
    //   286: invokespecial 36	java/lang/StringBuilder:<init>	()V
    //   289: aload_1
    //   290: astore 6
    //   292: aload 5
    //   294: astore 7
    //   296: aload_3
    //   297: ldc -87
    //   299: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   302: aload_0
    //   303: getfield 33	net/hockeyapp/android/tasks/DownloadFileTask:mUrlString	Ljava/lang/String;
    //   306: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   309: invokevirtual 55	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   312: aload_2
    //   313: invokestatic 175	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   316: lconst_0
    //   317: invokestatic 151	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   320: astore 7
    //   322: aload 5
    //   324: ifnull +8 -> 332
    //   327: aload 5
    //   329: invokevirtual 180	java/io/OutputStream:close	()V
    //   332: aload 7
    //   334: astore 6
    //   336: aload_1
    //   337: ifnull -181 -> 156
    //   340: aload_1
    //   341: invokevirtual 183	java/io/InputStream:close	()V
    //   344: aload 7
    //   346: astore 6
    //   348: goto -192 -> 156
    //   351: astore_1
    //   352: aload 7
    //   354: astore 6
    //   356: goto -200 -> 156
    //   359: aload_2
    //   360: astore 6
    //   362: aload_1
    //   363: astore 7
    //   365: new 59	java/io/File
    //   368: astore 8
    //   370: aload_2
    //   371: astore 6
    //   373: aload_1
    //   374: astore 7
    //   376: aload 8
    //   378: aload_0
    //   379: getfield 72	net/hockeyapp/android/tasks/DownloadFileTask:mDirectory	Ljava/io/File;
    //   382: aload_0
    //   383: getfield 57	net/hockeyapp/android/tasks/DownloadFileTask:mFilename	Ljava/lang/String;
    //   386: invokespecial 70	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   389: aload_2
    //   390: astore 6
    //   392: aload_1
    //   393: astore 7
    //   395: new 185	java/io/BufferedInputStream
    //   398: dup
    //   399: aload 9
    //   401: invokevirtual 189	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
    //   404: invokespecial 192	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   407: astore_1
    //   408: new 194	java/io/FileOutputStream
    //   411: astore 6
    //   413: aload 6
    //   415: aload 8
    //   417: invokespecial 197	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   420: sipush 1024
    //   423: newarray <illegal type>
    //   425: astore 7
    //   427: lconst_0
    //   428: lstore 11
    //   430: aload_1
    //   431: aload 7
    //   433: invokevirtual 201	java/io/InputStream:read	([B)I
    //   436: istore 13
    //   438: iload 13
    //   440: iconst_m1
    //   441: if_icmpeq +59 -> 500
    //   444: lload 11
    //   446: iload 13
    //   448: i2l
    //   449: ladd
    //   450: lstore 11
    //   452: aload_0
    //   453: iconst_1
    //   454: anewarray 203	java/lang/Integer
    //   457: dup
    //   458: iconst_0
    //   459: lload 11
    //   461: l2f
    //   462: ldc -52
    //   464: fmul
    //   465: iload 10
    //   467: i2f
    //   468: fdiv
    //   469: invokestatic 210	java/lang/Math:round	(F)I
    //   472: invokestatic 213	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   475: aastore
    //   476: invokevirtual 217	net/hockeyapp/android/tasks/DownloadFileTask:publishProgress	([Ljava/lang/Object;)V
    //   479: aload 6
    //   481: aload 7
    //   483: iconst_0
    //   484: iload 13
    //   486: invokevirtual 221	java/io/OutputStream:write	([BII)V
    //   489: goto -59 -> 430
    //   492: astore_2
    //   493: aload 6
    //   495: astore 5
    //   497: goto -230 -> 267
    //   500: aload 6
    //   502: invokevirtual 224	java/io/OutputStream:flush	()V
    //   505: lload 11
    //   507: invokestatic 151	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   510: astore 7
    //   512: aload 6
    //   514: ifnull +8 -> 522
    //   517: aload 6
    //   519: invokevirtual 180	java/io/OutputStream:close	()V
    //   522: aload_1
    //   523: ifnull +7 -> 530
    //   526: aload_1
    //   527: invokevirtual 183	java/io/InputStream:close	()V
    //   530: aload 7
    //   532: astore 6
    //   534: goto -378 -> 156
    //   537: astore_1
    //   538: aload 7
    //   540: ifnull +8 -> 548
    //   543: aload 7
    //   545: invokevirtual 180	java/io/OutputStream:close	()V
    //   548: aload 6
    //   550: ifnull +8 -> 558
    //   553: aload 6
    //   555: invokevirtual 183	java/io/InputStream:close	()V
    //   558: aload_1
    //   559: athrow
    //   560: astore 6
    //   562: goto -4 -> 558
    //   565: astore_2
    //   566: aload_1
    //   567: astore 6
    //   569: aload 4
    //   571: astore 7
    //   573: aload_2
    //   574: astore_1
    //   575: goto -37 -> 538
    //   578: astore_2
    //   579: aload 6
    //   581: astore 7
    //   583: aload_1
    //   584: astore 6
    //   586: aload_2
    //   587: astore_1
    //   588: goto -50 -> 538
    //   591: astore_2
    //   592: goto -325 -> 267
    //   595: astore_1
    //   596: goto -66 -> 530
    //   599: astore 6
    //   601: aload_1
    //   602: astore 6
    //   604: goto -448 -> 156
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	607	0	this	DownloadFileTask
    //   0	607	1	paramVarArgs	Void[]
    //   1	255	2	localObject1	Object
    //   264	126	2	localIOException1	IOException
    //   492	1	2	localIOException2	IOException
    //   565	9	2	localObject2	Object
    //   578	9	2	localObject3	Object
    //   591	1	2	localIOException3	IOException
    //   3	294	3	localStringBuilder	StringBuilder
    //   7	563	4	localIOException4	IOException
    //   10	486	5	localObject4	Object
    //   13	541	6	localObject5	Object
    //   560	1	6	localIOException5	IOException
    //   567	18	6	arrayOfVoid1	Void[]
    //   599	1	6	localIOException6	IOException
    //   602	1	6	arrayOfVoid2	Void[]
    //   16	566	7	localObject6	Object
    //   21	395	8	localObject7	Object
    //   52	348	9	localURLConnection	URLConnection
    //   76	390	10	i	int
    //   428	78	11	l	long
    //   436	49	13	j	int
    // Exception table:
    //   from	to	target	type
    //   18	23	264	java/io/IOException
    //   29	38	264	java/io/IOException
    //   44	54	264	java/io/IOException
    //   60	65	264	java/io/IOException
    //   71	78	264	java/io/IOException
    //   84	91	264	java/io/IOException
    //   102	112	264	java/io/IOException
    //   118	124	264	java/io/IOException
    //   165	175	264	java/io/IOException
    //   181	191	264	java/io/IOException
    //   197	202	264	java/io/IOException
    //   208	213	264	java/io/IOException
    //   219	224	264	java/io/IOException
    //   230	255	264	java/io/IOException
    //   261	264	264	java/io/IOException
    //   365	370	264	java/io/IOException
    //   376	389	264	java/io/IOException
    //   395	408	264	java/io/IOException
    //   327	332	351	java/io/IOException
    //   340	344	351	java/io/IOException
    //   420	427	492	java/io/IOException
    //   430	438	492	java/io/IOException
    //   452	489	492	java/io/IOException
    //   500	505	492	java/io/IOException
    //   18	23	537	finally
    //   29	38	537	finally
    //   44	54	537	finally
    //   60	65	537	finally
    //   71	78	537	finally
    //   84	91	537	finally
    //   102	112	537	finally
    //   118	124	537	finally
    //   165	175	537	finally
    //   181	191	537	finally
    //   197	202	537	finally
    //   208	213	537	finally
    //   219	224	537	finally
    //   230	255	537	finally
    //   261	264	537	finally
    //   274	278	537	finally
    //   285	289	537	finally
    //   296	316	537	finally
    //   365	370	537	finally
    //   376	389	537	finally
    //   395	408	537	finally
    //   543	548	560	java/io/IOException
    //   553	558	560	java/io/IOException
    //   408	420	565	finally
    //   420	427	578	finally
    //   430	438	578	finally
    //   452	489	578	finally
    //   500	505	578	finally
    //   408	420	591	java/io/IOException
    //   517	522	595	java/io/IOException
    //   526	530	595	java/io/IOException
    //   133	141	599	java/io/IOException
    //   148	156	599	java/io/IOException
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
      Object localObject;
      if (paramLong.longValue() > 0L)
      {
        this.mNotifier.downloadSuccessful(this);
        localObject = new Intent("android.intent.action.INSTALL_PACKAGE");
        ((Intent)localObject).setDataAndType(Uri.fromFile(new File(this.mDirectory, this.mFilename)), "application/vnd.android.package-archive");
        ((Intent)localObject).setFlags(268435456);
        paramLong = null;
        if (Build.VERSION.SDK_INT >= 24)
        {
          paramLong = StrictMode.getVmPolicy();
          StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().penaltyLog().build());
        }
        this.mContext.startActivity((Intent)localObject);
        if (paramLong != null) {
          StrictMode.setVmPolicy(paramLong);
        }
        return;
      }
      for (;;)
      {
        try
        {
          localObject = new android/app/AlertDialog$Builder;
          ((AlertDialog.Builder)localObject).<init>(this.mContext);
          ((AlertDialog.Builder)localObject).setTitle(R.string.hockeyapp_download_failed_dialog_title);
          if (this.mDownloadErrorMessage != null) {
            break label221;
          }
          paramLong = this.mContext.getString(R.string.hockeyapp_download_failed_dialog_message);
          ((AlertDialog.Builder)localObject).setMessage(paramLong);
          int i = R.string.hockeyapp_download_failed_dialog_negative_button;
          paramLong = new net/hockeyapp/android/tasks/DownloadFileTask$1;
          paramLong.<init>(this);
          ((AlertDialog.Builder)localObject).setNegativeButton(i, paramLong);
          i = R.string.hockeyapp_download_failed_dialog_positive_button;
          paramLong = new net/hockeyapp/android/tasks/DownloadFileTask$2;
          paramLong.<init>(this);
          ((AlertDialog.Builder)localObject).setPositiveButton(i, paramLong);
          ((AlertDialog.Builder)localObject).create().show();
        }
        catch (Exception paramLong) {}
        break;
        label221:
        paramLong = this.mDownloadErrorMessage;
      }
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  protected void onProgressUpdate(Integer... paramVarArgs)
  {
    try
    {
      if (this.mProgressDialog == null)
      {
        ProgressDialog localProgressDialog = new android/app/ProgressDialog;
        localProgressDialog.<init>(this.mContext);
        this.mProgressDialog = localProgressDialog;
        this.mProgressDialog.setProgressStyle(1);
        this.mProgressDialog.setMessage(this.mContext.getString(R.string.hockeyapp_update_loading));
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.show();
      }
      this.mProgressDialog.setProgress(paramVarArgs[0].intValue());
      return;
    }
    catch (Exception paramVarArgs)
    {
      for (;;) {}
    }
  }
  
  protected void setConnectionProperties(HttpURLConnection paramHttpURLConnection)
  {
    paramHttpURLConnection.addRequestProperty("User-Agent", "HockeySDK/Android 5.0.4");
    paramHttpURLConnection.setInstanceFollowRedirects(true);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/DownloadFileTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */