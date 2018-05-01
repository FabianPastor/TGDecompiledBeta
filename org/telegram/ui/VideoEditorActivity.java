package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.VideoSeekBarView;
import org.telegram.ui.Components.VideoSeekBarView.SeekBarDelegate;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;

@TargetApi(16)
public class VideoEditorActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private boolean allowMentions;
  private long audioFramesSize;
  private int bitrate;
  private PhotoViewerCaptionEnterView captionEditText;
  private ImageView captionItem;
  private ImageView compressItem;
  private int compressionsCount = -1;
  private boolean created;
  private CharSequence currentCaption;
  private String currentSubtitle;
  private VideoEditorActivityDelegate delegate;
  private long endTime;
  private long esimatedDuration;
  private int estimatedSize;
  private boolean firstCaptionLayout;
  private boolean inPreview;
  private float lastProgress;
  private boolean loadInitialVideo;
  private LinearLayoutManager mentionLayoutManager;
  private AnimatorSet mentionListAnimation;
  private RecyclerListView mentionListView;
  private MentionsAdapter mentionsAdapter;
  private ImageView muteItem;
  private boolean muteVideo;
  private boolean needSeek;
  private int originalBitrate;
  private int originalHeight;
  private long originalSize;
  private int originalWidth;
  private ChatActivity parentChatActivity;
  private PickerBottomLayoutViewer pickerView;
  private ImageView playButton;
  private boolean playerPrepared;
  private int previewViewEnd;
  private int previousCompression;
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
      //   40: ifne +49 -> 89
      //   43: aload_0
      //   44: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   47: invokestatic 27	org/telegram/ui/VideoEditorActivity:access$000	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Object;
      //   50: astore_3
      //   51: aload_3
      //   52: monitorenter
      //   53: aload_0
      //   54: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   57: aconst_null
      //   58: invokestatic 41	org/telegram/ui/VideoEditorActivity:access$902	(Lorg/telegram/ui/VideoEditorActivity;Ljava/lang/Thread;)Ljava/lang/Thread;
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
      //   74: aload 4
      //   76: invokestatic 47	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   79: goto -42 -> 37
      //   82: astore 4
      //   84: aload_3
      //   85: monitorexit
      //   86: aload 4
      //   88: athrow
      //   89: new 10	org/telegram/ui/VideoEditorActivity$1$1
      //   92: dup
      //   93: aload_0
      //   94: invokespecial 50	org/telegram/ui/VideoEditorActivity$1$1:<init>	(Lorg/telegram/ui/VideoEditorActivity$1;)V
      //   97: invokestatic 56	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
      //   100: ldc2_w 57
      //   103: invokestatic 64	java/lang/Thread:sleep	(J)V
      //   106: goto -106 -> 0
      //   109: astore_3
      //   110: aload_3
      //   111: invokestatic 47	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   114: goto -114 -> 0
      //   117: astore 4
      //   119: aload_3
      //   120: monitorexit
      //   121: aload 4
      //   123: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	124	0	this	1
      //   36	38	1	i	int
      //   30	2	2	bool	boolean
      //   109	11	3	localException1	Exception
      //   70	5	4	localException2	Exception
      //   82	5	4	localObject2	Object
      //   117	5	4	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   10	31	70	java/lang/Exception
      //   10	31	82	finally
      //   37	39	82	finally
      //   74	79	82	finally
      //   84	86	82	finally
      //   100	106	109	java/lang/Exception
      //   53	64	117	finally
      //   119	121	117	finally
    }
  };
  private RadialProgressView progressView;
  private QualityChooseView qualityChooseView;
  private PickerBottomLayoutViewer qualityPicker;
  private boolean requestingPreview;
  private int resultHeight;
  private int resultWidth;
  private int rotationValue;
  private int selectedCompression;
  private long startTime;
  private final Object sync = new Object();
  private TextureView textureView;
  private Thread thread;
  private boolean tryStartRequestPreviewOnFinish;
  private float videoDuration;
  private long videoFramesSize;
  private String videoPath;
  private MediaPlayer videoPlayer;
  private MessageObject videoPreviewMessageObject;
  private VideoSeekBarView videoSeekBarView;
  private VideoTimelineView videoTimelineView;
  
  public VideoEditorActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.videoPath = paramBundle.getString("videoPath");
  }
  
  private void closeCaptionEnter(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.currentCaption = this.captionEditText.getFieldCharSequence();
    }
    this.pickerView.setVisibility(0);
    if (!AndroidUtilities.isTablet())
    {
      this.videoSeekBarView.setVisibility(0);
      this.videoTimelineView.setVisibility(0);
    }
    ActionBar localActionBar = this.actionBar;
    Object localObject;
    if (this.muteVideo)
    {
      localObject = LocaleController.getString("AttachGif", NUM);
      localActionBar.setTitle((CharSequence)localObject);
      localActionBar = this.actionBar;
      if (!this.muteVideo) {
        break label157;
      }
      localObject = null;
      label89:
      localActionBar.setSubtitle((CharSequence)localObject);
      localObject = this.captionItem;
      if (!TextUtils.isEmpty(this.currentCaption)) {
        break label165;
      }
    }
    label157:
    label165:
    for (int i = NUM;; i = NUM)
    {
      ((ImageView)localObject).setImageResource(i);
      if (this.captionEditText.isPopupShowing()) {
        this.captionEditText.hidePopup();
      }
      this.captionEditText.closeKeyboard();
      return;
      localObject = LocaleController.getString("AttachVideo", NUM);
      break;
      localObject = this.currentSubtitle;
      break label89;
    }
  }
  
  private void destroyPlayer()
  {
    if (this.videoPlayer != null) {}
    try
    {
      if (this.videoPlayer != null) {
        this.videoPlayer.stop();
      }
      try
      {
        if (this.videoPlayer != null) {
          this.videoPlayer.release();
        }
        this.videoPlayer = null;
        return;
      }
      catch (Exception localException1)
      {
        for (;;) {}
      }
    }
    catch (Exception localException2)
    {
      for (;;) {}
    }
  }
  
  private void didChangedCompressionLevel(boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
    localEditor.putInt("compress_video2", this.selectedCompression);
    localEditor.commit();
    updateWidthHeightBitrateForCompression();
    updateVideoInfo();
    if (paramBoolean) {
      requestVideoPreview(1);
    }
  }
  
  private void onPlayComplete()
  {
    if (this.playButton != null) {
      this.playButton.setImageResource(NUM);
    }
    if ((this.videoSeekBarView != null) && (this.videoTimelineView != null))
    {
      if (!this.inPreview) {
        break label76;
      }
      this.videoSeekBarView.setProgress(0.0F);
    }
    try
    {
      for (;;)
      {
        if ((this.videoPlayer != null) && (this.videoTimelineView != null))
        {
          if (!this.inPreview) {
            break;
          }
          this.videoPlayer.seekTo(0);
        }
        return;
        label76:
        this.videoSeekBarView.setProgress(this.videoTimelineView.getLeftProgress());
      }
      this.videoPlayer.seekTo((int)(this.videoTimelineView.getLeftProgress() * this.videoDuration));
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
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
      localList = Path.getPaths((Container)localObject2, "/moov/trak/");
      localObject1 = null;
      j = 1;
      i = 1;
      if (Path.getPath((Container)localObject2, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") != null) {
        break label578;
      }
      FileLog.d("audio track not found");
      i = 0;
    }
    catch (Exception localException1)
    {
      List localList;
      Object localObject1;
      FileLog.e(localException1);
      return false;
    }
    int i = j;
    if (Path.getPath((Container)localObject2, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null)
    {
      FileLog.d("video track not found");
      i = 0;
    }
    for (;;)
    {
      if (j < localList.size())
      {
        localObject2 = (TrackBox)localList.get(j);
        long l1 = 0L;
        long l3 = 0L;
        long l2 = l1;
        try
        {
          Object localObject3 = ((TrackBox)localObject2).getMediaBox();
          l2 = l1;
          MediaHeaderBox localMediaHeaderBox = ((MediaBox)localObject3).getMediaHeaderBox();
          l2 = l1;
          localObject3 = ((MediaBox)localObject3).getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
          k = 0;
          for (;;)
          {
            l2 = l1;
            if (k >= localObject3.length) {
              break;
            }
            l1 += localObject3[k];
            k += 1;
          }
          l2 = l1;
          this.videoDuration = ((float)localMediaHeaderBox.getDuration() / (float)localMediaHeaderBox.getTimescale());
          float f1 = (float)(8L * l1);
          l2 = l1;
          float f2 = this.videoDuration;
          l2 = (int)(f1 / f2);
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            int k;
            FileLog.e(localException2);
            l1 = l2;
            l2 = l3;
          }
        }
        localObject2 = ((TrackBox)localObject2).getTrackHeaderBox();
        if ((((TrackHeaderBox)localObject2).getWidth() != 0.0D) && (((TrackHeaderBox)localObject2).getHeight() != 0.0D))
        {
          localObject1 = localObject2;
          k = (int)(l2 / 100000L * 100000L);
          this.bitrate = k;
          this.originalBitrate = k;
          if (this.bitrate > 900000) {
            this.bitrate = 900000;
          }
          this.videoFramesSize += l1;
        }
        else
        {
          this.audioFramesSize += l1;
        }
      }
      else
      {
        if (localException1 == null)
        {
          FileLog.d("video track header box not found");
          return false;
        }
        localObject2 = localException1.getMatrix();
        if (((Matrix)localObject2).equals(Matrix.ROTATE_90)) {
          this.rotationValue = 90;
        }
        for (;;)
        {
          j = (int)localException1.getWidth();
          this.originalWidth = j;
          this.resultWidth = j;
          j = (int)localException1.getHeight();
          this.originalHeight = j;
          this.resultHeight = j;
          this.videoDuration *= 1000.0F;
          this.selectedCompression = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("compress_video2", 1);
          updateWidthHeightBitrateForCompression();
          if ((i != 0) || ((this.resultWidth != this.originalWidth) && (this.resultHeight != this.originalHeight))) {
            break;
          }
          FileLog.d("video is not mp4");
          return false;
          if (((Matrix)localObject2).equals(Matrix.ROTATE_180)) {
            this.rotationValue = 180;
          } else if (((Matrix)localObject2).equals(Matrix.ROTATE_270)) {
            this.rotationValue = 270;
          }
        }
        updateVideoInfo();
        return true;
        label578:
        if (i != 0) {
          break;
        }
        return false;
        j = 0;
        continue;
      }
      j += 1;
    }
  }
  
  /* Error */
  private boolean reinitPlayer(String paramString)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: fconst_0
    //   3: fstore_2
    //   4: aload_0
    //   5: invokespecial 672	org/telegram/ui/VideoEditorActivity:destroyPlayer	()V
    //   8: aload_0
    //   9: getfield 318	org/telegram/ui/VideoEditorActivity:playButton	Landroid/widget/ImageView;
    //   12: ifnull +13 -> 25
    //   15: aload_0
    //   16: getfield 318	org/telegram/ui/VideoEditorActivity:playButton	Landroid/widget/ImageView;
    //   19: ldc_w 508
    //   22: invokevirtual 450	android/widget/ImageView:setImageResource	(I)V
    //   25: aload_0
    //   26: fconst_0
    //   27: putfield 380	org/telegram/ui/VideoEditorActivity:lastProgress	F
    //   30: aload_0
    //   31: new 470	android/media/MediaPlayer
    //   34: dup
    //   35: invokespecial 673	android/media/MediaPlayer:<init>	()V
    //   38: putfield 186	org/telegram/ui/VideoEditorActivity:videoPlayer	Landroid/media/MediaPlayer;
    //   41: aload_0
    //   42: getfield 186	org/telegram/ui/VideoEditorActivity:videoPlayer	Landroid/media/MediaPlayer;
    //   45: new 42	org/telegram/ui/VideoEditorActivity$2
    //   48: dup
    //   49: aload_0
    //   50: invokespecial 674	org/telegram/ui/VideoEditorActivity$2:<init>	(Lorg/telegram/ui/VideoEditorActivity;)V
    //   53: invokevirtual 678	android/media/MediaPlayer:setOnPreparedListener	(Landroid/media/MediaPlayer$OnPreparedListener;)V
    //   56: aload_0
    //   57: getfield 186	org/telegram/ui/VideoEditorActivity:videoPlayer	Landroid/media/MediaPlayer;
    //   60: aload_1
    //   61: invokevirtual 681	android/media/MediaPlayer:setDataSource	(Ljava/lang/String;)V
    //   64: aload_0
    //   65: getfield 186	org/telegram/ui/VideoEditorActivity:videoPlayer	Landroid/media/MediaPlayer;
    //   68: invokevirtual 684	android/media/MediaPlayer:prepareAsync	()V
    //   71: aload_0
    //   72: getfield 256	org/telegram/ui/VideoEditorActivity:muteVideo	Z
    //   75: ifeq +76 -> 151
    //   78: aload_0
    //   79: getfield 186	org/telegram/ui/VideoEditorActivity:videoPlayer	Landroid/media/MediaPlayer;
    //   82: ifnull +12 -> 94
    //   85: aload_0
    //   86: getfield 186	org/telegram/ui/VideoEditorActivity:videoPlayer	Landroid/media/MediaPlayer;
    //   89: fload_2
    //   90: fload_2
    //   91: invokevirtual 688	android/media/MediaPlayer:setVolume	(FF)V
    //   94: aload_1
    //   95: aload_0
    //   96: getfield 179	org/telegram/ui/VideoEditorActivity:videoPath	Ljava/lang/String;
    //   99: invokevirtual 691	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   102: ifne +5 -> 107
    //   105: iconst_1
    //   106: istore_3
    //   107: aload_0
    //   108: iload_3
    //   109: putfield 235	org/telegram/ui/VideoEditorActivity:inPreview	Z
    //   112: aload_0
    //   113: getfield 232	org/telegram/ui/VideoEditorActivity:textureView	Landroid/view/TextureView;
    //   116: ifnull +26 -> 142
    //   119: new 693	android/view/Surface
    //   122: dup
    //   123: aload_0
    //   124: getfield 232	org/telegram/ui/VideoEditorActivity:textureView	Landroid/view/TextureView;
    //   127: invokevirtual 699	android/view/TextureView:getSurfaceTexture	()Landroid/graphics/SurfaceTexture;
    //   130: invokespecial 702	android/view/Surface:<init>	(Landroid/graphics/SurfaceTexture;)V
    //   133: astore_1
    //   134: aload_0
    //   135: getfield 186	org/telegram/ui/VideoEditorActivity:videoPlayer	Landroid/media/MediaPlayer;
    //   138: aload_1
    //   139: invokevirtual 706	android/media/MediaPlayer:setSurface	(Landroid/view/Surface;)V
    //   142: iconst_1
    //   143: ireturn
    //   144: astore_1
    //   145: aload_1
    //   146: invokestatic 525	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   149: iconst_0
    //   150: ireturn
    //   151: fconst_1
    //   152: fstore_2
    //   153: goto -75 -> 78
    //   156: astore_1
    //   157: aload_1
    //   158: invokestatic 525	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   161: iconst_1
    //   162: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	this	VideoEditorActivity
    //   0	163	1	paramString	String
    //   3	150	2	f	float
    //   1	108	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   56	71	144	java/lang/Exception
    //   119	142	156	java/lang/Exception
  }
  
  private void requestVideoPreview(int paramInt)
  {
    if (this.videoPreviewMessageObject != null) {
      MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
    }
    int i;
    if ((this.requestingPreview) && (!this.tryStartRequestPreviewOnFinish))
    {
      i = 1;
      this.requestingPreview = false;
      this.loadInitialVideo = false;
      this.progressView.setVisibility(4);
      if (paramInt != 1) {
        break label486;
      }
      if (this.selectedCompression != this.compressionsCount - 1) {
        break label115;
      }
      this.tryStartRequestPreviewOnFinish = false;
      if (i != 0) {
        break label93;
      }
      reinitPlayer(this.videoPath);
    }
    label93:
    label115:
    label486:
    do
    {
      do
      {
        return;
        i = 0;
        break;
        this.playButton.setImageDrawable(null);
        this.progressView.setVisibility(0);
        this.loadInitialVideo = true;
        return;
        destroyPlayer();
        if (this.videoPreviewMessageObject == null)
        {
          localObject = new TLRPC.TL_message();
          ((TLRPC.TL_message)localObject).id = 0;
          ((TLRPC.TL_message)localObject).message = "";
          ((TLRPC.TL_message)localObject).media = new TLRPC.TL_messageMediaEmpty();
          ((TLRPC.TL_message)localObject).action = new TLRPC.TL_messageActionEmpty();
          this.videoPreviewMessageObject = new MessageObject((TLRPC.Message)localObject, null, false);
          this.videoPreviewMessageObject.messageOwner.attachPath = new File(FileLoader.getInstance().getDirectory(4), "video_preview.mp4").getAbsolutePath();
          this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
          this.videoPreviewMessageObject.videoEditedInfo.rotationValue = this.rotationValue;
          this.videoPreviewMessageObject.videoEditedInfo.originalWidth = this.originalWidth;
          this.videoPreviewMessageObject.videoEditedInfo.originalHeight = this.originalHeight;
          this.videoPreviewMessageObject.videoEditedInfo.originalPath = this.videoPath;
        }
        Object localObject = this.videoPreviewMessageObject.videoEditedInfo;
        long l2 = this.startTime;
        ((VideoEditedInfo)localObject).startTime = l2;
        localObject = this.videoPreviewMessageObject.videoEditedInfo;
        long l3 = this.endTime;
        ((VideoEditedInfo)localObject).endTime = l3;
        long l1 = l2;
        if (l2 == -1L) {
          l1 = 0L;
        }
        l2 = l3;
        if (l3 == -1L) {
          l2 = (this.videoDuration * 1000.0F);
        }
        if (l2 - l1 > 5000000L) {
          this.videoPreviewMessageObject.videoEditedInfo.endTime = (5000000L + l1);
        }
        this.videoPreviewMessageObject.videoEditedInfo.bitrate = this.bitrate;
        this.videoPreviewMessageObject.videoEditedInfo.resultWidth = this.resultWidth;
        this.videoPreviewMessageObject.videoEditedInfo.resultHeight = this.resultHeight;
        if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
          this.tryStartRequestPreviewOnFinish = true;
        }
      } while (this.videoPlayer != null);
      this.requestingPreview = true;
      this.playButton.setImageDrawable(null);
      this.progressView.setVisibility(0);
      return;
      this.tryStartRequestPreviewOnFinish = false;
    } while (paramInt != 2);
    reinitPlayer(this.videoPath);
  }
  
  private void showQualityView(final boolean paramBoolean)
  {
    if (paramBoolean) {
      this.previousCompression = this.selectedCompression;
    }
    AnimatorSet localAnimatorSet = new AnimatorSet();
    if (paramBoolean) {
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(152.0F) }), ObjectAnimator.ofFloat(this.videoTimelineView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(152.0F) }), ObjectAnimator.ofFloat(this.videoSeekBarView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(152.0F) }) });
    }
    for (;;)
    {
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramAnonymousAnimator = new AnimatorSet();
          if (paramBoolean)
          {
            VideoEditorActivity.this.qualityChooseView.setVisibility(0);
            VideoEditorActivity.this.qualityPicker.setVisibility(0);
            paramAnonymousAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(VideoEditorActivity.this.qualityChooseView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(VideoEditorActivity.this.qualityPicker, "translationY", new float[] { 0.0F }) });
          }
          for (;;)
          {
            paramAnonymousAnimator.setDuration(200L);
            paramAnonymousAnimator.setInterpolator(new AccelerateInterpolator());
            paramAnonymousAnimator.start();
            return;
            VideoEditorActivity.this.qualityChooseView.setVisibility(4);
            VideoEditorActivity.this.qualityPicker.setVisibility(4);
            paramAnonymousAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(VideoEditorActivity.this.pickerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(VideoEditorActivity.this.videoTimelineView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(VideoEditorActivity.this.videoSeekBarView, "translationY", new float[] { 0.0F }) });
          }
        }
      });
      localAnimatorSet.setDuration(200L);
      localAnimatorSet.setInterpolator(new DecelerateInterpolator());
      localAnimatorSet.start();
      return;
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.qualityChooseView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(120.0F) }), ObjectAnimator.ofFloat(this.qualityPicker, "translationY", new float[] { 0.0F, AndroidUtilities.dp(120.0F) }) });
    }
  }
  
  private void updateVideoInfo()
  {
    if (this.actionBar == null) {
      return;
    }
    int i;
    label113:
    int j;
    label137:
    label177:
    label196:
    ActionBar localActionBar;
    if (this.selectedCompression == 0)
    {
      this.compressItem.setImageResource(NUM);
      this.esimatedDuration = (Math.ceil((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
      if ((this.compressItem.getVisibility() != 8) && ((this.compressItem.getVisibility() != 0) || (this.selectedCompression != this.compressionsCount - 1))) {
        break label430;
      }
      if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
        break label414;
      }
      i = this.originalHeight;
      if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
        break label422;
      }
      j = this.originalWidth;
      this.estimatedSize = ((int)((float)this.originalSize * ((float)this.esimatedDuration / this.videoDuration)));
      if (this.videoTimelineView.getLeftProgress() != 0.0F) {
        break label543;
      }
      this.startTime = -1L;
      if (this.videoTimelineView.getRightProgress() != 1.0F) {
        break label567;
      }
      this.endTime = -1L;
      str = String.format("%dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
      i = (int)(this.esimatedDuration / 1000L / 60L);
      this.currentSubtitle = String.format("%s, %s", new Object[] { str, String.format("%d:%02d, ~%s", new Object[] { Integer.valueOf(i), Integer.valueOf((int)Math.ceil(this.esimatedDuration / 1000L) - i * 60), AndroidUtilities.formatFileSize(this.estimatedSize) }) });
      localActionBar = this.actionBar;
      if (!this.muteVideo) {
        break label591;
      }
    }
    label414:
    label422:
    label430:
    label454:
    label535:
    label543:
    label567:
    label591:
    for (String str = null;; str = this.currentSubtitle)
    {
      localActionBar.setSubtitle(str);
      return;
      if (this.selectedCompression == 1)
      {
        this.compressItem.setImageResource(NUM);
        break;
      }
      if (this.selectedCompression == 2)
      {
        this.compressItem.setImageResource(NUM);
        break;
      }
      if (this.selectedCompression == 3)
      {
        this.compressItem.setImageResource(NUM);
        break;
      }
      if (this.selectedCompression != 4) {
        break;
      }
      this.compressItem.setImageResource(NUM);
      break;
      i = this.originalWidth;
      break label113;
      j = this.originalHeight;
      break label137;
      if ((this.rotationValue == 90) || (this.rotationValue == 270))
      {
        i = this.resultHeight;
        if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
          break label535;
        }
      }
      for (j = this.resultWidth;; j = this.resultHeight)
      {
        this.estimatedSize = ((int)((float)(this.audioFramesSize + this.videoFramesSize) * ((float)this.esimatedDuration / this.videoDuration)));
        this.estimatedSize += this.estimatedSize / 32768 * 16;
        break;
        i = this.resultWidth;
        break label454;
      }
      this.startTime = ((this.videoTimelineView.getLeftProgress() * this.videoDuration) * 1000L);
      break label177;
      this.endTime = ((this.videoTimelineView.getRightProgress() * this.videoDuration) * 1000L);
      break label196;
    }
  }
  
  private void updateWidthHeightBitrateForCompression()
  {
    int i;
    float f;
    if (this.compressionsCount == -1)
    {
      if ((this.originalWidth > 1280) || (this.originalHeight > 1280)) {
        this.compressionsCount = 5;
      }
    }
    else
    {
      if (this.selectedCompression >= this.compressionsCount) {
        this.selectedCompression = (this.compressionsCount - 1);
      }
      if (this.selectedCompression != this.compressionsCount - 1) {
        switch (this.selectedCompression)
        {
        default: 
          i = 1600000;
          f = 1280.0F;
          label104:
          if (this.originalWidth <= this.originalHeight) {
            break;
          }
        }
      }
    }
    for (f /= this.originalWidth;; f /= this.originalHeight)
    {
      this.resultWidth = (Math.round(this.originalWidth * f / 2.0F) * 2);
      this.resultHeight = (Math.round(this.originalHeight * f / 2.0F) * 2);
      if (this.bitrate != 0)
      {
        this.bitrate = Math.min(i, (int)(this.originalBitrate / f));
        this.videoFramesSize = ((this.bitrate / 8 * this.videoDuration / 1000.0F));
      }
      return;
      if ((this.originalWidth > 848) || (this.originalHeight > 848))
      {
        this.compressionsCount = 4;
        break;
      }
      if ((this.originalWidth > 640) || (this.originalHeight > 640))
      {
        this.compressionsCount = 3;
        break;
      }
      if ((this.originalWidth > 480) || (this.originalHeight > 480))
      {
        this.compressionsCount = 2;
        break;
      }
      this.compressionsCount = 1;
      break;
      f = 432.0F;
      i = 400000;
      break label104;
      f = 640.0F;
      i = 900000;
      break label104;
      f = 848.0F;
      i = 1100000;
      break label104;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(-16777216);
    this.actionBar.setTitleColor(-1);
    this.actionBar.setItemsBackgroundColor(-12763843, false);
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setTitle(LocaleController.getString("AttachVideo", NUM));
    this.actionBar.setSubtitleColor(-1);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          if (VideoEditorActivity.this.pickerView.getVisibility() != 0) {
            VideoEditorActivity.this.closeCaptionEnter(false);
          }
        }
        while (paramAnonymousInt != 1)
        {
          return;
          VideoEditorActivity.this.finishFragment();
          return;
        }
        VideoEditorActivity.this.closeCaptionEnter(true);
      }
    });
    this.fragmentView = new SizeNotifierFrameLayoutPhoto(paramContext)
    {
      int lastWidth;
      
      public boolean dispatchKeyEventPreIme(KeyEvent paramAnonymousKeyEvent)
      {
        if ((paramAnonymousKeyEvent != null) && (paramAnonymousKeyEvent.getKeyCode() == 4) && (paramAnonymousKeyEvent.getAction() == 1) && ((VideoEditorActivity.this.captionEditText.isPopupShowing()) || (VideoEditorActivity.this.captionEditText.isKeyboardVisible())))
        {
          VideoEditorActivity.this.closeCaptionEnter(false);
          return false;
        }
        return super.dispatchKeyEventPreIme(paramAnonymousKeyEvent);
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        int i1 = getChildCount();
        int k;
        if ((getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) && (!AndroidUtilities.isInMultiwindow) && (!AndroidUtilities.isTablet()))
        {
          k = VideoEditorActivity.this.captionEditText.getEmojiPadding();
          if (AndroidUtilities.isTablet()) {
            break label103;
          }
        }
        View localView;
        label103:
        for (int m = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();; m = AndroidUtilities.dp(424.0F))
        {
          int n = 0;
          for (;;)
          {
            if (n >= i1) {
              break label767;
            }
            localView = getChildAt(n);
            if (localView.getVisibility() != 8) {
              break;
            }
            n += 1;
          }
          k = 0;
          break;
        }
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        int i2 = localView.getMeasuredWidth();
        int i3 = localView.getMeasuredHeight();
        int j = localLayoutParams.gravity;
        int i = j;
        if (j == -1) {
          i = 51;
        }
        switch (i & 0x7 & 0x7)
        {
        default: 
          j = localLayoutParams.leftMargin;
          label199:
          switch (i & 0x70)
          {
          default: 
            i = localLayoutParams.topMargin;
            label247:
            if (localView == VideoEditorActivity.this.mentionListView)
            {
              i = paramAnonymousInt4 - k - paramAnonymousInt2 - i3 - localLayoutParams.bottomMargin;
              if ((VideoEditorActivity.this.pickerView.getVisibility() == 0) || ((VideoEditorActivity.this.firstCaptionLayout) && (!VideoEditorActivity.this.captionEditText.isPopupShowing()) && (!VideoEditorActivity.this.captionEditText.isKeyboardVisible()) && (VideoEditorActivity.this.captionEditText.getEmojiPadding() == 0))) {
                i += AndroidUtilities.dp(400.0F);
              }
            }
            break;
          }
          break;
        }
        for (;;)
        {
          localView.layout(j, i, j + i2, i + i3);
          break;
          j = (paramAnonymousInt3 - paramAnonymousInt1 - i2) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
          break label199;
          j = paramAnonymousInt3 - i2 - localLayoutParams.rightMargin;
          break label199;
          i = localLayoutParams.topMargin;
          break label247;
          i = (m - i3) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
          break label247;
          i = m - i3 - localLayoutParams.bottomMargin;
          break label247;
          i -= VideoEditorActivity.this.captionEditText.getMeasuredHeight();
          continue;
          if (localView == VideoEditorActivity.this.captionEditText)
          {
            i = paramAnonymousInt4 - k - paramAnonymousInt2 - i3 - localLayoutParams.bottomMargin;
            if ((VideoEditorActivity.this.pickerView.getVisibility() == 0) || ((VideoEditorActivity.this.firstCaptionLayout) && (!VideoEditorActivity.this.captionEditText.isPopupShowing()) && (!VideoEditorActivity.this.captionEditText.isKeyboardVisible()) && (VideoEditorActivity.this.captionEditText.getEmojiPadding() == 0))) {
              i += AndroidUtilities.dp(400.0F);
            } else {
              VideoEditorActivity.access$2302(VideoEditorActivity.this, false);
            }
          }
          else if (VideoEditorActivity.this.captionEditText.isPopupView(localView))
          {
            if ((AndroidUtilities.isInMultiwindow) || (AndroidUtilities.isTablet())) {
              i = VideoEditorActivity.this.captionEditText.getTop() - localView.getMeasuredHeight() + AndroidUtilities.dp(1.0F);
            } else {
              i = VideoEditorActivity.this.captionEditText.getBottom();
            }
          }
          else if (localView == VideoEditorActivity.this.textureView)
          {
            j = (paramAnonymousInt3 - paramAnonymousInt1 - VideoEditorActivity.this.textureView.getMeasuredWidth()) / 2;
            if (AndroidUtilities.isTablet()) {
              i = (m - AndroidUtilities.dp(166.0F) - VideoEditorActivity.this.textureView.getMeasuredHeight()) / 2 + AndroidUtilities.dp(14.0F);
            } else {
              i = (m - AndroidUtilities.dp(166.0F) - VideoEditorActivity.this.textureView.getMeasuredHeight()) / 2 + AndroidUtilities.dp(14.0F);
            }
          }
        }
        label767:
        notifyHeightChanged();
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        int i1 = View.MeasureSpec.getSize(paramAnonymousInt1);
        setMeasuredDimension(i1, View.MeasureSpec.getSize(paramAnonymousInt2));
        int i;
        int i3;
        int j;
        label70:
        View localView;
        if (!AndroidUtilities.isTablet())
        {
          i = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
          measureChildWithMargins(VideoEditorActivity.this.captionEditText, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
          i3 = VideoEditorActivity.this.captionEditText.getMeasuredHeight();
          int i4 = getChildCount();
          j = 0;
          if (j >= i4) {
            break label462;
          }
          localView = getChildAt(j);
          if ((localView.getVisibility() != 8) && (localView != VideoEditorActivity.this.captionEditText)) {
            break label126;
          }
        }
        for (;;)
        {
          j += 1;
          break label70;
          i = AndroidUtilities.dp(424.0F);
          break;
          label126:
          if (VideoEditorActivity.this.captionEditText.isPopupView(localView))
          {
            if ((AndroidUtilities.isInMultiwindow) || (AndroidUtilities.isTablet()))
            {
              if (AndroidUtilities.isTablet()) {
                localView.measure(View.MeasureSpec.makeMeasureSpec(i1, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0F), View.MeasureSpec.getSize(paramAnonymousInt2) - i3), NUM));
              } else {
                localView.measure(View.MeasureSpec.makeMeasureSpec(i1, NUM), View.MeasureSpec.makeMeasureSpec(i - i3 - AndroidUtilities.statusBarHeight, NUM));
              }
            }
            else {
              localView.measure(View.MeasureSpec.makeMeasureSpec(i1, NUM), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, NUM));
            }
          }
          else
          {
            if (localView == VideoEditorActivity.this.textureView)
            {
              int k = i1;
              int i2 = i - AndroidUtilities.dp(166.0F);
              label311:
              int n;
              label345:
              float f3;
              if ((VideoEditorActivity.this.rotationValue == 90) || (VideoEditorActivity.this.rotationValue == 270))
              {
                m = VideoEditorActivity.this.originalHeight;
                if ((VideoEditorActivity.this.rotationValue != 90) && (VideoEditorActivity.this.rotationValue != 270)) {
                  break label425;
                }
                n = VideoEditorActivity.this.originalWidth;
                float f1 = k / m;
                float f2 = i2 / n;
                f3 = m / n;
                if (f1 <= f2) {
                  break label437;
                }
                k = (int)(i2 * f3);
              }
              label425:
              label437:
              for (int m = i2;; m = (int)(k / f3))
              {
                localView.measure(View.MeasureSpec.makeMeasureSpec(k, NUM), View.MeasureSpec.makeMeasureSpec(m, NUM));
                break;
                m = VideoEditorActivity.this.originalWidth;
                break label311;
                n = VideoEditorActivity.this.originalHeight;
                break label345;
              }
            }
            measureChildWithMargins(localView, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
          }
        }
        label462:
        if (this.lastWidth != i1)
        {
          VideoEditorActivity.this.videoTimelineView.clearFrames();
          this.lastWidth = i1;
        }
      }
    };
    this.fragmentView.setBackgroundColor(-16777216);
    SizeNotifierFrameLayoutPhoto localSizeNotifierFrameLayoutPhoto = (SizeNotifierFrameLayoutPhoto)this.fragmentView;
    localSizeNotifierFrameLayoutPhoto.setWithoutWindow(true);
    this.fragmentView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.pickerView = new PickerBottomLayoutViewer(paramContext);
    this.pickerView.setBackgroundColor(0);
    this.pickerView.updateSelectedCount(0, false);
    localSizeNotifierFrameLayoutPhoto.addView(this.pickerView, LayoutHelper.createFrame(-1, 48, 83));
    this.pickerView.cancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        VideoEditorActivity.this.finishFragment();
      }
    });
    this.pickerView.doneButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View arg1)
      {
        int i;
        long l3;
        long l4;
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
                if ((VideoEditorActivity.this.muteVideo) || ((VideoEditorActivity.this.compressItem.getVisibility() != 8) && ((VideoEditorActivity.this.compressItem.getVisibility() != 0) || (VideoEditorActivity.this.selectedCompression != VideoEditorActivity.this.compressionsCount - 1)))) {
                  break;
                }
                localObject1 = VideoEditorActivity.this.delegate;
                str = VideoEditorActivity.this.videoPath;
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
                  l3 = VideoEditorActivity.this.estimatedSize;
                  l4 = VideoEditorActivity.this.esimatedDuration;
                  if (VideoEditorActivity.this.currentCaption == null) {
                    break label320;
                  }
                  ??? = VideoEditorActivity.this.currentCaption.toString();
                  ((VideoEditorActivity.VideoEditorActivityDelegate)localObject1).didFinishEditVideo(str, l1, l2, j, k, m, n, i1, i, l3, l4, ???);
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
              FileLog.e(localException);
              continue;
            }
          }
          i = VideoEditorActivity.this.originalBitrate;
          continue;
          label320:
          ??? = null;
        }
        if (VideoEditorActivity.this.muteVideo)
        {
          VideoEditorActivity.access$1102(VideoEditorActivity.this, 1);
          VideoEditorActivity.this.updateWidthHeightBitrateForCompression();
        }
        VideoEditorActivity.VideoEditorActivityDelegate localVideoEditorActivityDelegate = VideoEditorActivity.this.delegate;
        String str = VideoEditorActivity.this.videoPath;
        long l1 = VideoEditorActivity.this.startTime;
        long l2 = VideoEditorActivity.this.endTime;
        int j = VideoEditorActivity.this.resultWidth;
        int k = VideoEditorActivity.this.resultHeight;
        int m = VideoEditorActivity.this.rotationValue;
        int n = VideoEditorActivity.this.originalWidth;
        int i1 = VideoEditorActivity.this.originalHeight;
        if (VideoEditorActivity.this.muteVideo)
        {
          i = -1;
          label443:
          l3 = VideoEditorActivity.this.estimatedSize;
          l4 = VideoEditorActivity.this.esimatedDuration;
          if (VideoEditorActivity.this.currentCaption == null) {
            break label527;
          }
        }
        label527:
        for (??? = VideoEditorActivity.this.currentCaption.toString();; ??? = null)
        {
          localVideoEditorActivityDelegate.didFinishEditVideo(str, l1, l2, j, k, m, n, i1, i, l3, l4, ???);
          break;
          i = VideoEditorActivity.this.bitrate;
          break label443;
        }
      }
    });
    Object localObject1 = new LinearLayout(paramContext);
    ((LinearLayout)localObject1).setOrientation(0);
    this.pickerView.addView((View)localObject1, LayoutHelper.createFrame(-2, 48, 49));
    this.captionItem = new ImageView(paramContext);
    this.captionItem.setScaleType(ImageView.ScaleType.CENTER);
    Object localObject2 = this.captionItem;
    int i;
    if (TextUtils.isEmpty(this.currentCaption))
    {
      i = NUM;
      ((ImageView)localObject2).setImageResource(i);
      this.captionItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
      ((LinearLayout)localObject1).addView(this.captionItem, LayoutHelper.createLinear(56, 48));
      this.captionItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          VideoEditorActivity.this.captionEditText.setFieldText(VideoEditorActivity.this.currentCaption);
          VideoEditorActivity.this.pickerView.setVisibility(8);
          VideoEditorActivity.access$2302(VideoEditorActivity.this, true);
          if (!AndroidUtilities.isTablet())
          {
            VideoEditorActivity.this.videoSeekBarView.setVisibility(8);
            VideoEditorActivity.this.videoTimelineView.setVisibility(8);
          }
          VideoEditorActivity.this.captionEditText.openKeyboard();
          ActionBar localActionBar = VideoEditorActivity.this.actionBar;
          if (VideoEditorActivity.this.muteVideo) {}
          for (paramAnonymousView = LocaleController.getString("GifCaption", NUM);; paramAnonymousView = LocaleController.getString("VideoCaption", NUM))
          {
            localActionBar.setTitle(paramAnonymousView);
            VideoEditorActivity.this.actionBar.setSubtitle(null);
            return;
          }
        }
      });
      this.compressItem = new ImageView(paramContext);
      this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
      this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
      localObject2 = this.compressItem;
      if (this.compressionsCount <= 1) {
        break label1282;
      }
      i = 0;
      label387:
      ((ImageView)localObject2).setVisibility(i);
      ((LinearLayout)localObject1).addView(this.compressItem, LayoutHelper.createLinear(56, 48));
      this.compressItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          VideoEditorActivity.this.showQualityView(true);
          VideoEditorActivity.this.requestVideoPreview(1);
        }
      });
      if (Build.VERSION.SDK_INT >= 18) {}
    }
    for (;;)
    {
      try
      {
        localObject2 = MediaController.selectCodec("video/avc");
        if (localObject2 != null) {
          continue;
        }
        this.compressItem.setVisibility(8);
      }
      catch (Exception localException)
      {
        label1282:
        String str;
        this.compressItem.setVisibility(8);
        FileLog.e(localException);
        continue;
        if (MediaController.selectColorFormat(localException, "video/avc") != 0) {
          continue;
        }
        this.compressItem.setVisibility(8);
        continue;
      }
      this.muteItem = new ImageView(paramContext);
      this.muteItem.setScaleType(ImageView.ScaleType.CENTER);
      this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
      ((LinearLayout)localObject1).addView(this.muteItem, LayoutHelper.createLinear(56, 48));
      this.muteItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = VideoEditorActivity.this;
          if (!VideoEditorActivity.this.muteVideo) {}
          for (boolean bool = true;; bool = false)
          {
            VideoEditorActivity.access$2502(paramAnonymousView, bool);
            VideoEditorActivity.this.updateMuteButton();
            return;
          }
        }
      });
      this.videoTimelineView = new VideoTimelineView(paramContext);
      this.videoTimelineView.setVideoPath(this.videoPath);
      this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate()
      {
        public void didStartDragging() {}
        
        public void didStopDragging() {}
        
        public void onLeftProgressChanged(float paramAnonymousFloat)
        {
          if ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared)) {
            return;
          }
          for (;;)
          {
            try
            {
              if (VideoEditorActivity.this.videoPlayer.isPlaying())
              {
                VideoEditorActivity.this.videoPlayer.pause();
                VideoEditorActivity.this.playButton.setImageResource(NUM);
              }
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
              continue;
            }
            try
            {
              VideoEditorActivity.this.getParentActivity().getWindow().clearFlags(128);
              VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
              VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * paramAnonymousFloat));
              VideoEditorActivity.access$4202(VideoEditorActivity.this, true);
              VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
              VideoEditorActivity.this.updateVideoInfo();
              return;
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
            }
          }
        }
        
        public void onRifhtProgressChanged(float paramAnonymousFloat)
        {
          if ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared)) {
            return;
          }
          for (;;)
          {
            try
            {
              if (VideoEditorActivity.this.videoPlayer.isPlaying())
              {
                VideoEditorActivity.this.videoPlayer.pause();
                VideoEditorActivity.this.playButton.setImageResource(NUM);
              }
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
              continue;
            }
            try
            {
              VideoEditorActivity.this.getParentActivity().getWindow().clearFlags(128);
              VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
              VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * paramAnonymousFloat));
              VideoEditorActivity.access$4202(VideoEditorActivity.this, true);
              VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
              VideoEditorActivity.this.updateVideoInfo();
              return;
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
            }
          }
        }
      });
      localSizeNotifierFrameLayoutPhoto.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 44.0F, 83, 0.0F, 0.0F, 0.0F, 67.0F));
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
          try
          {
            VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * f));
            VideoEditorActivity.access$602(VideoEditorActivity.this, f);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
      localSizeNotifierFrameLayoutPhoto.addView(this.videoSeekBarView, LayoutHelper.createFrame(-1, 40.0F, 83, 11.0F, 0.0F, 11.0F, 112.0F));
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
              if (VideoEditorActivity.this.playerPrepared) {
                if (VideoEditorActivity.this.inPreview)
                {
                  VideoEditorActivity.this.videoPlayer.seekTo(0);
                  return;
                }
              }
            }
            catch (Exception paramAnonymousSurfaceTexture)
            {
              FileLog.e(paramAnonymousSurfaceTexture);
              return;
            }
          }
          VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
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
      localSizeNotifierFrameLayoutPhoto.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 14.0F, 0.0F, 140.0F));
      this.progressView = new RadialProgressView(paramContext);
      this.progressView.setProgressColor(-1);
      this.progressView.setBackgroundResource(NUM);
      this.progressView.setVisibility(4);
      localSizeNotifierFrameLayoutPhoto.addView(this.progressView, LayoutHelper.createFrame(54, 54.0F, 17, 0.0F, 0.0F, 0.0F, 70.0F));
      this.playButton = new ImageView(paramContext);
      this.playButton.setScaleType(ImageView.ScaleType.CENTER);
      this.playButton.setImageResource(NUM);
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        /* Error */
        public void onClick(View arg1)
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   4: invokestatic 33	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
          //   7: ifnull +33 -> 40
          //   10: aload_0
          //   11: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   14: invokestatic 37	org/telegram/ui/VideoEditorActivity:access$1500	(Lorg/telegram/ui/VideoEditorActivity;)Z
          //   17: ifeq +23 -> 40
          //   20: aload_0
          //   21: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   24: invokestatic 40	org/telegram/ui/VideoEditorActivity:access$4400	(Lorg/telegram/ui/VideoEditorActivity;)Z
          //   27: ifne +13 -> 40
          //   30: aload_0
          //   31: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   34: invokestatic 43	org/telegram/ui/VideoEditorActivity:access$4500	(Lorg/telegram/ui/VideoEditorActivity;)Z
          //   37: ifeq +4 -> 41
          //   40: return
          //   41: aload_0
          //   42: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   45: invokestatic 33	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
          //   48: invokevirtual 49	android/media/MediaPlayer:isPlaying	()Z
          //   51: ifeq +48 -> 99
          //   54: aload_0
          //   55: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   58: invokestatic 33	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
          //   61: invokevirtual 52	android/media/MediaPlayer:pause	()V
          //   64: aload_0
          //   65: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   68: invokestatic 56	org/telegram/ui/VideoEditorActivity:access$4100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/widget/ImageView;
          //   71: ldc 57
          //   73: invokevirtual 63	android/widget/ImageView:setImageResource	(I)V
          //   76: aload_0
          //   77: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   80: invokevirtual 67	org/telegram/ui/VideoEditorActivity:getParentActivity	()Landroid/app/Activity;
          //   83: invokevirtual 73	android/app/Activity:getWindow	()Landroid/view/Window;
          //   86: sipush 128
          //   89: invokevirtual 78	android/view/Window:clearFlags	(I)V
          //   92: return
          //   93: astore_1
          //   94: aload_1
          //   95: invokestatic 84	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   98: return
          //   99: aload_0
          //   100: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   103: invokestatic 56	org/telegram/ui/VideoEditorActivity:access$4100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/widget/ImageView;
          //   106: aconst_null
          //   107: invokevirtual 88	android/widget/ImageView:setImageDrawable	(Landroid/graphics/drawable/Drawable;)V
          //   110: aload_0
          //   111: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   114: fconst_0
          //   115: invokestatic 92	org/telegram/ui/VideoEditorActivity:access$602	(Lorg/telegram/ui/VideoEditorActivity;F)F
          //   118: pop
          //   119: aload_0
          //   120: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   123: invokestatic 95	org/telegram/ui/VideoEditorActivity:access$4200	(Lorg/telegram/ui/VideoEditorActivity;)Z
          //   126: ifeq +41 -> 167
          //   129: aload_0
          //   130: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   133: invokestatic 33	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
          //   136: aload_0
          //   137: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   140: invokestatic 99	org/telegram/ui/VideoEditorActivity:access$500	(Lorg/telegram/ui/VideoEditorActivity;)F
          //   143: aload_0
          //   144: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   147: invokestatic 103	org/telegram/ui/VideoEditorActivity:access$700	(Lorg/telegram/ui/VideoEditorActivity;)Lorg/telegram/ui/Components/VideoSeekBarView;
          //   150: invokevirtual 109	org/telegram/ui/Components/VideoSeekBarView:getProgress	()F
          //   153: fmul
          //   154: f2i
          //   155: invokevirtual 112	android/media/MediaPlayer:seekTo	(I)V
          //   158: aload_0
          //   159: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   162: iconst_0
          //   163: invokestatic 116	org/telegram/ui/VideoEditorActivity:access$4202	(Lorg/telegram/ui/VideoEditorActivity;Z)Z
          //   166: pop
          //   167: aload_0
          //   168: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   171: invokestatic 33	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
          //   174: new 13	org/telegram/ui/VideoEditorActivity$14$1
          //   177: dup
          //   178: aload_0
          //   179: invokespecial 119	org/telegram/ui/VideoEditorActivity$14$1:<init>	(Lorg/telegram/ui/VideoEditorActivity$14;)V
          //   182: invokevirtual 123	android/media/MediaPlayer:setOnSeekCompleteListener	(Landroid/media/MediaPlayer$OnSeekCompleteListener;)V
          //   185: aload_0
          //   186: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   189: invokestatic 33	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
          //   192: new 15	org/telegram/ui/VideoEditorActivity$14$2
          //   195: dup
          //   196: aload_0
          //   197: invokespecial 124	org/telegram/ui/VideoEditorActivity$14$2:<init>	(Lorg/telegram/ui/VideoEditorActivity$14;)V
          //   200: invokevirtual 128	android/media/MediaPlayer:setOnCompletionListener	(Landroid/media/MediaPlayer$OnCompletionListener;)V
          //   203: aload_0
          //   204: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   207: invokestatic 33	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
          //   210: invokevirtual 131	android/media/MediaPlayer:start	()V
          //   213: aload_0
          //   214: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   217: invokevirtual 67	org/telegram/ui/VideoEditorActivity:getParentActivity	()Landroid/app/Activity;
          //   220: invokevirtual 73	android/app/Activity:getWindow	()Landroid/view/Window;
          //   223: sipush 128
          //   226: invokevirtual 134	android/view/Window:addFlags	(I)V
          //   229: aload_0
          //   230: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   233: invokestatic 138	org/telegram/ui/VideoEditorActivity:access$000	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Object;
          //   236: astore_1
          //   237: aload_1
          //   238: monitorenter
          //   239: aload_0
          //   240: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   243: invokestatic 142	org/telegram/ui/VideoEditorActivity:access$900	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Thread;
          //   246: ifnonnull +35 -> 281
          //   249: aload_0
          //   250: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   253: new 144	java/lang/Thread
          //   256: dup
          //   257: aload_0
          //   258: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   261: invokestatic 148	org/telegram/ui/VideoEditorActivity:access$4600	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Runnable;
          //   264: invokespecial 151	java/lang/Thread:<init>	(Ljava/lang/Runnable;)V
          //   267: invokestatic 155	org/telegram/ui/VideoEditorActivity:access$902	(Lorg/telegram/ui/VideoEditorActivity;Ljava/lang/Thread;)Ljava/lang/Thread;
          //   270: pop
          //   271: aload_0
          //   272: getfield 21	org/telegram/ui/VideoEditorActivity$14:this$0	Lorg/telegram/ui/VideoEditorActivity;
          //   275: invokestatic 142	org/telegram/ui/VideoEditorActivity:access$900	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Thread;
          //   278: invokevirtual 156	java/lang/Thread:start	()V
          //   281: aload_1
          //   282: monitorexit
          //   283: return
          //   284: astore_2
          //   285: aload_1
          //   286: monitorexit
          //   287: aload_2
          //   288: athrow
          //   289: astore_1
          //   290: aload_1
          //   291: invokestatic 84	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   294: return
          //   295: astore_1
          //   296: aload_1
          //   297: invokestatic 84	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   300: goto -71 -> 229
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	303	0	this	14
          //   284	4	2	localObject	Object
          // Exception table:
          //   from	to	target	type
          //   76	92	93	java/lang/Exception
          //   239	281	284	finally
          //   281	283	284	finally
          //   285	287	284	finally
          //   99	167	289	java/lang/Exception
          //   167	213	289	java/lang/Exception
          //   229	239	289	java/lang/Exception
          //   287	289	289	java/lang/Exception
          //   296	300	289	java/lang/Exception
          //   213	229	295	java/lang/Exception
        }
      });
      localSizeNotifierFrameLayoutPhoto.addView(this.playButton, LayoutHelper.createFrame(100, 100.0F, 17, 0.0F, 0.0F, 0.0F, 70.0F));
      if (this.captionEditText != null) {
        this.captionEditText.onDestroy();
      }
      this.captionEditText = new PhotoViewerCaptionEnterView(paramContext, localSizeNotifierFrameLayoutPhoto, null);
      this.captionEditText.setForceFloatingEmoji(AndroidUtilities.isTablet());
      this.captionEditText.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate()
      {
        private int[] location = new int[2];
        private int previousSize;
        private int previousY;
        
        public void onCaptionEnter()
        {
          VideoEditorActivity.this.closeCaptionEnter(true);
        }
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence)
        {
          if ((VideoEditorActivity.this.mentionsAdapter != null) && (VideoEditorActivity.this.captionEditText != null) && (VideoEditorActivity.this.parentChatActivity != null) && (paramAnonymousCharSequence != null)) {
            VideoEditorActivity.this.mentionsAdapter.searchUsernameOrHashtag(paramAnonymousCharSequence.toString(), VideoEditorActivity.this.captionEditText.getCursorPosition(), VideoEditorActivity.this.parentChatActivity.messages);
          }
        }
        
        public void onWindowSizeChanged(int paramAnonymousInt)
        {
          int j = Math.min(3, VideoEditorActivity.this.mentionsAdapter.getItemCount());
          int i;
          if (VideoEditorActivity.this.mentionsAdapter.getItemCount() > 3)
          {
            i = 18;
            i = AndroidUtilities.dp(i + j * 36);
            if (paramAnonymousInt - ActionBar.getCurrentActionBarHeight() * 2 >= i) {
              break label163;
            }
            VideoEditorActivity.access$4902(VideoEditorActivity.this, false);
            if ((VideoEditorActivity.this.mentionListView != null) && (VideoEditorActivity.this.mentionListView.getVisibility() == 0)) {
              VideoEditorActivity.this.mentionListView.setVisibility(4);
            }
          }
          for (;;)
          {
            VideoEditorActivity.this.fragmentView.getLocationInWindow(this.location);
            if ((this.previousSize != paramAnonymousInt) || (this.previousY != this.location[1]))
            {
              VideoEditorActivity.this.fragmentView.requestLayout();
              this.previousSize = paramAnonymousInt;
              this.previousY = this.location[1];
            }
            return;
            i = 0;
            break;
            label163:
            VideoEditorActivity.access$4902(VideoEditorActivity.this, true);
            if ((VideoEditorActivity.this.mentionListView != null) && (VideoEditorActivity.this.mentionListView.getVisibility() == 4)) {
              VideoEditorActivity.this.mentionListView.setVisibility(0);
            }
          }
        }
      });
      localSizeNotifierFrameLayoutPhoto.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
      this.captionEditText.onCreate();
      this.mentionListView = new RecyclerListView(paramContext);
      this.mentionListView.setTag(Integer.valueOf(5));
      this.mentionLayoutManager = new LinearLayoutManager(paramContext)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.mentionLayoutManager.setOrientation(1);
      this.mentionListView.setLayoutManager(this.mentionLayoutManager);
      this.mentionListView.setBackgroundColor(NUM);
      this.mentionListView.setVisibility(8);
      this.mentionListView.setClipToPadding(true);
      this.mentionListView.setOverScrollMode(2);
      localSizeNotifierFrameLayoutPhoto.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
      localObject1 = this.mentionListView;
      localObject2 = new MentionsAdapter(paramContext, true, 0L, new MentionsAdapter.MentionsAdapterDelegate()
      {
        public void needChangePanelVisibility(boolean paramAnonymousBoolean)
        {
          int i;
          if (paramAnonymousBoolean)
          {
            FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)VideoEditorActivity.this.mentionListView.getLayoutParams();
            int j = Math.min(3, VideoEditorActivity.this.mentionsAdapter.getItemCount());
            if (VideoEditorActivity.this.mentionsAdapter.getItemCount() > 3)
            {
              i = 18;
              i = j * 36 + i;
              localLayoutParams.height = AndroidUtilities.dp(i);
              localLayoutParams.topMargin = (-AndroidUtilities.dp(i));
              VideoEditorActivity.this.mentionListView.setLayoutParams(localLayoutParams);
              if (VideoEditorActivity.this.mentionListAnimation != null)
              {
                VideoEditorActivity.this.mentionListAnimation.cancel();
                VideoEditorActivity.access$5202(VideoEditorActivity.this, null);
              }
              if (VideoEditorActivity.this.mentionListView.getVisibility() != 0) {
                break label150;
              }
              VideoEditorActivity.this.mentionListView.setAlpha(1.0F);
            }
          }
          label150:
          do
          {
            return;
            i = 0;
            break;
            VideoEditorActivity.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
            if (VideoEditorActivity.this.allowMentions)
            {
              VideoEditorActivity.this.mentionListView.setVisibility(0);
              VideoEditorActivity.access$5202(VideoEditorActivity.this, new AnimatorSet());
              VideoEditorActivity.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(VideoEditorActivity.this.mentionListView, "alpha", new float[] { 0.0F, 1.0F }) });
              VideoEditorActivity.this.mentionListAnimation.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  if ((VideoEditorActivity.this.mentionListAnimation != null) && (VideoEditorActivity.this.mentionListAnimation.equals(paramAnonymous2Animator))) {
                    VideoEditorActivity.access$5202(VideoEditorActivity.this, null);
                  }
                }
              });
              VideoEditorActivity.this.mentionListAnimation.setDuration(200L);
              VideoEditorActivity.this.mentionListAnimation.start();
              return;
            }
            VideoEditorActivity.this.mentionListView.setAlpha(1.0F);
            VideoEditorActivity.this.mentionListView.setVisibility(4);
            return;
            if (VideoEditorActivity.this.mentionListAnimation != null)
            {
              VideoEditorActivity.this.mentionListAnimation.cancel();
              VideoEditorActivity.access$5202(VideoEditorActivity.this, null);
            }
          } while (VideoEditorActivity.this.mentionListView.getVisibility() == 8);
          if (VideoEditorActivity.this.allowMentions)
          {
            VideoEditorActivity.access$5202(VideoEditorActivity.this, new AnimatorSet());
            VideoEditorActivity.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(VideoEditorActivity.this.mentionListView, "alpha", new float[] { 0.0F }) });
            VideoEditorActivity.this.mentionListAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                if ((VideoEditorActivity.this.mentionListAnimation != null) && (VideoEditorActivity.this.mentionListAnimation.equals(paramAnonymous2Animator)))
                {
                  VideoEditorActivity.this.mentionListView.setVisibility(8);
                  VideoEditorActivity.access$5202(VideoEditorActivity.this, null);
                }
              }
            });
            VideoEditorActivity.this.mentionListAnimation.setDuration(200L);
            VideoEditorActivity.this.mentionListAnimation.start();
            return;
          }
          VideoEditorActivity.this.mentionListView.setVisibility(8);
        }
        
        public void onContextClick(TLRPC.BotInlineResult paramAnonymousBotInlineResult) {}
        
        public void onContextSearch(boolean paramAnonymousBoolean) {}
      });
      this.mentionsAdapter = ((MentionsAdapter)localObject2);
      ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
      this.mentionsAdapter.setAllowNewMentions(false);
      this.mentionListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          paramAnonymousView = VideoEditorActivity.this.mentionsAdapter.getItem(paramAnonymousInt);
          paramAnonymousInt = VideoEditorActivity.this.mentionsAdapter.getResultStartPosition();
          int i = VideoEditorActivity.this.mentionsAdapter.getResultLength();
          if ((paramAnonymousView instanceof TLRPC.User))
          {
            paramAnonymousView = (TLRPC.User)paramAnonymousView;
            if (paramAnonymousView != null) {
              VideoEditorActivity.this.captionEditText.replaceWithText(paramAnonymousInt, i, "@" + paramAnonymousView.username + " ");
            }
          }
          while (!(paramAnonymousView instanceof String)) {
            return;
          }
          VideoEditorActivity.this.captionEditText.replaceWithText(paramAnonymousInt, i, paramAnonymousView + " ");
        }
      });
      this.mentionListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (VideoEditorActivity.this.getParentActivity() == null) {}
          while (!(VideoEditorActivity.this.mentionsAdapter.getItem(paramAnonymousInt) instanceof String)) {
            return false;
          }
          paramAnonymousView = new AlertDialog.Builder(VideoEditorActivity.this.getParentActivity());
          paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
          paramAnonymousView.setMessage(LocaleController.getString("ClearSearch", NUM));
          paramAnonymousView.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              VideoEditorActivity.this.mentionsAdapter.clearRecentHashtags();
            }
          });
          paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
          VideoEditorActivity.this.showDialog(paramAnonymousView.create());
          return true;
        }
      });
      if (this.compressionsCount > 1)
      {
        this.qualityPicker = new PickerBottomLayoutViewer(paramContext);
        this.qualityPicker.setBackgroundColor(0);
        this.qualityPicker.updateSelectedCount(0, false);
        this.qualityPicker.setTranslationY(AndroidUtilities.dp(120.0F));
        this.qualityPicker.doneButton.setText(LocaleController.getString("Done", NUM).toUpperCase());
        localSizeNotifierFrameLayoutPhoto.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
        this.qualityPicker.cancelButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            VideoEditorActivity.access$1102(VideoEditorActivity.this, VideoEditorActivity.this.previousCompression);
            VideoEditorActivity.this.didChangedCompressionLevel(false);
            VideoEditorActivity.this.showQualityView(false);
            VideoEditorActivity.this.requestVideoPreview(2);
          }
        });
        this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            VideoEditorActivity.this.showQualityView(false);
            VideoEditorActivity.this.requestVideoPreview(2);
          }
        });
        this.qualityChooseView = new QualityChooseView(paramContext);
        this.qualityChooseView.setTranslationY(AndroidUtilities.dp(120.0F));
        this.qualityChooseView.setVisibility(4);
        localSizeNotifierFrameLayoutPhoto.addView(this.qualityChooseView, LayoutHelper.createFrame(-1, 90.0F, 83, 0.0F, 0.0F, 0.0F, 44.0F));
      }
      updateVideoInfo();
      updateMuteButton();
      return this.fragmentView;
      i = NUM;
      break;
      i = 8;
      break label387;
      str = ((MediaCodecInfo)localObject2).getName();
      if ((!str.equals("OMX.google.h264.encoder")) && (!str.equals("OMX.ST.VFM.H264Enc")) && (!str.equals("OMX.Exynos.avc.enc")) && (!str.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER")) && (!str.equals("OMX.MARVELL.VIDEO.H264ENCODER")) && (!str.equals("OMX.k3.video.encoder.avc")) && (!str.equals("OMX.TI.DUCATI1.VIDEO.H264E"))) {
        continue;
      }
      this.compressItem.setVisibility(8);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    boolean bool = true;
    if (paramInt == NotificationCenter.closeChats) {
      removeSelfFromStack();
    }
    String str;
    do
    {
      do
      {
        do
        {
          return;
          if (paramInt != NotificationCenter.FilePreparingFailed) {
            break;
          }
          paramVarArgs = (MessageObject)paramVarArgs[0];
          if (this.loadInitialVideo)
          {
            this.loadInitialVideo = false;
            this.progressView.setVisibility(4);
            reinitPlayer(this.videoPath);
            return;
          }
          if (this.tryStartRequestPreviewOnFinish)
          {
            destroyPlayer();
            if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {}
            for (;;)
            {
              this.tryStartRequestPreviewOnFinish = bool;
              return;
              bool = false;
            }
          }
        } while (paramVarArgs != this.videoPreviewMessageObject);
        this.requestingPreview = false;
        this.progressView.setVisibility(4);
        this.playButton.setImageResource(NUM);
        return;
      } while ((paramInt != NotificationCenter.FileNewChunkAvailable) || ((MessageObject)paramVarArgs[0] != this.videoPreviewMessageObject));
      str = (String)paramVarArgs[1];
    } while (((Long)paramVarArgs[2]).longValue() == 0L);
    this.requestingPreview = false;
    this.progressView.setVisibility(4);
    reinitPlayer(str);
  }
  
  public boolean onFragmentCreate()
  {
    if (this.created) {
      return true;
    }
    if ((this.videoPath == null) || (!processOpenVideo())) {
      return false;
    }
    if (!reinitPlayer(this.videoPath)) {
      return false;
    }
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
    this.created = true;
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    try
    {
      getParentActivity().getWindow().clearFlags(128);
      if (this.videoTimelineView != null) {
        this.videoTimelineView.destroy();
      }
      if (this.videoPlayer == null) {}
    }
    catch (Exception localException1)
    {
      try
      {
        this.videoPlayer.stop();
        this.videoPlayer.release();
        this.videoPlayer = null;
        if (this.captionEditText != null) {
          this.captionEditText.onDestroy();
        }
        requestVideoPreview(0);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileNewChunkAvailable);
        super.onFragmentDestroy();
        return;
        localException1 = localException1;
        FileLog.e(localException1);
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
      }
    }
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.pickerView.getVisibility() == 8) {
      closeCaptionEnter(true);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.textureView != null) {}
    try
    {
      if ((this.playerPrepared) && (!this.videoPlayer.isPlaying())) {
        this.videoPlayer.seekTo((int)(this.videoSeekBarView.getProgress() * this.videoDuration));
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }
  
  public void setDelegate(VideoEditorActivityDelegate paramVideoEditorActivityDelegate)
  {
    this.delegate = paramVideoEditorActivityDelegate;
  }
  
  public void setParentChatActivity(ChatActivity paramChatActivity)
  {
    this.parentChatActivity = paramChatActivity;
  }
  
  public void updateMuteButton()
  {
    if (this.videoPlayer != null) {
      if (!this.muteVideo) {
        break label125;
      }
    }
    label125:
    for (float f = 0.0F;; f = 1.0F)
    {
      if (this.videoPlayer != null) {
        this.videoPlayer.setVolume(f, f);
      }
      if (!this.muteVideo) {
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("AttachGif", NUM));
      this.actionBar.setSubtitle(null);
      this.muteItem.setImageResource(NUM);
      if (this.compressItem.getVisibility() == 0)
      {
        this.compressItem.setClickable(false);
        this.compressItem.setAlpha(0.5F);
        this.compressItem.setEnabled(false);
      }
      this.videoTimelineView.setMaxProgressDiff(30000.0F / this.videoDuration);
      return;
    }
    this.actionBar.setTitle(LocaleController.getString("AttachVideo", NUM));
    this.actionBar.setSubtitle(this.currentSubtitle);
    this.muteItem.setImageResource(NUM);
    if (this.compressItem.getVisibility() == 0)
    {
      this.compressItem.setClickable(true);
      this.compressItem.setAlpha(1.0F);
      this.compressItem.setEnabled(true);
    }
    this.videoTimelineView.setMaxProgressDiff(1.0F);
  }
  
  private class QualityChooseView
    extends View
  {
    private int circleSize;
    private int gapSize;
    private int lineSize;
    private boolean moving;
    private Paint paint = new Paint(1);
    private int sideSide;
    private boolean startMoving;
    private int startMovingQuality;
    private float startX;
    private TextPaint textPaint = new TextPaint(1);
    
    public QualityChooseView(Context paramContext)
    {
      super();
      this.textPaint.setTextSize(AndroidUtilities.dp(12.0F));
      this.textPaint.setColor(-3289651);
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int j = getMeasuredHeight() / 2 + AndroidUtilities.dp(6.0F);
      int i = 0;
      if (i < VideoEditorActivity.this.compressionsCount)
      {
        int k = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
        label83:
        String str;
        label124:
        float f2;
        float f3;
        float f4;
        if (i <= VideoEditorActivity.this.selectedCompression)
        {
          this.paint.setColor(-11292945);
          if (i != VideoEditorActivity.this.compressionsCount - 1) {
            break label284;
          }
          str = VideoEditorActivity.this.originalHeight + "p";
          f2 = this.textPaint.measureText(str);
          f3 = k;
          f4 = j;
          if (i != VideoEditorActivity.this.selectedCompression) {
            break label329;
          }
        }
        label284:
        label329:
        for (float f1 = AndroidUtilities.dp(8.0F);; f1 = this.circleSize / 2)
        {
          paramCanvas.drawCircle(f3, f4, f1, this.paint);
          paramCanvas.drawText(str, k - f2 / 2.0F, j - AndroidUtilities.dp(16.0F), this.textPaint);
          if (i != 0)
          {
            k = k - this.circleSize / 2 - this.gapSize - this.lineSize;
            paramCanvas.drawRect(k, j - AndroidUtilities.dp(1.0F), this.lineSize + k, AndroidUtilities.dp(2.0F) + j, this.paint);
          }
          i += 1;
          break;
          this.paint.setColor(-14540254);
          break label83;
          if (i == 0)
          {
            str = "240p";
            break label124;
          }
          if (i == 1)
          {
            str = "360p";
            break label124;
          }
          if (i == 2)
          {
            str = "480p";
            break label124;
          }
          str = "720p";
          break label124;
        }
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      View.MeasureSpec.getSize(paramInt1);
      this.circleSize = AndroidUtilities.dp(12.0F);
      this.gapSize = AndroidUtilities.dp(2.0F);
      this.sideSide = AndroidUtilities.dp(18.0F);
      this.lineSize = ((getMeasuredWidth() - this.circleSize * VideoEditorActivity.this.compressionsCount - this.gapSize * 8 - this.sideSide * 2) / (VideoEditorActivity.this.compressionsCount - 1));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool = false;
      float f = paramMotionEvent.getX();
      int i;
      int j;
      if (paramMotionEvent.getAction() == 0)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        i = 0;
        if (i < VideoEditorActivity.this.compressionsCount)
        {
          j = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
          if ((f <= j - AndroidUtilities.dp(15.0F)) || (f >= AndroidUtilities.dp(15.0F) + j)) {
            break label136;
          }
          if (i == VideoEditorActivity.this.selectedCompression) {
            bool = true;
          }
          this.startMoving = bool;
          this.startX = f;
          this.startMovingQuality = VideoEditorActivity.this.selectedCompression;
        }
      }
      label136:
      label322:
      label324:
      do
      {
        for (;;)
        {
          return true;
          i += 1;
          break;
          if (paramMotionEvent.getAction() != 2) {
            break label324;
          }
          if (this.startMoving)
          {
            if (Math.abs(this.startX - f) >= AndroidUtilities.getPixelsInCM(0.5F, true))
            {
              this.moving = true;
              this.startMoving = false;
              return true;
            }
          }
          else if (this.moving)
          {
            i = 0;
            for (;;)
            {
              if (i >= VideoEditorActivity.this.compressionsCount) {
                break label322;
              }
              j = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
              int k = this.lineSize / 2 + this.circleSize / 2 + this.gapSize;
              if ((f > j - k) && (f < j + k))
              {
                if (VideoEditorActivity.this.selectedCompression == i) {
                  break;
                }
                VideoEditorActivity.access$1102(VideoEditorActivity.this, i);
                VideoEditorActivity.this.didChangedCompressionLevel(false);
                invalidate();
                return true;
              }
              i += 1;
            }
          }
        }
      } while ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3));
      if (!this.moving)
      {
        i = 0;
        if (i < VideoEditorActivity.this.compressionsCount)
        {
          j = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
          if ((f <= j - AndroidUtilities.dp(15.0F)) || (f >= AndroidUtilities.dp(15.0F) + j)) {
            break label464;
          }
          if (VideoEditorActivity.this.selectedCompression != i)
          {
            VideoEditorActivity.access$1102(VideoEditorActivity.this, i);
            VideoEditorActivity.this.didChangedCompressionLevel(true);
            invalidate();
          }
        }
      }
      for (;;)
      {
        this.startMoving = false;
        this.moving = false;
        return true;
        label464:
        i += 1;
        break;
        if (VideoEditorActivity.this.selectedCompression != this.startMovingQuality) {
          VideoEditorActivity.this.requestVideoPreview(1);
        }
      }
    }
  }
  
  public static abstract interface VideoEditorActivityDelegate
  {
    public abstract void didFinishEditVideo(String paramString1, long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, long paramLong3, long paramLong4, String paramString2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/VideoEditorActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */