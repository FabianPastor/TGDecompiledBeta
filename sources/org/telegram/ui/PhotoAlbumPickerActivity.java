package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate;

public class PhotoAlbumPickerActivity extends BaseFragment implements NotificationCenterDelegate {
    private ArrayList<AlbumEntry> albumsSorted = null;
    private boolean allowCaption;
    private boolean allowGifs;
    private boolean allowOrder = true;
    private boolean allowSearchImages = true;
    private CharSequence caption;
    private ChatActivity chatActivity;
    private int columnsCount = 2;
    private EditTextEmoji commentTextView;
    private PhotoAlbumPickerActivityDelegate delegate;
    private TextView emptyView;
    private FrameLayout frameLayout2;
    private ActionBarMenuSubItem[] itemCells;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading = false;
    private int maxSelectedPhotos;
    private Paint paint = new Paint(1);
    private FrameLayout progressView;
    private RectF rect = new RectF();
    private int selectPhotoType;
    private View selectedCountView;
    private HashMap<Object, Object> selectedPhotos = new HashMap();
    private ArrayList<Object> selectedPhotosOrder = new ArrayList();
    private ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    private View shadow;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private TextPaint textPaint = new TextPaint(1);
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface PhotoAlbumPickerActivityDelegate {
        void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList, boolean z, int i);

