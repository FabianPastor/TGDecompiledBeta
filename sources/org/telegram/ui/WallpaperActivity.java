package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_reactionCount;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate.-CC;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.WallpapersListActivity.ColorWallpaper;
import org.telegram.ui.WallpapersListActivity.FileWallpaper;

public class WallpaperActivity extends BaseFragment implements FileDownloadProgressListener, NotificationCenterDelegate {
    private static final int share_item = 1;
    private int TAG;
    private int backgroundColor;
    private BackupImageView backgroundImage;
    private Paint backgroundPaint;
    private Mode blendMode = Mode.SRC_IN;
    private Bitmap blurredBitmap;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private FrameLayout buttonsContainer;
    private CheckBoxView[] checkBoxView;
    private Paint checkPaint;
    private ColorPicker colorPicker;
    private float currentIntensity = 0.4f;
    private Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    private Paint eraserPaint;
    private String imageFilter = "640_360";
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    private boolean isBlurred;
    private boolean isMotion;
    private RecyclerListView listView;
    private String loadingFile = null;
    private File loadingFileObject = null;
    private PhotoSize loadingSize = null;
    private int maxWallpaperSize = 1920;
    private AnimatorSet motionAnimation;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale = 1.0f;
    private int patternColor;
    private FrameLayout[] patternLayout = new FrameLayout[3];
    private ArrayList<Object> patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer = new FrameLayout[2];
    private TextView[] patternsCancelButton = new TextView[2];
    private LinearLayoutManager patternsLayoutManager;
    private RecyclerListView patternsListView;
    private TextView[] patternsSaveButton = new TextView[2];
    private int previousBackgroundColor;
    private float previousIntensity;
    private TL_wallPaper previousSelectedPattern;
    private boolean progressVisible;
    private RadialProgress2 radialProgress;
    private TL_wallPaper selectedPattern;
    private TextPaint textPaint;

    private class CheckBoxView extends View {
        private static final float progressBounceDiff = 0.2f;
        public final Property<CheckBoxView, Float> PROGRESS_PROPERTY = new FloatProperty<CheckBoxView>("progress") {
            public void setValue(CheckBoxView checkBoxView, float f) {
                CheckBoxView.this.progress = f;
                CheckBoxView.this.invalidate();
            }

            public Float get(CheckBoxView checkBoxView) {
                return Float.valueOf(CheckBoxView.this.progress);
            }
        };
        private ObjectAnimator checkAnimator;
        private String currentText;
        private int currentTextSize;
        private Bitmap drawBitmap;
        private Canvas drawCanvas;
        private boolean isChecked;
        private int maxTextSize;
        private float progress;
        private RectF rect = new RectF();

        public CheckBoxView(Context context, boolean z) {
            super(context);
            if (z) {
                this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Config.ARGB_4444);
                this.drawCanvas = new Canvas(this.drawBitmap);
            }
        }

