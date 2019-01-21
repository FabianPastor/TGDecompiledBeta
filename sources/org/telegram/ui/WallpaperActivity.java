package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_account_saveWallPaper;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputWallPaper;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.WallpapersListActivity.ColorWallpaper;
import org.telegram.ui.WallpapersListActivity.FileWallpaper;

public class WallpaperActivity extends BaseFragment implements FileDownloadProgressListener {
    private static final int share_item = 1;
    private int TAG;
    private BackupImageView backgroundImage;
    private Paint backgroundPaint;
    private Bitmap blurredBitmap;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private LinearLayout buttonsContainer;
    private Paint checkPaint;
    private Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    private Paint eraserPaint;
    private boolean isBlurred;
    private boolean isMotion;
    private RecyclerListView listView;
    private String loadingFile = null;
    private File loadingFileObject = null;
    private PhotoSize loadingSize = null;
    private AnimatorSet motionAnimation;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale = 1.0f;
    private boolean progressVisible;
    private RadialProgress2 radialProgress;
    private TextPaint textPaint;
    private Drawable themedWallpaper;
    private File wallpaperFile;

    private class CheckBoxView extends View {
        private static final float progressBounceDiff = 0.2f;
        public final Property<CheckBoxView, Float> PROGRESS_PROPERTY = new FloatProperty<CheckBoxView>("progress") {
            public void setValue(CheckBoxView object, float value) {
                CheckBoxView.this.progress = value;
                CheckBoxView.this.invalidate();
            }

            public Float get(CheckBoxView object) {
                return Float.valueOf(CheckBoxView.this.progress);
            }
        };
        private ObjectAnimator checkAnimator;
        private String currentText;
        private int currentTextSize;
        private Bitmap drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Config.ARGB_4444);
        private Canvas drawCanvas = new Canvas(this.drawBitmap);
        private boolean isChecked;
        private int maxTextSize;
        private float progress;
        private RectF rect = new RectF();

        public CheckBoxView(Context context) {
            super(context);
        }

