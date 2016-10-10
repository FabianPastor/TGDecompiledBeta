package com.google.android.gms.vision.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Metadata;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.text.internal.client.LineBoxParcel;
import com.google.android.gms.vision.text.internal.client.RecognitionOptions;
import com.google.android.gms.vision.text.internal.client.TextRecognizerOptions;
import com.google.android.gms.vision.text.internal.client.zzg;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class TextRecognizer
  extends Detector<TextBlock>
{
  private final zzg aLC;
  
  private TextRecognizer()
  {
    throw new IllegalStateException("Default constructor called");
  }
  
  private TextRecognizer(zzg paramzzg)
  {
    this.aLC = paramzzg;
  }
  
  private Bitmap zza(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramByteBuffer.hasArray()) && (paramByteBuffer.arrayOffset() == 0)) {}
    Object localObject;
    for (paramByteBuffer = paramByteBuffer.array();; paramByteBuffer = (ByteBuffer)localObject)
    {
      localObject = new ByteArrayOutputStream();
      new YuvImage(paramByteBuffer, paramInt1, paramInt2, paramInt3, null).compressToJpeg(new Rect(0, 0, paramInt2, paramInt3), 100, (OutputStream)localObject);
      paramByteBuffer = ((ByteArrayOutputStream)localObject).toByteArray();
      return BitmapFactory.decodeByteArray(paramByteBuffer, 0, paramByteBuffer.length);
      localObject = new byte[paramByteBuffer.capacity()];
      paramByteBuffer.get((byte[])localObject);
    }
  }
  
  private Rect zza(Rect paramRect, int paramInt1, int paramInt2, FrameMetadataParcel paramFrameMetadataParcel)
  {
    switch (paramFrameMetadataParcel.rotation)
    {
    default: 
      return paramRect;
    case 1: 
      return new Rect(paramInt2 - paramRect.bottom, paramRect.left, paramInt2 - paramRect.top, paramRect.right);
    case 2: 
      return new Rect(paramInt1 - paramRect.right, paramInt2 - paramRect.bottom, paramInt1 - paramRect.left, paramInt2 - paramRect.top);
    }
    return new Rect(paramRect.top, paramInt1 - paramRect.right, paramRect.bottom, paramInt1 - paramRect.left);
  }
  
  private SparseArray<TextBlock> zza(LineBoxParcel[] paramArrayOfLineBoxParcel)
  {
    int j = 0;
    SparseArray localSparseArray3 = new SparseArray();
    int k = paramArrayOfLineBoxParcel.length;
    int i = 0;
    while (i < k)
    {
      LineBoxParcel localLineBoxParcel = paramArrayOfLineBoxParcel[i];
      SparseArray localSparseArray2 = (SparseArray)localSparseArray3.get(localLineBoxParcel.aLN);
      SparseArray localSparseArray1 = localSparseArray2;
      if (localSparseArray2 == null)
      {
        localSparseArray1 = new SparseArray();
        localSparseArray3.append(localLineBoxParcel.aLN, localSparseArray1);
      }
      localSparseArray1.append(localLineBoxParcel.aLO, localLineBoxParcel);
      i += 1;
    }
    paramArrayOfLineBoxParcel = new SparseArray(localSparseArray3.size());
    i = j;
    while (i < localSparseArray3.size())
    {
      paramArrayOfLineBoxParcel.append(localSparseArray3.keyAt(i), new TextBlock((SparseArray)localSparseArray3.valueAt(i)));
      i += 1;
    }
    return paramArrayOfLineBoxParcel;
  }
  
  private int zzabw(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unsupported rotation degree.");
    case 0: 
      return 0;
    case 1: 
      return 90;
    case 2: 
      return 180;
    }
    return 270;
  }
  
  private Bitmap zzb(Bitmap paramBitmap, FrameMetadataParcel paramFrameMetadataParcel)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    Object localObject = paramBitmap;
    if (paramFrameMetadataParcel.rotation != 0)
    {
      localObject = new Matrix();
      ((Matrix)localObject).postRotate(zzabw(paramFrameMetadataParcel.rotation));
      localObject = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, (Matrix)localObject, false);
    }
    if ((paramFrameMetadataParcel.rotation == 1) || (paramFrameMetadataParcel.rotation == 3))
    {
      paramFrameMetadataParcel.width = j;
      paramFrameMetadataParcel.height = i;
    }
    return (Bitmap)localObject;
  }
  
  public SparseArray<TextBlock> detect(Frame paramFrame)
  {
    return zza(paramFrame, new RecognitionOptions(1, new Rect()));
  }
  
  public boolean isOperational()
  {
    return this.aLC.isOperational();
  }
  
  public void release()
  {
    super.release();
    this.aLC.zzcls();
  }
  
  public SparseArray<TextBlock> zza(Frame paramFrame, RecognitionOptions paramRecognitionOptions)
  {
    if (paramFrame == null) {
      throw new IllegalArgumentException("No frame supplied.");
    }
    FrameMetadataParcel localFrameMetadataParcel = FrameMetadataParcel.zzc(paramFrame);
    if (paramFrame.getBitmap() != null) {}
    for (Object localObject = paramFrame.getBitmap();; localObject = zza(paramFrame.getGrayscaleImageData(), ((Frame.Metadata)localObject).getFormat(), localFrameMetadataParcel.width, localFrameMetadataParcel.height))
    {
      localObject = zzb((Bitmap)localObject, localFrameMetadataParcel);
      if (!paramRecognitionOptions.aLP.isEmpty())
      {
        paramFrame = zza(paramRecognitionOptions.aLP, paramFrame.getMetadata().getWidth(), paramFrame.getMetadata().getHeight(), localFrameMetadataParcel);
        paramRecognitionOptions.aLP.set(paramFrame);
      }
      localFrameMetadataParcel.rotation = 0;
      return zza(this.aLC.zza((Bitmap)localObject, localFrameMetadataParcel, paramRecognitionOptions));
      localObject = paramFrame.getMetadata();
    }
  }
  
  public static class Builder
  {
    private TextRecognizerOptions aLD;
    private Context mContext;
    
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
      this.aLD = new TextRecognizerOptions();
    }
    
    public TextRecognizer build()
    {
      return new TextRecognizer(new zzg(this.mContext, this.aLD), null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/TextRecognizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */