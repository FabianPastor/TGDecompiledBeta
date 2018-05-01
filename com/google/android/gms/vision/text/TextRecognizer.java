package com.google.android.gms.vision.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;
import com.google.android.gms.internal.fc;
import com.google.android.gms.internal.fk;
import com.google.android.gms.internal.fm;
import com.google.android.gms.internal.fq;
import com.google.android.gms.internal.fr;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Metadata;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class TextRecognizer
  extends Detector<TextBlock>
{
  private final fq zzbNU;
  
  private TextRecognizer()
  {
    throw new IllegalStateException("Default constructor called");
  }
  
  private TextRecognizer(fq paramfq)
  {
    this.zzbNU = paramfq;
  }
  
  private static SparseArray<TextBlock> zza(fk[] paramArrayOffk)
  {
    int j = 0;
    SparseArray localSparseArray3 = new SparseArray();
    int k = paramArrayOffk.length;
    int i = 0;
    while (i < k)
    {
      fk localfk = paramArrayOffk[i];
      SparseArray localSparseArray2 = (SparseArray)localSparseArray3.get(localfk.zzbOf);
      SparseArray localSparseArray1 = localSparseArray2;
      if (localSparseArray2 == null)
      {
        localSparseArray1 = new SparseArray();
        localSparseArray3.append(localfk.zzbOf, localSparseArray1);
      }
      localSparseArray1.append(localfk.zzbOg, localfk);
      i += 1;
    }
    paramArrayOffk = new SparseArray(localSparseArray3.size());
    i = j;
    while (i < localSparseArray3.size())
    {
      paramArrayOffk.append(localSparseArray3.keyAt(i), new TextBlock((SparseArray)localSparseArray3.valueAt(i)));
      i += 1;
    }
    return paramArrayOffk;
  }
  
  public final SparseArray<TextBlock> detect(Frame paramFrame)
  {
    fm localfm = new fm(new Rect());
    if (paramFrame == null) {
      throw new IllegalArgumentException("No frame supplied.");
    }
    fc localfc = fc.zzc(paramFrame);
    Object localObject1;
    int j;
    int k;
    Object localObject2;
    if (paramFrame.getBitmap() != null)
    {
      localObject1 = paramFrame.getBitmap();
      j = ((Bitmap)localObject1).getWidth();
      k = ((Bitmap)localObject1).getHeight();
      localObject2 = localObject1;
      if (localfc.rotation == 0) {
        break label293;
      }
      localObject2 = new Matrix();
    }
    int i;
    switch (localfc.rotation)
    {
    default: 
      throw new IllegalArgumentException("Unsupported rotation degree.");
      localObject1 = paramFrame.getMetadata();
      localObject2 = paramFrame.getGrayscaleImageData();
      i = ((Frame.Metadata)localObject1).getFormat();
      j = localfc.width;
      k = localfc.height;
      if ((((ByteBuffer)localObject2).hasArray()) && (((ByteBuffer)localObject2).arrayOffset() == 0)) {
        localObject1 = ((ByteBuffer)localObject2).array();
      }
      for (;;)
      {
        localObject2 = new ByteArrayOutputStream();
        new YuvImage((byte[])localObject1, i, j, k, null).compressToJpeg(new Rect(0, 0, j, k), 100, (OutputStream)localObject2);
        localObject1 = ((ByteArrayOutputStream)localObject2).toByteArray();
        localObject1 = BitmapFactory.decodeByteArray((byte[])localObject1, 0, localObject1.length);
        break;
        localObject1 = new byte[((ByteBuffer)localObject2).capacity()];
        ((ByteBuffer)localObject2).get((byte[])localObject1);
      }
    case 0: 
      i = 0;
      ((Matrix)localObject2).postRotate(i);
      localObject2 = Bitmap.createBitmap((Bitmap)localObject1, 0, 0, j, k, (Matrix)localObject2, false);
      label293:
      if ((localfc.rotation == 1) || (localfc.rotation == 3))
      {
        localfc.width = k;
        localfc.height = j;
      }
      if (!localfm.zzbOh.isEmpty())
      {
        localObject1 = localfm.zzbOh;
        i = paramFrame.getMetadata().getWidth();
        j = paramFrame.getMetadata().getHeight();
        switch (localfc.rotation)
        {
        default: 
          paramFrame = (Frame)localObject1;
        }
      }
      break;
    }
    for (;;)
    {
      localfm.zzbOh.set(paramFrame);
      localfc.rotation = 0;
      return zza(this.zzbNU.zza((Bitmap)localObject2, localfc, localfm));
      i = 90;
      break;
      i = 180;
      break;
      i = 270;
      break;
      paramFrame = new Rect(j - ((Rect)localObject1).bottom, ((Rect)localObject1).left, j - ((Rect)localObject1).top, ((Rect)localObject1).right);
      continue;
      paramFrame = new Rect(i - ((Rect)localObject1).right, j - ((Rect)localObject1).bottom, i - ((Rect)localObject1).left, j - ((Rect)localObject1).top);
      continue;
      paramFrame = new Rect(((Rect)localObject1).top, i - ((Rect)localObject1).right, ((Rect)localObject1).bottom, i - ((Rect)localObject1).left);
    }
  }
  
  public final boolean isOperational()
  {
    return this.zzbNU.isOperational();
  }
  
  public final void release()
  {
    super.release();
    this.zzbNU.zzDQ();
  }
  
  public static class Builder
  {
    private Context mContext;
    private fr zzbNV;
    
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
      this.zzbNV = new fr();
    }
    
    public TextRecognizer build()
    {
      return new TextRecognizer(new fq(this.mContext, this.zzbNV), null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/TextRecognizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */