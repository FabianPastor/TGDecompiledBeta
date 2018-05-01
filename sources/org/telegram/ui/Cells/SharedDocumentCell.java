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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
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
    private int[] icons = new int[]{C0446R.drawable.media_doc_blue, C0446R.drawable.media_doc_green, C0446R.drawable.media_doc_red, C0446R.drawable.media_doc_yellow};
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

        public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
            z2 = false;
            SharedDocumentCell.this.extTextView.setVisibility(z ? 4 : 0);
            imageReceiver = SharedDocumentCell.this.placeholderImageView;
            if (z) {
                z2 = true;
            }
            imageReceiver.setVisibility(z2);
        }
    }

    public void onProgressUpload(String str, float f, boolean z) {
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
        r0.checkBox = new CheckBox(context2, C0446R.drawable.round_check2);
        r0.checkBox.setVisibility(4);
        r0.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
        View view = r0.checkBox;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i | 48, LocaleController.isRTL ? 0.0f : 34.0f, 30.0f, LocaleController.isRTL ? 34.0f : 0.0f, 0.0f));
    }

    private int getThumbForNameOrMime(String str, String str2) {
        if (str == null || str.length() == 0) {
            return this.icons[0];
        }
        int i;
        String substring;
        if (!(str.contains(".doc") || str.contains(".txt"))) {
            if (!str.contains(".psd")) {
                if (!str.contains(".xls")) {
                    if (!str.contains(".csv")) {
                        if (!(str.contains(".pdf") || str.contains(".ppt"))) {
                            if (!str.contains(".key")) {
                                if (!(str.contains(".zip") || str.contains(".rar") || str.contains(".ai") || str.contains(DefaultHlsExtractorFactory.MP3_FILE_EXTENSION) || str.contains(".mov"))) {
                                    if (!str.contains(".avi")) {
                                        i = -1;
                                        if (i == -1) {
                                            i = str.lastIndexOf(46);
                                            substring = i == -1 ? TtmlNode.ANONYMOUS_REGION_ID : str.substring(i + 1);
                                            if (substring.length() != 0) {
                                                i = substring.charAt(0) % this.icons.length;
                                            } else {
                                                i = str.charAt(0) % this.icons.length;
                                            }
                                        }
                                        return this.icons[i];
                                    }
                                }
                                i = 3;
                                if (i == -1) {
                                    i = str.lastIndexOf(46);
                                    if (i == -1) {
                                    }
                                    if (substring.length() != 0) {
                                        i = str.charAt(0) % this.icons.length;
                                    } else {
                                        i = substring.charAt(0) % this.icons.length;
                                    }
                                }
                                return this.icons[i];
                            }
                        }
                        i = 2;
                        if (i == -1) {
                            i = str.lastIndexOf(46);
                            if (i == -1) {
                            }
                            if (substring.length() != 0) {
                                i = substring.charAt(0) % this.icons.length;
                            } else {
                                i = str.charAt(0) % this.icons.length;
                            }
                        }
                        return this.icons[i];
                    }
                }
                i = 1;
                if (i == -1) {
                    i = str.lastIndexOf(46);
                    if (i == -1) {
                    }
                    if (substring.length() != 0) {
                        i = str.charAt(0) % this.icons.length;
                    } else {
                        i = substring.charAt(0) % this.icons.length;
                    }
                }
                return this.icons[i];
            }
        }
        i = null;
        if (i == -1) {
            i = str.lastIndexOf(46);
            if (i == -1) {
            }
            if (substring.length() != 0) {
                i = substring.charAt(0) % this.icons.length;
            } else {
                i = str.charAt(0) % this.icons.length;
            }
        }
        return this.icons[i];
    }

    public void setTextAndValueAndTypeAndThumb(String str, String str2, String str3, String str4, int i) {
        this.nameTextView.setText(str);
        this.dateTextView.setText(str2);
        if (str3 != null) {
            this.extTextView.setVisibility(0);
            this.extTextView.setText(str3);
        } else {
            this.extTextView.setVisibility(4);
        }
        if (i == 0) {
            this.placeholderImageView.setImageResource(getThumbForNameOrMime(str, str3));
            this.placeholderImageView.setVisibility(0);
        } else {
            this.placeholderImageView.setVisibility(4);
        }
        if (str4 == null) {
            if (i == 0) {
                this.thumbImageView.setImageBitmap(null);
                this.thumbImageView.setVisibility(4);
                return;
            }
        }
        if (str4 != null) {
            this.thumbImageView.setImage(str4, "40_40", null);
        } else {
            str = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(40.0f), i);
            Theme.setCombinedDrawableColor(str, Theme.getColor(Theme.key_files_folderIconBackground), false);
            Theme.setCombinedDrawableColor(str, Theme.getColor(Theme.key_files_folderIcon), true);
            this.thumbImageView.setImageDrawable(str);
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

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    public void setDocument(MessageObject messageObject, boolean z) {
        this.needDivider = z;
        this.message = messageObject;
        this.loaded = false;
        this.loading = false;
        if (messageObject == null || messageObject.getDocument() == null) {
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.extTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.dateTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.placeholderImageView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.thumbImageView.setVisibility(4);
            this.thumbImageView.setImageBitmap(null);
        } else {
            CharSequence charSequence;
            long j;
            TextView textView;
            Object[] objArr;
            if (messageObject.isMusic()) {
                Document document;
                if (messageObject.type == 0) {
                    document = messageObject.messageOwner.media.webpage.document;
                } else {
                    document = messageObject.messageOwner.media.document;
                }
                charSequence = null;
                for (int i = 0; i < document.attributes.size(); i++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if ((documentAttribute instanceof TL_documentAttributeAudio) && !((documentAttribute.performer == null || documentAttribute.performer.length() == 0) && (documentAttribute.title == null || documentAttribute.title.length() == 0))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(messageObject.getMusicAuthor());
                        stringBuilder.append(" - ");
                        stringBuilder.append(messageObject.getMusicTitle());
                        charSequence = stringBuilder.toString();
                    }
                }
            } else {
                charSequence = null;
            }
            String documentFileName = FileLoader.getDocumentFileName(messageObject.getDocument());
            if (charSequence == null) {
                charSequence = documentFileName;
            }
            this.nameTextView.setText(charSequence);
            this.placeholderImageView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.placeholderImageView.setImageResource(getThumbForNameOrMime(documentFileName, messageObject.getDocument().mime_type));
            TextView textView2 = this.extTextView;
            int lastIndexOf = documentFileName.lastIndexOf(46);
            textView2.setText(lastIndexOf == -1 ? TtmlNode.ANONYMOUS_REGION_ID : documentFileName.substring(lastIndexOf + 1).toLowerCase());
            if (!(messageObject.getDocument().thumb instanceof TL_photoSizeEmpty)) {
                if (messageObject.getDocument().thumb != null) {
                    this.thumbImageView.setVisibility(0);
                    this.thumbImageView.setImage(messageObject.getDocument().thumb.location, "40_40", (Drawable) null);
                    j = ((long) messageObject.messageOwner.date) * 1000;
                    textView = this.dateTextView;
                    objArr = new Object[2];
                    objArr[0] = AndroidUtilities.formatFileSize((long) messageObject.getDocument().size);
                    objArr[1] = LocaleController.formatString("formatDateAtTime", C0446R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(j)), LocaleController.getInstance().formatterDay.format(new Date(j)));
                    textView.setText(String.format("%s, %s", objArr));
                }
            }
            this.thumbImageView.setVisibility(4);
            this.thumbImageView.setImageBitmap(null);
            j = ((long) messageObject.messageOwner.date) * 1000;
            textView = this.dateTextView;
            objArr = new Object[2];
            objArr[0] = AndroidUtilities.formatFileSize((long) messageObject.getDocument().size);
            objArr[1] = LocaleController.formatString("formatDateAtTime", C0446R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(j)), LocaleController.getInstance().formatterDay.format(new Date(j)));
            textView.setText(String.format("%s, %s", objArr));
        }
        setWillNotDraw(this.needDivider ^ 1);
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
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        String str = null;
        if ((this.message.messageOwner.attachPath == null || this.message.messageOwner.attachPath.length() == 0 || !new File(this.message.messageOwner.attachPath).exists()) && !FileLoader.getPathToMessage(this.message.messageOwner).exists()) {
            str = FileLoader.getAttachFileName(this.message.getDocument());
        }
        this.loaded = false;
        if (str == null) {
            this.statusImageView.setVisibility(4);
            this.progressView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            this.loading = false;
            this.loaded = true;
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(str, this);
        this.loading = FileLoader.getInstance(this.currentAccount).isLoadingFile(str);
        this.statusImageView.setVisibility(0);
        this.statusImageView.setImageResource(this.loading ? C0446R.drawable.media_doc_pause : C0446R.drawable.media_doc_load);
        this.dateTextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(14.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : 0, 0);
        if (this.loading) {
            this.progressView.setVisibility(0);
            Float fileProgress = ImageLoader.getInstance().getFileProgress(str);
            if (fileProgress == null) {
                fileProgress = Float.valueOf(0.0f);
            }
            this.progressView.setProgress(fileProgress.floatValue(), false);
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

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f) + this.needDivider, NUM));
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onFailedDownload(String str) {
        updateFileExistIcon();
    }

    public void onSuccessDownload(String str) {
        this.progressView.setProgress(1.0f, true);
        updateFileExistIcon();
    }

    public void onProgressDownload(String str, float f) {
        if (this.progressView.getVisibility() != null) {
            updateFileExistIcon();
        }
        this.progressView.setProgress(f, true);
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