        public void setText(String text, int current, int max) {
            this.currentText = text;
            this.currentTextSize = current;
            this.maxTextSize = max;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(this.maxTextSize + AndroidUtilities.dp(56.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
        }

        protected void onDraw(Canvas canvas) {
            float checkProgress;
            float bounceProgress;
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
            int x = ((getMeasuredWidth() - this.currentTextSize) - AndroidUtilities.dp(28.0f)) / 2;
            canvas.drawText(this.currentText, (float) (AndroidUtilities.dp(28.0f) + x), (float) AndroidUtilities.dp(21.0f), WallpaperActivity.this.textPaint);
            canvas.save();
            canvas.translate((float) x, (float) AndroidUtilities.dp(7.0f));
            if (this.progress <= 0.5f) {
                checkProgress = this.progress / 0.5f;
                bounceProgress = checkProgress;
            } else {
                bounceProgress = 2.0f - (this.progress / 0.5f);
                checkProgress = 1.0f;
            }
            float bounce = ((float) AndroidUtilities.dp(1.0f)) * bounceProgress;
            this.rect.set(bounce, bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce);
            this.drawBitmap.eraseColor(0);
            this.drawCanvas.drawRoundRect(this.rect, this.rect.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.backgroundPaint);
            if (checkProgress != 1.0f) {
                float rad = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * checkProgress) + bounce);
                this.rect.set(((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(16.0f)) - rad, ((float) AndroidUtilities.dp(16.0f)) - rad);
                this.drawCanvas.drawRoundRect(this.rect, this.rect.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.eraserPaint);
            }
            if (this.progress > 0.5f) {
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) - (((float) AndroidUtilities.dp(2.5f)) * (1.0f - bounceProgress)))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(2.5f)) * (1.0f - bounceProgress)))), WallpaperActivity.this.checkPaint);
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) + (((float) AndroidUtilities.dp(6.0f)) * (1.0f - bounceProgress)))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(6.0f)) * (1.0f - bounceProgress)))), WallpaperActivity.this.checkPaint);
            }
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
            canvas.restore();
        }

        private void setProgress(float value) {
            if (this.progress != value) {
                this.progress = value;
                invalidate();
            }
        }

        private void cancelCheckAnimator() {
            if (this.checkAnimator != null) {
                this.checkAnimator.cancel();
            }
        }

        private void animateToCheckedState(boolean newCheckedState) {
            Property property = this.PROGRESS_PROPERTY;
            float[] fArr = new float[1];
            fArr[0] = newCheckedState ? 1.0f : 0.0f;
            this.checkAnimator = ObjectAnimator.ofFloat(this, property, fArr);
            this.checkAnimator.setDuration(300);
            this.checkAnimator.start();
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
        }

        public void setChecked(boolean checked, boolean animated) {
            if (checked != this.isChecked) {
                this.isChecked = checked;
                if (animated) {
                    animateToCheckedState(checked);
                    return;
                }
                cancelCheckAnimator();
                this.progress = checked ? 1.0f : 0.0f;
                invalidate();
            }
        }

        public boolean isChecked() {
            return this.isChecked;
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages = new ArrayList();

        public ListAdapter(Context context) {
            this.mContext = context;
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            Message message = new TL_message();
            message.message = "I can't even take you seriously right now.";
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = UserConfig.getInstance(WallpaperActivity.this.currentAccount).getClientUserId();
            message.id = 1;
            message.media = new TL_messageMediaEmpty();
            message.out = true;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = 0;
            this.messages.add(new MessageObject(WallpaperActivity.this.currentAccount, message, true));
            message = new TL_message();
            message.message = "Ah, you kids today with techno music! You should enjoy the classics, like Hasselhoff!";
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 265;
            message.from_id = 0;
            message.id = 1;
            message.reply_to_msg_id = 5;
            message.media = new TL_messageMediaEmpty();
            message.out = false;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = UserConfig.getInstance(WallpaperActivity.this.currentAccount).getClientUserId();
            this.messages.add(new MessageObject(WallpaperActivity.this.currentAccount, message, true));
            message = new TL_message();
            message.message = LocaleController.formatDateChat((long) date);
            message.id = 0;
            message.date = date;
            MessageObject messageObject = new MessageObject(WallpaperActivity.this.currentAccount, message, false);
            messageObject.type = 10;
            messageObject.contentType = 1;
            messageObject.isDateObject = true;
            this.messages.add(messageObject);
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            return this.messages.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new ChatMessageCell(this.mContext);
                ((ChatMessageCell) view).setDelegate(new ChatMessageCellDelegate() {
                    public void didPressShare(ChatMessageCell cell) {
                    }

                    public boolean needPlayMessage(MessageObject messageObject) {
                        return false;
                    }

                    public void didPressChannelAvatar(ChatMessageCell cell, Chat chat, int postId) {
                    }

                    public void didPressOther(ChatMessageCell cell) {
                    }

                    public void didPressUserAvatar(ChatMessageCell cell, User user) {
                    }

                    public void didPressBotButton(ChatMessageCell cell, KeyboardButton button) {
                    }

                    public void didPressVoteButton(ChatMessageCell cell, TL_pollAnswer button) {
                    }

                    public void didPressCancelSendButton(ChatMessageCell cell) {
                    }

                    public void didLongPress(ChatMessageCell cell) {
                    }

                    public boolean canPerformActions() {
                        return false;
                    }

                    public void didPressUrl(MessageObject messageObject, CharacterStyle url, boolean longPress) {
                    }

                    public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
                    }

                    public void didPressReplyMessage(ChatMessageCell cell, int id) {
                    }

                    public void didPressViaBot(ChatMessageCell cell, String username) {
                    }

                    public void didPressImage(ChatMessageCell cell) {
                    }

                    public void didPressInstantButton(ChatMessageCell cell, int type) {
                    }

                    public boolean isChatAdminCell(int uid) {
                        return false;
                    }
                });
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new ChatActionCellDelegate() {
                    public void didClickedImage(ChatActionCell cell) {
                    }

                    public void didLongPressed(ChatActionCell cell) {
                    }

                    public void needOpenUserProfile(int uid) {
                    }

                    public void didPressedReplyMessage(ChatActionCell cell, int id) {
                    }

                    public void didPressedBotButton(MessageObject messageObject, KeyboardButton button) {
                    }
                });
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int i) {
            if (i < 0 || i >= this.messages.size()) {
                return 4;
            }
            return ((MessageObject) this.messages.get(i)).contentType;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            MessageObject message = (MessageObject) this.messages.get(position);
            View view = holder.itemView;
            if (view instanceof ChatMessageCell) {
                boolean pinnedBotton;
                boolean pinnedTop;
                ChatMessageCell messageCell = (ChatMessageCell) view;
                messageCell.isChat = false;
                int nextType = getItemViewType(position - 1);
                int prevType = getItemViewType(position + 1);
                if ((message.messageOwner.reply_markup instanceof TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                    pinnedBotton = false;
                } else {
                    MessageObject nextMessage = (MessageObject) this.messages.get(position - 1);
                    pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                }
                if (prevType == holder.getItemViewType()) {
                    MessageObject prevMessage = (MessageObject) this.messages.get(position + 1);
                    pinnedTop = !(prevMessage.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300;
                } else {
                    pinnedTop = false;
                }
                messageCell.setFullyDraw(true);
                messageCell.setMessageObject(message, null, pinnedBotton, pinnedTop);
            } else if (view instanceof ChatActionCell) {
                ChatActionCell actionCell = (ChatActionCell) view;
                actionCell.setMessageObject(message);
                actionCell.setAlpha(1.0f);
            }
        }
    }

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    public WallpaperActivity(Object wallPaper, Bitmap bitmap) {
        this.currentWallpaper = wallPaper;
        this.currentWallpaperBitmap = bitmap;
    }

    public void setInitialModes(int modes) {
        boolean z;
        boolean z2 = true;
        if ((modes & 1) != 0) {
            z = true;
        } else {
            z = false;
        }
        this.isBlurred = z;
        if ((modes & 2) == 0) {
            z2 = false;
        }
        this.isMotion = z2;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
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
        this.backgroundPaint.setColor(-1);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.blurredBitmap != null) {
            this.blurredBitmap.recycle();
            this.blurredBitmap = null;
        }
        Theme.applyChatServiceMessageColor();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("BackgroundPreview", R.string.BackgroundPreview));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WallpaperActivity.this.lambda$checkDiscard$70$PassportActivity();
                } else if (id == 1 && WallpaperActivity.this.getParentActivity() != null) {
                    String link;
                    if (WallpaperActivity.this.currentWallpaper instanceof TL_wallPaper) {
                        link = "https://" + MessagesController.getInstance(WallpaperActivity.this.currentAccount).linkPrefix + "/bg/" + ((TL_wallPaper) WallpaperActivity.this.currentWallpaper).slug;
                        StringBuilder modes = new StringBuilder();
                        if (WallpaperActivity.this.isBlurred) {
                            modes.append("blur");
                        }
                        if (WallpaperActivity.this.isMotion) {
                            if (modes.length() > 0) {
                                modes.append("+");
                            }
                            modes.append("motion");
                        }
                        if (modes.length() > 0) {
                            link = link + "?mode=" + modes.toString();
                        }
                    } else if (WallpaperActivity.this.currentWallpaper instanceof ColorWallpaper) {
                        ColorWallpaper colorWallpaper = (ColorWallpaper) WallpaperActivity.this.currentWallpaper;
                        link = "https://" + MessagesController.getInstance(WallpaperActivity.this.currentAccount).linkPrefix + "/bg/" + String.format("%02X%02X%02X", new Object[]{Byte.valueOf((byte) colorWallpaper.color), Integer.valueOf(((byte) colorWallpaper.color) >> 8), Integer.valueOf(((byte) colorWallpaper.color) >> 16)});
                    } else {
                        return;
                    }
                    WallpaperActivity.this.showDialog(new ShareAlert(WallpaperActivity.this.getParentActivity(), null, link, false, link, false));
                }
            }
        });
        if (!(this.currentWallpaper == null || WallpapersListActivity.disableFeatures)) {
            this.actionBar.createMenu().addItem(1, (int) R.drawable.ic_share_video);
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.backgroundImage = new BackupImageView(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                WallpaperActivity.this.parallaxScale = WallpaperActivity.this.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                if (WallpaperActivity.this.isMotion) {
                    setScaleX(WallpaperActivity.this.parallaxScale);
                    setScaleY(WallpaperActivity.this.parallaxScale);
                }
                if (WallpaperActivity.this.radialProgress != null) {
                    int size = AndroidUtilities.dp(44.0f);
                    int x = (getMeasuredWidth() - size) / 2;
                    int y = (getMeasuredHeight() - size) / 2;
                    WallpaperActivity.this.radialProgress.setProgressRect(x, y, x + size, y + size);
                }
                WallpaperActivity.this.progressVisible = getMeasuredWidth() <= getMeasuredHeight();
            }

            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (WallpaperActivity.this.progressVisible && WallpaperActivity.this.radialProgress != null) {
                    WallpaperActivity.this.radialProgress.draw(canvas);
                }
            }
        };
        this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
        this.backgroundImage.getImageReceiver().setForceCrossfade(true);
        frameLayout.addView(this.backgroundImage, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.backgroundImage.getImageReceiver().setDelegate(new WallpaperActivity$$Lambda$0(this));
        this.radialProgress = new RadialProgress2(this.backgroundImage);
        this.radialProgress.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, true));
        this.listView.setOverScrollMode(2);
        this.listView.setAdapter(new ListAdapter(context));
        if (WallpapersListActivity.disableFeatures || (this.currentWallpaper instanceof ColorWallpaper)) {
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
        } else {
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(64.0f));
        }
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.bottomOverlayChat = new FrameLayout(context) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        frameLayout.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new WallpaperActivity$$Lambda$1(this));
        this.bottomOverlayChatText = new TextView(context);
        this.bottomOverlayChatText.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
        this.bottomOverlayChatText.setText(LocaleController.getString("SetBackground", R.string.SetBackground));
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        this.buttonsContainer = new LinearLayout(context);
        this.buttonsContainer.setOrientation(0);
        frameLayout.addView(this.buttonsContainer, LayoutHelper.createFrame(-2, 32.0f, 81, 0.0f, 0.0f, 0.0f, 66.0f));
        String text1 = LocaleController.getString("BackgroundBlurred", R.string.BackgroundBlurred);
        String text2 = LocaleController.getString("BackgroundMotion", R.string.BackgroundMotion);
        int textSize1 = (int) Math.ceil((double) this.textPaint.measureText(text1));
        int textSize2 = (int) Math.ceil((double) this.textPaint.measureText(text2));
        int a = 0;
        while (a < 2) {
            String str;
            int num = a;
            View checkBoxView = new CheckBoxView(context);
            if (a == 0) {
                str = text1;
            } else {
                str = text2;
            }
            checkBoxView.setText(str, a == 0 ? textSize1 : textSize2, Math.max(textSize1, textSize2));
            checkBoxView.setChecked(a == 0 ? this.isBlurred : this.isMotion, false);
            this.buttonsContainer.addView(checkBoxView, LayoutHelper.createLinear(-2, -2, a == 1 ? 9.0f : 0.0f, 0.0f, 0.0f, 0.0f));
            checkBoxView.setOnClickListener(new WallpaperActivity$$Lambda$2(this, checkBoxView, num));
            a++;
        }
        this.parallaxEffect = new WallpaperParallaxEffect(context);
        this.parallaxEffect.setCallback(new WallpaperActivity$$Lambda$3(this));
        if (WallpapersListActivity.disableFeatures || (this.currentWallpaper instanceof ColorWallpaper)) {
            this.buttonsContainer.setVisibility(8);
            this.isBlurred = false;
            this.isMotion = false;
        }
        setCurrentImage(true);
        updateButtonState(false, false);
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$WallpaperActivity(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        Drawable drawable = imageReceiver.getDrawable();
        if (set && drawable != null) {
            Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable));
            this.listView.invalidateViews();
            int N = this.buttonsContainer.getChildCount();
            for (int a = 0; a < N; a++) {
                this.buttonsContainer.getChildAt(a).invalidate();
            }
            if (this.radialProgress != null) {
                this.radialProgress.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
            }
            if (!thumb && this.isBlurred && this.blurredBitmap == null) {
                updateBlurred();
            }
        }
    }

    final /* synthetic */ void lambda$createView$2$WallpaperActivity(View view) {
        boolean done;
        long id;
        int color;
        File toFile = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
        if (this.isBlurred) {
            try {
                FileOutputStream stream = new FileOutputStream(toFile);
                this.blurredBitmap.compress(CompressFormat.JPEG, 87, stream);
                stream.close();
                done = true;
            } catch (Throwable e) {
                FileLog.e(e);
                done = false;
            }
        } else if (this.currentWallpaper instanceof TL_wallPaper) {
            try {
                done = AndroidUtilities.copyFile(FileLoader.getPathToAttach(((TL_wallPaper) this.currentWallpaper).document, true), toFile);
            } catch (Throwable e2) {
                done = false;
                FileLog.e(e2);
            }
        } else if (this.currentWallpaper instanceof ColorWallpaper) {
            done = true;
        } else if (this.currentWallpaper instanceof FileWallpaper) {
            FileWallpaper wallpaper = this.currentWallpaper;
            if (wallpaper.resId != 0 || ((long) wallpaper.resId) == -2) {
                done = true;
            } else {
                try {
                    done = AndroidUtilities.copyFile(wallpaper.path, toFile);
                } catch (Throwable e22) {
                    done = false;
                    FileLog.e(e22);
                }
            }
        } else {
            done = false;
        }
        if (this.currentWallpaper instanceof TL_wallPaper) {
            TL_wallPaper wallPaper = this.currentWallpaper;
            id = wallPaper.id;
            color = 0;
            if (!WallpapersListActivity.disableFeatures) {
                TL_account_saveWallPaper req = new TL_account_saveWallPaper();
                TL_inputWallPaper inputWallPaper = new TL_inputWallPaper();
                inputWallPaper.id = wallPaper.id;
                inputWallPaper.access_hash = wallPaper.access_hash;
                req.wallpaper = inputWallPaper;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, WallpaperActivity$$Lambda$4.$instance);
            }
        } else if (this.currentWallpaper instanceof ColorWallpaper) {
            ColorWallpaper wallPaper2 = this.currentWallpaper;
            id = wallPaper2.id;
            color = wallPaper2.color;
        } else if (this.currentWallpaper instanceof FileWallpaper) {
            id = this.currentWallpaper.id;
            color = 0;
        } else {
            id = 0;
            color = 0;
        }
        if (done) {
            Theme.serviceMessageColorBackup = Theme.getColor("chat_serviceBackground");
            Editor editor = MessagesController.getGlobalMainSettings().edit();
            editor.putLong("selectedBackground2", id);
            editor.putBoolean("selectedBackgroundBlurred", this.isBlurred);
            editor.putBoolean("selectedBackgroundMotion", this.isMotion);
            editor.putInt("selectedColor", color);
            editor.putBoolean("overrideThemeWallpaper", id != -2);
            editor.commit();
            Theme.reloadWallpaper();
        }
        if (this.delegate != null) {
            this.delegate.didSetNewBackground();
        }
        lambda$checkDiscard$70$PassportActivity();
    }

    static final /* synthetic */ void lambda$null$1$WallpaperActivity(TLObject response, TL_error error) {
    }

    final /* synthetic */ void lambda$createView$3$WallpaperActivity(CheckBoxView view, int num, View v) {
        if (this.bottomOverlayChat.isEnabled()) {
            view.setChecked(!view.isChecked(), true);
            if (num == 0) {
                this.isBlurred = view.isChecked();
                updateBlurred();
                return;
            }
            this.isMotion = view.isChecked();
            this.parallaxEffect.setEnabled(this.isMotion);
            animateMotionChange();
        }
    }

    final /* synthetic */ void lambda$createView$4$WallpaperActivity(int offsetX, int offsetY) {
        if (this.isMotion) {
            float progress;
            if (this.motionAnimation != null) {
                progress = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            } else {
                progress = 1.0f;
            }
            this.backgroundImage.setTranslationX(((float) offsetX) * progress);
            this.backgroundImage.setTranslationY(((float) offsetY) * progress);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, this.progressVisible);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, this.progressVisible);
        if (this.radialProgress.getIcon() != 13) {
            updateButtonState(false, true);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        if (this.isBlurred && this.blurredBitmap == null) {
            if (this.currentWallpaperBitmap != null) {
                this.blurredBitmap = Utilities.blurWallpaper(this.currentWallpaperBitmap);
            } else if (this.backgroundImage.getImageReceiver().hasNotThumb()) {
                this.blurredBitmap = Utilities.blurWallpaper(this.backgroundImage.getImageReceiver().getBitmap());
            }
        }
        if (!this.isBlurred) {
            setCurrentImage(false);
        } else if (this.blurredBitmap != null) {
            this.backgroundImage.setImageBitmap(this.blurredBitmap);
        }
    }

    private void updateButtonState(boolean ifSame, boolean animated) {
        float f = 1.0f;
        if (this.currentWallpaper instanceof TL_wallPaper) {
            if (animated && !this.progressVisible) {
                animated = false;
            }
            TL_wallPaper wallPaper = this.currentWallpaper;
            String fileName = FileLoader.getAttachFileName(wallPaper.document);
            if (!TextUtils.isEmpty(fileName)) {
                float f2;
                boolean fileExists = FileLoader.getPathToAttach(wallPaper.document, true).exists();
                if (fileExists) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    this.actionBar.setSubtitle(AndroidUtilities.formatFileSize((long) wallPaper.document.size));
                    this.radialProgress.setProgress(1.0f, animated);
                    this.radialProgress.setIcon(4, ifSame, animated);
                    this.backgroundImage.invalidate();
                } else {
                    DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, null, this);
                    boolean isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setIcon(13, ifSame, animated);
                    this.actionBar.setSubtitle(LocaleController.getString("LoadingFullImage", R.string.LoadingFullImage));
                    this.backgroundImage.invalidate();
                }
                LinearLayout linearLayout = this.buttonsContainer;
                if (fileExists) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.5f;
                }
                linearLayout.setAlpha(f2);
                this.bottomOverlayChat.setEnabled(fileExists);
                TextView textView = this.bottomOverlayChatText;
                if (!fileExists) {
                    f = 0.5f;
                }
                textView.setAlpha(f);
                return;
            }
            return;
        }
        this.radialProgress.setIcon(4, false, false);
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    private void animateMotionChange() {
        if (this.motionAnimation != null) {
            this.motionAnimation.cancel();
        }
        this.motionAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.isMotion) {
            animatorSet = this.motionAnimation;
            animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{this.parallaxScale});
            animatorArr[1] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{this.parallaxScale});
            animatorSet.playTogether(animatorArr);
        } else {
            animatorSet = this.motionAnimation;
            animatorArr = new Animator[4];
            animatorArr[0] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{1.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{1.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                WallpaperActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    private void setCurrentImage(boolean setThumb) {
        PhotoSize thumb = null;
        if (this.currentWallpaper instanceof TL_wallPaper) {
            TL_wallPaper wallPaper = this.currentWallpaper;
            if (setThumb) {
                thumb = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 100);
            }
            this.backgroundImage.setImage(wallPaper.document, "1920_1080", thumb, "100_100_b", "jpg", wallPaper.document.size, 1, this.currentWallpaper);
        } else if (this.currentWallpaper instanceof ColorWallpaper) {
            this.backgroundImage.setImageDrawable(new ColorDrawable(this.currentWallpaper.color));
        } else if (!(this.currentWallpaper instanceof FileWallpaper)) {
        } else {
            if (this.currentWallpaperBitmap != null) {
                this.backgroundImage.setImageBitmap(this.currentWallpaperBitmap);
                return;
            }
            FileWallpaper wallPaper2 = this.currentWallpaper;
            if (wallPaper2.path != null) {
                this.backgroundImage.setImage(wallPaper2.path.getAbsolutePath(), "1920_1080", null);
            } else if (((long) wallPaper2.resId) == -2) {
                this.backgroundImage.setImageDrawable(Theme.getThemedWallpaper(false));
            } else if (wallPaper2.resId != 0) {
                this.backgroundImage.setImageResource(wallPaper2.resId);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[7];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        return themeDescriptionArr;
    }
}
