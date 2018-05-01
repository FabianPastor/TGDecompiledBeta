package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
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
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;

public class SharedDocumentCell
  extends FrameLayout
  implements DownloadController.FileDownloadProgressListener
{
  private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
  private CheckBox checkBox;
  private int currentAccount = UserConfig.selectedAccount;
  private TextView dateTextView;
  private TextView extTextView;
  private int[] icons = { NUM, NUM, NUM, NUM };
  private boolean loaded;
  private boolean loading;
  private MessageObject message;
  private TextView nameTextView;
  private boolean needDivider;
  private ImageView placeholderImageView;
  private LineProgressView progressView;
  private ImageView statusImageView;
  private BackupImageView thumbImageView;
  
  public SharedDocumentCell(Context paramContext)
  {
    super(paramContext);
    this.placeholderImageView = new ImageView(paramContext);
    Object localObject = this.placeholderImageView;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label924;
      }
      f1 = 0.0F;
      label87:
      if (!LocaleController.isRTL) {
        break label931;
      }
      f2 = 12.0F;
      label97:
      addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.extTextView = new TextView(paramContext);
      this.extTextView.setTextColor(Theme.getColor("files_iconText"));
      this.extTextView.setTextSize(1, 14.0F);
      this.extTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.extTextView.setLines(1);
      this.extTextView.setMaxLines(1);
      this.extTextView.setSingleLine(true);
      this.extTextView.setGravity(17);
      this.extTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.extTextView;
      if (!LocaleController.isRTL) {
        break label937;
      }
      i = 5;
      label222:
      if (!LocaleController.isRTL) {
        break label942;
      }
      f1 = 0.0F;
      label231:
      if (!LocaleController.isRTL) {
        break label949;
      }
      f2 = 16.0F;
      label241:
      addView((View)localObject, LayoutHelper.createFrame(32, -2.0F, i | 0x30, f1, 22.0F, f2, 0.0F));
      this.thumbImageView = new BackupImageView(paramContext);
      localObject = this.thumbImageView;
      if (!LocaleController.isRTL) {
        break label955;
      }
      i = 5;
      label289:
      if (!LocaleController.isRTL) {
        break label960;
      }
      f1 = 0.0F;
      label298:
      if (!LocaleController.isRTL) {
        break label967;
      }
      f2 = 12.0F;
      label308:
      addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.thumbImageView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramAnonymousImageReceiver, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
        {
          int i = 4;
          paramAnonymousImageReceiver = SharedDocumentCell.this.extTextView;
          if (paramAnonymousBoolean1)
          {
            j = 4;
            paramAnonymousImageReceiver.setVisibility(j);
            paramAnonymousImageReceiver = SharedDocumentCell.this.placeholderImageView;
            if (!paramAnonymousBoolean1) {
              break label53;
            }
          }
          label53:
          for (int j = i;; j = 0)
          {
            paramAnonymousImageReceiver.setVisibility(j);
            return;
            j = 0;
            break;
          }
        }
      });
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setLines(1);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label973;
      }
      i = 5;
      label442:
      ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label978;
      }
      i = 5;
      label463:
      if (!LocaleController.isRTL) {
        break label983;
      }
      f1 = 8.0F;
      label473:
      if (!LocaleController.isRTL) {
        break label990;
      }
      f2 = 72.0F;
      label483:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 5.0F, f2, 0.0F));
      this.statusImageView = new ImageView(paramContext);
      this.statusImageView.setVisibility(4);
      this.statusImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sharedMedia_startStopLoadIcon"), PorterDuff.Mode.MULTIPLY));
      localObject = this.statusImageView;
      if (!LocaleController.isRTL) {
        break label997;
      }
      i = 5;
      label560:
      if (!LocaleController.isRTL) {
        break label1002;
      }
      f1 = 8.0F;
      label570:
      if (!LocaleController.isRTL) {
        break label1009;
      }
      f2 = 72.0F;
      label580:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 35.0F, f2, 0.0F));
      this.dateTextView = new TextView(paramContext);
      this.dateTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      this.dateTextView.setTextSize(1, 14.0F);
      this.dateTextView.setLines(1);
      this.dateTextView.setMaxLines(1);
      this.dateTextView.setSingleLine(true);
      this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.dateTextView;
      if (!LocaleController.isRTL) {
        break label1016;
      }
      i = 5;
      label684:
      ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.dateTextView;
      if (!LocaleController.isRTL) {
        break label1021;
      }
      i = 5;
      label705:
      if (!LocaleController.isRTL) {
        break label1026;
      }
      f1 = 8.0F;
      label715:
      if (!LocaleController.isRTL) {
        break label1033;
      }
      f2 = 72.0F;
      label725:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 30.0F, f2, 0.0F));
      this.progressView = new LineProgressView(paramContext);
      this.progressView.setProgressColor(Theme.getColor("sharedMedia_startStopLoadIcon"));
      localObject = this.progressView;
      if (!LocaleController.isRTL) {
        break label1040;
      }
      i = 5;
      label784:
      if (!LocaleController.isRTL) {
        break label1045;
      }
      f1 = 0.0F;
      label793:
      if (!LocaleController.isRTL) {
        break label1052;
      }
      f2 = 72.0F;
      label803:
      addView((View)localObject, LayoutHelper.createFrame(-1, 2.0F, i | 0x30, f1, 54.0F, f2, 0.0F));
      this.checkBox = new CheckBox(paramContext, NUM);
      this.checkBox.setVisibility(4);
      this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label1058;
      }
      i = 5;
      label876:
      if (!LocaleController.isRTL) {
        break label1063;
      }
      f1 = 0.0F;
      label885:
      if (!LocaleController.isRTL) {
        break label1070;
      }
    }
    label924:
    label931:
    label937:
    label942:
    label949:
    label955:
    label960:
    label967:
    label973:
    label978:
    label983:
    label990:
    label997:
    label1002:
    label1009:
    label1016:
    label1021:
    label1026:
    label1033:
    label1040:
    label1045:
    label1052:
    label1058:
    label1063:
    label1070:
    for (float f2 = 34.0F;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 30.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 12.0F;
      break label87;
      f2 = 0.0F;
      break label97;
      i = 3;
      break label222;
      f1 = 16.0F;
      break label231;
      f2 = 0.0F;
      break label241;
      i = 3;
      break label289;
      f1 = 12.0F;
      break label298;
      f2 = 0.0F;
      break label308;
      i = 3;
      break label442;
      i = 3;
      break label463;
      f1 = 72.0F;
      break label473;
      f2 = 8.0F;
      break label483;
      i = 3;
      break label560;
      f1 = 72.0F;
      break label570;
      f2 = 8.0F;
      break label580;
      i = 3;
      break label684;
      i = 3;
      break label705;
      f1 = 72.0F;
      break label715;
      f2 = 8.0F;
      break label725;
      i = 3;
      break label784;
      f1 = 72.0F;
      break label793;
      f2 = 0.0F;
      break label803;
      i = 3;
      break label876;
      f1 = 34.0F;
      break label885;
    }
  }
  
  private int getThumbForNameOrMime(String paramString1, String paramString2)
  {
    int j;
    if ((paramString1 != null) && (paramString1.length() != 0))
    {
      i = -1;
      if ((paramString1.contains(".doc")) || (paramString1.contains(".txt")) || (paramString1.contains(".psd")))
      {
        i = 0;
        j = i;
        if (i == -1)
        {
          i = paramString1.lastIndexOf('.');
          if (i != -1) {
            break label220;
          }
          paramString2 = "";
          label65:
          if (paramString2.length() == 0) {
            break label231;
          }
          j = paramString2.charAt(0) % this.icons.length;
        }
      }
    }
    label85:
    for (int i = this.icons[j];; i = this.icons[0])
    {
      return i;
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
      label220:
      paramString2 = paramString1.substring(i + 1);
      break label65;
      label231:
      j = paramString1.charAt(0) % this.icons.length;
      break label85;
    }
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
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.progressView.getVisibility() == 0) {
      updateFileExistIcon();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(AndroidUtilities.dp(72.0F), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    }
  }
  
  public void onFailedDownload(String paramString)
  {
    updateFileExistIcon();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    int i = AndroidUtilities.dp(56.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, NUM));
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
            i++;
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
      this.placeholderImageView.setVisibility(0);
      this.extTextView.setVisibility(0);
      this.placeholderImageView.setImageResource(getThumbForNameOrMime((String)localObject3, paramMessageObject.getDocument().mime_type));
      localObject2 = this.extTextView;
      i = ((String)localObject3).lastIndexOf('.');
      if (i == -1)
      {
        localObject1 = "";
        ((TextView)localObject2).setText((CharSequence)localObject1);
        if ((!(paramMessageObject.getDocument().thumb instanceof TLRPC.TL_photoSizeEmpty)) && (paramMessageObject.getDocument().thumb != null)) {
          break label490;
        }
        this.thumbImageView.setVisibility(4);
        this.thumbImageView.setImageBitmap(null);
        label343:
        long l = paramMessageObject.messageOwner.date * 1000L;
        this.dateTextView.setText(String.format("%s, %s", new Object[] { AndroidUtilities.formatFileSize(paramMessageObject.getDocument().size), LocaleController.formatString("formatDateAtTime", NUM, new Object[] { LocaleController.getInstance().formatterYear.format(new Date(l)), LocaleController.getInstance().formatterDay.format(new Date(l)) }) }));
        label446:
        if (this.needDivider) {
          break label587;
        }
      }
    }
    label490:
    label587:
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
      break label343;
      this.nameTextView.setText("");
      this.extTextView.setText("");
      this.dateTextView.setText("");
      this.placeholderImageView.setVisibility(0);
      this.extTextView.setVisibility(0);
      this.thumbImageView.setVisibility(4);
      this.thumbImageView.setImageBitmap(null);
      break label446;
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
      this.placeholderImageView.setImageResource(getThumbForNameOrMime(paramString1, paramString3));
      this.placeholderImageView.setVisibility(0);
      label62:
      if ((paramString4 == null) && (paramInt == 0)) {
        break label165;
      }
      if (paramString4 == null) {
        break label121;
      }
      this.thumbImageView.setImage(paramString4, "40_40", null);
      label90:
      this.thumbImageView.setVisibility(0);
    }
    for (;;)
    {
      return;
      this.extTextView.setVisibility(4);
      break;
      label110:
      this.placeholderImageView.setVisibility(4);
      break label62;
      label121:
      paramString1 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), paramInt);
      Theme.setCombinedDrawableColor(paramString1, Theme.getColor("files_folderIconBackground"), false);
      Theme.setCombinedDrawableColor(paramString1, Theme.getColor("files_folderIcon"), true);
      this.thumbImageView.setImageDrawable(paramString1);
      break label90;
      label165:
      this.thumbImageView.setImageBitmap(null);
      this.thumbImageView.setVisibility(4);
    }
  }
  
  public void updateFileExistIcon()
  {
    Object localObject1;
    Object localObject2;
    if ((this.message != null) && (this.message.messageOwner.media != null))
    {
      localObject1 = null;
      if ((this.message.messageOwner.attachPath != null) && (this.message.messageOwner.attachPath.length() != 0))
      {
        localObject2 = localObject1;
        if (new File(this.message.messageOwner.attachPath).exists()) {}
      }
      else
      {
        localObject2 = localObject1;
        if (!FileLoader.getPathToMessage(this.message.messageOwner).exists()) {
          localObject2 = FileLoader.getAttachFileName(this.message.getDocument());
        }
      }
      this.loaded = false;
      if (localObject2 == null)
      {
        this.statusImageView.setVisibility(4);
        this.progressView.setVisibility(4);
        this.dateTextView.setPadding(0, 0, 0, 0);
        this.loading = false;
        this.loaded = true;
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
      }
    }
    for (;;)
    {
      return;
      DownloadController.getInstance(this.currentAccount).addLoadingFileObserver((String)localObject2, this);
      this.loading = FileLoader.getInstance(this.currentAccount).isLoadingFile((String)localObject2);
      this.statusImageView.setVisibility(0);
      localObject1 = this.statusImageView;
      int i;
      if (this.loading)
      {
        i = NUM;
        label214:
        ((ImageView)localObject1).setImageResource(i);
        localObject1 = this.dateTextView;
        if (!LocaleController.isRTL) {
          break label310;
        }
        i = 0;
        label232:
        if (!LocaleController.isRTL) {
          break label319;
        }
      }
      label310:
      label319:
      for (int j = AndroidUtilities.dp(14.0F);; j = 0)
      {
        ((TextView)localObject1).setPadding(i, 0, j, 0);
        if (!this.loading) {
          break label325;
        }
        this.progressView.setVisibility(0);
        localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject2);
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = Float.valueOf(0.0F);
        }
        this.progressView.setProgress(((Float)localObject2).floatValue(), false);
        break;
        i = NUM;
        break label214;
        i = AndroidUtilities.dp(14.0F);
        break label232;
      }
      label325:
      this.progressView.setVisibility(4);
      continue;
      this.loading = false;
      this.loaded = true;
      this.progressView.setVisibility(4);
      this.progressView.setProgress(0.0F, false);
      this.statusImageView.setVisibility(4);
      this.dateTextView.setPadding(0, 0, 0, 0);
      DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/SharedDocumentCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */