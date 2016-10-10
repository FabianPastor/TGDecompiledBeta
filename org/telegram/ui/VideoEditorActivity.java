package org.telegram.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.VideoSeekBarView;
import org.telegram.ui.Components.VideoSeekBarView.SeekBarDelegate;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;

@TargetApi(16)
public class VideoEditorActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private long audioFramesSize;
  private int bitrate;
  private ImageView captionItem;
  private ImageView compressItem;
  private boolean created;
  private VideoEditorActivityDelegate delegate;
  private long endTime;
  private long esimatedDuration;
  private int estimatedSize;
  private float lastProgress;
  private ImageView muteItem;
  private boolean muteVideo;
  private boolean needCompressVideo;
  private boolean needSeek;
  private int originalBitrate;
  private int originalHeight;
  private long originalSize;
  private int originalWidth;
  private ImageView playButton;
  private boolean playerPrepared;
  private Runnable progressRunnable = new Runnable()
  {
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   4: invokestatic 27	org/telegram/ui/VideoEditorActivity:access$000	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Object;
      //   7: astore_3
      //   8: aload_3
      //   9: monitorenter
      //   10: aload_0
      //   11: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   14: invokestatic 31	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
      //   17: ifnull +48 -> 65
      //   20: aload_0
      //   21: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   24: invokestatic 31	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
      //   27: invokevirtual 37	android/media/MediaPlayer:isPlaying	()Z
      //   30: istore_2
      //   31: iload_2
      //   32: ifeq +33 -> 65
      //   35: iconst_1
      //   36: istore_1
      //   37: aload_3
      //   38: monitorexit
      //   39: iload_1
      //   40: ifne +51 -> 91
      //   43: aload_0
      //   44: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   47: invokestatic 27	org/telegram/ui/VideoEditorActivity:access$000	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Object;
      //   50: astore_3
      //   51: aload_3
      //   52: monitorenter
      //   53: aload_0
      //   54: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   57: aconst_null
      //   58: invokestatic 41	org/telegram/ui/VideoEditorActivity:access$702	(Lorg/telegram/ui/VideoEditorActivity;Ljava/lang/Thread;)Ljava/lang/Thread;
      //   61: pop
      //   62: aload_3
      //   63: monitorexit
      //   64: return
      //   65: iconst_0
      //   66: istore_1
      //   67: goto -30 -> 37
      //   70: astore 4
      //   72: iconst_0
      //   73: istore_1
      //   74: ldc 43
      //   76: aload 4
      //   78: invokestatic 49	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   81: goto -44 -> 37
      //   84: astore 4
      //   86: aload_3
      //   87: monitorexit
      //   88: aload 4
      //   90: athrow
      //   91: new 10	org/telegram/ui/VideoEditorActivity$1$1
      //   94: dup
      //   95: aload_0
      //   96: invokespecial 52	org/telegram/ui/VideoEditorActivity$1$1:<init>	(Lorg/telegram/ui/VideoEditorActivity$1;)V
      //   99: invokestatic 58	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
      //   102: ldc2_w 59
      //   105: invokestatic 66	java/lang/Thread:sleep	(J)V
      //   108: goto -108 -> 0
      //   111: astore_3
      //   112: ldc 43
      //   114: aload_3
      //   115: invokestatic 49	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   118: goto -118 -> 0
      //   121: astore 4
      //   123: aload_3
      //   124: monitorexit
      //   125: aload 4
      //   127: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	128	0	this	1
      //   36	38	1	i	int
      //   30	2	2	bool	boolean
      //   111	13	3	localException1	Exception
      //   70	7	4	localException2	Exception
      //   84	5	4	localObject2	Object
      //   121	5	4	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   10	31	70	java/lang/Exception
      //   10	31	84	finally
      //   37	39	84	finally
      //   74	81	84	finally
      //   86	88	84	finally
      //   102	108	111	java/lang/Exception
      //   53	64	121	finally
      //   123	125	121	finally
    }
  };
  private int resultHeight;
  private int resultWidth;
  private int rotationValue;
  private long startTime;
  private final Object sync = new Object();
  private TextureView textureView;
  private Thread thread;
  private float videoDuration;
  private long videoFramesSize;
  private String videoPath;
  private MediaPlayer videoPlayer;
  private VideoSeekBarView videoSeekBarView;
  private VideoTimelineView videoTimelineView;
  
  public VideoEditorActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.videoPath = paramBundle.getString("videoPath");
  }
  
  private int calculateEstimatedSize(float paramFloat)
  {
    int i = (int)((float)(this.audioFramesSize + this.videoFramesSize) * paramFloat);
    return i + i / 32768 * 16;
  }
  
  private void onPlayComplete()
  {
    if (this.playButton != null) {
      this.playButton.setImageResource(2130838025);
    }
    if ((this.videoSeekBarView != null) && (this.videoTimelineView != null)) {
      this.videoSeekBarView.setProgress(this.videoTimelineView.getLeftProgress());
    }
    try
    {
      if ((this.videoPlayer != null) && (this.videoTimelineView != null)) {
        this.videoPlayer.seekTo((int)(this.videoTimelineView.getLeftProgress() * this.videoDuration));
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private boolean processOpenVideo()
  {
    Object localObject2;
    int j;
    try
    {
      this.originalSize = new File(this.videoPath).length();
      localObject2 = new IsoFile(this.videoPath);
      localObject3 = Path.getPaths((Container)localObject2, "/moov/trak/");
      localObject1 = null;
      j = 1;
      i = 1;
      if (Path.getPath((Container)localObject2, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") != null) {
        break label635;
      }
      i = 0;
    }
    catch (Exception localException1)
    {
      Object localObject3;
      Object localObject1;
      FileLog.e("tmessages", localException1);
      return false;
    }
    int i = j;
    if (Path.getPath((Container)localObject2, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
      i = 0;
    }
    localObject3 = ((List)localObject3).iterator();
    for (;;)
    {
      if (!((Iterator)localObject3).hasNext()) {
        break label667;
      }
      localObject2 = (TrackBox)((Iterator)localObject3).next();
      l1 = 0L;
      l3 = 0L;
      l2 = l1;
      try
      {
        localObject4 = ((TrackBox)localObject2).getMediaBox();
        l2 = l1;
        localMediaHeaderBox = ((MediaBox)localObject4).getMediaHeaderBox();
        l2 = l1;
        localObject4 = ((MediaBox)localObject4).getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
        l2 = l1;
        k = localObject4.length;
        j = 0;
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException2);
          l1 = l2;
          l2 = l3;
        }
        this.audioFramesSize += l1;
      }
      l2 = l1;
      this.videoDuration = ((float)localMediaHeaderBox.getDuration() / (float)localMediaHeaderBox.getTimescale());
      f1 = (float)(8L * l1);
      l2 = l1;
      f2 = this.videoDuration;
      l2 = (int)(f1 / f2);
      localObject2 = ((TrackBox)localObject2).getTrackHeaderBox();
      if ((((TrackHeaderBox)localObject2).getWidth() != 0.0D) && (((TrackHeaderBox)localObject2).getHeight() != 0.0D))
      {
        localObject1 = localObject2;
        j = (int)(l2 / 100000L * 100000L);
        this.bitrate = j;
        this.originalBitrate = j;
        if (this.bitrate > 900000) {
          this.bitrate = 900000;
        }
        this.videoFramesSize += l1;
      }
    }
    label600:
    label617:
    label635:
    label667:
    while (localException1 != null) {
      for (;;)
      {
        long l1;
        long l3;
        long l2;
        Object localObject4;
        MediaHeaderBox localMediaHeaderBox;
        int k;
        float f2;
        localObject2 = localException1.getMatrix();
        if (((Matrix)localObject2).equals(Matrix.ROTATE_90))
        {
          this.rotationValue = 90;
          j = (int)localException1.getWidth();
          this.originalWidth = j;
          this.resultWidth = j;
          j = (int)localException1.getHeight();
          this.originalHeight = j;
          this.resultHeight = j;
          if ((this.resultWidth > 640) || (this.resultHeight > 640)) {
            if (this.resultWidth <= this.resultHeight) {
              break label600;
            }
          }
        }
        for (float f1 = 640.0F / this.resultWidth;; f1 = 640.0F / j)
        {
          this.resultWidth = ((int)(this.resultWidth * f1));
          this.resultHeight = ((int)(this.resultHeight * f1));
          if (this.bitrate != 0)
          {
            this.bitrate = ((int)(this.bitrate * Math.max(0.5F, f1)));
            this.videoFramesSize = ((this.bitrate / 8 * this.videoDuration));
          }
          if (i != 0) {
            break label617;
          }
          if (this.resultWidth == this.originalWidth) {
            break label674;
          }
          if (this.resultHeight != this.originalHeight) {
            break label617;
          }
          break label674;
          if (((Matrix)localObject2).equals(Matrix.ROTATE_180))
          {
            this.rotationValue = 180;
            break;
          }
          if (!((Matrix)localObject2).equals(Matrix.ROTATE_270)) {
            break;
          }
          this.rotationValue = 270;
          break;
          j = this.resultHeight;
        }
        this.videoDuration *= 1000.0F;
        updateVideoInfo();
        return true;
        if (i != 0) {
          break;
        }
        return false;
        while (j < k)
        {
          l1 += localObject4[j];
          j += 1;
        }
      }
    }
    return false;
    label674:
    return false;
  }
  
  private void updateVideoInfo()
  {
    if (this.actionBar == null) {
      return;
    }
    this.esimatedDuration = (Math.ceil((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
    int i;
    int j;
    if ((this.compressItem.getVisibility() == 8) || ((this.compressItem.getVisibility() == 0) && (!this.needCompressVideo))) {
      if ((this.rotationValue == 90) || (this.rotationValue == 270))
      {
        i = this.originalHeight;
        if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
          break label301;
        }
        j = this.originalWidth;
        label114:
        this.estimatedSize = ((int)((float)this.originalSize * ((float)this.esimatedDuration / this.videoDuration)));
        if (this.videoTimelineView.getLeftProgress() != 0.0F) {
          break label394;
        }
        this.startTime = -1L;
        label154:
        if (this.videoTimelineView.getRightProgress() != 1.0F) {
          break label418;
        }
      }
    }
    label301:
    label333:
    label386:
    label394:
    label418:
    for (this.endTime = -1L;; this.endTime = ((this.videoTimelineView.getRightProgress() * this.videoDuration) * 1000L))
    {
      String str1 = String.format("%dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
      i = (int)(this.esimatedDuration / 1000L / 60L);
      String str2 = String.format("%d:%02d, ~%s", new Object[] { Integer.valueOf(i), Integer.valueOf((int)Math.ceil(this.esimatedDuration / 1000L) - i * 60), AndroidUtilities.formatFileSize(this.estimatedSize) });
      this.actionBar.setSubtitle(String.format("%s, %s", new Object[] { str1, str2 }));
      return;
      i = this.originalWidth;
      break;
      j = this.originalHeight;
      break label114;
      if ((this.rotationValue == 90) || (this.rotationValue == 270))
      {
        i = this.resultHeight;
        if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
          break label386;
        }
      }
      for (j = this.resultWidth;; j = this.resultHeight)
      {
        this.estimatedSize = calculateEstimatedSize((float)this.esimatedDuration / this.videoDuration);
        break;
        i = this.resultWidth;
        break label333;
      }
      this.startTime = ((this.videoTimelineView.getLeftProgress() * this.videoDuration) * 1000L);
      break label154;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.needCompressVideo = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("compress_video", true);
    this.actionBar.setBackgroundColor(-16777216);
    this.actionBar.setItemsBackgroundColor(-12763843);
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165345));
    this.actionBar.setSubtitleColor(-1);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          VideoEditorActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext)
    {
      int lastWidth;
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        paramAnonymousInt1 = (paramAnonymousInt3 - paramAnonymousInt1 - VideoEditorActivity.this.textureView.getWidth()) / 2;
        VideoEditorActivity.this.textureView.layout(paramAnonymousInt1, AndroidUtilities.dp(14.0F), VideoEditorActivity.this.textureView.getMeasuredWidth() + paramAnonymousInt1, AndroidUtilities.dp(14.0F) + VideoEditorActivity.this.textureView.getMeasuredHeight());
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        int k = View.MeasureSpec.getSize(paramAnonymousInt1);
        int m = View.MeasureSpec.getSize(paramAnonymousInt2) - AndroidUtilities.dp(166.0F);
        int i;
        int j;
        label86:
        float f3;
        if ((VideoEditorActivity.this.rotationValue == 90) || (VideoEditorActivity.this.rotationValue == 270))
        {
          i = VideoEditorActivity.this.originalHeight;
          if ((VideoEditorActivity.this.rotationValue != 90) && (VideoEditorActivity.this.rotationValue != 270)) {
            break label215;
          }
          j = VideoEditorActivity.this.originalWidth;
          float f1 = k / i;
          float f2 = m / j;
          f3 = i / j;
          if (f1 <= f2) {
            break label227;
          }
          j = (int)(m * f3);
          i = m;
        }
        for (;;)
        {
          if (VideoEditorActivity.this.textureView != null)
          {
            FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)VideoEditorActivity.this.textureView.getLayoutParams();
            localLayoutParams.width = j;
            localLayoutParams.height = i;
          }
          if (this.lastWidth != j) {
            VideoEditorActivity.this.videoTimelineView.clearFrames();
          }
          this.lastWidth = j;
          super.onMeasure(paramAnonymousInt1, paramAnonymousInt2);
          return;
          i = VideoEditorActivity.this.originalWidth;
          break;
          label215:
          j = VideoEditorActivity.this.originalHeight;
          break label86;
          label227:
          i = (int)(k / f3);
          j = k;
        }
      }
    };
    this.fragmentView.setBackgroundColor(-16777216);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    Object localObject = new PickerBottomLayoutViewer(paramContext);
    ((PickerBottomLayoutViewer)localObject).setBackgroundColor(0);
    ((PickerBottomLayoutViewer)localObject).updateSelectedCount(0, false);
    localFrameLayout.addView((View)localObject, LayoutHelper.createFrame(-1, 48, 83));
    ((PickerBottomLayoutViewer)localObject).cancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        VideoEditorActivity.this.finishFragment();
      }
    });
    ((PickerBottomLayoutViewer)localObject).doneButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View arg1)
      {
        for (;;)
        {
          synchronized (VideoEditorActivity.this.sync)
          {
            Object localObject1 = VideoEditorActivity.this.videoPlayer;
            if (localObject1 != null) {}
            try
            {
              VideoEditorActivity.this.videoPlayer.stop();
              VideoEditorActivity.this.videoPlayer.release();
              VideoEditorActivity.access$102(VideoEditorActivity.this, null);
              if (VideoEditorActivity.this.delegate != null)
              {
                if ((VideoEditorActivity.this.compressItem.getVisibility() != 8) && ((VideoEditorActivity.this.compressItem.getVisibility() != 0) || (VideoEditorActivity.this.needCompressVideo))) {
                  break;
                }
                ??? = VideoEditorActivity.this.delegate;
                localObject1 = VideoEditorActivity.this.videoPath;
                l1 = VideoEditorActivity.this.startTime;
                l2 = VideoEditorActivity.this.endTime;
                j = VideoEditorActivity.this.originalWidth;
                k = VideoEditorActivity.this.originalHeight;
                m = VideoEditorActivity.this.rotationValue;
                n = VideoEditorActivity.this.originalWidth;
                i1 = VideoEditorActivity.this.originalHeight;
                if (VideoEditorActivity.this.muteVideo)
                {
                  i = -1;
                  ???.didFinishEditVideo((String)localObject1, l1, l2, j, k, m, n, i1, i, VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration);
                }
              }
              else
              {
                VideoEditorActivity.this.finishFragment();
                return;
              }
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
              continue;
            }
          }
          i = VideoEditorActivity.this.originalBitrate;
        }
        ??? = VideoEditorActivity.this.delegate;
        String str = VideoEditorActivity.this.videoPath;
        long l1 = VideoEditorActivity.this.startTime;
        long l2 = VideoEditorActivity.this.endTime;
        int j = VideoEditorActivity.this.resultWidth;
        int k = VideoEditorActivity.this.resultHeight;
        int m = VideoEditorActivity.this.rotationValue;
        int n = VideoEditorActivity.this.originalWidth;
        int i1 = VideoEditorActivity.this.originalHeight;
        if (VideoEditorActivity.this.muteVideo) {}
        for (int i = -1;; i = VideoEditorActivity.this.bitrate)
        {
          ???.didFinishEditVideo(str, l1, l2, j, k, m, n, i1, i, VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration);
          break;
        }
      }
    });
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setOrientation(0);
    ((PickerBottomLayoutViewer)localObject).addView(localLinearLayout, LayoutHelper.createFrame(-2, 48, 49));
    this.captionItem = new ImageView(paramContext);
    this.captionItem.setScaleType(ImageView.ScaleType.CENTER);
    this.captionItem.setImageResource(2130837889);
    this.captionItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(1090519039));
    localLinearLayout.addView(this.captionItem, LayoutHelper.createLinear(56, 48));
    this.captionItem.setVisibility(8);
    this.captionItem.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView) {}
    });
    this.compressItem = new ImageView(paramContext);
    this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
    localObject = this.compressItem;
    int i;
    if (this.needCompressVideo)
    {
      i = 2130837691;
      ((ImageView)localObject).setImageResource(i);
      this.compressItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(1090519039));
      localObject = this.compressItem;
      if ((this.originalHeight == this.resultHeight) && (this.originalWidth == this.resultWidth)) {
        break label787;
      }
      i = 0;
      label397:
      ((ImageView)localObject).setVisibility(i);
      localLinearLayout.addView(this.compressItem, LayoutHelper.createLinear(56, 48));
      this.compressItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = VideoEditorActivity.this;
          boolean bool;
          if (!VideoEditorActivity.this.needCompressVideo)
          {
            bool = true;
            VideoEditorActivity.access$1502(paramAnonymousView, bool);
            paramAnonymousView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            paramAnonymousView.putBoolean("compress_video", VideoEditorActivity.this.needCompressVideo);
            paramAnonymousView.commit();
            paramAnonymousView = VideoEditorActivity.this.compressItem;
            if (!VideoEditorActivity.this.needCompressVideo) {
              break label100;
            }
          }
          label100:
          for (int i = 2130837691;; i = 2130837692)
          {
            paramAnonymousView.setImageResource(i);
            VideoEditorActivity.this.updateVideoInfo();
            return;
            bool = false;
            break;
          }
        }
      });
      if (Build.VERSION.SDK_INT >= 18) {}
    }
    for (;;)
    {
      try
      {
        localObject = MediaController.selectCodec("video/avc");
        if (localObject != null) {
          continue;
        }
        this.compressItem.setVisibility(8);
      }
      catch (Exception localException)
      {
        label787:
        String str;
        this.compressItem.setVisibility(8);
        FileLog.e("tmessages", localException);
        continue;
        if (MediaController.selectColorFormat(localException, "video/avc") != 0) {
          continue;
        }
        this.compressItem.setVisibility(8);
        continue;
      }
      this.muteItem = new ImageView(paramContext);
      this.muteItem.setScaleType(ImageView.ScaleType.CENTER);
      this.muteItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(1090519039));
      localLinearLayout.addView(this.muteItem, LayoutHelper.createLinear(56, 48));
      this.muteItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = VideoEditorActivity.this;
          if (!VideoEditorActivity.this.muteVideo) {}
          for (boolean bool = true;; bool = false)
          {
            VideoEditorActivity.access$1902(paramAnonymousView, bool);
            VideoEditorActivity.this.updateMuteButton();
            return;
          }
        }
      });
      this.videoTimelineView = new VideoTimelineView(paramContext);
      this.videoTimelineView.setVideoPath(this.videoPath);
      this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate()
      {
        public void onLeftProgressChanged(float paramAnonymousFloat)
        {
          if ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared)) {
            return;
          }
          try
          {
            if (VideoEditorActivity.this.videoPlayer.isPlaying())
            {
              VideoEditorActivity.this.videoPlayer.pause();
              VideoEditorActivity.this.playButton.setImageResource(2130838025);
            }
            VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
            VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * paramAnonymousFloat));
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException);
            }
          }
          VideoEditorActivity.access$2802(VideoEditorActivity.this, true);
          VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
          VideoEditorActivity.this.updateVideoInfo();
        }
        
        public void onRifhtProgressChanged(float paramAnonymousFloat)
        {
          if ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared)) {
            return;
          }
          try
          {
            if (VideoEditorActivity.this.videoPlayer.isPlaying())
            {
              VideoEditorActivity.this.videoPlayer.pause();
              VideoEditorActivity.this.playButton.setImageResource(2130838025);
            }
            VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
            VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * paramAnonymousFloat));
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException);
            }
          }
          VideoEditorActivity.access$2802(VideoEditorActivity.this, true);
          VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
          VideoEditorActivity.this.updateVideoInfo();
        }
      });
      localFrameLayout.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 44.0F, 83, 0.0F, 0.0F, 0.0F, 67.0F));
      this.videoSeekBarView = new VideoSeekBarView(paramContext);
      this.videoSeekBarView.setDelegate(new VideoSeekBarView.SeekBarDelegate()
      {
        public void onSeekBarDrag(float paramAnonymousFloat)
        {
          float f;
          if (paramAnonymousFloat < VideoEditorActivity.this.videoTimelineView.getLeftProgress())
          {
            f = VideoEditorActivity.this.videoTimelineView.getLeftProgress();
            VideoEditorActivity.this.videoSeekBarView.setProgress(f);
          }
          while ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared))
          {
            return;
            f = paramAnonymousFloat;
            if (paramAnonymousFloat > VideoEditorActivity.this.videoTimelineView.getRightProgress())
            {
              f = VideoEditorActivity.this.videoTimelineView.getRightProgress();
              VideoEditorActivity.this.videoSeekBarView.setProgress(f);
            }
          }
          if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
            try
            {
              VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * f));
              VideoEditorActivity.access$402(VideoEditorActivity.this, f);
              return;
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
              return;
            }
          }
          VideoEditorActivity.access$402(VideoEditorActivity.this, f);
          VideoEditorActivity.access$2802(VideoEditorActivity.this, true);
        }
      });
      localFrameLayout.addView(this.videoSeekBarView, LayoutHelper.createFrame(-1, 40.0F, 83, 11.0F, 0.0F, 11.0F, 112.0F));
      this.textureView = new TextureView(paramContext);
      this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
      {
        public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if ((VideoEditorActivity.this.textureView == null) || (!VideoEditorActivity.this.textureView.isAvailable()) || (VideoEditorActivity.this.videoPlayer == null)) {}
          for (;;)
          {
            return;
            try
            {
              paramAnonymousSurfaceTexture = new Surface(VideoEditorActivity.this.textureView.getSurfaceTexture());
              VideoEditorActivity.this.videoPlayer.setSurface(paramAnonymousSurfaceTexture);
              if (VideoEditorActivity.this.playerPrepared)
              {
                VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
                return;
              }
            }
            catch (Exception paramAnonymousSurfaceTexture)
            {
              FileLog.e("tmessages", paramAnonymousSurfaceTexture);
            }
          }
        }
        
        public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
        {
          if (VideoEditorActivity.this.videoPlayer == null) {
            return true;
          }
          VideoEditorActivity.this.videoPlayer.setDisplay(null);
          return true;
        }
        
        public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
        
        public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture) {}
      });
      localFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 14.0F, 0.0F, 140.0F));
      this.playButton = new ImageView(paramContext);
      this.playButton.setScaleType(ImageView.ScaleType.CENTER);
      this.playButton.setImageResource(2130838025);
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View arg1)
        {
          if ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared)) {
            return;
          }
          if (VideoEditorActivity.this.videoPlayer.isPlaying())
          {
            VideoEditorActivity.this.videoPlayer.pause();
            VideoEditorActivity.this.playButton.setImageResource(2130838025);
            return;
          }
          try
          {
            VideoEditorActivity.this.playButton.setImageDrawable(null);
            VideoEditorActivity.access$402(VideoEditorActivity.this, 0.0F);
            if (VideoEditorActivity.this.needSeek)
            {
              VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * VideoEditorActivity.this.videoSeekBarView.getProgress()));
              VideoEditorActivity.access$2802(VideoEditorActivity.this, false);
            }
            VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener()
            {
              public void onSeekComplete(MediaPlayer paramAnonymous2MediaPlayer)
              {
                float f2 = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
                float f3 = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
                float f1 = f2;
                if (f2 == f3) {
                  f1 = f3 - 0.01F;
                }
                VideoEditorActivity.access$402(VideoEditorActivity.this, (VideoEditorActivity.this.videoPlayer.getCurrentPosition() - f1) / (f3 - f1));
                f1 = VideoEditorActivity.this.videoTimelineView.getRightProgress();
                f2 = VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                VideoEditorActivity.access$402(VideoEditorActivity.this, VideoEditorActivity.this.videoTimelineView.getLeftProgress() + VideoEditorActivity.this.lastProgress * (f1 - f2));
                VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.lastProgress);
              }
            });
            VideoEditorActivity.this.videoPlayer.start();
            synchronized (VideoEditorActivity.this.sync)
            {
              if (VideoEditorActivity.this.thread == null)
              {
                VideoEditorActivity.access$702(VideoEditorActivity.this, new Thread(VideoEditorActivity.this.progressRunnable));
                VideoEditorActivity.this.thread.start();
              }
              return;
            }
            return;
          }
          catch (Exception ???)
          {
            FileLog.e("tmessages", ???);
          }
        }
      });
      localFrameLayout.addView(this.playButton, LayoutHelper.createFrame(100, 100.0F, 17, 0.0F, 0.0F, 0.0F, 70.0F));
      updateVideoInfo();
      updateMuteButton();
      return this.fragmentView;
      i = 2130837692;
      break;
      i = 8;
      break label397;
      str = ((MediaCodecInfo)localObject).getName();
      if ((!str.equals("OMX.google.h264.encoder")) && (!str.equals("OMX.ST.VFM.H264Enc")) && (!str.equals("OMX.Exynos.avc.enc")) && (!str.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER")) && (!str.equals("OMX.MARVELL.VIDEO.H264ENCODER")) && (!str.equals("OMX.k3.video.encoder.avc")) && (!str.equals("OMX.TI.DUCATI1.VIDEO.H264E"))) {
        continue;
      }
      this.compressItem.setVisibility(8);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.closeChats) {
      removeSelfFromStack();
    }
  }
  
  public boolean onFragmentCreate()
  {
    if (this.created) {
      return true;
    }
    if ((this.videoPath == null) || (!processOpenVideo())) {
      return false;
    }
    this.videoPlayer = new MediaPlayer();
    this.videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
    {
      public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            VideoEditorActivity.this.onPlayComplete();
          }
        });
      }
    });
    this.videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
    {
      public void onPrepared(MediaPlayer paramAnonymousMediaPlayer)
      {
        VideoEditorActivity.access$802(VideoEditorActivity.this, true);
        if ((VideoEditorActivity.this.videoTimelineView != null) && (VideoEditorActivity.this.videoPlayer != null)) {
          VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
        }
      }
    });
    try
    {
      this.videoPlayer.setDataSource(this.videoPath);
      this.videoPlayer.prepareAsync();
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
      this.created = true;
      return super.onFragmentCreate();
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return false;
  }
  
  public void onFragmentDestroy()
  {
    if (this.videoTimelineView != null) {
      this.videoTimelineView.destroy();
    }
    if (this.videoPlayer != null) {}
    try
    {
      this.videoPlayer.stop();
      this.videoPlayer.release();
      this.videoPlayer = null;
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
      super.onFragmentDestroy();
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void setDelegate(VideoEditorActivityDelegate paramVideoEditorActivityDelegate)
  {
    this.delegate = paramVideoEditorActivityDelegate;
  }
  
  public void updateMuteButton()
  {
    float f;
    if (this.videoPlayer != null)
    {
      if (!this.muteVideo) {
        break label117;
      }
      f = 0.0F;
      if (this.videoPlayer != null) {
        this.videoPlayer.setVolume(f, f);
      }
    }
    if (this.muteVideo)
    {
      this.actionBar.setTitle(LocaleController.getString("AttachGif", 2131165340));
      this.muteItem.setImageResource(2130838029);
      if (this.captionItem.getVisibility() == 0)
      {
        this.needCompressVideo = true;
        this.compressItem.setImageResource(2130837691);
        this.compressItem.setClickable(false);
        this.compressItem.setAlpha(0.8F);
        this.captionItem.setEnabled(false);
      }
    }
    label117:
    do
    {
      return;
      f = 1.0F;
      break;
      this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165345));
      this.muteItem.setImageResource(2130838030);
    } while (this.captionItem.getVisibility() != 0);
    this.compressItem.setClickable(true);
    this.compressItem.setAlpha(1.0F);
    this.captionItem.setEnabled(true);
  }
  
  public static abstract interface VideoEditorActivityDelegate
  {
    public abstract void didFinishEditVideo(String paramString, long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, long paramLong3, long paramLong4);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/VideoEditorActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */