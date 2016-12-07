package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.internal.view.SupportMenu;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.PlayerControl;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSnapHelper;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RichText;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_pageBlockAnchor;
import org.telegram.tgnet.TLRPC.TL_pageBlockAuthorDate;
import org.telegram.tgnet.TLRPC.TL_pageBlockBlockquote;
import org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC.TL_pageBlockCover;
import org.telegram.tgnet.TLRPC.TL_pageBlockDivider;
import org.telegram.tgnet.TLRPC.TL_pageBlockEmbed;
import org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost;
import org.telegram.tgnet.TLRPC.TL_pageBlockFooter;
import org.telegram.tgnet.TLRPC.TL_pageBlockHeader;
import org.telegram.tgnet.TLRPC.TL_pageBlockList;
import org.telegram.tgnet.TLRPC.TL_pageBlockParagraph;
import org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC.TL_pageBlockPreformatted;
import org.telegram.tgnet.TLRPC.TL_pageBlockPullquote;
import org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC.TL_pageBlockSubheader;
import org.telegram.tgnet.TLRPC.TL_pageBlockSubtitle;
import org.telegram.tgnet.TLRPC.TL_pageBlockTitle;
import org.telegram.tgnet.TLRPC.TL_pageBlockUnsupported;
import org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC.TL_textBold;
import org.telegram.tgnet.TLRPC.TL_textConcat;
import org.telegram.tgnet.TLRPC.TL_textEmail;
import org.telegram.tgnet.TLRPC.TL_textEmpty;
import org.telegram.tgnet.TLRPC.TL_textFixed;
import org.telegram.tgnet.TLRPC.TL_textItalic;
import org.telegram.tgnet.TLRPC.TL_textPlain;
import org.telegram.tgnet.TLRPC.TL_textStrike;
import org.telegram.tgnet.TLRPC.TL_textUnderline;
import org.telegram.tgnet.TLRPC.TL_textUrl;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.Scroller;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TextPaintSpan;
import org.telegram.ui.Components.TextPaintUrlSpan;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.ExtractorRendererBuilder;
import org.telegram.ui.Components.VideoPlayer.Listener;

