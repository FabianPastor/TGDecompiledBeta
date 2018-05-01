package android.support.v4.print;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentAdapter.WriteResultCallback;
import android.print.PrintDocumentInfo.Builder;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@TargetApi(19)
@RequiresApi(19)
class PrintHelperKitkat
{
  public static final int COLOR_MODE_COLOR = 2;
  public static final int COLOR_MODE_MONOCHROME = 1;
  private static final String LOG_TAG = "PrintHelperKitkat";
  private static final int MAX_PRINT_SIZE = 3500;
  public static final int ORIENTATION_LANDSCAPE = 1;
  public static final int ORIENTATION_PORTRAIT = 2;
  public static final int SCALE_MODE_FILL = 2;
  public static final int SCALE_MODE_FIT = 1;
  int mColorMode = 2;
  final Context mContext;
  BitmapFactory.Options mDecodeOptions = null;
  protected boolean mIsMinMarginsHandlingCorrect = true;
  private final Object mLock = new Object();
  int mOrientation;
  protected boolean mPrintActivityRespectsOrientation = true;
  int mScaleMode = 2;
  
  PrintHelperKitkat(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private Bitmap convertBitmapForColorMode(Bitmap paramBitmap, int paramInt)
  {
    if (paramInt != 1) {
      return paramBitmap;
    }
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint();
    ColorMatrix localColorMatrix = new ColorMatrix();
    localColorMatrix.setSaturation(0.0F);
    localPaint.setColorFilter(new ColorMatrixColorFilter(localColorMatrix));
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    localCanvas.setBitmap(null);
    return localBitmap;
  }
  
  private Matrix getMatrix(int paramInt1, int paramInt2, RectF paramRectF, int paramInt3)
  {
    Matrix localMatrix = new Matrix();
    float f = paramRectF.width() / paramInt1;
    if (paramInt3 == 2) {}
    for (f = Math.max(f, paramRectF.height() / paramInt2);; f = Math.min(f, paramRectF.height() / paramInt2))
    {
      localMatrix.postScale(f, f);
      localMatrix.postTranslate((paramRectF.width() - paramInt1 * f) / 2.0F, (paramRectF.height() - paramInt2 * f) / 2.0F);
      return localMatrix;
    }
  }
  
  private static boolean isPortrait(Bitmap paramBitmap)
  {
    return paramBitmap.getWidth() <= paramBitmap.getHeight();
  }
  
  private Bitmap loadBitmap(Uri paramUri, BitmapFactory.Options paramOptions)
    throws FileNotFoundException
  {
    if ((paramUri == null) || (this.mContext == null)) {
      throw new IllegalArgumentException("bad argument to loadBitmap");
    }
    localUri = null;
    try
    {
      paramUri = this.mContext.getContentResolver().openInputStream(paramUri);
      localUri = paramUri;
      paramOptions = BitmapFactory.decodeStream(paramUri, null, paramOptions);
      if (paramUri != null) {}
      try
      {
        paramUri.close();
        return paramOptions;
      }
      catch (IOException paramUri)
      {
        Log.w("PrintHelperKitkat", "close fail ", paramUri);
        return paramOptions;
      }
      try
      {
        localUri.close();
        throw paramUri;
      }
      catch (IOException paramOptions)
      {
        for (;;)
        {
          Log.w("PrintHelperKitkat", "close fail ", paramOptions);
        }
      }
    }
    finally
    {
      if (localUri == null) {}
    }
  }
  
  private Bitmap loadConstrainedBitmap(Uri arg1, int paramInt)
    throws FileNotFoundException
  {
    if ((paramInt <= 0) || (??? == null) || (this.mContext == null)) {
      throw new IllegalArgumentException("bad argument to getScaledBitmap");
    }
    ??? = new BitmapFactory.Options();
    ((BitmapFactory.Options)???).inJustDecodeBounds = true;
    loadBitmap(???, (BitmapFactory.Options)???);
    int k = ((BitmapFactory.Options)???).outWidth;
    int m = ((BitmapFactory.Options)???).outHeight;
    if ((k <= 0) || (m <= 0)) {}
    int i;
    do
    {
      return null;
      int j = Math.max(k, m);
      i = 1;
      while (j > paramInt)
      {
        j >>>= 1;
        i <<= 1;
      }
    } while ((i <= 0) || (Math.min(k, m) / i <= 0));
    BitmapFactory.Options localOptions;
    synchronized (this.mLock)
    {
      this.mDecodeOptions = new BitmapFactory.Options();
      this.mDecodeOptions.inMutable = true;
      this.mDecodeOptions.inSampleSize = i;
      localOptions = this.mDecodeOptions;
    }
    try
    {
      ??? = loadBitmap(???, localOptions);
      synchronized (this.mLock)
      {
        this.mDecodeOptions = null;
        return (Bitmap)???;
      }
      ??? = finally;
      throw ???;
    }
    finally {}
  }
  
  private void writeBitmap(final PrintAttributes paramPrintAttributes, final int paramInt, final Bitmap paramBitmap, final ParcelFileDescriptor paramParcelFileDescriptor, final CancellationSignal paramCancellationSignal, final PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
  {
    if (this.mIsMinMarginsHandlingCorrect) {}
    for (final PrintAttributes localPrintAttributes = paramPrintAttributes;; localPrintAttributes = copyAttributes(paramPrintAttributes).setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0)).build())
    {
      new AsyncTask()
      {
        /* Error */
        protected Throwable doInBackground(Void... paramAnonymousVarArgs)
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   4: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
          //   7: ifeq +5 -> 12
          //   10: aconst_null
          //   11: areturn
          //   12: new 66	android/print/pdf/PrintedPdfDocument
          //   15: dup
          //   16: aload_0
          //   17: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   20: getfield 70	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
          //   23: aload_0
          //   24: getfield 33	android/support/v4/print/PrintHelperKitkat$2:val$pdfAttributes	Landroid/print/PrintAttributes;
          //   27: invokespecial 73	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
          //   30: astore 4
          //   32: aload_0
          //   33: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   36: aload_0
          //   37: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   40: aload_0
          //   41: getfield 33	android/support/v4/print/PrintHelperKitkat$2:val$pdfAttributes	Landroid/print/PrintAttributes;
          //   44: invokevirtual 79	android/print/PrintAttributes:getColorMode	()I
          //   47: invokestatic 83	android/support/v4/print/PrintHelperKitkat:access$100	(Landroid/support/v4/print/PrintHelperKitkat;Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
          //   50: astore_3
          //   51: aload_0
          //   52: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   55: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
          //   58: istore_2
          //   59: iload_2
          //   60: ifne +329 -> 389
          //   63: aload 4
          //   65: iconst_1
          //   66: invokevirtual 87	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
          //   69: astore 5
          //   71: aload_0
          //   72: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   75: getfield 91	android/support/v4/print/PrintHelperKitkat:mIsMinMarginsHandlingCorrect	Z
          //   78: ifeq +120 -> 198
          //   81: new 93	android/graphics/RectF
          //   84: dup
          //   85: aload 5
          //   87: invokevirtual 99	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
          //   90: invokevirtual 105	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
          //   93: invokespecial 108	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
          //   96: astore_1
          //   97: aload_0
          //   98: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   101: aload_3
          //   102: invokevirtual 113	android/graphics/Bitmap:getWidth	()I
          //   105: aload_3
          //   106: invokevirtual 116	android/graphics/Bitmap:getHeight	()I
          //   109: aload_1
          //   110: aload_0
          //   111: getfield 39	android/support/v4/print/PrintHelperKitkat$2:val$fittingMode	I
          //   114: invokestatic 120	android/support/v4/print/PrintHelperKitkat:access$200	(Landroid/support/v4/print/PrintHelperKitkat;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
          //   117: astore 6
          //   119: aload_0
          //   120: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   123: getfield 91	android/support/v4/print/PrintHelperKitkat:mIsMinMarginsHandlingCorrect	Z
          //   126: ifeq +169 -> 295
          //   129: aload 5
          //   131: invokevirtual 124	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
          //   134: aload_3
          //   135: aload 6
          //   137: aconst_null
          //   138: invokevirtual 130	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
          //   141: aload 4
          //   143: aload 5
          //   145: invokevirtual 134	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
          //   148: aload_0
          //   149: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   152: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
          //   155: istore_2
          //   156: iload_2
          //   157: ifeq +165 -> 322
          //   160: aload 4
          //   162: invokevirtual 137	android/print/pdf/PrintedPdfDocument:close	()V
          //   165: aload_0
          //   166: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   169: astore_1
          //   170: aload_1
          //   171: ifnull +10 -> 181
          //   174: aload_0
          //   175: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   178: invokevirtual 140	android/os/ParcelFileDescriptor:close	()V
          //   181: aload_3
          //   182: aload_0
          //   183: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   186: if_acmpeq +203 -> 389
          //   189: aload_3
          //   190: invokevirtual 143	android/graphics/Bitmap:recycle	()V
          //   193: aconst_null
          //   194: areturn
          //   195: astore_1
          //   196: aload_1
          //   197: areturn
          //   198: new 66	android/print/pdf/PrintedPdfDocument
          //   201: dup
          //   202: aload_0
          //   203: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   206: getfield 70	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
          //   209: aload_0
          //   210: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$attributes	Landroid/print/PrintAttributes;
          //   213: invokespecial 73	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
          //   216: astore 6
          //   218: aload 6
          //   220: iconst_1
          //   221: invokevirtual 87	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
          //   224: astore 7
          //   226: new 93	android/graphics/RectF
          //   229: dup
          //   230: aload 7
          //   232: invokevirtual 99	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
          //   235: invokevirtual 105	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
          //   238: invokespecial 108	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
          //   241: astore_1
          //   242: aload 6
          //   244: aload 7
          //   246: invokevirtual 134	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
          //   249: aload 6
          //   251: invokevirtual 137	android/print/pdf/PrintedPdfDocument:close	()V
          //   254: goto -157 -> 97
          //   257: astore_1
          //   258: aload 4
          //   260: invokevirtual 137	android/print/pdf/PrintedPdfDocument:close	()V
          //   263: aload_0
          //   264: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   267: astore 4
          //   269: aload 4
          //   271: ifnull +10 -> 281
          //   274: aload_0
          //   275: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   278: invokevirtual 140	android/os/ParcelFileDescriptor:close	()V
          //   281: aload_3
          //   282: aload_0
          //   283: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   286: if_acmpeq +7 -> 293
          //   289: aload_3
          //   290: invokevirtual 143	android/graphics/Bitmap:recycle	()V
          //   293: aload_1
          //   294: athrow
          //   295: aload 6
          //   297: aload_1
          //   298: getfield 147	android/graphics/RectF:left	F
          //   301: aload_1
          //   302: getfield 150	android/graphics/RectF:top	F
          //   305: invokevirtual 156	android/graphics/Matrix:postTranslate	(FF)Z
          //   308: pop
          //   309: aload 5
          //   311: invokevirtual 124	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
          //   314: aload_1
          //   315: invokevirtual 160	android/graphics/Canvas:clipRect	(Landroid/graphics/RectF;)Z
          //   318: pop
          //   319: goto -190 -> 129
          //   322: aload 4
          //   324: new 162	java/io/FileOutputStream
          //   327: dup
          //   328: aload_0
          //   329: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   332: invokevirtual 166	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
          //   335: invokespecial 169	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
          //   338: invokevirtual 173	android/print/pdf/PrintedPdfDocument:writeTo	(Ljava/io/OutputStream;)V
          //   341: aload 4
          //   343: invokevirtual 137	android/print/pdf/PrintedPdfDocument:close	()V
          //   346: aload_0
          //   347: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   350: astore_1
          //   351: aload_1
          //   352: ifnull +10 -> 362
          //   355: aload_0
          //   356: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   359: invokevirtual 140	android/os/ParcelFileDescriptor:close	()V
          //   362: aload_3
          //   363: aload_0
          //   364: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   367: if_acmpeq +22 -> 389
          //   370: aload_3
          //   371: invokevirtual 143	android/graphics/Bitmap:recycle	()V
          //   374: aconst_null
          //   375: areturn
          //   376: astore 4
          //   378: goto -97 -> 281
          //   381: astore_1
          //   382: goto -20 -> 362
          //   385: astore_1
          //   386: goto -205 -> 181
          //   389: aconst_null
          //   390: areturn
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	391	0	this	2
          //   0	391	1	paramAnonymousVarArgs	Void[]
          //   58	99	2	bool	boolean
          //   50	321	3	localBitmap	Bitmap
          //   30	312	4	localObject1	Object
          //   376	1	4	localIOException	IOException
          //   69	241	5	localPage1	android.graphics.pdf.PdfDocument.Page
          //   117	179	6	localObject2	Object
          //   224	21	7	localPage2	android.graphics.pdf.PdfDocument.Page
          // Exception table:
          //   from	to	target	type
          //   0	10	195	java/lang/Throwable
          //   12	59	195	java/lang/Throwable
          //   160	170	195	java/lang/Throwable
          //   174	181	195	java/lang/Throwable
          //   181	193	195	java/lang/Throwable
          //   258	269	195	java/lang/Throwable
          //   274	281	195	java/lang/Throwable
          //   281	293	195	java/lang/Throwable
          //   293	295	195	java/lang/Throwable
          //   341	351	195	java/lang/Throwable
          //   355	362	195	java/lang/Throwable
          //   362	374	195	java/lang/Throwable
          //   63	97	257	finally
          //   97	129	257	finally
          //   129	156	257	finally
          //   198	254	257	finally
          //   295	319	257	finally
          //   322	341	257	finally
          //   274	281	376	java/io/IOException
          //   355	362	381	java/io/IOException
          //   174	181	385	java/io/IOException
        }
        
        protected void onPostExecute(Throwable paramAnonymousThrowable)
        {
          if (paramCancellationSignal.isCanceled())
          {
            paramWriteResultCallback.onWriteCancelled();
            return;
          }
          if (paramAnonymousThrowable == null)
          {
            paramWriteResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
            return;
          }
          Log.e("PrintHelperKitkat", "Error writing printed content", paramAnonymousThrowable);
          paramWriteResultCallback.onWriteFailed(null);
        }
      }.execute(new Void[0]);
      return;
    }
  }
  
  protected PrintAttributes.Builder copyAttributes(PrintAttributes paramPrintAttributes)
  {
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder().setMediaSize(paramPrintAttributes.getMediaSize()).setResolution(paramPrintAttributes.getResolution()).setMinMargins(paramPrintAttributes.getMinMargins());
    if (paramPrintAttributes.getColorMode() != 0) {
      localBuilder.setColorMode(paramPrintAttributes.getColorMode());
    }
    return localBuilder;
  }
  
  public int getColorMode()
  {
    return this.mColorMode;
  }
  
  public int getOrientation()
  {
    if (this.mOrientation == 0) {
      return 1;
    }
    return this.mOrientation;
  }
  
  public int getScaleMode()
  {
    return this.mScaleMode;
  }
  
  public void printBitmap(final String paramString, final Bitmap paramBitmap, final OnPrintFinishCallback paramOnPrintFinishCallback)
  {
    if (paramBitmap == null) {
      return;
    }
    final int i = this.mScaleMode;
    PrintManager localPrintManager = (PrintManager)this.mContext.getSystemService("print");
    if (isPortrait(paramBitmap)) {}
    for (Object localObject = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;; localObject = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE)
    {
      localObject = new PrintAttributes.Builder().setMediaSize((PrintAttributes.MediaSize)localObject).setColorMode(this.mColorMode).build();
      localPrintManager.print(paramString, new PrintDocumentAdapter()
      {
        private PrintAttributes mAttributes;
        
        public void onFinish()
        {
          if (paramOnPrintFinishCallback != null) {
            paramOnPrintFinishCallback.onFinish();
          }
        }
        
        public void onLayout(PrintAttributes paramAnonymousPrintAttributes1, PrintAttributes paramAnonymousPrintAttributes2, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
        {
          boolean bool = true;
          this.mAttributes = paramAnonymousPrintAttributes2;
          paramAnonymousCancellationSignal = new PrintDocumentInfo.Builder(paramString).setContentType(1).setPageCount(1).build();
          if (!paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {}
          for (;;)
          {
            paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymousCancellationSignal, bool);
            return;
            bool = false;
          }
        }
        
        public void onWrite(PageRange[] paramAnonymousArrayOfPageRange, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
        {
          PrintHelperKitkat.this.writeBitmap(this.mAttributes, i, paramBitmap, paramAnonymousParcelFileDescriptor, paramAnonymousCancellationSignal, paramAnonymousWriteResultCallback);
        }
      }, (PrintAttributes)localObject);
      return;
    }
  }
  
  public void printBitmap(final String paramString, final Uri paramUri, final OnPrintFinishCallback paramOnPrintFinishCallback)
    throws FileNotFoundException
  {
    paramUri = new PrintDocumentAdapter()
    {
      private PrintAttributes mAttributes;
      Bitmap mBitmap = null;
      AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
      
      private void cancelLoad()
      {
        synchronized (PrintHelperKitkat.this.mLock)
        {
          if (PrintHelperKitkat.this.mDecodeOptions != null)
          {
            PrintHelperKitkat.this.mDecodeOptions.requestCancelDecode();
            PrintHelperKitkat.this.mDecodeOptions = null;
          }
          return;
        }
      }
      
      public void onFinish()
      {
        super.onFinish();
        cancelLoad();
        if (this.mLoadBitmap != null) {
          this.mLoadBitmap.cancel(true);
        }
        if (paramOnPrintFinishCallback != null) {
          paramOnPrintFinishCallback.onFinish();
        }
        if (this.mBitmap != null)
        {
          this.mBitmap.recycle();
          this.mBitmap = null;
        }
      }
      
      public void onLayout(final PrintAttributes paramAnonymousPrintAttributes1, final PrintAttributes paramAnonymousPrintAttributes2, final CancellationSignal paramAnonymousCancellationSignal, final PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
      {
        boolean bool = true;
        try
        {
          this.mAttributes = paramAnonymousPrintAttributes2;
          if (paramAnonymousCancellationSignal.isCanceled())
          {
            paramAnonymousLayoutResultCallback.onLayoutCancelled();
            return;
          }
        }
        finally {}
        if (this.mBitmap != null)
        {
          paramAnonymousCancellationSignal = new PrintDocumentInfo.Builder(paramString).setContentType(1).setPageCount(1).build();
          if (!paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {}
          for (;;)
          {
            paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymousCancellationSignal, bool);
            return;
            bool = false;
          }
        }
        this.mLoadBitmap = new AsyncTask()
        {
          protected Bitmap doInBackground(Uri... paramAnonymous2VarArgs)
          {
            try
            {
              paramAnonymous2VarArgs = PrintHelperKitkat.this.loadConstrainedBitmap(PrintHelperKitkat.3.this.val$imageFile, 3500);
              return paramAnonymous2VarArgs;
            }
            catch (FileNotFoundException paramAnonymous2VarArgs) {}
            return null;
          }
          
          protected void onCancelled(Bitmap paramAnonymous2Bitmap)
          {
            paramAnonymousLayoutResultCallback.onLayoutCancelled();
            PrintHelperKitkat.3.this.mLoadBitmap = null;
          }
          
          protected void onPostExecute(Bitmap paramAnonymous2Bitmap)
          {
            super.onPostExecute(paramAnonymous2Bitmap);
            Object localObject = paramAnonymous2Bitmap;
            if (paramAnonymous2Bitmap != null) {
              if (PrintHelperKitkat.this.mPrintActivityRespectsOrientation)
              {
                localObject = paramAnonymous2Bitmap;
                if (PrintHelperKitkat.this.mOrientation != 0) {
                  break label108;
                }
              }
            }
            for (;;)
            {
              try
              {
                PrintAttributes.MediaSize localMediaSize = PrintHelperKitkat.3.this.mAttributes.getMediaSize();
                localObject = paramAnonymous2Bitmap;
                if (localMediaSize != null)
                {
                  localObject = paramAnonymous2Bitmap;
                  if (localMediaSize.isPortrait() != PrintHelperKitkat.isPortrait(paramAnonymous2Bitmap))
                  {
                    localObject = new Matrix();
                    ((Matrix)localObject).postRotate(90.0F);
                    localObject = Bitmap.createBitmap(paramAnonymous2Bitmap, 0, 0, paramAnonymous2Bitmap.getWidth(), paramAnonymous2Bitmap.getHeight(), (Matrix)localObject, true);
                  }
                }
                label108:
                PrintHelperKitkat.3.this.mBitmap = ((Bitmap)localObject);
                if (localObject == null) {
                  break label190;
                }
                paramAnonymous2Bitmap = new PrintDocumentInfo.Builder(PrintHelperKitkat.3.this.val$jobName).setContentType(1).setPageCount(1).build();
                if (!paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1))
                {
                  bool = true;
                  paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymous2Bitmap, bool);
                  PrintHelperKitkat.3.this.mLoadBitmap = null;
                  return;
                }
              }
              finally {}
              boolean bool = false;
              continue;
              label190:
              paramAnonymousLayoutResultCallback.onLayoutFailed(null);
            }
          }
          
          protected void onPreExecute()
          {
            paramAnonymousCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
            {
              public void onCancel()
              {
                PrintHelperKitkat.3.this.cancelLoad();
                PrintHelperKitkat.3.1.this.cancel(false);
              }
            });
          }
        }.execute(new Uri[0]);
      }
      
      public void onWrite(PageRange[] paramAnonymousArrayOfPageRange, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
      {
        PrintHelperKitkat.this.writeBitmap(this.mAttributes, this.val$fittingMode, this.mBitmap, paramAnonymousParcelFileDescriptor, paramAnonymousCancellationSignal, paramAnonymousWriteResultCallback);
      }
    };
    paramOnPrintFinishCallback = (PrintManager)this.mContext.getSystemService("print");
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder();
    localBuilder.setColorMode(this.mColorMode);
    if ((this.mOrientation == 1) || (this.mOrientation == 0)) {
      localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
    }
    for (;;)
    {
      paramOnPrintFinishCallback.print(paramString, paramUri, localBuilder.build());
      return;
      if (this.mOrientation == 2) {
        localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
      }
    }
  }
  
  public void setColorMode(int paramInt)
  {
    this.mColorMode = paramInt;
  }
  
  public void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
  }
  
  public void setScaleMode(int paramInt)
  {
    this.mScaleMode = paramInt;
  }
  
  public static abstract interface OnPrintFinishCallback
  {
    public abstract void onFinish();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/print/PrintHelperKitkat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */