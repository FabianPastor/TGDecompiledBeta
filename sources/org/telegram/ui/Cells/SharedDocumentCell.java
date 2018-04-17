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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;

public class SharedDocumentCell extends FrameLayout implements FileDownloadProgressListener {
    private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    private CheckBox checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private TextView dateTextView;
    private TextView extTextView;
    private int[] icons = new int[]{R.drawable.media_doc_blue, R.drawable.media_doc_green, R.drawable.media_doc_red, R.drawable.media_doc_yellow};
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    private ImageView placeholderImageView;
    private LineProgressView progressView;
    private ImageView statusImageView;
    private BackupImageView thumbImageView;

    /* renamed from: org.telegram.ui.Cells.SharedDocumentCell$1 */
    class C19461 implements ImageReceiverDelegate {
        C19461() {
        }

        public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
            int i = 0;
            SharedDocumentCell.this.extTextView.setVisibility(set ? 4 : 0);
            ImageView access$100 = SharedDocumentCell.this.placeholderImageView;
            if (set) {
                i = 4;
            }
            access$100.setVisibility(i);
        }
    }

    public SharedDocumentCell(Context context) {
        Context context2 = context;
        super(context);
        this.placeholderImageView = new ImageView(context2);
        int i = 3;
        addView(this.placeholderImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        r0.extTextView = new TextView(context2);
        r0.extTextView.setTextColor(Theme.getColor(Theme.key_files_iconText));
        r0.extTextView.setTextSize(1, 14.0f);
        r0.extTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.extTextView.setLines(1);
        r0.extTextView.setMaxLines(1);
        r0.extTextView.setSingleLine(true);
        r0.extTextView.setGravity(17);
        r0.extTextView.setEllipsize(TruncateAt.END);
        addView(r0.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 22.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        r0.thumbImageView = new BackupImageView(context2);
        addView(r0.thumbImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        r0.thumbImageView.getImageReceiver().setDelegate(new C19461());
        r0.nameTextView = new TextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTextSize(1, 16.0f);
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.nameTextView.setLines(1);
        r0.nameTextView.setMaxLines(1);
        r0.nameTextView.setSingleLine(true);
        r0.nameTextView.setEllipsize(TruncateAt.END);
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(r0.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        r0.statusImageView = new ImageView(context2);
        r0.statusImageView.setVisibility(4);
        r0.statusImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sharedMedia_startStopLoadIcon), Mode.MULTIPLY));
        addView(r0.statusImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 35.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        r0.dateTextView = new TextView(context2);
        r0.dateTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        r0.dateTextView.setTextSize(1, 14.0f);
        r0.dateTextView.setLines(1);
        r0.dateTextView.setMaxLines(1);
        r0.dateTextView.setSingleLine(true);
        r0.dateTextView.setEllipsize(TruncateAt.END);
        r0.dateTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(r0.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        r0.progressView = new LineProgressView(context2);
        r0.progressView.setProgressColor(Theme.getColor(Theme.key_sharedMedia_startStopLoadIcon));
        addView(r0.progressView, LayoutHelper.createFrame(-1, 2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 72.0f, 54.0f, LocaleController.isRTL ? 72.0f : 0.0f, 0.0f));
        r0.checkBox = new CheckBox(context2, R.drawable.round_check2);
        r0.checkBox.setVisibility(4);
        r0.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
        View view = r0.checkBox;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i | 48, LocaleController.isRTL ? 0.0f : 34.0f, 30.0f, LocaleController.isRTL ? 34.0f : 0.0f, 0.0f));
    }

    private int getThumbForNameOrMime(String name, String mime) {
        if (name == null || name.length() == 0) {
            return this.icons[0];
        }
        int lastIndexOf;
        String ext;
        int color = -1;
        if (!(name.contains(".doc") || name.contains(".txt"))) {
            if (!name.contains(".psd")) {
                if (!name.contains(".xls")) {
                    if (!name.contains(".csv")) {
                        if (!(name.contains(".pdf") || name.contains(".ppt"))) {
                            if (!name.contains(".key")) {
                                if (name.contains(".zip") || name.contains(".rar") || name.contains(".ai") || name.contains(DefaultHlsExtractorFactory.MP3_FILE_EXTENSION) || name.contains(".mov") || name.contains(".avi")) {
                                    color = 3;
                                }
                                if (color == -1) {
                                    lastIndexOf = name.lastIndexOf(46);
                                    ext = lastIndexOf != -1 ? TtmlNode.ANONYMOUS_REGION_ID : name.substring(lastIndexOf + 1);
                                    if (ext.length() == 0) {
                                        color = ext.charAt(0) % this.icons.length;
                                    } else {
                                        color = name.charAt(0) % this.icons.length;
                                    }
                                }
                                return this.icons[color];
                            }
                        }
                        color = 2;
                        if (color == -1) {
                            lastIndexOf = name.lastIndexOf(46);
                            if (lastIndexOf != -1) {
                            }
                            if (ext.length() == 0) {
                                color = name.charAt(0) % this.icons.length;
                            } else {
                                color = ext.charAt(0) % this.icons.length;
                            }
                        }
                        return this.icons[color];
                    }
                }
                color = 1;
                if (color == -1) {
                    lastIndexOf = name.lastIndexOf(46);
                    if (lastIndexOf != -1) {
                    }
                    if (ext.length() == 0) {
                        color = ext.charAt(0) % this.icons.length;
                    } else {
                        color = name.charAt(0) % this.icons.length;
                    }
                }
                return this.icons[color];
            }
        }
        color = 0;
        if (color == -1) {
            lastIndexOf = name.lastIndexOf(46);
            if (lastIndexOf != -1) {
            }
            if (ext.length() == 0) {
                color = name.charAt(0) % this.icons.length;
            } else {
                color = ext.charAt(0) % this.icons.length;
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
            this.placeholderImageView.setImageResource(getThumbForNameOrMime(text, type));
            this.placeholderImageView.setVisibility(0);
        } else {
            this.placeholderImageView.setVisibility(4);
        }
        if (thumb == null) {
            if (resId == 0) {
                this.thumbImageView.setImageBitmap(null);
                this.thumbImageView.setVisibility(4);
                return;
            }
        }
        if (thumb != null) {
            this.thumbImageView.setImage(thumb, "40_40", null);
        } else {
            Drawable drawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(40.0f), resId);
            Theme.setCombinedDrawableColor(drawable, Theme.getColor(Theme.key_files_folderIconBackground), false);
            Theme.setCombinedDrawableColor(drawable, Theme.getColor(Theme.key_files_folderIcon), true);
            this.thumbImageView.setImageDrawable(drawable);
        }
        this.thumbImageView.setVisibility(0);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.progressView.getVisibility() == 0) {
            updateFileExistIcon();
        }
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    public void setDocument(MessageObject messageObject, boolean divider) {
        MessageObject messageObject2 = messageObject;
        this.needDivider = divider;
        this.message = messageObject2;
        this.loaded = false;
        this.loading = false;
        if (messageObject2 == null || messageObject.getDocument() == null) {
            r0.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            r0.extTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            r0.dateTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            r0.placeholderImageView.setVisibility(0);
            r0.extTextView.setVisibility(0);
            r0.thumbImageView.setVisibility(4);
            r0.thumbImageView.setImageBitmap(null);
        } else {
            long date;
            Object[] objArr;
            String name = null;
            if (messageObject.isMusic()) {
                Document document;
                if (messageObject2.type == 0) {
                    document = messageObject2.messageOwner.media.webpage.document;
                } else {
                    document = messageObject2.messageOwner.media.document;
                }
                String name2 = null;
                for (int a = 0; a < document.attributes.size(); a++) {
                    DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                    if ((attribute instanceof TL_documentAttributeAudio) && !((attribute.performer == null || attribute.performer.length() == 0) && (attribute.title == null || attribute.title.length() == 0))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(messageObject.getMusicAuthor());
                        stringBuilder.append(" - ");
                        stringBuilder.append(messageObject.getMusicTitle());
                        name2 = stringBuilder.toString();
                    }
                }
                name = name2;
            }
            String fileName = FileLoader.getDocumentFileName(messageObject.getDocument());
            if (name == null) {
                name = fileName;
            }
            r0.nameTextView.setText(name);
            r0.placeholderImageView.setVisibility(0);
            r0.extTextView.setVisibility(0);
            r0.placeholderImageView.setImageResource(getThumbForNameOrMime(fileName, messageObject.getDocument().mime_type));
            TextView textView = r0.extTextView;
            int lastIndexOf = fileName.lastIndexOf(46);
            textView.setText(lastIndexOf == -1 ? TtmlNode.ANONYMOUS_REGION_ID : fileName.substring(lastIndexOf + 1).toLowerCase());
            if (!(messageObject.getDocument().thumb instanceof TL_photoSizeEmpty)) {
                if (messageObject.getDocument().thumb != null) {
                    r0.thumbImageView.setVisibility(0);
                    r0.thumbImageView.setImage(messageObject.getDocument().thumb.location, "40_40", (Drawable) null);
                    date = ((long) messageObject2.messageOwner.date) * 1000;
                    textView = r0.dateTextView;
                    objArr = new Object[2];
                    objArr[0] = AndroidUtilities.formatFileSize((long) messageObject.getDocument().size);
                    objArr[1] = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)));
                    textView.setText(String.format("%s, %s", objArr));
                }
            }
            r0.thumbImageView.setVisibility(4);
            r0.thumbImageView.setImageBitmap(null);
            date = ((long) messageObject2.messageOwner.date) * 1000;
            textView = r0.dateTextView;
            objArr = new Object[2];
            objArr[0] = AndroidUtilities.formatFileSize((long) messageObject.getDocument().size);
            objArr[1] = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)));
            textView.setText(String.format("%s, %s", objArr));
        }
        setWillNotDraw(r0.needDivider ^ true);
        r0.progressView.setProgress(0.0f, false);
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
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        String fileName = null;
        if ((this.message.messageOwner.attachPath == null || this.message.messageOwner.attachPath.length() == 0 || !new File(this.message.messageOwner.attachPath).exists()) && !FileLoader.getPathToMessage(this.message.messageOwner).exists()) {
            fileName = FileLoader.getAttachFileName(this.message.getDocument());
        }
        this.loaded = false;
        if (fileName == null) {
            this.statusImageView.setVisibility(4);
            this.progressView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            this.loading = false;
            this.loaded = true;
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        } else {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            this.loading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
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
            } else {
                this.progressView.setVisibility(4);
            }
        }
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
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f) + this.needDivider, NUM));
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onFailedDownload(String name) {
        updateFileExistIcon();
    }

    public void onSuccessDownload(String name) {
        this.progressView.setProgress(1.0f, true);
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