        void startPhotoSelectActivity();
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PhotoAlbumPickerActivity.this.albumsSorted != null ? (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount))) : 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            PhotoPickerAlbumsCell photoPickerAlbumsCell = new PhotoPickerAlbumsCell(this.mContext);
            photoPickerAlbumsCell.setDelegate(new -$$Lambda$PhotoAlbumPickerActivity$ListAdapter$wak8B6ZyJqrggtYN7y4fwAyLKCg(this));
            return new Holder(photoPickerAlbumsCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(AlbumEntry albumEntry) {
            PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            PhotoPickerAlbumsCell photoPickerAlbumsCell = (PhotoPickerAlbumsCell) viewHolder.itemView;
            photoPickerAlbumsCell.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
            for (int i2 = 0; i2 < PhotoAlbumPickerActivity.this.columnsCount; i2++) {
                int access$1500 = (PhotoAlbumPickerActivity.this.columnsCount * i) + i2;
                if (access$1500 < PhotoAlbumPickerActivity.this.albumsSorted.size()) {
                    photoPickerAlbumsCell.setAlbum(i2, (AlbumEntry) PhotoAlbumPickerActivity.this.albumsSorted.get(access$1500));
                } else {
                    photoPickerAlbumsCell.setAlbum(i2, null);
                }
            }
            photoPickerAlbumsCell.requestLayout();
        }
    }

    public PhotoAlbumPickerActivity(int i, boolean z, boolean z2, ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.selectPhotoType = i;
        this.allowGifs = z;
        this.allowCaption = z2;
    }

    public boolean onFragmentCreate() {
        if (this.selectPhotoType == 0 && this.allowSearchImages) {
            this.albumsSorted = MediaController.allMediaAlbums;
        } else {
            this.albumsSorted = MediaController.allPhotoAlbums;
        }
        this.loading = this.albumsSorted == null;
        MediaController.loadGalleryPhotosAlbums(this.classGuid);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        Context context2 = context;
        String str = "dialogBackground";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        String str2 = "dialogTextBlack";
        this.actionBar.setTitleColor(Theme.getColor(str2));
        this.actionBar.setItemsColor(Theme.getColor(str2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoAlbumPickerActivity.this.finishFragment();
                } else if (i == 1) {
                    if (PhotoAlbumPickerActivity.this.delegate != null) {
                        PhotoAlbumPickerActivity.this.finishFragment(false);
                        PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
                    }
                } else if (i == 2) {
                    PhotoAlbumPickerActivity.this.openPhotoPicker(null, 0);
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.allowSearchImages) {
            createMenu.addItem(2, NUM).setContentDescription(LocaleController.getString("Search", NUM));
        }
        createMenu.addItem(1, NUM).setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastNotifyWidth;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int i3 = 0;
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    PhotoAlbumPickerActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                } else if (!AndroidUtilities.isInMultiwindow) {
                    size2 -= PhotoAlbumPickerActivity.this.commentTextView.getEmojiPadding();
                    i2 = MeasureSpec.makeMeasureSpec(size2, NUM);
                }
                int childCount = getChildCount();
                while (i3 < childCount) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        if (PhotoAlbumPickerActivity.this.commentTextView == null || !PhotoAlbumPickerActivity.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:48:0x00e9  */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x00e0  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x009f  */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x00e0  */
            /* JADX WARNING: Removed duplicated region for block: B:48:0x00e9  */
            public void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
                /*
                r8 = this;
                r9 = r8.lastNotifyWidth;
                r12 = r12 - r10;
                if (r9 == r12) goto L_0x0024;
            L_0x0005:
                r8.lastNotifyWidth = r12;
                r9 = org.telegram.ui.PhotoAlbumPickerActivity.this;
                r9 = r9.sendPopupWindow;
                if (r9 == 0) goto L_0x0024;
            L_0x000f:
                r9 = org.telegram.ui.PhotoAlbumPickerActivity.this;
                r9 = r9.sendPopupWindow;
                r9 = r9.isShowing();
                if (r9 == 0) goto L_0x0024;
            L_0x001b:
                r9 = org.telegram.ui.PhotoAlbumPickerActivity.this;
                r9 = r9.sendPopupWindow;
                r9.dismiss();
            L_0x0024:
                r9 = r8.getChildCount();
                r10 = r8.getKeyboardHeight();
                r0 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
                r1 = 0;
                if (r10 > r0) goto L_0x004a;
            L_0x0035:
                r10 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                if (r10 != 0) goto L_0x004a;
            L_0x0039:
                r10 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r10 != 0) goto L_0x004a;
            L_0x003f:
                r10 = org.telegram.ui.PhotoAlbumPickerActivity.this;
                r10 = r10.commentTextView;
                r10 = r10.getEmojiPadding();
                goto L_0x004b;
            L_0x004a:
                r10 = 0;
            L_0x004b:
                r8.setBottomClip(r10);
            L_0x004e:
                if (r1 >= r9) goto L_0x0100;
            L_0x0050:
                r0 = r8.getChildAt(r1);
                r2 = r0.getVisibility();
                r3 = 8;
                if (r2 != r3) goto L_0x005e;
            L_0x005c:
                goto L_0x00fc;
            L_0x005e:
                r2 = r0.getLayoutParams();
                r2 = (android.widget.FrameLayout.LayoutParams) r2;
                r3 = r0.getMeasuredWidth();
                r4 = r0.getMeasuredHeight();
                r5 = r2.gravity;
                r6 = -1;
                if (r5 != r6) goto L_0x0073;
            L_0x0071:
                r5 = 51;
            L_0x0073:
                r6 = r5 & 7;
                r5 = r5 & 112;
                r6 = r6 & 7;
                r7 = 1;
                if (r6 == r7) goto L_0x0091;
            L_0x007c:
                r7 = 5;
                if (r6 == r7) goto L_0x0087;
            L_0x007f:
                r6 = r2.leftMargin;
                r7 = r8.getPaddingLeft();
                r6 = r6 + r7;
                goto L_0x009b;
            L_0x0087:
                r6 = r12 - r3;
                r7 = r2.rightMargin;
                r6 = r6 - r7;
                r7 = r8.getPaddingRight();
                goto L_0x009a;
            L_0x0091:
                r6 = r12 - r3;
                r6 = r6 / 2;
                r7 = r2.leftMargin;
                r6 = r6 + r7;
                r7 = r2.rightMargin;
            L_0x009a:
                r6 = r6 - r7;
            L_0x009b:
                r7 = 16;
                if (r5 == r7) goto L_0x00b9;
            L_0x009f:
                r7 = 48;
                if (r5 == r7) goto L_0x00b1;
            L_0x00a3:
                r7 = 80;
                if (r5 == r7) goto L_0x00aa;
            L_0x00a7:
                r2 = r2.topMargin;
                goto L_0x00c6;
            L_0x00aa:
                r5 = r13 - r10;
                r5 = r5 - r11;
                r5 = r5 - r4;
                r2 = r2.bottomMargin;
                goto L_0x00c4;
            L_0x00b1:
                r2 = r2.topMargin;
                r5 = r8.getPaddingTop();
                r2 = r2 + r5;
                goto L_0x00c6;
            L_0x00b9:
                r5 = r13 - r10;
                r5 = r5 - r11;
                r5 = r5 - r4;
                r5 = r5 / 2;
                r7 = r2.topMargin;
                r5 = r5 + r7;
                r2 = r2.bottomMargin;
            L_0x00c4:
                r2 = r5 - r2;
            L_0x00c6:
                r5 = org.telegram.ui.PhotoAlbumPickerActivity.this;
                r5 = r5.commentTextView;
                if (r5 == 0) goto L_0x00f7;
            L_0x00ce:
                r5 = org.telegram.ui.PhotoAlbumPickerActivity.this;
                r5 = r5.commentTextView;
                r5 = r5.isPopupView(r0);
                if (r5 == 0) goto L_0x00f7;
            L_0x00da:
                r2 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r2 == 0) goto L_0x00e9;
            L_0x00e0:
                r2 = r8.getMeasuredHeight();
                r5 = r0.getMeasuredHeight();
                goto L_0x00f6;
            L_0x00e9:
                r2 = r8.getMeasuredHeight();
                r5 = r8.getKeyboardHeight();
                r2 = r2 + r5;
                r5 = r0.getMeasuredHeight();
            L_0x00f6:
                r2 = r2 - r5;
            L_0x00f7:
                r3 = r3 + r6;
                r4 = r4 + r2;
                r0.layout(r6, r2, r3, r4);
            L_0x00fc:
                r1 = r1 + 1;
                goto L_0x004e;
            L_0x0100:
                r8.notifyHeightChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoAlbumPickerActivity$AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor(str));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.actionBar.setTitle(LocaleController.getString("Gallery", NUM));
        this.listView = new RecyclerListView(context2);
        this.listView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(54.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setDrawingCacheEnabled(false);
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.getColor(str));
        this.emptyView = new TextView(context2);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString("NoPhotos", NUM));
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.emptyView.setOnTouchListener(-$$Lambda$PhotoAlbumPickerActivity$2ZdkXHoPXptp2wpUszGZ5G4bMiQ.INSTANCE);
        this.progressView = new FrameLayout(context2);
        this.progressView.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context2);
        radialProgressView.setProgressColor(-11371101);
        this.progressView.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        this.shadow = new View(context2);
        this.shadow.setBackgroundResource(NUM);
        this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.frameLayout2 = new FrameLayout(context2);
        this.frameLayout2.setBackgroundColor(Theme.getColor(str));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener(-$$Lambda$PhotoAlbumPickerActivity$qmrmGuRZuSx5NpHh8Dd6TzQejJo.INSTANCE);
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, null, 1);
        this.commentTextView.setFilters(new InputFilter[]{new LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
        EditTextBoldCursor editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        CharSequence charSequence = this.caption;
        if (charSequence != null) {
            this.commentTextView.setText(charSequence);
        }
        this.writeButtonContainer = new FrameLayout(context2);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        this.writeButtonContainer.setOnClickListener(new -$$Lambda$PhotoAlbumPickerActivity$uIfL5Po9QpxaySujKBo2EoxkIy0(this));
        this.writeButtonContainer.setOnLongClickListener(new -$$Lambda$PhotoAlbumPickerActivity$nPilQJMHimgRpBc1NACp9kKM8CY(this));
        this.writeButton = new ImageView(context2);
        this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor("dialogFloatingButtonPressed"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, this.writeButtonDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButtonDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), Mode.MULTIPLY));
        this.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedCountView = new View(context2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, PhotoAlbumPickerActivity.this.selectedPhotosOrder.size()))});
                int ceil = (int) Math.ceil((double) PhotoAlbumPickerActivity.this.textPaint.measureText(format));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                PhotoAlbumPickerActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                max /= 2;
                int i = measuredWidth - max;
                max += measuredWidth;
                PhotoAlbumPickerActivity.this.rect.set((float) i, 0.0f, (float) max, (float) getMeasuredHeight());
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), PhotoAlbumPickerActivity.this.paint);
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                PhotoAlbumPickerActivity.this.rect.set((float) (i + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (max - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), PhotoAlbumPickerActivity.this.paint);
                canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), PhotoAlbumPickerActivity.this.textPaint);
            }
        };
        this.selectedCountView.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
        if (this.selectPhotoType != 0) {
            this.commentTextView.setVisibility(8);
        }
        if (this.loading) {
            ArrayList arrayList = this.albumsSorted;
            if (arrayList == null || (arrayList != null && arrayList.isEmpty())) {
                this.progressView.setVisibility(0);
                this.listView.setEmptyView(null);
                return this.fragmentView;
            }
        }
        this.progressView.setVisibility(8);
        this.listView.setEmptyView(this.emptyView);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$PhotoAlbumPickerActivity(View view) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null || !chatActivity.isInScheduleMode()) {
            sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0);
            finishFragment();
            return;
        }
        AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.chatActivity.getCurrentUser()), new -$$Lambda$PhotoAlbumPickerActivity$v6LAFLQscQmd9i_mv6vE_Uci0Aw(this));
    }

    public /* synthetic */ void lambda$null$2$PhotoAlbumPickerActivity(boolean z, int i) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, z, i);
        finishFragment();
    }

    public /* synthetic */ boolean lambda$createView$7$PhotoAlbumPickerActivity(View view) {
        View view2 = view;
        ChatActivity chatActivity = this.chatActivity;
        if (!(chatActivity == null || this.maxSelectedPhotos == 1)) {
            chatActivity.getCurrentChat();
            User currentUser = this.chatActivity.getCurrentUser();
            if (this.chatActivity.getCurrentEncryptedChat() != null) {
                return false;
            }
            if (this.sendPopupLayout == null) {
                this.sendPopupLayout = new ActionBarPopupWindowLayout(getParentActivity());
                this.sendPopupLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == 0 && PhotoAlbumPickerActivity.this.sendPopupWindow != null && PhotoAlbumPickerActivity.this.sendPopupWindow.isShowing()) {
                            view.getHitRect(this.popupRect);
                            if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                PhotoAlbumPickerActivity.this.sendPopupWindow.dismiss();
                            }
                        }
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new -$$Lambda$PhotoAlbumPickerActivity$EGBQpBfkkE8H8gJpFNTVwnrGfSk(this));
                this.sendPopupLayout.setShowedFromBotton(false);
                this.itemCells = new ActionBarMenuSubItem[2];
                for (int i = 0; i < 2; i++) {
                    if (i != 1 || !UserObject.isUserSelf(currentUser)) {
                        this.itemCells[i] = new ActionBarMenuSubItem(getParentActivity());
                        if (i == 0) {
                            if (UserObject.isUserSelf(currentUser)) {
                                this.itemCells[i].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                            } else {
                                this.itemCells[i].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                            }
                        } else if (i == 1) {
                            this.itemCells[i].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                        }
                        this.itemCells[i].setMinimumWidth(AndroidUtilities.dp(196.0f));
                        this.sendPopupLayout.addView(this.itemCells[i], LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
                        this.itemCells[i].setOnClickListener(new -$$Lambda$PhotoAlbumPickerActivity$bBDElbZAx4F1XAqb6cVVPUN0cDg(this, i));
                    }
                }
                this.sendPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
                this.sendPopupWindow.setAnimationEnabled(false);
                this.sendPopupWindow.setAnimationStyle(NUM);
                this.sendPopupWindow.setOutsideTouchable(true);
                this.sendPopupWindow.setClippingEnabled(true);
                this.sendPopupWindow.setInputMethodMode(2);
                this.sendPopupWindow.setSoftInputMode(0);
                this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            }
            this.sendPopupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.sendPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            view2.getLocationInWindow(iArr);
            this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
            this.sendPopupWindow.dimBehind();
            view2.performHapticFeedback(3, 2);
        }
        return false;
    }

    public /* synthetic */ void lambda$null$4$PhotoAlbumPickerActivity(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$null$6$PhotoAlbumPickerActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.chatActivity.getCurrentUser()), new -$$Lambda$PhotoAlbumPickerActivity$Tuja0Jo4hX6P6AoLDSiFJS1HWts(this));
        } else if (i == 1) {
            sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$5$PhotoAlbumPickerActivity(boolean z, int i) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, z, i);
        finishFragment();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        fixLayout();
    }

    public void onPause() {
        super.onPause();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoad) {
            if (this.classGuid == ((Integer) objArr[0]).intValue()) {
                if (this.selectPhotoType == 0 && this.allowSearchImages) {
                    this.albumsSorted = (ArrayList) objArr[1];
                } else {
                    this.albumsSorted = (ArrayList) objArr[2];
                }
                FrameLayout frameLayout = this.progressView;
                if (frameLayout != null) {
                    frameLayout.setVisibility(8);
                }
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null && recyclerListView.getEmptyView() == null) {
                    this.listView.setEmptyView(this.emptyView);
                }
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                this.loading = false;
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
    }

    public void setAllowSearchImages(boolean z) {
        this.allowSearchImages = z;
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate photoAlbumPickerActivityDelegate) {
        this.delegate = photoAlbumPickerActivityDelegate;
    }

    private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, boolean z, int i) {
        if (!hashMap.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                Object obj = hashMap.get(arrayList.get(i2));
                SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                arrayList2.add(sendingMediaInfo);
                ArrayList arrayList3 = null;
                String str;
                CharSequence charSequence;
                if (obj instanceof PhotoEntry) {
                    PhotoEntry photoEntry = (PhotoEntry) obj;
                    if (photoEntry.isVideo) {
                        sendingMediaInfo.path = photoEntry.path;
                        sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                    } else {
                        str = photoEntry.imagePath;
                        if (str != null) {
                            sendingMediaInfo.path = str;
                        } else {
                            str = photoEntry.path;
                            if (str != null) {
                                sendingMediaInfo.path = str;
                            }
                        }
                    }
                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                    charSequence = photoEntry.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = photoEntry.entities;
                    if (!photoEntry.stickers.isEmpty()) {
                        arrayList3 = new ArrayList(photoEntry.stickers);
                    }
                    sendingMediaInfo.masks = arrayList3;
                    sendingMediaInfo.ttl = photoEntry.ttl;
                } else if (obj instanceof SearchImage) {
                    SearchImage searchImage = (SearchImage) obj;
                    str = searchImage.imagePath;
                    if (str != null) {
                        sendingMediaInfo.path = str;
                    } else {
                        sendingMediaInfo.searchImage = searchImage;
                    }
                    charSequence = searchImage.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = searchImage.entities;
                    if (!searchImage.stickers.isEmpty()) {
                        arrayList3 = new ArrayList(searchImage.stickers);
                    }
                    sendingMediaInfo.masks = arrayList3;
                    sendingMediaInfo.ttl = searchImage.ttl;
                    BotInlineResult botInlineResult = searchImage.inlineResult;
                    if (botInlineResult != null && searchImage.type == 1) {
                        sendingMediaInfo.inlineResult = botInlineResult;
                        sendingMediaInfo.params = searchImage.params;
                    }
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                }
            }
            this.delegate.didSelectPhotos(arrayList2, z, i);
        }
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PhotoAlbumPickerActivity.this.fixLayoutInternal();
                    if (PhotoAlbumPickerActivity.this.listView != null) {
                        PhotoAlbumPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void applyCaption() {
        if (this.commentTextView.length() > 0) {
            Object obj = this.selectedPhotos.get(Integer.valueOf(((Integer) this.selectedPhotosOrder.get(0)).intValue()));
            if (obj instanceof PhotoEntry) {
                ((PhotoEntry) obj).caption = this.commentTextView.getText().toString();
            } else if (obj instanceof SearchImage) {
                ((SearchImage) obj).caption = this.commentTextView.getText().toString();
            }
        }
    }

    private void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.columnsCount = 2;
            if (!AndroidUtilities.isTablet() && (rotation == 3 || rotation == 1)) {
                this.columnsCount = 4;
            }
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private boolean showCommentTextView(boolean z) {
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        this.frameLayout2.setTag(z ? Integer.valueOf(1) : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        } else {
            this.frameLayout2.setVisibility(4);
            this.writeButtonContainer.setVisibility(4);
        }
        float f = 0.2f;
        float f2 = 1.0f;
        this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
        this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
        float f3 = 0.0f;
        this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
        this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
        View view = this.selectedCountView;
        if (z) {
            f = 1.0f;
        }
        view.setScaleY(f);
        view = this.selectedCountView;
        if (!z) {
            f2 = 0.0f;
        }
        view.setAlpha(f2);
        this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
        view = this.shadow;
        if (!z) {
            f3 = (float) AndroidUtilities.dp(48.0f);
        }
        view.setTranslationY(f3);
        return true;
    }

    private void updatePhotosButton() {
        if (this.selectedPhotos.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        showCommentTextView(true);
    }

    private void openPhotoPicker(AlbumEntry albumEntry, int i) {
        PhotoPickerActivity photoPickerActivity;
        Editable text;
        if (albumEntry != null) {
            photoPickerActivity = new PhotoPickerActivity(i, albumEntry, this.selectedPhotos, this.selectedPhotosOrder, null, this.selectPhotoType, this.allowCaption, this.chatActivity);
            text = this.commentTextView.getText();
            this.caption = text;
            photoPickerActivity.setCaption(text);
            photoPickerActivity.setDelegate(new PhotoPickerActivityDelegate() {
                public void selectedPhotosChanged() {
                    PhotoAlbumPickerActivity.this.updatePhotosButton();
                }

                public void actionButtonPressed(boolean z, boolean z2, int i) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!z) {
                        PhotoAlbumPickerActivity photoAlbumPickerActivity = PhotoAlbumPickerActivity.this;
                        photoAlbumPickerActivity.sendSelectedPhotos(photoAlbumPickerActivity.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder, z2, i);
                    }
                }

                public void onCaptionChanged(CharSequence charSequence) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
                }
            });
            photoPickerActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(photoPickerActivity);
            return;
        }
        final HashMap hashMap = new HashMap();
        final ArrayList arrayList = new ArrayList();
        if (this.allowGifs) {
            PhotoPickerSearchActivity photoPickerSearchActivity = new PhotoPickerSearchActivity(hashMap, arrayList, null, this.selectPhotoType, this.allowCaption, this.chatActivity);
            Editable text2 = this.commentTextView.getText();
            this.caption = text2;
            photoPickerSearchActivity.setCaption(text2);
            photoPickerSearchActivity.setDelegate(new PhotoPickerActivityDelegate() {
                public void selectedPhotosChanged() {
                }

                public void actionButtonPressed(boolean z, boolean z2, int i) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!z) {
                        PhotoAlbumPickerActivity.this.sendSelectedPhotos(hashMap, arrayList, z2, i);
                    }
                }

                public void onCaptionChanged(CharSequence charSequence) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
                }
            });
            photoPickerSearchActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(photoPickerSearchActivity);
            return;
        }
        photoPickerActivity = new PhotoPickerActivity(0, albumEntry, hashMap, arrayList, null, this.selectPhotoType, this.allowCaption, this.chatActivity);
        text = this.commentTextView.getText();
        this.caption = text;
        photoPickerActivity.setCaption(text);
        photoPickerActivity.setDelegate(new PhotoPickerActivityDelegate() {
            public void selectedPhotosChanged() {
            }

            public void actionButtonPressed(boolean z, boolean z2, int i) {
                PhotoAlbumPickerActivity.this.removeSelfFromStack();
                if (!z) {
                    PhotoAlbumPickerActivity.this.sendSelectedPhotos(hashMap, arrayList, z2, i);
                }
            }

            public void onCaptionChanged(CharSequence charSequence) {
                PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
            }
        });
        photoPickerActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        presentFragment(photoPickerActivity);
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[8];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "dialogButtonSelector");
        themeDescriptionArr[5] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "dialogBackground");
        themeDescriptionArr[6] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{Theme.chat_attachEmptyDrawable}, null, "chat_attachEmptyImage");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, null, null, "chat_attachPhotoBackground");
        return themeDescriptionArr;
    }
}