        public void setText(String str, int i, int i2) {
            this.currentText = str;
            this.currentTextSize = i;
            this.maxTextSize = i2;
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(this.maxTextSize + AndroidUtilities.dp(56.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
            int measuredWidth = ((getMeasuredWidth() - this.currentTextSize) - AndroidUtilities.dp(28.0f)) / 2;
            canvas.drawText(this.currentText, (float) (AndroidUtilities.dp(28.0f) + measuredWidth), (float) AndroidUtilities.dp(21.0f), WallpaperActivity.this.textPaint);
            canvas.save();
            canvas.translate((float) measuredWidth, (float) AndroidUtilities.dp(7.0f));
            if (this.drawBitmap != null) {
                float f;
                float f2 = this.progress;
                if (f2 <= 0.5f) {
                    f2 /= 0.5f;
                    f = f2;
                } else {
                    f2 = 2.0f - (f2 / 0.5f);
                    f = 1.0f;
                }
                float dp = ((float) AndroidUtilities.dp(1.0f)) * f2;
                this.rect.set(dp, dp, ((float) AndroidUtilities.dp(18.0f)) - dp, ((float) AndroidUtilities.dp(18.0f)) - dp);
                this.drawBitmap.eraseColor(0);
                WallpaperActivity.this.backgroundPaint.setColor(-1);
                Canvas canvas2 = this.drawCanvas;
                RectF rectF = this.rect;
                canvas2.drawRoundRect(rectF, rectF.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.backgroundPaint);
                if (f != 1.0f) {
                    float min = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * f) + dp);
                    this.rect.set(((float) AndroidUtilities.dp(2.0f)) + min, ((float) AndroidUtilities.dp(2.0f)) + min, ((float) AndroidUtilities.dp(16.0f)) - min, ((float) AndroidUtilities.dp(16.0f)) - min);
                    Canvas canvas3 = this.drawCanvas;
                    RectF rectF2 = this.rect;
                    canvas3.drawRoundRect(rectF2, rectF2.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.eraserPaint);
                }
                if (this.progress > 0.5f) {
                    float f3 = 1.0f - f2;
                    this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) - (((float) AndroidUtilities.dp(2.5f)) * f3))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(2.5f)) * f3))), WallpaperActivity.this.checkPaint);
                    this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) + (((float) AndroidUtilities.dp(6.0f)) * f3))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(6.0f)) * f3))), WallpaperActivity.this.checkPaint);
                }
                canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
            } else {
                WallpaperActivity.this.backgroundPaint.setColor(WallpaperActivity.this.backgroundColor);
                this.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f));
                RectF rectF3 = this.rect;
                canvas.drawRoundRect(rectF3, rectF3.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.backgroundPaint);
            }
            canvas.restore();
        }

        private void setProgress(float f) {
            if (this.progress != f) {
                this.progress = f;
                invalidate();
            }
        }

        private void cancelCheckAnimator() {
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
        }

        private void animateToCheckedState(boolean z) {
            Property property = this.PROGRESS_PROPERTY;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            this.checkAnimator = ObjectAnimator.ofFloat(this, property, fArr);
            this.checkAnimator.setDuration(300);
            this.checkAnimator.start();
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
        }

        public void setChecked(boolean z, boolean z2) {
            if (z != this.isChecked) {
                this.isChecked = z;
                if (z2) {
                    animateToCheckedState(z);
                } else {
                    cancelCheckAnimator();
                    this.progress = z ? 1.0f : 0.0f;
                    invalidate();
                }
            }
        }

        public boolean isChecked() {
            return this.isChecked;
        }
    }

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    private class PatternCell extends BackupImageView implements FileDownloadProgressListener {
        private int TAG;
        private TL_wallPaper currentPattern;
        private RadialProgress2 radialProgress;
        private RectF rect = new RectF();
        private boolean wasSelected;

        public void onProgressUpload(String str, float f, boolean z) {
        }

        public PatternCell(Context context) {
            super(context);
            setRoundRadius(AndroidUtilities.dp(6.0f));
            this.radialProgress = new RadialProgress2(this);
            this.radialProgress.setProgressRect(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f));
            this.TAG = DownloadController.getInstance(WallpaperActivity.this.currentAccount).generateObserverTag();
        }

        private void setPattern(TL_wallPaper tL_wallPaper) {
            this.currentPattern = tL_wallPaper;
            if (tL_wallPaper != null) {
                setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, 100), tL_wallPaper.document), "100_100", null, null, "jpg", 0, 1, tL_wallPaper);
            } else {
                setImageDrawable(null);
            }
            updateSelected(false);
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateSelected(false);
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x003b  */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x002c  */
        /* JADX WARNING: Missing block: B:9:0x0025, code skipped:
            if (r0.id == org.telegram.ui.WallpaperActivity.access$100(r8.this$0).id) goto L_0x0027;
     */
        public void updateSelected(boolean r9) {
            /*
            r8 = this;
            r0 = r8.currentPattern;
            r1 = 0;
            if (r0 != 0) goto L_0x000d;
        L_0x0005:
            r0 = org.telegram.ui.WallpaperActivity.this;
            r0 = r0.selectedPattern;
            if (r0 == 0) goto L_0x0027;
        L_0x000d:
            r0 = org.telegram.ui.WallpaperActivity.this;
            r0 = r0.selectedPattern;
            if (r0 == 0) goto L_0x0029;
        L_0x0015:
            r0 = r8.currentPattern;
            if (r0 == 0) goto L_0x0029;
        L_0x0019:
            r2 = r0.id;
            r0 = org.telegram.ui.WallpaperActivity.this;
            r0 = r0.selectedPattern;
            r4 = r0.id;
            r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r0 != 0) goto L_0x0029;
        L_0x0027:
            r0 = 1;
            goto L_0x002a;
        L_0x0029:
            r0 = 0;
        L_0x002a:
            if (r0 == 0) goto L_0x003b;
        L_0x002c:
            r2 = org.telegram.ui.WallpaperActivity.this;
            r3 = r8.radialProgress;
            r4 = r2.selectedPattern;
            r6 = 0;
            r5 = r8;
            r7 = r9;
            r2.updateButtonState(r3, r4, r5, r6, r7);
            goto L_0x0041;
        L_0x003b:
            r0 = r8.radialProgress;
            r2 = 4;
            r0.setIcon(r2, r1, r9);
        L_0x0041:
            r8.invalidate();
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpaperActivity$PatternCell.updateSelected(boolean):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            getImageReceiver().setAlpha(0.8f);
            WallpaperActivity.this.backgroundPaint.setColor(WallpaperActivity.this.backgroundColor);
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), WallpaperActivity.this.backgroundPaint);
            super.onDraw(canvas);
            this.radialProgress.setColors(WallpaperActivity.this.patternColor, WallpaperActivity.this.patternColor, -1, -1);
            this.radialProgress.draw(canvas);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
        }

        public void onFailedDownload(String str, boolean z) {
            if (z) {
                this.radialProgress.setIcon(4, false, true);
            } else {
                WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, true, z);
            }
        }

        public void onSuccessDownload(String str) {
            this.radialProgress.setProgress(1.0f, WallpaperActivity.this.progressVisible);
            WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, false, true);
        }

        public void onProgressDownload(String str, float f) {
            this.radialProgress.setProgress(f, WallpaperActivity.this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, false, true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages = new ArrayList();

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            TL_message tL_message = new TL_message();
            if (WallpaperActivity.this.currentWallpaper instanceof ColorWallpaper) {
                tL_message.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", NUM);
            } else {
                tL_message.message = LocaleController.getString("BackgroundPreviewLine2", NUM);
            }
            int i = currentTimeMillis + 60;
            tL_message.date = i;
            tL_message.dialog_id = 1;
            tL_message.flags = 259;
            tL_message.from_id = UserConfig.getInstance(WallpaperActivity.this.currentAccount).getClientUserId();
            tL_message.id = 1;
            tL_message.media = new TL_messageMediaEmpty();
            tL_message.out = true;
            tL_message.to_id = new TL_peerUser();
            tL_message.to_id.user_id = 0;
            MessageObject messageObject = new MessageObject(WallpaperActivity.this.currentAccount, tL_message, true);
            messageObject.eventId = 1;
            messageObject.resetLayout();
            this.messages.add(messageObject);
            tL_message = new TL_message();
            if (WallpaperActivity.this.currentWallpaper instanceof ColorWallpaper) {
                tL_message.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", NUM);
            } else {
                tL_message.message = LocaleController.getString("BackgroundPreviewLine1", NUM);
            }
            tL_message.date = i;
            tL_message.dialog_id = 1;
            tL_message.flags = 265;
            tL_message.from_id = 0;
            tL_message.id = 1;
            tL_message.media = new TL_messageMediaEmpty();
            tL_message.out = false;
            tL_message.to_id = new TL_peerUser();
            tL_message.to_id.user_id = UserConfig.getInstance(WallpaperActivity.this.currentAccount).getClientUserId();
            MessageObject messageObject2 = new MessageObject(WallpaperActivity.this.currentAccount, tL_message, true);
            messageObject2.eventId = 1;
            messageObject2.resetLayout();
            this.messages.add(messageObject2);
            tL_message = new TL_message();
            tL_message.message = LocaleController.formatDateChat((long) currentTimeMillis);
            tL_message.id = 0;
            tL_message.date = currentTimeMillis;
            MessageObject messageObject3 = new MessageObject(WallpaperActivity.this.currentAccount, tL_message, false);
            messageObject3.type = 10;
            messageObject3.contentType = 1;
            messageObject3.isDateObject = true;
            this.messages.add(messageObject3);
        }

        public int getItemCount() {
            return this.messages.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View chatMessageCell;
            if (i == 0) {
                chatMessageCell = new ChatMessageCell(this.mContext);
                chatMessageCell.setDelegate(new ChatMessageCellDelegate() {
                    public /* synthetic */ boolean canPerformActions() {
                        return -CC.$default$canPerformActions(this);
                    }

                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didLongPress(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                        -CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressCancelSendButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i, float f, float f2) {
                        -CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                    }

                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didPressImage(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                        -CC.$default$didPressInstantButton(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didPressOther(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TL_reactionCount tL_reactionCount) {
                        -CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        -CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressShare(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                        -CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
                    }

                    public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, User user, float f, float f2) {
                        -CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        -CC.$default$didPressViaBot(this, chatMessageCell, str);
                    }

                    public /* synthetic */ void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
                        -CC.$default$didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                    }

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        -CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(int i) {
                        return -CC.$default$getAdminRank(this, i);
                    }

                    public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        -CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                    }

                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return -CC.$default$needPlayMessage(this, messageObject);
                    }

                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        -CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return -CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ void videoTimerReached() {
                        -CC.$default$videoTimerReached(this);
                    }
                });
            } else if (i == 1) {
                chatMessageCell = new ChatActionCell(this.mContext);
                chatMessageCell.setDelegate(new ChatActionCellDelegate() {
                    public /* synthetic */ void didClickImage(ChatActionCell chatActionCell) {
                        ChatActionCellDelegate.-CC.$default$didClickImage(this, chatActionCell);
                    }

                    public /* synthetic */ void didLongPress(ChatActionCell chatActionCell, float f, float f2) {
                        ChatActionCellDelegate.-CC.$default$didLongPress(this, chatActionCell, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
                        ChatActionCellDelegate.-CC.$default$didPressBotButton(this, messageObject, keyboardButton);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatActionCell chatActionCell, int i) {
                        ChatActionCellDelegate.-CC.$default$didPressReplyMessage(this, chatActionCell, i);
                    }

                    public /* synthetic */ void needOpenUserProfile(int i) {
                        ChatActionCellDelegate.-CC.$default$needOpenUserProfile(this, i);
                    }
                });
            } else {
                chatMessageCell = null;
            }
            chatMessageCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(chatMessageCell);
        }

        public int getItemViewType(int i) {
            return (i < 0 || i >= this.messages.size()) ? 4 : ((MessageObject) this.messages.get(i)).contentType;
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x0059  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
            r9 = this;
            r0 = r9.messages;
            r0 = r0.get(r11);
            r0 = (org.telegram.messenger.MessageObject) r0;
            r1 = r10.itemView;
            r2 = r1 instanceof org.telegram.ui.Cells.ChatMessageCell;
            if (r2 == 0) goto L_0x008b;
        L_0x000e:
            r1 = (org.telegram.ui.Cells.ChatMessageCell) r1;
            r2 = 0;
            r1.isChat = r2;
            r3 = r11 + -1;
            r4 = r9.getItemViewType(r3);
            r5 = 1;
            r11 = r11 + r5;
            r6 = r9.getItemViewType(r11);
            r7 = r0.messageOwner;
            r7 = r7.reply_markup;
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
            r8 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
            if (r7 != 0) goto L_0x0052;
        L_0x0029:
            r7 = r10.getItemViewType();
            if (r4 != r7) goto L_0x0052;
        L_0x002f:
            r4 = r9.messages;
            r3 = r4.get(r3);
            r3 = (org.telegram.messenger.MessageObject) r3;
            r4 = r3.isOutOwner();
            r7 = r0.isOutOwner();
            if (r4 != r7) goto L_0x0052;
        L_0x0041:
            r3 = r3.messageOwner;
            r3 = r3.date;
            r4 = r0.messageOwner;
            r4 = r4.date;
            r3 = r3 - r4;
            r3 = java.lang.Math.abs(r3);
            if (r3 > r8) goto L_0x0052;
        L_0x0050:
            r3 = 1;
            goto L_0x0053;
        L_0x0052:
            r3 = 0;
        L_0x0053:
            r10 = r10.getItemViewType();
            if (r6 != r10) goto L_0x0083;
        L_0x0059:
            r10 = r9.messages;
            r10 = r10.get(r11);
            r10 = (org.telegram.messenger.MessageObject) r10;
            r11 = r10.messageOwner;
            r11 = r11.reply_markup;
            r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
            if (r11 != 0) goto L_0x0083;
        L_0x0069:
            r11 = r10.isOutOwner();
            r4 = r0.isOutOwner();
            if (r11 != r4) goto L_0x0083;
        L_0x0073:
            r10 = r10.messageOwner;
            r10 = r10.date;
            r11 = r0.messageOwner;
            r11 = r11.date;
            r10 = r10 - r11;
            r10 = java.lang.Math.abs(r10);
            if (r10 > r8) goto L_0x0083;
        L_0x0082:
            r2 = 1;
        L_0x0083:
            r1.setFullyDraw(r5);
            r10 = 0;
            r1.setMessageObject(r0, r10, r3, r2);
            goto L_0x0099;
        L_0x008b:
            r10 = r1 instanceof org.telegram.ui.Cells.ChatActionCell;
            if (r10 == 0) goto L_0x0099;
        L_0x008f:
            r1 = (org.telegram.ui.Cells.ChatActionCell) r1;
            r1.setMessageObject(r0);
            r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r1.setAlpha(r10);
        L_0x0099:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpaperActivity$ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }
    }

    private class PatternsAdapter extends SelectionAdapter {
        private Context mContext;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemViewType(int i) {
            return super.getItemViewType(i);
        }

        public int getItemCount() {
            return (WallpaperActivity.this.patterns != null ? WallpaperActivity.this.patterns.size() : 0) + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new PatternCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            PatternCell patternCell = (PatternCell) viewHolder.itemView;
            if (i == 0) {
                patternCell.setPattern(null);
            } else {
                patternCell.setPattern((TL_wallPaper) WallpaperActivity.this.patterns.get(i - 1));
            }
            patternCell.getImageReceiver().setColorFilter(new PorterDuffColorFilter(WallpaperActivity.this.patternColor, WallpaperActivity.this.blendMode));
        }
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public WallpaperActivity(Object obj, Bitmap bitmap) {
        this.currentWallpaper = obj;
        this.currentWallpaperBitmap = bitmap;
        obj = this.currentWallpaper;
        if (obj instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
        } else if (obj instanceof ColorWallpaper) {
            ColorWallpaper colorWallpaper = (ColorWallpaper) obj;
            this.isMotion = colorWallpaper.motion;
            this.selectedPattern = colorWallpaper.pattern;
            if (this.selectedPattern != null) {
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    public void setInitialModes(boolean z, boolean z2) {
        this.isBlurred = z;
        this.isMotion = z2;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((int) (1080.0f / AndroidUtilities.density));
        stringBuilder.append("_");
        stringBuilder.append((int) (1920.0f / AndroidUtilities.density));
        stringBuilder.append("_f");
        this.imageFilter = stringBuilder.toString();
        Point point = AndroidUtilities.displaySize;
        this.maxWallpaperSize = Math.min(1920, Math.max(point.x, point.y));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.textPaint = new TextPaint(1);
        this.textPaint.setColor(-1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.checkPaint = new Paint(1);
        this.checkPaint.setStyle(Style.STROKE);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.checkPaint.setColor(0);
        this.checkPaint.setStrokeCap(Cap.ROUND);
        this.checkPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.eraserPaint = new Paint(1);
        this.eraserPaint.setColor(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.backgroundPaint = new Paint(1);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        Bitmap bitmap = this.blurredBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.blurredBitmap = null;
        }
        Theme.applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x01a3  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0181  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x01be A:{LOOP_END, LOOP:0: B:27:0x01bb->B:29:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x027d  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x029b  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0547  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0554  */
    public android.view.View createView(android.content.Context r33) {
        /*
        r32 = this;
        r6 = r32;
        r7 = r33;
        r0 = r6.actionBar;
        r1 = NUM; // 0x7var_f0 float:1.7945065E38 double:1.0529356216E-314;
        r0.setBackButtonImage(r1);
        r0 = r6.actionBar;
        r8 = 1;
        r0.setAllowOverlayTitle(r8);
        r0 = r6.actionBar;
        r1 = "BackgroundPreview";
        r2 = NUM; // 0x7f0e01c4 float:1.8875954E38 double:1.05316238E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r0.setTitle(r1);
        r0 = r6.actionBar;
        r1 = new org.telegram.ui.WallpaperActivity$1;
        r1.<init>();
        r0.setActionBarMenuOnItemClick(r1);
        r0 = r6.currentWallpaper;
        r1 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r1 != 0) goto L_0x0034;
    L_0x0030:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper;
        if (r0 == 0) goto L_0x0040;
    L_0x0034:
        r0 = r6.actionBar;
        r0 = r0.createMenu();
        r1 = NUM; // 0x7var_ float:1.7945176E38 double:1.052935649E-314;
        r0.addItem(r8, r1);
    L_0x0040:
        r9 = new android.widget.FrameLayout;
        r9.<init>(r7);
        r6.fragmentView = r9;
        r6.hasOwnBackground = r8;
        r0 = new org.telegram.ui.WallpaperActivity$2;
        r0.<init>(r7);
        r6.backgroundImage = r0;
        r0 = r6.currentWallpaper;
        r0 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        r10 = 2;
        r11 = 0;
        if (r0 == 0) goto L_0x006b;
    L_0x0058:
        r0 = 3;
        r1 = r6.patterns;
        if (r1 == 0) goto L_0x005f;
    L_0x005d:
        r1 = 0;
        goto L_0x0060;
    L_0x005f:
        r1 = 2;
    L_0x0060:
        r2 = r6.patterns;
        if (r2 != 0) goto L_0x006d;
    L_0x0064:
        r2 = r6.selectedPattern;
        if (r2 == 0) goto L_0x0069;
    L_0x0068:
        goto L_0x006d;
    L_0x0069:
        r2 = 0;
        goto L_0x006e;
    L_0x006b:
        r0 = 2;
        r1 = 0;
    L_0x006d:
        r2 = 1;
    L_0x006e:
        r3 = r6.backgroundImage;
        r12 = -1;
        r13 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r14 = 51;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r18 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18);
        r9.addView(r3, r4);
        r3 = r6.backgroundImage;
        r3 = r3.getImageReceiver();
        r4 = new org.telegram.ui.-$$Lambda$WallpaperActivity$DLrQAr4_n4zrBNqOkPDVZ2ql9yg;
        r4.<init>(r6);
        r3.setDelegate(r4);
        r3 = new org.telegram.ui.Components.RadialProgress2;
        r4 = r6.backgroundImage;
        r3.<init>(r4);
        r6.radialProgress = r3;
        r3 = r6.radialProgress;
        r4 = "chat_serviceText";
        r5 = "chat_serviceBackground";
        r3.setColors(r5, r5, r4, r4);
        r3 = new org.telegram.ui.Components.RecyclerListView;
        r3.<init>(r7);
        r6.listView = r3;
        r3 = r6.listView;
        r4 = new androidx.recyclerview.widget.LinearLayoutManager;
        r4.<init>(r7, r8, r8);
        r3.setLayoutManager(r4);
        r3 = r6.listView;
        r3.setOverScrollMode(r10);
        r3 = r6.listView;
        r4 = new org.telegram.ui.WallpaperActivity$ListAdapter;
        r4.<init>(r7);
        r3.setAdapter(r4);
        r3 = r6.listView;
        if (r2 == 0) goto L_0x00ca;
    L_0x00c7:
        r4 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        goto L_0x00cc;
    L_0x00ca:
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x00cc:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3.setPadding(r11, r11, r11, r4);
        r3 = r6.listView;
        r12 = -1;
        r13 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r14 = 51;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r18 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18);
        r9.addView(r3, r4);
        r3 = new org.telegram.ui.WallpaperActivity$3;
        r3.<init>(r7);
        r6.bottomOverlayChat = r3;
        r3 = r6.bottomOverlayChat;
        r3.setWillNotDraw(r11);
        r3 = r6.bottomOverlayChat;
        r12 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r3.setPadding(r11, r4, r11, r11);
        r3 = r6.bottomOverlayChat;
        r13 = 80;
        r15 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r14, r13);
        r9.addView(r3, r4);
        r3 = r6.bottomOverlayChat;
        r4 = new org.telegram.ui.-$$Lambda$WallpaperActivity$1kBuywjigfsWiHQkwiP2IyEMOss;
        r4.<init>(r6);
        r3.setOnClickListener(r4);
        r3 = new android.widget.TextView;
        r3.<init>(r7);
        r6.bottomOverlayChatText = r3;
        r3 = r6.bottomOverlayChatText;
        r5 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r3.setTextSize(r8, r5);
        r3 = r6.bottomOverlayChatText;
        r16 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r16);
        r3.setTypeface(r4);
        r3 = r6.bottomOverlayChatText;
        r17 = "chat_fieldOverlayText";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r3.setTextColor(r4);
        r3 = r6.bottomOverlayChatText;
        r4 = NUM; // 0x7f0e0a0b float:1.8880252E38 double:1.053163427E-314;
        r5 = "SetBackground";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r3.setText(r4);
        r3 = r6.bottomOverlayChat;
        r4 = r6.bottomOverlayChatText;
        r5 = 17;
        r13 = -2;
        r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r13, r5);
        r3.addView(r4, r12);
        r3 = new android.widget.FrameLayout;
        r3.<init>(r7);
        r6.buttonsContainer = r3;
        r3 = r6.buttonsContainer;
        r19 = -2;
        r20 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r21 = 81;
        r22 = 0;
        r23 = 0;
        r24 = 0;
        r25 = NUM; // 0x42840000 float:66.0 double:5.51351079E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r9.addView(r3, r4);
        r3 = new java.lang.String[r0];
        r4 = new int[r0];
        r12 = new org.telegram.ui.WallpaperActivity.CheckBoxView[r0];
        r6.checkBoxView = r12;
        r12 = r6.currentWallpaper;
        r12 = r12 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r12 == 0) goto L_0x01a3;
    L_0x0181:
        r12 = NUM; // 0x7f0e01bb float:1.8875936E38 double:1.0531623755E-314;
        r5 = "BackgroundColor";
        r5 = org.telegram.messenger.LocaleController.getString(r5, r12);
        r3[r11] = r5;
        r5 = NUM; // 0x7f0e01c3 float:1.8875952E38 double:1.0531623795E-314;
        r12 = "BackgroundPattern";
        r5 = org.telegram.messenger.LocaleController.getString(r12, r5);
        r3[r8] = r5;
        r5 = NUM; // 0x7f0e01c2 float:1.887595E38 double:1.053162379E-314;
        r12 = "BackgroundMotion";
        r5 = org.telegram.messenger.LocaleController.getString(r12, r5);
        r3[r10] = r5;
        goto L_0x01b9;
    L_0x01a3:
        r5 = NUM; // 0x7f0e01b9 float:1.8875932E38 double:1.0531623745E-314;
        r12 = "BackgroundBlurred";
        r5 = org.telegram.messenger.LocaleController.getString(r12, r5);
        r3[r11] = r5;
        r5 = NUM; // 0x7f0e01c2 float:1.887595E38 double:1.053162379E-314;
        r12 = "BackgroundMotion";
        r5 = org.telegram.messenger.LocaleController.getString(r12, r5);
        r3[r8] = r5;
    L_0x01b9:
        r5 = 0;
        r12 = 0;
    L_0x01bb:
        r15 = r3.length;
        if (r5 >= r15) goto L_0x01d9;
    L_0x01be:
        r15 = r6.textPaint;
        r14 = r3[r5];
        r14 = r15.measureText(r14);
        r14 = (double) r14;
        r14 = java.lang.Math.ceil(r14);
        r14 = (int) r14;
        r4[r5] = r14;
        r14 = r4[r5];
        r12 = java.lang.Math.max(r12, r14);
        r5 = r5 + 1;
        r14 = 51;
        goto L_0x01bb;
    L_0x01d9:
        r5 = r1;
    L_0x01da:
        if (r5 >= r0) goto L_0x027b;
    L_0x01dc:
        r14 = r6.checkBoxView;
        r15 = new org.telegram.ui.WallpaperActivity$CheckBoxView;
        r13 = r6.currentWallpaper;
        r13 = r13 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r13 == 0) goto L_0x01eb;
    L_0x01e6:
        if (r5 == 0) goto L_0x01e9;
    L_0x01e8:
        goto L_0x01eb;
    L_0x01e9:
        r13 = 0;
        goto L_0x01ec;
    L_0x01eb:
        r13 = 1;
    L_0x01ec:
        r15.<init>(r7, r13);
        r14[r5] = r15;
        r13 = r6.checkBoxView;
        r13 = r13[r5];
        r14 = r3[r5];
        r15 = r4[r5];
        r13.setText(r14, r15, r12);
        r13 = r6.currentWallpaper;
        r13 = r13 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r13 == 0) goto L_0x021f;
    L_0x0202:
        if (r5 != r8) goto L_0x0213;
    L_0x0204:
        r13 = r6.checkBoxView;
        r13 = r13[r5];
        r14 = r6.selectedPattern;
        if (r14 == 0) goto L_0x020e;
    L_0x020c:
        r14 = 1;
        goto L_0x020f;
    L_0x020e:
        r14 = 0;
    L_0x020f:
        r13.setChecked(r14, r11);
        goto L_0x022d;
    L_0x0213:
        if (r5 != r10) goto L_0x022d;
    L_0x0215:
        r13 = r6.checkBoxView;
        r13 = r13[r5];
        r14 = r6.isMotion;
        r13.setChecked(r14, r11);
        goto L_0x022d;
    L_0x021f:
        r13 = r6.checkBoxView;
        r13 = r13[r5];
        if (r5 != 0) goto L_0x0228;
    L_0x0225:
        r14 = r6.isBlurred;
        goto L_0x022a;
    L_0x0228:
        r14 = r6.isMotion;
    L_0x022a:
        r13.setChecked(r14, r11);
    L_0x022d:
        r13 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = r13 + r12;
        r14 = new android.widget.FrameLayout$LayoutParams;
        r15 = -2;
        r14.<init>(r15, r13);
        r15 = 51;
        r14.gravity = r15;
        if (r5 != r8) goto L_0x0248;
    L_0x0240:
        r15 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r13 = r13 + r15;
        goto L_0x0249;
    L_0x0248:
        r13 = 0;
    L_0x0249:
        r14.leftMargin = r13;
        r13 = r6.buttonsContainer;
        r15 = r6.checkBoxView;
        r15 = r15[r5];
        r13.addView(r15, r14);
        r13 = r6.checkBoxView;
        r14 = r13[r5];
        r13 = r13[r5];
        r15 = new org.telegram.ui.-$$Lambda$WallpaperActivity$1-TCmIiY1WoqCaRaze9MxJF8Hq4;
        r15.<init>(r6, r5, r14);
        r13.setOnClickListener(r15);
        if (r1 != 0) goto L_0x0276;
    L_0x0264:
        if (r5 != r10) goto L_0x0276;
    L_0x0266:
        r13 = r6.checkBoxView;
        r13 = r13[r5];
        r14 = 0;
        r13.setAlpha(r14);
        r13 = r6.checkBoxView;
        r13 = r13[r5];
        r14 = 4;
        r13.setVisibility(r14);
    L_0x0276:
        r5 = r5 + 1;
        r13 = -2;
        goto L_0x01da;
    L_0x027b:
        if (r2 != 0) goto L_0x0284;
    L_0x027d:
        r0 = r6.buttonsContainer;
        r1 = 8;
        r0.setVisibility(r1);
    L_0x0284:
        r0 = new org.telegram.ui.Components.WallpaperParallaxEffect;
        r0.<init>(r7);
        r6.parallaxEffect = r0;
        r0 = r6.parallaxEffect;
        r1 = new org.telegram.ui.-$$Lambda$WallpaperActivity$PNT8mBRP7gH3N8qVf5B9a9cTbAY;
        r1.<init>(r6);
        r0.setCallback(r1);
        r0 = r6.currentWallpaper;
        r0 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r0 == 0) goto L_0x052c;
    L_0x029b:
        r6.isBlurred = r11;
        r12 = new android.graphics.Rect;
        r12.<init>();
        r0 = r33.getResources();
        r1 = NUM; // 0x7var_ float:1.794592E38 double:1.05293583E-314;
        r0 = r0.getDrawable(r1);
        r13 = r0.mutate();
        r13.getPadding(r12);
        r0 = new android.graphics.PorterDuffColorFilter;
        r1 = "windowBackgroundWhite";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r2 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r0.<init>(r1, r2);
        r13.setColorFilter(r0);
        r14 = 0;
    L_0x02c6:
        if (r14 >= r10) goto L_0x052c;
    L_0x02c8:
        r15 = r6.patternLayout;
        r23 = new org.telegram.ui.WallpaperActivity$4;
        r0 = r23;
        r1 = r32;
        r2 = r33;
        r3 = r14;
        r4 = r13;
        r10 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r5 = r12;
        r0.<init>(r2, r3, r4, r5);
        r15[r14] = r23;
        r0 = r6.patternLayout;
        r0 = r0[r14];
        r1 = 4;
        r0.setVisibility(r1);
        r0 = r6.patternLayout;
        r0 = r0[r14];
        r0.setWillNotDraw(r11);
        if (r14 != 0) goto L_0x02f0;
    L_0x02ed:
        r0 = 342; // 0x156 float:4.79E-43 double:1.69E-321;
        goto L_0x02f2;
    L_0x02f0:
        r0 = 242; // 0xf2 float:3.39E-43 double:1.196E-321;
    L_0x02f2:
        r1 = 83;
        r2 = -1;
        r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r0, r1);
        if (r14 != 0) goto L_0x0319;
    L_0x02fb:
        r1 = r0.height;
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r12.top;
        r2 = r2 + r3;
        r1 = r1 + r2;
        r0.height = r1;
        r1 = r6.patternLayout;
        r1 = r1[r14];
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r12.top;
        r2 = r2 + r3;
        r1.setPadding(r11, r2, r11, r11);
    L_0x0319:
        r1 = r6.patternLayout;
        r1 = r1[r14];
        r9.addView(r1, r0);
        r0 = r6.patternsButtonsContainer;
        r1 = new org.telegram.ui.WallpaperActivity$5;
        r1.<init>(r7);
        r0[r14] = r1;
        r0 = r6.patternsButtonsContainer;
        r0 = r0[r14];
        r0.setWillNotDraw(r11);
        r0 = r6.patternsButtonsContainer;
        r0 = r0[r14];
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.setPadding(r11, r2, r11, r11);
        r0 = r6.patternLayout;
        r0 = r0[r14];
        r2 = r6.patternsButtonsContainer;
        r2 = r2[r14];
        r3 = 80;
        r4 = 51;
        r5 = -1;
        r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4, r3);
        r0.addView(r2, r15);
        r0 = r6.patternsCancelButton;
        r2 = new android.widget.TextView;
        r2.<init>(r7);
        r0[r14] = r2;
        r0 = r6.patternsCancelButton;
        r0 = r0[r14];
        r0.setTextSize(r8, r10);
        r0 = r6.patternsCancelButton;
        r0 = r0[r14];
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r16);
        r0.setTypeface(r2);
        r0 = r6.patternsCancelButton;
        r0 = r0[r14];
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r0.setTextColor(r2);
        r0 = r6.patternsCancelButton;
        r0 = r0[r14];
        r2 = NUM; // 0x7f0e0211 float:1.887611E38 double:1.053162418E-314;
        r4 = "Cancel";
        r2 = org.telegram.messenger.LocaleController.getString(r4, r2);
        r2 = r2.toUpperCase();
        r0.setText(r2);
        r0 = r6.patternsCancelButton;
        r0 = r0[r14];
        r2 = 17;
        r0.setGravity(r2);
        r0 = r6.patternsCancelButton;
        r0 = r0[r14];
        r2 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.setPadding(r4, r11, r5, r11);
        r0 = r6.patternsCancelButton;
        r0 = r0[r14];
        r4 = "listSelectorSDK21";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4, r11);
        r0.setBackgroundDrawable(r4);
        r0 = r6.patternsButtonsContainer;
        r0 = r0[r14];
        r4 = r6.patternsCancelButton;
        r4 = r4[r14];
        r1 = -1;
        r5 = 51;
        r15 = -2;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r1, r5);
        r0.addView(r4, r3);
        r0 = r6.patternsCancelButton;
        r0 = r0[r14];
        r1 = new org.telegram.ui.-$$Lambda$WallpaperActivity$25btyyl2ojPzi3umZx_yVj0JTsE;
        r1.<init>(r6, r14);
        r0.setOnClickListener(r1);
        r0 = r6.patternsSaveButton;
        r1 = new android.widget.TextView;
        r1.<init>(r7);
        r0[r14] = r1;
        r0 = r6.patternsSaveButton;
        r0 = r0[r14];
        r0.setTextSize(r8, r10);
        r0 = r6.patternsSaveButton;
        r0 = r0[r14];
        r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r16);
        r0.setTypeface(r1);
        r0 = r6.patternsSaveButton;
        r0 = r0[r14];
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r0.setTextColor(r1);
        r0 = r6.patternsSaveButton;
        r0 = r0[r14];
        r1 = NUM; // 0x7f0e098e float:1.8879999E38 double:1.053163365E-314;
        r3 = "Save";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r1 = r1.toUpperCase();
        r0.setText(r1);
        r0 = r6.patternsSaveButton;
        r0 = r0[r14];
        r1 = 17;
        r0.setGravity(r1);
        r0 = r6.patternsSaveButton;
        r0 = r0[r14];
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.setPadding(r3, r11, r2, r11);
        r0 = r6.patternsSaveButton;
        r0 = r0[r14];
        r2 = "listSelectorSDK21";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r11);
        r0.setBackgroundDrawable(r2);
        r0 = r6.patternsButtonsContainer;
        r0 = r0[r14];
        r2 = r6.patternsSaveButton;
        r2 = r2[r14];
        r3 = 53;
        r4 = -2;
        r15 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r15, r3);
        r0.addView(r2, r3);
        r0 = r6.patternsSaveButton;
        r0 = r0[r14];
        r2 = new org.telegram.ui.-$$Lambda$WallpaperActivity$rqsIbe4XDaEpSOIXXh5TVdi7z9A;
        r2.<init>(r6, r14);
        r0.setOnClickListener(r2);
        if (r14 != r8) goto L_0x0500;
    L_0x0459:
        r0 = new org.telegram.ui.WallpaperActivity$6;
        r0.<init>(r7);
        r6.patternsListView = r0;
        r0 = r6.patternsListView;
        r2 = new androidx.recyclerview.widget.LinearLayoutManager;
        r2.<init>(r7, r11, r11);
        r6.patternsLayoutManager = r2;
        r0.setLayoutManager(r2);
        r0 = r6.patternsListView;
        r2 = new org.telegram.ui.WallpaperActivity$PatternsAdapter;
        r2.<init>(r7);
        r6.patternsAdapter = r2;
        r0.setAdapter(r2);
        r0 = r6.patternsListView;
        r2 = new org.telegram.ui.WallpaperActivity$7;
        r2.<init>();
        r0.addItemDecoration(r2);
        r0 = r6.patternLayout;
        r0 = r0[r14];
        r2 = r6.patternsListView;
        r25 = -1;
        r26 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r27 = 51;
        r28 = 0;
        r29 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r30 = 0;
        r31 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31);
        r0.addView(r2, r3);
        r0 = r6.patternsListView;
        r2 = new org.telegram.ui.-$$Lambda$WallpaperActivity$yS3bJtjNmGQ30kYj94XwI29Szdc;
        r2.<init>(r6);
        r0.setOnItemClickListener(r2);
        r0 = new org.telegram.ui.Cells.HeaderCell;
        r0.<init>(r7);
        r6.intensityCell = r0;
        r0 = r6.intensityCell;
        r2 = NUM; // 0x7f0e01c1 float:1.8875948E38 double:1.0531623785E-314;
        r3 = "BackgroundIntensity";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        r0 = r6.patternLayout;
        r0 = r0[r14];
        r2 = r6.intensityCell;
        r26 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r29 = NUM; // 0x42e20000 float:113.0 double:5.543947133E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31);
        r0.addView(r2, r3);
        r0 = new org.telegram.ui.WallpaperActivity$8;
        r0.<init>(r7);
        r6.intensitySeekBar = r0;
        r0 = r6.intensitySeekBar;
        r2 = r6.currentIntensity;
        r0.setProgress(r2);
        r0 = r6.intensitySeekBar;
        r0.setReportChanges(r8);
        r0 = r6.intensitySeekBar;
        r2 = new org.telegram.ui.WallpaperActivity$9;
        r2.<init>();
        r0.setDelegate(r2);
        r0 = r6.patternLayout;
        r0 = r0[r14];
        r2 = r6.intensitySeekBar;
        r26 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r28 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r29 = NUM; // 0x43190000 float:153.0 double:5.56175563E-315;
        r30 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31);
        r0.addView(r2, r3);
        goto L_0x0527;
    L_0x0500:
        r0 = new org.telegram.ui.Components.ColorPicker;
        r2 = new org.telegram.ui.-$$Lambda$WallpaperActivity$p0o1Hwb9gTd7jrru2NyNxNi1jhs;
        r2.<init>(r6);
        r0.<init>(r7, r2);
        r6.colorPicker = r0;
        r0 = r6.patternLayout;
        r0 = r0[r14];
        r2 = r6.colorPicker;
        r25 = -1;
        r26 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r27 = 1;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r31 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31);
        r0.addView(r2, r3);
    L_0x0527:
        r14 = r14 + 1;
        r10 = 2;
        goto L_0x02c6;
    L_0x052c:
        r6.setCurrentImage(r8);
        r1 = r6.radialProgress;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r0 = r32;
        r3 = r32;
        r0.updateButtonState(r1, r2, r3, r4, r5);
        r0 = r6.backgroundImage;
        r0 = r0.getImageReceiver();
        r0 = r0.hasBitmapImage();
        if (r0 != 0) goto L_0x054e;
    L_0x0547:
        r0 = r6.fragmentView;
        r1 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r0.setBackgroundColor(r1);
    L_0x054e:
        r0 = r6.currentWallpaper;
        r0 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r0 != 0) goto L_0x0566;
    L_0x0554:
        r0 = r6.backgroundImage;
        r0 = r0.getImageReceiver();
        r0.setCrossfadeWithOldImage(r8);
        r0 = r6.backgroundImage;
        r0 = r0.getImageReceiver();
        r0.setForceCrossfade(r8);
    L_0x0566:
        r0 = r6.fragmentView;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpaperActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ void lambda$createView$0$WallpaperActivity(ImageReceiver imageReceiver, boolean z, boolean z2) {
        if (!(this.currentWallpaper instanceof ColorWallpaper)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (z && drawable != null) {
                Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable));
                this.listView.invalidateViews();
                int childCount = this.buttonsContainer.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    this.buttonsContainer.getChildAt(i).invalidate();
                }
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    String str = "chat_serviceText";
                    String str2 = "chat_serviceBackground";
                    radialProgress2.setColors(str2, str2, str, str);
                }
                if (!z2 && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x0118 A:{SYNTHETIC, Splitter:B:60:0x0118} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x013d  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01fd  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0210  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x028c  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0118 A:{SYNTHETIC, Splitter:B:60:0x0118} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x013d  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01fd  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0210  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x028c  */
    public /* synthetic */ void lambda$createView$1$WallpaperActivity(android.view.View r33) {
        /*
        r32 = this;
        r1 = r32;
        r2 = new java.io.File;
        r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r3 = r1.isBlurred;
        r4 = "wallpaper.jpg";
        if (r3 == 0) goto L_0x0013;
    L_0x000f:
        r3 = "wallpaper_original.jpg";
        goto L_0x0014;
    L_0x0013:
        r3 = r4;
    L_0x0014:
        r2.<init>(r0, r3);
        r0 = r1.currentWallpaper;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper;
        r5 = "jpg";
        r6 = -2;
        r8 = 87;
        r10 = 1;
        if (r3 == 0) goto L_0x005a;
    L_0x0024:
        r0 = r1.backgroundImage;	 Catch:{ Exception -> 0x003d }
        r0 = r0.getImageReceiver();	 Catch:{ Exception -> 0x003d }
        r0 = r0.getBitmap();	 Catch:{ Exception -> 0x003d }
        r3 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x003d }
        r3.<init>(r2);	 Catch:{ Exception -> 0x003d }
        r11 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x003d }
        r0.compress(r11, r8, r3);	 Catch:{ Exception -> 0x003d }
        r3.close();	 Catch:{ Exception -> 0x003d }
        r0 = 1;
        goto L_0x0042;
    L_0x003d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x0042:
        if (r0 != 0) goto L_0x00ba;
    L_0x0044:
        r0 = r1.currentWallpaper;
        r0 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r0;
        r0 = r0.document;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r10);
        r0 = org.telegram.messenger.AndroidUtilities.copyFile(r0, r2);	 Catch:{ Exception -> 0x0053 }
        goto L_0x00ba;
    L_0x0053:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0112;
    L_0x005a:
        r3 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r3 == 0) goto L_0x00bc;
    L_0x005e:
        r3 = r1.selectedPattern;
        if (r3 == 0) goto L_0x00b9;
    L_0x0062:
        r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0;	 Catch:{ all -> 0x00b4 }
        r0 = r1.backgroundImage;	 Catch:{ all -> 0x00b4 }
        r0 = r0.getImageReceiver();	 Catch:{ all -> 0x00b4 }
        r0 = r0.getBitmap();	 Catch:{ all -> 0x00b4 }
        r3 = r0.getWidth();	 Catch:{ all -> 0x00b4 }
        r11 = r0.getHeight();	 Catch:{ all -> 0x00b4 }
        r12 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x00b4 }
        r3 = android.graphics.Bitmap.createBitmap(r3, r11, r12);	 Catch:{ all -> 0x00b4 }
        r11 = new android.graphics.Canvas;	 Catch:{ all -> 0x00b4 }
        r11.<init>(r3);	 Catch:{ all -> 0x00b4 }
        r12 = r1.backgroundColor;	 Catch:{ all -> 0x00b4 }
        r11.drawColor(r12);	 Catch:{ all -> 0x00b4 }
        r12 = new android.graphics.Paint;	 Catch:{ all -> 0x00b4 }
        r13 = 2;
        r12.<init>(r13);	 Catch:{ all -> 0x00b4 }
        r13 = new android.graphics.PorterDuffColorFilter;	 Catch:{ all -> 0x00b4 }
        r14 = r1.patternColor;	 Catch:{ all -> 0x00b4 }
        r15 = r1.blendMode;	 Catch:{ all -> 0x00b4 }
        r13.<init>(r14, r15);	 Catch:{ all -> 0x00b4 }
        r12.setColorFilter(r13);	 Catch:{ all -> 0x00b4 }
        r13 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r14 = r1.currentIntensity;	 Catch:{ all -> 0x00b4 }
        r14 = r14 * r13;
        r13 = (int) r14;	 Catch:{ all -> 0x00b4 }
        r12.setAlpha(r13);	 Catch:{ all -> 0x00b4 }
        r13 = 0;
        r11.drawBitmap(r0, r13, r13, r12);	 Catch:{ all -> 0x00b4 }
        r0 = new java.io.FileOutputStream;	 Catch:{ all -> 0x00b4 }
        r0.<init>(r2);	 Catch:{ all -> 0x00b4 }
        r11 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x00b4 }
        r3.compress(r11, r8, r0);	 Catch:{ all -> 0x00b4 }
        r0.close();	 Catch:{ all -> 0x00b4 }
        goto L_0x00b9;
    L_0x00b4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0112;
    L_0x00b9:
        r0 = 1;
    L_0x00ba:
        r3 = 0;
        goto L_0x0114;
    L_0x00bc:
        r3 = r0 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper;
        if (r3 == 0) goto L_0x00eb;
    L_0x00c0:
        r0 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r0;
        r3 = r0.resId;
        if (r3 != 0) goto L_0x00b9;
    L_0x00c6:
        r11 = (long) r3;
        r3 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1));
        if (r3 != 0) goto L_0x00cc;
    L_0x00cb:
        goto L_0x00b9;
    L_0x00cc:
        r3 = r0.originalPath;	 Catch:{ Exception -> 0x00e4 }
        if (r3 == 0) goto L_0x00d3;
    L_0x00d0:
        r0 = r0.originalPath;	 Catch:{ Exception -> 0x00e4 }
        goto L_0x00d5;
    L_0x00d3:
        r0 = r0.path;	 Catch:{ Exception -> 0x00e4 }
    L_0x00d5:
        r3 = r0.equals(r2);	 Catch:{ Exception -> 0x00e4 }
        if (r3 == 0) goto L_0x00dd;
    L_0x00db:
        r0 = 1;
        goto L_0x0114;
    L_0x00dd:
        r0 = org.telegram.messenger.AndroidUtilities.copyFile(r0, r2);	 Catch:{ Exception -> 0x00e2 }
        goto L_0x0114;
    L_0x00e2:
        r0 = move-exception;
        goto L_0x00e6;
    L_0x00e4:
        r0 = move-exception;
        r3 = 0;
    L_0x00e6:
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
        goto L_0x0114;
    L_0x00eb:
        r3 = r0 instanceof org.telegram.messenger.MediaController.SearchImage;
        if (r3 == 0) goto L_0x0112;
    L_0x00ef:
        r0 = (org.telegram.messenger.MediaController.SearchImage) r0;
        r3 = r0.photo;
        if (r3 == 0) goto L_0x0102;
    L_0x00f5:
        r0 = r3.sizes;
        r3 = r1.maxWallpaperSize;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3, r10);
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r10);
        goto L_0x0108;
    L_0x0102:
        r0 = r0.imageUrl;
        r0 = org.telegram.messenger.ImageLoader.getHttpFilePath(r0, r5);
    L_0x0108:
        r0 = org.telegram.messenger.AndroidUtilities.copyFile(r0, r2);	 Catch:{ Exception -> 0x010d }
        goto L_0x00ba;
    L_0x010d:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0112:
        r0 = 0;
        goto L_0x00ba;
    L_0x0114:
        r11 = r1.isBlurred;
        if (r11 == 0) goto L_0x0137;
    L_0x0118:
        r0 = new java.io.File;	 Catch:{ all -> 0x0132 }
        r11 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x0132 }
        r0.<init>(r11, r4);	 Catch:{ all -> 0x0132 }
        r4 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0132 }
        r4.<init>(r0);	 Catch:{ all -> 0x0132 }
        r0 = r1.blurredBitmap;	 Catch:{ all -> 0x0132 }
        r11 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x0132 }
        r0.compress(r11, r8, r4);	 Catch:{ all -> 0x0132 }
        r4.close();	 Catch:{ all -> 0x0132 }
        r0 = 1;
        goto L_0x0137;
    L_0x0132:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x0137:
        r4 = r1.currentWallpaper;
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper;
        if (r8 == 0) goto L_0x0155;
    L_0x013d:
        r4 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r4;
        r12 = r4.id;
        r16 = r12;
        r11 = r4.access_hash;
        r4 = r4.slug;
        r23 = r11;
        r12 = r16;
        r20 = r12;
        r5 = 0;
        r6 = 1;
        r14 = 0;
        r19 = 0;
        goto L_0x01e8;
    L_0x0155:
        r8 = r4 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r8 == 0) goto L_0x01a1;
    L_0x0159:
        r4 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r4;
        r5 = r1.selectedPattern;
        if (r5 == 0) goto L_0x018d;
    L_0x015f:
        r12 = r5.id;
        r6 = r5.access_hash;
        r14 = r4.id;
        r9 = r4.patternId;
        r5 = (r14 > r9 ? 1 : (r14 == r9 ? 0 : -1));
        if (r5 != 0) goto L_0x0180;
    L_0x016b:
        r5 = r1.backgroundColor;
        r9 = r4.color;
        if (r5 != r9) goto L_0x0180;
    L_0x0171:
        r4 = r4.intensity;
        r5 = r1.currentIntensity;
        r4 = r4 - r5;
        r5 = NUM; // 0x3a83126f float:0.001 double:4.85008663E-315;
        r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r4 > 0) goto L_0x0180;
    L_0x017d:
        r16 = r12;
        goto L_0x0182;
    L_0x0180:
        r16 = -1;
    L_0x0182:
        r4 = r1.selectedPattern;
        r14 = r4.id;
        r4 = r4.slug;
        r9 = r14;
        r14 = r12;
        r12 = r16;
        goto L_0x0196;
    L_0x018d:
        r4 = 0;
        r6 = 0;
        r9 = 0;
        r12 = -1;
        r14 = 0;
    L_0x0196:
        r5 = r1.backgroundColor;
        r23 = r6;
        r20 = r14;
        r6 = 1;
        r19 = 0;
        r14 = r9;
        goto L_0x01e8;
    L_0x01a1:
        r6 = r4 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper;
        if (r6 == 0) goto L_0x01b7;
    L_0x01a5:
        r4 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r4;
        r12 = r4.id;
        r4 = r4.path;
        r19 = r4;
        r4 = 0;
        r5 = 0;
        r6 = 1;
    L_0x01b0:
        r14 = 0;
    L_0x01b2:
        r20 = 0;
        r23 = 0;
        goto L_0x01e8;
    L_0x01b7:
        r6 = r4 instanceof org.telegram.messenger.MediaController.SearchImage;
        if (r6 == 0) goto L_0x01de;
    L_0x01bb:
        r4 = (org.telegram.messenger.MediaController.SearchImage) r4;
        r6 = r4.photo;
        if (r6 == 0) goto L_0x01cf;
    L_0x01c1:
        r4 = r6.sizes;
        r5 = r1.maxWallpaperSize;
        r6 = 1;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5, r6);
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r4, r6);
        goto L_0x01d6;
    L_0x01cf:
        r6 = 1;
        r4 = r4.imageUrl;
        r4 = org.telegram.messenger.ImageLoader.getHttpFilePath(r4, r5);
    L_0x01d6:
        r11 = r4;
        r19 = r11;
        r4 = 0;
        r5 = 0;
        r12 = -1;
        goto L_0x01b0;
    L_0x01de:
        r6 = 1;
        r4 = 0;
        r5 = 0;
        r12 = 0;
        r14 = 0;
        r19 = 0;
        goto L_0x01b2;
    L_0x01e8:
        r7 = r1.currentAccount;
        r18 = org.telegram.messenger.MessagesController.getInstance(r7);
        r7 = r1.isBlurred;
        r9 = r1.isMotion;
        r10 = r1.currentIntensity;
        r16 = 0;
        r11 = (r23 > r16 ? 1 : (r23 == r16 ? 0 : -1));
        if (r11 == 0) goto L_0x01fd;
    L_0x01fa:
        r29 = 1;
        goto L_0x01ff;
    L_0x01fd:
        r29 = 0;
    L_0x01ff:
        r30 = 0;
        r22 = r4;
        r25 = r7;
        r26 = r9;
        r27 = r5;
        r28 = r10;
        r18.saveWallpaperToServer(r19, r20, r22, r23, r25, r26, r27, r28, r29, r30);
        if (r0 == 0) goto L_0x0288;
    L_0x0210:
        r0 = "chat_serviceBackground";
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r0);
        org.telegram.ui.ActionBar.Theme.serviceMessageColorBackup = r0;
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r0 = r0.edit();
        r7 = "selectedBackground2";
        r0.putLong(r7, r12);
        r7 = android.text.TextUtils.isEmpty(r4);
        r9 = "selectedBackgroundSlug";
        if (r7 != 0) goto L_0x0231;
    L_0x022d:
        r0.putString(r9, r4);
        goto L_0x0234;
    L_0x0231:
        r0.remove(r9);
    L_0x0234:
        r4 = r1.isBlurred;
        r7 = "selectedBackgroundBlurred";
        r0.putBoolean(r7, r4);
        r4 = r1.isMotion;
        r7 = "selectedBackgroundMotion";
        r0.putBoolean(r7, r4);
        r4 = "selectedColor";
        r0.putInt(r4, r5);
        r4 = r1.currentIntensity;
        r5 = "selectedIntensity";
        r0.putFloat(r5, r4);
        r4 = "selectedPattern";
        r0.putLong(r4, r14);
        r4 = -2;
        r7 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1));
        if (r7 == 0) goto L_0x025a;
    L_0x0259:
        goto L_0x025b;
    L_0x025a:
        r6 = 0;
    L_0x025b:
        r4 = "overrideThemeWallpaper";
        r0.putBoolean(r4, r6);
        r0.commit();
        org.telegram.ui.ActionBar.Theme.reloadWallpaper();
        if (r3 != 0) goto L_0x0288;
    L_0x0268:
        r0 = org.telegram.messenger.ImageLoader.getInstance();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r2 = r2.getAbsolutePath();
        r2 = org.telegram.messenger.ImageLoader.getHttpFileName(r2);
        r3.append(r2);
        r2 = "@100_100";
        r3.append(r2);
        r2 = r3.toString();
        r0.removeImage(r2);
    L_0x0288:
        r0 = r1.delegate;
        if (r0 == 0) goto L_0x028f;
    L_0x028c:
        r0.didSetNewBackground();
    L_0x028f:
        r32.finishFragment();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpaperActivity.lambda$createView$1$WallpaperActivity(android.view.View):void");
    }

    public /* synthetic */ void lambda$createView$2$WallpaperActivity(int i, CheckBoxView checkBoxView, View view) {
        if (this.buttonsContainer.getAlpha() == 1.0f) {
            if (!(this.currentWallpaper instanceof ColorWallpaper)) {
                checkBoxView.setChecked(checkBoxView.isChecked() ^ 1, true);
                if (i == 0) {
                    this.isBlurred = checkBoxView.isChecked();
                    updateBlurred();
                } else {
                    this.isMotion = checkBoxView.isChecked();
                    this.parallaxEffect.setEnabled(this.isMotion);
                    animateMotionChange();
                }
            } else if (i == 2) {
                checkBoxView.setChecked(checkBoxView.isChecked() ^ 1, true);
                this.isMotion = checkBoxView.isChecked();
                this.parallaxEffect.setEnabled(this.isMotion);
                animateMotionChange();
            } else {
                boolean z = false;
                if (i == 1 && this.patternLayout[i].getVisibility() == 0) {
                    this.backgroundImage.setImageDrawable(null);
                    this.selectedPattern = null;
                    this.isMotion = false;
                    updateButtonState(this.radialProgress, null, this, false, true);
                    updateSelectedPattern(true);
                    this.checkBoxView[1].setChecked(false, true);
                    this.patternsListView.invalidateViews();
                }
                if (this.patternLayout[i].getVisibility() != 0) {
                    z = true;
                }
                showPatternsView(i, z);
            }
        }
    }

    public /* synthetic */ void lambda$createView$3$WallpaperActivity(int i, int i2) {
        if (this.isMotion) {
            float f = 1.0f;
            if (this.motionAnimation != null) {
                f = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            }
            this.backgroundImage.setTranslationX(((float) i) * f);
            this.backgroundImage.setTranslationY(((float) i2) * f);
        }
    }

    public /* synthetic */ void lambda$createView$4$WallpaperActivity(int i, View view) {
        if (i == 0) {
            setBackgroundColor(this.previousBackgroundColor, 0, true);
        } else {
            this.selectedPattern = this.previousSelectedPattern;
            TL_wallPaper tL_wallPaper = this.selectedPattern;
            if (tL_wallPaper == null) {
                this.backgroundImage.setImageDrawable(null);
            } else {
                BackupImageView backupImageView = this.backgroundImage;
                ImageLocation forDocument = ImageLocation.getForDocument(tL_wallPaper.document);
                String str = this.imageFilter;
                TL_wallPaper tL_wallPaper2 = this.selectedPattern;
                backupImageView.setImage(forDocument, str, null, null, "jpg", tL_wallPaper2.document.size, 1, tL_wallPaper2);
            }
            this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
            this.currentIntensity = this.previousIntensity;
            this.intensitySeekBar.setProgress(this.currentIntensity);
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            updateButtonState(this.radialProgress, null, this, false, true);
            updateSelectedPattern(true);
        }
        showPatternsView(i, false);
    }

    public /* synthetic */ void lambda$createView$5$WallpaperActivity(int i, View view) {
        showPatternsView(i, false);
    }

    public /* synthetic */ void lambda$createView$6$WallpaperActivity(View view, int i) {
        boolean z = false;
        Object obj = this.selectedPattern != null ? 1 : null;
        if (i == 0) {
            this.backgroundImage.setImageDrawable(null);
            this.selectedPattern = null;
            this.isMotion = false;
            updateButtonState(this.radialProgress, null, this, false, true);
        } else {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) this.patterns.get(i - 1);
            this.backgroundImage.setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, null, null, "jpg", tL_wallPaper.document.size, 1, tL_wallPaper);
            this.selectedPattern = tL_wallPaper;
            this.isMotion = this.checkBoxView[2].isChecked();
            updateButtonState(this.radialProgress, null, this, false, true);
        }
        if (obj == (this.selectedPattern == null ? 1 : null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        CheckBoxView checkBoxView = this.checkBoxView[1];
        if (this.selectedPattern != null) {
            z = true;
        }
        checkBoxView.setChecked(z, true);
        this.patternsListView.invalidateViews();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof FileWallpaper) {
                FileWallpaper fileWallpaper = (FileWallpaper) obj;
                if (fileWallpaper.id == -1) {
                    fileWallpaper.id = ((Long) objArr[0]).longValue();
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    public void onFailedDownload(String str, boolean z) {
        updateButtonState(this.radialProgress, null, this, true, z);
    }

    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, this.progressVisible);
        updateButtonState(this.radialProgress, null, this, false, true);
    }

    public void onProgressDownload(String str, float f) {
        this.radialProgress.setProgress(f, this.progressVisible);
        if (this.radialProgress.getIcon() != 10) {
            updateButtonState(this.radialProgress, null, this, false, true);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        Bitmap bitmap;
        if (this.isBlurred && this.blurredBitmap == null) {
            bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.blurredBitmap = Utilities.blurWallpaper(bitmap);
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
                }
            }
        }
        if (this.isBlurred) {
            bitmap = this.blurredBitmap;
            if (bitmap != null) {
                this.backgroundImage.setImageBitmap(bitmap);
                return;
            }
            return;
        }
        setCurrentImage(false);
    }

    private void updateButtonState(RadialProgress2 radialProgress2, Object obj, FileDownloadProgressListener fileDownloadProgressListener, boolean z, boolean z2) {
        TL_wallPaper tL_wallPaper;
        if (fileDownloadProgressListener == this) {
            tL_wallPaper = this.selectedPattern;
            if (tL_wallPaper == null) {
                tL_wallPaper = this.currentWallpaper;
            }
        } else {
            tL_wallPaper = obj;
        }
        boolean z3 = tL_wallPaper instanceof TL_wallPaper;
        int i = 4;
        if (z3 || (tL_wallPaper instanceof SearchImage)) {
            String attachFileName;
            File pathToAttach;
            int i2;
            if (obj == null && z2 && !this.progressVisible) {
                z2 = false;
            }
            if (z3) {
                tL_wallPaper = tL_wallPaper;
                attachFileName = FileLoader.getAttachFileName(tL_wallPaper.document);
                if (!TextUtils.isEmpty(attachFileName)) {
                    pathToAttach = FileLoader.getPathToAttach(tL_wallPaper.document, true);
                    i2 = tL_wallPaper.document.size;
                } else {
                    return;
                }
            }
            File pathToAttach2;
            String attachFileName2;
            SearchImage searchImage = (SearchImage) tL_wallPaper;
            Photo photo = searchImage.photo;
            if (photo != null) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, this.maxWallpaperSize, true);
                pathToAttach2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                attachFileName2 = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                i2 = closestPhotoSizeWithSize.size;
            } else {
                pathToAttach2 = ImageLoader.getHttpFilePath(searchImage.imageUrl, "jpg");
                attachFileName2 = pathToAttach2.getName();
                i2 = searchImage.size;
            }
            String str = attachFileName2;
            pathToAttach = pathToAttach2;
            attachFileName = str;
            if (TextUtils.isEmpty(attachFileName)) {
                return;
            }
            boolean exists = pathToAttach.exists();
            if (exists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(fileDownloadProgressListener);
                radialProgress2.setProgress(1.0f, z2);
                if (obj != null) {
                    i = 6;
                }
                radialProgress2.setIcon(i, z, z2);
                if (obj == null) {
                    this.backgroundImage.invalidate();
                    if (i2 != 0) {
                        this.actionBar.setSubtitle(AndroidUtilities.formatFileSize((long) i2));
                    } else {
                        this.actionBar.setSubtitle(null);
                    }
                }
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, null, fileDownloadProgressListener);
                FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
                Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                if (fileProgress != null) {
                    radialProgress2.setProgress(fileProgress.floatValue(), z2);
                } else {
                    radialProgress2.setProgress(0.0f, z2);
                }
                radialProgress2.setIcon(10, z, z2);
                if (obj == null) {
                    this.actionBar.setSubtitle(LocaleController.getString("LoadingFullImage", NUM));
                    this.backgroundImage.invalidate();
                }
            }
            if (obj == null) {
                float f = 0.5f;
                if (this.selectedPattern == null) {
                    this.buttonsContainer.setAlpha(exists ? 1.0f : 0.5f);
                }
                this.bottomOverlayChat.setEnabled(exists);
                TextView textView = this.bottomOverlayChatText;
                if (exists) {
                    f = 1.0f;
                }
                textView.setAlpha(f);
            }
        } else {
            if (fileDownloadProgressListener != this) {
                i = 6;
            }
            radialProgress2.setIcon(i, z, z2);
        }
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        Object obj = this.currentWallpaper;
        if (obj instanceof ColorWallpaper) {
            ColorWallpaper colorWallpaper = (ColorWallpaper) obj;
            if (colorWallpaper.patternId != 0) {
                int size = this.patterns.size();
                for (int i = 0; i < size; i++) {
                    TL_wallPaper tL_wallPaper = (TL_wallPaper) this.patterns.get(i);
                    if (tL_wallPaper.id == colorWallpaper.patternId) {
                        this.selectedPattern = tL_wallPaper;
                        break;
                    }
                }
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    private void updateSelectedPattern(boolean z) {
        int childCount = this.patternsListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.patternsListView.getChildAt(i);
            if (childAt instanceof PatternCell) {
                ((PatternCell) childAt).updateSelected(z);
            }
        }
    }

    private void updateMotionButton() {
        this.checkBoxView[this.selectedPattern != null ? 2 : 0].setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        Object obj = this.checkBoxView[2];
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = this.selectedPattern != null ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(obj, property, fArr);
        obj = this.checkBoxView[0];
        property = View.ALPHA;
        fArr = new float[1];
        if (this.selectedPattern != null) {
            f = 0.0f;
        }
        fArr[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(obj, property, fArr);
        animatorSet.playTogether(animatorArr);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                WallpaperActivity.this.checkBoxView[WallpaperActivity.this.selectedPattern != null ? 0 : 2].setVisibility(4);
            }
        });
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void showPatternsView(int i, boolean z) {
        int i2;
        ArrayList arrayList;
        int i3 = i;
        final boolean z2 = z && i3 == 1 && this.selectedPattern != null;
        if (z) {
            if (i3 == 0) {
                i2 = this.backgroundColor;
                this.previousBackgroundColor = i2;
                this.colorPicker.setColor(i2, 0);
            } else {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                arrayList = this.patterns;
                if (arrayList != null) {
                    TL_wallPaper tL_wallPaper = this.selectedPattern;
                    if (tL_wallPaper == null) {
                        i2 = 0;
                    } else {
                        i2 = arrayList.indexOf(tL_wallPaper) + 1;
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(i2, ((this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(100.0f)) - AndroidUtilities.dp(12.0f)) / 2);
                }
            }
        }
        this.checkBoxView[z2 ? 2 : 0].setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        arrayList = new ArrayList();
        int i4 = i3 == 0 ? 1 : 0;
        float f = 1.0f;
        if (z) {
            this.patternLayout[i3].setVisibility(0);
            arrayList.add(ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i3].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
            arrayList.add(ObjectAnimator.ofFloat(this.buttonsContainer, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i3].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
            Object obj = this.checkBoxView[2];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z2 ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
            obj = this.checkBoxView[0];
            property = View.ALPHA;
            fArr = new float[1];
            if (z2) {
                f = 0.0f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
            arrayList.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{0.0f}));
            if (this.patternLayout[i4].getVisibility() == 0) {
                arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i4], View.ALPHA, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.ALPHA, new float[]{0.0f, 1.0f}));
                this.patternLayout[i3].setTranslationY(0.0f);
            } else {
                arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.TRANSLATION_Y, new float[]{(float) this.patternLayout[i3].getMeasuredHeight(), 0.0f}));
            }
        } else {
            arrayList.add(ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.buttonsContainer, View.TRANSLATION_Y, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.TRANSLATION_Y, new float[]{(float) r11[i3].getMeasuredHeight()}));
            arrayList.add(ObjectAnimator.ofFloat(this.checkBoxView[0], View.ALPHA, new float[]{1.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.checkBoxView[2], View.ALPHA, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{1.0f}));
        }
        animatorSet.playTogether(arrayList);
        final boolean z3 = z;
        i2 = i4;
        i3 = i;
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (z3 && WallpaperActivity.this.patternLayout[i2].getVisibility() == 0) {
                    WallpaperActivity.this.patternLayout[i2].setAlpha(1.0f);
                    WallpaperActivity.this.patternLayout[i2].setVisibility(4);
                } else if (!z3) {
                    WallpaperActivity.this.patternLayout[i3].setVisibility(4);
                }
                WallpaperActivity.this.checkBoxView[z2 ? 0 : 2].setVisibility(4);
            }
        });
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.motionAnimation = new AnimatorSet();
        if (this.isMotion) {
            animatorSet = this.motionAnimation;
            Animator[] animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{this.parallaxScale});
            animatorArr[1] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{this.parallaxScale});
            animatorSet.playTogether(animatorArr);
        } else {
            animatorSet = this.motionAnimation;
            r4 = new Animator[4];
            r4[0] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{1.0f});
            r4[1] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{1.0f});
            r4[2] = ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, new float[]{0.0f});
            r4[3] = ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, new float[]{0.0f});
            animatorSet.playTogether(r4);
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                WallpaperActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    private void setBackgroundColor(int i, int i2, boolean z) {
        this.backgroundColor = i;
        this.backgroundImage.setBackgroundColor(this.backgroundColor);
        CheckBoxView[] checkBoxViewArr = this.checkBoxView;
        i2 = 0;
        if (checkBoxViewArr[0] != null) {
            checkBoxViewArr[0].invalidate();
        }
        this.patternColor = AndroidUtilities.getPatternColor(this.backgroundColor);
        r3 = new int[4];
        int i3 = this.patternColor;
        r3[0] = i3;
        r3[1] = i3;
        r3[2] = i3;
        r3[3] = i3;
        Theme.applyChatServiceMessageColor(r3);
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            this.backgroundImage.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout = this.buttonsContainer;
        if (frameLayout != null) {
            i = frameLayout.getChildCount();
            while (i2 < i) {
                this.buttonsContainer.getChildAt(i2).invalidate();
                i2++;
            }
        }
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            String str = "chat_serviceText";
            String str2 = "chat_serviceBackground";
            radialProgress2.setColors(str2, str2, str, str);
        }
    }

    private void setCurrentImage(boolean z) {
        Object obj = this.currentWallpaper;
        PhotoSize photoSize = null;
        if (obj instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
            if (z) {
                photoSize = FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, 100);
            }
            this.backgroundImage.setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, ImageLocation.getForDocument(photoSize, tL_wallPaper.document), "100_100_b", "jpg", tL_wallPaper.document.size, 1, tL_wallPaper);
        } else if (obj instanceof ColorWallpaper) {
            setBackgroundColor(((ColorWallpaper) obj).color, 0, true);
            TL_wallPaper tL_wallPaper2 = this.selectedPattern;
            if (tL_wallPaper2 != null) {
                BackupImageView backupImageView = this.backgroundImage;
                ImageLocation forDocument = ImageLocation.getForDocument(tL_wallPaper2.document);
                String str = this.imageFilter;
                TL_wallPaper tL_wallPaper3 = this.selectedPattern;
                backupImageView.setImage(forDocument, str, null, null, "jpg", tL_wallPaper3.document.size, 1, tL_wallPaper3);
            }
        } else if (obj instanceof FileWallpaper) {
            Bitmap bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.backgroundImage.setImageBitmap(bitmap);
                return;
            }
            FileWallpaper fileWallpaper = (FileWallpaper) obj;
            File file = fileWallpaper.originalPath;
            if (file != null) {
                this.backgroundImage.setImage(file.getAbsolutePath(), this.imageFilter, null);
                return;
            }
            file = fileWallpaper.path;
            if (file != null) {
                this.backgroundImage.setImage(file.getAbsolutePath(), this.imageFilter, null);
                return;
            }
            int i = fileWallpaper.resId;
            if (((long) i) == -2) {
                this.backgroundImage.setImageDrawable(Theme.getThemedWallpaper(false));
            } else if (i != 0) {
                this.backgroundImage.setImageResource(i);
            }
        } else if (obj instanceof SearchImage) {
            SearchImage searchImage = (SearchImage) obj;
            Photo photo = searchImage.photo;
            if (photo != null) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 100);
                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(searchImage.photo.sizes, this.maxWallpaperSize, true);
                if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                    closestPhotoSizeWithSize2 = null;
                }
                this.backgroundImage.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize2, searchImage.photo), this.imageFilter, ImageLocation.getForPhoto(closestPhotoSizeWithSize, searchImage.photo), "100_100_b", "jpg", closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.size : 0, 1, searchImage);
                return;
            }
            this.backgroundImage.setImage(searchImage.imageUrl, this.imageFilter, searchImage.thumbUrl, "100_100_b");
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        FrameLayout[] frameLayoutArr;
        TextView[] textViewArr;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        int i = 0;
        while (true) {
            frameLayoutArr = this.patternLayout;
            if (i >= frameLayoutArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(frameLayoutArr[i], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.patternLayout[i], 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
            i++;
        }
        i = 0;
        while (true) {
            frameLayoutArr = this.patternsButtonsContainer;
            if (i >= frameLayoutArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(frameLayoutArr[i], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i], 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
            i++;
        }
        arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
        arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
        arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
        i = 0;
        while (true) {
            textViewArr = this.patternsSaveButton;
            if (i >= textViewArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(textViewArr[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            i++;
        }
        i = 0;
        while (true) {
            textViewArr = this.patternsCancelButton;
            if (i >= textViewArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(textViewArr[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            i++;
        }
        ColorPicker colorPicker = this.colorPicker;
        if (colorPicker != null) {
            colorPicker.provideThemeDescriptions(arrayList);
        }
        arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, null, null, null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, null, null, null, "player_progress"));
        arrayList.add(new ThemeDescription(this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextIn"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextOut"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeSelectedText"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
