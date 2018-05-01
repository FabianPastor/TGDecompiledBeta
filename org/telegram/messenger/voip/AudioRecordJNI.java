package org.telegram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;
import java.nio.ByteBuffer;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class AudioRecordJNI
{
  private AcousticEchoCanceler aec;
  private AutomaticGainControl agc;
  private AudioRecord audioRecord;
  private ByteBuffer buffer;
  private int bufferSize;
  private long nativeInst;
  private boolean needResampling = false;
  private NoiseSuppressor ns;
  private boolean running;
  private Thread thread;
  
  public AudioRecordJNI(long paramLong)
  {
    this.nativeInst = paramLong;
  }
  
  private int getBufferSize(int paramInt1, int paramInt2)
  {
    return Math.max(AudioRecord.getMinBufferSize(paramInt2, 16, 2), paramInt1);
  }
  
  private native void nativeCallback(ByteBuffer paramByteBuffer);
  
  private void startThread()
  {
    if (this.thread != null) {
      throw new IllegalStateException("thread already started");
    }
    this.running = true;
    if (this.needResampling) {}
    for (final ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(1764);; localByteBuffer = null)
    {
      this.thread = new Thread(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            if (AudioRecordJNI.this.running) {}
            try
            {
              if (!AudioRecordJNI.this.needResampling) {
                AudioRecordJNI.this.audioRecord.read(AudioRecordJNI.this.buffer, 1920);
              }
              while (!AudioRecordJNI.this.running)
              {
                AudioRecordJNI.this.audioRecord.stop();
                Log.i("tg-voip", "audiotrack thread exits");
                return;
                AudioRecordJNI.this.audioRecord.read(localByteBuffer, 1764);
                Resampler.convert44to48(localByteBuffer, AudioRecordJNI.this.buffer);
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
            }
            AudioRecordJNI.this.nativeCallback(AudioRecordJNI.this.buffer);
          }
        }
      });
      this.thread.start();
      return;
    }
  }
  
  private boolean tryInit(int paramInt1, int paramInt2)
  {
    boolean bool1 = true;
    if (this.audioRecord != null) {}
    try
    {
      this.audioRecord.release();
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("Trying to initialize AudioRecord with source=" + paramInt1 + " and sample rate=" + paramInt2);
      }
      int i = getBufferSize(this.bufferSize, 48000);
      try
      {
        AudioRecord localAudioRecord = new android/media/AudioRecord;
        localAudioRecord.<init>(paramInt1, paramInt2, 16, 2, i);
        this.audioRecord = localAudioRecord;
        if (paramInt2 != 48000)
        {
          bool2 = true;
          this.needResampling = bool2;
          if ((this.audioRecord == null) || (this.audioRecord.getState() != 1)) {
            break label145;
          }
          bool2 = bool1;
          return bool2;
        }
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          FileLog.e("AudioRecord init failed!", localException1);
          continue;
          boolean bool2 = false;
          continue;
          label145:
          bool2 = false;
        }
      }
    }
    catch (Exception localException2)
    {
      for (;;) {}
    }
  }
  
  public void init(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.audioRecord != null) {
      throw new IllegalStateException("already inited");
    }
    this.bufferSize = paramInt4;
    boolean bool = tryInit(7, 48000);
    if (!bool) {
      tryInit(1, 48000);
    }
    if (!bool) {
      tryInit(7, 44100);
    }
    if (!bool) {
      tryInit(1, 44100);
    }
    this.buffer = ByteBuffer.allocateDirect(paramInt4);
  }
  
  public void release()
  {
    this.running = false;
    if (this.thread != null) {}
    try
    {
      this.thread.join();
      this.thread = null;
      if (this.audioRecord != null)
      {
        this.audioRecord.release();
        this.audioRecord = null;
      }
      if (this.agc != null)
      {
        this.agc.release();
        this.agc = null;
      }
      if (this.ns != null)
      {
        this.ns.release();
        this.ns = null;
      }
      if (this.aec != null)
      {
        this.aec.release();
        this.aec = null;
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
  
  /* Error */
  public boolean start()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 70	org/telegram/messenger/voip/AudioRecordJNI:thread	Ljava/lang/Thread;
    //   6: ifnonnull +276 -> 282
    //   9: aload_0
    //   10: getfield 48	org/telegram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   13: ifnonnull +7 -> 20
    //   16: iload_1
    //   17: istore_2
    //   18: iload_2
    //   19: ireturn
    //   20: aload_0
    //   21: getfield 48	org/telegram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   24: invokevirtual 182	android/media/AudioRecord:startRecording	()V
    //   27: getstatic 187	android/os/Build$VERSION:SDK_INT	I
    //   30: istore_3
    //   31: iload_3
    //   32: bipush 16
    //   34: if_icmplt +121 -> 155
    //   37: invokestatic 190	android/media/audiofx/AutomaticGainControl:isAvailable	()Z
    //   40: ifeq +124 -> 164
    //   43: aload_0
    //   44: aload_0
    //   45: getfield 48	org/telegram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   48: invokevirtual 193	android/media/AudioRecord:getAudioSessionId	()I
    //   51: invokestatic 197	android/media/audiofx/AutomaticGainControl:create	(I)Landroid/media/audiofx/AutomaticGainControl;
    //   54: putfield 160	org/telegram/messenger/voip/AudioRecordJNI:agc	Landroid/media/audiofx/AutomaticGainControl;
    //   57: aload_0
    //   58: getfield 160	org/telegram/messenger/voip/AudioRecordJNI:agc	Landroid/media/audiofx/AutomaticGainControl;
    //   61: ifnull +12 -> 73
    //   64: aload_0
    //   65: getfield 160	org/telegram/messenger/voip/AudioRecordJNI:agc	Landroid/media/audiofx/AutomaticGainControl;
    //   68: iconst_0
    //   69: invokevirtual 201	android/media/audiofx/AutomaticGainControl:setEnabled	(Z)I
    //   72: pop
    //   73: invokestatic 202	android/media/audiofx/NoiseSuppressor:isAvailable	()Z
    //   76: ifeq +142 -> 218
    //   79: aload_0
    //   80: aload_0
    //   81: getfield 48	org/telegram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   84: invokevirtual 193	android/media/AudioRecord:getAudioSessionId	()I
    //   87: invokestatic 205	android/media/audiofx/NoiseSuppressor:create	(I)Landroid/media/audiofx/NoiseSuppressor;
    //   90: putfield 165	org/telegram/messenger/voip/AudioRecordJNI:ns	Landroid/media/audiofx/NoiseSuppressor;
    //   93: aload_0
    //   94: getfield 165	org/telegram/messenger/voip/AudioRecordJNI:ns	Landroid/media/audiofx/NoiseSuppressor;
    //   97: ifnull +17 -> 114
    //   100: aload_0
    //   101: getfield 165	org/telegram/messenger/voip/AudioRecordJNI:ns	Landroid/media/audiofx/NoiseSuppressor;
    //   104: ldc -49
    //   106: iconst_1
    //   107: invokestatic 213	org/telegram/messenger/voip/VoIPServerConfig:getBoolean	(Ljava/lang/String;Z)Z
    //   110: invokevirtual 214	android/media/audiofx/NoiseSuppressor:setEnabled	(Z)I
    //   113: pop
    //   114: invokestatic 215	android/media/audiofx/AcousticEchoCanceler:isAvailable	()Z
    //   117: ifeq +133 -> 250
    //   120: aload_0
    //   121: aload_0
    //   122: getfield 48	org/telegram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   125: invokevirtual 193	android/media/AudioRecord:getAudioSessionId	()I
    //   128: invokestatic 218	android/media/audiofx/AcousticEchoCanceler:create	(I)Landroid/media/audiofx/AcousticEchoCanceler;
    //   131: putfield 170	org/telegram/messenger/voip/AudioRecordJNI:aec	Landroid/media/audiofx/AcousticEchoCanceler;
    //   134: aload_0
    //   135: getfield 170	org/telegram/messenger/voip/AudioRecordJNI:aec	Landroid/media/audiofx/AcousticEchoCanceler;
    //   138: ifnull +17 -> 155
    //   141: aload_0
    //   142: getfield 170	org/telegram/messenger/voip/AudioRecordJNI:aec	Landroid/media/audiofx/AcousticEchoCanceler;
    //   145: ldc -36
    //   147: iconst_1
    //   148: invokestatic 213	org/telegram/messenger/voip/VoIPServerConfig:getBoolean	(Ljava/lang/String;Z)Z
    //   151: invokevirtual 221	android/media/audiofx/AcousticEchoCanceler:setEnabled	(Z)I
    //   154: pop
    //   155: aload_0
    //   156: invokespecial 223	org/telegram/messenger/voip/AudioRecordJNI:startThread	()V
    //   159: iconst_1
    //   160: istore_2
    //   161: goto -143 -> 18
    //   164: getstatic 105	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   167: ifeq -94 -> 73
    //   170: ldc -31
    //   172: invokestatic 228	org/telegram/messenger/FileLog:w	(Ljava/lang/String;)V
    //   175: goto -102 -> 73
    //   178: astore 4
    //   180: getstatic 105	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   183: ifeq -110 -> 73
    //   186: ldc -26
    //   188: aload 4
    //   190: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   193: goto -120 -> 73
    //   196: astore 4
    //   198: iload_1
    //   199: istore_2
    //   200: getstatic 105	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   203: ifeq -185 -> 18
    //   206: ldc -24
    //   208: aload 4
    //   210: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   213: iload_1
    //   214: istore_2
    //   215: goto -197 -> 18
    //   218: getstatic 105	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   221: ifeq -107 -> 114
    //   224: ldc -22
    //   226: invokestatic 228	org/telegram/messenger/FileLog:w	(Ljava/lang/String;)V
    //   229: goto -115 -> 114
    //   232: astore 4
    //   234: getstatic 105	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   237: ifeq -123 -> 114
    //   240: ldc -20
    //   242: aload 4
    //   244: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   247: goto -133 -> 114
    //   250: getstatic 105	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   253: ifeq -98 -> 155
    //   256: ldc -18
    //   258: invokestatic 228	org/telegram/messenger/FileLog:w	(Ljava/lang/String;)V
    //   261: goto -106 -> 155
    //   264: astore 4
    //   266: getstatic 105	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   269: ifeq -114 -> 155
    //   272: ldc -16
    //   274: aload 4
    //   276: invokestatic 146	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   279: goto -124 -> 155
    //   282: aload_0
    //   283: getfield 48	org/telegram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   286: invokevirtual 182	android/media/AudioRecord:startRecording	()V
    //   289: goto -130 -> 159
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	292	0	this	AudioRecordJNI
    //   1	213	1	bool1	boolean
    //   17	198	2	bool2	boolean
    //   30	5	3	i	int
    //   178	11	4	localThrowable1	Throwable
    //   196	13	4	localException	Exception
    //   232	11	4	localThrowable2	Throwable
    //   264	11	4	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   37	73	178	java/lang/Throwable
    //   164	175	178	java/lang/Throwable
    //   2	16	196	java/lang/Exception
    //   20	31	196	java/lang/Exception
    //   37	73	196	java/lang/Exception
    //   73	114	196	java/lang/Exception
    //   114	155	196	java/lang/Exception
    //   155	159	196	java/lang/Exception
    //   164	175	196	java/lang/Exception
    //   180	193	196	java/lang/Exception
    //   218	229	196	java/lang/Exception
    //   234	247	196	java/lang/Exception
    //   250	261	196	java/lang/Exception
    //   266	279	196	java/lang/Exception
    //   282	289	196	java/lang/Exception
    //   73	114	232	java/lang/Throwable
    //   218	229	232	java/lang/Throwable
    //   114	155	264	java/lang/Throwable
    //   250	261	264	java/lang/Throwable
  }
  
  public void stop()
  {
    if (this.audioRecord != null) {
      this.audioRecord.stop();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/voip/AudioRecordJNI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */