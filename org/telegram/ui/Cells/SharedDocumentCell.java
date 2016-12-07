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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;

public class SharedDocumentCell extends FrameLayout implements FileDownloadProgressListener {
    private static Paint paint;
    private int TAG;
    private CheckBox checkBox;
    private TextView dateTextView;
    private TextView extTextView;
    private int[] icons = new int[]{R.drawable.media_doc_blue, R.drawable.media_doc_green, R.drawable.media_doc_red, R.drawable.media_doc_yellow};
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    private ImageView placeholderImabeView;
    private LineProgressView progressView;
    private ImageView statusImageView;
    private BackupImageView thumbImageView;

    public SharedDocumentCell(Context context) {
        float f;
        float f2;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.TAG = MediaController.getInstance().generateObserverTag();
        this.placeholderImabeView = new ImageView(context);
        View view = this.placeholderImabeView;
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 0.0f;
        } else {
            f = 12.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 12.0f;
        } else {
            f2 = 0.0f;
        }
        addView(view, LayoutHelper.createFrame(40, 40.0f, i, f, 8.0f, f2, 0.0f));
        this.extTextView = new TextView(context);
        this.extTextView.setTextColor(-1);
        this.extTextView.setTextSize(1, 14.0f);
        this.extTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.extTextView.setLines(1);
        this.extTextView.setMaxLines(1);
        this.extTextView.setSingleLine(true);
        this.extTextView.setGravity(17);
        this.extTextView.setEllipsize(TruncateAt.END);
        addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 22.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        this.thumbImageView = new BackupImageView(context);
        view = this.thumbImageView;
        i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 0.0f;
        } else {
            f = 12.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 12.0f;
        } else {
            f2 = 0.0f;
        }
        addView(view, LayoutHelper.createFrame(40, 40.0f, i, f, 8.0f, f2, 0.0f));
        this.thumbImageView.getImageReceiver().setDelegate(new ImageReceiverDelegate() {
            public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
                int i;
                int i2 = 4;
                TextView access$000 = SharedDocumentCell.this.extTextView;
                if (set) {
                    i = 4;
                } else {
                    i = 0;
                }
                access$000.setVisibility(i);
                ImageView access$100 = SharedDocumentCell.this.placeholderImabeView;
                if (!set) {
                    i2 = 0;
                }
                access$100.setVisibility(i2);
            }
        });
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(-14606047);
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        this.statusImageView = new ImageView(context);
        this.statusImageView.setVisibility(4);
        addView(this.statusImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 35.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        this.dateTextView = new TextView(context);
        this.dateTextView.setTextColor(-6710887);
        this.dateTextView.setTextSize(1, 14.0f);
        this.dateTextView.setLines(1);
        this.dateTextView.setMaxLines(1);
        this.dateTextView.setSingleLine(true);
        this.dateTextView.setEllipsize(TruncateAt.END);
        this.dateTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, BitmapDescriptorFactory.HUE_ORANGE, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        this.progressView = new LineProgressView(context);
        addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 72.0f, 54.0f, LocaleController.isRTL ? 72.0f : 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, R.drawable.round_check2);
        this.checkBox.setVisibility(4);
        view = this.checkBox;
        i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 0.0f;
        } else {
            f = 34.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 34.0f;
        } else {
            f2 = 0.0f;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i, f, BitmapDescriptorFactory.HUE_ORANGE, f2, 0.0f));
    }

    private int getThumbForNameOrMime(String name, String mime) {
        if (name == null || name.length() == 0) {
            return this.icons[0];
        }
        int color = -1;
        if (name.contains(".doc") || name.contains(".txt") || name.contains(".psd")) {
            color = 0;
        } else if (name.contains(".xls") || name.contains(".csv")) {
            color = 1;
        } else if (name.contains(".pdf") || name.contains(".ppt") || name.contains(".key")) {
            color = 2;
        } else if (name.contains(".zip") || name.contains(".rar") || name.contains(".ai") || name.contains(".mp3") || name.contains(".mov") || name.contains(".avi")) {
            color = 3;
        }
        if (color == -1) {
            int idx = name.lastIndexOf(46);
            String ext = idx == -1 ? "" : name.substring(idx + 1);
            if (ext.length() != 0) {
                color = ext.charAt(0) % this.icons.length;
            } else {
                color = name.charAt(0) % this.icons.length;
            }
        }
        return this.icons[color];
    }

    public void setTextAndValueAndTypeAndThumb(String text, String value, String type, String thumb, int resId) {
        this.nameTextView.setText(text);
        this.dateTextView.setText(value);
        if (type != null) {
            this.extTextView.setVisibility(0);
            this.extTextView.setText(type);
        } else {
            this.extTextView.setVisibility(4);
        }
        if (resId == 0) {
            this.placeholderImabeView.setImageResource(getThumbForNameOrMime(text, type));
            this.placeholderImabeView.setVisibility(0);
        } else {
            this.placeholderImabeView.setVisibility(4);
        }
        if (thumb == null && resId == 0) {
            this.thumbImageView.setVisibility(4);
            return;
        }
        if (thumb != null) {
            this.thumbImageView.setImage(thumb, "40_40", null);
        } else {
            this.thumbImageView.setImageResource(resId);
        }
        this.thumbImageView.setVisibility(0);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MediaController.getInstance().removeLoadingFileObserver(this);
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    public void setDocument(MessageObject messageObject, boolean divider) {
        this.needDivider = divider;
        this.message = messageObject;
        this.loaded = false;
        this.loading = false;
        if (messageObject == null || messageObject.getDocument() == null) {
            this.nameTextView.setText("");
            this.extTextView.setText("");
            this.dateTextView.setText("");
            this.placeholderImabeView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.thumbImageView.setVisibility(4);
            this.thumbImageView.setImageBitmap(null);
        } else {
            String name = null;
            if (messageObject.isMusic()) {
                Document document;
                if (messageObject.type == 0) {
                    document = messageObject.messageOwner.media.webpage.document;
                } else {
                    document = messageObject.messageOwner.media.document;
                }
                for (int a = 0; a < document.attributes.size(); a++) {
                    DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                    if ((attribute instanceof TL_documentAttributeAudio) && !((attribute.performer == null || attribute.performer.length() == 0) && (attribute.title == null || attribute.title.length() == 0))) {
                        name = messageObject.getMusicAuthor() + " - " + messageObject.getMusicTitle();
                    }
                }
            }
            String fileName = FileLoader.getDocumentFileName(messageObject.getDocument());
            if (name == null) {
                name = fileName;
            }
            this.nameTextView.setText(name);
            this.placeholderImabeView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.placeholderImabeView.setImageResource(getThumbForNameOrMime(fileName, messageObject.getDocument().mime_type));
            TextView textView = this.extTextView;
            int idx = fileName.lastIndexOf(46);
            textView.setText(idx == -1 ? "" : fileName.substring(idx + 1).toLowerCase());
            if ((messageObject.getDocument().thumb instanceof TL_photoSizeEmpty) || messageObject.getDocument().thumb == null) {
                this.thumbImageView.setVisibility(4);
                this.thumbImageView.setImageBitmap(null);
            } else {
                this.thumbImageView.setVisibility(0);
                this.thumbImageView.setImage(messageObject.getDocument().thumb.location, "40_40", (Drawable) null);
            }
            long date = ((long) messageObject.messageOwner.date) * 1000;
            TextView textView2 = this.dateTextView;
            Object[] objArr = new Object[2];
            objArr[0] = AndroidUtilities.formatFileSize((long) messageObject.getDocument().size);
            objArr[1] = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)));
            textView2.setText(String.format("%s, %s", objArr));
        }
        setWillNotDraw(!this.needDivider);
        this.progressView.setProgress(0.0f, false);
        updateFileExistIcon();
    }

    public void updateFileExistIcon() {
        if (this.message == null || this.message.messageOwner.media == null) {
            this.loading = false;
            this.loaded = true;
            this.progressView.setVisibility(4);
            this.progressView.setProgress(0.0f, false);
            this.statusImageView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            MediaController.getInstance().removeLoadingFileObserver(this);
            return;
        }
        String fileName = null;
        if ((this.message.messageOwner.attachPath == null || this.message.messageOwner.attachPath.length() == 0 || !new File(this.message.messageOwner.attachPath).exists()) && !FileLoader.getPathToMessage(this.message.messageOwner).exists()) {
            fileName = FileLoader.getAttachFileName(this.message.getDocument());
        }
        this.loaded = false;
        if (fileName == null) {
            this.statusImageView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            this.loading = false;
            this.loaded = true;
            MediaController.getInstance().removeLoadingFileObserver(this);
            return;
        }
        MediaController.getInstance().addLoadingFileObserver(fileName, this);
        this.loading = FileLoader.getInstance().isLoadingFile(fileName);
        this.statusImageView.setVisibility(0);
        this.statusImageView.setImageResource(this.loading ? R.drawable.media_doc_pause : R.drawable.media_doc_load);
        this.dateTextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(14.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : 0, 0);
        if (this.loading) {
            this.progressView.setVisibility(0);
            Float progress = ImageLoader.getInstance().getFileProgress(fileName);
            if (progress == null) {
                progress = Float.valueOf(0.0f);
            }
            this.progressView.setProgress(progress.floatValue(), false);
            return;
        }
        this.progressView.setVisibility(4);
    }

    public MessageObject getMessage() {
        return this.message;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isLoading() {
        return this.loading;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(56.0f), NUM));
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    public void onFailedDownload(String name) {
        updateFileExistIcon();
    }

    public void onSuccessDownload(String name) {
        this.progressView.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
        updateFileExistIcon();
    }

    public void onProgressDownload(String fileName, float progress) {
        if (this.progressView.getVisibility() != 0) {
            updateFileExistIcon();
        }
        this.progressView.setProgress(progress, true);
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