@TargetApi(16)
public class ArticleViewer implements NotificationCenterDelegate, OnGestureListener, OnDoubleTapListener {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ArticleViewer Instance = null;
    private static final int TEXT_FLAG_ITALIC = 2;
    private static final int TEXT_FLAG_MEDIUM = 1;
    private static final int TEXT_FLAG_MONO = 4;
    private static final int TEXT_FLAG_REGULAR = 0;
    private static final int TEXT_FLAG_STRIKE = 32;
    private static final int TEXT_FLAG_UNDERLINE = 16;
    private static final int TEXT_FLAG_URL = 8;
    private static HashMap<Integer, TextPaint> authorTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> captionTextPaints = new HashMap();
    private static DecelerateInterpolator decelerateInterpolator = null;
    private static Paint dividerPaint = null;
    private static TextPaint embedPostAuthorPaint = null;
    private static TextPaint embedPostDatePaint = null;
    private static HashMap<Integer, TextPaint> embedPostTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> embedTextPaints = new HashMap();
    private static TextPaint errorTextPaint = null;
    private static HashMap<Integer, TextPaint> footerTextPaints = new HashMap();
    private static final int gallery_menu_openin = 3;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_share = 2;
    private static HashMap<Integer, TextPaint> headerTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> listTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> paragraphTextPaints = new HashMap();
    private static Paint preformattedBackgroundPaint;
    private static HashMap<Integer, TextPaint> preformattedTextPaints = new HashMap();
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private static Paint quoteLinePaint;
    private static HashMap<Integer, TextPaint> quoteTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> slideshowTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> subheaderTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> subquoteTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> subtitleTextPaints = new HashMap();
    private static HashMap<Integer, TextPaint> titleTextPaints = new HashMap();
    private static Paint urlPaint;
    private static HashMap<Integer, TextPaint> videoTextPaints = new HashMap();
    private ActionBar actionBar;
    private WebpageAdapter adapter;
    public HashMap<String, Integer> anchors = new HashMap();
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues = ((float[][]) Array.newInstance(Float.TYPE, new int[]{2, 8}));
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private ImageView backButton;
    private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-1);
    private Paint blackPaint = new Paint();
    public ArrayList<PageBlock> blocks = new ArrayList();
    private FrameLayout bottomLayout;
    private boolean canDragDown = true;
    private boolean canZoom = true;
    private TextView captionTextView;
    private TextView captionTextViewNew;
    private TextView captionTextViewOld;
    private ImageReceiver centerImage = new ImageReceiver();
    private boolean changingPage;
    private boolean checkingForLongPress = false;
    private FrameLayout containerView;
    private int[] coords = new int[2];
    private AnimatorSet currentActionBarAnimation;
    private AnimatedFileDrawable currentAnimation;
    private String[] currentFileNames = new String[3];
    private int currentIndex;
    private PageBlock currentMedia;
    private MessageObject currentMessageObject;
    private WebPage currentPage;
    private PlaceProviderObject currentPlaceObject;
    private int currentRotation;
    private Bitmap currentThumb;
    private boolean disableShowCheck;
    private boolean discardTap;
    private boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private float dragY;
    private boolean draggingDown;
    private GestureDetector gestureDetector;
    private FrameLayout headerView;
    private PlaceProviderObject hideAfterAnimation;
    private AnimatorSet imageMoveAnimation;
    private ArrayList<PageBlock> imagesArr = new ArrayList();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    private boolean isPhotoVisible;
    private boolean isPlaying;
    private boolean isVisible;
    private Object lastInsets;
    private LinearLayoutManager layoutManager;
    private ImageReceiver leftImage = new ImageReceiver();
    private RecyclerListView listView;
    private float maxX;
    private float maxY;
    private ActionBarMenuItem menuItem;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private Activity parentActivity;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private Runnable photoAnimationEndRunnable;
    private int photoAnimationInProgress;
    private PhotoBackgroundDrawable photoBackgroundDrawable = new PhotoBackgroundDrawable(-16777216);
    public ArrayList<PageBlock> photoBlocks = new ArrayList();
    private FrameLayoutDrawer photoContainerView;
    private long photoTransitionAnimationStartTime;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    private float pinchStartX;
    private float pinchStartY;
    private boolean playerNeedsPrepare;
    private int pressCount = 0;
    private TextPaintUrlSpan pressedLink;
    private StaticLayout pressedLinkOwnerLayout;
    private View pressedLinkOwnerView;
    private RadialProgressView[] radialProgressViews = new RadialProgressView[3];
    private ImageReceiver rightImage = new ImageReceiver();
    private float scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    private Scroller scroller;
    private ImageView shareButton;
    private ImageView sharePhotoButton;
    private PlaceProviderObject showAfterAnimation;
    private int switchImageAfterAnimation;
    private boolean textureUploaded;
    private long transitionAnimationStartTime;
    private float translationX;
    private float translationY;
    private Runnable updateProgressRunnable = new Runnable() {
        public void run() {
            if (!(ArticleViewer.this.videoPlayer == null || ArticleViewer.this.videoPlayerSeekbar == null || ArticleViewer.this.videoPlayerSeekbar.isDragging())) {
                PlayerControl playerControl = ArticleViewer.this.videoPlayer.getPlayerControl();
                ArticleViewer.this.videoPlayerSeekbar.setProgress(((float) playerControl.getCurrentPosition()) / ((float) playerControl.getDuration()));
                ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
                ArticleViewer.this.updateVideoPlayerTime();
            }
            if (ArticleViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable, 100);
            }
        }
    };
    private LinkPath urlPath = new LinkPath();
    private VelocityTracker velocityTracker;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private ImageView videoPlayButton;
    private VideoPlayer videoPlayer;
    private FrameLayout videoPlayerControlFrameLayout;
    private SeekBar videoPlayerSeekbar;
    private TextView videoPlayerTime;
    private TextureView videoTextureView;
    private Dialog visibleDialog;
    private boolean wasLayout;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    private class BackgroundDrawable extends ColorDrawable {
        public BackgroundDrawable(int color) {
            super(color);
        }

        public void setAlpha(int alpha) {
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (ArticleViewer.this.isVisible && alpha == 255) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(alpha);
        }
    }

    private class BlockAuthorDateCell extends View {
        private TL_pageBlockAuthorDate currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;

        public BlockAuthorDateCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockAuthorDate block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                String text;
                if (this.currentBlock.published_date != 0 && !TextUtils.isEmpty(this.currentBlock.author)) {
                    text = LocaleController.formatString("ArticleDateByAuthor", R.string.ArticleDateByAuthor, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000), this.currentBlock.author);
                } else if (TextUtils.isEmpty(this.currentBlock.author)) {
                    text = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000);
                } else {
                    text = LocaleController.formatString("ArticleByAuthor", R.string.ArticleByAuthor, this.currentBlock.author);
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(text, null, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(8.0f));
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockBlockquoteCell extends View {
        private TL_pageBlockBlockquote currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private StaticLayout textLayout2;
        private int textX = AndroidUtilities.dp(32.0f);
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockBlockquoteCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockBlockquote block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(50.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight());
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, width - AndroidUtilities.dp(50.0f), this.currentBlock);
                if (this.textLayout2 != null) {
                    this.textY2 = AndroidUtilities.dp(2.0f) + height;
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout2);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
                canvas.drawRect((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
            }
        }
    }

    private class BlockCollageCell extends FrameLayout {
        private Adapter adapter;
        private TL_pageBlockCollage currentBlock;
        private GridLayoutManager gridLayoutManager;
        private boolean inLayout;
        private RecyclerListView innerListView;
        private int lastCreatedWidth;
        private int listX;
        private StaticLayout textLayout;
        private int textX;
        private int textY;

        public BlockCollageCell(Context context) {
            super(context);
            this.innerListView = new RecyclerListView(context, ArticleViewer.this) {
                public void requestLayout() {
                    if (!BlockCollageCell.this.inLayout) {
                        super.requestLayout();
                    }
                }
            };
            this.innerListView.setGlowColor(-657673);
            this.innerListView.addItemDecoration(new ItemDecoration(ArticleViewer.this) {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                    outRect.left = 0;
                    outRect.top = 0;
                    int dp = AndroidUtilities.dp(2.0f);
                    outRect.right = dp;
                    outRect.bottom = dp;
                }
            });
            RecyclerListView recyclerListView = this.innerListView;
            LayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            this.gridLayoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
            recyclerListView = this.innerListView;
            Adapter anonymousClass3 = new Adapter(ArticleViewer.this) {
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return new Holder(new BlockPhotoCell(BlockCollageCell.this.getContext(), 2));
                }

                public void onBindViewHolder(ViewHolder holder, int position) {
                    holder.itemView.setBlock((TL_pageBlockPhoto) BlockCollageCell.this.currentBlock.items.get(position), false, false);
                }

                public int getItemCount() {
                    return BlockCollageCell.this.currentBlock.items.size();
                }

                public int getItemViewType(int position) {
                    return 0;
                }
            };
            this.adapter = anonymousClass3;
            recyclerListView.setAdapter(anonymousClass3);
            addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0f));
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockCollage block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            this.adapter.notifyDataSetChanged();
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            this.inLayout = true;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                int textWidth;
                int listWidth = width;
                if (this.currentBlock.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    listWidth -= this.listX + AndroidUtilities.dp(18.0f);
                    textWidth = listWidth;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                int countPerRow = listWidth / AndroidUtilities.dp(100.0f);
                int rowCount = (int) Math.ceil((double) (((float) this.currentBlock.items.size()) / ((float) countPerRow)));
                int itemSize = listWidth / countPerRow;
                this.gridLayoutManager.setSpanCount(countPerRow);
                this.innerListView.measure(MeasureSpec.makeMeasureSpec((itemSize * countPerRow) + AndroidUtilities.dp(2.0f), C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(itemSize * rowCount, C.ENCODING_PCM_32BIT));
                height = (rowCount * itemSize) - AndroidUtilities.dp(2.0f);
                if (this.lastCreatedWidth != width) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, textWidth, this.currentBlock);
                    if (this.textLayout != null) {
                        this.textY = AndroidUtilities.dp(8.0f) + height;
                        height += AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    }
                }
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            this.inLayout = false;
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(this.listX, 0, this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
            if (ArticleViewer.dividerPaint == null) {
                ArticleViewer.dividerPaint = new Paint();
                ArticleViewer.dividerPaint.setColor(-3288619);
            }
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(18.0f));
        }

        protected void onDraw(Canvas canvas) {
            int width = getMeasuredWidth() / 3;
            this.rect.set((float) width, (float) AndroidUtilities.dp(8.0f), (float) (width * 2), (float) AndroidUtilities.dp(10.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), (float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), ArticleViewer.dividerPaint);
        }
    }

    private class BlockEmbedCell extends FrameLayout {
        private TL_pageBlockEmbed currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;
        private WebView webView;

        @SuppressLint({"SetJavaScriptEnabled"})
        public BlockEmbedCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.webView = new WebView(context);
            this.webView.getSettings().setJavaScriptEnabled(true);
            this.webView.getSettings().setDomStorageEnabled(true);
            if (VERSION.SDK_INT >= 17) {
                this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            }
            String userAgent = this.webView.getSettings().getUserAgentString();
            if (userAgent != null) {
                this.webView.getSettings().setUserAgentString(userAgent.replace("Android", ""));
            }
            if (VERSION.SDK_INT >= 21) {
                this.webView.getSettings().setMixedContentMode(0);
                CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            }
            this.webView.setWebChromeClient(new WebChromeClient(ArticleViewer.this) {
                public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                    onShowCustomView(view, callback);
                }

                public void onShowCustomView(View view, CustomViewCallback callback) {
                }

                public void onHideCustomView() {
                    super.onHideCustomView();
                }
            });
            this.webView.setWebViewClient(new WebViewClient(ArticleViewer.this) {
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }
            });
            addView(this.webView);
        }

        public void setBlock(TL_pageBlockEmbed block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    try {
                        HashMap<String, String> args = new HashMap();
                        args.put("Referer", "http://youtube.com");
                        BlockEmbedCell.this.webView.loadUrl(BlockEmbedCell.this.currentBlock.url, args);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            });
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                height = (int) (((float) this.currentBlock.h) * (((float) width) / ((float) this.currentBlock.w)));
                this.webView.measure(MeasureSpec.makeMeasureSpec(width, C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(height, C.ENCODING_PCM_32BIT));
                if (this.lastCreatedWidth != width) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                    if (this.textLayout != null) {
                        this.textY = AndroidUtilities.dp(8.0f) + height;
                        height += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.webView.layout(0, 0, this.webView.getMeasuredWidth(), this.webView.getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockEmbedPostCell extends View {
        private AvatarDrawable avatarDrawable;
        private ImageReceiver avatarImageView = new ImageReceiver(this);
        private boolean avatarVisible;
        private TL_pageBlockEmbedPost currentBlock;
        private StaticLayout dateLayout;
        private int dateX;
        private int lastCreatedWidth;
        private StaticLayout nameLayout;
        private int nameX;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(32.0f);
        private int textY = AndroidUtilities.dp(56.0f);

        public BlockEmbedPostCell(Context context) {
            super(context);
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.avatarImageView.setImageCoords(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            this.avatarDrawable = new AvatarDrawable();
        }

        public void setBlock(TL_pageBlockEmbedPost block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                boolean z = this.currentBlock.author_photo_id != 0;
                this.avatarVisible = z;
                if (z) {
                    Photo photo = ArticleViewer.this.getPhotoWithId(this.currentBlock.author_photo_id);
                    z = photo != null;
                    this.avatarVisible = z;
                    if (z) {
                        this.avatarDrawable.setInfo(0, this.currentBlock.author, null, false);
                        this.avatarImageView.setImage(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(40.0f), true).location, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(40), Integer.valueOf(40)}), this.avatarDrawable, 0, null, true);
                    }
                }
                this.nameLayout = ArticleViewer.this.createLayoutForText(this.currentBlock.author, null, width - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), this.currentBlock);
                this.dateLayout = ArticleViewer.this.createLayoutForText(LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000), null, width - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), this.currentBlock);
                height = AndroidUtilities.dp(56.0f);
                if (this.currentBlock.text != null) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(50.0f), this.currentBlock);
                    if (this.textLayout != null) {
                        height += AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    }
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            int i = 54;
            int i2 = 0;
            if (this.currentBlock != null) {
                if (this.avatarVisible) {
                    this.avatarImageView.draw(canvas);
                }
                if (this.nameLayout != null) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 32)), (float) AndroidUtilities.dp(10.0f));
                    this.nameLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.dateLayout != null) {
                    canvas.save();
                    if (!this.avatarVisible) {
                        i = 0;
                    }
                    canvas.translate((float) AndroidUtilities.dp((float) (i + 32)), (float) AndroidUtilities.dp(29.0f));
                    this.dateLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                float dp = (float) AndroidUtilities.dp(18.0f);
                float dp2 = (float) AndroidUtilities.dp(6.0f);
                float dp3 = (float) AndroidUtilities.dp(20.0f);
                int measuredHeight = getMeasuredHeight();
                if (this.currentBlock.level == 0) {
                    i2 = AndroidUtilities.dp(6.0f);
                }
                canvas.drawRect(dp, dp2, dp3, (float) (measuredHeight - i2), ArticleViewer.quoteLinePaint);
            }
        }
    }

    private class BlockFooterCell extends View {
        private TL_pageBlockFooter currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockFooterCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockFooter block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockHeaderCell extends View {
        private TL_pageBlockHeader currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;

        public BlockHeaderCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockHeader block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(8.0f));
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockListCell extends View {
        private TL_pageBlockList currentBlock;
        private int lastCreatedWidth;
        private ArrayList<StaticLayout> textLayouts = new ArrayList();
        private ArrayList<StaticLayout> textNumLayouts = new ArrayList();
        private ArrayList<Integer> textYLayouts = new ArrayList();

        public BlockListCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockList block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            int count = this.textLayouts.size();
            int textX = AndroidUtilities.dp(36.0f);
            for (int a = 0; a < count; a++) {
                StaticLayout textLayout = (StaticLayout) this.textLayouts.get(a);
                if (ArticleViewer.this.checkLayoutForLinks(event, this, textLayout, textX, ((Integer) this.textYLayouts.get(a)).intValue())) {
                    return true;
                }
            }
            return super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayouts.clear();
                this.textYLayouts.clear();
                this.textNumLayouts.clear();
                int count = this.currentBlock.items.size();
                for (int a = 0; a < count; a++) {
                    String num;
                    RichText item = (RichText) this.currentBlock.items.get(a);
                    height += AndroidUtilities.dp(8.0f);
                    StaticLayout textLayout = ArticleViewer.this.createLayoutForText(null, item, width - AndroidUtilities.dp(54.0f), this.currentBlock);
                    this.textYLayouts.add(Integer.valueOf(height));
                    this.textLayouts.add(textLayout);
                    if (textLayout != null) {
                        height += textLayout.getHeight();
                    }
                    if (this.currentBlock.ordered) {
                        num = String.format(Locale.US, "%d.", new Object[]{Integer.valueOf(a + 1)});
                    } else {
                        num = "•";
                    }
                    this.textNumLayouts.add(ArticleViewer.this.createLayoutForText(num, item, width - AndroidUtilities.dp(54.0f), this.currentBlock));
                }
                height += AndroidUtilities.dp(8.0f);
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int count = this.textLayouts.size();
                for (int a = 0; a < count; a++) {
                    StaticLayout textLayout = (StaticLayout) this.textLayouts.get(a);
                    StaticLayout textLayout2 = (StaticLayout) this.textNumLayouts.get(a);
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(18.0f), (float) ((Integer) this.textYLayouts.get(a)).intValue());
                    if (textLayout2 != null) {
                        textLayout2.draw(canvas);
                    }
                    canvas.translate((float) AndroidUtilities.dp(18.0f), 0.0f);
                    ArticleViewer.this.drawLayoutLink(canvas, textLayout);
                    if (textLayout != null) {
                        textLayout.draw(canvas);
                    }
                    canvas.restore();
                }
            }
        }
    }

    private class BlockParagraphCell extends View {
        private TL_pageBlockParagraph currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX;
        private int textY;

        public BlockParagraphCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockParagraph block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                if (this.currentBlock.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18));
                }
                if (this.lastCreatedWidth != width) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, this.currentBlock);
                    if (this.textLayout != null) {
                        height = this.textLayout.getHeight();
                        height = this.currentBlock.level > 0 ? height + AndroidUtilities.dp(8.0f) : height + AndroidUtilities.dp(16.0f);
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockPhotoCell extends View {
        private TL_pageBlockPhoto currentBlock;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isLast;
        private int lastCreatedWidth;
        private boolean photoPressed;
        private StaticLayout textLayout;
        private int textX;
        private int textY;

        public BlockPhotoCell(Context context, int type) {
            super(context);
            this.currentType = type;
        }

        public void setBlock(TL_pageBlockPhoto block, boolean first, boolean last) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            this.isFirst = first;
            this.isLast = last;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                this.photoPressed = true;
            } else if (event.getAction() == 1 && this.photoPressed) {
                this.photoPressed = false;
                ArticleViewer.this.openPhoto(this.currentBlock);
            } else if (event.getAction() == 3) {
                this.photoPressed = false;
            }
            if (this.photoPressed || ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event)) {
                return true;
            }
            return false;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentType == 1) {
                width = ArticleViewer.this.listView.getWidth();
                height = ((View) getParent()).getMeasuredHeight();
            } else if (this.currentType == 2) {
                height = width;
            }
            if (this.currentBlock != null) {
                int photoX;
                int textWidth;
                Photo photo = ArticleViewer.this.getPhotoWithId(this.currentBlock.photo_id);
                int photoWidth = width;
                if (this.currentType != 0 || this.currentBlock.level <= 0) {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                } else {
                    photoX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.textX = photoX;
                    photoWidth -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (photo != null) {
                    String str;
                    PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize(), true);
                    PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                    if (image == thumb) {
                        thumb = null;
                    }
                    if (this.currentType == 0) {
                        height = (int) (((float) image.h) * (((float) photoWidth) / ((float) image.w)));
                    }
                    ImageReceiver imageReceiver = this.imageView;
                    int dp = (this.isFirst || this.currentType == 1 || this.currentType == 2 || this.currentBlock.level > 0) ? 0 : AndroidUtilities.dp(8.0f);
                    imageReceiver.setImageCoords(photoX, dp, photoWidth, height);
                    String filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoWidth), Integer.valueOf(height)});
                    ImageReceiver imageReceiver2 = this.imageView;
                    TLObject tLObject = image.location;
                    FileLocation fileLocation = thumb != null ? thumb.location : null;
                    if (thumb != null) {
                        str = "80_80_b";
                    } else {
                        str = null;
                    }
                    imageReceiver2.setImage(tLObject, filter, fileLocation, str, image.size, null, true);
                }
                if (this.currentType == 0 && this.lastCreatedWidth != width) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, textWidth, this.currentBlock);
                    if (this.textLayout != null) {
                        height += AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    }
                }
                if (!this.isFirst && !this.isLast && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentType != 2) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.imageView.draw(canvas);
                if (this.textLayout != null) {
                    canvas.save();
                    float f = (float) this.textX;
                    int imageY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.dp(8.0f);
                    this.textY = imageY;
                    canvas.translate(f, (float) imageY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockPreformattedCell extends View {
        private TL_pageBlockPreformatted currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;

        public BlockPreformattedCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockPreformatted block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(24.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(32.0f) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, (float) AndroidUtilities.dp(8.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(8.0f)), ArticleViewer.preformattedBackgroundPaint);
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(16.0f));
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockPullquoteCell extends View {
        private TL_pageBlockPullquote currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private StaticLayout textLayout2;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockPullquoteCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockPullquote block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight());
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout2 != null) {
                    this.textY2 = AndroidUtilities.dp(2.0f) + height;
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout2);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockSlideshowCell extends FrameLayout {
        private Adapter adapter;
        private TL_pageBlockSlideshow currentBlock;
        private RecyclerListView innerListView;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockSlideshowCell(Context context) {
            super(context);
            this.innerListView = new RecyclerListView(context);
            this.innerListView.setGlowColor(-657673);
            new LinearSnapHelper().attachToRecyclerView(this.innerListView);
            this.innerListView.setLayoutManager(new LinearLayoutManager(context, 0, false));
            RecyclerListView recyclerListView = this.innerListView;
            Adapter anonymousClass1 = new Adapter(ArticleViewer.this) {
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return new Holder(new BlockPhotoCell(BlockSlideshowCell.this.getContext(), 1));
                }

                public void onBindViewHolder(ViewHolder holder, int position) {
                    holder.itemView.setBlock((TL_pageBlockPhoto) BlockSlideshowCell.this.currentBlock.items.get(position), true, true);
                }

                public int getItemCount() {
                    return BlockSlideshowCell.this.currentBlock.items.size();
                }

                public int getItemViewType(int position) {
                    return 0;
                }
            };
            this.adapter = anonymousClass1;
            recyclerListView.setAdapter(anonymousClass1);
            addView(this.innerListView);
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockSlideshow block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            this.adapter.notifyDataSetChanged();
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                height = (int) (50.0f * (((float) width) / 100.0f));
                this.innerListView.measure(MeasureSpec.makeMeasureSpec(width, C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(height, C.ENCODING_PCM_32BIT));
                if (this.lastCreatedWidth != width) {
                    this.textY = AndroidUtilities.dp(8.0f) + height;
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                    if (this.textLayout != null) {
                        height += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(0, 0, this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockSubheaderCell extends View {
        private TL_pageBlockSubheader currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubheaderCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockSubheader block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockSubtitleCell extends View {
        private TL_pageBlockSubtitle currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;

        public BlockSubtitleCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockSubtitle block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(8.0f));
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockTitleCell extends View {
        private TL_pageBlockTitle currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;

        public BlockTitleCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockTitle block) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            requestLayout();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock == null) {
                height = 1;
            } else if (this.lastCreatedWidth != width) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(8.0f));
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockVideoCell extends View {
        private TL_pageBlockVideo currentBlock;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isLast;
        private int lastCreatedWidth;
        private boolean photoPressed;
        private StaticLayout textLayout;
        private int textX;
        private int textY;

        public BlockVideoCell(Context context, int type) {
            super(context);
            this.currentType = type;
        }

        public void setBlock(TL_pageBlockVideo block, boolean first, boolean last) {
            this.currentBlock = block;
            this.lastCreatedWidth = 0;
            this.isFirst = first;
            this.isLast = last;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                this.photoPressed = true;
            } else if (event.getAction() == 1 && this.photoPressed) {
                this.photoPressed = false;
                ArticleViewer.this.openPhoto(this.currentBlock);
            } else if (event.getAction() == 3) {
                this.photoPressed = false;
            }
            if (this.photoPressed || ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event)) {
                return true;
            }
            return false;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentType == 1) {
                width = ArticleViewer.this.listView.getWidth();
                height = ((View) getParent()).getMeasuredHeight();
            } else if (this.currentType == 2) {
                height = width;
            }
            if (this.currentBlock != null) {
                int photoX;
                int textWidth;
                Document document = ArticleViewer.this.getDocumentWithId(this.currentBlock.video_id);
                int photoWidth = width;
                if (this.currentType != 0 || this.currentBlock.level <= 0) {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                } else {
                    photoX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.textX = photoX;
                    photoWidth -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (document != null) {
                    String str;
                    PhotoSize thumb = document.thumb;
                    if (this.currentType != 2) {
                        height = (int) (((float) thumb.h) * (((float) photoWidth) / ((float) thumb.w)));
                    }
                    ImageReceiver imageReceiver = this.imageView;
                    int dp = (this.isFirst || this.currentType == 1 || this.currentType == 2 || this.currentBlock.level > 0) ? 0 : AndroidUtilities.dp(8.0f);
                    imageReceiver.setImageCoords(photoX, dp, photoWidth, height);
                    String filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoWidth), Integer.valueOf(height)});
                    ImageReceiver imageReceiver2 = this.imageView;
                    FileLocation fileLocation = thumb != null ? thumb.location : null;
                    if (thumb != null) {
                        str = "80_80_b";
                    } else {
                        str = null;
                    }
                    imageReceiver2.setImage(document, filter, fileLocation, str, document.size, null, true);
                }
                if (this.currentType == 0 && this.lastCreatedWidth != width) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, textWidth, this.currentBlock);
                    if (this.textLayout != null) {
                        height += AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    }
                }
                if (!this.isFirst && !this.isLast && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentType != 2) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.imageView.draw(canvas);
                if (this.textLayout != null) {
                    canvas.save();
                    float f = (float) this.textX;
                    int imageY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.dp(8.0f);
                    this.textY = imageY;
                    canvas.translate(f, (float) imageY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (ArticleViewer.this.checkingForLongPress && ArticleViewer.this.windowView != null) {
                ArticleViewer.this.checkingForLongPress = false;
                ArticleViewer.this.windowView.performHapticFeedback(0);
                if (ArticleViewer.this.pressedLink != null) {
                    final String urlFinal = ArticleViewer.this.pressedLink.getUrl();
                    Builder builder = new Builder(ArticleViewer.this.parentActivity);
                    builder.setTitle(urlFinal);
                    builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (ArticleViewer.this.parentActivity != null) {
                                if (which == 0) {
                                    Browser.openUrl(ArticleViewer.this.parentActivity, urlFinal);
                                } else if (which == 1) {
                                    String url = urlFinal;
                                    if (url.startsWith("mailto:")) {
                                        url = url.substring(7);
                                    } else if (url.startsWith("tel:")) {
                                        url = url.substring(4);
                                    }
                                    AndroidUtilities.addToClipboard(url);
                                }
                            }
                        }
                    });
                    ArticleViewer.this.showDialog(builder.create());
                    ArticleViewer.this.hideActionBar();
                    ArticleViewer.this.pressedLink = null;
                    ArticleViewer.this.pressedLinkOwnerLayout = null;
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                }
            }
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (ArticleViewer.this.pendingCheckForLongPress == null) {
                ArticleViewer.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$604(ArticleViewer.this);
            if (ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.postDelayed(ArticleViewer.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent event) {
            ArticleViewer.this.processTouchEvent(event);
            return true;
        }

        protected void onDraw(Canvas canvas) {
            ArticleViewer.this.drawContent(canvas);
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return child != ArticleViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, child, drawingTime);
        }
    }

    private class PhotoBackgroundDrawable extends ColorDrawable {
        private Runnable drawRunnable;

        public PhotoBackgroundDrawable(int color) {
            super(color);
        }

        public void setAlpha(int alpha) {
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (ArticleViewer.this.isPhotoVisible && alpha == 255) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(alpha);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != 0 && this.drawRunnable != null) {
                this.drawRunnable.run();
                this.drawRunnable = null;
            }
        }
    }

    public static class PlaceProviderObject {
        public int clipBottomAddition;
        public int clipTopAddition;
        public ImageReceiver imageReceiver;
        public int index;
        public View parentView;
        public int radius;
        public float scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        public int size;
        public Bitmap thumb;
        public int viewX;
        public int viewY;
    }

    private class RadialProgressView {
        private float alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        private float animatedAlphaValue = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        private float animatedProgressValue = 0.0f;
        private float animationProgressStart = 0.0f;
        private int backgroundState = -1;
        private float currentProgress = 0.0f;
        private long currentProgressTime = 0;
        private long lastUpdateTime = 0;
        private View parent = null;
        private int previousBackgroundState = -2;
        private RectF progressRect = new RectF();
        private float radOffset = 0.0f;
        private float scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        private int size = AndroidUtilities.dp(64.0f);

        public RadialProgressView(Context context, View parentView) {
            if (ArticleViewer.decelerateInterpolator == null) {
                ArticleViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                ArticleViewer.progressPaint = new Paint(1);
                ArticleViewer.progressPaint.setStyle(Style.STROKE);
                ArticleViewer.progressPaint.setStrokeCap(Cap.ROUND);
                ArticleViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                ArticleViewer.progressPaint.setColor(-1);
            }
            this.parent = parentView;
        }

        private void updateAnimation() {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.animatedProgressValue != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                this.radOffset += ((float) (360 * dt)) / 3000.0f;
                float progressDiff = this.currentProgress - this.animationProgressStart;
                if (progressDiff > 0.0f) {
                    this.currentProgressTime += dt;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (ArticleViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA) * progressDiff);
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.previousBackgroundState != -2) {
                this.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousBackgroundState = -2;
                }
                this.parent.invalidate();
            }
        }

        public void setProgress(float value, boolean animated) {
            if (animated) {
                this.animationProgressStart = this.animatedProgressValue;
            } else {
                this.animatedProgressValue = value;
                this.animationProgressStart = value;
            }
            this.currentProgress = value;
            this.currentProgressTime = 0;
        }

        public void setBackgroundState(int state, boolean animated) {
            this.lastUpdateTime = System.currentTimeMillis();
            if (!animated || this.backgroundState == state) {
                this.previousBackgroundState = -2;
            } else {
                this.previousBackgroundState = this.backgroundState;
                this.animatedAlphaValue = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            }
            this.backgroundState = state;
            this.parent.invalidate();
        }

        public void setAlpha(float value) {
            this.alpha = value;
        }

        public void setScale(float value) {
            this.scale = value;
        }

        public void onDraw(Canvas canvas) {
            Drawable drawable;
            int sizeScaled = (int) (((float) this.size) * this.scale);
            int x = (ArticleViewer.this.getContainerViewWidth() - sizeScaled) / 2;
            int y = (ArticleViewer.this.getContainerViewHeight() - sizeScaled) / 2;
            if (this.previousBackgroundState >= 0 && this.previousBackgroundState < 4) {
                drawable = ArticleViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState >= 0 && this.backgroundState < 4) {
                drawable = ArticleViewer.progressDrawables[this.backgroundState];
                if (drawable != null) {
                    if (this.previousBackgroundState != -2) {
                        drawable.setAlpha((int) (((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.animatedAlphaValue) * 255.0f) * this.alpha));
                    } else {
                        drawable.setAlpha((int) (this.alpha * 255.0f));
                    }
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState == 0 || this.backgroundState == 1 || this.previousBackgroundState == 0 || this.previousBackgroundState == 1) {
                int diff = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    ArticleViewer.progressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                } else {
                    ArticleViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                this.progressRect.set((float) (x + diff), (float) (y + diff), (float) ((x + sizeScaled) - diff), (float) ((y + sizeScaled) - diff));
                canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, ArticleViewer.progressPaint);
                updateAnimation();
            }
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    private class WebpageAdapter extends Adapter {
        private Context context;

        public WebpageAdapter(Context ctx) {
            this.context = ctx;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new BlockParagraphCell(this.context);
                    break;
                case 1:
                    view = new BlockHeaderCell(this.context);
                    break;
                case 2:
                    view = new BlockDividerCell(this.context);
                    break;
                case 3:
                    view = new BlockEmbedCell(this.context);
                    break;
                case 4:
                    view = new BlockSubtitleCell(this.context);
                    break;
                case 5:
                    view = new BlockVideoCell(this.context, 0);
                    break;
                case 6:
                    view = new BlockPullquoteCell(this.context);
                    break;
                case 7:
                    view = new BlockBlockquoteCell(this.context);
                    break;
                case 8:
                    view = new BlockSlideshowCell(this.context);
                    break;
                case 9:
                    view = new BlockPhotoCell(this.context, 0);
                    break;
                case 10:
                    view = new BlockAuthorDateCell(this.context);
                    break;
                case 11:
                    view = new BlockTitleCell(this.context);
                    break;
                case 12:
                    view = new BlockListCell(this.context);
                    break;
                case 13:
                    view = new BlockFooterCell(this.context);
                    break;
                case 14:
                    view = new BlockPreformattedCell(this.context);
                    break;
                case 15:
                    view = new BlockSubheaderCell(this.context);
                    break;
                case 16:
                    view = new BlockEmbedPostCell(this.context);
                    break;
                default:
                    view = new BlockCollageCell(this.context);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            PageBlock block = (PageBlock) ArticleViewer.this.blocks.get(position);
            PageBlock originalBlock = block;
            if (block instanceof TL_pageBlockCover) {
                block = block.cover;
            }
            boolean z2;
            switch (holder.getItemViewType()) {
                case 0:
                    holder.itemView.setBlock((TL_pageBlockParagraph) block);
                    return;
                case 1:
                    holder.itemView.setBlock((TL_pageBlockHeader) block);
                    return;
                case 2:
                    BlockDividerCell cell = holder.itemView;
                    return;
                case 3:
                    holder.itemView.setBlock((TL_pageBlockEmbed) block);
                    return;
                case 4:
                    holder.itemView.setBlock((TL_pageBlockSubtitle) block);
                    return;
                case 5:
                    BlockVideoCell cell2 = holder.itemView;
                    TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) block;
                    z2 = position == 0;
                    if (position != ArticleViewer.this.blocks.size() - 1) {
                        z = false;
                    }
                    cell2.setBlock(tL_pageBlockVideo, z2, z);
                    return;
                case 6:
                    holder.itemView.setBlock((TL_pageBlockPullquote) block);
                    return;
                case 7:
                    holder.itemView.setBlock((TL_pageBlockBlockquote) block);
                    return;
                case 8:
                    holder.itemView.setBlock((TL_pageBlockSlideshow) block);
                    return;
                case 9:
                    BlockPhotoCell cell3 = holder.itemView;
                    TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) block;
                    z2 = position == 0;
                    if (position != ArticleViewer.this.blocks.size() - 1) {
                        z = false;
                    }
                    cell3.setBlock(tL_pageBlockPhoto, z2, z);
                    return;
                case 10:
                    holder.itemView.setBlock((TL_pageBlockAuthorDate) block);
                    return;
                case 11:
                    holder.itemView.setBlock((TL_pageBlockTitle) block);
                    return;
                case 12:
                    holder.itemView.setBlock((TL_pageBlockList) block);
                    return;
                case 13:
                    holder.itemView.setBlock((TL_pageBlockFooter) block);
                    return;
                case 14:
                    holder.itemView.setBlock((TL_pageBlockPreformatted) block);
                    return;
                case 15:
                    holder.itemView.setBlock((TL_pageBlockSubheader) block);
                    return;
                case 16:
                    holder.itemView.setBlock((TL_pageBlockEmbedPost) block);
                    return;
                case 17:
                    holder.itemView.setBlock((TL_pageBlockCollage) block);
                    return;
                default:
                    return;
            }
        }

        private int getTypeForBlock(PageBlock block) {
            if (block instanceof TL_pageBlockParagraph) {
                return 0;
            }
            if (block instanceof TL_pageBlockHeader) {
                return 1;
            }
            if (block instanceof TL_pageBlockDivider) {
                return 2;
            }
            if (block instanceof TL_pageBlockEmbed) {
                return 3;
            }
            if (block instanceof TL_pageBlockSubtitle) {
                return 4;
            }
            if (block instanceof TL_pageBlockVideo) {
                return 5;
            }
            if (block instanceof TL_pageBlockPullquote) {
                return 6;
            }
            if (block instanceof TL_pageBlockBlockquote) {
                return 7;
            }
            if (block instanceof TL_pageBlockSlideshow) {
                return 8;
            }
            if (block instanceof TL_pageBlockPhoto) {
                return 9;
            }
            if (block instanceof TL_pageBlockAuthorDate) {
                return 10;
            }
            if (block instanceof TL_pageBlockTitle) {
                return 11;
            }
            if (block instanceof TL_pageBlockList) {
                return 12;
            }
            if (block instanceof TL_pageBlockFooter) {
                return 13;
            }
            if (block instanceof TL_pageBlockPreformatted) {
                return 14;
            }
            if (block instanceof TL_pageBlockSubheader) {
                return 15;
            }
            if (block instanceof TL_pageBlockEmbedPost) {
                return 16;
            }
            if (block instanceof TL_pageBlockCollage) {
                return 17;
            }
            if (block instanceof TL_pageBlockCover) {
                return getTypeForBlock(block.cover);
            }
            return 0;
        }

        public int getItemViewType(int position) {
            return getTypeForBlock((PageBlock) ArticleViewer.this.blocks.get(position));
        }

        public int getItemCount() {
            return (ArticleViewer.this.currentPage == null || ArticleViewer.this.currentPage.cached_page == null) ? 0 : ArticleViewer.this.blocks.size();
        }
    }

    static /* synthetic */ int access$604(ArticleViewer x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    public static ArticleViewer getInstance() {
        ArticleViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (ArticleViewer.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        ArticleViewer localInstance2 = new ArticleViewer();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    protected void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            this.windowView.postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    protected void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        if (this.pendingCheckForLongPress != null) {
            this.windowView.removeCallbacks(this.pendingCheckForLongPress);
        }
        if (this.pendingCheckForTap != null) {
            this.windowView.removeCallbacks(this.pendingCheckForTap);
        }
    }

    private int getTextFlags(RichText richText) {
        if (richText instanceof TL_textFixed) {
            return getTextFlags(((TL_textFixed) richText).text) | 4;
        }
        if (richText instanceof TL_textItalic) {
            return getTextFlags(((TL_textItalic) richText).text) | 2;
        }
        if (richText instanceof TL_textBold) {
            return getTextFlags(((TL_textBold) richText).text) | 1;
        }
        if (richText instanceof TL_textUnderline) {
            return getTextFlags(((TL_textUnderline) richText).text) | 16;
        }
        if (richText instanceof TL_textStrike) {
            return getTextFlags(((TL_textStrike) richText).text) | 32;
        }
        if (richText instanceof TL_textEmail) {
            return getTextFlags(((TL_textEmail) richText).text) | 8;
        }
        if (richText instanceof TL_textUrl) {
            return getTextFlags(((TL_textUrl) richText).text) | 8;
        }
        return 0;
    }

    private CharSequence getText(RichText parentRichText, RichText richText, PageBlock parentBlock) {
        if (richText instanceof TL_textFixed) {
            return getText(parentRichText, ((TL_textFixed) richText).text, parentBlock);
        }
        if (richText instanceof TL_textItalic) {
            return getText(parentRichText, ((TL_textItalic) richText).text, parentBlock);
        }
        if (richText instanceof TL_textBold) {
            return getText(parentRichText, ((TL_textBold) richText).text, parentBlock);
        }
        if (richText instanceof TL_textUnderline) {
            return getText(parentRichText, ((TL_textUnderline) richText).text, parentBlock);
        }
        if (richText instanceof TL_textStrike) {
            return getText(parentRichText, ((TL_textStrike) richText).text, parentBlock);
        }
        CharSequence spannableStringBuilder;
        if (richText instanceof TL_textEmail) {
            spannableStringBuilder = new SpannableStringBuilder(getText(parentRichText, ((TL_textEmail) richText).text, parentBlock));
            spannableStringBuilder.setSpan(new TextPaintUrlSpan(getTextPaint(parentRichText, richText, parentBlock), getUrl(richText)), 0, spannableStringBuilder.length(), 33);
            return spannableStringBuilder;
        } else if (richText instanceof TL_textUrl) {
            spannableStringBuilder = new SpannableStringBuilder(getText(parentRichText, ((TL_textUrl) richText).text, parentBlock));
            spannableStringBuilder.setSpan(new TextPaintUrlSpan(getTextPaint(parentRichText, richText, parentBlock), getUrl(richText)), 0, spannableStringBuilder.length(), 33);
            return spannableStringBuilder;
        } else if (richText instanceof TL_textPlain) {
            return ((TL_textPlain) richText).text;
        } else {
            if (richText instanceof TL_textEmpty) {
                return "";
            }
            if (!(richText instanceof TL_textConcat)) {
                return "not supported " + richText;
            }
            spannableStringBuilder = new SpannableStringBuilder();
            int count = richText.texts.size();
            for (int a = 0; a < count; a++) {
                RichText innerRichText = (RichText) richText.texts.get(a);
                CharSequence innerText = getText(parentRichText, innerRichText, parentBlock);
                int flags = getTextFlags(innerRichText);
                int startLength = spannableStringBuilder.length();
                spannableStringBuilder.append(innerText);
                if (flags != 0) {
                    if ((flags & 8) != 0) {
                        spannableStringBuilder.setSpan(new TextPaintUrlSpan(getTextPaint(parentRichText, innerRichText, parentBlock), getUrl(innerRichText)), startLength, spannableStringBuilder.length(), 33);
                    } else {
                        spannableStringBuilder.setSpan(new TextPaintSpan(getTextPaint(parentRichText, innerRichText, parentBlock)), startLength, spannableStringBuilder.length(), 33);
                    }
                }
            }
            return spannableStringBuilder;
        }
    }

    private String getUrl(RichText richText) {
        if (richText instanceof TL_textFixed) {
            return getUrl(((TL_textFixed) richText).text);
        }
        if (richText instanceof TL_textItalic) {
            return getUrl(((TL_textItalic) richText).text);
        }
        if (richText instanceof TL_textBold) {
            return getUrl(((TL_textBold) richText).text);
        }
        if (richText instanceof TL_textUnderline) {
            return getUrl(((TL_textUnderline) richText).text);
        }
        if (richText instanceof TL_textStrike) {
            return getUrl(((TL_textStrike) richText).text);
        }
        if (richText instanceof TL_textEmail) {
            return ((TL_textEmail) richText).email;
        }
        if (richText instanceof TL_textUrl) {
            return ((TL_textUrl) richText).url;
        }
        return null;
    }

    private TextPaint getTextPaint(RichText parentRichText, RichText richText, PageBlock parentBlock) {
        int flags = getTextFlags(richText);
        HashMap<Integer, TextPaint> currentMap = null;
        int textSize = AndroidUtilities.dp(14.0f);
        int textColor = SupportMenu.CATEGORY_MASK;
        if (parentBlock instanceof TL_pageBlockPhoto) {
            currentMap = captionTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = Theme.GROUP_CREATE_STATUS_OFFLINE_TEXT_COLOR;
        } else if (parentBlock instanceof TL_pageBlockTitle) {
            currentMap = titleTextPaints;
            textSize = AndroidUtilities.dp(21.0f);
            textColor = -16777216;
        } else if (parentBlock instanceof TL_pageBlockAuthorDate) {
            currentMap = authorTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = Theme.GROUP_CREATE_STATUS_OFFLINE_TEXT_COLOR;
        } else if (parentBlock instanceof TL_pageBlockFooter) {
            currentMap = footerTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = Theme.GROUP_CREATE_STATUS_OFFLINE_TEXT_COLOR;
        } else if (parentBlock instanceof TL_pageBlockSubtitle) {
            currentMap = subtitleTextPaints;
            textSize = AndroidUtilities.dp(18.0f);
            textColor = -16777216;
        } else if (parentBlock instanceof TL_pageBlockHeader) {
            currentMap = headerTextPaints;
            textSize = AndroidUtilities.dp(24.0f);
            textColor = -16777216;
        } else if (parentBlock instanceof TL_pageBlockSubheader) {
            currentMap = subheaderTextPaints;
            textSize = AndroidUtilities.dp(21.0f);
            textColor = -16777216;
        } else if ((parentBlock instanceof TL_pageBlockBlockquote) || (parentBlock instanceof TL_pageBlockPullquote)) {
            if (parentBlock.text == parentRichText) {
                currentMap = quoteTextPaints;
                textSize = AndroidUtilities.dp(15.0f);
                textColor = -16777216;
            } else if (parentBlock.caption == parentRichText) {
                currentMap = subquoteTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = Theme.GROUP_CREATE_STATUS_OFFLINE_TEXT_COLOR;
            }
        } else if (parentBlock instanceof TL_pageBlockPreformatted) {
            currentMap = preformattedTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = -16777216;
        } else if (parentBlock instanceof TL_pageBlockParagraph) {
            currentMap = paragraphTextPaints;
            textSize = AndroidUtilities.dp(15.0f);
            textColor = -16777216;
        } else if (parentBlock instanceof TL_pageBlockList) {
            currentMap = listTextPaints;
            textSize = AndroidUtilities.dp(15.0f);
            textColor = -16777216;
        } else if (parentBlock instanceof TL_pageBlockEmbed) {
            currentMap = embedTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = Theme.GROUP_CREATE_STATUS_OFFLINE_TEXT_COLOR;
        } else if (parentBlock instanceof TL_pageBlockSlideshow) {
            currentMap = slideshowTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = Theme.GROUP_CREATE_STATUS_OFFLINE_TEXT_COLOR;
        } else if (parentBlock instanceof TL_pageBlockEmbedPost) {
            if (richText != null) {
                currentMap = embedPostTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = -16777216;
            }
        } else if (parentBlock instanceof TL_pageBlockVideo) {
            currentMap = videoTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = -16777216;
        }
        if (currentMap == null) {
            if (errorTextPaint == null) {
                errorTextPaint = new TextPaint(1);
                errorTextPaint.setColor(SupportMenu.CATEGORY_MASK);
            }
            errorTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            return errorTextPaint;
        }
        TextPaint paint = (TextPaint) currentMap.get(Integer.valueOf(flags));
        if (paint == null) {
            paint = new TextPaint(1);
            if ((flags & 4) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            } else if ((parentBlock instanceof TL_pageBlockTitle) || (parentBlock instanceof TL_pageBlockHeader) || (parentBlock instanceof TL_pageBlockSubtitle) || (parentBlock instanceof TL_pageBlockSubheader)) {
                if ((flags & 1) != 0 && (flags & 2) != 0) {
                    paint.setTypeface(Typeface.create("serif", 3));
                } else if ((flags & 1) != 0) {
                    paint.setTypeface(Typeface.create("serif", 1));
                } else if ((flags & 2) != 0) {
                    paint.setTypeface(Typeface.create("serif", 2));
                } else {
                    paint.setTypeface(Typeface.create("serif", 0));
                }
            } else if ((flags & 1) != 0 && (flags & 2) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
            } else if ((flags & 1) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if ((flags & 2) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
            }
            if ((flags & 32) != 0) {
                paint.setFlags(paint.getFlags() | 16);
            }
            if ((flags & 16) != 0) {
                paint.setFlags(paint.getFlags() | 8);
            }
            if ((flags & 8) != 0) {
                textColor = -11697229;
            }
            paint.setColor(textColor);
            currentMap.put(Integer.valueOf(flags), paint);
        }
        paint.setTextSize((float) textSize);
        return paint;
    }

    private StaticLayout createLayoutForText(String plainText, RichText richText, int width, PageBlock parentBlock) {
        if (plainText == null && (richText == null || (richText instanceof TL_textEmpty))) {
            return null;
        }
        CharSequence text;
        if (quoteLinePaint == null) {
            quoteLinePaint = new Paint();
            quoteLinePaint.setColor(-16777216);
            preformattedBackgroundPaint = new Paint();
            preformattedBackgroundPaint.setColor(-657156);
            urlPaint = new Paint();
            urlPaint.setColor(Theme.MSG_LINK_SELECT_BACKGROUND_COLOR);
        }
        if (plainText != null) {
            text = plainText;
        } else {
            text = getText(richText, richText, parentBlock);
        }
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        TextPaint paint;
        if (!(parentBlock instanceof TL_pageBlockEmbedPost) || richText != null) {
            paint = getTextPaint(richText, richText, parentBlock);
        } else if (parentBlock.author == plainText) {
            if (embedPostAuthorPaint == null) {
                embedPostAuthorPaint = new TextPaint(1);
                embedPostAuthorPaint.setColor(-16777216);
            }
            embedPostAuthorPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            paint = embedPostAuthorPaint;
        } else {
            if (embedPostDatePaint == null) {
                embedPostDatePaint = new TextPaint(1);
                embedPostDatePaint.setColor(-7366752);
            }
            embedPostDatePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            paint = embedPostDatePaint;
        }
        if (parentBlock instanceof TL_pageBlockPullquote) {
            return new StaticLayout(text, paint, width, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        }
        return new StaticLayout(text, paint, width, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
    }

    private void drawLayoutLink(Canvas canvas, StaticLayout layout) {
        if (canvas != null && this.pressedLink != null && this.pressedLinkOwnerLayout == layout && this.pressedLink != null) {
            canvas.drawPath(this.urlPath, urlPaint);
        }
    }

    private boolean checkLayoutForLinks(MotionEvent event, View parentView, StaticLayout layout, int layoutX, int layoutY) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (parentView == null || layout == null || x < layoutX || x > layout.getWidth() + layoutX || y < layoutY || y > layout.getHeight() + layoutY) {
            return false;
        }
        if (!(layout.getText() instanceof Spannable)) {
            return false;
        }
        boolean result = false;
        if (event.getAction() == 0) {
            x -= layoutX;
            try {
                int line = layout.getLineForVertical(y - layoutY);
                int off = layout.getOffsetForHorizontal(line, (float) x);
                float left = layout.getLineLeft(line);
                if (left <= ((float) x) && layout.getLineWidth(line) + left >= ((float) x)) {
                    Spannable buffer = (Spannable) layout.getText();
                    TextPaintUrlSpan[] link = (TextPaintUrlSpan[]) buffer.getSpans(off, off, TextPaintUrlSpan.class);
                    if (link != null && link.length > 0) {
                        this.pressedLink = link[0];
                        this.pressedLinkOwnerLayout = layout;
                        this.pressedLinkOwnerView = parentView;
                        try {
                            int start = buffer.getSpanStart(this.pressedLink);
                            this.urlPath.setCurrentLayout(layout, start, 0.0f);
                            layout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), this.urlPath);
                            parentView.invalidate();
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                        result = true;
                    }
                }
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
        } else if (event.getAction() == 1) {
            if (this.pressedLink != null) {
                String url = this.pressedLink.getUrl();
                boolean isAnchor = false;
                int index = url.indexOf(35);
                if (index != -1) {
                    Integer row = (Integer) this.anchors.get(url.substring(index + 1));
                    if (row != null) {
                        this.layoutManager.scrollToPositionWithOffset(row.intValue(), 0);
                        isAnchor = true;
                    }
                }
                if (!isAnchor) {
                    Browser.openUrl(this.parentActivity, this.pressedLink.getUrl());
                }
                this.pressedLink = null;
                this.pressedLinkOwnerLayout = null;
                this.pressedLinkOwnerView = null;
                parentView.invalidate();
            }
        } else if (event.getAction() == 3 && this.pressedLink != null) {
            this.pressedLink = null;
            this.pressedLinkOwnerLayout = null;
            this.pressedLinkOwnerView = null;
            parentView.invalidate();
        }
        if (result && event.getAction() == 0) {
            startCheckLongPress();
        }
        if (event.getAction() == 0 || event.getAction() == 2) {
            return result;
        }
        cancelCheckLongPress();
        return result;
    }

    private Photo getPhotoWithId(long id) {
        if (this.currentPage == null || this.currentPage.cached_page == null) {
            return null;
        }
        if (this.currentPage.photo != null && this.currentPage.photo.id == id) {
            return this.currentPage.photo;
        }
        for (int a = 0; a < this.currentPage.cached_page.photos.size(); a++) {
            Photo photo = (Photo) this.currentPage.cached_page.photos.get(a);
            if (photo.id == id) {
                return photo;
            }
        }
        return null;
    }

    private Document getDocumentWithId(long id) {
        if (this.currentPage == null || this.currentPage.cached_page == null) {
            return null;
        }
        if (this.currentPage.document != null && this.currentPage.document.id == id) {
            return this.currentPage.document;
        }
        for (int a = 0; a < this.currentPage.cached_page.videos.size(); a++) {
            Document document = (Document) this.currentPage.cached_page.videos.get(a);
            if (document.id == id) {
                return document;
            }
        }
        return null;
    }

    public void didReceivedNotification(int id, Object... args) {
        String location;
        int a;
        if (id == NotificationCenter.FileDidFailedLoad) {
            location = args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] == null || !this.currentFileNames[a].equals(location)) {
                    a++;
                } else {
                    this.radialProgressViews[a].setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
                    checkProgress(a, true);
                    return;
                }
            }
        } else if (id == NotificationCenter.FileDidLoaded) {
            location = (String) args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] == null || !this.currentFileNames[a].equals(location)) {
                    a++;
                } else {
                    this.radialProgressViews[a].setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
                    checkProgress(a, true);
                    if (a == 0 && isMediaVideo(this.currentIndex)) {
                        onActionClick(false);
                        return;
                    }
                    return;
                }
            }
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            location = (String) args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] != null && this.currentFileNames[a].equals(location)) {
                    this.radialProgressViews[a].setProgress(args[1].floatValue(), true);
                }
                a++;
            }
        } else if (id == NotificationCenter.emojiDidLoaded && this.captionTextView != null) {
            this.captionTextView.invalidate();
        }
    }

    public void setParentActivity(final Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.windowView = new FrameLayout(activity) {
                private Runnable attachRunnable;

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                    if (VERSION.SDK_INT >= 21 && ArticleViewer.this.lastInsets != null) {
                        WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            if (heightSize > AndroidUtilities.displaySize.y) {
                                heightSize = AndroidUtilities.displaySize.y;
                            }
                            heightSize += AndroidUtilities.statusBarHeight;
                        }
                        heightSize -= insets.getSystemWindowInsetBottom();
                        widthSize -= insets.getSystemWindowInsetRight();
                    } else if (heightSize > AndroidUtilities.displaySize.y) {
                        heightSize = AndroidUtilities.displaySize.y;
                    }
                    setMeasuredDimension(widthSize, heightSize);
                    ArticleViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(widthSize, C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(heightSize, C.ENCODING_PCM_32BIT));
                    ArticleViewer.this.photoContainerView.measure(MeasureSpec.makeMeasureSpec(widthSize, C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(heightSize, C.ENCODING_PCM_32BIT));
                    ViewGroup.LayoutParams layoutParams = ArticleViewer.this.animatingImageView.getLayoutParams();
                    ArticleViewer.this.animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
                }

                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    ArticleViewer.this.containerView.layout(0, 0, ArticleViewer.this.containerView.getMeasuredWidth(), ArticleViewer.this.containerView.getMeasuredHeight());
                    ArticleViewer.this.photoContainerView.layout(0, 0, ArticleViewer.this.photoContainerView.getMeasuredWidth(), ArticleViewer.this.photoContainerView.getMeasuredHeight());
                    ArticleViewer.this.animatingImageView.layout(0, 0, ArticleViewer.this.animatingImageView.getMeasuredWidth(), ArticleViewer.this.animatingImageView.getMeasuredHeight());
                }

                protected void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    ArticleViewer.this.attachedToWindow = true;
                }

                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    ArticleViewer.this.attachedToWindow = false;
                }

                public boolean dispatchKeyEventPreIme(KeyEvent event) {
                    if (event == null || event.getKeyCode() != 4 || event.getAction() != 1) {
                        return super.dispatchKeyEventPreIme(event);
                    }
                    ArticleViewer.getInstance().close();
                    return true;
                }
            };
            this.windowView.setBackgroundDrawable(this.backgroundDrawable);
            this.windowView.setClipChildren(true);
            this.windowView.setFocusable(false);
            this.containerView = new FrameLayout(activity);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            if (VERSION.SDK_INT >= 21) {
                this.containerView.setFitsSystemWindows(true);
                this.containerView.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
                    @SuppressLint({"NewApi"})
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        WindowInsets oldInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                        ArticleViewer.this.lastInsets = insets;
                        if (oldInsets == null || !oldInsets.toString().equals(insets.toString())) {
                            ArticleViewer.this.windowView.requestLayout();
                        }
                        return insets.consumeSystemWindowInsets();
                    }
                });
                this.containerView.setSystemUiVisibility(1028);
            }
            this.photoContainerView = new FrameLayoutDrawer(activity);
            this.photoContainerView.setVisibility(4);
            this.photoContainerView.setBackgroundDrawable(this.photoBackgroundDrawable);
            this.windowView.addView(this.photoContainerView, LayoutHelper.createFrame(-1, -1, 51));
            this.listView = new RecyclerListView(activity);
            RecyclerListView recyclerListView = this.listView;
            LayoutManager linearLayoutManager = new LinearLayoutManager(this.parentActivity, 1, false);
            this.layoutManager = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            recyclerListView = this.listView;
            Adapter webpageAdapter = new WebpageAdapter(this.parentActivity);
            this.adapter = webpageAdapter;
            recyclerListView.setAdapter(webpageAdapter);
            this.listView.setClipToPadding(false);
            this.listView.setPadding(0, AndroidUtilities.dp(56.0f), 0, 0);
            this.listView.setTopGlowOffset(AndroidUtilities.dp(56.0f));
            this.listView.setGlowColor(-657673);
            this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemClick(View view, int position) {
                    return false;
                }
            });
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (ArticleViewer.this.listView.getChildCount() != 0) {
                        if (ArticleViewer.this.pressedLink != null) {
                            ArticleViewer.this.pressedLink = null;
                            ArticleViewer.this.pressedLinkOwnerLayout = null;
                            ArticleViewer.this.pressedLinkOwnerView.invalidate();
                            ArticleViewer.this.cancelCheckLongPress();
                        }
                        View child = ArticleViewer.this.listView.getChildAt(0);
                        Holder holder = (Holder) ArticleViewer.this.listView.findContainingViewHolder(child);
                        int top = child.getTop();
                        int newOffset = 0;
                        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
                            newOffset = top;
                        }
                        ArticleViewer.this.checkScroll(newOffset);
                    }
                }
            });
            this.headerView = new FrameLayout(activity);
            this.headerView.setBackgroundColor(-16777216);
            this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0f));
            this.backButton = new ImageView(activity);
            this.backButton.setScaleType(ScaleType.CENTER);
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setColor(Theme.SHARE_SHEET_SEND_DISABLED_TEXT_COLOR);
            this.backButton.setImageDrawable(backDrawable);
            this.backButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0f));
            this.backButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ArticleViewer.this.close();
                }
            });
            this.shareButton = new ImageView(activity);
            this.shareButton.setScaleType(ScaleType.CENTER);
            this.shareButton.setImageResource(R.drawable.ic_share_article);
            this.shareButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.headerView.addView(this.shareButton, LayoutHelper.createFrame(54, 56, 53));
            this.shareButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ArticleViewer.this.showDialog(new ShareAlert(activity, ArticleViewer.this.currentMessageObject, null, false, null, true));
                    ArticleViewer.this.hideActionBar();
                }
            });
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            if (progressDrawables == null) {
                progressDrawables = new Drawable[4];
                progressDrawables[0] = this.parentActivity.getResources().getDrawable(R.drawable.circle_big);
                progressDrawables[1] = this.parentActivity.getResources().getDrawable(R.drawable.cancel_big);
                progressDrawables[2] = this.parentActivity.getResources().getDrawable(R.drawable.load_big);
                progressDrawables[3] = this.parentActivity.getResources().getDrawable(R.drawable.play_big);
            }
            this.scroller = new Scroller(activity);
            this.blackPaint.setColor(-16777216);
            this.animatingImageView = new ClippingImageView(activity);
            this.animatingImageView.setAnimationValues(this.animationValues);
            this.animatingImageView.setVisibility(8);
            this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            this.actionBar = new ActionBar(activity);
            this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.actionBar.setOccupyStatusBar(false);
            this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR);
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
            this.photoContainerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    int i = 1;
                    if (id == -1) {
                        ArticleViewer.this.closePhoto(true);
                    } else if (id == 1) {
                        if (VERSION.SDK_INT < 23 || ArticleViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                            File f = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
                            if (f == null || !f.exists()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ArticleViewer.this.parentActivity);
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                                ArticleViewer.this.showDialog(builder.create());
                                return;
                            }
                            String file = f.toString();
                            Context access$300 = ArticleViewer.this.parentActivity;
                            if (!ArticleViewer.this.isMediaVideo(ArticleViewer.this.currentIndex)) {
                                i = 0;
                            }
                            MediaController.saveFile(file, access$300, i, null, null);
                            return;
                        }
                        ArticleViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    } else if (id == 2) {
                        ArticleViewer.this.onSharePressed();
                    } else if (id == 3) {
                        try {
                            ArticleViewer.this.closePhoto(false);
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                    }
                }

                public boolean canOpenMenu() {
                    File f = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
                    return f != null && f.exists();
                }
            });
            this.menuItem = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_other);
            this.menuItem.addSubItem(3, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp), 0);
            this.menuItem.addSubItem(2, LocaleController.getString("ShareFile", R.string.ShareFile), 0);
            this.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", R.string.SaveToGallery), 0);
            this.bottomLayout = new FrameLayout(this.parentActivity);
            this.bottomLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.photoContainerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.captionTextViewOld = new TextView(activity);
            this.captionTextViewOld.setMaxLines(10);
            this.captionTextViewOld.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.captionTextViewOld.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
            this.captionTextViewOld.setLinkTextColor(-1);
            this.captionTextViewOld.setTextColor(-1);
            this.captionTextViewOld.setGravity(19);
            this.captionTextViewOld.setTextSize(1, 16.0f);
            this.captionTextViewOld.setVisibility(4);
            this.photoContainerView.addView(this.captionTextViewOld, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            TextView textView = new TextView(activity);
            this.captionTextViewNew = textView;
            this.captionTextView = textView;
            this.captionTextViewNew.setMaxLines(10);
            this.captionTextViewNew.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.captionTextViewNew.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
            this.captionTextViewNew.setLinkTextColor(-1);
            this.captionTextViewNew.setTextColor(-1);
            this.captionTextViewNew.setGravity(19);
            this.captionTextViewNew.setTextSize(1, 16.0f);
            this.captionTextViewNew.setVisibility(4);
            this.photoContainerView.addView(this.captionTextViewNew, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.radialProgressViews[0] = new RadialProgressView(activity, this.photoContainerView);
            this.radialProgressViews[0].setBackgroundState(0, false);
            this.radialProgressViews[1] = new RadialProgressView(activity, this.photoContainerView);
            this.radialProgressViews[1].setBackgroundState(0, false);
            this.radialProgressViews[2] = new RadialProgressView(activity, this.photoContainerView);
            this.radialProgressViews[2].setBackgroundState(0, false);
            this.sharePhotoButton = new ImageView(activity);
            this.sharePhotoButton.setImageResource(R.drawable.share);
            this.sharePhotoButton.setScaleType(ScaleType.CENTER);
            this.sharePhotoButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.bottomLayout.addView(this.sharePhotoButton, LayoutHelper.createFrame(50, -1, 53));
            this.sharePhotoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ArticleViewer.this.onSharePressed();
                }
            });
            this.videoPlayerSeekbar = new SeekBar(activity);
            this.videoPlayerSeekbar.setColors(NUM, -1, -1);
            this.videoPlayerSeekbar.setDelegate(new SeekBarDelegate() {
                public void onSeekBarDrag(float progress) {
                    if (ArticleViewer.this.videoPlayer != null) {
                        ArticleViewer.this.videoPlayer.getPlayerControl().seekTo((int) (((float) ArticleViewer.this.videoPlayer.getDuration()) * progress));
                    }
                }
            });
            this.videoPlayerControlFrameLayout = new FrameLayout(activity) {
                public boolean onTouchEvent(MotionEvent event) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    if (!ArticleViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - ((float) AndroidUtilities.dp(48.0f)), event.getY())) {
                        return super.onTouchEvent(event);
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                    invalidate();
                    return true;
                }

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    long duration;
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    if (ArticleViewer.this.videoPlayer != null) {
                        duration = ArticleViewer.this.videoPlayer.getDuration();
                        if (duration == -1) {
                            duration = 0;
                        }
                    } else {
                        duration = 0;
                    }
                    duration /= 1000;
                    ArticleViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) ArticleViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)})))), getMeasuredHeight());
                }

                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
                    float progress = 0.0f;
                    if (ArticleViewer.this.videoPlayer != null) {
                        PlayerControl playerControl = ArticleViewer.this.videoPlayer.getPlayerControl();
                        progress = ((float) playerControl.getCurrentPosition()) / ((float) playerControl.getDuration());
                    }
                    ArticleViewer.this.videoPlayerSeekbar.setProgress(progress);
                }

                protected void onDraw(Canvas canvas) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
                    ArticleViewer.this.videoPlayerSeekbar.draw(canvas);
                    canvas.restore();
                }
            };
            this.videoPlayerControlFrameLayout.setWillNotDraw(false);
            this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
            this.videoPlayButton = new ImageView(activity);
            this.videoPlayButton.setScaleType(ScaleType.CENTER);
            this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
            this.videoPlayButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ArticleViewer.this.videoPlayer == null) {
                        return;
                    }
                    if (ArticleViewer.this.isPlaying) {
                        ArticleViewer.this.videoPlayer.getPlayerControl().pause();
                    } else {
                        ArticleViewer.this.videoPlayer.getPlayerControl().start();
                    }
                }
            });
            this.videoPlayerTime = new TextView(activity);
            this.videoPlayerTime.setTextColor(-1);
            this.videoPlayerTime.setGravity(16);
            this.videoPlayerTime.setTextSize(1, 13.0f);
            this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 8.0f, 0.0f));
            this.gestureDetector = new GestureDetector(activity, this);
            this.gestureDetector.setOnDoubleTapListener(this);
            ImageReceiverDelegate imageReceiverDelegate = new ImageReceiverDelegate() {
                public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
                    if (imageReceiver != ArticleViewer.this.centerImage || !set || !ArticleViewer.this.scaleToFill()) {
                        return;
                    }
                    if (ArticleViewer.this.wasLayout) {
                        ArticleViewer.this.setScaleToFill();
                    } else {
                        ArticleViewer.this.dontResetZoomOnFirstLayout = true;
                    }
                }
            };
            this.centerImage.setParentView(this.photoContainerView);
            this.centerImage.setCrossfadeAlpha((byte) 2);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setDelegate(imageReceiverDelegate);
            this.leftImage.setParentView(this.photoContainerView);
            this.leftImage.setCrossfadeAlpha((byte) 2);
            this.leftImage.setInvalidateAll(true);
            this.leftImage.setDelegate(imageReceiverDelegate);
            this.rightImage.setParentView(this.photoContainerView);
            this.rightImage.setCrossfadeAlpha((byte) 2);
            this.rightImage.setInvalidateAll(true);
            this.rightImage.setDelegate(imageReceiverDelegate);
        }
    }

    private void checkScroll(int top) {
        int maxHeight = AndroidUtilities.dp(56.0f);
        int minHeight = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0f));
        float heightDiff = (float) (maxHeight - minHeight);
        int height = Math.max(minHeight, top);
        float scale = DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD + ((((float) (height - minHeight)) / heightDiff) * DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD);
        int scaledHeight = (int) (((float) maxHeight) * scale);
        this.backButton.setScaleX(scale);
        this.backButton.setScaleY(scale);
        this.backButton.setTranslationY((float) ((maxHeight - height) / 2));
        this.shareButton.setScaleX(scale);
        this.shareButton.setScaleY(scale);
        this.shareButton.setTranslationY((float) ((maxHeight - height) / 2));
        this.headerView.setTranslationY((float) (height - maxHeight));
        this.listView.setTopGlowOffset(height);
    }

    private void addAllMediaFromBlock(PageBlock block) {
        if ((block instanceof TL_pageBlockPhoto) || (block instanceof TL_pageBlockVideo)) {
            this.photoBlocks.add(block);
        } else if (block instanceof TL_pageBlockSlideshow) {
            TL_pageBlockSlideshow slideshow = (TL_pageBlockSlideshow) block;
            count = slideshow.items.size();
            for (a = 0; a < count; a++) {
                innerBlock = (PageBlock) slideshow.items.get(a);
                if ((innerBlock instanceof TL_pageBlockPhoto) || (innerBlock instanceof TL_pageBlockVideo)) {
                    this.photoBlocks.add(innerBlock);
                }
            }
        } else if (block instanceof TL_pageBlockCollage) {
            TL_pageBlockCollage collage = (TL_pageBlockCollage) block;
            count = collage.items.size();
            for (a = 0; a < count; a++) {
                innerBlock = (PageBlock) collage.items.get(a);
                if ((innerBlock instanceof TL_pageBlockPhoto) || (innerBlock instanceof TL_pageBlockVideo)) {
                    this.photoBlocks.add(innerBlock);
                }
            }
        } else if (block instanceof TL_pageBlockCover) {
            this.photoBlocks.add(block.cover);
        }
    }

    public boolean open(MessageObject messageObject) {
        if (this.parentActivity == null || this.isVisible || messageObject == null) {
            return false;
        }
        this.actionBar.setVisibility(8);
        this.bottomLayout.setVisibility(8);
        this.captionTextViewNew.setVisibility(8);
        this.captionTextViewOld.setVisibility(8);
        this.shareButton.setAlpha(0.0f);
        this.backButton.setAlpha(0.0f);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        checkScroll(AndroidUtilities.dp(56.0f));
        this.currentMessageObject = messageObject;
        this.currentPage = messageObject.messageOwner.media.webpage;
        this.blocks.clear();
        this.photoBlocks.clear();
        for (int a = 0; a < this.currentPage.cached_page.blocks.size(); a++) {
            PageBlock block = (PageBlock) this.currentPage.cached_page.blocks.get(a);
            if (!(block instanceof TL_pageBlockUnsupported)) {
                if (block instanceof TL_pageBlockAnchor) {
                    this.anchors.put(block.name, Integer.valueOf(this.blocks.size()));
                } else {
                    addAllMediaFromBlock(block);
                    this.blocks.add(block);
                    if (block instanceof TL_pageBlockEmbedPost) {
                        if (!block.blocks.isEmpty()) {
                            block.level = -1;
                            for (int b = 0; b < block.blocks.size(); b++) {
                                PageBlock innerBlock = (PageBlock) block.blocks.get(b);
                                if (!(innerBlock instanceof TL_pageBlockUnsupported)) {
                                    if (innerBlock instanceof TL_pageBlockAnchor) {
                                        this.anchors.put(innerBlock.name, Integer.valueOf(this.blocks.size()));
                                    } else {
                                        innerBlock.level = 1;
                                        if (b == block.blocks.size() - 1) {
                                            innerBlock.bottom = true;
                                        }
                                        this.blocks.add(innerBlock);
                                        addAllMediaFromBlock(innerBlock);
                                    }
                                }
                            }
                        }
                        if (!(block.caption instanceof TL_textEmpty)) {
                            if (!block.blocks.isEmpty()) {
                                ((PageBlock) block.blocks.get(block.blocks.size() - 1)).bottom = false;
                            }
                            TL_pageBlockParagraph caption = new TL_pageBlockParagraph();
                            caption.text = block.caption;
                            caption.bottom = true;
                            caption.level = 1;
                            this.blocks.add(caption);
                        }
                    }
                }
            }
        }
        this.adapter.notifyDataSetChanged();
        this.lastInsets = null;
        WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
        if (this.attachedToWindow) {
            try {
                wm.removeView(this.windowView);
            } catch (Exception e) {
            }
        }
        try {
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            }
            LayoutParams layoutParams = this.windowLayoutParams;
            layoutParams.flags |= 1032;
            this.windowView.setFocusable(false);
            this.containerView.setFocusable(false);
            wm.addView(this.windowView, this.windowLayoutParams);
            this.isVisible = true;
            this.animationInProgress = 1;
            this.backgroundDrawable.setAlpha(0);
            this.containerView.setAlpha(0.0f);
            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0, 255}), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
            this.animationEndRunnable = new Runnable() {
                public void run() {
                    if (ArticleViewer.this.containerView != null && ArticleViewer.this.windowView != null) {
                        if (VERSION.SDK_INT >= 18) {
                            ArticleViewer.this.containerView.setLayerType(0, null);
                        }
                        ArticleViewer.this.animationInProgress = 0;
                    }
                }
            };
            animatorSet.setDuration(200);
            animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animation) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance().setAnimationInProgress(false);
                            if (ArticleViewer.this.animationEndRunnable != null) {
                                ArticleViewer.this.animationEndRunnable.run();
                                ArticleViewer.this.animationEndRunnable = null;
                            }
                        }
                    });
                }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded});
                    NotificationCenter.getInstance().setAnimationInProgress(true);
                    animatorSet.start();
                }
            });
            if (VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, null);
            }
            showActionBar(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
            return true;
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
            return false;
        }
    }

    private void hideActionBar() {
        AnimatorSet animatorSet = new AnimatorSet();
        r1 = new Animator[2];
        r1[0] = ObjectAnimator.ofFloat(this.backButton, "alpha", new float[]{0.0f});
        r1[1] = ObjectAnimator.ofFloat(this.shareButton, "alpha", new float[]{0.0f});
        animatorSet.playTogether(r1);
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private void showActionBar(int delay) {
        AnimatorSet animatorSet = new AnimatorSet();
        r1 = new Animator[2];
        r1[0] = ObjectAnimator.ofFloat(this.backButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        r1[1] = ObjectAnimator.ofFloat(this.shareButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        animatorSet.playTogether(r1);
        animatorSet.setDuration(150);
        animatorSet.setStartDelay((long) delay);
        animatorSet.start();
    }

    public void close() {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            if (this.isPhotoVisible) {
                closePhoto(true);
                return;
            }
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            this.windowView.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & 4) == 0) {
                        FileLog.e("tmessages", "visibility changed to " + visibility);
                    } else {
                        FileLog.e("tmessages", "visibility changed to " + visibility);
                    }
                }
            });
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[4];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[]{0.9f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[]{0.9f});
            animatorArr[2] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0});
            animatorArr[3] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.animationInProgress = 2;
            this.animationEndRunnable = new Runnable() {
                public void run() {
                    if (ArticleViewer.this.containerView != null) {
                        if (VERSION.SDK_INT >= 18) {
                            ArticleViewer.this.containerView.setLayerType(0, null);
                        }
                        ArticleViewer.this.animationInProgress = 0;
                        ArticleViewer.this.onClosed();
                        ArticleViewer.this.containerView.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        ArticleViewer.this.containerView.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                }
            };
            animatorSet.setDuration(200);
            animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animation) {
                    if (ArticleViewer.this.animationEndRunnable != null) {
                        ArticleViewer.this.animationEndRunnable.run();
                        ArticleViewer.this.animationEndRunnable = null;
                    }
                }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            if (VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, null);
            }
            animatorSet.start();
        }
    }

    private void onClosed() {
        this.isVisible = false;
        this.containerView.post(new Runnable() {
            public void run() {
                try {
                    if (ArticleViewer.this.windowView.getParent() != null) {
                        ((WindowManager) ArticleViewer.this.parentActivity.getSystemService("window")).removeView(ArticleViewer.this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            if (this.animationEndRunnable != null) {
                this.animationEndRunnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = 0;
        }
        if (this.animationInProgress != 0) {
            return true;
        }
        return false;
    }

    public void destroyArticleViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            releasePlayer();
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            Instance = null;
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void showDialog(Dialog dialog) {
        if (this.parentActivity != null) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            try {
                this.visibleDialog = dialog;
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        ArticleViewer.this.showActionBar(120);
                        ArticleViewer.this.visibleDialog = null;
                    }
                });
                dialog.show();
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
        }
    }

    private void onSharePressed() {
        if (this.parentActivity != null && this.currentMedia != null) {
            try {
                File f = getMediaFile(this.currentIndex);
                if (f == null || !f.exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                    showDialog(builder.create());
                    return;
                }
                Intent intent = new Intent("android.intent.action.SEND");
                if (isMediaVideo(this.currentIndex)) {
                    intent.setType(MimeTypes.VIDEO_MP4);
                } else {
                    intent.setType("image/jpeg");
                }
                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    private void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float containerWidth = (float) getContainerViewWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        float containerHeight = (float) getContainerViewHeight();
        float scaleFit = Math.min(containerHeight / bitmapHeight, containerWidth / bitmapWidth);
        this.scale = Math.max(containerWidth / ((float) ((int) (bitmapWidth * scaleFit))), containerHeight / ((float) ((int) (bitmapHeight * scaleFit))));
        updateMinMax(this.scale);
    }

    private void updateVideoPlayerTime() {
        String newText;
        if (this.videoPlayer == null) {
            newText = "00:00 / 00:00";
        } else {
            long current = this.videoPlayer.getCurrentPosition() / 1000;
            if (this.videoPlayer.getDuration() / 1000 == -1 || current == -1) {
                newText = "00:00 / 00:00";
            } else {
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(current / 60), Long.valueOf(current % 60), Long.valueOf(total / 60), Long.valueOf(total % 60)});
            }
        }
        if (!TextUtils.equals(this.videoPlayerTime.getText(), newText)) {
            this.videoPlayerTime.setText(newText);
        }
    }

    @SuppressLint({"NewApi"})
    private void preparePlayer(File file, boolean playWhenReady) {
        if (this.parentActivity != null) {
            releasePlayer();
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
                this.aspectRatioFrameLayout.setVisibility(4);
                this.photoContainerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.videoTextureView = new TextureView(this.parentActivity);
                this.videoTextureView.setOpaque(false);
                this.videoTextureView.setSurfaceTextureListener(new SurfaceTextureListener() {
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        if (ArticleViewer.this.videoPlayer != null) {
                            ArticleViewer.this.videoPlayer.setSurface(new Surface(ArticleViewer.this.videoTextureView.getSurfaceTexture()));
                        }
                    }

                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    }

                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        if (ArticleViewer.this.videoPlayer != null) {
                            ArticleViewer.this.videoPlayer.blockingClearSurface();
                        }
                        return true;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                        if (!ArticleViewer.this.textureUploaded) {
                            ArticleViewer.this.textureUploaded = true;
                            ArticleViewer.this.photoContainerView.invalidate();
                        }
                    }
                });
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView.setAlpha(0.0f);
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            if (this.videoPlayer == null) {
                long duration;
                this.videoPlayer = new VideoPlayer(new ExtractorRendererBuilder(this.parentActivity, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36", Uri.fromFile(file)));
                this.videoPlayer.addListener(new Listener() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (ArticleViewer.this.videoPlayer != null) {
                            if (playbackState == 5 || playbackState == 1) {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                                } catch (Throwable e) {
                                    FileLog.e("tmessages", e);
                                }
                            } else {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                                } catch (Throwable e2) {
                                    FileLog.e("tmessages", e2);
                                }
                            }
                            if (playbackState == 4 && ArticleViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                ArticleViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!ArticleViewer.this.videoPlayer.getPlayerControl().isPlaying() || playbackState == 5) {
                                if (ArticleViewer.this.isPlaying) {
                                    ArticleViewer.this.isPlaying = false;
                                    ArticleViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(ArticleViewer.this.updateProgressRunnable);
                                    if (playbackState == 5 && !ArticleViewer.this.videoPlayerSeekbar.isDragging()) {
                                        ArticleViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                        ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
                                        ArticleViewer.this.videoPlayer.seekTo(0);
                                        ArticleViewer.this.videoPlayer.getPlayerControl().pause();
                                    }
                                }
                            } else if (!ArticleViewer.this.isPlaying) {
                                ArticleViewer.this.isPlaying = true;
                                ArticleViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable);
                            }
                            ArticleViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception e) {
                        FileLog.e("tmessages", (Throwable) e);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        if (ArticleViewer.this.aspectRatioFrameLayout != null) {
                            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                                int temp = width;
                                width = height;
                                height = temp;
                            }
                            ArticleViewer.this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : (((float) width) * pixelWidthHeightRatio) / ((float) height), unappliedRotationDegrees);
                        }
                    }
                });
                if (this.videoPlayer != null) {
                    duration = this.videoPlayer.getDuration();
                    if (duration == -1) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                duration /= 1000;
                int size = (int) Math.ceil((double) this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)})));
                this.playerNeedsPrepare = true;
            }
            if (this.playerNeedsPrepare) {
                this.videoPlayer.prepare();
                this.playerNeedsPrepare = false;
            }
            if (this.videoPlayerControlFrameLayout != null) {
                this.videoPlayerControlFrameLayout.setVisibility(0);
                this.sharePhotoButton.setVisibility(8);
                this.menuItem.showSubItem(2);
            }
            if (this.videoTextureView.getSurfaceTexture() != null) {
                this.videoPlayer.setSurface(new Surface(this.videoTextureView.getSurfaceTexture()));
            }
            this.videoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    private void releasePlayer() {
        if (this.videoPlayer != null) {
            this.videoPlayer.release();
            this.videoPlayer = null;
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        if (this.aspectRatioFrameLayout != null) {
            this.photoContainerView.removeView(this.aspectRatioFrameLayout);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        if (this.videoPlayerControlFrameLayout != null) {
            this.videoPlayerControlFrameLayout.setVisibility(8);
            this.sharePhotoButton.setVisibility(0);
            this.menuItem.hideSubItem(2);
        }
    }

    private void toggleActionBar(boolean show, boolean animated) {
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        if (show) {
            this.actionBar.setVisibility(0);
            this.bottomLayout.setVisibility(0);
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(0);
            }
        }
        this.isActionBarVisible = show;
        this.actionBar.setEnabled(show);
        this.bottomLayout.setEnabled(show);
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList();
            ActionBar actionBar = this.actionBar;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = show ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, str, fArr));
            FrameLayout frameLayout = this.bottomLayout;
            str = "alpha";
            fArr = new float[1];
            fArr[0] = show ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, str2, fArr2));
            }
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(arrayList);
            if (!show) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animation) {
                        if (ArticleViewer.this.currentActionBarAnimation != null && ArticleViewer.this.currentActionBarAnimation.equals(animation)) {
                            ArticleViewer.this.actionBar.setVisibility(8);
                            ArticleViewer.this.bottomLayout.setVisibility(8);
                            if (ArticleViewer.this.captionTextView.getTag() != null) {
                                ArticleViewer.this.captionTextView.setVisibility(4);
                            }
                            ArticleViewer.this.currentActionBarAnimation = null;
                        }
                    }
                });
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        this.actionBar.setAlpha(show ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f);
        this.bottomLayout.setAlpha(show ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f);
        if (this.captionTextView.getTag() != null) {
            textView = this.captionTextView;
            if (!show) {
                f = 0.0f;
            }
            textView.setAlpha(f);
        }
        if (!show) {
            this.actionBar.setVisibility(8);
            this.bottomLayout.setVisibility(8);
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(4);
            }
        }
    }

    private String getFileName(int index) {
        return FileLoader.getAttachFileName(getMedia(index));
    }

    private TLObject getMedia(int index) {
        if (this.imagesArr.isEmpty() || index >= this.imagesArr.size() || index < 0) {
            return null;
        }
        PageBlock block = (PageBlock) this.imagesArr.get(index);
        if (block.photo_id != 0) {
            return getPhotoWithId(block.photo_id);
        }
        if (block.video_id != 0) {
            return getDocumentWithId(block.video_id);
        }
        return null;
    }

    private File getMediaFile(int index) {
        if (this.imagesArr.isEmpty() || index >= this.imagesArr.size() || index < 0) {
            return null;
        }
        PageBlock block = (PageBlock) this.imagesArr.get(index);
        if (block.photo_id != 0) {
            Photo photo = getPhotoWithId(block.photo_id);
            if (photo == null) {
                return null;
            }
            PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
            if (sizeFull != null) {
                return FileLoader.getPathToAttach(sizeFull, true);
            }
            return null;
        } else if (block.video_id == 0) {
            return null;
        } else {
            Document document = getDocumentWithId(block.video_id);
            if (document != null) {
                return FileLoader.getPathToAttach(document);
            }
            return null;
        }
    }

    private boolean isMediaVideo(int index) {
        if (this.imagesArr.isEmpty() || index >= this.imagesArr.size() || index < 0) {
            return false;
        }
        PageBlock block = (PageBlock) this.imagesArr.get(index);
        if (block.video_id == 0) {
            return false;
        }
        Document document = getDocumentWithId(block.video_id);
        if (document != null) {
            return MessageObject.isVideoDocument(document);
        }
        return false;
    }

    private FileLocation getFileLocation(int index, int[] size) {
        if (index < 0 || index >= this.imagesArr.size()) {
            return null;
        }
        TLObject media = getMedia(index);
        if (media instanceof Photo) {
            PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(((Photo) media).sizes, AndroidUtilities.getPhotoSize());
            if (sizeFull != null) {
                size[0] = sizeFull.size;
                if (size[0] == 0) {
                    size[0] = -1;
                }
                return sizeFull.location;
            }
            size[0] = -1;
            return null;
        } else if (!(media instanceof Document)) {
            return null;
        } else {
            Document document = (Document) media;
            if (document.thumb == null) {
                return null;
            }
            size[0] = document.thumb.size;
            if (size[0] == 0) {
                size[0] = -1;
            }
            return document.thumb.location;
        }
    }

    private void onPhotoShow(int index, PlaceProviderObject object) {
        Bitmap bitmap;
        this.currentIndex = -1;
        this.currentFileNames[0] = null;
        this.currentFileNames[1] = null;
        this.currentFileNames[2] = null;
        this.imagesArr.clear();
        if (object != null) {
            bitmap = object.thumb;
        } else {
            bitmap = null;
        }
        this.currentThumb = bitmap;
        this.menuItem.setVisibility(0);
        this.bottomLayout.setVisibility(0);
        this.bottomLayout.setTranslationY(0.0f);
        this.sharePhotoButton.setVisibility(8);
        this.menuItem.hideSubItem(2);
        this.menuItem.hideSubItem(3);
        this.actionBar.setTranslationY(0.0f);
        this.captionTextView.setTag(null);
        this.captionTextView.setVisibility(4);
        for (int a = 0; a < 3; a++) {
            if (this.radialProgressViews[a] != null) {
                this.radialProgressViews[a].setBackgroundState(-1, false);
            }
        }
        this.imagesArr.addAll(this.photoBlocks);
        setImageIndex(index, true);
        if (this.currentMedia != null && isMediaVideo(this.currentIndex)) {
            onActionClick(false);
        }
    }

    private void setImages() {
        if (this.photoAnimationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + 1);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    private void setImageIndex(int index, boolean init) {
        if (this.currentIndex != index) {
            if (!init) {
                this.currentThumb = null;
            }
            this.currentFileNames[0] = getFileName(index);
            this.currentFileNames[1] = getFileName(index + 1);
            this.currentFileNames[2] = getFileName(index - 1);
            int prevIndex = this.currentIndex;
            this.currentIndex = index;
            boolean isVideo = false;
            boolean sameImage = false;
            if (!this.imagesArr.isEmpty()) {
                if (this.currentIndex < 0 || this.currentIndex >= this.imagesArr.size()) {
                    closePhoto(false);
                    return;
                }
                PageBlock newMedia = (PageBlock) this.imagesArr.get(this.currentIndex);
                sameImage = this.currentMedia != null && this.currentMedia == newMedia;
                this.currentMedia = newMedia;
                isVideo = isMediaVideo(this.currentIndex);
                if (isVideo) {
                    this.menuItem.showSubItem(3);
                }
                setCurrentCaption(getText(this.currentMedia.caption, this.currentMedia.caption, this.currentMedia));
                if (this.currentAnimation != null) {
                    this.menuItem.hideSubItem(1);
                    this.menuItem.hideSubItem(2);
                    this.sharePhotoButton.setVisibility(0);
                    this.actionBar.setTitle(LocaleController.getString("AttachGif", R.string.AttachGif));
                } else {
                    if (this.imagesArr.size() != 1) {
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArr.size())));
                    } else if (isVideo) {
                        this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                    } else {
                        this.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                    }
                    this.menuItem.showSubItem(1);
                    ImageView imageView = this.sharePhotoButton;
                    int i = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? 0 : 8;
                    imageView.setVisibility(i);
                    if (this.sharePhotoButton.getVisibility() == 0) {
                        this.menuItem.hideSubItem(2);
                    } else {
                        this.menuItem.showSubItem(2);
                    }
                }
            }
            if (this.currentPlaceObject != null) {
                if (this.photoAnimationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(true, true);
                } else {
                    this.showAfterAnimation = this.currentPlaceObject;
                }
            }
            this.currentPlaceObject = getPlaceForPhoto(this.currentMedia);
            if (this.currentPlaceObject != null) {
                if (this.photoAnimationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(false, true);
                } else {
                    this.hideAfterAnimation = this.currentPlaceObject;
                }
            }
            if (!sameImage) {
                this.draggingDown = false;
                this.translationX = 0.0f;
                this.translationY = 0.0f;
                this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                this.animateToX = 0.0f;
                this.animateToY = 0.0f;
                this.animateToScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                this.animationStartTime = 0;
                this.imageMoveAnimation = null;
                if (this.aspectRatioFrameLayout != null) {
                    this.aspectRatioFrameLayout.setVisibility(4);
                }
                releasePlayer();
                this.pinchStartDistance = 0.0f;
                this.pinchStartScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                this.pinchCenterX = 0.0f;
                this.pinchCenterY = 0.0f;
                this.pinchStartX = 0.0f;
                this.pinchStartY = 0.0f;
                this.moveStartX = 0.0f;
                this.moveStartY = 0.0f;
                this.zooming = false;
                this.moving = false;
                this.doubleTap = false;
                this.invalidCoords = false;
                this.canDragDown = true;
                this.changingPage = false;
                this.switchImageAfterAnimation = 0;
                boolean z = (this.currentFileNames[0] == null || isVideo || this.radialProgressViews[0].backgroundState == 0) ? false : true;
                this.canZoom = z;
                updateMinMax(this.scale);
            }
            if (prevIndex == -1) {
                setImages();
                for (int a = 0; a < 3; a++) {
                    checkProgress(a, false);
                }
                return;
            }
            checkProgress(0, false);
            ImageReceiver temp;
            RadialProgressView tempProgress;
            if (prevIndex > this.currentIndex) {
                temp = this.rightImage;
                this.rightImage = this.centerImage;
                this.centerImage = this.leftImage;
                this.leftImage = temp;
                tempProgress = this.radialProgressViews[0];
                this.radialProgressViews[0] = this.radialProgressViews[2];
                this.radialProgressViews[2] = tempProgress;
                setIndexToImage(this.leftImage, this.currentIndex - 1);
                checkProgress(1, false);
                checkProgress(2, false);
            } else if (prevIndex < this.currentIndex) {
                temp = this.leftImage;
                this.leftImage = this.centerImage;
                this.centerImage = this.rightImage;
                this.rightImage = temp;
                tempProgress = this.radialProgressViews[0];
                this.radialProgressViews[0] = this.radialProgressViews[1];
                this.radialProgressViews[1] = tempProgress;
                setIndexToImage(this.rightImage, this.currentIndex + 1);
                checkProgress(1, false);
                checkProgress(2, false);
            }
        }
    }

    private void setCurrentCaption(CharSequence caption) {
        if (TextUtils.isEmpty(caption)) {
            this.captionTextView.setTextColor(-1);
            this.captionTextView.setTag(null);
            this.captionTextView.setVisibility(4);
            return;
        }
        this.captionTextView = this.captionTextViewOld;
        this.captionTextViewOld = this.captionTextViewNew;
        this.captionTextViewNew = this.captionTextView;
        CharSequence str = Emoji.replaceEmoji(new SpannableStringBuilder(caption.toString()), MessageObject.getTextPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        this.captionTextView.setTag(str);
        this.captionTextView.setText(str);
        this.captionTextView.setTextColor(-1);
        this.captionTextView.setAlpha(this.bottomLayout.getVisibility() == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = 4;
                ArticleViewer.this.captionTextViewOld.setTag(null);
                ArticleViewer.this.captionTextViewOld.setVisibility(4);
                TextView access$7200 = ArticleViewer.this.captionTextViewNew;
                if (ArticleViewer.this.bottomLayout.getVisibility() == 0) {
                    i = 0;
                }
                access$7200.setVisibility(i);
            }
        });
    }

    private void checkProgress(int a, boolean animated) {
        boolean z = true;
        if (this.currentFileNames[a] != null) {
            int index = this.currentIndex;
            if (a == 1) {
                index++;
            } else if (a == 2) {
                index--;
            }
            File f = getMediaFile(index);
            boolean isVideo = isMediaVideo(index);
            if (f == null || !f.exists()) {
                if (!isVideo) {
                    this.radialProgressViews[a].setBackgroundState(0, animated);
                } else if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[a])) {
                    this.radialProgressViews[a].setBackgroundState(1, false);
                } else {
                    this.radialProgressViews[a].setBackgroundState(2, false);
                }
                Float progress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[a]);
                if (progress == null) {
                    progress = Float.valueOf(0.0f);
                }
                this.radialProgressViews[a].setProgress(progress.floatValue(), false);
            } else if (isVideo) {
                this.radialProgressViews[a].setBackgroundState(3, animated);
            } else {
                this.radialProgressViews[a].setBackgroundState(-1, animated);
            }
            if (a == 0) {
                if (this.currentFileNames[0] == null || isVideo || this.radialProgressViews[0].backgroundState == 0) {
                    z = false;
                }
                this.canZoom = z;
                return;
            }
            return;
        }
        this.radialProgressViews[a].setBackgroundState(-1, animated);
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int index) {
        imageReceiver.setOrientation(0, false);
        int[] size = new int[1];
        FileLocation fileLocation = getFileLocation(index, size);
        if (fileLocation != null) {
            TLObject media = getMedia(index);
            Bitmap placeHolder;
            if (media instanceof Photo) {
                Drawable bitmapDrawable;
                FileLocation fileLocation2;
                Photo photo = (Photo) media;
                placeHolder = null;
                if (this.currentThumb != null && imageReceiver == this.centerImage) {
                    placeHolder = this.currentThumb;
                }
                if (size[0] == 0) {
                    size[0] = -1;
                }
                PhotoSize thumbLocation = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80);
                if (placeHolder != null) {
                    bitmapDrawable = new BitmapDrawable(null, placeHolder);
                } else {
                    bitmapDrawable = null;
                }
                if (thumbLocation != null) {
                    fileLocation2 = thumbLocation.location;
                } else {
                    fileLocation2 = null;
                }
                imageReceiver.setImage(fileLocation, null, null, bitmapDrawable, fileLocation2, "b", size[0], null, false);
            } else if (isMediaVideo(index)) {
                if (fileLocation instanceof TL_fileLocationUnavailable) {
                    imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                    return;
                }
                placeHolder = null;
                if (this.currentThumb != null && imageReceiver == this.centerImage) {
                    placeHolder = this.currentThumb;
                }
                imageReceiver.setImage(null, null, null, placeHolder != null ? new BitmapDrawable(null, placeHolder) : null, fileLocation, "b", 0, null, true);
            } else if (this.currentAnimation != null) {
                imageReceiver.setImageBitmap(this.currentAnimation);
                this.currentAnimation.setSecondParentView(this.photoContainerView);
            }
        } else if (size[0] == 0) {
            imageReceiver.setImageBitmap((Bitmap) null);
        } else {
            imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
        }
    }

    public boolean isShowingImage(PageBlock object) {
        return this.isPhotoVisible && !this.disableShowCheck && object != null && this.currentMedia == object;
    }

    private boolean checkPhotoAnimation() {
        if (this.photoAnimationInProgress != 0 && Math.abs(this.photoTransitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            if (this.photoAnimationEndRunnable != null) {
                this.photoAnimationEndRunnable.run();
                this.photoAnimationEndRunnable = null;
            }
            this.photoAnimationInProgress = 0;
        }
        if (this.photoAnimationInProgress != 0) {
            return true;
        }
        return false;
    }

    public boolean openPhoto(PageBlock block) {
        if (this.parentActivity == null || this.isPhotoVisible || checkPhotoAnimation() || block == null) {
            return false;
        }
        final PlaceProviderObject object = getPlaceForPhoto(block);
        if (object == null) {
            return false;
        }
        float scale;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.isPhotoVisible = true;
        toggleActionBar(true, false);
        this.actionBar.setAlpha(0.0f);
        this.bottomLayout.setAlpha(0.0f);
        this.captionTextView.setAlpha(0.0f);
        this.photoBackgroundDrawable.setAlpha(0);
        this.disableShowCheck = true;
        this.photoAnimationInProgress = 1;
        if (block != null) {
            this.currentAnimation = object.imageReceiver.getAnimation();
        }
        onPhotoShow(this.photoBlocks.indexOf(block), object);
        Rect drawRegion = object.imageReceiver.getDrawRegion();
        int orientation = object.imageReceiver.getOrientation();
        int animatedOrientation = object.imageReceiver.getAnimatedOrientation();
        if (animatedOrientation != 0) {
            orientation = animatedOrientation;
        }
        this.animatingImageView.setVisibility(0);
        this.animatingImageView.setRadius(object.radius);
        this.animatingImageView.setOrientation(orientation);
        this.animatingImageView.setNeedRadius(object.radius != 0);
        this.animatingImageView.setImageBitmap(object.thumb);
        this.animatingImageView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.animatingImageView.setPivotX(0.0f);
        this.animatingImageView.setPivotY(0.0f);
        this.animatingImageView.setScaleX(object.scale);
        this.animatingImageView.setScaleY(object.scale);
        this.animatingImageView.setTranslationX(((float) object.viewX) + (((float) drawRegion.left) * object.scale));
        this.animatingImageView.setTranslationY(((float) object.viewY) + (((float) drawRegion.top) * object.scale));
        ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
        layoutParams.width = drawRegion.right - drawRegion.left;
        layoutParams.height = drawRegion.bottom - drawRegion.top;
        this.animatingImageView.setLayoutParams(layoutParams);
        float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
        float scaleY = ((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) / ((float) layoutParams.height);
        if (scaleX > scaleY) {
            scale = scaleY;
        } else {
            scale = scaleX;
        }
        float xPos = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * scale)) / 2.0f;
        float yPos = (((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) - (((float) layoutParams.height) * scale)) / 2.0f;
        int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
        int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());
        int[] coords2 = new int[2];
        object.parentView.getLocationInWindow(coords2);
        int clipTop = ((coords2[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (object.viewY + drawRegion.top)) + object.clipTopAddition;
        if (clipTop < 0) {
            clipTop = 0;
        }
        int clipBottom = ((layoutParams.height + (object.viewY + drawRegion.top)) - ((object.parentView.getHeight() + coords2[1]) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + object.clipBottomAddition;
        if (clipBottom < 0) {
            clipBottom = 0;
        }
        clipTop = Math.max(clipTop, clipVertical);
        clipBottom = Math.max(clipBottom, clipVertical);
        this.animationValues[0][0] = this.animatingImageView.getScaleX();
        this.animationValues[0][1] = this.animatingImageView.getScaleY();
        this.animationValues[0][2] = this.animatingImageView.getTranslationX();
        this.animationValues[0][3] = this.animatingImageView.getTranslationY();
        this.animationValues[0][4] = ((float) clipHorizontal) * object.scale;
        this.animationValues[0][5] = ((float) clipTop) * object.scale;
        this.animationValues[0][6] = ((float) clipBottom) * object.scale;
        this.animationValues[0][7] = (float) this.animatingImageView.getRadius();
        this.animationValues[1][0] = scale;
        this.animationValues[1][1] = scale;
        this.animationValues[1][2] = xPos;
        this.animationValues[1][3] = yPos;
        this.animationValues[1][4] = 0.0f;
        this.animationValues[1][5] = 0.0f;
        this.animationValues[1][6] = 0.0f;
        this.animationValues[1][7] = 0.0f;
        this.photoContainerView.setVisibility(0);
        this.animatingImageView.setAnimationProgress(0.0f);
        final AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[5];
        float[] fArr = new float[2];
        animatorArr[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        int[] iArr = new int[2];
        animatorArr[1] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[]{0, 255});
        fArr = new float[2];
        animatorArr[2] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        fArr = new float[2];
        animatorArr[3] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        fArr = new float[2];
        animatorArr[4] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        animatorSet.playTogether(animatorArr);
        this.photoAnimationEndRunnable = new Runnable() {
            public void run() {
                if (ArticleViewer.this.photoContainerView != null) {
                    if (VERSION.SDK_INT >= 18) {
                        ArticleViewer.this.photoContainerView.setLayerType(0, null);
                    }
                    ArticleViewer.this.photoAnimationInProgress = 0;
                    ArticleViewer.this.photoTransitionAnimationStartTime = 0;
                    ArticleViewer.this.setImages();
                    ArticleViewer.this.photoContainerView.invalidate();
                    ArticleViewer.this.animatingImageView.setVisibility(8);
                    if (ArticleViewer.this.showAfterAnimation != null) {
                        ArticleViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
                    }
                    if (ArticleViewer.this.hideAfterAnimation != null) {
                        ArticleViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
                    }
                }
            }
        };
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapterProxy() {
            public void onAnimationEnd(Animator animation) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance().setAnimationInProgress(false);
                        if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                            ArticleViewer.this.photoAnimationEndRunnable.run();
                            ArticleViewer.this.photoAnimationEndRunnable = null;
                        }
                    }
                });
            }
        });
        this.photoTransitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded});
                NotificationCenter.getInstance().setAnimationInProgress(true);
                animatorSet.start();
            }
        });
        if (VERSION.SDK_INT >= 18) {
            this.photoContainerView.setLayerType(2, null);
        }
        this.photoBackgroundDrawable.drawRunnable = new Runnable() {
            public void run() {
                ArticleViewer.this.disableShowCheck = false;
                object.imageReceiver.setVisible(false, true);
            }
        };
        return true;
    }

    public void closePhoto(boolean animated) {
        if (this.parentActivity != null && this.isPhotoVisible && !checkPhotoAnimation()) {
            releasePlayer();
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            this.isActionBarVisible = false;
            if (this.velocityTracker != null) {
                this.velocityTracker.recycle();
                this.velocityTracker = null;
            }
            final PlaceProviderObject object = getPlaceForPhoto(this.currentMedia);
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (animated) {
                float scale2;
                this.photoAnimationInProgress = 1;
                this.animatingImageView.setVisibility(0);
                this.photoContainerView.invalidate();
                animatorSet = new AnimatorSet();
                ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                Rect drawRegion = null;
                int orientation = this.centerImage.getOrientation();
                int animatedOrientation = 0;
                if (!(object == null || object.imageReceiver == null)) {
                    animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                }
                if (animatedOrientation != 0) {
                    orientation = animatedOrientation;
                }
                this.animatingImageView.setOrientation(orientation);
                if (object != null) {
                    this.animatingImageView.setNeedRadius(object.radius != 0);
                    drawRegion = object.imageReceiver.getDrawRegion();
                    layoutParams.width = drawRegion.right - drawRegion.left;
                    layoutParams.height = drawRegion.bottom - drawRegion.top;
                    this.animatingImageView.setImageBitmap(object.thumb);
                } else {
                    this.animatingImageView.setNeedRadius(false);
                    layoutParams.width = this.centerImage.getImageWidth();
                    layoutParams.height = this.centerImage.getImageHeight();
                    this.animatingImageView.setImageBitmap(this.centerImage.getBitmap());
                }
                this.animatingImageView.setLayoutParams(layoutParams);
                float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                float scaleY = ((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) / ((float) layoutParams.height);
                if (scaleX > scaleY) {
                    scale2 = scaleY;
                } else {
                    scale2 = scaleX;
                }
                float yPos = (((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) - ((((float) layoutParams.height) * this.scale) * scale2)) / 2.0f;
                this.animatingImageView.setTranslationX(this.translationX + ((((float) AndroidUtilities.displaySize.x) - ((((float) layoutParams.width) * this.scale) * scale2)) / 2.0f));
                this.animatingImageView.setTranslationY(this.translationY + yPos);
                this.animatingImageView.setScaleX(this.scale * scale2);
                this.animatingImageView.setScaleY(this.scale * scale2);
                if (object != null) {
                    object.imageReceiver.setVisible(false, true);
                    int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                    int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());
                    int[] coords2 = new int[2];
                    object.parentView.getLocationInWindow(coords2);
                    int clipTop = ((coords2[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (object.viewY + drawRegion.top)) + object.clipTopAddition;
                    if (clipTop < 0) {
                        clipTop = 0;
                    }
                    int clipBottom = (((drawRegion.bottom - drawRegion.top) + (object.viewY + drawRegion.top)) - ((object.parentView.getHeight() + coords2[1]) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + object.clipBottomAddition;
                    if (clipBottom < 0) {
                        clipBottom = 0;
                    }
                    clipTop = Math.max(clipTop, clipVertical);
                    clipBottom = Math.max(clipBottom, clipVertical);
                    this.animationValues[0][0] = this.animatingImageView.getScaleX();
                    this.animationValues[0][1] = this.animatingImageView.getScaleY();
                    this.animationValues[0][2] = this.animatingImageView.getTranslationX();
                    this.animationValues[0][3] = this.animatingImageView.getTranslationY();
                    this.animationValues[0][4] = 0.0f;
                    this.animationValues[0][5] = 0.0f;
                    this.animationValues[0][6] = 0.0f;
                    this.animationValues[0][7] = 0.0f;
                    this.animationValues[1][0] = object.scale;
                    this.animationValues[1][1] = object.scale;
                    this.animationValues[1][2] = ((float) object.viewX) + (((float) drawRegion.left) * object.scale);
                    this.animationValues[1][3] = ((float) object.viewY) + (((float) drawRegion.top) * object.scale);
                    this.animationValues[1][4] = ((float) clipHorizontal) * object.scale;
                    this.animationValues[1][5] = ((float) clipTop) * object.scale;
                    this.animationValues[1][6] = ((float) clipBottom) * object.scale;
                    this.animationValues[1][7] = (float) object.radius;
                    animatorArr = new Animator[5];
                    float[] fArr = new float[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                    animatorArr[1] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[]{0});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    float f;
                    int h = AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                    r24 = new Animator[6];
                    r24[0] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[]{0});
                    r24[1] = ObjectAnimator.ofFloat(this.animatingImageView, "alpha", new float[]{0.0f});
                    ClippingImageView clippingImageView = this.animatingImageView;
                    String str = "translationY";
                    float[] fArr2 = new float[1];
                    if (this.translationY >= 0.0f) {
                        f = (float) h;
                    } else {
                        f = (float) (-h);
                    }
                    fArr2[0] = f;
                    r24[2] = ObjectAnimator.ofFloat(clippingImageView, str, fArr2);
                    r24[3] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                    r24[4] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
                    r24[5] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(r24);
                }
                this.photoAnimationEndRunnable = new Runnable() {
                    public void run() {
                        if (VERSION.SDK_INT >= 18) {
                            ArticleViewer.this.photoContainerView.setLayerType(0, null);
                        }
                        ArticleViewer.this.photoContainerView.setVisibility(4);
                        ArticleViewer.this.photoAnimationInProgress = 0;
                        ArticleViewer.this.onPhotoClosed(object);
                    }
                };
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animation) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                                    ArticleViewer.this.photoAnimationEndRunnable.run();
                                    ArticleViewer.this.photoAnimationEndRunnable = null;
                                }
                            }
                        });
                    }
                });
                this.photoTransitionAnimationStartTime = System.currentTimeMillis();
                if (VERSION.SDK_INT >= 18) {
                    this.photoContainerView.setLayerType(2, null);
                }
                animatorSet.start();
            } else {
                animatorSet = new AnimatorSet();
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.photoContainerView, "scaleX", new float[]{0.9f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.photoContainerView, "scaleY", new float[]{0.9f});
                animatorArr[2] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[]{0});
                animatorArr[3] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
                this.photoAnimationInProgress = 2;
                this.photoAnimationEndRunnable = new Runnable() {
                    public void run() {
                        if (ArticleViewer.this.photoContainerView != null) {
                            if (VERSION.SDK_INT >= 18) {
                                ArticleViewer.this.photoContainerView.setLayerType(0, null);
                            }
                            ArticleViewer.this.photoContainerView.setVisibility(4);
                            ArticleViewer.this.photoAnimationInProgress = 0;
                            ArticleViewer.this.onPhotoClosed(object);
                            ArticleViewer.this.photoContainerView.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            ArticleViewer.this.photoContainerView.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        }
                    }
                };
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animation) {
                        if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                            ArticleViewer.this.photoAnimationEndRunnable.run();
                            ArticleViewer.this.photoAnimationEndRunnable = null;
                        }
                    }
                });
                this.photoTransitionAnimationStartTime = System.currentTimeMillis();
                if (VERSION.SDK_INT >= 18) {
                    this.photoContainerView.setLayerType(2, null);
                }
                animatorSet.start();
            }
            if (this.currentAnimation != null) {
                this.currentAnimation.setSecondParentView(null);
                this.currentAnimation = null;
                this.centerImage.setImageBitmap((Drawable) null);
            }
        }
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        this.isPhotoVisible = false;
        this.disableShowCheck = true;
        this.currentMedia = null;
        this.currentThumb = null;
        if (this.currentAnimation != null) {
            this.currentAnimation.setSecondParentView(null);
            this.currentAnimation = null;
        }
        for (int a = 0; a < 3; a++) {
            if (this.radialProgressViews[a] != null) {
                this.radialProgressViews[a].setBackgroundState(-1, false);
            }
        }
        this.centerImage.setImageBitmap((Bitmap) null);
        this.leftImage.setImageBitmap((Bitmap) null);
        this.rightImage.setImageBitmap((Bitmap) null);
        this.photoContainerView.post(new Runnable() {
            public void run() {
                ArticleViewer.this.animatingImageView.setImageBitmap(null);
            }
        });
        this.disableShowCheck = false;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false);
        }
    }

    private void updateMinMax(float scale) {
        int maxW = ((int) ((((float) this.centerImage.getImageWidth()) * scale) - ((float) getContainerViewWidth()))) / 2;
        int maxH = ((int) ((((float) this.centerImage.getImageHeight()) * scale) - ((float) getContainerViewHeight()))) / 2;
        if (maxW > 0) {
            this.minX = (float) (-maxW);
            this.maxX = (float) maxW;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (maxH > 0) {
            this.minY = (float) (-maxH);
            this.maxY = (float) maxH;
            return;
        }
        this.maxY = 0.0f;
        this.minY = 0.0f;
    }

    private int getContainerViewWidth() {
        return this.photoContainerView.getWidth();
    }

    private int getContainerViewHeight() {
        int height = AndroidUtilities.displaySize.y;
        if (VERSION.SDK_INT >= 21) {
            return height + AndroidUtilities.statusBarHeight;
        }
        return height;
    }

    private boolean processTouchEvent(MotionEvent ev) {
        if (this.photoAnimationInProgress != 0 || this.animationStartTime != 0) {
            return false;
        }
        if (ev.getPointerCount() == 1 && this.gestureDetector.onTouchEvent(ev) && this.doubleTap) {
            this.doubleTap = false;
            this.moving = false;
            this.zooming = false;
            checkMinMax(false);
            return true;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            this.discardTap = false;
            if (!this.scroller.isFinished()) {
                this.scroller.abortAnimation();
            }
            if (!(this.draggingDown || this.changingPage)) {
                if (this.canZoom && ev.getPointerCount() == 2) {
                    this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                    this.pinchStartScale = this.scale;
                    this.pinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                    this.pinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                    this.pinchStartX = this.translationX;
                    this.pinchStartY = this.translationY;
                    this.zooming = true;
                    this.moving = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                } else if (ev.getPointerCount() == 1) {
                    this.moveStartX = ev.getX();
                    float y = ev.getY();
                    this.moveStartY = y;
                    this.dragY = y;
                    this.draggingDown = false;
                    this.canDragDown = true;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                }
            }
        } else if (ev.getActionMasked() == 2) {
            if (this.canZoom && ev.getPointerCount() == 2 && !this.draggingDown && this.zooming && !this.changingPage) {
                this.discardTap = true;
                this.scale = (((float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                this.translationY = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (this.scale / this.pinchStartScale));
                updateMinMax(this.scale);
                this.photoContainerView.invalidate();
            } else if (ev.getPointerCount() == 1) {
                if (this.velocityTracker != null) {
                    this.velocityTracker.addMovement(ev);
                }
                float dx = Math.abs(ev.getX() - this.moveStartX);
                float dy = Math.abs(ev.getY() - this.dragY);
                if (dx > ((float) AndroidUtilities.dp(3.0f)) || dy > ((float) AndroidUtilities.dp(3.0f))) {
                    this.discardTap = true;
                }
                if (this.canDragDown && !this.draggingDown && this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && dy >= ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) && dy / 2.0f > dx) {
                    this.draggingDown = true;
                    this.moving = false;
                    this.dragY = ev.getY();
                    if (this.isActionBarVisible) {
                        toggleActionBar(false, true);
                    }
                    return true;
                } else if (this.draggingDown) {
                    this.translationY = ev.getY() - this.dragY;
                    this.photoContainerView.invalidate();
                } else if (this.invalidCoords || this.animationStartTime != 0) {
                    this.invalidCoords = false;
                    this.moveStartX = ev.getX();
                    this.moveStartY = ev.getY();
                } else {
                    float moveDx = this.moveStartX - ev.getX();
                    float moveDy = this.moveStartY - ev.getY();
                    if (this.moving || ((this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && Math.abs(moveDy) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(moveDx)) || this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) {
                        if (!this.moving) {
                            moveDx = 0.0f;
                            moveDy = 0.0f;
                            this.moving = true;
                            this.canDragDown = false;
                        }
                        this.moveStartX = ev.getX();
                        this.moveStartY = ev.getY();
                        updateMinMax(this.scale);
                        if ((this.translationX < this.minX && !this.rightImage.hasImage()) || (this.translationX > this.maxX && !this.leftImage.hasImage())) {
                            moveDx /= 3.0f;
                        }
                        if (this.maxY == 0.0f && this.minY == 0.0f) {
                            if (this.translationY - moveDy < this.minY) {
                                this.translationY = this.minY;
                                moveDy = 0.0f;
                            } else if (this.translationY - moveDy > this.maxY) {
                                this.translationY = this.maxY;
                                moveDy = 0.0f;
                            }
                        } else if (this.translationY < this.minY || this.translationY > this.maxY) {
                            moveDy /= 3.0f;
                        }
                        this.translationX -= moveDx;
                        if (this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                            this.translationY -= moveDy;
                        }
                        this.photoContainerView.invalidate();
                    }
                }
            }
        } else if (ev.getActionMasked() == 3 || ev.getActionMasked() == 1 || ev.getActionMasked() == 6) {
            if (this.zooming) {
                this.invalidCoords = true;
                if (this.scale < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    updateMinMax(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    animateTo(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, 0.0f, true);
                } else if (this.scale > 3.0f) {
                    float atx = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                    float aty = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                    updateMinMax(3.0f);
                    if (atx < this.minX) {
                        atx = this.minX;
                    } else if (atx > this.maxX) {
                        atx = this.maxX;
                    }
                    if (aty < this.minY) {
                        aty = this.minY;
                    } else if (aty > this.maxY) {
                        aty = this.maxY;
                    }
                    animateTo(3.0f, atx, aty, true);
                } else {
                    checkMinMax(true);
                }
                this.zooming = false;
            } else if (this.draggingDown) {
                if (Math.abs(this.dragY - ev.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                    closePhoto(true);
                } else {
                    animateTo(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, 0.0f, false);
                }
                this.draggingDown = false;
            } else if (this.moving) {
                float moveToX = this.translationX;
                float moveToY = this.translationY;
                updateMinMax(this.scale);
                this.moving = false;
                this.canDragDown = true;
                float velocity = 0.0f;
                if (this.velocityTracker != null && this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    this.velocityTracker.computeCurrentVelocity(1000);
                    velocity = this.velocityTracker.getXVelocity();
                }
                if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / 3)) || velocity < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImage()) {
                    goToNext();
                    return true;
                } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / 3)) || velocity > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImage()) {
                    goToPrev();
                    return true;
                } else {
                    if (this.translationX < this.minX) {
                        moveToX = this.minX;
                    } else if (this.translationX > this.maxX) {
                        moveToX = this.maxX;
                    }
                    if (this.translationY < this.minY) {
                        moveToY = this.minY;
                    } else if (this.translationY > this.maxY) {
                        moveToY = this.maxY;
                    }
                    animateTo(this.scale, moveToX, moveToY, false);
                }
            }
        }
        return false;
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = this.translationX;
        float moveToY = this.translationY;
        updateMinMax(this.scale);
        if (this.translationX < this.minX) {
            moveToX = this.minX;
        } else if (this.translationX > this.maxX) {
            moveToX = this.maxX;
        }
        if (this.translationY < this.minY) {
            moveToY = this.minY;
        } else if (this.translationY > this.maxY) {
            moveToY = this.maxY;
        }
        animateTo(this.scale, moveToX, moveToY, zoom);
    }

    private void goToNext() {
        float extra = 0.0f;
        if (this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - extra) - ((float) (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float extra = 0.0f;
        if (this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, ((this.maxX + ((float) getContainerViewWidth())) + extra) + ((float) (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / 2)), this.translationY, false);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (this.scale != newScale || this.translationX != newTx || this.translationY != newTy) {
            this.zoomAnimation = isZoom;
            this.animateToScale = newScale;
            this.animateToX = newTx;
            this.animateToY = newTy;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) duration);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animation) {
                    ArticleViewer.this.imageMoveAnimation = null;
                    ArticleViewer.this.photoContainerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.photoContainerView.invalidate();
    }

    public float getAnimationValue() {
        return this.animationValue;
    }

    private void drawContent(Canvas canvas) {
        if (this.photoAnimationInProgress == 1) {
            return;
        }
        if (this.isPhotoVisible || this.photoAnimationInProgress == 2) {
            float currentScale;
            float currentTranslationY;
            float currentTranslationX;
            float scaleDiff;
            float alpha;
            int bitmapWidth;
            int bitmapHeight;
            float scaleX;
            float scaleY;
            float scale;
            int width;
            int height;
            float aty = -1.0f;
            if (this.imageMoveAnimation != null) {
                if (!this.scroller.isFinished()) {
                    this.scroller.abortAnimation();
                }
                float ts = this.scale + ((this.animateToScale - this.scale) * this.animationValue);
                float tx = this.translationX + ((this.animateToX - this.translationX) * this.animationValue);
                float ty = this.translationY + ((this.animateToY - this.translationY) * this.animationValue);
                if (this.animateToScale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.translationX == 0.0f) {
                    aty = ty;
                }
                currentScale = ts;
                currentTranslationY = ty;
                currentTranslationX = tx;
                this.photoContainerView.invalidate();
            } else {
                if (this.animationStartTime != 0) {
                    this.translationX = this.animateToX;
                    this.translationY = this.animateToY;
                    this.scale = this.animateToScale;
                    this.animationStartTime = 0;
                    updateMinMax(this.scale);
                    this.zoomAnimation = false;
                }
                if (!this.scroller.isFinished() && this.scroller.computeScrollOffset()) {
                    if (((float) this.scroller.getStartX()) < this.maxX && ((float) this.scroller.getStartX()) > this.minX) {
                        this.translationX = (float) this.scroller.getCurrX();
                    }
                    if (((float) this.scroller.getStartY()) < this.maxY && ((float) this.scroller.getStartY()) > this.minY) {
                        this.translationY = (float) this.scroller.getCurrY();
                    }
                    this.photoContainerView.invalidate();
                }
                if (this.switchImageAfterAnimation != 0) {
                    if (this.switchImageAfterAnimation == 1) {
                        setImageIndex(this.currentIndex + 1, false);
                    } else if (this.switchImageAfterAnimation == 2) {
                        setImageIndex(this.currentIndex - 1, false);
                    }
                    this.switchImageAfterAnimation = 0;
                }
                currentScale = this.scale;
                currentTranslationY = this.translationY;
                currentTranslationX = this.translationX;
                if (!this.moving) {
                    aty = this.translationY;
                }
            }
            if (this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || aty == -1.0f || this.zoomAnimation) {
                this.photoBackgroundDrawable.setAlpha(255);
            } else {
                float maxValue = ((float) getContainerViewHeight()) / 4.0f;
                this.photoBackgroundDrawable.setAlpha((int) Math.max(127.0f, 255.0f * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (Math.min(Math.abs(aty), maxValue) / maxValue))));
            }
            ImageReceiver sideImage = null;
            if (!(this.scale < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || this.zoomAnimation || this.zooming)) {
                if (currentTranslationX > this.maxX + ((float) AndroidUtilities.dp(5.0f))) {
                    sideImage = this.leftImage;
                } else if (currentTranslationX < this.minX - ((float) AndroidUtilities.dp(5.0f))) {
                    sideImage = this.rightImage;
                }
            }
            this.changingPage = sideImage != null;
            if (sideImage == this.rightImage) {
                float tranlateX = currentTranslationX;
                scaleDiff = 0.0f;
                alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                if (!this.zoomAnimation && tranlateX < this.minX) {
                    alpha = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, (this.minX - tranlateX) / ((float) canvas.getWidth()));
                    scaleDiff = (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - alpha) * 0.3f;
                    tranlateX = (float) ((-canvas.getWidth()) - (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / 2));
                }
                if (sideImage.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                    canvas.translate(((float) (canvas.getWidth() + (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / 2))) + tranlateX, 0.0f);
                    canvas.scale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - scaleDiff, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - scaleDiff);
                    bitmapWidth = sideImage.getBitmapWidth();
                    bitmapHeight = sideImage.getBitmapHeight();
                    scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    if (scaleX > scaleY) {
                        scale = scaleY;
                    } else {
                        scale = scaleX;
                    }
                    width = (int) (((float) bitmapWidth) * scale);
                    height = (int) (((float) bitmapHeight) * scale);
                    sideImage.setAlpha(alpha);
                    sideImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    sideImage.draw(canvas);
                    canvas.restore();
                }
                canvas.save();
                canvas.translate(tranlateX, currentTranslationY / currentScale);
                canvas.translate(((((float) canvas.getWidth()) * (this.scale + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE))) / 2.0f, (-currentTranslationY) / currentScale);
                this.radialProgressViews[1].setScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - scaleDiff);
                this.radialProgressViews[1].setAlpha(alpha);
                this.radialProgressViews[1].onDraw(canvas);
                canvas.restore();
            }
            float translateX = currentTranslationX;
            scaleDiff = 0.0f;
            alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            if (!this.zoomAnimation && translateX > this.maxX) {
                alpha = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, (translateX - this.maxX) / ((float) canvas.getWidth()));
                scaleDiff = alpha * 0.3f;
                alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - alpha;
                translateX = this.maxX;
            }
            boolean drawTextureView = VERSION.SDK_INT >= 16 && this.aspectRatioFrameLayout != null && this.aspectRatioFrameLayout.getVisibility() == 0;
            if (this.centerImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                canvas.translate(translateX, currentTranslationY);
                canvas.scale(currentScale - scaleDiff, currentScale - scaleDiff);
                bitmapWidth = this.centerImage.getBitmapWidth();
                bitmapHeight = this.centerImage.getBitmapHeight();
                if (drawTextureView && this.textureUploaded && Math.abs((((float) bitmapWidth) / ((float) bitmapHeight)) - (((float) this.videoTextureView.getMeasuredWidth()) / ((float) this.videoTextureView.getMeasuredHeight()))) > 0.01f) {
                    bitmapWidth = this.videoTextureView.getMeasuredWidth();
                    bitmapHeight = this.videoTextureView.getMeasuredHeight();
                }
                scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                if (scaleX > scaleY) {
                    scale = scaleY;
                } else {
                    scale = scaleX;
                }
                width = (int) (((float) bitmapWidth) * scale);
                height = (int) (((float) bitmapHeight) * scale);
                if (!(drawTextureView && this.textureUploaded && this.videoCrossfadeStarted && this.videoCrossfadeAlpha == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) {
                    this.centerImage.setAlpha(alpha);
                    this.centerImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    this.centerImage.draw(canvas);
                }
                if (drawTextureView) {
                    if (!this.videoCrossfadeStarted && this.textureUploaded) {
                        this.videoCrossfadeStarted = true;
                        this.videoCrossfadeAlpha = 0.0f;
                        this.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                    }
                    canvas.translate((float) ((-width) / 2), (float) ((-height) / 2));
                    this.videoTextureView.setAlpha(this.videoCrossfadeAlpha * alpha);
                    this.aspectRatioFrameLayout.draw(canvas);
                    if (this.videoCrossfadeStarted && this.videoCrossfadeAlpha < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        long newUpdateTime = System.currentTimeMillis();
                        long dt = newUpdateTime - this.videoCrossfadeAlphaLastTime;
                        this.videoCrossfadeAlphaLastTime = newUpdateTime;
                        this.videoCrossfadeAlpha += ((float) dt) / BitmapDescriptorFactory.HUE_MAGENTA;
                        this.photoContainerView.invalidate();
                        if (this.videoCrossfadeAlpha > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                            this.videoCrossfadeAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                        }
                    }
                }
                canvas.restore();
            }
            if (!drawTextureView && (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0)) {
                canvas.save();
                canvas.translate(translateX, currentTranslationY / currentScale);
                this.radialProgressViews[0].setScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - scaleDiff);
                this.radialProgressViews[0].setAlpha(alpha);
                this.radialProgressViews[0].onDraw(canvas);
                canvas.restore();
            }
            if (sideImage == this.leftImage) {
                if (sideImage.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                    canvas.translate(((-((((float) canvas.getWidth()) * (this.scale + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)))) / 2.0f) + currentTranslationX, 0.0f);
                    bitmapWidth = sideImage.getBitmapWidth();
                    bitmapHeight = sideImage.getBitmapHeight();
                    scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    scale = scaleX > scaleY ? scaleY : scaleX;
                    width = (int) (((float) bitmapWidth) * scale);
                    height = (int) (((float) bitmapHeight) * scale);
                    sideImage.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    sideImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    sideImage.draw(canvas);
                    canvas.restore();
                }
                canvas.save();
                canvas.translate(currentTranslationX, currentTranslationY / currentScale);
                canvas.translate((-((((float) canvas.getWidth()) * (this.scale + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)))) / 2.0f, (-currentTranslationY) / currentScale);
                this.radialProgressViews[2].setScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.radialProgressViews[2].setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.radialProgressViews[2].onDraw(canvas);
                canvas.restore();
            }
        }
    }

    private void onActionClick(boolean download) {
        TLObject media = getMedia(this.currentIndex);
        if ((media instanceof Document) && this.currentFileNames[0] != null) {
            Document document = (Document) media;
            File file = null;
            if (this.currentMedia != null) {
                file = getMediaFile(this.currentIndex);
                if (!(file == null || file.exists())) {
                    file = null;
                }
            }
            if (file != null) {
                preparePlayer(file, true);
            } else if (!download) {
            } else {
                if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[0])) {
                    FileLoader.getInstance().cancelLoadFile(document);
                } else {
                    FileLoader.getInstance().loadFile(document, true, false);
                }
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(velocityX), Math.round(velocityY), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.photoContainerView.postInvalidate();
        }
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        boolean z = false;
        if (this.discardTap) {
            return false;
        }
        boolean drawTextureView;
        if (VERSION.SDK_INT < 16 || this.aspectRatioFrameLayout == null || this.aspectRatioFrameLayout.getVisibility() != 0) {
            drawTextureView = false;
        } else {
            drawTextureView = true;
        }
        if (!(this.radialProgressViews[0] == null || this.photoContainerView == null || drawTextureView)) {
            int state = this.radialProgressViews[0].backgroundState;
            if (state > 0 && state <= 3) {
                float x = e.getX();
                float y = e.getY();
                if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, true);
                    return true;
                }
            }
        }
        if (!this.isActionBarVisible) {
            z = true;
        }
        toggleActionBar(z, true);
        return true;
    }

    public boolean onDoubleTap(MotionEvent e) {
        if (!this.canZoom || (this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && (this.translationY != 0.0f || this.translationX != 0.0f))) {
            return false;
        }
        if (this.animationStartTime != 0 || this.photoAnimationInProgress != 0) {
            return false;
        }
        if (this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            float atx = (e.getX() - ((float) (getContainerViewWidth() / 2))) - (((e.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
            float aty = (e.getY() - ((float) (getContainerViewHeight() / 2))) - (((e.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
            updateMinMax(3.0f);
            if (atx < this.minX) {
                atx = this.minX;
            } else if (atx > this.maxX) {
                atx = this.maxX;
            }
            if (aty < this.minY) {
                aty = this.minY;
            } else if (aty > this.maxY) {
                aty = this.maxY;
            }
            animateTo(3.0f, atx, aty, true);
        } else {
            animateTo(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, 0.0f, true);
        }
        this.doubleTap = true;
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private ImageReceiver getImageReceiverFromListView(RecyclerListView listView, PageBlock pageBlock, int[] coords) {
        int count = listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = listView.getChildAt(a);
            if (view instanceof BlockPhotoCell) {
                BlockPhotoCell cell = (BlockPhotoCell) view;
                if (cell.currentBlock == pageBlock) {
                    view.getLocationInWindow(coords);
                    return cell.imageView;
                }
            } else if (view instanceof BlockVideoCell) {
                BlockVideoCell cell2 = (BlockVideoCell) view;
                if (cell2.currentBlock == pageBlock) {
                    view.getLocationInWindow(coords);
                    return cell2.imageView;
                }
            } else if (view instanceof BlockCollageCell) {
                imageReceiver = getImageReceiverFromListView(((BlockCollageCell) view).innerListView, pageBlock, coords);
                if (imageReceiver != null) {
                    return imageReceiver;
                }
            } else if (view instanceof BlockSlideshowCell) {
                imageReceiver = getImageReceiverFromListView(((BlockSlideshowCell) view).innerListView, pageBlock, coords);
                if (imageReceiver != null) {
                    return imageReceiver;
                }
            } else {
                continue;
            }
        }
        return null;
    }

    private PlaceProviderObject getPlaceForPhoto(PageBlock pageBlock) {
        int i = 0;
        ImageReceiver imageReceiver = getImageReceiverFromListView(this.listView, pageBlock, this.coords);
        if (imageReceiver == null) {
            return null;
        }
        PlaceProviderObject object = new PlaceProviderObject();
        object.viewX = this.coords[0];
        int i2 = this.coords[1];
        if (VERSION.SDK_INT < 21) {
            i = AndroidUtilities.statusBarHeight;
        }
        object.viewY = i2 - i;
        object.parentView = this.listView;
        object.imageReceiver = imageReceiver;
        object.thumb = imageReceiver.getBitmap();
        object.radius = imageReceiver.getRoundRadius();
        object.clipTopAddition = AndroidUtilities.statusBarHeight;
        return object;
    }

    private boolean scaleToFill() {
        return false;
    }
}
