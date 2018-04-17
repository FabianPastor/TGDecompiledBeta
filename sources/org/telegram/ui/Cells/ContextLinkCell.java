package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.PhotoViewer;

public class ContextLinkCell extends View implements FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    private boolean buttonPressed;
    private int buttonState;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(27.0f);
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable = new LetterDrawable();
    private ImageReceiver linkImageView = new ImageReceiver(this);
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private RadialProgress radialProgress = new RadialProgress(this);
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(7.0f);

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public ContextLinkCell(Context context) {
        super(context);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Throwable e;
        char c;
        char c2;
        int width;
        CharSequence linkFinal;
        StaticLayout staticLayout;
        ArrayList<PhotoSize> photoThumbs;
        Throwable e2;
        String ext;
        int viewWidth;
        int w;
        int h;
        DocumentAttribute attribute;
        int[] result;
        int dp;
        String currentPhotoFilterThumb;
        String currentPhotoFilter;
        StringBuilder stringBuilder;
        String currentPhotoFilter2;
        int x;
        int y;
        float f;
        int x2;
        double lat;
        Object[] objArr;
        double lon;
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.linkLayout = null;
        this.currentPhotoObject = null;
        this.linkY = AndroidUtilities.dp(27.0f);
        if (this.inlineResult == null && r1.documentAttach == null) {
            setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
            return;
        }
        boolean z;
        int viewWidth2 = MeasureSpec.getSize(widthMeasureSpec);
        int maxWidth = (viewWidth2 - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(8.0f);
        PhotoSize currentPhotoObjectThumb = null;
        ArrayList<PhotoSize> photoThumbs2 = null;
        TLObject tLObject = null;
        String urlLocation = null;
        if (r1.documentAttach != null) {
            photoThumbs2 = new ArrayList();
            photoThumbs2.add(r1.documentAttach.thumb);
        } else if (!(r1.inlineResult == null || r1.inlineResult.photo == null)) {
            photoThumbs2 = new ArrayList(r1.inlineResult.photo.sizes);
        }
        ArrayList<PhotoSize> photoThumbs3 = photoThumbs2;
        float f2;
        if (r1.mediaWebpage || r1.inlineResult == null) {
            z = true;
            f2 = 4.0f;
        } else {
            if (r1.inlineResult.title != null) {
                try {
                    r1.titleLayout = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(r1.inlineResult.title.replace('\n', ' '), Theme.chat_contextResult_titleTextPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), Theme.chat_contextResult_titleTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_titleTextPaint.measureText(r1.inlineResult.title)), maxWidth), TruncateAt.END), Theme.chat_contextResult_titleTextPaint, maxWidth + AndroidUtilities.dp(4.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                } catch (Throwable e3) {
                    FileLog.m3e(e3);
                }
                r1.letterDrawable.setTitle(r1.inlineResult.title);
            }
            if (r1.inlineResult.description != null) {
                try {
                    c = '\n';
                    c2 = ' ';
                    z = true;
                    f2 = 4.0f;
                    try {
                        r1.descriptionLayout = ChatMessageCell.generateStaticLayout(Emoji.replaceEmoji(r1.inlineResult.description, Theme.chat_contextResult_descriptionTextPaint.getFontMetricsInt(), AndroidUtilities.dp(13.0f), false), Theme.chat_contextResult_descriptionTextPaint, maxWidth, maxWidth, 0, 3);
                        if (r1.descriptionLayout.getLineCount() > 0) {
                            r1.linkY = (r1.descriptionY + r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - z)) + AndroidUtilities.dp(1.0f);
                        }
                    } catch (Exception e4) {
                        e3 = e4;
                        FileLog.m3e(e3);
                        if (r1.inlineResult.url != null) {
                            try {
                                width = (int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(r1.inlineResult.url));
                                linkFinal = TextUtils.ellipsize(r1.inlineResult.url.replace(c, c2), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min(width, maxWidth), TruncateAt.MIDDLE);
                                staticLayout = staticLayout;
                                photoThumbs = photoThumbs3;
                                try {
                                    r1.linkLayout = new StaticLayout(linkFinal, Theme.chat_contextResult_descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                } catch (Throwable e32) {
                                    e2 = e32;
                                    FileLog.m3e(e2);
                                    ext = null;
                                    if (r1.documentAttach != null) {
                                        r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), z);
                                        currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
                                        if (currentPhotoObjectThumb == r1.currentPhotoObject) {
                                            currentPhotoObjectThumb = null;
                                        }
                                    } else if (!MessageObject.isGifDocument(r1.documentAttach)) {
                                        r1.currentPhotoObject = r1.documentAttach.thumb;
                                    } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                                        r1.currentPhotoObject = r1.documentAttach.thumb;
                                        ext = "webp";
                                    } else {
                                        r1.currentPhotoObject = r1.documentAttach.thumb;
                                    }
                                    if (r1.inlineResult != null) {
                                        if (r1.inlineResult.type.startsWith("gif")) {
                                            if (r1.documentAttachType != 2) {
                                                tLObject = (TL_webDocument) r1.inlineResult.content;
                                                r1.documentAttachType = 2;
                                            }
                                        } else if (r1.inlineResult.type.equals("photo")) {
                                            tLObject = r1.inlineResult.thumb instanceof TL_webDocument ? (TL_webDocument) r1.inlineResult.content : (TL_webDocument) r1.inlineResult.thumb;
                                        }
                                        tLObject = r1.inlineResult.thumb;
                                        if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue)) {
                                            if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo)) {
                                                viewWidth = viewWidth2;
                                                w = 0;
                                                h = 0;
                                                if (r1.documentAttach != null) {
                                                    viewWidth2 = 0;
                                                    while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                                        attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                                        if (attribute instanceof TL_documentAttributeImageSize) {
                                                            if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                                viewWidth2++;
                                                            }
                                                        }
                                                        w = attribute.f36w;
                                                        h = attribute.f35h;
                                                    }
                                                }
                                                if (r1.currentPhotoObject == null) {
                                                    if (currentPhotoObjectThumb != null) {
                                                        currentPhotoObjectThumb.size = -1;
                                                    }
                                                    w = r1.currentPhotoObject.f43w;
                                                    h = r1.currentPhotoObject.f42h;
                                                } else if (r1.inlineResult != null) {
                                                    result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                                    w = result[0];
                                                    h = result[1];
                                                }
                                                dp = AndroidUtilities.dp(80.0f);
                                                h = dp;
                                                w = dp;
                                                currentPhotoFilterThumb = "52_52_b";
                                                if (r1.mediaWebpage) {
                                                    viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                                    if (r1.documentAttachType != 2) {
                                                        currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(currentPhotoFilter);
                                                        stringBuilder.append("_b");
                                                        currentPhotoFilterThumb = stringBuilder.toString();
                                                        currentPhotoFilter2 = currentPhotoFilter;
                                                    } else {
                                                        currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                        currentPhotoFilterThumb = currentPhotoFilter;
                                                        currentPhotoFilter2 = currentPhotoFilter;
                                                    }
                                                } else {
                                                    currentPhotoFilter2 = "52_52";
                                                }
                                                r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                                if (r1.documentAttachType != 2) {
                                                    if (r1.currentPhotoObject == null) {
                                                        r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                                    } else {
                                                        r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                                    }
                                                } else if (r1.documentAttach == null) {
                                                    r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                                } else {
                                                    r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                                }
                                                r1.drawLinkImageView = true;
                                                if (r1.mediaWebpage) {
                                                    viewWidth2 = viewWidth;
                                                    dp = MeasureSpec.getSize(heightMeasureSpec);
                                                    if (dp == 0) {
                                                        dp = AndroidUtilities.dp(100.0f);
                                                    }
                                                    setMeasuredDimension(viewWidth2, dp);
                                                    x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                                    y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                                    r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                                    r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                                } else {
                                                    viewWidth2 = 0;
                                                    viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                                    viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                                    viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                                    dp = AndroidUtilities.dp(52.0f);
                                                    if (LocaleController.isRTL) {
                                                        f = 8.0f;
                                                        x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                                    } else {
                                                        f = 8.0f;
                                                        x2 = AndroidUtilities.dp(8.0f);
                                                    }
                                                    r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                                    r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                                    r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                                }
                                            }
                                        }
                                        lat = r1.inlineResult.send_message.geo.lat;
                                        viewWidth = viewWidth2;
                                        objArr = new Object[5];
                                        lon = r1.inlineResult.send_message.geo._long;
                                        objArr[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                                        objArr[3] = Double.valueOf(lat);
                                        objArr[4] = Double.valueOf(lon);
                                        urlLocation = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
                                        w = 0;
                                        h = 0;
                                        if (r1.documentAttach != null) {
                                            viewWidth2 = 0;
                                            while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                                attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                                if (attribute instanceof TL_documentAttributeImageSize) {
                                                    if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                        viewWidth2++;
                                                    }
                                                }
                                                w = attribute.f36w;
                                                h = attribute.f35h;
                                            }
                                        }
                                        if (r1.currentPhotoObject == null) {
                                            if (currentPhotoObjectThumb != null) {
                                                currentPhotoObjectThumb.size = -1;
                                            }
                                            w = r1.currentPhotoObject.f43w;
                                            h = r1.currentPhotoObject.f42h;
                                        } else if (r1.inlineResult != null) {
                                            result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                            w = result[0];
                                            h = result[1];
                                        }
                                        dp = AndroidUtilities.dp(80.0f);
                                        h = dp;
                                        w = dp;
                                        currentPhotoFilterThumb = "52_52_b";
                                        if (r1.mediaWebpage) {
                                            viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                            if (r1.documentAttachType != 2) {
                                                currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                currentPhotoFilterThumb = currentPhotoFilter;
                                                currentPhotoFilter2 = currentPhotoFilter;
                                            } else {
                                                currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(currentPhotoFilter);
                                                stringBuilder.append("_b");
                                                currentPhotoFilterThumb = stringBuilder.toString();
                                                currentPhotoFilter2 = currentPhotoFilter;
                                            }
                                        } else {
                                            currentPhotoFilter2 = "52_52";
                                        }
                                        if (r1.documentAttachType == 6) {
                                        }
                                        r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                        if (r1.documentAttachType != 2) {
                                            if (r1.documentAttach == null) {
                                                if (r1.currentPhotoObject == null) {
                                                }
                                                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                            } else {
                                                if (r1.currentPhotoObject == null) {
                                                }
                                                r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                            }
                                        } else if (r1.currentPhotoObject == null) {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                        } else {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                        }
                                        r1.drawLinkImageView = true;
                                        if (r1.mediaWebpage) {
                                            viewWidth2 = viewWidth;
                                            dp = MeasureSpec.getSize(heightMeasureSpec);
                                            if (dp == 0) {
                                                dp = AndroidUtilities.dp(100.0f);
                                            }
                                            setMeasuredDimension(viewWidth2, dp);
                                            x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                            y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                            r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                            r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                        } else {
                                            viewWidth2 = 0;
                                            viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                            viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                            viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                            dp = AndroidUtilities.dp(52.0f);
                                            if (LocaleController.isRTL) {
                                                f = 8.0f;
                                                x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                            } else {
                                                f = 8.0f;
                                                x2 = AndroidUtilities.dp(8.0f);
                                            }
                                            r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                            r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                        }
                                    }
                                    viewWidth = viewWidth2;
                                    w = 0;
                                    h = 0;
                                    if (r1.documentAttach != null) {
                                        viewWidth2 = 0;
                                        while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                            attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                            if (attribute instanceof TL_documentAttributeImageSize) {
                                                if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                    viewWidth2++;
                                                }
                                            }
                                            w = attribute.f36w;
                                            h = attribute.f35h;
                                        }
                                    }
                                    if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb != null) {
                                            currentPhotoObjectThumb.size = -1;
                                        }
                                        w = r1.currentPhotoObject.f43w;
                                        h = r1.currentPhotoObject.f42h;
                                    } else if (r1.inlineResult != null) {
                                        result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                        w = result[0];
                                        h = result[1];
                                    }
                                    dp = AndroidUtilities.dp(80.0f);
                                    h = dp;
                                    w = dp;
                                    currentPhotoFilterThumb = "52_52_b";
                                    if (r1.mediaWebpage) {
                                        currentPhotoFilter2 = "52_52";
                                    } else {
                                        viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                        if (r1.documentAttachType != 2) {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(currentPhotoFilter);
                                            stringBuilder.append("_b");
                                            currentPhotoFilterThumb = stringBuilder.toString();
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        } else {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            currentPhotoFilterThumb = currentPhotoFilter;
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        }
                                    }
                                    if (r1.documentAttachType == 6) {
                                    }
                                    r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                    if (r1.documentAttachType != 2) {
                                        if (r1.currentPhotoObject == null) {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                        } else {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                        }
                                    } else if (r1.documentAttach == null) {
                                        if (r1.currentPhotoObject == null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                    } else {
                                        if (r1.currentPhotoObject == null) {
                                        }
                                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                    }
                                    r1.drawLinkImageView = true;
                                    if (r1.mediaWebpage) {
                                        viewWidth2 = 0;
                                        viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                        viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                        viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                        dp = AndroidUtilities.dp(52.0f);
                                        if (LocaleController.isRTL) {
                                            f = 8.0f;
                                            x2 = AndroidUtilities.dp(8.0f);
                                        } else {
                                            f = 8.0f;
                                            x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                        }
                                        r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                        r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                    } else {
                                        viewWidth2 = viewWidth;
                                        dp = MeasureSpec.getSize(heightMeasureSpec);
                                        if (dp == 0) {
                                            dp = AndroidUtilities.dp(100.0f);
                                        }
                                        setMeasuredDimension(viewWidth2, dp);
                                        x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                        y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                        r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                        r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                    }
                                }
                            } catch (Throwable e322) {
                                photoThumbs = photoThumbs3;
                                e2 = e322;
                                FileLog.m3e(e2);
                                ext = null;
                                if (r1.documentAttach != null) {
                                    r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), z);
                                    currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
                                    if (currentPhotoObjectThumb == r1.currentPhotoObject) {
                                        currentPhotoObjectThumb = null;
                                    }
                                } else if (!MessageObject.isGifDocument(r1.documentAttach)) {
                                    r1.currentPhotoObject = r1.documentAttach.thumb;
                                } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                                    r1.currentPhotoObject = r1.documentAttach.thumb;
                                    ext = "webp";
                                } else {
                                    r1.currentPhotoObject = r1.documentAttach.thumb;
                                }
                                if (r1.inlineResult != null) {
                                    if (r1.inlineResult.type.startsWith("gif")) {
                                        if (r1.documentAttachType != 2) {
                                            tLObject = (TL_webDocument) r1.inlineResult.content;
                                            r1.documentAttachType = 2;
                                        }
                                    } else if (r1.inlineResult.type.equals("photo")) {
                                        if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                        }
                                    }
                                    tLObject = r1.inlineResult.thumb;
                                    if (r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) {
                                        if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo)) {
                                            viewWidth = viewWidth2;
                                            w = 0;
                                            h = 0;
                                            if (r1.documentAttach != null) {
                                                viewWidth2 = 0;
                                                while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                                    attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                                        if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                            viewWidth2++;
                                                        }
                                                    }
                                                    w = attribute.f36w;
                                                    h = attribute.f35h;
                                                }
                                            }
                                            if (r1.currentPhotoObject == null) {
                                                if (currentPhotoObjectThumb != null) {
                                                    currentPhotoObjectThumb.size = -1;
                                                }
                                                w = r1.currentPhotoObject.f43w;
                                                h = r1.currentPhotoObject.f42h;
                                            } else if (r1.inlineResult != null) {
                                                result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                                w = result[0];
                                                h = result[1];
                                            }
                                            dp = AndroidUtilities.dp(80.0f);
                                            h = dp;
                                            w = dp;
                                            currentPhotoFilterThumb = "52_52_b";
                                            if (r1.mediaWebpage) {
                                                viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                                if (r1.documentAttachType != 2) {
                                                    currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                    currentPhotoFilterThumb = currentPhotoFilter;
                                                    currentPhotoFilter2 = currentPhotoFilter;
                                                } else {
                                                    currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(currentPhotoFilter);
                                                    stringBuilder.append("_b");
                                                    currentPhotoFilterThumb = stringBuilder.toString();
                                                    currentPhotoFilter2 = currentPhotoFilter;
                                                }
                                            } else {
                                                currentPhotoFilter2 = "52_52";
                                            }
                                            if (r1.documentAttachType == 6) {
                                            }
                                            r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                            if (r1.documentAttachType != 2) {
                                                if (r1.documentAttach == null) {
                                                    if (r1.currentPhotoObject == null) {
                                                    }
                                                    r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                                } else {
                                                    if (r1.currentPhotoObject == null) {
                                                    }
                                                    r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                                }
                                            } else if (r1.currentPhotoObject == null) {
                                                if (currentPhotoObjectThumb == null) {
                                                }
                                                r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                            } else {
                                                if (currentPhotoObjectThumb == null) {
                                                }
                                                r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                            }
                                            r1.drawLinkImageView = true;
                                            if (r1.mediaWebpage) {
                                                viewWidth2 = viewWidth;
                                                dp = MeasureSpec.getSize(heightMeasureSpec);
                                                if (dp == 0) {
                                                    dp = AndroidUtilities.dp(100.0f);
                                                }
                                                setMeasuredDimension(viewWidth2, dp);
                                                x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                                y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                                r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                                r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                            } else {
                                                viewWidth2 = 0;
                                                viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                                viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                                viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                                dp = AndroidUtilities.dp(52.0f);
                                                if (LocaleController.isRTL) {
                                                    f = 8.0f;
                                                    x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                                } else {
                                                    f = 8.0f;
                                                    x2 = AndroidUtilities.dp(8.0f);
                                                }
                                                r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                                r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                                r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                            }
                                        }
                                    }
                                    lat = r1.inlineResult.send_message.geo.lat;
                                    viewWidth = viewWidth2;
                                    objArr = new Object[5];
                                    lon = r1.inlineResult.send_message.geo._long;
                                    objArr[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                                    objArr[3] = Double.valueOf(lat);
                                    objArr[4] = Double.valueOf(lon);
                                    urlLocation = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
                                    w = 0;
                                    h = 0;
                                    if (r1.documentAttach != null) {
                                        viewWidth2 = 0;
                                        while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                            attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                            if (attribute instanceof TL_documentAttributeImageSize) {
                                                if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                    viewWidth2++;
                                                }
                                            }
                                            w = attribute.f36w;
                                            h = attribute.f35h;
                                        }
                                    }
                                    if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb != null) {
                                            currentPhotoObjectThumb.size = -1;
                                        }
                                        w = r1.currentPhotoObject.f43w;
                                        h = r1.currentPhotoObject.f42h;
                                    } else if (r1.inlineResult != null) {
                                        result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                        w = result[0];
                                        h = result[1];
                                    }
                                    dp = AndroidUtilities.dp(80.0f);
                                    h = dp;
                                    w = dp;
                                    currentPhotoFilterThumb = "52_52_b";
                                    if (r1.mediaWebpage) {
                                        currentPhotoFilter2 = "52_52";
                                    } else {
                                        viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                        if (r1.documentAttachType != 2) {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(currentPhotoFilter);
                                            stringBuilder.append("_b");
                                            currentPhotoFilterThumb = stringBuilder.toString();
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        } else {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            currentPhotoFilterThumb = currentPhotoFilter;
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        }
                                    }
                                    if (r1.documentAttachType == 6) {
                                    }
                                    r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                    if (r1.documentAttachType != 2) {
                                        if (r1.currentPhotoObject == null) {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                        } else {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                        }
                                    } else if (r1.documentAttach == null) {
                                        if (r1.currentPhotoObject == null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                    } else {
                                        if (r1.currentPhotoObject == null) {
                                        }
                                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                    }
                                    r1.drawLinkImageView = true;
                                    if (r1.mediaWebpage) {
                                        viewWidth2 = 0;
                                        viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                        viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                        viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                        dp = AndroidUtilities.dp(52.0f);
                                        if (LocaleController.isRTL) {
                                            f = 8.0f;
                                            x2 = AndroidUtilities.dp(8.0f);
                                        } else {
                                            f = 8.0f;
                                            x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                        }
                                        r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                        r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                    } else {
                                        viewWidth2 = viewWidth;
                                        dp = MeasureSpec.getSize(heightMeasureSpec);
                                        if (dp == 0) {
                                            dp = AndroidUtilities.dp(100.0f);
                                        }
                                        setMeasuredDimension(viewWidth2, dp);
                                        x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                        y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                        r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                        r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                    }
                                }
                                viewWidth = viewWidth2;
                                w = 0;
                                h = 0;
                                if (r1.documentAttach != null) {
                                    viewWidth2 = 0;
                                    while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                        attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                        if (attribute instanceof TL_documentAttributeImageSize) {
                                            if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                viewWidth2++;
                                            }
                                        }
                                        w = attribute.f36w;
                                        h = attribute.f35h;
                                    }
                                }
                                if (r1.currentPhotoObject == null) {
                                    if (currentPhotoObjectThumb != null) {
                                        currentPhotoObjectThumb.size = -1;
                                    }
                                    w = r1.currentPhotoObject.f43w;
                                    h = r1.currentPhotoObject.f42h;
                                } else if (r1.inlineResult != null) {
                                    result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                    w = result[0];
                                    h = result[1];
                                }
                                dp = AndroidUtilities.dp(80.0f);
                                h = dp;
                                w = dp;
                                currentPhotoFilterThumb = "52_52_b";
                                if (r1.mediaWebpage) {
                                    viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                    if (r1.documentAttachType != 2) {
                                        currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                        currentPhotoFilterThumb = currentPhotoFilter;
                                        currentPhotoFilter2 = currentPhotoFilter;
                                    } else {
                                        currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(currentPhotoFilter);
                                        stringBuilder.append("_b");
                                        currentPhotoFilterThumb = stringBuilder.toString();
                                        currentPhotoFilter2 = currentPhotoFilter;
                                    }
                                } else {
                                    currentPhotoFilter2 = "52_52";
                                }
                                if (r1.documentAttachType == 6) {
                                }
                                r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                if (r1.documentAttachType != 2) {
                                    if (r1.documentAttach == null) {
                                        if (r1.currentPhotoObject == null) {
                                        }
                                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                    } else {
                                        if (r1.currentPhotoObject == null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                    }
                                } else if (r1.currentPhotoObject == null) {
                                    if (currentPhotoObjectThumb == null) {
                                    }
                                    r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                } else {
                                    if (currentPhotoObjectThumb == null) {
                                    }
                                    r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                }
                                r1.drawLinkImageView = true;
                                if (r1.mediaWebpage) {
                                    viewWidth2 = viewWidth;
                                    dp = MeasureSpec.getSize(heightMeasureSpec);
                                    if (dp == 0) {
                                        dp = AndroidUtilities.dp(100.0f);
                                    }
                                    setMeasuredDimension(viewWidth2, dp);
                                    x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                    y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                    r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                    r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                } else {
                                    viewWidth2 = 0;
                                    viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                    viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                    viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                    dp = AndroidUtilities.dp(52.0f);
                                    if (LocaleController.isRTL) {
                                        f = 8.0f;
                                        x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                    } else {
                                        f = 8.0f;
                                        x2 = AndroidUtilities.dp(8.0f);
                                    }
                                    r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                    r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                    r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                }
                            }
                            ext = null;
                            if (r1.documentAttach != null) {
                                r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), z);
                                currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
                                if (currentPhotoObjectThumb == r1.currentPhotoObject) {
                                    currentPhotoObjectThumb = null;
                                }
                            } else if (!MessageObject.isGifDocument(r1.documentAttach)) {
                                r1.currentPhotoObject = r1.documentAttach.thumb;
                            } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                                r1.currentPhotoObject = r1.documentAttach.thumb;
                                ext = "webp";
                            } else {
                                r1.currentPhotoObject = r1.documentAttach.thumb;
                            }
                            if (r1.inlineResult != null) {
                                if (r1.inlineResult.type.startsWith("gif")) {
                                    if (r1.documentAttachType != 2) {
                                        tLObject = (TL_webDocument) r1.inlineResult.content;
                                        r1.documentAttachType = 2;
                                    }
                                } else if (r1.inlineResult.type.equals("photo")) {
                                    if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                    }
                                }
                                tLObject = r1.inlineResult.thumb;
                                if (r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) {
                                    if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo)) {
                                        viewWidth = viewWidth2;
                                        w = 0;
                                        h = 0;
                                        if (r1.documentAttach != null) {
                                            viewWidth2 = 0;
                                            while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                                attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                                if (attribute instanceof TL_documentAttributeImageSize) {
                                                    if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                        viewWidth2++;
                                                    }
                                                }
                                                w = attribute.f36w;
                                                h = attribute.f35h;
                                            }
                                        }
                                        if (r1.currentPhotoObject == null) {
                                            if (currentPhotoObjectThumb != null) {
                                                currentPhotoObjectThumb.size = -1;
                                            }
                                            w = r1.currentPhotoObject.f43w;
                                            h = r1.currentPhotoObject.f42h;
                                        } else if (r1.inlineResult != null) {
                                            result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                            w = result[0];
                                            h = result[1];
                                        }
                                        dp = AndroidUtilities.dp(80.0f);
                                        h = dp;
                                        w = dp;
                                        currentPhotoFilterThumb = "52_52_b";
                                        if (r1.mediaWebpage) {
                                            viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                            if (r1.documentAttachType != 2) {
                                                currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                currentPhotoFilterThumb = currentPhotoFilter;
                                                currentPhotoFilter2 = currentPhotoFilter;
                                            } else {
                                                currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(currentPhotoFilter);
                                                stringBuilder.append("_b");
                                                currentPhotoFilterThumb = stringBuilder.toString();
                                                currentPhotoFilter2 = currentPhotoFilter;
                                            }
                                        } else {
                                            currentPhotoFilter2 = "52_52";
                                        }
                                        if (r1.documentAttachType == 6) {
                                        }
                                        r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                        if (r1.documentAttachType != 2) {
                                            if (r1.documentAttach == null) {
                                                if (r1.currentPhotoObject == null) {
                                                }
                                                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                            } else {
                                                if (r1.currentPhotoObject == null) {
                                                }
                                                r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                            }
                                        } else if (r1.currentPhotoObject == null) {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                        } else {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                        }
                                        r1.drawLinkImageView = true;
                                        if (r1.mediaWebpage) {
                                            viewWidth2 = viewWidth;
                                            dp = MeasureSpec.getSize(heightMeasureSpec);
                                            if (dp == 0) {
                                                dp = AndroidUtilities.dp(100.0f);
                                            }
                                            setMeasuredDimension(viewWidth2, dp);
                                            x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                            y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                            r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                            r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                        } else {
                                            viewWidth2 = 0;
                                            viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                            viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                            viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                            dp = AndroidUtilities.dp(52.0f);
                                            if (LocaleController.isRTL) {
                                                f = 8.0f;
                                                x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                            } else {
                                                f = 8.0f;
                                                x2 = AndroidUtilities.dp(8.0f);
                                            }
                                            r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                            r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                        }
                                    }
                                }
                                lat = r1.inlineResult.send_message.geo.lat;
                                viewWidth = viewWidth2;
                                objArr = new Object[5];
                                lon = r1.inlineResult.send_message.geo._long;
                                objArr[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                                objArr[3] = Double.valueOf(lat);
                                objArr[4] = Double.valueOf(lon);
                                urlLocation = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
                                w = 0;
                                h = 0;
                                if (r1.documentAttach != null) {
                                    viewWidth2 = 0;
                                    while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                        attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                        if (attribute instanceof TL_documentAttributeImageSize) {
                                            if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                viewWidth2++;
                                            }
                                        }
                                        w = attribute.f36w;
                                        h = attribute.f35h;
                                    }
                                }
                                if (r1.currentPhotoObject == null) {
                                    if (currentPhotoObjectThumb != null) {
                                        currentPhotoObjectThumb.size = -1;
                                    }
                                    w = r1.currentPhotoObject.f43w;
                                    h = r1.currentPhotoObject.f42h;
                                } else if (r1.inlineResult != null) {
                                    result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                    w = result[0];
                                    h = result[1];
                                }
                                dp = AndroidUtilities.dp(80.0f);
                                h = dp;
                                w = dp;
                                currentPhotoFilterThumb = "52_52_b";
                                if (r1.mediaWebpage) {
                                    currentPhotoFilter2 = "52_52";
                                } else {
                                    viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                    if (r1.documentAttachType != 2) {
                                        currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(currentPhotoFilter);
                                        stringBuilder.append("_b");
                                        currentPhotoFilterThumb = stringBuilder.toString();
                                        currentPhotoFilter2 = currentPhotoFilter;
                                    } else {
                                        currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                        currentPhotoFilterThumb = currentPhotoFilter;
                                        currentPhotoFilter2 = currentPhotoFilter;
                                    }
                                }
                                if (r1.documentAttachType == 6) {
                                }
                                r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                if (r1.documentAttachType != 2) {
                                    if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb == null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                    } else {
                                        if (currentPhotoObjectThumb == null) {
                                        }
                                        r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                    }
                                } else if (r1.documentAttach == null) {
                                    if (r1.currentPhotoObject == null) {
                                    }
                                    r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                } else {
                                    if (r1.currentPhotoObject == null) {
                                    }
                                    r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                }
                                r1.drawLinkImageView = true;
                                if (r1.mediaWebpage) {
                                    viewWidth2 = 0;
                                    viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                    viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                    viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                    dp = AndroidUtilities.dp(52.0f);
                                    if (LocaleController.isRTL) {
                                        f = 8.0f;
                                        x2 = AndroidUtilities.dp(8.0f);
                                    } else {
                                        f = 8.0f;
                                        x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                    }
                                    r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                    r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                    r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                } else {
                                    viewWidth2 = viewWidth;
                                    dp = MeasureSpec.getSize(heightMeasureSpec);
                                    if (dp == 0) {
                                        dp = AndroidUtilities.dp(100.0f);
                                    }
                                    setMeasuredDimension(viewWidth2, dp);
                                    x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                    y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                    r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                    r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                }
                            }
                            viewWidth = viewWidth2;
                            w = 0;
                            h = 0;
                            if (r1.documentAttach != null) {
                                viewWidth2 = 0;
                                while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                    attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                        if (!(attribute instanceof TL_documentAttributeVideo)) {
                                            viewWidth2++;
                                        }
                                    }
                                    w = attribute.f36w;
                                    h = attribute.f35h;
                                }
                            }
                            if (r1.currentPhotoObject == null) {
                                if (currentPhotoObjectThumb != null) {
                                    currentPhotoObjectThumb.size = -1;
                                }
                                w = r1.currentPhotoObject.f43w;
                                h = r1.currentPhotoObject.f42h;
                            } else if (r1.inlineResult != null) {
                                result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                w = result[0];
                                h = result[1];
                            }
                            dp = AndroidUtilities.dp(80.0f);
                            h = dp;
                            w = dp;
                            currentPhotoFilterThumb = "52_52_b";
                            if (r1.mediaWebpage) {
                                viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                if (r1.documentAttachType != 2) {
                                    currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                    currentPhotoFilterThumb = currentPhotoFilter;
                                    currentPhotoFilter2 = currentPhotoFilter;
                                } else {
                                    currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(currentPhotoFilter);
                                    stringBuilder.append("_b");
                                    currentPhotoFilterThumb = stringBuilder.toString();
                                    currentPhotoFilter2 = currentPhotoFilter;
                                }
                            } else {
                                currentPhotoFilter2 = "52_52";
                            }
                            if (r1.documentAttachType == 6) {
                            }
                            r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                            if (r1.documentAttachType != 2) {
                                if (r1.documentAttach == null) {
                                    if (r1.currentPhotoObject == null) {
                                    }
                                    r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                } else {
                                    if (r1.currentPhotoObject == null) {
                                    }
                                    r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                }
                            } else if (r1.currentPhotoObject == null) {
                                if (currentPhotoObjectThumb == null) {
                                }
                                r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                            } else {
                                if (currentPhotoObjectThumb == null) {
                                }
                                r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                            }
                            r1.drawLinkImageView = true;
                            if (r1.mediaWebpage) {
                                viewWidth2 = viewWidth;
                                dp = MeasureSpec.getSize(heightMeasureSpec);
                                if (dp == 0) {
                                    dp = AndroidUtilities.dp(100.0f);
                                }
                                setMeasuredDimension(viewWidth2, dp);
                                x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                            } else {
                                viewWidth2 = 0;
                                viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                dp = AndroidUtilities.dp(52.0f);
                                if (LocaleController.isRTL) {
                                    f = 8.0f;
                                    x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                } else {
                                    f = 8.0f;
                                    x2 = AndroidUtilities.dp(8.0f);
                                }
                                r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                            }
                        }
                        photoThumbs = photoThumbs3;
                        ext = null;
                        if (r1.documentAttach != null) {
                            r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), z);
                            currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
                            if (currentPhotoObjectThumb == r1.currentPhotoObject) {
                                currentPhotoObjectThumb = null;
                            }
                        } else if (!MessageObject.isGifDocument(r1.documentAttach)) {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                        } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                        } else {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                            ext = "webp";
                        }
                        if (r1.inlineResult != null) {
                            if (r1.inlineResult.type.startsWith("gif")) {
                                if (r1.inlineResult.type.equals("photo")) {
                                    if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                    }
                                }
                            } else if (r1.documentAttachType != 2) {
                                tLObject = (TL_webDocument) r1.inlineResult.content;
                                r1.documentAttachType = 2;
                            }
                            tLObject = r1.inlineResult.thumb;
                            if (r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) {
                                if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo)) {
                                    viewWidth = viewWidth2;
                                    w = 0;
                                    h = 0;
                                    if (r1.documentAttach != null) {
                                        viewWidth2 = 0;
                                        while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                            attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                            if (attribute instanceof TL_documentAttributeImageSize) {
                                                if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                    viewWidth2++;
                                                }
                                            }
                                            w = attribute.f36w;
                                            h = attribute.f35h;
                                        }
                                    }
                                    if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb != null) {
                                            currentPhotoObjectThumb.size = -1;
                                        }
                                        w = r1.currentPhotoObject.f43w;
                                        h = r1.currentPhotoObject.f42h;
                                    } else if (r1.inlineResult != null) {
                                        result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                        w = result[0];
                                        h = result[1];
                                    }
                                    dp = AndroidUtilities.dp(80.0f);
                                    h = dp;
                                    w = dp;
                                    currentPhotoFilterThumb = "52_52_b";
                                    if (r1.mediaWebpage) {
                                        currentPhotoFilter2 = "52_52";
                                    } else {
                                        viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                        if (r1.documentAttachType != 2) {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(currentPhotoFilter);
                                            stringBuilder.append("_b");
                                            currentPhotoFilterThumb = stringBuilder.toString();
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        } else {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            currentPhotoFilterThumb = currentPhotoFilter;
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        }
                                    }
                                    if (r1.documentAttachType == 6) {
                                    }
                                    r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                    if (r1.documentAttachType != 2) {
                                        if (r1.currentPhotoObject == null) {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                        } else {
                                            if (currentPhotoObjectThumb == null) {
                                            }
                                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                        }
                                    } else if (r1.documentAttach == null) {
                                        if (r1.currentPhotoObject == null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                    } else {
                                        if (r1.currentPhotoObject == null) {
                                        }
                                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                    }
                                    r1.drawLinkImageView = true;
                                    if (r1.mediaWebpage) {
                                        viewWidth2 = 0;
                                        viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                        viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                        viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                        dp = AndroidUtilities.dp(52.0f);
                                        if (LocaleController.isRTL) {
                                            f = 8.0f;
                                            x2 = AndroidUtilities.dp(8.0f);
                                        } else {
                                            f = 8.0f;
                                            x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                        }
                                        r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                        r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                    } else {
                                        viewWidth2 = viewWidth;
                                        dp = MeasureSpec.getSize(heightMeasureSpec);
                                        if (dp == 0) {
                                            dp = AndroidUtilities.dp(100.0f);
                                        }
                                        setMeasuredDimension(viewWidth2, dp);
                                        x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                        y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                        r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                        r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                    }
                                }
                            }
                            lat = r1.inlineResult.send_message.geo.lat;
                            viewWidth = viewWidth2;
                            objArr = new Object[5];
                            lon = r1.inlineResult.send_message.geo._long;
                            objArr[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                            objArr[3] = Double.valueOf(lat);
                            objArr[4] = Double.valueOf(lon);
                            urlLocation = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
                            w = 0;
                            h = 0;
                            if (r1.documentAttach != null) {
                                viewWidth2 = 0;
                                while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                    attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                        if (!(attribute instanceof TL_documentAttributeVideo)) {
                                            viewWidth2++;
                                        }
                                    }
                                    w = attribute.f36w;
                                    h = attribute.f35h;
                                }
                            }
                            if (r1.currentPhotoObject == null) {
                                if (currentPhotoObjectThumb != null) {
                                    currentPhotoObjectThumb.size = -1;
                                }
                                w = r1.currentPhotoObject.f43w;
                                h = r1.currentPhotoObject.f42h;
                            } else if (r1.inlineResult != null) {
                                result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                w = result[0];
                                h = result[1];
                            }
                            dp = AndroidUtilities.dp(80.0f);
                            h = dp;
                            w = dp;
                            currentPhotoFilterThumb = "52_52_b";
                            if (r1.mediaWebpage) {
                                viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                if (r1.documentAttachType != 2) {
                                    currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                    currentPhotoFilterThumb = currentPhotoFilter;
                                    currentPhotoFilter2 = currentPhotoFilter;
                                } else {
                                    currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(currentPhotoFilter);
                                    stringBuilder.append("_b");
                                    currentPhotoFilterThumb = stringBuilder.toString();
                                    currentPhotoFilter2 = currentPhotoFilter;
                                }
                            } else {
                                currentPhotoFilter2 = "52_52";
                            }
                            if (r1.documentAttachType == 6) {
                            }
                            r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                            if (r1.documentAttachType != 2) {
                                if (r1.documentAttach == null) {
                                    if (r1.currentPhotoObject == null) {
                                    }
                                    r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                } else {
                                    if (r1.currentPhotoObject == null) {
                                    }
                                    r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                }
                            } else if (r1.currentPhotoObject == null) {
                                if (currentPhotoObjectThumb == null) {
                                }
                                r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                            } else {
                                if (currentPhotoObjectThumb == null) {
                                }
                                r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                            }
                            r1.drawLinkImageView = true;
                            if (r1.mediaWebpage) {
                                viewWidth2 = viewWidth;
                                dp = MeasureSpec.getSize(heightMeasureSpec);
                                if (dp == 0) {
                                    dp = AndroidUtilities.dp(100.0f);
                                }
                                setMeasuredDimension(viewWidth2, dp);
                                x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                            } else {
                                viewWidth2 = 0;
                                viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                dp = AndroidUtilities.dp(52.0f);
                                if (LocaleController.isRTL) {
                                    f = 8.0f;
                                    x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                } else {
                                    f = 8.0f;
                                    x2 = AndroidUtilities.dp(8.0f);
                                }
                                r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                            }
                        }
                        viewWidth = viewWidth2;
                        w = 0;
                        h = 0;
                        if (r1.documentAttach != null) {
                            viewWidth2 = 0;
                            while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                if (attribute instanceof TL_documentAttributeImageSize) {
                                    if (!(attribute instanceof TL_documentAttributeVideo)) {
                                        viewWidth2++;
                                    }
                                }
                                w = attribute.f36w;
                                h = attribute.f35h;
                            }
                        }
                        if (r1.currentPhotoObject == null) {
                            if (currentPhotoObjectThumb != null) {
                                currentPhotoObjectThumb.size = -1;
                            }
                            w = r1.currentPhotoObject.f43w;
                            h = r1.currentPhotoObject.f42h;
                        } else if (r1.inlineResult != null) {
                            result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                            w = result[0];
                            h = result[1];
                        }
                        dp = AndroidUtilities.dp(80.0f);
                        h = dp;
                        w = dp;
                        currentPhotoFilterThumb = "52_52_b";
                        if (r1.mediaWebpage) {
                            currentPhotoFilter2 = "52_52";
                        } else {
                            viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                            if (r1.documentAttachType != 2) {
                                currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(currentPhotoFilter);
                                stringBuilder.append("_b");
                                currentPhotoFilterThumb = stringBuilder.toString();
                                currentPhotoFilter2 = currentPhotoFilter;
                            } else {
                                currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                currentPhotoFilterThumb = currentPhotoFilter;
                                currentPhotoFilter2 = currentPhotoFilter;
                            }
                        }
                        if (r1.documentAttachType == 6) {
                        }
                        r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                        if (r1.documentAttachType != 2) {
                            if (r1.currentPhotoObject == null) {
                                if (currentPhotoObjectThumb == null) {
                                }
                                r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                            } else {
                                if (currentPhotoObjectThumb == null) {
                                }
                                r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                            }
                        } else if (r1.documentAttach == null) {
                            if (r1.currentPhotoObject == null) {
                            }
                            r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                        } else {
                            if (r1.currentPhotoObject == null) {
                            }
                            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                        }
                        r1.drawLinkImageView = true;
                        if (r1.mediaWebpage) {
                            viewWidth2 = 0;
                            viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                            viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                            viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                            dp = AndroidUtilities.dp(52.0f);
                            if (LocaleController.isRTL) {
                                f = 8.0f;
                                x2 = AndroidUtilities.dp(8.0f);
                            } else {
                                f = 8.0f;
                                x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                            }
                            r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                            r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                        } else {
                            viewWidth2 = viewWidth;
                            dp = MeasureSpec.getSize(heightMeasureSpec);
                            if (dp == 0) {
                                dp = AndroidUtilities.dp(100.0f);
                            }
                            setMeasuredDimension(viewWidth2, dp);
                            x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                            y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                            r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                            r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                        }
                    }
                } catch (Exception e5) {
                    e322 = e5;
                    z = true;
                    f2 = 4.0f;
                    c2 = ' ';
                    c = '\n';
                    FileLog.m3e(e322);
                    if (r1.inlineResult.url != null) {
                        width = (int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(r1.inlineResult.url));
                        linkFinal = TextUtils.ellipsize(r1.inlineResult.url.replace(c, c2), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min(width, maxWidth), TruncateAt.MIDDLE);
                        staticLayout = staticLayout;
                        photoThumbs = photoThumbs3;
                        r1.linkLayout = new StaticLayout(linkFinal, Theme.chat_contextResult_descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        ext = null;
                        if (r1.documentAttach != null) {
                            r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), z);
                            currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
                            if (currentPhotoObjectThumb == r1.currentPhotoObject) {
                                currentPhotoObjectThumb = null;
                            }
                        } else if (!MessageObject.isGifDocument(r1.documentAttach)) {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                        } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                            ext = "webp";
                        } else {
                            r1.currentPhotoObject = r1.documentAttach.thumb;
                        }
                        if (r1.inlineResult != null) {
                            if (r1.inlineResult.type.startsWith("gif")) {
                                if (r1.documentAttachType != 2) {
                                    tLObject = (TL_webDocument) r1.inlineResult.content;
                                    r1.documentAttachType = 2;
                                }
                            } else if (r1.inlineResult.type.equals("photo")) {
                                if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                }
                            }
                            tLObject = r1.inlineResult.thumb;
                            if (r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) {
                                if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo)) {
                                    viewWidth = viewWidth2;
                                    w = 0;
                                    h = 0;
                                    if (r1.documentAttach != null) {
                                        viewWidth2 = 0;
                                        while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                            attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                            if (attribute instanceof TL_documentAttributeImageSize) {
                                                if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                    viewWidth2++;
                                                }
                                            }
                                            w = attribute.f36w;
                                            h = attribute.f35h;
                                        }
                                    }
                                    if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb != null) {
                                            currentPhotoObjectThumb.size = -1;
                                        }
                                        w = r1.currentPhotoObject.f43w;
                                        h = r1.currentPhotoObject.f42h;
                                    } else if (r1.inlineResult != null) {
                                        result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                        w = result[0];
                                        h = result[1];
                                    }
                                    dp = AndroidUtilities.dp(80.0f);
                                    h = dp;
                                    w = dp;
                                    currentPhotoFilterThumb = "52_52_b";
                                    if (r1.mediaWebpage) {
                                        viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                        if (r1.documentAttachType != 2) {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            currentPhotoFilterThumb = currentPhotoFilter;
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        } else {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(currentPhotoFilter);
                                            stringBuilder.append("_b");
                                            currentPhotoFilterThumb = stringBuilder.toString();
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        }
                                    } else {
                                        currentPhotoFilter2 = "52_52";
                                    }
                                    if (r1.documentAttachType == 6) {
                                    }
                                    r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                    if (r1.documentAttachType != 2) {
                                        if (r1.documentAttach == null) {
                                            if (r1.currentPhotoObject == null) {
                                            }
                                            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                        } else {
                                            if (r1.currentPhotoObject == null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                        }
                                    } else if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb == null) {
                                        }
                                        r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                    } else {
                                        if (currentPhotoObjectThumb == null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                    }
                                    r1.drawLinkImageView = true;
                                    if (r1.mediaWebpage) {
                                        viewWidth2 = viewWidth;
                                        dp = MeasureSpec.getSize(heightMeasureSpec);
                                        if (dp == 0) {
                                            dp = AndroidUtilities.dp(100.0f);
                                        }
                                        setMeasuredDimension(viewWidth2, dp);
                                        x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                        y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                        r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                        r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                    } else {
                                        viewWidth2 = 0;
                                        viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                        viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                        viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                        dp = AndroidUtilities.dp(52.0f);
                                        if (LocaleController.isRTL) {
                                            f = 8.0f;
                                            x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                        } else {
                                            f = 8.0f;
                                            x2 = AndroidUtilities.dp(8.0f);
                                        }
                                        r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                        r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                    }
                                }
                            }
                            lat = r1.inlineResult.send_message.geo.lat;
                            viewWidth = viewWidth2;
                            objArr = new Object[5];
                            lon = r1.inlineResult.send_message.geo._long;
                            objArr[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                            objArr[3] = Double.valueOf(lat);
                            objArr[4] = Double.valueOf(lon);
                            urlLocation = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
                            w = 0;
                            h = 0;
                            if (r1.documentAttach != null) {
                                viewWidth2 = 0;
                                while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                    attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                        if (!(attribute instanceof TL_documentAttributeVideo)) {
                                            viewWidth2++;
                                        }
                                    }
                                    w = attribute.f36w;
                                    h = attribute.f35h;
                                }
                            }
                            if (r1.currentPhotoObject == null) {
                                if (currentPhotoObjectThumb != null) {
                                    currentPhotoObjectThumb.size = -1;
                                }
                                w = r1.currentPhotoObject.f43w;
                                h = r1.currentPhotoObject.f42h;
                            } else if (r1.inlineResult != null) {
                                result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                w = result[0];
                                h = result[1];
                            }
                            dp = AndroidUtilities.dp(80.0f);
                            h = dp;
                            w = dp;
                            currentPhotoFilterThumb = "52_52_b";
                            if (r1.mediaWebpage) {
                                currentPhotoFilter2 = "52_52";
                            } else {
                                viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                if (r1.documentAttachType != 2) {
                                    currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(currentPhotoFilter);
                                    stringBuilder.append("_b");
                                    currentPhotoFilterThumb = stringBuilder.toString();
                                    currentPhotoFilter2 = currentPhotoFilter;
                                } else {
                                    currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                    currentPhotoFilterThumb = currentPhotoFilter;
                                    currentPhotoFilter2 = currentPhotoFilter;
                                }
                            }
                            if (r1.documentAttachType == 6) {
                            }
                            r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                            if (r1.documentAttachType != 2) {
                                if (r1.currentPhotoObject == null) {
                                    if (currentPhotoObjectThumb == null) {
                                    }
                                    r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                } else {
                                    if (currentPhotoObjectThumb == null) {
                                    }
                                    r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                }
                            } else if (r1.documentAttach == null) {
                                if (r1.currentPhotoObject == null) {
                                }
                                r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                            } else {
                                if (r1.currentPhotoObject == null) {
                                }
                                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                            }
                            r1.drawLinkImageView = true;
                            if (r1.mediaWebpage) {
                                viewWidth2 = 0;
                                viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                dp = AndroidUtilities.dp(52.0f);
                                if (LocaleController.isRTL) {
                                    f = 8.0f;
                                    x2 = AndroidUtilities.dp(8.0f);
                                } else {
                                    f = 8.0f;
                                    x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                }
                                r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                            } else {
                                viewWidth2 = viewWidth;
                                dp = MeasureSpec.getSize(heightMeasureSpec);
                                if (dp == 0) {
                                    dp = AndroidUtilities.dp(100.0f);
                                }
                                setMeasuredDimension(viewWidth2, dp);
                                x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                            }
                        }
                        viewWidth = viewWidth2;
                        w = 0;
                        h = 0;
                        if (r1.documentAttach != null) {
                            viewWidth2 = 0;
                            while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                if (attribute instanceof TL_documentAttributeImageSize) {
                                    if (!(attribute instanceof TL_documentAttributeVideo)) {
                                        viewWidth2++;
                                    }
                                }
                                w = attribute.f36w;
                                h = attribute.f35h;
                            }
                        }
                        if (r1.currentPhotoObject == null) {
                            if (currentPhotoObjectThumb != null) {
                                currentPhotoObjectThumb.size = -1;
                            }
                            w = r1.currentPhotoObject.f43w;
                            h = r1.currentPhotoObject.f42h;
                        } else if (r1.inlineResult != null) {
                            result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                            w = result[0];
                            h = result[1];
                        }
                        dp = AndroidUtilities.dp(80.0f);
                        h = dp;
                        w = dp;
                        currentPhotoFilterThumb = "52_52_b";
                        if (r1.mediaWebpage) {
                            viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                            if (r1.documentAttachType != 2) {
                                currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                currentPhotoFilterThumb = currentPhotoFilter;
                                currentPhotoFilter2 = currentPhotoFilter;
                            } else {
                                currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(currentPhotoFilter);
                                stringBuilder.append("_b");
                                currentPhotoFilterThumb = stringBuilder.toString();
                                currentPhotoFilter2 = currentPhotoFilter;
                            }
                        } else {
                            currentPhotoFilter2 = "52_52";
                        }
                        if (r1.documentAttachType == 6) {
                        }
                        r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                        if (r1.documentAttachType != 2) {
                            if (r1.documentAttach == null) {
                                if (r1.currentPhotoObject == null) {
                                }
                                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                            } else {
                                if (r1.currentPhotoObject == null) {
                                }
                                r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                            }
                        } else if (r1.currentPhotoObject == null) {
                            if (currentPhotoObjectThumb == null) {
                            }
                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                        } else {
                            if (currentPhotoObjectThumb == null) {
                            }
                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                        }
                        r1.drawLinkImageView = true;
                        if (r1.mediaWebpage) {
                            viewWidth2 = viewWidth;
                            dp = MeasureSpec.getSize(heightMeasureSpec);
                            if (dp == 0) {
                                dp = AndroidUtilities.dp(100.0f);
                            }
                            setMeasuredDimension(viewWidth2, dp);
                            x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                            y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                            r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                            r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                        } else {
                            viewWidth2 = 0;
                            viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                            viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                            viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                            dp = AndroidUtilities.dp(52.0f);
                            if (LocaleController.isRTL) {
                                f = 8.0f;
                                x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                            } else {
                                f = 8.0f;
                                x2 = AndroidUtilities.dp(8.0f);
                            }
                            r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                            r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                        }
                    }
                    photoThumbs = photoThumbs3;
                    ext = null;
                    if (r1.documentAttach != null) {
                        r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), z);
                        currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
                        if (currentPhotoObjectThumb == r1.currentPhotoObject) {
                            currentPhotoObjectThumb = null;
                        }
                    } else if (!MessageObject.isGifDocument(r1.documentAttach)) {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                    } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                    } else {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                        ext = "webp";
                    }
                    if (r1.inlineResult != null) {
                        if (r1.inlineResult.type.startsWith("gif")) {
                            if (r1.inlineResult.type.equals("photo")) {
                                if (r1.inlineResult.thumb instanceof TL_webDocument) {
                                }
                            }
                        } else if (r1.documentAttachType != 2) {
                            tLObject = (TL_webDocument) r1.inlineResult.content;
                            r1.documentAttachType = 2;
                        }
                        tLObject = r1.inlineResult.thumb;
                        if (r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) {
                            if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo)) {
                                viewWidth = viewWidth2;
                                w = 0;
                                h = 0;
                                if (r1.documentAttach != null) {
                                    viewWidth2 = 0;
                                    while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                        attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                        if (attribute instanceof TL_documentAttributeImageSize) {
                                            if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                viewWidth2++;
                                            }
                                        }
                                        w = attribute.f36w;
                                        h = attribute.f35h;
                                    }
                                }
                                if (r1.currentPhotoObject == null) {
                                    if (currentPhotoObjectThumb != null) {
                                        currentPhotoObjectThumb.size = -1;
                                    }
                                    w = r1.currentPhotoObject.f43w;
                                    h = r1.currentPhotoObject.f42h;
                                } else if (r1.inlineResult != null) {
                                    result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                    w = result[0];
                                    h = result[1];
                                }
                                dp = AndroidUtilities.dp(80.0f);
                                h = dp;
                                w = dp;
                                currentPhotoFilterThumb = "52_52_b";
                                if (r1.mediaWebpage) {
                                    currentPhotoFilter2 = "52_52";
                                } else {
                                    viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                    if (r1.documentAttachType != 2) {
                                        currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(currentPhotoFilter);
                                        stringBuilder.append("_b");
                                        currentPhotoFilterThumb = stringBuilder.toString();
                                        currentPhotoFilter2 = currentPhotoFilter;
                                    } else {
                                        currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                        currentPhotoFilterThumb = currentPhotoFilter;
                                        currentPhotoFilter2 = currentPhotoFilter;
                                    }
                                }
                                if (r1.documentAttachType == 6) {
                                }
                                r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                if (r1.documentAttachType != 2) {
                                    if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb == null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                    } else {
                                        if (currentPhotoObjectThumb == null) {
                                        }
                                        r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                    }
                                } else if (r1.documentAttach == null) {
                                    if (r1.currentPhotoObject == null) {
                                    }
                                    r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                } else {
                                    if (r1.currentPhotoObject == null) {
                                    }
                                    r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                }
                                r1.drawLinkImageView = true;
                                if (r1.mediaWebpage) {
                                    viewWidth2 = 0;
                                    viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                    viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                    viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                    dp = AndroidUtilities.dp(52.0f);
                                    if (LocaleController.isRTL) {
                                        f = 8.0f;
                                        x2 = AndroidUtilities.dp(8.0f);
                                    } else {
                                        f = 8.0f;
                                        x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                    }
                                    r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                    r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                    r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                } else {
                                    viewWidth2 = viewWidth;
                                    dp = MeasureSpec.getSize(heightMeasureSpec);
                                    if (dp == 0) {
                                        dp = AndroidUtilities.dp(100.0f);
                                    }
                                    setMeasuredDimension(viewWidth2, dp);
                                    x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                    y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                    r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                    r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                }
                            }
                        }
                        lat = r1.inlineResult.send_message.geo.lat;
                        viewWidth = viewWidth2;
                        objArr = new Object[5];
                        lon = r1.inlineResult.send_message.geo._long;
                        objArr[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                        objArr[3] = Double.valueOf(lat);
                        objArr[4] = Double.valueOf(lon);
                        urlLocation = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
                        w = 0;
                        h = 0;
                        if (r1.documentAttach != null) {
                            viewWidth2 = 0;
                            while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                if (attribute instanceof TL_documentAttributeImageSize) {
                                    if (!(attribute instanceof TL_documentAttributeVideo)) {
                                        viewWidth2++;
                                    }
                                }
                                w = attribute.f36w;
                                h = attribute.f35h;
                            }
                        }
                        if (r1.currentPhotoObject == null) {
                            if (currentPhotoObjectThumb != null) {
                                currentPhotoObjectThumb.size = -1;
                            }
                            w = r1.currentPhotoObject.f43w;
                            h = r1.currentPhotoObject.f42h;
                        } else if (r1.inlineResult != null) {
                            result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                            w = result[0];
                            h = result[1];
                        }
                        dp = AndroidUtilities.dp(80.0f);
                        h = dp;
                        w = dp;
                        currentPhotoFilterThumb = "52_52_b";
                        if (r1.mediaWebpage) {
                            viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                            if (r1.documentAttachType != 2) {
                                currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                currentPhotoFilterThumb = currentPhotoFilter;
                                currentPhotoFilter2 = currentPhotoFilter;
                            } else {
                                currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(currentPhotoFilter);
                                stringBuilder.append("_b");
                                currentPhotoFilterThumb = stringBuilder.toString();
                                currentPhotoFilter2 = currentPhotoFilter;
                            }
                        } else {
                            currentPhotoFilter2 = "52_52";
                        }
                        if (r1.documentAttachType == 6) {
                        }
                        r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                        if (r1.documentAttachType != 2) {
                            if (r1.documentAttach == null) {
                                if (r1.currentPhotoObject == null) {
                                }
                                r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                            } else {
                                if (r1.currentPhotoObject == null) {
                                }
                                r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                            }
                        } else if (r1.currentPhotoObject == null) {
                            if (currentPhotoObjectThumb == null) {
                            }
                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                        } else {
                            if (currentPhotoObjectThumb == null) {
                            }
                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                        }
                        r1.drawLinkImageView = true;
                        if (r1.mediaWebpage) {
                            viewWidth2 = viewWidth;
                            dp = MeasureSpec.getSize(heightMeasureSpec);
                            if (dp == 0) {
                                dp = AndroidUtilities.dp(100.0f);
                            }
                            setMeasuredDimension(viewWidth2, dp);
                            x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                            y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                            r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                            r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                        } else {
                            viewWidth2 = 0;
                            viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                            viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                            viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                            dp = AndroidUtilities.dp(52.0f);
                            if (LocaleController.isRTL) {
                                f = 8.0f;
                                x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                            } else {
                                f = 8.0f;
                                x2 = AndroidUtilities.dp(8.0f);
                            }
                            r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                            r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                        }
                    }
                    viewWidth = viewWidth2;
                    w = 0;
                    h = 0;
                    if (r1.documentAttach != null) {
                        viewWidth2 = 0;
                        while (viewWidth2 < r1.documentAttach.attributes.size()) {
                            attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                            if (attribute instanceof TL_documentAttributeImageSize) {
                                if (!(attribute instanceof TL_documentAttributeVideo)) {
                                    viewWidth2++;
                                }
                            }
                            w = attribute.f36w;
                            h = attribute.f35h;
                        }
                    }
                    if (r1.currentPhotoObject == null) {
                        if (currentPhotoObjectThumb != null) {
                            currentPhotoObjectThumb.size = -1;
                        }
                        w = r1.currentPhotoObject.f43w;
                        h = r1.currentPhotoObject.f42h;
                    } else if (r1.inlineResult != null) {
                        result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                        w = result[0];
                        h = result[1];
                    }
                    dp = AndroidUtilities.dp(80.0f);
                    h = dp;
                    w = dp;
                    currentPhotoFilterThumb = "52_52_b";
                    if (r1.mediaWebpage) {
                        currentPhotoFilter2 = "52_52";
                    } else {
                        viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                        if (r1.documentAttachType != 2) {
                            currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(currentPhotoFilter);
                            stringBuilder.append("_b");
                            currentPhotoFilterThumb = stringBuilder.toString();
                            currentPhotoFilter2 = currentPhotoFilter;
                        } else {
                            currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                            currentPhotoFilterThumb = currentPhotoFilter;
                            currentPhotoFilter2 = currentPhotoFilter;
                        }
                    }
                    if (r1.documentAttachType == 6) {
                    }
                    r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                    if (r1.documentAttachType != 2) {
                        if (r1.currentPhotoObject == null) {
                            if (currentPhotoObjectThumb == null) {
                            }
                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                        } else {
                            if (currentPhotoObjectThumb == null) {
                            }
                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                        }
                    } else if (r1.documentAttach == null) {
                        if (r1.currentPhotoObject == null) {
                        }
                        r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                    } else {
                        if (r1.currentPhotoObject == null) {
                        }
                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                    }
                    r1.drawLinkImageView = true;
                    if (r1.mediaWebpage) {
                        viewWidth2 = 0;
                        viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                        viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                        viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                        dp = AndroidUtilities.dp(52.0f);
                        if (LocaleController.isRTL) {
                            f = 8.0f;
                            x2 = AndroidUtilities.dp(8.0f);
                        } else {
                            f = 8.0f;
                            x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                        }
                        r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                        r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                    } else {
                        viewWidth2 = viewWidth;
                        dp = MeasureSpec.getSize(heightMeasureSpec);
                        if (dp == 0) {
                            dp = AndroidUtilities.dp(100.0f);
                        }
                        setMeasuredDimension(viewWidth2, dp);
                        x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                        y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                        r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                        r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                    }
                }
            }
            z = true;
            f2 = 4.0f;
            c2 = ' ';
            c = '\n';
            if (r1.inlineResult.url != null) {
                width = (int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(r1.inlineResult.url));
                linkFinal = TextUtils.ellipsize(r1.inlineResult.url.replace(c, c2), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min(width, maxWidth), TruncateAt.MIDDLE);
                staticLayout = staticLayout;
                photoThumbs = photoThumbs3;
                r1.linkLayout = new StaticLayout(linkFinal, Theme.chat_contextResult_descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                ext = null;
                if (r1.documentAttach != null) {
                    if (!MessageObject.isGifDocument(r1.documentAttach)) {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                    } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                        ext = "webp";
                    } else if (!(r1.documentAttachType == 5 || r1.documentAttachType == 3)) {
                        r1.currentPhotoObject = r1.documentAttach.thumb;
                    }
                } else if (!(r1.inlineResult == null || r1.inlineResult.photo == null)) {
                    r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), z);
                    currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
                    if (currentPhotoObjectThumb == r1.currentPhotoObject) {
                        currentPhotoObjectThumb = null;
                    }
                }
                if (r1.inlineResult != null) {
                    if ((r1.inlineResult.content instanceof TL_webDocument) && r1.inlineResult.type != null) {
                        if (r1.inlineResult.type.startsWith("gif")) {
                            if (r1.documentAttachType != 2) {
                                tLObject = (TL_webDocument) r1.inlineResult.content;
                                r1.documentAttachType = 2;
                            }
                        } else if (r1.inlineResult.type.equals("photo")) {
                            if (r1.inlineResult.thumb instanceof TL_webDocument) {
                            }
                        }
                    }
                    if (tLObject == null && (r1.inlineResult.thumb instanceof TL_webDocument)) {
                        tLObject = r1.inlineResult.thumb;
                    }
                    if (tLObject == null && r1.currentPhotoObject == null && currentPhotoObjectThumb == null) {
                        if (r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) {
                            if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo)) {
                                viewWidth = viewWidth2;
                                w = 0;
                                h = 0;
                                if (r1.documentAttach != null) {
                                    viewWidth2 = 0;
                                    while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                        attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                        if (attribute instanceof TL_documentAttributeImageSize) {
                                            if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                viewWidth2++;
                                            }
                                        }
                                        w = attribute.f36w;
                                        h = attribute.f35h;
                                    }
                                }
                                if (w == 0 || h == 0) {
                                    if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb != null) {
                                            currentPhotoObjectThumb.size = -1;
                                        }
                                        w = r1.currentPhotoObject.f43w;
                                        h = r1.currentPhotoObject.f42h;
                                    } else if (r1.inlineResult != null) {
                                        result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                                        w = result[0];
                                        h = result[1];
                                    }
                                }
                                if (w == 0 || h == 0) {
                                    dp = AndroidUtilities.dp(80.0f);
                                    h = dp;
                                    w = dp;
                                }
                                if (!(r1.documentAttach == null && r1.currentPhotoObject == null && tLObject == null && urlLocation == null)) {
                                    currentPhotoFilterThumb = "52_52_b";
                                    if (r1.mediaWebpage) {
                                        viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                                        if (r1.documentAttachType != 2) {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            currentPhotoFilterThumb = currentPhotoFilter;
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        } else {
                                            currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(currentPhotoFilter);
                                            stringBuilder.append("_b");
                                            currentPhotoFilterThumb = stringBuilder.toString();
                                            currentPhotoFilter2 = currentPhotoFilter;
                                        }
                                    } else {
                                        currentPhotoFilter2 = "52_52";
                                    }
                                    if (r1.documentAttachType == 6) {
                                    }
                                    r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                                    if (r1.documentAttachType != 2) {
                                        if (r1.documentAttach == null) {
                                            if (r1.currentPhotoObject == null) {
                                            }
                                            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                                        } else {
                                            if (r1.currentPhotoObject == null) {
                                            }
                                            r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                                        }
                                    } else if (r1.currentPhotoObject == null) {
                                        if (currentPhotoObjectThumb == null) {
                                        }
                                        r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                                    } else {
                                        if (currentPhotoObjectThumb == null) {
                                        }
                                        r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                                    }
                                    r1.drawLinkImageView = true;
                                }
                                if (r1.mediaWebpage) {
                                    viewWidth2 = viewWidth;
                                    dp = MeasureSpec.getSize(heightMeasureSpec);
                                    if (dp == 0) {
                                        dp = AndroidUtilities.dp(100.0f);
                                    }
                                    setMeasuredDimension(viewWidth2, dp);
                                    x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                                    y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                                    r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                                    r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                                } else {
                                    viewWidth2 = 0;
                                    if (!(r1.titleLayout == null || r1.titleLayout.getLineCount() == 0)) {
                                        viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                                    }
                                    if (!(r1.descriptionLayout == null || r1.descriptionLayout.getLineCount() == 0)) {
                                        viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                                    }
                                    if (r1.linkLayout != null && r1.linkLayout.getLineCount() > 0) {
                                        viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                                    }
                                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                                    dp = AndroidUtilities.dp(52.0f);
                                    if (LocaleController.isRTL) {
                                        f = 8.0f;
                                        x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                                    } else {
                                        f = 8.0f;
                                        x2 = AndroidUtilities.dp(8.0f);
                                    }
                                    r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                                    r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                                    if (r1.documentAttachType == 3 || r1.documentAttachType == 5) {
                                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                                    }
                                }
                            }
                        }
                        lat = r1.inlineResult.send_message.geo.lat;
                        viewWidth = viewWidth2;
                        objArr = new Object[5];
                        lon = r1.inlineResult.send_message.geo._long;
                        objArr[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                        objArr[3] = Double.valueOf(lat);
                        objArr[4] = Double.valueOf(lon);
                        urlLocation = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
                        w = 0;
                        h = 0;
                        if (r1.documentAttach != null) {
                            viewWidth2 = 0;
                            while (viewWidth2 < r1.documentAttach.attributes.size()) {
                                attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                                if (attribute instanceof TL_documentAttributeImageSize) {
                                    if (!(attribute instanceof TL_documentAttributeVideo)) {
                                        viewWidth2++;
                                    }
                                }
                                w = attribute.f36w;
                                h = attribute.f35h;
                            }
                        }
                        if (r1.currentPhotoObject == null) {
                            if (currentPhotoObjectThumb != null) {
                                currentPhotoObjectThumb.size = -1;
                            }
                            w = r1.currentPhotoObject.f43w;
                            h = r1.currentPhotoObject.f42h;
                        } else if (r1.inlineResult != null) {
                            result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                            w = result[0];
                            h = result[1];
                        }
                        dp = AndroidUtilities.dp(80.0f);
                        h = dp;
                        w = dp;
                        currentPhotoFilterThumb = "52_52_b";
                        if (r1.mediaWebpage) {
                            currentPhotoFilter2 = "52_52";
                        } else {
                            viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                            if (r1.documentAttachType != 2) {
                                currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(currentPhotoFilter);
                                stringBuilder.append("_b");
                                currentPhotoFilterThumb = stringBuilder.toString();
                                currentPhotoFilter2 = currentPhotoFilter;
                            } else {
                                currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                                currentPhotoFilterThumb = currentPhotoFilter;
                                currentPhotoFilter2 = currentPhotoFilter;
                            }
                        }
                        if (r1.documentAttachType == 6) {
                        }
                        r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                        if (r1.documentAttachType != 2) {
                            if (r1.currentPhotoObject == null) {
                                if (currentPhotoObjectThumb == null) {
                                }
                                r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                            } else {
                                if (currentPhotoObjectThumb == null) {
                                }
                                r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                            }
                        } else if (r1.documentAttach == null) {
                            if (r1.currentPhotoObject == null) {
                            }
                            r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                        } else {
                            if (r1.currentPhotoObject == null) {
                            }
                            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                        }
                        r1.drawLinkImageView = true;
                        if (r1.mediaWebpage) {
                            viewWidth2 = 0;
                            viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                            viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                            viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                            dp = AndroidUtilities.dp(52.0f);
                            if (LocaleController.isRTL) {
                                f = 8.0f;
                                x2 = AndroidUtilities.dp(8.0f);
                            } else {
                                f = 8.0f;
                                x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                            }
                            r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                            r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                        } else {
                            viewWidth2 = viewWidth;
                            dp = MeasureSpec.getSize(heightMeasureSpec);
                            if (dp == 0) {
                                dp = AndroidUtilities.dp(100.0f);
                            }
                            setMeasuredDimension(viewWidth2, dp);
                            x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                            y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                            r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                            r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                        }
                    }
                }
                viewWidth = viewWidth2;
                w = 0;
                h = 0;
                if (r1.documentAttach != null) {
                    viewWidth2 = 0;
                    while (viewWidth2 < r1.documentAttach.attributes.size()) {
                        attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                        if (attribute instanceof TL_documentAttributeImageSize) {
                            if (!(attribute instanceof TL_documentAttributeVideo)) {
                                viewWidth2++;
                            }
                        }
                        w = attribute.f36w;
                        h = attribute.f35h;
                    }
                }
                if (r1.currentPhotoObject == null) {
                    if (currentPhotoObjectThumb != null) {
                        currentPhotoObjectThumb.size = -1;
                    }
                    w = r1.currentPhotoObject.f43w;
                    h = r1.currentPhotoObject.f42h;
                } else if (r1.inlineResult != null) {
                    result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                    w = result[0];
                    h = result[1];
                }
                dp = AndroidUtilities.dp(80.0f);
                h = dp;
                w = dp;
                currentPhotoFilterThumb = "52_52_b";
                if (r1.mediaWebpage) {
                    viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                    if (r1.documentAttachType != 2) {
                        currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                        currentPhotoFilterThumb = currentPhotoFilter;
                        currentPhotoFilter2 = currentPhotoFilter;
                    } else {
                        currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(currentPhotoFilter);
                        stringBuilder.append("_b");
                        currentPhotoFilterThumb = stringBuilder.toString();
                        currentPhotoFilter2 = currentPhotoFilter;
                    }
                } else {
                    currentPhotoFilter2 = "52_52";
                }
                if (r1.documentAttachType == 6) {
                }
                r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                if (r1.documentAttachType != 2) {
                    if (r1.documentAttach == null) {
                        if (r1.currentPhotoObject == null) {
                        }
                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                    } else {
                        if (r1.currentPhotoObject == null) {
                        }
                        r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                    }
                } else if (r1.currentPhotoObject == null) {
                    if (currentPhotoObjectThumb == null) {
                    }
                    r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                } else {
                    if (currentPhotoObjectThumb == null) {
                    }
                    r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                }
                r1.drawLinkImageView = true;
                if (r1.mediaWebpage) {
                    viewWidth2 = viewWidth;
                    dp = MeasureSpec.getSize(heightMeasureSpec);
                    if (dp == 0) {
                        dp = AndroidUtilities.dp(100.0f);
                    }
                    setMeasuredDimension(viewWidth2, dp);
                    x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                    y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                    r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                    r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                } else {
                    viewWidth2 = 0;
                    viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                    viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                    viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                    dp = AndroidUtilities.dp(52.0f);
                    if (LocaleController.isRTL) {
                        f = 8.0f;
                        x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                    } else {
                        f = 8.0f;
                        x2 = AndroidUtilities.dp(8.0f);
                    }
                    r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                    r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                    r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                }
            }
        }
        photoThumbs = photoThumbs3;
        ext = null;
        if (r1.documentAttach != null) {
            r1.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), z);
            currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
            if (currentPhotoObjectThumb == r1.currentPhotoObject) {
                currentPhotoObjectThumb = null;
            }
        } else if (!MessageObject.isGifDocument(r1.documentAttach)) {
            r1.currentPhotoObject = r1.documentAttach.thumb;
        } else if (MessageObject.isStickerDocument(r1.documentAttach)) {
            r1.currentPhotoObject = r1.documentAttach.thumb;
        } else {
            r1.currentPhotoObject = r1.documentAttach.thumb;
            ext = "webp";
        }
        if (r1.inlineResult != null) {
            if (r1.inlineResult.type.startsWith("gif")) {
                if (r1.inlineResult.type.equals("photo")) {
                    if (r1.inlineResult.thumb instanceof TL_webDocument) {
                    }
                }
            } else if (r1.documentAttachType != 2) {
                tLObject = (TL_webDocument) r1.inlineResult.content;
                r1.documentAttachType = 2;
            }
            tLObject = r1.inlineResult.thumb;
            if (r1.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) {
                if (!(r1.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo)) {
                    viewWidth = viewWidth2;
                    w = 0;
                    h = 0;
                    if (r1.documentAttach != null) {
                        viewWidth2 = 0;
                        while (viewWidth2 < r1.documentAttach.attributes.size()) {
                            attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                            if (attribute instanceof TL_documentAttributeImageSize) {
                                if (!(attribute instanceof TL_documentAttributeVideo)) {
                                    viewWidth2++;
                                }
                            }
                            w = attribute.f36w;
                            h = attribute.f35h;
                        }
                    }
                    if (r1.currentPhotoObject == null) {
                        if (currentPhotoObjectThumb != null) {
                            currentPhotoObjectThumb.size = -1;
                        }
                        w = r1.currentPhotoObject.f43w;
                        h = r1.currentPhotoObject.f42h;
                    } else if (r1.inlineResult != null) {
                        result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                        w = result[0];
                        h = result[1];
                    }
                    dp = AndroidUtilities.dp(80.0f);
                    h = dp;
                    w = dp;
                    currentPhotoFilterThumb = "52_52_b";
                    if (r1.mediaWebpage) {
                        currentPhotoFilter2 = "52_52";
                    } else {
                        viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                        if (r1.documentAttachType != 2) {
                            currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(currentPhotoFilter);
                            stringBuilder.append("_b");
                            currentPhotoFilterThumb = stringBuilder.toString();
                            currentPhotoFilter2 = currentPhotoFilter;
                        } else {
                            currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                            currentPhotoFilterThumb = currentPhotoFilter;
                            currentPhotoFilter2 = currentPhotoFilter;
                        }
                    }
                    if (r1.documentAttachType == 6) {
                    }
                    r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
                    if (r1.documentAttachType != 2) {
                        if (r1.currentPhotoObject == null) {
                            if (currentPhotoObjectThumb == null) {
                            }
                            r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
                        } else {
                            if (currentPhotoObjectThumb == null) {
                            }
                            r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
                        }
                    } else if (r1.documentAttach == null) {
                        if (r1.currentPhotoObject == null) {
                        }
                        r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                    } else {
                        if (r1.currentPhotoObject == null) {
                        }
                        r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                    }
                    r1.drawLinkImageView = true;
                    if (r1.mediaWebpage) {
                        viewWidth2 = 0;
                        viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                        viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                        viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                        dp = AndroidUtilities.dp(52.0f);
                        if (LocaleController.isRTL) {
                            f = 8.0f;
                            x2 = AndroidUtilities.dp(8.0f);
                        } else {
                            f = 8.0f;
                            x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                        }
                        r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                        r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                        r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
                    } else {
                        viewWidth2 = viewWidth;
                        dp = MeasureSpec.getSize(heightMeasureSpec);
                        if (dp == 0) {
                            dp = AndroidUtilities.dp(100.0f);
                        }
                        setMeasuredDimension(viewWidth2, dp);
                        x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                        y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                        r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                        r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
                    }
                }
            }
            lat = r1.inlineResult.send_message.geo.lat;
            viewWidth = viewWidth2;
            objArr = new Object[5];
            lon = r1.inlineResult.send_message.geo._long;
            objArr[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
            objArr[3] = Double.valueOf(lat);
            objArr[4] = Double.valueOf(lon);
            urlLocation = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
            w = 0;
            h = 0;
            if (r1.documentAttach != null) {
                viewWidth2 = 0;
                while (viewWidth2 < r1.documentAttach.attributes.size()) {
                    attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                    if (attribute instanceof TL_documentAttributeImageSize) {
                        if (!(attribute instanceof TL_documentAttributeVideo)) {
                            viewWidth2++;
                        }
                    }
                    w = attribute.f36w;
                    h = attribute.f35h;
                }
            }
            if (r1.currentPhotoObject == null) {
                if (currentPhotoObjectThumb != null) {
                    currentPhotoObjectThumb.size = -1;
                }
                w = r1.currentPhotoObject.f43w;
                h = r1.currentPhotoObject.f42h;
            } else if (r1.inlineResult != null) {
                result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
                w = result[0];
                h = result[1];
            }
            dp = AndroidUtilities.dp(80.0f);
            h = dp;
            w = dp;
            currentPhotoFilterThumb = "52_52_b";
            if (r1.mediaWebpage) {
                viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                if (r1.documentAttachType != 2) {
                    currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                    currentPhotoFilterThumb = currentPhotoFilter;
                    currentPhotoFilter2 = currentPhotoFilter;
                } else {
                    currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(currentPhotoFilter);
                    stringBuilder.append("_b");
                    currentPhotoFilterThumb = stringBuilder.toString();
                    currentPhotoFilter2 = currentPhotoFilter;
                }
            } else {
                currentPhotoFilter2 = "52_52";
            }
            if (r1.documentAttachType == 6) {
            }
            r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
            if (r1.documentAttachType != 2) {
                if (r1.documentAttach == null) {
                    if (r1.currentPhotoObject == null) {
                    }
                    r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
                } else {
                    if (r1.currentPhotoObject == null) {
                    }
                    r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
                }
            } else if (r1.currentPhotoObject == null) {
                if (currentPhotoObjectThumb == null) {
                }
                r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
            } else {
                if (currentPhotoObjectThumb == null) {
                }
                r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
            }
            r1.drawLinkImageView = true;
            if (r1.mediaWebpage) {
                viewWidth2 = viewWidth;
                dp = MeasureSpec.getSize(heightMeasureSpec);
                if (dp == 0) {
                    dp = AndroidUtilities.dp(100.0f);
                }
                setMeasuredDimension(viewWidth2, dp);
                x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
                y = (dp - AndroidUtilities.dp(24.0f)) / 2;
                r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
            } else {
                viewWidth2 = 0;
                viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
                dp = AndroidUtilities.dp(52.0f);
                if (LocaleController.isRTL) {
                    f = 8.0f;
                    x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
                } else {
                    f = 8.0f;
                    x2 = AndroidUtilities.dp(8.0f);
                }
                r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
                r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
                r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
            }
        }
        viewWidth = viewWidth2;
        w = 0;
        h = 0;
        if (r1.documentAttach != null) {
            viewWidth2 = 0;
            while (viewWidth2 < r1.documentAttach.attributes.size()) {
                attribute = (DocumentAttribute) r1.documentAttach.attributes.get(viewWidth2);
                if (attribute instanceof TL_documentAttributeImageSize) {
                    if (!(attribute instanceof TL_documentAttributeVideo)) {
                        viewWidth2++;
                    }
                }
                w = attribute.f36w;
                h = attribute.f35h;
            }
        }
        if (r1.currentPhotoObject == null) {
            if (currentPhotoObjectThumb != null) {
                currentPhotoObjectThumb.size = -1;
            }
            w = r1.currentPhotoObject.f43w;
            h = r1.currentPhotoObject.f42h;
        } else if (r1.inlineResult != null) {
            result = MessageObject.getInlineResultWidthAndHeight(r1.inlineResult);
            w = result[0];
            h = result[1];
        }
        dp = AndroidUtilities.dp(80.0f);
        h = dp;
        w = dp;
        currentPhotoFilterThumb = "52_52_b";
        if (r1.mediaWebpage) {
            currentPhotoFilter2 = "52_52";
        } else {
            viewWidth2 = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
            if (r1.documentAttachType != 2) {
                currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                stringBuilder = new StringBuilder();
                stringBuilder.append(currentPhotoFilter);
                stringBuilder.append("_b");
                currentPhotoFilterThumb = stringBuilder.toString();
                currentPhotoFilter2 = currentPhotoFilter;
            } else {
                currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) viewWidth2) / AndroidUtilities.density)), Integer.valueOf(80)});
                currentPhotoFilterThumb = currentPhotoFilter;
                currentPhotoFilter2 = currentPhotoFilter;
            }
        }
        if (r1.documentAttachType == 6) {
        }
        r1.linkImageView.setAspectFit(r1.documentAttachType == 6);
        if (r1.documentAttachType != 2) {
            if (r1.currentPhotoObject == null) {
                if (currentPhotoObjectThumb == null) {
                }
                r1.linkImageView.setImage(tLObject, urlLocation, currentPhotoFilter2, null, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, -1, ext, 1);
            } else {
                if (currentPhotoObjectThumb == null) {
                }
                r1.linkImageView.setImage(r1.currentPhotoObject.location, currentPhotoFilter2, currentPhotoObjectThumb == null ? null : currentPhotoObjectThumb.location, currentPhotoFilterThumb, r1.currentPhotoObject.size, ext, 0);
            }
        } else if (r1.documentAttach == null) {
            if (r1.currentPhotoObject == null) {
            }
            r1.linkImageView.setImage(tLObject, urlLocation, null, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, -1, ext, 1);
        } else {
            if (r1.currentPhotoObject == null) {
            }
            r1.linkImageView.setImage(r1.documentAttach, null, r1.currentPhotoObject == null ? null : r1.currentPhotoObject.location, currentPhotoFilter2, r1.documentAttach.size, ext, 0);
        }
        r1.drawLinkImageView = true;
        if (r1.mediaWebpage) {
            viewWidth2 = 0;
            viewWidth2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
            viewWidth2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
            viewWidth2 += r1.linkLayout.getLineBottom(r1.linkLayout.getLineCount() - 1);
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), viewWidth2)) + r1.needDivider);
            dp = AndroidUtilities.dp(52.0f);
            if (LocaleController.isRTL) {
                f = 8.0f;
                x2 = AndroidUtilities.dp(8.0f);
            } else {
                f = 8.0f;
                x2 = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - dp;
            }
            r1.letterDrawable.setBounds(x2, AndroidUtilities.dp(f), x2 + dp, AndroidUtilities.dp(60.0f));
            r1.linkImageView.setImageCoords(x2, AndroidUtilities.dp(f), dp, dp);
            r1.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x2, AndroidUtilities.dp(56.0f));
        } else {
            viewWidth2 = viewWidth;
            dp = MeasureSpec.getSize(heightMeasureSpec);
            if (dp == 0) {
                dp = AndroidUtilities.dp(100.0f);
            }
            setMeasuredDimension(viewWidth2, dp);
            x = (viewWidth2 - AndroidUtilities.dp(24.0f)) / 2;
            y = (dp - AndroidUtilities.dp(24.0f)) / 2;
            r1.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
            r1.linkImageView.setImageCoords(0, 0, viewWidth2, dp);
        }
    }

    private void setAttachType() {
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        if (this.documentAttach != null) {
            if (MessageObject.isGifDocument(this.documentAttach)) {
                this.documentAttachType = 2;
            } else if (MessageObject.isStickerDocument(this.documentAttach)) {
                this.documentAttachType = 6;
            } else if (MessageObject.isMusicDocument(this.documentAttach)) {
                this.documentAttachType = 5;
            } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
                this.documentAttachType = 3;
            }
        } else if (this.inlineResult != null) {
            if (this.inlineResult.photo != null) {
                this.documentAttachType = 7;
            } else if (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                this.documentAttachType = 5;
            } else if (this.inlineResult.type.equals("voice")) {
                this.documentAttachType = 3;
            }
        }
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            TL_message message = new TL_message();
            message.out = true;
            message.id = -Utilities.random.nextInt();
            message.to_id = new TL_peerUser();
            Peer peer = message.to_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            message.from_id = clientUserId;
            peer.user_id = clientUserId;
            message.date = (int) (System.currentTimeMillis() / 1000);
            message.message = TtmlNode.ANONYMOUS_REGION_ID;
            message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia = message.media;
            messageMedia.flags |= 3;
            message.media.document = new TL_document();
            message.flags |= 768;
            if (this.documentAttach != null) {
                message.media.document = this.documentAttach;
                message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                String ext = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.id = 0;
                message.media.document.access_hash = 0;
                message.media.document.date = message.date;
                Document document = message.media.document;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("audio/");
                stringBuilder.append(ext);
                document.mime_type = stringBuilder.toString();
                message.media.document.size = 0;
                message.media.document.thumb = new TL_photoSizeEmpty();
                message.media.document.thumb.type = "s";
                message.media.document.dc_id = 0;
                TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                attributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                attributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : TtmlNode.ANONYMOUS_REGION_ID;
                attributeAudio.performer = this.inlineResult.description != null ? this.inlineResult.description : TtmlNode.ANONYMOUS_REGION_ID;
                attributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    attributeAudio.voice = true;
                }
                message.media.document.attributes.add(attributeAudio);
                TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(Utilities.MD5(this.inlineResult.content.url));
                stringBuilder2.append(".");
                stringBuilder2.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg"));
                fileName.file_name = stringBuilder2.toString();
                message.media.document.attributes.add(fileName);
                File directory = FileLoader.getDirectory(4);
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(Utilities.MD5(this.inlineResult.content.url));
                stringBuilder3.append(".");
                stringBuilder3.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg"));
                message.attachPath = new File(directory, stringBuilder3.toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, message, false);
        }
    }

    public void setLink(BotInlineResult contextResult, boolean media, boolean divider, boolean shadow) {
        this.needDivider = divider;
        this.needShadow = shadow;
        this.inlineResult = contextResult;
        if (this.inlineResult == null || this.inlineResult.document == null) {
            this.documentAttach = null;
        } else {
            this.documentAttach = this.inlineResult.document;
        }
        this.mediaWebpage = media;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public void setGif(Document document, boolean divider) {
        this.needDivider = divider;
        this.needShadow = false;
        this.inlineResult = null;
        this.documentAttach = document;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public Document getDocument() {
        return this.documentAttach;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false);
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!(this.mediaWebpage || this.delegate == null)) {
            if (this.inlineResult != null) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                boolean result = false;
                int side = AndroidUtilities.dp(NUM);
                if (this.documentAttachType != 3) {
                    if (this.documentAttachType != 5) {
                        if (!(this.inlineResult == null || this.inlineResult.content == null || TextUtils.isEmpty(this.inlineResult.content.url))) {
                            if (event.getAction() == 0) {
                                if (this.letterDrawable.getBounds().contains(x, y)) {
                                    this.buttonPressed = true;
                                    result = true;
                                }
                            } else if (this.buttonPressed) {
                                if (event.getAction() == 1) {
                                    this.buttonPressed = false;
                                    playSoundEffect(0);
                                    this.delegate.didPressedImage(this);
                                } else if (event.getAction() == 3) {
                                    this.buttonPressed = false;
                                } else if (event.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y)) {
                                    this.buttonPressed = false;
                                }
                            }
                        }
                        if (!result) {
                            result = super.onTouchEvent(event);
                        }
                        return result;
                    }
                }
                boolean area = this.letterDrawable.getBounds().contains(x, y);
                if (event.getAction() == 0) {
                    if (area) {
                        this.buttonPressed = true;
                        invalidate();
                        result = true;
                        this.radialProgress.swapBackground(getDrawableForCurrentState());
                    }
                } else if (this.buttonPressed) {
                    if (event.getAction() == 1) {
                        this.buttonPressed = false;
                        playSoundEffect(0);
                        didPressedButton();
                        invalidate();
                    } else if (event.getAction() == 3) {
                        this.buttonPressed = false;
                        invalidate();
                    } else if (event.getAction() == 2 && !area) {
                        this.buttonPressed = false;
                        invalidate();
                    }
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                }
                if (result) {
                    result = super.onTouchEvent(event);
                }
                return result;
            }
        }
        return super.onTouchEvent(event);
    }

    private void didPressedButton() {
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return;
        }
        if (this.buttonState == 0) {
            if (MediaController.getInstance().playMessage(this.currentMessageObject)) {
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 1) {
            if (MediaController.getInstance().pauseMessage(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 2) {
            this.radialProgress.setProgress(0.0f, false);
            if (this.documentAttach != null) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                FileLoader.getInstance(this.currentAccount).loadFile((TL_webDocument) this.inlineResult.content, true, 1);
            }
            this.buttonState = 4;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
            invalidate();
        } else if (this.buttonState == 4) {
            if (this.documentAttach != null) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile((TL_webDocument) this.inlineResult.content);
            }
            this.buttonState = 2;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        int w;
        int h;
        int x;
        if (!this.mediaWebpage) {
            if (this.documentAttachType != 3) {
                if (this.documentAttachType != 5) {
                    int y;
                    if (this.inlineResult != null && this.inlineResult.type.equals("file")) {
                        w = Theme.chat_inlineResultFile.getIntrinsicWidth();
                        h = Theme.chat_inlineResultFile.getIntrinsicHeight();
                        x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
                        y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultFile.setBounds(x, y, x + w, y + h);
                        Theme.chat_inlineResultFile.draw(canvas);
                    } else if (this.inlineResult != null && (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO) || this.inlineResult.type.equals("voice"))) {
                        w = Theme.chat_inlineResultAudio.getIntrinsicWidth();
                        h = Theme.chat_inlineResultAudio.getIntrinsicHeight();
                        x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
                        y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultAudio.setBounds(x, y, x + w, y + h);
                        Theme.chat_inlineResultAudio.draw(canvas);
                    } else if (this.inlineResult == null || !(this.inlineResult.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
                        this.letterDrawable.draw(canvas);
                    } else {
                        w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                        h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                        x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
                        y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
                        Theme.chat_inlineResultLocation.draw(canvas);
                    }
                }
            }
            this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
            this.radialProgress.draw(canvas);
        } else if (this.inlineResult != null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue))) {
            w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
            int h2 = Theme.chat_inlineResultLocation.getIntrinsicHeight();
            h = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - w) / 2);
            x = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - h2) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
            Theme.chat_inlineResultLocation.setBounds(h, x, h + w, x + h2);
            Theme.chat_inlineResultLocation.draw(canvas);
        }
        if (this.drawLinkImageView) {
            if (this.inlineResult != null) {
                this.linkImageView.setVisible(PhotoViewer.isShowingImage(this.inlineResult) ^ 1, false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == 1.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                if (!this.scaled || this.scale == 0.8f) {
                    this.scale += ((float) dt) / 400.0f;
                    if (this.scale > 1.0f) {
                        this.scale = 1.0f;
                    }
                } else {
                    this.scale -= ((float) dt) / 400.0f;
                    if (this.scale < 0.8f) {
                        this.scale = 0.8f;
                    }
                }
                invalidate();
            }
            canvas.scale(this.scale, this.scale, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage && (this.documentAttachType == 7 || this.documentAttachType == 2)) {
            this.radialProgress.draw(canvas);
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        if (this.needShadow) {
            Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0f));
            Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(canvas);
        }
    }

    private Drawable getDrawableForCurrentState() {
        Drawable drawable = null;
        if (this.documentAttachType != 3) {
            if (this.documentAttachType != 5) {
                if (this.buttonState == 1) {
                    drawable = Theme.chat_photoStatesDrawables[5][0];
                }
                return drawable;
            }
        }
        if (this.buttonState == -1) {
            return null;
        }
        this.radialProgress.setAlphaForPrevious(false);
        return Theme.chat_fileStatesDrawable[this.buttonState + 5][this.buttonPressed];
    }

    public void updateButtonState(boolean animated) {
        StringBuilder stringBuilder;
        float f;
        Float progress;
        boolean isLoading;
        Float progress2;
        String fileName = null;
        File cacheFile = null;
        if (this.documentAttachType != 5) {
            if (this.documentAttachType != 3) {
                if (this.mediaWebpage) {
                    if (this.inlineResult != null) {
                        if (this.inlineResult.document instanceof TL_document) {
                            fileName = FileLoader.getAttachFileName(this.inlineResult.document);
                            cacheFile = FileLoader.getPathToAttach(this.inlineResult.document);
                        } else if (this.inlineResult.photo instanceof TL_photo) {
                            this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                            fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                            cacheFile = FileLoader.getPathToAttach(this.currentPhotoObject);
                        } else if (this.inlineResult.content instanceof TL_webDocument) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(Utilities.MD5(this.inlineResult.content.url));
                            stringBuilder.append(".");
                            stringBuilder.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, "jpg"));
                            fileName = stringBuilder.toString();
                            cacheFile = new File(FileLoader.getDirectory(4), fileName);
                        } else if (this.inlineResult.thumb instanceof TL_webDocument) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(Utilities.MD5(this.inlineResult.thumb.url));
                            stringBuilder.append(".");
                            stringBuilder.append(ImageLoader.getHttpUrlExtension(this.inlineResult.thumb.url, "jpg"));
                            fileName = stringBuilder.toString();
                            cacheFile = new File(FileLoader.getDirectory(4), fileName);
                        }
                    } else if (this.documentAttach != null) {
                        fileName = FileLoader.getAttachFileName(this.documentAttach);
                        cacheFile = FileLoader.getPathToAttach(this.documentAttach);
                    }
                }
                if (TextUtils.isEmpty(fileName)) {
                    if (cacheFile.exists()) {
                        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
                        f = 0.0f;
                        if (this.documentAttachType != 5) {
                            if (this.documentAttachType == 3) {
                                this.buttonState = 1;
                                progress = ImageLoader.getInstance().getFileProgress(fileName);
                                if (progress != null) {
                                    f = progress.floatValue();
                                }
                                this.radialProgress.setProgress(f, false);
                                this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                                invalidate();
                            }
                        }
                        if (this.documentAttach == null) {
                            isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                        } else {
                            isLoading = ImageLoader.getInstance().isLoadingHttpFile(fileName);
                        }
                        if (isLoading) {
                            this.buttonState = 2;
                            this.radialProgress.setProgress(0.0f, animated);
                            this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                        } else {
                            this.buttonState = 4;
                            progress2 = ImageLoader.getInstance().getFileProgress(fileName);
                            if (progress2 == null) {
                                this.radialProgress.setProgress(progress2.floatValue(), animated);
                            } else {
                                this.radialProgress.setProgress(0.0f, animated);
                            }
                            this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                        }
                        invalidate();
                    } else {
                        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                        if (this.documentAttachType != 5) {
                            if (this.documentAttachType == 3) {
                                this.buttonState = -1;
                                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                                invalidate();
                            }
                        }
                        isLoading = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                        if (isLoading) {
                            if (isLoading || !MediaController.getInstance().isMessagePaused()) {
                                this.buttonState = 1;
                                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                                invalidate();
                            }
                        }
                        this.buttonState = 0;
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                        invalidate();
                    }
                    return;
                }
                this.radialProgress.setBackground(null, false, false);
            }
        }
        if (this.documentAttach != null) {
            fileName = FileLoader.getAttachFileName(this.documentAttach);
            cacheFile = FileLoader.getPathToAttach(this.documentAttach);
        } else if (this.inlineResult.content instanceof TL_webDocument) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(Utilities.MD5(this.inlineResult.content.url));
            stringBuilder.append(".");
            stringBuilder.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg"));
            fileName = stringBuilder.toString();
            cacheFile = new File(FileLoader.getDirectory(4), fileName);
        }
        if (TextUtils.isEmpty(fileName)) {
            if (cacheFile.exists()) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                if (this.documentAttachType != 5) {
                    if (this.documentAttachType == 3) {
                        this.buttonState = -1;
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                        invalidate();
                    }
                }
                isLoading = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (isLoading) {
                    if (isLoading) {
                    }
                    this.buttonState = 1;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                    invalidate();
                }
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
                f = 0.0f;
                if (this.documentAttachType != 5) {
                    if (this.documentAttachType == 3) {
                        this.buttonState = 1;
                        progress = ImageLoader.getInstance().getFileProgress(fileName);
                        if (progress != null) {
                            f = progress.floatValue();
                        }
                        this.radialProgress.setProgress(f, false);
                        this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                        invalidate();
                    }
                }
                if (this.documentAttach == null) {
                    isLoading = ImageLoader.getInstance().isLoadingHttpFile(fileName);
                } else {
                    isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                }
                if (isLoading) {
                    this.buttonState = 4;
                    progress2 = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress2 == null) {
                        this.radialProgress.setProgress(0.0f, animated);
                    } else {
                        this.radialProgress.setProgress(progress2.floatValue(), animated);
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                }
                invalidate();
            }
            return;
        }
        this.radialProgress.setBackground(null, false, false);
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String fileName) {
        updateButtonState(false);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        if (this.documentAttachType != 3) {
            if (this.documentAttachType != 5) {
                if (this.buttonState != 1) {
                    updateButtonState(false);
                    return;
                }
                return;
            }
        }
        if (this.buttonState != 4) {
            updateButtonState(false);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
