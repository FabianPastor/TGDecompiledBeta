package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertAudioLayout;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachAlertAudioLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ArrayList<MediaController.AudioEntry> audioEntries = new ArrayList<>();
    private View currentEmptyView;
    private float currentPanTranslationProgress;
    private AudioSelectDelegate delegate;
    private ImageView emptyImageView;
    /* access modifiers changed from: private */
    public TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private FrameLayout frameLayout;
    private boolean ignoreLayout;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private boolean loadingAudio;
    private int maxSelectedFiles = -1;
    /* access modifiers changed from: private */
    public MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    private SearchField searchField;
    /* access modifiers changed from: private */
    public LongSparseArray<MediaController.AudioEntry> selectedAudios = new LongSparseArray<>();
    private ArrayList<MediaController.AudioEntry> selectedAudiosOrder = new ArrayList<>();
    private boolean sendPressed;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;

    public interface AudioSelectDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList, CharSequence charSequence, boolean z, int i);
    }

    static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatAttachAlertAudioLayout(ChatAttachAlert chatAttachAlert, Context context) {
        super(chatAttachAlert, context);
        Context context2 = context;
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        loadAudio();
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.frameLayout = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor("dialogBackground"));
        AnonymousClass1 r0 = new SearchField(context2) {
            public void onTextChange(String str) {
                if (str.length() == 0 && ChatAttachAlertAudioLayout.this.listView.getAdapter() != ChatAttachAlertAudioLayout.this.listAdapter) {
                    ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.listAdapter);
                    ChatAttachAlertAudioLayout.this.listAdapter.notifyDataSetChanged();
                }
                if (ChatAttachAlertAudioLayout.this.searchAdapter != null) {
                    ChatAttachAlertAudioLayout.this.searchAdapter.search(str);
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                ChatAttachAlertAudioLayout.this.parentAlert.makeFocusable(getSearchEditText(), true);
                return super.onInterceptTouchEvent(motionEvent);
            }

            public void processTouchEvent(MotionEvent motionEvent) {
                MotionEvent obtain = MotionEvent.obtain(motionEvent);
                obtain.setLocation(obtain.getRawX(), (obtain.getRawY() - ChatAttachAlertAudioLayout.this.parentAlert.getSheetContainer().getTranslationY()) - ((float) AndroidUtilities.dp(58.0f)));
                ChatAttachAlertAudioLayout.this.listView.dispatchTouchEvent(obtain);
                obtain.recycle();
            }

            /* access modifiers changed from: protected */
            public void onFieldTouchUp(EditTextBoldCursor editTextBoldCursor) {
                ChatAttachAlertAudioLayout.this.parentAlert.makeFocusable(editTextBoldCursor, true);
            }
        };
        this.searchField = r0;
        r0.setHint(LocaleController.getString("SearchMusic", NUM));
        this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.progressView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener($$Lambda$ChatAttachAlertAudioLayout$UXushIeS54bEwzKZj1g46mjKy0.INSTANCE);
        ImageView imageView = new ImageView(context2);
        this.emptyImageView = imageView;
        imageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        this.emptyTitleTextView = textView;
        textView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context2);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        AnonymousClass2 r02 = new RecyclerListView(context2) {
            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) ((ChatAttachAlertAudioLayout.this.parentAlert.scrollOffsetY[0] + AndroidUtilities.dp(30.0f)) + ((Build.VERSION.SDK_INT < 21 || ChatAttachAlertAudioLayout.this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)));
            }
        };
        this.listView = r02;
        r02.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass3 r03 = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(9.0f), this.listView) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int i) {
                        return super.calculateDyToMakeVisible(view, i) - (ChatAttachAlertAudioLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int i) {
                        return super.calculateTimeForDeceleration(i) * 2;
                    }
                };
                r2.setTargetPosition(i);
                startSmoothScroll(r2);
            }
        };
        this.layoutManager = r03;
        recyclerListView.setLayoutManager(r03);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChatAttachAlertAudioLayout.this.lambda$new$1$ChatAttachAlertAudioLayout(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ChatAttachAlertAudioLayout.this.lambda$new$2$ChatAttachAlertAudioLayout(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlertAudioLayout chatAttachAlertAudioLayout = ChatAttachAlertAudioLayout.this;
                chatAttachAlertAudioLayout.parentAlert.updateLayout(chatAttachAlertAudioLayout, true, i2);
                ChatAttachAlertAudioLayout.this.updateEmptyViewPosition();
            }
        });
        this.searchAdapter = new SearchAdapter(context2);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context2);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        addView(this.shadow, layoutParams);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        updateEmptyView();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ChatAttachAlertAudioLayout(View view, int i) {
        onItemClick(view);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ boolean lambda$new$2$ChatAttachAlertAudioLayout(View view, int i) {
        onItemClick(view);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        onHide();
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    }

    /* access modifiers changed from: package-private */
    public void onHide() {
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        this.playingAudio = null;
    }

    /* access modifiers changed from: private */
    public void updateEmptyViewPosition() {
        View childAt;
        if (this.currentEmptyView.getVisibility() == 0 && (childAt = this.listView.getChildAt(0)) != null) {
            View view = this.currentEmptyView;
            view.setTranslationY(((float) (((view.getMeasuredHeight() - getMeasuredHeight()) + childAt.getTop()) / 2)) - (this.currentPanTranslationProgress / 2.0f));
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        boolean z;
        int i = 8;
        if (this.loadingAudio) {
            this.currentEmptyView = this.progressView;
            this.emptyView.setVisibility(8);
        } else {
            if (this.listView.getAdapter() == this.searchAdapter) {
                this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFound", NUM));
            } else {
                this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFiles", NUM));
                this.emptySubtitleTextView.setText(LocaleController.getString("NoAudioFilesInfo", NUM));
            }
            this.currentEmptyView = this.emptyView;
            this.progressView.setVisibility(8);
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter2 = this.searchAdapter;
        if (adapter == searchAdapter2) {
            z = searchAdapter2.searchResult.isEmpty();
        } else {
            z = this.audioEntries.isEmpty();
        }
        View view = this.currentEmptyView;
        if (z) {
            i = 0;
        }
        view.setVisibility(i);
        updateEmptyViewPosition();
    }

    public void setMaxSelectedFiles(int i) {
        this.maxSelectedFiles = i;
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
        int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
            top = i;
        } else {
            runShadowAnimation(false);
        }
        this.frameLayout.setTranslationY((float) top);
        return top + AndroidUtilities.dp(12.0f);
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }

    /* access modifiers changed from: package-private */
    public boolean onDismiss() {
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        return super.onDismiss();
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: package-private */
    public void onPreMeasure(int i, int i2) {
        int i3;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            i3 = AndroidUtilities.dp(8.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet()) {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    i3 = (int) (((float) i2) / 3.5f);
                    this.parentAlert.setAllowNestedScroll(true);
                }
            }
            i3 = (i2 / 5) * 2;
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != i3) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, i3, 0, AndroidUtilities.dp(48.0f));
            this.ignoreLayout = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void onShow() {
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        this.listAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public void onHidden() {
        this.selectedAudios.clear();
        this.selectedAudiosOrder.clear();
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    private void runShadowAnimation(final boolean z) {
        if ((z && this.shadow.getTag() != null) || (!z && this.shadow.getTag() == null)) {
            this.shadow.setTag(z ? null : 1);
            if (z) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlertAudioLayout.this.shadowAnimation != null && ChatAttachAlertAudioLayout.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            ChatAttachAlertAudioLayout.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = ChatAttachAlertAudioLayout.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatAttachAlertAudioLayout.this.shadowAnimation != null && ChatAttachAlertAudioLayout.this.shadowAnimation.equals(animator)) {
                        AnimatorSet unused = ChatAttachAlertAudioLayout.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = NotificationCenter.messagePlayingDidReset;
        if (i != i3 && i != NotificationCenter.messagePlayingDidStart && i != NotificationCenter.messagePlayingPlayStateChanged) {
            return;
        }
        if (i == i3 || i == NotificationCenter.messagePlayingPlayStateChanged) {
            int childCount = this.listView.getChildCount();
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = this.listView.getChildAt(i4);
                if (childAt instanceof SharedAudioCell) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) childAt;
                    if (sharedAudioCell.getMessage() != null) {
                        sharedAudioCell.updateButtonState(false, true);
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingDidStart && objArr[0].eventId == 0) {
            int childCount2 = this.listView.getChildCount();
            for (int i5 = 0; i5 < childCount2; i5++) {
                View childAt2 = this.listView.getChildAt(i5);
                if (childAt2 instanceof SharedAudioCell) {
                    SharedAudioCell sharedAudioCell2 = (SharedAudioCell) childAt2;
                    if (sharedAudioCell2.getMessage() != null) {
                        sharedAudioCell2.updateButtonState(false, true);
                    }
                }
            }
        }
    }

    private void showErrorBox(String str) {
        new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString("AppName", NUM)).setMessage(str).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
    }

    private void onItemClick(View view) {
        int i;
        if (view instanceof SharedAudioCell) {
            SharedAudioCell sharedAudioCell = (SharedAudioCell) view;
            MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) sharedAudioCell.getTag();
            boolean z = false;
            int i2 = 1;
            if (this.selectedAudios.indexOfKey(audioEntry.id) >= 0) {
                this.selectedAudios.remove(audioEntry.id);
                this.selectedAudiosOrder.remove(audioEntry);
                sharedAudioCell.setChecked(false, true);
            } else if (this.maxSelectedFiles < 0 || this.selectedAudios.size() < (i = this.maxSelectedFiles)) {
                this.selectedAudios.put(audioEntry.id, audioEntry);
                this.selectedAudiosOrder.add(audioEntry);
                sharedAudioCell.setChecked(true, true);
                z = true;
            } else {
                showErrorBox(LocaleController.formatString("PassportUploadMaxReached", NUM, LocaleController.formatPluralString("Files", i)));
                return;
            }
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            if (!z) {
                i2 = 2;
            }
            chatAttachAlert.updateCountButton(i2);
        }
    }

    /* access modifiers changed from: package-private */
    public int getSelectedItemsCount() {
        return this.selectedAudios.size();
    }

    /* access modifiers changed from: package-private */
    public void sendSelectedItems(boolean z, int i) {
        if (this.selectedAudios.size() != 0 && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.selectedAudiosOrder.size(); i2++) {
                arrayList.add(this.selectedAudiosOrder.get(i2).messageObject);
            }
            this.delegate.didSelectAudio(arrayList, this.parentAlert.commentTextView.getText().toString(), z, i);
        }
    }

    public void setDelegate(AudioSelectDelegate audioSelectDelegate) {
        this.delegate = audioSelectDelegate;
    }

    private void loadAudio() {
        this.loadingAudio = true;
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                ChatAttachAlertAudioLayout.this.lambda$loadAudio$4$ChatAttachAlertAudioLayout();
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:16|(2:23|24)|25|26) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0165 */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0162 A[SYNTHETIC, Splitter:B:23:0x0162] */
    /* renamed from: lambda$loadAudio$4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadAudio$4$ChatAttachAlertAudioLayout() {
        /*
            r18 = this;
            r1 = r18
            r0 = 6
            java.lang.String[] r4 = new java.lang.String[r0]
            java.lang.String r0 = "_id"
            r8 = 0
            r4[r8] = r0
            java.lang.String r0 = "artist"
            r9 = 1
            r4[r9] = r0
            java.lang.String r0 = "title"
            r10 = 2
            r4[r10] = r0
            java.lang.String r0 = "_data"
            r11 = 3
            r4[r11] = r0
            java.lang.String r0 = "duration"
            r12 = 4
            r4[r12] = r0
            java.lang.String r0 = "album"
            r13 = 5
            r4[r13] = r0
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0168 }
            android.content.ContentResolver r2 = r0.getContentResolver()     // Catch:{ Exception -> 0x0168 }
            android.net.Uri r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x0168 }
            java.lang.String r5 = "is_music != 0"
            r6 = 0
            java.lang.String r7 = "title"
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0168 }
            r0 = -2000000000(0xfffffffvar_ca6CLASSNAME, float:-1.2182823E-33)
        L_0x003c:
            boolean r3 = r2.moveToNext()     // Catch:{ all -> 0x015e }
            if (r3 == 0) goto L_0x0159
            org.telegram.messenger.MediaController$AudioEntry r3 = new org.telegram.messenger.MediaController$AudioEntry     // Catch:{ all -> 0x015e }
            r3.<init>()     // Catch:{ all -> 0x015e }
            int r4 = r2.getInt(r8)     // Catch:{ all -> 0x015e }
            long r4 = (long) r4     // Catch:{ all -> 0x015e }
            r3.id = r4     // Catch:{ all -> 0x015e }
            java.lang.String r4 = r2.getString(r9)     // Catch:{ all -> 0x015e }
            r3.author = r4     // Catch:{ all -> 0x015e }
            java.lang.String r4 = r2.getString(r10)     // Catch:{ all -> 0x015e }
            r3.title = r4     // Catch:{ all -> 0x015e }
            java.lang.String r4 = r2.getString(r11)     // Catch:{ all -> 0x015e }
            r3.path = r4     // Catch:{ all -> 0x015e }
            long r4 = r2.getLong(r12)     // Catch:{ all -> 0x015e }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r6
            int r5 = (int) r4     // Catch:{ all -> 0x015e }
            r3.duration = r5     // Catch:{ all -> 0x015e }
            java.lang.String r4 = r2.getString(r13)     // Catch:{ all -> 0x015e }
            r3.genre = r4     // Catch:{ all -> 0x015e }
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x015e }
            java.lang.String r5 = r3.path     // Catch:{ all -> 0x015e }
            r4.<init>(r5)     // Catch:{ all -> 0x015e }
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x015e }
            r5.<init>()     // Catch:{ all -> 0x015e }
            r5.out = r9     // Catch:{ all -> 0x015e }
            r5.id = r0     // Catch:{ all -> 0x015e }
            org.telegram.tgnet.TLRPC$TL_peerUser r15 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x015e }
            r15.<init>()     // Catch:{ all -> 0x015e }
            r5.peer_id = r15     // Catch:{ all -> 0x015e }
            org.telegram.tgnet.TLRPC$TL_peerUser r15 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x015e }
            r15.<init>()     // Catch:{ all -> 0x015e }
            r5.from_id = r15     // Catch:{ all -> 0x015e }
            org.telegram.tgnet.TLRPC$Peer r10 = r5.peer_id     // Catch:{ all -> 0x015e }
            org.telegram.ui.Components.ChatAttachAlert r12 = r1.parentAlert     // Catch:{ all -> 0x015e }
            int r12 = r12.currentAccount     // Catch:{ all -> 0x015e }
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)     // Catch:{ all -> 0x015e }
            int r12 = r12.getClientUserId()     // Catch:{ all -> 0x015e }
            r15.user_id = r12     // Catch:{ all -> 0x015e }
            r10.user_id = r12     // Catch:{ all -> 0x015e }
            long r16 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x015e }
            long r6 = r16 / r6
            int r7 = (int) r6     // Catch:{ all -> 0x015e }
            r5.date = r7     // Catch:{ all -> 0x015e }
            java.lang.String r6 = ""
            r5.message = r6     // Catch:{ all -> 0x015e }
            java.lang.String r6 = r3.path     // Catch:{ all -> 0x015e }
            r5.attachPath = r6     // Catch:{ all -> 0x015e }
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ all -> 0x015e }
            r6.<init>()     // Catch:{ all -> 0x015e }
            r5.media = r6     // Catch:{ all -> 0x015e }
            int r7 = r6.flags     // Catch:{ all -> 0x015e }
            r7 = r7 | r11
            r6.flags = r7     // Catch:{ all -> 0x015e }
            org.telegram.tgnet.TLRPC$TL_document r7 = new org.telegram.tgnet.TLRPC$TL_document     // Catch:{ all -> 0x015e }
            r7.<init>()     // Catch:{ all -> 0x015e }
            r6.document = r7     // Catch:{ all -> 0x015e }
            int r6 = r5.flags     // Catch:{ all -> 0x015e }
            r6 = r6 | 768(0x300, float:1.076E-42)
            r5.flags = r6     // Catch:{ all -> 0x015e }
            java.lang.String r6 = org.telegram.messenger.FileLoader.getFileExtension(r4)     // Catch:{ all -> 0x015e }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r5.media     // Catch:{ all -> 0x015e }
            org.telegram.tgnet.TLRPC$Document r7 = r7.document     // Catch:{ all -> 0x015e }
            r12 = r14
            r13 = 0
            r7.id = r13     // Catch:{ all -> 0x0157 }
            r7.access_hash = r13     // Catch:{ all -> 0x0157 }
            byte[] r13 = new byte[r8]     // Catch:{ all -> 0x0157 }
            r7.file_reference = r13     // Catch:{ all -> 0x0157 }
            int r13 = r5.date     // Catch:{ all -> 0x0157 }
            r7.date = r13     // Catch:{ all -> 0x0157 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x0157 }
            r13.<init>()     // Catch:{ all -> 0x0157 }
            java.lang.String r14 = "audio/"
            r13.append(r14)     // Catch:{ all -> 0x0157 }
            int r14 = r6.length()     // Catch:{ all -> 0x0157 }
            if (r14 <= 0) goto L_0x00f2
            goto L_0x00f4
        L_0x00f2:
            java.lang.String r6 = "mp3"
        L_0x00f4:
            r13.append(r6)     // Catch:{ all -> 0x0157 }
            java.lang.String r6 = r13.toString()     // Catch:{ all -> 0x0157 }
            r7.mime_type = r6     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$Document r6 = r6.document     // Catch:{ all -> 0x0157 }
            long r13 = r4.length()     // Catch:{ all -> 0x0157 }
            int r7 = (int) r13     // Catch:{ all -> 0x0157 }
            r6.size = r7     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$Document r6 = r6.document     // Catch:{ all -> 0x0157 }
            r6.dc_id = r8     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ all -> 0x0157 }
            r6.<init>()     // Catch:{ all -> 0x0157 }
            int r7 = r3.duration     // Catch:{ all -> 0x0157 }
            r6.duration = r7     // Catch:{ all -> 0x0157 }
            java.lang.String r7 = r3.title     // Catch:{ all -> 0x0157 }
            r6.title = r7     // Catch:{ all -> 0x0157 }
            java.lang.String r7 = r3.author     // Catch:{ all -> 0x0157 }
            r6.performer = r7     // Catch:{ all -> 0x0157 }
            int r7 = r6.flags     // Catch:{ all -> 0x0157 }
            r7 = r7 | r11
            r6.flags = r7     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r5.media     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$Document r7 = r7.document     // Catch:{ all -> 0x0157 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r7.attributes     // Catch:{ all -> 0x0157 }
            r7.add(r6)     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename     // Catch:{ all -> 0x0157 }
            r6.<init>()     // Catch:{ all -> 0x0157 }
            java.lang.String r4 = r4.getName()     // Catch:{ all -> 0x0157 }
            r6.file_name = r4     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ all -> 0x0157 }
            org.telegram.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ all -> 0x0157 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r4.attributes     // Catch:{ all -> 0x0157 }
            r4.add(r6)     // Catch:{ all -> 0x0157 }
            org.telegram.messenger.MessageObject r4 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x0157 }
            org.telegram.ui.Components.ChatAttachAlert r6 = r1.parentAlert     // Catch:{ all -> 0x0157 }
            int r6 = r6.currentAccount     // Catch:{ all -> 0x0157 }
            r4.<init>(r6, r5, r8, r9)     // Catch:{ all -> 0x0157 }
            r3.messageObject = r4     // Catch:{ all -> 0x0157 }
            r12.add(r3)     // Catch:{ all -> 0x0157 }
            int r0 = r0 + -1
            r14 = r12
            r10 = 2
            r12 = 4
            r13 = 5
            goto L_0x003c
        L_0x0157:
            r0 = move-exception
            goto L_0x0160
        L_0x0159:
            r12 = r14
            r2.close()     // Catch:{ Exception -> 0x0166 }
            goto L_0x016d
        L_0x015e:
            r0 = move-exception
            r12 = r14
        L_0x0160:
            if (r2 == 0) goto L_0x0165
            r2.close()     // Catch:{ all -> 0x0165 }
        L_0x0165:
            throw r0     // Catch:{ Exception -> 0x0166 }
        L_0x0166:
            r0 = move-exception
            goto L_0x016a
        L_0x0168:
            r0 = move-exception
            r12 = r14
        L_0x016a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x016d:
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlertAudioLayout$2tkwzVv4Lf8Cn1d7_KL7Mcx8xWM r0 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlertAudioLayout$2tkwzVv4Lf8Cn1d7_KL7Mcx8xWM
            r0.<init>(r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertAudioLayout.lambda$loadAudio$4$ChatAttachAlertAudioLayout():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAudio$3 */
    public /* synthetic */ void lambda$loadAudio$3$ChatAttachAlertAudioLayout(ArrayList arrayList) {
        this.loadingAudio = false;
        this.audioEntries = arrayList;
        this.listAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            if (ChatAttachAlertAudioLayout.this.audioEntries.isEmpty()) {
                return 1;
            }
            return ChatAttachAlertAudioLayout.this.audioEntries.size() + (ChatAttachAlertAudioLayout.this.audioEntries.isEmpty() ? 0 : 2);
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                AnonymousClass1 r4 = new SharedAudioCell(this.mContext) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        MessageObject unused = ChatAttachAlertAudioLayout.this.playingAudio = messageObject;
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(messageObject);
                        return MediaController.getInstance().setPlaylist(arrayList, messageObject, 0);
                    }
                };
                r4.setCheckForButtonPress(true);
                view = r4;
            } else if (i != 1) {
                view = new View(this.mContext);
            } else {
                view = new View(this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                int i2 = i - 1;
                MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) ChatAttachAlertAudioLayout.this.audioEntries.get(i2);
                SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                sharedAudioCell.setTag(audioEntry);
                boolean z = true;
                sharedAudioCell.setMessageObject(audioEntry.messageObject, i2 != ChatAttachAlertAudioLayout.this.audioEntries.size() - 1);
                if (ChatAttachAlertAudioLayout.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                    z = false;
                }
                sharedAudioCell.setChecked(z, false);
            }
        }

        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 2;
            }
            return i == 0 ? 1 : 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertAudioLayout.this.updateEmptyView();
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        private Context mContext;
        private int reqId = 0;
        /* access modifiers changed from: private */
        public ArrayList<MediaController.AudioEntry> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(String str) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (ChatAttachAlertAudioLayout.this.listView.getAdapter() != ChatAttachAlertAudioLayout.this.listAdapter) {
                    ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.listAdapter);
                }
                notifyDataSetChanged();
                return;
            }
            int i = this.lastSearchId + 1;
            this.lastSearchId = i;
            $$Lambda$ChatAttachAlertAudioLayout$SearchAdapter$JRqw_pNZgQzvfhH4m58tvIOYDc r1 = new Runnable(str, i) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatAttachAlertAudioLayout.SearchAdapter.this.lambda$search$1$ChatAttachAlertAudioLayout$SearchAdapter(this.f$1, this.f$2);
                }
            };
            this.searchRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$search$1 */
        public /* synthetic */ void lambda$search$1$ChatAttachAlertAudioLayout$SearchAdapter(String str, int i) {
            Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(ChatAttachAlertAudioLayout.this.audioEntries), i) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ ArrayList f$2;
                public final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ChatAttachAlertAudioLayout.SearchAdapter.this.lambda$search$0$ChatAttachAlertAudioLayout$SearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$search$0 */
        public /* synthetic */ void lambda$search$0$ChatAttachAlertAudioLayout$SearchAdapter(String str, ArrayList arrayList, int i) {
            String str2;
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList(), str, this.lastSearchId);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            int i2 = (translitString != null ? 1 : 0) + 1;
            String[] strArr = new String[i2];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) arrayList.get(i3);
                int i4 = 0;
                while (true) {
                    if (i4 >= i2) {
                        break;
                    }
                    String str3 = strArr[i4];
                    String str4 = audioEntry.author;
                    boolean contains = str4 != null ? str4.toLowerCase().contains(str3) : false;
                    if (!contains && (str2 = audioEntry.title) != null) {
                        contains = str2.toLowerCase().contains(str3);
                    }
                    if (contains) {
                        arrayList2.add(audioEntry);
                        break;
                    }
                    i4++;
                }
            }
            updateSearchResults(arrayList2, str, i);
        }

        private void updateSearchResults(ArrayList<MediaController.AudioEntry> arrayList, String str, int i) {
            AndroidUtilities.runOnUIThread(new Runnable(i, str, arrayList) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ChatAttachAlertAudioLayout.SearchAdapter.this.lambda$updateSearchResults$2$ChatAttachAlertAudioLayout$SearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$2 */
        public /* synthetic */ void lambda$updateSearchResults$2$ChatAttachAlertAudioLayout$SearchAdapter(int i, String str, ArrayList arrayList) {
            if (i == this.lastSearchId) {
                if (!(i == -1 || ChatAttachAlertAudioLayout.this.listView.getAdapter() == ChatAttachAlertAudioLayout.this.searchAdapter)) {
                    ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.searchAdapter);
                }
                if (ChatAttachAlertAudioLayout.this.listView.getAdapter() == ChatAttachAlertAudioLayout.this.searchAdapter) {
                    ChatAttachAlertAudioLayout.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoAudioFoundInfo", NUM, str)));
                }
                this.searchResult = arrayList;
                notifyDataSetChanged();
            }
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertAudioLayout.this.updateEmptyView();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (this.searchResult.isEmpty()) {
                return 1;
            }
            return this.searchResult.size() + (this.searchResult.isEmpty() ? 0 : 2);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                AnonymousClass1 r4 = new SharedAudioCell(this.mContext) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        MessageObject unused = ChatAttachAlertAudioLayout.this.playingAudio = messageObject;
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(messageObject);
                        return MediaController.getInstance().setPlaylist(arrayList, messageObject, 0);
                    }
                };
                r4.setCheckForButtonPress(true);
                view = r4;
            } else if (i != 1) {
                view = new View(this.mContext);
            } else {
                view = new View(this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                int i2 = i - 1;
                MediaController.AudioEntry audioEntry = this.searchResult.get(i2);
                SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                sharedAudioCell.setTag(audioEntry);
                boolean z = true;
                sharedAudioCell.setMessageObject(audioEntry.messageObject, i2 != this.searchResult.size() - 1);
                if (ChatAttachAlertAudioLayout.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                    z = false;
                }
                sharedAudioCell.setChecked(z, false);
            }
        }

        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 2;
            }
            return i == 0 ? 1 : 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void onContainerTranslationUpdated(float f) {
        this.currentPanTranslationProgress = f;
        super.onContainerTranslationUpdated(f);
        updateEmptyViewPosition();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.frameLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.searchField.getSearchBackground(), ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchBackground"));
        arrayList.add(new ThemeDescription((View) this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"searchIconImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        arrayList.add(new ThemeDescription((View) this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"clearSearchImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchText"));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchHint"));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        return arrayList;
    }
}
