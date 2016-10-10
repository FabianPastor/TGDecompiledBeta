package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;

public class SharedDocumentCell
  extends FrameLayout
  implements MediaController.FileDownloadProgressListener
{
  private static Paint paint;
  private int TAG;
  private CheckBox checkBox;
  private TextView dateTextView;
  private TextView extTextView;
  private int[] icons = { 2130837812, 2130837813, 2130837816, 2130837817 };
  private boolean loaded;
  private boolean loading;
  private MessageObject message;
  private TextView nameTextView;
  private boolean needDivider;
  private ImageView placeholderImabeView;
  private LineProgressView progressView;
  private ImageView statusImageView;
  private BackupImageView thumbImageView;
  
  public SharedDocumentCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.TAG = MediaController.getInstance().generateObserverTag();
    this.placeholderImabeView = new ImageView(paramContext);
    Object localObject = this.placeholderImabeView;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label890;
      }
      f1 = 0.0F;
      label108:
      if (!LocaleController.isRTL) {
        break label896;
      }
      f2 = 12.0F;
      label117:
      addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.extTextView = new TextView(paramContext);
      this.extTextView.setTextColor(-1);
      this.extTextView.setTextSize(1, 14.0F);
      this.extTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.extTextView.setLines(1);
      this.extTextView.setMaxLines(1);
      this.extTextView.setSingleLine(true);
      this.extTextView.setGravity(17);
      this.extTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.extTextView;
      if (!LocaleController.isRTL) {
        break label901;
      }
      i = 5;
      label240:
      if (!LocaleController.isRTL) {
        break label907;
      }
      f1 = 0.0F;
      label248:
      if (!LocaleController.isRTL) {
        break label913;
      }
      f2 = 16.0F;
      label257:
      addView((View)localObject, LayoutHelper.createFrame(32, -2.0F, i | 0x30, f1, 22.0F, f2, 0.0F));
      this.thumbImageView = new BackupImageView(paramContext);
      localObject = this.thumbImageView;
      if (!LocaleController.isRTL) {
        break label918;
      }
      i = 5;
      label307:
      if (!LocaleController.isRTL) {
        break label924;
      }
      f1 = 0.0F;
      label315:
      if (!LocaleController.isRTL) {
        break label930;
      }
      f2 = 12.0F;
      label324:
      addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.thumbImageView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramAnonymousImageReceiver, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
        {
          int j = 4;
          paramAnonymousImageReceiver = SharedDocumentCell.this.extTextView;
          if (paramAnonymousBoolean1)
          {
            i = 4;
            paramAnonymousImageReceiver.setVisibility(i);
            paramAnonymousImageReceiver = SharedDocumentCell.this.placeholderImabeView;
            if (!paramAnonymousBoolean1) {
              break label53;
            }
          }
          label53:
          for (int i = j;; i = 0)
          {
            paramAnonymousImageReceiver.setVisibility(i);
            return;
            i = 0;
            break;
          }
        }
      });
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setLines(1);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label935;
      }
      i = 5;
      label457:
      ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label941;
      }
      i = 5;
      label482:
      if (!LocaleController.isRTL) {
        break label947;
      }
      f1 = 8.0F;
      label491:
      if (!LocaleController.isRTL) {
        break label953;
      }
      f2 = 72.0F;
      label500:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 5.0F, f2, 0.0F));
      this.statusImageView = new ImageView(paramContext);
      this.statusImageView.setVisibility(4);
      localObject = this.statusImageView;
      if (!LocaleController.isRTL) {
        break label959;
      }
      i = 5;
      label557:
      if (!LocaleController.isRTL) {
        break label965;
      }
      f1 = 8.0F;
      label566:
      if (!LocaleController.isRTL) {
        break label971;
      }
      f2 = 72.0F;
      label575:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 35.0F, f2, 0.0F));
      this.dateTextView = new TextView(paramContext);
      this.dateTextView.setTextColor(-6710887);
      this.dateTextView.setTextSize(1, 14.0F);
      this.dateTextView.setLines(1);
      this.dateTextView.setMaxLines(1);
      this.dateTextView.setSingleLine(true);
      this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.dateTextView;
      if (!LocaleController.isRTL) {
        break label977;
      }
      i = 5;
      label678:
      ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.dateTextView;
      if (!LocaleController.isRTL) {
        break label983;
      }
      i = 5;
      label703:
      if (!LocaleController.isRTL) {
        break label989;
      }
      f1 = 8.0F;
      label712:
      if (!LocaleController.isRTL) {
        break label995;
      }
      f2 = 72.0F;
      label721:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 30.0F, f2, 0.0F));
      this.progressView = new LineProgressView(paramContext);
      localObject = this.progressView;
      if (!LocaleController.isRTL) {
        break label1001;
      }
      i = 5;
      label770:
      if (!LocaleController.isRTL) {
        break label1007;
      }
      f1 = 0.0F;
      label778:
      if (!LocaleController.isRTL) {
        break label1013;
      }
      f2 = 72.0F;
      label787:
      addView((View)localObject, LayoutHelper.createFrame(-1, 2.0F, i | 0x30, f1, 54.0F, f2, 0.0F));
      this.checkBox = new CheckBox(paramContext, 2130837959);
      this.checkBox.setVisibility(4);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label1018;
      }
      i = 5;
      label844:
      if (!LocaleController.isRTL) {
        break label1024;
      }
      f1 = 0.0F;
      label852:
      if (!LocaleController.isRTL) {
        break label1030;
      }
    }
    label890:
    label896:
    label901:
    label907:
    label913:
    label918:
    label924:
    label930:
    label935:
    label941:
    label947:
    label953:
    label959:
    label965:
    label971:
    label977:
    label983:
    label989:
    label995:
    label1001:
    label1007:
    label1013:
    label1018:
    label1024:
    label1030:
    for (float f2 = 34.0F;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 30.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 12.0F;
      break label108;
      f2 = 0.0F;
      break label117;
      i = 3;
      break label240;
      f1 = 16.0F;
      break label248;
      f2 = 0.0F;
      break label257;
      i = 3;
      break label307;
      f1 = 12.0F;
      break label315;
      f2 = 0.0F;
      break label324;
      i = 3;
      break label457;
      i = 3;
      break label482;
      f1 = 72.0F;
      break label491;
      f2 = 8.0F;
      break label500;
      i = 3;
      break label557;
      f1 = 72.0F;
      break label566;
      f2 = 8.0F;
      break label575;
      i = 3;
      break label678;
      i = 3;
      break label703;
      f1 = 72.0F;
      break label712;
      f2 = 8.0F;
      break label721;
      i = 3;
      break label770;
      f1 = 72.0F;
      break label778;
      f2 = 0.0F;
      break label787;
      i = 3;
      break label844;
      f1 = 34.0F;
      break label852;
    }
  }
  
  private int getThumbForNameOrMime(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString1.length() != 0))
    {
      int i = -1;
      if ((paramString1.contains(".doc")) || (paramString1.contains(".txt")) || (paramString1.contains(".psd")))
      {
        i = 0;
        j = i;
        if (i == -1)
        {
          i = paramString1.lastIndexOf('.');
          if (i != -1) {
            break label207;
          }
          paramString2 = "";
          label65:
          if (paramString2.length() == 0) {
            break label218;
          }
        }
      }
      label207:
      label218:
      for (int j = paramString2.charAt(0) % this.icons.length;; j = paramString1.charAt(0) % this.icons.length)
      {
        return this.icons[j];
        if ((paramString1.contains(".xls")) || (paramString1.contains(".csv")))
        {
          i = 1;
          break;
        }
        if ((paramString1.contains(".pdf")) || (paramString1.contains(".ppt")) || (paramString1.contains(".key")))
        {
          i = 2;
          break;
        }
        if ((!paramString1.contains(".zip")) && (!paramString1.contains(".rar")) && (!paramString1.contains(".ai")) && (!paramString1.contains(".mp3")) && (!paramString1.contains(".mov")) && (!paramString1.contains(".avi"))) {
          break;
        }
        i = 3;
        break;
        paramString2 = paramString1.substring(i + 1);
        break label65;
      }
    }
    return this.icons[0];
  }
  
  public MessageObject getMessage()
  {
    return this.message;
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  public boolean isLoaded()
  {
    return this.loaded;
  }
  
  public boolean isLoading()
  {
    return this.loading;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    MediaController.getInstance().removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(AndroidUtilities.dp(72.0F), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
  }
  
  public void onFailedDownload(String paramString)
  {
    updateFileExistIcon();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = AndroidUtilities.dp(56.0F);
    if (this.needDivider) {}
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
      return;
    }
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    if (this.progressView.getVisibility() != 0) {
      updateFileExistIcon();
    }
    this.progressView.setProgress(paramFloat, true);
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean) {}
  
  public void onSuccessDownload(String paramString)
  {
    this.progressView.setProgress(1.0F, true);
    updateFileExistIcon();
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox.getVisibility() != 0) {
      this.checkBox.setVisibility(0);
    }
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setDocument(MessageObject paramMessageObject, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.message = paramMessageObject;
    this.loaded = false;
    this.loading = false;
    Object localObject1;
    Object localObject3;
    int i;
    if ((paramMessageObject != null) && (paramMessageObject.getDocument() != null))
    {
      localObject1 = null;
      Object localObject2 = null;
      if (paramMessageObject.isMusic())
      {
        if (paramMessageObject.type == 0) {}
        for (localObject3 = paramMessageObject.messageOwner.media.webpage.document;; localObject3 = paramMessageObject.messageOwner.media.document)
        {
          i = 0;
          for (;;)
          {
            localObject1 = localObject2;
            if (i >= ((TLRPC.Document)localObject3).attributes.size()) {
              break;
            }
            TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject3).attributes.get(i);
            localObject1 = localObject2;
            if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
              if ((localDocumentAttribute.performer == null) || (localDocumentAttribute.performer.length() == 0))
              {
                localObject1 = localObject2;
                if (localDocumentAttribute.title != null)
                {
                  localObject1 = localObject2;
                  if (localDocumentAttribute.title.length() == 0) {}
                }
              }
              else
              {
                localObject1 = paramMessageObject.getMusicAuthor() + " - " + paramMessageObject.getMusicTitle();
              }
            }
            i += 1;
            localObject2 = localObject1;
          }
        }
      }
      localObject3 = FileLoader.getDocumentFileName(paramMessageObject.getDocument());
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = localObject3;
      }
      this.nameTextView.setText((CharSequence)localObject2);
      this.placeholderImabeView.setVisibility(0);
      this.extTextView.setVisibility(0);
      this.placeholderImabeView.setImageResource(getThumbForNameOrMime((String)localObject3, paramMessageObject.getDocument().mime_type));
      localObject2 = this.extTextView;
      i = ((String)localObject3).lastIndexOf('.');
      if (i == -1)
      {
        localObject1 = "";
        ((TextView)localObject2).setText((CharSequence)localObject1);
        if ((!(paramMessageObject.getDocument().thumb instanceof TLRPC.TL_photoSizeEmpty)) && (paramMessageObject.getDocument().thumb != null)) {
          break label497;
        }
        this.thumbImageView.setVisibility(4);
        this.thumbImageView.setImageBitmap(null);
        label350:
        long l = paramMessageObject.messageOwner.date * 1000L;
        this.dateTextView.setText(String.format("%s, %s", new Object[] { AndroidUtilities.formatFileSize(paramMessageObject.getDocument().size), LocaleController.formatString("formatDateAtTime", 2131166435, new Object[] { LocaleController.getInstance().formatterYear.format(new Date(l)), LocaleController.getInstance().formatterDay.format(new Date(l)) }) }));
        label453:
        if (this.needDivider) {
          break label594;
        }
      }
    }
    label497:
    label594:
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      this.progressView.setProgress(0.0F, false);
      updateFileExistIcon();
      return;
      localObject1 = ((String)localObject3).substring(i + 1).toLowerCase();
      break;
      this.thumbImageView.setVisibility(0);
      this.thumbImageView.setImage(paramMessageObject.getDocument().thumb.location, "40_40", (Drawable)null);
      break label350;
      this.nameTextView.setText("");
      this.extTextView.setText("");
      this.dateTextView.setText("");
      this.placeholderImabeView.setVisibility(0);
      this.extTextView.setVisibility(0);
      this.thumbImageView.setVisibility(4);
      this.thumbImageView.setImageBitmap(null);
      break label453;
    }
  }
  
  public void setTextAndValueAndTypeAndThumb(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    this.nameTextView.setText(paramString1);
    this.dateTextView.setText(paramString2);
    if (paramString3 != null)
    {
      this.extTextView.setVisibility(0);
      this.extTextView.setText(paramString3);
      if (paramInt != 0) {
        break label110;
      }
      this.placeholderImabeView.setImageResource(getThumbForNameOrMime(paramString1, paramString3));
      this.placeholderImabeView.setVisibility(0);
      label62:
      if ((paramString4 == null) && (paramInt == 0)) {
        break label133;
      }
      if (paramString4 == null) {
        break label121;
      }
      this.thumbImageView.setImage(paramString4, "40_40", null);
    }
    for (;;)
    {
      this.thumbImageView.setVisibility(0);
      return;
      this.extTextView.setVisibility(4);
      break;
      label110:
      this.placeholderImabeView.setVisibility(4);
      break label62;
      label121:
      this.thumbImageView.setImageResource(paramInt);
    }
    label133:
    this.thumbImageView.setVisibility(4);
  }
  
  public void updateFileExistIcon()
  {
    if ((this.message != null) && (this.message.messageOwner.media != null))
    {
      Object localObject2 = null;
      Object localObject1;
      if ((this.message.messageOwner.attachPath != null) && (this.message.messageOwner.attachPath.length() != 0))
      {
        localObject1 = localObject2;
        if (new File(this.message.messageOwner.attachPath).exists()) {}
      }
      else
      {
        localObject1 = localObject2;
        if (!FileLoader.getPathToMessage(this.message.messageOwner).exists()) {
          localObject1 = FileLoader.getAttachFileName(this.message.getDocument());
        }
      }
      this.loaded = false;
      if (localObject1 == null)
      {
        this.statusImageView.setVisibility(4);
        this.dateTextView.setPadding(0, 0, 0, 0);
        this.loading = false;
        this.loaded = true;
        MediaController.getInstance().removeLoadingFileObserver(this);
        return;
      }
      MediaController.getInstance().addLoadingFileObserver((String)localObject1, this);
      this.loading = FileLoader.getInstance().isLoadingFile((String)localObject1);
      this.statusImageView.setVisibility(0);
      localObject2 = this.statusImageView;
      int i;
      if (this.loading)
      {
        i = 2130837815;
        ((ImageView)localObject2).setImageResource(i);
        localObject2 = this.dateTextView;
        if (!LocaleController.isRTL) {
          break label296;
        }
        i = 0;
        label218:
        if (!LocaleController.isRTL) {
          break label305;
        }
      }
      label296:
      label305:
      for (int j = AndroidUtilities.dp(14.0F);; j = 0)
      {
        ((TextView)localObject2).setPadding(i, 0, j, 0);
        if (!this.loading) {
          break label310;
        }
        this.progressView.setVisibility(0);
        localObject2 = ImageLoader.getInstance().getFileProgress((String)localObject1);
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = Float.valueOf(0.0F);
        }
        this.progressView.setProgress(((Float)localObject1).floatValue(), false);
        return;
        i = 2130837814;
        break;
        i = AndroidUtilities.dp(14.0F);
        break label218;
      }
      label310:
      this.progressView.setVisibility(4);
      return;
    }
    this.loading = false;
    this.loaded = true;
    this.progressView.setVisibility(4);
    this.progressView.setProgress(0.0F, false);
    this.statusImageView.setVisibility(4);
    this.dateTextView.setPadding(0, 0, 0, 0);
    MediaController.getInstance().removeLoadingFileObserver(this);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/SharedDocumentCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */