package org.telegram.messenger.voip;

import android.media.AudioTrack;
import android.util.Log;
import java.nio.ByteBuffer;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class AudioTrackJNI
{
  private AudioTrack audioTrack;
  private byte[] buffer = new byte['ހ'];
  private int bufferSize;
  private long nativeInst;
  private boolean needResampling;
  private boolean running;
  private Thread thread;
  
  public AudioTrackJNI(long paramLong)
  {
    this.nativeInst = paramLong;
  }
  
  private int getBufferSize(int paramInt1, int paramInt2)
  {
    return Math.max(AudioTrack.getMinBufferSize(paramInt2, 4, 2), paramInt1);
  }
  
  private native void nativeCallback(byte[] paramArrayOfByte);
  
  private void startThread()
  {
    if (this.thread != null) {
      throw new IllegalStateException("thread already started");
    }
    this.running = true;
    this.thread = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          AudioTrackJNI.this.audioTrack.play();
          if (!AudioTrackJNI.this.needResampling) {
            break label189;
          }
          localByteBuffer1 = ByteBuffer.allocateDirect(1920);
        }
        catch (Exception localException1)
        {
          try
          {
            ByteBuffer localByteBuffer1;
            ByteBuffer localByteBuffer2;
            label44:
            if (!AudioTrackJNI.this.needResampling) {
              break label199;
            }
            AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
            localByteBuffer1.rewind();
            localByteBuffer1.put(AudioTrackJNI.this.buffer);
            Resampler.convert48to44(localByteBuffer1, localByteBuffer2);
            localByteBuffer2.rewind();
            localByteBuffer2.get(AudioTrackJNI.this.buffer, 0, 1764);
            AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1764);
            for (;;)
            {
              if (AudioTrackJNI.this.running) {
                break label44;
              }
              AudioTrackJNI.this.audioTrack.stop();
              Log.i("tg-voip", "audiotrack thread exits");
              for (;;)
              {
                return;
                localException1 = localException1;
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.e("error starting AudioTrack", localException1);
                }
              }
              label189:
              Object localObject = null;
              break;
              localByteBuffer2 = null;
              break label44;
              label199:
              AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
              AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1920);
            }
          }
          catch (Exception localException2)
          {
            FileLog.e(localException2);
          }
        }
        if (AudioTrackJNI.this.needResampling)
        {
          localByteBuffer2 = ByteBuffer.allocateDirect(1764);
          if (!AudioTrackJNI.this.running) {}
        }
      }
    });
    this.thread.start();
  }
  
  public void init(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.audioTrack != null) {
      throw new IllegalStateException("already inited");
    }
    paramInt2 = getBufferSize(paramInt4, 48000);
    this.bufferSize = paramInt4;
    if (paramInt3 == 1) {
      paramInt1 = 4;
    }
    for (;;)
    {
      this.audioTrack = new AudioTrack(0, 48000, paramInt1, 2, paramInt2, 1);
      if (this.audioTrack.getState() != 1) {}
      try
      {
        this.audioTrack.release();
        paramInt2 = getBufferSize(paramInt4 * 6, 44100);
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("buffer size: " + paramInt2);
        }
        if (paramInt3 == 1) {}
        for (paramInt1 = 4;; paramInt1 = 12)
        {
          this.audioTrack = new AudioTrack(0, 44100, paramInt1, 2, paramInt2, 1);
          this.needResampling = true;
          return;
          paramInt1 = 12;
          break;
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;) {}
      }
    }
  }
  
  public void release()
  {
    this.running = false;
    if (this.thread != null) {}
    try
    {
      this.thread.join();
      this.thread = null;
      if (this.audioTrack != null)
      {
        this.audioTrack.release();
        this.audioTrack = null;
      }
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        FileLog.e(localInterruptedException);
      }
    }
  }
  
  public void start()
  {
    if (this.thread == null) {
      startThread();
    }
    for (;;)
    {
      return;
      this.audioTrack.play();
    }
  }
  
  public void stop()
  {
    if (this.audioTrack != null) {}
    try
    {
      this.audioTrack.stop();
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/voip/AudioTrackJNI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */